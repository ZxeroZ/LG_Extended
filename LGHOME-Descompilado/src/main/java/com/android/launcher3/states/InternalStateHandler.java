package com.android.launcher3.states;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.MainThreadExecutor;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public abstract class InternalStateHandler extends Binder {
    public static final String EXTRA_STATE_HANDLER = "launcher.state_handler";
    private static final Scheduler sScheduler = new Scheduler();

    protected abstract boolean init(Launcher launcher, boolean alreadyOnHome);

    public final Intent addToIntent(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putBinder(EXTRA_STATE_HANDLER, this);
        intent.putExtras(bundle);
        return intent;
    }

    public final void initWhenReady() {
        sScheduler.schedule(this);
    }

    public boolean clearReference() {
        return sScheduler.clearReference(this);
    }

    public static boolean hasPending() {
        return sScheduler.hasPending();
    }

    public static boolean handleCreate(Launcher launcher, Intent intent) {
        return handleIntent(launcher, intent, false, false);
    }

    public static boolean handleNewIntent(Launcher launcher, Intent intent, boolean alreadyOnHome) {
        return handleIntent(launcher, intent, alreadyOnHome, true);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0027  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static boolean handleIntent(com.android.launcher3.Launcher r3, android.content.Intent r4, boolean r5, boolean r6) {
        /*
            if (r4 == 0) goto L27
            android.os.Bundle r0 = r4.getExtras()
            if (r0 == 0) goto L27
            android.os.Bundle r0 = r4.getExtras()
            java.lang.String r1 = "launcher.state_handler"
            android.os.IBinder r0 = r0.getBinder(r1)
            boolean r2 = r0 instanceof com.android.launcher3.states.InternalStateHandler
            if (r2 == 0) goto L27
            com.android.launcher3.states.InternalStateHandler r0 = (com.android.launcher3.states.InternalStateHandler) r0
            boolean r0 = r0.init(r3, r5)
            if (r0 != 0) goto L25
            android.os.Bundle r4 = r4.getExtras()
            r4.remove(r1)
        L25:
            r4 = 1
            goto L28
        L27:
            r4 = 0
        L28:
            if (r4 != 0) goto L32
            if (r6 != 0) goto L32
            com.android.launcher3.states.InternalStateHandler$Scheduler r4 = com.android.launcher3.states.InternalStateHandler.sScheduler
            boolean r4 = r4.initIfPending(r3, r5)
        L32:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.states.InternalStateHandler.handleIntent(com.android.launcher3.Launcher, android.content.Intent, boolean, boolean):boolean");
    }

    private static class Scheduler implements Runnable {
        private MainThreadExecutor mMainThreadExecutor;
        private WeakReference<InternalStateHandler> mPendingHandler;

        private Scheduler() {
            this.mPendingHandler = new WeakReference<>(null);
        }

        public void schedule(InternalStateHandler handler) {
            synchronized (this) {
                this.mPendingHandler = new WeakReference<>(handler);
                if (this.mMainThreadExecutor == null) {
                    this.mMainThreadExecutor = new MainThreadExecutor();
                }
            }
            this.mMainThreadExecutor.execute(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
            if (instanceNoCreate == null) {
                return;
            }
            LauncherModel.Callbacks callback = instanceNoCreate.getModel().getCallback();
            if (callback instanceof Launcher) {
                Launcher launcher = (Launcher) callback;
                initIfPending(launcher, launcher.isStarted());
            }
        }

        public boolean initIfPending(Launcher launcher, boolean alreadyOnHome) {
            InternalStateHandler internalStateHandler = this.mPendingHandler.get();
            if (internalStateHandler == null) {
                return false;
            }
            if (internalStateHandler.init(launcher, alreadyOnHome)) {
                return true;
            }
            clearReference(internalStateHandler);
            return true;
        }

        public boolean clearReference(InternalStateHandler handler) {
            synchronized (this) {
                if (this.mPendingHandler.get() != handler) {
                    return false;
                }
                this.mPendingHandler.clear();
                return true;
            }
        }

        public boolean hasPending() {
            return this.mPendingHandler.get() != null;
        }
    }
}
