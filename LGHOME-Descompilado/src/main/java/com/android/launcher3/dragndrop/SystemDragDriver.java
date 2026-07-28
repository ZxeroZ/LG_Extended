package com.android.launcher3.dragndrop;

import android.content.Context;
import android.view.DragEvent;
import android.view.MotionEvent;
import com.android.launcher3.DropTarget;

/* JADX INFO: compiled from: DragDriver.java */
/* JADX INFO: loaded from: classes.dex */
class SystemDragDriver extends DragDriver {
    private final Context mContext;
    private final DropTarget.DragObject mDragObject;
    float mLastX;
    float mLastY;

    @Override // com.android.launcher3.dragndrop.DragDriver
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override // com.android.launcher3.dragndrop.DragDriver
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public SystemDragDriver(DragController dragController, Context context, DropTarget.DragObject dragObject) {
        super(dragController);
        this.mLastX = 0.0f;
        this.mLastY = 0.0f;
        this.mDragObject = dragObject;
        this.mContext = context;
    }

    @Override // com.android.launcher3.dragndrop.DragDriver
    public boolean onDragEvent(DragEvent event) {
        switch (event.getAction()) {
            case 1:
                this.mLastX = event.getX();
                this.mLastY = event.getY();
                return true;
            case 2:
                this.mLastX = event.getX();
                this.mLastY = event.getY();
                this.mEventListener.onDriverDragMove(event.getX(), event.getY());
                return true;
            case 3:
                this.mLastX = event.getX();
                this.mLastY = event.getY();
                this.mEventListener.onDriverDragMove(event.getX(), event.getY());
                this.mEventListener.onDriverDragEnd(this.mLastX, this.mLastY);
                return true;
            case 4:
                this.mEventListener.onDriverDragCancel();
                return true;
            case 6:
                this.mEventListener.onDriverDragExitWindow();
            case 5:
                return true;
            default:
                return false;
        }
    }
}
