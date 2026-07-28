package com.android.launcher3.model.data;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import com.android.launcher3.AppWidgetResizeFrame;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.util.ContentWriter;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.wallpaperblur.WidgetBlurManager;

/* JADX INFO: loaded from: classes.dex */
public class LauncherAppWidgetInfo extends ItemInfo {
    public static final int CUSTOM_WIDGET_ID = -100;
    public static final int FLAG_DIRECT_CONFIG = 32;
    public static final int FLAG_ID_ALLOCATED = 16;
    public static final int FLAG_ID_NOT_VALID = 1;
    public static final int FLAG_PROVIDER_NOT_READY = 2;
    public static final int FLAG_RESTORE_STARTED = 8;
    public static final int FLAG_RESTORE_UPDATECENTER = 64;
    public static final int FLAG_UI_NOT_READY = 4;
    public static final int NO_ID = -1;
    public static final int RESTORE_COMPLETED = 0;
    public int appWidgetId;
    public Intent bindOptions;
    private boolean mHasNotifiedInitialWidgetSizeChanged;
    public ComponentName providerName;
    public int restoreStatus;
    public int minWidth = -1;
    public int minHeight = -1;
    public int installProgress = -1;
    public AppWidgetHostView hostView = null;

    public LauncherAppWidgetInfo(int appWidgetId, ComponentName providerName) {
        this.appWidgetId = -1;
        if (appWidgetId == -100) {
            this.itemType = 5;
        } else {
            this.itemType = 4;
        }
        this.appWidgetId = appWidgetId;
        this.providerName = providerName;
        this.spanX = -1;
        this.spanY = -1;
        this.minSpanX = -1;
        this.minSpanY = -1;
        this.user = Process.myUserHandle();
        this.restoreStatus = 0;
    }

    public boolean isCustomWidget() {
        return this.appWidgetId == -100;
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddToDatabase(Context context, ContentValues values) {
        super.onAddToDatabase(context, values);
        values.put("appWidgetId", Integer.valueOf(this.appWidgetId));
        values.put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, this.providerName.flattenToString());
        values.put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(this.restoreStatus));
        Intent intent = this.bindOptions;
        values.put(LauncherSettings.BaseLauncherColumns.INTENT, intent == null ? null : intent.toUri(0));
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddToDatabase(ContentWriter writer) {
        super.onAddToDatabase(writer);
        writer.put("appWidgetId", Integer.valueOf(this.appWidgetId)).put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, this.providerName.flattenToString()).put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(this.restoreStatus)).put(LauncherSettings.BaseLauncherColumns.INTENT, this.bindOptions);
    }

    public void onBindAppWidget(Launcher launcher, AppWidgetHostView hostView) {
        onBindAppWidget(launcher, hostView, LGHomeFeature.Config.FEATURE_USE_EXTRA_WIDGET_INFO.getValue() && (hostView.getAppWidgetInfo() instanceof LauncherAppWidgetProviderInfo) && ((LauncherAppWidgetProviderInfo) hostView.getAppWidgetInfo()).isLgeWidget);
    }

    public void onBindAppWidget(Launcher launcher, AppWidgetHostView hostView, boolean forceUpdate) {
        if (LGHomeFeature.Config.FEATURE_USE_EXTRA_WIDGET_INFO.getValue() && forceUpdate) {
            AppWidgetResizeFrame.updateAppWidgetOption(hostView, launcher, this.spanX, this.spanY, this.cellX, this.cellY, forceUpdate);
        }
        if (!this.mHasNotifiedInitialWidgetSizeChanged) {
            AppWidgetResizeFrame.updateWidgetSizeRanges(hostView, launcher, this.spanX, this.spanY, this.cellX, this.cellY);
            this.mHasNotifiedInitialWidgetSizeChanged = true;
        }
        WidgetBlurManager.getInstance(launcher).enableBlurViewIfAddedInCurrentPage(hostView, true);
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public String toString() {
        return "AppWidget(id=" + Integer.toString(this.appWidgetId) + ")";
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void unbind() {
        super.unbind();
        this.hostView = null;
    }

    public final boolean isWidgetIdValid() {
        return (this.restoreStatus & 1) == 0;
    }

    public final boolean hasRestoreFlag(int flag) {
        return (this.restoreStatus & flag) == flag;
    }

    public final boolean isUpdateCenterRestored() {
        return (this.restoreStatus & 64) != 0;
    }

    public AppWidgetHostView getHostView() {
        return this.hostView;
    }

    public void resetHasNotifiedInitialWidgetSizeChanged() {
        this.mHasNotifiedInitialWidgetSizeChanged = false;
    }
}
