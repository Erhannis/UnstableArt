package com.erhannis.unstableart.history;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.erhannis.android.orderednetworkview.DrawableNode;
import com.erhannis.android.orderednetworkview.Node;
import com.erhannis.unstableart.R;
import com.erhannis.unstableart.UAApplication;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;

/**
 * //TODO Things to do here:
 * apply
 * connect to (mipmap?) (/state/?) cache
 *
 * Created by erhannis on 3/18/17.
 */
public abstract class HistoryNode extends DrawableNode<HistoryNode> implements Serializable {
  public final Date creation;

  //TODO Not sure about this.  It's an optimization, but it might break some stuff in the future.
  //TODO Also, should it be transient?  It's intended to be ONLY an optimization?
  public HistoryNode preferredParent;

  public HistoryNode() {
    //TODO It MIGHT be necessary to be able to set this manually
    creation = new Date();
  }

  public void addChild(HistoryNode child) {
    children.remove(child);
    children.addFirst(child);
    //TODO See `preferredParent`
    child.preferredParent = this;
    //TODO Notify anybody?
  }

  //TODO This is a temporary default.
  private final Drawable DRAWABLE = ContextCompat.getDrawable(UAApplication.getContext(), R.drawable.n_blank);
  @Override
  public Drawable getDrawable() {
    return DRAWABLE;
  }
}
