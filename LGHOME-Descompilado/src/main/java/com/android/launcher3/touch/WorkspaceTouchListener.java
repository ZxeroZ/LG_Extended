package com.android.launcher3.touch;

import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragLayer;

/* JADX INFO: loaded from: classes.dex */
public class WorkspaceTouchListener implements View.OnTouchListener, Runnable {
    private static final int STATE_CANCELLED = 0;
    private static final int STATE_COMPLETED = 3;
    private static final int STATE_PENDING_PARENT_INFORM = 2;
    private static final int STATE_REQUESTED = 1;
    private final Launcher mLauncher;
    private final float mTouchSlop;
    private final Workspace mWorkspace;
    private final Rect mTempRect = new Rect();
    private final PointF mTouchDownPoint = new PointF();
    private int mLongPressState = 0;

    public WorkspaceTouchListener(Launcher launcher, Workspace workspace) {
        this.mLauncher = launcher;
        this.mWorkspace = workspace;
        this.mTouchSlop = ViewConfiguration.get(launcher).getScaledTouchSlop() * 2;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent ev) {
        boolean z;
        int actionMasked = ev.getActionMasked();
        if (actionMasked == 0) {
            boolean zCanHandleLongPress = canHandleLongPress();
            if (zCanHandleLongPress) {
                DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
                DragLayer dragLayer = this.mLauncher.getDragLayer();
                Rect insets = deviceProfile.getInsets();
                this.mTempRect.set(insets.left, insets.top, dragLayer.getWidth() - insets.right, dragLayer.getHeight() - insets.bottom);
                this.mTempRect.inset(deviceProfile.edgeMarginPx, deviceProfile.edgeMarginPx);
                zCanHandleLongPress = this.mTempRect.contains((int) ev.getX(), (int) ev.getY());
            }
            cancelLongPress();
            if (zCanHandleLongPress) {
                this.mLongPressState = 1;
                this.mTouchDownPoint.set(ev.getX(), ev.getY());
                this.mWorkspace.postDelayed(this, ViewConfiguration.getLongPressTimeout());
            }
            this.mWorkspace.onTouchEvent(ev);
            return true;
        }
        if (this.mLongPressState == 2) {
            ev.setAction(3);
            this.mWorkspace.onTouchEvent(ev);
            ev.setAction(actionMasked);
            this.mLongPressState = 3;
        }
        int i = this.mLongPressState;
        if (i == 3) {
            z = true;
        } else if (i == 1) {
            this.mWorkspace.onTouchEvent(ev);
            if (this.mWorkspace.isHandlingTouch()) {
                cancelLongPress();
            } else if (actionMasked == 2 && PointF.length(this.mTouchDownPoint.x - ev.getX(), this.mTouchDownPoint.y - ev.getY()) > this.mTouchSlop) {
                cancelLongPress();
            }
            z = true;
        } else {
            z = false;
        }
        if ((actionMasked == 1 || actionMasked == 6) && !this.mWorkspace.isHandlingTouch()) {
            Workspace workspace = this.mWorkspace;
            if (((CellLayout) workspace.getChildAt(workspace.getCurrentPage())) != null) {
                this.mWorkspace.onWallpaperTap(ev);
            }
        }
        if (actionMasked == 1 || actionMasked == 3) {
            cancelLongPress();
        }
        return z;
    }

    private boolean canHandleLongPress() {
        return AbstractFloatingView.getTopOpenView(this.mLauncher) == null && this.mLauncher.isInState(LauncherState.NORMAL);
    }

    private void cancelLongPress() {
        this.mWorkspace.removeCallbacks(this);
        this.mLongPressState = 0;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.mLongPressState == 1) {
            if (canHandleLongPress()) {
                this.mLongPressState = 2;
                this.mWorkspace.getParent().requestDisallowInterceptTouchEvent(true);
                this.mWorkspace.performHapticFeedback(0, 1);
                this.mLauncher.getUserEventDispatcher().logActionOnContainer(1, 0, 1, this.mWorkspace.getCurrentPage());
                return;
            }
            cancelLongPress();
        }
    }
}
