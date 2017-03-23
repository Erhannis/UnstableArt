package com.erhannis.arttraining.history;

import com.erhannis.arttraining.mechanics.State;
import com.erhannis.arttraining.mechanics.context.UACanvas;
import com.erhannis.arttraining.mechanics.stroke.Stroke;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by erhannis on 3/18/17.
 */
public class HistoryManager {
  //TODO Consider finality
  protected final HistoryNode root;
  //TODO Hmm, how show chain?  Double-link?
  protected HistoryNode selected;

  protected Stroke mCurStroke = null;

  public HistoryManager() {
    //TODO Consider
    root = new RootHN();
    select(root);
  }

  public void select(HistoryNode node) {
    //TODO Send events, etc.
    //TODO Check connected to root?
    selected = node;
  }

  public void attach(HistoryNode parent, HistoryNode child) {
    //TODO Send events, etc.
    //TODO Check connected to root?
    parent.children.add(child);
    child.parent = parent;
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
    UACanvas canvas = new UACanvas();
    for (HistoryNode node : chain) {
      if (node instanceof LayerModificationAHN) {
        ((LayerModificationAHN)node).apply(canvas);
      }
    }
    //TODO Do
    State state = new State();
    //TODO Initialize state to a default?
    for (HistoryNode node : chain) {
      if (node instanceof StateModificationAHN) {
        ((StateModificationAHN)node).apply(state);
      } else if (node instanceof PaintAHN) {
        //TODO Still need to figure out how we're referring to layers
        //TODO Still need to figure out how to get the AddStrokePHNs to set up the StrokePLs.
        ((PaintAHN)node);
      }
    }
  }
}
