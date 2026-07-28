package com.android.systemui.shared.plugins;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Toast;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginActionManager;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.UncaughtExceptionPreHandlerManager;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import java.io.PrintWriter;
import java.lang.Thread;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class PluginManagerImpl extends BroadcastReceiver implements PluginManager {
    static final String DISABLE_PLUGIN = "com.android.systemui.action.DISABLE_PLUGIN";
    private static final String TAG = "PluginManagerImpl";
    private final PluginActionManager.Factory mActionManagerFactory;
    private final Context mContext;
    private final boolean mIsDebuggable;
    private boolean mListening;
    private final PluginEnabler mPluginEnabler;
    private final PluginPrefs mPluginPrefs;
    private final ArraySet<String> mPrivilegedPlugins;
    private final ArrayMap<PluginListener<?>, PluginActionManager<?>> mPluginMap = new ArrayMap<>();
    private final Map<String, ClassLoader> mClassLoaders = new ArrayMap();

    public PluginManagerImpl(Context context, PluginActionManager.Factory factory, boolean z, UncaughtExceptionPreHandlerManager uncaughtExceptionPreHandlerManager, PluginEnabler pluginEnabler, PluginPrefs pluginPrefs, List<String> list) {
        ArraySet<String> arraySet = new ArraySet<>();
        this.mPrivilegedPlugins = arraySet;
        this.mContext = context;
        this.mActionManagerFactory = factory;
        this.mIsDebuggable = z;
        arraySet.addAll(list);
        this.mPluginPrefs = pluginPrefs;
        this.mPluginEnabler = pluginEnabler;
        uncaughtExceptionPreHandlerManager.registerHandler(new PluginExceptionHandler());
    }

    public boolean isDebuggable() {
        return this.mIsDebuggable;
    }

    @Override // com.android.systemui.shared.plugins.PluginManager
    public String[] getPrivilegedPlugins() {
        return (String[]) this.mPrivilegedPlugins.toArray(new String[0]);
    }

    @Override // com.android.systemui.shared.plugins.PluginManager
    public <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<T> cls) {
        addPluginListener((PluginListener) pluginListener, (Class) cls, false);
    }

    @Override // com.android.systemui.shared.plugins.PluginManager
    public <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<T> cls, boolean z) {
        addPluginListener(PluginManager.Helper.getAction(cls), pluginListener, cls, z);
    }

    @Override // com.android.systemui.shared.plugins.PluginManager
    public <T extends Plugin> void addPluginListener(String str, PluginListener<T> pluginListener, Class<T> cls) {
        addPluginListener(str, pluginListener, cls, false);
    }

    @Override // com.android.systemui.shared.plugins.PluginManager
    public <T extends Plugin> void addPluginListener(String str, PluginListener<T> pluginListener, Class<T> cls, boolean z) {
        this.mPluginPrefs.addAction(str);
        PluginActionManager<T> pluginActionManagerCreate = this.mActionManagerFactory.create(str, pluginListener, cls, z, isDebuggable());
        pluginActionManagerCreate.loadAll();
        synchronized (this) {
            this.mPluginMap.put(pluginListener, pluginActionManagerCreate);
        }
        startListening();
    }

    @Override // com.android.systemui.shared.plugins.PluginManager
    public void removePluginListener(PluginListener<?> pluginListener) {
        synchronized (this) {
            if (this.mPluginMap.containsKey(pluginListener)) {
                this.mPluginMap.remove(pluginListener).destroy();
                if (this.mPluginMap.size() == 0) {
                    stopListening();
                }
            }
        }
    }

    private void startListening() {
        if (this.mListening) {
            return;
        }
        this.mListening = true;
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme(AppNotifierManager.ExtraSpec.USAGE_PACKAGE);
        this.mContext.registerReceiver(this, intentFilter);
        intentFilter.addAction("com.android.systemui.action.PLUGIN_CHANGED");
        intentFilter.addAction(DISABLE_PLUGIN);
        intentFilter.addDataScheme(AppNotifierManager.ExtraSpec.USAGE_PACKAGE);
        this.mContext.registerReceiver(this, intentFilter, PluginActionManager.PLUGIN_PERMISSION, null, 2);
        this.mContext.registerReceiver(this, new IntentFilter("android.intent.action.USER_UNLOCKED"));
    }

    private void stopListening() {
        if (this.mListening) {
            this.mListening = false;
            this.mContext.unregisterReceiver(this);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int disableReason;
        if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
            synchronized (this) {
                Iterator<PluginActionManager<?>> it = this.mPluginMap.values().iterator();
                while (it.hasNext()) {
                    it.next().loadAll();
                }
            }
            return;
        }
        if (DISABLE_PLUGIN.equals(intent.getAction())) {
            ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(intent.getData().toString().substring(10));
            if (isPluginPrivileged(componentNameUnflattenFromString)) {
                return;
            }
            this.mPluginEnabler.setDisabled(componentNameUnflattenFromString, 2);
            ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).cancel(componentNameUnflattenFromString.getClassName(), 6);
            return;
        }
        String encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
        ComponentName componentNameUnflattenFromString2 = ComponentName.unflattenFromString(encodedSchemeSpecificPart);
        if (clearClassLoader(encodedSchemeSpecificPart)) {
            if (Build.IS_ENG) {
                Toast.makeText(this.mContext, "Reloading " + encodedSchemeSpecificPart, 1).show();
            } else {
                Log.v(TAG, "Reloading " + encodedSchemeSpecificPart);
            }
        }
        if ("android.intent.action.PACKAGE_REPLACED".equals(intent.getAction()) && componentNameUnflattenFromString2 != null && ((disableReason = this.mPluginEnabler.getDisableReason(componentNameUnflattenFromString2)) == 3 || disableReason == 4 || disableReason == 2)) {
            Log.i(TAG, "Re-enabling previously disabled plugin that has been updated: " + componentNameUnflattenFromString2.flattenToShortString());
            this.mPluginEnabler.setEnabled(componentNameUnflattenFromString2);
        }
        synchronized (this) {
            if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction()) || "android.intent.action.PACKAGE_CHANGED".equals(intent.getAction()) || "android.intent.action.PACKAGE_REPLACED".equals(intent.getAction())) {
                Iterator<PluginActionManager<?>> it2 = this.mPluginMap.values().iterator();
                while (it2.hasNext()) {
                    it2.next().reloadPackage(encodedSchemeSpecificPart);
                }
            } else {
                Iterator<PluginActionManager<?>> it3 = this.mPluginMap.values().iterator();
                while (it3.hasNext()) {
                    it3.next().onPackageRemoved(encodedSchemeSpecificPart);
                }
            }
        }
    }

    private boolean clearClassLoader(String str) {
        return this.mClassLoaders.remove(str) != null;
    }

    @Override // com.android.systemui.shared.plugins.PluginManager
    public <T> boolean dependsOn(Plugin plugin, Class<T> cls) {
        synchronized (this) {
            for (int i = 0; i < this.mPluginMap.size(); i++) {
                if (this.mPluginMap.valueAt(i).dependsOn(plugin, cls)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void dump(PrintWriter printWriter, String[] strArr) {
        synchronized (this) {
            printWriter.println(String.format("  plugin map (%d):", Integer.valueOf(this.mPluginMap.size())));
            for (PluginListener<?> pluginListener : this.mPluginMap.keySet()) {
                printWriter.println(String.format("    %s -> %s", pluginListener, this.mPluginMap.get(pluginListener)));
            }
        }
    }

    private boolean isPluginPrivileged(ComponentName componentName) {
        for (String str : this.mPrivilegedPlugins) {
            ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(str);
            if (componentNameUnflattenFromString != null) {
                if (componentNameUnflattenFromString.equals(componentName)) {
                    return true;
                }
            } else if (str.equals(componentName.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    static class ClassLoaderFilter extends ClassLoader {
        private final ClassLoader mBase;
        private final String mPackage;

        public ClassLoaderFilter(ClassLoader classLoader, String str) {
            super(ClassLoader.getSystemClassLoader());
            this.mBase = classLoader;
            this.mPackage = str;
        }

        @Override // java.lang.ClassLoader
        protected Class<?> loadClass(String str, boolean z) throws ClassNotFoundException {
            if (!str.startsWith(this.mPackage)) {
                super.loadClass(str, z);
            }
            return this.mBase.loadClass(str);
        }
    }

    private class PluginExceptionHandler implements Thread.UncaughtExceptionHandler {
        private PluginExceptionHandler() {
        }

        @Override // java.lang.Thread.UncaughtExceptionHandler
        public void uncaughtException(Thread thread, Throwable th) {
            if (SystemProperties.getBoolean("plugin.debugging", false)) {
                return;
            }
            boolean zCheckStack = checkStack(th);
            if (!zCheckStack) {
                synchronized (this) {
                    Iterator it = PluginManagerImpl.this.mPluginMap.values().iterator();
                    while (it.hasNext()) {
                        zCheckStack |= ((PluginActionManager) it.next()).disableAll();
                    }
                }
            }
            if (zCheckStack) {
                new CrashWhilePluginActiveException(th);
            }
        }

        private boolean checkStack(Throwable th) {
            boolean zCheckAndDisable;
            if (th == null) {
                return false;
            }
            synchronized (this) {
                zCheckAndDisable = false;
                for (StackTraceElement stackTraceElement : th.getStackTrace()) {
                    Iterator it = PluginManagerImpl.this.mPluginMap.values().iterator();
                    while (it.hasNext()) {
                        zCheckAndDisable |= ((PluginActionManager) it.next()).checkAndDisable(stackTraceElement.getClassName());
                    }
                }
            }
            return checkStack(th.getCause()) | zCheckAndDisable;
        }
    }

    public static class CrashWhilePluginActiveException extends RuntimeException {
        public CrashWhilePluginActiveException(Throwable th) {
            super(th);
        }
    }
}
