package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class AnimatorListeners {
    public static Animator.AnimatorListener forSuccessCallback(Runnable callback) {
        return new RunnableSuccessListener(callback);
    }

    public static Animator.AnimatorListener forEndCallback(Consumer<Boolean> callback) {
        return new EndStateCallbackWrapper(callback);
    }

    public static Animator.AnimatorListener forEndCallback(final Runnable callback) {
        return new AnimatorListenerAdapter() { // from class: com.android.launcher3.anim.AnimatorListeners.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                callback.run();
            }
        };
    }

    private static class EndStateCallbackWrapper extends AnimatorListenerAdapter {
        private final Consumer<Boolean> mListener;
        private boolean mListenerCalled = false;

        EndStateCallbackWrapper(Consumer<Boolean> listener) {
            this.mListener = listener;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            if (this.mListenerCalled) {
                return;
            }
            this.mListenerCalled = true;
            this.mListener.accept(false);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator anim) {
            if (this.mListenerCalled) {
                return;
            }
            boolean z = true;
            this.mListenerCalled = true;
            Consumer<Boolean> consumer = this.mListener;
            if ((anim instanceof ValueAnimator) && ((ValueAnimator) anim).getAnimatedFraction() <= 0.5f) {
                z = false;
            }
            consumer.accept(Boolean.valueOf(z));
        }
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
