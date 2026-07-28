package com.lge.lgewidgetlib;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import com.lge.lgewidgetlib.extview.IExtViewHostAdapter;
import com.lge.lgewidgetlib.extview.LgeAppWidgetExtViewHost;
import com.lge.lgewidgetlib.webview.LgeAppWidgetWebViewManager;

/* JADX INFO: loaded from: classes2.dex */
public class LgeAppWidgetHostView extends AppWidgetHostView {
    private static final String TAG = "LgeAppWidgetHostView";
    static LayoutInflater.Filter sInflaterFilter = new LayoutInflater.Filter() { // from class: com.lge.lgewidgetlib.LgeAppWidgetHostView.1
        @Override // android.view.LayoutInflater.Filter
        public boolean onLoadClass(Class clazz) {
            if (clazz.isAnnotationPresent(RemoteViews.RemoteView.class)) {
                return true;
            }
            return LgeRemoteViews.checkAnnotationForCustomView(clazz);
        }
    };
    private Context mContext;
    private LgeAppWidgetExtViewHost mExtViewHost;
    private IExtViewHostAdapter mExtViewHostAdapter;
    private Context mRemoteContextForCustomView;
    private LgeAppWidgetWebViewManager mWebViewManager;

    public LgeAppWidgetHostView(Context context) {
        super(context);
        this.mContext = context;
    }

    public LgeAppWidgetHostView(Context context, RemoteViews.InteractionHandler handler) {
        super(context, handler);
        this.mContext = context;
    }

    public LgeAppWidgetHostView(Context context, int animationIn, int animationOut) {
        super(context, animationIn, animationOut);
    }

    public void setExtViewHostAdapter(IExtViewHostAdapter extViewHostAdapter) {
        this.mExtViewHostAdapter = extViewHostAdapter;
    }

    private Context getRemoteContextForCustomView(RemoteViews views) {
        String packageName;
        try {
            return this.mContext.createApplicationContext(getAppWidgetInfo().providerInfo.applicationInfo, 3);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchFieldError unused) {
            if (views != null) {
                packageName = views.getPackage();
            } else {
                packageName = getAppWidgetInfo().provider.getPackageName();
            }
            if (packageName == null) {
                return this.mContext;
            }
            try {
                return this.mContext.createPackageContextAsUser(packageName, 3, (UserHandle) LgeReflectionUtil.getPrivateField(AppWidgetHostView.class, this, "mUser"));
            } catch (PackageManager.NameNotFoundException unused2) {
                Log.e(TAG, "Package name " + packageName + " not found");
                return this.mContext;
            }
        }
    }

