package com.android.launcher3.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.PackageUserKey;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class LauncherAppsCompat {
    public static final String ACTION_MANAGED_PROFILE_ADDED = "android.intent.action.MANAGED_PROFILE_ADDED";
    public static final String ACTION_MANAGED_PROFILE_AVAILABLE = "android.intent.action.MANAGED_PROFILE_AVAILABLE";
    public static final String ACTION_MANAGED_PROFILE_REMOVED = "android.intent.action.MANAGED_PROFILE_REMOVED";
    public static final String ACTION_MANAGED_PROFILE_UNAVAILABLE = "android.intent.action.MANAGED_PROFILE_UNAVAILABLE";
    public static final String ACTION_MANAGED_PROFILE_UNLOCKED = "android.intent.action.MANAGED_PROFILE_UNLOCKED";
    private static LauncherAppsCompat sInstance;
    private static Object sInstanceLock = new Object();

    public interface OnAppsChangedCallbackCompat {
        void onPackageAdded(String packageName, UserHandle user);

        void onPackageChanged(String packageName, UserHandle user);

        void onPackageRemoved(String packageName, UserHandle user);

        void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing);

        void onPackagesSuspended(String[] packageNames, UserHandle user);

        void onPackagesUnavailable(String[] packageNames, UserHandle user, boolean replacing);

        void onPackagesUnsuspended(String[] packageNames, UserHandle user);

        void onShortcutsChanged(String packageName, List<ShortcutInfoCompat> shortcuts, UserHandle user);
    }

    public abstract void addOnAppsChangedCallback(OnAppsChangedCallbackCompat listener);

    public abstract List<LauncherActivityInfo> getActivityList(String packageName, UserHandle user);

    public abstract ApplicationInfo getApplicationInfo(String packageName, int flags, UserHandle user);

    public abstract List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(PackageUserKey packageUser);

    public abstract boolean isActivityEnabledForProfile(ComponentName component, UserHandle user);

    public abstract boolean isPackageEnabledForProfile(String packageName, UserHandle user);

    public abstract boolean isPackageSuspendedForProfile(String packageName, UserHandle user);

    public abstract void removeOnAppsChangedCallback(OnAppsChangedCallbackCompat listener);

    public abstract LauncherActivityInfo resolveActivity(Intent intent, UserHandle user);

    public abstract void showAppDetailsForProfile(ComponentName component, UserHandle user, Rect sourceBounds, Bundle opts);

    public abstract void startActivityForProfile(ComponentName component, UserHandle user, Rect sourceBounds, Bundle opts);

    protected LauncherAppsCompat() {
    }

    public static LauncherAppsCompat getInstance(Context context) {
        LauncherAppsCompat launcherAppsCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                if (Utilities.isAtLeastO()) {
                    sInstance = new LauncherAppsCompatVO(context.getApplicationContext());
                } else {
                    sInstance = new LauncherAppsCompatVL(context.getApplicationContext());
                }
            }
            launcherAppsCompat = sInstance;
        }
        return launcherAppsCompat;
    }

    public static ShortcutInfo createShortcutInfoFromPinItemRequest(Context context, final PinItemRequestCompat request, final long acceptDelay) {
        if (request == null || request.getRequestType() != 1 || !request.isValid()) {
            return null;
        }
        if (acceptDelay <= 0) {
            if (!request.accept()) {
                return null;
            }
        } else {
            new LooperExecutor(LauncherModel.getWorkerLooper()).execute(new Runnable() { // from class: com.android.launcher3.compat.LauncherAppsCompat.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Thread.sleep(acceptDelay);
                    } catch (InterruptedException unused) {
                    }
                    if (request.isValid()) {
                        request.accept();
                    }
                }
            });
        }
        ShortcutInfoCompat shortcutInfoCompat = new ShortcutInfoCompat(request.getShortcutInfo());
        ShortcutInfo shortcutInfo = new ShortcutInfo(shortcutInfoCompat, context);
        shortcutInfo.iconBitmap = LauncherIcons.createShortcutIcon(shortcutInfoCompat, context, false);
        LauncherAppState.getInstance(context).getModel().updateAndBindShortcutInfo(shortcutInfo, shortcutInfoCompat);
        return shortcutInfo;
    }

    public void showAppDetailsForProfile(ComponentName component, UserHandle user) {
        showAppDetailsForProfile(component, user, null, null);
    }

    public boolean isAppEnabled(PackageManager pm, String packageName, int flags) {
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
}
