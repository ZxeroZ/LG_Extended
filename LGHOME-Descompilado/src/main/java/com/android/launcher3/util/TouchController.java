package com.android.launcher3.util;

import android.view.MotionEvent;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public interface TouchController {
    default void dump(String prefix, PrintWriter writer) {
    }

    boolean onControllerInterceptTouchEvent(MotionEvent ev);

    boolean onControllerTouchEvent(MotionEvent ev);
}
