package com.android.launcher3.widget;

import android.appwidget.AppWidgetHostView;
import android.os.Bundle;
import android.os.Parcelable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;

/* JADX INFO: loaded from: classes.dex */
public class PendingAddWidgetInfo extends PendingAddItemInfo {
    public static final int DOWNLOADED_FLAG = 1;
    public static final int UPDATED_SYSTEM_APP_FLAG = 2;
    public Bundle bindOptions = null;
    public AppWidgetHostView boundWidget;
    public int flags;
    public int icon;
    public LauncherAppWidgetProviderInfo info;
    public int minHeight;
    public int minResizeHeight;
    public int minResizeWidth;
    public int minWidth;
    public int previewImage;

    public PendingAddWidgetInfo(Launcher launcher, LauncherAppWidgetProviderInfo i, Parcelable data) {
        this.flags = 0;
        if (i.isCustomWidget) {
            this.itemType = 5;
        } else {
            this.itemType = 4;
        }
        this.info = i;
        this.user = AppWidgetManagerCompat.getInstance(launcher).getUser(i);
        this.componentName = i.provider;
        this.minWidth = i.minWidth;
        this.minHeight = i.minHeight;
        this.minResizeWidth = i.minResizeWidth;
        this.minResizeHeight = i.minResizeHeight;
        this.previewImage = i.previewImage;
        this.icon = i.icon;
        this.spanX = i.getSpanX(launcher);
        this.spanY = i.getSpanY(launcher);
        this.minSpanX = i.getMinSpanX(launcher);
        this.minSpanY = i.getMinSpanY(launcher);
        this.flags = initFlags(i);
    }

    public boolean isCustomWidget() {
        return this.itemType == 5;
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public String toString() {
        return String.format("PendingAddWidgetInfo package=%s, name=%s", this.componentName.getPackageName(), this.componentName.getShortClassName());
    }

    public int initFlags(LauncherAppWidgetProviderInfo info) {
        int i = info.providerInfo.applicationInfo.flags;
        if ((i & 1) == 0) {
            return (i & 128) != 0 ? 3 : 1;
        }
        return 0;
    }

    public WidgetAddFlowHandler getHandler() {
        return new WidgetAddFlowHandler(this.info);
    }
}
