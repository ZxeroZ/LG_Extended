package com.android.launcher3.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import com.android.launcher3.BaseActivity;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public final class ActivityTracker<T extends BaseActivity> {
    private static final String EXTRA_SCHEDULER_CALLBACK = "launcher.scheduler_callback";
    private WeakReference<T> mCurrentActivity = new WeakReference<>(null);

    /* JADX WARN: Incorrect return type in method signature: <R:TT;>()TR; */
    public BaseActivity getCreatedActivity() {
        return this.mCurrentActivity.get();
    }

    public void onActivityDestroyed(T activity) {
        if (this.mCurrentActivity.get() == activity) {
            this.mCurrentActivity.clear();
        }
    }

    public void runCallbackWhenActivityExists(SchedulerCallback<T> callback, Intent intent) {
        T t = this.mCurrentActivity.get();
        if (t != null) {
            Intent intent2 = t.getIntent();
            if (intent2 != null) {
                callback.addToIntent(intent2);
            }
            callback.init(t, t.isStarted());
            return;
        }
        callback.addToIntent(intent);
    }

    public boolean handleCreate(T activity) {
        this.mCurrentActivity = new WeakReference<>(activity);
        return handleIntent(activity, activity.getIntent(), false);
    }

    public boolean handleNewIntent(T activity, Intent intent) {
        return handleIntent(activity, intent, activity.isStarted());
    }

    private boolean handleIntent(T activity, Intent intent, boolean alreadyOnHome) {
        if (intent == null || intent.getExtras() == null) {
            return false;
        }
        IBinder binder = intent.getExtras().getBinder(EXTRA_SCHEDULER_CALLBACK);
        if (!(binder instanceof ObjectWrapper)) {
            return false;
        }
        if (((SchedulerCallback) ((ObjectWrapper) binder).get()).init(activity, alreadyOnHome)) {
            return true;
        }
        intent.getExtras().remove(EXTRA_SCHEDULER_CALLBACK);
        return true;
    }

    public interface SchedulerCallback<T extends BaseActivity> {
        boolean init(T activity, boolean alreadyOnHome);

        default Intent addToIntent(Intent intent) {
            Bundle bundle = new Bundle();
            bundle.putBinder(ActivityTracker.EXTRA_SCHEDULER_CALLBACK, ObjectWrapper.wrap(this));
            intent.putExtras(bundle);
            return intent;
        }
    }
}
