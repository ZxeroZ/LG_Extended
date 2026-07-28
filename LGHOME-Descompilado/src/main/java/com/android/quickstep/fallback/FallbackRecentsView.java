package com.android.quickstep.fallback;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.popup.QuickstepSystemShortcut;
import com.android.launcher3.statemanager.StateManager;
import com.android.quickstep.FallbackActivityInterface;
import com.android.quickstep.RecentsActivity;
import com.android.quickstep.util.SplitSelectStateController;
import com.android.quickstep.views.ClearAllButton;
import com.android.quickstep.views.OverviewActionsView;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.lge.launcher3.quickstep.ActivityManagerWrapperEx;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.PackageUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class FallbackRecentsView extends RecentsView<RecentsActivity> implements StateManager.StateListener<RecentsState> {
    private ActivityManager.RunningTaskInfo mHomeTaskInfo;

    @Override // com.android.quickstep.views.RecentsView
    public boolean shouldUseMultiWindowTaskSizeStrategy() {
        return false;
    }

    public FallbackRecentsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FallbackRecentsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, FallbackActivityInterface.INSTANCE);
        ((RecentsActivity) this.mActivity).getStateManager().addStateListener(this);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void init(OverviewActionsView actionsView, SplitSelectStateController controller) {
        super.init(actionsView, controller);
        setOverviewStateEnabled(true);
        setOverlayEnabled(true);
    }

    public void init() {
        setOverviewStateEnabled(true);
        setOverlayEnabled(true);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void startHome() {
        ((RecentsActivity) this.mActivity).startHome();
    }

    public void onGestureAnimationStartOnHome(ActivityManager.RunningTaskInfo homeTaskInfo) {
        this.mHomeTaskInfo = homeTaskInfo;
        setAnimRunning(true);
        onGestureAnimationStart(homeTaskInfo == null ? -1 : homeTaskInfo.taskId);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void onGestureAnimationEnd() {
        TaskView taskView;
        super.onGestureAnimationEnd();
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mHomeTaskInfo;
        if (runningTaskInfo == null || (taskView = getTaskView(runningTaskInfo.taskId)) == null) {
            return;
        }
        PendingAnimation pendingAnimationCreateTaskDismissAnimation = createTaskDismissAnimation(taskView, true, false, 150L, false);
        pendingAnimationCreateTaskDismissAnimation.addEndListener(new Consumer() { // from class: com.android.quickstep.fallback.-$$Lambda$FallbackRecentsView$Q_aXI7oZ-gF3MHdKQgo1xibjYW4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$onGestureAnimationEnd$0$FallbackRecentsView((PendingAnimation.EndState) obj);
            }
        });
        runDismissAnimation(pendingAnimationCreateTaskDismissAnimation);
    }

    public /* synthetic */ void lambda$onGestureAnimationEnd$0$FallbackRecentsView(PendingAnimation.EndState endState) {
        setCurrentTask(-1);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void setCurrentTask(int runningTaskId) {
        super.setCurrentTask(runningTaskId);
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mHomeTaskInfo;
        if (runningTaskInfo == null || runningTaskInfo.taskId == runningTaskId) {
            return;
        }
        this.mHomeTaskInfo = null;
        setRunningTaskHidden(false);
    }

    @Override // com.android.quickstep.views.RecentsView
    protected boolean shouldAddDummyTaskView(int runningTaskId) {
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mHomeTaskInfo;
        if (runningTaskInfo != null && runningTaskInfo.taskId == runningTaskId && getTaskViewCount() == 0) {
            return false;
        }
        return super.shouldAddDummyTaskView(runningTaskId);
    }

    @Override // com.android.quickstep.views.RecentsView
    protected void applyLoadPlan(ArrayList<Task> tasks) {
        boolean z;
        int displayId = getContext().getDisplayId();
        if (displayId != 0) {
            ArrayList arrayList = new ArrayList();
            PackageManager packageManager = getContext().getPackageManager();
            for (Task task : tasks) {
                if (task != null && task.key != null && (PackageUtils.isUse_Activities_on_secondary(packageManager, task.key.getPackageName()) || ActivityManagerWrapperEx.getInstance().hasFlagActivityExcludedFromRecents(task) || !ActivityManagerWrapperEx.getInstance().canBeLaunchedOnSubDisplay(displayId, task.key.getPackageName()))) {
                    arrayList.add(task);
                }
            }
            if (arrayList.size() > 0) {
                tasks.removeAll(arrayList);
            }
        }
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mHomeTaskInfo;
        if (runningTaskInfo != null && runningTaskInfo.taskId == this.mRunningTaskId && !tasks.isEmpty()) {
            Iterator<Task> it = tasks.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().key.id == this.mRunningTaskId) {
                        z = true;
                        break;
                    }
                } else {
                    z = false;
                    break;
                }
            }
            if (!z) {
                ArrayList<Task> arrayList2 = new ArrayList<>(tasks.size() + 1);
                arrayList2.addAll(tasks);
                arrayList2.add(Task.from(new Task.TaskKey(this.mHomeTaskInfo), this.mHomeTaskInfo, false));
                tasks = arrayList2;
            }
        }
        super.applyLoadPlan(tasks);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void setRunningTaskHidden(boolean isHidden) {
        if (this.mHomeTaskInfo != null) {
            isHidden = true;
        }
        super.setRunningTaskHidden(isHidden);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void setModalStateEnabled(boolean isModalState) {
        super.setModalStateEnabled(isModalState);
        if (isModalState) {
            ((RecentsActivity) this.mActivity).getStateManager().goToState(RecentsState.MODAL_TASK);
        } else if (((RecentsActivity) this.mActivity).isInState(RecentsState.MODAL_TASK)) {
            ((RecentsActivity) this.mActivity).getStateManager().goToState(RecentsState.DEFAULT);
        }
    }

    /* JADX DEBUG: Method merged with bridge method: onStateTransitionStart(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateListener
    public void onStateTransitionStart(RecentsState toState) {
        setOverviewStateEnabled(true);
        setFreezeViewVisibility(true);
    }

    /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateListener
    public void onStateTransitionComplete(RecentsState finalState) {
        setOverlayEnabled(finalState == RecentsState.DEFAULT || finalState == RecentsState.MODAL_TASK);
        setFreezeViewVisibility(false);
        launchSplitSelectedState();
    }

    @Override // com.android.quickstep.views.RecentsView
    public void setOverviewStateEnabled(boolean enabled) {
        super.setOverviewStateEnabled(enabled);
        if (enabled) {
            setDisallowScrollToClearAll(!((RecentsState) ((RecentsActivity) this.mActivity).getStateManager().getState()).hasButtons());
        }
        boolean z = getRecommandLayout() != null && LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue();
        boolean z2 = ((RecentsActivity) this.mActivity).getDeviceProfile().isLandscape;
        boolean z3 = ((RecentsActivity) this.mActivity).getDeviceProfile().isMultiWindowMode;
        float f = (((RecentsState) ((RecentsActivity) this.mActivity).getStateManager().getState()).hasRecommand() && z && !z2 && LGHomeFeature.isAppSuggestionEnabled()) ? 1.0f : 0.0f;
        if (z) {
            if (getContext().getDisplayId() == 4) {
                RECOMMAND_APP_ALPHA.setValue(this, 0.0f);
            } else {
                RECOMMAND_APP_ALPHA.setValue(this, f);
            }
        }
        if (getClearAllButton() != null) {
            ClearAllButton.VISIBILITY_ALPHA.set(getClearAllButton(), Float.valueOf(getChildCount() > 0 && !isFastOverView() ? 1.0f : 0.0f));
            setLocationOfClearAllButton();
        }
    }

    @Override // com.android.quickstep.views.RecentsView
    public void initiateSplitSelect(QuickstepSystemShortcut.SplitSelectSource splitSelectSource) {
        super.initiateSplitSelect(splitSelectSource);
        ((RecentsActivity) this.mActivity).getStateManager().goToState(RecentsState.OVERVIEW_SPLIT_SELECT);
    }

    @Override // com.android.quickstep.views.RecentsView
    public void initiateSplitSelect(TaskView taskView, int stagePosition) {
        super.initiateSplitSelect(taskView, stagePosition);
        ((RecentsActivity) this.mActivity).getStateManager().goToState(RecentsState.OVERVIEW_SPLIT_SELECT);
    }

    @Override // com.android.quickstep.views.RecentsView
    public boolean isFastOverView() {
        return !((RecentsState) ((RecentsActivity) this.mActivity).getStateManager().getState()).hasRecommand();
    }
}
