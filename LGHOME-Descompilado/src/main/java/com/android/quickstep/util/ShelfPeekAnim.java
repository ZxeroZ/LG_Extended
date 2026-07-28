package com.android.quickstep.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.animation.Interpolator;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.uioverrides.states.OverviewState;

/* JADX INFO: loaded from: classes.dex */
public class ShelfPeekAnim {
    public static final long DURATION = 240;
    public static final Interpolator INTERPOLATOR = Interpolators.OVERSHOOT_1_2;
    private boolean mIsPeeking;
    private final Launcher mLauncher;
    private ShelfAnimState mShelfState;

    public ShelfPeekAnim(Launcher launcher) {
        this.mLauncher = launcher;
    }

    public void setShelfState(ShelfAnimState shelfState, Interpolator interpolator, long duration) {
        if (this.mShelfState == shelfState || FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get()) {
            return;
        }
        this.mLauncher.getStateManager().cancelStateElementAnimation(2);
        this.mShelfState = shelfState;
        this.mIsPeeking = shelfState == ShelfAnimState.PEEK || this.mShelfState == ShelfAnimState.HIDE;
        if (this.mShelfState == ShelfAnimState.CANCEL) {
            return;
        }
        float verticalProgress = LauncherState.BACKGROUND_APP.getVerticalProgress(this.mLauncher);
        float verticalProgress2 = LauncherState.OVERVIEW.getVerticalProgress(this.mLauncher);
        float defaultVerticalProgress = verticalProgress - ((verticalProgress - OverviewState.getDefaultVerticalProgress(this.mLauncher)) * 0.25f);
        if (this.mShelfState != ShelfAnimState.HIDE) {
            verticalProgress = this.mShelfState == ShelfAnimState.PEEK ? defaultVerticalProgress : verticalProgress2;
        }
        Animator animatorCreateStateElementAnimation = this.mLauncher.getStateManager().createStateElementAnimation(2, verticalProgress);
        animatorCreateStateElementAnimation.setInterpolator(interpolator);
        animatorCreateStateElementAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.util.ShelfPeekAnim.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                ShelfPeekAnim.this.mShelfState = ShelfAnimState.CANCEL;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ShelfPeekAnim shelfPeekAnim = ShelfPeekAnim.this;
                shelfPeekAnim.mIsPeeking = shelfPeekAnim.mShelfState == ShelfAnimState.PEEK;
            }
        });
        animatorCreateStateElementAnimation.setDuration(duration).start();
    }

    public boolean isPeeking() {
        return this.mIsPeeking;
    }

    public enum ShelfAnimState {
        HIDE(true),
        PEEK(true),
        OVERVIEW(false),
        CANCEL(false);

        public final boolean shouldPreformHaptic;

        ShelfAnimState(boolean shouldPreformHaptic) {
            this.shouldPreformHaptic = shouldPreformHaptic;
        }
    }
}
