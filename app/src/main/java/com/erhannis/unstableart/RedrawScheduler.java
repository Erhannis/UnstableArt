package com.erhannis.unstableart;

/**
 * Created by erhannis on 5/5/17.
 */

public class RedrawScheduler {
  protected Runnable mCallback;

  protected final Object wakeupLock = new Object();
  protected boolean mRedrawScheduled = false;

  public RedrawScheduler(Runnable callback) {
    this.mCallback = callback;

    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          while (!mRedrawScheduled) {
            synchronized (wakeupLock) {
              try {
                wakeupLock.wait();
              } catch (InterruptedException e) {
                System.err.println("Redraw thread interrupted");
                return;
              }
            }
          }
          mRedrawScheduled = false;
          try {
            callback.run();
          } catch (Throwable t) {
            //TODO Maybe exit?
            t.printStackTrace();
          }
        }
      }
    });
    t.setDaemon(true);
    t.start();
  }

  public void scheduleRedraw() {
    synchronized (wakeupLock) {
      mRedrawScheduled = true;
      wakeupLock.notify();
    }
  }
}