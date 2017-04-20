package com.terlici.dragndroplist;

import java.util.List;

/**
 * Created by erhannis on 4/19/17.
 */

public interface Tree<T> {
  // Note that the children MAY also be trees...but it's not required.
  public List<T> getChildren();
}
