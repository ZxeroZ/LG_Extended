package com.lge.launcher3.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.LongSparseArray;
import com.android.launcher3.compat.UserManagerCompat;

/* JADX INFO: loaded from: classes.dex */
public class UserUtils {
    private static final String TAG = "UserUtils";
    private static long sUserSerialNum = -1;
    private static long sWorkFolderId = -1;
    private static String sWorkFolderName = "";

    public static final UserManagerCompat getUserManagerCompat(Context context) {
        return UserManagerCompat.getInstance(context);
    }

    public static final LongSparseArray<UserHandle> getAllUsers(Context context) {
        UserManagerCompat userManagerCompat = getUserManagerCompat(context);
        LongSparseArray<UserHandle> longSparseArray = new LongSparseArray<>();
        for (UserHandle userHandle : userManagerCompat.getUserProfiles()) {
            longSparseArray.put(userManagerCompat.getSerialNumberForUser(userHandle), userHandle);
        }
        return longSparseArray;
    }

    public static final UserHandle getUserHandle(Context context, int profileId) {
        return getAllUsers(context).get(profileId);
    }

    public static int compare(UserHandle lhs, UserHandle rhs) {
        return Boolean.compare(lhs.isOwner(), rhs.isOwner());
    }

    public static boolean isAdminUser(Context context) {
        return ((UserManager) context.getSystemService("user")).isAdminUser();
    }

    public static UserInfo getCurrentUserInfo(Context context) {
        return ((UserManager) context.getSystemService("user")).getUserInfo(UserHandle.myUserId());
    }

    public static boolean existUser(Context context, UserHandle user) {
        return UserManagerCompat.getInstance(context).getSerialNumberForUser(user) != -1;
    }

    public static boolean isOriginalApplication(Context context, String packageName) {
        ApplicationInfo applicationInfoAsUser;
        try {
            applicationInfoAsUser = context.getPackageManager().getApplicationInfoAsUser(packageName, 0, getUserManagerCompat(context).getDualUserId());
        } catch (PackageManager.NameNotFoundException unused) {
            LGLog.w(TAG, "Unable to get packageName. Package manager is dead?", new int[0]);
            applicationInfoAsUser = null;
        }
        if (applicationInfoAsUser != null) {
            if (applicationInfoAsUser.enabled) {
                return true;
            }
            LGLog.i(TAG, packageName + " dual app is disabled");
        }
        return false;
    }

    public static boolean isSecondApplication(Context context, int userID) {
        return getUserManagerCompat(context).isDual(userID);
    }

    public static void setWorkProfileInfo(long serial, long workFolderId) {
        LGLog.d(TAG, "UserSerial : " + serial + ", workFolderId : " + workFolderId);
        sUserSerialNum = serial;
        sWorkFolderId = workFolderId;
    }

    public static long getUserSerialNum() {
        return sUserSerialNum;
    }

    public static long getWorkFolderId() {
        return sWorkFolderId;
    }

    public static void setWorkProfileFolderName(String name) {
        LGLog.d(TAG, "work folder name : " + name);
        sWorkFolderName = name;
    }

    public static String getWorkProfileFolderName() {
        return sWorkFolderName;
    }
}
