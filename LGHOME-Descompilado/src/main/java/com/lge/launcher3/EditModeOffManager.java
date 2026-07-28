package com.lge.launcher3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.SystemClock;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUtilFunctionReflect;

/* JADX INFO: loaded from: classes.dex */
public class EditModeOffManager {
    private static final float POCKET_LUX;
    private static final String TAG = "EditModeOffManager";
    private final Context mContext;
    private boolean mIsRunning = false;
    private SensorEventListener mLightListener;
    private SensorEventListener mProximityListener;
    private final Sensor mSensorLight;
    private final SensorManager mSensorManager;
    private final Sensor mSensorProximity;

    static {
        POCKET_LUX = Build.VERSION.SDK_INT > 26 ? 8.0f : 4.0f;
    }

    public EditModeOffManager(Context context) {
        this.mContext = context;
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        this.mSensorManager = sensorManager;
        this.mSensorProximity = sensorManager.getDefaultSensor(8);
        this.mSensorLight = sensorManager.getDefaultSensor(5);
        LGLog.i(TAG, "POCKET_LUX = " + POCKET_LUX);
    }

    public void start() {
        boolean z = this.mIsRunning;
        if (!z) {
            SensorEventListener sensorEventListener = new SensorEventListener() { // from class: com.lge.launcher3.EditModeOffManager.1
                @Override // android.hardware.SensorEventListener
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }

                @Override // android.hardware.SensorEventListener
                public void onSensorChanged(SensorEvent event) {
                    float f = event.values[0];
                    LGLog.d(EditModeOffManager.TAG, "onSensorChanged(): distance = " + f);
                    if (f > 0.0f) {
                        EditModeOffManager.this.setLightSensorEnabled(false);
                    } else {
                        EditModeOffManager.this.setLightSensorEnabled(true);
                    }
                }
            };
            this.mProximityListener = sensorEventListener;
            this.mSensorManager.registerListener(sensorEventListener, this.mSensorProximity, 2);
            this.mIsRunning = true;
            LGLog.i(TAG, "EditModeOffManager started");
            return;
        }
        LGLog.i(TAG, "EditModeOffManager is not started, IsRunning = " + z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLightSensorEnabled(boolean enabled) {
        if (enabled) {
            if (this.mLightListener == null) {
                SensorEventListener sensorEventListener = new SensorEventListener() { // from class: com.lge.launcher3.EditModeOffManager.2
                    @Override // android.hardware.SensorEventListener
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    }

                    @Override // android.hardware.SensorEventListener
                    public void onSensorChanged(SensorEvent event) {
                        float f = event.values[0];
                        LGLog.d(EditModeOffManager.TAG, "onSensorChanged(): lux = " + f);
                        if (f < EditModeOffManager.POCKET_LUX) {
                            LGLog.i(EditModeOffManager.TAG, "call goToSleepWithForce()");
                            LGUtilFunctionReflect.OsManagerReflect.goToSleepWithForce(EditModeOffManager.this.mContext, SystemClock.uptimeMillis(), 0);
                        }
                    }
                };
                this.mLightListener = sensorEventListener;
                this.mSensorManager.registerListener(sensorEventListener, this.mSensorLight, 2);
                return;
            }
            return;
        }
        SensorEventListener sensorEventListener2 = this.mLightListener;
        if (sensorEventListener2 != null) {
            this.mSensorManager.unregisterListener(sensorEventListener2);
            this.mLightListener = null;
        }
    }

    public void end() {
        if (this.mIsRunning) {
            SensorEventListener sensorEventListener = this.mLightListener;
            if (sensorEventListener != null) {
                this.mSensorManager.unregisterListener(sensorEventListener);
                this.mLightListener = null;
            }
            SensorEventListener sensorEventListener2 = this.mProximityListener;
            if (sensorEventListener2 != null) {
                this.mSensorManager.unregisterListener(sensorEventListener2);
                this.mProximityListener = null;
            }
            this.mIsRunning = false;
            LGLog.i(TAG, "EditModeOffManager stopped");
            return;
        }
        LGLog.i(TAG, "EditModeOffManager is not running");
    }

    public void destroy() {
        end();
    }
}
