package com.android.systemui.shared.rotation;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.systemui.navigationbar.buttons.KeyButtonRipple;

/* JADX INFO: loaded from: classes.dex */
public class FloatingRotationButtonView extends ImageView {
    private static final float BACKGROUND_ALPHA = 0.92f;
    private final Configuration mLastConfiguration;
    private final Paint mOvalBgPaint;
    private KeyButtonRipple mRipple;

    public FloatingRotationButtonView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingRotationButtonView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOvalBgPaint = new Paint(3);
        this.mLastConfiguration = getResources().getConfiguration();
        setClickable(true);
        setWillNotDraw(false);
        forceHasOverlappingRendering(false);
    }

    public void setRipple(int i) {
        KeyButtonRipple keyButtonRipple = new KeyButtonRipple(getContext(), this, i);
        this.mRipple = keyButtonRipple;
        setBackground(keyButtonRipple);
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        if (i != 0) {
            jumpDrawablesToCurrentState();
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        KeyButtonRipple keyButtonRipple;
        int iUpdateFrom = this.mLastConfiguration.updateFrom(configuration);
        if (((iUpdateFrom & 1024) == 0 && (iUpdateFrom & 4096) == 0) || (keyButtonRipple = this.mRipple) == null) {
            return;
        }
        keyButtonRipple.updateResources();
    }

    public void setColors(int i, int i2) {
        getDrawable().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
        this.mOvalBgPaint.setColor(Color.valueOf(Color.red(i2), Color.green(i2), Color.blue(i2), BACKGROUND_ALPHA).toArgb());
        this.mRipple.setType(KeyButtonRipple.Type.OVAL);
    }

    public void setDarkIntensity(float f) {
        this.mRipple.setDarkIntensity(f);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        float fMin = Math.min(getWidth(), getHeight());
        canvas.drawOval(0.0f, 0.0f, fMin, fMin, this.mOvalBgPaint);
        super.draw(canvas);
    }
}
