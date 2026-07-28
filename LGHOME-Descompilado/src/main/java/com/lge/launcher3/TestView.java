package com.lge.launcher3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;

/* JADX INFO: loaded from: classes.dex */
public class TestView extends View {
    float density;
    float dpm;
    float mLineWidth;
    Paint mPaint;
    float xdpi;

    public TestView(Context context) {
        super(context);
        float f = getResources().getDisplayMetrics().xdpi;
        this.xdpi = f;
        this.dpm = f / 25.4f;
        this.density = 0.0f;
        this.mLineWidth = 0.0f;
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float f = getResources().getDisplayMetrics().xdpi;
        this.xdpi = f;
        this.dpm = f / 25.4f;
        this.density = 0.0f;
        this.mLineWidth = 0.0f;
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float f = getResources().getDisplayMetrics().xdpi;
        this.xdpi = f;
        this.dpm = f / 25.4f;
        this.density = 0.0f;
        this.mLineWidth = 0.0f;
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        float f = getResources().getDisplayMetrics().xdpi;
        this.xdpi = f;
        this.dpm = f / 25.4f;
        this.density = 0.0f;
        this.mLineWidth = 0.0f;
    }

    public void setLineW(float value) {
        this.mLineWidth = value;
    }

    @Override // android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mPaint == null) {
            initRes();
        }
        drawCmRuler(canvas, getPaddingTop());
        drawLineForTest(canvas, (int) (getPaddingTop() + (this.dpm * 4.0f)));
        drawDpRuler(canvas, (int) (getPaddingTop() + (this.dpm * 6.0f)));
        drawPixelRuler(canvas, (int) (getPaddingTop() + (this.dpm * 8.0f)));
    }

    private void initRes() {
        this.xdpi = getResources().getDisplayMetrics().xdpi;
        this.density = getResources().getDisplayMetrics().density;
        this.dpm = this.xdpi / 25.4f;
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mPaint.setStrokeWidth(this.dpm * 0.1f);
    }

    private void drawLineForTest(Canvas canvas, int top) {
        if (this.mPaint == null) {
            initRes();
        }
        this.mPaint.setColor(-16776961);
        this.mPaint.setStrokeWidth(this.dpm);
        float f = top;
        canvas.drawLine(0.0f, f, this.mLineWidth, f, this.mPaint);
        this.mPaint.setColor(SupportMenu.CATEGORY_MASK);
        float f2 = top + 20;
        canvas.drawLine(0.0f, f2, this.mLineWidth * (getResources().getDisplayMetrics().xdpi / getResources().getDisplayMetrics().ydpi), f2, this.mPaint);
    }

    private void drawCmRuler(Canvas canvas, int top) {
        float f;
        if (this.mPaint == null) {
            initRes();
        }
        this.mPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mPaint.setStrokeWidth(this.dpm * 0.1f);
        int i = (int) this.dpm;
        float width = getWidth();
        float f2 = this.dpm;
        int i2 = (int) (width / f2);
        int i3 = (int) (f2 * 2.0f);
        for (int i4 = 0; i4 < i2; i4++) {
            int i5 = i * i4;
            if (i4 % 5 == 0) {
                f = this.dpm * 2.0f;
            } else {
                f = this.dpm;
            }
            float f3 = i5;
            canvas.drawLine(f3, r5 - ((int) f), f3, top + i3, this.mPaint);
        }
    }

    private void drawPixelRuler(Canvas canvas, int top) {
        int i;
        if (this.mPaint == null) {
            initRes();
        }
        this.mPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mPaint.setStrokeWidth(this.dpm * 0.1f);
        int width = getWidth();
        for (int i2 = 0; i2 < width; i2++) {
            int i3 = i2 * 10;
            if (i2 % 5 == 0) {
                i = (int) (this.dpm * 2.0f);
            } else {
                i = (int) this.dpm;
            }
            float f = i3;
            canvas.drawLine(f, top, f, i + top, this.mPaint);
        }
    }

    private void drawDpRuler(Canvas canvas, int top) {
        int i;
        if (this.mPaint == null) {
            initRes();
        }
        this.mPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mPaint.setStrokeWidth(this.dpm * 0.1f);
        int width = getWidth();
        for (int i2 = 0; i2 < width; i2++) {
            int i3 = (int) (this.density * 2.0f * i2);
            if (i2 % 5 == 0) {
                i = (int) (this.dpm * 2.0f);
            } else {
                i = (int) this.dpm;
            }
            float f = i3;
            canvas.drawLine(f, top, f, i + top, this.mPaint);
        }
    }
}
