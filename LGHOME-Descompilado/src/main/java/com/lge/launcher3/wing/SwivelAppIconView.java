package com.lge.launcher3.wing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.ShortcutInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.wing.SwivelAppIconCache;
import com.lge.launcher3.wing.carousel.util.CarouselGraphicUtils;

/* JADX INFO: loaded from: classes2.dex */
public class SwivelAppIconView extends BubbleTextView {
    private static final String TAG = "SwivelAppIconView";
    final int mBgHeight;
    final int mBgWidth;
    final float mConerRoundFactor;
    final float[] mConerRoundRadius;
    private Context mContext;
    final int mFgHeight;
    final int mFgWidth;
    final int mIconHeight;
    final int mIconWidth;
    Drawable mLeftShadowDrawable;
    private Drawable mNonAdaptiveFgDrawable;
    private final Paint mPaint;
    private Bitmap mReflection;
    Drawable mRightShadowDrawable;
    private final int mShadowLength;
    private boolean mShowReflection;

    public SwivelAppIconView(Context context) {
        super(context);
        this.mPaint = new Paint();
        this.mShowReflection = true;
        this.mConerRoundFactor = 30.0f;
        this.mConerRoundRadius = new float[]{30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f};
        this.mFgWidth = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_fg_width);
        this.mFgHeight = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_fg_height);
        this.mBgWidth = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_width);
        this.mBgHeight = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_height);
        this.mIconWidth = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_width);
        this.mIconHeight = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_height);
        this.mShadowLength = 10;
        this.mContext = context;
    }

    public SwivelAppIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaint = new Paint();
        this.mShowReflection = true;
        this.mConerRoundFactor = 30.0f;
        this.mConerRoundRadius = new float[]{30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f};
        this.mFgWidth = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_fg_width);
        this.mFgHeight = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_fg_height);
        this.mBgWidth = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_width);
        this.mBgHeight = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_height);
        this.mIconWidth = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_width);
        this.mIconHeight = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_height);
        this.mShadowLength = 10;
        this.mContext = context;
    }

    public SwivelAppIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaint = new Paint();
        this.mShowReflection = true;
        this.mConerRoundFactor = 30.0f;
        this.mConerRoundRadius = new float[]{30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f};
        this.mFgWidth = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_fg_width);
        this.mFgHeight = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_fg_height);
        this.mBgWidth = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_width);
        this.mBgHeight = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_height);
        this.mIconWidth = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_width);
        this.mIconHeight = getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_height);
        this.mShadowLength = 10;
        this.mContext = context;
    }

    @Override // com.android.launcher3.BubbleTextView
    protected void setSwivelIcon(ShortcutInfo info) {
        SwivelAppIconCache.SwivelAppIconCacheEntry swivelAppIconCacheEntry = SwivelAppIconCache.getInstance(this.mContext).lambda$fillCache$1$SwivelAppIconCache(info);
        if (swivelAppIconCacheEntry == null) {
            return;
        }
        this.mIcon = swivelAppIconCacheEntry.icon;
        if (swivelAppIconCacheEntry.isAdaptiveIcon) {
            this.mFgSpringDrawable = swivelAppIconCacheEntry.adaptiveFgDrawable;
            this.mRightShadowDrawable = swivelAppIconCacheEntry.bgDrawableWithRightShadow;
            this.mLeftShadowDrawable = swivelAppIconCacheEntry.bgDrawableWithLeftShadow;
            this.mReflection = swivelAppIconCacheEntry.reflection;
            this.mScaledMaskPath = getBgRoundedRectPath();
            this.mIcon.setAlpha(0);
        } else if (this.mIcon != null) {
            this.mScaledMaskPath = null;
            this.mNonAdaptiveFgDrawable = swivelAppIconCacheEntry.nonAdaptiveFgDrawable;
            this.mRightShadowDrawable = swivelAppIconCacheEntry.bgDrawableWithRightShadow;
            this.mLeftShadowDrawable = swivelAppIconCacheEntry.bgDrawableWithLeftShadow;
            this.mReflection = swivelAppIconCacheEntry.reflection;
            this.mIcon.setAlpha(0);
        } else {
            Log.d(TAG, "mIcon is null");
        }
        applyCompoundDrawables(this.mIcon);
    }

    private Path getBgRoundedRectPath() {
        Path path = new Path();
        path.addRoundRect(new RectF(0.0f, 0.0f, this.mBgWidth, this.mBgHeight), this.mConerRoundRadius, Path.Direction.CW);
        return path;
    }

    private Path getFgRoundedRectPath() {
        Path path = new Path();
        int i = this.mBgWidth;
        int i2 = this.mIconWidth;
        int i3 = this.mBgHeight;
        int i4 = this.mIconHeight;
        path.addRoundRect(new RectF((i - i2) / 2, (i3 - i4) / 2, (i + i2) / 2, (i3 + i4) / 2), this.mConerRoundRadius, Path.Direction.CW);
        return path;
    }

    @Override // com.android.launcher3.BubbleTextView, android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mScaledMaskPath != null) {
            int iSave = canvas.save();
            if (this.mShowReflection) {
                this.mPaint.reset();
                this.mPaint.setAntiAlias(true);
                this.mPaint.setFilterBitmap(true);
                canvas.drawBitmap(this.mReflection, 0.0f, this.mBgHeight - CarouselGraphicUtils.getReflectionGap(), this.mPaint);
            }
            if (getRotationY() > 5.0f) {
                this.mLeftShadowDrawable.draw(canvas);
            } else {
                this.mRightShadowDrawable.draw(canvas);
            }
            this.mFgSpringDrawable.draw(canvas);
            canvas.restoreToCount(iSave);
        }
        if (this.mIcon != null && !(this.mIcon instanceof AdaptiveIconDrawable)) {
            int iSave2 = canvas.save();
            if (this.mShowReflection) {
                this.mPaint.reset();
                this.mPaint.setAntiAlias(true);
                this.mPaint.setFilterBitmap(true);
                canvas.drawBitmap(this.mReflection, 0.0f, this.mBgHeight - CarouselGraphicUtils.getReflectionGap(), this.mPaint);
            }
            if (getRotationY() > 0.0f) {
                this.mLeftShadowDrawable.draw(canvas);
            } else {
                this.mRightShadowDrawable.draw(canvas);
            }
            this.mNonAdaptiveFgDrawable.draw(canvas);
            canvas.restoreToCount(iSave2);
        }
        drawBadge(canvas);
    }

    public void showReflection(boolean show) {
        this.mShowReflection = show;
    }
}
