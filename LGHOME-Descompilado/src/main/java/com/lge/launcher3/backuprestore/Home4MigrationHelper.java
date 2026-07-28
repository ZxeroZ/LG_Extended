package com.lge.launcher3.backuprestore;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Process;
import android.util.Pair;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.util.ManagedProfileHeuristic;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGLog;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class Home4MigrationHelper {
    private static final int ALLAPPS_CONTAINER = -102;
    private static final int PLUSHOME_CONTAINER = -103;
    private static final String TAG = "Home4DatabaseMigrationHelper";
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public Home4MigrationHelper(Context context, SQLiteDatabase database) {
        this.mContext = context;
        this.mDatabase = database;
    }

    public void migrate() {
        LGLog.i(TAG, "Migrate LGHome4 database");
        deleteUnusedDatabaseAndTables();
        dropUnusedTables();
        migrateFavorites();
        setupGridSize();
        ManagedProfileHeuristic.markExistingUsersForNoFolderCreation(this.mContext);
    }

    public void deleteUnusedDatabaseAndTables() {
        String strReplace = this.mContext.getFilesDir().getAbsolutePath().replace("files", "databases");
        new File(strReplace + "/LGMenuInfos.db").delete();
        new File(strReplace + "/LGMenuInfos.db-journal").delete();
        new File(strReplace + "/smart_bulletin.db").delete();
        new File(strReplace + "/smart_bulletin.db-journal").delete();
    }

    private void dropUnusedTables() {
        try {
            this.mDatabase.execSQL("DROP TABLE IF EXISTS workspaceinfo");
            this.mDatabase.execSQL("DROP TABLE IF EXISTS essentialapplicationlist");
        } catch (SQLException e) {
            LGLog.w(TAG, "Failed to drop unused table: " + e.getMessage(), new int[0]);
        }
    }

    private void migrateFavorites() throws Throwable {
        addWorkspacesTable();
        addAppWidgetProviderColumn();
        addProfileColumn();
        addIntegerColumn(LauncherSettings.Favorites.RESTORED, 0L);
        updateFolderItemsRank(true);
        convertShortcutsToLauncherActivities();
        removeFullScreenItem();
        removeAllAppsItems();
        removePlusHomeItems();
        removeDuplicatedItems();
        resetValues();
        initWorkspaceTable();
        removeUnusedColumns();
    }

    private void removeUnusedColumns() {
        this.mDatabase.beginTransaction();
        try {
            createNewFavoritesTempTable();
            copyFavoritesFromOrg();
            dropOrgTable();
            renameFavoritesAsOrg();
            this.mDatabase.setTransactionSuccessful();
        } finally {
            this.mDatabase.endTransaction();
        }
    }

    private void renameFavoritesAsOrg() {
        this.mDatabase.execSQL("ALTER TABLE favorites_temp RENAME TO favorites");
    }

    private void createNewFavoritesTempTable() {
        long serialNumberForUser = UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(Process.myUserHandle());
        this.mDatabase.execSQL("CREATE TABLE favorites_temp (_id INTEGER PRIMARY KEY,title TEXT,intent TEXT,container INTEGER,screen INTEGER,cellX INTEGER,cellY INTEGER,spanX INTEGER,spanY INTEGER,itemType INTEGER,appWidgetId INTEGER NOT NULL DEFAULT -1,isShortcut INTEGER,iconType INTEGER,iconPackage TEXT,iconResource TEXT,icon BLOB,uri TEXT,displayMode INTEGER,appWidgetProvider TEXT,modified INTEGER NOT NULL DEFAULT 0,restored INTEGER NOT NULL DEFAULT 0,profileId INTEGER DEFAULT " + serialNumberForUser + ",rank INTEGER NOT NULL DEFAULT 0,options INTEGER NOT NULL DEFAULT 0,iconId TEXT,userCustomizedIcon BLOB);");
    }

    private void copyFavoritesFromOrg() {
        int type;
        Cursor cursorRawQuery = this.mDatabase.rawQuery("SELECT * FROM favorites_temp", null);
        String[] columnNames = cursorRawQuery.getColumnNames();
        Cursor cursorRawQuery2 = this.mDatabase.rawQuery("SELECT * FROM favorites", null);
        while (cursorRawQuery2.moveToNext()) {
            ContentValues contentValues = new ContentValues();
            for (String str : columnNames) {
                int columnIndex = cursorRawQuery2.getColumnIndex(str);
                if (columnIndex != -1 && (type = cursorRawQuery2.getType(columnIndex)) != 0) {
                    if (type == 1) {
                        contentValues.put(str, Integer.valueOf(cursorRawQuery2.getInt(columnIndex)));
                    } else if (type == 3) {
                        contentValues.put(str, cursorRawQuery2.getString(columnIndex));
                    } else if (type == 4) {
                        contentValues.put(str, cursorRawQuery2.getBlob(columnIndex));
                    } else {
                        cursorRawQuery.close();
                        cursorRawQuery2.close();
                        throw new RuntimeException("Invalid column type for " + str + ": " + type);
                    }
                }
            }
            this.mDatabase.insert("favorites_temp", null, contentValues);
        }
        cursorRawQuery.close();
        cursorRawQuery2.close();
    }

    private void dropOrgTable() {
        this.mDatabase.execSQL("DROP TABLE favorites");
    }

    private void setupGridSize() {
        Pair<Integer, Integer> maxGridSize = getMaxGridSize();
        int iIntValue = ((Integer) maxGridSize.first).intValue();
        int iIntValue2 = ((Integer) maxGridSize.second).intValue();
        LGLog.i(TAG, "Setup grid size : " + iIntValue + "x" + iIntValue2);
        SharedPreferencesManager.putInt(this.mContext, 0, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, iIntValue);
        SharedPreferencesManager.putInt(this.mContext, 0, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, iIntValue2);
    }

    private Pair<Integer, Integer> getMaxGridSize() {
        int i = 4;
        int i2 = 16;
        int i3 = 4;
        for (String str : this.mContext.getResources().getStringArray(R.array.config_dynamic_grid_preset)) {
            String[] strArrSplit = new String(str).split("x");
            if (strArrSplit.length == 2) {
                try {
                    int i4 = Integer.parseInt(strArrSplit[0]);
                    int i5 = Integer.parseInt(strArrSplit[1]);
                    int i6 = i4 * i5;
                    if (i2 < i6) {
                        i3 = i5;
                        i = i4;
                        i2 = i6;
                    }
                } catch (NumberFormatException unused) {
                }
            }
        }
        return new Pair<>(Integer.valueOf(i), Integer.valueOf(i3));
    }

    private void initWorkspaceTable() {
        LGLog.d(TAG, "Init workspace table..");
        this.mDatabase.beginTransaction();
        try {
            try {
                Cursor cursorRawQuery = this.mDatabase.rawQuery("SELECT screen FROM favorites WHERE container=-100 GROUP BY screen ORDER BY screen ASC", null);
                int i = 0;
                while (cursorRawQuery.moveToNext()) {
                    LGLog.d(TAG, "id: " + cursorRawQuery.getInt(0) + ", rank: " + i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_id", Integer.valueOf(cursorRawQuery.getInt(0)));
                    contentValues.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
                    LauncherProvider.addModifiedTime(contentValues);
                    this.mDatabase.insertOrThrow(LauncherSettings.WorkspaceScreens.TABLE_NAME, null, contentValues);
                    i++;
                }
                this.mDatabase.setTransactionSuccessful();
                cursorRawQuery.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            this.mDatabase.endTransaction();
        }
    }

    private void removeFullScreenItem() {
        this.mDatabase.execSQL("DELETE FROM favorites WHERE itemType=8");
    }

    private void removePlusHomeItems() {
        removeItemsOn(-103);
    }

    private void removeAllAppsItems() {
        Cursor cursorQuery = this.mDatabase.query("favorites", new String[]{"_id"}, "container=-102 AND itemType=2", null, null, null, null);
        while (cursorQuery.moveToNext()) {
            try {
                removeItemsOn(cursorQuery.getInt(0));
            } catch (Throwable th) {
                if (cursorQuery != null) {
                    try {
                        cursorQuery.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        if (cursorQuery != null) {
            cursorQuery.close();
        }
        removeItemsOn(-102);
    }

    private void removeItemsOn(int container) {
        this.mDatabase.execSQL("DELETE FROM favorites WHERE container=" + container);
    }

    private void removeDuplicatedItems() {
        addScreenRankColumn();
    }

    private void addScreenRankColumn() {
        Pair<Integer, Integer> maxGridSize = getMaxGridSize();
        int iIntValue = ((Integer) maxGridSize.first).intValue();
        int iIntValue2 = ((Integer) maxGridSize.second).intValue();
        this.mDatabase.beginTransaction();
        try {
            try {
                this.mDatabase.execSQL("ALTER TABLE favorites ADD COLUMN screenRank INTEGER NOT NULL DEFAULT 2147483647");
                this.mDatabase.execSQL("UPDATE favorites SET screenRank=cellX WHERE container=-101 AND screen IS NOT NULL AND cellX IS NOT NULL AND cellY IS NOT NULL");
                this.mDatabase.execSQL("UPDATE favorites SET screenRank=cellX+(cellY*?)+(screen+1)*? WHERE container=-100 AND screen IS NOT NULL AND cellX IS NOT NULL AND cellY IS NOT NULL", new Object[]{Integer.valueOf(iIntValue), Integer.valueOf(iIntValue2 * iIntValue)});
                Cursor cursorRawQuery = this.mDatabase.rawQuery("SELECT _id, screenRank FROM favorites WHERE itemType=2", null);
                while (cursorRawQuery.moveToNext()) {
                    try {
                        this.mDatabase.execSQL("UPDATE favorites SET screenRank=? WHERE container=?", new Object[]{Integer.valueOf(cursorRawQuery.getInt(1)), Integer.valueOf(cursorRawQuery.getInt(0))});
                    } catch (Throwable th) {
                        if (cursorRawQuery != null) {
                            try {
                                cursorRawQuery.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                }
                if (cursorRawQuery != null) {
                    cursorRawQuery.close();
                }
                this.mDatabase.setTransactionSuccessful();
            } catch (SQLException e) {
                LGLog.w(TAG, "Failed to calculate screenRank: " + e.getMessage(), new int[0]);
            }
        } finally {
            this.mDatabase.endTransaction();
        }
    }

    private void removeDuplicateItemsByScreenRank() {
        this.mDatabase.beginTransaction();
        try {
            try {
                Cursor cursorRawQuery = this.mDatabase.rawQuery("SELECT intent FROM favorites WHERE itemType=? GROUP BY intent HAVING COUNT(*)>1", new String[]{String.valueOf(0)});
                while (cursorRawQuery.moveToNext()) {
                    Cursor cursorRawQuery2 = this.mDatabase.rawQuery("SELECT _id, intent FROM favorites WHERE intent=? AND itemType=? ORDER BY screenRank ASC, rank ASC limit 1", new String[]{cursorRawQuery.getString(0), String.valueOf(0)});
                    while (cursorRawQuery2.moveToNext()) {
                        this.mDatabase.execSQL("DELETE FROM favorites WHERE _id<>? AND intent=? AND itemType=?", new Object[]{String.valueOf(cursorRawQuery2.getInt(0)), cursorRawQuery2.getString(1), String.valueOf(0)});
                    }
                    cursorRawQuery2.close();
                }
                cursorRawQuery.close();
                this.mDatabase.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            this.mDatabase.endTransaction();
        }
    }

    private void addAppWidgetProviderColumn() {
        this.mDatabase.beginTransaction();
        try {
            this.mDatabase.execSQL("ALTER TABLE favorites ADD COLUMN appWidgetProvider TEXT;");
            Cursor cursorQuery = this.mDatabase.query("favorites", new String[]{"_id", LauncherConst.EXTRA_PACKAGE_NAME, LauncherConst.EXTRA_CLASS_NAME}, "itemType = 4", null, null, null, null);
            while (cursorQuery.moveToNext()) {
                try {
                    this.mDatabase.execSQL("UPDATE favorites SET appWidgetProvider=? WHERE _id=?;", new Object[]{new ComponentName(cursorQuery.getString(1), cursorQuery.getString(2)).flattenToString(), Long.valueOf(cursorQuery.getLong(0))});
                } finally {
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            this.mDatabase.setTransactionSuccessful();
        } finally {
            this.mDatabase.endTransaction();
        }
    }

    private void resetValues() {
        try {
            this.mDatabase.execSQL("UPDATE favorites SET iconType=0 WHERE itemType=2");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            this.mDatabase.execSQL("UPDATE favorites SET iconId=? WHERE itemType=? OR itemType=?", new Object[]{"original", String.valueOf(0), String.valueOf(1)});
            this.mDatabase.execSQL("UPDATE favorites SET iconType=0 WHERE iconType=3");
            this.mDatabase.execSQL("UPDATE favorites SET spanX=1, spanY=1 WHERE itemType=0");
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        try {
            Cursor cursorRawQuery = this.mDatabase.rawQuery("SELECT _id, cellX FROM favorites WHERE container=-101 ORDER BY cellX ASC", null);
            while (cursorRawQuery.moveToNext()) {
                try {
                    this.mDatabase.execSQL("UPDATE favorites SET screen=? WHERE _id=?", new Object[]{Integer.valueOf(cursorRawQuery.getInt(1)), Integer.valueOf(cursorRawQuery.getInt(0))});
                } finally {
                }
            }
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
        } catch (SQLException e3) {
            e3.printStackTrace();
        }
    }

    private void addWorkspacesTable() {
        this.mDatabase.execSQL("CREATE TABLE workspaceScreens (_id INTEGER PRIMARY KEY,screenRank INTEGER,modified INTEGER NOT NULL DEFAULT 0);");
    }

    private void addIntegerColumn(String columnName, long defaultValue) {
        this.mDatabase.beginTransaction();
        try {
            this.mDatabase.execSQL("ALTER TABLE favorites ADD COLUMN " + columnName + " INTEGER NOT NULL DEFAULT " + defaultValue + ";");
            this.mDatabase.setTransactionSuccessful();
        } finally {
            this.mDatabase.endTransaction();
        }
    }

    private void addProfileColumn() {
        try {
            addIntegerColumn("profileId", UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(Process.myUserHandle()));
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private void updateFolderItemsRank(boolean addRankColumn) {
        this.mDatabase.beginTransaction();
        if (addRankColumn) {
            try {
                this.mDatabase.execSQL("ALTER TABLE favorites ADD COLUMN rank INTEGER NOT NULL DEFAULT 0;");
            } finally {
                this.mDatabase.endTransaction();
            }
        }
        Cursor cursorRawQuery = this.mDatabase.rawQuery("SELECT container, MAX(cellX) FROM favorites WHERE container IN (SELECT _id FROM favorites WHERE itemType = ?) GROUP BY container;", new String[]{Integer.toString(2)});
        while (cursorRawQuery.moveToNext()) {
            try {
                this.mDatabase.execSQL("UPDATE favorites SET rank=cellX+(cellY*?) WHERE container=? AND cellX IS NOT NULL AND cellY IS NOT NULL;", new Object[]{Long.valueOf(cursorRawQuery.getLong(1) + 1), Long.valueOf(cursorRawQuery.getLong(0))});
            } finally {
            }
        }
        if (cursorRawQuery != null) {
            cursorRawQuery.close();
        }
        this.mDatabase.setTransactionSuccessful();
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x00ca  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00cf  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void convertShortcutsToLauncherActivities() throws java.lang.Throwable {
        /*
            r15 = this;
            java.lang.String r0 = "intent"
            java.lang.String r1 = "_id"
            java.lang.String r2 = "Home4DatabaseMigrationHelper"
            android.database.sqlite.SQLiteDatabase r3 = r15.mDatabase
            r3.beginTransaction()
            r3 = 0
            r4 = 0
            android.content.Context r5 = r15.mContext     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            com.android.launcher3.compat.UserManagerCompat r5 = com.android.launcher3.compat.UserManagerCompat.getInstance(r5)     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            android.os.UserHandle r6 = android.os.Process.myUserHandle()     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            long r5 = r5.getSerialNumberForUser(r6)     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            android.database.sqlite.SQLiteDatabase r7 = r15.mDatabase     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            java.lang.String r8 = "favorites"
            java.lang.String[] r9 = new java.lang.String[]{r1, r0}     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            r10.<init>()     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            java.lang.String r11 = "itemType=1 AND profileId="
            r10.append(r11)     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            r10.append(r5)     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            java.lang.String r10 = r10.toString()     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            android.database.Cursor r5 = r7.query(r8, r9, r10, r11, r12, r13, r14)     // Catch: java.lang.Throwable -> L93 android.database.SQLException -> L96
            android.database.sqlite.SQLiteDatabase r6 = r15.mDatabase     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            java.lang.String r7 = "UPDATE favorites SET itemType=0 WHERE _id=?"
            android.database.sqlite.SQLiteStatement r3 = r6.compileStatement(r7)     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            int r1 = r5.getColumnIndexOrThrow(r1)     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            int r0 = r5.getColumnIndexOrThrow(r0)     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
        L4c:
            boolean r6 = r5.moveToNext()     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            if (r6 == 0) goto L76
            java.lang.String r6 = r5.getString(r0)     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            android.content.Intent r6 = android.content.Intent.parseUri(r6, r4)     // Catch: java.net.URISyntaxException -> L6d java.lang.Throwable -> L8b android.database.SQLException -> L8f
            boolean r6 = com.android.launcher3.Utilities.isLauncherAppTarget(r6)     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            if (r6 != 0) goto L61
            goto L4c
        L61:
            long r6 = r5.getLong(r1)     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            r8 = 1
            r3.bindLong(r8, r6)     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            r3.executeUpdateDelete()     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            goto L4c
        L6d:
            r6 = move-exception
            java.lang.String r7 = "Unable to parse intent"
            int[] r8 = new int[r4]     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            com.lge.launcher3.util.LGLog.w(r2, r7, r6, r8)     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            goto L4c
        L76:
            android.database.sqlite.SQLiteDatabase r0 = r15.mDatabase     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            r0.setTransactionSuccessful()     // Catch: java.lang.Throwable -> L8b android.database.SQLException -> L8f
            android.database.sqlite.SQLiteDatabase r0 = r15.mDatabase
            r0.endTransaction()
            if (r5 == 0) goto L85
            r5.close()
        L85:
            if (r3 == 0) goto Lc1
            r3.close()
            goto Lc1
        L8b:
            r0 = move-exception
            r1 = r3
            r3 = r5
            goto Lc3
        L8f:
            r0 = move-exception
            r1 = r3
            r3 = r5
            goto L98
        L93:
            r0 = move-exception
            r1 = r3
            goto Lc3
        L96:
            r0 = move-exception
            r1 = r3
        L98:
            java.lang.String r0 = r0.getMessage()     // Catch: java.lang.Throwable -> Lc2
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lc2
            r5.<init>()     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r6 = "Error deduping shortcuts: "
            r5.append(r6)     // Catch: java.lang.Throwable -> Lc2
            r5.append(r0)     // Catch: java.lang.Throwable -> Lc2
            java.lang.String r0 = r5.toString()     // Catch: java.lang.Throwable -> Lc2
            int[] r4 = new int[r4]     // Catch: java.lang.Throwable -> Lc2
            com.lge.launcher3.util.LGLog.w(r2, r0, r4)     // Catch: java.lang.Throwable -> Lc2
            android.database.sqlite.SQLiteDatabase r0 = r15.mDatabase
            r0.endTransaction()
            if (r3 == 0) goto Lbc
            r3.close()
        Lbc:
            if (r1 == 0) goto Lc1
            r1.close()
        Lc1:
            return
        Lc2:
            r0 = move-exception
        Lc3:
            android.database.sqlite.SQLiteDatabase r2 = r15.mDatabase
            r2.endTransaction()
            if (r3 == 0) goto Lcd
            r3.close()
        Lcd:
            if (r1 == 0) goto Ld2
            r1.close()
        Ld2:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.backuprestore.Home4MigrationHelper.convertShortcutsToLauncherActivities():void");
    }
}
