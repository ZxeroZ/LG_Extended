package com.android.launcher3.compat;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.util.PackageUserKey;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LauncherAppsCompatVO extends LauncherAppsCompatVL {
    LauncherAppsCompatVO(Context context) {
        super(context);
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompatVL, com.android.launcher3.compat.LauncherAppsCompat
    public ApplicationInfo getApplicationInfo(String packageName, int flags, UserHandle user) {
        try {
            ApplicationInfo applicationInfo = this.mLauncherApps.getApplicationInfo(packageName, flags, user);
            if ((applicationInfo.flags & 8388608) == 0) {
                return null;
            }
            if (applicationInfo.enabled) {
                return applicationInfo;
            }
            return null;
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompatVL, com.android.launcher3.compat.LauncherAppsCompat
    public List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(PackageUserKey packageUser) {
        String str;
        List<UserHandle> userProfiles;
        ArrayList arrayList = new ArrayList();
        UserHandle userHandleMyUserHandle = Process.myUserHandle();
        try {
            Method declaredMethod = LauncherApps.class.getDeclaredMethod("getShortcutConfigActivityList", String.class, UserHandle.class);
            if (packageUser == null) {
                userProfiles = UserManagerCompat.getInstance(this.mContext).getUserProfiles();
                str = null;
            } else {
                ArrayList arrayList2 = new ArrayList(1);
                arrayList2.add(packageUser.mUser);
                str = packageUser.mPackageName;
                userProfiles = arrayList2;
            }
            for (UserHandle userHandle : userProfiles) {
                boolean zEquals = userHandleMyUserHandle.equals(userHandle);
                for (LauncherActivityInfo launcherActivityInfo : (List) declaredMethod.invoke(this.mLauncherApps, str, userHandle)) {
                    if (zEquals || launcherActivityInfo.getApplicationInfo().targetSdkVersion >= 26) {
                        arrayList.add(new ShortcutConfigActivityInfo.ShortcutConfigActivityInfoVO(launcherActivityInfo));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("LauncherAppsCompatVO", "Error calling new API", e);
        }
        return arrayList;
    }
}
