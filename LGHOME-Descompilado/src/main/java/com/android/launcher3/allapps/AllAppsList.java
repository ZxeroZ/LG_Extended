package com.android.launcher3.allapps;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.os.UserHandle;
import com.android.launcher3.AppFilter;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.util.FlagOp;
import com.android.launcher3.util.StringFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsList {
    public static final int DEFAULT_APPLICATIONS_NUMBER = 42;
    private AppFilter mAppFilter;
    private IconCache mIconCache;
    public ArrayList<AppInfo> data = new ArrayList<>(42);
    public ArrayList<AppInfo> added = new ArrayList<>(42);
    public ArrayList<AppInfo> removed = new ArrayList<>();
    public ArrayList<AppInfo> modified = new ArrayList<>();

    public AllAppsList(IconCache iconCache, AppFilter appFilter) {
        this.mIconCache = iconCache;
        this.mAppFilter = appFilter;
    }

    public void add(AppInfo info) {
        AppFilter appFilter = this.mAppFilter;
        if ((appFilter == null || appFilter.shouldShowApp(info.componentName, info.user)) && !findActivity(this.data, info.componentName, info.user)) {
            this.data.add(info);
            this.added.add(info);
        }
    }

    public void clear() {
        this.data.clear();
        this.added.clear();
        this.removed.clear();
        this.modified.clear();
    }

    public int size() {
        return this.data.size();
    }

    public AppInfo get(int index) {
        return this.data.get(index);
    }

    public void addPackage(Context context, String packageName, UserHandle user) {
        Iterator<LauncherActivityInfo> it = LauncherAppsCompat.getInstance(context).getActivityList(packageName, user).iterator();
        while (it.hasNext()) {
            add(new AppInfo(context, it.next(), user, this.mIconCache));
        }
    }

    public void removePackage(String packageName, UserHandle user) {
        ArrayList<AppInfo> arrayList = this.data;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            AppInfo appInfo = arrayList.get(size);
            ComponentName component = appInfo.intent.getComponent();
            if (appInfo.user.equals(user) && packageName.equals(component.getPackageName())) {
                this.removed.add(appInfo);
                arrayList.remove(size);
            }
        }
    }

    public void updatePackageFlags(StringFilter pkgFilter, UserHandle user, FlagOp op) {
        ArrayList<AppInfo> arrayList = this.data;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            AppInfo appInfo = arrayList.get(size);
            ComponentName component = appInfo.intent.getComponent();
            if (appInfo.user.equals(user) && pkgFilter.matches(component.getPackageName())) {
                appInfo.runtimeStatusFlags = op.apply(appInfo.runtimeStatusFlags);
                this.modified.add(appInfo);
            }
        }
    }

    public void updateIconsAndLabels(HashSet<String> packages, UserHandle user, ArrayList<AppInfo> outUpdates) {
        for (AppInfo appInfo : this.data) {
            if (appInfo.user.equals(user) && packages.contains(appInfo.componentName.getPackageName())) {
                this.mIconCache.updateTitleAndIcon(appInfo);
                outUpdates.add(appInfo);
            }
        }
    }

    public void updateIconAndLabel(String packageName, UserHandle user, ArrayList<AppInfo> outUpdates) {
        for (AppInfo appInfo : this.data) {
            if (appInfo.user.equals(user) && packageName.equals(appInfo.componentName.getPackageName())) {
                appInfo.iconBitmap = this.mIconCache.getIcon(appInfo.intent, appInfo.user);
                outUpdates.add(appInfo);
            }
        }
    }

    public void updatePackage(Context context, String packageName, UserHandle user) {
        List<LauncherActivityInfo> activityList = LauncherAppsCompat.getInstance(context).getActivityList(packageName, user);
        if (activityList.size() > 0) {
            for (int size = this.data.size() - 1; size >= 0; size--) {
                AppInfo appInfo = this.data.get(size);
                ComponentName component = appInfo.intent.getComponent();
                if (user.equals(appInfo.user) && packageName.equals(component.getPackageName()) && !findActivity(activityList, component)) {
                    this.removed.add(appInfo);
                    this.data.remove(size);
                }
            }
            for (LauncherActivityInfo launcherActivityInfo : activityList) {
                AppInfo appInfoFindApplicationInfoLocked = findApplicationInfoLocked(launcherActivityInfo.getComponentName().getPackageName(), user, launcherActivityInfo.getComponentName().getClassName());
                if (appInfoFindApplicationInfoLocked == null) {
                    add(new AppInfo(context, launcherActivityInfo, user, this.mIconCache));
                } else {
                    this.mIconCache.getTitleAndIcon(appInfoFindApplicationInfoLocked, launcherActivityInfo, true);
                    this.modified.add(appInfoFindApplicationInfoLocked);
                }
            }
            return;
        }
        for (int size2 = this.data.size() - 1; size2 >= 0; size2--) {
            AppInfo appInfo2 = this.data.get(size2);
            ComponentName component2 = appInfo2.intent.getComponent();
            if (user.equals(appInfo2.user) && packageName.equals(component2.getPackageName())) {
                this.removed.add(appInfo2);
                this.mIconCache.remove(component2, user);
                this.data.remove(size2);
            }
        }
    }

    private static boolean findActivity(List<LauncherActivityInfo> apps, ComponentName component) {
        Iterator<LauncherActivityInfo> it = apps.iterator();
        while (it.hasNext()) {
            if (it.next().getComponentName().equals(component)) {
                return true;
            }
        }
        return false;
    }

    public static boolean packageHasActivities(Context context, String packageName, UserHandle user) {
        return LauncherAppsCompat.getInstance(context).getActivityList(packageName, user).size() > 0;
    }

    private static boolean findActivity(ArrayList<AppInfo> apps, ComponentName component, UserHandle user) {
        int size = apps.size();
        for (int i = 0; i < size; i++) {
            AppInfo appInfo = apps.get(i);
            if (appInfo.user.equals(user) && appInfo.componentName.equals(component)) {
                return true;
            }
        }
        return false;
    }

    private AppInfo findApplicationInfoLocked(String packageName, UserHandle user, String className) {
        for (AppInfo appInfo : this.data) {
            ComponentName component = appInfo.intent.getComponent();
            if (user.equals(appInfo.user) && packageName.equals(component.getPackageName()) && className.equals(component.getClassName())) {
                return appInfo;
            }
        }
        return null;
    }
}
