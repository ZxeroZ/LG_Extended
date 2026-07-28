package com.lge.launcher3.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class NetworkUtils {
    public static final String TAG = "NetworkUtils";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager;
        if (context == null || (connectivityManager = (ConnectivityManager) context.getSystemService("connectivity")) == null) {
            return false;
        }
        ArrayList<NetworkInfo> arrayList = new ArrayList();
        arrayList.add(connectivityManager.getNetworkInfo(0));
        arrayList.add(connectivityManager.getNetworkInfo(1));
        LGLog.d(TAG, String.format("isNetworkConnected() : %s", arrayList));
        for (NetworkInfo networkInfo : arrayList) {
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }
}
