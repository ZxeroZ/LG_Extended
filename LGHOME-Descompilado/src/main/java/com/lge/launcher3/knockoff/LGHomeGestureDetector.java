package com.lge.launcher3.knockoff;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.view.InputEventConsistencyVerifier;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.WindowUtils;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class LGHomeGestureDetector {
    private static final int LONG_PRESS = 2;
    private static final int SHOW_PRESS = 1;
    private static final int TAP = 3;
    private boolean mAlwaysInBiggerTapRegion;
    private boolean mAlwaysInTapRegion;
    private MotionEvent mCurrentDownEvent;
    private OnDoubleTapListener mDoubleTapListener;
    private int mDoubleTapSlopSquare;
    private int mDoubleTapTouchSlopSquare;
    private float mDownFocusX;
    private float mDownFocusY;
    private final Handler mHandler;
    private boolean mInLongPress;
    private final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    private boolean mIsDoubleTapping;
    private boolean mIsLongpressEnabled;
    private float mLastFocusX;
    private float mLastFocusY;
    private final OnGestureListener mListener;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private MotionEvent mPreviousUpEvent;
    private boolean mStillDown;
    private int mTouchSlopSquare;
    private VelocityTracker mVelocityTracker;
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout() + 100;

    public interface OnDoubleTapListener {
        boolean onDoubleTap(MotionEvent e);

        boolean onDoubleTapEvent(MotionEvent e);

        boolean onSingleTapConfirmed(MotionEvent e);
    }

    public interface OnGestureListener {
        boolean onDown(MotionEvent e);

        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);

        void onLongPress(MotionEvent e);

        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        void onShowPress(MotionEvent e);

        boolean onSingleTapUp(MotionEvent e);
    }

    public static class SimpleOnGestureListener implements OnGestureListener, OnDoubleTapListener {
        @Override // com.lge.launcher3.knockoff.LGHomeGestureDetector.OnDoubleTapListener
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override // com.lge.launcher3.knockoff.LGHomeGestureDetector.OnDoubleTapListener
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override // com.lge.launcher3.knockoff.LGHomeGestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override // com.lge.launcher3.knockoff.LGHomeGestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override // com.lge.launcher3.knockoff.LGHomeGestureDetector.OnGestureListener
        public void onLongPress(MotionEvent e) {
        }

        @Override // com.lge.launcher3.knockoff.LGHomeGestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override // com.lge.launcher3.knockoff.LGHomeGestureDetector.OnGestureListener
        public void onShowPress(MotionEvent e) {
        }

        @Override // com.lge.launcher3.knockoff.LGHomeGestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override // com.lge.launcher3.knockoff.LGHomeGestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }
    }

    private class GestureHandler extends Handler {
        GestureHandler() {
        }

        GestureHandler(Handler handler) {
            super(handler.getLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                LGHomeGestureDetector.this.mListener.onShowPress(LGHomeGestureDetector.this.mCurrentDownEvent);
                return;
            }
            if (i == 2) {
                LGHomeGestureDetector.this.dispatchLongPress();
                return;
            }
            if (i == 3) {
                if (LGHomeGestureDetector.this.mDoubleTapListener == null || LGHomeGestureDetector.this.mStillDown) {
                    return;
                }
                LGHomeGestureDetector.this.mDoubleTapListener.onSingleTapConfirmed(LGHomeGestureDetector.this.mCurrentDownEvent);
                return;
            }
            throw new RuntimeException("Unknown message " + msg);
        }
    }

    @Deprecated
    public LGHomeGestureDetector(OnGestureListener listener, Handler handler) {
        this(null, listener, handler);
    }

    @Deprecated
    public LGHomeGestureDetector(OnGestureListener listener) {
        this(null, listener, null);
    }

    public LGHomeGestureDetector(Context context, OnGestureListener listener) {
        this(context, listener, null);
    }

    public LGHomeGestureDetector(Context context, OnGestureListener listener, Handler handler) {
        this.mInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0) : null;
        if (handler != null) {
            this.mHandler = new GestureHandler(handler);
        } else {
            this.mHandler = new GestureHandler();
        }
        this.mListener = listener;
        if (listener instanceof OnDoubleTapListener) {
            setOnDoubleTapListener((OnDoubleTapListener) listener);
        }
        init(context);
    }

    public LGHomeGestureDetector(Context context, OnGestureListener listener, Handler handler, boolean unused) {
        this(context, listener, handler);
    }

    private void init(Context context) {
        int scaledDoubleTapTouchSlop;
        int realMillimeterPixel;
        int doubleTapSlop;
        int touchSlop;
        Objects.requireNonNull(this.mListener, "OnGestureListener must not be null");
        this.mIsLongpressEnabled = true;
        if (context == null) {
            touchSlop = ViewConfiguration.getTouchSlop();
            doubleTapSlop = ViewConfiguration.getDoubleTapSlop();
            this.mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
            this.mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
            scaledDoubleTapTouchSlop = touchSlop;
        } else {
            ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
            int scaledTouchSlop = viewConfiguration.getScaledTouchSlop();
            scaledDoubleTapTouchSlop = viewConfiguration.getScaledDoubleTapTouchSlop();
            int i = SystemProperties.getInt(LauncherConst.PROPERTY_SYS_KNOCKON_KNOCKOFF_DISTANCE, 0);
            if (i == 0) {
                realMillimeterPixel = viewConfiguration.getScaledDoubleTapSlop();
            } else {
                realMillimeterPixel = WindowUtils.getRealMillimeterPixel(context, i);
            }
            this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
            this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
            doubleTapSlop = realMillimeterPixel;
            touchSlop = scaledTouchSlop;
        }
        this.mTouchSlopSquare = touchSlop * touchSlop;
        this.mDoubleTapTouchSlopSquare = scaledDoubleTapTouchSlop * scaledDoubleTapTouchSlop;
        this.mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        this.mDoubleTapListener = onDoubleTapListener;
    }

    public void setIsLongpressEnabled(boolean isLongpressEnabled) {
        this.mIsLongpressEnabled = isLongpressEnabled;
    }

    public boolean isLongpressEnabled() {
        return this.mIsLongpressEnabled;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean zProcessTouchDown;
        InputEventConsistencyVerifier inputEventConsistencyVerifier;
        InputEventConsistencyVerifier inputEventConsistencyVerifier2 = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier2 != null) {
            inputEventConsistencyVerifier2.onTouchEvent(ev, 0);
        }
        int action = ev.getAction();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        int i = action & 255;
        boolean z = i == 6;
        int actionIndex = z ? ev.getActionIndex() : -1;
        int pointerCount = ev.getPointerCount();
        float x = 0.0f;
        float y = 0.0f;
        for (int i2 = 0; i2 < pointerCount; i2++) {
            if (actionIndex != i2) {
                x += ev.getX(i2);
                y += ev.getY(i2);
            }
        }
        if (z) {
            pointerCount--;
        }
        float f = pointerCount;
        float f2 = x / f;
        float f3 = y / f;
        if (i == 0) {
            zProcessTouchDown = processTouchDown(ev, f2, f3, false);
        } else if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    cancel();
                } else if (i == 5) {
                    this.mLastFocusX = f2;
                    this.mDownFocusX = f2;
                    this.mLastFocusY = f3;
                    this.mDownFocusY = f3;
                    cancelTaps();
                } else if (i == 6) {
                    this.mLastFocusX = f2;
                    this.mDownFocusX = f2;
                    this.mLastFocusY = f3;
                    this.mDownFocusY = f3;
                }
            } else if (!this.mInLongPress) {
                zProcessTouchDown = processTouchMove(ev, f2, f3, false);
            }
            zProcessTouchDown = false;
        } else {
            zProcessTouchDown = processTouchUp(ev, false);
        }
        if (!zProcessTouchDown && (inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier) != null) {
            inputEventConsistencyVerifier.onUnhandledEvent(ev, 0);
        }
        return zProcessTouchDown;
    }

    private boolean processTouchDown(MotionEvent ev, final float focusX, final float focusY, boolean handled) {
        MotionEvent motionEvent;
        if (this.mDoubleTapListener != null) {
            boolean zHasMessages = this.mHandler.hasMessages(3);
            if (zHasMessages) {
                this.mHandler.removeMessages(3);
            }
            MotionEvent motionEvent2 = this.mCurrentDownEvent;
            if (motionEvent2 != null && (motionEvent = this.mPreviousUpEvent) != null && zHasMessages && isConsideredDoubleTap(motionEvent2, motionEvent, ev)) {
                this.mIsDoubleTapping = true;
                handled = handled | this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | this.mDoubleTapListener.onDoubleTapEvent(ev);
            } else {
                this.mHandler.sendEmptyMessageDelayed(3, DOUBLE_TAP_TIMEOUT);
            }
        }
        this.mLastFocusX = focusX;
        this.mDownFocusX = focusX;
        this.mLastFocusY = focusY;
        this.mDownFocusY = focusY;
        MotionEvent motionEvent3 = this.mCurrentDownEvent;
        if (motionEvent3 != null) {
            motionEvent3.recycle();
        }
        this.mCurrentDownEvent = MotionEvent.obtain(ev);
        this.mAlwaysInTapRegion = true;
        this.mAlwaysInBiggerTapRegion = true;
        this.mStillDown = true;
        this.mInLongPress = false;
        if (this.mIsLongpressEnabled) {
            this.mHandler.removeMessages(2);
            this.mHandler.sendEmptyMessageAtTime(2, this.mCurrentDownEvent.getDownTime() + ((long) TAP_TIMEOUT) + ((long) LONGPRESS_TIMEOUT));
        }
        this.mHandler.sendEmptyMessageAtTime(1, this.mCurrentDownEvent.getDownTime() + ((long) TAP_TIMEOUT));
        return this.mListener.onDown(ev) | handled;
    }

    private boolean processTouchMove(MotionEvent ev, final float focusX, final float focusY, boolean handled) {
        float f = this.mLastFocusX - focusX;
        float f2 = this.mLastFocusY - focusY;
        if (this.mIsDoubleTapping) {
            return handled | this.mDoubleTapListener.onDoubleTapEvent(ev);
        }
        if (!this.mAlwaysInTapRegion) {
            if (Math.abs(f) < 1.0f && Math.abs(f2) < 1.0f) {
                return handled;
            }
            boolean zOnScroll = this.mListener.onScroll(this.mCurrentDownEvent, ev, f, f2);
            this.mLastFocusX = focusX;
            this.mLastFocusY = focusY;
            return zOnScroll;
        }
        int i = (int) (focusX - this.mDownFocusX);
        int i2 = (int) (focusY - this.mDownFocusY);
        int i3 = (i * i) + (i2 * i2);
        if (i3 > this.mTouchSlopSquare) {
            boolean zOnScroll2 = this.mListener.onScroll(this.mCurrentDownEvent, ev, f, f2);
            this.mLastFocusX = focusX;
            this.mLastFocusY = focusY;
            this.mAlwaysInTapRegion = false;
            this.mHandler.removeMessages(3);
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            handled = zOnScroll2;
        }
        if (i3 <= this.mDoubleTapTouchSlopSquare) {
            return handled;
        }
        this.mAlwaysInBiggerTapRegion = false;
        return handled;
    }

    private boolean processTouchUp(MotionEvent ev, boolean handled) {
        this.mStillDown = false;
        MotionEvent motionEventObtain = MotionEvent.obtain(ev);
        if (this.mIsDoubleTapping) {
            handled |= this.mDoubleTapListener.onDoubleTapEvent(ev);
        } else if (this.mInLongPress) {
            this.mHandler.removeMessages(3);
            this.mInLongPress = false;
        } else if (this.mAlwaysInTapRegion) {
            handled = this.mListener.onSingleTapUp(ev);
        } else {
            VelocityTracker velocityTracker = this.mVelocityTracker;
            int pointerId = ev.getPointerId(0);
            velocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
            float yVelocity = velocityTracker.getYVelocity(pointerId);
            float xVelocity = velocityTracker.getXVelocity(pointerId);
            if (Math.abs(yVelocity) > this.mMinimumFlingVelocity || Math.abs(xVelocity) > this.mMinimumFlingVelocity) {
                handled = this.mListener.onFling(this.mCurrentDownEvent, ev, xVelocity, yVelocity);
            }
        }
        MotionEvent motionEvent = this.mPreviousUpEvent;
        if (motionEvent != null) {
            motionEvent.recycle();
        }
        this.mPreviousUpEvent = motionEventObtain;
        VelocityTracker velocityTracker2 = this.mVelocityTracker;
        if (velocityTracker2 != null) {
            velocityTracker2.recycle();
            this.mVelocityTracker = null;
        }
        this.mIsDoubleTapping = false;
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        return handled;
    }

    private void cancel() {
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(3);
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
        this.mIsDoubleTapping = false;
        this.mStillDown = false;
        this.mAlwaysInTapRegion = false;
        this.mAlwaysInBiggerTapRegion = false;
        if (this.mInLongPress) {
            this.mInLongPress = false;
        }
    }

    private void cancelTaps() {
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(3);
        this.mIsDoubleTapping = false;
        this.mAlwaysInTapRegion = false;
        this.mAlwaysInBiggerTapRegion = false;
        if (this.mInLongPress) {
            this.mInLongPress = false;
        }
    }

    private boolean isConsideredDoubleTap(MotionEvent firstDown, MotionEvent firstUp, MotionEvent secondDown) {
        if (!this.mAlwaysInBiggerTapRegion || secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
            return false;
        }
        int x = ((int) firstDown.getX()) - ((int) secondDown.getX());
        int y = ((int) firstDown.getY()) - ((int) secondDown.getY());
        return (x * x) + (y * y) < this.mDoubleTapSlopSquare;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchLongPress() {
        this.mHandler.removeMessages(3);
        this.mInLongPress = true;
        this.mListener.onLongPress(this.mCurrentDownEvent);
    }
}
