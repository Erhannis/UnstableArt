package com.erhannis.android.orderednetworkview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.erhannis.mathnstuff.MeMath;
import com.erhannis.mathnstuff.MeUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * //TODO There might be a better name
 */
public class OrderedNetworkView<T extends DrawableNode<T>> extends View {
  public static interface OnDropMarkerListener<T> {
    public void onDropMarker(Marker m, T node);
  }

  //TODO Ehhh.  This seems...weird and confused.
  private class MetaUI {
    private static final float UI_ITEM_SIZE = (float)(NODE_SIZE * 1.5);

    private transient Marker selectedMarker = null;
    private transient boolean reset = true;
    private transient final LinkedHashMap<Marker, float[]> markerPositions = new LinkedHashMap<>();

    public void setMarkers(Collection<Marker> markers) {
      reset = true;
      markerPositions.clear();
      for (Marker m : markers) {
        markerPositions.put(m, new float[]{0,0});
      }
      selectedMarker = null;
    }

    public void considerMarker(Marker marker) {
      if (!markerPositions.containsKey(marker)) {
        reset = true;
        markerPositions.put(marker, new float[]{0,0});
        //TODO If interacting, cancel?
      }
    }

    private RectF getUiBox() {
      return getUiBox(new Rect(0, 0, OrderedNetworkView.this.getWidth(), OrderedNetworkView.this.getHeight()));
    }

    private RectF getUiBox(Rect viewport) {
      return new RectF(viewport.right - UI_ITEM_SIZE, viewport.top, viewport.right, viewport.top + (UI_ITEM_SIZE * markerPositions.size())); //TODO Setting
    }

    private void resetMarkerPositions(Rect viewport) {
      RectF uiBox = getUiBox(viewport);
      float x = uiBox.centerX();
      float y = uiBox.top + (UI_ITEM_SIZE / 2);
      for (Map.Entry<Marker, float[]> e : markerPositions.entrySet()) {
        e.getValue()[0] = x;
        e.getValue()[1] = y;
        y += UI_ITEM_SIZE;
      }
    }

    public boolean doesStartMetaInteraction(MotionEvent event) {
      //TODO Pointer index?
      RectF uiBox = getUiBox();
      boolean result = ((event.getActionMasked() == MotionEvent.ACTION_DOWN) && (uiBox.contains(event.getX(), event.getY())));
      return result;
    }

    /**
     *
     * @param event
     * @return if the meta-interaction is still underway
     */
    public boolean doMetaInteraction(MotionEvent event) {
      //TODO Double tap to zoom to marker?
      OrderedNetworkView.this.invalidate();
      float[] xy = new float[]{event.getX(), event.getY()};
      switch (event.getActionMasked()) {
        case MotionEvent.ACTION_UP:
          reset = true;
          if (selectedMarker != null) {
            OrderedNetworkView.this.metaDropMarkerAtSpot(selectedMarker, xy);
          }
          return false;
        case MotionEvent.ACTION_DOWN:
          selectedMarker = getNearestMarker(xy);
          return true;
        case MotionEvent.ACTION_MOVE: //TODO What about basically anything else?
          if (selectedMarker != null) {
            float[] mxy = markerPositions.get(selectedMarker);
            mxy[0] = xy[0];
            mxy[1] = xy[1];
          }
          return true;
        default:
          Log.d(TAG, "Unhandled action: " + event.getActionMasked());
        case MotionEvent.ACTION_CANCEL:
          reset = true;
          selectedMarker = null;
          return false;
      }
    }

    private Marker getNearestMarker(float[] xy) {
      Marker best = null;
      float dist2 = Float.POSITIVE_INFINITY;
      for (Map.Entry<Marker, float[]> e : markerPositions.entrySet()) {
        float dx = (e.getValue()[0] - xy[0]);
        float dy = (e.getValue()[1] - xy[1]);
        float newDist2 = (dx*dx) + (dy*dy);
        if (newDist2 < dist2) {
          best = e.getKey();
          dist2 = newDist2;
        }
      }
      return best;
    }

