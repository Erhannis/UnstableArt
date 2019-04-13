package com.erhannis.unstableart.ui.history;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.erhannis.unstableart.R;
import com.erhannis.android.orderednetworkview.Marker;
import com.erhannis.unstableart.UAApplication;

public class ViewMarker extends UAMarker {
    public ViewMarker() {
        //TODO Proper icon
        super(procureDrawable(R.drawable.m_view), "View");
    }
}
