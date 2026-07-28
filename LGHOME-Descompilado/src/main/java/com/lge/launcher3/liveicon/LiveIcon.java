package com.lge.launcher3.liveicon;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import com.android.launcher3.compat.UserManagerCompat;
import com.lge.launcher3.util.LGLog;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;

/* JADX INFO: loaded from: classes.dex */
public abstract class LiveIcon extends Observable {
    private static final int MSG_TIME_TICK = 2;
    private static final int MSG_UPDATE_ICON = 1;
    private static final String TAG = "LiveIcon";
    private static final long TIME_TICK_DELAY = 60000;
    protected Context mContext;
    private boolean mIsForceUpdate;
    private UserManagerCompat mUserManager;
    private Handler mHandler = new Handler() { // from class: com.lge.launcher3.liveicon.LiveIcon.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                LiveIcon.this.updateIcon();
            } else {
                if (i != 2) {
                    return;
                }
                LiveIcon.this.updateIcon();
                sendEmptyMessageDelayed(2, LiveIcon.TIME_TICK_DELAY);
            }
        }
    };
    private BroadcastReceiver mEventListener = new BroadcastReceiver() { // from class: com.lge.launcher3.liveicon.LiveIcon.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            LGLog.d(LiveIcon.TAG, "Receive: " + intent.getAction());
            LiveIcon.this.mHandler.removeMessages(1);
            LiveIcon.this.mHandler.sendEmptyMessage(1);
        }
    };

    public abstract ComponentName getComponentName();

    protected abstract Drawable getIcon();

    protected abstract IntentFilter getIntentFilter();

    protected abstract boolean shouldUpdate();

    protected abstract void updateIconImpl();

    public LiveIcon(Context context) {
        this.mContext = context;
        this.mUserManager = UserManagerCompat.getInstance(context);
    }

    public void updateIcon() {
        if (this.mIsForceUpdate || shouldUpdate()) {
            updateIconImpl();
            setChanged();
            this.mIsForceUpdate = false;
        }
        if (hasChanged()) {
            notifyObservers();
        }
    }

    public Drawable getBadgedIcon(UserHandle user) {
        return this.mUserManager.getBadgedDrawableForUser(getIcon(), user);
    }

    public void startEventListening() {
        this.mContext.registerReceiver(this.mEventListener, getIntentFilter());
    }

    public void stopEventListening() {
        this.mContext.unregisterReceiver(this.mEventListener);
    }

    public long getFirstTimeTickUpdateDelay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return (60 - ((long) calendar.get(13))) * 1000;
    }

    public void startTimeTickUpdate() {
        this.mHandler.sendEmptyMessageDelayed(2, getFirstTimeTickUpdateDelay());
    }

    public void stopTimeTickUpdate() {
        this.mHandler.removeMessages(2);
    }

    public void setForceUpdate() {
        this.mIsForceUpdate = true;
    }
}
