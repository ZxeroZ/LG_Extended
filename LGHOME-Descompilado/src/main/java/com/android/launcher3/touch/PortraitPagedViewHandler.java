package com.android.launcher3.touch;

import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.FloatProperty;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.view.GravityCompat;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.PagedView;
import com.android.launcher3.Utilities;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.OverScroller;
import com.android.launcher3.util.SplitConfigurationOptions;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PortraitPagedViewHandler implements PagedOrientationHandler {
    private final Matrix mTmpMatrix = new Matrix();
    private final RectF mTmpRectF = new RectF();

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void adjustFloatingIconStartVelocity(PointF velocity) {
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getDegreesRotated() {
        return 0.0f;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public <T> T getPrimaryValue(T x, T y) {
        return x;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getRotation() {
        return 0;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public <T> T getSecondaryValue(T x, T y) {
        return y;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getSplitTaskViewDismissDirection(int stagePosition, DeviceProfile dp) {
        return 0;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getTaskDismissDirectionFactor() {
        return -1;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getTaskDragDisplacementFactor(boolean isRtl) {
        return 1;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getTaskMenuX(float x, View thumbnailView) {
        return x;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getTaskMenuY(float y, View thumbnailView) {
        return y;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public boolean isGoingUp(float displacement, boolean isRtl) {
        return displacement < 0.0f;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public boolean isLayoutNaturalToLauncher() {
        return true;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setLayoutParamsForTaskMenuOptionItem(LinearLayout.LayoutParams lp) {
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void delegateScrollTo(PagedView pagedView, int secondaryScroll, int primaryScroll) {
        pagedView.superScrollTo(primaryScroll, secondaryScroll);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void delegateScrollBy(PagedView pagedView, int unboundedScroll, int x, int y) {
        pagedView.scrollTo(unboundedScroll + x, pagedView.getScrollY() + y);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void scrollerStartScroll(OverScroller scroller, int newPosition) {
        scroller.startScroll(newPosition - scroller.getCurrPos(), scroller.getCurrPos());
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void getCurveProperties(PagedView view, Rect insets, PagedOrientationHandler.CurveProperties out) {
        out.scroll = view.getScrollX();
        out.halfPageSize = view.getNormalChildWidth() / 2;
        out.halfScreenSize = view.getMeasuredWidth() / 2;
        out.screenCenter = insets.left + view.getPaddingLeft() + out.scroll + out.halfPageSize;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void delegateScrollTo(PagedView pagedView, int primaryScroll) {
        pagedView.superScrollTo(primaryScroll, pagedView.getScrollY());
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public <T> void set(T target, PagedOrientationHandler.Int2DAction<T> action, int param) {
        action.call(target, param, 0);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public <T> void set(T target, PagedOrientationHandler.Float2DAction<T> action, float param) {
        action.call(target, param, 0.0f);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getPrimaryDirection(MotionEvent event, int pointerIndex) {
        return event.getX(pointerIndex);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getPrimaryVelocity(VelocityTracker velocityTracker, int pointerId) {
        return velocityTracker.getXVelocity(pointerId);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getMeasuredSize(View view) {
        return view.getMeasuredWidth();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getPrimarySize(RectF rect) {
        return rect.width();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getClearAllScrollOffset(View view, boolean isRtl) {
        return (isRtl ? view.getPaddingRight() : -view.getPaddingLeft()) / 2;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getSecondaryDimension(View view) {
        return view.getHeight();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public FloatProperty<View> getPrimaryViewTranslate() {
        return LauncherAnimUtils.VIEW_TRANSLATE_X;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public FloatProperty<View> getSecondaryViewTranslate() {
        return LauncherAnimUtils.VIEW_TRANSLATE_Y;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setPrimaryAndResetSecondaryTranslate(View view, float translation) {
        view.setTranslationX(translation);
        view.setTranslationY(0.0f);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getPrimaryScroll(View view) {
        return view.getScrollX();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getPrimaryScale(View view) {
        return view.getScaleX();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setMaxScroll(AccessibilityEvent event, int maxScroll) {
        event.setMaxScrollX(maxScroll);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public boolean getRecentsRtlSetting(Resources resources) {
        return !Utilities.isRtl(resources);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setPrimaryScale(View view, float scale) {
        view.setScaleX(scale);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setSecondaryScale(View view, float scale) {
        view.setScaleY(scale);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getChildStart(View view) {
        return view.getLeft();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getChildStartWithTranslation(View view) {
        return view.getLeft() + view.getTranslationX();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getCenterForPage(View view, Rect insets) {
        return ((((view.getPaddingTop() + view.getMeasuredHeight()) + insets.top) - insets.bottom) - view.getPaddingBottom()) / 2;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getScrollOffsetStart(View view, Rect insets) {
        return insets.left + view.getPaddingLeft();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getScrollOffsetEnd(View view, Rect insets) {
        return (view.getWidth() - view.getPaddingRight()) - insets.right;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public SingleAxisSwipeDetector.Direction getOppositeSwipeDirection(boolean isRtl) {
        return SingleAxisSwipeDetector.VERTICAL;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getTaskMenuWidth(View view) {
        return view.getMeasuredWidth();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getTaskMenuLayoutOrientation(LinearLayout taskMenuLayout) {
        return taskMenuLayout.getOrientation();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getSplitTranslationDirectionFactor(int stagePosition, DeviceProfile deviceProfile) {
        return (deviceProfile.isLandscape && stagePosition == 1) ? -1 : 1;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public Pair<FloatProperty, FloatProperty> getSplitSelectTaskOffset(FloatProperty primary, FloatProperty secondary, DeviceProfile deviceProfile) {
        if (deviceProfile.isLandscape) {
            return new Pair<>(primary, secondary);
        }
        return new Pair<>(secondary, primary);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public List<SplitConfigurationOptions.SplitPositionOption> getSplitPositionOptions(DeviceProfile dp) {
        return Utilities.getSplitPositionOptions(dp);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void getInitialSplitPlaceholderBounds(int placeholderHeight, int placeholderInset, DeviceProfile dp, int stagePosition, Rect out) {
        int i;
        int i2 = dp.widthPx;
        int i3 = dp.heightPx;
        boolean z = stagePosition == 1;
        if (!dp.isLandscape) {
            i = dp.getInsets().top;
        } else {
            Rect insets = dp.getInsets();
            i = z ? insets.right : insets.left;
        }
        out.set(0, 0, i2, i + placeholderHeight);
        if (!dp.isLandscape) {
            out.inset(placeholderInset, 0);
            out.top -= ((int) ((((i3 * 1.0f) / 2.0f) * (i2 - (placeholderInset * 2))) / i2)) - placeholderHeight;
            return;
        }
        float f = i3;
        float f2 = i2;
        float f3 = f / f2;
        this.mTmpMatrix.reset();
        this.mTmpMatrix.postRotate(z ? 90.0f : 270.0f);
        this.mTmpMatrix.postTranslate(z ? f2 : 0.0f, z ? 0.0f : f2);
        this.mTmpMatrix.postScale(1.0f, f3);
        this.mTmpRectF.set(out);
        this.mTmpMatrix.mapRect(this.mTmpRectF);
        this.mTmpRectF.inset(0.0f, placeholderInset);
        this.mTmpRectF.roundOut(out);
        int i4 = (int) ((((f2 * 1.0f) / 2.0f) * (i3 - (placeholderInset * 2))) / f);
        int iWidth = out.width();
        if (z) {
            out.right += i4 - iWidth;
        } else {
            out.left -= i4 - iWidth;
        }
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void updateStagedSplitIconParams(View out, float onScreenRectCenterX, float onScreenRectCenterY, float fullscreenScaleX, float fullscreenScaleY, int drawableWidth, int drawableHeight, DeviceProfile dp, int stagePosition) {
        boolean z = stagePosition == 1;
        if (!dp.isLandscape) {
            float f = dp.getInsets().top;
            out.setX(Math.round((onScreenRectCenterX / fullscreenScaleX) - ((drawableWidth * 1.0f) / 2.0f)));
            out.setY(Math.round(((onScreenRectCenterY + (f / 2.0f)) / fullscreenScaleY) - ((drawableHeight * 1.0f) / 2.0f)));
        } else {
            if (z) {
                out.setX(Math.round(((onScreenRectCenterX - (dp.getInsets().right / 2.0f)) / fullscreenScaleX) - ((drawableWidth * 1.0f) / 2.0f)));
            } else {
                out.setX(Math.round(((onScreenRectCenterX + (dp.getInsets().left / 2.0f)) / fullscreenScaleX) - ((drawableWidth * 1.0f) / 2.0f)));
            }
            out.setY(Math.round((onScreenRectCenterY / fullscreenScaleY) - ((drawableHeight * 1.0f) / 2.0f)));
        }
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void getFinalSplitPlaceholderBounds(int splitDividerSize, DeviceProfile dp, int stagePosition, Rect out1, Rect out2) {
        int i = dp.heightPx;
        int i2 = dp.widthPx;
        int i3 = i / 2;
        out1.set(0, 0, i2, i3 - splitDividerSize);
        out2.set(0, i3 + splitDividerSize, i2, i);
        if (dp.isLandscape) {
            boolean z = stagePosition == 1;
            float f = i;
            float f2 = i2;
            float f3 = f / f2;
            this.mTmpMatrix.reset();
            this.mTmpMatrix.postRotate(z ? 90.0f : 270.0f);
            Matrix matrix = this.mTmpMatrix;
            if (!z) {
                f = 0.0f;
            }
            if (z) {
                f2 = 0.0f;
            }
            matrix.postTranslate(f, f2);
            this.mTmpMatrix.postScale(1.0f / f3, f3);
            this.mTmpRectF.set(out1);
            this.mTmpMatrix.mapRect(this.mTmpRectF);
            this.mTmpRectF.roundOut(out1);
            this.mTmpRectF.set(out2);
            this.mTmpMatrix.mapRect(this.mTmpRectF);
            this.mTmpRectF.roundOut(out2);
        }
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getDefaultSplitPosition(DeviceProfile deviceProfile) {
        if (deviceProfile.isTablet) {
            return deviceProfile.isLandscape ? 1 : 0;
        }
        throw new IllegalStateException("Default position available only for large screens");
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setSplitTaskSwipeRect(DeviceProfile dp, Rect outRect, SplitConfigurationOptions.StagedSplitBounds splitInfo, int desiredStagePosition) {
        float f;
        float f2;
        boolean z = dp.isLandscape;
        if (splitInfo.appsStackedVertically) {
            f = splitInfo.topTaskPercent;
        } else {
            f = splitInfo.leftTaskPercent;
        }
        if (splitInfo.appsStackedVertically) {
            f2 = splitInfo.dividerHeightPercent;
        } else {
            f2 = splitInfo.dividerWidthPercent;
        }
        if (desiredStagePosition == 0) {
            if (z) {
                outRect.right = outRect.left + ((int) (outRect.width() * f));
                return;
            } else {
                outRect.bottom = outRect.top + ((int) (outRect.height() * f));
                return;
            }
        }
        if (z) {
            outRect.left += (int) (outRect.width() * (f + f2));
        } else {
            outRect.top += (int) (outRect.height() * (f + f2));
        }
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setTaskIconParams(FrameLayout.LayoutParams iconParams, int taskIconMargin, int taskIconHeight, int thumbnailTopMargin, boolean isRtl) {
        iconParams.gravity = 49;
        iconParams.rightMargin = 0;
        iconParams.leftMargin = 0;
        iconParams.topMargin = taskIconMargin;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setSplitIconParams(View primaryIconView, View secondaryIconView, int taskIconHeight, int primarySnapshotWidth, int primarySnapshotHeight, int groupedTaskViewHeight, int groupedTaskViewWidth, boolean isRtl, DeviceProfile deviceProfile, SplitConfigurationOptions.StagedSplitBounds splitConfig) {
        int i;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) primaryIconView.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(layoutParams);
        if (deviceProfile.isLandscape) {
            if (deviceProfile.isSeascape()) {
                i = deviceProfile.getInsets().right;
            } else {
                i = deviceProfile.getInsets().left;
            }
            float f = groupedTaskViewWidth + 0;
            int i2 = (int) ((((deviceProfile.widthPx - i) / 2) / deviceProfile.widthPx) * f);
            int i3 = (int) (f * (i / deviceProfile.widthPx));
            boolean zIsSeascape = deviceProfile.isSeascape();
            int i4 = GravityCompat.START;
            if (zIsSeascape) {
                layoutParams.gravity = (isRtl ? 8388613 : 8388611) | 48;
                if (isRtl) {
                    i4 = 8388613;
                }
                layoutParams2.gravity = i4 | 48;
                if (splitConfig.initiatedFromSeascape) {
                    primaryIconView.setTranslationX(i2 - taskIconHeight);
                    secondaryIconView.setTranslationX(i2);
                } else {
                    primaryIconView.setTranslationX(r0 - taskIconHeight);
                    secondaryIconView.setTranslationX(i2 + i3);
                }
            } else {
                layoutParams.gravity = (isRtl ? 8388611 : 8388613) | 48;
                if (!isRtl) {
                    i4 = 8388613;
                }
                layoutParams2.gravity = i4 | 48;
                if (!splitConfig.initiatedFromSeascape) {
                    primaryIconView.setTranslationX(-i2);
                    secondaryIconView.setTranslationX(r8 + taskIconHeight);
                } else {
                    primaryIconView.setTranslationX((-i2) - i3);
                    secondaryIconView.setTranslationX(r9 + taskIconHeight);
                }
            }
        } else {
            layoutParams.gravity = 49;
            float f2 = taskIconHeight / 2.0f;
            primaryIconView.setTranslationX(-f2);
            layoutParams2.gravity = 49;
            secondaryIconView.setTranslationX(f2);
        }
        primaryIconView.setTranslationY(0.0f);
        secondaryIconView.setTranslationY(0.0f);
        primaryIconView.setLayoutParams(layoutParams);
        secondaryIconView.setLayoutParams(layoutParams2);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public PagedOrientationHandler.ChildBounds getChildBounds(View child, int childStart, int pageCenter, boolean layoutChild) {
        int measuredWidth = child.getMeasuredWidth();
        int i = childStart + measuredWidth;
        int measuredHeight = child.getMeasuredHeight();
        int i2 = pageCenter - (measuredHeight / 2);
        if (layoutChild) {
            child.layout(childStart, i2, i, i2 + measuredHeight);
        }
        return new PagedOrientationHandler.ChildBounds(measuredWidth, measuredHeight, i, i2);
    }
}
