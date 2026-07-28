package com.lge.launcher3.knockoff;

import android.content.ContentResolver;
import android.content.Context;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.knockoff.LGHomeGestureDetector;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.LGUtilFunctionReflect;
import com.lge.launcher3.util.WindowUtils;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class LGKnockOnListener extends LGHomeGestureDetector.SimpleOnGestureListener {
    private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    private static final String TAG = "LGKnockOnListener";
    private Context mContext;
    private MotionEvent mSecondDownEvent = null;

    public LGKnockOnListener(Context context) {
        this.mContext = null;
        Objects.requireNonNull(context, "Context for listener is null");
        this.mContext = context.getApplicationContext();
    }

    @Override // com.lge.launcher3.knockoff.LGHomeGestureDetector.SimpleOnGestureListener, com.lge.launcher3.knockoff.LGHomeGestureDetector.OnDoubleTapListener
    public boolean onDoubleTapEvent(MotionEvent ev) {
        MotionEvent motionEvent;
        int action = ev.getAction() & 255;
        if (action == 0) {
            this.mSecondDownEvent = MotionEvent.obtain(ev);
        } else if (action == 1 && this.mContext != null && (motionEvent = this.mSecondDownEvent) != null) {
            if (isConsideredDoubleTap(motionEvent, ev) && getGestureTurnScreenOn(this.mContext) && !isInVideoCall()) {
                try {
                    LGLog.i(TAG, "call goToSleepWithForce()");
                    LGUtilFunctionReflect.OsManagerReflect.goToSleepWithForce(this.mContext, SystemClock.uptimeMillis(), 0);
                    LGUserLog.send(this.mContext, LGUserLog.FEATURENAME_KNOCK_OFF);
                } catch (Exception e) {
                    LGLog.e(TAG, "onDoubleTap Exception = " + e);
                }
            }
            this.mSecondDownEvent.recycle();
        }
        return false;
    }

    private boolean isConsideredDoubleTap(MotionEvent secondDown, MotionEvent secondUp) {
        int realMillimeterPixel;
        if (secondUp.getEventTime() - secondDown.getEventTime() > DOUBLE_TAP_TIMEOUT) {
            LGLog.i(TAG, "isConsideredDoubleTap() : DOUBLE_TAP_TIMEOUT");
            return false;
        }
        int i = SystemProperties.getInt(LauncherConst.PROPERTY_SYS_KNOCKON_KNOCKOFF_DISTANCE, 0);
        if (i == 0) {
            realMillimeterPixel = ViewConfiguration.get(this.mContext).getScaledDoubleTapSlop();
        } else {
            realMillimeterPixel = WindowUtils.getRealMillimeterPixel(this.mContext, i);
        }
        int i2 = realMillimeterPixel * realMillimeterPixel;
        int x = ((int) secondDown.getX()) - ((int) secondUp.getX());
        int y = ((int) secondDown.getY()) - ((int) secondUp.getY());
        int i3 = (x * x) + (y * y);
        LGLog.i(TAG, "isConsideredDoubleTap() : distanceSquare = " + i3 + ", mDoubleTapSlopSquare = " + i2);
        return i3 < i2;
    }

    private boolean getGestureTurnScreenOn(Context context) {
        if (context == null) {
            LGLog.w(TAG, "getGestureTurnScreenOn() : context is null", new int[0]);
            return false;
        }
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver != null) {
            z = Settings.System.getInt(contentResolver, "gesture_trun_screen_on", 0) == 1;
            LGLog.i(TAG, "getGestureTurnScreenOn() : " + z);
        }
        return z;
    }

    private boolean isInVideoCall() {
        return "com.android.incallui.videocall.ui.InVideoCallActivity".equals(ActivityManagerWrapper.getInstance().getTopActivityAsDisplay(0).getClassName());
    }
}
