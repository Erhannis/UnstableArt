package com.erhannis.arttraining.history;

import android.graphics.Canvas;

import com.erhannis.arttraining.mechanics.context.ArtContext;

import java.util.ArrayList;
import java.util.Date;

/**
 * //TODO Things to do here:
 * apply
 * connect to (mipmap?) (/state/?) cache
 *
 * Created by erhannis on 3/18/17.
 */
public abstract class HistoryNode {
  public final Date creation;
  //TODO Would it make any sense to allow multiple inheritance?  cycles?
  //TODO Ech, do I or do I not make these final?
  public HistoryNode preferredParent;
  public ArrayList<HistoryNode> parents = new ArrayList<>();
  //TODO Preferred child, for ease of undo/redo?
  public ArrayList<HistoryNode> children = new ArrayList<>();

  public HistoryNode() {
    //TODO It MIGHT be necessary to be able to set this manually
    creation = new Date();
  }
}
