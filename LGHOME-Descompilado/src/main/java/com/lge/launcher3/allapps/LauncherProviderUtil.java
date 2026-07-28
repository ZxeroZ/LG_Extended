package com.lge.launcher3.allapps;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Xml;
import com.android.internal.util.XmlUtils;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.config.ProviderConfig;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.SqlArguments;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class LauncherProviderUtil {
    public static final Uri CONTENT_APPWIDGET_RESET_URI = Uri.parse("content://" + ProviderConfig.AUTHORITY + "/appWidgetReset");
    private static final String TAG = "com.lge.launcher3.allapps.LauncherProviderUtil";

    public static HashMap<String, ComponentName> makeMappingTable(Context context, int xmlID) {
        int next;
        HashMap<String, ComponentName> map = new HashMap<>();
        try {
            XmlResourceParser xml = context.getResources().getXml(xmlID);
            Xml.asAttributeSet(xml);
            XmlUtils.beginDocument(xml, "repackagedList");
            int depth = xml.getDepth();
            do {
                next = xml.next();
                if (next == 3 && xml.getDepth() <= depth) {
                    break;
                }
            } while (next != 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String buildOrWhereString(String column, int[] values) {
        StringBuilder sb = new StringBuilder();
        for (int length = values.length - 1; length >= 0; length--) {
            sb.append(column).append("=").append(values[length]);
            if (length > 0) {
                sb.append(" OR ");
            }
        }
        return sb.toString();
    }

    public static long dbInsertAndCheck(SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
        if (!values.containsKey("_id")) {
            throw new RuntimeException("Error: attempting to add item without specifying an id");
        }
        return db.insert(table, nullColumnHack, values);
    }

    public static void deleteId(SQLiteDatabase db, long id) {
        SqlArguments sqlArguments = new SqlArguments(LauncherSettings.Favorites.getContentUri(id), null, null);
        db.delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
    }

    public static HashMap<String, ComponentName> makeMappingTable(Context context) {
        LGLog.i(TAG, "Making makeMappingTable for froyo or GB");
        return null;
    }

    static String buildNotEqualWhereString(String column, int[] values) {
        StringBuilder sb = new StringBuilder();
        for (int length = values.length - 1; length >= 0; length--) {
            sb.append(column).append("!=").append(values[length]);
            if (length > 0) {
                sb.append(" AND ");
            }
        }
        return sb.toString();
    }
}
