package com.android.quickstep;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.RectF;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.views.FloatingIconView;
import com.android.quickstep.SwipeUpAnimationLogic;
import com.android.quickstep.util.StaggeredWorkspaceAnim;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.system.InputConsumerController;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class LauncherSwipeHandlerV2 extends BaseSwipeUpHandlerV2<BaseQuickstepLauncher, RecentsView> {
    private static final String TAG = "LauncherSwipeHandlerV2";

    public LauncherSwipeHandlerV2(Context context, RecentsAnimationDeviceState deviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, long touchTimeMs, boolean continuingLastGesture, InputConsumerController inputConsumer) {
        super(context, deviceState, taskAnimationManager, gestureState, touchTimeMs, continuingLastGesture, inputConsumer);
    }

    @Override // com.android.quickstep.BaseSwipeUpHandlerV2
    protected SwipeUpAnimationLogic.HomeAnimationFactory createHomeAnimationFactory(final long duration) {
        View firstMatchForAppClose;
        if (this.mActivity != 0) {
            TaskView runningTaskView = this.mRecentsView.getRunningTaskView();
            if (runningTaskView == null || runningTaskView.getTask().key.getComponent() == null) {
                firstMatchForAppClose = null;
            } else {
                firstMatchForAppClose = (((BaseQuickstepLauncher) this.mActivity).getWorkspace() == null || (LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue() && ((BaseQuickstepLauncher) this.mActivity).getWorkspace().getScreenIdForPageIndex(((BaseQuickstepLauncher) this.mActivity).getWorkspace().getCurrentPage()) == -301)) ? null : ((BaseQuickstepLauncher) this.mActivity).getWorkspace().getFirstMatchForAppClose(runningTaskView.getTask().key.getComponent().getPackageName(), UserHandle.of(runningTaskView.getTask().key.userId));
                Log.i(TAG, "[createHomeAnimationFactory] runningTaskWindowMode : " + runningTaskView.getTask().key.windowingMode);
            }
            final RectF rectF = new RectF();
            final boolean z = runningTaskView != null && runningTaskView.getTask().key.windowingMode == 6;
            boolean zIsSplitScreenVisible = SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).isSplitScreenVisible();
            Log.i(TAG, "[createHomeAnimationFactory] isSplitScreenTask: " + z + " isSplitScreenVisible: " + zIsSplitScreenVisible);
            final boolean z2 = (firstMatchForAppClose == null || !firstMatchForAppClose.isAttachedToWindow() || LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || z || zIsSplitScreenVisible) ? false : true;
            floatingIconView = z2 ? FloatingIconView.getFloatingIconView((Launcher) this.mActivity, firstMatchForAppClose, true, rectF, FloatingIconView.Action.Close) : null;
            ((BaseQuickstepLauncher) this.mActivity).getRootView().setForceHideBackArrow(true);
            ((BaseQuickstepLauncher) this.mActivity).setHintUserWillBeActive();
            return new SwipeUpAnimationLogic.HomeAnimationFactory(floatingIconView) { // from class: com.android.quickstep.LauncherSwipeHandlerV2.1
                @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
                public boolean needToForceEnd() {
                    return z;
                }

                @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
                public RectF getWindowTargetRect() {
                    if (z) {
                        return getWindowTargetRectOnSplitMode();
                    }
                    if (z2) {
                        return rectF;
                    }
                    return super.getWindowTargetRect();
                }

                private RectF getWindowTargetRectOnSplitMode() {
                    return new RectF(0.0f, 0.0f, 0.0f, 0.0f);
                }

                @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
                public AnimatorPlaybackController createActivityAnimationToHome() {
                    return ((BaseQuickstepLauncher) LauncherSwipeHandlerV2.this.mActivity).getStateManager().createAnimationToNewWorkspace(LauncherState.NORMAL, Math.max(LauncherSwipeHandlerV2.this.mDp.widthPx, LauncherSwipeHandlerV2.this.mDp.heightPx) * 2, 0);
                }

                @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
                public void playAtomicAnimation(float velocity) {
                    new StaggeredWorkspaceAnim((Launcher) LauncherSwipeHandlerV2.this.mActivity, velocity, true).start();
                }
            };
        }
        SwipeUpAnimationLogic.HomeAnimationFactory homeAnimationFactory = new SwipeUpAnimationLogic.HomeAnimationFactory(floatingIconView) { // from class: com.android.quickstep.LauncherSwipeHandlerV2.2
            @Override // com.android.quickstep.SwipeUpAnimationLogic.HomeAnimationFactory
            public AnimatorPlaybackController createActivityAnimationToHome() {
                return AnimatorPlaybackController.wrap(new AnimatorSet(), duration);
            }
        };
        this.mStateCallback.addChangeListener(STATE_LAUNCHER_PRESENT | STATE_HANDLER_INVALIDATED, new Consumer() { // from class: com.android.quickstep.-$$Lambda$LauncherSwipeHandlerV2$ncvybp13fGHhi_E6uOLbMbaqoMg
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$createHomeAnimationFactory$0$LauncherSwipeHandlerV2((Boolean) obj);
            }
        });
        return homeAnimationFactory;
    }

    public /* synthetic */ void lambda$createHomeAnimationFactory$0$LauncherSwipeHandlerV2(Boolean bool) {
        this.mRecentsView.startHome();
    }

    @Override // com.android.quickstep.BaseSwipeUpHandlerV2
    protected void finishRecentsControllerToHome(Runnable callback) {
        this.mRecentsAnimationController.finish(true, callback, true);
    }
}
