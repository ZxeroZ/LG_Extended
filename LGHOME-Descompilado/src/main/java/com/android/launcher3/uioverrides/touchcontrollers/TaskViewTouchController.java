package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.FlingBlockCheck;
import com.android.launcher3.util.TouchController;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;

/* JADX INFO: loaded from: classes.dex */
public abstract class TaskViewTouchController<T extends BaseDraggingActivity> extends AnimatorListenerAdapter implements TouchController, SingleAxisSwipeDetector.Listener {
    private static final float BLOCK_TRANSITION_PROGRESS = 0.08f;
    public static final float SUCCESS_TRANSITION_PROGRESS = 0.5f;
    protected final T mActivity;
    private AnimatorPlaybackController mCurrentAnimation;
    private boolean mCurrentAnimationIsGoingUp;
    private final SingleAxisSwipeDetector mDetector;
    private float mDisplacementShift;
    private float mEndDisplacement;
    private final boolean mIsRtl;
    private boolean mNoIntercept;
    private PendingAnimation mPendingAnimation;
    private float mProgressMultiplier;
    private final RecentsView mRecentsView;
    private TaskView mTaskBeingDragged;
    private final int[] mTempCords = new int[2];
    private FlingBlockCheck mFlingBlockCheck = new FlingBlockCheck();

    protected abstract boolean isRecentsInteractive();

    protected abstract boolean isRecentsModal();

    protected void onUserControlledAnimationCreated(AnimatorPlaybackController animController) {
    }

    public TaskViewTouchController(T activity) {
        this.mActivity = activity;
        RecentsView recentsView = (RecentsView) activity.getOverviewPanel();
        this.mRecentsView = recentsView;
        boolean zIsRtl = Utilities.isRtl(activity.getResources());
        this.mIsRtl = zIsRtl;
        this.mDetector = new SingleAxisSwipeDetector(activity, this, recentsView.getPagedOrientationHandler().getOppositeSwipeDirection(zIsRtl));
    }

