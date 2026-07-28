package com.android.launcher3.model.data;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageManagerHelper;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.VplApps;
import java.util.ArrayList;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class AppInfo extends ItemInfoWithIcon {
    public static final int DOWNLOADED_FLAG = 1;
    private static final String PRELOADED_PATH = "/data/preload/";
    private static final String TAG = "Launcher3.AppInfo";
    public static final int UPDATED_SYSTEM_APP_FLAG = 2;
    public ComponentName componentName;
    public long firstInstallTime;
    public int flags;
    public Intent intent;
    public boolean isPreloadedApp;
    public boolean isSilentOTA;
    public boolean isSystem;

    protected Intent getRestoredIntent() {
        return null;
    }

    public AppInfo() {
        this.flags = 0;
        this.isPreloadedApp = false;
        this.itemType = 1;
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public Intent getIntent() {
        return this.intent;
    }

    public AppInfo(Context context, LauncherActivityInfo info, UserHandle user) {
        this(info, user, UserManagerCompat.getInstance(context).isQuietModeEnabled(user));
    }

    public AppInfo(LauncherActivityInfo info, UserHandle user, boolean quietModeEnabled) {
        this.flags = 0;
        this.isPreloadedApp = false;
        this.componentName = info.getComponentName();
        this.container = -1L;
        this.user = user;
        if (PackageManagerHelper.isAppSuspended(info.getApplicationInfo())) {
            this.runtimeStatusFlags |= 4;
        }
        if (quietModeEnabled) {
            this.runtimeStatusFlags |= 8;
        }
        this.intent = makeLaunchIntent(info);
    }

    public AppInfo(Context context, LauncherActivityInfo info, UserHandle user, IconCache iconCache) {
        this(context, info, user, iconCache, UserManagerCompat.getInstance(context).isQuietModeEnabled(user));
    }

    public AppInfo(Context context, LauncherActivityInfo info, UserHandle user, IconCache iconCache, boolean quietModeEnabled) {
        this.flags = 0;
        this.isPreloadedApp = false;
        this.componentName = info.getComponentName();
        this.container = -1L;
        this.flags = initFlags(info);
        setAdditionalInfo(context.getPackageManager(), this.flags, info);
        this.firstInstallTime = info.getFirstInstallTime();
        if (PackageManagerHelper.isAppSuspended(info.getApplicationInfo())) {
            this.runtimeStatusFlags |= 4;
        }
        if (quietModeEnabled) {
            this.runtimeStatusFlags |= 8;
        }
        this.intent = makeLaunchIntent(context, info, user);
        this.user = user;
        iconCache.getTitleAndIcon(this, info, true);
    }

    public static int initFlags(LauncherActivityInfo info) {
        int i = info.getApplicationInfo().flags;
        if ((i & 1) == 0) {
            return (i & 128) != 0 ? 3 : 1;
        }
        return 0;
    }

    public static int getFlagsForUninstalledPackage(PackageManager pm, ComponentName cn) {
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(cn.getPackageName(), 8192);
            if (applicationInfo != null) {
                return (applicationInfo.flags & 1) == 0 ? 1 : 0;
            }
            return 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public AppInfo(AppInfo info) {
        super(info);
        this.flags = 0;
        this.isPreloadedApp = false;
        this.componentName = info.componentName;
        this.title = Utilities.trim(info.title);
        this.intent = new Intent(info.intent);
        this.flags = info.flags;
        this.firstInstallTime = info.firstInstallTime;
        this.isPreloadedApp = info.isPreloadedApp;
        this.isSystem = info.isSystem;
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public String toString() {
        CharSequence charSequence = this.title;
        return "ApplicationInfo(title=" + ((Object) charSequence) + " id=" + this.id + " type=" + this.itemType + " container=" + this.container + " screen=" + this.screenId + " cellX=" + this.cellX + " cellY=" + this.cellY + " spanX=" + this.spanX + " spanY=" + this.spanY + " dropPos=" + Arrays.toString(this.dropPos) + " user=" + this.user + " swivelPosition=" + this.swivelPosition + ")";
    }

    public static void dumpApplicationInfoList(String tag, String label, ArrayList<AppInfo> list) {
        Log.d(tag, label + " size=" + list.size());
        for (AppInfo appInfo : list) {
            CharSequence charSequence = appInfo.title;
            Log.d(tag, "   title=\"" + ((Object) charSequence) + "\" iconBitmap=" + appInfo.iconBitmap + " firstInstallTime=" + appInfo.firstInstallTime + " componentName=" + appInfo.componentName.getPackageName());
        }
    }

    public ShortcutInfo makeShortcut() {
        return new ShortcutInfo(this);
    }

    public ComponentKey toComponentKey() {
        return new ComponentKey(this.componentName, this.user);
    }

    public static Intent makeLaunchIntent(LauncherActivityInfo info) {
        return new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("android.intent.category.LAUNCHER").setComponent(info.getComponentName()).setFlags(270532608);
    }

    public static Intent makeLaunchIntent(Context context, LauncherActivityInfo info, UserHandle user) {
        return new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("android.intent.category.LAUNCHER").setComponent(info.getComponentName()).setFlags(270532608).putExtra(ItemInfo.EXTRA_PROFILE, UserManagerCompat.getInstance(context).getSerialNumberForUser(user));
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddToClipData(Bundle bundle) {
        super.onAddToClipData(bundle);
        bundle.putParcelable(LauncherSettings.BaseLauncherColumns.INTENT, this.intent);
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddFromClipData(Bundle bundle) {
        super.onAddFromClipData(bundle);
        this.intent = (Intent) bundle.getParcelable(LauncherSettings.BaseLauncherColumns.INTENT);
    }

    public boolean isPreloadedApp() {
        return this.isPreloadedApp || VplApps.contains(this.componentName.getPackageName());
    }

    private void setPreloadedApp(PackageManager pm, LauncherActivityInfo info) {
        String installerPackageName;
        ApplicationInfo applicationInfo = info.getApplicationInfo();
        try {
            installerPackageName = pm.getInstallerPackageName(applicationInfo.packageName);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            installerPackageName = null;
        }
        if (installerPackageName == null) {
            this.isPreloadedApp = true;
            return;
        }
        String str = applicationInfo.sourceDir;
        if (str != null) {
            this.isPreloadedApp = str.contains(PRELOADED_PATH);
        }
        if (this.isPreloadedApp) {
            return;
        }
        this.isPreloadedApp = pm.checkPermission("com.lge.permission.TRUSTED_INSTALLER", installerPackageName) == 0;
    }

    public void setAdditionalInfo(PackageManager pm, int appFlags, LauncherActivityInfo info) {
        boolean z = (appFlags & 1) != 0;
        this.isSystem = z;
        if (z) {
            this.isPreloadedApp = true;
        } else {
            setPreloadedApp(pm, info);
        }
    }

    public void copyFrom(AppInfo info) {
        this.componentName = info.componentName;
        this.title = Utilities.trim(info.title);
        if (info.intent != null) {
            this.intent = new Intent(info.intent);
        } else {
            this.intent = new Intent();
        }
        this.flags = info.flags;
        this.runtimeStatusFlags = info.runtimeStatusFlags;
        this.firstInstallTime = info.firstInstallTime;
        this.iconBitmap = info.iconBitmap;
        this.usingLowResIcon = info.usingLowResIcon;
        this.isPreloadedApp = info.isPreloadedApp;
        this.isSystem = info.isSystem;
    }
}
