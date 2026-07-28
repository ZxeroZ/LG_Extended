package com.android.quickstep.fallback;

import com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController;
import com.android.quickstep.RecentsActivity;

/* JADX INFO: loaded from: classes.dex */
public class RecentsTaskController extends TaskViewTouchController<RecentsActivity> {
    @Override // com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController
    protected boolean isRecentsModal() {
        return false;
    }

    public RecentsTaskController(RecentsActivity activity) {
        super(activity);
    }

    @Override // com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController
    protected boolean isRecentsInteractive() {
        return ((RecentsActivity) this.mActivity).hasWindowFocus();
    }
}
