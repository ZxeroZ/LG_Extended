package com.lge.launcher3.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Process;
import com.android.internal.app.ResolverActivity;
import com.android.launcher3.LauncherModel;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PackageUtils {
    public static final String ANDROID_INTENT_ACTION_MAIN = "android.intent.action.MAIN";
    public static final String ANDROID_INTENT_CATEGORY_HOME = "android.intent.category.HOME";
    private static final int DEFAULT_COVER_DISPLAY_CHECKER = 2;
    private static final int SECONDARY_COVER_DISPLAY_CHECKER = 16;
    private static final String TAG = "PackageUtils";

    public static ApplicationInfo getApplicationInfo(Context context, String packageName) {
        if (context == null || packageName == null) {
            LGLog.e(TAG, String.format("Invalid : context(%s), packageName(%s)", context, packageName));
            return null;
        }
        try {
            return context.getPackageManager().getApplicationInfo(packageName, 128);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.w(TAG, String.format("NameNotFoundException(%s)", e.getMessage()), new int[0]);
            return null;
        } catch (Exception e2) {
            LGLog.w(TAG, String.format("Exception(%s)", e2.getMessage()), new int[0]);
            return null;
        }
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        return getApplicationInfo(context, packageName) != null;
    }

    public static boolean isPackageUninstalled(Context context, String packageName) {
        return !isPackageInstalled(context, packageName);
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.w(TAG, String.format("NameNotFoundException(%s)", e.getMessage()), new int[0]);
            return null;
        } catch (NullPointerException e2) {
            LGLog.w(TAG, String.format("NullPointerException(%s)", e2.getMessage()), new int[0]);
            return null;
        }
    }

    public static String getPackageName(Context context, Intent intent) {
        return getPackageName(context.getPackageManager().resolveActivity(intent, 65536));
    }

    public static String getPackageName(ResolveInfo resolveInfo) {
        if (resolveInfo == null) {
            return null;
        }
        ComponentInfo componentInfo = resolveInfo.activityInfo;
        if (componentInfo == null) {
            componentInfo = resolveInfo.serviceInfo;
        }
        return componentInfo.packageName;
    }

    public static void setPrefHomeSetting(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null || packageName == null) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter("com.lge.launcher3.intent.action.SHOW_SETTING");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        List<ResolveInfo> listQueryIntentActivities = packageManager.queryIntentActivities(new Intent("com.lge.launcher3.intent.action.SHOW_SETTING"), 65600);
        int size = listQueryIntentActivities.size();
        ComponentName[] componentNameArr = new ComponentName[size];
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ResolveInfo resolveInfo = listQueryIntentActivities.get(i2);
            componentNameArr[i2] = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
            if (resolveInfo.match > i) {
                i = resolveInfo.match;
            }
        }
        try {
            packageManager.replacePreferredActivity(intentFilter, i, componentNameArr, new ComponentName(packageName, packageName + ".homesettings.HomeSettingsPrefActivity"));
        } catch (SecurityException unused) {
            LGLog.d(TAG, "relpacePreferredActivity security");
        }
    }

    public static void enableRecentUninstall(Context context, String packageName) {
        ComponentName componentName = new ComponentName(context, "com.lge.launcher3.CreateShortcuts");
        PackageManager packageManager = context.getPackageManager();
        if (packageName.equals(context.getPackageName()) && LGHomeFeature.Config.FEATURE_USE_RECENT_UNINSTALL_APP.getValue() && !ManagedProfileUtils.isAFW(context)) {
            if (packageManager.getComponentEnabledSetting(componentName) == 1) {
                return;
            }
            packageManager.setComponentEnabledSetting(componentName, 1, 1);
            LGLog.d(TAG, "CreateShortcuts activity-alias is enabled by " + packageName);
            return;
        }
        if (packageManager.getComponentEnabledSetting(componentName) == 2) {
            return;
        }
        packageManager.setComponentEnabledSetting(componentName, 2, 1);
        LGLog.d(TAG, "CreateShortcuts activity-alias is disabled by " + packageName);
    }

    public static Intent getHomeActivityIntent() {
        Intent intent = new Intent();
        intent.setAction(ANDROID_INTENT_ACTION_MAIN);
        intent.addCategory(ANDROID_INTENT_CATEGORY_HOME);
        return intent;
    }

    public static ResolveInfo getDefaultHomeActivityResolveInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            LGLog.w(TAG, "PackageManager is null", new int[0]);
            return null;
        }
        return packageManager.resolveActivity(getHomeActivityIntent(), 65536);
    }

    public static List<ResolveInfo> getActivityResolveInfoList(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            LGLog.w(TAG, "PackageManager is null", new int[0]);
            return null;
        }
        return packageManager.queryIntentActivities(intent, 65600);
    }

    public static boolean isResolverActivity(ResolveInfo resolveInfo) {
        if (resolveInfo == null || resolveInfo.activityInfo == null || resolveInfo.activityInfo.name == null) {
            return false;
        }
        return ResolverActivity.class.getName().equals(resolveInfo.activityInfo.name);
    }

    public static boolean isSafeMode(Context context) {
        PackageManager packageManager;
        if (context == null || (packageManager = context.getPackageManager()) == null) {
            return false;
        }
        return packageManager.isSafeMode();
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x002a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean isEnableBackupRestore(android.content.Context r6) {
        /*
            android.content.pm.ResolveInfo r0 = getDefaultHomeActivityResolveInfo(r6)
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L2a
            java.lang.String r3 = com.lge.launcher3.util.PackageUtils.TAG
            java.lang.Object[] r4 = new java.lang.Object[r2]
            r4[r1] = r0
            java.lang.String r5 = "isEnableBackupRestore() : defHomeResolveInfo(%s)"
            java.lang.String r4 = java.lang.String.format(r5, r4)
            com.lge.launcher3.util.LGLog.i(r3, r4)
            boolean r3 = isResolverActivity(r0)
            if (r3 != 0) goto L2a
            android.content.pm.ActivityInfo r0 = r0.activityInfo
            java.lang.String r0 = r0.packageName
            java.lang.String r3 = "com.lge.launcher2"
            boolean r0 = r0.startsWith(r3)
            if (r0 == 0) goto L2a
            goto L2b
        L2a:
            r1 = r2
        L2b:
            boolean r6 = getBackupRestoreFeatureforLGHome4(r6)
            if (r6 == 0) goto L32
            return r1
        L32:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.util.PackageUtils.isEnableBackupRestore(android.content.Context):boolean");
    }

    public static boolean getBackupRestoreFeatureforLGHome4(Context context) {
        Uri uri = Uri.parse("android.resource://com.lge.launcher2/bool/config_feature_use_backup_restore");
        String path = uri.getPath();
        String host = uri.getHost();
        String[] strArrSplit = path.split("/");
        String str = strArrSplit[1];
        String str2 = strArrSplit[2];
        if (!str.equalsIgnoreCase("bool")) {
            return false;
        }
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(host);
            return resourcesForApplication.getBoolean(resourcesForApplication.getIdentifier(str2, str, host));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (Resources.NotFoundException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static String getApplicationLabel(Context context, String packagename) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.w(TAG, String.format("NameNotFoundException(%s)", e.getMessage()), new int[0]);
            applicationInfo = null;
        } catch (Exception e2) {
            if (ManagedProfileUtils.hasProfileOwner(context)) {
                try {
                    context.getPackageManager().getPackageInfo(packagename, 8192);
                } catch (Exception e3) {
                    LGLog.w(TAG, String.format("getApplicationLabel1 Exception(%s)", e3.getMessage()), new int[0]);
                }
            } else {
                LGLog.w(TAG, String.format("getApplicationLabel2 Exception(%s)", e2.getMessage()), new int[0]);
            }
            applicationInfo = null;
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");
    }

    public static String getApplicationLabelFromResolveInfo(Context context, ComponentName componentName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(ANDROID_INTENT_ACTION_MAIN, (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent, 128)) {
            if (resolveInfo.activityInfo.packageName.equals(componentName.getPackageName()) && resolveInfo.activityInfo.name.equals(componentName.getClassName())) {
                return resolveInfo.loadLabel(packageManager).toString();
            }
        }
        return context.getString(R.string.package_state_unknown);
    }

    public static Drawable getApplicationIcon(Context context, String packagename) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.w(TAG, String.format("NameNotFoundException(%s)", e.getMessage()), new int[0]);
            applicationInfo = null;
        } catch (Exception e2) {
            if (ManagedProfileUtils.hasProfileOwner(context)) {
                try {
                    context.getPackageManager().getPackageInfo(packagename, 8192);
                } catch (Exception e3) {
                    LGLog.w(TAG, String.format("getApplicationLabel1 Exception(%s)", e3.getMessage()), new int[0]);
                }
            } else {
                LGLog.w(TAG, String.format("getApplicationLabel2 Exception(%s)", e2.getMessage()), new int[0]);
            }
            applicationInfo = null;
        }
        if (applicationInfo != null) {
            return applicationInfo.loadIcon(packageManager);
        }
        return null;
    }

    public static boolean isPackageOnSdcard(Context context, String packageName) {
        HashSet<String> hashSet = LauncherModel.sPendingPackages.get(Process.myUserHandle());
        if (hashSet != null && hashSet.contains(packageName)) {
            return true;
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 8192);
            if (applicationInfo == null) {
                return false;
            }
            return applicationInfo.isExternalAsec();
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.w(TAG, String.format("NameNotFoundException(%s)", e.getMessage()), new int[0]);
            return false;
        } catch (Exception e2) {
            LGLog.w(TAG, String.format("Exception(%s)", e2.getMessage()), new int[0]);
            return false;
        }
    }

    private static ComponentName[] buildHomeActivitiesList(Context context) {
        ArrayList arrayList = new ArrayList();
        ComponentName defaultHome = getDefaultHome(context, arrayList);
        LGLog.i(TAG, "currentDefaultHome = " + defaultHome);
        ComponentName[] componentNameArr = new ComponentName[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            ActivityInfo activityInfo = ((ResolveInfo) arrayList.get(i)).activityInfo;
            componentNameArr[i] = new ComponentName(activityInfo.packageName, activityInfo.name);
        }
        return componentNameArr;
    }

    public static ComponentName getDefaultHome(Context context, ArrayList<ResolveInfo> homeActivities) {
        return context.getPackageManager().getHomeActivities(homeActivities);
    }

    public static void setDefaultHome(Context context, ComponentName defHome) {
        IntentFilter intentFilter = new IntentFilter(ANDROID_INTENT_ACTION_MAIN);
        intentFilter.addCategory(ANDROID_INTENT_CATEGORY_HOME);
        intentFilter.addCategory("android.intent.category.DEFAULT");
        context.getPackageManager().replacePreferredActivity(intentFilter, 1048576, buildHomeActivitiesList(context), defHome);
    }

    public static boolean isPackageExisted(String targetPackage, Context mContext) {
        try {
            mContext.getPackageManager().getPackageInfo(targetPackage, 128);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static boolean isExceptDeviceProfileAppWidgetPackage(String packageName, String className) {
        if (packageName != null && className != null) {
            if (packageName.startsWith("com.android.chrome")) {
                if (className.contains("search")) {
                    return true;
                }
            } else if (packageName.startsWith("com.amazon.widgets")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIncludeValidPackage(Context context, String pkgName) {
        for (String str : context.getResources().getStringArray(R.array.include_valid_package)) {
            if (str.equals(pkgName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isUse_Activities_on_secondary(PackageManager packageManager, String packageName) {
        boolean z = false;
        if (packageManager == null || packageName == null) {
            LGLog.i(TAG, "isUse_Activities_on_secondary : packageManager = " + packageManager + ", packageName = " + packageName);
            return false;
        }
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 128);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.w(TAG, String.format("isUse_Activities_on_secondary : NameNotFoundException(%s)", e.getMessage()), new int[0]);
        } catch (Exception e2) {
            LGLog.w(TAG, String.format("isUse_Activities_on_secondary : Exception(%s)", e2.getMessage()), new int[0]);
        }
        int i = LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() ? 16 : 2;
        if (applicationInfo != null && (i & applicationInfo.restrictMultiDs) != 0) {
            z = true;
        }
        if (z) {
            LGLog.i(TAG, "isUse_Activities_on_secondary = " + z + ", packageName = " + packageName + ", applicationInfo = " + applicationInfo);
        }
        return z;
    }
}
