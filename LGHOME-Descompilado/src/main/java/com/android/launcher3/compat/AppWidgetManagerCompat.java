package com.android.launcher3.compat;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageUserKey;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class AppWidgetManagerCompat {
    private static AppWidgetManagerCompat sInstance;
    private static final Object sInstanceLock = new Object();
    final AppWidgetManager mAppWidgetManager;
    final Context mContext;

    public abstract boolean bindAppWidgetIdIfAllowed(int appWidgetId, AppWidgetProviderInfo info, Bundle options);

    public abstract LauncherAppWidgetProviderInfo findProvider(ComponentName provider, UserHandle user);

    public abstract List<AppWidgetProviderInfo> getAllProviders();

    public abstract List<AppWidgetProviderInfo> getAllProviders(PackageUserKey packageUser);

    public abstract HashMap<ComponentKey, AppWidgetProviderInfo> getAllProvidersMap();

    public abstract Bitmap getBadgeBitmap(LauncherAppWidgetProviderInfo info, Bitmap bitmap, int imageHeight);

    public abstract UserHandle getUser(LauncherAppWidgetProviderInfo info);

    public abstract Drawable loadIcon(LauncherAppWidgetProviderInfo info, IconCache cache);

    public abstract String loadLabel(LauncherAppWidgetProviderInfo info);

    public abstract Drawable loadPreview(AppWidgetProviderInfo info);

    public abstract void startConfigActivity(AppWidgetProviderInfo info, int widgetId, Activity activity, AppWidgetHost host, int requestCode);

    public static AppWidgetManagerCompat getInstance(Context context) {
        AppWidgetManagerCompat appWidgetManagerCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                if (Utilities.isAtLeastO()) {
                    sInstance = new AppWidgetManagerCompatVO(context.getApplicationContext());
                } else {
                    sInstance = new AppWidgetManagerCompatVL(context.getApplicationContext());
                }
            }
            appWidgetManagerCompat = sInstance;
        }
        return appWidgetManagerCompat;
    }

    AppWidgetManagerCompat(Context context) {
        this.mContext = context;
        this.mAppWidgetManager = AppWidgetManager.getInstance(context);
    }

    public AppWidgetProviderInfo getAppWidgetInfo(int appWidgetId) {
        return this.mAppWidgetManager.getAppWidgetInfo(appWidgetId);
    }

    public LauncherAppWidgetProviderInfo getLauncherAppWidgetInfo(int appWidgetId) {
        AppWidgetProviderInfo appWidgetInfo = getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo == null) {
            return null;
        }
        return LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, appWidgetInfo);
    }
}
