package com.lge.launcher3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Toast;
import com.lge.launcher3.util.LGLog;
import com.lge.os.PropertyUtils;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class MemoryDumpReceiver extends BroadcastReceiver {
    private static final String DUMP_ACTION_INTENT = "com.lge.appmemorymonitoring.action.DUMP_HEAP";
    private static final String TAG = "MemoryDumpReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!PropertyUtils.getInstance().getBoolean(3502, false)) {
            LGLog.d(TAG, "this device is not rooted. so can not proceed!");
            return;
        }
        if (DUMP_ACTION_INTENT.equals(intent.getAction())) {
            try {
                Toast.makeText(context, "Dump LGHome heap...", 1).show();
                File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                if (!externalStoragePublicDirectory.exists()) {
                    externalStoragePublicDirectory.mkdirs();
                }
                Debug.dumpHprofData(externalStoragePublicDirectory.getAbsolutePath() + "/LGHome_" + SystemClock.uptimeMillis() + ".hprof");
            } catch (Exception e) {
                LGLog.e(TAG, "Exception occurred on MemoryDumpReceiver : ", e);
            }
        }
    }
}
