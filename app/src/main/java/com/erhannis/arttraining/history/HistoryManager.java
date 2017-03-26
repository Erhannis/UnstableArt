package com.erhannis.arttraining.history;

import com.erhannis.arttraining.mechanics.State;
import com.erhannis.arttraining.mechanics.color.DoublesColor;
import com.erhannis.arttraining.mechanics.context.SolidPL;
import com.erhannis.arttraining.mechanics.context.StrokePL;
import com.erhannis.arttraining.mechanics.context.UACanvas;
import com.erhannis.arttraining.mechanics.stroke.BrushST;
import com.erhannis.arttraining.mechanics.stroke.Stroke;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by erhannis on 3/18/17.
 */
public class HistoryManager {
  //TODO Consider finality
  protected final RootHN root;
  //TODO Hmm, how show chain?  Double-link?
  protected HistoryNode selected;

  protected Stroke mCurStroke = null;

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
