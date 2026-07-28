package com.android.launcher3;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import java.util.Iterator;
import java.util.LinkedList;

/* JADX INFO: loaded from: classes.dex */
public class DeferredHandler {
    LinkedList<Runnable> mQueue = new LinkedList<>();
    private MessageQueue mMessageQueue = Looper.myQueue();
    private Impl mHandler = new Impl();

    class Impl extends Handler implements MessageQueue.IdleHandler {
        Impl() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            synchronized (DeferredHandler.this.mQueue) {
                if (DeferredHandler.this.mQueue.size() == 0) {
                    return;
                }
                DeferredHandler.this.mQueue.removeFirst().run();
                synchronized (DeferredHandler.this.mQueue) {
                    DeferredHandler.this.scheduleNextLocked();
                }
            }
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            handleMessage(null);
            return false;
        }
    }

    private class IdleRunnable implements Runnable {
        Runnable mRunnable;

        IdleRunnable(Runnable r) {
            this.mRunnable = r;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mRunnable.run();
        }
    }

    public void post(Runnable runnable) {
        synchronized (this.mQueue) {
            this.mQueue.add(runnable);
            if (this.mQueue.size() == 1) {
                scheduleNextLocked();
            }
        }
    }

    public void postIdle(final Runnable runnable) {
        post(new IdleRunnable(runnable));
    }

    public void cancelAll() {
        synchronized (this.mQueue) {
            this.mQueue.clear();
        }
    }

    public void flush() {
        LinkedList linkedList = new LinkedList();
        synchronized (this.mQueue) {
            linkedList.addAll(this.mQueue);
            this.mQueue.clear();
        }
        Iterator it = linkedList.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
    }

    void scheduleNextLocked() {
        if (this.mQueue.size() > 0) {
            if (this.mQueue.getFirst() instanceof IdleRunnable) {
                this.mMessageQueue.addIdleHandler(this.mHandler);
            } else {
                this.mHandler.sendEmptyMessage(1);
            }
        }
    }
}
