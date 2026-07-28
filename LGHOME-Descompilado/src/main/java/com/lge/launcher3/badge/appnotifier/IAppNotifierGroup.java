package com.lge.launcher3.badge.appnotifier;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public interface IAppNotifierGroup {
    void onUpdateAppNotifier(int count);

    AppNotifierDrawer registerAppNotifier(IAppNotifierGroup view, ArrayList<AppNotifierData> components);
}
