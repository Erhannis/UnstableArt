package com.erhannis.arttraining.mechanics.context;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Let's see; a layer needs...
 * image
 * combining rules
 * ?modifiers?  (opacity)  (could have a special opacity wrapping-layer, but I think the case is too common)
 *
 * Created by erhannis on 3/22/17.
 */
public abstract class Layer {
  public double opacity = 1.0;

  /**
   * Here's the recommended idea for this.
   *
   * A Layer may apply changes to the passed in canvas.  These changes should account for opacity.
   * It may then copy the canvas, and pass the copy to its children, if any.
   * It may then make further changes to the copy, if desired - ignoring opacity.
   * It should then merge the copy onto the passed-in canvas, ACCOUNTING for opacity.
   *
   * @param artContext
   * @param canvas
   */
  public abstract void draw(ArtContext artContext, Bitmap canvas);
}
