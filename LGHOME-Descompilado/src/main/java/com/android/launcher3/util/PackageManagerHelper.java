package com.android.launcher3.util;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.PromiseAppInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.util.PackageUtils;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PackageManagerHelper {
    private static final int FLAG_SUSPENDED = 1073741824;
    private static final String TAG = "PackageManagerHelper";
    private final Context mContext;
    private final LauncherAppsCompat mLauncherApps;
    private final PackageManager mPm;

    public PackageManagerHelper(Context context) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mLauncherApps = LauncherAppsCompat.getInstance(context);
    }

    public boolean isAppOnSdcard(String packageName, UserHandle user) {
        ApplicationInfo applicationInfo = this.mLauncherApps.getApplicationInfo(packageName, 8192, user);
        return (applicationInfo == null || (applicationInfo.flags & 262144) == 0) ? false : true;
    }

    public static boolean isAppEnabled(PackageManager pm, String packageName) {
        return isAppEnabled(pm, packageName, 0);
    }

    public static boolean isAppEnabled(PackageManager pm, String packageName, int flags) {
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, flags);
            if (applicationInfo != null) {
                return applicationInfo.enabled;
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static boolean isAppSuspended(PackageManager pm, String packageName) {
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                return isAppSuspended(applicationInfo);
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static boolean isAppSuspended(ApplicationInfo info) {
        return Utilities.isNycOrAbove() && (info.flags & FLAG_SUSPENDED) != 0;
    }

    public boolean isAppSuspended(String packageName, UserHandle user) {
        ApplicationInfo applicationInfo = this.mLauncherApps.getApplicationInfo(packageName, 0, user);
        return applicationInfo != null && isAppSuspended(applicationInfo);
    }

    public ApplicationInfo getApplicationInfo(String packageName, UserHandle user, int flags) {
        if (Utilities.ATLEAST_OREO) {
            ApplicationInfo applicationInfo = this.mLauncherApps.getApplicationInfo(packageName, flags, user);
            if (applicationInfo == null || (applicationInfo.flags & 8388608) == 0 || !applicationInfo.enabled) {
                return null;
            }
            return applicationInfo;
        }
        boolean zEquals = Process.myUserHandle().equals(user);
        if (!zEquals && flags == 0) {
            List<LauncherActivityInfo> activityList = this.mLauncherApps.getActivityList(packageName, user);
            if (activityList.size() > 0) {
                return activityList.get(0).getApplicationInfo();
            }
            return null;
        }
        try {
            ApplicationInfo applicationInfo2 = this.mPm.getApplicationInfo(packageName, flags);
            if (!zEquals || (applicationInfo2.flags & 8388608) != 0) {
                if (applicationInfo2.enabled) {
                    return applicationInfo2;
                }
            }
            return null;
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public boolean isSafeMode() {
        return this.mContext.getPackageManager().isSafeMode();
    }

    public static boolean hasPermissionForActivity(Context context, Intent intent, String srcPackage) {
        PackageManager packageManager = context.getPackageManager();
        ResolveInfo resolveInfoResolveActivity = packageManager.resolveActivity(intent, 0);
        if (resolveInfoResolveActivity == null) {
            return false;
        }
        if (TextUtils.isEmpty(resolveInfoResolveActivity.activityInfo.permission)) {
            return true;
        }
        if (TextUtils.isEmpty(srcPackage) || packageManager.checkPermission(resolveInfoResolveActivity.activityInfo.permission, srcPackage) != 0) {
            return false;
        }
        if (!Utilities.ATLEAST_MARSHMALLOW || TextUtils.isEmpty(AppOpsManager.permissionToOp(resolveInfoResolveActivity.activityInfo.permission))) {
            return true;
        }
        try {
            return packageManager.getApplicationInfo(srcPackage, 0).targetSdkVersion >= 23;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public boolean hasPermissionForActivity(Intent intent, String srcPackage) {
        ResolveInfo resolveInfoResolveActivity = this.mPm.resolveActivity(intent, 0);
        if (resolveInfoResolveActivity == null) {
            return false;
        }
        if (TextUtils.isEmpty(resolveInfoResolveActivity.activityInfo.permission)) {
            return true;
        }
        if (TextUtils.isEmpty(srcPackage) || this.mPm.checkPermission(resolveInfoResolveActivity.activityInfo.permission, srcPackage) != 0) {
            return false;
        }
        if (!Utilities.ATLEAST_MARSHMALLOW || TextUtils.isEmpty(AppOpsManager.permissionToOp(resolveInfoResolveActivity.activityInfo.permission))) {
            return true;
        }
        try {
            return this.mPm.getApplicationInfo(srcPackage, 0).targetSdkVersion >= 23;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public Intent getMarketIntent(String packageName) {
        return new Intent("android.intent.action.VIEW").setData(new Uri.Builder().scheme("market").authority("details").appendQueryParameter("id", packageName).build()).putExtra("android.intent.extra.REFERRER", new Uri.Builder().scheme("android-app").authority(this.mContext.getPackageName()).build());
    }

    public static Intent getMarketSearchIntent(Context context, String query) {
        try {
            Intent uri = Intent.parseUri(context.getString(R.string.market_search_intent), 0);
            if (!TextUtils.isEmpty(query)) {
                uri.setData(uri.getData().buildUpon().appendQueryParameter("q", query).build());
            }
            return uri;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void startDetailsActivityForInfo(ItemInfo info, Rect sourceBounds, Bundle opts) {
        if (info instanceof PromiseAppInfo) {
            Context context = this.mContext;
            context.startActivity(((PromiseAppInfo) info).getMarketIntent(context));
            return;
        }
        ComponentName targetComponent = null;
        if (info instanceof AppInfo) {
            targetComponent = ((AppInfo) info).componentName;
        } else if (info instanceof ShortcutInfo) {
            targetComponent = info.getTargetComponent();
        } else if (info instanceof PendingAddItemInfo) {
            targetComponent = ((PendingAddItemInfo) info).componentName;
        } else if (info instanceof LauncherAppWidgetInfo) {
            targetComponent = ((LauncherAppWidgetInfo) info).providerName;
        } else if (info instanceof WorkspaceItemInfo) {
            targetComponent = ((WorkspaceItemInfo) info).getTargetComponent();
        }
        if (targetComponent != null) {
            try {
                this.mLauncherApps.showAppDetailsForProfile(targetComponent, info.user, sourceBounds, opts);
            } catch (ActivityNotFoundException | SecurityException e) {
                Toast.makeText(this.mContext, R.string.activity_not_found, 0).show();
                Log.e(TAG, "Unable to launch settings", e);
            }
        }
    }

    public static IntentFilter getPackageFilter(String pkg, String... actions) {
        IntentFilter intentFilter = new IntentFilter();
        for (String str : actions) {
            intentFilter.addAction(str);
        }
        intentFilter.addDataScheme(AppNotifierManager.ExtraSpec.USAGE_PACKAGE);
        intentFilter.addDataSchemeSpecificPart(pkg, 0);
        return intentFilter;
    }

    public static boolean isSystemApp(Context context, Intent intent) {
        String packageName;
        PackageManager packageManager = context.getPackageManager();
        ComponentName component = intent.getComponent();
        if (component == null) {
            ResolveInfo resolveInfoResolveActivity = packageManager.resolveActivity(intent, 65536);
            packageName = (resolveInfoResolveActivity == null || resolveInfoResolveActivity.activityInfo == null) ? null : resolveInfoResolveActivity.activityInfo.packageName;
        } else {
            packageName = component.getPackageName();
        }
        if (packageName == null) {
            packageName = intent.getPackage();
        }
        if (packageName == null) {
            return false;
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if (packageInfo == null || packageInfo.applicationInfo == null) {
                return false;
            }
            return (packageInfo.applicationInfo.flags & 1) != 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static Pair<String, Resources> findSystemApk(String action, PackageManager pm) {
        Iterator<ResolveInfo> it = pm.queryBroadcastReceivers(new Intent(action), 1048576).iterator();
        while (it.hasNext()) {
            String str = it.next().activityInfo.packageName;
            try {
                return Pair.create(str, pm.getResourcesForApplication(str));
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w(TAG, "Failed to find resources for " + str);
            }
        }
        return null;
    }

    public static boolean isLauncherAppTarget(Intent launchIntent) {
        if (launchIntent == null || !PackageUtils.ANDROID_INTENT_ACTION_MAIN.equals(launchIntent.getAction()) || launchIntent.getComponent() == null || launchIntent.getCategories() == null || launchIntent.getCategories().size() != 1 || !launchIntent.hasCategory("android.intent.category.LAUNCHER") || !TextUtils.isEmpty(launchIntent.getDataString())) {
            return false;
        }
        Bundle extras = launchIntent.getExtras();
        return extras == null || extras.keySet().isEmpty();
    }

    public static boolean hasShortcutsPermission(Context context) {
        try {
            return ((LauncherApps) context.getSystemService(LauncherApps.class)).hasShortcutHostPermission();
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to make shortcut manager call", e);
            return false;
        }
    }
}
