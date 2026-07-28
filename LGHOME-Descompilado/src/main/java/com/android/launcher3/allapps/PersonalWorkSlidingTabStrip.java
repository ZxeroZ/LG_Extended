package com.android.launcher3.allapps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.launcher3.Utilities;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.util.Themes;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsHost;

/* JADX INFO: loaded from: classes.dex */
public class PersonalWorkSlidingTabStrip extends LinearLayout implements PageIndicator {
    private static final int POSITION_PERSONAL = 0;
    private static final int POSITION_WORK = 1;
    private AllAppsHost mAllAppsHost;
    private final Paint mDividerPaint;
    private int mIndicatorLeft;
    private int mIndicatorRight;
    private boolean mIsRtl;
    private int mLastActivePage;
    private float mScrollOffset;
    private int mSelectedIndicatorHeight;
    private final Paint mSelectedIndicatorPaint;
    private int mSelectedPosition;

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // com.android.launcher3.pageindicators.PageIndicator
    public void setMarkersCount(int numMarkers) {
    }

    public PersonalWorkSlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIndicatorLeft = -1;
        this.mIndicatorRight = -1;
        this.mSelectedPosition = 0;
        this.mLastActivePage = 0;
        setOrientation(0);
        setWillNotDraw(false);
        this.mSelectedIndicatorHeight = getResources().getDimensionPixelSize(R.dimen.all_apps_tabs_indicator_height);
        Paint paint = new Paint();
        this.mSelectedIndicatorPaint = paint;
        paint.setColor(Themes.getAttrColor(context, android.R.attr.colorAccent));
        Paint paint2 = new Paint();
        this.mDividerPaint = paint2;
        paint2.setColor(Themes.getAttrColor(context, android.R.attr.colorControlHighlight));
        paint2.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.all_apps_divider_height));
        this.mIsRtl = Utilities.isRtl(getResources());
    }

    public void updateTabTextColor(int pos) {
        this.mSelectedPosition = pos;
        int i = 0;
        while (i < getChildCount()) {
            ((Button) getChildAt(i)).setSelected(i == pos);
            i++;
        }
    }

    private void updateIndicatorPosition(float scrollOffset) {
        this.mScrollOffset = scrollOffset;
        updateIndicatorPosition();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        updateTabTextColor(this.mSelectedPosition);
        updateIndicatorPosition(this.mScrollOffset);
    }

    private void updateIndicatorPosition() {
        int width;
        View leftTab = getLeftTab();
        int left = -1;
        if (leftTab != null) {
            left = (int) (leftTab.getLeft() + (leftTab.getWidth() * this.mScrollOffset));
            width = leftTab.getWidth() + left;
        } else {
            width = -1;
        }
        setIndicatorPosition(left, width);
    }

    private View getLeftTab() {
        return getChildAt(this.mIsRtl ? 1 : 0);
    }

    private void setIndicatorPosition(int left, int right) {
        if (left == this.mIndicatorLeft && right == this.mIndicatorRight) {
            return;
        }
        this.mIndicatorLeft = left;
        this.mIndicatorRight = right;
        invalidate();
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float height = getHeight() - this.mDividerPaint.getStrokeWidth();
        canvas.drawLine(getPaddingLeft(), height, getWidth() - getPaddingRight(), height, this.mDividerPaint);
        canvas.drawRect(this.mIndicatorLeft, getHeight() - this.mSelectedIndicatorHeight, this.mIndicatorRight, getHeight(), this.mSelectedIndicatorPaint);
    }

    @Override // com.android.launcher3.pageindicators.PageIndicator
    public void setScroll(int currentScroll, int totalScroll) {
        updateIndicatorPosition(currentScroll / totalScroll);
    }

    @Override // com.android.launcher3.pageindicators.PageIndicator
    public void setActiveMarker(int activePage) {
        updateTabTextColor(activePage);
        updateIndicatorPosition(activePage);
        AllAppsHost allAppsHost = this.mAllAppsHost;
        if (allAppsHost != null && this.mLastActivePage != activePage) {
            allAppsHost.onTabChanged(activePage);
        }
        this.mLastActivePage = activePage;
    }

    public void setAllAppsHost(AllAppsHost containerView) {
        this.mAllAppsHost = containerView;
    }
}
