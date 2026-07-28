package com.android.launcher3.badge;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.SparseArray;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.graphics.ShadowGenerator;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class BadgeRenderer {
    private static final float CHAR_SIZE_PERCENTAGE = 0.12f;
    private static final boolean DOTS_ONLY = true;
    private static final float DOT_SCALE = 0.6f;
    private static final float OFFSET_PERCENTAGE = 0.02f;
    private static final float SIZE_PERCENTAGE = 0.38f;
    private static final float STACK_OFFSET_PERCENTAGE_X = 0.05f;
    private static final float STACK_OFFSET_PERCENTAGE_Y = 0.06f;
    private static final float TEXT_SIZE_PERCENTAGE = 0.26f;
    private final Paint mBackgroundPaint;
    private final SparseArray<Bitmap> mBackgroundsWithShadow;
    private final int mCharSize;
    private final Context mContext;
    private final IconDrawer mLargeIconDrawer;
    private final int mOffset;
    private final int mSize;
    private final IconDrawer mSmallIconDrawer;
    private final int mStackOffsetX;
    private final int mStackOffsetY;
    private final int mTextHeight;
    private final Paint mTextPaint;

    public BadgeRenderer(Context context, int iconSizePx) {
        Paint paint = new Paint(1);
        this.mTextPaint = paint;
        this.mBackgroundPaint = new Paint(3);
        this.mContext = context;
        Resources resources = context.getResources();
        float f = iconSizePx;
        this.mSize = (int) (0.38f * f);
        this.mCharSize = (int) (0.12f * f);
        this.mOffset = (int) (OFFSET_PERCENTAGE * f);
        this.mStackOffsetX = (int) (STACK_OFFSET_PERCENTAGE_X * f);
        this.mStackOffsetY = (int) (STACK_OFFSET_PERCENTAGE_Y * f);
        paint.setTextSize(f * TEXT_SIZE_PERCENTAGE);
        paint.setTextAlign(Paint.Align.CENTER);
        this.mLargeIconDrawer = new IconDrawer(resources.getDimensionPixelSize(R.dimen.badge_small_padding));
        this.mSmallIconDrawer = new IconDrawer(resources.getDimensionPixelSize(R.dimen.badge_large_padding));
        Rect rect = new Rect();
        paint.getTextBounds("0", 0, 1, rect);
        this.mTextHeight = rect.height();
        this.mBackgroundsWithShadow = new SparseArray<>(3);
    }

    public void draw(Canvas canvas, IconPalette palette, BadgeInfo badgeInfo, Rect iconBounds, float badgeScale, Point spaceForOffset) {
        this.mTextPaint.setColor(palette.textColor);
        IconDrawer iconDrawer = (badgeInfo == null || !badgeInfo.isIconLarge()) ? this.mSmallIconDrawer : this.mLargeIconDrawer;
        if (badgeInfo != null) {
            badgeInfo.getNotificationIconForBadge(this.mContext, palette.backgroundColor, this.mSize, iconDrawer.mPadding);
        }
        int length = (badgeInfo == null ? "0" : String.valueOf(badgeInfo.getNotificationCount())).length();
        int i = this.mSize;
        Bitmap bitmapCreatePillWithShadow = this.mBackgroundsWithShadow.get(length);
        if (bitmapCreatePillWithShadow == null) {
            bitmapCreatePillWithShadow = ShadowGenerator.createPillWithShadow(-1, i, this.mSize);
            this.mBackgroundsWithShadow.put(length, bitmapCreatePillWithShadow);
        }
        canvas.save(1);
        int i2 = iconBounds.right - (i / 2);
        int i3 = iconBounds.top + (this.mSize / 2);
        float f = badgeScale * DOT_SCALE;
        canvas.translate(i2 + Math.min(this.mOffset, spaceForOffset.x), i3 - Math.min(this.mOffset, spaceForOffset.y));
        canvas.scale(f, f);
        this.mBackgroundPaint.setColorFilter(palette.backgroundColorMatrixFilter);
        int height = bitmapCreatePillWithShadow.getHeight();
        this.mBackgroundPaint.setColorFilter(palette.saturatedBackgroundColorMatrixFilter);
        float f2 = (-height) / 2;
        canvas.drawBitmap(bitmapCreatePillWithShadow, f2, f2, this.mBackgroundPaint);
        canvas.restore();
    }

    private class IconDrawer {
        private final Bitmap mCircleClipBitmap;
        private final int mPadding;
        private final Paint mPaint;

        public IconDrawer(int padding) {
            Paint paint = new Paint(7);
            this.mPaint = paint;
            this.mPadding = padding;
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(BadgeRenderer.this.mSize, BadgeRenderer.this.mSize, Bitmap.Config.ALPHA_8);
            this.mCircleClipBitmap = bitmapCreateBitmap;
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmapCreateBitmap);
            canvas.drawCircle(BadgeRenderer.this.mSize / 2, BadgeRenderer.this.mSize / 2, (BadgeRenderer.this.mSize / 2) - padding, paint);
        }

        public void drawIcon(Shader icon, Canvas canvas) {
            this.mPaint.setShader(icon);
            canvas.drawBitmap(this.mCircleClipBitmap, (-BadgeRenderer.this.mSize) / 2, (-BadgeRenderer.this.mSize) / 2, this.mPaint);
            this.mPaint.setShader(null);
        }
    }
}
