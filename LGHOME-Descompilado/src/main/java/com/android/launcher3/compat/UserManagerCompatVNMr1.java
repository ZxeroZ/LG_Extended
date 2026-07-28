package com.android.launcher3.compat;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public class UserManagerCompatVNMr1 extends UserManagerCompatVN {
    UserManagerCompatVNMr1(Context context) {
        super(context);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatVN, com.android.launcher3.compat.UserManagerCompatVL, com.android.launcher3.compat.UserManagerCompatV17, com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public boolean isDual(int userId) {
        return super.isDual(userId);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatVN, com.android.launcher3.compat.UserManagerCompatVL, com.android.launcher3.compat.UserManagerCompatV17, com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public int getDualUserId() {
        return super.getDualUserId();
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public boolean isDemoUser() {
        return this.mUserManager.isDemoUser();
    }
}
