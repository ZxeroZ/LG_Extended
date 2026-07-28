package com.android.quickstep.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.util.MultiValueUpdateListener;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsEduView extends AbstractFloatingView {
    private AnimatorSet mAnimation;
    private GradientDrawable mCircle;
    private int mCircleSizePx;
    private GradientDrawable mGradient;
    private Launcher mLauncher;
    private int mMaxHeightPx;
    private int mPaddingPx;
    private int mWidthPx;

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 512) != 0;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
    }

    public AllAppsEduView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCircle = (GradientDrawable) context.getDrawable(R.drawable.all_apps_edu_circle);
        this.mCircleSizePx = getResources().getDimensionPixelSize(R.dimen.swipe_edu_circle_size);
        this.mPaddingPx = getResources().getDimensionPixelSize(R.dimen.swipe_edu_padding);
        this.mWidthPx = getResources().getDimensionPixelSize(R.dimen.swipe_edu_width);
        this.mMaxHeightPx = getResources().getDimensionPixelSize(R.dimen.swipe_edu_max_height);
        setWillNotDraw(false);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mGradient.draw(canvas);
        this.mCircle.draw(canvas);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsOpen = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsOpen = false;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        this.mLauncher.getDragLayer().removeView(this);
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        AnimatorSet animatorSet = this.mAnimation;
        return animatorSet != null && animatorSet.isRunning();
    }

    private void playAnimation() {
        if (this.mAnimation != null) {
            return;
        }
        this.mAnimation = new AnimatorSet();
        Rect rect = new Rect(this.mCircle.getBounds());
        Rect rect2 = new Rect(this.mGradient.getBounds());
        Rect rect3 = new Rect();
        float f = (this.mMaxHeightPx - this.mCircleSizePx) - this.mPaddingPx;
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        stateAnimationConfig.setInterpolator(10, Interpolators.clampToProgress(Interpolators.ACCEL, 0.0f, 0.08f));
        stateAnimationConfig.duration = 1200;
        stateAnimationConfig.userControlled = false;
        final AnimatorPlaybackController animatorPlaybackControllerCreateAnimationToNewWorkspace = this.mLauncher.getStateManager().createAnimationToNewWorkspace(LauncherState.ALL_APPS, stateAnimationConfig);
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.setInterpolator(Interpolators.LINEAR);
        long j = 1800;
        valueAnimatorOfFloat.setDuration(j);
        valueAnimatorOfFloat.addUpdateListener(new MultiValueUpdateListener(600, f, 1200, rect3, rect, rect2, 0.15f, animatorPlaybackControllerCreateAnimationToNewWorkspace) { // from class: com.android.quickstep.views.AllAppsEduView.1
            MultiValueUpdateListener.FloatProp mCircleAlpha;
            MultiValueUpdateListener.FloatProp mCircleScale;
            MultiValueUpdateListener.FloatProp mDeltaY;
            MultiValueUpdateListener.FloatProp mGradientAlpha;
            final /* synthetic */ Rect val$circleBoundsOg;
            final /* synthetic */ int val$firstPart;
            final /* synthetic */ Rect val$gradientBoundsOg;
            final /* synthetic */ float val$maxAllAppsProgress;
            final /* synthetic */ int val$secondPart;
            final /* synthetic */ AnimatorPlaybackController val$stateAnimationController;
            final /* synthetic */ Rect val$temp;
            final /* synthetic */ float val$transY;

            {
                this.val$firstPart = val$firstPart;
                this.val$transY = f;
                this.val$secondPart = val$secondPart;
                this.val$temp = rect3;
                this.val$circleBoundsOg = rect;
                this.val$gradientBoundsOg = rect2;
                this.val$maxAllAppsProgress = val$maxAllAppsProgress;
                this.val$stateAnimationController = animatorPlaybackControllerCreateAnimationToNewWorkspace;
                this.mCircleAlpha = new MultiValueUpdateListener.FloatProp(0.0f, 255.0f, 0.0f, val$firstPart, Interpolators.LINEAR);
                this.mCircleScale = new MultiValueUpdateListener.FloatProp(2.0f, 1.0f, 0.0f, val$firstPart, Interpolators.OVERSHOOT_1_7);
                this.mDeltaY = new MultiValueUpdateListener.FloatProp(0.0f, f, val$firstPart, val$secondPart, Interpolators.FAST_OUT_SLOW_IN);
                this.mGradientAlpha = new MultiValueUpdateListener.FloatProp(0.0f, 255.0f, val$firstPart, val$secondPart * 0.3f, Interpolators.LINEAR);
            }

            @Override // com.android.quickstep.util.MultiValueUpdateListener
            public void onUpdate(float progress) {
                this.val$temp.set(this.val$circleBoundsOg);
                this.val$temp.offset(0, (int) (-this.mDeltaY.value));
                Utilities.scaleRectAboutCenter(this.val$temp, this.mCircleScale.value);
                AllAppsEduView.this.mCircle.setBounds(this.val$temp);
                AllAppsEduView.this.mCircle.setAlpha((int) this.mCircleAlpha.value);
                AllAppsEduView.this.mGradient.setAlpha((int) this.mGradientAlpha.value);
                this.val$temp.set(this.val$gradientBoundsOg);
                this.val$temp.top = (int) (r7.top - this.mDeltaY.value);
                AllAppsEduView.this.mGradient.setBounds(this.val$temp);
                AllAppsEduView.this.invalidate();
                this.val$stateAnimationController.setPlayFraction(Utilities.mapToRange(this.mDeltaY.value, 0.0f, this.val$transY, 0.0f, this.val$maxAllAppsProgress, Interpolators.LINEAR));
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.views.AllAppsEduView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AllAppsEduView.this.mCircle.setAlpha(0);
                AllAppsEduView.this.mGradient.setAlpha(0);
            }
        });
        this.mAnimation.play(valueAnimatorOfFloat);
        ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.15f, 0.0f);
        valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.quickstep.views.-$$Lambda$AllAppsEduView$29tMjzaVbWfeju_CvzFfwaP_6aw
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                animatorPlaybackControllerCreateAnimationToNewWorkspace.setPlayFraction(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat2.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        valueAnimatorOfFloat2.setStartDelay(j);
        valueAnimatorOfFloat2.setDuration(250L);
        this.mAnimation.play(valueAnimatorOfFloat2);
        this.mAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.views.AllAppsEduView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AllAppsEduView.this.mAnimation = null;
                animatorPlaybackControllerCreateAnimationToNewWorkspace.dispatchOnCancel();
                AllAppsEduView.this.handleClose(false);
            }
        });
        this.mAnimation.start();
    }

    private void init(Launcher launcher) {
        this.mLauncher = launcher;
        int colorAccent = Themes.getColorAccent(launcher);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, Themes.getAttrBoolean(launcher, R.attr.isMainColorDark) ? new int[]{-1275068417, ViewCompat.MEASURED_SIZE_MASK} : new int[]{ColorUtils.setAlphaComponent(colorAccent, 127), ColorUtils.setAlphaComponent(colorAccent, 0)});
        this.mGradient = gradientDrawable;
        float f = this.mWidthPx / 2.0f;
        gradientDrawable.setCornerRadii(new float[]{f, f, f, f, 0.0f, 0.0f, 0.0f, 0.0f});
        int i = this.mMaxHeightPx;
        int i2 = this.mCircleSizePx;
        int i3 = this.mPaddingPx;
        int i4 = (i - i2) + i3;
        this.mCircle.setBounds(i3, i4, i3 + i2, i2 + i4);
        GradientDrawable gradientDrawable2 = this.mGradient;
        int i5 = this.mMaxHeightPx;
        gradientDrawable2.setBounds(0, i5 - this.mCircleSizePx, this.mWidthPx, i5);
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(this.mWidthPx, this.mMaxHeightPx);
        layoutParams.ignoreInsets = true;
        layoutParams.leftMargin = (deviceProfile.widthPx - this.mWidthPx) / 2;
        layoutParams.topMargin = (deviceProfile.heightPx - deviceProfile.hotseatBarSizePx) - this.mMaxHeightPx;
        setLayoutParams(layoutParams);
    }

    public static void show(Launcher launcher) {
        AllAppsEduView allAppsEduView = (AllAppsEduView) launcher.getViewCache().getView(R.layout.all_apps_edu_view, launcher, (ViewGroup) launcher.getDragLayer().getParent());
        allAppsEduView.init(launcher);
        launcher.getDragLayer().addView(allAppsEduView);
        launcher.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_ALL_APPS_EDU_SHOWN);
        allAppsEduView.requestLayout();
        allAppsEduView.playAnimation();
    }
}
