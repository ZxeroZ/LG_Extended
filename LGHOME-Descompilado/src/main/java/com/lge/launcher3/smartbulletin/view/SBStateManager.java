package com.lge.launcher3.smartbulletin.view;

import com.lge.launcher3.smartbulletin.log.SBLog;

/* JADX INFO: loaded from: classes.dex */
public class SBStateManager {
    private static SBState sOldState;
    private static SBState sState;

    public enum SBState {
        OPEN,
        COLLAPSE
    }

    public static void onChangeState(SBState state) {
        SBState sBState = sState;
        if (sBState == state) {
            return;
        }
        sOldState = sBState;
        sState = state;
        SBLog.d("SBStateManager", "sOldState = " + sBState + ", sState = " + state);
    }

    public static SBState getState() {
        return sState;
    }

    public static SBState getOldState() {
        return sOldState;
    }
}
