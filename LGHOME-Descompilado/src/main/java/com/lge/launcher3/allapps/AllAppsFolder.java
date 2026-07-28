package com.lge.launcher3.allapps;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.folderplus.FolderPlusActivity;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsFolder extends Folder {
    private static final boolean DEBUG_ALL_APPS_FOLDER_PLUS = true;
    private static final String TAG = "AllAppsFolder";
    private Folder.DataModel mAllAppsDataModel;

    public AllAppsFolder(Context context, AttributeSet attrs) {
        super(context, attrs);
        Folder.DataModel dataModel = new Folder.DataModel() { // from class: com.lge.launcher3.allapps.AllAppsFolder.1
            @Override // com.android.launcher3.folder.Folder.DataModel
            public void deleteItemFromDatabase(Context context2, ItemInfo item) {
            }

            @Override // com.android.launcher3.folder.Folder.DataModel
            public void addOrMoveItemInDatabase(Context context2, ItemInfo item, long container, long screenId, int cellX, int cellY) {
                addItemToDatabase(context2, item, container, screenId, cellX, cellY);
            }

            @Override // com.android.launcher3.folder.Folder.DataModel
            public void addItemToDatabase(Context context2, ItemInfo item, long container, long screenId, int cellX, int cellY) {
                AllAppsItemFactory.getInstance().addFolderItem(AllAppsFolder.this.mInfo, (ShortcutInfo) item);
            }

            @Override // com.android.launcher3.folder.Folder.DataModel
            public void updateItemInDatabase(Context context2, ItemInfo item) {
                AllAppsItemFactory.getInstance().updateFolderColorAndTitle((FolderInfo) item);
            }

            @Override // com.android.launcher3.folder.Folder.DataModel
            public void moveItemsInDatabase(Context context2, ArrayList<ItemInfo> items, long container, int screen) {
                AllAppsItemFactory.getInstance().moveItemsInDatabase(items, container, screen);
            }
        };
        this.mAllAppsDataModel = dataModel;
        setDataModel(dataModel);
    }

    public static Folder fromXml(Launcher launcher) {
        if (Utilities.isLGUI10_0()) {
            return (Folder) launcher.getLayoutInflater().inflate(R.layout.all_apps_folder_ux10_0, (ViewGroup) null);
        }
        return (Folder) launcher.getLayoutInflater().inflate(R.layout.all_apps_folder, (ViewGroup) null);
    }

    private boolean isInArrangeMode() {
        return this.mLauncher.getAllAppsHost().isInArrangeMode();
    }

    @Override // com.android.launcher3.folder.Folder, android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i(TAG, "Memory is full. so onLongClick() is canceled.");
            return false;
        }
        if (this.mLauncher.isInMultiWindowMode()) {
            Toast.makeText(getContext(), getResources().getString(R.string.home_screen_lock_in_multiwindow), 0).show();
            return true;
        }
        if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(getContext())) {
            Toast.makeText(getContext(), HomeSettingsSharedPreferences.getHomeLockDisableGuideText(getContext()), 0).show();
            return true;
        }
        if (!this.mLauncher.isDraggingEnabled()) {
            return true;
        }
        if (this.mLauncher.isLongClickFromKeyEnter) {
            this.mLauncher.isLongClickFromKeyEnter = false;
            return true;
        }
        return beginDrag(v, false);
    }

    @Override // com.android.launcher3.folder.Folder
    protected boolean beginDrag(View v, boolean accessible) {
        if (v.getTag() instanceof ShortcutInfo) {
            if (isInArrangeMode()) {
                beginDrag(v, accessible, false);
            } else {
                DragOptions dragOptions = new DragOptions();
                dragOptions.isDragFromAllAps = true;
                if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && this.mLauncher.getCarouselLayout() != null) {
                    this.mLauncher.getCarouselLayout().beginDragSharedDeepShortcut(v, this, dragOptions);
                } else {
                    startDragDeepShortcut(v, dragOptions);
                }
            }
        }
        return true;
    }

    @Override // com.android.launcher3.folder.Folder
    protected void onCloseComplete() {
        if (!isInArrangeMode() && this.mCurrentDragInfo != null) {
            this.mContent.addViewForRank(this.mCurrentDragView, this.mCurrentDragInfo, this.mEmptyCellRank);
            this.mItemsInvalidated = true;
            rearrangeChildren();
            this.mSuppressOnAdd = true;
            this.mInfo.add(this.mCurrentDragInfo);
            this.mSuppressOnAdd = false;
        }
        super.onCloseComplete();
    }

    @Override // com.android.launcher3.folder.Folder, com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onAdd(ShortcutInfo item) {
        if (this.mSuppressOnAdd) {
            return;
        }
        this.mContent.createAndAddViewForRank(item, this.mContent.allocateRankForNewItem(item));
        this.mItemsInvalidated = true;
        AllAppsItemFactory.getInstance().addFolderItem(this.mInfo, item);
    }

    @Override // com.android.launcher3.folder.Folder, com.android.launcher3.DropTarget
    public void onDrop(DropTarget.DragObject d) {
        View viewCreateAndAddViewForRank;
        LGLog.d(TAG, "onDrop");
        if (!this.mContent.rankOnCurrentPage(this.mEmptyCellRank)) {
            this.mTargetRank = getTargetRank(d, null);
            this.mReorderAlarmListener.onAlarm(this.mReorderAlarm);
            this.mOnScrollHintAlarm.cancelAlarm();
            this.mScrollPauseAlarm.cancelAlarm();
        }
        this.mContent.completePendingPageChanges();
        ItemInfo itemInfo = new ItemInfo(this.mCurrentDragInfo);
        ShortcutInfo shortcutInfo = this.mCurrentDragInfo;
        int itemCount = this.mEmptyCellRank > getItemCount() ? getItemCount() : this.mEmptyCellRank;
        if (this.mIsExternalDrag) {
            viewCreateAndAddViewForRank = this.mContent.createAndAddViewForRank(shortcutInfo, itemCount);
            AllAppsItemFactory.getInstance().removeItemInfo((AllAppsItemInfo) d.dragInfo);
            AllAppsItemFactory.getInstance().addFolderItem(this.mInfo, shortcutInfo);
            if (d.dragSource != this) {
                updateItemLocationsInDatabaseBatch();
            }
            this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().removeVacantAllAppsItem();
            this.mIsExternalDrag = false;
        } else {
            View view = this.mCurrentDragView;
            this.mContent.addViewForRank(view, shortcutInfo, itemCount);
            viewCreateAndAddViewForRank = view;
        }
        Runnable runnableExitSpringLoadedDragModeOnDrop = exitSpringLoadedDragModeOnDrop(d.dragSource, this, shortcutInfo, itemInfo);
        if (d.dragView.hasDrawn()) {
            float scaleX = getScaleX();
            float scaleY = getScaleY();
            setScaleX(1.0f);
            setScaleY(1.0f);
            this.mLauncher.getDragLayer().animateViewIntoPosition(d.dragView, viewCreateAndAddViewForRank, runnableExitSpringLoadedDragModeOnDrop, null);
            setScaleX(scaleX);
            setScaleY(scaleY);
        } else {
            d.deferDragViewCleanupPostAnimation = false;
            viewCreateAndAddViewForRank.setVisibility(0);
        }
        this.mItemsInvalidated = true;
        rearrangeChildren();
        this.mSuppressOnAdd = true;
        this.mInfo.add(shortcutInfo);
        this.mSuppressOnAdd = false;
        this.mCurrentDragInfo = null;
        this.mDragInProgress = false;
        if (this.mContent.getPageCount() > 1) {
            this.mInfo.setOption(4, true, this.mLauncher);
        }
        this.mContent.showAllCrossHair(isInArrangeMode());
    }

    @Override // com.android.launcher3.folder.Folder, com.android.launcher3.DropTarget
    public boolean isDropEnabled() {
        return !LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || this.mLauncher.getAllAppsHost().isInArrangeMode();
    }

    @Override // com.android.launcher3.folder.Folder
    public void startFolderPlus() {
        try {
            int i = getResources().getConfiguration().orientation;
            Intent intent = new Intent(getContext(), (Class<?>) FolderPlusActivity.class);
            intent.putExtra("folderId", this.mInfo.id);
            intent.putExtra("isAllApps", true);
            intent.putExtra("folderOrientation", i);
            this.mLauncher.startActivityForResult(intent, LauncherConst.REQUEST_FOLDERPLUS);
            this.mLauncher.mSuppressCloseFolder = true;
        } catch (ActivityNotFoundException e) {
            LGLog.e(TAG, "ActivityNotFoundException - ", e);
        }
    }

    @Override // com.android.launcher3.folder.Folder
    protected void updateItemLocationsInDatabaseBatch() {
        ArrayList<View> itemsInReadingOrder = getItemsInReadingOrder();
        ArrayList<ItemInfo> arrayList = new ArrayList<>();
        for (int i = 0; i < itemsInReadingOrder.size(); i++) {
            ItemInfo itemInfo = (ItemInfo) itemsInReadingOrder.get(i).getTag();
            itemInfo.rank = i;
            arrayList.add(itemInfo);
        }
        AllAppsItemFactory.getInstance().moveItemsInDatabase(arrayList, this.mInfo.id, 0);
    }

    @Override // com.android.launcher3.folder.Folder
    public void replaceFolderWithFinalItem() {
        Runnable runnable = new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsFolder.2
            @Override // java.lang.Runnable
            public void run() {
                AllAppsPagedView lGAllAppsPagedView = AllAppsFolder.this.mLauncher.getAllAppsHost().getLGAllAppsPagedView();
                if (lGAllAppsPagedView == null || AllAppsFolder.this.getItemCount() > 1) {
                    return;
                }
                ItemInfo itemInfo = (ItemInfo) AllAppsFolder.this.mFolderIcon.getTag();
                AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) lGAllAppsPagedView.getChildAt((int) itemInfo.screenId);
                if (allAppsPagedCellLayout != null) {
                    if (!(allAppsPagedCellLayout.getChildAt(itemInfo.cellX, itemInfo.cellY) instanceof FolderIcon)) {
                        LGLog.d(AllAppsFolder.TAG, "Can't replaceFolderWithFinalItem - child = " + allAppsPagedCellLayout.getChildAt(itemInfo.cellX, itemInfo.cellY));
                        return;
                    }
                    if (AllAppsFolder.this.getItemCount() == 1) {
                        allAppsPagedCellLayout.removeView(AllAppsFolder.this.mFolderIcon);
                        ShortcutInfo shortcutInfo = AllAppsFolder.this.mInfo.contents.get(0);
                        if (shortcutInfo != null) {
                            LGLog.d(AllAppsFolder.TAG, "replaceFolderWithFinalItem add finalItem = " + shortcutInfo + ", id = " + AllAppsFolder.this.mFolderIcon.getId() + ", cellLayout = " + allAppsPagedCellLayout + ", this = " + this);
                            lGAllAppsPagedView.addViewNewApplication((int) itemInfo.screenId, itemInfo.cellX, itemInfo.cellY, shortcutInfo);
                        }
                    } else {
                        LGLog.d(AllAppsFolder.TAG, "replaceFolderWithFinalItem removeNarrangePage - mFolderIcon = " + AllAppsFolder.this.mFolderIcon + ", id = " + AllAppsFolder.this.mFolderIcon.getId() + ", cellLayout = " + allAppsPagedCellLayout);
                        allAppsPagedCellLayout.removeNarrangePage(AllAppsFolder.this.mFolderIcon.getId(), false);
                    }
                    if (AllAppsFolder.this.mFolderIcon instanceof DropTarget) {
                        AllAppsFolder.this.mDragController.removeDropTarget((DropTarget) AllAppsFolder.this.mFolderIcon);
                    }
                    if (itemInfo instanceof AllAppsItemInfo) {
                        AllAppsItemFactory.getInstance().removeFolder((AllAppsItemInfo) itemInfo);
                        LGLog.d(AllAppsFolder.TAG, "replaceFolderWithFinalItem removeFolder - itemInfoOfFolder " + itemInfo);
                        return;
                    }
                    LGLog.d(AllAppsFolder.TAG, "replaceFolderWithFinalItem can't removeFolder - itemInfoOfFolder " + itemInfo);
                }
            }
        };
        View lastItem = this.mContent.getLastItem();
        if (lastItem != null) {
            LGLog.d(TAG, "replaceFolderWithFinalItem mFolderIcon.performDestroyAnimation - " + lastItem);
            this.mFolderIcon.performDestroyAnimation(lastItem, runnable);
        } else {
            LGLog.d(TAG, "replaceFolderWithFinalItem mFolderIcon.onCompleteRunnable");
            runnable.run();
        }
        this.mDestroyed = true;
    }

    @Override // com.android.launcher3.folder.Folder
    public void animateOpen() {
        super.animateOpen();
        this.mContent.showAllCrossHair(this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().isInArrangeMode());
    }
}
