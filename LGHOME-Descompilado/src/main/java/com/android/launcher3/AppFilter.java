package com.android.launcher3;

import android.content.ComponentName;
import android.os.UserHandle;

/* JADX INFO: loaded from: classes.dex */
public abstract class AppFilter {
    public abstract boolean shouldShowApp(ComponentName app, UserHandle userHandle);
}
