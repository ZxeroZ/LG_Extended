package com.android.launcher3.statemanager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.LauncherRootView;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.views.BaseDragLayer;
import com.lge.launcher3.wing.CarouselLayout;

/* JADX INFO: loaded from: classes.dex */
public abstract class StatefulActivity<STATE_TYPE extends BaseState<STATE_TYPE>> extends BaseDraggingActivity {
    private boolean mDeferredResumePending;
    private LauncherRootView mRootView;
    public final Handler mHandler = new Handler();
    private final Runnable mHandleDeferredResume = new Runnable() { // from class: com.android.launcher3.statemanager.-$$Lambda$PfKHpYxio81s8z3GRUF7bD3ZDc0
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.handleDeferredResume();
        }
    };

    public void controlStatusBar() {
    }

    protected abstract StateManager.StateHandler<STATE_TYPE>[] createStateHandlers();

    public void exitCleanViewMode() {
    }

    public CarouselLayout getCarouselLayout() {
        return null;
    }

    public abstract StateManager<STATE_TYPE> getStateManager();

    public Workspace getWorkspace() {
        return null;
    }

    protected void onDeferredResumed() {
    }

    public void onStateSetEnd(STATE_TYPE state) {
    }

    protected void onUiChangedWhileSleeping() {
    }

    public boolean showWorkspace(boolean animated) {
        return false;
    }

    public boolean isInState(STATE_TYPE state) {
        return getStateManager().getState() == state;
    }

    protected void inflateRootView(int layoutId) {
        LauncherRootView launcherRootView = (LauncherRootView) LayoutInflater.from(this).inflate(layoutId, (ViewGroup) null);
        this.mRootView = launcherRootView;
        launcherRootView.setSystemUiVisibility(1792);
    }

    /* JADX DEBUG: Method merged with bridge method: getRootView()Landroid/view/View; */
    @Override // com.android.launcher3.BaseDraggingActivity
    public final LauncherRootView getRootView() {
        return this.mRootView;
    }

    @Override // android.app.Activity
    public <T extends View> T findViewById(int i) {
        return (T) this.mRootView.findViewById(i);
    }

    public void onStateSetStart(STATE_TYPE state) {
        if (this.mDeferredResumePending) {
            handleDeferredResume();
        }
    }

    public StateManager.AtomicAnimationFactory<STATE_TYPE> createAtomicAnimationFactory() {
        return new StateManager.AtomicAnimationFactory<>(0);
    }

    @Override // com.android.launcher3.BaseDraggingActivity
    public void reapplyUi() {
        reapplyUi(true);
    }

    public void reapplyUi(boolean cancelCurrentAnimation) {
        getRootView().dispatchInsets();
        getStateManager().reapplyState(cancelCurrentAnimation);
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    protected void onStop() {
        final BaseDragLayer dragLayer = getDragLayer();
        boolean zIsUserActive = isUserActive();
        final BaseState state = getStateManager().getState();
        final int childCount = dragLayer.getChildCount();
        super.onStop();
        getStateManager().moveToRestState();
        onTrimMemory(20);
        if (zIsUserActive) {
            dragLayer.post(new Runnable() { // from class: com.android.launcher3.statemanager.-$$Lambda$StatefulActivity$eX-Mk2mv6cNmoDVXGXUA78x9kEY
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onStop$0$StatefulActivity(state, dragLayer, childCount);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onStop$0$StatefulActivity(BaseState baseState, BaseDragLayer baseDragLayer, int i) {
        if (getStateManager().isInStableState(baseState) && baseDragLayer.getAlpha() >= 1.0f && baseDragLayer.getChildCount() == i) {
            return;
        }
        onUiChangedWhileSleeping();
    }

    public void handleDeferredResume() {
        if (hasBeenResumed() && !getStateManager().getState().hasFlag(1)) {
            onDeferredResumed();
            addActivityFlags(4);
            this.mDeferredResumePending = false;
            return;
        }
        this.mDeferredResumePending = true;
    }

    @Override // com.android.launcher3.BaseDraggingActivity, com.android.launcher3.BaseActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mHandler.removeCallbacks(this.mHandleDeferredResume);
        Utilities.postAsyncCallback(this.mHandler, this.mHandleDeferredResume);
    }
}
