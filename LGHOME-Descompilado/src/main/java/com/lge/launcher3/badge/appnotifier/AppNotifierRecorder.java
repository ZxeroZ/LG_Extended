package com.lge.launcher3.badge.appnotifier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class AppNotifierRecorder {
    private static final String SHAREDPREFERENCE_NAME = "launcher.appnotifier";
    private static final String TAG = "AppNotifier.Recorder";

    public static void saveAppNotifier(Context context, String key, int count) {
        SharedPreferences.Editor editorEdit = openSharedPreference(context).edit();
        Objects.requireNonNull(editorEdit, "can't open SharedPreference Editor");
        LGLog.i(TAG, String.format("saveAppNotifier() : component(%s), count(%d)", key, Integer.valueOf(count)));
        editorEdit.putInt(key, count);
        editorEdit.apply();
    }

    public static void removeAppNotifier(Context context, String packageName) {
        SharedPreferences.Editor editorEdit = openSharedPreference(context).edit();
        Objects.requireNonNull(editorEdit, "can't open SharedPreference Editor");
        Set<String> allName = getAllName(context);
        for (String str : allName) {
            if (str.startsWith(packageName)) {
                LGLog.d(TAG, "Remove {" + str + "}");
                editorEdit.remove(str);
            }
        }
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(packageName);
        Iterator<ResolveInfo> it = packageManager.queryIntentActivities(intent, 0).iterator();
        while (it.hasNext()) {
            String str2 = it.next().activityInfo.name;
            if (!str2.startsWith(packageName)) {
                for (String str3 : allName) {
                    if (str3.startsWith(str2)) {
                        LGLog.d(TAG, "Remove {" + str3 + "}");
                        editorEdit.remove(str3);
                    }
                }
            }
        }
        editorEdit.apply();
    }

    public static int loadAppNotifier(Context context, String key) {
        return openSharedPreference(context).getInt(key, 0);
    }

    private static SharedPreferences openSharedPreference(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPREFERENCE_NAME, 0);
        Objects.requireNonNull(sharedPreferences, "can't open SharedPreference");
        return sharedPreferences;
    }

    public static Set<String> getAllName(Context context) {
        return openSharedPreference(context).getAll().keySet();
    }
}
