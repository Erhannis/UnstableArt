package com.erhannis.unstableart.mechanics.context;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.erhannis.unstableart.history.PaintAHN;
import com.erhannis.unstableart.mechanics.State;
import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.stroke.Stroke;
import com.erhannis.unstableart.mechanics.stroke.StrokeTool;
import com.erhannis.unstableart.mechanics.stroke.Tool;

import java.util.ArrayList;

/**
 * Created by erhannis on 3/22/17.
 */
public class StrokePL extends PaintLayer {
  public transient State state = new State();
  // first=bottom
  public transient ArrayList<PaintAHN> actions;

  public StrokePL() {
  }

  protected StrokePL(String uuid) {
    super(uuid);
  }

  @Override
  public void drawInner(ArtContext artContext, Bitmap canvas) {
    //Canvas cCanvas = new Canvas(canvas);
    for (int i = 0; i < actions.size(); i++) {
      //TODO Pass cCanvas in?
      actions.get(i).draw(state, canvas);
    }
  }

  @Override
  protected Layer init() {
    super.init();
    actions = new ArrayList<PaintAHN>();
    return this;
  }

  @Override
  public StrokePL instantiate() {
    return (StrokePL)new StrokePL(this.getId()).init();
  }
}
