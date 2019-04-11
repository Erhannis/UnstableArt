package com.erhannis.unstableart;

import android.app.Application;
import android.content.Context;

public class UAApplication extends Application {
  private static Context CONTEXT = null;

  @Override
  public void onCreate() {
    super.onCreate();
    CONTEXT = this.getApplicationContext();
  }

  public static Context getContext() {
    return CONTEXT;
  }
}
