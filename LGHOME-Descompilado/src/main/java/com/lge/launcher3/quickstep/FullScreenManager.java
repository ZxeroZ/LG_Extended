package com.lge.launcher3.quickstep;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.os.RemoteException;
import android.view.Display;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public final class FullScreenManager {
    private static final int COMPAT_MODE_ASPECT_LONG = 3;
    private static final String TAG = "FullScreenManager";
    private static volatile FullScreenManager sInstance;
    private static final Object sLock = new Object();
    private IActivityManager mActivityManager = ActivityManagerNative.getDefault();
    private Context mContext;
    private Context mDisplayContext;

    private FullScreenManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mDisplayContext = context;
    }

    public static FullScreenManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new FullScreenManager(context);
        }
        return sInstance;
    }

    public boolean isInFullScreenMode(String topPackageName) {
        if (topPackageName == null) {
            LGLog.d(TAG, "cannot know if isAlreadyFullScreen, topPackageName is null");
            return false;
        }
        if (getCompatMode(topPackageName) == 3) {
            return true;
        }
        return isInFullScreenModeByRatio(topPackageName);
    }

    private boolean isInFullScreenModeByRatio(String topPackageName) {
        float compatRatio = getCompatRatio(topPackageName);
        LGLog.d(TAG, "isInFullScreenModeByRatio: topPackageName : " + topPackageName + " compatRatio : " + compatRatio + " DisplayAspectRatio : " + getDisplayAspectRatio());
        return compatRatio >= getDisplayAspectRatio();
    }

    private int getCompatMode(String topPackageName) {
        if (topPackageName == null) {
            return 0;
        }
        try {
            return this.mActivityManager.getPackageMaxAspectRatio(topPackageName);
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private float getCompatRatio(String topPackageName) {
        if (topPackageName == null) {
            return 0.0f;
        }
        try {
            return this.mActivityManager.getPackageMaxAspectRatioValue(topPackageName);
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    private float getDisplayAspectRatio() {
        Display display = this.mDisplayContext.getDisplay();
        Point point = new Point();
        display.getRealSize(point);
        float f = point.x;
        float f2 = point.y;
        if (f <= 0.0f || f2 <= 0.0f) {
            return 2.1f;
        }
        return f2 > f ? f2 / f : f / f2;
    }
}
