package com.erhannis.unstableart.mechanics.context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * //TODO I'm tempted for this to replace UACanvas
 * Created by erhannis on 3/22/17.
 */
public class GroupLayer extends Layer {
  //IDEA It may be worth considering non-orderable layers.
  // Low=back
  public transient ArrayList<Layer> iLayers;

  public void addLayer(Layer iLayer) {
    iLayers.add(iLayer);
    //TODO Notify anyone?
  }

  @Override
  public void draw(ArtContext artContext, Bitmap canvas) {
    Bitmap copy = canvas.copy(canvas.getConfig(), true);
    for (Layer iLayer : iLayers) {
      iLayer.draw(artContext, copy);
    }
    Canvas cCanvas = new Canvas(canvas);
    Paint p = new Paint();
    //TODO Clamp?
    p.setAlpha((int)(opacity * 255)); //LOSS
    cCanvas.drawBitmap(copy, 0, 0, p);
  }

  @Override
  protected Layer init() {
    super.init();
    iLayers = new ArrayList<Layer>();
    return this;
  }

  @Override
  public GroupLayer instantiate() {
    return (GroupLayer) new GroupLayer().init();
  }
}
