package com.android.systemui.unfold;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.devicestate.DeviceStateManager;
import android.os.Handler;
import com.android.systemui.unfold.UnfoldSharedComponent;
import com.android.systemui.unfold.config.UnfoldTransitionConfig;
import com.android.systemui.unfold.updates.DeviceFoldStateProvider;
import com.android.systemui.unfold.updates.DeviceFoldStateProvider_Factory;
import com.android.systemui.unfold.updates.FoldStateProvider;
import com.android.systemui.unfold.updates.hinge.HingeAngleProvider;
import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import com.android.systemui.unfold.util.ATraceLoggerTransitionProgressListener;
import com.android.systemui.unfold.util.ATraceLoggerTransitionProgressListener_Factory;
import com.android.systemui.unfold.util.C0022ScaleAwareTransitionProgressProvider_Factory;
import com.android.systemui.unfold.util.ScaleAwareTransitionProgressProvider;
import com.android.systemui.unfold.util.ScaleAwareTransitionProgressProvider_Factory_Impl;
import dagger.internal.DoubleCheck;
import dagger.internal.InstanceFactory;
import dagger.internal.Preconditions;
import java.util.Optional;
import java.util.concurrent.Executor;
import javax.inject.Provider;

/* JADX INFO: loaded from: classes.dex */
public final class DaggerUnfoldSharedComponent implements UnfoldSharedComponent {
    private Provider<ATraceLoggerTransitionProgressListener> aTraceLoggerTransitionProgressListenerProvider;
    private Provider<ActivityManager> activityManagerProvider;
    private Provider<Executor> backgroundExecutorProvider;
    private Provider<UnfoldTransitionConfig> configProvider;
    private Provider<ContentResolver> contentResolverProvider;
    private Provider<Context> contextProvider;
    private Provider<DeviceFoldStateProvider> deviceFoldStateProvider;
    private Provider<DeviceStateManager> deviceStateManagerProvider;
    private Provider<Executor> executorProvider;
    private Provider<ScaleAwareTransitionProgressProvider.Factory> factoryProvider;
    private Provider<Handler> handlerProvider;
    private Provider<HingeAngleProvider> hingeAngleProvider;
    private Provider<FoldStateProvider> provideFoldStateProvider;
    private C0022ScaleAwareTransitionProgressProvider_Factory scaleAwareTransitionProgressProvider;
    private Provider<ScreenStatusProvider> screenStatusProvider2;
    private Provider<SensorManager> sensorManagerProvider;
    private Provider<String> tracingTagPrefixProvider;
    private Provider<Optional<UnfoldTransitionProgressProvider>> unfoldTransitionProgressProvider;

    private DaggerUnfoldSharedComponent(UnfoldSharedModule unfoldSharedModule, Context context, UnfoldTransitionConfig unfoldTransitionConfig, ScreenStatusProvider screenStatusProvider, DeviceStateManager deviceStateManager, ActivityManager activityManager, SensorManager sensorManager, Handler handler, Executor executor, Executor executor2, String str, ContentResolver contentResolver) {
        initialize(unfoldSharedModule, context, unfoldTransitionConfig, screenStatusProvider, deviceStateManager, activityManager, sensorManager, handler, executor, executor2, str, contentResolver);
    }

    public static UnfoldSharedComponent.Factory factory() {
        return new Factory();
    }

