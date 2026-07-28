package com.android.quickstep.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import com.android.quickstep.views.RecentsView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class ClearAllButton extends Button implements RecentsView.PageCallbacks {
    private static final String TAG = "ClearAllButton";
    public static final FloatProperty<ClearAllButton> VISIBILITY_ALPHA = new FloatProperty<ClearAllButton>("visibilityAlpha") { // from class: com.android.quickstep.views.ClearAllButton.1
        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(ClearAllButton view) {
            return Float.valueOf(view.mVisibilityAlpha);
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(ClearAllButton view, float v) {
            view.setVisibilityAlpha(v);
        }
    };
    private float mContentAlpha;
    private int mHiddenFlags;
    private boolean mIsRtl;
    private float mScrollAlpha;
    private int mScrollOffset;
    private float mSplitSelectScrollOffsetPrimary;
    private float mVisibilityAlpha;

    @Override // android.widget.TextView, android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // com.android.quickstep.views.RecentsView.PageCallbacks
    public void onPageScroll(RecentsView.ScrollState scrollState) {
    }

    public ClearAllButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScrollAlpha = 1.0f;
        this.mContentAlpha = 1.0f;
        this.mVisibilityAlpha = 1.0f;
        this.mIsRtl = getLayoutDirection() == 1;
        this.mVisibilityAlpha = getAlpha();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private RecentsView getRecentsView() {
        if (getParent() != null) {
            return (RecentsView) ((View) getParent()).findViewById(R.id.overview_panel);
        }
        LGLog.w(TAG, "getRecentsView is null", new int[0]);
        return null;
    }

    @Override // android.widget.TextView, android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        this.mIsRtl = getLayoutDirection() == 1;
    }

    public void setContentAlpha(float alpha) {
        if (this.mContentAlpha != alpha) {
            this.mContentAlpha = alpha;
            updateAlpha();
        }
    }

    public void setVisibilityAlpha(float alpha) {
        if (alpha == 1.0f || alpha == 0.0f) {
            if (getRecentsView() != null && getRecentsView().getChildCount() == 0) {
                alpha = 0.0f;
            }
            if (this.mVisibilityAlpha != alpha) {
                this.mVisibilityAlpha = alpha;
                updateAlpha();
            }
        }
    }

    private void updateAlpha() {
        float f = this.mScrollAlpha * this.mContentAlpha * this.mVisibilityAlpha;
        setAlpha(f);
        setClickable(f == 1.0f);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void updateHiddenFlags(int visibilityFlags, boolean enable) {
        if (enable) {
            this.mHiddenFlags = visibilityFlags | this.mHiddenFlags;
        } else {
            this.mHiddenFlags = (~visibilityFlags) & this.mHiddenFlags;
        }
        setVisibility(this.mHiddenFlags != 0 ? 4 : 0);
    }

    public void setSplitSelectScrollOffsetPrimary(float splitSelectScrollOffsetPrimary) {
        this.mSplitSelectScrollOffsetPrimary = splitSelectScrollOffsetPrimary;
    }
}
