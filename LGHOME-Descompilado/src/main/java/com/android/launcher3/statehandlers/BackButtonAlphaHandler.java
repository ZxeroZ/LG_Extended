package com.android.launcher3.statehandlers;

import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.UiThreadHelper;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.SystemUiProxy;

/* JADX INFO: loaded from: classes.dex */
public class BackButtonAlphaHandler implements StateManager.StateHandler<LauncherState> {
    private final AnimatedFloat mBackAlpha = new AnimatedFloat(new Runnable() { // from class: com.android.launcher3.statehandlers.-$$Lambda$BackButtonAlphaHandler$mrdHyxBR9D2GejP0Z1FCHuzG_k0
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.updateBackAlpha();
        }
    });
    private final BaseQuickstepLauncher mLauncher;

    /* JADX DEBUG: Method merged with bridge method: setState(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setState(LauncherState state) {
    }

    public BackButtonAlphaHandler(BaseQuickstepLauncher launcher) {
        this.mLauncher = launcher;
    }

    /* JADX DEBUG: Method merged with bridge method: setStateWithAnimation(Ljava/lang/Object;Lcom/android/launcher3/states/StateAnimationConfig;Lcom/android/launcher3/anim/PendingAnimation;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setStateWithAnimation(LauncherState toState, StateAnimationConfig config, PendingAnimation animation) {
        if (config.onlyPlayAtomicComponent()) {
            return;
        }
        if (!SysUINavigationMode.getMode(this.mLauncher).hasGestures) {
            UiThreadHelper.setBackButtonAlphaAsync(this.mLauncher, BaseQuickstepLauncher.SET_BACK_BUTTON_ALPHA, 1.0f, true);
            return;
        }
        this.mBackAlpha.value = SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).getLastNavButtonAlpha();
        boolean zShouldBackButtonBeHidden = this.mLauncher.shouldBackButtonBeHidden(toState);
        this.mLauncher.getRootView().setDisallowBackGesture(zShouldBackButtonBeHidden);
        animation.setFloat(this.mBackAlpha, AnimatedFloat.VALUE, zShouldBackButtonBeHidden ? 0.0f : 1.0f, Interpolators.LINEAR);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBackAlpha() {
        UiThreadHelper.setBackButtonAlphaAsync(this.mLauncher, BaseQuickstepLauncher.SET_BACK_BUTTON_ALPHA, this.mBackAlpha.value, false);
    }
}
