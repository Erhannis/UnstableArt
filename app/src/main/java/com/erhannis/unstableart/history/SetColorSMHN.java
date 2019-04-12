package com.erhannis.unstableart.history;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.erhannis.unstableart.R;
import com.erhannis.unstableart.UAApplication;
import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.layers.UACanvas;

/**
 * //TODO What if we want to allow changing color during a stroke?
 *
 * //TODO How do I actually get this to do something?  Do I use artContext for that?
 *
 * Created by erhannis on 3/18/17.
 */
public class SetColorSMHN extends StateModificationAHN {
  public final Color color;

  private SetColorSMHN() {
    color = null;
  }

  public SetColorSMHN(Color color) {
    this.color = color;
  }

  @Override
  public void apply(State state, UACanvas iCanvas) {
    state.color = color;
  }

  private final Drawable DRAWABLE = procureDrawable(R.drawable.n_set_color);
  @Override
  public Drawable getDrawable() {
    return DRAWABLE;
  }
}
