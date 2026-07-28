package com.android.launcher3.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class VibratorWrapper {
    public static final VibrationEffect EFFECT_CLICK;
    public static final MainThreadInitializedObject<VibratorWrapper> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.launcher3.util.-$$Lambda$uM4rwiZtImipLoI-eXUMAM6O0rY
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return new VibratorWrapper(context);
        }
    });
    public static final VibrationEffect OVERVIEW_HAPTIC;
    public static final String TAG = "VibratorWrapper";
    private final boolean mHasVibrator;
    private boolean mIsHapticFeedbackEnabled;
    private final Vibrator mVibrator;

    static {
        VibrationEffect vibrationEffectCreatePredefined = VibrationEffect.createPredefined(0);
        EFFECT_CLICK = vibrationEffectCreatePredefined;
        OVERVIEW_HAPTIC = vibrationEffectCreatePredefined;
    }

    public VibratorWrapper(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Vibrator.class);
        this.mVibrator = vibrator;
        boolean zHasVibrator = vibrator.hasVibrator();
        this.mHasVibrator = zHasVibrator;
        if (zHasVibrator) {
            final ContentResolver contentResolver = context.getContentResolver();
            this.mIsHapticFeedbackEnabled = isHapticFeedbackEnabled(contentResolver);
            contentResolver.registerContentObserver(Settings.System.getUriFor("haptic_feedback_enabled"), false, new ContentObserver(Executors.MAIN_EXECUTOR.getHandler()) { // from class: com.android.launcher3.util.VibratorWrapper.1
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange) {
                    VibratorWrapper vibratorWrapper = VibratorWrapper.this;
                    vibratorWrapper.mIsHapticFeedbackEnabled = vibratorWrapper.isHapticFeedbackEnabled(contentResolver);
                }
            });
            return;
        }
        this.mIsHapticFeedbackEnabled = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isHapticFeedbackEnabled(ContentResolver resolver) {
        return Settings.System.getInt(resolver, "haptic_feedback_enabled", 0) == 1;
    }

    public void vibrate(final VibrationEffect vibrationEffect) {
        if (this.mHasVibrator && this.mIsHapticFeedbackEnabled) {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.launcher3.util.-$$Lambda$VibratorWrapper$f_lMFgqSLhxcU_mGctqBrXQdWWE
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$vibrate$0$VibratorWrapper(vibrationEffect);
                }
            });
        }
    }

    public /* synthetic */ void lambda$vibrate$0$VibratorWrapper(VibrationEffect vibrationEffect) {
        this.mVibrator.vibrate(vibrationEffect);
    }

    public void vibrate(VibrationEffect vibrationEffect, View view) {
        vibrate(vibrationEffect, view, 1);
    }

    public void vibrate(VibrationEffect vibrationEffect, final View view, final int type) {
        if (view != null && this.mHasVibrator && this.mIsHapticFeedbackEnabled) {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.launcher3.util.-$$Lambda$VibratorWrapper$69kXzLvQruTFVotAH05WwIr52Ks
                @Override // java.lang.Runnable
                public final void run() {
                    VibratorWrapper.lambda$vibrate$1(view, type);
                }
            });
            return;
        }
        LGLog.i(TAG, "vibrate : failed. view = " + view + ", mHasVibrator = " + this.mHasVibrator + ", mIsHapticFeedbackEnabled = " + this.mIsHapticFeedbackEnabled);
    }

    static /* synthetic */ void lambda$vibrate$1(View view, int i) {
        if (view != null) {
            view.performHapticFeedback(i, 1);
        } else {
            LGLog.i(TAG, "vibrate : failed. view is null");
        }
    }
}
