package com.android.launcher3.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.android.launcher3.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class UiThreadCircularReveal {
    public static ValueAnimator createCircularReveal(View v, int x, int y, float r0, float r1) {
        return createCircularReveal(v, x, y, r0, r1, ViewOutlineProvider.BACKGROUND);
    }

    public static ValueAnimator createCircularReveal(final View v, int x, int y, float r0, float r1, final ViewOutlineProvider originalProvider) {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        final RevealOutlineProvider revealOutlineProvider = new RevealOutlineProvider(x, y, r0, r1);
        final float elevation = v.getElevation();
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.util.UiThreadCircularReveal.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                v.setOutlineProvider(revealOutlineProvider);
                v.setClipToOutline(true);
                v.setTranslationZ(-elevation);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                v.setOutlineProvider(originalProvider);
                v.setClipToOutline(false);
                v.setTranslationZ(0.0f);
            }
        });
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.util.UiThreadCircularReveal.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator arg0) {
                revealOutlineProvider.setProgress(arg0.getAnimatedFraction());
                v.invalidateOutline();
                if (Utilities.isLmpMR1OrAbove()) {
                    return;
                }
                v.invalidate();
            }
        });
        return valueAnimatorOfFloat;
    }
}
