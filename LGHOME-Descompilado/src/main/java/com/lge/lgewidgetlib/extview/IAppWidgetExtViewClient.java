package com.lge.lgewidgetlib.extview;

/* JADX INFO: loaded from: classes2.dex */
public interface IAppWidgetExtViewClient {
    boolean isWidgetUpdateSkippable();

    void notifyBindingStarted();

    void notifyClickOutSide();

    void notifyExtViewAvailable();

    void notifyRequestExtViewException();

    void notifyWidgetDeleted();

    void notifyWidgetHostDestroyed();

    void notifyWidgetReset();

    void onExtViewModeCanceled();

    void onExtViewModeComplete();

    void onWidgetModeComplete();

    void setExtViewHost(IAppWidgetExtViewHost host);

    void setExtViewHost(Object host);
}
