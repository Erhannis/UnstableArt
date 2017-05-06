package com.erhannis.unstableart.mechanics.context;

/**
 * Created by erhannis on 3/23/17.
 */
public abstract class GeomLayer extends Layer {
  public GeomLayer() {
  }

  protected GeomLayer(String uuid) {
    super(uuid);
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }
}
