package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.layers.UACanvas;

/**
 * Created by erhannis on 3/18/17.
 */
public class RootHN extends HistoryNode {
  public final UACanvas aCanvas;

  public RootHN() {
    this(new UACanvas());
  }

  //TODO Is this ctor necessary?
  public RootHN(UACanvas aCanvas) {
    this.aCanvas = aCanvas;
  }
}
