package com.android.quickstep.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.uioverrides.states.OverviewState;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.ScrimView;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.util.LayoutUtils;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class ShelfScrimView extends ScrimView<BaseQuickstepLauncher> implements SysUINavigationMode.NavigationModeChangeListener {
    private static final float BOTTOM_CORNER_RADIUS_RATIO = 2.0f;
    private static final float SCRIM_CATCHUP_THRESHOLD = 0.2f;
    private Interpolator mAfterMidProgressColorInterpolator;
    private Interpolator mBeforeMidProgressColorInterpolator;
    private float mDragHandleProgress;
    private boolean mDrawingFlatColor;
    private final int mEndAlpha;
    private boolean mIsTwoZoneSwipeModel;
    private final int mMaxScrimAlpha;
    private int mMidAlpha;
    private float mMidProgress;
    private final OnboardingPrefs mOnboardingPrefs;
    private final Paint mPaint;
    private final float mRadius;
    private int mRemainingScreenColor;
    private final Path mRemainingScreenPath;
    private boolean mRemainingScreenPathValid;
    private int mShelfColor;
    private float mShelfTop;
    private float mShelfTopAtThreshold;
    private float mShiftRange;
    private SysUINavigationMode.Mode mSysUINavigationMode;
    private final Path mTempPath;
    private float mTopOffset;

    public ShelfScrimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBeforeMidProgressColorInterpolator = Interpolators.ACCEL;
        this.mAfterMidProgressColorInterpolator = Interpolators.ACCEL;
        this.mTempPath = new Path();
        this.mRemainingScreenPath = new Path();
        this.mRemainingScreenPathValid = false;
        this.mMaxScrimAlpha = Math.round(LauncherState.OVERVIEW.getOverviewScrimAlpha(this.mLauncher) * 255.0f);
        this.mEndAlpha = Color.alpha(this.mEndScrim);
        this.mRadius = Themes.getDialogCornerRadius(context) * 2.0f;
        this.mPaint = new Paint(1);
        this.mOnboardingPrefs = ((BaseQuickstepLauncher) this.mLauncher).getOnboardingPrefs();
        this.mDrawingFlatColor = true;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mRemainingScreenPathValid = false;
    }

    @Override // com.android.launcher3.views.ScrimView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onNavigationModeChanged(SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).addModeChangeListener(this));
    }

    @Override // com.android.launcher3.views.ScrimView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).removeModeChangeListener(this);
    }

    @Override // com.android.quickstep.SysUINavigationMode.NavigationModeChangeListener
    public void onNavigationModeChanged(SysUINavigationMode.Mode newMode) {
        this.mSysUINavigationMode = newMode;
        if (newMode == SysUINavigationMode.Mode.NO_BUTTON) {
            this.mBeforeMidProgressColorInterpolator = Interpolators.ACCEL_2;
            this.mAfterMidProgressColorInterpolator = Interpolators.ACCEL;
            this.mIsTwoZoneSwipeModel = FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get();
        } else {
            this.mBeforeMidProgressColorInterpolator = Interpolators.ACCEL;
            this.mAfterMidProgressColorInterpolator = Interpolators.clampToProgress(Interpolators.ACCEL, 0.5f, 1.0f);
            this.mIsTwoZoneSwipeModel = false;
        }
    }

    @Override // com.android.launcher3.views.ScrimView
    public void reInitUi() {
        DeviceProfile deviceProfile = ((BaseQuickstepLauncher) this.mLauncher).getDeviceProfile();
        boolean zIsVerticalBarLayout = deviceProfile.isVerticalBarLayout();
        this.mDrawingFlatColor = zIsVerticalBarLayout;
        if (!zIsVerticalBarLayout) {
            this.mRemainingScreenPathValid = false;
            this.mShiftRange = ((BaseQuickstepLauncher) this.mLauncher).getAllAppsController().getShiftRange();
            Context context = getContext();
            if ((LauncherState.OVERVIEW.getVisibleElements(this.mLauncher) & 8) == 0) {
                this.mDragHandleProgress = 1.0f;
                if (FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get() && SysUINavigationMode.removeShelfFromOverview(context)) {
                    this.mMidAlpha = Themes.getAttrInteger(context, R.attr.allAppsInterimScrimAlpha);
                    this.mMidProgress = OverviewState.getDefaultVerticalProgress(this.mLauncher);
                } else {
                    this.mMidAlpha = 0;
                    this.mMidProgress = 1.0f;
                }
            } else {
                this.mMidAlpha = Themes.getAttrInteger(context, R.attr.allAppsInterimScrimAlpha);
                this.mMidProgress = LauncherState.OVERVIEW.getVerticalProgress(this.mLauncher);
                Rect hotseatLayoutPadding = deviceProfile.getHotseatLayoutPadding();
                this.mDragHandleProgress = 1.0f - (Math.min(((deviceProfile.hotseatBarSizePx + deviceProfile.getInsets().bottom) + hotseatLayoutPadding.bottom) + hotseatLayoutPadding.top, LayoutUtils.getDefaultSwipeHeight(context, deviceProfile)) / this.mShiftRange);
            }
            float f = deviceProfile.getInsets().top - this.mDragHandleSize.y;
            this.mTopOffset = f;
            this.mShelfTopAtThreshold = (this.mShiftRange * 0.2f) + f;
        }
        updateColors();
        updateSysUiColors();
        updateDragHandleAlpha();
        invalidate();
    }

    @Override // com.android.launcher3.views.ScrimView
    public void updateColors() {
        super.updateColors();
        this.mDragHandleOffset = 0.0f;
        if (this.mDrawingFlatColor) {
            return;
        }
        float f = this.mProgress;
        float f2 = this.mDragHandleProgress;
        if (f < f2) {
            this.mDragHandleOffset = this.mShiftRange * (f2 - this.mProgress);
        }
        if (this.mProgress >= 0.2f) {
            this.mShelfTop = (this.mShiftRange * this.mProgress) + this.mTopOffset;
        } else {
            this.mShelfTop = Utilities.mapRange(this.mProgress / 0.2f, -this.mRadius, this.mShelfTopAtThreshold);
        }
        if (this.mProgress < 1.0f) {
            if (this.mProgress >= this.mMidProgress) {
                this.mRemainingScreenColor = 0;
                this.mShelfColor = GraphicsUtils.setColorAlphaBound(this.mEndScrim, Math.round(Utilities.mapToRange(this.mProgress, this.mMidProgress, 1.0f, this.mMidAlpha, 0.0f, this.mBeforeMidProgressColorInterpolator)));
                return;
            } else {
                this.mShelfColor = GraphicsUtils.setColorAlphaBound(this.mEndScrim, Math.round(Utilities.mapToRange(this.mProgress, 0.0f, this.mMidProgress, this.mEndAlpha, this.mMidAlpha, this.mAfterMidProgressColorInterpolator)));
                this.mRemainingScreenColor = GraphicsUtils.setColorAlphaBound(this.mScrimColor, Math.round(Utilities.mapToRange(this.mProgress, 0.0f, this.mMidProgress, this.mMaxScrimAlpha, 0.0f, Interpolators.LINEAR)));
                return;
            }
        }
        this.mRemainingScreenColor = 0;
        this.mShelfColor = 0;
        LauncherState launcherState = (LauncherState) ((BaseQuickstepLauncher) this.mLauncher).getStateManager().getState();
        if (this.mSysUINavigationMode == SysUINavigationMode.Mode.NO_BUTTON) {
            if ((launcherState == LauncherState.BACKGROUND_APP || launcherState == LauncherState.QUICK_SWITCH) && ((BaseQuickstepLauncher) this.mLauncher).getShelfPeekAnim().isPeeking()) {
                this.mShelfColor = GraphicsUtils.setColorAlphaBound(this.mEndScrim, this.mMidAlpha);
            }
        }
    }

    @Override // com.android.launcher3.views.ScrimView
    protected void updateSysUiColors() {
        if (this.mDrawingFlatColor) {
            super.updateSysUiColors();
            return;
        }
        if (this.mShelfTop <= ((float) ((BaseQuickstepLauncher) this.mLauncher).getDeviceProfile().getInsets().top) / 2.0f) {
            ((BaseQuickstepLauncher) this.mLauncher).getSystemUiController().updateUiState(5, true ^ this.mIsScrimDark);
        } else {
            ((BaseQuickstepLauncher) this.mLauncher).getSystemUiController().updateUiState(5, 0);
        }
    }

    @Override // com.android.launcher3.views.ScrimView
    protected boolean shouldDragHandleBeVisible() {
        return (this.mIsTwoZoneSwipeModel && !this.mOnboardingPrefs.hasReachedMaxCount(OnboardingPrefs.ALL_APPS_COUNT)) || super.shouldDragHandleBeVisible();
    }

    @Override // com.android.launcher3.views.ScrimView, android.view.View
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawDragHandle(canvas);
    }

    private void drawBackground(Canvas canvas) {
        if (this.mDrawingFlatColor) {
            if (this.mCurrentFlatColor != 0) {
                canvas.drawColor(this.mCurrentFlatColor);
                return;
            }
            return;
        }
        if (Color.alpha(this.mShelfColor) == 0) {
            return;
        }
        if (this.mProgress <= 0.0f) {
            canvas.drawColor(this.mShelfColor);
            return;
        }
        int height = getHeight();
        int width = getWidth();
        if (this.mRemainingScreenColor != 0) {
            if (!this.mRemainingScreenPathValid) {
                this.mTempPath.reset();
                Path path = this.mTempPath;
                float f = height;
                float f2 = this.mRadius;
                float f3 = width;
                path.addRoundRect(0.0f, f - f2, f3, 10.0f + f + f2, f2, f2, Path.Direction.CW);
                this.mRemainingScreenPath.reset();
                this.mRemainingScreenPath.addRect(0.0f, 0.0f, f3, f, Path.Direction.CW);
                this.mRemainingScreenPath.op(this.mTempPath, Path.Op.DIFFERENCE);
            }
            float f4 = (height - this.mRadius) - this.mShelfTop;
            canvas.translate(0.0f, -f4);
            this.mPaint.setColor(this.mRemainingScreenColor);
            canvas.drawPath(this.mRemainingScreenPath, this.mPaint);
            canvas.translate(0.0f, f4);
        }
        this.mPaint.setColor(this.mShelfColor);
        float f5 = this.mRadius;
        canvas.drawRoundRect(0.0f, this.mShelfTop, width, height + f5, f5, f5, this.mPaint);
    }

    @Override // com.android.launcher3.views.ScrimView
    public float getVisualTop() {
        return this.mShelfTop;
    }
}
