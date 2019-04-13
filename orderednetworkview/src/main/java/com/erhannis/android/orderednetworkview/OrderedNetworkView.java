package com.erhannis.android.orderednetworkview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.erhannis.mathnstuff.MeMath;
import com.erhannis.mathnstuff.MeUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * //TODO There might be a better name
 */
public class OrderedNetworkView<T extends DrawableNode<T>> extends View {
  private static final double NODE_SIZE = 100; //TODO Is this where this should be noted?  Should it be hardcoded at all?
  private static final double ROW_SIZE = 150;
  private static final double COL_SIZE = 120;

  private static final double BG_TONE = 0.5;
  private static final int BG_COLOR = MeUtils.ARGBToInt(1, BG_TONE, BG_TONE, BG_TONE);

  public int dx = 0;
  public int dy = 0;
  public float sx = 1;
  public float sy = 1;

  private MirrorNode<T> root; //TODO Final?
  private final HashMap<Marker, T> markerPositions = new HashMap<>();
  private HashMap<T, MirrorNode<T>> nodeToMirror;

  private transient boolean dirty = true;
  private transient RectF netSize = new RectF(0,0,0,0);
  private transient HashMap<MirrorNode<T>, double[]> nodePositions = new HashMap<>();

  public OrderedNetworkView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
  }

  protected HashMap<T, MirrorNode<T>> mirrorRoot(MirrorNode<T> root) {
    LinkedList<MirrorNode<T>> pending = new LinkedList<>();
    HashMap<T, MirrorNode<T>> added = new HashMap<>();

    pending.push(root);
    added.put(root.mirror, root);
    while (!pending.isEmpty()) {
      MirrorNode<T> item = pending.remove();
      for (T child : item.mirror.children) {
        if (!added.containsKey(child)) {
          MirrorNode<T> mirror = new MirrorNode<>(child);
          added.put(child, mirror);
          pending.push(mirror);
        }
        item.children.add(added.get(child));
      }
    }

    return added;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    System.out.println("layout " + changed + " [" + left + ", " + top + ", " + right + ", " + bottom + "]");
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (root == null) {
      return;
    }
    if (dirty) {
      calculatePositions();
    }

    canvas.save();

    { // Init canvas
      Paint bucket = new Paint();
      bucket.setColor(BG_COLOR);
      canvas.drawPaint(bucket);

      // Center canvas
      //TODO Will have manual zoom instead
      Rect clip = canvas.getClipBounds();
      RectF vpSize = new RectF(netSize);
      vpSize.top -= NODE_SIZE;
      vpSize.right += NODE_SIZE;
      vpSize.bottom += NODE_SIZE;
      vpSize.left -= NODE_SIZE;

      if (vpSize.width() > vpSize.height()) {
        // Fit width
        float xs = clip.width() / vpSize.width();
        canvas.scale(xs, xs);
      } else {
        // Fit height
        float ys = clip.height() / vpSize.height();
        canvas.scale(ys, ys);
      }
      clip = canvas.getClipBounds();
      canvas.translate(clip.width() / 2f, clip.top - vpSize.top);

      canvas.scale(sx, sy);
      canvas.translate(dx, dy);
    }


    Paint linkPaint = new Paint();
    linkPaint.setColor(0xFFFFFFFF);
    linkPaint.setStrokeWidth(0);

    LinkedList<MirrorNode<T>> pending = new LinkedList<>();
    HashSet<MirrorNode<T>> completed = new HashSet<>();

    pending.push(root);
    completed.add(root);
    while (!pending.isEmpty()) {
      MirrorNode<T> node = pending.remove();

      { // Draw node
        //TODO Not sure how efficient this is.
        canvas.save();
        double[] pos = nodePositions.get(node);
        canvas.translate((float)pos[0], (float)pos[1]);
        node.mirror.getDrawable().draw(canvas);
        canvas.restore();
      }


      double i = 0;
      double c = node.children.size();
      for (MirrorNode<T> child : node.children) {
        if (!completed.contains(child)) {
          pending.push(child);
          completed.add(child);
        }
        { // Draw link
          Paint p = new Paint(linkPaint);
          double priority = MeMath.interpolate(1, 0, i / c);
          p.setColor(MeUtils.ARGBToInt(1, 1, priority, priority));
          double[] a = nodePositions.get(node);
          double[] b = nodePositions.get(child);
          //TODO Clip out center of nodes
          canvas.drawLine((float)a[0], (float)a[1], (float)b[0], (float)b[1], p);
        }
        i++;
      }
    }

    double markerOffsetX = Math.sqrt(MeMath.sqr(NODE_SIZE) / 2);
    double markerOffsetY = -Math.sqrt(MeMath.sqr(NODE_SIZE) / 2);
    for (Map.Entry<Marker, T> e : markerPositions.entrySet()) {
      Marker m = e.getKey();
      T t = e.getValue();
      if (t != null) {
        MirrorNode<T> node = nodeToMirror.get(t);

        //TODO Not sure how efficient this is.
        canvas.save();
        double[] pos = nodePositions.get(node);
        canvas.translate((float)(pos[0]+markerOffsetX), (float)(pos[1]+markerOffsetY));
        node.mirror.getDrawable().draw(canvas);
        canvas.restore();
      }
    }

    canvas.restore();
  }

  //TODO Fix.  There are many cases leading to overlapping nodes.
  protected void calculatePositions() {
    HashMap<MirrorNode<T>, double[]> nodePositions = new HashMap<>();
    RectF netSize = new RectF(0, 0, 0, 0);

    LinkedList<MirrorNode<T>> pending = new LinkedList<>();
    pending.push(root);
    nodePositions.put(root, new double[]{0,0});
    while (!pending.isEmpty()) {
      MirrorNode<T> node = pending.remove();
      double[] nodePos = nodePositions.get(node);
      netSize.union((float)nodePos[0], (float)nodePos[1]);

      double newX = nodePos[0];
      newX -= COL_SIZE * ((node.mirror.children.size() - 1) / 2.0);
      for (MirrorNode<T> child : node.children) {
        double newY = nodePos[1] - ROW_SIZE;
        if (nodePositions.containsKey(child)) {
          if (nodePositions.get(child)[1] > newY) {
            // Child hasn't been positioned, or this branch of the net is pushing it up
            pending.remove(child); //TODO Not sure if necessary, not sure how much cost
            pending.push(child);
            // Leaving it in the same col
            nodePositions.put(child, new double[]{nodePositions.get(child)[0],newY});
          }
        } else {
          // Child hasn't been positioned, or this branch of the net is pushing it up
          pending.remove(child); //TODO Not sure if necessary, not sure how much cost
          pending.push(child);
          nodePositions.put(child, new double[]{newX,newY});
        }

        newX += COL_SIZE;
      }
    }

    this.nodePositions = nodePositions;
    this.netSize = netSize;
  }


  /**
   * `parent` must already be in net.  If `child` exists, the two become linked, unless
   * already linked.  In either case, the new link becomes the most preferred.
   * If `child` does not exist, it will be added to the net.
   * However, its child nodes will not.  Please recursively add them, yourself.
   *
   * @param parent
   * @param child
   *
   * @throws IllegalArgumentException if parent is not in net
   */
  public void doAddLink(T parent, T child) {
    if (!nodeToMirror.containsKey(parent)) {
      throw new IllegalArgumentException("parent node was not found in net");
    }
    dirty = true;
    if (nodeToMirror.containsKey(child)) {
      MirrorNode<T> pm = nodeToMirror.get(parent);
      MirrorNode<T> cm = nodeToMirror.get(child);
      pm.children.remove(cm);
      pm.children.addFirst(cm);
    } else {
      MirrorNode<T> pm = nodeToMirror.get(parent);
      MirrorNode<T> cm = new MirrorNode<>(child);
      pm.children.addFirst(cm);
    }
  }

  public void setMarkerPosition(Marker marker, T node) {
    dirty = true;
    markerPositions.put(marker, node);
  }

  //TODO Include marker positions?
  public void reset(T root, Marker[] markers) {
    dirty = true;
    this.root = new MirrorNode<>(root);
    this.nodeToMirror = mirrorRoot(this.root);

    for (Marker m : markers) {
      markerPositions.put(m, null);
    }
  }
}
