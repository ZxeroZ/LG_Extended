package com.android.quickstep;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.RemoteAnimationTarget;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Executors;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.TaskAnimationManager;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.launcher3.quickstep.ActivityManagerWrapperEx;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

/* JADX INFO: loaded from: classes.dex */
public class TaskAnimationManager implements RecentsAnimationCallbacks.RecentsAnimationListener {
    public static final boolean ENABLE_SHELL_TRANSITIONS = SystemProperties.getBoolean("persist.wm.debug.shell_transit", false);
    private static final String TAG = TaskAnimationManager.class.getSimpleName();
    private RecentsAnimationCallbacks mCallbacks;
    private RecentsAnimationController mController;
    private Context mCtx;
    private RemoteAnimationTargetCompat mLastAppearedTaskTarget;
    private GestureState mLastGestureState;
    private RecentsAnimationTargets mTargets;

    public void dump() {
    }

    TaskAnimationManager(Context ctx) {
        this.mCtx = ctx;
    }

    public void preloadRecentsAnimation(final Intent intent) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskAnimationManager$SJrVOzvAeq6S3F5MuPzVclX2hsU
            @Override // java.lang.Runnable
            public final void run() {
                ActivityManagerWrapper.getInstance().startRecentsActivity(intent, 0L, null, null, null);
            }
        });
    }

    public RecentsAnimationCallbacks startRecentsAnimation(final GestureState gestureState, final Intent intent, RecentsAnimationCallbacks.RecentsAnimationListener listener) {
        if (this.mController != null) {
            Log.e("TaskAnimationManager", "New recents animation started before old animation completed", new Exception());
        }
        finishRunningRecentsAnimation(false);
        if (this.mCallbacks != null) {
            cleanUpRecentsAnimation();
        }
        BaseActivityInterface activityInterface = gestureState.getActivityInterface();
        this.mLastGestureState = gestureState;
        RecentsAnimationCallbacks recentsAnimationCallbacks = new RecentsAnimationCallbacks(SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mCtx), activityInterface.allowMinimizeSplitScreen());
        this.mCallbacks = recentsAnimationCallbacks;
        recentsAnimationCallbacks.addListener(new AnonymousClass1());
        this.mCallbacks.addListener(gestureState);
        this.mCallbacks.addListener(listener);
        final long swipeUpStartTimeMs = gestureState.getSwipeUpStartTimeMs();
        Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskAnimationManager$jNCAEMfbTuCDVtm6ULDnp68pNl0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$startRecentsAnimation$2$TaskAnimationManager(gestureState, intent, swipeUpStartTimeMs);
            }
        });
        gestureState.setState(GestureState.STATE_RECENTS_ANIMATION_INITIALIZED);
        return this.mCallbacks;
    }

    /* JADX INFO: renamed from: com.android.quickstep.TaskAnimationManager$1, reason: invalid class name */
    class AnonymousClass1 implements RecentsAnimationCallbacks.RecentsAnimationListener {
        AnonymousClass1() {
        }

        @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
        public void onRecentsAnimationStart(RecentsAnimationController controller, RecentsAnimationTargets targets) {
            if (TaskAnimationManager.this.mCallbacks == null) {
                return;
            }
            TaskAnimationManager.this.mController = controller;
            TaskAnimationManager.this.mTargets = targets;
            TaskAnimationManager taskAnimationManager = TaskAnimationManager.this;
            taskAnimationManager.mLastAppearedTaskTarget = taskAnimationManager.mTargets.findTask(TaskAnimationManager.this.mLastGestureState.getRunningTaskId());
            TaskAnimationManager.this.mLastGestureState.updateLastAppearedTaskTarget(TaskAnimationManager.this.mLastAppearedTaskTarget);
        }

        @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
        public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> thumbnailDatas) {
            TaskAnimationManager.this.cleanUpRecentsAnimation();
        }

        @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
        public void onRecentsAnimationFinished(RecentsAnimationController controller) {
            TaskAnimationManager.this.cleanUpRecentsAnimation();
        }

        @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
        public void onTasksAppeared(RemoteAnimationTargetCompat[] appearedTaskTargets) {
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = appearedTaskTargets[0];
            BaseActivityInterface activityInterface = TaskAnimationManager.this.mLastGestureState.getActivityInterface();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (RemoteAnimationTargetCompat remoteAnimationTargetCompat2 : appearedTaskTargets) {
                if (remoteAnimationTargetCompat2.activityType != 2) {
                    arrayList.add(remoteAnimationTargetCompat2);
                } else {
                    arrayList2.add(remoteAnimationTargetCompat2);
                }
            }
            RemoteAnimationTarget[] remoteAnimationTargetArr = (RemoteAnimationTarget[]) arrayList.stream().map(new Function() { // from class: com.android.quickstep.-$$Lambda$TaskAnimationManager$1$tAyCrZ2lAkHHAGjbVm8XNYk3trc
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((RemoteAnimationTargetCompat) obj).unwrap();
                }
            }).toArray(new IntFunction() { // from class: com.android.quickstep.-$$Lambda$TaskAnimationManager$1$YGEe6aMC6cqmmu6KU22_vLEQJ-A
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    return TaskAnimationManager.AnonymousClass1.lambda$onTasksAppeared$0(i);
                }
            });
            if (((RemoteAnimationTarget[]) arrayList2.stream().map(new Function() { // from class: com.android.quickstep.-$$Lambda$TaskAnimationManager$1$tAyCrZ2lAkHHAGjbVm8XNYk3trc
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((RemoteAnimationTargetCompat) obj).unwrap();
                }
            }).toArray(new IntFunction() { // from class: com.android.quickstep.-$$Lambda$TaskAnimationManager$1$RxEfG-dvZV3CNtcOXQnl_PVp8lM
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    return TaskAnimationManager.AnonymousClass1.lambda$onTasksAppeared$1(i);
                }
            })).length > 0 && (activityInterface.getCreatedActivity() instanceof RecentsActivity)) {
                ((RecentsActivity) activityInterface.getCreatedActivity()).startHome();
                return;
            }
            SystemUiProxy.INSTANCE.getNoCreate().onStartingSplitLegacy(remoteAnimationTargetArr);
            if (TaskAnimationManager.this.mController != null) {
                if (TaskAnimationManager.this.mLastAppearedTaskTarget == null || remoteAnimationTargetCompat.taskId != TaskAnimationManager.this.mLastAppearedTaskTarget.taskId) {
                    if (TaskAnimationManager.this.mLastAppearedTaskTarget != null) {
                        TaskAnimationManager.this.mController.removeTaskTarget(TaskAnimationManager.this.mLastAppearedTaskTarget);
                    }
                    TaskAnimationManager.this.mLastAppearedTaskTarget = remoteAnimationTargetCompat;
                    TaskAnimationManager.this.mLastGestureState.updateLastAppearedTaskTarget(TaskAnimationManager.this.mLastAppearedTaskTarget);
                }
            }
        }

        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: NEW_ARRAY (r0v0 int A[IMMUTABLE_TYPE]) (LINE:143) type: android.view.RemoteAnimationTarget[] */
        static /* synthetic */ RemoteAnimationTarget[] lambda$onTasksAppeared$0(int i) {
            return new RemoteAnimationTarget[i];
        }

        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: NEW_ARRAY (r0v0 int A[IMMUTABLE_TYPE]) (LINE:146) type: android.view.RemoteAnimationTarget[] */
        static /* synthetic */ RemoteAnimationTarget[] lambda$onTasksAppeared$1(int i) {
            return new RemoteAnimationTarget[i];
        }

        @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
        public boolean onSwitchToScreenshot(Runnable onFinished) {
            onFinished.run();
            return true;
        }
    }

    private /* synthetic */ void lambda$startRecentsAnimation$1(Intent intent, long j) {
        ActivityManagerWrapper.getInstance().startRecentsActivity(intent, j, this.mCallbacks, null, null);
    }

    public /* synthetic */ void lambda$startRecentsAnimation$2$TaskAnimationManager(GestureState gestureState, Intent intent, long j) {
        LGLog.i(TouchInteractionService.MTAG + TAG, "startRecentsAnimation : " + gestureState.getDisplayId() + ", " + intent);
        ActivityManagerWrapperEx.getInstance().startRecentsActivityEx(intent, j, this.mCallbacks, null, null, gestureState.getDisplayId());
    }

    public RecentsAnimationCallbacks continueRecentsAnimation(GestureState gestureState) {
        this.mCallbacks.removeListener(this.mLastGestureState);
        this.mLastGestureState = gestureState;
        this.mCallbacks.addListener(gestureState);
        gestureState.setState(GestureState.STATE_RECENTS_ANIMATION_INITIALIZED | GestureState.STATE_RECENTS_ANIMATION_STARTED);
        gestureState.updateLastAppearedTaskTarget(this.mLastAppearedTaskTarget);
        return this.mCallbacks;
    }

    public void finishRunningRecentsAnimation(boolean toHome) {
        Runnable __lambda_jo5sitwdnsscytyewikrw2tm4q;
        if (this.mController != null) {
            this.mCallbacks.notifyAnimationCanceled();
            Handler handler = Executors.MAIN_EXECUTOR.getHandler();
            if (toHome) {
                final RecentsAnimationController recentsAnimationController = this.mController;
                Objects.requireNonNull(recentsAnimationController);
                __lambda_jo5sitwdnsscytyewikrw2tm4q = new Runnable() { // from class: com.android.quickstep.-$$Lambda$O7aNsrBVqDLOWn6uOecL4_DLpjk
                    @Override // java.lang.Runnable
                    public final void run() {
                        recentsAnimationController.finishAnimationToHome();
                    }
                };
            } else {
                RecentsAnimationController recentsAnimationController2 = this.mController;
                Objects.requireNonNull(recentsAnimationController2);
                __lambda_jo5sitwdnsscytyewikrw2tm4q = new $$Lambda$jo5SItwDnSScytyEwiKrw2tm4Q(recentsAnimationController2);
            }
            Utilities.postAsyncCallback(handler, __lambda_jo5sitwdnsscytyewikrw2tm4q);
            cleanUpRecentsAnimation();
        }
    }

    public void notifyRecentsAnimationState(RecentsAnimationCallbacks.RecentsAnimationListener listener) {
        if (isRecentsAnimationRunning()) {
            listener.onRecentsAnimationStart(this.mController, this.mTargets);
        }
    }

    public boolean isRecentsAnimationRunning() {
        return this.mController != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanUpRecentsAnimation() {
        RecentsAnimationController recentsAnimationController = this.mController;
        if (recentsAnimationController != null) {
            recentsAnimationController.cleanupScreenshot();
        }
        RecentsAnimationTargets recentsAnimationTargets = this.mTargets;
        if (recentsAnimationTargets != null) {
            recentsAnimationTargets.release();
        }
        RecentsAnimationCallbacks recentsAnimationCallbacks = this.mCallbacks;
        if (recentsAnimationCallbacks != null) {
            recentsAnimationCallbacks.removeAllListeners();
        }
        this.mController = null;
        this.mCallbacks = null;
        this.mTargets = null;
        this.mLastGestureState = null;
        this.mLastAppearedTaskTarget = null;
    }
}
