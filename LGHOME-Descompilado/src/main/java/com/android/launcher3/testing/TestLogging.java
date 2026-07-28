package com.android.launcher3.testing;

import android.util.Log;
import android.view.MotionEvent;
import com.android.launcher3.Utilities;
import java.util.function.BiConsumer;

/* JADX INFO: loaded from: classes.dex */
public final class TestLogging {
    private static BiConsumer<String, String> sEventConsumer;

    private static void recordEventSlow(String sequence, String event) {
        Log.d(TestProtocol.TAPL_EVENTS_TAG, sequence + " / " + event);
        BiConsumer<String, String> biConsumer = sEventConsumer;
        if (biConsumer != null) {
            biConsumer.accept(sequence, event);
        }
    }

    public static void recordEvent(String sequence, String event) {
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            recordEventSlow(sequence, event);
        }
    }

    public static void recordEvent(String sequence, String message, Object parameter) {
        if (Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            recordEventSlow(sequence, message + ": " + parameter);
        }
    }

    public static void recordMotionEvent(String sequence, String message, MotionEvent event) {
        if (!Utilities.IS_RUNNING_IN_TEST_HARNESS || event.getAction() == 2) {
            return;
        }
        recordEventSlow(sequence, message + ": " + event);
    }

    static void setEventConsumer(BiConsumer<String, String> consumer) {
        sEventConsumer = consumer;
    }
}
