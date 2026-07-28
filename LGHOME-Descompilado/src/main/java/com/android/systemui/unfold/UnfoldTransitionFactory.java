package com.android.systemui.unfold;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.devicestate.DeviceStateManager;
import android.os.Handler;
import com.android.systemui.unfold.UnfoldSharedComponent;
import com.android.systemui.unfold.config.ResourceUnfoldTransitionConfig;
import com.android.systemui.unfold.config.UnfoldTransitionConfig;
import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import java.util.concurrent.Executor;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: UnfoldTransitionFactory.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000B\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001aV\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0015¨\u0006\u0016"}, d2 = {"createConfig", "Lcom/android/systemui/unfold/config/UnfoldTransitionConfig;", "context", "Landroid/content/Context;", "createUnfoldTransitionProgressProvider", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "config", "screenStatusProvider", "Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider;", "deviceStateManager", "Landroid/hardware/devicestate/DeviceStateManager;", "activityManager", "Landroid/app/ActivityManager;", "sensorManager", "Landroid/hardware/SensorManager;", "mainHandler", "Landroid/os/Handler;", "mainExecutor", "Ljava/util/concurrent/Executor;", "backgroundExecutor", "tracingTagPrefix", "", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 2, mv = {1, 6, 0}, xi = 48)
public final class UnfoldTransitionFactory {
    public static final UnfoldTransitionProgressProvider createUnfoldTransitionProgressProvider(Context context, UnfoldTransitionConfig config, ScreenStatusProvider screenStatusProvider, DeviceStateManager deviceStateManager, ActivityManager activityManager, SensorManager sensorManager, Handler mainHandler, Executor mainExecutor, Executor backgroundExecutor, String tracingTagPrefix) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(config, "config");
        Intrinsics.checkNotNullParameter(screenStatusProvider, "screenStatusProvider");
        Intrinsics.checkNotNullParameter(deviceStateManager, "deviceStateManager");
        Intrinsics.checkNotNullParameter(activityManager, "activityManager");
        Intrinsics.checkNotNullParameter(sensorManager, "sensorManager");
        Intrinsics.checkNotNullParameter(mainHandler, "mainHandler");
        Intrinsics.checkNotNullParameter(mainExecutor, "mainExecutor");
        Intrinsics.checkNotNullParameter(backgroundExecutor, "backgroundExecutor");
        Intrinsics.checkNotNullParameter(tracingTagPrefix, "tracingTagPrefix");
        UnfoldSharedComponent.Factory factory = DaggerUnfoldSharedComponent.factory();
        Intrinsics.checkNotNullExpressionValue(factory, "factory()");
        UnfoldTransitionProgressProvider unfoldTransitionProgressProviderOrElse = UnfoldSharedComponent.Factory.DefaultImpls.create$default(factory, context, config, screenStatusProvider, deviceStateManager, activityManager, sensorManager, mainHandler, mainExecutor, backgroundExecutor, tracingTagPrefix, null, 1024, null).getUnfoldTransitionProvider().orElse(null);
        if (unfoldTransitionProgressProviderOrElse != null) {
            return unfoldTransitionProgressProviderOrElse;
        }
        throw new IllegalStateException("Trying to create UnfoldTransitionProgressProvider when the transition is disabled");
    }

    public static final UnfoldTransitionConfig createConfig(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        return new ResourceUnfoldTransitionConfig(context);
    }
}
