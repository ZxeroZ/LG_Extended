package com.evie.screen.api;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import java.lang.ref.SoftReference;

/* JADX INFO: loaded from: classes.dex */
public class EvieApi {
    private static final long CONNECT_TIMEOUT_MS = 5000;
    private static final int LIB_VERSION = 8;
    private static final long MAX_RECONNECT_WAIT_TIME_MS = 60000;
    private static final String SIDE_SCREEN_PACKAGE_NAME = "com.discoveryscreen";
    private static final String SIDE_SCREEN_SERVICE_NAME = "com.evie.screen.EvieApiService";
    private static final String TAG = "EvieApi";
    private EvieApiCallbacks mCallbacks;
    private Runnable mCheckConnectionRunnable;
    private int mConnectAttemptCount;
    private final Runnable mConnectRunnable;
    private final int mCount;
    private boolean mHasNotifiedServiceState;
    private Activity mHostActivity;
    private boolean mIsDestroyed;
    private boolean mIsLightNavigationBar;
    private boolean mIsLightStatusBar;
    private boolean mIsResumed;
    private final boolean mIsRtl;
    private boolean mIsStatusBarHidden;
    private boolean mIsTranslucentNavigationBar;
    private boolean mLastNotifiedServiceState;
    private final Messenger mLocalMessenger;
    private final Handler mMainThreadHandler;
    private Messenger mRemoteMessenger;
    private boolean mSentWindowAttributes;
    private ServiceConnection mServiceConnection;
    private final Intent mSettingsActivityLaunchIntent;
    private static final boolean LOG = Build.TYPE.equals("userdebug");
    private static int sCount = 0;

    private static final class MainThreadHandler extends Handler {
        private SoftReference<EvieApi> mEvieApiRef;

        private MainThreadHandler(EvieApi evieApi) {
            super(Looper.getMainLooper());
            this.mEvieApiRef = new SoftReference<>(evieApi);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            EvieApi evieApi = this.mEvieApiRef.get();
            if (evieApi != null) {
                evieApi.handleRemoteMessage(message);
            }
        }
    }

