package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.layers.Layer;
import com.erhannis.unstableart.mechanics.layers.UACanvas;

public class ShowHideLayerLMHN extends LayerModificationAHN {
  public final Layer aLayer;
  public final boolean visible;

  private ShowHideLayerLMHN() {
    aLayer = null;
    visible = false;
  }

  public ShowHideLayerLMHN(Layer aLayer, boolean visible) {
    if (!aLayer.archetype) {
      System.err.println("Don't save instantiated layers in the nodes!  Just archetypes!");
    }
    this.aLayer = aLayer;
    this.visible = visible;
  }

  /**
   * Pass in an `instantiate`d canvas
   * @param iCanvas
   */
  @Override
  public void apply(UACanvas iCanvas) {
    Layer iLayer = ((Layer)iCanvas.archetypeToInstantiation.get(aLayer));
    iLayer.visible = visible;
  }
}
