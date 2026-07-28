package com.lge.lgewidgetlib.extview;

import android.view.View;
import android.widget.FrameLayout;

/* JADX INFO: loaded from: classes2.dex */
public interface IExtViewHostAdapter {
    void attachWidgetToExtLayer(View view, FrameLayout.LayoutParams lp);

    int calcExtWidgetBg(float alphaFraction, boolean keepWidgetBg);

    void detachWidgetFromExtLayer(View view);

    View getHostViewBlurLayout(View hostView);

    int getScreenHeight();

    int getWorkSpaceWidth();

    boolean isExtViewAvailable();

    void setWidgetExtHandler(IWidgetExtHandler widgetExtHandler);
}
