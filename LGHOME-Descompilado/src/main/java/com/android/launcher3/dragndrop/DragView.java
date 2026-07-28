package com.android.launcher3.dragndrop;

import android.animation.Animator;
import android.animation.FloatArrayEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DragView extends View {
    public static final int VIEW_ZOOM_DURATION = 150;
    ValueAnimator mAnim;
    private int mAnimatedShiftX;
    private int mAnimatedShiftY;
    private Drawable mBadge;
    private ColorMatrixColorFilter mBaseFilter;
    private Drawable mBgSpringDrawable;
    private Bitmap mBitmap;
    private ValueAnimator mBounceAnimator;
    private Bitmap mCrossFadeBitmap;
    float mCrossFadeProgress;
    float[] mCurrentFilter;
    private DragLayer mDragLayer;
    private Rect mDragRegion;
    private Point mDragVisualizeOffset;
    private Rect mFgOriginalBounds;
    private Drawable mFgSpringDrawable;
    private ValueAnimator mFilterAnimator;
    private boolean mHasDrawn;
    private float mInitialScale;
    private float mIntrinsicIconScale;
    private int mLastTouchX;
    private int mLastTouchY;
    private Launcher mLauncher;
    float mOffsetX;
    float mOffsetY;
    Paint mPaint;
    private int mRegistrationX;
    private int mRegistrationY;
    private Path mScaledMaskPath;
    private final int[] mTempLoc;
    private SpringFloatValue mTranslateX;
    private SpringFloatValue mTranslateY;
    private static final ColorMatrix sTempMatrix1 = new ColorMatrix();
    private static final ColorMatrix sTempMatrix2 = new ColorMatrix();
    public static float ADAPTIVE_ICON_SHRINK_RATIO_FOR_ANTIALIAS = 0.98f;
    public static int COLOR_CHANGE_DURATION = 120;
    static float sDragAlpha = 1.0f;

    public DragView(Launcher launcher, Bitmap bitmap, int registrationX, int registrationY, int left, int top, int width, int height, final float initialScale) {
        super(launcher);
        this.mDragVisualizeOffset = null;
        this.mDragRegion = null;
        this.mDragLayer = null;
        this.mHasDrawn = false;
        this.mCrossFadeProgress = 0.0f;
        this.mOffsetX = 0.0f;
        this.mOffsetY = 0.0f;
        this.mInitialScale = 1.0f;
        this.mIntrinsicIconScale = 1.0f;
        this.mTempLoc = new int[2];
        this.mLauncher = launcher;
        this.mDragLayer = launcher.getDragLayer();
        this.mInitialScale = initialScale;
        float f = width;
        final float dimensionPixelSize = (getResources().getDimensionPixelSize(R.dimen.dragViewScale) + f) / f;
        setScaleX(initialScale);
        setScaleY(initialScale);
        ValueAnimator valueAnimatorOfFloat = LauncherAnimUtils.ofFloat(this, 0.0f, 1.0f);
        this.mAnim = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(150L);
        this.mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.dragndrop.DragView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float fFloatValue = ((Float) animation.getAnimatedValue()).floatValue();
                int i = (int) (-DragView.this.mOffsetX);
                int i2 = (int) (-DragView.this.mOffsetY);
                float f2 = i;
                DragView.this.mOffsetX += f2;
                float f3 = i2;
                DragView.this.mOffsetY += f3;
                DragView dragView = DragView.this;
                float f4 = initialScale;
                dragView.setScaleX(f4 + ((dimensionPixelSize - f4) * fFloatValue));
                DragView dragView2 = DragView.this;
                float f5 = initialScale;
                dragView2.setScaleY(f5 + ((dimensionPixelSize - f5) * fFloatValue));
                if (DragView.sDragAlpha != 1.0f) {
                    DragView.this.setAlpha((DragView.sDragAlpha * fFloatValue) + (1.0f - fFloatValue));
                }
                if (DragView.this.getParent() == null) {
                    animation.cancel();
                    return;
                }
                DragView dragView3 = DragView.this;
                dragView3.setTranslationX(dragView3.getTranslationX() + f2);
                DragView dragView4 = DragView.this;
                dragView4.setTranslationY(dragView4.getTranslationY() + f3);
            }
        });
        this.mBitmap = Bitmap.createBitmap(bitmap, left, top, width, height);
        setDragRegion(new Rect(0, 0, width, height));
        this.mRegistrationX = registrationX;
        this.mRegistrationY = registrationY;
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        measure(iMakeMeasureSpec, iMakeMeasureSpec);
        this.mPaint = new Paint(2);
        if (Utilities.isLmpOrAbove()) {
            setElevation(getResources().getDimension(R.dimen.drag_elevation));
        }
    }

    public void setItemInfo(final ItemInfo info) {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVEICON_ANIMATION.getValue()) {
            if ((info.itemType == 0 || info.itemType == 6 || info.itemType == 2) && !LiveIconManager.getInstance(this.mContext).hasLiveIcon(info.getTargetComponent())) {
                if (this.mLauncher.getWorkspace() == null || !this.mLauncher.getWorkspace().isInOverviewMode()) {
                    if ((this.mLauncher.getAllAppsHost() == null || !this.mLauncher.getAllAppsHost().isInArrangeMode()) && info.getIntent() != null) {
                        new Handler(LauncherModel.getWorkerLooper()).postAtFrontOfQueue(new Runnable() { // from class: com.android.launcher3.dragndrop.DragView.2
                            @Override // java.lang.Runnable
                            public void run() {
                                LauncherAppState launcherAppState = LauncherAppState.getInstance(DragView.this.mLauncher);
                                Object[] objArr = new Object[1];
                                Drawable fullDrawable = DragView.getFullDrawable(info, launcherAppState, objArr, DragView.this.mLauncher);
                                if (fullDrawable instanceof AdaptiveIconDrawable) {
                                    int width = DragView.this.mBitmap.getWidth();
                                    int height = DragView.this.mBitmap.getHeight();
                                    int dimension = ((int) DragView.this.mLauncher.getResources().getDimension(R.dimen.blur_size_medium_outline)) / 2;
                                    int integer = DragView.this.getResources().getInteger(R.integer.config_adaptiveicon_additional_length);
                                    int i = integer + 0;
                                    Rect rect = new Rect(i, i, width + integer, integer + height);
                                    rect.inset(dimension, dimension);
                                    Rect rect2 = new Rect(rect);
                                    DragView dragView = DragView.this;
                                    dragView.mBadge = DragView.getBadge(info, launcherAppState, objArr[0], dragView.mLauncher);
                                    DragView.this.mBadge.setBounds(rect2);
                                    Utilities.scaleRectAboutCenter(rect, 0.9599999f);
                                    AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) fullDrawable;
                                    Rect rect3 = new Rect(rect);
                                    Utilities.scaleRectAboutCenter(rect3, DragView.ADAPTIVE_ICON_SHRINK_RATIO_FOR_ANTIALIAS);
                                    adaptiveIconDrawable.setBounds(rect3);
                                    final Path iconMask = adaptiveIconDrawable.getIconMask();
                                    DragView.this.mTranslateX = new SpringFloatValue(DragView.this, width * AdaptiveIconDrawable.getExtraInsetFraction());
                                    DragView.this.mTranslateY = new SpringFloatValue(DragView.this, height * AdaptiveIconDrawable.getExtraInsetFraction());
                                    rect.inset((int) ((-rect.width()) * AdaptiveIconDrawable.getExtraInsetFraction()), (int) ((-rect.height()) * AdaptiveIconDrawable.getExtraInsetFraction()));
                                    DragView.this.mBgSpringDrawable = adaptiveIconDrawable.getBackground();
                                    if (DragView.this.mBgSpringDrawable == null) {
                                        DragView.this.mBgSpringDrawable = new ColorDrawable(0);
                                    }
                                    DragView.this.mBgSpringDrawable.setBounds(rect);
                                    DragView.this.mFgSpringDrawable = adaptiveIconDrawable.getForeground();
                                    if (DragView.this.mFgSpringDrawable == null) {
                                        DragView.this.mFgSpringDrawable = new ColorDrawable(0);
                                    }
                                    DragView.this.mFgSpringDrawable.setBounds(rect);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.android.launcher3.dragndrop.DragView.2.1
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            DragView.this.mScaledMaskPath = iconMask;
                                            if (info.isDisabled()) {
                                                FastBitmapDrawable fastBitmapDrawable = new FastBitmapDrawable((Bitmap) null);
                                                fastBitmapDrawable.setIsDisabled(true);
                                                DragView.this.mBaseFilter = (ColorMatrixColorFilter) fastBitmapDrawable.getColorFilter();
                                            }
                                            DragView.this.updateColorFilter();
                                            DragView.this.startBounceAnimation();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateColorFilter() {
        Drawable drawable;
        if (this.mCurrentFilter == null) {
            this.mPaint.setColorFilter(null);
            if (this.mScaledMaskPath != null && (drawable = this.mBgSpringDrawable) != null && this.mFgSpringDrawable != null && this.mBadge != null) {
                drawable.setColorFilter(this.mBaseFilter);
                this.mFgSpringDrawable.setColorFilter(this.mBaseFilter);
                this.mBadge.setColorFilter(this.mBaseFilter);
            }
        } else {
            ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(this.mCurrentFilter);
            this.mPaint.setColorFilter(colorMatrixColorFilter);
            if (this.mScaledMaskPath != null && this.mBgSpringDrawable != null && this.mFgSpringDrawable != null) {
                ColorMatrixColorFilter colorMatrixColorFilter2 = this.mBaseFilter;
                if (colorMatrixColorFilter2 != null) {
                    ColorMatrix colorMatrix = sTempMatrix1;
                    colorMatrixColorFilter2.getColorMatrix(colorMatrix);
                    ColorMatrix colorMatrix2 = sTempMatrix2;
                    colorMatrix2.set(this.mCurrentFilter);
                    colorMatrix.postConcat(colorMatrix2);
                    colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
                }
                this.mBgSpringDrawable.setColorFilter(colorMatrixColorFilter);
                this.mFgSpringDrawable.setColorFilter(colorMatrixColorFilter);
            }
        }
        invalidate();
    }

    public static Drawable getFullDrawable(ItemInfo info, LauncherAppState appState, Object[] outObj, Context context) {
        if (info.itemType == 0) {
            LauncherActivityInfo launcherActivityInfoResolveActivity = LauncherAppsCompat.getInstance(context).resolveActivity(info.getIntent(), info.user);
            outObj[0] = launcherActivityInfoResolveActivity;
            if (launcherActivityInfoResolveActivity != null) {
                return appState.getIconCache().getFullResIcon(launcherActivityInfoResolveActivity);
            }
            return null;
        }
        if (info.itemType != 6) {
            return null;
        }
        if (info instanceof PendingAddShortcutInfo) {
            ShortcutConfigActivityInfo shortcutConfigActivityInfo = ((PendingAddShortcutInfo) info).activityInfo;
            outObj[0] = shortcutConfigActivityInfo;
            return shortcutConfigActivityInfo.getFullResIcon(appState.getIconCache());
        }
        ShortcutKey shortcutKeyFromItemInfo = ShortcutKey.fromItemInfo(info);
        DeepShortcutManager deepShortcutManager = DeepShortcutManager.getInstance(context);
        List<ShortcutInfoCompat> listQueryForFullDetails = deepShortcutManager.queryForFullDetails(shortcutKeyFromItemInfo.componentName.getPackageName(), Arrays.asList(shortcutKeyFromItemInfo.getId()), shortcutKeyFromItemInfo.user);
        if (listQueryForFullDetails.isEmpty()) {
            return null;
        }
        outObj[0] = listQueryForFullDetails.get(0);
        return deepShortcutManager.getShortcutIconDrawable(listQueryForFullDetails.get(0), appState.getInvariantDeviceProfile().fillResIconDpi);
    }

    public static Drawable getBadge(ItemInfo info, LauncherAppState appState, Object obj, Context context) {
        int i = appState.getInvariantDeviceProfile().iconBitmapSize;
        if (info.itemType != 6) {
            return context.getPackageManager().getUserBadgedIcon(new FixedSizeEmptyDrawable(i), info.user);
        }
        if (info.id == -1 || !(obj instanceof ShortcutInfoCompat)) {
            return new FixedSizeEmptyDrawable(i);
        }
        Bitmap shortcutInfoBadge = LauncherIcons.getShortcutInfoBadge((ShortcutInfoCompat) obj, appState.getIconCache());
        float f = i;
        float dimension = (f - context.getResources().getDimension(R.dimen.deep_shortcuts_badge)) / f;
        return new InsetDrawable(new FastBitmapDrawable(shortcutInfoBadge), dimension, dimension, 0.0f, 0.0f);
    }

    public void setIntrinsicIconScaleFactor(float scale) {
        this.mIntrinsicIconScale = scale;
    }

    public float getIntrinsicIconScaleFactor() {
        return this.mIntrinsicIconScale;
    }

    public float getOffsetY() {
        return this.mOffsetY;
    }

    public int getDragRegionLeft() {
        return this.mDragRegion.left;
    }

    public int getDragRegionTop() {
        return this.mDragRegion.top;
    }

    public int getDragRegionWidth() {
        return this.mDragRegion.width();
    }

    public int getDragRegionHeight() {
        return this.mDragRegion.height();
    }

    public void setDragVisualizeOffset(Point p) {
        this.mDragVisualizeOffset = p;
    }

    public Point getDragVisualizeOffset() {
        return this.mDragVisualizeOffset;
    }

    public void setDragRegion(Rect r) {
        this.mDragRegion = r;
    }

    public Rect getDragRegion() {
        return this.mDragRegion;
    }

    public float getInitialScale() {
        return this.mInitialScale;
    }

    private static class SpringFloatValue {
        private static final float DAMPENING_RATIO = 1.0f;
        private static final int PARALLAX_MAX_IN_DP = 8;
        private static final int STIFFNESS = 4000;
        private static final FloatPropertyCompat<SpringFloatValue> VALUE = new FloatPropertyCompat<SpringFloatValue>("value") { // from class: com.android.launcher3.dragndrop.DragView.SpringFloatValue.1
            /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
            @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
            public float getValue(SpringFloatValue object) {
                return object.mValue;
            }

            /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
            @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
            public void setValue(SpringFloatValue object, float value) {
                object.mValue = value;
                object.mView.invalidate();
            }
        };
        private final float mDelta;
        private final SpringAnimation mSpring;
        private float mValue;
        private final View mView;

        public SpringFloatValue(View view, float range) {
            this.mView = view;
            this.mSpring = new SpringAnimation(this, VALUE, 0.0f).setMinValue(-range).setMaxValue(range).setSpring(new SpringForce(0.0f).setDampingRatio(1.0f).setStiffness(4000.0f));
            this.mDelta = view.getResources().getDisplayMetrics().density * 8.0f;
        }

        public void animateToPos(float value) {
            SpringAnimation springAnimation = this.mSpring;
            float f = this.mDelta;
            springAnimation.animateToFinalPosition(Utilities.boundToRange(value, -f, f));
        }
    }

    public static class FixedSizeEmptyDrawable extends ColorDrawable {
        private final int mSize;

        public FixedSizeEmptyDrawable(int size) {
            super(0);
            this.mSize = size;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return this.mSize;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return this.mSize;
        }
    }

    public void updateInitialScaleToCurrentScale() {
        this.mInitialScale = getScaleX();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(this.mBitmap.getWidth(), this.mBitmap.getHeight());
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mHasDrawn = true;
        float f = this.mCrossFadeProgress;
        boolean z = f > 0.0f && this.mCrossFadeBitmap != null;
        if (z) {
            this.mPaint.setAlpha(z ? (int) ((1.0f - f) * 255.0f) : 255);
        }
        canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaint);
        if (z) {
            this.mPaint.setAlpha((int) (this.mCrossFadeProgress * 255.0f));
            canvas.save();
            canvas.scale((this.mBitmap.getWidth() * 1.0f) / this.mCrossFadeBitmap.getWidth(), (this.mBitmap.getHeight() * 1.0f) / this.mCrossFadeBitmap.getHeight());
            canvas.drawBitmap(this.mCrossFadeBitmap, 0.0f, 0.0f, this.mPaint);
            canvas.restore();
        }
        if (this.mScaledMaskPath != null) {
            int iSave = canvas.save();
            canvas.clipPath(this.mScaledMaskPath);
            this.mBgSpringDrawable.draw(canvas);
            canvas.translate(this.mTranslateX.mValue, this.mTranslateY.mValue);
            this.mFgSpringDrawable.draw(canvas);
            canvas.restoreToCount(iSave);
            this.mBadge.draw(canvas);
        }
    }

    public void setCrossFadeBitmap(Bitmap crossFadeBitmap) {
        this.mCrossFadeBitmap = crossFadeBitmap;
    }

    public void crossFade(int duration) {
        ValueAnimator valueAnimatorOfFloat = LauncherAnimUtils.ofFloat(this, 0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(duration);
        valueAnimatorOfFloat.setInterpolator(new DecelerateInterpolator(1.5f));
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.dragndrop.DragView.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                DragView.this.mCrossFadeProgress = animation.getAnimatedFraction();
            }
        });
        valueAnimatorOfFloat.start();
    }

    public void setColor(int color) {
        if (this.mPaint == null) {
            this.mPaint = new Paint(2);
        }
        if (color != 0) {
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.0f);
            ColorMatrix colorMatrix2 = new ColorMatrix();
            setColorScale(color, colorMatrix2);
            colorMatrix.postConcat(colorMatrix2);
            if (Utilities.isLmpOrAbove()) {
                animateFilterTo(colorMatrix.getArray());
                return;
            } else {
                this.mPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                invalidate();
                return;
            }
        }
        if (!Utilities.isLmpOrAbove() || this.mCurrentFilter == null) {
            this.mPaint.setColorFilter(null);
            invalidate();
        } else {
            animateFilterTo(new ColorMatrix().getArray());
        }
    }

    private void animateFilterTo(float[] targetFilter) {
        float[] array = this.mCurrentFilter;
        if (array == null) {
            array = new ColorMatrix().getArray();
        }
        this.mCurrentFilter = Arrays.copyOf(array, array.length);
        ValueAnimator valueAnimator = this.mFilterAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimatorOfObject = ValueAnimator.ofObject(new FloatArrayEvaluator(this.mCurrentFilter), array, targetFilter);
        this.mFilterAnimator = valueAnimatorOfObject;
        valueAnimatorOfObject.setDuration(COLOR_CHANGE_DURATION);
        this.mFilterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.dragndrop.DragView.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                DragView.this.mPaint.setColorFilter(new ColorMatrixColorFilter(DragView.this.mCurrentFilter));
                DragView.this.invalidate();
            }
        });
        this.mFilterAnimator.start();
    }

    public boolean hasDrawn() {
        return this.mHasDrawn;
    }

    @Override // android.view.View
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.mPaint.setAlpha((int) (alpha * 255.0f));
        invalidate();
    }

    public void show(int touchX, int touchY) {
        this.mDragLayer.addView(this);
        BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(0, 0);
        layoutParams.width = this.mBitmap.getWidth();
        layoutParams.height = this.mBitmap.getHeight();
        layoutParams.customPosition = true;
        setLayoutParams(layoutParams);
        setTranslationX(touchX - this.mRegistrationX);
        setTranslationY(touchY - this.mRegistrationY);
        post(new Runnable() { // from class: com.android.launcher3.dragndrop.DragView.5
            @Override // java.lang.Runnable
            public void run() {
                DragView.this.mAnim.start();
            }
        });
    }

    public void cancelAnimation() {
        ValueAnimator valueAnimator = this.mAnim;
        if (valueAnimator == null || !valueAnimator.isRunning()) {
            return;
        }
        this.mAnim.cancel();
    }

    public void resetLayoutParams() {
        this.mOffsetY = 0.0f;
        this.mOffsetX = 0.0f;
        requestLayout();
    }

    void move(int touchX, int touchY) {
        int i;
        if (touchX > 0 && touchY > 0 && (i = this.mLastTouchX) > 0 && this.mLastTouchY > 0 && this.mScaledMaskPath != null) {
            this.mTranslateX.animateToPos(i - touchX);
            this.mTranslateY.animateToPos(this.mLastTouchY - touchY);
        }
        this.mLastTouchX = touchX;
        this.mLastTouchY = touchY;
        setTranslationX((touchX - this.mRegistrationX) + ((int) this.mOffsetX));
        setTranslationY((touchY - this.mRegistrationY) + ((int) this.mOffsetY));
    }

    public void remove() {
        if (getParent() != null) {
            this.mDragLayer.removeView(this);
        }
    }

    public static void setColorScale(int color, ColorMatrix target) {
        target.setScale(Color.red(color) / 255.0f, Color.green(color) / 255.0f, Color.blue(color) / 255.0f, Color.alpha(color) / 255.0f);
    }

    public void animateShift(final int shiftX, final int shiftY) {
        if (this.mAnim.isStarted()) {
            return;
        }
        this.mAnimatedShiftX = shiftX;
        this.mAnimatedShiftY = shiftY;
        applyTranslation();
        this.mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.dragndrop.DragView.6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = 1.0f - animation.getAnimatedFraction();
                DragView.this.mAnimatedShiftX = (int) (shiftX * animatedFraction);
                DragView.this.mAnimatedShiftY = (int) (animatedFraction * shiftY);
                DragView.this.applyTranslation();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyTranslation() {
        setTranslationX((this.mLastTouchX - this.mRegistrationX) + this.mAnimatedShiftX);
        setTranslationY((this.mLastTouchY - this.mRegistrationY) + this.mAnimatedShiftY);
    }

    public void animateTo(int toTouchX, int toTouchY, Runnable onCompleteRunnable, int duration) {
        int[] iArr = this.mTempLoc;
        iArr[0] = toTouchX - this.mRegistrationX;
        iArr[1] = toTouchY - this.mRegistrationY;
        DragLayer dragLayer = this.mDragLayer;
        float f = this.mInitialScale;
        dragLayer.animateViewIntoPosition(this, iArr, 1.0f, f, f, 0, onCompleteRunnable, duration);
    }

    public void startBounceAnimation() {
        ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(0, 20);
        this.mBounceAnimator = valueAnimatorOfInt;
        valueAnimatorOfInt.setDuration(300L);
        this.mFgOriginalBounds = new Rect(this.mFgSpringDrawable.getBounds());
        this.mBounceAnimator.setRepeatCount(1);
        this.mBounceAnimator.setRepeatMode(2);
        this.mBounceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.dragndrop.DragView.7
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                int i = Integer.parseInt(animation.getAnimatedValue().toString());
                DragView.this.mFgSpringDrawable.setBounds(DragView.this.mFgOriginalBounds.left + i, DragView.this.mFgOriginalBounds.top + i, DragView.this.mFgOriginalBounds.right - i, DragView.this.mFgOriginalBounds.bottom - i);
                DragView.this.invalidate();
            }
        });
        this.mBounceAnimator.addListener(new Animator.AnimatorListener() { // from class: com.android.launcher3.dragndrop.DragView.8
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
            }
        });
        this.mBounceAnimator.start();
    }
}
