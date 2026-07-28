package com.android.quickstep.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import com.android.launcher3.Alarm;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.testing.TestProtocol;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class MotionPauseDetector {
    private static final long FORCE_PAUSE_TIMEOUT = 300;
    private static final long HARDER_TRIGGER_TIMEOUT = 400;
    private static final float RAPID_DECELERATION_FACTOR = 0.6f;
    private final Context mContext;
    private boolean mDisallowPause;
    private final Alarm mForcePauseTimeout;
    private boolean mHasEverBeenPaused;
    private boolean mIsPaused;
    private final boolean mMakePauseHarderToTrigger;
    private OnMotionPauseListener mOnMotionPauseListener;
    private Float mPreviousVelocity;
    private long mSlowStartTime;
    private final float mSpeedFast;
    private final float mSpeedSlow;
    private final float mSpeedSomewhatFast;
    private final float mSpeedVerySlow;
    private final VelocityProvider mVelocityProvider;

    public interface OnMotionPauseListener {
        void onMotionPauseChanged(boolean isPaused);
    }

    protected interface VelocityProvider {
        Float addMotionEvent(MotionEvent ev, int pointer);

        void clear();
    }

    public MotionPauseDetector(Context context) {
        this(context, false);
    }

    public MotionPauseDetector(Context context, boolean makePauseHarderToTrigger) {
        this(context, makePauseHarderToTrigger, 1);
    }

    public MotionPauseDetector(Context context, boolean makePauseHarderToTrigger, int axis) {
        this.mPreviousVelocity = null;
        this.mContext = context;
        Resources resources = context.getResources();
        this.mSpeedVerySlow = resources.getDimension(R.dimen.motion_pause_detector_speed_very_slow);
        this.mSpeedSlow = resources.getDimension(R.dimen.motion_pause_detector_speed_slow);
        this.mSpeedSomewhatFast = resources.getDimension(R.dimen.motion_pause_detector_speed_somewhat_fast);
        this.mSpeedFast = resources.getDimension(R.dimen.motion_pause_detector_speed_fast);
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "creating alarm");
        }
        Alarm alarm = new Alarm();
        this.mForcePauseTimeout = alarm;
        alarm.setOnAlarmListener(new OnAlarmListener() { // from class: com.android.quickstep.util.-$$Lambda$MotionPauseDetector$qIL3LsJgmgstZUJxtqxl2cJJCqA
            @Override // com.android.launcher3.OnAlarmListener
            public final void onAlarm(Alarm alarm2) {
                this.f$0.lambda$new$0$MotionPauseDetector(alarm2);
            }
        });
        this.mMakePauseHarderToTrigger = makePauseHarderToTrigger;
        this.mVelocityProvider = FeatureFlags.ENABLE_LSQ_VELOCITY_PROVIDER.get() ? new LSqVelocityProvider(axis) : new LinearVelocityProvider(axis);
    }

    public /* synthetic */ void lambda$new$0$MotionPauseDetector(Alarm alarm) {
        updatePaused(true);
    }

    public void setOnMotionPauseListener(OnMotionPauseListener listener) {
        this.mOnMotionPauseListener = listener;
    }

    public void setDisallowPause(boolean disallowPause) {
        this.mDisallowPause = disallowPause;
        updatePaused(this.mIsPaused);
    }

    public void addPosition(MotionEvent ev) {
        addPosition(ev, 0);
    }

    public void addPosition(MotionEvent ev, int pointerIndex) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "setting alarm");
        }
        this.mForcePauseTimeout.setAlarm(this.mMakePauseHarderToTrigger ? HARDER_TRIGGER_TIMEOUT : 300L);
        Float fAddMotionEvent = this.mVelocityProvider.addMotionEvent(ev, pointerIndex);
        if (fAddMotionEvent != null && this.mPreviousVelocity != null) {
            checkMotionPaused(fAddMotionEvent.floatValue(), this.mPreviousVelocity.floatValue(), ev.getEventTime());
        }
        this.mPreviousVelocity = fAddMotionEvent;
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0019  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void checkMotionPaused(float r6, float r7, long r8) {
        /*
            r5 = this;
            float r0 = java.lang.Math.abs(r6)
            float r1 = java.lang.Math.abs(r7)
            boolean r2 = r5.mIsPaused
            r3 = 1
            r4 = 0
            if (r2 == 0) goto L1d
            float r6 = r5.mSpeedFast
            int r7 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r7 < 0) goto L1a
            int r6 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r6 >= 0) goto L19
            goto L1a
        L19:
            r3 = r4
        L1a:
            r4 = r3
            goto L7a
        L1d:
            r2 = 0
            int r6 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r6 >= 0) goto L24
            r6 = r3
            goto L25
        L24:
            r6 = r4
        L25:
            int r7 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r7 >= 0) goto L2b
            r7 = r3
            goto L2c
        L2b:
            r7 = r4
        L2c:
            if (r6 == r7) goto L2f
            goto L7a
        L2f:
            float r6 = r5.mSpeedVerySlow
            int r7 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r7 >= 0) goto L3b
            int r6 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r6 >= 0) goto L3b
            r6 = r3
            goto L3c
        L3b:
            r6 = r4
        L3c:
            if (r6 != 0) goto L58
            boolean r7 = r5.mHasEverBeenPaused
            if (r7 != 0) goto L58
            r6 = 1058642330(0x3f19999a, float:0.6)
            float r1 = r1 * r6
            int r6 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r6 >= 0) goto L4c
            r6 = r3
            goto L4d
        L4c:
            r6 = r4
        L4d:
            if (r6 == 0) goto L57
            float r6 = r5.mSpeedSomewhatFast
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 >= 0) goto L57
            r6 = r3
            goto L58
        L57:
            r6 = r4
        L58:
            boolean r7 = r5.mMakePauseHarderToTrigger
            if (r7 == 0) goto L79
            float r6 = r5.mSpeedSlow
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            r0 = 0
            if (r6 >= 0) goto L76
            long r6 = r5.mSlowStartTime
            int r6 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r6 != 0) goto L6c
            r5.mSlowStartTime = r8
        L6c:
            long r6 = r5.mSlowStartTime
            long r8 = r8 - r6
            r6 = 400(0x190, double:1.976E-321)
            int r6 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r6 < 0) goto L19
            goto L1a
        L76:
            r5.mSlowStartTime = r0
            goto L7a
        L79:
            r4 = r6
        L7a:
            r5.updatePaused(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.util.MotionPauseDetector.checkMotionPaused(float, float, long):void");
    }

    private void updatePaused(boolean isPaused) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "updatePaused: " + isPaused);
        }
        if (this.mDisallowPause) {
            isPaused = false;
        }
        if (this.mIsPaused != isPaused) {
            this.mIsPaused = isPaused;
            if (isPaused) {
                AccessibilityManagerCompat.sendPauseDetectedEventToTest(this.mContext);
                this.mHasEverBeenPaused = true;
            }
            OnMotionPauseListener onMotionPauseListener = this.mOnMotionPauseListener;
            if (onMotionPauseListener != null) {
                onMotionPauseListener.onMotionPauseChanged(this.mIsPaused);
            }
        }
    }

    public void clear() {
        this.mVelocityProvider.clear();
        this.mPreviousVelocity = null;
        setOnMotionPauseListener(null);
        this.mHasEverBeenPaused = false;
        this.mIsPaused = false;
        this.mSlowStartTime = 0L;
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "canceling alarm");
        }
        this.mForcePauseTimeout.cancelAlarm();
    }

    public boolean isPaused() {
        return this.mIsPaused;
    }

    private static class LinearVelocityProvider implements VelocityProvider {
        private final int mAxis;
        private Long mPreviousTime = null;
        private Float mPreviousPosition = null;

        LinearVelocityProvider(int axis) {
            this.mAxis = axis;
        }

        @Override // com.android.quickstep.util.MotionPauseDetector.VelocityProvider
        public Float addMotionEvent(MotionEvent ev, int pointer) {
            Float fValueOf;
            long eventTime = ev.getEventTime();
            float axisValue = ev.getAxisValue(this.mAxis, pointer);
            if (this.mPreviousTime == null || this.mPreviousPosition == null) {
                fValueOf = null;
            } else {
                fValueOf = Float.valueOf((axisValue - this.mPreviousPosition.floatValue()) / Math.max(1L, eventTime - r8.longValue()));
            }
            this.mPreviousTime = Long.valueOf(eventTime);
            this.mPreviousPosition = Float.valueOf(axisValue);
            return fValueOf;
        }

        @Override // com.android.quickstep.util.MotionPauseDetector.VelocityProvider
        public void clear() {
            this.mPreviousTime = null;
            this.mPreviousPosition = null;
        }
    }

    private static class LSqVelocityProvider implements VelocityProvider {
        private static final int HISTORY_SIZE = 20;
        private static final long HORIZON_MS = 100;
        private final int mAxis;
        private final long[] mHistoricTimes = new long[20];
        private final float[] mHistoricPos = new float[20];
        private int mHistoryCount = 0;
        private int mHistoryStart = 0;

        LSqVelocityProvider(int axis) {
            this.mAxis = axis;
        }

        @Override // com.android.quickstep.util.MotionPauseDetector.VelocityProvider
        public void clear() {
            this.mHistoryStart = 0;
            this.mHistoryCount = 0;
        }

        private void addPositionAndTime(long eventTime, float eventPosition) {
            long[] jArr = this.mHistoricTimes;
            int i = this.mHistoryStart;
            jArr[i] = eventTime;
            this.mHistoricPos[i] = eventPosition;
            int i2 = i + 1;
            this.mHistoryStart = i2;
            if (i2 >= 20) {
                this.mHistoryStart = 0;
            }
            this.mHistoryCount = Math.min(20, this.mHistoryCount + 1);
        }

        @Override // com.android.quickstep.util.MotionPauseDetector.VelocityProvider
        public Float addMotionEvent(MotionEvent ev, int pointer) {
            int historySize = ev.getHistorySize();
            for (int i = 0; i < historySize; i++) {
                addPositionAndTime(ev.getHistoricalEventTime(i), ev.getHistoricalAxisValue(this.mAxis, pointer, i));
            }
            int i2 = this.mHistoryStart;
            addPositionAndTime(ev.getEventTime(), ev.getAxisValue(this.mAxis, pointer));
            return solveUnweightedLeastSquaresDeg2(i2);
        }

        private Float solveUnweightedLeastSquaresDeg2(final int pointPos) {
            long j = this.mHistoricTimes[pointPos];
            int i = 0;
            float f = 0.0f;
            float f2 = 0.0f;
            float f3 = 0.0f;
            float f4 = 0.0f;
            float f5 = 0.0f;
            float f6 = 0.0f;
            float f7 = 0.0f;
            for (int i2 = 0; i2 < this.mHistoryCount; i2++) {
                int i3 = pointPos - i2;
                if (i3 < 0) {
                    i3 += 20;
                }
                long j2 = j - this.mHistoricTimes[i3];
                if (j2 > HORIZON_MS) {
                    break;
                }
                i++;
                float f8 = -j2;
                float f9 = this.mHistoricPos[i3];
                float f10 = f8 * f8;
                float f11 = f10 * f8;
                f += f8;
                f2 += f10;
                f3 += f8 * f9;
                f4 += f10 * f9;
                f5 += f9;
                f6 += f11;
                f7 += f11 * f8;
            }
            if (i >= 3) {
                float f12 = i;
                float f13 = f3 - ((f * f5) / f12);
                float f14 = f6 - ((f * f2) / f12);
                float f15 = f4 - ((f5 * f2) / f12);
                float f16 = f7 - ((f2 * f2) / f12);
                float f17 = ((f2 - ((f * f) / f12)) * f16) - (f14 * f14);
                if (f17 == 0.0f) {
                    return null;
                }
                return Float.valueOf(((f13 * f16) - (f15 * f14)) / f17);
            }
            if (i != 1) {
                if (i != 2) {
                    return null;
                }
                int i4 = pointPos - 1;
                if (i4 < 0) {
                    i4 += 20;
                }
                long j3 = j - this.mHistoricTimes[i4];
                if (j3 != 0) {
                    float[] fArr = this.mHistoricPos;
                    return Float.valueOf((fArr[pointPos] - fArr[i4]) / j3);
                }
            }
            return Float.valueOf(0.0f);
        }
    }
}
