package com.erhannis.unstableart.history;

import android.support.v4.util.ObjectsCompat;

import com.erhannis.android.orderednetworkview.Node;
import com.erhannis.unstableart.mechanics.FullState;
import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.color.DoublesColor;
import com.erhannis.unstableart.mechanics.layers.GroupLayer;
import com.erhannis.unstableart.mechanics.layers.Layer;
import com.erhannis.unstableart.mechanics.layers.StrokePL;
import com.erhannis.unstableart.mechanics.layers.UACanvas;
import com.erhannis.unstableart.mechanics.stroke.BrushST;
import com.erhannis.unstableart.mechanics.stroke.Stroke;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * //TODO I think I need to seriously reconsider the threading and access patterns in this app
 *
 * Created by erhannis on 3/18/17.
 */
public class HistoryManager implements Serializable {
  //TODO Consider finality
  protected final RootHN root;
  protected HistoryNode selectedForView;
  protected HistoryNode selectedForEdit;

  protected transient Stroke mCurStroke = null;

  public HistoryManager() {
    //TODO Consider
    root = new RootHN();
    selectMarkers(root, root, 0);

    //TODO Just for testing
    testInit();
  }

  protected synchronized void testInit() {
    StrokePL strokeLayer = new StrokePL();
    attach(new AddLayerLMHN(root.aCanvas, strokeLayer));
    attach(new SetLayerSMHN(strokeLayer));
    attach(new SetToolSMHN(new BrushST()));
    attach(new SetColorSMHN(new DoublesColor(1, 0, 0.9, 0.5)));
    attach(new SetToolSizeSMHN(10.0));
    attach(new SetCanvasModeSMHN(State.CanvasMode.FOLLOW_VIEWPORT));
    //BlurEL blurLayer = new BlurEL();
    //attach(new AddLayerLMHN(root.aCanvas, blurLayer));
  }

  public synchronized void selectForView(HistoryNode node) {
    //TODO Send events, etc.
    //TODO Check connected to root?
    selectedForView = node;
    if (!ObjectsCompat.equals(selectedForView, selectedForEdit) && !Node.isAncestor(selectedForEdit, selectedForView)) {
      // The marker order is invalid; move edit marker, too
      selectedForEdit = selectedForView;
    }
  }

  public synchronized void selectForEdit(HistoryNode node) {
    //TODO Send events, etc.
    //TODO Check connected to root?
    selectedForEdit = node;
    if (!ObjectsCompat.equals(selectedForView, selectedForEdit) && !Node.isAncestor(selectedForEdit, selectedForView)) {
      // The marker order is invalid; move view marker, too
      selectedForView = selectedForEdit;
    }
  }

  public synchronized void selectMarkers(HistoryNode viewNode, HistoryNode editNode, int priorityMarker) {
    //TODO Send events, etc.
    //TODO Check connected to root?
    selectedForView = viewNode;
    selectedForEdit = editNode;
    if (!ObjectsCompat.equals(selectedForView, selectedForEdit) && !Node.isAncestor(selectedForEdit, selectedForView)) {
      // The marker order is invalid; move markers to match prioritized marker
      switch (priorityMarker) {
        case 1:
          selectedForView = selectedForEdit;
          break;
        case 0:
        default:
          selectedForEdit = selectedForView;
          break;
      }
    }
  }

  public synchronized void attach(HistoryNode child) {
    attach(selectedForEdit, child);
    selectForEdit(child);
  }

  public synchronized RootHN getRoot() {
    return root;
  }

  public synchronized HistoryNode getSelectedForView() {
    return selectedForView;
  }

  public synchronized HistoryNode getSelectedForEdit() {
    return selectedForEdit;
  }

  protected synchronized void attach(HistoryNode parent, HistoryNode child) {
    //TODO Send events, etc.
    //TODO Check connected to root?
    parent.addChild(child);
  }

