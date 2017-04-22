package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.context.GroupLayer;
import com.erhannis.unstableart.mechanics.context.Layer;
import com.erhannis.unstableart.mechanics.context.UACanvas;

/**
 * Notes on time-shenanigans:
 * 1.  If the target parent no longer exists, nothing happens.
 * 2.  If the moving child no longer exists, nothing happens.
 * 3.  If the moving child is a parent to the spot you're trying to put it, nothing happens.
 * 4.  Currently, it will get put at the specified index.  I kinda want to try to make it move
 *        naturally with changes, but that's a bit out of scope atm.  If children are removed to the
 *        point where the target index is invalid, it'll get put at the end of the list.
 * //TODO Mess with it
 *
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

  /**
   * newPosition is calculated like, remove the child from the tree.  Now newPosition points to the
   * index (in the new parent's children) at which the child is to be inserted.
   *
   * @param aChild
   * @param aNewParent
   * @param newPosition
   */
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
    Layer iChild = ((Layer)iCanvas.archetypeToInstantiation.get(aChild));
    GroupLayer iNewParent = ((GroupLayer)iCanvas.archetypeToInstantiation.get(aNewParent));
    GroupLayer iOldParent = GroupLayer.findIParent(iCanvas, iChild);
    if (GroupLayer.hasCycle(iChild, iNewParent)) {
      System.err.println("Skipping layer-move; cycle averted");
      return;
    }
    iOldParent.getChildren().remove(iChild);
    iNewParent.getChildren().add(Math.min(newPosition, iNewParent.getChildren().size()), iChild);
  }
}
