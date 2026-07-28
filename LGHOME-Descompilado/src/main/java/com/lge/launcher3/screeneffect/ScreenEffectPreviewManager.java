package com.lge.launcher3.screeneffect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import com.lge.launcher3.R;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectPreviewManager {
    public static final boolean DEBUG = false;
    public static final String TAG = "ScreenEffectPreviewManager";
    private Context mContext;
    private PreviewAnimationController mPreviewAnimationController;
    private IScreenEffectPreview mScreenEffectPreview = null;
    private ScreenEffectConst.ScreenEffectType mSelectedScreenEffectPreviewType = null;

    public ScreenEffectPreviewManager(Context context) {
        this.mContext = null;
        this.mPreviewAnimationController = null;
        this.mContext = context;
        this.mPreviewAnimationController = new PreviewAnimationController(context);
    }

    public void startPreviewAnimation(int typeIndex) {
        ScreenEffectConst.ScreenEffectType screenEffectType = ScreenEffectUtils.getScreenEffectType(this.mContext, typeIndex);
        if (this.mSelectedScreenEffectPreviewType != screenEffectType) {
            changeScreenEffectPreviewType(screenEffectType);
        }
        this.mPreviewAnimationController.startPreviewAnimation();
    }

    private void changeScreenEffectPreviewType(ScreenEffectConst.ScreenEffectType type) {
        this.mSelectedScreenEffectPreviewType = type;
        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType[this.mSelectedScreenEffectPreviewType.ordinal()];
        if (i == 1) {
            this.mScreenEffectPreview = new ScreenEffectPreviewSlide(this.mContext);
            return;
        }
        if (i == 2) {
            this.mScreenEffectPreview = new ScreenEffectPreviewBreeze(this.mContext);
        } else if (i == 3) {
            this.mScreenEffectPreview = new ScreenEffectPreviewCarousel(this.mContext);
        } else {
            if (i != 4) {
                return;
            }
            this.mScreenEffectPreview = new ScreenEffectPreviewPanorama(this.mContext);
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.screeneffect.ScreenEffectPreviewManager$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType;

        static {
            int[] iArr = new int[ScreenEffectConst.ScreenEffectType.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType = iArr;
            try {
                iArr[ScreenEffectConst.ScreenEffectType.SLIDE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType[ScreenEffectConst.ScreenEffectType.BREEZE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType[ScreenEffectConst.ScreenEffectType.CAROUSEL.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType[ScreenEffectConst.ScreenEffectType.PANORAMA.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public boolean drawChild(Canvas canvas, View child) {
        PreviewAnimationController previewAnimationController;
        if (this.mScreenEffectPreview == null || (previewAnimationController = this.mPreviewAnimationController) == null || !previewAnimationController.isRunning()) {
            return false;
        }
        child.buildDrawingCache(true);
        Bitmap drawingCache = child.getDrawingCache(true);
        if (drawingCache != null && !drawingCache.isRecycled()) {
            drawChild(canvas, child, ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT);
            drawChild(canvas, child, ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT);
            return true;
        }
        return false;
    }

    private void drawChild(Canvas canvas, View child, ScreenEffectConst.WhichPageToDraw whichPageToDraw) {
        ScreenEffectPreviewTargetManager screenEffectPreviewTargetManager = ScreenEffectPreviewTargetManager.getInstance(this.mContext);
        screenEffectPreviewTargetManager.updateTargetInfo(whichPageToDraw);
        canvas.save();
        canvas.concat(this.mScreenEffectPreview.getPageTransformationMatrix(child));
        canvas.drawBitmap(child.getDrawingCache(true), 0.0f, 0.0f, (Paint) null);
        ScreenEffectPreviewUtils.drawOutline(canvas, child, ScreenEffectPreviewUtils.getOutlineAlpha(screenEffectPreviewTargetManager.getTargetInfo(child)), null);
        canvas.restore();
    }

    public boolean isPreviewAnimationStarted() {
        return this.mPreviewAnimationController.isStarted();
    }

    public void cancelPreviewAnimation() {
        this.mPreviewAnimationController.clearAnimation();
    }

    private class PreviewAnimationController {
        private static final int ANIM_DURATION = 700;
        private final int mAnimStartDelay;
        private AnimatorSet mPreviewAnim = null;
        private boolean mIsStarted = false;
        private String mPreviewAnimType = null;

        public PreviewAnimationController(Context context) {
            this.mAnimStartDelay = context.getResources().getInteger(R.integer.config_screen_effect_preview_animation_start_delay);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startPreviewAnimation() {
            if (this.mIsStarted) {
                return;
            }
            this.mPreviewAnimType = ScreenEffectPreviewManager.this.mSelectedScreenEffectPreviewType != null ? ScreenEffectPreviewManager.this.mSelectedScreenEffectPreviewType.toString() : null;
            LGLog.i(ScreenEffectPreviewManager.TAG, String.format("startPreviewAnimation() : %s", this.mPreviewAnimType));
            this.mIsStarted = true;
            clearAnimation();
            ValueAnimator virtualScrollAnimation = getVirtualScrollAnimation();
            ValueAnimator invalidateAnimation = getInvalidateAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            this.mPreviewAnim = animatorSet;
            animatorSet.playTogether(virtualScrollAnimation, invalidateAnimation);
            this.mPreviewAnim.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.screeneffect.ScreenEffectPreviewManager.PreviewAnimationController.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    PreviewAnimationController.this.onStartPreviewAnimation();
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PreviewAnimationController.this.onEndPreviewAnimation();
                }
            });
            this.mPreviewAnim.setDuration(700L);
            this.mPreviewAnim.setStartDelay(this.mAnimStartDelay);
            this.mPreviewAnim.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onStartPreviewAnimation() {
            LGLog.i(ScreenEffectPreviewManager.TAG, String.format("onStartPreviewAnimation() : %s", this.mPreviewAnimType));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onEndPreviewAnimation() {
            LGLog.i(ScreenEffectPreviewManager.TAG, String.format("onEndPreviewAnimation() : %s", this.mPreviewAnimType));
            clearAnimation();
            View child = ScreenEffectPreviewTargetManager.getInstance(ScreenEffectPreviewManager.this.mContext).getChild();
            if (child != null) {
                child.destroyDrawingCache();
            }
        }

        private ValueAnimator getVirtualScrollAnimation() {
            View child = ScreenEffectPreviewTargetManager.getInstance(ScreenEffectPreviewManager.this.mContext).getChild();
            if (child == null) {
                return null;
            }
            ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(0, child.getMeasuredWidth());
            valueAnimatorOfInt.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.screeneffect.ScreenEffectPreviewManager.PreviewAnimationController.2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object animatedValue = animation.getAnimatedValue();
                    if (animatedValue instanceof Integer) {
                        ScreenEffectPreviewTargetManager.getInstance(ScreenEffectPreviewManager.this.mContext).setScrollX(((Integer) animatedValue).intValue());
                    }
                }
            });
            return valueAnimatorOfInt;
        }

        private ValueAnimator getInvalidateAnimation() {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimatorOfFloat.setInterpolator(new LinearInterpolator());
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.screeneffect.ScreenEffectPreviewManager.PreviewAnimationController.3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup parent = ScreenEffectPreviewTargetManager.getInstance(ScreenEffectPreviewManager.this.mContext).getParent();
                    if (parent != null) {
                        parent.invalidate();
                    }
                }
            });
            return valueAnimatorOfFloat;
        }

        public boolean isStarted() {
            return this.mIsStarted;
        }

        public boolean isRunning() {
            AnimatorSet animatorSet = this.mPreviewAnim;
            if (animatorSet == null) {
                return false;
            }
            return animatorSet.isRunning();
        }

        public void clearAnimation() {
            AnimatorSet animatorSet = this.mPreviewAnim;
            if (animatorSet == null) {
                return;
            }
            this.mIsStarted = false;
            for (Animator animator : animatorSet.getChildAnimations()) {
                if (animator != null) {
                    animator.removeAllListeners();
                    if (animator instanceof ValueAnimator) {
                        ((ValueAnimator) animator).removeAllUpdateListeners();
                    }
                }
            }
            this.mPreviewAnim.removeAllListeners();
            if (this.mPreviewAnim.isRunning()) {
                this.mPreviewAnim.cancel();
            }
            this.mPreviewAnim = null;
        }
    }

    public void destroy() {
        PreviewAnimationController previewAnimationController = this.mPreviewAnimationController;
        if (previewAnimationController != null) {
            previewAnimationController.clearAnimation();
            this.mPreviewAnimationController = null;
        }
        this.mScreenEffectPreview = null;
        this.mContext = null;
    }
}
