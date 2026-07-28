package com.lge.launcher3.smartbulletin.view;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import com.android.launcher3.LogDecelerateInterpolator;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class SBOverScrollView extends ScrollView {
    private static final int OVERSCROLL_CHILD_GAP = 100;
    private static final int SPRING_BACK_DURATION = 1000;
    private static final String TAG = "SBOverScrollView";
    private int mDeltaY;
    private TimeInterpolator mInterpolator;
    private boolean mIsTouching;
    private int mMaxOverScrollY;
    private int mMaxScrollRange;
    private int mStartTime;
    private int mStartY;
    private boolean mUsingScrollBy;

    public SBOverScrollView(Context context) {
        super(context);
        this.mMaxScrollRange = 0;
        this.mStartTime = 0;
        this.mStartY = 0;
        this.mMaxOverScrollY = 0;
        this.mDeltaY = 0;
        this.mIsTouching = false;
        this.mUsingScrollBy = false;
        this.mInterpolator = new LogDecelerateInterpolator(100, 0);
    }

    public SBOverScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMaxScrollRange = 0;
        this.mStartTime = 0;
        this.mStartY = 0;
        this.mMaxOverScrollY = 0;
        this.mDeltaY = 0;
        this.mIsTouching = false;
        this.mUsingScrollBy = false;
        this.mInterpolator = new LogDecelerateInterpolator(100, 0);
    }

    public SBOverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mMaxScrollRange = 0;
        this.mStartTime = 0;
        this.mStartY = 0;
        this.mMaxOverScrollY = 0;
        this.mDeltaY = 0;
        this.mIsTouching = false;
        this.mUsingScrollBy = false;
        this.mInterpolator = new LogDecelerateInterpolator(100, 0);
    }

    private View getSBCategoryLayout() {
        View childAt = null;
        for (int i = 0; i < getChildCount(); i++) {
            if (i == getChildCount() - 1) {
                childAt = getChildAt(i);
            }
        }
        return childAt;
    }

    private int getMaxScrollRange() {
        View sBCategoryLayout = getSBCategoryLayout();
        if (sBCategoryLayout != null) {
            return sBCategoryLayout.getMeasuredHeight() - getBottom();
        }
        return -1;
    }

    private void resetGap() {
        View sBCategoryLayout = getSBCategoryLayout();
        sBCategoryLayout.setTranslationY(0.0f);
        int i = 0;
        while (true) {
            SBCategoryLayout sBCategoryLayout2 = (SBCategoryLayout) sBCategoryLayout;
            if (i >= sBCategoryLayout2.getChildCount()) {
                return;
            }
            sBCategoryLayout2.getChildAt(i).setTranslationY(0.0f);
            i++;
        }
    }

    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        resetGap();
        int iMax = Math.max(getMaxScrollRange(), 0);
        this.mMaxScrollRange = iMax;
        this.mMaxOverScrollY = iMax != 0 ? ((SBCategoryLayout) getSBCategoryLayout()).getChildCount() * 100 : 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0011  */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0014  */
    @Override // android.widget.ScrollView, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            int r0 = r5.getAction()
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L14
            if (r0 == r2) goto L11
            r3 = 2
            if (r0 == r3) goto L14
            r2 = 3
            if (r0 == r2) goto L11
            goto L18
        L11:
            r4.mIsTouching = r1
            goto L18
        L14:
            r4.mIsTouching = r2
            r4.mUsingScrollBy = r1
        L18:
            boolean r5 = super.onTouchEvent(r5)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.smartbulletin.view.SBOverScrollView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // android.view.View
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (this.mUsingScrollBy && this.mDeltaY != deltaY) {
            return true;
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, this.mMaxScrollRange, maxOverScrollX, this.mMaxOverScrollY, isTouchEvent);
    }

    private boolean reachTopEdge() {
        return getScrollY() < 0;
    }

    private boolean reachBottomEdge() {
        return getScrollY() > this.mMaxScrollRange;
    }

    private float getAddScroll(float scroll, float index, float lastindex) {
        return (Math.abs(scroll) / lastindex) * index;
    }

    @Override // android.widget.ScrollView
    public void fling(int velocityY) {
        if (reachTopEdge() || reachBottomEdge()) {
            return;
        }
        super.fling(velocityY);
    }

    @Override // android.view.View
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.mMaxScrollRange > 0) {
            if (reachTopEdge() || reachBottomEdge()) {
                View sBCategoryLayout = getSBCategoryLayout();
                SBCategoryLayout sBCategoryLayout2 = (SBCategoryLayout) sBCategoryLayout;
                int childCount = sBCategoryLayout2.getChildCount() - 1;
                int i = 0;
                if (reachTopEdge()) {
                    sBCategoryLayout.setTranslationY(getScrollY());
                    while (i <= childCount) {
                        sBCategoryLayout2.getChildAt(i).setTranslationY(getAddScroll(getScrollY(), i, childCount));
                        i++;
                    }
                } else if (reachBottomEdge()) {
                    sBCategoryLayout.setTranslationY(getScrollY() - this.mMaxScrollRange);
                    while (i <= childCount) {
                        sBCategoryLayout2.getChildAt(i).setTranslationY(-getAddScroll(getScrollY() - this.mMaxScrollRange, childCount - i, childCount));
                        i++;
                    }
                }
                postInvalidate();
            }
        }
    }

    @Override // android.widget.ScrollView, android.view.View
    public void computeScroll() {
        if (this.mMaxScrollRange > 0 && reachTopEdge() && !this.mIsTouching) {
            if (!this.mUsingScrollBy) {
                this.mStartTime = (int) AnimationUtils.currentAnimationTimeMillis();
                this.mStartY = getScrollY();
                this.mUsingScrollBy = true;
            }
            float interpolation = this.mInterpolator.getInterpolation(((int) (AnimationUtils.currentAnimationTimeMillis() - ((long) this.mStartTime))) / 1000.0f);
            int iAbs = Math.abs((this.mStartY + Math.round(interpolation * (-r1))) - getScrollY());
            this.mDeltaY = iAbs;
            overScrollBy(0, iAbs, 0, getScrollY(), 0, this.mMaxScrollRange, 0, this.mMaxOverScrollY, false);
            return;
        }
        if (this.mMaxScrollRange > 0 && reachBottomEdge() && !this.mIsTouching) {
            if (!this.mUsingScrollBy) {
                this.mStartTime = (int) AnimationUtils.currentAnimationTimeMillis();
                this.mStartY = getScrollY();
                this.mUsingScrollBy = true;
            }
            float interpolation2 = this.mInterpolator.getInterpolation(((int) (AnimationUtils.currentAnimationTimeMillis() - ((long) this.mStartTime))) / 1000.0f);
            int i = -Math.abs((this.mStartY + Math.round(interpolation2 * (this.mMaxScrollRange - r1))) - getScrollY());
            this.mDeltaY = i;
            overScrollBy(0, i, 0, getScrollY(), 0, this.mMaxScrollRange, 0, this.mMaxOverScrollY, false);
            return;
        }
        super.computeScroll();
    }

    @Override // android.widget.ScrollView, android.view.View
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (this.mUsingScrollBy && clampedY) {
            clampedY = false;
            LGLog.i(TAG, "change clampedY from true to false");
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }
}
