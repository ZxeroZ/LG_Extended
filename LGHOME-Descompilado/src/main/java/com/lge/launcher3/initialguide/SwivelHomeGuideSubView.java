package com.lge.launcher3.initialguide;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SwivelHomeGuideSubView extends FrameLayout {
    private static String TAG = "SwivelHomeGuideSubView";
    List<Rect> mExclusionRects;

    public SwivelHomeGuideSubView(Context context) {
        this(context, null);
    }

    public SwivelHomeGuideSubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwivelHomeGuideSubView(Context context, AttributeSet attrs, int defStyle) {
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
