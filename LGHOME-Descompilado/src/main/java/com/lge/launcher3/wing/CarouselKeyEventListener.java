package com.lge.launcher3.wing;

import android.view.KeyEvent;
import android.view.View;
import com.android.launcher3.FocusHelper;

/* JADX INFO: loaded from: classes2.dex */
public class CarouselKeyEventListener implements View.OnKeyListener {
    @Override // android.view.View.OnKeyListener
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return FocusHelper.handleCarouselIconKeyEvent(v, keyCode, event);
    }
}
