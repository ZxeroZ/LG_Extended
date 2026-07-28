package com.lge.launcher3.allapps;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Process;
import android.util.Log;
import android.util.SparseArray;
import com.android.launcher3.compat.UserManagerCompat;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.SqlArguments;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsDBProvider extends ContentProvider {
    protected static final String CREATE_APP_INFO_TABLE = "CREATE TABLE app_infos (_id INTEGER PRIMARY KEY AUTOINCREMENT,component_name TEXT NOT NULL,profileId INTEGER NOT NULL DEFAULT -1)";
    public static final String CREATE_APP_INFO_TABLE_IF = "CREATE TABLE IF NOT EXISTS app_infos (_id INTEGER PRIMARY KEY AUTOINCREMENT,component_name TEXT NOT NULL,profileId INTEGER NOT NULL DEFAULT -1)";
    private static final String CREATE_PAGEMENU_CHILD_INFO_TABLE = "CREATE TABLE app_page_child_infos (_id INTEGER PRIMARY KEY AUTOINCREMENT,component_name TEXT,page_id INTEGER NOT NULL,cell_x INTEGER NOT NULL,cell_y INTEGER NOT NULL,title TEXT NOT NULL,itemtype INTEGER NOT NULL,folder_number LONG,folder_color INTEGER NOT NULL,menu_reserved1 INTEGER NOT NULL,menu_reserved2 INTEGER NOT NULL,menu_reserved3 TEXT,menu_reserved4 TEXT,options INTEGER NOT NULL DEFAULT 0);";
    public static final String CREATE_PAGEMENU_CHILD_INFO_TABLE_IF = "CREATE TABLE IF NOT EXISTS app_page_child_infos (_id INTEGER PRIMARY KEY AUTOINCREMENT,component_name TEXT,page_id INTEGER NOT NULL,cell_x INTEGER NOT NULL,cell_y INTEGER NOT NULL,title TEXT NOT NULL,itemtype INTEGER NOT NULL,folder_number LONG,folder_color INTEGER NOT NULL,menu_reserved1 INTEGER NOT NULL,menu_reserved2 INTEGER NOT NULL,menu_reserved3 TEXT,menu_reserved4 TEXT,options INTEGER NOT NULL DEFAULT 0);";
    private static final String CREATE_PAGEMENU_FOLDER_INFO_TABLE = "CREATE TABLE app_page_folder_infos (_id INTEGER PRIMARY KEY AUTOINCREMENT,component_name TEXT NOT NULL,page_id INTEGER NOT NULL,cell_x INTEGER NOT NULL,cell_y INTEGER NOT NULL,title TEXT NOT NULL,itemtype INTEGER NOT NULL,folder_number LONG,profileId INTEGER NOT NULL DEFAULT -1,rank INTEGER NOT NULL DEFAULT 0)";
    public static final String CREATE_PAGEMENU_FOLDER_INFO_TABLE_IF = "CREATE TABLE IF NOT EXISTS app_page_folder_infos (_id INTEGER PRIMARY KEY AUTOINCREMENT,component_name TEXT NOT NULL,page_id INTEGER NOT NULL,cell_x INTEGER NOT NULL,cell_y INTEGER NOT NULL,title TEXT NOT NULL,itemtype INTEGER NOT NULL,folder_number LONG,profileId INTEGER NOT NULL DEFAULT -1,rank INTEGER NOT NULL DEFAULT 0)";
    protected static final String CREATE_WIDGET_INFO_TABLE = "CREATE TABLE widget_infos (_id INTEGER PRIMARY KEY AUTOINCREMENT,component_name TEXT NOT NULL,profileId INTEGER NOT NULL DEFAULT -1)";
    public static final String CREATE_WIDGET_INFO_TABLE_IF = "CREATE TABLE IF NOT EXISTS widget_infos (_id INTEGER PRIMARY KEY AUTOINCREMENT,component_name TEXT NOT NULL,profileId INTEGER NOT NULL DEFAULT -1)";
    public static final String DATABASE_NAME = "AllAppsInfos.db";
    public static final String DATABASE_NAME_FOR_EASY = "EasyhomeInfos.db";
    private static final String DATABASE_NAME_SAFE = "AllAppsInfos_safe.db";
    private static final String DATABASE_NAME_SAFE_FOR_EASY = "EasyhomeInfos_safe.db";
    private static final int DATABASE_VERSION = 100;
    public static final String KEY_APP_COMPONENT_NAME = "component_name";
    public static final String KEY_APP_ID = "_id";
    public static final String KEY_APP_PROFILEID = "profileId";
    public static final String KEY_PAGEMENU_CHILD_CELL_X = "cell_x";
    public static final String KEY_PAGEMENU_CHILD_CELL_Y = "cell_y";
    public static final String KEY_PAGEMENU_CHILD_COMPONENT_NAME = "component_name";
    public static final String KEY_PAGEMENU_CHILD_FOLDERCOLOR = "folder_color";
    public static final String KEY_PAGEMENU_CHILD_FOLDERNUMBER = "folder_number";
    public static final String KEY_PAGEMENU_CHILD_ID = "_id";
    public static final String KEY_PAGEMENU_CHILD_ITEMTYPE = "itemtype";
    public static final String KEY_PAGEMENU_CHILD_OPTIONS = "options";
    public static final String KEY_PAGEMENU_CHILD_PAGE_ID = "page_id";
    public static final String KEY_PAGEMENU_CHILD_RANK = "rank";
    public static final String KEY_PAGEMENU_CHILD_RESERVED3 = "menu_reserved3";
    public static final String KEY_PAGEMENU_CHILD_RESERVED4 = "menu_reserved4";
    public static final String KEY_PAGEMENU_CHILD_TITLE = "title";
    public static final String KEY_PAGEMENU_FOLDER_UNEDITABLE = "menu_reserved1";
    public static final String KEY_PAGEMENU_PROFILE_ID = "menu_reserved2";
    public static final int PAGEMENU_DUMMYENTRY_ID = 1;
    protected static final String PARAMETER_NOTIFY = "notify";
    protected static final String TABLE_PAGEMENU_CHILD_INFOS = "app_page_child_infos";
    protected static final String TABLE_PAGEMENU_FOLDER_INFOS = "app_page_folder_infos";
    private static final String TAG = "AllAppsDBProvider";
    private static String sCurrentDBName;
    public int mId = 0;
    public static final Uri CONTENT_PAGEMENU_CHILD_URI = Uri.parse("content://com.lge.launcher3.AllAppsDBProvider/app_page_child_infos?notify=true");
    public static final Uri CONTENT_PAGEMENU_CHILD_URI_FOR_SWIVEL = Uri.parse("content://com.lge.launcher3.SwivelAllAppsDBProvider/app_page_child_infos?notify=true");
    public static final Uri CONTENT_PAGEMENU_FOLDER_URI = Uri.parse("content://com.lge.launcher3.AllAppsDBProvider/app_page_folder_infos?notify=true");
    public static final Uri CONTENT_PAGEMENU_FOLDER_URI_FOR_SWIVEL = Uri.parse("content://com.lge.launcher3.SwivelAllAppsDBProvider/app_page_folder_infos?notify=true");
    protected static SparseArray<DatabaseHelper> sOpenHelper = new SparseArray<>();

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        LGHomeFeature.init(getContext());
        sCurrentDBName = DATABASE_NAME_SAFE;
        if (LGHomeFeature.isDisableEasyHome()) {
            if (getContext().getPackageManager().isSafeMode()) {
                sCurrentDBName = DATABASE_NAME_SAFE;
            } else {
                sCurrentDBName = DATABASE_NAME;
            }
        } else if (getContext().getPackageManager().isSafeMode()) {
            sCurrentDBName = DATABASE_NAME_SAFE_FOR_EASY;
        } else {
            sCurrentDBName = DATABASE_NAME_FOR_EASY;
        }
        sOpenHelper.put(this.mId, new DatabaseHelper(getContext()));
        AllAppsDBAdapter.getInstance(false).setmSQLiteOpenHelper(sOpenHelper.get(this.mId));
        if (!getContext().getPackageManager().isSafeMode()) {
            return true;
        }
        destroyMenuDb(sOpenHelper.get(this.mId).getWritableDatabase());
        return true;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues initialValues) {
        long jInsert = sOpenHelper.get(this.mId).getWritableDatabase().insert(new SqlArguments(uri).table, null, initialValues);
        if (jInsert <= 0) {
            return null;
        }
        Uri uriWithAppendedId = ContentUris.withAppendedId(uri, jInsert);
        sendNotify(uriWithAppendedId);
        return uriWithAppendedId;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables(sqlArguments.table);
        SQLiteDatabase writableDatabase = sOpenHelper.get(this.mId).getWritableDatabase();
        if (writableDatabase == null) {
            return null;
        }
        Cursor cursorQuery = sQLiteQueryBuilder.query(writableDatabase, projection, sqlArguments.where, sqlArguments.args, null, null, sortOrder);
        if (cursorQuery != null) {
            cursorQuery.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursorQuery;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase writableDatabase = sOpenHelper.get(this.mId).getWritableDatabase();
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        int iUpdate = writableDatabase.update(sqlArguments.table, values, sqlArguments.where, sqlArguments.args);
        if (iUpdate > 0) {
            sendNotify(uri);
        }
        return iUpdate;
    }

    protected void sendNotify(Uri uri) {
        String queryParameter = uri.getQueryParameter("notify");
        if (queryParameter == null || "true".equals(queryParameter)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    protected class DatabaseHelper extends SQLiteOpenHelper {
        private final Context mContext;
        HashMap<String, ComponentName> mRepackagedList;

        DatabaseHelper(Context context) {
            super(context, AllAppsDBProvider.sCurrentDBName, (SQLiteDatabase.CursorFactory) null, 100);
            this.mRepackagedList = new HashMap<>();
            this.mContext = context;
        }

        DatabaseHelper(Context context, String databaseName) {
            super(context, databaseName, (SQLiteDatabase.CursorFactory) null, 100);
            this.mRepackagedList = new HashMap<>();
            this.mContext = context;
        }

        DatabaseHelper(Context context, String databaseName, int version) {
            super(context, databaseName, (SQLiteDatabase.CursorFactory) null, version);
            this.mRepackagedList = new HashMap<>();
            this.mContext = context;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        private void createTables(SQLiteDatabase db) {
            AllAppsDBProvider.createAllAppsTables(db);
        }

        private void convertComponentName(SQLiteDatabase db) throws Throwable {
            ComponentName componentName;
            String strBuildOrWhereString = LauncherProviderUtil.buildOrWhereString(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE, new int[]{0});
            HashMap<String, ComponentName> map = this.mRepackagedList;
            if (map == null || map.isEmpty()) {
                return;
            }
            db.beginTransaction();
            LGLog.i(AllAppsDBProvider.TAG, "size ! mRepackagedList.size() : " + this.mRepackagedList.size());
            Cursor cursor = null;
            try {
                try {
                    Cursor cursorQuery = db.query(AllAppsDBProvider.TABLE_PAGEMENU_CHILD_INFOS, new String[]{"_id", "component_name"}, strBuildOrWhereString, null, null, null, null);
                    try {
                        ContentValues contentValues = new ContentValues();
                        int columnIndex = cursorQuery.getColumnIndex("_id");
                        int columnIndex2 = cursorQuery.getColumnIndex("component_name");
                        while (cursorQuery != null && cursorQuery.moveToNext()) {
                            long j = cursorQuery.getLong(columnIndex);
                            String string = cursorQuery.getString(columnIndex2);
                            String str = "_id=" + j;
                            if (this.mRepackagedList.containsKey(string) && (componentName = this.mRepackagedList.get(string)) != null) {
                                contentValues.clear();
                                contentValues.put("component_name", componentName.flattenToShortString());
                                db.update(AllAppsDBProvider.TABLE_PAGEMENU_CHILD_INFOS, contentValues, str, null);
                            }
                        }
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    } catch (RuntimeException e) {
                        e = e;
                        cursor = cursorQuery;
                        e.printStackTrace();
                        db.endTransaction();
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        cursor = cursorQuery;
                        db.endTransaction();
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (RuntimeException e2) {
                e = e2;
            }
        }

        /* JADX DEBUG: Another duplicated slice has different insns count: {[INVOKE]}, finally: {[INVOKE, INVOKE, IF] complete} */
        private void upgradeFolderInfo(SQLiteDatabase db) {
            String strBuildOrWhereString = LauncherProviderUtil.buildOrWhereString(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE, new int[]{2});
            db.beginTransaction();
            Cursor cursorQuery = db.query(AllAppsDBProvider.TABLE_PAGEMENU_CHILD_INFOS, new String[]{"_id", AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER}, strBuildOrWhereString, null, null, null, null);
            try {
                try {
                    int columnIndex = cursorQuery.getColumnIndex("_id");
                    int columnIndex2 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER);
                    if (cursorQuery.moveToFirst()) {
                        LGLog.i(AllAppsDBProvider.TAG, "move Folder Info From LauncherDB to Menu");
                        do {
                            long j = cursorQuery.getLong(columnIndex);
                            int i = cursorQuery.getInt(columnIndex2);
                            LGLog.i(AllAppsDBProvider.TAG, "old folderNumber : " + i);
                            LGLog.i(AllAppsDBProvider.TAG, "New folderNumber : " + j);
                            moveFolderToMenuFolder(db, i, j);
                            moveAllFolderShortcut(db, i, j);
                        } while (cursorQuery.moveToNext());
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    if (cursorQuery == null) {
                        return;
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    db.endTransaction();
                    if (cursorQuery == null) {
                        return;
                    }
                }
                cursorQuery.close();
            } catch (Throwable th) {
                db.endTransaction();
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                throw th;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:17:0x0078 A[DONT_GENERATE] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private void moveFolderToMenuFolder(android.database.sqlite.SQLiteDatabase r12, int r13, long r14) {
            /*
                r11 = this;
                java.lang.String r0 = "menu_reserved1"
                java.lang.String r1 = "AllAppsDBProvider"
                android.content.Context r2 = r11.mContext
                android.content.ContentResolver r2 = r2.getContentResolver()
                r9 = 0
                r10 = 0
                android.net.Uri r4 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI     // Catch: java.lang.Exception -> L29
                r5 = 0
                java.lang.String r6 = "_id=?"
                r3 = 1
                java.lang.String[] r7 = new java.lang.String[r3]     // Catch: java.lang.Exception -> L29
                java.lang.String r3 = java.lang.String.valueOf(r13)     // Catch: java.lang.Exception -> L29
                r7[r9] = r3     // Catch: java.lang.Exception -> L29
                r8 = 0
                r3 = r2
                android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)     // Catch: java.lang.Exception -> L29
                java.lang.String r4 = "iconType"
                int r4 = r3.getColumnIndexOrThrow(r4)     // Catch: java.lang.Exception -> L27
                goto L33
            L27:
                r4 = move-exception
                goto L2b
            L29:
                r4 = move-exception
                r3 = r10
            L2b:
                java.lang.String r4 = r4.toString()
                com.lge.launcher3.util.LGLog.e(r1, r4)
                r4 = r9
            L33:
                if (r3 == 0) goto L8c
            L35:
                boolean r5 = r3.moveToNext()     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                if (r5 == 0) goto L76
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                r5.<init>()     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                java.lang.String r6 = "_id="
                r5.append(r6)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                r5.append(r14)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                android.content.ContentValues r6 = new android.content.ContentValues     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                r6.<init>()     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                int r7 = r3.getInt(r9)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                r6.put(r0, r7)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                java.lang.String r7 = "folder_color"
                int r8 = r3.getInt(r4)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                r6.put(r7, r8)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                java.lang.Integer r7 = java.lang.Integer.valueOf(r9)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                r6.put(r0, r7)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                java.lang.String r7 = "app_page_child_infos"
                r12.update(r7, r6, r5, r10)     // Catch: java.lang.Throwable -> L7c android.database.sqlite.SQLiteException -> L7e
                goto L35
            L76:
                if (r3 == 0) goto L8c
            L78:
                r3.close()
                goto L8c
            L7c:
                r12 = move-exception
                goto L86
            L7e:
                java.lang.String r12 = "moveFolderToMenuFolder moveToNext error!"
                com.lge.launcher3.util.LGLog.e(r1, r12)     // Catch: java.lang.Throwable -> L7c
                if (r3 == 0) goto L8c
                goto L78
            L86:
                if (r3 == 0) goto L8b
                r3.close()
            L8b:
                throw r12
            L8c:
                long r12 = (long) r13
                android.net.Uri r12 = com.android.launcher3.LauncherSettings.Favorites.getContentUri(r12)
                r2.delete(r12, r10, r10)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.allapps.AllAppsDBProvider.DatabaseHelper.moveFolderToMenuFolder(android.database.sqlite.SQLiteDatabase, int, long):void");
        }

        /* JADX DEBUG: Failed to insert an additional move for type inference into block B:79:0x0121 */
        /* JADX DEBUG: Multi-variable search result rejected for r9v0, resolved type: android.content.ContentResolver */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:41:0x00f5 A[DONT_GENERATE, PHI: r7 r14
          0x00f5: PHI (r7v2 int) = (r7v7 int), (r7v12 int) binds: [B:47:0x0104, B:40:0x00f3] A[DONT_GENERATE, DONT_INLINE]
          0x00f5: PHI (r14v1 int) = (r14v3 int), (r14v5 int) binds: [B:47:0x0104, B:40:0x00f3] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARN: Removed duplicated region for block: B:51:0x010a  */
        /* JADX WARN: Type inference failed for: r7v4 */
        /* JADX WARN: Type inference failed for: r7v5, types: [java.lang.String, java.lang.String[]] */
        /* JADX WARN: Type inference failed for: r7v6 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private void moveAllFolderShortcut(android.database.sqlite.SQLiteDatabase r18, int r19, long r20) {
            /*
                r17 = this;
                java.lang.String r0 = "title"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "container="
                r1.append(r2)
                r2 = r19
                r1.append(r2)
                java.lang.String r5 = r1.toString()
                java.lang.String r1 = "AllAppsDBProvider"
                java.lang.String r2 = "moveAllFolderShortcut "
                com.lge.launcher3.util.LGLog.i(r1, r2)
                android.net.Uri r3 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI
                r8 = r17
                android.content.Context r2 = r8.mContext
                android.content.ContentResolver r9 = r2.getContentResolver()
                r4 = 0
                r6 = 0
                r7 = 0
                r2 = r9
                android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7)
                if (r2 != 0) goto L31
                return
            L31:
                int r3 = r2.getCount()
                r4 = 1
                if (r3 >= r4) goto L3c
                r2.close()
                return
            L3c:
                int r3 = r2.getCount()
                android.net.Uri[] r3 = new android.net.Uri[r3]
                int r4 = r2.getCount()
                android.content.ContentValues[] r4 = new android.content.ContentValues[r4]
                java.lang.String r7 = "_id"
                int r7 = r2.getColumnIndex(r7)     // Catch: java.lang.Throwable -> Lfb android.database.sqlite.SQLiteException -> Lfd
                java.lang.String r10 = "intent"
                int r10 = r2.getColumnIndexOrThrow(r10)     // Catch: java.lang.Throwable -> Lfb android.database.sqlite.SQLiteException -> Lfd
                int r11 = r2.getColumnIndexOrThrow(r0)     // Catch: java.lang.Throwable -> Lfb android.database.sqlite.SQLiteException -> Lfd
                java.lang.String r12 = "cellX"
                int r12 = r2.getColumnIndexOrThrow(r12)     // Catch: java.lang.Throwable -> Lfb android.database.sqlite.SQLiteException -> Lfd
                java.lang.String r13 = "cellY"
                int r13 = r2.getColumnIndexOrThrow(r13)     // Catch: java.lang.Throwable -> Lfb android.database.sqlite.SQLiteException -> Lfd
                r14 = 0
            L65:
                boolean r15 = r2.moveToNext()     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                if (r15 == 0) goto Lf2
                int r15 = r2.getInt(r7)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                long r5 = (long) r15     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                android.net.Uri r5 = com.android.launcher3.LauncherSettings.Favorites.getContentUri(r5)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                r3[r14] = r5     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                java.lang.String r5 = r2.getString(r10)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                r6 = 0
                android.content.Intent r5 = android.content.Intent.parseUri(r5, r6)     // Catch: java.lang.Throwable -> L80 java.net.URISyntaxException -> L84
                goto L8a
            L80:
                r0 = move-exception
                r7 = r6
                goto Lf1
            L84:
                java.lang.String r5 = "URISyntaxException : while "
                com.lge.launcher3.util.LGLog.i(r1, r5)     // Catch: java.lang.Throwable -> Lef
                r5 = 0
            L8a:
                if (r5 == 0) goto Le8
                android.content.ContentValues r6 = new android.content.ContentValues     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                r6.<init>()     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                java.lang.String r15 = "component_name"
                android.content.ComponentName r5 = r5.getComponent()     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                java.lang.String r5 = r5.flattenToShortString()     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                r6.put(r15, r5)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                java.lang.String r5 = "page_id"
                r16 = r7
                r15 = 0
                java.lang.Integer r7 = java.lang.Integer.valueOf(r15)     // Catch: android.database.sqlite.SQLiteException -> Le6 java.lang.Throwable -> Lfb
                r6.put(r5, r7)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                java.lang.String r5 = r2.getString(r11)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                r6.put(r0, r5)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                java.lang.String r5 = "cell_x"
                int r7 = r2.getInt(r12)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                r6.put(r5, r7)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                java.lang.String r5 = "cell_y"
                int r7 = r2.getInt(r13)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                r6.put(r5, r7)     // Catch: android.database.sqlite.SQLiteException -> Lf9 java.lang.Throwable -> Lfb
                java.lang.String r5 = "itemtype"
                r7 = 0
                java.lang.Integer r15 = java.lang.Integer.valueOf(r7)     // Catch: java.lang.Throwable -> Lfb android.database.sqlite.SQLiteException -> Lff
                r6.put(r5, r15)     // Catch: java.lang.Throwable -> Lfb android.database.sqlite.SQLiteException -> Lff
                java.lang.String r5 = "folder_number"
                java.lang.Long r15 = java.lang.Long.valueOf(r20)     // Catch: java.lang.Throwable -> Lfb android.database.sqlite.SQLiteException -> Lff
                r6.put(r5, r15)     // Catch: java.lang.Throwable -> Lfb android.database.sqlite.SQLiteException -> Lff
                int r5 = r14 + 1
                r4[r14] = r6     // Catch: android.database.sqlite.SQLiteException -> Le4 java.lang.Throwable -> Lfb
                r14 = r5
                goto Leb
            Le4:
                r14 = r5
                goto Lff
            Le6:
                r7 = r15
                goto Lff
            Le8:
                r16 = r7
                r7 = 0
            Leb:
                r7 = r16
                goto L65
            Lef:
                r0 = move-exception
                r7 = 0
            Lf1:
                throw r0     // Catch: java.lang.Throwable -> Lfb android.database.sqlite.SQLiteException -> Lff
            Lf2:
                r7 = 0
                if (r2 == 0) goto L107
            Lf5:
                r2.close()
                goto L107
            Lf9:
                r7 = 0
                goto Lff
            Lfb:
                r0 = move-exception
                goto L12a
            Lfd:
                r7 = 0
                r14 = r7
            Lff:
                java.lang.String r0 = "moveAllFolderShortcut moveToNext error!"
                com.lge.launcher3.util.LGLog.e(r1, r0)     // Catch: java.lang.Throwable -> Lfb
                if (r2 == 0) goto L107
                goto Lf5
            L107:
                r6 = r7
            L108:
                if (r6 >= r14) goto L129
                r0 = r4[r6]
                if (r0 == 0) goto L11e
                java.lang.String r0 = "TABLE_PAGEMENU_FOLDER_INFOS : while "
                com.lge.launcher3.util.LGLog.i(r1, r0)
                r0 = r4[r6]
                java.lang.String r2 = "app_page_folder_infos"
                r5 = r18
                r7 = 0
                r5.insert(r2, r7, r0)
                goto L121
            L11e:
                r5 = r18
                r7 = 0
            L121:
                r0 = r3[r6]
                r9.delete(r0, r7, r7)
                int r6 = r6 + 1
                goto L108
            L129:
                return
            L12a:
                if (r2 == 0) goto L12f
                r2.close()
            L12f:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.allapps.AllAppsDBProvider.DatabaseHelper.moveAllFolderShortcut(android.database.sqlite.SQLiteDatabase, int, long):void");
        }

        private void makeHideList(SQLiteDatabase db) throws Throwable {
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            Cursor cursor = null;
            try {
                try {
                    Cursor cursorQuery = db.query(AllAppsDBProvider.TABLE_PAGEMENU_CHILD_INFOS, new String[]{"_id", "component_name"}, "menu_reserved1=1", null, null, null, null);
                    try {
                        int columnIndex = cursorQuery.getColumnIndex("_id");
                        int columnIndex2 = cursorQuery.getColumnIndex("component_name");
                        if (cursorQuery.moveToFirst()) {
                            do {
                                long j = cursorQuery.getLong(columnIndex);
                                String string = cursorQuery.getString(columnIndex2);
                                contentValues.clear();
                                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_FOLDER_UNEDITABLE, (Integer) 0);
                                db.update(AllAppsDBProvider.TABLE_PAGEMENU_CHILD_INFOS, contentValues, "_id=" + j, null);
                                insertHideItem(db, AllAppsConstant.TABLE_APP_INFOS, string);
                            } while (cursorQuery.moveToNext());
                        }
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    } catch (RuntimeException e) {
                        e = e;
                        cursor = cursorQuery;
                        e.printStackTrace();
                        db.endTransaction();
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        cursor = cursorQuery;
                        db.endTransaction();
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (RuntimeException e2) {
                e = e2;
            }
        }

        void insertHideItem(SQLiteDatabase db, String tableName, String cmpName) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("component_name", cmpName);
            db.insert(tableName, null, contentValues);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws Throwable {
            LGLog.i(AllAppsDBProvider.TAG, "onUpgrade , current version : " + oldVersion + " , new version : " + newVersion);
            if (oldVersion < 5 && newVersion >= 5) {
                convertComponentName(db);
                oldVersion = 5;
            }
            if (oldVersion == 5 && newVersion == 6) {
                db.execSQL("DROP TABLE IF EXISTS app_infos");
                db.execSQL("DROP TABLE IF EXISTS app_download_infos");
                db.execSQL(AllAppsDBProvider.CREATE_APP_INFO_TABLE);
                db.execSQL(AllAppsDBProvider.CREATE_WIDGET_INFO_TABLE);
                db.execSQL(AllAppsDBProvider.CREATE_PAGEMENU_FOLDER_INFO_TABLE);
                makeHideList(db);
                upgradeFolderInfo(db);
                oldVersion = 6;
            }
            if (oldVersion == 6) {
                AllAppsDBProvider.addProfileColumn(this.mContext, db);
                oldVersion = 7;
            }
            if (oldVersion == 7) {
                AllAppsDBProvider.addRankOptionsColumn(db);
                oldVersion = 100;
            }
            if (oldVersion != 100) {
                dropTables(db);
                createTables(db);
            }
            this.mRepackagedList = null;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dropTables(db);
            createTables(db);
        }

        private void dropTables(SQLiteDatabase db) {
            AllAppsDBProvider.dropAllAppsDBTables(db);
        }
    }

    public static void createAllAppsTables(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            try {
                db.execSQL(CREATE_PAGEMENU_CHILD_INFO_TABLE_IF);
                db.execSQL(CREATE_APP_INFO_TABLE_IF);
                db.execSQL(CREATE_WIDGET_INFO_TABLE_IF);
                db.execSQL(CREATE_PAGEMENU_FOLDER_INFO_TABLE_IF);
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                LGLog.e(TAG, e.getMessage(), e);
            }
        } finally {
            db.endTransaction();
        }
    }

    public static void dropAllAppsDBTables(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            try {
                db.execSQL("DROP TABLE IF EXISTS app_page_child_infos");
                db.execSQL("DROP TABLE IF EXISTS app_infos");
                db.execSQL("DROP TABLE IF EXISTS widget_infos");
                db.execSQL("DROP TABLE IF EXISTS app_page_folder_infos");
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                LGLog.e(TAG, e.getMessage(), e);
            }
        } finally {
            db.endTransaction();
        }
    }

    public static boolean addProfileColumn(Context context, SQLiteDatabase db) {
        boolean z;
        db.beginTransaction();
        try {
            try {
                long serialNumberForUser = UserManagerCompat.getInstance(context).getSerialNumberForUser(Process.myUserHandle());
                db.execSQL("ALTER TABLE app_page_folder_infos ADD COLUMN profileId INTEGER NOT NULL DEFAULT " + serialNumberForUser + ";");
                db.execSQL("ALTER TABLE app_infos ADD COLUMN profileId INTEGER NOT NULL DEFAULT " + serialNumberForUser + ";");
                db.execSQL("ALTER TABLE widget_infos ADD COLUMN profileId INTEGER NOT NULL DEFAULT " + serialNumberForUser + ";");
                db.setTransactionSuccessful();
                z = true;
            } catch (SQLException e) {
                LGLog.e(TAG, e.getMessage(), e);
                z = false;
            }
            return z;
        } finally {
            db.endTransaction();
        }
    }

    public static boolean addRankOptionsColumn(SQLiteDatabase db) {
        boolean z;
        db.beginTransaction();
        try {
            try {
                db.execSQL("ALTER TABLE app_page_folder_infos ADD COLUMN rank INTEGER NOT NULL DEFAULT 0;");
                db.execSQL("ALTER TABLE app_page_child_infos ADD COLUMN options INTEGER NOT NULL DEFAULT 0;");
                db.setTransactionSuccessful();
                z = true;
            } catch (SQLException e) {
                LGLog.e(TAG, e.getMessage(), e);
                z = false;
            }
            return z;
        } finally {
            db.endTransaction();
        }
    }

    public static boolean resetFolderColor(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_PAGEMENU_CHILD_FOLDERCOLOR, (Integer) 0);
            db.update(TABLE_PAGEMENU_CHILD_INFOS, contentValues, "itemtype = 2", null);
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            LGLog.e(TAG, e.getMessage(), e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        try {
            int iDelete = sOpenHelper.get(this.mId).getWritableDatabase().delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
            if (iDelete > 0) {
                sendNotify(uri);
            }
            return iDelete;
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            return 0;
        }
    }

    public static void destroyMenuDb(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            try {
                db.execSQL("DELETE FROM app_page_child_infos");
                db.execSQL("DELETE FROM app_page_folder_infos");
                db.execSQL("DELETE FROM app_infos");
                db.execSQL("DELETE FROM widget_infos");
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                LGLog.e(TAG, e.getMessage(), e);
            }
        } finally {
            db.endTransaction();
        }
    }

    public static void destroyMenuDb() {
        SparseArray<DatabaseHelper> sparseArray = sOpenHelper;
        if (sparseArray != null) {
            if (sparseArray.get(0) != null) {
                destroyMenuDb(sOpenHelper.get(0).getWritableDatabase());
            }
            if (!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() || sOpenHelper.get(1) == null) {
                return;
            }
            destroyMenuDb(sOpenHelper.get(1).getWritableDatabase());
        }
    }

    public static void closeDB() {
        SparseArray<DatabaseHelper> sparseArray = sOpenHelper;
        if (sparseArray != null) {
            if (sparseArray.get(0) != null) {
                LGLog.i(TAG, "closeDB()");
                sOpenHelper.get(0).close();
            }
            if (!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() || sOpenHelper.get(1) == null) {
                return;
            }
            LGLog.i(TAG, "closeSwivelDB()");
            sOpenHelper.get(1).close();
        }
    }

    public static boolean isSwivelUri(Uri uri) {
        return CONTENT_PAGEMENU_CHILD_URI_FOR_SWIVEL.equals(uri) || CONTENT_PAGEMENU_FOLDER_URI_FOR_SWIVEL.equals(uri);
    }

    public static Uri getContentPageMenuChildURi(boolean isSwivel) {
        if (isSwivel) {
            return CONTENT_PAGEMENU_CHILD_URI_FOR_SWIVEL;
        }
        return CONTENT_PAGEMENU_CHILD_URI;
    }

    public static Uri getContentPageMenuFolderUri(boolean isSwivel) {
        if (isSwivel) {
            return CONTENT_PAGEMENU_FOLDER_URI_FOR_SWIVEL;
        }
        return CONTENT_PAGEMENU_FOLDER_URI;
    }
}
