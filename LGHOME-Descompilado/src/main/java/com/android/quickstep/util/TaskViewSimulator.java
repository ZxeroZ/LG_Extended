package com.android.quickstep.util;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.IntProperty;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.util.TransformParams;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import java.util.function.IntConsumer;

/* JADX INFO: loaded from: classes.dex */
public class TaskViewSimulator implements TransformParams.BuilderProxy {
    public static final IntProperty<TaskViewSimulator> SCROLL = new IntProperty<TaskViewSimulator>("scroll") { // from class: com.android.quickstep.util.TaskViewSimulator.1
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;I)V */
        @Override // android.util.IntProperty
        public void setValue(TaskViewSimulator simulator, int i) {
            simulator.setScroll(i);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Integer get(TaskViewSimulator simulator) {
            return Integer.valueOf(simulator.mScrollState.scroll);
        }
    };
    private final Context mContext;
    private final TaskView.FullscreenDrawParams mCurrentFullscreenParams;
    private DeviceProfile mDp;
    private final RecentsOrientedState mOrientationState;
    private final int mPageSpacing;
    private final BaseActivityInterface mSizeStrategy;
    private final Rect mTmpCropRect = new Rect();
    private final RectF mTempRectF = new RectF();
    private final float[] mTempPoint = new float[2];
    private final Rect mTaskRect = new Rect();
    private final PointF mPivot = new PointF();
    private final Matrix mMatrix = new Matrix();
    private final Point mRunningTargetWindowPosition = new Point();
    private final Rect mThumbnailPosition = new Rect();
    private final ThumbnailData mThumbnailData = new ThumbnailData();
    private final TaskThumbnailView.PreviewPositionHelper mPositionHelper = new TaskThumbnailView.PreviewPositionHelper();
    private final Matrix mInversePositionMatrix = new Matrix();
    private float mCurveScale = 1.0f;
    public final AnimatedFloat recentsViewScale = new AnimatedFloat();
    public final AnimatedFloat fullScreenProgress = new AnimatedFloat();
    private final RecentsView.ScrollState mScrollState = new RecentsView.ScrollState();
    private boolean mLayoutValid = false;
    private boolean mScrollValid = false;

    static /* synthetic */ void lambda$new$0(int i) {
    }

    public TaskViewSimulator(Context context, BaseActivityInterface sizeStrategy) {
        this.mContext = context;
        this.mSizeStrategy = sizeStrategy;
        RecentsOrientedState recentsOrientedState = new RecentsOrientedState(context, sizeStrategy, new IntConsumer() { // from class: com.android.quickstep.util.-$$Lambda$TaskViewSimulator$cgnMqiFiMk2ykDJXOYOfiJ0QId0
            @Override // java.util.function.IntConsumer
            public final void accept(int i) {
                TaskViewSimulator.lambda$new$0(i);
            }
        });
        this.mOrientationState = recentsOrientedState;
        LGLog.d("TaskViewSimulator", "TaskViewSimulator : mOrientationState = " + recentsOrientedState + ", " + this);
        recentsOrientedState.setGestureActive(true);
        this.mCurrentFullscreenParams = new TaskView.FullscreenDrawParams(context);
        this.mPageSpacing = context.getResources().getDimensionPixelSize(R.dimen.recents_page_spacing);
    }

    public void setDp(DeviceProfile dp) {
        this.mDp = dp;
        this.mOrientationState.setMultiWindowMode(dp.isMultiWindowMode);
        this.mLayoutValid = false;
    }

    public void setLayoutRotation(int touchRotation, int displayRotation) {
        this.mOrientationState.update(touchRotation, displayRotation);
        this.mLayoutValid = false;
    }

    public void setRecentsConfiguration(Configuration configuration) {
        this.mOrientationState.setActivityConfiguration(configuration);
        this.mLayoutValid = false;
    }

    public float getFullScreenScale() {
        DeviceProfile deviceProfile = this.mDp;
        if (deviceProfile == null) {
            return 1.0f;
        }
        this.mSizeStrategy.calculateTaskSize(this.mContext, deviceProfile, this.mTaskRect, this.mOrientationState.getOrientationHandler());
        return this.mOrientationState.getFullScreenScaleAndPivot(this.mTaskRect, this.mDp, this.mPivot);
    }

    public void setPreview(RemoteAnimationTargetCompat runningTarget) {
        setPreviewBounds(runningTarget.screenSpaceBounds, runningTarget.contentInsets);
        this.mRunningTargetWindowPosition.set(runningTarget.screenSpaceBounds.left, runningTarget.screenSpaceBounds.top);
    }

    public void setPreviewBounds(Rect bounds, Rect insets) {
        this.mThumbnailData.insets.set(insets);
        this.mThumbnailData.windowingMode = 1;
        this.mThumbnailPosition.set(bounds);
        this.mLayoutValid = false;
    }

    public void setScroll(int scroll) {
        if (this.mScrollState.scroll != scroll) {
            this.mScrollState.scroll = scroll;
            this.mScrollValid = false;
        }
    }

    public void addAppToOverviewAnim(PendingAnimation pa, TimeInterpolator interpolator) {
        pa.addFloat(this.fullScreenProgress, AnimatedFloat.VALUE, 1.0f, 0.0f, interpolator);
        pa.addFloat(this.recentsViewScale, AnimatedFloat.VALUE, getFullScreenScale(), 1.0f, interpolator);
    }

