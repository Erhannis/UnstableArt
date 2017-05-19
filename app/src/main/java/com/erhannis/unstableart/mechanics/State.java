package com.erhannis.unstableart.mechanics;

import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.layers.Layer;
import com.erhannis.unstableart.mechanics.stroke.Tool;

/**
 * Created by erhannis on 3/23/17.
 */
public class State {
  public static enum CanvasMode {
    FIXED, FOLLOW_VIEWPORT
  }

  public Tool tool;
  public Color color;
  public double size;
  //TODO Note that this may not make sense for non-PaintLayers.
  //TODO Archetype or instantiation?
  // Instance, until proven otherwise
  public Layer iSelectedLayer;
  public CanvasMode canvasMode;
}
