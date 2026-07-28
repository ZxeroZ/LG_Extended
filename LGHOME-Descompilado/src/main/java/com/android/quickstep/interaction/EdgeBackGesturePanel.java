package com.android.quickstep.interaction;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.util.VibratorWrapper;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class EdgeBackGesturePanel extends View {
    private static final int ARROW_ANGLE_ADDED_PER_1000_SPEED = 4;
    private static final int ARROW_ANGLE_WHEN_EXTENDED_DEGREES = 56;
    private static final int ARROW_LENGTH_DP = 18;
    private static final int ARROW_MAX_ANGLE_SPEED_OFFSET_DEGREES = 4;
    private static final float ARROW_THICKNESS_DP = 2.5f;
    private static final int BASE_TRANSLATION_DP = 32;
    private static final long DISAPPEAR_ARROW_ANIMATION_DURATION_MS = 100;
    private static final long DISAPPEAR_FADE_ANIMATION_DURATION_MS = 80;
    private static final int GESTURE_DURATION_FOR_CLICK_MS = 400;
    private static final String LOG_TAG = "EdgeBackGesturePanel";
    private static final int RUBBER_BAND_AMOUNT = 15;
    private static final int RUBBER_BAND_AMOUNT_APPEAR = 4;
    private final SpringAnimation mAngleAnimation;
    private final SpringForce mAngleAppearForce;
    private final SpringForce mAngleDisappearForce;
    private float mAngleOffset;
    private final ValueAnimator mArrowDisappearAnimation;
    private final float mArrowLength;
    private int mArrowPaddingEnd;
    private final Path mArrowPath;
    private final float mArrowThickness;
    private boolean mArrowsPointLeft;
    private BackCallback mBackCallback;
    private final float mBaseTranslation;
    private float mCurrentAngle;
    private float mCurrentTranslation;
    private final float mDensity;
    private float mDesiredAngle;
    private float mDesiredTranslation;
    private float mDesiredVerticalTranslation;
    private float mDisappearAmount;
    private final Point mDisplaySize;
    private boolean mDragSlopPassed;
    private int mFingerOffset;
    private boolean mIsLeftPanel;
    private float mMaxTranslation;
    private int mMinArrowPosition;
    private final float mMinDeltaForSwitch;
    private final Paint mPaint;
    private float mPreviousTouchTranslation;
    private final SpringForce mRegularTranslationSpring;
    private int mScreenSize;
    private final DynamicAnimation.OnAnimationEndListener mSetGoneEndListener;
    private float mStartX;
    private float mStartY;
    private final float mSwipeThreshold;
    private float mTotalTouchDelta;
    private final SpringAnimation mTranslationAnimation;
    private boolean mTriggerBack;
    private final SpringForce mTriggerBackSpring;
    private VelocityTracker mVelocityTracker;
    private float mVerticalTranslation;
    private final SpringAnimation mVerticalTranslationAnimation;
    private long mVibrationTime;
    private static final Interpolator RUBBER_BAND_INTERPOLATOR = new PathInterpolator(0.2f, 1.0f, 1.0f, 1.0f);
    private static final Interpolator RUBBER_BAND_INTERPOLATOR_APPEAR = new PathInterpolator(0.25f, 1.0f, 1.0f, 1.0f);
    private static final FloatPropertyCompat<EdgeBackGesturePanel> CURRENT_ANGLE = new FloatPropertyCompat<EdgeBackGesturePanel>("currentAngle") { // from class: com.android.quickstep.interaction.EdgeBackGesturePanel.2
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(EdgeBackGesturePanel object, float value) {
            object.setCurrentAngle(value);
        }

        /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(EdgeBackGesturePanel object) {
            return object.getCurrentAngle();
        }
    };
    private static final FloatPropertyCompat<EdgeBackGesturePanel> CURRENT_TRANSLATION = new FloatPropertyCompat<EdgeBackGesturePanel>("currentTranslation") { // from class: com.android.quickstep.interaction.EdgeBackGesturePanel.3
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(EdgeBackGesturePanel object, float value) {
            object.setCurrentTranslation(value);
        }

        /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(EdgeBackGesturePanel object) {
            return object.getCurrentTranslation();
        }
    };
    private static final FloatPropertyCompat<EdgeBackGesturePanel> CURRENT_VERTICAL_TRANSLATION = new FloatPropertyCompat<EdgeBackGesturePanel>("verticalTranslation") { // from class: com.android.quickstep.interaction.EdgeBackGesturePanel.4
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(EdgeBackGesturePanel object, float value) {
            object.setVerticalTranslation(value);
        }

        /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(EdgeBackGesturePanel object) {
            return object.getVerticalTranslation();
        }
    };

    interface BackCallback {
        void cancelBack();

        void triggerBack();
    }

    private static float lerp(float start, float stop, float amount) {
        return start + ((stop - start) * amount);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public EdgeBackGesturePanel(Context context, ViewGroup parent, ViewGroup.LayoutParams layoutParams) {
        super(context);
        Paint paint = new Paint();
        this.mPaint = paint;
        this.mArrowPath = new Path();
        this.mDisplaySize = new Point();
        this.mSetGoneEndListener = new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.quickstep.interaction.EdgeBackGesturePanel.1
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                animation.removeEndListener(this);
                if (canceled) {
                    return;
                }
                EdgeBackGesturePanel.this.setVisibility(8);
            }
        };
        this.mDensity = context.getResources().getDisplayMetrics().density;
        this.mBaseTranslation = dp(32.0f);
        this.mArrowLength = dp(18.0f);
        float fDp = dp(ARROW_THICKNESS_DP);
        this.mArrowThickness = fDp;
        this.mMinDeltaForSwitch = dp(32.0f);
        paint.setStrokeWidth(fDp);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mArrowDisappearAnimation = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(DISAPPEAR_ARROW_ANIMATION_DURATION_MS);
        valueAnimatorOfFloat.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.quickstep.interaction.-$$Lambda$EdgeBackGesturePanel$CmMLn7ILAfzyZr3i-VL9KTLWaO0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$new$0$EdgeBackGesturePanel(valueAnimator);
            }
        });
        SpringAnimation springAnimation = new SpringAnimation(this, CURRENT_ANGLE);
        this.mAngleAnimation = springAnimation;
        SpringForce dampingRatio = new SpringForce().setStiffness(500.0f).setDampingRatio(0.5f);
        this.mAngleAppearForce = dampingRatio;
        this.mAngleDisappearForce = new SpringForce().setStiffness(1500.0f).setDampingRatio(0.5f).setFinalPosition(90.0f);
        springAnimation.setSpring(dampingRatio).setMaxValue(90.0f);
        SpringAnimation springAnimation2 = new SpringAnimation(this, CURRENT_TRANSLATION);
        this.mTranslationAnimation = springAnimation2;
        SpringForce dampingRatio2 = new SpringForce().setStiffness(1500.0f).setDampingRatio(0.75f);
        this.mRegularTranslationSpring = dampingRatio2;
        this.mTriggerBackSpring = new SpringForce().setStiffness(450.0f).setDampingRatio(0.75f);
        springAnimation2.setSpring(dampingRatio2);
        SpringAnimation springAnimation3 = new SpringAnimation(this, CURRENT_VERTICAL_TRANSLATION);
        this.mVerticalTranslationAnimation = springAnimation3;
        springAnimation3.setSpring(new SpringForce().setStiffness(1500.0f).setDampingRatio(0.75f));
        paint.setColor(context.getColor((context.getResources().getConfiguration().uiMode & 48) == 32 ? R.color.back_arrow_color_light : R.color.back_arrow_color_dark));
        loadDimens();
        updateArrowDirection();
        this.mSwipeThreshold = ResourceUtils.getDimenByName("navigation_edge_action_drag_threshold", context.getResources(), 16);
        parent.addView(this, layoutParams);
        setVisibility(8);
    }

    public /* synthetic */ void lambda$new$0$EdgeBackGesturePanel(ValueAnimator valueAnimator) {
        this.mDisappearAmount = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    void onDestroy() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this);
        }
    }

    void setIsLeftPanel(boolean isLeftPanel) {
        this.mIsLeftPanel = isLeftPanel;
    }

    boolean getIsLeftPanel() {
        return this.mIsLeftPanel;
    }

    void setDisplaySize(Point displaySize) {
        this.mDisplaySize.set(displaySize.x, displaySize.y);
        this.mScreenSize = Math.min(this.mDisplaySize.x, this.mDisplaySize.y);
    }

    void setBackCallback(BackCallback callback) {
        this.mBackCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getCurrentAngle() {
        return this.mCurrentAngle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getCurrentTranslation() {
        return this.mCurrentTranslation;
    }

    void onMotionEvent(MotionEvent event) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            this.mDragSlopPassed = false;
            resetOnDown();
            this.mStartX = event.getX();
            this.mStartY = event.getY();
            setVisibility(0);
            updatePosition(event.getY());
            return;
        }
        if (actionMasked == 1) {
            if (this.mTriggerBack) {
                triggerBack();
            } else {
                cancelBack();
            }
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
            return;
        }
        if (actionMasked == 2) {
            handleMoveEvent(event);
        } else {
            if (actionMasked != 3) {
                return;
            }
            cancelBack();
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateArrowDirection();
        loadDimens();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float width = this.mCurrentTranslation - (this.mArrowThickness / 2.0f);
        canvas.save();
        if (!this.mIsLeftPanel) {
            width = getWidth() - width;
        }
        canvas.translate(width, (getHeight() * 0.5f) + this.mVerticalTranslation);
        canvas.drawPath(calculatePath(polarToCartX(this.mCurrentAngle) * this.mArrowLength, polarToCartY(this.mCurrentAngle) * this.mArrowLength), this.mPaint);
        canvas.restore();
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mMaxTranslation = getWidth() - this.mArrowPaddingEnd;
    }

    private void loadDimens() {
        Resources resources = getResources();
        this.mArrowPaddingEnd = ResourceUtils.getDimenByName("navigation_edge_panel_padding", resources, 8);
        this.mMinArrowPosition = ResourceUtils.getDimenByName("navigation_edge_arrow_min_y", resources, 64);
        this.mFingerOffset = ResourceUtils.getDimenByName("navigation_edge_finger_offset", resources, 48);
    }

    private void updateArrowDirection() {
        this.mArrowsPointLeft = getLayoutDirection() == 0;
        invalidate();
    }

    private float getStaticArrowWidth() {
        return polarToCartX(56.0f) * this.mArrowLength;
    }

    private float polarToCartX(float angleInDegrees) {
        return (float) Math.cos(Math.toRadians(angleInDegrees));
    }

    private float polarToCartY(float angleInDegrees) {
        return (float) Math.sin(Math.toRadians(angleInDegrees));
    }

    private Path calculatePath(float x, float y) {
        if (!this.mArrowsPointLeft) {
            x = -x;
        }
        float fLerp = lerp(1.0f, 0.75f, this.mDisappearAmount);
        float f = x * fLerp;
        float f2 = y * fLerp;
        this.mArrowPath.reset();
        this.mArrowPath.moveTo(f, f2);
        this.mArrowPath.lineTo(0.0f, 0.0f);
        this.mArrowPath.lineTo(f, -f2);
        return this.mArrowPath;
    }

    private void triggerBack() {
        BackCallback backCallback = this.mBackCallback;
        if (backCallback != null) {
            backCallback.triggerBack();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        if ((Math.abs(this.mVelocityTracker.getXVelocity()) < 500.0f) || SystemClock.uptimeMillis() - this.mVibrationTime >= 400) {
            VibratorWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).vibrate(VibratorWrapper.EFFECT_CLICK);
        }
        float f = this.mAngleOffset;
        if (f > -4.0f) {
            this.mAngleOffset = Math.max(-8.0f, f - 8.0f);
            updateAngle(true);
        }
        final Runnable runnable = new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$EdgeBackGesturePanel$em4uXOotbLRxb54yoD3GkxnWIuw
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$triggerBack$2$EdgeBackGesturePanel();
            }
        };
        if (this.mTranslationAnimation.isRunning()) {
            this.mTranslationAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.quickstep.interaction.EdgeBackGesturePanel.5
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    animation.removeEndListener(this);
                    if (canceled) {
                        return;
                    }
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$triggerBack$2$EdgeBackGesturePanel() {
        this.mAngleOffset = Math.max(0.0f, this.mAngleOffset + 8.0f);
        updateAngle(true);
        this.mTranslationAnimation.setSpring(this.mTriggerBackSpring);
        setDesiredTranslation(this.mDesiredTranslation - dp(32.0f), true);
        animate().alpha(0.0f).setDuration(DISAPPEAR_FADE_ANIMATION_DURATION_MS).withEndAction(new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$EdgeBackGesturePanel$k5MRfZu1qbciaF4_g0oUpZ56WUU
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$triggerBack$1$EdgeBackGesturePanel();
            }
        });
        this.mArrowDisappearAnimation.start();
    }

    public /* synthetic */ void lambda$triggerBack$1$EdgeBackGesturePanel() {
        setVisibility(8);
    }

    private void cancelBack() {
        BackCallback backCallback = this.mBackCallback;
        if (backCallback != null) {
            backCallback.cancelBack();
        }
        if (this.mTranslationAnimation.isRunning()) {
            this.mTranslationAnimation.addEndListener(this.mSetGoneEndListener);
        } else {
            setVisibility(8);
        }
    }

    private void resetOnDown() {
        animate().cancel();
        this.mAngleAnimation.cancel();
        this.mTranslationAnimation.cancel();
        this.mVerticalTranslationAnimation.cancel();
        this.mArrowDisappearAnimation.cancel();
        this.mAngleOffset = 0.0f;
        this.mTranslationAnimation.setSpring(this.mRegularTranslationSpring);
        setTriggerBack(false, false);
        setDesiredTranslation(0.0f, false);
        setCurrentTranslation(0.0f);
        updateAngle(false);
        this.mPreviousTouchTranslation = 0.0f;
        this.mTotalTouchDelta = 0.0f;
        this.mVibrationTime = 0L;
        setDesiredVerticalTransition(0.0f, false);
    }

    private void handleMoveEvent(MotionEvent event) {
        float staticArrowWidth;
        float x = event.getX();
        float y = event.getY();
        float fAbs = Math.abs(x - this.mStartX);
        float f = y - this.mStartY;
        float f2 = fAbs - this.mPreviousTouchTranslation;
        if (Math.abs(f2) > 0.0f) {
            if (Math.signum(f2) == Math.signum(this.mTotalTouchDelta)) {
                this.mTotalTouchDelta += f2;
            } else {
                this.mTotalTouchDelta = f2;
            }
        }
        this.mPreviousTouchTranslation = fAbs;
        if (!this.mDragSlopPassed && fAbs > this.mSwipeThreshold) {
            this.mDragSlopPassed = true;
            VibratorWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).vibrate(VibratorWrapper.EFFECT_CLICK);
            this.mVibrationTime = SystemClock.uptimeMillis();
            this.mDisappearAmount = 0.0f;
            setAlpha(1.0f);
            setTriggerBack(true, true);
        }
        float f3 = this.mBaseTranslation;
        if (fAbs > f3) {
            float interpolation = RUBBER_BAND_INTERPOLATOR.getInterpolation(MathUtils.clamp((fAbs - f3) / (this.mScreenSize - f3), 0.0f, 1.0f));
            float f4 = this.mMaxTranslation;
            float f5 = this.mBaseTranslation;
            staticArrowWidth = f5 + (interpolation * (f4 - f5));
        } else {
            float interpolation2 = RUBBER_BAND_INTERPOLATOR_APPEAR.getInterpolation(MathUtils.clamp((f3 - fAbs) / f3, 0.0f, 1.0f));
            float f6 = this.mBaseTranslation;
            staticArrowWidth = f6 - (interpolation2 * (f6 / 4.0f));
        }
        boolean z = this.mTriggerBack;
        if (Math.abs(this.mTotalTouchDelta) > this.mMinDeltaForSwitch) {
            z = this.mTotalTouchDelta > 0.0f;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = this.mVelocityTracker.getXVelocity();
        float fMin = Math.min((((float) Math.hypot(xVelocity, this.mVelocityTracker.getYVelocity())) / 1000.0f) * 4.0f, 4.0f) * Math.signum(xVelocity);
        this.mAngleOffset = fMin;
        boolean z2 = this.mIsLeftPanel;
        if ((z2 && this.mArrowsPointLeft) || (!z2 && !this.mArrowsPointLeft)) {
            this.mAngleOffset = fMin * (-1.0f);
        }
        setTriggerBack(Math.abs(f) <= Math.abs(x - this.mStartX) * 2.0f ? z : false, true);
        if (this.mTriggerBack) {
            boolean z3 = this.mIsLeftPanel;
            if ((z3 && this.mArrowsPointLeft) || (!z3 && !this.mArrowsPointLeft)) {
                staticArrowWidth -= getStaticArrowWidth();
            }
        } else {
            staticArrowWidth = 0.0f;
        }
        setDesiredTranslation(staticArrowWidth, true);
        updateAngle(true);
        float height = (getHeight() / 2.0f) - this.mArrowLength;
        setDesiredVerticalTransition(RUBBER_BAND_INTERPOLATOR.getInterpolation(MathUtils.clamp(Math.abs(f) / (15.0f * height), 0.0f, 1.0f)) * height * Math.signum(f), true);
    }

    private void updatePosition(float touchY) {
        float fMax = Math.max(touchY - this.mFingerOffset, this.mMinArrowPosition) - (getLayoutParams().height / 2.0f);
        setX(this.mIsLeftPanel ? 0.0f : this.mDisplaySize.x - getLayoutParams().width);
        setY(MathUtils.clamp((int) fMax, 0, this.mDisplaySize.y));
    }

    private void setDesiredVerticalTransition(float verticalTranslation, boolean animated) {
        if (this.mDesiredVerticalTranslation != verticalTranslation) {
            this.mDesiredVerticalTranslation = verticalTranslation;
            if (!animated) {
                setVerticalTranslation(verticalTranslation);
            } else {
                this.mVerticalTranslationAnimation.animateToFinalPosition(verticalTranslation);
            }
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVerticalTranslation(float verticalTranslation) {
        this.mVerticalTranslation = verticalTranslation;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getVerticalTranslation() {
        return this.mVerticalTranslation;
    }

    private void setDesiredTranslation(float desiredTranslation, boolean animated) {
        if (this.mDesiredTranslation != desiredTranslation) {
            this.mDesiredTranslation = desiredTranslation;
            if (!animated) {
                setCurrentTranslation(desiredTranslation);
            } else {
                this.mTranslationAnimation.animateToFinalPosition(desiredTranslation);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentTranslation(float currentTranslation) {
        this.mCurrentTranslation = currentTranslation;
        invalidate();
    }

    private void setTriggerBack(boolean triggerBack, boolean animated) {
        if (this.mTriggerBack != triggerBack) {
            this.mTriggerBack = triggerBack;
            this.mAngleAnimation.cancel();
            updateAngle(animated);
            this.mTranslationAnimation.cancel();
        }
    }

    private void updateAngle(boolean animated) {
        boolean z = this.mTriggerBack;
        float f = z ? this.mAngleOffset + 56.0f : 90.0f;
        if (f != this.mDesiredAngle) {
            if (!animated) {
                setCurrentAngle(f);
            } else {
                this.mAngleAnimation.setSpring(z ? this.mAngleAppearForce : this.mAngleDisappearForce);
                this.mAngleAnimation.animateToFinalPosition(f);
            }
            this.mDesiredAngle = f;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentAngle(float currentAngle) {
        this.mCurrentAngle = currentAngle;
        invalidate();
    }

    private float dp(float dp) {
        return this.mDensity * dp;
    }
}
