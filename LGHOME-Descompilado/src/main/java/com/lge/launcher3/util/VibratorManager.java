package com.lge.launcher3.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.os.SystemVibrator;
import android.os.Vibrator;
import android.provider.Settings;
import com.android.launcher3.Launcher;
import com.lge.launcher3.config.LauncherConst;

/* JADX INFO: loaded from: classes.dex */
public class VibratorManager {
    private static final long DURATION = 30;
    private static final int NO_REPEAT = -1;
    private static final String SYSCONFIG_VIBRATOR_DISABLED = "config_disableVibratorFeedback";
    private static final String SYSPROP_KEY_HAPTIC_FEEDBACK = "ro.device.hapticfeedback";
    private static final String TAG = "VibratorManager";
    private static VibratorManager sInstance;
    private long[] mHapticPattern;
    private boolean mIsSystemHapticFeedbackEnabled;
    private Vibrator mVibrator;

    public static VibratorManager getInstance(Context context) {
        if (context == null) {
            LGLog.e(TAG, "getInstance() : Context is null");
            return null;
        }
        if (sInstance == null) {
            sInstance = new VibratorManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public static void performHapticFeedback(Context context, int haptic_id) {
        LGLog.i(TAG, "performHapticFeedback id = " + haptic_id);
        ((Launcher) context).getWorkspace().performHapticFeedback(haptic_id, 1);
    }

    private VibratorManager(Context context) {
        this.mVibrator = null;
        this.mHapticPattern = null;
        this.mIsSystemHapticFeedbackEnabled = false;
        this.mVibrator = new SystemVibrator(context);
        this.mHapticPattern = getHapticPattern(context);
        this.mIsSystemHapticFeedbackEnabled = !isVibratorFeedbackDisabled(context);
    }

    private long[] getHapticPattern(Context context) {
        try {
            Resources resources = context.getResources();
            return ArrayUtils.convertIntToLongArray(resources.getIntArray(resources.getIdentifier("config_longPressVibePattern", LauncherConst.RESOURCE_ARRAY_TYPE, LauncherConst.PACKAGE_NAME_NATIVE)));
        } catch (Resources.NotFoundException e) {
            LGLog.i(TAG, String.format("getHapticPattern() : NotFoundException(%s)", e.toString()));
            return null;
        } catch (NullPointerException e2) {
            LGLog.i(TAG, String.format("getHapticPattern() : NullPointerException(%s)", e2.toString()));
            return null;
        }
    }

    private boolean isSystemHapticFeedbackEnabled() {
        try {
            String str = SystemProperties.get(SYSPROP_KEY_HAPTIC_FEEDBACK, "1");
            LGLog.i(TAG, String.format("isSystemHapticFeedbackEnabled() : %s is %s.", SYSPROP_KEY_HAPTIC_FEEDBACK, str));
            return str != null && str.equals("1");
        } catch (Resources.NotFoundException e) {
            LGLog.i(TAG, String.format("isSystemHapticFeedbackEnabled() : NotFoundException(%s)", e.toString()));
            return false;
        }
    }

    public void vibrateIfSystemHapticFeedbackEnabled() {
        if (this.mIsSystemHapticFeedbackEnabled) {
            long[] jArr = this.mHapticPattern;
            if (jArr != null) {
                this.mVibrator.vibrate(jArr, -1);
            } else {
                this.mVibrator.vibrate(DURATION);
            }
        }
    }

    public static boolean isVibratorFeedbackEnabled(Context context) {
        return !isVibratorFeedbackDisabled(context) && isSettingsHapticFeedbackEnabled(context);
    }

    private static boolean isSettingsHapticFeedbackEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "haptic_feedback_enabled", 1) != 0;
    }

    private static boolean isVibratorFeedbackDisabled(Context context) {
        boolean z;
        try {
            Resources resources = context.getResources();
            z = resources.getBoolean(resources.getIdentifier(SYSCONFIG_VIBRATOR_DISABLED, "bool", "com.lge"));
        } catch (Resources.NotFoundException e) {
            e = e;
            z = true;
        }
        try {
            LGLog.d(TAG, String.format("isVibratorFeedbackDisabled() : SYSCONFIG_VIBRATOR_DISABLED(%s)", Boolean.valueOf(z)));
        } catch (Resources.NotFoundException e2) {
            e = e2;
            LGLog.i(TAG, String.format("isVibratorFeedbackDisabled() : NotFoundException(%s)", e.toString()));
        }
        return z;
    }
}
