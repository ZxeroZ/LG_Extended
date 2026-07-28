package com.lge.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherScroller;
import com.android.launcher3.LauncherState;
import com.android.launcher3.PageIndicator;
import com.android.launcher3.SearchDropTargetBar;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.touch.OverScroll;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.LauncherEdgeEffect;
import com.android.launcher3.util.OverScroller;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.operator.GoogleNowManager;
import com.lge.launcher3.operator.VZWSideScreenManager;
import com.lge.launcher3.pageindicator.PageIndicatorExtension;
import com.lge.launcher3.pageindicator.PageIndicatorListener;
import com.lge.launcher3.screeneffect.LoopNormalModeManager;
import com.lge.launcher3.screeneffect.ScreenEffectTargetManager;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.CPUBoostService;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.TouchPrediction;
import com.lge.launcher3.wallpaperblur.HomescreenBlurManager;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class PagedView extends ViewGroup implements ViewGroup.OnHierarchyChangeListener {
    public static final int ACTION_MOVE_ALLOW_EASY_FLING = 254;
    protected static final float ALPHA_QUANTIZE_LEVEL = 1.0E-4f;
    private static final int ANIM_TAG_KEY = 100;
    static final int AUTOMATIC_PAGE_SPACING = -1;
    public static final boolean DEBUG = false;
    public static final boolean DEBUG_FAILED_QUICKSWITCH = false;
    private static final int EXTEND_PAGE_SNAP_ANIMATION_DURATION = 500;
    private static final int FLING_THRESHOLD_VELOCITY = 500;
    public static final int INVALID_PAGE = -1;
    protected static final int INVALID_POINTER = -1;
    public static final int INVALID_RESTORE_PAGE = -1001;
    private static final float MAX_ANGLE_FOR_INAPPS = 0.43633235f;
    private static final float MAX_ANGLE_FOR_SWIPEUP_APPDRAWER = 0.8726647f;
    public static final float MAX_SCROLL_PROGRESS = 1.0f;
    private static final int MIN_FLING_VELOCITY = 250;
    private static final int MIN_LENGTH_FOR_FLING = 25;
    private static final int MIN_MOVE_FOR_INAPPS = 63;
    public static final int MIN_MOVE_FOR_SWIPEUP_APPDRAWER = 63;
    protected static final int MIN_SNAP_VELOCITY = 1500;
    protected static final float NANOTIME_DIV = 1.0E9f;
    private static final int OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION = 270;
    public static final int PAGE_SNAP_ANIMATION_DURATION = 600;
    public static final float RETURN_TO_ORIGINAL_PAGE_THRESHOLD = 0.1f;
    private static final float SIGNIFICANT_MOVE_THRESHOLD = 0.4f;
    protected static final int SLOW_PAGE_SNAP_ANIMATION_DURATION = 600;
    private static final String TAG = "PagedView";
    private static final float TOUCH_SLOP_MM_PER_INCH = 25.4f;
    public static final int TOUCH_STATE_NEXT_PAGE = 3;
    public static final int TOUCH_STATE_PREV_PAGE = 2;
    public static final int TOUCH_STATE_REORDERING = 4;
    public static final int TOUCH_STATE_REST = 0;
    public static final int TOUCH_STATE_SCROLLING = 1;
    public static final int TOUCH_STATE_SWIPE_DOWN = 5;
    public static final int TOUCH_STATE_SWIPE_UP = 6;
    public static float sTouchSlopRatio;
    private int NUM_ANIMATIONS_RUNNING_BEFORE_ZOOM_OUT;
    protected int mActivePointerId;
    protected boolean mAllowOverScroll;
    private View mBlurBackgroundView;
    private boolean mCancelTap;
    protected int mCellCountX;
    protected int mCellCountY;
    protected boolean mCenterPagesVertically;
    private boolean mCheckAppDrawerAnimationFinish;
    private boolean mCheckExitAnimationFinish;
    private boolean mCheckInapps;
    private boolean mCheckSwipeDownAppDrawer;
    private boolean mCheckSwipeUpAppDrawer;
    protected boolean mChildAddedOrRemoved;
    protected int mChildCountOnLastLayout;
    public int mCurrentPage;
    protected Interpolator mDefaultInterpolator;
    protected float mDensity;
    private float mDownMotionX;
    private float mDownMotionY;
    private float mDownScrollX;
    protected View mDragView;
    private float mDragViewBaselineLeft;
    protected int[] mDrawVisiblePagesRange;
    private final LauncherEdgeEffect mEdgeGlowLeft;
    private final LauncherEdgeEffect mEdgeGlowRight;
    protected boolean mFadeInAdjacentScreens;
    protected boolean mFirstLayout;
    protected int mFlingThresholdVelocity;
    protected boolean mForceDrawAllChildrenNextFrame;
    protected boolean mForceScreenScrolled;
    protected boolean mFreeScroll;
    protected int mFreeScrollMaxScrollX;
    protected int mFreeScrollMinScrollX;
    private int mGestureAngle;
    private float mInAppsDeltaY;
    private float mInAppsalpha;
    public final Rect mInsets;
    public boolean mIsBeingDragged;
    private boolean mIsInAppsEnabled;
    protected boolean mIsKeyDown;
    protected boolean mIsPageInTransition;
    private boolean mIsReordering;
    protected boolean mIsRtl;
    protected float mLastMotionX;
    protected float mLastMotionXRemainder;
    protected float mLastMotionY;
    protected int mLastScreenCenter;
    protected View.OnLongClickListener mLongClickListener;
    protected int mMaxScroll;
    private int mMaximumVelocity;
    protected int mMinFlingVelocity;
    private int mMinMoveForInApps;
    private int mMinMoveForSwipeUpAppDrawer;
    private float mMinScale;
    protected int mMinScroll;
    protected int mMinSnapVelocity;
    protected int mModifiedTouchSlop;
    protected OverScroller mNativeScroller;
    private boolean mNeedResetTranslation;
    public int mNextPage;
    private int mNormalChildHeight;
    private Runnable mOnPageTransitionEndCallback;
    protected PagedOrientationHandler mOrientationHandler;
    protected int mOverScrollX;
    public PageIndicator mOverviewPageIndicator;
    public PageIndicator mPageIndicator;
    int mPageIndicatorViewId;
    protected int mPageLayoutHeightGap;
    protected int mPageLayoutWidthGap;
    protected int[] mPageScrolls;
    protected int mPageSpacing;
    private PageSwitchListener mPageSwitchListener;
    protected float mParentDownMotionX;
    protected float mParentDownMotionY;
    private int mPostReorderingPreZoomInRemainingAnimationCount;
    private Runnable mPostReorderingPreZoomInRunnable;
    private boolean mReorderingStarted;
    protected int mRestorePage;
    protected LauncherScroller mScroller;
    private boolean mSettleOnPageInFreeScroll;
    int mSidePageHoverIndex;
    private Runnable mSidePageHoverRunnable;
    protected float mSmoothingTime;
    protected float mSpringOverScrollX;
    private float mSwipeUpAppDrawerAlpha;
    private float mSwipeUpAppDrawerDeltaY;
    protected int[] mTempVisiblePagesRange;
    private int[] mTmpIntPair;
    protected float mTotalMotionX;
    private TouchPrediction mTouchPrediction;
    protected int mTouchSlop;
    public int mTouchState;
    protected float mTouchX;
    private boolean mUseMinScale;
    private VelocityTracker mVelocityTracker;
    protected Rect mViewport;
    protected boolean mWasInOverscroll;
    protected static final ComputePageScrollsLogic SIMPLE_SCROLL_LOGIC = new ComputePageScrollsLogic() { // from class: com.lge.launcher3.-$$Lambda$PagedView$HwDRcCZu5IzR3n3WLc6X-wCXBeQ
        @Override // com.lge.launcher3.PagedView.ComputePageScrollsLogic
        public final boolean shouldIncludeView(View view) {
            return PagedView.lambda$static$0(view);
        }
    };
    private static int REORDERING_DROP_REPOSITION_DURATION = 200;
    static int REORDERING_REORDER_REPOSITION_DURATION = 300;
    private static int REORDERING_SIDE_PAGE_HOVER_TIMEOUT = 80;
    private static final Matrix sTmpInvMatrix = new Matrix();
    private static final float[] sTmpPoint = new float[2];
    protected static final int[] sTmpIntPoint = new int[2];
    private static final Rect sTmpRect = new Rect();
    private static boolean mIsOutOfTouchSlop = false;
    private static float TOUCH_SLOP_INIT_REACTION_DISTANCE = 0.5f;

    protected interface ComputePageScrollsLogic {
        boolean shouldIncludeView(View view);
    }

    public interface PageSwitchListener {
        void onPageSwitch(View newPage, int newPageIndex);
    }

    private static class ScrollInterpolator implements Interpolator {
        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float t) {
            float f = t - 1.0f;
            return (f * f * f * f * f) + 1.0f;
        }
    }

    protected int computeMinScrollX() {
        return 0;
    }

    public int getChildGap() {
        return 0;
    }

    protected void getEdgeVerticalPostion(int[] pos) {
    }

    protected View.OnClickListener getPageIndicatorClickListener() {
        return null;
    }

    public void goToMinusOneScreen(boolean animate) {
    }

    public boolean hasMinusOneScreenPreview() {
        return false;
    }

    protected int indexToPage(int index) {
        return index;
    }

    protected boolean isPageOrderFlipped() {
        return false;
    }

    protected int offsetForPageScrolls() {
        return 0;
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent event) {
        return true;
    }

    protected void onPageBeginTransition() {
    }

    protected void onScrollInteractionBegin() {
    }

    protected void onScrollInteractionEnd(int velocityX) {
    }

    protected void screenScrolled(int screenCenter) {
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: ?: TERNARY null = ((wrap:int:0x0000: INVOKE (r1v0 android.view.View) VIRTUAL call: android.view.View.getVisibility():int A[MD:():int (c), WRAPPED] (LINE:121)) != (8 int)) ? true : false */
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
        this.mSettleOnPageInFreeScroll = false;
        this.mFreeScrollMinScrollX = -1;
        this.mFreeScrollMaxScrollX = -1;
        this.mFirstLayout = true;
        this.mChildAddedOrRemoved = true;
        this.mRestorePage = INVALID_RESTORE_PAGE;
        this.mNextPage = -1;
        this.mPageSpacing = 0;
        this.mLastScreenCenter = -1;
        this.mOrientationHandler = PagedOrientationHandler.PORTRAIT;
        this.mTouchState = 0;
        this.mForceScreenScrolled = false;
        this.mCellCountX = 0;
        this.mCellCountY = 0;
        this.mAllowOverScroll = true;
        this.mTempVisiblePagesRange = new int[2];
        this.mDrawVisiblePagesRange = new int[2];
        this.mActivePointerId = -1;
        this.mFadeInAdjacentScreens = false;
        this.mIsPageInTransition = false;
        this.mWasInOverscroll = false;
        this.mViewport = new Rect();
        this.mMinScale = 1.0f;
        this.mUseMinScale = false;
        this.mSidePageHoverIndex = -1;
        this.mReorderingStarted = false;
        this.NUM_ANIMATIONS_RUNNING_BEFORE_ZOOM_OUT = 2;
        this.mInsets = new Rect();
        this.mIsKeyDown = false;
        this.mGestureAngle = 45;
        this.mNeedResetTranslation = false;
        this.mEdgeGlowLeft = new LauncherEdgeEffect();
        this.mEdgeGlowRight = new LauncherEdgeEffect();
        this.mTouchPrediction = null;
        this.mIsInAppsEnabled = false;
        this.mBlurBackgroundView = null;
        this.mCheckInapps = false;
        this.mCheckSwipeUpAppDrawer = false;
        this.mCheckSwipeDownAppDrawer = false;
        this.mCheckAppDrawerAnimationFinish = false;
        this.mCheckExitAnimationFinish = false;
        this.mTmpIntPair = new int[2];
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.PagedView, defStyle, 0);
        this.mPageLayoutWidthGap = typedArrayObtainStyledAttributes.getDimensionPixelSize(8, 0);
        this.mPageLayoutHeightGap = typedArrayObtainStyledAttributes.getDimensionPixelSize(3, 0);
        this.mPageIndicatorViewId = typedArrayObtainStyledAttributes.getResourceId(2, -1);
        typedArrayObtainStyledAttributes.recycle();
        setHapticFeedbackEnabled(false);
        this.mIsRtl = Utilities.isRtl(getResources());
        init();
    }

    protected void init() {
        this.mTouchPrediction = new TouchPrediction();
        this.mGestureAngle = getResources().getInteger(R.integer.config_gesture_angle_max);
        this.mScroller = new LauncherScroller(getContext());
        setDefaultInterpolator(new ScrollInterpolator());
        this.mCurrentPage = 0;
        this.mCenterPagesVertically = true;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        float f = getResources().getDisplayMetrics().density;
        this.mDensity = f;
        this.mFlingThresholdVelocity = (int) (500.0f * f);
        this.mMinFlingVelocity = (int) (250.0f * f);
        this.mMinSnapVelocity = (int) (f * 1500.0f);
        setOnHierarchyChangeListener(this);
        setWillNotDraw(false);
        setupTouchSlopRatio();
        this.mMinMoveForInApps = Utilities.pxFromDp(63.0f, getResources().getDisplayMetrics());
        this.mMinMoveForSwipeUpAppDrawer = Utilities.pxFromDp(getResources().getInteger(R.integer.config_min_move_four_swipeup), getResources().getDisplayMetrics());
    }

    protected void setEdgeGlowColor(int color) {
        this.mEdgeGlowLeft.setColor(color);
        this.mEdgeGlowRight.setColor(color);
    }

    public void setDefaultInterpolator(Interpolator interpolator) {
        this.mDefaultInterpolator = interpolator;
        this.mScroller.setInterpolator(interpolator);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        int i;
        super.onAttachedToWindow();
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) getParent()).getParent();
        if (this.mPageIndicator == null && (i = this.mPageIndicatorViewId) > -1) {
            PageIndicator pageIndicator = (PageIndicator) viewGroup.findViewById(i);
            this.mPageIndicator = pageIndicator;
            pageIndicator.removeAllMarkers(true);
            ArrayList<PageIndicator.PageMarkerResources> arrayList = new ArrayList<>();
            for (int i2 = 0; i2 < getChildCount(); i2++) {
                arrayList.add(getPageIndicatorMarker(i2));
            }
            this.mPageIndicator.addMarkers(arrayList, true);
            View.OnClickListener pageIndicatorClickListener = getPageIndicatorClickListener();
            if (pageIndicatorClickListener != null) {
                this.mPageIndicator.setOnClickListener(pageIndicatorClickListener);
            }
            updatePageIndicator();
            this.mPageIndicator.setContentDescription(getPageIndicatorDescription());
        }
        afterAttachedToWindow();
    }

    protected String getPageIndicatorDescription() {
        return getCurrentPageDescription();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mPageIndicator = null;
    }

    private float[] mapPointFromViewToParent(View v, float x, float y) {
        float[] fArr = sTmpPoint;
        fArr[0] = x;
        fArr[1] = y;
        v.getMatrix().mapPoints(fArr);
        fArr[0] = fArr[0] + v.getLeft();
        fArr[1] = fArr[1] + v.getTop();
        return fArr;
    }

    protected float[] mapPointFromParentToView(View v, float x, float y) {
        float[] fArr = sTmpPoint;
        fArr[0] = x - v.getLeft();
        fArr[1] = y - v.getTop();
        Matrix matrix = v.getMatrix();
        Matrix matrix2 = sTmpInvMatrix;
        matrix.invert(matrix2);
        matrix2.mapPoints(fArr);
        return fArr;
    }

    protected void updateDragViewTranslationDuringDrag() {
        if (this.mDragView != null) {
            float scrollX = (this.mLastMotionX - this.mDownMotionX) + (getScrollX() - this.mDownScrollX) + (this.mDragViewBaselineLeft - this.mDragView.getLeft());
            float f = this.mLastMotionY - this.mDownMotionY;
            this.mDragView.setTranslationX(scrollX);
            this.mDragView.setTranslationY(f);
        }
    }

    public void setMinScale(float f) {
        this.mMinScale = f;
        this.mUseMinScale = true;
        requestLayout();
    }

    @Override // android.view.View
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
        if (isReordering(true)) {
            float[] fArrMapPointFromParentToView = mapPointFromParentToView(this, this.mParentDownMotionX, this.mParentDownMotionY);
            this.mLastMotionX = fArrMapPointFromParentToView[0];
            this.mLastMotionY = fArrMapPointFromParentToView[1];
            updateDragViewTranslationDuringDrag();
        }
    }

    public int getViewportWidth() {
        return this.mViewport.width();
    }

    public int getViewportHeight() {
        return this.mViewport.height();
    }

    boolean isWorkspace() {
        return this instanceof Workspace;
    }

    public int getViewportOffsetX() {
        return (getMeasuredWidth() - getViewportWidth()) / 2;
    }

    protected int getViewportOffsetY() {
        return (getMeasuredHeight() - getViewportHeight()) / 2;
    }

    public PageIndicator getPageIndicator() {
        return this.mPageIndicator;
    }

    protected PageIndicator.PageMarkerResources getPageIndicatorMarker(int pageIndex) {
        return new PageIndicator.PageMarkerResources();
    }

    public void setPageSwitchListener(PageSwitchListener pageSwitchListener) {
        this.mPageSwitchListener = pageSwitchListener;
        if (pageSwitchListener != null) {
            pageSwitchListener.onPageSwitch(getPageAt(this.mCurrentPage), this.mCurrentPage);
        }
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

    protected void scrollAndForceFinish(int scrollX) {
        if (isWorkspace()) {
            LGLog.d(TAG, "scrollAndForceFinish - " + scrollX);
        }
        scrollTo(scrollX, 0);
        this.mScroller.setFinalX(scrollX);
        forceFinishScroller(true);
    }

    protected void updateCurrentPageScroll() {
        int i = this.mCurrentPage;
        int scrollForPage = (i < 0 || i >= getPageCount()) ? 0 : getScrollForPage(this.mCurrentPage);
        if (this instanceof Workspace) {
            LGLog.d(TAG, "updateCurrentPageScroll : " + scrollForPage);
        }
        scrollAndForceFinish(scrollForPage);
    }

    protected void abortScrollerAnimation(boolean resetNextPage) {
        this.mScroller.abortAnimation();
        if (resetNextPage) {
            this.mNextPage = -1;
        }
    }

    public void forceFinishScroller(boolean resetNextPage) {
        this.mScroller.forceFinished(true);
        if (resetNextPage) {
            this.mNextPage = -1;
            pageEndTransition();
        }
    }

    public int validateNewPage(int newPage) {
        if (this.mFreeScroll) {
            getFreeScrollPageRange(this.mTempVisiblePagesRange);
            int[] iArr = this.mTempVisiblePagesRange;
            newPage = Math.max(iArr[0], Math.min(newPage, iArr[1]));
        }
        return Math.max(0, Math.min(newPage, getPageCount() - 1));
    }

    public void setCurrentPage(int currentPage) {
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(true);
        }
        if (getChildCount() == 0) {
            return;
        }
        this.mForceScreenScrolled = true;
        int i = this.mCurrentPage;
        int iValidateNewPage = validateNewPage(currentPage);
        this.mCurrentPage = iValidateNewPage;
        getPageAt(iValidateNewPage).setContentDescription(getCurrentPageDescription());
        updateCurrentPageScroll();
        notifyPageSwitchListener(i);
        invalidate();
    }

    public void setRestorePage(int restorePage) {
        this.mRestorePage = restorePage;
    }

    public int getRestorePage() {
        return this.mRestorePage;
    }

    protected void notifyPageSwitchListener(int prevPage) {
        PageSwitchListener pageSwitchListener = this.mPageSwitchListener;
        if (pageSwitchListener != null) {
            pageSwitchListener.onPageSwitch(getPageAt(getNextPage()), getNextPage());
        }
        updatePageIndicator();
    }

    protected void updatePageIndicator() {
        PageIndicator pageIndicator = this.mPageIndicator;
        if (pageIndicator != null) {
            pageIndicator.setContentDescription(getPageIndicatorDescription());
            if (isReordering(false)) {
                return;
            }
            this.mPageIndicator.setActiveMarker(getNextPage());
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

    public void setPageMoving(boolean isMoving) {
        this.mIsPageInTransition = isMoving;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isPageInTransition() {
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

    @Override // android.view.View
    public void setOnLongClickListener(View.OnLongClickListener l) {
        this.mLongClickListener = l;
        int pageCount = getPageCount();
        for (int i = 0; i < pageCount; i++) {
            getPageAt(i).setOnLongClickListener(l);
        }
        super.setOnLongClickListener(l);
    }

    public void setOnPageTransitionEndCallback(Runnable callback) {
        if (this.mIsPageInTransition || callback == null) {
            this.mOnPageTransitionEndCallback = callback;
        } else {
            callback.run();
        }
    }

    protected int getUnboundedScrollX() {
        return getScrollX();
    }

    @Override // android.view.View
    public void scrollBy(int x, int y) {
        scrollTo(getUnboundedScrollX() + x, getScrollY() + y);
    }

    @Override // android.view.View
    public void scrollTo(int x, int y) {
        if (this.mFreeScroll) {
            if (!this.mScroller.isFinished() && (x > this.mFreeScrollMaxScrollX || x < this.mFreeScrollMinScrollX)) {
                forceFinishScroller(false);
            }
            x = Math.max(Math.min(x, this.mFreeScrollMaxScrollX), this.mFreeScrollMinScrollX);
        }
        boolean z = this.mIsRtl;
        boolean z2 = !z ? x >= this.mMinScroll : x <= this.mMaxScroll;
        boolean z3 = !z ? x <= this.mMaxScroll : x >= this.mMinScroll;
        if (!z2 && !z3) {
            this.mSpringOverScrollX = 0.0f;
        }
        if (z2) {
            super.scrollTo(z ? this.mMaxScroll : this.mMinScroll, y);
            if (this.mAllowOverScroll) {
                this.mWasInOverscroll = true;
                if (this.mIsRtl) {
                    overScroll(x - this.mMaxScroll);
                } else {
                    overScroll(x - this.mMinScroll);
                }
            }
        } else if (z3) {
            super.scrollTo(z ? this.mMinScroll : this.mMaxScroll, y);
            if (this.mAllowOverScroll) {
                this.mWasInOverscroll = true;
                if (this.mIsRtl) {
                    overScroll(x - this.mMinScroll);
                } else {
                    overScroll(x - this.mMaxScroll);
                }
            }
        } else {
            if (this.mWasInOverscroll) {
                overScroll(0.0f);
                this.mWasInOverscroll = false;
            }
            this.mOverScrollX = x;
            super.scrollTo(x, y);
        }
        this.mTouchX = x;
        this.mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
        if (isReordering(true)) {
            float[] fArrMapPointFromParentToView = mapPointFromParentToView(this, this.mParentDownMotionX, this.mParentDownMotionY);
            this.mLastMotionX = fArrMapPointFromParentToView[0];
            this.mLastMotionY = fArrMapPointFromParentToView[1];
            updateDragViewTranslationDuringDrag();
        }
    }

    public void sendScrollAccessibilityEvent() {
        if (!((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled() || this.mCurrentPage == getNextPage()) {
            return;
        }
        AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(4096);
        accessibilityEventObtain.setScrollable(true);
        accessibilityEventObtain.setScrollX(getScrollX());
        accessibilityEventObtain.setScrollY(getScrollY());
        accessibilityEventObtain.setMaxScrollX(this.mMaxScroll);
        accessibilityEventObtain.setMaxScrollY(0);
        sendAccessibilityEventUnchecked(accessibilityEventObtain);
    }

    protected boolean computeScrollHelper() {
        if (this.mScroller.computeScrollOffset()) {
            if (getScrollX() != this.mScroller.getCurrX() || getScrollY() != this.mScroller.getCurrY() || this.mOverScrollX != this.mScroller.getCurrX()) {
                scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            }
            invalidate();
            return true;
        }
        if (this.mNextPage == -1) {
            return false;
        }
        sendScrollAccessibilityEvent();
        int i = this.mCurrentPage;
        this.mCurrentPage = validateNewPage(this.mNextPage);
        this.mNextPage = -1;
        LoopNormalModeManager loopNormalModeManager = LoopNormalModeManager.getInstance(getContext());
        if (loopNormalModeManager.isEnabled(this) && (this instanceof Workspace)) {
            loopNormalModeManager.computeScrollHelperForLoop(this, this.mCurrentPage);
        }
        notifyPageSwitchListener(i);
        if (this.mTouchState == 0) {
            pageEndTransition();
        }
        onPostReorderingAnimationCompleted();
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled()) {
            announceForAccessibility(getCurrentPageDescription());
        }
        return true;
    }

    @Override // android.view.View
    public void computeScroll() {
        computeScrollHelper();
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public boolean isFullScreenPage;
        public boolean isOverviewMode;
        public boolean isPreviewPage;
        public boolean isVerticalLayout;

        public LayoutParams(int width, int height) {
            super(width, height);
            this.isFullScreenPage = false;
            this.isPreviewPage = false;
            this.isOverviewMode = false;
            this.isVerticalLayout = false;
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.isFullScreenPage = false;
            this.isPreviewPage = false;
            this.isOverviewMode = false;
            this.isVerticalLayout = false;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.isFullScreenPage = false;
            this.isPreviewPage = false;
            this.isOverviewMode = false;
            this.isVerticalLayout = false;
        }
    }

    /* JADX DEBUG: Method merged with bridge method: generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams; */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX DEBUG: Method merged with bridge method: generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams; */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void addFullScreenPage(View page, boolean isOverviewMode) {
        LayoutParams layoutParamsGenerateDefaultLayoutParams = generateDefaultLayoutParams();
        layoutParamsGenerateDefaultLayoutParams.isFullScreenPage = true;
        layoutParamsGenerateDefaultLayoutParams.isOverviewMode = isOverviewMode;
        super.addView(page, 0, layoutParamsGenerateDefaultLayoutParams);
    }

    public int getNormalChildHeight() {
        return this.mNormalChildHeight;
    }

    public int getExpectedWidth() {
        return getMeasuredWidth();
    }

    public int getNormalChildWidth() {
        return (((getExpectedWidth() - getPaddingLeft()) - getPaddingRight()) - this.mInsets.left) - this.mInsets.right;
    }

    /* JADX WARN: Removed duplicated region for block: B:64:0x0176  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void onMeasure(int r18, int r19) {
        /*
            r17 = this;
            r0 = r17
            android.content.Context r1 = r17.getContext()
            boolean r1 = r1 instanceof com.android.launcher3.Launcher
            if (r1 == 0) goto L11
            android.content.Context r1 = r17.getContext()
            com.android.launcher3.Launcher r1 = (com.android.launcher3.Launcher) r1
            goto L12
        L11:
            r1 = 0
        L12:
            int r3 = android.view.View.MeasureSpec.getMode(r18)
            int r4 = android.view.View.MeasureSpec.getSize(r18)
            int r5 = android.view.View.MeasureSpec.getMode(r19)
            int r6 = android.view.View.MeasureSpec.getSize(r19)
            r7 = 0
            if (r1 == 0) goto L30
            com.android.launcher3.DeviceProfile r8 = r1.getDeviceProfile()
            if (r8 == 0) goto L30
            boolean r9 = r8.isPhone
            boolean r8 = r8.allowRotation
            goto L32
        L30:
            r8 = r7
            r9 = r8
        L32:
            java.lang.String r10 = ", "
            java.lang.String r11 = "PagedView"
            if (r8 != 0) goto L64
            if (r6 >= r4) goto L64
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r12 = "set portait value of onMeasure value if phone && the Rotation value was changed! heightSize = "
            r8.append(r12)
            r8.append(r6)
            java.lang.String r6 = ", widthSize = "
            r8.append(r6)
            r8.append(r4)
            r8.append(r10)
            r8.append(r0)
            java.lang.String r4 = r8.toString()
            android.util.Log.i(r11, r4)
            int r4 = android.view.View.MeasureSpec.getSize(r19)
            int r6 = android.view.View.MeasureSpec.getSize(r18)
        L64:
            int r8 = r17.getChildCount()
            if (r8 != 0) goto L96
            super.onMeasure(r18, r19)
            boolean r1 = r17.isWorkspace()
            if (r1 == 0) goto L95
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "onMeasure  getChildCount() == 0, widthSize = "
            r1.append(r2)
            r1.append(r4)
            java.lang.String r2 = ", heightSize = "
            r1.append(r2)
            r1.append(r6)
            r1.append(r10)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            com.lge.launcher3.util.LGLog.i(r11, r1)
        L95:
            return
        L96:
            android.content.res.Resources r8 = r17.getResources()
            android.util.DisplayMetrics r8 = r8.getDisplayMetrics()
            int r10 = r8.widthPixels
            android.graphics.Rect r11 = r0.mInsets
            int r11 = r11.left
            int r10 = r10 + r11
            android.graphics.Rect r11 = r0.mInsets
            int r11 = r11.right
            int r10 = r10 + r11
            int r8 = r8.heightPixels
            android.graphics.Rect r11 = r0.mInsets
            int r11 = r11.top
            int r8 = r8 + r11
            android.graphics.Rect r11 = r0.mInsets
            int r11 = r11.bottom
            int r8 = r8 + r11
            int r8 = java.lang.Math.max(r10, r8)
            r10 = 1073741824(0x40000000, float:2.0)
            float r8 = (float) r8
            float r8 = r8 * r10
            int r8 = (int) r8
            boolean r10 = r0.mUseMinScale
            if (r10 == 0) goto Lcc
            float r8 = (float) r8
            float r10 = r0.mMinScale
            float r11 = r8 / r10
            int r11 = (int) r11
            float r8 = r8 / r10
            int r8 = (int) r8
            goto Lce
        Lcc:
            r11 = r4
            r8 = r6
        Lce:
            android.graphics.Rect r10 = r0.mViewport
            r10.set(r7, r7, r4, r6)
            if (r3 == 0) goto L1c8
            if (r5 != 0) goto Ld9
            goto L1c8
        Ld9:
            if (r4 <= 0) goto L1c4
            if (r6 > 0) goto Ldf
            goto L1c4
        Ldf:
            int r3 = r17.getPaddingTop()
            int r4 = r17.getPaddingBottom()
            int r3 = r3 + r4
            int r4 = r17.getPaddingLeft()
            int r5 = r17.getPaddingRight()
            int r4 = r4 + r5
            int r5 = r17.getChildCount()
            r6 = r7
        Lf6:
            if (r7 >= r5) goto L1c0
            android.view.View r10 = r0.getPageAt(r7)
            int r12 = r10.getVisibility()
            r13 = 8
            if (r12 == r13) goto L1b8
            android.view.ViewGroup$LayoutParams r12 = r10.getLayoutParams()
            com.lge.launcher3.PagedView$LayoutParams r12 = (com.lge.launcher3.PagedView.LayoutParams) r12
            boolean r13 = r12.isFullScreenPage
            r14 = 1073741824(0x40000000, float:2.0)
            if (r13 != 0) goto L18c
            int r13 = r12.width
            r15 = -2147483648(0xffffffff80000000, float:-0.0)
            r2 = -2
            if (r13 != r2) goto L119
            r13 = r15
            goto L11a
        L119:
            r13 = r14
        L11a:
            int r12 = r12.height
            if (r12 != r2) goto L11f
            r14 = r15
        L11f:
            if (r9 == 0) goto L127
            int r2 = r17.getViewportWidth()
            int r2 = r2 - r4
            goto L136
        L127:
            int r2 = r17.getViewportWidth()
            int r2 = r2 - r4
            android.graphics.Rect r12 = r0.mInsets
            int r12 = r12.left
            int r2 = r2 - r12
            android.graphics.Rect r12 = r0.mInsets
            int r12 = r12.right
            int r2 = r2 - r12
        L136:
            if (r1 == 0) goto L141
            com.android.launcher3.Workspace r12 = r1.getWorkspace()
            com.android.launcher3.Workspace$State r12 = r12.getState()
            goto L142
        L141:
            r12 = 0
        L142:
            boolean r15 = com.lge.launcher3.util.Utilities.isLGUI7_1()
            if (r15 == 0) goto L176
            boolean r15 = r10 instanceof com.android.launcher3.CellLayout
            if (r15 == 0) goto L176
            r15 = r10
            com.android.launcher3.CellLayout r15 = (com.android.launcher3.CellLayout) r15
            boolean r16 = r15.hasFullscreenItem()
            if (r16 != 0) goto L176
            if (r12 == 0) goto L176
            r16 = r1
            com.android.launcher3.Workspace$State r1 = com.android.launcher3.Workspace.State.OVERVIEW
            if (r12 == r1) goto L161
            com.android.launcher3.Workspace$State r1 = com.android.launcher3.Workspace.State.OVERVIEW_HIDDEN
            if (r12 != r1) goto L178
        L161:
            int r1 = r17.getViewportHeight()
            int r1 = r1 - r3
            android.graphics.Rect r12 = r0.mInsets
            int r12 = r12.top
            int r1 = r1 - r12
            android.graphics.Rect r12 = r0.mInsets
            int r12 = r12.bottom
            int r1 = r1 - r12
            int r12 = r15.getDefaultHomeLayoutHeight()
            int r1 = r1 + r12
            goto L187
        L176:
            r16 = r1
        L178:
            int r1 = r17.getViewportHeight()
            int r1 = r1 - r3
            android.graphics.Rect r12 = r0.mInsets
            int r12 = r12.top
            int r1 = r1 - r12
            android.graphics.Rect r12 = r0.mInsets
            int r12 = r12.bottom
            int r1 = r1 - r12
        L187:
            r0.mNormalChildHeight = r1
            r12 = r14
            r14 = r13
            goto L1a9
        L18c:
            r16 = r1
            if (r9 == 0) goto L195
            int r1 = r17.getViewportWidth()
            goto L1a3
        L195:
            int r1 = r17.getViewportWidth()
            android.graphics.Rect r2 = r0.mInsets
            int r2 = r2.left
            int r1 = r1 - r2
            android.graphics.Rect r2 = r0.mInsets
            int r2 = r2.right
            int r1 = r1 - r2
        L1a3:
            r2 = r1
            int r1 = r17.getViewportHeight()
            r12 = r14
        L1a9:
            if (r6 != 0) goto L1ac
            r6 = r2
        L1ac:
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r14)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r12)
            r10.measure(r2, r1)
            goto L1ba
        L1b8:
            r16 = r1
        L1ba:
            int r7 = r7 + 1
            r1 = r16
            goto Lf6
        L1c0:
            r0.setMeasuredDimension(r11, r8)
            return
        L1c4:
            super.onMeasure(r18, r19)
            return
        L1c8:
            super.onMeasure(r18, r19)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.PagedView.onMeasure(int, int):void");
    }

    protected void restoreScrollOnLayout() {
        setCurrentPage(getNextPage());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int i;
        int paddingTop;
        if (getChildCount() == 0) {
            return;
        }
        int childCount = getChildCount();
        int viewportOffsetX = getViewportOffsetX();
        int viewportOffsetY = getViewportOffsetY();
        if (this.mNeedResetTranslation) {
            if (this instanceof Workspace) {
                for (int i2 = 0; i2 < getChildCount(); i2++) {
                    CellLayout cellLayout = (CellLayout) getChildAt(i2);
                    if (cellLayout != null) {
                        ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
                        for (int i3 = 0; i3 < shortcutsAndWidgets.getChildCount(); i3++) {
                            shortcutsAndWidgets.getChildAt(i3).setTranslationX(0.0f);
                        }
                    }
                }
            }
            if (getPageIndicator() != null) {
                getPageIndicator().setTranslationX(0.0f);
            }
            this.mNeedResetTranslation = false;
            this.mFirstLayout = true;
        }
        this.mViewport.offset(viewportOffsetX, viewportOffsetY);
        boolean z = this.mIsRtl;
        int i4 = z ? childCount - 1 : 0;
        int i5 = z ? -1 : childCount;
        int i6 = z ? -1 : 1;
        int paddingTop2 = getPaddingTop() + getPaddingBottom();
        int paddingLeft = (((LayoutParams) getChildAt(i4).getLayoutParams()).isFullScreenPage ? 0 : getPaddingLeft()) + viewportOffsetX;
        if (this.mPageScrolls == null || childCount != this.mChildCountOnLastLayout) {
            this.mPageScrolls = new int[childCount];
        }
        while (i4 != i5) {
            View pageAt = getPageAt(i4);
            if (pageAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) pageAt.getLayoutParams();
                if (layoutParams.isFullScreenPage) {
                    paddingTop = viewportOffsetY;
                } else {
                    paddingTop = getPaddingTop() + viewportOffsetY + this.mInsets.top;
                    if (this.mCenterPagesVertically) {
                        paddingTop += ((((getViewportHeight() - this.mInsets.top) - this.mInsets.bottom) - paddingTop2) - pageAt.getMeasuredHeight()) / 2;
                        Workspace.State state = this instanceof Workspace ? ((Workspace) this).getState() : null;
                        if (com.lge.launcher3.util.Utilities.isLGUI7_1() && (pageAt instanceof CellLayout)) {
                            CellLayout cellLayout2 = (CellLayout) pageAt;
                            if (!cellLayout2.hasFullscreenItem() && state != null && (state == Workspace.State.OVERVIEW || state == Workspace.State.OVERVIEW_HIDDEN)) {
                                paddingTop -= cellLayout2.getDefaultHomeLayoutHeight() / 2;
                            }
                        }
                    }
                }
                int measuredWidth = pageAt.getMeasuredWidth();
                pageAt.layout(paddingLeft, paddingTop, pageAt.getMeasuredWidth() + paddingLeft, pageAt.getMeasuredHeight() + paddingTop);
                this.mPageScrolls[i4] = (paddingLeft - (layoutParams.isFullScreenPage ? 0 : getPaddingLeft())) - viewportOffsetX;
                int paddingRight = this.mPageSpacing;
                int i7 = i4 + i6;
                LayoutParams layoutParams2 = i7 != i5 ? (LayoutParams) getPageAt(i7).getLayoutParams() : null;
                if (layoutParams.isFullScreenPage) {
                    paddingRight = getPaddingLeft();
                } else if (layoutParams2 != null && layoutParams2.isFullScreenPage) {
                    paddingRight = getPaddingRight();
                }
                paddingLeft += measuredWidth + paddingRight + getChildGap();
            }
            i4 += i6;
        }
        ScreenEffectTargetManager.getInstance(getContext()).updatePageScrollsForLoop();
        if (this.mChildAddedOrRemoved) {
            updateMaxScrollX();
            setCurrentPage(getNextPage());
            updateFreescrollBounds();
            updateCurrentPageScroll();
            this.mChildAddedOrRemoved = false;
            if (isPageInTransition()) {
                if (this.mIsRtl) {
                    snapToPage(this.mCurrentPage);
                } else {
                    pageEndTransition();
                }
            }
        }
        LayoutTransition layoutTransition = getLayoutTransition();
        if (layoutTransition != null && layoutTransition.isRunning()) {
            layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() { // from class: com.lge.launcher3.PagedView.1
                @Override // android.animation.LayoutTransition.TransitionListener
                public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                }

                @Override // android.animation.LayoutTransition.TransitionListener
                public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                    if (transition.isRunning()) {
                        return;
                    }
                    transition.removeTransitionListener(this);
                    PagedView pagedView = PagedView.this;
                    pagedView.mMinScroll = pagedView.computeMinScrollX();
                    PagedView.this.updateMaxScrollX();
                }
            });
        } else {
            this.mMinScroll = computeMinScrollX();
            updateMaxScrollX();
        }
        boolean z2 = this.mFirstLayout;
        if (z2 && (i = this.mCurrentPage) >= 0 && i < childCount) {
            LGLog.i(TAG, "onLayout : mFirstLayout = " + z2 + ", mCurrentPage = " + i + ", scrollForPage = " + getScrollForPage(i) + ", scrollX " + getScrollX());
            updateCurrentPageScroll();
            this.mFirstLayout = false;
        }
        if (this.mScroller.isFinished() && this.mChildCountOnLastLayout != childCount) {
            int i8 = this.mRestorePage;
            if (i8 != -1001) {
                setCurrentPage(i8);
                this.mRestorePage = INVALID_RESTORE_PAGE;
            } else {
                setCurrentPage(getNextPage());
            }
            post(new Runnable() { // from class: com.lge.launcher3.PagedView.2
                @Override // java.lang.Runnable
                public void run() {
                    if (PagedView.this.mPageIndicator != null) {
                        PagedView.this.mPageIndicator.requestLayout();
                    }
                }
            });
        }
        this.mChildCountOnLastLayout = childCount;
        if (isReordering(true)) {
            updateDragViewTranslationDuringDrag();
        }
    }

    protected boolean getPageScrolls(int[] outPageScrolls, boolean layoutChildren, ComputePageScrollsLogic scrollLogic) {
        int childCount = getChildCount();
        boolean z = this.mIsRtl;
        boolean z2 = false;
        if (z) {
            childCount = -1;
        }
        int i = z ? -1 : 1;
        int paddingTop = ((((getPaddingTop() + getMeasuredHeight()) + this.mInsets.top) - this.mInsets.bottom) - getPaddingBottom()) / 2;
        int paddingLeft = this.mInsets.left + getPaddingLeft();
        int iOffsetForPageScrolls = offsetForPageScrolls() + paddingLeft;
        for (int i2 = z ? childCount - 1 : 0; i2 != childCount; i2 += i) {
            View pageAt = getPageAt(i2);
            if (scrollLogic.shouldIncludeView(pageAt)) {
                int measuredHeight = paddingTop - (pageAt.getMeasuredHeight() / 2);
                int measuredWidth = pageAt.getMeasuredWidth();
                if (layoutChildren) {
                    pageAt.layout(iOffsetForPageScrolls, measuredHeight, pageAt.getMeasuredWidth() + iOffsetForPageScrolls, pageAt.getMeasuredHeight() + measuredHeight);
                }
                int i3 = iOffsetForPageScrolls - paddingLeft;
                if (outPageScrolls[i2] != i3) {
                    outPageScrolls[i2] = i3;
                    z2 = true;
                }
                iOffsetForPageScrolls += measuredWidth + this.mPageSpacing + getChildGap();
            }
        }
        return z2;
    }

    protected void updateMinAndMaxScrollX() {
        this.mMinScroll = computeMinScrollX();
        this.mMaxScroll = computeMaxScrollX();
    }

    public void updateMaxScrollX() {
        this.mMaxScroll = computeMaxScrollX();
        updateFreescrollBounds();
    }

    protected int computeMaxScrollX() {
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

    public void onChildViewAdded(View parent, View child) {
        if (this.mPageIndicator != null && !isReordering(false)) {
            int iIndexOfChild = indexOfChild(child);
            if (GoogleNowManager.isAvailable(getContext())) {
                ((PageIndicatorExtension) this.mPageIndicator).addGoogleNowMarker();
            }
            if ((child instanceof CellLayout) && ((CellLayout) child).getMinusOneScreenPreview() != null) {
                ((PageIndicatorExtension) this.mPageIndicator).addGoogleNowMarker();
            } else {
                this.mPageIndicator.addMarker(iIndexOfChild, getPageIndicatorMarker(iIndexOfChild), true);
            }
        }
        this.mForceScreenScrolled = true;
        updateFreescrollBounds();
        invalidate();
    }

    public void onChildViewRemoved(View parent, View child) {
        this.mForceScreenScrolled = true;
        updateFreescrollBounds();
        invalidate();
    }

    private void removeMarkerForView(int index) {
        if (this.mPageIndicator == null || isReordering(false)) {
            return;
        }
        View childAt = getChildAt(index);
        if ((childAt instanceof CellLayout) && ((CellLayout) childAt).getMinusOneScreenPreview() != null) {
            if (GoogleNowManager.isAvailable(getContext())) {
                ((PageIndicatorExtension) this.mPageIndicator).addGoogleNowMarker();
                return;
            } else {
                ((PageIndicatorExtension) this.mPageIndicator).removeGoogleNowMarker();
                return;
            }
        }
        this.mPageIndicator.removeMarker(index, true);
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View v) {
        removeMarkerForView(indexOfChild(v));
        super.removeView(v);
    }

    @Override // android.view.ViewGroup
    public void removeViewInLayout(View v) {
        removeMarkerForView(indexOfChild(v));
        super.removeViewInLayout(v);
    }

    @Override // android.view.ViewGroup
    public void removeViewAt(int index) {
        removeMarkerForView(index);
        super.removeViewAt(index);
    }

    @Override // android.view.ViewGroup
    public void removeAllViewsInLayout() {
        PageIndicator pageIndicator = this.mPageIndicator;
        if (pageIndicator != null) {
            pageIndicator.removeAllMarkers(true);
        }
        super.removeAllViewsInLayout();
    }

    public int getChildOffset(int index) {
        if (index < 0 || index > getChildCount() - 1) {
            return 0;
        }
        return getPageAt(index).getLeft() - getViewportOffsetX();
    }

    protected void getFreeScrollPageRange(int[] range) {
        range[0] = 0;
        range[1] = Math.max(0, getChildCount() - 1);
    }

    protected void getVisiblePages(int[] range) {
        if (range != null) {
            if (range.equals(this.mDrawVisiblePagesRange)) {
                return;
            }
        } else {
            range = new int[2];
            this.mDrawVisiblePagesRange = range;
        }
        int childCount = getChildCount();
        int[] iArr = sTmpIntPoint;
        iArr[1] = 0;
        iArr[0] = 0;
        range[0] = -1;
        range[1] = -1;
        if (childCount > 0) {
            int viewportWidth = getViewportWidth();
            int i = 0;
            for (int i2 = 0; i2 < childCount; i2++) {
                View pageAt = getPageAt(i2);
                int[] iArr2 = sTmpIntPoint;
                iArr2[0] = 0;
                Utilities.getDescendantCoordRelativeToParent(pageAt, this, iArr2, false);
                if (iArr2[0] >= viewportWidth) {
                    if (range[0] != -1) {
                        break;
                    }
                } else {
                    iArr2[0] = pageAt.getMeasuredWidth();
                    Utilities.getDescendantCoordRelativeToParent(pageAt, this, iArr2, false);
                    if (iArr2[0] <= 0) {
                        if (range[0] != -1) {
                            break;
                        }
                    } else {
                        if (range[0] < 0) {
                            range[0] = i2;
                        }
                        i = i2;
                    }
                }
            }
            range[1] = i;
            return;
        }
        range[0] = -1;
        range[1] = -1;
    }

    protected Matrix getPageShiftMatrix() {
        return getMatrix();
    }

    protected boolean shouldDrawChild(View child) {
        return child.getVisibility() == 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        int childCount = getChildCount();
        if (childCount > 0) {
            this.mDrawVisiblePagesRange = null;
            int scrollX = getScrollX() + (getViewportWidth() / 2);
            if (scrollX != this.mLastScreenCenter || this.mForceScreenScrolled) {
                this.mForceScreenScrolled = false;
                screenScrolled(scrollX);
                this.mLastScreenCenter = scrollX;
            }
            getVisiblePages(this.mDrawVisiblePagesRange);
            int[] iArr = this.mDrawVisiblePagesRange;
            int i = iArr[0];
            int i2 = iArr[1];
            LoopNormalModeManager loopNormalModeManager = LoopNormalModeManager.getInstance(getContext());
            boolean zIsEnabled = loopNormalModeManager.isEnabled(this);
            boolean z = zIsEnabled && loopNormalModeManager.forceToDrawChildForLoop(this, i, i2);
            if ((i == -1 || i2 == -1) && !z) {
                return;
            }
            long drawingTime = getDrawingTime();
            canvas.save();
            canvas.clipRect(getScrollX(), getScrollY(), (getScrollX() + getRight()) - getLeft(), (getScrollY() + getBottom()) - getTop());
            if (!loopNormalModeManager.isOverviewState()) {
                pageShiftForLoop(childCount, loopNormalModeManager, zIsEnabled);
            }
            for (int i3 = childCount - 1; i3 >= 0; i3--) {
                View pageAt = getPageAt(i3);
                if (pageAt != this.mDragView) {
                    boolean zCheckValidDrawScreen = checkValidDrawScreen(i3, i, i2, pageAt);
                    shouldDrawChild(pageAt);
                    if (this.mForceDrawAllChildrenNextFrame || loopNormalModeManager.isWorkspaceSpringLoadedState() || loopNormalModeManager.isAllAppsView(this) || zCheckValidDrawScreen) {
                        canvas.save();
                        updateFullScreenSizeAndLocation(canvas, pageAt);
                        if (loopNormalModeManager.isWorkspaceSpringLoadedState() || loopNormalModeManager.isAllAppsView(this) || !zIsEnabled || !loopNormalModeManager.drawChildForLoop(canvas, this, pageAt)) {
                            drawChild(canvas, pageAt, drawingTime);
                        }
                        canvas.restore();
                    }
                }
            }
            View view = this.mDragView;
            if (view != null) {
                drawChild(canvas, view, drawingTime);
            }
            this.mForceDrawAllChildrenNextFrame = false;
            canvas.restore();
        }
    }

    private boolean checkValidDrawScreen(int index, int leftScreen, int rightScreen, View page) {
        if (leftScreen > index || index > rightScreen) {
            return LoopNormalModeManager.getInstance(getContext()).isEnabled(this) && rightScreen == getChildCount() - 1 && index == 0 && ((LayoutParams) page.getLayoutParams()).isFullScreenPage;
        }
        return true;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (getPageCount() > 0) {
            if (!this.mEdgeGlowLeft.isFinished()) {
                int iSave = canvas.save();
                Rect rect = this.mViewport;
                canvas.translate(rect.left, rect.top);
                canvas.rotate(270.0f);
                int[] iArr = sTmpIntPoint;
                getEdgeVerticalPostion(iArr);
                canvas.translate(rect.top - iArr[1], 0.0f);
                this.mEdgeGlowLeft.setSize(iArr[1] - iArr[0], rect.width());
                if (this.mEdgeGlowLeft.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(iSave);
            }
            if (this.mEdgeGlowRight.isFinished()) {
                return;
            }
            int iSave2 = canvas.save();
            Rect rect2 = this.mViewport;
            canvas.translate(rect2.left + this.mPageScrolls[this.mIsRtl ? 0 : getPageCount() - 1], rect2.top);
            canvas.rotate(90.0f);
            int[] iArr2 = sTmpIntPoint;
            getEdgeVerticalPostion(iArr2);
            canvas.translate(iArr2[0] - rect2.top, -rect2.width());
            this.mEdgeGlowRight.setSize(iArr2[1] - iArr2[0], rect2.width());
            if (this.mEdgeGlowRight.draw(canvas)) {
                postInvalidateOnAnimation();
            }
            canvas.restoreToCount(iSave2);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        int iIndexToPage = indexToPage(indexOfChild(child));
        boolean zIsFinished = this.mScroller.isFinished();
        if (iIndexToPage == this.mCurrentPage && zIsFinished) {
            return false;
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
        if (direction == 17) {
            if (getCurrentPage() > 0) {
                snapToPage(getCurrentPage() - 1);
                return true;
            }
        } else if (direction == 66 && getCurrentPage() < getPageCount() - 1) {
            snapToPage(getCurrentPage() + 1);
            return true;
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        int i = this.mCurrentPage;
        if (i >= 0 && i < getPageCount() && getPageAt(this.mCurrentPage) != null) {
            getPageAt(this.mCurrentPage).addFocusables(views, direction, focusableMode);
        }
        if (direction == 17) {
            int i2 = this.mCurrentPage;
            if (i2 <= 0 || getPageAt(i2 - 1) == null) {
                return;
            }
            getPageAt(this.mCurrentPage - 1).addFocusables(views, direction, focusableMode);
            return;
        }
        if (direction != 66 || this.mCurrentPage >= getPageCount() - 1 || getPageAt(this.mCurrentPage + 1) == null) {
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
            if (this.mTouchState == 1) {
                snapToDestination();
                this.mTouchState = 0;
            }
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    protected boolean hitsPreviousPage(float x, float y) {
        return this.mIsRtl ? x > ((float) (((getViewportOffsetX() + getViewportWidth()) - getPaddingRight()) - this.mPageSpacing)) : x < ((float) ((getViewportOffsetX() + getPaddingLeft()) + this.mPageSpacing));
    }

    protected boolean hitsNextPage(float x, float y) {
        return this.mIsRtl ? x < ((float) ((getViewportOffsetX() + getPaddingLeft()) + this.mPageSpacing)) : x > ((float) (((getViewportOffsetX() + getViewportWidth()) - getPaddingRight()) - this.mPageSpacing));
    }

    private boolean isTouchPointInViewportWithBuffer(int x, int y) {
        Rect rect = sTmpRect;
        rect.set(this.mViewport.left - (this.mViewport.width() / 2), this.mViewport.top, this.mViewport.right + (this.mViewport.width() / 2), this.mViewport.bottom);
        return rect.contains(x, y);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        acquireVelocityTrackerAndAddMovement(ev);
        if (getChildCount() <= 0) {
            return super.onInterceptTouchEvent(ev);
        }
        int action = ev.getAction() & 255;
        if (action == 0) {
            float x = ev.getX();
            float y = ev.getY();
            this.mDownMotionX = x;
            this.mDownMotionY = y;
            this.mDownScrollX = getScrollX();
            this.mLastMotionX = x;
            this.mLastMotionY = y;
            float[] fArrMapPointFromViewToParent = mapPointFromViewToParent(this, x, y);
            this.mParentDownMotionX = fArrMapPointFromViewToParent[0];
            this.mParentDownMotionY = fArrMapPointFromViewToParent[1];
            this.mLastMotionXRemainder = 0.0f;
            this.mTotalMotionX = 0.0f;
            this.mActivePointerId = ev.getPointerId(0);
            mIsOutOfTouchSlop = false;
            int iAbs = Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX());
            if ((this.mScroller.isFinished() || iAbs < this.mTouchSlop / 3) && isDistZeroChecked(iAbs)) {
                this.mIsBeingDragged = false;
                this.mTouchState = 0;
                if (!this.mScroller.isFinished() && !this.mFreeScroll) {
                    setCurrentPage(getNextPage());
                    pageEndTransition();
                }
            } else {
                boolean zIsTouchPointInViewportWithBuffer = isTouchPointInViewportWithBuffer((int) this.mDownMotionX, (int) this.mDownMotionY);
                this.mIsBeingDragged = zIsTouchPointInViewportWithBuffer;
                if (zIsTouchPointInViewportWithBuffer) {
                    this.mTouchState = 1;
                } else {
                    this.mTouchState = 0;
                }
            }
        } else if (action == 1) {
            int i = this.mTouchState;
            if (i == 1 || i == 4 || i == 5 || i == 6) {
                onTouchEvent(ev);
            }
        } else if (action == 2) {
            float fAbs = Math.abs(ev.getX() - this.mDownMotionX);
            float fAbs2 = Math.abs(ev.getY() - this.mDownMotionY);
            ev.getY();
            int i2 = this.mTouchSlop;
            boolean z = fAbs > ((float) i2);
            boolean z2 = fAbs2 > ((float) i2);
            int i3 = this.mModifiedTouchSlop;
            if (i3 > 0) {
                z = fAbs > ((float) i3);
            }
            if (!mIsOutOfTouchSlop && (z || z2)) {
                mIsOutOfTouchSlop = true;
                if (getContext() instanceof Launcher) {
                    com.lge.launcher3.util.Utilities.cancelProcPreLaunch(getContext());
                }
            }
            if (this.mTouchState == 1) {
                onTouchEvent(ev);
                if (z || z2) {
                    cancelCurrentPageLongPress();
                }
                return z;
            }
            if (this.mActivePointerId != -1) {
                determineScrollingStart(ev);
                if (this.mTouchState == 1) {
                    return z;
                }
            }
        } else if (action == 3) {
            if (this.mTouchState == 1) {
                snapToDestination();
            }
            resetTouchState();
        } else if (action == 6) {
            if (getContext() instanceof Launcher) {
                Launcher launcher = (Launcher) getContext();
                if (this.mInAppsalpha < 1.0f && this.mInAppsDeltaY != 0.0f && !launcher.isCleanViewState()) {
                    exitInApps();
                }
            }
            onSecondaryPointerUp(ev);
            releaseVelocityTracker();
        }
        return this.mTouchState != 0;
    }

    public boolean isHandlingTouch() {
        return this.mTouchState == 1;
    }

    protected void determineScrollingStart(MotionEvent ev) {
        determineScrollingStart(ev, 1.0f);
    }

    private boolean isValidGestureAngle(float deltaX, float deltaY) {
        if (!(this instanceof Workspace)) {
            return true;
        }
        float degrees = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
        if (degrees > 90.0f) {
            degrees = 180.0f - degrees;
        }
        return degrees >= 0.0f && degrees < ((float) this.mGestureAngle);
    }

    protected void determineScrollingStart(MotionEvent ev, float touchSlopScale) {
        int iFindPointerIndex = ev.findPointerIndex(this.mActivePointerId);
        if (iFindPointerIndex == -1) {
            return;
        }
        float x = ev.getX(iFindPointerIndex);
        float y = ev.getY(iFindPointerIndex);
        if (isTouchPointInViewportWithBuffer((int) x, (int) y)) {
            int iAbs = (int) Math.abs(x - this.mLastMotionX);
            int iAbs2 = (int) Math.abs(y - this.mLastMotionY);
            if ((iAbs > Math.round(touchSlopScale * ((float) this.mTouchSlop))) && isValidGestureAngle(iAbs, iAbs2)) {
                float f = this.mLastMotionX;
                this.mIsBeingDragged = true;
                this.mTouchState = 1;
                this.mTotalMotionX += Math.abs(f - x);
                this.mLastMotionX = x;
                this.mLastMotionXRemainder = 0.0f;
                this.mTouchX = getViewportOffsetX() + getScrollX();
                this.mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
                onScrollInteractionBegin();
                pageBeginTransition();
                scrollBy((int) (f - x), 0);
            }
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
        int scrollForPage = screenCenter - (getScrollForPage(page) + (getViewportWidth() / 2));
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
        int paddingRight = 0;
        if (iArr == null || index >= iArr.length || index < 0) {
            return 0;
        }
        View childAt = getChildAt(index);
        if (!((LayoutParams) childAt.getLayoutParams()).isFullScreenPage) {
            paddingRight = this.mIsRtl ? getPaddingRight() : getPaddingLeft();
        }
        return (int) (childAt.getX() - ((this.mPageScrolls[index] + paddingRight) + getViewportOffsetX()));
    }

    protected void dampedOverScroll(float amount) {
        float viewportWidth = amount / getViewportWidth();
        int iDampedScroll = OverScroll.dampedScroll(amount, getMeasuredWidth());
        if (viewportWidth < 0.0f) {
            this.mOverScrollX = iDampedScroll;
            this.mEdgeGlowLeft.onPull(-viewportWidth);
        } else if (viewportWidth > 0.0f) {
            this.mEdgeGlowRight.onPull(viewportWidth);
        } else {
            this.mOverScrollX = this.mMaxScroll + iDampedScroll;
            return;
        }
        invalidate();
    }

    protected void overScroll(float amount) {
        dampedOverScroll(amount);
    }

    public void enableFreeScroll(boolean settleOnPageInFreeScroll) {
        setEnableFreeScroll(true);
        this.mSettleOnPageInFreeScroll = settleOnPageInFreeScroll;
    }

    public void disableFreeScroll() {
        setEnableFreeScroll(false);
    }

    void updateFreescrollBounds() {
        getFreeScrollPageRange(this.mTempVisiblePagesRange);
        if (this.mIsRtl) {
            this.mFreeScrollMinScrollX = getScrollForPage(this.mTempVisiblePagesRange[1]);
            this.mFreeScrollMaxScrollX = getScrollForPage(this.mTempVisiblePagesRange[0]);
        } else {
            this.mFreeScrollMinScrollX = getScrollForPage(this.mTempVisiblePagesRange[0]);
            this.mFreeScrollMaxScrollX = getScrollForPage(this.mTempVisiblePagesRange[1]);
        }
    }

    public void setEnableFreeScroll(boolean freeScroll) {
        this.mFreeScroll = freeScroll;
        if (freeScroll) {
            updateFreescrollBounds();
            getFreeScrollPageRange(this.mTempVisiblePagesRange);
            int currentPage = getCurrentPage();
            int[] iArr = this.mTempVisiblePagesRange;
            if (currentPage < iArr[0]) {
                setCurrentPage(iArr[0]);
            } else {
                int currentPage2 = getCurrentPage();
                int[] iArr2 = this.mTempVisiblePagesRange;
                if (currentPage2 > iArr2[1]) {
                    setCurrentPage(iArr2[1]);
                }
            }
        }
        setEnableOverscroll(!freeScroll);
    }

    protected void setEnableOverscroll(boolean enable) {
        this.mAllowOverScroll = enable;
    }

    protected int getNearestHoverOverPageIndex() {
        View view = this.mDragView;
        if (view == null) {
            return -1;
        }
        int translationX = (int) (view.getTranslationX() * 1.8f);
        int measuredWidth = this.mDragView.getMeasuredWidth();
        if (Math.abs(translationX) > measuredWidth) {
            translationX = (int) this.mDragView.getTranslationX();
        }
        int left = this.mDragView.getLeft() + (measuredWidth / 2) + translationX;
        getFreeScrollPageRange(this.mTempVisiblePagesRange);
        int i = Integer.MAX_VALUE;
        int iIndexOfChild = indexOfChild(this.mDragView);
        for (int i2 = this.mTempVisiblePagesRange[0]; i2 <= this.mTempVisiblePagesRange[1]; i2++) {
            View pageAt = getPageAt(i2);
            int iAbs = Math.abs(left - (pageAt.getLeft() + (pageAt.getMeasuredWidth() / 2)));
            if (iAbs < i) {
                iIndexOfChild = i2;
                i = iAbs;
            }
        }
        return iIndexOfChild;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int i;
        int i2;
        super.onTouchEvent(ev);
        if (getChildCount() <= 0) {
            return super.onTouchEvent(ev);
        }
        acquireVelocityTrackerAndAddMovement(ev);
        int action = ev.getAction() & 255;
        int scrollForPage = 0;
        if (action == 0) {
            if (!this.mScroller.isFinished()) {
                abortScrollerAnimation(false);
            }
            float x = ev.getX();
            this.mLastMotionX = x;
            this.mDownMotionX = x;
            float y = ev.getY();
            this.mLastMotionY = y;
            this.mDownMotionY = y;
            this.mDownScrollX = getScrollX();
            float[] fArrMapPointFromViewToParent = mapPointFromViewToParent(this, this.mLastMotionX, this.mLastMotionY);
            this.mParentDownMotionX = fArrMapPointFromViewToParent[0];
            this.mParentDownMotionY = fArrMapPointFromViewToParent[1];
            this.mLastMotionXRemainder = 0.0f;
            this.mTotalMotionX = 0.0f;
            this.mActivePointerId = ev.getPointerId(0);
            if (this.mTouchState == 1) {
                onScrollInteractionBegin();
                pageBeginTransition();
            }
        } else if (action == 1) {
            int i3 = this.mTouchState;
            if (i3 == 1) {
                int i4 = this.mActivePointerId;
                int iFindPointerIndex = ev.findPointerIndex(i4);
                if (iFindPointerIndex == -1) {
                    return true;
                }
                if (this.mCurrentPage >= getChildCount() || this.mCurrentPage < 0) {
                    this.mCurrentPage = Math.max(0, Math.min(this.mCurrentPage, getPageCount() - 1));
                }
                float x2 = ev.getX(iFindPointerIndex);
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
                int xVelocity = (int) velocityTracker.getXVelocity(i4);
                int i5 = (int) (x2 - this.mDownMotionX);
                float measuredWidth = getPageAt(this.mCurrentPage).getMeasuredWidth();
                boolean z = ((float) Math.abs(i5)) > 0.4f * measuredWidth;
                float fAbs = this.mTotalMotionX + Math.abs((this.mLastMotionX + this.mLastMotionXRemainder) - x2);
                this.mTotalMotionX = fAbs;
                boolean z2 = fAbs > 25.0f && Math.abs(xVelocity) > this.mFlingThresholdVelocity;
                LoopNormalModeManager loopNormalModeManager = LoopNormalModeManager.getInstance(getContext());
                getNearestHoverOverPageIndex();
                if (!this.mFreeScroll) {
                    boolean z3 = ((float) Math.abs(i5)) > measuredWidth * 0.1f && Math.signum((float) xVelocity) != Math.signum((float) i5) && z2;
                    boolean z4 = this.mIsRtl;
                    boolean z5 = !z4 ? i5 >= 0 : i5 <= 0;
                    if (!z4 ? xVelocity < 0 : xVelocity > 0) {
                        scrollForPage = 1;
                    }
                    boolean zIsEnabled = loopNormalModeManager.isEnabled(this);
                    if (((z && !z5 && !z2) || (z2 && scrollForPage == 0)) && ((i2 = this.mCurrentPage) > 0 || zIsEnabled)) {
                        if (!z3) {
                            i2--;
                        }
                        snapToPageWithVelocity(i2, xVelocity);
                    } else if (((z && z5 && !z2) || (z2 && scrollForPage != 0)) && (this.mCurrentPage < getChildCount() - 1 || zIsEnabled)) {
                        int i6 = this.mCurrentPage;
                        if (!z3) {
                            i6++;
                        }
                        snapToPageWithVelocity(i6, xVelocity);
                    } else {
                        snapToDestination();
                    }
                } else {
                    if (!this.mScroller.isFinished()) {
                        abortScrollerAnimation(true);
                    }
                    float scaleX = getScaleX();
                    int scrollX = (int) (getScrollX() * scaleX);
                    LGLog.d(TAG, "4. TouchEvent initialScrollX = " + scrollX);
                    boolean z6 = this.mIsRtl;
                    this.mScroller.setInterpolator(this.mDefaultInterpolator);
                    this.mScroller.fling(scrollX, getScrollY(), (int) (((float) (-xVelocity)) * scaleX), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
                    int finalX = (int) (this.mScroller.getFinalX() / scaleX);
                    if (LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue()) {
                        this.mNextPage = getPageNearestToCenterOfScreen(finalX, true);
                    } else {
                        this.mNextPage = getPageNearestToCenterOfScreen(finalX, false);
                    }
                    int scrollForPage2 = getScrollForPage(!this.mIsRtl ? 0 : getPageCount() - 1);
                    int scrollForPage3 = getScrollForPage(!this.mIsRtl ? getPageCount() - 1 : 0);
                    if (this.mSettleOnPageInFreeScroll && finalX > 0 && finalX < (i = this.mMaxScroll)) {
                        if (finalX >= scrollForPage2 / 2) {
                            scrollForPage = finalX > (scrollForPage3 + i) / 2 ? i : getScrollForPage(this.mNextPage);
                        }
                        this.mScroller.setFinalX((int) (scrollForPage * getScaleX()));
                        int duration = 270 - this.mScroller.getDuration();
                        if (duration > 0) {
                            this.mScroller.extendDuration(duration);
                        }
                    }
                    invalidate();
                }
                onScrollInteractionEnd(xVelocity / getViewportWidth());
            } else if (i3 == 2) {
                int iMax = Math.max(0, this.mCurrentPage - 1);
                if (iMax != this.mCurrentPage) {
                    snapToPage(iMax);
                } else {
                    snapToDestination();
                }
            } else if (i3 == 3) {
                int iMin = Math.min(getChildCount() - 1, this.mCurrentPage + 1);
                if (iMin != this.mCurrentPage) {
                    snapToPage(iMin);
                } else {
                    snapToDestination();
                }
            } else if (i3 == 4) {
                this.mLastMotionX = ev.getX();
                float y2 = ev.getY();
                this.mLastMotionY = y2;
                float[] fArrMapPointFromViewToParent2 = mapPointFromViewToParent(this, this.mLastMotionX, y2);
                this.mParentDownMotionX = fArrMapPointFromViewToParent2[0];
                this.mParentDownMotionY = fArrMapPointFromViewToParent2[1];
                updateDragViewTranslationDuringDrag();
            } else if (i3 == 5) {
                int iFindPointerIndex2 = ev.findPointerIndex(this.mActivePointerId);
                if (iFindPointerIndex2 == -1) {
                    exitInApps();
                    this.mCheckInapps = false;
                    return true;
                }
                ev.getX(iFindPointerIndex2);
                float y3 = ev.getY(iFindPointerIndex2);
                if (this.mIsInAppsEnabled && !this.mCheckInapps) {
                    checkEnteringInAppsCondition(y3);
                }
            } else if (i3 == 6) {
                int iFindPointerIndex3 = ev.findPointerIndex(this.mActivePointerId);
                if (iFindPointerIndex3 == -1) {
                    exitSwipeUpAppDrawer();
                    this.mCheckSwipeUpAppDrawer = false;
                    return true;
                }
                ev.getX(iFindPointerIndex3);
                float y4 = ev.getY(iFindPointerIndex3);
                if (LGHomeFeature.isSwipeUpAppDrawerEnable() && !this.mCheckSwipeUpAppDrawer) {
                    checkEnteringSwipeUpAppDrawer(y4);
                    if (((Launcher) getContext()).isInState(LauncherState.CLEAN_VIEW)) {
                        this.mCheckSwipeUpAppDrawer = false;
                        backToWorkspaceFromSwipeUpAppDrawer(false);
                    }
                }
            } else if (!this.mCancelTap) {
                onUnhandledTap(ev);
            }
            removeCallbacks(this.mSidePageHoverRunnable);
            resetTouchState();
        } else if (action == 2) {
            int i7 = this.mTouchState;
            if (i7 == 1) {
                if (ev.findPointerIndex(this.mActivePointerId) == -1) {
                    return true;
                }
                float predictionX = getPredictionX(ev);
                float f = (this.mLastMotionX + this.mLastMotionXRemainder) - predictionX;
                this.mTotalMotionX += Math.abs(f);
                if (Math.abs(f) >= 1.0f) {
                    this.mTouchX += f;
                    this.mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
                    int i8 = (int) f;
                    scrollBy(i8, 0);
                    this.mLastMotionX = predictionX;
                    this.mLastMotionXRemainder = f - i8;
                } else {
                    awakenScrollBars();
                }
            } else if (i7 == 4) {
                this.mLastMotionX = ev.getX();
                float y5 = ev.getY();
                this.mLastMotionY = y5;
                float[] fArrMapPointFromViewToParent3 = mapPointFromViewToParent(this, this.mLastMotionX, y5);
                this.mParentDownMotionX = fArrMapPointFromViewToParent3[0];
                this.mParentDownMotionY = fArrMapPointFromViewToParent3[1];
                updateDragViewTranslationDuringDrag();
                final int iIndexOfChild = indexOfChild(this.mDragView);
                final int nearestHoverOverPageIndex = getNearestHoverOverPageIndex();
                if (nearestHoverOverPageIndex > -1 && nearestHoverOverPageIndex != indexOfChild(this.mDragView)) {
                    int[] iArr = this.mTempVisiblePagesRange;
                    iArr[0] = 0;
                    iArr[1] = getPageCount() - 1;
                    getFreeScrollPageRange(this.mTempVisiblePagesRange);
                    boolean zIsFinished = this.mScroller.isFinished();
                    int[] iArr2 = this.mTempVisiblePagesRange;
                    if (iArr2[0] <= nearestHoverOverPageIndex && nearestHoverOverPageIndex <= iArr2[1] && nearestHoverOverPageIndex != this.mSidePageHoverIndex && zIsFinished) {
                        this.mSidePageHoverIndex = nearestHoverOverPageIndex;
                        Runnable runnable = new Runnable() { // from class: com.lge.launcher3.PagedView.3
                            @Override // java.lang.Runnable
                            public void run() {
                                LayoutParams layoutParams = (LayoutParams) PagedView.this.getChildAt(0).getLayoutParams();
                                boolean z7 = ((CellLayout) PagedView.this.getChildAt(0)).getMinusOneScreenPreview() != null;
                                if (nearestHoverOverPageIndex == 0 && (layoutParams.isFullScreenPage || z7)) {
                                    return;
                                }
                                PagedView.this.snapToPage(nearestHoverOverPageIndex);
                                int i9 = iIndexOfChild;
                                int i10 = nearestHoverOverPageIndex;
                                int i11 = i9 < i10 ? -1 : 1;
                                if (i9 > i10) {
                                    i10 = i9 - 1;
                                }
                                for (int i12 = i9 < i10 ? i9 + 1 : i10; i12 <= i10; i12++) {
                                    View childAt = PagedView.this.getChildAt(i12);
                                    int viewportOffsetX = PagedView.this.getViewportOffsetX() + PagedView.this.getChildOffset(i12);
                                    int viewportOffsetX2 = PagedView.this.getViewportOffsetX() + PagedView.this.getChildOffset(i12 + i11);
                                    AnimatorSet animatorSet = (AnimatorSet) childAt.getTag(100);
                                    if (animatorSet != null) {
                                        animatorSet.cancel();
                                    }
                                    childAt.setTranslationX(viewportOffsetX - viewportOffsetX2);
                                    AnimatorSet animatorSet2 = new AnimatorSet();
                                    animatorSet2.setDuration(PagedView.REORDERING_REORDER_REPOSITION_DURATION);
                                    animatorSet2.playTogether(ObjectAnimator.ofFloat(childAt, "translationX", 0.0f));
                                    animatorSet2.start();
                                    childAt.setTag(animatorSet2);
                                }
                                PagedView pagedView = PagedView.this;
                                pagedView.removeView(pagedView.mDragView);
                                PagedView pagedView2 = PagedView.this;
                                pagedView2.addView(pagedView2.mDragView, nearestHoverOverPageIndex);
                                PagedView.this.mSidePageHoverIndex = -1;
                                if (PagedView.this.mPageIndicator != null) {
                                    PagedView.this.mPageIndicator.setActiveMarker(PagedView.this.getNextPage());
                                }
                            }
                        };
                        this.mSidePageHoverRunnable = runnable;
                        postDelayed(runnable, REORDERING_SIDE_PAGE_HOVER_TIMEOUT);
                    }
                } else {
                    removeCallbacks(this.mSidePageHoverRunnable);
                    this.mSidePageHoverIndex = -1;
                }
            } else if (i7 != 5 && i7 != 6) {
                determineScrollingStart(ev);
            }
        } else if (action == 3) {
            if (this.mTouchState == 1) {
                snapToDestination();
            }
            onScrollInteractionEnd(0);
            resetTouchState();
        } else if (action == 6) {
            int i9 = this.mTouchState;
            if (i9 == 5 && !this.mCheckInapps) {
                exitInApps();
                this.mCheckInapps = false;
            } else if (i9 == 6 && !this.mCheckSwipeUpAppDrawer) {
                exitSwipeUpAppDrawer();
                this.mCheckSwipeUpAppDrawer = false;
            }
            onSecondaryPointerUp(ev);
            releaseVelocityTracker();
        }
        return true;
    }

    public void resetTouchState() {
        releaseVelocityTracker();
        endReordering();
        this.mCancelTap = false;
        this.mIsBeingDragged = false;
        this.mTouchState = 0;
        this.mActivePointerId = -1;
        this.mEdgeGlowLeft.onRelease();
        this.mEdgeGlowRight.onRelease();
    }

    protected void onUnhandledTap(MotionEvent ev) {
        if (getContext() instanceof Launcher) {
            ((Launcher) getContext()).onClick(this);
        }
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        float f;
        float axisValue;
        boolean z = false;
        if (event.isFromSource(16)) {
            setBackgroundTransparentOfFocusHandler(false);
        }
        if ((event.getSource() & 2) != 0 && event.getAction() == 8) {
            if ((event.getMetaState() & 1) != 0) {
                axisValue = event.getAxisValue(9);
                f = 0.0f;
            } else {
                f = -event.getAxisValue(9);
                axisValue = event.getAxisValue(10);
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

    protected void setBackgroundTransparentOfFocusHandler(boolean useTransparentColor) {
        Launcher launcher;
        if (!(getContext() instanceof Launcher) || (launcher = (Launcher) getContext()) == null || launcher.mFocusHandler == null) {
            return;
        }
        launcher.mFocusHandler.setBackgroundTransparent(useTransparentColor);
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        this.mTouchPrediction.observedEvent(ev);
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
        int action = (ev.getAction() & 65280) >> 8;
        if (ev.getPointerId(action) == this.mActivePointerId) {
            int i = action == 0 ? 1 : 0;
            float x = ev.getX(i);
            this.mDownMotionX = x;
            this.mLastMotionX = x;
            this.mLastMotionY = ev.getY(i);
            this.mLastMotionXRemainder = 0.0f;
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
        return getPageNearestToCenterOfScreen(getScrollX(), false);
    }

    private int getPageNearestToCenterOfScreen(int scaledScrollX, boolean usePageScrollForChildLocation) {
        int iAbs;
        int viewportOffsetX = getViewportOffsetX() + scaledScrollX + (getViewportWidth() / 2);
        int childCount = getChildCount();
        int i = Integer.MAX_VALUE;
        int i2 = -1;
        for (int i3 = 0; i3 < childCount; i3++) {
            int measuredWidth = getPageAt(i3).getMeasuredWidth() / 2;
            if (usePageScrollForChildLocation) {
                iAbs = Math.abs(((getViewportOffsetX() + (this.mPageScrolls[i3] + getPaddingLeft())) + measuredWidth) - viewportOffsetX);
            } else {
                iAbs = Math.abs(((getViewportOffsetX() + getChildOffset(i3)) + measuredWidth) - ((LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue() ? measuredWidth / 4 : 0) + viewportOffsetX));
            }
            if (iAbs < i) {
                i2 = i3;
                i = iAbs;
            }
        }
        return i2;
    }

    public void snapToDestination() {
        snapToPage(getPageNearestToCenterOfScreen(), getPageSnapDuration());
    }

    protected boolean isInOverScroll() {
        int i = this.mOverScrollX;
        return i > this.mMaxScroll || i < 0;
    }

    protected int getPageSnapDuration() {
        if (isInOverScroll()) {
            return OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION;
        }
        return 600;
    }

    protected float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((float) (((double) (f - 0.5f)) * 0.4712389167638204d));
    }

    protected boolean snapToPageWithVelocity(int whichPage, int velocity) {
        int iValidateNewPage = validateNewPage(whichPage);
        int viewportWidth = getViewportWidth() / 2;
        int scrollForPage = getScrollForPage(iValidateNewPage);
        getScrollX();
        int unboundedScrollX = scrollForPage - getUnboundedScrollX();
        if (Math.abs(velocity) < this.mMinFlingVelocity) {
            return snapToPage(iValidateNewPage, 600);
        }
        float fMin = Math.min(1.0f, (Math.abs(unboundedScrollX) * 1.0f) / (viewportWidth * 2));
        float f = viewportWidth;
        return snapToPage(iValidateNewPage, unboundedScrollX, Math.round(Math.abs((f + (distanceInfluenceForSnapDuration(fMin) * f)) / Math.max(this.mMinSnapVelocity, Math.abs(velocity))) * 1000.0f) * 4);
    }

    public boolean snapToPage(int whichPage) {
        return snapToPage(whichPage, 600);
    }

    public boolean snapToPageImmediately(int whichPage) {
        return snapToPage(whichPage, 600, true, null);
    }

    public boolean snapToPage(int whichPage, int duration) {
        return snapToPage(whichPage, duration, false, null);
    }

    public boolean snapToPage(int whichPage, int duration, TimeInterpolator interpolator) {
        return snapToPage(whichPage, duration, false, interpolator);
    }

    protected boolean snapToPage(int whichPage, int duration, boolean immediate, TimeInterpolator interpolator) {
        int iValidateNewPage = validateNewPage(whichPage);
        int scrollForPage = getScrollForPage(iValidateNewPage);
        getScrollX();
        return snapToPage(iValidateNewPage, scrollForPage - getUnboundedScrollX(), duration, immediate, interpolator);
    }

    protected boolean snapToPage(int whichPage, int delta, int duration) {
        CPUBoostService.scrollboostUp(getContext());
        return snapToPage(whichPage, delta, duration, false, null);
    }

    protected boolean snapToPage(int whichPage, int delta, int duration, boolean immediate, TimeInterpolator interpolator) {
        int i;
        int i2;
        if (this.mFirstLayout) {
            setCurrentPage(whichPage);
            return false;
        }
        int iValidateNewPage = validateNewPage(whichPage);
        this.mNextPage = iValidateNewPage;
        View focusedChild = getFocusedChild();
        if (focusedChild != null && iValidateNewPage != (i2 = this.mCurrentPage) && focusedChild == getPageAt(i2)) {
            focusedChild.clearFocus();
        }
        pageBeginTransition();
        awakenScrollBars(duration);
        if (immediate) {
            i = 0;
        } else {
            if (duration == 0) {
                duration = Math.abs(delta);
            }
            i = duration;
        }
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(false);
        }
        if (interpolator != null) {
            this.mScroller.setInterpolator(interpolator);
        } else {
            this.mScroller.setInterpolator(this.mDefaultInterpolator);
        }
        this.mScroller.startScroll(getUnboundedScrollX(), 0, delta, 0, i);
        updatePageIndicator();
        if (immediate) {
            computeScroll();
        }
        this.mForceScreenScrolled = true;
        invalidate();
        return Math.abs(delta) > 0;
    }

    public void scrollLeft() {
        if (getNextPage() > 0) {
            snapToPage(getNextPage() - 1);
        }
    }

    public void scrollRight() {
        if (getNextPage() < getChildCount() - 1) {
            snapToPage(getNextPage() + 1);
        }
    }

    protected boolean onOverscroll(int amount) {
        if (!this.mAllowOverScroll) {
            return false;
        }
        onScrollInteractionBegin();
        overScroll(amount);
        onScrollInteractionEnd(0);
        return true;
    }

    public int getPageForView(View v) {
        if (v == null) {
            return -1;
        }
        ViewParent parent = v.getParent();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (parent == getPageAt(i)) {
                return i;
            }
        }
        return -1;
    }

    @Override // android.view.View
    public boolean performLongClick() {
        this.mCancelTap = true;
        return super.performLongClick();
    }

    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.lge.launcher3.PagedView.SavedState.1
            /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int currentPage;

        SavedState(Parcelable superState) {
            super(superState);
            this.currentPage = -1;
        }

        SavedState(Parcel in) {
            super(in);
            this.currentPage = -1;
            this.currentPage = in.readInt();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.currentPage);
        }
    }

    private void animateDragViewToOriginalPosition() {
        if (this.mDragView != null) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(REORDERING_DROP_REPOSITION_DURATION);
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.mDragView, "translationX", 0.0f), ObjectAnimator.ofFloat(this.mDragView, "translationY", 0.0f), ObjectAnimator.ofFloat(this.mDragView, "scaleX", 1.0f), ObjectAnimator.ofFloat(this.mDragView, "scaleY", 1.0f));
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.PagedView.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PagedView.this.onPostReorderingAnimationCompleted();
                }
            });
            animatorSet.start();
        }
    }

    public void onStartReordering() {
        this.mTouchState = 4;
        this.mIsReordering = true;
        invalidate();
    }

    public void onPostReorderingAnimationCompleted() {
        int i = this.mPostReorderingPreZoomInRemainingAnimationCount - 1;
        this.mPostReorderingPreZoomInRemainingAnimationCount = i;
        if (this.mPostReorderingPreZoomInRunnable != null) {
            if (i == 0 || LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                this.mPostReorderingPreZoomInRunnable.run();
                this.mPostReorderingPreZoomInRunnable = null;
            }
        }
    }

    public void onEndReordering() {
        this.mIsReordering = false;
        this.mDragView = null;
    }

    public boolean startReordering(View v) {
        int iIndexOfChild = indexOfChild(v);
        if (this.mTouchState == 0 && iIndexOfChild != -1) {
            int[] iArr = this.mTempVisiblePagesRange;
            iArr[0] = 0;
            iArr[1] = getPageCount() - 1;
            getFreeScrollPageRange(this.mTempVisiblePagesRange);
            this.mReorderingStarted = true;
            int[] iArr2 = this.mTempVisiblePagesRange;
            if (iArr2[0] <= iIndexOfChild && iIndexOfChild <= iArr2[1]) {
                if (this.mDragView != null) {
                    Log.d(TAG, "mDragView is already");
                    resetTouchState();
                } else {
                    View childAt = getChildAt(iIndexOfChild);
                    this.mDragView = childAt;
                    childAt.animate().scaleX(1.15f).scaleY(1.15f).setDuration(100L).start();
                    this.mDragViewBaselineLeft = this.mDragView.getLeft();
                    snapToPage(getPageNearestToCenterOfScreen());
                    disableFreeScroll();
                    onStartReordering();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isReordering(boolean testTouchState) {
        boolean z = this.mIsReordering;
        if (testTouchState) {
            return z & (this.mTouchState == 4);
        }
        return z;
    }

    void endReordering() {
        if (this.mReorderingStarted) {
            this.mReorderingStarted = false;
            final Runnable runnable = new Runnable() { // from class: com.lge.launcher3.PagedView.5
                @Override // java.lang.Runnable
                public void run() {
                    PagedView.this.onEndReordering();
                }
            };
            this.mPostReorderingPreZoomInRunnable = new Runnable() { // from class: com.lge.launcher3.PagedView.6
                @Override // java.lang.Runnable
                public void run() {
                    runnable.run();
                }
            };
            this.mPostReorderingPreZoomInRemainingAnimationCount = this.NUM_ANIMATIONS_RUNNING_BEFORE_ZOOM_OUT;
            snapToPage(indexOfChild(this.mDragView), 0);
            animateDragViewToOriginalPosition();
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        boolean zIsPageOrderFlipped = isPageOrderFlipped();
        info.setScrollable(getPageCount() > 1);
        if (getCurrentPage() < getPageCount() - 1) {
            info.addAction(zIsPageOrderFlipped ? 8192 : 4096);
        }
        if (getCurrentPage() > 0) {
            info.addAction(zIsPageOrderFlipped ? 4096 : 8192);
        }
        info.setClassName(getClass().getName());
        info.setLongClickable(false);
        if (Utilities.isLmpOrAbove()) {
            info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
        }
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
        event.setScrollable(getPageCount() > 1);
    }

    private boolean accessibilityScrollLeft() {
        if (getCurrentPage() <= 0) {
            return false;
        }
        scrollLeft();
        return true;
    }

    private boolean accessibilityScrollRight() {
        if (getCurrentPage() >= getPageCount() - 1) {
            return false;
        }
        scrollRight();
        return true;
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }
        boolean zIsPageOrderFlipped = isPageOrderFlipped();
        if (action == 4096) {
            if (zIsPageOrderFlipped) {
                if (!accessibilityScrollLeft()) {
                    return false;
                }
            } else if (!accessibilityScrollRight()) {
                return false;
            }
            return true;
        }
        if (action != 8192) {
            return false;
        }
        if (zIsPageOrderFlipped) {
            if (!accessibilityScrollRight()) {
                return false;
            }
        } else if (!accessibilityScrollLeft()) {
            return false;
        }
        return true;
    }

    public String getCurrentPageDescription() {
        return String.format(getContext().getString(R.string.default_scroll_format), Integer.valueOf(getNextPage() + 1), Integer.valueOf(getChildCount()));
    }

    private void setupTouchSlopRatio() {
        Resources resources = getContext().getResources();
        TOUCH_SLOP_INIT_REACTION_DISTANCE = resources.getFloat(R.dimen.config_touchslop_init_distance);
        float f = resources.getDisplayMetrics().xdpi;
        int i = (int) ((TOUCH_SLOP_INIT_REACTION_DISTANCE * f) / TOUCH_SLOP_MM_PER_INCH);
        int integer = resources.getInteger(R.integer.config_dropTouchSlop);
        if (CustomUIManager.getInstance(getContext()).getScaledTouchSlop() != 0) {
            integer = CustomUIManager.getInstance(getContext()).getScaledTouchSlop();
        }
        this.mModifiedTouchSlop = (int) ((resources.getFloat(R.dimen.config_touchslop_modified_init_distance) * f) / TOUCH_SLOP_MM_PER_INCH);
        if (CustomUIManager.getInstance(getContext()).getTouchSlop() > 0) {
            this.mModifiedTouchSlop = CustomUIManager.getInstance(getContext()).getTouchSlop();
        }
        if (integer < i) {
            i = integer;
        }
        int i2 = this.mTouchSlop;
        float f2 = i < i2 ? i / i2 : 1.0f;
        sTouchSlopRatio = f2;
        LGLog.i(TAG, "setupTouchSlopRatio : " + i + ",  " + i2 + ", " + f2 + ", " + this.mModifiedTouchSlop + ", " + f);
    }

    public int getCurrentPagePublic() {
        return getCurrentPage();
    }

    public int getPageCountPublic() {
        return getPageCount();
    }

    public View getPageAtPublic(int index) {
        return getPageAt(index);
    }

    public void setPageIndicator(final PageIndicatorExtension pageIndicator) {
        pageIndicator.setContentDescription(getPageIndicatorDescription());
        pageIndicator.setListener(new PageIndicatorListener() { // from class: com.lge.launcher3.PagedView.7
            @Override // com.lge.launcher3.pageindicator.PageIndicatorListener
            public void onChangePage(int page) {
                if (page >= 0) {
                    PagedView.this.snapToPage(page);
                } else if (page == -401) {
                    PagedView.this.goToMinusOneScreen(true);
                }
            }
        });
    }

    public void afterAttachedToWindow() {
        if (this.mPageIndicator != null) {
            int dimension = (int) getResources().getDimension(R.dimen.device_profile_pageIndicator_padding);
            setPageIndicator((PageIndicatorExtension) this.mPageIndicator);
            ((PageIndicatorExtension) this.mPageIndicator).setTypePadding(dimension);
        }
        if (this.mOverviewPageIndicator != null) {
            int dimension2 = (int) getResources().getDimension(R.dimen.device_profile_pageIndicator_padding);
            setPageIndicator((PageIndicatorExtension) this.mOverviewPageIndicator);
            ((PageIndicatorExtension) this.mOverviewPageIndicator).setTypePadding(dimension2);
        }
    }

    public int getChildOffset(CellLayout cellLayout) {
        return getChildOffset(indexOfChild(cellLayout));
    }

    public int getMaxScrollX() {
        return this.mMaxScroll;
    }

    public Rect getViewport() {
        return this.mViewport;
    }

    public void setFullScreenPage(View page, boolean fullScreenEnabled, boolean isVerticalLayout) {
        LayoutParams layoutParams = (LayoutParams) page.getLayoutParams();
        layoutParams.isOverviewMode = !fullScreenEnabled;
        layoutParams.isVerticalLayout = isVerticalLayout;
        page.setLayoutParams(layoutParams);
    }

    public float getPredictionX(MotionEvent ev) {
        float fComputePredictionLocation = this.mTouchPrediction.computePredictionLocation(ev);
        if (fComputePredictionLocation < getViewportOffsetX()) {
            fComputePredictionLocation = getViewportOffsetX();
        }
        return fComputePredictionLocation > ((float) (getViewportOffsetX() + getViewportWidth())) ? getViewportOffsetX() + getViewportWidth() : fComputePredictionLocation;
    }

    public boolean isDistZeroChecked(int xDist) {
        return this.mScroller.isFinished() || xDist != 0 || Math.abs(this.mScroller.getFinalX() - this.mScroller.getStartX()) <= 0;
    }

    public int getPageSpacing() {
        return this.mPageSpacing;
    }

    public LauncherScroller getScroller() {
        return this.mScroller;
    }

    public boolean isScrolling() {
        return isTouchActive() || !this.mScroller.isFinished() || isPageInTransition();
    }

    public boolean isTouchActive() {
        return this.mTouchState != 0;
    }

    public int getTouchState() {
        return this.mTouchState;
    }

    public void superScrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public boolean superDrawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public void ignoreReorderingUpdatePageIndicator() {
        PageIndicator pageIndicator = this.mPageIndicator;
        if (pageIndicator != null) {
            pageIndicator.setContentDescription(getPageIndicatorDescription());
            this.mPageIndicator.setActiveMarker(getNextPage());
        }
    }

    public void updateFullScreenSizeAndLocation(Canvas canvas, View page) {
        LayoutParams layoutParams = (LayoutParams) page.getLayoutParams();
        if (layoutParams.isFullScreenPage) {
            int iIndexOfChild = indexOfChild(page);
            if (layoutParams.isOverviewMode) {
                View pageAt = getPageAt(iIndexOfChild + 1);
                int top = pageAt != null ? pageAt.getTop() : page.getTop();
                canvas.clipRect(getScrollX(), getScrollY(), (getScrollX() + getRight()) - getLeft(), pageAt != null ? pageAt.getBottom() : page.getBottom());
                page.setScaleX(pageAt.getWidth() / page.getWidth());
                if (layoutParams.isVerticalLayout) {
                    float width = pageAt.getWidth() / page.getWidth();
                    if (this.mIsRtl) {
                        page.setScrollX((int) ((((page.getWidth() - pageAt.getWidth()) / 2) + (getPaddingRight() - this.mPageSpacing)) / width));
                    } else {
                        page.setScrollX(((int) ((((page.getWidth() - pageAt.getWidth()) / 2) + (getPaddingLeft() - this.mPageSpacing)) / width)) * (-1));
                    }
                }
                page.setTranslationY(top - page.getTop());
                return;
            }
            page.setScaleX(1.0f);
            page.setScrollX(0);
            page.setTranslationY(0.0f);
        }
    }

    public void pageShiftForLoop(final int pageCount, LoopNormalModeManager loopNormalMngr, boolean isLoopNormalEnabled) {
        if (isLoopNormalEnabled && loopNormalMngr.isWorkspaceSpringLoadedState()) {
            if (getScrollX() >= this.mMaxScroll - (getViewportWidth() / 2)) {
                if (getScrollX() >= this.mMaxScroll + (getViewportWidth() / 2)) {
                    loopNormalMngr.pageShiftForLoopOnSpringLoaded(this, this.mIsRtl ? LoopNormalModeManager.PageShiftDirection.SHIFT_TO_HEAD : LoopNormalModeManager.PageShiftDirection.SHIFT_TO_TAIL, true);
                    return;
                } else {
                    loopNormalMngr.pageShiftForLoopOnSpringLoaded(this, this.mIsRtl ? LoopNormalModeManager.PageShiftDirection.SHIFT_TO_HEAD : LoopNormalModeManager.PageShiftDirection.SHIFT_TO_TAIL, false);
                    return;
                }
            }
            if (getScrollX() <= getViewportWidth() / 2) {
                if (getScrollX() <= (getViewportWidth() / 2) * (-1)) {
                    loopNormalMngr.pageShiftForLoopOnSpringLoaded(this, this.mIsRtl ? LoopNormalModeManager.PageShiftDirection.SHIFT_TO_TAIL : LoopNormalModeManager.PageShiftDirection.SHIFT_TO_HEAD, true);
                    return;
                } else {
                    loopNormalMngr.pageShiftForLoopOnSpringLoaded(this, this.mIsRtl ? LoopNormalModeManager.PageShiftDirection.SHIFT_TO_TAIL : LoopNormalModeManager.PageShiftDirection.SHIFT_TO_HEAD, false);
                    return;
                }
            }
            loopNormalMngr.pageShiftForLoopOnSpringLoaded(this, LoopNormalModeManager.PageShiftDirection.SHIFT_NONE, true);
            return;
        }
        loopNormalMngr.pageShiftForLoopOnSpringLoaded(this, LoopNormalModeManager.PageShiftDirection.SHIFT_NONE, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMoveToItemsInCurrentScreen(float DeltaY) {
        if (this.mCheckInapps || this.mCheckSwipeUpAppDrawer) {
            return;
        }
        Launcher launcher = (Launcher) getContext();
        if (getChildAt(getCurrentPage()) != null) {
            launcher.getDragLayer().setScrollY(-((int) DeltaY));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMoveToItemsFromAllapps(float DeltaY) {
        if (this.mCheckInapps || this.mCheckSwipeUpAppDrawer) {
            return;
        }
        Launcher launcher = (Launcher) getContext();
        if (getChildAt(getCurrentPage()) != null) {
            launcher.getWorkspace().setScrollY(-((int) DeltaY));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetScrollAlphaByInApps() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt != null && (childAt.getScrollY() != 0 || Float.compare(childAt.getAlpha(), 1.0f) != 0)) {
                childAt.setAlpha(1.0f);
                childAt.setScrollY(0);
            }
        }
    }

    private boolean checkEnteringInAppsConditionMove(float y) {
        return ((int) y) > this.mMinMoveForInApps + this.mTouchSlop;
    }

    private boolean checkEnteringSwipeDownCondition(float y) {
        return ((int) y) > this.mMinMoveForInApps - this.mTouchSlop;
    }

    public void startExitSwipeUpDownAnimation(float alpha, float deltaY) {
        this.mCheckExitAnimationFinish = true;
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(alpha, 0.0f);
        valueAnimatorOfFloat.setDuration(250L);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.PagedView.8
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                PagedView.this.setAlphaToItemsInCurrentScreen(1.0f - ((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.PagedView.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PagedView.this.resetScrollAlphaByInApps();
                PagedView.this.mCheckExitAnimationFinish = false;
            }
        });
        valueAnimatorOfFloat.start();
        ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(deltaY, 0.0f);
        valueAnimatorOfFloat2.setDuration(150L);
        valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.PagedView.10
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                PagedView.this.setMoveToItemsInCurrentScreen(((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat2.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.PagedView.11
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PagedView.this.mCheckExitAnimationFinish = false;
            }
        });
        valueAnimatorOfFloat2.start();
    }

    public void startEnterSwipeUpAnimation(float alpha, float deltaY) {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(alpha, 1.0f);
        valueAnimatorOfFloat.setDuration(150L);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.PagedView.12
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                PagedView.this.setAlphaToItemsInCurrentScreen(1.0f - ((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.PagedView.13
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PagedView.this.mCheckAppDrawerAnimationFinish = false;
            }
        });
        valueAnimatorOfFloat.start();
        ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(deltaY, this.mMinMoveForSwipeUpAppDrawer);
        valueAnimatorOfFloat2.setDuration(150L);
        valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.PagedView.14
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                PagedView.this.setMoveToItemsInCurrentScreen(((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat2.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.PagedView.15
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PagedView.this.resetScrollAlphaByInApps();
            }
        });
        valueAnimatorOfFloat2.start();
    }

    public void startBackInAppsAnimation(float alpha, float deltaY) {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(alpha, 0.0f);
        valueAnimatorOfFloat.setDuration(250L);
        valueAnimatorOfFloat.setInterpolator(new DecelerateInterpolator());
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.PagedView.16
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                PagedView.this.setAlphaToItemsInCurrentScreen(1.0f - ((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.PagedView.17
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ((Launcher) PagedView.this.getContext()).showWorkspace(false);
                PagedView.this.mCheckAppDrawerAnimationFinish = false;
            }
        });
        valueAnimatorOfFloat.start();
        ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(deltaY, 0.0f);
        valueAnimatorOfFloat2.setInterpolator(new DecelerateInterpolator());
        valueAnimatorOfFloat2.setDuration(250L);
        valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.PagedView.18
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                PagedView.this.setMoveToItemsFromAllapps(((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat2.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.PagedView.19
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PagedView.this.resetScrollAlphaByInApps();
                PagedView.this.mCheckAppDrawerAnimationFinish = false;
            }
        });
        valueAnimatorOfFloat2.start();
    }

    private void backInApps() {
        LGLog.i(TAG, "backInApps");
        HomescreenBlurManager homescreenBlurManager = HomescreenBlurManager.getInstance(getContext());
        this.mCheckAppDrawerAnimationFinish = true;
        startBackInAppsAnimation(this.mInAppsalpha, this.mInAppsDeltaY);
        homescreenBlurManager.hideBackground(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER, 150);
        resetTouchState();
        this.mBlurBackgroundView = null;
        this.mInAppsalpha = 1.0f;
        this.mInAppsDeltaY = 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAlphaToItemsInCurrentScreen(float alpha) {
        Launcher launcher = (Launcher) getContext();
        float f = (float) ((((double) alpha) * 0.05d) + 0.949999988079071d);
        if (f > 1.0f) {
            f = 1.0f;
        }
        if (launcher instanceof Launcher) {
            SearchDropTargetBar searchBar = launcher.getSearchBar();
            View childAt = getChildAt(getCurrentPage());
            Hotseat hotseat = launcher.getHotseat();
            PageIndicator pageIndicator = getPageIndicator();
            View viewFindViewById = launcher.findViewById(R.id.swipeup_arrow);
            View viewFindViewById2 = launcher.findViewById(R.id.swipeup_guide_text);
            if (launcher.getDragLayer() != null && !this.mCheckAppDrawerAnimationFinish) {
                launcher.getDragLayer().setScaleX(f);
                launcher.getDragLayer().setScaleY(f);
            }
            if (searchBar != null) {
                if (this.mCheckInapps || this.mCheckSwipeUpAppDrawer) {
                    searchBar.setAlpha(0.0f);
                } else {
                    searchBar.setAlpha(alpha);
                }
            }
            if (childAt != null) {
                if (this.mCheckInapps || this.mCheckSwipeUpAppDrawer) {
                    childAt.setAlpha(0.0f);
                } else {
                    childAt.setAlpha(alpha);
                }
            }
            if (hotseat != null) {
                if (this.mCheckInapps || this.mCheckSwipeUpAppDrawer) {
                    hotseat.setAlpha(0.0f);
                } else {
                    hotseat.setAlpha(alpha);
                }
            }
            if (pageIndicator != null && !SwipeUpGuideAnimation.isInSwipUpGuideAnination()) {
                if (this.mCheckInapps || this.mCheckSwipeUpAppDrawer) {
                    pageIndicator.setAlpha(0.0f);
                } else {
                    pageIndicator.setAlpha(alpha);
                }
            }
            if (viewFindViewById != null) {
                if (this.mCheckInapps || this.mCheckSwipeUpAppDrawer) {
                    viewFindViewById.setAlpha(0.0f);
                } else {
                    viewFindViewById.setAlpha(alpha);
                }
            }
            if (viewFindViewById2 != null) {
                if (this.mCheckInapps || this.mCheckSwipeUpAppDrawer) {
                    viewFindViewById2.setAlpha(0.0f);
                } else {
                    viewFindViewById2.setAlpha(alpha);
                }
            }
        }
    }

    public boolean checkCognizingInAppsCondition(boolean xMoved, boolean yMoved, float deltaX, float deltaY) {
        int i;
        Launcher launcher = (Launcher) getContext();
        if (launcher.getWorkspace().getOpenFolder() == null && (i = this.mTouchState) != 6 && i != 1 && ((i == 5 || ((launcher instanceof Launcher) && launcher.isInState(LauncherState.NORMAL) && launcher.getWorkspace().getState() == Workspace.State.NORMAL && launcher.getWorkspace().getScreenIdForPageIndex(getCurrentPage()) != -301 && yMoved && deltaY > 0.0f)) && this.mTouchState != 6 && !this.mCheckInapps)) {
            float f = deltaY - this.mTouchSlop;
            this.mInAppsDeltaY = f;
            if (f < 0.0f) {
                this.mInAppsDeltaY = 0.0f;
            }
            setMoveToItemsInCurrentScreen(this.mInAppsDeltaY / 2.0f);
            float f2 = (deltaY - this.mTouchSlop) / this.mMinMoveForInApps;
            this.mInAppsalpha = f2;
            if (f2 < 0.0f) {
                this.mInAppsalpha = 0.0f;
            } else if (f2 > 1.0f) {
                this.mInAppsalpha = 1.0f;
            }
            setAlphaToItemsInCurrentScreen(1.0f - this.mInAppsalpha);
            setAlphaForInApps(this.mInAppsalpha);
            this.mTouchState = 5;
            cancelCurrentPageLongPress();
            launcher.cancelWorkspaceLongpress();
            if (checkEnteringInAppsConditionMove(deltaY)) {
                LGLog.i(TAG, "checkCognizingInAppsCondition is True");
                setMoveToItemsInCurrentScreen(0.0f);
                resetDragLayerScaleratio();
                boolean zEnterInApps = enterInApps();
                this.mCheckInapps = zEnterInApps;
                return zEnterInApps;
            }
        }
        return false;
    }

    private boolean checkAngleCondition(float deltaX, float deltaY) {
        return ((float) Math.atan((double) (deltaX / deltaY))) < MAX_ANGLE_FOR_INAPPS;
    }

    private boolean checkEnteringInAppsCondition(float y) {
        boolean z = ((int) (y - this.mDownMotionY)) > this.mMinMoveForInApps + this.mTouchSlop;
        if (!z) {
            exitInApps();
            snapToDestination();
        }
        return z;
    }

    private void setAlphaForInApps(float alpha) {
        HomescreenBlurManager homescreenBlurManager = HomescreenBlurManager.getInstance(getContext());
        if (homescreenBlurManager.isLiveWallpaperMode()) {
            setAlphaToItemsInCurrentScreen(1.0f - alpha);
            return;
        }
        boolean value = LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue();
        if (StaticBlurEngine.getInstance().isPowerSaveEnabled(getContext())) {
            value = false;
        }
        if (this.mBlurBackgroundView == null) {
            homescreenBlurManager.showBackgroundWithNoAnim(HomescreenBlurManager.BackgroundType.BOTTOM_ROOTVIEW);
            if (!value) {
                homescreenBlurManager.updateBackgroundViewContents();
            } else {
                LGLog.i(TAG, String.format("Alpha Blur Thredhold %f", Float.valueOf(0.3f)));
                homescreenBlurManager.updateBackgroundViewContents(0.3f);
            }
            View backgroundView = homescreenBlurManager.getBackgroundView();
            this.mBlurBackgroundView = backgroundView;
            if (backgroundView != null) {
                backgroundView.setVisibility(0);
            }
        }
        View view = this.mBlurBackgroundView;
        if (view != null) {
            if (!value) {
                view.setAlpha(alpha);
            } else if (alpha <= 0.3f) {
                view.setAlpha(alpha * 3.3333333f);
            } else {
                view.setAlpha(1.0f);
                homescreenBlurManager.updateBackgroundViewContents(alpha);
            }
        }
    }

    private boolean enterInApps() {
        ApplicationInfo applicationInfo;
        boolean z;
        LGLog.i(TAG, "enterInApps");
        try {
            applicationInfo = getContext().getPackageManager().getApplicationInfo(LauncherConst.GOOGLE_SEARCH_WIDGET_PACKAGENAME, 0);
            z = true;
        } catch (PackageManager.NameNotFoundException unused) {
            applicationInfo = null;
            z = false;
        }
        if (applicationInfo == null || !applicationInfo.enabled) {
            z = false;
        }
        if (!z) {
            Toast.makeText(getContext(), R.string.gsa_error_gesture, 0).show();
            exitInApps();
            snapToDestination();
            return false;
        }
        Intent intent = new Intent("com.google.android.googlequicksearchbox.SEARCH_GESTURE");
        intent.putExtra("search_within_corpus", "phone");
        intent.putExtra("android.intent.extra.TEXT", "");
        try {
            Launcher launcher = (Launcher) getContext();
            startActivityForResult(intent, LauncherConst.REQUEST_EXECUTE_INAPPS);
            if (launcher instanceof Launcher) {
                launcher.overridePendingTransition(R.anim.enter_inapps, 0);
            }
            boolean value = LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue();
            boolean zIsPowerSaveEnabled = StaticBlurEngine.getInstance().isPowerSaveEnabled(getContext());
            if (value && !zIsPowerSaveEnabled) {
                HomescreenBlurManager homescreenBlurManager = HomescreenBlurManager.getInstance(getContext());
                if (StaticBlurEngine.getInstance().needRTBlurMaxRadius()) {
                    homescreenBlurManager.updateBackgroundViewContents(1.0f);
                } else {
                    homescreenBlurManager.updateBackgroundViewContents();
                }
            }
            return true;
        } catch (Exception e) {
            LGLog.e(TAG, "Error in executing inapps:" + e.getMessage());
            this.mCheckInapps = false;
            exitInApps();
            snapToDestination();
            return false;
        }
    }

    public void exitInApps() {
        LGLog.i(TAG, "exitInApps");
        startExitSwipeUpDownAnimation(this.mInAppsalpha, this.mInAppsDeltaY / 2.0f);
        HomescreenBlurManager.getInstance(getContext()).hideBackgroundWithNoAnim(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER);
        this.mBlurBackgroundView = null;
        resetTouchState();
        this.mInAppsalpha = 1.0f;
        this.mInAppsDeltaY = 0.0f;
    }

    public void exitInAppsWithoutAni() {
        LGLog.i(TAG, "exitInAppsWithoutAni");
        setAlphaToItemsInCurrentScreen(1.0f);
        setMoveToItemsInCurrentScreen(0.0f);
        resetScrollAlphaByInApps();
        HomescreenBlurManager.getInstance(getContext()).hideBackgroundWithNoAnim(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER);
        this.mBlurBackgroundView = null;
    }

    public void setInAppsEnabled(boolean enabled) {
        this.mIsInAppsEnabled = enabled;
    }

    public boolean getInAppsEnabled() {
        return this.mIsInAppsEnabled;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1115) {
            backInApps();
            this.mCheckInapps = false;
        } else if (requestCode == 1116) {
            backToWorkspaceFromSwipeUpAppDrawer(true);
            this.mCheckSwipeUpAppDrawer = false;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setActivePointerIdToInvalid() {
        this.mActivePointerId = -1;
        this.mTouchState = 0;
    }

    public boolean getCheckInappsValue() {
        return this.mCheckInapps;
    }

    public boolean getSwipeUpAppDrawerEnable() {
        return !LGHomeFeature.isDisableAllApps() && LGHomeFeature.Config.FEATURE_SWIPEUP_APPDRAWER.getValue();
    }

    public boolean checkCognizingSwipeUpAppDrawerCondition(boolean xMoved, boolean yMoved, float deltaX, float deltaY) {
        int i;
        Launcher launcher = (Launcher) getContext();
        if (launcher.getWorkspace().getOpenFolder() == null && (i = this.mTouchState) != 5 && i != 1) {
            if (i == 6 || ((launcher instanceof Launcher) && launcher.isInState(LauncherState.NORMAL) && launcher.getWorkspace().getState() == Workspace.State.NORMAL && launcher.getWorkspace().getScreenIdForPageIndex(getCurrentPage()) != -301 && yMoved && deltaY < 0.0f)) {
                if (!this.mCheckSwipeUpAppDrawer) {
                    float f = this.mTouchSlop + deltaY;
                    this.mSwipeUpAppDrawerDeltaY = f;
                    if (f > 0.0f) {
                        this.mSwipeUpAppDrawerDeltaY = 0.0f;
                    }
                    setMoveToItemsInCurrentScreen(this.mSwipeUpAppDrawerDeltaY / 2.0f);
                    float f2 = ((-deltaY) - this.mTouchSlop) / this.mMinMoveForSwipeUpAppDrawer;
                    this.mSwipeUpAppDrawerAlpha = f2;
                    if (f2 < 0.0f) {
                        this.mSwipeUpAppDrawerAlpha = 0.0f;
                    } else if (f2 > 1.0f) {
                        this.mSwipeUpAppDrawerAlpha = 1.0f;
                    }
                    setAlphaToItemsInCurrentScreen(1.0f - this.mSwipeUpAppDrawerAlpha);
                    setAlphaForInApps(this.mSwipeUpAppDrawerAlpha);
                    this.mTouchState = 6;
                    cancelCurrentPageLongPress();
                    launcher.cancelWorkspaceLongpress();
                    if (checkEnteringSwipeUpAppDrawerMove(deltaY) && !this.mCheckInapps) {
                        LGLog.i(TAG, "Swipe AppDrawer : checkEnteringSwipeUpAppDrawerMove is True");
                        setMoveToItemsInCurrentScreen(0.0f);
                        resetDragLayerScaleratio();
                        launcher.showAllAppsView(true, false, true, false);
                        this.mCheckSwipeUpAppDrawer = true;
                        updateSwipeUpCount();
                        resetTouchState();
                        return true;
                    }
                }
            } else {
                if (!launcher.isAllAppsVisible() || launcher.getAllAppsHost().isInArrangeMode() || !yMoved || deltaY <= 0.0f || !checkEnteringSwipeUpAppDrawerMove((-deltaY) * 1.5f)) {
                    return false;
                }
                launcher.showWorkspace(true);
                resetTouchState();
                return true;
            }
        }
        return false;
    }

    private void resetDragLayerScaleratio() {
        Launcher launcher = (Launcher) getContext();
        launcher.getDragLayer().setScaleX(1.0f);
        launcher.getDragLayer().setScaleY(1.0f);
    }

    public void updateSwipeUpCount() {
        int integer = getContext().getResources().getInteger(R.integer.config_swipe_up_count);
        int i = SharedPreferencesManager.getInt(getContext(), 0, SharedPreferencesConst.SwipeUpKey.SWIPE_UP_COUNT, 0) + 1;
        if (i >= integer) {
            SharedPreferencesManager.putBoolean(getContext(), 0, SharedPreferencesConst.SwipeUpKey.IS_ENABLED, false);
        } else {
            SharedPreferencesManager.putInt(getContext(), 0, SharedPreferencesConst.SwipeUpKey.SWIPE_UP_COUNT, i);
        }
    }

    public void setCheckSwipeUpAppDrawer(boolean check) {
        this.mCheckSwipeUpAppDrawer = check;
    }

    public Boolean getCheckSwipeUpAppDrawer() {
        return Boolean.valueOf(this.mCheckSwipeUpAppDrawer);
    }

    public Boolean getCheckSwipeDownInAppDrawer() {
        return Boolean.valueOf(this.mCheckSwipeDownAppDrawer);
    }

    public Boolean getCheckAppDrawerAnimationFinished() {
        return Boolean.valueOf(this.mCheckAppDrawerAnimationFinish);
    }

    public Boolean getCheckExitAnimationFinished() {
        return Boolean.valueOf(this.mCheckExitAnimationFinish);
    }

    public void setCheckSwipeDownInAppDrawer(boolean check) {
        this.mCheckSwipeDownAppDrawer = check;
    }

    private boolean checkSwipeUpAppDrawerAngleCondition(float deltaX, float deltaY) {
        float fAtan = (float) Math.atan(deltaX / Math.abs(deltaY));
        LGLog.i(TAG, "checkSwipeUpAppDrawerAngleCondition : theta = " + fAtan);
        return fAtan < MAX_ANGLE_FOR_SWIPEUP_APPDRAWER;
    }

    public boolean checkEnteringSwipeUpAppDrawer(float y) {
        boolean z = Math.abs((int) (y - this.mDownMotionY)) > this.mMinMoveForInApps + this.mTouchSlop;
        if (!z) {
            exitSwipeUpAppDrawer();
            snapToDestination();
        }
        return z;
    }

    private boolean checkEnteringSwipeUpAppDrawerMove(float y) {
        return (-((int) y)) > this.mMinMoveForSwipeUpAppDrawer + this.mTouchSlop;
    }

    public void backToWorkspaceFromSwipeUpAppDrawer(Boolean animation) {
        LGLog.i(TAG, "Swipe AppDrawer : backToWorkspaceFromSwipeUpAppDrawer");
        HomescreenBlurManager homescreenBlurManager = HomescreenBlurManager.getInstance(getContext());
        if (animation.booleanValue()) {
            this.mCheckAppDrawerAnimationFinish = true;
            startBackInAppsAnimation(this.mSwipeUpAppDrawerAlpha, (-this.mMinMoveForSwipeUpAppDrawer) / 2);
        } else {
            setAlphaToItemsInCurrentScreen(1.0f);
            setMoveToItemsInCurrentScreen(0.0f);
        }
        homescreenBlurManager.hideBackground(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER, 150);
        this.mBlurBackgroundView = null;
        this.mSwipeUpAppDrawerAlpha = 1.0f;
        this.mSwipeUpAppDrawerDeltaY = 0.0f;
    }

    public void exitSwipeUpAppDrawer() {
        LGLog.i(TAG, "Swipe AppDrawer : exitSwipeUpAppDrawer");
        startExitSwipeUpDownAnimation(this.mSwipeUpAppDrawerAlpha, this.mSwipeUpAppDrawerDeltaY / 2.0f);
        HomescreenBlurManager.getInstance(getContext()).hideBackgroundWithNoAnim(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER);
        this.mBlurBackgroundView = null;
        resetTouchState();
        this.mSwipeUpAppDrawerAlpha = 1.0f;
        this.mSwipeUpAppDrawerDeltaY = 0.0f;
    }

    public void resetAppFlashPageIndicator(boolean isEnable) {
        PageIndicator pageIndicator = this.mPageIndicator;
        if (pageIndicator != null && (pageIndicator instanceof PageIndicatorExtension) && VZWSideScreenManager.isAvailable()) {
            if (isEnable) {
                ((PageIndicatorExtension) this.mPageIndicator).addVZWSideScreenMarker();
            } else {
                ((PageIndicatorExtension) this.mPageIndicator).removeVZWSideScreenMarker();
            }
        }
    }

    public void resetGoogleNowPageIndicator(boolean isEnable) {
        PageIndicator pageIndicator = this.mPageIndicator;
        if (pageIndicator == null || !(pageIndicator instanceof PageIndicatorExtension)) {
            return;
        }
        ((PageIndicatorExtension) pageIndicator).resetIsGoogleNowEnabled();
        if (!GoogleNowManager.isAvailable(getContext())) {
            ((PageIndicatorExtension) this.mPageIndicator).removeGoogleNowMarker();
        } else if (isEnable) {
            ((PageIndicatorExtension) this.mPageIndicator).addGoogleNowMarker();
        } else {
            ((PageIndicatorExtension) this.mPageIndicator).removeGoogleNowMarker();
        }
    }

    public int getTouchSlop() {
        return this.mTouchSlop;
    }

    public float getBlurBackgroundAlpha() {
        View view = this.mBlurBackgroundView;
        if (view != null) {
            return view.getAlpha();
        }
        return 0.0f;
    }

    public static boolean IsOutOfTouchSlop() {
        return mIsOutOfTouchSlop;
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
