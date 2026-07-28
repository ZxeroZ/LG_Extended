package com.android.quickstep.util;

import android.app.TaskInfo;
import com.android.systemui.shared.system.ActivityManagerWrapper;

/* JADX INFO: loaded from: classes.dex */
public final class AssistantUtilities {
    public static boolean isExcludedAssistantRunning() {
        return isExcludedAssistant(ActivityManagerWrapper.getInstance().getRunningTask());
    }

    public static boolean isExcludedAssistant(TaskInfo info) {
        return (info == null || getActivityType(info) != 4 || (info.baseIntent.getFlags() & 8388608) == 0) ? false : true;
    }

    public static int getActivityType(TaskInfo info) {
        return info.configuration.windowConfiguration.getActivityType();
    }

    private AssistantUtilities() {
    }
}
