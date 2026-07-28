package com.lge.launcher3.recentuninstall;

import android.content.Context;
import android.os.UserHandle;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class RUProgressListManager implements LauncherAppsCompat.OnAppsChangedCallbackCompat {
    public static final String TAG = "RUProgressListManager";
    private static RUProgressListManager sRUProgressManager;
    private Context mContext;
    private ArrayList<String> mUninstallProgressList = new ArrayList<>();
    private PackageChangedCallback mCallback = null;

    public interface PackageChangedCallback {
        void onPackageRemoved(String packageName);
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackageAdded(String packageName, UserHandle user) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackageChanged(String packageName, UserHandle user) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesSuspended(String[] packageNames, UserHandle user) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesUnavailable(String[] packageNames, UserHandle user, boolean replacing) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesUnsuspended(String[] packageNames, UserHandle user) {
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onShortcutsChanged(String packageName, List<ShortcutInfoCompat> shortcuts, UserHandle user) {
    }

    public static RUProgressListManager getInstance(Context context) {
        if (sRUProgressManager == null) {
            sRUProgressManager = new RUProgressListManager(context);
        }
        return sRUProgressManager;
    }

    private RUProgressListManager(Context context) {
        this.mContext = null;
        this.mContext = context;
        LauncherAppsCompat.getInstance(context).addOnAppsChangedCallback(this);
    }

    public void setPackageChangedCallback(PackageChangedCallback callback) {
        this.mCallback = callback;
    }

    public void addUninstallProgress(String packageName) {
        if (this.mUninstallProgressList.contains(packageName)) {
            return;
        }
        this.mUninstallProgressList.add(packageName);
    }

    public boolean contains(String packageName) {
        return this.mUninstallProgressList.contains(packageName);
    }

    public ArrayList<String> getList() {
        return this.mUninstallProgressList;
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackageRemoved(String packageName, UserHandle user) {
        LGLog.i(TAG, String.format("onPackageRemoved() : packageName(%s)", packageName));
        this.mUninstallProgressList.remove(packageName);
        PackageChangedCallback packageChangedCallback = this.mCallback;
        if (packageChangedCallback != null) {
            packageChangedCallback.onPackageRemoved(packageName);
        }
    }
}
