package com.android.launcher3;

import android.app.LGSharedPreferences;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.config.ProviderConfig;
import com.android.launcher3.util.ManagedProfileHeuristic;
import com.lge.launcher3.R;
import com.lge.launcher3.adaptive.AdaptiveTextUtil;
import com.lge.launcher3.backuprestore.RestoreHome4;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.receiver.DefaultWorkspaceLoader;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.sortappsby.SortAppsByConst;
import com.lge.launcher3.sortappsby.SortAppsByManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import com.lge.launcher3.util.UserUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class LauncherProvider extends ContentProvider {
    public static final String AUTHORITY = ProviderConfig.AUTHORITY;
    private static final int DATABASE_VERSION = 105;
    static final String EMPTY_DATABASE_CREATED = "EMPTY_DATABASE_CREATED";
    static final String EMPTY_DATABASE_CREATED_ALLAPPS = "EMPTY_DATABASE_CREATED_ALLAPPS";
    static final String EMPTY_DATABASE_CREATED_EASYHOME = "EMPTY_DATABASE_CREATED_EASYHOME";
    static final String EXIST_DATABASE = "EXIST_DATABASE";
    private static final boolean LOGD = false;
    static final String OLD_AUTHORITY = "com.android.launcher2.settings";
    private static final String RESTRICTION_PACKAGE_NAME = "workspace.configuration.package.name";
    static final String TABLE_FAVORITES = "favorites";
    static final String TABLE_HOME_PREFERENCES = "homePreferences";
    static final String TABLE_WORKSPACE_DETAIL = "workspaceDetail";
    static final String TABLE_WORKSPACE_SCREENS = "workspaceScreens";
    private static final String TAG = "LauncherProvider";
    private static final String URI_PARAM_IS_EXTERNAL_ADD = "isExternalAdd";
    private static boolean sIsDisabledAllApps;
    private static boolean sIsDisabledEasyHome;
    private boolean mIsRestoreDB;
    LauncherProviderChangeListener mListener;
    public DatabaseHelper mOpenHelper;
    public DatabaseHelper mOpenHelper_launcherfacade;

    @Override // android.content.ContentProvider
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
        if (instanceNoCreate == null) {
            return;
        }
        instanceNoCreate.getModel().dumpState("", fd, writer, args);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        Context context = getContext();
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        this.mOpenHelper = new DatabaseHelper(context, getLauncherDBFileName(context));
        StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        return true;
    }

    public boolean wasNewDbCreated() {
        return this.mOpenHelper.wasNewDbCreated();
    }

    public void setLauncherProviderChangeListener(LauncherProviderChangeListener listener) {
        this.mListener = listener;
        this.mOpenHelper.mListener = listener;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        SqlArguments sqlArguments = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(sqlArguments.where)) {
            return "vnd.android.cursor.dir/" + sqlArguments.table;
        }
        return "vnd.android.cursor.item/" + sqlArguments.table;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase writableDatabase;
        if (this.mIsRestoreDB) {
            return null;
        }
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables(sqlArguments.table);
        String queryParameter = uri.getQueryParameter(URI_PARAM_IS_EXTERNAL_ADD);
        if (LGFeatureConfig.isLauncherFacadeOperator() && "true".equals(queryParameter) && !sIsDisabledAllApps) {
            LGLog.d(TAG, "query the launcherfacade db");
            if (this.mOpenHelper_launcherfacade == null) {
                this.mOpenHelper_launcherfacade = new DatabaseHelper(getContext(), LauncherFiles.LAUNCHER_DB);
            }
            writableDatabase = this.mOpenHelper_launcherfacade.getWritableDatabase();
        } else {
            writableDatabase = this.mOpenHelper.getWritableDatabase();
        }
        Cursor cursorQuery = sQLiteQueryBuilder.query(writableDatabase, projection, sqlArguments.where, sqlArguments.args, null, null, sortOrder);
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

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues initialValues) {
        LauncherAppState instanceNoCreate;
        SqlArguments sqlArguments = new SqlArguments(uri);
        String queryParameter = uri.getQueryParameter(URI_PARAM_IS_EXTERNAL_ADD);
        boolean z = queryParameter != null && "true".equals(queryParameter);
        if (LGFeatureConfig.isLauncherFacadeOperator() && z) {
            LGLog.d(TAG, "insert isInSprintID is true");
            LauncherModel.isInSprintID = true;
        }
        if (LGFeatureConfig.isLauncherFacadeOperator() && z && !sIsDisabledAllApps) {
            LGLog.d(TAG, "insert to launcherfacade db");
            if (this.mOpenHelper_launcherfacade == null) {
                this.mOpenHelper_launcherfacade = new DatabaseHelper(getContext(), LauncherFiles.LAUNCHER_DB);
            }
            if (!this.mOpenHelper_launcherfacade.initializeExternalAdd(initialValues)) {
                return null;
            }
            SQLiteDatabase writableDatabase = this.mOpenHelper_launcherfacade.getWritableDatabase();
            addModifiedTime(initialValues);
            long jDbInsertAndCheck = dbInsertAndCheck(this.mOpenHelper_launcherfacade, writableDatabase, sqlArguments.table, null, initialValues);
            if (jDbInsertAndCheck < 0) {
                return null;
            }
            Uri uriWithAppendedId = ContentUris.withAppendedId(uri, jDbInsertAndCheck);
            notifyListeners();
            LauncherAppState instanceNoCreate2 = LauncherAppState.getInstanceNoCreate();
            if (instanceNoCreate2 != null) {
                instanceNoCreate2.reloadWorkspace();
            }
            return uriWithAppendedId;
        }
        if (z && !this.mOpenHelper.initializeExternalAdd(initialValues)) {
            return null;
        }
        SQLiteDatabase writableDatabase2 = this.mOpenHelper.getWritableDatabase();
        addModifiedTime(initialValues);
        long jDbInsertAndCheck2 = dbInsertAndCheck(this.mOpenHelper, writableDatabase2, sqlArguments.table, null, initialValues);
        if (jDbInsertAndCheck2 < 0) {
            return null;
        }
        Uri uriWithAppendedId2 = ContentUris.withAppendedId(uri, jDbInsertAndCheck2);
        notifyListeners();
        if (z && (instanceNoCreate = LauncherAppState.getInstanceNoCreate()) != null) {
            instanceNoCreate.reloadWorkspace();
        }
        return uriWithAppendedId2;
    }

    @Override // android.content.ContentProvider
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SqlArguments sqlArguments = new SqlArguments(uri);
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            int length = values.length;
            for (int i = 0; i < length; i++) {
                addModifiedTime(values[i]);
                if (dbInsertAndCheck(this.mOpenHelper, writableDatabase, sqlArguments.table, null, values[i]) < 0) {
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

    @Override // android.content.ContentProvider
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

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase writableDatabase;
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        String queryParameter = uri.getQueryParameter(URI_PARAM_IS_EXTERNAL_ADD);
        if (LGFeatureConfig.isLauncherFacadeOperator() && "true".equals(queryParameter) && !sIsDisabledAllApps) {
            LGLog.d(TAG, "delete from launcherfacade db");
            if (this.mOpenHelper_launcherfacade == null) {
                this.mOpenHelper_launcherfacade = new DatabaseHelper(getContext(), LauncherFiles.LAUNCHER_DB);
            }
            writableDatabase = this.mOpenHelper_launcherfacade.getWritableDatabase();
        } else {
            writableDatabase = this.mOpenHelper.getWritableDatabase();
        }
        int iDelete = writableDatabase.delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
        if (iDelete > 0) {
            notifyListeners();
        }
        return iDelete;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        addModifiedTime(values);
        int iUpdate = this.mOpenHelper.getWritableDatabase().update(sqlArguments.table, values, sqlArguments.where, sqlArguments.args);
        if (iUpdate > 0) {
            notifyListeners();
        }
        return iUpdate;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.content.ContentProvider
    public Bundle call(String method, final String arg, final Bundle extras) throws Throwable {
        LGLog.i(TAG, "call() : " + method);
        method.hashCode();
        switch (method) {
            case "delete_empty_folders":
                Bundle bundle = new Bundle();
                bundle.putSerializable("value", deleteEmptyFolders());
                return bundle;
            case "end_restore_db":
                this.mIsRestoreDB = false;
                return null;
            case "update_folder_items_rank":
                updateFolderItemsRank();
                return null;
            case "was_new_db_created":
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("value", wasNewDbCreated());
                return bundle2;
            case "get_boolean_setting":
                Bundle bundle3 = new Bundle();
                bundle3.putBoolean("value", getContext().getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).getBoolean(arg, extras.getBoolean(LauncherSettings.Settings.EXTRA_DEFAULT_VALUE)));
                return bundle3;
            case "update_max_screen_id":
                updateMaxScreenId(Long.parseLong(arg));
                return null;
            case "remove_ghost_widgets":
                DatabaseHelper databaseHelper = this.mOpenHelper;
                databaseHelper.removeGhostWidgets(databaseHelper.getWritableDatabase());
                return null;
            case "delete_appwidget_ids":
                Bundle bundle4 = new Bundle();
                deleteAppWidgetIds(extras.getIntArray(LauncherSettings.AppWidgets.EXTRA_APPWIDGET_IDS));
                return bundle4;
            case "get_feature_value":
                Bundle bundle5 = new Bundle();
                bundle5.putBoolean("value", LGHomeFeature.Config.FEATURE_SWIPE_DOWN_SUB_SWIVEL_HOME.getValue());
                return bundle5;
            case "generate_new_item_id":
                Bundle bundle6 = new Bundle();
                bundle6.putLong("value", this.mOpenHelper.generateNewItemId());
                return bundle6;
            case "generate_new_screen_id":
                Bundle bundle7 = new Bundle();
                bundle7.putLong("value", this.mOpenHelper.generateNewScreenId());
                return bundle7;
            case "clear_empty_db_flag":
                clearFlagEmptyDbCreated();
                return null;
            case "migrate_launcher2_shortcuts":
                migrateLauncher2Shortcuts();
                return null;
            case "homesettings_shared_prefs":
                Bundle bundle8 = new Bundle();
                if (HomeSettingsSharedPreferences.ENABLE_CONTINUOUS_LOOP.equals(arg)) {
                    bundle8.putBoolean("value", getContext().getSharedPreferences(HomeSettingsSharedPreferences.SHARED_PREFERENCES_KEY, 0).getBoolean(arg, false));
                    return bundle8;
                }
                if (HomeSettingsSharedPreferences.SELECTED_SCREEN_EFFECT_INDEX.equals(arg)) {
                    bundle8.putInt("value", getContext().getSharedPreferences(HomeSettingsSharedPreferences.SHARED_PREFERENCES_KEY, 0).getInt(arg, 0));
                    return bundle8;
                }
                if (HomeSettingsSharedPreferences.SELECTED_SECOND_SCREEN_EFFECT_INDEX.equals(arg)) {
                    bundle8.putInt("value", getContext().getSharedPreferences(HomeSettingsSharedPreferences.SHARED_PREFERENCES_KEY, 0).getInt(arg, 0));
                    return bundle8;
                }
                if (HomeSettingsSharedPreferences.ENABLE_HOMESCREEN_LOCK.equals(arg)) {
                    bundle8.putBoolean("value", HomeSettingsSharedPreferences.getHomescreenLockEnabled(getContext()));
                    return bundle8;
                }
                if (HomeSettingsSharedPreferences.ENABLE_SWING_HOME_SCREEN_LOCK.equals(arg)) {
                    bundle8.putBoolean("value", HomeSettingsSharedPreferences.getSwingHomeLockEnabled(getContext()));
                    return bundle8;
                }
                if (HomeSettingsSharedPreferences.SELECTED_SWIPE_DOWN_SUB_SWIVEL_HOME_INDEX.equals(arg)) {
                    String string = getContext().getSharedPreferences(HomeSettingsSharedPreferences.SHARED_PREFERENCES_KEY, 0).getString(arg, getContext().getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_default));
                    if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_SUB_SWIVEL_HOME.getValue() && !LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() && string.equals(getContext().getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_integrated_search))) {
                        string = getContext().getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_default);
                    } else if (!LGHomeFeature.Config.FEATURE_SWIPE_DOWN_SUB_SWIVEL_HOME.getValue()) {
                        string = getContext().getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_none);
                    }
                    bundle8.putString("value", string);
                    return bundle8;
                }
                return null;
            case "convert_shortcuts_to_launcher_activities":
                convertShortcutsToLauncherActivities();
                return null;
            case "load_default_favorites":
                loadDefaultFavoritesIfNecessary();
                return null;
            case "delete_database":
                deleteDatabase();
                return null;
            case "get_empty_db_flag":
                Bundle bundle9 = new Bundle();
                bundle9.putBoolean("value", Utilities.getPrefs(getContext()).getBoolean(EMPTY_DATABASE_CREATED, false));
                return bundle9;
            case "is_restore_db":
                Bundle bundle10 = new Bundle();
                bundle10.putBoolean("value", this.mIsRestoreDB);
                return bundle10;
            case "set_boolean_setting":
                boolean z = extras.getBoolean("value");
                getContext().getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).edit().putBoolean(arg, z).apply();
                LauncherProviderChangeListener launcherProviderChangeListener = this.mListener;
                if (launcherProviderChangeListener != null) {
                    launcherProviderChangeListener.onSettingsChanged(arg, z);
                }
                Bundle bundle11 = new Bundle();
                bundle11.putBoolean("value", z);
                return bundle11;
            case "close_db":
                this.mOpenHelper.close();
                return null;
            case "start_restore_db":
                this.mIsRestoreDB = true;
                return null;
            case "get_db_name":
                Bundle bundle12 = new Bundle();
                DatabaseHelper databaseHelper2 = this.mOpenHelper;
                bundle12.putString("value", databaseHelper2 != null ? databaseHelper2.getDatabaseName() : LauncherFiles.LAUNCHER_UNKNOWN_DB);
                return bundle12;
            case "reset_database_helper":
                resetDatabaseHelper();
                return null;
            case "get_grid_info":
                Context context = getContext();
                int[] iArr = {LGInvariantDeviceProfile.getSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, context.getResources().getInteger(R.integer.device_profile_default_numColumns)), LGInvariantDeviceProfile.getSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, context.getResources().getInteger(R.integer.device_profile_default_numRows))};
                Bundle bundle13 = new Bundle();
                bundle13.putIntArray(LauncherSettings.Settings.GRID_COMLUMN_ROW, iArr);
                return bundle13;
            case "create_empty_db":
                this.mOpenHelper.mRequestClearDB = true;
                DatabaseHelper databaseHelper3 = this.mOpenHelper;
                databaseHelper3.createEmptyDB(databaseHelper3.getWritableDatabase());
                return null;
            default:
                return null;
        }
    }

    private ArrayList<Long> deleteEmptyFolders() {
        ArrayList<Long> arrayList = new ArrayList<>();
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            try {
                Cursor cursorQuery = writableDatabase.query("favorites", new String[]{"_id"}, "itemType = 2 AND _id NOT IN (SELECT container FROM favorites)", null, null, null, null);
                while (cursorQuery.moveToNext()) {
                    arrayList.add(Long.valueOf(cursorQuery.getLong(0)));
                }
                cursorQuery.close();
                if (!arrayList.isEmpty()) {
                    writableDatabase.delete("favorites", Utilities.createDbSelectionQuery("_id", arrayList), null);
                }
                writableDatabase.setTransactionSuccessful();
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
                arrayList.clear();
            }
            return arrayList;
        } finally {
            writableDatabase.endTransaction();
        }
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

    public synchronized void createEmptyDB() {
        DatabaseHelper databaseHelper = this.mOpenHelper;
        databaseHelper.createEmptyDB(databaseHelper.getWritableDatabase());
    }

    public void clearFlagEmptyDbCreated() {
        String sharedPreferencesKey = LauncherAppState.getSharedPreferencesKey();
        if (!sIsDisabledEasyHome) {
            getContext().getSharedPreferences(sharedPreferencesKey, 0).edit().remove(EMPTY_DATABASE_CREATED_EASYHOME).commit();
        } else {
            getContext().getSharedPreferences(sharedPreferencesKey, 0).edit().remove(sIsDisabledAllApps ? EMPTY_DATABASE_CREATED : EMPTY_DATABASE_CREATED_ALLAPPS).commit();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x00a2 A[Catch: all -> 0x018c, TryCatch #0 {, blocks: (B:3:0x0001, B:10:0x001e, B:12:0x0024, B:14:0x0031, B:16:0x0037, B:17:0x003f, B:19:0x004f, B:45:0x00e8, B:46:0x00ec, B:49:0x00fd, B:50:0x010d, B:52:0x0112, B:54:0x011a, B:55:0x013e, B:57:0x0146, B:59:0x014e, B:23:0x005b, B:25:0x0069, B:27:0x006f, B:28:0x0077, B:30:0x007f, B:33:0x00a2, B:35:0x00a8, B:37:0x00ba, B:39:0x00c0, B:41:0x00d3, B:60:0x0156, B:62:0x015e, B:64:0x0168, B:66:0x0174, B:67:0x0183, B:6:0x0015), top: B:73:0x0001 }] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00b8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized void loadDefaultFavoritesIfNecessary() {
        /*
            r11 = this;
            monitor-enter(r11)
            java.lang.String r0 = com.android.launcher3.LauncherAppState.getSharedPreferencesKey()     // Catch: java.lang.Throwable -> L18c
            android.content.Context r1 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            r2 = 0
            android.content.SharedPreferences r0 = r1.getSharedPreferences(r0, r2)     // Catch: java.lang.Throwable -> L18c
            boolean r1 = com.android.launcher3.LauncherProvider.sIsDisabledEasyHome     // Catch: java.lang.Throwable -> L18c
            if (r1 != 0) goto L15
            java.lang.String r1 = "EMPTY_DATABASE_CREATED_EASYHOME"
            goto L1e
        L15:
            boolean r1 = com.android.launcher3.LauncherProvider.sIsDisabledAllApps     // Catch: java.lang.Throwable -> L18c
            if (r1 == 0) goto L1c
            java.lang.String r1 = "EMPTY_DATABASE_CREATED"
            goto L1e
        L1c:
            java.lang.String r1 = "EMPTY_DATABASE_CREATED_ALLAPPS"
        L1e:
            boolean r1 = r0.getBoolean(r1, r2)     // Catch: java.lang.Throwable -> L18c
            if (r1 == 0) goto L156
            java.lang.String r1 = "LauncherProvider"
            java.lang.String r3 = "loading default workspace"
            android.util.Log.d(r1, r3)     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.AutoInstallsLayout r1 = r11.createWorkspaceLoaderFromAppRestriction()     // Catch: java.lang.Throwable -> L18c
            if (r1 != 0) goto L4d
            boolean r3 = com.lge.launcher3.util.LGHomeFeature.isDisableEasyHome()     // Catch: java.lang.Throwable -> L18c
            if (r3 != 0) goto L3f
            java.lang.String r3 = "LauncherProvider"
            java.lang.String r4 = "skip AutoInstall for EasyHome"
            com.lge.launcher3.util.LGLog.i(r3, r4)     // Catch: java.lang.Throwable -> L18c
            goto L4d
        L3f:
            android.content.Context r1 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r11.mOpenHelper     // Catch: java.lang.Throwable -> L18c
            android.appwidget.AppWidgetHost r3 = r3.mAppWidgetHost     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.LauncherProvider$DatabaseHelper r4 = r11.mOpenHelper     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.AutoInstallsLayout r1 = com.android.launcher3.AutoInstallsLayout.get(r1, r3, r4)     // Catch: java.lang.Throwable -> L18c
        L4d:
            if (r1 == 0) goto L5b
            com.lge.launcher3.util.LGHomeFeature$Config r3 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_GDEC_CHANGE_DEFAULT_PAGE     // Catch: java.lang.Throwable -> L18c
            boolean r3 = r3.getValue()     // Catch: java.lang.Throwable -> L18c
            if (r3 == 0) goto L58
            goto L5b
        L58:
            r0 = r2
            goto Le3
        L5b:
            android.content.Context r3 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            android.content.pm.PackageManager r3 = r3.getPackageManager()     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.Partner r3 = com.android.launcher3.Partner.get(r3)     // Catch: java.lang.Throwable -> L18c
            if (r3 != 0) goto L9f
            boolean r4 = com.lge.launcher3.util.LGHomeFeature.isDisableEasyHome()     // Catch: java.lang.Throwable -> L18c
            if (r4 != 0) goto L77
            java.lang.String r0 = "LauncherProvider"
            java.lang.String r4 = "skip CotaReloadHandler for EasyHome"
            com.lge.launcher3.util.LGLog.i(r0, r4)     // Catch: java.lang.Throwable -> L18c
            goto L9f
        L77:
            java.lang.String r4 = "launcher.cota.package"
            boolean r4 = r0.contains(r4)     // Catch: java.lang.Throwable -> L18c
            if (r4 == 0) goto L9f
            java.lang.String r3 = "launcher.cota.package"
            r4 = 0
            java.lang.String r3 = r0.getString(r3, r4)     // Catch: java.lang.Throwable -> L18c
            java.lang.String r5 = "launcher.cota.filepath"
            java.lang.String r4 = r0.getString(r5, r4)     // Catch: java.lang.Throwable -> L18c
            java.lang.String r5 = "defaultpage"
            int r0 = r0.getInt(r5, r2)     // Catch: java.lang.Throwable -> L18c
            android.content.Context r5 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            android.content.pm.PackageManager r5 = r5.getPackageManager()     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.Partner r3 = com.lge.launcher3.util.LGPartner.getforPackageName(r5, r3, r4)     // Catch: java.lang.Throwable -> L18c
            goto La0
        L9f:
            r0 = r2
        La0:
            if (r3 == 0) goto Lb8
            java.lang.String r4 = r3.getFilePath()     // Catch: java.lang.Throwable -> L18c
            if (r4 == 0) goto Lb8
            com.lge.launcher3.PartnerFileLayoutParser r1 = new com.lge.launcher3.PartnerFileLayoutParser     // Catch: java.lang.Throwable -> L18c
            android.content.Context r4 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.LauncherProvider$DatabaseHelper r5 = r11.mOpenHelper     // Catch: java.lang.Throwable -> L18c
            android.appwidget.AppWidgetHost r5 = r5.mAppWidgetHost     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.LauncherProvider$DatabaseHelper r6 = r11.mOpenHelper     // Catch: java.lang.Throwable -> L18c
            r1.<init>(r4, r5, r6, r3)     // Catch: java.lang.Throwable -> L18c
            goto Le3
        Lb8:
            if (r3 == 0) goto Le3
            boolean r4 = r3.hasDefaultLayout()     // Catch: java.lang.Throwable -> L18c
            if (r4 == 0) goto Le3
            android.content.res.Resources r9 = r3.getResources()     // Catch: java.lang.Throwable -> L18c
            java.lang.String r4 = "partner_default_layout"
            java.lang.String r5 = "xml"
            java.lang.String r3 = r3.getPackageName()     // Catch: java.lang.Throwable -> L18c
            int r10 = r9.getIdentifier(r4, r5, r3)     // Catch: java.lang.Throwable -> L18c
            if (r10 == 0) goto Le3
            com.android.launcher3.DefaultLayoutParser r1 = new com.android.launcher3.DefaultLayoutParser     // Catch: java.lang.Throwable -> L18c
            android.content.Context r6 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r11.mOpenHelper     // Catch: java.lang.Throwable -> L18c
            android.appwidget.AppWidgetHost r7 = r3.mAppWidgetHost     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.LauncherProvider$DatabaseHelper r8 = r11.mOpenHelper     // Catch: java.lang.Throwable -> L18c
            r5 = r1
            r5.<init>(r6, r7, r8, r9, r10)     // Catch: java.lang.Throwable -> L18c
        Le3:
            if (r1 == 0) goto Le6
            r2 = 1
        Le6:
            if (r1 != 0) goto Lec
            com.android.launcher3.DefaultLayoutParser r1 = r11.getDefaultLayoutParser()     // Catch: java.lang.Throwable -> L18c
        Lec:
            r11.createEmptyDB()     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r11.mOpenHelper     // Catch: java.lang.Throwable -> L18c
            android.database.sqlite.SQLiteDatabase r4 = r3.getWritableDatabase()     // Catch: java.lang.Throwable -> L18c
            int r1 = r3.loadFavorites(r4, r1)     // Catch: java.lang.Throwable -> L18c
            if (r1 > 0) goto L10d
            if (r2 == 0) goto L10d
            r11.createEmptyDB()     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.LauncherProvider$DatabaseHelper r1 = r11.mOpenHelper     // Catch: java.lang.Throwable -> L18c
            android.database.sqlite.SQLiteDatabase r2 = r1.getWritableDatabase()     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.DefaultLayoutParser r3 = r11.getDefaultLayoutParser()     // Catch: java.lang.Throwable -> L18c
            r1.loadFavorites(r2, r3)     // Catch: java.lang.Throwable -> L18c
        L10d:
            r11.clearFlagEmptyDbCreated()     // Catch: java.lang.Throwable -> L18c
            if (r0 == 0) goto L13e
            com.lge.launcher3.util.LGHomeFeature$Config r1 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_GDEC_CHANGE_DEFAULT_PAGE     // Catch: java.lang.Throwable -> L18c
            boolean r1 = r1.getValue()     // Catch: java.lang.Throwable -> L18c
            if (r1 == 0) goto L13e
            java.lang.String r1 = "LauncherProvider"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L18c
            r2.<init>()     // Catch: java.lang.Throwable -> L18c
            java.lang.String r3 = "FEATURE_GDEC_CHANGE_DEFAULT_PAGE, defaultPage : "
            r2.append(r3)     // Catch: java.lang.Throwable -> L18c
            r2.append(r0)     // Catch: java.lang.Throwable -> L18c
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L18c
            android.util.Log.i(r1, r2)     // Catch: java.lang.Throwable -> L18c
            android.content.Context r1 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.LauncherModel.updateDefaultScreen(r1, r0)     // Catch: java.lang.Throwable -> L18c
            android.content.Context r0 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            com.android.launcher3.LauncherModel.validateDefaultScreen(r0)     // Catch: java.lang.Throwable -> L18c
        L13e:
            com.lge.launcher3.util.LGHomeFeature$Config r0 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN     // Catch: java.lang.Throwable -> L18c
            boolean r0 = r0.getValue()     // Catch: java.lang.Throwable -> L18c
            if (r0 == 0) goto L18a
            com.lge.launcher3.util.LGHomeFeature$Config r0 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_SMARTBULLETIN_SET_DEFAULT_LAYOUT     // Catch: java.lang.Throwable -> L18c
            boolean r0 = r0.getValue()     // Catch: java.lang.Throwable -> L18c
            if (r0 == 0) goto L18a
            android.content.Context r0 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            com.lge.launcher3.homesettings.SBHomeDataBaseUtil.turnOnSmartBulletin(r0)     // Catch: java.lang.Throwable -> L18c
            goto L18a
        L156:
            com.lge.launcher3.util.LGHomeFeature$Config r0 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME     // Catch: java.lang.Throwable -> L18c
            boolean r0 = r0.getValue()     // Catch: java.lang.Throwable -> L18c
            if (r0 != 0) goto L168
            java.lang.String r0 = "KR"
            java.lang.String r1 = com.lge.os.Build.CA_TARGET.COUNTRY     // Catch: java.lang.Throwable -> L18c
            boolean r0 = r0.equals(r1)     // Catch: java.lang.Throwable -> L18c
            if (r0 == 0) goto L18a
        L168:
            android.content.Context r0 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            java.lang.String r1 = "com.lge.appbox.client"
            boolean r0 = com.lge.launcher3.util.PackageUtils.isPackageInstalled(r0, r1)     // Catch: java.lang.Throwable -> L18c
            if (r0 == 0) goto L183
            java.lang.String r0 = "LauncherProvider"
            java.lang.String r1 = "start to make promising app list again"
            com.lge.launcher3.util.LGLog.i(r0, r1)     // Catch: java.lang.Throwable -> L18c
            android.content.Context r0 = r11.getContext()     // Catch: java.lang.Throwable -> L18c
            com.lge.launcher3.silentota.SilentOTA_Extension.makeAddedPromisingPackage(r0)     // Catch: java.lang.Throwable -> L18c
            goto L18a
        L183:
            java.lang.String r0 = "LauncherProvider"
            java.lang.String r1 = "skip to create promising app list again"
            com.lge.launcher3.util.LGLog.i(r0, r1)     // Catch: java.lang.Throwable -> L18c
        L18a:
            monitor-exit(r11)
            return
        L18c:
            r0 = move-exception
            monitor-exit(r11)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.loadDefaultFavoritesIfNecessary():void");
    }

    protected AutoInstallsLayout createWorkspaceLoaderFromAppRestriction() {
        String string;
        if (Build.VERSION.SDK_INT < 18) {
            return null;
        }
        Context context = getContext();
        Bundle applicationRestrictions = ((UserManager) context.getSystemService("user")).getApplicationRestrictions(context.getPackageName());
        if (applicationRestrictions != null && (string = applicationRestrictions.getString(RESTRICTION_PACKAGE_NAME)) != null) {
            try {
                return AutoInstallsLayout.get(context, string, context.getPackageManager().getResourcesForApplication(string), this.mOpenHelper.mAppWidgetHost, this.mOpenHelper);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Target package for restricted profile not found", e);
            }
        }
        return null;
    }

    private DefaultLayoutParser getDefaultLayoutParser() {
        int iDFromCAList = DefaultWorkspaceLoader.getIDFromCAList(getContext());
        if (iDFromCAList == 0) {
            iDFromCAList = LauncherAppState.getInstance(getContext()).getInvariantDeviceProfile().defaultLayoutId;
        }
        return new DefaultLayoutParser(getContext(), this.mOpenHelper.mAppWidgetHost, this.mOpenHelper, getContext().getResources(), iDFromCAList);
    }

    public void migrateLauncher2Shortcuts() throws Throwable {
        DatabaseHelper databaseHelper = this.mOpenHelper;
        databaseHelper.migrateLauncher2Shortcuts(databaseHelper.getWritableDatabase(), Uri.parse(getContext().getString(R.string.old_launcher_provider_uri)));
    }

    public void updateFolderItemsRank() {
        DatabaseHelper databaseHelper = this.mOpenHelper;
        databaseHelper.updateFolderItemsRank(databaseHelper.getWritableDatabase(), false);
    }

    public void convertShortcutsToLauncherActivities() throws Throwable {
        DatabaseHelper databaseHelper = this.mOpenHelper;
        databaseHelper.convertShortcutsToLauncherActivities(databaseHelper.getWritableDatabase());
    }

    public synchronized void deleteDatabase() {
        File file = new File(this.mOpenHelper.getWritableDatabase().getPath());
        this.mOpenHelper.close();
        if (file.exists()) {
            SQLiteDatabase.deleteDatabase(file);
        }
        String launcherDBFileName = getLauncherDBFileName(getContext());
        LGLog.i(TAG, "Delete Database and then set with [" + launcherDBFileName + "]");
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), launcherDBFileName);
        this.mOpenHelper = databaseHelper;
        databaseHelper.mListener = this.mListener;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper implements AutoInstallsLayout.LayoutParserCallback {
        private static final String TABLE_SHARING_CONTENTS = "sharingContents";
        final AppWidgetHost mAppWidgetHost;
        private final Context mContext;
        LauncherProviderChangeListener mListener;
        private long mMaxItemId;
        private long mMaxScreenId;
        private boolean mNewDbCreated;
        public boolean mRequestClearDB;

        public DatabaseHelper(Context context, String launcherDBFileName) {
            super(context, launcherDBFileName, (SQLiteDatabase.CursorFactory) null, 105);
            this.mMaxItemId = -1L;
            this.mMaxScreenId = -1L;
            this.mNewDbCreated = false;
            this.mRequestClearDB = false;
            this.mContext = context;
            this.mAppWidgetHost = new AppWidgetHost(context, 1024);
            if (this.mMaxItemId == -1) {
                this.mMaxItemId = initializeMaxItemId(getWritableDatabase());
            }
            if (this.mMaxScreenId == -1) {
                this.mMaxScreenId = initializeMaxScreenId(getWritableDatabase());
            }
        }

        public boolean wasNewDbCreated() {
            return this.mNewDbCreated;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            boolean z;
            this.mMaxItemId = 1L;
            this.mMaxScreenId = 0L;
            this.mNewDbCreated = true;
            addFavoritesTable(db, false);
            addWorkspacesTable(db, false);
            boolean z2 = Utilities.getPrefs(this.mContext).getBoolean(LauncherProvider.EXIST_DATABASE, false);
            AppWidgetHost appWidgetHost = this.mAppWidgetHost;
            if (appWidgetHost != null && ((z = this.mRequestClearDB) || !z2)) {
                LGLog.i(LauncherProvider.TAG, "deleteHost. request clear = " + z + ", exist DB = " + z2);
                this.mAppWidgetHost.deleteHost();
                new MainThreadExecutor().execute(new Runnable() { // from class: com.android.launcher3.LauncherProvider.DatabaseHelper.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (DatabaseHelper.this.mListener != null) {
                            DatabaseHelper.this.mListener.onAppWidgetHostReset();
                        }
                    }
                });
            } else {
                LGLog.i(LauncherProvider.TAG, "don't call deleteHost(). request clear = " + this.mRequestClearDB + ", exist DB = " + z2 + ", mAppWidgetHost = " + appWidgetHost);
            }
            this.mMaxItemId = initializeMaxItemId(db);
            setFlagEmptyDbCreated();
            ManagedProfileHeuristic.processAllUsers(Collections.emptyList(), this.mContext);
            addSharingContentsTable(db);
            addHideAppsTable(db);
            addWorkspaceDetailTable(db);
            addHomePreferencesTable(db);
            this.mRequestClearDB = false;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public SQLiteDatabase getWritableDatabase() {
            try {
                SQLiteDatabase writableDatabase = super.getWritableDatabase();
                checkHasWorkspaceDetailTable(writableDatabase);
                checkHasHomePreferencesTable(writableDatabase);
                return writableDatabase;
            } catch (SQLiteFullException e) {
                Log.e(LauncherProvider.TAG, "Disk full, all write operations will be ignored", e);
                return null;
            } catch (SQLiteException e2) {
                Log.d(LauncherProvider.TAG, "Ignoring sqlite exception", e2);
                return null;
            }
        }

        public long getDefaultUserSerial() {
            return UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(Process.myUserHandle());
        }

        private void addFavoritesTable(SQLiteDatabase db, boolean optional) {
            LauncherSettings.Favorites.addTableToDb(db, getDefaultUserSerial(), optional);
        }

        private void addWorkspacesTable(SQLiteDatabase db, boolean optional) {
            db.execSQL("CREATE TABLE " + (optional ? " IF NOT EXISTS " : "") + "workspaceScreens (_id INTEGER PRIMARY KEY,screenRank INTEGER,modified INTEGER NOT NULL DEFAULT 0);");
        }

        private void removeOrphanedItems(SQLiteDatabase db) {
            db.execSQL("DELETE FROM favorites WHERE screen NOT IN (SELECT _id FROM workspaceScreens) AND container = -100");
            db.execSQL("DELETE FROM favorites WHERE container <> -100 AND container <> -101 AND container NOT IN (SELECT _id FROM favorites WHERE itemType = 2)");
        }

        private void setFlagJustLoadedOldDb() {
            SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0);
            if (!LauncherProvider.sIsDisabledEasyHome) {
                sharedPreferences.edit().putBoolean(LauncherProvider.EMPTY_DATABASE_CREATED_EASYHOME, false).commit();
            } else {
                sharedPreferences.edit().putBoolean(LauncherProvider.sIsDisabledAllApps ? LauncherProvider.EMPTY_DATABASE_CREATED : LauncherProvider.EMPTY_DATABASE_CREATED_ALLAPPS, false).commit();
            }
            sharedPreferences.edit().putBoolean(LauncherProvider.EXIST_DATABASE, true).commit();
        }

        private void setFlagEmptyDbCreated() {
            SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0);
            if (!LauncherProvider.sIsDisabledEasyHome) {
                sharedPreferences.edit().putBoolean(LauncherProvider.EMPTY_DATABASE_CREATED_EASYHOME, true).commit();
            } else {
                sharedPreferences.edit().putBoolean(LauncherProvider.sIsDisabledAllApps ? LauncherProvider.EMPTY_DATABASE_CREATED : LauncherProvider.EMPTY_DATABASE_CREATED_ALLAPPS, true).commit();
            }
            sharedPreferences.edit().putBoolean(LauncherProvider.EXIST_DATABASE, true).commit();
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            LGLog.i(LauncherProvider.TAG, "Upgrade database " + oldVersion + " -> " + newVersion);
            checkUpgradeDB(this.mContext);
            switch (oldVersion) {
                case 100:
                    addSharingContentsTable(db);
                    addIconChangeColumn(db);
                case 101:
                    addHideAppsTable(db);
                case 102:
                    addWorkspaceDetailTable(db);
                case 103:
                    addHomePreferencesTable(db);
                    migrateGridInfoFromSharedPreferences(db);
                    RestoreHome4.requestBackupAndRestoreHome4(this.mContext);
                case 104:
                    LauncherProvider.updateHomeInfoFromOOS(this.mContext);
                    break;
                default:
                    createEmptyDB(db);
                    break;
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(LauncherProvider.TAG, "Database version downgrade from: " + oldVersion + " to " + newVersion + ". Wiping databse.");
            createEmptyDB(db);
        }

        public void createEmptyDB(SQLiteDatabase db) {
            LGLog.i(LauncherProvider.TAG, "Create empty database");
            db.beginTransaction();
            try {
                db.execSQL("DROP TABLE IF EXISTS favorites");
                db.execSQL("DROP TABLE IF EXISTS workspaceScreens");
                db.execSQL("DROP TABLE IF EXISTS workspaceDetail");
                db.execSQL("DROP TABLE IF EXISTS homePreferences");
                onCreate(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        public void removeGhostWidgets(SQLiteDatabase db) {
            AppWidgetHost appWidgetHostNewLauncherWidgetHost = newLauncherWidgetHost();
            try {
                int[] appWidgetIds = appWidgetHostNewLauncherWidgetHost.getAppWidgetIds();
                HashSet hashSet = new HashSet();
                try {
                    Cursor cursorQuery = db.query("favorites", new String[]{"appWidgetId"}, "itemType=4", null, null, null, null);
                    while (cursorQuery.moveToNext()) {
                        try {
                            hashSet.add(Integer.valueOf(cursorQuery.getInt(0)));
                        } finally {
                        }
                    }
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    for (int i : appWidgetIds) {
                        if (!hashSet.contains(Integer.valueOf(i))) {
                            try {
                                LGLog.i(LauncherProvider.TAG, "Deleting invalid widget " + i);
                                appWidgetHostNewLauncherWidgetHost.deleteAppWidgetId(i);
                            } catch (RuntimeException unused) {
                            }
                        }
                    }
                } catch (SQLException e) {
                    LGLog.w(LauncherProvider.TAG, "Error getting widgets list", e, new int[0]);
                }
            } catch (IncompatibleClassChangeError e2) {
                LGLog.e(LauncherProvider.TAG, "getAppWidgetIds not supported " + e2);
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:40:0x009c  */
        /* JADX WARN: Removed duplicated region for block: B:42:0x00a1  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void convertShortcutsToLauncherActivities(android.database.sqlite.SQLiteDatabase r15) throws java.lang.Throwable {
            /*
                r14 = this;
                java.lang.String r0 = "intent"
                java.lang.String r1 = "_id"
                java.lang.String r2 = "LauncherProvider"
                r15.beginTransaction()
                r3 = 0
                long r4 = r14.getDefaultUserSerial()     // Catch: java.lang.Throwable -> L7e android.database.SQLException -> L81
                java.lang.String r7 = "favorites"
                java.lang.String[] r8 = new java.lang.String[]{r1, r0}     // Catch: java.lang.Throwable -> L7e android.database.SQLException -> L81
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L7e android.database.SQLException -> L81
                r6.<init>()     // Catch: java.lang.Throwable -> L7e android.database.SQLException -> L81
                java.lang.String r9 = "itemType=1 AND profileId="
                r6.append(r9)     // Catch: java.lang.Throwable -> L7e android.database.SQLException -> L81
                r6.append(r4)     // Catch: java.lang.Throwable -> L7e android.database.SQLException -> L81
                java.lang.String r9 = r6.toString()     // Catch: java.lang.Throwable -> L7e android.database.SQLException -> L81
                r10 = 0
                r11 = 0
                r12 = 0
                r13 = 0
                r6 = r15
                android.database.Cursor r4 = r6.query(r7, r8, r9, r10, r11, r12, r13)     // Catch: java.lang.Throwable -> L7e android.database.SQLException -> L81
                java.lang.String r5 = "UPDATE favorites SET itemType=0 WHERE _id=?"
                android.database.sqlite.SQLiteStatement r3 = r15.compileStatement(r5)     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
                int r1 = r4.getColumnIndexOrThrow(r1)     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
                int r0 = r4.getColumnIndexOrThrow(r0)     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
            L3c:
                boolean r5 = r4.moveToNext()     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
                if (r5 == 0) goto L65
                java.lang.String r5 = r4.getString(r0)     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
                r6 = 0
                android.content.Intent r5 = android.content.Intent.parseUri(r5, r6)     // Catch: java.net.URISyntaxException -> L5e java.lang.Throwable -> L76 android.database.SQLException -> L7a
                boolean r5 = com.android.launcher3.Utilities.isLauncherAppTarget(r5)     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
                if (r5 != 0) goto L52
                goto L3c
            L52:
                long r5 = r4.getLong(r1)     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
                r7 = 1
                r3.bindLong(r7, r5)     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
                r3.executeUpdateDelete()     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
                goto L3c
            L5e:
                r5 = move-exception
                java.lang.String r6 = "Unable to parse intent"
                android.util.Log.e(r2, r6, r5)     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
                goto L3c
            L65:
                r15.setTransactionSuccessful()     // Catch: java.lang.Throwable -> L76 android.database.SQLException -> L7a
                r15.endTransaction()
                if (r4 == 0) goto L70
                r4.close()
            L70:
                if (r3 == 0) goto L95
                r3.close()
                goto L95
            L76:
                r0 = move-exception
                r1 = r3
                r3 = r4
                goto L97
            L7a:
                r0 = move-exception
                r1 = r3
                r3 = r4
                goto L83
            L7e:
                r0 = move-exception
                r1 = r3
                goto L97
            L81:
                r0 = move-exception
                r1 = r3
            L83:
                java.lang.String r4 = "Error deduping shortcuts"
                android.util.Log.w(r2, r4, r0)     // Catch: java.lang.Throwable -> L96
                r15.endTransaction()
                if (r3 == 0) goto L90
                r3.close()
            L90:
                if (r1 == 0) goto L95
                r1.close()
            L95:
                return
            L96:
                r0 = move-exception
            L97:
                r15.endTransaction()
                if (r3 == 0) goto L9f
                r3.close()
            L9f:
                if (r1 == 0) goto La4
                r1.close()
            La4:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.DatabaseHelper.convertShortcutsToLauncherActivities(android.database.sqlite.SQLiteDatabase):void");
        }

        public boolean recreateWorkspaceTable(SQLiteDatabase db) {
            db.beginTransaction();
            try {
                try {
                    Cursor cursorQuery = db.query("workspaceScreens", new String[]{"_id"}, null, null, null, null, LauncherSettings.WorkspaceScreens.SCREEN_RANK);
                    ArrayList arrayList = new ArrayList();
                    long jMax = 0;
                    while (cursorQuery.moveToNext()) {
                        try {
                            Long lValueOf = Long.valueOf(cursorQuery.getLong(0));
                            if (!arrayList.contains(lValueOf)) {
                                arrayList.add(lValueOf);
                                jMax = Math.max(jMax, lValueOf.longValue());
                            }
                        } catch (Throwable th) {
                            cursorQuery.close();
                            throw th;
                        }
                    }
                    cursorQuery.close();
                    db.execSQL("DROP TABLE IF EXISTS workspaceScreens");
                    addWorkspacesTable(db, false);
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("_id", (Long) arrayList.get(i));
                        contentValues.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
                        LauncherProvider.addModifiedTime(contentValues);
                        db.insertOrThrow("workspaceScreens", null, contentValues);
                    }
                    db.setTransactionSuccessful();
                    this.mMaxScreenId = jMax;
                    db.endTransaction();
                    return true;
                } catch (SQLException e) {
                    Log.e(LauncherProvider.TAG, e.getMessage(), e);
                    db.endTransaction();
                    return false;
                }
            } catch (Throwable th2) {
                db.endTransaction();
                throw th2;
            }
        }

        boolean updateFolderItemsRank(SQLiteDatabase db, boolean addRankColumn) {
            db.beginTransaction();
            if (addRankColumn) {
                try {
                    db.execSQL("ALTER TABLE favorites ADD COLUMN rank INTEGER NOT NULL DEFAULT 0;");
                } catch (SQLException e) {
                    Log.e(LauncherProvider.TAG, e.getMessage(), e);
                    return false;
                } finally {
                    db.endTransaction();
                }
            }
            Cursor cursorRawQuery = db.rawQuery("SELECT container, MAX(cellX) FROM favorites WHERE container IN (SELECT _id FROM favorites WHERE itemType = ?) GROUP BY container;", new String[]{Integer.toString(2)});
            while (cursorRawQuery.moveToNext()) {
                db.execSQL("UPDATE favorites SET rank=cellX+(cellY*?) WHERE container=? AND cellX IS NOT NULL AND cellY IS NOT NULL;", new Object[]{Long.valueOf(cursorRawQuery.getLong(1) + 1), Long.valueOf(cursorRawQuery.getLong(0))});
            }
            cursorRawQuery.close();
            db.setTransactionSuccessful();
            return true;
        }

        private boolean addProfileColumn(SQLiteDatabase db) {
            return addIntegerColumn(db, "profileId", getDefaultUserSerial());
        }

        private boolean addIntegerColumn(SQLiteDatabase db, String columnName, long defaultValue) {
            db.beginTransaction();
            try {
                try {
                    db.execSQL("ALTER TABLE favorites ADD COLUMN " + columnName + " INTEGER NOT NULL DEFAULT " + defaultValue + ";");
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    return true;
                } catch (SQLException e) {
                    Log.e(LauncherProvider.TAG, e.getMessage(), e);
                    db.endTransaction();
                    return false;
                }
            } catch (Throwable th) {
                db.endTransaction();
                throw th;
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

        public AppWidgetHost newLauncherWidgetHost() {
            return new AppWidgetHost(this.mContext, 1024);
        }

        @Override // com.android.launcher3.AutoInstallsLayout.LayoutParserCallback
        public long insertAndCheck(SQLiteDatabase db, ContentValues values) {
            return LauncherProvider.dbInsertAndCheck(this, db, "favorites", null, values);
        }

        public void updateMaxItemId(long id) {
            this.mMaxItemId = id + 1;
        }

        public void checkId(String table, ContentValues values) {
            long jLongValue = values.getAsLong("_id").longValue();
            if ("workspaceScreens".equals(table)) {
                this.mMaxScreenId = Math.max(jLongValue, this.mMaxScreenId);
            } else if ("favorites".equals(table)) {
                this.mMaxItemId = Math.max(jLongValue, this.mMaxItemId);
            }
        }

        private long initializeMaxItemId(SQLiteDatabase db) {
            return LauncherProvider.getMaxId(db, "favorites");
        }

        public long generateNewScreenId() {
            long j = this.mMaxScreenId;
            if (j < 0) {
                throw new RuntimeException("Error: max screen id was not initialized");
            }
            long j2 = j + 1;
            this.mMaxScreenId = j2;
            return j2;
        }

        private long initializeMaxScreenId(SQLiteDatabase db) {
            return LauncherProvider.getMaxId(db, "workspaceScreens");
        }

        boolean initializeExternalAdd(ContentValues values) {
            values.put("_id", Long.valueOf(generateNewItemId()));
            Integer asInteger = values.getAsInteger(LauncherSettings.BaseLauncherColumns.ITEM_TYPE);
            if (asInteger != null && asInteger.intValue() == 4 && !values.containsKey("appWidgetId")) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.mContext);
                ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(values.getAsString(LauncherSettings.Favorites.APPWIDGET_PROVIDER));
                if (componentNameUnflattenFromString != null) {
                    try {
                        int iAllocateAppWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                        values.put("appWidgetId", Integer.valueOf(iAllocateAppWidgetId));
                        if (!appWidgetManager.bindAppWidgetIdIfAllowed(iAllocateAppWidgetId, componentNameUnflattenFromString)) {
                            return false;
                        }
                    } catch (RuntimeException e) {
                        Log.e(LauncherProvider.TAG, "Failed to initialize external widget", e);
                    }
                }
                return false;
            }
            return addScreenIdIfNecessary(values.getAsLong("screen").longValue());
        }

        private boolean addScreenIdIfNecessary(long screenId) {
            if (screenId >= 0 && !hasScreenId(screenId)) {
                int maxScreenRank = getMaxScreenRank() + 1;
                ContentValues contentValues = new ContentValues();
                contentValues.put("_id", Long.valueOf(screenId));
                contentValues.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(maxScreenRank));
                if (LauncherProvider.dbInsertAndCheck(this, getWritableDatabase(), "workspaceScreens", null, contentValues) < 0) {
                    return false;
                }
            }
            return true;
        }

        private boolean hasScreenId(long screenId) {
            Cursor cursorQuery = getWritableDatabase().query("workspaceScreens", null, "_id = ?", new String[]{Long.toString(screenId)}, null, null, null);
            if (cursorQuery == null) {
                return false;
            }
            int count = cursorQuery.getCount();
            cursorQuery.close();
            return count > 0;
        }

        private int getMaxScreenRank() {
            Cursor cursorRawQuery = getWritableDatabase().rawQuery("SELECT MAX(screenRank) FROM workspaceScreens", null);
            int i = (cursorRawQuery == null || !cursorRawQuery.moveToNext()) ? -1 : cursorRawQuery.getInt(0);
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
            return i;
        }

        int loadFavorites(SQLiteDatabase db, AutoInstallsLayout loader) {
            ArrayList<Long> arrayList = new ArrayList<>();
            int iLoadLayout = loader.loadLayout(db, arrayList);
            Collections.sort(arrayList);
            ContentValues contentValues = new ContentValues();
            int i = 0;
            for (Long l : arrayList) {
                contentValues.clear();
                contentValues.put("_id", l);
                contentValues.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
                if (LauncherProvider.dbInsertAndCheck(this, db, "workspaceScreens", null, contentValues) < 0) {
                    throw new RuntimeException("Failed initialize screen tablefrom default layout");
                }
                i++;
            }
            this.mMaxItemId = initializeMaxItemId(db);
            this.mMaxScreenId = initializeMaxScreenId(db);
            if (this.mContext == null) {
                return 0;
            }
            if (LGHomeFeature.Config.FEATURE_USE_SORT_APPS_BY_NAME_IN_MULTIUSER.getValue()) {
                boolean zIsAdminUser = UserUtils.isAdminUser(this.mContext);
                boolean zHasDeviceOwner = ManagedProfileUtils.hasDeviceOwner(this.mContext);
                boolean zHasProfileOwner = ManagedProfileUtils.hasProfileOwner(this.mContext);
                if (!zIsAdminUser && !zHasDeviceOwner && !zHasProfileOwner) {
                    SortAppsByManager.rearrange(this.mContext, SortAppsByConst.SortType.NAME, false, true);
                    LGLog.i(LauncherProvider.TAG, String.format("loadFavorites() : SortAppsByName, UserInfo(%s), isAdminUser(%s), hasDeviceOwner(%s), hasProfileOwner(%s)", UserUtils.getCurrentUserInfo(this.mContext), Boolean.valueOf(zIsAdminUser), Boolean.valueOf(zHasDeviceOwner), Boolean.valueOf(zHasProfileOwner)));
                }
            }
            return iLoadLayout;
        }

        void migrateLauncher2Shortcuts(SQLiteDatabase db, Uri uri) throws Throwable {
            Cursor cursorQuery;
            Cursor cursor;
            String str;
            int i;
            DatabaseHelper databaseHelper;
            int iGenerateNewScreenId;
            int i2;
            int i3;
            int i4;
            int i5;
            HashSet hashSet;
            InvariantDeviceProfile invariantDeviceProfile;
            ArrayList arrayList;
            ArrayList arrayList2;
            SparseArray sparseArray;
            SparseArray sparseArray2;
            SQLiteDatabase sQLiteDatabase;
            int i6;
            SparseArray sparseArray3;
            int i7;
            int i8;
            int i9;
            UserHandle userForSerialNumber;
            long j;
            SparseArray sparseArray4;
            String str2;
            String str3;
            String str4;
            ArrayList arrayList3;
            ArrayList arrayList4;
            try {
                cursorQuery = this.mContext.getContentResolver().query(uri, null, null, null, "title ASC");
            } catch (Exception unused) {
                cursorQuery = null;
            }
            String str5 = LauncherProvider.TAG;
            if (cursorQuery != null) {
                try {
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                }
                if (cursorQuery.getCount() > 0) {
                    int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow("_id");
                    String str6 = "_id";
                    int columnIndexOrThrow2 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.INTENT);
                    String str7 = LauncherSettings.BaseLauncherColumns.INTENT;
                    int columnIndexOrThrow3 = cursorQuery.getColumnIndexOrThrow("title");
                    String str8 = "title";
                    int columnIndexOrThrow4 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_TYPE);
                    String str9 = LauncherSettings.BaseLauncherColumns.ICON_TYPE;
                    int columnIndexOrThrow5 = cursorQuery.getColumnIndexOrThrow("icon");
                    String str10 = "icon";
                    int columnIndexOrThrow6 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE);
                    String str11 = LauncherSettings.BaseLauncherColumns.ICON_PACKAGE;
                    int columnIndexOrThrow7 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE);
                    String str12 = LauncherSettings.BaseLauncherColumns.ICON_RESOURCE;
                    int columnIndexOrThrow8 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
                    String str13 = LauncherSettings.Favorites.CONTAINER;
                    int columnIndexOrThrow9 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ITEM_TYPE);
                    String str14 = LauncherSettings.BaseLauncherColumns.ITEM_TYPE;
                    int columnIndexOrThrow10 = cursorQuery.getColumnIndexOrThrow("screen");
                    String str15 = "screen";
                    int columnIndexOrThrow11 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
                    String str16 = LauncherSettings.Favorites.CELLX;
                    int columnIndexOrThrow12 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
                    String str17 = LauncherSettings.Favorites.CELLY;
                    int columnIndexOrThrow13 = cursorQuery.getColumnIndexOrThrow("uri");
                    String str18 = "uri";
                    String str19 = "displayMode";
                    int columnIndexOrThrow14 = cursorQuery.getColumnIndexOrThrow("displayMode");
                    int columnIndex = cursorQuery.getColumnIndex("profileId");
                    Cursor cursor2 = cursorQuery;
                    String str20 = "profileId";
                    databaseHelper = this;
                    try {
                        InvariantDeviceProfile invariantDeviceProfile2 = LauncherAppState.getInstance(databaseHelper.mContext).getInvariantDeviceProfile();
                        i2 = columnIndexOrThrow13;
                        i3 = invariantDeviceProfile2.numColumns;
                        i4 = invariantDeviceProfile2.numRows;
                        i5 = invariantDeviceProfile2.numHotseatIcons;
                        invariantDeviceProfile = invariantDeviceProfile2;
                        hashSet = new HashSet(cursor2.getCount());
                        arrayList = new ArrayList();
                        arrayList2 = new ArrayList();
                        sparseArray = new SparseArray();
                    } catch (Throwable th2) {
                        th = th2;
                        cursor = cursor2;
                    }
                    while (true) {
                        sparseArray2 = sparseArray;
                        if (!cursor2.moveToNext()) {
                            break;
                        }
                        cursor = cursor2;
                        int i10 = columnIndexOrThrow7;
                        try {
                            int i11 = cursor.getInt(columnIndexOrThrow9);
                            int i12 = columnIndexOrThrow9;
                            int i13 = columnIndexOrThrow6;
                            if (i11 == 0 || i11 == 1 || i11 == 2) {
                                int i14 = cursor.getInt(columnIndexOrThrow11);
                                int i15 = cursor.getInt(columnIndexOrThrow12);
                                int i16 = columnIndexOrThrow12;
                                int i17 = cursor.getInt(columnIndexOrThrow10);
                                int i18 = columnIndexOrThrow10;
                                int i19 = cursor.getInt(columnIndexOrThrow8);
                                int i20 = columnIndexOrThrow8;
                                String string = cursor.getString(columnIndexOrThrow2);
                                int i21 = columnIndexOrThrow2;
                                UserManagerCompat userManagerCompat = UserManagerCompat.getInstance(databaseHelper.mContext);
                                int i22 = columnIndexOrThrow11;
                                if (columnIndex != -1 && !cursor.isNull(columnIndex)) {
                                    i8 = columnIndexOrThrow5;
                                    i9 = columnIndexOrThrow4;
                                    j = cursor.getInt(columnIndex);
                                    userForSerialNumber = userManagerCompat.getUserForSerialNumber(j);
                                } else {
                                    i8 = columnIndexOrThrow5;
                                    i9 = columnIndexOrThrow4;
                                    UserHandle userHandleMyUserHandle = Process.myUserHandle();
                                    long serialNumberForUser = userManagerCompat.getSerialNumberForUser(userHandleMyUserHandle);
                                    userForSerialNumber = userHandleMyUserHandle;
                                    j = serialNumberForUser;
                                }
                                if (userForSerialNumber == null) {
                                    String str21 = str5;
                                    Launcher.addDumpLog(str21, "skipping deleted user", true);
                                    str5 = str21;
                                    columnIndexOrThrow7 = i10;
                                    columnIndexOrThrow9 = i12;
                                    columnIndexOrThrow6 = i13;
                                    columnIndexOrThrow12 = i16;
                                    columnIndexOrThrow10 = i18;
                                    columnIndexOrThrow8 = i20;
                                    columnIndexOrThrow2 = i21;
                                    columnIndexOrThrow11 = i22;
                                    columnIndexOrThrow4 = i9;
                                    columnIndexOrThrow5 = i8;
                                } else {
                                    String str22 = str5;
                                    int i23 = columnIndex;
                                    String string2 = cursor.getString(columnIndexOrThrow3);
                                    long j2 = j;
                                    String strContainerToString = LauncherSettings.Favorites.containerToString(i19);
                                    StringBuilder sb = new StringBuilder();
                                    int i24 = columnIndexOrThrow3;
                                    sb.append("migrating \"");
                                    sb.append(string2);
                                    sb.append("\" (");
                                    sb.append(i14);
                                    sb.append(",");
                                    sb.append(i15);
                                    sb.append("@");
                                    sb.append(strContainerToString);
                                    sb.append("/");
                                    sb.append(i17);
                                    sb.append("): ");
                                    sb.append(string);
                                    Launcher.addDumpLog(str22, sb.toString(), true);
                                    if (i11 != 2) {
                                        try {
                                            Intent uri2 = Intent.parseUri(string, 0);
                                            ComponentName component = uri2.getComponent();
                                            if (TextUtils.isEmpty(string)) {
                                                Launcher.addDumpLog(str22, "skipping empty intent", true);
                                            } else if (component != null && !LauncherModel.isValidPackageActivity(databaseHelper.mContext, component, userForSerialNumber)) {
                                                Launcher.addDumpLog(str22, "skipping item whose component no longer exists.", true);
                                            } else if (i19 == -100) {
                                                uri2.setPackage(null);
                                                int flags = uri2.getFlags();
                                                uri2.setFlags(0);
                                                String uri3 = uri2.toUri(0);
                                                uri2.setFlags(flags);
                                                if (hashSet.contains(uri3)) {
                                                    Launcher.addDumpLog(str22, "skipping duplicate", true);
                                                } else {
                                                    hashSet.add(uri3);
                                                }
                                            }
                                        } catch (URISyntaxException unused2) {
                                            Launcher.addDumpLog(str22, "skipping invalid intent uri", true);
                                        }
                                        columnIndex = i23;
                                        str5 = str22;
                                        columnIndexOrThrow7 = i10;
                                        columnIndexOrThrow9 = i12;
                                        columnIndexOrThrow6 = i13;
                                        columnIndexOrThrow12 = i16;
                                        columnIndexOrThrow10 = i18;
                                        columnIndexOrThrow8 = i20;
                                        columnIndexOrThrow2 = i21;
                                        columnIndexOrThrow11 = i22;
                                        columnIndexOrThrow4 = i9;
                                        columnIndexOrThrow5 = i8;
                                        columnIndexOrThrow3 = i24;
                                    }
                                    ContentValues contentValues = new ContentValues(cursor.getColumnCount());
                                    String str23 = str6;
                                    contentValues.put(str23, Integer.valueOf(cursor.getInt(columnIndexOrThrow)));
                                    String str24 = str7;
                                    contentValues.put(str24, string);
                                    String str25 = str8;
                                    contentValues.put(str25, cursor.getString(i24));
                                    int i25 = i9;
                                    int i26 = columnIndexOrThrow;
                                    String str26 = str9;
                                    contentValues.put(str26, Integer.valueOf(cursor.getInt(i25)));
                                    str9 = str26;
                                    int i27 = i8;
                                    byte[] blob = cursor.getBlob(i27);
                                    String str27 = str10;
                                    contentValues.put(str27, blob);
                                    str10 = str27;
                                    String str28 = str11;
                                    contentValues.put(str28, cursor.getString(i13));
                                    str11 = str28;
                                    String str29 = str12;
                                    contentValues.put(str29, cursor.getString(i10));
                                    str12 = str29;
                                    String str30 = str14;
                                    contentValues.put(str30, Integer.valueOf(i11));
                                    str14 = str30;
                                    contentValues.put("appWidgetId", (Integer) (-1));
                                    int i28 = i2;
                                    String string3 = cursor.getString(i28);
                                    i2 = i28;
                                    String str31 = str18;
                                    contentValues.put(str31, string3);
                                    int i29 = columnIndexOrThrow14;
                                    str18 = str31;
                                    Integer numValueOf = Integer.valueOf(cursor.getInt(i29));
                                    columnIndexOrThrow14 = i29;
                                    String str32 = str19;
                                    contentValues.put(str32, numValueOf);
                                    str19 = str32;
                                    String str33 = str20;
                                    contentValues.put(str33, Long.valueOf(j2));
                                    if (i19 == -101) {
                                        sparseArray4 = sparseArray2;
                                        sparseArray4.put(i17, contentValues);
                                    } else {
                                        sparseArray4 = sparseArray2;
                                    }
                                    str20 = str33;
                                    if (i19 != -100) {
                                        Integer numValueOf2 = Integer.valueOf(i17);
                                        str2 = str15;
                                        contentValues.put(str2, numValueOf2);
                                        Integer numValueOf3 = Integer.valueOf(i14);
                                        str3 = str16;
                                        contentValues.put(str3, numValueOf3);
                                        str4 = str17;
                                        contentValues.put(str4, Integer.valueOf(i15));
                                    } else {
                                        str2 = str15;
                                        str3 = str16;
                                        str4 = str17;
                                    }
                                    Integer numValueOf4 = Integer.valueOf(i19);
                                    String str34 = str13;
                                    contentValues.put(str34, numValueOf4);
                                    if (i11 != 2) {
                                        arrayList3 = arrayList;
                                        arrayList3.add(contentValues);
                                        arrayList4 = arrayList2;
                                    } else {
                                        arrayList3 = arrayList;
                                        arrayList4 = arrayList2;
                                        arrayList4.add(contentValues);
                                    }
                                    columnIndex = i23;
                                    arrayList = arrayList3;
                                    str13 = str34;
                                    str17 = str4;
                                    arrayList2 = arrayList4;
                                    str16 = str3;
                                    str6 = str23;
                                    columnIndexOrThrow4 = i25;
                                    str15 = str2;
                                    str8 = str25;
                                    str5 = str22;
                                    columnIndexOrThrow7 = i10;
                                    columnIndexOrThrow9 = i12;
                                    columnIndexOrThrow6 = i13;
                                    columnIndexOrThrow12 = i16;
                                    columnIndexOrThrow10 = i18;
                                    columnIndexOrThrow8 = i20;
                                    columnIndexOrThrow2 = i21;
                                    columnIndexOrThrow11 = i22;
                                    columnIndexOrThrow3 = i24;
                                    cursor2 = cursor;
                                    sparseArray = sparseArray4;
                                    columnIndexOrThrow = i26;
                                    str7 = str24;
                                    columnIndexOrThrow5 = i27;
                                }
                            } else {
                                columnIndexOrThrow7 = i10;
                                columnIndexOrThrow9 = i12;
                                columnIndexOrThrow6 = i13;
                            }
                            cursor2 = cursor;
                            sparseArray = sparseArray2;
                        } catch (Throwable th3) {
                            th = th3;
                        }
                        th = th3;
                        cursor.close();
                        throw th;
                    }
                    str = str5;
                    String str35 = str6;
                    String str36 = str13;
                    String str37 = str15;
                    String str38 = str16;
                    String str39 = str17;
                    cursor = cursor2;
                    ArrayList arrayList5 = arrayList;
                    ArrayList arrayList6 = arrayList2;
                    SparseArray sparseArray5 = sparseArray2;
                    int size = sparseArray5.size();
                    int i30 = 0;
                    while (i30 < size) {
                        int iKeyAt = sparseArray5.keyAt(i30);
                        ContentValues contentValues2 = (ContentValues) sparseArray5.valueAt(i30);
                        int i31 = size;
                        InvariantDeviceProfile invariantDeviceProfile3 = invariantDeviceProfile;
                        if (iKeyAt == invariantDeviceProfile3.hotseatAllAppsRank) {
                            while (true) {
                                iKeyAt++;
                                i7 = i5;
                                if (iKeyAt >= i7) {
                                    sparseArray3 = sparseArray5;
                                    break;
                                } else {
                                    if (sparseArray5.get(iKeyAt) == null) {
                                        sparseArray3 = sparseArray5;
                                        contentValues2.put(str37, Integer.valueOf(iKeyAt));
                                        break;
                                    }
                                    i5 = i7;
                                }
                            }
                        } else {
                            sparseArray3 = sparseArray5;
                            i7 = i5;
                        }
                        if (iKeyAt >= i7) {
                            contentValues2.put(str36, (Integer) (-100));
                        }
                        i30++;
                        i5 = i7;
                        invariantDeviceProfile = invariantDeviceProfile3;
                        sparseArray5 = sparseArray3;
                        size = i31;
                    }
                    ArrayList<ContentValues> arrayList7 = new ArrayList();
                    arrayList7.addAll(arrayList6);
                    arrayList7.addAll(arrayList5);
                    iGenerateNewScreenId = 0;
                    int i32 = 0;
                    loop3: while (true) {
                        int i33 = 0;
                        for (ContentValues contentValues3 : arrayList7) {
                            if (contentValues3.getAsInteger(str36).intValue() == -100) {
                                contentValues3.put(str37, Integer.valueOf(iGenerateNewScreenId));
                                contentValues3.put(str38, Integer.valueOf(i32));
                                contentValues3.put(str39, Integer.valueOf(i33));
                                i32 = (i32 + 1) % i3;
                                if (i32 == 0) {
                                    i33++;
                                }
                                if (i33 == i4 - 1) {
                                    break;
                                }
                            }
                        }
                        iGenerateNewScreenId = (int) generateNewScreenId();
                    }
                    if (arrayList7.size() > 0) {
                        db.beginTransaction();
                        try {
                            i6 = 0;
                            for (ContentValues contentValues4 : arrayList7) {
                                if (contentValues4 != null) {
                                    try {
                                        if (LauncherProvider.dbInsertAndCheck(databaseHelper, db, "favorites", null, contentValues4) < 0) {
                                            cursor.close();
                                            return;
                                        }
                                        i6++;
                                    } catch (Throwable th4) {
                                        th = th4;
                                        throw th;
                                    }
                                }
                            }
                            sQLiteDatabase = db;
                            db.setTransactionSuccessful();
                            db.endTransaction();
                        } catch (Throwable th5) {
                            th = th5;
                        }
                    } else {
                        sQLiteDatabase = db;
                        i6 = 0;
                    }
                    db.beginTransaction();
                    for (int i34 = 0; i34 <= iGenerateNewScreenId; i34++) {
                        try {
                            ContentValues contentValues5 = new ContentValues();
                            contentValues5.put(str35, Integer.valueOf(i34));
                            contentValues5.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i34));
                            if (LauncherProvider.dbInsertAndCheck(databaseHelper, sQLiteDatabase, "workspaceScreens", null, contentValues5) < 0) {
                                cursor.close();
                                return;
                            }
                        } finally {
                            db.endTransaction();
                        }
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    databaseHelper.updateFolderItemsRank(sQLiteDatabase, false);
                    i = i6;
                } else {
                    str = str5;
                    cursor = cursorQuery;
                    i = 0;
                    databaseHelper = this;
                    iGenerateNewScreenId = 0;
                }
                cursor.close();
            } else {
                databaseHelper = this;
                str = str5;
                i = 0;
                iGenerateNewScreenId = 0;
            }
            Launcher.addDumpLog(str, "migrated " + i + " icons from Launcher2 into " + (iGenerateNewScreenId + 1) + " screens", true);
            setFlagJustLoadedOldDb();
            databaseHelper.mMaxItemId = initializeMaxItemId(db);
            databaseHelper.mMaxScreenId = initializeMaxScreenId(db);
        }

        public void updateMaxScreenId(long maxScreenId) {
            this.mMaxScreenId = maxScreenId;
        }

        private void addHideAppsTable(SQLiteDatabase db) {
            LGLog.i(LauncherProvider.TAG, "Create table <hideapps>");
            db.execSQL("CREATE TABLE IF NOT EXISTS hideapps (_id INTEGER PRIMARY KEY,componentName TEXT NOT NULL, profileId INTEGER NOT NULL,modified INTEGER NOT NULL DEFAULT 0);");
        }

        private void addSharingContentsTable(SQLiteDatabase db) {
            if (hasTable(db, "sharingContents")) {
                LGLog.i(LauncherProvider.TAG, "Table <sharingContents> is already exist.");
            } else {
                createSharingContentTable(db);
                initializeSharingContentTable(db);
            }
        }

        private void createSharingContentTable(SQLiteDatabase db) {
            LGLog.i(LauncherProvider.TAG, "Create table <sharingContents>");
            db.execSQL("CREATE TABLE sharingContents (_id INTEGER PRIMARY KEY,adaptiveTextColor TEXT,wallpaperCommonColor TEXT,modified INTEGER NOT NULL DEFAULT 0);");
        }

        private void initializeSharingContentTable(SQLiteDatabase db) {
            LGLog.i(LauncherProvider.TAG, "Initialize table <sharingContents>");
            String hexString = Integer.toHexString(AdaptiveTextUtil.getAdaptiveTextColor(this.mContext));
            String hexString2 = Integer.toHexString(0);
            LGLog.i(LauncherProvider.TAG, "  # AdaptiveTextColor: " + hexString);
            LGLog.i(LauncherProvider.TAG, "  # WallpaperCommonColor: " + hexString2);
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", (Integer) 0);
            contentValues.put(LauncherSettings.SharingContents.ADAPTIVE_TEXT_COLOR, hexString);
            contentValues.put(LauncherSettings.SharingContents.WALLPAPER_COMMON_COLOR, hexString2);
            LauncherProvider.dbInsertAndCheck(this, db, "sharingContents", null, contentValues);
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

        private void checkUpgradeDB(Context context) {
            SharedPreferences.Editor editorEdit = context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).edit();
            editorEdit.putBoolean("upgrade", true);
            editorEdit.apply();
        }

        private void addWorkspaceDetailTable(SQLiteDatabase db) {
            LGLog.i(LauncherProvider.TAG, "Create table <workspaceDetail>");
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS workspaceDetail (_id INTEGER PRIMARY KEY,defaultScreen INTEGER NOT NULL DEFAULT 0, modified INTEGER NOT NULL DEFAULT 0);");
                int integer = this.mContext.getResources().getInteger(R.integer.config_workspaceDefaultScreen);
                ContentValues contentValues = new ContentValues();
                contentValues.put("_id", (Integer) 0);
                contentValues.put(LauncherSettings.WorkspaceDetail.DEFAULT_SCREEN, Integer.valueOf(integer));
                LauncherProvider.dbInsertAndCheck(this, db, "workspaceDetail", null, contentValues);
            } catch (SQLException e) {
                Log.e(LauncherProvider.TAG, e.getMessage(), e);
            }
        }

        private void checkHasWorkspaceDetailTable(SQLiteDatabase db) {
            if (hasTable(db, "workspaceDetail")) {
                return;
            }
            addWorkspaceDetailTable(db);
        }

        private void addHomePreferencesTable(SQLiteDatabase db) {
            LGLog.i(LauncherProvider.TAG, "add table <homePreferences>");
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS homePreferences (_id INTEGER,key TEXT PRIMARY KEY, value TEXT, modified INTEGER NOT NULL DEFAULT 0);");
                insertHomePreferences(db, LauncherConst.DB_HOME_PREF_KEY_APPBOX_RELOAD, LauncherConst.DB_HOME_PREF_VALUE_NONE);
                int integer = this.mContext.getResources().getInteger(LauncherProvider.sIsDisabledEasyHome ? R.integer.device_profile_default_numColumns : R.integer.device_profile_default_numColumns_easyhome);
                int integer2 = this.mContext.getResources().getInteger(LauncherProvider.sIsDisabledEasyHome ? R.integer.device_profile_default_numRows : R.integer.device_profile_default_numRows_easyhome);
                insertHomePreferences(db, SharedPreferencesManager.toKeyString(SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS), Integer.toString(integer));
                insertHomePreferences(db, SharedPreferencesManager.toKeyString(SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS), Integer.toString(integer2));
            } catch (SQLException e) {
                Log.e(LauncherProvider.TAG, e.getMessage(), e);
            }
        }

        private void insertHomePreferences(SQLiteDatabase db, String key, String value) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", (Integer) 0);
            contentValues.put(LauncherSettings.HomePreferences.KEY, key);
            contentValues.put("value", value);
            db.insert("homePreferences", null, contentValues);
        }

        private void updateHomePreferences(SQLiteDatabase db, String key, String value) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", (Integer) 0);
            contentValues.put(LauncherSettings.HomePreferences.KEY, key);
            contentValues.put("value", value);
            db.update("homePreferences", contentValues, "key = ?", new String[]{key});
        }

        private void migrateGridInfoFromSharedPreferences(SQLiteDatabase db) {
            LGLog.i(LauncherProvider.TAG, "migrateGridInfoFromSharedPreferences");
            try {
                int i = SharedPreferencesManager.getInt(this.mContext, 0, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, 0);
                int i2 = SharedPreferencesManager.getInt(this.mContext, 0, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, 0);
                if (i == 0 || i2 == 0) {
                    return;
                }
                LGLog.i(LauncherProvider.TAG, "Restored from pref, col = " + i + " row = " + i2);
                updateHomePreferences(db, SharedPreferencesManager.toKeyString(SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS), Integer.toString(i));
                updateHomePreferences(db, SharedPreferencesManager.toKeyString(SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS), Integer.toString(i2));
                SharedPreferencesManager.remove(this.mContext, 0, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS);
                SharedPreferencesManager.remove(this.mContext, 0, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS);
            } catch (SQLException e) {
                Log.e(LauncherProvider.TAG, e.getMessage(), e);
            }
        }

        private void checkHasHomePreferencesTable(SQLiteDatabase db) {
            if (hasTable(db, "homePreferences")) {
                return;
            }
            addHomePreferencesTable(db);
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

    public void resetDatabaseHelper() {
        String launcherDBFileName = getLauncherDBFileName(getContext());
        LGLog.i(TAG, "launcherDBFileName = " + launcherDBFileName);
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(), launcherDBFileName);
        this.mOpenHelper = databaseHelper;
        databaseHelper.getWritableDatabase();
    }

    public static boolean isUpgradeDB(Context context) {
        return context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).getBoolean("upgrade", false);
    }

    public void updateMaxScreenId(long maxScreenId) {
        this.mOpenHelper.updateMaxScreenId(maxScreenId);
    }

    public void deleteAppWidgetIds(final int[] ids) {
        new MainThreadExecutor().execute(new Runnable() { // from class: com.android.launcher3.LauncherProvider.1
            @Override // java.lang.Runnable
            public void run() {
                if (LauncherProvider.this.mListener != null) {
                    LauncherProvider.this.mListener.onDeleteAppWidgetIds(ids);
                    return;
                }
                AppWidgetHost appWidgetHost = new AppWidgetHost(LauncherProvider.this.getContext(), 1024);
                for (int i : ids) {
                    appWidgetHost.deleteAppWidgetId(i);
                }
            }
        });
    }

    public static String getLauncherDBFileName(Context context) {
        int defaultHome = LGHomeFeature.getDefaultHome(context);
        boolean z = defaultHome != 1;
        sIsDisabledAllApps = z;
        boolean z2 = defaultHome != 2;
        sIsDisabledEasyHome = z2;
        String str = z ? LauncherFiles.LAUNCHER_DB : LauncherFiles.LAUNCHER_ALLAPPS_DB;
        if (!z2) {
            str = LauncherFiles.LAUNCHER_EASYHOME_DB;
        }
        LGLog.d(TAG, "getLauncherDBFileName: " + str);
        return str;
    }

    public static void updateHomeInfoFromOOS(Context context) {
        int intForUser = Settings.Secure.getIntForUser(context.getContentResolver(), LauncherConst.LGHOME_DEFAULT_HOME, -1, Process.myUserHandle().getIdentifier());
        LGLog.i(TAG, "updateHomeInfoFromOOS :  getOriginalRelease() = " + com.lge.launcher3.util.Utilities.getOriginalRelease() + ", currentType = " + intForUser);
        if (com.lge.launcher3.util.Utilities.isAtLeastOriginalReleasePie()) {
            return;
        }
        boolean z = LGSharedPreferences.get(context, LauncherConst.PERM_PREFERENCES_FILE_NANE, 0).getBoolean(LauncherConst.PERM_PREFERENCES_KEY_ALLAPPS_DISABLED, context.getResources().getBoolean(R.bool.config_feature_disable_allapps));
        boolean z2 = LGSharedPreferences.get(context, LauncherConst.PERM_PREFERENCES_FILE_NANE, 0).getBoolean(LauncherConst.PERM_PREFERENCES_KEY_EASYHOME_DISABLED, context.getResources().getBoolean(R.bool.config_feature_disable_easyhome));
        int i = !z ? 1 : 0;
        if (!z2) {
            i = 2;
        }
        boolean z3 = LGSharedPreferences.get(context, HomeSettingsSharedPreferences.SHARED_PREFERENCES_KEY, 4).getBoolean(HomeSettingsSharedPreferences.ENABLE_APPDRAWER_BUTTON, true);
        boolean z4 = !z;
        StringBuilder sb = new StringBuilder();
        sb.append("updateHomeInfoFromOOS : type = ");
        sb.append(i);
        sb.append(", allapps = ");
        sb.append(z4);
        sb.append(", easyHome = ");
        sb.append(!z2);
        sb.append(", isUseAppDrawerButton = ");
        sb.append(z3);
        LGLog.i(TAG, sb.toString());
        Settings.Secure.putIntForUser(context.getContentResolver(), LauncherConst.LGHOME_DEFAULT_HOME, i, Process.myUserHandle().getIdentifier());
        Settings.System.putIntForUser(context.getContentResolver(), HomeSettingsSharedPreferences.ENABLE_APPDRAWER_BUTTON, z3 ? 1 : 0, Process.myUserHandle().getIdentifier());
    }
}
