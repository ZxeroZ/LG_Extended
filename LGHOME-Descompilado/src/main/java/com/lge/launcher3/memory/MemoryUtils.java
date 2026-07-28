package com.lge.launcher3.memory;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class MemoryUtils {
    public static final String TAG = "MemoryUtils";

    public static boolean hasAvailableFileSystemMemory(Context context, boolean showMemoryFullAlertDialog) {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
        boolean z = availableBlocksLong > 1048576;
        if (!z) {
            LGLog.i(TAG, "Hasn't available file system memory.(availableSize is " + availableBlocksLong + ")");
            if (context != null && showMemoryFullAlertDialog) {
                MemoryFullAlertDialogHandler.getInstance().show(context, 200);
            }
        } else if (availableBlocksLong <= 1048576) {
            LGLog.i(TAG, "availableSize = " + availableBlocksLong);
        }
        return z;
    }
}
