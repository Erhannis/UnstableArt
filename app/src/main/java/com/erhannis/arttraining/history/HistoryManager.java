package com.erhannis.arttraining.history;

import com.erhannis.arttraining.mechanics.stroke.Stroke;

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
    root = new HistoryNode();
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
  }

  //TODO Commit-rollback architecture?

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
    HistoryNode strokeNode = new AddStrokeHN(mCurStroke);
    attach(selected, strokeNode);
    select(strokeNode);
    Stroke stroke = mCurStroke;
    mCurStroke = null;
    return stroke;
  }
}
