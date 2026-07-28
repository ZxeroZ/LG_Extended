package com.lge.lgewidgetlib.extview;

import android.view.View;
import com.lge.lgewidgetlib.LgeReflectionUtil;
import com.lge.lgewidgetlib.WLog;
import java.lang.reflect.InvocationTargetException;

/* JADX INFO: loaded from: classes2.dex */
public class LgeAppWidgetExtViewHostProxy implements IAppWidgetExtViewHost {
    private static final String TAG = "LgeAppWidgetExtViewHostProxy";
    private Object mHost;

    public LgeAppWidgetExtViewHostProxy(Object host) {
        this.mHost = host;
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public boolean requestExtView(View[] views) {
        try {
            try {
                return ((Boolean) this.mHost.getClass().getMethod("requestExtView", View[].class).invoke(this.mHost, views)).booleanValue();
            } catch (InvocationTargetException unused) {
                WLog.e(TAG, "requestExtView : handle requestExtView exception");
                try {
                    this.mHost.getClass().getMethod("cancelExtView", new Class[0]).invoke(this.mHost, new Object[0]);
                } catch (Exception e) {
                    WLog.e(TAG, "requestExtView : handle cancelExtView exception");
                    e.printStackTrace();
                }
                try {
                    this.mHost.getClass().getMethod("notifyRequestExtViewException", new Class[0]).invoke(this.mHost, new Object[0]);
                } catch (Exception e2) {
                    WLog.e(TAG, "requestExtView : handle notifyRequestExtViewException exception");
                    e2.printStackTrace();
                }
                return false;
            } catch (Exception e3) {
                e3.printStackTrace();
                return false;
            }
        } catch (NoSuchMethodException e4) {
            e4.printStackTrace();
            return false;
        }
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public void requestNormalView() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mHost, "requestNormalView");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public int getExtendedWidgetHeight() {
        return LgeReflectionUtil.callIntMethodWithVoidParameter(this.mHost, "getExtendedWidgetHeight");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public void clientExpandAnimationFinished() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mHost, "clientExpandAnimationFinished");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public void clientRestoreAnimationFinished() {
        LgeReflectionUtil.callVoidMethodWithVoidParameter(this.mHost, "clientRestoreAnimationFinished");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public boolean isExtViewAvailable() {
        return LgeReflectionUtil.callBooleanMethodWithVoidParameter(this.mHost, "isExtViewAvailable");
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public void requestExtViewDimming(boolean enable, int endAlpha) {
        LgeReflectionUtil.callVoidMethodWithBooleanIntegerParameter(this.mHost, "requestExtViewDimming", enable, endAlpha);
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public void requestExtViewDimming(boolean enable, int endAlpha, int duration) {
        LgeReflectionUtil.callVoidMethodWithBooleanIntegerIntegerParameter(this.mHost, "requestExtViewDimming", enable, endAlpha, duration);
    }

    @Override // com.lge.lgewidgetlib.extview.IAppWidgetExtViewHost
    public boolean isImprovedExtHost() {
        return LgeReflectionUtil.callBooleanMethodWithVoidParameter(this.mHost, "isImprovedExtHost");
    }
}
