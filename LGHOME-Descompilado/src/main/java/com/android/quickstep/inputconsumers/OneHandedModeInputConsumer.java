package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.SystemUiProxy;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class OneHandedModeInputConsumer extends DelegateInputConsumer {
    private static final int ANGLE_MAX = 150;
    private static final int ANGLE_MIN = 30;
    private final Context mContext;
    private final RecentsAnimationDeviceState mDeviceState;
    private final Point mDisplaySize;
    private final PointF mDownPos;
    private final float mDragDistThreshold;
    private boolean mIsStopGesture;
    private final PointF mLastPos;
    private final int mNavBarSize;
    private boolean mPassedSlop;
    private final float mSquaredSlop;

    public OneHandedModeInputConsumer(Context context, RecentsAnimationDeviceState deviceState, InputConsumer delegate, InputMonitorCompat inputMonitor) {
        super(delegate, inputMonitor);
        this.mDownPos = new PointF();
        this.mLastPos = new PointF();
        this.mContext = context;
        this.mDeviceState = deviceState;
        this.mDragDistThreshold = context.getResources().getDimensionPixelSize(R.dimen.gestures_onehanded_drag_threshold);
        this.mSquaredSlop = Utilities.squaredTouchSlop(context);
        this.mDisplaySize = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getInfo().currentSize;
        this.mNavBarSize = ResourceUtils.getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, context.getResources());
    }

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return this.mDelegate.getType() | 2048;
    }

    @Override // com.android.quickstep.InputConsumer
    public void onMotionEvent(MotionEvent ev) {
        int actionMasked = ev.getActionMasked();
        if (actionMasked == 0) {
            this.mDownPos.set(ev.getX(), ev.getY());
            this.mLastPos.set(this.mDownPos);
        } else if (actionMasked == 1) {
            if (this.mLastPos.y >= this.mDownPos.y && this.mPassedSlop) {
                onStartGestureDetected();
            } else if (this.mIsStopGesture) {
                onStopGestureDetected();
            }
            clearState();
        } else if (actionMasked != 2) {
            if (actionMasked == 3) {
                clearState();
            }
        } else if (this.mState != 2) {
            if (!this.mDelegate.allowInterceptByParent()) {
                this.mState = 2;
            } else {
                this.mLastPos.set(ev.getX(), ev.getY());
                if (this.mPassedSlop) {
                    if (((float) Math.hypot(this.mLastPos.x - this.mDownPos.x, this.mLastPos.y - this.mDownPos.y)) > this.mDragDistThreshold && this.mPassedSlop) {
                        this.mIsStopGesture = true;
                    }
                } else if (Utilities.squaredHypot(this.mLastPos.x - this.mDownPos.x, this.mLastPos.y - this.mDownPos.y) > this.mSquaredSlop) {
                    if ((!this.mDeviceState.isOneHandedModeActive() && isValidStartAngle(this.mDownPos.x - this.mLastPos.x, this.mDownPos.y - this.mLastPos.y)) || (this.mDeviceState.isOneHandedModeActive() && isValidExitAngle(this.mDownPos.x - this.mLastPos.x, this.mDownPos.y - this.mLastPos.y))) {
                        this.mPassedSlop = isInSystemGestureRegion(this.mLastPos);
                        setActive(ev);
                    } else {
                        this.mState = 2;
                    }
                }
            }
        }
        if (this.mState != 1) {
            this.mDelegate.onMotionEvent(ev);
        }
    }

    private void clearState() {
        this.mPassedSlop = false;
        this.mState = 0;
        this.mIsStopGesture = false;
    }

    private void onStartGestureDetected() {
        if (this.mDeviceState.isSwipeToNotificationEnabled()) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).expandNotificationPanel();
        } else {
            if (this.mDeviceState.isOneHandedModeActive()) {
                return;
            }
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).startOneHandedMode();
        }
    }

    private void onStopGestureDetected() {
        if (this.mDeviceState.isOneHandedModeEnabled() && this.mDeviceState.isOneHandedModeActive()) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).stopOneHandedMode();
        }
    }

    private boolean isInSystemGestureRegion(PointF lastPos) {
        return this.mDeviceState.isGesturalNavMode() && lastPos.y > ((float) (this.mDisplaySize.y - this.mNavBarSize));
    }

    private boolean isValidStartAngle(float deltaX, float deltaY) {
        float degrees = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
        return degrees > -150.0f && degrees < -30.0f;
    }

    private boolean isValidExitAngle(float deltaX, float deltaY) {
        float degrees = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
        return degrees > 30.0f && degrees < 150.0f;
    }
}
