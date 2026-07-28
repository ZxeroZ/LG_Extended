package com.android.launcher3;

import android.view.KeyEvent;
import android.view.View;

/* JADX INFO: compiled from: FocusHelper.java */
/* JADX INFO: loaded from: classes.dex */
class IconKeyEventListener implements View.OnKeyListener {
    IconKeyEventListener() {
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return FocusHelper.handleIconKeyEvent(v, keyCode, event);
    }
}
