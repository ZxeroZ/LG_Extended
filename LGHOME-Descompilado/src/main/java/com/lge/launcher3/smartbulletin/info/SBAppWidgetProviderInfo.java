package com.lge.launcher3.smartbulletin.info;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;

/* JADX INFO: loaded from: classes.dex */
public class SBAppWidgetProviderInfo implements Comparable {
    public AppWidgetProviderInfo mAppWidgetProviderInfo;
    public ComponentName mCompoentName;
    public int mDatabaseId;
    public boolean mIsEnabled;
    public int mPositionX;
    public int mPostionY;
    public int mSpanX;
    public int mSpanY;
    public int mWidgetId;

    public SBAppWidgetProviderInfo(AppWidgetProviderInfo appWidgetInfo) {
        this.mAppWidgetProviderInfo = appWidgetInfo;
    }

    public ComponentName getComponentName() {
        return this.mAppWidgetProviderInfo.provider;
    }

    @Override // java.lang.Comparable
    public int compareTo(Object another) {
        int i;
        int i2;
        SBAppWidgetProviderInfo sBAppWidgetProviderInfo = (SBAppWidgetProviderInfo) another;
        boolean z = this.mIsEnabled;
        if (z && !sBAppWidgetProviderInfo.mIsEnabled) {
            return -1;
        }
        if ((z || !sBAppWidgetProviderInfo.mIsEnabled) && (i = this.mPostionY) <= (i2 = sBAppWidgetProviderInfo.mPostionY)) {
            return i < i2 ? -1 : 0;
        }
        return 1;
    }

    public boolean isSameComponent(ComponentName componentName) {
        try {
            return componentName.equals(this.mAppWidgetProviderInfo.provider);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasSameProvider(SBAppWidgetProviderInfo o) {
        try {
            return this.mAppWidgetProviderInfo.provider.flattenToString().equals(o.mAppWidgetProviderInfo.provider.flattenToString());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
