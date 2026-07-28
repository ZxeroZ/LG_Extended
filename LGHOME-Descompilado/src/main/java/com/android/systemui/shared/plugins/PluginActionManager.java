package com.android.systemui.shared.plugins;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginInstance;
import com.android.systemui.shared.plugins.VersionInfo;
import com.lge.launcher3.config.LauncherConst;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public class PluginActionManager<T extends Plugin> {
    private static final boolean DEBUG = false;
    public static final String PLUGIN_PERMISSION = "com.android.systemui.permission.PLUGIN";
    private static final String TAG = "PluginInstanceManager";
    private final String mAction;
    private final boolean mAllowMultiple;
    private final Executor mBgExecutor;
    private final Context mContext;
    private final boolean mIsDebuggable;
    private final PluginListener<T> mListener;
    private final Executor mMainExecutor;
    private final NotificationManager mNotificationManager;
    private final Class<T> mPluginClass;
    private final PluginEnabler mPluginEnabler;
    private final PluginInstance.Factory mPluginInstanceFactory;
    private final ArrayList<PluginInstance<T>> mPluginInstances;
    private final PackageManager mPm;
    private final ArraySet<String> mPrivilegedPlugins;

    private PluginActionManager(Context context, PackageManager packageManager, String str, PluginListener<T> pluginListener, Class<T> cls, boolean z, Executor executor, Executor executor2, boolean z2, NotificationManager notificationManager, PluginEnabler pluginEnabler, List<String> list, PluginInstance.Factory factory) {
        ArraySet<String> arraySet = new ArraySet<>();
        this.mPrivilegedPlugins = arraySet;
        this.mPluginInstances = new ArrayList<>();
        this.mPluginClass = cls;
        this.mMainExecutor = executor;
        this.mBgExecutor = executor2;
        this.mContext = context;
        this.mPm = packageManager;
        this.mAction = str;
        this.mListener = pluginListener;
        this.mAllowMultiple = z;
        this.mNotificationManager = notificationManager;
        this.mPluginEnabler = pluginEnabler;
        this.mPluginInstanceFactory = factory;
        arraySet.addAll(list);
        this.mIsDebuggable = z2;
    }

    public void loadAll() {
        this.mBgExecutor.execute(new Runnable() { // from class: com.android.systemui.shared.plugins.-$$Lambda$PluginActionManager$kBZKG6dtfbpWOlthMBUiG2NGTM4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.queryAll();
            }
        });
    }

    public void destroy() {
        for (final PluginInstance pluginInstance : new ArrayList(this.mPluginInstances)) {
            this.mMainExecutor.execute(new Runnable() { // from class: com.android.systemui.shared.plugins.-$$Lambda$PluginActionManager$pBQrwk3hQN6yH77r52dt5cnCyVw
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$destroy$0$PluginActionManager(pluginInstance);
                }
            });
        }
    }

    public void onPackageRemoved(final String str) {
        this.mBgExecutor.execute(new Runnable() { // from class: com.android.systemui.shared.plugins.-$$Lambda$PluginActionManager$tRQyi7k9O-R4crA50YERIw8utIE
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onPackageRemoved$1$PluginActionManager(str);
            }
        });
    }

    public void reloadPackage(final String str) {
        this.mBgExecutor.execute(new Runnable() { // from class: com.android.systemui.shared.plugins.-$$Lambda$PluginActionManager$nTrqaXNoyZ9Ewe_oMzlt3sqKZco
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$reloadPackage$2$PluginActionManager(str);
            }
        });
    }

    public /* synthetic */ void lambda$reloadPackage$2$PluginActionManager(String str) {
        lambda$onPackageRemoved$1$PluginActionManager(str);
        queryPkg(str);
    }

    public boolean checkAndDisable(String str) {
        boolean zDisable = false;
        for (PluginInstance<T> pluginInstance : new ArrayList(this.mPluginInstances)) {
            if (str.startsWith(pluginInstance.getPackage())) {
                zDisable |= disable(pluginInstance, 3);
            }
        }
        return zDisable;
    }

    public boolean disableAll() {
        ArrayList arrayList = new ArrayList(this.mPluginInstances);
        boolean zDisable = false;
        for (int i = 0; i < arrayList.size(); i++) {
            zDisable |= disable((PluginInstance) arrayList.get(i), 4);
        }
        return zDisable;
    }

    boolean isPluginPrivileged(ComponentName componentName) {
        for (String str : this.mPrivilegedPlugins) {
            ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(str);
            if (componentNameUnflattenFromString == null) {
                if (str.equals(componentName.getPackageName())) {
                    return true;
                }
            } else if (componentNameUnflattenFromString.equals(componentName)) {
                return true;
            }
        }
        return false;
    }

    private boolean disable(PluginInstance<T> pluginInstance, int i) {
        ComponentName componentName = pluginInstance.getComponentName();
        if (isPluginPrivileged(componentName)) {
            return false;
        }
        Log.w(TAG, "Disabling plugin " + componentName.flattenToShortString());
        this.mPluginEnabler.setDisabled(componentName, i);
        return true;
    }

    <C> boolean dependsOn(Plugin plugin, Class<C> cls) {
        for (PluginInstance pluginInstance : new ArrayList(this.mPluginInstances)) {
            if (pluginInstance.containsPluginClass(plugin.getClass())) {
                return pluginInstance.getVersionInfo() != null && pluginInstance.getVersionInfo().hasClass(cls);
            }
        }
        return false;
    }

    public String toString() {
        return String.format("%s@%s (action=%s)", getClass().getSimpleName(), Integer.valueOf(hashCode()), this.mAction);
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$handleQueryPlugins$5$PluginActionManager(Lcom/android/systemui/shared/plugins/PluginInstance;)V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: onPluginConnected, reason: merged with bridge method [inline-methods] */
    public void lambda$handleQueryPlugins$5$PluginActionManager(PluginInstance<T> pluginInstance) {
        PluginPrefs.setHasPlugins(this.mContext);
        pluginInstance.onCreate(this.mContext, this.mListener);
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$destroy$0$PluginActionManager(Lcom/android/systemui/shared/plugins/PluginInstance;)V */
    /* JADX DEBUG: Method merged with bridge method: lambda$queryAll$3$PluginActionManager(Lcom/android/systemui/shared/plugins/PluginInstance;)V */
    /* JADX DEBUG: Method merged with bridge method: lambda$removePkg$4$PluginActionManager(Lcom/android/systemui/shared/plugins/PluginInstance;)V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: onPluginDisconnected, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public void lambda$removePkg$4$PluginActionManager(PluginInstance<T> pluginInstance) {
        pluginInstance.onDestroy(this.mListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queryAll() {
        for (int size = this.mPluginInstances.size() - 1; size >= 0; size--) {
            final PluginInstance<T> pluginInstance = this.mPluginInstances.get(size);
            this.mMainExecutor.execute(new Runnable() { // from class: com.android.systemui.shared.plugins.-$$Lambda$PluginActionManager$UWzagWxnH7QoNH7ZAC9-VqRJxlg
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$queryAll$3$PluginActionManager(pluginInstance);
                }
            });
        }
        this.mPluginInstances.clear();
        handleQueryPlugins(null);
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$onPackageRemoved$1$PluginActionManager(Ljava/lang/String;)V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: removePkg, reason: merged with bridge method [inline-methods] */
    public void lambda$onPackageRemoved$1$PluginActionManager(String str) {
        for (int size = this.mPluginInstances.size() - 1; size >= 0; size--) {
            final PluginInstance<T> pluginInstance = this.mPluginInstances.get(size);
            if (pluginInstance.getPackage().equals(str)) {
                this.mMainExecutor.execute(new Runnable() { // from class: com.android.systemui.shared.plugins.-$$Lambda$PluginActionManager$1J013yPjqW1h78J0FyIon-FoVCQ
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$removePkg$4$PluginActionManager(pluginInstance);
                    }
                });
                this.mPluginInstances.remove(size);
            }
        }
    }

    private void queryPkg(String str) {
        if (this.mAllowMultiple || this.mPluginInstances.size() == 0) {
            handleQueryPlugins(str);
        }
    }

    private void handleQueryPlugins(String str) {
        Intent intent = new Intent(this.mAction);
        if (str != null) {
            intent.setPackage(str);
        }
        List<ResolveInfo> listQueryIntentServices = this.mPm.queryIntentServices(intent, 0);
        if (listQueryIntentServices.size() > 1 && !this.mAllowMultiple) {
            Log.w(TAG, "Multiple plugins found for " + this.mAction);
            return;
        }
        for (ResolveInfo resolveInfo : listQueryIntentServices) {
            final PluginInstance<T> pluginInstanceLoadPluginComponent = loadPluginComponent(new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name));
            if (pluginInstanceLoadPluginComponent != null) {
                this.mPluginInstances.add(pluginInstanceLoadPluginComponent);
                this.mMainExecutor.execute(new Runnable() { // from class: com.android.systemui.shared.plugins.-$$Lambda$PluginActionManager$8TM7K2rxqRKfAu55Vw3Zcf26ozs
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$handleQueryPlugins$5$PluginActionManager(pluginInstanceLoadPluginComponent);
                    }
                });
            }
        }
    }

    private PluginInstance<T> loadPluginComponent(ComponentName componentName) {
        if (!this.mIsDebuggable && !isPluginPrivileged(componentName)) {
            Log.w(TAG, "Plugin cannot be loaded on production build: " + componentName);
            return null;
        }
        if (!this.mPluginEnabler.isEnabled(componentName)) {
            return null;
        }
        String packageName = componentName.getPackageName();
        try {
            if (this.mPm.checkPermission(PLUGIN_PERMISSION, packageName) != 0) {
                Log.d(TAG, "Plugin doesn't have permission: " + packageName);
                return null;
            }
            try {
                return this.mPluginInstanceFactory.create(this.mContext, this.mPm.getApplicationInfo(packageName, 0), componentName, this.mPluginClass);
            } catch (VersionInfo.InvalidVersionException e) {
                reportInvalidVersion(componentName, componentName.getClassName(), e);
                return null;
            }
        } catch (Throwable th) {
            Log.w(TAG, "Couldn't load plugin: " + packageName, th);
            return null;
        }
    }

    private void reportInvalidVersion(ComponentName componentName, String str, VersionInfo.InvalidVersionException invalidVersionException) {
        Notification.Builder color = new Notification.Builder(this.mContext, PluginManager.NOTIFICATION_CHANNEL_ID).setStyle(new Notification.BigTextStyle()).setSmallIcon(Resources.getSystem().getIdentifier("stat_sys_warning", LauncherConst.RESOURCE_IMAGE_TYPE, LauncherConst.PACKAGE_NAME_NATIVE)).setWhen(0L).setShowWhen(false).setVisibility(1).setColor(this.mContext.getColor(Resources.getSystem().getIdentifier("system_notification_accent_color", "color", LauncherConst.PACKAGE_NAME_NATIVE)));
        try {
            str = this.mPm.getServiceInfo(componentName, 0).loadLabel(this.mPm).toString();
        } catch (PackageManager.NameNotFoundException unused) {
        }
        if (!invalidVersionException.isTooNew()) {
            color.setContentTitle("Plugin \"" + str + "\" is too old").setContentText("Contact plugin developer to get an updated version.\n" + invalidVersionException.getMessage());
        } else {
            color.setContentTitle("Plugin \"" + str + "\" is too new").setContentText("Check to see if an OTA is available.\n" + invalidVersionException.getMessage());
        }
        color.addAction(new Notification.Action.Builder((Icon) null, "Disable plugin", PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.systemui.action.DISABLE_PLUGIN").setData(Uri.parse("package://" + componentName.flattenToString())), 67108864)).build());
        this.mNotificationManager.notify(6, color.build());
        Log.w(TAG, "Plugin has invalid interface version " + invalidVersionException.getActualVersion() + ", expected " + invalidVersionException.getExpectedVersion());
    }

    public static class Factory {
        private final Executor mBgExecutor;
        private final Context mContext;
        private final Executor mMainExecutor;
        private final NotificationManager mNotificationManager;
        private final PackageManager mPackageManager;
        private final PluginEnabler mPluginEnabler;
        private final PluginInstance.Factory mPluginInstanceFactory;
        private final List<String> mPrivilegedPlugins;

        public Factory(Context context, PackageManager packageManager, Executor executor, Executor executor2, NotificationManager notificationManager, PluginEnabler pluginEnabler, List<String> list, PluginInstance.Factory factory) {
            this.mContext = context;
            this.mPackageManager = packageManager;
            this.mMainExecutor = executor;
            this.mBgExecutor = executor2;
            this.mNotificationManager = notificationManager;
            this.mPluginEnabler = pluginEnabler;
            this.mPrivilegedPlugins = list;
            this.mPluginInstanceFactory = factory;
        }

        <T extends Plugin> PluginActionManager<T> create(String str, PluginListener<T> pluginListener, Class<T> cls, boolean z, boolean z2) {
            return new PluginActionManager<>(this.mContext, this.mPackageManager, str, pluginListener, cls, z, this.mMainExecutor, this.mBgExecutor, z2, this.mNotificationManager, this.mPluginEnabler, this.mPrivilegedPlugins, this.mPluginInstanceFactory);
        }
    }

    public static class PluginContextWrapper extends ContextWrapper {
        private final ClassLoader mClassLoader;
        private LayoutInflater mInflater;

        public PluginContextWrapper(Context context, ClassLoader classLoader) {
            super(context);
            this.mClassLoader = classLoader;
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public ClassLoader getClassLoader() {
            return this.mClassLoader;
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Object getSystemService(String str) {
            if ("layout_inflater".equals(str)) {
                if (this.mInflater == null) {
                    this.mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
                }
                return this.mInflater;
            }
            return getBaseContext().getSystemService(str);
        }
    }
}
