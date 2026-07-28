package com.lge.launcher3.pageindicator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.launcher3.PageIndicator;
import com.android.launcher3.PageIndicatorMarker;
import com.lge.launcher3.R;
import com.lge.launcher3.adaptive.AdaptiveTextUtil;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class PageIndicatorMarkerExtension extends PageIndicatorMarker {
    private static final String TAG = "PageIndicatorMarkerExtension";
    private boolean mIsAddMaker;
    private PageIndicator.PageMarkerResources mResource;

    public PageIndicatorMarkerExtension(Context context) {
        this(context, null);
    }

    public PageIndicatorMarkerExtension(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorMarkerExtension(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIsAddMaker = false;
        this.mResource = null;
    }

    public boolean getIsAddIconMaker() {
        return this.mIsAddMaker;
    }

    public boolean setIsAddIconMaker(boolean isAddMarker) {
        this.mIsAddMaker = isAddMarker;
        return isAddMarker;
    }

    @Override // com.android.launcher3.PageIndicatorMarker
    public void setMarkerDrawables(int activeResId, int inactiveResId) {
        super.setMarkerDrawables(activeResId, inactiveResId);
        if (inactiveResId == R.drawable.ic_pageindicator_add) {
            setIsAddIconMaker(true);
        } else {
            setIsAddIconMaker(false);
        }
    }

    public void setColor(int acolor, int inacolor) {
        if (acolor == 0 && inacolor == 0) {
            if (Utilities.isLGUI7_1()) {
                this.mActiveMarker.setImageTintList(null);
                this.mInactiveMarker.setImageTintList(null);
                return;
            }
            return;
        }
        Drawable drawable = this.mActiveMarker.getDrawable();
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(acolor);
        } else {
            drawable.setTint(acolor);
        }
        Drawable drawable2 = this.mInactiveMarker.getDrawable();
        if (drawable2 instanceof GradientDrawable) {
            ((GradientDrawable) drawable2).setColor(inacolor);
        } else {
            drawable2.setTint(inacolor);
        }
    }

    public void setAdaptiveColor(int color) {
        int color2;
        if (color == 0) {
            return;
        }
        if (AdaptiveTextUtil.isDarkColor(color)) {
            color2 = getContext().getResources().getColor(R.color.workspace_adaptive_color2);
        } else {
            color2 = getContext().getResources().getColor(R.color.workspace_adaptive_color1);
        }
        if (DDTUtils.isAdditionalThemeApplied(getContext())) {
            return;
        }
        this.mActiveMarker.getDrawable().setTint(color2);
        this.mInactiveMarker.getDrawable().setTint(color2);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setMarkerResource(PageIndicator.PageMarkerResources resource) {
        this.mResource = resource;
    }

    public PageIndicator.PageMarkerResources getMarkerResource() {
        return this.mResource;
    }
}
