package com.android.launcher3.compat;

import android.content.Context;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.launcher3.util.LongArrayMap;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class UserManagerCompatV17 extends UserManagerCompatV16 {
    protected UserManager mUserManager;
    protected HashMap<UserHandle, Long> mUserToSerialMap;
    protected LongArrayMap<UserHandle> mUsers;

    UserManagerCompatV17(Context context) {
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public long getSerialNumberForUser(UserHandle user) {
        return this.mUserManager.getSerialNumberForUser(user);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public UserHandle getUserForSerialNumber(long serialNumber) {
        return this.mUserManager.getUserForSerialNumber(serialNumber);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public void enableAndResetCache() {
        synchronized (this) {
            this.mUsers = new LongArrayMap<>();
            this.mUserToSerialMap = new HashMap<>();
            UserHandle userHandleMyUserHandle = Process.myUserHandle();
            long serialNumberForUser = this.mUserManager.getSerialNumberForUser(userHandleMyUserHandle);
            this.mUsers.put(serialNumberForUser, userHandleMyUserHandle);
            this.mUserToSerialMap.put(userHandleMyUserHandle, Long.valueOf(serialNumberForUser));
        }
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public boolean isDual(int userId) {
        return UserManager.isDual(userId);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public int getDualUserId() {
        return this.mUserManager.getDualUserId();
    }
}
