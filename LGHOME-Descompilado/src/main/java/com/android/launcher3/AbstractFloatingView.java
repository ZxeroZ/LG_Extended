package com.android.launcher3;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractFloatingView extends LinearLayout implements TouchController {
    public static final int TYPE_ACCESSIBLE = 3775;
    public static final int TYPE_ACTION_POPUP = 2;
    public static final int TYPE_ALL = 4095;
    public static final int TYPE_ALL_APPS_EDU = 512;
    public static final int TYPE_DISCOVERY_BOUNCE = 64;
    public static final int TYPE_FOLDER = 1;
    public static final int TYPE_HIDE_BACK_BUTTON = 488;
    public static final int TYPE_LISTENER = 256;
    public static final int TYPE_ON_BOARD_POPUP = 32;
    public static final int TYPE_OPTIONS_POPUP = 1024;
    public static final int TYPE_REBIND_SAFE = 112;
    public static final int TYPE_RECENT_GUIDE = 2048;
    public static final int TYPE_SNACKBAR = 128;
    public static final int TYPE_STATUS_BAR_SWIPE_DOWN_DISALLOW = 636;
    public static final int TYPE_TASK_MENU = 512;
    public static final int TYPE_WIDGETS_BOTTOM_SHEET = 4;
    public static final int TYPE_WIDGETS_FULL_SHEET = 16;
    public static final int TYPE_WIDGET_RESIZE_FRAME = 8;
    protected boolean mIsOpen;

    @Retention(RetentionPolicy.SOURCE)
    public @interface FloatingViewType {
    }

    public void addHintCloseAnim(float distanceToMove, Interpolator interpolator, PendingAnimation target) {
    }

    public boolean canInterceptEventsInSystemGestureRegion() {
        return false;
    }

    public Animator createHintCloseAnim(float distanceToMove) {
        return null;
    }

    protected Pair<View, String> getAccessibilityTarget() {
        return null;
    }

    public int getLogContainerType() {
        return 0;
    }

    protected abstract void handleClose(boolean animate);

    protected abstract boolean isOfType(int type);

    public abstract void logActionCommand(int command);

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    protected void onWidgetsBound() {
    }

    public AbstractFloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractFloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public final void close(boolean animate) {
        boolean zAreAnimationsEnabled = animate & Utilities.areAnimationsEnabled(getContext());
        if (this.mIsOpen) {
            BaseActivity.fromContext(getContext()).getUserEventDispatcher().resetElapsedContainerMillis("container closed");
        }
        handleClose(zAreAnimationsEnabled);
        this.mIsOpen = false;
    }

    public final boolean isOpen() {
        return this.mIsOpen;
    }

    public boolean onBackPressed() {
        logActionCommand(1);
        close(true);
        return true;
    }

    protected void announceAccessibilityChanges() {
        Pair<View, String> accessibilityTarget = getAccessibilityTarget();
        if (accessibilityTarget == null || !AccessibilityManagerCompat.isAccessibilityEnabled(getContext())) {
            return;
        }
        AccessibilityManagerCompat.sendCustomAccessibilityEvent((View) accessibilityTarget.first, 32, (String) accessibilityTarget.second);
        if (this.mIsOpen) {
            sendAccessibilityEvent(8);
        }
        ActivityContext.lookupContext(getContext()).getDragLayer().sendAccessibilityEvent(2048);
    }

    public static <T extends AbstractFloatingView> T getOpenView(ActivityContext activity, int type) {
        BaseDragLayer dragLayer = activity.getDragLayer();
        if (dragLayer == null) {
            return null;
        }
        for (int childCount = dragLayer.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = dragLayer.getChildAt(childCount);
            if (childAt instanceof AbstractFloatingView) {
                T t = (T) childAt;
                if (t.isOfType(type) && t.isOpen()) {
                    return t;
                }
            }
        }
        return null;
    }

    public static void closeOpenContainer(ActivityContext activity, int type) {
        AbstractFloatingView openView = getOpenView(activity, type);
        if (openView != null) {
            openView.close(true);
        }
    }

    public static void closeOpenViews(ActivityContext activity, boolean animate, int type) {
        BaseDragLayer dragLayer = activity.getDragLayer();
        for (int childCount = dragLayer.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = dragLayer.getChildAt(childCount);
            if (childAt instanceof AbstractFloatingView) {
                AbstractFloatingView abstractFloatingView = (AbstractFloatingView) childAt;
                if (abstractFloatingView.isOfType(type)) {
                    abstractFloatingView.close(animate);
                }
            }
        }
    }

    public static void closeAllOpenViews(ActivityContext activity, boolean animate) {
        closeOpenViews(activity, animate, TYPE_ALL);
        activity.finishAutoCancelActionMode();
    }

    public static void closeAllOpenViews(ActivityContext activity) {
        closeAllOpenViews(activity, true);
    }

    public static void closeAllOpenViewsExcept(ActivityContext activity, boolean animate, int type) {
        closeOpenViews(activity, animate, (~type) & TYPE_ALL);
        activity.finishAutoCancelActionMode();
    }

    public static void closeAllOpenViewsExcept(ActivityContext activity, int type) {
        closeAllOpenViewsExcept(activity, true, type);
    }

    public static AbstractFloatingView getTopOpenView(ActivityContext activity) {
        return getTopOpenViewWithType(activity, TYPE_ALL);
    }

    public static AbstractFloatingView getTopOpenViewWithType(ActivityContext activity, int type) {
        return getOpenView(activity, type);
    }
}
