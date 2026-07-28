package com.lge.launcher3.hideapps;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.UserHandle;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.UserManagerCompat;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class HideAppsStorage {
    private static final boolean DEBUG = true;
    private static final String TAG = "HideApps.Storage";

    public static void addItems(Context context, List<HideAppItem> items) {
        int size = items.size();
        LGLog.i(TAG, "Add " + size + " items");
        UserManagerCompat userManagerCompat = UserManagerCompat.getInstance(context);
        int maxId = getMaxId(context);
        ContentValues[] contentValuesArr = new ContentValues[size];
        for (int i = 0; i < size; i++) {
            HideAppItem hideAppItem = items.get(i);
            LGLog.i(TAG, "Add: " + hideAppItem.toString());
            ComponentName componentName = hideAppItem.activityInfo.getComponentName();
            contentValuesArr[i] = new ContentValues();
            maxId++;
            contentValuesArr[i].put("_id", Integer.valueOf(maxId));
            contentValuesArr[i].put("componentName", componentName.flattenToString());
            contentValuesArr[i].put("profileId", Long.valueOf(userManagerCompat.getSerialNumberForUser(hideAppItem.userHandle)));
        }
        context.getContentResolver().bulkInsert(LauncherSettings.HideApps.CONTENT_URI, contentValuesArr);
        notifyChange(context);
    }

    public static void deleteAll(Context context) {
        LGLog.i(TAG, "Delete all items");
        LGLog.d(TAG, context.getContentResolver().delete(LauncherSettings.HideApps.CONTENT_URI, null, null) + " items are deleted");
        notifyChange(context);
    }

    public static synchronized void removeItemForPkg(Context context, String packageName, UserHandle user) {
        ContentResolver contentResolver = context.getContentResolver();
        long serialNumberForUser = UserManagerCompat.getInstance(context).getSerialNumberForUser(user);
        contentResolver.delete(LauncherSettings.HideApps.CONTENT_URI, "componentName LIKE ? AND profileId = ?", new String[]{packageName + "/%", Long.toString(serialNumberForUser)});
        notifyChange(context);
    }

    public static ArrayList<ComponentName> getAllItems(Context context, UserHandle userHandle) {
        ArrayList<ComponentName> arrayList = new ArrayList<>();
        try {
            Cursor cursorQuery = context.getContentResolver().query(LauncherSettings.HideApps.CONTENT_URI, new String[]{"componentName"}, "profileId=?", new String[]{Long.toString(UserManagerCompat.getInstance(context).getSerialNumberForUser(userHandle))}, null);
            while (cursorQuery.moveToNext()) {
                try {
                    arrayList.add(ComponentName.unflattenFromString(cursorQuery.getString(0)));
                } finally {
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (SQLException e) {
            LGLog.w(TAG, e.getMessage(), new int[0]);
        }
        return arrayList;
    }

    public static int getMaxId(Context context) {
        try {
            Cursor cursorQuery = context.getContentResolver().query(LauncherSettings.HideApps.CONTENT_URI, new String[]{"MAX(_id)"}, null, null, null);
            try {
                if (cursorQuery.getCount() == 0) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return -1;
                }
                cursorQuery.moveToFirst();
                int i = cursorQuery.getInt(0);
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                return i;
            } finally {
            }
        } catch (SQLException e) {
            LGLog.w(TAG, e.getMessage(), new int[0]);
            return -1;
        }
        LGLog.w(TAG, e.getMessage(), new int[0]);
        return -1;
    }

    public static void notifyChange(Context context) {
        context.getContentResolver().notifyChange(LauncherSettings.HideApps.CONTENT_URI, null);
    }
}
