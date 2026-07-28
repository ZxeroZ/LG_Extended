package com.lge.launcher3.droptarget;

import android.content.Context;
import android.util.AttributeSet;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.Launcher;
import com.android.launcher3.SearchDropTargetBar;
import com.android.launcher3.dragndrop.DragController;
import com.lge.launcher3.R;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class LGSearchDropTargetBar extends SearchDropTargetBar {
    private ButtonDropTarget mAllAppsDisableDropTarget;
    private ButtonDropTarget mCancelDropTarget;
    private ButtonDropTarget mDisableDropTarget;

    public LGSearchDropTargetBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LGSearchDropTargetBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // com.android.launcher3.SearchDropTargetBar
    public void setup(Launcher launcher, DragController dragController) {
        if (launcher.isSafeMode()) {
            return;
        }
        super.setup(launcher, dragController);
        if (LGHomeFeature.isEnableDefaultHome() || (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && LGHomeFeature.Config.FEATURE_DISABLE_ALLAPPS.getValue() && LGHomeFeature.isDisableEasyHome())) {
            dragController.addDragListener(this.mDisableDropTarget);
            dragController.addDropTarget(this.mDisableDropTarget);
            this.mDisableDropTarget.setLauncher(launcher);
        } else {
            dragController.addDragListener(this.mAllAppsDisableDropTarget);
            dragController.addDropTarget(this.mAllAppsDisableDropTarget);
            this.mAllAppsDisableDropTarget.setLauncher(launcher);
        }
        dragController.addDragListener(this.mCancelDropTarget);
        dragController.addDropTarget(this.mCancelDropTarget);
        this.mCancelDropTarget.setLauncher(launcher);
    }

    @Override // com.android.launcher3.SearchDropTargetBar, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButtonDropTarget buttonDropTarget = (ButtonDropTarget) this.mDropTargetBar.findViewById(R.id.cancel_target_text);
        this.mCancelDropTarget = buttonDropTarget;
        buttonDropTarget.setSearchDropTargetBar(this);
        ButtonDropTarget buttonDropTarget2 = (ButtonDropTarget) this.mDropTargetBar.findViewById(R.id.disable_target_text);
        this.mDisableDropTarget = buttonDropTarget2;
        buttonDropTarget2.setSearchDropTargetBar(this);
        ButtonDropTarget buttonDropTarget3 = (ButtonDropTarget) this.mDropTargetBar.findViewById(R.id.all_apps_disable_target_text);
        this.mAllAppsDisableDropTarget = buttonDropTarget3;
        buttonDropTarget3.setSearchDropTargetBar(this);
        if (LGHomeFeature.isEnableDefaultHome() || (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && LGHomeFeature.Config.FEATURE_DISABLE_ALLAPPS.getValue() && LGHomeFeature.isDisableEasyHome())) {
            this.mAllAppsDisableDropTarget.setVisibility(8);
        } else {
            this.mDisableDropTarget.setVisibility(8);
        }
    }

    @Override // com.android.launcher3.SearchDropTargetBar, com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragStart(DragSource source, Object info, int dragAction) {
        setVisibility(0);
        if (UninstallModeManager.getInstance(this.mContext).isInUninstallMode()) {
            return;
        }
        super.onDragStart(source, info, dragAction);
    }

    @Override // com.android.launcher3.SearchDropTargetBar, com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragEnd() {
        setVisibility(4);
        if (!UninstallModeManager.getInstance(this.mContext).isInUninstallMode() || this.mDropTargetBar.getAlpha() > 0.0f) {
            super.onDragEnd();
        }
    }

    @Override // com.android.launcher3.SearchDropTargetBar
    public void enableAccessibleDrag(boolean enable) {
        super.enableAccessibleDrag(enable);
        this.mCancelDropTarget.enableAccessibleDrag(enable);
        if (LGHomeFeature.isEnableDefaultHome() || (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && LGHomeFeature.Config.FEATURE_DISABLE_ALLAPPS.getValue() && LGHomeFeature.isDisableEasyHome())) {
            this.mDisableDropTarget.enableAccessibleDrag(enable);
        } else {
            this.mAllAppsDisableDropTarget.enableAccessibleDrag(enable);
        }
    }
}
