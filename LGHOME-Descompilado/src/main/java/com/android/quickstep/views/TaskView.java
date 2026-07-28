package com.android.quickstep.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.view.GravityCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.util.ViewPool;
import com.android.quickstep.RecentsModel;
import com.android.quickstep.TaskIconCache;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.TaskThumbnailCache;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.util.RecentsOrientedState;
import com.android.quickstep.util.TaskCornerRadius;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class TaskView extends FrameLayout implements RecentsView.PageCallbacks, ViewPool.Reusable {
    private static final long DIM_ANIM_DURATION = 700;
    public static final int FLAG_NOT_FOCUSABLE_NOT_TOUCHABLE = 24;
    public static final int INDEX_DIGITAL_WELLBEING_TOAST = 0;
    public static final long SCALE_ICON_DURATION = 120;
    private static final String TAG = "TaskView";
    private final BaseDraggingActivity mActivity;
    private float mBoxTranslationY;
    private View mContextualChip;
    private View mContextualChipWrapper;
    private final FullscreenDrawParams mCurrentFullscreenParams;
    private float mCurveScale;
    private final DigitalWellBeingToast mDigitalWellBeingToast;
    private float mDismissScale;
    private float mDismissTranslationX;
    private float mDismissTranslationY;
    public Runnable mEnableFocusableRunnable;
    private float mFocusTransitionProgress;
    private float mFooterAlpha;
    private float mFooterVerticalOffset;
    private final FooterWrapper[] mFooters;
    private float mFullscreenProgress;
    private float mGridEndTranslationX;
    private float mGridProgress;
    private float mGridTranslationX;
    private float mGridTranslationY;
    private TaskHeaderView mHeaderView;
    private ObjectAnimator mIconAndDimAnimator;
    private TaskIconCache.IconLoadRequest mIconLoadRequest;
    private float mIconScaleAnimStartProgress;
    private IconView mIconView;
    private final PointF mLastTouchDownPosition;
    private TaskMenuView mMenuView;
    private float mModalness;
    private float mNonGridScale;
    private float mNonGridTranslationX;
    private float mNonGridTranslationY;
    private final TaskOutlineProvider mOutlineProvider;
    private boolean mShowScreenshot;
    private TaskThumbnailView mSnapshotView;
    private float mSplitSelectScrollOffsetPrimary;
    private float mSplitSelectTranslationX;
    private float mSplitSelectTranslationY;
    private float mStableAlpha;
    private int mStackHeight;
    private Task mTask;
    protected final TaskIdAttributeContainer[] mTaskIdAttributeContainer;
    protected final int[] mTaskIdContainer;
    private final View.OnAttachStateChangeListener mTaskMenuStateListener;
    private float mTaskOffsetTranslationX;
    private float mTaskOffsetTranslationY;
    private float mTaskResistanceTranslationX;
    private float mTaskResistanceTranslationY;
    private int mTaskViewId;
    private TaskThumbnailCache.ThumbnailLoadRequest mThumbnailLoadRequest;
    private Window mWindow;
    private static final TimeInterpolator CURVE_INTERPOLATOR = new TimeInterpolator() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$qryhlLB3zw8-2a0XB3QCg8taq1c
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            return TaskView.lambda$static$0(f);
        }
    };
    public static float MAX_PAGE_SCRIM_ALPHA = 0.4f;
    public static float EDGE_SCALE_DOWN_FACTOR = 0.03f;
    private static final Interpolator GRID_INTERPOLATOR = Interpolators.ACCEL_DEACCEL;
    private static final List<Rect> SYSTEM_GESTURE_EXCLUSION_RECT = Collections.singletonList(new Rect());
    private static final FloatProperty<TaskView> FOCUS_TRANSITION = new FloatProperty<TaskView>("focusTransition") { // from class: com.android.quickstep.views.TaskView.1
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setIconAndDimTransitionProgress(v, false);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mFocusTransitionProgress);
        }
    };
    private static final FloatProperty<TaskView> SPLIT_SELECT_TRANSLATION_X = new FloatProperty<TaskView>("splitSelectTranslationX") { // from class: com.android.quickstep.views.TaskView.2
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setSplitSelectTranslationX(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mSplitSelectTranslationX);
        }
    };
    private static final FloatProperty<TaskView> SPLIT_SELECT_TRANSLATION_Y = new FloatProperty<TaskView>("splitSelectTranslationY") { // from class: com.android.quickstep.views.TaskView.3
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setSplitSelectTranslationY(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mSplitSelectTranslationY);
        }
    };
    private static final FloatProperty<TaskView> DISMISS_TRANSLATION_X = new FloatProperty<TaskView>("dismissTranslationX") { // from class: com.android.quickstep.views.TaskView.4
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setDismissTranslationX(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mDismissTranslationX);
        }
    };
    private static final FloatProperty<TaskView> DISMISS_TRANSLATION_Y = new FloatProperty<TaskView>("dismissTranslationY") { // from class: com.android.quickstep.views.TaskView.5
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setDismissTranslationY(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mDismissTranslationY);
        }
    };
    private static final FloatProperty<TaskView> TASK_OFFSET_TRANSLATION_X = new FloatProperty<TaskView>("taskOffsetTranslationX") { // from class: com.android.quickstep.views.TaskView.6
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setTaskOffsetTranslationX(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mTaskOffsetTranslationX);
        }
    };
    private static final FloatProperty<TaskView> TASK_OFFSET_TRANSLATION_Y = new FloatProperty<TaskView>("taskOffsetTranslationY") { // from class: com.android.quickstep.views.TaskView.7
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setTaskOffsetTranslationY(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mTaskOffsetTranslationY);
        }
    };
    private static final FloatProperty<TaskView> TASK_RESISTANCE_TRANSLATION_X = new FloatProperty<TaskView>("taskResistanceTranslationX") { // from class: com.android.quickstep.views.TaskView.8
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setTaskResistanceTranslationX(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mTaskResistanceTranslationX);
        }
    };
    private static final FloatProperty<TaskView> TASK_RESISTANCE_TRANSLATION_Y = new FloatProperty<TaskView>("taskResistanceTranslationY") { // from class: com.android.quickstep.views.TaskView.9
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setTaskResistanceTranslationY(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mTaskResistanceTranslationY);
        }
    };
    private static final FloatProperty<TaskView> NON_GRID_TRANSLATION_X = new FloatProperty<TaskView>("nonGridTranslationX") { // from class: com.android.quickstep.views.TaskView.10
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setNonGridTranslationX(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mNonGridTranslationX);
        }
    };
    private static final FloatProperty<TaskView> NON_GRID_TRANSLATION_Y = new FloatProperty<TaskView>("nonGridTranslationY") { // from class: com.android.quickstep.views.TaskView.11
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setNonGridTranslationY(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mNonGridTranslationY);
        }
    };
    public static final FloatProperty<TaskView> GRID_END_TRANSLATION_X = new FloatProperty<TaskView>("gridEndTranslationX") { // from class: com.android.quickstep.views.TaskView.12
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setGridEndTranslationX(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mGridEndTranslationX);
        }
    };
    public static final FloatProperty<TaskView> SNAPSHOT_SCALE = new FloatProperty<TaskView>("snapshotScale") { // from class: com.android.quickstep.views.TaskView.13
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskView taskView, float v) {
            taskView.setSnapshotScale(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mSnapshotView.getScaleX());
        }
    };

    public boolean containsMultipleTasks() {
        return false;
    }

    protected int getChildTaskIndexAtPosition(PointF position) {
        return 0;
    }

    public float getSplitRatio() {
        return 0.5f;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0012: ARITH (wrap:float:0x000f: ARITH (wrap:float:0x000c: CAST (float) (wrap:double:0x000b: NEG 
      (wrap:double:0x0007: INVOKE (wrap:double:0x0006: ARITH (wrap:double:0x0000: CAST (double) (r4v0 float)) * (3.141592653589793d double) A[WRAPPED]) STATIC call: java.lang.Math.cos(double):double A[MD:(double):double (c), WRAPPED] (LINE:137))
     A[WRAPPED] (LINE:137))) / (2.0f float) A[WRAPPED] (LINE:137)) + (0.5f float) (LINE:137) */
    static /* synthetic */ float lambda$static$0(float f) {
        return (((float) (-Math.cos(((double) f) * 3.141592653589793d))) / 2.0f) + 0.5f;
    }

    public TaskView(Context context) {
        this(context, null);
    }

    public TaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTaskMenuStateListener = new View.OnAttachStateChangeListener() { // from class: com.android.quickstep.views.TaskView.14
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View view) {
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View view) {
                if (TaskView.this.mMenuView != null) {
                    TaskView.this.mMenuView.removeOnAttachStateChangeListener(this);
                    TaskView.this.mMenuView = null;
                }
            }
        };
        this.mNonGridScale = 1.0f;
        this.mDismissScale = 1.0f;
        this.mIconScaleAnimStartProgress = 0.0f;
        this.mFocusTransitionProgress = 1.0f;
        this.mModalness = 0.0f;
        this.mStableAlpha = 1.0f;
        this.mTaskViewId = -1;
        this.mTaskIdContainer = new int[]{-1, -1};
        this.mTaskIdAttributeContainer = new TaskIdAttributeContainer[2];
        this.mFooters = new FooterWrapper[2];
        this.mFooterVerticalOffset = 0.0f;
        this.mFooterAlpha = 1.0f;
        this.mLastTouchDownPosition = new PointF();
        this.mEnableFocusableRunnable = new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$S9Be8tPdMC5ZKBPEAq838rZhwEU
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$11$TaskView();
            }
        };
        MAX_PAGE_SCRIM_ALPHA = LGHomeFeature.Config.FEATURE_UX_9_21.getValue() ? 0.45f : 0.25f;
        EDGE_SCALE_DOWN_FACTOR = LGHomeFeature.Config.FEATURE_UX_9_21.getValue() ? 0.1f : 0.03f;
        BaseDraggingActivity baseDraggingActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(context);
        this.mActivity = baseDraggingActivity;
        this.mWindow = baseDraggingActivity.getWindow();
        setOnClickListener(new View.OnClickListener() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$_WFmReiFKic34_CcX18yLnnW5hc
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$1$TaskView(view);
            }
        });
        FullscreenDrawParams fullscreenDrawParams = new FullscreenDrawParams(context);
        this.mCurrentFullscreenParams = fullscreenDrawParams;
        this.mDigitalWellBeingToast = new DigitalWellBeingToast(baseDraggingActivity, this);
        TaskOutlineProvider taskOutlineProvider = new TaskOutlineProvider(getContext(), fullscreenDrawParams);
        this.mOutlineProvider = taskOutlineProvider;
        setOutlineProvider(taskOutlineProvider);
    }

    public /* synthetic */ void lambda$new$1$TaskView(View view) {
        if (getTask() == null || getRecentsView().getAnimRunning() || confirmSecondSplitSelectApp()) {
            return;
        }
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && isRunningTask()) {
            createLaunchAnimationForRunningTask().start();
        } else {
            launchTaskAnimated();
        }
        this.mActivity.getUserEventDispatcher().logTaskLaunchOrDismiss(0, 0, getRecentsView().indexOfChild(this), TaskUtils.getLaunchComponentKeyForTask(getTask().key));
        this.mActivity.getStatsLogManager().logger().withItemInfo(getItemInfo()).log(StatsLogManager.LauncherEvent.LAUNCHER_TASK_LAUNCH_TAP);
    }

    public void setTaskViewId(int id) {
        this.mTaskViewId = id;
    }

    public int getTaskViewId() {
        return this.mTaskViewId;
    }

    public WorkspaceItemInfo getItemInfo() {
        ComponentKey launchComponentKeyForTask = TaskUtils.getLaunchComponentKeyForTask(getTask().key);
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo();
        workspaceItemInfo.itemType = 7;
        workspaceItemInfo.container = -109L;
        workspaceItemInfo.user = launchComponentKeyForTask.user;
        workspaceItemInfo.intent = new Intent().setComponent(launchComponentKeyForTask.componentName);
        workspaceItemInfo.title = TaskUtils.getTitle(getContext(), getTask());
        workspaceItemInfo.screenId = getRecentsView().indexOfChild(this);
        return workspaceItemInfo;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSnapshotView = (TaskThumbnailView) findViewById(R.id.snapshot);
        TaskHeaderView taskHeaderView = (TaskHeaderView) findViewById(R.id.headerview);
        this.mHeaderView = taskHeaderView;
        this.mSnapshotView.setHeaderView(taskHeaderView);
        if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
            setElevation(0.0f);
        }
    }

    public void setModalness(float modalness) {
        this.mModalness = modalness;
        View view = this.mContextualChip;
        if (view != null) {
            view.setScaleX(Utilities.comp(modalness));
            this.mContextualChip.setScaleY(Utilities.comp(modalness));
        }
        View view2 = this.mContextualChipWrapper;
        if (view2 != null) {
            view2.setAlpha(Utilities.comp(modalness));
        }
        updateFooterVerticalOffset(this.mFooterVerticalOffset);
    }

    public TaskMenuView getMenuView() {
        return this.mMenuView;
    }

    public DigitalWellBeingToast getDigitalWellBeingToast() {
        return this.mDigitalWellBeingToast;
    }

    public void bind(Task task, RecentsOrientedState orientedState) {
        cancelPendingLoadTasks();
        this.mTask = task;
        this.mTaskIdContainer[0] = task.key.id;
        this.mTaskIdAttributeContainer[0] = new TaskIdAttributeContainer(task, this.mSnapshotView, this.mIconView, this.mHeaderView, -1);
        this.mSnapshotView.bind(task);
        this.mHeaderView.bind(this, this.mTask);
        setOrientationState(orientedState);
    }

    public Task getTask() {
        return this.mTask;
    }

    public int[] getTaskIds() {
        return this.mTaskIdContainer;
    }

    public TaskThumbnailView getThumbnail() {
        return this.mSnapshotView;
    }

    public IconView getIconView() {
        return this.mIconView;
    }

    public Drawable getIconViewDrawable() {
        IconView iconView = this.mIconView;
        if (iconView == null) {
            return this.mHeaderView.getIconView().getDrawable();
        }
        return iconView.getDrawable();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            this.mLastTouchDownPosition.set(ev.getX(), ev.getY());
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean confirmSecondSplitSelectApp() {
        TaskIdAttributeContainer taskIdAttributeContainer = this.mTaskIdAttributeContainer[getChildTaskIndexAtPosition(this.mLastTouchDownPosition)];
        return getRecentsView().confirmSplitSelect(this, taskIdAttributeContainer.getTask(), taskIdAttributeContainer.getIconViewOnHeaderView(), taskIdAttributeContainer.getThumbnailView());
    }

    public AnimatorPlaybackController createLaunchAnimationForRunningTask() {
        final PendingAnimation pendingAnimationCreateTaskLaunchAnimation = getRecentsView().createTaskLaunchAnimation(this, 336L, Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
        AnimatorPlaybackController animatorPlaybackControllerCreatePlaybackController = pendingAnimationCreateTaskLaunchAnimation.createPlaybackController();
        animatorPlaybackControllerCreatePlaybackController.setEndAction(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$uOg05ua15d2rRzVl2AauXF2hPjw
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$createLaunchAnimationForRunningTask$2$TaskView(pendingAnimationCreateTaskLaunchAnimation);
            }
        });
        return animatorPlaybackControllerCreatePlaybackController;
    }

    public /* synthetic */ void lambda$createLaunchAnimationForRunningTask$2$TaskView(PendingAnimation pendingAnimation) {
        pendingAnimation.finish(true, 3);
        launchTaskAnimated();
    }

    public RunnableList launchTaskAnimated() {
        return launchTaskAnimated(false);
    }

    public RunnableList launchTaskAnimated(boolean isThreeButtonToggleOverview) {
        if (this.mTask == null) {
            return null;
        }
        RecentsView recentsView = getRecentsView();
        if (recentsView.getSplitPlaceholder() != null && recentsView.getSplitPlaceholder().isSplitSelectActive() && !isThreeButtonToggleOverview) {
            final RunnableList runnableList = new RunnableList();
            InteractionJankMonitorWrapper.begin(this, 49, "Enter form GroupedTaskView");
            recentsView.getSplitPlaceholder().launchTasks(this, new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$SQNkWUOfYD1sc5o1cdDpoUF66l8
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TaskView.lambda$launchTaskAnimated$3(runnableList, (Boolean) obj);
                }
            }, false);
            recentsView.addSideTaskLaunchCallback(runnableList);
            return runnableList;
        }
        TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "startActivityFromRecentsAsync", this.mTask);
        ActivityOptionsWrapper activityLaunchOptions = this.mActivity.getActivityLaunchOptions(this);
        if (this.mActivity.getDisplayId() != 0) {
            activityLaunchOptions.options.setLaunchDisplayId(getDisplay().getDisplayId());
        } else if (this.mTask.key.displayId != 0 || this.mTask.key.windowingMode != 5) {
            activityLaunchOptions.options.setLaunchDisplayId(0);
        }
        if (ActivityManagerWrapper.getInstance().startActivityFromRecents(this.mTask.key, activityLaunchOptions.options)) {
            if (this.mTask.key.windowingMode == 5) {
                RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mActivity).forceInvalidateLoadedTasks();
                BaseDraggingActivity baseDraggingActivity = this.mActivity;
                if (baseDraggingActivity instanceof Launcher) {
                    ((Launcher) baseDraggingActivity).getStateManager().goToState(LauncherState.NORMAL);
                }
            }
            getRecentsView().onTaskLaunched(this.mTask);
            return activityLaunchOptions.onEndCallback;
        }
        notifyTaskLaunchFailed(TAG);
        return null;
    }

    static /* synthetic */ void lambda$launchTaskAnimated$3(RunnableList runnableList, Boolean bool) {
        runnableList.executeAllAndDestroy();
        InteractionJankMonitorWrapper.end(49);
    }

    public TaskIdAttributeContainer[] getTaskIdAttributeContainers() {
        return this.mTaskIdAttributeContainer;
    }

    public void launchTask(Consumer<Boolean> callback) {
        launchTask(callback, false);
    }

    public void launchTask(final Consumer<Boolean> callback, boolean freezeTaskList) {
        Task task = this.mTask;
        if (task != null) {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "startActivityFromRecentsAsync", task);
            final ActivityOptions activityOptionsMakeCustomAnimation = ActivityOptionsCompat.makeCustomAnimation(getContext(), 0, 0, new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$jw3UnSvQopO-TJoQ8Dk22xjdtuQ
                @Override // java.lang.Runnable
                public final void run() {
                    callback.accept(true);
                }
            }, Executors.MAIN_EXECUTOR.getHandler());
            activityOptionsMakeCustomAnimation.setLaunchDisplayId(getDisplay().getDisplayId());
            if (freezeTaskList) {
                ActivityOptionsCompat.setFreezeRecentTasksList(activityOptionsMakeCustomAnimation);
            }
            final Task.TaskKey taskKey = this.mTask.key;
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$cf3_9DKjtXkBJKwhGyKeTjDTQSM
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$launchTask$6$TaskView(taskKey, activityOptionsMakeCustomAnimation, callback);
                }
            });
            return;
        }
        callback.accept(false);
    }

    public /* synthetic */ void lambda$launchTask$6$TaskView(Task.TaskKey taskKey, ActivityOptions activityOptions, final Consumer consumer) {
        if (ActivityManagerWrapper.getInstance().startActivityFromRecents(taskKey, activityOptions)) {
            return;
        }
        Executors.MAIN_EXECUTOR.post(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$uu1QMJ0scO983WEeMIlejrgzCHI
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$launchTask$5$TaskView(consumer);
            }
        });
    }

    public /* synthetic */ void lambda$launchTask$5$TaskView(Consumer consumer) {
        notifyTaskLaunchFailed(TAG);
        consumer.accept(false);
    }

    public void onTaskListVisibilityChanged(boolean visible) {
        if (this.mTask == null) {
            return;
        }
        cancelPendingLoadTasks();
        if (visible) {
            RecentsModel recentsModelLambda$get$0$MainThreadInitializedObject = RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext());
            TaskThumbnailCache thumbnailCache = recentsModelLambda$get$0$MainThreadInitializedObject.getThumbnailCache();
            TaskIconCache iconCache = recentsModelLambda$get$0$MainThreadInitializedObject.getIconCache();
            this.mThumbnailLoadRequest = thumbnailCache.updateThumbnailInBackground(this.mTask, new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$jrhWMEB5JOSL4oi2JptzFhB801A
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$onTaskListVisibilityChanged$7$TaskView((ThumbnailData) obj);
                }
            });
            this.mIconLoadRequest = iconCache.updateIconInBackground(this.mTask, new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$5Wxcvrk3EvgVUSwFBTAabp6j5cE
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$onTaskListVisibilityChanged$8$TaskView((Task) obj);
                }
            });
            return;
        }
        this.mSnapshotView.setThumbnail(null, null);
        this.mHeaderView.unBindTask();
        setIcon(null);
        this.mTask.thumbnail = null;
    }

    public /* synthetic */ void lambda$onTaskListVisibilityChanged$7$TaskView(ThumbnailData thumbnailData) {
        this.mSnapshotView.setThumbnail(this.mTask, thumbnailData);
        this.mHeaderView.bind(this, this.mTask);
    }

    public /* synthetic */ void lambda$onTaskListVisibilityChanged$8$TaskView(Task task) {
        setIcon(task.icon, task.title);
        setTitle(task);
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && isRunningTask()) {
            getRecentsView().updateLiveTileIcon(task.icon);
        }
        this.mDigitalWellBeingToast.initialize(this.mTask);
    }

    private void cancelPendingLoadTasks() {
        TaskThumbnailCache.ThumbnailLoadRequest thumbnailLoadRequest = this.mThumbnailLoadRequest;
        if (thumbnailLoadRequest != null) {
            thumbnailLoadRequest.cancel();
            this.mThumbnailLoadRequest = null;
        }
        TaskIconCache.IconLoadRequest iconLoadRequest = this.mIconLoadRequest;
        if (iconLoadRequest != null) {
            iconLoadRequest.cancel();
            this.mIconLoadRequest = null;
        }
    }

    private boolean showTaskMenu(int action) {
        if (!getRecentsView().isClearAllHidden()) {
            getRecentsView().snapToPage(getRecentsView().indexOfChild(this));
        } else {
            this.mMenuView = TaskMenuView.showForTask(this);
            this.mActivity.getStatsLogManager().logger().withItemInfo(getItemInfo()).log(StatsLogManager.LauncherEvent.LAUNCHER_TASK_ICON_TAP_OR_LONGPRESS);
            UserEventDispatcher.newInstance(getContext()).logActionOnItem(action, 0, 11);
            TaskMenuView taskMenuView = this.mMenuView;
            if (taskMenuView != null) {
                taskMenuView.addOnAttachStateChangeListener(this.mTaskMenuStateListener);
            }
        }
        return this.mMenuView != null;
    }

    private void setIcon(Drawable icon) {
        setIcon(icon, "");
    }

    private /* synthetic */ void lambda$setIcon$9(View view) {
        showTaskMenu(0);
    }

    private /* synthetic */ boolean lambda$setIcon$10(View view) {
        requestDisallowInterceptTouchEvent(true);
        return showTaskMenu(1);
    }

    private void setIcon(Drawable icon, String title) {
        this.mHeaderView.setIconView(icon, title);
    }

    public void setOrientationState(RecentsOrientedState orientationState) {
        if (getResources().getConfiguration().orientation == 2 && (orientationState.getOrientationHandler().getRotation() == 1 || orientationState.getOrientationHandler().getRotation() == 3)) {
            return;
        }
        PagedOrientationHandler orientationHandler = orientationState.getOrientationHandler();
        boolean z = getLayoutDirection() == 1;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mSnapshotView.getLayoutParams();
        int dimension = (int) getResources().getDimension(R.dimen.task_thumbnail_top_margin);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mHeaderView.getLayoutParams();
        int i = getRecentsView().mTaskWidth;
        int i2 = getRecentsView().mTaskHeight;
        int i3 = (i2 - i) - dimension;
        int i4 = i + (dimension / 2);
        int i5 = i2 / 2;
        float f = 0.0f;
        int rotation = orientationHandler.getRotation();
        int i6 = GravityCompat.START;
        if (rotation == 1) {
            if (!z) {
                i6 = 8388613;
            }
            layoutParams2.gravity = i6 | 16;
            layoutParams2.rightMargin = -i3;
            layoutParams2.leftMargin = 0;
            layoutParams2.topMargin = layoutParams.topMargin / 2;
            f = i4 - i5;
            layoutParams.leftMargin = 0;
            layoutParams.rightMargin = dimension;
        } else if (rotation == 2) {
            layoutParams2.gravity = 81;
            layoutParams2.bottomMargin = -dimension;
            layoutParams2.rightMargin = 0;
            layoutParams2.topMargin = 0;
            layoutParams2.leftMargin = 0;
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
        } else if (rotation == 3) {
            if (z) {
                i6 = 8388613;
            }
            layoutParams2.gravity = i6 | 16;
            layoutParams2.leftMargin = -i3;
            layoutParams2.rightMargin = 0;
            layoutParams2.topMargin = layoutParams.topMargin / 2;
            f = -(i4 - i5);
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = dimension;
        } else {
            layoutParams2.gravity = 49;
            layoutParams2.rightMargin = 0;
            layoutParams2.topMargin = 0;
            layoutParams2.leftMargin = 0;
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
        }
        this.mSnapshotView.setLayoutParams(layoutParams);
        this.mHeaderView.setLayoutParams(layoutParams2);
        this.mHeaderView.setTranslationX(f);
        this.mHeaderView.setRotation(orientationHandler.getDegreesRotated());
        TaskHeaderView taskHeaderView = this.mHeaderView;
        if (taskHeaderView == null || taskHeaderView.getTaskMenuView() == null || !this.mHeaderView.getTaskMenuView().isOpen()) {
            return;
        }
        this.mHeaderView.getTaskMenuView().close(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIconAndDimTransitionProgress(float progress, boolean invert) {
        if (invert) {
            progress = 1.0f - progress;
        }
        this.mFocusTransitionProgress = progress;
        this.mSnapshotView.setDimAlphaMultipler(progress);
        float interpolation = Interpolators.clampToProgress(Interpolators.FAST_OUT_SLOW_IN, invert ? 0.82857144f : 0.0f, invert ? 1.0f : 0.17142858f).getInterpolation(progress);
        this.mHeaderView.setAlpha(interpolation);
        this.mHeaderView.setAlpha(interpolation);
        updateFooterVerticalOffset(1.0f - interpolation);
    }

    public void setIconScaleAnimStartProgress(float startProgress) {
        this.mIconScaleAnimStartProgress = startProgress;
    }

    public void animateIconScaleAndDimIntoView() {
        ObjectAnimator objectAnimator = this.mIconAndDimAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, FOCUS_TRANSITION, 1.0f);
        this.mIconAndDimAnimator = objectAnimatorOfFloat;
        objectAnimatorOfFloat.setCurrentFraction(this.mIconScaleAnimStartProgress);
        this.mIconAndDimAnimator.setDuration(DIM_ANIM_DURATION).setInterpolator(Interpolators.LINEAR);
        this.mIconAndDimAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.views.TaskView.15
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                TaskView.this.mIconAndDimAnimator = null;
            }
        });
        this.mIconAndDimAnimator.start();
    }

    protected void setIconScaleAndDim(float iconScale) {
        setIconScaleAndDim(iconScale, false);
    }

    private void setIconScaleAndDim(float iconScale, boolean invert) {
        ObjectAnimator objectAnimator = this.mIconAndDimAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        setIconAndDimTransitionProgress(iconScale, invert);
    }

    protected void resetViewTransforms() {
        setCurveScale(1.0f);
        setTranslationX(0.0f);
        setTranslationY(0.0f);
        if (!LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue()) {
            setTranslationZ(0.0f);
        }
        setAlpha(this.mStableAlpha);
        setIconScaleAndDim(1.0f);
    }

    public float getStableAlpha() {
        return this.mStableAlpha;
    }

    public void setStableAlpha(float parentAlpha) {
        setStableAlpha(parentAlpha, false);
    }

    public void setStableAlpha(float parentAlpha, boolean onlyValue) {
        this.mStableAlpha = parentAlpha;
        if (onlyValue) {
            return;
        }
        setAlpha(parentAlpha);
    }

    @Override // com.android.launcher3.util.ViewPool.Reusable
    public void onRecycle() {
        resetViewTransforms();
        this.mSnapshotView.setThumbnail(this.mTask, null);
        setOverlayEnabled(false);
        onTaskListVisibilityChanged(false);
    }

    @Override // com.android.quickstep.views.RecentsView.PageCallbacks
    public void onPageScroll(RecentsView.ScrollState scrollState) {
        if (this.mModalness > 0.0f) {
            return;
        }
        if (!LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue()) {
            float interpolation = CURVE_INTERPOLATOR.getInterpolation(scrollState.linearInterpolation);
            float curveScaleForCurveInterpolation = LGHomeFeature.Config.FEATURE_UX_9_21.getValue() ? getCurveScaleForCurveInterpolation(interpolation) : 1.0f;
            this.mSnapshotView.setDimAlpha(interpolation * MAX_PAGE_SCRIM_ALPHA);
            setCurveScale(curveScaleForCurveInterpolation);
            this.mFooterAlpha = Utilities.boundToRange(1.0f - (scrollState.linearInterpolation * 2.0f), 0.0f, 1.0f);
            for (FooterWrapper footerWrapper : this.mFooters) {
                if (footerWrapper != null) {
                    footerWrapper.mView.setAlpha(this.mFooterAlpha);
                }
            }
        } else {
            layout((int) scrollState.leftOffset, getTop(), ((int) scrollState.leftOffset) + getMeasuredWidth(), getTop() + getMeasuredHeight());
            this.mHeaderView.setDimAlpha(scrollState.linearInterpolation);
            this.mSnapshotView.setDimAlpha(scrollState.linearInterpolation);
            setCurveScale(1.0f);
        }
        if (this.mMenuView != null) {
            PagedOrientationHandler pagedOrientationHandler = getPagedOrientationHandler();
            RecentsView recentsView = getRecentsView();
            this.mMenuView.setPosition(getX() - recentsView.getScrollX(), getY() - recentsView.getScrollY(), pagedOrientationHandler);
            this.mMenuView.setScaleX(getScaleX());
            this.mMenuView.setScaleY(getScaleY());
        }
    }

    public View setFooter(int index, View view) {
        View view2;
        boolean z = this.mFooterVerticalOffset <= 0.0f;
        FooterWrapper[] footerWrapperArr = this.mFooters;
        if (footerWrapperArr[index] != null) {
            View view3 = footerWrapperArr[index].mView;
            this.mFooters[index].release();
            removeView(view3);
            view2 = view3;
            z = false;
        } else {
            view2 = null;
        }
        if (view != null) {
            int childCount = getChildCount();
            int i = index - 1;
            while (true) {
                if (i < 0) {
                    break;
                }
                FooterWrapper[] footerWrapperArr2 = this.mFooters;
                if (footerWrapperArr2[i] != null) {
                    childCount = indexOfChild(footerWrapperArr2[i].mView);
                    break;
                }
                i--;
            }
            addView(view, childCount);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.gravity = 81;
            layoutParams.bottomMargin = ((ViewGroup.MarginLayoutParams) this.mSnapshotView.getLayoutParams()).bottomMargin;
            view.setAlpha(this.mFooterAlpha);
            this.mFooters[index] = new FooterWrapper(view);
            if (z) {
                this.mFooters[index].animateEntry();
            }
        } else {
            this.mFooters[index] = null;
        }
        this.mStackHeight = 0;
        for (FooterWrapper footerWrapper : this.mFooters) {
            if (footerWrapper != null) {
                footerWrapper.setVerticalShift(this.mStackHeight);
                this.mStackHeight += footerWrapper.mExpectedHeight;
            }
        }
        return view2;
    }

    public void setContextualChip(View view) {
        View view2 = this.mContextualChipWrapper;
        if (view2 != null) {
            removeView(view2);
        }
        if (view != null) {
            this.mContextualChipWrapper = view;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
            layoutParams.gravity = 81;
            int expectedViewHeight = getExpectedViewHeight(view);
            float dimension = getResources().getDimension(R.dimen.chip_hint_vertical_offset);
            layoutParams.bottomMargin = (int) ((((ViewGroup.MarginLayoutParams) this.mSnapshotView.getLayoutParams()).bottomMargin - expectedViewHeight) + dimension);
            View childAt = ((FrameLayout) this.mContextualChipWrapper).getChildAt(0);
            this.mContextualChip = childAt;
            childAt.setScaleX(0.0f);
            this.mContextualChip.setScaleY(0.0f);
            GradientDrawable gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.chip_scrim_gradient, this.mActivity.getTheme());
            float taskCornerRadius = getTaskCornerRadius();
            gradientDrawable.setCornerRadii(new float[]{0.0f, 0.0f, 0.0f, 0.0f, taskCornerRadius, taskCornerRadius, taskCornerRadius, taskCornerRadius});
            this.mContextualChipWrapper.setBackground(new InsetDrawable((Drawable) gradientDrawable, 0, 0, 0, (int) (expectedViewHeight - dimension)));
            this.mContextualChipWrapper.setPadding(0, 0, 0, 0);
            this.mContextualChipWrapper.setAlpha(0.0f);
            addView(view, getChildCount(), layoutParams);
            View view3 = this.mContextualChip;
            if (view3 != null) {
                view3.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50L);
            }
            View view4 = this.mContextualChipWrapper;
            if (view4 != null) {
                view4.animate().alpha(1.0f).setDuration(50L);
            }
        }
    }

    public float getTaskCornerRadius() {
        return TaskCornerRadius.get(this.mActivity);
    }

    public View clearContextualChip() {
        View view = this.mContextualChipWrapper;
        if (view != null) {
            removeView(view);
        }
        View view2 = this.mContextualChipWrapper;
        this.mContextualChipWrapper = null;
        this.mContextualChip = null;
        return view2;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setPivotX((right - left) * 0.5f);
        setPivotY(this.mSnapshotView.getTop() + (this.mSnapshotView.getHeight() * 0.5f));
        List<Rect> list = SYSTEM_GESTURE_EXCLUSION_RECT;
        list.get(0).set(0, 0, getWidth(), getHeight());
        setSystemGestureExclusionRects(list);
        this.mStackHeight = 0;
        for (FooterWrapper footerWrapper : this.mFooters) {
            if (footerWrapper != null) {
                this.mStackHeight += footerWrapper.mView.getHeight();
            }
        }
        updateFooterVerticalOffset(0.0f);
    }

    private void updateFooterVerticalOffset(float offset) {
        this.mFooterVerticalOffset = offset;
        for (FooterWrapper footerWrapper : this.mFooters) {
            if (footerWrapper != null) {
                footerWrapper.updateFooterOffset();
            }
        }
    }

    public static float getCurveScaleForInterpolation(float linearInterpolation) {
        return getCurveScaleForCurveInterpolation(CURVE_INTERPOLATOR.getInterpolation(linearInterpolation));
    }

    private static float getCurveScaleForCurveInterpolation(float curveInterpolation) {
        return 1.0f - (curveInterpolation * EDGE_SCALE_DOWN_FACTOR);
    }

    private void setCurveScale(float curveScale) {
        this.mCurveScale = curveScale;
        setScaleX(curveScale);
        setScaleY(this.mCurveScale);
    }

    public float getCurveScale() {
        return this.mCurveScale;
    }

    private void setNonGridScale(float nonGridScale) {
        this.mNonGridScale = nonGridScale;
        applyScale();
    }

    public float getNonGridScale() {
        return this.mNonGridScale;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSnapshotScale(float dismissScale) {
        this.mDismissScale = dismissScale;
        applyScale();
    }

    public void setGridProgress(float gridProgress) {
        this.mGridProgress = gridProgress;
        applyTranslationX();
        applyTranslationY();
        applyScale();
    }

    private void applyScale() {
        float persistentScale = getPersistentScale() * 1.0f * this.mDismissScale;
        setScaleX(persistentScale);
        setScaleY(persistentScale);
        updateSnapshotRadius();
    }

    public float getPersistentScale() {
        return Utilities.mapRange(GRID_INTERPOLATOR.getInterpolation(this.mGridProgress), this.mNonGridScale, 1.0f) * 1.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSplitSelectTranslationX(float x) {
        this.mSplitSelectTranslationX = x;
        applyTranslationX();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSplitSelectTranslationY(float y) {
        this.mSplitSelectTranslationY = y;
        applyTranslationY();
    }

    public void setSplitScrollOffsetPrimary(float splitSelectScrollOffsetPrimary) {
        this.mSplitSelectScrollOffsetPrimary = splitSelectScrollOffsetPrimary;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDismissTranslationX(float x) {
        this.mDismissTranslationX = x;
        applyTranslationX();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDismissTranslationY(float y) {
        this.mDismissTranslationY = y;
        applyTranslationY();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTaskOffsetTranslationX(float x) {
        this.mTaskOffsetTranslationX = x;
        applyTranslationX();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTaskOffsetTranslationY(float y) {
        this.mTaskOffsetTranslationY = y;
        applyTranslationY();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTaskResistanceTranslationX(float x) {
        this.mTaskResistanceTranslationX = x;
        applyTranslationX();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTaskResistanceTranslationY(float y) {
        this.mTaskResistanceTranslationY = y;
        applyTranslationY();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNonGridTranslationX(float nonGridTranslationX) {
        this.mNonGridTranslationX = nonGridTranslationX;
        applyTranslationX();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNonGridTranslationY(float nonGridTranslationY) {
        this.mNonGridTranslationY = nonGridTranslationY;
        applyTranslationY();
    }

    public void setGridTranslationX(float gridTranslationX) {
        this.mGridTranslationX = gridTranslationX;
        applyTranslationX();
    }

    public float getGridTranslationX() {
        return this.mGridTranslationX;
    }

    public void setGridTranslationY(float gridTranslationY) {
        this.mGridTranslationY = gridTranslationY;
        applyTranslationY();
    }

    public float getGridTranslationY() {
        return this.mGridTranslationY;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setGridEndTranslationX(float gridEndTranslationX) {
        this.mGridEndTranslationX = gridEndTranslationX;
        applyTranslationX();
    }

    public float getScrollAdjustment(boolean fullscreenEnabled, boolean gridEnabled) {
        float fFloatValue;
        if (gridEnabled) {
            fFloatValue = this.mGridTranslationX;
        } else {
            fFloatValue = ((Float) getPrimaryNonGridTranslationProperty().get(this)).floatValue();
        }
        return fFloatValue + 0.0f + this.mSplitSelectScrollOffsetPrimary;
    }

    public float getOffsetAdjustment(boolean fullscreenEnabled, boolean gridEnabled) {
        return getScrollAdjustment(fullscreenEnabled, gridEnabled);
    }

    public float getSizeAdjustment(boolean fullscreenEnabled) {
        if (fullscreenEnabled) {
            return 1.0f * this.mNonGridScale;
        }
        return 1.0f;
    }

    private void setBoxTranslationY(float boxTranslationY) {
        this.mBoxTranslationY = boxTranslationY;
        applyTranslationY();
    }

    private void applyTranslationX() {
        setTranslationX(this.mDismissTranslationX + this.mTaskOffsetTranslationX + this.mTaskResistanceTranslationX + this.mSplitSelectTranslationX + this.mGridEndTranslationX + getPersistentTranslationX());
    }

    private void applyTranslationY() {
        setTranslationY(this.mDismissTranslationY + this.mTaskOffsetTranslationY + this.mTaskResistanceTranslationY + this.mSplitSelectTranslationY + getPersistentTranslationY());
    }

    public float getPersistentTranslationX() {
        return getNonGridTrans(this.mNonGridTranslationX) + getGridTrans(this.mGridTranslationX);
    }

    public float getPersistentTranslationY() {
        return this.mBoxTranslationY + getNonGridTrans(this.mNonGridTranslationY) + getGridTrans(this.mGridTranslationY);
    }

    public FloatProperty<TaskView> getPrimarySplitTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getPrimaryValue(SPLIT_SELECT_TRANSLATION_X, SPLIT_SELECT_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getSecondarySplitTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getSecondaryValue(SPLIT_SELECT_TRANSLATION_X, SPLIT_SELECT_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getPrimaryDismissTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getPrimaryValue(DISMISS_TRANSLATION_X, DISMISS_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getSecondaryDissmissTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getSecondaryValue(DISMISS_TRANSLATION_X, DISMISS_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getPrimaryTaskOffsetTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getPrimaryValue(TASK_OFFSET_TRANSLATION_X, TASK_OFFSET_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getTaskResistanceTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getSecondaryValue(TASK_RESISTANCE_TRANSLATION_X, TASK_RESISTANCE_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getPrimaryNonGridTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getPrimaryValue(NON_GRID_TRANSLATION_X, NON_GRID_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getSecondaryNonGridTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getSecondaryValue(NON_GRID_TRANSLATION_X, NON_GRID_TRANSLATION_Y);
    }

    private static final class TaskOutlineProvider extends ViewOutlineProvider {
        private FullscreenDrawParams mFullscreenParams;
        private final int mMarginTop;

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
        }

        TaskOutlineProvider(Context context, FullscreenDrawParams fullscreenParams) {
            this.mMarginTop = context.getResources().getDimensionPixelSize(R.dimen.task_thumbnail_top_margin);
            this.mFullscreenParams = fullscreenParams;
        }

        public void setFullscreenParams(FullscreenDrawParams params) {
            this.mFullscreenParams = params;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class FooterWrapper extends ViewOutlineProvider {
        final ViewOutlineProvider mDelegate;
        final int mExpectedHeight;
        final ViewOutlineProvider mOldOutlineProvider;
        final int mOldPaddingBottom;
        final View mView;
        int mAnimationOffset = 0;
        int mEntryAnimationOffset = 0;

        public FooterWrapper(View view) {
            this.mView = view;
            ViewOutlineProvider outlineProvider = view.getOutlineProvider();
            this.mOldOutlineProvider = outlineProvider;
            this.mDelegate = outlineProvider == null ? ViewOutlineProvider.BACKGROUND : outlineProvider;
            this.mExpectedHeight = TaskView.this.getExpectedViewHeight(view);
            this.mOldPaddingBottom = view.getPaddingBottom();
            if (outlineProvider != null) {
                view.setOutlineProvider(this);
                view.setClipToOutline(true);
            }
        }

        public void setVerticalShift(int shift) {
            View view = this.mView;
            view.setPadding(view.getPaddingLeft(), this.mView.getPaddingTop(), this.mView.getPaddingRight(), this.mOldPaddingBottom + shift);
        }

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            this.mDelegate.getOutline(view, outline);
            outline.offset(0, (-this.mAnimationOffset) - this.mEntryAnimationOffset);
        }

        void updateFooterOffset() {
            this.mAnimationOffset = Math.round(TaskView.this.mStackHeight * Utilities.or(TaskView.this.mFooterVerticalOffset, TaskView.this.mModalness));
            this.mView.setTranslationY(r0 + this.mEntryAnimationOffset + TaskView.this.mCurrentFullscreenParams.mCurrentDrawnInsets.bottom + TaskView.this.mCurrentFullscreenParams.mCurrentDrawnInsets.top);
            this.mView.invalidateOutline();
        }

        void release() {
            this.mView.setOutlineProvider(this.mOldOutlineProvider);
            setVerticalShift(0);
        }

        void animateEntry() {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.quickstep.views.-$$Lambda$TaskView$FooterWrapper$WH6xfMOCRDiRr4lfM5MkJ9CEbhY
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$animateEntry$0$TaskView$FooterWrapper(valueAnimator);
                }
            });
            valueAnimatorOfFloat.setDuration(100L);
            valueAnimatorOfFloat.start();
        }

        public /* synthetic */ void lambda$animateEntry$0$TaskView$FooterWrapper(ValueAnimator valueAnimator) {
            this.mEntryAnimationOffset = Math.round((1.0f - valueAnimator.getAnimatedFraction()) * ((this.mExpectedHeight + this.mView.getPaddingBottom()) - this.mOldPaddingBottom));
            updateFooterOffset();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getExpectedViewHeight(View view) {
        int i = view.getLayoutParams().height;
        if (i > 0) {
            return i;
        }
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(1073741823, Integer.MIN_VALUE);
        view.measure(iMakeMeasureSpec, iMakeMeasureSpec);
        return view.getMeasuredHeight();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.string.accessibility_close, getContext().getText(R.string.accessibility_close)));
        Context context = getContext();
        this.mSnapshotView.setContentDescription(getTask().title);
        Iterator<SystemShortcut> it = TaskOverlayFactory.getEnabledShortcuts(this, this.mActivity.getDeviceProfile()).iterator();
        while (it.hasNext()) {
            info.addAction(it.next().createAccessibilityAction(context));
        }
        if (this.mDigitalWellBeingToast.hasLimit()) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.string.accessibility_app_usage_settings, getContext().getText(R.string.accessibility_app_usage_settings)));
        }
        RecentsView recentsView = getRecentsView();
        info.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(0, 1, (recentsView.getTaskViewCount() - recentsView.indexOfChild(this)) - 1, 1, false));
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (action == R.string.accessibility_close) {
            getRecentsView().dismissTask(this, true, true);
            return true;
        }
        if (action == R.string.accessibility_app_usage_settings) {
            this.mDigitalWellBeingToast.openAppUsageSettings(this);
            return true;
        }
        for (SystemShortcut systemShortcut : TaskOverlayFactory.getEnabledShortcuts(this, this.mActivity.getDeviceProfile())) {
            if (systemShortcut.hasHandlerForAction(action)) {
                systemShortcut.onClick(this);
                return true;
            }
        }
        return super.performAccessibilityAction(action, arguments);
    }

    public RecentsView getRecentsView() {
        return (RecentsView) getParent();
    }

    PagedOrientationHandler getPagedOrientationHandler() {
        return getRecentsView().mOrientationState.getOrientationHandler();
    }

    public void notifyTaskLaunchFailed(String tag) {
        Task task = this.mTask;
        String str = "Failed to launch task";
        if (task != null) {
            str = "Failed to launch task (task=" + task.key.baseIntent + " userId=" + this.mTask.key.userId + ")";
        }
        Log.w(tag, str);
        Toast.makeText(getContext(), R.string.activity_not_available, 0).show();
    }

    public void setFullscreenProgress(float progress) {
        float fBoundToRange = Utilities.boundToRange(progress, 0.0f, 1.0f);
        this.mFullscreenProgress = fBoundToRange;
        boolean z = fBoundToRange > 0.0f;
        this.mHeaderView.setVisibility(fBoundToRange >= 1.0f ? 4 : 0);
        setClipChildren(!z);
        setClipToPadding(!z);
        TaskThumbnailView thumbnail = getThumbnail();
        updateCurrentFullscreenParams(thumbnail.getPreviewPositionHelper());
        if (!getRecentsView().isTaskIconScaledDown(this)) {
            setIconScaleAndDim(fBoundToRange, true);
        }
        thumbnail.setFullscreenParams(this.mCurrentFullscreenParams);
        this.mOutlineProvider.setFullscreenParams(this.mCurrentFullscreenParams);
        invalidateOutline();
    }

    protected void updateSnapshotRadius() {
        updateCurrentFullscreenParams(this.mSnapshotView.getPreviewPositionHelper());
        this.mSnapshotView.setFullscreenParams(this.mCurrentFullscreenParams);
    }

    void updateCurrentFullscreenParams(TaskThumbnailView.PreviewPositionHelper previewPositionHelper) {
        if (getRecentsView() == null) {
            return;
        }
        this.mCurrentFullscreenParams.setProgress(this.mFullscreenProgress, getRecentsView() != null ? getRecentsView().getScaleX() : 1.0f, getWidth(), this.mActivity.getDeviceProfile(), previewPositionHelper);
    }

    private float getGridTrans(float endTranslation) {
        return Utilities.mapRange(GRID_INTERPOLATOR.getInterpolation(this.mGridProgress), 0.0f, endTranslation);
    }

    private float getNonGridTrans(float endTranslation) {
        return endTranslation - getGridTrans(endTranslation);
    }

    public boolean isRunningTask() {
        return getRecentsView() != null && this == getRecentsView().getRunningTaskView();
    }

    public boolean isFocusedTask() {
        return getRecentsView() != null && this == getRecentsView().getCurrentPageTaskView();
    }

    public void setShowScreenshot(boolean showScreenshot) {
        this.mShowScreenshot = showScreenshot;
    }

    public boolean showScreenshot() {
        if (isRunningTask()) {
            return this.mShowScreenshot;
        }
        return true;
    }

    public void setOverlayEnabled(boolean overlayEnabled) {
        this.mSnapshotView.setOverlayEnabled(overlayEnabled);
    }

    public void initiateSplitSelect(SplitConfigurationOptions.SplitPositionOption splitPositionOption) {
        AbstractFloatingView.closeOpenViews(this.mActivity, false, 512);
        getRecentsView().initiateSplitSelect(this, splitPositionOption.stagePosition);
    }

    public static class FullscreenDrawParams {
        private final float mCornerRadius;
        public float mCurrentDrawnCornerRadius;
        public RectF mCurrentDrawnInsets = new RectF();
        public float mScale = 1.0f;
        private final float mWindowCornerRadius;

        public FullscreenDrawParams(Context context) {
            float f = TaskCornerRadius.get(context);
            this.mCornerRadius = f;
            this.mWindowCornerRadius = QuickStepContract.getWindowCornerRadius(context);
            this.mCurrentDrawnCornerRadius = f;
        }

        public void setProgress(float fullscreenProgress, float parentScale, int previewWidth, DeviceProfile dp, TaskThumbnailView.PreviewPositionHelper pph) {
            RectF insetsToDrawInFullscreen = pph.getInsetsToDrawInFullscreen();
            float f = insetsToDrawInFullscreen.left * fullscreenProgress;
            float f2 = insetsToDrawInFullscreen.right * fullscreenProgress;
            this.mCurrentDrawnInsets.set(f, insetsToDrawInFullscreen.top * fullscreenProgress, f2, insetsToDrawInFullscreen.bottom * fullscreenProgress);
            this.mCurrentDrawnCornerRadius = Utilities.mapRange(fullscreenProgress, this.mCornerRadius, dp.isMultiWindowMode ? 0.0f : this.mWindowCornerRadius) / parentScale;
            if (previewWidth > 0) {
                float f3 = previewWidth;
                this.mScale = f3 / ((f + f3) + f2);
            }
        }
    }

    private void setTitle(Task task) {
        this.mHeaderView.setTitleView(task);
    }

    public void setHeaderViewVisibility(int item, boolean visible) {
        TaskHeaderView taskHeaderView = this.mHeaderView;
        if (taskHeaderView != null) {
            taskHeaderView.setVisibility(item, visible);
        }
    }

    public boolean isPinned() {
        if (getTask() != null) {
            return getTask().isPinned;
        }
        return false;
    }

    public void doPinAnimation() {
        TaskHeaderView taskHeaderView = this.mHeaderView;
        if (taskHeaderView != null) {
            taskHeaderView.startPinButtonShakeAnimation();
        }
    }

    public void setEnabledButtonsOfHeader(boolean enable) {
        TaskHeaderView taskHeaderView = this.mHeaderView;
        if (taskHeaderView != null) {
            taskHeaderView.setEnabledButtons(enable);
        }
    }

    public void setDimAlpha(float dimAlpha) {
        if (!LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
            this.mHeaderView.setDimAlpha(dimAlpha);
        }
        this.mSnapshotView.setDimAlpha(dimAlpha);
    }

    public float getDimAlpha() {
        return this.mSnapshotView.getDimAlpha();
    }

    public /* synthetic */ void lambda$new$11$TaskView() {
        if (isAdaptedWindowFlag(24)) {
            LGLog.i(TAG, "launchTaskInternal(): clearFlags FLAG_NOT_FOCUSABLE");
            this.mWindow.clearFlags(24);
        }
    }

    public void checkFocusableFlag() {
        if (isAdaptedWindowFlag(24)) {
            this.mEnableFocusableRunnable.run();
        }
    }

    public boolean isAdaptedWindowFlag(int flag) {
        Window window = this.mActivity.getWindow();
        this.mWindow = window;
        return (window.getAttributes().flags & flag) == flag;
    }

    public View getHeaderView() {
        return this.mHeaderView;
    }

    public class TaskIdAttributeContainer {
        private final int mA11yNodeId;
        private final TaskHeaderView mHeaderView;
        private final IconView mIconView;
        private int mStagePosition;
        private final Task mTask;
        private final TaskThumbnailView mThumbnailView;

        public TaskIdAttributeContainer(Task task, TaskThumbnailView thumbnailView, IconView iconView, TaskHeaderView headerView, int stagePosition) {
            this.mTask = task;
            this.mThumbnailView = thumbnailView;
            this.mIconView = iconView;
            this.mHeaderView = headerView;
            this.mStagePosition = stagePosition;
            this.mA11yNodeId = stagePosition == 1 ? R.id.split_bottomRight_appInfo : R.id.split_topLeft_appInfo;
        }

        public TaskThumbnailView getThumbnailView() {
            return this.mThumbnailView;
        }

        public Task getTask() {
            return this.mTask;
        }

        public WorkspaceItemInfo getItemInfo() {
            return TaskView.this.getItemInfo();
        }

        public TaskView getTaskView() {
            return TaskView.this;
        }

        public IconView getIconView() {
            return this.mIconView;
        }

        public ImageView getIconViewOnHeaderView() {
            return this.mHeaderView.getIconView();
        }

        public int getStagePosition() {
            return this.mStagePosition;
        }

        void setStagePosition(int stagePosition) {
            this.mStagePosition = stagePosition;
        }

        public int getA11yNodeId() {
            return this.mA11yNodeId;
        }
    }
}
