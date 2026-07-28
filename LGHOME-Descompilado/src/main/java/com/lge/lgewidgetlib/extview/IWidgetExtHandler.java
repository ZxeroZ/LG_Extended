package com.lge.lgewidgetlib.extview;

/* JADX INFO: loaded from: classes2.dex */
public interface IWidgetExtHandler {
    void cancelExtViewMode();

    boolean isExtViewMode();

    void notifyBindingStarted();

    void notifyExtViewAvailable();

    void notifyWidgetHostDestroyed();
}
