package com.erhannis.arttraining.history;

import android.graphics.Canvas;

import com.erhannis.arttraining.mechanics.color.Color;
import com.erhannis.arttraining.mechanics.context.ArtContext;

/**
 * //TODO Ooohhh, fisk.  You can't just have all the nodes draw in order - this one affects the background,
 * even if it's the last one to be applied.  ...Ow, that's gonna mess with the caches, maybe. ...mmmaaaybe.
 * Created by erhannis on 3/18/17.
 */
public class SetBackgroundColorHN extends SetBackgroundHN {
  public final Color color;

  public SetBackgroundColorHN(Color color) {
    this.color = color;
  }

  @Override
  public void draw(ArtContext artContext, Canvas canvas) {
    parent.draw(artContext, canvas);
    //TODO TODO Do
    throw new RuntimeException("Unimplemented");
  }
}
