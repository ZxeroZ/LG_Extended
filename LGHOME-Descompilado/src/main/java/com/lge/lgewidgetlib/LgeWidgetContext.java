package com.lge.lgewidgetlib;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import com.lge.launcher3.config.QMemoPanelConst;
import dalvik.system.PathClassLoader;

/* JADX INFO: loaded from: classes2.dex */
public class LgeWidgetContext extends ContextWrapper {
    ClassLoader mClassLoader;

    @Override // android.content.ContextWrapper, android.content.Context
    public Context getApplicationContext() {
        return this;
    }

    public LgeWidgetContext(Context base) {
        super(base);
        this.mClassLoader = null;
        String str = getApplicationInfo().sourceDir;
        if (LgeWidgetFeature.isCustomClassLoaderSupportPackage(base.getPackageName())) {
            this.mClassLoader = new PathClassLoader(str, base.getClassLoader().getParent());
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public ClassLoader getClassLoader() {
        ClassLoader classLoader = this.mClassLoader;
        return classLoader != null ? classLoader : super.getClassLoader();
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        if (isLGEAppWidgetPackage(packageName)) {
            return new LgeWidgetContext(super.createPackageContext(packageName, 3));
        }
        return super.createPackageContext(packageName, flags);
    }

    public Context createPackageContextAsUser(String packageName, int flags, UserHandle user) throws PackageManager.NameNotFoundException {
        if (isLGEAppWidgetPackage(packageName)) {
            return new LgeWidgetContext(super.createPackageContextAsUser(packageName, 3, user));
        }
        return createPackageContextAsUser(packageName, flags, user);
    }

    public static String[] getLGEAllowedPackages() {
        return new String[]{"com.android.calendar", "com.lge.concierge", "com.lge.sizechangable.weather", QMemoPanelConst.QMEMOPANEL_PACKAGE_NAME, "com.lge.clock", "com.lge.sizechangable.musicwidget.widget", "com.lge.iftttmanager", "com.lge.music", "com.lge.phonemanagement", "com.lge.fmradio", "com.lge.email", "com.lge.smartwidget"};
    }

    public static boolean isLGEAppWidgetPackage(String packageName) {
        String[] lGEAllowedPackages = getLGEAllowedPackages();
        if (packageName == null) {
            return false;
        }
        for (String str : lGEAllowedPackages) {
            if (packageName.startsWith(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLGEWeatherWidgetPackage(String packageName) {
        if (packageName == null) {
            return false;
        }
        return packageName.startsWith("com.lge.sizechangable.weather");
    }
}
