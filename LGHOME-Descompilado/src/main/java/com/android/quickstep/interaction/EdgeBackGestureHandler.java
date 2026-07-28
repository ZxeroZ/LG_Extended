package com.android.quickstep.interaction;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.SystemProperties;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import com.android.launcher3.ResourceUtils;
import com.android.quickstep.interaction.EdgeBackGesturePanel;

/* JADX INFO: loaded from: classes.dex */
public class EdgeBackGestureHandler implements View.OnTouchListener {
    private static final int MAX_LONG_PRESS_TIMEOUT = SystemProperties.getInt("gestures.back_timeout", 250);
    private static final String TAG = "EdgeBackGestureHandler";
    private int mBottomGestureHeight;
    private final Context mContext;
    private BackGestureResult mDisallowedGestureReason;
    private EdgeBackGesturePanel mEdgeBackPanel;
    private int mEdgeWidth;
    private BackGestureAttemptCallback mGestureCallback;
    private boolean mIsEnabled;
    private int mLeftInset;
    private final int mLongPressTimeout;
    private int mRightInset;
    private final float mTouchSlop;
    private final Point mDisplaySize = new Point();
    private final PointF mDownPoint = new PointF();
    private boolean mThresholdCrossed = false;
    private boolean mAllowGesture = false;
    private final EdgeBackGesturePanel.BackCallback mBackCallback = new EdgeBackGesturePanel.BackCallback() { // from class: com.android.quickstep.interaction.EdgeBackGestureHandler.1
        @Override // com.android.quickstep.interaction.EdgeBackGesturePanel.BackCallback
        public void triggerBack() {
            BackGestureResult backGestureResult;
            if (EdgeBackGestureHandler.this.mGestureCallback != null) {
                BackGestureAttemptCallback backGestureAttemptCallback = EdgeBackGestureHandler.this.mGestureCallback;
                if (EdgeBackGestureHandler.this.mEdgeBackPanel.getIsLeftPanel()) {
                    backGestureResult = BackGestureResult.BACK_COMPLETED_FROM_LEFT;
                } else {
                    backGestureResult = BackGestureResult.BACK_COMPLETED_FROM_RIGHT;
                }
                backGestureAttemptCallback.onBackGestureAttempted(backGestureResult);
            }
        }

        @Override // com.android.quickstep.interaction.EdgeBackGesturePanel.BackCallback
        public void cancelBack() {
            BackGestureResult backGestureResult;
            if (EdgeBackGestureHandler.this.mGestureCallback != null) {
                BackGestureAttemptCallback backGestureAttemptCallback = EdgeBackGestureHandler.this.mGestureCallback;
                if (EdgeBackGestureHandler.this.mEdgeBackPanel.getIsLeftPanel()) {
                    backGestureResult = BackGestureResult.BACK_CANCELLED_FROM_LEFT;
                } else {
                    backGestureResult = BackGestureResult.BACK_CANCELLED_FROM_RIGHT;
                }
                backGestureAttemptCallback.onBackGestureAttempted(backGestureResult);
            }
        }
    };

    interface BackGestureAttemptCallback {
        void onBackGestureAttempted(BackGestureResult result);
    }

    enum BackGestureResult {
        UNKNOWN,
        BACK_COMPLETED_FROM_LEFT,
        BACK_COMPLETED_FROM_RIGHT,
        BACK_CANCELLED_FROM_LEFT,
        BACK_CANCELLED_FROM_RIGHT,
        BACK_NOT_STARTED_TOO_FAR_FROM_EDGE,
        BACK_NOT_STARTED_IN_NAV_BAR_REGION
    }

