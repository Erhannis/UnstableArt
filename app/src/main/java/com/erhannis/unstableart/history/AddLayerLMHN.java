package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.layers.GroupLayer;
import com.erhannis.unstableart.mechanics.layers.Layer;
import com.erhannis.unstableart.mechanics.layers.UACanvas;

/**
 * //TODO Incomplete
 * Created by erhannis on 3/18/17.
 */
public class AddLayerLMHN extends LayerModificationAHN {
  public final GroupLayer aParent;
  public final Layer aChild;

  private AddLayerLMHN() {
    aParent = null;
    aChild = null;
  }

  public AddLayerLMHN(GroupLayer aParent, Layer aChild) {
    if (!aParent.archetype || !aChild.archetype) {
      System.err.println("Don't save instantiated layers in the nodes!  Just archetypes!");
    }
    this.aParent = aParent;
    this.aChild = aChild;
  }

  /**
   * Pass in an `instantiate`d canvas
   * @param iCanvas
   */
  @Override
  public void apply(UACanvas iCanvas) {
    Layer iChild = aChild.instantiate();
    iCanvas.archetypeToInstantiation.put(aChild, iChild);
    ((GroupLayer)iCanvas.archetypeToInstantiation.get(aParent)).addLayer(iChild);
  }
}
