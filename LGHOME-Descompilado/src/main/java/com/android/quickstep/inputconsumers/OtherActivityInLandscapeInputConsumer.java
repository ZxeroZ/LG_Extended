package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.launcher3.BaseDraggingActivity;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.OverviewCommandHelper;
import com.android.quickstep.OverviewComponentObserver;
import com.android.quickstep.util.MotionPauseDetector;
import com.lge.launcher3.R;
import com.lge.launcher3.quickstep.ActivityManagerWrapperEx;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class OtherActivityInLandscapeInputConsumer extends ContextWrapper implements InputConsumer {
    private static final boolean DEBUG = false;
    private static final String TAG = "OtherActivityInLandscapeInputConsumer";
    private int mActivePointerId;
    private BaseDraggingActivity mActivity;
    private final int mAngleThreshold;
    private final Context mContext;
    private int mDisplayId;
    private float mDistance;
    private final PointF mDownPos;
    private final float mDragDistThreshold;
    private long mDragTime;
    private final float mFlingDistThreshold;
    private final GestureDetector mGestureDetector;
    boolean mIsPaused;
    private final PointF mLastPos;
    private final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMinDisplacement;
    OverviewCommandHelper mOverviewCommandHelper;
    protected final OverviewComponentObserver mOverviewComponentObserver;
    private boolean mPassedSlop;
    private final float mSquaredSlop;
    private final PointF mStartDragPos;

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return 8;
    }

    public OtherActivityInLandscapeInputConsumer(Context context, GestureState gestureState, int displayId, OverviewComponentObserver overviewComponentObserver, OverviewCommandHelper overviewCommandHelper) {
        super(context);
        this.mDownPos = new PointF();
        this.mLastPos = new PointF();
        this.mStartDragPos = new PointF();
        this.mActivePointerId = -1;
        Resources resources = context.getResources();
        this.mContext = context;
        this.mActivity = gestureState.getActivityInterface().getCreatedActivity();
        float scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mDisplayId = displayId;
        this.mSquaredSlop = scaledTouchSlop * scaledTouchSlop;
        this.mGestureDetector = new GestureDetector(context, new SimpleTransitionGestureListener());
        this.mMotionPauseDetector = new MotionPauseDetector(context);
        this.mOverviewComponentObserver = overviewComponentObserver;
        this.mOverviewCommandHelper = overviewCommandHelper;
        this.mDragDistThreshold = resources.getDimension(R.dimen.gestures_assistant_drag_threshold);
        this.mFlingDistThreshold = resources.getDimension(R.dimen.gestures_assistant_fling_threshold);
        this.mAngleThreshold = resources.getInteger(R.integer.simple_transition_landscape_gesture_corner_deg_threshold);
        this.mMotionPauseMinDisplacement = context.getResources().getDimension(R.dimen.motion_pause_detector_min_displacement_from_app);
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0131  */
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
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onMotionEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            int r0 = r8.getActionMasked()
            r1 = 0
            if (r0 == 0) goto L139
            r2 = 1
            if (r0 == r2) goto L131
            r3 = 2
            if (r0 == r3) goto L64
            r3 = 3
            if (r0 == r3) goto L131
            r3 = 5
            if (r0 == r3) goto L5d
            r3 = 6
            if (r0 == r3) goto L18
            goto L153
        L18:
            int r0 = r8.getActionIndex()
            int r3 = r8.getPointerId(r0)
            int r4 = r7.mActivePointerId
            if (r3 != r4) goto L153
            if (r0 != 0) goto L27
            r1 = r2
        L27:
            android.graphics.PointF r0 = r7.mDownPos
            float r2 = r8.getX(r1)
            android.graphics.PointF r3 = r7.mLastPos
            float r3 = r3.x
            android.graphics.PointF r4 = r7.mDownPos
            float r4 = r4.x
            float r3 = r3 - r4
            float r2 = r2 - r3
            float r3 = r8.getY(r1)
            android.graphics.PointF r4 = r7.mLastPos
            float r4 = r4.y
            android.graphics.PointF r5 = r7.mDownPos
            float r5 = r5.y
            float r4 = r4 - r5
            float r3 = r3 - r4
            r0.set(r2, r3)
            android.graphics.PointF r0 = r7.mLastPos
            float r2 = r8.getX(r1)
            float r3 = r8.getY(r1)
            r0.set(r2, r3)
            int r0 = r8.getPointerId(r1)
            r7.mActivePointerId = r0
            goto L153
        L5d:
            com.android.quickstep.util.MotionPauseDetector r0 = r7.mMotionPauseDetector
            r0.clear()
            goto L153
        L64:
            int r0 = r7.mActivePointerId
            int r0 = r8.findPointerIndex(r0)
            r3 = -1
            if (r0 != r3) goto L6f
            goto L153
        L6f:
            android.graphics.PointF r3 = r7.mLastPos
            float r4 = r8.getX(r0)
            float r0 = r8.getY(r0)
            r3.set(r4, r0)
            boolean r0 = r7.mPassedSlop
            if (r0 != 0) goto Ldb
            android.graphics.PointF r0 = r7.mLastPos
            float r0 = r0.x
            android.graphics.PointF r3 = r7.mDownPos
            float r3 = r3.x
            float r0 = r0 - r3
            android.graphics.PointF r3 = r7.mLastPos
            float r3 = r3.y
            android.graphics.PointF r4 = r7.mDownPos
            float r4 = r4.y
            float r3 = r3 - r4
            float r0 = com.android.launcher3.Utilities.squaredHypot(r0, r3)
            float r3 = r7.mSquaredSlop
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L9d
            r1 = r2
        L9d:
            if (r1 == 0) goto L153
            r7.mPassedSlop = r2
            android.graphics.PointF r0 = r7.mStartDragPos
            android.graphics.PointF r1 = r7.mLastPos
            float r1 = r1.x
            android.graphics.PointF r2 = r7.mLastPos
            float r2 = r2.y
            r0.set(r1, r2)
            android.graphics.PointF r0 = r7.mLastPos
            float r0 = r0.x
            android.graphics.PointF r1 = r7.mDownPos
            float r1 = r1.x
            float r0 = r0 - r1
            double r0 = (double) r0
            android.graphics.PointF r2 = r7.mLastPos
            float r2 = r2.y
            android.graphics.PointF r3 = r7.mDownPos
            float r3 = r3.y
            float r2 = r2 - r3
            double r2 = (double) r2
            double r0 = java.lang.Math.hypot(r0, r2)
            float r0 = (float) r0
            r7.mDistance = r0
            com.android.quickstep.util.MotionPauseDetector r0 = r7.mMotionPauseDetector
            com.android.quickstep.inputconsumers.-$$Lambda$f6MvWlUk3Dkfgo9qervLDmVaLkg r1 = new com.android.quickstep.inputconsumers.-$$Lambda$f6MvWlUk3Dkfgo9qervLDmVaLkg
            r1.<init>()
            r0.setOnMotionPauseListener(r1)
            long r0 = android.os.SystemClock.uptimeMillis()
            r7.mDragTime = r0
            goto L153
        Ldb:
            android.graphics.PointF r0 = r7.mLastPos
            float r0 = r0.x
            android.graphics.PointF r3 = r7.mDownPos
            float r3 = r3.x
            float r0 = r0 - r3
            double r3 = (double) r0
            android.graphics.PointF r0 = r7.mLastPos
            float r0 = r0.y
            android.graphics.PointF r5 = r7.mDownPos
            float r5 = r5.y
            float r0 = r0 - r5
            double r5 = (double) r0
            double r3 = java.lang.Math.hypot(r3, r5)
            float r0 = (float) r3
            r7.mDistance = r0
            boolean r0 = r7.mIsPaused
            if (r0 != 0) goto L153
            android.graphics.PointF r0 = r7.mLastPos
            float r0 = r0.x
            android.graphics.PointF r3 = r7.mStartDragPos
            float r3 = r3.x
            float r0 = r0 - r3
            float r0 = java.lang.Math.abs(r0)
            android.graphics.PointF r3 = r7.mLastPos
            float r3 = r3.y
            android.graphics.PointF r4 = r7.mStartDragPos
            float r4 = r4.y
            float r3 = r3 - r4
            float r3 = java.lang.Math.abs(r3)
            boolean r0 = r7.isValidAssistantGestureAngle(r0, r3)
            if (r0 == 0) goto L153
            float r0 = r7.getDisplacement(r8)
            float r0 = -r0
            float r3 = r7.mMotionPauseMinDisplacement
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 >= 0) goto L126
            r1 = r2
        L126:
            com.android.quickstep.util.MotionPauseDetector r0 = r7.mMotionPauseDetector
            r0.setDisallowPause(r1)
            com.android.quickstep.util.MotionPauseDetector r0 = r7.mMotionPauseDetector
            r0.addPosition(r8)
            goto L153
        L131:
            com.android.quickstep.util.MotionPauseDetector r0 = r7.mMotionPauseDetector
            r0.clear()
            r7.mPassedSlop = r1
            goto L153
        L139:
            int r0 = r8.getPointerId(r1)
            r7.mActivePointerId = r0
            android.graphics.PointF r0 = r7.mDownPos
            float r1 = r8.getX()
            float r2 = r8.getY()
            r0.set(r1, r2)
            android.graphics.PointF r0 = r7.mLastPos
            android.graphics.PointF r1 = r7.mDownPos
            r0.set(r1)
        L153:
            android.view.GestureDetector r0 = r7.mGestureDetector
            r0.onTouchEvent(r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.inputconsumers.OtherActivityInLandscapeInputConsumer.onMotionEvent(android.view.MotionEvent):void");
    }

    private float getDisplacement(MotionEvent ev) {
        return ev.getY() - this.mDownPos.y;
    }

    private class SimpleTransitionGestureListener extends GestureDetector.SimpleOnGestureListener {
        private SimpleTransitionGestureListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LGLog.i(OtherActivityInLandscapeInputConsumer.TAG, "onFling : mPassedSlop = " + OtherActivityInLandscapeInputConsumer.this.mPassedSlop + ", velocityX = " + velocityX + ", velocityY = " + velocityY + ", mDistance = " + OtherActivityInLandscapeInputConsumer.this.mDistance + ", mFlingDistThreshold = " + OtherActivityInLandscapeInputConsumer.this.mFlingDistThreshold + ", mStartDragPos = (" + OtherActivityInLandscapeInputConsumer.this.mStartDragPos.x + ", " + OtherActivityInLandscapeInputConsumer.this.mStartDragPos.y + "), mIsPaused = " + OtherActivityInLandscapeInputConsumer.this.mIsPaused + ", mDisplayId = " + OtherActivityInLandscapeInputConsumer.this.mDisplayId + ", mContext = " + OtherActivityInLandscapeInputConsumer.this.mContext);
            if (!OtherActivityInLandscapeInputConsumer.this.isValidAssistantGestureAngle(velocityX, -velocityY) || OtherActivityInLandscapeInputConsumer.this.mDistance < OtherActivityInLandscapeInputConsumer.this.mFlingDistThreshold || OtherActivityInLandscapeInputConsumer.this.mIsPaused || OtherActivityInLandscapeInputConsumer.this.mContext == null) {
                return true;
            }
            if (OtherActivityInLandscapeInputConsumer.this.mDisplayId == 0) {
                LGLog.d(OtherActivityInLandscapeInputConsumer.TAG, "onFling : startActivity : " + OtherActivityInLandscapeInputConsumer.this.mOverviewComponentObserver.getHomeIntent());
                OtherActivityInLandscapeInputConsumer.this.mContext.startActivity(OtherActivityInLandscapeInputConsumer.this.mOverviewComponentObserver.getHomeIntent());
                return true;
            }
            LGLog.d(OtherActivityInLandscapeInputConsumer.TAG, "onFling : startCoverDisplayHome");
            ActivityManagerWrapperEx.getInstance().startMultiDisplayHomeAsDisplayId(OtherActivityInLandscapeInputConsumer.this.mDisplayId);
            return true;
        }
    }

    public void onMotionPauseChanged(boolean isPaused) {
        OverviewCommandHelper overviewCommandHelper;
        if (this.mIsPaused || !isPaused) {
            return;
        }
        this.mIsPaused = isPaused;
        if (!isPaused || (overviewCommandHelper = this.mOverviewCommandHelper) == null) {
            return;
        }
        overviewCommandHelper.onOverviewToggle(this.mDisplayId);
        BaseDraggingActivity baseDraggingActivity = this.mActivity;
        if (baseDraggingActivity != null) {
            baseDraggingActivity.getRootView().performHapticFeedback(1, 1);
        }
        this.mMotionPauseDetector.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isValidAssistantGestureAngle(float deltaX, float deltaY) {
        float degrees = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
        float f = degrees > 90.0f ? 180.0f - degrees : degrees;
        int i = this.mAngleThreshold;
        boolean z = f > ((float) i) && f < 90.0f;
        LGLog.d(TAG, "isValidAssistantGestureAngle : result " + z + ", deltaX = " + deltaX + ", deltaY = " + deltaY + ", angle = " + f + "(" + degrees + "), mAngleThreshold = " + i);
        return z;
    }
}
