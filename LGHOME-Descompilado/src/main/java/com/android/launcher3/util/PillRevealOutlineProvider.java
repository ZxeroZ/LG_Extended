package com.android.launcher3.util;

import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
public class PillRevealOutlineProvider extends RevealOutlineAnimation {
    private int mCenterX;
    private int mCenterY;
    private final float mFinalRadius;
    protected Rect mPillRect;

    @Override // com.android.launcher3.util.RevealOutlineAnimation
    public boolean shouldRemoveElevationDuringAnimation() {
        return false;
    }

    public PillRevealOutlineProvider(int x, int y, Rect pillRect, float radius) {
        this.mCenterX = x;
        this.mCenterY = y;
        this.mPillRect = pillRect;
        this.mFinalRadius = radius;
        this.mOutlineRadius = radius;
    }

    public void setProgress(float progress) {
        int iMax = (int) (progress * Math.max(this.mCenterX, this.mPillRect.width() - this.mCenterX));
        this.mOutline.left = Math.max(this.mPillRect.left, this.mCenterX - iMax);
        this.mOutline.top = Math.max(this.mPillRect.top, this.mCenterY - iMax);
        this.mOutline.right = Math.min(this.mPillRect.right, this.mCenterX + iMax);
        this.mOutline.bottom = Math.min(this.mPillRect.bottom, this.mCenterY + iMax);
        this.mOutlineRadius = Math.min(this.mFinalRadius, this.mOutline.height() / 2);
    }
}
