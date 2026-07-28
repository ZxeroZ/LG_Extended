package com.lge.launcher3.smartbulletin.widgetlibrary;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;

/* JADX INFO: loaded from: classes.dex */
public class MyWidgetContext extends ContextWrapper {
    @Override // android.content.ContextWrapper, android.content.Context
    public Context getApplicationContext() {
        return this;
    }

    public MyWidgetContext(Context base) {
        super(base);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }

    public Context createApplicationContext(ApplicationInfo application, int flags) throws PackageManager.NameNotFoundException {
        if (isLGEAppWidgetPackage(application.packageName)) {
            return new MyWidgetContext(super.createApplicationContext(application, 3));
        }
        return super.createApplicationContext(application, flags);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        if (isLGEAppWidgetPackage(packageName)) {
            return new MyWidgetContext(super.createPackageContext(packageName, 3));
        }
        return super.createPackageContext(packageName, flags);
    }

    public Context createPackageContextAsUser(String packageName, int flags, UserHandle user) throws PackageManager.NameNotFoundException {
        if (isLGEAppWidgetPackage(packageName)) {
            return new MyWidgetContext(super.createPackageContextAsUser(packageName, 3, user));
        }
        return createPackageContextAsUser(packageName, flags, user);
    }

    public static boolean isLGEAppWidgetPackage(String packageName) {
        if (packageName == null) {
            return false;
        }
        return packageName.startsWith("com.lge.") || packageName.startsWith("com.android.calendar");
    }
}
