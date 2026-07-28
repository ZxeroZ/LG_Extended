package com.lge.lgewidgetlib;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.util.SparseArray;
import android.widget.RemoteViews;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.lgewidgetlib.extview.IExtViewHostAdapter;
import com.lge.lgewidgetlib.extview.IWidgetExtHandler;
import com.lge.lgewidgetlib.extview.LgeAppWidgetExtViewHost;
import java.util.HashMap;

/* JADX INFO: loaded from: classes2.dex */
public class LgeAppWidgetHost extends AppWidgetHost implements IWidgetExtHandler {
    private static final boolean DEBUG = false;
    private static final String TAG = "LgeAppWidgetHost";
    IExtViewHostAdapter mConciergeBoard;
    IExtViewHostAdapter mConciergeBoardMngr;
    Context mContext;
    PackageUpdateReceiver mPackageReceiver;
    private OnProvidersChangedListener mProvidersChangedListener;

    public interface OnProvidersChangedListener {
        void onProvidersChanged();
    }

    public void setOnProvidersChangedListener(OnProvidersChangedListener l) {
        this.mProvidersChangedListener = l;
    }

    public LgeAppWidgetHost(Context context, int hostId, IExtViewHostAdapter conciergeBoardMngr) {
        super(context, hostId);
        this.mConciergeBoardMngr = null;
        this.mConciergeBoard = null;
        this.mProvidersChangedListener = null;
        this.mContext = context;
        initConciergeBoardMngr(conciergeBoardMngr);
        initPackageUpdateReceiver();
    }

    public LgeAppWidgetHost(Context context, int hostId, RemoteViews.InteractionHandler handler, Looper looper, IExtViewHostAdapter conciergeBoardMngr) {
        super(context, hostId, handler, looper);
        this.mConciergeBoardMngr = null;
        this.mConciergeBoard = null;
        this.mProvidersChangedListener = null;
        this.mContext = context;
        initConciergeBoardMngr(conciergeBoardMngr);
        initPackageUpdateReceiver();
    }

    public LgeAppWidgetHost(Context context, int hostId) {
        super(context, hostId);
        this.mConciergeBoardMngr = null;
        this.mConciergeBoard = null;
        this.mProvidersChangedListener = null;
        this.mContext = context;
    }

    public LgeAppWidgetHost(Context context, int hostId, RemoteViews.InteractionHandler handler, Looper looper) {
        super(context, hostId, handler, looper);
        this.mConciergeBoardMngr = null;
        this.mConciergeBoard = null;
        this.mProvidersChangedListener = null;
        this.mContext = context;
    }

    public void initConciergeBoardMngr(IExtViewHostAdapter conciergeBoardMngr) {
        if (conciergeBoardMngr != null) {
            this.mConciergeBoardMngr = conciergeBoardMngr;
            conciergeBoardMngr.setWidgetExtHandler(this);
        }
    }

