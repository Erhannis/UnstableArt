package com.erhannis.arttraining;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by erhannis on 10/5/16.
 */
public class MiscUtils {
  public static float rateCircle(ArrayList<PointF> line, float[] circle) {
    //TODO Make better
    float score = 0;
    for (PointF p : line) {
      float r = (float)Math.sqrt(((circle[0] - p.x)*(circle[0] - p.x)) + ((circle[1] - p.y)*(circle[1] - p.y)));
      float diff = Math.abs(r - circle[2]);
      score += (1-(diff / circle[2]));
    }
    return 100 * (score / line.size());
  }

  public static float[] makeCircle(PointF center, float radius) {
    return new float[]{center.x, center.y, radius};
  }

  public static PointF findCircleCenter(ArrayList<PointF> line) {
    //TODO Make better than average
    //TODO Weight longer segments more?  And account for their straightness?
    float x = 0;
    float y = 0;
    for (PointF p : line) {
      x += p.x;
      y += p.y;
    }
    return new PointF(x / line.size(), y / line.size());
  }

  public static float findCircleRadius(ArrayList<PointF> line, PointF center) {
    //TODO Weight longer segments more?  And account for their straightness?
    //TODO Make more efficient
    float radius = 0;
    for (PointF p : line) {
      radius += Math.sqrt(((center.x - p.x)*(center.x - p.x)) + ((center.y - p.y)*(center.y - p.y)));
    }
    return (radius / line.size());
  }

  // From wikipedia
  // Three points are a counter-clockwise turn if ccw > 0, clockwise if
  // ccw < 0, and collinear if ccw = 0 because ccw is a determinant that
  // gives twice the signed  area of the triangle formed by p1, p2 and p3.
  public static float ccw(PointF p1, PointF p2, PointF p3) {
    return (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);
  }

  public static void swap(List l, int a, int b) {
    Object oa = l.get(a);
    Object ob = l.get(b);
    l.set(a, ob);
    l.set(b, oa);
  }

  public static double angle(PointF center, PointF p) {
    return Math.atan2(p.y - center.y, p.x - center.x);
  }

  // Also adapted from wikipedia
  public static List<PointF> convexHullGrahamScan(ArrayList<PointF> points) {
    points = new ArrayList<PointF>(points);
    //TODO Overkill
    Collections.sort(points, new Comparator<PointF>() {
      @Override
      public int compare(PointF lhs, PointF rhs) {
        if (lhs.y == rhs.y) {
          return Double.compare(lhs.x, rhs.x);
        } else {
          return Double.compare(lhs.y, rhs.y);
        }
      }
    });

    int n = points.size();
    //points[N+1] = the array of points
    //swap points[1] with the point with the lowest y-coordinate
    //sort points by polar angle with points[1]
    final PointF lowest = points.get(0);
    Collections.sort(points, new Comparator<PointF>() {
      @Override
      public int compare(PointF lhs, PointF rhs) {
        return Double.compare(angle(lowest, lhs), angle(lowest, rhs));
      }
    });

    // We want points[0] to be a sentinel point that will stop the loop.
    //let points[0] = points[N]
    //TODO Inefficient
    points.add(0, points.get(points.size() - 1));

    // M will denote the number of points on the convex hull.
    int m = 1;
    for (int i = 2; i <= n; i++) {
      // Find next valid point on convex hull.
      while (ccw(points.get(m - 1), points.get(m), points.get(i)) <= 0) {
        if (m > 1) {
          m -= 1;
          // All points are collinear
        } else if (i == n) {
          break;
        } else {
          i += 1;
        }
      }

      // Update M and swap points[i] to the correct place.
      m += 1;
      swap(points, m, i);
    }
    //TODO Remove doubled point?
    return points;
  }
}
