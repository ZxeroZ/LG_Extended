package com.android.launcher3.allapps;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.BaseRecyclerView;
import com.android.launcher3.BaseRecyclerViewFastScrollBar;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsRecyclerView extends BaseRecyclerView {
    private static final int FAST_SCROLL_BAR_MODE_DISTRIBUTE_BY_ROW = 0;
    private static final int FAST_SCROLL_BAR_MODE_DISTRIBUTE_BY_SECTIONS = 1;
    private static final int FAST_SCROLL_MODE_FREE_SCROLL = 1;
    private static final int FAST_SCROLL_MODE_JUMP_TO_FIRST_ICON = 0;
    private AlphabeticalAppsList mApps;
    int mFastScrollFrameIndex;
    final int[] mFastScrollFrames;
    private final int mFastScrollMode;
    BaseRecyclerViewFastScrollBar.FastScrollFocusableView mLastFastScrollFocusedView;
    private int mNumAppsPerRow;
    int mPrevFastScrollFocusedPosition;
    private final int mScrollBarMode;
    private BaseRecyclerView.ScrollPositionState mScrollPosState;
    Runnable mSmoothSnapNextFrameRunnable;

    public AllAppsRecyclerView(Context context) {
        this(context, null);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        this.mFastScrollFrames = new int[10];
        this.mFastScrollMode = 0;
        this.mScrollBarMode = 0;
        this.mScrollPosState = new BaseRecyclerView.ScrollPositionState();
        this.mSmoothSnapNextFrameRunnable = new Runnable() { // from class: com.android.launcher3.allapps.AllAppsRecyclerView.1
            @Override // java.lang.Runnable
            public void run() {
                if (AllAppsRecyclerView.this.mFastScrollFrameIndex < AllAppsRecyclerView.this.mFastScrollFrames.length) {
                    AllAppsRecyclerView allAppsRecyclerView = AllAppsRecyclerView.this;
                    allAppsRecyclerView.scrollBy(0, allAppsRecyclerView.mFastScrollFrames[AllAppsRecyclerView.this.mFastScrollFrameIndex]);
                    AllAppsRecyclerView.this.mFastScrollFrameIndex++;
                    AllAppsRecyclerView allAppsRecyclerView2 = AllAppsRecyclerView.this;
                    allAppsRecyclerView2.postOnAnimation(allAppsRecyclerView2.mSmoothSnapNextFrameRunnable);
                    return;
                }
                AllAppsRecyclerView allAppsRecyclerView3 = AllAppsRecyclerView.this;
                RecyclerView.ViewHolder viewHolderFindViewHolderForPosition = allAppsRecyclerView3.findViewHolderForPosition(allAppsRecyclerView3.mPrevFastScrollFocusedPosition);
                if (viewHolderFindViewHolderForPosition == null || !(viewHolderFindViewHolderForPosition.itemView instanceof BaseRecyclerViewFastScrollBar.FastScrollFocusableView) || AllAppsRecyclerView.this.mLastFastScrollFocusedView == viewHolderFindViewHolderForPosition.itemView) {
                    return;
                }
                AllAppsRecyclerView.this.mLastFastScrollFocusedView = (BaseRecyclerViewFastScrollBar.FastScrollFocusableView) viewHolderFindViewHolderForPosition.itemView;
                AllAppsRecyclerView.this.mLastFastScrollFocusedView.setFastScrollFocused(true, true);
            }
        };
    }

    public void setApps(AlphabeticalAppsList apps) {
        this.mApps = apps;
    }

    public void setNumAppsPerRow(DeviceProfile grid, int numAppsPerRow) {
        this.mNumAppsPerRow = numAppsPerRow;
        RecyclerView.RecycledViewPool recycledViewPool = getRecycledViewPool();
        int iCeil = (int) Math.ceil(grid.availableHeightPx / grid.allAppsIconSizePx);
        recycledViewPool.setMaxRecycledViews(3, 1);
        recycledViewPool.setMaxRecycledViews(1, this.mNumAppsPerRow * iCeil);
        recycledViewPool.setMaxRecycledViews(2, this.mNumAppsPerRow);
        recycledViewPool.setMaxRecycledViews(0, iCeil);
    }

    public void scrollToTop() {
        scrollToPosition(0);
    }

    @Override // com.android.launcher3.BaseRecyclerView, android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipRect(this.mBackgroundPadding.left, this.mBackgroundPadding.top, getWidth() - this.mBackgroundPadding.right, getHeight() - this.mBackgroundPadding.bottom);
        super.dispatchDraw(canvas);
    }

    @Override // com.android.launcher3.BaseRecyclerView, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        addOnItemTouchListener(this);
    }

    public int getContainerType(View v) {
        int childPosition;
        if (this.mApps.hasFilter()) {
            return 8;
        }
        return ((v instanceof BubbleTextView) && (childPosition = getChildPosition((BubbleTextView) v)) != -1 && this.mApps.getAdapterItems().get(childPosition).viewType == 2) ? 7 : 4;
    }

    @Override // com.android.launcher3.BaseRecyclerView
    public String scrollToPositionAtProgress(float touchFraction) {
        int numAppRows = this.mApps.getNumAppRows();
        if (numAppRows == 0) {
            return "";
        }
        stopScroll();
        List<AlphabeticalAppsList.FastScrollSectionInfo> fastScrollerSections = this.mApps.getFastScrollerSections();
        AlphabeticalAppsList.FastScrollSectionInfo fastScrollSectionInfo = fastScrollerSections.get(0);
        int i = 1;
        while (i < fastScrollerSections.size()) {
            AlphabeticalAppsList.FastScrollSectionInfo fastScrollSectionInfo2 = fastScrollerSections.get(i);
            if (fastScrollSectionInfo2.touchFraction > touchFraction) {
                break;
            }
            i++;
            fastScrollSectionInfo = fastScrollSectionInfo2;
        }
        getCurScrollState(this.mScrollPosState, this.mApps.getAdapterItems());
        getAvailableScrollHeight(numAppRows, this.mScrollPosState.rowHeight, 0);
        if (this.mPrevFastScrollFocusedPosition != fastScrollSectionInfo.fastScrollToItem.position) {
            this.mPrevFastScrollFocusedPosition = fastScrollSectionInfo.fastScrollToItem.position;
            BaseRecyclerViewFastScrollBar.FastScrollFocusableView fastScrollFocusableView = this.mLastFastScrollFocusedView;
            if (fastScrollFocusableView != null) {
                fastScrollFocusableView.setFastScrollFocused(false, true);
                this.mLastFastScrollFocusedView = null;
            }
            smoothSnapToPosition(this.mPrevFastScrollFocusedPosition, this.mScrollPosState);
        }
        return fastScrollSectionInfo.sectionName;
    }

    @Override // com.android.launcher3.BaseRecyclerView
    public void onFastScrollCompleted() {
        super.onFastScrollCompleted();
        BaseRecyclerViewFastScrollBar.FastScrollFocusableView fastScrollFocusableView = this.mLastFastScrollFocusedView;
        if (fastScrollFocusableView != null) {
            fastScrollFocusableView.setFastScrollFocused(false, true);
            this.mLastFastScrollFocusedView = null;
        }
        this.mPrevFastScrollFocusedPosition = -1;
    }

    @Override // com.android.launcher3.BaseRecyclerView
    public void onUpdateScrollbar() {
        List<AlphabeticalAppsList.AdapterItem> adapterItems = this.mApps.getAdapterItems();
        if (adapterItems.isEmpty() || this.mNumAppsPerRow == 0) {
            this.mScrollbar.setScrollbarThumbOffset(-1, -1);
            return;
        }
        int numAppRows = this.mApps.getNumAppRows();
        getCurScrollState(this.mScrollPosState, adapterItems);
        if (this.mScrollPosState.rowIndex < 0) {
            this.mScrollbar.setScrollbarThumbOffset(-1, -1);
        } else {
            synchronizeScrollBarThumbOffsetToViewScroll(this.mScrollPosState, numAppRows, 0);
        }
    }

    private void smoothSnapToPosition(final int position, BaseRecyclerView.ScrollPositionState scrollPosState) {
        removeCallbacks(this.mSmoothSnapNextFrameRunnable);
        int paddingTop = (getPaddingTop() + (scrollPosState.rowIndex * scrollPosState.rowHeight)) - scrollPosState.rowTopOffset;
        int scrollAtPosition = getScrollAtPosition(position, scrollPosState.rowHeight);
        int length = this.mFastScrollFrames.length;
        for (int i = 0; i < length; i++) {
            this.mFastScrollFrames[i] = (scrollAtPosition - paddingTop) / length;
        }
        this.mFastScrollFrameIndex = 0;
        postOnAnimation(this.mSmoothSnapNextFrameRunnable);
    }

    private void getCurScrollState(BaseRecyclerView.ScrollPositionState stateOut, List<AlphabeticalAppsList.AdapterItem> items) {
        stateOut.rowIndex = -1;
        stateOut.rowTopOffset = -1;
        stateOut.rowHeight = -1;
        if (items.isEmpty() || this.mNumAppsPerRow == 0) {
            return;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int childPosition = getChildPosition(childAt);
            if (childPosition != -1) {
                AlphabeticalAppsList.AdapterItem adapterItem = items.get(childPosition);
                if (adapterItem.viewType == 1 || adapterItem.viewType == 2) {
                    stateOut.rowIndex = adapterItem.rowIndex;
                    stateOut.rowTopOffset = getLayoutManager().getDecoratedTop(childAt);
                    stateOut.rowHeight = childAt.getHeight();
                    return;
                }
            }
        }
    }

    private int getScrollAtPosition(int position, int rowHeight) {
        AlphabeticalAppsList.AdapterItem adapterItem = this.mApps.getAdapterItems().get(position);
        if (adapterItem.viewType == 1 || adapterItem.viewType == 2) {
            return (adapterItem.rowIndex > 0 ? getPaddingTop() : 0) + (adapterItem.rowIndex * rowHeight);
        }
        return 0;
    }
}
