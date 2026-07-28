package com.lge.launcher3.swipehomescreen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.android.launcher3.Utilities;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class SwipeDownHomeDialog {
    private static final String TAG = "SwipeDownHomeDialog";
    private static boolean isShowSelectSwipeDownHomeDialog;
    private static DialogInterface.OnClickListener mSelectedListener;
    private static AlertDialog mSwipeDownHomeDialog;
    private static String sCancleAction;
    private static Context sLauncherContext;
    private static String sSwipeDownHomeTitle;

    private static void setLauncherContext(Context context) {
        try {
            sLauncherContext = context.createPackageContext("com.lge.launcher3", 0);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.i(TAG, "SwipeDownHomeDialog.setLauncherContext :" + e);
            e.printStackTrace();
        }
    }

    private static void getLauncherResource() {
        sSwipeDownHomeTitle = sLauncherContext.getResources().getString(R.string.menu_swipe_down_home);
        sCancleAction = sLauncherContext.getResources().getString(R.string.cancel_action);
    }

    public static String getSwipeDownHomeText(Context context) {
        return getSwipeDownHomeTextList(context)[HomeSettingsSharedPreferences.getSwipeDownHome(context)];
    }

    public static String getSwipeDownSwivelHomeText(Context context) {
        return getSwipeDownHomeTextList(context)[HomeSettingsSharedPreferences.getSwipeDownSwivelHome(context)];
    }

    public static String getSwipeDownHomeTextString(Context context) {
        return TextUtils.join(",", getSwipeDownHomeTextList(context));
    }

    private static String[] getSwipeDownHomeTextList(Context context) {
        setLauncherContext(context);
        return Utilities.supportIntegratedSearchOrSearchBySwipingDownHome(context) ? LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() ? new String[]{sLauncherContext.getResources().getString(R.string.search_guide_title), sLauncherContext.getResources().getString(R.string.navigation_accessibility_notification), sLauncherContext.getResources().getString(R.string.sp_none_home_NORMAL)} : new String[]{sLauncherContext.getResources().getString(R.string.google_inapps), sLauncherContext.getResources().getString(R.string.navigation_accessibility_notification), sLauncherContext.getResources().getString(R.string.sp_none_home_NORMAL)} : new String[]{sLauncherContext.getResources().getString(R.string.navigation_accessibility_notification), sLauncherContext.getResources().getString(R.string.sp_none_home_NORMAL)};
    }

    public static void showSelectSwipeDownHomeDialog(final Context context, DialogInterface.OnClickListener selectedListener) {
        String str = TAG;
        LGLog.d(str, "showSelectSwipeDownHomeDialog()");
        AlertDialog alertDialog = mSwipeDownHomeDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            LGLog.i(str, "showing SelectSwipeDownHomeDialog.");
            return;
        }
        mSelectedListener = selectedListener;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        setLauncherContext(context);
        getLauncherResource();
        final String[] swipeDownHomeTextList = getSwipeDownHomeTextList(context);
        builder.setTitle(sSwipeDownHomeTitle);
        builder.setSingleChoiceItems(swipeDownHomeTextList, HomeSettingsSharedPreferences.getSwipeDownHome(context), new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.swipehomescreen.SwipeDownHomeDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                String str2 = SwipeDownHomeDialog.TAG;
                CharSequence charSequence = swipeDownHomeTextList[which];
                LGLog.i(str2, "Click SingleChoiceItems - items[" + which + "] = " + ((Object) charSequence) + ", getSwipeDownHome = " + HomeSettingsSharedPreferences.getSwipeDownHome(context));
                if (which != HomeSettingsSharedPreferences.getSwipeDownHome(context)) {
                    HomeSettingsSharedPreferences.putSwipeDownHome(context, which);
                    if (SwipeDownHomeDialog.mSelectedListener != null) {
                        SwipeDownHomeDialog.mSelectedListener.onClick(dialog, which);
                    }
                }
                SwipeDownHomeDialog.clearDialog();
            }
        });
        builder.setNegativeButton(sCancleAction, (DialogInterface.OnClickListener) null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.lge.launcher3.swipehomescreen.SwipeDownHomeDialog.2
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog) {
                LGLog.i(SwipeDownHomeDialog.TAG, "onDismiss");
                SwipeDownHomeDialog.clearDialog();
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        mSwipeDownHomeDialog = alertDialogCreate;
        alertDialogCreate.show();
    }

    public static void showSelectSwipeDownSwivelHomeDialog(final Context context, DialogInterface.OnClickListener selectedListener) {
        String str = TAG;
        LGLog.d(str, "showSelectSwipeSwivelDownHomeDialog()");
        AlertDialog alertDialog = mSwipeDownHomeDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            LGLog.i(str, "showing SelectSwipeDownHomeDialog.");
            return;
        }
        mSelectedListener = selectedListener;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        setLauncherContext(context);
        getLauncherResource();
        final String[] swipeDownHomeTextList = getSwipeDownHomeTextList(context);
        builder.setTitle(sSwipeDownHomeTitle);
        builder.setSingleChoiceItems(swipeDownHomeTextList, HomeSettingsSharedPreferences.getSwipeDownSwivelHome(context), new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.swipehomescreen.SwipeDownHomeDialog.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                String str2 = SwipeDownHomeDialog.TAG;
                CharSequence charSequence = swipeDownHomeTextList[which];
                LGLog.i(str2, "Click SingleChoiceItems - items[" + which + "] = " + ((Object) charSequence) + ", getSwipeDownSwivelHome = " + HomeSettingsSharedPreferences.getSwipeDownSwivelHome(context));
                if (which != HomeSettingsSharedPreferences.getSwipeDownSwivelHome(context)) {
                    HomeSettingsSharedPreferences.putSwipeDownSwivelHome(context, which);
                    if (SwipeDownHomeDialog.mSelectedListener != null) {
                        SwipeDownHomeDialog.mSelectedListener.onClick(dialog, which);
                    }
                }
                SwipeDownHomeDialog.clearDialog();
            }
        });
        builder.setNegativeButton(sCancleAction, (DialogInterface.OnClickListener) null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.lge.launcher3.swipehomescreen.SwipeDownHomeDialog.4
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog) {
                LGLog.i(SwipeDownHomeDialog.TAG, "onDismiss");
                SwipeDownHomeDialog.clearDialog();
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        mSwipeDownHomeDialog = alertDialogCreate;
        alertDialogCreate.show();
    }

    public static void clearDialog() {
        LGLog.i(TAG, "clearDialog");
        AlertDialog alertDialog = mSwipeDownHomeDialog;
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
            mSwipeDownHomeDialog = null;
        }
    }
}
