package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.LGActivityTrigger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAnimationRunner;
import com.android.launcher3.LauncherRootView;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.ActivityTracker;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.fallback.FallbackRecentsStateController;
import com.android.quickstep.fallback.FallbackRecentsView;
import com.android.quickstep.fallback.RecentsDragLayer;
import com.android.quickstep.fallback.RecentsState;
import com.android.quickstep.util.RecentsAtomicAnimationFactory;
import com.android.quickstep.util.SplitSelectStateController;
import com.android.quickstep.views.ClearAllButton;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.RecommandAppLayout;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.RemoteAnimationAdapterCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.initialguide.MultiWindowGuideManager;
import com.lge.launcher3.initialguide.SwivelHomeGuideManager;
import com.lge.launcher3.quickstep.ActivityManagerWrapperEx;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.util.WindowUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class RecentsActivity extends StatefulActivity<RecentsState> {
    public static final ActivityTracker<RecentsActivity> ACTIVITY_TRACKER = new ActivityTracker<>();
    private static final int DELAY_RESET_SPLIT_SELECTION = 300;
    private static final String TAG = "RecentsActivity";
    private LauncherAnimationRunner.RemoteAnimationFactory mActivityLaunchAnimationRunner;
    private RecentsDragLayer mDragLayer;
    private FallbackRecentsView mFallbackRecentsView;
    private Configuration mOldConfig;
    private StateManager<RecentsState> mStateManager;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    LGActivityTrigger mActivityTriggerCallBack = new LGActivityTrigger() { // from class: com.android.quickstep.RecentsActivity.2
        public void activityChanged(int callMethod, Bundle bundle) {
            if (callMethod == 7 || callMethod == 8) {
                String[] strArr = {bundle.getString("NAME")};
                LGLog.d(RecentsActivity.TAG, "activityChanged: " + callMethod + ", " + strArr);
                if (RecentsActivity.this.mFallbackRecentsView != null) {
                    RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(RecentsActivity.this.getApplicationContext()).callOnTaskStackChanged();
                    RecentsActivity.this.mFallbackRecentsView.reloadIfNeeded();
                }
            }
        }
    };
    final BroadcastReceiver mSystemBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.quickstep.RecentsActivity.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context ctx, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.SCREEN_OFF")) {
                RecentsActivity.this.moveTaskToBack(true);
                return;
            }
            if (action.equals("android.intent.action.USER_SWITCHED")) {
                RecentsActivity.this.moveTaskToBack(true);
            } else if (action.equals(LauncherConst.ACTION_CONTROL_DUAL_RECENT)) {
                Utilities.getCoverDisplayState();
                if ((RecentsActivity.this.getRootView() != null ? RecentsActivity.this.getRootView().getContext().getDisplayId() : 0) != 0) {
                    RecentsActivity.this.moveTaskToBack(true);
                }
            }
        }
    };

    protected void setupViews() {
        inflateRootView(R.layout.fallback_recents_activity);
        setContentView(getRootView());
        this.mDragLayer = (RecentsDragLayer) findViewById(R.id.drag_layer);
        this.mFallbackRecentsView = (FallbackRecentsView) findViewById(R.id.overview_panel);
        this.mDragLayer.recreateControllers();
        this.mFallbackRecentsView.setRecommandAppLayout((RecommandAppLayout) findViewById(R.id.recommand_container));
        ClearAllButton clearAllButton = (ClearAllButton) findViewById(R.id.clear_all_button);
        clearAllButton.setVisibility(0);
        this.mFallbackRecentsView.setClearAllButton(clearAllButton);
        this.mFallbackRecentsView.init(null, new SplitSelectStateController(this, this.mHandler, getStateManager(), null));
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        onHandleConfigChanged();
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ACTIVITY_TRACKER.handleNewIntent(this, intent);
    }

    protected void onHandleConfigChanged() {
        this.mUserEventDispatcher = null;
        initDeviceProfile();
        AbstractFloatingView.closeOpenViews(this, true, 3983);
        dispatchDeviceProfileChanged();
        reapplyUi();
        LauncherRootView rootView = getRootView();
        if (rootView != null) {
            rootView.postInvalidate();
        }
        this.mDragLayer.recreateControllers();
    }

    protected DeviceProfile createDeviceProfile() {
        DeviceProfile deviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).getDeviceProfile(this);
        if (this.mDragLayer != null && isInMultiWindowMode()) {
            return deviceProfile.getMultiWindowProfile(this, getMultiWindowDisplaySize());
        }
        return deviceProfile.copy(this);
    }

    @Override // com.android.launcher3.views.ActivityContext
    public BaseDragLayer getDragLayer() {
        return this.mDragLayer;
    }

    @Override // com.android.launcher3.BaseDraggingActivity
    public <T extends View> T getOverviewPanel() {
        return this.mFallbackRecentsView;
    }

    @Override // com.android.launcher3.BaseDraggingActivity
    public void returnToHomescreen() {
        super.returnToHomescreen();
    }

    @Override // com.android.launcher3.BaseDraggingActivity
    public ActivityOptionsWrapper getActivityLaunchOptions(final View v) {
        if (!(v instanceof TaskView)) {
            return super.getActivityLaunchOptions(v);
        }
        final TaskView taskView = (TaskView) v;
        final RunnableList runnableList = new RunnableList();
        this.mActivityLaunchAnimationRunner = new LauncherAnimationRunner.RemoteAnimationFactory() { // from class: com.android.quickstep.-$$Lambda$RecentsActivity$EKT-ij3m1gjeFGw7HjlC0eVonyA
            @Override // com.android.launcher3.LauncherAnimationRunner.RemoteAnimationFactory
            /* JADX INFO: renamed from: onCreateAnimation */
            public final void lambda$onCreateAnimation$0$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, LauncherAnimationRunner.AnimationResult animationResult) {
                this.f$0.lambda$getActivityLaunchOptions$0$RecentsActivity(taskView, runnableList, i, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, animationResult);
            }
        };
        ActivityOptionsWrapper activityOptionsWrapper = new ActivityOptionsWrapper(ActivityOptionsCompat.makeRemoteAnimation(new RemoteAnimationAdapterCompat(new LauncherAnimationRunner(this.mUiHandler, this.mActivityLaunchAnimationRunner, true), 336L, 120L, getIApplicationThread())), runnableList);
        activityOptionsWrapper.options.setSplashScreenStyle(0);
        return activityOptionsWrapper;
    }

    public /* synthetic */ void lambda$getActivityLaunchOptions$0$RecentsActivity(TaskView taskView, final RunnableList runnableList, int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, LauncherAnimationRunner.AnimationResult animationResult) {
        AnimatorSet animatorSetComposeRecentsLaunchAnimator = composeRecentsLaunchAnimator(taskView, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2);
        animatorSetComposeRecentsLaunchAnimator.addListener(resetStateListener());
        Objects.requireNonNull(runnableList);
        animationResult.setAnimation(animatorSetComposeRecentsLaunchAnimator, this, new Runnable() { // from class: com.android.quickstep.-$$Lambda$hQD1JeGZDm5kxmTgspSnjaDiUIY
            @Override // java.lang.Runnable
            public final void run() {
                runnableList.executeAllAndDestroy();
            }
        }, true);
    }

    private AnimatorSet composeRecentsLaunchAnimator(TaskView taskView, RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets) {
        AnimatorSet animatorSet = new AnimatorSet();
        if (taskView == null) {
            return animatorSet;
        }
        boolean zTaskIsATargetWithMode = TaskUtils.taskIsATargetWithMode(appTargets, getTaskId(), 1);
        PendingAnimation pendingAnimation = new PendingAnimation(336L);
        TaskViewUtils.createRecentsWindowAnimator(taskView, !zTaskIsATargetWithMode, appTargets, wallpaperTargets, null, pendingAnimation);
        animatorSet.play(pendingAnimation.buildAnim());
        if (zTaskIsATargetWithMode) {
            AnimatorSet animatorSetCreateAdjacentPageAnimForTaskLaunch = this.mFallbackRecentsView.createAdjacentPageAnimForTaskLaunch(taskView);
            animatorSetCreateAdjacentPageAnimForTaskLaunch.setInterpolator(Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            animatorSetCreateAdjacentPageAnimForTaskLaunch.setDuration(336L);
            animatorSetCreateAdjacentPageAnimForTaskLaunch.addListener(resetStateListener());
            animatorSet.play(animatorSetCreateAdjacentPageAnimForTaskLaunch);
        }
        return animatorSet;
    }

    @Override // com.android.launcher3.BaseDraggingActivity, com.android.launcher3.BaseActivity, android.app.Activity
    protected void onStart() {
        this.mFallbackRecentsView.setContentAlpha(1.0f);
        super.onStart();
        getRootView().setBackgroundColor(SysUINavigationMode.getMode(getApplicationContext()).hasGestures ? 0 : getColor(R.color.wallpaper_blur_dim_color));
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity, com.android.launcher3.BaseActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        getRootView().setBackgroundColor(0);
        onTrimMemory(20);
        MultiWindowGuideManager.getInstance(getApplicationContext()).hideGuide();
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            SwivelHomeGuideManager.getInstance(getApplicationContext()).hideGuide();
        }
        AbstractFloatingView.closeAllOpenViews(this);
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity, com.android.launcher3.BaseDraggingActivity, com.android.launcher3.BaseActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        AccessibilityManagerCompat.sendStateEventToTest(getBaseContext(), 2);
        WindowUtils.addFlagForFreeform(getWindow(), true);
        AbstractFloatingView.closeAllOpenViews(this);
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        WindowUtils.addFlagForFreeform(getWindow(), false);
        AbstractFloatingView.closeAllOpenViews(this);
        Executors.MAIN_EXECUTOR.postDelayed(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsActivity$Bk8NFCYmg4gAhz4ezJox5vzp8m4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onPause$1$RecentsActivity();
            }
        }, ((RecentsState) getStateManager().getState()).getTransitionDuration(this) + 300);
    }

    public /* synthetic */ void lambda$onPause$1$RecentsActivity() {
        FallbackRecentsView fallbackRecentsView = this.mFallbackRecentsView;
        if (fallbackRecentsView != null) {
            fallbackRecentsView.resetFromSplitSelectionState();
        }
    }

    @Override // com.android.launcher3.BaseDraggingActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getDisplayId() == 4) {
            setRequestedOrientation(-1);
        }
        this.mStateManager = new StateManager<>(this, RecentsState.DEFAULT);
        this.mOldConfig = new Configuration(getResources().getConfiguration());
        initDeviceProfile();
        setupViews();
        ActivityManagerWrapperEx.getInstance().registerLGActivityTrigger(this.mActivityTriggerCallBack);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction(LauncherConst.ACTION_CONTROL_DUAL_RECENT);
        registerReceiver(this.mSystemBroadcastReceiver, intentFilter);
        getSystemUiController().updateUiState(0, Themes.getAttrBoolean(this, R.attr.isWorkspaceDarkText));
        ACTIVITY_TRACKER.handleCreate(this);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        RecommandAppLayout recommandLayout;
        FallbackRecentsView fallbackRecentsView;
        int iDiff = newConfig.diff(this.mOldConfig);
        if ((iDiff & 1152) != 0) {
            onHandleConfigChanged();
        }
        if ((iDiff & 128) != 0 && (fallbackRecentsView = this.mFallbackRecentsView) != null && fallbackRecentsView.getNativeScroller() != null && !this.mFallbackRecentsView.getNativeScroller().isFinished()) {
            this.mFallbackRecentsView.getNativeScroller().forceFinished(true);
        }
        FallbackRecentsView fallbackRecentsView2 = this.mFallbackRecentsView;
        if (fallbackRecentsView2 != null && (recommandLayout = fallbackRecentsView2.getRecommandLayout()) != null) {
            if (getDeviceProfile().mDisplayId == 4 || !LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue()) {
                recommandLayout.setVisibility(8);
            } else {
                recommandLayout.setVisibility(0);
                if (getDeviceProfile().isMultiWindowMode) {
                    RecentsView.RECOMMAND_APP_ALPHA.setValue(this.mFallbackRecentsView, 0.0f);
                }
            }
        }
        this.mOldConfig.setTo(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    private void initDeviceProfile() {
        this.mDeviceProfile = createDeviceProfile();
        onDeviceProfileInitiated();
    }

    @Override // android.app.Activity
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).getThumbnailCache().getHighResLoadingState().setVisible(true);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).onTrimMemory(level);
    }

    @Override // com.android.launcher3.BaseDraggingActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        ActivityManagerWrapperEx.getInstance().unRegisterLGActivityTrigger(this.mActivityTriggerCallBack);
        unregisterReceiver(this.mSystemBroadcastReceiver);
        ACTIVITY_TRACKER.onActivityDestroyed(this);
        this.mActivityLaunchAnimationRunner = null;
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        startHome();
    }

    public void startHome() {
        if (getDisplayId() == 0) {
            startActivity(new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME).setFlags(268435456));
        } else {
            ActivityManagerWrapperEx.getInstance().startMultiDisplayHomeAsDisplayId(getDisplayId());
        }
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    protected StateManager.StateHandler<RecentsState>[] createStateHandlers() {
        return new StateManager.StateHandler[]{new FallbackRecentsStateController(this)};
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    public StateManager<RecentsState> getStateManager() {
        return this.mStateManager;
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.println(prefix + "Misc:");
        dumpMisc(prefix + "\t", writer);
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    public StateManager.AtomicAnimationFactory<RecentsState> createAtomicAnimationFactory() {
        return new RecentsAtomicAnimationFactory(this, 0);
    }

    private AnimatorListenerAdapter resetStateListener() {
        return new AnimatorListenerAdapter() { // from class: com.android.quickstep.RecentsActivity.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                RecentsActivity.this.mFallbackRecentsView.resetTaskVisuals();
                RecentsActivity.this.mStateManager.reapplyState();
            }
        };
    }
}
