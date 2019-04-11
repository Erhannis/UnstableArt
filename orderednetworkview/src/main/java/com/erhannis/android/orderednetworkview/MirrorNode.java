package com.erhannis.android.orderednetworkview;

public class MirrorNode<T> extends Node<MirrorNode> {
  public final T mirror;

  public MirrorNode(T mirror) {
    this.mirror = mirror;
  }
}
