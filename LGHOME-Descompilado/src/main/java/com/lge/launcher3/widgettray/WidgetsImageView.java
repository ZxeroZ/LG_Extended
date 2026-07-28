package com.lge.launcher3.widgettray;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import com.android.launcher3.widget.WidgetImageView;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.uninstall.UninstallBadgeUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsImageView extends WidgetImageView {
    private static final boolean DEBUG = false;
    private boolean mIsAvailableUninstall;
    private boolean mIsUninstallMode;
    private int mShadowDistance;
    private ImageView mUninstallBadge;
    private int mUninstallBadgePositionX;
    private UninstallBadgeUtils.UninstallType mUninstallType;

    public WidgetsImageView(Context context) {
        super(context);
        this.mUninstallType = null;
        this.mUninstallBadgePositionX = 0;
    }

    public WidgetsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mUninstallType = null;
        this.mUninstallBadgePositionX = 0;
    }

    public WidgetsImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mUninstallType = null;
        this.mUninstallBadgePositionX = 0;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        this.mShadowDistance = getContext().getResources().getDimensionPixelSize(R.dimen.widget_shadowDistance);
    }

    @Override // com.android.launcher3.widget.WidgetImageView
    protected void updateDstRectF() {
        float width;
        float height;
        float width2;
        if (getWidth() == 0) {
            LGLog.i("WidgetsImageView", "GroupWidgetItemAdapter does not use the widgetsImageView Rect ");
            this.mDstRectF.set(0.0f, 0.0f, this.mBitmap.getWidth(), this.mBitmap.getHeight());
            return;
        }
        if (this.mBitmap.getWidth() > this.mBitmap.getHeight()) {
            width = getWidth() - this.mShadowDistance;
            height = (width / this.mBitmap.getWidth()) * this.mBitmap.getHeight();
            if (this.mShadowDistance + height > getHeight()) {
                width2 = getHeight() / (this.mShadowDistance + height);
                width *= width2;
                height *= width2;
            }
        } else if (this.mBitmap.getWidth() < this.mBitmap.getHeight()) {
            height = getHeight() - this.mShadowDistance;
            width = (height / this.mBitmap.getHeight()) * this.mBitmap.getWidth();
            if (width > getWidth()) {
                width2 = getWidth() / width;
                width *= width2;
                height *= width2;
            }
        } else {
            width = this.mBitmap.getWidth();
            height = this.mBitmap.getHeight();
            if (this.mShadowDistance + width > getWidth()) {
                width = getWidth() - this.mShadowDistance;
                height = width;
            }
            if (this.mShadowDistance + height > getHeight()) {
                width = getHeight() - this.mShadowDistance;
                height = width;
            }
        }
        if (Utilities.isLGUI8_0()) {
            float width3 = (getWidth() - width) / 2.0f;
            float height2 = (getHeight() - height) / 2.0f;
            this.mDstRectF.set(width3, height2, width + width3, height + height2);
        } else if (com.android.launcher3.Utilities.isRtl(getResources())) {
            this.mDstRectF.set(getWidth() - width, 0.0f, getWidth(), height);
        } else {
            this.mDstRectF.set(0.0f, 0.0f, width, height);
        }
    }

    @Override // com.android.launcher3.widget.WidgetImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mBitmap != null) {
            updateDstRectF();
            ImageView imageView = this.mUninstallBadge;
            if (imageView != null) {
                imageView.setVisibility(4);
            }
            if (this.mIsUninstallMode) {
                if (this.mIsAvailableUninstall && this.mUninstallBadge != null) {
                    setUninstallBadgePositionX();
                    this.mUninstallBadge.setVisibility(0);
                    if (com.android.launcher3.Utilities.isRtl(getResources())) {
                        this.mUninstallBadge.startAnimation(getScaleAnimation());
                    }
                }
            } else {
                ImageView imageView2 = this.mUninstallBadge;
                if (imageView2 != null) {
                    imageView2.clearAnimation();
                }
            }
            canvas.drawBitmap(this.mBitmap, (Rect) null, this.mDstRectF, this.mPaint);
        }
    }

    public void setUninstallBadgePositionX() {
        if (Utilities.isLGUI8_0()) {
            return;
        }
        this.mUninstallBadgePositionX = (int) Math.max((getWidth() - this.mDstRectF.width()) - getContext().getResources().getDimension(R.dimen.widget_preview_padding), 0.0f);
        if (com.android.launcher3.Utilities.isRtl(getResources())) {
            this.mUninstallBadge.setPadding(this.mUninstallBadgePositionX, 0, 0, 0);
        } else {
            this.mUninstallBadge.setPadding(0, 0, this.mUninstallBadgePositionX, 0);
        }
    }

    public int getUninstallBadgePositionX() {
        return this.mUninstallBadgePositionX;
    }

    public boolean isAvailableUninstall() {
        return this.mIsAvailableUninstall;
    }

    public UninstallBadgeUtils.UninstallType getUninstallType() {
        return this.mUninstallType;
    }

    public void setUninstallBadge(ImageView badge, boolean isUninstallMode, boolean isAvailableUninstall, UninstallBadgeUtils.UninstallType uninstallType) {
        if (badge != null) {
            this.mUninstallBadge = badge;
            this.mIsUninstallMode = isUninstallMode;
            this.mIsAvailableUninstall = isAvailableUninstall;
            this.mUninstallType = uninstallType;
        }
    }

    private void drawShadow(Canvas canvas) {
        BitmapDrawable bitmapDrawable = new BitmapDrawable(this.mBitmap);
        Rect rect = new Rect();
        rect.set((int) this.mDstRectF.left, (int) this.mDstRectF.top, (int) this.mDstRectF.right, (int) this.mDstRectF.bottom);
        if (com.android.launcher3.Utilities.isRtl(getResources())) {
            rect.offsetTo(rect.left - this.mShadowDistance, rect.top + this.mShadowDistance);
        } else {
            rect.offsetTo(rect.left + this.mShadowDistance, rect.top + this.mShadowDistance);
        }
        bitmapDrawable.setBounds(rect);
        bitmapDrawable.setColorFilter(Utilities.sBlack, PorterDuff.Mode.MULTIPLY);
        bitmapDrawable.setAlpha(50);
        bitmapDrawable.draw(canvas);
    }

    private ScaleAnimation getScaleAnimation() {
        int i;
        int uninstallBadgePositionX;
        ImageView imageView = this.mUninstallBadge;
        int i2 = 0;
        if (imageView == null || imageView.getDrawable() == null) {
            i = 0;
        } else {
            int intrinsicWidth = this.mUninstallBadge.getDrawable().getIntrinsicWidth();
            int intrinsicHeight = this.mUninstallBadge.getDrawable().getIntrinsicHeight();
            if (com.android.launcher3.Utilities.isRtl(getResources())) {
                uninstallBadgePositionX = getUninstallBadgePositionX() + (intrinsicWidth / 2);
            } else {
                uninstallBadgePositionX = intrinsicWidth / 2;
            }
            int i3 = intrinsicHeight / 2;
            i2 = uninstallBadgePositionX;
            i = i3;
        }
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.17f, 1.0f, 1.17f, 0, i2, 0, i);
        scaleAnimation.setDuration(600L);
        scaleAnimation.setRepeatMode(2);
        scaleAnimation.setRepeatCount(-1);
        return scaleAnimation;
    }
}
