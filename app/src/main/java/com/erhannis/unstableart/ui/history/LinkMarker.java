package com.erhannis.unstableart.ui.history;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.erhannis.unstableart.R;

public class LinkMarker extends UAMarker {
    public LinkMarker() {
        //TODO Proper icon
        super(alterDrawable(procureDrawable(R.drawable.m_link)), "Link");
    }

    private static Drawable alterDrawable(Drawable drawable) {
        drawable.setColorFilter(0xFF0000FF, PorterDuff.Mode.SRC_IN);  //TODO Setting
        return drawable;
    }
}
