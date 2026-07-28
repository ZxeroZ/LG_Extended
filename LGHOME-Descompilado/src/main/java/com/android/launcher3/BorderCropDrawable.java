package com.android.launcher3;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class BorderCropDrawable extends Drawable {
    private final Rect mBoundsShift;
    private final Drawable mChild;
    private final Rect mPadding;

    BorderCropDrawable(Drawable child, boolean cropLeft, boolean cropTop, boolean cropRight, boolean cropBottom) {
        this.mChild = child;
        Rect rect = new Rect();
        this.mBoundsShift = rect;
        Rect rect2 = new Rect();
        this.mPadding = rect2;
        child.getPadding(rect2);
        if (cropLeft) {
            rect.left = -rect2.left;
            rect2.left = 0;
        }
        if (cropTop) {
            rect.top = -rect2.top;
            rect2.top = 0;
        }
        if (cropRight) {
            rect.right = rect2.right;
            rect2.right = 0;
        }
        if (cropBottom) {
            rect.bottom = rect2.bottom;
            rect2.bottom = 0;
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        this.mChild.setBounds(bounds.left + this.mBoundsShift.left, bounds.top + this.mBoundsShift.top, bounds.right + this.mBoundsShift.right, bounds.bottom + this.mBoundsShift.bottom);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        padding.set(this.mPadding);
        return (padding.bottom | ((padding.left | padding.top) | padding.right)) != 0;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.mChild.draw(canvas);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mChild.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mChild.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.mChild.setColorFilter(cf);
    }
}
