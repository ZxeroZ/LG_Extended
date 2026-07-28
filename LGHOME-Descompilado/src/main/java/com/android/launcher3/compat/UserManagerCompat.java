package com.android.launcher3.compat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import com.android.launcher3.Utilities;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class UserManagerCompat {
    private static UserManagerCompat sInstance;
    private static final Object sInstanceLock = new Object();

    public abstract void enableAndResetCache();

    public abstract Drawable getBadgedDrawableForUser(Drawable unbadged, UserHandle user);

    public abstract CharSequence getBadgedLabelForUser(CharSequence label, UserHandle user);

    public abstract int getDualUserId();

    public abstract long getSerialNumberForUser(UserHandle user);

    public abstract long getUserCreationTime(UserHandle user);

    public abstract UserHandle getUserForSerialNumber(long serialNumber);

    public abstract List<UserHandle> getUserProfiles();

    public abstract boolean isDemoUser();

    public abstract boolean isDual(int userId);

    public abstract boolean isQuietModeEnabled(UserHandle user);

    public abstract boolean isUserUnlocked(UserHandle user);

    public abstract void requestQuietModeEnabled(boolean enabled, UserHandle userHandle);

    protected UserManagerCompat() {
    }

    public static UserManagerCompat getInstance(Context context) {
        UserManagerCompat userManagerCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                if (Utilities.isNycOrAbove()) {
                    sInstance = new UserManagerCompatVN(context.getApplicationContext());
                } else if (Utilities.ATLEAST_LOLLIPOP) {
                    sInstance = new UserManagerCompatVL(context.getApplicationContext());
                } else if (Utilities.ATLEAST_JB_MR1) {
                    sInstance = new UserManagerCompatV17(context.getApplicationContext());
                } else {
                    sInstance = new UserManagerCompatV16();
                }
            }
            userManagerCompat = sInstance;
        }
        return userManagerCompat;
    }
}
