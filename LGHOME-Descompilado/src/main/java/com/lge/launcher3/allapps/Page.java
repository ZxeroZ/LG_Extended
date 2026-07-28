package com.lge.launcher3.allapps;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public interface Page {
    View getChildOnPageAt(int i);

    int getPageChildCount();

    int indexOfChildOnPage(View v);

    void removeAllViewsOnPage();

    void removeViewOnPageAt(int i);
}
