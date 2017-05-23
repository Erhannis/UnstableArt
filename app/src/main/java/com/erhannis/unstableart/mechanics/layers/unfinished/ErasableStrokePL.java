package com.erhannis.unstableart.mechanics.layers.unfinished;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.layers.Layer;
import com.erhannis.unstableart.mechanics.layers.StrokePL;

/**
 * This...should be how it works by default.  Except, maybe not?  I'll have to think if this adversely affects anything.
 *
 * Created by erhannis on 5/22/17.
 */
public class ErasableStrokePL extends StrokePL {
  public ErasableStrokePL() {
  }

  protected ErasableStrokePL(String uuid) {
    super(uuid);
  }

  @Override
  public void drawInner(ArtContext artContext, Bitmap canvas) {
    //Canvas cCanvas = new Canvas(canvas);
    Bitmap copy = canvas.copy(Bitmap.Config.ARGB_8888, true);
    for (int i = 0; i < strokes.size(); i++) {
      //TODO Pass cCanvas in?
      tools.get(i).apply(artContext, colors.get(i), null, sizes.get(i), strokes.get(i), copy);
    }
    copyOntoWithOpacity(copy, canvas);
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }

  @Override
  public ErasableStrokePL instantiate() {
    return (ErasableStrokePL)new ErasableStrokePL(this.getId()).init();
  }

  @Override
  public String getDescription() {
    return "Additive Stroke Paint Layer - Adds strokes additively.";
  }
}
