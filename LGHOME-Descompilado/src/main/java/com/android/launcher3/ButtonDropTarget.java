package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.FloatArrayEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import com.android.launcher3.DropTarget;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.UiThreadCircularReveal;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.lge.contextenginelibrary.BuildConfig;
import com.lge.launcher3.R;
import com.lge.launcher3.droptarget.DisableDropTarget;
import com.lge.launcher3.droptarget.LGUninstallDropTarget;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGSoundManager;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.VibratorManager;

/* JADX INFO: loaded from: classes.dex */
public abstract class ButtonDropTarget extends TextView implements DropTarget, DragController.DragListener, View.OnClickListener {
    private static int DRAG_VIEW_DROP_DURATION = 285;
    final int DISAPPEAR_ANIM_DURATION;
    final int OPEN_CLOSE_ANIM_DURATION;
    final int TRANSLATE_ANIM_DURATION;
    protected TextView animationView;
    protected boolean mActive;
    private Animator mAni;
    private int mBottomDragPadding;
    protected AnimatorSet mCurrentColorAnim;
    ColorMatrix mCurrentFilter;
    public int mDragViewOriginColor;
    protected Drawable mDrawable;
    private String mDropTargetTitle;
    ColorMatrix mDstFilter;
    protected int mHoverColor;
    private boolean mIsAcceptableDragged;
    private boolean mIsDragEntered;
    protected Launcher mLauncher;
    protected ColorStateList mOriginalTextColor;
    protected SearchDropTargetBar mSearchDropTargetBar;
    ColorMatrix mSrcFilter;
    private Animation mTranslateToLeftAni;
    private Animation mTranslateToRightAni;

    enum DROP_TARGET_ANIM_TYPE {
        OPEN,
        CLOSE
    }

    protected abstract void completeDrop(DropTarget.DragObject d);

    protected String getAccessibilityDropConfirmation() {
        return null;
    }

    public abstract LauncherLogProto.Target getDropTargetForLogging();

    @Override // com.android.launcher3.DropTarget
    public void onFlingToDelete(DropTarget.DragObject d, PointF vec) {
    }

    @Override // com.android.launcher3.DropTarget
    public void prepareAccessibilityDrop() {
    }

    protected abstract boolean supportsDrop(DragSource source, Object info);

