package com.lge.launcher3.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.lge.launcher3.config.LauncherConst;
import com.lge.os.Build;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GiftBoxManager {
    private static final Uri KTZRADATPER_CONTENT_URI = Uri.parse("content://com.lge.ktzradapter.provider/appInfo");
    private static final int NETWORK_CLASS_2_G = 1;
    private static final int NETWORK_CLASS_3_G = 2;
    private static final int NETWORK_CLASS_4_G = 3;
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    private static final String TAG = "GiftBoxManager";
    private static final int TELEPHONY_NR_STATE_5G = 3;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final OnDataFreeAppUpdateListener mListener;
    private final TelephonyManager mTelephonyManager;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: com.lge.launcher3.util.GiftBoxManager.1
        @Override // android.telephony.PhoneStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            LGLog.d(GiftBoxManager.TAG, "onDataConnectionStateChanged");
            GiftBoxManager giftBoxManager = GiftBoxManager.this;
            giftBoxManager.onChangedNetworkState(giftBoxManager.mContext);
        }

        @Override // android.telephony.PhoneStateListener
        public void onDataConnectionStateChanged(int state, int networkType) {
            super.onDataConnectionStateChanged(state, networkType);
            LGLog.d(GiftBoxManager.TAG, "onDataConnectionStateChanged");
            GiftBoxManager giftBoxManager = GiftBoxManager.this;
            giftBoxManager.onChangedNetworkState(giftBoxManager.mContext);
        }
    };
    private ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.lge.launcher3.util.GiftBoxManager.2
        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onAvailable(Network network) {
            LGLog.d(GiftBoxManager.TAG, "onAvailable");
            GiftBoxManager giftBoxManager = GiftBoxManager.this;
            giftBoxManager.onChangedNetworkState(giftBoxManager.mContext);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            LGLog.d(GiftBoxManager.TAG, "onLost");
            GiftBoxManager giftBoxManager = GiftBoxManager.this;
            giftBoxManager.onChangedNetworkState(giftBoxManager.mContext);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onUnavailable() {
            LGLog.d(GiftBoxManager.TAG, "onUnavailable");
            GiftBoxManager giftBoxManager = GiftBoxManager.this;
            giftBoxManager.onChangedNetworkState(giftBoxManager.mContext);
        }
    };

    public interface OnDataFreeAppUpdateListener {
        void onUpdatedDataFreeApps(List<String> dataFreeApps);
    }

    public static int getNetworkClass(int networkType) {
        switch (networkType) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
            case 16:
                return 1;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
            case 17:
                return 2;
            case 13:
            case 18:
            case 19:
                return 3;
            default:
                return 0;
        }
    }

    private static String getNetworkClassName(int networkClass) {
        return networkClass != 0 ? networkClass != 1 ? networkClass != 2 ? networkClass != 3 ? "Not defined" : "NETWORK_CLASS_4_G" : "NETWORK_CLASS_3_G" : "NETWORK_CLASS_2_G" : "NETWORK_CLASS_UNKNOWN";
    }

    public GiftBoxManager(Context context, OnDataFreeAppUpdateListener listener) {
        this.mContext = context.getApplicationContext();
        this.mListener = listener;
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
    }

    List<String> getDataFreeApps(Context context) {
        ArrayList arrayList = new ArrayList();
        if (!isProperToSupportDataFree(context)) {
            return arrayList;
        }
        try {
            Cursor cursorQuery = context.getContentResolver().query(KTZRADATPER_CONTENT_URI, new String[]{LauncherConst.EXTRA_PACKAGE_NAME}, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        do {
                            arrayList.add(cursorQuery.getString(0));
                        } while (cursorQuery.moveToNext());
                    }
                } finally {
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (SQLException e) {
            LGLog.w(TAG, e.getMessage(), new int[0]);
        }
        LGLog.d(TAG, "dataFreeApps : " + arrayList);
        return arrayList;
    }

    public static boolean isProperToSupportDataFree(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Uri uri = KTZRADATPER_CONTENT_URI;
        if (packageManager.resolveContentProvider(uri.getAuthority(), 0) == null) {
            LGLog.d(TAG, "Not support " + uri);
            return false;
        }
        boolean zIsAirplaneModeEnabled = isAirplaneModeEnabled(context);
        boolean zIsKTSimType = isKTSimType();
        boolean zIsMPTCPOn = isMPTCPOn(context);
        boolean zIsRoamingEnable = isRoamingEnable(context);
        NetworkCapabilities networkCapability = getNetworkCapability(context);
        LGLog.d(TAG, "isAirplaneModeEnabled? " + zIsAirplaneModeEnabled + ", isKTSimType? " + zIsKTSimType + ", isMPTCPOn? " + zIsMPTCPOn + ", isRoamingEnable? " + zIsRoamingEnable + ", capabilities? " + networkCapability);
        if (isAirplaneModeEnabled(context) || !isKTSimType() || zIsMPTCPOn || zIsRoamingEnable || networkCapability == null) {
            return false;
        }
        if (networkCapability.hasTransport(1)) {
            return true;
        }
        return networkCapability.hasTransport(0) && isGreaterThanOrEqualTo4G(context);
    }

    private static boolean isKTSimType() {
        return "KT".equals(SystemProperties.get("product.lge.ril.card_operator"));
    }

    private static boolean isMPTCPOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "giga_multipass_switch_status", 0) == 1;
    }

    private static boolean isRoamingEnable(Context context) {
        long defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        boolean zIsNetworkRoaming = telephonyManager == null ? false : telephonyManager.isNetworkRoaming((int) defaultSubscriptionId);
        if ("KR".equals(Build.CA_TARGET.COUNTRY)) {
            return zIsNetworkRoaming || "true".equals(SystemProperties.get("persist.product.lge.radio.isroaming"));
        }
        return zIsNetworkRoaming;
    }

    private static boolean isAirplaneModeEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    private static NetworkCapabilities getNetworkCapability(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            return connectivityManager.getNetworkCapabilities(activeNetwork);
        }
        return null;
    }

    private static boolean isGreaterThanOrEqualTo4G(Context context) {
        ServiceState serviceState = ((TelephonyManager) context.getSystemService("phone")).getServiceState();
        int dataRegState = serviceState.getDataRegState();
        int dataNetworkType = serviceState.getDataNetworkType();
        int networkClass = getNetworkClass(dataNetworkType);
        int nrStatus = serviceState.getNrStatus();
        boolean z = 3 == nrStatus;
        LGLog.d(TAG, "dataRegState? " + ServiceState.rilServiceStateToString(dataRegState) + ", dateNetworkType? " + TelephonyManager.getNetworkTypeName(dataNetworkType) + ", networkClass? " + getNetworkClassName(networkClass) + ", nrStatus? " + nrStatus + ", is5G? " + z);
        return dataRegState == 0 && (z || networkClass == 3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onChangedNetworkState(Context context) {
        this.mListener.onUpdatedDataFreeApps(getDataFreeApps(context));
    }

    public void registerNetworkCallback(Context context) {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 65);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(0).addTransportType(1).addTransportType(4).removeCapability(15);
        connectivityManager.registerNetworkCallback(builder.build(), this.mNetworkCallback);
        LGLog.d(TAG, "");
    }

    public void unregisterNetworkCallback() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
        LGLog.d(TAG, "");
    }
}
