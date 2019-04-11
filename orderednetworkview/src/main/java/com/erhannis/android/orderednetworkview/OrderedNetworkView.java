package com.erhannis.android.orderednetworkview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Pair;
import android.view.View;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * //TODO There might be a better name
 */
public class OrderedNetworkView<T extends Node<T>> extends View {
  private MirrorNode<T> root; //TODO Final?
  private final HashMap<Marker, T> markerPositions = new HashMap<>();
  private HashMap<T, MirrorNode<T>> nodeToMirror;

  //TODO Should I include the other constructors?
  public OrderedNetworkView(Context context, T root, Marker[] markers) {
    super(context);
    reset(root, markers);
  }

  protected HashMap<T, MirrorNode<T>> mirrorRoot(MirrorNode<T> root) {
    LinkedList<MirrorNode<T>> pending = new LinkedList<>();
    HashMap<T, MirrorNode<T>> added = new HashMap<>();

    pending.push(root);
    added.put(root.mirror, root);
    while (!pending.isEmpty()) {
      MirrorNode<T> item = pending.remove();
      for (T child : item.mirror.children) {
        if (!added.containsKey(child)) {
          MirrorNode<T> mirror = new MirrorNode<>(child);
          added.put(child, mirror);
          pending.push(mirror);
        }
        item.children.add(added.get(child));
      }
    }

    return added;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    asdf;
  }

  /**
   * `parent` must already be in net.  If `child` exists, the two become linked, unless
   * already linked.  In either case, the new link becomes the most preferred.
   * If `child` does not exist, it will be added to the net.
   * However, its child nodes will not.  Please recursively add them, yourself.
   *
   * @param parent
   * @param child
   *
   * @throws IllegalArgumentException if parent is not in net
   */
  public void doAddLink(T parent, T child) {
    if (!nodeToMirror.containsKey(parent)) {
      throw new IllegalArgumentException("parent node was not found in net");
    }
    if (nodeToMirror.containsKey(child)) {
      MirrorNode<T> pm = nodeToMirror.get(parent);
      MirrorNode<T> cm = nodeToMirror.get(child);
      pm.children.remove(cm);
      pm.children.addFirst(cm);
    } else {
      MirrorNode<T> pm = nodeToMirror.get(parent);
      MirrorNode<T> cm = new MirrorNode<>(child);
      pm.children.addFirst(cm);
    }
  }

  //TODO Include marker positions?
  public void reset(T root, Marker[] markers) {
    this.root = new MirrorNode<>(root);
    this.nodeToMirror = mirrorRoot(this.root);

    for (Marker m : markers) {
      markerPositions.put(m, null);
    }
  }
}
