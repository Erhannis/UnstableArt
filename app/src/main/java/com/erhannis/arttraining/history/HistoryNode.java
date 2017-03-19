package com.erhannis.arttraining.history;

import java.util.ArrayList;
import java.util.Date;

/**
 * //TODO Things to do here:
 * apply
 * connect to (mipmap?) (/state/?) cache
 *
 * Created by erhannis on 3/18/17.
 */
public class HistoryNode {
  public final Date creation;
  public final ArrayList<HistoryNode> children = new ArrayList<>();

  public HistoryNode() {
    //TODO It MIGHT be necessary to be able to set this manually
    creation = new Date();
  }
}
