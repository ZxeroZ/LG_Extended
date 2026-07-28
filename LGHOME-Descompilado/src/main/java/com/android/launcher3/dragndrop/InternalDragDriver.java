package com.android.launcher3.dragndrop;

import android.view.DragEvent;

/* JADX INFO: compiled from: DragDriver.java */
/* JADX INFO: loaded from: classes.dex */
class InternalDragDriver extends DragDriver {
    @Override // com.android.launcher3.dragndrop.DragDriver
    public boolean onDragEvent(DragEvent event) {
        return false;
    }

    public InternalDragDriver(DragController dragController) {
        super(dragController);
    }
}
