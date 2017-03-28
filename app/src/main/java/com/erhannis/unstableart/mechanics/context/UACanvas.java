package com.erhannis.unstableart.mechanics.context;

import java.util.HashMap;

/**
 * Created by erhannis on 3/22/17.
 */
public class UACanvas extends GroupLayer {
  //TODO Methods?
  public HashMap<Layer, Layer> archetypeToInstantiation;

  @Override
  protected Layer init() {
    super.init();
    archetypeToInstantiation = new HashMap<Layer, Layer>();
    return this;
  }

  @Override
  public UACanvas instantiate() {
    return (UACanvas) new UACanvas().init();
  }
}
