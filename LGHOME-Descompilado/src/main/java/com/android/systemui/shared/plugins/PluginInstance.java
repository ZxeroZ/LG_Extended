package com.android.systemui.shared.plugins;

import android.app.ActivityThread;
import android.app.LoadedApk;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginFragment;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginActionManager;
import com.android.systemui.shared.plugins.PluginManagerImpl;
import com.android.systemui.shared.plugins.VersionInfo;
import dalvik.system.PathClassLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class PluginInstance<T extends Plugin> {
    private static final String TAG = "PluginInstance";
    private static final Map<String, ClassLoader> sClassLoaders = new ArrayMap();
    private final ComponentName mComponentName;
    private final T mPlugin;
    private final Context mPluginContext;
    private final VersionInfo mVersionInfo;

    public PluginInstance(ComponentName componentName, T t, Context context, VersionInfo versionInfo) {
        this.mComponentName = componentName;
        this.mPlugin = t;
        this.mPluginContext = context;
        this.mVersionInfo = versionInfo;
    }

    public void onCreate(Context context, PluginListener<T> pluginListener) {
        T t = this.mPlugin;
        if (!(t instanceof PluginFragment)) {
            t.onCreate(context, this.mPluginContext);
        }
        pluginListener.onPluginConnected(this.mPlugin, this.mPluginContext);
    }

    public void onDestroy(PluginListener<T> pluginListener) {
        pluginListener.onPluginDisconnected(this.mPlugin);
        T t = this.mPlugin;
        if (t instanceof PluginFragment) {
            return;
        }
        t.onDestroy();
    }

    public boolean containsPluginClass(Class cls) {
        return this.mPlugin.getClass().getName().equals(cls.getName());
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public String getPackage() {
        return this.mComponentName.getPackageName();
    }

    public VersionInfo getVersionInfo() {
        return this.mVersionInfo;
    }

    Context getPluginContext() {
        return this.mPluginContext;
    }

    public static class Factory {
        private final ClassLoader mBaseClassLoader;
        private final InstanceFactory<?> mInstanceFactory;
        private final boolean mIsDebug;
        private final List<String> mPrivilegedPlugins;
        private final VersionChecker mVersionChecker;

        public Factory(ClassLoader classLoader, InstanceFactory<?> instanceFactory, VersionChecker versionChecker, List<String> list, boolean z) {
            this.mPrivilegedPlugins = list;
            this.mBaseClassLoader = classLoader;
            this.mInstanceFactory = instanceFactory;
            this.mVersionChecker = versionChecker;
            this.mIsDebug = z;
        }

        public <T extends Plugin> PluginInstance<T> create(Context context, ApplicationInfo applicationInfo, ComponentName componentName, Class<T> cls) throws IllegalAccessException, PackageManager.NameNotFoundException, InstantiationException, ClassNotFoundException {
            ClassLoader classLoader = getClassLoader(applicationInfo, this.mBaseClassLoader);
            PluginActionManager.PluginContextWrapper pluginContextWrapper = new PluginActionManager.PluginContextWrapper(context.createApplicationContext(applicationInfo, 0), classLoader);
            Class<?> cls2 = Class.forName(componentName.getClassName(), true, classLoader);
            Plugin pluginCreate = this.mInstanceFactory.create(cls2);
            return new PluginInstance<>(componentName, pluginCreate, pluginContextWrapper, this.mVersionChecker.checkVersion(cls2, cls, pluginCreate));
        }

        private boolean isPluginPackagePrivileged(String str) {
            for (String str2 : this.mPrivilegedPlugins) {
                ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(str2);
                if (componentNameUnflattenFromString != null) {
                    if (componentNameUnflattenFromString.getPackageName().equals(str)) {
                        return true;
                    }
                } else if (str2.equals(str)) {
                    return true;
                }
            }
            return false;
        }

        private ClassLoader getParentClassLoader(ClassLoader classLoader) {
            return new PluginManagerImpl.ClassLoaderFilter(classLoader, "com.android.systemui.plugin");
        }

        private ClassLoader getClassLoader(ApplicationInfo applicationInfo, ClassLoader classLoader) {
            if (this.mIsDebug || isPluginPackagePrivileged(applicationInfo.packageName)) {
                if (PluginInstance.sClassLoaders.containsKey(applicationInfo.packageName)) {
                    return (ClassLoader) PluginInstance.sClassLoaders.get(applicationInfo.packageName);
                }
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                LoadedApk.makePaths((ActivityThread) null, true, applicationInfo, arrayList, arrayList2);
                PathClassLoader pathClassLoader = new PathClassLoader(TextUtils.join(File.pathSeparator, arrayList), TextUtils.join(File.pathSeparator, arrayList2), getParentClassLoader(classLoader));
                PluginInstance.sClassLoaders.put(applicationInfo.packageName, pathClassLoader);
                return pathClassLoader;
            }
            Log.w(PluginInstance.TAG, "Cannot get class loader for non-privileged plugin. Src:" + applicationInfo.sourceDir + ", pkg: " + applicationInfo.packageName);
            return null;
        }
    }

    public static class VersionChecker {
        public <T extends Plugin> VersionInfo checkVersion(Class<T> cls, Class<T> cls2, Plugin plugin) {
            VersionInfo versionInfoAddClass = new VersionInfo().addClass(cls2);
            VersionInfo versionInfoAddClass2 = new VersionInfo().addClass(cls);
            if (versionInfoAddClass2.hasVersionInfo()) {
                versionInfoAddClass.checkVersion(versionInfoAddClass2);
                return versionInfoAddClass2;
            }
            if (plugin.getVersion() == versionInfoAddClass.getDefaultVersion()) {
                return null;
            }
            throw new VersionInfo.InvalidVersionException("Invalid legacy version", false);
        }
    }

    public static class InstanceFactory<T extends Plugin> {
        T create(Class cls) throws IllegalAccessException, InstantiationException {
            return (T) cls.newInstance();
        }
    }
}
