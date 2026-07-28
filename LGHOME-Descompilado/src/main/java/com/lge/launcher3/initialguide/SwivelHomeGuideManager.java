package com.lge.launcher3.initialguide;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.lge.content.LocalBroadcastManager;
import com.lge.launcher3.R;
import com.lge.launcher3.quickstep.ActivityManagerWrapperEx;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class SwivelHomeGuideManager {
    public static final String ACTION_SWIVEL_GUIDE_MAIN_FINISH = "com.lge.launcher3.intent.action.swivel_guide_main_finish";
    public static final String ACTION_SWIVEL_GUIDE_MAIN_MOVE_NEXT_PAGE = "com.lge.launcher3.intent.action.swivel_guide_main_move_next_page";
    public static final String ACTION_SWIVEL_GUIDE_MAIN_MOVE_PREVIOUS_PAGE = "com.lge.launcher3.intent.action.swivel_guide_main_move_previous_page";
    public static final String ACTION_SWIVEL_GUIDE_SUB_FINISH = "com.lge.launcher3.intent.action.swivel_guide_sub_finish";
    public static final String ACTION_SWIVEL_GUIDE_SUB_MOVE_NEXT_PAGE = "com.lge.launcher3.intent.action.swivel_guide_sub_move_next_page";
    public static final String ACTION_SWIVEL_GUIDE_SUB_MOVE_PREVIOUS_PAGE = "com.lge.launcher3.intent.action.swivel_guide_sub_move_previous_page";
    public static final String TAG = "SwivelHomeGuideManager";
    public static final int TRY_MAX_COUNT = 2;
    public static final int TRY_SHOW_GUIDE = 1;
    private static Activity keepActivity;
    public static Handler mHander;
    private static SwivelHomeGuideManager sInstance;
    private static int tryCount;
    private Context mContext;
    private boolean mIsAlreadyShownSwivelHomeGuide;
    private WindowManager mWindowManager = null;
    private View mSwivelHomeGuideView = null;
    private WindowManager.LayoutParams mWindowLayoutParams = null;

    public static SwivelHomeGuideManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SwivelHomeGuideManager(context);
        }
        return sInstance;
    }

    private SwivelHomeGuideManager(Context context) {
        this.mContext = null;
        this.mIsAlreadyShownSwivelHomeGuide = false;
        this.mContext = context;
        this.mIsAlreadyShownSwivelHomeGuide = SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.SwivelHomeGuideKey.ALREADY_SHOWN, false);
        mHander = new Handler() { // from class: com.lge.launcher3.initialguide.SwivelHomeGuideManager.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                LGLog.i(SwivelHomeGuideManager.TAG, "handleMessage() msg.what = " + msg.what + ", tryCount = " + SwivelHomeGuideManager.tryCount);
                if (SwivelHomeGuideManager.this.checkTopActivity() || SwivelHomeGuideManager.tryCount > 2) {
                    SwivelHomeGuideManager.this.sendIntentShowingGuideFromDefaultDisplay(SwivelHomeGuideManager.keepActivity);
                } else {
                    sendEmptyMessageDelayed(1, 150L);
                    SwivelHomeGuideManager.tryCount++;
                }
            }
        };
    }

    public boolean hideGuide() {
        String str = TAG;
        LGLog.d(str, "hideGuide() ");
        if (SharedPreferencesManager.getBoolean(this.mContext, 0, SharedPreferencesConst.SwivelHomeGuideFromSettingsKey.ALREADY_SHOWN_FROM_SETTINGS, false)) {
            LGLog.i(str, "hideGuide() swivel home initial guide is shown from settings");
            return false;
        }
        sendIntentHideGuide();
        return true;
    }

    public boolean endSwivel() {
        LGLog.d(TAG, "endSwivel() ");
        sendIntentHideGuide();
        return true;
    }

    public void showGuide(Activity activity) {
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            String str = TAG;
            LGLog.d(str, "showGuide() ");
            if (this.mIsAlreadyShownSwivelHomeGuide || SharedPreferencesManager.getBoolean(this.mContext, 0, SharedPreferencesConst.SwivelHomeGuideKey.ALREADY_SHOWN, false)) {
                LGLog.i(str, "showGuide() swivel home initial guide is already shown");
                return;
            }
            if (SharedPreferencesManager.getBoolean(this.mContext, 0, SharedPreferencesConst.SwivelHomeGuideFromSettingsKey.ALREADY_SHOWN_FROM_SETTINGS, false)) {
                LGLog.i(str, "showGuide() swivel home initial guide is already shown from settings");
                return;
            }
            if (!checkTopActivity()) {
                if (mHander.hasMessages(1)) {
                    return;
                }
                keepActivity = activity;
                mHander.sendEmptyMessageDelayed(1, 150L);
                tryCount++;
                return;
            }
            sendIntentShowingGuideFromDefaultDisplay(activity);
        }
    }

    public void showGuideFromSettings(Activity activity, int displayId) {
        if (activity.isInMultiWindowMode()) {
            Toast.makeText(activity, R.string.cannot_open_in_multi_or_popup_window, 0).show();
            LGLog.i(TAG, "Skip to show the swivel home initial guide in multi window or popup window");
            return;
        }
        LGLog.d(TAG, "showGuideFromSettings() displayId = " + displayId);
        SharedPreferencesManager.putBoolean(this.mContext, 0, SharedPreferencesConst.SwivelHomeGuideFromSettingsKey.ALREADY_SHOWN_FROM_SETTINGS, true);
        if (displayId != 0) {
            sendIntentShowingGuideFromSecondaryDisplay(activity);
        } else {
            sendIntentShowingGuideFromDefaultDisplay(activity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendIntentShowingGuideFromDefaultDisplay(Activity activity) {
        mHander.removeMessages(1);
        keepActivity = null;
        tryCount = 0;
        if (activity == null) {
            LGLog.i(TAG, "sendIntentShowingGuideFromDefaultDisplay() activity = " + activity);
            return;
        }
        activity.startActivity(new Intent(activity, (Class<?>) SwivelHomeGuideMainActivity.class));
        Intent intent = new Intent(activity, (Class<?>) SwivelHomeGuideSubActivity.class);
        intent.addFlags(268435456);
        intent.addFlags(134217728);
        ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
        activityOptionsMakeBasic.setLaunchDisplayId(4);
        activity.startActivity(intent, activityOptionsMakeBasic.toBundle());
    }

    private void sendIntentShowingGuideFromSecondaryDisplay(Activity activity) {
        mHander.removeMessages(1);
        keepActivity = null;
        tryCount = 0;
        if (activity == null) {
            LGLog.i(TAG, "sendIntentShowingGuideFromSecondaryDisplay() activity = " + activity);
            return;
        }
        activity.startActivity(new Intent(activity, (Class<?>) SwivelHomeGuideSubActivity.class));
        Intent intent = new Intent(activity, (Class<?>) SwivelHomeGuideMainActivity.class);
        intent.addFlags(268435456);
        intent.addFlags(134217728);
        ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
        activityOptionsMakeBasic.setLaunchDisplayId(0);
        activity.startActivity(intent, activityOptionsMakeBasic.toBundle());
    }

    private void sendIntentHideGuide() {
        LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(new Intent(ACTION_SWIVEL_GUIDE_MAIN_FINISH));
        LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(new Intent(ACTION_SWIVEL_GUIDE_SUB_FINISH));
        SharedPreferencesManager.putBoolean(this.mContext, 0, SharedPreferencesConst.SwivelHomeGuideFromSettingsKey.ALREADY_SHOWN_FROM_SETTINGS, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkTopActivity() {
        if (ActivityManagerWrapperEx.getInstance().getTopActivityDisplay(4) != null) {
            return true;
        }
        LGLog.i(TAG, "checkTopActivity() Top Activity on second screen is null.");
        return false;
    }
}
