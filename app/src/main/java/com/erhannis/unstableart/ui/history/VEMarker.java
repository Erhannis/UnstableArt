package com.erhannis.unstableart.ui.history;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.erhannis.unstableart.R;

public class VEMarker extends UAMarker {
    public VEMarker() {
        //TODO Proper icon
        super(alterDrawable(procureDrawable(R.drawable.m_view_edit)), "View/Edit");
    }

    private static Drawable alterDrawable(Drawable drawable) {
        drawable.setColorFilter(0xFF00FFFF, PorterDuff.Mode.SRC_IN);  //TODO Setting
        return drawable;
    }
}
