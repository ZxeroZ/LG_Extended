package com.lge.launcher3.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.view.View;
import android.widget.Toast;
import com.lge.launcher3.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LGActivityUtil {
    private static final String TAG = "LGActivityUtil";
    private static ArrayList<PackageInfo> sExternalAppsList;

    public static boolean checkForDataInRoaming(Activity activity, String packageName) {
        return false;
    }

    public static boolean startActivityWithAnimation(Activity activity, Intent intent, View v, Object tag, boolean animation) {
        return false;
    }

    public static boolean startDataConnectionDialogActivity(Activity activity, Intent intent, View v, Object tag) {
        return true;
    }

    public static boolean isTopActivity(Context context, ComponentName componentName) {
        int iCompareTo;
        List<ActivityManager.RunningTaskInfo> runningTasks;
        String string = null;
        try {
            runningTasks = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
        } catch (NullPointerException e) {
            LGLog.i(TAG, "Running>>>>>" + string);
            LGLog.i(TAG, "ComponentName>>>>>" + componentName.toString());
            LGLog.w(TAG, "Null Referenced Exception in Top-Activity Info", e, new int[0]);
        }
        if (runningTasks == null || runningTasks.size() <= 0) {
            iCompareTo = 0;
        } else {
            string = runningTasks.get(0).topActivity.toString();
            iCompareTo = string.compareTo(componentName.toString());
        }
        return iCompareTo == 0;
    }

    private static void startActivity(Activity activity, Intent intent) {
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
            LGLog.e(TAG, "ActivityNotFoundException: intent= " + intent.getAction(), e);
            LGLog.d(TAG, "Unable to launch. tag=LGActivityUtil intent=" + intent);
        } catch (SecurityException e2) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
            LGLog.e(TAG, "SecurityException: intent= " + intent.getAction(), e2);
            LGLog.d(TAG, "Launcher does not have the permission to launch " + intent + ". Make sure to create a MAIN intent-filter for the corresponding activity or use the exported attribute for this activity. tag=LGActivityUtil intent=" + intent);
        }
    }

    public static boolean isSettingsActivity(Intent intent) {
        return intent.getComponent() != null && "com.android.settings.Settings".equals(intent.getComponent().getClassName());
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.lge.launcher3.util.LGActivityUtil$1] */
    public static void sendCleanViewActivatedIntent(final Activity activity) {
        new Thread() { // from class: com.lge.launcher3.util.LGActivityUtil.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Intent intent = new Intent();
                intent.setAction("com.lge.livewallpaper.cleanview.ACTIVATED");
                activity.sendBroadcast(intent);
            }
        }.start();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.lge.launcher3.util.LGActivityUtil$2] */
    public static void sendCleanViewDeactivatedIntent(final Activity activity) {
        new Thread() { // from class: com.lge.launcher3.util.LGActivityUtil.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Intent intent = new Intent();
                intent.setAction("com.lge.livewallpaper.cleanview.DEACTIVATED");
                activity.sendBroadcast(intent);
            }
        }.start();
    }

    public static void notifyUpgradedToWidget(Activity activity) {
        Intent intent = new Intent();
        intent.setAction("com.lge.launcher2.DB_UPGRADED");
        LGLog.i(TAG, "broadcast to widget for DB migration, intent: " + intent.getAction());
        activity.sendBroadcast(intent);
    }

    public static void notifyToWeatherWidget(Activity activity) {
        Intent intent = new Intent();
        intent.setAction("com.lge.launcher2.RESUME");
        LGLog.i(TAG, "broadcast to weather widget, intent: " + intent.getAction());
        activity.sendBroadcast(intent);
    }

    public static void clearExternalAppList() {
        if (sExternalAppsList != null) {
            sExternalAppsList = null;
        }
    }

    private static ArrayList<PackageInfo> refreshExternalAppList(Context context) {
        return new ArrayList<>();
    }

    public static boolean isExternalApp(Context context, String packageName) {
        ArrayList<PackageInfo> arrayListRefreshExternalAppList = sExternalAppsList;
        if (arrayListRefreshExternalAppList == null) {
            arrayListRefreshExternalAppList = refreshExternalAppList(context);
            sExternalAppsList = arrayListRefreshExternalAppList;
        }
        Iterator<PackageInfo> it = arrayListRefreshExternalAppList.iterator();
        while (it.hasNext()) {
            if (it.next().packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInstalledExternal(PackageInfo pi) {
        return (pi.applicationInfo.flags & 262144) != 0;
    }
}
