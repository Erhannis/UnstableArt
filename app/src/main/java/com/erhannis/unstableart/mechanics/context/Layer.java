package com.erhannis.unstableart.mechanics.context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.terlici.dragndroplist.IDd;

import java.io.Serializable;
import java.util.UUID;

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
public abstract class Layer implements Serializable, IDd<String> {
  //TODO I feel like this ought to be transient, too, but I'm not sure
  public boolean archetype = true;

  // I have mixed feelings about using UUIDs for identification, buuuuut....
  public final String uuid;

  public transient double opacity = 1.0; //TODO Technically initialization

  public String getId() {
    return uuid;
  }

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

  public Layer() {
    this.uuid = UUID.randomUUID().toString();
  }

  protected Layer(String uuid) {
    this.uuid = uuid;
  }

  protected void copyOntoWithOpacity(Bitmap bIn, Bitmap bOut) {
    Canvas cCanvas = new Canvas(bOut);
    //TODO Factor out?
    Paint p = new Paint();
    //TODO Clamp?
    p.setAlpha((int)(opacity * 255)); //LOSS
    cCanvas.drawBitmap(bIn, 0, 0, p);
  }

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

  // I strongly suggest this be written: return (LayerSubclass)new LayerSubclass(this.getId()).init();
  public abstract Layer instantiate();

  public String toString() {
    //TODO Consider
    return this.getClass().getSimpleName() + ":" + uuid.subSequence(0, 8);
  }
}
