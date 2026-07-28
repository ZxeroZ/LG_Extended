package com.android.launcher3.anim;

import android.content.Context;
import android.graphics.Path;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.util.DisplayController;

/* JADX INFO: loaded from: classes.dex */
public class Interpolators {
    public static final Interpolator EXAGGERATED_EASE;
    private static final float FAST_FLING_PX_MS = 10.0f;
    private static final int MIN_SETTLE_DURATION = 200;
    public static final Interpolator OVERSHOOT_1_2;
    public static final Interpolator OVERSHOOT_1_7;
    private static final float OVERSHOOT_FACTOR = 0.9f;
    public static final Interpolator SCROLL;
    public static final Interpolator SCROLL_CUBIC;
    public static final Interpolator TOUCH_RESPONSE_INTERPOLATOR;
    public static final Interpolator ZOOM_IN;
    public static final Interpolator ZOOM_OUT;
    public static final Interpolator LINEAR = new LinearInterpolator();
    public static final Interpolator ACCEL = new AccelerateInterpolator();
    public static final Interpolator ACCEL_0_75 = new AccelerateInterpolator(0.75f);
    public static final Interpolator ACCEL_1_5 = new AccelerateInterpolator(1.5f);
    public static final Interpolator ACCEL_2 = new AccelerateInterpolator(2.0f);
    public static final Interpolator ACCEL_7 = new AccelerateInterpolator(7.0f);
    public static final Interpolator DEACCEL = new DecelerateInterpolator();
    public static final Interpolator DEACCEL_1_5 = new DecelerateInterpolator(1.5f);
    public static final Interpolator DEACCEL_1_7 = new DecelerateInterpolator(1.7f);
    public static final Interpolator DEACCEL_2 = new DecelerateInterpolator(2.0f);
    public static final Interpolator DEACCEL_2_5 = new DecelerateInterpolator(2.5f);
    public static final Interpolator DEACCEL_3 = new DecelerateInterpolator(3.0f);
    public static final Interpolator DEACCEL_5 = new DecelerateInterpolator(5.0f);
    public static final Interpolator DEACCEL_7 = new DecelerateInterpolator(7.0f);
    public static final Interpolator ACCEL_DEACCEL = new AccelerateDecelerateInterpolator();
    public static final Interpolator FAST_OUT_SLOW_IN = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator AGGRESSIVE_EASE = new PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f);
    public static final Interpolator AGGRESSIVE_EASE_IN_OUT = new PathInterpolator(0.6f, 0.0f, 0.4f, 1.0f);
    public static final Interpolator DELAYED_OUT = new PathInterpolator(0.0f, 0.0f, 0.99f, 0.3f);
    public static final Interpolator INSTANT = new Interpolator() { // from class: com.android.launcher3.anim.-$$Lambda$Interpolators$1MtJuxUJHSF4_AFPGh6NDb0oWHM
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            return Interpolators.lambda$static$0(f);
        }
    };
    public static final Interpolator FINAL_FRAME = new Interpolator() { // from class: com.android.launcher3.anim.-$$Lambda$Interpolators$BYh-4UQN8RfS1ruklDaswjWbjWc
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            return Interpolators.lambda$static$1(f);
        }
    };
    public static final Interpolator SCROLL_ALIGN_CAROUSEL_VIEW = new PathInterpolator(0.0f, 0.2f, 0.6f, 1.0f);

    static /* synthetic */ float lambda$static$0(float f) {
        return 1.0f;
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: ?: TERNARY null = ((r1v0 float) < (1.0f float)) ? (0.0f float) : (1.0f float) */
    static /* synthetic */ float lambda$static$1(float f) {
        return f < 1.0f ? 0.0f : 1.0f;
    }

    static {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.cubicTo(0.05f, 0.0f, 0.133333f, 0.08f, 0.166666f, 0.4f);
        path.cubicTo(0.225f, 0.94f, 0.5f, 1.0f, 1.0f, 1.0f);
        EXAGGERATED_EASE = new PathInterpolator(path);
        OVERSHOOT_1_2 = new OvershootInterpolator(1.2f);
        OVERSHOOT_1_7 = new OvershootInterpolator(1.7f);
        TOUCH_RESPONSE_INTERPOLATOR = new PathInterpolator(0.3f, 0.0f, 0.1f, 1.0f);
        ZOOM_IN = new Interpolator() { // from class: com.android.launcher3.anim.Interpolators.1
            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float v) {
                return Interpolators.DEACCEL_3.getInterpolation(1.0f - Interpolators.ZOOM_OUT.getInterpolation(1.0f - v));
            }
        };
        ZOOM_OUT = new Interpolator() { // from class: com.android.launcher3.anim.Interpolators.2
            private static final float FOCAL_LENGTH = 0.35f;

            private float zInterpolate(float input) {
                return (1.0f - (FOCAL_LENGTH / (input + FOCAL_LENGTH))) / 0.7407408f;
            }

            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float v) {
                return zInterpolate(v);
            }
        };
        SCROLL = new Interpolator() { // from class: com.android.launcher3.anim.Interpolators.3
            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float t) {
                float f = t - 1.0f;
                return (f * f * f * f * f) + 1.0f;
            }
        };
        SCROLL_CUBIC = new Interpolator() { // from class: com.android.launcher3.anim.Interpolators.4
            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float t) {
                float f = t - 1.0f;
                return (f * f * f) + 1.0f;
            }
        };
    }

    public static Interpolator scrollInterpolatorForVelocity(float velocity) {
        return Math.abs(velocity) > FAST_FLING_PX_MS ? SCROLL : SCROLL_CUBIC;
    }

    public static Interpolator overshootInterpolatorForVelocity(float velocity) {
        return new OvershootInterpolator(Math.min(Math.abs(velocity), 3.0f));
    }

    public static Interpolator clampToProgress(final Interpolator interpolator, final float lowerBound, final float upperBound) {
        if (upperBound <= lowerBound) {
            throw new IllegalArgumentException("lowerBound must be less than upperBound");
        }
        return new Interpolator() { // from class: com.android.launcher3.anim.-$$Lambda$Interpolators$fEJIfG1V7dsxD_CaHgdkyZ5cs3A
            @Override // android.animation.TimeInterpolator
            public final float getInterpolation(float f) {
                return Interpolators.lambda$clampToProgress$2(lowerBound, upperBound, interpolator, f);
            }
        };
    }

    static /* synthetic */ float lambda$clampToProgress$2(float f, float f2, Interpolator interpolator, float f3) {
        if (f3 < f) {
            return 0.0f;
        }
        if (f3 > f2) {
            return 1.0f;
        }
        return interpolator.getInterpolation((f3 - f) / (f2 - f));
    }

    public static Interpolator mapToProgress(final Interpolator interpolator, final float lowerBound, final float upperBound) {
        return new Interpolator() { // from class: com.android.launcher3.anim.-$$Lambda$Interpolators$6QI4XwNFpwVfhfBz92O-VuuwABY
            @Override // android.animation.TimeInterpolator
            public final float getInterpolation(float f) {
                return Utilities.mapRange(interpolator.getInterpolation(f), lowerBound, upperBound);
            }
        };
    }

    public static class OvershootParams {
        public long duration;
        public float end;
        public Interpolator interpolator;
        public float start;

        public OvershootParams(float startProgress, float overshootPastProgress, float endProgress, float velocityPxPerMs, int totalDistancePx, Context context) {
            float fAbs = Math.abs(velocityPxPerMs);
            this.start = startProgress;
            this.end = overshootPastProgress + Utilities.boundToRange((((0.9f * fAbs) * DisplayController.getSingleFrameMs(context)) / totalDistancePx) / 2.0f, 0.02f, 0.15f);
            this.duration = (long) (fAbs / ((fAbs * fAbs) / ((((int) (r4 * r7)) - ((int) (startProgress * r7))) * 2)));
            long jMax = Math.max(200L, ((long) Math.sqrt(((int) ((r4 - endProgress) * r7)) / r3)) * 4);
            long j = this.duration;
            final float f = j / (j + jMax);
            this.duration = j + jMax;
            final Interpolator interpolatorClampToProgress = Interpolators.clampToProgress(Interpolators.DEACCEL, 0.0f, f);
            Interpolator interpolator = Interpolators.ACCEL_DEACCEL;
            float f2 = this.start;
            final Interpolator interpolatorClampToProgress2 = Interpolators.clampToProgress(Interpolators.mapToProgress(interpolator, 1.0f, (endProgress - f2) / (this.end - f2)), f, 1.0f);
            this.interpolator = new Interpolator() { // from class: com.android.launcher3.anim.-$$Lambda$Interpolators$OvershootParams$oxXQiVYlYqwHBI3w1lFujAxmqA8
                @Override // android.animation.TimeInterpolator
                public final float getInterpolation(float f3) {
                    return Interpolators.OvershootParams.lambda$new$0(f, interpolatorClampToProgress, interpolatorClampToProgress2, f3);
                }
            };
        }

        static /* synthetic */ float lambda$new$0(float f, Interpolator interpolator, Interpolator interpolator2, float f2) {
            if (f2 <= f) {
                return interpolator.getInterpolation(f2);
            }
            return interpolator2.getInterpolation(f2);
        }
    }
}
