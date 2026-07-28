package com.android.launcher3.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.android.launcher3.Utilities;

/* JADX INFO: loaded from: classes.dex */
public abstract class RevealOutlineAnimation extends ViewOutlineProvider {
    protected Rect mOutline = new Rect();
    protected float mOutlineRadius;

    abstract void setProgress(float progress);

    abstract boolean shouldRemoveElevationDuringAnimation();

    public ValueAnimator createRevealAnimator(final View revealView) {
        return createRevealAnimator(revealView, false);
    }

    public ValueAnimator createRevealAnimator(final View revealView, boolean isReversed) {
        ValueAnimator valueAnimatorOfFloat = isReversed ? ValueAnimator.ofFloat(1.0f, 0.0f) : ValueAnimator.ofFloat(0.0f, 1.0f);
        final float elevation = revealView.getElevation();
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.util.RevealOutlineAnimation.1
            private boolean mWasCanceled = false;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                revealView.setOutlineProvider(RevealOutlineAnimation.this);
                revealView.setClipToOutline(true);
                if (RevealOutlineAnimation.this.shouldRemoveElevationDuringAnimation()) {
                    revealView.setTranslationZ(-elevation);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mWasCanceled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (this.mWasCanceled) {
                    return;
                }
                revealView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
                revealView.setClipToOutline(false);
                if (RevealOutlineAnimation.this.shouldRemoveElevationDuringAnimation()) {
                    revealView.setTranslationZ(0.0f);
                }
            }
        });
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.util.RevealOutlineAnimation.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator arg0) {
                RevealOutlineAnimation.this.setProgress(((Float) arg0.getAnimatedValue()).floatValue());
                revealView.invalidateOutline();
                if (Utilities.ATLEAST_LOLLIPOP_MR1) {
                    return;
                }
                revealView.invalidate();
            }
        });
        return valueAnimatorOfFloat;
    }

    @Override // android.view.ViewOutlineProvider
    public void getOutline(View v, Outline outline) {
        outline.setRoundRect(this.mOutline, this.mOutlineRadius);
    }
}
