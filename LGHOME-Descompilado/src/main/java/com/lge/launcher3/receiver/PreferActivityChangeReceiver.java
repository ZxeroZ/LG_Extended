package com.lge.launcher3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.homesettings.SettingsSearchUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class PreferActivityChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "PreferActivityChangeReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Bundle extras;
        if (intent.getAction() == null || (extras = intent.getExtras()) == null) {
            return;
        }
        IntentFilter intentFilter = (IntentFilter) extras.get("intentFilter");
        boolean z = intentFilter != null && intentFilter.hasCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME);
        String str = (String) extras.get(LauncherConst.EXTRA_PACKAGE_NAME);
        if (!z || str == null) {
            return;
        }
        LGLog.i(TAG, "Preferred Launcher changed: " + str);
        PackageUtils.setPrefHomeSetting(context, "com.lge.launcher3");
        if (str.equals("com.lge.launcher3")) {
            SettingsSearchUtils.updateSettingSearchDB(context, LGHomeFeature.isEnableDefaultHome(), true, false);
        } else if (str.equals(LauncherConst.EASYHOME_PACKAGENAME)) {
            SettingsSearchUtils.updateSettingSearchDB(context, false, false, true);
        } else {
            SettingsSearchUtils.updateSettingSearchDB(context, false, false, false);
        }
        PackageUtils.enableRecentUninstall(context, str);
    }
}
