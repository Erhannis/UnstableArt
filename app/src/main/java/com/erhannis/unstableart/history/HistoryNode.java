package com.erhannis.unstableart.history;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.erhannis.android.orderednetworkview.DrawableNode;
import com.erhannis.android.orderednetworkview.Node;
import com.erhannis.unstableart.R;
import com.erhannis.unstableart.UAApplication;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

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

  /**
   * Rebuilds the preferred parents *OF THE CURRENT PREFERRED PATH*.
   * @param root
   * @param <T>
   */
  public static <T extends HistoryNode> void rebuildPreferredParents(T root) {
    HistoryNode cur = root;
    while (!cur.children.isEmpty()) {
      HistoryNode next = cur.children.getFirst();
      next.preferredParent = cur;
      cur = next;
    }
  }

  protected static Drawable procureDrawable(int id) {
    Drawable d = ContextCompat.getDrawable(UAApplication.getContext(), id);
    d.setBounds(-50, -50, 50, 50);
    return d;
  }

  //TODO This is a temporary default.
  private final Drawable DRAWABLE = procureDrawable(R.drawable.n_blank);
  @Override
  public Drawable getDrawable() {
    return DRAWABLE;
  }

  /*
  // Just because it's useful sometimes
  public static <T extends HistoryNode> void recurse(T root) {
    LinkedList<T> pending = new LinkedList<>();
    HashSet<T> added = new HashSet<>();

    pending.push(root);
    added.add(root);
    while (!pending.isEmpty()) {
      T item = pending.remove();
      for (T child : item.children) {
        if (!added.contains(child)) {
          // Do stuff
          added.add(child);
          pending.push(child);
        }
      }
    }
  }
  */
}
