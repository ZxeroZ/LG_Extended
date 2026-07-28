package com.android.launcher3.util;

import android.content.Context;
import android.os.Looper;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.util.ResourceBasedOverride;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/* JADX INFO: loaded from: classes.dex */
public class MainThreadInitializedObject<T> {
    private final ObjectProvider<T> mProvider;
    private T mValue;

    public interface ObjectProvider<T> {
        T get(Context context);
    }

    public MainThreadInitializedObject(ObjectProvider<T> provider) {
        this.mProvider = provider;
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$get$0$MainThreadInitializedObject(Landroid/content/Context;)Ljava/lang/Object; */
    /* JADX INFO: renamed from: get, reason: merged with bridge method [inline-methods] */
    public T lambda$get$0$MainThreadInitializedObject(final Context context) {
        if (this.mValue == null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.mValue = this.mProvider.get(context.getApplicationContext());
            } else {
                try {
                    return (T) new MainThreadExecutor().submit(new Callable() { // from class: com.android.launcher3.util.-$$Lambda$MainThreadInitializedObject$_qwb-t2dHJ70jMYIrPM-zx5ZBDI
                        @Override // java.util.concurrent.Callable
                        public final Object call() {
                            return this.f$0.lambda$get$0$MainThreadInitializedObject(context);
                        }
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return this.mValue;
    }

    public T getNoCreate() {
        return this.mValue;
    }

    public void initializeForTesting(T value) {
        this.mValue = value;
    }

    public static <T extends ResourceBasedOverride> MainThreadInitializedObject<T> forOverride(final Class<T> clazz, final int resourceId) {
        return new MainThreadInitializedObject<>(new ObjectProvider() { // from class: com.android.launcher3.util.-$$Lambda$MainThreadInitializedObject$78ivjLDiQLpJKU8EbkaXzW3zhG0
            @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
            public final Object get(Context context) {
                return ResourceBasedOverride.Overrides.getObject(clazz, context, resourceId);
            }
        });
    }
}
