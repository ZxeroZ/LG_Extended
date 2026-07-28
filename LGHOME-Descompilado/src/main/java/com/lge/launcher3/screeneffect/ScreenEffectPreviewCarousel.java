package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Matrix;
import android.view.View;
import com.lge.launcher3.screeneffect.ScreenEffectTargetManager;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectPreviewCarousel extends ScreenEffectCarousel implements IScreenEffectPreview {
    public ScreenEffectPreviewCarousel(Context context) {
        super(context);
        scaleCameraLocationZ(0.75f);
    }

    @Override // com.lge.launcher3.screeneffect.IScreenEffectPreview
    public Matrix getPageTransformationMatrix(View child) {
        ScreenEffectPreviewTargetManager screenEffectPreviewTargetManager = ScreenEffectPreviewTargetManager.getInstance(this.mContext);
        ScreenEffectTargetManager.TargetInfo targetInfo = screenEffectPreviewTargetManager.getTargetInfo(child);
        Matrix pageTransformationMatrix = super.getPageTransformationMatrix(child, screenEffectPreviewTargetManager);
        pageTransformationMatrix.postTranslate(-targetInfo.scrollX, 0.0f);
        return pageTransformationMatrix;
    }
}
