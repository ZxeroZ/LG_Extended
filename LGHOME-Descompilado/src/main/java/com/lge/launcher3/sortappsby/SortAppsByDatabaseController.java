package com.lge.launcher3.sortappsby;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.SparseIntArray;
import com.android.launcher3.LauncherSettings;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SortAppsByDatabaseController {
    public static final String TAG = "SortAppsByDatabaseController";

    public static SparseIntArray getScreenArray(Context context) {
        Cursor cursorQuery;
        try {
            cursorQuery = context.getContentResolver().query(LauncherSettings.WorkspaceScreens.CONTENT_URI, new String[]{"_id", LauncherSettings.WorkspaceScreens.SCREEN_RANK}, null, null, null, null);
        } catch (SQLiteException e) {
            LGLog.i(TAG, String.format("getScreenArray() : SQLiteException(%s)", e.toString()));
            cursorQuery = null;
        }
        if (cursorQuery == null) {
            LGLog.i(TAG, "getScreenArray() : Cursor is null");
            return null;
        }
        int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow("_id");
        int columnIndexOrThrow2 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.WorkspaceScreens.SCREEN_RANK);
        SparseIntArray sparseIntArray = new SparseIntArray();
        while (cursorQuery.moveToNext()) {
            sparseIntArray.put(cursorQuery.getInt(columnIndexOrThrow2), cursorQuery.getInt(columnIndexOrThrow));
        }
        cursorQuery.close();
        return sparseIntArray;
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x0106  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00d0 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.ArrayList<com.lge.launcher3.sortappsby.SortAppsByItemInfo> getItemList(android.content.Context r35, android.util.SparseIntArray r36, int r37, java.util.ArrayList<com.lge.launcher3.sortappsby.SortAppsByItemInfo> r38, final int r39) {
        /*
            r1 = r36
            if (r38 != 0) goto Lb
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r2 = r0
            goto Ld
        Lb:
            r2 = r38
        Ld:
            r3 = 1
            r4 = 0
            r5 = 0
            r15 = r35
            r0 = r37
            android.database.Cursor r0 = getCursorOfItemList(r15, r0)     // Catch: android.database.sqlite.SQLiteException -> L1a
            r14 = r0
            goto L30
        L1a:
            r0 = move-exception
            r6 = r0
            java.lang.String r0 = com.lge.launcher3.sortappsby.SortAppsByDatabaseController.TAG
            java.lang.Object[] r7 = new java.lang.Object[r3]
            java.lang.String r6 = r6.toString()
            r7[r5] = r6
            java.lang.String r6 = "getItemList() : SQLiteException(%s)"
            java.lang.String r6 = java.lang.String.format(r6, r7)
            com.lge.launcher3.util.LGLog.i(r0, r6)
            r14 = r4
        L30:
            if (r14 != 0) goto L3a
            java.lang.String r0 = com.lge.launcher3.sortappsby.SortAppsByDatabaseController.TAG
            java.lang.String r1 = "getItemList() : Cursor is null"
            com.lge.launcher3.util.LGLog.i(r0, r1)
            return r4
        L3a:
            java.lang.String r0 = "_id"
            int r13 = r14.getColumnIndexOrThrow(r0)
            java.lang.String r0 = "title"
            int r12 = r14.getColumnIndexOrThrow(r0)
            java.lang.String r0 = "intent"
            int r11 = r14.getColumnIndexOrThrow(r0)
            java.lang.String r0 = "screen"
            int r10 = r14.getColumnIndexOrThrow(r0)
            java.lang.String r0 = "cellX"
            int r9 = r14.getColumnIndexOrThrow(r0)
            java.lang.String r0 = "cellY"
            int r8 = r14.getColumnIndexOrThrow(r0)
            java.lang.String r0 = "spanX"
            int r7 = r14.getColumnIndexOrThrow(r0)
            java.lang.String r0 = "spanY"
            int r6 = r14.getColumnIndexOrThrow(r0)
            java.lang.String r0 = "itemType"
            int r3 = r14.getColumnIndexOrThrow(r0)
            java.lang.String r0 = "profileId"
            int r4 = r14.getColumnIndexOrThrow(r0)
        L76:
            boolean r0 = r14.moveToNext()
            if (r0 == 0) goto L172
            int r5 = r14.getInt(r13)
            r37 = r13
            java.lang.String r13 = r14.getString(r12)
            java.lang.String r0 = r14.getString(r11)
            if (r0 == 0) goto Lbc
            r16 = r11
            r11 = 0
            android.content.Intent r0 = android.content.Intent.parseUri(r0, r11)     // Catch: java.net.URISyntaxException -> L9a
            r38 = r12
            r19 = 1
            r21 = 0
            goto Lc6
        L9a:
            r0 = move-exception
            r17 = r0
            java.lang.String r0 = com.lge.launcher3.sortappsby.SortAppsByDatabaseController.TAG
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]
            java.lang.String r17 = r17.toString()
            r18 = 0
            r11[r18] = r17
            r19 = 1
            r21 = 0
            r11[r19] = r21
            r38 = r12
            java.lang.String r12 = "getItemList() : URISyntaxException(%s), intent(%s)"
            java.lang.String r11 = java.lang.String.format(r12, r11)
            com.lge.launcher3.util.LGLog.i(r0, r11)
            goto Lc4
        Lbc:
            r16 = r11
            r38 = r12
            r19 = 1
            r21 = 0
        Lc4:
            r0 = r21
        Lc6:
            int r11 = r14.getInt(r10)
            int r12 = r1.indexOfValue(r11)
            if (r12 >= 0) goto L106
            java.lang.String r0 = com.lge.launcher3.sortappsby.SortAppsByDatabaseController.TAG
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r17 = r10
            java.lang.String r10 = "indexOfValue isn't valid/ title  = "
            r12.append(r10)
            r12.append(r13)
            java.lang.String r10 = "/ id = "
            r12.append(r10)
            r12.append(r5)
            java.lang.String r5 = "/ screenId = "
            r12.append(r5)
            r12.append(r11)
            java.lang.String r5 = r12.toString()
            r10 = 0
            int[] r11 = new int[r10]
            com.lge.launcher3.util.LGLog.w(r0, r5, r11)
        Lfb:
            r13 = r37
            r12 = r38
            r5 = r10
            r11 = r16
            r10 = r17
            goto L76
        L106:
            r17 = r10
            r10 = 0
            int r12 = r1.keyAt(r12)
            r1 = r39
            if (r12 != r1) goto L114
            r1 = r36
            goto Lfb
        L114:
            int r18 = r14.getInt(r9)
            int r20 = r14.getInt(r8)
            int r22 = r14.getInt(r7)
            int r23 = r14.getInt(r6)
            int r24 = r14.getInt(r3)
            int r25 = r14.getInt(r4)
            com.lge.launcher3.sortappsby.SortAppsByItemInfo r1 = new com.lge.launcher3.sortappsby.SortAppsByItemInfo
            r26 = r6
            r6 = r1
            r27 = r7
            r7 = r35
            r28 = r8
            r8 = r5
            r5 = r9
            r9 = r13
            r30 = r10
            r29 = r17
            r10 = r0
            r31 = r16
            r32 = r38
            r33 = r37
            r13 = r18
            r34 = r14
            r14 = r20
            r15 = r22
            r16 = r23
            r17 = r24
            r18 = r25
            r6.<init>(r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            r2.add(r1)
            r15 = r35
            r1 = r36
            r9 = r5
            r6 = r26
            r7 = r27
            r8 = r28
            r10 = r29
            r5 = r30
            r11 = r31
            r12 = r32
            r13 = r33
            r14 = r34
            goto L76
        L172:
            r34 = r14
            r34.close()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.sortappsby.SortAppsByDatabaseController.getItemList(android.content.Context, android.util.SparseIntArray, int, java.util.ArrayList, int):java.util.ArrayList");
    }

    private static Cursor getCursorOfItemList(Context context, int selectionItemType) {
        return context.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, new String[]{"_id", "title", LauncherSettings.BaseLauncherColumns.INTENT, "screen", LauncherSettings.Favorites.CELLX, LauncherSettings.Favorites.CELLY, "spanX", "spanY", LauncherSettings.BaseLauncherColumns.ITEM_TYPE, "profileId"}, "(container = -100) AND (itemType = ?)", new String[]{Integer.toString(selectionItemType)}, null, null);
    }

    public static void updateRearrangedItemList(Context context, ArrayList<SortAppsByItemInfo> rearrangedItemList) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        for (SortAppsByItemInfo sortAppsByItemInfo : rearrangedItemList) {
            boolean zIsScreenIdChanged = sortAppsByItemInfo.isScreenIdChanged();
            boolean zIsCellXChanged = sortAppsByItemInfo.isCellXChanged();
            boolean zIsCellYChanged = sortAppsByItemInfo.isCellYChanged();
            if (zIsScreenIdChanged || zIsCellXChanged || zIsCellYChanged) {
                contentValues.clear();
                if (zIsScreenIdChanged) {
                    contentValues.put("screen", Integer.valueOf(sortAppsByItemInfo.mNewScreenId));
                }
                if (zIsCellXChanged) {
                    contentValues.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(sortAppsByItemInfo.mNewCellX));
                }
                if (zIsCellYChanged) {
                    contentValues.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(sortAppsByItemInfo.mNewCellY));
                }
                try {
                    contentResolver.update(LauncherSettings.Favorites.CONTENT_URI, contentValues, "_id = " + sortAppsByItemInfo.mId, null);
                } catch (SQLiteException e) {
                    String str = TAG;
                    LGLog.i(str, String.format("updateRearrangedItemList() : SQLiteException(%s)", e.toString()));
                    LGLog.i(str, String.format("updateRearrangedItemList() : itemInfo(%s)", sortAppsByItemInfo.toString()));
                }
            }
        }
    }
}
