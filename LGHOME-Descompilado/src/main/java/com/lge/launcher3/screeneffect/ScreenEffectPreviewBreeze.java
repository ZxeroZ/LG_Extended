package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Matrix;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectPreviewBreeze extends ScreenEffectBreeze implements IScreenEffectPreview {
    public ScreenEffectPreviewBreeze(Context context) {
        super(context);
        scaleCameraLocationZ(0.4f);
    }

    @Override // com.lge.launcher3.screeneffect.IScreenEffectPreview
    public Matrix getPageTransformationMatrix(View child) {
        Matrix pageTransformationMatrix = super.getPageTransformationMatrix(child, ScreenEffectPreviewTargetManager.getInstance(this.mContext));
        pageTransformationMatrix.postTranslate(r0.getScrollX(), 0.0f);
        return pageTransformationMatrix;
    }
}
