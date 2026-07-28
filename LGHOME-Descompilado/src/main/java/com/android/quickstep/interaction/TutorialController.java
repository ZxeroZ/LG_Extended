package com.android.quickstep.interaction;

import android.content.Context;
import android.graphics.drawable.RippleDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.quickstep.interaction.EdgeBackGestureHandler;
import com.android.quickstep.interaction.NavBarGestureHandler;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
abstract class TutorialController implements EdgeBackGestureHandler.BackGestureAttemptCallback, NavBarGestureHandler.NavBarGestureAttemptCallback {
    private static final int FEEDBACK_ANIMATION_MS = 500;
    private static final int FEEDBACK_VISIBLE_MS = 3000;
    private static final int RIPPLE_VISIBLE_MS = 300;
    final Button mActionButton;
    final Button mActionTextButton;
    final ImageButton mCloseButton;
    final Context mContext;
    final View mFakeTaskView;
    final TextView mFeedbackView;
    final TutorialHandAnimation mHandCoachingAnimation;
    final ImageView mHandCoachingView;
    private final Runnable mHideFeedbackRunnable;
    final RippleDrawable mRippleDrawable;
    final View mRippleView;
    final TextView mSubtitleTextView;
    final TextView mTitleTextView;
    final TutorialFragment mTutorialFragment;
    TutorialType mTutorialType;

    enum TutorialType {
        RIGHT_EDGE_BACK_NAVIGATION,
        LEFT_EDGE_BACK_NAVIGATION,
        BACK_NAVIGATION_COMPLETE,
        HOME_NAVIGATION,
        HOME_NAVIGATION_COMPLETE,
        OVERVIEW_NAVIGATION,
        OVERVIEW_NAVIGATION_COMPLETE,
        ASSISTANT,
        ASSISTANT_COMPLETE
    }

    Integer getActionButtonStringId() {
        return null;
    }

    Integer getActionTextButtonStringId() {
        return null;
    }

    Integer getSubtitleStringId() {
        return null;
    }

    Integer getTitleStringId() {
        return null;
    }

    void onActionButtonClicked(View button) {
    }

    void onActionTextButtonClicked(View button) {
    }

