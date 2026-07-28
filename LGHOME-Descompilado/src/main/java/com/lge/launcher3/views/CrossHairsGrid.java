package com.lge.launcher3.views;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.InterruptibleInOutAnimator;
import com.android.launcher3.Launcher;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import com.lge.os.Build;
import java.lang.reflect.Array;

/* JADX INFO: loaded from: classes.dex */
public class CrossHairsGrid {
    public static final int CROSS_HAIRS_ANIMATOR = 0;
    public static final int CROSS_HAIRS_ANIMATOR2 = 1;
    private static final Drawable DEFAULT_DRAWABLE = new ColorDrawable(0);
    private static final String TAG = "CrossHairsGrid";
    private final DashPathEffect dashPath;
    private float mAlpha;
    private int mAnimDuration;
    private final Paint mBackgroundPaint;
    private float mBackgroundPaintAlpha;
    private Drawable mCellRecthairDrawableHor;
    private Drawable mCellRecthairDrawableHor_mini;
    private Drawable mCellRecthairDrawableVerMiddle;
    private Drawable mCellRecthairDrawableVerMiddle_mini;
    private int mCountX;
    private int mCountY;
    private Point[][] mCrossPoint;
    public InterruptibleInOutAnimator mCrosshairsAnimator2;
    private Drawable mCrosshairsDrawable;
    private Rect mDrawRect;
    private int mHeight;
    private TimeInterpolator mInterpolator;
    private int mPaddingBottom;
    private int mPaddingTop;
    private float mScaleX;
    private int mWidth;

    /* JADX WARN: Illegal instructions before constructor call */
    public CrossHairsGrid(Context context) {
        Launcher launcher = (Launcher) context;
        this(context, launcher.getDeviceProfile().inv.numColumns, launcher.getDeviceProfile().inv.numRows);
    }

    public CrossHairsGrid(Context context, int countX, int countY) {
        this.mCellRecthairDrawableHor = null;
        this.mCellRecthairDrawableHor_mini = null;
        this.mCellRecthairDrawableVerMiddle = null;
        this.mCellRecthairDrawableVerMiddle_mini = null;
        this.mCrosshairsDrawable = null;
        this.dashPath = new DashPathEffect(new float[]{5.0f, 1.0f}, 1.2f);
        this.mBackgroundPaint = new Paint();
        this.mAlpha = 0.0f;
        this.mBackgroundPaintAlpha = 0.0f;
        this.mCrosshairsAnimator2 = null;
        Resources resources = context.getResources();
        updateCrossPoint(countX, countY);
        this.mDrawRect = new Rect();
        this.mAnimDuration = resources.getInteger(R.integer.config_crosshairsFadeInTime);
        this.mInterpolator = new DecelerateInterpolator(2.5f);
    }

    public void updateCrossPoint(int countX, int countY) {
        this.mCountX = countX;
        this.mCountY = countY;
        this.mCrossPoint = (Point[][]) Array.newInstance((Class<?>) Point.class, countX + 1, countY + 1);
        for (int i = 0; i <= this.mCountX; i++) {
            for (int i2 = 0; i2 <= this.mCountY; i2++) {
                this.mCrossPoint[i][i2] = new Point(0, 0);
            }
        }
    }

    private Drawable getDrawable(Context context, int id) {
        Resources resources = context.getResources();
        if (resources != null) {
            Drawable drawable = resources.getDrawable(id);
            drawable.setFilterBitmap(true);
            return drawable;
        }
        LGLog.w(TAG, "Failed to init", new int[0]);
        return DEFAULT_DRAWABLE;
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
    }

