package com.android.launcher3;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/* JADX INFO: loaded from: classes.dex */
public class StylusEventHelper {
    private boolean mIsButtonPressed;
    private StylusButtonListener mListener;
    private final float mSlop;
    private View mView;

    public interface StylusButtonListener {
        boolean onPressed(MotionEvent event);

        boolean onReleased(MotionEvent event);
    }

    public StylusEventHelper(StylusButtonListener listener, View view) {
        this.mListener = listener;
        this.mView = view;
        if (view != null) {
            this.mSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        } else {
            this.mSlop = ViewConfiguration.getTouchSlop();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0044  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onMotionEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            boolean r0 = isStylusButtonPressed(r8)
            int r1 = r8.getAction()
            r2 = 0
            if (r1 == 0) goto L51
            r3 = 1
            if (r1 == r3) goto L44
            r4 = 2
            if (r1 == r4) goto L15
            r0 = 3
            if (r1 == r0) goto L44
            goto L5c
        L15:
            android.view.View r1 = r7.mView
            float r4 = r8.getX()
            float r5 = r8.getY()
            float r6 = r7.mSlop
            boolean r1 = com.android.launcher3.Utilities.pointInView(r1, r4, r5, r6)
            if (r1 != 0) goto L28
            return r2
        L28:
            boolean r1 = r7.mIsButtonPressed
            if (r1 != 0) goto L37
            if (r0 == 0) goto L37
            r7.mIsButtonPressed = r3
            com.android.launcher3.StylusEventHelper$StylusButtonListener r0 = r7.mListener
            boolean r8 = r0.onPressed(r8)
            return r8
        L37:
            if (r1 == 0) goto L5c
            if (r0 != 0) goto L5c
            r7.mIsButtonPressed = r2
            com.android.launcher3.StylusEventHelper$StylusButtonListener r0 = r7.mListener
            boolean r8 = r0.onReleased(r8)
            return r8
        L44:
            boolean r0 = r7.mIsButtonPressed
            if (r0 == 0) goto L5c
            r7.mIsButtonPressed = r2
            com.android.launcher3.StylusEventHelper$StylusButtonListener r0 = r7.mListener
            boolean r8 = r0.onReleased(r8)
            return r8
        L51:
            r7.mIsButtonPressed = r0
            if (r0 == 0) goto L5c
            com.android.launcher3.StylusEventHelper$StylusButtonListener r0 = r7.mListener
            boolean r8 = r0.onPressed(r8)
            return r8
        L5c:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.StylusEventHelper.onMotionEvent(android.view.MotionEvent):boolean");
    }

    public boolean inStylusButtonPressed() {
        return this.mIsButtonPressed;
    }

    private static boolean isStylusButtonPressed(MotionEvent event) {
        return event.getToolType(0) == 2 && (event.getButtonState() & 2) == 2;
    }
}
