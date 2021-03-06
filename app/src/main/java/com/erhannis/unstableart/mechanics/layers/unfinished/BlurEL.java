package com.erhannis.unstableart.mechanics.layers.unfinished;

import android.graphics.Bitmap;

import com.erhannis.unstableart.mechanics.color.ColorUtils;
import com.erhannis.unstableart.mechanics.color.DoublesColor;
import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.layers.Layer;

/**
 * Vaguely works.  Not very well, not very fast.  On hold.
 *
 * Created by erhannis on 3/22/17.
 */
public class BlurEL extends EffectLayer {
  //TODO Settings, write and allow change
  //TODO Double?
  public transient int radius;

  public BlurEL() {
  }

  protected BlurEL(String uuid) {
    super(uuid);
  }

  @Override
  public void drawInner(ArtContext artContext, Bitmap bCanvas) {
    Bitmap bCanvas2 = Bitmap.createBitmap(bCanvas.getWidth(), bCanvas.getHeight(), bCanvas.getConfig());
    //TODO Figure out scaling
    for (int x = 0; x < bCanvas.getWidth(); x++) {
      for (int y = 0; y < bCanvas.getHeight(); y++) {
        int count = 0;
        double a = 0;
        double r = 0;
        double g = 0;
        double b = 0;
        //TODO Ends up with too much black
        for (int x2 = Math.max(0, x - radius); x2 < Math.min(bCanvas.getWidth(), x + radius); x2++) {
          for (int y2 = Math.max(0, y - radius); y2 < Math.min(bCanvas.getHeight(), y + radius); y2++) {
            count++;
            int iColor = bCanvas.getPixel(x2, y2); //LOSS?
            double[] dColor = ColorUtils.intARGBToDoublesARGB(iColor);
            a += dColor[0];
            r += dColor[1];
            g += dColor[2];
            b += dColor[3];
          }
        }
        a /= count;
        r /= count;
        g /= count;
        b /= count;
        DoublesColor newColor = new DoublesColor(a, r, g, b);
        bCanvas2.setPixel(x, y, newColor.getARGBInt()); //LOSS
      }
    }
    //TODO Wrong
    bCanvas.eraseColor(0x00000000);
    copyOntoWithOpacity(bCanvas2, bCanvas);
  }

  @Override
  protected Layer init() {
    super.init();
    //TODO Allow change
    radius = 4;
    return this;
  }

  @Override
  public BlurEL instantiate() {
    return (BlurEL)new BlurEL(this.getId()).init();
  }

  @Override
  public String getDescription() {
    return "Blur Effect Layer - Unfinished.  Intended to...blur the layers beneath, I think?";
  }
}