    @Override // android.appwidget.AppWidgetHostView
    public void updateAppWidget(RemoteViews remoteViews) {
        View view;
        if (isWidgetUpdateSkippable()) {
            return;
        }
        if (remoteViews != null) {
            if (isLgeWidget()) {
                LgeRemoteViews lgeRemoteViewsFromRemoteViews = LgeRemoteViews.getLgeRemoteViewsFromRemoteViews(remoteViews);
                this.mRemoteContextForCustomView = getRemoteContextForCustomView(lgeRemoteViewsFromRemoteViews);
                super.updateAppWidget(lgeRemoteViewsFromRemoteViews);
                if (isWeatherWidget() && (view = (View) LgeReflectionUtil.getPrivateField(AppWidgetHostView.class, this, "mView")) != null) {
                    Log.d(TAG, "It's weather widget, invalidate view");
                    view.invalidate();
                }
                try {
                    if (getAppWidgetInfo().provider.getPackageName().equals("com.lge.concierge")) {
                        initExtManager();
                        return;
                    }
                    return;
                } catch (ClassCastException unused) {
                    WLog.d(TAG, "ClassCastException package = " + remoteViews.getPackage());
                    try {
                        WLog.d(TAG, "ClassCastException version = " + this.mContext.getPackageManager().getPackageInfo(remoteViews.getPackage(), 0).versionName);
                        WLog.d(TAG, "ClassCastException getAppWidgetInfo().provider = " + getAppWidgetInfo().provider.getClassName());
                        return;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            super.updateAppWidget(remoteViews);
            return;
        }
        if (isLgeWidget()) {
            cancelWidgetExt();
        }
        super.updateAppWidget(remoteViews);
        if (isLgeWidget()) {
            notifyWidgetReset();
        }
    }

    private void initWebViewManager() {
        LgeAppWidgetWebViewManager lgeAppWidgetWebViewManager = new LgeAppWidgetWebViewManager(this.mContext, this);
        this.mWebViewManager = lgeAppWidgetWebViewManager;
        lgeAppWidgetWebViewManager.init();
    }

    private void initExtManager() {
        this.mExtViewHost = new LgeAppWidgetExtViewHost(this, this.mExtViewHostAdapter);
        WLog.i(TAG, "mExtViewHost created");
        if (!this.mExtViewHost.updateExtViewList()) {
            this.mExtViewHost = null;
            WLog.e(TAG, "Cannot setExtViewHost, mExtViewHost --> null");
        } else {
            initWebViewManager();
        }
    }

    /* JADX DEBUG: Method merged with bridge method: generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams; */
    @Override // android.appwidget.AppWidgetHostView, android.widget.FrameLayout, android.view.ViewGroup
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        if (isLgeWidget()) {
            Context context = this.mRemoteContextForCustomView;
            if (context == null) {
                context = this.mContext;
            }
            return new FrameLayout.LayoutParams(context, attrs);
        }
        return super.generateLayoutParams(attrs);
    }

    @Override // android.appwidget.AppWidgetHostView
    protected View getDefaultView() {
        View viewInflate;
        int i;
        RuntimeException runtimeException = null;
        try {
            if (getAppWidgetInfo() != null) {
                if (!isLgeWidget()) {
                    return super.getDefaultView();
                }
                Context remoteContextForCustomView = getRemoteContextForCustomView(null);
                this.mRemoteContextForCustomView = remoteContextForCustomView;
                LayoutInflater layoutInflaterCloneInContext = ((LayoutInflater) remoteContextForCustomView.getSystemService("layout_inflater")).cloneInContext(this.mRemoteContextForCustomView);
                layoutInflaterCloneInContext.setFactory(new CustomLayoutInflaterFactory());
                layoutInflaterCloneInContext.setFilter(sInflaterFilter);
                Bundle appWidgetOptions = AppWidgetManager.getInstance(this.mContext).getAppWidgetOptions(getAppWidgetId());
                int i2 = getAppWidgetInfo().initialLayout;
                if (appWidgetOptions.containsKey("appWidgetCategory") && appWidgetOptions.getInt("appWidgetCategory") == 2 && (i = getAppWidgetInfo().initialKeyguardLayout) != 0) {
                    i2 = i;
                }
                viewInflate = layoutInflaterCloneInContext.inflate(i2, (ViewGroup) this, false);
            } else {
                Log.w(TAG, "can't inflate defaultView because mInfo is missing");
                viewInflate = null;
            }
        } catch (RuntimeException e) {
            runtimeException = e;
            viewInflate = null;
        }
        if (runtimeException != null) {
            Log.w(TAG, "Error inflating AppWidget " + getAppWidgetInfo() + ": " + runtimeException.toString());
        }
        if (viewInflate != null) {
            return viewInflate;
        }
        WLog.d(TAG, "getDefaultView couldn't find any view, so inflating error");
        return getErrorView();
    }

    boolean isLgeWidget() {
        if (getAppWidgetInfo() != null) {
            return LgeWidgetContext.isLGEAppWidgetPackage(getAppWidgetInfo().provider.getPackageName());
        }
        return false;
    }

    boolean isWeatherWidget() {
        if (getAppWidgetInfo() != null) {
            return LgeWidgetContext.isLGEWeatherWidgetPackage(getAppWidgetInfo().provider.getPackageName());
        }
        return false;
    }

    void cancelWidgetExt() {
        LgeAppWidgetExtViewHost lgeAppWidgetExtViewHost;
        if (isLgeWidget() && (lgeAppWidgetExtViewHost = this.mExtViewHost) != null && lgeAppWidgetExtViewHost.isExpanded()) {
            this.mExtViewHost.cancelExtView();
        }
    }

    boolean isWidgetExtEnabled() {
        LgeAppWidgetExtViewHost lgeAppWidgetExtViewHost;
        if (!isLgeWidget() || (lgeAppWidgetExtViewHost = this.mExtViewHost) == null) {
            return false;
        }
        return lgeAppWidgetExtViewHost.isExpanded();
    }

    void notifyWidgetDeleted() {
        LgeAppWidgetExtViewHost lgeAppWidgetExtViewHost = this.mExtViewHost;
        if (lgeAppWidgetExtViewHost != null) {
            lgeAppWidgetExtViewHost.notifyWidgetDeleted();
        }
    }

    void notifyWidgetReset() {
        LgeAppWidgetExtViewHost lgeAppWidgetExtViewHost = this.mExtViewHost;
        if (lgeAppWidgetExtViewHost != null) {
            lgeAppWidgetExtViewHost.notifyWidgetReset();
        }
    }

    void notifyExtViewAvailable() {
        LgeAppWidgetExtViewHost lgeAppWidgetExtViewHost = this.mExtViewHost;
        if (lgeAppWidgetExtViewHost != null) {
            lgeAppWidgetExtViewHost.notifyExtViewAvailable();
        }
    }

    void notifyWidgetHostDestroyed() {
        LgeAppWidgetExtViewHost lgeAppWidgetExtViewHost = this.mExtViewHost;
        if (lgeAppWidgetExtViewHost != null) {
            lgeAppWidgetExtViewHost.notifyWidgetHostDestroyed();
        }
    }

    void notifyBindingStarted() {
        LgeAppWidgetExtViewHost lgeAppWidgetExtViewHost = this.mExtViewHost;
        if (lgeAppWidgetExtViewHost != null) {
            lgeAppWidgetExtViewHost.notifyBindingStarted();
        }
    }

    boolean isWidgetUpdateSkippable() {
        LgeAppWidgetExtViewHost lgeAppWidgetExtViewHost = this.mExtViewHost;
        if (lgeAppWidgetExtViewHost != null) {
            return lgeAppWidgetExtViewHost.isWidgetUpdateSkippable();
        }
        return false;
    }
}
