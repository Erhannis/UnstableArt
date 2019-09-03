package com.erhannis.unstableart.ui.history;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.erhannis.unstableart.R;

public class AnchorMarker extends UAMarker {
    public AnchorMarker() {
        //TODO Proper icon
        super(alterDrawable(procureDrawable(R.drawable.m_anchor)), "Anchor");
    }

    private static Drawable alterDrawable(Drawable drawable) {
        drawable.setColorFilter(0xFFFF00FF, PorterDuff.Mode.SRC_IN);  //TODO Setting
        return drawable;
    }
}
