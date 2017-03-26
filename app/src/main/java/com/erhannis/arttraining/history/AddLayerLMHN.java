package com.erhannis.arttraining.history;

import com.erhannis.arttraining.mechanics.State;
import com.erhannis.arttraining.mechanics.context.GroupLayer;
import com.erhannis.arttraining.mechanics.context.Layer;
import com.erhannis.arttraining.mechanics.context.PaintLayer;
import com.erhannis.arttraining.mechanics.context.StrokePL;
import com.erhannis.arttraining.mechanics.context.UACanvas;

/**
 * //TODO Incomplete
 * Created by erhannis on 3/18/17.
 */
public class AddLayerLMHN extends LayerModificationAHN {
  public final GroupLayer aParent;
  public final Layer aChild;

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