    @Override // android.appwidget.AppWidgetHost
    protected AppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        WLog.d(TAG, "LgeWidgetLib Version = " + LgeWidgetFeature.getLgeWidgetLibVersion());
        if (this.mConciergeBoardMngr == null) {
            return super.onCreateView(context, appWidgetId, appWidget);
        }
        return new LgeAppWidgetHostView(context, (RemoteViews.InteractionHandler) LgeReflectionUtil.getPrivateField(AppWidgetHost.class, this, "mInteractionHandler"));
    }

    @Override // com.lge.lgewidgetlib.extview.IWidgetExtHandler
    public void cancelExtViewMode() {
        if (this.mConciergeBoardMngr == null) {
            return;
        }
        Object privateField = LgeReflectionUtil.getPrivateField(AppWidgetHost.class, this, "mViews");
        if (privateField instanceof HashMap) {
            for (AppWidgetHostView appWidgetHostView : ((HashMap) privateField).values()) {
                if (appWidgetHostView instanceof LgeAppWidgetHostView) {
                    ((LgeAppWidgetHostView) appWidgetHostView).cancelWidgetExt();
                }
            }
            return;
        }
        if (privateField instanceof SparseArray) {
            SparseArray sparseArray = (SparseArray) privateField;
            for (int i = 0; i < sparseArray.size(); i++) {
                AppWidgetHostView appWidgetHostView2 = (AppWidgetHostView) sparseArray.get(sparseArray.keyAt(i));
                if (appWidgetHostView2 instanceof LgeAppWidgetHostView) {
                    ((LgeAppWidgetHostView) appWidgetHostView2).cancelWidgetExt();
                }
            }
        }
    }

    @Override // com.lge.lgewidgetlib.extview.IWidgetExtHandler
    public boolean isExtViewMode() {
        if (this.mConciergeBoardMngr == null) {
            return false;
        }
        return LgeAppWidgetExtViewHost.sIsExpaned;
    }

    @Override // com.lge.lgewidgetlib.extview.IWidgetExtHandler
    public void notifyExtViewAvailable() {
        if (this.mConciergeBoardMngr == null) {
            return;
        }
        Object privateField = LgeReflectionUtil.getPrivateField(AppWidgetHost.class, this, "mViews");
        if (privateField instanceof HashMap) {
            for (AppWidgetHostView appWidgetHostView : ((HashMap) privateField).values()) {
                if (appWidgetHostView instanceof LgeAppWidgetHostView) {
                    ((LgeAppWidgetHostView) appWidgetHostView).notifyExtViewAvailable();
                }
            }
            return;
        }
        if (privateField instanceof SparseArray) {
            SparseArray sparseArray = (SparseArray) privateField;
            for (int i = 0; i < sparseArray.size(); i++) {
                AppWidgetHostView appWidgetHostView2 = (AppWidgetHostView) sparseArray.get(sparseArray.keyAt(i));
                if (appWidgetHostView2 instanceof LgeAppWidgetHostView) {
                    ((LgeAppWidgetHostView) appWidgetHostView2).notifyExtViewAvailable();
                }
            }
        }
    }

    @Override // com.lge.lgewidgetlib.extview.IWidgetExtHandler
    public void notifyWidgetHostDestroyed() {
        WLog.i(TAG, "notifyWidgetHostDestroyed()");
        if (this.mConciergeBoardMngr == null) {
            WLog.e(TAG, "mConciergeBoardMngr is null");
            return;
        }
        Object privateField = LgeReflectionUtil.getPrivateField(AppWidgetHost.class, this, "mViews");
        if (privateField instanceof HashMap) {
            for (AppWidgetHostView appWidgetHostView : ((HashMap) privateField).values()) {
                if (appWidgetHostView instanceof LgeAppWidgetHostView) {
                    ((LgeAppWidgetHostView) appWidgetHostView).notifyWidgetHostDestroyed();
                }
            }
            return;
        }
        if (privateField instanceof SparseArray) {
            SparseArray sparseArray = (SparseArray) privateField;
            for (int i = 0; i < sparseArray.size(); i++) {
                AppWidgetHostView appWidgetHostView2 = (AppWidgetHostView) sparseArray.get(sparseArray.keyAt(i));
                if (appWidgetHostView2 instanceof LgeAppWidgetHostView) {
                    ((LgeAppWidgetHostView) appWidgetHostView2).notifyWidgetHostDestroyed();
                }
            }
        }
    }

    @Override // com.lge.lgewidgetlib.extview.IWidgetExtHandler
    public void notifyBindingStarted() {
        WLog.i(TAG, "notifyBindingStarted()");
        if (this.mConciergeBoardMngr == null) {
            WLog.e(TAG, "mConciergeBoardMngr is null");
            return;
        }
        Object privateField = LgeReflectionUtil.getPrivateField(AppWidgetHost.class, this, "mViews");
        if (privateField instanceof HashMap) {
            for (AppWidgetHostView appWidgetHostView : ((HashMap) privateField).values()) {
                if (appWidgetHostView instanceof LgeAppWidgetHostView) {
                    ((LgeAppWidgetHostView) appWidgetHostView).notifyBindingStarted();
                }
            }
            return;
        }
        if (privateField instanceof SparseArray) {
            SparseArray sparseArray = (SparseArray) privateField;
            for (int i = 0; i < sparseArray.size(); i++) {
                AppWidgetHostView appWidgetHostView2 = (AppWidgetHostView) sparseArray.get(sparseArray.keyAt(i));
                if (appWidgetHostView2 instanceof LgeAppWidgetHostView) {
                    ((LgeAppWidgetHostView) appWidgetHostView2).notifyBindingStarted();
                }
            }
        }
    }

    @Override // android.appwidget.AppWidgetHost
    public void deleteAppWidgetId(int appWidgetId) {
        Object privateField = LgeReflectionUtil.getPrivateField(AppWidgetHost.class, this, "mViews");
        if (privateField instanceof HashMap) {
            AppWidgetHostView appWidgetHostView = (AppWidgetHostView) ((HashMap) privateField).get(Integer.valueOf(appWidgetId));
            if (appWidgetHostView instanceof LgeAppWidgetHostView) {
                ((LgeAppWidgetHostView) appWidgetHostView).notifyWidgetDeleted();
            }
        } else if (privateField instanceof SparseArray) {
            AppWidgetHostView appWidgetHostView2 = (AppWidgetHostView) ((SparseArray) privateField).get(appWidgetId);
            if (appWidgetHostView2 instanceof LgeAppWidgetHostView) {
                ((LgeAppWidgetHostView) appWidgetHostView2).notifyWidgetDeleted();
            }
        }
        super.deleteAppWidgetId(appWidgetId);
    }

    @Override // android.appwidget.AppWidgetHost
    public void stopListening() {
        super.stopListening();
        deletePackageReceiver();
    }

    @Override // android.appwidget.AppWidgetHost
    public void deleteHost() {
        super.deleteHost();
        deletePackageReceiver();
    }

    public void initPackageUpdateReceiver() {
        if (this.mPackageReceiver == null) {
            WLog.i(TAG, "initPackageUpdateReceiver");
            this.mPackageReceiver = new PackageUpdateReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
            intentFilter.addDataScheme(AppNotifierManager.ExtraSpec.USAGE_PACKAGE);
            this.mContext.registerReceiver(this.mPackageReceiver, intentFilter);
        }
    }

    void deletePackageReceiver() {
        if (this.mPackageReceiver != null) {
            WLog.i(TAG, "deletePackageReceiver");
            this.mContext.unregisterReceiver(this.mPackageReceiver);
            this.mPackageReceiver = null;
        }
    }

    class PackageUpdateReceiver extends BroadcastReceiver {
        PackageUpdateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED") && LgeWidgetFeature.isCustomClassLoaderSupportPackage(schemeSpecificPart)) {
                CustomLayoutInflaterFactory.clearConstructorMap();
                WLog.d(LgeAppWidgetHost.TAG, "Package Added, clear CustomConstructorMap");
            }
        }
    }
}
