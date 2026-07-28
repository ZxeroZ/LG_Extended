package com.android.launcher3;

/* JADX INFO: loaded from: classes.dex */
public interface DragScroller {
    boolean onEnterScrollArea(int x, int y, int direction);

    boolean onExitScrollArea();

    void scrollLeft();

    void scrollRight();
}
