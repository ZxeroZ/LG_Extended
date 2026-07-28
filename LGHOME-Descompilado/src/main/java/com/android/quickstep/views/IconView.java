package com.android.quickstep.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import com.android.launcher3.FastBitmapDrawable;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class IconView extends View {
    private Drawable mDrawable;
    private int mDrawableHeight;
    private int mDrawableWidth;
    private ArrayList<OnScaleUpdateListener> mScaleListeners;

    public interface OnScaleUpdateListener {
        void onScaleUpdate(float scale);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public IconView(Context context) {
        super(context);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDrawable(Drawable d) {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        this.mDrawable = d;
        if (d != null) {
            d.setCallback(this);
            this.mDrawable.setBounds(0, 0, getWidth(), getHeight());
            setDrawableSizeInternal(getWidth(), getHeight());
        }
        invalidate();
    }

    public void setDrawableSize(int iconWidth, int iconHeight) {
        this.mDrawableWidth = iconWidth;
        this.mDrawableHeight = iconHeight;
        if (this.mDrawable != null) {
            setDrawableSizeInternal(getWidth(), getHeight());
        }
    }

    private void setDrawableSizeInternal(int selfWidth, int selfHeight) {
        Rect rect = new Rect(0, 0, selfWidth, selfHeight);
        Rect rect2 = new Rect();
        Gravity.apply(17, this.mDrawableWidth, this.mDrawableHeight, rect, rect2);
        this.mDrawable.setBounds(rect2);
    }

    public Drawable getDrawable() {
        return this.mDrawable;
    }

    public int getDrawableWidth() {
        return this.mDrawableWidth;
    }

    public int getDrawableHeight() {
        return this.mDrawableHeight;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, w, h);
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mDrawable;
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mDrawable;
        if (drawable != null && drawable.isStateful() && drawable.setState(getDrawableState())) {
            invalidateDrawable(drawable);
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        ArrayList<OnScaleUpdateListener> arrayList;
        super.invalidateDrawable(drawable);
        if (!(drawable instanceof FastBitmapDrawable) || (arrayList = this.mScaleListeners) == null) {
            return;
        }
        Iterator<OnScaleUpdateListener> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onScaleUpdate(((FastBitmapDrawable) drawable).getScale());
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    public void addUpdateScaleListener(OnScaleUpdateListener listener) {
        if (this.mScaleListeners == null) {
            this.mScaleListeners = new ArrayList<>();
        }
        this.mScaleListeners.add(listener);
        Drawable drawable = this.mDrawable;
        if (drawable instanceof FastBitmapDrawable) {
            listener.onScaleUpdate(((FastBitmapDrawable) drawable).getScale());
        }
    }

    public void removeUpdateScaleListener(OnScaleUpdateListener listener) {
        ArrayList<OnScaleUpdateListener> arrayList = this.mScaleListeners;
        if (arrayList != null) {
            arrayList.remove(listener);
        }
    }

    @Override // android.view.View
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        if (alpha > 0.0f) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }
}
