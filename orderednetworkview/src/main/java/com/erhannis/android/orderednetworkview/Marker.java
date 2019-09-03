package com.erhannis.android.orderednetworkview;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

public class Marker {
  public final Drawable icon;
  public final String name;

  public Marker(Drawable icon, String name) {
    this.icon = icon;
    this.name = name;
  }
}
