package com.android.launcher3;

import android.view.KeyEvent;
import android.view.View;

/* JADX INFO: compiled from: FocusHelper.java */
/* JADX INFO: loaded from: classes.dex */
class HotseatIconKeyEventListener implements View.OnKeyListener {
    HotseatIconKeyEventListener() {
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return FocusHelper.handleHotseatButtonKeyEvent(v, keyCode, event);
    }
}
