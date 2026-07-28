package com.android.launcher3;

import android.content.Context;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class SecondaryDisplayLauncherManager {
    private static SecondaryDisplayLauncherManager INSTANCE;
    private static final String TAG = SecondaryDisplayLauncher.class.getSimpleName();
    private final Context mContext;
    public ISecondaryDisplayLauncherCallback mSecondaryDisplayLauncherCallback;

    public static SecondaryDisplayLauncherManager getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SecondaryDisplayLauncherManager(context.getApplicationContext());
        }
        return INSTANCE;
    }

    private SecondaryDisplayLauncherManager(Context context) {
        this.mContext = context;
    }

    public void setSecondaryDisplayLauncherCallback(ISecondaryDisplayLauncherCallback callback) {
        this.mSecondaryDisplayLauncherCallback = callback;
        LGLog.d(TAG, "setSecondaryLauncherCallback: " + callback);
    }

    public ISecondaryDisplayLauncherCallback getSecondaryDisplayLauncherCallback() {
        if (this.mSecondaryDisplayLauncherCallback == null) {
            LGLog.i(TAG, "callback is null");
        }
        return this.mSecondaryDisplayLauncherCallback;
    }
}
