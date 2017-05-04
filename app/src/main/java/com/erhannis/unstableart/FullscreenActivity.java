package com.erhannis.unstableart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.erhannis.unstableart.history.HistoryManager;
import com.erhannis.unstableart.history.SetCanvasModeSMHN;
import com.erhannis.unstableart.history.SetColorSMHN;
import com.erhannis.unstableart.history.SetToolSizeSMHN;
import com.erhannis.unstableart.mechanics.FullState;
import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.color.IntColor;
import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.context.GroupLayer;
import com.erhannis.unstableart.mechanics.context.Layer;
import com.erhannis.unstableart.mechanics.context.StrokePL;
import com.erhannis.unstableart.mechanics.stroke.StrokePoint;
import com.erhannis.unstableart.settings.InputMapper;
import com.erhannis.unstableart.ui.layers.LayersFragment;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import java8.util.function.Consumer;

/**
 * The double-drawer functionality was taken from the question and answers at
 * http://stackoverflow.com/questions/17861755
 *
 */
public class FullscreenActivity extends AppCompatActivity implements LayersFragment.OnLayersFragmentInteractionListener {
//<editor-fold desc="Constants">
  private static final String TAG = "FullscreenActivity";

  private static final boolean AUTO_HIDE = true;
  private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
  private static final int UI_ANIMATION_DELAY = 300;

  final String serverUri = "tcp://192.168.0.6:1883";

  final String clientId = "ExampleAndroidClient";
  final String subscriptionTopic = "topic1";
  final String publishTopic = "topic2";
  final String publishMessage = "blah blah blah";
  private static final String COLOR_SPLINE_TOPIC = "color_spline";
  private static final String COLOR_VALUE_TOPIC = "color_value";

  private static final String M_REDO = "Redo";
  private static final String M_UNDO = "Undo";
  private static final String M_COLOR = "Color";
  private static final String M_SIZE = "Size";
  private static final String M_CANVAS_MODE = "Toggle canvas mode";
  private static final String M_SAVE = "Save";
  private static final String M_SAVE_AS = "Save as...";
  private static final String M_LOAD = "Load...";
  private static final String[] ACTIONS_MENU = {M_REDO, M_UNDO, M_COLOR, M_SIZE, M_CANVAS_MODE, M_SAVE, M_SAVE_AS, M_LOAD};
//</editor-fold>

//<editor-fold desc="UI">
  private SurfaceView surf;
  //TODO Is this permissible?
  private SurfaceHolder mSurfaceHolder;

  private DrawerLayout mDrawerLayout;
  private ActionBarDrawerToggle mDrawerToggle;
  private LinearLayout mLeftDrawerView;
  private ListView mRightDrawerView;

  private LayersFragment<String> layersFragment;

  private Matrix mViewportMatrix = new Matrix();
  private Matrix mViewportMatrixInverse = new Matrix();
//</editor-fold>

  private MqttAndroidClient mqttAndroidClient;

  //TODO I kinda wanted this to be final, but now it's how we're saving/loading files
  private HistoryManager historyManager = new HistoryManager();

  private File mLastSave = null;

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

