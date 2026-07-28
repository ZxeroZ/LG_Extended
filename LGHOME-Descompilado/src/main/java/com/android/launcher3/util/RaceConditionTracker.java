package com.android.launcher3.util;

/* JADX INFO: loaded from: classes.dex */
public class RaceConditionTracker {
    public static final boolean ENTER = true;
    static final String ENTER_POSTFIX = "enter";
    public static final boolean EXIT = false;
    static final String EXIT_POSTFIX = "exit";
    private static EventProcessor sEventProcessor;

    public interface EventProcessor {
        void onEvent(String eventName);
    }

    static void setEventProcessor(EventProcessor eventProcessor) {
        sEventProcessor = eventProcessor;
    }

    public static void onEvent(String eventName) {
        EventProcessor eventProcessor = sEventProcessor;
        if (eventProcessor != null) {
            eventProcessor.onEvent(eventName);
        }
    }

    public static void onEvent(String eventName, boolean isEnter) {
        EventProcessor eventProcessor = sEventProcessor;
        if (eventProcessor != null) {
            eventProcessor.onEvent(enterExitEvt(eventName, isEnter));
        }
    }

    public static String enterExitEvt(String eventName, boolean isEnter) {
        return eventName + ":" + (isEnter ? ENTER_POSTFIX : EXIT_POSTFIX);
    }

    public static String enterEvt(String eventName) {
        return enterExitEvt(eventName, true);
    }

    public static String exitEvt(String eventName) {
        return enterExitEvt(eventName, false);
    }
}
