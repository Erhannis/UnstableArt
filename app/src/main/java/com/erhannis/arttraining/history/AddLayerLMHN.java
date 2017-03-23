package com.erhannis.arttraining.history;

import com.erhannis.arttraining.mechanics.context.PaintLayer;
import com.erhannis.arttraining.mechanics.context.StrokePL;
import com.erhannis.arttraining.mechanics.context.UACanvas;

/**
 * //TODO Incomplete
 * Created by erhannis on 3/18/17.
 */
public class AddLayerLMHN extends LayerModificationAHN {
  @Override
  public void apply(UACanvas canvas) {
    //TODO This is incomplete.
    //TODO We should be able to add layers to any group.
    //TODO We should be able to add any KIND of layer.
    canvas.layers.add(new StrokePL());
  }
}
