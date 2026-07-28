package com.lge.lgewidgetlib.extview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

/* JADX INFO: loaded from: classes2.dex */
class WidgetAnimator extends ValueAnimator {
    private static final int ANI_DURATION = 300;
    private static final boolean DEBUG = true;
    private static final String TAG = "ExtViewEffect";
    private View[] mExpandingViews;
    private int mExtendedWidgetHeight;
    private int mExtendedWidgetTopMargin;
    private int mNormalWidgetHeight;
    private int mNormalWidgetTopMargin;
    private UpdateListener mUpdateListener;
    private ExtViewEventListener mWidgetAnimationListener;
    private View mWidgetContainer;
    private boolean mIsReverse = false;
    private boolean mIsCanceled = false;

    public WidgetAnimator(View widgetContainer, View[] expandingViews, int extendedWidgetHeight, int extendedWidgetTopMargin, ExtViewEventListener animationListener) {
        Log.d(TAG, "WidgetMoveAnimator");
        this.mWidgetContainer = widgetContainer;
        this.mExpandingViews = expandingViews;
        this.mExtendedWidgetTopMargin = extendedWidgetTopMargin;
        reset();
        this.mNormalWidgetHeight = widgetContainer.getHeight();
        this.mExtendedWidgetHeight = extendedWidgetHeight;
        UpdateListener updateListener = new UpdateListener();
        this.mUpdateListener = updateListener;
        addUpdateListener(updateListener);
        addListener(new WidgetAnimatorListener());
        setDuration(300L);
        setInterpolator(new AccelerateDecelerateInterpolator());
        this.mWidgetAnimationListener = animationListener;
    }

    public void reset() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mWidgetContainer.getLayoutParams();
        this.mNormalWidgetTopMargin = layoutParams.topMargin;
        setIntValues(layoutParams.topMargin, this.mExtendedWidgetTopMargin);
    }

    public boolean isNeedToBeMoved() {
        return this.mExtendedWidgetTopMargin != this.mNormalWidgetTopMargin;
    }

    @Override // android.animation.ValueAnimator
    public void reverse() {
        this.mIsReverse = true;
        restoreView();
        if (isNeedToBeMoved()) {
            super.reverse();
        } else {
            this.mWidgetContainer.post(new Runnable() { // from class: com.lge.lgewidgetlib.extview.WidgetAnimator.1
                @Override // java.lang.Runnable
                public void run() {
                    WidgetAnimator.this.mWidgetAnimationListener.onRestoreReqComplete();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void expandView() {
        this.mWidgetContainer.getLayoutParams().height = this.mExtendedWidgetHeight;
        this.mWidgetContainer.requestLayout();
        for (View view : this.mExpandingViews) {
            view.getLayoutParams().height = this.mExtendedWidgetHeight;
            view.requestLayout();
        }
    }

    private void restoreView() {
        this.mWidgetContainer.getLayoutParams().height = this.mNormalWidgetHeight;
        this.mWidgetContainer.requestLayout();
        for (View view : this.mExpandingViews) {
            view.getLayoutParams().height = this.mNormalWidgetHeight;
        }
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void start() {
        this.mIsReverse = false;
        if (isNeedToBeMoved()) {
            super.start();
        } else {
            expandView();
            this.mWidgetContainer.post(new Runnable() { // from class: com.lge.lgewidgetlib.extview.WidgetAnimator.2
                @Override // java.lang.Runnable
                public void run() {
                    WidgetAnimator.this.mWidgetAnimationListener.onExpandReqComplete();
                }
            });
        }
    }

    private final class UpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private UpdateListener() {
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            int iIntValue = ((Integer) animation.getAnimatedValue()).intValue();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) WidgetAnimator.this.mWidgetContainer.getLayoutParams();
            layoutParams.topMargin = iIntValue;
            WidgetAnimator.this.mWidgetContainer.setLayoutParams(layoutParams);
            WidgetAnimator.this.mWidgetContainer.requestLayout();
            if (WidgetAnimator.this.mIsReverse || iIntValue != WidgetAnimator.this.mExtendedWidgetTopMargin) {
                return;
            }
            WidgetAnimator.this.expandView();
        }
    }

    private final class WidgetAnimatorListener implements Animator.AnimatorListener {
        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator arg0) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator arg0) {
        }

        private WidgetAnimatorListener() {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator arg0) {
            WidgetAnimator.this.mWidgetAnimationListener.onCancelReqComplete();
            WidgetAnimator.this.mIsCanceled = true;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator arg0) {
            if (WidgetAnimator.this.mIsCanceled) {
                WidgetAnimator.this.mIsCanceled = false;
            } else if (WidgetAnimator.this.mIsReverse) {
                WidgetAnimator.this.mWidgetAnimationListener.onRestoreReqComplete();
            } else {
                WidgetAnimator.this.mWidgetAnimationListener.onExpandReqComplete();
            }
        }
    }
}
