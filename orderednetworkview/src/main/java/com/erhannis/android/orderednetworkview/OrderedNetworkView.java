package com.erhannis.android.orderednetworkview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * //TODO There might be a better name
 */
public class OrderedNetworkView<T extends DrawableNode<T>> extends View {
  private static final double NODE_SIZE = 100; //TODO Is this where this should be noted?  Should it be hardcoded at all?
  private static final double ROW_SIZE = 150;
  private static final double COL_SIZE = 100;

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
  protected void onDraw(Canvas canvas) {
    if (root == null) {
      return;
    }
    if (dirty) {
      calculatePositions();
    }

    canvas.save();
    Paint linkPaint = new Paint();

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

      for (MirrorNode<T> child : node.children) {
        if (!completed.contains(child)) {
          pending.push(child);
          completed.add(child);
        }
        { // Draw link
          Paint p = new Paint(linkPaint);
          double[] a = nodePositions.get(node);
          double[] b = nodePositions.get(node);
          canvas.drawLine((float)a[0], (float)a[1], (float)b[0], (float)b[1], p);
        }
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

      double newX = nodePos[0];
      newX -= COL_SIZE * ((node.mirror.children.size() - 1) / 2.0);
      for (MirrorNode<T> child : node.children) {
        double newY = nodePos[1] + ROW_SIZE;
        if (!nodePositions.containsKey(child) || nodePositions.get(child)[1] < newY) {
          // Either child hasn't been positioned, or this branch of the net is pushing it up
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
