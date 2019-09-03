package com.erhannis.unstableart.ui.history;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.erhannis.unstableart.R;
import com.erhannis.android.orderednetworkview.Marker;
import com.erhannis.unstableart.UAApplication;

public class ViewMarker extends UAMarker {
    public ViewMarker() {
        //TODO Proper icon
        super(alterDrawable(procureDrawable(R.drawable.m_view)), "View");
    }

    private static Drawable alterDrawable(Drawable drawable) {
        drawable.setColorFilter(0xFF0000FF, PorterDuff.Mode.SRC_IN);  //TODO Setting
        return drawable;
    }
}
