package com.lge.launcher3.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import com.lge.uicc.LGUiccManager;

/* JADX INFO: loaded from: classes.dex */
public class LGUsimInfo {
    static final String TAG = "LGUsimInfo";
    private static LGUsimInfo sLGUsimInfo;
    private TelephonyManager mTelephony;

    public LGUsimInfo(Context context) {
        this.mTelephony = (TelephonyManager) context.getSystemService("phone");
    }

    public static LGUsimInfo getInstance(Context context) {
        if (sLGUsimInfo == null) {
            sLGUsimInfo = new LGUsimInfo(context);
        }
        return sLGUsimInfo;
    }

    public void onDestroy() {
        this.mTelephony = null;
        sLGUsimInfo = null;
    }

    public String getMcc() {
        TelephonyManager telephonyManager = this.mTelephony;
        if (telephonyManager != null) {
            String simOperator = telephonyManager.getSimOperator();
            if (simOperator == null || simOperator.length() < 5) {
                return null;
            }
            return simOperator.substring(0, 3);
        }
        LGLog.d(TAG, "mTelephony is null");
        return null;
    }

    public String getMnc() {
        TelephonyManager telephonyManager = this.mTelephony;
        if (telephonyManager != null) {
            String simOperator = telephonyManager.getSimOperator();
            if (simOperator == null || simOperator.length() < 5) {
                return null;
            }
            return simOperator.substring(3);
        }
        LGLog.d(TAG, "mTelephony is null");
        return null;
    }

    public String getSpn() {
        TelephonyManager telephonyManager = this.mTelephony;
        if (telephonyManager != null) {
            String simOperatorName = telephonyManager.getSimOperatorName();
            if (simOperatorName == null || simOperatorName.length() == 0) {
                return null;
            }
            return simOperatorName;
        }
        LGLog.d(TAG, "mTelephony is null");
        return null;
    }

    public String getIccid() {
        TelephonyManager telephonyManager = this.mTelephony;
        if (telephonyManager != null) {
            String simSerialNumber = telephonyManager.getSimSerialNumber();
            if (simSerialNumber == null || simSerialNumber.length() == 0) {
                return null;
            }
            return simSerialNumber;
        }
        LGLog.d(TAG, "mTelephony is null");
        return null;
    }

    public String getGid() {
        return LGUiccManager.getProperty("gid1", (String) null);
    }

    public String getImsi() {
        TelephonyManager telephonyManager = this.mTelephony;
        if (telephonyManager != null) {
            String subscriberId = telephonyManager.getSubscriberId();
            if (subscriberId == null || subscriberId.length() == 0) {
                return null;
            }
            return subscriberId;
        }
        LGLog.d(TAG, "mTelephony is null");
        return null;
    }

    public int getSP() {
        String imsi = getImsi();
        if (imsi != null) {
            try {
                if (imsi.length() < 9) {
                    LGLog.i(TAG, "imsiValue length is = " + imsi.length());
                    return -1;
                }
                return Integer.parseInt(imsi.substring(7, 9));
            } catch (NumberFormatException e) {
                LGLog.e(TAG, "IMSI value is not integer", e);
            }
        }
        return -1;
    }

    public int getSimState() {
        return this.mTelephony.getSimState();
    }

    public String toString() {
        return "[USIM] Mcc : " + getMcc() + ", Mnc : " + getMnc() + ", Spn : " + getSpn() + ", Iccid : " + getIccid() + ", Gid : " + getGid() + ", Imsi : " + getImsi() + ", SP : " + getSP();
    }

    public boolean equalComparatorMcc(String compValue) {
        String mcc = getMcc();
        if (mcc == null || compValue == null) {
            return false;
        }
        return mcc.equals(compValue);
    }

    public boolean equalComparatorMnc(String compValue) {
        String mnc = getMnc();
        if (mnc == null || compValue == null) {
            return false;
        }
        return mnc.equals(compValue);
    }

    public boolean equalComparatorGIDWithLength(String compValue, int length) {
        String gid = getGid();
        if (gid == null || compValue == null) {
            return false;
        }
        if (gid.length() < length) {
            LGLog.i(TAG, "gidValue length is = " + gid.length());
            return false;
        }
        return compValue.substring(0, length).equalsIgnoreCase(gid.substring(0, length));
    }
}
