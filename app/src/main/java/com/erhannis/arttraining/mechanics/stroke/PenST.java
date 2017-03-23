package com.erhannis.arttraining.mechanics.stroke;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.erhannis.arttraining.mechanics.color.Color;
import com.erhannis.arttraining.mechanics.color.DoublesColor;

/**
 * Created by erhannis on 3/22/17.
 */
public class PenST extends StrokeTool {
  //TODO Hmm, tranforms?  Color bounds?  Contexts?
  public void apply(Color color, Stroke stroke, Bitmap canvas) {
    Paint paint = new Paint();
    //TODO Set color, alpha, width
    DoublesColor baseColor = new DoublesColor(1, 0, 0, 0);
    //TODO Should maybe Canvas be passed in?
    Canvas cCanvas = new Canvas(canvas);
    for (int i = 0; i < stroke.points.size() - 1; i++) {
      StrokePoint pa = stroke.points.get(i);
      StrokePoint pb = stroke.points.get(i+1);
      //TODO Inefficient?
      paint.setColor(new DoublesColor(baseColor.value[0] * pa.pressure, baseColor.value[1], baseColor.value[2], baseColor.value[3]).getARGBInt());
      cCanvas.drawLine(pa.pos.x, pa.pos.y, pb.pos.x, pb.pos.y, paint);
    }
  }
}
