package com.lge.lgewidgetlib.extview;

import android.animation.ValueAnimator;
import android.appwidget.AppWidgetHostView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.lge.lgewidgetlib.LgeAppWidgetHostView;
import com.lge.lgewidgetlib.LgeReflectionUtil;
import com.lge.lgewidgetlib.WLog;

/* JADX INFO: loaded from: classes2.dex */
abstract class ExtViewHandler implements View.OnTouchListener, ValueAnimator.AnimatorUpdateListener {
    static final String TAG = "ExtViewEffect";
    WidgetAnimator mAnimationMngr;
    IAppWidgetExtViewClient mClient;
    ExtViewEventListener mEventListener;
    ExtViewContainerLayout mExtViewLayout;
    LgeAppWidgetHostView mHostView;
    IExtViewHostAdapter mLauncherIF;
    FrameLayout.LayoutParams mWidgetViewParam;
    LgeAppWidgetExtViewClientProxy mExtViewClient = null;
    View mColorView = null;
    View mWidgetHostLayer = null;

    abstract void attachWidget(View view);

    abstract void detachWidget(View view);

    abstract int getExtViewHeight();

    abstract FrameLayout.LayoutParams getExtViewParam(View view);

    ExtViewHandler(LgeAppWidgetHostView view, IAppWidgetExtViewClient client, IExtViewHostAdapter launcherIf, ExtViewEventListener eventListener) {
        this.mExtViewLayout = null;
        this.mHostView = view;
        this.mLauncherIF = launcherIf;
        this.mExtViewLayout = new ExtViewContainerLayout(this.mHostView.getContext(), this, this);
        this.mEventListener = eventListener;
        this.mClient = client;
    }

    public void setExtViewBackgroudDimming(boolean enable, int endAlpha) {
        WLog.d(TAG, "setExtViewBackgroundDimming = " + enable);
        this.mExtViewLayout.setDimming(enable, endAlpha);
    }

    public void setExtViewBackgroudDimming(boolean enable, int endAlpha, int duration) {
        WLog.d(TAG, "setExtViewBackgroundDimming = " + enable + ", " + duration);
        this.mExtViewLayout.setDimming(enable, endAlpha, duration);
    }

    protected void transitionToExtView() {
        if (this.mLauncherIF != null) {
            View widgetView = getWidgetView();
            this.mWidgetViewParam = (FrameLayout.LayoutParams) widgetView.getLayoutParams();
            WLog.i(TAG, "try attaching ExtView to dragLayout");
            attachWidget(this.mExtViewLayout);
            this.mHostView.removeView(widgetView);
            FrameLayout.LayoutParams extViewParam = getExtViewParam(widgetView);
            this.mColorView = new View(this.mHostView.getContext());
            FrameLayout.LayoutParams extViewParam2 = getExtViewParam(widgetView);
            extViewParam2.topMargin = getTopMarginOfExtView();
            this.mExtViewLayout.addView(this.mColorView, extViewParam2);
            this.mExtViewLayout.addView(widgetView, extViewParam);
            WLog.i(TAG, "transitionToExtView Success");
        }
    }

    int[] getLocationOfBaseLayer() {
        int[] iArr = new int[2];
        ((View) this.mHostView.getParent()).getLocationInWindow(iArr);
        return iArr;
    }

    protected int getTopMarginOfExtView() {
        return getLocationOfBaseLayer()[1] + ((this.mHostView.getHeight() - getWidgetView().getHeight()) / 2);
    }

    void transitionToNormalView() {
        if (this.mLauncherIF == null || this.mWidgetViewParam == null) {
            return;
        }
        View view = this.mColorView;
        if (view != null) {
            this.mExtViewLayout.removeView(view);
            this.mColorView = null;
        }
        View view2 = this.mWidgetHostLayer;
        if (view2 != null) {
            view2.setVisibility(0);
            this.mWidgetHostLayer = null;
        }
        View widgetView = getWidgetView();
        this.mExtViewLayout.removeView(widgetView);
        detachWidget(this.mExtViewLayout);
        widgetView.setLayoutParams(this.mWidgetViewParam);
        this.mHostView.addView(widgetView);
        this.mWidgetViewParam = null;
    }

    final View getWidgetView() {
        return (View) LgeReflectionUtil.getPrivateField(AppWidgetHostView.class, this.mHostView, "mView");
    }

    public void setShowColorView(boolean isVisible) {
        View view = this.mColorView;
        if (view == null) {
            return;
        }
        view.setVisibility(isVisible ? 0 : 4);
    }

    private void initAnimator(View[] expandingViews) {
        View widgetView = getWidgetView();
        WidgetAnimator widgetAnimator = this.mAnimationMngr;
        if (widgetAnimator == null) {
            this.mAnimationMngr = new WidgetAnimator(widgetView, expandingViews, getExtViewHeight(), getTopMarginOfExtView(), this.mEventListener);
        } else {
            widgetAnimator.reset();
        }
    }

    void startAnimation(View[] expandingViews) {
        initAnimator(expandingViews);
        if (this.mAnimationMngr.isNeedToBeMoved()) {
            setShowColorView(false);
            View hostViewBlurLayout = this.mLauncherIF.getHostViewBlurLayout(this.mHostView);
            this.mWidgetHostLayer = hostViewBlurLayout;
            hostViewBlurLayout.setVisibility(4);
        } else {
            this.mColorView.setBackgroundColor(this.mLauncherIF.calcExtWidgetBg(0.0f, true));
        }
        this.mAnimationMngr.start();
    }

    void reverseAnimation() {
        if (this.mAnimationMngr.isNeedToBeMoved()) {
            setShowColorView(false);
        }
        this.mAnimationMngr.reverse();
    }

    void cancelAnimation() {
        WidgetAnimator widgetAnimator = this.mAnimationMngr;
        if (widgetAnimator == null || !widgetAnimator.isRunning()) {
            return;
        }
        this.mAnimationMngr.cancel();
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View arg0, MotionEvent arg1) {
        IAppWidgetExtViewClient iAppWidgetExtViewClient = this.mClient;
        if (iAppWidgetExtViewClient == null) {
            return false;
        }
        iAppWidgetExtViewClient.notifyClickOutSide();
        return false;
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator animation) {
        if (this.mColorView == null) {
            return;
        }
        this.mColorView.setBackgroundColor(this.mLauncherIF.calcExtWidgetBg(animation.getAnimatedFraction(), !this.mAnimationMngr.isNeedToBeMoved()));
    }
}
