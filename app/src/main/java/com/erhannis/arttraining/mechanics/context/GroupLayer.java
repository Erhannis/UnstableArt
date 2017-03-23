package com.erhannis.arttraining.mechanics.context;

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
  public ArrayList<Layer> layers = new ArrayList<Layer>();

  @Override
  public void draw(ArtContext artContext, Bitmap canvas) {
    Bitmap copy = canvas.copy(canvas.getConfig(), true);
    for (Layer layer : layers) {
      layer.draw(artContext, copy);
    }
    Canvas cCanvas = new Canvas(canvas);
    Paint p = new Paint();
    //TODO Clamp?
    p.setAlpha((int)(opacity * 255)); //LOSS
    cCanvas.drawBitmap(copy, 0, 0, p);
  }
}
