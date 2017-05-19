package com.erhannis.unstableart.history;

import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.layers.StrokePL;
import com.erhannis.unstableart.mechanics.layers.UACanvas;
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
      ((StrokePL)state.iSelectedLayer).strokes.add(stroke);
      ((StrokePL)state.iSelectedLayer).colors.add(state.color);
      ((StrokePL)state.iSelectedLayer).sizes.add(state.size);
      ((StrokePL)state.iSelectedLayer).tools.add((StrokeTool)state.tool);
    }
  }
}
