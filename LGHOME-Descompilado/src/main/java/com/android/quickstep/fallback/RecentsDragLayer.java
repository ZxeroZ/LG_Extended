package com.android.quickstep.fallback;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.RecentsActivity;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class RecentsDragLayer extends BaseDragLayer<RecentsActivity> {
    public RecentsDragLayer(Context context, AttributeSet attrs) {
        super(context, attrs, 1);
    }

    @Override // com.android.launcher3.views.BaseDragLayer
    public void recreateControllers() {
        this.mControllers = new TouchController[]{new RecentsTaskController((RecentsActivity) this.mActivity), new FallbackNavBarTouchController((RecentsActivity) this.mActivity)};
    }

    @Override // com.android.launcher3.InsettableFrameLayout, com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        super.setInsets(insets);
        setBackground((insets.top == 0 || !this.mAllowSysuiScrims) ? null : Themes.getAttrDrawable(getContext(), R.attr.workspaceStatusBarScrim));
    }
}
