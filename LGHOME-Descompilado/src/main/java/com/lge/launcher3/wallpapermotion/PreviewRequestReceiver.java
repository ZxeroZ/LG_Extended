package com.lge.launcher3.wallpapermotion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.FileProvider;
import com.lge.launcher3.util.LGLog;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class PreviewRequestReceiver extends BroadcastReceiver {
    private static final String TAG = "PreviewRequestReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String str = TAG;
        LGLog.i(str, "onReceive() : " + intent.getAction());
        File currentFile = WallpaperMotionUtils.getCurrentFile(context);
        if (currentFile == null || !currentFile.exists()) {
            LGLog.i(str, "Not found : " + currentFile);
            return;
        }
        WallpaperMotionUtils.sendPreviewImage(context, FileProvider.getUriForFile(context, WallpaperMotionUtils.LAUNCHER_FILEPROVIDER, currentFile));
    }
}
