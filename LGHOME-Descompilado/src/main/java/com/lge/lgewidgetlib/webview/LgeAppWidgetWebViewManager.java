package com.lge.lgewidgetlib.webview;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.lge.lgewidgetlib.LgeAppWidgetHostView;
import com.lge.lgewidgetlib.LgeReflectionUtil;

/* JADX INFO: loaded from: classes2.dex */
public class LgeAppWidgetWebViewManager {
    private Context mContext;
    private LgeAppWidgetHostView mHostView;

    public LgeAppWidgetWebViewManager(Context context, LgeAppWidgetHostView hostView) {
        this.mHostView = hostView;
        this.mContext = context;
    }

    public void init() {
        findWebView(getWidgetView());
    }

    public View getWidgetView() {
        return (View) LgeReflectionUtil.getPrivateField(AppWidgetHostView.class, this.mHostView, "mView");
    }

    private void findWebView(View view) {
        if (isWebView(view)) {
            initWebView(view);
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                findWebView(viewGroup.getChildAt(i));
            }
        }
    }

    private boolean isWebView(View view) {
        try {
            view.getClass().getMethod("initWebView", Context.class);
            return true;
        } catch (NoSuchMethodException unused) {
            return false;
        }
    }

    private void initWebView(View view) {
        try {
            try {
                view.getClass().getMethod("initWebView", Context.class).invoke(view, this.mContext);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchMethodException unused) {
        }
    }
}
