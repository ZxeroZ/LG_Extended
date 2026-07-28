package com.android.launcher3;

import android.content.Context;
import android.view.animation.Interpolator;
import androidx.core.view.InputDeviceCompat;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.AnimatorSetBuilder;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.HintState;
import com.android.launcher3.states.SpringLoadedState;
import com.android.launcher3.uioverrides.states.AllAppsState;
import com.android.launcher3.uioverrides.states.OverviewState;
import com.lge.launcher3.uioverrides.InAppsState;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.WindowUtils;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public abstract class LauncherState implements BaseState<LauncherState> {
    public static final LauncherState ALL_APPS;
    public static final int ALL_APPS_CONTENT = 16;
    public static final int ALL_APPS_HEADER = 4;
    public static final int ALL_APPS_HEADER_EXTRA = 8;
    public static final LauncherState APPS_SPRING_LOADED;
    public static final int APPS_VIEW_ITEM_MASK = 30;
    public static final LauncherState BACKGROUND_APP;
    public static final LauncherState CLEAN_VIEW;
    protected static final PageAlphaProvider DEFAULT_ALPHA_PROVIDER;
    public static final LauncherState DYNAMIC_GRID_OVERVIEW;
    public static final int FLAG_CLOSE_POPUPS;
    protected static final int FLAG_DISABLE_ACCESSIBILITY;
    protected static final int FLAG_DISABLE_INTERACTION;
    protected static final int FLAG_DISABLE_PAGE_CLIPPING;
    protected static final int FLAG_HAS_SYS_UI_SCRIM;
    protected static final int FLAG_HIDE_BACK_BUTTON;
    public static final int FLAG_HIDE_FREEFORM_POPUPS;
    protected static final int FLAG_MULTI_PAGE;
    protected static final int FLAG_OVERVIEW_UI;
    protected static final int FLAG_PAGE_BACKGROUNDS;
    protected static final int FLAG_USE_BLUR;
    protected static final int FLAG_USE_MOTION;
    protected static final int FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED;
    public static final int FLAG_WORKSPACE_INACCESSIBLE;
    public static final LauncherState HINT_STATE;
    public static final int HOTSEAT_ICONS = 1;
    public static final int HOTSEAT_SEARCH_BOX = 2;
    public static final LauncherState INAPPS;
    public static final int NONE = 0;
    public static final LauncherState NORMAL;
    public static final float NO_OFFSET = 0.0f;
    public static final float NO_SCALE = 1.0f;
    public static final LauncherState OVERVIEW;
    public static final int OVERVIEW_BUTTONS = 64;
    public static final LauncherState OVERVIEW_MODAL_TASK;
    public static final LauncherState OVERVIEW_PEEK;
    public static final LauncherState OVERVIEW_SPLIT_SELECT;
    public static final LauncherState QUICK_SWITCH;
    public static final int RECENTS_CLEAR_ALL_BUTTON = 128;
    public static final int RECOMMENDED_LAYOUT = 256;
    public static final int SPLIT_PLACHOLDER_VIEW = 1024;
    public static final LauncherState SPRING_LOADED;
    private static final int STATE_SPRING_LOADED_FLAGS;
    protected static final PageAlphaProvider SWIVEL_ALPHA_PROVIDER;
    public static final int VERTICAL_SWIPE_INDICATOR = 32;
    public static final LauncherState WIDGETS;
    public static final LauncherState WIDGETS_SPRING_LOADED;
    public static final int WORKSPACE_INDICATOR = 512;
    private static final LauncherState[] sAllStates;
    public final int containerType;
    public final boolean disableInteraction;
    public final boolean disablePageClipping;
    public final boolean disableRestore;
    public final boolean hasMultipleVisiblePages;
    public final boolean hasSysUiScrim;
    public final boolean hasWorkspacePageBackground;
    public final boolean hideBackButton;
    private final int mFlags;
    public final int ordinal;
    public final boolean overviewUi;
    public boolean skipAtomicAnim = false;
    public final int transitionDuration;
    public final boolean useBlur;
    public final boolean useMotion;
    public final int workspaceAccessibilityFlag;
    public final boolean workspaceIconsCanBeDragged;

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: ?: TERNARY null = ((r1v0 float) < (1.0f float)) ? (0.0f float) : (1.0f float) */
    static /* synthetic */ float lambda$prepareForAtomicAnimation$0(float f) {
        return f < 1.0f ? 0.0f : 1.0f;
    }

    protected float getDepthUnchecked(Context context) {
        return 0.0f;
    }

    public float getOverviewFullscreenProgress() {
        return 0.0f;
    }

    public float getOverviewModalness() {
        return 0.0f;
    }

    public float getOverviewScrimAlpha(Launcher launcher) {
        return 0.0f;
    }

    public float getOverviewSecondaryTranslation(Launcher launcher) {
        return 0.0f;
    }

    public float getSplitSelectTranslation(Launcher launcher) {
        return 0.0f;
    }

    public float getVerticalProgress(Launcher launcher) {
        return 1.0f;
    }

    public float getWorkspaceScrimAlpha(Launcher launcher) {
        return 0.0f;
    }

    public void onStateDisabled(Launcher launcher) {
    }

    static {
        int i = 0;
        int flag = BaseState.getFlag(0);
        FLAG_MULTI_PAGE = flag;
        int i2 = 1;
        int flag2 = BaseState.getFlag(1);
        FLAG_DISABLE_ACCESSIBILITY = flag2;
        int flag3 = BaseState.getFlag(2);
        FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED = flag3;
        int flag4 = BaseState.getFlag(3);
        FLAG_DISABLE_PAGE_CLIPPING = flag4;
        int flag5 = BaseState.getFlag(4);
        FLAG_PAGE_BACKGROUNDS = flag5;
        FLAG_DISABLE_INTERACTION = BaseState.getFlag(5);
        FLAG_OVERVIEW_UI = BaseState.getFlag(6);
        int flag6 = BaseState.getFlag(7);
        FLAG_HIDE_BACK_BUTTON = flag6;
        int flag7 = BaseState.getFlag(8);
        FLAG_HAS_SYS_UI_SCRIM = flag7;
        FLAG_USE_BLUR = BaseState.getFlag(9);
        int flag8 = BaseState.getFlag(10);
        FLAG_USE_MOTION = flag8;
        FLAG_WORKSPACE_INACCESSIBLE = BaseState.getFlag(11);
        FLAG_CLOSE_POPUPS = BaseState.getFlag(12);
        FLAG_HIDE_FREEFORM_POPUPS = BaseState.getFlag(13);
        DEFAULT_ALPHA_PROVIDER = new PageAlphaProvider(Interpolators.ACCEL_2) { // from class: com.android.launcher3.LauncherState.1
            @Override // com.android.launcher3.LauncherState.PageAlphaProvider
            public float getPageAlpha(int pageIndex) {
                return 1.0f;
            }
        };
        SWIVEL_ALPHA_PROVIDER = new PageAlphaProvider(Interpolators.ACCEL_2) { // from class: com.android.launcher3.LauncherState.2
            @Override // com.android.launcher3.LauncherState.PageAlphaProvider
            public float getPageAlpha(int pageIndex) {
                return 0.0f;
            }
        };
        sAllStates = new LauncherState[16];
        NORMAL = new LauncherState(i, i2, i, flag3 | 2 | flag6 | flag7 | flag8) { // from class: com.android.launcher3.LauncherState.3
            @Override // com.android.launcher3.statemanager.BaseState
            public int getTransitionDuration(Context context) {
                return 0;
            }

            @Override // com.android.launcher3.LauncherState, com.android.launcher3.statemanager.BaseState
            public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState previousState) {
                return super.getHistoryForState((LauncherState) previousState);
            }
        };
        int i3 = flag | flag2 | 2 | flag3 | flag4 | flag5 | flag6;
        STATE_SPRING_LOADED_FLAGS = i3;
        SPRING_LOADED = new SpringLoadedState(1);
        ALL_APPS = new AllAppsState(6);
        HINT_STATE = new HintState(8);
        OVERVIEW = new OverviewState(2);
        OVERVIEW_PEEK = OverviewState.newPeekState(3);
        OVERVIEW_MODAL_TASK = OverviewState.newModalTaskState(4);
        QUICK_SWITCH = OverviewState.newSwitchState(5);
        BACKGROUND_APP = OverviewState.newBackgroundState(7);
        OVERVIEW_SPLIT_SELECT = OverviewState.newSplitSelectState(9);
        WIDGETS = new LauncherState(10, i2, i, i) { // from class: com.android.launcher3.LauncherState.4
            @Override // com.android.launcher3.statemanager.BaseState
            public int getTransitionDuration(Context context) {
                return 0;
            }

            @Override // com.android.launcher3.LauncherState, com.android.launcher3.statemanager.BaseState
            public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState previousState) {
                return super.getHistoryForState((LauncherState) previousState);
            }
        };
        CLEAN_VIEW = new LauncherState(11, i2, i, flag8 | 2) { // from class: com.android.launcher3.LauncherState.5
            @Override // com.android.launcher3.statemanager.BaseState
            public int getTransitionDuration(Context context) {
                return 0;
            }

            @Override // com.android.launcher3.LauncherState, com.android.launcher3.statemanager.BaseState
            public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState previousState) {
                return super.getHistoryForState((LauncherState) previousState);
            }
        };
        int i4 = 150;
        APPS_SPRING_LOADED = new LauncherState(12, i2, i4, i3) { // from class: com.android.launcher3.LauncherState.6
            @Override // com.android.launcher3.statemanager.BaseState
            public int getTransitionDuration(Context context) {
                return 0;
            }

            @Override // com.android.launcher3.LauncherState, com.android.launcher3.statemanager.BaseState
            public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState previousState) {
                return super.getHistoryForState((LauncherState) previousState);
            }
        };
        WIDGETS_SPRING_LOADED = new LauncherState(13, i2, i4, i3) { // from class: com.android.launcher3.LauncherState.7
            @Override // com.android.launcher3.statemanager.BaseState
            public int getTransitionDuration(Context context) {
                return 0;
            }

            @Override // com.android.launcher3.LauncherState, com.android.launcher3.statemanager.BaseState
            public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState previousState) {
                return super.getHistoryForState((LauncherState) previousState);
            }
        };
        DYNAMIC_GRID_OVERVIEW = new LauncherState(14, i2, i, i) { // from class: com.android.launcher3.LauncherState.8
            @Override // com.android.launcher3.statemanager.BaseState
            public int getTransitionDuration(Context context) {
                return 0;
            }

            @Override // com.android.launcher3.LauncherState, com.android.launcher3.statemanager.BaseState
            public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState previousState) {
                return super.getHistoryForState((LauncherState) previousState);
            }
        };
        INAPPS = new InAppsState(15);
    }

    public LauncherState(int id, int containerType, int transitionDuration, int flags) {
        this.containerType = containerType;
        this.transitionDuration = transitionDuration;
        this.mFlags = flags;
        this.hasWorkspacePageBackground = (FLAG_PAGE_BACKGROUNDS & flags) != 0;
        this.hasMultipleVisiblePages = (FLAG_MULTI_PAGE & flags) != 0;
        this.workspaceAccessibilityFlag = (FLAG_DISABLE_ACCESSIBILITY & flags) != 0 ? 4 : 0;
        this.disableRestore = (flags & 2) != 0;
        this.workspaceIconsCanBeDragged = (FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED & flags) != 0;
        this.disablePageClipping = (FLAG_DISABLE_PAGE_CLIPPING & flags) != 0;
        this.disableInteraction = (FLAG_DISABLE_INTERACTION & flags) != 0;
        this.overviewUi = (FLAG_OVERVIEW_UI & flags) != 0;
        this.hideBackButton = (FLAG_HIDE_BACK_BUTTON & flags) != 0;
        this.hasSysUiScrim = (FLAG_HAS_SYS_UI_SCRIM & flags) != 0;
        this.useBlur = (FLAG_USE_BLUR & flags) != 0;
        this.useMotion = (FLAG_USE_MOTION & flags) != 0;
        this.ordinal = id;
        sAllStates[id] = this;
    }

    public static LauncherState[] values() {
        LauncherState[] launcherStateArr = sAllStates;
        return (LauncherState[]) Arrays.copyOf(launcherStateArr, launcherStateArr.length);
    }

    public ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        return new ScaleAndTranslation(1.0f, 0.0f, 0.0f);
    }

    public ScaleAndTranslation getHotseatScaleAndTranslation(Launcher launcher) {
        return getWorkspaceScaleAndTranslation(launcher);
    }

    public float[] getOverviewScaleAndOffset(Launcher launcher) {
        return launcher.getNormalOverviewScaleAndOffset();
    }

    public ScaleAndTranslation getQsbScaleAndTranslation(Launcher launcher) {
        return new ScaleAndTranslation(1.0f, 0.0f, 0.0f);
    }

    public void onStateEnabled(Launcher launcher) {
        dispatchWindowStateChanged(launcher);
        WindowUtils.addFlagForFreeform(launcher.getWindow(), false);
    }

    public int getVisibleElements(Launcher launcher) {
        if (launcher.getWorkspace() == null || launcher.getWorkspace().getState() != Workspace.State.NORMAL) {
            return 512;
        }
        return InputDeviceCompat.SOURCE_DPAD;
    }

    public boolean areElementsVisible(Launcher launcher, int elements) {
        return (getVisibleElements(launcher) & elements) == elements;
    }

    public final float getDepth(Context context) {
        return getDepth(context, BaseDraggingActivity.fromContext(context).getDeviceProfile().isMultiWindowMode);
    }

    public final float getDepth(Context context, boolean isMultiWindowMode) {
        if (isMultiWindowMode) {
            return 0.0f;
        }
        return getDepthUnchecked(context);
    }

    public String getDescription(Launcher launcher) {
        return launcher.getWorkspace().getCurrentPageDescription();
    }

    public PageAlphaProvider getWorkspacePageAlphaProvider(Launcher launcher) {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            return SWIVEL_ALPHA_PROVIDER;
        }
        if (this != NORMAL || !launcher.getDeviceProfile().shouldFadeAdjacentWorkspaceScreens()) {
            return DEFAULT_ALPHA_PROVIDER;
        }
        Workspace workspace = launcher.getWorkspace();
        final int nextPage = workspace != null ? workspace.getNextPage() : -1;
        return new PageAlphaProvider(Interpolators.ACCEL_2) { // from class: com.android.launcher3.LauncherState.9
            @Override // com.android.launcher3.LauncherState.PageAlphaProvider
            public float getPageAlpha(int pageIndex) {
                return pageIndex != nextPage ? 0.0f : 1.0f;
            }
        };
    }

    /* JADX DEBUG: Method merged with bridge method: getHistoryForState(Lcom/android/launcher3/statemanager/BaseState;)Lcom/android/launcher3/statemanager/BaseState; */
    @Override // com.android.launcher3.statemanager.BaseState
    public LauncherState getHistoryForState(LauncherState previousState) {
        return NORMAL;
    }

    @Override // com.android.launcher3.statemanager.BaseState
    public boolean hasFlag(int mask) {
        return (mask & this.mFlags) != 0;
    }

    public void onStateTransitionEnd(Launcher launcher) {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            launcher.getRotationHelper().setCurrentStateRequest(1);
        } else {
            launcher.getRotationHelper().setCurrentStateRequest(!launcher.isStarted());
        }
    }

    public void onBackPressed(Launcher launcher) {
        if (this != NORMAL) {
            StateManager<LauncherState> stateManager = launcher.getStateManager();
            stateManager.goToState((LauncherState) stateManager.getLastState());
        }
    }

    public void prepareForAtomicAnimation(Launcher launcher, LauncherState fromState, AnimatorSetBuilder builder) {
        LauncherState launcherState = NORMAL;
        if (this == launcherState && fromState == OVERVIEW) {
            builder.setInterpolator(1, Interpolators.DEACCEL);
            builder.setInterpolator(3, Interpolators.ACCEL);
            builder.setInterpolator(6, Interpolators.clampToProgress(Interpolators.ACCEL, 0.0f, 0.9f));
            builder.setInterpolator(7, Interpolators.ACCEL);
            builder.setInterpolator(9, Interpolators.DEACCEL_1_7);
            return;
        }
        if (this == launcherState && fromState == OVERVIEW_PEEK) {
            builder.setInterpolator(9, new Interpolator() { // from class: com.android.launcher3.-$$Lambda$LauncherState$tXP9KctGQVmal3ETQMOt7VbEnpA
                @Override // android.animation.TimeInterpolator
                public final float getInterpolation(float f) {
                    return LauncherState.lambda$prepareForAtomicAnimation$0(f);
                }
            });
        }
    }

    protected static void dispatchWindowStateChanged(Launcher launcher) {
        launcher.getWindow().getDecorView().sendAccessibilityEvent(32);
    }

    public static abstract class PageAlphaProvider {
        public final Interpolator interpolator;

        public abstract float getPageAlpha(int pageIndex);

        public PageAlphaProvider(Interpolator interpolator) {
            this.interpolator = interpolator;
        }
    }

    public static class ScaleAndTranslation {
        public float scale;
        public float translationX;
        public float translationY;

        public ScaleAndTranslation() {
        }

        public ScaleAndTranslation(float scale, float translationX, float translationY) {
            this.scale = scale;
            this.translationX = translationX;
            this.translationY = translationY;
        }
    }

    @Override // com.android.launcher3.statemanager.BaseState
    public boolean hasRecommand() {
        return hasFlag(256);
    }

    public String toString() {
        return LauncherState.class.getSimpleName() + " - " + this.ordinal;
    }
}
