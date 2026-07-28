package com.android.launcher3;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class HolographicOutlineHelper {
    private static HolographicOutlineHelper sInstance;
    private final SparseArray<Bitmap> mBitmapCache;
    private final Paint mBlurPaint;
    private final Canvas mCanvas = new Canvas();
    private final Paint mDrawPaint;
    private final Paint mErasePaint;
    private final BlurMaskFilter mMediumInnerBlurMaskFilter;
    private final BlurMaskFilter mMediumOuterBlurMaskFilter;
    private final BlurMaskFilter mShadowBlurMaskFilter;
    private final BlurMaskFilter mThinOuterBlurMaskFilter;

    private HolographicOutlineHelper(Context context) {
        Paint paint = new Paint();
        this.mDrawPaint = paint;
        Paint paint2 = new Paint();
        this.mBlurPaint = paint2;
        Paint paint3 = new Paint();
        this.mErasePaint = paint3;
        this.mBitmapCache = new SparseArray<>(4);
        Resources resources = context.getResources();
        float dimension = resources.getDimension(R.dimen.blur_size_medium_outline);
        this.mMediumOuterBlurMaskFilter = new BlurMaskFilter(dimension, BlurMaskFilter.Blur.OUTER);
        this.mMediumInnerBlurMaskFilter = new BlurMaskFilter(dimension, BlurMaskFilter.Blur.NORMAL);
        this.mThinOuterBlurMaskFilter = new BlurMaskFilter(resources.getDimension(R.dimen.blur_size_thin_outline), BlurMaskFilter.Blur.OUTER);
        this.mShadowBlurMaskFilter = new BlurMaskFilter(resources.getDimension(R.dimen.blur_size_click_shadow), BlurMaskFilter.Blur.NORMAL);
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        paint2.setFilterBitmap(true);
        paint2.setAntiAlias(true);
        paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        paint3.setFilterBitmap(true);
        paint3.setAntiAlias(true);
    }

    public static HolographicOutlineHelper obtain(Context context) {
        if (sInstance == null) {
            sInstance = new HolographicOutlineHelper(context);
        }
        return sInstance;
    }

    void applyExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color, int outlineColor) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, true);
    }

    void applyExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color, int outlineColor, boolean clipAlpha) {
        if (clipAlpha) {
            int width = srcDst.getWidth() * srcDst.getHeight();
            int[] iArr = new int[width];
            srcDst.getPixels(iArr, 0, srcDst.getWidth(), 0, 0, srcDst.getWidth(), srcDst.getHeight());
            for (int i = 0; i < width; i++) {
                if ((iArr[i] >>> 24) < 188) {
                    iArr[i] = 0;
                }
            }
            srcDst.setPixels(iArr, 0, srcDst.getWidth(), 0, 0, srcDst.getWidth(), srcDst.getHeight());
        }
        Bitmap bitmapExtractAlpha = srcDst.extractAlpha();
        this.mBlurPaint.setMaskFilter(this.mMediumOuterBlurMaskFilter);
        Bitmap bitmapExtractAlpha2 = bitmapExtractAlpha.extractAlpha(this.mBlurPaint, new int[2]);
        this.mBlurPaint.setMaskFilter(this.mThinOuterBlurMaskFilter);
        Bitmap bitmapExtractAlpha3 = bitmapExtractAlpha.extractAlpha(this.mBlurPaint, new int[2]);
        srcDstCanvas.setBitmap(bitmapExtractAlpha);
        srcDstCanvas.drawColor(com.lge.launcher3.util.Utilities.sBlack, PorterDuff.Mode.SRC_OUT);
        this.mBlurPaint.setMaskFilter(this.mMediumInnerBlurMaskFilter);
        Bitmap bitmapExtractAlpha4 = bitmapExtractAlpha.extractAlpha(this.mBlurPaint, new int[2]);
        srcDstCanvas.setBitmap(bitmapExtractAlpha4);
        srcDstCanvas.drawBitmap(bitmapExtractAlpha, -r14[0], -r14[1], this.mErasePaint);
        srcDstCanvas.drawRect(0.0f, 0.0f, -r14[0], bitmapExtractAlpha4.getHeight(), this.mErasePaint);
        srcDstCanvas.drawRect(0.0f, 0.0f, bitmapExtractAlpha4.getWidth(), -r14[1], this.mErasePaint);
        srcDstCanvas.setBitmap(srcDst);
        srcDstCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        this.mDrawPaint.setColor(color);
        srcDstCanvas.drawBitmap(bitmapExtractAlpha4, r14[0], r14[1], this.mDrawPaint);
        srcDstCanvas.drawBitmap(bitmapExtractAlpha2, r10[0], r10[1], this.mDrawPaint);
        this.mDrawPaint.setColor(outlineColor);
        srcDstCanvas.drawBitmap(bitmapExtractAlpha3, r12[0], r12[1], this.mDrawPaint);
        srcDstCanvas.setBitmap(null);
        bitmapExtractAlpha3.recycle();
        bitmapExtractAlpha2.recycle();
        bitmapExtractAlpha4.recycle();
        bitmapExtractAlpha.recycle();
    }

    Bitmap createMediumDropShadow(BubbleTextView view) {
        Drawable icon = view.getIcon();
        if (icon == null) {
            return null;
        }
        Rect bounds = icon.getBounds();
        int iWidth = (int) (bounds.width() * view.getScaleX());
        int iHeight = (int) (bounds.height() * view.getScaleY());
        if (iWidth <= 0 || iHeight <= 0) {
            return null;
        }
        int i = (iWidth << 16) | iHeight;
        Bitmap bitmapCreateBitmap = this.mBitmapCache.get(i);
        if (bitmapCreateBitmap == null) {
            bitmapCreateBitmap = Bitmap.createBitmap(iWidth, iHeight, Bitmap.Config.ARGB_8888);
            this.mCanvas.setBitmap(bitmapCreateBitmap);
            this.mBitmapCache.put(i, bitmapCreateBitmap);
        } else {
            this.mCanvas.setBitmap(bitmapCreateBitmap);
            this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        this.mCanvas.save(1);
        this.mCanvas.scale(view.getScaleX(), view.getScaleY());
        this.mCanvas.translate(-bounds.left, -bounds.top);
        icon.draw(this.mCanvas);
        this.mCanvas.restore();
        this.mCanvas.setBitmap(null);
        this.mBlurPaint.setMaskFilter(this.mShadowBlurMaskFilter);
        return bitmapCreateBitmap.extractAlpha(this.mBlurPaint, null);
    }
}
