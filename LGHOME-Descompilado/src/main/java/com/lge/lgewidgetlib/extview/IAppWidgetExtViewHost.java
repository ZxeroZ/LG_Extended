package com.lge.lgewidgetlib.extview;

import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public interface IAppWidgetExtViewHost {
    void clientExpandAnimationFinished();

    void clientRestoreAnimationFinished();

    int getExtendedWidgetHeight();

    boolean isExtViewAvailable();

    boolean isImprovedExtHost();

    boolean requestExtView(View[] views);

    void requestExtViewDimming(boolean enable, int endAlpha);

    void requestExtViewDimming(boolean enable, int endAlpha, int duration);

    void requestNormalView();
}
