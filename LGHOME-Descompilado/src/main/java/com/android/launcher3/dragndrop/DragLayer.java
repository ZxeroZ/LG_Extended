package com.android.launcher3.dragndrop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.SearchDropTargetBar;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Workspace;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.OverviewScrim;
import com.android.launcher3.graphics.WorkspaceAndHotseatScrim;
import com.android.launcher3.util.PendingRequestArgs;
import com.android.launcher3.views.BaseDragLayer;
import com.lge.launcher3.R;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class DragLayer extends BaseDragLayer<Launcher> {
    private static final int ALPHA_CHANNEL_COUNT = 4;
    public static final int ALPHA_INDEX_LAUNCHER_LOAD = 1;
    public static final int ALPHA_INDEX_OVERLAY = 0;
    public static final int ALPHA_INDEX_SWIPE_UP = 3;
    public static final int ALPHA_INDEX_TRANSITIONS = 2;
    public static final int ANIMATION_END_DISAPPEAR = 0;
    public static final int ANIMATION_END_REMAIN_VISIBLE = 2;
    private static final int SCRIM_COLOR = Utilities.sBlack & ViewCompat.MEASURED_SIZE_MASK;
    private static final String TAG = "DragLayer";
    View mAnchorView;
    int mAnchorViewInitialScrollX;
    private float mBackgroundAlpha;
    private boolean mBlockTouches;
    private int mChildCountOnLastUpdate;
    private final TimeInterpolator mCubicEaseOutInterpolator;
    DragController mDragController;
    private ValueAnimator mDropAnim;
    DragView mDropView;
    private boolean mHoverPointClosesFolder;
    private boolean mInScrollArea;
    private final boolean mIsRtl;
    public Launcher mLauncher;
    private Drawable mLeftHoverDrawable;
    private Drawable mLeftHoverDrawableActive;
    private final OverviewScrim mOverviewScrim;
    private Drawable mRightHoverDrawable;
    private Drawable mRightHoverDrawableActive;
    private final Rect mScrollChildPosition;
    private boolean mShowPageHints;
    private final int[] mTmpXY;
    private int mTopViewIndex;
    private WorkspaceAndHotseatScrim mWorkspaceScrim;

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent ev) {
        return false;
    }

    public void showPageHints() {
    }

    public DragLayer(Context context, AttributeSet attrs) {
        super(context, attrs, 4);
        this.mTmpXY = new int[2];
        this.mDropAnim = null;
        this.mCubicEaseOutInterpolator = new DecelerateInterpolator(1.5f);
        this.mDropView = null;
        this.mAnchorViewInitialScrollX = 0;
        this.mAnchorView = null;
        this.mHoverPointClosesFolder = false;
        this.mChildCountOnLastUpdate = -1;
        this.mBackgroundAlpha = 0.0f;
        this.mScrollChildPosition = new Rect();
        this.mBlockTouches = false;
        setMotionEventSplittingEnabled(false);
        setChildrenDrawingOrderEnabled(true);
        Resources resources = getResources();
        this.mLeftHoverDrawable = resources.getDrawable(R.drawable.page_hover_left);
        this.mRightHoverDrawable = resources.getDrawable(R.drawable.page_hover_right);
        this.mLeftHoverDrawableActive = resources.getDrawable(R.drawable.page_hover_left_active);
        this.mRightHoverDrawableActive = resources.getDrawable(R.drawable.page_hover_right_active);
        this.mIsRtl = com.android.launcher3.Utilities.isRtl(resources);
        this.mWorkspaceScrim = new WorkspaceAndHotseatScrim(this);
        this.mOverviewScrim = new OverviewScrim(this);
    }

    public void setup(Launcher launcher, DragController controller) {
        this.mLauncher = launcher;
        this.mDragController = controller;
        recreateControllers();
    }

    @Override // com.android.launcher3.views.BaseDragLayer
    public void recreateControllers() {
        this.mControllers = ((Launcher) this.mActivity).createTouchControllers();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.mDragController.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    private boolean isEventOverAccessibleDropTargetBar(MotionEvent ev) {
        return isInAccessibleDrag() && isEventOverView(((Launcher) this.mActivity).getSearchBar(), ev);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptHoverEvent(MotionEvent ev) {
        if (this.mActivity != 0 && ((Launcher) this.mActivity).getWorkspace() != null) {
            AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mActivity);
            if ((topOpenView instanceof Folder) && ((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
                Folder folder = (Folder) topOpenView;
                int action = ev.getAction();
                if (AbstractFloatingView.getOpenView(this.mLauncher, 2) == null && AbstractFloatingView.getOpenView(this.mLauncher, 4) == null) {
                    if (action == 7) {
                        boolean z = isEventOverView(topOpenView, ev) || isEventOverAccessibleDropTargetBar(ev);
                        if (!z && !this.mHoverPointClosesFolder) {
                            sendTapOutsideFolderAccessibilityEvent(folder.isEditingName());
                            this.mHoverPointClosesFolder = true;
                            return true;
                        }
                        if (!z) {
                            return true;
                        }
                        this.mHoverPointClosesFolder = false;
                    } else if (action == 9) {
                        if (!(isEventOverView(topOpenView, ev) || isEventOverAccessibleDropTargetBar(ev))) {
                            sendTapOutsideFolderAccessibilityEvent(folder.isEditingName());
                            this.mHoverPointClosesFolder = true;
                            return true;
                        }
                        this.mHoverPointClosesFolder = false;
                    }
                }
            }
        }
        return false;
    }

    private void sendTapOutsideFolderAccessibilityEvent(boolean isEditingName) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        if (accessibilityManager.isEnabled()) {
            int i = isEditingName ? R.string.folder_tap_to_rename : R.string.folder_tap_to_close;
            AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(8);
            onInitializeAccessibilityEvent(accessibilityEventObtain);
            accessibilityEventObtain.getText().add(getContext().getString(i));
            accessibilityManager.sendAccessibilityEvent(accessibilityEventObtain);
        }
    }

    private boolean isInAccessibleDrag() {
        LauncherAccessibilityDelegate accessibilityDelegate = LauncherAppState.getInstance(getContext()).getAccessibilityDelegate();
        return accessibilityDelegate != null && accessibilityDelegate.isInAccessibleDrag();
    }

    @Override // com.android.launcher3.views.BaseDragLayer, android.view.ViewGroup
    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mLauncher);
        if (topOpenView == null) {
            return super.onRequestSendAccessibilityEvent(child, event);
        }
        if (child == topOpenView) {
            return super.onRequestSendAccessibilityEvent(child, event);
        }
        if (isInAccessibleDrag() && (child instanceof SearchDropTargetBar)) {
            return super.onRequestSendAccessibilityEvent(child, event);
        }
        return false;
    }

    @Override // com.android.launcher3.views.BaseDragLayer, android.view.ViewGroup, android.view.View
    public void addChildrenForAccessibility(ArrayList<View> childrenForAccessibility) {
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mLauncher);
        if (topOpenView != null) {
            childrenForAccessibility.add(topOpenView);
            if (isInAccessibleDrag()) {
                childrenForAccessibility.add(this.mLauncher.getSearchBar());
                return;
            }
            return;
        }
        super.addChildrenForAccessibility(childrenForAccessibility);
    }

    public float mapCoordInSelfToDescendent(View descendant, int[] coord) {
        return com.android.launcher3.Utilities.mapCoordInSelfToDescendent(descendant, this, coord);
    }

    @Override // com.android.launcher3.views.BaseDragLayer
    public void getViewRectRelativeToSelf(View v, Rect r) {
        int[] iArr = new int[2];
        getLocationInWindow(iArr);
        int i = iArr[0];
        int i2 = iArr[1];
        v.getLocationInWindow(iArr);
        int i3 = iArr[0] - i;
        int i4 = iArr[1] - i2;
        r.set(i3, i4, v.getMeasuredWidth() + i3, v.getMeasuredHeight() + i4);
    }

    @Override // com.android.launcher3.views.BaseDragLayer, android.view.ViewGroup, android.view.View
    public boolean dispatchUnhandledMove(View focused, int direction) {
        return (AbstractFloatingView.getTopOpenView(this.mLauncher) != null) || this.mDragController.dispatchUnhandledMove(focused, direction);
    }

    @Override // com.android.launcher3.views.BaseDragLayer, android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public void animateViewIntoPosition(DragView dragView, final View child) {
        animateViewIntoPosition(dragView, child, null, null);
    }

    public void animateViewIntoPosition(DragView dragView, final int[] pos, float alpha, float scaleX, float scaleY, int animationEndStyle, Runnable onFinishRunnable, int duration) {
        Rect rect = new Rect();
        getViewRectRelativeToSelf(dragView, rect);
        animateViewIntoPosition(dragView, rect.left, rect.top, pos[0], pos[1], alpha, 1.0f, 1.0f, scaleX, scaleY, onFinishRunnable, animationEndStyle, duration, null);
    }

    public void animateViewIntoPosition(DragView dragView, final View child, final Runnable onFinishAnimationRunnable, View anchorView) {
        animateViewIntoPosition(dragView, child, -1, onFinishAnimationRunnable, anchorView);
    }

    public void animateViewIntoPosition(DragView dragView, final View child, int duration, final Runnable onFinishAnimationRunnable, View anchorView) {
        int iRound;
        int iRound2;
        int iRound3;
        float intrinsicIconScaleFactor;
        if (child != null) {
            ShortcutAndWidgetContainer shortcutAndWidgetContainer = (ShortcutAndWidgetContainer) child.getParent();
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) child.getLayoutParams();
            shortcutAndWidgetContainer.measureChild(child);
            Rect rect = new Rect();
            getViewRectRelativeToSelf(dragView, rect);
            float scaleX = child.getScaleX();
            float f = 1.0f - scaleX;
            int[] iArr = {layoutParams.x + ((int) ((child.getMeasuredWidth() * f) / 2.0f)), layoutParams.y + ((int) ((child.getMeasuredHeight() * f) / 2.0f))};
            float descendantCoordRelativeToSelf = getDescendantCoordRelativeToSelf((View) child.getParent(), iArr) * scaleX;
            int i = iArr[0];
            int i2 = iArr[1];
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                intrinsicIconScaleFactor = descendantCoordRelativeToSelf / dragView.getIntrinsicIconScaleFactor();
                iRound = (int) ((i2 + Math.round(textView.getPaddingTop() * intrinsicIconScaleFactor)) - ((dragView.getMeasuredHeight() * (1.0f - intrinsicIconScaleFactor)) / 2.0f));
                if (dragView.getDragVisualizeOffset() != null) {
                    iRound -= Math.round(dragView.getDragVisualizeOffset().y * intrinsicIconScaleFactor);
                }
                if (textView.getCompoundDrawables()[0] != null) {
                    iRound3 = i + Math.round(descendantCoordRelativeToSelf * child.getPaddingStart());
                } else {
                    iRound3 = i - ((dragView.getMeasuredWidth() - Math.round(descendantCoordRelativeToSelf * child.getMeasuredWidth())) / 2);
                }
            } else {
                if (child instanceof FolderIcon) {
                    iRound = (int) (((int) ((i2 + Math.round((child.getPaddingTop() - dragView.getDragRegionTop()) * descendantCoordRelativeToSelf)) - ((descendantCoordRelativeToSelf * 2.0f) / 2.0f))) - (((1.0f - descendantCoordRelativeToSelf) * dragView.getMeasuredHeight()) / 2.0f));
                    if (((FolderIcon) child).isLayoutHorizontal()) {
                        iRound3 = i + Math.round(child.getPaddingStart() * descendantCoordRelativeToSelf);
                        intrinsicIconScaleFactor = descendantCoordRelativeToSelf;
                    } else {
                        iRound2 = (dragView.getMeasuredWidth() - Math.round(child.getMeasuredWidth() * descendantCoordRelativeToSelf)) / 2;
                    }
                } else {
                    iRound = i2 - (Math.round((dragView.getHeight() - child.getMeasuredHeight()) * descendantCoordRelativeToSelf) / 2);
                    iRound2 = Math.round((dragView.getMeasuredWidth() - child.getMeasuredWidth()) * descendantCoordRelativeToSelf) / 2;
                }
                iRound3 = i - iRound2;
                intrinsicIconScaleFactor = descendantCoordRelativeToSelf;
            }
            int i3 = iRound3;
            int i4 = rect.left;
            int i5 = rect.top;
            child.setVisibility(4);
            animateViewIntoPosition(dragView, i4, i5, i3, iRound, 1.0f, 1.0f, 1.0f, intrinsicIconScaleFactor, intrinsicIconScaleFactor, new Runnable() { // from class: com.android.launcher3.dragndrop.DragLayer.1
                @Override // java.lang.Runnable
                public void run() {
                    child.setVisibility(0);
                    View view = child;
                    if (view instanceof BubbleTextView) {
                        ((BubbleTextView) view).setItemInfo();
                    }
                    Runnable runnable = onFinishAnimationRunnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }, 0, duration, anchorView);
            return;
        }
        LGLog.d(TAG, "child is null");
    }

    public void animateViewIntoPosition(final DragView view, final int fromX, final int fromY, final int toX, final int toY, float finalAlpha, float initScaleX, float initScaleY, float finalScaleX, float finalScaleY, Runnable onCompleteRunnable, int animationEndStyle, int duration, View anchorView) {
        animateView(view, new Rect(fromX, fromY, view.getMeasuredWidth() + fromX, view.getMeasuredHeight() + fromY), new Rect(toX, toY, view.getMeasuredWidth() + toX, view.getMeasuredHeight() + toY), finalAlpha, initScaleX, initScaleY, finalScaleX, finalScaleY, duration, null, null, onCompleteRunnable, animationEndStyle, anchorView);
    }

    public void animateView(final DragView view, final Rect from, final Rect to, final float finalAlpha, final float initScaleX, final float initScaleY, final float finalScaleX, final float finalScaleY, int duration, final Interpolator motionInterpolator, final Interpolator alphaInterpolator, final Runnable onCompleteRunnable, final int animationEndStyle, View anchorView) {
        int iMax;
        float fHypot = (float) Math.hypot(to.left - from.left, to.top - from.top);
        Resources resources = getResources();
        float integer = resources.getInteger(R.integer.config_dropAnimMaxDist);
        if (duration < 0) {
            int integer2 = resources.getInteger(R.integer.config_dropAnimMaxDuration);
            if (fHypot < integer) {
                integer2 = (int) (integer2 * this.mCubicEaseOutInterpolator.getInterpolation(fHypot / integer));
            }
            iMax = Math.max(integer2, resources.getInteger(R.integer.config_dropAnimMinDuration));
        } else {
            iMax = duration;
        }
        TimeInterpolator timeInterpolator = (alphaInterpolator == null || motionInterpolator == null) ? this.mCubicEaseOutInterpolator : null;
        final float alpha = view.getAlpha();
        final float scaleX = view.getScaleX();
        animateView(view, new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.dragndrop.DragLayer.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float fFloatValue = ((Float) animation.getAnimatedValue()).floatValue();
                int measuredWidth = view.getMeasuredWidth();
                int measuredHeight = view.getMeasuredHeight();
                Interpolator interpolator = alphaInterpolator;
                float interpolation = interpolator == null ? fFloatValue : interpolator.getInterpolation(fFloatValue);
                Interpolator interpolator2 = motionInterpolator;
                float interpolation2 = interpolator2 == null ? fFloatValue : interpolator2.getInterpolation(fFloatValue);
                float f = initScaleX;
                float f2 = scaleX;
                float f3 = f * f2;
                float f4 = initScaleY * f2;
                float f5 = 1.0f - fFloatValue;
                float f6 = (finalScaleX * fFloatValue) + (f3 * f5);
                float f7 = (finalScaleY * fFloatValue) + (f5 * f4);
                float f8 = (finalAlpha * interpolation) + (alpha * (1.0f - interpolation));
                float f9 = from.left + (((f3 - 1.0f) * measuredWidth) / 2.0f);
                int iRound = (int) (from.top + (((f4 - 1.0f) * measuredHeight) / 2.0f) + Math.round((to.top - r4) * interpolation2));
                int iRound2 = (((int) (f9 + Math.round((to.left - f9) * interpolation2))) - DragLayer.this.mDropView.getScrollX()) + (DragLayer.this.mAnchorView == null ? 0 : (int) (DragLayer.this.mAnchorView.getScaleX() * (DragLayer.this.mAnchorViewInitialScrollX - DragLayer.this.mAnchorView.getScrollX())));
                int scrollY = iRound - DragLayer.this.mDropView.getScrollY();
                DragLayer.this.mDropView.setTranslationX(iRound2);
                DragLayer.this.mDropView.setTranslationY(scrollY);
                DragLayer.this.mDropView.setScaleX(f6);
                DragLayer.this.mDropView.setScaleY(f7);
                DragLayer.this.mDropView.setAlpha(f8);
            }
        }, iMax, timeInterpolator, onCompleteRunnable, animationEndStyle, anchorView);
    }

    public void animateView(final DragView view, ValueAnimator.AnimatorUpdateListener updateCb, int duration, TimeInterpolator interpolator, final Runnable onCompleteRunnable, final int animationEndStyle, View anchorView) {
        ValueAnimator valueAnimator = this.mDropAnim;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mDropView = view;
        view.cancelAnimation();
        this.mDropView.resetLayoutParams();
        if (anchorView != null) {
            this.mAnchorViewInitialScrollX = anchorView.getScrollX();
        }
        this.mAnchorView = anchorView;
        ValueAnimator valueAnimator2 = new ValueAnimator();
        this.mDropAnim = valueAnimator2;
        valueAnimator2.setInterpolator(interpolator);
        this.mDropAnim.setDuration(duration);
        this.mDropAnim.setFloatValues(0.0f, 1.0f);
        this.mDropAnim.addUpdateListener(updateCb);
        this.mDropAnim.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.dragndrop.DragLayer.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Runnable runnable = onCompleteRunnable;
                if (runnable != null) {
                    runnable.run();
                }
                int i = animationEndStyle;
                if (i == 0) {
                    DragLayer.this.clearAnimatedView();
                } else {
                    if (i != 2) {
                        return;
                    }
                    DragLayer.this.fadeOutDropView();
                }
            }
        });
        this.mDropAnim.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fadeOutDropView() {
        if (this.mDropView != null) {
            Animation animationLoadAnimation = AnimationUtils.loadAnimation(this.mLauncher.getApplicationContext(), R.anim.fade_out_widget);
            animationLoadAnimation.setFillAfter(true);
            this.mDropView.startAnimation(animationLoadAnimation);
        }
    }

    public void clearAnimatedView() {
        ValueAnimator valueAnimator = this.mDropAnim;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        DragView dragView = this.mDropView;
        if (dragView != null) {
            this.mDragController.onDeferredEndDrag(dragView);
        }
        this.mDropView = null;
        invalidate();
    }

    public View getAnimatedView() {
        return this.mDropView;
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        updateChildIndices();
        ((Launcher) this.mActivity).onDragLayerHierarchyChanged();
    }

    @Override // com.android.launcher3.views.BaseDragLayer, android.view.ViewGroup
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        updateChildIndices();
        ((Launcher) this.mActivity).onDragLayerHierarchyChanged();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void bringChildToFront(View child) {
        super.bringChildToFront(child);
        updateChildIndices();
    }

    private void updateChildIndices() {
        this.mTopViewIndex = -1;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) instanceof DragView) {
                this.mTopViewIndex = i;
            }
        }
        this.mChildCountOnLastUpdate = childCount;
    }

    @Override // android.view.ViewGroup
    protected int getChildDrawingOrder(int childCount, int i) {
        if (this.mChildCountOnLastUpdate != childCount) {
            updateChildIndices();
        }
        int i2 = this.mTopViewIndex;
        return i2 == -1 ? i : i == childCount + (-1) ? i2 : i < i2 ? i : i + 1;
    }

    void onEnterScrollArea(int direction) {
        this.mInScrollArea = true;
        invalidate();
    }

    void onExitScrollArea() {
        this.mInScrollArea = false;
        invalidate();
    }

    public void hidePageHints() {
        this.mShowPageHints = false;
        invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        float f = this.mBackgroundAlpha;
        if (f > 0.0f) {
            canvas.drawColor((((int) (f * 255.0f)) << 24) | SCRIM_COLOR);
        }
        this.mWorkspaceScrim.draw(canvas);
        this.mOverviewScrim.updateCurrentScrimmedView(this);
        super.dispatchDraw(canvas);
        if (this.mOverviewScrim.getScrimmedView() == null) {
            this.mOverviewScrim.draw(canvas);
        }
    }

    private void drawPageHints(Canvas canvas) {
        if (this.mShowPageHints) {
            Workspace workspace = this.mLauncher.getWorkspace();
            int measuredWidth = getMeasuredWidth();
            int nextPage = workspace.getNextPage();
            CellLayout cellLayout = (CellLayout) workspace.getChildAt(this.mIsRtl ? nextPage + 1 : nextPage - 1);
            CellLayout cellLayout2 = (CellLayout) workspace.getChildAt(this.mIsRtl ? nextPage - 1 : nextPage + 1);
            if (cellLayout != null && cellLayout.isDragTarget()) {
                Drawable drawable = (this.mInScrollArea && cellLayout.getIsDragOverlapping()) ? this.mLeftHoverDrawableActive : this.mLeftHoverDrawable;
                drawable.setBounds(0, this.mScrollChildPosition.top, drawable.getIntrinsicWidth(), this.mScrollChildPosition.bottom);
                drawable.draw(canvas);
            }
            if (cellLayout2 == null || !cellLayout2.isDragTarget()) {
                return;
            }
            Drawable drawable2 = (this.mInScrollArea && cellLayout2.getIsDragOverlapping()) ? this.mRightHoverDrawableActive : this.mRightHoverDrawable;
            drawable2.setBounds(measuredWidth - drawable2.getIntrinsicWidth(), this.mScrollChildPosition.top, measuredWidth, this.mScrollChildPosition.bottom);
            drawable2.draw(canvas);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == this.mOverviewScrim.getScrimmedView()) {
            this.mOverviewScrim.draw(canvas);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    public void setBackgroundAlpha(float alpha) {
        if (alpha != this.mBackgroundAlpha) {
            this.mBackgroundAlpha = alpha;
            invalidate();
        }
    }

    public float getBackgroundAlpha() {
        return this.mBackgroundAlpha;
    }

    public BaseDragLayer.TouchCompleteListener getTouchCompleteListener() {
        return this.mTouchCompleteListener;
    }

    @Override // com.android.launcher3.InsettableFrameLayout, com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        super.setInsets(insets);
    }

    @Override // android.view.View
    public boolean onDragEvent(DragEvent event) {
        if (event.getAction() != 2) {
            LGLog.d("DragNDrop", " " + event.getAction() + " " + event.getX() + " " + event.getY() + " " + event.getLocalState() + "  " + event.getResult() + "  " + event.getClipData() + " " + event.getClipDescription());
        }
        ClipDescription clipDescription = event.getClipDescription();
        if (clipDescription != null && "FloatingActivity".equals(clipDescription.getLabel())) {
            return floatingFileManagerDragEvent(event);
        }
        return this.mDragController.onDragEvent(event);
    }

    private boolean floatingFileManagerDragEvent(DragEvent event) {
        int action = event.getAction();
        if (action == 1) {
            LGLog.i("DragLayer", "DragEvent.ACTION_DRAG_STARTED");
            return true;
        }
        if (action != 3) {
            return false;
        }
        return dropFileManagerShortcut(event.getClipData());
    }

    private boolean dropFileManagerShortcut(ClipData clipData) {
        Launcher launcher;
        Workspace workspace;
        if (clipData != null && clipData.getItemCount() > 0) {
            Intent intent = clipData.getItemAt(0).getIntent();
            LGLog.d("DragLayer", "DragEvent.ACTION_DROP, Intent = " + intent);
            if (intent != null && (launcher = this.mLauncher) != null) {
                boolean zIsWorkspaceState = launcher.isWorkspaceState();
                boolean zIsInUninstallMode = UninstallModeManager.getInstance(this.mLauncher).isInUninstallMode();
                if ((zIsWorkspaceState || zIsInUninstallMode) && (workspace = this.mLauncher.getWorkspace()) != null) {
                    return fileDropShortCutCreate(this.mLauncher, intent, workspace.getCurrentPage());
                }
                return false;
            }
        }
        return false;
    }

    public static boolean fileDropShortCutCreate(Launcher launcher, Intent intent, int currntPage) {
        Intent intent2;
        if (!MemoryUtils.hasAvailableFileSystemMemory(launcher, true)) {
            Toast.makeText(launcher, R.string.memory_full_msg_shortage, 1).show();
            LGLog.i("DragLayer", "LGHome:mem full sorry");
            return false;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            LGLog.i("DragLayer", "extrasBundle is null ");
            return false;
        }
        int[] iArr = {-1, -1};
        String str = (String) extras.get("android.intent.extra.shortcut.NAME");
        String type = intent.getType();
        Boolean boolValueOf = Boolean.valueOf(intent.getBooleanExtra("com.lge.filemanager.intent.extra.ISDIR", false));
        if ("null/null".equals(type) && boolValueOf.booleanValue()) {
            LGLog.i("DragLayer", "mimetype is " + type);
            intent2 = new Intent();
            Object obj = extras.get("android.intent.extra.STREAM");
            intent2.setComponent(new ComponentName("com.lge.filemanager", "com.lge.filemanager.view.FolderActivity2"));
            intent2.setAction("com.lge.filemanager.intent.action.EXECUTE");
            intent2.setFlags(67108864);
            intent2.putExtra("shortcutKey", (String) obj);
        } else {
            Intent intent3 = new Intent("android.intent.action.VIEW");
            try {
                intent3.setDataAndType((Uri) extras.get("android.intent.extra.STREAM"), type);
            } catch (ClassCastException unused) {
                LGLog.i("DragLayer", "mimetype is " + type);
            }
            intent2 = intent3;
        }
        Workspace workspace = launcher.getWorkspace();
        ArrayList<Long> screenOrder = workspace.getScreenOrder();
        if (screenOrder.size() == 0) {
            LGLog.i("DragLayer", "workspaceScreens size is zero");
            return false;
        }
        long jLongValue = screenOrder.get(workspace.getCurrentPage()).longValue();
        long jCommitExtraEmptyScreen = jLongValue == -201 ? workspace.commitExtraEmptyScreen() : jLongValue;
        if (jCommitExtraEmptyScreen == -301 || jCommitExtraEmptyScreen == -401) {
            Toast.makeText(launcher, R.string.out_of_space, 1).show();
            return false;
        }
        intent.putExtra("android.intent.extra.shortcut.INTENT", intent2);
        if (LauncherModel.findNextAvailableIconSpaceInScreen(launcher, jCommitExtraEmptyScreen, iArr, 1, 1)) {
            launcher.processShortcutFromFileManager(new ComponentName("com.lge.filemanager", "com.lge.filemanager.view.LaunchActivity"), -100L, jCommitExtraEmptyScreen, new int[]{1, 1});
            PendingRequestArgs pendingRequestArgs = launcher.getPendingRequestArgs(intent);
            if (pendingRequestArgs != null) {
                launcher.completeAddShortcut(intent, -100L, jCommitExtraEmptyScreen, iArr[0], iArr[1], pendingRequestArgs, true);
            } else {
                launcher.completeAddShortcut(intent, -100L, jCommitExtraEmptyScreen, iArr[0], iArr[1]);
            }
            if (str == null || str.length() == 0) {
                Toast.makeText(launcher, R.string.sp_shortcut_installed_empty_title_NORMAL, 1).show();
            } else {
                Toast.makeText(launcher, launcher.getString(R.string.sp_shortcut_install, new Object[]{str}), 1).show();
            }
            return true;
        }
        Toast.makeText(launcher, R.string.out_of_space, 1).show();
        return false;
    }

    public WorkspaceAndHotseatScrim getScrim() {
        return this.mWorkspaceScrim;
    }

    public OverviewScrim getOverviewScrim() {
        return this.mOverviewScrim;
    }
}
