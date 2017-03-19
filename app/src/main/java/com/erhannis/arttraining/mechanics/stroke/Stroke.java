package com.erhannis.arttraining.mechanics.stroke;

import java.util.ArrayList;

/**
 * //TODO How many properties ought to be on this object, how many in global state, and how many on points?
 *
 * Created by erhannis on 3/18/17.
 */
public class Stroke {
  //TODO Make these final and immutable?
  public Tool tool;
  public ArrayList<StrokePoint> points = new ArrayList<>();
}
