package com.mobeta.android.dslv;

import android.graphics.Point;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/* JADX INFO: loaded from: classes2.dex */
public class DragSortController extends SimpleFloatViewManager implements View.OnTouchListener, GestureDetector.OnGestureListener {
    public static final int CLICK_REMOVE = 0;
    public static final int FLING_REMOVE = 1;
    public static final int MISS = -1;
    public static final int ON_DOWN = 0;
    public static final int ON_DRAG = 1;
    public static final int ON_LONG_PRESS = 2;
    private static final String TAG = "DragSortController";
    private boolean mCanDrag;
    private int mClickRemoveHitPos;
    private int mClickRemoveId;
    private int mCurrX;
    private int mCurrY;
    private GestureDetector mDetector;
    private int mDragHandleId;
    private int mDragInitMode;
    private boolean mDragging;
    private DragSortListView mDslv;
    private int mFlingHandleId;
    private int mFlingHitPos;
    private GestureDetector mFlingRemoveDetector;
    private GestureDetector.OnGestureListener mFlingRemoveListener;
    private float mFlingSpeed;
    private int mHitPos;
    private boolean mIsRemoving;
    private int mItemX;
    private int mItemY;
    private int mPositionX;
    private boolean mRemoveEnabled;
    private int mRemoveMode;
    private boolean mSortEnabled;
    private int[] mTempLoc;
    private int mTouchSlop;

