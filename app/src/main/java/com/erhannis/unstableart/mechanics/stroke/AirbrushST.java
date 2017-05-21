package com.erhannis.unstableart.mechanics.stroke;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Xfermode;

import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.color.DoublesColor;
import com.erhannis.unstableart.mechanics.context.ArtContext;

/**
 * Created by erhannis on 5/20/17.
 */
public class AirbrushST extends StrokeTool {
  //TODO Hmm, tranforms?  Color bounds?  Contexts?  Paths?  Base thickness?
  public void apply(ArtContext artContext, Color color, Xfermode mode, double size, Stroke stroke, Bitmap canvas) {
    Paint paint = new Paint();
    if (mode != null) {
      paint.setXfermode(mode);
    }
    //TODO Set color, alpha, width
    //TODO Should maybe Canvas be passed in?
    Canvas cCanvas = new Canvas(canvas);
    cCanvas.concat(artContext.transform);
    for (int i = 0; i < stroke.points.size(); i++) {
      StrokePoint p = stroke.points.get(i);
      //TODO Inefficient?
      //TODO Reduce alpha for airbrush?
      paint.setColor(new DoublesColor(color.getA() * p.pressure, color.getR(), color.getG(), color.getB()).getARGBInt());
      //TODO Improve
      cCanvas.drawCircle((float)p.x, (float)p.y, (float)(p.pressure * size), paint);
    }
  }
}
