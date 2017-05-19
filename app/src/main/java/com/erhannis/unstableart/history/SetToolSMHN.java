package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.layers.UACanvas;
import com.erhannis.unstableart.mechanics.stroke.Tool;

/**
 * //TODO How do I actually get this to do something?  Do I use artContext for that?
 *
 * Created by erhannis on 3/18/17.
 */
public class SetToolSMHN extends StateModificationAHN {
  public final Tool tool;

  private SetToolSMHN() {
    tool = null;
  }

  public SetToolSMHN(Tool tool) {
    this.tool = tool;
  }

  @Override
  public void apply(State state, UACanvas iCanvas) {
    state.tool = tool;
  }
}
