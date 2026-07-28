package com.lge.launcher3.allapps;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.lge.launcher3.R;
import com.lge.launcher3.infos.HomeConfiguration;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class PagedViewCellLayout extends ViewGroup implements Page {
    static final String TAG = "PagedViewCellLayout";
    protected int mCellCountX;
    protected int mCellCountY;
    protected int mCellHeight;
    protected int mCellWidth;
    protected AllAppsPagedCellLayoutChildren mChildren;
    protected int mHeightGap;
    protected int mOriginalCellHeight;
    protected int mOriginalCellWidth;
    private int mOriginalHeightGap;
    private int mOriginalWidthGap;
    protected int mWidthGap;

    public void allowHardwareLayerCreation() {
    }

    public PagedViewCellLayout(Context context) {
        this(context, null);
    }

    public PagedViewCellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedViewCellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAlwaysDrawnWithCacheEnabled(false);
        if (context != null) {
            Resources resources = context.getResources();
            if (resources != null) {
                this.mOriginalCellWidth = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_width);
                this.mOriginalCellHeight = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_height);
                this.mCellCountX = HomeConfiguration.getCellCountX(context);
                this.mCellCountY = HomeConfiguration.getCellCountY(context);
                this.mHeightGap = 0;
                this.mWidthGap = 0;
                this.mOriginalHeightGap = 0;
                this.mOriginalWidthGap = 0;
            }
            AllAppsPagedCellLayoutChildren allAppsPagedCellLayoutChildren = new AllAppsPagedCellLayoutChildren(context);
            this.mChildren = allAppsPagedCellLayoutChildren;
            if (allAppsPagedCellLayoutChildren != null) {
                allAppsPagedCellLayoutChildren.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mWidthGap, this.mHeightGap, this.mCellCountX, this.mCellCountY);
                addView(this.mChildren);
            }
        }
    }

    public int getCellWidth() {
        return this.mCellWidth;
    }

    public int getCellHeight() {
        return this.mCellHeight;
    }

    @Override // android.view.View
    public void setAlpha(float alpha) {
        this.mChildren.setAlpha(alpha);
    }

    public void destroyHardwareLayers() {
        AllAppsPagedCellLayoutChildren allAppsPagedCellLayoutChildren = this.mChildren;
        if (allAppsPagedCellLayoutChildren != null) {
            allAppsPagedCellLayoutChildren.destroyHardwareLayer();
        }
    }

    public void createHardwareLayers() {
        AllAppsPagedCellLayoutChildren allAppsPagedCellLayoutChildren = this.mChildren;
        if (allAppsPagedCellLayoutChildren != null) {
            allAppsPagedCellLayoutChildren.createHardwareLayer();
        }
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

    public boolean addViewToCellLayout(View child, int index, int childId, AllAppsPagedCellLayoutParam params) {
        if (this.mChildren == null || params.cellX < 0 || params.cellX > this.mCellCountX - 1 || params.cellY < 0 || params.cellY > this.mCellCountY - 1) {
            return false;
        }
        if (params.cellHSpan < 0) {
            params.cellHSpan = this.mCellCountX;
        }
        if (params.cellVSpan < 0) {
            params.cellVSpan = this.mCellCountY;
        }
        child.setId(childId);
        this.mChildren.addView(child, index, params);
        child.setHapticFeedbackEnabled(false);
        return true;
    }

    @Override // com.lge.launcher3.allapps.Page
    public void removeAllViewsOnPage() {
        this.mChildren.removeAllViews();
        destroyHardwareLayers();
    }

    @Override // com.lge.launcher3.allapps.Page
    public void removeViewOnPageAt(int index) {
        this.mChildren.removeViewAt(index);
    }

    @Override // com.lge.launcher3.allapps.Page
    public int getPageChildCount() {
        return this.mChildren.getChildCount();
    }

    public AllAppsPagedCellLayoutChildren getChildrenLayout() {
        return this.mChildren;
    }

    @Override // com.lge.launcher3.allapps.Page
    public View getChildOnPageAt(int i) {
        return this.mChildren.getChildAt(i);
    }

    public View getChildOnPageAt(int cellX, int cellY) {
        for (int i = 0; i < this.mChildren.getChildCount(); i++) {
            View childAt = this.mChildren.getChildAt(i);
            AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam = (AllAppsPagedCellLayoutParam) childAt.getLayoutParams();
            if (allAppsPagedCellLayoutParam.cellX == cellX && allAppsPagedCellLayoutParam.cellY == cellY) {
                return childAt;
            }
        }
        return null;
    }

    @Override // com.lge.launcher3.allapps.Page
    public int indexOfChildOnPage(View v) {
        return this.mChildren.indexOfChild(v);
    }

    public int getCellCountX() {
        return this.mCellCountX;
    }

    public int getCellCountY() {
        return this.mCellCountY;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        int mode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        int size2 = View.MeasureSpec.getSize(heightMeasureSpec);
        if (mode == 0 || mode2 == 0) {
            LGLog.i(TAG, "CellLayout cannot have UNSPECIFIED dimensions. widthSpecSize = " + size + ", heightSpecSize = " + size2);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int i = this.mCellCountX - 1;
        int i2 = this.mCellCountY - 1;
        int i3 = (size - this.mPaddingLeft) - this.mPaddingRight;
        int i4 = (size2 - this.mPaddingTop) - this.mPaddingBottom;
        int i5 = this.mOriginalWidthGap;
        this.mWidthGap = i5;
        int i6 = this.mOriginalHeightGap;
        this.mHeightGap = i6;
        int i7 = this.mCellCountX;
        int i8 = (i3 - (i * i5)) / i7;
        this.mCellWidth = i8;
        int i9 = this.mCellCountY;
        int i10 = (i4 - (i2 * i6)) / i9;
        this.mCellHeight = i10;
        this.mChildren.setCellDimensions(i8, i10, i5, i6, i7, i9);
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(i4, 1073741824);
        int childCount = getChildCount();
        for (int i11 = 0; i11 < childCount; i11++) {
            View childAt = getChildAt(i11);
            if (childAt != null) {
                childAt.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
            }
        }
        setMeasuredDimension(size, size2);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                childAt.layout(this.mPaddingLeft, this.mPaddingTop, (r - l) - this.mPaddingRight, (b - t) - this.mPaddingBottom);
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        View childOnPageAt;
        boolean zOnTouchEvent = super.onTouchEvent(event);
        int pageChildCount = getPageChildCount();
        if (pageChildCount <= 0 || (childOnPageAt = getChildOnPageAt(pageChildCount - 1)) == null) {
            return zOnTouchEvent;
        }
        int bottom = childOnPageAt.getBottom();
        if (((int) Math.ceil(getPageChildCount() / getCellCountX())) < getCellCountY()) {
            bottom += this.mCellHeight / 2;
        }
        return zOnTouchEvent || event.getY() < ((float) bottom);
    }

    public void enableCenteredContent(boolean enabled) {
        AllAppsPagedCellLayoutChildren allAppsPagedCellLayoutChildren = this.mChildren;
        if (allAppsPagedCellLayoutChildren != null) {
            allAppsPagedCellLayoutChildren.enableCenteredContent(enabled);
        }
    }

    @Override // android.view.ViewGroup
    public void setChildrenDrawingCacheEnabled(boolean enabled) {
        this.mChildren.setChildrenDrawingCacheEnabled(enabled);
    }

    public void setCellCount(int xCount, int yCount) {
        this.mCellCountX = xCount;
        this.mCellCountY = yCount;
        requestLayout();
    }

    public void setGap(int widthGap, int heightGap) {
        this.mWidthGap = widthGap;
        this.mHeightGap = heightGap;
        AllAppsPagedCellLayoutChildren allAppsPagedCellLayoutChildren = this.mChildren;
        if (allAppsPagedCellLayoutChildren != null) {
            allAppsPagedCellLayoutChildren.setGap(widthGap, heightGap);
        }
    }

    public int[] getCellCountForDimensions(int width, int height) {
        int iMin = Math.min(this.mCellWidth, this.mCellHeight);
        return new int[]{(width + iMin) / iMin, (height + iMin) / iMin};
    }

    void onDragChild(View child) {
        ((AllAppsPagedCellLayoutParam) child.getLayoutParams()).isDragging = true;
    }

    public int estimateCellHSpan(int width) {
        int i = width - ((this.mPaddingLeft * 2) + (this.mPaddingRight * 2));
        int i2 = this.mWidthGap;
        return Math.max(1, (i + i2) / (this.mOriginalCellWidth + i2));
    }

    public int estimateCellVSpan(int height) {
        int i = height - (this.mPaddingTop + this.mPaddingBottom);
        int i2 = this.mHeightGap;
        return Math.max(1, (i + i2) / (this.mOriginalCellHeight + i2));
    }

    public int[] estimateCellPosition(int x, int y) {
        return new int[]{this.mPaddingLeft + (this.mCellWidth * x) + (x * this.mWidthGap) + (this.mOriginalCellWidth / 2), this.mPaddingTop + (this.mCellHeight * y) + (y * this.mHeightGap) + (this.mOriginalCellHeight / 2)};
    }

    public void calculateCellCount(int width, int height, int maxCellCountX, int maxCellCountY) {
        this.mCellCountX = Math.min(maxCellCountX, estimateCellHSpan(width));
        this.mCellCountY = Math.min(maxCellCountY, estimateCellVSpan(height));
        requestLayout();
    }

    public int estimateCellWidth(int hSpan) {
        return hSpan * this.mOriginalCellWidth;
    }

    public int estimateCellHeight(int vSpan) {
        return vSpan * this.mOriginalCellHeight;
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AllAppsPagedCellLayoutParam(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof AllAppsPagedCellLayoutParam;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new AllAppsPagedCellLayoutParam(p);
    }
}
