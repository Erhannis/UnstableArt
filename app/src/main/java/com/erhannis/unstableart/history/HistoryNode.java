package com.erhannis.unstableart.history;

import java.util.Date;
import java.util.LinkedHashSet;

/**
 * //TODO Things to do here:
 * apply
 * connect to (mipmap?) (/state/?) cache
 *
 * Created by erhannis on 3/18/17.
 */
public abstract class HistoryNode {
  public final Date creation;
  //TODO Would it make any sense to allow multiple inheritance?  cycles?
  //TODO Ech, do I or do I not make these final?
  public HistoryNode preferredParent;
  public LinkedHashSet<HistoryNode> parents = new LinkedHashSet<>();
  public HistoryNode preferredChild; // For ease of redo
  public LinkedHashSet<HistoryNode> children = new LinkedHashSet<>();

  public HistoryNode() {
    //TODO It MIGHT be necessary to be able to set this manually
    creation = new Date();
  }

  /**
   * Also sets preferredParent.  Until further notice.
   * @param parent
   */
  public void setParent(HistoryNode parent) {
    parents.add(parent);
    preferredParent = parent;
    //TODO Notify anybody?
  }

  public void addChild(HistoryNode child) {
    children.add(child);
    preferredChild = child;
    //TODO Notify anybody?
  }
}
