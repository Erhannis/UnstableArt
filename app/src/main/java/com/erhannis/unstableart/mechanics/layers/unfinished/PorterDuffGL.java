package com.erhannis.unstableart.mechanics.layers.unfinished;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuffXfermode;

import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.layers.GroupLayer;
import com.erhannis.unstableart.mechanics.layers.Layer;

/**
 * Created by erhannis on 3/22/17.
 */
public class PorterDuffGL extends GroupLayer {
  //TODO What if they want to change it?
  public final PorterDuffXfermode mode;

  private PorterDuffGL() {
    mode = null;
  }

  public PorterDuffGL(PorterDuffXfermode mode) {
    this.mode = mode;
  }

  protected PorterDuffGL(String uuid, PorterDuffXfermode mode) {
    super(uuid);
    this.mode = mode;
  }

  @Override
  public void drawInner(ArtContext artContext, Bitmap canvas) {
    Bitmap blank = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), canvas.getConfig());
    for (Layer iLayer : iLayers) {
      iLayer.draw(artContext, blank);
    }
    copyOntoWithOpacityAndXfermode(blank, canvas, mode);
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }

  @Override
  public PorterDuffGL instantiate() {
    return (PorterDuffGL) new PorterDuffGL(this.getId(), mode).init();
  }

  @Override
  public String getDescription() {
    return "PorterDuff Group Layer - draws the contained layers onto the canvas using a given PorterDuff mode.  Currently no given way to specify the mode, so it's unfinished.";
  }
}
