package com.android.launcher3.accessibility;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class OverviewScreenAccessibilityDelegate extends View.AccessibilityDelegate {
    protected static final int MOVE_BACKWARD = 2131296326;
    protected static final int MOVE_FORWARD = 2131296327;
    private final int DELAY_TIME;
    private boolean isAccessibilityAction;
    private final SparseArray<AccessibilityNodeInfo.AccessibilityAction> mActions;
    private Handler mHandler;
    private Runnable mResetAccessibilityAction;
    protected final Workspace mWorkspace;

    public OverviewScreenAccessibilityDelegate(Workspace workspace) {
        SparseArray<AccessibilityNodeInfo.AccessibilityAction> sparseArray = new SparseArray<>();
        this.mActions = sparseArray;
        this.DELAY_TIME = 200;
        this.isAccessibilityAction = false;
        this.mHandler = new Handler();
        this.mResetAccessibilityAction = new Runnable() { // from class: com.android.launcher3.accessibility.OverviewScreenAccessibilityDelegate.1
            @Override // java.lang.Runnable
            public void run() {
                OverviewScreenAccessibilityDelegate.this.isAccessibilityAction = false;
            }
        };
        this.mWorkspace = workspace;
        Context context = workspace.getContext();
        boolean zIsRtl = Utilities.isRtl(context.getResources());
        int i = R.string.action_move_screen_right;
        sparseArray.put(R.id.action_move_screen_backwards, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_screen_backwards, context.getText(zIsRtl ? R.string.action_move_screen_right : R.string.action_move_screen_left)));
        sparseArray.put(R.id.action_move_screen_forwards, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_screen_forwards, context.getText(zIsRtl ? R.string.action_move_screen_left : i)));
    }

    @Override // android.view.View.AccessibilityDelegate
    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        aroundPerformAccessibilityAction(host, action, args);
        return true;
    }

    private void movePage(int finalIndex, View view) {
        this.mWorkspace.onStartReordering();
        this.mWorkspace.removeView(view);
        this.mWorkspace.addView(view, finalIndex);
        this.mWorkspace.onEndReordering();
        Workspace workspace = this.mWorkspace;
        workspace.announceForAccessibility(workspace.getContext().getText(R.string.screen_moved));
        this.mWorkspace.updateAccessibilityFlags();
        view.performAccessibilityAction(64, null);
    }

    @Override // android.view.View.AccessibilityDelegate
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        int iIndexOfChild = this.mWorkspace.indexOfChild(host);
        if (iIndexOfChild < this.mWorkspace.getChildCount() - 1) {
            info.addAction(this.mActions.get(R.id.action_move_screen_forwards));
        }
        if (iIndexOfChild > this.mWorkspace.numCustomPages()) {
            info.addAction(this.mActions.get(R.id.action_move_screen_backwards));
        }
    }

    public boolean aroundPerformAccessibilityAction(View host, int action, Bundle args) {
        if (host != null) {
            if (action == 64) {
                if (!this.isAccessibilityAction && !this.mWorkspace.getIsDragOccuring()) {
                    this.isAccessibilityAction = true;
                    this.mWorkspace.setCurrentPage(this.mWorkspace.indexOfChild(host));
                    this.mHandler.postDelayed(this.mResetAccessibilityAction, 200L);
                    return super.performAccessibilityAction(host, action, args);
                }
            } else {
                if (action == R.id.action_move_screen_forwards) {
                    movePage(this.mWorkspace.indexOfChild(host) + 1, host);
                    return true;
                }
                if (action == R.id.action_move_screen_backwards) {
                    movePage(this.mWorkspace.indexOfChild(host) - 1, host);
                }
            }
        }
        return true;
    }
}
