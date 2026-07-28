package com.lge.launcher3.widgettray;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.lge.launcher3.util.PackageUtils;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GroupLauncherAppWidgetProviderInfo extends LauncherAppWidgetProviderInfo {
    private List<Object> mGroupList;

    public GroupLauncherAppWidgetProviderInfo(Context context, List<Object> packageWidgets) {
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) packageWidgets.get(0);
        this.mGroupList = packageWidgets;
        setInfo(context, launcherAppWidgetProviderInfo);
    }

    private void setInfo(Context context, LauncherAppWidgetProviderInfo info) {
        this.provider = info.provider == null ? null : info.provider.clone();
        this.minWidth = info.minWidth;
        this.minHeight = info.minHeight;
        this.minResizeWidth = info.minResizeHeight;
        this.minResizeHeight = info.minResizeHeight;
        this.updatePeriodMillis = info.updatePeriodMillis;
        this.initialLayout = info.initialLayout;
        this.initialKeyguardLayout = info.initialKeyguardLayout;
        this.configure = info.configure == null ? null : info.configure.clone();
        this.icon = info.icon;
        this.autoAdvanceViewId = info.autoAdvanceViewId;
        this.resizeMode = info.resizeMode;
        this.widgetCategory = info.widgetCategory;
        this.providerInfo = info.providerInfo;
        this.spanX = info.getSpanX(context);
        this.spanY = info.getSpanY(context);
        this.minSpanX = info.getMinSpanX(context);
        this.minSpanY = info.getSpanY(context);
        List<Object> list = this.mGroupList;
        if (list != null && list.size() > 1) {
            context.getPackageManager();
            ApplicationInfo applicationInfo = info.providerInfo.applicationInfo;
            this.label = PackageUtils.getApplicationLabel(context, info.provider.getPackageName());
            this.previewImage = applicationInfo.icon;
            return;
        }
        this.label = info.label != null ? info.label.substring(0) : null;
        this.previewImage = info.previewImage;
    }

    public String getLabel() {
        return this.label;
    }

    public List<Object> getGroupList() {
        return this.mGroupList;
    }
}
