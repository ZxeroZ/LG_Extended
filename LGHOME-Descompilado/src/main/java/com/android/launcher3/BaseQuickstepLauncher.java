package com.android.launcher3;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.WellbeingModel;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.proxy.ProxyActivityStarter;
import com.android.launcher3.proxy.StartActivityParams;
import com.android.launcher3.statehandlers.BackButtonAlphaHandler;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.uioverrides.RecentsViewStateController;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.launcher3.util.UiThreadHelper;
import com.android.quickstep.RecentsModel;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.util.QuickstepOnboardingPrefs;
import com.android.quickstep.util.RemoteAnimationProvider;
import com.android.quickstep.util.RemoteFadeOutAnimationListener;
import com.android.quickstep.util.ShelfPeekAnim;
import com.android.quickstep.util.SplitSelectStateController;
import com.android.quickstep.views.OverviewActionsView;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseQuickstepLauncher extends Launcher implements SysUINavigationMode.NavigationModeChangeListener {
    public static final UiThreadHelper.AsyncCommand SET_BACK_BUTTON_ALPHA = new UiThreadHelper.AsyncCommand() { // from class: com.android.launcher3.-$$Lambda$BaseQuickstepLauncher$f_kzoz-jM-zRC_NNQmaGpAVmGp8
        @Override // com.android.launcher3.util.UiThreadHelper.AsyncCommand
        public final void execute(Context context, int i, int i2) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).lambda$setNavBarButtonAlpha$2$SystemUiProxy(Float.intBitsToFloat(i), i2 != 0);
        }
    };
    private OverviewActionsView mActionsView;
    private QuickstepTransitionManager mAppTransitionManager;
    private DepthController mDepthController = new DepthController(this);
    private final ShelfPeekAnim mShelfPeekAnim = new ShelfPeekAnim(this);

    @Override // com.android.launcher3.Launcher, com.android.launcher3.BaseDraggingActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).addModeChangeListener(this);
        addMultiWindowModeChangedListener(this.mDepthController);
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.BaseDraggingActivity, android.app.Activity
    public void onDestroy() {
        SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).removeModeChangeListener(this);
        super.onDestroy();
    }

    public QuickstepTransitionManager getAppTransitionManager() {
        return this.mAppTransitionManager;
    }

    @Override // com.android.quickstep.SysUINavigationMode.NavigationModeChangeListener
    public void onNavigationModeChanged(SysUINavigationMode.Mode newMode) {
        getDragLayer().recreateControllers();
        if (this.mActionsView == null || !isOverviewActionsEnabled()) {
            return;
        }
        this.mActionsView.updateVerticalMargin(newMode);
    }

    @Override // com.android.launcher3.Launcher, android.app.Activity
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).getThumbnailCache().getHighResLoadingState().setVisible(true);
    }

    @Override // com.android.launcher3.Launcher, android.app.Activity, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).onTrimMemory(level);
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    protected void onUiChangedWhileSleeping() {
        ActivityManagerWrapper.getInstance().invalidateHomeTaskSnapshot(this);
    }

    @Override // com.android.launcher3.Launcher, android.app.Activity
    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) {
        if (requestCode != -1) {
            this.mPendingActivityRequestCode = requestCode;
            StartActivityParams startActivityParams = new StartActivityParams(this, requestCode);
            startActivityParams.intentSender = intent;
            startActivityParams.fillInIntent = fillInIntent;
            startActivityParams.flagsMask = flagsMask;
            startActivityParams.flagsValues = flagsValues;
            startActivityParams.extraFlags = extraFlags;
            startActivityParams.options = options;
            startActivity(ProxyActivityStarter.getLaunchIntent(this, startActivityParams));
            return;
        }
        super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    @Override // android.app.Activity
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (requestCode != -1) {
            this.mPendingActivityRequestCode = requestCode;
            StartActivityParams startActivityParams = new StartActivityParams(this, requestCode);
            startActivityParams.intent = intent;
            startActivityParams.options = options;
            startActivity(ProxyActivityStarter.getLaunchIntent(this, startActivityParams));
            return;
        }
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.statemanager.StatefulActivity
    protected void onDeferredResumed() {
        super.onDeferredResumed();
        if (this.mPendingActivityRequestCode == -1 || !isInState(LauncherState.NORMAL)) {
            return;
        }
        onActivityResult(this.mPendingActivityRequestCode, 0, null);
        startActivity(ProxyActivityStarter.getLaunchIntent(this, null));
    }

    @Override // com.android.launcher3.Launcher
    protected void setupViews() {
        super.setupViews();
        SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).updateMode();
        this.mActionsView = null;
        ((RecentsView) getOverviewPanel()).init(this.mActionsView, new SplitSelectStateController(this, this.mHandler, getStateManager(), getDepthController()));
        isOverviewActionsEnabled();
        this.mAppTransitionManager = new QuickstepTransitionManager(this);
    }

    private boolean isOverviewActionsEnabled() {
        return FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get() && SysUINavigationMode.removeShelfFromOverview(this);
    }

    public <T extends OverviewActionsView> T getActionsView() {
        return (T) this.mActionsView;
    }

    @Override // com.android.launcher3.Launcher
    protected void closeOpenViews(boolean animate) {
        super.closeOpenViews(animate);
        ActivityManagerWrapper.getInstance().closeSystemWindows(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_HOME_KEY);
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.statemanager.StatefulActivity
    protected StateManager.StateHandler<LauncherState>[] createStateHandlers() {
        return (!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() || getCarouselLayout() == null) ? new StateManager.StateHandler[]{getAllAppsController(), getWorkspace(), getDepthController(), new RecentsViewStateController(this), new BackButtonAlphaHandler(this)} : new StateManager.StateHandler[]{getAllAppsController(), getWorkspace(), getDepthController(), new RecentsViewStateController(this), new BackButtonAlphaHandler(this), getCarouselLayout()};
    }

    public DepthController getDepthController() {
        return this.mDepthController;
    }

    @Override // com.android.launcher3.Launcher
    protected OnboardingPrefs createOnboardingPrefs(SharedPreferences sharedPrefs) {
        return new QuickstepOnboardingPrefs(this, sharedPrefs);
    }

    @Override // com.android.launcher3.Launcher
    public void useFadeOutAnimationForLauncherStart(final CancellationSignal signal) {
        getAppTransitionManager().setRemoteAnimationProvider(new RemoteAnimationProvider() { // from class: com.android.launcher3.BaseQuickstepLauncher.1
            @Override // com.android.quickstep.util.RemoteAnimationProvider
            public AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets) {
                signal.cancel();
                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
                valueAnimatorOfFloat.addUpdateListener(new RemoteFadeOutAnimationListener(appTargets, wallpaperTargets));
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(valueAnimatorOfFloat);
                return animatorSet;
            }
        }, signal);
    }

    @Override // com.android.launcher3.Launcher
    public float[] getNormalOverviewScaleAndOffset() {
        return SysUINavigationMode.getMode(this) == SysUINavigationMode.Mode.NO_BUTTON ? new float[]{1.0f, 1.0f} : new float[]{1.1f, 0.0f};
    }

    @Override // com.android.launcher3.Launcher
    public void onDragLayerHierarchyChanged() {
        onLauncherStateOrFocusChanged();
    }

    @Override // com.android.launcher3.BaseActivity
    protected void onActivityFlagsChanged(int changeBits) {
        if ((changeBits & 72) != 0) {
            onLauncherStateOrFocusChanged();
        }
        if ((changeBits & 1) != 0) {
            this.mDepthController.setActivityStarted(isStarted());
        }
        super.onActivityFlagsChanged(changeBits);
    }

    public boolean shouldBackButtonBeHidden(LauncherState toState) {
        boolean z = SysUINavigationMode.getMode(this).hasGestures && toState.hasFlag(LauncherState.FLAG_HIDE_BACK_BUTTON) && hasWindowFocus() && (getActivityFlags() & 64) == 0;
        if (z) {
            return AbstractFloatingView.getTopOpenViewWithType(this, 3607) == null;
        }
        return z;
    }

    private void onLauncherStateOrFocusChanged() {
        boolean zShouldBackButtonBeHidden = shouldBackButtonBeHidden((LauncherState) getStateManager().getState());
        UiThreadHelper.setBackButtonAlphaAsync(getApplicationContext(), SET_BACK_BUTTON_ALPHA, zShouldBackButtonBeHidden ? 0.0f : 1.0f, true);
        if (getDragLayer() != null) {
            getRootView().setDisallowBackGesture(zShouldBackButtonBeHidden);
        }
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.LauncherModel.Callbacks
    public void finishBindingItems() {
        super.finishBindingItems();
    }

    @Override // com.android.launcher3.Launcher
    public Stream<SystemShortcut.Factory> getSupportedShortcuts() {
        return Stream.concat(super.getSupportedShortcuts(), Stream.of(WellbeingModel.SHORTCUT_FACTORY));
    }

    @Override // com.android.launcher3.BaseDraggingActivity
    public ActivityOptionsWrapper getActivityLaunchOptions(View v) {
        ActivityOptionsWrapper activityLaunchOptions;
        if (this.mAppTransitionManager.hasControlRemoteAppTransitionPermission()) {
            activityLaunchOptions = this.mAppTransitionManager.getActivityLaunchOptions(this, v);
        } else {
            activityLaunchOptions = super.getActivityLaunchOptions(v);
        }
        activityLaunchOptions.options.setSplashScreenStyle(0);
        return activityLaunchOptions;
    }

    public ShelfPeekAnim getShelfPeekAnim() {
        return this.mShelfPeekAnim;
    }

    public void setHintUserWillBeActive() {
        addActivityFlags(32);
    }
}
