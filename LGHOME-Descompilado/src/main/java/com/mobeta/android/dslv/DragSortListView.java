package com.mobeta.android.dslv;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class DragSortListView extends ListView {
    private static final int DRAGGING = 4;
    public static final int DRAG_NEG_X = 2;
    public static final int DRAG_NEG_Y = 8;
    public static final int DRAG_POS_X = 1;
    public static final int DRAG_POS_Y = 4;
    private static final int DROPPING = 2;
    private static final int IDLE = 0;
    private static final int NO_CANCEL = 0;
    private static final int ON_INTERCEPT_TOUCH_EVENT = 2;
    private static final int ON_TOUCH_EVENT = 1;
    private static final int REMOVING = 1;
    private static final int STOPPED = 3;
    private static final int sCacheSize = 3;
    private AdapterWrapper mAdapterWrapper;
    protected boolean mAnimate;
    private boolean mBlockLayoutRequests;
    private MotionEvent mCancelEvent;
    private int mCancelMethod;
    private HeightCache mChildHeightCache;
    protected float mCurrFloatAlpha;
    private int mDownScrollStartY;
    private float mDownScrollStartYF;
    private int mDragDeltaX;
    private int mDragDeltaY;
    private float mDragDownScrollHeight;
    private float mDragDownScrollStartFrac;
    protected boolean mDragEnabled;
    private int mDragFlags;
    private DragListener mDragListener;
    private DragScroller mDragScroller;
    protected DragSortTracker mDragSortTracker;
    private int mDragStartY;
    private int mDragState;
    private float mDragUpScrollHeight;
    protected float mDragUpScrollStartFrac;
    protected DropAnimator mDropAnimator;
    private DropListener mDropListener;
    private int mFirstExpPos;
    protected float mFloatAlpha;
    private Point mFloatLoc;
    private int mFloatPos;
    private View mFloatView;
    private int mFloatViewHeight;
    private int mFloatViewHeightHalf;
    private boolean mFloatViewInvalidated;
    protected FloatViewManager mFloatViewManager;
    private int mFloatViewMid;
    private boolean mFloatViewOnMeasured;
    private boolean mIgnoreTouchEvent;
    private boolean mInTouchEvent;
    protected int mItemHeightCollapsed;
    private boolean mLastCallWasIntercept;
    private int mLastX;
    private int mLastY;
    protected LiftAnimator mLiftAnimator;
    private boolean mListViewIntercepted;
    protected float mMaxScrollSpeed;
    private DataSetObserver mObserver;
    private int mOffsetX;
    private int mOffsetY;
    protected RemoveAnimator mRemoveAnimator;
    private RemoveListener mRemoveListener;
    private float mRemoveVelocityX;
    private View[] mSampleViewTypes;
    private DragScrollProfile mScrollProfile;
    private int mSecondExpPos;
    private float mSlideFrac;
    protected float mSlideRegionFrac;
    private int mSrcPos;
    private Point mTouchLoc;
    protected boolean mTrackDragSort;
    private int mUpScrollStartY;
    private float mUpScrollStartYF;
    private boolean mUseRemoveVelocity;
    private int mWidthMeasureSpec;
    private int mX;
    private int mY;

    public interface DragListener {
        void drag(int i, int i2);
    }

    public interface DragScrollProfile {
        float getSpeed(float f, long j);
    }

    public interface DragSortListener extends DropListener, DragListener, RemoveListener {
    }

    public interface DropListener {
        void drop(int i, int i2);
    }

    public interface FloatViewManager {
        View onCreateFloatView(int i);

        void onDestroyFloatView(View view);

        void onDragFloatView(View view, Point point, Point point2);
    }

    public interface RemoveListener {
        void remove(int i);
    }

    private static int rotate(int i, int i2, int i3, int i4) {
        int i5 = i4 - i3;
        int i6 = i + i2;
        return i6 < i3 ? i6 + i5 : i6 >= i4 ? i6 - i5 : i6;
    }

    public DragSortListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFloatLoc = new Point();
        this.mTouchLoc = new Point();
        this.mFloatViewOnMeasured = false;
        this.mFloatAlpha = 1.0f;
        this.mCurrFloatAlpha = 1.0f;
        this.mAnimate = false;
        this.mDragEnabled = true;
        this.mDragState = 0;
        this.mItemHeightCollapsed = 1;
        this.mWidthMeasureSpec = 0;
        this.mSampleViewTypes = new View[1];
        this.mDragUpScrollStartFrac = 0.33333334f;
        this.mDragDownScrollStartFrac = 0.33333334f;
        this.mMaxScrollSpeed = 0.5f;
        this.mScrollProfile = new DragScrollProfile() { // from class: com.mobeta.android.dslv.DragSortListView.1
            @Override // com.mobeta.android.dslv.DragSortListView.DragScrollProfile
            public float getSpeed(float f, long j) {
                return DragSortListView.this.mMaxScrollSpeed * f;
            }
        };
        this.mDragFlags = 0;
        this.mLastCallWasIntercept = false;
        this.mInTouchEvent = false;
        this.mFloatViewManager = null;
        this.mCancelMethod = 0;
        this.mSlideRegionFrac = 0.25f;
        this.mSlideFrac = 0.0f;
        this.mTrackDragSort = false;
        this.mBlockLayoutRequests = false;
        this.mIgnoreTouchEvent = false;
        this.mChildHeightCache = new HeightCache(3);
        this.mRemoveVelocityX = 0.0f;
        this.mListViewIntercepted = false;
        this.mFloatViewInvalidated = false;
        this.mDragScroller = new DragScroller();
        this.mCancelEvent = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0.0f, 0.0f, 0, 0.0f, 0.0f, 0, 0);
        this.mObserver = new DataSetObserver() { // from class: com.mobeta.android.dslv.DragSortListView.2
            private void cancel() {
                if (DragSortListView.this.mDragState == 4) {
                    DragSortListView.this.cancelDrag();
                }
            }

            @Override // android.database.DataSetObserver
            public void onChanged() {
                cancel();
            }

            @Override // android.database.DataSetObserver
            public void onInvalidated() {
                cancel();
            }
        };
    }

    public void setFloatAlpha(float f) {
        this.mCurrFloatAlpha = f;
    }

    public float getFloatAlpha() {
        return this.mCurrFloatAlpha;
    }

    public void setMaxScrollSpeed(float f) {
        this.mMaxScrollSpeed = f;
    }

    @Override // android.widget.ListView, android.widget.AbsListView
    public void setAdapter(ListAdapter listAdapter) {
        if (listAdapter != null) {
            this.mAdapterWrapper = new AdapterWrapper(listAdapter);
            listAdapter.registerDataSetObserver(this.mObserver);
            if (listAdapter instanceof DropListener) {
                setDropListener((DropListener) listAdapter);
            }
            if (listAdapter instanceof DragListener) {
                setDragListener((DragListener) listAdapter);
            }
            if (listAdapter instanceof RemoveListener) {
                setRemoveListener((RemoveListener) listAdapter);
            }
        } else {
            this.mAdapterWrapper = null;
        }
        super.setAdapter((ListAdapter) this.mAdapterWrapper);
    }

    public ListAdapter getInputAdapter() {
        AdapterWrapper adapterWrapper = this.mAdapterWrapper;
        if (adapterWrapper == null) {
            return null;
        }
        return adapterWrapper.getAdapter();
    }

    private class AdapterWrapper extends BaseAdapter {
        private ListAdapter mAdapter;

        public AdapterWrapper(ListAdapter listAdapter) {
            this.mAdapter = listAdapter;
            listAdapter.registerDataSetObserver(new DataSetObserver() { // from class: com.mobeta.android.dslv.DragSortListView.AdapterWrapper.1
                @Override // android.database.DataSetObserver
                public void onChanged() {
                    AdapterWrapper.this.notifyDataSetChanged();
                }

                @Override // android.database.DataSetObserver
                public void onInvalidated() {
                    AdapterWrapper.this.notifyDataSetInvalidated();
                }
            });
        }

        public ListAdapter getAdapter() {
            return this.mAdapter;
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return this.mAdapter.getItemId(i);
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mAdapter.getItem(i);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mAdapter.getCount();
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return this.mAdapter.areAllItemsEnabled();
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int i) {
            return this.mAdapter.isEnabled(i);
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int i) {
            return this.mAdapter.getItemViewType(i);
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return this.mAdapter.getViewTypeCount();
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return this.mAdapter.hasStableIds();
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean isEmpty() {
            return this.mAdapter.isEmpty();
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            DragSortItemView dragSortItemView;
            DragSortItemView dragSortItemView2;
            if (view != null) {
                dragSortItemView2 = (DragSortItemView) view;
                View childAt = dragSortItemView2.getChildAt(0);
                View view2 = this.mAdapter.getView(i, childAt, DragSortListView.this);
                if (view2 != childAt) {
                    if (childAt != null) {
                        dragSortItemView2.removeViewAt(0);
                    }
                    dragSortItemView2.addView(view2);
                }
            } else {
                View view3 = this.mAdapter.getView(i, null, DragSortListView.this);
                if (view3 instanceof Checkable) {
                    dragSortItemView = new DragSortItemViewCheckable(DragSortListView.this.getContext());
                } else {
                    dragSortItemView = new DragSortItemView(DragSortListView.this.getContext());
                }
                dragSortItemView.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
                dragSortItemView.addView(view3);
                dragSortItemView2 = dragSortItemView;
            }
            DragSortListView dragSortListView = DragSortListView.this;
            dragSortListView.adjustItem(i + dragSortListView.getHeaderViewsCount(), dragSortItemView2, true);
            return dragSortItemView2;
        }
    }

    private void drawDivider(int i, Canvas canvas) {
        ViewGroup viewGroup;
        int i2;
        int top;
        Drawable divider = getDivider();
        int dividerHeight = getDividerHeight();
        if (divider == null || dividerHeight == 0 || (viewGroup = (ViewGroup) getChildAt(i - getFirstVisiblePosition())) == null) {
            return;
        }
        int paddingLeft = getPaddingLeft();
        int width = getWidth() - getPaddingRight();
        int height = viewGroup.getChildAt(0).getHeight();
        if (i > this.mSrcPos) {
            top = viewGroup.getTop() + height;
            i2 = dividerHeight + top;
        } else {
            int bottom = viewGroup.getBottom() - height;
            int i3 = bottom - dividerHeight;
            i2 = bottom;
            top = i3;
        }
        canvas.save();
        canvas.clipRect(paddingLeft, top, width, i2);
        divider.setBounds(paddingLeft, top, width, i2);
        divider.draw(canvas);
        canvas.restore();
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        float f;
        super.dispatchDraw(canvas);
        if (this.mDragState != 0) {
            int i = this.mFirstExpPos;
            if (i != this.mSrcPos) {
                drawDivider(i, canvas);
            }
            int i2 = this.mSecondExpPos;
            if (i2 != this.mFirstExpPos && i2 != this.mSrcPos) {
                drawDivider(i2, canvas);
            }
        }
        View view = this.mFloatView;
        if (view != null) {
            int width = view.getWidth();
            int height = this.mFloatView.getHeight();
            int i3 = this.mFloatLoc.x;
            int width2 = getWidth();
            if (i3 < 0) {
                i3 = -i3;
            }
            if (i3 < width2) {
                float f2 = (width2 - i3) / width2;
                f = f2 * f2;
            } else {
                f = 0.0f;
            }
            int i4 = (int) (this.mCurrFloatAlpha * 255.0f * f);
            canvas.save();
            canvas.translate(this.mFloatLoc.x, this.mFloatLoc.y);
            canvas.clipRect(0, 0, width, height);
            canvas.saveLayerAlpha(0.0f, 0.0f, width, height, i4, 31);
            this.mFloatView.draw(canvas);
            canvas.restore();
            canvas.restore();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getItemHeight(int i) {
        View childAt = getChildAt(i - getFirstVisiblePosition());
        if (childAt != null) {
            return childAt.getHeight();
        }
        return calcItemHeight(i, getChildHeight(i));
    }

    private void printPosData() {
        Log.d("mobeta", "mSrcPos=" + this.mSrcPos + " mFirstExpPos=" + this.mFirstExpPos + " mSecondExpPos=" + this.mSecondExpPos);
    }

    private class HeightCache {
        private SparseIntArray mMap;
        private int mMaxSize;
        private ArrayList<Integer> mOrder;

        public HeightCache(int i) {
            this.mMap = new SparseIntArray(i);
            this.mOrder = new ArrayList<>(i);
            this.mMaxSize = i;
        }

        public void add(int i, int i2) {
            int i3 = this.mMap.get(i, -1);
            if (i3 != i2) {
                if (i3 == -1) {
                    if (this.mMap.size() == this.mMaxSize) {
                        this.mMap.delete(this.mOrder.remove(0).intValue());
                    }
                } else {
                    this.mOrder.remove(Integer.valueOf(i));
                }
                this.mMap.put(i, i2);
                this.mOrder.add(Integer.valueOf(i));
            }
        }

        public int get(int i) {
            return this.mMap.get(i, -1);
        }

        public void clear() {
            this.mMap.clear();
            this.mOrder.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getShuffleEdge(int i, int i2) {
        int headerViewsCount = getHeaderViewsCount();
        int footerViewsCount = getFooterViewsCount();
        if (i <= headerViewsCount || i >= getCount() - footerViewsCount) {
            return i2;
        }
        int dividerHeight = getDividerHeight();
        int i3 = this.mFloatViewHeight - this.mItemHeightCollapsed;
        int childHeight = getChildHeight(i);
        int itemHeight = getItemHeight(i);
        int i4 = this.mSecondExpPos;
        int i5 = this.mSrcPos;
        if (i4 <= i5) {
            if (i != i4 || this.mFirstExpPos == i4) {
                if (i > i4 && i <= i5) {
                }
            } else if (i == i5) {
                i2 += itemHeight;
                i3 = this.mFloatViewHeight;
            } else {
                i2 += itemHeight - childHeight;
            }
            i2 -= i3;
        } else if (i > i5 && i <= this.mFirstExpPos) {
            i2 += i3;
        } else if (i == i4 && this.mFirstExpPos != i4) {
            i2 += itemHeight - childHeight;
        }
        if (i <= i5) {
            return i2 + (((this.mFloatViewHeight - dividerHeight) - getChildHeight(i - 1)) / 2);
        }
        return i2 + (((childHeight - dividerHeight) - this.mFloatViewHeight) / 2);
    }

    private boolean updatePositions() {
        int i;
        int i2;
        int firstVisiblePosition = getFirstVisiblePosition();
        int count = this.mFirstExpPos;
        View childAt = getChildAt(count - firstVisiblePosition);
        if (childAt == null) {
            count = (getChildCount() / 2) + firstVisiblePosition;
            childAt = getChildAt(count - firstVisiblePosition);
        }
        int top = childAt.getTop();
        int height = childAt.getHeight();
        int shuffleEdge = getShuffleEdge(count, top);
        int dividerHeight = getDividerHeight();
        if (this.mFloatViewMid < shuffleEdge) {
            while (count >= 0) {
                count--;
                int itemHeight = getItemHeight(count);
                if (count == 0) {
                    i = (top - dividerHeight) - itemHeight;
                    int i3 = shuffleEdge;
                    shuffleEdge = i;
                    i2 = i3;
                    break;
                }
                top -= itemHeight + dividerHeight;
                int shuffleEdge2 = getShuffleEdge(count, top);
                if (this.mFloatViewMid >= shuffleEdge2) {
                    i2 = shuffleEdge;
                    shuffleEdge = shuffleEdge2;
                    break;
                }
                shuffleEdge = shuffleEdge2;
            }
            i2 = shuffleEdge;
        } else {
            int count2 = getCount();
            while (count < count2) {
                if (count == count2 - 1) {
                    i = top + dividerHeight + height;
                    int i32 = shuffleEdge;
                    shuffleEdge = i;
                    i2 = i32;
                    break;
                }
                top += height + dividerHeight;
                int i4 = count + 1;
                int itemHeight2 = getItemHeight(i4);
                int shuffleEdge3 = getShuffleEdge(i4, top);
                if (this.mFloatViewMid < shuffleEdge3) {
                    i2 = shuffleEdge;
                    shuffleEdge = shuffleEdge3;
                    break;
                }
                count = i4;
                height = itemHeight2;
                shuffleEdge = shuffleEdge3;
            }
            i2 = shuffleEdge;
        }
        int headerViewsCount = getHeaderViewsCount();
        int footerViewsCount = getFooterViewsCount();
        int i5 = this.mFirstExpPos;
        int i6 = this.mSecondExpPos;
        float f = this.mSlideFrac;
        if (this.mAnimate) {
            int iAbs = Math.abs(shuffleEdge - i2);
            int i7 = this.mFloatViewMid;
            if (i7 < shuffleEdge) {
                int i8 = shuffleEdge;
                shuffleEdge = i2;
                i2 = i8;
            }
            int i9 = (int) (this.mSlideRegionFrac * 0.5f * iAbs);
            float f2 = i9;
            int i10 = shuffleEdge + i9;
            int i11 = i2 - i9;
            if (i7 < i10) {
                this.mFirstExpPos = count - 1;
                this.mSecondExpPos = count;
                this.mSlideFrac = ((i10 - i7) * 0.5f) / f2;
            } else if (i7 < i11) {
                this.mFirstExpPos = count;
                this.mSecondExpPos = count;
            } else {
                this.mFirstExpPos = count;
                this.mSecondExpPos = count + 1;
                this.mSlideFrac = (((i2 - i7) / f2) + 1.0f) * 0.5f;
            }
        } else {
            this.mFirstExpPos = count;
            this.mSecondExpPos = count;
        }
        if (this.mFirstExpPos < headerViewsCount) {
            this.mFirstExpPos = headerViewsCount;
            this.mSecondExpPos = headerViewsCount;
            count = headerViewsCount;
        } else if (this.mSecondExpPos >= getCount() - footerViewsCount) {
            count = (getCount() - footerViewsCount) - 1;
            this.mFirstExpPos = count;
            this.mSecondExpPos = count;
        }
        boolean z = (this.mFirstExpPos == i5 && this.mSecondExpPos == i6 && this.mSlideFrac == f) ? false : true;
        int i12 = this.mFloatPos;
        if (count == i12) {
            return z;
        }
        DragListener dragListener = this.mDragListener;
        if (dragListener != null) {
            dragListener.drag(i12 - headerViewsCount, count - headerViewsCount);
        }
        this.mFloatPos = count;
        return true;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mTrackDragSort) {
            this.mDragSortTracker.appendState();
        }
    }

    private class SmoothAnimator implements Runnable {
        private float mA;
        private float mAlpha;
        private float mB;
        private float mC;
        private boolean mCanceled;
        private float mD;
        private float mDurationF;
        protected long mStartTime;

        public void onStart() {
        }

        public void onStop() {
        }

        public void onUpdate(float f, float f2) {
        }

        public SmoothAnimator(float f, int i) {
            this.mAlpha = f;
            this.mDurationF = i;
            float f2 = 1.0f / ((f * 2.0f) * (1.0f - f));
            this.mD = f2;
            this.mA = f2;
            this.mB = f / ((f - 1.0f) * 2.0f);
            this.mC = 1.0f / (1.0f - f);
        }

        public float transform(float f) {
            float f2 = this.mAlpha;
            if (f < f2) {
                return this.mA * f * f;
            }
            if (f < 1.0f - f2) {
                return this.mB + (this.mC * f);
            }
            float f3 = f - 1.0f;
            return 1.0f - ((this.mD * f3) * f3);
        }

        public void start() {
            this.mStartTime = SystemClock.uptimeMillis();
            this.mCanceled = false;
            onStart();
            DragSortListView.this.post(this);
        }

        public void cancel() {
            this.mCanceled = true;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mCanceled) {
                return;
            }
            float fUptimeMillis = (SystemClock.uptimeMillis() - this.mStartTime) / this.mDurationF;
            if (fUptimeMillis >= 1.0f) {
                onUpdate(1.0f, 1.0f);
                onStop();
            } else {
                onUpdate(fUptimeMillis, transform(fUptimeMillis));
                DragSortListView.this.post(this);
            }
        }
    }

    private class LiftAnimator extends SmoothAnimator {
        private float mFinalDragDeltaY;
        private float mInitDragDeltaY;

        public LiftAnimator(float f, int i) {
            super(f, i);
        }

        @Override // com.mobeta.android.dslv.DragSortListView.SmoothAnimator
        public void onStart() {
            this.mInitDragDeltaY = DragSortListView.this.mDragDeltaY;
            this.mFinalDragDeltaY = DragSortListView.this.mFloatViewHeightHalf;
        }

        @Override // com.mobeta.android.dslv.DragSortListView.SmoothAnimator
        public void onUpdate(float f, float f2) {
            if (DragSortListView.this.mDragState != 4) {
                cancel();
                return;
            }
            DragSortListView.this.mDragDeltaY = (int) ((this.mFinalDragDeltaY * f2) + ((1.0f - f2) * this.mInitDragDeltaY));
            DragSortListView.this.mFloatLoc.y = DragSortListView.this.mY - DragSortListView.this.mDragDeltaY;
            DragSortListView.this.doDragFloatView(true);
        }
    }

    protected class DropAnimator extends SmoothAnimator {
        private int mDropPos;
        private float mInitDeltaX;
        private float mInitDeltaY;
        private int srcPos;

        public DropAnimator(float f, int i) {
            super(f, i);
        }

        @Override // com.mobeta.android.dslv.DragSortListView.SmoothAnimator
        public void onStart() {
            this.mDropPos = DragSortListView.this.mFloatPos;
            this.srcPos = DragSortListView.this.mSrcPos;
            DragSortListView.this.mDragState = 2;
            this.mInitDeltaY = DragSortListView.this.mFloatLoc.y - getTargetY();
            this.mInitDeltaX = DragSortListView.this.mFloatLoc.x - DragSortListView.this.getPaddingLeft();
        }

        private int getTargetY() {
            int bottom;
            int firstVisiblePosition = DragSortListView.this.getFirstVisiblePosition();
            int dividerHeight = (DragSortListView.this.mItemHeightCollapsed + DragSortListView.this.getDividerHeight()) / 2;
            View childAt = DragSortListView.this.getChildAt(this.mDropPos - firstVisiblePosition);
            if (childAt != null) {
                int i = this.mDropPos;
                int i2 = this.srcPos;
                if (i == i2) {
                    return childAt.getTop();
                }
                if (i < i2) {
                    bottom = childAt.getTop();
                } else {
                    bottom = childAt.getBottom() + dividerHeight;
                    dividerHeight = DragSortListView.this.mFloatViewHeight;
                }
                return bottom - dividerHeight;
            }
            cancel();
            return -1;
        }

        @Override // com.mobeta.android.dslv.DragSortListView.SmoothAnimator
        public void onUpdate(float f, float f2) {
            int targetY = getTargetY();
            int paddingLeft = DragSortListView.this.getPaddingLeft();
            float f3 = DragSortListView.this.mFloatLoc.y - targetY;
            float f4 = DragSortListView.this.mFloatLoc.x - paddingLeft;
            float f5 = 1.0f - f2;
            if (f5 < Math.abs(f3 / this.mInitDeltaY) || f5 < Math.abs(f4 / this.mInitDeltaX)) {
                DragSortListView.this.mFloatLoc.y = targetY + ((int) (this.mInitDeltaY * f5));
                DragSortListView.this.mFloatLoc.x = DragSortListView.this.getPaddingLeft() + ((int) (this.mInitDeltaX * f5));
                DragSortListView.this.doDragFloatView(true);
            }
        }

        @Override // com.mobeta.android.dslv.DragSortListView.SmoothAnimator
        public void onStop() {
            DragSortListView.this.dropFloatView();
        }
    }

    protected class RemoveAnimator extends SmoothAnimator {
        private int mFirstChildHeight;
        private int mFirstPos;
        private float mFirstStartBlank;
        private float mFloatLocX;
        private int mSecondChildHeight;
        private int mSecondPos;
        private float mSecondStartBlank;
        private int srcPos;

        public RemoveAnimator(float f, int i) {
            super(f, i);
            this.mFirstChildHeight = -1;
            this.mSecondChildHeight = -1;
        }

        @Override // com.mobeta.android.dslv.DragSortListView.SmoothAnimator
        public void onStart() {
            this.mFirstChildHeight = -1;
            this.mSecondChildHeight = -1;
            this.mFirstPos = DragSortListView.this.mFirstExpPos;
            this.mSecondPos = DragSortListView.this.mSecondExpPos;
            this.srcPos = DragSortListView.this.mSrcPos;
            DragSortListView.this.mDragState = 1;
            this.mFloatLocX = DragSortListView.this.mFloatLoc.x;
            if (DragSortListView.this.mUseRemoveVelocity) {
                float width = DragSortListView.this.getWidth() * 2.0f;
                if (DragSortListView.this.mRemoveVelocityX == 0.0f) {
                    DragSortListView.this.mRemoveVelocityX = (this.mFloatLocX >= 0.0f ? 1 : -1) * width;
                    return;
                }
                float f = width * 2.0f;
                if (DragSortListView.this.mRemoveVelocityX < 0.0f) {
                    float f2 = -f;
                    if (DragSortListView.this.mRemoveVelocityX > f2) {
                        DragSortListView.this.mRemoveVelocityX = f2;
                        return;
                    }
                }
                if (DragSortListView.this.mRemoveVelocityX <= 0.0f || DragSortListView.this.mRemoveVelocityX >= f) {
                    return;
                }
                DragSortListView.this.mRemoveVelocityX = f;
                return;
            }
            DragSortListView.this.destroyFloatView();
        }

        @Override // com.mobeta.android.dslv.DragSortListView.SmoothAnimator
        public void onUpdate(float f, float f2) {
            View childAt;
            float f3 = 1.0f - f2;
            int firstVisiblePosition = DragSortListView.this.getFirstVisiblePosition();
            View childAt2 = DragSortListView.this.getChildAt(this.mFirstPos - firstVisiblePosition);
            if (DragSortListView.this.mUseRemoveVelocity) {
                float fUptimeMillis = (SystemClock.uptimeMillis() - this.mStartTime) / 1000.0f;
                if (fUptimeMillis == 0.0f) {
                    return;
                }
                float f4 = DragSortListView.this.mRemoveVelocityX * fUptimeMillis;
                int width = DragSortListView.this.getWidth();
                DragSortListView dragSortListView = DragSortListView.this;
                float f5 = (DragSortListView.this.mRemoveVelocityX > 0.0f ? 1 : -1) * fUptimeMillis;
                float f6 = width;
                dragSortListView.mRemoveVelocityX = dragSortListView.mRemoveVelocityX + (f5 * f6);
                this.mFloatLocX += f4;
                DragSortListView.this.mFloatLoc.x = (int) this.mFloatLocX;
                float f7 = this.mFloatLocX;
                if (f7 < f6 && f7 > (-width)) {
                    this.mStartTime = SystemClock.uptimeMillis();
                    DragSortListView.this.doDragFloatView(true);
                    return;
                }
            }
            if (childAt2 != null) {
                if (this.mFirstChildHeight == -1) {
                    this.mFirstChildHeight = DragSortListView.this.getChildHeight(this.mFirstPos, childAt2, false);
                    this.mFirstStartBlank = childAt2.getHeight() - this.mFirstChildHeight;
                }
                int iMax = Math.max((int) (this.mFirstStartBlank * f3), 1);
                ViewGroup.LayoutParams layoutParams = childAt2.getLayoutParams();
                layoutParams.height = this.mFirstChildHeight + iMax;
                childAt2.setLayoutParams(layoutParams);
            }
            int i = this.mSecondPos;
            if (i == this.mFirstPos || (childAt = DragSortListView.this.getChildAt(i - firstVisiblePosition)) == null) {
                return;
            }
            if (this.mSecondChildHeight == -1) {
                this.mSecondChildHeight = DragSortListView.this.getChildHeight(this.mSecondPos, childAt, false);
                this.mSecondStartBlank = childAt.getHeight() - this.mSecondChildHeight;
            }
            int iMax2 = Math.max((int) (f3 * this.mSecondStartBlank), 1);
            ViewGroup.LayoutParams layoutParams2 = childAt.getLayoutParams();
            layoutParams2.height = this.mSecondChildHeight + iMax2;
            childAt.setLayoutParams(layoutParams2);
        }

        @Override // com.mobeta.android.dslv.DragSortListView.SmoothAnimator
        public void onStop() {
            DragSortListView.this.doRemoveItem();
        }
    }

    public void removeItem(int i) {
        this.mUseRemoveVelocity = false;
        removeItem(i, 0.0f);
    }

    public void removeItem(int i, float f) {
        int i2 = this.mDragState;
        if (i2 == 0 || i2 == 4) {
            if (i2 == 0) {
                int headerViewsCount = getHeaderViewsCount() + i;
                this.mSrcPos = headerViewsCount;
                this.mFirstExpPos = headerViewsCount;
                this.mSecondExpPos = headerViewsCount;
                this.mFloatPos = headerViewsCount;
                View childAt = getChildAt(headerViewsCount - getFirstVisiblePosition());
                if (childAt != null) {
                    childAt.setVisibility(4);
                }
            }
            this.mDragState = 1;
            this.mRemoveVelocityX = f;
            if (this.mInTouchEvent) {
                int i3 = this.mCancelMethod;
                if (i3 == 1) {
                    super.onTouchEvent(this.mCancelEvent);
                } else if (i3 == 2) {
                    super.onInterceptTouchEvent(this.mCancelEvent);
                }
            }
            RemoveAnimator removeAnimator = this.mRemoveAnimator;
            if (removeAnimator != null) {
                removeAnimator.start();
            } else {
                doRemoveItem(i);
            }
        }
    }

    public void moveItem(int i, int i2) {
        if (this.mDropListener != null) {
            int count = getInputAdapter().getCount();
            if (i < 0 || i >= count || i2 < 0 || i2 >= count) {
                return;
            }
            this.mDropListener.drop(i, i2);
        }
    }

    public void cancelDrag() {
        if (this.mDragState == 4) {
            this.mDragScroller.stopScrolling(true);
            destroyFloatView();
            clearPositions();
            adjustAllItems();
            if (this.mInTouchEvent) {
                this.mDragState = 3;
            } else {
                this.mDragState = 0;
            }
        }
    }

    private void clearPositions() {
        this.mSrcPos = -1;
        this.mFirstExpPos = -1;
        this.mSecondExpPos = -1;
        this.mFloatPos = -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dropFloatView() {
        int i;
        this.mDragState = 2;
        if (this.mDropListener != null && (i = this.mFloatPos) >= 0 && i < getCount()) {
            int headerViewsCount = getHeaderViewsCount();
            this.mDropListener.drop(this.mSrcPos - headerViewsCount, this.mFloatPos - headerViewsCount);
        }
        destroyFloatView();
        adjustOnReorder();
        clearPositions();
        adjustAllItems();
        if (this.mInTouchEvent) {
            this.mDragState = 3;
        } else {
            this.mDragState = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRemoveItem() {
        doRemoveItem(this.mSrcPos - getHeaderViewsCount());
    }

    private void doRemoveItem(int i) {
        this.mDragState = 1;
        RemoveListener removeListener = this.mRemoveListener;
        if (removeListener != null) {
            removeListener.remove(i);
        }
        destroyFloatView();
        adjustOnReorder();
        clearPositions();
        if (this.mInTouchEvent) {
            this.mDragState = 3;
        } else {
            this.mDragState = 0;
        }
    }

    private void adjustOnReorder() {
        int firstVisiblePosition = getFirstVisiblePosition();
        if (this.mSrcPos < firstVisiblePosition) {
            View childAt = getChildAt(0);
            setSelectionFromTop(firstVisiblePosition - 1, (childAt != null ? childAt.getTop() : 0) - getPaddingTop());
        }
    }

    public boolean stopDrag(boolean z) {
        this.mUseRemoveVelocity = false;
        return stopDrag(z, 0.0f);
    }

    public boolean stopDragWithVelocity(boolean z, float f) {
        this.mUseRemoveVelocity = true;
        return stopDrag(z, f);
    }

    public boolean stopDrag(boolean z, float f) {
        if (this.mFloatView == null) {
            return false;
        }
        this.mDragScroller.stopScrolling(true);
        if (z) {
            removeItem(this.mSrcPos - getHeaderViewsCount(), f);
        } else {
            DropAnimator dropAnimator = this.mDropAnimator;
            if (dropAnimator != null) {
                dropAnimator.start();
            } else {
                dropFloatView();
            }
        }
        if (this.mTrackDragSort) {
            this.mDragSortTracker.stopTracking();
        }
        return true;
    }

    @Override // android.widget.AbsListView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (this.mIgnoreTouchEvent) {
            this.mIgnoreTouchEvent = false;
            return false;
        }
        if (!this.mDragEnabled) {
            return super.onTouchEvent(motionEvent);
        }
        boolean z2 = this.mLastCallWasIntercept;
        this.mLastCallWasIntercept = false;
        if (!z2) {
            saveTouchCoords(motionEvent);
        }
        int i = this.mDragState;
        if (i == 4) {
            onDragTouchEvent(motionEvent);
            return true;
        }
        if (i == 0 && super.onTouchEvent(motionEvent)) {
            z = true;
        }
        int action = motionEvent.getAction() & 255;
        if (action == 1 || action == 3) {
            doActionUpOrCancel();
        } else if (z) {
            this.mCancelMethod = 1;
        }
        return z;
    }

    private void doActionUpOrCancel() {
        this.mCancelMethod = 0;
        this.mInTouchEvent = false;
        if (this.mDragState == 3) {
            this.mDragState = 0;
        }
        this.mCurrFloatAlpha = this.mFloatAlpha;
        this.mListViewIntercepted = false;
        this.mChildHeightCache.clear();
    }

    private void saveTouchCoords(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (action != 0) {
            this.mLastX = this.mX;
            this.mLastY = this.mY;
        }
        this.mX = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        this.mY = y;
        if (action == 0) {
            this.mLastX = this.mX;
            this.mLastY = y;
        }
        this.mOffsetX = ((int) motionEvent.getRawX()) - this.mX;
        this.mOffsetY = ((int) motionEvent.getRawY()) - this.mY;
    }

    public boolean listViewIntercepted() {
        return this.mListViewIntercepted;
    }

    @Override // android.widget.AbsListView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z;
        if (!this.mDragEnabled) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        saveTouchCoords(motionEvent);
        this.mLastCallWasIntercept = true;
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            if (this.mDragState != 0) {
                this.mIgnoreTouchEvent = true;
                return true;
            }
            this.mInTouchEvent = true;
        }
        if (this.mFloatView != null) {
            z = true;
        } else {
            if (super.onInterceptTouchEvent(motionEvent)) {
                this.mListViewIntercepted = true;
                z = true;
            } else {
                z = false;
            }
            if (action == 1 || action == 3) {
                doActionUpOrCancel();
            } else if (z) {
                this.mCancelMethod = 1;
            } else {
                this.mCancelMethod = 2;
            }
        }
        if (action == 1 || action == 3) {
            this.mInTouchEvent = false;
        }
        return z;
    }

    public void setDragScrollStart(float f) {
        setDragScrollStarts(f, f);
    }

    public void setDragScrollStarts(float f, float f2) {
        if (f2 > 0.5f) {
            this.mDragDownScrollStartFrac = 0.5f;
        } else {
            this.mDragDownScrollStartFrac = f2;
        }
        if (f > 0.5f) {
            this.mDragUpScrollStartFrac = 0.5f;
        } else {
            this.mDragUpScrollStartFrac = f;
        }
        if (getHeight() != 0) {
            updateScrollStarts();
        }
    }

    private void continueDrag(int i, int i2) {
        this.mFloatLoc.x = i - this.mDragDeltaX;
        this.mFloatLoc.y = i2 - this.mDragDeltaY;
        doDragFloatView(true);
        int iMin = Math.min(i2, this.mFloatViewMid + this.mFloatViewHeightHalf);
        int iMax = Math.max(i2, this.mFloatViewMid - this.mFloatViewHeightHalf);
        int scrollDir = this.mDragScroller.getScrollDir();
        int i3 = this.mLastY;
        if (iMin > i3 && iMin > this.mDownScrollStartY && scrollDir != 1) {
            if (scrollDir != -1) {
                this.mDragScroller.stopScrolling(true);
            }
            this.mDragScroller.startScrolling(1);
        } else if (iMax < i3 && iMax < this.mUpScrollStartY && scrollDir != 0) {
            if (scrollDir != -1) {
                this.mDragScroller.stopScrolling(true);
            }
            this.mDragScroller.startScrolling(0);
        } else {
            if (iMax < this.mUpScrollStartY || iMin > this.mDownScrollStartY || !this.mDragScroller.isScrolling()) {
                return;
            }
            this.mDragScroller.stopScrolling(true);
        }
    }

    private void updateScrollStarts() {
        int paddingTop = getPaddingTop();
        float height = (getHeight() - paddingTop) - getPaddingBottom();
        float f = paddingTop;
        float f2 = (this.mDragUpScrollStartFrac * height) + f;
        this.mUpScrollStartYF = f2;
        float f3 = ((1.0f - this.mDragDownScrollStartFrac) * height) + f;
        this.mDownScrollStartYF = f3;
        this.mUpScrollStartY = (int) f2;
        this.mDownScrollStartY = (int) f3;
        this.mDragUpScrollHeight = f2 - f;
        this.mDragDownScrollHeight = (paddingTop + r1) - f3;
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updateScrollStarts();
    }

    private void adjustAllItems() {
        int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();
        int iMin = Math.min(lastVisiblePosition - firstVisiblePosition, ((getCount() - 1) - getFooterViewsCount()) - firstVisiblePosition);
        for (int iMax = Math.max(0, getHeaderViewsCount() - firstVisiblePosition); iMax <= iMin; iMax++) {
            View childAt = getChildAt(iMax);
            if (childAt != null) {
                adjustItem(firstVisiblePosition + iMax, childAt, false);
            }
        }
    }

    private void adjustItem(int i) {
        View childAt = getChildAt(i - getFirstVisiblePosition());
        if (childAt != null) {
            adjustItem(i, childAt, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustItem(int i, View view, boolean z) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int iCalcItemHeight = (i == this.mSrcPos || i == this.mFirstExpPos || i == this.mSecondExpPos) ? calcItemHeight(i, view, z) : -2;
        if (iCalcItemHeight != layoutParams.height) {
            layoutParams.height = iCalcItemHeight;
            view.setLayoutParams(layoutParams);
        }
        if (i == this.mFirstExpPos || i == this.mSecondExpPos) {
            int i2 = this.mSrcPos;
            if (i < i2) {
                ((DragSortItemView) view).setGravity(80);
            } else if (i > i2) {
                ((DragSortItemView) view).setGravity(48);
            }
        }
        int visibility = view.getVisibility();
        int i3 = 0;
        if (i == this.mSrcPos && this.mFloatView != null) {
            i3 = 4;
        }
        if (i3 != visibility) {
            view.setVisibility(i3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getChildHeight(int i) {
        View view;
        if (i == this.mSrcPos) {
            return 0;
        }
        View childAt = getChildAt(i - getFirstVisiblePosition());
        if (childAt != null) {
            return getChildHeight(i, childAt, false);
        }
        int i2 = this.mChildHeightCache.get(i);
        if (i2 != -1) {
            return i2;
        }
        ListAdapter adapter = getAdapter();
        int itemViewType = adapter.getItemViewType(i);
        int viewTypeCount = adapter.getViewTypeCount();
        if (viewTypeCount != this.mSampleViewTypes.length) {
            this.mSampleViewTypes = new View[viewTypeCount];
        }
        if (itemViewType >= 0) {
            View[] viewArr = this.mSampleViewTypes;
            if (viewArr[itemViewType] == null) {
                view = adapter.getView(i, null, this);
                this.mSampleViewTypes[itemViewType] = view;
            } else {
                view = adapter.getView(i, viewArr[itemViewType], this);
            }
        } else {
            view = adapter.getView(i, null, this);
        }
        int childHeight = getChildHeight(i, view, true);
        this.mChildHeightCache.add(i, childHeight);
        return childHeight;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getChildHeight(int i, View view, boolean z) {
        if (i == this.mSrcPos) {
            return 0;
        }
        if (i >= getHeaderViewsCount() && i < getCount() - getFooterViewsCount()) {
            view = ((ViewGroup) view).getChildAt(0);
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null && layoutParams.height > 0) {
            return layoutParams.height;
        }
        int height = view.getHeight();
        if (height != 0 && !z) {
            return height;
        }
        measureItem(view);
        return view.getMeasuredHeight();
    }

    private int calcItemHeight(int i, View view, boolean z) {
        return calcItemHeight(i, getChildHeight(i, view, z));
    }

    private int calcItemHeight(int i, int i2) {
        getDividerHeight();
        boolean z = this.mAnimate && this.mFirstExpPos != this.mSecondExpPos;
        int i3 = this.mFloatViewHeight;
        int i4 = this.mItemHeightCollapsed;
        int i5 = i3 - i4;
        int i6 = (int) (this.mSlideFrac * i5);
        int i7 = this.mSrcPos;
        return i == i7 ? i7 == this.mFirstExpPos ? z ? i6 + i4 : i3 : i7 == this.mSecondExpPos ? i3 - i6 : i4 : i == this.mFirstExpPos ? z ? i2 + i6 : i2 + i5 : i == this.mSecondExpPos ? (i2 + i5) - i6 : i2;
    }

    @Override // android.widget.AbsListView, android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.mBlockLayoutRequests) {
            return;
        }
        super.requestLayout();
    }

    private int adjustScroll(int i, View view, int i2, int i3) {
        int i4;
        int i5;
        int childHeight = getChildHeight(i);
        int height = view.getHeight();
        int iCalcItemHeight = calcItemHeight(i, childHeight);
        int i6 = this.mSrcPos;
        if (i != i6) {
            i4 = height - childHeight;
            i5 = iCalcItemHeight - childHeight;
        } else {
            i4 = height;
            i5 = iCalcItemHeight;
        }
        int i7 = this.mFloatViewHeight;
        int i8 = this.mFirstExpPos;
        if (i6 != i8 && i6 != this.mSecondExpPos) {
            i7 -= this.mItemHeightCollapsed;
        }
        if (i <= i2) {
            if (i > i8) {
                return 0 + (i7 - i5);
            }
            return 0;
        }
        if (i == i3) {
            if (i <= i8) {
                i4 -= i7;
            } else if (i == this.mSecondExpPos) {
                return 0 + (height - iCalcItemHeight);
            }
            return 0 + i4;
        }
        if (i <= i8) {
            return 0 - i7;
        }
        if (i == this.mSecondExpPos) {
            return 0 - i5;
        }
        return 0;
    }

    private void measureItem(View view) {
        int iMakeMeasureSpec;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new AbsListView.LayoutParams(-1, -2);
            view.setLayoutParams(layoutParams);
        }
        int childMeasureSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, getListPaddingLeft() + getListPaddingRight(), layoutParams.width);
        if (layoutParams.height > 0) {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824);
        } else {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        view.measure(childMeasureSpec, iMakeMeasureSpec);
    }

    private void measureFloatView() {
        View view = this.mFloatView;
        if (view != null) {
            measureItem(view);
            int measuredHeight = this.mFloatView.getMeasuredHeight();
            this.mFloatViewHeight = measuredHeight;
            this.mFloatViewHeightHalf = measuredHeight / 2;
        }
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        View view = this.mFloatView;
        if (view != null) {
            if (view.isLayoutRequested()) {
                measureFloatView();
            }
            this.mFloatViewOnMeasured = true;
        }
        this.mWidthMeasureSpec = i;
    }

    @Override // android.widget.ListView, android.widget.AbsListView
    protected void layoutChildren() {
        super.layoutChildren();
        View view = this.mFloatView;
        if (view != null) {
            if (view.isLayoutRequested() && !this.mFloatViewOnMeasured) {
                measureFloatView();
            }
            View view2 = this.mFloatView;
            view2.layout(0, 0, view2.getMeasuredWidth(), this.mFloatView.getMeasuredHeight());
            this.mFloatViewOnMeasured = false;
        }
    }

    protected boolean onDragTouchEvent(MotionEvent motionEvent) {
        motionEvent.getAction();
        int action = motionEvent.getAction() & 255;
        if (action == 1) {
            if (this.mDragState == 4) {
                stopDrag(false);
            }
            doActionUpOrCancel();
        } else if (action == 2) {
            continueDrag((int) motionEvent.getX(), (int) motionEvent.getY());
        } else if (action == 3) {
            if (this.mDragState == 4) {
                cancelDrag();
            }
            doActionUpOrCancel();
        }
        return true;
    }

    private void invalidateFloatView() {
        this.mFloatViewInvalidated = true;
    }

    public boolean startDrag(int i, int i2, int i3, int i4) {
        FloatViewManager floatViewManager;
        View viewOnCreateFloatView;
        if (!this.mInTouchEvent || (floatViewManager = this.mFloatViewManager) == null || (viewOnCreateFloatView = floatViewManager.onCreateFloatView(i)) == null) {
            return false;
        }
        return startDrag(i, viewOnCreateFloatView, i2, i3, i4);
    }

    public boolean startDrag(int i, View view, int i2, int i3, int i4) {
        if (this.mDragState != 0 || !this.mInTouchEvent || this.mFloatView != null || view == null || !this.mDragEnabled) {
            return false;
        }
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        int headerViewsCount = i + getHeaderViewsCount();
        this.mFirstExpPos = headerViewsCount;
        this.mSecondExpPos = headerViewsCount;
        this.mSrcPos = headerViewsCount;
        this.mFloatPos = headerViewsCount;
        this.mDragState = 4;
        this.mDragFlags = 0;
        this.mDragFlags = i2 | 0;
        this.mFloatView = view;
        measureFloatView();
        this.mDragDeltaX = i3;
        this.mDragDeltaY = i4;
        this.mDragStartY = this.mY;
        this.mFloatLoc.x = this.mX - i3;
        this.mFloatLoc.y = this.mY - this.mDragDeltaY;
        View childAt = getChildAt(this.mSrcPos - getFirstVisiblePosition());
        if (childAt != null) {
            childAt.setVisibility(4);
        }
        if (this.mTrackDragSort) {
            this.mDragSortTracker.startTracking();
        }
        int i5 = this.mCancelMethod;
        if (i5 == 1) {
            super.onTouchEvent(this.mCancelEvent);
        } else if (i5 == 2) {
            super.onInterceptTouchEvent(this.mCancelEvent);
        }
        requestLayout();
        LiftAnimator liftAnimator = this.mLiftAnimator;
        if (liftAnimator != null) {
            liftAnimator.start();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDragFloatView(boolean z) {
        int firstVisiblePosition = getFirstVisiblePosition() + (getChildCount() / 2);
        View childAt = getChildAt(getChildCount() / 2);
        if (childAt == null) {
            return;
        }
        doDragFloatView(firstVisiblePosition, childAt, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDragFloatView(int i, View view, boolean z) {
        this.mBlockLayoutRequests = true;
        updateFloatView();
        int i2 = this.mFirstExpPos;
        int i3 = this.mSecondExpPos;
        boolean zUpdatePositions = updatePositions();
        if (zUpdatePositions) {
            adjustAllItems();
            setSelectionFromTop(i, (view.getTop() + adjustScroll(i, view, i2, i3)) - getPaddingTop());
            layoutChildren();
        }
        if (zUpdatePositions || z) {
            invalidate();
        }
        this.mBlockLayoutRequests = false;
    }

    private void updateFloatView() {
        int i;
        int i2;
        if (this.mFloatViewManager != null) {
            this.mTouchLoc.set(this.mX, this.mY);
            this.mFloatViewManager.onDragFloatView(this.mFloatView, this.mFloatLoc, this.mTouchLoc);
        }
        int i3 = this.mFloatLoc.x;
        int i4 = this.mFloatLoc.y;
        int paddingLeft = getPaddingLeft();
        int i5 = this.mDragFlags;
        if ((i5 & 1) == 0 && i3 > paddingLeft) {
            this.mFloatLoc.x = paddingLeft;
        } else if ((i5 & 2) == 0 && i3 < paddingLeft) {
            this.mFloatLoc.x = paddingLeft;
        }
        int headerViewsCount = getHeaderViewsCount();
        int footerViewsCount = getFooterViewsCount();
        int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();
        int paddingTop = getPaddingTop();
        if (firstVisiblePosition < headerViewsCount) {
            paddingTop = getChildAt((headerViewsCount - firstVisiblePosition) - 1).getBottom();
        }
        if ((this.mDragFlags & 8) == 0 && firstVisiblePosition <= (i2 = this.mSrcPos)) {
            paddingTop = Math.max(getChildAt(i2 - firstVisiblePosition).getTop(), paddingTop);
        }
        int height = getHeight() - getPaddingBottom();
        if (lastVisiblePosition >= (getCount() - footerViewsCount) - 1) {
            height = getChildAt(((getCount() - footerViewsCount) - 1) - firstVisiblePosition).getBottom();
        }
        if ((this.mDragFlags & 4) == 0 && lastVisiblePosition >= (i = this.mSrcPos)) {
            height = Math.min(getChildAt(i - firstVisiblePosition).getBottom(), height);
        }
        if (i4 < paddingTop) {
            this.mFloatLoc.y = paddingTop;
        } else {
            int i6 = this.mFloatViewHeight;
            if (i4 + i6 > height) {
                this.mFloatLoc.y = height - i6;
            }
        }
        this.mFloatViewMid = this.mFloatLoc.y + this.mFloatViewHeightHalf;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void destroyFloatView() {
        View view = this.mFloatView;
        if (view != null) {
            view.setVisibility(8);
            FloatViewManager floatViewManager = this.mFloatViewManager;
            if (floatViewManager != null) {
                floatViewManager.onDestroyFloatView(this.mFloatView);
            }
            this.mFloatView = null;
            invalidate();
        }
    }

    public void setFloatViewManager(FloatViewManager floatViewManager) {
        this.mFloatViewManager = floatViewManager;
    }

    public void setDragListener(DragListener dragListener) {
        this.mDragListener = dragListener;
    }

    public void setDragEnabled(boolean z) {
        this.mDragEnabled = z;
    }

    public boolean isDragEnabled() {
        return this.mDragEnabled;
    }

    public void setDropListener(DropListener dropListener) {
        this.mDropListener = dropListener;
    }

    public void setRemoveListener(RemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

    public void setDragSortListener(DragSortListener dragSortListener) {
        setDropListener(dragSortListener);
        setDragListener(dragSortListener);
        setRemoveListener(dragSortListener);
    }

    public void setDragScrollProfile(DragScrollProfile dragScrollProfile) {
        if (dragScrollProfile != null) {
            this.mScrollProfile = dragScrollProfile;
        }
    }

    public void moveCheckState(int i, int i2) {
        int i3;
        int i4;
        SparseBooleanArray checkedItemPositions = getCheckedItemPositions();
        if (i2 < i) {
            i4 = i;
            i3 = i2;
        } else {
            i3 = i;
            i4 = i2;
        }
        int i5 = i4 + 1;
        int[] iArr = new int[checkedItemPositions.size()];
        int[] iArr2 = new int[checkedItemPositions.size()];
        int iBuildRunList = buildRunList(checkedItemPositions, i3, i5, iArr, iArr2);
        if (iBuildRunList == 1 && iArr[0] == iArr2[0]) {
            return;
        }
        if (i < i2) {
            for (int i6 = 0; i6 != iBuildRunList; i6++) {
                setItemChecked(rotate(iArr[i6], -1, i3, i5), true);
                setItemChecked(rotate(iArr2[i6], -1, i3, i5), false);
            }
            return;
        }
        for (int i7 = 0; i7 != iBuildRunList; i7++) {
            setItemChecked(iArr[i7], false);
            setItemChecked(iArr2[i7], true);
        }
    }

    public void removeCheckState(int i) {
        SparseBooleanArray checkedItemPositions = getCheckedItemPositions();
        if (checkedItemPositions.size() == 0) {
            return;
        }
        int[] iArr = new int[checkedItemPositions.size()];
        int[] iArr2 = new int[checkedItemPositions.size()];
        int iKeyAt = checkedItemPositions.keyAt(checkedItemPositions.size() - 1) + 1;
        int iBuildRunList = buildRunList(checkedItemPositions, i, iKeyAt, iArr, iArr2);
        for (int i2 = 0; i2 != iBuildRunList; i2++) {
            if (iArr[i2] != i && (iArr2[i2] >= iArr[i2] || iArr2[i2] <= i)) {
                setItemChecked(rotate(iArr[i2], -1, i, iKeyAt), true);
            }
            setItemChecked(rotate(iArr2[i2], -1, i, iKeyAt), false);
        }
    }

    private static int buildRunList(SparseBooleanArray sparseBooleanArray, int i, int i2, int[] iArr, int[] iArr2) {
        int iKeyAt;
        int iFindFirstSetIndex = findFirstSetIndex(sparseBooleanArray, i, i2);
        if (iFindFirstSetIndex == -1) {
            return 0;
        }
        int iKeyAt2 = sparseBooleanArray.keyAt(iFindFirstSetIndex);
        int i3 = iKeyAt2 + 1;
        int i4 = 0;
        for (int i5 = iFindFirstSetIndex + 1; i5 < sparseBooleanArray.size() && (iKeyAt = sparseBooleanArray.keyAt(i5)) < i2; i5++) {
            if (sparseBooleanArray.valueAt(i5)) {
                if (iKeyAt == i3) {
                    i3++;
                } else {
                    iArr[i4] = iKeyAt2;
                    iArr2[i4] = i3;
                    i4++;
                    i3 = iKeyAt + 1;
                    iKeyAt2 = iKeyAt;
                }
            }
        }
        if (i3 == i2) {
            i3 = i;
        }
        iArr[i4] = iKeyAt2;
        iArr2[i4] = i3;
        int i6 = i4 + 1;
        if (i6 <= 1 || iArr[0] != i) {
            return i6;
        }
        int i7 = i6 - 1;
        if (iArr2[i7] != i) {
            return i6;
        }
        iArr[0] = iArr[i7];
        return i6 - 1;
    }

    private static int findFirstSetIndex(SparseBooleanArray sparseBooleanArray, int i, int i2) {
        int size = sparseBooleanArray.size();
        int iInsertionIndexForKey = insertionIndexForKey(sparseBooleanArray, i);
        while (iInsertionIndexForKey < size && sparseBooleanArray.keyAt(iInsertionIndexForKey) < i2 && !sparseBooleanArray.valueAt(iInsertionIndexForKey)) {
            iInsertionIndexForKey++;
        }
        if (iInsertionIndexForKey == size || sparseBooleanArray.keyAt(iInsertionIndexForKey) >= i2) {
            return -1;
        }
        return iInsertionIndexForKey;
    }

    private static int insertionIndexForKey(SparseBooleanArray sparseBooleanArray, int i) {
        int size = sparseBooleanArray.size();
        int i2 = 0;
        while (size - i2 > 0) {
            int i3 = (i2 + size) >> 1;
            if (sparseBooleanArray.keyAt(i3) < i) {
                i2 = i3 + 1;
            } else {
                size = i3;
            }
        }
        return i2;
    }

    private class DragScroller implements Runnable {
        public static final int DOWN = 1;
        public static final int STOP = -1;
        public static final int UP = 0;
        private float dt;
        private int dy;
        private boolean mAbort;
        private long mCurrTime;
        private int mFirstFooter;
        private int mLastHeader;
        private long mPrevTime;
        private float mScrollSpeed;
        private boolean mScrolling = false;
        private int scrollDir;
        private long tStart;

        public boolean isScrolling() {
            return this.mScrolling;
        }

        public int getScrollDir() {
            if (this.mScrolling) {
                return this.scrollDir;
            }
            return -1;
        }

        public DragScroller() {
        }

        public void startScrolling(int i) {
            if (this.mScrolling) {
                return;
            }
            this.mAbort = false;
            this.mScrolling = true;
            long jUptimeMillis = SystemClock.uptimeMillis();
            this.tStart = jUptimeMillis;
            this.mPrevTime = jUptimeMillis;
            this.scrollDir = i;
            DragSortListView.this.post(this);
        }

        public void stopScrolling(boolean z) {
            if (z) {
                DragSortListView.this.removeCallbacks(this);
                this.mScrolling = false;
            } else {
                this.mAbort = true;
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mAbort) {
                this.mScrolling = false;
                return;
            }
            int firstVisiblePosition = DragSortListView.this.getFirstVisiblePosition();
            int lastVisiblePosition = DragSortListView.this.getLastVisiblePosition();
            int count = DragSortListView.this.getCount();
            int paddingTop = DragSortListView.this.getPaddingTop();
            int height = (DragSortListView.this.getHeight() - paddingTop) - DragSortListView.this.getPaddingBottom();
            int iMin = Math.min(DragSortListView.this.mY, DragSortListView.this.mFloatViewMid + DragSortListView.this.mFloatViewHeightHalf);
            int iMax = Math.max(DragSortListView.this.mY, DragSortListView.this.mFloatViewMid - DragSortListView.this.mFloatViewHeightHalf);
            if (this.scrollDir == 0) {
                View childAt = DragSortListView.this.getChildAt(0);
                if (childAt == null) {
                    this.mScrolling = false;
                    return;
                } else {
                    if (firstVisiblePosition == 0 && childAt.getTop() == paddingTop) {
                        this.mScrolling = false;
                        return;
                    }
                    this.mScrollSpeed = DragSortListView.this.mScrollProfile.getSpeed((DragSortListView.this.mUpScrollStartYF - iMax) / DragSortListView.this.mDragUpScrollHeight, this.mPrevTime);
                }
            } else {
                View childAt2 = DragSortListView.this.getChildAt(lastVisiblePosition - firstVisiblePosition);
                if (childAt2 == null) {
                    this.mScrolling = false;
                    return;
                } else {
                    if (lastVisiblePosition == count - 1 && childAt2.getBottom() <= height + paddingTop) {
                        this.mScrolling = false;
                        return;
                    }
                    this.mScrollSpeed = -DragSortListView.this.mScrollProfile.getSpeed((iMin - DragSortListView.this.mDownScrollStartYF) / DragSortListView.this.mDragDownScrollHeight, this.mPrevTime);
                }
            }
            long jUptimeMillis = SystemClock.uptimeMillis();
            this.mCurrTime = jUptimeMillis;
            float f = jUptimeMillis - this.mPrevTime;
            this.dt = f;
            int iRound = Math.round(this.mScrollSpeed * f);
            this.dy = iRound;
            if (iRound >= 0) {
                this.dy = Math.min(height, iRound);
                lastVisiblePosition = firstVisiblePosition;
            } else {
                this.dy = Math.max(-height, iRound);
            }
            View childAt3 = DragSortListView.this.getChildAt(lastVisiblePosition - firstVisiblePosition);
            int top = childAt3.getTop() + this.dy;
            if (lastVisiblePosition == 0 && top > paddingTop) {
                top = paddingTop;
            }
            DragSortListView.this.mBlockLayoutRequests = true;
            DragSortListView.this.setSelectionFromTop(lastVisiblePosition, top - paddingTop);
            DragSortListView.this.layoutChildren();
            DragSortListView.this.invalidate();
            DragSortListView.this.mBlockLayoutRequests = false;
            DragSortListView.this.doDragFloatView(lastVisiblePosition, childAt3, false);
            this.mPrevTime = this.mCurrTime;
            DragSortListView.this.post(this);
        }
    }

    protected class DragSortTracker {
        File mFile;
        StringBuilder mBuilder = new StringBuilder();
        private int mNumInBuffer = 0;
        private int mNumFlushes = 0;
        private boolean mTracking = false;

        public DragSortTracker() {
            File file = new File(Environment.getExternalStorageDirectory(), "dslv_state.txt");
            this.mFile = file;
            if (file.exists()) {
                return;
            }
            try {
                this.mFile.createNewFile();
                Log.d("mobeta", "file created");
            } catch (IOException e) {
                Log.w("mobeta", "Could not create dslv_state.txt");
                Log.d("mobeta", e.getMessage());
            }
        }

        public void startTracking() {
            this.mBuilder.append("<DSLVStates>\n");
            this.mNumFlushes = 0;
            this.mTracking = true;
        }

        public void appendState() {
            if (this.mTracking) {
                this.mBuilder.append("<DSLVState>\n");
                int childCount = DragSortListView.this.getChildCount();
                int firstVisiblePosition = DragSortListView.this.getFirstVisiblePosition();
                this.mBuilder.append("    <Positions>");
                for (int i = 0; i < childCount; i++) {
                    this.mBuilder.append(firstVisiblePosition + i).append(",");
                }
                this.mBuilder.append("</Positions>\n");
                this.mBuilder.append("    <Tops>");
                for (int i2 = 0; i2 < childCount; i2++) {
                    this.mBuilder.append(DragSortListView.this.getChildAt(i2).getTop()).append(",");
                }
                this.mBuilder.append("</Tops>\n");
                this.mBuilder.append("    <Bottoms>");
                for (int i3 = 0; i3 < childCount; i3++) {
                    this.mBuilder.append(DragSortListView.this.getChildAt(i3).getBottom()).append(",");
                }
                this.mBuilder.append("</Bottoms>\n");
                this.mBuilder.append("    <FirstExpPos>").append(DragSortListView.this.mFirstExpPos).append("</FirstExpPos>\n");
                StringBuilder sbAppend = this.mBuilder.append("    <FirstExpBlankHeight>");
                DragSortListView dragSortListView = DragSortListView.this;
                int itemHeight = dragSortListView.getItemHeight(dragSortListView.mFirstExpPos);
                DragSortListView dragSortListView2 = DragSortListView.this;
                sbAppend.append(itemHeight - dragSortListView2.getChildHeight(dragSortListView2.mFirstExpPos)).append("</FirstExpBlankHeight>\n");
                this.mBuilder.append("    <SecondExpPos>").append(DragSortListView.this.mSecondExpPos).append("</SecondExpPos>\n");
                StringBuilder sbAppend2 = this.mBuilder.append("    <SecondExpBlankHeight>");
                DragSortListView dragSortListView3 = DragSortListView.this;
                int itemHeight2 = dragSortListView3.getItemHeight(dragSortListView3.mSecondExpPos);
                DragSortListView dragSortListView4 = DragSortListView.this;
                sbAppend2.append(itemHeight2 - dragSortListView4.getChildHeight(dragSortListView4.mSecondExpPos)).append("</SecondExpBlankHeight>\n");
                this.mBuilder.append("    <SrcPos>").append(DragSortListView.this.mSrcPos).append("</SrcPos>\n");
                this.mBuilder.append("    <SrcHeight>").append(DragSortListView.this.mFloatViewHeight + DragSortListView.this.getDividerHeight()).append("</SrcHeight>\n");
                this.mBuilder.append("    <ViewHeight>").append(DragSortListView.this.getHeight()).append("</ViewHeight>\n");
                this.mBuilder.append("    <LastY>").append(DragSortListView.this.mLastY).append("</LastY>\n");
                this.mBuilder.append("    <FloatY>").append(DragSortListView.this.mFloatViewMid).append("</FloatY>\n");
                this.mBuilder.append("    <ShuffleEdges>");
                for (int i4 = 0; i4 < childCount; i4++) {
                    StringBuilder sb = this.mBuilder;
                    DragSortListView dragSortListView5 = DragSortListView.this;
                    sb.append(dragSortListView5.getShuffleEdge(firstVisiblePosition + i4, dragSortListView5.getChildAt(i4).getTop())).append(",");
                }
                this.mBuilder.append("</ShuffleEdges>\n");
                this.mBuilder.append("</DSLVState>\n");
                int i5 = this.mNumInBuffer + 1;
                this.mNumInBuffer = i5;
                if (i5 > 1000) {
                    flush();
                    this.mNumInBuffer = 0;
                }
            }
        }

        public void flush() {
            if (this.mTracking) {
                try {
                    FileWriter fileWriter = new FileWriter(this.mFile, this.mNumFlushes != 0);
                    fileWriter.write(this.mBuilder.toString());
                    StringBuilder sb = this.mBuilder;
                    sb.delete(0, sb.length());
                    fileWriter.flush();
                    fileWriter.close();
                    this.mNumFlushes++;
                } catch (IOException unused) {
                }
            }
        }

        public void stopTracking() {
            if (this.mTracking) {
                this.mBuilder.append("</DSLVStates>\n");
                flush();
                this.mTracking = false;
            }
        }
    }
}