    public void draw(Canvas canvas) {
      if (reset) {
        resetMarkerPositions(canvas.getClipBounds());
        reset = false;
      }
      Paint uiBg = new Paint();
      uiBg.setColor(0xFF000000); //TODO Setting
      RectF uiBox = getUiBox(canvas.getClipBounds());
      canvas.drawRect(uiBox, uiBg);
      for (Map.Entry<Marker, float[]> e : markerPositions.entrySet()) {
        canvas.save();
        canvas.translate(e.getValue()[0], e.getValue()[1]);
        canvas.scale(2f, 2f);
        e.getKey().icon.draw(canvas);
        canvas.restore();
      }
    }
  }

  private static final String TAG = "OrderedNetworkView";

  private static final double NODE_SIZE = 100; //TODO Is this where this should be noted?  Should it be hardcoded at all?
  private static final double ROW_SIZE = 150;
  private static final double COL_SIZE = 120;

  private static final double BG_TONE = 0.5;
  private static final int BG_COLOR = MeUtils.ARGBToInt(1, BG_TONE, BG_TONE, BG_TONE);

  private boolean viewportInitialized = false;
  private Matrix mViewportMatrix = new Matrix();
  private Matrix mViewportMatrixInverse = new Matrix();

  private MirrorNode<T> root; //TODO Final?
  private final LinkedHashMap<Marker, T> markerPositions = new LinkedHashMap<>();
  private HashMap<T, MirrorNode<T>> nodeToMirror;

  private transient boolean dirty = true;
  private transient RectF netSize = new RectF(0,0,0,0);
  private transient HashMap<MirrorNode<T>, double[]> nodePositions = new HashMap<>();

  private MetaUI metaUI = new MetaUI();

  private final Handler mainThread;

  private OnDropMarkerListener<T> onDropMarkerListener;

