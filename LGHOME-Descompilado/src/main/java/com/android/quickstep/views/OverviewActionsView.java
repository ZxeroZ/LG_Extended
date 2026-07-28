package com.android.quickstep.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.launcher3.Insettable;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.TaskOverlayFactory.OverlayUICallbacks;
import com.android.quickstep.util.LayoutUtils;
import com.lge.launcher3.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public class OverviewActionsView<T extends TaskOverlayFactory.OverlayUICallbacks> extends FrameLayout implements View.OnClickListener, Insettable {
    public static final int DISABLED_ROTATED = 2;
    public static final int DISABLED_SCROLLING = 1;
    public static final int HIDDEN_DISABLED_FEATURE = 2;
    public static final int HIDDEN_GESTURE_RUNNING = 16;
    public static final int HIDDEN_NON_ZERO_ROTATION = 4;
    public static final int HIDDEN_NO_RECENTS = 32;
    public static final int HIDDEN_NO_TASKS = 8;
    public static final int HIDDEN_UNSUPPORTED_NAVIGATION = 1;
    private static final int INDEX_CONTENT_ALPHA = 0;
    private static final int INDEX_FULLSCREEN_ALPHA = 2;
    private static final int INDEX_HIDDEN_FLAGS_ALPHA = 3;
    private static final int INDEX_VISIBILITY_ALPHA = 1;
    protected T mCallbacks;
    protected int mDisabledFlags;
    private int mHiddenFlags;
    private final Rect mInsets;
    private final MultiValueAlpha mMultiValueAlpha;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionsDisabledFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionsHiddenFlags {
    }

    public OverviewActionsView(Context context) {
        this(context, null);
    }

    public OverviewActionsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverviewActionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        this.mInsets = new Rect();
        this.mMultiValueAlpha = new MultiValueAlpha(this, 4);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        View viewFindViewById = findViewById(R.id.action_share);
        viewFindViewById.setOnClickListener(this);
        findViewById(R.id.action_screenshot).setOnClickListener(this);
        if (FeatureFlags.ENABLE_OVERVIEW_SHARE.get()) {
            viewFindViewById.setVisibility(0);
            findViewById(R.id.share_space).setVisibility(0);
        }
    }

    public void setCallbacks(T callbacks) {
        this.mCallbacks = callbacks;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mCallbacks == null) {
            return;
        }
        int id = view.getId();
        if (id == R.id.action_share) {
            this.mCallbacks.onShare();
        } else if (id == R.id.action_screenshot) {
            this.mCallbacks.onScreenshot();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateHiddenFlags(2, !FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get());
        updateHiddenFlags(1, !SysUINavigationMode.removeShelfFromOverview(getContext()));
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateVerticalMargin(SysUINavigationMode.getMode(getContext()));
    }

    @Override // com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        this.mInsets.set(insets);
        updateVerticalMargin(SysUINavigationMode.getMode(getContext()));
    }

    public void updateHiddenFlags(int visibilityFlags, boolean enable) {
        if (enable) {
            this.mHiddenFlags = visibilityFlags | this.mHiddenFlags;
        } else {
            this.mHiddenFlags = (~visibilityFlags) & this.mHiddenFlags;
        }
        boolean z = this.mHiddenFlags != 0;
        this.mMultiValueAlpha.getProperty(3).setValue(z ? 0.0f : 1.0f);
        setVisibility(z ? 4 : 0);
    }

    public void updateDisabledFlags(int disabledFlags, boolean enable) {
        if (enable) {
            this.mDisabledFlags = disabledFlags | this.mDisabledFlags;
        } else {
            this.mDisabledFlags = (~disabledFlags) & this.mDisabledFlags;
        }
        LayoutUtils.setViewEnabled(this, (this.mDisabledFlags & (-3)) == 0);
    }

    public MultiValueAlpha.AlphaProperty getContentAlpha() {
        return this.mMultiValueAlpha.getProperty(0);
    }

    public MultiValueAlpha.AlphaProperty getVisibilityAlpha() {
        return this.mMultiValueAlpha.getProperty(1);
    }

    public MultiValueAlpha.AlphaProperty getFullscreenAlpha() {
        return this.mMultiValueAlpha.getProperty(2);
    }

    public void updateVerticalMargin(SysUINavigationMode.Mode mode) {
        int dimensionPixelSize;
        if (getResources().getConfiguration().orientation == 2) {
            dimensionPixelSize = 0;
        } else if (mode == SysUINavigationMode.Mode.THREE_BUTTONS) {
            dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.overview_actions_bottom_margin_three_button);
        } else {
            dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.overview_actions_bottom_margin_gesture);
        }
        int i = dimensionPixelSize + this.mInsets.bottom;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, i);
    }
}
