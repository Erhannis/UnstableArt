package com.erhannis.unstableart.mechanics.stroke;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.color.DoublesColor;
import com.erhannis.unstableart.mechanics.context.ArtContext;

/**
 * Created by erhannis on 3/22/17.
 */
public class ColorPickerST extends StrokeTool {
  public void apply(ArtContext artContext, Color color, double size, Stroke stroke, Bitmap canvas) {
    if (stroke.points.size() > 0) {
      StrokePoint pt = stroke.points.get(stroke.points.size() - 1);
      Matrix tf = new Matrix();
      tf.invert(artContext.transform);
      try {
        float[] v = {(float)pt.x, (float)pt.y};
        tf.mapPoints(v);
        int newColor = canvas.getPixel((int)v[0], (int)v[1]);

      } catch (IllegalArgumentException e) {
      }
    }
  }
}
