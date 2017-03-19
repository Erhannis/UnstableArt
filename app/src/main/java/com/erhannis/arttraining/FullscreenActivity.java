package com.erhannis.arttraining;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.input.InputManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.erhannis.arttraining.color.Color;
import com.erhannis.arttraining.color.DoublesColor;
import com.erhannis.arttraining.color.IntColor;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
  public static class StrokePoint {
    public PointF pos;
    public float pressure;
    public Color color;

    public StrokePoint(PointF pos, float pressure, Color color) {
      this.pos = pos;
      this.pressure = pressure;
      this.color = color;
    }
  }

  private static final boolean AUTO_HIDE = true;
  private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
  private static final int UI_ANIMATION_DELAY = 300;

  MqttAndroidClient mqttAndroidClient;

  final String serverUri = "tcp://192.168.0.6:1883";

  final String clientId = "ExampleAndroidClient";
  final String subscriptionTopic = "topic1";
  final String publishTopic = "topic2";
  final String publishMessage = "blah blah blah";
  private static final String COLOR_SPLINE_TOPIC = "color_spline";
  private static final String COLOR_VALUE_TOPIC = "color_value";

  private SurfaceView surf;

  private ArrayList<ArrayList<StrokePoint>> lines = new ArrayList<ArrayList<StrokePoint>>();
  private Color curColor = new DoublesColor(1, 0, 0, 0);

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
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_fullscreen);

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

    final SurfaceHolder sh = surf.getHolder();

    surf.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float p = event.getPressure();
        //System.out.println(event.getActionMasked());

        switch (event.getActionMasked()) {
          case MotionEvent.ACTION_DOWN:
            lines.add(new ArrayList<StrokePoint>());
            break;
          case MotionEvent.ACTION_CANCEL:
          case MotionEvent.ACTION_UP:
            if (lines.size() > 0) {
            }
            break;
        }
        if (lines.size() == 0) {
          return true;
        }
        ArrayList<StrokePoint> line = lines.get(lines.size() - 1);
        line.add(new StrokePoint(new PointF(x, y), p, curColor));

        Canvas c = sh.lockCanvas();
        if (c != null) {
          drawCanvas(c);
          sh.unlockCanvasAndPost(c);
        }

        return true;
      }
    });

    mVisible = true;

    //toggle();
  }


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
        curColor = new IntColor((int)Long.parseLong(payload));
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


  private void drawCanvas(Canvas c) {
    c.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);
    Paint paint = new Paint();
    float lastPressure = 0;
    for (ArrayList<StrokePoint> line : lines) {
      for (int i = 0; i < line.size() - 1; i++) {
        StrokePoint a = line.get(i);
        StrokePoint b = line.get(i+1);
        paint.setColor(a.color.getARGBInt());
        lastPressure = b.pressure;
        //TODO Set size
        c.drawLine(a.pos.x, a.pos.y, b.pos.x, b.pos.y, paint);
      }
    }

    c.drawText("" + lastPressure, 10, 10, paint);
  }



  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    // Trigger the initial hide() shortly after the activity has been
    // created, to briefly hint to the user that UI controls
    // are available.
    delayedHide(100);
  }

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
}
