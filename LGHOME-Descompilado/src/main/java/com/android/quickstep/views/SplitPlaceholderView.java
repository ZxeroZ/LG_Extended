package com.android.quickstep.views;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.TypedValue;
import android.widget.FrameLayout;
import com.android.launcher3.FastBitmapDrawable;

/* JADX INFO: loaded from: classes.dex */
public class SplitPlaceholderView extends FrameLayout {
    public static final FloatProperty<SplitPlaceholderView> ALPHA_FLOAT = new FloatProperty<SplitPlaceholderView>("SplitViewAlpha") { // from class: com.android.quickstep.views.SplitPlaceholderView.1
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(SplitPlaceholderView splitPlaceholderView, float v) {
            splitPlaceholderView.setVisibility(v != 0.0f ? 0 : 8);
            splitPlaceholderView.setAlpha(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(SplitPlaceholderView splitPlaceholderView) {
            return Float.valueOf(splitPlaceholderView.getAlpha());
        }
    };
    private IconView mIconView;
    private final Paint mPaint;
    private final Rect mTempRect;

    public SplitPlaceholderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Paint paint = new Paint(1);
        this.mPaint = paint;
        this.mTempRect = new Rect();
        paint.setColor(getThemeBackgroundColor(context));
        setWillNotDraw(false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        drawBackground(canvas);
        super.dispatchDraw(canvas);
        if (this.mIconView != null) {
            getLocalVisibleRect(this.mTempRect);
            ((FloatingTaskView) getParent()).centerIconView(this.mIconView, this.mTempRect.centerX(), this.mTempRect.centerY());
        }
    }

    public IconView getIconView() {
        return this.mIconView;
    }

    public void setIcon(Drawable drawable, int iconSize) {
        if (this.mIconView == null) {
            IconView iconView = new IconView(getContext());
            this.mIconView = iconView;
            addView(iconView);
        }
        this.mIconView.setDrawable(getScaledDrawable(drawable, iconSize, iconSize));
        this.mIconView.setDrawableSize(iconSize, iconSize);
        this.mIconView.setLayoutParams(new FrameLayout.LayoutParams(iconSize, iconSize));
    }

    private Drawable getScaledDrawable(Drawable source, int destW, int destH) {
        return source instanceof FastBitmapDrawable ? new FastBitmapDrawable(Bitmap.createScaledBitmap(((FastBitmapDrawable) source).getBitmap(), destW, destH, true)) : source;
    }

    private void drawBackground(Canvas canvas) {
        ((FloatingTaskView) getParent()).drawRoundedRect(canvas, this.mPaint);
    }

    private static int getThemeBackgroundColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorBackground, typedValue, true);
        return typedValue.data;
    }
}
