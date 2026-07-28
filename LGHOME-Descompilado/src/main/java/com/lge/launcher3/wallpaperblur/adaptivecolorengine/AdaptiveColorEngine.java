package com.lge.launcher3.wallpaperblur.adaptivecolorengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManagerGlobal;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.colorextractor.ColorExtractor;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.logs.Logs;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes.dex */
public final class AdaptiveColorEngine {
    private static final int LIVE_WALLPAPER_CAPTURE_DURATION = 10000;
    private static final int START_CAPTURE = 1;
    private static final String TAG = "AdaptiveColorEngine";
    private static AdaptiveColorEngine sThis;
    private AdaptiveColor mAdaptiveColor;
    private Context mContext;
    private long mDuration;
    private HandlerThread mHandlerThread;
    private ArrayList<WeakReference<IAdaptiveColorEngineListener>> mImageEngineListeners;
    private boolean mIsValid;
    private Bitmap mSource;
    private long mStartTime;
    private StaticBlurEngine mStaticBlurEngine;
    private Handler mThreadHandler;
    private AtomicBoolean mIsRunning = new AtomicBoolean();
    private AtomicBoolean mIsLoaded = new AtomicBoolean();
    private Object mLock = new Object();

    public interface IAdaptiveColorEngineListener {
        void onAdaptiveColorChanged(AdaptiveColor adaptiveColor);
    }

    private AdaptiveColorEngine() {
    }

    public static AdaptiveColorEngine getInstance() {
        Logs.aassert(Looper.getMainLooper() == Looper.myLooper(), "use ImageEngin on Main Thread");
        if (sThis == null) {
            synchronized (AdaptiveColorEngine.class) {
                if (sThis == null) {
                    sThis = new AdaptiveColorEngine();
                }
            }
        }
        return sThis;
    }

    public void init(Context context) {
        Logs.setLogOn(TAG);
        Logs.aassert(!this.mIsValid, "ImageEngine is already intialized");
        this.mContext = context;
        this.mImageEngineListeners = new ArrayList<>();
        clearAdaptiveColor();
        this.mStaticBlurEngine = StaticBlurEngine.init();
        this.mIsValid = true;
        initLiveWallpaperCaptureThread();
    }

