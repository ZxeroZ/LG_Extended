package com.lge.launcher3.backuprestore;

import android.app.WallpaperManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.widget.Toast;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.LauncherSettings;
import com.lge.content.pm.PackageManagerEx;
import com.lge.launcher3.AllAppsLauncherExtension;
import com.lge.launcher3.EasyHomeLauncherExtension;
import com.lge.launcher3.LauncherExtension;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsDBProvider;
import com.lge.launcher3.allapps.SwivelAllAppsDBProvider;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.homesettings.SBHomeDataBaseUtil;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.Utilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.Date;

/* JADX INFO: loaded from: classes.dex */
public class BackupRestoreImpl {
    public static final String NEXT_REFESH = "refresh";
    public static final String NEXT_RESTORE = "restore";
    private static final String TAG = "LGBackupRestore";
    private static final String WORKSPACE_FILE_FOR_LGHOME = "workspace.db";
    private static final String WORKSPACE_NAME_FOR_LGHOME = "workspace";
    public boolean BACKUP_RESTORE_PREFERENCES_ENABLED;
    private AppWidgetHost mAppWidgetHost;
    private AppWidgetManager mAppWidgetManager;
    private final Context mContext;
    private LGHomeType mLGHomeType;
    public File exportDir = null;
    public File mLoadMenuFile = null;
    public boolean BACKUP_RESTORE_PREFERENCES_DISABLED = false;
    private boolean mIsBackupCancel = false;

    private enum LGHomeType {
        LGHome4,
        LGHome5_6,
        LGHome6_1
    }

    public BackupRestoreImpl(Context context) {
        this.mContext = context;
        this.mAppWidgetHost = new AppWidgetHost(context, 1024);
        this.mAppWidgetManager = AppWidgetManager.getInstance(context);
    }

    String existBackupFile() {
        File[] fileArrListFiles;
        File file = new File(getDatabaseInHomeDirectory());
        if (!file.exists() || (fileArrListFiles = file.listFiles()) == null) {
            return null;
        }
        for (File file2 : fileArrListFiles) {
            if (file2.getName().equals(WORKSPACE_FILE_FOR_LGHOME)) {
                Date date = new Date(file2.lastModified());
                return DateFormat.getDateFormat(this.mContext).format(date) + "  " + DateFormat.getTimeFormat(this.mContext).format(date);
            }
        }
        return null;
    }

