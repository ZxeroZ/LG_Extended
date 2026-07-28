package com.android.launcher3.accessibility;

import com.android.launcher3.CellLayout;
import com.android.launcher3.folder.FolderPagedView;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class FolderAccessibilityHelper extends DragAndDropAccessibilityDelegate {
    private final FolderPagedView mParent;
    private final int mStartPosition;

    public FolderAccessibilityHelper(CellLayout layout) {
        super(layout);
        FolderPagedView folderPagedView = (FolderPagedView) layout.getParent();
        this.mParent = folderPagedView;
        this.mStartPosition = folderPagedView.indexOfChild(layout) * layout.getCountX() * layout.getCountY();
    }

    @Override // com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate
    protected int intersectsValidDropTarget(int id) {
        return Math.min(id, (this.mParent.getAllocatedContentSize() - this.mStartPosition) - 1);
    }

    @Override // com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate
    protected String getLocationDescriptionForIconDrop(int id) {
        return this.mContext.getString(R.string.move_to_position, Integer.valueOf(id + this.mStartPosition + 1));
    }

    @Override // com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate
    protected String getConfirmationForIconDrop(int id) {
        return this.mContext.getString(R.string.item_moved);
    }
}
