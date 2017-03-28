package com.erhannis.unstableart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.input.InputManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.InputDevice;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.erhannis.unstableart.history.HistoryManager;
import com.erhannis.unstableart.history.SetToolSMHN;
import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.context.UACanvas;
import com.erhannis.unstableart.mechanics.stroke.BrushST;
import com.erhannis.unstableart.mechanics.stroke.PenST;
import com.erhannis.unstableart.mechanics.stroke.StrokePoint;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * The double-drawer functionality was taken from the question and answers at
 * http://stackoverflow.com/questions/17861755
 *
 */
public class FullscreenActivity extends AppCompatActivity {
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
//</editor-fold>

//<editor-fold desc="UI">
  private SurfaceView surf;
  //TODO Is this permissible?
  private SurfaceHolder mSurfaceHolder;

  private DrawerLayout mDrawerLayout;
  private ActionBarDrawerToggle mDrawerToggle;
  private ListView mLeftDrawerView;
  private ListView mRightDrawerView;
//</editor-fold>

  private MqttAndroidClient mqttAndroidClient;

  private final HistoryManager historyManager = new HistoryManager();

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
      mLeftDrawerView = (ListView)findViewById(R.id.left_drawer);
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

      mLeftDrawerView.setAdapter(new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_single_choice, new String[]{"Pen", "Brush"}));
      //TODO Set selected listener
      /*
      mLeftDrawerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
          //TODO Brittle.
          //TODO Check if already selected?  Can that happen?
          switch (i) {
            case 0:
              historyManager.attach(new SetToolSMHN(new PenST()));
              break;
            case 1:
              historyManager.attach(new SetToolSMHN(new BrushST()));
              break;
          }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
      });
      */

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
          }
        }
      });

      mRightDrawerView.setAdapter(new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_1, new String[]{"Redo", "Undo"}));
      mRightDrawerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          //TODO Brittle.
          switch (i) {
            case 0:
              if (historyManager.tryRedo()) {
                redraw();
              }
              break;
            case 1:
              if (historyManager.tryUndo()) {
                redraw();
              }
              break;
          }
        }
      });

      mDrawerLayout.addDrawerListener(mDrawerToggle); // Set the drawer toggle as the DrawerListener
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_fullscreen);

    //TODO Do we want this?
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    //initMqtt();

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

    surf.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        //TODO Make SURE this thing gets all the processing it needs.  Do NOT drop points or strokes, so help me.
        float x = event.getX();
        float y = event.getY();
        float p = event.getPressure();
        //System.out.println(event.getActionMasked());

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
            historyManager.getCurStroke().points.add(new StrokePoint(new PointF(x, y), p));
            return true; //TODO SHOULD draw?
          default:
            Log.d(TAG, "Unhandled action: " + event.getActionMasked());
            break;
        }
        redraw();
        return true;
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
    //TODO BACKGROUND background?
    viewport.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);
    ArtContext artContext = new ArtContext();
    // Don't forget; graphics origin is in the top left corner.  ... :/
    int cHPix = 1500; //NOTE Canvas render width
    int cVPix = 900; //NOTE Canvas render height
    //NOTE Canvas target
    artContext.spatialBounds.left = 0;
    artContext.spatialBounds.right = artContext.spatialBounds.left + cHPix;
    artContext.spatialBounds.top = 0;
    artContext.spatialBounds.bottom = artContext.spatialBounds.top + cVPix;
    //TODO Inefficient?  Keep canvas?
    Bitmap bCanvas = Bitmap.createBitmap(cHPix, cVPix, Bitmap.Config.ARGB_8888);

    //Canvas cCanvas = new Canvas(bCanvas);
    //cCanvas.drawARGB(0xFF, 0x00, 0xFF, 0xFF);
    //TODO INEFFICIENT, DON'T KEEP
    UACanvas iCanvas = historyManager.rebuild();
    iCanvas.draw(artContext, bCanvas);

    //TODO Save/keep/etc. matrix
    Matrix viewMatrix = new Matrix();
    //TODO Paint?
    viewport.drawBitmap(bCanvas, viewMatrix, null);
    //c.drawText("" + lastPressure, 10, 10, paint);
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
}
