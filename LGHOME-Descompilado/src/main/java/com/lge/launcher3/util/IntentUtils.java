package com.lge.launcher3.util;

import android.content.ComponentName;
import android.content.Intent;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class IntentUtils {
    public static final String getTargetPackage(Intent intent) {
        if (intent == null) {
            return null;
        }
        String str = intent.getPackage();
        if (str != null) {
            return str;
        }
        ComponentName component = intent.getComponent();
        if (component == null) {
            return null;
        }
        return component.getPackageName();
    }

    public static boolean hasCategoryLauncher(Intent intent) {
        Set<String> categories;
        return (intent == null || (categories = intent.getCategories()) == null || !categories.contains("android.intent.category.LAUNCHER")) ? false : true;
    }

    public static Intent makeLGSettingIntent() {
        Intent intent = new Intent("android.settings.SETTINGS");
        intent.setFlags(270532608);
        intent.setPackage("com.android.settings");
        LGLog.d("IntentUtils", "com.android.settings: " + intent);
        return intent;
    }

    public static Intent makeLGHomeSettingIntent() {
        Intent intent = new Intent("com.lge.launcher3.intent.action.SHOW_SETTING");
        intent.setFlags(270532608);
        intent.setPackage("com.lge.launcher3");
        LGLog.d("IntentUtils", "com.android.settings: " + intent);
        return intent;
    }
}
