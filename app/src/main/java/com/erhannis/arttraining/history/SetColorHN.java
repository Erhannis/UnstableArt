package com.erhannis.arttraining.history;

import com.erhannis.arttraining.mechanics.color.Color;

/**
 * //TODO What if we want to allow changing color during a stroke?
 *
 * //TODO How do I actually get this to do something?  Do I use artContext for that?
 *
 * Created by erhannis on 3/18/17.
 */
public class SetColorHN extends HistoryNode {
  public final Color color;

  public SetColorHN(Color color) {
    this.color = color;
  }
}
