package com.android.quickstep.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.util.ActivityTracker;
import java.util.function.BiPredicate;

/* JADX INFO: loaded from: classes.dex */
public class ActivityInitListener<T extends BaseActivity> implements ActivityTracker.SchedulerCallback<T> {
    private final ActivityTracker<T> mActivityTracker;
    private boolean mIsRegistered = false;
    private BiPredicate<T, Boolean> mOnInitListener;

    public ActivityInitListener(BiPredicate<T, Boolean> onInitListener, ActivityTracker<T> tracker) {
        this.mOnInitListener = onInitListener;
        this.mActivityTracker = tracker;
    }

    @Override // com.android.launcher3.util.ActivityTracker.SchedulerCallback
    public final boolean init(T activity, boolean alreadyOnHome) {
        if (this.mIsRegistered) {
            return handleInit(activity, alreadyOnHome);
        }
        return false;
    }

    protected boolean handleInit(T activity, boolean alreadyOnHome) {
        return this.mOnInitListener.test(activity, Boolean.valueOf(alreadyOnHome));
    }

    public void register(Intent intent) {
        this.mIsRegistered = true;
        this.mActivityTracker.runCallbackWhenActivityExists(this, intent);
    }

    public void unregister() {
        this.mIsRegistered = false;
        this.mOnInitListener = null;
    }

    public void registerAndStartActivity(Intent intent, RemoteAnimationProvider animProvider, Context context, Handler handler, long duration) {
        this.mIsRegistered = true;
        context.startActivity(addToIntent(new Intent(intent)), animProvider.toActivityOptions(handler, duration, context).toBundle());
    }
}
