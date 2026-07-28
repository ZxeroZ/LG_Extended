package com.lge.launcher3.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class VdfDataPopupConnectivityUtils {
    private static final boolean LOGD = false;
    private static final int[] MOBILE_DATA_NETWORK_TYPES = {0, 4, 5};
    private static final String TAG = "ConnectivityUtils";

    public static boolean getWifiConnectionState(Context context) {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1);
            if (networkInfo != null) {
                return networkInfo.isConnected();
            }
            return false;
        } catch (Exception e) {
            Log.w(TAG, "", e);
            return false;
        }
    }

    public static boolean getMobileDataConnectionState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (!isMobileDataEnabled(context)) {
            return false;
        }
        try {
            for (int i : MOBILE_DATA_NETWORK_TYPES) {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(i);
                if (networkInfo == null) {
                    return false;
                }
                if (networkInfo.isConnected()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.w(TAG, "", e);
            return false;
        }
    }

    public static boolean isWiFiEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "wifi_on", 1) == 1;
    }

    public static boolean isMobileDataEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "mobile_data", 1) == 1;
    }

    public static boolean isDataInRoamingEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "data_roaming", 0) == 1;
    }

    public static void setWiFiState(Context context, boolean state) {
        ((WifiManager) context.getSystemService("wifi")).setWifiEnabled(state);
    }

    public static String[] getRequestedPermissions(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "", e);
            packageInfo = null;
        }
        return (packageInfo == null || packageInfo.requestedPermissions == null) ? new String[0] : packageInfo.requestedPermissions;
    }

    public static boolean isInternetPermissionRequested(Context context, String packageName) {
        String[] requestedPermissions = getRequestedPermissions(context, packageName);
        if (requestedPermissions != null) {
            for (String str : requestedPermissions) {
                if (str.equals("android.permission.INTERNET")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSimStateReady(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getSimState() == 5;
    }
}
