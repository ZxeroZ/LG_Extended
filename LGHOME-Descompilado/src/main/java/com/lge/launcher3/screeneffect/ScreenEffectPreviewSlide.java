package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Matrix;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectPreviewSlide extends ScreenEffectSlide implements IScreenEffectPreview {
    public ScreenEffectPreviewSlide(Context context) {
        super(context);
    }

    @Override // com.lge.launcher3.screeneffect.IScreenEffectPreview
    public Matrix getPageTransformationMatrix(View child) {
        ScreenEffectPreviewTargetManager screenEffectPreviewTargetManager = ScreenEffectPreviewTargetManager.getInstance(this.mContext);
        this.mPageMatrix.reset();
        this.mPageMatrix.postTranslate(screenEffectPreviewTargetManager.getScrollX(), 0.0f);
        return this.mPageMatrix;
    }
}
