package com.lge.launcher3.wing.carousel.manager;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import androidx.recyclerview.widget.RecyclerView;
import com.lge.launcher3.R;
import com.lge.launcher3.wing.carousel.scrolltweaker.NormalScroller;
import com.lge.launcher3.wing.carousel.transformer.LinearViewTransformer;
import com.lge.launcher3.wing.carousel.util.MultiSparseArray;
import com.lge.launcher3.wing.carousel.widget.CarouselView;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/* JADX INFO: loaded from: classes2.dex */
public class CarouselLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = "CarouselLayoutManager";
    private Context mContext;
    private int mDecoratedChildHeight;
    private int mDecoratedChildWidth;
    private CarouselView.Scroller mScroller;
    public static final CarouselView.ViewTransformer DEFAULT_TRANSFORMER = new LinearViewTransformer();
    public static final CarouselView.Scroller DEFAULT_SCROLLER = new NormalScroller();
    private CarouselView.OnItemClickListener mOnItemClickListener = null;
    private boolean mInfinite = false;
    private CarouselView.DrawOrder mDrawOrder = CarouselView.DrawOrder.FirstBack;
    private int mExtraVisibleChilds = 0;
    private int mGravity = 1;
    private Queue<Runnable> mPendingTasks = new LinkedList();
    private Handler mHandler = new Handler();
    private RecyclerView mRecyclerView = null;
    private int mLeftOffset = 0;
    private int mTopOffset = 0;
    private int mMeasuredWidth = 0;
    private int mMeasuredHeight = 0;
    private boolean mHasDatasetUpdated = false;
    private boolean mLongClicked = false;
    private boolean mScrollPositionUpdated = false;
    private Interpolator mScrollInterpolator = null;
    private int mScrollOffset = 0;
    private CarouselView.ViewTransformer mTransformer = DEFAULT_TRANSFORMER;

    private int positionOfIndex(int childIndex) {
        return childIndex;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollVertically() {
        return false;
    }

    public CarouselLayoutManager(Context context) {
        setTransformer(null);
        resetOptions();
        this.mContext = context;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        savedState.scrollOffset = this.mScrollOffset;
        return savedState;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (state instanceof SavedState) {
            this.mScrollOffset = ((SavedState) state).scrollOffset;
        }
    }

    public CarouselLayoutManager setTransformer(CarouselView.ViewTransformer transformer) {
        CarouselView.ViewTransformer viewTransformer = this.mTransformer;
        CarouselView.ViewTransformer viewTransformer2 = transformer != null ? transformer : DEFAULT_TRANSFORMER;
        this.mTransformer = viewTransformer2;
        if (viewTransformer2 != viewTransformer) {
            resetOptions();
            transformer.onAttach(this);
        }
        return this;
    }

    public void resetOptions() {
        setScroller(null);
        setDrawOrder(CarouselView.DrawOrder.FirstBack);
    }

    public CarouselView.ViewTransformer getTransformer() {
        return this.mTransformer;
    }

    public int getExtraVisibleChilds() {
        return this.mExtraVisibleChilds;
    }

    public CarouselLayoutManager setExtraVisibleChilds(CarouselView carouselView, int num) {
        this.mExtraVisibleChilds = num;
        carouselView.setItemViewCacheSize(((num + 2) * 2) + 1);
        return this;
    }

    public boolean isInfinite() {
        return this.mInfinite;
    }

    public CarouselLayoutManager setInfinite(boolean infinite) {
        this.mInfinite = infinite;
        return this;
    }

    public CarouselView.Scroller getScroller() {
        return this.mScroller;
    }

    public CarouselLayoutManager setScroller(CarouselView.Scroller scroller) {
        if (scroller == null) {
            scroller = DEFAULT_SCROLLER;
        }
        this.mScroller = scroller;
        return this;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
        requestLayout();
    }

    public int getGravity() {
        return this.mGravity;
    }

    public CarouselView.DrawOrder getDrawOrder() {
        return this.mDrawOrder;
    }

    public CarouselLayoutManager setDrawOrder(CarouselView.DrawOrder drawOrder) {
        this.mDrawOrder = drawOrder;
        return this;
    }

    public CarouselLayoutManager setOnItemClickListener(CarouselView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(this.mContext.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_width), this.mContext.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_frame_height));
    }

    public int getScrollX() {
        return this.mScrollOffset;
    }

    private int getContentLeftX() {
        return this.mScrollOffset - (getContentWidth() / 2);
    }

    private int getContentRightX() {
        return this.mScrollOffset + (getContentWidth() / 2);
    }

    private int getLeftmostVisiblePosition() {
        int iFloor = ((int) Math.floor(pixelToPosition(getContentLeftX()))) - this.mExtraVisibleChilds;
        return this.mInfinite ? iFloor : Math.max(iFloor, 0);
    }

    private int getRightmostVisiblePosition() {
        int iCeil = ((int) Math.ceil(pixelToPosition(getContentRightX()))) + this.mExtraVisibleChilds;
        return this.mInfinite ? iCeil : Math.min(iCeil, getItemCount() - 1);
    }

    public int getCurrentPosition() {
        return Math.round(pixelToPosition(this.mScrollOffset));
    }

    public float getCurrentPositionPoint() {
        return pixelToPosition(this.mScrollOffset);
    }

    public float getCurrentOffset() {
        float fPixelToPosition = pixelToPosition(this.mScrollOffset);
        return Math.abs(fPixelToPosition - ((float) Math.floor(fPixelToPosition)));
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0017  */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int scrollHorizontallyBy(int r5, androidx.recyclerview.widget.RecyclerView.Recycler r6, androidx.recyclerview.widget.RecyclerView.State r7) {
        /*
            r4 = this;
            com.lge.launcher3.wing.carousel.widget.CarouselView$Scroller r0 = r4.mScroller
            if (r0 == 0) goto L8
            int r5 = r0.tweakScrollDx(r5)
        L8:
            boolean r0 = r4.mInfinite
            if (r0 != 0) goto L2c
            int r0 = r4.mScrollOffset
            int r1 = r0 + r5
            r2 = 0
            if (r1 >= 0) goto L19
            if (r0 <= 0) goto L17
            int r5 = -r0
            goto L2c
        L17:
            r5 = r2
            goto L2c
        L19:
            int r0 = r4.mDecoratedChildWidth
            int r1 = r4.getItemCount()
            int r1 = r1 + (-1)
            int r0 = r0 * r1
            int r1 = r4.mScrollOffset
            int r3 = r1 + r5
            if (r3 <= r0) goto L2c
            if (r1 >= r0) goto L17
            int r0 = r0 - r1
            r5 = r0
        L2c:
            if (r5 == 0) goto L36
            int r0 = r4.mScrollOffset
            int r0 = r0 + r5
            r4.mScrollOffset = r0
            r4.fillChildrenView(r6, r7)
        L36:
            com.lge.launcher3.wing.carousel.widget.CarouselView$Scroller r6 = r4.mScroller
            if (r6 == 0) goto L3e
            int r5 = r6.inverseTweakScrollDx(r5)
        L3e:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager.scrollHorizontallyBy(int, androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State):int");
    }

    protected float pixelToPosition(int pixel) {
        int i = this.mDecoratedChildWidth;
        if (i != 0) {
            return pixel / i;
        }
        return 0.0f;
    }

    private int getContentWidth() {
        return (this.mMeasuredWidth - getPaddingRight()) - getPaddingLeft();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        this.mDecoratedChildWidth = 0;
        this.mDecoratedChildHeight = 0;
        super.onMeasure(recycler, state, widthSpec, heightSpec);
        adjustHostDimension(recycler, state, widthSpec, heightSpec);
        log("carousel width = " + this.mMeasuredWidth + ", height = " + this.mMeasuredHeight, new Object[0]);
        if (CarouselView.isDebug()) {
            Log.d(TAG, String.format("carousel onMeasure %d %d %d %d", Integer.valueOf(View.MeasureSpec.getMode(widthSpec)), Integer.valueOf(View.MeasureSpec.getMode(heightSpec)), Integer.valueOf(View.MeasureSpec.getSize(widthSpec)), Integer.valueOf(View.MeasureSpec.getSize(heightSpec))));
        }
    }

    void adjustHostDimension(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int mode = View.MeasureSpec.getMode(widthSpec);
        int mode2 = View.MeasureSpec.getMode(heightSpec);
        int size = View.MeasureSpec.getSize(widthSpec);
        int size2 = View.MeasureSpec.getSize(heightSpec);
        this.mMeasuredWidth = 0;
        this.mMeasuredHeight = 0;
        measureChildSize(recycler);
        int iMax = Math.max(this.mDecoratedChildWidth, getMinimumWidth());
        int iMax2 = Math.max(this.mDecoratedChildHeight, getMinimumHeight());
        if (mode == Integer.MIN_VALUE) {
            size = Math.min(iMax, size);
        } else if (mode != 1073741824) {
            size = iMax;
        }
        if (mode2 == Integer.MIN_VALUE) {
            size2 = Math.min(iMax2, size2);
        } else if (mode2 != 1073741824) {
            size2 = iMax2;
        }
        this.mMeasuredWidth = size;
        this.mMeasuredHeight = size2;
        setMeasuredDimension(size, size2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void setMeasuredDimension(int widthSize, int heightSize) {
        super.setMeasuredDimension(widthSize, heightSize);
        this.mMeasuredWidth = widthSize;
        this.mMeasuredHeight = heightSize;
    }

    private void measureChildSize(RecyclerView.Recycler recycler) {
        this.mDecoratedChildWidth = this.mContext.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_width);
        this.mDecoratedChildHeight = this.mContext.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_frame_height);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
        Rect rect = new Rect();
        calculateItemDecorationsForChild(child, rect);
        int i = widthUsed + rect.left + rect.right;
        int i2 = heightUsed + rect.top + rect.bottom;
        RecyclerView recyclerView = this.mRecyclerView;
        int width = recyclerView != null ? recyclerView.getWidth() : this.mMeasuredWidth;
        RecyclerView recyclerView2 = this.mRecyclerView;
        child.measure(RecyclerView.LayoutManager.getChildMeasureSpec(width, getPaddingLeft() + getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin + i, layoutParams.width, false), RecyclerView.LayoutManager.getChildMeasureSpec(recyclerView2 != null ? recyclerView2.getHeight() : this.mMeasuredHeight, getPaddingTop() + getPaddingBottom() + layoutParams.topMargin + layoutParams.bottomMargin + i2, layoutParams.height, false));
    }

    private void updateWindowVariables() {
        int i = this.mGravity & 7;
        if (i == 3) {
            this.mLeftOffset = getPaddingLeft();
        } else if (i == 5) {
            this.mLeftOffset = (this.mMeasuredWidth - getPaddingRight()) - this.mDecoratedChildWidth;
        } else {
            this.mLeftOffset = ((((this.mMeasuredWidth - getPaddingLeft()) - getPaddingRight()) - this.mDecoratedChildWidth) / 2) + getPaddingLeft();
        }
        int i2 = this.mGravity & 112;
        if (i2 == 16) {
            this.mTopOffset = ((((this.mMeasuredHeight - getPaddingTop()) - getPaddingBottom()) - this.mDecoratedChildHeight) / 2) + getPaddingTop();
        } else if (i2 != 80) {
            this.mTopOffset = getPaddingTop();
        } else {
            this.mTopOffset = (this.mMeasuredHeight - getPaddingBottom()) - this.mDecoratedChildHeight;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final Queue<Runnable> queue;
        super.onLayoutChildren(recycler, state);
        logv("onLayoutChildren ==============", new Exception());
        if (getItemCount() == 0) {
            logv("just detachAndScrapAttachedViews", new Object[0]);
            removeAndRecycleAllViews(recycler);
            return;
        }
        measureChildSize(recycler);
        updateWindowVariables();
        if (state.didStructureChange() || this.mHasDatasetUpdated || this.mScrollPositionUpdated) {
            recycler.clear();
            detachAndScrapAttachedViews(recycler);
            this.mHasDatasetUpdated = false;
            this.mScrollPositionUpdated = false;
        }
        fillChildrenView(recycler, state);
        logv("onLayoutChildren : Queue Pending Tasks", new Object[0]);
        synchronized (this) {
            queue = this.mPendingTasks;
            this.mPendingTasks = new LinkedList();
        }
        post(new Runnable() { // from class: com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager.1
            @Override // java.lang.Runnable
            public void run() {
                while (!queue.isEmpty()) {
                    ((Runnable) queue.poll()).run();
                }
            }
        });
        logv("onLayoutChildren ============== end", new Object[0]);
    }

    private int getVisibleChildCount() {
        return (getContentWidth() / this.mDecoratedChildWidth) + 1;
    }

    public int translatePosition(int position) {
        if (!this.mInfinite) {
            return position;
        }
        int itemCount = getItemCount();
        int i = position % itemCount;
        return i < 0 ? i + itemCount : i;
    }

    public boolean isValidPosition(int position) {
        int itemCount = getItemCount();
        if (itemCount == 0) {
            return false;
        }
        return this.mInfinite || (position >= 0 && position < itemCount);
    }

    private void fillChildrenView(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int i;
        int i2;
        logv("fillChildrenView ==============", new Object[0]);
        MultiSparseArray<View> multiSparseArray = new MultiSparseArray<>(getChildCount());
        logv("getChildCount() = " + getChildCount(), new Object[0]);
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            int position = getPosition(childAt);
            multiSparseArray.put(position, childAt);
            logv(String.format("viewCache[%d] = %s", Integer.valueOf(position), childAt), new Object[0]);
            detachView(childAt);
        }
        int leftmostVisiblePosition = getLeftmostVisiblePosition();
        int rightmostVisiblePosition = getRightmostVisiblePosition();
        int currentPosition = getCurrentPosition();
        if (leftmostVisiblePosition <= rightmostVisiblePosition) {
            int i3 = AnonymousClass6.$SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DrawOrder[this.mDrawOrder.ordinal()];
            if (i3 == 1 || i3 == 2) {
                if (this.mDrawOrder == CarouselView.DrawOrder.FirstFront) {
                    i = -1;
                    rightmostVisiblePosition = leftmostVisiblePosition;
                    leftmostVisiblePosition = rightmostVisiblePosition;
                } else {
                    i = 1;
                }
                int i4 = leftmostVisiblePosition - i;
                do {
                    i4 += i;
                    drawChild(i4, multiSparseArray, recycler, state);
                } while (i4 != rightmostVisiblePosition);
            } else if (i3 == 3) {
                while (true) {
                    i2 = currentPosition - leftmostVisiblePosition;
                    if (i2 <= rightmostVisiblePosition - currentPosition) {
                        break;
                    }
                    drawChild(leftmostVisiblePosition, multiSparseArray, recycler, state);
                    leftmostVisiblePosition++;
                }
                while (i2 < rightmostVisiblePosition - currentPosition) {
                    drawChild(rightmostVisiblePosition, multiSparseArray, recycler, state);
                    rightmostVisiblePosition--;
                }
                while (leftmostVisiblePosition < rightmostVisiblePosition) {
                    drawChild(leftmostVisiblePosition, multiSparseArray, recycler, state);
                    drawChild(rightmostVisiblePosition, multiSparseArray, recycler, state);
                    leftmostVisiblePosition++;
                    rightmostVisiblePosition--;
                }
                drawChild(currentPosition, multiSparseArray, recycler, state);
            } else if (i3 == 4) {
                drawChild(currentPosition, multiSparseArray, recycler, state);
                int i5 = currentPosition - 1;
                int i6 = rightmostVisiblePosition;
                while (true) {
                    if (i5 < leftmostVisiblePosition && i6 > rightmostVisiblePosition) {
                        break;
                    }
                    if (i5 >= leftmostVisiblePosition) {
                        drawChild(i5, multiSparseArray, recycler, state);
                        i5--;
                    }
                    if (i6 <= rightmostVisiblePosition) {
                        drawChild(i6, multiSparseArray, recycler, state);
                        i6++;
                    }
                }
            }
        }
        for (int size = multiSparseArray.size() - 1; size >= 0; size--) {
            logv(String.format("recycleView (%d) %s", Integer.valueOf(multiSparseArray.keyAt(size)), multiSparseArray.valuesAt(size)), new Object[0]);
            Iterator<View> it = multiSparseArray.valuesAt(size).iterator();
            while (it.hasNext()) {
                recycler.recycleView(it.next());
            }
        }
        logv("getChildCount() = " + getChildCount(), new Object[0]);
        logv("fillChildrenView ============== end", new Object[0]);
    }

    /* JADX INFO: renamed from: com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager$6, reason: invalid class name */
    static /* synthetic */ class AnonymousClass6 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DrawOrder;

        static {
            int[] iArr = new int[CarouselView.DrawOrder.values().length];
            $SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DrawOrder = iArr;
            try {
                iArr[CarouselView.DrawOrder.FirstBack.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DrawOrder[CarouselView.DrawOrder.FirstFront.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DrawOrder[CarouselView.DrawOrder.CenterFront.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DrawOrder[CarouselView.DrawOrder.CenterBack.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private void drawChild(final int position, MultiSparseArray<View> viewCache, RecyclerView.Recycler recycler, RecyclerView.State state) {
        logv(String.format("drawChild (%d)", Integer.valueOf(position)), new Object[0]);
        int iTranslatePosition = translatePosition(position);
        View viewPop = viewCache.pop(iTranslatePosition);
        try {
            if (viewPop == null) {
                viewPop = recycler.getViewForPosition(iTranslatePosition);
                viewPop.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (CarouselLayoutManager.this.mOnItemClickListener != null) {
                            if (!CarouselLayoutManager.this.mLongClicked) {
                                CarouselView.OnItemClickListener onItemClickListener = CarouselLayoutManager.this.mOnItemClickListener;
                                int i = position;
                                onItemClickListener.onItemClick(null, v, i, CarouselLayoutManager.this.translatePosition(i));
                                return;
                            }
                            CarouselLayoutManager.this.mLongClicked = false;
                        }
                    }
                });
                viewPop.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager.3
                    @Override // android.view.View.OnLongClickListener
                    public boolean onLongClick(View view) {
                        CarouselLayoutManager.this.mLongClicked = true;
                        return false;
                    }
                });
                addView(viewPop);
                logv(String.format("addView (%d [%d]) %s", Integer.valueOf(position), Integer.valueOf(iTranslatePosition), viewPop), new Object[0]);
            } else {
                attachView(viewPop);
            }
            measureChildWithMargins(viewPop, 0, 0);
            if (state.isPreLayout()) {
                return;
            }
            int i = this.mLeftOffset;
            int i2 = this.mTopOffset;
            layoutDecorated(viewPop, i, i2, i + this.mDecoratedChildWidth, i2 + this.mDecoratedChildHeight);
            this.mTransformer.transform(viewPop, -(pixelToPosition(this.mScrollOffset) - position));
        } catch (IndexOutOfBoundsException unused) {
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void scrollToPosition(final int position) {
        RecyclerView recyclerView;
        if (this.mDecoratedChildWidth == 0 && getItemCount() > 0) {
            this.mPendingTasks.add(new Runnable() { // from class: com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager.4
                @Override // java.lang.Runnable
                public void run() {
                    CarouselLayoutManager.this.scrollToPosition(position);
                }
            });
            return;
        }
        int i = this.mDecoratedChildWidth * position;
        log("scrollToPosition " + position + "scrollOffset " + this.mScrollOffset + " -> " + i, new Object[0]);
        if (Math.abs(i - this.mScrollOffset) > ((double) this.mDecoratedChildWidth) * 1.5d) {
            this.mScrollPositionUpdated = true;
            log("scrollToPosition " + position + "set mScrollPositionUpdated", new Object[0]);
        }
        this.mScrollOffset = i;
        if (Build.VERSION.SDK_INT < 18 || (recyclerView = this.mRecyclerView) == null || recyclerView.isInLayout()) {
            return;
        }
        requestLayout();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void smoothScrollToPosition(final RecyclerView recyclerView, final RecyclerView.State state, final int position) {
        int iMax;
        log("smoothScrollToPosition " + position + " " + recyclerView, new Object[0]);
        int itemCount = getItemCount();
        int i = this.mDecoratedChildWidth;
        if (i == 0 && itemCount > 0) {
            this.mPendingTasks.add(new Runnable() { // from class: com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager.5
                @Override // java.lang.Runnable
                public void run() {
                    CarouselLayoutManager.this.smoothScrollToPosition(recyclerView, state, position);
                }
            });
            return;
        }
        if (i * itemCount == 0) {
            return;
        }
        if (!isInfinite()) {
            iMax = Math.max(0, Math.min(itemCount - 1, position));
        } else {
            iMax = position % itemCount;
        }
        int i2 = Integer.MAX_VALUE;
        for (int i3 = -1; i3 <= 1; i3++) {
            if (isInfinite() || i3 == 0) {
                int i4 = this.mDecoratedChildWidth;
                int i5 = (((i3 * itemCount) + iMax) * i4) - (this.mScrollOffset % (i4 * itemCount));
                if (Math.abs(i5) < Math.abs(i2)) {
                    i2 = i5;
                }
            }
        }
        recyclerView.smoothScrollBy(i2, 0, this.mScrollInterpolator);
    }

    public void setScrollInterpolator(Interpolator interpolator) {
        this.mScrollInterpolator = interpolator;
    }

    private PointF computeScrollVectorForPosition(int targetPosition) {
        return new PointF(targetPosition * this.mDecoratedChildWidth < this.mScrollOffset ? -1 : 1, 0.0f);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);
        removeAllViews();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsChanged(RecyclerView recyclerView) {
        super.onItemsChanged(recyclerView);
        this.mHasDatasetUpdated = true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount);
        this.mHasDatasetUpdated = true;
        for (int i = 0; i < itemCount; i++) {
            View viewFindViewByPosition = findViewByPosition(positionStart + i);
            if (viewFindViewByPosition != null) {
                viewFindViewByPosition.forceLayout();
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        this.mHasDatasetUpdated = true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        int itemCount2;
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        if (getItemCount() > 0 && this.mScrollOffset > (itemCount2 = this.mDecoratedChildWidth * (getItemCount() - 1))) {
            this.mScrollOffset = itemCount2;
        }
        this.mHasDatasetUpdated = true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        super.onItemsMoved(recyclerView, from, to, itemCount);
        this.mHasDatasetUpdated = true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        this.mRecyclerView = view;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        this.mRecyclerView = null;
    }

    private static void log(String format, Object... args) {
        if (CarouselView.isDebug()) {
            if (args.length > 0) {
                Log.d(TAG, String.format(format, args));
            } else {
                Log.d(TAG, format);
            }
        }
    }

    private static void logv(String format, Object... args) {
        if (CarouselView.isDebug()) {
            if (args.length > 0) {
                Log.v(TAG, String.format(format, args));
            } else {
                Log.v(TAG, format);
            }
        }
    }

    protected CarouselView getCarouselView() {
        return (CarouselView) this.mRecyclerView;
    }

    protected boolean post(Runnable action) {
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return false;
        }
        recyclerView.post(action);
        return true;
    }

    static class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager.SavedState.1
            /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int scrollOffset;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        SavedState() {
        }

        private SavedState(Parcel in) {
            this.scrollOffset = in.readInt();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.scrollOffset);
        }
    }
}
