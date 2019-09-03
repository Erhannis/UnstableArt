package com.erhannis.android.orderednetworkview;

import android.graphics.drawable.Drawable;

public abstract class DrawableNode<T extends DrawableNode> extends Node<T> {
  //TODO It'd be nice if this were not Android specific
  /**
   * Preferred form:
   * 100x100, vector.  Icon centered in a circle 100 diameter.
   * Black and white, because it'll probably get tinted.
   * @return
   */
  public abstract Drawable getDrawable();
}
