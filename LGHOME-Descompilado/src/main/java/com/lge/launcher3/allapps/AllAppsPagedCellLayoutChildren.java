package com.lge.launcher3.allapps;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsPagedCellLayoutChildren extends ViewGroup {
    static final String TAG = "AllAppsPagedCellLayoutChildren";
    private int mCellHeight;
    private int mCellWidth;
    private boolean mCenterContent;
    private int mCountX;
    private int mCountY;
    private int mHeightGap;
    private boolean mInvertIfRtl;
    private int mWidthGap;

    public AllAppsPagedCellLayoutChildren(Context context) {
        super(context);
        this.mInvertIfRtl = false;
    }

    @Override // android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                childAt.cancelLongPress();
            }
        }
    }

    public void setGap(int widthGap, int heightGap) {
        this.mWidthGap = widthGap;
        this.mHeightGap = heightGap;
        requestLayout();
    }

    public void setCellDimensions(int cellWidth, int cellHeight, int widthGap, int heightGap, int countX, int countY) {
        this.mCellWidth = cellWidth;
        this.mCellHeight = cellHeight;
        this.mWidthGap = widthGap;
        this.mHeightGap = heightGap;
        this.mCountX = countX;
        this.mCountY = countY;
        requestLayout();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (child != null) {
            Rect rect = new Rect();
            child.getDrawingRect(rect);
            requestRectangleOnScreen(rect);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        int mode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        int size2 = View.MeasureSpec.getSize(heightMeasureSpec);
        if (mode == 0 || mode2 == 0) {
            throw new RuntimeException("CellLayout cannot have UNSPECIFIED dimensions");
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam = (AllAppsPagedCellLayoutParam) childAt.getLayoutParams();
                allAppsPagedCellLayoutParam.setup(this.mCellWidth, this.mCellHeight, this.mWidthGap, this.mHeightGap, invertLayoutHorizontally(), this.mCountX);
                childAt.measure(View.MeasureSpec.makeMeasureSpec(allAppsPagedCellLayoutParam.width, 1073741824), View.MeasureSpec.makeMeasureSpec(allAppsPagedCellLayoutParam.height, 1073741824));
            }
        }
        setMeasuredDimension(size, size2);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measuredWidth;
        int childCount = getChildCount();
        if (!this.mCenterContent || childCount <= 0) {
            measuredWidth = 0;
        } else {
            int iMin = Integer.MAX_VALUE;
            int iMax = 0;
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt != null && childAt.getVisibility() != 8) {
                    AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam = (AllAppsPagedCellLayoutParam) childAt.getLayoutParams();
                    iMin = Math.min(iMin, allAppsPagedCellLayoutParam.x);
                    iMax = Math.max(iMax, allAppsPagedCellLayoutParam.x + allAppsPagedCellLayoutParam.width);
                }
            }
            measuredWidth = (getMeasuredWidth() - (iMax - iMin)) / 2;
        }
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt2 = getChildAt(i2);
            if (childAt2 != null && childAt2.getVisibility() != 8) {
                AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam2 = (AllAppsPagedCellLayoutParam) childAt2.getLayoutParams();
                int i3 = allAppsPagedCellLayoutParam2.x + measuredWidth;
                int i4 = allAppsPagedCellLayoutParam2.y;
                childAt2.layout(i3, i4, allAppsPagedCellLayoutParam2.width + i3, allAppsPagedCellLayoutParam2.height + i4);
            }
        }
    }

    void destroyHardwareLayer() {
        setLayerType(0, null);
    }

    void createHardwareLayer() {
        setLayerType(2, null);
    }

    public void enableCenteredContent(boolean enabled) {
        this.mCenterContent = enabled;
    }

    @Override // android.view.ViewGroup
    protected void setChildrenDrawingCacheEnabled(boolean enabled) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                childAt.setDrawingCacheEnabled(enabled);
                if (!childAt.isHardwareAccelerated()) {
                    childAt.buildDrawingCache(true);
                }
            }
        }
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View child) {
        super.onViewAdded(child);
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
    }

    public View getChildAtForPagedViewCellLayoutChildren(int x, int y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam = (AllAppsPagedCellLayoutParam) childAt.getLayoutParams();
                if (allAppsPagedCellLayoutParam.cellX <= x && x < allAppsPagedCellLayoutParam.cellX + allAppsPagedCellLayoutParam.cellHSpan && allAppsPagedCellLayoutParam.cellY <= y && y < allAppsPagedCellLayoutParam.cellY + allAppsPagedCellLayoutParam.cellVSpan) {
                    return childAt;
                }
            }
        }
        return null;
    }

    public void setInvertIfRtl(boolean invert) {
        this.mInvertIfRtl = invert;
    }

    public boolean invertLayoutHorizontally() {
        return this.mInvertIfRtl && Utilities.isRtl(getResources());
    }
}
