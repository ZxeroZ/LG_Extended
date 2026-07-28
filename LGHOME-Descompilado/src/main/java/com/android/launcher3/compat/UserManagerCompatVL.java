package com.android.launcher3.compat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.util.LongArrayMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UserManagerCompatVL extends UserManagerCompatV17 {
    private static final String USER_CREATION_TIME_KEY = "user_creation_time_";
    private final Context mContext;
    private final PackageManager mPm;

    UserManagerCompatVL(Context context) {
        super(context);
        this.mPm = context.getPackageManager();
        this.mContext = context;
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV17, com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public void enableAndResetCache() {
        synchronized (this) {
            this.mUsers = new LongArrayMap<>();
            this.mUserToSerialMap = new HashMap<>();
            List<UserHandle> userProfiles = this.mUserManager.getUserProfiles();
            if (userProfiles != null) {
                for (UserHandle userHandle : userProfiles) {
                    long serialNumberForUser = this.mUserManager.getSerialNumberForUser(userHandle);
                    this.mUsers.put(serialNumberForUser, userHandle);
                    this.mUserToSerialMap.put(userHandle, Long.valueOf(serialNumberForUser));
                }
            }
        }
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public List<UserHandle> getUserProfiles() {
        List<UserHandle> userProfiles = this.mUserManager.getUserProfiles();
        if (userProfiles == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(userProfiles.size());
        Iterator<UserHandle> it = userProfiles.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next());
        }
        return arrayList;
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public Drawable getBadgedDrawableForUser(Drawable unbadged, UserHandle user) {
        return this.mPm.getUserBadgedIcon(unbadged, user);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public CharSequence getBadgedLabelForUser(CharSequence label, UserHandle user) {
        return user == null ? label : this.mPm.getUserBadgedLabel(label, user);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public long getUserCreationTime(UserHandle user) {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0);
        String str = USER_CREATION_TIME_KEY + getSerialNumberForUser(user);
        if (!sharedPreferences.contains(str)) {
            sharedPreferences.edit().putLong(str, System.currentTimeMillis()).apply();
        }
        return sharedPreferences.getLong(str, 0L);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV17, com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public boolean isDual(int userId) {
        UserManager userManager = this.mUserManager;
        return UserManager.isDual(userId);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV17, com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public int getDualUserId() {
        return this.mUserManager.getDualUserId();
    }
}
