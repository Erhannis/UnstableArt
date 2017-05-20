package com.erhannis.unstableart.mechanics.layers.unfinished;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.layers.GroupLayer;
import com.erhannis.unstableart.mechanics.layers.Layer;

import java.util.HashMap;

/**
 * Created by erhannis on 5/19/17.
 */
public class HSVGL extends GroupLayer {
  public HSVGL() {
  }

  protected HSVGL(String uuid) {
    super(uuid);
  }

  @Override
  public void drawInner(ArtContext artContext, Bitmap canvas) {
    //TODO Hopefully we'll be able to combine some of these and not clog up memory.
    //TODO Do some testing and find out how many such bitmaps we can have at once, anyway.
    Bitmap blank = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), canvas.getConfig());
    Bitmap h = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), canvas.getConfig());
    Bitmap s = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), canvas.getConfig());
    Bitmap v = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), canvas.getConfig());
    if (iLayers.size() >= 1) {
      // Hue
      iLayers.get(0).draw(artContext, h);
      //setHue(blank, h);
    }
    if (iLayers.size() >= 2) {
      // Saturation
      iLayers.get(1).draw(artContext, s);
      //setSat(blank, s);
    }
    if (iLayers.size() >= 3) {
      // Value
      iLayers.get(2).draw(artContext, v);
      //setVal(blank, v);
    }
    copyOntoWithOpacity(blank, canvas);
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }

  @Override
  public HSVGL instantiate() {
    return (HSVGL) new HSVGL(this.getId()).init();
  }

  @Override
  public String getDescription() {
    return "Hue Saturation Value Group Layer - Expects three layers.  The first will provide the hue of the result, the second the saturation, the third the value.";
  }
}
