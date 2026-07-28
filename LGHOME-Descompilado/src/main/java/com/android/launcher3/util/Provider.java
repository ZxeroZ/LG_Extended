package com.android.launcher3.util;

/* JADX INFO: loaded from: classes.dex */
public abstract class Provider<T> {
    public abstract T get();

    public static <T> Provider<T> of(final T value) {
        return new Provider<T>() { // from class: com.android.launcher3.util.Provider.1
            @Override // com.android.launcher3.util.Provider
            public T get() {
                return (T) value;
            }
        };
    }
}
