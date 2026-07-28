package com.lge.launcher3.badge.appnotifier;

import android.os.Process;
import android.os.UserHandle;

/* JADX INFO: loaded from: classes.dex */
public class AppNotifierData {
    public String componentName;
    public String packageName;
    public UserHandle user;

    public AppNotifierData(String packageName, String componentName, UserHandle user) {
        this.packageName = packageName;
        this.componentName = componentName;
        if (user == null) {
            this.user = Process.myUserHandle();
        } else {
            this.user = user;
        }
    }

    public int hashCode() {
        return this.componentName.hashCode() + this.user.hashCode();
    }

    public boolean equals(Object o) {
        AppNotifierData appNotifierData = (AppNotifierData) o;
        return appNotifierData.componentName.equals(this.componentName) && appNotifierData.packageName.equals(this.packageName) && appNotifierData.user.equals(this.user);
    }
}
