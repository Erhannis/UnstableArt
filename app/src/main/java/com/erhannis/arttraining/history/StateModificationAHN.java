package com.erhannis.arttraining.history;

import com.erhannis.arttraining.mechanics.State;
import com.erhannis.arttraining.mechanics.context.ArtContext;
import com.erhannis.arttraining.mechanics.context.UACanvas;

/**
 * Created by erhannis on 3/18/17.
 */
public abstract class StateModificationAHN extends HistoryNode {
  public abstract void apply(State state);
}
