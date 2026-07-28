package com.android.launcher3.dragndrop;

import android.content.Context;
import android.view.DragEvent;
import android.view.MotionEvent;
import com.android.launcher3.DropTarget;

/* JADX INFO: loaded from: classes.dex */
public abstract class DragDriver {
    protected final EventListener mEventListener;

    public interface EventListener {
        void onDriverDragCancel();

        void onDriverDragEnd(float x, float y);

        void onDriverDragExitWindow();

        void onDriverDragMove(float x, float y);
    }

    public abstract boolean onDragEvent(DragEvent event);

    public void onDragViewAnimationEnd() {
    }

    public DragDriver(EventListener eventListener) {
        this.mEventListener = eventListener;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 1) {
            this.mEventListener.onDriverDragMove(ev.getX(), ev.getY());
            this.mEventListener.onDriverDragEnd(ev.getX(), ev.getY());
        } else if (action == 2) {
            this.mEventListener.onDriverDragMove(ev.getX(), ev.getY());
        } else if (action == 3) {
            this.mEventListener.onDriverDragCancel();
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 1) {
            this.mEventListener.onDriverDragEnd(ev.getX(), ev.getY());
        } else if (action == 3) {
            this.mEventListener.onDriverDragCancel();
        }
        return true;
    }

    public static DragDriver create(Context context, DragController dragController, DropTarget.DragObject dragObject, DragOptions options) {
        return new InternalDragDriver(dragController);
    }
}
