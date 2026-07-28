package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;

/* JADX INFO: compiled from: WorkspaceStateTransitionAnimation.java */
/* JADX INFO: loaded from: classes.dex */
class AlphaUpdateListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
    private static final float ALPHA_CUTOFF_THRESHOLD = 0.01f;
    private boolean mAccessibilityEnabled;
    private View mView;

    public AlphaUpdateListener(View v, boolean accessibilityEnabled) {
        this.mView = v;
        this.mAccessibilityEnabled = accessibilityEnabled;
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator arg0) {
        updateVisibility(this.mView, this.mAccessibilityEnabled);
    }

    public static void updateVisibility(View view, boolean accessibilityEnabled) {
        int i = accessibilityEnabled ? 8 : 4;
        if (view.getAlpha() < 0.01f && view.getVisibility() != i) {
            view.setVisibility(i);
        } else {
            if (view.getAlpha() <= 0.01f || view.getVisibility() == 0) {
                return;
            }
            view.setVisibility(0);
        }
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator arg0) {
        updateVisibility(this.mView, this.mAccessibilityEnabled);
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator arg0) {
        this.mView.setVisibility(0);
    }
}
