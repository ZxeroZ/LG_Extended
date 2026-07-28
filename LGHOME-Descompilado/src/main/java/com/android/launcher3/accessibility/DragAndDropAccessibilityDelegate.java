package com.android.launcher3.accessibility;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import com.android.launcher3.CellLayout;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.lge.launcher3.R;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class DragAndDropAccessibilityDelegate extends ExploreByTouchHelper implements View.OnClickListener {
    protected static final int INVALID_POSITION = -1;
    private static final int[] sTempArray = new int[2];
    protected final Context mContext;
    protected final LauncherAccessibilityDelegate mDelegate;
    private final Rect mTempRect;
    protected final CellLayout mView;

    protected abstract String getConfirmationForIconDrop(int id);

    protected abstract String getLocationDescriptionForIconDrop(int id);

    protected abstract int intersectsValidDropTarget(int id);

    public DragAndDropAccessibilityDelegate(CellLayout forView) {
        super(forView);
        this.mTempRect = new Rect();
        this.mView = forView;
        Context context = forView.getContext();
        this.mContext = context;
        this.mDelegate = LauncherAppState.getInstance(context).getAccessibilityDelegate();
    }

    @Override // androidx.customview.widget.ExploreByTouchHelper
    protected int getVirtualViewAt(float x, float y) {
        if (x < 0.0f || y < 0.0f || x > this.mView.getMeasuredWidth() || y > this.mView.getMeasuredHeight()) {
            return Integer.MIN_VALUE;
        }
        int[] iArr = sTempArray;
        this.mView.pointToCellExact((int) x, (int) y, iArr);
        return intersectsValidDropTarget(iArr[0] + (iArr[1] * this.mView.getCountX()));
    }

    @Override // androidx.customview.widget.ExploreByTouchHelper
    protected void getVisibleVirtualViews(List<Integer> virtualViews) {
        int countX = this.mView.getCountX() * this.mView.getCountY();
        for (int i = 0; i < countX; i++) {
            if (intersectsValidDropTarget(i) == i) {
                virtualViews.add(Integer.valueOf(i));
            }
        }
    }

    @Override // androidx.customview.widget.ExploreByTouchHelper
    protected boolean onPerformActionForVirtualView(int viewId, int action, Bundle args) {
        if (action != 16 || viewId == Integer.MIN_VALUE) {
            return false;
        }
        this.mDelegate.handleAccessibleDrop(this.mView, getItemBounds(viewId), getConfirmationForIconDrop(viewId));
        return true;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        onPerformActionForVirtualView(getFocusedVirtualView(), 16, null);
    }

    @Override // androidx.customview.widget.ExploreByTouchHelper
    protected void onPopulateEventForVirtualView(int id, AccessibilityEvent event) {
        if (id == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Invalid virtual view id");
        }
        event.setContentDescription(this.mContext.getString(R.string.action_move_here));
    }

    @Override // androidx.customview.widget.ExploreByTouchHelper
    protected void onPopulateNodeForVirtualView(int id, AccessibilityNodeInfoCompat node) {
        if (id == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Invalid virtual view id");
        }
        node.setContentDescription(getLocationDescriptionForIconDrop(id));
        node.setBoundsInParent(getItemBounds(id));
        node.addAction(16);
        node.setClickable(true);
        node.setFocusable(true);
    }

    private Rect getItemBounds(int id) {
        int countX = id % this.mView.getCountX();
        int countX2 = id / this.mView.getCountX();
        LauncherAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        this.mView.cellToRect(countX, countX2, dragInfo.info.spanX, dragInfo.info.spanY, this.mTempRect);
        return this.mTempRect;
    }
}
