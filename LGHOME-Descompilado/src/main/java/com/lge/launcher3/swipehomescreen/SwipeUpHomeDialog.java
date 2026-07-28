package com.lge.launcher3.swipehomescreen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class SwipeUpHomeDialog {
    private static final String TAG = "SwipeUpHomeDialog";
    private static boolean isShowSelectSwipeUpHomeDialog;
    private static DialogInterface.OnClickListener mSelectedListener;
    private static AlertDialog mSwipeUpHomeDialog;
    private static String sCancleAction;
    private static Context sLauncherContext;
    private static String sSwipeUpHomeTitle;

    private static void setLauncherContext(Context context) {
        try {
            sLauncherContext = context.createPackageContext("com.lge.launcher3", 0);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.i(TAG, "SwipeUpHomeDialog.setLauncherContext :" + e);
            e.printStackTrace();
        }
    }

    private static void getLauncherResource() {
        sSwipeUpHomeTitle = sLauncherContext.getResources().getString(R.string.menu_swipe_up_home);
        sCancleAction = sLauncherContext.getResources().getString(R.string.cancel_action);
    }

    public static String getSwipeUpHomeText(Context context) {
        return getSwipeUpHomeTextList(context)[HomeSettingsSharedPreferences.getSwipeUpHome(context)];
    }

    private static String[] getSwipeUpHomeTextList(Context context) {
        setLauncherContext(context);
        return new String[]{sLauncherContext.getResources().getString(R.string.search_guide_title), sLauncherContext.getResources().getString(R.string.sp_none_home_NORMAL)};
    }

    public static void showSelectSwipeUpHomeDialog(final Context context, DialogInterface.OnClickListener selectedListener) {
        String str = TAG;
        LGLog.d(str, "showSelectSwipeUpHomeDialog()");
        AlertDialog alertDialog = mSwipeUpHomeDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            LGLog.i(str, "showing SelectSwipeUpHomeDialog.");
            return;
        }
        mSelectedListener = selectedListener;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        setLauncherContext(context);
        getLauncherResource();
        final String[] swipeUpHomeTextList = getSwipeUpHomeTextList(context);
        builder.setTitle(sSwipeUpHomeTitle);
        builder.setSingleChoiceItems(swipeUpHomeTextList, HomeSettingsSharedPreferences.getSwipeUpHome(context), new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.swipehomescreen.SwipeUpHomeDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                String str2 = SwipeUpHomeDialog.TAG;
                CharSequence charSequence = swipeUpHomeTextList[which];
                LGLog.i(str2, "Click SingleChoiceItems - items[" + which + "] = " + ((Object) charSequence) + ", getSwipeUpHome = " + HomeSettingsSharedPreferences.getSwipeUpHome(context));
                if (which != HomeSettingsSharedPreferences.getSwipeUpHome(context)) {
                    HomeSettingsSharedPreferences.putSwipeUpHome(context, which);
                    if (SwipeUpHomeDialog.mSelectedListener != null) {
                        SwipeUpHomeDialog.mSelectedListener.onClick(dialog, which);
                    }
                }
                SwipeUpHomeDialog.clearDialog();
            }
        });
        builder.setNegativeButton(sCancleAction, (DialogInterface.OnClickListener) null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.lge.launcher3.swipehomescreen.SwipeUpHomeDialog.2
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog) {
                LGLog.i(SwipeUpHomeDialog.TAG, "onDismiss");
                SwipeUpHomeDialog.clearDialog();
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        mSwipeUpHomeDialog = alertDialogCreate;
        alertDialogCreate.show();
    }

    public static void clearDialog() {
        LGLog.i(TAG, "clearDialog");
        AlertDialog alertDialog = mSwipeUpHomeDialog;
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
            mSwipeUpHomeDialog = null;
        }
    }
}
