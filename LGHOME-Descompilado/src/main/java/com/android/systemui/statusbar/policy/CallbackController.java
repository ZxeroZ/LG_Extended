package com.android.systemui.statusbar.policy;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/* JADX INFO: loaded from: classes.dex */
public interface CallbackController<T> {
    void addCallback(T t);

    void removeCallback(T t);

    default T observe(LifecycleOwner lifecycleOwner, T t) {
        return observe(lifecycleOwner.getLifecycle(), t);
    }

    default T observe(Lifecycle lifecycle, final T t) {
        lifecycle.addObserver(new LifecycleEventObserver() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$CallbackController$SvKTqdpal6JkvNoKg2EuL_VfLlY
            @Override // androidx.lifecycle.LifecycleEventObserver
            public final void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                CallbackController.lambda$observe$0(this.f$0, t, lifecycleOwner, event);
            }
        });
        return t;
    }

    static /* synthetic */ void lambda$observe$0(CallbackController _this, Object obj, LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            _this.addCallback(obj);
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            _this.removeCallback(obj);
        }
    }
}