    public Boolean backupTo(String workspaceName, String allAppsWorkspaceName, String menuName, String swivelWorkspaceName, String swivelMenuName, String wallpapername, String swivelWallpaperName) throws Throwable {
        if (!isExternalStorageAvail()) {
            Toast.makeText(this.mContext, R.string.sp_sdcard_unmounted_ics_vzw_NORMAL, 0).show();
            LGLog.d(TAG, "No storage available when backup");
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        backupWallpaper(wallpapername);
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            backupSwivelWallpaper(swivelWallpaperName);
        }
        try {
            LauncherSettings.Settings.call(this.mContext.getContentResolver(), LauncherSettings.Settings.METHOD_CLOSE_DB);
            AllAppsDBProvider.closeDB();
            backupDB(LauncherFiles.LAUNCHER_DB, workspaceName);
            backupDB(LauncherFiles.LAUNCHER_ALLAPPS_DB, allAppsWorkspaceName);
            backupDB(AllAppsDBProvider.DATABASE_NAME, menuName);
            if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                backupDB("launcher_swivel.db", swivelWorkspaceName);
                backupDB(SwivelAllAppsDBProvider.DATABASE_NAME, swivelMenuName);
            }
            backupPref(LGBackupRestoreAgent.PREF_FOR_BACKUP);
            long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
            if (jCurrentTimeMillis2 < 2500) {
                try {
                    Thread.sleep(2500 - jCurrentTimeMillis2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (IOException unused) {
            return false;
        }
    }

    private void backupDB(String srcName, String destName) throws IOException {
        File file;
        LGLog.i(TAG, "backupDB() : " + srcName + "->" + destName);
        File file2 = new File(getDatabaseDir() + "/" + srcName);
        File file3 = new File(getDatabaseInHomeDirectory());
        if (!file3.exists()) {
            file3.mkdirs();
        }
        if (file2.exists()) {
            flushDBIfNeeded(file2);
            if (destName.indexOf(".db") == -1) {
                file = new File(file3, destName + ".db");
            } else {
                file = new File(file3, destName);
            }
            file.createNewFile();
            copyFile(file2, file);
        }
    }

    private void flushDBIfNeeded(File dbFile) {
        if (((new File(dbFile.getPath() + "-journal").exists() | false) | new File(dbFile.getPath() + "-shm").exists()) || new File(dbFile.getPath() + "-wal").exists()) {
            LGLog.i(TAG, "flush DB: " + dbFile);
            try {
                SQLiteDatabase.openOrCreateDatabase(dbFile, (SQLiteDatabase.CursorFactory) null).close();
            } catch (SQLException e) {
                LGLog.w(TAG, e.toString(), new int[0]);
            }
        }
    }

    private void backupPref(String prefName) throws IOException {
        LGLog.i(TAG, "backupPref() : " + prefName);
        ResolveInfo defaultHomeActivityResolveInfo = PackageUtils.getDefaultHomeActivityResolveInfo(this.mContext);
        if (defaultHomeActivityResolveInfo == null) {
            return;
        }
        int defaultHome = LGHomeFeature.getDefaultHome(this.mContext);
        String name = defaultHomeActivityResolveInfo.activityInfo.name;
        if ("com.lge.launcher3".equals(defaultHomeActivityResolveInfo.activityInfo.packageName)) {
            if (defaultHome == 1) {
                name = AllAppsLauncherExtension.class.getName();
            } else if (defaultHome == 2) {
                name = EasyHomeLauncherExtension.class.getName();
            } else {
                name = LauncherExtension.class.getName();
            }
        }
        LGLog.i(TAG, "backup default home = " + defaultHomeActivityResolveInfo.activityInfo.name + ", type = " + defaultHome);
        this.mContext.getSharedPreferences(LGBackupRestoreAgent.PREF_FOR_BACKUP, 0).edit().putString(LGBackupRestoreAgent.KEY_DEFAULT_HOME, name).commit();
        File file = new File(getPrefDir() + "/" + prefName + ".xml");
        File file2 = new File(getDatabaseInHomeDirectory());
        if (!file2.exists()) {
            file2.mkdirs();
        }
        if (file.exists()) {
            File file3 = new File(file2, prefName + ".xml");
            file3.createNewFile();
            copyFile(file, file3);
        }
    }

    private boolean isExternalStorageAvail() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    static class Result {
        String errMsg;
        BackupErrorCode errorCode = BackupErrorCode.NO_ERROR;
        boolean succeeded;

        Result(boolean dbInvalid, String errMsg) {
            this.succeeded = false;
            this.succeeded = dbInvalid;
            this.errMsg = errMsg;
        }

        public static Result succeeded() {
            return new Result(true, null);
        }

        public static Result failed(String message) {
            return new Result(false, message);
        }
    }

    public Result restoreFrom(File workspaceDbFile, File allAppsWorkspaceDbFile, File menuDbFile, File swivelWorkspaceDbFile, File swivelMenuDbFile, File wallpaperFile, File swivelWallpaperFile, File prefFile) {
        Resources resources = this.mContext.getResources();
        if (workspaceDbFile == null && allAppsWorkspaceDbFile == null) {
            return Result.failed(resources.getString(R.string.sp_nofile_sdcard_NORMAL));
        }
        if (!isExternalStorageAvail()) {
            return Result.failed(resources.getString(R.string.sp_sdcard_unmounted_ics_vzw_NORMAL));
        }
        try {
            LauncherSettings.Settings.call(this.mContext.getContentResolver(), LauncherSettings.Settings.METHOD_START_RESTORE_DB);
            AppWidgetHost appWidgetHost = new AppWidgetHost(this.mContext, 1024);
            int[] appWidgetIds = appWidgetHost.getAppWidgetIds();
            importWorkspaceDb(workspaceDbFile, LauncherFiles.LAUNCHER_DB);
            importWorkspaceDb(allAppsWorkspaceDbFile, LauncherFiles.LAUNCHER_ALLAPPS_DB);
            if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                importWorkspaceDb(swivelWorkspaceDbFile, "launcher_swivel.db");
            }
            for (int i : appWidgetIds) {
                appWidgetHost.deleteAppWidgetId(i);
            }
            importMenuDb(menuDbFile);
            if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                importSwivelMenuDb(swivelMenuDbFile);
            }
            importPref(prefFile, LGBackupRestoreAgent.PREF_FOR_RESTORE);
            if (this.mLGHomeType == LGHomeType.LGHome4) {
                setDefaultHomeByClass(AllAppsLauncherExtension.class);
            } else if (this.mLGHomeType == LGHomeType.LGHome5_6) {
                setDefaultHomeByClass(LauncherExtension.class);
            } else {
                String string = this.mContext.getSharedPreferences(LGBackupRestoreAgent.PREF_FOR_RESTORE, 0).getString(LGBackupRestoreAgent.KEY_DEFAULT_HOME, null);
                LGLog.i(TAG, "default home = " + string);
                if (string != null) {
                    if (string.contains(LauncherExtension.class.getName())) {
                        setDefaultHomeByClass(LauncherExtension.class);
                    } else if (string.contains(AllAppsLauncherExtension.class.getName())) {
                        setDefaultHomeByClass(AllAppsLauncherExtension.class);
                    }
                }
            }
            LauncherSettings.Settings.call(this.mContext.getContentResolver(), LauncherSettings.Settings.METHOD_RESET_DATABASE_HELPER);
            LauncherSettings.Settings.call(this.mContext.getContentResolver(), LauncherSettings.Settings.METHOD_CLEAR_EMPTY_DB_FLAG);
            LauncherSettings.Settings.call(this.mContext.getContentResolver(), LauncherSettings.Settings.METHOD_END_RESTORE_DB);
            restoreWallpaper(wallpaperFile);
            if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                restoreSwivelWallpaper(swivelWallpaperFile);
            }
            onRestoreComplete();
            return Result.succeeded();
        } catch (ImportException e) {
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    private void setDefaultHomeByClass(Class<?> cls) {
        LGLog.i(TAG, "change default home to " + cls.getName() + ", originalRelease = " + Utilities.isAtLeastOriginalReleasePie());
        PackageUtils.setDefaultHome(this.mContext, new ComponentName(this.mContext.getPackageName(), LauncherExtension.class.getName()));
        int i = !cls.equals(LauncherExtension.class) ? 1 : 0;
        LGHomeFeature.updateDefaultHome(this.mContext, i);
        Settings.Secure.putInt(this.mContext.getApplicationContext().getContentResolver(), LauncherConst.LGHOME_DEFAULT_HOME, i);
        if (Utilities.isAtLeastOriginalReleasePie()) {
            return;
        }
        LGHomeFeature.updateDisableAllAppsState(this.mContext, i != 1);
        LGHomeFeature.updateDisableEasyHomeState(this.mContext, i != 2);
        LGHomeFeature.updateEnableSwivelHomeState(this.mContext, false);
    }

    private void onRestoreComplete() {
        deleteRecentlyUninstallApps();
        SharedPreferencesManager.putBoolean(this.mContext, 0, SharedPreferencesConst.BackupRestoreKey.RESTORED, true);
    }

    private void deleteRecentlyUninstallApps() {
        String[] strArr = new String[0];
        UserHandle userHandleMyUserHandle = Process.myUserHandle();
        IPackageManager iPackageManagerAsInterface = IPackageManager.Stub.asInterface(ServiceManager.getService(AppNotifierManager.ExtraSpec.USAGE_PACKAGE));
        try {
            for (String str : PackageManagerEx.getDefault().getDisabledByLGLauncherPackageList(userHandleMyUserHandle.getIdentifier())) {
                try {
                    iPackageManagerAsInterface.deletePackageAsUser(str, -1, (IPackageDeleteObserver) null, userHandleMyUserHandle.getIdentifier(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        } catch (RemoteException e2) {
            e2.printStackTrace();
        } catch (NoClassDefFoundError unused) {
            LGLog.d(TAG, "Not implement PackageManagerEX in framework");
        }
    }

    protected void importWorkspaceDb(File dbFile, String dstName) throws ImportException {
        importWorkspaceDb(dbFile, dstName, false);
    }

    protected void importWorkspaceDb(File dbFile, String dstName, boolean isUpgradeHome4) throws ImportException {
        if (dbFile == null) {
            LGLog.i(TAG, "importWorkspaceDb(): " + dbFile + " is not exist");
            return;
        }
        LGLog.i(TAG, "importWorkspaceDb(): " + dbFile + "->" + dstName);
        try {
            SQLiteDatabase sQLiteDatabaseOpenOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, (SQLiteDatabase.CursorFactory) null);
            try {
                Cursor cursorQuery = sQLiteDatabaseOpenOrCreateDatabase.query("favorites", null, null, null, null, null, null);
                try {
                    if (cursorQuery.getCount() <= 0) {
                        LGLog.w(TAG, "importWorkspaceDb(): skipped, favorites is empty.", new int[0]);
                        sQLiteDatabaseOpenOrCreateDatabase.close();
                        if (cursorQuery != null) {
                            cursorQuery.close();
                            return;
                        }
                        return;
                    }
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    if (sQLiteDatabaseOpenOrCreateDatabase.getVersion() < 100) {
                        this.mLGHomeType = LGHomeType.LGHome4;
                        try {
                            new Home4MigrationHelper(this.mContext, sQLiteDatabaseOpenOrCreateDatabase).migrate();
                            sQLiteDatabaseOpenOrCreateDatabase.setVersion(100);
                        } catch (Exception e) {
                            throw new ImportException(this.mContext.getString(R.string.sp_load_failed_NORMAL), e);
                        }
                    } else if (sQLiteDatabaseOpenOrCreateDatabase.getVersion() < 104) {
                        this.mLGHomeType = LGHomeType.LGHome5_6;
                    } else {
                        this.mLGHomeType = LGHomeType.LGHome6_1;
                    }
                    saveCurrentGridInfo(sQLiteDatabaseOpenOrCreateDatabase);
                    importFavoritesTable(sQLiteDatabaseOpenOrCreateDatabase);
                    if (!isUpgradeHome4) {
                        needsToSkipCustomPage();
                    }
                    sQLiteDatabaseOpenOrCreateDatabase.close();
                    String databaseDir = getDatabaseDir();
                    if (this.mLGHomeType == LGHomeType.LGHome4) {
                        dstName = LauncherFiles.LAUNCHER_ALLAPPS_DB;
                    }
                    File file = new File(databaseDir + "/" + dstName);
                    LGLog.i(TAG, "delete old databases before copy new files - result = " + SQLiteDatabase.deleteDatabase(file) + ", exist = " + file.exists());
                    try {
                        copyFile(dbFile, file);
                        return;
                    } catch (Exception e2) {
                        throw new ImportException(this.mContext.getString(R.string.sp_load_failed_NORMAL), e2);
                    }
                } finally {
                }
            } catch (Exception e3) {
                LGLog.w(TAG, "importWorkspaceDb(): skipped, " + e3.toString(), new int[0]);
                sQLiteDatabaseOpenOrCreateDatabase.close();
            }
            LGLog.w(TAG, "importWorkspaceDb(): skipped, " + e3.toString(), new int[0]);
            sQLiteDatabaseOpenOrCreateDatabase.close();
        } catch (SQLiteException e4) {
            throw new ImportException(this.mContext.getString(R.string.sp_file_corrupted_NORMAL), e4);
        }
    }

    private void saveCurrentGridInfo(SQLiteDatabase database) {
        if (this.mLGHomeType == LGHomeType.LGHome5_6) {
            int integer = this.mContext.getResources().getInteger(R.integer.device_profile_default_numColumns);
            int integer2 = this.mContext.getResources().getInteger(R.integer.device_profile_default_numRows);
            int sharedPrefValue = LGInvariantDeviceProfile.getSharedPrefValue(this.mContext, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, integer);
            int sharedPrefValue2 = LGInvariantDeviceProfile.getSharedPrefValue(this.mContext, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, integer2);
            SharedPreferencesManager.putInt(this.mContext, 0, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, sharedPrefValue);
            SharedPreferencesManager.putInt(this.mContext, 0, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, sharedPrefValue2);
            LGLog.i(TAG, "saveCurrentGridInfo() : cols = " + sharedPrefValue + ", rows = " + sharedPrefValue2);
        }
    }

    protected void importMenuDb(File dbFile) throws ImportException {
        if (dbFile == null) {
            LGLog.i(TAG, "importMenuDb(): dbFile is not exist");
            return;
        }
        try {
            SQLiteDatabase sQLiteDatabaseOpenOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, (SQLiteDatabase.CursorFactory) null);
            if (sQLiteDatabaseOpenOrCreateDatabase.getVersion() < 100) {
                LGLog.i(TAG, "LGHome4 DB imported, version " + sQLiteDatabaseOpenOrCreateDatabase.getVersion());
                migrateHome4MenuDB(sQLiteDatabaseOpenOrCreateDatabase);
                sQLiteDatabaseOpenOrCreateDatabase.setVersion(100);
            }
            sQLiteDatabaseOpenOrCreateDatabase.close();
            File file = new File(getDatabaseDir() + "/AllAppsInfos.db");
            LGLog.i(TAG, "delete old databases before copy new files - result = " + SQLiteDatabase.deleteDatabase(file) + ", exist = " + file.exists());
            try {
                copyFile(dbFile, file);
            } catch (Exception e) {
                throw new ImportException(this.mContext.getString(R.string.sp_load_failed_NORMAL), e);
            }
        } catch (SQLiteException e2) {
            throw new ImportException(this.mContext.getString(R.string.sp_file_corrupted_NORMAL), e2);
        }
    }

    protected void importSwivelMenuDb(File dbFile) throws ImportException {
        if (dbFile == null) {
            LGLog.i(TAG, "importMenuDb(): dbFile is not exist");
            return;
        }
        try {
            SQLiteDatabase sQLiteDatabaseOpenOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, (SQLiteDatabase.CursorFactory) null);
            if (sQLiteDatabaseOpenOrCreateDatabase.getVersion() < 100) {
                LGLog.i(TAG, "LGHome4 DB imported, version " + sQLiteDatabaseOpenOrCreateDatabase.getVersion());
                migrateHome4MenuDB(sQLiteDatabaseOpenOrCreateDatabase);
                sQLiteDatabaseOpenOrCreateDatabase.setVersion(100);
            }
            sQLiteDatabaseOpenOrCreateDatabase.close();
            File file = new File(getDatabaseDir() + "/SwivelAllAppsInfos.db");
            LGLog.i(TAG, "delete old databases before copy new files - result = " + SQLiteDatabase.deleteDatabase(file) + ", exist = " + file.exists());
            try {
                copyFile(dbFile, file);
            } catch (Exception e) {
                throw new ImportException(this.mContext.getString(R.string.sp_load_failed_NORMAL), e);
            }
        } catch (SQLiteException e2) {
            throw new ImportException(this.mContext.getString(R.string.sp_file_corrupted_NORMAL), e2);
        }
    }

    private void importPref(File prefFile, String dstPrefName) throws ImportException {
        if (prefFile == null) {
            LGLog.i(TAG, "importPref(): prefFile is not exist");
            return;
        }
        try {
            copyFile(prefFile, new File(getPrefDir() + "/" + dstPrefName + ".xml"));
        } catch (Exception e) {
            throw new ImportException(this.mContext.getString(R.string.sp_load_failed_NORMAL), e);
        }
    }

    private boolean needsToSkipCustomPage() {
        if (SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(this.mContext)) {
            SBHomeDataBaseUtil.turnOffSmartBulletin(this.mContext);
        }
        if (!SBHomeDataBaseUtil.existQmemoPanelItemInDataBase(this.mContext)) {
            return false;
        }
        SBHomeDataBaseUtil.turnOffQMemoPanel(this.mContext);
        return false;
    }

    /* JADX DEBUG: Another duplicated slice has different insns count: {[]}, finally: {[INVOKE, MOVE_EXCEPTION, INVOKE, MOVE_EXCEPTION] complete} */
    private void copyFile(File src, File dst) throws IOException {
        LGLog.i(TAG, "copyFile() start : " + src + " -> " + dst);
        FileInputStream fileInputStream = new FileInputStream(src);
        try {
            FileChannel channel = fileInputStream.getChannel();
            FileOutputStream fileOutputStream = new FileOutputStream(dst);
            try {
                FileChannel channel2 = fileOutputStream.getChannel();
                if (channel != null && channel2 != null) {
                    channel.transferTo(0L, channel.size(), channel2);
                }
                fileOutputStream.close();
                fileInputStream.close();
                LGLog.i(TAG, "copyFile() end : " + src + " -> " + dst);
            } finally {
            }
        } catch (Throwable th) {
            try {
                fileInputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private String getDatabaseDir() {
        return this.mContext.getFilesDir().getAbsolutePath().replace("files", "databases");
    }

    private String getPrefDir() {
        return this.mContext.getFilesDir().getAbsolutePath().replace("files", "shared_prefs");
    }

    public long getDatabaseSize() throws Throwable {
        long length;
        long length2;
        long length3;
        File file = new File(getDatabaseDir() + "/launcher.db");
        File file2 = new File(getDatabaseDir() + "/launcher_allapps.db");
        File file3 = new File(getDatabaseDir() + "/AllAppsInfos.db");
        File file4 = new File(getDatabaseDir() + "/launcher_swivel.db");
        File file5 = new File(getDatabaseDir() + "/SwivelAllAppsInfos.db");
        backupWallpaperCheckSize("wallpaper_for_lgbackup_check_size");
        File file6 = new File(getDatabaseInHomeDirectory() + "/wallpaper_for_lgbackup_check_size.dat");
        try {
            backupPref(LGBackupRestoreAgent.PREF_FOR_BACKUP);
        } catch (IOException e) {
            LGLog.e(TAG, e.toString());
        }
        File file7 = new File(getPrefDir() + "/pref_for_backup.xml");
        if (file.exists()) {
            LGLog.i(TAG, "size = " + file.length() + " " + file.toString());
            length = file.length();
        } else {
            length = 0;
        }
        if (file2.exists()) {
            LGLog.i(TAG, "size = " + file2.length() + " " + file2.toString());
            length += file2.length();
        }
        if (file3.exists()) {
            LGLog.i(TAG, "size = " + file3.length() + " " + file3.toString());
            length += file3.length();
        }
        if (file4.exists()) {
            LGLog.i(TAG, "size = " + file4.length() + " " + file4.toString());
            length = file4.length();
        }
        if (file5.exists()) {
            LGLog.i(TAG, "size = " + file5.length() + " " + file5.toString());
            length += file5.length();
        }
        if (file6.exists()) {
            LGLog.i(TAG, "size = " + file6.length() + " " + file6.toString());
            length2 = file6.length();
            file6.delete();
        } else {
            length2 = 0;
        }
        if (file7.exists()) {
            LGLog.i(TAG, "size = " + file7.length() + " " + file7.toString());
            length3 = file7.length() + 0;
        } else {
            length3 = 0;
        }
        return length + length2 + length3;
    }

    public String getDatabaseInHomeDirectory() {
        return this.mContext.getFilesDir().getAbsolutePath().replace("files", "home");
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:11:0x0035 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:13:0x0037 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:27:0x004d */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:28:0x0008 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:31:0x0008 */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v10 */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r2v5 */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r2v8 */
    /* JADX WARN: Type inference failed for: r2v9 */
    private void SaveBitmapToFileCache(Bitmap bitmap, String str) throws Throwable {
        File file = new File(str);
        ?? r2 = 0;
        r2 = 0;
        r2 = 0;
        try {
            try {
                try {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    try {
                        r2 = 100;
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        LGLog.i(TAG, "SaveBitmapToFileCache(): " + str);
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e = e;
                        r2 = fileOutputStream;
                        LGLog.e(TAG, e.getMessage());
                        if (r2 != 0) {
                            r2.close();
                            r2 = r2;
                        }
                    } catch (Throwable th) {
                        th = th;
                        r2 = fileOutputStream;
                        if (r2 != 0) {
                            try {
                                r2.close();
                            } catch (IOException e2) {
                                LGLog.e(TAG, e2.getMessage());
                            }
                        }
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                }
            } catch (IOException e4) {
                LGLog.e(TAG, e4.getMessage());
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private boolean backupWallpaper(String wallpapername) throws Throwable {
        if (wallpapername == null) {
            wallpapername = "curwallpaper";
        }
        File file = new File(getDatabaseInHomeDirectory());
        if (!file.exists()) {
            file.mkdirs();
        }
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this.mContext);
        if (wallpaperManager.getWallpaperInfo() == null) {
            Bitmap bitmap = ((BitmapDrawable) wallpaperManager.getDrawable()).getBitmap();
            if (bitmap != null) {
                SaveBitmapToFileCache(bitmap, getDatabaseInHomeDirectory() + "/" + wallpapername + ".dat");
                return true;
            }
            LGLog.i(TAG, "bitmap is NULL");
            return false;
        }
        LGLog.i(TAG, "When LGHome backup, livewallpaper is not supported");
        File file2 = new File(getDatabaseInHomeDirectory() + "/" + wallpapername + ".dat");
        if (!file2.exists()) {
            return false;
        }
        file2.delete();
        LGLog.i(TAG, "old " + wallpapername + " file is removed");
        return false;
    }

    private boolean backupWallpaperCheckSize(String wallpapername) throws Throwable {
        if (wallpapername == null) {
            wallpapername = "curwallpaper";
        }
        File file = new File(getDatabaseInHomeDirectory());
        if (!file.exists()) {
            file.mkdirs();
        }
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this.mContext);
        if (wallpaperManager.getWallpaperInfo() == null) {
            Bitmap bitmap = ((BitmapDrawable) wallpaperManager.getDrawable()).getBitmap();
            if (bitmap != null) {
                SaveBitmapToFileCache(bitmap, getDatabaseInHomeDirectory() + "/" + wallpapername + ".dat");
                return true;
            }
            LGLog.i(TAG, "bitmap is NULL");
            return false;
        }
        LGLog.i(TAG, "When LGHome backup, livewallpaper is not supported");
        File file2 = new File(getDatabaseInHomeDirectory() + "/" + wallpapername + ".dat");
        if (!file2.exists()) {
            return false;
        }
        file2.delete();
        LGLog.i(TAG, "old " + wallpapername + " file is removed");
        return false;
    }

    private boolean backupSwivelWallpaper(String swivelWallpapername) throws Throwable {
        if (swivelWallpapername == null) {
            swivelWallpapername = "swivel_wallpaper";
        }
        File file = new File(getDatabaseInHomeDirectory());
        if (!file.exists()) {
            file.mkdirs();
        }
        if (WallpaperManager.getInstance(this.mContext).getWallpaperInfo() == null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getSwivelWallpaperDrawable(this.mContext);
            if (bitmapDrawable != null) {
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null) {
                    SaveBitmapToFileCache(bitmap, getDatabaseInHomeDirectory() + "/" + swivelWallpapername + ".dat");
                    return true;
                }
                LGLog.i(TAG, "bitmap is NULL");
                return false;
            }
            LGLog.i(TAG, "wallpaperDrawable is NULL");
            return false;
        }
        LGLog.i(TAG, "When LGHome backup, livewallpaper is not supported");
        File file2 = new File(getDatabaseInHomeDirectory() + "/" + swivelWallpapername + ".dat");
        if (!file2.exists()) {
            return false;
        }
        file2.delete();
        LGLog.i(TAG, "old " + swivelWallpapername + " file is removed");
        return false;
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:23:0x0040 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:42:0x0062 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:61:0x000b */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:8:0x0022 */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0054 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:64:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v10, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r0v13 */
    /* JADX WARN: Type inference failed for: r0v14 */
    /* JADX WARN: Type inference failed for: r0v15 */
    /* JADX WARN: Type inference failed for: r0v16 */
    /* JADX WARN: Type inference failed for: r0v17 */
    /* JADX WARN: Type inference failed for: r0v18 */
    /* JADX WARN: Type inference failed for: r0v19 */
    /* JADX WARN: Type inference failed for: r0v4, types: [android.graphics.drawable.Drawable] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v7 */
    /* JADX WARN: Type inference failed for: r0v8 */
    /* JADX WARN: Type inference failed for: r0v9 */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r5v0, types: [android.content.Context] */
    /* JADX WARN: Type inference failed for: r5v10 */
    /* JADX WARN: Type inference failed for: r5v11 */
    /* JADX WARN: Type inference failed for: r5v14, types: [java.io.FileInputStream, java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r5v2 */
    /* JADX WARN: Type inference failed for: r5v3 */
    /* JADX WARN: Type inference failed for: r5v4, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r5v6, types: [java.io.FileInputStream] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.drawable.Drawable getSwivelWallpaperDrawable(android.content.Context r5) throws java.lang.Throwable {
        /*
            r0 = 0
            android.app.WallpaperManager r5 = android.app.WallpaperManager.getInstance(r5)     // Catch: java.lang.Throwable -> L45 java.lang.SecurityException -> L4a java.lang.IllegalArgumentException -> L4c
            r1 = 16
            android.os.ParcelFileDescriptor r2 = r5.getWallpaperFile(r1)     // Catch: java.lang.Throwable -> L45 java.lang.SecurityException -> L4a java.lang.IllegalArgumentException -> L4c
            if (r2 == 0) goto L26
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L3b java.lang.SecurityException -> L40 java.lang.IllegalArgumentException -> L42
            java.io.FileDescriptor r1 = r2.getFileDescriptor()     // Catch: java.lang.Throwable -> L3b java.lang.SecurityException -> L40 java.lang.IllegalArgumentException -> L42
            r5.<init>(r1)     // Catch: java.lang.Throwable -> L3b java.lang.SecurityException -> L40 java.lang.IllegalArgumentException -> L42
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeStream(r5)     // Catch: java.lang.SecurityException -> L22 java.lang.IllegalArgumentException -> L24 java.lang.Throwable -> L62
            android.graphics.drawable.BitmapDrawable r3 = new android.graphics.drawable.BitmapDrawable     // Catch: java.lang.SecurityException -> L22 java.lang.IllegalArgumentException -> L24 java.lang.Throwable -> L62
            r3.<init>(r1)     // Catch: java.lang.SecurityException -> L22 java.lang.IllegalArgumentException -> L24 java.lang.Throwable -> L62
            r0 = r5
            r5 = r3
            goto L2a
        L22:
            r1 = move-exception
            goto L4f
        L24:
            r1 = move-exception
            goto L4f
        L26:
            android.graphics.drawable.Drawable r5 = r5.getBuiltInDrawable(r1)     // Catch: java.lang.Throwable -> L3b java.lang.SecurityException -> L40 java.lang.IllegalArgumentException -> L42
        L2a:
            if (r2 == 0) goto L39
            r2.close()     // Catch: java.io.IOException -> L35
            if (r0 == 0) goto L39
            r0.close()     // Catch: java.io.IOException -> L35
            goto L39
        L35:
            r0 = move-exception
            r0.printStackTrace()
        L39:
            r0 = r5
            goto L61
        L3b:
            r5 = move-exception
            r4 = r0
            r0 = r5
            r5 = r4
            goto L63
        L40:
            r1 = move-exception
            goto L43
        L42:
            r1 = move-exception
        L43:
            r5 = r0
            goto L4f
        L45:
            r5 = move-exception
            r2 = r0
            r0 = r5
            r5 = r2
            goto L63
        L4a:
            r1 = move-exception
            goto L4d
        L4c:
            r1 = move-exception
        L4d:
            r5 = r0
            r2 = r5
        L4f:
            r1.printStackTrace()     // Catch: java.lang.Throwable -> L62
            if (r2 == 0) goto L61
            r2.close()     // Catch: java.io.IOException -> L5d
            if (r5 == 0) goto L61
            r5.close()     // Catch: java.io.IOException -> L5d
            goto L61
        L5d:
            r5 = move-exception
            r5.printStackTrace()
        L61:
            return r0
        L62:
            r0 = move-exception
        L63:
            if (r2 == 0) goto L72
            r2.close()     // Catch: java.io.IOException -> L6e
            if (r5 == 0) goto L72
            r5.close()     // Catch: java.io.IOException -> L6e
            goto L72
        L6e:
            r5 = move-exception
            r5.printStackTrace()
        L72:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.backuprestore.BackupRestoreImpl.getSwivelWallpaperDrawable(android.content.Context):android.graphics.drawable.Drawable");
    }

    private boolean setWallpaperBitmap(String imagePath, boolean isSwivel) {
        if (!new File(imagePath).exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(imagePath, options);
        if (bitmapDecodeFile == null) {
            return false;
        }
        new SetWallpaperTask(this.mContext, isSwivel).execute(bitmapDecodeFile);
        return true;
    }

    private boolean restoreWallpaper(File wallpaperFile) {
        if (wallpaperFile == null) {
            wallpaperFile = new File(getDatabaseInHomeDirectory() + "/curwallpaper.dat");
        }
        if (wallpaperFile != null) {
            if (wallpaperFile.exists()) {
                return setWallpaperBitmap(wallpaperFile.getAbsolutePath(), false);
            }
            LGLog.i(TAG, "Faild to restore wallpaper: doesn't exist");
        }
        return false;
    }

    private boolean restoreSwivelWallpaper(File wallpaperFile) {
        if (wallpaperFile == null) {
            wallpaperFile = new File(getDatabaseInHomeDirectory() + "/curwallpaper.dat");
        }
        if (wallpaperFile == null) {
            return false;
        }
        if (wallpaperFile.exists()) {
            return setWallpaperBitmap(wallpaperFile.getAbsolutePath(), true);
        }
        LGLog.i(TAG, "Faild to restore wallpaper: doesn't exist");
        return false;
    }

    public boolean getBackupCancel() {
        return this.mIsBackupCancel;
    }

    public void setBackupCancel(boolean backupCancel) {
        this.mIsBackupCancel = backupCancel;
    }

    class ImportException extends Exception {
        private static final long serialVersionUID = 4237665591482481579L;

        public ImportException(String message, Throwable e) {
            super(message, e);
        }
    }

    private void importFavoritesTable(SQLiteDatabase db) throws ImportException {
        LGLog.d(TAG, "Import favorite table");
        reallocateAppWidgetIds(db);
        setAsRestoredForApplications(db);
        filterShortcuts(db);
        filterDeepShortcut(db);
    }

    private void filterShortcuts(SQLiteDatabase db) throws ImportException {
        filterByAction(db);
    }

    private boolean isFilteringAction(String action) {
        for (String str : this.mContext.getResources().getStringArray(R.array.config_restore_filtering_actions)) {
            if (str.equals(action)) {
                return true;
            }
        }
        return false;
    }

    private void filterByAction(SQLiteDatabase db) throws ImportException {
        String[] strArr = {"_id", "title", LauncherSettings.BaseLauncherColumns.INTENT};
        db.beginTransaction();
        try {
            try {
                Cursor cursorQuery = db.query("favorites", strArr, "itemType=1", null, null, null, null);
                while (cursorQuery.moveToNext()) {
                    try {
                        int i = cursorQuery.getInt(0);
                        String string = cursorQuery.getString(1);
                        String string2 = cursorQuery.getString(2);
                        if (string2 != null) {
                            try {
                                if (isFilteringAction(Intent.parseUri(string2, 0).getAction())) {
                                    db.delete("favorites", "_id=" + i, null);
                                    LGLog.d(TAG, "Filter shortcuts: " + string);
                                }
                            } catch (URISyntaxException unused) {
                            }
                        }
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
                db.setTransactionSuccessful();
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (RuntimeException e) {
                throw new ImportException(e.getMessage(), e);
            }
        } finally {
            db.endTransaction();
        }
    }

    private void filterDeepShortcut(SQLiteDatabase db) throws ImportException {
        db.delete("favorites", "itemType=6", null);
    }

    private void setAsRestoredForApplications(SQLiteDatabase db) {
        try {
            db.execSQL("UPDATE favorites SET restored=1 WHERE itemType=0");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reallocateAppWidgetIds(SQLiteDatabase db) throws ImportException {
        LGLog.d(TAG, "Reallocate appwidget ids");
        String[] strArr = {"_id", LauncherSettings.Favorites.APPWIDGET_PROVIDER, "appWidgetId"};
        db.beginTransaction();
        try {
            try {
                Cursor cursorQuery = db.query("favorites", strArr, "itemType=4", null, null, null, null);
                while (cursorQuery.moveToNext()) {
                    try {
                        int i = cursorQuery.getInt(0);
                        String string = cursorQuery.getString(1);
                        int i2 = cursorQuery.getInt(2);
                        int iAllocateAppWidgetId = allocateAppWidgetId(string);
                        LGLog.i(TAG, String.format(" # [%d -> %d] %s", Integer.valueOf(i2), Integer.valueOf(iAllocateAppWidgetId), string));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("appWidgetId", Integer.valueOf(iAllocateAppWidgetId));
                        db.update("favorites", contentValues, "_id=" + i, null);
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
                db.setTransactionSuccessful();
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (RuntimeException e) {
                throw new ImportException(e.getMessage(), e);
            }
        } finally {
            db.endTransaction();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0067  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int allocateAppWidgetId(java.lang.String r6) {
        /*
            r5 = this;
            r0 = -1
            if (r6 != 0) goto L4
            return r0
        L4:
            android.content.ComponentName r6 = android.content.ComponentName.unflattenFromString(r6)
            boolean r1 = r5.hasPackage(r6)
            java.lang.String r2 = "LGBackupRestore"
            if (r1 == 0) goto L75
            android.appwidget.AppWidgetHost r1 = r5.mAppWidgetHost     // Catch: java.lang.SecurityException -> L6c
            int r1 = r1.allocateAppWidgetId()     // Catch: java.lang.SecurityException -> L6c
            android.appwidget.AppWidgetManager r3 = r5.mAppWidgetManager     // Catch: java.lang.SecurityException -> L69
            r3.bindAppWidgetId(r1, r6)     // Catch: java.lang.SecurityException -> L69
            android.appwidget.AppWidgetManager r3 = r5.mAppWidgetManager     // Catch: java.lang.SecurityException -> L69
            android.appwidget.AppWidgetProviderInfo r3 = r3.getAppWidgetInfo(r1)     // Catch: java.lang.SecurityException -> L69
            android.content.ComponentName r4 = r3.configure     // Catch: java.lang.SecurityException -> L69
            if (r4 == 0) goto L67
            int r4 = r3.previewImage     // Catch: java.lang.SecurityException -> L69
            if (r4 != 0) goto L49
            boolean r4 = r5.isSystem(r6)     // Catch: java.lang.SecurityException -> L69
            if (r4 != 0) goto L49
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.SecurityException -> L69
            r3.<init>()     // Catch: java.lang.SecurityException -> L69
            java.lang.String r4 = "Has not preview image: "
            r3.append(r4)     // Catch: java.lang.SecurityException -> L69
            r3.append(r6)     // Catch: java.lang.SecurityException -> L69
            java.lang.String r6 = r3.toString()     // Catch: java.lang.SecurityException -> L69
            com.lge.launcher3.util.LGLog.d(r2, r6)     // Catch: java.lang.SecurityException -> L69
            android.appwidget.AppWidgetHost r6 = r5.mAppWidgetHost     // Catch: java.lang.SecurityException -> L69
            r6.deleteAppWidgetId(r1)     // Catch: java.lang.SecurityException -> L69
            goto L89
        L49:
            int r3 = r3.initialLayout     // Catch: java.lang.SecurityException -> L69
            if (r3 != 0) goto L67
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.SecurityException -> L69
            r3.<init>()     // Catch: java.lang.SecurityException -> L69
            java.lang.String r4 = "Has not initial layout: "
            r3.append(r4)     // Catch: java.lang.SecurityException -> L69
            r3.append(r6)     // Catch: java.lang.SecurityException -> L69
            java.lang.String r6 = r3.toString()     // Catch: java.lang.SecurityException -> L69
            com.lge.launcher3.util.LGLog.d(r2, r6)     // Catch: java.lang.SecurityException -> L69
            android.appwidget.AppWidgetHost r6 = r5.mAppWidgetHost     // Catch: java.lang.SecurityException -> L69
            r6.deleteAppWidgetId(r1)     // Catch: java.lang.SecurityException -> L69
            goto L89
        L67:
            r0 = r1
            goto L89
        L69:
            r6 = move-exception
            r0 = r1
            goto L6d
        L6c:
            r6 = move-exception
        L6d:
            java.lang.String r6 = r6.getMessage()
            com.lge.launcher3.util.LGLog.e(r2, r6)
            goto L89
        L75:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Cannot find provider: "
            r1.append(r3)
            r1.append(r6)
            java.lang.String r6 = r1.toString()
            com.lge.launcher3.util.LGLog.d(r2, r6)
        L89:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.backuprestore.BackupRestoreImpl.allocateAppWidgetId(java.lang.String):int");
    }

    private boolean isSystem(ComponentName cn) {
        try {
            return (this.mContext.getPackageManager().getApplicationInfo(cn.getPackageName(), 0).flags & 1) == 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private boolean hasPackage(ComponentName provider) {
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            packageManager.getReceiverInfo(provider, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            try {
                packageManager.getReceiverInfo(new ComponentName(packageManager.currentToCanonicalPackageNames(new String[]{provider.getPackageName()})[0], provider.getClassName()), 0);
            } catch (Exception unused2) {
                return false;
            }
        }
        return true;
    }

    private void migrateHome4MenuDB(SQLiteDatabase db) {
        int version = db.getVersion();
        if (version <= 5) {
            LGLog.i(TAG, "migrateHome4MenuDB(): version " + version + " is not Supported.");
            AllAppsDBProvider.dropAllAppsDBTables(db);
            AllAppsDBProvider.createAllAppsTables(db);
            return;
        }
        if (version == 6) {
            LGLog.i(TAG, "migrateHome4MenuDB(): migrate oldVersion = " + version);
            AllAppsDBProvider.addProfileColumn(this.mContext, db);
            version = 7;
        }
        if (version == 7) {
            LGLog.i(TAG, "migrateHome4MenuDB(): migrate oldVersion = " + version);
            AllAppsDBProvider.addRankOptionsColumn(db);
            AllAppsDBProvider.resetFolderColor(db);
        }
    }

    private class SetWallpaperTask extends AsyncTask<Bitmap, Void, Boolean> {
        Context mContext;
        boolean mIsSwivel;

        SetWallpaperTask(Context context, boolean isSwivel) {
            this.mContext = context;
            this.mIsSwivel = isSwivel;
        }

        /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Bitmap... params) {
            LGLog.i(BackupRestoreImpl.TAG, "set restored wallpaper");
            boolean z = false;
            Bitmap bitmap = params[0];
            try {
                WallpaperManager.getInstance(this.mContext).setBitmap(bitmap, null, true, this.mIsSwivel ? 16 : 1);
                bitmap.recycle();
                z = true;
            } catch (IOException unused) {
                bitmap.recycle();
            } catch (Throwable th) {
                bitmap.recycle();
                throw th;
            }
            return Boolean.valueOf(z);
        }

        /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            LGLog.i(BackupRestoreImpl.TAG, "SetWallpaperTask : " + aBoolean);
        }
    }
}
