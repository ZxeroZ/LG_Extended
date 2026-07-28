package com.lge.launcher3.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class VdfDataPopupListMatcher {
    private static final String TAG = "FeatureMatcher";
    private static List<String> sDataPopupPackageList;

    public static boolean isDataConnectionPopupRequired(Context context, String packageName) {
        Log.v(TAG, "isDataConnectionPopupRequired: " + packageName);
        String str = LGFeatureConfig.FEATURE_OPERATOR;
        String str2 = LGFeatureConfig.FEATURE_COUNTRY;
        return checkTLFList(context, packageName, str, str2) || checkVDFList(context, packageName, str, str2);
    }

    public static boolean isRoamingDataPopupRequired(Context context, String packageName) {
        Log.v(TAG, "isDataConnectionPopupRequired: " + packageName);
        String str = LGFeatureConfig.FEATURE_OPERATOR;
        String str2 = LGFeatureConfig.FEATURE_COUNTRY;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null || !telephonyManager.isNetworkRoaming()) {
            return false;
        }
        return checkVDFList(context, packageName, str, str2);
    }

    private static boolean checkTLFList(Context context, String packageName, final String op, final String country) {
        if (!op.equals("TLF") || (!country.equals("EU") && !country.equals("ES") && !country.equals("COM"))) {
            return false;
        }
        if (sDataPopupPackageList == null) {
            sDataPopupPackageList = new ArrayList();
            String[] stringArray = LGHomeResources.getInstance(context).getStringArray("data_popup_packages_tlf");
            if (stringArray == null) {
                stringArray = context.getResources().getStringArray(R.array.data_popup_packages_tlf);
            }
            for (String str : stringArray) {
                sDataPopupPackageList.add(str);
            }
        }
        return sDataPopupPackageList.contains(packageName);
    }

    private static boolean checkVDFList(Context context, String packageName, final String op, final String country) {
        if (op.equals("VDF") && (country.equals("EU") || country.equals("ES") || country.equals("COM"))) {
            LGUsimInfo lGUsimInfo = LGUsimInfo.getInstance(context);
            String mcc = lGUsimInfo.getMcc();
            String mnc = lGUsimInfo.getMnc();
            if ("214".equals(mcc) && "01".equals(mnc)) {
                if (sDataPopupPackageList == null) {
                    sDataPopupPackageList = new ArrayList();
                    String[] stringArray = LGHomeResources.getInstance(context).getStringArray("data_popup_packages_vdf");
                    if (stringArray == null) {
                        stringArray = context.getResources().getStringArray(R.array.data_popup_packages_vdf);
                    }
                    for (String str : stringArray) {
                        sDataPopupPackageList.add(str);
                    }
                }
                return sDataPopupPackageList.contains(packageName);
            }
        }
        return false;
    }
}
