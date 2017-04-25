package com.erhannis.unstableart.mechanics.stroke;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.color.DoublesColor;

/**
 * Created by erhannis on 3/22/17.
 */
public class BrushST extends StrokeTool {
  //TODO Hmm, tranforms?  Color bounds?  Contexts?  Paths?  Base thickness?
  public void apply(Color color, double size, Stroke stroke, Bitmap canvas) {
    Paint paint = new Paint();
    //TODO Set color, alpha, width
    //TODO Should maybe Canvas be passed in?
    Canvas cCanvas = new Canvas(canvas);
    for (int i = 0; i < stroke.points.size() - 1; i++) {
      StrokePoint pa = stroke.points.get(i);
      StrokePoint pb = stroke.points.get(i+1);
      //TODO Inefficient?
      paint.setColor(new DoublesColor(color.getA() * pa.pressure, color.getR(), color.getG(), color.getB()).getARGBInt());
      //TODO Improve
      paint.setStrokeWidth((float)(pa.pressure * size));
      cCanvas.drawLine((float)pa.x, (float)pa.y, (float)pb.x, (float)pb.y, paint);
    }
  }
}
