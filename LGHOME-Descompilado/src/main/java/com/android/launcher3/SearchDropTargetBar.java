package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import com.android.launcher3.dragndrop.DragController;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class SearchDropTargetBar extends FrameLayout implements DragController.DragListener {
    private static final int TRANSITION_DURATION = 200;
    private static final AccelerateInterpolator sAccelerateInterpolator = new AccelerateInterpolator();
    protected ButtonDropTarget mAllAppsDeleteDropTarget;
    protected ButtonDropTarget mAllAppsUninstallDropTarget;
    private boolean mDeferOnDragEnd;
    protected ButtonDropTarget mDeleteDropTarget;
    protected View mDropTargetBar;
    private ValueAnimator mHideSearchBarAnim;
    private boolean mIsSearchBarHidden;
    private View mQSBSearchBar;
    protected ObjectAnimator mShowDropTargetBarAnim;
    protected ButtonDropTarget mUninstallDropTarget;

    public SearchDropTargetBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchDropTargetBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDeferOnDragEnd = false;
    }

    public void setup(Launcher launcher, DragController dragController) {
        dragController.addDragListener(this);
        if (LGHomeFeature.isEnableDefaultHome() || (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && LGHomeFeature.Config.FEATURE_DISABLE_ALLAPPS.getValue() && LGHomeFeature.isDisableEasyHome())) {
            dragController.setFlingToDeleteDropTarget(this.mDeleteDropTarget);
            dragController.addDragListener(this.mDeleteDropTarget);
            dragController.addDragListener(this.mUninstallDropTarget);
            dragController.addDropTarget(this.mDeleteDropTarget);
            dragController.addDropTarget(this.mUninstallDropTarget);
            this.mDeleteDropTarget.setLauncher(launcher);
            this.mUninstallDropTarget.setLauncher(launcher);
            return;
        }
        dragController.setFlingToDeleteDropTarget(this.mAllAppsDeleteDropTarget);
        dragController.addDragListener(this.mAllAppsDeleteDropTarget);
        dragController.addDragListener(this.mAllAppsUninstallDropTarget);
        dragController.addDropTarget(this.mAllAppsDeleteDropTarget);
        dragController.addDropTarget(this.mAllAppsUninstallDropTarget);
        this.mAllAppsDeleteDropTarget.setLauncher(launcher);
        this.mAllAppsUninstallDropTarget.setLauncher(launcher);
    }

    public void setQsbSearchBar(View qsb) {
        this.mQSBSearchBar = qsb;
        if (qsb != null) {
            ObjectAnimator objectAnimatorOfFloat = LauncherAnimUtils.ofFloat(qsb, "alpha", 1.0f, 0.0f);
            this.mHideSearchBarAnim = objectAnimatorOfFloat;
            setupAnimation(objectAnimatorOfFloat, this.mQSBSearchBar);
        } else {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 0.0f);
            this.mHideSearchBarAnim = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setDuration(200L);
        }
    }

    private void prepareStartAnimation(View v) {
        if (v != null) {
            v.setLayerType(2, null);
        }
    }

    protected void setupAnimation(ValueAnimator anim, final View v) {
        anim.setInterpolator(sAccelerateInterpolator);
        anim.setDuration(200L);
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.SearchDropTargetBar.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                View view = v;
                if (view != null) {
                    view.setLayerType(0, null);
                }
            }
        });
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        View viewFindViewById = findViewById(R.id.drag_target_bar);
        this.mDropTargetBar = viewFindViewById;
        this.mDeleteDropTarget = (ButtonDropTarget) viewFindViewById.findViewById(R.id.delete_target_text);
        this.mUninstallDropTarget = (ButtonDropTarget) this.mDropTargetBar.findViewById(R.id.uninstall_target_text);
        this.mAllAppsDeleteDropTarget = (ButtonDropTarget) this.mDropTargetBar.findViewById(R.id.all_apps_delete_target_text);
        this.mAllAppsUninstallDropTarget = (ButtonDropTarget) this.mDropTargetBar.findViewById(R.id.all_apps_uninstall_target_text);
        this.mDeleteDropTarget.setSearchDropTargetBar(this);
        this.mUninstallDropTarget.setSearchDropTargetBar(this);
        this.mAllAppsDeleteDropTarget.setSearchDropTargetBar(this);
        this.mAllAppsUninstallDropTarget.setSearchDropTargetBar(this);
        if (LGHomeFeature.isEnableDefaultHome() || (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && LGHomeFeature.Config.FEATURE_DISABLE_ALLAPPS.getValue() && LGHomeFeature.isDisableEasyHome())) {
            this.mAllAppsDeleteDropTarget.setVisibility(8);
            this.mAllAppsUninstallDropTarget.setVisibility(8);
        } else {
            this.mDeleteDropTarget.setVisibility(8);
            this.mUninstallDropTarget.setVisibility(8);
        }
        this.mDropTargetBar.setAlpha(0.0f);
        ObjectAnimator objectAnimatorOfFloat = LauncherAnimUtils.ofFloat(this.mDropTargetBar, "alpha", 0.0f, 1.0f);
        this.mShowDropTargetBarAnim = objectAnimatorOfFloat;
        setupAnimation(objectAnimatorOfFloat, this.mDropTargetBar);
    }

    public void finishAnimations() {
        prepareStartAnimation(this.mDropTargetBar);
        this.mShowDropTargetBarAnim.reverse();
        prepareStartAnimation(this.mQSBSearchBar);
        this.mHideSearchBarAnim.reverse();
    }

    public void showSearchBar(boolean animated) {
        if (this.mIsSearchBarHidden) {
            if (animated) {
                prepareStartAnimation(this.mQSBSearchBar);
                this.mHideSearchBarAnim.reverse();
            } else {
                this.mHideSearchBarAnim.cancel();
                View view = this.mQSBSearchBar;
                if (view != null) {
                    view.setAlpha(1.0f);
                }
            }
            this.mIsSearchBarHidden = false;
        }
    }

    public void hideSearchBar(boolean animated) {
        if (this.mIsSearchBarHidden) {
            return;
        }
        if (animated) {
            prepareStartAnimation(this.mQSBSearchBar);
            this.mHideSearchBarAnim.start();
        } else {
            this.mHideSearchBarAnim.cancel();
            View view = this.mQSBSearchBar;
            if (view != null) {
                view.setAlpha(0.0f);
            }
        }
        this.mIsSearchBarHidden = true;
    }

    public void showDeleteTarget() {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            LGLog.i("SearchDropTargetBar", "showDeleteTarget() Doesn't shows the drop target bar on swivel mode");
            return;
        }
        prepareStartAnimation(this.mDropTargetBar);
        this.mShowDropTargetBarAnim.start();
        hideSearchBar(true);
    }

    public void hideDeleteTarget() {
        prepareStartAnimation(this.mDropTargetBar);
        this.mShowDropTargetBarAnim.reverse();
        showSearchBar(true);
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragStart(DragSource source, Object info, int dragAction) {
        showDeleteTarget();
    }

    public void deferOnDragEnd() {
        this.mDeferOnDragEnd = true;
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragEnd() {
        if (!this.mDeferOnDragEnd) {
            hideDeleteTarget();
        } else {
            this.mDeferOnDragEnd = false;
        }
    }

    public Rect getSearchBarBounds() {
        View view = this.mQSBSearchBar;
        if (view == null) {
            return null;
        }
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        Rect rect = new Rect();
        rect.left = iArr[0];
        rect.top = iArr[1];
        rect.right = iArr[0] + this.mQSBSearchBar.getWidth();
        rect.bottom = iArr[1] + this.mQSBSearchBar.getHeight();
        return rect;
    }

    public void enableAccessibleDrag(boolean enable) {
        View view = this.mQSBSearchBar;
        if (view != null) {
            view.setVisibility(enable ? 8 : 0);
        }
        if (LGHomeFeature.isEnableDefaultHome() || (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && LGHomeFeature.Config.FEATURE_DISABLE_ALLAPPS.getValue() && LGHomeFeature.isDisableEasyHome())) {
            this.mDeleteDropTarget.enableAccessibleDrag(enable);
            this.mUninstallDropTarget.enableAccessibleDrag(enable);
        } else {
            this.mAllAppsDeleteDropTarget.enableAccessibleDrag(enable);
            this.mAllAppsUninstallDropTarget.enableAccessibleDrag(enable);
        }
    }
}
