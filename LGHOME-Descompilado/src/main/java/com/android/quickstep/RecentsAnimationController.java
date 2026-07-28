package com.android.quickstep;

import android.os.SystemClock;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.Preconditions;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.RecentsAnimationControllerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.launcher3.util.LGLog;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class RecentsAnimationController {
    private static final String TAG = "RecentsAnimationController";
    private final boolean mAllowMinimizeSplitScreen;
    private final RecentsAnimationControllerCompat mController;
    private boolean mDisableInputProxyPending;
    private InputConsumer mInputConsumer;
    private InputConsumerController mInputConsumerController;
    private Supplier<InputConsumer> mInputProxySupplier;
    private final Consumer<RecentsAnimationController> mOnFinishedListener;
    private boolean mTouchInProgress;
    private boolean mUseLauncherSysBarFlags = false;
    private boolean mSplitScreenMinimized = false;

    public RecentsAnimationController(RecentsAnimationControllerCompat controller, boolean allowMinimizeSplitScreen, Consumer<RecentsAnimationController> onFinishedListener) {
        this.mController = controller;
        this.mOnFinishedListener = onFinishedListener;
        this.mAllowMinimizeSplitScreen = allowMinimizeSplitScreen;
    }

    public ThumbnailData screenshotTask(int taskId) {
        return this.mController.screenshotTask(taskId);
    }

    public void setUseLauncherSystemBarFlags(final boolean useLauncherSysBarFlags) {
        if (this.mUseLauncherSysBarFlags != useLauncherSysBarFlags) {
            this.mUseLauncherSysBarFlags = useLauncherSysBarFlags;
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationController$jrBZHIc5h0nUJzfWTRwvUBSnVD0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setUseLauncherSystemBarFlags$0$RecentsAnimationController(useLauncherSysBarFlags);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setUseLauncherSystemBarFlags$0$RecentsAnimationController(boolean z) {
        this.mController.setAnimationTargetsBehindSystemBars(!z);
    }

    public void setSplitScreenMinimized(final boolean splitScreenMinimized) {
        if (this.mAllowMinimizeSplitScreen && this.mSplitScreenMinimized != splitScreenMinimized) {
            this.mSplitScreenMinimized = splitScreenMinimized;
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationController$RPaSsdv9Lo29148Fl6jeEzVPkm0
                @Override // java.lang.Runnable
                public final void run() {
                    RecentsAnimationController.lambda$setSplitScreenMinimized$1(splitScreenMinimized);
                }
            });
        }
    }

    static /* synthetic */ void lambda$setSplitScreenMinimized$1(boolean z) {
        SystemUiProxy noCreate = SystemUiProxy.INSTANCE.getNoCreate();
        if (noCreate != null) {
            noCreate.setSplitScreenMinimized(z);
        }
    }

    public void setDeferCancelUntilNextTransition(boolean defer, boolean screenshot) {
        this.mController.setDeferCancelUntilNextTransition(defer, screenshot);
    }

    public void cleanupScreenshot() {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationController$wdrnkuBS_DTdVxUI9eT__TsCD8s
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$cleanupScreenshot$2$RecentsAnimationController();
            }
        });
    }

    public /* synthetic */ void lambda$cleanupScreenshot$2$RecentsAnimationController() {
        this.mController.cleanupScreenshot();
    }

    public boolean removeTaskTarget(RemoteAnimationTargetCompat target) {
        return this.mController.removeTask(target.taskId);
    }

    public void finishAnimationToHome() {
        finishAndDisableInputProxy(true, null, false);
    }

    public void finishAnimationToApp() {
        finishAndDisableInputProxy(false, null, false);
    }

    public void finish(boolean toRecents, Runnable onFinishComplete) {
        finish(toRecents, onFinishComplete, false);
    }

    public void finish(boolean toRecents, Runnable onFinishComplete, boolean sendUserLeaveHint) {
        Preconditions.assertUIThread();
        if (toRecents && this.mTouchInProgress) {
            this.mDisableInputProxyPending = true;
            finishController(toRecents, onFinishComplete, sendUserLeaveHint);
        } else {
            finishAndDisableInputProxy(toRecents, onFinishComplete, sendUserLeaveHint);
        }
    }

    private void finishAndDisableInputProxy(boolean toRecents, Runnable onFinishComplete, boolean sendUserLeaveHint) {
        disableInputProxy();
        finishController(toRecents, onFinishComplete, sendUserLeaveHint);
    }

    public void finishController(final boolean toRecents, final Runnable callback, final boolean sendUserLeaveHint) {
        LGLog.d(TAG, "[RecentsAnimation] call finishController. " + toRecents);
        this.mOnFinishedListener.accept(this);
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationController$xX-Xk2FTZ76YyEYKwMCp5qPiKTI
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$finishController$3$RecentsAnimationController(toRecents, sendUserLeaveHint, callback);
            }
        });
    }

    public /* synthetic */ void lambda$finishController$3$RecentsAnimationController(boolean z, boolean z2, Runnable runnable) {
        this.mController.setInputConsumerEnabled(false);
        this.mController.finish(z, z2);
        LGLog.d(TAG, "[RecentsAnimation] finishController in UIThread. " + z);
        if (runnable != null) {
            Executors.MAIN_EXECUTOR.execute(runnable);
        }
    }

    public void enableInputConsumer() {
        Executors.UI_HELPER_EXECUTOR.submit(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationController$45mR4Pboao0wAKuZs5CeulVgdkc
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$enableInputConsumer$4$RecentsAnimationController();
            }
        });
    }

    public /* synthetic */ void lambda$enableInputConsumer$4$RecentsAnimationController() {
        this.mController.hideCurrentInputMethod();
        this.mController.setInputConsumerEnabled(true);
    }

    public void enableInputProxy(InputConsumerController inputConsumerController, Supplier<InputConsumer> inputProxySupplier) {
        this.mInputProxySupplier = inputProxySupplier;
        this.mInputConsumerController = inputConsumerController;
        inputConsumerController.setInputListener(new InputConsumerController.InputListener() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationController$5kTLgYWmWhXsaifybK6cNVaIvqM
            @Override // com.android.systemui.shared.system.InputConsumerController.InputListener
            public final boolean onInputEvent(InputEvent inputEvent) {
                return this.f$0.onInputConsumerEvent(inputEvent);
            }
        });
    }

    public RecentsAnimationControllerCompat getController() {
        return this.mController;
    }

    private void disableInputProxy() {
        if (this.mInputConsumer != null && this.mTouchInProgress) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            MotionEvent motionEventObtain = MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 3, 0.0f, 0.0f, 0);
            this.mInputConsumer.onMotionEvent(motionEventObtain);
            motionEventObtain.recycle();
        }
        InputConsumerController inputConsumerController = this.mInputConsumerController;
        if (inputConsumerController != null) {
            inputConsumerController.setInputListener(null);
        }
        this.mInputProxySupplier = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onInputConsumerEvent(InputEvent ev) {
        if (ev instanceof MotionEvent) {
            onInputConsumerMotionEvent((MotionEvent) ev);
            return false;
        }
        if (!(ev instanceof KeyEvent)) {
            return false;
        }
        if (this.mInputConsumer == null) {
            this.mInputConsumer = this.mInputProxySupplier.get();
        }
        this.mInputConsumer.onKeyEvent((KeyEvent) ev);
        return true;
    }

    private boolean onInputConsumerMotionEvent(MotionEvent ev) {
        int action = ev.getAction();
        boolean z = this.mTouchInProgress;
        if (!z && action != 0) {
            Log.w(TAG, "Received non-down motion before down motion: " + action);
            return false;
        }
        if (z && action == 0) {
            Log.w(TAG, "Received down motion while touch was already in progress");
            return false;
        }
        if (action == 0) {
            this.mTouchInProgress = true;
            if (this.mInputConsumer == null) {
                this.mInputConsumer = this.mInputProxySupplier.get();
            }
        } else if (action == 3 || action == 1) {
            this.mTouchInProgress = false;
            if (this.mDisableInputProxyPending) {
                this.mDisableInputProxyPending = false;
                disableInputProxy();
            }
        }
        InputConsumer inputConsumer = this.mInputConsumer;
        if (inputConsumer != null) {
            inputConsumer.onMotionEvent(ev);
        }
        return true;
    }
}
