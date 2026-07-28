package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.lge.launcher3.screeneffect.interpolator.ScreenEffectInterpolatorOvershoot;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectBase {
    public static final String TAG = "ScreenEffectBase";
    protected Camera mCamera;
    protected Context mContext;
    protected Interpolator mInterpolator;
    protected Matrix mPageMatrix;
    protected Paint mPagePaint;
    protected PointF mPivot;
    protected static final Matrix IDENTITY_MATRIX = new Matrix();
    protected static boolean sIsRtL = false;

    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return false;
    }

    public boolean isOverscrollHandledBySelf() {
        return false;
    }

    public ScreenEffectBase(Context context) {
        this(context, -1.0f);
    }

    public ScreenEffectBase(Context context, float overshootTension) {
        this.mContext = null;
        this.mCamera = new Camera();
        this.mPageMatrix = new Matrix();
        this.mPagePaint = new Paint();
        this.mPivot = new PointF();
        this.mInterpolator = null;
        if (context == null) {
            LGLog.e(TAG, String.format("Context is null", new Object[0]));
            return;
        }
        this.mContext = context;
        setupCameraLocationZ();
        this.mInterpolator = new ScreenEffectInterpolatorOvershoot(context, overshootTension);
        sIsRtL = Utilities.isRtl(this.mContext.getResources());
    }

    private void setupCameraLocationZ() {
        float locationZ = this.mCamera.getLocationZ() * WindowUtils.getDensity(this.mContext);
        Camera camera = this.mCamera;
        camera.setLocation(camera.getLocationX(), this.mCamera.getLocationY(), locationZ);
    }

    protected void scaleCameraLocationZ(float scale) {
        float locationZ = this.mCamera.getLocationZ() * scale;
        Camera camera = this.mCamera;
        camera.setLocation(camera.getLocationX(), this.mCamera.getLocationY(), locationZ);
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
