package com.lge.launcher3.badge.appnotifier;

/* JADX INFO: loaded from: classes.dex */
public interface IAppNotifierView {
    void onUpdateAppNotifier(int count);

    AppNotifierDrawer registerAppNotifier(IAppNotifierView view, AppNotifierData appData);
}
