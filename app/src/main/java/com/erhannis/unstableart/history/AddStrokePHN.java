package com.erhannis.unstableart.history;

import android.graphics.Bitmap;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.context.StrokePL;
import com.erhannis.unstableart.mechanics.context.UACanvas;
import com.erhannis.unstableart.mechanics.stroke.Stroke;
import com.erhannis.unstableart.mechanics.stroke.StrokeTool;

/**
 * Created by erhannis on 3/18/17.
 */
public class AddStrokePHN extends PaintAHN {
  public final Stroke stroke;

  private AddStrokePHN() {
    stroke = null;
  }

  public AddStrokePHN(Stroke stroke) {
    this.stroke = stroke;
  }

  @Override
  public void apply(State state, UACanvas iCanvas) {
    if (state.iSelectedLayer instanceof StrokePL && state.tool instanceof StrokeTool) {
      ((StrokePL)state.iSelectedLayer).actions.add(this);
      ((StrokePL)state.iSelectedLayer).strokes.add(stroke);
      ((StrokePL)state.iSelectedLayer).colors.add(state.color);
      ((StrokePL)state.iSelectedLayer).sizes.add(state.size);
      ((StrokePL)state.iSelectedLayer).tools.add((StrokeTool)state.tool);
    }
  }

  @Override
  public void draw(ArtContext artContext, State state, Bitmap canvas) {
    if (state.tool instanceof StrokeTool) {
      //TODO I have mixed feelings about just passing State in.  Can we maybe please not allow tools to just mess with the state whenever?
      //colors.get(i), sizes.get(i), strokes.get(i)
      ((StrokeTool)state.tool).apply(artContext, state, canvas);
    }
  }
}
