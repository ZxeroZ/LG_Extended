package com.android.systemui.plugins;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import com.android.systemui.plugins.annotations.ProvidesInterface;

/* JADX INFO: loaded from: classes.dex */
@ProvidesInterface(action = OverviewScreenshotActions.ACTION, version = 1)
public interface OverviewScreenshotActions extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_OVERVIEW_SCREENSHOT_ACTIONS";
    public static final int VERSION = 1;

    void setupActions(ViewGroup parent, Bitmap screenshot, Activity activity);
}
