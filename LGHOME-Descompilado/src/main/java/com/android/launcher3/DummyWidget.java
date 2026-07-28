package com.android.launcher3;

import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class DummyWidget implements CustomAppWidget {
    @Override // com.android.launcher3.CustomAppWidget
    public int getIcon() {
        return 0;
    }

    @Override // com.android.launcher3.CustomAppWidget
    public String getLabel() {
        return "Dumb Launcher Widget";
    }

    @Override // com.android.launcher3.CustomAppWidget
    public int getMinSpanX() {
        return 1;
    }

    @Override // com.android.launcher3.CustomAppWidget
    public int getMinSpanY() {
        return 1;
    }

    @Override // com.android.launcher3.CustomAppWidget
    public int getPreviewImage() {
        return 0;
    }

    @Override // com.android.launcher3.CustomAppWidget
    public int getResizeMode() {
        return 3;
    }

    @Override // com.android.launcher3.CustomAppWidget
    public int getSpanX() {
        return 2;
    }

    @Override // com.android.launcher3.CustomAppWidget
    public int getSpanY() {
        return 2;
    }

    @Override // com.android.launcher3.CustomAppWidget
    public int getWidgetLayout() {
        return R.layout.dummy_widget;
    }
}
