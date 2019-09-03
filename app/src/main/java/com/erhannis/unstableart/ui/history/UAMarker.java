package com.erhannis.unstableart.ui.history;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.erhannis.android.orderednetworkview.Marker;
import com.erhannis.unstableart.UAApplication;

public class UAMarker extends Marker {
    public UAMarker(Drawable icon, String name) {
        super(icon, name);
    }

    protected static Drawable procureDrawable(int id) {
        Drawable d = ContextCompat.getDrawable(UAApplication.getContext(), id);
        d.setBounds(-25, -25, 25, 25);
        return d;
    }
}
