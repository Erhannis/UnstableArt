package com.erhannis.unstableart.mechanics.color;

import java.io.Serializable;

/**
 * Created by erhannis on 3/18/17.
 */
public abstract class Color implements Serializable {
  //TODO Interface?

  public abstract int getARGBInt();
  public abstract double getA();
  public abstract double getR();
  public abstract double getG();
  public abstract double getB();
}
