package com.erhannis.unstableart.mechanics.context;

/**
 * Created by erhannis on 3/23/17.
 */
public abstract class EffectLayer extends Layer {
  public EffectLayer() {
  }

  protected EffectLayer(String uuid) {
    super(uuid);
  }

  @Override
  protected Layer init() {
    super.init();
    return this;
  }
}
