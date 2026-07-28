package com.android.systemui.plugins;

import android.app.Activity;
import android.content.Context;
import android.widget.FrameLayout;
import com.android.systemui.plugins.annotations.ProvidesInterface;

/* JADX INFO: loaded from: classes.dex */
@ProvidesInterface(action = RecentsExtraCard.ACTION, version = 1)
public interface RecentsExtraCard extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_RECENTS_EXTRA_CARD";
    public static final int VERSION = 1;

    void setupView(Context context, FrameLayout frameLayout, Activity activity);
}
