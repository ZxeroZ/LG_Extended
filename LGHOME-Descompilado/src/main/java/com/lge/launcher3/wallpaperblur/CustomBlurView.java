package com.lge.launcher3.wallpaperblur;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.core.view.ViewCompat;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class CustomBlurView extends ImageView {
    private static final int SCRIM_COLOR = Utilities.sBlack & ViewCompat.MEASURED_SIZE_MASK;
    private float mBackgroundAlpha;

    public CustomBlurView(Context context) {
        super(context);
        this.mBackgroundAlpha = 0.0f;
    }

    public CustomBlurView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBackgroundAlpha = 0.0f;
    }

    public CustomBlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBackgroundAlpha = 0.0f;
    }

    public CustomBlurView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mBackgroundAlpha = 0.0f;
    }

    public void setBackgroundAlpha(float alpha) {
        if (alpha != this.mBackgroundAlpha) {
            this.mBackgroundAlpha = alpha;
            invalidate();
        }
    }

    @Override // android.view.View
    protected void dispatchDraw(Canvas canvas) {
        float f = this.mBackgroundAlpha;
        if (f > 0.0f) {
            canvas.drawColor((((int) (f * 255.0f)) << 24) | SCRIM_COLOR);
        }
        super.dispatchDraw(canvas);
    }
}
