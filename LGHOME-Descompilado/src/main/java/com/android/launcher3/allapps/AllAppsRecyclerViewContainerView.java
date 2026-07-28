package com.android.launcher3.allapps;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.ClickShadowView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsRecyclerViewContainerView extends FrameLayout implements BubbleTextView.BubbleTextShadowHandler {
    private final ClickShadowView mTouchFeedbackView;

    public AllAppsRecyclerViewContainerView(Context context) {
        this(context, null);
    }

    public AllAppsRecyclerViewContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsRecyclerViewContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DeviceProfile deviceProfile = ((Launcher) context).getDeviceProfile();
        ClickShadowView clickShadowView = new ClickShadowView(context);
        this.mTouchFeedbackView = clickShadowView;
        int extraSize = deviceProfile.allAppsIconSizePx + clickShadowView.getExtraSize();
        addView(clickShadowView, extraSize, extraSize);
    }

    @Override // com.android.launcher3.BubbleTextView.BubbleTextShadowHandler
    public void setPressedIcon(BubbleTextView icon, Bitmap background) {
        if (icon == null || background == null) {
            this.mTouchFeedbackView.setBitmap(null);
            this.mTouchFeedbackView.animate().cancel();
        } else if (this.mTouchFeedbackView.setBitmap(background)) {
            this.mTouchFeedbackView.alignWithIconView(icon, (ViewGroup) icon.getParent());
            this.mTouchFeedbackView.animateShadow();
        }
    }
}
