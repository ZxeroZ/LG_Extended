package com.lge.launcher3.allapps;

import android.view.MotionEvent;

/* JADX INFO: loaded from: classes.dex */
public interface IPageIndicator {
    boolean enabled();

    void flash();

    void hideScrollIndicator(boolean immediately);

    boolean isInterceptTouchEvent(MotionEvent ev);

    void showScrollIndicator(boolean immediately);

    void update(float offset, int pageWidth, int numPages);
}
