package com.android.quickstep;

import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

/* JADX INFO: loaded from: classes.dex */
public interface InputConsumer {
    public static final String[] NAMES = {"TYPE_NO_OP", "TYPE_OVERVIEW", "TYPE_OTHER_ACTIVITY", "TYPE_ASSISTANT", "TYPE_DEVICE_LOCKED", "TYPE_ACCESSIBILITY", "TYPE_SCREEN_PINNED", "TYPE_OVERVIEW_WITHOUT_FOCUS", "TYPE_RESET_GESTURE", "TYPE_PROGRESS_DELEGATE", "TYPE_SYSUI_OVERLAY", "TYPE_ONE_HANDED"};
    public static final InputConsumer NO_OP = new InputConsumer() { // from class: com.android.quickstep.-$$Lambda$InputConsumer$cayMJybMMyH-XvJ76yEWjWeqQUo
        @Override // com.android.quickstep.InputConsumer
        public final int getType() {
            return InputConsumer.lambda$static$0();
        }
    };
    public static final int TYPE_ACCESSIBILITY = 32;
    public static final int TYPE_ASSISTANT = 8;
    public static final int TYPE_DEVICE_LOCKED = 16;
    public static final int TYPE_NO_OP = 1;
    public static final int TYPE_ONE_HANDED = 2048;
    public static final int TYPE_OTHER_ACTIVITY = 4;
    public static final int TYPE_OVERVIEW = 2;
    public static final int TYPE_OVERVIEW_WITHOUT_FOCUS = 128;
    public static final int TYPE_PROGRESS_DELEGATE = 512;
    public static final int TYPE_RESET_GESTURE = 256;
    public static final int TYPE_SCREEN_PINNED = 64;
    public static final int TYPE_SYSUI_OVERLAY = 1024;

    static /* synthetic */ int lambda$static$0() {
        return 1;
    }

    default boolean allowInterceptByParent() {
        return true;
    }

    default InputConsumer getActiveConsumerInHierarchy() {
        return this;
    }

    int getType();

    default boolean isConsumerDetachedFromGesture() {
        return false;
    }

    default void notifyOrientationSetup() {
    }

    default void onConsumerAboutToBeSwitched() {
    }

    default void onKeyEvent(KeyEvent ev) {
    }

    default void onMotionEvent(MotionEvent ev) {
    }

    default void onInputEvent(InputEvent ev) {
        if (ev instanceof MotionEvent) {
            onMotionEvent((MotionEvent) ev);
        } else {
            onKeyEvent((KeyEvent) ev);
        }
    }

    default String getName() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            String[] strArr = NAMES;
            if (i < strArr.length) {
                if ((getType() & (1 << i)) != 0) {
                    if (sb.length() > 0) {
                        sb.append(":");
                    }
                    sb.append(strArr[i]);
                }
                i++;
            } else {
                return sb.toString();
            }
        }
    }
}
