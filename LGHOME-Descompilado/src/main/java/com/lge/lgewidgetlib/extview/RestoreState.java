package com.lge.lgewidgetlib.extview;

import android.view.View;
import com.lge.lgewidgetlib.WLog;

/* JADX INFO: loaded from: classes2.dex */
public class RestoreState implements ExtViewState {
    private static final String TAG = "RestoreState";
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

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onCancelReqComplete() {
    }

    public RestoreState(IExtViewStateMachine stateMachine, IAppWidgetExtViewClient extViewClient, IExtViewHostAdapter hostAdapter, ExtViewHandler extViewHandler) {
        this.mStateMachine = stateMachine;
        this.mExtViewClient = extViewClient;
        this.mHostAdapter = hostAdapter;
        this.mExtViewHandler = extViewHandler;
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onExpandReqComplete() {
        WLog.e(TAG, "onExpandReqComplete is not allowed on Restoe");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onRestoreReqComplete() {
        this.mExtViewHandler.transitionToNormalView();
        this.mExtViewClient.onWidgetModeComplete();
        IExtViewStateMachine iExtViewStateMachine = this.mStateMachine;
        iExtViewStateMachine.setState(iExtViewStateMachine.getNormalState());
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public boolean requestExtView(View[] views) {
        WLog.exceptWithLog(TAG, "currently it is on Restoe");
        return false;
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void requestNormalView() {
        WLog.exceptWithLog(TAG, "requestNormalView is already called");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void cancelExtView() {
        this.mExtViewClient.onExtViewModeCanceled();
        this.mExtViewHandler.cancelAnimation();
        IExtViewStateMachine iExtViewStateMachine = this.mStateMachine;
        iExtViewStateMachine.setState(iExtViewStateMachine.getNormalState());
        this.mExtViewHandler.transitionToNormalView();
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void notifyWidgetDeleted() {
        WLog.exceptWithLog(TAG, "notifyWidgetDeleted is not allowed on RestoreState");
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
