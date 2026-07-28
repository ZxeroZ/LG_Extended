package com.lge.launcher3.util;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.mdm.LGMDMManagerInternal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ManagedProfileUtils {
    private static final String TAG = "ManagedProfileUtils";

    public static boolean isAFW(Context context) {
        return hasDeviceOwner(context) || hasProfileOwner(context);
    }

    public static boolean hasDeviceOwner(Context context) {
        return !android.text.TextUtils.isEmpty(((DevicePolicyManager) context.getSystemService("device_policy")).getDeviceOwner());
    }

    public static boolean hasProfileOwner(Context context) {
        Iterator<UserHandle> it = ((UserManager) context.getSystemService("user")).getUserProfiles().iterator();
        while (it.hasNext()) {
            if (hasProfileOwnerAsUser(context, it.next().getIdentifier())) {
                LGLog.d(TAG, "hasProfileOwner");
                return true;
            }
        }
        return false;
    }

    public static boolean isUninstallBlocked(Context context, String packageName) {
        if (!((DevicePolicyManager) context.getSystemService("device_policy")).isUninstallBlocked(null, packageName)) {
            return false;
        }
        LGLog.d(TAG, "Uninstall blocked by DevicePolicyManager " + packageName);
        return true;
    }

    public static boolean hasUserRestriction(Context context) {
        if (!((UserManager) context.getSystemService("user")).hasUserRestriction("no_uninstall_apps")) {
            return false;
        }
        LGLog.d(TAG, "UserRestriction by UserManager ");
        return true;
    }

    public static boolean hasProfileOwnerAsUser(Context context, int userId) {
        return ((DevicePolicyManager) context.getSystemService("device_policy")).getProfileOwnerAsUser(userId) != null;
    }

    public static boolean isAdminApplication(Context context, ComponentName componentName) {
        List<ComponentName> activeAdmins = ((DevicePolicyManager) context.getSystemService("device_policy")).getActiveAdmins();
        if (activeAdmins != null && componentName != null) {
            for (ComponentName componentName2 : activeAdmins) {
                if (componentName2.getPackageName() != null && componentName2.getPackageName().equals(componentName.getPackageName())) {
                    LGLog.d(TAG, "ActiveAdmin : " + componentName.getPackageName());
                    return true;
                }
            }
        }
        return false;
    }

    public static HashSet<String> getAdminPackageList(Context context) {
        HashSet<String> hashSet = new HashSet<>();
        List<ComponentName> activeAdmins = ((DevicePolicyManager) context.getSystemService("device_policy")).getActiveAdmins();
        if (activeAdmins != null) {
            Iterator<ComponentName> it = activeAdmins.iterator();
            while (it.hasNext()) {
                hashSet.add(it.next().getPackageName());
            }
        }
        return hashSet;
    }

    public static boolean isAdminApplication(Context context, String packageName) {
        List<ComponentName> activeAdmins = ((DevicePolicyManager) context.getSystemService("device_policy")).getActiveAdmins();
        if (activeAdmins == null) {
            return false;
        }
        Iterator<ComponentName> it = activeAdmins.iterator();
        while (it.hasNext()) {
            if (it.next().getPackageName().equals(packageName)) {
                LGLog.d(TAG, "ActiveAdmin : " + packageName);
                return true;
            }
        }
        return false;
    }

    public static boolean isLGMDMAppNotAllowUninstall(Context context, ComponentName componentName) {
        String packageName = componentName.getPackageName();
        LGMDMManagerInternal lGMDMManagerInternal = LGMDMManagerInternal.getInstance();
        if (lGMDMManagerInternal == null || lGMDMManagerInternal.checkUninstallAllow((ComponentName) null, packageName, UserHandle.myUserId())) {
            return false;
        }
        LGLog.d(TAG, "LGMDMAppVersionState not allow uninstall : " + componentName);
        return true;
    }

    public static boolean isLGMDMAppNotAllowUninstall(Context context, String packageName) {
        LGMDMManagerInternal lGMDMManagerInternal = LGMDMManagerInternal.getInstance();
        if (lGMDMManagerInternal == null || lGMDMManagerInternal.checkUninstallAllow((ComponentName) null, packageName, UserHandle.myUserId())) {
            return false;
        }
        LGLog.d(TAG, "LGMDMAppVersionState not allow uninstall : " + packageName);
        return true;
    }

    public static boolean skipInitialguideForDeviceProfile(Context context) {
        if (!LGHomeFeature.Config.FEATURE_USE_SKIP_INITIALGUIDE_FOR_DEVICE_OWNER.getValue() || !hasDeviceOwner(context)) {
            return false;
        }
        LGLog.i(TAG, "skip Initialguide because DO is enabled");
        return true;
    }
}
