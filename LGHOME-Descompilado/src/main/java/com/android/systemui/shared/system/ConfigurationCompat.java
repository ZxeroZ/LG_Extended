package com.android.systemui.shared.system;

import android.content.res.Configuration;

/* JADX INFO: loaded from: classes.dex */
public class ConfigurationCompat {
    public static int getWindowConfigurationRotation(Configuration c) {
        return c.windowConfiguration.getRotation();
    }
}
