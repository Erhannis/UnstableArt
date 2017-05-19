package com.erhannis.unstableart.mechanics;

import com.erhannis.unstableart.mechanics.layers.UACanvas;

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
