package com.android.quickstep.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.util.MultiValueUpdateListener;
import com.android.quickstep.util.TaskCornerRadius;
import com.android.systemui.shared.system.QuickStepContract;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class FloatingTaskView extends FrameLayout {
    private final StatefulActivity mActivity;
    private final FullscreenDrawParams mFullscreenParams;
    private final boolean mIsRtl;
    private PagedOrientationHandler mOrientationHandler;
    private SplitPlaceholderView mSplitPlaceholderView;
    private int mStagePosition;
    private RectF mStartingPosition;
    private FloatingTaskThumbnailView mThumbnailView;

    public FloatingTaskView(Context context) {
        this(context, null);
    }

    public FloatingTaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mActivity = (StatefulActivity) BaseActivity.fromContext(context);
        this.mIsRtl = Utilities.isRtl(getResources());
        this.mFullscreenParams = new FullscreenDrawParams(context);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mThumbnailView = (FloatingTaskThumbnailView) findViewById(R.id.thumbnail);
        SplitPlaceholderView splitPlaceholderView = (SplitPlaceholderView) findViewById(R.id.split_placeholder);
        this.mSplitPlaceholderView = splitPlaceholderView;
        splitPlaceholderView.setAlpha(0.0f);
    }

    private void init(StatefulActivity launcher, View originalView, Bitmap thumbnail, Drawable icon, RectF positionOut) {
        this.mStartingPosition = positionOut;
        updateInitialPositionForView(originalView);
        InsettableFrameLayout.LayoutParams layoutParams = (InsettableFrameLayout.LayoutParams) getLayoutParams();
        this.mSplitPlaceholderView.setLayoutParams(new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height));
        setPivotX(0.0f);
        setPivotY(0.0f);
        this.mThumbnailView.setThumbnail(thumbnail);
        RecentsView recentsView = (RecentsView) launcher.getOverviewPanel();
        this.mOrientationHandler = recentsView.getPagedOrientationHandler();
        this.mStagePosition = recentsView.getSplitPlaceholder().getActiveSplitStagePosition();
        this.mSplitPlaceholderView.setIcon(icon, getResources().getDimensionPixelSize(R.dimen.split_placeholder_icon_size));
        this.mSplitPlaceholderView.getIconView().setRotation(this.mOrientationHandler.getDegreesRotated());
    }

    public static FloatingTaskView getFloatingTaskView(StatefulActivity launcher, View originalView, Bitmap thumbnail, Drawable icon, RectF positionOut) {
        ViewGroup viewGroup = (ViewGroup) launcher.getDragLayer().getParent();
        FloatingTaskView floatingTaskView = (FloatingTaskView) launcher.getLayoutInflater().inflate(R.layout.floating_split_select_view, viewGroup, false);
        floatingTaskView.init(launcher, originalView, thumbnail, icon, positionOut);
        viewGroup.addView(floatingTaskView);
        return floatingTaskView;
    }

    public void updateInitialPositionForView(View originalView) {
        Utilities.getBoundsForViewInDragLayer(this.mActivity.getDragLayer(), originalView, new Rect(0, 0, originalView.getWidth(), originalView.getHeight()), false, null, this.mStartingPosition);
        InsettableFrameLayout.LayoutParams layoutParams = new InsettableFrameLayout.LayoutParams(Math.round(this.mStartingPosition.width()), Math.round(this.mStartingPosition.height()));
        initPosition(this.mStartingPosition, layoutParams);
        setLayoutParams(layoutParams);
    }

    public void update(RectF bounds, float progress) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        float f = bounds.left - this.mStartingPosition.left;
        float f2 = bounds.top - marginLayoutParams.topMargin;
        float fWidth = bounds.width() / marginLayoutParams.width;
        float fHeight = bounds.height() / marginLayoutParams.height;
        this.mFullscreenParams.updateParams(bounds, progress, fWidth, fHeight);
        setTranslationX(f);
        setTranslationY(f2);
        setScaleX(fWidth);
        setScaleY(fHeight);
        this.mSplitPlaceholderView.invalidate();
        this.mThumbnailView.invalidate();
        this.mOrientationHandler.setPrimaryScale(this.mSplitPlaceholderView.getIconView(), 1.0f / fWidth);
        this.mOrientationHandler.setSecondaryScale(this.mSplitPlaceholderView.getIconView(), 1.0f / fHeight);
    }

    public void updateOrientationHandler(PagedOrientationHandler orientationHandler) {
        this.mOrientationHandler = orientationHandler;
        this.mSplitPlaceholderView.getIconView().setRotation(this.mOrientationHandler.getDegreesRotated());
    }

    protected void initPosition(RectF pos, InsettableFrameLayout.LayoutParams lp) {
        this.mStartingPosition.set(pos);
        lp.ignoreInsets = true;
        lp.topMargin = Math.round(pos.top);
        if (this.mIsRtl) {
            lp.setMarginStart(this.mActivity.getDeviceProfile().widthPx - Math.round(pos.right));
        } else {
            lp.setMarginStart(Math.round(pos.left));
        }
        int i = (int) pos.left;
        layout(i, lp.topMargin, lp.width + i, lp.topMargin + lp.height);
    }

    public void addAnimation(PendingAnimation animation, RectF startingBounds, Rect endBounds, boolean fadeWithThumbnail, boolean isStagedTask) {
        this.mFullscreenParams.setIsStagedTask(isStagedTask);
        int[] iArr = new int[2];
        this.mActivity.getDragLayer().getLocationOnScreen(iArr);
        SplitOverlayProperties splitOverlayProperties = new SplitOverlayProperties(endBounds, startingBounds, iArr[0], iArr[1]);
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        animation.add(valueAnimatorOfFloat);
        long duration = animation.getDuration();
        RectF rectF = new RectF();
        if (fadeWithThumbnail) {
            animation.addFloat(this.mSplitPlaceholderView, SplitPlaceholderView.ALPHA_FLOAT, 0.0f, 1.0f, Interpolators.ACCEL);
            animation.addFloat(this.mThumbnailView, LauncherAnimUtils.VIEW_ALPHA, 1.0f, 0.0f, Interpolators.DEACCEL_3);
        } else if (isStagedTask && this.mSplitPlaceholderView.getAlpha() == 0.0f) {
            animation.addFloat(this.mSplitPlaceholderView, SplitPlaceholderView.ALPHA_FLOAT, 0.3f, 1.0f, Interpolators.ACCEL);
        }
        valueAnimatorOfFloat.addUpdateListener(new MultiValueUpdateListener(splitOverlayProperties, duration, rectF, startingBounds) { // from class: com.android.quickstep.views.FloatingTaskView.1
            final MultiValueUpdateListener.FloatProp mDx;
            final MultiValueUpdateListener.FloatProp mDy;
            final MultiValueUpdateListener.FloatProp mTaskViewScaleX;
            final MultiValueUpdateListener.FloatProp mTaskViewScaleY;
            final /* synthetic */ long val$animDuration;
            final /* synthetic */ RectF val$floatingTaskViewBounds;
            final /* synthetic */ SplitOverlayProperties val$prop;
            final /* synthetic */ RectF val$startingBounds;

            {
                this.val$prop = splitOverlayProperties;
                this.val$animDuration = duration;
                this.val$floatingTaskViewBounds = rectF;
                this.val$startingBounds = startingBounds;
                this.mDx = new MultiValueUpdateListener.FloatProp(0.0f, splitOverlayProperties.dX, 0.0f, duration, Interpolators.LINEAR);
                this.mDy = new MultiValueUpdateListener.FloatProp(0.0f, splitOverlayProperties.dY, 0.0f, duration, Interpolators.LINEAR);
                this.mTaskViewScaleX = new MultiValueUpdateListener.FloatProp(1.0f, splitOverlayProperties.finalTaskViewScaleX, 0.0f, duration, Interpolators.LINEAR);
                this.mTaskViewScaleY = new MultiValueUpdateListener.FloatProp(1.0f, splitOverlayProperties.finalTaskViewScaleY, 0.0f, duration, Interpolators.LINEAR);
            }

            @Override // com.android.quickstep.util.MultiValueUpdateListener
            public void onUpdate(float percent) {
                this.val$floatingTaskViewBounds.set(this.val$startingBounds);
                this.val$floatingTaskViewBounds.offset(this.mDx.value, this.mDy.value);
                Utilities.scaleRectFAboutCenter(this.val$floatingTaskViewBounds, this.mTaskViewScaleX.value, this.mTaskViewScaleY.value);
                FloatingTaskView.this.update(this.val$floatingTaskViewBounds, percent);
            }
        });
    }

    void drawRoundedRect(Canvas canvas, Paint paint) {
        if (this.mFullscreenParams == null) {
            return;
        }
        canvas.drawRoundRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.mFullscreenParams.mCurrentDrawnCornerRadius / this.mFullscreenParams.mScaleX, this.mFullscreenParams.mCurrentDrawnCornerRadius / this.mFullscreenParams.mScaleY, paint);
    }

    void centerIconView(IconView iconView, float onScreenRectCenterX, float onScreenRectCenterY) {
        this.mOrientationHandler.updateStagedSplitIconParams(iconView, onScreenRectCenterX, onScreenRectCenterY, this.mFullscreenParams.mScaleX, this.mFullscreenParams.mScaleY, iconView.getDrawableWidth(), iconView.getDrawableHeight(), this.mActivity.getDeviceProfile(), this.mStagePosition);
    }

    private static class SplitOverlayProperties {
        private final float dX;
        private final float dY;
        private final float finalTaskViewScaleX;
        private final float finalTaskViewScaleY;

        SplitOverlayProperties(Rect endBounds, RectF startTaskViewBounds, int dragLayerLeft, int dragLayerTop) {
            float fWidth = endBounds.width() / startTaskViewBounds.width();
            float fHeight = endBounds.height() / startTaskViewBounds.height();
            this.finalTaskViewScaleX = fWidth;
            this.finalTaskViewScaleY = fHeight;
            float fCenterX = endBounds.centerX() - dragLayerLeft;
            float fCenterY = endBounds.centerY() - dragLayerTop;
            this.dX = fCenterX - startTaskViewBounds.centerX();
            this.dY = fCenterY - startTaskViewBounds.centerY();
        }
    }

    public static class FullscreenDrawParams {
        private final float mCornerRadius;
        public float mCurrentDrawnCornerRadius;
        public boolean mIsStagedTask;
        private final float mWindowCornerRadius;
        public final RectF mBounds = new RectF();
        public float mScaleX = 1.0f;
        public float mScaleY = 1.0f;

        public FullscreenDrawParams(Context context) {
            float f = TaskCornerRadius.get(context);
            this.mCornerRadius = f;
            this.mWindowCornerRadius = QuickStepContract.getWindowCornerRadius(context);
            this.mCurrentDrawnCornerRadius = f;
        }

        public void updateParams(RectF bounds, float progress, float scaleX, float scaleY) {
            this.mBounds.set(bounds);
            this.mScaleX = scaleX;
            this.mScaleY = scaleY;
            this.mCurrentDrawnCornerRadius = this.mIsStagedTask ? this.mWindowCornerRadius : Utilities.mapRange(progress, this.mCornerRadius, this.mWindowCornerRadius);
        }

        public void setIsStagedTask(boolean isStagedTask) {
            this.mIsStagedTask = isStagedTask;
        }
    }
}
