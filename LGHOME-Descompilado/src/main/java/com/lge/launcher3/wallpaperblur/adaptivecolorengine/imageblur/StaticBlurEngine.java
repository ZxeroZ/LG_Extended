package com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.logs.Logs;

/* JADX INFO: loaded from: classes.dex */
public class StaticBlurEngine {
    public static final float ALPHA_BLUR_THRESHOLD = 0.3f;
    public static final int ALPHA_BLUR_THRESHOLD_RADIUS = 18;
    public static final int MAX_RADIUS = 60;
    public static final int RT_MAX_RADIUS = 60;
    private static final int RT_SCALE_FACTOR = 2;
    public static final int SCALE_FACTOR = 2;
    public static final int SCALE_FACTOR_WIDGET = 1;
    public static final int SCALE_FACTOR_WIDGET_BLUR = 4;
    public static final int SCREENSHOT_RADIUS = 60;
    private static final int SCREENSHOT_SCALE_FACTOR = 1;
    public static final String TAG = "StaticBlurEngine";
    public static final int WIDGET_BLUR_RADIUS = 10;
    private static StaticBlurEngine sThis;
    private Bitmap mAlphaBlurredImage;
    private boolean mBlurAnimatorRunning = false;
    private int mBlurRadius;
    private Bitmap mBlurredImage;
    private Bitmap mBlurredImageForWidget;
    private Bitmap mCroppedAlphaBlurredImage;
    private Bitmap mCroppedSourceImage;
    private boolean mLGBlurEngineInitialized;
    private boolean mRealTimeBlurStarted;
    private Bitmap mRealtimeBlurImage;
    private Bitmap mScreenshotBlurImage;
    private Bitmap mSourceImage;

    public Bitmap getScreenshotBlurImage(Context context, int width, int height) {
        return null;
    }

    public void initializeLGBlurEngine(Context context) {
    }

    public boolean needRTBlurMaxRadius() {
        return false;
    }

    private StaticBlurEngine() {
    }

    public static StaticBlurEngine init() {
        if (sThis == null) {
            synchronized (StaticBlurEngine.class) {
                if (sThis == null) {
                    sThis = new StaticBlurEngine();
                }
            }
        }
        return sThis;
    }

    public static StaticBlurEngine getInstance() {
        StaticBlurEngine staticBlurEngine = sThis;
        if (staticBlurEngine != null) {
            return staticBlurEngine;
        }
        Logs.e("StaticBlurEngine is not initialized");
        throw new RuntimeException("StaticBlurEngine is not initialized");
    }

