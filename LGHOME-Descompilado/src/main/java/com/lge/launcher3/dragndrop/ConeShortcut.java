package com.lge.launcher3.dragndrop;

import android.view.View;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.lge.contextenginelibrary.BuildConfig;

/* JADX INFO: loaded from: classes.dex */
public class ConeShortcut implements DragSource {
    private Launcher mLauncher;

    @Override // com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
    }

    @Override // com.android.launcher3.DragSource
    public float getIntrinsicIconScaleFactor() {
        return 0.0f;
    }

    @Override // com.android.launcher3.DragSource
    public void onFlingToDeleteCompleted() {
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsAppInfoDropTarget() {
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsDeleteDropTarget() {
        return true;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsFlingToDelete() {
        return false;
    }

    public ConeShortcut(Launcher launcher) {
        this.mLauncher = launcher;
    }

    @Override // com.android.launcher3.DragSource
    public void onDropCompleted(View target, DropTarget.DragObject d, boolean isFlingToDelete, boolean success) {
        if (target instanceof ButtonDropTarget) {
            return;
        }
        this.mLauncher.exitSpringLoadedDragModeDelayed(true, BuildConfig.VERSION_CODE, null);
    }
}
