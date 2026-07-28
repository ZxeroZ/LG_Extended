package com.lge.launcher3.allapps;

import android.content.ComponentName;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.AppInfo;

/* JADX INFO: loaded from: classes.dex */
public class CacheKey {
    public ComponentName componentName;
    public UserHandle user;

    public CacheKey(ComponentName componentName, UserHandle user) {
        this.componentName = componentName;
        if (user == null) {
            this.user = Process.myUserHandle();
        } else {
            this.user = user;
        }
    }

    public static CacheKey createKey(ShortcutInfo info) {
        return new CacheKey(info.intent.getComponent(), info.user);
    }

    public static CacheKey createKey(AppInfo info) {
        return new CacheKey(info.componentName, info.user);
    }

    public int hashCode() {
        return this.componentName.hashCode() + this.user.hashCode();
    }

    public boolean equals(Object o) {
        CacheKey cacheKey = (CacheKey) o;
        return cacheKey.componentName.equals(this.componentName) && cacheKey.user.equals(this.user);
    }
}
