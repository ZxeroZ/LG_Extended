package com.android.systemui.plugins;

import android.view.MotionEvent;
import com.android.systemui.plugins.annotations.ProvidesInterface;

/* JADX INFO: loaded from: classes.dex */
@ProvidesInterface(action = OverscrollPlugin.ACTION, version = 4)
public interface OverscrollPlugin extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_LAUNCHER_OVERSCROLL";
    public static final String DEVICE_STATE_APP = "App";
    public static final String DEVICE_STATE_LAUNCHER = "Launcher";
    public static final String DEVICE_STATE_LOCKED = "Locked";
    public static final String DEVICE_STATE_UNKNOWN = "Unknown";
    public static final int VERSION = 4;

    boolean allowsUnderlyingActivityOverscroll();

    boolean blockOtherGestures();

    boolean isActive();

    void onTouchEvent(MotionEvent event, int horizontalDistancePx, int verticalDistancePx, int thresholdPx, int flingDistanceThresholdPx, int flingVelocityThresholdPx, String deviceState, String underlyingActivity);
}
