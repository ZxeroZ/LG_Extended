package com.android.launcher3.compat;

import android.content.Context;
import android.content.pm.PackageInstaller;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import android.util.SparseArray;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.icons.IconCache;
import com.lge.launcher3.R;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class PackageInstallerCompatVL extends PackageInstallerCompat {
    final SparseArray<String> mActiveSessions = new SparseArray<>();
    private String mAppBoxInstallerPackageName;
    private final IconCache mCache;
    private final PackageInstaller.SessionCallback mCallback;
    final PackageInstaller mInstaller;
    private final Handler mWorker;

    PackageInstallerCompatVL(Context context) {
        this.mAppBoxInstallerPackageName = "com.lge.appbox.client";
        PackageInstaller.SessionCallback sessionCallback = new PackageInstaller.SessionCallback() { // from class: com.android.launcher3.compat.PackageInstallerCompatVL.1
            @Override // android.content.pm.PackageInstaller.SessionCallback
            public void onActiveChanged(int sessionId, boolean active) {
            }

            @Override // android.content.pm.PackageInstaller.SessionCallback
            public void onCreated(int sessionId) {
                pushSessionDisplayToLauncher(sessionId);
            }

            @Override // android.content.pm.PackageInstaller.SessionCallback
            public void onFinished(int sessionId, boolean success) {
                String str = PackageInstallerCompatVL.this.mActiveSessions.get(sessionId);
                PackageInstallerCompatVL.this.mActiveSessions.remove(sessionId);
                if (str != null) {
                    PackageInstallerCompatVL.this.sendUpdate(new PackageInstallerCompat.PackageInstallInfo(str, success ? 0 : 2, 0));
                }
            }

            @Override // android.content.pm.PackageInstaller.SessionCallback
            public void onProgressChanged(int sessionId, float progress) {
                PackageInstaller.SessionInfo sessionInfo = PackageInstallerCompatVL.this.mInstaller.getSessionInfo(sessionId);
                if (sessionInfo != null) {
                    PackageInstallerCompatVL.this.sendUpdate(new PackageInstallerCompat.PackageInstallInfo(sessionInfo.getAppPackageName(), 1, (int) (sessionInfo.getProgress() * 100.0f)));
                }
            }

            @Override // android.content.pm.PackageInstaller.SessionCallback
            public void onBadgingChanged(int sessionId) {
                pushSessionDisplayToLauncher(sessionId);
            }

            private void pushSessionDisplayToLauncher(int sessionId) {
                PackageInstaller.SessionInfo sessionInfo = PackageInstallerCompatVL.this.mInstaller.getSessionInfo(sessionId);
                if (sessionInfo != null) {
                    PackageInstallerCompatVL.this.addSessionInfoToCahce(sessionInfo, Process.myUserHandle());
                    LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
                    if (instanceNoCreate != null) {
                        instanceNoCreate.getModel().updateSessionDisplayInfo(sessionInfo.getAppPackageName());
                    }
                }
            }
        };
        this.mCallback = sessionCallback;
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        this.mInstaller = packageInstaller;
        this.mCache = LauncherAppState.getInstance(context).getIconCache();
        Handler handler = new Handler(LauncherModel.getWorkerLooper());
        this.mWorker = handler;
        packageInstaller.registerSessionCallback(sessionCallback, handler);
        this.mAppBoxInstallerPackageName = context.getString(R.string.appbox_installer_package_name);
    }

    @Override // com.android.launcher3.compat.PackageInstallerCompat
    public HashMap<String, Integer> updateAndGetActiveSessionCache() {
        HashMap<String, Integer> map = new HashMap<>();
        UserHandle userHandleMyUserHandle = Process.myUserHandle();
        for (PackageInstaller.SessionInfo sessionInfo : this.mInstaller.getAllSessions()) {
            addSessionInfoToCahce(sessionInfo, userHandleMyUserHandle);
            if (sessionInfo.getAppPackageName() != null) {
                map.put(sessionInfo.getAppPackageName(), Integer.valueOf((int) (sessionInfo.getProgress() * 100.0f)));
                this.mActiveSessions.put(sessionInfo.getSessionId(), sessionInfo.getAppPackageName());
                if (!this.mAppBoxInstallerPackageName.equals(sessionInfo.getInstallerPackageName())) {
                    this.mActivePackagesExceptAppBoxInstaller.put(sessionInfo.getAppPackageName(), Integer.valueOf((int) (sessionInfo.getProgress() * 100.0f)));
                }
            }
        }
        return map;
    }

    void addSessionInfoToCahce(PackageInstaller.SessionInfo info, UserHandle user) {
        String appPackageName = info.getAppPackageName();
        if (appPackageName != null) {
            this.mCache.cachePackageInstallInfo(appPackageName, user, info.getAppIcon(), info.getAppLabel());
        }
    }

    @Override // com.android.launcher3.compat.PackageInstallerCompat
    public void onStop() {
        this.mInstaller.unregisterSessionCallback(this.mCallback);
    }

    void sendUpdate(PackageInstallerCompat.PackageInstallInfo info) {
        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
        if (instanceNoCreate != null) {
            instanceNoCreate.getModel().setPackageState(info);
        }
    }
}
