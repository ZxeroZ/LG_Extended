package com.lge.launcher3.homesettings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.PackageUtils;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class HomeSettingsUtils {
    public static boolean isLGHome(ResolveInfo resolveInfo) {
        if (resolveInfo == null) {
            return false;
        }
        return resolveInfo.activityInfo.packageName.startsWith("com.lge.launcher3");
    }

    public static boolean isEasyHome(ResolveInfo resolveInfo) {
        if (resolveInfo == null) {
            return false;
        }
        return resolveInfo.activityInfo.packageName.startsWith(LauncherConst.EASYHOME_PACKAGENAME);
    }

    public static boolean isHomeSelectorExist(Context context) {
        Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null);
        intent.setComponent(new ComponentName("com.lge.homeselector", "com.lge.homeselector.HomeSelector"));
        List<ResolveInfo> activityResolveInfoList = PackageUtils.getActivityResolveInfoList(context, intent);
        return activityResolveInfoList != null && activityResolveInfoList.size() > 0;
    }

    public static String getAppNameFromPackageName(String packageName) {
        int iLastIndexOf;
        if (packageName == null || (iLastIndexOf = packageName.lastIndexOf(".")) < 0) {
            return null;
        }
        String strSubstring = packageName.substring(iLastIndexOf + 1);
        String upperCase = strSubstring.toUpperCase(Locale.getDefault());
        int length = strSubstring.length();
        return upperCase.substring(0, 1) + strSubstring.substring(1, length);
    }

    public static final String[] getScreenEffectList(Context context) {
        return context.getResources().getStringArray(R.array.screen_effect);
    }
}
