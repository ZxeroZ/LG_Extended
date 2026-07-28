package com.android.quickstep.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.anim.SpringAnimationBuilder;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.DynamicResource;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.plugins.ResourceProvider;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class StaggeredWorkspaceAnim {
    private static final int ALPHA_DURATION_MS = 250;
    private static final int APP_CLOSE_ROW_START_DELAY_MS = 0;
    private static final float MAX_VELOCITY_PX_PER_S = 22.0f;
    private final AnimatorSet mAnimators = new AnimatorSet();
    private final float mSpringTransY;
    private final float mVelocity;

    private void addScrimAnimationForState(Launcher launcher, LauncherState state, PropertySetter setter) {
    }

    public StaggeredWorkspaceAnim(Launcher launcher, float velocity, boolean animateOverviewScrim) {
        prepareToAnimate(launcher, animateOverviewScrim);
        this.mVelocity = velocity;
        this.mSpringTransY = (((Math.abs(velocity) * 0.9f) / MAX_VELOCITY_PX_PER_S) + 0.2f) * launcher.getResources().getDimensionPixelSize(R.dimen.swipe_up_max_workspace_trans_y);
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        final Workspace workspace = launcher.getWorkspace();
        final CellLayout cellLayout = workspace != null ? (CellLayout) workspace.getChildAt(workspace.getCurrentPage()) : null;
        ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout != null ? cellLayout.getShortcutsAndWidgets() : null;
        if (shortcutsAndWidgets == null) {
            return;
        }
        final Hotseat hotseat = launcher.getHotseat();
        final boolean clipChildren = workspace.getClipChildren();
        final boolean clipToPadding = workspace.getClipToPadding();
        final boolean clipChildren2 = cellLayout.getClipChildren();
        final boolean clipToPadding2 = cellLayout.getClipToPadding();
        final boolean clipChildren3 = hotseat.getClipChildren();
        boolean clipToPadding3 = hotseat.getClipToPadding();
        workspace.setClipChildren(false);
        workspace.setClipToPadding(false);
        cellLayout.setClipChildren(false);
        cellLayout.setClipToPadding(false);
        hotseat.setClipChildren(false);
        hotseat.setClipToPadding(false);
        int i = deviceProfile.inv.numRows + (deviceProfile.isVerticalBarLayout() ? 0 : 2);
        int childCount = shortcutsAndWidgets.getChildCount() - 1;
        while (childCount >= 0) {
            View childAt = shortcutsAndWidgets.getChildAt(childCount);
            ShortcutAndWidgetContainer shortcutAndWidgetContainer = shortcutsAndWidgets;
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) childAt.getLayoutParams();
            addStaggeredAnimationForView(childAt, layoutParams.cellY + layoutParams.cellVSpan, i);
            childCount--;
            clipToPadding3 = clipToPadding3;
            shortcutsAndWidgets = shortcutAndWidgetContainer;
        }
        final boolean z = clipToPadding3;
        ViewGroup viewGroup = (ViewGroup) hotseat.getChildAt(0);
        if (deviceProfile.isVerticalBarLayout()) {
            for (int childCount2 = viewGroup.getChildCount() - 1; childCount2 >= 0; childCount2--) {
                View childAt2 = viewGroup.getChildAt(childCount2);
                addStaggeredAnimationForView(childAt2, ((CellLayout.LayoutParams) childAt2.getLayoutParams()).cellY + 1, i);
            }
        } else {
            for (int childCount3 = viewGroup.getChildCount() - 1; childCount3 >= 0; childCount3--) {
                addStaggeredAnimationForView(viewGroup.getChildAt(childCount3), deviceProfile.inv.numRows + 1, i);
            }
        }
        addDepthAnimationForState(launcher, LauncherState.NORMAL, 250L);
        this.mAnimators.play(launcher.getDragLayer().getScrim().createSysuiMultiplierAnim(0.0f, 1.0f).setDuration(250L));
        this.mAnimators.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.util.StaggeredWorkspaceAnim.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                workspace.setClipChildren(clipChildren);
                workspace.setClipToPadding(clipToPadding);
                cellLayout.setClipChildren(clipChildren2);
                cellLayout.setClipToPadding(clipToPadding2);
                hotseat.setClipChildren(clipChildren3);
                hotseat.setClipToPadding(z);
            }
        });
    }

    private void prepareToAnimate(Launcher launcher, boolean animateOverviewScrim) {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        stateAnimationConfig.animFlags = 31;
        stateAnimationConfig.duration = 0L;
        if (launcher.getWorkspace() != null && launcher.getState() != LauncherState.OVERVIEW && launcher.getWorkspace().getState() == Workspace.State.NORMAL && launcher.getWorkspace().getOverlayTranslation() == 0.0f) {
            launcher.getStateManager().createAtomicAnimation(LauncherState.BACKGROUND_APP, LauncherState.NORMAL, stateAnimationConfig).start();
        }
        ((RecentsView) launcher.getOverviewPanel()).getScroller().forceFinished(true);
    }

    public AnimatorSet getAnimators() {
        return this.mAnimators;
    }

    public StaggeredWorkspaceAnim addAnimatorListener(Animator.AnimatorListener listener) {
        this.mAnimators.addListener(listener);
        return this;
    }

    public void start() {
        this.mAnimators.start();
    }

    private void addStaggeredAnimationForView(final View v, int row, int totalRows) {
        long j = ((totalRows - row) + 1) * 0;
        v.setTranslationY(this.mSpringTransY);
        ResourceProvider resourceProviderProvider = DynamicResource.provider(v.getContext());
        ValueAnimator valueAnimatorBuild = new SpringAnimationBuilder(v.getContext()).setStiffness(resourceProviderProvider.getFloat(R.dimen.staggered_stiffness)).setDampingRatio(resourceProviderProvider.getFloat(R.dimen.staggered_damping_ratio)).setMinimumVisibleChange(1.0f).setStartValue(this.mSpringTransY).setEndValue(0.0f).setStartVelocity(this.mVelocity).build(v, LauncherAnimUtils.VIEW_TRANSLATE_Y);
        valueAnimatorBuild.setStartDelay(j);
        valueAnimatorBuild.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.util.StaggeredWorkspaceAnim.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                v.setTranslationY(0.0f);
            }
        });
        this.mAnimators.play(valueAnimatorBuild);
        v.setAlpha(0.0f);
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(v, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f);
        objectAnimatorOfFloat.setInterpolator(Interpolators.LINEAR);
        objectAnimatorOfFloat.setDuration(250L);
        objectAnimatorOfFloat.setStartDelay(j);
        objectAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.util.StaggeredWorkspaceAnim.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                v.setAlpha(1.0f);
            }
        });
        this.mAnimators.play(objectAnimatorOfFloat);
    }

    private void addDepthAnimationForState(Launcher launcher, LauncherState state, long duration) {
        if (launcher instanceof BaseQuickstepLauncher) {
            PendingAnimation pendingAnimation = new PendingAnimation(duration);
            ((BaseQuickstepLauncher) launcher).getDepthController().setStateWithAnimation(state, new StateAnimationConfig(), pendingAnimation);
            this.mAnimators.play(pendingAnimation.buildAnim());
        }
    }
}
