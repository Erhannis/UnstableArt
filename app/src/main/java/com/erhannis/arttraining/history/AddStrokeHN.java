package com.erhannis.arttraining.history;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.erhannis.arttraining.mechanics.color.Color;
import com.erhannis.arttraining.mechanics.color.DoublesColor;
import com.erhannis.arttraining.mechanics.context.ArtContext;
import com.erhannis.arttraining.mechanics.stroke.Stroke;
import com.erhannis.arttraining.mechanics.stroke.StrokePoint;

/**
 * Created by erhannis on 3/18/17.
 */
public class AddStrokeHN extends HistoryNode {
  public final Stroke stroke;

  public AddStrokeHN(Stroke stroke) {
    this.stroke = stroke;
  }

  @Override
  public void draw(ArtContext artContext, Canvas canvas) {
    parent.draw(artContext, canvas);
    //TODO TODO Do better; color, tool
    Paint paint = new Paint();
    DoublesColor baseColor = new DoublesColor(1, 0, 0, 0);
    for (int i = 0; i < stroke.points.size() - 1; i++) {
      StrokePoint pa = stroke.points.get(i);
      StrokePoint pb = stroke.points.get(i+1);
      //TODO Set color, alpha, width
      //TODO Inefficient?
      paint.setColor(new DoublesColor(baseColor.value[0] * pa.pressure, baseColor.value[1], baseColor.value[2], baseColor.value[3]).getARGBInt());
      canvas.drawLine(pa.pos.x, pa.pos.y, pb.pos.x, pb.pos.y, paint);
    }
  }
}