    EdgeBackGestureHandler(Context context) {
        Resources resources = context.getResources();
        this.mContext = context;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mLongPressTimeout = Math.min(MAX_LONG_PRESS_TIMEOUT, ViewConfiguration.getLongPressTimeout());
        this.mBottomGestureHeight = ResourceUtils.getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, resources);
        this.mEdgeWidth = ResourceUtils.getNavbarSize("config_backGestureInset", resources);
    }

    void setViewGroupParent(ViewGroup parent) {
        this.mIsEnabled = parent != null;
        EdgeBackGesturePanel edgeBackGesturePanel = this.mEdgeBackPanel;
        if (edgeBackGesturePanel != null) {
            edgeBackGesturePanel.onDestroy();
            this.mEdgeBackPanel = null;
        }
        if (this.mIsEnabled) {
            EdgeBackGesturePanel edgeBackGesturePanel2 = new EdgeBackGesturePanel(this.mContext, parent, createLayoutParams());
            this.mEdgeBackPanel = edgeBackGesturePanel2;
            edgeBackGesturePanel2.setBackCallback(this.mBackCallback);
            if (this.mContext.getDisplay() != null) {
                this.mContext.getDisplay().getRealSize(this.mDisplaySize);
                this.mEdgeBackPanel.setDisplaySize(this.mDisplaySize);
            }
        }
    }

    void registerBackGestureAttemptCallback(BackGestureAttemptCallback callback) {
        this.mGestureCallback = callback;
    }

    void unregisterBackGestureAttemptCallback() {
        this.mGestureCallback = null;
    }

    private ViewGroup.LayoutParams createLayoutParams() {
        Resources resources = this.mContext.getResources();
        return new ViewGroup.LayoutParams(ResourceUtils.getNavbarSize("navigation_edge_panel_width", resources), ResourceUtils.getNavbarSize("navigation_edge_panel_height", resources));
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!this.mIsEnabled) {
            return false;
        }
        onMotionEvent(motionEvent);
        return true;
    }

    private boolean isWithinTouchRegion(int x, int y) {
        if (x > this.mEdgeWidth + this.mLeftInset && x < (this.mDisplaySize.x - this.mEdgeWidth) - this.mRightInset) {
            this.mDisallowedGestureReason = BackGestureResult.BACK_NOT_STARTED_TOO_FAR_FROM_EDGE;
            return false;
        }
        if (y < this.mDisplaySize.y - this.mBottomGestureHeight) {
            return true;
        }
        this.mDisallowedGestureReason = BackGestureResult.BACK_NOT_STARTED_IN_NAV_BAR_REGION;
        return false;
    }

    private void cancelGesture(MotionEvent ev) {
        this.mAllowGesture = false;
        MotionEvent motionEventObtain = MotionEvent.obtain(ev);
        motionEventObtain.setAction(3);
        this.mEdgeBackPanel.onMotionEvent(motionEventObtain);
        motionEventObtain.recycle();
    }

    private void onMotionEvent(MotionEvent ev) {
        BackGestureAttemptCallback backGestureAttemptCallback;
        int actionMasked = ev.getActionMasked();
        if (actionMasked == 0) {
            boolean z = ev.getX() <= ((float) (this.mEdgeWidth + this.mLeftInset));
            this.mDisallowedGestureReason = BackGestureResult.UNKNOWN;
            this.mAllowGesture = isWithinTouchRegion((int) ev.getX(), (int) ev.getY());
            this.mDownPoint.set(ev.getX(), ev.getY());
            if (this.mAllowGesture) {
                this.mEdgeBackPanel.setIsLeftPanel(z);
                this.mEdgeBackPanel.onMotionEvent(ev);
                this.mThresholdCrossed = false;
            }
        } else if (this.mAllowGesture) {
            if (!this.mThresholdCrossed) {
                if (actionMasked == 5) {
                    cancelGesture(ev);
                    return;
                }
                if (actionMasked == 2) {
                    if (ev.getEventTime() - ev.getDownTime() > this.mLongPressTimeout) {
                        cancelGesture(ev);
                        return;
                    }
                    float fAbs = Math.abs(ev.getX() - this.mDownPoint.x);
                    float fAbs2 = Math.abs(ev.getY() - this.mDownPoint.y);
                    if (fAbs2 > fAbs && fAbs2 > this.mTouchSlop) {
                        cancelGesture(ev);
                        return;
                    } else if (fAbs > fAbs2 && fAbs > this.mTouchSlop) {
                        this.mThresholdCrossed = true;
                    }
                }
            }
            this.mEdgeBackPanel.onMotionEvent(ev);
        }
        if (actionMasked == 1 || actionMasked == 3) {
            float fAbs3 = Math.abs(ev.getX() - this.mDownPoint.x);
            if (fAbs3 <= Math.abs(ev.getY() - this.mDownPoint.y) || fAbs3 <= this.mTouchSlop || this.mAllowGesture || (backGestureAttemptCallback = this.mGestureCallback) == null) {
                return;
            }
            backGestureAttemptCallback.onBackGestureAttempted(this.mDisallowedGestureReason);
        }
    }

    void setInsets(int leftInset, int rightInset) {
        this.mLeftInset = leftInset;
        this.mRightInset = rightInset;
    }
}
