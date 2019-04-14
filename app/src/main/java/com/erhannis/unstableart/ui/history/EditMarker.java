package com.erhannis.unstableart.ui.history;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.erhannis.unstableart.R;

public class EditMarker extends UAMarker {
    public EditMarker() {
        //TODO Proper icon
        super(alterDrawable(procureDrawable(R.drawable.m_edit)), "Edit");
    }

    private static Drawable alterDrawable(Drawable drawable) {
        drawable.setColorFilter(0xFF00FF00, PorterDuff.Mode.SRC_IN);
        return drawable;
    }
}
