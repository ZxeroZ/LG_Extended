package com.android.launcher3.compat;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.launcher3.Utilities;
import com.android.launcher3.testing.TestProtocol;

/* JADX INFO: loaded from: classes.dex */
public class AccessibilityManagerCompat {
    public static boolean isAccessibilityEnabled(Context context) {
        return getManager(context).isEnabled();
    }

    public static boolean isObservedEventType(Context context, int eventType) {
        return isAccessibilityEnabled(context);
    }

    public static void sendCustomAccessibilityEvent(View target, int type, String text) {
        if (target == null || !isObservedEventType(target.getContext(), type)) {
            return;
        }
        AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(type);
        target.onInitializeAccessibilityEvent(accessibilityEventObtain);
        if (!TextUtils.isEmpty(text)) {
            accessibilityEventObtain.getText().add(text);
        }
        getManager(target.getContext()).sendAccessibilityEvent(accessibilityEventObtain);
    }

    private static AccessibilityManager getManager(Context context) {
        return (AccessibilityManager) context.getSystemService("accessibility");
    }

    public static void sendStateEventToTest(Context context, int stateOrdinal) {
        AccessibilityManager accessibilityManagerForTest = getAccessibilityManagerForTest(context);
        if (accessibilityManagerForTest == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("state", stateOrdinal);
        sendEventToTest(accessibilityManagerForTest, TestProtocol.SWITCHED_TO_STATE_MESSAGE, bundle);
        Log.d(TestProtocol.PERMANENT_DIAG_TAG, "sendStateEventToTest: " + stateOrdinal);
    }

    public static void sendScrollFinishedEventToTest(Context context) {
        AccessibilityManager accessibilityManagerForTest = getAccessibilityManagerForTest(context);
        if (accessibilityManagerForTest == null) {
            return;
        }
        sendEventToTest(accessibilityManagerForTest, TestProtocol.SCROLL_FINISHED_MESSAGE, null);
    }

    public static void sendPauseDetectedEventToTest(Context context) {
        AccessibilityManager accessibilityManagerForTest = getAccessibilityManagerForTest(context);
        if (accessibilityManagerForTest == null) {
            return;
        }
        sendEventToTest(accessibilityManagerForTest, TestProtocol.PAUSE_DETECTED_MESSAGE, null);
    }

    public static void sendDismissAnimationEndsEventToTest(Context context) {
        AccessibilityManager accessibilityManagerForTest = getAccessibilityManagerForTest(context);
        if (accessibilityManagerForTest == null) {
            return;
        }
        sendEventToTest(accessibilityManagerForTest, TestProtocol.DISMISS_ANIMATION_ENDS_MESSAGE, null);
    }

    private static void sendEventToTest(AccessibilityManager accessibilityManager, String eventTag, Bundle data) {
        AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(16384);
        accessibilityEventObtain.setClassName(eventTag);
        accessibilityEventObtain.setParcelableData(data);
        accessibilityManager.sendAccessibilityEvent(accessibilityEventObtain);
    }

    private static AccessibilityManager getAccessibilityManagerForTest(Context context) {
        if (!Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            return null;
        }
        AccessibilityManager manager = getManager(context);
        if (manager.isEnabled()) {
            return manager;
        }
        return null;
    }

    public static int getRecommendedTimeoutMillis(Context context, int originalTimeout, int flags) {
        return Build.VERSION.SDK_INT >= 29 ? getManager(context).getRecommendedTimeoutMillis(originalTimeout, flags) : originalTimeout;
    }
}
