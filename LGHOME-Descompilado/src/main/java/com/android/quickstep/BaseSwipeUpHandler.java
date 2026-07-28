package com.android.quickstep;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.VibratorWrapper;
import com.android.launcher3.util.WindowBounds;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.SwipeUpAnimationLogic;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.quickstep.util.SurfaceTransactionApplier;
import com.android.quickstep.util.TransformParams;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.display.DisplayManagerHelper;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseSwipeUpHandler<T extends StatefulActivity<?>, Q extends RecentsView> extends SwipeUpAnimationLogic implements RecentsAnimationCallbacks.RecentsAnimationListener {
    private static final String TAG = "BaseSwipeUpHandler";
    protected T mActivity;
    protected final ActivityInitListener mActivityInitListener;
    protected final BaseActivityInterface<?, T> mActivityInterface;
    protected boolean mCanceled;
    public int mDisplayId;
    protected Runnable mGestureEndCallback;
    protected final InputConsumerController mInputConsumer;
    protected RecentsAnimationController mRecentsAnimationController;
    private final ArrayList<Runnable> mRecentsAnimationStartCallbacks;
    protected RecentsAnimationTargets mRecentsAnimationTargets;
    protected Q mRecentsView;
    private boolean mRecentsViewScrollLinked;
    protected MultiStateCallback mStateCallback;

    public interface Factory {
        BaseSwipeUpHandler newHandler(GestureState gestureState, long touchTimeMs, boolean continuingLastGesture);
    }

    protected abstract InputConsumer createNewInputProxyHandler();

    public void finishToHome() {
    }

    public abstract Intent getLaunchIntent();

    protected abstract boolean handleTaskAppeared(RemoteAnimationTargetCompat[] appearedTaskTarget);

    public boolean isInQuickSwitchMode() {
        return false;
    }

    public boolean isRunningOver3PHome() {
        return false;
    }

    protected abstract boolean moveWindowWithRecentsScroll();

    public boolean needToStartHome() {
        return false;
    }

    public abstract void onConsumerAboutToBeSwitched();

    public abstract void onGestureCancelled();

    public abstract void onGestureEnded(float endVelocity, PointF velocity, PointF downPos);

    public void onGestureStarted(boolean isLikelyToStartNewTask) {
    }

    public abstract void onMotionPauseChanged(boolean isPaused);

    public void setIsLikelyToStartNewTask(boolean isLikelyToStartNewTask) {
    }

    @Override // com.android.quickstep.SwipeUpAnimationLogic
    public abstract void updateFinalShift();

    protected BaseSwipeUpHandler(Context context, RecentsAnimationDeviceState deviceState, GestureState gestureState, InputConsumerController inputConsumer) {
        super(context, deviceState, gestureState, new TransformParams());
        this.mRecentsAnimationStartCallbacks = new ArrayList<>();
        this.mRecentsViewScrollLinked = false;
        BaseActivityInterface<?, T> activityInterface = gestureState.getActivityInterface();
        this.mActivityInterface = activityInterface;
        this.mActivityInitListener = activityInterface.createActivityInitListener(new Predicate() { // from class: com.android.quickstep.-$$Lambda$dks1ED7Bg65wCyxpcrvDoVgD8RU
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f$0.onActivityInit((Boolean) obj);
            }
        });
        this.mInputConsumer = inputConsumer;
        this.mDisplayId = gestureState.getDisplayId();
        this.mAlphaOnEnd = context.getResources().getFloat(R.dimen.config_alpha_on_end);
    }

    protected void initAfterSubclassConstructor() {
        initTransitionEndpoints(this.mTaskViewSimulator.getOrientationState().getLauncherDeviceProfile());
    }

    protected void performHapticFeedback() {
        if (this.mActivity != null) {
            VibratorWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).vibrate(VibratorWrapper.OVERVIEW_HAPTIC, this.mActivity.getRootView());
        } else {
            VibratorWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).vibrate(VibratorWrapper.OVERVIEW_HAPTIC, this.mRecentsView);
        }
    }

    public Consumer<MotionEvent> getRecentsViewDispatcher(float navbarRotation) {
        Q q = this.mRecentsView;
        if (q != null) {
            return q.getEventDispatcher(navbarRotation);
        }
        return null;
    }

    public void setGestureEndCallback(Runnable gestureEndCallback) {
        this.mGestureEndCallback = gestureEndCallback;
    }

    protected void linkRecentsViewScroll() {
        SurfaceTransactionApplier.create(this.mRecentsView, new Consumer() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandler$z52qNPrBMdVdVVL1LidCtuQOL88
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$linkRecentsViewScroll$1$BaseSwipeUpHandler((SurfaceTransactionApplier) obj);
            }
        });
        this.mRecentsView.setOnScrollChangeListener(new View.OnScrollChangeListener() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandler$Cmy_3Jmjv3s2UIja9wL2fUtJI7c
            @Override // android.view.View.OnScrollChangeListener
            public final void onScrollChange(View view, int i, int i2, int i3, int i4) {
                this.f$0.lambda$linkRecentsViewScroll$2$BaseSwipeUpHandler(view, i, i2, i3, i4);
            }
        });
        runOnRecentsAnimationStart(new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandler$XY4Y5EZBJ7_jWPfzy_WtinyBDJQ
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$linkRecentsViewScroll$3$BaseSwipeUpHandler();
            }
        });
        this.mRecentsViewScrollLinked = true;
    }

    public /* synthetic */ void lambda$linkRecentsViewScroll$1$BaseSwipeUpHandler(final SurfaceTransactionApplier surfaceTransactionApplier) {
        this.mTransformParams.setSyncTransactionApplier(surfaceTransactionApplier);
        runOnRecentsAnimationStart(new Runnable() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandler$Dv5GnbIp1n-eaSjyCMcuBaaWG4I
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$linkRecentsViewScroll$0$BaseSwipeUpHandler(surfaceTransactionApplier);
            }
        });
    }

    public /* synthetic */ void lambda$linkRecentsViewScroll$0$BaseSwipeUpHandler(SurfaceTransactionApplier surfaceTransactionApplier) {
        this.mRecentsAnimationTargets.addReleaseCheck(surfaceTransactionApplier);
    }

    public /* synthetic */ void lambda$linkRecentsViewScroll$2$BaseSwipeUpHandler(View view, int i, int i2, int i3, int i4) {
        if (moveWindowWithRecentsScroll()) {
            updateFinalShift();
        }
    }

    public /* synthetic */ void lambda$linkRecentsViewScroll$3$BaseSwipeUpHandler() {
        this.mRecentsView.setRecentsAnimationTargets(this.mRecentsAnimationController, this.mRecentsAnimationTargets);
    }

    protected void startNewTask(final Consumer<Boolean> resultCallback) {
        int i;
        TaskView taskView;
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            return;
        }
        if (this.mRecentsView.getNextPageTaskView() != null) {
            i = this.mRecentsView.getNextPageTaskView().getTask().key.id;
        } else {
            LGLog.d(TAG, "Failed to next page task view");
            i = -1;
        }
        if (!this.mCanceled && i != -1 && (taskView = this.mRecentsView.getTaskView(i)) != null) {
            this.mGestureState.updateLastStartedTaskId(i);
            final boolean zContains = this.mGestureState.getPreviouslyAppearedTaskIds().contains(Integer.valueOf(i));
            taskView.launchTask(new Consumer() { // from class: com.android.quickstep.-$$Lambda$BaseSwipeUpHandler$_feihPI5jg_7_PnKJpc3WhAFDUs
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$startNewTask$4$BaseSwipeUpHandler(resultCallback, zContains, (Boolean) obj);
                }
            }, true);
        }
        this.mCanceled = false;
    }

    public /* synthetic */ void lambda$startNewTask$4$BaseSwipeUpHandler(Consumer consumer, boolean z, Boolean bool) {
        consumer.accept(bool);
        if (bool.booleanValue()) {
            if (z) {
                onRestartPreviouslyAppearedTask();
            }
            RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
            if (recentsAnimationController != null) {
                recentsAnimationController.finish(false, null);
            }
            T t = this.mActivity;
            if (t instanceof Launcher) {
                ((Launcher) t).getStateManager().goToState(LauncherState.NORMAL);
                return;
            }
            return;
        }
        this.mActivityInterface.onLaunchTaskFailed();
        this.mRecentsAnimationController.finish(true, null);
    }

    protected void onRestartPreviouslyAppearedTask() {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            recentsAnimationController.finish(false, null);
        }
    }

    protected void runOnRecentsAnimationStart(Runnable action) {
        if (this.mRecentsAnimationTargets == null) {
            this.mRecentsAnimationStartCallbacks.add(action);
        } else {
            action.run();
        }
    }

    protected boolean hasTargets() {
        RecentsAnimationTargets recentsAnimationTargets = this.mRecentsAnimationTargets;
        return recentsAnimationTargets != null && recentsAnimationTargets.hasTargets();
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationStart(RecentsAnimationController recentsAnimationController, RecentsAnimationTargets targets) {
        DeviceProfile deviceProfileCopy;
        this.mRecentsAnimationController = recentsAnimationController;
        this.mRecentsAnimationTargets = targets;
        this.mTransformParams.setTargetSet(this.mRecentsAnimationTargets);
        RemoteAnimationTargetCompat remoteAnimationTargetCompatFindTask = targets.findTask(this.mGestureState.getRunningTaskId());
        if (remoteAnimationTargetCompatFindTask != null) {
            this.mTaskViewSimulator.setPreview(remoteAnimationTargetCompatFindTask);
        }
        if (this.mActivity == null) {
            DeviceProfile launcherDeviceProfile = this.mTaskViewSimulator.getOrientationState().getLauncherDeviceProfile();
            if (targets.minimizedHomeBounds != null && remoteAnimationTargetCompatFindTask != null) {
                deviceProfileCopy = launcherDeviceProfile.getMultiWindowProfile(this.mContext, new WindowBounds(this.mActivityInterface.getOverviewWindowBounds(targets.minimizedHomeBounds, remoteAnimationTargetCompatFindTask), targets.homeContentInsets, DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).getInfo(0).rotation));
            } else {
                deviceProfileCopy = launcherDeviceProfile.copy(this.mContext);
            }
            deviceProfileCopy.updateInsets(targets.homeContentInsets);
            deviceProfileCopy.updateIsSeascape(this.mContext);
            initTransitionEndpoints(deviceProfileCopy);
        }
        if (this.mRecentsAnimationStartCallbacks.isEmpty()) {
            return;
        }
        Iterator it = new ArrayList(this.mRecentsAnimationStartCallbacks).iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
        this.mRecentsAnimationStartCallbacks.clear();
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> thumbnailDatas) {
        this.mRecentsAnimationController = null;
        this.mRecentsAnimationTargets = null;
        Q q = this.mRecentsView;
        if (q != null) {
            q.setRecentsAnimationTargets(null, null);
        }
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationFinished(RecentsAnimationController controller) {
        this.mRecentsAnimationController = null;
        this.mRecentsAnimationTargets = null;
        Q q = this.mRecentsView;
        if (q != null) {
            q.setRecentsAnimationTargets(null, null);
        }
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onTasksAppeared(RemoteAnimationTargetCompat[] appearedTaskTarget) {
        if (this.mRecentsAnimationController == null || !handleTaskAppeared(appearedTaskTarget)) {
            return;
        }
        this.mRecentsAnimationController.finish(false, null);
        this.mActivityInterface.onLaunchTaskSuccess();
        ActiveGestureLog.INSTANCE.addLog("finishRecentsAnimation", false);
    }

    protected int getLastAppearedTaskIndex() {
        if (this.mGestureState.getLastAppearedTaskId() != -1) {
            return this.mRecentsView.getTaskIndexForId(this.mGestureState.getLastAppearedTaskId());
        }
        return this.mRecentsView.getRunningTaskIndex();
    }

    protected boolean hasStartedNewTask() {
        return this.mGestureState.getLastStartedTaskId() != -1;
    }

    protected boolean onActivityInit(Boolean alreadyOnHome) {
        StatefulActivity createdActivity = this.mActivityInterface.getCreatedActivity();
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "BaseSwipeUpHandler.1");
        }
        if (createdActivity == null) {
            return true;
        }
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "BaseSwipeUpHandler.2");
        }
        initTransitionEndpoints(createdActivity.getDeviceProfile());
        return true;
    }

    public void initWhenReady(Intent intent) {
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).getTasks(null);
        this.mActivityInitListener.register(intent);
    }

    protected void applyWindowTransform() {
        if (this.mWindowTransitionController != null) {
            this.mWindowTransitionController.setPlayFraction(this.mCurrentShift.value / this.mDragLengthFactor);
        }
        if (this.mRecentsAnimationTargets != null) {
            if (this.mRecentsViewScrollLinked) {
                this.mTaskViewSimulator.setScroll(this.mRecentsView.getScrollOffset());
            }
            this.mTaskViewSimulator.apply(this.mTransformParams);
        }
    }

    @Override // com.android.quickstep.SwipeUpAnimationLogic
    protected RectFSpringAnim createWindowAnimationToHome(float startProgress, SwipeUpAnimationLogic.HomeAnimationFactory homeAnimationFactory) {
        RectFSpringAnim rectFSpringAnimCreateWindowAnimationToHome = super.createWindowAnimationToHome(startProgress, homeAnimationFactory);
        RecentsAnimationTargets recentsAnimationTargets = this.mRecentsAnimationTargets;
        if (recentsAnimationTargets != null) {
            recentsAnimationTargets.addReleaseCheck(rectFSpringAnimCreateWindowAnimationToHome);
        }
        return rectFSpringAnimCreateWindowAnimationToHome;
    }

    public void nodifiyStartRecents() {
        if (DisplayManagerHelper.isMultiDisplayDevice()) {
            WindowUtils.sendDualRecentsIntent(this.mContext, this.mDisplayId != 0);
        }
    }

    public boolean needToChangeConsumer(float navbarRotation) {
        Q q = this.mRecentsView;
        boolean zNeedToChangeConsumer = q != null ? q.needToChangeConsumer(navbarRotation) : false;
        if (zNeedToChangeConsumer) {
            LGLog.d(TAG, "needToChangeConsumer : " + zNeedToChangeConsumer + ", navbarRotation = " + navbarRotation + ", mRecentsView = " + this.mRecentsView);
        }
        return zNeedToChangeConsumer;
    }
}
