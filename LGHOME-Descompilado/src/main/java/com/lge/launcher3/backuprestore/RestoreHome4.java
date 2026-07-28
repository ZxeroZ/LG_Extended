package com.lge.launcher3.backuprestore;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.LauncherSettings;
import com.lge.launcher3.backuprestore.BackupRestoreImpl;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.util.LGLog;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class RestoreHome4 {
    private static final String ACTION_COMPLETE_HOME_BACKUP = "com.lge.bnr.intent.action.COMPLETE_HOME_BACKUP";
    public static final String BACKUP_DB_PAGEMENU_FILENAME = "/menu.db";
    public static final String BACKUP_DB_WORKSPACE_FILENAME = "/workspace.db";
    private static final String HOME_BACKUP_CLASSNAME = "com.lge.launcher2.homesettings.LGBackupRestoreService";
    public static final String HOME_PACKAGE_NAME = "com.lge.launcher2";
    private static final String TAG = "RestoreHome4";
    public static final File HOME_BACKUP_DB_PATH = new File(Environment.getExternalStoragePublicDirectory(""), "Home");
    private static final Uri BACKUP_URI = Uri.parse("content://com.lge.launcher2.homesettings.LGBackupRestoreService/backup");

    public static void requestBackupAndRestoreHome4(Context ctx) {
        try {
            if (ctx.getPackageManager().getPackageInfo("com.lge.launcher2", 0) == null) {
                LGLog.i(TAG, "The information of Home4 can't find");
                return;
            }
            LGLog.i(TAG, "request backup home4");
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.lge.launcher2", HOME_BACKUP_CLASSNAME));
            intent.putExtra("BNR_MODE", 1);
            intent.setData(BACKUP_URI);
            ctx.startService(intent);
            ctx.registerReceiver(new BroadcastReceiver() { // from class: com.lge.launcher3.backuprestore.RestoreHome4.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent2) {
                    LGLog.i(RestoreHome4.TAG, "receiver ACTION_COMPLETE_HOME_BACKUP of Home4");
                    if (!RestoreHome4.isExternalStorageAvail()) {
                        LGLog.i(RestoreHome4.TAG, "Restore of Home4 is failed because isExternalStorage is not available");
                    }
                    RestoreHome4.restore(context);
                    RestoreHome4.deleteDirectory(RestoreHome4.HOME_BACKUP_DB_PATH.getPath());
                    RestoreHome4.disableHome4Package(context);
                    context.sendBroadcast(new Intent(IntentConst.Action.ACTION_KILL_PROCESS.getValue(context)));
                    context.unregisterReceiver(this);
                }
            }, new IntentFilter("com.lge.bnr.intent.action.COMPLETE_HOME_BACKUP"));
        } catch (PackageManager.NameNotFoundException unused) {
            LGLog.i(TAG, "Home4 can't find because NameNotFoundException");
        }
    }

    public static void restore(Context context) {
        BackupRestoreImpl backupRestoreImpl = new BackupRestoreImpl(context);
        try {
            LGLog.i(TAG, "start restore of Home4");
            File file = HOME_BACKUP_DB_PATH;
            backupRestoreImpl.importWorkspaceDb(new File(file + BACKUP_DB_WORKSPACE_FILENAME), LauncherFiles.LAUNCHER_ALLAPPS_DB, true);
            backupRestoreImpl.importMenuDb(new File(file + BACKUP_DB_PAGEMENU_FILENAME));
            LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_RESET_DATABASE_HELPER);
            LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_CLEAR_EMPTY_DB_FLAG);
        } catch (BackupRestoreImpl.ImportException e) {
            LGLog.i(TAG, "failed to restore of Home4, " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void deleteDirectory(String dirPath) {
        File file = new File(dirPath);
        File[] fileArrListFiles = file.listFiles();
        if (fileArrListFiles != null) {
            for (File file2 : fileArrListFiles) {
                if (file2.isDirectory()) {
                    deleteDirectory(file2.getAbsolutePath());
                } else {
                    file2.delete();
                }
            }
        }
        file.delete();
        LGLog.i(TAG, "finish deleteDirectory");
    }

    public static void disableHome4Package(Context ctx) {
        IPackageManager iPackageManagerAsInterface = IPackageManager.Stub.asInterface(ServiceManager.getService(AppNotifierManager.ExtraSpec.USAGE_PACKAGE));
        UserManager userManager = (UserManager) ctx.getSystemService("user");
        if (iPackageManagerAsInterface == null || userManager == null) {
            return;
        }
        try {
            List users = userManager.getUsers();
            PackageInfo packageInfo = iPackageManagerAsInterface.getPackageInfo("com.lge.launcher2", 0L, ((UserInfo) users.get(0)).id);
            if (packageInfo == null) {
                LGLog.i(TAG, "Fail to start disable Home4 because the packageInfo of Home4 is null");
                return;
            }
            boolean z = true;
            if (packageInfo.applicationInfo == null || (packageInfo.applicationInfo.flags & 1) == 0) {
                z = false;
            }
            if (z) {
                LGLog.i(TAG, "Disable Home4");
                Iterator it = users.iterator();
                while (it.hasNext()) {
                    iPackageManagerAsInterface.setApplicationEnabledSetting("com.lge.launcher2", 2, 0, ((UserInfo) it.next()).id, ctx.getPackageName());
                }
                return;
            }
            LGLog.i(TAG, "Uninstall Home4");
            iPackageManagerAsInterface.deletePackageAsUser("com.lge.launcher2", -1, (IPackageDeleteObserver) null, ((UserInfo) users.get(0)).id, 2);
        } catch (RemoteException e) {
            LGLog.i(TAG, "Failed to talk to package manager", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isExternalStorageAvail() {
        return Environment.getExternalStorageState().equals("mounted");
    }
}
