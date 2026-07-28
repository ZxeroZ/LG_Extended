package com.lge.launcher3.allapps;

import android.view.KeyEvent;
import android.view.View;
import com.android.launcher3.FocusHelper;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsKeyEventListener implements View.OnKeyListener {
    @Override // android.view.View.OnKeyListener
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return FocusHelper.handleAppsCustomizeKeyEvent(v, keyCode, event);
    }
}
