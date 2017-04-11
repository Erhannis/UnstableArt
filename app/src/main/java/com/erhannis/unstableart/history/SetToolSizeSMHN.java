package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.context.UACanvas;

/**
 * Created by erhannis on 3/27/17.
 */
public class SetToolSizeSMHN extends StateModificationAHN {
  public final double size;

  private SetToolSizeSMHN() {
    size = 10;
  }

  public SetToolSizeSMHN(double size) {
    this.size = size;
  }

  @Override
  public void apply(State state, UACanvas iCanvas) {
    state.size = size;
  }
}
