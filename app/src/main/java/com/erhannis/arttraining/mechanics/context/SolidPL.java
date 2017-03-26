package com.erhannis.arttraining.mechanics.context;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.erhannis.arttraining.mechanics.color.Color;
import com.erhannis.arttraining.mechanics.stroke.Stroke;
import com.erhannis.arttraining.mechanics.stroke.StrokeTool;

import java.util.ArrayList;

/**
 * Created by erhannis on 3/23/17.
 */
public class SolidPL extends PaintLayer {
  //TODO Initialize?
  public transient Color color;

  @Override
  public void draw(ArtContext artContext, Bitmap canvas) {
    Canvas cCanvas = new Canvas(canvas);
    cCanvas.drawColor(color.getARGBInt());
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }

  //TODO Pass in color?
  @Override
  public StrokePL instantiate() {
    return (StrokePL)new StrokePL().init();
  }
}
