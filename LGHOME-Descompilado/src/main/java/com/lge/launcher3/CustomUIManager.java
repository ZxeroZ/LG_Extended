package com.lge.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.widget.Toast;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class CustomUIManager {
    public static final boolean DEBUG = false;
    private static final String FILENAME = "com.lge.launcher3.custom_ui_pref";
    public static String KEY_ANGLE_THRESHOLD_ASSISTANT = "test_angle_threshold_assistant";
    public static String KEY_DRAG_DIST_THRESHOLD_ASSISTANT = "test_drag_dist_threshold_assistant";
    public static String KEY_FLING_ASSISTANT = "test_fling_assistant";
    public static String KEY_FLING_DIST_THRESHOLD_ASSISTANT = "test_fling_dist_threshold_assistant";
    public static String KEY_SCALED_TOUCHSLOP = "test_scaled_touch_slop";
    public static String KEY_TOAST_ASSISTANT = "test_toast_assistant";
    public static String KEY_TOUCHSLOP = "test_touch_slop";
    public static String KEY_WORKSPACE_PAGE_SPACING = "test_workspace_page_spacing";
    private static final String TAG = "CustomUIManager";
    private static final CustomUIManager sInstance = new CustomUIManager();
    private int mAngleThresholdOfAssistant;
    private Context mContext;
    private float mDragDistThresholdOfAssistant;
    private float mFlingDistThresholdOfAssistant;
    private int mScaledTouchSlop;
    private SharedPreferences mSharedPreferences;
    Toast mToast;
    private int mTouchSlop;
    private boolean mUseToastOfAssistant;
    private int mWorkspacePageSpacing;
    private boolean mUseFlingOfAssistant = true;
    public SharedPreferences.OnSharedPreferenceChangeListener mChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.lge.launcher3.CustomUIManager.1
        @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            CustomUIManager.this.mTouchSlop = sharedPreferences.getInt(CustomUIManager.KEY_TOUCHSLOP, 0);
            CustomUIManager.this.mScaledTouchSlop = sharedPreferences.getInt(CustomUIManager.KEY_SCALED_TOUCHSLOP, 0);
            CustomUIManager.this.mWorkspacePageSpacing = sharedPreferences.getInt(CustomUIManager.KEY_WORKSPACE_PAGE_SPACING, 0);
            CustomUIManager.this.mDragDistThresholdOfAssistant = sharedPreferences.getFloat(CustomUIManager.KEY_DRAG_DIST_THRESHOLD_ASSISTANT, 0.0f);
            CustomUIManager.this.mFlingDistThresholdOfAssistant = sharedPreferences.getFloat(CustomUIManager.KEY_FLING_DIST_THRESHOLD_ASSISTANT, 0.0f);
            CustomUIManager customUIManager = CustomUIManager.this;
            customUIManager.mAngleThresholdOfAssistant = customUIManager.mSharedPreferences.getInt(CustomUIManager.KEY_ANGLE_THRESHOLD_ASSISTANT, 0);
            LGLog.i(CustomUIManager.TAG, String.format("CustomUIManager init: mTouchSlop = %s, mScaledTouchSlop = %s, mWorkspacePageSpacing = %s, mDragDistThresholdOfAssistant = %s, mFlingDistThresholdOfAssistant = %s, mAngleThresholdOfAssistant = %s", Integer.valueOf(CustomUIManager.this.mTouchSlop), Integer.valueOf(CustomUIManager.this.mScaledTouchSlop), Integer.valueOf(CustomUIManager.this.mWorkspacePageSpacing), Float.valueOf(CustomUIManager.this.mDragDistThresholdOfAssistant), Float.valueOf(CustomUIManager.this.mFlingDistThresholdOfAssistant), Integer.valueOf(CustomUIManager.this.mAngleThresholdOfAssistant)));
        }
    };

    public void destroy() {
    }

    private CustomUIManager() {
    }

    private void init(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context.getPackageName(), CustomUIActivity.class.getName());
        try {
            packageManager.setComponentEnabledSetting(componentName, 1, 1);
        } catch (IllegalArgumentException e) {
            LGLog.d(TAG, "init : failed enable component " + componentName + ", " + e.toString());
        }
        this.mContext = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, 4);
        this.mSharedPreferences = sharedPreferences;
        this.mTouchSlop = sharedPreferences.getInt(KEY_TOUCHSLOP, 0);
        this.mScaledTouchSlop = this.mSharedPreferences.getInt(KEY_SCALED_TOUCHSLOP, 0);
        this.mWorkspacePageSpacing = this.mSharedPreferences.getInt(KEY_WORKSPACE_PAGE_SPACING, 0);
        this.mDragDistThresholdOfAssistant = this.mSharedPreferences.getFloat(KEY_DRAG_DIST_THRESHOLD_ASSISTANT, 0.0f);
        this.mFlingDistThresholdOfAssistant = this.mSharedPreferences.getFloat(KEY_FLING_DIST_THRESHOLD_ASSISTANT, 0.0f);
        this.mAngleThresholdOfAssistant = this.mSharedPreferences.getInt(KEY_ANGLE_THRESHOLD_ASSISTANT, 0);
        this.mUseToastOfAssistant = this.mSharedPreferences.getBoolean(KEY_TOAST_ASSISTANT, false);
        this.mUseFlingOfAssistant = this.mSharedPreferences.getBoolean(KEY_FLING_ASSISTANT, true);
        LGLog.i(TAG, String.format("CustomUIManager init: mTouchSlop = %s, mScaledTouchSlop = %s, mWorkspacePageSpacing = %s, mDragDistThresholdOfAssistant = %s, mFlingDistThresholdOfAssistant = %s, mAngleThresholdOfAssistant = %s, mUseToastFlingOfAssistant = %s, mUseFlingOfAssistant = %s", Integer.valueOf(this.mTouchSlop), Integer.valueOf(this.mScaledTouchSlop), Integer.valueOf(this.mWorkspacePageSpacing), Float.valueOf(this.mDragDistThresholdOfAssistant), Float.valueOf(this.mFlingDistThresholdOfAssistant), Integer.valueOf(this.mAngleThresholdOfAssistant), Boolean.valueOf(this.mUseToastOfAssistant), Boolean.valueOf(this.mUseFlingOfAssistant)));
    }

    public static CustomUIManager getInstance(Context context) {
        return sInstance;
    }

    public boolean isEnabled() {
        return sInstance.mContext != null;
    }

    public void setScaledTouchSlop(int value) {
        if (this.mContext != null) {
            this.mScaledTouchSlop = value;
            putInt(KEY_SCALED_TOUCHSLOP, value);
        }
    }

    public int getScaledTouchSlop() {
        return this.mScaledTouchSlop;
    }

    public void setTouchSlop(int value) {
        if (this.mContext != null) {
            this.mTouchSlop = value;
            putInt(KEY_TOUCHSLOP, value);
        }
    }

    public int getTouchSlop() {
        return this.mTouchSlop;
    }

    public void setWorkspacePageSpacing(int value) {
        if (this.mContext != null) {
            this.mWorkspacePageSpacing = value;
            putInt(KEY_WORKSPACE_PAGE_SPACING, value);
        }
    }

    public int getWorkspacePageSpacing() {
        return this.mWorkspacePageSpacing;
    }

    public void setDragDistThresholdOfAssistant(float value) {
        if (this.mContext != null) {
            this.mDragDistThresholdOfAssistant = value;
            putFloat(KEY_DRAG_DIST_THRESHOLD_ASSISTANT, value);
        }
    }

    public float getDragDistThresholdOfAssistant() {
        return this.mDragDistThresholdOfAssistant;
    }

    public void setFlingDistThresholdOfAssistant(float value) {
        if (this.mContext != null) {
            this.mFlingDistThresholdOfAssistant = value;
            putFloat(KEY_FLING_DIST_THRESHOLD_ASSISTANT, value);
        }
    }

    public float getFlingDistThresholdOfAssistant() {
        return this.mFlingDistThresholdOfAssistant;
    }

    public void setAngleThresholdOfAssistant(int value) {
        if (this.mContext != null) {
            this.mAngleThresholdOfAssistant = value;
            putInt(KEY_ANGLE_THRESHOLD_ASSISTANT, value);
        }
    }

    public int getAngleThresholdOfAssistant() {
        return this.mAngleThresholdOfAssistant;
    }

    public void setUseToastOfAssistant(boolean value) {
        if (this.mContext != null) {
            this.mUseToastOfAssistant = value;
            putBoolean(KEY_TOAST_ASSISTANT, value);
        }
    }

    public boolean getUseToastOfAssistant() {
        return this.mUseToastOfAssistant;
    }

    public void setUseFlingOfAssistant(boolean value) {
        if (this.mContext != null) {
            this.mUseFlingOfAssistant = value;
            putBoolean(KEY_FLING_ASSISTANT, value);
        }
    }

    public boolean getUseFlingOfAssistant() {
        if (this.mContext == null) {
            return true;
        }
        if (!this.mUseFlingOfAssistant) {
            LGLog.i(TAG, "onFling : block assistant. ");
        }
        return this.mUseFlingOfAssistant;
    }

    private void putFloat(String key, float value) {
        SharedPreferences sharedPreferences = this.mSharedPreferences;
        if (sharedPreferences != null) {
            sharedPreferences.edit().putFloat(key, value).commit();
        }
    }

    private void putInt(String key, int value) {
        SharedPreferences sharedPreferences = this.mSharedPreferences;
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt(key, value).commit();
        }
    }

    private void putBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = this.mSharedPreferences;
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(key, value).commit();
        }
    }

    public void showToast(Context context, int gestureType, float distance, PointF lastPos, PointF downPos) {
        if (getInstance(this.mContext).getUseToastOfAssistant()) {
            String string = Integer.toString(gestureType);
            if (gestureType == 3) {
                string = "SWIPE";
            } else if (gestureType == 4) {
                string = "FLING";
            }
            float fHypot = (float) Math.hypot(lastPos.x - downPos.x, lastPos.y - downPos.y);
            Toast toast = this.mToast;
            if (toast == null) {
                this.mToast = Toast.makeText(context, string + "(" + distance + ", " + fHypot + ")", 0);
            } else {
                toast.setText(string + "(" + distance + ", " + fHypot + ")");
            }
            this.mToast.show();
        }
    }
}
