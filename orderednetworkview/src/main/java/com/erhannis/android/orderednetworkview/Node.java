package com.erhannis.android.orderednetworkview;

import android.graphics.drawable.Drawable;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public abstract class Node<T extends Node> { // This is AMAZING jiggerypokery
  // The elements are defined to be in descending priority order
  public final LinkedList<T> children = new LinkedList<>();
}
