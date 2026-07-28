package com.android.launcher3.accessibility;

import android.text.TextUtils;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class WorkspaceAccessibilityHelper extends DragAndDropAccessibilityDelegate {
    public WorkspaceAccessibilityHelper(CellLayout layout) {
        super(layout);
    }

    @Override // com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate
    protected int intersectsValidDropTarget(int id) {
        int countX = this.mView.getCountX();
        int countY = this.mView.getCountY();
        int i = id % countX;
        int i2 = id / countX;
        LauncherAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        if (dragInfo.dragType == LauncherAccessibilityDelegate.DragType.WIDGET && this.mView.isHotseat()) {
            return -1;
        }
        if (dragInfo.dragType == LauncherAccessibilityDelegate.DragType.WIDGET) {
            int i3 = dragInfo.info.spanX;
            int i4 = dragInfo.info.spanY;
            for (int i5 = 0; i5 < i3; i5++) {
                for (int i6 = 0; i6 < i4; i6++) {
                    int i7 = i - i5;
                    int i8 = i2 - i6;
                    if (i7 >= 0 && i8 >= 0) {
                        boolean z = true;
                        for (int i9 = i7; i9 < i7 + i3 && z; i9++) {
                            for (int i10 = i8; i10 < i8 + i4; i10++) {
                                if (i9 >= countX || i10 >= countY || this.mView.isOccupied(i9, i10)) {
                                    z = false;
                                    break;
                                }
                            }
                        }
                        if (z) {
                            return i7 + (countX * i8);
                        }
                    }
                }
            }
            return -1;
        }
        View childAt = this.mView.getChildAt(i, i2);
        if (childAt == null || childAt == dragInfo.item) {
            return id;
        }
        if (dragInfo.dragType != LauncherAccessibilityDelegate.DragType.FOLDER) {
            ItemInfo itemInfo = (ItemInfo) childAt.getTag();
            if ((itemInfo instanceof AppInfo) || (itemInfo instanceof FolderInfo) || (itemInfo instanceof ShortcutInfo)) {
                return id;
            }
        }
        return -1;
    }

    @Override // com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate
    protected String getConfirmationForIconDrop(int id) {
        int countX = id % this.mView.getCountX();
        int countX2 = id / this.mView.getCountX();
        LauncherAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        View childAt = this.mView.getChildAt(countX, countX2);
        if (childAt == null || childAt == dragInfo.item) {
            return this.mContext.getString(R.string.item_moved);
        }
        ItemInfo itemInfo = (ItemInfo) childAt.getTag();
        if ((itemInfo instanceof AppInfo) || (itemInfo instanceof ShortcutInfo)) {
            return this.mContext.getString(R.string.folder_created);
        }
        return itemInfo instanceof FolderInfo ? this.mContext.getString(R.string.added_to_folder) : "";
    }

    @Override // com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate
    protected String getLocationDescriptionForIconDrop(int id) {
        int countX = id % this.mView.getCountX();
        int countX2 = id / this.mView.getCountX();
        LauncherAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        View childAt = this.mView.getChildAt(countX, countX2);
        if (childAt == null || childAt == dragInfo.item) {
            return this.mView.isHotseat() ? this.mContext.getString(R.string.move_to_hotseat_position, Integer.valueOf(id + 1)) : this.mContext.getString(R.string.move_to_empty_cell, Integer.valueOf(countX2 + 1), Integer.valueOf(countX + 1));
        }
        ItemInfo itemInfo = (ItemInfo) childAt.getTag();
        if (itemInfo instanceof ShortcutInfo) {
            return this.mContext.getString(R.string.create_folder_with, itemInfo.title);
        }
        if (!(itemInfo instanceof FolderInfo)) {
            return "";
        }
        if (TextUtils.isEmpty(itemInfo.title)) {
            ShortcutInfo shortcutInfo = null;
            for (ShortcutInfo shortcutInfo2 : ((FolderInfo) itemInfo).contents) {
                if (shortcutInfo == null || shortcutInfo.rank > shortcutInfo2.rank) {
                    shortcutInfo = shortcutInfo2;
                }
            }
            if (shortcutInfo != null) {
                return this.mContext.getString(R.string.add_to_folder_with_app, shortcutInfo.title);
            }
        }
        return this.mContext.getString(R.string.add_to_folder, itemInfo.title);
    }
}
