package com.android.quickstep.util;

import com.android.launcher3.util.Executors;

/* JADX INFO: loaded from: classes.dex */
public abstract class CancellableTask<T> implements Runnable {
    private boolean mCancelled = false;

    public abstract T getResultOnBg();

    public abstract void handleResult(T result);

    @Override // java.lang.Runnable
    public final void run() {
        if (this.mCancelled) {
            return;
        }
        final T resultOnBg = getResultOnBg();
        if (this.mCancelled) {
            return;
        }
        Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.util.-$$Lambda$CancellableTask$AKEKVznZt6hzCwDgs5ykNuX_CUo
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$run$0$CancellableTask(resultOnBg);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: java.lang.Object */
    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$run$0$CancellableTask(Object obj) {
        if (this.mCancelled) {
            return;
        }
        handleResult(obj);
    }

    public void cancel() {
        this.mCancelled = true;
    }
}
