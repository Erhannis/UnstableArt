package com.erhannis.unstableart.mechanics.stroke;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.color.ColorUtils;
import com.erhannis.unstableart.mechanics.context.ArtContext;

/**
 * //TODO Note that it ignores xfermode
 * Created by erhannis on 3/22/17.
 */
public class FillST extends StrokeTool {
  //TODO Hmm, tranforms?  Color bounds?  Contexts?  Paths?  Base thickness?
  public void apply(ArtContext artContext, Color color, Xfermode mode, double size, Stroke stroke, Bitmap canvas) {
    //TODO Should maybe Canvas be passed in?
    Canvas cCanvas = new Canvas(canvas);
    cCanvas.drawARGB((int)(color.getA() * 0xFF), (int)(color.getR() * 0xFF), (int)(color.getG() * 0xFF), (int)(color.getB() * 0xFF));
  }
}
