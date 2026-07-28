package com.lge.launcher3.allapps;

/* JADX INFO: loaded from: classes.dex */
public interface CellLayoutListener {
    void onEndMiniView(int index);

    void onPageBeginMoving(int currentIndex, int nextIndex);

    void onPageEndMoving(int currentIndex, int nextIndex);

    void setCurrentPage();
}
