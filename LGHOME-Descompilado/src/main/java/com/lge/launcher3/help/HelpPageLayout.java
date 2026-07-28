package com.lge.launcher3.help;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes.dex */
public class HelpPageLayout extends LinearLayout {
    public HelpPageLayout(Context context) {
        this(context, null);
    }

    public HelpPageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HelpPageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
