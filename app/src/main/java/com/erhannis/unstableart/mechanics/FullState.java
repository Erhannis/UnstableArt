package com.erhannis.unstableart.mechanics;

import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.context.Layer;
import com.erhannis.unstableart.mechanics.context.UACanvas;
import com.erhannis.unstableart.mechanics.stroke.Tool;

/**
 * Created by erhannis on 3/23/17.
 */
public class FullState {
  public State state;
  public UACanvas iCanvas;

  public FullState(State state, UACanvas iCanvas) {
    this.state = state;
    this.iCanvas = iCanvas;
  }
}
