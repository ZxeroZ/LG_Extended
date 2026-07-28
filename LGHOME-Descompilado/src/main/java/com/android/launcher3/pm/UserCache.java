package com.android.launcher3.pm;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.SafeCloseable;
import com.android.launcher3.util.SimpleBroadcastReceiver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class UserCache {
    public static final MainThreadInitializedObject<UserCache> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.launcher3.pm.-$$Lambda$UserCache$UIrX6UKbSZYvgA7wUshqWkIKZkY
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return UserCache.lambda$UIrX6UKbSZYvgA7wUshqWkIKZkY(context);
        }
    });
    private final Context mContext;
    private final ArrayList<Runnable> mUserChangeListeners = new ArrayList<>();
    private final SimpleBroadcastReceiver mUserChangeReceiver = new SimpleBroadcastReceiver(new Consumer() { // from class: com.android.launcher3.pm.-$$Lambda$UserCache$zu1GNRG_zl1pJdIxkVp1lPzVZaM
        @Override // java.util.function.Consumer
        public final void accept(Object obj) {
            this.f$0.onUsersChanged((Intent) obj);
        }
    });
    private final UserManager mUserManager;
    private ArrayMap<UserHandle, Long> mUserToSerialMap;
    private LongSparseArray<UserHandle> mUsers;

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR (r1v0 android.content.Context) A[MD:(android.content.Context):void (m)] call: com.android.launcher3.pm.UserCache.<init>(android.content.Context):void type: CONSTRUCTOR */
    public static /* synthetic */ UserCache lambda$UIrX6UKbSZYvgA7wUshqWkIKZkY(Context context) {
        return new UserCache(context);
    }

    private UserCache(Context context) {
        this.mContext = context;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUsersChanged(Intent intent) {
        enableAndResetCache();
        this.mUserChangeListeners.forEach(new Consumer() { // from class: com.android.launcher3.pm.-$$Lambda$YNFGg4v_quJTFq0zrWSJoDe4_Zo
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Runnable) obj).run();
            }
        });
    }

    public SafeCloseable addUserChangeListener(final Runnable command) {
        SafeCloseable safeCloseable;
        synchronized (this) {
            if (this.mUserChangeListeners.isEmpty()) {
                this.mUserChangeReceiver.register(this.mContext, LauncherAppsCompat.ACTION_MANAGED_PROFILE_ADDED, LauncherAppsCompat.ACTION_MANAGED_PROFILE_REMOVED);
                enableAndResetCache();
            }
            this.mUserChangeListeners.add(command);
            safeCloseable = new SafeCloseable() { // from class: com.android.launcher3.pm.-$$Lambda$UserCache$iSMcL-44WidpfGGQAGAxvOpZ7IY
                @Override // com.android.launcher3.util.SafeCloseable, java.lang.AutoCloseable
                public final void close() {
                    this.f$0.lambda$addUserChangeListener$0$UserCache(command);
                }
            };
        }
        return safeCloseable;
    }

    private void enableAndResetCache() {
        synchronized (this) {
            this.mUsers = new LongSparseArray<>();
            this.mUserToSerialMap = new ArrayMap<>();
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

    /* JADX DEBUG: Method merged with bridge method: lambda$addUserChangeListener$0$UserCache(Ljava/lang/Runnable;)V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: removeUserChangeListener, reason: merged with bridge method [inline-methods] */
    public void lambda$addUserChangeListener$0$UserCache(Runnable command) {
        synchronized (this) {
            this.mUserChangeListeners.remove(command);
            if (this.mUserChangeListeners.isEmpty()) {
                this.mContext.unregisterReceiver(this.mUserChangeReceiver);
                this.mUsers = null;
                this.mUserToSerialMap = null;
            }
        }
    }

    public long getSerialNumberForUser(UserHandle user) {
        synchronized (this) {
            ArrayMap<UserHandle, Long> arrayMap = this.mUserToSerialMap;
            if (arrayMap != null) {
                Long l = arrayMap.get(user);
                return l == null ? 0L : l.longValue();
            }
            return this.mUserManager.getSerialNumberForUser(user);
        }
    }

    public UserHandle getUserForSerialNumber(long serialNumber) {
        synchronized (this) {
            LongSparseArray<UserHandle> longSparseArray = this.mUsers;
            if (longSparseArray != null) {
                return longSparseArray.get(serialNumber);
            }
            return this.mUserManager.getUserForSerialNumber(serialNumber);
        }
    }

    public List<UserHandle> getUserProfiles() {
        synchronized (this) {
            if (this.mUsers != null) {
                return new ArrayList(this.mUserToSerialMap.keySet());
            }
            List<UserHandle> userProfiles = this.mUserManager.getUserProfiles();
            return userProfiles == null ? Collections.emptyList() : userProfiles;
        }
    }
}
