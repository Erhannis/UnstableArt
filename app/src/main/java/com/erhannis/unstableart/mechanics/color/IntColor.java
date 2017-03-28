package com.erhannis.unstableart.mechanics.color;

import com.erhannis.mathnstuff.MeUtils;

/**
 * ARGB int
 *
 * Created by erhannis on 3/18/17.
 */
public class IntColor extends Color {
  public final int value;

  public IntColor(int argb) {
    this.value = argb;
  }

  @Override
  public int getARGBInt() {
    return value;
  }

  @Override
  public double getA() {
    return MeUtils.intToARGB(value)[0] / 255.0;
  }

  @Override
  public double getR() {
    return MeUtils.intToARGB(value)[1] / 255.0;
  }

  @Override
  public double getG() {
    return MeUtils.intToARGB(value)[2] / 255.0;
  }

  @Override
  public double getB() {
    return MeUtils.intToARGB(value)[3] / 255.0;
  }
}
