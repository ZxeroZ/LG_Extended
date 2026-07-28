package com.android.launcher3.touch;

import android.content.res.Resources;
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
import com.lge.launcher3.R;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LandscapePagedViewHandler implements PagedOrientationHandler {
    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getDegreesRotated() {
        return 90.0f;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public <T> T getPrimaryValue(T x, T y) {
        return y;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getRotation() {
        return 1;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public <T> T getSecondaryValue(T x, T y) {
        return x;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getSplitTaskViewDismissDirection(int stagePosition, DeviceProfile dp) {
        return 0;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getSplitTranslationDirectionFactor(int stagePosition, DeviceProfile deviceProfile) {
        return stagePosition == 1 ? -1 : 1;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getTaskDismissDirectionFactor() {
        return 1;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getTaskDragDisplacementFactor(boolean isRtl) {
        return isRtl ? 1 : -1;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getTaskMenuLayoutOrientation(LinearLayout taskMenuLayout) {
        return 1;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getTaskMenuY(float y, View thumbnailView) {
        return y;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public boolean isGoingUp(float displacement, boolean isRtl) {
        if (isRtl) {
            if (displacement < 0.0f) {
                return true;
            }
        } else if (displacement > 0.0f) {
            return true;
        }
        return false;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public boolean isLayoutNaturalToLauncher() {
        return false;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void delegateScrollTo(PagedView pagedView, int secondaryScroll, int minMaxScroll) {
        pagedView.superScrollTo(secondaryScroll, minMaxScroll);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void delegateScrollBy(PagedView pagedView, int unboundedScroll, int x, int y) {
        pagedView.scrollTo(pagedView.getScrollX() + x, unboundedScroll + y);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void scrollerStartScroll(OverScroller scroller, int newPosition) {
        scroller.startScroll(scroller.getCurrPos(), newPosition - scroller.getCurrPos());
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void getCurveProperties(PagedView view, Rect insets, PagedOrientationHandler.CurveProperties out) {
        out.scroll = view.getScrollY();
        out.halfPageSize = view.getNormalChildHeight() / 2;
        out.halfScreenSize = view.getMeasuredHeight() / 2;
        out.screenCenter = insets.top + view.getPaddingTop() + out.scroll + out.halfPageSize;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void adjustFloatingIconStartVelocity(PointF velocity) {
        velocity.set(-velocity.y, velocity.x);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void delegateScrollTo(PagedView pagedView, int primaryScroll) {
        pagedView.superScrollTo(pagedView.getScrollX(), primaryScroll);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public <T> void set(T target, PagedOrientationHandler.Int2DAction<T> action, int param) {
        action.call(target, 0, param);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public <T> void set(T target, PagedOrientationHandler.Float2DAction<T> action, float param) {
        action.call(target, 0.0f, param);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getPrimaryDirection(MotionEvent event, int pointerIndex) {
        return event.getY(pointerIndex);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getPrimaryVelocity(VelocityTracker velocityTracker, int pointerId) {
        return velocityTracker.getYVelocity(pointerId);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getMeasuredSize(View view) {
        return view.getMeasuredHeight();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getPrimarySize(RectF rect) {
        return rect.height();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getClearAllScrollOffset(View view, boolean isRtl) {
        return (isRtl ? view.getPaddingBottom() : -view.getPaddingTop()) / 2;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getSecondaryDimension(View view) {
        return view.getWidth();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public FloatProperty<View> getPrimaryViewTranslate() {
        return LauncherAnimUtils.VIEW_TRANSLATE_Y;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public FloatProperty<View> getSecondaryViewTranslate() {
        return LauncherAnimUtils.VIEW_TRANSLATE_X;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setPrimaryAndResetSecondaryTranslate(View view, float translation) {
        view.setTranslationX(0.0f);
        view.setTranslationY(translation);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getPrimaryScroll(View view) {
        return view.getScrollY();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getPrimaryScale(View view) {
        return view.getScaleY();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setMaxScroll(AccessibilityEvent event, int maxScroll) {
        event.setMaxScrollY(maxScroll);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public boolean getRecentsRtlSetting(Resources resources) {
        return !Utilities.isRtl(resources);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setPrimaryScale(View view, float scale) {
        view.setScaleY(scale);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setSecondaryScale(View view, float scale) {
        view.setScaleX(scale);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getChildStart(View view) {
        return view.getTop();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getChildStartWithTranslation(View view) {
        return view.getTop() + view.getTranslationY();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getCenterForPage(View view, Rect insets) {
        return ((((view.getPaddingLeft() + view.getMeasuredWidth()) + insets.left) - insets.right) - view.getPaddingRight()) / 2;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getScrollOffsetStart(View view, Rect insets) {
        return insets.top + view.getPaddingTop();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getScrollOffsetEnd(View view, Rect insets) {
        return (view.getHeight() - view.getPaddingBottom()) - insets.bottom;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public SingleAxisSwipeDetector.Direction getOppositeSwipeDirection(boolean isRtl) {
        return isRtl ? SingleAxisSwipeDetector.SEASCAPE_HORIZONTAL : SingleAxisSwipeDetector.HORIZONTAL;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public float getTaskMenuX(float x, View thumbnailView) {
        return thumbnailView.getMeasuredWidth() + x;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getTaskMenuWidth(View view) {
        return view.getMeasuredHeight();
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setLayoutParamsForTaskMenuOptionItem(LinearLayout.LayoutParams lp) {
        lp.width = 0;
        lp.height = -2;
        lp.weight = 1.0f;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public Pair<FloatProperty, FloatProperty> getSplitSelectTaskOffset(FloatProperty primary, FloatProperty secondary, DeviceProfile deviceProfile) {
        return new Pair<>(primary, secondary);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public List<SplitConfigurationOptions.SplitPositionOption> getSplitPositionOptions(DeviceProfile dp) {
        return Collections.singletonList(new SplitConfigurationOptions.SplitPositionOption(R.drawable.recentapp_ic_dualwindow_normal, R.string.recentapps_name_multi_window, 0, 0));
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void getInitialSplitPlaceholderBounds(int placeholderHeight, int placeholderInset, DeviceProfile dp, int stagePosition, Rect out) {
        out.set(0, 0, dp.widthPx, dp.getInsets().top + placeholderHeight);
        out.inset(placeholderInset, 0);
        out.top -= ((int) ((((dp.heightPx * 1.0f) / 2.0f) * (r6 - (placeholderInset * 2))) / dp.widthPx)) - placeholderHeight;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void updateStagedSplitIconParams(View out, float onScreenRectCenterX, float onScreenRectCenterY, float fullscreenScaleX, float fullscreenScaleY, int drawableWidth, int drawableHeight, DeviceProfile dp, int stagePosition) {
        float f = dp.getInsets().top;
        out.setX(Math.round((onScreenRectCenterX / fullscreenScaleX) - ((drawableWidth * 1.0f) / 2.0f)));
        out.setY(Math.round(((onScreenRectCenterY + (f / 2.0f)) / fullscreenScaleY) - ((drawableHeight * 1.0f) / 2.0f)));
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void getFinalSplitPlaceholderBounds(int splitDividerSize, DeviceProfile dp, int stagePosition, Rect out1, Rect out2) {
        int i = dp.heightPx;
        int i2 = dp.widthPx;
        int i3 = i / 2;
        out1.set(0, 0, i2, i3 - splitDividerSize);
        out2.set(0, i3 + splitDividerSize, i2, i);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public int getDefaultSplitPosition(DeviceProfile deviceProfile) {
        throw new IllegalStateException("Default position not available in fake landscape");
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setSplitTaskSwipeRect(DeviceProfile dp, Rect outRect, SplitConfigurationOptions.StagedSplitBounds splitInfo, int desiredStagePosition) {
        float f;
        float f2;
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
            outRect.bottom = outRect.top + ((int) (outRect.height() * f));
        } else {
            outRect.top += (int) (outRect.height() * (f + f2));
        }
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setTaskIconParams(FrameLayout.LayoutParams iconParams, int taskIconMargin, int taskIconHeight, int thumbnailTopMargin, boolean isRtl) {
        iconParams.gravity = (isRtl ? GravityCompat.START : GravityCompat.END) | 16;
        iconParams.rightMargin = (-taskIconHeight) - (taskIconMargin / 2);
        iconParams.leftMargin = 0;
        iconParams.topMargin = thumbnailTopMargin / 2;
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public void setSplitIconParams(View primaryIconView, View secondaryIconView, int taskIconHeight, int primarySnapshotWidth, int primarySnapshotHeight, int groupedTaskViewHeight, int groupedTaskViewWidth, boolean isRtl, DeviceProfile deviceProfile, SplitConfigurationOptions.StagedSplitBounds splitConfig) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) primaryIconView.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(layoutParams);
        float f = ((deviceProfile.heightPx - r9) / 2) / deviceProfile.heightPx;
        float f2 = deviceProfile.getInsets().top / deviceProfile.heightPx;
        float f3 = groupedTaskViewHeight - deviceProfile.overviewModeWodrkspaceTranslationYPxInMultiWindow;
        int i = (int) (f * f3);
        int i2 = (int) (f3 * f2);
        int i3 = GravityCompat.START;
        layoutParams.gravity = (isRtl ? 8388611 : 8388613) | 80;
        if (!isRtl) {
            i3 = 8388613;
        }
        layoutParams2.gravity = i3 | 80;
        primaryIconView.setTranslationX(0.0f);
        secondaryIconView.setTranslationX(0.0f);
        if (splitConfig.initiatedFromSeascape) {
            primaryIconView.setTranslationY((-i) - i2);
            secondaryIconView.setTranslationY(r9 + taskIconHeight);
        } else {
            primaryIconView.setTranslationY(-i);
            secondaryIconView.setTranslationY(r8 + taskIconHeight);
        }
        primaryIconView.setLayoutParams(layoutParams);
        secondaryIconView.setLayoutParams(layoutParams2);
    }

    @Override // com.android.launcher3.touch.PagedOrientationHandler
    public PagedOrientationHandler.ChildBounds getChildBounds(View child, int childStart, int pageCenter, boolean layoutChild) {
        int measuredHeight = child.getMeasuredHeight();
        int i = childStart + measuredHeight;
        int measuredWidth = child.getMeasuredWidth();
        int i2 = pageCenter - (measuredWidth / 2);
        if (layoutChild) {
            child.layout(i2, childStart, i2 + measuredWidth, i);
        }
        return new PagedOrientationHandler.ChildBounds(measuredHeight, measuredWidth, i, i2);
    }
}
