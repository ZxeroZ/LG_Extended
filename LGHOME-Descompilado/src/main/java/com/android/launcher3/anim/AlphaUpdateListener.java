package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class AlphaUpdateListener extends AnimationSuccessListener implements ValueAnimator.AnimatorUpdateListener {
    public static final float ALPHA_CUTOFF_THRESHOLD = 0.01f;
    private static final String TAG = "AlphaUpdateListener";
    private View mView;

    public AlphaUpdateListener(View v) {
        this.mView = v;
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator arg0) {
        updateVisibility(this.mView);
    }

    @Override // com.android.launcher3.anim.AnimationSuccessListener
    public void onAnimationSuccess(Animator animator) {
        updateVisibility(this.mView);
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator arg0) {
        if (this.mCancelled) {
            this.mCancelled = false;
            LGLog.d(TAG, "onAnimationStart : mCancelled change to false. " + this.mView);
        }
        this.mView.setVisibility(0);
    }

    public static void updateVisibility(View view) {
        if (view.getAlpha() < 0.01f && view.getVisibility() != 4) {
            view.setVisibility(4);
        } else {
            if (view.getAlpha() <= 0.01f || view.getVisibility() == 0) {
                return;
            }
            view.setVisibility(0);
        }
    }
}
