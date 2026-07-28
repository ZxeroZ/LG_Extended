package com.android.launcher3.touch;

import android.view.View;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.model.data.ItemInfo;

/* JADX INFO: loaded from: classes.dex */
public class ItemLongClickListener {
    public static View.OnLongClickListener INSTANCE_WORKSPACE = new View.OnLongClickListener() { // from class: com.android.launcher3.touch.-$$Lambda$ItemLongClickListener$nz9MSaglTImbNX-jBQmvpOY7s8M
        @Override // android.view.View.OnLongClickListener
        public final boolean onLongClick(View view) {
            return ItemLongClickListener.onWorkspaceItemLongClick(view);
        }
    };
    public static View.OnLongClickListener INSTANCE_ALL_APPS = new View.OnLongClickListener() { // from class: com.android.launcher3.touch.-$$Lambda$ItemLongClickListener$w0E77iw3NhDMXITrEZo4RYOwnrg
        @Override // android.view.View.OnLongClickListener
        public final boolean onLongClick(View view) {
            return ItemLongClickListener.onAllAppsItemLongClick(view);
        }
    };

    public static void beginDrag(View v, Launcher launcher, ItemInfo info, DragOptions dragOptions) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean onWorkspaceItemLongClick(View v) {
        Launcher launcher = Launcher.getLauncher(v.getContext());
        if (!canStartDrag(launcher)) {
            return false;
        }
        if ((!launcher.isInState(LauncherState.NORMAL) && !launcher.isInState(LauncherState.OVERVIEW)) || !(v.getTag() instanceof ItemInfo)) {
            return false;
        }
        launcher.setWaitingForResult(null);
        beginDrag(v, launcher, (ItemInfo) v.getTag(), new DragOptions());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean onAllAppsItemLongClick(View v) {
        Launcher launcher = Launcher.getLauncher(v.getContext());
        if (!canStartDrag(launcher)) {
            return false;
        }
        if ((!launcher.isInState(LauncherState.ALL_APPS) && !launcher.isInState(LauncherState.OVERVIEW)) || launcher.getWorkspace().isSwitchingState()) {
            return false;
        }
        launcher.getDeviceProfile();
        DragOptions dragOptions = new DragOptions();
        dragOptions.isDragFromOverView = true;
        launcher.getWorkspace().beginDragSharedDeepShortcut(v, launcher.getAllAppsHost().getLGAllAppsPagedView(), dragOptions);
        return false;
    }

    public static boolean canStartDrag(Launcher launcher) {
        return (launcher == null || launcher.isWorkspaceLocked() || launcher.getDragController().isDragging()) ? false : true;
    }
}
