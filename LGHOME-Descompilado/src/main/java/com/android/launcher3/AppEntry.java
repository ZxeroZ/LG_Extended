package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class AppEntry {
    private static final String TAG = "AppEntry";
    private Drawable mIcon;
    private String mLabel;
    private Intent mLaunchIntent;

    AppEntry(ResolveInfo info, PackageManager packageManager) {
        String string = info.loadLabel(packageManager).toString();
        this.mLabel = string;
        LGLog.i(TAG, "create AppEntry - info : " + info + ", packageManager : " + packageManager + ", mLabel : " + string);
        this.mIcon = info.loadIcon(packageManager);
        Intent flags = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("android.intent.category.LAUNCHER").setFlags(270532608);
        this.mLaunchIntent = flags;
        flags.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
    }

    AppEntry(Context context, String packageName, String activityName) {
        String applicationLabel = PackageUtils.getApplicationLabel(context, packageName);
        this.mLabel = applicationLabel;
        LGLog.i(TAG, "create AppEntry - packageName : " + packageName + ", activityName : " + activityName + ", mLabel : " + applicationLabel);
        this.mIcon = PackageUtils.getApplicationIcon(context, packageName);
        Intent flags = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("android.intent.category.LAUNCHER").setFlags(270532608);
        this.mLaunchIntent = flags;
        flags.setComponent(new ComponentName(packageName, activityName));
    }

    String getLabel() {
        return this.mLabel;
    }

    Drawable getIcon() {
        return this.mIcon;
    }

    Intent getLaunchIntent() {
        return this.mLaunchIntent;
    }

    ComponentName getComponentName() {
        return this.mLaunchIntent.getComponent();
    }

    public String toString() {
        return this.mLabel;
    }
}
