package com.erhannis.unstableart.history;

import android.graphics.Bitmap;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.context.UACanvas;

/**
 * Created by erhannis on 3/18/17.
 */
public abstract class PaintAHN extends HistoryNode {
  public abstract void apply(State state, UACanvas iCanvas);
  public abstract void draw(ArtContext artContext, State state, Bitmap canvas);
}
