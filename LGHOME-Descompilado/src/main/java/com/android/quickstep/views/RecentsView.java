package com.android.quickstep.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.UserHandle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Pair;
import android.util.Property;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherRootView;
import com.android.launcher3.LauncherState;
import com.android.launcher3.PagedView;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.anim.SpringProperty;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.popup.QuickstepSystemShortcut;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.uioverrides.DeviceFlag;
import com.android.launcher3.util.DynamicResource;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.OverScroller;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.util.SystemUiController;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.ViewPool;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.RecentsAnimationController;
import com.android.quickstep.RecentsAnimationTargets;
import com.android.quickstep.RecentsModel;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskThumbnailCache;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.ViewUtils;
import com.android.quickstep.fallback.FallbackRecentsView;
import com.android.quickstep.util.RecentsOrientedState;
import com.android.quickstep.util.SplitScreenBounds;
import com.android.quickstep.util.SplitSelectStateController;
import com.android.quickstep.util.SurfaceTransactionApplier;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.plugins.ResourceProvider;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.wm.shell.pip.IPipAnimationListener;
import com.lge.launcher3.R;
import com.lge.launcher3.quickstep.ActivityManagerWrapperEx;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* JADX INFO: loaded from: classes.dex */
public abstract class RecentsView<T extends StatefulActivity> extends PagedView implements Insettable, TaskThumbnailCache.HighResLoadingState.HighResLoadingStateChangedCallback, InvariantDeviceProfile.OnIDPChangeListener, RecentsModel.TaskVisualsChangeListener, SplitScreenBounds.OnChangeListener {
    private static final int ADDITION_TASK_DURATION = 200;
    private static final int DISMISS_TASK_DURATION = 300;
    public static final float OVERVIEW_NEW_UI_CENTER_DISTANCE_RATIO = 1.21f;
    public static final float OVERVIEW_NEW_UI_DISMISS_ANI_OFFSET = 50.0f;
    public static final float OVERVIEW_NEW_UI_MIN_DIM = 0.8f;
    public static final float OVERVIEW_NEW_UI_OPEN_MENU_DIM = 0.3f;
    public static final float OVERVIEW_NEW_UI_OPEN_MENU_SCALE = 1.03f;
    public static final float OVERVIEW_OPEN_MENU_CENTER_PAGE_DIM = 0.2f;
    public static final float OVERVIEW_OPEN_MENU_SIDE_PAGE_DIM = 0.5f;
    private static final String TAG = "RecentsView";
    public static final float UPDATE_SYSUI_FLAGS_THRESHOLD = 0.85f;
    private final int[] INVALID_TASK_IDS;
    private final int MAX_SCROLL_PAGE_GAP;
    protected final T mActivity;
    private float mAdjacentPageOffset;
    private ClearAllButton mClearAllButton;
    private final Rect mClearAllButtonDeadZoneRect;
    private int mClearAllButtonHeight;
    private int mClearAllButtonWidth;
    private Rect mClipBound;
    public int mConsumerId;

    @ViewDebug.ExportedProperty(category = DeviceFlag.NAMESPACE_LAUNCHER)
    protected float mContentAlpha;
    protected GestureState.GestureEndTarget mCurrentGestureEndTarget;
    protected boolean mDisallowScrollToClearAll;
    private int mDownX;
    private int mDownY;
    private boolean mDwbToastShown;
    private final Drawable mEmptyIcon;
    private final CharSequence mEmptyMessage;
    private final int mEmptyMessagePadding;
    private final TextPaint mEmptyMessagePaint;
    private Layout mEmptyTextLayout;
    protected boolean mEnableDrawingLiveTile;
    private final float mFastFlingVelocity;
    private FloatingTaskView mFirstFloatingTaskView;
    protected boolean mFreezeViewVisibility;

    @ViewDebug.ExportedProperty(category = DeviceFlag.NAMESPACE_LAUNCHER)
    protected float mFullscreenProgress;
    private boolean mHandleTaskStackChanges;
    private final SparseBooleanArray mHasVisibleTaskData;
    private final PinnedStackAnimationListener mIPipAnimationListener;
    private int mIdOfTaskList;
    private final InvariantDeviceProfile mIdp;
    private int mIgnoreResetTaskId;
    private boolean mIsAnimRunning;
    private boolean mIsDragging;
    protected final Rect mLastComputedGridSize;
    protected final Rect mLastComputedGridTaskSize;
    protected Float mLastComputedTaskEndPushOutDistance;
    protected final Rect mLastComputedTaskSize;
    protected Float mLastComputedTaskStartPushOutDistance;
    private final Point mLastMeasureSize;
    private LayoutTransition mLayoutTransition;
    private boolean mLiveTileOverlayAttached;
    private final RecentsModel mModel;
    private BaseActivity.MultiWindowModeChangedListener mMultiWindowModeChangedListener;
    private OnEmptyMessageUpdatedListener mOnEmptyMessageUpdatedListener;
    protected RecentsOrientedState mOrientationState;
    private boolean mOverlayEnabled;
    private boolean mOverviewEnterAnimationWorking;
    private boolean mOverviewFullscreenEnabled;
    private boolean mOverviewGridEnabled;
    private boolean mOverviewSelectEnabled;
    private boolean mOverviewStateEnabled;
    private PendingAnimation mPendingAnimation;
    private PendingAnimation mPendingTaskMenuAnimation;
    private int mPipCornerRadius;
    private int mPipShadowRadius;
    private int mPreScrollX;
    private int mPreVelocity;
    protected RecentsAnimationController mRecentsAnimationController;
    protected RecentsAnimationTargets mRecentsAnimationTargets;
    private RecommandAppLayout mRecommandLayout;
    private int mRecommandLayoutHeight;
    private boolean mRunningTaskIconScaledDown;
    protected int mRunningTaskId;
    protected boolean mRunningTaskTileHidden;
    private final ScrollState mScrollState;
    private FloatingTaskView mSecondFloatingTaskView;
    private View mSecondSplitHiddenView;
    private boolean mShowEmptyMessage;
    private RunnableList mSideTaskLaunchCallback;
    protected final BaseActivityInterface mSizeStrategy;
    private SplitConfigurationOptions.StagedSplitBounds mSplitBoundsConfig;
    private TaskView mSplitHiddenTaskView;
    private int mSplitHiddenTaskViewIndex;
    private final int mSplitPlaceholderInset;
    private final int mSplitPlaceholderSize;
    private QuickstepSystemShortcut.SplitSelectSource mSplitSelectSource;
    private SplitSelectStateController mSplitSelectStateController;
    private final Toast mSplitUnsupportedToast;
    private final float mSquaredTouchSlop;
    private boolean mSwipeDownShouldLaunchApp;
    protected SurfaceTransactionApplier mSyncTransactionApplier;
    protected int mTaskHeight;
    private int mTaskListChangeId;

