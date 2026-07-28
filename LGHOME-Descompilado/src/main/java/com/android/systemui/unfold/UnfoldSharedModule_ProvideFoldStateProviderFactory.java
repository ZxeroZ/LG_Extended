package com.android.systemui.unfold;

import com.android.systemui.unfold.updates.DeviceFoldStateProvider;
import com.android.systemui.unfold.updates.FoldStateProvider;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* JADX INFO: loaded from: classes.dex */
public final class UnfoldSharedModule_ProvideFoldStateProviderFactory implements Factory<FoldStateProvider> {
    private final Provider<DeviceFoldStateProvider> deviceFoldStateProvider;
    private final UnfoldSharedModule module;

    public UnfoldSharedModule_ProvideFoldStateProviderFactory(UnfoldSharedModule unfoldSharedModule, Provider<DeviceFoldStateProvider> provider) {
        this.module = unfoldSharedModule;
        this.deviceFoldStateProvider = provider;
    }

    /* JADX DEBUG: Method merged with bridge method: get()Ljava/lang/Object; */
    /* JADX INFO: renamed from: get, reason: merged with bridge method [inline-methods] */
    public FoldStateProvider m592get() {
        return provideFoldStateProvider(this.module, (DeviceFoldStateProvider) this.deviceFoldStateProvider.get());
    }

    public static UnfoldSharedModule_ProvideFoldStateProviderFactory create(UnfoldSharedModule unfoldSharedModule, Provider<DeviceFoldStateProvider> provider) {
        return new UnfoldSharedModule_ProvideFoldStateProviderFactory(unfoldSharedModule, provider);
    }

    public static FoldStateProvider provideFoldStateProvider(UnfoldSharedModule unfoldSharedModule, DeviceFoldStateProvider deviceFoldStateProvider) {
        return (FoldStateProvider) Preconditions.checkNotNullFromProvides(unfoldSharedModule.provideFoldStateProvider(deviceFoldStateProvider));
    }
}
