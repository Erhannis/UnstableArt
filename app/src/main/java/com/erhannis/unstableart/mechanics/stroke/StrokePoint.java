package com.erhannis.unstableart.mechanics.stroke;

import android.graphics.PointF;

import java.io.Serializable;

/**
 * //TODO Color?
 *
 * Created by erhannis on 3/18/17.
 */
public class StrokePoint implements Serializable {
  //TODO Could have a `landmarkX/Y`, then `localX/Y`, for super detail
  public double x;
  public double y;
  public float pressure;

  public StrokePoint() {
  }

  public StrokePoint(double x, double y, float pressure) {
    this.x = x;
    this.y = y;
    this.pressure = pressure;
  }
}
