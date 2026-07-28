package com.lge.launcher3.config;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.lge.launcher3.util.Utilities;
import com.lge.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class LGFeatureConfig {
    public static final String FEATURE_COUNTRY;
    public static final String FEATURE_OPERATOR;
    public static final boolean FEATURE_ENABLE_LGLOG = !"user".equals(Build.TYPE);
    public static boolean sDebugWidgetSize = false;
    public static boolean sDebugLauncherModel = false;
    public static boolean sDebugMemoryTracking = false;
    public static boolean sDebugPorfileStatup = false;
    public static boolean sDebugOccupiedCell = false;

    static {
        String str;
        String str2 = "";
        try {
            str = Build.CA_TARGET.OPERATOR;
        } catch (NoClassDefFoundError unused) {
            Log.w("LGFeatureConfig", "LG lib not found");
            str = "";
        }
        FEATURE_OPERATOR = str;
        try {
            str2 = Build.CA_TARGET.COUNTRY;
        } catch (NoClassDefFoundError unused2) {
            Log.w("LGFeatureConfig", "LG lib not found");
        }
        FEATURE_COUNTRY = str2;
    }

    public static boolean isFolderPhone(Context context) {
        return context.getResources().getConfiguration().keyboard == 3;
    }

    public static boolean isLauncherFacadeOperator() {
        String str = FEATURE_OPERATOR;
        return str.equals("SPR") || (!Utilities.isOsuUpgraded() && (str.equals("TMO") || str.equals("DISH")));
    }
}
