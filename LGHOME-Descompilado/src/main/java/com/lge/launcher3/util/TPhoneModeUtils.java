package com.lge.launcher3.util;

import android.content.Context;
import android.content.Intent;
import android.telecom.DefaultDialerManager;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.config.LauncherConst;

/* JADX INFO: loaded from: classes.dex */
public class TPhoneModeUtils {
    private static final String TAG = "TPhoneModeUtils";

    public static int getDefaultPhoneMode(Context context) {
        return getPhoneMode(DefaultDialerManager.getDefaultDialerApplication(context));
    }

    public static int getPhoneMode(String packageName) {
        int i;
        if (LauncherConst.SKT_PHONE_PACKAGE_NAME.equals(packageName)) {
            i = 1;
        } else {
            i = "com.android.contacts".equals(packageName) ? 0 : 2;
        }
        LGLog.i(TAG, "defaultDialerPackageName = " + packageName + ", sktMode = " + i);
        return i;
    }

    public static void updateTPhoneMode(Context context) {
        Intent intent = new Intent(IntentConst.Action.ACTION_RELOAD_TPHONEMODE.getValue(context));
        int defaultPhoneMode = getDefaultPhoneMode(context);
        intent.putExtra("modeAfter", defaultPhoneMode);
        context.sendBroadcast(intent);
        LGLog.i(TAG, "updateTPhoneMode: modeAfter = " + defaultPhoneMode);
    }
}
