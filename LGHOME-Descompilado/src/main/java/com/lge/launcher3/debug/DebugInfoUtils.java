package com.lge.launcher3.debug;

import android.view.View;
import android.view.ViewParent;
import com.android.launcher3.CellLayout;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Workspace;

/* JADX INFO: loaded from: classes.dex */
public class DebugInfoUtils {
    public static CellLayout getParent(ShortcutAndWidgetContainer container) {
        ViewParent parent;
        if (container == null || (parent = container.getParent()) == null || !(parent instanceof CellLayout)) {
            return null;
        }
        return (CellLayout) parent;
    }

    public static Workspace getParent(CellLayout cellLayout) {
        ViewParent parent;
        if (cellLayout == null || (parent = cellLayout.getParent()) == null || !(parent instanceof Workspace)) {
            return null;
        }
        return (Workspace) parent;
    }

    public static Workspace getGrandParent(ShortcutAndWidgetContainer container) {
        return getParent(getParent(container));
    }

    public static int indexOfChild(CellLayout cellLayout) {
        Workspace parent = getParent(cellLayout);
        if (parent == null) {
            return -1;
        }
        return parent.indexOfChild(cellLayout);
    }

    public static int indexOfChild(ShortcutAndWidgetContainer container) {
        CellLayout parent = getParent(container);
        if (parent == null) {
            return -1;
        }
        return indexOfChild(parent);
    }

    public static String getViewVisibleInfo(View view) {
        if (view == null) {
            return null;
        }
        return String.format("%s {getLayerType(%d), getVisibility(%d), alpha(%.2f)}", view.getClass().getSimpleName(), Integer.valueOf(view.getLayerType()), Integer.valueOf(view.getVisibility()), Float.valueOf(view.getAlpha()));
    }

    public static int getScrollX(ShortcutAndWidgetContainer container) {
        Workspace grandParent = getGrandParent(container);
        if (grandParent == null) {
            return -1;
        }
        return grandParent.getScrollX();
    }

    public static int getScrollX(CellLayout cellLayout) {
        Workspace parent = getParent(cellLayout);
        if (parent == null) {
            return -1;
        }
        return parent.getScrollX();
    }

    public static int getCurrentPage(ShortcutAndWidgetContainer container) {
        Workspace grandParent = getGrandParent(container);
        if (grandParent == null) {
            return -1;
        }
        return grandParent.getCurrentPage();
    }

    public static int getCurrentPage(CellLayout cellLayout) {
        Workspace parent = getParent(cellLayout);
        if (parent == null) {
            return -1;
        }
        return parent.getCurrentPage();
    }
}
