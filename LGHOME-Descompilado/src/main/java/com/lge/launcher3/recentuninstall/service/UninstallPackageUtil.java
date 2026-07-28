package com.lge.launcher3.recentuninstall.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.util.LGLog;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UninstallPackageUtil {
    static final long DEFAULT_UNINSTALL_ELAPSED_TIME = 86400000;
    static final long DIRECT_UNINSTALL_TIME = 0;

    static void uninstallDisableAppsAllUsers(Context context, long elapsedTime) {
        Iterator<UserHandle> it = getUserProfiles(context).iterator();
        while (it.hasNext()) {
            uninstallDisabledApps(context, it.next().getIdentifier(), elapsedTime);
        }
    }

    private static List<UserHandle> getUserProfiles(Context context) {
        List<UserHandle> userProfiles = ((UserManager) context.getSystemService("user")).getUserProfiles();
        return userProfiles == null ? Collections.emptyList() : userProfiles;
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x009e  */
    /* JADX WARN: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static void uninstallDisabledApps(android.content.Context r18, long r19, long r21) {
        /*
            r0 = r19
            r2 = r21
            java.lang.String r4 = "RUService"
            r6 = 0
            long r7 = java.lang.System.currentTimeMillis()     // Catch: java.lang.NoClassDefFoundError -> L95 android.os.RemoteException -> La7
            com.lge.content.pm.PackageManagerEx r9 = com.lge.content.pm.PackageManagerEx.getDefault()     // Catch: java.lang.NoClassDefFoundError -> L95 android.os.RemoteException -> La7
            int r10 = (int) r0     // Catch: java.lang.NoClassDefFoundError -> L95 android.os.RemoteException -> La7
            java.lang.String[] r11 = r9.getDisabledByLGLauncherPackageList(r10)     // Catch: java.lang.NoClassDefFoundError -> L95 android.os.RemoteException -> La7
            if (r11 == 0) goto L9c
            int r12 = r11.length     // Catch: java.lang.NoClassDefFoundError -> L95 android.os.RemoteException -> La7
            if (r12 <= 0) goto L9c
            r12 = r6
        L1a:
            int r13 = r11.length     // Catch: java.lang.NoClassDefFoundError -> L93 android.os.RemoteException -> La7
            if (r6 >= r13) goto L91
            r13 = r11[r6]     // Catch: java.lang.NoClassDefFoundError -> L93 android.os.RemoteException -> La7
            r14 = 0
            int r14 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r14 == 0) goto L85
            if (r14 <= 0) goto L36
            long r14 = r9.getLastDisabledTime(r13, r10)     // Catch: java.lang.NoClassDefFoundError -> L93 android.os.RemoteException -> La7
            long r14 = r7 - r14
            long r14 = java.lang.Math.abs(r14)     // Catch: java.lang.NoClassDefFoundError -> L93 android.os.RemoteException -> La7
            int r14 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
            if (r14 <= 0) goto L36
            goto L85
        L36:
            long r14 = r9.getLastDisabledTime(r13, r10)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            long r14 = r7 - r14
            r16 = r6
            long r5 = r9.getLastDisabledTime(r13, r10)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            r12.<init>()     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            r17 = r9
            java.lang.String r9 = "uninstallDisabledApps() Package: "
            r12.append(r9)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            r12.append(r13)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            java.lang.String r9 = " userId:"
            r12.append(r9)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            r12.append(r0)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            java.lang.String r9 = " elapsedTime: "
            r12.append(r9)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            r12.append(r2)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            java.lang.String r9 = " elapsedTimeAfterDisable: "
            r12.append(r9)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            r12.append(r14)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            java.lang.String r9 = "currentTime:"
            r12.append(r9)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            r12.append(r7)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            java.lang.String r9 = " disabledTime: "
            r12.append(r9)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            r12.append(r5)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            java.lang.String r5 = r12.toString()     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            com.lge.launcher3.util.LGLog.i(r4, r5)     // Catch: java.lang.NoClassDefFoundError -> L83 android.os.RemoteException -> La7
            r12 = 1
            goto L8c
        L83:
            r5 = 1
            goto L96
        L85:
            r16 = r6
            r17 = r9
            uninstallPackage(r13, r0)     // Catch: java.lang.NoClassDefFoundError -> L93 android.os.RemoteException -> La7
        L8c:
            int r6 = r16 + 1
            r9 = r17
            goto L1a
        L91:
            r6 = r12
            goto L9c
        L93:
            r5 = r12
            goto L96
        L95:
            r5 = r6
        L96:
            java.lang.String r0 = "Not implement PackageManagerEx in framework"
            com.lge.launcher3.util.LGLog.d(r4, r0)
            r6 = r5
        L9c:
            if (r6 == 0) goto La6
            java.lang.String r0 = "Reschedule for unexpired apps"
            com.lge.launcher3.util.LGLog.d(r4, r0)
            sendSchedulingIntent(r18)
        La6:
            return
        La7:
            r0 = move-exception
            r0.printStackTrace()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.recentuninstall.service.UninstallPackageUtil.uninstallDisabledApps(android.content.Context, long, long):void");
    }

    public static void uninstallPackage(String packageName, long userId) {
        LGLog.i("RUService", "uninstallPackage() Package: " + packageName + " userId:" + userId);
        try {
            IPackageManager.Stub.asInterface(ServiceManager.getService(AppNotifierManager.ExtraSpec.USAGE_PACKAGE)).deletePackageAsUser(packageName, -1, (IPackageDeleteObserver) null, (int) userId, 0);
        } catch (Exception e) {
            LGLog.e("RUService", "Failed to uninstall package", e);
        }
    }

    public static void disablePackage(String packageName, long userId) {
        LGLog.i("RUService", "disablePackage() Package: " + packageName + " userId:" + userId);
        try {
            IPackageManager.Stub.asInterface(ServiceManager.getService(AppNotifierManager.ExtraSpec.USAGE_PACKAGE)).setApplicationEnabledSetting(packageName, 2, 0, (int) userId, "com.lge.launcher3");
        } catch (RemoteException e) {
            LGLog.e("RUService", "Failed to disable package", e);
        }
    }

    public static void sendSchedulingIntent(Context context) {
        Intent intent = new Intent(IntentConst.Action.ACTION_SCHEDULE_UNINSTALL_JOB.getValue(context));
        intent.setPackage("com.lge.launcher3");
        context.sendBroadcast(intent);
    }
}