    public void clear() {
        if (this.mIsValid) {
            this.mIsValid = false;
            synchronized (this.mContext) {
                StaticBlurEngine staticBlurEngine = this.mStaticBlurEngine;
                if (staticBlurEngine != null) {
                    staticBlurEngine.clear();
                    this.mStaticBlurEngine = null;
                }
                ArrayList<WeakReference<IAdaptiveColorEngineListener>> arrayList = this.mImageEngineListeners;
                if (arrayList != null) {
                    Iterator<WeakReference<IAdaptiveColorEngineListener>> it = arrayList.iterator();
                    while (it.hasNext()) {
                        it.next().clear();
                    }
                    this.mImageEngineListeners.clear();
                    this.mImageEngineListeners = null;
                }
                Bitmap bitmap = this.mSource;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.mSource = null;
                }
            }
            this.mContext = null;
            synchronized (AdaptiveColorEngine.class) {
                sThis = null;
            }
        }
    }

    public void setImage(Bitmap image) {
        if (this.mIsValid) {
            LGLog.d(TAG, "setImage");
            if (image == null) {
                clearAdaptiveColor();
                notifyConfigurationChanged();
                return;
            }
            synchronized (this.mContext) {
                Bitmap bitmap = this.mSource;
                if (bitmap != null) {
                    bitmap.recycle();
                }
                Bitmap bitmapCopy = image.copy(image.getConfig(), false);
                this.mSource = bitmapCopy;
                if (bitmapCopy == null) {
                    return;
                }
                this.mAdaptiveColor.update(ColorExtractor.getInstance().extract(this.mSource));
                if (isLiveWallpaperCaptureRunning()) {
                    this.mStaticBlurEngine.blur(this.mContext, this.mSource, this.mAdaptiveColor.getBlurRadius(), true);
                } else {
                    this.mStaticBlurEngine.blur(this.mContext, this.mSource, this.mAdaptiveColor.getBlurRadius());
                }
                this.mSource.recycle();
                this.mSource = null;
                notifyConfigurationChanged();
            }
        }
    }

    public void updateTuner() {
        Logs.aassert(this.mIsValid, "ImageEngine is not in valid state");
        this.mAdaptiveColor.update();
        notifyConfigurationChanged();
    }

    private void clearAdaptiveColor() {
        if (this.mAdaptiveColor == null) {
            this.mAdaptiveColor = AdaptiveColor.getInstance();
        }
        this.mAdaptiveColor.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyConfigurationChanged() {
        if (this.mAdaptiveColor.isColored()) {
            if (Looper.getMainLooper() != Looper.myLooper()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.lge.launcher3.wallpaperblur.adaptivecolorengine.AdaptiveColorEngine.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AdaptiveColorEngine.this.notifyConfigurationChanged();
                    }
                });
                return;
            }
            ArrayList<WeakReference<IAdaptiveColorEngineListener>> arrayList = this.mImageEngineListeners;
            if (arrayList != null) {
                Iterator<WeakReference<IAdaptiveColorEngineListener>> it = arrayList.iterator();
                while (it.hasNext()) {
                    WeakReference<IAdaptiveColorEngineListener> next = it.next();
                    if (next != null) {
                        if (next.get() != null) {
                            next.get().onAdaptiveColorChanged(this.mAdaptiveColor);
                        } else {
                            it.remove();
                            next.clear();
                        }
                    }
                }
            }
        }
    }

    public void addListener(IAdaptiveColorEngineListener listener) {
        Logs.aassert(this.mIsValid, "ImageEngine is not in valid state");
        this.mImageEngineListeners.add(new WeakReference<>(listener));
    }

    public StaticBlurEngine getBlurEngine() {
        return this.mStaticBlurEngine;
    }

    public void setBlurImage(Bitmap image) {
        if (this.mIsValid) {
            synchronized (this.mContext) {
                Bitmap bitmap = this.mSource;
                if (bitmap != null) {
                    bitmap.recycle();
                }
                Bitmap bitmapCopy = image.copy(image.getConfig(), false);
                this.mSource = bitmapCopy;
                if (bitmapCopy == null) {
                    return;
                }
                this.mStaticBlurEngine.blur(this.mContext, bitmapCopy, this.mAdaptiveColor.getBlurRadius(), true);
                this.mSource.recycle();
                this.mSource = null;
            }
        }
    }

    private void initLiveWallpaperCaptureThread() {
        HandlerThread handlerThread = new HandlerThread("BlurCaptureThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mThreadHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: com.lge.launcher3.wallpaperblur.adaptivecolorengine.AdaptiveColorEngine.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    synchronized (AdaptiveColorEngine.this.mLock) {
                        boolean zBlurLiveWallpaperForWidget = AdaptiveColorEngine.this.blurLiveWallpaperForWidget();
                        if (System.currentTimeMillis() - AdaptiveColorEngine.this.mStartTime > AdaptiveColorEngine.this.mDuration) {
                            AdaptiveColorEngine.this.mIsRunning.set(false);
                            AdaptiveColorEngine.this.mThreadHandler.removeMessages(1);
                            LGLog.d(AdaptiveColorEngine.TAG, "captureLiveWallpaper completed");
                        } else {
                            if (AdaptiveColorEngine.this.mIsRunning.get()) {
                                sendEmptyMessageDelayed(1, zBlurLiveWallpaperForWidget ? 0L : 33L);
                            }
                        }
                    }
                }
            }
        };
    }

    public boolean isLiveWallpaperCaptureRunning() {
        return this.mIsRunning.get();
    }

    public boolean isLiveWallpaperCaptureLoaded() {
        return this.mIsLoaded.get();
    }

    public void startLiveWallpaperCapture() {
        synchronized (this.mLock) {
            LGLog.d(TAG, "startCaptureLiveWallpaper");
            this.mIsRunning.set(true);
            this.mIsLoaded.set(false);
            this.mStartTime = System.currentTimeMillis();
            this.mDuration = 10000L;
            this.mThreadHandler.sendEmptyMessage(1);
        }
    }

    public void stopLiveWallpaperCapture() {
        synchronized (this.mLock) {
            LGLog.d(TAG, "stopCaptureLiveWallpaper");
            this.mIsRunning.set(false);
            this.mThreadHandler.removeMessages(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean blurLiveWallpaperForWidget() {
        Bitmap bitmapScreenshotWallpaper;
        try {
            bitmapScreenshotWallpaper = WindowManagerGlobal.getWindowManagerService().screenshotWallpaper();
        } catch (Exception e) {
            LGLog.e(TAG, e.getMessage());
            bitmapScreenshotWallpaper = null;
        }
        if (bitmapScreenshotWallpaper == null) {
            return false;
        }
        this.mStaticBlurEngine.blurOnlyLiveWallpaperWidget(this.mContext, bitmapScreenshotWallpaper);
        this.mIsLoaded.set(true);
        return true;
    }
}
