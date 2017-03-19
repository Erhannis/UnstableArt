package com.erhannis.arttraining.color;

import com.erhannis.mathnstuff.MeMath;
import com.erhannis.mathnstuff.MeUtils;

/**
 * Created by erhannis on 3/18/17.
 */
public class DoublesColor extends Color {
  // A, R, G, B; nominally from 0-1, but not necessarily.
  public double[] value;

  public DoublesColor(double a, double r, double g, double b) {
    value = new double[]{a, r, g, b};
  }

  @Override
  public int getARGBInt() {
    //TODO Move to ColorUtils?
    //TODO Account for color context?
    return MeUtils.ARGBToInt(value[0], value[1], value[2], value[3]);
  }
}