    private boolean canInterceptTouch() {
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.forceFinishIfCloseToEnd();
        }
        if (this.mCurrentAnimation != null) {
            return true;
        }
        if (AbstractFloatingView.getTopOpenViewWithType(this.mActivity, AbstractFloatingView.TYPE_ACCESSIBLE) != null) {
            return false;
        }
        return isRecentsInteractive();
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animation) {
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController == null || animation != animatorPlaybackController.getTarget()) {
            return;
        }
        clearState();
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x0074, code lost:
    
        r1 = 0;
     */
    @Override // com.android.launcher3.util.TouchController
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onControllerInterceptTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            int r0 = r8.getAction()
            r1 = 3
            r2 = 1
            if (r0 == r2) goto Le
            int r0 = r8.getAction()
            if (r0 != r1) goto L15
        Le:
            com.android.launcher3.anim.AnimatorPlaybackController r0 = r7.mCurrentAnimation
            if (r0 != 0) goto L15
            r7.clearState()
        L15:
            int r0 = r8.getAction()
            r3 = 0
            if (r0 != 0) goto L82
            boolean r0 = r7.canInterceptTouch()
            r0 = r0 ^ r2
            r7.mNoIntercept = r0
            if (r0 == 0) goto L26
            return r3
        L26:
            com.android.launcher3.anim.AnimatorPlaybackController r0 = r7.mCurrentAnimation
            if (r0 == 0) goto L2b
            goto L7d
        L2b:
            r0 = 0
            r7.mTaskBeingDragged = r0
            r4 = r3
        L2f:
            com.android.quickstep.views.RecentsView r5 = r7.mRecentsView
            int r5 = r5.getTaskViewCount()
            if (r4 >= r5) goto L74
            com.android.quickstep.views.RecentsView r5 = r7.mRecentsView
            com.android.quickstep.views.TaskView r5 = r5.getTaskViewAt(r4)
            com.android.quickstep.views.RecentsView r6 = r7.mRecentsView
            boolean r6 = r6.isTaskViewVisible(r5)
            if (r6 == 0) goto L71
            T extends com.android.launcher3.BaseDraggingActivity r6 = r7.mActivity
            com.android.launcher3.views.BaseDragLayer r6 = r6.getDragLayer()
            boolean r6 = r6.isEventOverView(r5, r8)
            if (r6 == 0) goto L71
            boolean r6 = r7.isRecentsModal()
            if (r6 == 0) goto L5a
            r7.mTaskBeingDragged = r0
            goto L74
        L5a:
            r7.mTaskBeingDragged = r5
            T extends com.android.launcher3.BaseDraggingActivity r0 = r7.mActivity
            com.android.quickstep.SysUINavigationMode$Mode r0 = com.android.quickstep.SysUINavigationMode.getMode(r0)
            boolean r0 = r0.hasGestures
            if (r0 != 0) goto L68
        L66:
            r1 = r2
            goto L75
        L68:
            com.android.quickstep.views.RecentsView r0 = r7.mRecentsView
            int r0 = r0.getCurrentPage()
            if (r4 != r0) goto L66
            goto L75
        L71:
            int r4 = r4 + 1
            goto L2f
        L74:
            r1 = r3
        L75:
            com.android.quickstep.views.TaskView r0 = r7.mTaskBeingDragged
            if (r0 != 0) goto L7c
            r7.mNoIntercept = r2
            return r3
        L7c:
            r2 = r3
        L7d:
            com.android.launcher3.touch.SingleAxisSwipeDetector r0 = r7.mDetector
            r0.setDetectableScrollConditions(r1, r2)
        L82:
            boolean r0 = r7.mNoIntercept
            if (r0 == 0) goto L87
            return r3
        L87:
            r7.onControllerTouchEvent(r8)
            com.android.launcher3.touch.SingleAxisSwipeDetector r8 = r7.mDetector
            boolean r8 = r8.isDraggingOrSettling()
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController.onControllerInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerTouchEvent(MotionEvent ev) {
        return this.mDetector.onTouchEvent(ev);
    }

    private void reInitAnimationController(boolean goingUp) {
        if (this.mCurrentAnimation == null || this.mCurrentAnimationIsGoingUp != goingUp) {
            int scrollDirections = this.mDetector.getScrollDirections();
            if (goingUp && (scrollDirections & 1) == 0) {
                return;
            }
            if (goingUp || (scrollDirections & 2) != 0) {
                AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
                if (animatorPlaybackController != null) {
                    animatorPlaybackController.setPlayFraction(0.0f);
                }
                PendingAnimation pendingAnimation = this.mPendingAnimation;
                if (pendingAnimation != null) {
                    pendingAnimation.finish(false, 3);
                    this.mPendingAnimation = null;
                }
                PagedOrientationHandler pagedOrientationHandler = this.mRecentsView.getPagedOrientationHandler();
                this.mCurrentAnimationIsGoingUp = goingUp;
                long secondaryDimension = pagedOrientationHandler.getSecondaryDimension(this.mActivity.getDragLayer()) * 2;
                int taskDragDisplacementFactor = pagedOrientationHandler.getTaskDragDisplacementFactor(this.mIsRtl);
                int secondaryDimension2 = pagedOrientationHandler.getSecondaryDimension(this.mTaskBeingDragged);
                if (goingUp) {
                    this.mPendingAnimation = this.mRecentsView.createTaskDismissAnimation(this.mTaskBeingDragged, true, true, secondaryDimension);
                    this.mEndDisplacement = -secondaryDimension2;
                } else {
                    this.mPendingAnimation = this.mRecentsView.createTaskLaunchAnimation(this.mTaskBeingDragged, secondaryDimension, Interpolators.ZOOM_IN);
                    this.mTempCords[1] = pagedOrientationHandler.getSecondaryDimension(this.mTaskBeingDragged.getThumbnail());
                    this.mEndDisplacement = r2 - this.mTempCords[1];
                }
                this.mEndDisplacement *= taskDragDisplacementFactor;
                AnimatorPlaybackController animatorPlaybackController2 = this.mCurrentAnimation;
                if (animatorPlaybackController2 != null) {
                    animatorPlaybackController2.setOnCancelRunnable(null);
                }
                AnimatorPlaybackController onCancelRunnable = this.mPendingAnimation.createPlaybackController().setOnCancelRunnable(new Runnable() { // from class: com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$TaskViewTouchController$Ja_Yqyl9PP4YeC53fimSUxLy-74
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.clearState();
                    }
                });
                this.mCurrentAnimation = onCancelRunnable;
                onUserControlledAnimationCreated(onCancelRunnable);
                this.mCurrentAnimation.getTarget().addListener(this);
                this.mCurrentAnimation.dispatchOnStart();
                this.mProgressMultiplier = 1.0f / this.mEndDisplacement;
            }
        }
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public void onDragStart(boolean start, float startDisplacement) {
        PagedOrientationHandler pagedOrientationHandler = this.mRecentsView.getPagedOrientationHandler();
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController == null) {
            reInitAnimationController(pagedOrientationHandler.isGoingUp(startDisplacement, this.mIsRtl));
            this.mDisplacementShift = 0.0f;
        } else {
            this.mDisplacementShift = animatorPlaybackController.getProgressFraction() / this.mProgressMultiplier;
            this.mCurrentAnimation.pause();
        }
        this.mFlingBlockCheck.unblockFling();
    }

    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    public boolean onDrag(float displacement) {
        boolean zIsGoingUp;
        PagedOrientationHandler pagedOrientationHandler = this.mRecentsView.getPagedOrientationHandler();
        float f = displacement + this.mDisplacementShift;
        if (f == 0.0f) {
            zIsGoingUp = this.mCurrentAnimationIsGoingUp;
        } else {
            zIsGoingUp = pagedOrientationHandler.isGoingUp(f, this.mIsRtl);
        }
        boolean z = this.mTaskBeingDragged.isPinned() && zIsGoingUp;
        if (zIsGoingUp != this.mCurrentAnimationIsGoingUp) {
            reInitAnimationController(zIsGoingUp);
            this.mFlingBlockCheck.blockFling();
        } else if (z) {
            this.mFlingBlockCheck.blockFling();
        } else {
            this.mFlingBlockCheck.onEvent();
        }
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController != null && (!z || (z && this.mProgressMultiplier * f < BLOCK_TRANSITION_PROGRESS))) {
            animatorPlaybackController.setPlayFraction(Utilities.boundToRange(f * this.mProgressMultiplier, 0.0f, 1.0f));
        }
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && (this.mRecentsView.getCurrentPage() != 0 || zIsGoingUp)) {
            this.mRecentsView.redrawLiveTile(true);
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x003f A[PHI: r0
      0x003f: PHI (r0v4 int) = (r0v3 int), (r0v9 int) binds: [B:16:0x003d, B:13:0x0035] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0041 A[PHI: r0
      0x0041: PHI (r0v8 int) = (r0v3 int), (r0v9 int) binds: [B:16:0x003d, B:13:0x0035] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Listener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onDragEnd(float r14) {
        /*
            r13 = this;
            com.android.launcher3.touch.SingleAxisSwipeDetector r0 = r13.mDetector
            boolean r0 = r0.isFling(r14)
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L14
            com.android.launcher3.util.FlingBlockCheck r3 = r13.mFlingBlockCheck
            boolean r3 = r3.isBlocked()
            if (r3 == 0) goto L14
            r3 = r1
            goto L15
        L14:
            r3 = r2
        L15:
            if (r3 == 0) goto L18
            r0 = r2
        L18:
            com.android.quickstep.views.RecentsView r4 = r13.mRecentsView
            com.android.launcher3.touch.PagedOrientationHandler r4 = r4.getPagedOrientationHandler()
            com.android.launcher3.anim.AnimatorPlaybackController r5 = r13.mCurrentAnimation
            float r5 = r5.getProgressFraction()
            com.android.launcher3.anim.AnimatorPlaybackController r6 = r13.mCurrentAnimation
            float r6 = r6.getInterpolatedProgress()
            if (r0 == 0) goto L38
            r0 = 4
            boolean r6 = r13.mIsRtl
            boolean r4 = r4.isGoingUp(r14, r6)
            boolean r6 = r13.mCurrentAnimationIsGoingUp
            if (r4 != r6) goto L41
            goto L3f
        L38:
            r0 = 3
            r4 = 1056964608(0x3f000000, float:0.5)
            int r4 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r4 <= 0) goto L41
        L3f:
            r4 = r1
            goto L42
        L41:
            r4 = r2
        L42:
            r8 = r4
            r4 = 1065353216(0x3f800000, float:1.0)
            if (r8 == 0) goto L4a
            float r6 = r4 - r5
            goto L4b
        L4a:
            r6 = r5
        L4b:
            long r6 = com.android.launcher3.touch.BaseSwipeDetector.calculateDuration(r14, r6)
            if (r3 == 0) goto L59
            if (r8 != 0) goto L59
            int r3 = com.android.launcher3.LauncherAnimUtils.blockedFlingDurationFactor(r14)
            long r9 = (long) r3
            long r6 = r6 * r9
        L59:
            r11 = r6
            r3 = 1098907648(0x41800000, float:16.0)
            float r3 = r3 * r14
            float r6 = r13.mEndDisplacement
            float r6 = java.lang.Math.abs(r6)
            float r3 = r3 / r6
            float r5 = r5 + r3
            r3 = 0
            float r5 = com.android.launcher3.Utilities.boundToRange(r5, r3, r4)
            boolean r6 = r13.mCurrentAnimationIsGoingUp
            if (r6 == 0) goto L80
            com.android.launcher3.util.FlingBlockCheck r6 = r13.mFlingBlockCheck
            boolean r6 = r6.isBlocked()
            if (r6 == 0) goto L80
            com.android.quickstep.views.TaskView r6 = r13.mTaskBeingDragged
            boolean r6 = r6.isPinned()
            if (r6 == 0) goto L80
            r6 = r1
            goto L81
        L80:
            r6 = r2
        L81:
            com.android.launcher3.anim.AnimatorPlaybackController r7 = r13.mCurrentAnimation
            com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$TaskViewTouchController$Szn7nh3yioNfP-CcKbYt83GgFCQ r9 = new com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$TaskViewTouchController$Szn7nh3yioNfP-CcKbYt83GgFCQ
            r9.<init>()
            r7.setEndAction(r9)
            com.android.launcher3.anim.AnimatorPlaybackController r0 = r13.mCurrentAnimation
            android.animation.ValueAnimator r0 = r0.getAnimationPlayer()
            r6 = 2
            float[] r6 = new float[r6]
            r6[r2] = r5
            if (r8 == 0) goto L99
            goto L9a
        L99:
            r4 = r3
        L9a:
            r6[r1] = r4
            r0.setFloatValues(r6)
            r0.setDuration(r11)
            android.view.animation.Interpolator r1 = com.android.launcher3.anim.Interpolators.scrollInterpolatorForVelocity(r14)
            r0.setInterpolator(r1)
            com.android.launcher3.config.FeatureFlags$BooleanFlag r1 = com.android.launcher3.config.FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE
            boolean r1 = r1.get()
            if (r1 == 0) goto Lbf
            com.android.launcher3.anim.AnimatorPlaybackController r1 = r13.mCurrentAnimation
            android.animation.ValueAnimator r1 = r1.getAnimationPlayer()
            com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$TaskViewTouchController$V8tAN0tQwUQ_fpODV4O-VV5lKWI r2 = new com.android.launcher3.uioverrides.touchcontrollers.-$$Lambda$TaskViewTouchController$V8tAN0tQwUQ_fpODV4O-VV5lKWI
            r2.<init>()
            r1.addUpdateListener(r2)
        Lbf:
            com.android.launcher3.anim.AnimatorPlaybackController r6 = r13.mCurrentAnimation
            T extends com.android.launcher3.BaseDraggingActivity r7 = r13.mActivity
            float r10 = r13.mEndDisplacement
            r9 = r14
            r6.startWithVelocity(r7, r8, r9, r10, r11)
            r0.start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController.onDragEnd(float):void");
    }

    public /* synthetic */ void lambda$onDragEnd$1$TaskViewTouchController(ValueAnimator valueAnimator) {
        if (this.mRecentsView.getCurrentPage() != 0 || this.mCurrentAnimationIsGoingUp) {
            this.mRecentsView.redrawLiveTile(true);
        }
    }

    private void onCurrentAnimationEnd(boolean wasSuccess, int logAction) {
        PendingAnimation pendingAnimation = this.mPendingAnimation;
        if (pendingAnimation != null) {
            pendingAnimation.finish(wasSuccess, logAction);
            this.mPendingAnimation = null;
        }
        clearState();
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$onDragEnd$0$TaskViewTouchController(ZIZ)V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: onCurrentAnimationEnd, reason: merged with bridge method [inline-methods] */
    public void lambda$onDragEnd$0$TaskViewTouchController(boolean wasSuccess, int logAction, boolean doPinAnimation) {
        PendingAnimation pendingAnimation = this.mPendingAnimation;
        if (pendingAnimation != null) {
            pendingAnimation.finish(wasSuccess, logAction);
            this.mPendingAnimation = null;
        }
        if (doPinAnimation) {
            this.mTaskBeingDragged.doPinAnimation();
        }
        clearState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearState() {
        this.mDetector.finishedScrolling();
        this.mDetector.setDetectableScrollConditions(0, false);
        this.mTaskBeingDragged = null;
        this.mCurrentAnimation = null;
        PendingAnimation pendingAnimation = this.mPendingAnimation;
        if (pendingAnimation != null) {
            pendingAnimation.finish(false, 3);
            this.mPendingAnimation = null;
        }
    }
}
