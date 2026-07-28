package com.android.quickstep.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.android.quickstep.views.RecentsView;

/* JADX INFO: loaded from: classes.dex */
public class RecentsExtraViewContainer extends FrameLayout implements RecentsView.PageCallbacks {
    private boolean mScrollable;

    public RecentsExtraViewContainer(Context context) {
        super(context);
        this.mScrollable = false;
    }

    public RecentsExtraViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScrollable = false;
    }

    public RecentsExtraViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mScrollable = false;
    }

    public boolean isScrollable() {
        return this.mScrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.mScrollable = scrollable;
    }
}