    @Override // android.view.GestureDetector.OnGestureListener
    public final boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent motionEvent) {
    }

    public DragSortController(DragSortListView dragSortListView) {
        this(dragSortListView, 0, 0, 1);
    }

    public DragSortController(DragSortListView dragSortListView, int i, int i2, int i3) {
        this(dragSortListView, i, i2, i3, 0);
    }

    public DragSortController(DragSortListView dragSortListView, int i, int i2, int i3, int i4) {
        this(dragSortListView, i, i2, i3, i4, 0);
    }

    public DragSortController(DragSortListView dragSortListView, int i, int i2, int i3, int i4, int i5) {
        super(dragSortListView);
        this.mDragInitMode = 0;
        this.mSortEnabled = true;
        this.mRemoveEnabled = false;
        this.mIsRemoving = false;
        this.mHitPos = -1;
        this.mFlingHitPos = -1;
        this.mClickRemoveHitPos = -1;
        this.mTempLoc = new int[2];
        this.mDragging = false;
        this.mFlingSpeed = 500.0f;
        this.mFlingRemoveListener = new GestureDetector.SimpleOnGestureListener() { // from class: com.mobeta.android.dslv.DragSortController.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public final boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (DragSortController.this.mRemoveEnabled && DragSortController.this.mIsRemoving) {
                    int width = DragSortController.this.mDslv.getWidth() / 5;
                    if (f > DragSortController.this.mFlingSpeed) {
                        if (DragSortController.this.mPositionX > (-width)) {
                            DragSortController.this.mDslv.stopDragWithVelocity(true, f);
                        }
                    } else if (f < (-DragSortController.this.mFlingSpeed) && DragSortController.this.mPositionX < width) {
                        DragSortController.this.mDslv.stopDragWithVelocity(true, f);
                    }
                    DragSortController.this.mIsRemoving = false;
                }
                return false;
            }
        };
        this.mDslv = dragSortListView;
        this.mDetector = new GestureDetector(dragSortListView.getContext(), this);
        GestureDetector gestureDetector = new GestureDetector(dragSortListView.getContext(), this.mFlingRemoveListener);
        this.mFlingRemoveDetector = gestureDetector;
        gestureDetector.setIsLongpressEnabled(false);
        this.mTouchSlop = ViewConfiguration.get(dragSortListView.getContext()).getScaledTouchSlop();
        this.mDragHandleId = i;
        this.mClickRemoveId = i4;
        this.mFlingHandleId = i5;
        setRemoveMode(i3);
        setDragInitMode(i2);
    }

    public int getDragInitMode() {
        return this.mDragInitMode;
    }

    public void setDragInitMode(int i) {
        this.mDragInitMode = i;
    }

    public void setSortEnabled(boolean z) {
        this.mSortEnabled = z;
    }

    public boolean isSortEnabled() {
        return this.mSortEnabled;
    }

    public void setRemoveMode(int i) {
        this.mRemoveMode = i;
    }

    public int getRemoveMode() {
        return this.mRemoveMode;
    }

    public void setRemoveEnabled(boolean z) {
        this.mRemoveEnabled = z;
    }

    public boolean isRemoveEnabled() {
        return this.mRemoveEnabled;
    }

    public void setDragHandleId(int i) {
        this.mDragHandleId = i;
    }

    public void setFlingHandleId(int i) {
        this.mFlingHandleId = i;
    }

    public void setClickRemoveId(int i) {
        this.mClickRemoveId = i;
    }

    public boolean startDrag(int i, int i2, int i3) {
        int i4 = (!this.mSortEnabled || this.mIsRemoving) ? 0 : 12;
        if (this.mRemoveEnabled && this.mIsRemoving) {
            i4 = i4 | 1 | 2;
        }
        DragSortListView dragSortListView = this.mDslv;
        boolean zStartDrag = dragSortListView.startDrag(i - dragSortListView.getHeaderViewsCount(), i4, i2, i3);
        this.mDragging = zStartDrag;
        return zStartDrag;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (this.mDslv.isDragEnabled() && !this.mDslv.listViewIntercepted()) {
            this.mDetector.onTouchEvent(motionEvent);
            if (this.mRemoveEnabled && this.mDragging && this.mRemoveMode == 1) {
                this.mFlingRemoveDetector.onTouchEvent(motionEvent);
            }
            int action = motionEvent.getAction() & 255;
            if (action == 0) {
                this.mCurrX = (int) motionEvent.getX();
                this.mCurrY = (int) motionEvent.getY();
            } else {
                if (action != 1) {
                    if (action == 3) {
                    }
                } else if (this.mRemoveEnabled && this.mIsRemoving) {
                    int i = this.mPositionX;
                    if (i < 0) {
                        i = -i;
                    }
                    if (i > this.mDslv.getWidth() / 2) {
                        this.mDslv.stopDragWithVelocity(true, 0.0f);
                    }
                }
                this.mIsRemoving = false;
                this.mDragging = false;
            }
        }
        return false;
    }

    @Override // com.mobeta.android.dslv.SimpleFloatViewManager, com.mobeta.android.dslv.DragSortListView.FloatViewManager
    public void onDragFloatView(View view, Point point, Point point2) {
        if (this.mRemoveEnabled && this.mIsRemoving) {
            this.mPositionX = point.x;
        }
    }

    public int startDragPosition(MotionEvent motionEvent) {
        return dragHandleHitPosition(motionEvent);
    }

    public int startFlingPosition(MotionEvent motionEvent) {
        if (this.mRemoveMode == 1) {
            return flingHandleHitPosition(motionEvent);
        }
        return -1;
    }

    public int dragHandleHitPosition(MotionEvent motionEvent) {
        return viewIdHitPosition(motionEvent, this.mDragHandleId);
    }

    public int flingHandleHitPosition(MotionEvent motionEvent) {
        return viewIdHitPosition(motionEvent, this.mFlingHandleId);
    }

    public int viewIdHitPosition(MotionEvent motionEvent, int i) {
        int iPointToPosition = this.mDslv.pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
        int headerViewsCount = this.mDslv.getHeaderViewsCount();
        int footerViewsCount = this.mDslv.getFooterViewsCount();
        int count = this.mDslv.getCount();
        if (iPointToPosition != -1 && iPointToPosition >= headerViewsCount && iPointToPosition < count - footerViewsCount) {
            DragSortListView dragSortListView = this.mDslv;
            View childAt = dragSortListView.getChildAt(iPointToPosition - dragSortListView.getFirstVisiblePosition());
            int rawX = (int) motionEvent.getRawX();
            int rawY = (int) motionEvent.getRawY();
            View viewFindViewById = i == 0 ? childAt : childAt.findViewById(i);
            if (viewFindViewById != null) {
                viewFindViewById.getLocationOnScreen(this.mTempLoc);
                int[] iArr = this.mTempLoc;
                if (rawX > iArr[0] && rawY > iArr[1] && rawX < iArr[0] + viewFindViewById.getWidth() && rawY < this.mTempLoc[1] + viewFindViewById.getHeight()) {
                    this.mItemX = childAt.getLeft();
                    this.mItemY = childAt.getTop();
                    return iPointToPosition;
                }
            }
        }
        return -1;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent motionEvent) {
        if (this.mRemoveEnabled && this.mRemoveMode == 0) {
            this.mClickRemoveHitPos = viewIdHitPosition(motionEvent, this.mClickRemoveId);
        }
        int iStartDragPosition = startDragPosition(motionEvent);
        this.mHitPos = iStartDragPosition;
        if (iStartDragPosition != -1 && this.mDragInitMode == 0) {
            startDrag(iStartDragPosition, ((int) motionEvent.getX()) - this.mItemX, ((int) motionEvent.getY()) - this.mItemY);
        }
        this.mIsRemoving = false;
        this.mCanDrag = true;
        this.mPositionX = 0;
        this.mFlingHitPos = startFlingPosition(motionEvent);
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        int i;
        if (motionEvent == null || motionEvent2 == null) {
            Log.e(TAG, "onScroll() MotionEvent is null e1:" + motionEvent + " e2:" + motionEvent2);
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int x2 = (int) motionEvent2.getX();
        int y2 = (int) motionEvent2.getY();
        int i2 = x2 - this.mItemX;
        int i3 = y2 - this.mItemY;
        if (this.mCanDrag && !this.mDragging && ((i = this.mHitPos) != -1 || this.mFlingHitPos != -1)) {
            if (i != -1) {
                if (this.mDragInitMode == 1 && Math.abs(y2 - y) > this.mTouchSlop && this.mSortEnabled) {
                    startDrag(this.mHitPos, i2, i3);
                } else if (this.mDragInitMode != 0 && Math.abs(x2 - x) > this.mTouchSlop && this.mRemoveEnabled) {
                    this.mIsRemoving = true;
                    startDrag(this.mFlingHitPos, i2, i3);
                }
            } else if (this.mFlingHitPos != -1) {
                if (Math.abs(x2 - x) > this.mTouchSlop && this.mRemoveEnabled) {
                    this.mIsRemoving = true;
                    startDrag(this.mFlingHitPos, i2, i3);
                } else if (Math.abs(y2 - y) > this.mTouchSlop) {
                    this.mCanDrag = false;
                }
            }
        }
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent motionEvent) {
        if (this.mHitPos == -1 || this.mDragInitMode != 2) {
            return;
        }
        this.mDslv.performHapticFeedback(0);
        startDrag(this.mHitPos, this.mCurrX - this.mItemX, this.mCurrY - this.mItemY);
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        int i;
        if (!this.mRemoveEnabled || this.mRemoveMode != 0 || (i = this.mClickRemoveHitPos) == -1) {
            return true;
        }
        DragSortListView dragSortListView = this.mDslv;
        dragSortListView.removeItem(i - dragSortListView.getHeaderViewsCount());
        return true;
    }
}
