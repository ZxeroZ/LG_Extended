package com.android.launcher3.uioverrides;

import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import java.util.HashSet;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class DejankBinderTracker implements Binder.ProxyTransactListener {
    private static final String TAG = "DejankBinderTracker";
    private static final Object sLock = new Object();
    private static boolean sTemporarilyIgnoreTracking;
    private static boolean sTrackingAllowed;
    private static final HashSet<String> sWhitelistedFrameworkClasses;
    private boolean mIsTracking = false;
    private BiConsumer<String, Integer> mUnexpectedTransactionCallback;

    public void onTransactEnded(Object session) {
    }

    public Object onTransactStarted(IBinder binder, int transactionCode) {
        return null;
    }

    static {
        HashSet<String> hashSet = new HashSet<>();
        sWhitelistedFrameworkClasses = hashSet;
        hashSet.add("android.view.IWindowSession");
        hashSet.add("android.os.IPowerManager");
        sTemporarilyIgnoreTracking = false;
        sTrackingAllowed = false;
    }

    public static void whitelistIpcs(Runnable runnable) {
        sTemporarilyIgnoreTracking = true;
        runnable.run();
        sTemporarilyIgnoreTracking = false;
    }

    public static <T> T whitelistIpcs(Supplier<T> supplier) {
        sTemporarilyIgnoreTracking = true;
        T t = supplier.get();
        sTemporarilyIgnoreTracking = false;
        return t;
    }

    public static void allowBinderTrackingInTests() {
        sTrackingAllowed = true;
    }

    public static void disallowBinderTrackingInTests() {
        sTrackingAllowed = false;
    }

    public DejankBinderTracker(BiConsumer<String, Integer> unexpectedTransactionCallback) {
        this.mUnexpectedTransactionCallback = unexpectedTransactionCallback;
    }

    public void startTracking() {
        if (!Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") && !Build.TYPE.toLowerCase(Locale.ROOT).equals("eng")) {
            Log.wtf(TAG, "Unexpected use of binder tracker in non-debug build", new Exception());
        } else {
            if (this.mIsTracking) {
                return;
            }
            this.mIsTracking = true;
            Binder.setProxyTransactListener(this);
        }
    }

    public void stopTracking() {
        if (this.mIsTracking) {
            this.mIsTracking = false;
            Binder.setProxyTransactListener(null);
        }
    }

    public synchronized Object onTransactStarted(IBinder binder, int transactionCode, int flags) {
        String simpleName;
        if (!this.mIsTracking || !sTrackingAllowed || sTemporarilyIgnoreTracking || (flags & 1) == 1 || !isMainThread()) {
            return null;
        }
        try {
            simpleName = binder.getInterfaceDescriptor();
            if (sWhitelistedFrameworkClasses.contains(simpleName)) {
                return null;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            simpleName = binder.getClass().getSimpleName();
        }
        this.mUnexpectedTransactionCallback.accept(simpleName, Integer.valueOf(transactionCode));
        return null;
    }

    public static boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }
}
