package com.erhannis.arttraining.mechanics.stroke;

import android.graphics.PointF;

import com.erhannis.arttraining.mechanics.color.Color;

/**
 * //TODO Color?
 *
 * Created by erhannis on 3/18/17.
 */
public class StrokePoint {
  public PointF pos;
  public float pressure;

  public StrokePoint(PointF pos, float pressure) {
    this.pos = pos;
    this.pressure = pressure;
  }
}
