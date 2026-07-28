package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.Launcher;
import com.android.launcher3.QuickstepTransitionManager;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.TraceHelper;
import com.android.launcher3.util.VibratorWrapper;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.SwipeUpAnimationLogic;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.inputconsumers.OverviewInputConsumer;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.quickstep.util.ShelfPeekAnim;
import com.android.quickstep.views.LiveTileOverlay;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseSwipeUpHandlerV2<T extends StatefulActivity<?>, Q extends RecentsView> extends BaseSwipeUpHandler<T, Q> implements View.OnApplyWindowInsetsListener {
    private static final boolean DEBUG = true;
    private static final int LAUNCHER_UI_STATES;
    private static final int LOG_NO_OP_PAGE_INDEX = -1;
    public static final long MAX_SWIPE_DURATION = 350;
    public static final long MIN_OVERSHOOT_DURATION = 120;
    public static final float MIN_PROGRESS_FOR_OVERVIEW = 0.7f;
    public static final long RECENTS_ATTACH_DURATION = 300;
    private static final String SCREENSHOT_CAPTURED_EVT = "ScreenshotCaptured";
    private static final int STATE_APP_CONTROLLER_RECEIVED;
    private static final int STATE_CAPTURE_SCREENSHOT;
    private static final int STATE_CURRENT_TASK_FINISHED;
    private static final int STATE_GESTURE_CANCELLED;
    private static final int STATE_GESTURE_COMPLETED;
    private static final int STATE_GESTURE_STARTED;
    protected static final int STATE_HANDLER_INVALIDATED;
    protected static final int STATE_LAUNCHER_DRAWN;
    protected static final int STATE_LAUNCHER_PRESENT;
    protected static final int STATE_LAUNCHER_STARTED;
    private static final String[] STATE_NAMES = null;
    private static final int STATE_RESUME_LAST_TASK;
    private static final int STATE_SCALED_CONTROLLER_HOME;
    private static final int STATE_SCALED_CONTROLLER_RECENTS;
    protected static final int STATE_SCREENSHOT_CAPTURED;
    private static final int STATE_SCREENSHOT_VIEW_SHOWN;
    private static final int STATE_START_NEW_TASK;
    private static final float SWIPE_DURATION_MULTIPLIER;
    private static final String TAG = "BaseSwipeUpHandlerV2";
    private static final String WINDOW_ANIM_TAG = "[WindowAnim] ";
    private TaskStackChangeListener mActivityRestartListener;
    private BaseActivityInterface.AnimationFactory mAnimationFactory;
    private boolean mContinuingLastGesture;
    private PointF mDownPos;
    private boolean mGestureStarted;
    private boolean mHasLauncherTransitionControllerStarted;
    AnimatorSet mHomeAnimator;
    private boolean mIsLikelyToStartNewTask;
    private boolean mIsShelfPeeking;
    private long mLauncherFrameDrawnTime;
    private AnimatorPlaybackController mLauncherTransitionController;
    private int mLogAction;
    private int mLogDirection;
    private final Runnable mOnDeferredActivityLaunch;
    private boolean mPassedOverviewThreshold;
    private SwipeUpAnimationLogic.RunningWindowAnim mRunningWindowAnim;
    protected final TaskAnimationManager mTaskAnimationManager;
    private ThumbnailData mTaskSnapshot;
    private final long mTouchTimeMs;
    private boolean mWasLauncherAlreadyVisible;

    private static int getFlagForIndex(int index, String name) {
        return 1 << index;
    }

    static /* synthetic */ float lambda$animateToProgressInternal$10(float f, float f2) {
        return f;
    }

    static /* synthetic */ void lambda$new$0(long j) {
    }

    protected abstract SwipeUpAnimationLogic.HomeAnimationFactory createHomeAnimationFactory(long duration);

    protected abstract void finishRecentsControllerToHome(Runnable callback);

    static {
        int flagForIndex = getFlagForIndex(0, "STATE_LAUNCHER_PRESENT");
        STATE_LAUNCHER_PRESENT = flagForIndex;
        int flagForIndex2 = getFlagForIndex(1, "STATE_LAUNCHER_STARTED");
        STATE_LAUNCHER_STARTED = flagForIndex2;
        int flagForIndex3 = getFlagForIndex(2, "STATE_LAUNCHER_DRAWN");
        STATE_LAUNCHER_DRAWN = flagForIndex3;
        STATE_APP_CONTROLLER_RECEIVED = getFlagForIndex(3, "STATE_APP_CONTROLLER_RECEIVED");
        STATE_SCALED_CONTROLLER_HOME = getFlagForIndex(4, "STATE_SCALED_CONTROLLER_HOME");
        STATE_SCALED_CONTROLLER_RECENTS = getFlagForIndex(5, "STATE_SCALED_CONTROLLER_RECENTS");
        STATE_HANDLER_INVALIDATED = getFlagForIndex(6, "STATE_HANDLER_INVALIDATED");
        STATE_GESTURE_STARTED = getFlagForIndex(7, "STATE_GESTURE_STARTED");
        STATE_GESTURE_CANCELLED = getFlagForIndex(8, "STATE_GESTURE_CANCELLED");
        STATE_GESTURE_COMPLETED = getFlagForIndex(9, "STATE_GESTURE_COMPLETED");
        STATE_CAPTURE_SCREENSHOT = getFlagForIndex(10, "STATE_CAPTURE_SCREENSHOT");
        STATE_SCREENSHOT_CAPTURED = getFlagForIndex(11, "STATE_SCREENSHOT_CAPTURED");
        STATE_SCREENSHOT_VIEW_SHOWN = getFlagForIndex(12, "STATE_SCREENSHOT_VIEW_SHOWN");
        STATE_RESUME_LAST_TASK = getFlagForIndex(13, "STATE_RESUME_LAST_TASK");
        STATE_START_NEW_TASK = getFlagForIndex(14, "STATE_START_NEW_TASK");
        STATE_CURRENT_TASK_FINISHED = getFlagForIndex(15, "STATE_CURRENT_TASK_FINISHED");
        LAUNCHER_UI_STATES = flagForIndex | flagForIndex3 | flagForIndex2;
        SWIPE_DURATION_MULTIPLIER = Math.min(1.4285715f, 3.3333333f);
    }

    public BaseSwipeUpHandlerV2(Context context, RecentsAnimationDeviceState deviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, long touchTimeMs, boolean continuingLastGesture, InputConsumerController inputConsumer) {
        super(context, deviceState, gestureState, inputConsumer);
        this.mAnimationFactory = new BaseActivityInterface.AnimationFactory() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$1ahh_R2GYo_8wamX2hWNOua1uYQ
            @Override // com.android.quickstep.BaseActivityInterface.AnimationFactory
            public final void createActivityInterface(long j) {
                BaseSwipeUpHandlerV2.lambda$new$0(j);
            }
        };
        this.mLogAction = 3;
        this.mLogDirection = 1;
        this.mOnDeferredActivityLaunch = new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$9tLPnb0P3D-daaqnMnMDPaQEq9Y
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.onDeferredActivityLaunch();
            }
        };
        this.mActivityRestartListener = new TaskStackChangeListener() { // from class: com.android.quickstep.BaseSwipeUpHandlerV2.2
            @Override // com.android.systemui.shared.system.TaskStackChangeListener
            public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo task, boolean homeTaskVisible, boolean clearedTask, boolean wasVisible) {
                if (task.taskId == BaseSwipeUpHandlerV2.this.mGestureState.getRunningTaskId()) {
                    BaseSwipeUpHandlerV2.this.endRunningWindowAnim(true);
                    ActivityManagerWrapper.getInstance().unregisterTaskStackListener(BaseSwipeUpHandlerV2.this.mActivityRestartListener);
                    String packageName = (BaseSwipeUpHandlerV2.this.mGestureState.getHomeIntent() == null || BaseSwipeUpHandlerV2.this.mGestureState.getHomeIntent().getComponent() == null) ? null : BaseSwipeUpHandlerV2.this.mGestureState.getHomeIntent().getComponent().getPackageName();
                    String packageName2 = task.baseActivity != null ? task.baseActivity.getPackageName() : null;
                    if (packageName == null || packageName2 == null || !packageName.equals(packageName2)) {
                        ActivityManagerWrapper.getInstance().startActivityFromRecents(task.taskId, (ActivityOptions) null);
                    }
                }
            }
        };
        this.mTaskAnimationManager = taskAnimationManager;
        this.mTouchTimeMs = touchTimeMs;
        this.mContinuingLastGesture = continuingLastGesture;
        initAfterSubclassConstructor();
        initStateCallbacks();
    }

    private void initStateCallbacks() {
        this.mStateCallback = new MultiStateCallback(STATE_NAMES);
        MultiStateCallback multiStateCallback = this.mStateCallback;
        int i = STATE_LAUNCHER_PRESENT;
        int i2 = STATE_GESTURE_STARTED;
        multiStateCallback.runOnceAtState(i | i2, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$q8XocoNo9zA_kEewjC1KEL7K-ak
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.onLauncherPresentAndGestureStarted();
            }
        });
        MultiStateCallback multiStateCallback2 = this.mStateCallback;
        int i3 = STATE_LAUNCHER_DRAWN;
        multiStateCallback2.runOnceAtState(i3 | i2, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$lLNRhJzVEu-Xip2MUxeX6rEM9Pk
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.initializeLauncherAnimationController();
            }
        });
        this.mStateCallback.runOnceAtState(i | i3, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$Fzk8AxjH8GXF9y8n_KWSEYWvNyg
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.launcherFrameDrawn();
            }
        });
        this.mStateCallback.runOnceAtState(STATE_LAUNCHER_STARTED | i | STATE_GESTURE_CANCELLED, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$hzzDOYtUCCPLvzcE4za5m_90iWk
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.resetStateForAnimationCancel();
            }
        });
        MultiStateCallback multiStateCallback3 = this.mStateCallback;
        int i4 = STATE_RESUME_LAST_TASK;
        int i5 = STATE_APP_CONTROLLER_RECEIVED;
        multiStateCallback3.runOnceAtState(i4 | i5, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$VPPi_zUmFwfYYrKjqonTycjjDC0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.resumeLastTask();
            }
        });
        MultiStateCallback multiStateCallback4 = this.mStateCallback;
        int i6 = STATE_START_NEW_TASK;
        int i7 = STATE_SCREENSHOT_CAPTURED;
        multiStateCallback4.runOnceAtState(i6 | i7, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$lT879QMh27UqCvABqEx-XIprUS8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.startNewTask();
            }
        });
        MultiStateCallback multiStateCallback5 = this.mStateCallback;
        int i8 = STATE_CAPTURE_SCREENSHOT;
        multiStateCallback5.runOnceAtState(i | i5 | i3 | i8, new Runnable() { // from class: com.android.quickstep.-$$Lambda$jrV23LArb_z0JVyUrp9spi6HVCA
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.switchToScreenshot();
            }
        });
        MultiStateCallback multiStateCallback6 = this.mStateCallback;
        int i9 = STATE_GESTURE_COMPLETED;
        int i10 = STATE_SCALED_CONTROLLER_RECENTS;
        multiStateCallback6.runOnceAtState(i7 | i9 | i10, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$sbf-pdruQjKzieBwE7RPHAzIM8Y
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.finishCurrentTransitionToRecents();
            }
        });
        MultiStateCallback multiStateCallback7 = this.mStateCallback;
        int i11 = STATE_SCALED_CONTROLLER_HOME;
        multiStateCallback7.runOnceAtState(i7 | i9 | i11, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$x5QWW0k8SQxssjq3Tx_sxvJjO6A
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.finishCurrentTransitionToHome();
            }
        });
        MultiStateCallback multiStateCallback8 = this.mStateCallback;
        int i12 = STATE_CURRENT_TASK_FINISHED;
        multiStateCallback8.runOnceAtState(i11 | i12, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$CZAdjd8cZEub1JamxAH_LuNv-2Q
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.reset();
            }
        });
        this.mStateCallback.runOnceAtState(i2 | i3 | i | i5 | i10 | i12 | i9, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$n3i4lllTXra6dlzAVON9iKwb0oI
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.setupLauncherUiAfterSwipeUpToRecentsAnimation();
            }
        });
        this.mGestureState.runOnceAtState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED, new $$Lambda$BaseSwipeUpHandlerV2$mzlHvDYwct0eTiHIKotSqfhDDls(this));
        this.mGestureState.runOnceAtState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED | GestureState.STATE_RECENTS_SCROLLING_FINISHED, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$frPfTQ-Rhw4OsFZey2rgL1Hh4Hc
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.onSettledOnEndTarget();
            }
        });
        MultiStateCallback multiStateCallback9 = this.mStateCallback;
        int i13 = STATE_HANDLER_INVALIDATED;
        multiStateCallback9.runOnceAtState(i13, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$8Ry-FQC5pdpYWDDsL2ivg4FsnqQ
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.invalidateHandler();
            }
        });
        this.mStateCallback.runOnceAtState(i | i13, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$nmC2hwE1z1Pqfe4KBjtIRecFhT0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.invalidateHandlerWithLauncher();
            }
        });
        this.mStateCallback.runOnceAtState(i13 | i4, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$YrkFbdDdt608TNoLkdH7o-HaTc4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.notifyTransitionCancelled();
            }
        });
        this.mGestureState.runOnceAtState(GestureState.STATE_END_TARGET_SET, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$Lk3VIDOAYJbEzFsZNx5uFA22Q3g
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$initStateCallbacks$1$BaseSwipeUpHandlerV2();
            }
        });
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            return;
        }
        this.mStateCallback.addChangeListener(i | i5 | STATE_SCREENSHOT_VIEW_SHOWN | i8, new Consumer() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$Jb2UQhoJfX6MsiKS55trmTwz960
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$initStateCallbacks$2$BaseSwipeUpHandlerV2((Boolean) obj);
            }
        });
    }

    public /* synthetic */ void lambda$initStateCallbacks$1$BaseSwipeUpHandlerV2() {
        this.mDeviceState.onEndTargetCalculated(this.mGestureState.getEndTarget(), this.mActivityInterface);
    }

    public /* synthetic */ void lambda$initStateCallbacks$2$BaseSwipeUpHandlerV2(Boolean bool) {
        this.mRecentsView.setRunningTaskHidden(!bool.booleanValue());
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    protected boolean onActivityInit(Boolean bool) {
        super.onActivityInit(bool);
        T t = (T) this.mActivityInterface.getCreatedActivity();
        if (this.mActivity == t) {
            return true;
        }
        if (this.mActivity != null) {
            int state = this.mStateCallback.getState() & (~LAUNCHER_UI_STATES);
            initStateCallbacks();
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(state);
        }
        this.mWasLauncherAlreadyVisible = bool.booleanValue();
        this.mActivity = t;
        if (bool.booleanValue()) {
            this.mActivity.clearForceInvisibleFlag(9);
        } else {
            this.mActivity.addForceInvisibleFlag(9);
        }
        this.mRecentsView = (Q) t.getOverviewPanel();
        this.mRecentsView.setOnPageTransitionEndCallback(null);
        addLiveTileOverlay();
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_LAUNCHER_PRESENT);
        if (bool.booleanValue()) {
            onLauncherStart();
        } else {
            t.runOnceOnStart(new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$jz2BnBGJNSO0_Pzf2HrcNX6Ybmc
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.onLauncherStart();
                }
            });
        }
        setupRecentsViewUi();
        if (this.mDeviceState.getNavMode() == SysUINavigationMode.Mode.TWO_BUTTONS) {
            this.mActivityInterface.updateOverviewPredictionState();
        }
        linkRecentsViewScroll();
        return true;
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    protected boolean moveWindowWithRecentsScroll() {
        return this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.HOME;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLauncherStart() {
        StatefulActivity createdActivity = this.mActivityInterface.getCreatedActivity();
        if (this.mActivity == createdActivity && !this.mStateCallback.hasStates(STATE_HANDLER_INVALIDATED)) {
            this.mTaskViewSimulator.setRecentsConfiguration(this.mActivity.getResources().getConfiguration());
            if (this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.HOME) {
                Runnable runnable = new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$mCBVCNIwJ_5lLsZz6DUNG20lePM
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onLauncherStart$3$BaseSwipeUpHandlerV2();
                    }
                };
                if (this.mWasLauncherAlreadyVisible) {
                    this.mStateCallback.runOnceAtState(STATE_GESTURE_STARTED, runnable);
                } else {
                    runnable.run();
                }
            }
            AbstractFloatingView.closeAllOpenViewsExcept(createdActivity, this.mWasLauncherAlreadyVisible, 256);
            if (this.mWasLauncherAlreadyVisible) {
                this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_LAUNCHER_DRAWN);
            } else {
                Object objBeginSection = TraceHelper.INSTANCE.beginSection("WTS-init");
                BaseDragLayer dragLayer = createdActivity.getDragLayer();
                dragLayer.getViewTreeObserver().addOnDrawListener(new AnonymousClass1(objBeginSection, dragLayer, createdActivity));
            }
            createdActivity.getRootView().setOnApplyWindowInsetsListener(this);
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_LAUNCHER_STARTED);
        }
    }

    public /* synthetic */ void lambda$onLauncherStart$3$BaseSwipeUpHandlerV2() {
        this.mAnimationFactory = this.mActivityInterface.prepareRecentsUI(this.mDeviceState, this.mWasLauncherAlreadyVisible, new Consumer() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$2eJRO6D6hFUnon3uJ5d2D37owok
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.onAnimatorPlaybackControllerCreated((AnimatorPlaybackController) obj);
            }
        });
        maybeUpdateRecentsAttachedState(false);
    }

    /* JADX INFO: renamed from: com.android.quickstep.BaseSwipeUpHandlerV2$1, reason: invalid class name */
    class AnonymousClass1 implements ViewTreeObserver.OnDrawListener {
        boolean mHandled = false;
        final /* synthetic */ StatefulActivity val$activity;
        final /* synthetic */ View val$dragLayer;
        final /* synthetic */ Object val$traceToken;

        AnonymousClass1(final Object val$traceToken, final View val$dragLayer, final StatefulActivity val$activity) {
            this.val$traceToken = val$traceToken;
            this.val$dragLayer = val$dragLayer;
            this.val$activity = val$activity;
        }

        @Override // android.view.ViewTreeObserver.OnDrawListener
        public void onDraw() {
            if (this.mHandled) {
                return;
            }
            this.mHandled = true;
            TraceHelper.INSTANCE.endSection(this.val$traceToken);
            final View view = this.val$dragLayer;
            view.post(new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$1$_6-RdSEOie0oAuAHMKbbjPgeMDo
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onDraw$0$BaseSwipeUpHandlerV2$1(view);
                }
            });
            if (this.val$activity != BaseSwipeUpHandlerV2.this.mActivity) {
                return;
            }
            BaseSwipeUpHandlerV2.this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(BaseSwipeUpHandlerV2.STATE_LAUNCHER_DRAWN);
        }

        public /* synthetic */ void lambda$onDraw$0$BaseSwipeUpHandlerV2$1(View view) {
            view.getViewTreeObserver().removeOnDrawListener(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLauncherPresentAndGestureStarted() {
        setupRecentsViewUi();
        this.mGestureState.getActivityInterface().setOnDeferredActivityLaunchCallback(this.mOnDeferredActivityLaunch);
        notifyGestureStartedAsync();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDeferredActivityLaunch() {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            this.mActivityInterface.switchRunningTaskViewToScreenshot(null, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$XwsXxG-Bx5JKu57W1N8d8KubnVM
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onDeferredActivityLaunch$4$BaseSwipeUpHandlerV2();
                }
            });
        } else {
            this.mTaskAnimationManager.finishRunningRecentsAnimation(true);
        }
    }

    public /* synthetic */ void lambda$onDeferredActivityLaunch$4$BaseSwipeUpHandlerV2() {
        this.mTaskAnimationManager.finishRunningRecentsAnimation(true);
    }

    private void setupRecentsViewUi() {
        if (this.mContinuingLastGesture) {
            updateSysUiFlags(this.mCurrentShift.value);
        } else {
            notifyGestureAnimationStartToRecents();
        }
    }

    protected void notifyGestureAnimationStartToRecents() {
        this.mRecentsView.onGestureAnimationStart(this.mGestureState.getRunningTaskId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launcherFrameDrawn() {
        this.mLauncherFrameDrawnTime = SystemClock.uptimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeLauncherAnimationController() {
        buildAnimationController();
        TraceHelper.INSTANCE.endSection(TraceHelper.INSTANCE.beginSection("logToggleRecents", 2));
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).getThumbnailCache().getHighResLoadingState().setVisible(true);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    public void onMotionPauseChanged(boolean isPaused) {
        setShelfState(isPaused ? ShelfPeekAnim.ShelfAnimState.PEEK : ShelfPeekAnim.ShelfAnimState.HIDE, ShelfPeekAnim.INTERPOLATOR, 240L);
        if (this.mDeviceState.isFullyGesturalNavMode() && isPaused) {
            this.mActivityInterface.updateOverviewPredictionState();
        }
    }

    public void maybeUpdateRecentsAttachedState() {
        maybeUpdateRecentsAttachedState(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeUpdateRecentsAttachedState(boolean animate) {
        if (!this.mDeviceState.isFullyGesturalNavMode() || this.mRecentsView == null) {
            return;
        }
        RemoteAnimationTargetCompat remoteAnimationTargetCompatFindTask = this.mRecentsAnimationTargets != null ? this.mRecentsAnimationTargets.findTask(this.mGestureState.getRunningTaskId()) : null;
        boolean z = true;
        if (this.mGestureState.getEndTarget() != null) {
            z = this.mGestureState.getEndTarget().recentsAttachedToAppWindow;
        } else if ((!this.mContinuingLastGesture || this.mRecentsView.getRunningTaskIndex() == this.mRecentsView.getNextPage()) && ((remoteAnimationTargetCompatFindTask == null || !isNotInRecents(remoteAnimationTargetCompatFindTask)) && !this.mIsShelfPeeking && !this.mIsLikelyToStartNewTask)) {
            z = false;
        }
        this.mAnimationFactory.setRecentsAttachedToAppWindow(z, animate);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    public void setIsLikelyToStartNewTask(boolean isLikelyToStartNewTask) {
        setIsLikelyToStartNewTask(isLikelyToStartNewTask, true);
    }

    private void setIsLikelyToStartNewTask(boolean isLikelyToStartNewTask, boolean animate) {
        if (this.mIsLikelyToStartNewTask != isLikelyToStartNewTask) {
            this.mIsLikelyToStartNewTask = isLikelyToStartNewTask;
            maybeUpdateRecentsAttachedState(animate);
        }
    }

    public void setShelfState(ShelfPeekAnim.ShelfAnimState shelfState, Interpolator interpolator, long duration) {
        this.mAnimationFactory.setShelfState(shelfState, interpolator, duration);
        boolean z = this.mIsShelfPeeking;
        boolean z2 = shelfState == ShelfPeekAnim.ShelfAnimState.PEEK;
        this.mIsShelfPeeking = z2;
        if (z2 != z) {
            maybeUpdateRecentsAttachedState();
        }
        if (shelfState.shouldPreformHaptic) {
            performHapticFeedback();
        }
    }

    private void buildAnimationController() {
        if (canCreateNewOrUpdateExistingLauncherTransitionController()) {
            initTransitionEndpoints(this.mActivity.getDeviceProfile());
            this.mAnimationFactory.createActivityInterface(this.mTransitionDragLength);
        }
    }

    private boolean canCreateNewOrUpdateExistingLauncherTransitionController() {
        return (this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME || this.mHasLauncherTransitionControllerStarted) ? false : true;
    }

    @Override // android.view.View.OnApplyWindowInsetsListener
    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        this.mTaskViewSimulator.setRecentsConfiguration(this.mActivity.getResources().getConfiguration());
        WindowInsets windowInsetsOnApplyWindowInsets = view.onApplyWindowInsets(windowInsets);
        buildAnimationController();
        return windowInsetsOnApplyWindowInsets;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimatorPlaybackControllerCreated(AnimatorPlaybackController anim) {
        this.mLauncherTransitionController = anim;
        anim.dispatchSetInterpolator(new TimeInterpolator() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$JUp3NUzSHJjAHwEF6GbGfzI3py8
            @Override // android.animation.TimeInterpolator
            public final float getInterpolation(float f) {
                return this.f$0.lambda$onAnimatorPlaybackControllerCreated$5$BaseSwipeUpHandlerV2(f);
            }
        });
        this.mLauncherTransitionController.dispatchOnStart();
        updateLauncherTransitionProgress();
    }

    public /* synthetic */ float lambda$onAnimatorPlaybackControllerCreated$5$BaseSwipeUpHandlerV2(float f) {
        return f * this.mDragLengthFactor;
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    public Intent getLaunchIntent() {
        return this.mGestureState.getOverviewIntent();
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler, com.android.quickstep.SwipeUpAnimationLogic
    public void updateFinalShift() {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && this.mRecentsAnimationTargets != null) {
            LiveTileOverlay.INSTANCE.update(this.mTaskViewSimulator.getCurrentCropRect(), this.mTaskViewSimulator.getCurrentCornerRadius());
        }
        boolean z = this.mCurrentShift.value >= 0.7f;
        if (z != this.mPassedOverviewThreshold) {
            this.mPassedOverviewThreshold = z;
            if (!this.mDeviceState.isFullyGesturalNavMode()) {
                performHapticFeedback();
            }
        }
        updateSysUiFlags(this.mCurrentShift.value);
        applyWindowTransform();
        updateLauncherTransitionProgress();
    }

    private void updateLauncherTransitionProgress() {
        if (this.mLauncherTransitionController == null || !canCreateNewOrUpdateExistingLauncherTransitionController()) {
            return;
        }
        this.mLauncherTransitionController.setPlayFraction(this.mCurrentShift.value / this.mDragLengthFactor);
    }

    private void updateSysUiFlags(float windowProgress) {
        if (this.mRecentsAnimationController == null || this.mRecentsView == null) {
            return;
        }
        TaskView runningTaskView = this.mRecentsView.getRunningTaskView();
        TaskView taskViewNearestToCenterOfScreen = this.mRecentsView.getTaskViewNearestToCenterOfScreen();
        int sysUiStatusNavFlags = taskViewNearestToCenterOfScreen == null ? 0 : taskViewNearestToCenterOfScreen.getThumbnail().getSysUiStatusNavFlags();
        boolean z = true;
        boolean z2 = windowProgress > 0.14999998f;
        boolean z3 = taskViewNearestToCenterOfScreen != runningTaskView;
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (!z2 && (!z3 || sysUiStatusNavFlags == 0)) {
            z = false;
        }
        recentsAnimationController.setUseLauncherSystemBarFlags(z);
        this.mRecentsAnimationController.setSplitScreenMinimized(z2);
        this.mActivity.getSystemUiController().updateUiState(4, z2 ? 0 : sysUiStatusNavFlags);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler, com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationStart(RecentsAnimationController controller, RecentsAnimationTargets targets) {
        ActiveGestureLog.INSTANCE.addLog("startRecentsAnimationCallback", targets.apps.length);
        super.onRecentsAnimationStart(controller, targets);
        MultiStateCallback multiStateCallback = this.mStateCallback;
        int i = STATE_APP_CONTROLLER_RECEIVED;
        int i2 = STATE_GESTURE_STARTED | i;
        final RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        Objects.requireNonNull(recentsAnimationController);
        multiStateCallback.runOnceAtState(i2, new Runnable() { // from class: com.android.quickstep.-$$Lambda$v5oDBD-8NFK_ndtfGYo0snfO6Ew
            @Override // java.lang.Runnable
            public final void run() {
                recentsAnimationController.enableInputConsumer();
            }
        });
        this.mStateCallback.setStateOnUiThread(i);
        this.mPassedOverviewThreshold = false;
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler, com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> thumbnailDatas) {
        ActiveGestureLog.INSTANCE.addLog("cancelRecentsAnimation");
        this.mActivityInitListener.unregister();
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_CANCELLED | STATE_HANDLER_INVALIDATED);
        super.onRecentsAnimationCanceled(thumbnailDatas);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    public void onGestureStarted(boolean isLikelyToStartNewTask) {
        notifyGestureStartedAsync();
        setIsLikelyToStartNewTask(isLikelyToStartNewTask, false);
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_STARTED);
        this.mGestureStarted = true;
    }

    private void notifyGestureStartedAsync() {
        if (this.mActivity != null) {
            this.mActivity.clearForceInvisibleFlag(9);
        }
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    public void onGestureCancelled() {
        updateDisplacement(0.0f);
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_COMPLETED);
        this.mLogAction = 6;
        handleNormalGestureEnd(0.0f, false, new PointF(), true);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    public void onGestureEnded(float endVelocity, PointF velocity, PointF downPos) {
        boolean z = this.mGestureStarted && Math.abs(endVelocity) > this.mContext.getResources().getDimension(R.dimen.quickstep_fling_threshold_velocity);
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_COMPLETED);
        this.mLogAction = z ? 4 : 3;
        if (Math.abs(velocity.y) > Math.abs(velocity.x)) {
            this.mLogDirection = velocity.y >= 0.0f ? 2 : 1;
        } else {
            this.mLogDirection = velocity.x < 0.0f ? 3 : 4;
        }
        this.mDownPos = downPos;
        handleNormalGestureEnd(endVelocity, z, velocity, false);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    protected InputConsumer createNewInputProxyHandler() {
        endRunningWindowAnim(this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME);
        endLauncherTransitionController();
        StatefulActivity createdActivity = this.mActivityInterface.getCreatedActivity();
        if (createdActivity == null) {
            return InputConsumer.NO_OP;
        }
        return new OverviewInputConsumer(this.mGestureState, createdActivity, null, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void endRunningWindowAnim(boolean cancel) {
        SwipeUpAnimationLogic.RunningWindowAnim runningWindowAnim = this.mRunningWindowAnim;
        if (runningWindowAnim != null) {
            if (cancel) {
                runningWindowAnim.cancel();
            } else {
                runningWindowAnim.end();
            }
        }
    }

    /* JADX INFO: renamed from: com.android.quickstep.BaseSwipeUpHandlerV2$7, reason: invalid class name */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget;

        static {
            int[] iArr = new int[GestureState.GestureEndTarget.values().length];
            $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget = iArr;
            try {
                iArr[GestureState.GestureEndTarget.HOME.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget[GestureState.GestureEndTarget.RECENTS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget[GestureState.GestureEndTarget.NEW_TASK.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget[GestureState.GestureEndTarget.LAST_TASK.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSettledOnEndTarget() {
        int i = AnonymousClass7.$SwitchMap$com$android$quickstep$GestureState$GestureEndTarget[this.mGestureState.getEndTarget().ordinal()];
        if (i == 1) {
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_SCALED_CONTROLLER_HOME | STATE_CAPTURE_SCREENSHOT);
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).notifySwipeToHomeFinished();
        } else if (i == 2) {
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_SCALED_CONTROLLER_RECENTS | STATE_CAPTURE_SCREENSHOT | STATE_SCREENSHOT_VIEW_SHOWN);
        } else if (i == 3) {
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_START_NEW_TASK | STATE_CAPTURE_SCREENSHOT);
        } else if (i == 4) {
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RESUME_LAST_TASK);
        }
        ActiveGestureLog.INSTANCE.addLog("onSettledOnEndTarget " + this.mGestureState.getEndTarget());
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    protected boolean handleTaskAppeared(RemoteAnimationTargetCompat[] appearedTaskTarget) {
        if (this.mStateCallback.hasStates(STATE_HANDLER_INVALIDATED)) {
            return false;
        }
        boolean zAnyMatch = Arrays.stream(appearedTaskTarget).anyMatch(new Predicate() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$3BUJVWqJq7rKgMyzpx01yvrWkVk
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f$0.lambda$handleTaskAppeared$6$BaseSwipeUpHandlerV2((RemoteAnimationTargetCompat) obj);
            }
        });
        if (!this.mStateCallback.hasStates(STATE_START_NEW_TASK) || !zAnyMatch) {
            return false;
        }
        reset();
        return true;
    }

    public /* synthetic */ boolean lambda$handleTaskAppeared$6$BaseSwipeUpHandlerV2(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        return remoteAnimationTargetCompat.taskId == this.mGestureState.getLastStartedTaskId();
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x001f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private com.android.quickstep.GestureState.GestureEndTarget calculateEndTarget(android.graphics.PointF r6, float r7, boolean r8, boolean r9) {
        /*
            r5 = this;
            Q extends com.android.quickstep.views.RecentsView r0 = r5.mRecentsView
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L1f
            boolean r0 = r5.hasTargets()
            if (r0 != 0) goto Le
        Lc:
            r0 = r1
            goto L20
        Le:
            Q extends com.android.quickstep.views.RecentsView r0 = r5.mRecentsView
            int r0 = r0.getRunningTaskIndex()
            Q extends com.android.quickstep.views.RecentsView r3 = r5.mRecentsView
            int r3 = r3.getNextPage()
            if (r0 < 0) goto L1f
            if (r3 == r0) goto L1f
            goto Lc
        L1f:
            r0 = r2
        L20:
            com.android.quickstep.AnimatedFloat r3 = r5.mCurrentShift
            float r3 = r3.value
            r4 = 1060320051(0x3f333333, float:0.7)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L2d
            r3 = r1
            goto L2e
        L2d:
            r3 = r2
        L2e:
            if (r8 != 0) goto L67
            if (r9 == 0) goto L36
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
            goto Lb4
        L36:
            com.android.quickstep.RecentsAnimationDeviceState r6 = r5.mDeviceState
            boolean r6 = r6.isFullyGesturalNavMode()
            if (r6 == 0) goto L56
            boolean r6 = r5.mIsShelfPeeking
            if (r6 == 0) goto L46
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS
            goto Lb4
        L46:
            if (r0 == 0) goto L4c
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto Lb4
        L4c:
            if (r3 != 0) goto L52
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
            goto Lb4
        L52:
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.HOME
            goto Lb4
        L56:
            if (r3 == 0) goto L5f
            boolean r6 = r5.mGestureStarted
            if (r6 == 0) goto L5f
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS
            goto Lb4
        L5f:
            if (r0 == 0) goto L64
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto Lb4
        L64:
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
            goto Lb4
        L67:
            r8 = 0
            int r8 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r8 >= 0) goto L6e
            r8 = r1
            goto L6f
        L6e:
            r8 = r2
        L6f:
            if (r0 == 0) goto L80
            float r6 = r6.x
            float r6 = java.lang.Math.abs(r6)
            float r7 = java.lang.Math.abs(r7)
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 <= 0) goto L80
            goto L81
        L80:
            r1 = r2
        L81:
            com.android.quickstep.RecentsAnimationDeviceState r6 = r5.mDeviceState
            boolean r6 = r6.isFullyGesturalNavMode()
            if (r6 == 0) goto L90
            if (r8 == 0) goto L90
            if (r1 != 0) goto L90
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.HOME
            goto Lb4
        L90:
            com.android.quickstep.RecentsAnimationDeviceState r6 = r5.mDeviceState
            boolean r6 = r6.isFullyGesturalNavMode()
            if (r6 == 0) goto La1
            if (r8 == 0) goto La1
            boolean r6 = r5.mIsShelfPeeking
            if (r6 != 0) goto La1
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto Lb4
        La1:
            if (r8 == 0) goto Lad
            if (r3 != 0) goto Laa
            if (r1 == 0) goto Laa
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto Lb4
        Laa:
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS
            goto Lb4
        Lad:
            if (r0 == 0) goto Lb2
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto Lb4
        Lb2:
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
        Lb4:
            com.android.quickstep.RecentsAnimationDeviceState r7 = r5.mDeviceState
            com.android.quickstep.GestureState r8 = r5.mGestureState
            int r8 = r8.getDisplayId()
            boolean r7 = r7.isOverviewDisabled(r8)
            if (r7 == 0) goto Lcc
            com.android.quickstep.GestureState$GestureEndTarget r7 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS
            if (r6 == r7) goto Lca
            com.android.quickstep.GestureState$GestureEndTarget r7 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
            if (r6 != r7) goto Lcc
        Lca:
            com.android.quickstep.GestureState$GestureEndTarget r6 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
        Lcc:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.BaseSwipeUpHandlerV2.calculateEndTarget(android.graphics.PointF, float, boolean, boolean):com.android.quickstep.GestureState$GestureEndTarget");
    }

    private void handleNormalGestureEnd(float endVelocity, boolean isFling, PointF velocity, boolean isCancel) {
        float fBoundToRange;
        long j;
        Interpolator interpolator;
        long jMin;
        float f;
        long jMax;
        PointF pointF = new PointF(velocity.x / 1000.0f, velocity.y / 1000.0f);
        float f2 = this.mCurrentShift.value;
        GestureState.GestureEndTarget gestureEndTargetCalculateEndTarget = calculateEndTarget(velocity, endVelocity, isFling, isCancel);
        if (gestureEndTargetCalculateEndTarget == GestureState.GestureEndTarget.NEW_TASK) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).exitSplitScreen(this.mGestureState.getRunningTaskId());
        }
        float f3 = gestureEndTargetCalculateEndTarget.isLauncher ? 1.0f : 0.0f;
        Interpolator interpolator2 = Interpolators.DEACCEL;
        if (!isFling) {
            long jMin2 = Math.min(350L, Math.abs(Math.round((f3 - f2) * 350.0f * SWIPE_DURATION_MULTIPLIER)));
            interpolator = gestureEndTargetCalculateEndTarget == GestureState.GestureEndTarget.RECENTS ? Interpolators.OVERSHOOT_1_2 : Interpolators.DEACCEL;
            f = f3;
            j = 350;
            jMin = jMin2;
            fBoundToRange = f2;
        } else {
            fBoundToRange = Utilities.boundToRange(f2 - ((pointF.y * DisplayController.getSingleFrameMs(this.mContext)) / this.mTransitionDragLength), 0.0f, this.mDragLengthFactor);
            if (Math.abs(endVelocity) <= this.mContext.getResources().getDimension(R.dimen.quickstep_fling_min_velocity) || this.mTransitionDragLength <= 0) {
                j = 350;
                interpolator = interpolator2;
                jMin = 350;
            } else if (gestureEndTargetCalculateEndTarget == GestureState.GestureEndTarget.RECENTS && !this.mDeviceState.isFullyGesturalNavMode()) {
                j = 350;
                Interpolators.OvershootParams overshootParams = new Interpolators.OvershootParams(fBoundToRange, f3, f3, endVelocity / 1000.0f, this.mTransitionDragLength, this.mContext);
                float f4 = overshootParams.end;
                Interpolator interpolator3 = overshootParams.interpolator;
                jMin = Utilities.boundToRange(overshootParams.duration, 120L, 350L);
                f = f4;
                interpolator = interpolator3;
            } else {
                j = 350;
                jMin = Math.min(350L, ((long) Math.round(Math.abs(((f3 - f2) * this.mTransitionDragLength) / pointF.y))) * 2);
                interpolator = gestureEndTargetCalculateEndTarget == GestureState.GestureEndTarget.RECENTS ? Interpolators.OVERSHOOT_1_2 : interpolator2;
            }
            f = f3;
        }
        if (gestureEndTargetCalculateEndTarget.isLauncher && this.mRecentsAnimationController != null) {
            this.mRecentsAnimationController.enableInputProxy(this.mInputConsumer, new Supplier() { // from class: com.android.quickstep.-$$Lambda$i2p1z_Oza987qZjr6drAKrVxw-s
                @Override // java.util.function.Supplier
                public final Object get() {
                    return this.f$0.createNewInputProxyHandler();
                }
            });
        }
        if (gestureEndTargetCalculateEndTarget == GestureState.GestureEndTarget.HOME) {
            setShelfState(ShelfPeekAnim.ShelfAnimState.CANCEL, Interpolators.LINEAR, 0L);
            WindowUtils.minimizeAllFreeforms();
            jMax = Math.max(120L, jMin);
        } else {
            if (gestureEndTargetCalculateEndTarget == GestureState.GestureEndTarget.RECENTS) {
                LiveTileOverlay.INSTANCE.startIconAnimation();
                if (this.mRecentsView != null) {
                    int pageNearestToCenterOfScreen = this.mRecentsView.getPageNearestToCenterOfScreen();
                    if (this.mRecentsView.getNextPage() != pageNearestToCenterOfScreen) {
                        this.mRecentsView.snapToPage(pageNearestToCenterOfScreen, Math.toIntExact(jMin));
                    }
                    if (this.mRecentsView.getScroller().getDuration() > j) {
                        this.mRecentsView.snapToPage(this.mRecentsView.getNextPage(), Workspace.REORDER_TIMEOUT);
                    }
                    jMin = Math.max(jMin, this.mRecentsView.getScroller().getDuration());
                }
                if (this.mDeviceState.isFullyGesturalNavMode()) {
                    setShelfState(ShelfPeekAnim.ShelfAnimState.OVERVIEW, interpolator, jMin);
                }
                nodifiyStartRecents();
            }
            jMax = jMin;
        }
        if (!isRunningOver3PHome() && this.mRecentsView != null) {
            this.mRecentsView.setOnPageTransitionEndCallback(new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$V3-h7TjNfn48MhZZkLAqWLifBfI
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$handleNormalGestureEnd$7$BaseSwipeUpHandlerV2();
                }
            });
        } else {
            this.mGestureState.setState(GestureState.STATE_RECENTS_SCROLLING_FINISHED);
        }
        WindowUtils.sendGestureActionIntent(this.mContext, gestureEndTargetCalculateEndTarget.toString(), this.mDisplayId);
        animateToProgress(fBoundToRange, f, jMax, interpolator, gestureEndTargetCalculateEndTarget, pointF);
    }

    public /* synthetic */ void lambda$handleNormalGestureEnd$7$BaseSwipeUpHandlerV2() {
        this.mGestureState.setState(GestureState.STATE_RECENTS_SCROLLING_FINISHED);
    }

    private void doLogGesture(GestureState.GestureEndTarget endTarget) {
        StatsLogManager.LauncherEvent launcherEvent;
        if (this.mDp == null || this.mDownPos == null) {
            return;
        }
        UserEventDispatcher.newInstance(this.mContext).logStateChangeAction(this.mLogAction, this.mLogDirection, (int) this.mDownPos.x, (int) this.mDownPos.y, 11, 13, endTarget.containerType, endTarget == GestureState.GestureEndTarget.LAST_TASK ? -1 : this.mRecentsView.getNextPage());
        int i = AnonymousClass7.$SwitchMap$com$android$quickstep$GestureState$GestureEndTarget[endTarget.ordinal()];
        if (i == 1) {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_HOME_GESTURE;
        } else if (i == 2) {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_OVERVIEW_GESTURE;
        } else if (i == 3 || i == 4) {
            if (this.mLogDirection == 3) {
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_QUICKSWITCH_LEFT;
            } else {
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_QUICKSWITCH_RIGHT;
            }
        } else {
            launcherEvent = StatsLogManager.LauncherEvent.IGNORE;
        }
        StatsLogManager.newInstance(this.mContext).logger().withSrcState(1).withDstState(StatsLogManager.containerTypeToAtomState(endTarget.containerType)).log(launcherEvent);
    }

    private void animateToProgress(final float start, final float end, final long duration, final Interpolator interpolator, final GestureState.GestureEndTarget target, final PointF velocityPxPerMs) {
        runOnRecentsAnimationStart(new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$2WXF-s19WoNbD8m6P2hOs60cS3s
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$animateToProgress$8$BaseSwipeUpHandlerV2(start, end, duration, interpolator, target, velocityPxPerMs);
            }
        });
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$animateToProgress$8$BaseSwipeUpHandlerV2(FFJLandroid/view/animation/Interpolator;Lcom/android/quickstep/GestureState$GestureEndTarget;Landroid/graphics/PointF;)V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: animateToProgressInternal, reason: merged with bridge method [inline-methods] */
    public void lambda$animateToProgress$8$BaseSwipeUpHandlerV2(float start, final float end, long duration, Interpolator interpolator, final GestureState.GestureEndTarget target, PointF velocityPxPerMs) {
        this.mGestureState.setEndTarget(target, false);
        maybeUpdateRecentsAttachedState();
        if (this.mGestureState.getEndTarget().isLauncher) {
            ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mActivityRestartListener);
        }
        if (this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME) {
            final boolean zIsRunningOver3PHome = isRunningOver3PHome();
            final boolean z = this instanceof FallbackSwipeHandler;
            SwipeUpAnimationLogic.HomeAnimationFactory homeAnimationFactoryCreateHomeAnimationFactory = createHomeAnimationFactory(duration);
            final RectFSpringAnim rectFSpringAnimCreateWindowAnimationToHome = createWindowAnimationToHome(start, homeAnimationFactoryCreateHomeAnimationFactory);
            if (z) {
                rectFSpringAnimCreateWindowAnimationToHome.setFinishProgress(this.mAlphaOnEnd);
            }
            rectFSpringAnimCreateWindowAnimationToHome.addAnimatorListener(new AnimationSuccessListener() { // from class: com.android.quickstep.BaseSwipeUpHandlerV2.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (!zIsRunningOver3PHome || rectFSpringAnimCreateWindowAnimationToHome == null) {
                        return;
                    }
                    LGLog.d(BaseSwipeUpHandlerV2.TAG, "[RecentsAnimation] onAnimationStart: call end");
                    rectFSpringAnimCreateWindowAnimationToHome.end();
                }

                @Override // com.android.launcher3.anim.AnimationSuccessListener
                public void onAnimationSuccess(Animator animator) {
                    if (BaseSwipeUpHandlerV2.this.mRecentsAnimationController == null) {
                        return;
                    }
                    if (z && BaseSwipeUpHandlerV2.this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME) {
                        BaseSwipeUpHandlerV2.this.mGestureState.setState(GestureState.STATE_RECENTS_SCROLLING_FINISHED);
                    }
                    BaseSwipeUpHandlerV2.this.mGestureState.setState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED);
                }
            });
            if (LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION.getValue()) {
                rectFSpringAnimCreateWindowAnimationToHome.addAnimatorListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.BaseSwipeUpHandlerV2.4
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                        QuickstepTransitionManager appTransitionManager;
                        LGLog.d(BaseSwipeUpHandlerV2.TAG, "[WindowAnim] windowAnim - onAnimationStart");
                        if (BaseSwipeUpHandlerV2.this.mActivity == null || !(BaseSwipeUpHandlerV2.this.mActivity instanceof Launcher) || (appTransitionManager = ((BaseQuickstepLauncher) ((Launcher) BaseSwipeUpHandlerV2.this.mActivity)).getAppTransitionManager()) == null) {
                            return;
                        }
                        BaseSwipeUpHandlerV2.this.mHomeAnimator = new AnimatorSet();
                        final Pair<AnimatorSet, Runnable> launcherContentAnimator = appTransitionManager.getLauncherContentAnimator(false, null);
                        BaseSwipeUpHandlerV2.this.mHomeAnimator.play((Animator) launcherContentAnimator.first);
                        if (LGHomeFeature.Config.FEATURE_USE_LGBLUR_2_WITH_LAUNCH_ANIM.getValue()) {
                            BaseSwipeUpHandlerV2.this.mHomeAnimator.play(appTransitionManager.getTransitionBlurAnimator(false));
                        }
                        BaseSwipeUpHandlerV2.this.mHomeAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.BaseSwipeUpHandlerV2.4.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation2) {
                                LGLog.d(BaseSwipeUpHandlerV2.TAG, "[WindowAnim] home layer - onAnimationEnd");
                                ((Runnable) launcherContentAnimator.second).run();
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationCancel(Animator animation2) {
                                LGLog.d(BaseSwipeUpHandlerV2.TAG, "[WindowAnim] home layer - onAnimationEnd");
                            }
                        });
                        BaseSwipeUpHandlerV2.this.mHomeAnimator.start();
                    }
                });
            }
            getOrientationHandler().adjustFloatingIconStartVelocity(velocityPxPerMs);
            if (homeAnimationFactoryCreateHomeAnimationFactory.needToForceEnd()) {
                rectFSpringAnimCreateWindowAnimationToHome.setFinishProgress(1.0f);
                rectFSpringAnimCreateWindowAnimationToHome.forceEndWithAllCallback();
            } else {
                rectFSpringAnimCreateWindowAnimationToHome.start(this.mContext, velocityPxPerMs);
            }
            homeAnimationFactoryCreateHomeAnimationFactory.playAtomicAnimation(velocityPxPerMs.y);
            this.mRunningWindowAnim = SwipeUpAnimationLogic.RunningWindowAnim.wrap(rectFSpringAnimCreateWindowAnimationToHome);
            this.mLauncherTransitionController = null;
        } else {
            ObjectAnimator objectAnimatorAnimateToValue = this.mCurrentShift.animateToValue(start, end);
            objectAnimatorAnimateToValue.setDuration(duration).setInterpolator(interpolator);
            objectAnimatorAnimateToValue.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$F0BKDPOrxVsRpbJnsOrhkUgd7QM
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$animateToProgressInternal$9$BaseSwipeUpHandlerV2(valueAnimator);
                }
            });
            objectAnimatorAnimateToValue.addListener(new AnimationSuccessListener() { // from class: com.android.quickstep.BaseSwipeUpHandlerV2.5
                @Override // com.android.launcher3.anim.AnimationSuccessListener
                public void onAnimationSuccess(Animator animator) {
                    if (BaseSwipeUpHandlerV2.this.mRecentsAnimationController == null) {
                        return;
                    }
                    if (BaseSwipeUpHandlerV2.this.mRecentsView != null) {
                        if (BaseSwipeUpHandlerV2.this.mActivity != null && (BaseSwipeUpHandlerV2.this.mActivity instanceof RecentsActivity)) {
                            BaseSwipeUpHandlerV2.this.mActivity.getRootView().setBackgroundColor(BaseSwipeUpHandlerV2.this.mActivity.getColor(R.color.wallpaper_blur_dim_color));
                        }
                        int nextPage = BaseSwipeUpHandlerV2.this.mRecentsView.getNextPage();
                        int lastAppearedTaskIndex = BaseSwipeUpHandlerV2.this.getLastAppearedTaskIndex();
                        boolean zHasStartedNewTask = BaseSwipeUpHandlerV2.this.hasStartedNewTask();
                        if (target == GestureState.GestureEndTarget.NEW_TASK && nextPage == lastAppearedTaskIndex && !zHasStartedNewTask) {
                            BaseSwipeUpHandlerV2.this.mGestureState.setEndTarget(GestureState.GestureEndTarget.LAST_TASK);
                        } else if (target == GestureState.GestureEndTarget.LAST_TASK && zHasStartedNewTask) {
                            BaseSwipeUpHandlerV2.this.mGestureState.setEndTarget(GestureState.GestureEndTarget.NEW_TASK);
                        }
                    }
                    BaseSwipeUpHandlerV2.this.mGestureState.setState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED);
                }
            });
            objectAnimatorAnimateToValue.start();
            this.mRunningWindowAnim = SwipeUpAnimationLogic.RunningWindowAnim.wrap(objectAnimatorAnimateToValue);
        }
        if (this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME) {
            start = 0.0f;
        }
        Interpolator interpolatorMapToProgress = Interpolators.mapToProgress(interpolator, start, end);
        AnimatorPlaybackController animatorPlaybackController = this.mLauncherTransitionController;
        if (animatorPlaybackController == null) {
            return;
        }
        if (start == end || duration <= 0) {
            animatorPlaybackController.dispatchSetInterpolator(new TimeInterpolator() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$7AQPR_K1O-qW2SLFQrnkrwAGTsk
                @Override // android.animation.TimeInterpolator
                public final float getInterpolation(float f) {
                    return BaseSwipeUpHandlerV2.lambda$animateToProgressInternal$10(end, f);
                }
            });
        } else {
            animatorPlaybackController.dispatchSetInterpolator(interpolatorMapToProgress);
        }
        this.mLauncherTransitionController.getAnimationPlayer().setDuration(Math.max(0L, duration));
        if (FeatureFlags.UNSTABLE_SPRINGS.get()) {
            this.mLauncherTransitionController.dispatchOnStart();
        }
        this.mLauncherTransitionController.getAnimationPlayer().start();
        this.mHasLauncherTransitionControllerStarted = true;
    }

    public /* synthetic */ void lambda$animateToProgressInternal$9$BaseSwipeUpHandlerV2(ValueAnimator valueAnimator) {
        computeRecentsScrollIfInvisible();
    }

    private void computeRecentsScrollIfInvisible() {
        if (this.mRecentsView == null || this.mRecentsView.getVisibility() == 0) {
            return;
        }
        this.mRecentsView.computeScroll();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void continueComputingRecentsScrollIfNecessary() {
        if (this.mGestureState.hasState(GestureState.STATE_RECENTS_SCROLLING_FINISHED) || this.mStateCallback.hasStates(STATE_HANDLER_INVALIDATED) || this.mCanceled) {
            return;
        }
        computeRecentsScrollIfInvisible();
        this.mRecentsView.postOnAnimation(new $$Lambda$BaseSwipeUpHandlerV2$mzlHvDYwct0eTiHIKotSqfhDDls(this));
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler, com.android.quickstep.SwipeUpAnimationLogic
    protected RectFSpringAnim createWindowAnimationToHome(float startProgress, SwipeUpAnimationLogic.HomeAnimationFactory homeAnimationFactory) {
        RectFSpringAnim rectFSpringAnimCreateWindowAnimationToHome = super.createWindowAnimationToHome(startProgress, homeAnimationFactory);
        rectFSpringAnimCreateWindowAnimationToHome.addOnUpdateListener(new RectFSpringAnim.OnUpdateListener() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$Tuou2GWGxs33OukqAoz0WI644hQ
            @Override // com.android.quickstep.util.RectFSpringAnim.OnUpdateListener
            public final void onUpdate(RectF rectF, float f) {
                this.f$0.lambda$createWindowAnimationToHome$11$BaseSwipeUpHandlerV2(rectF, f);
            }
        });
        rectFSpringAnimCreateWindowAnimationToHome.addAnimatorListener(new AnimationSuccessListener() { // from class: com.android.quickstep.BaseSwipeUpHandlerV2.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                if (BaseSwipeUpHandlerV2.this.mActivity != null) {
                    BaseSwipeUpHandlerV2.this.removeLiveTileOverlay();
                }
                if (BaseSwipeUpHandlerV2.this.mActivity != null) {
                    VibratorWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(BaseSwipeUpHandlerV2.this.mActivity).vibrate(VibratorWrapper.OVERVIEW_HAPTIC, BaseSwipeUpHandlerV2.this.mActivity.getRootView());
                } else if (BaseSwipeUpHandlerV2.this.mRecentsView != null) {
                    VibratorWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(BaseSwipeUpHandlerV2.this.mRecentsView.getContext()).vibrate(VibratorWrapper.OVERVIEW_HAPTIC, BaseSwipeUpHandlerV2.this.mRecentsView);
                }
            }

            @Override // com.android.launcher3.anim.AnimationSuccessListener
            public void onAnimationSuccess(Animator animator) {
                if (BaseSwipeUpHandlerV2.this.mRecentsView != null) {
                    Q q = BaseSwipeUpHandlerV2.this.mRecentsView;
                    final Q q2 = BaseSwipeUpHandlerV2.this.mRecentsView;
                    Objects.requireNonNull(q2);
                    q.post(new Runnable() { // from class: com.android.quickstep.-$$Lambda$4Q670e3qGa8Ve9KzoUn5m4P6nkw
                        @Override // java.lang.Runnable
                        public final void run() {
                            q2.resetTaskVisuals();
                        }
                    });
                }
                BaseSwipeUpHandlerV2.this.maybeUpdateRecentsAttachedState(false);
                BaseSwipeUpHandlerV2.this.mActivityInterface.onSwipeUpToHomeComplete(BaseSwipeUpHandlerV2.this.mDeviceState);
            }
        });
        return rectFSpringAnimCreateWindowAnimationToHome;
    }

    public /* synthetic */ void lambda$createWindowAnimationToHome$11$BaseSwipeUpHandlerV2(RectF rectF, float f) {
        updateSysUiFlags(Math.max(f, this.mCurrentShift.value));
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    public void onConsumerAboutToBeSwitched() {
        if (this.mActivity != null) {
            this.mActivity.clearRunOnceOnStartCallback();
            resetLauncherListenersAndOverlays();
        }
        if (this.mGestureState.getEndTarget() != null && !this.mGestureState.isRunningAnimationToLauncher()) {
            cancelCurrentAnimation();
        } else {
            reset();
        }
    }

    public boolean isCanceled() {
        return this.mCanceled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resumeLastTask() {
        this.mRecentsAnimationController.finish(false, null);
        ActiveGestureLog.INSTANCE.addLog("finishRecentsAnimation", false);
        doLogGesture(GestureState.GestureEndTarget.LAST_TASK);
        reset();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNewTask() {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            this.mRecentsAnimationController.finish(true, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$v62QKtrk7g0Om45UshUxt3HkYtw
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.startNewTaskInternal();
                }
            });
        } else {
            startNewTaskInternal();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNewTaskInternal() {
        startNewTask(new Consumer() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$jYrlfFUImh3uPSdao93KDOUEczA
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$startNewTaskInternal$12$BaseSwipeUpHandlerV2((Boolean) obj);
            }
        });
    }

    public /* synthetic */ void lambda$startNewTaskInternal$12$BaseSwipeUpHandlerV2(Boolean bool) {
        if (!bool.booleanValue()) {
            reset();
            endLauncherTransitionController();
            updateSysUiFlags(1.0f);
        }
        doLogGesture(GestureState.GestureEndTarget.NEW_TASK);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    protected void onRestartPreviouslyAppearedTask() {
        super.onRestartPreviouslyAppearedTask();
        reset();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reset() {
        this.mStateCallback.setStateOnUiThread(STATE_HANDLER_INVALIDATED);
    }

    private void cancelCurrentAnimation() {
        this.mCanceled = true;
        this.mCurrentShift.cancelAnimation();
        AnimatorPlaybackController animatorPlaybackController = this.mLauncherTransitionController;
        if (animatorPlaybackController != null && animatorPlaybackController.getAnimationPlayer().isStarted()) {
            this.mLauncherTransitionController.getAnimationPlayer().cancel();
        }
        AnimatorSet animatorSet = this.mHomeAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.mHomeAnimator = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateHandler() {
        endRunningWindowAnim(false);
        if (this.mGestureEndCallback != null) {
            this.mGestureEndCallback.run();
        }
        this.mActivityInitListener.unregister();
        ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mActivityRestartListener);
        this.mTaskSnapshot = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateHandlerWithLauncher() {
        endLauncherTransitionController();
        this.mRecentsView.onGestureAnimationEnd();
        resetLauncherListenersAndOverlays();
    }

    private void endLauncherTransitionController() {
        setShelfState(ShelfPeekAnim.ShelfAnimState.CANCEL, Interpolators.LINEAR, 0L);
        AnimatorPlaybackController animatorPlaybackController = this.mLauncherTransitionController;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.getAnimationPlayer().end();
            this.mLauncherTransitionController = null;
        }
    }

    private void resetLauncherListenersAndOverlays() {
        if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            this.mActivityInterface.setOnDeferredActivityLaunchCallback(null);
        }
        this.mActivity.getRootView().setOnApplyWindowInsetsListener(null);
        removeLiveTileOverlay();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyTransitionCancelled() {
        this.mAnimationFactory.onTransitionCancelled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetStateForAnimationCancel() {
        this.mActivityInterface.onTransitionCancelled(this.mWasLauncherAlreadyVisible || this.mGestureStarted);
        this.mActivity.clearForceInvisibleFlag(1);
    }

    protected void switchToScreenshot() {
        int runningTaskId = this.mGestureState.getRunningTaskId();
        boolean zPostFrameDrawn = false;
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            if (this.mRecentsAnimationController != null) {
                this.mRecentsAnimationController.getController().setWillFinishToHome(true);
                if (this.mTaskSnapshot == null) {
                    this.mTaskSnapshot = this.mRecentsAnimationController.screenshotTask(runningTaskId);
                }
                this.mRecentsView.updateThumbnail(runningTaskId, this.mTaskSnapshot, false);
            }
            this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
            return;
        }
        if (!hasTargets()) {
            this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
            return;
        }
        if (this.mRecentsAnimationController != null) {
            if (this.mTaskSnapshot == null) {
                this.mTaskSnapshot = this.mRecentsAnimationController.screenshotTask(runningTaskId);
            }
            TaskView taskViewUpdateThumbnail = this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME ? null : this.mRecentsView.updateThumbnail(runningTaskId, this.mTaskSnapshot);
            if (taskViewUpdateThumbnail != null && !this.mCanceled) {
                zPostFrameDrawn = ViewUtils.postFrameDrawn(taskViewUpdateThumbnail, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$lLL4pmmGoly0WntrZ7h3ZXe9SIc
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$switchToScreenshot$13$BaseSwipeUpHandlerV2();
                    }
                }, new BooleanSupplier() { // from class: com.android.quickstep.-$$Lambda$fP431_rRw6yUKVcjrndFW17hsVU
                    @Override // java.util.function.BooleanSupplier
                    public final boolean getAsBoolean() {
                        return this.f$0.isCanceled();
                    }
                });
            }
        }
        if (zPostFrameDrawn) {
            return;
        }
        Object objBeginSection = TraceHelper.INSTANCE.beginSection(SCREENSHOT_CAPTURED_EVT, 4);
        this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
        TraceHelper.INSTANCE.endSection(objBeginSection);
    }

    public /* synthetic */ void lambda$switchToScreenshot$13$BaseSwipeUpHandlerV2() {
        this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishCurrentTransitionToRecents() {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || !hasTargets() || this.mRecentsAnimationController == null) {
            this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
        } else {
            this.mRecentsAnimationController.finish(true, new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$3Q-C1w7WsBn0OkR-UqLCpqLBhLg
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$finishCurrentTransitionToRecents$14$BaseSwipeUpHandlerV2();
                }
            });
        }
        ActiveGestureLog.INSTANCE.addLog("finishRecentsAnimation", true);
    }

    public /* synthetic */ void lambda$finishCurrentTransitionToRecents$14$BaseSwipeUpHandlerV2() {
        this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishCurrentTransitionToHome() {
        if (!hasTargets() || this.mRecentsAnimationController == null) {
            this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
        } else {
            finishRecentsControllerToHome(new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$K8Lt73Gdfwa62LY59r67FX_LEtU
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$finishCurrentTransitionToHome$16$BaseSwipeUpHandlerV2();
                }
            });
        }
        ActiveGestureLog.INSTANCE.addLog("finishRecentsAnimation", true);
        doLogGesture(GestureState.GestureEndTarget.HOME);
    }

    private /* synthetic */ void lambda$finishCurrentTransitionToHome$15() {
        this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
    }

    public /* synthetic */ void lambda$finishCurrentTransitionToHome$16$BaseSwipeUpHandlerV2() {
        this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
        if ((this.mActivityInterface instanceof FallbackActivityInterface) && !isRunningOver3PHome() && this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME) {
            ActivityOptions activityOptionsMakeCustomAnimation = ActivityOptions.makeCustomAnimation(this.mContext, 0, 0);
            activityOptionsMakeCustomAnimation.setLaunchDisplayId(this.mGestureState.getDisplayId());
            LGLog.d(TAG, "[RecentsAnimation] createHomeAnimationFactory : " + this.mGestureState.getDisplayId() + ", " + this.mGestureState.getHomeIntent());
            this.mContext.startActivity(new Intent(this.mGestureState.getHomeIntent()), activityOptionsMakeCustomAnimation.toBundle());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setupLauncherUiAfterSwipeUpToRecentsAnimation() {
        endLauncherTransitionController();
        this.mActivityInterface.onSwipeUpToRecentsComplete();
        if (this.mRecentsAnimationController != null) {
            this.mRecentsAnimationController.setDeferCancelUntilNextTransition(true, true);
        }
        this.mRecentsView.onSwipeUpAnimationSuccess();
        SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).onOverviewShown(false, TAG);
        doLogGesture(GestureState.GestureEndTarget.RECENTS);
        reset();
    }

    private void addLiveTileOverlay() {
        if (LiveTileOverlay.INSTANCE.attach(this.mActivity.getRootView().getOverlay())) {
            this.mRecentsView.setLiveTileOverlayAttached(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeLiveTileOverlay() {
        LiveTileOverlay.INSTANCE.detach(this.mActivity.getRootView().getOverlay());
        this.mRecentsView.setLiveTileOverlayAttached(false);
    }

    private static boolean isNotInRecents(RemoteAnimationTargetCompat app) {
        return app.isNotInRecents || app.activityType == 2;
    }

    @Override // com.android.quickstep.BaseSwipeUpHandler
    public void finishToHome() {
        ActivityOptions activityOptionsMakeCustomAnimation = ActivityOptions.makeCustomAnimation(this.mContext, 0, 0);
        activityOptionsMakeCustomAnimation.setLaunchDisplayId(this.mGestureState.getDisplayId());
        LGLog.d(TAG, "[RecentsAnimation] finishToHome() : " + this.mGestureState.getDisplayId() + ", " + this.mGestureState.getHomeIntent());
        this.mContext.startActivity(new Intent(this.mGestureState.getHomeIntent()), activityOptionsMakeCustomAnimation.toBundle());
    }
}
