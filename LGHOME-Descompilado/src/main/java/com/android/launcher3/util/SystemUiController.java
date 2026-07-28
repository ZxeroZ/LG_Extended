package com.android.launcher3.util;

import android.view.Window;
import com.android.launcher3.Utilities;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class SystemUiController {
    public static final int FLAG_DARK_NAV = 2;
    public static final int FLAG_DARK_STATUS = 8;
    public static final int FLAG_LIGHT_NAV = 1;
    public static final int FLAG_LIGHT_STATUS = 4;
    public static final int UI_STATE_ALL_APPS = 1;
    public static final int UI_STATE_BASE_WINDOW = 0;
    public static final int UI_STATE_OVERVIEW = 4;
    public static final int UI_STATE_ROOT_VIEW = 3;
    public static final int UI_STATE_SCRIM_VIEW = 5;
    public static final int UI_STATE_WIDGET_BOTTOM_SHEET = 2;
    private final int[] mStates = new int[6];
    private final Window mWindow;

    public SystemUiController(Window window) {
        this.mWindow = window;
    }

    public void updateUiState(int uiState, boolean isLight) {
        updateUiState(uiState, isLight ? 5 : 10);
    }

    public void updateUiState(int uiState, int flags) {
        int[] iArr = this.mStates;
        if (iArr[uiState] == flags) {
            return;
        }
        iArr[uiState] = flags;
        int systemUiVisibility = this.mWindow.getDecorView().getSystemUiVisibility();
        int sysUiVisibilityFlags = systemUiVisibility;
        for (int i : this.mStates) {
            sysUiVisibilityFlags = getSysUiVisibilityFlags(i, sysUiVisibilityFlags);
        }
        if (sysUiVisibilityFlags != systemUiVisibility) {
            this.mWindow.getDecorView().setSystemUiVisibility(sysUiVisibilityFlags);
        }
    }

    public int getBaseSysuiVisibility() {
        return getSysUiVisibilityFlags(this.mStates[0], this.mWindow.getDecorView().getSystemUiVisibility());
    }

    private int getSysUiVisibilityFlags(int stateFlag, int currentVisibility) {
        if (Utilities.ATLEAST_OREO) {
            if ((stateFlag & 1) != 0) {
                currentVisibility |= 16;
            } else if ((stateFlag & 2) != 0) {
                currentVisibility &= -17;
            }
        }
        return (stateFlag & 4) != 0 ? currentVisibility | 8 : (stateFlag & 8) != 0 ? currentVisibility & (-9) : currentVisibility;
    }

    public String toString() {
        return "mStates=" + Arrays.toString(this.mStates);
    }
}
