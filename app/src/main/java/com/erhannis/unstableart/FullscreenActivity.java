package com.erhannis.unstableart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.erhannis.android.distributedui.FragmentHandle;
import com.erhannis.android.distributedui.HubActivity;
import com.erhannis.android.orderednetworkview.Marker;
import com.erhannis.android.orderednetworkview.OrderedNetworkView;
import com.erhannis.mathnstuff.TimeoutTimer;
import com.erhannis.unstableart.history.HistoryManager;
import com.erhannis.unstableart.history.HistoryNode;
import com.erhannis.unstableart.history.SetCanvasModeSMHN;
import com.erhannis.unstableart.history.SetColorSMHN;
import com.erhannis.unstableart.history.SetToolSMHN;
import com.erhannis.unstableart.history.SetToolSizeSMHN;
import com.erhannis.unstableart.mechanics.FullState;
import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.color.IntColor;
import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.layers.Layer;
import com.erhannis.unstableart.mechanics.stroke.AirbrushST;
import com.erhannis.unstableart.mechanics.stroke.BrushST;
import com.erhannis.unstableart.mechanics.stroke.EraserST;
import com.erhannis.unstableart.mechanics.stroke.FillST;
import com.erhannis.unstableart.mechanics.stroke.PenST;
import com.erhannis.unstableart.mechanics.stroke.SolidBrushST;
import com.erhannis.unstableart.mechanics.stroke.StrokePoint;
import com.erhannis.unstableart.settings.InputMapper;
import com.erhannis.unstableart.ui.Spacer;
import com.erhannis.unstableart.ui.colors.ColorsFragment;
import com.erhannis.unstableart.ui.history.AnchorMarker;
import com.erhannis.unstableart.ui.history.EditMarker;
import com.erhannis.unstableart.ui.history.HistoryFragment;
import com.erhannis.unstableart.ui.history.LinkMarker;
import com.erhannis.unstableart.ui.history.VEMarker;
import com.erhannis.unstableart.ui.history.ViewMarker;
import com.erhannis.unstableart.ui.layers.LayersFragment;
import com.erhannis.unstableart.ui.tools.ActionsFragment;
import com.erhannis.unstableart.ui.tools.ToolsFragment;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java8.util.function.Consumer;

/**
 * The double-drawer functionality was taken from the question and answers at
 * http://stackoverflow.com/questions/17861755
 *
 */
