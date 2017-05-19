package com.erhannis.unstableart.mechanics.layers.unfinished;

import com.erhannis.unstableart.mechanics.layers.Layer;

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
