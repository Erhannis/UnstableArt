package com.erhannis.unstableart.mechanics.context;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.erhannis.unstableart.mechanics.color.Color;

/**
 * Created by erhannis on 3/23/17.
 */
public class SolidPL extends PaintLayer {
  //TODO Initialize?
  public transient Color color;

  public SolidPL() {
  }

  protected SolidPL(String uuid) {
    super(uuid);
  }

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
  public SolidPL instantiate() {
    return (SolidPL) new SolidPL(this.getId()).init();
  }
}