    @ViewDebug.ExportedProperty(category = DeviceFlag.NAMESPACE_LAUNCHER)
    protected float mTaskModalness;
    private final TaskStackChangeListener mTaskStackListener;
    private final int mTaskTopMargin;
    private final Rect mTaskViewDeadZoneRect;
    private int mTaskViewIdCount;
    private final ViewPool<TaskView> mTaskViewPool;
    private int mTaskViewStartIndex;
    protected float mTaskViewsPrimarySplitTranslation;
    protected float mTaskViewsSecondarySplitTranslation;
    protected float mTaskViewsSecondaryTranslation;
    protected int mTaskWidth;
    private final float[] mTempFloat;
    private final Matrix mTempMatrix;
    private final PointF mTempPointF;
    protected final Rect mTempRect;
    protected final RectF mTempRectF;
    private Task mTmpRunningTask;
    private boolean mTouchDownToStartHome;
    private int mUnboundedScrollX;
    private boolean mUseOverScrollEffect;
    public static final FloatProperty<RecentsView> TASK_PRIMARY_SPLIT_TRANSLATION = new FloatProperty<RecentsView>("taskPrimarySplitTranslation") { // from class: com.android.quickstep.views.RecentsView.1
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(RecentsView recentsView, float v) {
            recentsView.setTaskViewsPrimarySplitTranslation(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mTaskViewsPrimarySplitTranslation);
        }
    };
    public static final FloatProperty<RecentsView> TASK_SECONDARY_SPLIT_TRANSLATION = new FloatProperty<RecentsView>("taskSecondarySplitTranslation") { // from class: com.android.quickstep.views.RecentsView.2
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(RecentsView recentsView, float v) {
            recentsView.setTaskViewsSecondarySplitTranslation(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mTaskViewsSecondarySplitTranslation);
        }
    };
    public static final FloatProperty<RecentsView> CONTENT_ALPHA = new FloatProperty<RecentsView>("contentAlpha") { // from class: com.android.quickstep.views.RecentsView.3
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(RecentsView view, float v) {
            view.setContentAlpha(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(RecentsView view) {
            return Float.valueOf(view.getContentAlpha());
        }
    };
    public static final FloatProperty<RecentsView> FULLSCREEN_PROGRESS = new FloatProperty<RecentsView>("fullscreenProgress") { // from class: com.android.quickstep.views.RecentsView.4
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(RecentsView recentsView, float v) {
            recentsView.setFullscreenProgress(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mFullscreenProgress);
        }
    };
    public static final FloatProperty<RecentsView> TASK_MODALNESS = new FloatProperty<RecentsView>("taskModalness") { // from class: com.android.quickstep.views.RecentsView.5
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(RecentsView recentsView, float v) {
            recentsView.setTaskModalness(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mTaskModalness);
        }
    };
    public static final FloatProperty<RecentsView> ADJACENT_PAGE_OFFSET = new FloatProperty<RecentsView>("adjacentPageOffset") { // from class: com.android.quickstep.views.RecentsView.6
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(RecentsView recentsView, float v) {
            if (recentsView.mAdjacentPageOffset != v) {
                recentsView.mAdjacentPageOffset = v;
                recentsView.updatePageOffsets();
            }
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mAdjacentPageOffset);
        }
    };
    public static final Property<View, Float> DIM_ALPHA = new FloatProperty<View>("DimAlpha") { // from class: com.android.quickstep.views.RecentsView.10
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            ((TaskView) object).setDimAlpha(value);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(((TaskView) object).getDimAlpha());
        }
    };
    public static final Property<View, Float> CORNER_RADIUS_MORPH = new FloatProperty<View>("CornerRadiusMorph") { // from class: com.android.quickstep.views.RecentsView.11
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(View object, float value) {
            ((TaskView) object).getThumbnail().setCornerRadius(value);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(View object) {
            return Float.valueOf(((TaskView) object).getThumbnail().getCornerRadius());
        }
    };
    public static final FloatProperty<RecentsView> RECOMMAND_APP_ALPHA = new FloatProperty<RecentsView>("alpha") { // from class: com.android.quickstep.views.RecentsView.12
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(RecentsView view, float visibilityAlpha) {
            RecommandAppLayout recommandLayout;
            if (!LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue() || (recommandLayout = view.getRecommandLayout()) == null) {
                return;
            }
            if (!view.isSupportRecommendedLayout() || view.isOverviewSelectState()) {
                visibilityAlpha = 0.0f;
            }
            recommandLayout.setAlpha(visibilityAlpha);
            if (visibilityAlpha == 0.0f) {
                recommandLayout.setVisibility(4);
            } else {
                recommandLayout.setVisibility(0);
            }
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(RecentsView view) {
            return Float.valueOf(view.getRecommandLayout().getAlpha());
        }
    };

    public interface OnEmptyMessageUpdatedListener {
        void onEmptyMessageUpdated(boolean isEmpty);
    }

    public interface PageCallbacks {
        default void onPageScroll(ScrollState scrollState) {
        }
    }

    private void animateActionsViewIn() {
    }

    @Override // com.android.launcher3.PagedView
    public String getCurrentPageDescription() {
        return "";
    }

    protected DepthController getDepthController() {
        return null;
    }

    protected TaskView getHomeTaskView() {
        return null;
    }

    public TransformParams getLiveTileParams(boolean mightNeedToRefill) {
        return null;
    }

    public boolean hasRecentsExtraCard() {
        return false;
    }

    public void init(OverviewActionsView actionsView) {
    }

    public abstract boolean isFastOverView();

    @Override // com.android.launcher3.PagedView
    protected boolean isPageOrderFlipped() {
        return true;
    }

    public void onDigitalWellbeingToastShown() {
    }

    protected void onTaskLaunchAnimationUpdate(float progress, TaskView tv) {
    }

    public void onTaskLaunched(Task task) {
    }

    public void redrawLiveTile(boolean mightNeedToRefill) {
    }

    public void setDisallowScrollToClearAll(boolean disallowScrollToClearAll) {
    }

    public void setModalStateEnabled(boolean isModalState) {
    }

    protected boolean shouldStealTouchFromSiblingsBelow(MotionEvent ev) {
        return true;
    }

    public abstract boolean shouldUseMultiWindowTaskSizeStrategy();

    public boolean showAsGrid() {
        return false;
    }

    public abstract void startHome();

    /* JADX INFO: renamed from: com.android.quickstep.views.RecentsView$7, reason: invalid class name */
    class AnonymousClass7 implements TaskStackChangeListener {
        AnonymousClass7() {
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onActivityPinned(String packageName, int userId, int taskId, int stackId) {
            TaskView taskView;
            if (RecentsView.this.mHandleTaskStackChanges && TaskUtils.checkCurrentOrManagedUserId(userId, RecentsView.this.getContext()) && (taskView = RecentsView.this.getTaskView(taskId)) != null) {
                RecentsView.this.removeView(taskView);
            }
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onActivityUnpinned() {
            if (RecentsView.this.mHandleTaskStackChanges) {
                RecentsView.this.reloadIfNeeded();
                RecentsView.this.enableLayoutTransitions();
            }
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskRemoved(final int taskId) {
            if (RecentsView.this.mHandleTaskStackChanges) {
                Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$7$dPN27SoM0bHCKwAeYoeih6Dt9hQ
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onTaskRemoved$3$RecentsView$7(taskId);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onTaskRemoved$3$RecentsView$7(int i) {
            final Handler handler;
            final TaskView taskView = RecentsView.this.getTaskView(i);
            if (taskView == null || (handler = taskView.getHandler()) == null) {
                return;
            }
            Task.TaskKey taskKey = taskView.getTask().key;
            if (PackageManagerWrapper.getInstance().getActivityInfo(taskKey.getComponent(), taskKey.userId) == null) {
                handler.post(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$7$Jc3L85em6g80y5WM82EReqlxOu4
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onTaskRemoved$0$RecentsView$7(taskView);
                    }
                });
            } else {
                RecentsView.this.mModel.findTaskWithId(taskKey.id, new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$7$iKnuZuvFB2PUMfFJXDH4WUL_Yf4
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        this.f$0.lambda$onTaskRemoved$2$RecentsView$7(handler, taskView, (Task.TaskKey) obj);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onTaskRemoved$0$RecentsView$7(TaskView taskView) {
            RecentsView.this.dismissTask(taskView, true, false);
        }

        public /* synthetic */ void lambda$onTaskRemoved$1$RecentsView$7(TaskView taskView) {
            RecentsView.this.dismissTask(taskView, true, false);
        }

        public /* synthetic */ void lambda$onTaskRemoved$2$RecentsView$7(Handler handler, final TaskView taskView, Task.TaskKey taskKey) {
            if (taskKey == null) {
                handler.post(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$7$riAOoS7AkCNdw32BXNibe-vsXxM
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onTaskRemoved$1$RecentsView$7(taskView);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$new$0$RecentsView(boolean z) {
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext().getApplicationContext()).callOnTaskStackChanged();
        RecentsOrientedState recentsOrientedState = this.mOrientationState;
        if (recentsOrientedState != null) {
            recentsOrientedState.setMultiWindowMode(z);
            setLayoutRotation(this.mOrientationState.getTouchRotation(), this.mOrientationState.getDisplayRotation());
            rotateAllChildTasks();
        }
        if (z || !this.mOverviewStateEnabled) {
            return;
        }
        reloadIfNeeded();
    }

    public RecentsView(Context context, AttributeSet attributeSet, int i, BaseActivityInterface baseActivityInterface) {
        super(context, attributeSet, i);
        this.mEnableDrawingLiveTile = false;
        this.mLastComputedTaskSize = new Rect();
        this.mLastComputedGridSize = new Rect();
        this.mLastComputedGridTaskSize = new Rect();
        this.mLastComputedTaskStartPushOutDistance = null;
        this.mLastComputedTaskEndPushOutDistance = null;
        this.mTempRect = new Rect();
        this.mTempPointF = new PointF();
        this.mTempRectF = new RectF();
        this.mTempMatrix = new Matrix();
        boolean z = true;
        this.mTempFloat = new float[1];
        this.mClearAllButtonDeadZoneRect = new Rect();
        this.mTaskViewDeadZoneRect = new Rect();
        this.mScrollState = new ScrollState();
        this.mHasVisibleTaskData = new SparseBooleanArray();
        this.mOverviewGridEnabled = false;
        this.mAdjacentPageOffset = 0.0f;
        this.mTaskViewsSecondaryTranslation = 0.0f;
        this.mTaskViewsPrimarySplitTranslation = 0.0f;
        this.mTaskViewsSecondarySplitTranslation = 0.0f;
        this.mIsDragging = false;
        this.MAX_SCROLL_PAGE_GAP = 250;
        this.mUseOverScrollEffect = false;
        this.mIdOfTaskList = -1;
        this.mTaskStackListener = new AnonymousClass7();
        this.mIPipAnimationListener = new PinnedStackAnimationListener();
        this.mTaskListChangeId = -1;
        this.mRunningTaskId = -1;
        this.INVALID_TASK_IDS = new int[]{-1, -1};
        this.mRunningTaskIconScaledDown = false;
        this.mContentAlpha = 1.0f;
        this.mFullscreenProgress = 0.0f;
        this.mTaskModalness = 0.0f;
        this.mIgnoreResetTaskId = -1;
        this.mLastMeasureSize = new Point();
        this.mSplitUnsupportedToast = Toast.makeText(getContext(), R.string.cannot_open_in_multi_or_popup_window, 0);
        this.mSplitHiddenTaskViewIndex = -1;
        this.mTaskViewStartIndex = 0;
        this.mMultiWindowModeChangedListener = new BaseActivity.MultiWindowModeChangedListener() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$m3bIDMGFiEmq8xPpP6M4MW1CmHY
            @Override // com.android.launcher3.BaseActivity.MultiWindowModeChangedListener
            public final void onMultiWindowModeChanged(boolean z2) {
                this.f$0.lambda$new$0$RecentsView(z2);
            }
        };
        this.mClipBound = new Rect();
        this.mIsAnimRunning = false;
        this.mDisallowScrollToClearAll = true;
        if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
            setPageSpacing(getResources().getDimensionPixelSize(R.dimen.recents_page_ux_9_21_spacing));
        } else {
            setPageSpacing(getResources().getDimensionPixelSize(R.dimen.recents_page_spacing));
        }
        setEnableFreeScroll(true);
        this.mSizeStrategy = baseActivityInterface;
        T t = (T) BaseActivity.fromContext(context);
        this.mActivity = t;
        baseActivityInterface.setDisplayId(t.getDisplayId());
        this.mOrientationState = new RecentsOrientedState(context, baseActivityInterface, new IntConsumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$Quz4nOpG5u_McWaOdzIzYK7NdiE
            @Override // java.util.function.IntConsumer
            public final void accept(int i2) {
                this.f$0.animateRecentsRotationInPlace(i2);
            }
        });
        boolean z2 = (t == null || t.getDeviceProfile() == null) ? false : t.getDeviceProfile().isMultiWindowMode;
        this.mOrientationState.setMultiWindowMode(z2);
        LGLog.d(TAG, "RecentsView : isMultiWindowMode = " + z2 + ", mOrientationState = " + this.mOrientationState + ", " + this);
        this.mOrientationState.setActivityConfiguration(context.getResources().getConfiguration());
        this.mFastFlingVelocity = (float) getResources().getDimensionPixelSize(R.dimen.recents_fast_fling_velocity);
        this.mModel = RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(context);
        this.mIdp = InvariantDeviceProfile.INSTANCE.lambda$get$0$MainThreadInitializedObject(context);
        this.mTaskViewPool = new ViewPool<>(context, this, R.layout.task, 20, 10);
        if (!this.mOrientationHandler.getRecentsRtlSetting(getResources()) && !LGHomeFeature.Config.FEATURE_USE_RTL_DIRECTION_ON_RECENT_VIEW.getValue()) {
            z = false;
        }
        this.mIsRtl = z;
        setLayoutDirection(this.mIsRtl ? 1 : 0);
        this.mSplitPlaceholderSize = getResources().getDimensionPixelSize(R.dimen.split_placeholder_size);
        this.mSplitPlaceholderInset = getResources().getDimensionPixelSize(R.dimen.split_placeholder_inset);
        this.mTaskTopMargin = getResources().getDimensionPixelSize(R.dimen.task_thumbnail_top_margin);
        this.mSquaredTouchSlop = Utilities.squaredTouchSlop(context);
        Drawable drawable = context.getDrawable(R.drawable.ic_empty_recents);
        this.mEmptyIcon = drawable;
        drawable.setCallback(this);
        this.mEmptyMessage = context.getText(R.string.recents_empty_message);
        TextPaint textPaint = new TextPaint();
        this.mEmptyMessagePaint = textPaint;
        textPaint.setColor(Themes.getAttrColor(context, android.R.attr.textColorPrimary));
        textPaint.setTextSize(getResources().getDimension(R.dimen.recents_empty_message_text_size));
        this.mEmptyMessagePadding = getResources().getDimensionPixelSize(R.dimen.recents_empty_message_text_padding);
        setWillNotDraw(false);
        updateEmptyMessage();
        this.mOrientationHandler = this.mOrientationState.getOrientationHandler();
        t.getViewCache().setCacheSize(R.layout.digital_wellbeing_toast, 5);
        this.mRecommandLayoutHeight = LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue() ? getResources().getDimensionPixelSize(R.dimen.recommand_app_layout_height) : 0;
        this.mClearAllButtonHeight = getResources().getDimensionPixelSize(R.dimen.recent_clear_all_height);
        this.mClearAllButtonWidth = getResources().getDimensionPixelSize(R.dimen.recent_clear_all_width);
    }

    public OverScroller getScroller() {
        return this.mScroller;
    }

    public boolean isRtl() {
        return this.mIsRtl;
    }

    @Override // com.android.quickstep.RecentsModel.TaskVisualsChangeListener
    public Task onTaskThumbnailChanged(int taskId, ThumbnailData thumbnailData) {
        TaskView taskView;
        if (!this.mHandleTaskStackChanges || (taskView = getTaskView(taskId)) == null) {
            return null;
        }
        Task task = taskView.getTask();
        taskView.getThumbnail().setThumbnail(task, thumbnailData);
        return task;
    }

    @Override // com.android.quickstep.RecentsModel.TaskVisualsChangeListener
    public void onTaskIconChanged(String pkg, UserHandle user) {
        for (int i = 0; i < getTaskViewCount(); i++) {
            Task task = getTaskViewAt(i).getTask();
            if (task != null && task.key != null && pkg.equals(task.key.getPackageName()) && task.key.userId == user.getIdentifier()) {
                task.icon = null;
            }
        }
    }

    public TaskView updateThumbnail(int taskId, ThumbnailData thumbnailData, boolean refreshNow) {
        TaskView taskView = getTaskView(taskId);
        if (taskView != null) {
            taskView.getThumbnail().setThumbnail(taskView.getTask(), thumbnailData, refreshNow);
        }
        return taskView;
    }

    public TaskView updateThumbnail(int taskId, ThumbnailData thumbnailData) {
        return updateThumbnail(taskId, thumbnailData, true);
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        updateTaskStackListenerState();
    }

    @Override // com.android.launcher3.InvariantDeviceProfile.OnIDPChangeListener
    public void onIdpChanged(int changeFlags, InvariantDeviceProfile idp) {
        LGLog.i(TAG, String.format("[DEVICE_PROFILE] onIdpChanged : flag = %s, %s", Integer.valueOf(changeFlags), getClass().getSimpleName()));
        if (((this instanceof LauncherRecentsView) && (changeFlags & 4) != 0) || ((this instanceof FallbackRecentsView) && (changeFlags & 8) != 0)) {
            getTaskSize(new Rect());
            resetPaddingFromTaskSize();
        }
        if ((changeFlags & 2) == 0) {
            return;
        }
        this.mModel.getIconCache().clear();
        unloadVisibleTaskData();
        loadVisibleTaskData();
    }

    public void init(OverviewActionsView actionsView, SplitSelectStateController splitController) {
        this.mSplitSelectStateController = splitController;
    }

    public SplitSelectStateController getSplitPlaceholder() {
        return this.mSplitSelectStateController;
    }

    public boolean isSplitSelectionActive() {
        SplitSelectStateController splitSelectStateController = this.mSplitSelectStateController;
        return splitSelectStateController != null && splitSelectStateController.isSplitSelectActive();
    }

    public boolean isOverviewSelectState() {
        return this.mActivity.isInState(LauncherState.OVERVIEW_SPLIT_SELECT);
    }

    public void addSideTaskLaunchCallback(final RunnableList callback) {
        if (this.mSideTaskLaunchCallback == null) {
            this.mSideTaskLaunchCallback = new RunnableList();
        }
        RunnableList runnableList = this.mSideTaskLaunchCallback;
        Objects.requireNonNull(callback);
        runnableList.add(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$hQD1JeGZDm5kxmTgspSnjaDiUIY
            @Override // java.lang.Runnable
            public final void run() {
                callback.executeAllAndDestroy();
            }
        });
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateTaskStackListenerState();
        this.mModel.getThumbnailCache().getHighResLoadingState().addCallback(this);
        this.mActivity.addMultiWindowModeChangedListener(this.mMultiWindowModeChangedListener);
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        this.mSyncTransactionApplier = new SurfaceTransactionApplier(this);
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).addThumbnailChangeListener(this);
        this.mIdp.addOnChangeListener(this);
        this.mIPipAnimationListener.setActivityAndRecentsView(this.mActivity, this);
        SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).setPinnedStackAnimationListener(this.mIPipAnimationListener);
        this.mOrientationState.initListeners();
        SplitScreenBounds.INSTANCE.addOnChangeListener(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        RecentGuideView recentGuideView = RecentGuideView.getRecentGuideView();
        if (recentGuideView != null) {
            recentGuideView.handleClose(false);
        }
        updateTaskStackListenerState();
        this.mModel.getThumbnailCache().getHighResLoadingState().removeCallback(this);
        this.mActivity.removeMultiWindowModeChangedListener(this.mMultiWindowModeChangedListener);
        ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mTaskStackListener);
        this.mSyncTransactionApplier = null;
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).removeThumbnailChangeListener(this);
        this.mIdp.removeOnChangeListener(this);
        SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).setPinnedStackAnimationListener(null);
        SplitScreenBounds.INSTANCE.removeOnChangeListener(this);
        this.mIPipAnimationListener.setActivityAndRecentsView(null, null);
        this.mOrientationState.destroyListeners();
    }

    @Override // com.android.launcher3.PagedView, android.view.ViewGroup
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        if (child instanceof TaskView) {
            TaskView taskView = (TaskView) child;
            this.mHasVisibleTaskData.delete(taskView.getTask().key.id);
            this.mTaskViewPool.recycle(taskView);
            taskView.setTaskViewId(-1);
        }
        onChildViewsChanged();
        updateTaskStartIndex(child);
        updateEmptyMessage();
    }

    @Override // com.android.launcher3.PagedView, android.view.ViewGroup
    public void onViewAdded(View view) {
        super.onViewAdded(view);
        view.setAlpha(this.mContentAlpha);
        view.setLayoutDirection(!this.mIsRtl ? 1 : 0);
        updateTaskStartIndex(view);
        onChildViewsChanged();
        updateEmptyMessage();
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        maybeDrawEmptyMessage(canvas);
        super.draw(canvas);
    }

    private void updateTaskStartIndex(View affectingView) {
        if ((affectingView instanceof TaskView) || (affectingView instanceof ClearAllButton)) {
            return;
        }
        int childCount = getChildCount();
        this.mTaskViewStartIndex = 0;
        while (true) {
            int i = this.mTaskViewStartIndex;
            if (i >= childCount || (getChildAt(i) instanceof TaskView)) {
                return;
            } else {
                this.mTaskViewStartIndex++;
            }
        }
    }

    public boolean isTaskViewVisible(TaskView tv) {
        return Math.abs(indexOfChild(tv) - getNextPage()) <= 1;
    }

    public TaskView getTaskView(int taskId) {
        for (int i = 0; i < getTaskViewCount(); i++) {
            TaskView taskViewAt = getTaskViewAt(i);
            if (taskViewAt.getTask() != null && taskViewAt.getTask().key != null && taskViewAt.getTask().key.id == taskId) {
                return taskViewAt;
            }
        }
        return null;
    }

    public void setOverviewStateEnabled(boolean enabled) {
        this.mOverviewStateEnabled = enabled;
        if ((this instanceof LauncherRecentsView) && enabled) {
            WindowUtils.minimizeAllFreeforms();
        }
        updateTaskStackListenerState();
        this.mOrientationState.setRotationWatcherEnabled(!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && enabled);
        if (enabled) {
            return;
        }
        this.mTmpRunningTask = null;
    }

    public boolean isClearAllHidden() {
        return this.mClearAllButton.getAlpha() != 1.0f;
    }

    @Override // com.android.launcher3.PagedView
    protected void onPageBeginTransition() {
        super.onPageBeginTransition();
    }

    @Override // com.android.launcher3.PagedView
    protected void onPageEndTransition() {
        super.onPageEndTransition();
        isClearAllHidden();
        if (getNextPage() > 0) {
            setSwipeDownShouldLaunchApp(true);
        }
        setEnabledButtonsOfHeaderView(false);
    }

    @Override // com.android.launcher3.PagedView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int action = ev.getAction();
        if (action == 0) {
            if (!isHandlingTouch() && !isModal()) {
                if (this.mShowEmptyMessage) {
                    this.mTouchDownToStartHome = true;
                } else {
                    updateDeadZoneRects();
                    boolean z = this.mClearAllButton.getAlpha() == 1.0f && this.mClearAllButtonDeadZoneRect.contains(x, y);
                    boolean z2 = (ev.getEdgeFlags() & 256) != 0;
                    if (!z && !z2 && !this.mTaskViewDeadZoneRect.contains(getScrollX() + x, y)) {
                        this.mTouchDownToStartHome = true;
                    }
                }
            }
            this.mDownX = x;
            this.mDownY = y;
        } else if (action == 1) {
            if (this.mTouchDownToStartHome && !this.mIsDragging) {
                LGLog.d(TAG, "[RecentsAnimation] onTouchEvent: call startHome");
                AbstractFloatingView.closeOpenViews(this.mActivity, false, 2560);
                if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && LGHomeFeature.Config.FEATURE_USE_SIMPLE_TRANSITION_OF_LANDSCAPE.getValue() && (this.mActivity instanceof Launcher) && getResources().getInteger(R.integer.config_simple_transition_landscape) == 2 && getResources().getConfiguration().orientation == 2) {
                    ((Launcher) this.mActivity).getRotationHelper().setCurrentStateRequest(0);
                }
                startHome();
            }
            this.mTouchDownToStartHome = false;
        } else if (action != 2) {
            if (action == 3) {
                this.mTouchDownToStartHome = false;
                setEnableFreeScroll(getScaleX() == 1.0f);
            }
        } else if (this.mTouchDownToStartHome && (isHandlingTouch() || Utilities.squaredHypot(this.mDownX - x, this.mDownY - y) > this.mSquaredTouchSlop)) {
            this.mTouchDownToStartHome = false;
        }
        return isHandlingTouch() || shouldStealTouchFromSiblingsBelow(ev);
    }

    @Override // com.android.launcher3.PagedView
    protected void determineScrollingStart(MotionEvent ev, float touchSlopScale) {
        if (isModal()) {
            return;
        }
        if (!this.mActivity.isInState(LauncherState.BACKGROUND_APP) || !this.mActivity.getDeviceProfile().isMultiWindowMode) {
            super.determineScrollingStart(ev, touchSlopScale);
        }
        if (this.mIsBeingDragged) {
            AbstractFloatingView.closeOpenViews(this.mActivity, false, 2560);
        }
    }