    if(mDrawerLayout == null || mLeftDrawerView == null || mRightDrawerView == null || mDrawerToggle == null) {
      // Configure navigation drawer
      mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
      mLeftDrawerView = (LinearLayout)findViewById(R.id.left_drawer);
      mRightDrawerView = (ListView)findViewById(R.id.right_drawer);
      mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

        /** Called when a drawer has settled in a completely closed state. */
        public void onDrawerClosed(View drawerView) {
          if(drawerView.equals(mLeftDrawerView)) {
            getSupportActionBar().setTitle(getTitle());
            supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            mDrawerToggle.syncState();
          }
        }

        /** Called when a drawer has settled in a completely open state. */
        public void onDrawerOpened(View drawerView) {
          if(drawerView.equals(mLeftDrawerView)) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            mDrawerToggle.syncState();
          }
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
          // Avoid normal indicator glyph behaviour. This is to avoid glyph movement when opening the right drawer
          //super.onDrawerSlide(drawerView, slideOffset);
        }
      };

      initToolDrawer();
      initActionDrawer();

      mDrawerLayout.addDrawerListener(mDrawerToggle); // Set the drawer toggle as the DrawerListener
    }

    redraw();
  }

  protected void initToolDrawer() {
    /**/
    FragmentManager fragMan = getSupportFragmentManager();
    FragmentTransaction fragTransaction = fragMan.beginTransaction();

    layersFragment = new LayersFragment<String>();
    FullState fullState = historyManager.rebuild();
    layersFragment.setTree(fullState.iCanvas, fullState.state.iSelectedLayer.getId());
    fragTransaction.add(mLeftDrawerView.getId(), layersFragment, "LayersFragment");
    fragTransaction.commit();

    Button btnAddStrokeLayer = new Button(this);
    btnAddStrokeLayer.setText("Add stroke layer");
    btnAddStrokeLayer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //TODO There should probably be a better way of getting a uuid
        String uuid = historyManager.rebuild().iCanvas.getId();
        historyManager.executeCreateLayer(uuid, new StrokePL());
        redraw();
      }
    });
    mLeftDrawerView.addView(btnAddStrokeLayer);

    Button btnAddGroupLayer = new Button(this);
    btnAddGroupLayer.setText("Add group layer");
    btnAddGroupLayer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //TODO There should probably be a better way of getting a uuid
        String uuid = historyManager.rebuild().iCanvas.getId();
        historyManager.executeCreateLayer(uuid, new GroupLayer());
        redraw();
      }
    });
    mLeftDrawerView.addView(btnAddGroupLayer);

    if (1==1) return;
    /**/

    /*
    mLeftDrawerView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, new String[]{"Pen", "Brush", "test"}));
    //TODO Set selected listener

    mLeftDrawerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //TODO Brittle.
        //TODO Check if already selected?  Can that happen?
        switch (i) {
          case 0:
            historyManager.attach(new SetToolSMHN(new PenST()));
            break;
          case 1:
            historyManager.attach(new SetToolSMHN(new BrushST()));
            break;
          case 2:
            Intent intent = new Intent(getApplicationContext(), TreeTestActivity.class);
            startActivity(intent);
            break;
        }
      }
    });
    */
  }

  protected void initActionDrawer() {
    //TODO Add "Save" vs. "Save as..."
    mRightDrawerView.setAdapter(new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, ACTIONS_MENU));
    mRightDrawerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //TODO Brittle.
        String s = adapterView.getItemAtPosition(i).toString();
        switch (s) {
          case M_REDO:
            if (historyManager.tryRedo()) {
              redraw();
            }
            break;
          case M_UNDO:
            if (historyManager.tryUndo()) {
              redraw();
            }
            break;
          case M_COLOR:
            getTextInput(FullscreenActivity.this, "8 digit hex color", new Consumer<String>() {
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
          case M_SIZE:
            getTextInput(FullscreenActivity.this, "Size, double-precision", new Consumer<String>() {
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
          case M_CANVAS_MODE:
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
            redraw();
            break;
          case M_SAVE:
            if (mLastSave != null) {
              try {
                saveTo(mLastSave);
              } catch (IOException e) {
                e.printStackTrace();
                showToast(FullscreenActivity.this, "Error saving\n" + e.getMessage());
              }
              break;
            }
          case M_SAVE_AS:
            getTextInput(FullscreenActivity.this, "Save to filename", new Consumer<String>() {
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
          case M_LOAD:
            getTextInput(FullscreenActivity.this, "Load from filename", new Consumer<String>() {
              @Override
              public void accept(String s) {
                final File f = new File(s);
                if (f.exists()) {
                  //TODO Check if unsaved changes
                  try {
                    mLastSave = null; // May result in undesired behavior, but better than what MIGHT result from the alternative.
                    loadFrom(f);
                    mLastSave = f;
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
        }
      }
    });
  }

  private void saveTo(File file) throws IOException {
    //TODO Static Kryo?
    Kryo kryo = new Kryo();
    Output output = new Output(new FileOutputStream(file));
    output.writeString(BuildConfig.GIT_HASH);
    kryo.writeObject(output, historyManager);
    output.close();
  }

  private void loadFrom(File file) throws IOException {
    //TODO Static Kryo?
    Kryo kryo = new Kryo();
    String versionString = null;
    Input input = null;
    try {
      input = new Input(new FileInputStream(file));
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

    setContentView(R.layout.activity_fullscreen);

    //TODO Do we want this?
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    //initMqtt();

    //xstream.setMode(XStream.ID_REFERENCES);

    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    InputManager im = (InputManager) this.getSystemService(Context.INPUT_SERVICE);

    int[] ids = im.getInputDeviceIds();

    for (int i : ids) {
      InputDevice dev = im.getInputDevice(i);
      System.out.println(i + " : " + dev);
    }

    /*
    Random rand = new Random();
    for (int i = 0; i < 10; i++) {
      ArrayList<PointF> line = new ArrayList<PointF>();
      for (int j = 0; j < 10; j++) {
        line.add(new PointF(rand.nextFloat() * 500, rand.nextFloat() * 500));
      }
      lines.add(line);
    }
    */

    surf = (SurfaceView)findViewById(R.id.surfaceView);

    mSurfaceHolder = surf.getHolder();

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
        redraw();
        return true;
      }

      @Override
      public void onLongPress(MotionEvent e) {
        //TODO Map
        mViewportMatrix.reset();
        mViewportMatrixInverse.reset();
        redraw();
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
        showToast("Double tap");
        return false;
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
        redraw();
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
              float[] xy = new float[]{Float.NaN, Float.NaN};
              float x = Float.NaN;
              float y = Float.NaN;
              float p = Float.NaN;
              for (int i = 0; i < event.getHistorySize(); i++) {
                x = event.getHistoricalX(i);
                y = event.getHistoricalY(i);
                p = event.getHistoricalPressure(i);
                xy[0] = x;
                xy[1] = y;
                mViewportMatrixInverse.mapPoints(xy);
                historyManager.getCurStroke().points.add(new StrokePoint(xy[0], xy[1], p));
              }
              if (event.getX() != x || event.getY() != y) {
                //TODO Debatable
                x = event.getX();
                y = event.getY();
                p = event.getPressure();
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
          redraw();
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

  //TODO Is this use of mSurfaceHolder ok?
  protected void redraw() {
    if (mSurfaceHolder != null) {
      Canvas c = mSurfaceHolder.lockCanvas();
      if (c != null) {
        drawCanvas(c);
        mSurfaceHolder.unlockCanvasAndPost(c);
      }
    }
  }

  private void drawCanvas(Canvas viewport) {
    //TODO Fixed vs. Viewport canvas mode

    //TODO BACKGROUND background?
    viewport.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);

    //TODO INEFFICIENT, DON'T KEEP ...?
    FullState fullState = historyManager.rebuild();
    ArtContext artContext = new ArtContext();
    switch (fullState.state.canvasMode) {
      case FIXED: {
        // Don't forget; graphics origin is in the top left corner.  ... :/
        int cHPix = 1920; //NOTE Canvas render width
        int cVPix = 1007; //NOTE Canvas render height
        //NOTE Canvas target
        artContext.spatialBounds.left = 400;
        artContext.spatialBounds.right = artContext.spatialBounds.left + (cHPix * 4);
        artContext.spatialBounds.top = 400;
        artContext.spatialBounds.bottom = artContext.spatialBounds.top + (cVPix * 4);
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

        //TODO Seems fishy here
        if (layersFragment != null) {
          layersFragment.setTree(fullState.iCanvas, fullState.state.iSelectedLayer.getId());
        }

        //TODO This step could introduce extra rounding error?
        viewport.concat(mViewportMatrix);
        //TODO Paint?
        viewport.drawBitmap(bCanvas, canvasMatrix, null);
        //viewport.drawText("" + debugInfo, 10, 10, new Paint());
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

        //TODO This is for testing.  This should be separated out when we actually do the two modes.
        artContext.transform.set(mViewportMatrix);

        //TODO Inefficient?  Keep canvas?
        Bitmap bCanvas = Bitmap.createBitmap(cHPix, cVPix, Bitmap.Config.ARGB_8888);

        fullState.iCanvas.draw(artContext, bCanvas);

        //TODO Seems fishy here
        if (layersFragment != null) {
          layersFragment.setTree(fullState.iCanvas, fullState.state.iSelectedLayer.getId());
        }

        //TODO Paint?
        viewport.drawBitmap(bCanvas, 0, 0, null);
        break;
      }
      default:
        throw new IllegalStateException("Unrecognized canvas mode: " + fullState.state.canvasMode);
    }
    //viewport.drawText("" + debugInfo, 10, 10, new Paint());
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    mDrawerToggle.syncState();
    // Trigger the initial hide() shortly after the activity has been
    // created, to briefly hint to the user that UI controls
    // are available.
    delayedHide(100);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    //TODO What does this have to do with the left drawer?
    // If the nav drawer is open, hide action items related to the content view
    for(int i = 0; i< menu.size(); i++)
      menu.getItem(i).setVisible(!mDrawerLayout.isDrawerOpen(mLeftDrawerView));

    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    //TODO What does this have to do with the right drawer?
    switch(item.getItemId()) {
      case android.R.id.home:
        mDrawerToggle.onOptionsItemSelected(item);

        if(mDrawerLayout.isDrawerOpen(mRightDrawerView))
          mDrawerLayout.closeDrawer(mRightDrawerView);

        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  //TODO Allow mapping
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    //TODO Broke back button?
    System.out.println("keycode " + keyCode);
    switch (keyCode) {
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        if (historyManager.tryUndo()) {
          redraw();
        }
        return true;
      case KeyEvent.KEYCODE_VOLUME_UP:
        if (historyManager.tryRedo()) {
          redraw();
        }
        return true;
      default:
        return super.onKeyDown(keyCode, event);
    }
  }

  //<editor-fold desc="EXPORTABLE">
  // From http://stackoverflow.com/a/10904665/513038
  public static void getTextInput(Context ctx, String title, final Consumer<String> callback) {
    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    builder.setTitle(title);

// Set up the input
    final EditText input = new EditText(ctx);
    input.setInputType(InputType.TYPE_CLASS_TEXT);
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

  //<editor-fold desc="MQTT">
  private void initMqtt() {
    mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
    mqttAndroidClient.setCallback(new MqttCallbackExtended() {
      @Override
      public void connectComplete(boolean reconnect, String serverURI) {

        if (reconnect) {
          addToHistory("Reconnected to : " + serverURI);
          // Because Clean Session is true, we need to re-subscribe
          //subscribeToTopic(subscriptionTopic);
          subscribeToColorTopic();
        } else {
          addToHistory("Connected to: " + serverURI);
        }
      }

      @Override
      public void connectionLost(Throwable cause) {
        addToHistory("The Connection was lost.");
      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        addToHistory("Incoming message: " + new String(message.getPayload()));
      }

      @Override
      public void deliveryComplete(IMqttDeliveryToken token) {

      }
    });

    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    mqttConnectOptions.setAutomaticReconnect(true);
    mqttConnectOptions.setCleanSession(false);

    try {
      //addToHistory("Connecting to " + serverUri);
      mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
          disconnectedBufferOptions.setBufferEnabled(true);
          disconnectedBufferOptions.setBufferSize(100);
          disconnectedBufferOptions.setPersistBuffer(false);
          disconnectedBufferOptions.setDeleteOldestMessages(false);
          mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
          subscribeToColorTopic();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
          exception.printStackTrace();
          addToHistory("Failed to connect to: " + serverUri);
        }
      });
    } catch (Exception ex){
      ex.printStackTrace();
    }
  }


  public void subscribeToTopic(final String topic, IMqttMessageListener listener) {
    try {
      mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          addToHistory("Subscribed to " + topic);
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
          addToHistory("Failed to subscribe to " + topic);
        }
      });

      mqttAndroidClient.subscribe(topic, 0, listener);

    } catch (Exception ex){
      System.err.println("Exception whilst subscribing");
      ex.printStackTrace();
    }
  }

  public void subscribeToColorTopic() {
    subscribeToTopic(COLOR_VALUE_TOPIC, new IMqttMessageListener() {
      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        System.out.println("Message: " + topic + " : " + payload);
        //TODO Change color
        //curColor = new IntColor((int)Long.parseLong(payload));
      }
    });
  }

  public void publishMessage(String topic, String msg) {
    try {
      MqttMessage message = new MqttMessage();
      message.setPayload(msg.getBytes());
      mqttAndroidClient.publish(topic, message);
      addToHistory("Message Published");
      if(!mqttAndroidClient.isConnected()){
        addToHistory(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
      }
    } catch (Exception e) {
      System.err.println("Error Publishing: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void addToHistory(String s) {
    System.out.println(s);
  }
  //</editor-fold>

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
  public void onCreateLayer(String parentUuid, Layer child) {
    historyManager.executeCreateLayer(parentUuid, child);
    redraw();
  }

  @Override
  public void onSelectLayer(String layerUuid) {
    historyManager.executeSelectLayer(layerUuid);
    redraw();
  }

  @Override
  public void onMoveLayer(String layerUuid, String newParentUuid, int newPosition) {
    historyManager.executeMoveLayer(layerUuid, newParentUuid, newPosition);
    redraw();
  }
  //</editor-fold>
}