    public ButtonDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHoverColor = 0;
        this.OPEN_CLOSE_ANIM_DURATION = 200;
        this.TRANSLATE_ANIM_DURATION = 140;
        this.DISAPPEAR_ANIM_DURATION = 200;
        this.mIsAcceptableDragged = false;
        this.mIsDragEntered = false;
        this.mBottomDragPadding = getResources().getDimensionPixelSize(R.dimen.drop_target_drag_padding);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mOriginalTextColor = getTextColors();
        ((Launcher) getContext()).getDeviceProfile();
        this.mDropTargetTitle = getText().toString();
    }

    protected void setDrawable(int resId) {
        this.mDrawable = getResources().getDrawable(resId);
        if (Build.VERSION.SDK_INT >= 17) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(this.mDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(this.mDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
        }
    }

    public void setLauncher(Launcher launcher) {
        this.mLauncher = launcher;
    }

    public void setSearchDropTargetBar(SearchDropTargetBar searchDropTargetBar) {
        this.mSearchDropTargetBar = searchDropTargetBar;
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragEnter(DropTarget.DragObject d) {
        if (this.mIsDragEntered || !isAcceptableDragged()) {
            return;
        }
        this.mIsDragEntered = true;
        sendAccessibilityEvent(32768);
        setText("");
        this.mDragViewOriginColor = d.dragView.getSolidColor();
        if (this instanceof LGUninstallDropTarget) {
            d.dragView.setColor(SupportMenu.CATEGORY_MASK);
        } else {
            d.dragView.setColor(-12303292);
        }
        d.dragView.setAlpha(0.6f);
        final Runnable runnable = new Runnable() { // from class: com.android.launcher3.ButtonDropTarget.1
            @Override // java.lang.Runnable
            public void run() {
                ButtonDropTarget.this.mDrawable.setAlpha(0);
                ButtonDropTarget buttonDropTarget = ButtonDropTarget.this;
                buttonDropTarget.mAni = buttonDropTarget.getCreateCircularReveal(new Runnable() { // from class: com.android.launcher3.ButtonDropTarget.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                    }
                }, DROP_TARGET_ANIM_TYPE.OPEN);
                ButtonDropTarget.this.mAni.start();
            }
        };
        if (this.mLauncher.mDeviceProfile.isLandscape) {
            runnable.run();
        } else {
            TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, ((getRight() + getLeft()) / 2) - getIconCenter(d.dragView.getMeasuredWidth(), d.dragView.getMeasuredHeight(), this.mDrawable.getIntrinsicWidth(), this.mDrawable.getIntrinsicHeight()).centerX(), 0.0f, 0.0f);
            this.mTranslateToRightAni = translateAnimation;
            translateAnimation.setDuration(140L);
            this.mTranslateToRightAni.setRepeatCount(0);
            this.mTranslateToRightAni.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.launcher3.ButtonDropTarget.2
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation anim) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation anim) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation anim) {
                    runnable.run();
                }
            });
            startAnimation(this.mTranslateToRightAni);
        }
        VibratorManager.performHapticFeedback(this.mLauncher, 65541);
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragOver(DropTarget.DragObject d) {
        if (this.mIsDragEntered || !isAcceptableDragged()) {
            return;
        }
        onDragEnter(d);
    }

    public static boolean isWidgetTypeItemInfo(Object item) {
        return (item instanceof PendingAddWidgetInfo) || (item instanceof PendingAddShortcutInfo);
    }

    protected void resetHoverColor() {
        if (Utilities.isLmpOrAbove()) {
            animateTextColor(this.mOriginalTextColor.getDefaultColor());
        } else {
            this.mDrawable.setColorFilter(null);
            setTextColor(this.mOriginalTextColor);
        }
    }

    private void animateTextColor(int targetColor) {
        AnimatorSet animatorSet = this.mCurrentColorAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mCurrentColorAnim = animatorSet2;
        animatorSet2.setDuration(DragView.COLOR_CHANGE_DURATION);
        if (this.mSrcFilter == null) {
            this.mSrcFilter = new ColorMatrix();
            this.mDstFilter = new ColorMatrix();
            this.mCurrentFilter = new ColorMatrix();
        }
        DragView.setColorScale(getTextColor(), this.mSrcFilter);
        DragView.setColorScale(targetColor, this.mDstFilter);
        ValueAnimator valueAnimatorOfObject = ValueAnimator.ofObject(new FloatArrayEvaluator(this.mCurrentFilter.getArray()), this.mSrcFilter.getArray(), this.mDstFilter.getArray());
        valueAnimatorOfObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.ButtonDropTarget.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                ButtonDropTarget.this.mDrawable.setColorFilter(new ColorMatrixColorFilter(ButtonDropTarget.this.mCurrentFilter));
                ButtonDropTarget.this.invalidate();
            }
        });
        this.mCurrentColorAnim.play(valueAnimatorOfObject);
        this.mCurrentColorAnim.play(ObjectAnimator.ofArgb(this, "textColor", targetColor));
        this.mCurrentColorAnim.start();
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragExit(DropTarget.DragObject d) {
        Runnable runnable;
        this.mIsDragEntered = false;
        d.dragView.setColor(this.mDragViewOriginColor);
        d.dragView.setAlpha(1.0f);
        Rect iconCenter = getIconCenter(d.dragView.getMeasuredWidth(), d.dragView.getMeasuredHeight(), this.mDrawable.getIntrinsicWidth(), this.mDrawable.getIntrinsicHeight());
        int right = (getRight() + getLeft()) / 2;
        final Runnable runnable2 = new Runnable() { // from class: com.android.launcher3.ButtonDropTarget.4
            @Override // java.lang.Runnable
            public void run() {
                ButtonDropTarget buttonDropTarget = ButtonDropTarget.this;
                buttonDropTarget.setText(buttonDropTarget.mDropTargetTitle);
            }
        };
        if (this.mLauncher.mDeviceProfile.isLandscape) {
            runnable = new Runnable() { // from class: com.android.launcher3.ButtonDropTarget.5
                @Override // java.lang.Runnable
                public void run() {
                    ButtonDropTarget.this.mDrawable.setAlpha(255);
                    ButtonDropTarget.this.setBackgroundColor(0);
                    runnable2.run();
                }
            };
        } else {
            Runnable runnable3 = new Runnable() { // from class: com.android.launcher3.ButtonDropTarget.6
                @Override // java.lang.Runnable
                public void run() {
                    ButtonDropTarget.this.mDrawable.setAlpha(255);
                    ButtonDropTarget.this.setBackgroundColor(0);
                    ButtonDropTarget buttonDropTarget = ButtonDropTarget.this;
                    buttonDropTarget.startAnimation(buttonDropTarget.mTranslateToLeftAni);
                }
            };
            TranslateAnimation translateAnimation = new TranslateAnimation(right - iconCenter.centerX(), 0.0f, 0.0f, 0.0f);
            this.mTranslateToLeftAni = translateAnimation;
            translateAnimation.setDuration(140L);
            this.mTranslateToLeftAni.setRepeatCount(0);
            this.mTranslateToLeftAni.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.launcher3.ButtonDropTarget.7
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation anim) {
                    runnable2.run();
                }
            });
            runnable = runnable3;
        }
        if (d.dragComplete) {
            return;
        }
        Animator createCircularReveal = getCreateCircularReveal(runnable, DROP_TARGET_ANIM_TYPE.CLOSE);
        this.mAni = createCircularReveal;
        createCircularReveal.start();
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragStart(DragSource source, Object info, int dragAction) {
        int dimensionPixelSize;
        int dimensionPixelOffset;
        this.mActive = supportsDrop(source, info);
        this.mDrawable.setColorFilter(null);
        AnimatorSet animatorSet = this.mCurrentColorAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.mCurrentColorAnim = null;
        }
        if (this.mLauncher.getWorkspace().isInOverviewMode()) {
            this.mActive = false;
        }
        if (LGHomeFeature.isEnableDefaultHome()) {
            ((ViewGroup) getParent()).setVisibility(this.mActive ? 0 : 8);
        } else if ((this instanceof LGUninstallDropTarget) || (this instanceof DisableDropTarget)) {
            setVisibility(this.mActive ? 0 : 8);
        } else {
            ((ViewGroup) getParent()).setVisibility(this.mActive ? 0 : 8);
        }
        restoreNormalStatus();
        ((ViewGroup) getParent()).setBackgroundColor(Color.parseColor("#00000000"));
        this.mIsAcceptableDragged = false;
        this.mIsDragEntered = false;
        if (this.animationView == null) {
            TextView textView = new TextView(getContext());
            this.animationView = textView;
            this.mSearchDropTargetBar.addView(textView);
            ViewGroup.LayoutParams layoutParams = this.animationView.getLayoutParams();
            if (this.mLauncher.getDeviceProfile().isLandscape && this.mLauncher.getDeviceProfile().allowRotation) {
                dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.delete_button_animation_image_width_land);
            } else {
                dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.delete_button_animation_image_width_port);
            }
            layoutParams.width = dimensionPixelSize;
            ViewGroup.LayoutParams layoutParams2 = this.animationView.getLayoutParams();
            if (this.mLauncher.getDeviceProfile().isLandscape && this.mLauncher.getDeviceProfile().allowRotation) {
                dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.delete_button_animation_image_height_land);
            } else {
                dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.delete_button_animation_image_height_port);
            }
            layoutParams2.height = dimensionPixelOffset;
        }
    }

    @Override // com.android.launcher3.DropTarget
    public final boolean acceptDrop(DropTarget.DragObject dragObject) {
        return supportsDrop(dragObject.dragSource, dragObject.dragInfo);
    }

    @Override // com.android.launcher3.DropTarget
    public boolean isDropEnabled() {
        if (!isAcceptableDragged() || UninstallModeManager.getInstance(this.mContext).isInUninstallMode()) {
            return false;
        }
        return this.mActive;
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragEnd() {
        this.mActive = false;
        Animator animator = this.mAni;
        if (animator != null && animator.isRunning()) {
            this.mAni.cancel();
        }
        if (this.animationView != null) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = this.mSearchDropTargetBar.getWidth();
            layoutParams.height = this.mSearchDropTargetBar.getHeight();
            this.animationView.setVisibility(8);
        }
    }

    @Override // com.android.launcher3.DropTarget
    public void onDrop(final DropTarget.DragObject d) {
        this.mDrawable.setAlpha(0);
        setText("");
        Animation animation = this.mTranslateToRightAni;
        if (animation != null) {
            animation.setAnimationListener(null);
            this.mTranslateToRightAni = null;
        }
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect rect = new Rect();
        dragLayer.getViewRectRelativeToSelf(d.dragView, rect);
        Rect dropTargetCenter = getDropTargetCenter(d.dragView.getMeasuredWidth(), d.dragView.getMeasuredHeight());
        float fWidth = dropTargetCenter.width() / rect.width();
        this.mSearchDropTargetBar.deferOnDragEnd();
        LGUserLog.send(this.mLauncher, LGUserLog.FEATURENAME_REMOVE_ITEM_BY_TRASHCAN);
        final Runnable runnable = new Runnable() { // from class: com.android.launcher3.ButtonDropTarget.9
            @Override // java.lang.Runnable
            public void run() {
                ButtonDropTarget.this.setBackgroundColor(0);
                ButtonDropTarget.this.mDrawable.setAlpha(0);
                ButtonDropTarget.this.setDropCustomDrawablePadding();
                ButtonDropTarget.this.completeDrop(d);
                ButtonDropTarget.this.mSearchDropTargetBar.onDragEnd();
                ButtonDropTarget.this.mLauncher.exitSpringLoadedDragModeDelayed(true, BuildConfig.VERSION_CODE, null);
            }
        };
        Runnable runnable2 = new Runnable() { // from class: com.android.launcher3.ButtonDropTarget.10
            @Override // java.lang.Runnable
            public void run() {
                ButtonDropTarget buttonDropTarget = ButtonDropTarget.this;
                buttonDropTarget.mAni = buttonDropTarget.getCreateCircularReveal(runnable, DROP_TARGET_ANIM_TYPE.CLOSE);
                ButtonDropTarget.this.mAni.start();
                ButtonDropTarget buttonDropTarget2 = ButtonDropTarget.this;
                if ((buttonDropTarget2 instanceof LGUninstallDropTarget) || (buttonDropTarget2 instanceof DisableDropTarget)) {
                    LGSoundManager.getInstance(buttonDropTarget2.getContext()).play(LGSoundManager.SoundType.SOUND_INDEX_UNINSTALL);
                } else {
                    LGSoundManager.getInstance(buttonDropTarget2.getContext()).play(LGSoundManager.SoundType.SOUND_INDEX_REMOVE);
                }
            }
        };
        if (this instanceof LGUninstallDropTarget) {
            d.dragView.setColor(SupportMenu.CATEGORY_MASK);
        } else {
            d.dragView.setColor(-12303292);
        }
        d.dragView.setAlpha(0.6f);
        dragLayer.animateView(d.dragView, rect, dropTargetCenter, fWidth, 1.0f, 1.0f, 0.2f, 0.2f, 200, new DecelerateInterpolator(2.0f), new LinearInterpolator(), runnable2, 0, null);
    }

    @Override // com.android.launcher3.DropTarget
    public void getHitRectRelativeToDragLayer(Rect outRect) {
        super.getHitRect(outRect);
        outRect.bottom += this.mBottomDragPadding;
        int[] iArr = new int[2];
        this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf((View) this, iArr);
        outRect.offsetTo(iArr[0], iArr[1]);
    }

    protected Rect getIconRect(int viewWidth, int viewHeight, int drawableWidth, int drawableHeight) {
        int paddingLeft;
        int paddingRight;
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect rect = new Rect();
        dragLayer.getViewRectRelativeToSelf(this, rect);
        if (Utilities.isRtl(getResources())) {
            paddingRight = rect.right - getPaddingRight();
            paddingLeft = paddingRight - drawableWidth;
        } else {
            paddingLeft = getPaddingLeft() + rect.left;
            paddingRight = paddingLeft + drawableWidth;
        }
        int measuredHeight = rect.top + ((getMeasuredHeight() - drawableHeight) / 2);
        rect.set(paddingLeft, measuredHeight, paddingRight, measuredHeight + drawableHeight);
        rect.offset((-(viewWidth - drawableWidth)) / 2, (-(viewHeight - drawableHeight)) / 2);
        return rect;
    }

    @Override // com.android.launcher3.DropTarget
    public void getLocationInDragLayer(int[] loc) {
        this.mLauncher.getDragLayer().getLocationInDragLayer(this, loc);
    }

    public void enableAccessibleDrag(boolean enable) {
        setOnClickListener(enable ? this : null);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        LauncherAppState.getInstance(getContext()).getAccessibilityDelegate().handleAccessibleDrop(this, null, getAccessibilityDropConfirmation());
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            setCustomDrawablePadding();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    public int getTextColor() {
        return getTextColors().getDefaultColor();
    }

    public void setDropTargetTitle(String dropTargetTitle) {
        this.mDropTargetTitle = dropTargetTitle;
    }

    private void restoreNormalStatus() {
        setText(this.mDropTargetTitle);
        this.mDrawable.setAlpha(255);
        setCustomDrawablePadding();
    }

    private void setCustomDrawablePadding() {
        int width = getWidth();
        Rect rect = new Rect();
        TextPaint paint = getPaint();
        String str = this.mDropTargetTitle;
        paint.getTextBounds(str, 0, str.length(), rect);
        setPaddingRelative(iWidth, 0, iWidth, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDropCustomDrawablePadding() {
        setPaddingRelative(width, 0, width, 0);
    }

    private Rect getIconCenter(int viewWidth, int viewHeight, int drawableWidth, int drawableHeight) {
        int i;
        int paddingRight;
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect rect = new Rect();
        dragLayer.getViewRectRelativeToSelf(this, rect);
        if (Utilities.isRtl(getResources())) {
            paddingRight = rect.right - getPaddingRight();
            i = paddingRight - drawableWidth;
        } else {
            int paddingLeft = rect.left + getPaddingLeft();
            int i2 = drawableWidth + paddingLeft;
            i = paddingLeft;
            paddingRight = i2;
        }
        int measuredHeight = rect.top + ((getMeasuredHeight() - drawableHeight) / 2);
        rect.set(i, measuredHeight, paddingRight, drawableHeight + measuredHeight);
        return rect;
    }

    private Rect getDropTargetCenter(int viewWidth, int viewHeight) {
        Rect rect = new Rect();
        getGlobalVisibleRect(rect);
        rect.offset((-(viewWidth - getWidth())) / 2, (-(viewHeight - getHeight())) / 2);
        return rect;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Animator getCreateCircularReveal(final Runnable onNextAnimationRunnable, final DROP_TARGET_ANIM_TYPE type) {
        ValueAnimator valueAnimatorCreateCircularReveal;
        int width = getWidth() / 2;
        int height = getHeight() / 2;
        float fHypot = (float) Math.hypot(width, height);
        int i = AnonymousClass12.$SwitchMap$com$android$launcher3$ButtonDropTarget$DROP_TARGET_ANIM_TYPE[type.ordinal()];
        if (i == 1) {
            this.animationView.setX(getLeft() + ((getWidth() - this.animationView.getWidth()) / 2));
            this.animationView.setY(getTop() + ((getHeight() - this.animationView.getHeight()) / 2));
            this.animationView.setVisibility(0);
            valueAnimatorCreateCircularReveal = UiThreadCircularReveal.createCircularReveal(this, width, height, 0.0f, fHypot);
        } else {
            valueAnimatorCreateCircularReveal = i != 2 ? null : UiThreadCircularReveal.createCircularReveal(this, width, height, fHypot, 0.0f);
        }
        valueAnimatorCreateCircularReveal.setDuration(200L);
        valueAnimatorCreateCircularReveal.setInterpolator(new LogDecelerateInterpolator(100, 0));
        valueAnimatorCreateCircularReveal.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.ButtonDropTarget.11
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                ButtonDropTarget buttonDropTarget = ButtonDropTarget.this;
                if (buttonDropTarget instanceof LGUninstallDropTarget) {
                    buttonDropTarget.animationView.setBackgroundResource(R.drawable.btn_homescreen_spring_loaded_red);
                } else {
                    buttonDropTarget.animationView.setBackgroundResource(R.drawable.btn_homescreen_spring_loaded_gray);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Runnable runnable = onNextAnimationRunnable;
                if (runnable != null) {
                    runnable.run();
                }
                if (type == DROP_TARGET_ANIM_TYPE.CLOSE) {
                    ButtonDropTarget.this.animationView.setVisibility(8);
                }
            }
        });
        return valueAnimatorCreateCircularReveal;
    }

    /* JADX INFO: renamed from: com.android.launcher3.ButtonDropTarget$12, reason: invalid class name */
    static /* synthetic */ class AnonymousClass12 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$ButtonDropTarget$DROP_TARGET_ANIM_TYPE;

        static {
            int[] iArr = new int[DROP_TARGET_ANIM_TYPE.values().length];
            $SwitchMap$com$android$launcher3$ButtonDropTarget$DROP_TARGET_ANIM_TYPE = iArr;
            try {
                iArr[DROP_TARGET_ANIM_TYPE.OPEN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$launcher3$ButtonDropTarget$DROP_TARGET_ANIM_TYPE[DROP_TARGET_ANIM_TYPE.CLOSE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public boolean isAcceptableDragged() {
        if (!this.mIsAcceptableDragged) {
            this.mIsAcceptableDragged = this.mLauncher.getDragController().getDragDistance() >= getResources().getDisplayMetrics().xdpi * 0.1f;
        }
        return this.mIsAcceptableDragged;
    }
}