public class FullscreenActivity extends HubActivity implements
        LayersFragment.OnLayersFragmentInteractionListener<String>,
        ColorsFragment.OnColorsFragmentInteractionListener,
        ToolsFragment.OnToolsFragmentInteractionListener,
        ActionsFragment.OnActionsFragmentInteractionListener,
        HistoryFragment.OnHistoryFragmentInteractionListener {
//<editor-fold desc="Constants">
  private static final String TAG = "FullscreenActivity";

  private static final boolean AUTO_HIDE = true;
  private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
  private static final int UI_ANIMATION_DELAY = 300;
//</editor-fold>

//<editor-fold desc="UI">
  private SurfaceView surf;
  private SurfaceHolder mSurfaceHolder;

  private LinearLayout mTopLeftDrawerView;
  private LinearLayout mBottomLeftDrawerView;
  private LinearLayout mTopRightDrawerView;
  private LinearLayout mBottomRightDrawerView;

  private LayersFragment<String> mLayersFragment;
  private ColorsFragment mColorsFragment;
  private ToolsFragment mToolsFragment;
  private ActionsFragment mActionsFragment;
  private HistoryFragment mHistoryFragment;

  private Matrix mViewportMatrix = new Matrix();
  private Matrix mViewportMatrixInverse = new Matrix();
//</editor-fold>

  //TODO I kinda wanted this to be final, but now it's how we're saving/loading files
  private HistoryManager historyManager = new HistoryManager();

  private File mLastSave = null;

  private final RedrawScheduler mRedrawScheduler = new RedrawScheduler(new Runnable() {
    @Override
    public void run() {
      if (mSurfaceHolder != null) {
        Canvas c = mSurfaceHolder.lockCanvas();
        if (c != null) {
          try {
            drawCanvas(c);
          } catch (Throwable t) {
            t.printStackTrace();
            //TODO Toasts could pile up
            showToast("An error occurred while rendering");
          }
          mSurfaceHolder.unlockCanvasAndPost(c);
        }
      }
    }
  });

  private final Handler mHideHandler = new Handler();
  private final Runnable mHidePart2Runnable = new Runnable() {
    @SuppressLint("InlinedApi")
    @Override
    public void run() {
    }
  };
  private final Runnable mShowPart2Runnable = new Runnable() {
    @Override
    public void run() {
      // Delayed display of UI elements
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
        actionBar.show();
      }
      //mControlsView.setVisibility(View.VISIBLE);
    }
  };
  private boolean mVisible;
  private final Runnable mHideRunnable = new Runnable() {
    @Override
    public void run() {
      hide();
    }
  };
  /**
   * Touch listener to use for in-layout UI controls to delay hiding the
   * system UI. This is to prevent the jarring behavior of controls going away
   * while interacting with activity UI.
   */
  private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
      if (AUTO_HIDE) {
        delayedHide(AUTO_HIDE_DELAY_MILLIS);
      }
      return false;
    }
  };

  @Override
  protected void onStart() {
    super.onStart();

    if(mTopLeftDrawerView == null || mBottomLeftDrawerView == null || mTopRightDrawerView == null || mBottomRightDrawerView == null) {
      // Configure navigation drawer
      mTopLeftDrawerView = (LinearLayout)findViewById(R.id.top_left_drawer);
      mBottomLeftDrawerView = (LinearLayout)findViewById(R.id.bottom_left_drawer);
      mTopRightDrawerView = (LinearLayout)findViewById(R.id.top_right_drawer);
      mBottomRightDrawerView = (LinearLayout)findViewById(R.id.bottom_right_drawer);

      // This is simultaneously elegant and horrifying, like a zombie ballerina
      for (View[] buttonDrawerPair : new View[][]{
              {findViewById(R.id.btnTopLeftDrawer), mTopLeftDrawerView},
              {findViewById(R.id.btnBottomLeftDrawer), mBottomLeftDrawerView},
              {findViewById(R.id.btnTopRightDrawer), mTopRightDrawerView},
              {findViewById(R.id.btnBottomRightDrawer), mBottomRightDrawerView}
      }) {
        Button button = (Button)buttonDrawerPair[0];
        View drawer = buttonDrawerPair[1];
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            switch (drawer.getVisibility()) {
              case View.VISIBLE:
                button.setText("OPEN");
                drawer.setVisibility(View.GONE);
                break;
              case View.INVISIBLE:
              case View.GONE:
              default:
                button.setText("CLOSE");
                drawer.setVisibility(View.VISIBLE);
                break;
            }
          }
        });
      }

      initColorDrawer();
      initLayerDrawer();
      initActionDrawer();
      initHistoryDrawer();
    }

    scheduleRedraw();
  }

  protected void initColorDrawer() {
    LinearLayout colorsContainer = new LinearLayout(this);
    colorsContainer.setId(View.generateViewId());
    colorsContainer.setOrientation(LinearLayout.VERTICAL);

    mTopLeftDrawerView.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

    mTopLeftDrawerView.addView(colorsContainer);

    FragmentManager fragMan = getSupportFragmentManager();
    FragmentTransaction fragTransaction = fragMan.beginTransaction();

    mColorsFragment = new ColorsFragment();
    //TODO Set current color?
    //FullState fullState = historyManager.rebuild();
    //colorsFragment.setCurColor();
    fragTransaction.add(colorsContainer.getId(), mColorsFragment, "ColorsFragment");

    fragTransaction.commit();
  }

  protected void initLayerDrawer() {
    LinearLayout layersContainer = new LinearLayout(this);
    layersContainer.setId(View.generateViewId());
    layersContainer.setOrientation(LinearLayout.VERTICAL);

    mBottomLeftDrawerView.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

    mBottomLeftDrawerView.addView(layersContainer);

    FragmentManager fragMan = getSupportFragmentManager();
    FragmentTransaction fragTransaction = fragMan.beginTransaction();

    mLayersFragment = new LayersFragment<String>();
    FullState fullState = historyManager.rebuild();
    mLayersFragment.setTree(fullState.iCanvas, fullState.state.iSelectedLayer.getId());
    fragTransaction.add(layersContainer.getId(), mLayersFragment, "LayersFragment");

    fragTransaction.commit();
  }

  protected void initActionDrawer() {
    LinearLayout actionsContainer = new LinearLayout(this);
    actionsContainer.setId(View.generateViewId());
    actionsContainer.setOrientation(LinearLayout.VERTICAL);

    LinearLayout toolsContainer = new LinearLayout(this);
    toolsContainer.setId(View.generateViewId());
    toolsContainer.setOrientation(LinearLayout.VERTICAL);

    mTopRightDrawerView.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

    mTopRightDrawerView.addView(actionsContainer);
    mTopRightDrawerView.addView(new Spacer(this, 0xFF000000));
    mTopRightDrawerView.addView(toolsContainer);

    FragmentManager fragMan = getSupportFragmentManager();
    FragmentTransaction fragTransaction = fragMan.beginTransaction();

    mActionsFragment = new ActionsFragment();
    fragTransaction.add(actionsContainer.getId(), mActionsFragment, "ActionsFragment");

    mToolsFragment = new ToolsFragment();
    fragTransaction.add(toolsContainer.getId(), mToolsFragment, "ToolsFragment");

    fragTransaction.commit();
  }

  protected void initHistoryDrawer() {
    LinearLayout historyContainer = new LinearLayout(this);
    historyContainer.setId(View.generateViewId());
    historyContainer.setOrientation(LinearLayout.VERTICAL);

    mBottomRightDrawerView.addView(historyContainer);

    FragmentManager fragMan = getSupportFragmentManager();
    FragmentTransaction fragTransaction = fragMan.beginTransaction();

    mHistoryFragment = new HistoryFragment();
    LinkedHashMap<Marker, HistoryNode> markers = new LinkedHashMap<>();
    VEMarker veMarker = new VEMarker();
    ViewMarker viewMarker = new ViewMarker();
    EditMarker editMarker = new EditMarker();
    LinkMarker linkMarker = new LinkMarker();
    AnchorMarker anchorMarker = new AnchorMarker();
    HistoryNode root = historyManager.getRoot();
    //NOTE List markers
    markers.put(veMarker, null);
    markers.put(viewMarker, root);
    markers.put(editMarker, root);
    markers.put(linkMarker, null);
    markers.put(anchorMarker, null);
    mHistoryFragment.reset(root, markers);
    //TODO Setup
    fragTransaction.add(historyContainer.getId(), mHistoryFragment, "HistoryFragment");

    fragTransaction.commit();
  }

  private void saveTo(File file) throws IOException {
    //TODO Static Kryo?
    Kryo kryo = new Kryo();
    kryo.setRegistrationRequired(false); //TODO SECURITY My understanding is that this could cause security holes
    Output output = new Output(new FileOutputStream(file));
    output.writeString(BuildConfig.GIT_HASH);
    kryo.writeObject(output, historyManager);
    output.close();
  }

  private void loadFrom(File file) throws IOException {
    loadFrom(new FileInputStream(file));
  }

  private void loadFrom(InputStream inputStream) throws IOException {
    //TODO Static Kryo?
    Kryo kryo = new Kryo();
    kryo.setRegistrationRequired(false); //TODO SECURITY My understanding is that this could cause security holes
    String versionString = null;
    Input input = null;
    try {
      input = new Input(inputStream);
      versionString = input.readString();
      historyManager = kryo.readObject(input, HistoryManager.class);
      input.close();
    } catch (KryoException e) {
      e.printStackTrace();
      try {
        input.close();
      } catch (Exception e2) {
      }
      if (versionString != null) {
        throw new RuntimeException(versionString, e);
      } else {
        throw e;
      }
    }
    //TODO Throw error with version on fail?
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri data = this.getIntent().getData();
    if (data != null) {
      System.out.println("Autoloading " + data.getPath());
      try {
        InputStream inputStream = getContentResolver().openInputStream(data);
        loadFrom(inputStream);
        File f = new File(data.getPath());
        if (f.exists() && f.isFile()) {
          mLastSave = f;
        }
      } catch (Throwable e) {
        e.printStackTrace();
        showToast("Error loading file\nErr message OR version string: " + e.getMessage());
      }
    }

    setContentView(R.layout.activity_fullscreen);

    //TODO Do we want this?
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    //TODO Remove, probably
    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    InputManager im = (InputManager) this.getSystemService(Context.INPUT_SERVICE);
    int[] ids = im.getInputDeviceIds();
    for (int i : ids) {
      InputDevice dev = im.getInputDevice(i);
      System.out.println(i + " : " + dev);
    }

    surf = (SurfaceView)findViewById(R.id.surfaceView);

    mSurfaceHolder = surf.getHolder();
    mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
      @Override
      public void surfaceCreated(SurfaceHolder holder) {
        scheduleRedraw();
      }

      @Override
      public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

      }

      @Override
      public void surfaceDestroyed(SurfaceHolder holder) {

      }
    });

    //TODO Export some of this view stuff?

    final GestureDetectorCompat mGestureDetector = new GestureDetectorCompat(this, new GestureDetector.OnGestureListener() {
      @Override
      public boolean onDown(MotionEvent e) {
        return false;
      }

      @Override
      public void onShowPress(MotionEvent e) {
      }

      @Override
      public boolean onSingleTapUp(MotionEvent e) {
        //TODO Map?
        showToast("Tap up");
        return false;
      }

      @Override
      public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mViewportMatrix.postTranslate(-distanceX, -distanceY);
        if (!mViewportMatrix.invert(mViewportMatrixInverse)) {
          throw new IllegalStateException("Viewport matrix non-invertible!");
        }
        scheduleRedraw();
        return true;
      }

      @Override
      public void onLongPress(MotionEvent e) {
        //TODO Map
        showToast("Long press");
      }

      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
      }
    });

    mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
      @Override
      public boolean onSingleTapConfirmed(MotionEvent e) {
        //TODO Map
        showToast("Single tap confirmed");
        return false;
      }

      @Override
      public boolean onDoubleTap(MotionEvent e) {
        //TODO Map
        mViewportMatrix.reset();
        mViewportMatrixInverse.reset();
        scheduleRedraw();
        return true;
      }

      @Override
      public boolean onDoubleTapEvent(MotionEvent e) {
        //TODO Map?
        showToast("Double tap event");
        return false;
      }
    });

    //TODO Context click?

    // http://stackoverflow.com/a/19545542/513038
    final ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
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
        scheduleRedraw();
        return true;
      }

      @Override
      public void onScaleEnd(ScaleGestureDetector detector) {
      }
    });

    surf.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        //TODO Make SURE this thing gets all the processing it needs.  Do NOT drop points or strokes, so help me.
        //TODO Transform
        //TODO Pointers?
        //TODO Buttons?
        //TODO TOOL_TYPE_ERASER TOOL_TYPE_STYLUS TOOL_TYPE_FINGER
        if (InputMapper.getMapper().deviceDraws(event.getDevice())) {
          switch (event.getActionMasked()) {
            case MotionEvent.ACTION_CANCEL:
              //TODO DO I want to rollback?
              historyManager.rollbackStrokeTransaction();
              return true; //TODO Still redraw?
            case MotionEvent.ACTION_UP:
              historyManager.commitStrokeTransaction();
              break;
            case MotionEvent.ACTION_DOWN:
              historyManager.startStrokeTransaction(); // Continue into next case
            case MotionEvent.ACTION_MOVE: //TODO What about basically anything else?
              //TODO Check transaction?
              System.out.println("event " + event.getActionMasked());
              float[] xy = new float[]{Float.NaN, Float.NaN};
              float x = Float.NaN;
              float y = Float.NaN;
              float p = Float.NaN;
              for (int i = 0; i < event.getHistorySize(); i++) {
                x = event.getHistoricalX(i);
                y = event.getHistoricalY(i);
                p = event.getHistoricalPressure(i);
                System.out.println("hp {" + x + ", " + y + ", " + p + "}");
                xy[0] = x;
                xy[1] = y;
                mViewportMatrixInverse.mapPoints(xy);
                historyManager.getCurStroke().points.add(new StrokePoint(xy[0], xy[1], p));
              }
              //TODO If using finger, this can be much closer to the end of the historical points than is good for some brushes.
              if (event.getX() != x || event.getY() != y) {
                //TODO Debatable
                x = event.getX();
                y = event.getY();
                p = event.getPressure();
                System.out.println("ep {" + x + ", " + y + ", " + p + "}");
                xy[0] = x;
                xy[1] = y;
                mViewportMatrixInverse.mapPoints(xy);
                historyManager.getCurStroke().points.add(new StrokePoint(xy[0], xy[1], p));
              }

              return true; //TODO SHOULD draw?
            default:
              Log.d(TAG, "Unhandled action: " + event.getActionMasked());
              break;
          }
          scheduleRedraw();
          return true;
        } else if (InputMapper.getMapper().deviceMoves(event.getDevice())) {
          boolean retVal = mScaleGestureDetector.onTouchEvent(event);
          if (!mScaleGestureDetector.isInProgress()) {
            retVal = mGestureDetector.onTouchEvent(event) || retVal;
          }
          return retVal || FullscreenActivity.super.onTouchEvent(event);
        } else {
          // Dunno
          return FullscreenActivity.super.onTouchEvent(event);
        }
      }
    });

    mVisible = true;

    //toggle();
  }

  //TODO Maybe just call it on the object in the first place?
  protected void scheduleRedraw() {
    mRedrawScheduler.scheduleRedraw();
  }

  /**
   * Draws onto the passed viewport.  Returns the rendered canvas bitmap.  The former is usually more important.
   * If all you want is the latter, you still have to pass in a viewport, but it could be tiny.
   * @param viewport
   * @return
   */
  private Bitmap drawCanvas(Canvas viewport) {
    //TODO We're passing in the viewport, but using mViewportMatrix?
    //TODO We could split this method for just returning the canvas bitmap.

    final Bitmap result;

    //TODO BACKGROUND background?
    viewport.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);

    //TODO INEFFICIENT, DON'T KEEP ...?
    FullState fullState = historyManager.rebuild();
    ArtContext artContext = new ArtContext();
    //TODO Might be nice to merge these two as far as possible
    switch (fullState.state.canvasMode) {
      case FIXED: {
        // Don't forget; graphics origin is in the top left corner.  ... :/
        //TODO Allow canvas redimming, rescaling
        int cHPix = 1920; //NOTE Canvas render width
        int cVPix = 1007; //NOTE Canvas render height
        //NOTE Canvas target
        artContext.spatialBounds.left = 0;
        artContext.spatialBounds.right = artContext.spatialBounds.left + cHPix;
        artContext.spatialBounds.top = 0;
        artContext.spatialBounds.bottom = artContext.spatialBounds.top + cVPix;
        Matrix canvasMatrix = new Matrix();
        canvasMatrix.preTranslate(artContext.spatialBounds.left, artContext.spatialBounds.top);
        canvasMatrix.preScale((artContext.spatialBounds.right - artContext.spatialBounds.left) / cHPix, (artContext.spatialBounds.bottom - artContext.spatialBounds.top) / cVPix);
        //TODO Rotate?
        Matrix canvasMatrixInverse = new Matrix();
        canvasMatrix.invert(canvasMatrixInverse);

        //TODO This is for testing.  This should be separated out when we actually do the two modes.
        artContext.transform.set(canvasMatrixInverse);

        //TODO Inefficient?  Keep canvas?
        Bitmap bCanvas = Bitmap.createBitmap(cHPix, cVPix, Bitmap.Config.ARGB_8888);

        fullState.iCanvas.draw(artContext, bCanvas);

        //TODO This step could introduce extra rounding error?
        viewport.concat(mViewportMatrix);
        //TODO Paint?
        viewport.drawBitmap(bCanvas, canvasMatrix, null);
        //viewport.drawText("" + debugInfo, 10, 10, new Paint());

        result = bCanvas;
        break;
      }
      case FOLLOW_VIEWPORT: {
        // Don't forget; graphics origin is in the top left corner.  ... :/
        int cHPix = surf.getWidth();
        int cVPix = surf.getHeight();
        artContext.spatialBounds.left = 0;
        artContext.spatialBounds.right = artContext.spatialBounds.left + cHPix;
        artContext.spatialBounds.top = 0;
        artContext.spatialBounds.bottom = artContext.spatialBounds.top + cVPix;

        System.out.println(cHPix + " x " + cVPix);

        artContext.transform.set(mViewportMatrix);

        //TODO Inefficient?  Keep canvas?
        Bitmap bCanvas = Bitmap.createBitmap(cHPix, cVPix, Bitmap.Config.ARGB_8888);

        fullState.iCanvas.draw(artContext, bCanvas);

        //TODO Paint?
        viewport.drawBitmap(bCanvas, 0, 0, null);

        result = bCanvas;
        break;
      }
      default:
        throw new IllegalStateException("Unrecognized canvas mode: " + fullState.state.canvasMode);
    }

    //TODO Seems fishy here
    if (mLayersFragment != null) {
      mLayersFragment.setTree(fullState.iCanvas, fullState.state.iSelectedLayer.getId());
    }

    //TODO Ditto
    //TODO And wow, this seems pretty awful
    if (mHistoryFragment != null) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          //TODO Remember that the UI thread must be unburdened, to catch brush strokes.  Are these calls problematic?
          //TODO THIS IS THE WORST
          OrderedNetworkView<HistoryNode> onvHistory = mHistoryFragment.getOnvHistory();
          if (onvHistory != null) {
            HistoryNode root = historyManager.getRoot();
            HistoryNode selectedForView = historyManager.getSelectedForView();
            HistoryNode selectedForEdit = historyManager.getSelectedForEdit();
            HistoryNode selectedForAnchor = historyManager.getSelectedForAnchor();
            LinkedHashMap<Marker, HistoryNode> markerPositions = onvHistory.getMarkerPositions();
            Iterator<Map.Entry<Marker, HistoryNode>> iter = markerPositions.entrySet().iterator();
            //NOTE List markers
            iter.next(); // View/Edit
            iter.next().setValue(selectedForView); // View
            iter.next().setValue(selectedForEdit); // Edit
            iter.next(); // Link
            iter.next().setValue(selectedForAnchor); // Anchor
            mHistoryFragment.reset(root, markerPositions);
            //onvHistory.setMarkerPosition(onvHistory.getMarkerPositions().keySet().iterator().next(), historyManager.getSelected());
          }
        }
      });
    }

    //viewport.drawText("" + debugInfo, 10, 10, new Paint());

    return result;
  }

  @Override
  public void hostFragment(FragmentHandle fragmentHandle) {
    //TODO Do
  }

  @Override
  public void dropFragment(FragmentHandle fragmentHandle) {
    //TODO Do
  }

  //TODO Allow mapping
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    System.out.println("keycode " + keyCode);
    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        if (historyManager.tryUndo()) {
          scheduleRedraw();
        }
        return true;
      case KeyEvent.KEYCODE_VOLUME_UP:
        if (historyManager.tryRedo()) {
          scheduleRedraw();
        }
        return true;
      default:
        return super.onKeyDown(keyCode, event);
    }
  }

  //<editor-fold desc="EXPORTABLE">
  public static void getTextInput(Context ctx, String title, final Consumer<String> callback) {
    getTextInput(ctx, title, "", callback);
  }

  // From http://stackoverflow.com/a/10904665/513038
  public static void getTextInput(Context ctx, String title, CharSequence existing, final Consumer<String> callback) {
    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    builder.setTitle(title);

// Set up the input
    final EditText input = new EditText(ctx);
    input.setInputType(InputType.TYPE_CLASS_TEXT);
    if (existing != null) {
      input.setText(existing);
    }
    builder.setView(input);

// Set up the buttons
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        callback.accept(input.getText().toString());
      }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });

    builder.show();
  }

  public static void getYesNoCancelInput(Context ctx, String title, final Consumer<Boolean> callback) {
    new AlertDialog.Builder(ctx)
            .setTitle(title)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setCancelable(true)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                callback.accept(true);
              }})
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                callback.accept(false);
              }
            } ).setNeutralButton(android.R.string.cancel, null).show();
  }

  public static void showToast(final Activity ctx, final String text) {
    ctx.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
      }
    });
  }

  public static Set<String> getDeviceSources(InputDevice inputDevice) {
    int sourcesInt = inputDevice.getSources();
    HashSet<String> sourcesSet = new HashSet<>();
    if (0 < (sourcesInt & InputDevice.SOURCE_ANY)) {
      sourcesSet.add("SOURCE_ANY");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_BLUETOOTH_STYLUS)) {
      sourcesSet.add("SOURCE_BLUETOOTH_STYLUS");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_CLASS_BUTTON)) {
      sourcesSet.add("SOURCE_CLASS_BUTTON");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_CLASS_JOYSTICK)) {
      sourcesSet.add("SOURCE_CLASS_JOYSTICK");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_CLASS_MASK)) {
      sourcesSet.add("SOURCE_CLASS_MASK");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_CLASS_NONE)) {
      sourcesSet.add("SOURCE_CLASS_NONE");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_CLASS_POINTER)) {
      sourcesSet.add("SOURCE_CLASS_POINTER");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_CLASS_POSITION)) {
      sourcesSet.add("SOURCE_CLASS_POSITION");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_CLASS_POINTER)) {
      sourcesSet.add("SOURCE_CLASS_POINTER");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_CLASS_TRACKBALL)) {
      sourcesSet.add("SOURCE_CLASS_TRACKBALL");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_DPAD)) {
      sourcesSet.add("SOURCE_DPAD");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_GAMEPAD)) {
      sourcesSet.add("SOURCE_GAMEPAD");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_HDMI)) {
      sourcesSet.add("SOURCE_HDMI");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_JOYSTICK)) {
      sourcesSet.add("SOURCE_JOYSTICK");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_KEYBOARD)) {
      sourcesSet.add("SOURCE_KEYBOARD");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_MOUSE)) {
      sourcesSet.add("SOURCE_MOUSE");
    }
