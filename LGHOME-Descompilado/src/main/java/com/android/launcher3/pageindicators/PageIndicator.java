package com.android.launcher3.pageindicators;

/* JADX INFO: loaded from: classes.dex */
public interface PageIndicator {
    void setActiveMarker(int activePage);

    void setMarkersCount(int numMarkers);

    void setScroll(int currentScroll, int totalScroll);
}
