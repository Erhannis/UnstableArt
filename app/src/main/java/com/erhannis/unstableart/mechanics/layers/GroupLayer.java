package com.erhannis.unstableart.mechanics.layers;

import android.graphics.Bitmap;

import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.terlici.dragndroplist.Tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * //TODO I'm tempted for this to replace UACanvas
 * Created by erhannis on 3/22/17.
 */
public class GroupLayer extends Layer implements Tree<Layer> {
  //IDEA It may be worth considering non-orderable layers.
  // Low=back
  public transient ArrayList<Layer> iLayers;

  public GroupLayer() {
  }

  protected GroupLayer(String uuid) {
    super(uuid);
  }

  public void addLayer(Layer iLayer) {
    iLayers.add(iLayer);
    //TODO Notify anyone?
  }

  @Override
  public List<Layer> getChildren() {
    return iLayers;
  }

  @Override
  public void drawInner(ArtContext artContext, Bitmap canvas) {
    Bitmap copy = canvas.copy(canvas.getConfig(), true);
    for (Layer iLayer : iLayers) {
      iLayer.draw(artContext, copy);
    }
    copyOntoWithOpacity(copy, canvas);
  }

  @Override
  protected Layer init() {
    super.init();
    iLayers = new ArrayList<Layer>();
    return this;
  }

  @Override
  public GroupLayer instantiate() {
    return (GroupLayer) new GroupLayer(this.getId()).init();
  }

  //<editor-fold desc="Group methods">
  public static GroupLayer findIParent(GroupLayer iRoot, Layer iChild) {
    LinkedList<GroupLayer> toSearch = new LinkedList<>();
    HashSet<GroupLayer> searched = new HashSet<>();
    toSearch.offer(iRoot);
    while (!toSearch.isEmpty()) {
      GroupLayer iLayer = toSearch.poll();
      searched.add(iLayer);
      for (Layer i : iLayer.getChildren()) {
        if (i == iChild) {
          return iLayer;
        }
        if (i instanceof GroupLayer) {
          // Just in case we ever allow cycles
          //TODO Though, if we do...uh, I dunno.
          if (!searched.contains(i)) {
            toSearch.offer((GroupLayer)i);
          }
        }
      }
    }
    return null;
  }

  /**
   * Checks to see if moving iNewChild into iNewParent would be like putting a bag into itself.
   * Ie., checks to see if iNewChild is an ancestor to iNewParent.
   * @param iNewChild
   * @param iNewParent
   * @return
   */
  public static boolean hasCycle(Layer iNewChild, GroupLayer iNewParent) {
    if (!(iNewChild instanceof GroupLayer)) {
      return false;
    }
    LinkedList<GroupLayer> toSearch = new LinkedList<>();
    HashSet<GroupLayer> searched = new HashSet<>();
    toSearch.offer((GroupLayer)iNewChild);
    while (!toSearch.isEmpty()) {
      GroupLayer iLayer = toSearch.poll();
      searched.add(iLayer);
      if (iLayer == iNewParent) {
        return true;
      }
      for (Layer i : iLayer.getChildren()) {
        if (i instanceof GroupLayer) {
          // Just in case we ever allow cycles
          //TODO Though, if we do...uh, I dunno.
          if (!searched.contains(i)) {
            toSearch.offer((GroupLayer)i);
          }
        }
      }
    }
    return false;
  }

  @Override
  public String getDescription() {
    return "GroupLayer - Contains several layers, rendering them all on top of each other.";
  }
  //</editor-fold>
}
