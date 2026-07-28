package com.android.quickstep.fallback;

import android.content.Context;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.uioverrides.states.OverviewModalTaskState;
import com.android.quickstep.RecentsActivity;

/* JADX INFO: loaded from: classes.dex */
public class RecentsState implements BaseState<RecentsState> {
    public static final RecentsState BACKGROUND_APP;
    public static final RecentsState DEFAULT;
    private static final int FLAG_FULL_SCREEN;
    private static final int FLAG_HAS_BUTTONS;
    private static final int FLAG_LIVE_TILE;
    private static final int FLAG_MODAL;
    private static final int FLAG_OVERVIEW_ACTIONS;
    private static final int FLAG_OVERVIEW_UI;
    private static final int FLAG_RECOMMANDED;
    private static final int FLAG_SCRIM;
    private static final int FLAG_SHOW_AS_GRID;
    public static final RecentsState MODAL_TASK;
    private static final float NO_OFFSET = 0.0f;
    private static final float NO_SCALE = 1.0f;
    public static final RecentsState OVERVIEW_SPLIT_SELECT;
    private final int mFlags;
    public final int ordinal;

    @Override // com.android.launcher3.statemanager.BaseState
    public int getTransitionDuration(Context context) {
        return 250;
    }

    static {
        int flag = BaseState.getFlag(0);
        FLAG_MODAL = flag;
        int flag2 = BaseState.getFlag(1);
        FLAG_HAS_BUTTONS = flag2;
        int flag3 = BaseState.getFlag(2);
        FLAG_FULL_SCREEN = flag3;
        FLAG_OVERVIEW_ACTIONS = BaseState.getFlag(3);
        int flag4 = BaseState.getFlag(4);
        FLAG_SHOW_AS_GRID = flag4;
        int flag5 = BaseState.getFlag(5);
        FLAG_SCRIM = flag5;
        FLAG_LIVE_TILE = BaseState.getFlag(6);
        int flag6 = BaseState.getFlag(7);
        FLAG_OVERVIEW_UI = flag6;
        int flag7 = BaseState.getFlag(8);
        FLAG_RECOMMANDED = flag7;
        DEFAULT = new RecentsState(0, flag2 | flag7);
        MODAL_TASK = new ModalState(1, flag | flag2 | 2 | flag7);
        BACKGROUND_APP = new BackgroundAppState(2, flag3 | 3);
        OVERVIEW_SPLIT_SELECT = new RecentsState(5, flag4 | flag5 | flag6);
    }

    public RecentsState(int id, int flags) {
        this.ordinal = id;
        this.mFlags = flags;
    }

    public String toString() {
        return "Ordinal-" + this.ordinal;
    }

    @Override // com.android.launcher3.statemanager.BaseState
    public final boolean hasFlag(int mask) {
        return (mask & this.mFlags) != 0;
    }

    /* JADX DEBUG: Method merged with bridge method: getHistoryForState(Lcom/android/launcher3/statemanager/BaseState;)Lcom/android/launcher3/statemanager/BaseState; */
    @Override // com.android.launcher3.statemanager.BaseState
    public RecentsState getHistoryForState(RecentsState previousState) {
        return DEFAULT;
    }

    public float getOverviewModalness() {
        return hasFlag(FLAG_MODAL) ? 1.0f : 0.0f;
    }

    public boolean isFullScreen() {
        return hasFlag(FLAG_FULL_SCREEN);
    }

    public boolean hasButtons() {
        return hasFlag(FLAG_HAS_BUTTONS);
    }

    public float[] getOverviewScaleAndOffset(RecentsActivity activity) {
        return new float[]{1.0f, 0.0f};
    }

    private static class ModalState extends RecentsState {
        @Override // com.android.quickstep.fallback.RecentsState, com.android.launcher3.statemanager.BaseState
        public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState previousState) {
            return super.getHistoryForState((RecentsState) previousState);
        }

        public ModalState(int id, int flags) {
            super(id, flags);
        }

        @Override // com.android.quickstep.fallback.RecentsState
        public float[] getOverviewScaleAndOffset(RecentsActivity activity) {
            return OverviewModalTaskState.getOverviewScaleAndOffsetForModalState(activity);
        }
    }

    private static class BackgroundAppState extends RecentsState {
        @Override // com.android.quickstep.fallback.RecentsState, com.android.launcher3.statemanager.BaseState
        public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState previousState) {
            return super.getHistoryForState((RecentsState) previousState);
        }

        public BackgroundAppState(int id, int flags) {
            super(id, flags);
        }

        @Override // com.android.quickstep.fallback.RecentsState
        public float[] getOverviewScaleAndOffset(RecentsActivity activity) {
            return com.android.launcher3.uioverrides.states.BackgroundAppState.getOverviewScaleAndOffsetForBackgroundState(activity);
        }
    }

    @Override // com.android.launcher3.statemanager.BaseState
    public boolean hasRecommand() {
        return hasFlag(FLAG_RECOMMANDED);
    }
}
