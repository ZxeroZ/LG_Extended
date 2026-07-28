package com.android.launcher3.touch;

import android.content.res.Resources;
import android.graphics.Canvas;
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
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.PagedView;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.OverScroller;
import com.android.launcher3.util.SplitConfigurationOptions;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface PagedOrientationHandler {
    public static final PagedOrientationHandler PORTRAIT = new PortraitPagedViewHandler();
    public static final PagedOrientationHandler LANDSCAPE = new LandscapePagedViewHandler();
    public static final PagedOrientationHandler SEASCAPE = new SeascapePagedViewHandler();
    public static final Int2DAction<View> VIEW_SCROLL_BY = new Int2DAction() { // from class: com.android.launcher3.touch.-$$Lambda$PagedOrientationHandler$k1Q3MNHnKzExQ9f-AcIyonKafcM
        @Override // com.android.launcher3.touch.PagedOrientationHandler.Int2DAction
        public final void call(Object obj, int i, int i2) {
            ((View) obj).scrollBy(i, i2);
        }
    };
    public static final Int2DAction<View> VIEW_SCROLL_TO = new Int2DAction() { // from class: com.android.launcher3.touch.-$$Lambda$PagedOrientationHandler$RSyS5_SLzg8RAnh9nfGVI-CQSBI
        @Override // com.android.launcher3.touch.PagedOrientationHandler.Int2DAction
        public final void call(Object obj, int i, int i2) {
            ((View) obj).scrollTo(i, i2);
        }
    };
    public static final Float2DAction<Canvas> CANVAS_TRANSLATE = new Float2DAction() { // from class: com.android.launcher3.touch.-$$Lambda$PagedOrientationHandler$HT0D7S8EJdDOP2efJkAQgNIVjW8
        @Override // com.android.launcher3.touch.PagedOrientationHandler.Float2DAction
        public final void call(Object obj, float f, float f2) {
            ((Canvas) obj).translate(f, f2);
        }
    };
    public static final Float2DAction<Matrix> MATRIX_POST_TRANSLATE = new Float2DAction() { // from class: com.android.launcher3.touch.-$$Lambda$PagedOrientationHandler$nlcrEZjfXzHAociO2IEsX0xVlxg
        @Override // com.android.launcher3.touch.PagedOrientationHandler.Float2DAction
        public final void call(Object obj, float f, float f2) {
            ((Matrix) obj).postTranslate(f, f2);
        }
    };

    public static class CurveProperties {
        public int halfPageSize;
        public int halfScreenSize;
        public int screenCenter;
        public int scroll;
    }

    public interface Float2DAction<T> {
        void call(T target, float x, float y);
    }

    public interface Int2DAction<T> {
        void call(T target, int x, int y);
    }

    void adjustFloatingIconStartVelocity(PointF velocity);

    void delegateScrollBy(PagedView pagedView, int unboundedScroll, int x, int y);

    void delegateScrollTo(PagedView pagedView, int primaryScroll);

    void delegateScrollTo(PagedView pagedView, int secondaryScroll, int primaryScroll);

    int getCenterForPage(View view, Rect insets);

    ChildBounds getChildBounds(View child, int childStart, int pageCenter, boolean layoutChild);

    int getChildStart(View view);

    float getChildStartWithTranslation(View view);

    int getClearAllScrollOffset(View view, boolean isRtl);

    void getCurveProperties(PagedView view, Rect insets, CurveProperties out);

    int getDefaultSplitPosition(DeviceProfile deviceProfile);

    float getDegreesRotated();

    void getFinalSplitPlaceholderBounds(int splitDividerSize, DeviceProfile dp, int stagePosition, Rect out1, Rect out2);

    void getInitialSplitPlaceholderBounds(int placeholderHeight, int placeholderInset, DeviceProfile dp, int stagePosition, Rect out);

    int getMeasuredSize(View view);

    SingleAxisSwipeDetector.Direction getOppositeSwipeDirection(boolean isRtl);

    float getPrimaryDirection(MotionEvent event, int pointerIndex);

    float getPrimaryScale(View view);

    int getPrimaryScroll(View view);

    float getPrimarySize(RectF rect);

    <T> T getPrimaryValue(T x, T y);

    float getPrimaryVelocity(VelocityTracker velocityTracker, int pointerId);

    FloatProperty<View> getPrimaryViewTranslate();

    boolean getRecentsRtlSetting(Resources resources);

    int getRotation();

    int getScrollOffsetEnd(View view, Rect insets);

    int getScrollOffsetStart(View view, Rect insets);

    int getSecondaryDimension(View view);

    <T> T getSecondaryValue(T x, T y);

    FloatProperty<View> getSecondaryViewTranslate();

    List<SplitConfigurationOptions.SplitPositionOption> getSplitPositionOptions(DeviceProfile dp);

    Pair<FloatProperty, FloatProperty> getSplitSelectTaskOffset(FloatProperty primary, FloatProperty secondary, DeviceProfile deviceProfile);

    int getSplitTaskViewDismissDirection(int stagePosition, DeviceProfile dp);

    int getSplitTranslationDirectionFactor(int stagePosition, DeviceProfile deviceProfile);

    int getTaskDismissDirectionFactor();

    int getTaskDragDisplacementFactor(boolean isRtl);

    int getTaskMenuLayoutOrientation(LinearLayout taskMenuLayout);

    int getTaskMenuWidth(View view);

    float getTaskMenuX(float x, View thumbnailView);

    float getTaskMenuY(float y, View thumbnailView);

    boolean isGoingUp(float displacement, boolean isRtl);

    boolean isLayoutNaturalToLauncher();

    void scrollerStartScroll(OverScroller scroller, int newPosition);

    <T> void set(T target, Float2DAction<T> action, float param);

    <T> void set(T target, Int2DAction<T> action, int param);

    void setLayoutParamsForTaskMenuOptionItem(LinearLayout.LayoutParams lp);

    void setMaxScroll(AccessibilityEvent event, int maxScroll);

    void setPrimaryAndResetSecondaryTranslate(View view, float translation);

    void setPrimaryScale(View view, float scale);

    void setSecondaryScale(View view, float scale);

    void setSplitIconParams(View primaryIconView, View secondaryIconView, int taskIconHeight, int primarySnapshotWidth, int primarySnapshotHeight, int groupedTaskViewHeight, int groupedTaskViewWidth, boolean isRtl, DeviceProfile deviceProfile, SplitConfigurationOptions.StagedSplitBounds splitConfig);

    void setSplitTaskSwipeRect(DeviceProfile dp, Rect outRect, SplitConfigurationOptions.StagedSplitBounds splitInfo, int desiredStagePosition);

    void setTaskIconParams(FrameLayout.LayoutParams iconParams, int taskIconMargin, int taskIconHeight, int thumbnailTopMargin, boolean isRtl);

    void updateStagedSplitIconParams(View out, float onScreenRectCenterX, float onScreenRectCenterY, float fullscreenScaleX, float fullscreenScaleY, int drawableWidth, int drawableHeight, DeviceProfile dp, int stagePosition);

    public static class ChildBounds {
        public final int childPrimaryEnd;
        public final int childSecondaryEnd;
        public final int primaryDimension;
        public final int secondaryDimension;

        ChildBounds(int primaryDimension, int secondaryDimension, int childPrimaryEnd, int childSecondaryEnd) {
            this.primaryDimension = primaryDimension;
            this.secondaryDimension = secondaryDimension;
            this.childPrimaryEnd = childPrimaryEnd;
            this.childSecondaryEnd = childSecondaryEnd;
        }
    }
}
