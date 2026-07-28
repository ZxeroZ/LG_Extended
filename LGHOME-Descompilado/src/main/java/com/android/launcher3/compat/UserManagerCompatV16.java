package com.android.launcher3.compat;

import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UserManagerCompatV16 extends UserManagerCompat {
    @Override // com.android.launcher3.compat.UserManagerCompat
    public void enableAndResetCache() {
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public Drawable getBadgedDrawableForUser(Drawable unbadged, UserHandle user) {
        return unbadged;
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public CharSequence getBadgedLabelForUser(CharSequence label, UserHandle user) {
        return label;
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public int getDualUserId() {
        return 0;
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public long getSerialNumberForUser(UserHandle user) {
        return 0L;
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public long getUserCreationTime(UserHandle user) {
        return 0L;
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public boolean isDemoUser() {
        return false;
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public boolean isDual(int userId) {
        return false;
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public boolean isQuietModeEnabled(UserHandle user) {
        return false;
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public boolean isUserUnlocked(UserHandle user) {
        return true;
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public void requestQuietModeEnabled(boolean enabled, UserHandle userHandle) {
    }

    UserManagerCompatV16() {
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public List<UserHandle> getUserProfiles() {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(Process.myUserHandle());
        return arrayList;
    }

    @Override // com.android.launcher3.compat.UserManagerCompat
    public UserHandle getUserForSerialNumber(long serialNumber) {
        return Process.myUserHandle();
    }
}
