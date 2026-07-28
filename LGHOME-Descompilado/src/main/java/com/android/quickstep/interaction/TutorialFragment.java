package com.android.quickstep.interaction;

import android.content.Intent;
import android.graphics.Insets;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.quickstep.interaction.TutorialController;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
abstract class TutorialFragment extends Fragment implements View.OnTouchListener {
    static final String KEY_TUTORIAL_TYPE = "tutorial_type";
    private static final String LOG_TAG = "TutorialFragment";
    EdgeBackGestureHandler mEdgeBackGestureHandler;
    TutorialHandAnimation mHandCoachingAnimation;
    NavBarGestureHandler mNavBarGestureHandler;
    View mRootView;
    TutorialController mTutorialController = null;
    TutorialController.TutorialType mTutorialType;

    abstract TutorialController createController(TutorialController.TutorialType type);

    abstract Class<? extends TutorialController> getControllerClass();

    abstract int getHandAnimationResId();

    TutorialFragment() {
    }

    public static TutorialFragment newInstance(TutorialController.TutorialType tutorialType) {
        TutorialFragment fragmentForTutorialType = getFragmentForTutorialType(tutorialType);
        if (fragmentForTutorialType == null) {
            fragmentForTutorialType = new BackGestureTutorialFragment();
            tutorialType = TutorialController.TutorialType.RIGHT_EDGE_BACK_NAVIGATION;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TUTORIAL_TYPE, tutorialType);
        fragmentForTutorialType.setArguments(bundle);
        return fragmentForTutorialType;
    }

    /* JADX INFO: renamed from: com.android.quickstep.interaction.TutorialFragment$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType;

        static {
            int[] iArr = new int[TutorialController.TutorialType.values().length];
            $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType = iArr;
            try {
                iArr[TutorialController.TutorialType.RIGHT_EDGE_BACK_NAVIGATION.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.LEFT_EDGE_BACK_NAVIGATION.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.BACK_NAVIGATION_COMPLETE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.HOME_NAVIGATION.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.HOME_NAVIGATION_COMPLETE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.OVERVIEW_NAVIGATION.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.OVERVIEW_NAVIGATION_COMPLETE.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.ASSISTANT.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[TutorialController.TutorialType.ASSISTANT_COMPLETE.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }

    private static TutorialFragment getFragmentForTutorialType(TutorialController.TutorialType tutorialType) {
        switch (AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[tutorialType.ordinal()]) {
            case 1:
            case 2:
            case 3:
                return new BackGestureTutorialFragment();
            case 4:
            case 5:
                return new HomeGestureTutorialFragment();
            case 6:
            case 7:
                return new OverviewGestureTutorialFragment();
            case 8:
            case 9:
                return new AssistantGestureTutorialFragment();
            default:
                Log.e(LOG_TAG, "Failed to find an appropriate fragment for " + tutorialType.name());
                return null;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }
        this.mTutorialType = (TutorialController.TutorialType) savedInstanceState.getSerializable(KEY_TUTORIAL_TYPE);
        this.mEdgeBackGestureHandler = new EdgeBackGestureHandler(getContext());
        this.mNavBarGestureHandler = new NavBarGestureHandler(getContext());
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mEdgeBackGestureHandler.unregisterBackGestureAttemptCallback();
        this.mNavBarGestureHandler.unregisterNavBarGestureAttemptCallback();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View viewInflate = inflater.inflate(R.layout.gesture_tutorial_fragment, container, false);
        this.mRootView = viewInflate;
        viewInflate.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: com.android.quickstep.interaction.-$$Lambda$TutorialFragment$2KypMkJkngd0jOu1tDum7DsrFpc
            @Override // android.view.View.OnApplyWindowInsetsListener
            public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                return this.f$0.lambda$onCreateView$0$TutorialFragment(view, windowInsets);
            }
        });
        this.mRootView.setOnTouchListener(this);
        this.mHandCoachingAnimation = new TutorialHandAnimation(getContext(), this.mRootView, getHandAnimationResId());
        return this.mRootView;
    }

    public /* synthetic */ WindowInsets lambda$onCreateView$0$TutorialFragment(View view, WindowInsets windowInsets) {
        Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars());
        this.mEdgeBackGestureHandler.setInsets(insets.left, insets.right);
        return windowInsets;
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        changeController(this.mTutorialType);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mHandCoachingAnimation.stop();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.mNavBarGestureHandler.onTouch(view, motionEvent) | this.mEdgeBackGestureHandler.onTouch(view, motionEvent);
    }

    void onAttachedToWindow() {
        this.mEdgeBackGestureHandler.setViewGroupParent((ViewGroup) getRootView());
    }

    void onDetachedFromWindow() {
        this.mEdgeBackGestureHandler.setViewGroupParent(null);
    }

    void changeController(TutorialController.TutorialType tutorialType) {
        if (getControllerClass().isInstance(this.mTutorialController)) {
            this.mTutorialController.setTutorialType(tutorialType);
        } else {
            this.mTutorialController = createController(tutorialType);
        }
        this.mTutorialController.transitToController();
        this.mEdgeBackGestureHandler.registerBackGestureAttemptCallback(this.mTutorialController);
        this.mNavBarGestureHandler.registerNavBarGestureAttemptCallback(this.mTutorialController);
        this.mTutorialType = tutorialType;
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(KEY_TUTORIAL_TYPE, this.mTutorialType);
        super.onSaveInstanceState(savedInstanceState);
    }

    View getRootView() {
        return this.mRootView;
    }

    TutorialHandAnimation getHandAnimation() {
        return this.mHandCoachingAnimation;
    }

    void closeTutorial() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    void startSystemNavigationSetting() {
        startActivity(new Intent("com.android.settings.GESTURE_NAVIGATION_SETTINGS"));
    }
}
