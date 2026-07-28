package com.lge.lgewidgetlib.extview;

import android.view.View;
import com.lge.lgewidgetlib.WLog;

/* JADX INFO: loaded from: classes2.dex */
public class NormalState implements ExtViewState {
    private static final String TAG = "NormalState";
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
    public void notifyStateChanged() {
    }

    public NormalState(IExtViewStateMachine stateMachine, IAppWidgetExtViewClient extViewClient, IExtViewHostAdapter hostAdapter, ExtViewHandler extViewHandler) {
        this.mStateMachine = stateMachine;
        this.mExtViewClient = extViewClient;
        this.mHostAdapter = hostAdapter;
        this.mExtViewHandler = extViewHandler;
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onExpandReqComplete() {
        WLog.exceptWithLog(TAG, "onExpandReqComplete is not allowed on NormalState");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onRestoreReqComplete() {
        WLog.exceptWithLog(TAG, "onRestoreReqComplete is not allowed on NormalState");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onCancelReqComplete() {
        WLog.exceptWithLog(TAG, "onCancelReqComplete is not allowed on NormalState");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public boolean requestExtView(View[] views) {
        WLog.i(TAG, "requestExtView, isExtViewAvailable = " + this.mHostAdapter.isExtViewAvailable());
        if (!this.mHostAdapter.isExtViewAvailable()) {
            return false;
        }
        WLog.i(TAG, "requestExtView, start Animation");
        this.mExtViewHandler.transitionToExtView();
        this.mExtViewHandler.startAnimation(views);
        IExtViewStateMachine iExtViewStateMachine = this.mStateMachine;
        iExtViewStateMachine.setState(iExtViewStateMachine.getExpandingState());
        return true;
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void requestNormalView() {
        WLog.exceptWithLog(TAG, "requestNormalView is not allowed on NormalState");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void cancelExtView() {
        WLog.i(TAG, "cancelExtView on NormalState");
        this.mExtViewClient.onExtViewModeCanceled();
        this.mExtViewHandler.cancelAnimation();
        this.mExtViewHandler.transitionToNormalView();
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void notifyWidgetDeleted() {
        this.mExtViewClient.notifyWidgetDeleted();
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public boolean isExtViewAvailable() {
        return this.mHostAdapter.isExtViewAvailable();
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void notifyExtViewAvailable() {
        this.mExtViewClient.notifyExtViewAvailable();
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void requestBackgroundDimming(boolean enable, int endAlpha) {
        WLog.e(TAG, "dimming request is not allowed on NormalState");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void requestBackgroundDimming(boolean enable, int endAlpha, int duration) {
        WLog.e(TAG, "dimming request is not allowed on NormalState");
    }
}
