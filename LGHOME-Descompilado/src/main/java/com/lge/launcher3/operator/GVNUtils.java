package com.lge.launcher3.operator;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.screeneffect.ScreenEffectUtils;

/* JADX INFO: loaded from: classes.dex */
public class GVNUtils {
    public static final String RO_LGE_GIOVANNA;
    private static String TAG = "com.lge.launcher3.operator.GVNUtils";

    static {
        RO_LGE_GIOVANNA = Build.VERSION.SDK_INT < 28 ? "ro.lge.giovanna" : "ro.product.lge.giovanna";
    }

    public static boolean isGiovanna(Context context) {
        return SystemProperties.getBoolean(RO_LGE_GIOVANNA, false);
    }

    public static boolean isGVNScreenEffectOn(Context context) {
        return isGiovanna(context) && ScreenEffectConst.ScreenEffectType.CAROUSEL.equals(ScreenEffectUtils.getSelectedScreenEffectType(context));
    }

    public static boolean isDisclosureEffectsEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "disclosure_effects_enabled", 0) == 1;
    }
}
