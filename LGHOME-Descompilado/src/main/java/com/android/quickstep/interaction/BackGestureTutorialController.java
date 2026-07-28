package com.android.quickstep.interaction;

import android.graphics.PointF;
import android.view.View;
import com.android.quickstep.interaction.EdgeBackGestureHandler;
import com.android.quickstep.interaction.NavBarGestureHandler;
import com.android.quickstep.interaction.TutorialController;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
final class BackGestureTutorialController extends TutorialController {
    BackGestureTutorialController(BackGestureTutorialFragment fragment, TutorialController.TutorialType tutorialType) {
        super(fragment, tutorialType);
    }

    @Override // com.android.quickstep.interaction.TutorialController
    Integer getTitleStringId() {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
        if (i == 1) {
            return Integer.valueOf(R.string.back_gesture_tutorial_playground_title_swipe_inward_right_edge);
        }
        if (i == 2) {
            return Integer.valueOf(R.string.back_gesture_tutorial_playground_title_swipe_inward_left_edge);
        }
        if (i != 3) {
            return null;
        }
        return Integer.valueOf(R.string.gesture_tutorial_confirm_title);
    }

    @Override // com.android.quickstep.interaction.TutorialController
    Integer getSubtitleStringId() {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
        if (i == 1) {
            return Integer.valueOf(R.string.back_gesture_tutorial_engaged_subtitle_swipe_inward_right_edge);
        }
        if (i == 2) {
            return Integer.valueOf(R.string.back_gesture_tutorial_engaged_subtitle_swipe_inward_left_edge);
        }
        if (i != 3) {
            return null;
        }
        return Integer.valueOf(R.string.back_gesture_tutorial_confirm_subtitle);
    }

    @Override // com.android.quickstep.interaction.TutorialController
    Integer getActionButtonStringId() {
        if (this.mTutorialType == TutorialController.TutorialType.BACK_NAVIGATION_COMPLETE) {
            return Integer.valueOf(R.string.gesture_tutorial_action_button_label_done);
        }
        return null;
    }

    @Override // com.android.quickstep.interaction.TutorialController
    Integer getActionTextButtonStringId() {
        if (this.mTutorialType == TutorialController.TutorialType.BACK_NAVIGATION_COMPLETE) {
            return Integer.valueOf(R.string.gesture_tutorial_action_button_label_settings);
        }
        return null;
    }

    @Override // com.android.quickstep.interaction.TutorialController
    void onActionButtonClicked(View button) {
        this.mTutorialFragment.closeTutorial();
    }

    @Override // com.android.quickstep.interaction.TutorialController
    void onActionTextButtonClicked(View button) {
        this.mTutorialFragment.startSystemNavigationSetting();
    }

    @Override // com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureAttemptCallback
    public void onBackGestureAttempted(EdgeBackGestureHandler.BackGestureResult result) {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
        if (i == 1) {
            handleAttemptFromRight(result);
            return;
        }
        if (i == 2) {
            handleAttemptFromLeft(result);
        } else {
            if (i != 3) {
                return;
            }
            if (result == EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT || result == EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT) {
                this.mTutorialFragment.closeTutorial();
            }
        }
    }

    /* JADX INFO: renamed from: com.android.quickstep.interaction.BackGestureTutorialController$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult;
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType;

        static {
            int[] iArr = new int[EdgeBackGestureHandler.BackGestureResult.values().length];
            $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult = iArr;
            try {
                iArr[EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_LEFT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_NOT_STARTED_TOO_FAR_FROM_EDGE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_NOT_STARTED_IN_NAV_BAR_REGION.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            int[] iArr2 = new int[TutorialController.TutorialType.values().length];
            $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType = iArr2;
            try {
                iArr2[TutorialController.TutorialType.RIGHT_EDGE_BACK_NAVIGATION.ordinal()] = 1;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.LEFT_EDGE_BACK_NAVIGATION.ordinal()] = 2;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.BACK_NAVIGATION_COMPLETE.ordinal()] = 3;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }

    private void handleAttemptFromRight(EdgeBackGestureHandler.BackGestureResult result) {
        switch (AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[result.ordinal()]) {
            case 1:
                hideFeedback();
                hideHandCoachingAnimation();
                showRippleEffect(new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$BackGestureTutorialController$peFpfjR95MNASwIDLetU3wYSeJE
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$handleAttemptFromRight$0$BackGestureTutorialController();
                    }
                });
                break;
            case 2:
                showFeedback(R.string.back_gesture_feedback_cancelled_right_edge);
                break;
            case 3:
            case 4:
            case 5:
                showFeedback(R.string.back_gesture_feedback_swipe_too_far_from_right_edge);
                break;
            case 6:
                showFeedback(R.string.back_gesture_feedback_swipe_in_nav_bar);
                break;
        }
    }

    public /* synthetic */ void lambda$handleAttemptFromRight$0$BackGestureTutorialController() {
        this.mTutorialFragment.changeController(TutorialController.TutorialType.LEFT_EDGE_BACK_NAVIGATION);
    }

    private void handleAttemptFromLeft(EdgeBackGestureHandler.BackGestureResult result) {
        switch (AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[result.ordinal()]) {
            case 1:
            case 2:
            case 5:
                showFeedback(R.string.back_gesture_feedback_swipe_too_far_from_left_edge);
                break;
            case 3:
                hideFeedback();
                hideHandCoachingAnimation();
                showRippleEffect(new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$BackGestureTutorialController$RmJpuldtMCyguLBJH1VYJOB9Cm0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$handleAttemptFromLeft$1$BackGestureTutorialController();
                    }
                });
                break;
            case 4:
                showFeedback(R.string.back_gesture_feedback_cancelled_left_edge);
                break;
            case 6:
                showFeedback(R.string.back_gesture_feedback_swipe_in_nav_bar);
                break;
        }
    }

    public /* synthetic */ void lambda$handleAttemptFromLeft$1$BackGestureTutorialController() {
        this.mTutorialFragment.changeController(TutorialController.TutorialType.BACK_NAVIGATION_COMPLETE);
    }

    @Override // com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureAttemptCallback
    public void onNavBarGestureAttempted(NavBarGestureHandler.NavBarGestureResult result, PointF finalVelocity) {
        if (this.mTutorialType == TutorialController.TutorialType.BACK_NAVIGATION_COMPLETE && result == NavBarGestureHandler.NavBarGestureResult.HOME_GESTURE_COMPLETED) {
            this.mTutorialFragment.closeTutorial();
        }
    }
}
