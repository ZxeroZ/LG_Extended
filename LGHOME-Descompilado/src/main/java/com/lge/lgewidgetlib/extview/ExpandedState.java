package com.lge.lgewidgetlib.extview;

import android.view.View;
import com.lge.lgewidgetlib.WLog;

/* JADX INFO: loaded from: classes2.dex */
public class ExpandedState implements ExtViewState {
    private static final String TAG = "ExpandedState";
    IAppWidgetExtViewClient mExtViewClient;
    ExtViewHandler mExtViewHandler;
    IExtViewHostAdapter mHostAdapter;
    IExtViewStateMachine mStateMachine;

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void clientExpandAnimationFinished() {
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void clientRestoreAnimationFinished() {
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public boolean isExtViewAvailable() {
        return false;
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void notifyExtViewAvailable() {
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void notifyStateChanged() {
    }

    public ExpandedState(IExtViewStateMachine stateMachine, IAppWidgetExtViewClient extViewClient, IExtViewHostAdapter hostAdapter, ExtViewHandler extViewHandler) {
        this.mStateMachine = stateMachine;
        this.mExtViewClient = extViewClient;
        this.mHostAdapter = hostAdapter;
        this.mExtViewHandler = extViewHandler;
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onExpandReqComplete() {
        WLog.exceptWithLog(TAG, "onExpandReqComplete is not allowed on ExpandedState");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onRestoreReqComplete() {
        WLog.exceptWithLog(TAG, "onRestoreReqComplete is not allowed on ExpandedState");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onCancelReqComplete() {
        WLog.exceptWithLog(TAG, "onCancelReqComplete is not allowed on ExpandedState");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public boolean requestExtView(View[] views) {
        WLog.exceptWithLog(TAG, "already ExtView");
        return false;
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void requestNormalView() {
        this.mExtViewHandler.reverseAnimation();
        IExtViewStateMachine iExtViewStateMachine = this.mStateMachine;
        iExtViewStateMachine.setState(iExtViewStateMachine.getRestoreState());
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void cancelExtView() {
        this.mExtViewClient.onExtViewModeCanceled();
        this.mExtViewHandler.transitionToNormalView();
        IExtViewStateMachine iExtViewStateMachine = this.mStateMachine;
        iExtViewStateMachine.setState(iExtViewStateMachine.getNormalState());
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void notifyWidgetDeleted() {
        WLog.exceptWithLog(TAG, "notifyWidgetDeleted is not allowed on ExpandedState");
        this.mExtViewClient.notifyWidgetDeleted();
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void requestBackgroundDimming(boolean enable, int endAlpha) {
        this.mExtViewHandler.setExtViewBackgroudDimming(enable, endAlpha);
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void requestBackgroundDimming(boolean enable, int endAlpha, int duration) {
        this.mExtViewHandler.setExtViewBackgroudDimming(enable, endAlpha, duration);
    }
}
