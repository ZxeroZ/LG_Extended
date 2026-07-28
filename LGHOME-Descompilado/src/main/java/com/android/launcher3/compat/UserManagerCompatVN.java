package com.android.launcher3.compat;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.launcher3.Utilities;
import java.lang.reflect.InvocationTargetException;

/* JADX INFO: loaded from: classes.dex */
public class UserManagerCompatVN extends UserManagerCompatVL {
    private static final String TAG = "UserManagerCompatVN";

    UserManagerCompatVN(Context context) {
        super(context);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public boolean isQuietModeEnabled(UserHandle user) {
        if (user != null) {
            try {
                return ((Boolean) UserManager.class.getMethod("isQuietModeEnabled", UserHandle.class).invoke(this.mUserManager, user)).booleanValue();
            } catch (IllegalAccessException e) {
                e = e;
                Log.e(TAG, "Running on N without isQuietModeEnabled", e);
                return false;
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (NoSuchMethodError e3) {
                e = e3;
                Log.e(TAG, "Running on N without isQuietModeEnabled", e);
                return false;
            } catch (NoSuchMethodException e4) {
                e = e4;
                Log.e(TAG, "Running on N without isQuietModeEnabled", e);
                return false;
            } catch (InvocationTargetException e5) {
                e = e5;
                Log.e(TAG, "Running on N without isQuietModeEnabled", e);
                return false;
            }
        }
        return false;
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public boolean isUserUnlocked(UserHandle user) {
        return this.mUserManager.isUserUnlocked(user);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatVL, com.android.launcher3.compat.UserManagerCompatV17, com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public boolean isDual(int userId) {
        UserManager userManager = this.mUserManager;
        return UserManager.isDual(userId);
    }

    @Override // com.android.launcher3.compat.UserManagerCompatVL, com.android.launcher3.compat.UserManagerCompatV17, com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public int getDualUserId() {
        return this.mUserManager.getDualUserId();
    }

    @Override // com.android.launcher3.compat.UserManagerCompatV16, com.android.launcher3.compat.UserManagerCompat
    public void requestQuietModeEnabled(boolean enabled, UserHandle userHandle) {
        if (Utilities.ATLEAST_T) {
            this.mUserManager.requestQuietModeEnabled(enabled, userHandle);
        }
    }
}
