package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import com.android.launcher3.Workspace;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class OverscrollScreenEffectBase {
    protected static final Matrix IDENTITY_MATRIX = new Matrix();
    protected static final String TAG = "OverscrollScreenEffectBase";
    protected Context mContext;
    protected Interpolator mInterpolator;
    protected Matrix mPageMatrix = new Matrix();
    protected PointF mPivot = new PointF();

    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return false;
    }

    public OverscrollScreenEffectBase(Context context, Interpolator interpolator) {
        this.mContext = null;
        this.mInterpolator = null;
        if (context == null) {
            LGLog.e(TAG, String.format("Context is null", new Object[0]));
        } else {
            this.mContext = context;
            this.mInterpolator = interpolator;
        }
    }

    protected boolean superDrawChild(Canvas canvas, View child, long drawingTime) {
        ViewParent parent = child.getParent();
        if (!(parent instanceof Workspace)) {
            return false;
        }
        ((Workspace) parent).superDrawChild(canvas, child, drawingTime);
        return true;
    }

    protected boolean drawChild(Canvas canvas, Bitmap bitmap, Paint paint) {
        if (bitmap == null) {
            return false;
        }
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return true;
    }

    protected boolean drawChild(Canvas canvas, Bitmap bitmap, Rect src, Rect dst, Paint paint) {
        if (bitmap == null) {
            return false;
        }
        canvas.drawBitmap(bitmap, src, dst, paint);
        return true;
    }

    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }
}
