package com.android.systemui.plugins.shared;

import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes.dex */
public interface LauncherExterns {
    SharedPreferences getDevicePrefs();

    SharedPreferences getSharedPrefs();

    void runOnOverlayHidden(Runnable runnable);
}
