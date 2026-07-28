package com.lge.launcher3;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.animation.LinearInterpolator;
import com.android.launcher3.dragndrop.DragLayer;
import com.lge.launcher3.util.LGLog;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class BurnInProtectionHelper implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
    public static final int BURN_IN_MAX_RADIUS_DEFAULT = -1;
    private static final int BURN_IN_SHIFT_STEP = 1;
    private static final long CENTERING_ANIMATION_DURATION_MS = 100;
    private static final String TAG = "BurnInProtection";
    private boolean mBurnInProtectionActive;
    private final int mBurnInRadiusMaxSquared;
    private final ValueAnimator mCenteringAnimator;
    private boolean mFirstUpdate;
    private final int mMaxHorizontalBurnInOffset;
    private final int mMaxVerticalBurnInOffset;
    private final int mMinHorizontalBurnInOffset;
    private final int mMinVerticalBurnInOffset;
    private final DragLayer mView;
    private int mLastBurnInXOffset = 0;
    private int mXOffsetDirection = 1;
    private int mLastBurnInYOffset = 0;
    private int mYOffsetDirection = 1;
    private int mAppliedBurnInXOffset = 0;
    private int mAppliedBurnInYOffset = 0;

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animator) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationRepeat(Animator animator) {
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animator) {
    }

    public BurnInProtectionHelper(Context context, int minHorizontalOffset, int maxHorizontalOffset, int minVerticalOffset, int maxVerticalOffset, int maxOffsetRadius, DragLayer view) {
        this.mMinHorizontalBurnInOffset = minHorizontalOffset;
        this.mMaxHorizontalBurnInOffset = maxHorizontalOffset;
        this.mMinVerticalBurnInOffset = minVerticalOffset;
        this.mMaxVerticalBurnInOffset = maxVerticalOffset;
        if (maxOffsetRadius != -1) {
            this.mBurnInRadiusMaxSquared = maxOffsetRadius * maxOffsetRadius;
        } else {
            this.mBurnInRadiusMaxSquared = -1;
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
        this.mCenteringAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(CENTERING_ANIMATION_DURATION_MS);
        valueAnimatorOfFloat.setInterpolator(new LinearInterpolator());
        valueAnimatorOfFloat.addListener(this);
        valueAnimatorOfFloat.addUpdateListener(this);
        this.mView = view;
    }

    public void startBurnInProtection() {
        if (this.mBurnInProtectionActive) {
            return;
        }
        this.mBurnInProtectionActive = true;
        this.mFirstUpdate = true;
        this.mCenteringAnimator.cancel();
        updateBurnInProtection();
    }

    private void updateBurnInProtection() {
        if (this.mBurnInProtectionActive) {
            adjustOffsets();
            this.mAppliedBurnInXOffset = this.mLastBurnInXOffset;
            this.mAppliedBurnInYOffset = this.mLastBurnInYOffset;
            this.mView.mLauncher.getPageindicator().setTranslationX(this.mLastBurnInXOffset);
            this.mView.mLauncher.getPageindicator().setTranslationY(this.mLastBurnInYOffset);
            this.mView.mLauncher.getHotseat().setTranslationX(this.mLastBurnInXOffset);
            this.mView.mLauncher.getHotseat().setTranslationY(this.mLastBurnInYOffset);
            LGLog.d(TAG, "updateBurnInProtection() : " + this.mLastBurnInXOffset + ", " + this.mLastBurnInYOffset);
        }
    }

    public void cancelBurnInProtection() {
        if (this.mBurnInProtectionActive) {
            this.mBurnInProtectionActive = false;
            updateBurnInProtection();
        }
    }

    private void adjustOffsets() {
        int i;
        int i2;
        int i3;
        do {
            int i4 = this.mXOffsetDirection;
            int i5 = i4 * 1;
            int i6 = this.mLastBurnInXOffset + i5;
            this.mLastBurnInXOffset = i6;
            if (i6 > this.mMaxHorizontalBurnInOffset || i6 < this.mMinHorizontalBurnInOffset) {
                this.mLastBurnInXOffset = i6 - i5;
                this.mXOffsetDirection = i4 * (-1);
                int i7 = this.mYOffsetDirection;
                int i8 = i7 * 1;
                int i9 = this.mLastBurnInYOffset + i8;
                this.mLastBurnInYOffset = i9;
                if (i9 > this.mMaxVerticalBurnInOffset || i9 < this.mMinVerticalBurnInOffset) {
                    this.mLastBurnInYOffset = i9 - i8;
                    this.mYOffsetDirection = i7 * (-1);
                }
            }
            i = this.mBurnInRadiusMaxSquared;
            if (i == -1) {
                return;
            }
            i2 = this.mLastBurnInXOffset;
            i3 = this.mLastBurnInYOffset;
        } while ((i2 * i2) + (i3 * i3) > i);
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.println(prefix + TAG);
        String str = prefix + "  ";
        pw.println(str + "mBurnInProtectionActive=" + this.mBurnInProtectionActive);
        pw.println(str + "mHorizontalBurnInOffsetsBounds=(" + this.mMinHorizontalBurnInOffset + ", " + this.mMaxHorizontalBurnInOffset + ")");
        pw.println(str + "mVerticalBurnInOffsetsBounds=(" + this.mMinVerticalBurnInOffset + ", " + this.mMaxVerticalBurnInOffset + ")");
        int i = this.mBurnInRadiusMaxSquared;
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("mBurnInRadiusMaxSquared=");
        sb.append(i);
        pw.println(sb.toString());
        pw.println(str + "mLastBurnInOffset=(" + this.mLastBurnInXOffset + ", " + this.mLastBurnInYOffset + ")");
        pw.println(str + "mOfsetChangeDirections=(" + this.mXOffsetDirection + ", " + this.mYOffsetDirection + ")");
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animator) {
        if (animator != this.mCenteringAnimator || this.mBurnInProtectionActive) {
            return;
        }
        this.mAppliedBurnInXOffset = 0;
        this.mAppliedBurnInYOffset = 0;
        this.mView.setTranslationX(0.0f);
        this.mView.setTranslationY(0.0f);
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (this.mBurnInProtectionActive) {
            return;
        }
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mView.setTranslationX(this.mAppliedBurnInXOffset * fFloatValue);
        this.mView.setTranslationY(this.mAppliedBurnInYOffset * fFloatValue);
    }
}
