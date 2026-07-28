package com.android.launcher3.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.touch.BaseSwipeDetector;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.views.BaseDragLayer;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractSlideInView extends AbstractFloatingView implements SingleAxisSwipeDetector.Listener {
    protected static final Property<AbstractSlideInView, Float> TRANSLATION_SHIFT = new Property<AbstractSlideInView, Float>(Float.class, "translationShift") { // from class: com.android.launcher3.views.AbstractSlideInView.1
        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(AbstractSlideInView view) {
            return Float.valueOf(view.mTranslationShift);
        }

        /* JADX DEBUG: Method merged with bridge method: set(Ljava/lang/Object;Ljava/lang/Object;)V */
        @Override // android.util.Property
        public void set(AbstractSlideInView view, Float value) {
            view.setTranslationShift(value.floatValue());
        }
    };
    protected static final float TRANSLATION_SHIFT_CLOSED = 1.0f;
    protected static final float TRANSLATION_SHIFT_OPENED = 0.0f;
    private final View mColorScrim;
    protected View mContent;
    protected final Launcher mLauncher;
    protected boolean mNoIntercept;
    protected final ObjectAnimator mOpenCloseAnimator;
    protected Interpolator mScrollInterpolator;
    protected final SingleAxisSwipeDetector mSwipeDetector;
    protected float mTranslationShift;

    protected int getScrimColor(Context context) {
        return -1;
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragStart(boolean start, float startDisplacement) {
    }

    public AbstractSlideInView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTranslationShift = 1.0f;
        Launcher launcher = Launcher.getLauncher(context);
        this.mLauncher = launcher;
        this.mScrollInterpolator = Interpolators.SCROLL_CUBIC;
        this.mSwipeDetector = new SingleAxisSwipeDetector(context, this, SingleAxisSwipeDetector.VERTICAL);
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[0]);
        this.mOpenCloseAnimator = objectAnimatorOfPropertyValuesHolder;
        objectAnimatorOfPropertyValuesHolder.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.views.AbstractSlideInView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AbstractSlideInView.this.mSwipeDetector.finishedScrolling();
                AbstractSlideInView.this.announceAccessibilityChanges();
            }
        });
        int scrimColor = getScrimColor(launcher);
        this.mColorScrim = scrimColor != -1 ? createColorScrim(launcher, scrimColor) : null;
    }

    protected void attachToContainer() {
        if (this.mColorScrim != null) {
            getPopupContainer().addView(this.mColorScrim);
        }
        getPopupContainer().addView(this);
    }

    protected void setTranslationShift(float translationShift) {
        this.mTranslationShift = translationShift;
        this.mContent.setTranslationY(translationShift * r0.getHeight());
        View view = this.mColorScrim;
        if (view != null) {
            view.setAlpha(1.0f - this.mTranslationShift);
        }
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (this.mNoIntercept) {
            return false;
        }
        this.mSwipeDetector.setDetectableScrollConditions(this.mSwipeDetector.isIdleState() ? 2 : 0, false);
        this.mSwipeDetector.onTouchEvent(ev);
        return this.mSwipeDetector.isDraggingOrSettling() || !getPopupContainer().isEventOverView(this.mContent, ev);
    }

    @Override // com.android.launcher3.AbstractFloatingView, com.android.launcher3.util.TouchController
    public boolean onControllerTouchEvent(MotionEvent ev) {
        this.mSwipeDetector.onTouchEvent(ev);
        if (ev.getAction() == 1 && this.mSwipeDetector.isIdleState() && !isOpeningAnimationRunning() && !getPopupContainer().isEventOverView(this.mContent, ev)) {
            close(true);
        }
        return true;
    }

    private boolean isOpeningAnimationRunning() {
        return this.mIsOpen && this.mOpenCloseAnimator.isRunning();
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public boolean onDrag(float displacement) {
        float height = this.mContent.getHeight();
        setTranslationShift(Utilities.boundToRange(displacement, 0.0f, height) / height);
        return true;
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragEnd(float velocity) {
        if ((this.mSwipeDetector.isFling(velocity) && velocity > 0.0f) || this.mTranslationShift > 0.5f) {
            this.mScrollInterpolator = Interpolators.scrollInterpolatorForVelocity(velocity);
            this.mOpenCloseAnimator.setDuration(BaseSwipeDetector.calculateDuration(velocity, 1.0f - this.mTranslationShift));
            close(true);
        } else {
            this.mOpenCloseAnimator.setValues(PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, 0.0f));
            this.mOpenCloseAnimator.setDuration(BaseSwipeDetector.calculateDuration(velocity, this.mTranslationShift)).setInterpolator(Interpolators.DEACCEL);
            this.mOpenCloseAnimator.start();
        }
    }

    protected void handleClose(boolean animate, long defaultDuration) {
        if (this.mIsOpen) {
            if (!animate) {
                this.mOpenCloseAnimator.cancel();
                setTranslationShift(1.0f);
                onCloseComplete();
            } else {
                this.mOpenCloseAnimator.setValues(PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, 1.0f));
                this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.views.AbstractSlideInView.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        AbstractSlideInView.this.onCloseComplete();
                    }
                });
                if (this.mSwipeDetector.isIdleState()) {
                    this.mOpenCloseAnimator.setDuration(defaultDuration).setInterpolator(Interpolators.ACCEL);
                } else {
                    this.mOpenCloseAnimator.setInterpolator(this.mScrollInterpolator);
                }
                this.mOpenCloseAnimator.start();
            }
        }
    }

    protected void onCloseComplete() {
        this.mIsOpen = false;
        getPopupContainer().removeView(this);
        if (this.mColorScrim != null) {
            getPopupContainer().removeView(this.mColorScrim);
        }
    }

    protected BaseDragLayer getPopupContainer() {
        return this.mLauncher.getDragLayer();
    }

    protected static View createColorScrim(Context context, int bgColor) {
        View view = new View(context);
        view.forceHasOverlappingRendering(false);
        view.setBackgroundColor(bgColor);
        BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(-1, -1);
        layoutParams.ignoreInsets = true;
        view.setLayoutParams(layoutParams);
        return view;
    }
}
