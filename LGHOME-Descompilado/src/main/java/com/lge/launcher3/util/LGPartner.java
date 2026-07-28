package com.lge.launcher3.util;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Pair;
import com.android.launcher3.Partner;

/* JADX INFO: loaded from: classes.dex */
public class LGPartner extends Partner {
    private static final String TAG = "LGPartner";
    private String mFilePath;

    private LGPartner(String packageName, Resources res, String filepath) {
        super(packageName, res);
        this.mFilePath = filepath;
    }

    @Override // com.android.launcher3.Partner
    public String getFilePath() {
        return this.mFilePath;
    }

    public static synchronized Partner getforPackageName(PackageManager pm, String packageName, String filepath) {
        Pair<String, Resources> pairFindApk = Utilities.findApk(packageName, pm);
        LGLog.i(TAG, "getforPackageName() : packageName = " + packageName + " filepath = " + filepath + " apkInfo = " + pairFindApk);
        if (pairFindApk == null) {
            return null;
        }
        return new LGPartner((String) pairFindApk.first, (Resources) pairFindApk.second, filepath);
    }
}
