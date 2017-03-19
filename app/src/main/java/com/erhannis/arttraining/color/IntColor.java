package com.erhannis.arttraining.color;

/**
 * ARGB int
 *
 * Created by erhannis on 3/18/17.
 */
public class IntColor extends Color {
  public int value;

  public IntColor(int argb) {
    this.value = argb;
  }

  @Override
  public int getARGBInt() {
    return value;
  }
}
