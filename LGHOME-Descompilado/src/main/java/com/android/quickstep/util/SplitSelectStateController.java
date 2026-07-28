package com.android.quickstep.util;

import android.app.ActivityOptions;
import android.app.ActivityThread;
import android.app.IApplicationThread;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.RemoteAnimationAdapter;
import android.view.SurfaceControl;
import android.window.TransitionInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.util.Executors;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskAnimationManager;
import com.android.quickstep.TaskViewUtils;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.RemoteAnimationAdapterCompat;
import com.android.systemui.shared.system.RemoteAnimationRunnerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.RemoteTransitionCompat;
import com.android.systemui.shared.system.RemoteTransitionRunner;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class SplitSelectStateController {
    private final Context mContext;
    private final DepthController mDepthController;
    private final Handler mHandler;
    private Intent mInitialTaskIntent;
    private TaskView mLaunchingTaskView;
    private boolean mRecentsAnimationRunning;
    private String mSecondTaskPackageName;
    private int mStagePosition;
    private final StateManager mStateManager;
    private final SystemUiProxy mSystemUiProxy;
    private int mInitialTaskId = -1;
    private int mSecondTaskId = -1;

    public SplitSelectStateController(Context context, Handler handler, StateManager stateManager, DepthController depthController) {
        this.mContext = context;
        this.mHandler = handler;
        this.mSystemUiProxy = SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(context);
        this.mStateManager = stateManager;
        this.mDepthController = depthController;
    }

    public void setInitialTaskSelect(int taskId, int stagePosition) {
        this.mInitialTaskId = taskId;
        this.mStagePosition = stagePosition;
        this.mInitialTaskIntent = null;
    }

    public void setInitialTaskSelect(Intent intent, int stagePosition) {
        this.mInitialTaskIntent = intent;
        this.mStagePosition = stagePosition;
        this.mInitialTaskId = -1;
    }

    public void launchSplitTasks(Consumer<Boolean> callback) {
        Intent intent;
        if (this.mInitialTaskIntent != null) {
            Intent intent2 = new Intent();
            if (TextUtils.equals(this.mInitialTaskIntent.getComponent().getPackageName(), this.mSecondTaskPackageName)) {
                intent2.addFlags(134217728);
            }
            intent = intent2;
        } else {
            intent = null;
        }
        Intent intent3 = this.mInitialTaskIntent;
        launchTasks(this.mInitialTaskId, intent3 != null ? PendingIntent.getActivity(this.mContext, 0, intent3, QuickStepContract.SYSUI_STATE_VOICE_INTERACTION_WINDOW_SHOWING) : null, intent, this.mSecondTaskId, this.mStagePosition, callback, false, 0.5f);
    }

    public void setSecondTask(Task task) {
        this.mSecondTaskId = task.key.id;
        if (this.mInitialTaskIntent != null) {
            this.mSecondTaskPackageName = task.getTopComponent().getPackageName();
        }
    }

    public int getInitialTaskId() {
        return this.mInitialTaskId;
    }

    public void launchTasks(TaskView taskView, Consumer<Boolean> callback, boolean freezeTaskList) {
        this.mLaunchingTaskView = taskView;
        launchTasks(this.mInitialTaskId, taskView.getTaskIdAttributeContainers()[0].getTask().key.id, 0, callback, freezeTaskList, taskView.getSplitRatio());
    }

    public void launchTasks(int taskId1, int taskId2, int stagePosition, Consumer<Boolean> callback, boolean freezeTaskList, float splitRatio) {
        launchTasks(taskId1, null, null, taskId2, stagePosition, callback, freezeTaskList, splitRatio);
    }

    public void launchTasks(int taskId1, PendingIntent taskPendingIntent, Intent fillInIntent, int taskId2, int stagePosition, Consumer<Boolean> callback, boolean freezeTaskList, float splitRatio) {
        int[] iArr = stagePosition == 0 ? new int[]{taskId1, taskId2} : new int[]{taskId2, taskId1};
        if (TaskAnimationManager.ENABLE_SHELL_TRANSITIONS) {
            this.mSystemUiProxy.startTasks(iArr[0], null, iArr[1], null, 1, splitRatio, new RemoteTransitionCompat((RemoteTransitionRunner) new RemoteSplitLaunchTransitionRunner(taskId1, taskPendingIntent, taskId2, callback), (Executor) Executors.MAIN_EXECUTOR, (IApplicationThread) ActivityThread.currentActivityThread().getApplicationThread()));
            return;
        }
        RemoteAnimationAdapter remoteAnimationAdapter = new RemoteAnimationAdapter(RemoteAnimationAdapterCompat.wrapRemoteAnimationRunner(new RemoteSplitLaunchAnimationRunner(taskId1, taskPendingIntent, taskId2, callback)), 300L, 150L, ActivityThread.currentActivityThread().getApplicationThread());
        ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
        if (taskPendingIntent == null) {
            this.mSystemUiProxy.startTasksWithLegacyTransition(iArr[0], activityOptionsMakeBasic.toBundle(), iArr[1], null, 1, splitRatio, remoteAnimationAdapter);
        } else {
            this.mSystemUiProxy.startIntentAndTaskWithLegacyTransition(taskPendingIntent, fillInIntent, taskId2, activityOptionsMakeBasic.toBundle(), null, stagePosition, splitRatio, remoteAnimationAdapter);
        }
    }

    public int getActiveSplitStagePosition() {
        return this.mStagePosition;
    }

    public void setRecentsAnimationRunning(boolean running) {
        this.mRecentsAnimationRunning = running;
    }

    /* JADX INFO: Access modifiers changed from: private */
    class RemoteSplitLaunchTransitionRunner implements RemoteTransitionRunner {
        private final int mInitialTaskId;
        private final PendingIntent mInitialTaskPendingIntent;
        private final int mSecondTaskId;
        private final Consumer<Boolean> mSuccessCallback;

        RemoteSplitLaunchTransitionRunner(int initialTaskId, PendingIntent initialTaskPendingIntent, int secondTaskId, Consumer<Boolean> callback) {
            this.mInitialTaskId = initialTaskId;
            this.mInitialTaskPendingIntent = initialTaskPendingIntent;
            this.mSecondTaskId = secondTaskId;
            this.mSuccessCallback = callback;
        }

        @Override // com.android.systemui.shared.system.RemoteTransitionRunner
        public void startAnimation(IBinder transition, TransitionInfo info, SurfaceControl.Transaction t, final Runnable finishCallback) {
            TaskViewUtils.composeRecentsSplitLaunchAnimator(this.mInitialTaskId, this.mInitialTaskPendingIntent, this.mSecondTaskId, info, t, new Runnable() { // from class: com.android.quickstep.util.-$$Lambda$SplitSelectStateController$RemoteSplitLaunchTransitionRunner$G6atAScamQKyFZDv-BKWe3Y_oDg
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startAnimation$0$SplitSelectStateController$RemoteSplitLaunchTransitionRunner(finishCallback);
                }
            });
            SplitSelectStateController.this.resetState();
        }

        public /* synthetic */ void lambda$startAnimation$0$SplitSelectStateController$RemoteSplitLaunchTransitionRunner(Runnable runnable) {
            runnable.run();
            Consumer<Boolean> consumer = this.mSuccessCallback;
            if (consumer != null) {
                consumer.accept(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class RemoteSplitLaunchAnimationRunner implements RemoteAnimationRunnerCompat {
        private final int mInitialTaskId;
        private final PendingIntent mInitialTaskPendingIntent;
        private final int mSecondTaskId;
        private final Consumer<Boolean> mSuccessCallback;

        RemoteSplitLaunchAnimationRunner(int initialTaskId, PendingIntent initialTaskPendingIntent, int secondTaskId, Consumer<Boolean> successCallback) {
            this.mInitialTaskId = initialTaskId;
            this.mInitialTaskPendingIntent = initialTaskPendingIntent;
            this.mSecondTaskId = secondTaskId;
            this.mSuccessCallback = successCallback;
        }

        @Override // com.android.systemui.shared.system.RemoteAnimationRunnerCompat
        public void onAnimationStart(int transit, final RemoteAnimationTargetCompat[] apps, final RemoteAnimationTargetCompat[] wallpapers, final RemoteAnimationTargetCompat[] nonApps, final Runnable finishedCallback) {
            Utilities.postAsyncCallback(SplitSelectStateController.this.mHandler, new Runnable() { // from class: com.android.quickstep.util.-$$Lambda$SplitSelectStateController$RemoteSplitLaunchAnimationRunner$fE5C0JsLiv4UubKxjfAKON_xXtk
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onAnimationStart$1$SplitSelectStateController$RemoteSplitLaunchAnimationRunner(apps, wallpapers, nonApps, finishedCallback);
                }
            });
        }

        public /* synthetic */ void lambda$onAnimationStart$1$SplitSelectStateController$RemoteSplitLaunchAnimationRunner(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, final Runnable runnable) {
            TaskViewUtils.composeRecentsSplitLaunchAnimatorLegacy(SplitSelectStateController.this.mLaunchingTaskView, this.mInitialTaskId, this.mInitialTaskPendingIntent, this.mSecondTaskId, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, SplitSelectStateController.this.mStateManager, SplitSelectStateController.this.mDepthController, new Runnable() { // from class: com.android.quickstep.util.-$$Lambda$SplitSelectStateController$RemoteSplitLaunchAnimationRunner$Fqx8S1bf_ToHonsjoXBforalfqk
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onAnimationStart$0$SplitSelectStateController$RemoteSplitLaunchAnimationRunner(runnable);
                }
            });
        }

        public /* synthetic */ void lambda$onAnimationStart$0$SplitSelectStateController$RemoteSplitLaunchAnimationRunner(Runnable runnable) {
            runnable.run();
            Consumer<Boolean> consumer = this.mSuccessCallback;
            if (consumer != null) {
                consumer.accept(true);
            }
            SplitSelectStateController.this.resetState();
        }

        @Override // com.android.systemui.shared.system.RemoteAnimationRunnerCompat
        public void onAnimationCancelled() {
            Utilities.postAsyncCallback(SplitSelectStateController.this.mHandler, new Runnable() { // from class: com.android.quickstep.util.-$$Lambda$SplitSelectStateController$RemoteSplitLaunchAnimationRunner$zDah41eDNv-LfeYlqcCyird8D9Y
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onAnimationCancelled$2$SplitSelectStateController$RemoteSplitLaunchAnimationRunner();
                }
            });
        }

        public /* synthetic */ void lambda$onAnimationCancelled$2$SplitSelectStateController$RemoteSplitLaunchAnimationRunner() {
            Consumer<Boolean> consumer = this.mSuccessCallback;
            if (consumer != null) {
                consumer.accept(Boolean.valueOf(SplitSelectStateController.this.mRecentsAnimationRunning));
            }
            SplitSelectStateController.this.resetState();
        }
    }

    public void resetState() {
        this.mInitialTaskId = -1;
        this.mInitialTaskIntent = null;
        this.mSecondTaskId = -1;
        this.mStagePosition = -1;
        this.mRecentsAnimationRunning = false;
        this.mLaunchingTaskView = null;
    }

    public boolean isSplitSelectActive() {
        return isInitialTaskIntentSet() && this.mSecondTaskId == -1;
    }

    public boolean isBothSplitAppsConfirmed() {
        return isInitialTaskIntentSet() && this.mSecondTaskId != -1;
    }

    private boolean isInitialTaskIntentSet() {
        return (this.mInitialTaskId == -1 && this.mInitialTaskIntent == null) ? false : true;
    }
}
