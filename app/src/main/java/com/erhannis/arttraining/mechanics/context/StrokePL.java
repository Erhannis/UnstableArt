package com.erhannis.arttraining.mechanics.context;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.erhannis.arttraining.mechanics.color.Color;
import com.erhannis.arttraining.mechanics.stroke.Stroke;
import com.erhannis.arttraining.mechanics.stroke.StrokeTool;

import java.util.ArrayList;

/**
 * Created by erhannis on 3/22/17.
 */
public class StrokePL extends PaintLayer {
  //TODO Initialize?
  // These must all have the same sizes; they go together.
  // first=bottom
  public ArrayList<Stroke> strokes;
  public ArrayList<StrokeTool> tools;
  public ArrayList<Color> colors;

  @Override
  public void draw(ArtContext artContext, Bitmap canvas) {
    Canvas cCanvas = new Canvas(canvas);
    for (int i = 0; i < strokes.size(); i++) {
      //TODO Pass cCanvas in?
      tools.get(i).apply(colors.get(i), strokes.get(i), canvas);
    }
  }
}
