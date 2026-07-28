package com.android.launcher3;

import android.os.Handler;

/* JADX INFO: loaded from: classes.dex */
public class Alarm implements Runnable {
    private OnAlarmListener mAlarmListener;
    private long mAlarmTriggerTime;
    private boolean mWaitingForCallback;
    private boolean mAlarmPending = false;
    private Handler mHandler = new Handler();

    public void setOnAlarmListener(OnAlarmListener alarmListener) {
        this.mAlarmListener = alarmListener;
    }

    public void setAlarm(long millisecondsInFuture) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mAlarmPending = true;
        long j = millisecondsInFuture + jCurrentTimeMillis;
        this.mAlarmTriggerTime = j;
        if (this.mWaitingForCallback) {
            return;
        }
        this.mHandler.postDelayed(this, j - jCurrentTimeMillis);
        this.mWaitingForCallback = true;
    }

    public void cancelAlarm() {
        this.mAlarmTriggerTime = 0L;
        this.mAlarmPending = false;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.mWaitingForCallback = false;
        if (this.mAlarmTriggerTime != 0) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            long j = this.mAlarmTriggerTime;
            if (j > jCurrentTimeMillis) {
                this.mHandler.postDelayed(this, Math.max(0L, j - jCurrentTimeMillis));
                this.mWaitingForCallback = true;
                return;
            }
            this.mAlarmPending = false;
            OnAlarmListener onAlarmListener = this.mAlarmListener;
            if (onAlarmListener != null) {
                onAlarmListener.onAlarm(this);
            }
        }
    }

    public boolean alarmPending() {
        return this.mAlarmPending;
    }
}