    public void initDrawables(Context context) {
        if (this.mCellRecthairDrawableHor == null) {
            this.mCellRecthairDrawableHor = getDrawable(context, R.drawable.lg_homescreen_grid_line_hor);
        }
        if (this.mCellRecthairDrawableHor_mini == null) {
            this.mCellRecthairDrawableHor_mini = getDrawable(context, R.drawable.lg_homescreen_grid_line_hor_mini);
        }
        if (this.mCellRecthairDrawableVerMiddle == null) {
            this.mCellRecthairDrawableVerMiddle = getDrawable(context, R.drawable.lg_homescreen_grid_line_ver_m);
        }
        if (this.mCellRecthairDrawableVerMiddle_mini == null) {
            this.mCellRecthairDrawableVerMiddle_mini = getDrawable(context, R.drawable.lg_homescreen_grid_line_ver_m_mini);
        }
        if (this.mCrosshairsDrawable == null) {
            this.mCrosshairsDrawable = getDrawable(context, R.drawable.lg_homescreen_grid_plus);
        }
    }

    public boolean visible() {
        return this.mAlpha > 0.0f;
    }

    public void draw(Canvas canvas, View view, int defaultHomePadding) {
        this.mScaleX = view.getScaleX();
        this.mWidth = view.getWidth();
        this.mHeight = view.getHeight();
        if (Utilities.isLGUI7_1()) {
            this.mPaddingTop = view.getPaddingTop() + defaultHomePadding;
        } else {
            this.mPaddingTop = view.getPaddingTop();
        }
        this.mPaddingBottom = view.getPaddingBottom();
        Point[][] pointArrCalculateCrossPoints = calculateCrossPoints();
        drawGridBG(canvas);
        drawCrossHairs(canvas, pointArrCalculateCrossPoints);
        if (Build.LGUI_VERSION.RELEASE < 6) {
            drawHorizontalGrid(canvas, pointArrCalculateCrossPoints);
            drawVerticalGrid(canvas, pointArrCalculateCrossPoints);
        }
    }

    private void drawGridBG(Canvas canvas) {
        if (this.mScaleX >= 1.0f) {
            this.mDrawRect.set(2, 2, this.mWidth - 2, this.mHeight - 2);
            this.mBackgroundPaint.setPathEffect(this.dashPath);
            this.mBackgroundPaint.setStyle(Paint.Style.STROKE);
            this.mBackgroundPaint.setStrokeWidth(1.7f);
            this.mBackgroundPaint.setAntiAlias(true);
            this.mBackgroundPaint.setColor(Utilities.sWhite);
            this.mBackgroundPaint.setAlpha((int) (this.mBackgroundPaintAlpha * 255.0f));
            canvas.drawRect(this.mDrawRect, this.mBackgroundPaint);
        }
    }

    private Point[][] calculateCrossPoints() {
        Point[][] pointArr = this.mCrossPoint;
        float f = this.mWidth / this.mCountX;
        float f2 = ((this.mHeight - this.mPaddingTop) - this.mPaddingBottom) / this.mCountY;
        for (int i = 0; i <= this.mCountX; i++) {
            int i2 = 0;
            while (i2 <= this.mCountY) {
                pointArr[i][i2].x = (int) ((i * f) + 0.5f);
                pointArr[i][i2].y = (int) ((i2 * f2) + 0.5f + (i2 == 0 ? 0 : this.mPaddingTop));
                i2++;
            }
        }
        return pointArr;
    }

    private void drawCrossHairs(Canvas canvas, Point[][] crossPoint) {
        Drawable drawable = this.mCrosshairsDrawable;
        int intrinsicWidth = drawable.getIntrinsicWidth() / 2;
        int intrinsicHeight = drawable.getIntrinsicHeight() / 2;
        drawable.setAlpha((int) (this.mAlpha * 255.0f));
        for (int i = 1; i < this.mCountX; i++) {
            for (int i2 = 1; i2 < this.mCountY; i2++) {
                drawable.setBounds(crossPoint[i][i2].x - intrinsicWidth, crossPoint[i][i2].y - intrinsicHeight, crossPoint[i][i2].x + intrinsicWidth, crossPoint[i][i2].y + intrinsicHeight);
                drawable.draw(canvas);
            }
        }
    }

