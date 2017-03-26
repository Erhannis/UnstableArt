package com.erhannis.arttraining.history;

import android.graphics.Canvas;

import com.erhannis.arttraining.mechanics.State;
import com.erhannis.arttraining.mechanics.context.ArtContext;
import com.erhannis.arttraining.mechanics.context.Layer;
import com.erhannis.arttraining.mechanics.context.UACanvas;

/**
 * Created by erhannis on 3/18/17.
 */
public class SetLayerSMHN extends StateModificationAHN {
  public final Layer aLayer;

  public SetLayerSMHN(Layer aLayer) {
    this.aLayer = aLayer;
  }

  @Override
  public void apply(State state, UACanvas iCanvas) {
    state.iSelectedLayer = iCanvas.archetypeToInstantiation.get(aLayer);
  }
}
