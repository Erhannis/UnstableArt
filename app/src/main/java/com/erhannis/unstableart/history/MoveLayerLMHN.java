package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.context.GroupLayer;
import com.erhannis.unstableart.mechanics.context.Layer;
import com.erhannis.unstableart.mechanics.context.UACanvas;

/**
 * //TODO Incomplete
 * Created by erhannis on 3/18/17.
 */
public class MoveLayerLMHN extends LayerModificationAHN {
  public final GroupLayer aNewParent;
  public final Layer aChild;
  public final int newPosition;

  private MoveLayerLMHN() {
    aNewParent = null;
    aChild = null;
    newPosition = 0;
  }

  public MoveLayerLMHN(Layer aChild, GroupLayer aNewParent, int newPosition) {
    if (!aNewParent.archetype || !aChild.archetype) {
      System.err.println("Don't save instantiated layers in the nodes!  Just archetypes!");
    }
    this.aNewParent = aNewParent;
    this.aChild = aChild;
    this.newPosition = newPosition;
  }

  /**
   * Pass in an `instantiate`d canvas
   * @param iCanvas
   */
  @Override
  public void apply(UACanvas iCanvas) {
    //TODO Do
    asdf;
  }
}
