package com.lge.launcher3.operator;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.Launcher;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.VdfDataPopupConnectivityUtils;
import com.lge.launcher3.util.VdfDataPopupListMatcher;

/* JADX INFO: loaded from: classes.dex */
public class VDFDataPopup {
    private static final int REQUEST_DATA_CONNECTION_DIALOG = 20;
    private static final int REQUEST_DATA_CONNECTION_IN_ROAM_DIALOG = 21;
    private static final String TAG = "VDFDataPopup";

    public static void runActivityResultDataPopup(int requestCode, int resultCode, Launcher launcher, Intent pendingIntent) {
        if (requestCode != 20) {
            if (requestCode == 21) {
                LGLog.d(TAG, "REQUEST_DATA_CONNECTION_IN_ROAM_DIALOG: " + resultCode);
                if (pendingIntent != null) {
                    try {
                        launcher.startActivity(pendingIntent);
                        return;
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(launcher, R.string.activity_not_found, 0).show();
                        LGLog.e(TAG, TAG, e);
                        return;
                    } catch (SecurityException e2) {
                        Toast.makeText(launcher, R.string.activity_not_found, 0).show();
                        LGLog.d(TAG, "Launcher does not have the permission to launch " + pendingIntent + ". Make sure to create a MAIN intent-filter for the corresponding activity or use the exported attribute for this activity.", e2);
                        return;
                    }
                }
                return;
            }
            return;
        }
        LGLog.d(TAG, "REQUEST_DATA_CONNECTION_DIALOG: " + resultCode);
        if (pendingIntent != null) {
            if (!checkForDataInRoaming(launcher, PackageUtils.getPackageName(launcher, pendingIntent)) || resultCode == 1) {
                try {
                    launcher.startActivity(pendingIntent);
                } catch (ActivityNotFoundException e3) {
                    Toast.makeText(launcher, R.string.activity_not_found, 0).show();
                    LGLog.e(TAG, TAG, e3);
                } catch (SecurityException e4) {
                    Toast.makeText(launcher, R.string.activity_not_found, 0).show();
                    LGLog.d(TAG, "Launcher does not have the permission to launch " + pendingIntent + ". Make sure to create a MAIN intent-filter for the corresponding activity or use the exported attribute for this activity.", e4);
                }
            }
        }
    }

    private static boolean checkForDataInRoaming(Activity activity, String packageName) {
        LGLog.d(TAG, "checkForDataInRoaming: required: " + VdfDataPopupListMatcher.isRoamingDataPopupRequired(activity, packageName) + ", enabled: " + VdfDataPopupConnectivityUtils.isDataInRoamingEnabled(activity));
        if (VdfDataPopupListMatcher.isRoamingDataPopupRequired(activity, packageName) && !VdfDataPopupConnectivityUtils.isDataInRoamingEnabled(activity)) {
            Intent intent = new Intent();
            intent.setClassName("com.lge.android.connectionmanager.widget", "com.lge.android.connectionmanager.widget.RoamingConnectionRequestDialogActivity");
            try {
                activity.startActivityForResult(intent, 21);
                return true;
            } catch (ActivityNotFoundException unused) {
                LGLog.w(TAG, "Connection Widget is not installed.", new int[0]);
            }
        }
        return false;
    }

    public static boolean startDataConnectionDialogActivity(Activity activity, Intent intent, View v, Object tag) {
        String str;
        ResolveInfo resolveInfoResolveActivity = activity.getPackageManager().resolveActivity(intent, 65536);
        if (resolveInfoResolveActivity != null) {
            str = resolveInfoResolveActivity.activityInfo.packageName;
            LGLog.d(TAG, resolveInfoResolveActivity.activityInfo.packageName);
        } else {
            str = null;
        }
        if (str == null || VdfDataPopupConnectivityUtils.getWifiConnectionState(activity) || !VdfDataPopupConnectivityUtils.isSimStateReady(activity)) {
            return false;
        }
        if (!VdfDataPopupListMatcher.isDataConnectionPopupRequired(activity, str) || VdfDataPopupConnectivityUtils.isMobileDataEnabled(activity)) {
            return checkForDataInRoaming(activity, str);
        }
        Intent intent2 = new Intent();
        intent2.setClassName("com.lge.android.connectionmanager.widget", "com.lge.android.connectionmanager.widget.ConnectionRequiredDialogActivity");
        try {
            activity.startActivityForResult(intent2, 20);
            return true;
        } catch (ActivityNotFoundException unused) {
            LGLog.w(TAG, "Connection Widget is not installed.", new int[0]);
            return true;
        }
    }
}
