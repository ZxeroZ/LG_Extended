package com.android.launcher3;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.android.launcher3.DropTarget;
import com.android.launcher3.UninstallDropTarget;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.FlingAnimation;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsItemInfo;
import com.lge.launcher3.droptarget.ButtonDropTargetUtils;
import com.lge.launcher3.uninstallmode.DeleteItemDialog;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.wing.SwivelAppIconView;

/* JADX INFO: loaded from: classes.dex */
public class DeleteDropTarget extends ButtonDropTarget {
    private int mControlType;

    public DeleteDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeleteDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mControlType = 0;
    }

    @Override // com.android.launcher3.ButtonDropTarget, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHoverColor = getResources().getColor(R.color.delete_target_hover_tint);
        setDrawable(R.drawable.ic_homescreen_trashcan);
    }

    public static boolean supportsDrop(Object info) {
        if (!LGHomeFeature.isEnableDefaultHome()) {
            return (info instanceof ShortcutInfo) || (info instanceof LauncherAppWidgetInfo) || (info instanceof FolderInfo) || (info instanceof AllAppsItemInfo);
        }
        if (!(info instanceof LauncherAppWidgetInfo)) {
            if (!(info instanceof ShortcutInfo)) {
                return false;
            }
            ShortcutInfo shortcutInfo = (ShortcutInfo) info;
            if (shortcutInfo.itemType != 1 && shortcutInfo.itemType != 6) {
                return false;
            }
        }
        return true;
    }

    @Override // com.android.launcher3.ButtonDropTarget
    protected boolean supportsDrop(DragSource source, Object info) {
        if (info instanceof ShortcutInfo) {
            setDropTargetTitle(getContext().getString(R.string.delete_target_shortcut_label));
            setDrawable(R.drawable.ic_homescreen_trashcan);
        } else if (info instanceof LauncherAppWidgetInfo) {
            setDropTargetTitle(getContext().getString(R.string.delete_target_widget_label));
            setDrawable(R.drawable.ic_homescreen_trashcan);
        } else if (info instanceof FolderInfo) {
            setDropTargetTitle(getContext().getString(R.string.delete_target_folder_label));
            setDrawable(R.drawable.ic_homescreen_trashcan);
        } else if (info instanceof AllAppsItemInfo) {
            setDropTargetTitle(getContext().getString(R.string.droptarget_cancel));
            setDrawable(R.drawable.ic_homescreen_reset);
        }
        boolean z = true;
        boolean z2 = source.supportsDeleteDropTarget() && supportsDrop(info);
        if (!LGHomeFeature.isEnableDefaultHome()) {
            return z2;
        }
        boolean zIsShortcutWithApplicationType = ButtonDropTargetUtils.isShortcutWithApplicationType(getContext(), info);
        if (!z2 && !zIsShortcutWithApplicationType) {
            z = false;
        }
        return z;
    }

    @Override // com.android.launcher3.ButtonDropTarget
    protected void completeDrop(DropTarget.DragObject d) {
        DeleteItemDialog.showDialogFragment(this.mLauncher, (ItemInfo) d.dragInfo, d.dragSource instanceof Workspace ? this.mLauncher.getWorkspace().getDragInfo().cell : null, d);
    }

    @Override // com.android.launcher3.ButtonDropTarget, com.android.launcher3.DropTarget
    public void onDrop(DropTarget.DragObject d) {
        if (d.dragSource instanceof UninstallDropTarget.UninstallSource) {
            ((UninstallDropTarget.UninstallSource) d.dragSource).deferCompleteDropAfterUninstallActivity();
        }
        super.onDrop(d);
    }

    /* JADX WARN: Type inference failed for: r3v2, types: [com.android.launcher3.DeleteDropTarget$1] */
    public static boolean removeWorkspaceOrFolderItem(Launcher launcher, ItemInfo item, View view) {
        if ((item instanceof ShortcutInfo) || (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && (item instanceof AppInfo))) {
            if (item.swivelPosition == -1) {
                LauncherModel.deleteItemFromDatabase(launcher, item);
            }
        } else if (item instanceof FolderInfo) {
            FolderInfo folderInfo = (FolderInfo) item;
            launcher.removeFolder(folderInfo);
            LauncherModel.deleteFolderContentsFromDatabase(launcher, folderInfo);
        } else {
            if (!(item instanceof LauncherAppWidgetInfo)) {
                return false;
            }
            final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) item;
            launcher.removeAppWidget(launcherAppWidgetInfo);
            LauncherModel.deleteItemFromDatabase(launcher, launcherAppWidgetInfo);
            final LauncherAppWidgetHost appWidgetHost = launcher.getAppWidgetHost();
            if (appWidgetHost != null && !launcherAppWidgetInfo.isCustomWidget() && launcherAppWidgetInfo.isWidgetIdValid()) {
                new AsyncTask<Void, Void, Void>() { // from class: com.android.launcher3.DeleteDropTarget.1
                    /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
                    @Override // android.os.AsyncTask
                    public Void doInBackground(Void... args) {
                        appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }
        if (view != null) {
            if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && launcher.getCarouselLayout() != null) {
                UninstallModeManager.getInstance(launcher).runUninstallBadgeAnimation(false, 0);
                ((SwivelAppIconView) view).setVisibilityForUninstallBadge(false, 0);
                launcher.getCarouselLayout().getAdapter().onItemRemove(view);
                UninstallModeManager.getInstance(launcher).runUninstallBadgeAnimation(true, 0);
            } else {
                if (UninstallModeManager.getInstance(launcher).isInUninstallMode()) {
                    launcher.shrinkAndFadeOutWorkspaceItem(view);
                } else {
                    launcher.getWorkspace().removeWorkspaceItem(view);
                }
                launcher.getWorkspace().stripEmptyScreens();
                UninstallModeManager.getInstance(launcher).removeFolderItem(launcher.getWorkspace(), item, view);
            }
        }
        return true;
    }

    @Override // com.android.launcher3.ButtonDropTarget, com.android.launcher3.DropTarget
    public void onFlingToDelete(final DropTarget.DragObject d, PointF vel) {
        d.dragView.setColor(0);
        d.dragView.updateInitialScaleToCurrentScale();
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        FlingAnimation flingAnimation = new FlingAnimation(d, vel, getIconRect(d.dragView.getMeasuredWidth(), d.dragView.getMeasuredHeight(), this.mDrawable.getIntrinsicWidth(), this.mDrawable.getIntrinsicHeight()), dragLayer);
        final int duration = flingAnimation.getDuration();
        final long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        dragLayer.animateView(d.dragView, flingAnimation, duration, new TimeInterpolator() { // from class: com.android.launcher3.DeleteDropTarget.2
            private int mCount = -1;
            private float mOffset = 0.0f;

            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float t) {
                int i = this.mCount;
                if (i < 0) {
                    this.mCount = i + 1;
                } else if (i == 0) {
                    this.mOffset = Math.min(0.5f, (AnimationUtils.currentAnimationTimeMillis() - jCurrentAnimationTimeMillis) / duration);
                    this.mCount++;
                }
                return Math.min(1.0f, this.mOffset + t);
            }
        }, new Runnable() { // from class: com.android.launcher3.DeleteDropTarget.3
            @Override // java.lang.Runnable
            public void run() {
                DeleteDropTarget.this.mLauncher.exitSpringLoadedDragMode();
                DeleteDropTarget.this.completeDrop(d);
                DeleteDropTarget.this.mLauncher.getDragController().onDeferredEndFling(d);
            }
        }, 0, null);
    }

    @Override // com.android.launcher3.ButtonDropTarget
    protected String getAccessibilityDropConfirmation() {
        return getResources().getString(R.string.item_removed);
    }

    @Override // com.android.launcher3.ButtonDropTarget
    public LauncherLogProto.Target getDropTargetForLogging() {
        LauncherLogProto.Target targetNewTarget = LoggerUtils.newTarget(2);
        targetNewTarget.controlType = this.mControlType;
        return targetNewTarget;
    }
}
