package com.lge.launcher3.allapps;

import com.lge.launcher3.allapps.AllAppsDBProvider;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class SwivelAllAppsDBProvider extends AllAppsDBProvider {
    public static final String DATABASE_NAME = "SwivelAllAppsInfos.db";
    private static final String DATABASE_NAME_SAFE = "SwivelAllAppsInfos_safe.db";
    private static final int DATABASE_VERSION = 100;
    private static final String TAG = "SwivelAllAppsDBProvider";
    private static String sCurrentDBName;

    @Override // com.lge.launcher3.allapps.AllAppsDBProvider, android.content.ContentProvider
    public boolean onCreate() {
        LGHomeFeature.init(getContext());
        this.mId = 1;
        sCurrentDBName = DATABASE_NAME_SAFE;
        if (getContext().getPackageManager().isSafeMode()) {
            sCurrentDBName = DATABASE_NAME_SAFE;
        } else {
            sCurrentDBName = DATABASE_NAME;
        }
        sOpenHelper.put(this.mId, new AllAppsDBProvider.DatabaseHelper(getContext(), sCurrentDBName, 100));
        AllAppsDBAdapter.getInstance(true).setmSQLiteOpenHelper(sOpenHelper.get(this.mId));
        if (getContext().getPackageManager().isSafeMode()) {
            destroyMenuDb(sOpenHelper.get(this.mId).getWritableDatabase());
        }
        return true;
    }
}
