package com.lge.launcher3.initialguide;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SwivelHomeGuideMainView extends FrameLayout {
    private static String TAG = "SwivelHomeGuideMainView";
    List<Rect> mExclusionRects;

    public SwivelHomeGuideMainView(Context context) {
        this(context, null);
    }

    public SwivelHomeGuideMainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwivelHomeGuideMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mExclusionRects = new ArrayList();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mExclusionRects.add(new Rect(left, top, right, bottom));
        setSystemGestureExclusionRects(this.mExclusionRects);
    }
}
