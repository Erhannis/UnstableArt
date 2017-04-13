package com.erhannis.unstableart.mechanics.context;

/**
 * Created by erhannis on 3/23/17.
 */
public abstract class PaintLayer extends Layer {
  public PaintLayer() {
  }

  protected PaintLayer(Layer uuidParent) {
    super(uuidParent);
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }
}
