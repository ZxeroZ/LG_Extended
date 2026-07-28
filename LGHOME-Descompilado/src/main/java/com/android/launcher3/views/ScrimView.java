package com.android.launcher3.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.RectEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.IntProperty;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.uioverrides.WallpaperColorInfo;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.Themes;
import com.lge.launcher3.R;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ScrimView<T extends Launcher> extends View implements Insettable, WallpaperColorInfo.OnChangeListener, AccessibilityManager.AccessibilityStateChangeListener, StateManager.StateListener<LauncherState> {
    private static final int ALPHA_CHANNEL_COUNT = 1;
    public static final IntProperty<ScrimView> DRAG_HANDLE_ALPHA = new IntProperty<ScrimView>("dragHandleAlpha") { // from class: com.android.launcher3.views.ScrimView.1
        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Integer get(ScrimView scrimView) {
            if (scrimView != null) {
                return Integer.valueOf(scrimView.mDragHandleAlpha);
            }
            return 0;
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;I)V */
        @Override // android.util.IntProperty
        public void setValue(ScrimView scrimView, int value) {
            if (scrimView != null) {
                scrimView.setDragHandleAlpha(value);
            }
        }
    };
    private static final long DRAG_HANDLE_BOUNCE_DELAY_MS = 200;
    private static final long DRAG_HANDLE_BOUNCE_DURATION_MS = 300;
    private static final int DRAG_HANDLE_BOUNCE_REPEAT_COUNT = 2;
    private static final int SETTINGS = 2131821065;
    private static final int WALLPAPERS = 2131821283;
    private static final int WIDGETS = 2131821288;
    private final AccessibilityManager mAM;
    private final ScrimView<T>.AccessibilityHelper mAccessibilityHelper;
    protected int mCurrentFlatColor;
    protected Drawable mDragHandle;
    private int mDragHandleAlpha;
    private ObjectAnimator mDragHandleAnim;
    private final Rect mDragHandleBounds;
    protected float mDragHandleOffset;
    private final int mDragHandlePaddingInVerticalBarLayout;
    protected final Point mDragHandleSize;
    private final int mDragHandleTouchSize;
    protected int mEndFlatColor;
    protected int mEndFlatColorAlpha;
    protected final int mEndScrim;
    private final RectF mHitRect;
    protected final boolean mIsScrimDark;
    protected final T mLauncher;
    protected float mMaxScrimAlpha;
    private final MultiValueAlpha mMultiValueAlpha;
    protected float mProgress;
    protected int mScrimColor;
    private final int[] mTempPos;
    private final Rect mTempRect;
    private final WallpaperColorInfo mWallpaperColorInfo;

    public float getVisualTop() {
        return Float.MAX_VALUE;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateListener
    public void onStateTransitionComplete(LauncherState finalState) {
    }

    /* JADX DEBUG: Method merged with bridge method: onStateTransitionStart(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateListener
    public void onStateTransitionStart(LauncherState toState) {
    }

    public void reInitUi() {
    }

    public ScrimView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTempRect = new Rect();
        this.mTempPos = new int[2];
        this.mProgress = 1.0f;
        this.mHitRect = new RectF();
        this.mDragHandleAlpha = 255;
        this.mLauncher = (T) Launcher.cast(Launcher.getLauncher(context));
        this.mWallpaperColorInfo = WallpaperColorInfo.INSTANCE.lambda$get$0$MainThreadInitializedObject(context);
        int attrColor = Themes.getAttrColor(context, R.attr.allAppsScrimColor);
        this.mEndScrim = attrColor;
        this.mIsScrimDark = ColorUtils.calculateLuminance(attrColor) < 0.5d;
        this.mMaxScrimAlpha = 0.7f;
        Resources resources = context.getResources();
        Point point = new Point(resources.getDimensionPixelSize(R.dimen.vertical_drag_handle_width), resources.getDimensionPixelSize(R.dimen.vertical_drag_handle_height));
        this.mDragHandleSize = point;
        this.mDragHandleBounds = new Rect(0, 0, point.x, point.y);
        this.mDragHandleTouchSize = resources.getDimensionPixelSize(R.dimen.vertical_drag_handle_touch_size);
        this.mDragHandlePaddingInVerticalBarLayout = context.getResources().getDimensionPixelSize(R.dimen.vertical_drag_handle_padding_in_vertical_bar_layout);
        ScrimView<T>.AccessibilityHelper accessibilityHelperCreateAccessibilityHelper = createAccessibilityHelper();
        this.mAccessibilityHelper = accessibilityHelperCreateAccessibilityHelper;
        ViewCompat.setAccessibilityDelegate(this, accessibilityHelperCreateAccessibilityHelper);
        this.mAM = (AccessibilityManager) context.getSystemService("accessibility");
        setFocusable(false);
        this.mMultiValueAlpha = new MultiValueAlpha(this, 1);
    }

    public MultiValueAlpha.AlphaProperty getAlphaProperty(int index) {
        return this.mMultiValueAlpha.getProperty(index);
    }

    protected ScrimView<T>.AccessibilityHelper createAccessibilityHelper() {
        return new AccessibilityHelper();
    }

    @Override // com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        updateDragHandleBounds();
        updateDragHandleVisibility(null);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateDragHandleBounds();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mWallpaperColorInfo.addOnChangeListener(this);
        onExtractedColorsChanged(this.mWallpaperColorInfo);
        this.mAM.addAccessibilityStateChangeListener(this);
        onAccessibilityStateChanged(this.mAM.isEnabled());
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mWallpaperColorInfo.removeOnChangeListener(this);
        this.mAM.removeAccessibilityStateChangeListener(this);
    }

    @Override // com.android.launcher3.uioverrides.WallpaperColorInfo.OnChangeListener
    public void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo) {
        int mainColor = wallpaperColorInfo.getMainColor();
        this.mScrimColor = mainColor;
        int iCompositeColors = ColorUtils.compositeColors(this.mEndScrim, ColorUtils.setAlphaComponent(mainColor, Math.round(this.mMaxScrimAlpha * 255.0f)));
        this.mEndFlatColor = iCompositeColors;
        this.mEndFlatColorAlpha = Color.alpha(iCompositeColors);
        updateColors();
        invalidate();
    }

    public void setProgress(float progress) {
        if (this.mProgress != progress) {
            this.mProgress = progress;
            updateColors();
            updateDragHandleAlpha();
            invalidate();
        }
    }

    protected void updateColors() {
        float f = this.mProgress;
        this.mCurrentFlatColor = f >= 1.0f ? 0 : ColorUtils.setAlphaComponent(this.mEndFlatColor, Math.round((1.0f - f) * this.mEndFlatColorAlpha));
    }

    protected void updateSysUiColors() {
        if (this.mProgress <= 0.1f) {
            this.mLauncher.getSystemUiController().updateUiState(5, true ^ this.mIsScrimDark);
        } else {
            this.mLauncher.getSystemUiController().updateUiState(5, 0);
        }
    }

    protected void updateDragHandleAlpha() {
        Drawable drawable = this.mDragHandle;
        if (drawable != null) {
            drawable.setAlpha(this.mDragHandleAlpha);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDragHandleAlpha(int alpha) {
        if (alpha != this.mDragHandleAlpha) {
            this.mDragHandleAlpha = alpha;
            Drawable drawable = this.mDragHandle;
            if (drawable != null) {
                drawable.setAlpha(alpha);
                invalidate();
            }
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i = this.mCurrentFlatColor;
        if (i != 0) {
            canvas.drawColor(i);
        }
        drawDragHandle(canvas);
    }

    protected void drawDragHandle(Canvas canvas) {
        if (this.mDragHandle != null) {
            canvas.translate(0.0f, -this.mDragHandleOffset);
            this.mDragHandle.draw(canvas);
            canvas.translate(0.0f, this.mDragHandleOffset);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        boolean zOnTouchEvent = super.onTouchEvent(event);
        if (!zOnTouchEvent && this.mDragHandle != null && event.getAction() == 0 && this.mDragHandle.getAlpha() == 255 && this.mHitRect.contains(event.getX(), event.getY())) {
            final Drawable drawable = this.mDragHandle;
            this.mDragHandle = null;
            drawable.setBounds(this.mDragHandleBounds);
            Rect rect = new Rect(this.mDragHandleBounds);
            rect.offset(0, (-this.mDragHandleBounds.height()) / 2);
            final Rect rect2 = new Rect(this.mDragHandleBounds);
            rect2.top = rect.top;
            Keyframe keyframeOfObject = Keyframe.ofObject(0.6f, rect);
            keyframeOfObject.setInterpolator(Interpolators.DEACCEL);
            Keyframe keyframeOfObject2 = Keyframe.ofObject(1.0f, this.mDragHandleBounds);
            keyframeOfObject2.setInterpolator(Interpolators.ACCEL);
            PropertyValuesHolder propertyValuesHolderOfKeyframe = PropertyValuesHolder.ofKeyframe("bounds", Keyframe.ofObject(0.0f, this.mDragHandleBounds), keyframeOfObject, keyframeOfObject2);
            propertyValuesHolderOfKeyframe.setEvaluator(new RectEvaluator());
            ObjectAnimator objectAnimatorOfPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(drawable, propertyValuesHolderOfKeyframe);
            objectAnimatorOfPropertyValuesHolder.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.views.ScrimView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ScrimView.this.getOverlay().remove(drawable);
                    ScrimView.this.updateDragHandleVisibility(drawable);
                }
            });
            objectAnimatorOfPropertyValuesHolder.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.views.-$$Lambda$ScrimView$FOqGLZSgtbvaTzIrB0rpZlNTRRE
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$onTouchEvent$0$ScrimView(rect2, valueAnimator);
                }
            });
            getOverlay().add(drawable);
            objectAnimatorOfPropertyValuesHolder.start();
        }
        return zOnTouchEvent;
    }

    public /* synthetic */ void lambda$onTouchEvent$0$ScrimView(Rect rect, ValueAnimator valueAnimator) {
        invalidate(rect);
    }

    public boolean startDragHandleEducationAnim() {
        stopDragHandleEducationAnim();
        Drawable drawable = this.mDragHandle;
        if (drawable == null || drawable.getAlpha() != 255) {
            return false;
        }
        final Drawable drawable2 = this.mDragHandle;
        this.mDragHandle = null;
        Rect rect = new Rect(this.mDragHandleBounds);
        rect.offset(0, -((int) this.mDragHandleOffset));
        drawable2.setBounds(rect);
        Rect rect2 = new Rect(rect);
        rect2.offset(0, -rect.height());
        final Rect rect3 = new Rect(rect);
        rect3.top = rect2.top;
        Keyframe keyframeOfObject = Keyframe.ofObject(0.6f, rect2);
        keyframeOfObject.setInterpolator(Interpolators.DEACCEL);
        Keyframe keyframeOfObject2 = Keyframe.ofObject(1.0f, rect);
        keyframeOfObject2.setInterpolator(Interpolators.ACCEL_DEACCEL);
        PropertyValuesHolder propertyValuesHolderOfKeyframe = PropertyValuesHolder.ofKeyframe("bounds", Keyframe.ofObject(0.0f, rect), keyframeOfObject, keyframeOfObject2);
        propertyValuesHolderOfKeyframe.setEvaluator(new RectEvaluator());
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(drawable2, propertyValuesHolderOfKeyframe);
        this.mDragHandleAnim = objectAnimatorOfPropertyValuesHolder;
        objectAnimatorOfPropertyValuesHolder.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.views.-$$Lambda$ScrimView$MN5m5YBbCbxzDM8Z1ZaBntaXBuo
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$startDragHandleEducationAnim$1$ScrimView(rect3, valueAnimator);
            }
        });
        this.mDragHandleAnim.setDuration(500L);
        this.mDragHandleAnim.setInterpolator(Interpolators.clampToProgress(Interpolators.LINEAR, 0.0f, 1.0f - (200.0f / 500)));
        this.mDragHandleAnim.setRepeatCount(2);
        getOverlay().add(drawable2);
        this.mDragHandleAnim.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.views.ScrimView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ScrimView.this.mDragHandleAnim = null;
                ScrimView.this.getOverlay().remove(drawable2);
                ScrimView.this.updateDragHandleVisibility(drawable2);
            }
        });
        this.mDragHandleAnim.start();
        return true;
    }

    public /* synthetic */ void lambda$startDragHandleEducationAnim$1$ScrimView(Rect rect, ValueAnimator valueAnimator) {
        invalidate(rect);
    }

    private void stopDragHandleEducationAnim() {
        ObjectAnimator objectAnimator = this.mDragHandleAnim;
        if (objectAnimator != null) {
            objectAnimator.end();
        }
    }

    protected void updateDragHandleBounds() {
        int iRound;
        int i;
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = (getMeasuredHeight() - this.mDragHandleSize.y) - deviceProfile.getInsets().bottom;
        if (deviceProfile.isVerticalBarLayout()) {
            i = deviceProfile.workspacePadding.bottom + this.mDragHandlePaddingInVerticalBarLayout;
            if (deviceProfile.isSeascape()) {
                iRound = ((measuredWidth - deviceProfile.getInsets().right) - this.mDragHandleSize.x) - this.mDragHandlePaddingInVerticalBarLayout;
            } else {
                iRound = this.mDragHandlePaddingInVerticalBarLayout + deviceProfile.getInsets().left;
            }
        } else {
            iRound = Math.round((measuredWidth - this.mDragHandleSize.x) / 2.0f);
            i = deviceProfile.hotseatBarSizePx;
        }
        this.mDragHandleBounds.offsetTo(iRound, measuredHeight - i);
        this.mHitRect.set(this.mDragHandleBounds);
        this.mHitRect.inset((this.mDragHandleSize.x - this.mDragHandleTouchSize) / 2.0f, (this.mDragHandleSize.y - this.mDragHandleTouchSize) / 2.0f);
        Drawable drawable = this.mDragHandle;
        if (drawable != null) {
            drawable.setBounds(this.mDragHandleBounds);
        }
    }

    @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
    public void onAccessibilityStateChanged(boolean enabled) {
        StateManager<LauncherState> stateManager = this.mLauncher.getStateManager();
        stateManager.removeStateListener(this);
        if (enabled) {
            stateManager.addStateListener(this);
        } else {
            setImportantForAccessibility(4);
        }
        updateDragHandleVisibility(null);
    }

    public void updateDragHandleVisibility() {
        updateDragHandleVisibility(null);
    }

    public void updateDragHandleVisibility(Drawable recycle) {
        boolean z = this.mLauncher.getDeviceProfile().isVerticalBarLayout() || this.mAM.isEnabled();
        if (z != (this.mDragHandle != null)) {
            if (z) {
                if (recycle == null) {
                    recycle = this.mLauncher.getDrawable(R.drawable.drag_handle_indicator);
                }
                this.mDragHandle = recycle;
                recycle.setBounds(this.mDragHandleBounds);
                updateDragHandleAlpha();
            } else {
                this.mDragHandle = null;
            }
            invalidate();
        }
    }

    protected boolean shouldDragHandleBeVisible() {
        return this.mLauncher.getDeviceProfile().isVerticalBarLayout() || this.mAM.isEnabled();
    }

    @Override // android.view.View
    public boolean dispatchHoverEvent(MotionEvent event) {
        return this.mAccessibilityHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    @Override // android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.mAccessibilityHelper.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override // android.view.View
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        this.mAccessibilityHelper.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    protected class AccessibilityHelper extends ExploreByTouchHelper {
        private static final int DRAG_HANDLE_ID = 1;

        public AccessibilityHelper() {
            super(ScrimView.this);
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected int getVirtualViewAt(float x, float y) {
            return ScrimView.this.mDragHandleBounds.contains((int) x, (int) y) ? 1 : Integer.MIN_VALUE;
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
            virtualViewIds.add(1);
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateNodeForVirtualView(int virtualViewId, AccessibilityNodeInfoCompat node) {
            node.setContentDescription(ScrimView.this.getContext().getString(R.string.all_apps_button_label));
            node.setBoundsInParent(ScrimView.this.mDragHandleBounds);
            ScrimView scrimView = ScrimView.this;
            scrimView.getLocationOnScreen(scrimView.mTempPos);
            ScrimView.this.mTempRect.set(ScrimView.this.mDragHandleBounds);
            ScrimView.this.mTempRect.offset(ScrimView.this.mTempPos[0], ScrimView.this.mTempPos[1]);
            node.setBoundsInScreen(ScrimView.this.mTempRect);
            node.addAction(16);
            node.setClickable(true);
            node.setFocusable(true);
            if (ScrimView.this.mLauncher.isInState(LauncherState.NORMAL)) {
                Context context = ScrimView.this.getContext();
                if (Utilities.isWallpaperAllowed(context)) {
                    node.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.string.wallpaper_button_text, context.getText(R.string.wallpaper_button_text)));
                }
                node.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.string.widget_button_text, context.getText(R.string.widget_button_text)));
                node.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.string.settings_button_text, context.getText(R.string.settings_button_text)));
            }
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action, Bundle arguments) {
            if (action != 16) {
                return false;
            }
            ScrimView.this.mLauncher.getUserEventDispatcher().logActionOnControl(0, 1, ((LauncherState) ScrimView.this.mLauncher.getStateManager().getState()).containerType);
            ScrimView.this.mLauncher.getStateManager().goToState(LauncherState.ALL_APPS);
            return true;
        }
    }

    public int getDragHandleSize() {
        return this.mDragHandleSize.y;
    }
}
