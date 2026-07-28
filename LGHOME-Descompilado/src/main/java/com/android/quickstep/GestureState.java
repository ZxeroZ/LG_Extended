package com.android.quickstep;

import android.app.ActivityManager;
import android.content.Intent;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.launcher3.util.LGLog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class GestureState implements RecentsAnimationCallbacks.RecentsAnimationListener {
    private static final String TAG = "GestureState";
    private final BaseActivityInterface mActivityInterface;
    private int mDisplayId;
    private GestureEndTarget mEndTarget;
    private final int mGestureId;
    private final Intent mHomeIntent;
    private RemoteAnimationTargetCompat mLastAppearedTaskTarget;
    private int mLastStartedTaskId;
    private boolean mNeedToHome;
    private final Intent mOverviewIntent;
    private Set<Integer> mPreviouslyAppearedTaskIds;
    private HashMap<Integer, ThumbnailData> mRecentsAnimationCanceledSnapshots;
    private RecentsAnimationController mRecentsAnimationController;
    private ActivityManager.RunningTaskInfo mRunningTask;
    private final MultiStateCallback mStateCallback;
    private long mSwipeUpStartTimeMs;
    private static final ArrayList<String> STATE_NAMES = new ArrayList<>();
    public static final GestureState DEFAULT_STATE = new GestureState();
    private static int FLAG_COUNT = 0;
    public static final int STATE_END_TARGET_SET = getFlagForIndex("STATE_END_TARGET_SET");
    public static final int STATE_END_TARGET_ANIMATION_FINISHED = getFlagForIndex("STATE_END_TARGET_ANIMATION_FINISHED");
    public static final int STATE_RECENTS_ANIMATION_INITIALIZED = getFlagForIndex("STATE_RECENTS_ANIMATION_INITIALIZED");
    public static final int STATE_RECENTS_ANIMATION_STARTED = getFlagForIndex("STATE_RECENTS_ANIMATION_STARTED");
    public static final int STATE_RECENTS_ANIMATION_CANCELED = getFlagForIndex("STATE_RECENTS_ANIMATION_CANCELED");
    public static final int STATE_RECENTS_ANIMATION_FINISHED = getFlagForIndex("STATE_RECENTS_ANIMATION_FINISHED");
    public static final int STATE_RECENTS_ANIMATION_ENDED = getFlagForIndex("STATE_RECENTS_ANIMATION_ENDED");
    public static final int STATE_OVERSCROLL_WINDOW_CREATED = getFlagForIndex("STATE_OVERSCROLL_WINDOW_CREATED");
    public static final int STATE_RECENTS_SCROLLING_FINISHED = getFlagForIndex("STATE_RECENTS_SCROLLING_FINISHED");
    private static final String[] NEED_TO_HOME_ACTIVITY = {"com.android.gallery3d.app.GalleryDualScreenViewActivity"};

    public enum GestureEndTarget {
        HOME(true, 1, false),
        RECENTS(true, 12, true),
        NEW_TASK(false, 13, true),
        LAST_TASK(false, 13, true);

        public final int containerType;
        public final boolean isLauncher;
        public final boolean recentsAttachedToAppWindow;

        GestureEndTarget(boolean isLauncher, int containerType, boolean recentsAttachedToAppWindow) {
            this.isLauncher = isLauncher;
            this.containerType = containerType;
            this.recentsAttachedToAppWindow = recentsAttachedToAppWindow;
        }
    }

    private static int getFlagForIndex(String name) {
        int i = FLAG_COUNT;
        int i2 = 1 << i;
        FLAG_COUNT = i + 1;
        return i2;
    }

    public GestureState(OverviewComponentObserver componentObserver, int gestureId) {
        this(componentObserver, gestureId, 0);
    }

    public GestureState(OverviewComponentObserver componentObserver, int gestureId, int displayId) {
        this.mPreviouslyAppearedTaskIds = new HashSet();
        this.mLastStartedTaskId = -1;
        this.mDisplayId = 0;
        this.mNeedToHome = false;
        this.mHomeIntent = componentObserver.getHomeIntent(displayId);
        this.mOverviewIntent = componentObserver.getOverviewIntent(displayId);
        BaseActivityInterface activityInterface = componentObserver.getActivityInterface(displayId);
        this.mActivityInterface = activityInterface;
        activityInterface.setDisplayId(displayId);
        this.mStateCallback = new MultiStateCallback((String[]) STATE_NAMES.toArray(new String[0]));
        this.mGestureId = gestureId;
        this.mDisplayId = displayId;
    }

    public GestureState(GestureState other) {
        this.mPreviouslyAppearedTaskIds = new HashSet();
        this.mLastStartedTaskId = -1;
        this.mDisplayId = 0;
        this.mNeedToHome = false;
        this.mHomeIntent = other.mHomeIntent;
        this.mOverviewIntent = other.mOverviewIntent;
        this.mActivityInterface = other.mActivityInterface;
        this.mStateCallback = other.mStateCallback;
        this.mGestureId = other.mGestureId;
        this.mRunningTask = other.mRunningTask;
        this.mEndTarget = other.mEndTarget;
        this.mLastAppearedTaskTarget = other.mLastAppearedTaskTarget;
        this.mPreviouslyAppearedTaskIds = other.mPreviouslyAppearedTaskIds;
        this.mLastStartedTaskId = other.mLastStartedTaskId;
        this.mDisplayId = other.mDisplayId;
    }

    public GestureState() {
        this.mPreviouslyAppearedTaskIds = new HashSet();
        this.mLastStartedTaskId = -1;
        this.mDisplayId = 0;
        this.mNeedToHome = false;
        this.mHomeIntent = new Intent();
        this.mOverviewIntent = new Intent();
        this.mActivityInterface = null;
        this.mStateCallback = new MultiStateCallback((String[]) STATE_NAMES.toArray(new String[0]));
        this.mGestureId = -1;
    }

    public boolean hasState(int stateMask) {
        return this.mStateCallback.hasStates(stateMask);
    }

    public void setState(int stateFlag) {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(stateFlag);
    }

    public void runOnceAtState(int stateMask, Runnable callback) {
        this.mStateCallback.runOnceAtState(stateMask, callback);
    }

    public Intent getHomeIntent() {
        return this.mHomeIntent;
    }

    public Intent getOverviewIntent() {
        return this.mOverviewIntent;
    }

    public <T extends StatefulActivity<?>> BaseActivityInterface<?, T> getActivityInterface() {
        return this.mActivityInterface;
    }

    public int getGestureId() {
        return this.mGestureId;
    }

    public ActivityManager.RunningTaskInfo getRunningTask() {
        return this.mRunningTask;
    }

    public int getRunningTaskId() {
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mRunningTask;
        if (runningTaskInfo != null) {
            return runningTaskInfo.taskId;
        }
        return -1;
    }

    public void updateRunningTask(ActivityManager.RunningTaskInfo runningTask) {
        this.mRunningTask = runningTask;
        setNeedToHome();
    }

    public void updateLastAppearedTaskTarget(RemoteAnimationTargetCompat lastAppearedTaskTarget) {
        this.mLastAppearedTaskTarget = lastAppearedTaskTarget;
        if (lastAppearedTaskTarget != null) {
            this.mPreviouslyAppearedTaskIds.add(Integer.valueOf(lastAppearedTaskTarget.taskId));
        }
    }

    public int getLastAppearedTaskId() {
        RemoteAnimationTargetCompat remoteAnimationTargetCompat = this.mLastAppearedTaskTarget;
        if (remoteAnimationTargetCompat != null) {
            return remoteAnimationTargetCompat.taskId;
        }
        return -1;
    }

    public void updatePreviouslyAppearedTaskIds(Set<Integer> previouslyAppearedTaskIds) {
        this.mPreviouslyAppearedTaskIds = previouslyAppearedTaskIds;
    }

    public Set<Integer> getPreviouslyAppearedTaskIds() {
        return this.mPreviouslyAppearedTaskIds;
    }

    public void updateLastStartedTaskId(int lastStartedTaskId) {
        this.mLastStartedTaskId = lastStartedTaskId;
    }

    public int getLastStartedTaskId() {
        return this.mLastStartedTaskId;
    }

    public GestureEndTarget getEndTarget() {
        return this.mEndTarget;
    }

    public void setEndTarget(GestureEndTarget target) {
        setEndTarget(target, true);
    }

    public void setEndTarget(GestureEndTarget target, boolean isAtomic) {
        this.mEndTarget = target;
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_END_TARGET_SET);
        ActiveGestureLog.INSTANCE.addLog("setEndTarget " + this.mEndTarget);
        if (isAtomic) {
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_END_TARGET_ANIMATION_FINISHED);
        }
    }

    public boolean isRunningAnimationToLauncher() {
        GestureEndTarget gestureEndTarget;
        return isRecentsAnimationRunning() && (gestureEndTarget = this.mEndTarget) != null && gestureEndTarget.isLauncher;
    }

    public boolean isRecentsAnimationRunning() {
        return this.mStateCallback.hasStates(STATE_RECENTS_ANIMATION_INITIALIZED) && !this.mStateCallback.hasStates(STATE_RECENTS_ANIMATION_ENDED);
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationStart(RecentsAnimationController controller, RecentsAnimationTargets targets) {
        this.mRecentsAnimationController = controller;
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RECENTS_ANIMATION_STARTED);
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> thumbnailDatas) {
        this.mRecentsAnimationCanceledSnapshots = thumbnailDatas;
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RECENTS_ANIMATION_CANCELED);
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RECENTS_ANIMATION_ENDED);
        if (this.mRecentsAnimationCanceledSnapshots != null) {
            RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
            if (recentsAnimationController != null) {
                recentsAnimationController.cleanupScreenshot();
            }
            this.mRecentsAnimationCanceledSnapshots = null;
        }
    }

    @Override // com.android.quickstep.RecentsAnimationCallbacks.RecentsAnimationListener
    public void onRecentsAnimationFinished(RecentsAnimationController controller) {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RECENTS_ANIMATION_FINISHED);
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RECENTS_ANIMATION_ENDED);
    }

    void setSwipeUpStartTimeMs(long uptimeMs) {
        this.mSwipeUpStartTimeMs = uptimeMs;
    }

    long getSwipeUpStartTimeMs() {
        return this.mSwipeUpStartTimeMs;
    }

    public void dump(PrintWriter pw) {
        pw.println("GestureState:");
        pw.println("  gestureID=" + this.mGestureId);
        pw.println("  runningTask=" + this.mRunningTask);
        pw.println("  endTarget=" + this.mEndTarget);
        pw.println("  lastAppearedTaskTargetId=" + getLastAppearedTaskId());
        pw.println("  lastStartedTaskId=" + this.mLastStartedTaskId);
        pw.println("  isRecentsAnimationRunning=" + isRecentsAnimationRunning());
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    private void setNeedToHome() {
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mRunningTask;
        if (runningTaskInfo == null || runningTaskInfo.topActivity == null) {
            this.mNeedToHome = false;
            return;
        }
        if ((this.mRunningTask.baseIntent.getFlags() & 8388608) != 8388608) {
            this.mNeedToHome = false;
            return;
        }
        String className = this.mRunningTask.topActivity.getClassName();
        for (String str : NEED_TO_HOME_ACTIVITY) {
            if (str.equals(className)) {
                LGLog.i(TAG, "exceptional case");
                this.mNeedToHome = true;
                return;
            }
        }
    }

    public boolean getNeedToHome() {
        return this.mNeedToHome;
    }
}
