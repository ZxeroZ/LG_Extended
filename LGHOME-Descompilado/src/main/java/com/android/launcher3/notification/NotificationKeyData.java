package com.android.launcher3.notification;

import android.app.Notification;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class NotificationKeyData {
    public int count;
    public int flag;
    public String group;
    public final String notificationKey;
    public final String shortcutId;

    private NotificationKeyData(String notificationKey, String shortcutId, int count, int flags, String group) {
        this.notificationKey = notificationKey;
        this.shortcutId = shortcutId;
        this.count = Math.max(1, count);
        this.flag = flags;
        this.group = group;
    }

    public static NotificationKeyData fromNotification(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        if (Build.VERSION.SDK_INT >= 26) {
            return new NotificationKeyData(sbn.getKey(), notification.getShortcutId(), notification.number, notification.flags, notification.getGroup());
        }
        return new NotificationKeyData(sbn.getKey(), null, notification.number, notification.flags, notification.getGroup());
    }

    public static List<String> extractKeysOnly(List<NotificationKeyData> notificationKeys) {
        ArrayList arrayList = new ArrayList(notificationKeys.size());
        Iterator<NotificationKeyData> it = notificationKeys.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().notificationKey);
        }
        return arrayList;
    }

    public boolean equals(Object obj) {
        if (obj instanceof NotificationKeyData) {
            return ((NotificationKeyData) obj).notificationKey.equals(this.notificationKey);
        }
        return false;
    }
}
