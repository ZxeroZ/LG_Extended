package com.lge.launcher3.homesettings;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.preference.Preference;
import android.util.Log;
import com.lge.launcher3.BuildConfig;
import com.lge.launcher3.R;
import com.lge.mdm.LGMDMManager;

/* JADX INFO: loaded from: classes.dex */
public class LGMDMUtils {
    private static final String ACTION_CHANGE_DEFAULT_LAUNCHER = "com.lge.mdm.intent.action.CHANGE_DEFAULT_LAUNCHER";
    private static final String TAG = "LGMDMUtils";

    public static void registerLGMDMFilter(Context context, BroadcastReceiver receiver) {
        if (context == null || receiver == null || !Build.BRAND.equals(BuildConfig.FLAVOR_app) || "5.2.0".compareTo(getMDMVersion()) > 0) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CHANGE_DEFAULT_LAUNCHER);
        context.registerReceiver(receiver, intentFilter);
    }

    public static void unregisterLGMDMFilter(Context context, BroadcastReceiver receiver) {
        if (context == null || receiver == null || !Build.BRAND.equals(BuildConfig.FLAVOR_app) || "5.2.0".compareTo(getMDMVersion()) > 0) {
            return;
        }
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.w(TAG, "mLGMDMReceiver unregisterReceiver ", e);
        }
    }

    public static boolean receiveLGMDMIntentAction(Intent intent) {
        return intent != null && ACTION_CHANGE_DEFAULT_LAUNCHER.equals(intent.getAction());
    }

    public static void changeHomeSelectorPreference(Preference homeSelPreference) {
        if (homeSelPreference != null && Build.BRAND.equals(BuildConfig.FLAVOR_app) && LGMDMManager.getInstance().getEnforceDefaultLauncher((ComponentName) null)) {
            homeSelPreference.setSummary(R.string.sp_lgmdm_restrict_feature_NORMAL);
            homeSelPreference.setEnabled(false);
        }
    }

    public static String getMDMVersion() {
        try {
            return LGMDMManager.getInstance().getMDMVersion();
        } catch (NoClassDefFoundError e) {
            Log.w(TAG, e.getMessage());
            return "";
        }
    }
}
