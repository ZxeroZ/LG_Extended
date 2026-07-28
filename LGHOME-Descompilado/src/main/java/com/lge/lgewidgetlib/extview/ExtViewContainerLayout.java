package com.lge.lgewidgetlib.extview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/* JADX INFO: loaded from: classes2.dex */
class ExtViewContainerLayout extends FrameLayout {
    private static final int DIMMING_DURATION = 300;
    ValueAnimator.AnimatorUpdateListener mAniListener;
    ValueAnimator mAnimator;
    View.OnTouchListener mTouchListener;

    public ExtViewContainerLayout(Context context, View.OnTouchListener touchListner, ValueAnimator.AnimatorUpdateListener aniListener) {
        super(context);
        this.mTouchListener = touchListner;
        this.mAniListener = aniListener;
        ValueAnimator valueAnimator = new ValueAnimator();
        this.mAnimator = valueAnimator;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.lgewidgetlib.extview.ExtViewContainerLayout.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                ExtViewContainerLayout.this.setBackgroundColor(Color.argb(((Integer) animation.getAnimatedValue()).intValue(), 0, 0, 0));
                if (ExtViewContainerLayout.this.mAniListener != null) {
                    ExtViewContainerLayout.this.mAniListener.onAnimationUpdate(animation);
                }
            }
        });
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent arg0) {
        View.OnTouchListener onTouchListener;
        super.onTouchEvent(arg0);
        if (arg0.getAction() == 1 && (onTouchListener = this.mTouchListener) != null) {
            onTouchListener.onTouch(this, arg0);
        }
        return true;
    }

    public void setDimming(boolean enable, int endAlpha) {
        this.mAnimator.setDuration(300L);
        runDimming(enable, endAlpha);
    }

    public void setDimming(boolean enable, int endAlpha, int duration) {
        this.mAnimator.setDuration(duration);
        runDimming(enable, endAlpha);
    }

    private void runDimming(boolean enable, int endAlpha) {
        if (this.mAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        this.mAnimator.setIntValues(0, endAlpha);
        if (enable) {
            this.mAnimator.start();
        } else {
            this.mAnimator.reverse();
        }
    }
}
