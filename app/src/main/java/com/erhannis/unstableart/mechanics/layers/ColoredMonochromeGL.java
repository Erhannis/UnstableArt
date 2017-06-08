package com.erhannis.unstableart.mechanics.layers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.erhannis.unstableart.mechanics.context.ArtContext;

/**
 * Created by erhannis on 5/19/17.
 */
public class ColoredMonochromeGL extends GroupLayer {
  public ColoredMonochromeGL() {
  }

  protected ColoredMonochromeGL(String uuid) {
    super(uuid);
  }

  @Override
  public void drawInner(ArtContext artContext, Bitmap canvas) {
    Bitmap base = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), canvas.getConfig());
    Bitmap mult = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), canvas.getConfig());
    if (iLayers.size() >= 1) {
      // Base
      iLayers.get(0).draw(artContext, base);
    }
    if (iLayers.size() >= 2) {
      // Mult
      iLayers.get(1).draw(artContext, mult);
    }
    Canvas cCanvas = new Canvas(base);
    Paint p = new Paint();
    p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
    cCanvas.drawBitmap(mult, 0, 0, p);
    copyOntoWithOpacity(base, canvas);
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }

  @Override
  public ColoredMonochromeGL instantiate() {
    return (ColoredMonochromeGL) new ColoredMonochromeGL(this.getId()).init();
  }

  @Override
  public String getDescription() {
    return "Colored Monochrome Group Layer - Expects two layers.  The first is the base (expected to be monochrome), against which the second (recommended to be flat white with fully saturated hues alpha'd on) is multiplied, and the result is painted onto the canvas.";
  }
}
