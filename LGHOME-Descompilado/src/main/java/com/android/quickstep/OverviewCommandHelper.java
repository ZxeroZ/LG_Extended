package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.SystemClock;
import android.view.ViewConfiguration;
import com.android.launcher3.ISecondaryDisplayLauncherCallback;
import com.android.launcher3.SecondaryDisplayLauncherManager;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.util.Executors;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.util.RemoteAnimationProvider;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class OverviewCommandHelper {
    private static final String TAG = "OverviewCommandHelper";
    private final Context mContext;
    private final RecentsAnimationDeviceState mDeviceState;
    private long mLastToggleTime;
    private final OverviewComponentObserver mOverviewComponentObserver;
    private final RecentsModel mRecentsModel;

    public OverviewCommandHelper(Context context, RecentsAnimationDeviceState deviceState, OverviewComponentObserver observer) {
        this.mContext = context;
        this.mDeviceState = deviceState;
        this.mRecentsModel = RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(context);
        this.mOverviewComponentObserver = observer;
    }

    public void onDesktopAppDrawerToggle(final int mode) {
        Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$OverviewCommandHelper$VjbwAGLtYmaU7DyU0r-bgP5THzU
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDesktopAppDrawerToggle$0$OverviewCommandHelper(mode);
            }
        });
    }

    public /* synthetic */ void lambda$onDesktopAppDrawerToggle$0$OverviewCommandHelper(int i) {
        ISecondaryDisplayLauncherCallback secondaryDisplayLauncherCallback = SecondaryDisplayLauncherManager.getInstance(this.mContext).getSecondaryDisplayLauncherCallback();
        LGLog.i(TAG, "onDesktopAppDrawerToggle - callback = " + secondaryDisplayLauncherCallback + ", mode = " + i);
        if (secondaryDisplayLauncherCallback != null) {
            secondaryDisplayLauncherCallback.executeAppDrawer(i);
        } else {
            LGLog.d("OverviewCommandHelper", "onDesktopAppDrawerToggle failed. callback is null");
        }
    }

    public void onOverviewToggle() {
        if (this.mDeviceState.isScreenPinningActive()) {
            return;
        }
        ActivityManagerWrapper.getInstance().closeSystemWindows(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
        Executors.MAIN_EXECUTOR.execute(new RecentsActivityCommand());
    }

    public void onOverviewShown(boolean triggeredFromAltTab) {
        if (triggeredFromAltTab) {
            ActivityManagerWrapper.getInstance().closeSystemWindows(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
        }
        Executors.MAIN_EXECUTOR.execute(new ShowRecentsCommand(triggeredFromAltTab));
    }

    public void onOverviewHidden() {
        Executors.MAIN_EXECUTOR.execute(new HideRecentsCommand());
    }

    public void onTip(final int actionType, final int viewType) {
        Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$OverviewCommandHelper$eWDDJlltLX_GZ97GRUcnavx0yNw
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onTip$1$OverviewCommandHelper(actionType, viewType);
            }
        });
    }

    public /* synthetic */ void lambda$onTip$1$OverviewCommandHelper(int i, int i2) {
        UserEventDispatcher.newInstance(this.mContext).logActionTip(i, i2);
    }

    private class ShowRecentsCommand extends RecentsActivityCommand {
        private final boolean mTriggeredFromAltTab;

        ShowRecentsCommand(boolean triggeredFromAltTab) {
            super();
            this.mTriggeredFromAltTab = triggeredFromAltTab;
        }

        @Override // com.android.quickstep.OverviewCommandHelper.RecentsActivityCommand
        protected boolean handleCommand(long elapsedTime) {
            return this.mActivityInterface.getVisibleRecentsView() != null;
        }

        @Override // com.android.quickstep.OverviewCommandHelper.RecentsActivityCommand
        protected void onTransitionComplete() {
            RecentsView visibleRecentsView;
            if (!this.mTriggeredFromAltTab || (visibleRecentsView = this.mActivityInterface.getVisibleRecentsView()) == null) {
                return;
            }
            TaskView nextTaskView = visibleRecentsView.getNextTaskView();
            if (nextTaskView == null) {
                if (visibleRecentsView.getTaskViewCount() > 0) {
                    visibleRecentsView.getTaskViewAt(0).requestFocus();
                    return;
                } else {
                    visibleRecentsView.requestFocus();
                    return;
                }
            }
            nextTaskView.requestFocus();
        }
    }

    private class HideRecentsCommand extends RecentsActivityCommand {
        private HideRecentsCommand() {
            super();
        }

        @Override // com.android.quickstep.OverviewCommandHelper.RecentsActivityCommand
        protected boolean handleCommand(long elapsedTime) {
            RecentsView visibleRecentsView = this.mActivityInterface.getVisibleRecentsView();
            if (visibleRecentsView == null) {
                return false;
            }
            int nextPage = visibleRecentsView.getNextPage();
            if (nextPage >= 0 && nextPage < visibleRecentsView.getTaskViewCount()) {
                ((TaskView) visibleRecentsView.getPageAt(nextPage)).launchTaskAnimated();
                return true;
            }
            visibleRecentsView.startHome();
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class RecentsActivityCommand<T extends StatefulActivity<?>> implements Runnable {
        protected final BaseActivityInterface<?, T> mActivityInterface;
        private final AppToOverviewAnimationProvider<T> mAnimationProvider;
        private final long mCreateTime;
        private final int mDisplayId;
        private ActivityInitListener mListener;
        private final long mToggleClickedTime;
        private boolean mUserEventLogged;

        protected void onTransitionComplete() {
        }

        public RecentsActivityCommand() {
            this.mToggleClickedTime = SystemClock.uptimeMillis();
            this.mDisplayId = 0;
            BaseActivityInterface<?, T> activityInterface = OverviewCommandHelper.this.mOverviewComponentObserver.getActivityInterface();
            this.mActivityInterface = activityInterface;
            this.mCreateTime = SystemClock.elapsedRealtime();
            this.mAnimationProvider = new AppToOverviewAnimationProvider<>(activityInterface, RecentsModel.getRunningTaskId(), OverviewCommandHelper.this.mDeviceState);
            OverviewCommandHelper.this.mRecentsModel.getTasks(null);
        }

        public RecentsActivityCommand(int displayId) {
            this.mToggleClickedTime = SystemClock.uptimeMillis();
            this.mDisplayId = displayId;
            BaseActivityInterface<?, T> activityInterface = OverviewCommandHelper.this.mOverviewComponentObserver.getActivityInterface(displayId);
            this.mActivityInterface = activityInterface;
            this.mCreateTime = SystemClock.elapsedRealtime();
            this.mAnimationProvider = new AppToOverviewAnimationProvider<>(activityInterface, RecentsModel.getRunningTaskId(), OverviewCommandHelper.this.mDeviceState);
            OverviewCommandHelper.this.mRecentsModel.getTasks(null);
            if (displayId != 0) {
                WindowUtils.sendDualRecentsIntent(OverviewCommandHelper.this.mContext, true);
            } else if (OverviewCommandHelper.this.mOverviewComponentObserver.isHomeAndOverviewSame()) {
                WindowUtils.sendDualRecentsIntent(OverviewCommandHelper.this.mContext, false);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            long j = this.mCreateTime - OverviewCommandHelper.this.mLastToggleTime;
            OverviewCommandHelper.this.mLastToggleTime = this.mCreateTime;
            if (handleCommand(j) || this.mActivityInterface.switchToRecentsIfVisible(new Runnable() { // from class: com.android.quickstep.-$$Lambda$yohogTfuHiwavnqAdn7rhmJYwiE
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.onTransitionComplete();
                }
            })) {
                return;
            }
            ActivityInitListener activityInitListenerCreateActivityInitListener = this.mActivityInterface.createActivityInitListener(new Predicate() { // from class: com.android.quickstep.-$$Lambda$OverviewCommandHelper$RecentsActivityCommand$A8cP0jKrv7aJTsCxl5am1xWtG7E
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return this.f$0.onActivityReady((Boolean) obj);
                }
            });
            this.mListener = activityInitListenerCreateActivityInitListener;
            activityInitListenerCreateActivityInitListener.registerAndStartActivity(OverviewCommandHelper.this.mOverviewComponentObserver.getOverviewIntent(this.mDisplayId), new RemoteAnimationProvider() { // from class: com.android.quickstep.OverviewCommandHelper.RecentsActivityCommand.1
                @Override // com.android.quickstep.util.RemoteAnimationProvider
                public AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets) {
                    return RecentsActivityCommand.this.createWindowAnimation(appTargets, wallpaperTargets);
                }
            }, OverviewCommandHelper.this.mContext, Executors.MAIN_EXECUTOR.getHandler(), this.mAnimationProvider.getRecentsLaunchDuration());
        }

        protected boolean handleCommand(long elapsedTime) {
            RecentsView visibleRecentsView = this.mActivityInterface.getVisibleRecentsView();
            if (visibleRecentsView == null) {
                return elapsedTime < ((long) ViewConfiguration.getDoubleTapTimeout());
            }
            visibleRecentsView.showNextTask();
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean onActivityReady(Boolean wasVisible) {
            StatefulActivity createdActivity = this.mActivityInterface.getCreatedActivity();
            if (!this.mUserEventLogged) {
                createdActivity.getUserEventDispatcher().logActionCommand(6, this.mActivityInterface.getContainerType(), 12);
                this.mUserEventLogged = true;
            }
            return this.mAnimationProvider.onActivityReady(createdActivity, wasVisible);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets) {
            this.mListener.unregister();
            AnimatorSet animatorSetCreateWindowAnimation = this.mAnimationProvider.createWindowAnimation(appTargets, wallpaperTargets);
            animatorSetCreateWindowAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.quickstep.OverviewCommandHelper.RecentsActivityCommand.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    RecentsActivityCommand.this.onTransitionComplete();
                }
            });
            return animatorSetCreateWindowAnimation;
        }
    }

    public void onOverviewToggle(int displayId) {
        if (this.mDeviceState.isScreenPinningActive(displayId)) {
            return;
        }
        ActivityManagerWrapper.getInstance().closeSystemWindows(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
        Executors.MAIN_EXECUTOR.execute(new RecentsActivityCommand(displayId));
    }
}
