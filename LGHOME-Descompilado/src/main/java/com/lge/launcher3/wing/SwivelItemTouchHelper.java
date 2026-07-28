package com.lge.launcher3.wing;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchUIUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class SwivelItemTouchHelper extends RecyclerView.ItemDecoration implements RecyclerView.OnChildAttachStateChangeListener {
    static final int ACTION_MODE_DRAG_MASK = 16711680;
    private static final int ACTION_MODE_IDLE_MASK = 255;
    static final int ACTION_MODE_SWIPE_MASK = 65280;
    public static final int ACTION_STATE_DRAG = 2;
    public static final int ACTION_STATE_IDLE = 0;
    public static final int ACTION_STATE_SWIPE = 1;
    private static final int ACTIVE_POINTER_ID_NONE = -1;
    public static final int ANIMATION_TYPE_DRAG = 8;
    public static final int ANIMATION_TYPE_SWIPE_CANCEL = 4;
    public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 2;
    private static final boolean DEBUG = false;
    static final int DIRECTION_FLAG_COUNT = 8;
    public static final int DOWN = 2;
    public static final int END = 32;
    public static final int LEFT = 4;
    private static final int PIXELS_PER_SECOND = 1000;
    public static final int RIGHT = 8;
    public static final int START = 16;
    private static final String TAG = "SwivelItemTouchHelper";
    public static final int UP = 1;
    Callback mCallback;
    private List<Integer> mDistances;
    private long mDragScrollStartTimeInMs;
    float mDx;
    float mDy;
    GestureDetectorCompat mGestureDetector;
    float mInitialTouchX;
    float mInitialTouchY;
    public ItemTouchHelperGestureListener mItemTouchHelperGestureListener;
    private float mMaxSwipeVelocity;
    RecyclerView mRecyclerView;
    int mSelectedFlags;
    private float mSelectedStartX;
    private float mSelectedStartY;
    private int mSlop;
    private List<RecyclerView.ViewHolder> mSwapTargets;
    private float mSwipeEscapeVelocity;
    private Rect mTmpRect;
    VelocityTracker mVelocityTracker;
    final List<View> mPendingCleanup = new ArrayList();
    private final float[] mTmpPosition = new float[2];
    RecyclerView.ViewHolder mSelected = null;
    int mActivePointerId = -1;
    private int mActionState = 0;
    List<RecoverAnimation> mRecoverAnimations = new ArrayList();
    final Runnable mScrollRunnable = new Runnable() { // from class: com.lge.launcher3.wing.SwivelItemTouchHelper.1
        @Override // java.lang.Runnable
        public void run() {
            if (SwivelItemTouchHelper.this.mSelected == null || !SwivelItemTouchHelper.this.scrollIfNecessary()) {
                return;
            }
            if (SwivelItemTouchHelper.this.mSelected != null) {
                SwivelItemTouchHelper swivelItemTouchHelper = SwivelItemTouchHelper.this;
                swivelItemTouchHelper.moveIfNecessary(swivelItemTouchHelper.mSelected);
            }
            SwivelItemTouchHelper.this.mRecyclerView.removeCallbacks(SwivelItemTouchHelper.this.mScrollRunnable);
            ViewCompat.postOnAnimation(SwivelItemTouchHelper.this.mRecyclerView, this);
        }
    };
    private RecyclerView.ChildDrawingOrderCallback mChildDrawingOrderCallback = null;
    View mOverdrawChild = null;
    int mOverdrawChildPosition = -1;
    public final RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.OnItemTouchListener() { // from class: com.lge.launcher3.wing.SwivelItemTouchHelper.2
        @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
            int iFindPointerIndex;
            RecoverAnimation recoverAnimationFindAnimation;
            SwivelItemTouchHelper.this.mGestureDetector.onTouchEvent(event);
            int actionMasked = event.getActionMasked();
            if (actionMasked == 0) {
                SwivelItemTouchHelper.this.mActivePointerId = event.getPointerId(0);
                SwivelItemTouchHelper.this.mInitialTouchX = event.getX();
                SwivelItemTouchHelper.this.mInitialTouchY = event.getY();
                SwivelItemTouchHelper.this.obtainVelocityTracker();
                if (SwivelItemTouchHelper.this.mSelected == null && (recoverAnimationFindAnimation = SwivelItemTouchHelper.this.findAnimation(event)) != null) {
                    SwivelItemTouchHelper.this.mInitialTouchX -= recoverAnimationFindAnimation.mX;
                    SwivelItemTouchHelper.this.mInitialTouchY -= recoverAnimationFindAnimation.mY;
                    SwivelItemTouchHelper.this.endRecoverAnimation(recoverAnimationFindAnimation.mViewHolder, true);
                    if (SwivelItemTouchHelper.this.mPendingCleanup.remove(recoverAnimationFindAnimation.mViewHolder.itemView)) {
                        SwivelItemTouchHelper.this.mCallback.clearView(SwivelItemTouchHelper.this.mRecyclerView, recoverAnimationFindAnimation.mViewHolder);
                    }
                    SwivelItemTouchHelper.this.select(recoverAnimationFindAnimation.mViewHolder, recoverAnimationFindAnimation.mActionState);
                    SwivelItemTouchHelper swivelItemTouchHelper = SwivelItemTouchHelper.this;
                    swivelItemTouchHelper.updateDxDy(event, swivelItemTouchHelper.mSelectedFlags, 0);
                }
            } else if (actionMasked == 3 || actionMasked == 1) {
                SwivelItemTouchHelper.this.mActivePointerId = -1;
                SwivelItemTouchHelper.this.select(null, 0);
            } else if (SwivelItemTouchHelper.this.mActivePointerId != -1 && (iFindPointerIndex = event.findPointerIndex(SwivelItemTouchHelper.this.mActivePointerId)) >= 0) {
                SwivelItemTouchHelper.this.checkSelectForSwipe(actionMasked, event, iFindPointerIndex);
            }
            if (SwivelItemTouchHelper.this.mVelocityTracker != null) {
                SwivelItemTouchHelper.this.mVelocityTracker.addMovement(event);
            }
            return SwivelItemTouchHelper.this.mSelected != null;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent event) {
            SwivelItemTouchHelper.this.mGestureDetector.onTouchEvent(event);
            if (SwivelItemTouchHelper.this.mVelocityTracker != null) {
                SwivelItemTouchHelper.this.mVelocityTracker.addMovement(event);
            }
            if (SwivelItemTouchHelper.this.mActivePointerId == -1) {
                return;
            }
            int actionMasked = event.getActionMasked();
            int iFindPointerIndex = event.findPointerIndex(SwivelItemTouchHelper.this.mActivePointerId);
            if (iFindPointerIndex >= 0) {
                SwivelItemTouchHelper.this.checkSelectForSwipe(actionMasked, event, iFindPointerIndex);
            }
            RecyclerView.ViewHolder viewHolder = SwivelItemTouchHelper.this.mSelected;
            if (viewHolder == null) {
                return;
            }
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(SwivelItemTouchHelper.this.mRecyclerView.getContext())) {
                        return;
                    }
                    if (SwivelItemTouchHelper.this.mCallback != null) {
                        SwivelItemTouchHelper.this.mCallback.onTouchMove(event);
                    }
                    if (iFindPointerIndex >= 0) {
                        SwivelItemTouchHelper swivelItemTouchHelper = SwivelItemTouchHelper.this;
                        swivelItemTouchHelper.updateDxDy(event, swivelItemTouchHelper.mSelectedFlags, iFindPointerIndex);
                        SwivelItemTouchHelper.this.moveIfNecessary(viewHolder);
                        SwivelItemTouchHelper.this.mRecyclerView.removeCallbacks(SwivelItemTouchHelper.this.mScrollRunnable);
                        SwivelItemTouchHelper.this.mScrollRunnable.run();
                        SwivelItemTouchHelper.this.mRecyclerView.invalidate();
                        return;
                    }
                    return;
                }
                if (actionMasked != 3) {
                    if (actionMasked != 6) {
                        return;
                    }
                    int actionIndex = event.getActionIndex();
                    if (event.getPointerId(actionIndex) == SwivelItemTouchHelper.this.mActivePointerId) {
                        SwivelItemTouchHelper.this.mActivePointerId = event.getPointerId(actionIndex == 0 ? 1 : 0);
                        SwivelItemTouchHelper swivelItemTouchHelper2 = SwivelItemTouchHelper.this;
                        swivelItemTouchHelper2.updateDxDy(event, swivelItemTouchHelper2.mSelectedFlags, actionIndex);
                        return;
                    }
                    return;
                }
                if (SwivelItemTouchHelper.this.mVelocityTracker != null) {
                    SwivelItemTouchHelper.this.mVelocityTracker.clear();
                }
            }
            SwivelItemTouchHelper.this.select(null, 0);
            SwivelItemTouchHelper.this.mActivePointerId = -1;
            if (SwivelItemTouchHelper.this.mCallback != null) {
                SwivelItemTouchHelper.this.mCallback.onTouchMove(event);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (disallowIntercept) {
                SwivelItemTouchHelper.this.select(null, 0);
            }
        }
    };

    public interface ViewDropHandler {
        void prepareForDrop(View view, View target, int x, int y);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
    public void onChildViewAttachedToWindow(View view) {
    }

    public SwivelItemTouchHelper(Callback callback) {
        this.mCallback = callback;
    }

    private static boolean hitTest(View child, float x, float y, float left, float top) {
        return x >= left && x <= left + ((float) child.getWidth()) && y >= top && y <= top + ((float) child.getHeight());
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        RecyclerView recyclerView2 = this.mRecyclerView;
        if (recyclerView2 == recyclerView) {
            return;
        }
        if (recyclerView2 != null) {
            destroyCallbacks();
        }
        this.mRecyclerView = recyclerView;
        if (recyclerView != null) {
            Resources resources = recyclerView.getResources();
            this.mSwipeEscapeVelocity = resources.getDimension(R.dimen.item_touch_helper_swipe_escape_velocity);
            this.mMaxSwipeVelocity = resources.getDimension(R.dimen.item_touch_helper_swipe_escape_max_velocity);
            setupCallbacks();
        }
    }

    private void setupCallbacks() {
        this.mSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
        this.mRecyclerView.addItemDecoration(this);
        this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.addOnChildAttachStateChangeListener(this);
        startGestureDetection();
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeItemDecoration(this);
        this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            this.mCallback.clearView(this.mRecyclerView, this.mRecoverAnimations.get(0).mViewHolder);
        }
        this.mRecoverAnimations.clear();
        this.mOverdrawChild = null;
        this.mOverdrawChildPosition = -1;
        releaseVelocityTracker();
        stopGestureDetection();
    }

    private void startGestureDetection() {
        this.mItemTouchHelperGestureListener = new ItemTouchHelperGestureListener();
        this.mGestureDetector = new GestureDetectorCompat(this.mRecyclerView.getContext(), this.mItemTouchHelperGestureListener);
    }

    private void stopGestureDetection() {
        ItemTouchHelperGestureListener itemTouchHelperGestureListener = this.mItemTouchHelperGestureListener;
        if (itemTouchHelperGestureListener != null) {
            itemTouchHelperGestureListener.doNotReactToLongPress();
            this.mItemTouchHelperGestureListener = null;
        }
        if (this.mGestureDetector != null) {
            this.mGestureDetector = null;
        }
    }

    private void getSelectedDxDy(float[] outPosition) {
        if ((this.mSelectedFlags & 12) != 0) {
            outPosition[0] = (this.mSelectedStartX + this.mDx) - this.mSelected.itemView.getLeft();
        } else {
            outPosition[0] = this.mSelected.itemView.getTranslationX();
        }
        if ((this.mSelectedFlags & 3) != 0) {
            outPosition[1] = (this.mSelectedStartY + this.mDy) - this.mSelected.itemView.getTop();
        } else {
            outPosition[1] = this.mSelected.itemView.getTranslationY();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        float f;
        float f2;
        if (this.mSelected != null) {
            getSelectedDxDy(this.mTmpPosition);
            float[] fArr = this.mTmpPosition;
            float f3 = fArr[0];
            f2 = fArr[1];
            f = f3;
        } else {
            f = 0.0f;
            f2 = 0.0f;
        }
        this.mCallback.onDrawOver(c, parent, this.mSelected, this.mRecoverAnimations, this.mActionState, f, f2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        float f;
        float f2;
        this.mOverdrawChildPosition = -1;
        if (this.mSelected != null) {
            getSelectedDxDy(this.mTmpPosition);
            float[] fArr = this.mTmpPosition;
            float f3 = fArr[0];
            f2 = fArr[1];
            f = f3;
        } else {
            f = 0.0f;
            f2 = 0.0f;
        }
        this.mCallback.onDraw(c, parent, this.mSelected, this.mRecoverAnimations, this.mActionState, f, f2);
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x0121  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void select(androidx.recyclerview.widget.RecyclerView.ViewHolder r24, int r25) {
        /*
            r23 = this;
            r11 = r23
            r12 = r24
            r13 = r25
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r11.mSelected
            if (r12 != r0) goto Lf
            int r0 = r11.mActionState
            if (r13 != r0) goto Lf
            return
        Lf:
            r0 = -9223372036854775808
            r11.mDragScrollStartTimeInMs = r0
            int r4 = r11.mActionState
            r14 = 1
            r11.endRecoverAnimation(r12, r14)
            r11.mActionState = r13
            r15 = 2
            if (r13 != r15) goto L30
            if (r12 == 0) goto L28
            android.view.View r0 = r12.itemView
            r11.mOverdrawChild = r0
            r23.addChildDrawingOrderCallback()
            goto L30
        L28:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Must pass a ViewHolder when dragging"
            r0.<init>(r1)
            throw r0
        L30:
            int r0 = r13 * 8
            r10 = 8
            int r0 = r0 + r10
            int r0 = r14 << r0
            int r16 = r0 + (-1)
            androidx.recyclerview.widget.RecyclerView$ViewHolder r9 = r11.mSelected
            r8 = 0
            if (r9 == 0) goto Led
            android.view.View r0 = r9.itemView
            android.view.ViewParent r0 = r0.getParent()
            if (r0 == 0) goto Ld9
            if (r4 != r15) goto L4a
            r7 = r8
            goto L4f
        L4a:
            int r0 = r11.swipeIfNecessary(r9)
            r7 = r0
        L4f:
            r23.releaseVelocityTracker()
            r0 = 4
            r1 = 0
            if (r7 == r14) goto L7c
            if (r7 == r15) goto L7c
            if (r7 == r0) goto L69
            if (r7 == r10) goto L69
            r2 = 16
            if (r7 == r2) goto L69
            r2 = 32
            if (r7 == r2) goto L69
            r17 = r1
            r18 = r17
            goto L8e
        L69:
            float r2 = r11.mDx
            float r2 = java.lang.Math.signum(r2)
            androidx.recyclerview.widget.RecyclerView r3 = r11.mRecyclerView
            int r3 = r3.getWidth()
            float r3 = (float) r3
            float r2 = r2 * r3
            r18 = r1
            r17 = r2
            goto L8e
        L7c:
            float r2 = r11.mDy
            float r2 = java.lang.Math.signum(r2)
            androidx.recyclerview.widget.RecyclerView r3 = r11.mRecyclerView
            int r3 = r3.getHeight()
            float r3 = (float) r3
            float r2 = r2 * r3
            r17 = r1
            r18 = r2
        L8e:
            if (r4 != r15) goto L92
            r6 = r10
            goto L97
        L92:
            if (r7 <= 0) goto L96
            r6 = r15
            goto L97
        L96:
            r6 = r0
        L97:
            float[] r0 = r11.mTmpPosition
            r11.getSelectedDxDy(r0)
            float[] r0 = r11.mTmpPosition
            r19 = r0[r8]
            r20 = r0[r14]
            com.lge.launcher3.wing.SwivelItemTouchHelper$3 r5 = new com.lge.launcher3.wing.SwivelItemTouchHelper$3
            r0 = r5
            r1 = r23
            r2 = r9
            r3 = r6
            r14 = r5
            r5 = r19
            r15 = r6
            r6 = r20
            r21 = r7
            r7 = r17
            r8 = r18
            r22 = r9
            r9 = r21
            r21 = r10
            r10 = r22
            r0.<init>(r2, r3, r4, r5, r6, r7, r8)
            com.lge.launcher3.wing.SwivelItemTouchHelper$Callback r0 = r11.mCallback
            androidx.recyclerview.widget.RecyclerView r1 = r11.mRecyclerView
            float r2 = r17 - r19
            float r3 = r18 - r20
            long r0 = r0.getAnimationDuration(r1, r15, r2, r3)
            r14.setDuration(r0)
            java.util.List<com.lge.launcher3.wing.SwivelItemTouchHelper$RecoverAnimation> r0 = r11.mRecoverAnimations
            r0.add(r14)
            r14.start()
            r8 = 1
            goto Le9
        Ld9:
            r0 = r9
            r21 = r10
            android.view.View r1 = r0.itemView
            r11.removeChildDrawingOrderCallbackIfNecessary(r1)
            com.lge.launcher3.wing.SwivelItemTouchHelper$Callback r1 = r11.mCallback
            androidx.recyclerview.widget.RecyclerView r2 = r11.mRecyclerView
            r1.clearView(r2, r0)
            r8 = 0
        Le9:
            r0 = 0
            r11.mSelected = r0
            goto Lf0
        Led:
            r21 = r10
            r8 = 0
        Lf0:
            if (r12 == 0) goto L121
            com.lge.launcher3.wing.SwivelItemTouchHelper$Callback r0 = r11.mCallback
            androidx.recyclerview.widget.RecyclerView r1 = r11.mRecyclerView
            int r0 = r0.getAbsoluteMovementFlags(r1, r12)
            r0 = r0 & r16
            int r1 = r11.mActionState
            int r1 = r1 * 8
            int r0 = r0 >> r1
            r11.mSelectedFlags = r0
            android.view.View r0 = r12.itemView
            int r0 = r0.getLeft()
            float r0 = (float) r0
            r11.mSelectedStartX = r0
            android.view.View r0 = r12.itemView
            int r0 = r0.getTop()
            float r0 = (float) r0
            r11.mSelectedStartY = r0
            r11.mSelected = r12
            r0 = 2
            if (r13 != r0) goto L121
            android.view.View r0 = r12.itemView
            r1 = 0
            r0.performHapticFeedback(r1)
            goto L122
        L121:
            r1 = 0
        L122:
            androidx.recyclerview.widget.RecyclerView r0 = r11.mRecyclerView
            android.view.ViewParent r0 = r0.getParent()
            if (r0 == 0) goto L134
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r11.mSelected
            if (r2 == 0) goto L130
            r14 = 1
            goto L131
        L130:
            r14 = r1
        L131:
            r0.requestDisallowInterceptTouchEvent(r14)
        L134:
            if (r8 != 0) goto L13f
            androidx.recyclerview.widget.RecyclerView r0 = r11.mRecyclerView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r0 = r0.getLayoutManager()
            r0.requestSimpleAnimationsInNextLayout()
        L13f:
            com.lge.launcher3.wing.SwivelItemTouchHelper$Callback r0 = r11.mCallback
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r11.mSelected
            int r2 = r11.mActionState
            r0.onSelectedChanged(r1, r2)
            androidx.recyclerview.widget.RecyclerView r0 = r11.mRecyclerView
            r0.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.wing.SwivelItemTouchHelper.select(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    void postDispatchSwipe(final RecoverAnimation anim, final int swipeDir) {
        this.mRecyclerView.post(new Runnable() { // from class: com.lge.launcher3.wing.SwivelItemTouchHelper.4
            @Override // java.lang.Runnable
            public void run() {
                if (SwivelItemTouchHelper.this.mRecyclerView == null || !SwivelItemTouchHelper.this.mRecyclerView.isAttachedToWindow() || anim.mOverridden || anim.mViewHolder.getAdapterPosition() == -1) {
                    return;
                }
                RecyclerView.ItemAnimator itemAnimator = SwivelItemTouchHelper.this.mRecyclerView.getItemAnimator();
                if ((itemAnimator == null || !itemAnimator.isRunning(null)) && !SwivelItemTouchHelper.this.hasRunningRecoverAnim()) {
                    SwivelItemTouchHelper.this.mCallback.onSwiped(anim.mViewHolder, swipeDir);
                } else {
                    SwivelItemTouchHelper.this.mRecyclerView.post(this);
                }
            }
        });
    }

    boolean hasRunningRecoverAnim() {
        int size = this.mRecoverAnimations.size();
        for (int i = 0; i < size; i++) {
            if (!this.mRecoverAnimations.get(i).mEnded) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x007d  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00c4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    boolean scrollIfNecessary() {
        /*
            r16 = this;
            r0 = r16
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r0.mSelected
            r2 = 0
            r3 = -9223372036854775808
            if (r1 != 0) goto Lc
            r0.mDragScrollStartTimeInMs = r3
            return r2
        Lc:
            long r5 = java.lang.System.currentTimeMillis()
            long r7 = r0.mDragScrollStartTimeInMs
            int r1 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r1 != 0) goto L19
            r7 = 0
            goto L1b
        L19:
            long r7 = r5 - r7
        L1b:
            androidx.recyclerview.widget.RecyclerView r1 = r0.mRecyclerView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r1 = r1.getLayoutManager()
            android.graphics.Rect r9 = r0.mTmpRect
            if (r9 != 0) goto L2c
            android.graphics.Rect r9 = new android.graphics.Rect
            r9.<init>()
            r0.mTmpRect = r9
        L2c:
            androidx.recyclerview.widget.RecyclerView$ViewHolder r9 = r0.mSelected
            android.view.View r9 = r9.itemView
            android.graphics.Rect r10 = r0.mTmpRect
            r1.calculateItemDecorationsForChild(r9, r10)
            boolean r9 = r1.canScrollHorizontally()
            r10 = 0
            if (r9 == 0) goto L7d
            float r9 = r0.mSelectedStartX
            float r11 = r0.mDx
            float r9 = r9 + r11
            int r9 = (int) r9
            android.graphics.Rect r11 = r0.mTmpRect
            int r11 = r11.left
            int r11 = r9 - r11
            androidx.recyclerview.widget.RecyclerView r12 = r0.mRecyclerView
            int r12 = r12.getPaddingLeft()
            int r11 = r11 - r12
            float r12 = r0.mDx
            int r13 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r13 >= 0) goto L59
            if (r11 >= 0) goto L59
            r12 = r11
            goto L7e
        L59:
            int r11 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r11 <= 0) goto L7d
            androidx.recyclerview.widget.RecyclerView$ViewHolder r11 = r0.mSelected
            android.view.View r11 = r11.itemView
            int r11 = r11.getWidth()
            int r9 = r9 + r11
            android.graphics.Rect r11 = r0.mTmpRect
            int r11 = r11.right
            int r9 = r9 + r11
            androidx.recyclerview.widget.RecyclerView r11 = r0.mRecyclerView
            int r11 = r11.getWidth()
            androidx.recyclerview.widget.RecyclerView r12 = r0.mRecyclerView
            int r12 = r12.getPaddingRight()
            int r11 = r11 - r12
            int r9 = r9 - r11
            if (r9 <= 0) goto L7d
            r12 = r9
            goto L7e
        L7d:
            r12 = r2
        L7e:
            boolean r1 = r1.canScrollVertically()
            if (r1 == 0) goto Lc4
            float r1 = r0.mSelectedStartY
            float r9 = r0.mDy
            float r1 = r1 + r9
            int r1 = (int) r1
            android.graphics.Rect r9 = r0.mTmpRect
            int r9 = r9.top
            int r9 = r1 - r9
            androidx.recyclerview.widget.RecyclerView r11 = r0.mRecyclerView
            int r11 = r11.getPaddingTop()
            int r9 = r9 - r11
            float r11 = r0.mDy
            int r13 = (r11 > r10 ? 1 : (r11 == r10 ? 0 : -1))
            if (r13 >= 0) goto La1
            if (r9 >= 0) goto La1
            r1 = r9
            goto Lc5
        La1:
            int r9 = (r11 > r10 ? 1 : (r11 == r10 ? 0 : -1))
            if (r9 <= 0) goto Lc4
            androidx.recyclerview.widget.RecyclerView$ViewHolder r9 = r0.mSelected
            android.view.View r9 = r9.itemView
            int r9 = r9.getHeight()
            int r1 = r1 + r9
            android.graphics.Rect r9 = r0.mTmpRect
            int r9 = r9.bottom
            int r1 = r1 + r9
            androidx.recyclerview.widget.RecyclerView r9 = r0.mRecyclerView
            int r9 = r9.getHeight()
            androidx.recyclerview.widget.RecyclerView r10 = r0.mRecyclerView
            int r10 = r10.getPaddingBottom()
            int r9 = r9 - r10
            int r1 = r1 - r9
            if (r1 <= 0) goto Lc4
            goto Lc5
        Lc4:
            r1 = r2
        Lc5:
            if (r12 == 0) goto Lde
            com.lge.launcher3.wing.SwivelItemTouchHelper$Callback r9 = r0.mCallback
            androidx.recyclerview.widget.RecyclerView r10 = r0.mRecyclerView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r11 = r0.mSelected
            android.view.View r11 = r11.itemView
            int r11 = r11.getWidth()
            androidx.recyclerview.widget.RecyclerView r13 = r0.mRecyclerView
            int r13 = r13.getWidth()
            r14 = r7
            int r12 = r9.interpolateOutOfBoundsScroll(r10, r11, r12, r13, r14)
        Lde:
            r14 = r12
            if (r1 == 0) goto Lfd
            com.lge.launcher3.wing.SwivelItemTouchHelper$Callback r9 = r0.mCallback
            androidx.recyclerview.widget.RecyclerView r10 = r0.mRecyclerView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r11 = r0.mSelected
            android.view.View r11 = r11.itemView
            int r11 = r11.getHeight()
            androidx.recyclerview.widget.RecyclerView r12 = r0.mRecyclerView
            int r13 = r12.getHeight()
            r12 = r1
            r1 = r14
            r14 = r7
            int r7 = r9.interpolateOutOfBoundsScroll(r10, r11, r12, r13, r14)
            r12 = r1
            r1 = r7
            goto Lfe
        Lfd:
            r12 = r14
        Lfe:
            if (r12 != 0) goto L106
            if (r1 == 0) goto L103
            goto L106
        L103:
            r0.mDragScrollStartTimeInMs = r3
            return r2
        L106:
            long r7 = r0.mDragScrollStartTimeInMs
            int r2 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r2 != 0) goto L10e
            r0.mDragScrollStartTimeInMs = r5
        L10e:
            androidx.recyclerview.widget.RecyclerView r2 = r0.mRecyclerView
            r2.scrollBy(r12, r1)
            r1 = 1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.wing.SwivelItemTouchHelper.scrollIfNecessary():boolean");
    }

    private List<RecyclerView.ViewHolder> findSwapTargets(RecyclerView.ViewHolder viewHolder) {
        RecyclerView.ViewHolder viewHolder2 = viewHolder;
        List<RecyclerView.ViewHolder> list = this.mSwapTargets;
        if (list == null) {
            this.mSwapTargets = new ArrayList();
            this.mDistances = new ArrayList();
        } else {
            list.clear();
            this.mDistances.clear();
        }
        int boundingBoxMargin = this.mCallback.getBoundingBoxMargin();
        int iRound = Math.round(this.mSelectedStartX + this.mDx) - boundingBoxMargin;
        int iRound2 = Math.round(this.mSelectedStartY + this.mDy) - boundingBoxMargin;
        int i = boundingBoxMargin * 2;
        int width = viewHolder2.itemView.getWidth() + iRound + i;
        int height = viewHolder2.itemView.getHeight() + iRound2 + i;
        int i2 = (iRound + width) / 2;
        int i3 = (iRound2 + height) / 2;
        RecyclerView.LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        int childCount = layoutManager.getChildCount();
        int i4 = 0;
        while (i4 < childCount) {
            View childAt = layoutManager.getChildAt(i4);
            if (childAt != viewHolder2.itemView && childAt.getBottom() >= iRound2 && childAt.getTop() <= height && childAt.getRight() >= iRound && childAt.getLeft() <= width) {
                RecyclerView.ViewHolder childViewHolder = this.mRecyclerView.getChildViewHolder(childAt);
                if (this.mCallback.canDropOver(this.mRecyclerView, this.mSelected, childViewHolder)) {
                    int iAbs = Math.abs(i2 - ((childAt.getLeft() + childAt.getRight()) / 2));
                    int iAbs2 = Math.abs(i3 - ((childAt.getTop() + childAt.getBottom()) / 2));
                    int i5 = (iAbs * iAbs) + (iAbs2 * iAbs2);
                    int size = this.mSwapTargets.size();
                    int i6 = 0;
                    for (int i7 = 0; i7 < size && i5 > this.mDistances.get(i7).intValue(); i7++) {
                        i6++;
                    }
                    this.mSwapTargets.add(i6, childViewHolder);
                    this.mDistances.add(i6, Integer.valueOf(i5));
                }
            }
            i4++;
            viewHolder2 = viewHolder;
        }
        return this.mSwapTargets;
    }

    void moveIfNecessary(RecyclerView.ViewHolder viewHolder) {
        if (!this.mRecyclerView.isLayoutRequested() && this.mActionState == 2) {
            float moveThreshold = this.mCallback.getMoveThreshold(viewHolder);
            int i = (int) (this.mSelectedStartX + this.mDx);
            int i2 = (int) (this.mSelectedStartY + this.mDy);
            if (Math.abs(i2 - viewHolder.itemView.getTop()) >= viewHolder.itemView.getHeight() * moveThreshold || Math.abs(i - viewHolder.itemView.getLeft()) >= viewHolder.itemView.getWidth() * moveThreshold) {
                List<RecyclerView.ViewHolder> listFindSwapTargets = findSwapTargets(viewHolder);
                if (listFindSwapTargets.size() == 0) {
                    return;
                }
                RecyclerView.ViewHolder viewHolderChooseDropTarget = this.mCallback.chooseDropTarget(viewHolder, listFindSwapTargets, i, i2);
                if (viewHolderChooseDropTarget == null) {
                    this.mSwapTargets.clear();
                    this.mDistances.clear();
                    return;
                }
                int adapterPosition = viewHolderChooseDropTarget.getAdapterPosition();
                int adapterPosition2 = viewHolder.getAdapterPosition();
                if (this.mCallback.onMove(this.mRecyclerView, viewHolder, viewHolderChooseDropTarget)) {
                    this.mCallback.onMoved(this.mRecyclerView, viewHolder, adapterPosition2, viewHolderChooseDropTarget, adapterPosition, i, i2);
                }
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
    public void onChildViewDetachedFromWindow(View view) {
        removeChildDrawingOrderCallbackIfNecessary(view);
        RecyclerView.ViewHolder childViewHolder = this.mRecyclerView.getChildViewHolder(view);
        if (childViewHolder == null) {
            return;
        }
        RecyclerView.ViewHolder viewHolder = this.mSelected;
        if (viewHolder != null && childViewHolder == viewHolder) {
            select(null, 0);
            return;
        }
        endRecoverAnimation(childViewHolder, false);
        if (this.mPendingCleanup.remove(childViewHolder.itemView)) {
            this.mCallback.clearView(this.mRecyclerView, childViewHolder);
        }
    }

    void endRecoverAnimation(RecyclerView.ViewHolder viewHolder, boolean override) {
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            RecoverAnimation recoverAnimation = this.mRecoverAnimations.get(size);
            if (recoverAnimation.mViewHolder == viewHolder) {
                recoverAnimation.mOverridden |= override;
                if (!recoverAnimation.mEnded) {
                    recoverAnimation.cancel();
                }
                this.mRecoverAnimations.remove(size);
                return;
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.setEmpty();
    }

    void obtainVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    private void releaseVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private RecyclerView.ViewHolder findSwipedView(MotionEvent motionEvent) {
        View viewFindChildView;
        RecyclerView.LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        int i = this.mActivePointerId;
        if (i == -1) {
            return null;
        }
        int iFindPointerIndex = motionEvent.findPointerIndex(i);
        float x = motionEvent.getX(iFindPointerIndex) - this.mInitialTouchX;
        float y = motionEvent.getY(iFindPointerIndex) - this.mInitialTouchY;
        float fAbs = Math.abs(x);
        float fAbs2 = Math.abs(y);
        int i2 = this.mSlop;
        if (fAbs < i2 && fAbs2 < i2) {
            return null;
        }
        if (fAbs > fAbs2 && layoutManager.canScrollHorizontally()) {
            return null;
        }
        if ((fAbs2 <= fAbs || !layoutManager.canScrollVertically()) && (viewFindChildView = findChildView(motionEvent)) != null) {
            return this.mRecyclerView.getChildViewHolder(viewFindChildView);
        }
        return null;
    }

    void checkSelectForSwipe(int action, MotionEvent motionEvent, int pointerIndex) {
        RecyclerView.ViewHolder viewHolderFindSwipedView;
        int absoluteMovementFlags;
        if (this.mSelected != null || action != 2 || this.mActionState == 2 || !this.mCallback.isItemViewSwipeEnabled(this.mRecyclerView.getContext()) || this.mRecyclerView.getScrollState() == 1 || (viewHolderFindSwipedView = findSwipedView(motionEvent)) == null || (absoluteMovementFlags = (this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, viewHolderFindSwipedView) & 65280) >> 8) == 0) {
            return;
        }
        float x = motionEvent.getX(pointerIndex);
        float y = motionEvent.getY(pointerIndex);
        float f = x - this.mInitialTouchX;
        float f2 = y - this.mInitialTouchY;
        float fAbs = Math.abs(f);
        float fAbs2 = Math.abs(f2);
        int i = this.mSlop;
        if (fAbs >= i || fAbs2 >= i) {
            if (fAbs > fAbs2) {
                if (f < 0.0f && (absoluteMovementFlags & 4) == 0) {
                    return;
                }
                if (f > 0.0f && (absoluteMovementFlags & 8) == 0) {
                    return;
                }
            } else {
                if (f2 < 0.0f && (absoluteMovementFlags & 1) == 0) {
                    return;
                }
                if (f2 > 0.0f && (absoluteMovementFlags & 2) == 0) {
                    return;
                }
            }
            this.mDy = 0.0f;
            this.mDx = 0.0f;
            this.mActivePointerId = motionEvent.getPointerId(0);
            select(viewHolderFindSwipedView, 1);
        }
    }

    View findChildView(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        RecyclerView.ViewHolder viewHolder = this.mSelected;
        if (viewHolder != null) {
            View view = viewHolder.itemView;
            if (hitTest(view, x, y, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy)) {
                return view;
            }
        }
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            RecoverAnimation recoverAnimation = this.mRecoverAnimations.get(size);
            View view2 = recoverAnimation.mViewHolder.itemView;
            if (hitTest(view2, x, y, recoverAnimation.mX, recoverAnimation.mY)) {
                return view2;
            }
        }
        return this.mRecyclerView.findChildViewUnder(x, 200.0f);
    }

    public void startDrag(RecyclerView.ViewHolder viewHolder) {
        if (!this.mCallback.hasDragFlag(this.mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start drag has been called but dragging is not enabled");
            return;
        }
        if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e(TAG, "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this SwivelItemTouchHelper.");
            return;
        }
        obtainVelocityTracker();
        this.mDy = 0.0f;
        this.mDx = 0.0f;
        select(viewHolder, 2);
    }

    public void startSwipe(RecyclerView.ViewHolder viewHolder) {
        if (!this.mCallback.hasSwipeFlag(this.mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start swipe has been called but swiping is not enabled");
            return;
        }
        if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e(TAG, "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this SwivelItemTouchHelper.");
            return;
        }
        obtainVelocityTracker();
        this.mDy = 0.0f;
        this.mDx = 0.0f;
        select(viewHolder, 1);
    }

    RecoverAnimation findAnimation(MotionEvent event) {
        if (this.mRecoverAnimations.isEmpty()) {
            return null;
        }
        View viewFindChildView = findChildView(event);
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            RecoverAnimation recoverAnimation = this.mRecoverAnimations.get(size);
            if (recoverAnimation.mViewHolder.itemView == viewFindChildView) {
                return recoverAnimation;
            }
        }
        return null;
    }

    void updateDxDy(MotionEvent ev, int directionFlags, int pointerIndex) {
        float x = ev.getX(pointerIndex);
        float y = ev.getY(pointerIndex);
        float f = x - this.mInitialTouchX;
        this.mDx = f;
        this.mDy = y - this.mInitialTouchY;
        if ((directionFlags & 4) == 0) {
            this.mDx = Math.max(0.0f, f);
        }
        if ((directionFlags & 8) == 0) {
            this.mDx = Math.min(0.0f, this.mDx);
        }
        if ((directionFlags & 1) == 0) {
            this.mDy = Math.max(0.0f, this.mDy);
        }
        if ((directionFlags & 2) == 0) {
            this.mDy = Math.min(0.0f, this.mDy);
        }
    }

    private int swipeIfNecessary(RecyclerView.ViewHolder viewHolder) {
        if (this.mActionState == 2) {
            return 0;
        }
        int movementFlags = this.mCallback.getMovementFlags(this.mRecyclerView, viewHolder);
        int iConvertToAbsoluteDirection = (this.mCallback.convertToAbsoluteDirection(movementFlags, ViewCompat.getLayoutDirection(this.mRecyclerView)) & 65280) >> 8;
        if (iConvertToAbsoluteDirection == 0) {
            return 0;
        }
        int i = (movementFlags & 65280) >> 8;
        if (Math.abs(this.mDx) > Math.abs(this.mDy)) {
            int iCheckHorizontalSwipe = checkHorizontalSwipe(viewHolder, iConvertToAbsoluteDirection);
            if (iCheckHorizontalSwipe > 0) {
                return (i & iCheckHorizontalSwipe) == 0 ? Callback.convertToRelativeDirection(iCheckHorizontalSwipe, ViewCompat.getLayoutDirection(this.mRecyclerView)) : iCheckHorizontalSwipe;
            }
            int iCheckVerticalSwipe = checkVerticalSwipe(viewHolder, iConvertToAbsoluteDirection);
            if (iCheckVerticalSwipe > 0) {
                return iCheckVerticalSwipe;
            }
        } else {
            int iCheckVerticalSwipe2 = checkVerticalSwipe(viewHolder, iConvertToAbsoluteDirection);
            if (iCheckVerticalSwipe2 > 0) {
                return iCheckVerticalSwipe2;
            }
            int iCheckHorizontalSwipe2 = checkHorizontalSwipe(viewHolder, iConvertToAbsoluteDirection);
            if (iCheckHorizontalSwipe2 > 0) {
                return (i & iCheckHorizontalSwipe2) == 0 ? Callback.convertToRelativeDirection(iCheckHorizontalSwipe2, ViewCompat.getLayoutDirection(this.mRecyclerView)) : iCheckHorizontalSwipe2;
            }
        }
        return 0;
    }

    private int checkHorizontalSwipe(RecyclerView.ViewHolder viewHolder, int flags) {
        if ((flags & 12) == 0) {
            return 0;
        }
        int i = this.mDx > 0.0f ? 8 : 4;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null && this.mActivePointerId > -1) {
            velocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
            float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
            float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
            int i2 = xVelocity <= 0.0f ? 4 : 8;
            float fAbs = Math.abs(xVelocity);
            if ((i2 & flags) != 0 && i == i2 && fAbs >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && fAbs > Math.abs(yVelocity)) {
                return i2;
            }
        }
        float width = this.mRecyclerView.getWidth() * this.mCallback.getSwipeThreshold(viewHolder);
        if ((flags & i) == 0 || Math.abs(this.mDx) <= width) {
            return 0;
        }
        return i;
    }

    private int checkVerticalSwipe(RecyclerView.ViewHolder viewHolder, int flags) {
        if ((flags & 3) == 0) {
            return 0;
        }
        int i = this.mDy > 0.0f ? 2 : 1;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null && this.mActivePointerId > -1) {
            velocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
            float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
            float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
            int i2 = yVelocity <= 0.0f ? 1 : 2;
            float fAbs = Math.abs(yVelocity);
            if ((i2 & flags) != 0 && i2 == i && fAbs >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && fAbs > Math.abs(xVelocity)) {
                return i2;
            }
        }
        float height = this.mRecyclerView.getHeight() * this.mCallback.getSwipeThreshold(viewHolder);
        if ((flags & i) == 0 || Math.abs(this.mDy) <= height) {
            return 0;
        }
        return i;
    }

    private void addChildDrawingOrderCallback() {
        if (Build.VERSION.SDK_INT >= 21) {
            return;
        }
        if (this.mChildDrawingOrderCallback == null) {
            this.mChildDrawingOrderCallback = new RecyclerView.ChildDrawingOrderCallback() { // from class: com.lge.launcher3.wing.SwivelItemTouchHelper.5
                @Override // androidx.recyclerview.widget.RecyclerView.ChildDrawingOrderCallback
                public int onGetChildDrawingOrder(int childCount, int i) {
                    if (SwivelItemTouchHelper.this.mOverdrawChild == null) {
                        return i;
                    }
                    int iIndexOfChild = SwivelItemTouchHelper.this.mOverdrawChildPosition;
                    if (iIndexOfChild == -1) {
                        iIndexOfChild = SwivelItemTouchHelper.this.mRecyclerView.indexOfChild(SwivelItemTouchHelper.this.mOverdrawChild);
                        SwivelItemTouchHelper.this.mOverdrawChildPosition = iIndexOfChild;
                    }
                    return i == childCount + (-1) ? iIndexOfChild : i < iIndexOfChild ? i : i + 1;
                }
            };
        }
        this.mRecyclerView.setChildDrawingOrderCallback(this.mChildDrawingOrderCallback);
    }

    void removeChildDrawingOrderCallbackIfNecessary(View view) {
        if (view == this.mOverdrawChild) {
            this.mOverdrawChild = null;
            if (this.mChildDrawingOrderCallback != null) {
                this.mRecyclerView.setChildDrawingOrderCallback(null);
            }
        }
    }

    public static abstract class Callback {
        private static final int ABS_HORIZONTAL_DIR_FLAGS = 789516;
        public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;
        public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;
        private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 500;
        static final int RELATIVE_DIR_FLAGS = 3158064;
        private static final Interpolator sDragScrollInterpolator = new Interpolator() { // from class: com.lge.launcher3.wing.SwivelItemTouchHelper.Callback.1
            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float t) {
                return t * t * t * t * t;
            }
        };
        private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator() { // from class: com.lge.launcher3.wing.SwivelItemTouchHelper.Callback.2
            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float t) {
                float f = t - 1.0f;
                return (f * f * f * f * f) + 1.0f;
            }
        };
        private int mCachedMaxScrollSpeed = -1;

        public static int convertToRelativeDirection(int flags, int layoutDirection) {
            int i;
            int i2 = flags & ABS_HORIZONTAL_DIR_FLAGS;
            if (i2 == 0) {
                return flags;
            }
            int i3 = flags & (~i2);
            if (layoutDirection == 0) {
                i = i2 << 2;
            } else {
                int i4 = i2 << 1;
                i3 |= (-789517) & i4;
                i = (i4 & ABS_HORIZONTAL_DIR_FLAGS) << 2;
            }
            return i3 | i;
        }

        public static int makeFlag(int actionState, int directions) {
            return directions << (actionState * 8);
        }

        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
            return true;
        }

        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            int i;
            int i2 = flags & RELATIVE_DIR_FLAGS;
            if (i2 == 0) {
                return flags;
            }
            int i3 = flags & (~i2);
            if (layoutDirection == 0) {
                i = i2 >> 2;
            } else {
                int i4 = i2 >> 1;
                i3 |= (-3158065) & i4;
                i = (i4 & RELATIVE_DIR_FLAGS) >> 2;
            }
            return i3 | i;
        }

        public int getBoundingBoxMargin() {
            return 0;
        }

        public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
            return 0.5f;
        }

        public abstract int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder);

        public float getSwipeEscapeVelocity(float defaultValue) {
            return defaultValue;
        }

        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return 0.5f;
        }

        public float getSwipeVelocityThreshold(float defaultValue) {
            return defaultValue;
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onLongPress(MotionEvent e, View v) {
        }

        public abstract boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);

        public abstract void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

        public void onTouchMove(MotionEvent event) {
        }

        public static ItemTouchUIUtil getDefaultUIUtil() {
            return SwivelItemTouchUIUtilImpl.INSTANCE;
        }

        public static int makeMovementFlags(int dragFlags, int swipeFlags) {
            return makeFlag(2, dragFlags) | makeFlag(1, swipeFlags) | makeFlag(0, swipeFlags | dragFlags);
        }

        final int getAbsoluteMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return convertToAbsoluteDirection(getMovementFlags(recyclerView, viewHolder), ViewCompat.getLayoutDirection(recyclerView));
        }

        boolean hasDragFlag(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return (getAbsoluteMovementFlags(recyclerView, viewHolder) & SwivelItemTouchHelper.ACTION_MODE_DRAG_MASK) != 0;
        }

        boolean hasSwipeFlag(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return (getAbsoluteMovementFlags(recyclerView, viewHolder) & 65280) != 0;
        }

        public boolean isItemViewSwipeEnabled(Context context) {
            return !HomeSettingsSharedPreferences.getHomescreenLockEnabled(context);
        }

        public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected, List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
            int bottom;
            int iAbs;
            int top;
            int iAbs2;
            int left;
            int iAbs3;
            int right;
            int iAbs4;
            int width = curX + selected.itemView.getWidth();
            int height = curY + selected.itemView.getHeight();
            int left2 = curX - selected.itemView.getLeft();
            int top2 = curY - selected.itemView.getTop();
            int size = dropTargets.size();
            RecyclerView.ViewHolder viewHolder = null;
            int i = -1;
            for (int i2 = 0; i2 < size; i2++) {
                RecyclerView.ViewHolder viewHolder2 = dropTargets.get(i2);
                if (left2 > 0 && (right = viewHolder2.itemView.getRight() - width) < 0 && viewHolder2.itemView.getRight() > selected.itemView.getRight() && (iAbs4 = Math.abs(right)) > i) {
                    viewHolder = viewHolder2;
                    i = iAbs4;
                }
                if (left2 < 0 && (left = viewHolder2.itemView.getLeft() - curX) > 0 && viewHolder2.itemView.getLeft() < selected.itemView.getLeft() && (iAbs3 = Math.abs(left)) > i) {
                    viewHolder = viewHolder2;
                    i = iAbs3;
                }
                if (top2 < 0 && (top = viewHolder2.itemView.getTop() - curY) > 0 && viewHolder2.itemView.getTop() < selected.itemView.getTop() && (iAbs2 = Math.abs(top)) > i) {
                    viewHolder = viewHolder2;
                    i = iAbs2;
                }
                if (top2 > 0 && (bottom = viewHolder2.itemView.getBottom() - height) < 0 && viewHolder2.itemView.getBottom() > selected.itemView.getBottom() && (iAbs = Math.abs(bottom)) > i) {
                    viewHolder = viewHolder2;
                    i = iAbs;
                }
            }
            return viewHolder;
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                SwivelItemTouchUIUtilImpl.INSTANCE.onSelected(viewHolder.itemView);
            }
        }

        private int getMaxDragScroll(RecyclerView recyclerView) {
            if (this.mCachedMaxScrollSpeed == -1) {
                this.mCachedMaxScrollSpeed = 30;
            }
            return this.mCachedMaxScrollSpeed;
        }

        /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: androidx.recyclerview.widget.RecyclerView$LayoutManager */
        /* JADX WARN: Multi-variable type inference failed */
        public void onMoved(final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, int fromPos, final RecyclerView.ViewHolder target, int toPos, int x, int y) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof ViewDropHandler) {
                ((ViewDropHandler) layoutManager).prepareForDrop(viewHolder.itemView, target.itemView, x, y);
                return;
            }
            if (layoutManager.canScrollHorizontally()) {
                if (layoutManager.getDecoratedLeft(target.itemView) <= recyclerView.getPaddingLeft()) {
                    recyclerView.scrollToPosition(toPos);
                }
                if (layoutManager.getDecoratedRight(target.itemView) >= recyclerView.getWidth() - recyclerView.getPaddingRight()) {
                    recyclerView.scrollToPosition(toPos);
                }
            }
            if (layoutManager.canScrollVertically()) {
                if (layoutManager.getDecoratedTop(target.itemView) <= recyclerView.getPaddingTop()) {
                    recyclerView.scrollToPosition(toPos);
                }
                if (layoutManager.getDecoratedBottom(target.itemView) >= recyclerView.getHeight() - recyclerView.getPaddingBottom()) {
                    recyclerView.scrollToPosition(toPos);
                }
            }
        }

        void onDraw(Canvas c, RecyclerView parent, RecyclerView.ViewHolder selected, List<RecoverAnimation> recoverAnimationList, int actionState, float dX, float dY) {
            int size = recoverAnimationList.size();
            for (int i = 0; i < size; i++) {
                RecoverAnimation recoverAnimation = recoverAnimationList.get(i);
                recoverAnimation.update();
                int iSave = c.save();
                onChildDraw(c, parent, recoverAnimation.mViewHolder, recoverAnimation.mX, recoverAnimation.mY, recoverAnimation.mActionState, false);
                c.restoreToCount(iSave);
            }
            if (selected != null) {
                int iSave2 = c.save();
                onChildDraw(c, parent, selected, dX, dY, actionState, true);
                c.restoreToCount(iSave2);
            }
        }

        void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.ViewHolder selected, List<RecoverAnimation> recoverAnimationList, int actionState, float dX, float dY) {
            int size = recoverAnimationList.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                RecoverAnimation recoverAnimation = recoverAnimationList.get(i);
                int iSave = c.save();
                onChildDrawOver(c, parent, recoverAnimation.mViewHolder, recoverAnimation.mX, recoverAnimation.mY, recoverAnimation.mActionState, false);
                c.restoreToCount(iSave);
            }
            if (selected != null) {
                int iSave2 = c.save();
                onChildDrawOver(c, parent, selected, dX, dY, actionState, true);
                c.restoreToCount(iSave2);
            }
            for (int i2 = size - 1; i2 >= 0; i2--) {
                RecoverAnimation recoverAnimation2 = recoverAnimationList.get(i2);
                if (recoverAnimation2.mEnded && !recoverAnimation2.mIsPendingCleanup) {
                    recoverAnimationList.remove(i2);
                } else if (!recoverAnimation2.mEnded) {
                    z = true;
                }
            }
            if (z) {
                parent.invalidate();
            }
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            SwivelItemTouchUIUtilImpl.INSTANCE.clearView(viewHolder.itemView);
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            SwivelItemTouchUIUtilImpl.INSTANCE.onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);
        }

        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            SwivelItemTouchUIUtilImpl.INSTANCE.onDrawOver(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);
        }

        public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (itemAnimator == null) {
                return animationType == 8 ? 200L : 250L;
            }
            if (animationType == 8) {
                return itemAnimator.getMoveDuration();
            }
            return itemAnimator.getRemoveDuration();
        }

        public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
            int iSignum = (int) (((int) (((int) Math.signum(viewSizeOutOfBounds)) * getMaxDragScroll(recyclerView) * sDragViewScrollCapInterpolator.getInterpolation(Math.min(1.0f, (Math.abs(viewSizeOutOfBounds) * 1.0f) / viewSize)))) * sDragScrollInterpolator.getInterpolation(msSinceStartScroll <= DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS ? msSinceStartScroll / 500.0f : 1.0f));
            return iSignum == 0 ? viewSizeOutOfBounds > 0 ? 1 : -1 : iSignum;
        }
    }

    public static abstract class SimpleCallback extends Callback {
        private int mDefaultDragDirs;
        private int mDefaultSwipeDirs;

        public SimpleCallback(int dragDirs, int swipeDirs) {
            this.mDefaultSwipeDirs = swipeDirs;
            this.mDefaultDragDirs = dragDirs;
        }

        public void setDefaultSwipeDirs(int defaultSwipeDirs) {
            this.mDefaultSwipeDirs = defaultSwipeDirs;
        }

        public void setDefaultDragDirs(int defaultDragDirs) {
            this.mDefaultDragDirs = defaultDragDirs;
        }

        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return this.mDefaultSwipeDirs;
        }

        public int getDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return this.mDefaultDragDirs;
        }

        @Override // com.lge.launcher3.wing.SwivelItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(getDragDirs(recyclerView, viewHolder), getSwipeDirs(recyclerView, viewHolder));
        }
    }

    public class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean mShouldReactToLongPress = true;

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            return true;
        }

        ItemTouchHelperGestureListener() {
        }

        void doNotReactToLongPress() {
            this.mShouldReactToLongPress = false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent e) {
            View viewFindChildView;
            RecyclerView.ViewHolder childViewHolder;
            if (this.mShouldReactToLongPress && (viewFindChildView = SwivelItemTouchHelper.this.findChildView(e)) != null && (childViewHolder = SwivelItemTouchHelper.this.mRecyclerView.getChildViewHolder(viewFindChildView)) != null && SwivelItemTouchHelper.this.mCallback.hasDragFlag(SwivelItemTouchHelper.this.mRecyclerView, childViewHolder)) {
                SwivelItemTouchHelper.this.mCallback.onLongPress(e, viewFindChildView);
                if (e.getPointerId(0) == SwivelItemTouchHelper.this.mActivePointerId) {
                    int iFindPointerIndex = e.findPointerIndex(SwivelItemTouchHelper.this.mActivePointerId);
                    float x = e.getX(iFindPointerIndex);
                    float y = e.getY(iFindPointerIndex);
                    SwivelItemTouchHelper.this.mInitialTouchX = x;
                    SwivelItemTouchHelper.this.mInitialTouchY = y;
                    SwivelItemTouchHelper swivelItemTouchHelper = SwivelItemTouchHelper.this;
                    swivelItemTouchHelper.mDy = 0.0f;
                    swivelItemTouchHelper.mDx = 0.0f;
                    if (SwivelItemTouchHelper.this.mCallback.isLongPressDragEnabled()) {
                        SwivelItemTouchHelper.this.select(childViewHolder, 2);
                    }
                }
            }
        }
    }

    private static class RecoverAnimation implements Animator.AnimatorListener {
        final int mActionState;
        final int mAnimationType;
        private float mFraction;
        boolean mIsPendingCleanup;
        final float mStartDx;
        final float mStartDy;
        final float mTargetX;
        final float mTargetY;
        private final ValueAnimator mValueAnimator;
        final RecyclerView.ViewHolder mViewHolder;
        float mX;
        float mY;
        boolean mOverridden = false;
        boolean mEnded = false;

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
        }

        RecoverAnimation(RecyclerView.ViewHolder viewHolder, int animationType, int actionState, float startDx, float startDy, float targetX, float targetY) {
            this.mActionState = actionState;
            this.mAnimationType = animationType;
            this.mViewHolder = viewHolder;
            this.mStartDx = startDx;
            this.mStartDy = startDy;
            this.mTargetX = targetX;
            this.mTargetY = targetY;
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mValueAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.wing.SwivelItemTouchHelper.RecoverAnimation.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    RecoverAnimation.this.setFraction(animation.getAnimatedFraction());
                }
            });
            valueAnimatorOfFloat.setTarget(viewHolder.itemView);
            valueAnimatorOfFloat.addListener(this);
            setFraction(0.0f);
        }

        public void setDuration(long duration) {
            this.mValueAnimator.setDuration(duration);
        }

        public void start() {
            this.mViewHolder.setIsRecyclable(false);
            this.mValueAnimator.start();
        }

        public void cancel() {
            this.mValueAnimator.cancel();
        }

        public void setFraction(float fraction) {
            this.mFraction = fraction;
        }

        public void update() {
            float f = this.mStartDx;
            float f2 = this.mTargetX;
            if (f == f2) {
                this.mX = this.mViewHolder.itemView.getTranslationX();
            } else {
                this.mX = f + (this.mFraction * (f2 - f));
            }
            float f3 = this.mStartDy;
            float f4 = this.mTargetY;
            if (f3 == f4) {
                this.mY = this.mViewHolder.itemView.getTranslationY();
            } else {
                this.mY = f3 + (this.mFraction * (f4 - f3));
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            if (!this.mEnded) {
                this.mViewHolder.setIsRecyclable(true);
            }
            this.mEnded = true;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            setFraction(1.0f);
        }
    }
}
