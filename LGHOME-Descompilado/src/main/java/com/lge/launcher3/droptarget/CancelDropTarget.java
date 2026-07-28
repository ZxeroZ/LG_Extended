package com.lge.launcher3.droptarget;

import android.content.Context;
import android.util.AttributeSet;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class CancelDropTarget extends ButtonDropTarget {
    @Override // com.android.launcher3.ButtonDropTarget
    protected void completeDrop(DropTarget.DragObject d) {
    }

    public CancelDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CancelDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // com.android.launcher3.ButtonDropTarget, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHoverColor = getResources().getColor(R.color.cancel_target_hover_tint);
        setDrawable(R.drawable.ic_homescreen_reset);
    }

    @Override // com.android.launcher3.ButtonDropTarget
    protected boolean supportsDrop(DragSource source, Object info) {
        return (info instanceof PendingAddItemInfo) || (source instanceof PopupContainerWithArrow);
    }

    @Override // com.android.launcher3.ButtonDropTarget
    public LauncherLogProto.Target getDropTargetForLogging() {
        LauncherLogProto.Target targetNewTarget = LoggerUtils.newTarget(2);
        targetNewTarget.controlType = 0;
        return targetNewTarget;
    }
}
