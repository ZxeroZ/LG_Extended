package com.android.launcher3.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import com.android.launcher3.AbstractFloatingView;

/* JADX INFO: loaded from: classes.dex */
public class ListenerView extends AbstractFloatingView {
    public Runnable mCloseListener;

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 256) != 0;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
    }

    public ListenerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVisibility(8);
    }

    public void setListener(Runnable listener) {
        this.mCloseListener = listener;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsOpen = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsOpen = false;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        if (this.mIsOpen) {
            Runnable runnable = this.mCloseListener;
            if (runnable != null) {
                runnable.run();
            } else if (getParent() instanceof ViewGroup) {
                ((ViewGroup) getParent()).removeView(this);
            }
        }
        this.mIsOpen = false;
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            handleClose(false);
        }
        return false;
    }
}
