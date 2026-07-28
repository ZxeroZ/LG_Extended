package com.lge.launcher3.liveicon;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.lge.launcher3.R;
import java.util.Calendar;
import java.util.Date;

/* JADX INFO: loaded from: classes.dex */
public class AnalogClockView extends FrameLayout {
    private Drawable mCenterDot;
    private float mHour;
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private float mMinutes;

    public AnalogClockView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        Resources resources = context.getResources();
        this.mHourHand = resources.getDrawable(R.drawable.clock_hourhand, null);
        this.mMinuteHand = resources.getDrawable(R.drawable.clock_minutehand, null);
        this.mCenterDot = resources.getDrawable(R.drawable.clock_centerdot, null);
    }

    public void update() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        float f = calendar.get(12);
        float f2 = calendar.get(10) + (f / 60.0f);
        if (this.mMinutes == f && this.mHour == f2) {
            return;
        }
        this.mMinutes = f;
        this.mHour = f2;
        invalidate();
    }

    public AnalogClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int right = (getRight() - getLeft()) / 2;
        int bottom = (getBottom() - getTop()) / 2;
        Drawable drawable = this.mMinuteHand;
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = this.mMinuteHand.getIntrinsicHeight();
            canvas.save();
            canvas.rotate((this.mMinutes / 60.0f) * 360.0f, right, bottom);
            int i = intrinsicWidth / 2;
            this.mMinuteHand.setBounds(right - i, bottom - intrinsicHeight, i + right, bottom);
            this.mMinuteHand.draw(canvas);
            canvas.restore();
        }
        Drawable drawable2 = this.mHourHand;
        if (drawable2 != null) {
            int intrinsicWidth2 = drawable2.getIntrinsicWidth();
            int intrinsicHeight2 = this.mHourHand.getIntrinsicHeight();
            canvas.save();
            canvas.rotate((this.mHour / 12.0f) * 360.0f, right, bottom);
            int i2 = intrinsicWidth2 / 2;
            this.mHourHand.setBounds(right - i2, bottom - intrinsicHeight2, i2 + right, bottom);
            this.mHourHand.draw(canvas);
            canvas.restore();
        }
        Drawable drawable3 = this.mCenterDot;
        if (drawable3 != null) {
            int intrinsicWidth3 = drawable3.getIntrinsicWidth() / 2;
            int intrinsicHeight3 = this.mCenterDot.getIntrinsicHeight() / 2;
            this.mCenterDot.setBounds(right - intrinsicWidth3, bottom - intrinsicHeight3, right + intrinsicWidth3, bottom + intrinsicHeight3);
            this.mCenterDot.draw(canvas);
        }
    }

    public boolean shouldUpdate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        float f = calendar.get(12);
        return (this.mMinutes == f && this.mHour == ((float) calendar.get(10)) + (f / 60.0f)) ? false : true;
    }
}
