package com.android.launcher3.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.lge.launcher3.R;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class SwipeHelper {
    private static final boolean CONSTRAIN_SWIPE = true;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_INVALIDATE = false;
    private static final boolean DISMISS_IF_SWIPED_FAR_ENOUGH = true;
    private static final boolean FADE_OUT_DURING_SWIPE = true;
    private static final boolean SLOW_ANIMATIONS = false;
    private static final int SNAP_ANIM_LEN = 150;
    static final float SWIPE_PROGRESS_FADE_END = 0.5f;
    static final String TAG = "SwipeHelper";
    public static final int X = 0;
    public static final int Y = 1;
    private Callback mCallback;
    private boolean mCanCurrViewBeDimissed;
    private View mCurrView;
    private float mDensityScale;
    private boolean mDisableHwLayers;
    private boolean mDragging;
    private int mFalsingThreshold;
    private FlingAnimationUtils mFlingAnimationUtils;
    private float mInitialTouchPos;
    private LongPressListener mLongPressListener;
    private boolean mLongPressSent;
    private float mPagingTouchSlop;
    private float mPerpendicularInitialTouchPos;
    private boolean mSnappingChild;
    private int mSwipeDirection;
    private boolean mTouchAboveFalsingThreshold;
    private Runnable mWatchLongPress;
    private float SWIPE_ESCAPE_VELOCITY = 100.0f;
    private int DEFAULT_ESCAPE_ANIMATION_DURATION = 200;
    private int MAX_ESCAPE_ANIMATION_DURATION = 400;
    private int MAX_DISMISS_VELOCITY = 4000;
    private float mMinSwipeProgress = 0.0f;
    private float mMaxSwipeProgress = 1.0f;
    private float mTranslation = 0.0f;
    private final int[] mTmpPos = new int[2];
    private HashMap<View, Animator> mDismissPendingMap = new HashMap<>();
    private Handler mHandler = new Handler();
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private long mLongPressTimeout = (long) (ViewConfiguration.getLongPressTimeout() * 1.5f);

    public interface Callback {
        boolean canChildBeDismissed(View v);

        View getChildAtPosition(MotionEvent ev);

        float getFalsingThresholdFactor();

        boolean isAntiFalsingNeeded();

        void onBeginDrag(View v);

        void onChildDismissed(View v);

        void onChildSnappedBack(View animView, float targetLeft);

        void onDragCancelled(View v);

        boolean updateSwipeProgress(View animView, boolean dismissable, float swipeProgress);
    }

    public interface LongPressListener {
        boolean onLongPress(View v, int x, int y);
    }

    protected boolean handleUpEvent(MotionEvent ev, View animView, float velocity, float translation) {
        return false;
    }

    public void onDownUpdate(View currView) {
    }

    protected void onMoveUpdate(View view, float totalTranslation, float delta) {
    }

    protected void prepareDismissAnimation(View view, Animator anim) {
    }

    protected void prepareSnapBackAnimation(View view, Animator anim) {
    }

    public SwipeHelper(int swipeDirection, Callback callback, Context context) {
        this.mCallback = callback;
        this.mSwipeDirection = swipeDirection;
        this.mDensityScale = context.getResources().getDisplayMetrics().density;
        this.mPagingTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        this.mFalsingThreshold = context.getResources().getDimensionPixelSize(R.dimen.swipe_helper_falsing_threshold);
        this.mFlingAnimationUtils = new FlingAnimationUtils(context, getMaxEscapeAnimDuration() / 1000.0f);
    }

    public void setLongPressListener(LongPressListener listener) {
        this.mLongPressListener = listener;
    }

    public void setDensityScale(float densityScale) {
        this.mDensityScale = densityScale;
    }

    public void setPagingTouchSlop(float pagingTouchSlop) {
        this.mPagingTouchSlop = pagingTouchSlop;
    }

    public void setDisableHardwareLayers(boolean disableHwLayers) {
        this.mDisableHwLayers = disableHwLayers;
    }

    private float getPos(MotionEvent ev) {
        return this.mSwipeDirection == 0 ? ev.getX() : ev.getY();
    }

    private float getPerpendicularPos(MotionEvent ev) {
        return this.mSwipeDirection == 0 ? ev.getY() : ev.getX();
    }

    protected float getTranslation(View v) {
        return this.mSwipeDirection == 0 ? v.getTranslationX() : v.getTranslationY();
    }

    private float getVelocity(VelocityTracker vt) {
        return this.mSwipeDirection == 0 ? vt.getXVelocity() : vt.getYVelocity();
    }

    protected ObjectAnimator createTranslationAnimation(View v, float newPos) {
        return ObjectAnimator.ofFloat(v, (Property<View, Float>) (this.mSwipeDirection == 0 ? View.TRANSLATION_X : View.TRANSLATION_Y), newPos);
    }

    private float getPerpendicularVelocity(VelocityTracker vt) {
        return this.mSwipeDirection == 0 ? vt.getYVelocity() : vt.getXVelocity();
    }

    protected Animator getViewTranslationAnimator(View v, float target, ValueAnimator.AnimatorUpdateListener listener) {
        ObjectAnimator objectAnimatorCreateTranslationAnimation = createTranslationAnimation(v, target);
        if (listener != null) {
            objectAnimatorCreateTranslationAnimation.addUpdateListener(listener);
        }
        return objectAnimatorCreateTranslationAnimation;
    }

    protected void setTranslation(View v, float translate) {
        if (v == null) {
            return;
        }
        if (this.mSwipeDirection == 0) {
            v.setTranslationX(translate);
        } else {
            v.setTranslationY(translate);
        }
    }

    protected float getSize(View v) {
        return this.mSwipeDirection == 0 ? v.getMeasuredWidth() : v.getMeasuredHeight();
    }

    public void setMinSwipeProgress(float minSwipeProgress) {
        this.mMinSwipeProgress = minSwipeProgress;
    }

    public void setMaxSwipeProgress(float maxSwipeProgress) {
        this.mMaxSwipeProgress = maxSwipeProgress;
    }

    private float getSwipeProgressForOffset(View view, float translation) {
        return Math.min(Math.max(this.mMinSwipeProgress, Math.abs(translation / getSize(view))), this.mMaxSwipeProgress);
    }

    private float getSwipeAlpha(float progress) {
        return Math.min(0.0f, Math.max(1.0f, progress / 0.5f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSwipeProgressFromOffset(View animView, boolean dismissable) {
        updateSwipeProgressFromOffset(animView, dismissable, getTranslation(animView));
    }

    private void updateSwipeProgressFromOffset(View animView, boolean dismissable, float translation) {
        float swipeProgressForOffset = getSwipeProgressForOffset(animView, translation);
        if (!this.mCallback.updateSwipeProgress(animView, dismissable, swipeProgressForOffset) && dismissable) {
            if (!this.mDisableHwLayers) {
                if (swipeProgressForOffset != 0.0f && swipeProgressForOffset != 1.0f) {
                    animView.setLayerType(2, null);
                } else {
                    animView.setLayerType(0, null);
                }
            }
            animView.setAlpha(getSwipeAlpha(swipeProgressForOffset));
        }
        invalidateGlobalRegion(animView);
    }

    public static void invalidateGlobalRegion(View view) {
        invalidateGlobalRegion(view, new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
    }

    public static void invalidateGlobalRegion(View view, RectF childBounds) {
        while (view.getParent() != null && (view.getParent() instanceof View)) {
            view = (View) view.getParent();
            view.getMatrix().mapRect(childBounds);
            view.invalidate((int) Math.floor(childBounds.left), (int) Math.floor(childBounds.top), (int) Math.ceil(childBounds.right), (int) Math.ceil(childBounds.bottom));
        }
    }

    public void removeLongPressCallback() {
        Runnable runnable = this.mWatchLongPress;
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
            this.mWatchLongPress = null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x005e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onInterceptTouchEvent(final android.view.MotionEvent r7) {
        /*
            r6 = this;
            int r0 = r7.getAction()
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L77
            if (r0 == r1) goto L5e
            r3 = 2
            if (r0 == r3) goto L12
            r7 = 3
            if (r0 == r7) goto L5e
            goto Lcc
        L12:
            android.view.View r0 = r6.mCurrView
            if (r0 == 0) goto Lcc
            boolean r0 = r6.mLongPressSent
            if (r0 != 0) goto Lcc
            android.view.VelocityTracker r0 = r6.mVelocityTracker
            r0.addMovement(r7)
            float r0 = r6.getPos(r7)
            float r3 = r6.getPerpendicularPos(r7)
            float r4 = r6.mInitialTouchPos
            float r0 = r0 - r4
            float r4 = r6.mPerpendicularInitialTouchPos
            float r3 = r3 - r4
            float r4 = java.lang.Math.abs(r0)
            float r5 = r6.mPagingTouchSlop
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto Lcc
            float r0 = java.lang.Math.abs(r0)
            float r3 = java.lang.Math.abs(r3)
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto Lcc
            com.android.launcher3.notification.SwipeHelper$Callback r0 = r6.mCallback
            android.view.View r3 = r6.mCurrView
            r0.onBeginDrag(r3)
            r6.mDragging = r1
            float r7 = r6.getPos(r7)
            r6.mInitialTouchPos = r7
            android.view.View r7 = r6.mCurrView
            float r7 = r6.getTranslation(r7)
            r6.mTranslation = r7
            r6.removeLongPressCallback()
            goto Lcc
        L5e:
            boolean r7 = r6.mDragging
            if (r7 != 0) goto L69
            boolean r7 = r6.mLongPressSent
            if (r7 == 0) goto L67
            goto L69
        L67:
            r7 = r2
            goto L6a
        L69:
            r7 = r1
        L6a:
            r6.mDragging = r2
            r0 = 0
            r6.mCurrView = r0
            r6.mLongPressSent = r2
            r6.removeLongPressCallback()
            if (r7 == 0) goto Lcc
            return r1
        L77:
            r6.mTouchAboveFalsingThreshold = r2
            r6.mDragging = r2
            r6.mSnappingChild = r2
            r6.mLongPressSent = r2
            android.view.VelocityTracker r0 = r6.mVelocityTracker
            r0.clear()
            com.android.launcher3.notification.SwipeHelper$Callback r0 = r6.mCallback
            android.view.View r0 = r0.getChildAtPosition(r7)
            r6.mCurrView = r0
            if (r0 == 0) goto Lcc
            r6.onDownUpdate(r0)
            com.android.launcher3.notification.SwipeHelper$Callback r0 = r6.mCallback
            android.view.View r3 = r6.mCurrView
            boolean r0 = r0.canChildBeDismissed(r3)
            r6.mCanCurrViewBeDimissed = r0
            android.view.VelocityTracker r0 = r6.mVelocityTracker
            r0.addMovement(r7)
            float r0 = r6.getPos(r7)
            r6.mInitialTouchPos = r0
            float r0 = r6.getPerpendicularPos(r7)
            r6.mPerpendicularInitialTouchPos = r0
            android.view.View r0 = r6.mCurrView
            float r0 = r6.getTranslation(r0)
            r6.mTranslation = r0
            com.android.launcher3.notification.SwipeHelper$LongPressListener r0 = r6.mLongPressListener
            if (r0 == 0) goto Lcc
            java.lang.Runnable r0 = r6.mWatchLongPress
            if (r0 != 0) goto Lc3
            com.android.launcher3.notification.SwipeHelper$1 r0 = new com.android.launcher3.notification.SwipeHelper$1
            r0.<init>()
            r6.mWatchLongPress = r0
        Lc3:
            android.os.Handler r7 = r6.mHandler
            java.lang.Runnable r0 = r6.mWatchLongPress
            long r3 = r6.mLongPressTimeout
            r7.postDelayed(r0, r3)
        Lcc:
            boolean r7 = r6.mDragging
            if (r7 != 0) goto Ld6
            boolean r7 = r6.mLongPressSent
            if (r7 == 0) goto Ld5
            goto Ld6
        Ld5:
            r1 = r2
        Ld6:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.notification.SwipeHelper.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    public void dismissChild(final View view, float velocity, boolean useAccelerateInterpolator) {
        dismissChild(view, velocity, null, 0L, useAccelerateInterpolator, 0L, false);
    }

    public void dismissChild(final View animView, float velocity, final Runnable endAction, long delay, boolean useAccelerateInterpolator, long fixedDuration, boolean isDismissAll) {
        float size;
        long jMin;
        final boolean zCanChildBeDismissed = this.mCallback.canChildBeDismissed(animView);
        boolean z = false;
        boolean z2 = animView.getLayoutDirection() == 1;
        boolean z3 = velocity == 0.0f && (getTranslation(animView) == 0.0f || isDismissAll) && this.mSwipeDirection == 1;
        boolean z4 = velocity == 0.0f && (getTranslation(animView) == 0.0f || isDismissAll) && z2;
        if (velocity < 0.0f || (velocity == 0.0f && getTranslation(animView) < 0.0f && !isDismissAll)) {
            z = true;
        }
        if (z || z4 || z3) {
            size = -getSize(animView);
        } else {
            size = getSize(animView);
        }
        float f = size;
        if (fixedDuration == 0) {
            long j = this.MAX_ESCAPE_ANIMATION_DURATION;
            if (velocity != 0.0f) {
                jMin = Math.min(j, (int) ((Math.abs(f - getTranslation(animView)) * 1000.0f) / Math.abs(velocity)));
            } else {
                jMin = this.DEFAULT_ESCAPE_ANIMATION_DURATION;
            }
        } else {
            jMin = fixedDuration;
        }
        if (!this.mDisableHwLayers) {
            animView.setLayerType(2, null);
        }
        Animator viewTranslationAnimator = getViewTranslationAnimator(animView, f, new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.notification.SwipeHelper.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                SwipeHelper.this.onTranslationUpdate(animView, ((Float) animation.getAnimatedValue()).floatValue(), zCanChildBeDismissed);
            }
        });
        if (viewTranslationAnimator == null) {
            return;
        }
        if (useAccelerateInterpolator) {
            viewTranslationAnimator.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
            viewTranslationAnimator.setDuration(jMin);
        } else {
            this.mFlingAnimationUtils.applyDismissing(viewTranslationAnimator, getTranslation(animView), f, velocity, getSize(animView));
        }
        if (delay > 0) {
            viewTranslationAnimator.setStartDelay(delay);
        }
        viewTranslationAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.notification.SwipeHelper.3
            private boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                SwipeHelper.this.updateSwipeProgressFromOffset(animView, zCanChildBeDismissed);
                SwipeHelper.this.mDismissPendingMap.remove(animView);
                if (!this.mCancelled) {
                    SwipeHelper.this.mCallback.onChildDismissed(animView);
                }
                Runnable runnable = endAction;
                if (runnable != null) {
                    runnable.run();
                }
                if (SwipeHelper.this.mDisableHwLayers) {
                    return;
                }
                animView.setLayerType(0, null);
            }
        });
        prepareDismissAnimation(animView, viewTranslationAnimator);
        this.mDismissPendingMap.put(animView, viewTranslationAnimator);
        viewTranslationAnimator.start();
    }

    public void snapChild(final View animView, final float targetLeft, float velocity) {
        final boolean zCanChildBeDismissed = this.mCallback.canChildBeDismissed(animView);
        Animator viewTranslationAnimator = getViewTranslationAnimator(animView, targetLeft, new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.notification.SwipeHelper.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                SwipeHelper.this.onTranslationUpdate(animView, ((Float) animation.getAnimatedValue()).floatValue(), zCanChildBeDismissed);
            }
        });
        if (viewTranslationAnimator == null) {
            return;
        }
        viewTranslationAnimator.setDuration(150);
        viewTranslationAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.notification.SwipeHelper.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SwipeHelper.this.mSnappingChild = false;
                SwipeHelper.this.updateSwipeProgressFromOffset(animView, zCanChildBeDismissed);
                SwipeHelper.this.mCallback.onChildSnappedBack(animView, targetLeft);
            }
        });
        prepareSnapBackAnimation(animView, viewTranslationAnimator);
        this.mSnappingChild = true;
        viewTranslationAnimator.start();
    }

    public void onTranslationUpdate(View animView, float value, boolean canBeDismissed) {
        updateSwipeProgressFromOffset(animView, canBeDismissed, value);
    }

    private void snapChildInstantly(final View view) {
        boolean zCanChildBeDismissed = this.mCallback.canChildBeDismissed(view);
        setTranslation(view, 0.0f);
        updateSwipeProgressFromOffset(view, zCanChildBeDismissed);
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x002a  */
    /* JADX WARN: Removed duplicated region for block: B:22:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void snapChildIfNeeded(final android.view.View r5, boolean r6, float r7) {
        /*
            r4 = this;
            boolean r0 = r4.mDragging
            if (r0 == 0) goto L8
            android.view.View r0 = r4.mCurrView
            if (r0 == r5) goto Lc
        L8:
            boolean r0 = r4.mSnappingChild
            if (r0 == 0) goto Ld
        Lc:
            return
        Ld:
            r0 = 0
            java.util.HashMap<android.view.View, android.animation.Animator> r1 = r4.mDismissPendingMap
            java.lang.Object r1 = r1.get(r5)
            android.animation.Animator r1 = (android.animation.Animator) r1
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L1f
            r1.cancel()
        L1d:
            r0 = r2
            goto L28
        L1f:
            float r1 = r4.getTranslation(r5)
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 == 0) goto L28
            goto L1d
        L28:
            if (r0 == 0) goto L33
            if (r6 == 0) goto L30
            r4.snapChild(r5, r7, r3)
            goto L33
        L30:
            r4.snapChildInstantly(r5)
        L33:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.notification.SwipeHelper.snapChildIfNeeded(android.view.View, boolean, float):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0032  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0091  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            boolean r0 = r7.mLongPressSent
            r1 = 1
            if (r0 == 0) goto L6
            return r1
        L6:
            boolean r0 = r7.mDragging
            r2 = 0
            if (r0 != 0) goto L1b
            com.android.launcher3.notification.SwipeHelper$Callback r0 = r7.mCallback
            android.view.View r0 = r0.getChildAtPosition(r8)
            if (r0 == 0) goto L17
            r7.onInterceptTouchEvent(r8)
            return r1
        L17:
            r7.removeLongPressCallback()
            return r2
        L1b:
            android.view.VelocityTracker r0 = r7.mVelocityTracker
            r0.addMovement(r8)
            int r0 = r8.getAction()
            r3 = 0
            if (r0 == r1) goto L91
            r4 = 2
            if (r0 == r4) goto L32
            r4 = 3
            if (r0 == r4) goto L91
            r2 = 4
            if (r0 == r2) goto L32
            goto Ld5
        L32:
            android.view.View r0 = r7.mCurrView
            if (r0 == 0) goto Ld5
            float r8 = r7.getPos(r8)
            float r0 = r7.mInitialTouchPos
            float r8 = r8 - r0
            float r0 = java.lang.Math.abs(r8)
            int r2 = r7.getFalsingThreshold()
            float r2 = (float) r2
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 < 0) goto L4c
            r7.mTouchAboveFalsingThreshold = r1
        L4c:
            com.android.launcher3.notification.SwipeHelper$Callback r2 = r7.mCallback
            android.view.View r4 = r7.mCurrView
            boolean r2 = r2.canChildBeDismissed(r4)
            if (r2 != 0) goto L79
            android.view.View r2 = r7.mCurrView
            float r2 = r7.getSize(r2)
            r4 = 1048576000(0x3e800000, float:0.25)
            float r4 = r4 * r2
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 < 0) goto L6b
            int r8 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r8 <= 0) goto L69
            r8 = r4
            goto L79
        L69:
            float r8 = -r4
            goto L79
        L6b:
            float r8 = r8 / r2
            double r2 = (double) r8
            r5 = 4609753056924675352(0x3ff921fb54442d18, double:1.5707963267948966)
            double r2 = r2 * r5
            double r2 = java.lang.Math.sin(r2)
            float r8 = (float) r2
            float r8 = r8 * r4
        L79:
            android.view.View r0 = r7.mCurrView
            float r2 = r7.mTranslation
            float r2 = r2 + r8
            r7.setTranslation(r0, r2)
            android.view.View r0 = r7.mCurrView
            boolean r2 = r7.mCanCurrViewBeDimissed
            r7.updateSwipeProgressFromOffset(r0, r2)
            android.view.View r0 = r7.mCurrView
            float r2 = r7.mTranslation
            float r2 = r2 + r8
            r7.onMoveUpdate(r0, r2, r8)
            goto Ld5
        L91:
            android.view.View r0 = r7.mCurrView
            if (r0 != 0) goto L96
            goto Ld5
        L96:
            android.view.VelocityTracker r0 = r7.mVelocityTracker
            r4 = 1000(0x3e8, float:1.401E-42)
            float r5 = r7.getMaxVelocity()
            r0.computeCurrentVelocity(r4, r5)
            android.view.VelocityTracker r0 = r7.mVelocityTracker
            float r0 = r7.getVelocity(r0)
            android.view.View r4 = r7.mCurrView
            float r5 = r7.getTranslation(r4)
            boolean r4 = r7.handleUpEvent(r8, r4, r0, r5)
            if (r4 != 0) goto Ld3
            boolean r8 = r7.isDismissGesture(r8)
            if (r8 == 0) goto Lc4
            android.view.View r8 = r7.mCurrView
            boolean r3 = r7.swipedFastEnough()
            r3 = r3 ^ r1
            r7.dismissChild(r8, r0, r3)
            goto Ld0
        Lc4:
            com.android.launcher3.notification.SwipeHelper$Callback r8 = r7.mCallback
            android.view.View r4 = r7.mCurrView
            r8.onDragCancelled(r4)
            android.view.View r8 = r7.mCurrView
            r7.snapChild(r8, r3, r0)
        Ld0:
            r8 = 0
            r7.mCurrView = r8
        Ld3:
            r7.mDragging = r2
        Ld5:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.notification.SwipeHelper.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private int getFalsingThreshold() {
        return (int) (this.mFalsingThreshold * this.mCallback.getFalsingThresholdFactor());
    }

    private float getMaxVelocity() {
        return this.MAX_DISMISS_VELOCITY * this.mDensityScale;
    }

    protected float getEscapeVelocity() {
        return getUnscaledEscapeVelocity() * this.mDensityScale;
    }

    protected float getUnscaledEscapeVelocity() {
        return this.SWIPE_ESCAPE_VELOCITY;
    }

    protected long getMaxEscapeAnimDuration() {
        return this.MAX_ESCAPE_ANIMATION_DURATION;
    }

    protected boolean swipedFarEnough() {
        return ((double) Math.abs(getTranslation(this.mCurrView))) > ((double) getSize(this.mCurrView)) * 0.4d;
    }

    protected boolean isDismissGesture(MotionEvent ev) {
        if (this.mCallback.isAntiFalsingNeeded() && !this.mTouchAboveFalsingThreshold) {
            return false;
        }
        return (swipedFastEnough() || swipedFarEnough()) && ev.getActionMasked() == 1 && this.mCallback.canChildBeDismissed(this.mCurrView);
    }

    protected boolean swipedFastEnough() {
        float velocity = getVelocity(this.mVelocityTracker);
        float translation = getTranslation(this.mCurrView);
        if (Math.abs(velocity) > getEscapeVelocity()) {
            if ((velocity > 0.0f) == (translation > 0.0f)) {
                return true;
            }
        }
        return false;
    }
}