    public EvieApi(Activity activity, EvieApiCallbacks evieApiCallbacks, boolean z, Intent intent) {
        int i = sCount;
        sCount = i + 1;
        this.mCount = i;
        MainThreadHandler mainThreadHandler = new MainThreadHandler();
        this.mMainThreadHandler = mainThreadHandler;
        this.mLocalMessenger = new Messenger(mainThreadHandler);
        Runnable runnable = new Runnable() { // from class: com.evie.screen.api.EvieApi.1
            @Override // java.lang.Runnable
            public void run() {
                EvieApi.this.handleConnect();
            }
        };
        this.mConnectRunnable = runnable;
        this.mCheckConnectionRunnable = new Runnable() { // from class: com.evie.screen.api.EvieApi.2
            @Override // java.lang.Runnable
            public void run() {
                EvieApi.this.checkConnection();
            }
        };
        this.mServiceConnection = new ServiceConnection() { // from class: com.evie.screen.api.EvieApi.3
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                EvieApi.this.handleOnServiceConnected(iBinder);
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                if (EvieApi.LOG) {
                    Log.d(EvieApi.TAG, EvieApi.this.mCount + " onServiceDisconnected");
                }
                EvieApi.this.handleConnectionError(null);
            }
        };
        if (LOG) {
            Log.d(TAG, i + " constructor");
        }
        if (activity == null) {
            throw new IllegalArgumentException("hostLauncher must not be null");
        }
        if (evieApiCallbacks == null) {
            throw new IllegalArgumentException("callbacks must not be null");
        }
        this.mHostActivity = activity;
        this.mCallbacks = evieApiCallbacks;
        this.mIsRtl = z;
        this.mSettingsActivityLaunchIntent = intent;
        runnable.run();
    }

    public void startScroll() {
        sendRemote(Message.obtain((Handler) null, 1));
    }

    public void updateScroll(float f) {
        Message messageObtain = Message.obtain((Handler) null, 2);
        messageObtain.arg1 = Float.floatToIntBits(f);
        sendRemote(messageObtain);
    }

    public void endScroll(float f) {
        Message messageObtain = Message.obtain((Handler) null, 3);
        messageObtain.arg1 = Float.floatToIntBits(f);
        sendRemote(messageObtain);
    }

    public synchronized void onAttachedToWindow() {
        if (!this.mSentWindowAttributes && this.mIsResumed && !this.mIsDestroyed) {
            if (LOG) {
                Log.d(TAG, this.mCount + " onAttachedToWindow");
            }
            Message messageObtain = Message.obtain((Handler) null, 0);
            if (this.mIsRtl) {
                messageObtain.arg1 |= 1;
            }
            if (this.mIsLightStatusBar) {
                messageObtain.arg1 |= 2;
            }
            if (this.mIsStatusBarHidden) {
                messageObtain.arg1 |= 4;
            }
            if (this.mIsLightNavigationBar) {
                messageObtain.arg1 |= 8;
            }
            if (this.mIsTranslucentNavigationBar) {
                messageObtain.arg1 |= 16;
            }
            messageObtain.arg2 = 8;
            messageObtain.replyTo = this.mLocalMessenger;
            messageObtain.obj = this.mHostActivity.getWindow().getAttributes();
            if (sendRemote(messageObtain)) {
                this.mSentWindowAttributes = true;
                Message messageObtain2 = Message.obtain((Handler) null, 14);
                messageObtain2.obj = this.mSettingsActivityLaunchIntent;
                sendRemote(messageObtain2);
            }
        }
    }

    public synchronized void onDetachedFromWindow() {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " onDetachedFromWindow");
        }
        this.mSentWindowAttributes = false;
        sendRemote(Message.obtain((Handler) null, 5));
    }

    public synchronized void onResume() {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " onResume");
        }
        this.mIsResumed = true;
        if (this.mHostActivity.getWindow() != null) {
            onAttachedToWindow();
        }
        sendRemote(Message.obtain((Handler) null, 6));
    }

    public synchronized void onPause() {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " onPause");
        }
        this.mIsResumed = false;
        sendRemote(Message.obtain((Handler) null, 4));
    }

    public synchronized void onDestroy() {
        if (LOG) {
            Log.d(TAG, this.mCount + " onDestroy");
        }
        safeUnboundService();
        this.mIsDestroyed = true;
        this.mHostActivity = null;
        this.mCallbacks = null;
    }

    public synchronized void turnOff() {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " turnOff");
        }
        sendRemote(Message.obtain((Handler) null, 13));
        onDestroy();
    }

    public synchronized void onAppLaunched(ComponentName componentName) {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " onAppLaunched");
        }
        Message messageObtain = Message.obtain((Handler) null, 9);
        messageObtain.obj = componentName;
        sendRemote(messageObtain);
    }

    public synchronized void setLightStatusBar(boolean z) {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " setLightStatusBar");
        }
        this.mIsLightStatusBar = z;
        Message messageObtain = Message.obtain((Handler) null, 10);
        if (z) {
            messageObtain.arg1 |= 2;
        }
        sendRemote(messageObtain);
    }

    public synchronized void setStatusBarVisible(boolean z) {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " setStatusBarVisibility");
        }
        this.mIsStatusBarHidden = !z;
        Message messageObtain = Message.obtain((Handler) null, 12);
        if (this.mIsStatusBarHidden) {
            messageObtain.arg1 |= 4;
        }
        sendRemote(messageObtain);
    }

    public synchronized void setLightNavigationBar(boolean z) {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " setLightNavigationBar");
        }
        this.mIsLightNavigationBar = z;
        Message messageObtain = Message.obtain((Handler) null, 15);
        if (z) {
            messageObtain.arg1 |= 8;
        }
        sendRemote(messageObtain);
    }

    public synchronized void setTranslucentNavigationBar(boolean z) {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " setTranslucentNavigationBar");
        }
        this.mIsTranslucentNavigationBar = z;
        Message messageObtain = Message.obtain((Handler) null, 16);
        if (z) {
            messageObtain.arg1 |= 16;
        }
        sendRemote(messageObtain);
    }

    public synchronized void hide() {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " hide");
        }
        sendRemote(Message.obtain((Handler) null, 11));
    }

    public static boolean isSideScreenAvailable(Context context) {
        try {
            return context.getApplicationContext().getPackageManager().getApplicationEnabledSetting(SIDE_SCREEN_PACKAGE_NAME) != 3;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleRemoteMessage(Message message) {
        if (this.mIsDestroyed) {
            return;
        }
        int i = message.what;
        if (i == 2) {
            this.mCallbacks.onScrollUpdated(Float.intBitsToFloat(message.arg1));
        } else if (i == 8) {
            notifyServiceStateChanged(true);
        }
    }

    private synchronized boolean sendRemote(Message message) {
        if (this.mIsDestroyed) {
            return false;
        }
        Messenger messenger = this.mRemoteMessenger;
        if (messenger == null) {
            if (LOG) {
                Log.d(TAG, this.mCount + " mRemoteMessenger is null");
            }
            return false;
        }
        try {
            messenger.send(message);
            return true;
        } catch (RemoteException e) {
            handleConnectionError(e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleConnect() {
        boolean z = LOG;
        if (z) {
            Log.d(TAG, this.mCount + " handleConnect");
        }
        if (!this.mIsDestroyed && !this.mHostActivity.isFinishing() && !this.mHostActivity.isDestroyed() && isSideScreenAvailable(this.mHostActivity)) {
            Intent intent = new Intent(SIDE_SCREEN_SERVICE_NAME);
            intent.setPackage(SIDE_SCREEN_PACKAGE_NAME);
            if (!this.mHostActivity.bindService(intent, this.mServiceConnection, 129)) {
                handleConnectionError(null);
            } else {
                if (z) {
                    Log.d(TAG, this.mCount + " service bound");
                }
                this.mMainThreadHandler.postDelayed(this.mCheckConnectionRunnable, CONNECT_TIMEOUT_MS);
            }
            return;
        }
        handleConnectionError(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void checkConnection() {
        if (this.mRemoteMessenger == null) {
            if (LOG) {
                Log.d(TAG, this.mCount + " service bound but not connected, retrying...");
            }
            handleConnectionError(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleOnServiceConnected(IBinder iBinder) {
        if (LOG) {
            Log.d(TAG, this.mCount + " onServiceConnected");
        }
        this.mMainThreadHandler.removeCallbacks(this.mCheckConnectionRunnable);
        if (!this.mIsDestroyed && !this.mHostActivity.isFinishing() && !this.mHostActivity.isDestroyed()) {
            this.mRemoteMessenger = new Messenger(iBinder);
            this.mConnectAttemptCount = 0;
            if (this.mHostActivity.getWindow() != null) {
                onAttachedToWindow();
            }
            return;
        }
        handleConnectionError(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleConnectionError(RemoteException remoteException) {
        boolean z = LOG;
        if (z) {
            Log.e(TAG, this.mCount + " handleConnectionError", remoteException);
        }
        this.mMainThreadHandler.removeCallbacks(this.mCheckConnectionRunnable);
        safeUnboundService();
        this.mRemoteMessenger = null;
        this.mSentWindowAttributes = false;
        notifyServiceStateChanged(false);
        this.mMainThreadHandler.removeCallbacks(this.mConnectRunnable);
        int i = this.mConnectAttemptCount + 1;
        this.mConnectAttemptCount = i;
        long jRound = Math.round(Math.pow(i, 1.5d) * 1000.0d);
        if (jRound > MAX_RECONNECT_WAIT_TIME_MS || this.mIsDestroyed || this.mHostActivity.isFinishing() || this.mHostActivity.isDestroyed()) {
            this.mConnectAttemptCount = 0;
            if (z) {
                Log.d(TAG, this.mCount + " Giving up reconnect retries");
            }
        } else {
            this.mMainThreadHandler.postDelayed(this.mConnectRunnable, jRound);
            if (z) {
                Log.d(TAG, this.mCount + " Retrying in " + jRound);
            }
        }
    }

    private synchronized void notifyServiceStateChanged(final boolean z) {
        if (!this.mIsDestroyed && (!this.mHasNotifiedServiceState || this.mLastNotifiedServiceState != z)) {
            this.mHostActivity.runOnUiThread(new Runnable() { // from class: com.evie.screen.api.EvieApi.4
                @Override // java.lang.Runnable
                public void run() {
                    if (EvieApi.LOG) {
                        Log.d(EvieApi.TAG, EvieApi.this.mCount + " notifyServiceStateChanged: " + z);
                    }
                    EvieApi.this.mCallbacks.onServiceStateChanged(z);
                    EvieApi.this.mHasNotifiedServiceState = true;
                    EvieApi.this.mLastNotifiedServiceState = z;
                }
            });
        }
    }

    private void safeUnboundService() {
        if (this.mIsDestroyed) {
            return;
        }
        if (LOG) {
            Log.d(TAG, this.mCount + " safeUnboundService");
        }
        try {
            this.mHostActivity.unbindService(this.mServiceConnection);
        } catch (Exception e) {
            if (LOG) {
                Log.e(TAG, this.mCount + " safeUnboundService", e);
            }
        }
    }
}
