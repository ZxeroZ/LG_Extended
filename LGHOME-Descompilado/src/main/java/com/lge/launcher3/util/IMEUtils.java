package com.lge.launcher3.util;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/* JADX INFO: loaded from: classes.dex */
public class IMEUtils {
    public static final int DEFAULT_DELAY_MILLIS = 100;

    public static boolean showInputMethod(Context aContext, View aView) {
        InputMethodManager inputMethodManager = (InputMethodManager) aContext.getApplicationContext().getSystemService("input_method");
        if (inputMethodManager == null || aView == null) {
            return false;
        }
        return inputMethodManager.showSoftInput(aView, 1, null);
    }

    public static void showInputMethodDelayed(final Context aContext, final View aView, int aDelayMillis) {
        new Handler().postDelayed(new Runnable() { // from class: com.lge.launcher3.util.IMEUtils.1
            @Override // java.lang.Runnable
            public void run() {
                IMEUtils.showInputMethod(aContext, aView);
            }
        }, aDelayMillis);
    }

    public static boolean hideInputMethod(Context aContext, View aView) {
        InputMethodManager inputMethodManager = (InputMethodManager) aContext.getApplicationContext().getSystemService("input_method");
        if (inputMethodManager == null || aView == null || aView.getWindowToken() == null) {
            return false;
        }
        return inputMethodManager.hideSoftInputFromWindow(aView.getWindowToken(), 0);
    }

    public static boolean showInputMethod(View aView) {
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance == null || aView == null) {
            return false;
        }
        return inputMethodManagerPeekInstance.showSoftInput(aView, 0);
    }

    public static void showInputMethodDelayed(final View aView, int aDelayMillis) {
        new Handler().postDelayed(new Runnable() { // from class: com.lge.launcher3.util.IMEUtils.2
            @Override // java.lang.Runnable
            public void run() {
                IMEUtils.showInputMethod(aView);
            }
        }, aDelayMillis);
    }

    public static boolean hideInputMethod(View aView) {
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance == null || aView == null || aView.getWindowToken() == null) {
            return false;
        }
        return inputMethodManagerPeekInstance.hideSoftInputFromWindow(aView.getWindowToken(), 0);
    }
}
