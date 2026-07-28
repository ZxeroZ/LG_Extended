package com.android.quickstep.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Insets;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Property;
import android.view.View;
import androidx.core.view.ViewCompat;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.Themes;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.views.TaskView;
import com.android.systemui.plugins.OverviewScreenshotActions;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ConfigurationCompat;
import com.lge.launcher3.CustomUIManager;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class TaskThumbnailView extends View implements PluginListener<OverviewScreenshotActions> {
    public static final boolean DEBUG = false;
    private static final String TAG = "TaskThumbnailView";
    private final BaseActivity mActivity;
    private final Paint mBackgroundPaint;
    private final Paint mBackgroundPaintForLetterbox;
    protected BitmapShader mBitmapShader;
    private final Paint mClearPaint;
    private float mCornerRadius;
    private float mDimAlpha;
    private float mDimAlphaMultiplier;
    private final Paint mDimmingPaintAfterClearing;
    private TaskView.FullscreenDrawParams mFullscreenParams;
    private final boolean mIsDarkTextTheme;
    private final TaskOverlayFactory.TaskOverlay mOverlay;
    private boolean mOverlayEnabled;
    private OverviewScreenshotActions mOverviewScreenshotActionsPlugin;
    private final Paint mPaint;
    private final PreviewPositionHelper mPreviewPositionHelper;
    private final Rect mPreviewRect;
    private float mSaturation;
    private Task mTask;
    private TaskHeaderView mTaskHeader;
    private ThumbnailData mThumbnailData;
    boolean needDrawBackgroundForLetterbox;
    private static final ColorMatrix COLOR_MATRIX = new ColorMatrix();
    private static final ColorMatrix SATURATION_COLOR_MATRIX = new ColorMatrix();
    private static final MainThreadInitializedObject<TaskView.FullscreenDrawParams> TEMP_PARAMS = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.quickstep.views.-$$Lambda$RhdQ5MP_gY2iW0r-nGffucvTJ9M
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return new TaskView.FullscreenDrawParams(context);
        }
    });
    public static final Property<TaskThumbnailView, Float> DIM_ALPHA = new FloatProperty<TaskThumbnailView>("dimAlpha") { // from class: com.android.quickstep.views.TaskThumbnailView.1
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TaskThumbnailView thumbnail, float dimAlpha) {
            thumbnail.setDimAlpha(dimAlpha);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TaskThumbnailView thumbnailView) {
            return Float.valueOf(thumbnailView.mDimAlpha);
        }
    };
    private static final LightingColorFilter[] sDimFilterCache = new LightingColorFilter[256];
    private static final LightingColorFilter[] sHighlightFilterCache = new LightingColorFilter[256];

    public TaskThumbnailView(Context context) {
        this(context, null);
    }

    public TaskThumbnailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskThumbnailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Paint paint = new Paint(1);
        this.mPaint = paint;
        Paint paint2 = new Paint(1);
        this.mBackgroundPaint = paint2;
        Paint paint3 = new Paint();
        this.mClearPaint = paint3;
        Paint paint4 = new Paint();
        this.mDimmingPaintAfterClearing = paint4;
        this.mPreviewRect = new Rect();
        this.mPreviewPositionHelper = new PreviewPositionHelper();
        this.mDimAlpha = 1.0f;
        this.mDimAlphaMultiplier = 1.0f;
        this.mSaturation = 1.0f;
        Paint paint5 = new Paint(1);
        this.mBackgroundPaintForLetterbox = paint5;
        this.mOverlay = TaskOverlayFactory.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).createOverlay(this);
        paint.setFilterBitmap(true);
        paint2.setColor(-1);
        paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint4.setColor(ViewCompat.MEASURED_STATE_MASK);
        BaseActivity baseActivityFromContext = BaseActivity.fromContext(context);
        this.mActivity = baseActivityFromContext;
        this.mIsDarkTextTheme = Themes.getAttrBoolean(baseActivityFromContext, R.attr.isWorkspaceDarkText);
        this.mFullscreenParams = TEMP_PARAMS.lambda$get$0$MainThreadInitializedObject(context);
        if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
            this.mCornerRadius = getResources().getDimension(R.dimen.overview_ux_9_21_task_corner_radius);
        } else {
            this.mCornerRadius = getResources().getDimension(R.dimen.overview_new_ui_task_corner_radius);
        }
        if (LGHomeFeature.Config.FEATURE_USE_LETTERBOX_FOR_THUMBNAIL.getValue()) {
            paint5.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
            if (baseActivityFromContext == null || baseActivityFromContext.getDeviceProfile() == null) {
                return;
            }
            paint5.setColor(baseActivityFromContext.getDeviceProfile().inv.mColorOfLetterBox);
        }
    }

    public void bind(Task task) {
        this.mOverlay.reset();
        this.mTask = task;
        int i = ViewCompat.MEASURED_STATE_MASK;
        if (task != null) {
            i = (-16777216) | task.colorBackground;
        }
        this.mPaint.setColor(i);
        this.mBackgroundPaint.setColor(i);
    }

    public void setThumbnail(Task task, ThumbnailData thumbnailData, boolean refreshNow) {
        this.mTask = task;
        if (thumbnailData == null || thumbnailData.thumbnail == null) {
            thumbnailData = null;
        }
        this.mThumbnailData = thumbnailData;
        if (refreshNow) {
            refresh();
        }
    }

    public void setThumbnail(Task task, ThumbnailData thumbnailData) {
        setThumbnail(task, thumbnailData, true);
    }

    public void refresh() {
        ThumbnailData thumbnailData = this.mThumbnailData;
        if (thumbnailData != null && thumbnailData.thumbnail != null) {
            Bitmap bitmap = this.mThumbnailData.thumbnail;
            bitmap.prepareToDraw();
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.DECAL, Shader.TileMode.DECAL);
            this.mBitmapShader = bitmapShader;
            this.mPaint.setShader(bitmapShader);
            updateThumbnailMatrix();
        } else {
            this.mBitmapShader = null;
            this.mThumbnailData = null;
            this.mPaint.setShader(null);
            this.mOverlay.reset();
        }
        OverviewScreenshotActions overviewScreenshotActions = this.mOverviewScreenshotActionsPlugin;
        if (overviewScreenshotActions != null) {
            overviewScreenshotActions.setupActions(getTaskView(), getThumbnail(), this.mActivity);
        }
        updateThumbnailPaintFilter();
    }

    public void setDimAlphaMultipler(float dimAlphaMultipler) {
        this.mDimAlphaMultiplier = dimAlphaMultipler;
        setDimAlpha(this.mDimAlpha);
    }

    public void setDimAlpha(float dimAlpha) {
        this.mDimAlpha = dimAlpha;
        updateThumbnailPaintFilter();
    }

    public TaskOverlayFactory.TaskOverlay getTaskOverlay() {
        return this.mOverlay;
    }

    public float getDimAlpha() {
        return this.mDimAlpha;
    }

    public Rect getInsets(Rect fallback) {
        ThumbnailData thumbnailData = this.mThumbnailData;
        return thumbnailData != null ? thumbnailData.insets : fallback;
    }

    public Insets getScaledInsets() {
        if (this.mThumbnailData == null) {
            return Insets.NONE;
        }
        RectF rectF = new RectF(0.0f, 0.0f, this.mThumbnailData.thumbnail.getWidth(), this.mThumbnailData.thumbnail.getHeight());
        RectF rectF2 = new RectF(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        Matrix matrix = new Matrix();
        this.mPreviewPositionHelper.getMatrix().invert(matrix);
        RectF rectF3 = new RectF();
        matrix.mapRect(rectF3, rectF2);
        return Insets.of(Math.round(rectF3.left), Math.round(rectF3.top), Math.round(rectF.right - rectF3.right), Math.round(rectF.bottom - rectF3.bottom));
    }

    public int getSysUiStatusNavFlags() {
        ThumbnailData thumbnailData = this.mThumbnailData;
        if (thumbnailData != null) {
            return ((thumbnailData.appearance & 8) != 0 ? 4 : 8) | 0 | ((this.mThumbnailData.appearance & 16) != 0 ? 1 : 2);
        }
        return 0;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        RectF rectF = this.mFullscreenParams.mCurrentDrawnInsets;
        canvas.save();
        canvas.scale(this.mFullscreenParams.mScale, this.mFullscreenParams.mScale);
        canvas.translate(rectF.left, rectF.top);
        drawOnCanvas(canvas, -rectF.left, -rectF.top, getMeasuredWidth() + rectF.right, getMeasuredHeight() + rectF.bottom, this.mFullscreenParams.mCurrentDrawnCornerRadius);
        canvas.restore();
    }

    /* JADX DEBUG: Method merged with bridge method: onPluginConnected(Lcom/android/systemui/plugins/Plugin;Landroid/content/Context;)V */
    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginConnected(OverviewScreenshotActions overviewScreenshotActions, Context context) {
        this.mOverviewScreenshotActionsPlugin = overviewScreenshotActions;
        overviewScreenshotActions.setupActions(getTaskView(), getThumbnail(), this.mActivity);
    }

    /* JADX DEBUG: Method merged with bridge method: onPluginDisconnected(Lcom/android/systemui/plugins/Plugin;)V */
    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginDisconnected(OverviewScreenshotActions plugin) {
        if (this.mOverviewScreenshotActionsPlugin != null) {
            this.mOverviewScreenshotActionsPlugin = null;
        }
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PluginManagerWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).addPluginListener(this, OverviewScreenshotActions.class);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        PluginManagerWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).removePluginListener(this);
    }

    public PreviewPositionHelper getPreviewPositionHelper() {
        return this.mPreviewPositionHelper;
    }

    public void setFullscreenParams(TaskView.FullscreenDrawParams fullscreenParams) {
        this.mFullscreenParams = fullscreenParams;
        invalidate();
    }

    public void drawOnCanvas(Canvas canvas, float x, float y, float width, float height, float cornerRadius) {
        BaseActivity baseActivity;
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && this.mTask != null && getTaskView().isRunningTask() && !getTaskView().showScreenshot()) {
            canvas.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius, this.mClearPaint);
            canvas.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius, this.mDimmingPaintAfterClearing);
            return;
        }
        Task task = this.mTask;
        boolean z = false;
        boolean z2 = task == null || task.isLocked || this.mBitmapShader == null || this.mThumbnailData == null;
        if (LGHomeFeature.Config.FEATURE_USE_LETTERBOX_FOR_THUMBNAIL.getValue() && (this.mPreviewPositionHelper.letterBoxRect.width() != 0 || this.mPreviewPositionHelper.letterBoxRect.height() != 0)) {
            z = true;
        }
        this.needDrawBackgroundForLetterbox = z;
        if (z) {
            if (CustomUIManager.getInstance(getContext()).isEnabled() && (baseActivity = this.mActivity) != null && baseActivity.getDeviceProfile() != null) {
                this.mBackgroundPaintForLetterbox.setColor(this.mActivity.getDeviceProfile().inv.mColorOfLetterBox);
            }
            float f = cornerRadius + 1.0f;
            canvas.drawRoundRect(x, y, width, height, f, f, this.mBackgroundPaintForLetterbox);
            if (z2) {
                return;
            }
        } else if (z2 || this.mPreviewPositionHelper.mClipBottom > 0.0f || this.mThumbnailData.isTranslucent || this.mThumbnailData.insets.bottom == 0) {
            float f2 = cornerRadius + 1.0f;
            canvas.drawRoundRect(x, y, width, height, f2, f2, this.mBackgroundPaint);
            if (z2) {
                return;
            }
        }
        if (!LGHomeFeature.Config.FEATURE_USE_LETTERBOX_FOR_THUMBNAIL.getValue() || !this.mPreviewPositionHelper.needLetterbox) {
            if (this.mPreviewPositionHelper.mClipBottom > 0.0f) {
                canvas.save();
                canvas.clipRect(x, y, width, this.mPreviewPositionHelper.mClipBottom);
                canvas.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius, this.mPaint);
                canvas.restore();
                return;
            }
            canvas.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius, this.mPaint);
            return;
        }
        if (this.mPreviewPositionHelper.mClipBottom > 0.0f) {
            canvas.save();
            if (this.mPreviewPositionHelper.letterBoxRect.width() == 0) {
                canvas.clipRect(x, y, width, this.mPreviewPositionHelper.mClipBottom);
            } else {
                canvas.clipRect(this.mPreviewPositionHelper.letterBoxRect);
            }
            canvas.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius, this.mPaint);
            canvas.restore();
            return;
        }
        if (this.mPreviewPositionHelper.letterBoxRect.width() != 0) {
            canvas.clipRect(this.mPreviewPositionHelper.letterBoxRect);
        }
        canvas.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius, this.mPaint);
    }

    public TaskView getTaskView() {
        return (TaskView) getParent();
    }

    public void setOverlayEnabled(boolean overlayEnabled) {
        if (this.mOverlayEnabled != overlayEnabled) {
            this.mOverlayEnabled = overlayEnabled;
            updateOverlay();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOverlay() {
        ThumbnailData thumbnailData;
        if (this.mOverlayEnabled && this.mBitmapShader != null && (thumbnailData = this.mThumbnailData) != null) {
            this.mOverlay.initOverlay(this.mTask, thumbnailData, this.mPreviewPositionHelper.mMatrix, this.mPreviewPositionHelper.mIsOrientationChanged);
        } else {
            this.mOverlay.reset();
        }
    }

    private void updateThumbnailPaintFilter() {
        int i = (int) ((1.0f - (this.mDimAlpha * this.mDimAlphaMultiplier)) * 255.0f);
        ColorFilter colorFilter = getColorFilter(i, this.mIsDarkTextTheme, this.mSaturation);
        this.mBackgroundPaint.setColorFilter(colorFilter);
        this.mDimmingPaintAfterClearing.setAlpha(255 - i);
        if (this.mBitmapShader != null) {
            this.mPaint.setColorFilter(colorFilter);
        } else {
            this.mPaint.setColorFilter(null);
            this.mPaint.setColor(Color.argb(255, i, i, i));
        }
        invalidate();
    }

    private void updateThumbnailMatrix() {
        ThumbnailData thumbnailData;
        this.mPreviewPositionHelper.mClipBottom = -1.0f;
        this.mPreviewPositionHelper.mIsOrientationChanged = false;
        if (this.mBitmapShader != null && (thumbnailData = this.mThumbnailData) != null) {
            this.mPreviewRect.set(0, 0, thumbnailData.thumbnail.getWidth(), this.mThumbnailData.thumbnail.getHeight());
            this.mPreviewPositionHelper.updateThumbnailMatrix(this.mPreviewRect, this.mThumbnailData, getMeasuredWidth(), getMeasuredHeight(), this.mActivity.getDeviceProfile(), ConfigurationCompat.getWindowConfigurationRotation(this.mActivity.getResources().getConfiguration()), this.mActivity);
            this.mBitmapShader.setLocalMatrix(this.mPreviewPositionHelper.mMatrix);
            this.mPaint.setShader(this.mBitmapShader);
        }
        getTaskView().updateCurrentFullscreenParams(this.mPreviewPositionHelper);
        invalidate();
        post(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$TaskThumbnailView$BjgFhzlEcBTnv99gMjQ1hriP9gY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.updateOverlay();
            }
        });
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateThumbnailMatrix();
    }

    private static ColorFilter getColorFilter(int intensity, boolean shouldLighten, float saturation) {
        int iBoundToRange = Utilities.boundToRange(intensity, 0, 255);
        if (iBoundToRange == 255 && saturation == 1.0f) {
            return null;
        }
        float f = iBoundToRange / 255.0f;
        ColorMatrix colorMatrix = COLOR_MATRIX;
        colorMatrix.setScale(f, f, f, 1.0f);
        if (saturation != 1.0f) {
            ColorMatrix colorMatrix2 = SATURATION_COLOR_MATRIX;
            colorMatrix2.setSaturation(saturation);
            colorMatrix.postConcat(colorMatrix2);
        }
        if (shouldLighten) {
            float[] array = colorMatrix.getArray();
            float f2 = 255 - iBoundToRange;
            array[4] = f2;
            array[9] = f2;
            array[14] = f2;
        }
        return new ColorMatrixColorFilter(colorMatrix);
    }

    public Bitmap getThumbnail() {
        ThumbnailData thumbnailData = this.mThumbnailData;
        if (thumbnailData == null) {
            return null;
        }
        return thumbnailData.thumbnail;
    }

    public boolean isRealSnapshot() {
        ThumbnailData thumbnailData = this.mThumbnailData;
        if (thumbnailData == null) {
            return false;
        }
        return thumbnailData.isRealSnapshot;
    }

    public static class PreviewPositionHelper {
        private boolean mIsOrientationChanged;
        private final RectF mClippedInsets = new RectF();
        private final Matrix mMatrix = new Matrix();
        private float mClipBottom = -1.0f;
        public Rect letterBoxRect = new Rect();
        boolean needLetterbox = false;

        private int getRotationDelta(int oldRotation, int newRotation) {
            int i = newRotation - oldRotation;
            return i < 0 ? i + 4 : i;
        }

        private boolean isOrientationChange(int deltaRotation) {
            return deltaRotation == 1 || deltaRotation == 3;
        }

        public Matrix getMatrix() {
            return this.mMatrix;
        }

        /* JADX WARN: Code restructure failed: missing block: B:35:0x00a3, code lost:
        
            if (r12 != r22) goto L43;
         */
        /* JADX WARN: Code restructure failed: missing block: B:37:0x00a6, code lost:
        
            if (r7 != r21) goto L41;
         */
        /* JADX WARN: Code restructure failed: missing block: B:40:0x00ab, code lost:
        
            if (r7 != r21) goto L41;
         */
        /* JADX WARN: Code restructure failed: missing block: B:41:0x00ad, code lost:
        
            r12 = true;
            r8 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:42:0x00b0, code lost:
        
            if (r12 != r21) goto L43;
         */
        /* JADX WARN: Code restructure failed: missing block: B:43:0x00b2, code lost:
        
            r11 = r14;
         */
        /* JADX WARN: Code restructure failed: missing block: B:44:0x00b4, code lost:
        
            r11 = r14;
         */
        /* JADX WARN: Code restructure failed: missing block: B:45:0x00b5, code lost:
        
            r8 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:46:0x00b6, code lost:
        
            r12 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:47:0x00b7, code lost:
        
            r9 = (r9 + r23.getInsets().top) + r23.getInsets().bottom;
         */
        /* JADX WARN: Code restructure failed: missing block: B:48:0x00c9, code lost:
        
            if (r12 == false) goto L50;
         */
        /* JADX WARN: Code restructure failed: missing block: B:49:0x00cb, code lost:
        
            r7 = (int) ((r21 / 2) - ((r8 * r11) / 2.0f));
         */
        /* JADX WARN: Code restructure failed: missing block: B:50:0x00d4, code lost:
        
            r7 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:51:0x00d5, code lost:
        
            if (r8 == false) goto L53;
         */
        /* JADX WARN: Code restructure failed: missing block: B:52:0x00d7, code lost:
        
            r1 = (int) ((r22 / 2) - ((r9 * r11) / 2.0f));
         */
        /* JADX WARN: Code restructure failed: missing block: B:53:0x00e0, code lost:
        
            r1 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:54:0x00e1, code lost:
        
            if (r12 != false) goto L58;
         */
        /* JADX WARN: Code restructure failed: missing block: B:55:0x00e3, code lost:
        
            if (r8 == false) goto L57;
         */
        /* JADX WARN: Code restructure failed: missing block: B:57:0x00e6, code lost:
        
            r18.letterBoxRect.set(0, 0, 0, 0);
            r25 = r8;
         */
        /* JADX WARN: Code restructure failed: missing block: B:58:0x00ef, code lost:
        
            r25 = r8;
            r18.letterBoxRect.set(r7, r1, ((int) java.lang.Math.ceil(r8 * r11)) + r7, ((int) java.lang.Math.ceil(r9 * r11)) + r1);
         */
        /* JADX WARN: Code restructure failed: missing block: B:59:0x0107, code lost:
        
            r1 = r25;
            r7 = false;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void updateThumbnailMatrix(android.graphics.Rect r19, com.android.systemui.shared.recents.model.ThumbnailData r20, int r21, int r22, com.android.launcher3.DeviceProfile r23, int r24, android.content.Context r25) {
            /*
                r18 = this;
                r0 = r18
                r1 = r20
                r2 = r21
                r3 = r22
                r4 = r23
                r5 = -1082130432(0xffffffffbf800000, float:-1.0)
                r0.mClipBottom = r5
                float r5 = r1.scale
                android.graphics.Rect r6 = r23.getInsets()
                android.graphics.Rect r7 = r1.insets
                android.graphics.Rect r6 = r0.getBoundedInsets(r6, r7)
                r7 = 0
                if (r25 == 0) goto L22
                boolean r8 = com.lge.launcher3.util.WindowUtils.isHideNav(r25)
                goto L23
            L22:
                r8 = r7
            L23:
                if (r8 == 0) goto L27
                r6.bottom = r7
            L27:
                int r8 = r19.width()
                float r8 = (float) r8
                int r9 = r6.left
                int r10 = r6.right
                int r9 = r9 + r10
                float r9 = (float) r9
                float r9 = r9 * r5
                float r8 = r8 - r9
                int r9 = r19.height()
                float r9 = (float) r9
                int r10 = r6.top
                int r11 = r6.bottom
                int r10 = r10 + r11
                float r10 = (float) r10
                float r10 = r10 * r5
                float r9 = r9 - r10
                int r10 = r1.rotation
                r11 = r24
                int r10 = r0.getRotationDelta(r11, r10)
                boolean r11 = r4.isMultiWindowMode
                r12 = 1
                if (r11 != 0) goto L54
                int r11 = r1.windowingMode
                if (r11 != r12) goto L54
                r11 = r12
                goto L55
            L54:
                r11 = r7
            L55:
                boolean r13 = r0.isOrientationChange(r10)
                if (r13 == 0) goto L5f
                if (r11 == 0) goto L5f
                r13 = r12
                goto L60
            L5f:
                r13 = r7
            L60:
                com.lge.launcher3.util.LGHomeFeature$Config r14 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LETTERBOX_FOR_THUMBNAIL
                boolean r14 = r14.getValue()
                if (r14 == 0) goto L7e
                boolean r14 = r4.mIsMultiDisplay
                if (r14 != 0) goto L7e
                boolean r14 = r4.isMultiWindowMode
                if (r14 != 0) goto L7e
                int r14 = (int) r8
                int r15 = (int) r9
                boolean r14 = com.android.quickstep.util.LayoutUtils.isWideImage(r14, r15)
                if (r14 == 0) goto L7b
                r0.needLetterbox = r7
                goto L80
            L7b:
                r0.needLetterbox = r12
                goto L80
            L7e:
                r0.needLetterbox = r7
            L80:
                boolean r14 = r0.needLetterbox
                if (r14 == 0) goto L10b
                float r11 = (float) r3
                float r11 = r11 / r9
                float r14 = (float) r2
                float r14 = r14 / r8
                float r15 = r9 * r14
                r16 = r13
                double r12 = (double) r15
                double r12 = java.lang.Math.ceil(r12)
                int r12 = (int) r12
                float r13 = r8 * r11
                r17 = r8
                double r7 = (double) r13
                double r7 = java.lang.Math.ceil(r7)
                int r7 = (int) r7
                int r1 = r1.orientation
                r8 = 1
                if (r1 != r8) goto La9
                if (r7 <= r2) goto La6
                if (r12 == r3) goto Lb4
                goto Lb2
            La6:
                if (r7 == r2) goto Lb5
                goto Lad
            La9:
                if (r12 <= r3) goto Lb0
                if (r7 == r2) goto Lb5
            Lad:
                r12 = r8
                r8 = 0
                goto Lb7
            Lb0:
                if (r12 == r2) goto Lb4
            Lb2:
                r11 = r14
                goto Lb6
            Lb4:
                r11 = r14
            Lb5:
                r8 = 0
            Lb6:
                r12 = 0
            Lb7:
                android.graphics.Rect r1 = r23.getInsets()
                int r1 = r1.top
                float r1 = (float) r1
                float r9 = r9 + r1
                android.graphics.Rect r1 = r23.getInsets()
                int r1 = r1.bottom
                float r1 = (float) r1
                float r9 = r9 + r1
                r1 = 1073741824(0x40000000, float:2.0)
                if (r12 == 0) goto Ld4
                int r7 = r2 / 2
                float r7 = (float) r7
                float r13 = r17 * r11
                float r13 = r13 / r1
                float r7 = r7 - r13
                int r7 = (int) r7
                goto Ld5
            Ld4:
                r7 = 0
            Ld5:
                if (r8 == 0) goto Le0
                int r13 = r3 / 2
                float r13 = (float) r13
                float r14 = r9 * r11
                float r14 = r14 / r1
                float r13 = r13 - r14
                int r1 = (int) r13
                goto Le1
            Le0:
                r1 = 0
            Le1:
                if (r12 != 0) goto Lef
                if (r8 == 0) goto Le6
                goto Lef
            Le6:
                android.graphics.Rect r1 = r0.letterBoxRect
                r7 = 0
                r1.set(r7, r7, r7, r7)
                r25 = r8
                goto L107
            Lef:
                android.graphics.Rect r13 = r0.letterBoxRect
                float r14 = r17 * r11
                double r14 = (double) r14
                double r14 = java.lang.Math.ceil(r14)
                int r14 = (int) r14
                int r14 = r14 + r7
                float r9 = r9 * r11
                r25 = r8
                double r8 = (double) r9
                double r8 = java.lang.Math.ceil(r8)
                int r8 = (int) r8
                int r8 = r8 + r1
                r13.set(r7, r1, r14, r8)
            L107:
                r1 = r25
                r7 = 0
                goto L12f
            L10b:
                r17 = r8
                r8 = r12
                r16 = r13
                android.graphics.Rect r1 = r0.letterBoxRect
                r7 = 0
                r1.set(r7, r7, r7, r7)
                if (r2 != 0) goto L11c
                r1 = r7
                r12 = r1
                r11 = 0
                goto L12f
            L11c:
                if (r10 <= 0) goto L122
                if (r11 == 0) goto L122
                r12 = r8
                goto L123
            L122:
                r12 = r7
            L123:
                if (r16 == 0) goto L128
                float r1 = (float) r2
                float r1 = r1 / r9
                goto L12b
            L128:
                float r1 = (float) r2
                float r1 = r1 / r17
            L12b:
                r11 = r1
                r1 = r7
                r7 = r12
                r12 = r1
            L12f:
                android.graphics.Rect r8 = r23.getInsets()
                if (r7 != 0) goto L170
                boolean r7 = r4.isMultiWindowMode
                if (r7 == 0) goto L147
                android.graphics.RectF r7 = r0.mClippedInsets
                int r9 = r8.left
                float r9 = (float) r9
                float r9 = r9 * r5
                int r10 = r8.top
                float r10 = (float) r10
                float r10 = r10 * r5
                r7.offsetTo(r9, r10)
                goto L154
            L147:
                android.graphics.RectF r7 = r0.mClippedInsets
                int r9 = r6.left
                float r9 = (float) r9
                float r9 = r9 * r5
                int r10 = r6.top
                float r10 = (float) r10
                float r10 = r10 * r5
                r7.offsetTo(r9, r10)
            L154:
                android.graphics.Matrix r7 = r0.mMatrix
                int r9 = r6.left
                int r9 = -r9
                float r9 = (float) r9
                float r9 = r9 * r5
                android.graphics.Rect r10 = r0.letterBoxRect
                int r10 = r10.width()
                if (r10 != 0) goto L169
                int r6 = r6.top
                int r6 = -r6
                float r6 = (float) r6
                float r6 = r6 * r5
                goto L16a
            L169:
                r6 = 0
            L16a:
                r7.setTranslate(r9, r6)
                r7 = r19
                goto L175
            L170:
                r7 = r19
                r0.setThumbnailRotation(r10, r6, r5, r7)
            L175:
                if (r16 == 0) goto L182
                int r6 = r19.height()
                float r6 = (float) r6
                float r6 = r6 * r11
                int r7 = r19.width()
                goto L18c
            L182:
                int r6 = r19.width()
                float r6 = (float) r6
                float r6 = r6 * r11
                int r7 = r19.height()
            L18c:
                float r7 = (float) r7
                float r7 = r7 * r11
                android.graphics.RectF r9 = r0.mClippedInsets
                float r10 = r9.left
                float r10 = r10 * r11
                r9.left = r10
                android.graphics.RectF r9 = r0.mClippedInsets
                float r10 = r9.top
                float r10 = r10 * r11
                r9.top = r10
                boolean r4 = r4.isMultiWindowMode
                if (r4 == 0) goto L1b3
                android.graphics.RectF r2 = r0.mClippedInsets
                int r3 = r8.right
                float r3 = (float) r3
                float r3 = r3 * r5
                float r3 = r3 * r11
                r2.right = r3
                android.graphics.RectF r2 = r0.mClippedInsets
                int r3 = r8.bottom
                float r3 = (float) r3
                float r3 = r3 * r5
                float r3 = r3 * r11
                r2.bottom = r3
                goto L1ce
            L1b3:
                android.graphics.RectF r4 = r0.mClippedInsets
                float r5 = r4.left
                float r6 = r6 - r5
                float r2 = (float) r2
                float r6 = r6 - r2
                r2 = 0
                float r5 = java.lang.Math.max(r2, r6)
                r4.right = r5
                android.graphics.RectF r4 = r0.mClippedInsets
                float r5 = r4.top
                float r7 = r7 - r5
                float r3 = (float) r3
                float r7 = r7 - r3
                float r2 = java.lang.Math.max(r2, r7)
                r4.bottom = r2
            L1ce:
                android.graphics.Matrix r2 = r0.mMatrix
                r2.postScale(r11, r11)
                com.lge.launcher3.util.LGHomeFeature$Config r2 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LETTERBOX_FOR_THUMBNAIL
                boolean r2 = r2.getValue()
                if (r2 == 0) goto L1ee
                if (r12 != 0) goto L1df
                if (r1 == 0) goto L1ee
            L1df:
                android.graphics.Matrix r1 = r0.mMatrix
                android.graphics.Rect r2 = r0.letterBoxRect
                int r2 = r2.left
                float r2 = (float) r2
                android.graphics.Rect r3 = r0.letterBoxRect
                int r3 = r3.top
                float r3 = (float) r3
                r1.postTranslate(r2, r3)
            L1ee:
                r12 = r16
                r0.mIsOrientationChanged = r12
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.views.TaskThumbnailView.PreviewPositionHelper.updateThumbnailMatrix(android.graphics.Rect, com.android.systemui.shared.recents.model.ThumbnailData, int, int, com.android.launcher3.DeviceProfile, int, android.content.Context):void");
        }

        private Rect getBoundedInsets(Rect activityInsets, Rect insets) {
            if (com.lge.launcher3.util.Utilities.sIsNotchDevice) {
                return new Rect(Math.max(insets.left, activityInsets.left), Math.max(insets.top, activityInsets.top), Math.max(insets.right, activityInsets.right), Math.max(insets.bottom, activityInsets.bottom));
            }
            return new Rect(Math.min(insets.left, activityInsets.left), Math.min(insets.top, activityInsets.top), Math.min(insets.right, activityInsets.right), Math.min(insets.bottom, activityInsets.bottom));
        }

        private void setThumbnailRotation(int deltaRotate, Rect thumbnailInsets, float scale, Rect thumbnailPosition) {
            int i;
            int iHeight;
            int i2;
            int iHeight2;
            int i3;
            this.mMatrix.setRotate(deltaRotate * 90);
            int i4 = 0;
            if (deltaRotate != 1) {
                if (deltaRotate == 2) {
                    i4 = -thumbnailInsets.top;
                    int i5 = -thumbnailInsets.left;
                    int iWidth = thumbnailPosition.width();
                    iHeight2 = thumbnailPosition.height();
                    i3 = iWidth;
                    i = i5;
                } else if (deltaRotate != 3) {
                    i2 = 0;
                    i = 0;
                    iHeight = 0;
                } else {
                    int i6 = thumbnailInsets.top;
                    i = thumbnailInsets.right;
                    iHeight2 = thumbnailPosition.width();
                    i3 = 0;
                    i4 = i6;
                }
                i2 = iHeight2;
                iHeight = i3;
            } else {
                int i7 = thumbnailInsets.bottom;
                i = thumbnailInsets.left;
                iHeight = thumbnailPosition.height();
                i4 = i7;
                i2 = 0;
            }
            this.mClippedInsets.offsetTo(i4 * scale, i * scale);
            this.mMatrix.postTranslate(iHeight - this.mClippedInsets.left, i2 - this.mClippedInsets.top);
        }

        public RectF getInsetsToDrawInFullscreen() {
            return this.mClippedInsets;
        }

        /* JADX WARN: Code restructure failed: missing block: B:55:0x0104, code lost:
        
            if (r14 != r20.getMeasuredHeight()) goto L68;
         */
        /* JADX WARN: Code restructure failed: missing block: B:58:0x010b, code lost:
        
            if (r11 != r20.getMeasuredWidth()) goto L64;
         */
        /* JADX WARN: Code restructure failed: missing block: B:63:0x0118, code lost:
        
            if (r11 != r20.getMeasuredWidth()) goto L64;
         */
        /* JADX WARN: Code restructure failed: missing block: B:64:0x011a, code lost:
        
            r11 = true;
            r13 = r12;
         */
        /* JADX WARN: Code restructure failed: missing block: B:65:0x011e, code lost:
        
            r13 = r12;
         */
        /* JADX WARN: Code restructure failed: missing block: B:67:0x0125, code lost:
        
            if (r14 != r20.getMeasuredHeight()) goto L68;
         */
        /* JADX WARN: Code restructure failed: missing block: B:68:0x0127, code lost:
        
            r11 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:69:0x0129, code lost:
        
            r11 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:70:0x012a, code lost:
        
            r12 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:72:0x012d, code lost:
        
            if (r11 == false) goto L74;
         */
        /* JADX WARN: Code restructure failed: missing block: B:73:0x012f, code lost:
        
            r14 = (int) ((r20.getMeasuredWidth() / 2) - ((r6 * r13) / 2.0f));
         */
        /* JADX WARN: Code restructure failed: missing block: B:74:0x013c, code lost:
        
            r14 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:75:0x013d, code lost:
        
            if (r12 == false) goto L77;
         */
        /* JADX WARN: Code restructure failed: missing block: B:76:0x013f, code lost:
        
            r1 = (int) ((r20.getMeasuredHeight() / 2) - ((r9 * r13) / 2.0f));
         */
        /* JADX WARN: Code restructure failed: missing block: B:77:0x014e, code lost:
        
            r1 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:78:0x014f, code lost:
        
            if (r11 != false) goto L82;
         */
        /* JADX WARN: Code restructure failed: missing block: B:79:0x0151, code lost:
        
            if (r12 == false) goto L81;
         */
        /* JADX WARN: Code restructure failed: missing block: B:81:0x0154, code lost:
        
            r19.letterBoxRect.set(0, 0, 0, 0);
            r22 = r11;
            r16 = r12;
            r17 = r13;
         */
        /* JADX WARN: Code restructure failed: missing block: B:82:0x0161, code lost:
        
            r22 = r11;
            r16 = r12;
            r17 = r13;
            r19.letterBoxRect.set(r14, r1, ((int) java.lang.Math.ceil(r6 * r13)) + r14, ((int) java.lang.Math.ceil(r9 * r13)) + r1);
         */
        /* JADX WARN: Code restructure failed: missing block: B:83:0x017e, code lost:
        
            r11 = r22;
            r13 = r17;
            r12 = false;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void updateThumbnailMatrixForLGE(com.android.quickstep.views.TaskThumbnailView r20, android.graphics.Rect r21, com.android.systemui.shared.recents.model.ThumbnailData r22, int r23, int r24, com.android.launcher3.DeviceProfile r25, int r26) {
            /*
                r19 = this;
                r0 = r19
                r1 = r22
                r2 = r23
                r3 = r25
                r4 = -1082130432(0xffffffffbf800000, float:-1.0)
                r0.mClipBottom = r4
                float r4 = r1.scale
                android.graphics.Rect r5 = r25.getInsets()
                android.graphics.Rect r6 = r1.insets
                android.graphics.Rect r5 = r0.getBoundedInsets(r5, r6)
                int r6 = r21.width()
                float r6 = (float) r6
                int r7 = r5.left
                int r8 = r5.right
                int r7 = r7 + r8
                float r7 = (float) r7
                float r7 = r7 * r4
                float r6 = r6 - r7
                com.android.launcher3.BaseActivity r7 = com.android.quickstep.views.TaskThumbnailView.m544$$Nest$fgetmActivity(r20)
                com.android.launcher3.DeviceProfile r7 = r7.getDeviceProfile()
                boolean r8 = r7.mIsMultiDisplay
                if (r8 == 0) goto L3e
                android.graphics.Rect r8 = r7.getInsets()
                int r8 = r8.top
                android.graphics.Rect r9 = r7.getInsets()
                int r9 = r9.bottom
                goto L42
            L3e:
                int r8 = r5.top
                int r9 = r5.bottom
            L42:
                int r8 = r8 + r9
                float r8 = (float) r8
                android.graphics.Bitmap r9 = r1.thumbnail
                int r9 = r9.getHeight()
                float r9 = (float) r9
                float r8 = r8 * r4
                float r9 = r9 - r8
                int r8 = r1.rotation
                r10 = r26
                int r8 = r0.getRotationDelta(r10, r8)
                boolean r10 = r3.isMultiWindowMode
                r11 = 1
                if (r10 != 0) goto L60
                int r10 = r1.windowingMode
                if (r10 != r11) goto L60
                r10 = r11
                goto L61
            L60:
                r10 = 0
            L61:
                boolean r13 = r0.isOrientationChange(r8)
                if (r13 == 0) goto L6b
                if (r10 == 0) goto L6b
                r10 = r11
                goto L6c
            L6b:
                r10 = 0
            L6c:
                if (r2 != 0) goto L75
                r11 = 0
                r12 = 0
                r13 = 0
                r16 = 0
                goto L194
            L75:
                android.graphics.Bitmap r14 = r1.thumbnail
                int r14 = r14.getWidth()
                android.graphics.Bitmap r15 = r1.thumbnail
                int r15 = r15.getHeight()
                r16 = 2
                if (r14 <= 0) goto L93
                if (r15 <= 0) goto L93
                int r13 = r7.mDisplayId
                r12 = 4
                if (r13 == r12) goto L93
                if (r15 < r14) goto L90
                r12 = r11
                goto L95
            L90:
                r12 = r16
                goto L95
            L93:
                int r12 = r1.orientation
            L95:
                if (r8 == r12) goto Laf
                com.android.launcher3.BaseActivity r12 = com.android.quickstep.views.TaskThumbnailView.m544$$Nest$fgetmActivity(r20)
                boolean r12 = r12.isInMultiWindowMode()
                if (r12 == 0) goto La9
                com.lge.launcher3.util.LGHomeFeature$Config r12 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LETTERBOX_FOR_THUMBNAIL
                boolean r12 = r12.getValue()
                if (r12 == 0) goto Laf
            La9:
                int r12 = r1.windowingMode
                if (r12 != r11) goto Laf
                r12 = r11
                goto Lb0
            Laf:
                r12 = 0
            Lb0:
                com.lge.launcher3.util.LGHomeFeature$Config r13 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LETTERBOX_FOR_THUMBNAIL
                boolean r13 = r13.getValue()
                if (r13 == 0) goto Ld0
                boolean r13 = r7.mIsMultiDisplay
                if (r13 != 0) goto Ld0
                boolean r13 = r7.isMultiWindowMode
                if (r13 != 0) goto Ld0
                int r13 = (int) r6
                int r14 = (int) r9
                boolean r13 = com.android.quickstep.util.LayoutUtils.isWideImage(r13, r14)
                if (r13 == 0) goto Lcc
                r13 = 0
                r0.needLetterbox = r13
                goto Ld3
            Lcc:
                r13 = 0
                r0.needLetterbox = r11
                goto Ld3
            Ld0:
                r13 = 0
                r0.needLetterbox = r13
            Ld3:
                boolean r13 = r0.needLetterbox
                if (r13 == 0) goto L184
                int r12 = r20.getMeasuredHeight()
                float r12 = (float) r12
                float r12 = r12 / r9
                int r13 = r20.getMeasuredWidth()
                float r13 = (float) r13
                float r13 = r13 / r6
                float r14 = r9 * r13
                double r14 = (double) r14
                double r14 = java.lang.Math.ceil(r14)
                int r14 = (int) r14
                float r15 = r6 * r12
                r18 = r12
                double r11 = (double) r15
                double r11 = java.lang.Math.ceil(r11)
                int r11 = (int) r11
                int r1 = r1.orientation
                r12 = 1
                if (r1 != r12) goto L10e
                int r1 = r20.getMeasuredWidth()
                if (r11 <= r1) goto L107
                int r1 = r20.getMeasuredHeight()
                if (r14 == r1) goto L129
                goto L127
            L107:
                int r1 = r20.getMeasuredWidth()
                if (r11 == r1) goto L11e
                goto L11a
            L10e:
                int r1 = r20.getMeasuredHeight()
                if (r14 <= r1) goto L121
                int r1 = r20.getMeasuredWidth()
                if (r11 == r1) goto L11e
            L11a:
                r11 = r12
                r13 = r18
                goto L12a
            L11e:
                r13 = r18
                goto L129
            L121:
                int r1 = r20.getMeasuredHeight()
                if (r14 == r1) goto L129
            L127:
                r11 = 0
                goto L12b
            L129:
                r11 = 0
            L12a:
                r12 = 0
            L12b:
                r1 = 1073741824(0x40000000, float:2.0)
                if (r11 == 0) goto L13c
                int r14 = r20.getMeasuredWidth()
                int r14 = r14 / 2
                float r14 = (float) r14
                float r15 = r6 * r13
                float r15 = r15 / r1
                float r14 = r14 - r15
                int r14 = (int) r14
                goto L13d
            L13c:
                r14 = 0
            L13d:
                if (r12 == 0) goto L14e
                int r15 = r20.getMeasuredHeight()
                int r15 = r15 / 2
                float r15 = (float) r15
                float r16 = r9 * r13
                float r16 = r16 / r1
                float r15 = r15 - r16
                int r1 = (int) r15
                goto L14f
            L14e:
                r1 = 0
            L14f:
                if (r11 != 0) goto L161
                if (r12 == 0) goto L154
                goto L161
            L154:
                android.graphics.Rect r1 = r0.letterBoxRect
                r14 = 0
                r1.set(r14, r14, r14, r14)
                r22 = r11
                r16 = r12
                r17 = r13
                goto L17e
            L161:
                android.graphics.Rect r15 = r0.letterBoxRect
                r22 = r11
                float r11 = r6 * r13
                r16 = r12
                double r11 = (double) r11
                double r11 = java.lang.Math.ceil(r11)
                int r11 = (int) r11
                int r11 = r11 + r14
                float r12 = r9 * r13
                r17 = r13
                double r12 = (double) r12
                double r12 = java.lang.Math.ceil(r12)
                int r12 = (int) r12
                int r12 = r12 + r1
                r15.set(r14, r1, r11, r12)
            L17e:
                r11 = r22
                r13 = r17
                r12 = 0
                goto L194
            L184:
                android.graphics.Rect r1 = r0.letterBoxRect
                r11 = 0
                r1.set(r11, r11, r11, r11)
                if (r10 == 0) goto L18f
                float r1 = (float) r2
                float r1 = r1 / r9
                goto L191
            L18f:
                float r1 = (float) r2
                float r1 = r1 / r6
            L191:
                r13 = r1
                r16 = r11
            L194:
                android.graphics.Rect r1 = r25.getInsets()
                if (r12 != 0) goto L1cb
                boolean r8 = r3.isMultiWindowMode
                if (r8 == 0) goto L1ac
                android.graphics.RectF r8 = r0.mClippedInsets
                int r12 = r1.left
                float r12 = (float) r12
                float r12 = r12 * r4
                int r14 = r1.top
                float r14 = (float) r14
                float r14 = r14 * r4
                r8.offsetTo(r12, r14)
                goto L1b9
            L1ac:
                android.graphics.RectF r8 = r0.mClippedInsets
                int r12 = r5.left
                float r12 = (float) r12
                float r12 = r12 * r4
                int r14 = r5.top
                float r14 = (float) r14
                float r14 = r14 * r4
                r8.offsetTo(r12, r14)
            L1b9:
                android.graphics.Matrix r8 = r0.mMatrix
                int r12 = r5.left
                int r12 = -r12
                float r12 = (float) r12
                float r12 = r12 * r4
                int r5 = r5.top
                int r5 = -r5
                float r5 = (float) r5
                float r5 = r5 * r4
                r8.setTranslate(r12, r5)
                r12 = r21
                goto L1d0
            L1cb:
                r12 = r21
                r0.setThumbnailRotation(r8, r5, r4, r12)
            L1d0:
                if (r10 == 0) goto L1dd
                int r5 = r21.height()
                float r5 = (float) r5
                float r5 = r5 * r13
                int r8 = r21.width()
                goto L1e7
            L1dd:
                int r5 = r21.width()
                float r5 = (float) r5
                float r5 = r5 * r13
                int r8 = r21.height()
            L1e7:
                float r8 = (float) r8
                float r8 = r8 * r13
                if (r11 != 0) goto L233
                if (r16 == 0) goto L1ee
                goto L233
            L1ee:
                android.graphics.RectF r7 = r0.mClippedInsets
                float r12 = r7.left
                float r12 = r12 * r13
                r7.left = r12
                android.graphics.RectF r7 = r0.mClippedInsets
                float r12 = r7.top
                float r12 = r12 * r13
                r7.top = r12
                boolean r3 = r3.isMultiWindowMode
                if (r3 == 0) goto L214
                android.graphics.RectF r2 = r0.mClippedInsets
                int r3 = r1.right
                float r3 = (float) r3
                float r3 = r3 * r4
                float r3 = r3 * r13
                r2.right = r3
                android.graphics.RectF r2 = r0.mClippedInsets
                int r1 = r1.bottom
                float r1 = (float) r1
                float r1 = r1 * r4
                float r1 = r1 * r13
                r2.bottom = r1
                goto L2c0
            L214:
                android.graphics.RectF r1 = r0.mClippedInsets
                float r3 = r1.left
                float r5 = r5 - r3
                float r2 = (float) r2
                float r5 = r5 - r2
                r2 = 0
                float r3 = java.lang.Math.max(r2, r5)
                r1.right = r3
                android.graphics.RectF r1 = r0.mClippedInsets
                float r3 = r1.top
                float r8 = r8 - r3
                r3 = r24
                float r3 = (float) r3
                float r8 = r8 - r3
                float r3 = java.lang.Math.max(r2, r8)
                r1.bottom = r3
                goto L2c0
            L233:
                android.graphics.RectF r1 = r0.mClippedInsets
                android.graphics.Rect r2 = r7.getInsets()
                r1.set(r2)
                com.lge.launcher3.util.LGHomeFeature$Config r1 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
                boolean r1 = r1.getValue()
                if (r1 == 0) goto L2a4
                android.content.Context r1 = r20.getContext()
                com.android.quickstep.SysUINavigationMode$Mode r1 = com.android.quickstep.SysUINavigationMode.getMode(r1)
                com.android.quickstep.SysUINavigationMode$Mode r2 = com.android.quickstep.SysUINavigationMode.Mode.NO_BUTTON
                if (r1 != r2) goto L26b
                android.graphics.RectF r1 = r0.mClippedInsets
                android.content.Context r2 = r20.getContext()
                int r2 = com.lge.launcher3.util.WindowUtils.getStatusBarHeight(r2)
                float r2 = (float) r2
                r1.top = r2
                android.graphics.RectF r1 = r0.mClippedInsets
                android.content.Context r2 = r20.getContext()
                int r2 = com.lge.launcher3.util.WindowUtils.getNavigationBarHeight(r2)
                float r2 = (float) r2
                r1.bottom = r2
                goto L2a4
            L26b:
                boolean r1 = r7.isLandscape
                if (r1 == 0) goto L28a
                android.graphics.RectF r1 = r0.mClippedInsets
                android.content.Context r2 = r20.getContext()
                int r2 = com.lge.launcher3.util.WindowUtils.getStatusBarHeight(r2)
                float r2 = (float) r2
                r1.left = r2
                android.graphics.RectF r1 = r0.mClippedInsets
                android.content.Context r2 = r20.getContext()
                int r2 = com.lge.launcher3.util.WindowUtils.getNavigationBarHeight(r2)
                float r2 = (float) r2
                r1.right = r2
                goto L2a4
            L28a:
                android.graphics.RectF r1 = r0.mClippedInsets
                android.content.Context r2 = r20.getContext()
                int r2 = com.lge.launcher3.util.WindowUtils.getStatusBarHeight(r2)
                float r2 = (float) r2
                r1.top = r2
                android.graphics.RectF r1 = r0.mClippedInsets
                android.content.Context r2 = r20.getContext()
                int r2 = com.lge.launcher3.util.WindowUtils.getNavigationBarHeight(r2)
                float r2 = (float) r2
                r1.bottom = r2
            L2a4:
                android.graphics.RectF r1 = r0.mClippedInsets
                float r2 = r1.left
                float r2 = r2 * r13
                r1.left = r2
                android.graphics.RectF r1 = r0.mClippedInsets
                float r2 = r1.top
                float r2 = r2 * r13
                r1.top = r2
                android.graphics.RectF r1 = r0.mClippedInsets
                float r2 = r1.right
                float r2 = r2 * r13
                r1.right = r2
                android.graphics.RectF r1 = r0.mClippedInsets
                float r2 = r1.bottom
                float r2 = r2 * r13
                r1.bottom = r2
            L2c0:
                android.graphics.Matrix r1 = r0.mMatrix
                r1.postScale(r13, r13)
                com.lge.launcher3.util.LGHomeFeature$Config r1 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LETTERBOX_FOR_THUMBNAIL
                boolean r1 = r1.getValue()
                if (r1 == 0) goto L2e0
                if (r11 != 0) goto L2d1
                if (r16 == 0) goto L2e0
            L2d1:
                android.graphics.Matrix r1 = r0.mMatrix
                android.graphics.Rect r2 = r0.letterBoxRect
                int r2 = r2.left
                float r2 = (float) r2
                android.graphics.Rect r3 = r0.letterBoxRect
                int r3 = r3.top
                float r3 = (float) r3
                r1.postTranslate(r2, r3)
            L2e0:
                if (r10 == 0) goto L2e3
                goto L2e4
            L2e3:
                r6 = r9
            L2e4:
                float r6 = r6 * r13
                r1 = 0
                float r1 = java.lang.Math.max(r1, r6)
                double r2 = (double) r1
                double r2 = java.lang.Math.ceil(r2)
                int r4 = r20.getMeasuredHeight()
                double r4 = (double) r4
                int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r2 >= 0) goto L2fa
                r0.mClipBottom = r1
            L2fa:
                r0.mIsOrientationChanged = r10
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.views.TaskThumbnailView.PreviewPositionHelper.updateThumbnailMatrixForLGE(com.android.quickstep.views.TaskThumbnailView, android.graphics.Rect, com.android.systemui.shared.recents.model.ThumbnailData, int, int, com.android.launcher3.DeviceProfile, int):void");
        }
    }

    public void setCornerRadius(float radius) {
        if (this.mCornerRadius != radius) {
            this.mCornerRadius = radius;
            invalidate();
        }
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public static LightingColorFilter getDimmingColorFilter(int intensity, boolean shouldLighten) {
        int iBoundToRange = Utilities.boundToRange(intensity, 0, 255);
        if (iBoundToRange == 255) {
            return null;
        }
        if (shouldLighten) {
            LightingColorFilter[] lightingColorFilterArr = sHighlightFilterCache;
            if (lightingColorFilterArr[iBoundToRange] == null) {
                int i = 255 - iBoundToRange;
                lightingColorFilterArr[iBoundToRange] = new LightingColorFilter(Color.argb(255, iBoundToRange, iBoundToRange, iBoundToRange), Color.argb(255, i, i, i));
            }
            return lightingColorFilterArr[iBoundToRange];
        }
        LightingColorFilter[] lightingColorFilterArr2 = sDimFilterCache;
        if (lightingColorFilterArr2[iBoundToRange] == null) {
            lightingColorFilterArr2[iBoundToRange] = new LightingColorFilter(Color.argb(255, iBoundToRange, iBoundToRange, iBoundToRange), 0);
        }
        return lightingColorFilterArr2[iBoundToRange];
    }

    void setHeaderView(TaskHeaderView taskHeader) {
        this.mTaskHeader = taskHeader;
        invalidate();
    }
}
