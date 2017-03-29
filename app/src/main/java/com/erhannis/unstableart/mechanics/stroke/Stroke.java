package com.erhannis.unstableart.mechanics.stroke;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * //TODO How many properties ought to be on this object, how many in global state, and how many on points?
 *
 * Created by erhannis on 3/18/17.
 */
public class Stroke implements Serializable {
  //TODO Make these final and immutable?
  public ArrayList<StrokePoint> points = new ArrayList<>();
}
