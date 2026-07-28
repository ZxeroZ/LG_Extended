package com.android.launcher3.pageindicators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Themes;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class PageIndicatorDots extends View implements PageIndicator {
    private static final long ANIMATION_DURATION = 150;
    private static final int ENTER_ANIMATION_DURATION = 400;
    private static final float ENTER_ANIMATION_OVERSHOOT_TENSION = 4.9f;
    private static final int ENTER_ANIMATION_STAGGERED_DELAY = 150;
    private static final int ENTER_ANIMATION_START_DELAY = 300;
    private static final float SHIFT_PER_ANIMATION = 0.5f;
    private static final float SHIFT_THRESHOLD = 0.1f;
    private final int mActiveColor;
    private int mActivePage;
    private ObjectAnimator mAnimator;
    private final Paint mCirclePaint;
    private float mCurrentPosition;
    private final float mDotRadius;
    private float[] mEntryAnimationRadiusFactors;
    private float mFinalPosition;
    private final int mInActiveColor;
    private final boolean mIsRtl;
    private int mNumPages;
    private static final RectF sTempRect = new RectF();
    private static final Property<PageIndicatorDots, Float> CURRENT_POSITION = new Property<PageIndicatorDots, Float>(Float.TYPE, "current_position") { // from class: com.android.launcher3.pageindicators.PageIndicatorDots.1
        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(PageIndicatorDots obj) {
            return Float.valueOf(obj.mCurrentPosition);
        }

        /* JADX DEBUG: Method merged with bridge method: set(Ljava/lang/Object;Ljava/lang/Object;)V */
        @Override // android.util.Property
        public void set(PageIndicatorDots obj, Float pos) {
            obj.mCurrentPosition = pos.floatValue();
            obj.invalidate();
            obj.invalidateOutline();
        }
    };

    public PageIndicatorDots(Context context) {
        this(context, null);
    }

    public PageIndicatorDots(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorDots(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Paint paint = new Paint(1);
        this.mCirclePaint = paint;
        paint.setStyle(Paint.Style.FILL);
        this.mDotRadius = getResources().getDimension(R.dimen.page_indicator_dot_size) / 2.0f;
        setOutlineProvider(new MyOutlineProver());
        this.mActiveColor = Themes.getColorAccent(context);
        this.mInActiveColor = Themes.getAttrColor(context, android.R.attr.colorControlHighlight);
        this.mIsRtl = Utilities.isRtl(getResources());
    }

    @Override // com.android.launcher3.pageindicators.PageIndicator
    public void setScroll(int currentScroll, int totalScroll) {
        int i = this.mNumPages;
        if (i > 1) {
            if (this.mIsRtl) {
                currentScroll = totalScroll - currentScroll;
            }
            int i2 = totalScroll / (i - 1);
            int i3 = currentScroll / i2;
            int i4 = i3 * i2;
            int i5 = i4 + i2;
            float f = i2 * 0.1f;
            float f2 = currentScroll;
            if (f2 < i4 + f) {
                animateToPosition(i3);
            } else if (f2 > i5 - f) {
                animateToPosition(i3 + 1);
            } else {
                animateToPosition(i3 + 0.5f);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateToPosition(float position) {
        this.mFinalPosition = position;
        if (Math.abs(this.mCurrentPosition - position) < 0.1f) {
            this.mCurrentPosition = this.mFinalPosition;
        }
        if (this.mAnimator != null || Float.compare(this.mCurrentPosition, this.mFinalPosition) == 0) {
            return;
        }
        float f = this.mCurrentPosition;
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, CURRENT_POSITION, f > this.mFinalPosition ? f - 0.5f : f + 0.5f);
        this.mAnimator = objectAnimatorOfFloat;
        objectAnimatorOfFloat.addListener(new AnimationCycleListener());
        this.mAnimator.setDuration(150L);
        this.mAnimator.start();
    }

    public void stopAllAnimations() {
        ObjectAnimator objectAnimator = this.mAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mAnimator = null;
        }
        float f = this.mActivePage;
        this.mFinalPosition = f;
        CURRENT_POSITION.set(this, Float.valueOf(f));
    }

    public void prepareEntryAnimation() {
        this.mEntryAnimationRadiusFactors = new float[this.mNumPages];
        invalidate();
    }

    public void playEntryAnimation() {
        int length = this.mEntryAnimationRadiusFactors.length;
        if (length == 0) {
            this.mEntryAnimationRadiusFactors = null;
            invalidate();
            return;
        }
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(ENTER_ANIMATION_OVERSHOOT_TENSION);
        AnimatorSet animatorSet = new AnimatorSet();
        for (final int i = 0; i < length; i++) {
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(400L);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.pageindicators.PageIndicatorDots.2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    PageIndicatorDots.this.mEntryAnimationRadiusFactors[i] = ((Float) animation.getAnimatedValue()).floatValue();
                    PageIndicatorDots.this.invalidate();
                }
            });
            duration.setInterpolator(overshootInterpolator);
            duration.setStartDelay((i * 150) + 300);
            animatorSet.play(duration);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.pageindicators.PageIndicatorDots.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PageIndicatorDots.this.mEntryAnimationRadiusFactors = null;
                PageIndicatorDots.this.invalidateOutline();
                PageIndicatorDots.this.invalidate();
            }
        });
        animatorSet.start();
    }

    @Override // com.android.launcher3.pageindicators.PageIndicator
    public void setActiveMarker(int activePage) {
        if (this.mActivePage != activePage) {
            this.mActivePage = activePage;
        }
    }

    @Override // com.android.launcher3.pageindicators.PageIndicator
    public void setMarkersCount(int numMarkers) {
        this.mNumPages = numMarkers;
        requestLayout();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824 ? View.MeasureSpec.getSize(widthMeasureSpec) : (int) (((this.mNumPages * 3) + 2) * this.mDotRadius), View.MeasureSpec.getMode(heightMeasureSpec) == 1073741824 ? View.MeasureSpec.getSize(heightMeasureSpec) : (int) (this.mDotRadius * 4.0f));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float f = this.mDotRadius * 3.0f;
        float f2 = this.mDotRadius;
        float width = (((getWidth() - (this.mNumPages * f)) + f2) / 2.0f) + f2;
        float height = getHeight() / 2;
        int i = 0;
        if (this.mEntryAnimationRadiusFactors != null) {
            if (this.mIsRtl) {
                width = getWidth() - width;
                f = -f;
            }
            while (i < this.mEntryAnimationRadiusFactors.length) {
                this.mCirclePaint.setColor(i == this.mActivePage ? this.mActiveColor : this.mInActiveColor);
                canvas.drawCircle(width, height, this.mDotRadius * this.mEntryAnimationRadiusFactors[i], this.mCirclePaint);
                width += f;
                i++;
            }
            return;
        }
        this.mCirclePaint.setColor(this.mInActiveColor);
        while (i < this.mNumPages) {
            canvas.drawCircle(width, height, this.mDotRadius, this.mCirclePaint);
            width += f;
            i++;
        }
        this.mCirclePaint.setColor(this.mActiveColor);
        RectF activeRect = getActiveRect();
        float f3 = this.mDotRadius;
        canvas.drawRoundRect(activeRect, f3, f3, this.mCirclePaint);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RectF getActiveRect() {
        float f = this.mCurrentPosition;
        float f2 = (int) f;
        float f3 = f - f2;
        float f4 = this.mDotRadius;
        float f5 = f4 * 2.0f;
        float f6 = f4 * 3.0f;
        float width = ((getWidth() - (this.mNumPages * f6)) + this.mDotRadius) / 2.0f;
        RectF rectF = sTempRect;
        rectF.top = (getHeight() * 0.5f) - this.mDotRadius;
        rectF.bottom = (getHeight() * 0.5f) + this.mDotRadius;
        rectF.left = width + (f2 * f6);
        rectF.right = rectF.left + f5;
        if (f3 < 0.5f) {
            rectF.right += f3 * f6 * 2.0f;
        } else {
            rectF.right += f6;
            rectF.left += (f3 - 0.5f) * f6 * 2.0f;
        }
        if (this.mIsRtl) {
            float fWidth = rectF.width();
            rectF.right = getWidth() - rectF.left;
            rectF.left = rectF.right - fWidth;
        }
        return rectF;
    }

    private class MyOutlineProver extends ViewOutlineProvider {
        private MyOutlineProver() {
        }

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            if (PageIndicatorDots.this.mEntryAnimationRadiusFactors == null) {
                RectF activeRect = PageIndicatorDots.this.getActiveRect();
                outline.setRoundRect((int) activeRect.left, (int) activeRect.top, (int) activeRect.right, (int) activeRect.bottom, PageIndicatorDots.this.mDotRadius);
            }
        }
    }

    private class AnimationCycleListener extends AnimatorListenerAdapter {
        private boolean mCancelled;

        private AnimationCycleListener() {
            this.mCancelled = false;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            this.mCancelled = true;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            if (this.mCancelled) {
                return;
            }
            PageIndicatorDots.this.mAnimator = null;
            PageIndicatorDots pageIndicatorDots = PageIndicatorDots.this;
            pageIndicatorDots.animateToPosition(pageIndicatorDots.mFinalPosition);
        }
    }
}
