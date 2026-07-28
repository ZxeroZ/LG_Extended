package com.lge.launcher3.screeneffect;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import com.android.launcher3.CellLayout;
import com.android.launcher3.LauncherScroller;
import com.android.launcher3.Utilities;
import com.lge.launcher3.PagedView;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.screeneffect.interpolator.ScreenEffectInterpolatorSpring;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectTargetManager {
    public static final boolean DEBUG = false;
    public static final String TAG = "ScreenEffectTargetManager";
    private static ScreenEffectTargetManager sInstance;
    private boolean mIsRtl;
    protected ViewGroup mParent = null;
    protected TargetInfo mTargetInfo = new TargetInfo();
    private int mPrevHeadPageScroll = 0;
    private int mNextTailPageScroll = 0;
    private int mMaxScrollForLoop = 0;
    private int mDeltalForLoop = 0;

    public int indexOfHead() {
        return 0;
    }

    public static ScreenEffectTargetManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ScreenEffectTargetManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public void reset() {
        this.mPrevHeadPageScroll = 0;
        this.mNextTailPageScroll = 0;
        this.mMaxScrollForLoop = 0;
        this.mDeltalForLoop = 0;
    }

    protected ScreenEffectTargetManager(Context context) {
        this.mIsRtl = false;
        this.mIsRtl = Utilities.isRtl(context.getResources());
    }

    public void setParent(ViewGroup parent) {
        this.mParent = parent;
    }

    public ViewGroup getParent() {
        return this.mParent;
    }

    public void updatePageScrollsForLoop() {
        int iIndexOfHead = indexOfHead();
        int iIndexOfTail = indexOfTail();
        boolean zIsFullscreen = isFullscreen(getChildAt(iIndexOfHead));
        boolean zIsOverview = isOverview(getChildAt(iIndexOfHead));
        boolean zIsVerticalLayout = isVerticalLayout(getChildAt(iIndexOfHead));
        int childMeasuredWidth = getChildMeasuredWidth(iIndexOfHead);
        int childMeasuredWidth2 = getChildMeasuredWidth(iIndexOfTail);
        int pageSpacing = getPageSpacing();
        int i = !this.mIsRtl ? -1 : 1;
        this.mPrevHeadPageScroll = getScrollForPage(iIndexOfHead) + ((zIsFullscreen ? childMeasuredWidth : childMeasuredWidth + pageSpacing) * i);
        this.mNextTailPageScroll = getScrollForPage(iIndexOfTail) + (i * (-1) * (zIsFullscreen ? childMeasuredWidth : childMeasuredWidth2 + pageSpacing));
        if (zIsFullscreen && zIsOverview && zIsVerticalLayout) {
            this.mDeltalForLoop = (childMeasuredWidth - (childMeasuredWidth2 + pageSpacing)) * (-1) * i;
        } else {
            this.mDeltalForLoop = 0;
        }
    }

    public void updateMaxScroll() {
        int iIndexOfHead = indexOfHead();
        int iIndexOfTail = indexOfTail();
        boolean zIsFullscreen = isFullscreen(getChildAt(iIndexOfHead));
        boolean zIsOverview = isOverview(getChildAt(iIndexOfHead));
        boolean zIsVerticalLayout = isVerticalLayout(getChildAt(iIndexOfHead));
        int childMeasuredWidth = getChildMeasuredWidth(iIndexOfHead);
        int childMeasuredWidth2 = getChildMeasuredWidth(iIndexOfTail);
        int maxScroll = getMaxScroll();
        this.mMaxScrollForLoop = maxScroll;
        if (zIsFullscreen && (!zIsOverview || !zIsVerticalLayout)) {
            this.mMaxScrollForLoop = maxScroll + childMeasuredWidth;
            return;
        }
        if (!this.mIsRtl) {
            childMeasuredWidth = childMeasuredWidth2;
        }
        this.mMaxScrollForLoop = maxScroll + childMeasuredWidth + getPageSpacing();
    }

    public TargetInfo getTargetInfo(View child) {
        this.mTargetInfo.setChild(child);
        return this.mTargetInfo;
    }

    public void setScrollX(int scrollX) {
        this.mTargetInfo.setScrollX(scrollX);
    }

    public int getScrollForPageLoop(int index) {
        int iValidatePageIndexForLoop = validatePageIndexForLoop(index);
        if (iValidatePageIndexForLoop < indexOfHead()) {
            return this.mPrevHeadPageScroll + this.mDeltalForLoop;
        }
        if (indexOfTail() < iValidatePageIndexForLoop) {
            return this.mNextTailPageScroll - this.mDeltalForLoop;
        }
        int scrollForPage = getScrollForPage(iValidatePageIndexForLoop);
        if (iValidatePageIndexForLoop == indexOfHead() && (this.mParent instanceof PagedView)) {
            boolean zIsFullscreen = isFullscreen(getChildAt(iValidatePageIndexForLoop));
            boolean zIsOverview = isOverview(getChildAt(iValidatePageIndexForLoop));
            boolean zIsVerticalLayout = isVerticalLayout(getChildAt(iValidatePageIndexForLoop));
            if (zIsFullscreen && zIsOverview && zIsVerticalLayout) {
                return this.mDeltalForLoop + scrollForPage;
            }
        }
        return scrollForPage;
    }

    public int getNearestScrollForPageLoop(int index) {
        return (getChildAt(index) == null || getChildAt(index).getTranslationX() <= 0.0f) ? (getChildAt(index) == null || getChildAt(index).getTranslationX() >= 0.0f) ? index : this.mIsRtl ? indexOfTail() + 1 : indexOfHead() - 1 : this.mIsRtl ? indexOfHead() - 1 : indexOfTail() + 1;
    }

    public int getScrollForPage(int index) {
        ViewGroup viewGroup = this.mParent;
        if (viewGroup instanceof PagedView) {
            return ((PagedView) viewGroup).getScrollForPage(index);
        }
        return 0;
    }

    public int validatePageIndexForLoop(int index) {
        if (hasOneChild()) {
            return index;
        }
        int iIndexOfHead = indexOfHead();
        int iIndexOfTail = indexOfTail();
        return (index < iIndexOfHead || iIndexOfTail < index) ? index : (isHeadToTail() && isTailPage(index)) ? iIndexOfHead - 1 : (isTailToHead() && isHeadPage(index)) ? iIndexOfTail + 1 : index;
    }

    public int[] getScrollDeltaAndRange(int index) {
        int scrollForPageLoop = this.mTargetInfo.scrollX - getScrollForPageLoop(index);
        int i = index + 1;
        if ((scrollForPageLoop < 0 && !this.mIsRtl) || (scrollForPageLoop > 0 && this.mIsRtl)) {
            i = index - 1;
        }
        return new int[]{scrollForPageLoop, getScrollForPageLoop(i) - getScrollForPageLoop(index)};
    }

    public int indexOfChild(View child) {
        ViewGroup viewGroup = this.mParent;
        if (viewGroup != null) {
            return viewGroup.indexOfChild(child);
        }
        return -1;
    }

    public int indexOfPrevChild(View child) {
        if (hasOneChild()) {
            return -1;
        }
        if (isHeadPage(child)) {
            return indexOfTail();
        }
        return indexOfChild(child) - 1;
    }

    public int indexOfNextChild(View child) {
        if (hasOneChild()) {
            return -1;
        }
        if (isTailPage(child)) {
            return indexOfHead();
        }
        return indexOfChild(child) + 1;
    }

    public int indexOfTail() {
        return getChildCount() - 1;
    }

    public int indexOfTail(PagedView pagedView) {
        return getChildCount(pagedView) - 1;
    }

    public View getChildAt(int index) {
        ViewGroup viewGroup = this.mParent;
        if (viewGroup != null) {
            return viewGroup.getChildAt(index);
        }
        return null;
    }

    public int getChildMeasuredWidth(int index) {
        View childAt = getChildAt(index);
        if (childAt == null) {
            return 0;
        }
        return childAt.getMeasuredWidth();
    }

    public int getPageSpacing() {
        ViewGroup viewGroup = this.mParent;
        if (viewGroup instanceof PagedView) {
            return ((PagedView) viewGroup).getPageSpacing();
        }
        return 0;
    }

    public PointF getParentPivot(View child, PointF recycle) {
        if (recycle == null) {
            recycle = new PointF();
        }
        recycle.set(0.0f, 0.0f);
        if (this.mParent instanceof PagedView) {
            recycle.x = (-r0.getLeft()) + (!isFullscreen(child) ? this.mParent.getPaddingLeft() : 0);
            recycle.y = (-this.mParent.getTop()) + this.mParent.getPaddingTop();
        }
        return recycle;
    }

    public int getChildCount() {
        ViewGroup viewGroup = this.mParent;
        if (viewGroup != null) {
            return viewGroup.getChildCount();
        }
        return 0;
    }

    public int getChildCount(PagedView pagedView) {
        return pagedView.getChildCount();
    }

    public boolean hasOneChild() {
        return getChildCount() <= 1;
    }

    public int getMaxScroll() {
        ViewGroup viewGroup = this.mParent;
        if (viewGroup instanceof PagedView) {
            return ((PagedView) viewGroup).getMaxScrollX();
        }
        return 0;
    }

    public int getMaxScrollForLoop() {
        return this.mMaxScrollForLoop;
    }

    public void setDefaultInterpolator(Interpolator interpolator) {
        ViewGroup viewGroup = this.mParent;
        if (viewGroup instanceof PagedView) {
            ((PagedView) viewGroup).setDefaultInterpolator(interpolator);
        }
    }

    public LayoutTransition getLayoutTransition() {
        ViewGroup viewGroup = this.mParent;
        if (viewGroup instanceof PagedView) {
            return ((PagedView) viewGroup).getLayoutTransition();
        }
        return null;
    }

    public boolean isHeadPage(View child) {
        return indexOfChild(child) == indexOfHead();
    }

    public boolean isHeadPage(int index) {
        return index == indexOfHead();
    }

    public boolean isTailPage(View child) {
        return indexOfChild(child) == indexOfTail();
    }

    public boolean isTailPage(int index) {
        return index == indexOfTail();
    }

    public boolean isOverscrollLeft() {
        return this.mTargetInfo.overscrollState == ScreenEffectConst.OverscrollState.OVERSCROLL_LEFT;
    }

    public boolean isOverscrollRight() {
        return this.mTargetInfo.overscrollState == ScreenEffectConst.OverscrollState.OVERSCROLL_RIGHT;
    }

    public boolean isHeadToTail() {
        return !this.mIsRtl ? isOverscrollLeft() : isOverscrollRight();
    }

    public boolean isTailToHead() {
        return !this.mIsRtl ? isOverscrollRight() : isOverscrollLeft();
    }

    public boolean isHeadToTailScrollOver(int scrollX) {
        if (this.mIsRtl) {
            if (scrollX >= this.mPrevHeadPageScroll + (this.mDeltalForLoop * 2)) {
                return true;
            }
        } else if (scrollX <= this.mPrevHeadPageScroll + (this.mDeltalForLoop * 2)) {
            return true;
        }
        return false;
    }

    public boolean isTailToHeadScrollOver(int scrollX) {
        if (this.mIsRtl) {
            if (scrollX <= this.mNextTailPageScroll - this.mDeltalForLoop) {
                return true;
            }
        } else if (scrollX >= this.mNextTailPageScroll - this.mDeltalForLoop) {
            return true;
        }
        return false;
    }

    public static boolean isFullscreen(View child) {
        PagedView.LayoutParams layoutParams;
        if (child == null || (layoutParams = (PagedView.LayoutParams) child.getLayoutParams()) == null) {
            return false;
        }
        return layoutParams.isFullScreenPage;
    }

    public static boolean isOverview(View child) {
        PagedView.LayoutParams layoutParams;
        if (child == null || (layoutParams = (PagedView.LayoutParams) child.getLayoutParams()) == null) {
            return false;
        }
        return layoutParams.isOverviewMode;
    }

    public static boolean isVerticalLayout(View child) {
        PagedView.LayoutParams layoutParams;
        if (child == null || (layoutParams = (PagedView.LayoutParams) child.getLayoutParams()) == null) {
            return false;
        }
        return layoutParams.isVerticalLayout;
    }

    public void updateAllPagesToOpaque() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            updatePageToOpaque(getChildAt(i));
        }
    }

    public void updatePageToOpaque(View child) {
        if (child == null) {
            return;
        }
        if (child instanceof CellLayout) {
            ((CellLayout) child).setShortcutAndWidgetAlpha(1.0f);
        } else {
            child.setAlpha(1.0f);
        }
    }

    public void updatePageToWantedAlpha(View child, float alpha) {
        if (child == null) {
            return;
        }
        if (child instanceof CellLayout) {
            ((CellLayout) child).setShortcutAndWidgetAlpha(alpha);
        } else {
            child.setAlpha(alpha);
        }
    }

    public class TargetInfo {
        private View mChild = null;
        public int scrollX = 0;
        public ScreenEffectConst.ScrollDirection scrollDirection = ScreenEffectConst.ScrollDirection.NONE;
        public ScreenEffectConst.OverscrollState overscrollState = ScreenEffectConst.OverscrollState.NONE;
        public ScreenEffectConst.WhichPageToDraw whichPageToDraw = ScreenEffectConst.WhichPageToDraw.NONE;
        public ScreenEffectConst.FixedOverscrollState fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.NONE;
        public float scrollProgress = 0.0f;

        public TargetInfo() {
        }

        public void setChild(View child) {
            if (this.mChild == child) {
                return;
            }
            this.mChild = child;
            updateInfos();
        }

        public void setScrollX(int scrollX) {
            if (this.scrollX == scrollX) {
                return;
            }
            this.scrollX = scrollX;
            updateInfos();
        }

        private void updateInfos() {
            updateScrollDirection();
            updateOverscrollState();
            updateWhichPageToDraw();
            updateFixedOverscrollState();
            updateScrollProgress();
        }

        private void updateScrollDirection() {
            int scrollForPage = ScreenEffectTargetManager.this.getScrollForPage(ScreenEffectTargetManager.this.mParent instanceof PagedView ? ((PagedView) ScreenEffectTargetManager.this.mParent).getCurrentPage() : -1);
            int i = this.scrollX;
            if (i < scrollForPage) {
                this.scrollDirection = ScreenEffectConst.ScrollDirection.TO_LEFT;
            } else if (scrollForPage < i) {
                this.scrollDirection = ScreenEffectConst.ScrollDirection.TO_RIGHT;
            } else {
                this.scrollDirection = ScreenEffectConst.ScrollDirection.NONE;
            }
        }

        private void updateOverscrollState() {
            if (this.scrollX < 0) {
                this.overscrollState = ScreenEffectConst.OverscrollState.OVERSCROLL_LEFT;
            } else if (ScreenEffectTargetManager.this.getMaxScroll() < this.scrollX) {
                this.overscrollState = ScreenEffectConst.OverscrollState.OVERSCROLL_RIGHT;
            } else {
                this.overscrollState = ScreenEffectConst.OverscrollState.NONE;
            }
        }

        private void updateWhichPageToDraw() {
            if (isOverscrollEffectRunning() || ScreenEffectTargetManager.this.mParent == null) {
                return;
            }
            boolean zIsEnabled = LoopNormalModeManager.getInstance(ScreenEffectTargetManager.this.mParent.getContext()).isEnabled((PagedView) ScreenEffectTargetManager.this.mParent);
            boolean value = LGHomeFeature.Config.FEATURE_USE_LASTPAGE_EFFECT.getValue();
            if (!zIsEnabled && !value && ScreenEffectTargetManager.this.isOverscrollLeft()) {
                this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT;
                return;
            }
            if (!zIsEnabled && !value && ScreenEffectTargetManager.this.isOverscrollRight()) {
                this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT;
                return;
            }
            int scrollForPageLoop = this.scrollX - ScreenEffectTargetManager.this.getScrollForPageLoop(ScreenEffectTargetManager.this.indexOfChild(this.mChild));
            if (scrollForPageLoop > 0) {
                this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT;
            } else if (scrollForPageLoop < 0) {
                this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT;
            } else {
                this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.NONE;
            }
        }

        private boolean isOverscrollEffectRunning() {
            LauncherScroller scroller;
            return (ScreenEffectTargetManager.this.mParent instanceof PagedView) && (scroller = ((PagedView) ScreenEffectTargetManager.this.mParent).getScroller()) != null && !scroller.isFinished() && (scroller.getInterpolator() instanceof ScreenEffectInterpolatorSpring);
        }

        private void updateFixedOverscrollState() {
            this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.NONE;
            int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[this.whichPageToDraw.ordinal()];
            if (i == 1) {
                int i2 = this.scrollX;
                if (i2 < 0) {
                    this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.INNER;
                    return;
                } else {
                    if (i2 > 0) {
                        this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.OUTER;
                        return;
                    }
                    return;
                }
            }
            if (i != 2) {
                return;
            }
            int maxScroll = ScreenEffectTargetManager.this.getMaxScroll();
            int i3 = this.scrollX;
            if (maxScroll < i3) {
                this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.INNER;
            } else if (maxScroll > i3) {
                this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.OUTER;
            }
        }

        private void updateScrollProgress() {
            ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.this;
            int[] scrollDeltaAndRange = ScreenEffectTargetManager.this.getScrollDeltaAndRange(screenEffectTargetManager.validatePageIndexForLoop(screenEffectTargetManager.indexOfChild(this.mChild)));
            int i = scrollDeltaAndRange[0];
            int iAbs = Math.abs(scrollDeltaAndRange[1]);
            this.scrollProgress = 0.0f;
            if (iAbs != 0) {
                float f = i / iAbs;
                this.scrollProgress = f;
                if (f < 0.0f) {
                    this.scrollProgress = f + 1.0f;
                }
            }
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.screeneffect.ScreenEffectTargetManager$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw;

        static {
            int[] iArr = new int[ScreenEffectConst.WhichPageToDraw.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw = iArr;
            try {
                iArr[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NONE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }
}
