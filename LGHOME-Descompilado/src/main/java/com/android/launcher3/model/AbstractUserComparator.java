package com.android.launcher3.model;

import android.content.Context;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.model.data.ItemInfo;
import java.util.Comparator;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractUserComparator<T extends ItemInfo> implements Comparator<T> {
    private final UserManagerCompat mUserManager;
    private HashMap<UserHandle, Long> mUserSerialCache = new HashMap<>();
    private final UserHandle mMyUser = Process.myUserHandle();

    public AbstractUserComparator(Context context) {
        this.mUserManager = UserManagerCompat.getInstance(context);
    }

    /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
    @Override // java.util.Comparator
    public int compare(T lhs, T rhs) {
        if (this.mMyUser.equals(lhs.user)) {
            return -1;
        }
        return getAndCacheUserSerial(lhs.user).compareTo(getAndCacheUserSerial(rhs.user));
    }

    private Long getAndCacheUserSerial(UserHandle user) {
        Long l = this.mUserSerialCache.get(user);
        if (l != null) {
            return l;
        }
        Long lValueOf = Long.valueOf(this.mUserManager.getSerialNumberForUser(user));
        this.mUserSerialCache.put(user, lValueOf);
        return lValueOf;
    }

    public void clearUserCache() {
        this.mUserSerialCache.clear();
    }
}
