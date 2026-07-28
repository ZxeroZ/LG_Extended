package com.android.systemui.unfold;

import android.hardware.SensorManager;
import com.android.systemui.dagger.qualifiers.UiBackground;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import com.android.systemui.unfold.config.UnfoldTransitionConfig;
import com.android.systemui.unfold.progress.FixedTimingTransitionProgressProvider;
import com.android.systemui.unfold.progress.PhysicsBasedUnfoldTransitionProgressProvider;
import com.android.systemui.unfold.updates.DeviceFoldStateProvider;
import com.android.systemui.unfold.updates.FoldStateProvider;
import com.android.systemui.unfold.updates.hinge.EmptyHingeAngleProvider;
import com.android.systemui.unfold.updates.hinge.HingeAngleProvider;
import com.android.systemui.unfold.updates.hinge.HingeSensorAngleProvider;
import com.android.systemui.unfold.util.ATraceLoggerTransitionProgressListener;
import com.android.systemui.unfold.util.ScaleAwareTransitionProgressProvider;
import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import java.util.concurrent.Executor;
import javax.inject.Singleton;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: UnfoldSharedModule.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\"\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\t\u001a\u00020\nH\u0007J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0007J.\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\fH\u0007¨\u0006\u0017"}, d2 = {"Lcom/android/systemui/unfold/UnfoldSharedModule;", "", "()V", "hingeAngleProvider", "Lcom/android/systemui/unfold/updates/hinge/HingeAngleProvider;", "config", "Lcom/android/systemui/unfold/config/UnfoldTransitionConfig;", "sensorManager", "Landroid/hardware/SensorManager;", "executor", "Ljava/util/concurrent/Executor;", "provideFoldStateProvider", "Lcom/android/systemui/unfold/updates/FoldStateProvider;", "deviceFoldStateProvider", "Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;", "unfoldTransitionProgressProvider", "Ljava/util/Optional;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "scaleAwareProviderFactory", "Lcom/android/systemui/unfold/util/ScaleAwareTransitionProgressProvider$Factory;", "tracingListener", "Lcom/android/systemui/unfold/util/ATraceLoggerTransitionProgressListener;", "foldStateProvider", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
@Module
public final class UnfoldSharedModule {
    @Provides
    @Singleton
    public final Optional<UnfoldTransitionProgressProvider> unfoldTransitionProgressProvider(UnfoldTransitionConfig config, ScaleAwareTransitionProgressProvider.Factory scaleAwareProviderFactory, ATraceLoggerTransitionProgressListener tracingListener, FoldStateProvider foldStateProvider) {
        UnfoldTransitionProgressProvider fixedTimingTransitionProgressProvider;
        Intrinsics.checkNotNullParameter(config, "config");
        Intrinsics.checkNotNullParameter(scaleAwareProviderFactory, "scaleAwareProviderFactory");
        Intrinsics.checkNotNullParameter(tracingListener, "tracingListener");
        Intrinsics.checkNotNullParameter(foldStateProvider, "foldStateProvider");
        if (!config.isEnabled()) {
            Optional<UnfoldTransitionProgressProvider> optionalEmpty = Optional.empty();
            Intrinsics.checkNotNullExpressionValue(optionalEmpty, "{\n            Optional.empty()\n        }");
            return optionalEmpty;
        }
        if (config.isHingeAngleEnabled()) {
            fixedTimingTransitionProgressProvider = new PhysicsBasedUnfoldTransitionProgressProvider(foldStateProvider);
        } else {
            fixedTimingTransitionProgressProvider = new FixedTimingTransitionProgressProvider(foldStateProvider);
        }
        ScaleAwareTransitionProgressProvider scaleAwareTransitionProgressProviderWrap = scaleAwareProviderFactory.wrap(fixedTimingTransitionProgressProvider);
        scaleAwareTransitionProgressProviderWrap.addCallback((UnfoldTransitionProgressProvider.TransitionProgressListener) tracingListener);
        Optional<UnfoldTransitionProgressProvider> optionalOf = Optional.of(scaleAwareTransitionProgressProviderWrap);
        Intrinsics.checkNotNullExpressionValue(optionalOf, "{\n            val basePr…             })\n        }");
        return optionalOf;
    }

    @Provides
    @Singleton
    public final FoldStateProvider provideFoldStateProvider(DeviceFoldStateProvider deviceFoldStateProvider) {
        Intrinsics.checkNotNullParameter(deviceFoldStateProvider, "deviceFoldStateProvider");
        return deviceFoldStateProvider;
    }

    @Provides
    public final HingeAngleProvider hingeAngleProvider(UnfoldTransitionConfig config, SensorManager sensorManager, @UiBackground Executor executor) {
        Intrinsics.checkNotNullParameter(config, "config");
        Intrinsics.checkNotNullParameter(sensorManager, "sensorManager");
        Intrinsics.checkNotNullParameter(executor, "executor");
        if (config.isHingeAngleEnabled()) {
            return new HingeSensorAngleProvider(sensorManager, executor);
        }
        return EmptyHingeAngleProvider.INSTANCE;
    }
}
