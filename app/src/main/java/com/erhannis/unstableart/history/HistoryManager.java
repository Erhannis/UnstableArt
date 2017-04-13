package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.color.DoublesColor;
import com.erhannis.unstableart.mechanics.context.BlurEL;
import com.erhannis.unstableart.mechanics.context.StrokePL;
import com.erhannis.unstableart.mechanics.context.UACanvas;
import com.erhannis.unstableart.mechanics.stroke.BrushST;
import com.erhannis.unstableart.mechanics.stroke.Stroke;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by erhannis on 3/18/17.
 */
public class HistoryManager implements Serializable {
  //TODO Consider finality
  protected final RootHN root;
  //TODO Split into view and edit?
  protected HistoryNode selected;

  protected transient Stroke mCurStroke = null;

  public HistoryManager() {
    //TODO Consider
    root = new RootHN();
    select(root);

    //TODO Just for testing
    testInit();
  }

  public void testInit() {
    StrokePL strokeLayer = new StrokePL();
    attach(new AddLayerLMHN(root.aCanvas, strokeLayer));
    attach(new SetLayerSMHN(strokeLayer));
    attach(new SetToolSMHN(new BrushST()));
    attach(new SetColorSMHN(new DoublesColor(1, 0, 0.9, 0.5)));
    attach(new SetToolSizeSMHN(10.0));
    //BlurEL blurLayer = new BlurEL();
    //attach(new AddLayerLMHN(root.aCanvas, blurLayer));
  }

  public void select(HistoryNode node) {
    //TODO Send events, etc.
    //TODO Check connected to root?
    selected = node;
  }

  public void attach(HistoryNode child) {
    attach(selected, child);
    select(child);
  }

  public void attach(HistoryNode parent, HistoryNode child) {
    //TODO Send events, etc.
    //TODO Check connected to root?
    parent.addChild(child);
    child.setParent(parent);
  }

  /**
   * Iff possible, undo.
   * @return whether undo happened
   */
  public synchronized boolean tryUndo() {
    if (selected.preferredParent != null) {
      select(selected.preferredParent);
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
    if (selected.preferredChild != null) {
      select(selected.preferredChild);
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
    attach(selected, strokeNode);
    select(strokeNode);
    Stroke stroke = mCurStroke;
    mCurStroke = null;
    return stroke;
  }

  public synchronized void rollbackStrokeTransaction() {
    //TODO Throw error if not in correct state?
    mCurStroke = null;
  }

  public UACanvas rebuild() {
    HistoryNode curr = selected;
    ArrayList<HistoryNode> chain = new ArrayList<HistoryNode>();
    while (curr != null) {
      chain.add(curr);
      curr = curr.preferredParent;
    }
    Collections.reverse(chain);
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
    return iCanvas;
  }
}
