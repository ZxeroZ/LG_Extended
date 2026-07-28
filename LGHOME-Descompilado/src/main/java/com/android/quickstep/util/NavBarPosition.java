package com.android.quickstep.util;

import com.android.launcher3.util.DisplayController;
import com.android.quickstep.SysUINavigationMode;

/* JADX INFO: loaded from: classes.dex */
public class NavBarPosition {
    private int mDisplayRotation;
    private final SysUINavigationMode.Mode mMode;

    public NavBarPosition(SysUINavigationMode.Mode mode, DisplayController.Info info) {
        this.mMode = mode;
        this.mDisplayRotation = info.rotation;
    }

    public NavBarPosition(SysUINavigationMode.Mode mode, int displayRotation) {
        this.mMode = mode;
        this.mDisplayRotation = displayRotation;
    }

    public boolean isRightEdge() {
        return this.mMode != SysUINavigationMode.Mode.NO_BUTTON && this.mDisplayRotation == 1;
    }

    public boolean isLeftEdge() {
        return this.mMode != SysUINavigationMode.Mode.NO_BUTTON && this.mDisplayRotation == 3;
    }

    public float getRotation() {
        if (isLeftEdge()) {
            return 90.0f;
        }
        return isRightEdge() ? -90 : 0;
    }

    public void refreshRotate(NavBarPosition newPosition) {
        this.mDisplayRotation = newPosition.mDisplayRotation;
    }
}
