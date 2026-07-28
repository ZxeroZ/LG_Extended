package androidx.core.view;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.R;

/* JADX INFO: loaded from: classes.dex */
public final class ViewGroupCompat {
    public static final int LAYOUT_MODE_CLIP_BOUNDS = 0;
    public static final int LAYOUT_MODE_OPTICAL_BOUNDS = 1;

    private ViewGroupCompat() {
    }

    @Deprecated
    public static boolean onRequestSendAccessibilityEvent(ViewGroup group, View child, AccessibilityEvent event) {
        return group.onRequestSendAccessibilityEvent(child, event);
    }

    @Deprecated
    public static void setMotionEventSplittingEnabled(ViewGroup group, boolean split) {
        group.setMotionEventSplittingEnabled(split);
    }

    public static int getLayoutMode(ViewGroup group) {
        if (Build.VERSION.SDK_INT >= 18) {
            return group.getLayoutMode();
        }
        return 0;
    }

    public static void setLayoutMode(ViewGroup group, int mode) {
        if (Build.VERSION.SDK_INT >= 18) {
            group.setLayoutMode(mode);
        }
    }

    public static void setTransitionGroup(ViewGroup group, boolean isTransitionGroup) {
        if (Build.VERSION.SDK_INT >= 21) {
            group.setTransitionGroup(isTransitionGroup);
        } else {
            group.setTag(R.id.tag_transition_group, Boolean.valueOf(isTransitionGroup));
        }
    }

    public static boolean isTransitionGroup(ViewGroup group) {
        if (Build.VERSION.SDK_INT >= 21) {
            return group.isTransitionGroup();
        }
        Boolean bool = (Boolean) group.getTag(R.id.tag_transition_group);
        return ((bool == null || !bool.booleanValue()) && group.getBackground() == null && ViewCompat.getTransitionName(group) == null) ? false : true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.ViewGroup */
    /* JADX WARN: Multi-variable type inference failed */
    public static int getNestedScrollAxes(ViewGroup group) {
        if (Build.VERSION.SDK_INT >= 21) {
            return group.getNestedScrollAxes();
        }
        if (group instanceof NestedScrollingParent) {
            return ((NestedScrollingParent) group).getNestedScrollAxes();
        }
        return 0;
    }
}
