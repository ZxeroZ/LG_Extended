package com.android.launcher3.uioverrides.touchcontrollers;

import android.graphics.PointF;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.util.TouchController;
import com.android.quickstep.SystemUiProxy;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGHomeFeature;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class StatusBarTouchController implements TouchController {
    private static final String TAG = "StatusBarController";
    private boolean mCanIntercept;
    private final SparseArray<PointF> mDownEvents = new SparseArray<>();
    private int mLastAction;
    private final Launcher mLauncher;
    private final SystemUiProxy mSystemUiProxy;
    private final float mTouchSlop;

    public StatusBarTouchController(Launcher l) {
        this.mLauncher = l;
        this.mSystemUiProxy = SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(l);
        this.mTouchSlop = ViewConfiguration.get(l).getScaledTouchSlop() * 2;
    }

    @Override // com.android.launcher3.util.TouchController
    public void dump(String prefix, PrintWriter writer) {
        writer.println(prefix + "mCanIntercept:" + this.mCanIntercept);
        writer.println(prefix + "mLastAction:" + MotionEvent.actionToString(this.mLastAction));
        writer.println(prefix + "mSysUiProxy available:" + SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).isActive());
    }

    private void dispatchTouchEvent(MotionEvent ev) {
        if (this.mSystemUiProxy.isActive()) {
            this.mLastAction = ev.getActionMasked();
            this.mSystemUiProxy.onStatusBarMotionEvent(ev);
        }
    }

    @Override // com.android.launcher3.util.TouchController
    public final boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        Launcher launcher = this.mLauncher;
        if (launcher != null && launcher.getStateManager().getState() == LauncherState.NORMAL && ((LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || HomeSettingsSharedPreferences.getSwipeDownHome(this.mLauncher.getApplicationContext()) == Utilities.SWIPE_DOWN_HOME_NOTIFICATION_PANEL) && ((!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || HomeSettingsSharedPreferences.getSwipeDownSwivelHome(this.mLauncher.getApplicationContext()) == Utilities.SWIPE_DOWN_HOME_NOTIFICATION_PANEL) && ev.getPointerCount() <= 1 && (this.mLauncher.getWorkspace() == null || this.mLauncher.getWorkspace().getState() == Workspace.State.NORMAL)))) {
            int actionMasked = ev.getActionMasked();
            int actionIndex = ev.getActionIndex();
            int pointerId = ev.getPointerId(actionIndex);
            if (actionMasked == 0) {
                boolean zCanInterceptTouch = canInterceptTouch(ev);
                this.mCanIntercept = zCanInterceptTouch;
                if (!zCanInterceptTouch) {
                    return false;
                }
                this.mDownEvents.put(pointerId, new PointF(ev.getX(), ev.getY()));
            } else if (ev.getActionMasked() == 5) {
                this.mDownEvents.put(pointerId, new PointF(ev.getX(actionIndex), ev.getY(actionIndex)));
            }
            if (!this.mCanIntercept || actionMasked != 2 || this.mDownEvents.get(pointerId) == null) {
                return false;
            }
            float y = ev.getY(actionIndex) - this.mDownEvents.get(pointerId).y;
            float x = ev.getX(actionIndex) - this.mDownEvents.get(pointerId).x;
            if (y > this.mTouchSlop && y > Math.abs(x) && ev.getPointerCount() == 1) {
                ev.setAction(0);
                dispatchTouchEvent(ev);
                setWindowSlippery(true);
                return true;
            }
            if (Math.abs(x) > this.mTouchSlop) {
                this.mCanIntercept = false;
            }
        }
        return false;
    }

    @Override // com.android.launcher3.util.TouchController
    public final boolean onControllerTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action != 1 && action != 3) {
            return true;
        }
        dispatchTouchEvent(ev);
        this.mLauncher.getUserEventDispatcher().logActionOnContainer(action == 1 ? 4 : 3, 2, 1, this.mLauncher.getWorkspace().getCurrentPage());
        setWindowSlippery(false);
        return true;
    }

    private void setWindowSlippery(boolean enable) {
        Window window = this.mLauncher.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (enable) {
            attributes.flags |= 536870912;
        } else {
            attributes.flags &= -536870913;
        }
        window.setAttributes(attributes);
    }

    private boolean canInterceptTouch(MotionEvent ev) {
        if (!this.mLauncher.isInState(LauncherState.NORMAL) || AbstractFloatingView.getTopOpenViewWithType(this.mLauncher, AbstractFloatingView.TYPE_STATUS_BAR_SWIPE_DOWN_DISALLOW) != null) {
            return false;
        }
        if (ev.getY() > this.mLauncher.getDragLayer().getHeight() - this.mLauncher.getDeviceProfile().getInsets().bottom) {
            return false;
        }
        return SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).isActive();
    }
}
