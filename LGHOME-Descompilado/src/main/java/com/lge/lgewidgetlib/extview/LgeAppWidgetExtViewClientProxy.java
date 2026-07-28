package com.lge.lgewidgetlib.extview;

import android.view.View;
import com.lge.lgewidgetlib.LgeReflectionUtil;

/* JADX INFO: loaded from: classes2.dex */
class LgeAppWidgetExtViewClientProxy implements IAppWidgetExtViewClient {
    View mExtView;

    public LgeAppWidgetExtViewClientProxy(IAppWidgetExtViewHost extViewHost, View widgetView) {
        this.mExtView = widgetView;
        setExtViewHost(extViewHost);
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void setExtViewHost(IAppWidgetExtViewHost host) {
        setExtViewHost((Object) host);
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void setExtViewHost(Object host) {
        try {
            try {
                this.mExtView.getClass().getMethod("setExtViewHost", Object.class).invoke(this.mExtView, host);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchMethodException e2) {
            throw new RuntimeException(e2);
        }
    }

    public View getExtView() {
        return this.mExtView;
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void onExtViewModeCanceled() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mExtView, "onExtViewModeCanceled");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void onExtViewModeComplete() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mExtView, "onExtViewModeComplete");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void onWidgetModeComplete() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mExtView, "onWidgetModeComplete");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void notifyWidgetDeleted() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mExtView, "notifyWidgetDeleted");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void notifyExtViewAvailable() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mExtView, "notifyExtViewAvailable");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void notifyWidgetReset() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mExtView, "notifyWidgetReset");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void notifyRequestExtViewException() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mExtView, "notifyRequestExtViewException");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void notifyWidgetHostDestroyed() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mExtView, "notifyWidgetHostDestroyed");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void notifyBindingStarted() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mExtView, "notifyBindingStarted");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public boolean isWidgetUpdateSkippable() {
        return LgeReflectionUtil.callBooleanMethodWithVoidParameter(this.mExtView, "isWidgetUpdateSkippable");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewClient
    public void notifyClickOutSide() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mExtView, "notifyClickOutSide");
    }
}
