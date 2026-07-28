package com.android.systemui.plugins;

import com.android.systemui.plugins.annotations.ProvidesInterface;

/* JADX INFO: loaded from: classes.dex */
@ProvidesInterface(action = ResourceProvider.ACTION, version = 1)
public interface ResourceProvider extends Plugin {
    public static final String ACTION = "com.android.launcher3.action.PLUGIN_DYNAMIC_RESOURCE";
    public static final int VERSION = 1;

    int getColor(int resId);

    float getDimension(int resId);

    float getFloat(int resId);

    float getFraction(int resId);

    int getInt(int resId);
}
