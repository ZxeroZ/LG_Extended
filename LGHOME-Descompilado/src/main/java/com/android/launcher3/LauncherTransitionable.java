package com.android.launcher3;

/* JADX INFO: loaded from: classes.dex */
public interface LauncherTransitionable {
    void onLauncherTransitionEnd(Launcher l, boolean animated, boolean toWorkspace);

    void onLauncherTransitionPrepare(Launcher l, boolean animated, boolean toWorkspace);

    void onLauncherTransitionStart(Launcher l, boolean animated, boolean toWorkspace);

    void onLauncherTransitionStep(Launcher l, float t);
}
