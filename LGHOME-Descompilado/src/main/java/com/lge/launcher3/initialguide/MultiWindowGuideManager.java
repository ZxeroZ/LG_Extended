package com.lge.launcher3.initialguide;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class MultiWindowGuideManager {
    public static final String TAG = "MultiWindowGuideManager";
    private static MultiWindowGuideManager sInstance;
    private Context mContext;
    private boolean mIsAlreadyShownMultiWindowGuide;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private WindowManager mWindowManager;
    private View mMultiWindowGuideView = null;
    private Window window = null;

    public static MultiWindowGuideManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MultiWindowGuideManager(context);
        }
        return sInstance;
    }

    private MultiWindowGuideManager(Context context) {
        this.mContext = null;
        this.mWindowManager = null;
        this.mWindowLayoutParams = null;
        this.mIsAlreadyShownMultiWindowGuide = false;
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mWindowLayoutParams = new WindowManager.LayoutParams(-1, -1, 2038, 256, -3);
        this.mIsAlreadyShownMultiWindowGuide = SharedPreferencesManager.getBoolean(this.mContext, 0, SharedPreferencesConst.MultiWindowGuideKey.ALREADY_SHOWN, false);
    }

    public boolean hideGuide() {
        if (this.mMultiWindowGuideView == null) {
            return false;
        }
        Window window = this.window;
        if (window != null) {
            window.setNavigationBarColor(0);
            this.window = null;
        }
        if (this.mWindowManager == null) {
            this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        }
        LGLog.i(TAG, "hideGuide");
        this.mWindowManager.removeView(this.mMultiWindowGuideView);
        this.mMultiWindowGuideView = null;
        return true;
    }

    public void showGuide(Window mWindow) {
        if (this.mIsAlreadyShownMultiWindowGuide) {
            return;
        }
        hideGuide();
        this.window = mWindow;
        if (mWindow != null) {
            mWindow.setNavigationBarColor(this.mContext.getResources().getColor(R.color.dual_window_initial_guide_bg));
        }
        View viewInflate = View.inflate(this.mContext, R.layout.multiwindow_initial_guide, null);
        this.mMultiWindowGuideView = viewInflate;
        if (viewInflate != null) {
            LGLog.i(TAG, "showGuide");
            if (this.mWindowManager == null) {
                this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
            }
            this.mWindowManager.addView(this.mMultiWindowGuideView, this.mWindowLayoutParams);
            return;
        }
        LGLog.i(TAG, "Failed showing MultiWindowGuide");
    }

    public void saveMultiWindowGuideShown(boolean save) {
        LGLog.i(TAG, "saveMultiWindowGuideShown - " + save);
        this.mIsAlreadyShownMultiWindowGuide = save;
        SharedPreferencesManager.putBoolean(this.mContext, 0, SharedPreferencesConst.MultiWindowGuideKey.ALREADY_SHOWN, save);
    }

    public void onConfigurationChanged() {
        WindowManager windowManager;
        View view;
        if (this.mIsAlreadyShownMultiWindowGuide || (windowManager = this.mWindowManager) == null || (view = this.mMultiWindowGuideView) == null) {
            return;
        }
        windowManager.removeView(view);
        this.mMultiWindowGuideView = null;
        View viewInflate = View.inflate(this.mContext, R.layout.multiwindow_initial_guide, null);
        this.mMultiWindowGuideView = viewInflate;
        if (viewInflate != null) {
            this.mWindowManager.addView(viewInflate, this.mWindowLayoutParams);
        }
    }
}
