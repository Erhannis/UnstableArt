package com.erhannis.unstableart.mechanics.context;

/**
 * Created by erhannis on 3/23/17.
 */
public abstract class PaintLayer extends Layer {
  @Override
  protected Layer init() {
    super.init();
    return this;
  }
}
