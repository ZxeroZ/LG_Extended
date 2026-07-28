package com.android.launcher3.uioverrides.touchcontrollers;

import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.launcher3.Launcher;
import com.android.launcher3.anim.PendingAnimation;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;

/* JADX INFO: loaded from: classes.dex */
public final class PortraitOverviewStateTouchHelper {
    Launcher mLauncher;
    RecentsView mRecentsView;

    public PortraitOverviewStateTouchHelper(Launcher launcher) {
        this.mLauncher = launcher;
        this.mRecentsView = (RecentsView) launcher.getOverviewPanel();
    }

    boolean canInterceptTouch(MotionEvent ev) {
        if (this.mRecentsView.getTaskViewCount() > 0) {
            return ev.getY() >= ((float) this.mRecentsView.getTaskViewAt(0).getBottom());
        }
        return PortraitStatesTouchController.isTouchOverHotseat(this.mLauncher, ev);
    }

    boolean shouldSwipeDownReturnToApp() {
        return this.mRecentsView.getNextPageTaskView() != null && this.mRecentsView.shouldSwipeDownLaunchApp();
    }

    PendingAnimation createSwipeDownToTaskAppAnimation(long duration, Interpolator interpolator) {
        RecentsView recentsView = this.mRecentsView;
        recentsView.setCurrentPage(recentsView.getPageNearestToCenterOfScreen());
        TaskView currentPageTaskView = this.mRecentsView.getCurrentPageTaskView();
        if (currentPageTaskView == null) {
            throw new IllegalStateException("There is no task view to animate to.");
        }
        return this.mRecentsView.createTaskLaunchAnimation(currentPageTaskView, duration, interpolator);
    }
}
