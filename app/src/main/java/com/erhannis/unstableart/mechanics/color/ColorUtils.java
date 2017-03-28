package com.erhannis.unstableart.mechanics.color;

import com.erhannis.mathnstuff.MeUtils;

/**
 * Created by erhannis on 3/18/17.
 */
public class ColorUtils {
  //TODO Conversion functions
  public static double[] intARGBToDoublesARGB(int intARGB) {
    int[] intsARGB = MeUtils.intToARGB(intARGB);
    double[] doublesARGB = new double[]{intsARGB[0] / 255.0, intsARGB[1] / 255.0, intsARGB[2] / 255.0, intsARGB[3] / 255.0};
    return doublesARGB;
  }
}
