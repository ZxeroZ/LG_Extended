package com.android.systemui.unfold.util;

import dagger.internal.Factory;
import javax.inject.Provider;

/* JADX INFO: loaded from: classes.dex */
public final class ATraceLoggerTransitionProgressListener_Factory implements Factory<ATraceLoggerTransitionProgressListener> {
    private final Provider<String> tracePrefixProvider;

    public ATraceLoggerTransitionProgressListener_Factory(Provider<String> provider) {
        this.tracePrefixProvider = provider;
    }

    /* JADX DEBUG: Method merged with bridge method: get()Ljava/lang/Object; */
    /* JADX INFO: renamed from: get, reason: merged with bridge method [inline-methods] */
    public ATraceLoggerTransitionProgressListener m595get() {
        return newInstance((String) this.tracePrefixProvider.get());
    }

    public static ATraceLoggerTransitionProgressListener_Factory create(Provider<String> provider) {
        return new ATraceLoggerTransitionProgressListener_Factory(provider);
    }

    public static ATraceLoggerTransitionProgressListener newInstance(String str) {
        return new ATraceLoggerTransitionProgressListener(str);
    }
}
