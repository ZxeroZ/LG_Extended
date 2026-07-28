package com.android.systemui.unfold;

import android.hardware.SensorManager;
import com.android.systemui.unfold.config.UnfoldTransitionConfig;
import com.android.systemui.unfold.updates.hinge.HingeAngleProvider;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.concurrent.Executor;
import javax.inject.Provider;

/* JADX INFO: loaded from: classes.dex */
public final class UnfoldSharedModule_HingeAngleProviderFactory implements Factory<HingeAngleProvider> {
    private final Provider<UnfoldTransitionConfig> configProvider;
    private final Provider<Executor> executorProvider;
    private final UnfoldSharedModule module;
    private final Provider<SensorManager> sensorManagerProvider;

    public UnfoldSharedModule_HingeAngleProviderFactory(UnfoldSharedModule unfoldSharedModule, Provider<UnfoldTransitionConfig> provider, Provider<SensorManager> provider2, Provider<Executor> provider3) {
        this.module = unfoldSharedModule;
        this.configProvider = provider;
        this.sensorManagerProvider = provider2;
        this.executorProvider = provider3;
    }

    /* JADX DEBUG: Method merged with bridge method: get()Ljava/lang/Object; */
    /* JADX INFO: renamed from: get, reason: merged with bridge method [inline-methods] */
    public HingeAngleProvider m591get() {
        return hingeAngleProvider(this.module, (UnfoldTransitionConfig) this.configProvider.get(), (SensorManager) this.sensorManagerProvider.get(), (Executor) this.executorProvider.get());
    }

    public static UnfoldSharedModule_HingeAngleProviderFactory create(UnfoldSharedModule unfoldSharedModule, Provider<UnfoldTransitionConfig> provider, Provider<SensorManager> provider2, Provider<Executor> provider3) {
        return new UnfoldSharedModule_HingeAngleProviderFactory(unfoldSharedModule, provider, provider2, provider3);
    }

    public static HingeAngleProvider hingeAngleProvider(UnfoldSharedModule unfoldSharedModule, UnfoldTransitionConfig unfoldTransitionConfig, SensorManager sensorManager, Executor executor) {
        return (HingeAngleProvider) Preconditions.checkNotNullFromProvides(unfoldSharedModule.hingeAngleProvider(unfoldTransitionConfig, sensorManager, executor));
    }
}
