package com.lge.launcher3.smartbulletin.widgetlibrary;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import com.lge.launcher3.smartbulletin.log.SBLog;

/* JADX INFO: loaded from: classes.dex */
public class MyAppWidgetHost extends AppWidgetHost {
    private static final String TAG = "MyAppWidgetHost";
    private OnProvidersChangedListener mProvidersChangedListener;

    public interface OnProvidersChangedListener {
        void onProvidersChanged();
    }

    public MyAppWidgetHost(Context context, int hostId) {
        super(context, hostId);
        this.mProvidersChangedListener = null;
    }

    @Override // android.appwidget.AppWidgetHost
    protected AppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        return new MyAppWidgetHostView(context);
    }

    @Override // android.appwidget.AppWidgetHost
    protected void onProviderChanged(int appWidgetId, AppWidgetProviderInfo appWidget) {
        SBLog.i(TAG, "onProviderChanged id:" + appWidgetId + " info:" + appWidget);
        super.onProviderChanged(appWidgetId, appWidget);
    }

    @Override // android.appwidget.AppWidgetHost
    protected void onProvidersChanged() {
        SBLog.i(TAG, "onProvidersChanged");
        super.onProvidersChanged();
        OnProvidersChangedListener onProvidersChangedListener = this.mProvidersChangedListener;
        if (onProvidersChangedListener != null) {
            onProvidersChangedListener.onProvidersChanged();
        }
    }

    public void setOnProvidersChangedListener(OnProvidersChangedListener l) {
        this.mProvidersChangedListener = l;
    }
}