  /**
   * Iff possible, undo.
   * @return whether undo happened
   */
  public synchronized boolean tryUndo() {
    if (selectedForEdit.preferredParent != null) {
      HistoryNode target = selectedForEdit.preferredParent;
      if (ObjectsCompat.equals(selectedForView, selectedForEdit)) {
        selectForEdit(target);
        selectForView(target);
      } else {
        selectForEdit(target);
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Iff possible, redo.
   * @return whether redo happened
   */
  public synchronized boolean tryRedo() {
    if (!selectedForEdit.children().isEmpty()) {
      selectForEdit(selectedForEdit.children().peekFirst());
      return true;
    } else {
      return false;
    }
  }

  //TODO Commit-rollback architecture?

  //TODO How show current stroke?

  //TODO Allow pass in?
  public synchronized Stroke startStrokeTransaction() {
    //TODO Throw error if in transaction?
    //TODO Set state?  (inTransaction = stroke)
    mCurStroke = new Stroke();
    return mCurStroke;
  }

  public synchronized Stroke getCurStroke() {
    //TODO Throw error if not in correct state?
    return mCurStroke;
  }

  public synchronized Stroke commitStrokeTransaction() {
    //TODO Throw error if not in correct state?
    HistoryNode strokeNode = new AddStrokePHN(mCurStroke);
    attach(selectedForEdit, strokeNode);
    selectForEdit(strokeNode);
    Stroke stroke = mCurStroke;
    mCurStroke = null;
    return stroke;
  }

  public synchronized void rollbackStrokeTransaction() {
    //TODO Throw error if not in correct state?
    mCurStroke = null;
  }

  //TODO Check transaction?
  public synchronized void executeCreateLayer(String parentUuid, Layer child) {
    Layer layer = findLayerByUuid(root, parentUuid);
    if (layer != null && layer instanceof GroupLayer) {
      attach(new AddLayerLMHN((GroupLayer)layer, child));
    } else {
      //TODO Toast?
      System.err.println("HM.executeCreateLayer error");
    }
  }

  //TODO Check transaction?
  public synchronized void executeSelectLayer(String layerUuid) {
    Layer layer = findLayerByUuid(root, layerUuid);
    if (layer != null) {
      attach(new SetLayerSMHN(layer));
    } else {
      //TODO Toast?
      System.err.println("HM.executeSelectLayer error");
    }
  }

  //TODO Check transaction?
  public synchronized void executeMoveLayer(String layerUuid, String newParentUuid, int newPosition) {
    Layer aChild = findLayerByUuid(root, layerUuid);
    Layer aParent = findLayerByUuid(root, newParentUuid);
    if (aChild != null && aParent != null && aParent instanceof GroupLayer) {
      attach(new MoveLayerLMHN(aChild, (GroupLayer)aParent, newPosition));
    } else {
      //TODO Toast?
      System.err.println("HM.executeMoveLayer error");
    }
  }

  //TODO Check transaction?
  public synchronized void executeShowHideLayer(String layerUuid, boolean visible) {
    Layer aLayer = findLayerByUuid(root, layerUuid);
    if (aLayer != null) {
      attach(new ShowHideLayerLMHN(aLayer, visible));
    } else {
      //TODO Toast?
      System.err.println("HM.executeShowHideLayer error");
    }
  }

  protected static Layer findLayerByUuid(HistoryNode root, String layerUuid) {
    if (layerUuid == null) {
      return null;
    }
    // Assuming here that RootHN occurs first or not at all
    if (root instanceof RootHN && layerUuid.equals(((RootHN)root).aCanvas.uuid)) {
      return ((RootHN)root).aCanvas;
    }
    LinkedList<HistoryNode> toSearch = new LinkedList<>();
    HashSet<HistoryNode> searched = new HashSet<>();
    toSearch.offer(root);
    while (!toSearch.isEmpty()) {
      HistoryNode node = toSearch.poll();
      searched.add(node);
      //TODO It's POSSIBLE that we might have other kinds of nodes that introduce new layers.
      //TODO We might also consider having a different way of tracking layers.
      if (node instanceof AddLayerLMHN) {
        if (layerUuid.equals(((AddLayerLMHN)node).aChild.uuid)) {
          return ((AddLayerLMHN)node).aChild;
        }
      }
      for (HistoryNode child : node.children()) {
        // Just in case we ever allow cycles
        if (!searched.contains(child)) {
          toSearch.offer(child);
        }
      }
    }
    return null;
  }

  /** I STRONGLY recommend that the code calling this function not retain any references to
   *  the returned FullState or any subfields, on pain of threading issues.
   */
  public synchronized FullState rebuild() {
    HistoryNode curr = root;
    ArrayList<HistoryNode> chain = new ArrayList<HistoryNode>();
    chain.add(curr);
    while (!curr.children().isEmpty() && !ObjectsCompat.equals(curr, selectedForView)) {
      curr = curr.children().getFirst();
      chain.add(curr);
    }
    // Now we have a list of actions, from start to finish
    // Set up current layer structure
    UACanvas iCanvas = root.aCanvas.instantiate();
    iCanvas.archetypeToInstantiation.put(root.aCanvas, iCanvas);
    for (HistoryNode node : chain) {
      if (node instanceof LayerModificationAHN) {
        ((LayerModificationAHN)node).apply(iCanvas);
      }
    }
    //TODO Do
    State state = new State();
    //TODO Initialize state to a default?
    for (HistoryNode node : chain) {
      if (node instanceof StateModificationAHN) {
        ((StateModificationAHN)node).apply(state, iCanvas);
      } else if (node instanceof PaintAHN) {
        ((PaintAHN) node).apply(state, iCanvas);
      }
      //TODO Account for all node types?
    }
    return new FullState(state, iCanvas);
  }
}
