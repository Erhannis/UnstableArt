package com.erhannis.arttraining.mechanics.stroke;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.erhannis.arttraining.mechanics.color.Color;

/**
 * I'm not sure if, e.g., "bucket" is a Tool, but just in case, I'm splitting this up.
 *
 * Created by erhannis on 3/22/17.
 */
public abstract class StrokeTool extends Tool {
  //TODO Canvas instead of Bitmap?
  public abstract void apply(Color color, Stroke stroke, Bitmap canvas);
}
