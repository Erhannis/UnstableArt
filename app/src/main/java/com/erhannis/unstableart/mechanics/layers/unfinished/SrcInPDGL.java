package com.erhannis.unstableart.mechanics.layers.unfinished;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.erhannis.unstableart.mechanics.context.ArtContext;
import com.erhannis.unstableart.mechanics.layers.Layer;

/**
 * //TODO Not working right
 * Created by erhannis on 3/22/17.
 */
public class SrcInPDGL extends PorterDuffGL {
  public SrcInPDGL() {
    super(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
  }

  protected SrcInPDGL(String uuid) {
    super(uuid, new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }

  @Override
  public SrcInPDGL instantiate() {
    return (SrcInPDGL) new SrcInPDGL(this.getId()).init();
  }

  @Override
  public String getDescription() {
    return "SrcIn PorterDuff Group Layer - draws the contained layers onto the canvas via the SrcIn PorterDuff mode.  Eventually you'll be able to directly pick the mode of PorterDuffGL, so this is unfinished.";
  }
}
