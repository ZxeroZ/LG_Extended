package com.lge.launcher3.allapps;

import android.animation.AnimatorSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.allapps.VerticalPullDetector;
import com.android.launcher3.util.TouchController;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsTransitionController implements TouchController, VerticalPullDetector.Listener {
    private static final float DEFAULT_SHIFT_RANGE = 10.0f;
    private static final float RECATCH_REJECTION_FRACTION = 0.0875f;
    private AllAppsHost mAllAppsView;
    private float mContainerVelocity;
    private AnimatorSet mCurrentAnimation;
    private final VerticalPullDetector mDetector;
    private final Launcher mLauncher;
    private boolean mNoIntercept;
    private float mProgress;
    private float mShiftRange;
    private float mShiftStart;
    private float mStatusBarHeight;
    private Workspace mWorkspace;
    private final Interpolator mAccelInterpolator = new AccelerateInterpolator(2.0f);
    private final Interpolator mDecelInterpolator = new DecelerateInterpolator(3.0f);
    private float mLastDispacement = 0.0f;

    public AllAppsTransitionController(Launcher l) {
        this.mLauncher = l;
        this.mAllAppsView = l.getAllAppsHost();
        this.mWorkspace = l.getWorkspace();
        VerticalPullDetector verticalPullDetector = new VerticalPullDetector(l);
        this.mDetector = verticalPullDetector;
        verticalPullDetector.setListener(this);
        this.mShiftRange = DEFAULT_SHIFT_RANGE;
        this.mProgress = 1.0f;
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerTouchEvent(MotionEvent ev) {
        return this.mDetector.onTouchEvent(ev);
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & 255;
        if (action == 0) {
            this.mNoIntercept = false;
            if ((!this.mLauncher.isAllAppsVisible() && this.mLauncher.getWorkspace().workspaceInModalState()) || AbstractFloatingView.getTopOpenView(this.mLauncher) != null || this.mLauncher.getWorkspace().getCheckAppDrawerAnimationFinished().booleanValue() || this.mLauncher.getWorkspace().getCheckExitAnimationFinished().booleanValue()) {
                this.mNoIntercept = true;
            } else {
                int i = this.mLauncher.isAllAppsVisible() ? 2 : 3;
                Log.d("AllAppsController", "onControllerInterceptTouchEvent directionsToDetectScroll = " + i);
                this.mDetector.setDetectableScrollConditions(i, false);
            }
        } else if (action == 5) {
            Log.d("AllAppsController", "onControllerInterceptTouchEvent multi touch");
            this.mNoIntercept = true;
        }
        if (this.mNoIntercept) {
            Log.d("AllAppsController", "onControllerInterceptTouchEvent mNoIntercept true");
            return false;
        }
        this.mDetector.onTouchEvent(ev);
        if (this.mDetector.isSettlingState() && (isInDisallowRecatchBottomZone() || isInDisallowRecatchTopZone())) {
            return false;
        }
        return this.mDetector.isDraggingOrSettling();
    }

    private boolean isInDisallowRecatchTopZone() {
        return this.mProgress < RECATCH_REJECTION_FRACTION;
    }

    private boolean isInDisallowRecatchBottomZone() {
        return this.mProgress > 0.9125f;
    }

    @Override // com.android.launcher3.allapps.VerticalPullDetector.Listener
    public void onDragStart(boolean start) {
        Log.d("AllAppsController", "onDragStart " + start);
        Utilities.cancelProcPreLaunch(this.mLauncher);
    }

    @Override // com.android.launcher3.allapps.VerticalPullDetector.Listener
    public boolean onDrag(float displacementX, float displacementY, float velocity) {
        this.mLastDispacement = displacementY;
        if (this.mLauncher.getWorkspace().getInAppsEnabled() && this.mDetector.getScrollConditions() == 3 && this.mLauncher.getWorkspace().checkCognizingInAppsCondition(false, true, displacementX, displacementY)) {
            this.mDetector.setDetectableScrollConditions(0, false);
            return false;
        }
        if (!LGHomeFeature.isSwipeUpAppDrawerEnable() || this.mDetector.getScrollConditions() == 0 || !this.mLauncher.getWorkspace().checkCognizingSwipeUpAppDrawerCondition(false, true, 0.0f, displacementY)) {
            return true;
        }
        this.mDetector.setDetectableScrollConditions(0, false);
        return false;
    }

    @Override // com.android.launcher3.allapps.VerticalPullDetector.Listener
    public void onDragEnd(float velocity, boolean fling) {
        this.mDetector.finishedScrolling();
        Workspace workspace = this.mLauncher.getWorkspace();
        if (workspace == null) {
            Log.d("AllAppsController", "onDragEnd(): workspace is null");
            return;
        }
        Log.d("AllAppsController", "onDragEnd ScrollCondition = " + this.mDetector.getScrollConditions() + "/ TouchState() = " + workspace.getTouchState());
        if (this.mLauncher.isInState(LauncherState.NORMAL)) {
            if (workspace.getInAppsEnabled() && !workspace.getCheckInappsValue() && this.mDetector.getScrollConditions() == 3 && (workspace.getTouchState() == 5 || workspace.getTouchState() == 0)) {
                this.mLauncher.getWorkspace().exitInApps();
                return;
            }
            if (!LGHomeFeature.isSwipeUpAppDrawerEnable() || workspace.getCheckSwipeUpAppDrawer().booleanValue() || workspace.getCheckInappsValue() || this.mDetector.getScrollConditions() == 0) {
                return;
            }
            if (workspace.getTouchState() == 6 || workspace.getTouchState() == 0) {
                this.mLauncher.getWorkspace().exitSwipeUpAppDrawer();
            }
        }
    }

    public void preparePull(boolean start) {
        if (start) {
            this.mStatusBarHeight = this.mLauncher.getDragLayer().getInsets().top;
            if (this.mLauncher.isAllAppsVisible()) {
                return;
            }
            this.mLauncher.getAllAppsHost().setVisibility(0);
        }
    }

    public void setProgress(float progress) {
        float f = this.mProgress;
        float f2 = this.mShiftRange;
        float f3 = f * f2;
        this.mProgress = progress;
        float f4 = f2 * progress;
        float fBoundToRange = com.android.launcher3.Utilities.boundToRange(progress, 0.0f, 1.0f);
        float f5 = 1.0f - fBoundToRange;
        this.mAccelInterpolator.getInterpolation(fBoundToRange);
        this.mLauncher.getAllAppsHost().setAlpha(f5);
        this.mLauncher.getAllAppsHost().getContentView().setAlpha(f5);
        this.mLauncher.getAllAppsHost().setTranslationY(f4);
        if (this.mDetector.isDraggingState()) {
            return;
        }
        this.mContainerVelocity = this.mDetector.computeVelocity(f4 - f3, System.currentTimeMillis());
    }

    private void cancelAnimation() {
        AnimatorSet animatorSet = this.mCurrentAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.mCurrentAnimation = null;
        }
    }

    public void resetDetector() {
        this.mDetector.finishedScrolling();
    }
}
