package com.erhannis.unstableart.mechanics.context;

import java.util.HashMap;

/**
 * Created by erhannis on 3/22/17.
 */
public class UACanvas extends GroupLayer {
  //TODO Methods?
  public HashMap<Layer, Layer> archetypeToInstantiation;

  public UACanvas() {
  }

  protected UACanvas(String uuid) {
    super(uuid);
  }

  @Override
  protected Layer init() {
    super.init();
    archetypeToInstantiation = new HashMap<Layer, Layer>();
    return this;
  }

  @Override
  public UACanvas instantiate() {
    return (UACanvas) new UACanvas(this.getId()).init();
  }
}
