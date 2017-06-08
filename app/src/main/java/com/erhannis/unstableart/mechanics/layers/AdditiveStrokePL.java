package com.erhannis.unstableart.mechanics.layers;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

import com.erhannis.unstableart.mechanics.context.ArtContext;

/**
 * Created by erhannis on 3/22/17.
 */
public class AdditiveStrokePL extends StrokePL {
  private static final Xfermode mode = new PorterDuffXfermode(PorterDuff.Mode.ADD);

  public AdditiveStrokePL() {
  }

  protected AdditiveStrokePL(String uuid) {
    super(uuid);
  }

  @Override
  public void drawInner(ArtContext artContext, Bitmap canvas) {
    //Canvas cCanvas = new Canvas(canvas);
    for (int i = 0; i < strokes.size(); i++) {
      //TODO Pass cCanvas in?
      tools.get(i).apply(artContext, colors.get(i), mode, sizes.get(i), strokes.get(i), canvas);
    }
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }

  @Override
  public AdditiveStrokePL instantiate() {
    return (AdditiveStrokePL)new AdditiveStrokePL(this.getId()).init();
  }

  @Override
  public String getDescription() {
    return "Additive Stroke Paint Layer - Adds strokes additively.";
  }
}
