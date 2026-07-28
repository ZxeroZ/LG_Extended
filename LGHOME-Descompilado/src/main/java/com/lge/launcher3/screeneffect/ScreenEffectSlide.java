package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import com.lge.launcher3.util.MathFunctionUtils;
import com.lge.launcher3.util.OrientationUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectSlide extends ScreenEffectBase {
    public ScreenEffectSlide(Context context) {
        super(context);
    }

    @Override // com.lge.launcher3.screeneffect.ScreenEffectBase
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!OrientationUtils.isPortrait(this.mContext) || !MathFunctionUtils.equals(child.getAlpha(), 1.0f)) {
            return false;
        }
        ScreenEffectTargetManager.getInstance(this.mContext).updatePageToOpaque(child);
        return false;
    }
}
