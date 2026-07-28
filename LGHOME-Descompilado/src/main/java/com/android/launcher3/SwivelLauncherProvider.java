package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;
import android.text.TextUtils;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.config.ProviderConfig;
import com.android.launcher3.util.ManagedProfileHeuristic;
import com.lge.launcher3.LauncherExtension;
import com.lge.launcher3.receiver.DefaultWorkspaceLoader;
import com.lge.launcher3.silentota.SilentOTASwivel;
import com.lge.launcher3.util.LGLog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

/* JADX INFO: loaded from: classes.dex */
public class SwivelLauncherProvider extends LauncherProvider {
    public static final String AUTHORITY = ProviderConfig.AUTHORITY_SWIVEL;
    public static final String DATABASE_NAME = "launcher_swivel.db";
    private static final int DATABASE_VERSION = 105;
    static final String EMPTY_SWIVEL_DATABASE_CREATED = "EMPTY_SWIVEL_DATABASE_CREATED";
    private static final String EXIST_DATABASE = "SWIVEL_EXIST_DATABASE";
    private static final boolean LOGD = false;
    static final String TABLE_FAVORITES = "favorites";
    private static final String TAG = "SwivelLauncherProvider";
    private boolean mIsRestoreDB;
    LauncherProviderChangeListener mListener;
    public DatabaseHelper mOpenHelper;

    public static String getLauncherDBFileName() {
        return "launcher_swivel.db";
    }

