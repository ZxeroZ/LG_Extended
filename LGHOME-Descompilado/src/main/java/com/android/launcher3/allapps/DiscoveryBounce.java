package com.android.launcher3.allapps;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.UserManager;
import android.view.MotionEvent;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.states.InternalStateHandler;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.OnboardingPrefs;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class DiscoveryBounce extends AbstractFloatingView {
    public static final int BOUNCE_MAX_COUNT = 3;
    private static final long DELAY_MS = 450;
    public static final String HOME_BOUNCE_COUNT = "launcher.home_bounce_count";
    public static final String HOME_BOUNCE_SEEN = "launcher.apps_view_shown";
    public static final String SHELF_BOUNCE_COUNT = "launcher.shelf_bounce_count";
    public static final String SHELF_BOUNCE_SEEN = "launcher.shelf_bounce_seen";
    private final Animator mDiscoBounceAnimation;
    private final Launcher mLauncher;

    private static boolean shouldShowForWorkProfile(Launcher launcher) {
        return false;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 64) != 0;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
    }

    public DiscoveryBounce(Launcher launcher, float delta) {
        super(launcher, null);
        this.mLauncher = launcher;
        AllAppsTransitionController allAppsController = launcher.getAllAppsController();
        Animator animatorLoadAnimator = AnimatorInflater.loadAnimator(launcher, R.animator.discovery_bounce);
        this.mDiscoBounceAnimation = animatorLoadAnimator;
        animatorLoadAnimator.setTarget(new VerticalProgressWrapper(allAppsController, delta));
        animatorLoadAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.allapps.DiscoveryBounce.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                DiscoveryBounce.this.handleClose(false);
            }
        });
        animatorLoadAnimator.addListener(allAppsController.getProgressAnimatorListener());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mDiscoBounceAnimation.start();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mDiscoBounceAnimation.isRunning()) {
            this.mDiscoBounceAnimation.end();
        }
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public boolean onBackPressed() {
        super.onBackPressed();
        return false;
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        handleClose(false);
        return false;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        if (this.mIsOpen) {
            this.mIsOpen = false;
            this.mLauncher.getDragLayer().removeView(this);
            this.mLauncher.getAllAppsController().setProgress(((LauncherState) this.mLauncher.getStateManager().getState()).getVerticalProgress(this.mLauncher));
        }
    }

    private void show(int containerType) {
        this.mIsOpen = true;
        this.mLauncher.getDragLayer().addView(this);
    }

    public static void showForHomeIfNeeded(Launcher launcher) {
        showForHomeIfNeeded(launcher, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showForHomeIfNeeded(final Launcher launcher, boolean withDelay) {
        if (!launcher.isInState(LauncherState.NORMAL) || launcher.getSharedPrefs().getBoolean("launcher.apps_view_shown", false) || AbstractFloatingView.getTopOpenView(launcher) != null || UserManagerCompat.getInstance(launcher).isDemoUser() || ActivityManager.isRunningInTestHarness()) {
            return;
        }
        if (withDelay) {
            new Handler().postDelayed(new Runnable() { // from class: com.android.launcher3.allapps.-$$Lambda$DiscoveryBounce$bCHUhoKUJRagTYcvEt7yTLtOYlg
                @Override // java.lang.Runnable
                public final void run() {
                    DiscoveryBounce.showForHomeIfNeeded(launcher, false);
                }
            }, DELAY_MS);
        } else {
            new DiscoveryBounce(launcher, 0.0f).show(2);
        }
    }

    public static void showForOverviewIfNeeded(Launcher launcher, PagedOrientationHandler orientationHandler) {
        showForOverviewIfNeeded(launcher, true, orientationHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showForOverviewIfNeeded(final Launcher launcher, boolean withDelay, final PagedOrientationHandler orientationHandler) {
        OnboardingPrefs onboardingPrefs = launcher.getOnboardingPrefs();
        if (!launcher.isInState(LauncherState.OVERVIEW) || !launcher.hasBeenResumed() || launcher.isForceInvisible() || launcher.getDeviceProfile().isVerticalBarLayout() || !orientationHandler.isLayoutNaturalToLauncher() || onboardingPrefs.getBoolean("launcher.shelf_bounce_seen") || ((UserManager) launcher.getSystemService(UserManager.class)).isDemoUser() || Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            return;
        }
        if (withDelay) {
            new Handler().postDelayed(new Runnable() { // from class: com.android.launcher3.allapps.-$$Lambda$DiscoveryBounce$-nvyAjMKl2Nws9IcHEeAYz6__qM
                @Override // java.lang.Runnable
                public final void run() {
                    DiscoveryBounce.showForOverviewIfNeeded(launcher, false, orientationHandler);
                }
            }, DELAY_MS);
        } else {
            if (AbstractFloatingView.getTopOpenView(launcher) != null) {
                return;
            }
            onboardingPrefs.incrementEventCount("launcher.shelf_bounce_count");
            new DiscoveryBounce(launcher, 1.0f - LauncherState.OVERVIEW.getVerticalProgress(launcher)).show(7);
        }
    }

    public static void showForOverviewIfNeeded(Launcher launcher) {
        showForOverviewIfNeeded(launcher, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showForOverviewIfNeeded(final Launcher launcher, boolean withDelay) {
        if (!launcher.isInState(LauncherState.OVERVIEW) || !launcher.hasBeenResumed() || launcher.isForceInvisible() || launcher.getDeviceProfile().isVerticalBarLayout() || launcher.getSharedPrefs().getBoolean("launcher.shelf_bounce_seen", false) || UserManagerCompat.getInstance(launcher).isDemoUser() || ActivityManager.isRunningInTestHarness()) {
            return;
        }
        if (withDelay) {
            new Handler().postDelayed(new Runnable() { // from class: com.android.launcher3.allapps.-$$Lambda$DiscoveryBounce$BAOxvv37sH9pJQoxlh_b13oXUog
                @Override // java.lang.Runnable
                public final void run() {
                    DiscoveryBounce.showForOverviewIfNeeded(launcher, false);
                }
            }, DELAY_MS);
        } else {
            if (InternalStateHandler.hasPending() || AbstractFloatingView.getTopOpenView(launcher) != null) {
                return;
            }
            new DiscoveryBounce(launcher, 1.0f - LauncherState.OVERVIEW.getVerticalProgress(launcher)).show(7);
        }
    }

    public static class VerticalProgressWrapper {
        private final AllAppsTransitionController mController;
        private final float mDelta;

        private VerticalProgressWrapper(AllAppsTransitionController controller, float delta) {
            this.mController = controller;
            this.mDelta = delta;
        }

        public float getProgress() {
            return this.mController.getProgress() + this.mDelta;
        }

        public void setProgress(float progress) {
            this.mController.setProgress(progress - this.mDelta);
        }
    }

    private static void incrementShelfBounceCount(Launcher launcher) {
        SharedPreferences sharedPrefs = launcher.getSharedPrefs();
        int i = sharedPrefs.getInt("launcher.shelf_bounce_count", 0);
        if (i > 3) {
            return;
        }
        sharedPrefs.edit().putInt("launcher.shelf_bounce_count", i + 1).apply();
    }

    private static void incrementHomeBounceCount(Launcher launcher) {
        SharedPreferences sharedPrefs = launcher.getSharedPrefs();
        int i = sharedPrefs.getInt("launcher.home_bounce_count", 0);
        if (i > 3) {
            return;
        }
        sharedPrefs.edit().putInt("launcher.home_bounce_count", i + 1).apply();
    }
}