    private void initialize(UnfoldSharedModule unfoldSharedModule, Context context, UnfoldTransitionConfig unfoldTransitionConfig, ScreenStatusProvider screenStatusProvider, DeviceStateManager deviceStateManager, ActivityManager activityManager, SensorManager sensorManager, Handler handler, Executor executor, Executor executor2, String str, ContentResolver contentResolver) {
        this.configProvider = InstanceFactory.create(unfoldTransitionConfig);
        dagger.internal.Factory factoryCreate = InstanceFactory.create(contentResolver);
        this.contentResolverProvider = factoryCreate;
        C0022ScaleAwareTransitionProgressProvider_Factory c0022ScaleAwareTransitionProgressProvider_FactoryCreate = C0022ScaleAwareTransitionProgressProvider_Factory.create(factoryCreate);
        this.scaleAwareTransitionProgressProvider = c0022ScaleAwareTransitionProgressProvider_FactoryCreate;
        this.factoryProvider = ScaleAwareTransitionProgressProvider_Factory_Impl.create(c0022ScaleAwareTransitionProgressProvider_FactoryCreate);
        dagger.internal.Factory factoryCreate2 = InstanceFactory.create(str);
        this.tracingTagPrefixProvider = factoryCreate2;
        this.aTraceLoggerTransitionProgressListenerProvider = ATraceLoggerTransitionProgressListener_Factory.create(factoryCreate2);
        this.contextProvider = InstanceFactory.create(context);
        this.sensorManagerProvider = InstanceFactory.create(sensorManager);
        dagger.internal.Factory factoryCreate3 = InstanceFactory.create(executor2);
        this.backgroundExecutorProvider = factoryCreate3;
        this.hingeAngleProvider = UnfoldSharedModule_HingeAngleProviderFactory.create(unfoldSharedModule, this.configProvider, this.sensorManagerProvider, factoryCreate3);
        this.screenStatusProvider2 = InstanceFactory.create(screenStatusProvider);
        this.deviceStateManagerProvider = InstanceFactory.create(deviceStateManager);
        this.activityManagerProvider = InstanceFactory.create(activityManager);
        this.executorProvider = InstanceFactory.create(executor);
        dagger.internal.Factory factoryCreate4 = InstanceFactory.create(handler);
        this.handlerProvider = factoryCreate4;
        DeviceFoldStateProvider_Factory deviceFoldStateProvider_FactoryCreate = DeviceFoldStateProvider_Factory.create(this.contextProvider, this.hingeAngleProvider, this.screenStatusProvider2, this.deviceStateManagerProvider, this.activityManagerProvider, this.executorProvider, factoryCreate4);
        this.deviceFoldStateProvider = deviceFoldStateProvider_FactoryCreate;
        Provider<FoldStateProvider> provider = DoubleCheck.provider(UnfoldSharedModule_ProvideFoldStateProviderFactory.create(unfoldSharedModule, deviceFoldStateProvider_FactoryCreate));
        this.provideFoldStateProvider = provider;
        this.unfoldTransitionProgressProvider = DoubleCheck.provider(UnfoldSharedModule_UnfoldTransitionProgressProviderFactory.create(unfoldSharedModule, this.configProvider, this.factoryProvider, this.aTraceLoggerTransitionProgressListenerProvider, provider));
    }

    @Override // com.android.systemui.unfold.UnfoldSharedComponent
    public Optional<UnfoldTransitionProgressProvider> getUnfoldTransitionProvider() {
        return (Optional) this.unfoldTransitionProgressProvider.get();
    }

    private static final class Factory implements UnfoldSharedComponent.Factory {
        private Factory() {
        }

        @Override // com.android.systemui.unfold.UnfoldSharedComponent.Factory
        public UnfoldSharedComponent create(Context context, UnfoldTransitionConfig unfoldTransitionConfig, ScreenStatusProvider screenStatusProvider, DeviceStateManager deviceStateManager, ActivityManager activityManager, SensorManager sensorManager, Handler handler, Executor executor, Executor executor2, String str, ContentResolver contentResolver) {
            Preconditions.checkNotNull(context);
            Preconditions.checkNotNull(unfoldTransitionConfig);
            Preconditions.checkNotNull(screenStatusProvider);
            Preconditions.checkNotNull(deviceStateManager);
            Preconditions.checkNotNull(activityManager);
            Preconditions.checkNotNull(sensorManager);
            Preconditions.checkNotNull(handler);
            Preconditions.checkNotNull(executor);
            Preconditions.checkNotNull(executor2);
            Preconditions.checkNotNull(str);
            Preconditions.checkNotNull(contentResolver);
            return new DaggerUnfoldSharedComponent(new UnfoldSharedModule(), context, unfoldTransitionConfig, screenStatusProvider, deviceStateManager, activityManager, sensorManager, handler, executor, executor2, str, contentResolver);
        }
    }
}
