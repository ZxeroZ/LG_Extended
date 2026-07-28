package com.android.launcher3;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.util.LongSparseArray;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class MemoryTracker extends Service {
    public static final String ACTION_START_TRACKING = "com.android.launcher3.action.START_TRACKING";
    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_UPDATE = 3;
    public static final String TAG = "MemoryTracker";
    private static final long UPDATE_RATE = 5000;
    ActivityManager mAm;
    public final LongSparseArray<ProcessMemInfo> mData = new LongSparseArray<>();
    public final ArrayList<Long> mPids = new ArrayList<>();
    private int[] mPidsArray = new int[0];
    private final Object mLock = new Object();
    Handler mHandler = new Handler() { // from class: com.android.launcher3.MemoryTracker.1
        @Override // android.os.Handler
        public void handleMessage(Message m) {
            int i = m.what;
            if (i == 1) {
                MemoryTracker.this.mHandler.removeMessages(3);
                MemoryTracker.this.mHandler.sendEmptyMessage(3);
            } else if (i == 2) {
                MemoryTracker.this.mHandler.removeMessages(3);
            } else {
                if (i != 3) {
                    return;
                }
                MemoryTracker.this.update();
                MemoryTracker.this.mHandler.removeMessages(3);
                MemoryTracker.this.mHandler.sendEmptyMessageDelayed(3, MemoryTracker.UPDATE_RATE);
            }
        }
    };
    private final IBinder mBinder = new MemoryTrackerInterface();

    public static class ProcessMemInfo {
        public long currentPss;
        public long currentUss;
        public String name;
        public int pid;
        public long startTime;
        public long[] pss = new long[256];
        public long[] uss = new long[256];
        public long max = 1;
        public int head = 0;

        public ProcessMemInfo(int pid, String name, long start) {
            this.pid = pid;
            this.name = name;
            this.startTime = start;
        }

        public long getUptime() {
            return System.currentTimeMillis() - this.startTime;
        }
    }

    public static void startTrackingMe(Context context, String name) {
        context.startService(new Intent(context, (Class<?>) MemoryTracker.class).setAction(ACTION_START_TRACKING).putExtra("pid", Process.myPid()).putExtra("name", name));
    }

    public ProcessMemInfo getMemInfo(int pid) {
        return this.mData.get(pid);
    }

    public int[] getTrackedProcesses() {
        return this.mPidsArray;
    }

    public void startTrackingProcess(int pid, String name, long start) {
        synchronized (this.mLock) {
            long j = pid;
            Long lValueOf = Long.valueOf(j);
            if (this.mPids.contains(lValueOf)) {
                return;
            }
            this.mPids.add(lValueOf);
            updatePidsArrayL();
            this.mData.put(j, new ProcessMemInfo(pid, name, start));
        }
    }

    void updatePidsArrayL() {
        int size = this.mPids.size();
        this.mPidsArray = new int[size];
        StringBuffer stringBuffer = new StringBuffer("Now tracking processes: ");
        for (int i = 0; i < size; i++) {
            int iIntValue = this.mPids.get(i).intValue();
            this.mPidsArray[i] = iIntValue;
            stringBuffer.append(iIntValue);
            stringBuffer.append(" ");
        }
        Log.v(TAG, stringBuffer.toString());
    }

    void update() {
        synchronized (this.mLock) {
            Debug.MemoryInfo[] processMemoryInfo = this.mAm.getProcessMemoryInfo(this.mPidsArray);
            int i = 0;
            while (true) {
                if (i >= processMemoryInfo.length) {
                    break;
                }
                Debug.MemoryInfo memoryInfo = processMemoryInfo[i];
                if (i > this.mPids.size()) {
                    Log.e(TAG, "update: unknown process info received: " + memoryInfo);
                    break;
                }
                long jIntValue = this.mPids.get(i).intValue();
                ProcessMemInfo processMemInfo = this.mData.get(jIntValue);
                processMemInfo.head = (processMemInfo.head + 1) % processMemInfo.pss.length;
                long[] jArr = processMemInfo.pss;
                int i2 = processMemInfo.head;
                long totalPss = memoryInfo.getTotalPss();
                processMemInfo.currentPss = totalPss;
                jArr[i2] = totalPss;
                long[] jArr2 = processMemInfo.uss;
                int i3 = processMemInfo.head;
                long totalPrivateDirty = memoryInfo.getTotalPrivateDirty();
                processMemInfo.currentUss = totalPrivateDirty;
                jArr2[i3] = totalPrivateDirty;
                if (processMemInfo.currentPss > processMemInfo.max) {
                    processMemInfo.max = processMemInfo.currentPss;
                }
                if (processMemInfo.currentUss > processMemInfo.max) {
                    processMemInfo.max = processMemInfo.currentUss;
                }
                if (processMemInfo.currentPss == 0) {
                    Log.v(TAG, "update: pid " + jIntValue + " has pss=0, it probably died");
                    this.mData.remove(jIntValue);
                }
                i++;
            }
            for (int size = this.mPids.size() - 1; size >= 0; size--) {
                if (this.mData.get(this.mPids.get(size).intValue()) == null) {
                    this.mPids.remove(size);
                    updatePidsArrayL();
                }
            }
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= 26) {
            String str = TAG;
            createNotificationChannelIfNeeded(this, str);
            startForeground(hashCode(), new Notification.Builder(this, str).setContentTitle(str).build());
        }
        ActivityManager activityManager = (ActivityManager) getSystemService("activity");
        this.mAm = activityManager;
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(256)) {
            if (runningServiceInfo.service.getPackageName().equals(getPackageName())) {
                Log.v(TAG, "discovered running service: " + runningServiceInfo.process + " (" + runningServiceInfo.pid + ")");
                startTrackingProcess(runningServiceInfo.pid, runningServiceInfo.process, System.currentTimeMillis() - (SystemClock.elapsedRealtime() - runningServiceInfo.activeSince));
            }
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : this.mAm.getRunningAppProcesses()) {
            String str2 = runningAppProcessInfo.processName;
            if (str2.startsWith(getPackageName())) {
                Log.v(TAG, "discovered other running process: " + str2 + " (" + runningAppProcessInfo.pid + ")");
                startTrackingProcess(runningAppProcessInfo.pid, str2, System.currentTimeMillis());
            }
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mHandler.sendEmptyMessage(2);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Received start id " + startId + ": " + intent);
        if (intent != null && ACTION_START_TRACKING.equals(intent.getAction())) {
            startTrackingProcess(intent.getIntExtra("pid", -1), intent.getStringExtra("name"), intent.getLongExtra("start", System.currentTimeMillis()));
        }
        this.mHandler.sendEmptyMessage(1);
        return 1;
    }

    public class MemoryTrackerInterface extends Binder {
        public MemoryTrackerInterface() {
        }

        MemoryTracker getService() {
            return MemoryTracker.this;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        this.mHandler.sendEmptyMessage(1);
        return this.mBinder;
    }

    private static void createNotificationChannelIfNeeded(Context context, String channelId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (Build.VERSION.SDK_INT < 26 || notificationManager.getNotificationChannel(channelId) != null) {
            return;
        }
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, 2);
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(false);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}
