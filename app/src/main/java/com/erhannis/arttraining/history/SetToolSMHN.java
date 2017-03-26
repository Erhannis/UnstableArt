package com.erhannis.arttraining.history;

import com.erhannis.arttraining.mechanics.State;
import com.erhannis.arttraining.mechanics.color.Color;
import com.erhannis.arttraining.mechanics.context.ArtContext;
import com.erhannis.arttraining.mechanics.context.UACanvas;
import com.erhannis.arttraining.mechanics.stroke.Tool;

/**
 * //TODO How do I actually get this to do something?  Do I use artContext for that?
 *
 * Created by erhannis on 3/18/17.
 */
public class SetToolSMHN extends StateModificationAHN {
  public final Tool tool;

  public SetToolSMHN(Tool tool) {
    this.tool = tool;
  }

  @Override
  public void apply(State state, UACanvas iCanvas) {
    state.tool = tool;
  }
}