    public Bitmap getBlurImage(int x, int y, int width, int height) {
        if (this.mBlurredImage == null) {
            return null;
        }
        int i = x >> 2;
        int i2 = y >> 2;
        int i3 = width >> 2;
        int i4 = height >> 2;
        LGLog.i(TAG, String.format("getBlurImage %d x %d -> (%d,%d)", Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i), Integer.valueOf(i2)));
        return Bitmap.createBitmap(this.mBlurredImage, i, i2, i3, i4);
    }

    public Bitmap getWallpaperImage(int x, int y, int width, int height) {
        if (this.mSourceImage == null) {
            return null;
        }
        int i = x >> 2;
        int i2 = y >> 2;
        int i3 = width >> 2;
        int i4 = height >> 2;
        LGLog.i(TAG, String.format("getWallpaperImage %d x %d -> (%d,%d)", Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i), Integer.valueOf(i2)));
        return Bitmap.createBitmap(this.mSourceImage, i, i2, i3, i4);
    }

    public Bitmap getRealtimeBlurImage(int radius) {
        LGLog.i(TAG, String.format("getRealtimeBlurImage radius : %d", Integer.valueOf(radius)));
        if (radius <= 18) {
            return this.mCroppedAlphaBlurredImage;
        }
        Bitmap bitmap = this.mRealtimeBlurImage;
        if (bitmap != null) {
            return bitmap;
        }
        return null;
    }

    public void setBlurAnimatorRunning(boolean blurAnimatorRunning) {
        this.mBlurAnimatorRunning = blurAnimatorRunning;
    }

    public boolean isRealtimeBlurStarted() {
        return this.mRealTimeBlurStarted;
    }

    public boolean isRealtimeBlurInitialized() {
        return this.mLGBlurEngineInitialized;
    }

    public boolean isPowerSaveEnabled(Context context) {
        return Utilities.isPowerSaveMode(context);
    }

    public boolean startRealtimeBlur(Context context, int x, int y, int width, int height) {
        if (this.mBlurAnimatorRunning) {
            LGLog.i(TAG, "BlurAnimator Running skip to startRealtimeBlur");
            return false;
        }
        if (!this.mRealTimeBlurStarted) {
            int i = x >> 2;
            int i2 = y >> 2;
            int i3 = width >> 2;
            int i4 = height >> 2;
            LGLog.i(TAG, String.format("startRealtimeBlur %d x %d -> (%d,%d)", Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i), Integer.valueOf(i2)));
            this.mCroppedSourceImage = Bitmap.createBitmap(this.mSourceImage, i, i2, i3, i4);
            this.mCroppedAlphaBlurredImage = Bitmap.createBitmap(this.mAlphaBlurredImage, i, i2, i3, i4);
            this.mRealTimeBlurStarted = true;
            return true;
        }
        LGLog.e(TAG, "Realtime Blur already started");
        return false;
    }

    public void stopRealTimeBlur() {
        if (this.mRealTimeBlurStarted) {
            LGLog.i(TAG, "stopRealTimeBlur");
            this.mRealTimeBlurStarted = false;
        }
    }

    public void releaseLGBlurEngine() {
        String str = TAG;
        LGLog.i(str, "releaseLGBlurEngine");
        if (this.mRealTimeBlurStarted) {
            LGLog.w(str, "Not Finished Realtime Blur", new int[0]);
            return;
        }
        if (this.mLGBlurEngineInitialized) {
            stopRealTimeBlur();
            this.mLGBlurEngineInitialized = false;
        }
        this.mCroppedSourceImage = null;
        this.mCroppedAlphaBlurredImage = null;
        this.mRealtimeBlurImage = null;
        this.mScreenshotBlurImage = null;
    }

    public void blur(Context context, Bitmap image, int blurRadius) {
        blur(context, image, blurRadius, false);
        LGLog.i(TAG, "blur >>>> (1)");
    }

    public void blur(Context context, Bitmap image, int blurRadius, boolean isLiveWallpaper) {
        clear();
        setBlurRadius(60);
        if (needBlur()) {
            String str = TAG;
            LGLog.i(str, "blur >>>> (2)");
            int width = image.getWidth();
            int height = image.getHeight();
            int iPow = (int) Math.pow(2.0d, 2.0d);
            if (width < iPow || height < iPow) {
                return;
            }
            if (LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue()) {
                LGLog.i(str, "Change Wallpaper START");
                initializeLGBlurEngine(context);
                this.mSourceImage = Bitmap.createScaledBitmap(image, width >> 2, height >> 2, true);
                LGLog.i(str, "Change Wallpaper END");
                return;
            }
            this.mBlurredImage = BlurRenderScript.blur(context, Bitmap.createScaledBitmap(image, width >> 2, height >> 2, true), this.mBlurRadius);
            if (isLiveWallpaper) {
                return;
            }
            this.mBlurredImageForWidget = Bitmap.createScaledBitmap(BlurRenderScript.blur(context, Bitmap.createScaledBitmap(image, width >> 4, height >> 4, true), 10), width >> 1, height >> 1, true);
        }
    }

    public void blurOnlyLiveWallpaperWidget(Context context, Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int iPow = (int) Math.pow(2.0d, 2.0d);
        if (width < iPow || height < iPow) {
            return;
        }
        this.mBlurredImageForWidget = Bitmap.createScaledBitmap(BlurRenderScript.blur(context, Bitmap.createScaledBitmap(image, width >> 4, height >> 4, true), 10), width >> 1, height >> 1, true);
    }

    private void setBlurRadius(int blurRadius) {
        if (blurRadius > 60) {
            blurRadius = 60;
        }
        this.mBlurRadius = blurRadius;
    }

    public void clear() {
        LGLog.i(TAG, "clear");
        releaseLGBlurEngine();
        this.mSourceImage = null;
        this.mAlphaBlurredImage = null;
        if (this.mBlurredImage != null) {
            this.mBlurredImage = null;
        }
    }

    public boolean needBlur() {
        return this.mBlurRadius != 0;
    }

    public boolean hasBlurredImage() {
        return this.mBlurredImage != null;
    }

    public Point getBlurreImageSize() {
        if (this.mBlurredImage != null) {
            return new Point(this.mBlurredImage.getWidth(), this.mBlurredImage.getHeight());
        }
        return null;
    }

    public Bitmap getBlurredImageForWidget() {
        return this.mBlurredImageForWidget;
    }
}
