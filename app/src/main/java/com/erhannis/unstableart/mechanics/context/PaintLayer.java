package com.erhannis.unstableart.mechanics.context;

/**
 * Created by erhannis on 3/23/17.
 */
public abstract class PaintLayer extends Layer {
  public PaintLayer() {
  }

  protected PaintLayer(String uuid) {
    super(uuid);
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }
}
