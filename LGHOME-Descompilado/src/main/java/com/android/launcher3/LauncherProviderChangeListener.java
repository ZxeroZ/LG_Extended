package com.android.launcher3;

/* JADX INFO: loaded from: classes.dex */
public interface LauncherProviderChangeListener {
    void onAppWidgetHostReset();

    void onDeleteAppWidgetIds(int[] ids);

    void onLauncherProviderChange();

    void onSettingsChanged(String settings, boolean value);
}
