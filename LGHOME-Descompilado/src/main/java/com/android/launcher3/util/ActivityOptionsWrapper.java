package com.android.launcher3.util;

import android.app.ActivityOptions;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class ActivityOptionsWrapper {
    public final RunnableList onEndCallback;
    public final ActivityOptions options;

    public ActivityOptionsWrapper(ActivityOptions options, RunnableList onEndCallback) {
        this.options = options;
        this.onEndCallback = onEndCallback;
    }

    public Bundle toBundle() {
        return this.options.toBundle();
    }
}
