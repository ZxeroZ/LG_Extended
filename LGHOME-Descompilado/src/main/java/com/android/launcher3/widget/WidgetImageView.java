package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class WidgetImageView extends View {
    private int leftPadding;
    private Drawable mBadge;
    private final int mBadgeMargin;
    protected Bitmap mBitmap;
    protected final RectF mDstRectF;
    protected final Paint mPaint;
    private int topPadding;

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public WidgetImageView(Context context) {
        this(context, null);
    }

    public WidgetImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaint = new Paint(3);
        this.mDstRectF = new RectF();
        this.topPadding = 0;
        this.leftPadding = 0;
        this.mBadgeMargin = context.getResources().getDimensionPixelSize(R.dimen.profile_badge_margin);
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        invalidate();
    }

    public void setBitmap(Bitmap bitmap, Drawable badge) {
        this.mBitmap = bitmap;
        this.mBadge = badge;
        invalidate();
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mBitmap != null) {
            updateDstRectF();
            canvas.drawBitmap(this.mBitmap, (Rect) null, this.mDstRectF, this.mPaint);
            Drawable drawable = this.mBadge;
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }

    protected void updateDstRectF() {
        if (this.leftPadding != 0 && this.topPadding != 0) {
            setImageViewForBottomSheet();
        } else if (this.mBitmap.getWidth() > getWidth()) {
            this.mDstRectF.set(0.0f, this.topPadding, getWidth(), ((getWidth() / this.mBitmap.getWidth()) * this.mBitmap.getHeight()) + this.topPadding);
        } else {
            this.mDstRectF.set((getWidth() - this.mBitmap.getWidth()) * 0.5f, this.topPadding, (getWidth() + this.mBitmap.getWidth()) * 0.5f, this.mBitmap.getHeight() + this.topPadding);
        }
    }

    public Rect getBitmapBounds() {
        updateDstRectF();
        Rect rect = new Rect();
        this.mDstRectF.round(rect);
        return rect;
    }

    private void setImageViewForBottomSheet() {
        int width = getWidth() - (this.leftPadding * 2);
        int height = getHeight() - (this.topPadding * 2);
        if (this.mBitmap.getWidth() > width && this.mBitmap.getHeight() > height) {
            float width2 = width / this.mBitmap.getWidth();
            float height2 = height / this.mBitmap.getHeight();
            if (width2 > height2) {
                width2 = height2;
            }
            float[] widgetXYBottomSeat = getWidgetXYBottomSeat(this.mBitmap.getWidth() * width2, width2 * this.mBitmap.getHeight());
            this.mDstRectF.set(widgetXYBottomSeat[0], widgetXYBottomSeat[1], widgetXYBottomSeat[2], widgetXYBottomSeat[3]);
            return;
        }
        if (this.mBitmap.getWidth() > width) {
            float width3 = (width / this.mBitmap.getWidth()) * this.mBitmap.getHeight();
            this.mDstRectF.set(this.leftPadding, (getHeight() - width3) * 0.5f, getWidth() - this.leftPadding, (getHeight() + width3) * 0.5f);
        } else if (this.mBitmap.getHeight() > height) {
            float height3 = (height / this.mBitmap.getHeight()) * this.mBitmap.getWidth();
            this.mDstRectF.set((getWidth() - height3) * 0.5f, this.topPadding, (getWidth() + height3) * 0.5f, getHeight() - this.topPadding);
        } else {
            float[] widgetXYBottomSeat2 = getWidgetXYBottomSeat(this.mBitmap.getWidth(), this.mBitmap.getHeight());
            this.mDstRectF.set(widgetXYBottomSeat2[0], widgetXYBottomSeat2[1], widgetXYBottomSeat2[2], widgetXYBottomSeat2[3]);
        }
    }

    private float[] getWidgetXYBottomSeat(float bitmapWidth, float bitmapHeight) {
        return new float[]{(getWidth() - bitmapWidth) * 0.5f, (getHeight() - bitmapHeight) * 0.5f, (getWidth() + bitmapWidth) * 0.5f, (getHeight() + bitmapHeight) * 0.5f};
    }

    public void setPaddingWidgetImageView(int padding) {
        this.topPadding = padding;
        this.leftPadding = padding;
    }
}
