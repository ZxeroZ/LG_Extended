package com.lge.launcher3.wallpapermotion;

import android.animation.ObjectAnimator;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperMotionManager implements SensorEventListener {
    private static final boolean DEBUG = false;
    private static final int MSG_UPDATE_OFFSET = 1;
    private static final float NS2S = 1.0E-9f;
    private static final String TAG = "WallpaperMotionManager";
    private final Context mContext;
    private final float mGatedGyroMaxDT;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private boolean mIsGatedGyro;
    private ContentObserver mMotionSettingsObserver;
    private float mParallaxRatio;
    private boolean mPause;
    private final BroadcastReceiver mPowerSaveModeReceiver;
    private float mPrevPosX;
    private float mPrevPosY;
    private float mPrevX;
    private float mPrevY;
    private final View mRootView;
    private final SensorManager mSensorManager;
    private float mTimestamp;
    private final WallpaperManager mWallpaperManager;
    private IBinder mToken = null;
    private boolean mIsRunning = false;
    private boolean mIsActivityRunning = false;
    private boolean mEnableParallax = true;
    private final Sensor mSensor = getSensor();

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private static class OffsetPos {
        float x;
        float y;

        OffsetPos(float posX, float posY) {
            this.x = posX;
            this.y = posY;
        }
    }

    public WallpaperMotionManager(Context context, View rootView) {
        this.mContext = context;
        this.mRootView = rootView;
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        this.mWallpaperManager = WallpaperManager.getInstance(context);
        HandlerThread handlerThread = new HandlerThread("WallpaperMotion");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: com.lge.launcher3.wallpapermotion.WallpaperMotionManager.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what != 1 || WallpaperMotionManager.this.mWallpaperManager == null || WallpaperMotionManager.this.mToken == null || msg.obj == null) {
                    return;
                }
                final OffsetPos offsetPos = (OffsetPos) msg.obj;
                WallpaperMotionManager.this.mWallpaperManager.setWallpaperOffsets(WallpaperMotionManager.this.mToken, offsetPos.x, offsetPos.y);
                if (LGHomeFeature.Config.FEATURE_USE_PARALLAX.getValue() && WallpaperMotionManager.this.mEnableParallax) {
                    WallpaperMotionManager.this.mRootView.post(new Runnable() { // from class: com.lge.launcher3.wallpapermotion.WallpaperMotionManager.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                                WallpaperMotionManager.this.mRootView.setTranslationX(0.0f);
                                WallpaperMotionManager.this.mRootView.setTranslationY(0.0f);
                            } else {
                                WallpaperMotionManager.this.mRootView.setTranslationX(WallpaperMotionManager.this.covertOffsetToPosition(offsetPos.x, WallpaperMotionManager.this.mRootView.getWidth()));
                                WallpaperMotionManager.this.mRootView.setTranslationY(WallpaperMotionManager.this.covertOffsetToPosition(offsetPos.y, WallpaperMotionManager.this.mRootView.getHeight()));
                            }
                        }
                    });
                }
            }
        };
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.wallpapermotion.WallpaperMotionManager.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                LGLog.i(WallpaperMotionManager.TAG, "onReceive(): mIsActivityRunning = " + WallpaperMotionManager.this.mIsActivityRunning + ", " + intent.getAction());
                if (WallpaperMotionManager.this.mIsActivityRunning) {
                    if (Utilities.isPowerSaveMode(context2)) {
                        WallpaperMotionManager.this.end(false);
                    } else {
                        WallpaperMotionManager.this.start();
                    }
                }
            }
        };
        this.mPowerSaveModeReceiver = broadcastReceiver;
        context.registerReceiver(broadcastReceiver, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED"));
        this.mParallaxRatio = context.getResources().getFloat(R.dimen.config_parallaxRatio);
        this.mGatedGyroMaxDT = context.getResources().getFloat(R.dimen.config_gated_gyro_max_dt);
        this.mMotionSettingsObserver = new ContentObserver(new Handler()) { // from class: com.lge.launcher3.wallpapermotion.WallpaperMotionManager.3
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                WallpaperMotionUtils.updateMotionEnabled(WallpaperMotionManager.this.mContext);
                LGLog.i(WallpaperMotionManager.TAG, "onChange(): isMotionEnabled = " + WallpaperMotionUtils.isMotionEnabled(WallpaperMotionManager.this.mContext));
            }
        };
        try {
            context.getContentResolver().registerContentObserver(Uri.parse(WallpaperMotionUtils.WALLPAPERPICKER_DATA_PROVIDER), true, this.mMotionSettingsObserver);
        } catch (SecurityException e) {
            e.printStackTrace();
            this.mMotionSettingsObserver = null;
        }
    }

    public void destroy() {
        end();
        this.mContext.unregisterReceiver(this.mPowerSaveModeReceiver);
        if (this.mMotionSettingsObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mMotionSettingsObserver);
        }
        this.mHandlerThread.quit();
    }

    public void setWindowToken(IBinder windowToken) {
        this.mToken = windowToken;
    }

    public void start() {
        if (WallpaperMotionUtils.isMotionEnabled(this.mContext)) {
            boolean zIsPowerSaveMode = Utilities.isPowerSaveMode(this.mContext);
            boolean zIsFixedWallpaper = WallpaperMotionUtils.isFixedWallpaper(this.mContext);
            if (!this.mIsRunning && !zIsPowerSaveMode && zIsFixedWallpaper && !LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                this.mSensorManager.registerListener(this, this.mSensor, 1);
                this.mIsRunning = true;
                this.mIsActivityRunning = true;
                this.mPrevY = 0.0f;
                this.mPrevX = 0.0f;
                this.mPrevPosY = 0.5f;
                this.mPrevPosX = 0.5f;
                this.mTimestamp = 0.0f;
                Handler handler = this.mHandler;
                handler.sendMessage(handler.obtainMessage(1, new OffsetPos(this.mPrevPosX, this.mPrevPosY)));
                LGLog.i(TAG, "WallpaperMotion started");
            } else {
                LGLog.i(TAG, "WallpaperMotion is not started, IsRunning = " + this.mIsRunning + ", isMotionEnabled = " + WallpaperMotionUtils.isMotionEnabled(this.mContext) + ", isPowerSaveMode = " + zIsPowerSaveMode + ", isFixedWallpaper = " + zIsFixedWallpaper);
                resetRootViewTranslation();
            }
        } else {
            LGLog.i(TAG, "isMotionEnabled: false");
            resetRootViewTranslation();
        }
        this.mPause = false;
    }

    public void resetRootViewTranslation() {
        if (LGHomeFeature.Config.FEATURE_USE_PARALLAX.getValue()) {
            if (Float.compare(this.mRootView.getTranslationX(), 0.0f) == 0 && Float.compare(this.mRootView.getTranslationY(), 0.0f) == 0) {
                return;
            }
            LGLog.i(TAG, "reset rootview translationXY");
            this.mRootView.setTranslationX(0.0f);
            this.mRootView.setTranslationY(0.0f);
        }
    }

    public void end() {
        end(true);
    }

    public void end(boolean pause) {
        if (this.mIsRunning) {
            this.mSensorManager.unregisterListener(this);
            this.mIsRunning = false;
            LGLog.i(TAG, "WallpaperMotion stopped");
        }
        if (pause) {
            this.mIsActivityRunning = false;
        }
    }

    public Point pause() {
        this.mPause = true;
        return getOffset();
    }

    public Point getOffset() {
        return new Point((int) ((this.mPrevPosX * 100.0f) - 50.0f), (int) ((this.mPrevPosY * 100.0f) - 50.0f));
    }

    public void resume() {
        this.mPause = false;
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent event) {
        if (this.mTimestamp != 0.0f && !this.mPause) {
            float f = (event.timestamp - this.mTimestamp) * NS2S;
            if (this.mIsGatedGyro && f > this.mGatedGyroMaxDT) {
                this.mTimestamp = 0.0f;
                return;
            }
            float f2 = event.values[0];
            this.mPrevX = Math.max(-90.0f, Math.min(90.0f, this.mPrevX + ((float) Math.toDegrees(event.values[1] * f))));
            this.mPrevY = Math.max(-90.0f, Math.min(90.0f, this.mPrevY + ((float) Math.toDegrees(f * f2))));
            if (this.mToken == null) {
                this.mToken = this.mRootView.getWindowToken();
            }
            if (this.mToken != null && this.mWallpaperManager != null) {
                float f3 = (this.mPrevX + 90.0f) / 180.0f;
                float f4 = (this.mPrevY + 90.0f) / 180.0f;
                if (f3 < 0.0f) {
                    f3 = 0.0f;
                } else if (f3 > 1.0f) {
                    f3 = 1.0f;
                }
                float f5 = f4 >= 0.0f ? f4 > 1.0f ? 1.0f : f4 : 0.0f;
                if (Math.abs(this.mPrevPosX - f3) > 0.01d || Math.abs(this.mPrevPosY - f5) > 0.01d) {
                    Handler handler = this.mHandler;
                    handler.sendMessage(handler.obtainMessage(1, new OffsetPos(f3, f5)));
                    this.mPrevPosX = f3;
                    this.mPrevPosY = f5;
                }
            }
        }
        this.mTimestamp = event.timestamp;
    }

    public boolean isRunning() {
        return this.mIsRunning;
    }

    public void setEnableParallax(boolean enable) {
        setEnableParallax(enable, true);
    }

    public void setEnableParallax(boolean enable, boolean animation) {
        float fCovertOffsetToPosition;
        if (LGHomeFeature.Config.FEATURE_USE_PARALLAX.getValue() && enable != this.mEnableParallax && this.mIsRunning) {
            this.mEnableParallax = enable;
            float fCovertOffsetToPosition2 = 0.0f;
            if (enable) {
                fCovertOffsetToPosition2 = covertOffsetToPosition(this.mPrevPosX, this.mRootView.getWidth());
                fCovertOffsetToPosition = covertOffsetToPosition(this.mPrevPosY, this.mRootView.getHeight());
            } else {
                fCovertOffsetToPosition = 0.0f;
            }
            if (animation) {
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this.mRootView, "translationX", fCovertOffsetToPosition2);
                ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(this.mRootView, "translationY", fCovertOffsetToPosition);
                objectAnimatorOfFloat.setDuration(100L);
                objectAnimatorOfFloat2.setDuration(100L);
                objectAnimatorOfFloat.start();
                objectAnimatorOfFloat2.start();
                return;
            }
            this.mRootView.setTranslationX(fCovertOffsetToPosition2);
            this.mRootView.setTranslationY(fCovertOffsetToPosition);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float covertOffsetToPosition(float offset, int len) {
        return (offset - 0.5f) * len * this.mParallaxRatio;
    }

    private Sensor getSensor() {
        Sensor defaultSensor = this.mSensorManager.getDefaultSensor(499898123);
        if (defaultSensor != null) {
            LGLog.i(TAG, "Sensor: LGSensor.TYPE_GATED_GYRO");
            this.mIsGatedGyro = true;
            return defaultSensor;
        }
        LGLog.i(TAG, "Sensor: Sensor.TYPE_GYROSCOPE");
        this.mIsGatedGyro = false;
        return this.mSensorManager.getDefaultSensor(4);
    }
}
