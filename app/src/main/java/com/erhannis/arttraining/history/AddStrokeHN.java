package com.erhannis.arttraining.history;

import com.erhannis.arttraining.mechanics.stroke.Stroke;

/**
 * Created by erhannis on 3/18/17.
 */
public class AddStrokeHN extends HistoryNode {
  public final Stroke stroke;

  public AddStrokeHN(Stroke stroke) {
    this.stroke = stroke;
  }
}
