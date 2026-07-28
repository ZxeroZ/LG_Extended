package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

/* JADX INFO: loaded from: classes.dex */
public abstract class AnimationSuccessListener extends AnimatorListenerAdapter {
    protected boolean mCancelled = false;

    public abstract void onAnimationSuccess(Animator animator);

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animation) {
        this.mCancelled = true;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animation) {
        if (this.mCancelled) {
            return;
        }
        onAnimationSuccess(animation);
    }

    public static AnimationSuccessListener forRunnable(Runnable r) {
        return new RunnableSuccessListener(r);
    }

    private static class RunnableSuccessListener extends AnimationSuccessListener {
        private final Runnable mRunnable;

        private RunnableSuccessListener(Runnable r) {
            this.mRunnable = r;
        }

        @Override // com.android.launcher3.anim.AnimationSuccessListener
        public void onAnimationSuccess(Animator animator) {
            this.mRunnable.run();
        }
    }
}
