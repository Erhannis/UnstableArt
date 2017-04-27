package com.erhannis.unstableart.mechanics.context;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.stroke.Stroke;
import com.erhannis.unstableart.mechanics.stroke.StrokeTool;

import java.util.ArrayList;

/**
 * Created by erhannis on 3/22/17.
 */
public class StrokePL extends PaintLayer {
  //TODO Initialize?
  // These must all have the same sizes; they go together.
  // first=bottom
  public transient ArrayList<Stroke> strokes;
  public transient ArrayList<StrokeTool> tools;
  public transient ArrayList<Color> colors;
  public transient ArrayList<Double> sizes;

  public StrokePL() {
  }

  protected StrokePL(Layer uuidParent) {
    super(uuidParent);
  }

  @Override
  public void draw(ArtContext artContext, Bitmap canvas) {
    Canvas cCanvas = new Canvas(canvas);
    for (int i = 0; i < strokes.size(); i++) {
      //TODO Pass cCanvas in?
      tools.get(i).apply(artContext, colors.get(i), sizes.get(i), strokes.get(i), canvas);
    }
  }

  @Override
  protected Layer init() {
    super.init();
    strokes = new ArrayList<Stroke>();
    tools = new ArrayList<StrokeTool>();
    colors = new ArrayList<Color>();
    sizes = new ArrayList<Double>();
    return this;
  }

  @Override
  public StrokePL instantiate() {
    return (StrokePL)new StrokePL(this).init();
  }
}
