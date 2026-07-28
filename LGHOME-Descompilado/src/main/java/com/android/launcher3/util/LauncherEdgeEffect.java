package com.android.launcher3.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes.dex */
public class LauncherEdgeEffect {
    private static final float EPSILON = 0.001f;
    private static final float MAX_ALPHA = 0.5f;
    private static final float MAX_GLOW_SCALE = 2.0f;
    private static final int MAX_VELOCITY = 10000;
    private static final int MIN_VELOCITY = 100;
    private static final int PULL_DECAY_TIME = 2000;
    private static final float PULL_DISTANCE_ALPHA_GLOW_FACTOR = 0.8f;
    private static final float PULL_GLOW_BEGIN = 0.0f;
    private static final int PULL_TIME = 167;
    private static final int RECEDE_TIME = 600;
    private static final int STATE_ABSORB = 2;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PULL = 1;
    private static final int STATE_PULL_DECAY = 4;
    private static final int STATE_RECEDE = 3;
    private static final int VELOCITY_GLOW_FACTOR = 6;
    private float mBaseGlowScale;
    private float mDisplacement;
    private float mDuration;
    private float mGlowAlpha;
    private float mGlowAlphaFinish;
    private float mGlowAlphaStart;
    private float mGlowScaleY;
    private float mGlowScaleYFinish;
    private float mGlowScaleYStart;
    private final Interpolator mInterpolator;
    private final Paint mPaint;
    private float mPullDistance;
    private float mRadius;
    private long mStartTime;
    private float mTargetDisplacement;
    private static final double ANGLE = 0.5235987755982988d;
    private static final float SIN = (float) Math.sin(ANGLE);
    private static final float COS = (float) Math.cos(ANGLE);
    private int mState = 0;
    private final Rect mBounds = new Rect();

    public LauncherEdgeEffect() {
        Paint paint = new Paint();
        this.mPaint = paint;
        this.mDisplacement = 0.5f;
        this.mTargetDisplacement = 0.5f;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        this.mInterpolator = new DecelerateInterpolator();
    }

    public void setSize(int width, int height) {
        float f = SIN;
        float f2 = (width * 0.5f) / f;
        float f3 = COS;
        float f4 = f2 - (f3 * f2);
        float f5 = height;
        float f6 = (0.75f * f5) / f;
        float f7 = f6 - (f3 * f6);
        this.mRadius = f2;
        this.mBaseGlowScale = f4 > 0.0f ? Math.min(f7 / f4, 1.0f) : 1.0f;
        Rect rect = this.mBounds;
        rect.set(rect.left, this.mBounds.top, width, (int) Math.min(f5, f4));
    }

    public boolean isFinished() {
        return this.mState == 0;
    }

    public void finish() {
        this.mState = 0;
    }

    public void onPull(float deltaDistance) {
        onPull(deltaDistance, 0.5f);
    }

    public void onPull(float deltaDistance, float displacement) {
        long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        this.mTargetDisplacement = displacement;
        int i = this.mState;
        if (i != 4 || jCurrentAnimationTimeMillis - this.mStartTime >= this.mDuration) {
            if (i != 1) {
                this.mGlowScaleY = Math.max(0.0f, this.mGlowScaleY);
            }
            this.mState = 1;
            this.mStartTime = jCurrentAnimationTimeMillis;
            this.mDuration = 167.0f;
            this.mPullDistance += deltaDistance;
            float fMin = Math.min(0.5f, this.mGlowAlpha + (Math.abs(deltaDistance) * 0.8f));
            this.mGlowAlphaStart = fMin;
            this.mGlowAlpha = fMin;
            if (this.mPullDistance == 0.0f) {
                this.mGlowScaleYStart = 0.0f;
                this.mGlowScaleY = 0.0f;
            } else {
                float fMax = (float) (Math.max(0.0d, (1.0d - (1.0d / Math.sqrt(Math.abs(r5) * this.mBounds.height()))) - 0.3d) / 0.7d);
                this.mGlowScaleYStart = fMax;
                this.mGlowScaleY = fMax;
            }
            this.mGlowAlphaFinish = this.mGlowAlpha;
            this.mGlowScaleYFinish = this.mGlowScaleY;
        }
    }

