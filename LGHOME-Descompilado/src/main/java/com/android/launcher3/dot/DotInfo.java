package com.android.launcher3.dot;

import com.android.launcher3.notification.NotificationKeyData;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DotInfo {
    public static final int MAX_COUNT = 999;
    private final List<NotificationKeyData> mNotificationKeys = new ArrayList();
    private int mTotalCount;

    public boolean addOrUpdateNotificationKey(NotificationKeyData notificationKey) {
        int iIndexOf = this.mNotificationKeys.indexOf(notificationKey);
        NotificationKeyData notificationKeyData = iIndexOf == -1 ? null : this.mNotificationKeys.get(iIndexOf);
        if (notificationKeyData != null) {
            if (notificationKeyData.count == notificationKey.count) {
                return false;
            }
            int i = this.mTotalCount - notificationKeyData.count;
            this.mTotalCount = i;
            this.mTotalCount = i + notificationKey.count;
            notificationKeyData.count = notificationKey.count;
            return true;
        }
        boolean zAdd = this.mNotificationKeys.add(notificationKey);
        if (zAdd) {
            this.mTotalCount += notificationKey.count;
        }
        return zAdd;
    }

    public boolean removeNotificationKey(NotificationKeyData notificationKey) {
        boolean zRemove = this.mNotificationKeys.remove(notificationKey);
        if (zRemove) {
            this.mTotalCount -= notificationKey.count;
        }
        return zRemove;
    }

    public List<NotificationKeyData> getNotificationKeys() {
        return this.mNotificationKeys;
    }

    public int getNotificationCount() {
        return Math.min(this.mTotalCount, MAX_COUNT);
    }
}
