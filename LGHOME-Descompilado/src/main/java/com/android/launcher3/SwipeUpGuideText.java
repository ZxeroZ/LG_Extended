package com.android.launcher3;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;
import com.lge.launcher3.R;
import com.lge.launcher3.adaptive.AdaptiveTextManager;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class SwipeUpGuideText extends TextView {
    private static final int LARGE_SHADOW_COLOR = 2133864496;
    private static final float SHADOW_LARGE_RADIUS = 5.0f;
    private static final float SHADOW_LARGE_Y_OFFSET = 0.0f;
    private static final float SHADOW_SMALL_RADIUS = 1.5f;
    private static final float SHADOW_SMALL_Y_OFFSET = 2.5f;
    private static final int SMALL_SHADOW_COLOR = 858796080;
    private static final String TAG = "SwipeUpGuideText";
    private Context mContext;

    public SwipeUpGuideText(Context context) {
        super(context);
        this.mContext = context;
    }

    public SwipeUpGuideText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public SwipeUpGuideText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        int adaptiveTextColor = AdaptiveTextManager.getAdaptiveTextColor();
        setTextColor(adaptiveTextColor);
        if (isBrightAdaptiveColor(adaptiveTextColor)) {
            drawWithShadow(canvas);
        } else {
            getPaint().clearShadowLayer();
            super.draw(canvas);
        }
    }

    private boolean isBrightAdaptiveColor(int color) {
        return color == this.mContext.getApplicationContext().getResources().getColor(R.color.workspace_adaptive_color1, null);
    }

    private void drawWithShadow(Canvas canvas) {
        LGLog.d(TAG, "drawWithShadow");
        setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, 0.0f, LARGE_SHADOW_COLOR);
        super.draw(canvas);
        canvas.save();
        canvas.clipRect(getScrollX(), getScrollY() + getExtendedPaddingTop(), getScrollX() + getWidth(), getScrollY() + getHeight());
        setShadowLayer(SHADOW_SMALL_RADIUS, 0.0f, SHADOW_SMALL_Y_OFFSET, SMALL_SHADOW_COLOR);
        super.draw(canvas);
        canvas.restore();
    }
}
