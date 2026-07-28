package com.android.launcher3.statemanager;

import android.content.Context;
import com.android.launcher3.statemanager.BaseState;

/* JADX INFO: loaded from: classes.dex */
public interface BaseState<T extends BaseState> {
    public static final int FLAG_DISABLE_RESTORE = 2;
    public static final int FLAG_NON_INTERACTIVE = 1;

    static int getFlag(int index) {
        return 1 << (index + 2);
    }

    T getHistoryForState(T previousState);

    int getTransitionDuration(Context context);

    boolean hasFlag(int flagMask);

    boolean hasRecommand();

    default boolean shouldDisableRestore() {
        return hasFlag(2);
    }
}
