package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import android.window.TransitionInfo;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.TaskViewUtils;
import com.android.quickstep.util.SurfaceTransactionApplier;
import com.android.quickstep.util.TaskViewSimulator;
import com.android.quickstep.util.TransformParams;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public final class TaskViewUtils {
    private static final String TAG = "TaskViewUtils";
    public static final int TYPE_DOCK_DIVIDER = 2034;

    private TaskViewUtils() {
    }

    public static TaskView findTaskViewToLaunch(RecentsView recentsView, View v, RemoteAnimationTargetCompat[] targets) {
        int i;
        TaskView taskView;
        if (v instanceof TaskView) {
            TaskView taskView2 = (TaskView) v;
            if (recentsView.isTaskViewVisible(taskView2)) {
                return taskView2;
            }
            return null;
        }
        int i2 = 0;
        if (v.getTag() instanceof ItemInfo) {
            ItemInfo itemInfo = (ItemInfo) v.getTag();
            ComponentName targetComponent = itemInfo.getTargetComponent();
            int identifier = itemInfo.user.getIdentifier();
            if (targetComponent != null) {
                for (int i3 = 0; i3 < recentsView.getTaskViewCount(); i3++) {
                    TaskView taskViewAt = recentsView.getTaskViewAt(i3);
                    if (recentsView.isTaskViewVisible(taskViewAt)) {
                        Task.TaskKey taskKey = taskViewAt.getTask().key;
                        if (targetComponent.equals(taskKey.getComponent()) && identifier == taskKey.userId) {
                            return taskViewAt;
                        }
                    }
                }
            }
        }
        if (targets == null) {
            return null;
        }
        int length = targets.length;
        while (true) {
            if (i2 >= length) {
                i = -1;
                break;
            }
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = targets[i2];
            if (remoteAnimationTargetCompat.mode == 0) {
                i = remoteAnimationTargetCompat.taskId;
                break;
            }
            i2++;
        }
        if (i == -1 || (taskView = recentsView.getTaskView(i)) == null || !recentsView.isTaskViewVisible(taskView)) {
            return null;
        }
        return taskView;
    }

    public static void createRecentsWindowAnimator(TaskView v, boolean skipViewChanges, RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets, DepthController depthController, PendingAnimation out) {
        SurfaceTransactionApplier surfaceTransactionApplier = new SurfaceTransactionApplier(v);
        final RemoteAnimationTargets remoteAnimationTargets = new RemoteAnimationTargets(appTargets, wallpaperTargets, new RemoteAnimationTargetCompat[0], 0);
        remoteAnimationTargets.addReleaseCheck(surfaceTransactionApplier);
        final TransformParams targetSet = new TransformParams().setSyncTransactionApplier(surfaceTransactionApplier).setTargetSet(remoteAnimationTargets);
        RecentsView recentsView = v.getRecentsView();
        if (recentsView == null) {
            LGLog.i(TAG, "RecentsView is null");
            return;
        }
        int iIndexOfChild = recentsView.indexOfChild(v);
        boolean z = iIndexOfChild != recentsView.getCurrentPage();
        int scrollOffset = recentsView.getScrollOffset(iIndexOfChild);
        Context context = v.getContext();
        DeviceProfile deviceProfile = BaseActivity.fromContext(context).getDeviceProfile();
        int i = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getInfo().rotation;
        final TaskViewSimulator taskViewSimulator = null;
        if (remoteAnimationTargets.apps.length > 0) {
            taskViewSimulator = new TaskViewSimulator(context, recentsView.getSizeStrategy());
            taskViewSimulator.setDp(deviceProfile);
            taskViewSimulator.setLayoutRotation(i, i);
            taskViewSimulator.setPreview(remoteAnimationTargets.apps[remoteAnimationTargets.apps.length - 1]);
            taskViewSimulator.fullScreenProgress.value = 0.0f;
            taskViewSimulator.recentsViewScale.value = 1.0f;
            taskViewSimulator.setScroll(scrollOffset);
            out.setFloat(taskViewSimulator.fullScreenProgress, AnimatedFloat.VALUE, 1.0f, Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            out.setFloat(taskViewSimulator.recentsViewScale, AnimatedFloat.VALUE, taskViewSimulator.getFullScreenScale(), Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            out.setInt(taskViewSimulator, TaskViewSimulator.SCROLL, 0, Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            out.addOnFrameCallback(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskViewUtils$46yREG8X8W0Hm5Et2SrRqkYQwis
                @Override // java.lang.Runnable
                public final void run() {
                    taskViewSimulator.apply(targetSet);
                }
            });
        }
        final TaskViewSimulator taskViewSimulator2 = taskViewSimulator;
        out.addFloat(targetSet, TransformParams.TARGET_ALPHA, 0.0f, 1.0f, Interpolators.clampToProgress(Interpolators.LINEAR, 0.0f, 0.2f));
        if (!skipViewChanges && z && taskViewSimulator2 != null) {
            out.addFloat(v, LauncherAnimUtils.VIEW_ALPHA, 1.0f, 0.0f, Interpolators.clampToProgress(Interpolators.LINEAR, 0.2f, 0.4f));
            taskViewSimulator2.apply(targetSet);
            final TaskThumbnailView thumbnail = v.getThumbnail();
            RectF rectF = new RectF(0.0f, 0.0f, thumbnail.getWidth(), thumbnail.getHeight());
            float[] fArr = {0.0f, 0.0f, thumbnail.getWidth(), thumbnail.getHeight()};
            Utilities.getDescendantCoordRelativeToAncestor((View) thumbnail, thumbnail.getRootView(), fArr, false);
            RectF rectF2 = new RectF(fArr[0], fArr[1], fArr[2], fArr[3]);
            final Matrix matrix = new Matrix();
            matrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.FILL);
            final Matrix matrix2 = new Matrix();
            matrix.invert(matrix2);
            final Matrix matrix3 = new Matrix();
            taskViewSimulator2.getCurrentMatrix().invert(matrix3);
            final Matrix matrix4 = new Matrix();
            out.addOnFrameCallback(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskViewUtils$SiiJZV6X1Ibxsmal6crlUayl6_4
                @Override // java.lang.Runnable
                public final void run() {
                    TaskViewUtils.lambda$createRecentsWindowAnimator$1(matrix4, matrix, matrix3, taskViewSimulator2, matrix2, thumbnail);
                }
            });
            out.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.TaskViewUtils.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    thumbnail.setAnimationMatrix(null);
                }
            });
        }
        out.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.TaskViewUtils.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                remoteAnimationTargets.release();
            }
        });
        if (depthController != null) {
            out.setFloat(depthController, DepthController.DEPTH, LauncherState.BACKGROUND_APP.getDepth(context), Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
        }
    }

    static /* synthetic */ void lambda$createRecentsWindowAnimator$1(Matrix matrix, Matrix matrix2, Matrix matrix3, TaskViewSimulator taskViewSimulator, Matrix matrix4, TaskThumbnailView taskThumbnailView) {
        matrix.set(matrix2);
        matrix.postConcat(matrix3);
        matrix.postConcat(taskViewSimulator.getCurrentMatrix());
        matrix.postConcat(matrix4);
        taskThumbnailView.setAnimationMatrix(matrix);
    }

    public static void composeRecentsSplitLaunchAnimator(int initialTaskId, PendingIntent initialTaskPendingIntent, int secondTaskId, TransitionInfo transitionInfo, SurfaceControl.Transaction t, Runnable finishCallback) {
        TransitionInfo.Change change = null;
        TransitionInfo.Change change2 = null;
        for (int i = 0; i < transitionInfo.getChanges().size(); i++) {
            TransitionInfo.Change change3 = (TransitionInfo.Change) transitionInfo.getChanges().get(i);
            int i2 = change3.getTaskInfo() != null ? change3.getTaskInfo().taskId : -1;
            change3.getMode();
            if ((i2 == initialTaskId || i2 == secondTaskId) && change3.getParent() == null) {
                throw new IllegalStateException("Initiating multi-split launch but the splitroot of " + i2 + " is already visible or has broken hierarchy.");
            }
            if (i2 == initialTaskId && initialTaskId != -1) {
                change = transitionInfo.getChange(change3.getParent());
            }
            if (i2 == secondTaskId) {
                change2 = transitionInfo.getChange(change3.getParent());
            }
        }
        animateSplitRoot(t, change);
        animateSplitRoot(t, change2);
        t.apply();
        finishCallback.run();
    }

    private static void animateSplitRoot(SurfaceControl.Transaction t, TransitionInfo.Change splitRoot) {
        if (splitRoot != null) {
            t.setVisibility(splitRoot.getLeash(), true);
            t.setAlpha(splitRoot.getLeash(), 1.0f);
        }
    }

    public static void composeRecentsSplitLaunchAnimatorLegacy(TaskView launchingTaskView, int initialTaskId, PendingIntent initialTaskPendingIntent, int secondTaskId, RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets, RemoteAnimationTargetCompat[] nonAppTargets, StateManager stateManager, DepthController depthController, final Runnable finishCallback) {
        if (launchingTaskView != null) {
            AnimatorSet animatorSet = new AnimatorSet();
            RecentsView recentsView = launchingTaskView.getRecentsView();
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.TaskViewUtils.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    finishCallback.run();
                }
            });
            composeRecentsLaunchAnimator(animatorSet, launchingTaskView, appTargets, wallpaperTargets, nonAppTargets, true, stateManager, recentsView, depthController);
            animatorSet.start();
            return;
        }
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : appTargets) {
            if (remoteAnimationTargetCompat.taskInfo != null) {
                int i = remoteAnimationTargetCompat.taskInfo.taskId;
            }
            int i2 = remoteAnimationTargetCompat.mode;
            SurfaceControl surfaceControl = remoteAnimationTargetCompat.leash;
            if (surfaceControl != null) {
                if (i2 == 0) {
                    arrayList.add(surfaceControl);
                } else if (i2 == 1) {
                    arrayList2.add(surfaceControl);
                }
            }
        }
        for (int i3 = 0; i3 < nonAppTargets.length; i3++) {
            SurfaceControl surfaceControl2 = nonAppTargets[i3].leash;
            if (nonAppTargets[i3].windowType == 2034 && surfaceControl2 != null) {
                arrayList.add(surfaceControl2);
            }
        }
        final SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(370L);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.quickstep.-$$Lambda$TaskViewUtils$l-FlJgJ_cBMCHKmF0ywvkjf9FWI
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TaskViewUtils.lambda$composeRecentsSplitLaunchAnimatorLegacy$2(arrayList, transaction, valueAnimator);
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.TaskViewUtils.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                for (SurfaceControl surfaceControl3 : arrayList) {
                    transaction.setVisibility(surfaceControl3, true).setAlpha(surfaceControl3, 0.0f);
                }
                transaction.apply();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    transaction.setVisibility((SurfaceControl) it.next(), false);
                }
                super.onAnimationEnd(animation);
                finishCallback.run();
            }
        });
        valueAnimatorOfFloat.start();
    }

    static /* synthetic */ void lambda$composeRecentsSplitLaunchAnimatorLegacy$2(ArrayList arrayList, SurfaceControl.Transaction transaction, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            transaction.setAlpha((SurfaceControl) it.next(), animatedFraction);
        }
        transaction.apply();
    }

    public static void composeRecentsLaunchAnimator(AnimatorSet anim, View v, RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets, RemoteAnimationTargetCompat[] nonAppTargets, boolean launcherClosing, StateManager stateManager, final RecentsView recentsView, DepthController depthController) {
        Animator duration;
        Animator.AnimatorListener anonymousClass7;
        AnimatorSet animatorSet;
        TaskView taskViewFindTaskViewToLaunch = findTaskViewToLaunch(recentsView, v, appTargets);
        final PendingAnimation pendingAnimation = new PendingAnimation(336L);
        createRecentsWindowAnimator(taskViewFindTaskViewToLaunch, !launcherClosing, appTargets, wallpaperTargets, depthController, pendingAnimation);
        if (launcherClosing) {
            createSplitAuxiliarySurfacesAnimator(nonAppTargets, true, new Consumer() { // from class: com.android.quickstep.-$$Lambda$TaskViewUtils$tZ8EKWpJ1-kGHTWut_H8AaH07lY
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TaskViewUtils.lambda$composeRecentsLaunchAnimator$3(pendingAnimation, (ValueAnimator) obj);
                }
            });
        }
        if (launcherClosing) {
            DeviceProfile deviceProfile = BaseActivity.fromContext(v.getContext()).getDeviceProfile();
            if (deviceProfile.isTablet) {
                duration = ObjectAnimator.ofFloat(recentsView, RecentsView.CONTENT_ALPHA, 0.0f);
            } else {
                duration = recentsView.createAdjacentPageAnimForTaskLaunch(taskViewFindTaskViewToLaunch);
            }
            if (deviceProfile.isTablet) {
                Log.d(TAG, "TVU composeRecentsLaunchAnimator alpha=0");
                duration.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.TaskViewUtils.5
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                        Log.d(TaskViewUtils.TAG, "TVU composeRecentsLaunchAnimator onStart");
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        Log.d(TaskViewUtils.TAG, "TVU composeRecentsLaunchAnimator onCancel, alpha=" + (recentsView == null ? -1.0f : ((Float) RecentsView.CONTENT_ALPHA.get(recentsView)).floatValue()));
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        Log.d(TaskViewUtils.TAG, "TVU composeRecentsLaunchAnimator onEnd");
                    }
                });
            }
            duration.setInterpolator(Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            duration.setDuration(336L);
            anonymousClass7 = new AnonymousClass6(recentsView, stateManager);
            animatorSet = null;
        } else {
            AnimatorPlaybackController animatorPlaybackControllerCreateAnimationToNewWorkspace = stateManager.createAnimationToNewWorkspace(LauncherState.NORMAL, 336L);
            animatorPlaybackControllerCreateAnimationToNewWorkspace.dispatchOnStart();
            AnimatorSet target = animatorPlaybackControllerCreateAnimationToNewWorkspace.getTarget();
            duration = animatorPlaybackControllerCreateAnimationToNewWorkspace.getAnimationPlayer().setDuration(336L);
            anonymousClass7 = new AnonymousClass7(recentsView, stateManager);
            animatorSet = target;
        }
        pendingAnimation.add(duration);
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && recentsView.getRunningTaskIndex() != -1) {
            pendingAnimation.addOnFrameCallback(null);
        }
        anim.play(pendingAnimation.buildAnim());
        stateManager.setCurrentAnimation(anim, animatorSet);
        anim.addListener(anonymousClass7);
    }

    static /* synthetic */ void lambda$composeRecentsLaunchAnimator$3(PendingAnimation pendingAnimation, ValueAnimator valueAnimator) {
        valueAnimator.setStartDelay(pendingAnimation.getDuration() - 100);
        pendingAnimation.add(valueAnimator);
    }

    /* JADX INFO: renamed from: com.android.quickstep.TaskViewUtils$6, reason: invalid class name */
    class AnonymousClass6 extends AnimatorListenerAdapter {
        final /* synthetic */ RecentsView val$recentsView;
        final /* synthetic */ StateManager val$stateManager;

        AnonymousClass6(final RecentsView val$recentsView, final StateManager val$stateManager) {
            this.val$recentsView = val$recentsView;
            this.val$stateManager = val$stateManager;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            final RecentsView recentsView = this.val$recentsView;
            final StateManager stateManager = this.val$stateManager;
            recentsView.finishRecentsAnimation(false, new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskViewUtils$6$8jnxcDpX7MGtbngJk2siqfwNUuU
                @Override // java.lang.Runnable
                public final void run() {
                    recentsView.post(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskViewUtils$6$6rFVcKo3kN-wZAxJtI-kkjgdRRs
                        @Override // java.lang.Runnable
                        public final void run() {
                            TaskViewUtils.AnonymousClass6.lambda$onAnimationEnd$0(stateManager);
                        }
                    });
                }
            });
        }

        static /* synthetic */ void lambda$onAnimationEnd$0(StateManager stateManager) {
            stateManager.moveToRestState();
            stateManager.reapplyState();
        }
    }

    /* JADX INFO: renamed from: com.android.quickstep.TaskViewUtils$7, reason: invalid class name */
    class AnonymousClass7 extends AnimatorListenerAdapter {
        final /* synthetic */ RecentsView val$recentsView;
        final /* synthetic */ StateManager val$stateManager;

        AnonymousClass7(final RecentsView val$recentsView, final StateManager val$stateManager) {
            this.val$recentsView = val$recentsView;
            this.val$stateManager = val$stateManager;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            RecentsView recentsView = this.val$recentsView;
            final StateManager stateManager = this.val$stateManager;
            recentsView.finishRecentsAnimation(false, new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskViewUtils$7$gNR-NTb6qk6Y1e2iCq0fYFdEMoc
                @Override // java.lang.Runnable
                public final void run() {
                    stateManager.goToState(LauncherState.NORMAL, false);
                }
            });
        }
    }

    public static ValueAnimator createSplitAuxiliarySurfacesAnimator(RemoteAnimationTargetCompat[] nonApps, final boolean shown, Consumer<ValueAnimator> animatorHandler) {
        if (nonApps == null || nonApps.length == 0) {
            return null;
        }
        final SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        final ArrayList arrayList = new ArrayList(nonApps.length);
        boolean z = false;
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : nonApps) {
            SurfaceControl surfaceControl = remoteAnimationTargetCompat.leash;
            if (remoteAnimationTargetCompat.windowType == 2034 && surfaceControl != null) {
                arrayList.add(surfaceControl);
                z = true;
            }
        }
        if (!z) {
            return null;
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.quickstep.-$$Lambda$TaskViewUtils$JP0VoHyyG_K7bxlBsuCYrFlUK_o
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TaskViewUtils.lambda$createSplitAuxiliarySurfacesAnimator$4(arrayList, transaction, shown, valueAnimator);
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.TaskViewUtils.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (shown) {
                    for (SurfaceControl surfaceControl2 : arrayList) {
                        transaction.setAlpha(surfaceControl2, 0.0f);
                        transaction.setVisibility(surfaceControl2, true);
                    }
                    transaction.apply();
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!shown) {
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        transaction.setVisibility((SurfaceControl) it.next(), false);
                    }
                    transaction.apply();
                }
                transaction.close();
            }
        });
        valueAnimatorOfFloat.setDuration(100L);
        animatorHandler.accept(valueAnimatorOfFloat);
        return valueAnimatorOfFloat;
    }

    static /* synthetic */ void lambda$createSplitAuxiliarySurfacesAnimator$4(List list, SurfaceControl.Transaction transaction, boolean z, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            transaction.setAlpha((SurfaceControl) it.next(), z ? animatedFraction : 1.0f - animatedFraction);
        }
        transaction.apply();
    }
}
