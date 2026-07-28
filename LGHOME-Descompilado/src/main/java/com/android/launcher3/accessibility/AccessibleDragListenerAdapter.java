package com.android.launcher3.accessibility;

import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DragSource;
import com.android.launcher3.Launcher;
import com.android.launcher3.dragndrop.DragController;

/* JADX INFO: loaded from: classes.dex */
public class AccessibleDragListenerAdapter implements DragController.DragListener {
    private final int mDragType;
    private final ViewGroup mViewGroup;

    public AccessibleDragListenerAdapter(ViewGroup parent, int dragType) {
        this.mViewGroup = parent;
        this.mDragType = dragType;
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragStart(DragSource source, Object info, int dragAction) {
        enableAccessibleDrag(true);
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragEnd() {
        enableAccessibleDrag(false);
        Launcher.getLauncher(this.mViewGroup.getContext()).getDragController().removeDragListener(this);
    }

    protected void enableAccessibleDrag(boolean enable) {
        for (int i = 0; i < this.mViewGroup.getChildCount(); i++) {
            setEnableForLayout((CellLayout) this.mViewGroup.getChildAt(i), enable);
        }
    }

    protected final void setEnableForLayout(CellLayout layout, boolean enable) {
        layout.enableAccessibleDrag(enable, this.mDragType);
    }
}
