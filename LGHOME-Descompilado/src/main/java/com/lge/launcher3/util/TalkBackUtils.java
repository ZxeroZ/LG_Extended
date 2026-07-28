package com.lge.launcher3.util;

import android.content.Context;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/* JADX INFO: loaded from: classes.dex */
public class TalkBackUtils {
    public static boolean isEnabled(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        return accessibilityManager != null && accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled();
    }

    public static void setTalkBack(View view, String text) {
        if (!(view.getContentDescription() != null)) {
            view.setContentDescription(text);
        }
        view.sendAccessibilityEvent(4);
    }

    public static void sendAccessibilityEvent(Context context, int textResId, boolean hover) {
        sendAccessibilityEvent(context, context.getString(textResId), hover);
    }

    public static void sendAccessibilityEvent(Context context, String text, boolean hover) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        if (accessibilityManager.isEnabled()) {
            AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(hover ? 128 : 32);
            if (accessibilityEventObtain == null) {
                return;
            }
            accessibilityEventObtain.getText().add(text);
            accessibilityManager.sendAccessibilityEvent(accessibilityEventObtain);
        }
    }
}
