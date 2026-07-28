package com.lge.launcher3;

import android.os.Bundle;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class EasyHomeLauncherExtension extends LauncherExtension {
    private static final String TAG = "EasyHomeLauncherExtension";

    @Override // com.lge.launcher3.LauncherExtension, com.android.launcher3.uioverrides.QuickstepLauncher, com.android.launcher3.BaseQuickstepLauncher, com.android.launcher3.Launcher, com.android.launcher3.BaseDraggingActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        LGLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }
}