    private void drawHorizontalGrid(Canvas canvas, Point[][] crossPoint) {
        Boolean bool = false;
        Drawable drawable = bool.booleanValue() ? this.mCellRecthairDrawableHor_mini : this.mCellRecthairDrawableHor;
        int i = (crossPoint[1][1].x - crossPoint[0][1].x) / 15;
        int intrinsicHeight = drawable.getIntrinsicHeight() / 2;
        drawable.setAlpha((int) (this.mAlpha * 255.0f));
        for (int i2 = 0; i2 < this.mCountX; i2++) {
            for (int i3 = 1; i3 < this.mCountY; i3++) {
                if (i2 == 0) {
                    int i4 = i2 + 1;
                    drawable.setBounds(crossPoint[i2][i3].x + i, crossPoint[i2][i3].y - intrinsicHeight, crossPoint[i4][i3].x - (i * 2), crossPoint[i4][i3].y + intrinsicHeight);
                } else if (i2 == this.mCountX - 1) {
                    int i5 = i2 + 1;
                    drawable.setBounds(crossPoint[i2][i3].x + (i * 2), crossPoint[i2][i3].y - intrinsicHeight, crossPoint[i5][i3].x - i, crossPoint[i5][i3].y + intrinsicHeight);
                } else {
                    int i6 = i * 2;
                    int i7 = i2 + 1;
                    drawable.setBounds(crossPoint[i2][i3].x + i6, crossPoint[i2][i3].y - intrinsicHeight, crossPoint[i7][i3].x - i6, crossPoint[i7][i3].y + intrinsicHeight);
                }
                drawable.draw(canvas);
            }
        }
    }

    private void drawVerticalGrid(Canvas canvas, Point[][] crossPoint) {
        Boolean bool = false;
        Drawable drawable = bool.booleanValue() ? this.mCellRecthairDrawableVerMiddle_mini : this.mCellRecthairDrawableVerMiddle;
        int intrinsicWidth = drawable.getIntrinsicWidth() / 2;
        int i = (crossPoint[1][1].y - crossPoint[1][0].y) / 15;
        drawable.setAlpha((int) (this.mAlpha * 255.0f));
        for (int i2 = 0; i2 < this.mCountY; i2++) {
            for (int i3 = 1; i3 < this.mCountX; i3++) {
                if (i2 == 0) {
                    int i4 = i2 + 1;
                    drawable.setBounds(crossPoint[i3][i2].x - intrinsicWidth, crossPoint[i3][i2].y + i, crossPoint[i3][i4].x + intrinsicWidth, crossPoint[i3][i4].y - (i * 2));
                } else if (i2 == this.mCountY - 1) {
                    int i5 = i2 + 1;
                    drawable.setBounds(crossPoint[i3][i2].x - intrinsicWidth, crossPoint[i3][i2].y + (i * 2), crossPoint[i3][i5].x + intrinsicWidth, crossPoint[i3][i5].y - i);
                } else {
                    int i6 = i * 2;
                    int i7 = i2 + 1;
                    drawable.setBounds(crossPoint[i3][i2].x - intrinsicWidth, crossPoint[i3][i2].y + i6, crossPoint[i3][i7].x + intrinsicWidth, crossPoint[i3][i7].y - i6);
                }
                drawable.draw(canvas);
            }
        }
    }

    public InterruptibleInOutAnimator setAnimator(final View parent, final int type) {
        InterruptibleInOutAnimator interruptibleInOutAnimator = new InterruptibleInOutAnimator(parent, this.mAnimDuration, 0.0f, type != 0 ? type != 1 ? 0.0f : 0.4f : 1.0f);
        interruptibleInOutAnimator.getAnimator().addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.views.CrossHairsGrid.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                Float f = (Float) animation.getAnimatedValue();
                if (f != null) {
                    int i = type;
                    if (i == 0) {
                        CrossHairsGrid.this.mAlpha = f.floatValue();
                    } else if (i == 1) {
                        CrossHairsGrid.this.mBackgroundPaintAlpha = f.floatValue();
                    }
                }
                parent.invalidate();
            }
        });
        interruptibleInOutAnimator.getAnimator().setInterpolator(this.mInterpolator);
        return interruptibleInOutAnimator;
    }
}
