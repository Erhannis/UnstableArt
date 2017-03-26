package com.erhannis.arttraining.history;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.erhannis.arttraining.mechanics.State;
import com.erhannis.arttraining.mechanics.color.Color;
import com.erhannis.arttraining.mechanics.color.DoublesColor;
import com.erhannis.arttraining.mechanics.context.ArtContext;
import com.erhannis.arttraining.mechanics.context.PaintLayer;
import com.erhannis.arttraining.mechanics.context.StrokePL;
import com.erhannis.arttraining.mechanics.context.UACanvas;
import com.erhannis.arttraining.mechanics.stroke.Stroke;
import com.erhannis.arttraining.mechanics.stroke.StrokePoint;

/**
 * Created by erhannis on 3/18/17.
 */
public class AddStrokePHN extends PaintAHN {
  public final Stroke stroke;

  public AddStrokePHN(Stroke stroke) {
    this.stroke = stroke;
  }

  @Override
  public void apply(State state, UACanvas iCanvas) {
    if (state.iSelectedLayer instanceof StrokePL) {
      ((StrokePL)state.iSelectedLayer).strokes.add(stroke);
    }
  }
}
