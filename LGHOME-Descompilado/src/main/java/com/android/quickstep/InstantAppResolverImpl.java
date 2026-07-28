package com.android.quickstep;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.util.InstantAppResolver;

/* JADX INFO: loaded from: classes.dex */
public class InstantAppResolverImpl extends InstantAppResolver {
    public static final String COMPONENT_CLASS_MARKER = "@instantapp";
    private static final String TAG = "InstantAppResolverImpl";
    private final PackageManager mPM;

    public InstantAppResolverImpl(Context context) {
        this.mPM = context.getPackageManager();
    }

    @Override // com.android.launcher3.util.InstantAppResolver
    public boolean isInstantApp(ApplicationInfo info) {
        return info.isInstantApp();
    }

    @Override // com.android.launcher3.util.InstantAppResolver
    public boolean isInstantApp(AppInfo info) {
        ComponentName targetComponent = info.getTargetComponent();
        return targetComponent != null && targetComponent.getClassName().equals(COMPONENT_CLASS_MARKER);
    }
}
