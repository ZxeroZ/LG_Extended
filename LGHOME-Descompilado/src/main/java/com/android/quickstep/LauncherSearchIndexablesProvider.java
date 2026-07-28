package com.android.quickstep;

import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexablesContract;
import android.provider.SearchIndexablesProvider;
import android.util.Xml;
import com.lge.launcher3.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class LauncherSearchIndexablesProvider extends SearchIndexablesProvider {
    public boolean onCreate() {
        return true;
    }

    public Cursor queryXmlResources(String[] strings) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS);
        matrixCursor.newRow().add("xmlResId", Integer.valueOf(R.xml.indexable_launcher_prefs)).add("intentAction", "android.intent.action.APPLICATION_PREFERENCES").add("intentTargetPackage", getContext().getPackageName()).add("intentTargetClass", getContext().getPackageManager().resolveActivity(new Intent("android.intent.action.APPLICATION_PREFERENCES").setPackage(getContext().getPackageName()), 0).activityInfo.name);
        return matrixCursor;
    }

    public Cursor queryRawData(String[] projection) {
        return new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
    }

    public Cursor queryNonIndexableKeys(String[] projection) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS);
        if (!((LauncherApps) getContext().getSystemService(LauncherApps.class)).hasShortcutHostPermission()) {
            try {
                XmlResourceParser xml = getContext().getResources().getXml(R.xml.indexable_launcher_prefs);
                try {
                    int depth = xml.getDepth();
                    int[] iArr = {android.R.attr.key};
                    while (true) {
                        int next = xml.next();
                        if ((next == 3 && xml.getDepth() <= depth) || next == 1) {
                            break;
                        }
                        if (next == 2) {
                            TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(Xml.asAttributeSet(xml), iArr);
                            matrixCursor.addRow(new String[]{typedArrayObtainStyledAttributes.getString(0)});
                            typedArrayObtainStyledAttributes.recycle();
                        }
                    }
                    if (xml != null) {
                        xml.close();
                    }
                } catch (Throwable th) {
                    if (xml != null) {
                        try {
                            xml.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException | XmlPullParserException e) {
                throw new RuntimeException(e);
            }
        }
        return matrixCursor;
    }
}
