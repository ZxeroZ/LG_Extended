package com.android.systemui.unfold.updates;

import android.R;
import android.app.ActivityManager;
import android.content.Context;
import android.hardware.devicestate.DeviceStateManager;
import android.os.Handler;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.unfold.updates.FoldStateProvider;
import com.android.systemui.unfold.updates.hinge.HingeAngleProvider;
import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.inject.Inject;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: DeviceFoldStateProvider.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0011\u0018\u00002\u00020\u0001:\u000489:;BC\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\b\u0001\u0010\f\u001a\u00020\r\u0012\b\b\u0001\u0010\u000e\u001a\u00020\u000f¢\u0006\u0002\u0010\u0010J\u0010\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020%H\u0016J\b\u0010-\u001a\u00020+H\u0002J\u000f\u0010.\u001a\u0004\u0018\u00010\u0014H\u0002¢\u0006\u0002\u0010/J\u0010\u00100\u001a\u00020+2\u0006\u00101\u001a\u00020\u0014H\u0002J\u0010\u00102\u001a\u00020+2\u0006\u00103\u001a\u00020\"H\u0002J\u0010\u00104\u001a\u00020+2\u0006\u0010,\u001a\u00020%H\u0016J\b\u00105\u001a\u00020+H\u0002J\b\u00106\u001a\u00020+H\u0016J\b\u00107\u001a\u00020+H\u0016R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0011\u001a\u00060\u0012R\u00020\u0000X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0015\u001a\u00060\u0016R\u00020\u0000X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\u00020\u00188VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0019R\u000e\u0010\u001a\u001a\u00020\u0018X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001b\u001a\u00020\u00188BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u0019R\u000e\u0010\u001c\u001a\u00020\u0018X\u0082\u000e¢\u0006\u0002\n\u0000R\u0018\u0010\u001d\u001a\u0004\u0018\u00010\u0014X\u0082\u000e¢\u0006\n\n\u0002\u0010 \u0012\u0004\b\u001e\u0010\u001fR\u000e\u0010!\u001a\u00020\"X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010#\u001a\b\u0012\u0004\u0012\u00020%0$X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010&\u001a\u00060'R\u00020\u0000X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010(\u001a\u00060)R\u00020\u0000X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006<"}, d2 = {"Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;", "Lcom/android/systemui/unfold/updates/FoldStateProvider;", "context", "Landroid/content/Context;", "hingeAngleProvider", "Lcom/android/systemui/unfold/updates/hinge/HingeAngleProvider;", "screenStatusProvider", "Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider;", "deviceStateManager", "Landroid/hardware/devicestate/DeviceStateManager;", "activityManager", "Landroid/app/ActivityManager;", "mainExecutor", "Ljava/util/concurrent/Executor;", "handler", "Landroid/os/Handler;", "(Landroid/content/Context;Lcom/android/systemui/unfold/updates/hinge/HingeAngleProvider;Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider;Landroid/hardware/devicestate/DeviceStateManager;Landroid/app/ActivityManager;Ljava/util/concurrent/Executor;Landroid/os/Handler;)V", "foldStateListener", "Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$FoldStateListener;", "halfOpenedTimeoutMillis", "", "hingeAngleListener", "Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$HingeAngleListener;", "isFinishedOpening", "", "()Z", "isFolded", "isTransitionInProgress", "isUnfoldHandled", "lastFoldUpdate", "getLastFoldUpdate$annotations", "()V", "Ljava/lang/Integer;", "lastHingeAngle", "", "outputListeners", "", "Lcom/android/systemui/unfold/updates/FoldStateProvider$FoldUpdatesListener;", "screenListener", "Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$ScreenStatusListener;", "timeoutRunnable", "Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$TimeoutRunnable;", "addCallback", "", "listener", "cancelTimeout", "getClosingThreshold", "()Ljava/lang/Integer;", "notifyFoldUpdate", "update", "onHingeAngle", "angle", "removeCallback", "rescheduleAbortAnimationTimeout", "start", "stop", "FoldStateListener", "HingeAngleListener", "ScreenStatusListener", "TimeoutRunnable", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class DeviceFoldStateProvider implements FoldStateProvider {
    private final ActivityManager activityManager;
    private final DeviceStateManager deviceStateManager;
    private final FoldStateListener foldStateListener;
    private final int halfOpenedTimeoutMillis;
    private final Handler handler;
    private final HingeAngleListener hingeAngleListener;
    private final HingeAngleProvider hingeAngleProvider;
    private boolean isFolded;
    private boolean isUnfoldHandled;
    private Integer lastFoldUpdate;
    private float lastHingeAngle;
    private final Executor mainExecutor;
    private final List<FoldStateProvider.FoldUpdatesListener> outputListeners;
    private final ScreenStatusListener screenListener;
    private final ScreenStatusProvider screenStatusProvider;
    private final TimeoutRunnable timeoutRunnable;

    private static /* synthetic */ void getLastFoldUpdate$annotations() {
    }

    @Inject
    public DeviceFoldStateProvider(Context context, HingeAngleProvider hingeAngleProvider, ScreenStatusProvider screenStatusProvider, DeviceStateManager deviceStateManager, ActivityManager activityManager, @Main Executor mainExecutor, @Main Handler handler) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(hingeAngleProvider, "hingeAngleProvider");
        Intrinsics.checkNotNullParameter(screenStatusProvider, "screenStatusProvider");
        Intrinsics.checkNotNullParameter(deviceStateManager, "deviceStateManager");
        Intrinsics.checkNotNullParameter(activityManager, "activityManager");
        Intrinsics.checkNotNullParameter(mainExecutor, "mainExecutor");
        Intrinsics.checkNotNullParameter(handler, "handler");
        this.hingeAngleProvider = hingeAngleProvider;
        this.screenStatusProvider = screenStatusProvider;
        this.deviceStateManager = deviceStateManager;
        this.activityManager = activityManager;
        this.mainExecutor = mainExecutor;
        this.handler = handler;
        this.outputListeners = new ArrayList();
        this.hingeAngleListener = new HingeAngleListener(this);
        this.screenListener = new ScreenStatusListener(this);
        this.foldStateListener = new FoldStateListener(this, context);
        this.timeoutRunnable = new TimeoutRunnable(this);
        this.halfOpenedTimeoutMillis = context.getResources().getInteger(R.integer.config_notificationWarnRemoteViewSizeBytes);
        this.isUnfoldHandled = true;
    }

    @Override // com.android.systemui.unfold.updates.FoldStateProvider
    public void start() {
        this.deviceStateManager.registerCallback(this.mainExecutor, this.foldStateListener);
        this.screenStatusProvider.addCallback(this.screenListener);
        this.hingeAngleProvider.addCallback(this.hingeAngleListener);
    }

    @Override // com.android.systemui.unfold.updates.FoldStateProvider
    public void stop() {
        this.screenStatusProvider.removeCallback(this.screenListener);
        this.deviceStateManager.unregisterCallback(this.foldStateListener);
        this.hingeAngleProvider.removeCallback(this.hingeAngleListener);
        this.hingeAngleProvider.stop();
    }

    /* JADX DEBUG: Method merged with bridge method: addCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(FoldStateProvider.FoldUpdatesListener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.outputListeners.add(listener);
    }

    /* JADX DEBUG: Method merged with bridge method: removeCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(FoldStateProvider.FoldUpdatesListener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.outputListeners.remove(listener);
    }

    @Override // com.android.systemui.unfold.updates.FoldStateProvider
    public boolean isFinishedOpening() {
        Integer num;
        Integer num2;
        return !this.isFolded && (((num = this.lastFoldUpdate) != null && num.intValue() == 4) || ((num2 = this.lastFoldUpdate) != null && num2.intValue() == 3));
    }

    private final boolean isTransitionInProgress() {
        Integer num = this.lastFoldUpdate;
        if (num != null && num.intValue() == 0) {
            return true;
        }
        Integer num2 = this.lastFoldUpdate;
        return num2 != null && num2.intValue() == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onHingeAngle(float angle) {
        boolean z = false;
        boolean z2 = angle < this.lastHingeAngle;
        Integer closingThreshold = getClosingThreshold();
        boolean z3 = closingThreshold == null || angle < ((float) closingThreshold.intValue());
        boolean z4 = 180.0f - angle < 15.0f;
        Integer num = this.lastFoldUpdate;
        if (num != null && num.intValue() == 1) {
            z = true;
        }
        if (z2 && z3 && !z && !z4) {
            notifyFoldUpdate(1);
        }
        if (isTransitionInProgress()) {
            if (z4) {
                notifyFoldUpdate(4);
                cancelTimeout();
            } else {
                rescheduleAbortAnimationTimeout();
            }
        }
        this.lastHingeAngle = angle;
        Iterator<T> it = this.outputListeners.iterator();
        while (it.hasNext()) {
            ((FoldStateProvider.FoldUpdatesListener) it.next()).onHingeAngleUpdate(angle);
        }
    }

    private final Integer getClosingThreshold() {
        ActivityManager.RunningTaskInfo runningTaskInfo;
        List<ActivityManager.RunningTaskInfo> runningTasks = this.activityManager.getRunningTasks(1);
        Integer numValueOf = (runningTasks == null || (runningTaskInfo = (ActivityManager.RunningTaskInfo) CollectionsKt.getOrNull(runningTasks, 0)) == null) ? null : Integer.valueOf(runningTaskInfo.topActivityType);
        if (numValueOf == null) {
            return null;
        }
        if (numValueOf.intValue() == 2) {
            return (Integer) null;
        }
        return 60;
    }

    /* JADX INFO: compiled from: DeviceFoldStateProvider.kt */
    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$FoldStateListener;", "Landroid/hardware/devicestate/DeviceStateManager$FoldStateListener;", "context", "Landroid/content/Context;", "(Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;Landroid/content/Context;)V", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    private final class FoldStateListener extends DeviceStateManager.FoldStateListener {
        final /* synthetic */ DeviceFoldStateProvider this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FoldStateListener(final DeviceFoldStateProvider this$0, Context context) {
            super(context, new Consumer() { // from class: com.android.systemui.unfold.updates.DeviceFoldStateProvider.FoldStateListener.1
                @Override // java.util.function.Consumer
                public /* bridge */ /* synthetic */ void accept(Object obj) {
                    accept(((Boolean) obj).booleanValue());
                }

                public final void accept(boolean z) {
                    this$0.isFolded = z;
                    this$0.lastHingeAngle = 0.0f;
                    if (z) {
                        this$0.hingeAngleProvider.stop();
                        this$0.notifyFoldUpdate(5);
                        this$0.cancelTimeout();
                        this$0.isUnfoldHandled = false;
                        return;
                    }
                    this$0.notifyFoldUpdate(0);
                    this$0.rescheduleAbortAnimationTimeout();
                    this$0.hingeAngleProvider.start();
                }
            });
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(context, "context");
            this.this$0 = this$0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void notifyFoldUpdate(int update) {
        Iterator<T> it = this.outputListeners.iterator();
        while (it.hasNext()) {
            ((FoldStateProvider.FoldUpdatesListener) it.next()).onFoldUpdate(update);
        }
        this.lastFoldUpdate = Integer.valueOf(update);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void rescheduleAbortAnimationTimeout() {
        if (isTransitionInProgress()) {
            cancelTimeout();
        }
        this.handler.postDelayed(this.timeoutRunnable, this.halfOpenedTimeoutMillis);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void cancelTimeout() {
        this.handler.removeCallbacks(this.timeoutRunnable);
    }

    /* JADX INFO: compiled from: DeviceFoldStateProvider.kt */
    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$ScreenStatusListener;", "Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider$ScreenListener;", "(Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;)V", "onScreenTurnedOn", "", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    private final class ScreenStatusListener implements ScreenStatusProvider.ScreenListener {
        final /* synthetic */ DeviceFoldStateProvider this$0;

        /* JADX DEBUG: Incorrect args count in method signature: ()V */
        public ScreenStatusListener(DeviceFoldStateProvider this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            this.this$0 = this$0;
        }

        @Override // com.android.systemui.unfold.updates.screen.ScreenStatusProvider.ScreenListener
        public void onScreenTurnedOn() {
            if (this.this$0.isFolded || this.this$0.isUnfoldHandled) {
                return;
            }
            Iterator it = this.this$0.outputListeners.iterator();
            while (it.hasNext()) {
                ((FoldStateProvider.FoldUpdatesListener) it.next()).onFoldUpdate(2);
            }
            this.this$0.isUnfoldHandled = true;
        }
    }

    /* JADX INFO: compiled from: DeviceFoldStateProvider.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0002H\u0016¨\u0006\u0007"}, d2 = {"Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$HingeAngleListener;", "Landroidx/core/util/Consumer;", "", "(Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;)V", "accept", "", "angle", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    private final class HingeAngleListener implements androidx.core.util.Consumer<Float> {
        final /* synthetic */ DeviceFoldStateProvider this$0;

        /* JADX DEBUG: Incorrect args count in method signature: ()V */
        public HingeAngleListener(DeviceFoldStateProvider this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            this.this$0 = this$0;
        }

        /* JADX DEBUG: Method arguments types fixed to match base method, original types: [java.lang.Object] */
        @Override // androidx.core.util.Consumer
        public /* bridge */ /* synthetic */ void accept(Float f) {
            accept(f.floatValue());
        }

        public void accept(float angle) {
            this.this$0.onHingeAngle(angle);
        }
    }

    /* JADX INFO: compiled from: DeviceFoldStateProvider.kt */
    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$TimeoutRunnable;", "Ljava/lang/Runnable;", "(Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;)V", "run", "", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    private final class TimeoutRunnable implements Runnable {
        final /* synthetic */ DeviceFoldStateProvider this$0;

        /* JADX DEBUG: Incorrect args count in method signature: ()V */
        public TimeoutRunnable(DeviceFoldStateProvider this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            this.this$0 = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.notifyFoldUpdate(3);
        }
    }
}
