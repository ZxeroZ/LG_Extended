package com.android.launcher3;

import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.StylusEventHelper;

/* JADX INFO: loaded from: classes.dex */
public class SimpleOnStylusPressListener implements StylusEventHelper.StylusButtonListener {
    private View mView;

    @Override // com.android.launcher3.StylusEventHelper.StylusButtonListener
    public boolean onReleased(MotionEvent event) {
        return false;
    }

    public SimpleOnStylusPressListener(View view) {
        this.mView = view;
    }

    @Override // com.android.launcher3.StylusEventHelper.StylusButtonListener
    public boolean onPressed(MotionEvent event) {
        return this.mView.isLongClickable() && this.mView.performLongClick();
    }
}
