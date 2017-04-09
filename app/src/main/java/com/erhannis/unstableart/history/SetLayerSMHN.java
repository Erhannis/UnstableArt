package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.context.Layer;
import com.erhannis.unstableart.mechanics.context.UACanvas;

/**
 * Created by erhannis on 3/18/17.
 */
public class SetLayerSMHN extends StateModificationAHN {
  public final Layer aLayer;

  public SetLayerSMHN() {
    aLayer = null;
  }

  public SetLayerSMHN(Layer aLayer) {
    this.aLayer = aLayer;
  }

  @Override
  public void apply(State state, UACanvas iCanvas) {
    state.iSelectedLayer = iCanvas.archetypeToInstantiation.get(aLayer);
  }
}
