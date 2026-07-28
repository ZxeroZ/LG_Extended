package com.lge.lgewidgetlib.extview;

import android.view.View;
import android.widget.FrameLayout;
import com.lge.lgewidgetlib.LgeAppWidgetHostView;

/* JADX INFO: loaded from: classes2.dex */
class ExtToDragLayer extends ExtViewHandler {
    public ExtToDragLayer(LgeAppWidgetHostView view, IAppWidgetExtViewClient client, IExtViewHostAdapter extViewHandler, ExtViewEventListener eventListener) {
        super(view, client, extViewHandler, eventListener);
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewHandler
    protected FrameLayout.LayoutParams getExtViewParam(View view) {
        int[] iArr = new int[2];
        this.mHostView.getLocationInWindow(iArr);
        view.getLocationInWindow(new int[2]);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(view.getWidth(), view.getHeight());
        if (view.isLayoutRtl()) {
            layoutParams.setMarginStart((this.mLauncherIF.getWorkSpaceWidth() - (iArr[0] + this.mHostView.getWidth())) + view.getLeft());
        } else {
            layoutParams.setMarginStart(iArr[0] + view.getLeft());
        }
        layoutParams.topMargin = iArr[1] + ((this.mHostView.getHeight() - view.getHeight()) / 2);
        return layoutParams;
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewHandler
    protected void attachWidget(View view) {
        this.mLauncherIF.attachWidgetToExtLayer(view, new FrameLayout.LayoutParams(-1, -1));
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewHandler
    protected void detachWidget(View view) {
        this.mLauncherIF.detachWidgetFromExtLayer(view);
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewHandler
    protected int getExtViewHeight() {
        return this.mLauncherIF.getScreenHeight() - getTopMarginOfExtView();
    }
}
