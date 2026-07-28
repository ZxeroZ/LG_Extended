package com.android.systemui.shared.system;

import android.app.ActivityManager;
import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
public class TaskDescriptionCompat {
    private ActivityManager.TaskDescription mTaskDescription;

    public TaskDescriptionCompat(ActivityManager.TaskDescription td) {
        this.mTaskDescription = td;
    }

    public int getPrimaryColor() {
        ActivityManager.TaskDescription taskDescription = this.mTaskDescription;
        if (taskDescription != null) {
            return taskDescription.getPrimaryColor();
        }
        return 0;
    }

    public int getBackgroundColor() {
        ActivityManager.TaskDescription taskDescription = this.mTaskDescription;
        if (taskDescription != null) {
            return taskDescription.getBackgroundColor();
        }
        return 0;
    }

    public static Bitmap getIcon(ActivityManager.TaskDescription desc, int userId) {
        if (desc.getInMemoryIcon() != null) {
            return desc.getInMemoryIcon();
        }
        return ActivityManager.TaskDescription.loadTaskDescriptionIcon(desc.getIconFilename(), userId);
    }
}