    protected void applyLoadPlan(final ArrayList<Task> tasks) {
        PendingAnimation pendingAnimation = this.mPendingAnimation;
        if (pendingAnimation != null) {
            pendingAnimation.addEndListener(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$fEG27sHuPQ5_hwRbC4mDaauNOGc
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$applyLoadPlan$1$RecentsView(tasks, (PendingAnimation.EndState) obj);
                }
            });
            return;
        }
        if (tasks == null || tasks.isEmpty()) {
            removeTasksViewsAndClearAllButton();
            onTaskStackUpdated();
            return;
        }
        boolean z = this.mIdOfTaskList != tasks.hashCode();
        LGLog.i(TAG, "[RECENT_TASK] applyLoadPlan : " + tasks.size() + ", changed = " + z);
        this.mIdOfTaskList = tasks.hashCode();
        unloadVisibleTaskData();
        int i = this.mIgnoreResetTaskId;
        TaskView taskView = i == -1 ? null : getTaskView(i);
        int size = tasks.size();
        if (getTaskViewCount() != size) {
            for (int taskViewCount = getTaskViewCount(); taskViewCount < size; taskViewCount++) {
                addView(this.mTaskViewPool.getView());
            }
            while (getTaskViewCount() > size) {
                removeView(getChildAt(getChildCount() - 1));
            }
        }
        for (int i2 = size - 1; i2 >= 0; i2--) {
            ((TaskView) getChildAt(((size - i2) - 1) + this.mTaskViewStartIndex)).bind(tasks.get(i2), this.mOrientationState);
        }
        if (this.mNextPage == -1) {
            TaskView runningTaskView = getRunningTaskView();
            if (runningTaskView != null) {
                setCurrentPage(indexOfChild(runningTaskView));
            } else if (getTaskViewCount() > 0) {
                setCurrentPage(indexOfChild(getTaskViewAt(0)));
            }
        }
        int i3 = this.mIgnoreResetTaskId;
        if (i3 != -1 && getTaskView(i3) != taskView) {
            this.mIgnoreResetTaskId = -1;
        }
        resetTaskVisuals();
        onTaskStackUpdated();
        updateEnabledOverlays();
        updateClearAllEnabled();
        setTaskViewsPrimarySplitTranslation(this.mTaskViewsPrimarySplitTranslation);
        setTaskViewsSecondarySplitTranslation(this.mTaskViewsSecondarySplitTranslation);
    }

    public /* synthetic */ void lambda$applyLoadPlan$1$RecentsView(ArrayList arrayList, PendingAnimation.EndState endState) {
        applyLoadPlan(arrayList);
    }

    private boolean isModal() {
        return this.mTaskModalness > 0.0f;
    }

    private void removeTasksViewsAndClearAllButton() {
        for (int taskViewCount = getTaskViewCount() - 1; taskViewCount >= 0; taskViewCount--) {
            removeView(getTaskViewAt(taskViewCount));
        }
        if (indexOfChild(this.mClearAllButton) != -1) {
            removeView(this.mClearAllButton);
        }
    }

    public int getTaskViewCount() {
        int childCount = getChildCount() - this.mTaskViewStartIndex;
        if (childCount == 0) {
            return 0;
        }
        return childCount;
    }

    protected void onTaskStackUpdated() {
        updateEmptyMessage();
        updateClearAllEnabled();
        if (LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue()) {
            updateStackLayout();
        }
    }

    public void resetTaskVisuals() {
        for (int taskViewCount = getTaskViewCount() - 1; taskViewCount >= 0; taskViewCount--) {
            TaskView taskViewAt = getTaskViewAt(taskViewCount);
            if (this.mIgnoreResetTaskId != taskViewAt.getTask().key.id) {
                taskViewAt.resetViewTransforms();
                taskViewAt.setStableAlpha(this.mContentAlpha);
                taskViewAt.setFullscreenProgress(this.mFullscreenProgress);
                taskViewAt.setModalness(this.mTaskModalness);
            }
        }
        rotateAllChildTasks();
        boolean z = this.mRunningTaskTileHidden;
        if (z) {
            setRunningTaskHidden(z);
        }
        if (this.mIgnoreResetTaskId != this.mRunningTaskId) {
            applyRunningTaskIconScale();
        }
        if (!LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue()) {
            updateCurveProperties();
        } else {
            updateStackProperties();
        }
        loadVisibleTaskData();
        setTaskModalness(0.0f);
        if (this.mOverviewStateEnabled) {
            setEnabledButtonsOfHeaderView(true);
        }
    }

    public void setFullscreenProgress(float fullscreenProgress) {
        this.mFullscreenProgress = fullscreenProgress;
        int taskViewCount = getTaskViewCount();
        for (int i = 0; i < taskViewCount; i++) {
            getTaskViewAt(i).setFullscreenProgress(this.mFullscreenProgress);
        }
    }

    private void updateTaskStackListenerState() {
        boolean z = this.mOverviewStateEnabled && isAttachedToWindow() && getWindowVisibility() == 0;
        if (z != this.mHandleTaskStackChanges) {
            this.mHandleTaskStackChanges = z;
            if (z) {
                reloadIfNeeded();
            }
        }
    }

    @Override // com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        this.mInsets.set(insets);
        resetPaddingFromTaskSize();
    }

    private void resetPaddingFromTaskSize() {
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        getTaskSize(this.mTempRect);
        this.mTaskWidth = this.mTempRect.width();
        this.mTaskHeight = this.mTempRect.height();
        this.mTempRect.top -= this.mTaskTopMargin;
        int dimension = (int) getResources().getDimension(R.dimen.task_thumbnail_top_margin);
        int i = 0;
        if (deviceProfile.isLandscape) {
            dimension = 0;
        } else {
            int i2 = this.mOrientationHandler.getDegreesRotated() == 270.0f ? dimension : 0;
            if (this.mOrientationHandler.getDegreesRotated() != 90.0f) {
                dimension = 0;
            }
            i = i2;
        }
        setPadding((this.mTempRect.left - this.mInsets.left) - i, this.mTempRect.top - this.mInsets.top, ((deviceProfile.widthPx - this.mInsets.right) - this.mTempRect.right) - dimension, (deviceProfile.heightPx - this.mInsets.bottom) - this.mTempRect.bottom);
        LGLog.d(TAG, "[DEVICE_PROFILE] resetPaddingFromTaskSize : isLand = " + deviceProfile.isLandscape + ", size = " + this.mTempRect + ", padding(" + getPaddingLeft() + ", " + getPaddingTop() + ", " + getPaddingRight() + ", " + getPaddingBottom() + "), dp.widthPx = " + deviceProfile.widthPx + ", dp.heightPx = " + deviceProfile.heightPx + ", mInsets = " + this.mInsets + ", dp.insets" + deviceProfile.getInsets() + ", " + getHeight() + ", " + this.mTempRect.height());
        RECOMMAND_APP_ALPHA.setValue(this, isSupportRecommendedLayout() ? 1.0f : 0.0f);
        setLocationOfClearAllButton();
        RecommandAppLayout recommandAppLayout = this.mRecommandLayout;
        if (recommandAppLayout != null) {
            setCustomClipBound(recommandAppLayout.getAlpha(), "resetPaddingFromTaskSize");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSupportRecommendedLayout() {
        return !(this.mActivity.getDisplayId() == 4) && (this.mActivity.getStateManager().getState().hasRecommand() && getRecommandLayout() != null && LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue()) && !this.mActivity.getDeviceProfile().isLandscape && LGHomeFeature.isAppSuggestionEnabled();
    }

    public void getTaskSize(Rect outRect) {
        BaseActivityInterface baseActivityInterface = this.mSizeStrategy;
        T t = this.mActivity;
        baseActivityInterface.calculateTaskSize(t, t.getDeviceProfile(), outRect, this.mOrientationHandler);
    }

    public void getModalTaskSize(Rect outRect) {
        BaseActivityInterface baseActivityInterface = this.mSizeStrategy;
        T t = this.mActivity;
        baseActivityInterface.calculateModalTaskSize(t, t.getDeviceProfile(), outRect);
    }

    @Override // com.android.launcher3.PagedView
    protected boolean computeScrollHelper() {
        boolean zComputeScrollHelper = super.computeScrollHelper();
        boolean z = false;
        if (LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue()) {
            updateStackProperties();
        } else {
            updateCurveProperties(getScaleX() == 1.0f);
        }
        if (zComputeScrollHelper || isHandlingTouch()) {
            if (zComputeScrollHelper) {
                z = this.mScroller.getCurrVelocity() > this.mFastFlingVelocity;
            }
            loadVisibleTaskData();
        }
        this.mModel.getThumbnailCache().getHighResLoadingState().setFlingingFast(z);
        return zComputeScrollHelper;
    }

    public void updateCurveProperties() {
        updateCurveProperties(false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v3, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void updateCurveProperties(boolean useAnim) {
        double d;
        double d2;
        float fAbs;
        if (getPageCount() == 0 || getPageAt(0).getMeasuredWidth() == 0) {
            return;
        }
        int scrollX = getScaleX() == 1.0f ? getScrollX() - this.mPreScrollX : getScrollX();
        this.mOrientationHandler.getCurveProperties(this, this.mInsets, this.mScrollState);
        this.mScrollState.scrollFromEdge = this.mIsRtl ? this.mScrollState.scroll : this.mMaxScroll - this.mScrollState.scroll;
        int pageCount = getPageCount();
        for (int i = 0; i < pageCount; i++) {
            View pageAt = getPageAt(i);
            this.mScrollState.updateInterpolation(this.mOrientationHandler.getChildStartWithTranslation(pageAt), this.mPageSpacing);
            if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
                useAnim = false;
            }
            if (useAnim) {
                float f = 0.0f;
                if (getScrollX() < this.mMinScroll && (isHandlingTouch() || this.mUseOverScrollEffect)) {
                    if (i == getChildCount() - 2) {
                        pageAt.setTranslationX(-getScrollX());
                        this.mUseOverScrollEffect = true;
                    } else {
                        pageAt.setTranslationX(0.0f);
                    }
                } else if (getScrollX() <= this.mMaxScroll || !(isHandlingTouch() || this.mUseOverScrollEffect)) {
                    if (isHandlingTouch()) {
                        if (this.mScrollState.linearInterpolation > 0.0f) {
                            d = this.mScrollState.linearInterpolation;
                            d2 = scrollX;
                        } else {
                            d = this.mScrollState.linearInterpolation;
                            d2 = -scrollX;
                        }
                        fAbs = (float) (d * d2 * 1.2d);
                    } else if (this.mPreVelocity == ((int) this.mScroller.getCurrVelocity()) || Math.abs(this.mScroller.getFinalPos() - this.mScroller.getStartPos()) <= this.mScrollState.halfPageSize) {
                        fAbs = 0.0f;
                    } else {
                        fAbs = (-this.mScrollState.linearInterpolation) * Math.abs(scrollX) * Math.abs(this.mScroller.getCurrVelocity() / 700.0f);
                        float progress = Utilities.getProgress(this.mScroller.getCurrPos(), this.mScroller.getStartPos(), this.mScroller.getFinalPos());
                        if (0.0f <= progress && progress <= 1.0f) {
                            fAbs *= 1.0f - Interpolators.ACCEL_7.getInterpolation(progress);
                        }
                    }
                    if (Math.abs(fAbs) > 250.0f) {
                        fAbs = fAbs < 0.0f ? -250 : 250;
                    }
                    if (getScrollX() <= this.mMaxScroll && getScrollX() >= 0) {
                        f = fAbs;
                    }
                    pageAt.setTranslationX(f);
                    this.mUseOverScrollEffect = false;
                } else if (i == 1) {
                    pageAt.setTranslationX(this.mMaxScroll - getScrollX());
                    this.mUseOverScrollEffect = true;
                } else {
                    pageAt.setTranslationX(0.0f);
                }
            }
            ((PageCallbacks) pageAt).onPageScroll(this.mScrollState);
        }
        this.mPreScrollX = getScrollX();
        this.mPreVelocity = (int) this.mScroller.getCurrVelocity();
    }

    public void loadVisibleTaskData() {
        if ((!this.mOverviewStateEnabled || this.mTaskListChangeId == -1) && (getParent() instanceof View) && ((View) getParent()).getVisibility() != 0) {
            return;
        }
        int pageNearestToCenterOfScreen = getPageNearestToCenterOfScreen();
        int childCount = getChildCount();
        int iMax = Math.max(0, pageNearestToCenterOfScreen - 2);
        int i = childCount - 1;
        int iMin = Math.min(pageNearestToCenterOfScreen + 2, i);
        if (LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue()) {
            iMax = Math.max(0, pageNearestToCenterOfScreen - 3);
            iMin = Math.min(pageNearestToCenterOfScreen + 3, i);
        }
        for (int i2 = 0; i2 < getTaskViewCount(); i2++) {
            TaskView taskViewAt = getTaskViewAt(i2);
            Task task = taskViewAt.getTask();
            int iIndexOfChild = indexOfChild(taskViewAt);
            boolean z = iMax <= iIndexOfChild && iIndexOfChild <= iMin;
            if (z) {
                if (task != this.mTmpRunningTask) {
                    if (!this.mHasVisibleTaskData.get(task.key.id)) {
                        taskViewAt.onTaskListVisibilityChanged(true);
                    }
                    this.mHasVisibleTaskData.put(task.key.id, z);
                }
            } else {
                if (this.mHasVisibleTaskData.get(task.key.id)) {
                    taskViewAt.onTaskListVisibilityChanged(false);
                }
                this.mHasVisibleTaskData.delete(task.key.id);
            }
        }
    }

    private void unloadVisibleTaskData() {
        TaskView taskView;
        for (int i = 0; i < this.mHasVisibleTaskData.size(); i++) {
            if (this.mHasVisibleTaskData.valueAt(i) && (taskView = getTaskView(this.mHasVisibleTaskData.keyAt(i))) != null) {
                taskView.onTaskListVisibilityChanged(false);
            }
        }
        this.mHasVisibleTaskData.clear();
    }

    @Override // com.android.quickstep.TaskThumbnailCache.HighResLoadingState.HighResLoadingStateChangedCallback
    public void onHighResLoadingStateChanged(boolean enabled) {
        TaskView taskView;
        for (int i = 0; i < this.mHasVisibleTaskData.size(); i++) {
            if (this.mHasVisibleTaskData.valueAt(i) && (taskView = getTaskView(this.mHasVisibleTaskData.keyAt(i))) != null) {
                taskView.onTaskListVisibilityChanged(true);
            }
        }
    }

    public void reset() {
        setCurrentTask(-1);
        this.mIgnoreResetTaskId = -1;
        this.mTaskListChangeId = -1;
        this.mRecentsAnimationController = null;
        this.mRecentsAnimationTargets = null;
        this.mOverviewEnterAnimationWorking = false;
        unloadVisibleTaskData();
        setCurrentPage(0);
        this.mDwbToastShown = false;
        this.mActivity.getSystemUiController().updateUiState(4, 0);
        resetFromSplitSelectionState();
        this.mSplitSelectStateController.resetState();
        if (this.mOrientationState.setGestureActive(false)) {
            updateOrientationHandler();
        }
    }

    public TaskView getRunningTaskView() {
        return getTaskView(this.mRunningTaskId);
    }

    public int getRunningTaskIndex() {
        return getTaskIndexForId(this.mRunningTaskId);
    }

    public boolean isTaskInExpectedScrollPosition(int taskIndex) {
        return getScrollForPage(taskIndex) == getPagedOrientationHandler().getPrimaryScroll(this);
    }

    private boolean isFocusedTaskInExpectedScrollPosition() {
        TaskView currentPageTaskView = getCurrentPageTaskView();
        return currentPageTaskView != null && isTaskInExpectedScrollPosition(indexOfChild(currentPageTaskView));
    }

    /* JADX WARN: Incorrect return type in method signature: <T:Lcom/android/quickstep/views/TaskView;>(Z)TT; */
    private TaskView getTaskViewFromPool(boolean isGrouped) {
        TaskView taskView = (TaskView) this.mTaskViewPool.getView();
        taskView.setTaskViewId(this.mTaskViewIdCount);
        int i = this.mTaskViewIdCount;
        if (i == Integer.MAX_VALUE) {
            this.mTaskViewIdCount = 0;
        } else {
            this.mTaskViewIdCount = i + 1;
        }
        return taskView;
    }

    public int getTaskIndexForId(int taskId) {
        TaskView taskView = getTaskView(taskId);
        if (taskView == null) {
            return -1;
        }
        return indexOfChild(taskView);
    }

    public int getTaskViewStartIndex() {
        return this.mTaskViewStartIndex;
    }

    public void reloadIfNeeded(boolean forceUpdateItems) {
        if (!this.mModel.isTaskListValid(this.mTaskListChangeId)) {
            this.mTaskListChangeId = this.mModel.getTasks(new $$Lambda$eFmuyMfVOc7HK7w69eJovKdvgRA(this));
        }
        updateClearAllEnabled();
        updateHeaderItemsVisibility(5, !isFastOverView());
        RecommandAppLayout recommandAppLayout = this.mRecommandLayout;
        if (recommandAppLayout != null) {
            recommandAppLayout.updateItems(forceUpdateItems);
        }
    }

    public void onGestureAnimationStart(int runningTaskId) {
        if (this.mOrientationState.setGestureActive(true)) {
            updateOrientationHandler();
        }
        showCurrentTask(runningTaskId);
        setEnableFreeScroll(false);
        setEnableDrawingLiveTile(false);
        setRunningTaskHidden(true);
        setRunningTaskIconScaledDown(true);
    }

    public void onSwipeUpAnimationSuccess() {
        if (getRunningTaskView() != null) {
            animateUpRunningTaskIconScale((FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && this.mLiveTileOverlayAttached) ? LiveTileOverlay.INSTANCE.cancelIconAnimation() : 0.0f);
        }
        setSwipeDownShouldLaunchApp(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateRecentsRotationInPlace(final int newRotation) {
        if (newRotation == this.mOrientationState.getOrientationHandler().getRotation()) {
            LGLog.d(TAG, "animateRecentsRotationInPlace : skip. canRecentsActivityRotate = " + this.mOrientationState.canRecentsActivityRotate() + ", isLandscape = " + this.mActivity.getDeviceProfile().isLandscape);
            return;
        }
        AnimatorSet recentsChangedOrientation = setRecentsChangedOrientation(true);
        recentsChangedOrientation.addListener(AnimationSuccessListener.forRunnable(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$p-UPrIBvGzb25dUWUFnbBK39l-Y
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$animateRecentsRotationInPlace$2$RecentsView(newRotation);
            }
        }));
        recentsChangedOrientation.start();
    }

    public /* synthetic */ void lambda$animateRecentsRotationInPlace$2$RecentsView(int i) {
        setLayoutRotation(i, this.mOrientationState.getDisplayRotation());
        this.mActivity.getDragLayer().recreateControllers();
        rotateAllChildTasks();
        setRecentsChangedOrientation(false).start();
    }

    public AnimatorSet setRecentsChangedOrientation(boolean fadeInChildren) {
        getRunningTaskIndex();
        int currentPage = getCurrentPage();
        AnimatorSet animatorSet = new AnimatorSet();
        int i = 0;
        while (true) {
            if (i >= getTaskViewCount()) {
                break;
            }
            TaskView taskViewAt = getTaskViewAt(i);
            if (taskViewAt instanceof TaskView) {
                TaskView taskView = taskViewAt;
                if (taskView.getHeaderView() != null && (currentPage == i || (!fadeInChildren && taskView.getHeaderView().getAlpha() < 1.0f))) {
                    View headerView = taskView.getHeaderView();
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = fadeInChildren ? 0.0f : 1.0f;
                    animatorSet.play(ObjectAnimator.ofFloat(headerView, (Property<View, Float>) property, fArr));
                }
            }
            if (!fadeInChildren || currentPage != i) {
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = fadeInChildren ? 0.0f : 1.0f;
                animatorSet.play(ObjectAnimator.ofFloat(taskViewAt, (Property<TaskView, Float>) property2, fArr2));
            }
            i++;
        }
        if (this.mClearAllButton != null) {
            boolean z = fadeInChildren || getChildCount() == 0;
            ClearAllButton clearAllButton = this.mClearAllButton;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 0.0f : 1.0f;
            animatorSet.play(ObjectAnimator.ofFloat(clearAllButton, (Property<ClearAllButton, Float>) property3, fArr3));
        }
        return animatorSet;
    }

    private void rotateAllChildTasks() {
        for (int i = 0; i < getTaskViewCount(); i++) {
            getTaskViewAt(i).setOrientationState(this.mOrientationState);
        }
        if (RecentGuideView.getRecentGuideView() != null) {
            RecentGuideView.rotateGuideView(this.mOrientationState);
        }
        if (this.mRecommandLayout != null) {
            this.mRecommandLayout.updateRotation((!this.mShowEmptyMessage || this.mEmptyTextLayout == null) ? this.mOrientationState.getOrientationHandler().getDegreesRotated() : 0.0f, true);
        }
    }

    public void onGestureAnimationEnd() {
        if (this.mOrientationState.setGestureActive(false)) {
            updateOrientationHandler();
        }
        setOnScrollChangeListener(null);
        setEnableFreeScroll(true);
        setEnableDrawingLiveTile(true);
        if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            setRunningTaskViewShowScreenshot(true);
        }
        setRunningTaskHidden(false);
        animateUpRunningTaskIconScale();
        animateActionsViewIn();
        if (this instanceof LauncherRecentsView) {
            removeExcludeApp();
        }
    }

    protected boolean shouldAddDummyTaskView(int runningTaskId) {
        return getTaskView(runningTaskId) == null;
    }

    public void showCurrentTask(int runningTaskId) {
        boolean z = this.mRunningTaskTileHidden;
        setCurrentTask(runningTaskId);
        setCurrentPage(getRunningTaskIndex());
        setRunningTaskViewShowScreenshot(false);
        setRunningTaskHidden(z);
        this.mTaskListChangeId = this.mModel.getTasks(new $$Lambda$eFmuyMfVOc7HK7w69eJovKdvgRA(this));
    }

    public void setCurrentTask(int runningTaskId) {
        int i = this.mRunningTaskId;
        if (i == runningTaskId) {
            return;
        }
        if (i != -1) {
            setRunningTaskIconScaledDown(false);
            setRunningTaskViewShowScreenshot(true);
            setRunningTaskHidden(false);
        }
        this.mRunningTaskId = runningTaskId;
    }

    public void setRunningTaskHidden(boolean isHidden) {
        this.mRunningTaskTileHidden = isHidden;
        TaskView runningTaskView = getRunningTaskView();
        if (runningTaskView != null) {
            runningTaskView.setStableAlpha(isHidden ? 0.0f : this.mContentAlpha);
            if (isHidden) {
                return;
            }
            AccessibilityManagerCompat.sendCustomAccessibilityEvent(runningTaskView, 8, null);
        }
    }

    private void setRunningTaskViewShowScreenshot(boolean showScreenshot) {
        TaskView runningTaskView;
        if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || (runningTaskView = getRunningTaskView()) == null) {
            return;
        }
        runningTaskView.setShowScreenshot(showScreenshot);
    }

    public void showNextTask() {
        TaskView runningTaskView = getRunningTaskView();
        if (runningTaskView == null) {
            if (getTaskViewCount() > 0) {
                getTaskViewAt(0).launchTaskAnimated(true);
            }
        } else if (getNextTaskView() != null) {
            getNextTaskView().launchTaskAnimated(true);
        } else {
            runningTaskView.launchTaskAnimated(true);
        }
    }

    public void setRunningTaskIconScaledDown(boolean isScaledDown) {
        if (this.mRunningTaskIconScaledDown != isScaledDown) {
            this.mRunningTaskIconScaledDown = isScaledDown;
            applyRunningTaskIconScale();
        }
    }

    public boolean isTaskIconScaledDown(TaskView taskView) {
        return this.mRunningTaskIconScaledDown && getRunningTaskView() == taskView;
    }

    private void applyRunningTaskIconScale() {
        TaskView runningTaskView = getRunningTaskView();
        if (runningTaskView != null) {
            runningTaskView.setIconScaleAndDim(this.mRunningTaskIconScaledDown ? 0.0f : 1.0f);
        }
    }

    public void animateUpRunningTaskIconScale() {
        animateUpRunningTaskIconScale(0.0f);
    }

    public void animateUpRunningTaskIconScale(float startProgress) {
        this.mRunningTaskIconScaledDown = false;
        TaskView runningTaskView = getRunningTaskView();
        if (runningTaskView != null) {
            runningTaskView.animateIconScaleAndDimIntoView();
            runningTaskView.setIconScaleAnimStartProgress(startProgress);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableLayoutTransitions() {
        if (this.mLayoutTransition == null) {
            LayoutTransition layoutTransition = new LayoutTransition();
            this.mLayoutTransition = layoutTransition;
            layoutTransition.enableTransitionType(2);
            this.mLayoutTransition.setDuration(200L);
            this.mLayoutTransition.setStartDelay(2, 0L);
            this.mLayoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() { // from class: com.android.quickstep.views.RecentsView.8
                @Override // android.animation.LayoutTransition.TransitionListener
                public void startTransition(LayoutTransition transition, ViewGroup viewGroup, View view, int i) {
                }

                @Override // android.animation.LayoutTransition.TransitionListener
                public void endTransition(LayoutTransition transition, ViewGroup viewGroup, View view, int i) {
                    if (view instanceof TaskView) {
                        RecentsView.this.snapToPage(0);
                        RecentsView.this.disableLayoutTransitions();
                    }
                }
            });
        }
        setLayoutTransition(this.mLayoutTransition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disableLayoutTransitions() {
        setLayoutTransition(null);
    }

    public void setSwipeDownShouldLaunchApp(boolean swipeDownShouldLaunchApp) {
        this.mSwipeDownShouldLaunchApp = swipeDownShouldLaunchApp;
    }

    public boolean shouldSwipeDownLaunchApp() {
        return this.mSwipeDownShouldLaunchApp;
    }

    public static class ScrollState extends PagedOrientationHandler.CurveProperties {
        public float leftOffset;
        public float linearInterpolation;
        public float scrollFromEdge;

        public void updateInterpolation(float childStart, int pageSpacing) {
            float f = this.screenCenter - (childStart + this.halfPageSize);
            float f2 = this.halfScreenSize + this.halfPageSize + pageSpacing;
            if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
                this.linearInterpolation = Math.abs(f) / f2;
            } else {
                this.linearInterpolation = Math.min(1.0f, Math.abs(f) / f2);
            }
        }
    }

    public void setIgnoreResetTask(int taskId) {
        this.mIgnoreResetTaskId = taskId;
    }

    public void clearIgnoreResetTask(int taskId) {
        if (this.mIgnoreResetTaskId == taskId) {
            this.mIgnoreResetTaskId = -1;
        }
    }

    private void addDismissedTaskAnimations(View taskView, long duration, PendingAnimation anim) {
        anim.setFloat(taskView, LauncherAnimUtils.VIEW_ALPHA, 0.0f, Interpolators.ACCEL_2);
        FloatProperty<View> secondaryViewTranslate = this.mOrientationHandler.getSecondaryViewTranslate();
        int secondaryDimension = this.mOrientationHandler.getSecondaryDimension(taskView);
        int taskDismissDirectionFactor = this.mOrientationHandler.getTaskDismissDirectionFactor();
        ResourceProvider resourceProviderProvider = DynamicResource.provider(this.mActivity);
        anim.add(ObjectAnimator.ofFloat(taskView, secondaryViewTranslate, taskDismissDirectionFactor * secondaryDimension).setDuration(duration), Interpolators.LINEAR, new SpringProperty(0).setDampingRatio(resourceProviderProvider.getFloat(R.dimen.dismiss_task_trans_y_damping_ratio)).setStiffness(resourceProviderProvider.getFloat(R.dimen.dismiss_task_trans_y_stiffness)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeTask(TaskView taskView, int index, PendingAnimation.EndState endState) {
        if (taskView.getTask() != null) {
            if (ActivityManagerWrapperEx.getInstance().skipRemoveTask(taskView.getTask())) {
                LGLog.i(TAG, "skip removeTask because the task is restricted. " + taskView.getTask().topActivity);
                return;
            }
            ActivityManagerWrapper.getInstance().removeTask(taskView.getTask().key.id);
            this.mActivity.getUserEventDispatcher().logTaskLaunchOrDismiss(endState.logAction, 1, index, TaskUtils.getLaunchComponentKeyForTask(taskView.getTask().key));
            this.mActivity.getStatsLogManager().logger().withItemInfo(taskView.getItemInfo()).log(StatsLogManager.LauncherEvent.LAUNCHER_TASK_DISMISS_SWIPE_UP);
        }
    }

    private void createInitialSplitSelectAnimation(PendingAnimation anim) {
        LGLog.d(TAG, "createInitialSplitSelectAnimation");
        this.mOrientationHandler.getInitialSplitPlaceholderBounds(this.mSplitPlaceholderSize, this.mSplitPlaceholderInset, this.mActivity.getDeviceProfile(), this.mSplitSelectStateController.getActiveSplitStagePosition(), this.mTempRect);
        RectF rectF = new RectF();
        TaskView taskView = this.mSplitHiddenTaskView;
        if (taskView != null) {
            taskView.setVisibility(4);
            FloatingTaskView floatingTaskView = FloatingTaskView.getFloatingTaskView(this.mActivity, this.mSplitHiddenTaskView.getThumbnail(), this.mSplitHiddenTaskView.getThumbnail().getThumbnail(), this.mSplitHiddenTaskView.getIconViewDrawable(), rectF);
            this.mFirstFloatingTaskView = floatingTaskView;
            floatingTaskView.setAlpha(1.0f);
            this.mFirstFloatingTaskView.addAnimation(anim, rectF, this.mTempRect, true, true);
        } else {
            QuickstepSystemShortcut.SplitSelectSource splitSelectSource = this.mSplitSelectSource;
            if (splitSelectSource != null) {
                FloatingTaskView floatingTaskView2 = FloatingTaskView.getFloatingTaskView(this.mActivity, splitSelectSource.view, null, splitSelectSource.drawable, rectF);
                this.mFirstFloatingTaskView = floatingTaskView2;
                floatingTaskView2.setAlpha(1.0f);
                this.mFirstFloatingTaskView.addAnimation(anim, rectF, this.mTempRect, false, true);
            }
        }
        InteractionJankMonitorWrapper.begin(this, 49, "First tile selected");
        anim.addEndListener(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$O9mTil9chZtxt6HZ5llLZuMsw1E
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RecentsView.lambda$createInitialSplitSelectAnimation$3((PendingAnimation.EndState) obj);
            }
        });
    }

    static /* synthetic */ void lambda$createInitialSplitSelectAnimation$3(PendingAnimation.EndState endState) {
        if (endState.isSuccess) {
            InteractionJankMonitorWrapper.end(49);
        } else {
            InteractionJankMonitorWrapper.cancel(49);
        }
    }

    public void onFinishInitSplitAnimation() {
        LGLog.d(TAG, "onFinishInitSplitAnimation");
        PendingAnimation pendingAnimation = this.mPendingAnimation;
        if (pendingAnimation != null) {
            pendingAnimation.finish(true, 3);
            InteractionJankMonitorWrapper.end(49);
        }
        SplitSelectStateController splitSelectStateController = this.mSplitSelectStateController;
        if ((splitSelectStateController != null && !splitSelectStateController.isSplitSelectActive()) || this.mSplitHiddenTaskViewIndex == -1) {
            resetFromSplitSelectionState();
        } else {
            setTaskViewsPrimarySplitTranslation(this.mTaskViewsPrimarySplitTranslation);
            setTaskViewsSecondarySplitTranslation(this.mTaskViewsSecondarySplitTranslation);
        }
    }

    public PendingAnimation createTaskDismissAnimation(TaskView taskView, boolean animateTaskView, boolean shouldRemoveTask, long duration) {
        return createTaskDismissAnimation(taskView, animateTaskView, shouldRemoveTask, duration, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x0084  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0086  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.android.launcher3.anim.PendingAnimation createTaskDismissAnimation(final com.android.quickstep.views.TaskView r20, boolean r21, boolean r22, long r23, boolean r25) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r23
            com.android.launcher3.anim.PendingAnimation r4 = r0.mPendingAnimation
            r5 = 0
            if (r4 == 0) goto Lf
            r6 = 3
            r4.finish(r5, r6)
        Lf:
            com.android.launcher3.anim.PendingAnimation r4 = new com.android.launcher3.anim.PendingAnimation
            r4.<init>(r2)
            int r6 = r19.getPageCount()
            if (r6 != 0) goto L1b
            return r4
        L1b:
            int[] r7 = new int[r6]
            int[] r8 = new int[r6]
            com.android.launcher3.PagedView$ComputePageScrollsLogic r9 = com.android.quickstep.views.RecentsView.SIMPLE_SCROLL_LOGIC
            r0.getPageScrolls(r7, r5, r9)
            com.android.quickstep.views.-$$Lambda$RecentsView$nZYWeik_4_Lnh0A3zkrKFPvrQL4 r9 = new com.android.quickstep.views.-$$Lambda$RecentsView$nZYWeik_4_Lnh0A3zkrKFPvrQL4
            r9.<init>()
            r0.getPageScrolls(r8, r5, r9)
            int r9 = r19.getTaskViewCount()
            r10 = 1
            if (r6 <= r10) goto L3d
            r11 = r7[r10]
            r12 = r7[r5]
            int r11 = r11 - r12
            int r11 = java.lang.Math.abs(r11)
            goto L3e
        L3d:
            r11 = r5
        L3e:
            int r12 = r19.indexOfChild(r20)
            r13 = r5
            r14 = r13
        L44:
            if (r13 >= r6) goto Le0
            android.view.View r15 = r0.getChildAt(r13)
            if (r15 != r1) goto L62
            if (r21 == 0) goto L57
            if (r25 == 0) goto L54
            r0.createInitialSplitSelectAnimation(r4)
            goto L57
        L54:
            r0.addDismissedTaskAnimations(r1, r2, r4)
        L57:
            r15.getX()
            r16 = r5
            r17 = r6
            r18 = r7
            goto Ld6
        L62:
            boolean r5 = r0.mIsRtl
            r17 = r6
            if (r5 == 0) goto L6a
            r5 = r11
            goto L6b
        L6a:
            r5 = 0
        L6b:
            int r6 = r0.mCurrentPage
            if (r6 != r12) goto L7a
            int r6 = r9 + (-1)
            int r10 = r0.mCurrentPage
            if (r10 != r6) goto L88
            boolean r6 = r0.mIsRtl
            if (r6 == 0) goto L86
            goto L84
        L7a:
            int r6 = r0.mCurrentPage
            r10 = 1
            int r6 = r6 - r10
            if (r12 != r6) goto L88
            boolean r6 = r0.mIsRtl
            if (r6 == 0) goto L86
        L84:
            int r6 = -r11
            goto L87
        L86:
            r6 = r11
        L87:
            int r5 = r5 + r6
        L88:
            r6 = r8[r13]
            r10 = r7[r13]
            int r6 = r6 - r10
            int r6 = r6 + r5
            if (r6 == 0) goto Ld1
            com.android.launcher3.touch.PagedOrientationHandler r5 = r0.mOrientationHandler
            android.util.FloatProperty r5 = r5.getPrimaryViewTranslate()
            T extends com.android.launcher3.statemanager.StatefulActivity r10 = r0.mActivity
            com.android.systemui.plugins.ResourceProvider r10 = com.android.launcher3.util.DynamicResource.provider(r10)
            com.android.launcher3.anim.SpringProperty r14 = new com.android.launcher3.anim.SpringProperty
            r18 = r7
            r7 = 1
            r14.<init>(r7)
            r7 = 2131165625(0x7f0701b9, float:1.7945472E38)
            float r7 = r10.getFloat(r7)
            com.android.launcher3.anim.SpringProperty r7 = r14.setDampingRatio(r7)
            r14 = 2131165626(0x7f0701ba, float:1.7945474E38)
            float r10 = r10.getFloat(r14)
            com.android.launcher3.anim.SpringProperty r7 = r7.setStiffness(r10)
            r10 = 1
            float[] r14 = new float[r10]
            float r6 = (float) r6
            r16 = 0
            r14[r16] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r15, r5, r14)
            android.animation.ObjectAnimator r5 = r5.setDuration(r2)
            android.view.animation.Interpolator r6 = com.android.launcher3.anim.Interpolators.ACCEL
            r4.add(r5, r6, r7)
            r14 = r10
            goto Ld6
        Ld1:
            r18 = r7
            r10 = 1
            r16 = 0
        Ld6:
            int r13 = r13 + 1
            r5 = r16
            r6 = r17
            r7 = r18
            goto L44
        Le0:
            if (r14 == 0) goto Lfb
            com.lge.launcher3.util.LGHomeFeature$Config r2 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI
            boolean r2 = r2.getValue()
            if (r2 != 0) goto Lf3
            com.android.quickstep.views.-$$Lambda$CvnoLYYyGI-oRJ631yBnR0gTTn0 r2 = new com.android.quickstep.views.-$$Lambda$CvnoLYYyGI-oRJ631yBnR0gTTn0
            r2.<init>()
            r4.addOnFrameCallback(r2)
            goto Lfb
        Lf3:
            com.android.quickstep.views.-$$Lambda$Eq2excPmoUXyQjtX2OKMmtIlK8Y r2 = new com.android.quickstep.views.-$$Lambda$Eq2excPmoUXyQjtX2OKMmtIlK8Y
            r2.<init>()
            r4.addOnFrameCallback(r2)
        Lfb:
            com.lge.launcher3.util.LGHomeFeature$Config r2 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI
            boolean r2 = r2.getValue()
            if (r2 != 0) goto L10b
            if (r21 == 0) goto L10b
            r2 = 1036831949(0x3dcccccd, float:0.1)
            r1.setTranslationZ(r2)
        L10b:
            r0.mPendingAnimation = r4
            com.android.quickstep.views.RecentsView$9 r2 = new com.android.quickstep.views.RecentsView$9
            r3 = r22
            r2.<init>(r1, r3, r12)
            r4.addEndListener(r2)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.views.RecentsView.createTaskDismissAnimation(com.android.quickstep.views.TaskView, boolean, boolean, long, boolean):com.android.launcher3.anim.PendingAnimation");
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: ?: TERNARY null = (((wrap:int:0x0000: INVOKE (r3v0 android.view.View) VIRTUAL call: android.view.View.getVisibility():int A[MD:():int (c), WRAPPED] (LINE:2017)) == (8 int) || (r3v0 android.view.View) == (r2v0 com.android.quickstep.views.TaskView))) ? false : true */
    static /* synthetic */ boolean lambda$createTaskDismissAnimation$4(TaskView taskView, View view) {
        return (view.getVisibility() == 8 || view == taskView) ? false : true;
    }

    /* JADX INFO: renamed from: com.android.quickstep.views.RecentsView$9, reason: invalid class name */
    class AnonymousClass9 implements Consumer<PendingAnimation.EndState> {
        final /* synthetic */ int val$draggedIndex;
        final /* synthetic */ boolean val$shouldRemoveTask;
        final /* synthetic */ TaskView val$taskView;

        AnonymousClass9(final TaskView val$taskView, final boolean val$shouldRemoveTask, final int val$draggedIndex) {
            this.val$taskView = val$taskView;
            this.val$shouldRemoveTask = val$shouldRemoveTask;
            this.val$draggedIndex = val$draggedIndex;
        }

        /* JADX DEBUG: Method merged with bridge method: accept(Ljava/lang/Object;)V */
        @Override // java.util.function.Consumer
        public void accept(final PendingAnimation.EndState endState) {
            if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && this.val$taskView.isRunningTask() && endState.isSuccess) {
                RecentsView.this.finishRecentsAnimation(true, new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$9$-_c9lXhWHvXKhXoZW5ncSSaJqjg
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$accept$0$RecentsView$9(endState);
                    }
                });
            } else {
                lambda$accept$0$RecentsView$9(endState);
            }
        }

        /* JADX DEBUG: Method merged with bridge method: lambda$accept$0$RecentsView$9(Lcom/android/launcher3/anim/PendingAnimation$EndState;)V */
        /* JADX INFO: Access modifiers changed from: private */
        /* JADX INFO: renamed from: onEnd, reason: merged with bridge method [inline-methods] */
        public void lambda$accept$0$RecentsView$9(PendingAnimation.EndState endState) {
            if (endState.isSuccess) {
                if (this.val$shouldRemoveTask) {
                    RecentsView.this.removeTask(this.val$taskView, this.val$draggedIndex, endState);
                }
                int i = RecentsView.this.mCurrentPage;
                if (this.val$draggedIndex < i || i == RecentsView.this.getTaskViewCount() - 1) {
                    i--;
                }
                RecentsView.this.removeViewInLayout(this.val$taskView);
                RecentsView.this.updateClearAllEnabled();
                if (RecentsView.this.getTaskViewCount() == 0) {
                    RecentsView recentsView = RecentsView.this;
                    recentsView.removeViewInLayout(recentsView.mClearAllButton);
                    if (!RecentsView.this.mActivity.isInMultiWindowMode()) {
                        RecentsView.this.startHome();
                    }
                } else {
                    RecentsView.this.snapToPageImmediately(i);
                }
                RecentsView recentsView2 = RecentsView.this;
                recentsView2.onLayout(false, recentsView2.getLeft(), RecentsView.this.getTop(), RecentsView.this.getRight(), RecentsView.this.getBottom());
                AbstractFloatingView.closeOpenViews(RecentsView.this.mActivity, false, 2560);
            }
            RecentsView.this.resetTaskVisuals();
            RecentsView.this.mPendingAnimation = null;
        }
    }

    public boolean shouldShiftThumbnailsForSplitSelect() {
        return (this.mActivity.getDeviceProfile().isTablet && this.mActivity.getDeviceProfile().isLandscape) ? false : true;
    }

    protected void onDismissAnimationEnds() {
        AccessibilityManagerCompat.sendDismissAnimationEndsEventToTest(getContext());
    }

    public PendingAnimation createAllTasksDismissAnimation(long duration) {
        PendingAnimation pendingAnimation = new PendingAnimation(duration);
        final int taskViewCount = getTaskViewCount();
        for (int i = 0; i < taskViewCount; i++) {
            if (!getTaskViewAt(i).isPinned()) {
                addDismissedTaskAnimations(getChildAt(i), duration, pendingAnimation);
            }
        }
        this.mPendingAnimation = pendingAnimation;
        pendingAnimation.addEndListener(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$DCWmq7TBOGQvdQH1jwcoaFllydg
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$createAllTasksDismissAnimation$5$RecentsView(taskViewCount, (PendingAnimation.EndState) obj);
            }
        });
        return pendingAnimation;
    }

    public /* synthetic */ void lambda$createAllTasksDismissAnimation$5$RecentsView(int i, PendingAnimation.EndState endState) {
        if (endState.isSuccess) {
            ArrayList arrayList = new ArrayList();
            boolean z = true;
            for (int i2 = 0; i2 < i; i2++) {
                TaskView taskViewAt = getTaskViewAt(i2);
                if (taskViewAt == null || taskViewAt.isPinned()) {
                    z = false;
                } else {
                    LGLog.i(TAG, "[RECENT_TASK] createAllTasksDismissAnimation " + i2 + ", " + taskViewAt.getTask().key.id + ", " + taskViewAt.getTask().getTopComponent());
                    arrayList.add(taskViewAt);
                }
            }
            ActivityManagerWrapper.getInstance().removeAllRecentTasks();
            LGLog.i(TAG, "[RECENT_TASK] createAllTasksDismissAnimation : removeView size = " + arrayList.size());
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                removeView((View) it.next());
            }
            if (z) {
                startHome();
            }
            updateClearAllEnabled();
        }
        this.mPendingAnimation = null;
    }

    private boolean snapToPageRelative(int pageCount, int delta, boolean cycle) {
        if (pageCount == 0) {
            return false;
        }
        int nextPage = getNextPage() + delta;
        if (!cycle && (nextPage < 0 || nextPage >= pageCount)) {
            return false;
        }
        snapToPage((nextPage + pageCount) % pageCount);
        getChildAt(getNextPage()).requestFocus();
        return true;
    }

    protected void runDismissAnimation(final PendingAnimation pendingAnim) {
        AnimatorPlaybackController animatorPlaybackControllerCreatePlaybackController = pendingAnim.createPlaybackController();
        animatorPlaybackControllerCreatePlaybackController.dispatchOnStart();
        animatorPlaybackControllerCreatePlaybackController.setEndAction(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$JyVfuG1RofqQ8V7LkR7tgDcWScw
            @Override // java.lang.Runnable
            public final void run() {
                pendingAnim.finish(true, 3);
            }
        });
        animatorPlaybackControllerCreatePlaybackController.getAnimationPlayer().setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        animatorPlaybackControllerCreatePlaybackController.start();
    }

    public void dismissTask(TaskView taskView, boolean animateTaskView, boolean removeTask) {
        runDismissAnimation(createTaskDismissAnimation(taskView, animateTaskView, removeTask, 300L));
    }

    public void dismissAllTasks(View view) {
        runDismissAnimation(createAllTasksDismissAnimation(300L));
        this.mActivity.getUserEventDispatcher().logActionOnControl(0, 13);
    }

    private void dismissCurrentTask() {
        TaskView nextPageTaskView = getNextPageTaskView();
        if (nextPageTaskView != null) {
            dismissTask(nextPageTaskView, true, true);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            int keyCode = event.getKeyCode();
            if (keyCode == 21) {
                return snapToPageRelative(getPageCount(), this.mIsRtl ? 1 : -1, false);
            }
            if (keyCode == 22) {
                return snapToPageRelative(getPageCount(), this.mIsRtl ? -1 : 1, false);
            }
            if (keyCode == 61) {
                return snapToPageRelative(getTaskViewCount(), event.isShiftPressed() ? -1 : 1, event.isAltPressed());
            }
            if (keyCode == 67 || keyCode == 112) {
                dismissCurrentTask();
                return true;
            }
            if (keyCode == 158 && event.isAltPressed()) {
                dismissCurrentTask();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (!gainFocus || getChildCount() <= 0) {
            return;
        }
        if (direction != 1) {
            if (direction == 2) {
                setCurrentPage(0);
                return;
            } else if (direction != 17 && direction != 66) {
                return;
            }
        }
        setCurrentPage(getChildCount() - 1);
    }

    public float getContentAlpha() {
        return this.mContentAlpha;
    }

    public void setContentAlpha(float alpha) {
        if (alpha == this.mContentAlpha) {
            return;
        }
        float fBoundToRange = Utilities.boundToRange(alpha, 0.0f, 1.0f);
        this.mContentAlpha = fBoundToRange;
        for (int taskViewCount = getTaskViewCount() - 1; taskViewCount >= 0; taskViewCount--) {
            TaskView taskViewAt = getTaskViewAt(taskViewCount);
            if (!this.mRunningTaskTileHidden || taskViewAt.getTask().key.id != this.mRunningTaskId) {
                if (taskViewAt.getTask().key.id != this.mIgnoreResetTaskId) {
                    taskViewAt.setStableAlpha(fBoundToRange);
                } else {
                    taskViewAt.setStableAlpha(fBoundToRange, true);
                    LGLog.d(TAG, "setContentAlpha: alpha = " + fBoundToRange + ", child.getTask() " + taskViewAt.getTask() + ", mRunningTaskTileHidden = " + this.mRunningTaskTileHidden);
                }
            }
        }
        int iRound = Math.round(255.0f * fBoundToRange);
        this.mEmptyMessagePaint.setAlpha(iRound);
        this.mEmptyIcon.setAlpha(iRound);
        if (fBoundToRange > 0.0f) {
            setVisibility(0);
            return;
        }
        if (this.mFreezeViewVisibility) {
            return;
        }
        setVisibility(8);
        ClearAllButton clearAllButton = this.mClearAllButton;
        if (clearAllButton != null) {
            clearAllButton.setVisibility(8);
        }
        if (this.mRecommandLayout != null) {
            RECOMMAND_APP_ALPHA.setValue(this, 0.0f);
        }
    }

    public void setFreezeViewVisibility(boolean freezeViewVisibility) {
        if (this.mFreezeViewVisibility != freezeViewVisibility) {
            this.mFreezeViewVisibility = freezeViewVisibility;
            if (freezeViewVisibility) {
                return;
            }
            setVisibility(this.mContentAlpha > 0.0f ? 0 : 8);
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        ClearAllButton clearAllButton = this.mClearAllButton;
        if (clearAllButton != null) {
            clearAllButton.updateHiddenFlags(32, visibility != 0);
        }
        if (this.mRecommandLayout != null && visibility != 0) {
            RECOMMAND_APP_ALPHA.setValue(this, 0.0f);
        }
        RecentGuideView recentGuideView = RecentGuideView.getRecentGuideView();
        if (recentGuideView != null && visibility != 0) {
            recentGuideView.handleClose(false);
        }
        this.mActivity.controlStatusBar();
        if (isOverviewSelectState()) {
            ClearAllButton clearAllButton2 = this.mClearAllButton;
            if (clearAllButton2 != null) {
                clearAllButton2.setVisibility(8);
            }
            if (this.mRecommandLayout != null) {
                RECOMMAND_APP_ALPHA.setValue(this, 0.0f);
                this.mRecommandLayout.setVisibility(8);
                return;
            }
            return;
        }
        ClearAllButton clearAllButton3 = this.mClearAllButton;
        if (clearAllButton3 != null) {
            clearAllButton3.setVisibility(visibility);
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LGLog.d(TAG, "onConfigurationChanged : " + newConfig);
        if (this.mOrientationState.setActivityConfiguration(newConfig)) {
            updateOrientationHandler();
        }
    }

    public void setLayoutRotation(int touchRotation, int displayRotation) {
        if (this.mOrientationState.update(touchRotation, displayRotation)) {
            updateOrientationHandler();
            return;
        }
        LGLog.d(TAG, "setLayoutRotation : skip. isLandScape = " + this.mActivity.getDeviceProfile().isLandscape + ", " + this.mOrientationHandler);
    }

    private void updateOrientationHandler() {
        this.mOrientationHandler = this.mOrientationState.getOrientationHandler();
        this.mIsRtl = this.mOrientationHandler.getRecentsRtlSetting(getResources()) || LGHomeFeature.Config.FEATURE_USE_RTL_DIRECTION_ON_RECENT_VIEW.getValue();
        setLayoutDirection(this.mIsRtl ? 1 : 0);
        this.mClearAllButton.setLayoutDirection(1 ^ (this.mIsRtl ? 1 : 0));
        this.mClearAllButton.setRotation(this.mOrientationHandler.getDegreesRotated());
        this.mActivity.getDragLayer().recreateControllers();
        onOrientationChanged();
        resetPaddingFromTaskSize();
        requestLayout();
        setCurrentPage(this.mCurrentPage);
    }

    private void onOrientationChanged() {
        setModalStateEnabled(false);
        if (isSplitSelectionActive()) {
            onRotateInSplitSelectionState();
        }
    }

    public RecentsOrientedState getPagedViewOrientedState() {
        return this.mOrientationState;
    }

    public PagedOrientationHandler getPagedOrientationHandler() {
        return this.mOrientationHandler;
    }

    public TaskView getNextTaskView() {
        return getTaskViewAtByAbsoluteIndex(getRunningTaskIndex() + 1);
    }

    public TaskView getCurrentPageTaskView() {
        return getTaskViewAtByAbsoluteIndex(getCurrentPage());
    }

    public TaskView getNextPageTaskView() {
        return getTaskViewAtByAbsoluteIndex(getNextPage());
    }

    public TaskView getTaskViewNearestToCenterOfScreen() {
        return getTaskViewAtByAbsoluteIndex(getPageNearestToCenterOfScreen());
    }

    public TaskView getTaskViewAt(int index) {
        return getTaskViewAtByAbsoluteIndex(index + this.mTaskViewStartIndex);
    }

    private TaskView getTaskViewAtByAbsoluteIndex(int index) {
        if (index >= getChildCount() || index < 0) {
            return null;
        }
        View childAt = getChildAt(index);
        if (childAt instanceof TaskView) {
            return (TaskView) childAt;
        }
        return null;
    }

    private TaskView requireTaskViewAt(int index) {
        return (TaskView) Objects.requireNonNull(getTaskViewAt(index));
    }

    public void setOnEmptyMessageUpdatedListener(OnEmptyMessageUpdatedListener listener) {
        this.mOnEmptyMessageUpdatedListener = listener;
    }

    public void updateEmptyMessage() {
        boolean z = true;
        boolean z2 = getTaskViewCount() == 0;
        if (this.mLastMeasureSize.x == getWidth() && this.mLastMeasureSize.y == getHeight()) {
            z = false;
        }
        RecommandAppLayout recommandAppLayout = this.mRecommandLayout;
        if (recommandAppLayout != null && z2) {
            recommandAppLayout.updateRotation(0.0f, false);
        }
        if (z2 != this.mShowEmptyMessage || z) {
            setContentDescription(z2 ? this.mEmptyMessage : "");
            this.mShowEmptyMessage = z2;
            updateEmptyStateUi(z);
            invalidate();
            OnEmptyMessageUpdatedListener onEmptyMessageUpdatedListener = this.mOnEmptyMessageUpdatedListener;
            if (onEmptyMessageUpdatedListener != null) {
                onEmptyMessageUpdatedListener.onEmptyMessageUpdated(this.mShowEmptyMessage);
            }
        }
    }

    @Override // com.android.launcher3.PagedView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateEmptyStateUi(changed);
        getTaskSize(this.mTempRect);
        getPagedViewOrientedState().getFullScreenScaleAndPivot(this.mTempRect, this.mActivity.getDeviceProfile(), this.mTempPointF);
        setPivotX(this.mTempPointF.x);
        setPivotY(this.mTempPointF.y);
        setTaskModalness(this.mTaskModalness);
        updatePageOffsets();
        setImportantForAccessibility(isModal() ? 2 : 0);
    }

    private void updatePivots() {
        if (this.mOverviewSelectEnabled) {
            setPivotX(this.mLastComputedTaskSize.centerX());
            setPivotY(this.mLastComputedTaskSize.bottom);
        } else {
            getPagedViewOrientedState().getFullScreenScaleAndPivot(this.mTempRect, this.mActivity.getDeviceProfile(), this.mTempPointF);
            setPivotX(this.mTempPointF.x);
            setPivotY(this.mTempPointF.y);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePageOffsets() {
        float width = this.mAdjacentPageOffset * getWidth();
        float interpolation = Interpolators.ACCEL_0_75.getInterpolation(this.mTaskModalness) * getWidth();
        if (this.mIsRtl) {
            width = -width;
            interpolation = -interpolation;
        }
        int childCount = getChildCount();
        int i = this.mRunningTaskId;
        TaskView taskView = (i == -1 || !this.mRunningTaskTileHidden) ? null : getTaskView(i);
        int iIndexOfChild = taskView != null ? indexOfChild(taskView) : -1;
        int currentPage = getCurrentPage();
        int i2 = 0;
        while (i2 < childCount) {
            float f = 0.0f;
            float f2 = i2 == iIndexOfChild ? 0.0f : i2 < iIndexOfChild ? -width : width;
            if (i2 != currentPage) {
                f = i2 < currentPage ? -interpolation : interpolation;
            }
            getChildAt(i2).setTranslationX(f2 + f);
            i2++;
        }
        updateCurveProperties();
    }

    public float getPageOffsetScale() {
        return Math.max(getWidth(), 1);
    }

    public void resetModalVisuals() {
        TaskView currentPageTaskView = getCurrentPageTaskView();
        if (currentPageTaskView != null) {
            currentPageTaskView.getThumbnail().getTaskOverlay().resetModalVisuals();
        }
    }

    protected void setTaskViewsPrimarySplitTranslation(float translation) {
        this.mTaskViewsPrimarySplitTranslation = translation;
        for (int i = 0; i < getTaskViewCount(); i++) {
            TaskView taskViewRequireTaskViewAt = requireTaskViewAt(i);
            taskViewRequireTaskViewAt.getPrimarySplitTranslationProperty().set(taskViewRequireTaskViewAt, Float.valueOf(translation));
        }
    }

    protected void setTaskViewsSecondarySplitTranslation(float translation) {
        this.mTaskViewsSecondarySplitTranslation = translation;
        for (int i = 0; i < getTaskViewCount(); i++) {
            TaskView taskViewRequireTaskViewAt = requireTaskViewAt(i);
            if (taskViewRequireTaskViewAt != this.mSplitHiddenTaskView) {
                taskViewRequireTaskViewAt.getSecondarySplitTranslationProperty().set(taskViewRequireTaskViewAt, Float.valueOf(translation));
            }
        }
    }

    public void applySplitPrimaryScrollOffset() {
        float f;
        float f2 = 0.0f;
        if (isSplitPlaceholderFirstInGrid()) {
            f2 = this.mIsRtl ? this.mSplitPlaceholderSize : -this.mSplitPlaceholderSize;
            f = 0.0f;
        } else if (isSplitPlaceholderLastInGrid()) {
            f = this.mIsRtl ? -this.mSplitPlaceholderSize : this.mSplitPlaceholderSize;
        } else {
            f = 0.0f;
        }
        for (int i = 0; i < getTaskViewCount(); i++) {
            requireTaskViewAt(i).setSplitScrollOffsetPrimary(f2);
        }
        this.mClearAllButton.setSplitSelectScrollOffsetPrimary(f);
    }

    private boolean isSplitPlaceholderFirstInGrid() {
        if (!this.mActivity.getDeviceProfile().isLandscape || !showAsGrid() || !isSplitSelectionActive()) {
            return false;
        }
        int activeSplitStagePosition = this.mSplitSelectStateController.getActiveSplitStagePosition();
        if (this.mIsRtl) {
            if (activeSplitStagePosition != 1) {
                return false;
            }
        } else if (activeSplitStagePosition != 0) {
            return false;
        }
        return true;
    }

    private boolean isSplitPlaceholderLastInGrid() {
        if (!this.mActivity.getDeviceProfile().isLandscape || !showAsGrid() || !isSplitSelectionActive()) {
            return false;
        }
        int activeSplitStagePosition = this.mSplitSelectStateController.getActiveSplitStagePosition();
        if (this.mIsRtl) {
            if (activeSplitStagePosition != 0) {
                return false;
            }
        } else if (activeSplitStagePosition != 1) {
            return false;
        }
        return true;
    }

    public void resetSplitPrimaryScrollOffset() {
        for (int i = 0; i < getTaskViewCount(); i++) {
            requireTaskViewAt(i).setSplitScrollOffsetPrimary(0.0f);
        }
        this.mClearAllButton.setSplitSelectScrollOffsetPrimary(0.0f);
    }

    public void initiateSplitSelect(TaskView taskView) {
        initiateSplitSelect(taskView, this.mOrientationHandler.getDefaultSplitPosition(this.mActivity.getDeviceProfile()));
    }

    public void initiateSplitSelect(TaskView taskView, int stagePosition) {
        LGLog.i(TAG, "initiateSplitSelect. initTaskId[" + taskView.getTask().key.id + "]");
        this.mSplitHiddenTaskView = taskView;
        this.mSplitSelectStateController.setInitialTaskSelect(taskView.getTask().key.id, stagePosition);
        this.mSplitHiddenTaskViewIndex = indexOfChild(taskView);
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            finishRecentsAnimation(true, null);
        }
    }

    public void initiateSplitSelect(QuickstepSystemShortcut.SplitSelectSource splitSelectSource) {
        this.mSplitSelectSource = splitSelectSource;
        this.mSplitSelectStateController.setInitialTaskSelect(splitSelectSource.intent, splitSelectSource.position.stagePosition);
    }

    public PendingAnimation createSplitSelectInitAnimation(int duration) {
        TaskView taskView = this.mSplitHiddenTaskView;
        if (taskView != null) {
            return createTaskDismissAnimation(taskView, true, false, duration, true);
        }
        PendingAnimation pendingAnimation = new PendingAnimation(duration);
        createInitialSplitSelectAnimation(pendingAnimation);
        return pendingAnimation;
    }

    public boolean confirmSplitSelect(TaskView containerTaskView, Task task, ImageView iconView, TaskThumbnailView thumbnailView) {
        if (!isSplitSelectionActive()) {
            resetFromSplitSelectionState();
            return false;
        }
        LGLog.i(TAG, "confirmSplitSelect");
        if (!task.isDockable) {
            this.mSplitUnsupportedToast.show();
            return true;
        }
        if (this.mSplitSelectStateController.isBothSplitAppsConfirmed() || this.mFirstFloatingTaskView == null) {
            return true;
        }
        this.mSplitSelectStateController.setSecondTask(task);
        RectF rectF = new RectF();
        Rect rect = new Rect();
        Rect rect2 = new Rect();
        Rect rect3 = this.mTempRect;
        final PendingAnimation pendingAnimation = new PendingAnimation(this.mActivity.getStateManager().getState().getTransitionDuration(this.mActivity));
        this.mOrientationHandler.getFinalSplitPlaceholderBounds(getResources().getDimensionPixelSize(R.dimen.multi_window_task_divider_size) / 2, this.mActivity.getDeviceProfile(), this.mSplitSelectStateController.getActiveSplitStagePosition(), rect3, rect);
        this.mFirstFloatingTaskView.getBoundsOnScreen(rect2);
        this.mFirstFloatingTaskView.addAnimation(pendingAnimation, new RectF(rect2), rect3, false, true);
        FloatingTaskView floatingTaskView = FloatingTaskView.getFloatingTaskView(this.mActivity, thumbnailView, thumbnailView.getThumbnail(), iconView.getDrawable(), rectF);
        this.mSecondFloatingTaskView = floatingTaskView;
        floatingTaskView.setAlpha(1.0f);
        this.mSecondFloatingTaskView.addAnimation(pendingAnimation, rectF, rect, true, false);
        pendingAnimation.addEndListener(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$gn1yLYtMBys1F17EGfT7SCPow94
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$confirmSplitSelect$8$RecentsView((PendingAnimation.EndState) obj);
            }
        });
        if (containerTaskView.containsMultipleTasks()) {
            this.mSecondSplitHiddenView = thumbnailView;
        } else {
            this.mSecondSplitHiddenView = containerTaskView;
        }
        this.mSecondSplitHiddenView.setVisibility(4);
        InteractionJankMonitorWrapper.begin(this, 49, "Second tile selected");
        AnimatorPlaybackController animatorPlaybackControllerCreatePlaybackController = pendingAnimation.createPlaybackController();
        animatorPlaybackControllerCreatePlaybackController.setEndAction(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$m3P8c3Cweci5DqVP3XPAHeRBtg8
            @Override // java.lang.Runnable
            public final void run() {
                pendingAnimation.finish(true, 0);
            }
        });
        animatorPlaybackControllerCreatePlaybackController.start();
        return true;
    }

    public /* synthetic */ void lambda$confirmSplitSelect$8$RecentsView(PendingAnimation.EndState endState) {
        this.mSplitSelectStateController.launchSplitTasks(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$C9YfetaZfx1Ot21k133gaTJY-lc
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$confirmSplitSelect$7$RecentsView((Boolean) obj);
            }
        });
        InteractionJankMonitorWrapper.end(49);
    }

    public /* synthetic */ void lambda$confirmSplitSelect$7$RecentsView(Boolean bool) {
        resetFromSplitSelectionState();
    }

    public void resetFromSplitSelectionState() {
        Log.i(TAG, "resetFromSplitSelectionState");
        if (this.mSplitSelectSource != null || this.mSplitHiddenTaskViewIndex != -1) {
            if (this.mFirstFloatingTaskView != null) {
                this.mActivity.getRootView().removeView(this.mFirstFloatingTaskView);
                this.mFirstFloatingTaskView = null;
            }
            if (this.mSecondFloatingTaskView != null) {
                this.mActivity.getRootView().removeView(this.mSecondFloatingTaskView);
                this.mSecondFloatingTaskView = null;
                this.mSecondSplitHiddenView.setVisibility(0);
                this.mSecondSplitHiddenView = null;
            }
            this.mSplitSelectSource = null;
        }
        SplitSelectStateController splitSelectStateController = this.mSplitSelectStateController;
        if (splitSelectStateController != null) {
            splitSelectStateController.resetState();
        }
        clearSplitHolderView();
        Pair<FloatProperty, FloatProperty> splitSelectTaskOffset = getPagedOrientationHandler().getSplitSelectTaskOffset(TASK_PRIMARY_SPLIT_TRANSLATION, TASK_SECONDARY_SPLIT_TRANSLATION, this.mActivity.getDeviceProfile());
        ((FloatProperty) splitSelectTaskOffset.first).set(this, Float.valueOf(0.0f));
        ((FloatProperty) splitSelectTaskOffset.second).set(this, Float.valueOf(0.0f));
        if (this.mSplitHiddenTaskViewIndex == -1) {
            return;
        }
        if (!this.mActivity.getDeviceProfile().isTablet) {
            int i = this.mCurrentPage;
            int i2 = this.mSplitHiddenTaskViewIndex;
            if (i2 <= i) {
                i2 = i + 1;
            }
            if (snapToPageImmediately(i2)) {
                this.mCurrentPage = i2;
            }
        }
        onLayout(false, getLeft(), getTop(), getRight(), getBottom());
        resetTaskVisuals();
        this.mSplitHiddenTaskViewIndex = -1;
        TaskView taskView = this.mSplitHiddenTaskView;
        if (taskView != null) {
            taskView.setVisibility(0);
            this.mSplitHiddenTaskView = null;
        }
    }

    private void clearSplitHolderView() {
        LauncherRootView rootView = this.mActivity.getRootView();
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View childAt = rootView.getChildAt(i);
            if (childAt instanceof FloatingTaskView) {
                Log.w(TAG, "Remove invalid holder view.");
                rootView.removeView(childAt);
            }
        }
    }

    public float getSplitSelectTranslation() {
        int activeSplitStagePosition = getSplitPlaceholder().getActiveSplitStagePosition();
        if (!shouldShiftThumbnailsForSplitSelect()) {
            return 0.0f;
        }
        return this.mActivity.getResources().getDimension(R.dimen.split_placeholder_size) * getPagedOrientationHandler().getSplitTranslationDirectionFactor(activeSplitStagePosition, this.mActivity.getDeviceProfile());
    }

    protected void onRotateInSplitSelectionState() {
        this.mOrientationHandler.getInitialSplitPlaceholderBounds(this.mSplitPlaceholderSize, this.mSplitPlaceholderInset, this.mActivity.getDeviceProfile(), this.mSplitSelectStateController.getActiveSplitStagePosition(), this.mTempRect);
        this.mTempRectF.set(this.mTempRect);
        FloatingTaskView floatingTaskView = this.mFirstFloatingTaskView;
        if (floatingTaskView != null) {
            floatingTaskView.updateOrientationHandler(this.mOrientationHandler);
            this.mFirstFloatingTaskView.update(this.mTempRectF, 1.0f);
        }
        Pair<FloatProperty, FloatProperty> splitSelectTaskOffset = getPagedOrientationHandler().getSplitSelectTaskOffset(TASK_PRIMARY_SPLIT_TRANSLATION, TASK_SECONDARY_SPLIT_TRANSLATION, this.mActivity.getDeviceProfile());
        ((FloatProperty) splitSelectTaskOffset.first).set(this, Float.valueOf(getSplitSelectTranslation()));
        ((FloatProperty) splitSelectTaskOffset.second).set(this, Float.valueOf(0.0f));
        applySplitPrimaryScrollOffset();
    }

    private void updateDeadZoneRects() {
        this.mClearAllButtonDeadZoneRect.setEmpty();
        if (this.mClearAllButton.getWidth() > 0) {
            int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.recents_clear_all_deadzone_vertical_margin);
            this.mClearAllButton.getHitRect(this.mClearAllButtonDeadZoneRect);
            this.mClearAllButtonDeadZoneRect.inset((-getPaddingRight()) / 2, -dimensionPixelSize);
        }
        this.mTaskViewDeadZoneRect.setEmpty();
        int taskViewCount = getTaskViewCount();
        if (taskViewCount > 0) {
            TaskView taskViewAt = getTaskViewAt(0);
            getTaskViewAt(taskViewCount - 1).getHitRect(this.mTaskViewDeadZoneRect);
            this.mTaskViewDeadZoneRect.union(taskViewAt.getLeft(), taskViewAt.getTop(), taskViewAt.getRight(), taskViewAt.getBottom());
        }
    }

    private void updateEmptyStateUi(boolean sizeChanged) {
        boolean z = getWidth() > 0 && getHeight() > 0;
        if (sizeChanged && z) {
            this.mEmptyTextLayout = null;
            this.mLastMeasureSize.set(getWidth(), getHeight());
        }
        if (this.mShowEmptyMessage && z && this.mEmptyTextLayout == null) {
            int i = this.mLastMeasureSize.x;
            int i2 = this.mEmptyMessagePadding;
            int i3 = (i - i2) - i2;
            CharSequence charSequence = this.mEmptyMessage;
            StaticLayout staticLayoutBuild = StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), this.mEmptyMessagePaint, i3).setAlignment(Layout.Alignment.ALIGN_CENTER).build();
            this.mEmptyTextLayout = staticLayoutBuild;
            int height = (this.mLastMeasureSize.y - ((staticLayoutBuild.getHeight() + this.mEmptyMessagePadding) + this.mEmptyIcon.getIntrinsicHeight())) / 2;
            int intrinsicWidth = (this.mLastMeasureSize.x - this.mEmptyIcon.getIntrinsicWidth()) / 2;
            Drawable drawable = this.mEmptyIcon;
            drawable.setBounds(intrinsicWidth, height, drawable.getIntrinsicWidth() + intrinsicWidth, this.mEmptyIcon.getIntrinsicHeight() + height);
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || (this.mShowEmptyMessage && who == this.mEmptyIcon);
    }

    protected void maybeDrawEmptyMessage(Canvas canvas) {
        if (!this.mShowEmptyMessage || this.mEmptyTextLayout == null) {
            return;
        }
        int dimension = (int) getResources().getDimension(R.dimen.task_thumbnail_top_margin);
        int i = 0;
        if (this.mActivity.getDeviceProfile().isMultiWindowMode || this.mActivity.getDeviceProfile().isLandscape) {
            dimension = 0;
        } else {
            int i2 = (this.mOrientationState.getRecentsActivityRotation() == 3 || this.mOrientationState.getTouchRotation() == 3) ? dimension : 0;
            if (this.mOrientationState.getRecentsActivityRotation() != 1 && this.mOrientationState.getTouchRotation() != 1) {
                dimension = 0;
            }
            i = i2;
        }
        this.mTempRect.set(this.mInsets.left + getPaddingLeft() + i, this.mInsets.top + getPaddingTop(), this.mInsets.right + getPaddingRight() + dimension, this.mInsets.bottom + getPaddingBottom());
        canvas.save();
        canvas.translate(getScrollX() + ((this.mTempRect.left - this.mTempRect.right) / 2), getScrollY() + ((this.mTempRect.top - this.mTempRect.bottom) / 2));
        canvas.translate(this.mEmptyMessagePadding, this.mEmptyIcon.getBounds().bottom + this.mEmptyMessagePadding);
        this.mEmptyTextLayout.draw(canvas);
        canvas.restore();
    }

    public AnimatorSet createAdjacentPageAnimForTaskLaunch(TaskView tv) {
        AnimatorSet animatorSet = new AnimatorSet();
        int iIndexOfChild = indexOfChild(tv);
        int currentPage = getCurrentPage();
        boolean z = iIndexOfChild == currentPage;
        float maxScaleForFullScreen = getMaxScaleForFullScreen();
        if (z) {
            RecentsView recentsView = tv.getRecentsView();
            animatorSet.play(ObjectAnimator.ofFloat(recentsView, LauncherAnimUtils.SCALE_PROPERTY, maxScaleForFullScreen));
            animatorSet.play(ObjectAnimator.ofFloat(recentsView, FULLSCREEN_PROGRESS, 1.0f));
        } else {
            float width = tv.getWidth() * (maxScaleForFullScreen - tv.getCurveScale());
            View pageAt = getPageAt(currentPage);
            Property property = TRANSLATION_X;
            float[] fArr = new float[1];
            fArr[0] = this.mIsRtl ? -width : width;
            animatorSet.play(ObjectAnimator.ofFloat(pageAt, (Property<View, Float>) property, fArr));
            int i = currentPage + (currentPage - iIndexOfChild);
            if (i >= 0 && i < getPageCount()) {
                PropertyListBuilder propertyListBuilder = new PropertyListBuilder();
                if (this.mIsRtl) {
                    width = -width;
                }
                animatorSet.play(propertyListBuilder.translationX(width).scale(1.0f).build(getPageAt(i)));
            }
        }
        return animatorSet;
    }

    public float getMaxScaleForFullScreen() {
        getTaskSize(this.mTempRect);
        return getPagedViewOrientedState().getFullScreenScaleAndPivot(this.mTempRect, this.mActivity.getDeviceProfile(), this.mTempPointF);
    }

    public PendingAnimation createTaskLaunchAnimation(final TaskView tv, long duration, Interpolator interpolator) {
        RecommandAppLayout recommandAppLayout;
        if (getTaskViewCount() == 0) {
            return new PendingAnimation(duration);
        }
        if (!LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue() && (recommandAppLayout = this.mRecommandLayout) != null) {
            recommandAppLayout.setAlpha(0.0f);
        }
        final int sysUiStatusNavFlags = tv.getThumbnail().getSysUiStatusNavFlags();
        if (!LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue()) {
            this.mClearAllButton.setVisibilityAlpha(0.0f);
        }
        final boolean[] zArr = {false};
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$13ahlT_zm0j2WxzY-lr2Th5Y_xY
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$createTaskLaunchAnimation$10$RecentsView(sysUiStatusNavFlags, tv, zArr, valueAnimator);
            }
        });
        AnimatorSet animatorSetCreateAdjacentPageAnimForTaskLaunch = createAdjacentPageAnimForTaskLaunch(tv);
        DepthController depthController = getDepthController();
        if (depthController != null) {
            animatorSetCreateAdjacentPageAnimForTaskLaunch.play(ObjectAnimator.ofFloat(depthController, DepthController.DEPTH, LauncherState.BACKGROUND_APP.getDepth(this.mActivity)));
        }
        animatorSetCreateAdjacentPageAnimForTaskLaunch.play(valueAnimatorOfFloat);
        animatorSetCreateAdjacentPageAnimForTaskLaunch.setInterpolator(interpolator);
        PendingAnimation pendingAnimation = new PendingAnimation(duration);
        this.mPendingAnimation = pendingAnimation;
        pendingAnimation.add(animatorSetCreateAdjacentPageAnimForTaskLaunch);
        this.mPendingAnimation.addEndListener(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$UXF5WyLAjAUKGz7bm2Sb0X4bAnE
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$createTaskLaunchAnimation$12$RecentsView(tv, (PendingAnimation.EndState) obj);
            }
        });
        return this.mPendingAnimation;
    }

    public /* synthetic */ void lambda$createTaskLaunchAnimation$10$RecentsView(int i, TaskView taskView, boolean[] zArr, ValueAnimator valueAnimator) {
        SystemUiController systemUiController = this.mActivity.getSystemUiController();
        if (valueAnimator.getAnimatedFraction() <= 0.85f) {
            i = 0;
        }
        systemUiController.updateUiState(4, i);
        onTaskLaunchAnimationUpdate(valueAnimator.getAnimatedFraction(), taskView);
        boolean z = valueAnimator.getAnimatedFraction() >= 0.5f;
        if (z != zArr[0]) {
            zArr[0] = z;
            performHapticFeedback(1, 1);
        }
    }

    public /* synthetic */ void lambda$createTaskLaunchAnimation$12$RecentsView(final TaskView taskView, PendingAnimation.EndState endState) {
        if (endState.isSuccess) {
            new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$DBOOobIslz_Q4wMUINN3lwjW53E
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$createTaskLaunchAnimation$11$RecentsView(taskView, (Boolean) obj);
                }
            };
            taskView.launchTask(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$QQq4re1DSAGe8rYuQeUGmqvL6W8
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.onTaskLaunchAnimationEnd(((Boolean) obj).booleanValue());
                }
            });
            Task task = taskView.getTask();
            if (task != null) {
                this.mActivity.getUserEventDispatcher().logTaskLaunchOrDismiss(endState.logAction, 2, indexOfChild(taskView), TaskUtils.getLaunchComponentKeyForTask(task.key));
                this.mActivity.getStatsLogManager().logger().withItemInfo(taskView.getItemInfo()).log(StatsLogManager.LauncherEvent.LAUNCHER_TASK_LAUNCH_SWIPE_DOWN);
            }
        } else {
            onTaskLaunchAnimationEnd(false);
            onTaskLaunchedAfter(taskView);
        }
        this.mPendingAnimation = null;
    }

    public /* synthetic */ void lambda$createTaskLaunchAnimation$11$RecentsView(TaskView taskView, Boolean bool) {
        onTaskLaunchAnimationEnd(bool.booleanValue());
        if (!bool.booleanValue()) {
            taskView.notifyTaskLaunchFailed(TAG);
        }
        onTaskLaunchedAfter(taskView);
    }

    protected void onTaskLaunchAnimationEnd(boolean success) {
        if (success) {
            resetTaskVisuals();
        }
    }

    @Override // com.android.launcher3.PagedView
    protected void notifyPageSwitchListener(int prevPage) {
        super.notifyPageSwitchListener(prevPage);
        loadVisibleTaskData();
        updateEnabledOverlays();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addChildrenForAccessibility(ArrayList<View> outChildren) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            outChildren.add(getChildAt(childCount));
        }
    }

    @Override // com.android.launcher3.PagedView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(1, getTaskViewCount(), false, 0));
    }

    @Override // com.android.launcher3.PagedView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        int taskViewCount = getTaskViewCount();
        event.setScrollable(taskViewCount > 0);
        if (event.getEventType() == 4096) {
            int[] visibleChildrenRange = getVisibleChildrenRange();
            event.setFromIndex(taskViewCount - visibleChildrenRange[1]);
            event.setToIndex(taskViewCount - visibleChildrenRange[0]);
            event.setItemCount(taskViewCount);
        }
    }

    @Override // com.android.launcher3.PagedView, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ListView.class.getName();
    }

    public void setEnableDrawingLiveTile(boolean enableDrawingLiveTile) {
        this.mEnableDrawingLiveTile = enableDrawingLiveTile;
    }

    public void setRecentsAnimationTargets(RecentsAnimationController recentsAnimationController, RecentsAnimationTargets recentsAnimationTargets) {
        this.mRecentsAnimationController = recentsAnimationController;
        this.mRecentsAnimationTargets = recentsAnimationTargets;
        if (recentsAnimationTargets == null && recentsAnimationController == null) {
            setAnimRunning(false);
        }
    }

    public void setLiveTileOverlayAttached(boolean liveTileOverlayAttached) {
        this.mLiveTileOverlayAttached = liveTileOverlayAttached;
    }

    public void updateLiveTileIcon(Drawable icon) {
        if (this.mLiveTileOverlayAttached) {
            LiveTileOverlay.INSTANCE.setIcon(icon);
        }
    }

    public void finishRecentsAnimation(boolean toRecents, final Runnable onFinishComplete) {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            recentsAnimationController.finish(toRecents, new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$d9WSHcMRg3fTboa3Kk-PSrKpNYQ
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$finishRecentsAnimation$13$RecentsView(onFinishComplete);
                }
            });
        } else if (onFinishComplete != null) {
            onFinishComplete.run();
        }
    }

    public /* synthetic */ void lambda$finishRecentsAnimation$13$RecentsView(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
            setCurrentTask(-1);
        }
    }

    @Override // com.android.launcher3.PagedView
    protected int computeMinScroll() {
        if (getTaskViewCount() > 0) {
            if (this.mDisallowScrollToClearAll) {
                if (this.mIsRtl) {
                    return getScrollForPage(indexOfChild(getTaskViewAt(getTaskViewCount() - 1)));
                }
                return getScrollForPage(this.mTaskViewStartIndex);
            }
            if (this.mIsRtl) {
                return getScrollForPage(indexOfChild(getTaskViewAt(getTaskViewCount() - 1)) + 1);
            }
            return getScrollForPage(this.mTaskViewStartIndex);
        }
        return super.computeMinScroll();
    }

    @Override // com.android.launcher3.PagedView
    protected int computeMaxScroll() {
        if (getTaskViewCount() > 0) {
            if (this.mDisallowScrollToClearAll) {
                if (this.mIsRtl) {
                    return getScrollForPage(this.mTaskViewStartIndex);
                }
                return getScrollForPage(indexOfChild(getTaskViewAt(getTaskViewCount() - 1)));
            }
            if (this.mIsRtl) {
                return getScrollForPage(this.mTaskViewStartIndex);
            }
            return getScrollForPage(indexOfChild(getTaskViewAt(getTaskViewCount() - 1)) + 1);
        }
        return super.computeMaxScroll();
    }

    public ClearAllButton getClearAllButton() {
        return this.mClearAllButton;
    }

    @Override // com.android.launcher3.PagedView
    protected boolean onOverscroll(int amount) {
        if (amount > 0 && !this.mIsRtl) {
            return false;
        }
        if (amount >= 0 || !this.mIsRtl) {
            return super.onOverscroll(amount);
        }
        return false;
    }

    public int getScrollOffset() {
        return getScrollOffset(getRunningTaskIndex());
    }

    public int getScrollOffset(int pageIndex) {
        if (pageIndex == -1) {
            return 0;
        }
        return getScrollForPage(pageIndex) - this.mOrientationHandler.getPrimaryScroll(this);
    }

    public Consumer<MotionEvent> getEventDispatcher(final float navbarRotation) {
        final float degreesRotated = navbarRotation == 0.0f ? this.mOrientationHandler.getDegreesRotated() : -navbarRotation;
        if (degreesRotated == 0.0f) {
            this.mConsumerId = 0;
            return new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$L5qiNMJR6DpWb-gzzoNblMAV1bU
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$getEventDispatcher$14$RecentsView((MotionEvent) obj);
                }
            };
        }
        return new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$NjkRDWU04gn9nlWnVq2e0HMfsOs
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$getEventDispatcher$15$RecentsView(navbarRotation, degreesRotated, (MotionEvent) obj);
            }
        };
    }

    public /* synthetic */ void lambda$getEventDispatcher$14$RecentsView(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
    }

    public /* synthetic */ void lambda$getEventDispatcher$15$RecentsView(float f, float f2, MotionEvent motionEvent) {
        if (f != 0.0f && this.mOrientationState.isMultipleOrientationSupportedByDevice() && !this.mOrientationState.getOrientationHandler().isLayoutNaturalToLauncher()) {
            this.mConsumerId = 1;
            this.mOrientationState.flipVertical(motionEvent);
            super.onTouchEvent(motionEvent);
            this.mOrientationState.flipVertical(motionEvent);
            return;
        }
        this.mConsumerId = 2;
        float f3 = -f2;
        this.mOrientationState.transformEvent(f3, motionEvent, true);
        super.onTouchEvent(motionEvent);
        this.mOrientationState.transformEvent(f3, motionEvent, false);
    }

    private void updateEnabledOverlays() {
        int nextPage = this.mOverlayEnabled ? getNextPage() : -1;
        int taskViewCount = getTaskViewCount();
        int i = this.mTaskViewStartIndex;
        while (i < this.mTaskViewStartIndex + taskViewCount) {
            getTaskViewAtByAbsoluteIndex(i).setOverlayEnabled(i == nextPage);
            i++;
        }
    }

    public void setOverlayEnabled(boolean overlayEnabled) {
        if (this.mOverlayEnabled != overlayEnabled) {
            this.mOverlayEnabled = overlayEnabled;
            updateEnabledOverlays();
        }
    }

    public void setOverviewGridEnabled(boolean overviewGridEnabled) {
        if (this.mOverviewGridEnabled != overviewGridEnabled) {
            this.mOverviewGridEnabled = overviewGridEnabled;
            requestLayout();
        }
    }

    public void setOverviewFullscreenEnabled(boolean overviewFullscreenEnabled) {
        if (this.mOverviewFullscreenEnabled != overviewFullscreenEnabled) {
            this.mOverviewFullscreenEnabled = overviewFullscreenEnabled;
            requestLayout();
        }
    }

    public void setOverviewSelectEnabled(boolean overviewSelectEnabled) {
        if (this.mOverviewSelectEnabled != overviewSelectEnabled) {
            this.mOverviewSelectEnabled = overviewSelectEnabled;
            updatePivots();
        }
    }

    public void switchToScreenshot(ThumbnailData thumbnailData, Runnable onFinishRunnable) {
        TaskView runningTaskView = getRunningTaskView();
        if (runningTaskView != null) {
            runningTaskView.setShowScreenshot(true);
            if (thumbnailData != null) {
                runningTaskView.getThumbnail().setThumbnail(runningTaskView.getTask(), thumbnailData);
            } else {
                runningTaskView.getThumbnail().refresh();
            }
            ViewUtils.postFrameDrawn(runningTaskView, onFinishRunnable);
            return;
        }
        onFinishRunnable.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTaskModalness(float modalness) {
        this.mTaskModalness = modalness;
        updatePageOffsets();
        if (getCurrentPageTaskView() != null) {
            getCurrentPageTaskView().setModalness(modalness);
        }
        if (this.mOrientationState.canRecentsActivityRotate()) {
            return;
        }
        this.mOrientationState.getTouchRotation();
    }

    @Override // com.android.quickstep.util.SplitScreenBounds.OnChangeListener
    public void onSecondaryWindowBoundsChanged() {
        setInsets(this.mInsets);
        requestLayout();
    }

    public BaseActivityInterface getSizeStrategy() {
        return this.mSizeStrategy;
    }

    private boolean showAsFullscreen() {
        return this.mOverviewFullscreenEnabled && this.mCurrentGestureEndTarget != GestureState.GestureEndTarget.RECENTS;
    }

    public int getPipCornerRadius() {
        return this.mPipCornerRadius;
    }

    public int getPipShadowRadius() {
        return this.mPipShadowRadius;
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class PinnedStackAnimationListener<T extends BaseActivity> extends IPipAnimationListener.Stub {
        private T mActivity;
        private RecentsView mRecentsView;

        @Override // com.android.wm.shell.pip.IPipAnimationListener
        public void onExpandPip() {
        }

        private PinnedStackAnimationListener() {
        }

        public void setActivityAndRecentsView(T activity, RecentsView recentsView) {
            this.mActivity = activity;
            this.mRecentsView = recentsView;
        }

        @Override // com.android.wm.shell.pip.IPipAnimationListener
        public void onPipAnimationStarted() {
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$PinnedStackAnimationListener$Y9Qp7Qrc0TUF1TFuMV2u2PqkZ4Y
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onPipAnimationStarted$0$RecentsView$PinnedStackAnimationListener();
                }
            });
        }

        public /* synthetic */ void lambda$onPipAnimationStarted$0$RecentsView$PinnedStackAnimationListener() {
            T t = this.mActivity;
            if (t != null) {
                t.clearForceInvisibleFlag(9);
            }
        }

        @Override // com.android.wm.shell.pip.IPipAnimationListener
        public void onPipResourceDimensionsChanged(int cornerRadius, int shadowRadius) {
            RecentsView recentsView = this.mRecentsView;
            if (recentsView != null) {
                recentsView.mPipCornerRadius = cornerRadius;
                this.mRecentsView.mPipShadowRadius = shadowRadius;
            }
        }
    }

    @Override // com.android.launcher3.PagedView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void reloadIfNeeded() {
        reloadIfNeeded(false);
    }

    public void setRotationForMultiWindowMode(boolean isMultiWindowMode) {
        T t = this.mActivity;
        if (t instanceof Launcher) {
            RotationHelper rotationHelper = ((Launcher) t).getRotationHelper();
            if (isMultiWindowMode && rotationHelper.getLastActivityFlags() != -1) {
                ((Launcher) this.mActivity).getRotationHelper().setCurrentStateRequest(1);
            } else {
                if (isMultiWindowMode || rotationHelper.getLastActivityFlags() != -1) {
                    return;
                }
                ((Launcher) this.mActivity).getRotationHelper().setCurrentStateRequest(2);
            }
        }
    }

    public OverScroller getNativeScroller() {
        return this.mScroller;
    }

    public void setDraggingState(boolean enabled) {
        this.mIsDragging = enabled;
    }

    public boolean getOverviewStateEnable() {
        return this.mOverviewStateEnabled;
    }

    private void onChildViewsChanged() {
        boolean z = getChildCount() > 0 && !isFastOverView();
        this.mClearAllButton.setImportantForAccessibility(getChildCount() > 0 ? 1 : 2);
        if (z && this.mClearAllButton.getAlpha() == 0.0f) {
            this.mClearAllButton.setVisibilityAlpha(1.0f);
        } else if (getChildCount() == 0) {
            this.mClearAllButton.setVisibilityAlpha(0.0f);
        }
        setFocusable(!z);
        if (this.mOverviewStateEnabled) {
            setEnabledButtonsOfHeaderView(true);
        }
    }

    public void setClearAllButton(View clearAllButton) {
        ClearAllButton clearAllButton2 = (ClearAllButton) clearAllButton;
        this.mClearAllButton = clearAllButton2;
        clearAllButton2.setOnClickListener(new View.OnClickListener() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$v8AirrKK9SKJqEfinSexVAR4fqU
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$setClearAllButton$16$RecentsView(view);
            }
        });
        this.mClearAllButton.forceHasOverlappingRendering(false);
        if (isRtl()) {
            this.mClearAllButton.setNextFocusRightId(getId());
            setNextFocusLeftId(this.mClearAllButton.getId());
        } else {
            this.mClearAllButton.setNextFocusLeftId(getId());
            setNextFocusRightId(this.mClearAllButton.getId());
        }
    }

    public /* synthetic */ void lambda$setClearAllButton$16$RecentsView(View view) {
        this.mActivity.getUserEventDispatcher().logActionOnControl(0, 13);
        dismissAllTasks(view);
    }

    public void setLocationOfClearAllButton() {
        ClearAllButton clearAllButton = this.mClearAllButton;
        if (clearAllButton != null) {
            InsettableFrameLayout.LayoutParams layoutParams = (InsettableFrameLayout.LayoutParams) clearAllButton.getLayoutParams();
            Rect rect = new Rect();
            getTaskSize(rect);
            int i = (!LGHomeFeature.isAppSuggestionEnabled() || this.mRecommandLayout == null || this.mActivity.getDeviceProfile().isLandscape || (this.mActivity.getDisplayId() == 4)) ? 0 : this.mRecommandLayoutHeight;
            float rotation = this.mClearAllButton.getRotation();
            boolean zIsRtl = Utilities.isRtl(getResources());
            if (rotation == 90.0f) {
                layoutParams.gravity = zIsRtl ? 8388661 : 8388659;
                layoutParams.leftMargin = (-(this.mClearAllButtonWidth - rect.left)) / 2;
                layoutParams.rightMargin = 0;
                layoutParams.topMargin = (int) (rect.top + ((rect.height() - this.mClearAllButtonHeight) * 0.5f));
                layoutParams.bottomMargin = 0;
            } else if (rotation == 270.0f) {
                layoutParams.gravity = zIsRtl ? 8388659 : 8388661;
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = (-(this.mClearAllButtonWidth - rect.left)) / 2;
                layoutParams.topMargin = (int) (rect.top + ((rect.height() - this.mClearAllButtonHeight) * 0.5f));
                layoutParams.bottomMargin = 0;
            } else {
                layoutParams.gravity = 81;
                layoutParams.leftMargin = this.mInsets.left / 2;
                layoutParams.rightMargin = this.mInsets.right / 2;
                layoutParams.topMargin = 0;
                layoutParams.bottomMargin = this.mInsets.bottom + i + (((((this.mActivity.getDeviceProfile().heightPx - rect.bottom) - i) - this.mInsets.bottom) - this.mClearAllButtonHeight) / 2);
            }
            this.mClearAllButton.setLayoutParams(layoutParams);
        }
    }

    public boolean isVisibleClearAllButton() {
        int childCount = getChildCount();
        updateEmptyMessage();
        return (this.mShowEmptyMessage || childCount == 0) ? false : true;
    }

    public void updateClearAllEnabled() {
        int childCount = getChildCount();
        int i = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            if (getTaskViewAt(i2).isPinned()) {
                i++;
            }
        }
        if (childCount > 0 && childCount == i) {
            this.mClearAllButton.setEnabled(false);
            this.mClearAllButton.setTextColor(getContext().getResources().getColor(R.color.overview_disable_color));
        } else {
            this.mClearAllButton.setEnabled(true);
            this.mClearAllButton.setTextColor(getContext().getResources().getColor(R.color.white_color));
        }
    }

    public void updateHeaderItemsVisibility(int item, boolean visible) {
        for (int i = 0; i < getChildCount(); i++) {
            TaskView taskView = (TaskView) getChildAt(i);
            if (visible) {
                taskView.setHeaderViewVisibility(item, true);
            } else {
                taskView.setHeaderViewVisibility(item, false);
            }
        }
    }

    private void setEnabledButtonsOfHeaderView(boolean force) {
        int iValidateNewPage = validateNewPage(getCurrentPage());
        for (int i = 0; i < getChildCount(); i++) {
            TaskView taskView = (TaskView) getPageAt(i);
            if (taskView != null && (isTaskViewVisible(taskView) || force)) {
                if (iValidateNewPage == i) {
                    taskView.setEnabledButtonsOfHeader(true);
                } else {
                    taskView.setEnabledButtonsOfHeader(false);
                }
            }
        }
    }

    public void setRecommandAppLayout(RecommandAppLayout recommandAppLayout) {
        this.mRecommandLayout = recommandAppLayout;
        if (recommandAppLayout != null) {
            if (!LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue() || getContext().getDisplayId() == 4 || !LGHomeFeature.isAppSuggestionEnabled()) {
                this.mRecommandLayout.setVisibility(8);
            } else {
                this.mRecommandLayout.setVisibility(0);
            }
        }
        setLocationOfClearAllButton();
    }

    public RecommandAppLayout getRecommandLayout() {
        return this.mRecommandLayout;
    }

    public void updateStackLayout() {
        float f = 0.0f;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            f += 0.1f;
            getChildAt(childCount).setTranslationZ(f);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r7v1, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00aa  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void updateStackProperties() {
        /*
            r13 = this;
            int r0 = r13.getPageCount()
            if (r0 == 0) goto Lda
            r0 = 0
            android.view.View r1 = r13.getPageAt(r0)
            int r1 = r1.getMeasuredWidth()
            if (r1 != 0) goto L13
            goto Lda
        L13:
            int r1 = r13.getNormalChildWidth()
            int r1 = r1 / 2
            android.graphics.Rect r2 = r13.mInsets
            int r2 = r2.left
            int r3 = r13.getPaddingLeft()
            int r2 = r2 + r3
            int r3 = r13.getScrollX()
            int r2 = r2 + r3
            int r2 = r2 + r1
            int r3 = r13.getNormalChildWidth()
            int r4 = r13.mPageSpacing
            int r3 = r3 + r4
            int r4 = r13.getPageCount()
            boolean r5 = r13.mIsRtl
            if (r5 == 0) goto L39
            int r0 = r4 + (-1)
        L39:
            boolean r5 = r13.mIsRtl
            r6 = -1
            if (r5 == 0) goto L3f
            r4 = r6
        L3f:
            boolean r5 = r13.mIsRtl
            if (r5 == 0) goto L44
            goto L45
        L44:
            r6 = 1
        L45:
            android.graphics.Rect r5 = r13.mInsets
            int r5 = r5.left
            int r7 = r13.getPaddingLeft()
            int r5 = r5 + r7
        L4e:
            if (r0 == r4) goto Lda
            android.view.View r7 = r13.getPageAt(r0)
            float r8 = (float) r5
            float r9 = r7.getTranslationX()
            float r9 = r9 + r8
            float r10 = (float) r1
            float r9 = r9 + r10
            float r10 = (float) r2
            float r10 = r10 - r9
            r9 = 0
            int r11 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1))
            if (r11 <= 0) goto Laa
            int r11 = r3 * 3
            float r11 = (float) r11
            int r12 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
            if (r12 >= 0) goto Laa
            boolean r9 = r13.mOverviewEnterAnimationWorking
            if (r9 == 0) goto L76
            com.android.quickstep.views.RecentsView$ScrollState r8 = r13.mScrollState
            int r9 = r5 - r1
            float r9 = (float) r9
            r8.leftOffset = r9
            goto L9d
        L76:
            r9 = 1067114824(0x3f9ae148, float:1.21)
            float r9 = r10 / r9
            float r8 = r8 + r9
            android.graphics.Rect r9 = r13.mInsets
            int r9 = r9.left
            int r12 = r13.getScrollX()
            int r9 = r9 + r12
            float r9 = (float) r9
            int r9 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r9 <= 0) goto L8f
            com.android.quickstep.views.RecentsView$ScrollState r9 = r13.mScrollState
            r9.leftOffset = r8
            goto L9d
        L8f:
            com.android.quickstep.views.RecentsView$ScrollState r8 = r13.mScrollState
            android.graphics.Rect r9 = r13.mInsets
            int r9 = r9.left
            int r12 = r13.getScrollX()
            int r9 = r9 + r12
            float r9 = (float) r9
            r8.leftOffset = r9
        L9d:
            com.android.quickstep.views.RecentsView$ScrollState r8 = r13.mScrollState
            r9 = 1061997773(0x3f4ccccd, float:0.8)
            float r10 = r10 / r11
            float r9 = java.lang.Math.min(r9, r10)
            r8.linearInterpolation = r9
            goto Lc2
        Laa:
            boolean r10 = r13.mOverviewEnterAnimationWorking
            if (r10 == 0) goto Lba
            if (r0 != 0) goto Lba
            r10 = r7
            com.android.quickstep.views.TaskView r10 = (com.android.quickstep.views.TaskView) r10
            com.android.quickstep.views.TaskThumbnailView r10 = r10.getThumbnail()
            r10.setCornerRadius(r9)
        Lba:
            com.android.quickstep.views.RecentsView$ScrollState r10 = r13.mScrollState
            r10.leftOffset = r8
            com.android.quickstep.views.RecentsView$ScrollState r8 = r13.mScrollState
            r8.linearInterpolation = r9
        Lc2:
            r8 = r7
            com.android.quickstep.views.RecentsView$PageCallbacks r8 = (com.android.quickstep.views.RecentsView.PageCallbacks) r8
            com.android.quickstep.views.RecentsView$ScrollState r9 = r13.mScrollState
            r8.onPageScroll(r9)
            int r7 = r7.getMeasuredWidth()
            int r8 = r13.mPageSpacing
            int r7 = r7 + r8
            int r8 = r13.getChildGap()
            int r7 = r7 + r8
            int r5 = r5 + r7
            int r0 = r0 + r6
            goto L4e
        Lda:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.views.RecentsView.updateStackProperties():void");
    }

    public PendingAnimation createTaskMenuAnimation(TaskView taskView, long duration, final boolean isOpen) {
        PendingAnimation pendingAnimation = new PendingAnimation(duration);
        int childCount = getChildCount();
        if (childCount == 0) {
            return pendingAnimation;
        }
        getNormalChildWidth();
        int i = this.mPageSpacing;
        int iIndexOfChild = indexOfChild(taskView);
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (i2 != iIndexOfChild - 1 || LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
                if (i2 == iIndexOfChild) {
                    if (isOpen) {
                        addAnim(ObjectAnimator.ofFloat(childAt, (Property<View, Float>) SCALE_X, 1.0f, 1.03f), duration, Interpolators.ACCEL, pendingAnimation);
                        addAnim(ObjectAnimator.ofFloat(childAt, (Property<View, Float>) SCALE_Y, 1.0f, 1.03f), duration, Interpolators.ACCEL, pendingAnimation);
                    } else {
                        addAnim(ObjectAnimator.ofFloat(childAt, (Property<View, Float>) SCALE_X, 1.03f, 1.0f), duration, Interpolators.ACCEL_2, pendingAnimation);
                        addAnim(ObjectAnimator.ofFloat(childAt, (Property<View, Float>) SCALE_Y, 1.03f, 1.0f), duration, Interpolators.ACCEL_2, pendingAnimation);
                    }
                } else if (i2 == iIndexOfChild + 1 && !LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
                    if (isOpen) {
                        addAnim(ObjectAnimator.ofFloat(childAt, DIM_ALPHA, TaskView.MAX_PAGE_SCRIM_ALPHA, 0.5f), duration, Interpolators.ACCEL, pendingAnimation);
                    } else {
                        addAnim(ObjectAnimator.ofFloat(childAt, DIM_ALPHA, 0.5f, TaskView.MAX_PAGE_SCRIM_ALPHA), duration, Interpolators.ACCEL_2, pendingAnimation);
                    }
                }
            } else if (isOpen) {
                addAnim(ObjectAnimator.ofFloat(childAt, DIM_ALPHA, 0.0f, 0.5f), duration, Interpolators.ACCEL, pendingAnimation);
            } else {
                addAnim(ObjectAnimator.ofFloat(childAt, DIM_ALPHA, 0.5f, 0.0f), duration, Interpolators.ACCEL_2, pendingAnimation);
            }
            if (isOpen) {
                addAnim(ObjectAnimator.ofFloat(this.mClearAllButton, ClearAllButton.VISIBILITY_ALPHA, 1.0f, 0.0f), duration, Interpolators.ACCEL_2, pendingAnimation);
            } else {
                addAnim(ObjectAnimator.ofFloat(this.mClearAllButton, ClearAllButton.VISIBILITY_ALPHA, 0.0f, 1.0f), duration, Interpolators.ACCEL_2, pendingAnimation);
            }
            boolean z = this.mActivity.getDeviceProfile().isLandscape;
            if (LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue() && this.mRecommandLayout != null && ((!z || !this.mActivity.getDeviceProfile().isMultiWindowMode) && LGHomeFeature.isAppSuggestionEnabled())) {
                if (this.mActivity.getDeviceProfile().mDisplayId == 4) {
                    this.mRecommandLayout.setVisibility(8);
                } else if ((this instanceof FallbackRecentsView) && this.mActivity.getDeviceProfile().isMultiWindowMode) {
                    RECOMMAND_APP_ALPHA.setValue(this, 0.0f);
                } else if (isOpen) {
                    addAnim(ObjectAnimator.ofFloat(this, RECOMMAND_APP_ALPHA, 1.0f, 0.5f), duration, Interpolators.ACCEL_2, pendingAnimation);
                } else {
                    addAnim(ObjectAnimator.ofFloat(this, RECOMMAND_APP_ALPHA, 0.5f, 1.0f), duration, Interpolators.ACCEL_2, pendingAnimation);
                }
            } else {
                this.mRecommandLayout.setVisibility(4);
            }
        }
        pendingAnimation.addEndListener(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$CMLhUF5eIvpO1V4t69Mp7CYj6Tk
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$createTaskMenuAnimation$17$RecentsView(isOpen, (PendingAnimation.EndState) obj);
            }
        });
        return pendingAnimation;
    }

    public /* synthetic */ void lambda$createTaskMenuAnimation$17$RecentsView(boolean z, PendingAnimation.EndState endState) {
        if (!z) {
            resetTaskVisuals();
        }
        this.mPendingTaskMenuAnimation = null;
    }

    public PendingAnimation createTaskEnterAnimation(TaskView taskView, long duration, final boolean isOpen) {
        float dimension;
        PendingAnimation pendingAnimation = new PendingAnimation(duration);
        int childCount = getChildCount();
        if (childCount == 0) {
            return pendingAnimation;
        }
        int normalChildWidth = getNormalChildWidth() + this.mPageSpacing;
        float x = 0.0f;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (i == 0) {
                if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
                    dimension = getResources().getDimension(R.dimen.overview_ux_9_21_task_corner_radius);
                } else {
                    dimension = getResources().getDimension(R.dimen.overview_new_ui_task_corner_radius);
                }
                addAnim(ObjectAnimator.ofFloat(childAt, CORNER_RADIUS_MORPH, 0.0f, dimension), duration, Interpolators.ACCEL, pendingAnimation);
                x = childAt.getX();
            } else if (x != 0.0f && i <= 3) {
                addAnim(ObjectAnimator.ofFloat(childAt, (Property<View, Float>) TRANSLATION_X, ((x - normalChildWidth) - childAt.getX()) + (getNormalChildWidth() / 2)), duration, Interpolators.ACCEL, pendingAnimation);
            }
        }
        pendingAnimation.addEndListener(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$kRxGQGgwMIrULCX_PCa2WHspN64
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$createTaskEnterAnimation$18$RecentsView(isOpen, (PendingAnimation.EndState) obj);
            }
        });
        return pendingAnimation;
    }

    public /* synthetic */ void lambda$createTaskEnterAnimation$18$RecentsView(boolean z, PendingAnimation.EndState endState) {
        float dimension;
        if (!z) {
            this.mOverviewEnterAnimationWorking = false;
            if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
                dimension = getResources().getDimension(R.dimen.overview_ux_9_21_task_corner_radius);
            } else {
                dimension = getResources().getDimension(R.dimen.overview_new_ui_task_corner_radius);
            }
            ((TaskView) getChildAt(0)).getThumbnail().setCornerRadius(dimension);
            resetTaskVisuals();
        }
        this.mPendingTaskMenuAnimation = null;
    }

    public boolean openTaskMenu(TaskView taskView, final boolean isOpen, final boolean isEnter) {
        T t;
        PendingAnimation pendingAnimationCreateTaskMenuAnimation;
        if (this.mPendingTaskMenuAnimation != null || ((t = this.mActivity) != null && (t instanceof Launcher) && !((Launcher) t).isInState(LauncherState.OVERVIEW))) {
            LGLog.d(TAG, "don't open the TaskMenu because state is not Overview or mPendingTaskMenuAnimation is not null");
            return false;
        }
        if (isSplitSelectionActive()) {
            LGLog.d(TAG, "Don't open task menu when split selection mode.");
            return false;
        }
        T t2 = this.mActivity;
        final int i = (t2 == null || !t2.isInMultiWindowMode()) ? 150 : 0;
        if (isEnter) {
            pendingAnimationCreateTaskMenuAnimation = createTaskEnterAnimation(taskView, i, isOpen);
        } else {
            pendingAnimationCreateTaskMenuAnimation = createTaskMenuAnimation(taskView, i, isOpen);
        }
        this.mPendingTaskMenuAnimation = pendingAnimationCreateTaskMenuAnimation;
        AnimatorPlaybackController animatorPlaybackControllerCreatePlaybackController = pendingAnimationCreateTaskMenuAnimation.createPlaybackController();
        animatorPlaybackControllerCreatePlaybackController.dispatchOnStart();
        animatorPlaybackControllerCreatePlaybackController.setEndAction(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$lpIrnRwEv1btS13XkqtnVBgYSIY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openTaskMenu$19$RecentsView(i);
            }
        });
        animatorPlaybackControllerCreatePlaybackController.getAnimationPlayer().setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        animatorPlaybackControllerCreatePlaybackController.start();
        return true;
    }

    public /* synthetic */ void lambda$openTaskMenu$19$RecentsView(int i) {
        if (i == 0) {
            this.mPendingTaskMenuAnimation = null;
        } else {
            this.mPendingTaskMenuAnimation.finish(true, 3);
        }
    }

    public void setOverviewEnterAnimationWorking(boolean isWorking) {
        this.mOverviewEnterAnimationWorking = isWorking;
    }

    public boolean getOverviewEnterAnimationWorking() {
        return this.mOverviewEnterAnimationWorking;
    }

    @Override // com.android.launcher3.PagedView, android.view.View
    public void scrollTo(int x, int y) {
        this.mUnboundedScrollX = x;
        super.scrollTo(x, y);
    }

    public void updateRecommandLayoutItems() {
        if (!LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue() || !LGHomeFeature.isAppSuggestionEnabled() || this.mRecommandLayout == null || this.mActivity.getDeviceProfile().iconSizePx == this.mRecommandLayout.getIconSize()) {
            return;
        }
        post(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$hge08ZaUJNbZSAYX1eoQDURc4nc
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updateRecommandLayoutItems$20$RecentsView();
            }
        });
    }

    public /* synthetic */ void lambda$updateRecommandLayoutItems$20$RecentsView() {
        RecommandAppLayout recommandAppLayout = this.mRecommandLayout;
        if (recommandAppLayout != null) {
            recommandAppLayout.updateItems(true);
        }
    }

    public void removeExcludeApp() {
        TaskView runningTaskView = getRunningTaskView();
        Task task = runningTaskView != null ? runningTaskView.getTask() : null;
        boolean zHasFlagActivityExcludedFromRecents = ActivityManagerWrapperEx.getInstance().hasFlagActivityExcludedFromRecents(task);
        LGLog.i(TAG, "removeExcludeApp: isExcluded = " + zHasFlagActivityExcludedFromRecents + ", " + task);
        if (zHasFlagActivityExcludedFromRecents) {
            dismissTask(runningTaskView, true, false);
            if (getChildCount() == 1) {
                AbstractFloatingView.closeOpenViews(this.mActivity, false, 2048);
            }
        }
    }

    public void updatePadding(Rect newRect, DeviceProfile dp) {
        this.mTempRect.set(newRect);
        this.mTaskWidth = this.mTempRect.width();
        this.mTaskHeight = this.mTempRect.height();
        this.mTempRect.top -= this.mTaskTopMargin;
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        setPadding(this.mTempRect.left - this.mInsets.left, this.mTempRect.top - this.mInsets.top, (dp.widthPx - this.mInsets.right) - this.mTempRect.right, (calculateHeight(dp) - this.mInsets.bottom) - this.mTempRect.bottom);
        LGLog.d(TAG, String.format("[DEVICE_PROFILE] updatePadding: top(%s -> %s), bottom(%s -> %s)", Integer.valueOf(paddingTop), Integer.valueOf(getPaddingTop()), Integer.valueOf(paddingBottom), Integer.valueOf(getPaddingBottom())));
    }

    private int calculateHeight(DeviceProfile dp) {
        int i = dp.heightPx;
        if (!dp.isMultiWindowMode) {
            return i;
        }
        if (SysUINavigationMode.getCurrentMode(getContext()) != SysUINavigationMode.Mode.NO_BUTTON.resValue && dp.isLandscape) {
            return i;
        }
        int i2 = dp.heightPx;
        int navigationSize = SysUINavigationMode.getNavigationSize(getContext());
        return navigationSize + i2;
    }

    public int getmTaskTopMargin() {
        return this.mTaskTopMargin;
    }

    public PendingAnimation createOneTaskDismissAnimation(final TaskView taskView, long duration) {
        PendingAnimation pendingAnimation = new PendingAnimation(duration);
        addDismissedTaskAnimations(taskView, duration, pendingAnimation);
        this.mPendingAnimation = pendingAnimation;
        pendingAnimation.addEndListener(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$j74gv43pGfpwieZnhP467s46AxY
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$createOneTaskDismissAnimation$21$RecentsView(taskView, (PendingAnimation.EndState) obj);
            }
        });
        return pendingAnimation;
    }

    public /* synthetic */ void lambda$createOneTaskDismissAnimation$21$RecentsView(TaskView taskView, PendingAnimation.EndState endState) {
        if (endState.isSuccess) {
            removeTask(taskView, -1, endState);
            removeView(taskView);
        }
        if (getChildCount() == 0) {
            startHome();
        }
        updateClearAllEnabled();
        this.mPendingAnimation = null;
    }

    public void dismissOneTask(TaskView taskView) {
        runDismissAnimation(createOneTaskDismissAnimation(taskView, 300L));
    }

    private static void addAnim(Animator anim, long duration, TimeInterpolator interpolator, PendingAnimation pendingAnimation) {
        anim.setDuration(duration).setInterpolator(interpolator);
        pendingAnimation.add(anim);
    }

    public void onTaskLaunchedAfter(TaskView tv) {
        tv.setHeaderViewVisibility(8, true);
        RecommandAppLayout recommandAppLayout = this.mRecommandLayout;
        if (recommandAppLayout != null) {
            recommandAppLayout.setAlpha(1.0f);
        }
        this.mClearAllButton.setVisibilityAlpha(1.0f);
        if (LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue()) {
            this.mClearAllButton.setScaleX(1.0f);
            this.mClearAllButton.setScaleY(1.0f);
            this.mClearAllButton.setTranslationZ(0.0f);
            RecommandAppLayout recommandAppLayout2 = this.mRecommandLayout;
            if (recommandAppLayout2 != null) {
                recommandAppLayout2.setScaleX(1.0f);
                this.mRecommandLayout.setScaleY(1.0f);
                this.mRecommandLayout.setTranslationZ(0.0f);
            }
        }
    }

    public void setCustomClipBound(float alpha, String caller) {
        if (alpha > 0.0f && (this.mOrientationHandler.getDegreesRotated() == 90.0f || this.mOrientationHandler.getDegreesRotated() == 270.0f)) {
            int i = this.mActivity.getDeviceProfile().widthPx;
            int i2 = this.mActivity.getDeviceProfile().heightPx;
            this.mClipBound.set(0, 0, Math.min(i, i2), (Math.max(i, i2) - this.mRecommandLayoutHeight) - this.mInsets.bottom);
            setClipBounds(this.mClipBound);
        } else {
            setClipBounds(null);
        }
        LGLog.d(TAG, "setCustomClipBound : caller = " + caller + ", recommand alpha = " + alpha + ", mOrientationHandler.getDegreesRotated() = " + this.mOrientationHandler.getDegreesRotated() + ", bound = " + getClipBounds());
    }

    public boolean needToChangeConsumer(float navbarRotation) {
        int i;
        if ((navbarRotation == 0.0f ? this.mOrientationHandler.getDegreesRotated() : -navbarRotation) == 0.0f) {
            i = 0;
        } else {
            i = (navbarRotation == 0.0f || !this.mOrientationState.isMultipleOrientationSupportedByDevice() || this.mOrientationState.getOrientationHandler().isLayoutNaturalToLauncher()) ? 2 : 1;
        }
        int i2 = this.mConsumerId;
        boolean z = i2 != i;
        if (z) {
            LGLog.d(TAG, "needToChangeConsumer : mConsumerId = " + i2 + ", newId = " + i);
        }
        return z;
    }

    public void setAnimRunning(boolean isRunning) {
        this.mIsAnimRunning = isRunning;
    }

    public boolean getAnimRunning() {
        return this.mIsAnimRunning;
    }

    protected void launchSplitSelectedState() {
        BaseActivityInterface baseActivityInterface = this.mSizeStrategy;
        if (baseActivityInterface == null || !baseActivityInterface.getNeedLaunchSplitSelectState()) {
            return;
        }
        Executors.MAIN_EXECUTOR.postDelayed(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$RecentsView$5J7NhybcW_w_KnzoFk4KyzVJ_WY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$launchSplitSelectedState$22$RecentsView();
            }
        }, 500);
        this.mSizeStrategy.setNeedLaunchSplitSelectState(false);
    }

    public /* synthetic */ void lambda$launchSplitSelectedState$22$RecentsView() {
        SplitConfigurationOptions.SplitPositionOption splitPositionOption = getPagedOrientationHandler().getSplitPositionOptions(this.mActivity.getDeviceProfile()).get(0);
        if (splitPositionOption != null) {
            if (getCurrentPageTaskView() != null) {
                getCurrentPageTaskView().initiateSplitSelect(splitPositionOption);
            } else {
                getTaskViewAt(0).initiateSplitSelect(splitPositionOption);
            }
        }
    }

    public boolean getOverviewStateOrSplitSelectedEnable() {
        return this.mOverviewStateEnabled || isOverviewSelectState();
    }
}
