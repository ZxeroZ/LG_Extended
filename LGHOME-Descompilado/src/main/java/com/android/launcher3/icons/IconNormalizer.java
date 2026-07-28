package com.android.launcher3.icons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;
import java.nio.ByteBuffer;
import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public class IconNormalizer {
    private static final float BOUND_RATIO_MARGIN = 0.05f;
    private static final float CIRCLE_AREA_BY_RECT = 0.7853982f;
    private static final boolean DEBUG = false;
    public static final float ICON_VISIBLE_AREA_FACTOR = 0.97f;
    private static final float LINEAR_SCALE_SLOPE = 0.040449437f;
    private static final float MAX_CIRCLE_AREA_FACTOR = 0.6597222f;
    private static final float MAX_SQUARE_AREA_FACTOR = 0.6510417f;
    private static final int MIN_VISIBLE_ALPHA = 40;
    private static final float PIXEL_DIFF_PERCENTAGE_THRESHOLD = 0.005f;
    private static final float SCALE_NOT_INITIALIZED = 0.0f;
    private static final String TAG = "IconNormalizer";
    private final RectF mAdaptiveIconBounds;
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap;
    private final Rect mBounds;
    private final Canvas mCanvas;
    private boolean mEnableShapeDetection;
    private final float[] mLeftBorder;
    private final Matrix mMatrix;
    private final int mMaxSize;
    private final Paint mPaintMaskShape;
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels;
    private final float[] mRightBorder;
    private final Path mShapePath;

    IconNormalizer(Context context, int iconBitmapSize, boolean shapeDetection) {
        int i = iconBitmapSize * 2;
        this.mMaxSize = i;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ALPHA_8);
        this.mBitmap = bitmapCreateBitmap;
        this.mCanvas = new Canvas(bitmapCreateBitmap);
        this.mPixels = new byte[i * i];
        this.mLeftBorder = new float[i];
        this.mRightBorder = new float[i];
        this.mBounds = new Rect();
        this.mAdaptiveIconBounds = new RectF();
        Paint paint = new Paint();
        this.mPaintMaskShape = paint;
        paint.setColor(SupportMenu.CATEGORY_MASK);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        Paint paint2 = new Paint();
        this.mPaintMaskShapeOutline = paint2;
        paint2.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mShapePath = new Path();
        this.mMatrix = new Matrix();
        this.mAdaptiveIconScale = 0.0f;
        this.mEnableShapeDetection = shapeDetection;
    }

    private static float getScale(float hullArea, float boundingArea, float fullArea) {
        float f = hullArea / boundingArea;
        if (hullArea / fullArea > (f < CIRCLE_AREA_BY_RECT ? MAX_CIRCLE_AREA_FACTOR : ((1.0f - f) * LINEAR_SCALE_SLOPE) + MAX_SQUARE_AREA_FACTOR)) {
            return (float) Math.sqrt(r4 / r3);
        }
        return 1.0f;
    }

    public static float normalizeAdaptiveIcon(Drawable d, int size, RectF outBounds) {
        Rect rect = new Rect(d.getBounds());
        d.setBounds(0, 0, size, size);
        Path iconMask = ((AdaptiveIconDrawable) d).getIconMask();
        Region region = new Region();
        region.setPath(iconMask, new Region(0, 0, size, size));
        Rect bounds = region.getBounds();
        int area = GraphicsUtils.getArea(region);
        if (outBounds != null) {
            float f = size;
            outBounds.set(bounds.left / f, bounds.top / f, 1.0f - (bounds.right / f), 1.0f - (bounds.bottom / f));
        }
        d.setBounds(rect);
        float f2 = area;
        return getScale(f2, f2, size * size);
    }

    private boolean isShape(Path maskPath) {
        if (Math.abs((this.mBounds.width() / this.mBounds.height()) - 1.0f) > BOUND_RATIO_MARGIN) {
            return false;
        }
        this.mMatrix.reset();
        this.mMatrix.setScale(this.mBounds.width(), this.mBounds.height());
        this.mMatrix.postTranslate(this.mBounds.left, this.mBounds.top);
        maskPath.transform(this.mMatrix, this.mShapePath);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShape);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShapeOutline);
        return isTransparentBitmap();
    }

    private boolean isTransparentBitmap() {
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(this.mPixels);
        byteBufferWrap.rewind();
        this.mBitmap.copyPixelsToBuffer(byteBufferWrap);
        int i = this.mBounds.top;
        int i2 = this.mMaxSize;
        int i3 = i * i2;
        int i4 = i2 - this.mBounds.right;
        int i5 = 0;
        while (i < this.mBounds.bottom) {
            int i6 = i3 + this.mBounds.left;
            for (int i7 = this.mBounds.left; i7 < this.mBounds.right; i7++) {
                if ((this.mPixels[i6] & UByte.MAX_VALUE) > 40) {
                    i5++;
                }
                i6++;
            }
            i3 = i6 + i4;
            i++;
        }
        return ((float) i5) / ((float) (this.mBounds.width() * this.mBounds.height())) < PIXEL_DIFF_PERCENTAGE_THRESHOLD;
    }

    public synchronized float getScale(Drawable d, RectF outBounds, Path path, boolean[] outMaskShape) {
        if (BaseIconFactory.ATLEAST_OREO && (d instanceof AdaptiveIconDrawable)) {
            if (this.mAdaptiveIconScale == 0.0f) {
                this.mAdaptiveIconScale = normalizeAdaptiveIcon(d, this.mMaxSize, this.mAdaptiveIconBounds);
            }
            if (outBounds != null) {
                outBounds.set(this.mAdaptiveIconBounds);
            }
            return this.mAdaptiveIconScale;
        }
        int intrinsicWidth = d.getIntrinsicWidth();
        int intrinsicHeight = d.getIntrinsicHeight();
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            if (intrinsicWidth <= 0 || intrinsicWidth > this.mMaxSize) {
                intrinsicWidth = this.mMaxSize;
            }
            if (intrinsicHeight <= 0 || intrinsicHeight > this.mMaxSize) {
                intrinsicHeight = this.mMaxSize;
            }
        } else {
            int i = this.mMaxSize;
            if (intrinsicWidth > i || intrinsicHeight > i) {
                int iMax = Math.max(intrinsicWidth, intrinsicHeight);
                int i2 = this.mMaxSize;
                intrinsicWidth = (intrinsicWidth * i2) / iMax;
                intrinsicHeight = (i2 * intrinsicHeight) / iMax;
            }
        }
        int i3 = 0;
        this.mBitmap.eraseColor(0);
        d.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        d.draw(this.mCanvas);
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(this.mPixels);
        byteBufferWrap.rewind();
        this.mBitmap.copyPixelsToBuffer(byteBufferWrap);
        int i4 = this.mMaxSize;
        int i5 = i4 + 1;
        int i6 = i4 - intrinsicWidth;
        int i7 = 0;
        int i8 = 0;
        int i9 = -1;
        int iMax2 = -1;
        int i10 = -1;
        while (i7 < intrinsicHeight) {
            int i11 = -1;
            int i12 = -1;
            for (int i13 = i3; i13 < intrinsicWidth; i13++) {
                if ((this.mPixels[i8] & UByte.MAX_VALUE) > 40) {
                    if (i11 == -1) {
                        i11 = i13;
                    }
                    i12 = i13;
                }
                i8++;
            }
            i8 += i6;
            this.mLeftBorder[i7] = i11;
            this.mRightBorder[i7] = i12;
            if (i11 != -1) {
                if (i9 == -1) {
                    i9 = i7;
                }
                int iMin = Math.min(i5, i11);
                iMax2 = Math.max(iMax2, i12);
                i5 = iMin;
                i10 = i7;
            }
            i7++;
            i3 = 0;
        }
        if (i9 != -1 && iMax2 != -1) {
            convertToConvexArray(this.mLeftBorder, 1, i9, i10);
            convertToConvexArray(this.mRightBorder, -1, i9, i10);
            float f = 0.0f;
            for (int i14 = 0; i14 < intrinsicHeight; i14++) {
                float[] fArr = this.mLeftBorder;
                if (fArr[i14] > -1.0f) {
                    f += (this.mRightBorder[i14] - fArr[i14]) + 1.0f;
                }
            }
            this.mBounds.left = i5;
            this.mBounds.right = iMax2;
            this.mBounds.top = i9;
            this.mBounds.bottom = i10;
            if (outBounds != null) {
                float f2 = intrinsicWidth;
                float f3 = intrinsicHeight;
                outBounds.set(this.mBounds.left / f2, this.mBounds.top / f3, 1.0f - (this.mBounds.right / f2), 1.0f - (this.mBounds.bottom / f3));
            }
            if (outMaskShape != null && this.mEnableShapeDetection && outMaskShape.length > 0) {
                outMaskShape[0] = isShape(path);
            }
            return getScale(f, ((i10 + 1) - i9) * ((iMax2 + 1) - i5), intrinsicWidth * intrinsicHeight);
        }
        return 1.0f;
    }

    private static void convertToConvexArray(float[] xCoordinates, int direction, int topY, int bottomY) {
        float[] fArr = new float[xCoordinates.length - 1];
        int i = -1;
        float f = Float.MAX_VALUE;
        for (int i2 = topY + 1; i2 <= bottomY; i2++) {
            if (xCoordinates[i2] > -1.0f) {
                if (f == Float.MAX_VALUE) {
                    i = topY;
                } else {
                    float f2 = ((xCoordinates[i2] - xCoordinates[i]) / (i2 - i)) - f;
                    float f3 = direction;
                    if (f2 * f3 < 0.0f) {
                        while (i > topY) {
                            i--;
                            if ((((xCoordinates[i2] - xCoordinates[i]) / (i2 - i)) - fArr[i]) * f3 >= 0.0f) {
                                break;
                            }
                        }
                    }
                }
                f = (xCoordinates[i2] - xCoordinates[i]) / (i2 - i);
                for (int i3 = i; i3 < i2; i3++) {
                    fArr[i3] = f;
                    xCoordinates[i3] = xCoordinates[i] + ((i3 - i) * f);
                }
                i = i2;
            }
        }
    }

    public static int getNormalizedCircleSize(int size) {
        return (int) Math.round(Math.sqrt(((double) (((size * size) * MAX_CIRCLE_AREA_FACTOR) * 4.0f)) / 3.141592653589793d));
    }
}
