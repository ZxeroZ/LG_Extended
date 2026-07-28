package com.lge.launcher3.swipehomescreen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class SwipeDownSubSwivelHomeDialog {
    private static final String TAG = "SwipeDownSubSwivelHomeDialog";
    private static DialogInterface.OnClickListener mSelectedListener;
    private static AlertDialog mSwipeDownSubSwivelHomeDialog;
    private static String sCancleAction;
    private static Context sLauncherContext;
    private static String sSwipeDownSubSwivelHomeTitle;

    private static void setLauncherContext(Context context) {
        try {
            sLauncherContext = context.createPackageContext("com.lge.launcher3", 0);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.i(TAG, "SwipeDownSubSwivelHomeDialog.setLauncherContext :" + e);
            e.printStackTrace();
        }
    }

    private static void getLauncherResource() {
        sSwipeDownSubSwivelHomeTitle = sLauncherContext.getResources().getString(R.string.menu_swipe_down_sub_swivel_home);
        sCancleAction = sLauncherContext.getResources().getString(R.string.cancel_action);
    }

    public static String getSwipeDownSubSwivelHomeText(Context context) {
        String[] swipeDownSubSwivelHomeTextList = getSwipeDownSubSwivelHomeTextList(context);
        String[] swipeDownSubSwivelHomeTextListForPreference = getSwipeDownSubSwivelHomeTextListForPreference(context);
        for (int i = 0; i < swipeDownSubSwivelHomeTextListForPreference.length; i++) {
            if (swipeDownSubSwivelHomeTextListForPreference[i].equals(HomeSettingsSharedPreferences.getSwipeDownSubSwivelHome(context))) {
                return swipeDownSubSwivelHomeTextList[i];
            }
        }
        for (int i2 = 0; i2 < swipeDownSubSwivelHomeTextListForPreference.length; i2++) {
            if (swipeDownSubSwivelHomeTextListForPreference[i2].equals(context.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_default))) {
                return swipeDownSubSwivelHomeTextList[i2];
            }
        }
        return context.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_default);
    }

    private static String[] getSwipeDownSubSwivelHomeTextList(Context context) {
        setLauncherContext(context);
        String str = String.format(sLauncherContext.getResources().getString(R.string.navigation_accessibility_notification_sub_swivel_home), sLauncherContext.getResources().getString(R.string.second_screen_title));
        return LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() ? new String[]{sLauncherContext.getResources().getString(R.string.search_guide_title), str, sLauncherContext.getResources().getString(R.string.sp_none_home_NORMAL)} : new String[]{str, sLauncherContext.getResources().getString(R.string.sp_none_home_NORMAL)};
    }

    public static String getSwipeDownSubSwivelHomeTextString(Context context) {
        return TextUtils.join(",", getSwipeDownSubSwivelHomeTextListForPreference(context));
    }

    private static String[] getSwipeDownSubSwivelHomeTextListForPreference(Context context) {
        setLauncherContext(context);
        return LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() ? new String[]{sLauncherContext.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_integrated_search), sLauncherContext.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_integrated_quick_setting), sLauncherContext.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_none)} : new String[]{sLauncherContext.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_integrated_quick_setting), sLauncherContext.getResources().getString(R.string.config_swipe_down_on_the_sub_swivel_home_none)};
    }

    public static void showSelectSwipeDownSubSwivelHomeDialog(final Context context, DialogInterface.OnClickListener selectedListener) {
        String str = TAG;
        LGLog.d(str, "showSelectSwipeDownSubSwivelHomeDialog()");
        AlertDialog alertDialog = mSwipeDownSubSwivelHomeDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            LGLog.i(str, "showing SelectSwipeDownSubSwivelHomeDialog.");
            return;
        }
        mSelectedListener = selectedListener;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        setLauncherContext(context);
        getLauncherResource();
        final String[] swipeDownSubSwivelHomeTextList = getSwipeDownSubSwivelHomeTextList(context);
        final String[] swipeDownSubSwivelHomeTextListForPreference = getSwipeDownSubSwivelHomeTextListForPreference(context);
        String swipeDownSubSwivelHome = HomeSettingsSharedPreferences.getSwipeDownSubSwivelHome(context);
        int length = swipeDownSubSwivelHomeTextListForPreference.length - 1;
        int i = 0;
        while (true) {
            if (i >= swipeDownSubSwivelHomeTextListForPreference.length) {
                break;
            }
            if (swipeDownSubSwivelHome.contentEquals(swipeDownSubSwivelHomeTextListForPreference[i])) {
                length = i;
                break;
            }
            i++;
        }
        builder.setTitle(sSwipeDownSubSwivelHomeTitle);
        builder.setSingleChoiceItems(swipeDownSubSwivelHomeTextList, length, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.swipehomescreen.SwipeDownSubSwivelHomeDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                String str2 = SwipeDownSubSwivelHomeDialog.TAG;
                CharSequence charSequence = swipeDownSubSwivelHomeTextList[which];
                LGLog.i(str2, "Click SingleChoiceItems - items[" + which + "] = " + ((Object) charSequence) + ", getSwipeDownSubSwivelHome = " + HomeSettingsSharedPreferences.getSwipeDownSubSwivelHome(context));
                if (swipeDownSubSwivelHomeTextListForPreference[which] != HomeSettingsSharedPreferences.getSwipeDownSubSwivelHome(context)) {
                    HomeSettingsSharedPreferences.putSwipeDownSubSwivelHome(context, String.valueOf(swipeDownSubSwivelHomeTextListForPreference[which]));
                    if (SwipeDownSubSwivelHomeDialog.mSelectedListener != null) {
                        SwipeDownSubSwivelHomeDialog.mSelectedListener.onClick(dialog, which);
                    }
                }
                SwipeDownSubSwivelHomeDialog.clearDialog();
            }
        });
        builder.setNegativeButton(sCancleAction, (DialogInterface.OnClickListener) null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.lge.launcher3.swipehomescreen.SwipeDownSubSwivelHomeDialog.2
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog) {
                LGLog.i(SwipeDownSubSwivelHomeDialog.TAG, "onDismiss");
                SwipeDownSubSwivelHomeDialog.clearDialog();
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        mSwipeDownSubSwivelHomeDialog = alertDialogCreate;
        alertDialogCreate.show();
    }

    public static void clearDialog() {
        LGLog.i(TAG, "clearDialog");
        AlertDialog alertDialog = mSwipeDownSubSwivelHomeDialog;
        if (alertDialog == null) {
            return;
        }
        try {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                LGLog.e(TAG, "clearDialog - dismiss error.", e);
            }
        } finally {
            mSwipeDownSubSwivelHomeDialog = null;
        }
    }
}
