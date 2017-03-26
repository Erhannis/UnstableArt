package com.erhannis.arttraining.mechanics.context;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.io.Serializable;

/**
 * Let's see; a layer needs...
 * image
 * combining rules
 * ?modifiers?  (opacity)  (could have a special opacity wrapping-layer, but I think the case is too common)
 *
 * Any variables these have should be made transient; changes post-`instantiate` are the
 * responsibility of the history nodes representing that setting happening.
 *
 * Created by erhannis on 3/22/17.
 */
public abstract class Layer implements Serializable {
  //TODO I feel like this ought to be transient, too, but I'm not sure
  public boolean archetype = true;

  public transient double opacity = 1.0; //TODO Technically initialization

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

  /**
   * Instances of this (via `instantiate`) ought to call super.init()
   * Then init the things pertaining to that specific subclass
   * And finally return `this`
   *
   * @return
   */
  protected Layer init() {
    archetype = false;
    return this;
  }

  // I strongly suggest this be written: return (LayerSubclass)new LayerSubclass().init();
  public abstract Layer instantiate();
}
