package com.lge.launcher3.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.net.Uri;
import android.os.UserHandle;
import com.android.launcher3.compat.LauncherAppsCompat;

/* JADX INFO: loaded from: classes.dex */
public class LauncherActivityInfoUtils {
    public static final String TAG = "LauncherActivityInfoUtils";

    public static LauncherActivityInfo getLauncherActivityInfo(Context context, Intent intent, UserHandle userHandle) {
        if (context == null || intent == null) {
            LGLog.d(TAG, String.format("context(%s), intent(%s)", context, intent));
            return null;
        }
        Intent intent2 = new Intent(intent.getAction(), (Uri) null);
        intent2.addCategory("android.intent.category.LAUNCHER");
        intent2.setComponent(intent.getComponent());
        return LauncherAppsCompat.getInstance(context).resolveActivity(intent2, userHandle);
    }
}
