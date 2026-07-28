package com.lge.launcher3.droptarget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.android.launcher3.ShortcutInfo;
import com.lge.launcher3.sortappsby.SortAppsByItemInfo;
import com.lge.launcher3.util.IntentUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class ButtonDropTargetUtils {
    public static final String TAG = "ButtonDropTargetUtils";

    public static boolean isShortcutWithApplicationType(Context context, Object info) {
        if (info == null || !(info instanceof ShortcutInfo)) {
            return false;
        }
        ShortcutInfo shortcutInfo = (ShortcutInfo) info;
        if (shortcutInfo.itemType != 0) {
            return false;
        }
        if (shortcutInfo.isPromise() && (shortcutInfo.hasStatusFlag(2) || shortcutInfo.hasStatusFlag(256))) {
            return false;
        }
        Intent intent = shortcutInfo.getIntent();
        if (!IntentUtils.hasCategoryLauncher(intent)) {
            return true;
        }
        if ((shortcutInfo.runtimeStatusFlags & 1) != 0) {
            return false;
        }
        return isShortcutWithApplicationType(context, intent.getComponent(), shortcutInfo.user.getIdentifier());
    }

    public static boolean isShortcutWithApplicationType(Context context, SortAppsByItemInfo info) {
        if (info == null || info.getItemType() != 0) {
            return false;
        }
        Intent intent = info.getIntent();
        if (IntentUtils.hasCategoryLauncher(intent)) {
            return isShortcutWithApplicationType(context, intent.getComponent(), info.getUserHandle().getIdentifier());
        }
        return true;
    }

    public static boolean isShortcutWithApplicationType(Context context, ComponentName component, int userId) {
        if (component == null) {
            return false;
        }
        String packageName = component.getPackageName();
        String className = component.getClassName();
        if (PackageUtils.isPackageOnSdcard(context, packageName)) {
            return false;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(packageName);
            Iterator it = packageManager.queryIntentActivitiesAsUser(intent, 0, userId).iterator();
            while (it.hasNext()) {
                if (className.equals(((ResolveInfo) it.next()).activityInfo.name)) {
                    return false;
                }
            }
        } catch (SecurityException e) {
            LGLog.i(TAG, String.format("isShortcutWithApplicationType() : SecurityException(%s)", e.toString()));
        }
        return true;
    }
}
