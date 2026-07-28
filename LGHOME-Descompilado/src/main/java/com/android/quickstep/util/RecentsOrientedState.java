package com.android.quickstep.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.BaseActivityInterface;
import com.android.systemui.shared.system.ConfigurationCompat;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import com.lge.systemservice.core.LGContext;
import com.lge.systemservice.core.PostureManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.IntConsumer;

/* JADX INFO: loaded from: classes.dex */
public final class RecentsOrientedState implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final boolean DEBUG = true;
    private static final int FLAG_HOME_ROTATION_ALLOWED_IN_PREFS = 4;
    private static final int FLAG_HOME_ROTATION_FORCE_ENABLED_FOR_TESTING = 128;
    private static final int FLAG_MULTIPLE_ORIENTATION_SUPPORTED_BY_ACTIVITY = 1;
    private static final int FLAG_MULTIPLE_ORIENTATION_SUPPORTED_BY_DENSITY = 2;
    private static final int FLAG_MULTIWINDOW_ROTATION_ALLOWED = 16;
    private static final int FLAG_ROTATION_WATCHER_ENABLED = 64;
    private static final int FLAG_ROTATION_WATCHER_SUPPORTED = 32;
    private static final int FLAG_SWIPE_UP_NOT_RUNNING = 256;
    private static final int FLAG_SYSTEM_ROTATION_ALLOWED = 8;
    private static final int MASK_MULTIPLE_ORIENTATION_SUPPORTED_BY_DEVICE = 3;
    private static final String TAG = "RecentsOrientedState";
    private static final int VALUE_ROTATION_WATCHER_ENABLED = 363;
    private Configuration mActivityConfiguration;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private int mFlags;
    private final OrientationEventListener mOrientationListener;
    private final PostureManager mPostureManager;
    private final SharedPreferences mSharedPrefs;
    private ContentObserver mSystemAutoRotateObserver = new ContentObserver(new Handler()) { // from class: com.android.quickstep.util.RecentsOrientedState.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            RecentsOrientedState.this.updateAutoRotateSetting();
        }
    };
    private PagedOrientationHandler mOrientationHandler = PagedOrientationHandler.PORTRAIT;
    private int mTouchRotation = 0;
    private int mDisplayRotation = 0;
    private int mRecentsActivityRotation = 0;
    private final Matrix mTmpMatrix = new Matrix();
    private int mPreviousRotation = 0;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SurfaceRotation {
    }

    public static int getRotationForUserDegreesRotated(float degrees, int currentRotation) {
        if (degrees == -1.0f) {
            return currentRotation;
        }
        if (currentRotation != 0) {
            if (currentRotation != 1) {
                if (currentRotation != 2) {
                    if (currentRotation == 3) {
                        if (degrees < 20 || (degrees > 340 && degrees < 360.0f)) {
                            return 0;
                        }
                        if (degrees > 160 && degrees < 180.0f) {
                            return 2;
                        }
                        if (degrees > 250 && degrees < 360.0f) {
                            return 1;
                        }
                    }
                } else {
                    if (degrees < 110) {
                        return 3;
                    }
                    if (degrees > 250) {
                        return 1;
                    }
                }
            } else {
                if (degrees < 200 && degrees > 90.0f) {
                    return 2;
                }
                if ((degrees > 340 && degrees < 360.0f) || (degrees >= 0.0f && degrees < 70)) {
                    return 0;
                }
                if (degrees > 70 && degrees < 180.0f) {
                    return 3;
                }
            }
        } else {
            if (degrees > 180.0f && degrees < 290) {
                return 1;
            }
            if (degrees < 180.0f && degrees > 70) {
                return 3;
            }
        }
        return currentRotation;
    }

    public RecentsOrientedState(Context context, BaseActivityInterface sizeStrategy, final IntConsumer rotationChangeListener) {
        int i = 0;
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mSharedPrefs = Utilities.getPrefs(context);
        this.mPostureManager = (PostureManager) new LGContext(context).getLGSystemService("postureservice");
        this.mOrientationListener = new OrientationEventListener(context) { // from class: com.android.quickstep.util.RecentsOrientedState.2
            @Override // android.view.OrientationEventListener
            public void onOrientationChanged(int degrees) {
                int rotationForUserDegreesRotated = RecentsOrientedState.getRotationForUserDegreesRotated(degrees, RecentsOrientedState.this.mPreviousRotation);
                if (rotationForUserDegreesRotated != RecentsOrientedState.this.mPreviousRotation) {
                    RecentsOrientedState.this.mPreviousRotation = rotationForUserDegreesRotated;
                    rotationChangeListener.accept(rotationForUserDegreesRotated);
                }
            }
        };
        if (!(sizeStrategy.mDisplayId == 4) && sizeStrategy.rotationSupportedByActivity) {
            i = 1;
        }
        this.mFlags = i;
        Resources resources = context.getResources();
        if ((resources.getConfiguration().smallestScreenWidthDp * resources.getDisplayMetrics().densityDpi) / DisplayMetrics.DENSITY_DEVICE_STABLE < 600) {
            this.mFlags |= 2;
        }
        this.mFlags |= 256;
        forceAllowRotationForTesting(true);
        initFlags();
    }

    public boolean setActivityConfiguration(Configuration activityConfiguration) {
        this.mActivityConfiguration = activityConfiguration;
        LGLog.d(TAG, "setActivityConfiguration : " + activityConfiguration + ", " + this);
        return update(this.mTouchRotation, this.mDisplayRotation);
    }

    public void setMultiWindowMode(boolean isMultiWindow) {
        setFlag(16, isMultiWindow);
    }

    public boolean setGestureActive(boolean isGestureActive) {
        setFlag(256, !isGestureActive);
        return update(this.mTouchRotation, this.mDisplayRotation);
    }

    public boolean update(int touchRotation, int displayRotation) {
        this.mRecentsActivityRotation = inferRecentsActivityRotation(displayRotation);
        this.mDisplayRotation = displayRotation;
        this.mTouchRotation = touchRotation;
        this.mPreviousRotation = touchRotation;
        PagedOrientationHandler pagedOrientationHandler = this.mOrientationHandler;
        PostureManager postureManager = this.mPostureManager;
        boolean z = postureManager != null && postureManager.getSwivelState() == 2;
        if (this.mRecentsActivityRotation == this.mTouchRotation || z || (canRecentsActivityRotate() && (this.mFlags & 16) != 0)) {
            this.mOrientationHandler = PagedOrientationHandler.PORTRAIT;
            Log.d(TAG, "current RecentsOrientedState: " + this);
        } else {
            int i = this.mTouchRotation;
            if (i == 1) {
                this.mOrientationHandler = PagedOrientationHandler.LANDSCAPE;
            } else if (i == 3) {
                this.mOrientationHandler = PagedOrientationHandler.SEASCAPE;
            } else {
                this.mOrientationHandler = PagedOrientationHandler.PORTRAIT;
            }
        }
        LGLog.d(TAG, "current RecentsOrientedState: " + this);
        return pagedOrientationHandler != this.mOrientationHandler;
    }

    private int inferRecentsActivityRotation(int displayRotation) {
        if (isRecentsActivityRotationAllowed()) {
            Configuration configuration = this.mActivityConfiguration;
            int windowConfigurationRotation = configuration == null ? displayRotation : ConfigurationCompat.getWindowConfigurationRotation(configuration);
            LGLog.d(TAG, "inferRecentsActivityRotation : result = " + windowConfigurationRotation + ", displayRotation = " + displayRotation + ", mActivityConfiguration = " + this.mActivityConfiguration);
            return windowConfigurationRotation;
        }
        LGLog.d(TAG, "inferRecentsActivityRotation 2 : 0");
        return 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0032  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x0014  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void setFlag(int r6, boolean r7) {
        /*
            r5 = this;
            boolean r0 = com.android.launcher3.testing.TestProtocol.sDisableSensorRotation
            r1 = 1
            r2 = 0
            r3 = 363(0x16b, float:5.09E-43)
            if (r0 != 0) goto L14
            int r0 = r5.mFlags
            r4 = r0 & 363(0x16b, float:5.09E-43)
            if (r4 != r3) goto L14
            r0 = r0 & 16
            if (r0 != 0) goto L14
            r0 = r1
            goto L15
        L14:
            r0 = r2
        L15:
            if (r7 == 0) goto L1d
            int r7 = r5.mFlags
            r6 = r6 | r7
            r5.mFlags = r6
            goto L23
        L1d:
            int r7 = r5.mFlags
            int r6 = ~r6
            r6 = r6 & r7
            r5.mFlags = r6
        L23:
            boolean r6 = com.android.launcher3.testing.TestProtocol.sDisableSensorRotation
            if (r6 != 0) goto L32
            int r6 = r5.mFlags
            r7 = r6 & 363(0x16b, float:5.09E-43)
            if (r7 != r3) goto L32
            r6 = r6 & 16
            if (r6 != 0) goto L32
            goto L33
        L32:
            r1 = r2
        L33:
            if (r0 == r1) goto L3f
            com.android.launcher3.util.LooperExecutor r6 = com.android.launcher3.util.Executors.UI_HELPER_EXECUTOR
            com.android.quickstep.util.-$$Lambda$RecentsOrientedState$uQWWzUC2gWf6Ghu_eaU5GSnVVDk r7 = new com.android.quickstep.util.-$$Lambda$RecentsOrientedState$uQWWzUC2gWf6Ghu_eaU5GSnVVDk
            r7.<init>()
            r6.execute(r7)
        L3f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.util.RecentsOrientedState.setFlag(int, boolean):void");
    }

    public /* synthetic */ void lambda$setFlag$0$RecentsOrientedState(boolean z) {
        LGLog.d(TAG, "setFlag : isRotationEnabled = " + z + ", " + this);
        if (z) {
            this.mOrientationListener.enable();
        } else {
            this.mOrientationListener.disable();
        }
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if ("pref_allowRotation".equals(s)) {
            updateHomeRotationSetting();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAutoRotateSetting() {
        setFlag(8, Settings.System.getInt(this.mContentResolver, "accelerometer_rotation", 1) == 1);
    }

    private void updateHomeRotationSetting() {
        setFlag(4, this.mSharedPrefs.getBoolean("pref_allowRotation", false));
    }

    private void initFlags() {
        setFlag(32, this.mOrientationListener.canDetectOrientation());
        updateAutoRotateSetting();
    }

    public void initListeners() {
        if (isMultipleOrientationSupportedByDevice()) {
            this.mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
            this.mContentResolver.registerContentObserver(Settings.System.getUriFor("accelerometer_rotation"), false, this.mSystemAutoRotateObserver);
        }
        initFlags();
    }

    public void destroyListeners() {
        if (isMultipleOrientationSupportedByDevice()) {
            this.mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
            this.mContentResolver.unregisterContentObserver(this.mSystemAutoRotateObserver);
        }
        setRotationWatcherEnabled(false);
    }

    public void forceAllowRotationForTesting(boolean forceAllow) {
        setFlag(128, forceAllow);
    }

    public int getDisplayRotation() {
        return this.mDisplayRotation;
    }

    public int getTouchRotation() {
        return this.mTouchRotation;
    }

    public int getRecentsActivityRotation() {
        return this.mRecentsActivityRotation;
    }

    public boolean isMultipleOrientationSupportedByDevice() {
        return (this.mFlags & 3) == 3;
    }

    public boolean isRecentsActivityRotationAllowed() {
        int i = this.mFlags;
        return ((i & 3) == 3 && (i & 148) == 0) ? false : true;
    }

    public boolean canRecentsActivityRotate() {
        return (this.mFlags & 8) != 0 && isRecentsActivityRotationAllowed();
    }

    public void setRotationWatcherEnabled(boolean isEnabled) {
        setFlag(64, isEnabled);
    }

    public float getFullScreenScaleAndPivot(Rect taskView, DeviceProfile dp, PointF outPivot) {
        Rect insets = dp.getInsets();
        float f = (dp.widthPx - insets.left) - insets.right;
        float f2 = (dp.heightPx - insets.top) - insets.bottom;
        if (WindowUtils.isHideNav(this.mContext) && !dp.isLandscape) {
            f2 += insets.bottom;
        }
        if (dp.isMultiWindowMode) {
            int i = SplitScreenBounds.INSTANCE.getSecondaryWindowBounds(this.mContext).availableSize.y;
            if (WindowUtils.isHideNav(this.mContext) && !dp.isLandscape) {
                i += dp.getInsets().bottom;
            }
            outPivot.set(r0.availableSize.x, i);
        } else {
            outPivot.set(f, f2);
        }
        float fMin = Math.min(outPivot.x / taskView.width(), outPivot.y / taskView.height());
        if (f > 0.0f) {
            fMin = (fMin * dp.widthPx) / f;
        }
        if (fMin == 1.0f) {
            outPivot.set(f / 2.0f, f2 / 2.0f);
        } else if (dp.isMultiWindowMode) {
            float f3 = 1.0f / (fMin - 1.0f);
            outPivot.set(((taskView.right * fMin) - f) * f3, ((taskView.bottom * fMin) - f2) * f3);
        } else {
            float f4 = fMin / (fMin - 1.0f);
            outPivot.set(taskView.left * f4, taskView.top * f4);
        }
        return fMin;
    }

    public PagedOrientationHandler getOrientationHandler() {
        return this.mOrientationHandler;
    }

    public void flipVertical(MotionEvent ev) {
        this.mTmpMatrix.setScale(1.0f, -1.0f);
        ev.transform(this.mTmpMatrix);
    }

    public void transformEvent(float degrees, MotionEvent ev, boolean inverse) {
        Matrix matrix = this.mTmpMatrix;
        if (inverse) {
            degrees = -degrees;
        }
        matrix.setRotate(degrees);
        ev.transform(this.mTmpMatrix);
    }

    public boolean isDisplayPhoneNatural() {
        int i = this.mDisplayRotation;
        return i == 0 || i == 2;
    }

    public static void postDisplayRotation(int displayRotation, float screenWidth, float screenHeight, Matrix out) {
        if (displayRotation == 1) {
            out.postRotate(270.0f);
            out.postTranslate(0.0f, screenWidth);
        } else if (displayRotation == 2) {
            out.postRotate(180.0f);
            out.postTranslate(screenHeight, screenWidth);
        } else {
            if (displayRotation != 3) {
                return;
            }
            out.postRotate(90.0f);
            out.postTranslate(screenHeight, 0.0f);
        }
    }

    public String toString() {
        boolean z = (this.mFlags & 8) != 0;
        return "[this=" + LoggerUtils.extractObjectNameAndAddress(super.toString()) + " mOrientationHandler=" + LoggerUtils.extractObjectNameAndAddress(this.mOrientationHandler.toString()) + " mDisplayRotation=" + this.mDisplayRotation + " mTouchRotation=" + this.mTouchRotation + " mRecentsActivityRotation=" + this.mRecentsActivityRotation + " isRecentsActivityRotationAllowed=" + isRecentsActivityRotationAllowed() + " mSystemRotation=" + z + " mFlags=" + this.mFlags + "]";
    }

    public DeviceProfile getLauncherDeviceProfile() {
        InvariantDeviceProfile invariantDeviceProfileLambda$get$0$MainThreadInitializedObject = InvariantDeviceProfile.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext);
        int i = this.mRecentsActivityRotation;
        if (i == 1 || i == 3) {
            return invariantDeviceProfileLambda$get$0$MainThreadInitializedObject.landscapeProfile;
        }
        return invariantDeviceProfileLambda$get$0$MainThreadInitializedObject.portraitProfile;
    }
}
