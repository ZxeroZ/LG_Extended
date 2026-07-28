package com.lge.launcher3.backuprestore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class LGBackupBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LGBackupBroadcastReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LGLog.i(TAG, action + " is received START");
        if (LGHomeFeature.Config.FEATURE_USE_BACKUP_RESTORE.getValue() && PackageUtils.isEnableBackupRestore(context)) {
            int i = intent.getExtras().getInt("BNR_MODE");
            if ("com.lge.bnr.intent.action.QUERY".equals(action)) {
                long databaseSize = new LGBackupRestoreAgent(context.getApplicationContext(), intent).getDatabaseSize();
                LGLog.i(TAG, "nBackupSize=" + databaseSize);
                Intent intent2 = new Intent();
                intent2.setAction("com.lge.bnr.REPONSE");
                intent2.putExtra("sPackageName", context.getPackageName());
                if (i == 1) {
                    intent2.putExtra("BackupSize", databaseSize);
                }
                context.sendBroadcast(intent2);
                LGLog.i(TAG, intent2.getAction() + " is sent");
                return;
            }
            if ("com.lge.bnr.intent.action.REQUEST_LGHOME".equals(action)) {
                if (i == 1) {
                    LGBackupRestoreService.getInstance(context).schedule(intent);
                    LGLog.i(TAG, "LGBackupRestoreAgent startBackup");
                    LGUserLog.send(context, LGUserLog.FEATURENAME_BACKUP);
                } else if (i == 2) {
                    LGBackupRestoreService.getInstance(context).schedule(intent);
                    LGLog.i(TAG, "LGBackupRestoreAgent startRestore");
                    LGUserLog.send(context, LGUserLog.FEATURENAME_RESTORE);
                } else if (i == 3) {
                    LGBackupRestoreService.getInstance(context).schedule(intent);
                    LGLog.i(TAG, "LGBackupRestoreAgent startRestoreOld");
                    LGUserLog.send(context, LGUserLog.FEATURENAME_RESTORE);
                }
                LGLog.i(TAG, action + " is received END");
            }
        }
    }
}
