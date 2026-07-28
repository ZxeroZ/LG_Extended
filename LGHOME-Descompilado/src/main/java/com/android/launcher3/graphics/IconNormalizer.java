package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Random;
import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public class IconNormalizer {
    private static final float BOUND_RATIO_MARGIN = 0.05f;
    private static final float CIRCLE_AREA_BY_RECT = 0.7853982f;
    private static final boolean DEBUG = false;
    private static final float LINEAR_SCALE_SLOPE = 0.040449437f;
    private static final Object LOCK = new Object();
    private static final float MAX_CIRCLE_AREA_FACTOR = 0.6597222f;
    private static final float MAX_SQUARE_AREA_FACTOR = 0.6510417f;
    private static final int MIN_VISIBLE_ALPHA = 40;
    private static final float PIXEL_DIFF_PERCENTAGE_THRESHOLD = 0.005f;
    private static final float SCALE_NOT_INITIALIZED = 0.0f;
    private static final String TAG = "IconNormalizer";
    private static IconNormalizer sIconNormalizer;
    private final Rect mAdaptiveIconBounds;
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap;
    private final Bitmap mBitmapARGB;
    private final Rect mBounds;
    private final Canvas mCanvas;
    private Canvas mCanvasARGB;
    private File mDir;
    private int mFileId;
    private final float[] mLeftBorder;
    private final Matrix mMatrix;
    private final int mMaxSize;
    private Paint mPaintIcon;
    private final Paint mPaintMaskShape;
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels;
    private final int[] mPixelsARGB;
    private Random mRandom;
    private final float[] mRightBorder;

    private IconNormalizer(Context context) {
        int i = LauncherAppState.getIDP(context).iconBitmapSize * 2;
        this.mMaxSize = i;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ALPHA_8);
        this.mBitmap = bitmapCreateBitmap;
        this.mCanvas = new Canvas(bitmapCreateBitmap);
        this.mPixels = new byte[i * i];
        this.mPixelsARGB = new int[i * i];
        this.mLeftBorder = new float[i];
        this.mRightBorder = new float[i];
        this.mBounds = new Rect();
        this.mAdaptiveIconBounds = new Rect();
        Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        this.mBitmapARGB = bitmapCreateBitmap2;
        this.mCanvasARGB = new Canvas(bitmapCreateBitmap2);
        Paint paint = new Paint();
        this.mPaintIcon = paint;
        paint.setColor(-1);
        Paint paint2 = new Paint();
        this.mPaintMaskShape = paint2;
        paint2.setColor(SupportMenu.CATEGORY_MASK);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        Paint paint3 = new Paint();
        this.mPaintMaskShapeOutline = paint3;
        paint3.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        this.mMatrix = new Matrix();
        int[] iArr = new int[i * i];
        this.mAdaptiveIconScale = 0.0f;
        this.mDir = context.getExternalFilesDir(null);
        this.mRandom = new Random();
    }

    private boolean isShape(Path maskPath) {
        if (Math.abs((this.mBounds.width() / this.mBounds.height()) - 1.0f) > BOUND_RATIO_MARGIN) {
            return false;
        }
        this.mFileId = this.mRandom.nextInt();
        this.mBitmapARGB.eraseColor(0);
        this.mCanvasARGB.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaintIcon);
        this.mMatrix.reset();
        this.mMatrix.setScale(this.mBounds.width(), this.mBounds.height());
        this.mMatrix.postTranslate(this.mBounds.left, this.mBounds.top);
        maskPath.transform(this.mMatrix);
        this.mCanvasARGB.drawPath(maskPath, this.mPaintMaskShape);
        this.mCanvasARGB.drawPath(maskPath, this.mPaintMaskShapeOutline);
        return isTransparentBitmap(this.mBitmapARGB);
    }

    private boolean isTransparentBitmap(Bitmap bitmap) {
        int iWidth = this.mBounds.width();
        int iHeight = this.mBounds.height();
        bitmap.getPixels(this.mPixelsARGB, 0, iWidth, this.mBounds.left, this.mBounds.top, iWidth, iHeight);
        int i = 0;
        for (int i2 = 0; i2 < iWidth * iHeight; i2++) {
            if (Color.alpha(this.mPixelsARGB[i2]) > 40) {
                i++;
            }
        }
        return ((float) i) / ((float) (this.mBounds.width() * this.mBounds.height())) < PIXEL_DIFF_PERCENTAGE_THRESHOLD;
    }

    public synchronized float getScale(Drawable d, RectF outBounds, Path path, boolean[] outMaskShape) {
        if (Utilities.isAtLeastO() && (d instanceof AdaptiveIconDrawable) && this.mAdaptiveIconScale != 0.0f) {
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
            int i14 = i12;
            this.mRightBorder[i7] = i14;
            if (i11 != -1) {
                if (i9 == -1) {
                    i9 = i7;
                }
                int iMin = Math.min(i5, i11);
                iMax2 = Math.max(iMax2, i14);
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
            for (int i15 = 0; i15 < intrinsicHeight; i15++) {
                float[] fArr = this.mLeftBorder;
                if (fArr[i15] > -1.0f) {
                    f += (this.mRightBorder[i15] - fArr[i15]) + 1.0f;
                }
            }
            float f2 = f / (((i10 + 1) - i9) * ((iMax2 + 1) - i5));
            float f3 = f2 < CIRCLE_AREA_BY_RECT ? MAX_CIRCLE_AREA_FACTOR : ((1.0f - f2) * LINEAR_SCALE_SLOPE) + MAX_SQUARE_AREA_FACTOR;
            this.mBounds.left = i5;
            this.mBounds.right = iMax2;
            this.mBounds.top = i9;
            this.mBounds.bottom = i10;
            if (outBounds != null) {
                float f4 = intrinsicWidth;
                outBounds.set(this.mBounds.left / f4, this.mBounds.top, 1.0f - (this.mBounds.right / f4), 1.0f - (this.mBounds.bottom / intrinsicHeight));
            }
            if (outMaskShape != null && outMaskShape.length > 0) {
                outMaskShape[0] = isShape(path);
            }
            float fSqrt = f / (intrinsicWidth * intrinsicHeight) > f3 ? (float) Math.sqrt(f3 / r7) : 1.0f;
            if (Utilities.isAtLeastO() && (d instanceof AdaptiveIconDrawable) && this.mAdaptiveIconScale == 0.0f) {
                this.mAdaptiveIconScale = fSqrt;
                this.mAdaptiveIconBounds.set(this.mBounds);
            }
            return fSqrt;
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

    public static IconNormalizer getInstance(Context context) {
        synchronized (LOCK) {
            if (sIconNormalizer == null) {
                sIconNormalizer = new IconNormalizer(context);
            }
        }
        return sIconNormalizer;
    }
}