    public void onRelease() {
        this.mPullDistance = 0.0f;
        int i = this.mState;
        if (i == 1 || i == 4) {
            this.mState = 3;
            this.mGlowAlphaStart = this.mGlowAlpha;
            this.mGlowScaleYStart = this.mGlowScaleY;
            this.mGlowAlphaFinish = 0.0f;
            this.mGlowScaleYFinish = 0.0f;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = 600.0f;
        }
    }

    public void onAbsorb(int velocity) {
        this.mState = 2;
        int iMin = Math.min(Math.max(100, Math.abs(velocity)), MAX_VELOCITY);
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mDuration = (iMin * 0.02f) + 0.15f;
        this.mGlowAlphaStart = 0.3f;
        this.mGlowScaleYStart = Math.max(this.mGlowScaleY, 0.0f);
        this.mGlowScaleYFinish = Math.min(((((iMin / 100) * iMin) * 1.5E-4f) / 2.0f) + 0.025f, 1.0f);
        this.mGlowAlphaFinish = Math.max(this.mGlowAlphaStart, Math.min(iMin * 6 * 1.0E-5f, 0.5f));
        this.mTargetDisplacement = 0.5f;
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
    }

    public int getColor() {
        return this.mPaint.getColor();
    }

    public boolean draw(Canvas canvas) {
        boolean z;
        update();
        float fCenterX = this.mBounds.centerX();
        float fHeight = this.mBounds.height() - this.mRadius;
        canvas.scale(1.0f, Math.min(this.mGlowScaleY, 1.0f) * this.mBaseGlowScale, fCenterX, 0.0f);
        float fWidth = (this.mBounds.width() * (Math.max(0.0f, Math.min(this.mDisplacement, 1.0f)) - 0.5f)) / 2.0f;
        this.mPaint.setAlpha((int) (this.mGlowAlpha * 255.0f));
        canvas.drawCircle(fCenterX + fWidth, fHeight, this.mRadius, this.mPaint);
        if (this.mState == 3 && this.mGlowScaleY == 0.0f) {
            this.mState = 0;
            z = true;
        } else {
            z = false;
        }
        return this.mState != 0 || z;
    }

    public int getMaxHeight() {
        return (int) ((this.mBounds.height() * 2.0f) + 0.5f);
    }

    private void update() {
        float fMin = Math.min((AnimationUtils.currentAnimationTimeMillis() - this.mStartTime) / this.mDuration, 1.0f);
        float interpolation = this.mInterpolator.getInterpolation(fMin);
        float f = this.mGlowAlphaStart;
        this.mGlowAlpha = f + ((this.mGlowAlphaFinish - f) * interpolation);
        float f2 = this.mGlowScaleYStart;
        this.mGlowScaleY = f2 + ((this.mGlowScaleYFinish - f2) * interpolation);
        this.mDisplacement = (this.mDisplacement + this.mTargetDisplacement) / 2.0f;
        if (fMin >= 0.999f) {
            int i = this.mState;
            if (i == 1) {
                this.mState = 4;
                this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                this.mDuration = 2000.0f;
                this.mGlowAlphaStart = this.mGlowAlpha;
                this.mGlowScaleYStart = this.mGlowScaleY;
                this.mGlowAlphaFinish = 0.0f;
                this.mGlowScaleYFinish = 0.0f;
                return;
            }
            if (i != 2) {
                if (i == 3) {
                    this.mState = 0;
                    return;
                } else {
                    if (i != 4) {
                        return;
                    }
                    this.mState = 3;
                    return;
                }
            }
            this.mState = 3;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = 600.0f;
            this.mGlowAlphaStart = this.mGlowAlpha;
            this.mGlowScaleYStart = this.mGlowScaleY;
            this.mGlowAlphaFinish = 0.0f;
            this.mGlowScaleYFinish = 0.0f;
        }
    }
}
