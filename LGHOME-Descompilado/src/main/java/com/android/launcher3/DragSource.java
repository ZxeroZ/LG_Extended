package com.android.launcher3;

import android.view.View;
import com.android.launcher3.DropTarget;
import com.android.launcher3.logging.UserEventDispatcher;

/* JADX INFO: loaded from: classes.dex */
public interface DragSource extends UserEventDispatcher.LogContainerProvider {
    float getIntrinsicIconScaleFactor();

    void onDropCompleted(View target, DropTarget.DragObject d, boolean isFlingToDelete, boolean success);

    void onFlingToDeleteCompleted();

    boolean supportsAppInfoDropTarget();

    boolean supportsDeleteDropTarget();

    boolean supportsFlingToDelete();
}
