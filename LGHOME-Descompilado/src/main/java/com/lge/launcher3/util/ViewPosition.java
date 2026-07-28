package com.lge.launcher3.util;

import android.graphics.Rect;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class ViewPosition {
    public static final boolean DEBUG_ViewPosition = false;
    private static float[] sPt = {0.0f, 0.0f};
    private static final int[] mTmpXY = new int[2];

    public static void getLocationInDragLayer(View root, View child, int[] loc) {
        loc[0] = 0;
        loc[1] = 0;
        getDescendantCoordRelativeToSelf(root, child, loc);
    }

    public static final float getDescendantCoordRelativeToSelf(View root, View descendant, int[] coord) {
        float[] fArr = sPt;
        fArr[0] = coord[0];
        fArr[1] = coord[1];
        descendant.getMatrix().mapPoints(sPt);
        float scaleX = descendant.getScaleX() * 1.0f;
        float[] fArr2 = sPt;
        fArr2[0] = fArr2[0] + descendant.getLeft();
        float[] fArr3 = sPt;
        fArr3[1] = fArr3[1] + descendant.getTop();
        Object parent = descendant.getParent();
        while ((parent instanceof View) && parent != root) {
            View view = (View) parent;
            view.getMatrix().mapPoints(sPt);
            scaleX *= view.getScaleX();
            float[] fArr4 = sPt;
            fArr4[0] = fArr4[0] + (view.getLeft() - view.getScrollX());
            float[] fArr5 = sPt;
            fArr5[1] = fArr5[1] + (view.getTop() - view.getScrollY());
            parent = view.getParent();
        }
        float[] fArr6 = sPt;
        coord[0] = (int) (((double) fArr6[0]) + 0.5d);
        coord[1] = (int) (((double) fArr6[1]) + 0.5d);
        return scaleX;
    }

    public static float getDescendantRectRelativeToSelf(View root, View descendant, Rect r) {
        int[] iArr = mTmpXY;
        iArr[0] = 0;
        iArr[1] = 0;
        float descendantCoordRelativeToSelf = getDescendantCoordRelativeToSelf(root, descendant, iArr);
        r.set(iArr[0], iArr[1], iArr[0] + descendant.getWidth(), iArr[1] + descendant.getHeight());
        return descendantCoordRelativeToSelf;
    }

    public static void getViewRectRelativeToSelf(View root, View v, Rect r) {
        int[] iArr = new int[2];
        root.getLocationInWindow(iArr);
        int i = iArr[0];
        int i2 = iArr[1];
        v.getLocationInWindow(iArr);
        int i3 = iArr[0] - i;
        int i4 = iArr[1] - i2;
        r.set(i3, i4, v.getMeasuredWidth() + i3, v.getMeasuredHeight() + i4);
    }
}
