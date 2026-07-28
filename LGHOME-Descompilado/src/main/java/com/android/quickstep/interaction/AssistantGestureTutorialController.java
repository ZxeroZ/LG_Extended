package com.android.quickstep.interaction;

import android.graphics.PointF;
import android.view.View;
import com.android.quickstep.interaction.EdgeBackGestureHandler;
import com.android.quickstep.interaction.NavBarGestureHandler;
import com.android.quickstep.interaction.TutorialController;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
final class AssistantGestureTutorialController extends TutorialController {
    @Override // com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureAttemptCallback
    public void setAssistantProgress(float progress) {
    }

    AssistantGestureTutorialController(AssistantGestureTutorialFragment fragment, TutorialController.TutorialType tutorialType) {
        super(fragment, tutorialType);
    }

    @Override // com.android.quickstep.interaction.TutorialController
    Integer getTitleStringId() {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
        if (i == 1) {
            return Integer.valueOf(R.string.assistant_gesture_tutorial_playground_title);
        }
        if (i != 2) {
            return null;
        }
        return Integer.valueOf(R.string.gesture_tutorial_confirm_title);
    }

    @Override // com.android.quickstep.interaction.TutorialController
    Integer getSubtitleStringId() {
        if (this.mTutorialType == TutorialController.TutorialType.ASSISTANT) {
            return Integer.valueOf(R.string.assistant_gesture_tutorial_playground_subtitle);
        }
        return null;
    }

    @Override // com.android.quickstep.interaction.TutorialController
    Integer getActionButtonStringId() {
        if (this.mTutorialType == TutorialController.TutorialType.ASSISTANT_COMPLETE) {
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
            showFeedback(R.string.assistant_gesture_feedback_swipe_too_far_from_corner);
        }
    }

    @Override // com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureAttemptCallback
    public void onNavBarGestureAttempted(NavBarGestureHandler.NavBarGestureResult result, PointF finalVelocity) {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
        if (i == 1) {
            switch (AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[result.ordinal()]) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    showFeedback(R.string.assistant_gesture_feedback_swipe_too_far_from_corner);
                    break;
                case 7:
                    hideFeedback();
                    hideHandCoachingAnimation();
                    showRippleEffect(new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$AssistantGestureTutorialController$XCQFvINqNDH8U1-EGnrW2uIq3S0
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onNavBarGestureAttempted$0$AssistantGestureTutorialController();
                        }
                    });
                    break;
                case 8:
                    showFeedback(R.string.assistant_gesture_feedback_swipe_not_diagonal);
                    break;
                case 9:
                    showFeedback(R.string.assistant_gesture_feedback_swipe_not_long_enough);
                    break;
            }
            return;
        }
        if (i == 2 && result == NavBarGestureHandler.NavBarGestureResult.HOME_GESTURE_COMPLETED) {
            this.mTutorialFragment.closeTutorial();
        }
    }

    /* JADX INFO: renamed from: com.android.quickstep.interaction.AssistantGestureTutorialController$1, reason: invalid class name */
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
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.OVERVIEW_GESTURE_COMPLETED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.HOME_NOT_STARTED_TOO_FAR_FROM_EDGE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.OVERVIEW_NOT_STARTED_TOO_FAR_FROM_EDGE.ordinal()] = 4;
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
            try {
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.ASSISTANT_COMPLETED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.ASSISTANT_NOT_STARTED_BAD_ANGLE.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[NavBarGestureHandler.NavBarGestureResult.ASSISTANT_NOT_STARTED_SWIPE_TOO_SHORT.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            int[] iArr2 = new int[EdgeBackGestureHandler.BackGestureResult.values().length];
            $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult = iArr2;
            try {
                iArr2[EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused13) {
            }
            int[] iArr3 = new int[TutorialController.TutorialType.values().length];
            $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType = iArr3;
            try {
                iArr3[TutorialController.TutorialType.ASSISTANT.ordinal()] = 1;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.ASSISTANT_COMPLETE.ordinal()] = 2;
            } catch (NoSuchFieldError unused15) {
            }
        }
    }

    public /* synthetic */ void lambda$onNavBarGestureAttempted$0$AssistantGestureTutorialController() {
        this.mTutorialFragment.changeController(TutorialController.TutorialType.ASSISTANT_COMPLETE);
    }
}
