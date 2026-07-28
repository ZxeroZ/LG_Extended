package com.lge.launcher3.initialguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import java.util.Calendar;
import kotlin.jvm.internal.LongCompanionObject;

/* JADX INFO: loaded from: classes.dex */
public class InitialGuideManager {
    public static final boolean DEBUG = false;
    private static final boolean DEBUG_ALWAYS_SHOW_INITIAL_GUIDE = false;
    private static final long DONT_SHOW_AGAIN_FLAG = 0;
    public static final String TAG = "InitialGuideManager";
    private static final int UNBLOCK_DURATION_TO_START_ACTIVITY_AGAIN = 500;
    private static Context sContext;
    private static InitialGuideManager sInstance;
    private long mFirstShownTime;
    private boolean mIsAlreadyShown;
    private boolean mIsInitialGuideActivityStarted;
    private boolean mIsReadyToShow = false;

    public static InitialGuideManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new InitialGuideManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private InitialGuideManager(Context context) {
        this.mIsAlreadyShown = false;
        this.mIsInitialGuideActivityStarted = false;
        String str = TAG;
        LGLog.i(str, "Create a new InitialGuideManager instance.");
        sContext = context;
        String str2 = LGFeatureConfig.FEATURE_OPERATOR;
        boolean value = LGHomeFeature.Config.FEATURE_USE_SKIP_INITIALGUIDE.getValue();
        boolean zSkipInitialguideForDeviceProfile = ManagedProfileUtils.skipInitialguideForDeviceProfile(context);
        if (!str2.equals("VZW") || value || zSkipInitialguideForDeviceProfile) {
            LGLog.i(str, "Initial guide will not be provided - skip(" + value + ", " + zSkipInitialguideForDeviceProfile + ") ,Operator : " + str2);
            this.mIsInitialGuideActivityStarted = true;
            saveInitialGuideShown(true);
            return;
        }
        this.mIsAlreadyShown = SharedPreferencesManager.getBoolean(sContext, 0, SharedPreferencesConst.InitialGuideKey.ALREADY_SHOWN, false);
    }

    public boolean showInitialGuide(Activity activity) {
        if (this.mIsInitialGuideActivityStarted) {
            LGLog.i(TAG, "showInitialGuide() : InitialGuideActivity is already started.");
            return false;
        }
        Intent intent = new Intent(activity, (Class<?>) InitialGuideActivity.class);
        activity.startActivity(intent);
        LGLog.i(TAG, String.format("showInitialGuide() : Operator -> " + LGFeatureConfig.FEATURE_OPERATOR + " ,Start InitialGuideActivity(%s)", intent));
        setInitialGuideActivityIsStarted(true);
        return true;
    }

    public boolean isAlreadyShown() {
        return this.mIsAlreadyShown;
    }

    public boolean isReadyToShow() {
        return this.mIsReadyToShow;
    }

    public void setReadyToShow(boolean ready) {
        LGLog.i(TAG, String.format("setReadyToShow(%s)", Boolean.valueOf(ready)));
        this.mIsReadyToShow = ready;
    }

    public void saveInitialGuideShown(boolean save) {
        LGLog.i(TAG, String.format("saveInitialGuideShown(%s)", Boolean.valueOf(save)));
        this.mIsAlreadyShown = save;
        SharedPreferencesManager.putBoolean(sContext, 0, SharedPreferencesConst.InitialGuideKey.ALREADY_SHOWN, save);
    }

    public void setInitialGuideActivityIsStarted(boolean start) {
        LGLog.i(TAG, String.format("setInitialGuideActivityIsStarted(%s)", Boolean.valueOf(start)));
        this.mIsInitialGuideActivityStarted = start;
        if (start) {
            new Handler().postDelayed(new Runnable() { // from class: com.lge.launcher3.initialguide.InitialGuideManager.1
                @Override // java.lang.Runnable
                public void run() {
                    LGLog.i(InitialGuideManager.TAG, "setInitialGuideActivityIsStarted() : Time out to block to start InitialGuideActivity.");
                    InitialGuideManager.this.setInitialGuideActivityIsStarted(false);
                }
            }, 500L);
        }
    }

    public void saveFirstShownTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        long j = SharedPreferencesManager.getLong(sContext, 0, SharedPreferencesConst.InitialGuideKey.FIRST_SHOWN_TIME, LongCompanionObject.MAX_VALUE);
        this.mFirstShownTime = j;
        if (j != LongCompanionObject.MAX_VALUE) {
            LGLog.i(TAG, String.format("Initial Guide doesn't show again any more", new Object[0]));
            SharedPreferencesManager.putLong(sContext, 0, SharedPreferencesConst.InitialGuideKey.FIRST_SHOWN_TIME, 0L);
        } else {
            LGLog.i(TAG, String.format("saveInitialGuideShownFistShownTime(%d)", Long.valueOf(calendar.getTimeInMillis())));
            SharedPreferencesManager.putLong(sContext, 0, SharedPreferencesConst.InitialGuideKey.FIRST_SHOWN_TIME, calendar.getTimeInMillis());
        }
    }

    public void checkNeedShowAgain() {
        new Thread(new Runnable() { // from class: com.lge.launcher3.initialguide.InitialGuideManager.2
            @Override // java.lang.Runnable
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                InitialGuideManager.this.mFirstShownTime = SharedPreferencesManager.getLong(InitialGuideManager.sContext, 0, SharedPreferencesConst.InitialGuideKey.FIRST_SHOWN_TIME, LongCompanionObject.MAX_VALUE);
                if (InitialGuideManager.this.mFirstShownTime == LongCompanionObject.MAX_VALUE || InitialGuideManager.this.mFirstShownTime == 0) {
                    return;
                }
                if (Math.abs(InitialGuideManager.this.mFirstShownTime - calendar.getTimeInMillis()) > InitialGuideManager.sContext.getResources().getInteger(R.integer.config_initial_guide_show_again_time) * 60 * 60 * 1000) {
                    LGLog.i(InitialGuideManager.TAG, String.format("InitialGuide will be shown again, over %d hours", Integer.valueOf(InitialGuideManager.sContext.getResources().getInteger(R.integer.config_initial_guide_show_again_time))));
                    SharedPreferencesManager.putLong(InitialGuideManager.sContext, 0, SharedPreferencesConst.InitialGuideKey.FIRST_SHOWN_TIME, 0L);
                    InitialGuideManager.this.setInitialGuideActivityIsStarted(false);
                    InitialGuideManager.this.saveInitialGuideShown(false);
                    InitialGuideManager.this.setReadyToShow(true);
                }
            }
        }).start();
    }

    public void destroy() {
        sInstance = null;
        sContext = null;
    }
}
