package com.lge.lgewidgetlib.extview;

import android.appwidget.AppWidgetHostView;
import android.view.View;
import com.lge.lgewidgetlib.LgeAppWidgetHostView;
import com.lge.lgewidgetlib.LgeReflectionUtil;
import com.lge.lgewidgetlib.WLog;

/* JADX INFO: loaded from: classes2.dex */
public class LgeAppWidgetExtViewHost implements IAppWidgetExtViewHost, IExtViewStateMachine, ExtViewEventListener {
    private static final String TAG = "ExtViewHost";
    public static boolean sIsExpaned;
    ExtViewState mExpandedState;
    ExtViewState mExpandingState;
    private LgeAppWidgetExtViewClientProxy mExtViewClient = null;
    private ExtViewHandler mExtViewHandler = null;
    private IExtViewHostAdapter mHostAdapter;
    private LgeAppWidgetHostView mHostView;
    ExtViewState mNormalState;
    ExtViewState mRestoreState;
    ExtViewState mState;

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public boolean isImprovedExtHost() {
        return true;
    }

    public LgeAppWidgetExtViewHost(LgeAppWidgetHostView view, IExtViewHostAdapter hostAdapter) {
        this.mHostView = view;
        this.mHostAdapter = hostAdapter;
    }

    public boolean updateExtViewList() {
        View view = (View) LgeReflectionUtil.getPrivateField(AppWidgetHostView.class, this.mHostView, "mView");
        this.mExtViewClient = null;
        View lgeCustomView = LgeReflectionUtil.getLgeCustomView("setExtViewHost", new Class[]{Object.class}, view);
        if (lgeCustomView == null) {
            return false;
        }
        return setExtViewClient(lgeCustomView);
    }

    private boolean setExtViewClient(View view) {
        this.mExtViewClient = new LgeAppWidgetExtViewClientProxy(this, view);
        this.mExtViewHandler = new ExtToDragLayer(this.mHostView, this.mExtViewClient, this.mHostAdapter, this);
        this.mNormalState = new NormalState(this, this.mExtViewClient, this.mHostAdapter, this.mExtViewHandler);
        this.mExpandingState = new ExpandingState(this, this.mExtViewClient, this.mHostAdapter, this.mExtViewHandler);
        this.mExpandedState = new ExpandedState(this, this.mExtViewClient, this.mHostAdapter, this.mExtViewHandler);
        this.mRestoreState = new RestoreState(this, this.mExtViewClient, this.mHostAdapter, this.mExtViewHandler);
        setState(this.mNormalState);
        return true;
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public boolean requestExtView(View[] views) {
        return this.mState.requestExtView(views);
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public void requestNormalView() {
        this.mState.requestNormalView();
    }

    public void cancelExtView() {
        if (this.mExtViewClient == null) {
            WLog.d(TAG, "endExtViewMode mExtViewClient = null return");
        } else {
            this.mState.cancelExtView();
        }
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewStateMachine
    public ExtViewState getNormalState() {
        return this.mNormalState;
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewStateMachine
    public ExtViewState getExpandingState() {
        return this.mExpandingState;
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewStateMachine
    public ExtViewState getExpandedState() {
        return this.mExpandedState;
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewStateMachine
    public ExtViewState getRestoreState() {
        return this.mRestoreState;
    }

    public boolean isExpanded() {
        return !(this.mState instanceof NormalState);
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public int getExtendedWidgetHeight() {
        return this.mExtViewHandler.getExtViewHeight();
    }

    public void notifyWidgetDeleted() {
        LgeAppWidgetExtViewClientProxy lgeAppWidgetExtViewClientProxy = this.mExtViewClient;
        if (lgeAppWidgetExtViewClientProxy != null) {
            lgeAppWidgetExtViewClientProxy.notifyWidgetDeleted();
        }
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public void clientExpandAnimationFinished() {
        this.mState.clientExpandAnimationFinished();
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public void clientRestoreAnimationFinished() {
        this.mState.clientRestoreAnimationFinished();
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewStateMachine
    public void setState(ExtViewState newState) {
        WLog.i(TAG, "New State = " + newState);
        if (newState instanceof NormalState) {
            sIsExpaned = false;
        } else {
            sIsExpaned = true;
        }
        this.mState = newState;
        newState.notifyStateChanged();
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onExpandReqComplete() {
        this.mState.onExpandReqComplete();
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onRestoreReqComplete() {
        this.mState.onRestoreReqComplete();
    }

    @Override // com.lge.lgewidgetlib.extview.ExtViewEventListener
    public void onCancelReqComplete() {
        this.mState.onCancelReqComplete();
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public boolean isExtViewAvailable() {
        return this.mState.isExtViewAvailable();
    }

    public void notifyExtViewAvailable() {
        this.mState.notifyExtViewAvailable();
    }

    public void notifyRequestExtViewException() {
        LgeAppWidgetExtViewClientProxy lgeAppWidgetExtViewClientProxy = this.mExtViewClient;
        if (lgeAppWidgetExtViewClientProxy != null) {
            lgeAppWidgetExtViewClientProxy.notifyRequestExtViewException();
        } else {
            WLog.e(TAG, "notifyRequestExtViewException, mExtViewClient is null");
        }
    }

    public void notifyWidgetHostDestroyed() {
        LgeAppWidgetExtViewClientProxy lgeAppWidgetExtViewClientProxy = this.mExtViewClient;
        if (lgeAppWidgetExtViewClientProxy != null) {
            lgeAppWidgetExtViewClientProxy.notifyWidgetHostDestroyed();
        } else {
            WLog.e(TAG, "notifyWidgetHostDestroyed, mExtViewClient is null");
        }
    }

    public void notifyBindingStarted() {
        LgeAppWidgetExtViewClientProxy lgeAppWidgetExtViewClientProxy = this.mExtViewClient;
        if (lgeAppWidgetExtViewClientProxy != null) {
            lgeAppWidgetExtViewClientProxy.notifyBindingStarted();
        } else {
            WLog.e(TAG, "notifyBindingStarted, mExtViewClient is null");
        }
    }

    public boolean isWidgetUpdateSkippable() {
        LgeAppWidgetExtViewClientProxy lgeAppWidgetExtViewClientProxy = this.mExtViewClient;
        if (lgeAppWidgetExtViewClientProxy != null) {
            return lgeAppWidgetExtViewClientProxy.isWidgetUpdateSkippable();
        }
        WLog.e(TAG, "isWidgetUpdateSkippable, mExtViewClient is null");
        return false;
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public void requestExtViewDimming(boolean enable, int endAlpha) {
        this.mState.requestBackgroundDimming(enable, endAlpha);
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public void requestExtViewDimming(boolean enable, int endAlpha, int duration) {
        this.mState.requestBackgroundDimming(enable, endAlpha, duration);
    }

    public void notifyWidgetReset() {
        LgeAppWidgetExtViewClientProxy lgeAppWidgetExtViewClientProxy = this.mExtViewClient;
        if (lgeAppWidgetExtViewClientProxy != null) {
            lgeAppWidgetExtViewClientProxy.notifyWidgetReset();
        }
    }
}
