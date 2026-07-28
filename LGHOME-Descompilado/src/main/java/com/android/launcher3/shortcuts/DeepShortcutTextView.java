package com.android.launcher3.shortcuts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.launcher3.BubbleTextView;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierData;
import com.lge.launcher3.badge.appnotifier.AppNotifierDrawer;
import com.lge.launcher3.badge.appnotifier.IAppNotifierView;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class DeepShortcutTextView extends BubbleTextView {
    private final Rect mDragHandleBounds;
    private final int mDragHandleWidth;
    private boolean mShouldPerformClick;

    @Override // com.android.launcher3.BubbleTextView
    protected void applyCompoundDrawables(Drawable icon) {
    }

    public DeepShortcutTextView(Context context) {
        this(context, null, 0);
    }

    public DeepShortcutTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeepShortcutTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDragHandleBounds = new Rect();
        this.mShouldPerformClick = true;
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) {
            this.mDragHandleWidth = 0;
        } else {
            Resources resources = getResources();
            setDragHandle(resources);
            this.mDragHandleWidth = resources.getDimensionPixelSize(R.dimen.deep_shortcut_padding_end) + resources.getDimensionPixelSize(R.dimen.deep_shortcut_drag_handle_size) + (resources.getDimensionPixelSize(R.dimen.deep_shortcut_drawable_padding) / 2);
        }
        setMaxLines(R.integer.device_profile_iconTextMaxLines);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) {
            return;
        }
        this.mDragHandleBounds.set(0, 0, this.mDragHandleWidth, getMeasuredHeight());
        if (com.android.launcher3.Utilities.isRtl(getResources())) {
            return;
        }
        this.mDragHandleBounds.offset(getMeasuredWidth() - this.mDragHandleBounds.width(), 0);
    }

    @Override // com.android.launcher3.BubbleTextView, android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && !LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && !Utilities.isLGUI10_0()) {
            this.mShouldPerformClick = !this.mDragHandleBounds.contains((int) ev.getX(), (int) ev.getY());
        }
        return super.onTouchEvent(ev);
    }

    @Override // android.view.View
    public boolean performClick() {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) {
            return super.performClick();
        }
        return this.mShouldPerformClick && super.performClick();
    }

    @Override // com.lge.launcher3.badge.BadgeTextView, com.lge.launcher3.badge.appnotifier.IAppNotifierView
    public AppNotifierDrawer registerAppNotifier(IAppNotifierView view, AppNotifierData appData) {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) {
            return super.registerAppNotifier(view, appData);
        }
        return AppNotifierDrawer.NULL;
    }

    private void setDragHandle(Resources resources) {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) {
            return;
        }
        Drawable drawable = resources.getDrawable(R.drawable.appshortcut_pinning);
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.deep_shortcut_drag_handle_size);
        drawable.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
        drawable.setAlpha(40);
        setCompoundDrawablesRelative(null, null, drawable, null);
    }
}
