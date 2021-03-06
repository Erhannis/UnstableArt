package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.layers.Layer;
import com.erhannis.unstableart.mechanics.layers.UACanvas;

/**
 * Created by erhannis on 3/18/17.
 */
public class SetLayerSMHN extends StateModificationAHN {
  public final Layer aLayer;

  private SetLayerSMHN() {
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
