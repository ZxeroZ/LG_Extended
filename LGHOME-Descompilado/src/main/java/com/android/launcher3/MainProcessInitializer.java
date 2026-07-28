package com.android.launcher3;

import android.content.Context;
import com.android.launcher3.graphics.IconShape;
import com.android.launcher3.logging.FileLog;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class MainProcessInitializer {
    public static void initialize(Context context) {
        ((MainProcessInitializer) Utilities.getOverrideObject(MainProcessInitializer.class, context, R.string.main_process_initializer_class)).init(context);
    }

    protected void init(Context context) {
        FileLog.setDir(context.getApplicationContext().getFilesDir());
        SessionCommitReceiver.applyDefaultUserPrefs(context);
        IconShape.init(context);
    }
}
