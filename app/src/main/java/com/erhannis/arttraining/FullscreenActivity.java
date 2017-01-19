package com.erhannis.arttraining;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
  /**
   * Whether or not the system UI should be auto-hidden after
   * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
   */
  private static final boolean AUTO_HIDE = true;

  /**
   * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
   * user interaction before hiding the system UI.
   */
  private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

  /**
   * Some older devices needs a small delay between UI widget updates
   * and a change of the status and navigation bar.
   */
  private static final int UI_ANIMATION_DELAY = 300;

  private SurfaceView surf;

  private ArrayList<ArrayList<PointF>> lines = new ArrayList<ArrayList<PointF>>();
  private ArrayList<ArrayList<PointF>> hulls = new ArrayList<ArrayList<PointF>>();
  private ArrayList<float[]> circles = new ArrayList<float[]>();
  private float lastScore = 0;

  private final Handler mHideHandler = new Handler();
  private final Runnable mHidePart2Runnable = new Runnable() {
    @SuppressLint("InlinedApi")
    @Override
    public void run() {
      // Delayed removal of status and navigation bar

      // Note that some of these constants are new as of API 16 (Jelly Bean)
      // and API 19 (KitKat). It is safe to use them, as they are inlined
      // at compile-time and do nothing on earlier devices.
//      mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//              | View.SYSTEM_UI_FLAG_FULLSCREEN
//              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
            lines.add(new ArrayList<PointF>());
            break;
          case MotionEvent.ACTION_CANCEL:
          case MotionEvent.ACTION_UP:
            if (lines.size() > 0) {
              checkCircle(lines.get(lines.size() - 1));
              hulls.add(new ArrayList<PointF>(MiscUtils.convexHullGrahamScan(lines.get(lines.size() - 1))));
            }
            break;
        }
        if (lines.size() == 0) {
          return true;
        }
        ArrayList<PointF> line = lines.get(lines.size() - 1);
        line.add(new PointF(x, y));

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

  private void checkCircle(ArrayList<PointF> line) {
    PointF center = MiscUtils.findCircleCenter(line);
    float radius = MiscUtils.findCircleRadius(line, center);
    float[] circle = MiscUtils.makeCircle(center, radius);
    circles.add(circle);
    float score = MiscUtils.rateCircle(line, circle);
    System.out.println("score " + score);
    lastScore = score;
  }

  private void drawCanvas(Canvas c) {
    c.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);
    Paint paint = new Paint();
    for (ArrayList<PointF> line : lines) {
      for (int i = 0; i < line.size() - 1; i++) {
        PointF a = line.get(i);
        PointF b = line.get(i+1);
        c.drawLine(a.x, a.y, b.x, b.y, paint);
      }
    }

    paint.setColor(0xFF00FF00);
    for (ArrayList<PointF> line : hulls) {
      for (int i = 0; i < line.size() - 1; i++) {
        PointF a = line.get(i);
        PointF b = line.get(i+1);
        c.drawLine(a.x, a.y, b.x, b.y, paint);
      }
    }

    paint.setColor(0xFF0000FF);
    paint.setStyle(Paint.Style.STROKE);

    for (float[] circle : circles) {
      c.drawCircle(circle[0], circle[1], circle[2], paint);
    }

    c.drawText("" + lastScore, 10, 10, paint);
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
