package com.lge.launcher3;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.android.launcher3.Insettable;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.Workspace;
import com.lge.launcher3.screeneffect.WorkspaceStateTransitionWatcher;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class OverViewPanel extends LinearLayout implements WorkspaceStateTransitionWatcher.StateTransitionListener, Insettable {
    public static final String TAG = "OverViewPanel";
    boolean mOnWorkspaceTransition;
    Workspace.State mWorkspaceState;

    public OverViewPanel(Context context) {
        super(context, null);
        this.mWorkspaceState = Workspace.State.NORMAL;
        this.mOnWorkspaceTransition = false;
    }

    public OverViewPanel(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mWorkspaceState = Workspace.State.NORMAL;
        this.mOnWorkspaceTransition = false;
    }

    public OverViewPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        this.mWorkspaceState = Workspace.State.NORMAL;
        this.mOnWorkspaceTransition = false;
    }

    public OverViewPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mWorkspaceState = Workspace.State.NORMAL;
        this.mOnWorkspaceTransition = false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LGLog.d(TAG, "OverViewPanel : onAttachedToWindow");
        updateWatcher();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        WorkspaceStateTransitionWatcher.getInstance(getContext()).removeListener(this);
    }

    public void updateWatcher() {
        LGLog.d(TAG, "OverViewPanel : updateWatcher");
        Workspace workspace = ((Launcher) getContext()).getWorkspace();
        if (workspace != null) {
            this.mWorkspaceState = workspace.getState();
        }
        WorkspaceStateTransitionWatcher.getInstance(getContext()).addListener(this);
    }

    @Override // com.lge.launcher3.screeneffect.WorkspaceStateTransitionWatcher.StateTransitionListener
    public void onStateTransitionStart(Workspace.State fromState, Workspace.State toState) {
        this.mOnWorkspaceTransition = true;
    }

    @Override // com.lge.launcher3.screeneffect.WorkspaceStateTransitionWatcher.StateTransitionListener
    public void onStateTransitionEnd(Workspace.State fromState, Workspace.State toState) {
        this.mOnWorkspaceTransition = false;
        this.mWorkspaceState = toState;
        resetCellLayoutsBackgroundAlpha(toState);
    }

    private void resetCellLayoutsBackgroundAlpha(Workspace.State toState) {
        Workspace workspace;
        Context context = getContext();
        if (context == null || !(context instanceof Launcher) || (workspace = ((Launcher) getContext()).getWorkspace()) == null) {
            return;
        }
        workspace.setCellLayoutsBackgroundAlpha(toState);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.mWorkspaceState != Workspace.State.OVERVIEW || this.mOnWorkspaceTransition;
    }

    @Override // com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        InsettableFrameLayout.LayoutParams layoutParams = (InsettableFrameLayout.LayoutParams) getLayoutParams();
        boolean z = getResources().getConfiguration().orientation == 2;
        layoutParams.topMargin = LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() ? 0 : insets.top;
        layoutParams.leftMargin = insets.left;
        layoutParams.rightMargin = insets.right;
        layoutParams.bottomMargin = (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && z) ? 0 : insets.bottom;
    }
}
