package com.lge.launcher3.debug;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class EventLogger {
    private static final String TAG = "Event";
    public static final boolean VERBOSE = !"user".equals(Build.TYPE);
    public static String sPackageVersion = null;
    public static String sElapsedForLauncherCreation = null;
    public static String sElapsedForLauncherBinding = null;

    public static void initStaticValues() {
        sElapsedForLauncherCreation = null;
        sElapsedForLauncherBinding = null;
    }

    public static void initPackageVersion(final Context context) {
        if (sPackageVersion != null) {
            return;
        }
        new Thread(new Runnable() { // from class: com.lge.launcher3.debug.EventLogger.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    EventLogger.sPackageVersion = packageInfo.versionName + "(" + packageInfo.versionCode + ")";
                } catch (PackageManager.NameNotFoundException unused) {
                    LGLog.w(EventLogger.TAG, "Failed to init package version", new int[0]);
                }
            }
        }).start();
    }
}
