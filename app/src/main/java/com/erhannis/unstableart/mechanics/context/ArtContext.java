package com.erhannis.unstableart.mechanics.context;

import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Maybe stores things like...
 * color bounds
 * spatial bounds
 *
 * Created by erhannis on 3/21/17.
 */
public class ArtContext {
  //TODO I might prefer doubles
  public final RectF spatialBounds = new RectF();
  public final Matrix transform = new Matrix();
}
