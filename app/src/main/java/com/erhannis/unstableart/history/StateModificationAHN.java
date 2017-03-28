package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.context.UACanvas;

/**
 * Created by erhannis on 3/18/17.
 */
public abstract class StateModificationAHN extends HistoryNode {
  public abstract void apply(State state, UACanvas iCanvas);
}
