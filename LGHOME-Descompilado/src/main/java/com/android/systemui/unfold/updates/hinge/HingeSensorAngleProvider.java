package com.android.systemui.unfold.updates.hinge;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Trace;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Consumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: HingeSensorAngleProvider.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001:\u0001\u0013B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0016\u0010\r\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0016J\u0016\u0010\u0010\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0016J\b\u0010\u0011\u001a\u00020\u000eH\u0016J\b\u0010\u0012\u001a\u00020\u000eH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u000b\u001a\u00060\fR\u00020\u0000X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Lcom/android/systemui/unfold/updates/hinge/HingeSensorAngleProvider;", "Lcom/android/systemui/unfold/updates/hinge/HingeAngleProvider;", "sensorManager", "Landroid/hardware/SensorManager;", "executor", "Ljava/util/concurrent/Executor;", "(Landroid/hardware/SensorManager;Ljava/util/concurrent/Executor;)V", "listeners", "", "Landroidx/core/util/Consumer;", "", "sensorListener", "Lcom/android/systemui/unfold/updates/hinge/HingeSensorAngleProvider$HingeAngleSensorListener;", "addCallback", "", "listener", "removeCallback", "start", "stop", "HingeAngleSensorListener", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class HingeSensorAngleProvider implements HingeAngleProvider {
    private final Executor executor;
    private final List<Consumer<Float>> listeners;
    private final HingeAngleSensorListener sensorListener;
    private final SensorManager sensorManager;

    public HingeSensorAngleProvider(SensorManager sensorManager, Executor executor) {
        Intrinsics.checkNotNullParameter(sensorManager, "sensorManager");
        Intrinsics.checkNotNullParameter(executor, "executor");
        this.sensorManager = sensorManager;
        this.executor = executor;
        this.sensorListener = new HingeAngleSensorListener(this);
        this.listeners = new ArrayList();
    }

    @Override // com.android.systemui.unfold.updates.hinge.HingeAngleProvider
    public void start() {
        this.executor.execute(new Runnable() { // from class: com.android.systemui.unfold.updates.hinge.HingeSensorAngleProvider.start.1
            @Override // java.lang.Runnable
            public final void run() {
                Trace.beginSection("HingeSensorAngleProvider#start");
                HingeSensorAngleProvider.this.sensorManager.registerListener(HingeSensorAngleProvider.this.sensorListener, HingeSensorAngleProvider.this.sensorManager.getDefaultSensor(36), 0);
                Trace.endSection();
            }
        });
    }

    @Override // com.android.systemui.unfold.updates.hinge.HingeAngleProvider
    public void stop() {
        this.executor.execute(new Runnable() { // from class: com.android.systemui.unfold.updates.hinge.HingeSensorAngleProvider.stop.1
            @Override // java.lang.Runnable
            public final void run() {
                HingeSensorAngleProvider.this.sensorManager.unregisterListener(HingeSensorAngleProvider.this.sensorListener);
            }
        });
    }

    /* JADX DEBUG: Method merged with bridge method: removeCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(Consumer<Float> listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.listeners.remove(listener);
    }

    /* JADX DEBUG: Method merged with bridge method: addCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(Consumer<Float> listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.listeners.add(listener);
    }

    /* JADX INFO: compiled from: HingeSensorAngleProvider.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000bH\u0016¨\u0006\f"}, d2 = {"Lcom/android/systemui/unfold/updates/hinge/HingeSensorAngleProvider$HingeAngleSensorListener;", "Landroid/hardware/SensorEventListener;", "(Lcom/android/systemui/unfold/updates/hinge/HingeSensorAngleProvider;)V", "onAccuracyChanged", "", "sensor", "Landroid/hardware/Sensor;", "accuracy", "", "onSensorChanged", NotificationCompat.CATEGORY_EVENT, "Landroid/hardware/SensorEvent;", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    private final class HingeAngleSensorListener implements SensorEventListener {
        final /* synthetic */ HingeSensorAngleProvider this$0;

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        /* JADX DEBUG: Incorrect args count in method signature: ()V */
        public HingeAngleSensorListener(HingeSensorAngleProvider this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            this.this$0 = this$0;
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent event) {
            Intrinsics.checkNotNullParameter(event, "event");
            Iterator it = this.this$0.listeners.iterator();
            while (it.hasNext()) {
                ((Consumer) it.next()).accept(Float.valueOf(event.values[0]));
            }
        }
    }
}
