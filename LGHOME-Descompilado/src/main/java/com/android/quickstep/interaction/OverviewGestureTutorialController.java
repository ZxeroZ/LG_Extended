package com.android.quickstep.interaction;

import android.graphics.PointF;
import android.view.View;
import com.android.quickstep.interaction.EdgeBackGestureHandler;
import com.android.quickstep.interaction.NavBarGestureHandler;
import com.android.quickstep.interaction.TutorialController;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
final class OverviewGestureTutorialController extends SwipeUpGestureTutorialController {
    OverviewGestureTutorialController(OverviewGestureTutorialFragment fragment, TutorialController.TutorialType tutorialType) {
        super(fragment, tutorialType);
    }

    @Override // com.android.quickstep.interaction.TutorialController
    Integer getTitleStringId() {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
        if (i == 1) {
            return Integer.valueOf(R.string.overview_gesture_tutorial_playground_title);
        }
        if (i != 2) {
            return null;
        }
        return Integer.valueOf(R.string.gesture_tutorial_confirm_title);
    }

    @Override // com.android.quickstep.interaction.TutorialController
    Integer getSubtitleStringId() {
        if (this.mTutorialType == TutorialController.TutorialType.OVERVIEW_NAVIGATION) {
            return Integer.valueOf(R.string.overview_gesture_tutorial_playground_subtitle);
        }
        return null;
    }

    @Override // com.android.quickstep.interaction.TutorialController
    Integer getActionButtonStringId() {
        if (this.mTutorialType == TutorialController.TutorialType.OVERVIEW_NAVIGATION_COMPLETE) {
            return Integer.valueOf(R.string.gesture_tutorial_action_button_label_done);
        }
        return null;
    }

    @Override // com.android.quickstep.interaction.TutorialController
    void onActionButtonClicked(View button) {
        this.mTutorialFragment.closeTutorial();
    }

    @Override // com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureAttemptCallback
    public void onBackGestureAttempted(EdgeBackGestureHandler.BackGestureResult result) {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
        if (i != 1) {
            if (i != 2) {
                return;
            }
            if (result == EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT || result == EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT) {
                this.mTutorialFragment.closeTutorial();
                return;
            }
            return;
        }
        int i2 = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[result.ordinal()];
        if (i2 == 1 || i2 == 2 || i2 == 3 || i2 == 4) {
            showFeedback(R.string.overview_gesture_feedback_swipe_too_far_from_edge);
        }
    }

    @Override // com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureAttemptCallback
    public void onNavBarGestureAttempted(NavBarGestureHandler.NavBarGestureResult result, PointF finalVelocity) {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
        if (i == 1) {
            switch (AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[result.ordinal()]) {
                case 1:
                    animateFakeTaskViewHome(finalVelocity, new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$OverviewGestureTutorialController$12K26Nfhi4blahOfmX63nKZ25Bs
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onNavBarGestureAttempted$0$OverviewGestureTutorialController();
                        }
                    });
                    break;
                case 2:
                case 3:
                    showFeedback(R.string.overview_gesture_feedback_swipe_too_far_from_edge);
                    break;
                case 4:
                    fadeOutFakeTaskView(true, new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$OverviewGestureTutorialController$AdMMugbt7Olq7YGzAvDFLx1E1Og
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onNavBarGestureAttempted$1$OverviewGestureTutorialController();
                        }
                    });
                    break;
                case 5:
                case 6:
                    fadeOutFakeTaskView(false, null);
                    showFeedback(R.string.overview_gesture_feedback_wrong_swipe_direction);
                    break;
            }
            return;
        }
        if (i == 2 && result == NavBarGestureHandler.NavBarGestureResult.HOME_GESTURE_COMPLETED) {
            this.mTutorialFragment.closeTutorial();
        }
    }

    /* JADX INFO: renamed from: com.android.quickstep.interaction.OverviewGestureTutorialController$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult;
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult;
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType;

        static {
            int[] iArr = new int[NavBarGestureHandler.NavBarGestureResult.values().length];
            $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult = iArr;
            try {
                iArr[NavBarGestureHandler.NavBarGestureResult.HOME_GESTURE_COMPLETED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.HOME_NOT_STARTED_TOO_FAR_FROM_EDGE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.OVERVIEW_NOT_STARTED_TOO_FAR_FROM_EDGE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.OVERVIEW_GESTURE_COMPLETED.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.HOME_OR_OVERVIEW_NOT_STARTED_WRONG_SWIPE_DIRECTION.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.HOME_OR_OVERVIEW_CANCELLED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            int[] iArr2 = new int[EdgeBackGestureHandler.BackGestureResult.values().length];
            $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult = iArr2;
            try {
                iArr2[EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused10) {
            }
            int[] iArr3 = new int[TutorialController.TutorialType.values().length];
            $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType = iArr3;
            try {
                iArr3[TutorialController.TutorialType.OVERVIEW_NAVIGATION.ordinal()] = 1;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.OVERVIEW_NAVIGATION_COMPLETE.ordinal()] = 2;
            } catch (NoSuchFieldError unused12) {
            }
        }
    }

    public /* synthetic */ void lambda$onNavBarGestureAttempted$0$OverviewGestureTutorialController() {
        showFeedback(R.string.overview_gesture_feedback_home_detected);
    }

    public /* synthetic */ void lambda$onNavBarGestureAttempted$1$OverviewGestureTutorialController() {
        this.mTutorialFragment.changeController(TutorialController.TutorialType.OVERVIEW_NAVIGATION_COMPLETE);
    }
}
