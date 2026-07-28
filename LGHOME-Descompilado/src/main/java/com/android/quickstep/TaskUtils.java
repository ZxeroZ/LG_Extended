package com.android.quickstep;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class TaskUtils {
    private static final String TAG = "TaskUtils";

    private TaskUtils() {
    }

    public static CharSequence getTitle(Context context, Task task) {
        UserHandle userHandleOf = UserHandle.of(task.key.userId);
        ApplicationInfo applicationInfo = new PackageManagerHelper(context).getApplicationInfo(task.key.getPackageName(), userHandleOf, 0);
        if (applicationInfo == null) {
            Log.e(TAG, "Failed to get title for task " + task);
            return "";
        }
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getUserBadgedLabel(applicationInfo.loadLabel(packageManager), userHandleOf);
    }

    public static boolean hasAutoRemoveRecentFlag(Task task) {
        ActivityInfo activityInfo;
        return (task == null || (activityInfo = PackageManagerWrapper.getInstance().getActivityInfo(task.key.getComponent(), task.key.userId)) == null || (activityInfo.flags & 8192) == 0) ? false : true;
    }

    public static ComponentKey getLaunchComponentKeyForTask(Task.TaskKey taskKey) {
        ComponentName component;
        if (taskKey.sourceComponent != null) {
            component = taskKey.sourceComponent;
        } else {
            component = taskKey.getComponent();
        }
        return new ComponentKey(component, UserHandle.of(taskKey.userId));
    }

    public static boolean taskIsATargetWithMode(RemoteAnimationTargetCompat[] targets, int taskId, int mode) {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : targets) {
            if (remoteAnimationTargetCompat.mode == mode && remoteAnimationTargetCompat.taskId == taskId) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkCurrentOrManagedUserId(int currentUserId, Context context) {
        if (currentUserId == UserHandle.myUserId()) {
            return true;
        }
        List<UserHandle> userProfiles = UserCache.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getUserProfiles();
        for (int size = userProfiles.size() - 1; size >= 0; size--) {
            if (currentUserId == userProfiles.get(size).getIdentifier()) {
                return true;
            }
        }
        return false;
    }
}
