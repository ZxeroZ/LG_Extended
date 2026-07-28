package com.lge.launcher3;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class StylusBlockSettingObserver extends ContentObserver {
    private static final String TAG = "StylusBlockSettingObserver";
    public static final String URI_NAME = "pen_gesture_block";
    public Context mContext;
    int mUseStylus;

    public StylusBlockSettingObserver(Context context, Handler handler) {
        super(handler);
        this.mUseStylus = 0;
        this.mContext = context;
    }

    public void registerObserver(Context context) {
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor(URI_NAME), true, this);
        int intForUser = Settings.System.getIntForUser(context.getContentResolver(), URI_NAME, 0, -2);
        this.mUseStylus = intForUser;
        LGLog.d(TAG, "StylusBlockSettingObserver registerObserver: use stylus = " + intForUser);
    }

    public void unregisterObserver(Context context) {
        context.getContentResolver().unregisterContentObserver(this);
        LGLog.d(TAG, "StylusBlockSettingObserver unregisterObserver");
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), URI_NAME, 0, -2);
        this.mUseStylus = intForUser;
        LGLog.i(TAG, "StylusBlockSettingObserver onChange selfChange - " + selfChange + ", use stylus = " + intForUser);
    }

    public boolean isPenGestureBlock() {
        return this.mUseStylus == 1;
    }
}
