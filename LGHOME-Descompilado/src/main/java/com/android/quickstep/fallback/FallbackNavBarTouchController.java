package com.android.quickstep.fallback;

import android.graphics.PointF;
import android.view.MotionEvent;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.TouchController;
import com.android.quickstep.RecentsActivity;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.util.NavBarPosition;
import com.android.quickstep.util.TriggerSwipeUpTouchTracker;

/* JADX INFO: loaded from: classes.dex */
public class FallbackNavBarTouchController implements TouchController, TriggerSwipeUpTouchTracker.OnSwipeUpListener {
    private final RecentsActivity mActivity;
    private final TriggerSwipeUpTouchTracker mTriggerSwipeUpTracker;

    @Override // com.android.quickstep.util.TriggerSwipeUpTouchTracker.OnSwipeUpListener
    public void onSwipeUpCancelled() {
    }

    public FallbackNavBarTouchController(RecentsActivity activity) {
        this.mActivity = activity;
        SysUINavigationMode.Mode mode = SysUINavigationMode.getMode(activity);
        if (mode == SysUINavigationMode.Mode.NO_BUTTON) {
            this.mTriggerSwipeUpTracker = new TriggerSwipeUpTouchTracker(activity, true, new NavBarPosition(mode, DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(activity).getInfo()), null, this);
        } else {
            this.mTriggerSwipeUpTracker = null;
        }
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (!((ev.getEdgeFlags() & 256) != 0) || this.mTriggerSwipeUpTracker == null) {
            return false;
        }
        if (ev.getAction() == 0) {
            this.mTriggerSwipeUpTracker.init();
        }
        onControllerTouchEvent(ev);
        return this.mTriggerSwipeUpTracker.interceptedTouch();
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerTouchEvent(MotionEvent ev) {
        TriggerSwipeUpTouchTracker triggerSwipeUpTouchTracker = this.mTriggerSwipeUpTracker;
        if (triggerSwipeUpTouchTracker == null) {
            return false;
        }
        triggerSwipeUpTouchTracker.onMotionEvent(ev);
        return true;
    }

    @Override // com.android.quickstep.util.TriggerSwipeUpTouchTracker.OnSwipeUpListener
    public void onSwipeUp(boolean wasFling, PointF finalVelocity) {
        ((FallbackRecentsView) this.mActivity.getOverviewPanel()).startHome();
    }
}
