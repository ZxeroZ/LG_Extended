package com.android.launcher3.anim;

import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
public class RoundedRectRevealOutlineProvider extends RevealOutlineAnimation {
    private final float mEndRadius;
    private final Rect mEndRect;
    private final float mStartRadius;
    private final Rect mStartRect;

    @Override // com.android.launcher3.anim.RevealOutlineAnimation
    public boolean shouldRemoveElevationDuringAnimation() {
        return false;
    }

    public RoundedRectRevealOutlineProvider(float startRadius, float endRadius, Rect startRect, Rect endRect) {
        this.mStartRadius = startRadius;
        this.mEndRadius = endRadius;
        this.mStartRect = startRect;
        this.mEndRect = endRect;
    }

    @Override // com.android.launcher3.anim.RevealOutlineAnimation
    public void setProgress(float progress) {
        float f = 1.0f - progress;
        this.mOutlineRadius = (this.mStartRadius * f) + (this.mEndRadius * progress);
        this.mOutline.left = (int) ((this.mStartRect.left * f) + (this.mEndRect.left * progress));
        this.mOutline.top = (int) ((this.mStartRect.top * f) + (this.mEndRect.top * progress));
        this.mOutline.right = (int) ((this.mStartRect.right * f) + (this.mEndRect.right * progress));
        this.mOutline.bottom = (int) ((f * this.mStartRect.bottom) + (progress * this.mEndRect.bottom));
    }
}
