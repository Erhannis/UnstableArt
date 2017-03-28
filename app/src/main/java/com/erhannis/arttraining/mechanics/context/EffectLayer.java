package com.erhannis.arttraining.mechanics.context;

/**
 * Created by erhannis on 3/23/17.
 */
public abstract class EffectLayer extends Layer {
  @Override
  protected Layer init() {
    super.init();
    return this;
  }
}
