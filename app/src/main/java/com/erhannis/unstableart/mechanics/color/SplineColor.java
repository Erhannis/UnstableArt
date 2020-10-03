package com.erhannis.unstableart.mechanics.color;

import com.erhannis.mathnstuff.MeUtils;
import com.erhannis.mathnstuff.splines.ColorSpline;

public class SplineColor extends Color {
  // A, R, G, B; nominally from 0-1, but not necessarily.  Should be immutable, probably.
  private final ColorSpline spline;
  public final double t;

  private SplineColor() {
    spline = null;
    t = 0;
  }

  public SplineColor(ColorSpline spline, double t) { //TODO `spline` should be immutable....
    this.spline = spline;
    this.t = t;
  }

  @Override
  public int getARGBInt() {
    //TODO Move to ColorUtils?
    //TODO Account for color context?
    return spline.InterpolateToARGB(t); //LOSS ??
  }

  @Override
  public double getA() {
    return spline.InterpolateToDARGB(t)[0];
  }

  @Override
  public double getR() {
    return spline.InterpolateToDARGB(t)[1];
  }

  @Override
  public double getG() {
    return spline.InterpolateToDARGB(t)[2];
  }

  @Override
  public double getB() {
    return spline.InterpolateToDARGB(t)[3];
  }
}
