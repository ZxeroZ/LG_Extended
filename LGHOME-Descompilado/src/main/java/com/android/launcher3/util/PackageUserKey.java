package com.android.launcher3.util;

import android.appwidget.AppWidgetProviderInfo;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class PackageUserKey {
    private int mHashCode;
    public String mPackageName;
    public UserHandle mUser;

    public static PackageUserKey fromItemInfo(ItemInfo info) {
        return new PackageUserKey(info.getTargetComponent().getPackageName(), info.user);
    }

    public static PackageUserKey fromWidgetInfo(AppWidgetProviderInfo info) {
        return new PackageUserKey(info.provider.getPackageName(), info.getProfile());
    }

    public static PackageUserKey fromNotification(StatusBarNotification notification) {
        return new PackageUserKey(notification.getPackageName(), notification.getUser());
    }

    public PackageUserKey(String packageName, UserHandle user) {
        update(packageName, user);
    }

    private void update(String packageName, UserHandle user) {
        this.mPackageName = packageName;
        this.mUser = user;
        this.mHashCode = Arrays.hashCode(new Object[]{packageName, user});
    }

    public boolean updateFromItemInfo(ItemInfo info) {
        if (!DeepShortcutManager.supportsShortcuts(info)) {
            return false;
        }
        update(info.getTargetComponent().getPackageName(), info.user);
        return true;
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PackageUserKey)) {
            return false;
        }
        PackageUserKey packageUserKey = (PackageUserKey) obj;
        return this.mPackageName.equals(packageUserKey.mPackageName) && this.mUser.equals(packageUserKey.mUser);
    }
}
