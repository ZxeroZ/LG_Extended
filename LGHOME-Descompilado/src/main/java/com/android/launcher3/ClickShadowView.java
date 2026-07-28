package com.android.launcher3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class ClickShadowView extends View {
    private static final int SHADOW_HIGH_ALPHA = 60;
    private static final int SHADOW_LOW_ALPHA = 30;
    private static final int SHADOW_SIZE_FACTOR = 3;
    private Bitmap mBitmap;
    private final Paint mPaint;
    private final float mShadowOffset;
    private final float mShadowPadding;

    public ClickShadowView(Context context) {
        super(context);
        Paint paint = new Paint(2);
        this.mPaint = paint;
        paint.setColor(com.lge.launcher3.util.Utilities.sBlack);
        this.mShadowPadding = getResources().getDimension(R.dimen.blur_size_click_shadow);
        this.mShadowOffset = getResources().getDimension(R.dimen.click_shadow_high_shift);
    }

    public int getExtraSize() {
        return (int) (this.mShadowPadding * 3.0f);
    }

    public boolean setBitmap(Bitmap b) {
        if (b == this.mBitmap) {
            return false;
        }
        this.mBitmap = b;
        invalidate();
        return true;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mBitmap != null) {
            this.mPaint.setAlpha(30);
            canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaint);
            this.mPaint.setAlpha(60);
            canvas.drawBitmap(this.mBitmap, 0.0f, this.mShadowOffset, this.mPaint);
        }
    }

    public void animateShadow() {
        setAlpha(0.0f);
        animate().alpha(1.0f).setDuration(2000L).setInterpolator(FastBitmapDrawable.CLICK_FEEDBACK_INTERPOLATOR).start();
    }

    public void alignWithIconView(BubbleTextView view, ViewGroup viewParent) {
        float left = (view.getLeft() + viewParent.getLeft()) - getLeft();
        float top = (view.getTop() + viewParent.getTop()) - getTop();
        int right = view.getRight() - view.getLeft();
        setTranslationX(((((left + viewParent.getTranslationX()) + (view.getCompoundPaddingLeft() * view.getScaleX())) + (((((right - view.getCompoundPaddingRight()) - view.getCompoundPaddingLeft()) - view.getIcon().getBounds().width()) * view.getScaleX()) / 2.0f)) + ((right * (1.0f - view.getScaleX())) / 2.0f)) - this.mShadowPadding);
        setTranslationY((((top + viewParent.getTranslationY()) + (view.getPaddingTop() * view.getScaleY())) + ((view.getHeight() * (1.0f - view.getScaleY())) / 2.0f)) - this.mShadowPadding);
    }
}
