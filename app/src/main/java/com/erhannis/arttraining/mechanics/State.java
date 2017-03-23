package com.erhannis.arttraining.mechanics;

import com.erhannis.arttraining.mechanics.color.Color;
import com.erhannis.arttraining.mechanics.context.Layer;
import com.erhannis.arttraining.mechanics.stroke.Tool;

/**
 * Created by erhannis on 3/23/17.
 */
public class State {
  public Tool tool;
  public Color color;
  //TODO Note that this may not make sense for non-PaintLayers.
  public Layer selectedLayer;
}
