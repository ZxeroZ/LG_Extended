package com.android.launcher3.util;

import android.view.View;
import android.view.ViewTreeObserver;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public class ViewOnDrawExecutor implements Executor, ViewTreeObserver.OnDrawListener, Runnable, View.OnAttachStateChangeListener {
    private View mAttachedView;
    private boolean mCompleted;
    private boolean mFirstDrawCompleted;
    private Launcher mLauncher;
    private boolean mLoadAnimationCompleted;
    private final ArrayList<Runnable> mTasks = new ArrayList<>();

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewDetachedFromWindow(View v) {
    }

    public void attachTo(Launcher launcher) {
        attachTo(launcher, launcher.getWorkspace(), true);
    }

    public void attachTo(Launcher launcher, View attachedView, boolean waitForLoadAnimation) {
        this.mLauncher = launcher;
        this.mAttachedView = attachedView;
        attachedView.addOnAttachStateChangeListener(this);
        if (!waitForLoadAnimation) {
            this.mLoadAnimationCompleted = true;
        }
        if (this.mAttachedView.isAttachedToWindow()) {
            attachObserver();
        }
    }

    private void attachObserver() {
        if (this.mCompleted) {
            return;
        }
        this.mAttachedView.getViewTreeObserver().addOnDrawListener(this);
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable command) {
        this.mTasks.add(command);
        LauncherModel.setWorkerPriority(10);
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewAttachedToWindow(View v) {
        attachObserver();
    }

    @Override // android.view.ViewTreeObserver.OnDrawListener
    public void onDraw() {
        this.mFirstDrawCompleted = true;
        this.mAttachedView.post(this);
    }

    public void onLoadAnimationCompleted() {
        this.mLoadAnimationCompleted = true;
        View view = this.mAttachedView;
        if (view != null) {
            view.post(this);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.mLoadAnimationCompleted && this.mFirstDrawCompleted && !this.mCompleted) {
            runAllTasks();
        }
    }

    public void markCompleted() {
        this.mTasks.clear();
        this.mCompleted = true;
        View view = this.mAttachedView;
        if (view != null) {
            view.getViewTreeObserver().removeOnDrawListener(this);
            this.mAttachedView.removeOnAttachStateChangeListener(this);
        }
        Launcher launcher = this.mLauncher;
        if (launcher != null) {
            launcher.clearPendingExecutor(this);
        }
        LauncherModel.setWorkerPriority(0);
    }

    protected boolean isCompleted() {
        return this.mCompleted;
    }

    protected void runAllTasks() {
        Iterator<Runnable> it = this.mTasks.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        markCompleted();
    }
}
