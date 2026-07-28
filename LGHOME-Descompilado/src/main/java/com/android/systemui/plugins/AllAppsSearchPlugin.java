package com.android.systemui.plugins;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.EditText;
import com.android.systemui.plugins.annotations.ProvidesInterface;

/* JADX INFO: loaded from: classes.dex */
@ProvidesInterface(action = AllAppsSearchPlugin.ACTION, version = 3)
public interface AllAppsSearchPlugin extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_ALL_APPS_SEARCH_ACTIONS";
    public static final int VERSION = 3;

    void onAnimationEnd(float progress);

    void onDragStart(float progress);

    void setEditText(EditText editText);

    void setProgress(float progress);

    void setup(ViewGroup parent, Activity activity, float allAppsContainerHeight);
}
