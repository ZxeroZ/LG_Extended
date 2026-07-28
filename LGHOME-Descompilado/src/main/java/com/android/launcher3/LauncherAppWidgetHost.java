package com.android.launcher3;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.os.TransactionTooLargeException;
import android.view.LayoutInflater;
import android.view.View;
import com.lge.launcher3.concierge.ConciergeBoardMngr;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGLog;
import com.lge.lgewidgetlib.LgeAppWidgetHost;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class LauncherAppWidgetHost extends LgeAppWidgetHost {
    private static final int FLAG_LISTENING = 1;
    private static final int FLAG_LISTEN_IF_RESUMED = 4;
    private static final int FLAG_RESUMED = 2;
    private static final String TAG = "LauncherAppWidgetHost";
    private int mFlags;
    private Launcher mLauncher;
    private final ArrayList<Runnable> mProviderChangeListeners;
    private int mQsbWidgetId;

    public LauncherAppWidgetHost(Launcher launcher, int hostId) {
        super(launcher, hostId);
        this.mProviderChangeListeners = new ArrayList<>();
        this.mQsbWidgetId = -1;
        this.mFlags = 2;
        this.mLauncher = launcher;
        initConciergeBoardMngr(ConciergeBoardMngr.getInstance());
        initPackageUpdateReceiver();
        LGLog.d(TAG, "newAppWidgetHost with " + ConciergeBoardMngr.getInstance());
    }

    public void setQsbWidgetId(int widgetId) {
        this.mQsbWidgetId = widgetId;
    }

    @Override // com.lge.lgewidgetlib.LgeAppWidgetHost, android.appwidget.AppWidgetHost
    protected AppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        if (appWidgetId == this.mQsbWidgetId) {
            return new LauncherAppWidgetHostView(context) { // from class: com.android.launcher3.LauncherAppWidgetHost.1
                @Override // com.android.launcher3.LauncherAppWidgetHostView, android.appwidget.AppWidgetHostView
                protected View getErrorView() {
                    return new View(getContext());
                }
            };
        }
        return new LauncherAppWidgetHostView(context);
    }

    @Override // android.appwidget.AppWidgetHost
    public void startListening() {
        try {
            super.startListening();
        } catch (Exception e) {
            if (!(e.getCause() instanceof TransactionTooLargeException)) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override // com.lge.lgewidgetlib.LgeAppWidgetHost, android.appwidget.AppWidgetHost
    public void stopListening() {
        super.stopListening();
        clearViews();
    }

    public void addProviderChangeListener(Runnable callback) {
        this.mProviderChangeListeners.add(callback);
    }

    public void removeProviderChangeListener(Runnable callback) {
        this.mProviderChangeListeners.remove(callback);
    }

    @Override // android.appwidget.AppWidgetHost
    protected void onProvidersChanged() {
        LGLog.i(TAG, "onProvidersChanged");
        LauncherModel model = this.mLauncher.getModel();
        Launcher launcher = this.mLauncher;
        model.loadAndBindWidgetsAndShortcuts(launcher, launcher, true);
        if (this.mProviderChangeListeners.isEmpty()) {
            return;
        }
        Iterator it = new ArrayList(this.mProviderChangeListeners).iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v0, resolved type: android.content.Context */
    /* JADX WARN: Multi-variable type inference failed */
    public AppWidgetHostView createView(Context context, int i, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo) {
        AppWidgetHostView appWidgetHostViewCreateView;
        if (launcherAppWidgetProviderInfo.isCustomWidget) {
            LauncherAppWidgetHostView launcherAppWidgetHostView = new LauncherAppWidgetHostView(context);
            ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(launcherAppWidgetProviderInfo.initialLayout, launcherAppWidgetHostView);
            launcherAppWidgetHostView.setAppWidget(0, launcherAppWidgetProviderInfo);
            launcherAppWidgetHostView.updateLastInflationOrientation();
            appWidgetHostViewCreateView = launcherAppWidgetHostView;
        } else {
            appWidgetHostViewCreateView = super.createView(context, i, (AppWidgetProviderInfo) launcherAppWidgetProviderInfo);
        }
        UninstallModeManager.getInstance(context);
        if (UninstallModeManager.isEnabled()) {
            appWidgetHostViewCreateView.setOnClickListener((View.OnClickListener) context);
            appWidgetHostViewCreateView.setClickable(false);
        }
        return appWidgetHostViewCreateView;
    }

    @Override // android.appwidget.AppWidgetHost
    protected void onProviderChanged(int appWidgetId, AppWidgetProviderInfo appWidget) {
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfoFromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this.mLauncher, appWidget);
        super.onProviderChanged(appWidgetId, launcherAppWidgetProviderInfoFromProviderInfo);
        launcherAppWidgetProviderInfoFromProviderInfo.afterUpdateInitSpans(this.mLauncher);
    }

    public Launcher getLauncher() {
        return this.mLauncher;
    }

    public void setResumed(boolean isResumed) {
        int i = this.mFlags;
        if (isResumed == ((i & 2) != 0)) {
            return;
        }
        if (isResumed) {
            int i2 = i | 2;
            this.mFlags = i2;
            if ((i2 & 4) == 0 || (i2 & 1) != 0) {
                return;
            }
            startListening();
            return;
        }
        this.mFlags = i & (-3);
    }

    public void setListenIfResumed(boolean listenIfResumed) {
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            int i = this.mFlags;
            if (listenIfResumed == ((i & 4) != 0)) {
                return;
            }
            if (listenIfResumed) {
                int i2 = i | 4;
                this.mFlags = i2;
                if ((i2 & 2) != 0) {
                    startListening();
                    return;
                }
                return;
            }
            this.mFlags = i & (-5);
            stopListening();
        }
    }
}
