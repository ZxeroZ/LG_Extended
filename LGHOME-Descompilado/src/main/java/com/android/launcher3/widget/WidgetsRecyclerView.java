package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.launcher3.BaseRecyclerView;
import com.android.launcher3.model.WidgetsModel;
import com.lge.launcher3.R;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsRecyclerView extends BaseRecyclerView {
    private static final String TAG = "WidgetsRecyclerView";
    private BaseRecyclerView.ScrollPositionState mScrollPosState;
    private WidgetsModel mWidgets;

    public WidgetsRecyclerView(Context context) {
        this(context, null);
    }

    public WidgetsRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetsRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mScrollPosState = new BaseRecyclerView.ScrollPositionState();
    }

    public WidgetsRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    @Override // com.android.launcher3.BaseRecyclerView, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        addOnItemTouchListener(this);
    }

    @Override // com.android.launcher3.BaseRecyclerView
    public int getFastScrollerTrackColor(int defaultTrackColor) {
        return Utilities.sWhite;
    }

    @Override // com.android.launcher3.BaseRecyclerView
    public int getFastScrollerThumbInactiveColor(int defaultInactiveThumbColor) {
        return getResources().getColor(R.color.widgets_view_fastscroll_thumb_inactive_color);
    }

    public void setWidgets(WidgetsModel widgets) {
        this.mWidgets = widgets;
    }

    @Override // com.android.launcher3.BaseRecyclerView, android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipRect(this.mBackgroundPadding.left, this.mBackgroundPadding.top, getWidth() - this.mBackgroundPadding.right, getHeight() - this.mBackgroundPadding.bottom);
        super.dispatchDraw(canvas);
    }

    @Override // com.android.launcher3.BaseRecyclerView
    public String scrollToPositionAtProgress(float touchFraction) {
        int packageSize = this.mWidgets.getPackageSize();
        if (packageSize == 0) {
            return "";
        }
        stopScroll();
        getCurScrollState(this.mScrollPosState);
        float f = packageSize * touchFraction;
        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(0, (int) (-(getAvailableScrollHeight(packageSize, this.mScrollPosState.rowHeight, 0) * touchFraction)));
        if (touchFraction == 1.0f) {
            f -= 1.0f;
        }
        return this.mWidgets.getPackageItemInfo((int) f).titleSectionName;
    }

    @Override // com.android.launcher3.BaseRecyclerView
    public void onUpdateScrollbar() {
        int packageSize = this.mWidgets.getPackageSize();
        if (packageSize == 0) {
            this.mScrollbar.setScrollbarThumbOffset(-1, -1);
            return;
        }
        getCurScrollState(this.mScrollPosState);
        if (this.mScrollPosState.rowIndex < 0) {
            this.mScrollbar.setScrollbarThumbOffset(-1, -1);
        } else {
            synchronizeScrollBarThumbOffsetToViewScroll(this.mScrollPosState, packageSize, 0);
        }
    }

    private void getCurScrollState(BaseRecyclerView.ScrollPositionState stateOut) {
        stateOut.rowIndex = -1;
        stateOut.rowTopOffset = -1;
        stateOut.rowHeight = -1;
        if (this.mWidgets.getPackageSize() == 0) {
            return;
        }
        View childAt = getChildAt(0);
        stateOut.rowIndex = getChildPosition(childAt);
        stateOut.rowTopOffset = getLayoutManager().getDecoratedTop(childAt);
        stateOut.rowHeight = childAt.getHeight();
    }
}
