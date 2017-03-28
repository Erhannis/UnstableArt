package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.context.UACanvas;

/**
 * //TODO What if we want to allow changing color during a stroke?
 *
 * //TODO How do I actually get this to do something?  Do I use artContext for that?
 *
 * Created by erhannis on 3/18/17.
 */
public class SetColorSMHN extends StateModificationAHN {
  public final Color color;

  public SetColorSMHN(Color color) {
    this.color = color;
  }

  @Override
  public void apply(State state, UACanvas iCanvas) {
    state.color = color;
  }
}
