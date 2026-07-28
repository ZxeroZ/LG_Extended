package com.android.quickstep.util;

import com.android.systemui.shared.recents.model.Task;
import com.android.wm.shell.util.StagedSplitBounds;

/* JADX INFO: loaded from: classes.dex */
public class GroupTask {
    public StagedSplitBounds mStagedSplitBounds;
    public Task task1;
    public Task task2;

    public GroupTask(Task t1, Task t2, StagedSplitBounds stagedSplitBounds) {
        this.task1 = t1;
        this.task2 = t2;
        this.mStagedSplitBounds = stagedSplitBounds;
    }

    public GroupTask(GroupTask group) {
        this.task1 = new Task(group.task1);
        this.task2 = group.task2 != null ? new Task(group.task2) : null;
        this.mStagedSplitBounds = group.mStagedSplitBounds;
    }

    public boolean containsTask(int taskId) {
        Task task;
        return this.task1.key.id == taskId || ((task = this.task2) != null && task.key.id == taskId);
    }

    public boolean hasMultipleTasks() {
        return this.task2 != null;
    }
}
