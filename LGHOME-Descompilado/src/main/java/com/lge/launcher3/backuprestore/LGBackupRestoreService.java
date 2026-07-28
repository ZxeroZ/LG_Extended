package com.lge.launcher3.backuprestore;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class LGBackupRestoreService {
    private static final String TAG = "LGBackupRestoreService";
    private static LGBackupRestoreService sInstance;
    private Context mContext;

    public static LGBackupRestoreService getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LGBackupRestoreService(context.getApplicationContext());
        }
        return sInstance;
    }

    private LGBackupRestoreService(Context context) {
        LGLog.i(TAG, "Create a new LGBackupRestoreService instance.");
        this.mContext = context;
    }

    public void schedule(Intent intent) {
        new BackupRestoreTask(this.mContext, intent).execute(new Void[0]);
    }

    class BackupRestoreTask extends AsyncTask<Void, Void, Void> {
        private final Context mContext;
        private final Intent mIntent;

        public BackupRestoreTask(Context context, Intent intent) {
            this.mContext = context;
            this.mIntent = intent;
        }

        /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voids) {
            LGLog.i(LGBackupRestoreService.TAG, "do in background task start");
            int intExtra = this.mIntent.getIntExtra("BNR_MODE", 1);
            LGBackupRestoreAgent lGBackupRestoreAgent = new LGBackupRestoreAgent(this.mContext, this.mIntent);
            ArrayList<String> stringArrayListExtra = this.mIntent.getStringArrayListExtra("OLD_FILELIST");
            if (intExtra == 1) {
                lGBackupRestoreAgent.startBackup();
            } else if (intExtra == 2) {
                lGBackupRestoreAgent.startRestore();
            } else if (intExtra == 3 && stringArrayListExtra != null) {
                lGBackupRestoreAgent.startRestoreOld(stringArrayListExtra);
            }
            LGLog.i(LGBackupRestoreService.TAG, "do in background task end");
            return null;
        }
    }
}
