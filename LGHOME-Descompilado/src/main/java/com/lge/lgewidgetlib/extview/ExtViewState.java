package com.lge.lgewidgetlib.extview;

import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public interface ExtViewState extends ExtViewEventListener {
    void cancelExtView();

    void clientExpandAnimationFinished();

    void clientRestoreAnimationFinished();

    boolean isExtViewAvailable();

    void notifyExtViewAvailable();

    void notifyStateChanged();

    void notifyWidgetDeleted();

    void requestBackgroundDimming(boolean enable, int endAlpha);

    void requestBackgroundDimming(boolean enable, int endAlpha, int duration);

    boolean requestExtView(View[] views);

    void requestNormalView();
}