  public OrderedNetworkView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    setInteractions();
    mainThread = new Handler();
  }

  public void setOnDropMarkerListener(OnDropMarkerListener<T> listener) {
    onDropMarkerListener = listener;
  }

  public LinkedHashMap<Marker, T> getMarkerPositions() {
    return new LinkedHashMap<>(markerPositions);
  }

  protected static <T extends Node<T>> HashMap<T, MirrorNode<T>> mirrorRoot(MirrorNode<T> root) {
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
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (root == null) {
      return;
    }
    if (dirty) {
      calculatePositions();
      dirty = false;
    }

    canvas.save();

    { // Init canvas
      Paint bucket = new Paint();
      bucket.setColor(BG_COLOR);
      canvas.drawPaint(bucket);

      // Center canvas
      if (!viewportInitialized) {
        Rect clip = canvas.getClipBounds();
        RectF vpSize = new RectF(netSize);
        vpSize.top -= NODE_SIZE;
        vpSize.right += NODE_SIZE;
        vpSize.bottom += NODE_SIZE;
        vpSize.left -= NODE_SIZE;

        float scale;
        if (vpSize.width() > vpSize.height()) {
          // Fit width
          scale = clip.width() / vpSize.width();
        } else {
          // Fit height
          scale = clip.height() / vpSize.height();
        }
        mViewportMatrix.postScale(scale, scale);
        mViewportMatrix.postTranslate((clip.width() / 2f), scale * (clip.top - vpSize.top)); //TODO I think this line is wrong in a way that doesn't show itself in the current way it's being used
        viewportInitialized = true;
        if (!mViewportMatrix.invert(mViewportMatrixInverse)) {
          throw new IllegalStateException("Viewport matrix non-invertible!");
        }
      }

      canvas.concat(mViewportMatrix);
    }


    Paint linkPaint = new Paint();
    linkPaint.setColor(0xFFFFFFFF); //TODO Setting
    linkPaint.setStrokeWidth(0); //TODO Setting

    LinkedList<MirrorNode<T>> pending = new LinkedList<>();
    HashSet<MirrorNode<T>> completed = new HashSet<>();

    pending.push(root);
    completed.add(root);
    while (!pending.isEmpty()) {
      MirrorNode<T> node = pending.remove();

      { // Draw links
        double i = 0;
        double c = node.children.size();
        for (MirrorNode<T> child : node.children) {
          if (!completed.contains(child)) {
            pending.push(child);
            completed.add(child);
          }
          { // Draw link
            Paint p = new Paint(linkPaint);
            double priority = MeMath.interpolate(1, 0, i / c); //TODO Setting
            p.setColor(MeUtils.ARGBToInt(1, 1, priority, priority)); //TODO Setting
            double[] a = nodePositions.get(node);
            double[] b = nodePositions.get(child);
            //TODO Clip out center of nodes
            canvas.drawLine((float) a[0], (float) a[1], (float) b[0], (float) b[1], p);
          }
          i++;
        }
      }

      { // Draw node
        //TODO Not sure how efficient this is.
        canvas.save();
        double[] pos = nodePositions.get(node);
        canvas.translate((float)pos[0], (float)pos[1]);
        node.mirror.getDrawable().draw(canvas);
        canvas.restore();
      }
    }

    { // Draw markers
      double markerOffsetX = Math.sqrt(MeMath.sqr(NODE_SIZE) / 8);
      double markerOffsetY = -Math.sqrt(MeMath.sqr(NODE_SIZE) / 8);
      for (Map.Entry<Marker, T> e : markerPositions.entrySet()) {
        Marker m = e.getKey();
        T t = e.getValue();
        if (t != null) {
          MirrorNode<T> node = nodeToMirror.get(t);

          //TODO Not sure how efficient this is.
          canvas.save();
          double[] pos = nodePositions.get(node);
          canvas.translate((float) (pos[0] + markerOffsetX), (float) (pos[1] + markerOffsetY));
          m.icon.draw(canvas);
          canvas.restore();
        }
      }
    }

    canvas.restore();

    canvas.save();
    metaUI.draw(canvas);
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
    invalidate();
    if (nodeToMirror.containsKey(child)) {
      MirrorNode<T> pm = nodeToMirror.get(parent);
      MirrorNode<T> cm = nodeToMirror.get(child);
      pm.children.remove(cm);
      pm.children.addFirst(cm);
    } else {
      MirrorNode<T> pm = nodeToMirror.get(parent);
      MirrorNode<T> cm = new MirrorNode<>(child);
      nodeToMirror.put(child, cm);
      pm.children.addFirst(cm);
    }
  }

  public void setMarkerPosition(Marker marker, T node) {
    Log.d(TAG, "setMarkerPosition " + marker + " -> " + node);
    dirty = true;
    invalidate();
    markerPositions.put(marker, node);
    metaUI.considerMarker(marker);
  }

  //TODO Include marker positions?
  public void reset(T root, LinkedHashMap<Marker, T> markerPositions) {
    dirty = true;
    invalidate();
    this.root = new MirrorNode<>(root);
    this.nodeToMirror = mirrorRoot(this.root);

    this.markerPositions.clear();
    this.markerPositions.putAll(markerPositions);
    metaUI.setMarkers(markerPositions.keySet());
  }

  public void refresh() {
    reset(root.mirror, getMarkerPositions());
  }

  //// Gestures

  private void setInteractions() {
    final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
      @Override
      public boolean onDown(MotionEvent e) {
        return false;
      }

      @Override
      public void onShowPress(MotionEvent e) {
        //TODO Map
      }

      @Override
      public boolean onSingleTapUp(MotionEvent e) {
        //TODO Map
        return false;
      }

      @Override
      public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //TODO Map
        mViewportMatrix.postTranslate(-distanceX, -distanceY);
        if (!mViewportMatrix.invert(mViewportMatrixInverse)) {
          throw new IllegalStateException("Viewport matrix non-invertible!");
        }
        invalidate();
        return true;
      }

      @Override
      public void onLongPress(MotionEvent e) {
        //TODO Map
        float[] xy = {e.getX(), e.getY()};
        mViewportMatrixInverse.mapPoints(xy);
      }

      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //TODO Map
        return false;
      }
    });
    gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
      @Override
      public boolean onSingleTapConfirmed(MotionEvent e) {
        //TODO Map
        return false;
      }

      @Override
      public boolean onDoubleTap(MotionEvent e) {
        //TODO Map
        mViewportMatrix.reset();
        mViewportMatrixInverse.reset();
        viewportInitialized = false;
        invalidate();
        return true;
      }

      @Override
      public boolean onDoubleTapEvent(MotionEvent e) {
        //TODO Map
        return false;
      }
    });
    gestureDetector.setIsLongpressEnabled(true);
    final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
      private float lastFocusX;
      private float lastFocusY;

      @Override
      public boolean onScaleBegin(ScaleGestureDetector detector) {
        lastFocusX = detector.getFocusX();
        lastFocusY = detector.getFocusY();
        return true;
      }

      @Override
      public boolean onScale(ScaleGestureDetector detector) {
        Matrix transformationMatrix = new Matrix();
        float focusX = detector.getFocusX();
        float focusY = detector.getFocusY();

        //Zoom focus is where the fingers are centered,
        transformationMatrix.postTranslate(-focusX, -focusY);

        transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());

        /* Adding focus shift to allow for scrolling with two pointers down. Remove it to skip this functionality. This could be done in fewer lines, but for clarity I do it this way here */
        //Edited after comment by chochim
        float focusShiftX = focusX - lastFocusX;
        float focusShiftY = focusY - lastFocusY;
        transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY);
        mViewportMatrix.postConcat(transformationMatrix);
        if (!mViewportMatrix.invert(mViewportMatrixInverse)) {
          throw new IllegalStateException("Viewport matrix non-invertible!");
        }
        lastFocusX = focusX;
        lastFocusY = focusY;

        invalidate();
        return true;
      }

      @Override
      public void onScaleEnd(ScaleGestureDetector detector) {
      }
    });
    setOnTouchListener(new OnTouchListener() {
      private boolean isInteractingMeta = false;
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (isInteractingMeta) {
          isInteractingMeta = metaUI.doMetaInteraction(event);
        } else {
          if (metaUI.doesStartMetaInteraction(event)) {
            isInteractingMeta = metaUI.doMetaInteraction(event);
          } else {
            scaleGestureDetector.onTouchEvent(event);
            if (!scaleGestureDetector.isInProgress()) {
              //TODO May, for instance, miss touchUp events
              gestureDetector.onTouchEvent(event);
            }
          }
        }
        return true;
      }
    });
  }

  private void metaDropMarkerAtSpot(final Marker m, float[] dropPos) {
    mViewportMatrixInverse.mapPoints(dropPos);
    final MirrorNode<T> node = getNearestNode(dropPos);
    double[] nodePos = nodePositions.get(node);
    double dx = (nodePos[0] - dropPos[0]);
    double dy = (nodePos[1] - dropPos[1]);
    double dist = Math.sqrt((dx*dx) + (dy*dy));
    if (dist <= NODE_SIZE * 1.5) { //TODO Setting
      mainThread.post(new Runnable() {
        @Override
        public void run() {
          OnDropMarkerListener<T> l = onDropMarkerListener;
          if (l != null) {
            l.onDropMarker(m, node.mirror);
          }
        }
      });
    }
  }

  private MirrorNode<T> getNearestNode(float[] xy) {
    MirrorNode<T> best = null;
    double dist2 = Float.POSITIVE_INFINITY;
    for (Map.Entry<MirrorNode<T>, double[]> e : nodePositions.entrySet()) {
      double dx = (e.getValue()[0] - xy[0]);
      double dy = (e.getValue()[1] - xy[1]);
      double newDist2 = (dx*dx) + (dy*dy);
      if (newDist2 < dist2) {
        best = e.getKey();
        dist2 = newDist2;
      }
    }
    return best;
  }
}
