package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;

/* JADX INFO: loaded from: classes.dex */
public class FirstFrameAnimatorHelper extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
    private static final boolean DEBUG = false;
    private static final int IDEAL_FRAME_DURATION = 16;
    private static final int MAX_DELAY = 1000;
    private static ViewTreeObserver.OnDrawListener sGlobalDrawListener;
    static long sGlobalFrameCounter;
    private static boolean sVisible;
    private boolean mAdjustedSecondFrameTime;
    private boolean mHandlingOnAnimationUpdate;
    private long mStartFrame;
    private long mStartTime = -1;
    private View mTarget;

    public FirstFrameAnimatorHelper(ValueAnimator animator, View target) {
        this.mTarget = target;
        animator.addUpdateListener(this);
    }

    public FirstFrameAnimatorHelper(ViewPropertyAnimator vpa, View target) {
        this.mTarget = target;
        vpa.setListener(this);
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animation) {
        ValueAnimator valueAnimator = (ValueAnimator) animation;
        valueAnimator.addUpdateListener(this);
        onAnimationUpdate(valueAnimator);
    }

    public static void setIsVisible(boolean visible) {
        sVisible = visible;
    }

    public static void initializeDrawListener(View view) {
        if (sGlobalDrawListener != null) {
            view.getViewTreeObserver().removeOnDrawListener(sGlobalDrawListener);
        }
        sGlobalDrawListener = new ViewTreeObserver.OnDrawListener() { // from class: com.android.launcher3.FirstFrameAnimatorHelper.1
            private long mTime = System.currentTimeMillis();

            @Override // android.view.ViewTreeObserver.OnDrawListener
            public void onDraw() {
                FirstFrameAnimatorHelper.sGlobalFrameCounter++;
            }
        };
        view.getViewTreeObserver().addOnDrawListener(sGlobalDrawListener);
        sVisible = true;
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0085  */
    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onAnimationUpdate(final android.animation.ValueAnimator r16) {
        /*
            r15 = this;
            r0 = r15
            r1 = r16
            long r2 = java.lang.System.currentTimeMillis()
            long r4 = r0.mStartTime
            r6 = -1
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 != 0) goto L15
            long r4 = com.android.launcher3.FirstFrameAnimatorHelper.sGlobalFrameCounter
            r0.mStartFrame = r4
            r0.mStartTime = r2
        L15:
            long r4 = r16.getCurrentPlayTime()
            r6 = 1065353216(0x3f800000, float:1.0)
            float r7 = r16.getAnimatedFraction()
            int r6 = java.lang.Float.compare(r6, r7)
            r8 = 1
            if (r6 != 0) goto L28
            r6 = r8
            goto L29
        L28:
            r6 = 0
        L29:
            boolean r9 = r0.mHandlingOnAnimationUpdate
            if (r9 != 0) goto L94
            boolean r9 = com.android.launcher3.FirstFrameAnimatorHelper.sVisible
            if (r9 == 0) goto L94
            long r9 = r16.getDuration()
            int r9 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r9 >= 0) goto L94
            if (r6 != 0) goto L94
            r0.mHandlingOnAnimationUpdate = r8
            long r9 = com.android.launcher3.FirstFrameAnimatorHelper.sGlobalFrameCounter
            long r11 = r0.mStartFrame
            long r9 = r9 - r11
            r11 = 0
            int r6 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            r13 = 1000(0x3e8, double:4.94E-321)
            if (r6 != 0) goto L62
            long r7 = r0.mStartTime
            long r7 = r7 + r13
            int r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r7 >= 0) goto L62
            int r7 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r7 <= 0) goto L62
            android.view.View r2 = r0.mTarget
            android.view.View r2 = r2.getRootView()
            r2.invalidate()
            r1.setCurrentPlayTime(r11)
            goto L91
        L62:
            r7 = 1
            int r7 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r7 != 0) goto L85
            long r8 = r0.mStartTime
            long r13 = r13 + r8
            int r10 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r10 >= 0) goto L85
            boolean r10 = r0.mAdjustedSecondFrameTime
            if (r10 != 0) goto L85
            r10 = 16
            long r8 = r8 + r10
            int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r2 <= 0) goto L85
            int r2 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r2 <= 0) goto L85
            r1.setCurrentPlayTime(r10)
            r1 = 1
            r0.mAdjustedSecondFrameTime = r1
            goto L91
        L85:
            if (r7 <= 0) goto L91
            android.view.View r2 = r0.mTarget
            com.android.launcher3.FirstFrameAnimatorHelper$2 r3 = new com.android.launcher3.FirstFrameAnimatorHelper$2
            r3.<init>()
            r2.post(r3)
        L91:
            r1 = 0
            r0.mHandlingOnAnimationUpdate = r1
        L94:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.FirstFrameAnimatorHelper.onAnimationUpdate(android.animation.ValueAnimator):void");
    }

    public void print(ValueAnimator animation) {
        long j = sGlobalFrameCounter;
        long j2 = j - this.mStartFrame;
        View view = this.mTarget;
        boolean zIsDirty = view.isDirty();
        Log.d("FirstFrameAnimatorHelper", j + "(" + j2 + ") " + view + " dirty? " + zIsDirty + " " + (animation.getCurrentPlayTime() / animation.getDuration()) + " " + this + " " + animation);
    }
}
