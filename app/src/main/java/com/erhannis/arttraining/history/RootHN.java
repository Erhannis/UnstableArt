package com.erhannis.arttraining.history;

import android.graphics.Canvas;

import com.erhannis.arttraining.mechanics.color.Color;
import com.erhannis.arttraining.mechanics.context.ArtContext;
import com.erhannis.arttraining.mechanics.context.UACanvas;

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
