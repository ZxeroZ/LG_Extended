package com.android.launcher3.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class LooperExecutor extends AbstractExecutorService {
    private final Handler mHandler;

    @Override // java.util.concurrent.ExecutorService
    public boolean isShutdown() {
        return false;
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isTerminated() {
        return false;
    }

    public LooperExecutor(Looper looper) {
        this.mHandler = new Handler(looper);
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable runnable) {
        if (this.mHandler.getLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            this.mHandler.post(runnable);
        }
    }

    public void post(Runnable runnable) {
        getHandler().post(runnable);
    }

    public void postDelayed(Runnable runnable, int delay) {
        getHandler().postDelayed(runnable, delay);
    }

    @Override // java.util.concurrent.ExecutorService
    @Deprecated
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.concurrent.ExecutorService
    @Deprecated
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.concurrent.ExecutorService
    @Deprecated
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public Thread getThread() {
        return getHandler().getLooper().getThread();
    }

    public Looper getLooper() {
        return getHandler().getLooper();
    }

    public void setThreadPriority(int priority) {
        Process.setThreadPriority(((HandlerThread) getThread()).getThreadId(), priority);
    }
}
