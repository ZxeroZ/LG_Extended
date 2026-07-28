package androidx.core.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

/* JADX INFO: loaded from: classes.dex */
public final class GestureDetectorCompat {
    private final GestureDetectorCompatImpl mImpl;

    interface GestureDetectorCompatImpl {
        boolean isLongpressEnabled();

        boolean onTouchEvent(MotionEvent ev);

        void setIsLongpressEnabled(boolean enabled);

        void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener listener);
    }

    static class GestureDetectorCompatImplBase implements GestureDetectorCompatImpl {
        private static final int LONG_PRESS = 2;
        private static final int SHOW_PRESS = 1;
        private static final int TAP = 3;
        private boolean mAlwaysInBiggerTapRegion;
        private boolean mAlwaysInTapRegion;
        MotionEvent mCurrentDownEvent;
        boolean mDeferConfirmSingleTap;
        GestureDetector.OnDoubleTapListener mDoubleTapListener;
        private int mDoubleTapSlopSquare;
        private float mDownFocusX;
        private float mDownFocusY;
        private final Handler mHandler;
        private boolean mInLongPress;
        private boolean mIsDoubleTapping;
        private boolean mIsLongpressEnabled;
        private float mLastFocusX;
        private float mLastFocusY;
        final GestureDetector.OnGestureListener mListener;
        private int mMaximumFlingVelocity;
        private int mMinimumFlingVelocity;
        private MotionEvent mPreviousUpEvent;
        boolean mStillDown;
        private int mTouchSlopSquare;
        private VelocityTracker mVelocityTracker;
        private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
        private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();

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
                    GestureDetectorCompatImplBase.this.mListener.onShowPress(GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                    return;
                }
                if (i == 2) {
                    GestureDetectorCompatImplBase.this.dispatchLongPress();
                    return;
                }
                if (i == 3) {
                    if (GestureDetectorCompatImplBase.this.mDoubleTapListener != null) {
                        if (!GestureDetectorCompatImplBase.this.mStillDown) {
                            GestureDetectorCompatImplBase.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                            return;
                        } else {
                            GestureDetectorCompatImplBase.this.mDeferConfirmSingleTap = true;
                            return;
                        }
                    }
                    return;
                }
                throw new RuntimeException("Unknown message " + msg);
            }
        }

        GestureDetectorCompatImplBase(Context context, GestureDetector.OnGestureListener listener, Handler handler) {
            if (handler != null) {
                this.mHandler = new GestureHandler(handler);
            } else {
                this.mHandler = new GestureHandler();
            }
            this.mListener = listener;
            if (listener instanceof GestureDetector.OnDoubleTapListener) {
                setOnDoubleTapListener((GestureDetector.OnDoubleTapListener) listener);
            }
            init(context);
        }

        private void init(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null");
            }
            if (this.mListener == null) {
                throw new IllegalArgumentException("OnGestureListener must not be null");
            }
            this.mIsLongpressEnabled = true;
            ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
            int scaledTouchSlop = viewConfiguration.getScaledTouchSlop();
            int scaledDoubleTapSlop = viewConfiguration.getScaledDoubleTapSlop();
            this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
            this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
            this.mTouchSlopSquare = scaledTouchSlop * scaledTouchSlop;
            this.mDoubleTapSlopSquare = scaledDoubleTapSlop * scaledDoubleTapSlop;
        }

        @Override // androidx.core.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
            this.mDoubleTapListener = onDoubleTapListener;
        }

        @Override // androidx.core.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public void setIsLongpressEnabled(boolean isLongpressEnabled) {
            this.mIsLongpressEnabled = isLongpressEnabled;
        }

        @Override // androidx.core.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public boolean isLongpressEnabled() {
            return this.mIsLongpressEnabled;
        }

        @Override // androidx.core.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public boolean onTouchEvent(MotionEvent ev) {
            boolean zOnDoubleTap;
            MotionEvent motionEvent;
            boolean zOnFling;
            GestureDetector.OnDoubleTapListener onDoubleTapListener;
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
            float f = z ? pointerCount - 1 : pointerCount;
            float f2 = x / f;
            float f3 = y / f;
            if (i == 0) {
                if (this.mDoubleTapListener == null) {
                    zOnDoubleTap = false;
                } else {
                    boolean zHasMessages = this.mHandler.hasMessages(3);
                    if (zHasMessages) {
                        this.mHandler.removeMessages(3);
                    }
                    MotionEvent motionEvent2 = this.mCurrentDownEvent;
                    if (motionEvent2 != null && (motionEvent = this.mPreviousUpEvent) != null && zHasMessages && isConsideredDoubleTap(motionEvent2, motionEvent, ev)) {
                        this.mIsDoubleTapping = true;
                        zOnDoubleTap = this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | false | this.mDoubleTapListener.onDoubleTapEvent(ev);
                    } else {
                        this.mHandler.sendEmptyMessageDelayed(3, DOUBLE_TAP_TIMEOUT);
                        zOnDoubleTap = false;
                    }
                }
                this.mLastFocusX = f2;
                this.mDownFocusX = f2;
                this.mLastFocusY = f3;
                this.mDownFocusY = f3;
                MotionEvent motionEvent3 = this.mCurrentDownEvent;
                if (motionEvent3 != null) {
                    motionEvent3.recycle();
                }
                this.mCurrentDownEvent = MotionEvent.obtain(ev);
                this.mAlwaysInTapRegion = true;
                this.mAlwaysInBiggerTapRegion = true;
                this.mStillDown = true;
                this.mInLongPress = false;
                this.mDeferConfirmSingleTap = false;
                if (this.mIsLongpressEnabled) {
                    this.mHandler.removeMessages(2);
                    this.mHandler.sendEmptyMessageAtTime(2, this.mCurrentDownEvent.getDownTime() + ((long) TAP_TIMEOUT) + ((long) ViewConfiguration.getLongPressTimeout()));
                }
                this.mHandler.sendEmptyMessageAtTime(1, this.mCurrentDownEvent.getDownTime() + ((long) TAP_TIMEOUT));
                return zOnDoubleTap | this.mListener.onDown(ev);
            }
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        cancel();
                        return false;
                    }
                    if (i == 5) {
                        this.mLastFocusX = f2;
                        this.mDownFocusX = f2;
                        this.mLastFocusY = f3;
                        this.mDownFocusY = f3;
                        cancelTaps();
                        return false;
                    }
                    if (i != 6) {
                        return false;
                    }
                    this.mLastFocusX = f2;
                    this.mDownFocusX = f2;
                    this.mLastFocusY = f3;
                    this.mDownFocusY = f3;
                    this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                    int actionIndex2 = ev.getActionIndex();
                    int pointerId = ev.getPointerId(actionIndex2);
                    float xVelocity = this.mVelocityTracker.getXVelocity(pointerId);
                    float yVelocity = this.mVelocityTracker.getYVelocity(pointerId);
                    for (int i3 = 0; i3 < pointerCount; i3++) {
                        if (i3 != actionIndex2) {
                            int pointerId2 = ev.getPointerId(i3);
                            if ((this.mVelocityTracker.getXVelocity(pointerId2) * xVelocity) + (this.mVelocityTracker.getYVelocity(pointerId2) * yVelocity) < 0.0f) {
                                this.mVelocityTracker.clear();
                                return false;
                            }
                        }
                    }
                    return false;
                }
                if (this.mInLongPress) {
                    return false;
                }
                float f4 = this.mLastFocusX - f2;
                float f5 = this.mLastFocusY - f3;
                if (this.mIsDoubleTapping) {
                    return false | this.mDoubleTapListener.onDoubleTapEvent(ev);
                }
                if (!this.mAlwaysInTapRegion) {
                    if (Math.abs(f4) < 1.0f && Math.abs(f5) < 1.0f) {
                        return false;
                    }
                    boolean zOnScroll = this.mListener.onScroll(this.mCurrentDownEvent, ev, f4, f5);
                    this.mLastFocusX = f2;
                    this.mLastFocusY = f3;
                    return zOnScroll;
                }
                int i4 = (int) (f2 - this.mDownFocusX);
                int i5 = (int) (f3 - this.mDownFocusY);
                int i6 = (i4 * i4) + (i5 * i5);
                if (i6 > this.mTouchSlopSquare) {
                    zOnFling = this.mListener.onScroll(this.mCurrentDownEvent, ev, f4, f5);
                    this.mLastFocusX = f2;
                    this.mLastFocusY = f3;
                    this.mAlwaysInTapRegion = false;
                    this.mHandler.removeMessages(3);
                    this.mHandler.removeMessages(1);
                    this.mHandler.removeMessages(2);
                } else {
                    zOnFling = false;
                }
                if (i6 > this.mTouchSlopSquare) {
                    this.mAlwaysInBiggerTapRegion = false;
                }
            } else {
                this.mStillDown = false;
                MotionEvent motionEventObtain = MotionEvent.obtain(ev);
                if (this.mIsDoubleTapping) {
                    zOnFling = this.mDoubleTapListener.onDoubleTapEvent(ev) | false;
                } else {
                    if (this.mInLongPress) {
                        this.mHandler.removeMessages(3);
                        this.mInLongPress = false;
                    } else if (this.mAlwaysInTapRegion) {
                        boolean zOnSingleTapUp = this.mListener.onSingleTapUp(ev);
                        if (this.mDeferConfirmSingleTap && (onDoubleTapListener = this.mDoubleTapListener) != null) {
                            onDoubleTapListener.onSingleTapConfirmed(ev);
                        }
                        zOnFling = zOnSingleTapUp;
                    } else {
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        int pointerId3 = ev.getPointerId(0);
                        velocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                        float yVelocity2 = velocityTracker.getYVelocity(pointerId3);
                        float xVelocity2 = velocityTracker.getXVelocity(pointerId3);
                        if (Math.abs(yVelocity2) > this.mMinimumFlingVelocity || Math.abs(xVelocity2) > this.mMinimumFlingVelocity) {
                            zOnFling = this.mListener.onFling(this.mCurrentDownEvent, ev, xVelocity2, yVelocity2);
                        }
                    }
                    zOnFling = false;
                }
                MotionEvent motionEvent4 = this.mPreviousUpEvent;
                if (motionEvent4 != null) {
                    motionEvent4.recycle();
                }
                this.mPreviousUpEvent = motionEventObtain;
                VelocityTracker velocityTracker2 = this.mVelocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.mVelocityTracker = null;
                }
                this.mIsDoubleTapping = false;
                this.mDeferConfirmSingleTap = false;
                this.mHandler.removeMessages(1);
                this.mHandler.removeMessages(2);
            }
            return zOnFling;
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
            this.mDeferConfirmSingleTap = false;
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
            this.mDeferConfirmSingleTap = false;
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

        void dispatchLongPress() {
            this.mHandler.removeMessages(3);
            this.mDeferConfirmSingleTap = false;
            this.mInLongPress = true;
            this.mListener.onLongPress(this.mCurrentDownEvent);
        }
    }

    static class GestureDetectorCompatImplJellybeanMr2 implements GestureDetectorCompatImpl {
        private final GestureDetector mDetector;

        GestureDetectorCompatImplJellybeanMr2(Context context, GestureDetector.OnGestureListener listener, Handler handler) {
            this.mDetector = new GestureDetector(context, listener, handler);
        }

        @Override // androidx.core.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public boolean isLongpressEnabled() {
            return this.mDetector.isLongpressEnabled();
        }

        @Override // androidx.core.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public boolean onTouchEvent(MotionEvent ev) {
            return this.mDetector.onTouchEvent(ev);
        }

        @Override // androidx.core.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public void setIsLongpressEnabled(boolean enabled) {
            this.mDetector.setIsLongpressEnabled(enabled);
        }

        @Override // androidx.core.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener listener) {
            this.mDetector.setOnDoubleTapListener(listener);
        }
    }

    public GestureDetectorCompat(Context context, GestureDetector.OnGestureListener listener) {
        this(context, listener, null);
    }

    public GestureDetectorCompat(Context context, GestureDetector.OnGestureListener listener, Handler handler) {
        if (Build.VERSION.SDK_INT > 17) {
            this.mImpl = new GestureDetectorCompatImplJellybeanMr2(context, listener, handler);
        } else {
            this.mImpl = new GestureDetectorCompatImplBase(context, listener, handler);
        }
    }

    public boolean isLongpressEnabled() {
        return this.mImpl.isLongpressEnabled();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.mImpl.onTouchEvent(event);
    }

    public void setIsLongpressEnabled(boolean enabled) {
        this.mImpl.setIsLongpressEnabled(enabled);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener listener) {
        this.mImpl.setOnDoubleTapListener(listener);
    }
}
