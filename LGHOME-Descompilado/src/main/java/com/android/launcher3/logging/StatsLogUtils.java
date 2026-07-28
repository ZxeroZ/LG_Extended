package com.android.launcher3.logging;

import android.view.View;
import android.view.ViewParent;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;

/* JADX INFO: loaded from: classes.dex */
public class StatsLogUtils {
    public static final int LAUNCHER_STATE_ALLAPPS = 3;
    public static final int LAUNCHER_STATE_BACKGROUND = 0;
    public static final int LAUNCHER_STATE_HOME = 1;
    public static final int LAUNCHER_STATE_OVERVIEW = 2;
    private static final int MAXIMUM_VIEW_HIERARCHY_LEVEL = 5;

    public interface LogContainerProvider {
        void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent);
    }

    public interface LogStateProvider {
        int getCurrentState();
    }

    public static LogContainerProvider getLaunchProviderRecursive(View v) {
        if (v != null) {
            ViewParent parent = v.getParent();
            int i = 5;
            while (parent != null) {
                int i2 = i - 1;
                if (i <= 0) {
                    break;
                }
                if (parent instanceof LogContainerProvider) {
                    return (LogContainerProvider) parent;
                }
                parent = parent.getParent();
                i = i2;
            }
        }
        return null;
    }
}
