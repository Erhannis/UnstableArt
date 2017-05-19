package com.erhannis.unstableart.mechanics.layers.unfinished;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.layers.Layer;

/**
 * Created by erhannis on 3/22/17.
 */
public class SrcAtopPDGL extends PorterDuffGL {
  public SrcAtopPDGL() {
    super(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
  }

  protected SrcAtopPDGL(String uuid) {
    super(uuid, new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }

  @Override
  public SrcAtopPDGL instantiate() {
    return (SrcAtopPDGL) new SrcAtopPDGL(this.getId()).init();
  }

  @Override
  public String getDescription() {
    return "SrcAtop PorterDuff Group Layer - draws the contained layers onto the canvas via the SrcAtop PorterDuff mode.  Eventually you'll be able to directly pick the mode of PorterDuffGL, so this is unfinished.";
  }
}
