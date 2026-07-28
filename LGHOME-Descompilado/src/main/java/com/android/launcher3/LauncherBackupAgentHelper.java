package com.android.launcher3;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.android.launcher3.LauncherSettings;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class LauncherBackupAgentHelper extends BackupAgentHelper {
    static final boolean DEBUG = false;
    private static final String LAUNCHER_DATA_PREFIX = "L";
    private static final String TAG = "LauncherBackupAgentHelper";
    static final boolean VERBOSE = false;
    private static BackupManager sBackupManager;
    private LauncherBackupHelper mHelper;

    public static void dataChanged(Context context) {
        if (sBackupManager == null) {
            sBackupManager = new BackupManager(context);
        }
        sBackupManager.dataChanged();
    }

    @Override // android.app.backup.BackupAgent
    public void onCreate() {
        super.onCreate();
        LauncherBackupHelper launcherBackupHelper = new LauncherBackupHelper(this);
        this.mHelper = launcherBackupHelper;
        addHelper(LAUNCHER_DATA_PREFIX, launcherBackupHelper);
    }

    @Override // android.app.backup.BackupAgentHelper, android.app.backup.BackupAgent
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        boolean zMoveToNext;
        if (!Utilities.isLmpOrAbove()) {
            Log.i(TAG, "You shall not pass!!!");
            Log.d(TAG, "Restore is only supported on devices running Lollipop and above.");
            return;
        }
        ContentResolver contentResolver = getContentResolver();
        LauncherSettings.Settings.call(contentResolver, LauncherSettings.Settings.METHOD_CREATE_EMPTY_DB);
        try {
            super.onRestore(data, appVersionCode, newState);
            Cursor cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI, null, null, null, null);
            zMoveToNext = cursorQuery.moveToNext();
            cursorQuery.close();
        } catch (Exception e) {
            Log.e(TAG, "Restore failed", e);
            zMoveToNext = false;
        }
        if (zMoveToNext && this.mHelper.restoreSuccessful) {
            LauncherSettings.Settings.call(contentResolver, LauncherSettings.Settings.METHOD_CLEAR_EMPTY_DB_FLAG);
            LauncherClings.synchonouslyMarkFirstRunClingDismissed(this);
            if (this.mHelper.restoredBackupVersion <= 3) {
                LauncherSettings.Settings.call(contentResolver, LauncherSettings.Settings.METHOD_UPDATE_FOLDER_ITEMS_RANK);
                LauncherSettings.Settings.call(contentResolver, LauncherSettings.Settings.METHOD_CONVERT_SHORTCUTS_TO_LAUNCHER_ACTIVITIES);
                return;
            }
            return;
        }
        LauncherSettings.Settings.call(contentResolver, LauncherSettings.Settings.METHOD_CREATE_EMPTY_DB);
    }
}
