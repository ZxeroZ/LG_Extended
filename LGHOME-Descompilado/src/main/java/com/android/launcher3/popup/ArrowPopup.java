package com.android.launcher3.popup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.RevealOutlineAnimation;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.graphics.TriangleShape;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import com.lge.launcher3.R;
import java.util.ArrayList;
import java.util.Collections;

/* JADX INFO: loaded from: classes.dex */
public abstract class ArrowPopup<T extends BaseDraggingActivity> extends AbstractFloatingView {
    private final View mArrow;
    private final int mArrowOffset;
    protected boolean mDeferContainerRemoval;
    private final Rect mEndRect;
    private int mGravity;
    protected final LayoutInflater mInflater;
    protected boolean mIsAboveIcon;
    protected boolean mIsLeftAligned;
    protected final boolean mIsRtl;
    protected final T mLauncher;
    protected Animator mOpenCloseAnimator;
    private final float mOutlineRadius;
    private final Rect mStartRect;
    private final Rect mTempRect;

    protected abstract void getTargetObjectLocation(Rect outPos);

    protected void onCreateCloseAnimation(AnimatorSet anim) {
    }

    protected void onInflationComplete(boolean isReversed) {
    }

    public ArrowPopup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTempRect = new Rect();
        this.mStartRect = new Rect();
        this.mEndRect = new Rect();
        this.mInflater = LayoutInflater.from(context);
        this.mOutlineRadius = Themes.getDialogCornerRadius(context);
        this.mLauncher = (T) BaseDraggingActivity.fromContext(context);
        this.mIsRtl = Utilities.isRtl(getResources());
        Resources resources = getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.popup_arrow_width);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.popup_arrow_height);
        View view = new View(context);
        this.mArrow = view;
        view.setLayoutParams(new BaseDragLayer.LayoutParams(dimensionPixelSize, dimensionPixelSize2));
        this.mArrowOffset = resources.getDimensionPixelSize(R.dimen.popup_arrow_vertical_offset);
    }

    public ArrowPopup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowPopup(Context context) {
        this(context, null, 0);
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        if (animate) {
            animateClose();
        } else {
            closeComplete();
        }
    }

    public <R extends View> R inflateAndAdd(int i, ViewGroup viewGroup) {
        R r = (R) this.mInflater.inflate(i, viewGroup, false);
        viewGroup.addView(r);
        return r;
    }

    public <R extends View> R inflateAndAdd(int i, ViewGroup viewGroup, int i2) {
        R r = (R) this.mInflater.inflate(i, viewGroup, false);
        viewGroup.addView(r, i2);
        return r;
    }

    protected void reorderAndShow(int viewsToFlip) {
        setVisibility(4);
        this.mIsOpen = true;
        getPopupContainer().addView(this);
        orientAboutObject();
        boolean z = this.mIsAboveIcon;
        if (z) {
            int childCount = getChildCount();
            ArrayList arrayList = new ArrayList(childCount);
            for (int i = 0; i < childCount; i++) {
                if (i == viewsToFlip) {
                    Collections.reverse(arrayList);
                }
                arrayList.add(getChildAt(i));
            }
            Collections.reverse(arrayList);
            removeAllViews();
            for (int i2 = 0; i2 < childCount; i2++) {
                addView((View) arrayList.get(i2));
            }
            orientAboutObject();
        }
        onInflationComplete(z);
        Resources resources = getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(isAlignedWithStart() ? R.dimen.popup_arrow_horizontal_center_start : R.dimen.popup_arrow_horizontal_center_end);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.popup_arrow_width) / 2;
        getPopupContainer().addView(this.mArrow);
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) this.mArrow.getLayoutParams();
        if (this.mIsLeftAligned) {
            this.mArrow.setX((getX() + dimensionPixelSize) - dimensionPixelSize2);
        } else {
            this.mArrow.setX(((getX() + getMeasuredWidth()) - dimensionPixelSize) - dimensionPixelSize2);
        }
        if (Gravity.isVertical(this.mGravity)) {
            this.mArrow.setVisibility(4);
        } else {
            ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create(layoutParams.width, layoutParams.height, true ^ this.mIsAboveIcon));
            Paint paint = shapeDrawable.getPaint();
            paint.setColor(Themes.getAttrColor(getContext(), R.attr.popupColorPrimary));
            paint.setPathEffect(new CornerPathEffect(getResources().getDimensionPixelSize(R.dimen.popup_arrow_corner_radius)));
            this.mArrow.setBackground(shapeDrawable);
            if (this.mIsAboveIcon) {
                this.mArrow.setClipBounds(new Rect(0, -this.mArrowOffset, layoutParams.width, layoutParams.height));
            } else {
                this.mArrow.setClipBounds(new Rect(0, 0, layoutParams.width, layoutParams.height + this.mArrowOffset));
            }
            this.mArrow.setElevation(getElevation());
        }
        this.mArrow.setPivotX(layoutParams.width / 2);
        this.mArrow.setPivotY(this.mIsAboveIcon ? layoutParams.height : 0.0f);
        animateOpen();
    }

    protected boolean isAlignedWithStart() {
        boolean z = this.mIsLeftAligned;
        return (z && !this.mIsRtl) || (!z && this.mIsRtl);
    }

    protected void orientAboutObject() {
        orientAboutObject(true, true);
    }

    private void orientAboutObject(boolean allowAlignLeft, boolean allowAlignRight) {
        int dimensionPixelSize;
        int i;
        int i2;
        boolean z = false;
        measure(0, 0);
        int measuredWidth = getMeasuredWidth();
        int dimensionPixelSize2 = this.mArrow.getLayoutParams().height + this.mArrowOffset + getResources().getDimensionPixelSize(R.dimen.popup_vertical_padding);
        int measuredHeight = getMeasuredHeight() + dimensionPixelSize2;
        getTargetObjectLocation(this.mTempRect);
        BaseDragLayer popupContainer = getPopupContainer();
        Rect insets = popupContainer.getInsets();
        int i3 = this.mTempRect.left;
        int i4 = this.mTempRect.right - measuredWidth;
        boolean z2 = !this.mIsRtl ? allowAlignLeft : !allowAlignRight;
        this.mIsLeftAligned = z2;
        int i5 = z2 ? i3 : i4;
        int iWidth = this.mTempRect.width();
        Resources resources = getResources();
        if (isAlignedWithStart()) {
            int dimensionPixelSize3 = resources.getDimensionPixelSize(R.dimen.deep_shortcut_icon_size);
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.popup_padding_start);
            i = iWidth / 2;
            i2 = dimensionPixelSize3 / 2;
        } else {
            int dimensionPixelSize4 = resources.getDimensionPixelSize(R.dimen.deep_shortcut_drag_handle_size);
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.popup_padding_end);
            i = iWidth / 2;
            i2 = dimensionPixelSize4 / 2;
        }
        int i6 = (i - i2) - dimensionPixelSize;
        if (!this.mIsLeftAligned) {
            i6 = -i6;
        }
        int i7 = i5 + i6;
        if (allowAlignLeft || allowAlignRight) {
            boolean z3 = (i7 + measuredWidth) + insets.left < popupContainer.getRight() - insets.right;
            boolean z4 = i7 > popupContainer.getLeft() + insets.left;
            boolean z5 = this.mIsLeftAligned;
            if (!((z5 && z3) || (!z5 && z4))) {
                boolean z6 = allowAlignLeft && !z5;
                if (allowAlignRight && z5) {
                    z = true;
                }
                orientAboutObject(z6, z);
                return;
            }
        }
        int iHeight = this.mTempRect.height();
        int i8 = this.mTempRect.top - measuredHeight;
        boolean z7 = i8 > popupContainer.getTop() + insets.top;
        this.mIsAboveIcon = z7;
        if (!z7) {
            i8 = this.mTempRect.top + iHeight + dimensionPixelSize2;
        }
        int i9 = i7 - insets.left;
        int i10 = i8 - insets.top;
        this.mGravity = 0;
        if (measuredHeight + i10 > popupContainer.getBottom() - insets.bottom) {
            this.mGravity = 16;
            int i11 = (i3 + iWidth) - insets.left;
            int i12 = (i4 - iWidth) - insets.left;
            if (!this.mIsRtl) {
                if (measuredWidth + i11 < popupContainer.getRight()) {
                    this.mIsLeftAligned = true;
                    i9 = i11;
                } else {
                    this.mIsLeftAligned = false;
                    i9 = i12;
                }
            } else if (i12 > popupContainer.getLeft()) {
                this.mIsLeftAligned = false;
                i9 = i12;
            } else {
                this.mIsLeftAligned = true;
                i9 = i11;
            }
            this.mIsAboveIcon = true;
        }
        setX(i9);
        if (Gravity.isVertical(this.mGravity)) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mArrow.getLayoutParams();
        if (this.mIsAboveIcon) {
            layoutParams.gravity = 80;
            layoutParams2.gravity = 80;
            layoutParams.bottomMargin = ((getPopupContainer().getHeight() - i10) - getMeasuredHeight()) - insets.top;
            layoutParams2.bottomMargin = ((layoutParams.bottomMargin - layoutParams2.height) - this.mArrowOffset) - insets.bottom;
            return;
        }
        layoutParams.gravity = 48;
        layoutParams2.gravity = 48;
        layoutParams.topMargin = i10 + insets.top;
        layoutParams2.topMargin = ((layoutParams.topMargin - insets.top) - layoutParams2.height) - this.mArrowOffset;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected Pair<View, String> getAccessibilityTarget() {
        return Pair.create(this, "");
    }

    private void animateOpen() {
        setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        Resources resources = getResources();
        long integer = resources.getInteger(R.integer.config_popupOpenCloseDuration);
        long integer2 = resources.getInteger(R.integer.config_popupArrowOpenCloseDuration);
        Interpolator interpolator = Interpolators.ACCEL_DEACCEL;
        this.mEndRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        final ValueAnimator valueAnimatorCreateRevealAnimator = createOpenCloseOutlineProvider().createRevealAnimator(this, false);
        valueAnimatorCreateRevealAnimator.setDuration(integer);
        valueAnimatorCreateRevealAnimator.setInterpolator(interpolator);
        valueAnimatorCreateRevealAnimator.start();
        valueAnimatorCreateRevealAnimator.pause();
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(integer + integer2);
        valueAnimatorOfFloat.setInterpolator(interpolator);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.popup.-$$Lambda$ArrowPopup$95uIW2y5X1tqSRqwbuvYfIF_ovE
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$animateOpen$0$ArrowPopup(valueAnimatorCreateRevealAnimator, valueAnimator);
            }
        });
        animatorSet.play(valueAnimatorOfFloat);
        this.mArrow.setScaleX(0.0f);
        this.mArrow.setScaleY(0.0f);
        ObjectAnimator duration = ObjectAnimator.ofFloat(this.mArrow, LauncherAnimUtils.SCALE_PROPERTY, 1.0f).setDuration(integer2);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.popup.ArrowPopup.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ArrowPopup.this.setAlpha(1.0f);
                ArrowPopup.this.announceAccessibilityChanges();
                ArrowPopup.this.mOpenCloseAnimator = null;
            }
        });
        this.mOpenCloseAnimator = animatorSet;
        animatorSet.playSequentially(duration, valueAnimatorCreateRevealAnimator);
        animatorSet.start();
    }

    public /* synthetic */ void lambda$animateOpen$0$ArrowPopup(ValueAnimator valueAnimator, ValueAnimator valueAnimator2) {
        float fFloatValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
        this.mArrow.setAlpha(fFloatValue);
        if (!valueAnimator.isStarted()) {
            fFloatValue = 0.0f;
        }
        setAlpha(fFloatValue);
    }

    protected void animateClose() {
        if (this.mIsOpen) {
            if (getOutlineProvider() instanceof RevealOutlineAnimation) {
                ((RevealOutlineAnimation) getOutlineProvider()).getOutline(this.mEndRect);
            } else {
                this.mEndRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            }
            Animator animator = this.mOpenCloseAnimator;
            if (animator != null) {
                animator.cancel();
            }
            this.mIsOpen = false;
            AnimatorSet animatorSet = new AnimatorSet();
            Resources resources = getResources();
            Interpolator interpolator = Interpolators.ACCEL_DEACCEL;
            long integer = resources.getInteger(R.integer.config_popupOpenCloseDuration);
            long integer2 = resources.getInteger(R.integer.config_popupArrowOpenCloseDuration);
            final ObjectAnimator duration = ObjectAnimator.ofFloat(this.mArrow, LauncherAnimUtils.SCALE_PROPERTY, 0.0f).setDuration(integer2);
            ValueAnimator valueAnimatorCreateRevealAnimator = createOpenCloseOutlineProvider().createRevealAnimator(this, true);
            valueAnimatorCreateRevealAnimator.setDuration(integer);
            valueAnimatorCreateRevealAnimator.setInterpolator(interpolator);
            animatorSet.playSequentially(valueAnimatorCreateRevealAnimator, duration);
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(getAlpha(), 0.0f);
            valueAnimatorOfFloat.setDuration(integer + integer2);
            valueAnimatorOfFloat.setInterpolator(interpolator);
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.popup.-$$Lambda$ArrowPopup$_5Nr3BqlDi0j1FhpZ3WrpXriiyo
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$animateClose$1$ArrowPopup(duration, valueAnimator);
                }
            });
            animatorSet.play(valueAnimatorOfFloat);
            onCreateCloseAnimation(animatorSet);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.popup.ArrowPopup.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ArrowPopup.this.mOpenCloseAnimator = null;
                    if (ArrowPopup.this.mDeferContainerRemoval) {
                        ArrowPopup.this.setVisibility(4);
                    } else {
                        ArrowPopup.this.closeComplete();
                    }
                }
            });
            this.mOpenCloseAnimator = animatorSet;
            animatorSet.start();
        }
    }

    public /* synthetic */ void lambda$animateClose$1$ArrowPopup(Animator animator, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mArrow.setAlpha(fFloatValue);
        if (animator.isStarted()) {
            fFloatValue = 0.0f;
        }
        setAlpha(fFloatValue);
    }

    private RoundedRectRevealOutlineProvider createOpenCloseOutlineProvider() {
        Resources resources = getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(this.mIsLeftAligned ^ this.mIsRtl ? R.dimen.popup_arrow_horizontal_center_start : R.dimen.popup_arrow_horizontal_center_end);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.popup_arrow_width) / 2;
        float dimension = resources.getDimension(R.dimen.popup_arrow_corner_radius);
        if (!this.mIsLeftAligned) {
            dimensionPixelSize = getMeasuredWidth() - dimensionPixelSize;
        }
        int measuredHeight = this.mIsAboveIcon ? getMeasuredHeight() : 0;
        this.mStartRect.set(dimensionPixelSize - dimensionPixelSize2, measuredHeight, dimensionPixelSize + dimensionPixelSize2, measuredHeight);
        return new RoundedRectRevealOutlineProvider(dimension, this.mOutlineRadius, this.mStartRect, this.mEndRect);
    }

    protected void closeComplete() {
        Animator animator = this.mOpenCloseAnimator;
        if (animator != null) {
            animator.cancel();
            this.mOpenCloseAnimator = null;
        }
        this.mIsOpen = false;
        this.mDeferContainerRemoval = false;
        getPopupContainer().removeView(this);
        getPopupContainer().removeView(this.mArrow);
    }

    protected BaseDragLayer getPopupContainer() {
        return this.mLauncher.getDragLayer();
    }
}
