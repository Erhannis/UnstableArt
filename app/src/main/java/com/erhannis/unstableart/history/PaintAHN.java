package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.layers.UACanvas;

/**
 * Created by erhannis on 3/18/17.
 */
public abstract class PaintAHN extends HistoryNode {
  public abstract void apply(State state, UACanvas iCanvas);
}
