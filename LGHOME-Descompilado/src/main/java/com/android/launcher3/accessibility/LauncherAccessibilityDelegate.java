package com.android.launcher3.accessibility;

import android.app.AlertDialog;
import android.appwidget.AppWidgetProviderInfo;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.launcher3.AppWidgetResizeFrame;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.InfoDropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.lge.launcher3.R;
import com.lge.launcher3.droptarget.LGUninstallDropTarget;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.widgettray.LGWidgetCell;
import com.lge.launcher3.widgettray.WidgetsUninstallButton;
import com.lge.launcher3.widgettray.WidgetsViewPagerAdapter;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class LauncherAccessibilityDelegate extends View.AccessibilityDelegate implements DragController.DragListener {
    protected static final int ADD_TO_WORKSPACE = 2131296302;
    protected static final int DEEP_SHORTCUTS = 2131296315;
    protected static final int INFO = 2131296319;
    protected static final int MOVE = 2131296325;
    protected static final int MOVE_TO_WORKSPACE = 2131296328;
    protected static final int REMOVE = 2131296331;
    protected static final int RESIZE = 2131296332;
    private static final String TAG = "LauncherAccessibilityDelegate";
    protected static final int UNINSTALL = 2131296336;
    protected static final int UNINSTALL_WIDGET = 2131296337;
    protected final SparseArray<AccessibilityNodeInfo.AccessibilityAction> mActions;
    private DragInfo mDragInfo;
    private AccessibilityDragSource mDragSource;
    final Launcher mLauncher;
    private WidgetsUninstallButton mUninstallBtn;

    public interface AccessibilityDragSource {
        void enableAccessibleDrag(boolean enable);

        void startDrag(CellLayout.CellInfo cellInfo, boolean accessible);
    }

    public static class DragInfo {
        public DragType dragType;
        public ItemInfo info;
        public View item;
    }

    public enum DragType {
        ICON,
        FOLDER,
        WIDGET
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragStart(DragSource source, Object info, int dragAction) {
    }

    public LauncherAccessibilityDelegate(Launcher launcher) {
        SparseArray<AccessibilityNodeInfo.AccessibilityAction> sparseArray = new SparseArray<>();
        this.mActions = sparseArray;
        this.mDragInfo = null;
        this.mDragSource = null;
        this.mUninstallBtn = null;
        this.mLauncher = launcher;
        sparseArray.put(R.id.action_remove, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_remove, launcher.getText(R.string.delete_target_label)));
        sparseArray.put(R.id.action_info, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_info, launcher.getText(R.string.info_target_label)));
        sparseArray.put(R.id.action_uninstall, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_uninstall, launcher.getText(R.string.delete_target_uninstall_label)));
        sparseArray.put(R.id.action_add_to_workspace, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_add_to_workspace, launcher.getText(R.string.action_add_to_workspace)));
        sparseArray.put(R.id.action_move, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move, launcher.getText(R.string.action_move)));
        sparseArray.put(R.id.action_move_to_workspace, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_to_workspace, launcher.getText(R.string.action_move_to_workspace)));
        sparseArray.put(R.id.action_resize, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_resize, launcher.getText(R.string.action_resize)));
        sparseArray.put(R.id.action_deep_shortcuts, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_deep_shortcuts, launcher.getText(R.string.action_deep_shortcut)));
        sparseArray.put(R.id.action_uninstall_widget, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_uninstall_widget, launcher.getText(R.string.talkback_uninstall_message)));
    }

    @Override // android.view.View.AccessibilityDelegate
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        addActions(host, info);
    }

    protected void addActions(View host, AccessibilityNodeInfo info) {
        if (host.getTag() instanceof ItemInfo) {
            ItemInfo itemInfo = (ItemInfo) host.getTag();
            boolean z = host instanceof BubbleTextView;
            if (z && ((BubbleTextView) host).hasDeepShortcuts()) {
                info.addAction(this.mActions.get(R.id.action_deep_shortcuts));
            }
            if (DeleteDropTarget.supportsDrop(itemInfo)) {
                info.addAction(this.mActions.get(R.id.action_remove));
            }
            if (LGUninstallDropTarget.supportsDrop(host.getContext(), itemInfo)) {
                info.addAction(this.mActions.get(R.id.action_uninstall));
            }
            if (InfoDropTarget.supportsDrop(host.getContext(), itemInfo)) {
                info.addAction(this.mActions.get(R.id.action_info));
            }
            if ((itemInfo instanceof ShortcutInfo) || (itemInfo instanceof LauncherAppWidgetInfo) || (itemInfo instanceof FolderInfo)) {
                info.addAction(this.mActions.get(R.id.action_move));
                if (itemInfo.container >= 0) {
                    info.addAction(this.mActions.get(R.id.action_move_to_workspace));
                } else if ((itemInfo instanceof LauncherAppWidgetInfo) && !getSupportedResizeActions(host, (LauncherAppWidgetInfo) itemInfo).isEmpty()) {
                    info.addAction(this.mActions.get(R.id.action_resize));
                }
            }
            if (((itemInfo instanceof AppInfo) || (itemInfo instanceof PendingAddItemInfo)) && (!(host instanceof LGWidgetCell) || !WidgetsViewPagerAdapter.checkGroupWidget(((LGWidgetCell) host).mInfo))) {
                info.addAction(this.mActions.get(R.id.action_add_to_workspace));
            }
            boolean z2 = host instanceof LGWidgetCell;
            if (z2 && !WidgetsViewPagerAdapter.checkGroupWidget(((LGWidgetCell) host).mInfo)) {
                info.setClickable(false);
                info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            }
            if (z && this.mLauncher.getWorkspace().getState() == Workspace.State.OVERVIEW) {
                info.setClickable(false);
                info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            }
            if (z2 && this.mLauncher.getWorkspace().getState() == Workspace.State.OVERVIEW_HIDDEN) {
                WidgetsUninstallButton widgetsUninstallButton = (WidgetsUninstallButton) this.mLauncher.findViewById(R.id.widget_tray_uninstall);
                this.mUninstallBtn = widgetsUninstallButton;
                if (widgetsUninstallButton == null || !widgetsUninstallButton.isUninstallMode()) {
                    return;
                }
                info.setClickable(false);
                info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                if (Utilities.isDownloadedWidget(itemInfo, this.mLauncher)) {
                    info.addAction(this.mActions.get(R.id.action_uninstall_widget));
                } else {
                    info.removeAction(this.mActions.get(R.id.action_info));
                }
                if (WidgetsViewPagerAdapter.checkGroupWidget(((LGWidgetCell) host).mInfo)) {
                    return;
                }
                info.setLongClickable(false);
                info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
                info.removeAction(this.mActions.get(R.id.action_add_to_workspace));
            }
        }
    }

    @Override // android.view.View.AccessibilityDelegate
    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        if ((host.getTag() instanceof ItemInfo) && performAction(host, (ItemInfo) host.getTag(), action)) {
            return true;
        }
        return super.performAccessibilityAction(host, action, args);
    }

    public boolean performAction(final View host, final ItemInfo item, int action) {
        if (action == R.id.action_remove) {
            if (!DeleteDropTarget.removeWorkspaceOrFolderItem(this.mLauncher, item, host)) {
                return false;
            }
            announceConfirmation(R.string.item_removed);
            return true;
        }
        if (action == R.id.action_info) {
            InfoDropTarget.startDetailsActivityForInfo(item, this.mLauncher);
            return true;
        }
        if (action == R.id.action_uninstall) {
            return LGUninstallDropTarget.startUninstallActivity(this.mLauncher, item);
        }
        if (action == R.id.action_move) {
            beginAccessibleDrag(host, item);
            return false;
        }
        if (action == R.id.action_add_to_workspace) {
            final int[] iArr = new int[2];
            final long jFindSpaceOnWorkspace = findSpaceOnWorkspace(item, iArr);
            this.mLauncher.showWorkspace(true, new Runnable() { // from class: com.android.launcher3.accessibility.LauncherAccessibilityDelegate.1
                @Override // java.lang.Runnable
                public void run() {
                    ItemInfo itemInfo = item;
                    if (itemInfo instanceof AppInfo) {
                        ShortcutInfo shortcutInfoMakeShortcut = ((AppInfo) itemInfo).makeShortcut();
                        Launcher launcher = LauncherAccessibilityDelegate.this.mLauncher;
                        long j = jFindSpaceOnWorkspace;
                        int[] iArr2 = iArr;
                        LauncherModel.addItemToDatabase(launcher, shortcutInfoMakeShortcut, -100L, j, iArr2[0], iArr2[1]);
                        ArrayList<ItemInfo> arrayList = new ArrayList<>();
                        arrayList.add(shortcutInfoMakeShortcut);
                        LauncherAccessibilityDelegate.this.mLauncher.bindItems(arrayList, 0, arrayList.size(), true);
                    } else if (itemInfo instanceof PendingAddItemInfo) {
                        PendingAddItemInfo pendingAddItemInfo = (PendingAddItemInfo) itemInfo;
                        Workspace workspace = LauncherAccessibilityDelegate.this.mLauncher.getWorkspace();
                        workspace.snapToPage(workspace.getPageIndexForScreenId(jFindSpaceOnWorkspace));
                        LauncherAccessibilityDelegate.this.mLauncher.addPendingItem(pendingAddItemInfo, -100L, jFindSpaceOnWorkspace, iArr, pendingAddItemInfo.spanX, pendingAddItemInfo.spanY);
                    }
                    LauncherAccessibilityDelegate.this.announceConfirmation(R.string.item_added_to_workspace);
                }
            });
            return true;
        }
        if (action == R.id.action_move_to_workspace) {
            Folder openFolder = this.mLauncher.getWorkspace().getOpenFolder();
            this.mLauncher.closeFolder(openFolder, new boolean[0]);
            ShortcutInfo shortcutInfo = (ShortcutInfo) item;
            openFolder.getInfo().remove(shortcutInfo);
            int[] iArr2 = new int[2];
            LauncherModel.moveItemInDatabase(this.mLauncher, shortcutInfo, -100L, findSpaceOnWorkspace(item, iArr2), iArr2[0], iArr2[1]);
            new Handler().post(new Runnable() { // from class: com.android.launcher3.accessibility.LauncherAccessibilityDelegate.2
                @Override // java.lang.Runnable
                public void run() {
                    ArrayList<ItemInfo> arrayList = new ArrayList<>();
                    arrayList.add(item);
                    LauncherAccessibilityDelegate.this.mLauncher.bindItems(arrayList, 0, arrayList.size(), true);
                    LauncherAccessibilityDelegate.this.announceConfirmation(R.string.item_moved);
                }
            });
            return false;
        }
        if (action != R.id.action_resize) {
            return action == R.id.action_deep_shortcuts && PopupContainerWithArrow.showForIcon((BubbleTextView) host) != null;
        }
        final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) item;
        final ArrayList<Integer> supportedResizeActions = getSupportedResizeActions(host, launcherAppWidgetInfo);
        CharSequence[] charSequenceArr = new CharSequence[supportedResizeActions.size()];
        for (int i = 0; i < supportedResizeActions.size(); i++) {
            charSequenceArr[i] = this.mLauncher.getText(supportedResizeActions.get(i).intValue());
        }
        new AlertDialog.Builder(this.mLauncher).setTitle(R.string.action_resize).setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: com.android.launcher3.accessibility.LauncherAccessibilityDelegate.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                LauncherAccessibilityDelegate.this.performResizeAction(((Integer) supportedResizeActions.get(which)).intValue(), host, launcherAppWidgetInfo);
                dialog.dismiss();
            }
        }).show();
        return true;
    }

    private ArrayList<Integer> getSupportedResizeActions(View host, LauncherAppWidgetInfo info) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        AppWidgetProviderInfo appWidgetInfo = ((LauncherAppWidgetHostView) host).getAppWidgetInfo();
        if (appWidgetInfo == null || !(host.getParent().getParent() instanceof CellLayout)) {
            return arrayList;
        }
        CellLayout cellLayout = (CellLayout) host.getParent().getParent();
        if ((appWidgetInfo.resizeMode & 1) != 0) {
            if (cellLayout.isRegionVacant(info.cellX + info.spanX, info.cellY, 1, info.spanY) || cellLayout.isRegionVacant(info.cellX - 1, info.cellY, 1, info.spanY)) {
                arrayList.add(Integer.valueOf(R.string.action_increase_width));
            }
            if (info.spanX > info.minSpanX && info.spanX > 1) {
                arrayList.add(Integer.valueOf(R.string.action_decrease_width));
            }
        }
        if ((appWidgetInfo.resizeMode & 2) != 0) {
            if (cellLayout.isRegionVacant(info.cellX, info.cellY + info.spanY, info.spanX, 1) || cellLayout.isRegionVacant(info.cellX, info.cellY - 1, info.spanX, 1)) {
                arrayList.add(Integer.valueOf(R.string.action_increase_height));
            }
            if (info.spanY > info.minSpanY && info.spanY > 1) {
                arrayList.add(Integer.valueOf(R.string.action_decrease_height));
            }
        }
        return arrayList;
    }

    void performResizeAction(int action, View host, LauncherAppWidgetInfo info) {
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) host.getLayoutParams();
        CellLayout cellLayout = (CellLayout) host.getParent().getParent();
        cellLayout.markCellsAsUnoccupiedForView(host);
        if (action == R.string.action_increase_width) {
            if ((host.getLayoutDirection() == 1 && cellLayout.isRegionVacant(info.cellX - 1, info.cellY, 1, info.spanY)) || !cellLayout.isRegionVacant(info.cellX + info.spanX, info.cellY, 1, info.spanY)) {
                layoutParams.cellX--;
                info.cellX--;
            }
            layoutParams.cellHSpan++;
            info.spanX++;
        } else if (action == R.string.action_decrease_width) {
            layoutParams.cellHSpan--;
            info.spanX--;
        } else if (action == R.string.action_increase_height) {
            if (!cellLayout.isRegionVacant(info.cellX, info.cellY + info.spanY, info.spanX, 1)) {
                layoutParams.cellY--;
                info.cellY--;
            }
            layoutParams.cellVSpan++;
            info.spanY++;
        } else if (action == R.string.action_decrease_height) {
            layoutParams.cellVSpan--;
            info.spanY--;
        }
        cellLayout.markCellsAsOccupiedForView(host);
        Rect rect = new Rect();
        AppWidgetResizeFrame.getWidgetSizeRanges(this.mLauncher, info.spanX, info.spanY, rect);
        ((LauncherAppWidgetHostView) host).updateAppWidgetSize(null, rect.left, rect.top, rect.right, rect.bottom);
        host.requestLayout();
        LauncherModel.updateItemInDatabase(this.mLauncher, info);
        announceConfirmation(this.mLauncher.getString(R.string.widget_resized, new Object[]{Integer.valueOf(info.spanX), Integer.valueOf(info.spanY)}));
    }

    void announceConfirmation(int resId) {
        announceConfirmation(this.mLauncher.getResources().getString(resId));
    }

    void announceConfirmation(String confirmation) {
        this.mLauncher.getDragLayer().announceForAccessibility(confirmation);
    }

    public boolean isInAccessibleDrag() {
        return this.mDragInfo != null;
    }

    public DragInfo getDragInfo() {
        return this.mDragInfo;
    }

    public void handleAccessibleDrop(View clickedTarget, Rect dropLocation, String confirmation) {
        if (isInAccessibleDrag()) {
            int[] iArr = new int[2];
            if (dropLocation == null) {
                iArr[0] = clickedTarget.getWidth() / 2;
                iArr[1] = clickedTarget.getHeight() / 2;
            } else {
                iArr[0] = dropLocation.centerX();
                iArr[1] = dropLocation.centerY();
            }
            this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(clickedTarget, iArr);
            this.mLauncher.getDragController().completeAccessibleDrag(iArr);
            if (TextUtils.isEmpty(confirmation)) {
                return;
            }
            announceConfirmation(confirmation);
        }
    }

    public void beginAccessibleDrag(View item, ItemInfo info) {
        DragInfo dragInfo = new DragInfo();
        this.mDragInfo = dragInfo;
        dragInfo.info = info;
        this.mDragInfo.item = item;
        this.mDragInfo.dragType = DragType.ICON;
        if (info instanceof FolderInfo) {
            this.mDragInfo.dragType = DragType.FOLDER;
        } else if (info instanceof LauncherAppWidgetInfo) {
            this.mDragInfo.dragType = DragType.WIDGET;
        }
        CellLayout.CellInfo cellInfo = new CellLayout.CellInfo(item, info);
        Rect rect = new Rect();
        this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(item, rect);
        this.mLauncher.getDragController().prepareAccessibleDrag(rect.centerX(), rect.centerY());
        Workspace workspace = this.mLauncher.getWorkspace();
        Folder openFolder = workspace.getOpenFolder();
        if (openFolder != null) {
            if (openFolder.getItemsInReadingOrder().contains(item)) {
                this.mDragSource = openFolder;
            } else {
                this.mLauncher.closeFolder(new boolean[0]);
            }
        }
        if (this.mDragSource == null) {
            this.mDragSource = workspace;
        }
        this.mDragSource.enableAccessibleDrag(true);
        this.mDragSource.startDrag(cellInfo, true);
        if (this.mLauncher.getDragController().isDragging()) {
            this.mLauncher.getDragController().addDragListener(this);
        }
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragEnd() {
        this.mLauncher.getDragController().removeDragListener(this);
        this.mDragInfo = null;
        AccessibilityDragSource accessibilityDragSource = this.mDragSource;
        if (accessibilityDragSource != null) {
            accessibilityDragSource.enableAccessibleDrag(false);
            this.mDragSource = null;
        }
    }

    protected long findSpaceOnWorkspace(ItemInfo itemInfo, int[] iArr) {
        Workspace workspace = this.mLauncher.getWorkspace();
        ArrayList<Long> screenOrder = workspace.getScreenOrder();
        int currentPage = workspace.getCurrentPage();
        long jLongValue = screenOrder.get(currentPage).longValue();
        boolean zFindCellForSpan = ((CellLayout) workspace.getPageAt(currentPage)).findCellForSpan(iArr, itemInfo.spanX, itemInfo.spanY);
        for (int iHasCustomContent = workspace.hasCustomContent(); !zFindCellForSpan && iHasCustomContent < screenOrder.size(); iHasCustomContent++) {
            jLongValue = screenOrder.get(iHasCustomContent).longValue();
            zFindCellForSpan = ((CellLayout) workspace.getPageAt(iHasCustomContent)).findCellForSpan(iArr, itemInfo.spanX, itemInfo.spanY);
        }
        if (zFindCellForSpan) {
            return jLongValue;
        }
        workspace.addExtraEmptyScreen();
        long jCommitExtraEmptyScreen = workspace.commitExtraEmptyScreen();
        if (!workspace.getScreenWithId(jCommitExtraEmptyScreen).findCellForSpan(iArr, itemInfo.spanX, itemInfo.spanY)) {
            Log.wtf(TAG, "Not enough space on an empty screen");
        }
        return jCommitExtraEmptyScreen;
    }
}
