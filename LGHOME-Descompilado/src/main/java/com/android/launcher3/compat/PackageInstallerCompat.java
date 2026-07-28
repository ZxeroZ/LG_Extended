package com.android.launcher3.compat;

import android.content.Context;
import com.android.launcher3.Utilities;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public abstract class PackageInstallerCompat {
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_INSTALLED = 0;
    public static final int STATUS_INSTALLING = 1;
    private static PackageInstallerCompat sInstance;
    private static final Object sInstanceLock = new Object();
    final HashMap<String, Integer> mActivePackagesExceptAppBoxInstaller = new HashMap<>();

    public abstract void onStop();

    public abstract HashMap<String, Integer> updateAndGetActiveSessionCache();

    public static PackageInstallerCompat getInstance(Context context) {
        PackageInstallerCompat packageInstallerCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                if (Utilities.isLmpOrAbove()) {
                    sInstance = new PackageInstallerCompatVL(context);
                } else {
                    sInstance = new PackageInstallerCompatV16();
                }
            }
            packageInstallerCompat = sInstance;
        }
        return packageInstallerCompat;
    }

    public HashMap<String, Integer> getActiveSessionExceptAppBoxInstaller() {
        return this.mActivePackagesExceptAppBoxInstaller;
    }

    public static final class PackageInstallInfo {
        public final String packageName;
        public int progress;
        public int state;

        public PackageInstallInfo(String packageName) {
            this.packageName = packageName;
        }

        public PackageInstallInfo(String packageName, int state, int progress) {
            this.packageName = packageName;
            this.state = state;
            this.progress = progress;
        }
    }
}
