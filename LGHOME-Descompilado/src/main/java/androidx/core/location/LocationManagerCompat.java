package androidx.core.location;

import android.content.Context;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.collection.SimpleArrayMap;
import androidx.core.location.GnssStatusCompat;
import androidx.core.os.CancellationSignal;
import androidx.core.os.ExecutorCompat;
import androidx.core.util.Consumer;
import androidx.core.util.Preconditions;
import java.lang.reflect.Field;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/* JADX INFO: loaded from: classes.dex */
public final class LocationManagerCompat {
    private static final long GET_CURRENT_LOCATION_TIMEOUT_MS = 30000;
    private static final long MAX_CURRENT_LOCATION_AGE_MS = 10000;
    private static final long PRE_N_LOOPER_TIMEOUT_S = 5;
    private static Field sContextField;
    private static final SimpleArrayMap<Object, Object> sGnssStatusListeners = new SimpleArrayMap<>();

    public static boolean isLocationEnabled(LocationManager locationManager) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Api28Impl.isLocationEnabled(locationManager);
        }
        if (Build.VERSION.SDK_INT <= 19) {
            try {
                if (sContextField == null) {
                    Field declaredField = LocationManager.class.getDeclaredField("mContext");
                    sContextField = declaredField;
                    declaredField.setAccessible(true);
                }
                Context context = (Context) sContextField.get(locationManager);
                if (context != null) {
                    if (Build.VERSION.SDK_INT == 19) {
                        return Settings.Secure.getInt(context.getContentResolver(), "location_mode", 0) != 0;
                    }
                    return !TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), "location_providers_allowed"));
                }
            } catch (ClassCastException | IllegalAccessException | NoSuchFieldException | SecurityException unused) {
            }
        }
        return locationManager.isProviderEnabled("network") || locationManager.isProviderEnabled("gps");
    }

    public static void getCurrentLocation(LocationManager locationManager, String provider, CancellationSignal cancellationSignal, Executor executor, final Consumer<Location> consumer) {
        if (Build.VERSION.SDK_INT >= 30) {
            Api30Impl.getCurrentLocation(locationManager, provider, cancellationSignal, executor, consumer);
            return;
        }
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        final Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
        if (lastKnownLocation != null && SystemClock.elapsedRealtime() - LocationCompat.getElapsedRealtimeMillis(lastKnownLocation) < MAX_CURRENT_LOCATION_AGE_MS) {
            executor.execute(new Runnable() { // from class: androidx.core.location.LocationManagerCompat.1
                @Override // java.lang.Runnable
                public void run() {
                    consumer.accept(lastKnownLocation);
                }
            });
            return;
        }
        final CancellableLocationListener cancellableLocationListener = new CancellableLocationListener(locationManager, executor, consumer);
        locationManager.requestLocationUpdates(provider, 0L, 0.0f, cancellableLocationListener, Looper.getMainLooper());
        if (cancellationSignal != null) {
            cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: androidx.core.location.LocationManagerCompat.2
                @Override // androidx.core.os.CancellationSignal.OnCancelListener
                public void onCancel() {
                    cancellableLocationListener.cancel();
                }
            });
        }
        cancellableLocationListener.startTimeout(GET_CURRENT_LOCATION_TIMEOUT_MS);
    }

    public static String getGnssHardwareModelName(LocationManager locationManager) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Api28Impl.getGnssHardwareModelName(locationManager);
        }
        return null;
    }

    public static int getGnssYearOfHardware(LocationManager locationManager) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Api28Impl.getGnssYearOfHardware(locationManager);
        }
        return 0;
    }

    public static boolean registerGnssStatusCallback(LocationManager locationManager, GnssStatusCompat.Callback callback, Handler handler) {
        if (Build.VERSION.SDK_INT >= 30) {
            return registerGnssStatusCallback(locationManager, ExecutorCompat.create(handler), callback);
        }
        return registerGnssStatusCallback(locationManager, new InlineHandlerExecutor(handler), callback);
    }

    public static boolean registerGnssStatusCallback(LocationManager locationManager, Executor executor, GnssStatusCompat.Callback callback) {
        if (Build.VERSION.SDK_INT >= 30) {
            return registerGnssStatusCallback(locationManager, null, executor, callback);
        }
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper == null) {
            looperMyLooper = Looper.getMainLooper();
        }
        return registerGnssStatusCallback(locationManager, new Handler(looperMyLooper), executor, callback);
    }

    /* JADX WARN: Removed duplicated region for block: B:89:0x010e A[Catch: all -> 0x012a, TryCatch #6 {all -> 0x012a, blocks: (B:83:0x00eb, B:84:0x0103, B:87:0x0106, B:89:0x010e, B:91:0x0116, B:92:0x011c, B:93:0x011d, B:94:0x0122, B:95:0x0123, B:96:0x0129, B:73:0x00da), top: B:107:0x009a }] */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0123 A[Catch: all -> 0x012a, TryCatch #6 {all -> 0x012a, blocks: (B:83:0x00eb, B:84:0x0103, B:87:0x0106, B:89:0x010e, B:91:0x0116, B:92:0x011c, B:93:0x011d, B:94:0x0122, B:95:0x0123, B:96:0x0129, B:73:0x00da), top: B:107:0x009a }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static boolean registerGnssStatusCallback(final android.location.LocationManager r9, android.os.Handler r10, java.util.concurrent.Executor r11, androidx.core.location.GnssStatusCompat.Callback r12) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 1
            r2 = 0
            r3 = 30
            if (r0 < r3) goto L28
            androidx.collection.SimpleArrayMap<java.lang.Object, java.lang.Object> r0 = androidx.core.location.LocationManagerCompat.sGnssStatusListeners
            monitor-enter(r0)
            java.lang.Object r10 = r0.get(r12)     // Catch: java.lang.Throwable -> L25
            androidx.core.location.LocationManagerCompat$GnssStatusTransport r10 = (androidx.core.location.LocationManagerCompat.GnssStatusTransport) r10     // Catch: java.lang.Throwable -> L25
            if (r10 != 0) goto L18
            androidx.core.location.LocationManagerCompat$GnssStatusTransport r10 = new androidx.core.location.LocationManagerCompat$GnssStatusTransport     // Catch: java.lang.Throwable -> L25
            r10.<init>(r12)     // Catch: java.lang.Throwable -> L25
        L18:
            boolean r9 = r9.registerGnssStatusCallback(r11, r10)     // Catch: java.lang.Throwable -> L25
            if (r9 == 0) goto L23
            r0.put(r12, r10)     // Catch: java.lang.Throwable -> L25
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L25
            return r1
        L23:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L25
            return r2
        L25:
            r9 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L25
            throw r9
        L28:
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 24
            if (r0 < r3) goto L5d
            if (r10 == 0) goto L32
            r0 = r1
            goto L33
        L32:
            r0 = r2
        L33:
            androidx.core.util.Preconditions.checkArgument(r0)
            androidx.collection.SimpleArrayMap<java.lang.Object, java.lang.Object> r0 = androidx.core.location.LocationManagerCompat.sGnssStatusListeners
            monitor-enter(r0)
            java.lang.Object r3 = r0.get(r12)     // Catch: java.lang.Throwable -> L5a
            androidx.core.location.LocationManagerCompat$PreRGnssStatusTransport r3 = (androidx.core.location.LocationManagerCompat.PreRGnssStatusTransport) r3     // Catch: java.lang.Throwable -> L5a
            if (r3 != 0) goto L47
            androidx.core.location.LocationManagerCompat$PreRGnssStatusTransport r3 = new androidx.core.location.LocationManagerCompat$PreRGnssStatusTransport     // Catch: java.lang.Throwable -> L5a
            r3.<init>(r12)     // Catch: java.lang.Throwable -> L5a
            goto L4a
        L47:
            r3.unregister()     // Catch: java.lang.Throwable -> L5a
        L4a:
            r3.register(r11)     // Catch: java.lang.Throwable -> L5a
            boolean r9 = r9.registerGnssStatusCallback(r3, r10)     // Catch: java.lang.Throwable -> L5a
            if (r9 == 0) goto L58
            r0.put(r12, r3)     // Catch: java.lang.Throwable -> L5a
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L5a
            return r1
        L58:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L5a
            return r2
        L5a:
            r9 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L5a
            throw r9
        L5d:
            if (r10 == 0) goto L61
            r0 = r1
            goto L62
        L61:
            r0 = r2
        L62:
            androidx.core.util.Preconditions.checkArgument(r0)
            androidx.collection.SimpleArrayMap<java.lang.Object, java.lang.Object> r0 = androidx.core.location.LocationManagerCompat.sGnssStatusListeners
            monitor-enter(r0)
            java.lang.Object r3 = r0.get(r12)     // Catch: java.lang.Throwable -> L14e
            androidx.core.location.LocationManagerCompat$GpsStatusTransport r3 = (androidx.core.location.LocationManagerCompat.GpsStatusTransport) r3     // Catch: java.lang.Throwable -> L14e
            if (r3 != 0) goto L76
            androidx.core.location.LocationManagerCompat$GpsStatusTransport r3 = new androidx.core.location.LocationManagerCompat$GpsStatusTransport     // Catch: java.lang.Throwable -> L14e
            r3.<init>(r9, r12)     // Catch: java.lang.Throwable -> L14e
            goto L79
        L76:
            r3.unregister()     // Catch: java.lang.Throwable -> L14e
        L79:
            r3.register(r11)     // Catch: java.lang.Throwable -> L14e
            java.util.concurrent.FutureTask r11 = new java.util.concurrent.FutureTask     // Catch: java.lang.Throwable -> L14e
            androidx.core.location.LocationManagerCompat$3 r4 = new androidx.core.location.LocationManagerCompat$3     // Catch: java.lang.Throwable -> L14e
            r4.<init>()     // Catch: java.lang.Throwable -> L14e
            r11.<init>(r4)     // Catch: java.lang.Throwable -> L14e
            android.os.Looper r9 = android.os.Looper.myLooper()     // Catch: java.lang.Throwable -> L14e
            android.os.Looper r4 = r10.getLooper()     // Catch: java.lang.Throwable -> L14e
            if (r9 != r4) goto L94
            r11.run()     // Catch: java.lang.Throwable -> L14e
            goto L9a
        L94:
            boolean r9 = r10.post(r11)     // Catch: java.lang.Throwable -> L14e
            if (r9 == 0) goto L135
        L9a:
            java.util.concurrent.TimeUnit r9 = java.util.concurrent.TimeUnit.SECONDS     // Catch: java.lang.Throwable -> Le6 java.util.concurrent.TimeoutException -> Le9 java.util.concurrent.ExecutionException -> L104
            r4 = 5
            long r4 = r9.toNanos(r4)     // Catch: java.lang.Throwable -> Le6 java.util.concurrent.TimeoutException -> Le9 java.util.concurrent.ExecutionException -> L104
            long r6 = java.lang.System.nanoTime()     // Catch: java.lang.Throwable -> Le6 java.util.concurrent.TimeoutException -> Le9 java.util.concurrent.ExecutionException -> L104
            long r6 = r6 + r4
            r9 = r2
        La8:
            java.util.concurrent.TimeUnit r8 = java.util.concurrent.TimeUnit.NANOSECONDS     // Catch: java.lang.Throwable -> Ld1 java.util.concurrent.TimeoutException -> Ld4 java.util.concurrent.ExecutionException -> Ld7 java.lang.InterruptedException -> Lda
            java.lang.Object r4 = r11.get(r4, r8)     // Catch: java.lang.Throwable -> Ld1 java.util.concurrent.TimeoutException -> Ld4 java.util.concurrent.ExecutionException -> Ld7 java.lang.InterruptedException -> Lda
            java.lang.Boolean r4 = (java.lang.Boolean) r4     // Catch: java.lang.Throwable -> Ld1 java.util.concurrent.TimeoutException -> Ld4 java.util.concurrent.ExecutionException -> Ld7 java.lang.InterruptedException -> Lda
            boolean r4 = r4.booleanValue()     // Catch: java.lang.Throwable -> Ld1 java.util.concurrent.TimeoutException -> Ld4 java.util.concurrent.ExecutionException -> Ld7 java.lang.InterruptedException -> Lda
            if (r4 == 0) goto Lc6
            androidx.collection.SimpleArrayMap<java.lang.Object, java.lang.Object> r4 = androidx.core.location.LocationManagerCompat.sGnssStatusListeners     // Catch: java.lang.Throwable -> Ld1 java.util.concurrent.TimeoutException -> Ld4 java.util.concurrent.ExecutionException -> Ld7 java.lang.InterruptedException -> Lda
            r4.put(r12, r3)     // Catch: java.lang.Throwable -> Ld1 java.util.concurrent.TimeoutException -> Ld4 java.util.concurrent.ExecutionException -> Ld7 java.lang.InterruptedException -> Lda
            if (r9 == 0) goto Lc4
            java.lang.Thread r9 = java.lang.Thread.currentThread()     // Catch: java.lang.Throwable -> L14e
            r9.interrupt()     // Catch: java.lang.Throwable -> L14e
        Lc4:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L14e
            return r1
        Lc6:
            if (r9 == 0) goto Lcf
            java.lang.Thread r9 = java.lang.Thread.currentThread()     // Catch: java.lang.Throwable -> L14e
            r9.interrupt()     // Catch: java.lang.Throwable -> L14e
        Lcf:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L14e
            return r2
        Ld1:
            r10 = move-exception
            r1 = r9
            goto L12b
        Ld4:
            r11 = move-exception
            r1 = r9
            goto Leb
        Ld7:
            r10 = move-exception
            r1 = r9
            goto L106
        Lda:
            long r4 = java.lang.System.nanoTime()     // Catch: java.util.concurrent.TimeoutException -> Le2 java.util.concurrent.ExecutionException -> Le4 java.lang.Throwable -> L12a
            long r4 = r6 - r4
            r9 = r1
            goto La8
        Le2:
            r11 = move-exception
            goto Leb
        Le4:
            r10 = move-exception
            goto L106
        Le6:
            r10 = move-exception
            r1 = r2
            goto L12b
        Le9:
            r11 = move-exception
            r1 = r2
        Leb:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L12a
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L12a
            r12.<init>()     // Catch: java.lang.Throwable -> L12a
            java.lang.StringBuilder r10 = r12.append(r10)     // Catch: java.lang.Throwable -> L12a
            java.lang.String r12 = " appears to be blocked, please run registerGnssStatusCallback() directly on a Looper thread or ensure the main Looper is not blocked by this thread"
            java.lang.StringBuilder r10 = r10.append(r12)     // Catch: java.lang.Throwable -> L12a
            java.lang.String r10 = r10.toString()     // Catch: java.lang.Throwable -> L12a
            r9.<init>(r10, r11)     // Catch: java.lang.Throwable -> L12a
            throw r9     // Catch: java.lang.Throwable -> L12a
        L104:
            r10 = move-exception
            r1 = r2
        L106:
            java.lang.Throwable r9 = r10.getCause()     // Catch: java.lang.Throwable -> L12a
            boolean r9 = r9 instanceof java.lang.RuntimeException     // Catch: java.lang.Throwable -> L12a
            if (r9 != 0) goto L123
            java.lang.Throwable r9 = r10.getCause()     // Catch: java.lang.Throwable -> L12a
            boolean r9 = r9 instanceof java.lang.Error     // Catch: java.lang.Throwable -> L12a
            if (r9 == 0) goto L11d
            java.lang.Throwable r9 = r10.getCause()     // Catch: java.lang.Throwable -> L12a
            java.lang.Error r9 = (java.lang.Error) r9     // Catch: java.lang.Throwable -> L12a
            throw r9     // Catch: java.lang.Throwable -> L12a
        L11d:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L12a
            r9.<init>(r10)     // Catch: java.lang.Throwable -> L12a
            throw r9     // Catch: java.lang.Throwable -> L12a
        L123:
            java.lang.Throwable r9 = r10.getCause()     // Catch: java.lang.Throwable -> L12a
            java.lang.RuntimeException r9 = (java.lang.RuntimeException) r9     // Catch: java.lang.Throwable -> L12a
            throw r9     // Catch: java.lang.Throwable -> L12a
        L12a:
            r10 = move-exception
        L12b:
            if (r1 == 0) goto L134
            java.lang.Thread r9 = java.lang.Thread.currentThread()     // Catch: java.lang.Throwable -> L14e
            r9.interrupt()     // Catch: java.lang.Throwable -> L14e
        L134:
            throw r10     // Catch: java.lang.Throwable -> L14e
        L135:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L14e
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L14e
            r11.<init>()     // Catch: java.lang.Throwable -> L14e
            java.lang.StringBuilder r10 = r11.append(r10)     // Catch: java.lang.Throwable -> L14e
            java.lang.String r11 = " is shutting down"
            java.lang.StringBuilder r10 = r10.append(r11)     // Catch: java.lang.Throwable -> L14e
            java.lang.String r10 = r10.toString()     // Catch: java.lang.Throwable -> L14e
            r9.<init>(r10)     // Catch: java.lang.Throwable -> L14e
            throw r9     // Catch: java.lang.Throwable -> L14e
        L14e:
            r9 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L14e
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.location.LocationManagerCompat.registerGnssStatusCallback(android.location.LocationManager, android.os.Handler, java.util.concurrent.Executor, androidx.core.location.GnssStatusCompat$Callback):boolean");
    }

    public static void unregisterGnssStatusCallback(LocationManager locationManager, GnssStatusCompat.Callback callback) {
        if (Build.VERSION.SDK_INT >= 30) {
            SimpleArrayMap<Object, Object> simpleArrayMap = sGnssStatusListeners;
            synchronized (simpleArrayMap) {
                GnssStatus.Callback callback2 = (GnssStatusTransport) simpleArrayMap.remove(callback);
                if (callback2 != null) {
                    locationManager.unregisterGnssStatusCallback(callback2);
                }
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            SimpleArrayMap<Object, Object> simpleArrayMap2 = sGnssStatusListeners;
            synchronized (simpleArrayMap2) {
                PreRGnssStatusTransport preRGnssStatusTransport = (PreRGnssStatusTransport) simpleArrayMap2.remove(callback);
                if (preRGnssStatusTransport != null) {
                    preRGnssStatusTransport.unregister();
                    locationManager.unregisterGnssStatusCallback(preRGnssStatusTransport);
                }
            }
            return;
        }
        SimpleArrayMap<Object, Object> simpleArrayMap3 = sGnssStatusListeners;
        synchronized (simpleArrayMap3) {
            GpsStatusTransport gpsStatusTransport = (GpsStatusTransport) simpleArrayMap3.remove(callback);
            if (gpsStatusTransport != null) {
                gpsStatusTransport.unregister();
                locationManager.removeGpsStatusListener(gpsStatusTransport);
            }
        }
    }

    private LocationManagerCompat() {
    }

    private static class GnssStatusTransport extends GnssStatus.Callback {
        final GnssStatusCompat.Callback mCallback;

        GnssStatusTransport(GnssStatusCompat.Callback callback) {
            Preconditions.checkArgument(callback != null, "invalid null callback");
            this.mCallback = callback;
        }

        @Override // android.location.GnssStatus.Callback
        public void onStarted() {
            this.mCallback.onStarted();
        }

        @Override // android.location.GnssStatus.Callback
        public void onStopped() {
            this.mCallback.onStopped();
        }

        @Override // android.location.GnssStatus.Callback
        public void onFirstFix(int ttffMillis) {
            this.mCallback.onFirstFix(ttffMillis);
        }

        @Override // android.location.GnssStatus.Callback
        public void onSatelliteStatusChanged(GnssStatus status) {
            this.mCallback.onSatelliteStatusChanged(GnssStatusCompat.wrap(status));
        }
    }

    private static class PreRGnssStatusTransport extends GnssStatus.Callback {
        final GnssStatusCompat.Callback mCallback;
        volatile Executor mExecutor;

        PreRGnssStatusTransport(GnssStatusCompat.Callback callback) {
            Preconditions.checkArgument(callback != null, "invalid null callback");
            this.mCallback = callback;
        }

        public void register(Executor executor) {
            Preconditions.checkArgument(executor != null, "invalid null executor");
            Preconditions.checkState(this.mExecutor == null);
            this.mExecutor = executor;
        }

        public void unregister() {
            this.mExecutor = null;
        }

        @Override // android.location.GnssStatus.Callback
        public void onStarted() {
            final Executor executor = this.mExecutor;
            if (executor == null) {
                return;
            }
            executor.execute(new Runnable() { // from class: androidx.core.location.LocationManagerCompat.PreRGnssStatusTransport.1
                @Override // java.lang.Runnable
                public void run() {
                    if (PreRGnssStatusTransport.this.mExecutor != executor) {
                        return;
                    }
                    PreRGnssStatusTransport.this.mCallback.onStarted();
                }
            });
        }

        @Override // android.location.GnssStatus.Callback
        public void onStopped() {
            final Executor executor = this.mExecutor;
            if (executor == null) {
                return;
            }
            executor.execute(new Runnable() { // from class: androidx.core.location.LocationManagerCompat.PreRGnssStatusTransport.2
                @Override // java.lang.Runnable
                public void run() {
                    if (PreRGnssStatusTransport.this.mExecutor != executor) {
                        return;
                    }
                    PreRGnssStatusTransport.this.mCallback.onStopped();
                }
            });
        }

        @Override // android.location.GnssStatus.Callback
        public void onFirstFix(final int ttffMillis) {
            final Executor executor = this.mExecutor;
            if (executor == null) {
                return;
            }
            executor.execute(new Runnable() { // from class: androidx.core.location.LocationManagerCompat.PreRGnssStatusTransport.3
                @Override // java.lang.Runnable
                public void run() {
                    if (PreRGnssStatusTransport.this.mExecutor != executor) {
                        return;
                    }
                    PreRGnssStatusTransport.this.mCallback.onFirstFix(ttffMillis);
                }
            });
        }

        @Override // android.location.GnssStatus.Callback
        public void onSatelliteStatusChanged(final GnssStatus status) {
            final Executor executor = this.mExecutor;
            if (executor == null) {
                return;
            }
            executor.execute(new Runnable() { // from class: androidx.core.location.LocationManagerCompat.PreRGnssStatusTransport.4
                @Override // java.lang.Runnable
                public void run() {
                    if (PreRGnssStatusTransport.this.mExecutor != executor) {
                        return;
                    }
                    PreRGnssStatusTransport.this.mCallback.onSatelliteStatusChanged(GnssStatusCompat.wrap(status));
                }
            });
        }
    }

    private static class GpsStatusTransport implements GpsStatus.Listener {
        final GnssStatusCompat.Callback mCallback;
        volatile Executor mExecutor;
        private final LocationManager mLocationManager;

        GpsStatusTransport(LocationManager locationManager, GnssStatusCompat.Callback callback) {
            Preconditions.checkArgument(callback != null, "invalid null callback");
            this.mLocationManager = locationManager;
            this.mCallback = callback;
        }

        public void register(Executor executor) {
            Preconditions.checkState(this.mExecutor == null);
            this.mExecutor = executor;
        }

        public void unregister() {
            this.mExecutor = null;
        }

        @Override // android.location.GpsStatus.Listener
        public void onGpsStatusChanged(int event) {
            GpsStatus gpsStatus;
            final Executor executor = this.mExecutor;
            if (executor == null) {
                return;
            }
            if (event == 1) {
                executor.execute(new Runnable() { // from class: androidx.core.location.LocationManagerCompat.GpsStatusTransport.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (GpsStatusTransport.this.mExecutor != executor) {
                            return;
                        }
                        GpsStatusTransport.this.mCallback.onStarted();
                    }
                });
                return;
            }
            if (event == 2) {
                executor.execute(new Runnable() { // from class: androidx.core.location.LocationManagerCompat.GpsStatusTransport.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (GpsStatusTransport.this.mExecutor != executor) {
                            return;
                        }
                        GpsStatusTransport.this.mCallback.onStopped();
                    }
                });
                return;
            }
            if (event != 3) {
                if (event == 4 && (gpsStatus = this.mLocationManager.getGpsStatus(null)) != null) {
                    final GnssStatusCompat gnssStatusCompatWrap = GnssStatusCompat.wrap(gpsStatus);
                    executor.execute(new Runnable() { // from class: androidx.core.location.LocationManagerCompat.GpsStatusTransport.4
                        @Override // java.lang.Runnable
                        public void run() {
                            if (GpsStatusTransport.this.mExecutor != executor) {
                                return;
                            }
                            GpsStatusTransport.this.mCallback.onSatelliteStatusChanged(gnssStatusCompatWrap);
                        }
                    });
                    return;
                }
                return;
            }
            GpsStatus gpsStatus2 = this.mLocationManager.getGpsStatus(null);
            if (gpsStatus2 != null) {
                final int timeToFirstFix = gpsStatus2.getTimeToFirstFix();
                executor.execute(new Runnable() { // from class: androidx.core.location.LocationManagerCompat.GpsStatusTransport.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (GpsStatusTransport.this.mExecutor != executor) {
                            return;
                        }
                        GpsStatusTransport.this.mCallback.onFirstFix(timeToFirstFix);
                    }
                });
            }
        }
    }

    private static class Api30Impl {
        private Api30Impl() {
        }

        static void getCurrentLocation(LocationManager locationManager, String provider, CancellationSignal cancellationSignal, Executor executor, final Consumer<Location> consumer) {
            locationManager.getCurrentLocation(provider, cancellationSignal != null ? (android.os.CancellationSignal) cancellationSignal.getCancellationSignalObject() : null, executor, new java.util.function.Consumer<Location>() { // from class: androidx.core.location.LocationManagerCompat.Api30Impl.1
                /* JADX DEBUG: Method merged with bridge method: accept(Ljava/lang/Object;)V */
                @Override // java.util.function.Consumer
                public void accept(Location location) {
                    consumer.accept(location);
                }
            });
        }
    }

    private static class Api28Impl {
        private Api28Impl() {
        }

        static boolean isLocationEnabled(LocationManager locationManager) {
            return locationManager.isLocationEnabled();
        }

        static String getGnssHardwareModelName(LocationManager locationManager) {
            return locationManager.getGnssHardwareModelName();
        }

        static int getGnssYearOfHardware(LocationManager locationManager) {
            return locationManager.getGnssYearOfHardware();
        }
    }

    private static final class CancellableLocationListener implements LocationListener {
        private Consumer<Location> mConsumer;
        private final Executor mExecutor;
        private final LocationManager mLocationManager;
        private final Handler mTimeoutHandler = new Handler(Looper.getMainLooper());
        Runnable mTimeoutRunnable;
        private boolean mTriggered;

        @Override // android.location.LocationListener
        public void onProviderEnabled(String provider) {
        }

        @Override // android.location.LocationListener
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        CancellableLocationListener(LocationManager locationManager, Executor executor, Consumer<Location> consumer) {
            this.mLocationManager = locationManager;
            this.mExecutor = executor;
            this.mConsumer = consumer;
        }

        public void cancel() {
            synchronized (this) {
                if (this.mTriggered) {
                    return;
                }
                this.mTriggered = true;
                cleanup();
            }
        }

        public void startTimeout(long timeoutMs) {
            synchronized (this) {
                if (this.mTriggered) {
                    return;
                }
                Runnable runnable = new Runnable() { // from class: androidx.core.location.LocationManagerCompat.CancellableLocationListener.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CancellableLocationListener.this.mTimeoutRunnable = null;
                        CancellableLocationListener.this.onLocationChanged((Location) null);
                    }
                };
                this.mTimeoutRunnable = runnable;
                this.mTimeoutHandler.postDelayed(runnable, timeoutMs);
            }
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String p) {
            onLocationChanged((Location) null);
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(final Location location) {
            synchronized (this) {
                if (this.mTriggered) {
                    return;
                }
                this.mTriggered = true;
                final Consumer<Location> consumer = this.mConsumer;
                this.mExecutor.execute(new Runnable() { // from class: androidx.core.location.LocationManagerCompat.CancellableLocationListener.2
                    @Override // java.lang.Runnable
                    public void run() {
                        consumer.accept(location);
                    }
                });
                cleanup();
            }
        }

        private void cleanup() {
            this.mConsumer = null;
            this.mLocationManager.removeUpdates(this);
            Runnable runnable = this.mTimeoutRunnable;
            if (runnable != null) {
                this.mTimeoutHandler.removeCallbacks(runnable);
                this.mTimeoutRunnable = null;
            }
        }
    }

    private static final class InlineHandlerExecutor implements Executor {
        private final Handler mHandler;

        InlineHandlerExecutor(Handler handler) {
            this.mHandler = (Handler) Preconditions.checkNotNull(handler);
        }

        @Override // java.util.concurrent.Executor
        public void execute(Runnable command) {
            if (Looper.myLooper() == this.mHandler.getLooper()) {
                command.run();
            } else if (!this.mHandler.post((Runnable) Preconditions.checkNotNull(command))) {
                throw new RejectedExecutionException(this.mHandler + " is shutting down");
            }
        }
    }
}