    public RectF getCurrentCropRect() {
        RectF rectF = this.mCurrentFullscreenParams.mCurrentDrawnInsets;
        this.mTempRectF.set(-rectF.left, -rectF.top, this.mTaskRect.width() + rectF.right, this.mTaskRect.height() + rectF.bottom);
        this.mInversePositionMatrix.mapRect(this.mTempRectF);
        return this.mTempRectF;
    }

    public RecentsOrientedState getOrientationState() {
        return this.mOrientationState;
    }

    public Matrix getCurrentMatrix() {
        return this.mMatrix;
    }

    public void applyWindowToHomeRotation(Matrix matrix) {
        this.mMatrix.postTranslate(this.mDp.windowX, this.mDp.windowY);
        RecentsOrientedState.postDisplayRotation(RotationHelper.deltaRotation(this.mOrientationState.getRecentsActivityRotation(), this.mOrientationState.getDisplayRotation()), this.mDp.widthPx, this.mDp.heightPx, matrix);
        matrix.postTranslate(-this.mRunningTargetWindowPosition.x, -this.mRunningTargetWindowPosition.y);
    }

    public void apply(TransformParams params) {
        if (this.mDp == null || this.mThumbnailPosition.isEmpty()) {
            return;
        }
        if (!this.mLayoutValid) {
            this.mLayoutValid = true;
            getFullScreenScale();
            this.mThumbnailData.rotation = this.mOrientationState.getDisplayRotation();
            this.mPositionHelper.updateThumbnailMatrix(this.mThumbnailPosition, this.mThumbnailData, this.mTaskRect.width(), this.mTaskRect.height(), this.mDp, this.mOrientationState.getRecentsActivityRotation(), this.mContext);
            this.mPositionHelper.getMatrix().invert(this.mInversePositionMatrix);
            PagedOrientationHandler orientationHandler = this.mOrientationState.getOrientationHandler();
            this.mScrollState.halfPageSize = ((Integer) orientationHandler.getPrimaryValue(Integer.valueOf(this.mTaskRect.width()), Integer.valueOf(this.mTaskRect.height()))).intValue() / 2;
            this.mScrollState.halfScreenSize = ((Integer) orientationHandler.getPrimaryValue(Integer.valueOf(this.mDp.widthPx), Integer.valueOf(this.mDp.heightPx))).intValue() / 2;
            this.mScrollValid = false;
        }
        if (!this.mScrollValid) {
            this.mScrollValid = true;
            int iIntValue = ((Integer) this.mOrientationState.getOrientationHandler().getPrimaryValue(Integer.valueOf(this.mTaskRect.left), Integer.valueOf(this.mTaskRect.top))).intValue();
            RecentsView.ScrollState scrollState = this.mScrollState;
            scrollState.screenCenter = scrollState.scroll + iIntValue + this.mScrollState.halfPageSize;
            this.mScrollState.updateInterpolation(iIntValue, this.mPageSpacing);
            this.mCurveScale = TaskView.getCurveScaleForInterpolation(this.mScrollState.linearInterpolation);
        }
        this.mCurrentFullscreenParams.setProgress(Utilities.boundToRange(this.fullScreenProgress.value, 0.0f, 1.0f), this.recentsViewScale.value, this.mTaskRect.width(), this.mDp, this.mPositionHelper);
        RectF rectF = this.mCurrentFullscreenParams.mCurrentDrawnInsets;
        float f = this.mCurrentFullscreenParams.mScale;
        float fWidth = this.mTaskRect.width();
        float fHeight = this.mTaskRect.height();
        this.mMatrix.set(this.mPositionHelper.getMatrix());
        this.mMatrix.postTranslate(rectF.left, rectF.top);
        this.mMatrix.postScale(f, f);
        this.mMatrix.postTranslate(this.mTaskRect.left, this.mTaskRect.top);
        Matrix matrix = this.mMatrix;
        float f2 = this.mCurveScale;
        matrix.postScale(f2, f2, fWidth / 2.0f, fHeight / 2.0f);
        this.mOrientationState.getOrientationHandler().set(this.mMatrix, PagedOrientationHandler.MATRIX_POST_TRANSLATE, this.mScrollState.scroll);
        this.mMatrix.postScale(this.recentsViewScale.value, this.recentsViewScale.value, this.mPivot.x, this.mPivot.y);
        applyWindowToHomeRotation(this.mMatrix);
        this.mTempRectF.set(-rectF.left, -rectF.top, fWidth + rectF.right, fHeight + rectF.bottom);
        this.mInversePositionMatrix.mapRect(this.mTempRectF);
        this.mTempRectF.roundOut(this.mTmpCropRect);
        params.applySurfaceParams(params.createSurfaceParams(this));
    }

    @Override // com.android.quickstep.util.TransformParams.BuilderProxy
    public void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat app, TransformParams params) {
        builder.withMatrix(this.mMatrix).withWindowCrop(this.mTmpCropRect).withCornerRadius(getCurrentCornerRadius());
    }

    public float getCurrentCornerRadius() {
        float f = this.mCurrentFullscreenParams.mCurrentDrawnCornerRadius;
        float[] fArr = this.mTempPoint;
        fArr[0] = f;
        fArr[1] = 0.0f;
        this.mInversePositionMatrix.mapVectors(fArr);
        return Math.max(Math.abs(this.mTempPoint[0]), Math.abs(this.mTempPoint[1]));
    }
}
