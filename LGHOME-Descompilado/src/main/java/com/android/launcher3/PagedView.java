package com.android.launcher3;

import android.animation.LayoutTransition;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.ScrollView;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.touch.OverScroll;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.uioverrides.DeviceFlag;
import com.android.launcher3.util.OverScroller;
import com.android.launcher3.views.ActivityContext;
import com.lge.launcher3.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class PagedView<T extends View & com.android.launcher3.pageindicators.PageIndicator> extends ViewGroup {
    public static final int ACTION_MOVE_ALLOW_EASY_FLING = 254;
    protected static final boolean DEBUG = false;
    public static final boolean DEBUG_FAILED_QUICKSWITCH = false;
    private static final int EASY_FLING_THRESHOLD_VELOCITY = 400;
    private static final int FLING_THRESHOLD_VELOCITY = 500;
    public static final int INVALID_PAGE = -1;
    protected static final int INVALID_POINTER = -1;
    private static final float MAX_SCROLL_PROGRESS = 1.0f;
    private static final int MIN_FLING_VELOCITY = 250;
    private static final int MIN_SNAP_VELOCITY = 1500;
    private static final int OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION = 270;
    public static final int PAGE_SNAP_ANIMATION_DURATION = 750;
    private static final float RETURN_TO_ORIGINAL_PAGE_THRESHOLD = 0.33f;
    private static final float SIGNIFICANT_MOVE_THRESHOLD = 0.4f;
    protected static final ComputePageScrollsLogic SIMPLE_SCROLL_LOGIC = new ComputePageScrollsLogic() { // from class: com.android.launcher3.-$$Lambda$PagedView$bFsGWHKJCiy-iqldga8RW5Ge_gk
        @Override // com.android.launcher3.PagedView.ComputePageScrollsLogic
        public final boolean shouldIncludeView(View view) {
            return PagedView.lambda$static$0(view);
        }
    };
    private static final String TAG = "PagedView";
    protected int mActivePointerId;
    private boolean mAllowEasyFling;
    protected boolean mAllowOverScroll;

    @ViewDebug.ExportedProperty(category = DeviceFlag.NAMESPACE_LAUNCHER)
    protected int mCurrentPage;
    private Interpolator mDefaultInterpolator;
    private float mDownMotionPrimary;
    private float mDownMotionX;
    private float mDownMotionY;
    protected final int mEasyFlingThresholdVelocity;
    protected boolean mFirstLayout;
    protected final int mFlingThresholdVelocity;
    private boolean mFreeScroll;
    protected final Rect mInsets;
    protected boolean mIsBeingDragged;
    protected boolean mIsLayoutValid;
    protected boolean mIsPageInTransition;
    protected boolean mIsRtl;
    private float mLastMotion;
    private float mLastMotionRemainder;
    protected int mMaxScroll;
    private int mMaximumVelocity;
    protected final int mMinFlingVelocity;
    protected int mMinScroll;
    protected final int mMinSnapVelocity;

    @ViewDebug.ExportedProperty(category = DeviceFlag.NAMESPACE_LAUNCHER)
    protected int mNextPage;
    private Runnable mOnPageTransitionEndCallback;
    protected PagedOrientationHandler mOrientationHandler;
    protected T mPageIndicator;
    int mPageIndicatorViewId;
    protected int[] mPageScrolls;
    protected int mPageSlop;
    protected int mPageSpacing;
    protected OverScroller mScroller;
    protected float mSpringOverScroll;
    private int[] mTmpIntPair;
    private float mTotalMotion;
    protected int mTouchSlop;
    protected int mUnboundedScroll;
    private VelocityTracker mVelocityTracker;
    protected boolean mWasInOverscroll;

    /* JADX INFO: Access modifiers changed from: protected */
    public interface ComputePageScrollsLogic {
        boolean shouldIncludeView(View view);
    }

    protected boolean canAnnouncePageDescription() {
        return true;
    }

    protected int computeMinScroll() {
        return 0;
    }

    protected int getChildGap() {
        return 0;
    }

    protected int indexToPage(int index) {
        return index;
    }

    protected boolean isPageOrderFlipped() {
        return false;
    }

    protected void onPageBeginTransition() {
    }

    protected void onScrollInteractionBegin() {
    }

    protected void onScrollInteractionEnd() {
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: ?: TERNARY null = ((wrap:int:0x0000: INVOKE (r1v0 android.view.View) VIRTUAL call: android.view.View.getVisibility():int A[MD:():int (c), WRAPPED] (LINE:81)) != (8 int)) ? true : false */
    static /* synthetic */ boolean lambda$static$0(View view) {
        return view.getVisibility() != 8;
    }

    public PagedView(Context context) {
        this(context, null);
    }

    public PagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFreeScroll = false;
        this.mFirstLayout = true;
        this.mNextPage = -1;
        this.mPageSpacing = 0;
        this.mOrientationHandler = PagedOrientationHandler.PORTRAIT;
        this.mAllowOverScroll = true;
        this.mActivePointerId = -1;
        this.mIsPageInTransition = false;
        this.mWasInOverscroll = false;
        this.mInsets = new Rect();
        this.mTmpIntPair = new int[2];
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.PagedView, defStyle, 0);
        this.mPageIndicatorViewId = typedArrayObtainStyledAttributes.getResourceId(2, -1);
        typedArrayObtainStyledAttributes.recycle();
        setHapticFeedbackEnabled(false);
        this.mIsRtl = Utilities.isRtl(getResources());
        this.mScroller = new OverScroller(context);
        setDefaultInterpolator(Interpolators.SCROLL);
        this.mCurrentPage = 0;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mPageSlop = viewConfiguration.getScaledPagingTouchSlop();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        float f = getResources().getDisplayMetrics().density;
        this.mFlingThresholdVelocity = (int) (500.0f * f);
        this.mEasyFlingThresholdVelocity = (int) (400.0f * f);
        this.mMinFlingVelocity = (int) (250.0f * f);
        this.mMinSnapVelocity = (int) (f * 1500.0f);
        if (Utilities.ATLEAST_OREO) {
            setDefaultFocusHighlightEnabled(false);
        }
    }

    protected void setDefaultInterpolator(Interpolator interpolator) {
        this.mDefaultInterpolator = interpolator;
        this.mScroller.setInterpolator(interpolator);
    }

    public void initParentViews(View view) {
        int i = this.mPageIndicatorViewId;
        if (i > -1) {
            T t = (T) view.findViewById(i);
            this.mPageIndicator = t;
            t.setMarkersCount(getChildCount());
        }
    }

    public T getPageIndicator() {
        return this.mPageIndicator;
    }

    public int getCurrentPage() {
        return this.mCurrentPage;
    }

    public int getNextPage() {
        int i = this.mNextPage;
        return i != -1 ? i : this.mCurrentPage;
    }

    public int getPageCount() {
        return getChildCount();
    }

    public View getPageAt(int index) {
        return getChildAt(index);
    }

    protected void updateCurrentPageScroll() {
        int i = this.mCurrentPage;
        int scrollForPage = (i < 0 || i >= getPageCount()) ? 0 : getScrollForPage(this.mCurrentPage);
        this.mOrientationHandler.set(this, (PagedOrientationHandler.Int2DAction<PagedView<T>>) PagedOrientationHandler.VIEW_SCROLL_TO, scrollForPage);
        this.mOrientationHandler.scrollerStartScroll(this.mScroller, scrollForPage);
        forceFinishScroller(true);
    }

    private void abortScrollerAnimation(boolean resetNextPage) {
        this.mScroller.abortAnimation();
        if (resetNextPage) {
            this.mNextPage = -1;
            pageEndTransition();
        }
    }

    protected void forceFinishScroller(boolean resetNextPage) {
        this.mScroller.forceFinished(true);
        if (resetNextPage) {
            this.mNextPage = -1;
            pageEndTransition();
        }
    }

    protected int validateNewPage(int newPage) {
        return Utilities.boundToRange(ensureWithinScrollBounds(newPage), 0, getPageCount() - 1);
    }

    /* JADX DEBUG: Duplicate block (B:18:0x0033) to fix multi-entry loop: BACK_EDGE: B:16:0x002a -> B:18:0x0033 */
    private int ensureWithinScrollBounds(int page) {
        int i = !this.mIsRtl ? 1 : -1;
        int scrollForPage = getScrollForPage(page);
        while (true) {
            if (scrollForPage >= this.mMinScroll) {
                break;
            }
            page += i;
            int scrollForPage2 = getScrollForPage(page);
            if (scrollForPage2 <= scrollForPage) {
                Log.e(TAG, "validateNewPage: failed to find a page > mMinScrollX");
                scrollForPage = scrollForPage2;
                break;
            }
            scrollForPage = scrollForPage2;
        }
        while (true) {
            if (scrollForPage <= this.mMaxScroll) {
                break;
            }
            page -= i;
            int scrollForPage3 = getScrollForPage(page);
            if (scrollForPage3 >= scrollForPage) {
                Log.e(TAG, "validateNewPage: failed to find a page < mMaxScrollX");
                break;
            }
            scrollForPage = scrollForPage3;
        }
        return page;
    }

    public void setCurrentPage(int currentPage) {
        setCurrentPage(currentPage, -1);
    }

    public void setCurrentPage(int currentPage, int overridePrevPage) {
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(true);
        }
        if (getChildCount() == 0) {
            return;
        }
        if (overridePrevPage == -1) {
            overridePrevPage = this.mCurrentPage;
        }
        this.mCurrentPage = validateNewPage(currentPage);
        updateCurrentPageScroll();
        notifyPageSwitchListener(overridePrevPage);
        invalidate();
    }

    protected void notifyPageSwitchListener(int prevPage) {
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        T t = this.mPageIndicator;
        if (t != null) {
            t.setActiveMarker(getNextPage());
        }
    }

    protected void pageBeginTransition() {
        if (this.mIsPageInTransition) {
            return;
        }
        this.mIsPageInTransition = true;
        onPageBeginTransition();
    }

    protected void pageEndTransition() {
        if (this.mIsPageInTransition) {
            this.mIsPageInTransition = false;
            onPageEndTransition();
        }
    }

    protected boolean isPageInTransition() {
        return this.mIsPageInTransition;
    }

    protected void onPageEndTransition() {
        this.mWasInOverscroll = false;
        AccessibilityManagerCompat.sendScrollFinishedEventToTest(getContext());
        AccessibilityManagerCompat.sendCustomAccessibilityEvent(getPageAt(this.mCurrentPage), 8, null);
        Runnable runnable = this.mOnPageTransitionEndCallback;
        if (runnable != null) {
            runnable.run();
            this.mOnPageTransitionEndCallback = null;
        }
    }

    public void setOnPageTransitionEndCallback(Runnable callback) {
        if (this.mIsPageInTransition || callback == null) {
            this.mOnPageTransitionEndCallback = callback;
        } else {
            callback.run();
        }
    }

    protected int getUnboundedScroll() {
        return this.mUnboundedScroll;
    }

    @Override // android.view.View
    public void scrollBy(int x, int y) {
        this.mOrientationHandler.delegateScrollBy(this, getUnboundedScroll(), x, y);
    }

    @Override // android.view.View
    public void scrollTo(int x, int y) {
        int iIntValue = ((Integer) this.mOrientationHandler.getPrimaryValue(Integer.valueOf(x), Integer.valueOf(y))).intValue();
        int iIntValue2 = ((Integer) this.mOrientationHandler.getSecondaryValue(Integer.valueOf(x), Integer.valueOf(y))).intValue();
        this.mUnboundedScroll = iIntValue;
        boolean z = this.mIsRtl;
        boolean z2 = !z ? iIntValue >= this.mMinScroll : iIntValue <= this.mMaxScroll;
        boolean z3 = !z ? iIntValue <= this.mMaxScroll : iIntValue >= this.mMinScroll;
        if (!z2 && !z3) {
            this.mSpringOverScroll = 0.0f;
        }
        if (z2) {
            this.mOrientationHandler.delegateScrollTo(this, iIntValue2, z ? this.mMaxScroll : this.mMinScroll);
            if (this.mAllowOverScroll) {
                this.mWasInOverscroll = true;
                overScroll(iIntValue - (this.mIsRtl ? this.mMaxScroll : this.mMinScroll));
                return;
            }
            return;
        }
        if (z3) {
            this.mOrientationHandler.delegateScrollTo(this, iIntValue2, z ? this.mMinScroll : this.mMaxScroll);
            if (this.mAllowOverScroll) {
                this.mWasInOverscroll = true;
                overScroll(iIntValue - (this.mIsRtl ? this.mMinScroll : this.mMaxScroll));
                return;
            }
            return;
        }
        if (this.mWasInOverscroll) {
            overScroll(0);
            this.mWasInOverscroll = false;
        }
        super.scrollTo(x, y);
    }

    public void superScrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    private void sendScrollAccessibilityEvent() {
        if (!AccessibilityManagerCompat.isObservedEventType(getContext(), 4096) || this.mCurrentPage == getNextPage()) {
            return;
        }
        AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(4096);
        accessibilityEventObtain.setScrollable(true);
        accessibilityEventObtain.setScrollX(getScrollX());
        accessibilityEventObtain.setScrollY(getScrollY());
        this.mOrientationHandler.setMaxScroll(accessibilityEventObtain, this.mMaxScroll);
        sendAccessibilityEventUnchecked(accessibilityEventObtain);
    }

    protected boolean computeScrollHelper() {
        return computeScrollHelper(true);
    }

    protected void announcePageForAccessibility() {
        if (AccessibilityManagerCompat.isAccessibilityEnabled(getContext())) {
            announceForAccessibility(getCurrentPageDescription());
        }
    }

    protected boolean computeScrollHelper(boolean shouldInvalidate) {
        if (this.mScroller.computeScrollOffset()) {
            int primaryScroll = this.mOrientationHandler.getPrimaryScroll(this);
            if (this.mUnboundedScroll != this.mScroller.getCurrPos() || primaryScroll != this.mScroller.getCurrPos()) {
                this.mOrientationHandler.set(this, (PagedOrientationHandler.Int2DAction<PagedView<T>>) PagedOrientationHandler.VIEW_SCROLL_TO, this.mScroller.getCurrPos());
            }
            if (!shouldInvalidate) {
                return true;
            }
            invalidate();
            return true;
        }
        if (this.mNextPage == -1 || !shouldInvalidate) {
            return false;
        }
        sendScrollAccessibilityEvent();
        int i = this.mCurrentPage;
        this.mCurrentPage = validateNewPage(this.mNextPage);
        this.mNextPage = -1;
        notifyPageSwitchListener(i);
        if (!this.mIsBeingDragged) {
            pageEndTransition();
        }
        if (!canAnnouncePageDescription()) {
            return false;
        }
        announcePageForAccessibility();
        return false;
    }

    @Override // android.view.View
    public void computeScroll() {
        computeScrollHelper();
    }

    public int getExpectedHeight() {
        return getMeasuredHeight();
    }

    public int getNormalChildHeight() {
        return (((getExpectedHeight() - getPaddingTop()) - getPaddingBottom()) - this.mInsets.top) - this.mInsets.bottom;
    }

    public int getExpectedWidth() {
        return getMeasuredWidth();
    }

    public int getNormalChildWidth() {
        return (((getExpectedWidth() - getPaddingLeft()) - getPaddingRight()) - this.mInsets.left) - this.mInsets.right;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        this.mIsLayoutValid = false;
        super.requestLayout();
    }

    @Override // android.view.View
    public void forceLayout() {
        this.mIsLayoutValid = false;
        super.forceLayout();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        int mode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        int size2 = View.MeasureSpec.getSize(heightMeasureSpec);
        if (mode == 0 || mode2 == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else if (size <= 0 || size2 <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureChildren(View.MeasureSpec.makeMeasureSpec((size - this.mInsets.left) - this.mInsets.right, 1073741824), View.MeasureSpec.makeMeasureSpec((size2 - this.mInsets.top) - this.mInsets.bottom, 1073741824));
            setMeasuredDimension(size, size2);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        boolean z;
        int i;
        this.mIsLayoutValid = true;
        int childCount = getChildCount();
        int[] iArr = this.mPageScrolls;
        if (iArr == null || childCount != iArr.length) {
            this.mPageScrolls = new int[childCount];
            z = true;
        } else {
            z = false;
        }
        if (childCount == 0) {
            return;
        }
        boolean z2 = getPageScrolls(this.mPageScrolls, true, SIMPLE_SCROLL_LOGIC) ? true : z;
        LayoutTransition layoutTransition = getLayoutTransition();
        if (layoutTransition != null && layoutTransition.isRunning()) {
            layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() { // from class: com.android.launcher3.PagedView.1
                @Override // android.animation.LayoutTransition.TransitionListener
                public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                }

                @Override // android.animation.LayoutTransition.TransitionListener
                public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                    if (transition.isRunning()) {
                        return;
                    }
                    transition.removeTransitionListener(this);
                    PagedView.this.updateMinAndMaxScrollX();
                }
            });
        } else {
            updateMinAndMaxScrollX();
        }
        if (this.mFirstLayout && (i = this.mCurrentPage) >= 0 && i < childCount) {
            updateCurrentPageScroll();
            this.mFirstLayout = false;
        }
        if (this.mScroller.isFinished() && z2) {
            setCurrentPage(getNextPage());
        }
    }

    protected boolean getPageScrolls(int[] outPageScrolls, boolean layoutChildren, ComputePageScrollsLogic scrollLogic) {
        int childCount = getChildCount();
        boolean z = this.mIsRtl;
        if (z) {
            childCount = -1;
        }
        int i = z ? -1 : 1;
        int centerForPage = this.mOrientationHandler.getCenterForPage(this, this.mInsets);
        int scrollOffsetStart = this.mOrientationHandler.getScrollOffsetStart(this, this.mInsets);
        int scrollOffsetEnd = this.mOrientationHandler.getScrollOffsetEnd(this, this.mInsets);
        boolean z2 = false;
        int childGap = scrollOffsetStart;
        for (int i2 = z ? childCount - 1 : 0; i2 != childCount; i2 += i) {
            View pageAt = getPageAt(i2);
            if (scrollLogic.shouldIncludeView(pageAt)) {
                PagedOrientationHandler.ChildBounds childBounds = this.mOrientationHandler.getChildBounds(pageAt, childGap, centerForPage, layoutChildren);
                int i3 = childBounds.primaryDimension;
                int iMax = this.mIsRtl ? childGap - scrollOffsetStart : Math.max(0, childBounds.childPrimaryEnd - scrollOffsetEnd);
                if (outPageScrolls[i2] != iMax) {
                    outPageScrolls[i2] = iMax;
                    z2 = true;
                }
                childGap += i3 + this.mPageSpacing + getChildGap();
            }
        }
        return z2;
    }

    protected void updateMinAndMaxScrollX() {
        this.mMinScroll = computeMinScroll();
        this.mMaxScroll = computeMaxScroll();
    }

    protected int computeMaxScroll() {
        int childCount = getChildCount();
        if (childCount > 0) {
            return getScrollForPage(this.mIsRtl ? 0 : childCount - 1);
        }
        return 0;
    }

    public void setPageSpacing(int pageSpacing) {
        this.mPageSpacing = pageSpacing;
        requestLayout();
    }

    public int getPageSpacing() {
        return this.mPageSpacing;
    }

    private void dispatchPageCountChanged() {
        T t = this.mPageIndicator;
        if (t != null) {
            t.setMarkersCount(getChildCount());
        }
        invalidate();
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        dispatchPageCountChanged();
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        this.mCurrentPage = validateNewPage(this.mCurrentPage);
        dispatchPageCountChanged();
    }

    protected int getChildOffset(int index) {
        if (index < 0 || index > getChildCount() - 1) {
            return 0;
        }
        return this.mOrientationHandler.getChildStart(getPageAt(index));
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        int iIndexToPage = indexToPage(indexOfChild(child));
        if (iIndexToPage == this.mCurrentPage && this.mScroller.isFinished()) {
            return false;
        }
        if (immediate) {
            setCurrentPage(iIndexToPage);
            return true;
        }
        snapToPage(iIndexToPage);
        return true;
    }

    @Override // android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        int i = this.mNextPage;
        if (i == -1) {
            i = this.mCurrentPage;
        }
        View pageAt = getPageAt(i);
        if (pageAt != null) {
            return pageAt.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (super.dispatchUnhandledMove(focused, direction)) {
            return true;
        }
        if (this.mIsRtl) {
            if (direction == 17) {
                direction = 66;
            } else if (direction == 66) {
                direction = 17;
            }
        }
        if (direction == 17) {
            if (getCurrentPage() <= 0) {
                return false;
            }
            snapToPage(getCurrentPage() - 1);
            getChildAt(getCurrentPage() - 1).requestFocus(direction);
            return true;
        }
        if (direction != 66 || getCurrentPage() >= getPageCount() - 1) {
            return false;
        }
        snapToPage(getCurrentPage() + 1);
        getChildAt(getCurrentPage() + 1).requestFocus(direction);
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (getDescendantFocusability() == 393216) {
            return;
        }
        int i = this.mCurrentPage;
        if (i >= 0 && i < getPageCount()) {
            getPageAt(this.mCurrentPage).addFocusables(views, direction, focusableMode);
        }
        if (direction == 17) {
            int i2 = this.mCurrentPage;
            if (i2 > 0) {
                getPageAt(i2 - 1).addFocusables(views, direction, focusableMode);
                return;
            }
            return;
        }
        if (direction != 66 || this.mCurrentPage >= getPageCount() - 1) {
            return;
        }
        getPageAt(this.mCurrentPage + 1).addFocusables(views, direction, focusableMode);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void focusableViewAvailable(View focused) {
        View pageAt = getPageAt(this.mCurrentPage);
        for (View view = focused; view != pageAt; view = (View) view.getParent()) {
            if (view == this || !(view.getParent() instanceof View)) {
                return;
            }
        }
        super.focusableViewAvailable(focused);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            getPageAt(this.mCurrentPage).cancelLongPress();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0037  */
    @Override // android.view.ViewGroup
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            int r0 = r5.getChildCount()
            r1 = 0
            if (r0 > 0) goto L8
            return r1
        L8:
            r5.acquireVelocityTrackerAndAddMovement(r6)
            int r0 = r6.getAction()
            r2 = 1
            r3 = 2
            if (r0 != r3) goto L18
            boolean r4 = r5.mIsBeingDragged
            if (r4 == 0) goto L18
            return r2
        L18:
            r0 = r0 & 255(0xff, float:3.57E-43)
            if (r0 == 0) goto L3b
            if (r0 == r2) goto L37
            if (r0 == r3) goto L2e
            r1 = 3
            if (r0 == r1) goto L37
            r1 = 6
            if (r0 == r1) goto L27
            goto L61
        L27:
            r5.onSecondaryPointerUp(r6)
            r5.releaseVelocityTracker()
            goto L61
        L2e:
            int r0 = r5.mActivePointerId
            r1 = -1
            if (r0 == r1) goto L61
            r5.determineScrollingStart(r6)
            goto L61
        L37:
            r5.resetTouchState()
            goto L61
        L3b:
            float r0 = r6.getX()
            float r2 = r6.getY()
            r5.mDownMotionX = r0
            r5.mDownMotionY = r2
            com.android.launcher3.touch.PagedOrientationHandler r0 = r5.mOrientationHandler
            float r0 = r0.getPrimaryDirection(r6, r1)
            r5.mLastMotion = r0
            r5.mDownMotionPrimary = r0
            r0 = 0
            r5.mLastMotionRemainder = r0
            r5.mTotalMotion = r0
            r5.mAllowEasyFling = r1
            int r6 = r6.getPointerId(r1)
            r5.mActivePointerId = r6
            r5.updateIsBeingDraggedOnTouchDown()
        L61:
            boolean r6 = r5.mIsBeingDragged
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.PagedView.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    private void updateIsBeingDraggedOnTouchDown() {
        if (this.mScroller.isFinished() || Math.abs(this.mScroller.getFinalPos() - this.mScroller.getCurrPos()) < this.mPageSlop / 3) {
            this.mIsBeingDragged = false;
            if (this.mScroller.isFinished() || this.mFreeScroll) {
                return;
            }
            setCurrentPage(getNextPage());
            pageEndTransition();
            return;
        }
        this.mIsBeingDragged = true;
    }

    public boolean isHandlingTouch() {
        return this.mIsBeingDragged;
    }

    protected void determineScrollingStart(MotionEvent ev) {
        determineScrollingStart(ev, 1.0f);
    }

    protected void determineScrollingStart(MotionEvent ev, float touchSlopScale) {
        int iFindPointerIndex = ev.findPointerIndex(this.mActivePointerId);
        if (iFindPointerIndex == -1) {
            return;
        }
        float primaryDirection = this.mOrientationHandler.getPrimaryDirection(ev, iFindPointerIndex);
        if (((int) Math.abs(primaryDirection - this.mLastMotion)) > Math.round(touchSlopScale * ((float) this.mTouchSlop)) || ev.getAction() == 254) {
            this.mIsBeingDragged = true;
            this.mTotalMotion += Math.abs(this.mLastMotion - primaryDirection);
            this.mLastMotion = primaryDirection;
            this.mLastMotionRemainder = 0.0f;
            onScrollInteractionBegin();
            pageBeginTransition();
            requestDisallowInterceptTouchEvent(true);
        }
    }

    protected void cancelCurrentPageLongPress() {
        View pageAt = getPageAt(this.mCurrentPage);
        if (pageAt != null) {
            pageAt.cancelLongPress();
        }
    }

    protected float getScrollProgress(int screenCenter, View v, int page) {
        int measuredWidth;
        int scrollForPage = screenCenter - (getScrollForPage(page) + (getMeasuredWidth() / 2));
        int childCount = getChildCount();
        int i = page + 1;
        if ((scrollForPage < 0 && !this.mIsRtl) || (scrollForPage > 0 && this.mIsRtl)) {
            i = page - 1;
        }
        if (i < 0 || i > childCount - 1) {
            measuredWidth = v.getMeasuredWidth() + this.mPageSpacing;
        } else {
            measuredWidth = Math.abs(getScrollForPage(i) - getScrollForPage(page));
        }
        return Math.max(Math.min(scrollForPage / (measuredWidth * 1.0f), 1.0f), -1.0f);
    }

    public int getScrollForPage(int index) {
        int[] iArr = this.mPageScrolls;
        if (iArr == null || index >= iArr.length || index < 0) {
            return 0;
        }
        return iArr[index];
    }

    public int getLayoutTransitionOffsetForPage(int index) {
        int[] iArr = this.mPageScrolls;
        if (iArr == null || index >= iArr.length || index < 0) {
            return 0;
        }
        return (int) (getChildAt(index).getX() - (this.mPageScrolls[index] + (this.mIsRtl ? getPaddingRight() : getPaddingLeft())));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.mScroller.isSpringing() && this.mSpringOverScroll != 0.0f) {
            int iSave = canvas.save();
            this.mOrientationHandler.set(canvas, PagedOrientationHandler.CANVAS_TRANSLATE, -this.mSpringOverScroll);
            super.dispatchDraw(canvas);
            canvas.restoreToCount(iSave);
            return;
        }
        super.dispatchDraw(canvas);
    }

    private int getSpringOverScroll(int amount) {
        if (!this.mScroller.isSpringing()) {
            return 0;
        }
        if (amount < 0) {
            return this.mScroller.getCurrPos() - this.mMinScroll;
        }
        return Math.max(0, this.mScroller.getCurrPos() - this.mMaxScroll);
    }

    protected void dampedOverScroll(int amount) {
        if (amount == 0) {
            return;
        }
        int iDampedScroll = OverScroll.dampedScroll(amount, this.mOrientationHandler.getMeasuredSize(this));
        if (this.mScroller.isSpringing()) {
            this.mSpringOverScroll = getSpringOverScroll(amount);
            invalidate();
        } else {
            this.mOrientationHandler.delegateScrollTo(this, Utilities.boundToRange(this.mOrientationHandler.getPrimaryScroll(this), this.mMinScroll, this.mMaxScroll) + iDampedScroll);
            invalidate();
        }
    }

    protected void overScroll(int amount) {
        if (this.mScroller.isSpringing()) {
            this.mSpringOverScroll = getSpringOverScroll(amount);
            invalidate();
        } else {
            if (amount == 0) {
                return;
            }
            if (this.mFreeScroll && !this.mScroller.isFinished()) {
                this.mOrientationHandler.delegateScrollTo(this, (amount < 0 ? this.mMinScroll : this.mMaxScroll) + amount);
            } else {
                dampedOverScroll(amount);
            }
        }
    }

    public void setEnableFreeScroll(boolean freeScroll) {
        boolean z = this.mFreeScroll;
        if (z == freeScroll) {
            return;
        }
        this.mFreeScroll = freeScroll;
        if (freeScroll) {
            setCurrentPage(getNextPage());
        } else {
            if (!z || getScrollForPage(getNextPage()) == getScrollX()) {
                return;
            }
            snapToPage(getNextPage());
        }
    }

    protected void setEnableOverscroll(boolean enable) {
        this.mAllowOverScroll = enable;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int i;
        if (getChildCount() <= 0) {
            return false;
        }
        acquireVelocityTrackerAndAddMovement(ev);
        int action = ev.getAction() & 255;
        if (action == 0) {
            updateIsBeingDraggedOnTouchDown();
            if (!this.mScroller.isFinished()) {
                abortScrollerAnimation(false);
            }
            this.mDownMotionX = ev.getX();
            this.mDownMotionY = ev.getY();
            float primaryDirection = this.mOrientationHandler.getPrimaryDirection(ev, 0);
            this.mLastMotion = primaryDirection;
            this.mDownMotionPrimary = primaryDirection;
            this.mLastMotionRemainder = 0.0f;
            this.mTotalMotion = 0.0f;
            this.mAllowEasyFling = false;
            this.mActivePointerId = ev.getPointerId(0);
            if (this.mIsBeingDragged) {
                onScrollInteractionBegin();
                pageBeginTransition();
            }
        } else if (action == 1) {
            if (this.mIsBeingDragged) {
                int iFindPointerIndex = ev.findPointerIndex(this.mActivePointerId);
                if (iFindPointerIndex == -1) {
                    return true;
                }
                float primaryDirection2 = this.mOrientationHandler.getPrimaryDirection(ev, iFindPointerIndex);
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
                int primaryVelocity = (int) this.mOrientationHandler.getPrimaryVelocity(velocityTracker, this.mActivePointerId);
                int i2 = (int) (primaryDirection2 - this.mDownMotionPrimary);
                float measuredSize = this.mOrientationHandler.getMeasuredSize(getPageAt(this.mCurrentPage));
                boolean z = ((float) Math.abs(i2)) > 0.4f * measuredSize;
                float fAbs = this.mTotalMotion + Math.abs((this.mLastMotion + this.mLastMotionRemainder) - primaryDirection2);
                this.mTotalMotion = fAbs;
                boolean z2 = (this.mAllowEasyFling || (fAbs > ((float) this.mPageSlop) ? 1 : (fAbs == ((float) this.mPageSlop) ? 0 : -1)) > 0) && shouldFlingForVelocity(primaryVelocity);
                boolean z3 = this.mIsRtl;
                boolean z4 = !z3 ? i2 >= 0 : i2 <= 0;
                boolean z5 = !z3 ? primaryVelocity >= 0 : primaryVelocity <= 0;
                if (this.mFreeScroll) {
                    if (!this.mScroller.isFinished()) {
                        abortScrollerAnimation(true);
                    }
                    int primaryScroll = this.mOrientationHandler.getPrimaryScroll(this);
                    int scrollForPage = this.mMaxScroll;
                    int i3 = this.mMinScroll;
                    if ((primaryScroll >= scrollForPage && (z5 || !z2)) || (primaryScroll <= i3 && (!z5 || !z2))) {
                        this.mScroller.springBack(primaryScroll, i3, scrollForPage);
                        this.mNextPage = getPageNearestToCenterOfScreen();
                    } else {
                        this.mScroller.setInterpolator(this.mDefaultInterpolator);
                        this.mScroller.fling(primaryScroll, -primaryVelocity, i3, scrollForPage, Math.round(getWidth() * 0.5f * 0.07f));
                        int finalPos = this.mScroller.getFinalPos();
                        this.mNextPage = getPageNearestToCenterOfScreen(finalPos);
                        int scrollForPage2 = getScrollForPage(!this.mIsRtl ? 0 : getPageCount() - 1);
                        int scrollForPage3 = getScrollForPage(this.mIsRtl ? 0 : getPageCount() - 1);
                        if (finalPos > i3 && finalPos < scrollForPage) {
                            if (finalPos < (scrollForPage2 + i3) / 2) {
                                scrollForPage = i3;
                            } else if (finalPos <= (scrollForPage3 + scrollForPage) / 2) {
                                scrollForPage = getScrollForPage(this.mNextPage);
                            }
                            this.mScroller.setFinalPos(scrollForPage);
                            int duration = 270 - this.mScroller.getDuration();
                            if (duration > 0) {
                                this.mScroller.extendDuration(duration);
                            }
                        }
                    }
                    invalidate();
                } else {
                    if (Math.abs(i2) > measuredSize * RETURN_TO_ORIGINAL_PAGE_THRESHOLD && Math.signum(primaryVelocity) != Math.signum(i2) && z2) {
                        i = 1;
                    }
                    if (((z && !z4 && !z2) || (z2 && !z5)) && (i = this.mCurrentPage) > 0) {
                        if (i == 0) {
                            i--;
                        }
                        snapToPageWithVelocity(i, primaryVelocity);
                    } else if (((z && z4 && !z2) || (z2 && z5)) && this.mCurrentPage < getChildCount() - 1) {
                        int i4 = this.mCurrentPage;
                        if (i == 0) {
                            i4++;
                        }
                        snapToPageWithVelocity(i4, primaryVelocity);
                    } else {
                        snapToDestination();
                    }
                }
                onScrollInteractionEnd();
            }
            resetTouchState();
        } else if (action != 2) {
            if (action == 3) {
                if (this.mIsBeingDragged) {
                    snapToDestination();
                    onScrollInteractionEnd();
                }
                resetTouchState();
            } else if (action == 6) {
                onSecondaryPointerUp(ev);
                releaseVelocityTracker();
            } else if (action == 254) {
                determineScrollingStart(ev);
                this.mAllowEasyFling = true;
            }
        } else if (this.mIsBeingDragged) {
            int iFindPointerIndex2 = ev.findPointerIndex(this.mActivePointerId);
            if (iFindPointerIndex2 == -1) {
                return true;
            }
            float primaryDirection3 = this.mOrientationHandler.getPrimaryDirection(ev, iFindPointerIndex2);
            float f = (this.mLastMotion + this.mLastMotionRemainder) - primaryDirection3;
            this.mTotalMotion += Math.abs(f);
            if (Math.abs(f) >= 1.0f) {
                this.mLastMotion = primaryDirection3;
                int i5 = (int) f;
                this.mLastMotionRemainder = f - i5;
                this.mOrientationHandler.set(this, (PagedOrientationHandler.Int2DAction<PagedView<T>>) PagedOrientationHandler.VIEW_SCROLL_BY, i5);
            } else {
                awakenScrollBars();
            }
        } else {
            determineScrollingStart(ev);
        }
        return true;
    }

    protected boolean shouldFlingForVelocity(int velocity) {
        return ((float) Math.abs(velocity)) > ((float) (this.mAllowEasyFling ? this.mEasyFlingThresholdVelocity : this.mFlingThresholdVelocity));
    }

    private void resetTouchState() {
        releaseVelocityTracker();
        this.mIsBeingDragged = false;
        this.mActivePointerId = -1;
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        float f;
        float axisValue;
        if ((event.getSource() & 2) != 0 && event.getAction() == 8) {
            if ((event.getMetaState() & 1) != 0) {
                axisValue = event.getAxisValue(9);
                f = 0.0f;
            } else {
                f = -event.getAxisValue(9);
                axisValue = event.getAxisValue(10);
            }
            boolean z = false;
            if (!canScroll(Math.abs(f), Math.abs(axisValue))) {
                return false;
            }
            if (axisValue != 0.0f || f != 0.0f) {
                if (!this.mIsRtl ? axisValue > 0.0f || f > 0.0f : axisValue < 0.0f || f < 0.0f) {
                    z = true;
                }
                if (z) {
                    scrollRight();
                } else {
                    scrollLeft();
                }
                return true;
            }
        }
        return super.onGenericMotionEvent(event);
    }

    protected boolean canScroll(float absVScroll, float absHScroll) {
        ActivityContext activityContextLookupContext = ActivityContext.lookupContext(getContext());
        return activityContextLookupContext == null || AbstractFloatingView.getTopOpenView(activityContextLookupContext) == null;
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
    }

    private void releaseVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int actionIndex = ev.getActionIndex();
        if (ev.getPointerId(actionIndex) == this.mActivePointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            float primaryDirection = this.mOrientationHandler.getPrimaryDirection(ev, i);
            this.mDownMotionPrimary = primaryDirection;
            this.mLastMotion = primaryDirection;
            this.mLastMotionRemainder = 0.0f;
            this.mActivePointerId = ev.getPointerId(i);
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        int iIndexToPage = indexToPage(indexOfChild(child));
        if (iIndexToPage < 0 || iIndexToPage == getCurrentPage() || isInTouchMode()) {
            return;
        }
        snapToPage(iIndexToPage);
    }

    public int getPageNearestToCenterOfScreen() {
        return getPageNearestToCenterOfScreen(this.mOrientationHandler.getPrimaryScroll(this));
    }

    private int getPageNearestToCenterOfScreen(int scaledScroll) {
        int measuredSize = scaledScroll + (this.mOrientationHandler.getMeasuredSize(this) / 2);
        int childCount = getChildCount();
        int i = Integer.MAX_VALUE;
        int i2 = -1;
        for (int i3 = 0; i3 < childCount; i3++) {
            int iAbs = Math.abs((getChildOffset(i3) + (this.mOrientationHandler.getMeasuredSize(getPageAt(i3)) / 2)) - measuredSize);
            if (iAbs < i) {
                i2 = i3;
                i = iAbs;
            }
        }
        return i2;
    }

    protected void snapToDestination() {
        snapToPage(getPageNearestToCenterOfScreen(), getPageSnapDuration());
    }

    protected boolean isInOverScroll() {
        int primaryScroll = this.mOrientationHandler.getPrimaryScroll(this);
        return primaryScroll > this.mMaxScroll || primaryScroll < this.mMinScroll;
    }

    protected int getPageSnapDuration() {
        if (isInOverScroll()) {
            return OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION;
        }
        return 750;
    }

    private float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((float) (((double) (f - 0.5f)) * 0.4712389167638204d));
    }

    protected boolean snapToPageWithVelocity(int whichPage, int velocity) {
        int iValidateNewPage = validateNewPage(whichPage);
        int measuredSize = this.mOrientationHandler.getMeasuredSize(this) / 2;
        int scrollForPage = getScrollForPage(iValidateNewPage) - getUnboundedScroll();
        if (Math.abs(velocity) < this.mMinFlingVelocity) {
            return snapToPage(iValidateNewPage, 750);
        }
        float fMin = Math.min(1.0f, (Math.abs(scrollForPage) * 1.0f) / (measuredSize * 2));
        float f = measuredSize;
        float fDistanceInfluenceForSnapDuration = f + (distanceInfluenceForSnapDuration(fMin) * f);
        float fMax = Math.max(this.mMinSnapVelocity, Math.abs(velocity));
        int iRound = Math.round(Math.abs(fDistanceInfluenceForSnapDuration / fMax) * 1000.0f) * 4;
        if (FeatureFlags.QUICKSTEP_SPRINGS.get() && this.mCurrentPage != iValidateNewPage) {
            return snapToPage(iValidateNewPage, scrollForPage, iRound, false, null, fMax * Math.signum(scrollForPage), true);
        }
        return snapToPage(iValidateNewPage, scrollForPage, iRound);
    }

    public boolean snapToPage(int whichPage) {
        return snapToPage(whichPage, 750);
    }

    public boolean snapToPageImmediately(int whichPage) {
        return snapToPage(whichPage, 750, true, null);
    }

    public boolean snapToPage(int whichPage, int duration) {
        return snapToPage(whichPage, duration, false, null);
    }

    public boolean snapToPage(int whichPage, int duration, TimeInterpolator interpolator) {
        return snapToPage(whichPage, duration, false, interpolator);
    }

    protected boolean snapToPage(int whichPage, int duration, boolean immediate, TimeInterpolator interpolator) {
        int iValidateNewPage = validateNewPage(whichPage);
        return snapToPage(iValidateNewPage, getScrollForPage(iValidateNewPage) - getUnboundedScroll(), duration, immediate, interpolator, 0.0f, false);
    }

    protected boolean snapToPage(int whichPage, int delta, int duration) {
        return snapToPage(whichPage, delta, duration, false, null, 0.0f, false);
    }

    protected boolean snapToPage(int whichPage, int delta, int duration, boolean immediate, TimeInterpolator interpolator, float velocity, boolean spring) {
        if (this.mFirstLayout) {
            setCurrentPage(whichPage);
            return false;
        }
        this.mNextPage = validateNewPage(whichPage);
        awakenScrollBars(duration);
        if (immediate) {
            duration = 0;
        } else if (duration == 0) {
            duration = Math.abs(delta);
        }
        if (duration != 0) {
            pageBeginTransition();
        }
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(false);
        }
        if (interpolator != null) {
            this.mScroller.setInterpolator(interpolator);
        } else {
            this.mScroller.setInterpolator(this.mDefaultInterpolator);
        }
        if (spring && FeatureFlags.QUICKSTEP_SPRINGS.get()) {
            this.mScroller.startScrollSpring(getUnboundedScroll(), delta, duration, velocity);
        } else {
            this.mScroller.startScroll(getUnboundedScroll(), delta, duration);
        }
        updatePageIndicator();
        if (immediate) {
            computeScroll();
            pageEndTransition();
        }
        invalidate();
        return Math.abs(delta) > 0;
    }

    public boolean scrollLeft() {
        if (getNextPage() > 0) {
            snapToPage(getNextPage() - 1);
            return true;
        }
        return onOverscroll(-getMeasuredWidth());
    }

    public boolean scrollRight() {
        if (getNextPage() < getChildCount() - 1) {
            snapToPage(getNextPage() + 1);
            return true;
        }
        return onOverscroll(getMeasuredWidth());
    }

    protected boolean onOverscroll(int amount) {
        if (!this.mAllowOverScroll) {
            return false;
        }
        onScrollInteractionBegin();
        overScroll(amount);
        onScrollInteractionEnd();
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ScrollView.class.getName();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction;
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction2;
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction3;
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction4;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        boolean zIsPageOrderFlipped = isPageOrderFlipped();
        int i = !this.mAllowOverScroll ? 1 : 0;
        accessibilityNodeInfo.setScrollable(getPageCount() > i);
        if (getCurrentPage() < getPageCount() - i) {
            if (zIsPageOrderFlipped) {
                accessibilityAction3 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD;
            } else {
                accessibilityAction3 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD;
            }
            accessibilityNodeInfo.addAction(accessibilityAction3);
            if (this.mIsRtl) {
                accessibilityAction4 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_LEFT;
            } else {
                accessibilityAction4 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_RIGHT;
            }
            accessibilityNodeInfo.addAction(accessibilityAction4);
        }
        if (getCurrentPage() >= i) {
            if (zIsPageOrderFlipped) {
                accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD;
            } else {
                accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD;
            }
            accessibilityNodeInfo.addAction(accessibilityAction);
            if (this.mIsRtl) {
                accessibilityAction2 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_RIGHT;
            } else {
                accessibilityAction2 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_LEFT;
            }
            accessibilityNodeInfo.addAction(accessibilityAction2);
        }
        accessibilityNodeInfo.setLongClickable(false);
        accessibilityNodeInfo.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEvent(int eventType) {
        if (eventType != 4096) {
            super.sendAccessibilityEvent(eventType);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        boolean z = true;
        if (!this.mAllowOverScroll && getPageCount() <= 1) {
            z = false;
        }
        event.setScrollable(z);
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }
        boolean zIsPageOrderFlipped = isPageOrderFlipped();
        if (action == 4096) {
            if (zIsPageOrderFlipped) {
                if (!scrollLeft()) {
                    return false;
                }
            } else if (!scrollRight()) {
                return false;
            }
            return true;
        }
        if (action == 8192) {
            if (zIsPageOrderFlipped) {
                if (!scrollRight()) {
                    return false;
                }
            } else if (!scrollLeft()) {
                return false;
            }
            return true;
        }
        switch (action) {
            case android.R.id.accessibilityActionPageLeft:
                if (!this.mIsRtl) {
                    return scrollLeft();
                }
                return scrollRight();
            case android.R.id.accessibilityActionPageRight:
                if (!this.mIsRtl) {
                    return scrollRight();
                }
                return scrollLeft();
            default:
                return false;
        }
    }

    protected String getCurrentPageDescription() {
        return getContext().getString(R.string.default_scroll_format, Integer.valueOf(getNextPage() + 1), Integer.valueOf(getChildCount()));
    }

    protected float getDownMotionX() {
        return this.mDownMotionX;
    }

    protected float getDownMotionY() {
        return this.mDownMotionY;
    }

    public int[] getVisibleChildrenRange() {
        float f = 0.0f;
        float measuredWidth = getMeasuredWidth() + 0.0f;
        float scaleX = getScaleX();
        if (scaleX < 1.0f && scaleX > 0.0f) {
            float measuredWidth2 = getMeasuredWidth() / 2;
            f = measuredWidth2 - ((measuredWidth2 - 0.0f) / scaleX);
            measuredWidth = ((measuredWidth - measuredWidth2) / scaleX) + measuredWidth2;
        }
        int childCount = getChildCount();
        int i = -1;
        int i2 = -1;
        for (int i3 = 0; i3 < childCount; i3++) {
            float left = (r8.getLeft() + getPageAt(i3).getTranslationX()) - getScrollX();
            if (left <= measuredWidth && left + r8.getMeasuredWidth() >= f) {
                if (i == -1) {
                    i = i3;
                }
                i2 = i3;
            }
        }
        int[] iArr = this.mTmpIntPair;
        iArr[0] = i;
        iArr[1] = i2;
        return iArr;
    }
}
