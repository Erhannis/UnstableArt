package com.erhannis.unstableart.mechanics.stroke;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.context.ArtContext;

/**
 * Created by erhannis on 3/22/17.
 */
public class PenST extends StrokeTool {
  //TODO Hmm, tranforms?  Color bounds?  Contexts?  Paths?  Base thickness?
  public void apply(ArtContext artContext, Color color, double size, Stroke stroke, Bitmap canvas) {
    Paint paint = new Paint();
    //TODO Set color, alpha, width
    // I'm thinking the Pen won't have a size; just a infinithin line.
    paint.setColor(color.getARGBInt());
    //TODO Should maybe Canvas be passed in?
    Canvas cCanvas = new Canvas(canvas);
    cCanvas.concat(artContext.transform);
    for (int i = 0; i < stroke.points.size() - 1; i++) {
      StrokePoint pa = stroke.points.get(i);
      StrokePoint pb = stroke.points.get(i+1);
      cCanvas.drawLine((float)pa.x, (float)pa.y, (float)pb.x, (float)pb.y, paint);
    }
  }
}
