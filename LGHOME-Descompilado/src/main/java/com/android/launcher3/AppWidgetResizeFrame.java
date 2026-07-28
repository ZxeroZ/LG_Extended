package com.android.launcher3;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.launcher3.CellLayout;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.views.BaseDragLayer;
import com.lge.launcher3.LGAppWidgetResizeFrame;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.uninstall.UninstallBadgeUtils;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.wallpaperblur.WidgetBlurManager;
import com.lge.lgewidgetlib.LgeWidgetContext;

/* JADX INFO: loaded from: classes.dex */
public class AppWidgetResizeFrame extends AbstractFloatingView {
    private static final float DIMMED_HANDLE_ALPHA = 0.0f;
    protected static final float RESIZE_THRESHOLD = 0.66f;
    private static final int SNAP_DURATION = 150;
    private static Point[] sCellSize;
    private static Rect sTmpRect = new Rect();
    private final int mBackgroundPadding;
    private int mBaselineHeight;
    private int mBaselineWidth;
    private int mBaselineX;
    private int mBaselineY;
    protected boolean mBottomBorderActive;
    protected final ImageView mBottomHandle;
    private int mBottomTouchRegionAdjustment;
    protected final CellLayout mCellLayout;
    protected int mDeltaX;
    protected int mDeltaXAddOn;
    protected int mDeltaY;
    protected int mDeltaYAddOn;
    protected final int[] mDirectionVector;
    private final DragLayer mDragLayer;
    protected final int[] mLastDirectionVector;
    protected final Launcher mLauncher;
    protected boolean mLeftBorderActive;
    protected final ImageView mLeftHandle;
    protected int mMinHSpan;
    protected int mMinVSpan;
    protected int mResizeMode;
    protected boolean mRightBorderActive;
    protected final ImageView mRightHandle;
    protected int mRunningHInc;
    protected int mRunningVInc;
    private final int[] mTmpPt;
    protected boolean mTopBorderActive;
    protected final ImageView mTopHandle;
    private int mTopTouchRegionAdjustment;
    protected final int mTouchTargetWidth;
    private final Rect mWidgetPadding;
    protected final LauncherAppWidgetHostView mWidgetView;
    private int mXDown;
    private int mYDown;

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 8) != 0;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
    }

    public AppWidgetResizeFrame(Context context, LauncherAppWidgetHostView widgetView, CellLayout cellLayout, DragLayer dragLayer) {
        super(context, null);
        this.mDirectionVector = new int[2];
        this.mLastDirectionVector = new int[2];
        this.mTmpPt = new int[2];
        this.mTopTouchRegionAdjustment = 0;
        this.mBottomTouchRegionAdjustment = 0;
        this.mLauncher = (Launcher) context;
        this.mCellLayout = cellLayout;
        this.mWidgetView = widgetView;
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) widgetView.getAppWidgetInfo();
        this.mResizeMode = launcherAppWidgetProviderInfo.resizeMode;
        this.mDragLayer = dragLayer;
        this.mMinHSpan = launcherAppWidgetProviderInfo.getMinSpanX(context);
        this.mMinVSpan = launcherAppWidgetProviderInfo.getMinSpanY(context);
        setBackgroundResource(R.drawable.widget_resize_shadow);
        setForeground(getResources().getDrawable(R.drawable.widget_resize_frame));
        setPadding(0, 0, 0, 0);
        FrameLayout frameLayout = new FrameLayout(context);
        addView(frameLayout, new LinearLayout.LayoutParams(-1, -1));
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.widget_handle_margin);
        ImageView imageView = new ImageView(context);
        this.mLeftHandle = imageView;
        imageView.setImageResource(R.drawable.ic_widget_resize_handle);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2, 19);
        layoutParams.leftMargin = dimensionPixelSize;
        frameLayout.addView(imageView, layoutParams);
        ImageView imageView2 = new ImageView(context);
        this.mRightHandle = imageView2;
        imageView2.setImageResource(R.drawable.ic_widget_resize_handle);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-2, -2, 21);
        layoutParams2.rightMargin = dimensionPixelSize;
        frameLayout.addView(imageView2, layoutParams2);
        ImageView imageView3 = new ImageView(context);
        this.mTopHandle = imageView3;
        imageView3.setImageResource(R.drawable.ic_widget_resize_handle);
        FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-2, -2, 49);
        layoutParams3.topMargin = dimensionPixelSize;
        frameLayout.addView(imageView3, layoutParams3);
        ImageView imageView4 = new ImageView(context);
        this.mBottomHandle = imageView4;
        imageView4.setImageResource(R.drawable.ic_widget_resize_handle);
        FrameLayout.LayoutParams layoutParams4 = new FrameLayout.LayoutParams(-2, -2, 81);
        layoutParams4.bottomMargin = dimensionPixelSize;
        frameLayout.addView(imageView4, layoutParams4);
        if (!launcherAppWidgetProviderInfo.isCustomWidget) {
            this.mWidgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(context, widgetView.getAppWidgetInfo().provider, null);
        } else {
            int dimensionPixelSize2 = context.getResources().getDimensionPixelSize(R.dimen.default_widget_padding);
            this.mWidgetPadding = new Rect(dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize2);
        }
        int i = this.mResizeMode;
        if (i == 1) {
            imageView3.setVisibility(8);
            imageView4.setVisibility(8);
        } else if (i == 2) {
            imageView.setVisibility(8);
            imageView2.setVisibility(8);
        }
        int dimensionPixelSize3 = getResources().getDimensionPixelSize(R.dimen.resize_frame_background_padding);
        this.mBackgroundPadding = dimensionPixelSize3;
        this.mTouchTargetWidth = dimensionPixelSize3 * 2;
        cellLayout.markCellsAsUnoccupiedForView(widgetView);
    }

    public boolean beginResizeIfPointInRegion(int x, int y) {
        int i = this.mResizeMode;
        boolean z = (i & 1) != 0;
        boolean z2 = (i & 2) != 0;
        this.mLeftBorderActive = x < this.mTouchTargetWidth && z;
        int width = getWidth();
        int i2 = this.mTouchTargetWidth;
        this.mRightBorderActive = x > width - i2 && z;
        this.mTopBorderActive = y < i2 + this.mTopTouchRegionAdjustment && z2;
        boolean z3 = y > (getHeight() - this.mTouchTargetWidth) + this.mBottomTouchRegionAdjustment && z2;
        this.mBottomBorderActive = z3;
        boolean z4 = this.mLeftBorderActive || this.mRightBorderActive || this.mTopBorderActive || z3;
        this.mBaselineWidth = getMeasuredWidth();
        this.mBaselineHeight = getMeasuredHeight();
        this.mBaselineX = getLeft();
        this.mBaselineY = getTop();
        if (z4) {
            this.mLeftHandle.setAlpha(this.mLeftBorderActive ? 1.0f : 0.0f);
            this.mRightHandle.setAlpha(this.mRightBorderActive ? 1.0f : 0.0f);
            this.mTopHandle.setAlpha(this.mTopBorderActive ? 1.0f : 0.0f);
            this.mBottomHandle.setAlpha(this.mBottomBorderActive ? 1.0f : 0.0f);
        }
        return z4;
    }

    public void updateDeltas(int deltaX, int deltaY) {
        if (this.mLeftBorderActive) {
            int iMax = Math.max(-this.mBaselineX, deltaX);
            this.mDeltaX = iMax;
            this.mDeltaX = Math.min(this.mBaselineWidth - (this.mTouchTargetWidth * 2), iMax);
        } else if (this.mRightBorderActive) {
            int iMin = Math.min(this.mDragLayer.getWidth() - (this.mBaselineX + this.mBaselineWidth), deltaX);
            this.mDeltaX = iMin;
            this.mDeltaX = Math.max((-this.mBaselineWidth) + (this.mTouchTargetWidth * 2), iMin);
        }
        if (this.mTopBorderActive) {
            int iMax2 = Math.max(-this.mBaselineY, deltaY);
            this.mDeltaY = iMax2;
            this.mDeltaY = Math.min(this.mBaselineHeight - (this.mTouchTargetWidth * 2), iMax2);
        } else if (this.mBottomBorderActive) {
            int iMin2 = Math.min(this.mDragLayer.getHeight() - (this.mBaselineY + this.mBaselineHeight), deltaY);
            this.mDeltaY = iMin2;
            this.mDeltaY = Math.max((-this.mBaselineHeight) + (this.mTouchTargetWidth * 2), iMin2);
        }
    }

    public void visualizeResizeForDelta(int deltaX, int deltaY) {
        visualizeResizeForDelta(deltaX, deltaY, false);
    }

    private void visualizeResizeForDelta(int deltaX, int deltaY, boolean onDismiss) {
        updateDeltas(deltaX, deltaY);
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        if (this.mLeftBorderActive) {
            layoutParams.x = this.mBaselineX + this.mDeltaX;
            layoutParams.width = this.mBaselineWidth - this.mDeltaX;
        } else if (this.mRightBorderActive) {
            layoutParams.width = this.mBaselineWidth + this.mDeltaX;
        }
        if (this.mTopBorderActive) {
            layoutParams.y = this.mBaselineY + this.mDeltaY;
            layoutParams.height = this.mBaselineHeight - this.mDeltaY;
        } else if (this.mBottomBorderActive) {
            layoutParams.height = this.mBaselineHeight + this.mDeltaY;
        }
        resizeWidgetIfNeeded(onDismiss);
        requestLayout();
    }

    protected void resizeWidgetIfNeeded(boolean onDismiss) {
        int iMin;
        int i;
        int iMin2;
        int i2;
        int cellWidth = this.mCellLayout.getCellWidth() + this.mCellLayout.getWidthGap();
        int cellHeight = this.mCellLayout.getCellHeight() + this.mCellLayout.getHeightGap();
        int i3 = this.mDeltaX + this.mDeltaXAddOn;
        float f = ((i3 * 1.0f) / cellWidth) - this.mRunningHInc;
        float f2 = (((this.mDeltaY + this.mDeltaYAddOn) * 1.0f) / cellHeight) - this.mRunningVInc;
        int countX = this.mCellLayout.getCountX();
        int countY = this.mCellLayout.getCountY();
        int iRound = Math.abs(f) > RESIZE_THRESHOLD ? Math.round(f) : 0;
        int iRound2 = Math.abs(f2) > RESIZE_THRESHOLD ? Math.round(f2) : 0;
        if (!onDismiss && iRound == 0 && iRound2 == 0) {
            return;
        }
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) this.mWidgetView.getLayoutParams();
        int i4 = layoutParams.cellHSpan;
        int i5 = layoutParams.cellVSpan;
        int i6 = layoutParams.useTmpCoords ? layoutParams.tmpCellX : layoutParams.cellX;
        int i7 = layoutParams.useTmpCoords ? layoutParams.tmpCellY : layoutParams.cellY;
        if (this.mLeftBorderActive) {
            iMin = Math.min(layoutParams.cellHSpan - this.mMinHSpan, Math.max(-i6, iRound));
            iRound = Math.max(-(layoutParams.cellHSpan - this.mMinHSpan), Math.min(i6, iRound * (-1)));
            i = -iRound;
        } else if (this.mRightBorderActive) {
            iRound = Math.max(-(layoutParams.cellHSpan - this.mMinHSpan), Math.min(countX - (i6 + i4), iRound));
            i = iRound;
            iMin = 0;
        } else {
            iMin = 0;
            i = 0;
        }
        if (this.mTopBorderActive) {
            iMin2 = Math.min(layoutParams.cellVSpan - this.mMinVSpan, Math.max(-i7, iRound2));
            iRound2 = Math.max(-(layoutParams.cellVSpan - this.mMinVSpan), Math.min(i7, iRound2 * (-1)));
            i2 = -iRound2;
        } else if (this.mBottomBorderActive) {
            iRound2 = Math.max(-(layoutParams.cellVSpan - this.mMinVSpan), Math.min(countY - (i7 + i5), iRound2));
            i2 = iRound2;
            iMin2 = 0;
        } else {
            iMin2 = 0;
            i2 = 0;
        }
        int[] iArr = this.mDirectionVector;
        iArr[0] = 0;
        iArr[1] = 0;
        boolean z = this.mLeftBorderActive;
        if (z || this.mRightBorderActive) {
            i4 += iRound;
            i6 += iMin;
            if (i != 0) {
                iArr[0] = z ? -1 : 1;
            }
        }
        int i8 = i4;
        int i9 = i6;
        boolean z2 = this.mTopBorderActive;
        if (z2 || this.mBottomBorderActive) {
            i5 += iRound2;
            i7 += iMin2;
            if (i2 != 0) {
                iArr[1] = z2 ? -1 : 1;
            }
        }
        int i10 = i7;
        int i11 = i5;
        if (!onDismiss && i2 == 0 && i == 0) {
            return;
        }
        if (onDismiss) {
            int[] iArr2 = this.mLastDirectionVector;
            iArr[0] = iArr2[0];
            iArr[1] = iArr2[1];
        } else {
            int[] iArr3 = this.mLastDirectionVector;
            iArr3[0] = iArr[0];
            iArr3[1] = iArr[1];
        }
        if (this.mCellLayout.createAreaForResize(i9, i10, i8, i11, this.mWidgetView, iArr, onDismiss)) {
            layoutParams.tmpCellX = i9;
            layoutParams.tmpCellY = i10;
            layoutParams.cellHSpan = i8;
            layoutParams.cellVSpan = i11;
            this.mRunningVInc += i2;
            this.mRunningHInc += i;
            if (!onDismiss) {
                updateWidgetSizeRanges(this.mWidgetView, this.mLauncher, i8, i11, i9, i10);
            }
        }
        this.mWidgetView.requestLayout();
    }

    public static void updateWidgetSizeRanges(AppWidgetHostView widgetView, Launcher launcher, int spanX, int spanY, int cellX, int cellY) {
        updateWidgetSizeRanges(widgetView, launcher, spanX, spanY, cellX, cellY, (widgetView == null || widgetView.getAppWidgetInfo() == null || widgetView.getAppWidgetInfo().provider == null || !LgeWidgetContext.isLGEAppWidgetPackage(widgetView.getAppWidgetInfo().provider.getPackageName())) ? false : true);
    }

    public static void updateWidgetSizeRanges(AppWidgetHostView widgetView, Launcher launcher, int spanX, int spanY, int cellX, int cellY, boolean useBundle) {
        getWidgetSizeRanges(launcher, spanX, spanY, sTmpRect);
        widgetView.updateAppWidgetSize(makeWidgetInfoBundle(widgetView, launcher, spanX, spanY, cellX, cellY, useBundle, "updateWidgetSizeRanges"), sTmpRect.left, sTmpRect.top, sTmpRect.right, sTmpRect.bottom);
    }

    public static void updateAppWidgetOption(AppWidgetHostView widgetView, Launcher launcher, int spanX, int spanY, int cellX, int cellY, boolean useBundle) {
        if (widgetView != null) {
            widgetView.updateAppWidgetOptions(makeWidgetInfoBundle(widgetView, launcher, spanX, spanY, cellX, cellY, useBundle, "updateAppWidgetOption"));
        }
    }

    public static Bundle makeWidgetInfoBundle(AppWidgetHostView widgetView, Launcher launcher, int spanX, int spanY, int cellX, int cellY, boolean useBundle, String caller) {
        if (LGHomeFeature.Config.FEATURE_USE_EXTRA_WIDGET_INFO.getValue() && useBundle) {
            int i = launcher.getDeviceProfile().numRows;
            int i2 = launcher.getDeviceProfile().numColumns;
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(launcher.getApplicationContext());
            appWidgetOptions = appWidgetManager != null ? appWidgetManager.getAppWidgetOptions(widgetView.getAppWidgetId()) : null;
            if (appWidgetOptions == null) {
                LGLog.i("AppWidgetResizeFrame", "makeWidgetInfoBundle : make bundle because cannot find widget option. " + appWidgetManager);
                appWidgetOptions = new Bundle();
            }
            appWidgetOptions.putInt("device_state", -1);
            appWidgetOptions.putInt("spanX", spanX);
            appWidgetOptions.putInt("spanY", spanY);
            appWidgetOptions.putInt("x", cellX);
            appWidgetOptions.putInt("y", cellY);
            appWidgetOptions.putInt("numRows", i);
            appWidgetOptions.putInt("numColumns", i2);
            LGLog.d("AppWidgetResizeFrame", String.format("makeWidgetInfoBundle : call by(%s), device_state(%s), size(%sx%s), locate(%s,%s), grid(row:%s, column:%s), info(%s)", caller, -1, Integer.valueOf(spanX), Integer.valueOf(spanY), Integer.valueOf(cellX), Integer.valueOf(cellY), Integer.valueOf(i), Integer.valueOf(i2), widgetView.getTag()));
        }
        return appWidgetOptions;
    }

    public static Rect getWidgetSizeRanges(Launcher launcher, int spanX, int spanY, Rect rect) {
        if (rect == null) {
            rect = new Rect();
        }
        Rect cellLayoutMetrics = Workspace.getCellLayoutMetrics(launcher, 0);
        Rect cellLayoutMetrics2 = Workspace.getCellLayoutMetrics(launcher, 1);
        float f = launcher.getResources().getDisplayMetrics().density;
        int i = cellLayoutMetrics.left;
        int i2 = cellLayoutMetrics.top;
        int i3 = spanX - 1;
        int i4 = (int) (((i * spanX) + (cellLayoutMetrics.right * i3)) / f);
        int i5 = spanY - 1;
        int i6 = (int) (((i2 * spanY) + (cellLayoutMetrics.bottom * i5)) / f);
        int i7 = cellLayoutMetrics2.left;
        int i8 = cellLayoutMetrics2.top;
        int i9 = (int) (((spanX * i7) + (i3 * cellLayoutMetrics2.right)) / f);
        int i10 = (int) (((spanY * i8) + (i5 * cellLayoutMetrics2.bottom)) / f);
        if (launcher.mDeviceProfile.isTablet) {
            rect.set(i9, i6, i4, i10);
        } else {
            rect.set(i9, i10, i9, i10);
        }
        return rect;
    }

    public static Rect getWidgetSizeRanges(Context context, int spanX, int spanY, Rect rect) {
        if (sCellSize == null) {
            InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
            Point[] pointArr = new Point[2];
            sCellSize = pointArr;
            pointArr[0] = idp.landscapeProfile.getCellSize();
            sCellSize[1] = idp.portraitProfile.getCellSize();
        }
        if (rect == null) {
            rect = new Rect();
        }
        float f = context.getResources().getDisplayMetrics().density;
        rect.set((int) ((spanX * sCellSize[1].x) / f), (int) ((sCellSize[0].y * spanY) / f), (int) ((sCellSize[0].x * spanX) / f), (int) ((spanY * sCellSize[1].y) / f));
        return rect;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resizeWidgetIfNeeded(true);
    }

    public void onTouchUp() {
        int cellWidth = this.mCellLayout.getCellWidth() + this.mCellLayout.getWidthGap();
        int cellHeight = this.mCellLayout.getCellHeight() + this.mCellLayout.getHeightGap();
        this.mDeltaXAddOn = this.mRunningHInc * cellWidth;
        this.mDeltaYAddOn = this.mRunningVInc * cellHeight;
        this.mDeltaX = 0;
        this.mDeltaY = 0;
        post(new Runnable() { // from class: com.android.launcher3.AppWidgetResizeFrame.1
            @Override // java.lang.Runnable
            public void run() {
                AppWidgetResizeFrame.this.snapToWidget(true);
            }
        });
    }

    public void snapToWidget(boolean animate) {
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        float shirinkFactor = UninstallModeManager.getInstance(this.mContext).getShirinkFactor(this.mLauncher.getWorkspace());
        int width = (int) ((((this.mWidgetView.getWidth() * shirinkFactor) + (this.mBackgroundPadding * 2)) - this.mWidgetPadding.left) - this.mWidgetPadding.right);
        int height = (int) ((((this.mWidgetView.getHeight() * shirinkFactor) + (this.mBackgroundPadding * 2)) - this.mWidgetPadding.top) - this.mWidgetPadding.bottom);
        this.mTmpPt[0] = this.mWidgetView.getLeft();
        this.mTmpPt[1] = this.mWidgetView.getTop();
        this.mDragLayer.getDescendantCoordRelativeToSelf((View) this.mCellLayout.getShortcutsAndWidgets(), this.mTmpPt);
        int i = (this.mTmpPt[0] - this.mBackgroundPadding) + this.mWidgetPadding.left;
        int i2 = (this.mTmpPt[1] - this.mBackgroundPadding) + this.mWidgetPadding.top;
        if (i2 < 0) {
            this.mTopTouchRegionAdjustment = -i2;
        } else {
            this.mTopTouchRegionAdjustment = 0;
        }
        int i3 = i2 + height;
        if (i3 > this.mDragLayer.getHeight()) {
            this.mBottomTouchRegionAdjustment = -(i3 - this.mDragLayer.getHeight());
        } else {
            this.mBottomTouchRegionAdjustment = 0;
        }
        if (!animate) {
            layoutParams.width = width;
            layoutParams.height = height;
            layoutParams.x = i;
            layoutParams.y = i2;
            this.mLeftHandle.setAlpha(1.0f);
            this.mRightHandle.setAlpha(1.0f);
            this.mTopHandle.setAlpha(1.0f);
            this.mBottomHandle.setAlpha(1.0f);
            requestLayout();
            return;
        }
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(layoutParams, this, PropertyValuesHolder.ofInt("width", layoutParams.width, width), PropertyValuesHolder.ofInt("height", layoutParams.height, height), PropertyValuesHolder.ofInt("x", layoutParams.x, i), PropertyValuesHolder.ofInt("y", layoutParams.y, i2));
        ObjectAnimator objectAnimatorOfFloat = LauncherAnimUtils.ofFloat(this.mLeftHandle, "alpha", 1.0f);
        ObjectAnimator objectAnimatorOfFloat2 = LauncherAnimUtils.ofFloat(this.mRightHandle, "alpha", 1.0f);
        ObjectAnimator objectAnimatorOfFloat3 = LauncherAnimUtils.ofFloat(this.mTopHandle, "alpha", 1.0f);
        ObjectAnimator objectAnimatorOfFloat4 = LauncherAnimUtils.ofFloat(this.mBottomHandle, "alpha", 1.0f);
        objectAnimatorOfPropertyValuesHolder.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.AppWidgetResizeFrame.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                AppWidgetResizeFrame.this.requestLayout();
            }
        });
        AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        int i4 = this.mResizeMode;
        if (i4 == 2) {
            animatorSetCreateAnimatorSet.playTogether(objectAnimatorOfPropertyValuesHolder, objectAnimatorOfFloat3, objectAnimatorOfFloat4);
        } else if (i4 == 1) {
            animatorSetCreateAnimatorSet.playTogether(objectAnimatorOfPropertyValuesHolder, objectAnimatorOfFloat, objectAnimatorOfFloat2);
        } else {
            animatorSetCreateAnimatorSet.playTogether(objectAnimatorOfPropertyValuesHolder, objectAnimatorOfFloat, objectAnimatorOfFloat2, objectAnimatorOfFloat3, objectAnimatorOfFloat4);
        }
        animatorSetCreateAnimatorSet.setDuration(150L);
        animatorSetCreateAnimatorSet.start();
    }

    private boolean isUninstallBadgeTouched(int x, int y) {
        int top;
        int left;
        int left2;
        int top2;
        float f = this.mContext.getResources().getFloat(R.dimen.lg_badge_clickable_ratio);
        if (UninstallBadgeUtils.getUninstallBadgeRect() != null) {
            left = ((int) (UninstallBadgeUtils.getUninstallBadgeRect().left / f)) + getLeft();
            left2 = ((int) (UninstallBadgeUtils.getUninstallBadgeRect().right * f)) + getLeft();
            top2 = ((int) (UninstallBadgeUtils.getUninstallBadgeRect().top / f)) + getTop();
            top = ((int) (UninstallBadgeUtils.getUninstallBadgeRect().bottom * f)) + getTop();
        } else {
            top = 0;
            left = 0;
            left2 = 0;
            top2 = 0;
        }
        return x > left && x < left2 && y > top2 && y < top;
    }

    private boolean handleTouchDown(MotionEvent ev) {
        Rect rect = new Rect();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        getHitRect(rect);
        if (!rect.contains(x, y) || ((UninstallModeManager.getInstance(this.mContext).isInUninstallMode() && isUninstallBadgeTouched(x, y)) || !beginResizeIfPointInRegion(x - getLeft(), y - getTop()))) {
            return false;
        }
        this.mXDown = x;
        this.mYDown = y;
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0024  */
    @Override // com.android.launcher3.AbstractFloatingView, com.android.launcher3.util.TouchController
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onControllerTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            int r0 = r5.getAction()
            float r1 = r5.getX()
            int r1 = (int) r1
            float r2 = r5.getY()
            int r2 = (int) r2
            if (r0 == 0) goto L36
            r5 = 1
            if (r0 == r5) goto L24
            r3 = 2
            if (r0 == r3) goto L1a
            r3 = 3
            if (r0 == r3) goto L24
            goto L35
        L1a:
            int r0 = r4.mXDown
            int r1 = r1 - r0
            int r0 = r4.mYDown
            int r2 = r2 - r0
            r4.visualizeResizeForDelta(r1, r2)
            goto L35
        L24:
            int r0 = r4.mXDown
            int r1 = r1 - r0
            int r0 = r4.mYDown
            int r2 = r2 - r0
            r4.visualizeResizeForDelta(r1, r2)
            r4.onTouchUp()
            r0 = 0
            r4.mYDown = r0
            r4.mXDown = r0
        L35:
            return r5
        L36:
            boolean r5 = r4.handleTouchDown(r5)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.AppWidgetResizeFrame.onControllerTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && handleTouchDown(ev)) {
            return true;
        }
        close(false);
        return false;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        WidgetBlurManager.getInstance(this.mLauncher).enableResizedFrame(false);
        this.mDragLayer.removeView(this);
    }

    public static void showForWidget(LauncherAppWidgetHostView widget, CellLayout cellLayout) {
        Launcher launcher = Launcher.getLauncher(cellLayout.getContext());
        AbstractFloatingView.closeAllOpenViews(launcher);
        DragLayer dragLayer = launcher.getDragLayer();
        LGAppWidgetResizeFrame lGAppWidgetResizeFrame = new LGAppWidgetResizeFrame(cellLayout.getContext(), widget, cellLayout, dragLayer);
        BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(-1, -1);
        layoutParams.customPosition = true;
        dragLayer.addView(lGAppWidgetResizeFrame, layoutParams);
        lGAppWidgetResizeFrame.mIsOpen = true;
        lGAppWidgetResizeFrame.snapToWidget(false);
        lGAppWidgetResizeFrame.sendAccessibilityEvent(32);
        WidgetBlurManager.getInstance(cellLayout.getContext()).enableResizedFrame(true);
    }
}
