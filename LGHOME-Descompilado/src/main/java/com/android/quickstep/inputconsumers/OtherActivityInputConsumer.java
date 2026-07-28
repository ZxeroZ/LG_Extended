package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.launcher3.Utilities;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.TraceHelper;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.BaseSwipeUpHandler;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskAnimationManager;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.CachedEventDispatcher;
import com.android.quickstep.util.MotionPauseDetector;
import com.android.quickstep.util.NavBarPosition;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import java.util.Objects;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class OtherActivityInputConsumer extends ContextWrapper implements InputConsumer {
    public static final String DOWN_EVT = "OtherActivityInputConsumer.DOWN";
    public static final float QUICKSTEP_TOUCH_SLOP_RATIO_GESTURAL = 2.0f;
    public static final float QUICKSTEP_TOUCH_SLOP_RATIO_TWO_BUTTON = 9.0f;
    private static final String TAG = "OtherActivityInputConsumer";
    private static final String UP_EVT = "OtherActivityInputConsumer.UP";
    private RecentsAnimationCallbacks mActiveCallbacks;
    private int mActivePointerId;
    private final BaseActivityInterface mActivityInterface;
    private Runnable mCancelRecentsAnimationRunnable;
    private final RecentsAnimationDeviceState mDeviceState;
    private final boolean mDisableHorizontalSwipe;
    private final PointF mDownPos;
    private final GestureState mGestureState;
    private final BaseSwipeUpHandler.Factory mHandlerFactory;
    private final InputMonitorCompat mInputMonitorCompat;
    private BaseSwipeUpHandler mInteractionHandler;
    private final boolean mIsDeferredDownTarget;
    private final PointF mLastPos;
    private Handler mMainThreadHandler;
    private final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMinDisplacement;
    private final NavBarPosition mNavBarPosition;
    private final Consumer<OtherActivityInputConsumer> mOnCompleteCallback;
    private boolean mPassedPilferInputSlop;
    private boolean mPassedSlopOnThisGesture;
    private boolean mPassedWindowMoveSlop;
    private final CachedEventDispatcher mRecentsViewDispatcher;
    private final float mSquaredTouchSlop;
    private float mStartDisplacement;
    private final TaskAnimationManager mTaskAnimationManager;
    private final float mTouchSlop;
    private VelocityTracker mVelocityTracker;

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return 4;
    }

    @Override // com.android.quickstep.InputConsumer
    public boolean isConsumerDetachedFromGesture() {
        return true;
    }

    static /* synthetic */ void lambda$new$0() {
        LGLog.i(TAG + "[RecentsAnimation]", "cancelRecentsAnimation");
        ActivityManagerWrapper.getInstance().cancelRecentsAnimation(true);
    }

    public OtherActivityInputConsumer(Context base, RecentsAnimationDeviceState deviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, boolean isDeferredDownTarget, Consumer<OtherActivityInputConsumer> onCompleteCallback, InputMonitorCompat inputMonitorCompat, boolean disableHorizontalSwipe, BaseSwipeUpHandler.Factory handlerFactory) {
        super(base);
        this.mRecentsViewDispatcher = new CachedEventDispatcher();
        this.mDownPos = new PointF();
        this.mLastPos = new PointF();
        this.mActivePointerId = -1;
        this.mCancelRecentsAnimationRunnable = new Runnable() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$OtherActivityInputConsumer$EjYF-k6GXX3Ti0a0CqE4Q7WLJNI
            @Override // java.lang.Runnable
            public final void run() {
                OtherActivityInputConsumer.lambda$new$0();
            }
        };
        this.mDeviceState = deviceState;
        NavBarPosition navBarPosition = deviceState.getNavBarPosition(gestureState.getDisplayId());
        this.mNavBarPosition = navBarPosition;
        this.mTaskAnimationManager = taskAnimationManager;
        this.mGestureState = gestureState;
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mHandlerFactory = handlerFactory;
        this.mActivityInterface = gestureState.getActivityInterface();
        this.mMotionPauseDetector = new MotionPauseDetector(base, false, (navBarPosition.isLeftEdge() || navBarPosition.isRightEdge()) ? 0 : 1);
        this.mMotionPauseMinDisplacement = base.getResources().getDimension(R.dimen.motion_pause_detector_min_displacement_from_app);
        this.mOnCompleteCallback = onCompleteCallback;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mInputMonitorCompat = inputMonitorCompat;
        boolean zIsRecentsAnimationRunning = taskAnimationManager.isRecentsAnimationRunning();
        this.mIsDeferredDownTarget = !zIsRecentsAnimationRunning && isDeferredDownTarget;
        float f = deviceState.isFullyGesturalNavMode() ? 2.0f : 9.0f;
        float scaledTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        this.mTouchSlop = scaledTouchSlop;
        this.mSquaredTouchSlop = f * scaledTouchSlop * scaledTouchSlop;
        this.mPassedWindowMoveSlop = zIsRecentsAnimationRunning;
        this.mPassedPilferInputSlop = zIsRecentsAnimationRunning;
        this.mDisableHorizontalSwipe = !zIsRecentsAnimationRunning && disableHorizontalSwipe;
    }

    private void forceCancelGesture(MotionEvent ev) {
        int action = ev.getAction();
        ev.setAction(3);
        finishTouchTracking(ev);
        ev.setAction(action);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v17, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r0v18, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r0v20, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r7v2, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r7v3, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r7v6, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r8v3, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r8v4, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r8v7, resolved type: byte */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // com.android.quickstep.InputConsumer
    public void onMotionEvent(MotionEvent motionEvent) {
        BaseSwipeUpHandler baseSwipeUpHandler;
        if (this.mVelocityTracker == null) {
            return;
        }
        NavBarPosition navBarPosition = this.mDeviceState.getNavBarPosition(this.mGestureState.getDisplayId());
        boolean zNeedToChangeConsumer = (!this.mPassedWindowMoveSlop || (baseSwipeUpHandler = this.mInteractionHandler) == null) ? false : baseSwipeUpHandler.needToChangeConsumer(navBarPosition.getRotation());
        if (navBarPosition.getRotation() != this.mNavBarPosition.getRotation()) {
            this.mNavBarPosition.refreshRotate(navBarPosition);
        }
        if (this.mPassedWindowMoveSlop && this.mInteractionHandler != null && (!this.mRecentsViewDispatcher.hasConsumer() || zNeedToChangeConsumer)) {
            this.mRecentsViewDispatcher.setConsumer(this.mInteractionHandler.getRecentsViewDispatcher(this.mNavBarPosition.getRotation()));
            int action = motionEvent.getAction();
            motionEvent.setAction(254);
            this.mRecentsViewDispatcher.dispatchEvent(motionEvent);
            motionEvent.setAction(action);
        }
        int edgeFlags = motionEvent.getEdgeFlags();
        motionEvent.setEdgeFlags(edgeFlags | 256);
        this.mRecentsViewDispatcher.dispatchEvent(motionEvent);
        motionEvent.setEdgeFlags(edgeFlags);
        this.mVelocityTracker.addMovement(motionEvent);
        if (motionEvent.getActionMasked() == 6) {
            this.mVelocityTracker.clear();
            this.mMotionPauseDetector.clear();
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            Object objBeginSection = TraceHelper.INSTANCE.beginSection(DOWN_EVT, 4);
            this.mActivePointerId = motionEvent.getPointerId(0);
            this.mDownPos.set(motionEvent.getX(), motionEvent.getY());
            this.mLastPos.set(this.mDownPos);
            if (!this.mIsDeferredDownTarget) {
                startTouchTrackingForWindowAnimation(motionEvent.getEventTime());
            }
            TraceHelper.INSTANCE.endSection(objBeginSection);
            return;
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                int iFindPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                if (iFindPointerIndex == -1) {
                    return;
                }
                this.mLastPos.set(motionEvent.getX(iFindPointerIndex), motionEvent.getY(iFindPointerIndex));
                float displacement = getDisplacement(motionEvent);
                float f = this.mLastPos.x - this.mDownPos.x;
                float f2 = this.mLastPos.y - this.mDownPos.y;
                if (!this.mPassedWindowMoveSlop && !this.mIsDeferredDownTarget) {
                    float fAbs = Math.abs(displacement);
                    float f3 = this.mTouchSlop;
                    if (fAbs > f3) {
                        this.mPassedWindowMoveSlop = true;
                        this.mStartDisplacement = Math.min(displacement, -f3);
                    }
                }
                float fAbs2 = Math.abs(f);
                float f4 = -displacement;
                byte b = Utilities.squaredHypot(f, f2) >= this.mSquaredTouchSlop;
                if (!this.mPassedSlopOnThisGesture && b != false) {
                    this.mPassedSlopOnThisGesture = true;
                }
                boolean z = (!this.mPassedSlopOnThisGesture && this.mPassedPilferInputSlop) == true || fAbs2 > f4;
                if (!this.mPassedPilferInputSlop && b != false) {
                    if (this.mDisableHorizontalSwipe && Math.abs(f) > Math.abs(f2)) {
                        forceCancelGesture(motionEvent);
                        return;
                    }
                    this.mPassedPilferInputSlop = true;
                    if (this.mIsDeferredDownTarget) {
                        startTouchTrackingForWindowAnimation(motionEvent.getEventTime());
                    }
                    if (!this.mPassedWindowMoveSlop) {
                        this.mPassedWindowMoveSlop = true;
                        this.mStartDisplacement = Math.min(displacement, -this.mTouchSlop);
                    }
                    LGLog.d(TAG, "[RecentsAnimation] notifyGestureStarted");
                    SystemUiProxy systemUiProxyLambda$get$0$MainThreadInitializedObject = SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mActivityInterface.getCreatedActivity());
                    if (systemUiProxyLambda$get$0$MainThreadInitializedObject != null) {
                        systemUiProxyLambda$get$0$MainThreadInitializedObject.onNotifyGestureStarted(this.mGestureState.getDisplayId());
                    }
                    notifyGestureStarted(z);
                }
                BaseSwipeUpHandler baseSwipeUpHandler2 = this.mInteractionHandler;
                if (baseSwipeUpHandler2 != null) {
                    if (this.mPassedWindowMoveSlop) {
                        baseSwipeUpHandler2.updateDisplacement(displacement - this.mStartDisplacement);
                    }
                    if (this.mDeviceState.isFullyGesturalNavMode()) {
                        this.mMotionPauseDetector.setDisallowPause(f4 < this.mMotionPauseMinDisplacement || z || !(this.mPassedPilferInputSlop && !z) == true);
                        this.mMotionPauseDetector.addPosition(motionEvent);
                        this.mInteractionHandler.setIsLikelyToStartNewTask(z);
                        return;
                    }
                    return;
                }
                return;
            }
            if (actionMasked != 3) {
                if (actionMasked == 5) {
                    if (this.mPassedPilferInputSlop) {
                        return;
                    }
                    if (this.mDeviceState.isInSwipeUpTouchRegion(motionEvent, motionEvent.getActionIndex())) {
                        return;
                    }
                    forceCancelGesture(motionEvent);
                    return;
                }
                if (actionMasked != 6) {
                    return;
                }
                int actionIndex = motionEvent.getActionIndex();
                if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
                    int i = actionIndex == 0 ? 1 : 0;
                    this.mDownPos.set(motionEvent.getX(i) - (this.mLastPos.x - this.mDownPos.x), motionEvent.getY(i) - (this.mLastPos.y - this.mDownPos.y));
                    this.mLastPos.set(motionEvent.getX(i), motionEvent.getY(i));
                    this.mActivePointerId = motionEvent.getPointerId(i);
                    return;
                }
                return;
            }
        }
        finishTouchTracking(motionEvent);
    }

    private void notifyGestureStarted(boolean isLikelyToStartNewTask) {
        ActiveGestureLog.INSTANCE.addLog("startQuickstep");
        if (this.mInteractionHandler == null) {
            return;
        }
        TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
        this.mInputMonitorCompat.pilferPointers();
        this.mActivityInterface.closeOverlay();
        ActivityManagerWrapper.getInstance().closeSystemWindows(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
        this.mInteractionHandler.onGestureStarted(isLikelyToStartNewTask);
        if (this.mGestureState.getNeedToHome()) {
            this.mInteractionHandler.onGestureCancelled();
            this.mInteractionHandler.finishToHome();
        }
    }

    private void startTouchTrackingForWindowAnimation(long touchTimeMs) {
        ActiveGestureLog.INSTANCE.addLog("startRecentsAnimation");
        BaseSwipeUpHandler baseSwipeUpHandlerNewHandler = this.mHandlerFactory.newHandler(this.mGestureState, touchTimeMs, this.mTaskAnimationManager.isRecentsAnimationRunning());
        this.mInteractionHandler = baseSwipeUpHandlerNewHandler;
        baseSwipeUpHandlerNewHandler.setGestureEndCallback(new Runnable() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$OtherActivityInputConsumer$JaVS4jRZdR6zfxuB3eSHAAnUTLY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.onInteractionGestureFinished();
            }
        });
        MotionPauseDetector motionPauseDetector = this.mMotionPauseDetector;
        final BaseSwipeUpHandler baseSwipeUpHandler = this.mInteractionHandler;
        Objects.requireNonNull(baseSwipeUpHandler);
        motionPauseDetector.setOnMotionPauseListener(new MotionPauseDetector.OnMotionPauseListener() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$aOoMJGIiMU12KhkiYvIApePCcJg
            @Override // com.android.quickstep.util.MotionPauseDetector.OnMotionPauseListener
            public final void onMotionPauseChanged(boolean z) {
                baseSwipeUpHandler.onMotionPauseChanged(z);
            }
        });
        Intent intent = new Intent(this.mInteractionHandler.getLaunchIntent());
        this.mInteractionHandler.initWhenReady(intent);
        if (this.mTaskAnimationManager.isRecentsAnimationRunning()) {
            RecentsAnimationCallbacks recentsAnimationCallbacksContinueRecentsAnimation = this.mTaskAnimationManager.continueRecentsAnimation(this.mGestureState);
            this.mActiveCallbacks = recentsAnimationCallbacksContinueRecentsAnimation;
            recentsAnimationCallbacksContinueRecentsAnimation.addListener(this.mInteractionHandler);
            this.mTaskAnimationManager.notifyRecentsAnimationState(this.mInteractionHandler);
            LGLog.d(TAG, "[RecentsAnimation] startTouchTrackingForWindowAnimation : notifyGestureStarted");
            notifyGestureStarted(true);
            return;
        }
        intent.putExtra(ActiveGestureLog.INTENT_EXTRA_LOG_TRACE_ID, this.mGestureState.getGestureId());
        this.mActiveCallbacks = this.mTaskAnimationManager.startRecentsAnimation(this.mGestureState, intent, this.mInteractionHandler);
    }

    private void finishTouchTracking(MotionEvent ev) {
        float f;
        Object objBeginSection = TraceHelper.INSTANCE.beginSection(UP_EVT, 4);
        LGLog.i(TAG + "[RecentsAnimation]", "[RecentsAnimation] finishTouchTracking: mPassedWindowMoveSlop = " + this.mPassedWindowMoveSlop + ", mInteractionHandler = " + this.mInteractionHandler + ", action = " + ev.getActionMasked());
        if (this.mPassedWindowMoveSlop && this.mInteractionHandler != null) {
            if (ev.getActionMasked() == 3) {
                this.mInteractionHandler.onGestureCancelled();
            } else {
                this.mVelocityTracker.computeCurrentVelocity(1000, ViewConfiguration.get(this).getScaledMaximumFlingVelocity());
                float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                if (this.mNavBarPosition.isRightEdge()) {
                    f = xVelocity;
                } else {
                    f = this.mNavBarPosition.isLeftEdge() ? -xVelocity : yVelocity;
                }
                this.mInteractionHandler.updateDisplacement(getDisplacement(ev) - this.mStartDisplacement);
                this.mInteractionHandler.onGestureEnded(f, new PointF(xVelocity, yVelocity), this.mDownPos);
            }
        } else {
            onConsumerAboutToBeSwitched();
            onInteractionGestureFinished();
            this.mMainThreadHandler.removeCallbacks(this.mCancelRecentsAnimationRunnable);
            this.mMainThreadHandler.postDelayed(this.mCancelRecentsAnimationRunnable, 100L);
        }
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
        this.mMotionPauseDetector.clear();
        TraceHelper.INSTANCE.endSection(objBeginSection);
    }

    @Override // com.android.quickstep.InputConsumer
    public void notifyOrientationSetup() {
        this.mDeviceState.onStartGesture(this.mGestureState.getDisplayId());
    }

    @Override // com.android.quickstep.InputConsumer
    public void onConsumerAboutToBeSwitched() {
        Preconditions.assertUIThread();
        this.mMainThreadHandler.removeCallbacks(this.mCancelRecentsAnimationRunnable);
        if (this.mInteractionHandler != null) {
            removeListener();
            this.mInteractionHandler.onConsumerAboutToBeSwitched();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInteractionGestureFinished() {
        Preconditions.assertUIThread();
        removeListener();
        this.mInteractionHandler = null;
        MotionPauseDetector motionPauseDetector = this.mMotionPauseDetector;
        if (motionPauseDetector != null) {
            motionPauseDetector.clear();
        }
        this.mOnCompleteCallback.accept(this);
    }

    private void removeListener() {
        RecentsAnimationCallbacks recentsAnimationCallbacks = this.mActiveCallbacks;
        if (recentsAnimationCallbacks != null) {
            recentsAnimationCallbacks.removeListener(this.mInteractionHandler);
        }
    }

    private float getDisplacement(MotionEvent ev) {
        float y;
        float f;
        if (this.mNavBarPosition.isRightEdge()) {
            y = ev.getX();
            f = this.mDownPos.x;
        } else {
            if (this.mNavBarPosition.isLeftEdge()) {
                return this.mDownPos.x - ev.getX();
            }
            y = ev.getY();
            f = this.mDownPos.y;
        }
        return y - f;
    }

    @Override // com.android.quickstep.InputConsumer
    public boolean allowInterceptByParent() {
        return !this.mPassedPilferInputSlop || this.mGestureState.hasState(GestureState.STATE_OVERSCROLL_WINDOW_CREATED);
    }
}
