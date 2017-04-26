package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.context.UACanvas;

/**
 * Created by erhannis on 3/27/17.
 */
public class SetCanvasModeSMHN extends StateModificationAHN {
  public final State.CanvasMode mode;

  private SetCanvasModeSMHN() {
    mode = null;
  }

  public SetCanvasModeSMHN(State.CanvasMode mode) {
    this.mode = mode;
  }

  @Override
  public void apply(State state, UACanvas iCanvas) {
    state.canvasMode = mode;
  }
}
