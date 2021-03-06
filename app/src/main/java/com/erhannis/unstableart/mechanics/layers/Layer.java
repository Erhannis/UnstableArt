package com.erhannis.unstableart.mechanics.layers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Xfermode;

import com.erhannis.unstableart.Described;
import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.terlici.dragndroplist.IDd;
import com.terlici.dragndroplist.Visible;

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
public abstract class Layer implements Serializable, IDd<String>, Visible, Described {
  //TODO I feel like this ought to be transient, too, but I'm not sure
  public boolean archetype = true;

  // I have mixed feelings about using UUIDs for identification, buuuuut....
  public final String uuid;

  //TODO Just make a layerState or something?
  public transient boolean visible = true;
  public transient double opacity = 1.0; //TODO Technically initialization

  @Override
  public String getId() {
    return uuid;
  }

  @Override
  public boolean isVisible() {
    return visible;
  }

  public void draw(ArtContext artContext, Bitmap canvas) {
    if (!visible) {
      return;
    }
    drawInner(artContext, canvas);
  }

  /**
   * Here's the recommended idea for this.
   *
   * If not `visible`, do nothing.  Otherwise:
   * A Layer may apply changes to the passed in canvas.  These changes should account for opacity.
   * It may then copy the canvas, and pass the copy to its children, if any.
   * It may then make further changes to the copy, if desired - ignoring opacity.
   * It should then merge the copy onto the passed-in canvas, ACCOUNTING for opacity.
   *
   * @param artContext
   * @param canvas
   */
  public abstract void drawInner(ArtContext artContext, Bitmap canvas);

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

  protected void copyOntoWithOpacityAndXfermode(Bitmap bIn, Bitmap bOut, Xfermode mode) {
    Canvas cCanvas = new Canvas(bOut);
    //TODO Factor out?
    Paint p = new Paint();
    p.setXfermode(mode);
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
