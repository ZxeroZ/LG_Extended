package com.lge.launcher3.homesettings;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexablesProvider;
import com.lge.launcher3.util.LGLog;
import com.lge.provider.LGSearchIndexablesContract;

/* JADX INFO: loaded from: classes.dex */
public class HomeSearchIndexablesProvider extends SearchIndexablesProvider {
    private static final String TAG = "SettingsSearch";

    public boolean onCreate() {
        return true;
    }

    public Cursor queryNonIndexableKeys(String[] projection) {
        return new MatrixCursor(LGSearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS);
    }

    public Cursor queryRawData(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(LGSearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
        for (SearchIndexableRaw searchIndexableRaw : new SearchIndexableItem(getContext()).values()) {
            LGLog.d(TAG, searchIndexableRaw.toString());
            Object[] objArr = new Object[20];
            objArr[12] = searchIndexableRaw.key;
            objArr[5] = searchIndexableRaw.keywords;
            objArr[7] = searchIndexableRaw.className;
            objArr[1] = searchIndexableRaw.title;
            objArr[6] = searchIndexableRaw.screenTitle;
            objArr[2] = searchIndexableRaw.summaryOn;
            objArr[3] = searchIndexableRaw.summaryOff;
            objArr[9] = searchIndexableRaw.intentAction;
            objArr[11] = searchIndexableRaw.intentClass;
            objArr[10] = searchIndexableRaw.intentPackage;
            objArr[18] = Integer.valueOf(searchIndexableRaw.visible ? 1 : 0);
            matrixCursor.addRow(objArr);
        }
        return matrixCursor;
    }

    public Cursor queryXmlResources(String[] projection) {
        return new MatrixCursor(LGSearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS);
    }
}