//    if (0 < (sourcesInt & InputDevice.SOURCE_MOUSE_RELATIVE)) {
//      sourcesSet.add("SOURCE_MOUSE_RELATIVE");
//    }
//    if (0 < (sourcesInt & InputDevice.SOURCE_ROTARY_ENCODER)) {
//      sourcesSet.add("SOURCE_ROTARY_ENCODER");
//    }
    if (0 < (sourcesInt & InputDevice.SOURCE_STYLUS)) {
      sourcesSet.add("SOURCE_STYLUS");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_TOUCHPAD)) {
      sourcesSet.add("SOURCE_TOUCHPAD");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_TOUCHSCREEN)) {
      sourcesSet.add("SOURCE_TOUCHSCREEN");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_TOUCHSCREEN)) {
      sourcesSet.add("SOURCE_TOUCHSCREEN");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_TOUCH_NAVIGATION)) {
      sourcesSet.add("SOURCE_TOUCH_NAVIGATION");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_TRACKBALL)) {
      sourcesSet.add("SOURCE_TRACKBALL");
    }
    if (0 < (sourcesInt & InputDevice.SOURCE_UNKNOWN)) {
      sourcesSet.add("SOURCE_UNKNOWN");
    }
    return sourcesSet;
  }

  public void showToast(String text) {
    showToast(this, text);
  }
  //</editor-fold>

  //TODO Incorporate MQTT, sometime?

  //<editor-fold desc="SHOW/HIDE OVERLAY">
  private void toggle() {
    if (mVisible) {
      hide();
    } else {
      show();
    }
  }

  private void hide() {
    // Hide UI first
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }
    //mControlsView.setVisibility(View.GONE);
    mVisible = false;

    // Schedule a runnable to remove the status and navigation bar after a delay
    mHideHandler.removeCallbacks(mShowPart2Runnable);
    mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
  }

  @SuppressLint("InlinedApi")
  private void show() {
    // Show the system bar
    //mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    mVisible = true;

    // Schedule a runnable to display UI elements after a delay
    mHideHandler.removeCallbacks(mHidePart2Runnable);
    mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
  }

  /**
   * Schedules a call to hide() in [delay] milliseconds, canceling any
   * previously scheduled calls.
   */
  private void delayedHide(int delayMillis) {
    mHideHandler.removeCallbacks(mHideRunnable);
    mHideHandler.postDelayed(mHideRunnable, delayMillis);
  }
  //</editor-fold>

  //<editor-fold desc="FRAGMENT INTERACTIONS">
  @Override
  public List<FragmentHandle> getFragmentHandles() {
    ArrayList<FragmentHandle> fragmentHandles = new ArrayList<>();
    fragmentHandles.add(new FragmentHandle(ColorsFragment.class, "ColorsFragment"));
    return fragmentHandles;
  }

  @Override
  public Object onMessage(String method, Object... objects) {
    switch (method) {
      case "onSelectColor":
        onSelectColor((Color)objects[0]);
        break;
      case "onSelectHistory":
        onSelectHistory((HistoryNode)objects[0], (HistoryNode)objects[1], (HistoryNode)objects[2], (Integer)objects[3]);
        break;
      case "onMoveColorsFragment":
        onMoveColorsFragment();
        break;
      default:
        throw new RuntimeException("Unknown method: " + method);
    }
    return null;
  }

  @Override
  public void onCreateLayer(String parentUuid, Layer child) {
    historyManager.executeCreateLayer(parentUuid, child);
    scheduleRedraw();
  }

  @Override
  public void onSelectLayer(String layerUuid) {
    historyManager.executeSelectLayer(layerUuid);
    scheduleRedraw();
  }

  @Override
  public void onMoveLayer(String layerUuid, String newParentUuid, int newPosition) {
    historyManager.executeMoveLayer(layerUuid, newParentUuid, newPosition);
    scheduleRedraw();
  }

  @Override
  public void onShowHideLayer(String layerUuid, boolean visible) {
    historyManager.executeShowHideLayer(layerUuid, visible);
    scheduleRedraw();
  }

  //TODO Should this be at the top of the code?
  protected Color mScheduledColor = null;
  //TODO Optionize the timeout?
  protected final TimeoutTimer mColorTimer = new TimeoutTimer(500, new Runnable() {
    @Override
    public void run() {
      Color newColor = mScheduledColor;
      if (newColor != null) {
        //TODO Think of other potential solutions
        historyManager.attach(new SetColorSMHN(mScheduledColor));
        scheduleRedraw();
      }
    }
  });

  @Override
  //TODO Should it (potentially) be by color uuid?
  public void onSelectColor(Color color) {
    mScheduledColor = color;
    mColorTimer.restart();
  }

  //TODO This is SUCH a hack
  protected boolean mColorsFragmentIsLocal = true;
  @Override
  public void onMoveColorsFragment() {
    if (mColorsFragmentIsLocal) {
//      FragmentManager fragMan = getSupportFragmentManager();
//      FragmentTransaction fragTransaction = fragMan.beginTransaction();
//      fragTransaction.remove(mColorsFragment); //TODO Would this survive a rotation?
//      fragTransaction.commit();
      //TODO
      //sendRawToSatellites(new DistributedUIFragmentChange(DistributedUIFragmentChange.ChangeType.HOST_FRAGMENT, new FragmentHandle(ColorsFragment.class, "ColorsFragment")));
      mColorsFragmentIsLocal = false;
    } else {
//      mColorsFragment.get
//
//      FragmentManager fragMan = getSupportFragmentManager();
//      FragmentTransaction fragTransaction = fragMan.beginTransaction();
//
//      mLayersFragment = new LayersFragment<String>();
//      FullState fullState = historyManager.rebuild();
//      mLayersFragment.setTree(fullState.iCanvas, fullState.state.iSelectedLayer.getId());
//      fragTransaction.add(layersContainer.getId(), mLayersFragment, "LayersFragment");
//
//      mColorsFragment = new ColorsFragment();
//      //TODO Set current color?
//      //FullState fullState = historyManager.rebuild();
//      //colorsFragment.setCurColor();
//      fragTransaction.add(colorsContainer.getId(), mColorsFragment, "ColorsFragment");
//
//      fragTransaction.commit();
      //TODO
      //sendRawToSatellites(new DistributedUIFragmentChange(DistributedUIFragmentChange.ChangeType.DROP_FRAGMENT, new FragmentHandle(ColorsFragment.class, "ColorsFragment")));
      mColorsFragmentIsLocal = true;
    }
  }

  @Override
  public void onSelectTool(String tool) {
    //TODO Hmm.  Instances seem unnecessary for these two tools.
    //TODO Select by uuid?
    switch (tool) {
      case ToolsFragment.M_PEN:
        historyManager.attach(new SetToolSMHN(new PenST()));
        break;
      case ToolsFragment.M_BRUSH:
        historyManager.attach(new SetToolSMHN(new BrushST()));
        break;
      case ToolsFragment.M_SOLID_BRUSH:
        historyManager.attach(new SetToolSMHN(new SolidBrushST()));
        break;
      case ToolsFragment.M_AIRBRUSH:
        historyManager.attach(new SetToolSMHN(new AirbrushST()));
        break;
      case ToolsFragment.M_ERASER:
        historyManager.attach(new SetToolSMHN(new EraserST()));
        break;
      case ToolsFragment.M_FILL:
        historyManager.attach(new SetToolSMHN(new FillST()));
        break;
      default:
        System.err.println("Unhandled tool: " + tool);
        showToast("Unhandled tool: " + tool);
        break;
    }
  }

  @Override
  public void onSelectAction(String action) {
    switch (action) {
      case ActionsFragment.M_REDO:
        if (historyManager.tryRedo()) {
          scheduleRedraw();
        }
        break;
      case ActionsFragment.M_UNDO:
        if (historyManager.tryUndo()) {
          scheduleRedraw();
        }
        break;
      case ActionsFragment.M_COLOR:
        int curColor = historyManager.rebuild().state.color.getARGBInt(); //LOSS //TODO This seems really heavy
        getTextInput(FullscreenActivity.this, "8 digit hex color", Long.toString(curColor, 16), new Consumer<String>() {
          @Override
          public void accept(String s) {
            try {
              int intARGB = (int)Long.parseLong(s, 16); //LOSS
              historyManager.attach(new SetColorSMHN(new IntColor(intARGB)));
            } catch (NumberFormatException nfe) {
              showToast(FullscreenActivity.this, "Invalid hex string; use 0-9 and A-F");
            }
          }
        });
        break;
      case ActionsFragment.M_SIZE:
        double curSize = historyManager.rebuild().state.size; //TODO This seems really heavy
        getTextInput(FullscreenActivity.this, "Size, double-precision", curSize + "", new Consumer<String>() {
          @Override
          public void accept(String s) {
            try {
              double size = Double.parseDouble(s);
              historyManager.attach(new SetToolSizeSMHN(size));
            } catch (NumberFormatException nfe) {
              showToast(FullscreenActivity.this, "Invalid double, decimal numbers only");
            }
          }
        });
        break;
      case ActionsFragment.M_CANVAS_MODE:
        //TODO C'mon, there's gotta be a better way
        State.CanvasMode curMode = historyManager.rebuild().state.canvasMode;
        switch (curMode) {
          case FIXED:
            historyManager.attach(new SetCanvasModeSMHN(State.CanvasMode.FOLLOW_VIEWPORT));
            break;
          case FOLLOW_VIEWPORT:
            historyManager.attach(new SetCanvasModeSMHN(State.CanvasMode.FIXED));
            break;
          default:
            throw new IllegalStateException("Invalid current mode: " + curMode);
        }
        scheduleRedraw();
        break;
      case ActionsFragment.M_SAVE:
        if (mLastSave != null) {
          try {
            saveTo(mLastSave);
          } catch (IOException e) {
            e.printStackTrace();
            showToast(FullscreenActivity.this, "Error saving\n" + e.getMessage());
          }
          break;
        }
      case ActionsFragment.M_SAVE_AS:
        String filename = mLastSave != null ? mLastSave.getAbsolutePath() : "/sdcard/" + System.currentTimeMillis() + ".uaf";
        getTextInput(FullscreenActivity.this, "Save to path/filename (*.uaf)", filename, new Consumer<String>() {
          @Override
          public void accept(String s) {
            final File f = new File(s);
            if (f.exists()) {
              getYesNoCancelInput(FullscreenActivity.this, "File exists.  Overwrite?", new Consumer<Boolean>() {
                @Override
                public void accept(Boolean overwrite) {
                  if (overwrite) {
                    try {
                      saveTo(f);
                      mLastSave = f;
                    } catch (IOException e) {
                      e.printStackTrace();
                      showToast(FullscreenActivity.this, "Error saving\n" + e.getMessage());
                    }
                  }
                }
              });
            } else {
              if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
              }
              try {
                saveTo(f);
                mLastSave = f;
              } catch (IOException e) {
                e.printStackTrace();
                showToast(FullscreenActivity.this, "Error saving\n" + e.getMessage());
              }
            }
          }
        });
        break;
      case ActionsFragment.M_LOAD:
        getTextInput(FullscreenActivity.this, "Load from path/filename", new Consumer<String>() {
          @Override
          public void accept(String s) {
            final File f = new File(s);
            if (f.exists()) {
              //TODO Check if unsaved changes
              try {
                mLastSave = null; // May result in undesired behavior, but better than what MIGHT result from the alternative.
                loadFrom(f);
                mLastSave = f;
                scheduleRedraw();
              } catch (Exception e) {
                e.printStackTrace();
                showToast(FullscreenActivity.this, "Couldn't load file, probably older version.\nErr message OR version string: " + e.getMessage());
              }
            } else {
              showToast(FullscreenActivity.this, "Can't find file!");
            }
          }
        });
        break;
      case ActionsFragment.M_EXPORT:
        getTextInput(FullscreenActivity.this, "Export to path/filename (jpg/png/webp)", new Consumer<String>() {
          @Override
          public void accept(String s) {
            final File f = new File(s);

            // Hmm.  Kinda questionable way of reducing code duplication,
            final Runnable doExport = new Runnable() {
              @Override
              public void run() {
                FileOutputStream fos = null;
                try {
                  //TODO Allow settings - compression, format, background
                  fos = new FileOutputStream(f);
                  Bitmap bCanvas = drawCanvas(new Canvas());
                  switch (MiscUtils.getExtension(f).toUpperCase()) {
                    case "JPG":
                    case "JPEG":
                      bCanvas.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                      break;
                    case "PNG":
                      bCanvas.compress(Bitmap.CompressFormat.PNG, 100, fos);
                      break;
                    case "WEBP":
                      bCanvas.compress(Bitmap.CompressFormat.WEBP, 90, fos);
                      break;
                    default:
                      throw new IOException("Unknown extension");
                  }
                  fos.flush();
                } catch (IOException e) {
                  e.printStackTrace();
                  showToast(FullscreenActivity.this, "Error exporting\n" + e.getMessage());
                } finally {
                  if (fos != null) {
                    try {
                      fos.close();
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                  }
                }
              }
            };

            if (f.exists()) {
              getYesNoCancelInput(FullscreenActivity.this, "File exists.  Overwrite?", new Consumer<Boolean>() {
                @Override
                public void accept(Boolean overwrite) {
                  if (overwrite) {
                    doExport.run();
                  }
                }
              });
            } else {
              if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
              }
              doExport.run();
            }
          }
        });
        break;
    }
  }

  @Override
  public void onSelectHistory(HistoryNode viewNode, HistoryNode editNode, HistoryNode anchorNode, int priorityMarker) {
    historyManager.selectMarkers(viewNode, editNode, anchorNode, priorityMarker);
    scheduleRedraw();
  }
  //</editor-fold>
}