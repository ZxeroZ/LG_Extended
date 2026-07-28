package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.operator.GVNScreenManager;
import com.lge.launcher3.screeneffect.ScreenEffectTargetManager;
import com.lge.launcher3.util.WindowUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectCarouselGVN extends ScreenEffectCarousel {
    private static final String EFFECT_FILE_PREFIX = "gvn_transition_effect_";
    private static final int[] EFFECT_TRANSLATE_X = {R.dimen.gvn_transi_effect_translate_X0, R.dimen.gvn_transi_effect_translate_X1, R.dimen.gvn_transi_effect_translate_X2};
    private static final int[] EFFECT_TRANSLATE_Y = {R.dimen.gvn_transi_effect_translate_Y0, R.dimen.gvn_transi_effect_translate_Y1, R.dimen.gvn_transi_effect_translate_Y2};
    private Drawable mTransiDrawable;
    private Matrix mTransiDrawableMatrix;
    private int mTransiDrawablePosX;
    private int mTransiDrawablePosY;

    private int getAlpha(float dxRatio) {
        return dxRatio <= 0.5f ? (int) (dxRatio * 2.0f * 255.0f) : (int) ((1.0f - dxRatio) * 2.0f * 255.0f);
    }

    public ScreenEffectCarouselGVN(Context context) {
        super(context);
        this.mTransiDrawable = null;
        this.mTransiDrawableMatrix = new Matrix();
    }

    private void initPaint() {
        if (this.mPagePaint == null) {
            this.mPagePaint = new Paint();
        } else {
            this.mPagePaint.reset();
        }
    }

    private void initTransiDrawable() {
        int transiAniEffectImgIdx = GVNScreenManager.getInstance(this.mContext).getTransiAniEffectImgIdx();
        Drawable drawable = this.mContext.getDrawable(this.mContext.getResources().getIdentifier(EFFECT_FILE_PREFIX + transiAniEffectImgIdx, LauncherConst.RESOURCE_IMAGE_TYPE, this.mContext.getPackageName()));
        this.mTransiDrawable = drawable;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), this.mTransiDrawable.getIntrinsicHeight());
        this.mTransiDrawablePosX = this.mContext.getResources().getDimensionPixelSize(EFFECT_TRANSLATE_X[transiAniEffectImgIdx]);
        this.mTransiDrawablePosY = this.mContext.getResources().getDimensionPixelSize(EFFECT_TRANSLATE_Y[transiAniEffectImgIdx]);
    }

    @Override // com.lge.launcher3.screeneffect.ScreenEffectCarousel, com.lge.launcher3.screeneffect.ScreenEffectBase
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!GVNScreenManager.getInstance(this.mContext).isEnableTransiAniEffect()) {
            return super.drawChild(canvas, child, drawingTime);
        }
        ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
        ScreenEffectTargetManager.TargetInfo targetInfo = screenEffectTargetManager.getTargetInfo(child);
        int iIndexOfChild = screenEffectTargetManager.indexOfChild(child);
        int i = targetInfo.scrollX;
        float dxRatio = getDxRatio(i);
        int alpha = getAlpha(dxRatio);
        int scrollForPageLoop = i - screenEffectTargetManager.getScrollForPageLoop(iIndexOfChild);
        initPaint();
        initTransiDrawable();
        Matrix transiDrawableMatrix = getTransiDrawableMatrix(child, dxRatio, scrollForPageLoop, this.mTransiDrawable);
        canvas.save();
        canvas.concat(transiDrawableMatrix);
        this.mTransiDrawable.setAlpha(alpha);
        this.mTransiDrawable.draw(canvas);
        canvas.restore();
        return super.drawChild(canvas, child, drawingTime);
    }

    private Matrix getTransiDrawableMatrix(View child, float dxRatio, int scrollX, Drawable drawable) {
        int left = child.getLeft();
        int height = child.getHeight();
        float fWidth = drawable.getBounds().width() / 2.0f;
        float fHeight = drawable.getBounds().height() / 2.0f;
        this.mTransiDrawableMatrix.reset();
        this.mTransiDrawableMatrix.preScale(dxRatio, dxRatio);
        this.mTransiDrawableMatrix.postTranslate(fWidth, fHeight);
        this.mTransiDrawableMatrix.preTranslate(-fWidth, -fHeight);
        this.mTransiDrawableMatrix.postTranslate((left + scrollX) - this.mTransiDrawablePosX, height + this.mTransiDrawablePosY);
        return this.mTransiDrawableMatrix;
    }

    private float getDxRatio(int scrollX) {
        float displayWidth = WindowUtils.getDisplayWidth(this.mContext);
        float f = (scrollX % displayWidth) / displayWidth;
        if (Float.compare(f, 0.0f) != 0) {
            return f + (scrollX < 0 ? 1.0f : 0.0f);
        }
        return f;
    }
}
