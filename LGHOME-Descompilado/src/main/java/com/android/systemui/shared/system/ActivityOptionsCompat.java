package com.android.systemui.shared.system;

import android.app.ActivityOptions;
import android.content.Context;
import android.os.Handler;

/* JADX INFO: loaded from: classes.dex */
public abstract class ActivityOptionsCompat {
    public static ActivityOptions makeSplitScreenOptions(boolean z) {
        return ActivityOptions.makeBasic();
    }

    public static ActivityOptions makeFreeformOptions() {
        ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
        activityOptionsMakeBasic.setLaunchWindowingMode(5);
        return activityOptionsMakeBasic;
    }

    public static ActivityOptions makeRemoteAnimation(RemoteAnimationAdapterCompat remoteAnimationAdapterCompat) {
        return ActivityOptions.makeRemoteAnimation(remoteAnimationAdapterCompat.getWrapped(), remoteAnimationAdapterCompat.getRemoteTransition().getTransition());
    }

    public static ActivityOptions makeRemoteTransition(RemoteTransitionCompat remoteTransitionCompat) {
        return ActivityOptions.makeRemoteTransition(remoteTransitionCompat.getTransition());
    }

    public static ActivityOptions makeCustomAnimation(Context context, int i, int i2, final Runnable runnable, final Handler handler) {
        return ActivityOptions.makeCustomTaskAnimation(context, i, i2, handler, new ActivityOptions.OnAnimationStartedListener() { // from class: com.android.systemui.shared.system.ActivityOptionsCompat.1
            public void onAnimationStarted(long j) {
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    handler.post(runnable2);
                }
            }
        }, null);
    }

    public static ActivityOptions setFreezeRecentTasksList(ActivityOptions activityOptions) {
        activityOptions.setFreezeRecentTasksReordering();
        return activityOptions;
    }

    public static ActivityOptions setLauncherSourceInfo(ActivityOptions activityOptions, long j) {
        activityOptions.setSourceInfo(1, j);
        return activityOptions;
    }
}
