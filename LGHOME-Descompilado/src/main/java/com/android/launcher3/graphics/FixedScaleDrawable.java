package com.android.launcher3.graphics;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class FixedScaleDrawable extends DrawableWrapper {
    private static final float LEGACY_ICON_SCALE = 0.46669f;
    private float mScaleX;
    private float mScaleY;

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) {
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) {
    }

    public FixedScaleDrawable() {
        super(new ColorDrawable());
        this.mScaleX = LEGACY_ICON_SCALE;
        this.mScaleY = LEGACY_ICON_SCALE;
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int iSave = canvas.save(1);
        canvas.scale(this.mScaleX, this.mScaleY, getBounds().exactCenterX(), getBounds().exactCenterY());
        super.draw(canvas);
        canvas.restoreToCount(iSave);
    }

    public void setScale(float scale) {
        float intrinsicHeight = getIntrinsicHeight();
        float intrinsicWidth = getIntrinsicWidth();
        float f = scale * LEGACY_ICON_SCALE;
        this.mScaleX = f;
        this.mScaleY = f;
        if (intrinsicHeight > intrinsicWidth && intrinsicWidth > 0.0f) {
            this.mScaleX = f * (intrinsicWidth / intrinsicHeight);
        } else {
            if (intrinsicWidth <= intrinsicHeight || intrinsicHeight <= 0.0f) {
                return;
            }
            this.mScaleY = f * (intrinsicHeight / intrinsicWidth);
        }
    }
}
