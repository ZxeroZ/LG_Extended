package com.android.systemui.unfold.util;

import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import com.android.systemui.unfold.util.ScaleAwareTransitionProgressProvider;
import dagger.internal.InstanceFactory;
import javax.inject.Provider;

/* JADX INFO: loaded from: classes.dex */
public final class ScaleAwareTransitionProgressProvider_Factory_Impl implements ScaleAwareTransitionProgressProvider.Factory {
    private final C0022ScaleAwareTransitionProgressProvider_Factory delegateFactory;

    ScaleAwareTransitionProgressProvider_Factory_Impl(C0022ScaleAwareTransitionProgressProvider_Factory c0022ScaleAwareTransitionProgressProvider_Factory) {
        this.delegateFactory = c0022ScaleAwareTransitionProgressProvider_Factory;
    }

    @Override // com.android.systemui.unfold.util.ScaleAwareTransitionProgressProvider.Factory
    public ScaleAwareTransitionProgressProvider wrap(UnfoldTransitionProgressProvider unfoldTransitionProgressProvider) {
        return this.delegateFactory.get(unfoldTransitionProgressProvider);
    }

    public static Provider<ScaleAwareTransitionProgressProvider.Factory> create(C0022ScaleAwareTransitionProgressProvider_Factory c0022ScaleAwareTransitionProgressProvider_Factory) {
        return InstanceFactory.create(new ScaleAwareTransitionProgressProvider_Factory_Impl(c0022ScaleAwareTransitionProgressProvider_Factory));
    }
}
