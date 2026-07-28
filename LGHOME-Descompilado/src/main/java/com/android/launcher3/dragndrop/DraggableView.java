package com.android.launcher3.dragndrop;

import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
public interface DraggableView {
    public static final int DRAGGABLE_ICON = 0;
    public static final int DRAGGABLE_WIDGET = 1;

    static /* synthetic */ int lambda$ofType$0(int i) {
        return i;
    }

    int getViewType();

    default void getVisualDragBounds(Rect bounds) {
    }

    default void prepareDrawDragView() {
    }

    static DraggableView ofType(final int type) {
        return new DraggableView() { // from class: com.android.launcher3.dragndrop.-$$Lambda$DraggableView$AKMZUTgmGzxxFE6KwINNgi5rzUk
            @Override // com.android.launcher3.dragndrop.DraggableView
            public final int getViewType() {
                return DraggableView.lambda$ofType$0(type);
            }
        };
    }
}
