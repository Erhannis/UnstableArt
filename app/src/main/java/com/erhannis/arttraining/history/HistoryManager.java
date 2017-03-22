package com.erhannis.arttraining.history;

import android.graphics.Canvas;

import com.erhannis.arttraining.mechanics.context.ArtContext;
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
    HistoryNode strokeNode = new AddStrokeHN(mCurStroke);
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

  //TODO Create context, rather than pass it?
  /**
   *
   * @param artContext
   * @param canvas The canvas to be filled with art.  This canvas is unaware of the viewport.  The viewport will probably copy this canvas into itself, scaled and transformed.
   */
  public void draw(ArtContext artContext, Canvas canvas) {
    selected.draw(artContext, canvas);
  }
}
