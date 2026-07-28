package com.android.quickstep.inputconsumers;

import android.view.MotionEvent;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.TaskAnimationManager;

/* JADX INFO: loaded from: classes.dex */
public class ResetGestureInputConsumer implements InputConsumer {
    private final TaskAnimationManager mTaskAnimationManager;

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return 256;
    }

    public ResetGestureInputConsumer(TaskAnimationManager taskAnimationManager) {
        this.mTaskAnimationManager = taskAnimationManager;
    }

    @Override // com.android.quickstep.InputConsumer
    public void onMotionEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && this.mTaskAnimationManager.isRecentsAnimationRunning()) {
            this.mTaskAnimationManager.finishRunningRecentsAnimation(false);
        }
    }
}
