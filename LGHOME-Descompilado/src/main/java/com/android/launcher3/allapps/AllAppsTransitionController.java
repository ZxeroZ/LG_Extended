package com.android.launcher3.allapps;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.FloatProperty;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.anim.SpringObjectAnimator;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.ScrimView;
import com.android.systemui.plugins.AllAppsSearchPlugin;
import com.android.systemui.plugins.PluginListener;
import com.lge.launcher3.LauncherExtension;
import com.lge.launcher3.R;
import com.lge.launcher3.adaptive.AdaptiveTextManager;
import com.lge.launcher3.adaptive.AdaptiveTextUtil;
import com.lge.launcher3.allapps.AllAppsHost;
import com.lge.launcher3.uioverrides.InAppsState;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.util.WindowUtils;
import com.lge.launcher3.wallpaperblur.CustomBlurView;
import com.lge.launcher3.wallpaperblur.HomescreenBlurManager;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsTransitionController implements StateManager.StateHandler<LauncherState>, DeviceProfile.OnDeviceProfileChangeListener, PluginListener<AllAppsSearchPlugin> {
    private static final int APPS_VIEW_ALPHA_CHANNEL_INDEX = 0;
    public static final float SPRING_DAMPING_RATIO = 0.9f;
    public static final float SPRING_STIFFNESS = 600.0f;
    public static String TAG = "AllAppsTransitionController";
    private AllAppsHost mAppsView;
    private View mBlurBackgroundView;
    private final boolean mIsDarkTheme;
    private boolean mIsVerticalLayout;
    private final Launcher mLauncher;
    public int mMaxDimAlpha;
    private AllAppsSearchPlugin mPlugin;
    private View mPluginContent;
    private ScrimView mScrimView;
    private float mShiftRangeOfNative;
    private LauncherState mToState;
    public static final FloatProperty<AllAppsTransitionController> ALL_APPS_PROGRESS = new FloatProperty<AllAppsTransitionController>("allAppsProgress") { // from class: com.android.launcher3.allapps.AllAppsTransitionController.1
        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(AllAppsTransitionController controller) {
            return Float.valueOf(controller.mProgress);
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(AllAppsTransitionController controller, float progress) {
            controller.setProgress(progress);
        }
    };
    public static final FloatProperty<AllAppsTransitionController> BLUR_PROGRESS = new FloatProperty<AllAppsTransitionController>("blurProgress") { // from class: com.android.launcher3.allapps.AllAppsTransitionController.5
        boolean DEBUG = false;

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(AllAppsTransitionController controller, float progress) {
            if (this.DEBUG) {
                LGLog.d(AllAppsTransitionController.TAG, "BLUR_PROGRESS : " + progress);
            }
            controller.setBlurBackgroundAlpha(progress);
            if (controller.mBlurBackgroundView instanceof CustomBlurView) {
                ((CustomBlurView) controller.mBlurBackgroundView).setBackgroundAlpha(controller.mEnableDim ? (controller.mMaxDimAlpha / 100.0f) * progress : 0.0f);
            }
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(AllAppsTransitionController controller) {
            return Float.valueOf(controller.getBlurProgress());
        }
    };
    public final float OVERVIEW_NORMAL_SCALE = 1.0f;
    public final float OVERVIEW_EXPAND_SCALE = 1.1f;
    public final int OVERVIEW_EXPAND_DURATION = 400;
    public final float ALL_APPS_ANI_INIT_SCALE = 0.92f;
    private float mScrollRangeDelta = 0.0f;
    private boolean mEnableDim = true;
    private float mShiftRange = getModifiedShiftRange();
    private float mProgress = 1.0f;

    public AllAppsTransitionController(Launcher l) {
        this.mLauncher = l;
        this.mShiftRangeOfNative = l.getDeviceProfile().heightPx;
        this.mMaxDimAlpha = l.getResources().getInteger(R.integer.config_workspaceScrimAlpha);
        this.mIsDarkTheme = Themes.getAttrBoolean(l, R.attr.isMainColorDark);
        this.mIsVerticalLayout = l.getDeviceProfile().isVerticalBarLayout();
        l.addOnDeviceProfileChangeListener(this);
    }

    public float getShiftRangeOfNative() {
        return this.mShiftRangeOfNative;
    }

    public float getShiftRange() {
        return this.mShiftRange;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onProgressAnimationStart() {
        LGLog.d(TAG, "onProgressAnimationStart");
        AllAppsHost allAppsHost = this.mAppsView;
        if (allAppsHost != null) {
            allAppsHost.onPause();
            this.mAppsView.onLauncherTransitionPrepare(this.mLauncher, false, false);
        }
        this.mLauncher.cancelWorkspaceLongpress();
    }

    @Override // com.android.launcher3.DeviceProfile.OnDeviceProfileChangeListener
    public void onDeviceProfileChanged(DeviceProfile dp) {
        this.mIsVerticalLayout = dp.isVerticalBarLayout();
        setScrollRangeDelta(this.mScrollRangeDelta);
        if (this.mIsVerticalLayout) {
            AllAppsHost allAppsHost = this.mAppsView;
            if (allAppsHost != null) {
                allAppsHost.setAlpha(1.0f);
            }
            Launcher launcher = this.mLauncher;
            if (launcher != null) {
                if (launcher.getHotseat() != null) {
                    this.mLauncher.getHotseat().setTranslationY(0.0f);
                }
                if (this.mLauncher.getWorkspace() == null || this.mLauncher.getWorkspace().getPageIndicator() == null) {
                    return;
                }
                this.mLauncher.getWorkspace().getPageIndicator().setTranslationY(0.0f);
            }
        }
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        float f = this.mShiftRange * progress;
        if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
            float f2 = progress * (this.mLauncher.getDeviceProfile().heightPx / 20.0f);
            AllAppsHost allAppsHost = this.mAppsView;
            if (allAppsHost == null || allAppsHost.getAppContainerView() == null) {
                return;
            }
            this.mAppsView.getAppContainerView().setTranslationY(f2);
            return;
        }
        AllAppsHost allAppsHost2 = this.mAppsView;
        if (allAppsHost2 != null) {
            allAppsHost2.setTranslationY(f);
        }
    }

    public float getProgress() {
        return this.mProgress;
    }

    /* JADX DEBUG: Method merged with bridge method: setState(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setState(LauncherState state) {
        this.mToState = state;
        if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
            setAdaptiveSystemUi(this.mProgress);
        }
        setProgress(state.getVerticalProgress(this.mLauncher));
        setAlphas(state, new StateAnimationConfig(), PropertySetter.NO_ANIM_PROPERTY_SETTER);
        onProgressAnimationEnd();
    }

    /* JADX DEBUG: Method merged with bridge method: setStateWithAnimation(Ljava/lang/Object;Lcom/android/launcher3/states/StateAnimationConfig;Lcom/android/launcher3/anim/PendingAnimation;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setStateWithAnimation(LauncherState toState, StateAnimationConfig config, PendingAnimation builder) {
        Interpolator interpolator;
        this.mToState = toState;
        float verticalProgress = toState.getVerticalProgress(this.mLauncher);
        if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
            setAdaptiveSystemUi(verticalProgress);
        }
        this.mEnableDim = !(toState instanceof InAppsState);
        this.mToState.skipAtomicAnim = false;
        if (Float.compare(this.mProgress, verticalProgress) == 0) {
            if (!config.onlyPlayAtomicComponent()) {
                setAlphas(toState, config, builder);
                if (LGHomeFeature.isOverviewNewUIReactiveAnimationEnable()) {
                    setScales(toState, config, builder);
                }
            }
            onProgressAnimationEnd();
            return;
        }
        if (config.onlyPlayAtomicComponent()) {
            return;
        }
        if (config.userControlled) {
            interpolator = Interpolators.LINEAR;
        } else if (toState == LauncherState.OVERVIEW) {
            interpolator = config.getInterpolator(6, Interpolators.FAST_OUT_SLOW_IN);
        } else {
            interpolator = Interpolators.FAST_OUT_SLOW_IN;
        }
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || !LGHomeFeature.isEnableDefaultHome()) {
            Animator animatorCreateSpringAnimation = createSpringAnimation(this.mProgress, verticalProgress);
            animatorCreateSpringAnimation.setDuration(config.duration);
            animatorCreateSpringAnimation.setInterpolator(config.getInterpolator(0, interpolator));
            animatorCreateSpringAnimation.addListener(getProgressAnimatorListener());
            builder.add(animatorCreateSpringAnimation);
        }
        setAlphas(toState, config, builder);
    }

    public Animator createSpringAnimation(float... progressValues) {
        LGLog.d(TAG, "createSpringAnimation : " + progressValues);
        if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
            return new SpringObjectAnimator(this, ALL_APPS_PROGRESS, 1.0f / this.mShiftRange, 1.0f, 10000.0f, progressValues);
        }
        return new SpringObjectAnimator(this, ALL_APPS_PROGRESS, 1.0f / this.mShiftRange, 0.9f, 600.0f, progressValues);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x000e, code lost:
    
        if (r2 != 16) goto L16;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private android.view.animation.Interpolator getInterpolator(int r2) {
        /*
            r1 = this;
            if (r2 == 0) goto L22
            r0 = 3
            if (r2 == r0) goto L1f
            r0 = 6
            if (r2 == r0) goto L11
            r0 = 15
            if (r2 == r0) goto L22
            r0 = 16
            if (r2 == r0) goto L22
            goto L1c
        L11:
            com.lge.launcher3.util.LGHomeFeature$Config r2 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION
            boolean r2 = r2.getValue()
            if (r2 == 0) goto L1c
            android.view.animation.Interpolator r2 = com.android.launcher3.anim.Interpolators.ACCEL_1_5
            return r2
        L1c:
            android.view.animation.Interpolator r2 = com.android.launcher3.anim.Interpolators.DEACCEL_7
            return r2
        L1f:
            android.view.animation.Interpolator r2 = com.android.launcher3.anim.Interpolators.DEACCEL_3
            return r2
        L22:
            android.view.animation.Interpolator r2 = com.android.launcher3.anim.Interpolators.ACCEL_2
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.AllAppsTransitionController.getInterpolator(int):android.view.animation.Interpolator");
    }

    public void setAlphas(LauncherState state, StateAnimationConfig config, PropertySetter setter) {
        if (!state.skipAtomicAnim) {
            if (config == null) {
                LGLog.w(TAG, "State animation config is null", new int[0]);
            }
            setter.setFloat(this, BLUR_PROGRESS, state.useBlur ? 1.0f : 0.0f, getInterpolator(state.ordinal));
        }
        state.skipAtomicAnim = false;
        int visibleElements = state.getVisibleElements(this.mLauncher);
        int i = visibleElements & 8;
        Launcher launcher = this.mLauncher;
        boolean z = ((visibleElements & 16) == 0 || (launcher != null && launcher.getWorkspace() != null && this.mLauncher.getWorkspace().getOpenFolder() != null)) ? false : true;
        boolean z2 = (visibleElements & 30) != 0;
        Interpolator interpolator = config.getInterpolator(10, Interpolators.LINEAR);
        config.getInterpolator(12, interpolator);
        if (this.mPlugin == null) {
            setter.setViewAlpha(this.mAppsView.getContentView(), z ? 1.0f : 0.0f, interpolator);
            if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
                setter.setViewAlpha(this.mAppsView.getAppContainerView(), z ? 1.0f : 0.0f, interpolator);
                setter.setViewAlpha(this.mAppsView.getSearchView(), z ? 1.0f : 0.0f, interpolator);
            }
        } else {
            setter.setViewAlpha(this.mPluginContent, z ? 1.0f : 0.0f, interpolator);
            setter.setViewAlpha(this.mAppsView.getContentView(), 0.0f, interpolator);
            if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
                setter.setViewAlpha(this.mAppsView.getAppContainerView(), 0.0f, interpolator);
                setter.setViewAlpha(this.mAppsView.getSearchView(), 0.0f, interpolator);
            }
        }
        if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
            setter.setFloat(this.mAppsView.getAppContainerView(), LauncherAnimUtils.SCALE_PROPERTY, z ? 1.0f : 0.92f, config.getInterpolator(15, Interpolators.LINEAR));
        }
        setter.setInt(this.mScrimView, ScrimView.DRAG_HANDLE_ALPHA, (visibleElements & 32) != 0 ? 255 : 0, interpolator);
        setter.setViewAlpha(this.mAppsView, z ? 1.0f : 0.0f, z2 ? Interpolators.INSTANT : Interpolators.FINAL_FRAME);
    }

    private void setAdaptiveSystemUi(float progress) {
        if (Float.compare(progress, 1.0f) == 0) {
            AdaptiveTextUtil.setAdaptiveSystemUi(this.mLauncher.getWindow().getDecorView(), this.mLauncher, this.mToState == LauncherState.NORMAL);
        } else if (Float.compare(progress, 0.0f) == 0) {
            AdaptiveTextUtil.setAdaptiveSystemUi(this.mLauncher.getWindow().getDecorView(), this.mLauncher, false);
        }
    }

    private void setScales(LauncherState toState, StateAnimationConfig config, PendingAnimation builder) {
        final CustomBlurView customBlurView = (CustomBlurView) this.mBlurBackgroundView;
        if (customBlurView == null) {
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        if (toState == LauncherState.OVERVIEW_PEEK) {
            animatorSet.playTogether(ObjectAnimator.ofFloat(customBlurView, "scaleX", 1.0f, 1.1f), ObjectAnimator.ofFloat(customBlurView, "scaleY", 1.0f, 1.1f));
            animatorSet.setDuration(400L);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.allapps.AllAppsTransitionController.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    CustomBlurView customBlurView2 = customBlurView;
                    if (customBlurView2 != null) {
                        customBlurView2.setScaleX(1.1f);
                        customBlurView.setScaleY(1.1f);
                    }
                }
            });
            animatorSet.start();
            return;
        }
        if (toState == LauncherState.NORMAL) {
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(customBlurView, "scaleX", 1.1f, 1.0f);
            ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(customBlurView, "scaleY", 1.1f, 1.0f);
            animatorSet.setDuration(400L);
            animatorSet.playTogether(objectAnimatorOfFloat, objectAnimatorOfFloat2);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.allapps.AllAppsTransitionController.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    CustomBlurView customBlurView2 = customBlurView;
                    if (customBlurView2 != null) {
                        customBlurView2.setScaleX(1.0f);
                        customBlurView.setScaleY(1.0f);
                    }
                }
            });
            animatorSet.start();
        }
    }

    public AnimatorListenerAdapter getProgressAnimatorListener() {
        return new AnimationSuccessListener() { // from class: com.android.launcher3.allapps.AllAppsTransitionController.4
            @Override // com.android.launcher3.anim.AnimationSuccessListener
            public void onAnimationSuccess(Animator animator) {
                AllAppsTransitionController.this.onProgressAnimationEnd();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                AllAppsTransitionController.this.onProgressAnimationStart();
            }
        };
    }

    public void setupViews(AllAppsHost appsView) {
        this.mAppsView = appsView;
        this.mScrimView = (ScrimView) this.mLauncher.findViewById(R.id.scrim_view);
    }

    public void setScrollRangeDelta(float delta) {
        this.mScrollRangeDelta = delta;
        this.mShiftRange = getModifiedShiftRange() - this.mScrollRangeDelta;
        this.mShiftRangeOfNative = this.mLauncher.getDeviceProfile().heightPx;
        ScrimView scrimView = this.mScrimView;
        if (scrimView != null) {
            scrimView.reInitUi();
        }
    }

    private float getModifiedShiftRange() {
        return (this.mLauncher.getDeviceProfile().heightPx - WindowUtils.getNavigationBarHeight(this.mLauncher)) - this.mLauncher.getDeviceProfile().hotseatBarSizePx;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onProgressAnimationEnd() {
        if (Float.compare(this.mProgress, 1.0f) == 0) {
            AllAppsHost allAppsHost = this.mAppsView;
            if (allAppsHost != null) {
                allAppsHost.onLauncherTransitionEnd(this.mLauncher, true, true);
            }
            if (!LGHomeFeature.isDisableAllApps() && this.mToState != LauncherState.NORMAL) {
                ((LauncherExtension) this.mLauncher).getSwipeUpGuideAnimation().cancelSwipeUpAnim();
            }
            if (!LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
                AdaptiveTextUtil.setAdaptiveSystemUi(this.mLauncher.getWindow().getDecorView(), this.mLauncher, this.mToState == LauncherState.NORMAL);
            }
            if (Utilities.isLGUI8_0() && this.mToState == LauncherState.NORMAL && this.mLauncher.getWorkspace() != null) {
                if (this.mLauncher.getWorkspace().hasCustomContent() && this.mLauncher.getWorkspace().getCurrentPage() == 0) {
                    AdaptiveTextUtil.adaptiveNavigationBarLight(this.mLauncher.getWorkspace());
                    return;
                } else {
                    AdaptiveTextUtil.adaptiveNavigationBar(this.mLauncher.getWorkspace(), AdaptiveTextManager.getAdaptiveTextColor());
                    return;
                }
            }
            return;
        }
        if (Float.compare(this.mProgress, 0.0f) == 0) {
            if (this.mAppsView != null) {
                if (this.mLauncher.getWorkspace() != null && this.mLauncher.getWorkspace().getOpenFolder() == null) {
                    this.mAppsView.setChildVisible();
                }
                this.mAppsView.onLauncherTransitionEnd(this.mLauncher, true, false);
            }
            Workspace workspace = this.mLauncher.getWorkspace();
            if (!LGHomeFeature.isDisableAllApps() && workspace != null) {
                ((LauncherExtension) this.mLauncher).getSwipeUpGuideAnimation().cancelSwipeUpAnim();
                workspace.updateSwipeUpCount();
            }
            if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
                return;
            }
            AdaptiveTextUtil.setAdaptiveSystemUi(this.mLauncher.getWindow().getDecorView(), this.mLauncher, false);
            return;
        }
        if (this.mAppsView == null || !this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            return;
        }
        this.mAppsView.setChildVisible();
        this.mAppsView.onLauncherTransitionEnd(this.mLauncher, true, false);
    }

    /* JADX DEBUG: Method merged with bridge method: onPluginConnected(Lcom/android/systemui/plugins/Plugin;Landroid/content/Context;)V */
    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginConnected(AllAppsSearchPlugin plugin, Context context) {
        this.mPlugin = plugin;
        View viewInflate = this.mLauncher.getLayoutInflater().inflate(R.layout.all_apps_content_layout, (ViewGroup) this.mAppsView, false);
        this.mPluginContent = viewInflate;
        this.mAppsView.addView(viewInflate);
        this.mPluginContent.setAlpha(0.0f);
        this.mPlugin.setup((ViewGroup) this.mPluginContent, this.mLauncher, this.mShiftRange);
    }

    /* JADX DEBUG: Method merged with bridge method: onPluginDisconnected(Lcom/android/systemui/plugins/Plugin;)V */
    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginDisconnected(AllAppsSearchPlugin plugin) {
        this.mPlugin = null;
        this.mAppsView.removeView(this.mPluginContent);
    }

    public void onDragStart(boolean toAllApps) {
        AllAppsSearchPlugin allAppsSearchPlugin = this.mPlugin;
        if (allAppsSearchPlugin == null) {
            return;
        }
        allAppsSearchPlugin.onDragStart(toAllApps ? 1.0f : 0.0f);
    }

    public float getBlurProgress() {
        View view = this.mBlurBackgroundView;
        if (view == null) {
            return 0.0f;
        }
        return view.getAlpha();
    }

    public void setBlurBackgroundAlpha(float alpha) {
        HomescreenBlurManager homescreenBlurManager = HomescreenBlurManager.getInstance(this.mLauncher);
        if (this.mLauncher.getWorkspace() != null && this.mLauncher.getWorkspace().getOpenFolder() != null) {
            LGLog.i(TAG, "skip setBlurBackgroundAlpha because folder is opened. alpha = " + alpha);
            return;
        }
        boolean value = LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue();
        if (StaticBlurEngine.getInstance().isPowerSaveEnabled(this.mLauncher)) {
            value = false;
        }
        View view = this.mBlurBackgroundView;
        int visibility = view != null ? view.getVisibility() : -1;
        if (visibility == 8 && alpha == 0.0f) {
            return;
        }
        if (this.mBlurBackgroundView == null || visibility != 0) {
            homescreenBlurManager.showBackgroundWithNoAnim(HomescreenBlurManager.BackgroundType.MIDDLE_ROOTVIEW);
            View backgroundView = homescreenBlurManager.getBackgroundView();
            this.mBlurBackgroundView = backgroundView;
            if (backgroundView != null && backgroundView.getVisibility() != 0) {
                if (!value) {
                    homescreenBlurManager.updateBackgroundViewContents();
                } else {
                    LGLog.i(TAG, String.format("Alpha Blur Thredhold %f", Float.valueOf(0.3f)));
                    homescreenBlurManager.updateBackgroundViewContents(0.3f);
                }
                this.mBlurBackgroundView.setVisibility(0);
            }
        }
        View view2 = this.mBlurBackgroundView;
        if (view2 != null) {
            if (!value) {
                view2.setAlpha(alpha);
            } else if (alpha <= 0.3f) {
                view2.setAlpha(3.3333333f * alpha);
            } else {
                view2.setAlpha(1.0f);
                homescreenBlurManager.updateBackgroundViewContents(alpha);
            }
            if (Float.compare(alpha, 0.0f) == 0 && this.mLauncher.getStateManager().getState() == LauncherState.NORMAL) {
                this.mBlurBackgroundView.setVisibility(8);
            }
        }
    }
}
