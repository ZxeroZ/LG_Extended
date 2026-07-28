package com.lge.launcher3.adaptive;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class LiveWallpaperColorObserver extends ContentObserver {
    private static final String TAG = "LiveWallpaperColorObserver";
    public static final String URI_NAME = "adaptive_color_live_wallpaper";
    int mBrightness;
    public Context mContext;

    public LiveWallpaperColorObserver(Context context, Handler handler) {
        super(handler);
        this.mBrightness = 0;
        this.mContext = context;
    }

    public void registerObserver(Context context) {
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(URI_NAME), true, this);
        int intForUser = Settings.Secure.getIntForUser(context.getContentResolver(), URI_NAME, 0, -2);
        this.mBrightness = intForUser;
        LGLog.d(TAG, "LiveWallpaperColorObserver registerObserver: mBrightness = " + intForUser);
    }

    public void unregisterObserver(Context context) {
        context.getContentResolver().unregisterContentObserver(this);
        LGLog.d(TAG, "LiveWallpaperColorObserver unregisterObserver");
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        this.mBrightness = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), URI_NAME, 0, -2);
        boolean zIsLiveWallpaperMode = AdaptiveTextUtil.isLiveWallpaperMode(this.mContext);
        if (zIsLiveWallpaperMode) {
            if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                AdaptiveTextUtil.updateAdaptiveColorForSwivel(this.mContext);
            }
            AdaptiveTextUtil.runAdaptiveColorForStatusBar(this.mContext);
        }
        LGLog.i(TAG, "LiveWallpaperColorObserver onChange selfChange - " + selfChange + ", mBrightness = " + this.mBrightness + ", isLiveWallpaper = " + zIsLiveWallpaperMode);
    }
}
