package com.android.launcher3.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.statemanager.StateManager;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsPagedView;

/* JADX INFO: loaded from: classes.dex */
public class WorkEduView extends AbstractSlideInView implements Insettable, StateManager.StateListener<LauncherState> {
    private static final int DEFAULT_CLOSE_DURATION = 200;
    protected static final int FINAL_SCRIM_BG_COLOR = -2013265920;
    public static final String KEY_LEGACY_WORK_EDU_SEEN = "showed_bottom_user_education";
    public static final String KEY_WORK_EDU_STEP = "showed_work_profile_edu";
    private static final int WORK_EDU_NOT_STARTED = 0;
    private static final int WORK_EDU_PERSONAL_APPS = 1;
    private static final int WORK_EDU_WORK_APPS = 2;
    private AllAppsPagedView mAllAppsPagedView;
    private TextView mContentText;
    private Rect mInsets;
    private int mNextWorkEduStep;
    private Button mProceedButton;
    private View mViewWrapper;

    @Override // com.android.launcher3.AbstractFloatingView
    public int getLogContainerType() {
        return 14;
    }

    @Override // com.android.launcher3.views.AbstractSlideInView
    protected int getScrimColor(Context context) {
        return FINAL_SCRIM_BG_COLOR;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 32) != 0;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
    }

    public WorkEduView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public WorkEduView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInsets = new Rect();
        this.mNextWorkEduStep = 1;
        this.mContent = this;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        this.mLauncher.getSharedPrefs().edit().putInt(KEY_WORK_EDU_STEP, this.mNextWorkEduStep).apply();
        handleClose(true, 200L);
    }

    @Override // com.android.launcher3.views.AbstractSlideInView
    protected void onCloseComplete() {
        super.onCloseComplete();
        this.mLauncher.getStateManager().removeStateListener(this);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mViewWrapper = findViewById(R.id.view_wrapper);
        this.mProceedButton = (Button) findViewById(R.id.proceed);
        TextView textView = (TextView) findViewById(R.id.content_text);
        this.mContentText = textView;
        textView.post(new Runnable() { // from class: com.android.launcher3.views.-$$Lambda$WorkEduView$nn8qT2yxJXho_KdeZYySe76LaKw
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onFinishInflate$0$WorkEduView();
            }
        });
        if (this.mLauncher.getAllAppsHost().getLGAllAppsPagedView() instanceof AllAppsPagedView) {
            this.mAllAppsPagedView = this.mLauncher.getAllAppsHost().getLGAllAppsPagedView();
        }
        this.mProceedButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.views.-$$Lambda$WorkEduView$ZOEaQQUFqW0BroPENJtjRTqM7ec
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onFinishInflate$1$WorkEduView(view);
            }
        });
    }

    public /* synthetic */ void lambda$onFinishInflate$0$WorkEduView() {
        TextView textView = this.mContentText;
        textView.setMinLines(textView.getLineCount());
    }

    public /* synthetic */ void lambda$onFinishInflate$1$WorkEduView(View view) {
        AllAppsPagedView allAppsPagedView = this.mAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.snapToPage(allAppsPagedView.getManagedProfileStartPage());
        }
        goToWorkTab(true);
    }

    private void goToWorkTab(boolean animate) {
        this.mProceedButton.setText(this.mLauncher.getStringCache().workProfileEduAccept);
        final String str = this.mLauncher.getStringCache().workProfileEduWorkAllapps;
        if (animate) {
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this.mContentText, (Property<TextView, Float>) ALPHA, 0.0f);
            objectAnimatorOfFloat.addListener(new AnimationSuccessListener() { // from class: com.android.launcher3.views.WorkEduView.1
                @Override // com.android.launcher3.anim.AnimationSuccessListener
                public void onAnimationSuccess(Animator animator) {
                    WorkEduView.this.mContentText.setText(str);
                    ObjectAnimator.ofFloat(WorkEduView.this.mContentText, (Property<TextView, Float>) View.ALPHA, 1.0f).start();
                }
            });
            objectAnimatorOfFloat.start();
        } else {
            this.mContentText.setText(str);
        }
        this.mNextWorkEduStep = 2;
        this.mProceedButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.views.-$$Lambda$WorkEduView$Zr1BzLvxjUmamWv6ZR-AqNraDo8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$goToWorkTab$2$WorkEduView(view);
            }
        });
    }

    public /* synthetic */ void lambda$goToWorkTab$2$WorkEduView(View view) {
        handleClose(true);
    }

    @Override // com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        int i = insets.left - this.mInsets.left;
        int i2 = insets.right - this.mInsets.right;
        int i3 = insets.bottom - this.mInsets.bottom;
        this.mInsets.set(insets);
        setPadding(i, getPaddingTop(), i2, 0);
        View view = this.mViewWrapper;
        view.setPaddingRelative(view.getPaddingStart(), this.mViewWrapper.getPaddingTop(), this.mViewWrapper.getPaddingEnd(), i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show() {
        attachToContainer();
        animateOpen();
        this.mLauncher.getStateManager().addStateListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goToFirstPage() {
        AllAppsPagedView allAppsPagedView = this.mAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.snapToPageImmediately(0);
        }
    }

    private void animateOpen() {
        if (this.mIsOpen || this.mOpenCloseAnimator.isRunning()) {
            return;
        }
        this.mIsOpen = true;
        this.mOpenCloseAnimator.setValues(PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, 0.0f));
        this.mOpenCloseAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        this.mOpenCloseAnimator.start();
    }

    public static StateManager.StateListener<LauncherState> showEduFlowIfNeeded(final Launcher launcher, StateManager.StateListener<LauncherState> oldListener) {
        if (oldListener != null) {
            launcher.getStateManager().removeStateListener(oldListener);
        }
        if (hasSeenLegacyEdu(launcher) || launcher.getSharedPrefs().getInt(KEY_WORK_EDU_STEP, 0) != 0) {
            return null;
        }
        StateManager.StateListener<LauncherState> stateListener = new StateManager.StateListener<LauncherState>() { // from class: com.android.launcher3.views.WorkEduView.2
            /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
            @Override // com.android.launcher3.statemanager.StateManager.StateListener
            public void onStateTransitionComplete(LauncherState finalState) {
                if (finalState != LauncherState.ALL_APPS) {
                    return;
                }
                WorkEduView workEduView = (WorkEduView) LayoutInflater.from(launcher).inflate(R.layout.work_profile_edu, (ViewGroup) launcher.getDragLayer(), false);
                workEduView.show();
                workEduView.goToFirstPage();
                launcher.getStateManager().removeStateListener(this);
            }
        };
        launcher.getStateManager().addStateListener(stateListener);
        return stateListener;
    }

    public static void showWorkEduIfNeeded(Launcher launcher) {
        if (hasSeenLegacyEdu(launcher) || launcher.getSharedPrefs().getInt(KEY_WORK_EDU_STEP, 0) != 0) {
            return;
        }
        if (AbstractFloatingView.getTopOpenView(launcher) != null) {
            AbstractFloatingView.closeOpenViews(launcher, false, 32);
        }
        WorkEduView workEduView = (WorkEduView) LayoutInflater.from(launcher).inflate(R.layout.work_profile_edu, (ViewGroup) launcher.getDragLayer(), false);
        workEduView.show();
        workEduView.goToWorkTab(false);
    }

    private static boolean hasSeenLegacyEdu(Launcher launcher) {
        return launcher.getSharedPrefs().getBoolean(KEY_LEGACY_WORK_EDU_SEEN, false);
    }

    /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateListener
    public void onStateTransitionComplete(LauncherState finalState) {
        close(false);
    }
}
