package com.erhannis.unstableart.mechanics.layers.unfinished;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.layers.GroupLayer;
import com.erhannis.unstableart.mechanics.layers.Layer;

/**
 * Created by erhannis on 3/22/17.
 */
public class LightGL extends GroupLayer {
  public LightGL() {
  }

  protected LightGL(String uuid) {
    super(uuid);
  }

  @Override
  public void drawInner(ArtContext artContext, Bitmap canvas) {
    if (iLayers.size() > 0) {
      Bitmap result = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), canvas.getConfig());
      Paint pAdd = new Paint();
      pAdd.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
      Paint pMul = new Paint();
      pMul.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
      Canvas cResult = new Canvas(result);
      //TODO Arguably, we could redraw base each time
      //TODO Arguably, this is a stupid amount of bitmap creation
      Bitmap base = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), canvas.getConfig());
      iLayers.get(0).draw(artContext, base);
      for (int i = 1; i < iLayers.size(); i++) {
        Bitmap cur = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), canvas.getConfig());
        iLayers.get(i).draw(artContext, cur);
        Canvas cCanvas = new Canvas(cur);
        cCanvas.drawBitmap(base, 0, 0, pMul);
        cResult.drawBitmap(cur, 0, 0, pAdd);
      }
      copyOntoWithOpacity(result, canvas);
    }
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }

  @Override
  public LightGL instantiate() {
    return (LightGL) new LightGL(this.getId()).init();
  }

  @Override
  public String getDescription() {
    return "Light Group Layer - Intended to represent the way lighting actually works, more or less.  The first layer is the base color layer.  Consider it to be the color of whatever you're drawing, flatly illuminated by neutral white light.  (I recommend not drawing shadows into it.  Flat colors.)  For each other layer, multiply it with the base and linearly add the result to the output.";
  }
}