    @Override // com.android.launcher3.LauncherProvider, android.content.ContentProvider
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
        if (instanceNoCreate == null) {
            return;
        }
        instanceNoCreate.getModel().dumpState("", fd, writer, args);
    }

    @Override // com.android.launcher3.LauncherProvider, android.content.ContentProvider
    public boolean onCreate() {
        Context context = getContext();
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        this.mOpenHelper = new DatabaseHelper(context, "launcher_swivel.db");
        StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        return true;
    }

    @Override // com.android.launcher3.LauncherProvider
    public boolean wasNewDbCreated() {
        return this.mOpenHelper.wasNewDbCreated();
    }

    @Override // com.android.launcher3.LauncherProvider
    public void setLauncherProviderChangeListener(LauncherProviderChangeListener listener) {
        this.mListener = listener;
        this.mOpenHelper.mListener = listener;
    }

    @Override // com.android.launcher3.LauncherProvider, android.content.ContentProvider
    public String getType(Uri uri) {
        SqlArguments sqlArguments = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(sqlArguments.where)) {
            return "vnd.android.cursor.dir/" + sqlArguments.table;
        }
        return "vnd.android.cursor.item/" + sqlArguments.table;
    }

    @Override // com.android.launcher3.LauncherProvider, android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (this.mIsRestoreDB) {
            return null;
        }
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables(sqlArguments.table);
        Cursor cursorQuery = sQLiteQueryBuilder.query(this.mOpenHelper.getWritableDatabase(), projection, sqlArguments.where, sqlArguments.args, null, null, sortOrder);
        cursorQuery.setNotificationUri(getContext().getContentResolver(), uri);
        return cursorQuery;
    }

    public static long dbInsertAndCheck(DatabaseHelper helper, SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
        if (values == null) {
            throw new RuntimeException("Error: attempting to insert null values");
        }
        if (!values.containsKey("_id")) {
            throw new RuntimeException("Error: attempting to add item without specifying an id");
        }
        helper.checkId(table, values);
        return db.insert(table, nullColumnHack, values);
    }

    @Override // com.android.launcher3.LauncherProvider, android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues initialValues) {
        SqlArguments sqlArguments = new SqlArguments(uri);
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        addModifiedTime(initialValues);
        long jDbInsertAndCheck = dbInsertAndCheck(this.mOpenHelper, writableDatabase, sqlArguments.table, (String) null, initialValues);
        LGLog.i(TAG, "insert() rowId = " + jDbInsertAndCheck);
        if (jDbInsertAndCheck < 0) {
            return null;
        }
        Uri uriWithAppendedId = ContentUris.withAppendedId(uri, jDbInsertAndCheck);
        notifyListeners();
        return uriWithAppendedId;
    }

    @Override // com.android.launcher3.LauncherProvider, android.content.ContentProvider
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SqlArguments sqlArguments = new SqlArguments(uri);
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            int length = values.length;
            for (int i = 0; i < length; i++) {
                addModifiedTime(values[i]);
                if (dbInsertAndCheck(this.mOpenHelper, writableDatabase, sqlArguments.table, (String) null, values[i]) < 0) {
                    return 0;
                }
            }
            writableDatabase.setTransactionSuccessful();
            writableDatabase.endTransaction();
            notifyListeners();
            return values.length;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    @Override // com.android.launcher3.LauncherProvider, android.content.ContentProvider
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentProviderResult[] contentProviderResultArrApplyBatch = super.applyBatch(operations);
            writableDatabase.setTransactionSuccessful();
            return contentProviderResultArrApplyBatch;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    @Override // com.android.launcher3.LauncherProvider, android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        int iDelete = this.mOpenHelper.getWritableDatabase().delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
        LGLog.i(TAG, "delete() count = " + iDelete);
        if (iDelete > 0) {
            notifyListeners();
        }
        return iDelete;
    }

    @Override // com.android.launcher3.LauncherProvider, android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        addModifiedTime(values);
        int iUpdate = this.mOpenHelper.getWritableDatabase().update(sqlArguments.table, values, sqlArguments.where, sqlArguments.args);
        if (iUpdate > 0) {
            notifyListeners();
        }
        return iUpdate;
    }

    @Override // com.android.launcher3.LauncherProvider, android.content.ContentProvider
    public Bundle call(String method, final String arg, final Bundle extras) {
        LGLog.i(TAG, "swivel provider call() : " + method);
        method.hashCode();
        if (method.equals(LauncherSettings.Settings.METHOD_NEW_ITEM_ID)) {
            Bundle bundle = new Bundle();
            bundle.putLong("value", this.mOpenHelper.generateNewItemId());
            return bundle;
        }
        if (!method.equals(LauncherSettings.Settings.METHOD_LOAD_DEFAULT_SWIVEL_FAVORITES)) {
            return null;
        }
        loadDefaultSwivelFavoritesIfNecessary();
        return null;
    }

    private void notifyListeners() {
        LauncherBackupAgentHelper.dataChanged(getContext());
        LauncherProviderChangeListener launcherProviderChangeListener = this.mListener;
        if (launcherProviderChangeListener != null) {
            launcherProviderChangeListener.onLauncherProviderChange();
        }
    }

    public static void addModifiedTime(ContentValues values) {
        values.put(LauncherSettings.ChangeLogColumns.MODIFIED, Long.valueOf(System.currentTimeMillis()));
    }

    @Override // com.android.launcher3.LauncherProvider
    public synchronized void createEmptyDB() {
        DatabaseHelper databaseHelper = this.mOpenHelper;
        databaseHelper.createEmptyDB(databaseHelper.getWritableDatabase());
    }

    @Override // com.android.launcher3.LauncherProvider
    public void clearFlagEmptyDbCreated() {
        getContext().getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).edit().remove(EMPTY_SWIVEL_DATABASE_CREATED).commit();
    }

    private DefaultLayoutParser getDefaultLayoutParser() {
        int iDFromCAList = DefaultWorkspaceLoader.getIDFromCAList(getContext());
        if (iDFromCAList == 0) {
            iDFromCAList = LauncherAppState.getInstance(getContext()).getInvariantDeviceProfile().defaultLayoutId;
        }
        return new DefaultLayoutParser(getContext(), this.mOpenHelper.mAppWidgetHost, this.mOpenHelper, getContext().getResources(), iDFromCAList);
    }

    public static class DatabaseHelper extends SQLiteOpenHelper implements AutoInstallsLayout.LayoutParserCallback {
        private static final String TABLE_SHARING_CONTENTS = "sharingContents";
        final AppWidgetHost mAppWidgetHost;
        private final Context mContext;
        LauncherProviderChangeListener mListener;
        private long mMaxItemId;
        private boolean mNewDbCreated;
        public boolean mRequestClearDB;

        public DatabaseHelper(Context context, String launcherDBFileName) {
            super(context, launcherDBFileName, (SQLiteDatabase.CursorFactory) null, 105);
            this.mMaxItemId = -1L;
            this.mNewDbCreated = false;
            this.mRequestClearDB = false;
            this.mContext = context;
            this.mAppWidgetHost = new AppWidgetHost(context, 1024);
            if (this.mMaxItemId == -1) {
                this.mMaxItemId = initializeMaxItemId(getWritableDatabase());
            }
        }

        public boolean wasNewDbCreated() {
            return this.mNewDbCreated;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            this.mMaxItemId = 1L;
            this.mNewDbCreated = true;
            addFavoritesTable(db, false);
            this.mMaxItemId = initializeMaxItemId(db);
            setFlagEmptyDbCreated();
            ManagedProfileHeuristic.processAllUsers(Collections.emptyList(), this.mContext);
            this.mRequestClearDB = false;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public SQLiteDatabase getWritableDatabase() {
            try {
                return super.getWritableDatabase();
            } catch (SQLiteFullException e) {
                LGLog.e(SwivelLauncherProvider.TAG, "Disk full, all write operations will be ignored", e);
                return null;
            } catch (SQLiteException e2) {
                LGLog.d(SwivelLauncherProvider.TAG, "Ignoring sqlite exception", e2);
                return null;
            }
        }

        public long getDefaultUserSerial() {
            return UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(Process.myUserHandle());
        }

        private void addFavoritesTable(SQLiteDatabase db, boolean optional) {
            LauncherSettings.Favorites.addTableToDbSwivel(db, getDefaultUserSerial(), optional);
        }

        private void setFlagEmptyDbCreated() {
            SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0);
            sharedPreferences.edit().putBoolean(SwivelLauncherProvider.EMPTY_SWIVEL_DATABASE_CREATED, true).commit();
            sharedPreferences.edit().putBoolean(SwivelLauncherProvider.EXIST_DATABASE, true).commit();
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            LGLog.i(SwivelLauncherProvider.TAG, "Upgrade database " + oldVersion + " -> " + newVersion);
            if (oldVersion != 1) {
                createEmptyDB(db);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            LGLog.w(SwivelLauncherProvider.TAG, "Database version downgrade from: " + oldVersion + " to " + newVersion + ". Wiping databse.", new int[0]);
            createEmptyDB(db);
        }

        public void createEmptyDB(SQLiteDatabase db) {
            LGLog.i(SwivelLauncherProvider.TAG, "Create empty database");
            db.beginTransaction();
            try {
                db.execSQL("DROP TABLE IF EXISTS favorites");
                onCreate(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        @Override // com.android.launcher3.AutoInstallsLayout.LayoutParserCallback
        public long generateNewItemId() {
            long j = this.mMaxItemId;
            if (j < 0) {
                throw new RuntimeException("Error: max item id was not initialized");
            }
            long j2 = j + 1;
            this.mMaxItemId = j2;
            return j2;
        }

        @Override // com.android.launcher3.AutoInstallsLayout.LayoutParserCallback
        public long insertAndCheck(SQLiteDatabase db, ContentValues values) {
            return SwivelLauncherProvider.dbInsertAndCheck(this, db, "favorites", (String) null, values);
        }

        public void updateMaxItemId(long id) {
            this.mMaxItemId = id + 1;
        }

        public void checkId(String table, ContentValues values) {
            long jLongValue = values.getAsLong("_id").longValue();
            if ("favorites".equals(table)) {
                this.mMaxItemId = Math.max(jLongValue, this.mMaxItemId);
            }
        }

        private long initializeMaxItemId(SQLiteDatabase db) {
            return SwivelLauncherProvider.getMaxId(db, "favorites");
        }

        int loadFavorites(SQLiteDatabase db, SwivelLayoutParser loader) {
            int iLoadLayout = loader.loadLayout(db);
            this.mMaxItemId = initializeMaxItemId(db);
            return iLoadLayout;
        }

        private boolean hasTable(SQLiteDatabase db, String tableName) {
            Cursor cursorQuery = db.query("sqlite_master", new String[]{"name"}, "type='table' AND name LIKE ?", new String[]{tableName}, null, null, null);
            if (cursorQuery == null) {
                return false;
            }
            boolean z = cursorQuery.getCount() != 0;
            cursorQuery.close();
            return z;
        }

        private boolean hasColumn(SQLiteDatabase db, String tableName, String columnName) {
            Cursor cursorQuery = db.query(tableName, null, null, null, null, null, null);
            if (cursorQuery != null) {
                z = cursorQuery.getColumnIndex(columnName) != -1;
                cursorQuery.close();
            }
            return z;
        }

        private void addIconChangeColumn(SQLiteDatabase db) {
            if (!hasColumn(db, "favorites", LauncherSettings.BaseLauncherColumns.ICON_ID)) {
                db.execSQL("ALTER TABLE favorites ADD COLUMN iconId TEXT;");
            }
            if (hasColumn(db, "favorites", LauncherSettings.BaseLauncherColumns.USER_CUSTOMIZED_ICON)) {
                return;
            }
            db.execSQL("ALTER TABLE favorites ADD COLUMN userCustomizedIcon BLOB;");
        }
    }

    static long getMaxId(SQLiteDatabase db, String table) {
        long j = -1;
        try {
            Cursor cursorQuery = db.query(table, new String[]{"MAX(_id)"}, null, null, null, null, null);
            long j2 = (cursorQuery == null || !cursorQuery.moveToNext()) ? -1L : cursorQuery.getLong(0);
            if (cursorQuery != null) {
                try {
                    cursorQuery.close();
                } catch (SQLException e) {
                    e = e;
                    j = j2;
                    e.printStackTrace();
                    return j;
                }
            }
            if (j2 != -1) {
                return j2;
            }
            throw new RuntimeException("Error: could not query max id in " + table);
        } catch (SQLException e2) {
            e = e2;
        }
    }

    static class SqlArguments {
        public final String[] args;
        public final String table;
        public final String where;

        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
                return;
            }
            if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
            if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            }
            this.table = url.getPathSegments().get(0);
            this.where = "_id=" + ContentUris.parseId(url);
            this.args = null;
        }

        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = null;
                this.args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }

    private void loadDefaultSwivelFavoritesIfNecessary() {
        String str = TAG;
        LGLog.i(str, " loadDefaultSwivelFavoritesIfNecessary");
        if (getContext().getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).getBoolean(EMPTY_SWIVEL_DATABASE_CREATED, false)) {
            SwivelLayoutParser swivelLayoutParser = (SwivelLayoutParser) createWorkspaceLoaderFromAppRestriction();
            if (swivelLayoutParser == null) {
                LGLog.i(str, "start to parse swivel layout");
                swivelLayoutParser = new SwivelLayoutParser(getContext(), this.mOpenHelper, getContext().getResources(), getContext().getResources().getIdentifier("default_workspace_items_swivel", "xml", getContext().getPackageName()), "favorites");
                new SilentOTASwivel(getContext());
            }
            createEmptyDB();
            DatabaseHelper databaseHelper = this.mOpenHelper;
            if (databaseHelper.loadFavorites(databaseHelper.getWritableDatabase(), swivelLayoutParser) <= 0) {
                LGLog.i(str, "No swivel items are initially inserted");
            }
            clearFlagEmptyDbCreated();
        }
        Launcher launcher = (Launcher) LauncherAppState.getInstance(getContext()).getModel().getCallback();
        if (launcher != null) {
            LGLog.i(str, "init swivel items on initial database inserted");
            ((LauncherExtension) launcher).initSwivelItems();
        }
    }
}
