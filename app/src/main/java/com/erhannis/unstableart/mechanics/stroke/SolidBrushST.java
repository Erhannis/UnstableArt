package com.erhannis.unstableart.mechanics.stroke;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Xfermode;

import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.color.DoublesColor;
import com.erhannis.unstableart.mechanics.context.ArtContext;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by erhannis on 3/22/17.
 */
public class SolidBrushST extends StrokeTool {
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

    StrokePoint lastPoint = null;
    ArrayList<StrokePoint> points = new ArrayList<>(stroke.points.size());
    for (StrokePoint p : stroke.points) {
      if (!p.equivalent(lastPoint)) {
        points.add(p);
      }
      lastPoint = p;
    }

    //TODO Reduce code copying
    if (points.size() == 0) {

    } else if (points.size() == 1) {

    } else if (points.size() == 2) {
      int end = points.size() - 1;
      StrokePoint p0 = points.get(0);
      StrokePoint p1 = points.get(1);
      double s1x, s1y, s2x, s2y;
      double s3x, s3y, s4x, s4y;

      double[] n = getNorm(p0, p1);
      double[] n0 = scaleIp(copy(n), p0.pressure * size);
      s1x = p0.x + n0[0];
      s1y = p0.y + n0[1];
      s2x = p0.x - n0[0];
      s2y = p0.y - n0[1];
      double[] n1 = scaleIp(n, p1.pressure * size);
      s3x = p1.x - n1[0];
      s3y = p1.y - n1[1];
      s4x = p1.x + n1[0];
      s4y = p1.y + n1[1];

      Path path = new Path();
      path.moveTo((float) s1x, (float) s1y);
      path.lineTo((float) s2x, (float) s2y);
      path.lineTo((float) s3x, (float) s3y);
      path.lineTo((float) s4x, (float) s4y);
      path.close();

      paint.setColor(new DoublesColor(color.getA() * p0.pressure, color.getR(), color.getG(), color.getB()).getARGBInt());

      cCanvas.drawPath(path, paint);
    } else {
      size = size / 2;

//      Paint debug = new Paint();
//      debug.setColor(0xFF000000);
//      debug.setStrokeWidth(0);
//
//      Paint debug2 = new Paint();
//      debug2.setColor(0xFFFF0000);
//      debug2.setStrokeWidth(0);

      //// First and middle segments
      for (int i = 0; i < points.size() - 2; i++) {
        StrokePoint pz = null;
        if (i > 0) {
          pz = points.get(i - 1);
        }
        StrokePoint p0 = points.get(i);
        StrokePoint p1 = points.get(i + 1);
        StrokePoint p2 = points.get(i + 2);
        double s1x, s1y, s2x, s2y;
        double s3x, s3y, s4x, s4y;

        if (i == 0) {
          double[] n0 = scaleIp(getNorm(p0, p1), p0.pressure * size);
          s1x = p0.x - n0[0];
          s1y = p0.y - n0[1];
          s2x = p0.x + n0[0];
          s2y = p0.y + n0[1];
          //cCanvas.drawLines(concat(s1x, s1y, s2x, s2y), debug2);
          //cCanvas.drawLines(concat(p0.x, p0.y, p1.x, p1.y), debug);
        } else {
          double[] n0 = scaleIp(getMiddleNorm(pz, p0, p1), p0.pressure * size);
          s1x = p0.x + n0[0];
          s1y = p0.y + n0[1];
          s2x = p0.x - n0[0];
          s2y = p0.y - n0[1];
        }
        double[] n1 = scaleIp(getMiddleNorm(p0, p1, p2), p1.pressure * size);
        s3x = p1.x - n1[0];
        s3y = p1.y - n1[1];
        s4x = p1.x + n1[0];
        s4y = p1.y + n1[1];

        Path path = new Path();
        path.moveTo((float) s1x, (float) s1y);
        path.lineTo((float) s2x, (float) s2y);
        path.lineTo((float) s3x, (float) s3y);
        path.lineTo((float) s4x, (float) s4y);
        path.close();

        paint.setColor(new DoublesColor(color.getA() * p0.pressure, color.getR(), color.getG(), color.getB()).getARGBInt());

        cCanvas.drawPath(path, paint);

//        cCanvas.drawLines(concat(p0.x, p0.y, p1.x, p1.y, s1x, s1y, s2x, s2y, s2x, s2y, s3x, s3y, s3x, s3y, s4x, s4y), debug);
      }

      //// Last segment
      int end = points.size() - 1;
      StrokePoint p0 = points.get(end - 2);
      StrokePoint p1 = points.get(end - 1);
      StrokePoint p2 = points.get(end);
      double s1x, s1y, s2x, s2y;
      double s3x, s3y, s4x, s4y;

      double[] n0 = scaleIp(getMiddleNorm(p0, p1, p2), p1.pressure * size);
      s1x = p1.x + n0[0];
      s1y = p1.y + n0[1];
      s2x = p1.x - n0[0];
      s2y = p1.y - n0[1];
      double[] n1 = scaleIp(getNorm(p1, p2), p2.pressure * size);
      s3x = p2.x + n1[0];
      s3y = p2.y + n1[1];
      s4x = p2.x - n1[0];
      s4y = p2.y - n1[1];

      Path path = new Path();
      path.moveTo((float) s1x, (float) s1y);
      path.lineTo((float) s2x, (float) s2y);
      path.lineTo((float) s3x, (float) s3y);
      path.lineTo((float) s4x, (float) s4y);
      path.close();

      paint.setColor(new DoublesColor(color.getA() * p0.pressure, color.getR(), color.getG(), color.getB()).getARGBInt());

      cCanvas.drawPath(path, paint);
    }
  }

  private static float[] concat(Object... coords) {
    ArrayList<Float> floats = new ArrayList<>();
    for (Object o : coords) {
      if (o instanceof Number) {
        floats.add(((Number)o).floatValue());
      }
      if (o.getClass().isArray()) {
        if (o instanceof double[]) {
          for (double d : ((double[])o)) {
            floats.add((float)d);
          }
        } else if (o instanceof float[]) {
          for (float f : ((float[])o)) {
            floats.add(f);
          }
        } else {

        }
      }
    }
    float[] result = new float[floats.size()];
    for (int i = 0; i < floats.size(); i++) {
      result[i] = floats.get(i);
    }
    return result;
  }

  private static double[] copy(double[] a) {
    return Arrays.copyOf(a, a.length);
  }

  /**
   * Returns a normal to the line from p0 to p1.  May not be the normal you expect.
   * If p0 is on the right and p1 is on the left, the returned normal points down.
   * It is also normalized.
   * @param p0
   * @param p1
   * @return
   */
  private static double[] getNorm(StrokePoint p0, StrokePoint p1) {
    double dx = p1.x - p0.x;
    double dy = p1.y - p0.y;
    if (dx == 0 && dy == 0) {
      return new double[]{0,0}; //TODO Error?
    }
    double nx = -dy;
    double ny = dx;
    double mag = Math.sqrt((nx * nx) + (ny * ny));
    return new double[]{nx / mag, ny / mag};
  }

  private static double[] scaleIp(double[] v, double s) {
    v[0] *= s;
    v[1] *= s;
    return v;
  }

  /**
   * Norm at a corner, kinda.  If p0 is on the right, p1 on the left, and p2 above p1, then
   * the returned vector points down and left.  It is normalized.
   * @param p0
   * @param p1
   * @param p2
   * @return
   */
  private static double[] getMiddleNorm(StrokePoint p0, StrokePoint p1, StrokePoint p2) {
    double[] ban = normalizeIp(new double[]{p0.x - p1.x, p0.y - p1.y});
    double[] bcn = normalizeIp(new double[]{p2.x - p1.x, p2.y - p1.y});
    if (ban[0] == -bcn[0] && ban[1] == -bcn[1]) {
      return getNorm(p0, p1);
    }
    if (!isSmallAngle(p0, p1, p2)) {
      return scaleIp(normalizeIp(addIp(ban, bcn)), -1);
    } else {
      return normalizeIp(addIp(ban, bcn));
    }
  }

  /*
  private static float[] getMiddleNormDebug(StrokePoint p0, StrokePoint p1, StrokePoint p2) {
    float[] result = new float[0];
    double[] ban = normalizeIp(new double[]{p0.x - p1.x, p0.y - p1.y});
    double[] bcn = normalizeIp(new double[]{p2.x - p1.x, p2.y - p1.y});
    if (ban[0] == -bcn[0] && ban[1] == -bcn[1]) {
      return getNorm(p0, p1);
    }
    if (!isSmallAngle(p0, p1, p2)) {
      return scaleIp(normalizeIp(addIp(ban, bcn)), -1);
    } else {
      return normalizeIp(addIp(ban, bcn));
    }
  }
  */

  private static double[] addIp(double[] a, double[] b) {
    a[0] += b[0];
    a[1] += b[1];
    return a;
  }

  private static double[] normalizeIp(double[] v) {
    if (v[0] == 0 && v[1] == 0) {
      return v;
    }
    double mag = Math.sqrt((v[0] * v[0]) + (v[1] * v[1]));
    v[0] /= mag;
    v[1] /= mag;
    return v;
  }

  private static boolean isSmallAngle(StrokePoint p0, StrokePoint p1, StrokePoint p2) {
    double bax = p0.x - p1.x;
    double bay = p0.y - p1.y;
    if (bax == 0 && bay == 0) {
      return true; //TODO Error?
    }
    double cbx = p2.x - p1.x;
    double cby = p2.y - p1.y;
    if (cbx == 0 && cby == 0) {
      return true; //TODO Error?
    }
    double nx = -bay;
    double ny = bax;
    //TODO Might be backwards
    return ((nx * cbx) + (ny * cby) > 0);
  }

  /*
  private Path makePath(StrokePoint pa, StrokePoint pb, StrokePoint pc, double size) {
    double wa = pa.pressure * size;
    double wb = pb.pressure * size;
    double wc = pc.pressure * size;

    Path p = new Path();

  }
  */
}
