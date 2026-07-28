package com.lge.launcher3.wallpaperblur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;

/* JADX INFO: loaded from: classes.dex */
public class WidgetBlurLayout extends FrameLayout {
    public static final boolean DEBUG = false;
    public static final String TAG = "WidgetBlurLayout";
    private WidgetBlurView mBlurView;
    private View mColorView;

    public WidgetBlurLayout(Context context) {
        super(context);
        this.mBlurView = null;
        this.mColorView = null;
        if (WidgetBlurManager.getInstance(context).isDisabled()) {
            return;
        }
        addBlurView();
        addColorView();
    }

    public void addBlurView() {
        if (this.mBlurView != null) {
            return;
        }
        WidgetBlurView widgetBlurView = new WidgetBlurView(this.mContext);
        this.mBlurView = widgetBlurView;
        addView(widgetBlurView);
    }

    public void removeBlurView() {
        WidgetBlurView widgetBlurView = this.mBlurView;
        if (widgetBlurView == null) {
            return;
        }
        removeView(widgetBlurView);
        this.mBlurView.destroy();
        this.mBlurView = null;
    }

    public void addColorView() {
        if (this.mColorView != null) {
            return;
        }
        this.mColorView = new View(this.mContext);
        updateColorView(WallpaperBlurredImageController.getInstance(this.mContext).getCommonColor());
        addView(this.mColorView);
    }

    public void removeColorView() {
        View view = this.mColorView;
        if (view == null) {
            return;
        }
        removeView(view);
        this.mColorView = null;
    }

    public void enableBlurView(boolean enable) {
        enableBlurView(enable, true);
    }

    public void enableBlurView(boolean enable, boolean animate) {
        WidgetBlurView widgetBlurView = this.mBlurView;
        if (widgetBlurView == null) {
            return;
        }
        widgetBlurView.enable(enable, animate);
        invalidate();
    }

    public void updateBlurView() {
        WidgetBlurView widgetBlurView = this.mBlurView;
        if (widgetBlurView == null) {
            return;
        }
        widgetBlurView.updateBlurredImage();
        invalidate();
    }

    public void enableColorView(boolean enable) {
        View view = this.mColorView;
        if (view == null) {
            return;
        }
        view.setVisibility(enable ? 0 : 4);
    }

    public void updateColorView(int color) {
        View view = this.mColorView;
        if (view == null) {
            return;
        }
        view.setBackgroundColor(color);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        WidgetBlurView widgetBlurView = this.mBlurView;
        if (widgetBlurView != null && widgetBlurView.shouldUpdateBlurredImage()) {
            updateBlurredImage();
        }
        super.dispatchDraw(canvas);
    }

    private boolean updateBlurredImage() {
        if (this.mBlurView == null) {
            return false;
        }
        Bitmap blurredImage = getBlurredImage(WallpaperBlurredImageController.getInstance(this.mContext));
        this.mBlurView.setBlurredImage(blurredImage);
        return blurredImage != null;
    }

    protected Bitmap getBlurredImage(WallpaperBlurredImageController wallpaperBlurredImageController) {
        return wallpaperBlurredImageController.getBlurredImageForChildOfWorkspace(this);
    }
}
