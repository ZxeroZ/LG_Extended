package com.lge.launcher3.homesettings;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/* JADX INFO: loaded from: classes.dex */
public class HomeSettingContext extends ContextWrapper {
    public HomeSettingContext(Context base) {
        super(base);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }

    public Context createApplicationContext(ApplicationInfo application, int flags) throws PackageManager.NameNotFoundException {
        return new HomeSettingContext(super.createApplicationContext(application, flags));
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        return new HomeSettingContext(super.createPackageContext(packageName, flags));
    }
}
