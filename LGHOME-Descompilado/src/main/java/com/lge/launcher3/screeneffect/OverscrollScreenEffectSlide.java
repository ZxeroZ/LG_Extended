package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.screeneffect.interpolator.ScreenEffectInterpolatorSpring;
import com.lge.launcher3.util.MathFunctionUtils;
import com.lge.launcher3.util.OrientationUtils;

/* JADX INFO: loaded from: classes.dex */
public class OverscrollScreenEffectSlide extends OverscrollScreenEffectBase {
    public static final boolean DEBUG = false;

    public OverscrollScreenEffectSlide(Context context) {
        super(context, new ScreenEffectInterpolatorSpring());
    }

    @Override // com.lge.launcher3.screeneffect.OverscrollScreenEffectBase
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!OrientationUtils.isPortrait(this.mContext) || !MathFunctionUtils.equals(child.getAlpha(), 1.0f)) {
            return false;
        }
        ScreenEffectConst.OverscrollState overscrollState = ScreenEffectTargetManager.getInstance(this.mContext).getTargetInfo(child).overscrollState;
        float f = ScreenEffectTargetManager.getInstance(this.mContext).getTargetInfo(child).scrollProgress;
        if (overscrollState == ScreenEffectConst.OverscrollState.OVERSCROLL_LEFT) {
            ScreenEffectTargetManager.getInstance(this.mContext).updatePageToWantedAlpha(child, (float) Math.pow(f, 3.0d));
            return false;
        }
        if (overscrollState != ScreenEffectConst.OverscrollState.OVERSCROLL_RIGHT) {
            return false;
        }
        ScreenEffectTargetManager.getInstance(this.mContext).updatePageToWantedAlpha(child, (float) Math.pow(1.0f - f, 3.0d));
        return false;
    }
}
