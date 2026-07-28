package com.lge.lgewidgetlib.extview;

import android.view.View;
import com.lge.lgewidgetlib.WLog;

/* JADX INFO: loaded from: classes2.dex */
public class ExpandingState implements ExtViewState {
    private static final String TAG = "ExpandingState";
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

    public ExpandingState(IExtViewStateMachine stateMachine, IAppWidgetExtViewClient extViewClient, IExtViewHostAdapter hostAdapter, ExtViewHandler extViewHandler) {
        this.mStateMachine = stateMachine;
        this.mExtViewClient = extViewClient;
        this.mHostAdapter = hostAdapter;
        this.mExtViewHandler = extViewHandler;
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onExpandReqComplete() {
        this.mExtViewClient.onExtViewModeComplete();
        IExtViewStateMachine iExtViewStateMachine = this.mStateMachine;
        iExtViewStateMachine.setState(iExtViewStateMachine.getExpandedState());
        this.mExtViewHandler.setShowColorView(true);
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onRestoreReqComplete() {
        WLog.exceptWithLog(TAG, "onRestoreReqComplete is not allowed on ExpandingState");
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public boolean requestExtView(View[] views) {
        WLog.exceptWithLog(TAG, "requestExtView is already called");
        return false;
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void requestNormalView() {
        IExtViewStateMachine iExtViewStateMachine = this.mStateMachine;
        iExtViewStateMachine.setState(iExtViewStateMachine.getRestoreState());
        this.mExtViewHandler.reverseAnimation();
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void cancelExtView() {
        this.mExtViewClient.onExtViewModeCanceled();
        this.mExtViewHandler.cancelAnimation();
        this.mExtViewHandler.transitionToNormalView();
        IExtViewStateMachine iExtViewStateMachine = this.mStateMachine;
        iExtViewStateMachine.setState(iExtViewStateMachine.getNormalState());
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewState
    public void notifyWidgetDeleted() {
        WLog.exceptWithLog(TAG, "notifyWidgetDeleted is not allowed on ExpandingState");
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
