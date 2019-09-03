package com.erhannis.android.orderednetworkview;

import android.graphics.drawable.Drawable;
import android.support.v4.util.ObjectsCompat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class Node<T extends Node> { // This is AMAZING jiggerypokery
  // The elements are defined to be in descending priority order
  private final LinkedList<Object> children = new LinkedList<Object>();

  //TODO Once Kryo supports both good generics AND deep graphs, get rid of the accessor
  public LinkedList<T> children() {
    return (LinkedList<T>)(Object)this.children;
  }


  /**
   * Checks if `descendant` is recursively a child of `ancestor`.
   * @param ancestor
   * @param descendant
   * @return true if `descendant` is a descendant of `ancestor`.
   */
  public static <T extends Node<T>> boolean isAncestor(T ancestor, T descendant) {
    LinkedList<T> pending = new LinkedList<>();
    HashSet<T> added = new HashSet<>();

    pending.push(ancestor);
    added.add(ancestor);
    while (!pending.isEmpty()) {
      Node<T> item = pending.remove();
      for (T child : item.children()) {
        if (!added.contains(child)) {
          if (ObjectsCompat.equals(child, descendant)) {
            return true;
          } else {
            pending.push(child);
            added.add(child);
          }
        }
      }
    }

    return false;
  }

  public static <T extends Node<T>> List<T> findPreferredPath(T ancestor, T descendant) {
    LinkedList<T> nodeStack = new LinkedList<>();
    LinkedList<Iterator<T>> childStack = new LinkedList<>();
    HashSet<T> added = new HashSet<>();

    nodeStack.addLast(ancestor);
    childStack.addLast(ancestor.children().iterator());
    added.add(ancestor);
    while (!nodeStack.isEmpty()) {
      T curNode = nodeStack.getLast();
      if (ObjectsCompat.equals(curNode, descendant)) {
        return nodeStack;
      }
      Iterator<T> curIter = childStack.getLast();

      if (!curIter.hasNext()) {
        nodeStack.removeLast();
        childStack.removeLast();
      } else {
        T child = curIter.next();
        if (!added.contains(child)) {
          nodeStack.addLast(child);
          childStack.addLast(child.children().iterator());
          added.add(child);
        } // else do nothing and the iterator will tick up
      }
    }
    return null;
  }

  /**
   * Causes the most preferred path from `ancestor` to `descendant` to be
   * the most preferred path leading leafwards from `ancestor`.
   * If there is no path from `ancestor` to `descendant`, does nothing.
   * Returns true iff any orderings were changed.
   *
   * @param ancestor
   * @param descendant
   * @param <T>
   * @return true iff anything was changed
   */
  public static <T extends Node<T>> boolean preferPath(T ancestor, T descendant) {
    List<T> path = findPreferredPath(ancestor, descendant);
    if (path == null || path.isEmpty()) {
      return false;
    }
    Iterator<T> i = path.iterator();
    T cur = i.next();
    T next = null;
    boolean changed = false;
    while (i.hasNext()) {
      next = i.next();
      if (!ObjectsCompat.equals(cur.children().getFirst(), next)) {
        cur.children().remove(next);
        cur.children().addFirst(next);
        changed = true;
      }
      cur = next;
    }
    return changed;
  }

  /*
  // This just happens to be a useful template
  private static <T extends Node<T>> void traversePreferredPaths(T ancestor, T descendant) {
    LinkedList<T> nodeStack = new LinkedList<>();
    LinkedList<Iterator<T>> childStack = new LinkedList<>();
    HashSet<T> added = new HashSet<>();

    nodeStack.addLast(ancestor);
    childStack.addLast(ancestor.children.iterator());
    added.add(ancestor);
    while (true) {
      T curNode = nodeStack.getLast();
      Iterator<T> curIter = childStack.getLast();

      if (!curIter.hasNext()) {
        nodeStack.removeLast();
        childStack.removeLast();
      } else {
        T child = curIter.next();
        if (!added.contains(child)) {
          nodeStack.addLast(child);
          childStack.addLast(child.children.iterator());
          added.add(child);
        } // else do nothing and the iterator will tick up
      }
    }
  }
  /**/
}
