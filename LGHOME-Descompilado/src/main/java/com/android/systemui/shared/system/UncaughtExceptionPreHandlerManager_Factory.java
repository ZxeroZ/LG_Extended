package com.android.systemui.shared.system;

import dagger.internal.Factory;

/* JADX INFO: loaded from: classes.dex */
public final class UncaughtExceptionPreHandlerManager_Factory implements Factory<UncaughtExceptionPreHandlerManager> {
    /* JADX DEBUG: Method merged with bridge method: get()Ljava/lang/Object; */
    /* JADX INFO: renamed from: get, reason: merged with bridge method [inline-methods] */
    public UncaughtExceptionPreHandlerManager m590get() {
        return newInstance();
    }

    public static UncaughtExceptionPreHandlerManager_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static UncaughtExceptionPreHandlerManager newInstance() {
        return new UncaughtExceptionPreHandlerManager();
    }

    private static final class InstanceHolder {
        private static final UncaughtExceptionPreHandlerManager_Factory INSTANCE = new UncaughtExceptionPreHandlerManager_Factory();

        private InstanceHolder() {
        }
    }
}
