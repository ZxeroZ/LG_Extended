package com.lge.launcher3.allapps;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TabHost;
import com.android.launcher3.CellLayout;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.lge.launcher3.PagedView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class FocusHelperUtil {

    public interface FocusableView {
    }

    static View getClosestIconInApps(AllAppsPagedCellLayout layout, View v, int keycode) {
        return null;
    }

    public static TabHost findTabHostParent(View v) {
        ViewParent parent = v.getParent();
        while (parent != null && !(parent instanceof TabHost)) {
            parent = parent.getParent();
        }
        return (TabHost) parent;
    }

    static ViewGroup getAppsCustomizePage(ViewGroup container, int index) {
        ViewGroup viewGroup = (ViewGroup) ((PagedView) container).getPageAt(index);
        return viewGroup instanceof AllAppsPagedCellLayout ? (ViewGroup) viewGroup.getChildAt(0) : viewGroup;
    }

    public static ShortcutAndWidgetContainer getCellLayoutChildrenForIndex(ViewGroup container, int i) {
        ViewGroup viewGroup = (ViewGroup) container.getChildAt(i);
        if (viewGroup == null) {
            return null;
        }
        return (ShortcutAndWidgetContainer) viewGroup.getChildAt(0);
    }

    static ArrayList<View> getCellLayoutChildrenSortedSpatially(CellLayout layout, ViewGroup parent) {
        ArrayList<View> arrayList = new ArrayList<>();
        if (parent == null) {
            return arrayList;
        }
        final int countX = layout.getCountX();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            arrayList.add(parent.getChildAt(i));
        }
        Collections.sort(arrayList, new Comparator<View>() { // from class: com.lge.launcher3.allapps.FocusHelperUtil.1
            /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
            @Override // java.util.Comparator
            public int compare(View lhs, View rhs) {
                CellLayoutParams cellLayoutParams = (CellLayoutParams) lhs.getLayoutParams();
                CellLayoutParams cellLayoutParams2 = (CellLayoutParams) rhs.getLayoutParams();
                return ((cellLayoutParams.cellY * countX) + cellLayoutParams.cellX) - ((cellLayoutParams2.cellY * countX) + cellLayoutParams2.cellX);
            }
        });
        return arrayList;
    }

    static View findIndexOfIcon(ArrayList<View> views, int i, int delta) {
        View view;
        int size = views.size();
        do {
            i += delta;
            if (i < 0 || i >= size) {
                return null;
            }
            view = views.get(i);
        } while (!isFocusableIcon(view));
        return view;
    }

    public static View getIconInDirection(CellLayout layout, ViewGroup parent, int i, int delta) {
        return findIndexOfIcon(getCellLayoutChildrenSortedSpatially(layout, parent), i, delta);
    }

    static View getIconInDirection(CellLayout layout, ViewGroup parent, View v, int delta) {
        ArrayList<View> cellLayoutChildrenSortedSpatially = getCellLayoutChildrenSortedSpatially(layout, parent);
        return findIndexOfIcon(cellLayoutChildrenSortedSpatially, cellLayoutChildrenSortedSpatially.indexOf(v), delta);
    }

    static View getClosestIconOnLine(CellLayout layout, ViewGroup parent, View v, int lineDelta) {
        ArrayList<View> cellLayoutChildrenSortedSpatially = getCellLayoutChildrenSortedSpatially(layout, parent);
        CellLayoutParams cellLayoutParams = (CellLayoutParams) v.getLayoutParams();
        int countY = layout.getCountY();
        int i = cellLayoutParams.cellY;
        int i2 = i + lineDelta;
        if (i2 < 0 || i2 >= countY) {
            return null;
        }
        float f = Float.MAX_VALUE;
        int iIndexOf = cellLayoutChildrenSortedSpatially.indexOf(v);
        int size = lineDelta < 0 ? -1 : cellLayoutChildrenSortedSpatially.size();
        int i3 = -1;
        while (iIndexOf != size) {
            View view = cellLayoutChildrenSortedSpatially.get(iIndexOf);
            CellLayoutParams cellLayoutParams2 = (CellLayoutParams) view.getLayoutParams();
            boolean z = false;
            if (lineDelta >= 0 ? cellLayoutParams2.cellY > i : cellLayoutParams2.cellY < i) {
                z = true;
            }
            if (z && isFocusableIcon(view)) {
                float fSqrt = (float) Math.sqrt(((cellLayoutParams2.cellX - cellLayoutParams.cellX) * (cellLayoutParams2.cellX - cellLayoutParams.cellX)) + ((cellLayoutParams2.cellY - cellLayoutParams.cellY) * (cellLayoutParams2.cellY - cellLayoutParams.cellY)));
                if (fSqrt < f) {
                    i3 = iIndexOf;
                    f = fSqrt;
                }
            }
            iIndexOf = iIndexOf <= size ? iIndexOf + 1 : iIndexOf - 1;
        }
        if (i3 > -1) {
            return cellLayoutChildrenSortedSpatially.get(i3);
        }
        return null;
    }

    static View getLastPagedViewCellLayoutChildren(ViewGroup newParent, int countX, int countY) {
        View childAtForPagedViewCellLayoutChildren = null;
        boolean z = false;
        for (int i = countY - 1; i >= 0; i--) {
            int i2 = countX - 1;
            while (true) {
                if (i2 < 0) {
                    break;
                }
                childAtForPagedViewCellLayoutChildren = ((AllAppsPagedCellLayoutChildren) newParent).getChildAtForPagedViewCellLayoutChildren(i2, i);
                if (childAtForPagedViewCellLayoutChildren != null) {
                    z = true;
                    break;
                }
                i2--;
            }
            if (z) {
                break;
            }
        }
        return childAtForPagedViewCellLayoutChildren;
    }

    static boolean isFocusableIcon(View view) {
        return view instanceof FocusableView;
    }

    public static View getFirstFocusableIcon(CellLayout layout) {
        ShortcutAndWidgetContainer shortcutAndWidgetContainer;
        if (layout == null || (shortcutAndWidgetContainer = (ShortcutAndWidgetContainer) layout.getChildAt(0, 0)) == null) {
            return null;
        }
        return getIconInDirection(layout, shortcutAndWidgetContainer, -1, 1);
    }
}
