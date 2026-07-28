package com.lge.launcher3.hideapps;

import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.os.UserHandle;

/* JADX INFO: loaded from: classes.dex */
public class HideAppItem {
    LauncherActivityInfo activityInfo;
    boolean checked;
    Intent intent;
    UserHandle userHandle;

    public LauncherActivityInfo getActivityInfo() {
        return this.activityInfo;
    }

    public UserHandle getUserHandle() {
        return this.userHandle;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(this.activityInfo.getComponentName()).append(", ").append(this.userHandle.toString()).append("}");
        return sb.toString();
    }
}
