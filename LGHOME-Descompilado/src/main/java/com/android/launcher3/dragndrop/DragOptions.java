package com.android.launcher3.dragndrop;

import android.graphics.Point;
import com.android.launcher3.DropTarget;

/* JADX INFO: loaded from: classes.dex */
public class DragOptions {
    public boolean isAccessibleDrag = false;
    public Point systemDndStartPoint = null;
    public boolean hasDeepShortcuts = false;
    public boolean isDragFromAllAps = false;
    public boolean isDragFromOverView = false;
    public PreDragCondition preDragCondition = null;

    public interface PreDragCondition {
        void onPreDragEnd(DropTarget.DragObject dragObject, boolean dragStarted);

        void onPreDragStart(DropTarget.DragObject dragObject);

        boolean shouldStartDrag(double distanceDragged);
    }
}
