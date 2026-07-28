package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.Log;
import android.view.ViewDebug;
import androidx.core.view.ViewCompat;
import com.android.launcher3.icons.ShadowGenerator;

/* JADX INFO: loaded from: classes.dex */
public class DotRenderer {
    private static final float SIZE_PERCENTAGE = 0.228f;
    private static final String TAG = "DotRenderer";
    private final Bitmap mBackgroundWithShadow;
    private final float mBitmapOffset;
    private final Paint mCirclePaint = new Paint(3);
    private final float mCircleRadius;
    private final float[] mLeftDotPosition;
    private final float[] mRightDotPosition;

    public static class DrawParams {

        @ViewDebug.ExportedProperty(category = "notification dot", formatToHexString = true)
        public int color;

        @ViewDebug.ExportedProperty(category = "notification dot")
        public Rect iconBounds = new Rect();

        @ViewDebug.ExportedProperty(category = "notification dot")
        public boolean leftAlign;

        @ViewDebug.ExportedProperty(category = "notification dot")
        public float scale;
    }

    public DotRenderer(int iconSizePx, Path iconShapePath, int pathSize) {
        int iRound = Math.round(iconSizePx * SIZE_PERCENTAGE);
        ShadowGenerator.Builder builder = new ShadowGenerator.Builder(0);
        builder.ambientShadowAlpha = 88;
        this.mBackgroundWithShadow = builder.setupBlurForSize(iRound).createPill(iRound, iRound);
        this.mCircleRadius = builder.radius;
        this.mBitmapOffset = (-r3.getHeight()) * 0.5f;
        float f = pathSize;
        this.mLeftDotPosition = getPathPoint(iconShapePath, f, -1.0f);
        this.mRightDotPosition = getPathPoint(iconShapePath, f, 1.0f);
    }

    private static float[] getPathPoint(Path path, float size, float direction) {
        float f = size / 2.0f;
        float f2 = (direction * f) + f;
        Path path2 = new Path();
        path2.moveTo(f, f);
        path2.lineTo((direction * 1.0f) + f2, 0.0f);
        path2.lineTo(f2, -1.0f);
        path2.close();
        path2.op(path, Path.Op.INTERSECT);
        float[] fArr = new float[2];
        new PathMeasure(path2, false).getPosTan(0.0f, fArr, null);
        fArr[0] = fArr[0] / size;
        fArr[1] = fArr[1] / size;
        return fArr;
    }

    public float[] getLeftDotPosition() {
        return this.mLeftDotPosition;
    }

    public float[] getRightDotPosition() {
        return this.mRightDotPosition;
    }

    public void draw(Canvas canvas, DrawParams params) {
        float fMin;
        if (params == null) {
            Log.e(TAG, "Invalid null argument(s) passed in call to draw.");
            return;
        }
        canvas.save();
        Rect rect = params.iconBounds;
        float[] fArr = params.leftAlign ? this.mLeftDotPosition : this.mRightDotPosition;
        float fWidth = rect.left + (rect.width() * fArr[0]);
        float fHeight = rect.top + (rect.height() * fArr[1]);
        Rect clipBounds = canvas.getClipBounds();
        if (params.leftAlign) {
            fMin = Math.max(0.0f, clipBounds.left - (this.mBitmapOffset + fWidth));
        } else {
            fMin = Math.min(0.0f, clipBounds.right - (fWidth - this.mBitmapOffset));
        }
        canvas.translate(fWidth + fMin, fHeight + Math.max(0.0f, clipBounds.top - (this.mBitmapOffset + fHeight)));
        canvas.scale(params.scale, params.scale);
        this.mCirclePaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        Bitmap bitmap = this.mBackgroundWithShadow;
        float f = this.mBitmapOffset;
        canvas.drawBitmap(bitmap, f, f, this.mCirclePaint);
        this.mCirclePaint.setColor(params.color);
        canvas.drawCircle(0.0f, 0.0f, this.mCircleRadius, this.mCirclePaint);
        canvas.restore();
    }
}