    TutorialController(TutorialFragment tutorialFragment, TutorialType tutorialType) {
        this.mTutorialFragment = tutorialFragment;
        this.mTutorialType = tutorialType;
        this.mContext = tutorialFragment.getContext();
        View rootView = tutorialFragment.getRootView();
        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.gesture_tutorial_fragment_close_button);
        this.mCloseButton = imageButton;
        imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.quickstep.interaction.-$$Lambda$TutorialController$cQcxh2H43NgQwxEriUA6mhX5iTQ
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$0$TutorialController(view);
            }
        });
        this.mTitleTextView = (TextView) rootView.findViewById(R.id.gesture_tutorial_fragment_title_view);
        this.mSubtitleTextView = (TextView) rootView.findViewById(R.id.gesture_tutorial_fragment_subtitle_view);
        this.mFeedbackView = (TextView) rootView.findViewById(R.id.gesture_tutorial_fragment_feedback_view);
        this.mFakeTaskView = rootView.findViewById(R.id.gesture_tutorial_fake_task_view);
        View viewFindViewById = rootView.findViewById(R.id.gesture_tutorial_ripple_view);
        this.mRippleView = viewFindViewById;
        this.mRippleDrawable = (RippleDrawable) viewFindViewById.getBackground();
        this.mHandCoachingAnimation = tutorialFragment.getHandAnimation();
        ImageView imageView = (ImageView) rootView.findViewById(R.id.gesture_tutorial_fragment_hand_coaching);
        this.mHandCoachingView = imageView;
        imageView.bringToFront();
        this.mActionTextButton = (Button) rootView.findViewById(R.id.gesture_tutorial_fragment_action_text_button);
        this.mActionButton = (Button) rootView.findViewById(R.id.gesture_tutorial_fragment_action_button);
        this.mHideFeedbackRunnable = new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$TutorialController$0fkxqp2nAVWdeVP_ys95eofA5SY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$1$TutorialController();
            }
        };
    }

    public /* synthetic */ void lambda$new$0$TutorialController(View view) {
        this.mTutorialFragment.closeTutorial();
    }

    public /* synthetic */ void lambda$new$1$TutorialController() {
        this.mFeedbackView.animate().alpha(0.0f).setDuration(500L).withEndAction(new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$YZoQEzAoimBoZpqu_k1I2UP3yBo
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.showHandCoachingAnimation();
            }
        }).start();
    }

    void setTutorialType(TutorialType tutorialType) {
        this.mTutorialType = tutorialType;
    }

    void showFeedback(int resId) {
        hideHandCoachingAnimation();
        this.mFeedbackView.setText(resId);
        this.mFeedbackView.animate().alpha(1.0f).setDuration(500L).start();
        this.mFeedbackView.removeCallbacks(this.mHideFeedbackRunnable);
        this.mFeedbackView.postDelayed(this.mHideFeedbackRunnable, 3000L);
    }

    void hideFeedback() {
        this.mFeedbackView.setText((CharSequence) null);
        this.mFeedbackView.removeCallbacks(this.mHideFeedbackRunnable);
        this.mFeedbackView.clearAnimation();
        this.mFeedbackView.setAlpha(0.0f);
    }

    void setRippleHotspot(float x, float y) {
        this.mRippleDrawable.setHotspot(x, y);
    }

    void showRippleEffect(final Runnable onCompleteRunnable) {
        this.mRippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});
        this.mRippleView.postDelayed(new Runnable() { // from class: com.android.quickstep.interaction.-$$Lambda$TutorialController$444mAelP2IV77LtbIeCOxdnwM4Y
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showRippleEffect$2$TutorialController(onCompleteRunnable);
            }
        }, 300L);
    }

    public /* synthetic */ void lambda$showRippleEffect$2$TutorialController(Runnable runnable) {
        this.mRippleDrawable.setState(new int[0]);
        if (runnable != null) {
            runnable.run();
        }
    }

    void showHandCoachingAnimation() {
        if (isComplete()) {
            return;
        }
        this.mHandCoachingAnimation.startLoopedAnimation(this.mTutorialType);
    }

    void hideHandCoachingAnimation() {
        this.mHandCoachingAnimation.stop();
        this.mHandCoachingView.setVisibility(4);
    }

    void transitToController() {
        hideFeedback();
        updateTitles();
        updateActionButtons();
        if (isComplete()) {
            hideHandCoachingAnimation();
        } else {
            showHandCoachingAnimation();
        }
    }

    private void updateTitles() {
        updateTitleView(this.mTitleTextView, getTitleStringId(), R.style.TextAppearance_GestureTutorial_Title);
        updateTitleView(this.mSubtitleTextView, getSubtitleStringId(), R.style.TextAppearance_GestureTutorial_Subtitle);
    }

    private void updateTitleView(TextView textView, Integer stringId, int styleId) {
        if (stringId == null) {
            textView.setVisibility(8);
            return;
        }
        textView.setVisibility(0);
        textView.setText(stringId.intValue());
        textView.setTextAppearance(styleId);
    }

    private void updateActionButtons() {
        updateButton(this.mActionButton, getActionButtonStringId(), new View.OnClickListener() { // from class: com.android.quickstep.interaction.-$$Lambda$XglmDr1aJYp_KoxXXao296nsRek
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.onActionButtonClicked(view);
            }
        });
        updateButton(this.mActionTextButton, getActionTextButtonStringId(), new View.OnClickListener() { // from class: com.android.quickstep.interaction.-$$Lambda$zRp70sDjcSWJsQWtip7sX0S9C6Q
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.onActionTextButtonClicked(view);
            }
        });
    }

    private void updateButton(Button button, Integer stringId, View.OnClickListener listener) {
        if (stringId == null) {
            button.setVisibility(4);
            return;
        }
        button.setVisibility(0);
        button.setText(stringId.intValue());
        button.setOnClickListener(listener);
    }

    private boolean isComplete() {
        return this.mTutorialType == TutorialType.BACK_NAVIGATION_COMPLETE || this.mTutorialType == TutorialType.HOME_NAVIGATION_COMPLETE || this.mTutorialType == TutorialType.OVERVIEW_NAVIGATION_COMPLETE || this.mTutorialType == TutorialType.ASSISTANT_COMPLETE;
    }
}
