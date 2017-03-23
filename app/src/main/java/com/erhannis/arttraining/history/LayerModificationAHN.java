package com.erhannis.arttraining.history;

import com.erhannis.arttraining.mechanics.context.UACanvas;

/**
 * Created by erhannis on 3/18/17.
 */
public abstract class LayerModificationAHN extends HistoryNode {
  public abstract void apply(UACanvas canvas);
}
