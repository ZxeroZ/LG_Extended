package com.android.launcher3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseRecyclerView extends RecyclerView implements RecyclerView.OnItemTouchListener {
    private static final int SCROLL_DELTA_THRESHOLD_DP = 4;
    protected Rect mBackgroundPadding;
    private float mDeltaThreshold;
    private int mDownX;
    private int mDownY;
    int mDy;
    private int mLastY;
    protected BaseRecyclerViewFastScrollBar mScrollbar;

    public static class ScrollPositionState {
        public int rowHeight;
        public int rowIndex;
        public int rowTopOffset;
    }

    public int getFastScrollerThumbInactiveColor(int defaultInactiveThumbColor) {
        return defaultInactiveThumbColor;
    }

    public int getFastScrollerTrackColor(int defaultTrackColor) {
        return defaultTrackColor;
    }

    public void onFastScrollCompleted() {
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public abstract void onUpdateScrollbar();

    public abstract String scrollToPositionAtProgress(float touchFraction);

    public BaseRecyclerView(Context context) {
        this(context, null);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDy = 0;
        this.mBackgroundPadding = new Rect();
        this.mDeltaThreshold = getResources().getDisplayMetrics().density * 4.0f;
        this.mScrollbar = new BaseRecyclerViewFastScrollBar(this, getResources());
        setOnScrollListener(new ScrollListener());
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        public ScrollListener() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            BaseRecyclerView.this.mDy = dy;
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        addOnItemTouchListener(this);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent ev) {
        return handleTouchEvent(ev);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
    public void onTouchEvent(RecyclerView rv, MotionEvent ev) {
        handleTouchEvent(ev);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0026  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean handleTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            int r0 = r5.getAction()
            float r1 = r5.getX()
            int r1 = (int) r1
            float r2 = r5.getY()
            int r2 = (int) r2
            if (r0 == 0) goto L35
            r1 = 1
            if (r0 == r1) goto L26
            r1 = 2
            if (r0 == r1) goto L1a
            r1 = 3
            if (r0 == r1) goto L26
            goto L4f
        L1a:
            r4.mLastY = r2
            com.android.launcher3.BaseRecyclerViewFastScrollBar r0 = r4.mScrollbar
            int r1 = r4.mDownX
            int r3 = r4.mDownY
            r0.handleTouchEvent(r5, r1, r3, r2)
            goto L4f
        L26:
            r4.onFastScrollCompleted()
            com.android.launcher3.BaseRecyclerViewFastScrollBar r0 = r4.mScrollbar
            int r1 = r4.mDownX
            int r2 = r4.mDownY
            int r3 = r4.mLastY
            r0.handleTouchEvent(r5, r1, r2, r3)
            goto L4f
        L35:
            r4.mDownX = r1
            r4.mLastY = r2
            r4.mDownY = r2
            boolean r0 = r4.shouldStopScroll(r5)
            if (r0 == 0) goto L44
            r4.stopScroll()
        L44:
            com.android.launcher3.BaseRecyclerViewFastScrollBar r0 = r4.mScrollbar
            int r1 = r4.mDownX
            int r2 = r4.mDownY
            int r3 = r4.mLastY
            r0.handleTouchEvent(r5, r1, r2, r3)
        L4f:
            com.android.launcher3.BaseRecyclerViewFastScrollBar r5 = r4.mScrollbar
            boolean r5 = r5.isDragging()
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.BaseRecyclerView.handleTouchEvent(android.view.MotionEvent):boolean");
    }

    protected boolean shouldStopScroll(MotionEvent ev) {
        return ev.getAction() == 0 && ((float) Math.abs(this.mDy)) < this.mDeltaThreshold && getScrollState() != 0;
    }

    public void updateBackgroundPadding(Rect padding) {
        this.mBackgroundPadding.set(padding);
    }

    public Rect getBackgroundPadding() {
        return this.mBackgroundPadding;
    }

    public int getMaxScrollbarWidth() {
        return this.mScrollbar.getThumbMaxWidth();
    }

    protected int getAvailableScrollHeight(int rowCount, int rowHeight, int yOffset) {
        return (((getPaddingTop() + yOffset) + (rowCount * rowHeight)) + getPaddingBottom()) - ((getHeight() - this.mBackgroundPadding.top) - this.mBackgroundPadding.bottom);
    }

    protected int getAvailableScrollBarHeight() {
        return ((getHeight() - this.mBackgroundPadding.top) - this.mBackgroundPadding.bottom) - this.mScrollbar.getThumbHeight();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        onUpdateScrollbar();
        this.mScrollbar.draw(canvas);
    }

    protected void synchronizeScrollBarThumbOffsetToViewScroll(ScrollPositionState scrollPosState, int rowCount, int yOffset) {
        int width;
        int availableScrollHeight = getAvailableScrollHeight(rowCount, scrollPosState.rowHeight, yOffset);
        int availableScrollBarHeight = getAvailableScrollBarHeight();
        if (availableScrollHeight <= 0) {
            this.mScrollbar.setScrollbarThumbOffset(-1, -1);
            return;
        }
        int paddingTop = this.mBackgroundPadding.top + ((int) (((((getPaddingTop() + yOffset) + (scrollPosState.rowIndex * scrollPosState.rowHeight)) - scrollPosState.rowTopOffset) / availableScrollHeight) * availableScrollBarHeight));
        if (Utilities.isRtl(getResources())) {
            width = this.mBackgroundPadding.left;
        } else {
            width = (getWidth() - this.mBackgroundPadding.right) - this.mScrollbar.getWidth();
        }
        this.mScrollbar.setScrollbarThumbOffset(width, paddingTop);
    }
}
