package com.erhannis.unstableart.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by erhannis on 5/13/17.
 */

public class Spacer extends View {
  public Spacer(Context context, int color) {
    super(context);
    ViewGroup.LayoutParams lp = getLayoutParams();
    if (lp == null) {
      lp = new ViewGroup.LayoutParams(0, 0);
    }
    lp.height = 10;
    lp.width = 10;
    setLayoutParams(lp);
    setBackgroundColor(color);
  }
}
