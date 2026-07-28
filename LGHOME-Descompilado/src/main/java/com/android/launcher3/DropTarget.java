package com.android.launcher3;

import android.graphics.PointF;
import android.graphics.Rect;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.model.data.ItemInfo;

/* JADX INFO: loaded from: classes.dex */
public interface DropTarget {
    public static final String TAG = "DropTarget";

    boolean acceptDrop(DragObject dragObject);

    void getHitRectRelativeToDragLayer(Rect outRect);

    int getLeft();

    void getLocationInDragLayer(int[] loc);

    int getTop();

    boolean isDropEnabled();

    void onDragEnter(DragObject dragObject);

    void onDragExit(DragObject dragObject);

    void onDragOver(DragObject dragObject);

    void onDrop(DragObject dragObject);

    void onFlingToDelete(DragObject dragObject, PointF vec);

    void prepareAccessibilityDrop();

    public static class DragObject {
        public boolean accessibleDrag;
        public int x = -1;
        public int y = -1;
        public int xOffset = -1;
        public int yOffset = -1;
        public boolean dragComplete = false;
        public DragView dragView = null;
        public Object dragInfo = null;
        public ItemInfo originalDragInfo = null;
        public DragSource dragSource = null;
        public Runnable postAnimationRunnable = null;
        public boolean cancelled = false;
        public boolean deferDragViewCleanupPostAnimation = true;

        public final float[] getVisualCenter(float[] recycle) {
            if (recycle == null) {
                recycle = new float[2];
            }
            int i = this.x - this.xOffset;
            int i2 = this.y - this.yOffset;
            recycle[0] = i + (this.dragView.getDragRegion().width() / 2);
            recycle[1] = i2 + (this.dragView.getDragRegion().height() / 2);
            return recycle;
        }
    }
}
