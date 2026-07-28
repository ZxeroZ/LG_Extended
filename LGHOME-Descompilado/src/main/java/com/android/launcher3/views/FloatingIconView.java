package com.android.launcher3.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Outline;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CancellationSignal;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.Utilities;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.FolderAdaptiveIcon;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.IconShape;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class FloatingIconView extends View implements Animator.AnimatorListener, ClipPathView, ViewTreeObserver.OnGlobalLayoutListener {
    private static final float DAMPING_RATIO_MEDIUM_LOW_BOUNCY = 0.3f;
    private static final boolean DEBUG = true;
    private static final int FADE_DURATION_MS = 200;
    private static final int FG_TRANS_X_FACTOR = 60;
    private static final int FG_TRANS_Y_FACTOR = 75;
    private static final String ICON_ANIM_TAG = "[FloatingIconAnim] ";
    public static final float SHAPE_PROGRESS_DURATION = 0.1f;
    private static final String STK_PACKAGE_NAME = "com.android.stk";
    private static final String TAG = "FloatingIconView";
    private static IconLoadResult sIconLoadResult;
    private Drawable mBackground;
    private Drawable mBadge;
    private final int mBlurSizeOutline;
    private Path mClipPath;
    private DrawFilter mDrawFilter;
    private final Rect mEndRevealRect;
    private Runnable mEndRunnable;
    private AnimatorSet mFadeAnimatorSet;
    private Runnable mFastFinishRunnable;
    private final SpringAnimation mFgSpringX;
    private final SpringAnimation mFgSpringY;
    private float mFgTransX;
    private float mFgTransY;
    private final Rect mFinalDrawableBounds;
    private Drawable mForeground;
    private IconLoadResult mIconLoadResult;
    private boolean mIsAdaptiveIcon;
    private boolean mIsAllowRotationAndLandscape;
    private boolean mIsOpening;
    private final boolean mIsRtl;
    private boolean mIsVerticalBarLayout;
    private final Launcher mLauncher;
    private ListenerView mListenerView;
    private CancellationSignal mLoadIconSignal;
    private Runnable mOnTargetChangeRunnable;
    private View mOriginalIcon;
    private final Rect mOutline;
    private RectF mPositionOut;
    private ValueAnimator mRevealAnimator;
    private float mRotation;
    private final Rect mStartRevealRect;
    private float mTaskCornerRadius;
    private static final Rect sTmpRect = new Rect();
    private static final RectF sTmpRectF = new RectF();
    private static final Object[] sTmpObjArray = new Object[1];
    private static final FloatPropertyCompat<FloatingIconView> mFgTransYProperty = new FloatPropertyCompat<FloatingIconView>("FloatingViewFgTransY") { // from class: com.android.launcher3.views.FloatingIconView.1
        /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(FloatingIconView view) {
            return view.mFgTransY;
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(FloatingIconView view, float transY) {
            view.mFgTransY = transY;
            view.invalidate();
        }
    };
    private static final FloatPropertyCompat<FloatingIconView> mFgTransXProperty = new FloatPropertyCompat<FloatingIconView>("FloatingViewFgTransX") { // from class: com.android.launcher3.views.FloatingIconView.2
        /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(FloatingIconView view) {
            return view.mFgTransX;
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(FloatingIconView view, float transX) {
            view.mFgTransX = transX;
            view.invalidate();
        }
    };

    public enum Action {
        Open,
        SwipeUpClose,
        Close
    }

    private static int getOffsetForIconBounds(Launcher l, Drawable drawable, RectF position) {
        return 0;
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationRepeat(Animator animator) {
    }

    public FloatingIconView(Context context) {
        this(context, null);
    }

    public FloatingIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsVerticalBarLayout = false;
        this.mIsAllowRotationAndLandscape = false;
        this.mIsAdaptiveIcon = true;
        this.mStartRevealRect = new Rect();
        this.mEndRevealRect = new Rect();
        this.mDrawFilter = new PaintFlagsDrawFilter(0, 3);
        this.mOutline = new Rect();
        this.mFinalDrawableBounds = new Rect();
        this.mLauncher = Launcher.getLauncher(context);
        this.mBlurSizeOutline = getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        this.mIsRtl = Utilities.isRtl(getResources());
        this.mListenerView = new ListenerView(context, attrs);
        this.mFgSpringX = new SpringAnimation(this, mFgTransXProperty).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(200.0f));
        this.mFgSpringY = new SpringAnimation(this, mFgTransYProperty).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(200.0f));
    }

    public boolean isAdaptiveIcon() {
        return this.mIsAdaptiveIcon;
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIsOpening) {
            return;
        }
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        super.onDetachedFromWindow();
    }

    /* JADX WARN: Removed duplicated region for block: B:79:0x01db  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void update(android.graphics.RectF r21, float r22, float r23, float r24, float r25, com.android.launcher3.views.FloatingIconView.Action r26) {
        /*
            r20 = this;
            r6 = r20
            r7 = r21
            r0 = r23
            r1 = r24
            r14 = r26
            com.android.launcher3.views.FloatingIconView$Action r2 = com.android.launcher3.views.FloatingIconView.Action.Open
            if (r14 != r2) goto L14
            r2 = 1
            r16 = r2
            r2 = r22
            goto L18
        L14:
            r2 = r22
            r16 = 0
        L18:
            r6.setAlpha(r2)
            android.view.ViewGroup$LayoutParams r2 = r20.getLayoutParams()
            r5 = r2
            com.android.launcher3.InsettableFrameLayout$LayoutParams r5 = (com.android.launcher3.InsettableFrameLayout.LayoutParams) r5
            boolean r2 = r6.mIsRtl
            if (r2 == 0) goto L39
            float r2 = r7.left
            com.android.launcher3.Launcher r3 = r6.mLauncher
            com.android.launcher3.DeviceProfile r3 = r3.getDeviceProfile()
            int r3 = r3.widthPx
            int r4 = r5.getMarginStart()
            int r3 = r3 - r4
            int r4 = r5.width
            int r3 = r3 - r4
            goto L3f
        L39:
            float r2 = r7.left
            int r3 = r5.getMarginStart()
        L3f:
            float r3 = (float) r3
            float r2 = r2 - r3
            r4 = r2
            float r2 = r7.top
            int r3 = r5.topMargin
            float r3 = (float) r3
            float r3 = r2 - r3
            r6.setTranslationX(r4)
            r6.setTranslationY(r3)
            int r2 = r5.width
            int r8 = r5.height
            int r2 = java.lang.Math.min(r2, r8)
            float r2 = (float) r2
            r13 = 0
            int r8 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            r12 = 1065353216(0x3f800000, float:1.0)
            if (r8 != 0) goto L62
            r17 = r12
            goto L64
        L62:
            r17 = r2
        L64:
            float r2 = r21.width()
            float r2 = r2 / r17
            float r8 = r21.height()
            float r8 = r8 / r17
            float r2 = java.lang.Math.min(r2, r8)
            float r2 = java.lang.Math.max(r12, r2)
            r8 = 2139095039(0x7f7fffff, float:3.4028235E38)
            int r8 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r8 >= 0) goto L85
            boolean r8 = java.lang.Float.isNaN(r2)
            if (r8 == 0) goto L9c
        L85:
            java.lang.String r8 = com.android.launcher3.views.FloatingIconView.TAG
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "scale is "
            r9.append(r10)
            r9.append(r2)
            java.lang.String r2 = r9.toString()
            com.lge.launcher3.util.LGLog.i(r8, r2)
            r2 = r12
        L9c:
            r6.setPivotX(r13)
            r6.setPivotY(r13)
            r6.setScaleX(r2)
            r6.setScaleY(r2)
            if (r16 == 0) goto Laf
            r8 = 1092616192(0x41200000, float:10.0)
            r18 = r8
            goto Lb1
        Laf:
            r18 = r12
        Lb1:
            float r8 = java.lang.Math.max(r1, r0)
            r10 = 1065353216(0x3f800000, float:1.0)
            r11 = 0
            android.view.animation.Interpolator r19 = com.android.launcher3.anim.Interpolators.LINEAR
            r9 = r24
            r15 = r12
            r12 = r18
            r15 = r13
            r13 = r19
            float r8 = com.android.launcher3.Utilities.mapToRange(r8, r9, r10, r11, r12, r13)
            r9 = 1065353216(0x3f800000, float:1.0)
            float r8 = com.android.launcher3.Utilities.boundToRange(r8, r15, r9)
            boolean r9 = r6.mIsVerticalBarLayout
            if (r9 != 0) goto Le0
            boolean r9 = r6.mIsAllowRotationAndLandscape
            if (r9 == 0) goto Ld5
            goto Le0
        Ld5:
            android.graphics.Rect r9 = r6.mOutline
            float r10 = r21.height()
            float r10 = r10 / r2
            int r10 = (int) r10
            r9.bottom = r10
            goto Lea
        Le0:
            android.graphics.Rect r9 = r6.mOutline
            float r10 = r21.width()
            float r10 = r10 / r2
            int r10 = (int) r10
            r9.right = r10
        Lea:
            float r2 = r25 / r2
            r6.mTaskCornerRadius = r2
            boolean r2 = r6.mIsAdaptiveIcon
            if (r2 == 0) goto L205
            android.graphics.drawable.Drawable r2 = r6.mForeground
            if (r2 == 0) goto L205
            android.graphics.drawable.Drawable r2 = r6.mBackground
            if (r2 == 0) goto L205
            com.lge.launcher3.util.LGHomeFeature$Config r2 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION
            boolean r2 = r2.getValue()
            if (r2 == 0) goto L144
            if (r16 != 0) goto L144
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L144
            android.animation.ValueAnimator r0 = r6.mRevealAnimator
            if (r0 != 0) goto L13b
            com.android.launcher3.graphics.IconShape r0 = com.android.launcher3.graphics.IconShape.getShape()
            android.graphics.Rect r2 = r6.mStartRevealRect
            android.graphics.Rect r9 = r6.mOutline
            float r10 = r6.mTaskCornerRadius
            r11 = r16 ^ 1
            r1 = r20
            r12 = r3
            r3 = r9
            r9 = r4
            r4 = r10
            r10 = r5
            r5 = r11
            android.animation.Animator r0 = r0.createRevealAnimator(r1, r2, r3, r4, r5)
            android.animation.ValueAnimator r0 = (android.animation.ValueAnimator) r0
            r6.mRevealAnimator = r0
            com.android.launcher3.views.FloatingIconView$3 r1 = new com.android.launcher3.views.FloatingIconView$3
            r1.<init>()
            r0.addListener(r1)
            android.animation.ValueAnimator r0 = r6.mRevealAnimator
            r0.start()
            android.animation.ValueAnimator r0 = r6.mRevealAnimator
            r0.pause()
            goto L13e
        L13b:
            r12 = r3
            r9 = r4
            r10 = r5
        L13e:
            android.animation.ValueAnimator r0 = r6.mRevealAnimator
            r0.setCurrentFraction(r8)
            goto L147
        L144:
            r12 = r3
            r9 = r4
            r10 = r5
        L147:
            boolean r0 = r6.mIsVerticalBarLayout
            if (r0 != 0) goto L157
            boolean r0 = r6.mIsAllowRotationAndLandscape
            if (r0 == 0) goto L150
            goto L157
        L150:
            android.graphics.Rect r0 = r6.mOutline
            int r0 = r0.height()
            goto L15d
        L157:
            android.graphics.Rect r0 = r6.mOutline
            int r0 = r0.width()
        L15d:
            float r0 = (float) r0
            float r0 = r0 / r17
            r6.setBackgroundDrawableBounds(r0)
            r1 = 1073741824(0x40000000, float:2.0)
            if (r16 == 0) goto L1a2
            android.graphics.Rect r2 = r6.mFinalDrawableBounds
            int r2 = r2.height()
            android.graphics.Rect r3 = r6.mFinalDrawableBounds
            int r3 = r3.width()
            boolean r4 = r6.mIsVerticalBarLayout
            if (r4 != 0) goto L183
            boolean r5 = r6.mIsAllowRotationAndLandscape
            if (r5 == 0) goto L17c
            goto L183
        L17c:
            float r2 = (float) r2
            float r5 = r2 * r0
            float r5 = r5 - r2
            float r5 = r5 / r1
            int r2 = (int) r5
            goto L184
        L183:
            r2 = 0
        L184:
            if (r4 != 0) goto L18d
            boolean r4 = r6.mIsAllowRotationAndLandscape
            if (r4 == 0) goto L18b
            goto L18d
        L18b:
            r15 = 0
            goto L192
        L18d:
            float r3 = (float) r3
            float r0 = r0 * r3
            float r0 = r0 - r3
            float r0 = r0 / r1
            int r15 = (int) r0
        L192:
            android.graphics.Rect r0 = com.android.launcher3.views.FloatingIconView.sTmpRect
            android.graphics.Rect r1 = r6.mFinalDrawableBounds
            r0.set(r1)
            r0.offset(r15, r2)
            android.graphics.drawable.Drawable r1 = r6.mForeground
            r1.setBounds(r0)
            goto L205
        L1a2:
            com.android.launcher3.views.FloatingIconView$Action r0 = com.android.launcher3.views.FloatingIconView.Action.Close
            if (r14 != r0) goto L1db
            com.android.launcher3.Launcher r0 = r6.mLauncher
            com.android.launcher3.DeviceProfile r0 = r0.getDeviceProfile()
            int r0 = r0.availableWidthPx
            float r0 = (float) r0
            float r0 = r0 / r1
            android.graphics.RectF r1 = r6.mPositionOut
            float r1 = r1.centerX()
            int r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
            if (r0 >= 0) goto L1db
            boolean r0 = r6.mIsRtl
            if (r0 == 0) goto L1ce
            float r0 = r7.right
            com.android.launcher3.Launcher r1 = r6.mLauncher
            com.android.launcher3.DeviceProfile r1 = r1.getDeviceProfile()
            int r1 = r1.widthPx
            int r2 = r10.getMarginStart()
            int r1 = r1 - r2
            goto L1d7
        L1ce:
            float r0 = r7.right
            int r1 = r10.getMarginStart()
            int r2 = r10.width
            int r1 = r1 + r2
        L1d7:
            float r1 = (float) r1
            float r0 = r0 - r1
            r4 = r0
            goto L1dc
        L1db:
            r4 = r9
        L1dc:
            com.android.launcher3.Launcher r0 = r6.mLauncher
            com.android.launcher3.DeviceProfile r0 = r0.getDeviceProfile()
            int r0 = r0.availableWidthPx
            float r0 = (float) r0
            float r4 = r4 / r0
            r0 = 1114636288(0x42700000, float:60.0)
            float r4 = r4 * r0
            int r0 = (int) r4
            com.android.launcher3.Launcher r1 = r6.mLauncher
            com.android.launcher3.DeviceProfile r1 = r1.getDeviceProfile()
            int r1 = r1.availableHeightPx
            float r1 = (float) r1
            float r3 = r12 / r1
            r1 = 1117126656(0x42960000, float:75.0)
            float r3 = r3 * r1
            int r1 = (int) r3
            androidx.dynamicanimation.animation.SpringAnimation r2 = r6.mFgSpringX
            float r0 = (float) r0
            r2.animateToFinalPosition(r0)
            androidx.dynamicanimation.animation.SpringAnimation r0 = r6.mFgSpringY
            float r1 = (float) r1
            r0.animateToFinalPosition(r1)
        L205:
            r20.invalidate()
            r20.invalidateOutline()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.views.FloatingIconView.update(android.graphics.RectF, float, float, float, float, com.android.launcher3.views.FloatingIconView$Action):void");
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animator) {
        LGLog.d(TAG, "[FloatingIconAnim] onAnimationEnd");
        CancellationSignal cancellationSignal = this.mLoadIconSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
        Runnable runnable = this.mEndRunnable;
        if (runnable != null) {
            runnable.run();
            return;
        }
        ValueAnimator valueAnimator = this.mRevealAnimator;
        if (valueAnimator != null) {
            valueAnimator.end();
        }
    }

    private void matchPositionOf(Launcher launcher, View v, boolean isOpening, RectF positionOut) {
        float locationBoundsForView = getLocationBoundsForView(launcher, v, isOpening, positionOut);
        InsettableFrameLayout.LayoutParams layoutParams = new InsettableFrameLayout.LayoutParams(Math.round(positionOut.width()), Math.round(positionOut.height()));
        updatePosition(locationBoundsForView, positionOut, layoutParams);
        setLayoutParams(layoutParams);
    }

    private void updatePosition(float rotation, RectF position, InsettableFrameLayout.LayoutParams lp) {
        this.mRotation = rotation;
        this.mPositionOut.set(position);
        lp.ignoreInsets = true;
        lp.topMargin = Math.round(position.top);
        if (this.mIsRtl) {
            lp.setMarginStart(Math.round(this.mLauncher.getDeviceProfile().widthPx - position.right));
        } else {
            lp.setMarginStart(Math.round(position.left));
        }
        lp.width = Math.round(position.width());
        lp.height = Math.round(position.height());
        int marginStart = this.mIsRtl ? (this.mLauncher.getDeviceProfile().widthPx - lp.getMarginStart()) - lp.width : lp.leftMargin;
        layout(marginStart, lp.topMargin, lp.width + marginStart, lp.topMargin + lp.height);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0028 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0029  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static float getLocationBoundsForView(com.android.launcher3.Launcher r11, android.view.View r12, boolean r13, android.graphics.RectF r14) {
        /*
            r0 = 1
            r13 = r13 ^ r0
            boolean r1 = r12 instanceof com.android.launcher3.shortcuts.DeepShortcutView
            r2 = 0
            if (r1 == 0) goto L10
            com.android.launcher3.shortcuts.DeepShortcutView r12 = (com.android.launcher3.shortcuts.DeepShortcutView) r12
            com.android.launcher3.BubbleTextView r12 = r12.getBubbleText()
        Ld:
            r3 = r12
            r7 = r2
            goto L25
        L10:
            android.view.ViewParent r1 = r12.getParent()
            boolean r1 = r1 instanceof com.android.launcher3.shortcuts.DeepShortcutView
            if (r1 == 0) goto L23
            android.view.ViewParent r12 = r12.getParent()
            com.android.launcher3.shortcuts.DeepShortcutView r12 = (com.android.launcher3.shortcuts.DeepShortcutView) r12
            android.view.View r12 = r12.getIconView()
            goto Ld
        L23:
            r3 = r12
            r7 = r13
        L25:
            r12 = 0
            if (r3 != 0) goto L29
            return r12
        L29:
            android.graphics.Rect r13 = new android.graphics.Rect
            r13.<init>()
            boolean r1 = r3 instanceof com.android.launcher3.BubbleTextView
            if (r1 == 0) goto L4d
            r1 = r3
            com.android.launcher3.BubbleTextView r1 = (com.android.launcher3.BubbleTextView) r1
            r1.getIconBounds(r13)
            java.lang.Object r1 = r3.getTag()
            boolean r1 = r1 instanceof com.android.launcher3.model.data.ItemInfo
            if (r1 == 0) goto L63
            java.lang.Object r1 = r3.getTag()
            com.android.launcher3.model.data.ItemInfo r1 = (com.android.launcher3.model.data.ItemInfo) r1
            long r4 = r1.container
            r8 = -101(0xffffffffffffff9b, double:NaN)
            int r1 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            goto L63
        L4d:
            boolean r1 = r3 instanceof com.android.launcher3.folder.FolderIcon
            if (r1 == 0) goto L58
            r1 = r3
            com.android.launcher3.folder.FolderIcon r1 = (com.android.launcher3.folder.FolderIcon) r1
            r1.getPreviewBounds(r13)
            goto L63
        L58:
            int r1 = r3.getWidth()
            int r4 = r3.getHeight()
            r13.set(r2, r2, r1, r4)
        L63:
            r1 = 4
            float[] r1 = new float[r1]
            int r4 = r13.left
            float r4 = (float) r4
            r1[r2] = r4
            int r4 = r13.top
            float r4 = (float) r4
            r1[r0] = r4
            int r4 = r13.right
            float r4 = (float) r4
            r9 = 2
            r1[r9] = r4
            int r13 = r13.bottom
            float r13 = (float) r13
            r10 = 3
            r1[r10] = r13
            float[] r13 = new float[r0]
            r13[r2] = r12
            com.android.launcher3.dragndrop.DragLayer r4 = r11.getDragLayer()
            r6 = 0
            r5 = r1
            r8 = r13
            com.android.launcher3.Utilities.getDescendantCoordRelativeToAncestor(r3, r4, r5, r6, r7, r8)
            r11 = r1[r2]
            r12 = r1[r9]
            float r11 = java.lang.Math.min(r11, r12)
            r12 = r1[r0]
            r3 = r1[r10]
            float r12 = java.lang.Math.min(r12, r3)
            r3 = r1[r2]
            r4 = r1[r9]
            float r3 = java.lang.Math.max(r3, r4)
            r0 = r1[r0]
            r1 = r1[r10]
            float r0 = java.lang.Math.max(r0, r1)
            r14.set(r11, r12, r3, r0)
            r11 = r13[r2]
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.views.FloatingIconView.getLocationBoundsForView(com.android.launcher3.Launcher, android.view.View, boolean, android.graphics.RectF):float");
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x00ab  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00ad  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00b7  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00c2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static void getIconResult(com.android.launcher3.Launcher r14, android.view.View r15, com.android.launcher3.model.data.ItemInfo r16, android.graphics.RectF r17, com.android.launcher3.views.FloatingIconView.IconLoadResult r18) {
        /*
            r0 = r14
            r1 = r15
            r7 = r16
            r8 = r18
            com.android.launcher3.config.FeatureFlags$BooleanFlag r2 = com.android.launcher3.config.FeatureFlags.ADAPTIVE_ICON_WINDOW_ANIM
            boolean r2 = r2.get()
            r9 = 1
            r10 = 0
            if (r2 == 0) goto L18
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 26
            if (r2 < r3) goto L18
            r2 = r9
            goto L19
        L18:
            r2 = r10
        L19:
            com.lge.launcher3.util.LGHomeFeature$Config r3 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION
            boolean r3 = r3.getValue()
            if (r3 != 0) goto L23
            r2 = r2 & 0
        L23:
            boolean r3 = r1 instanceof com.android.launcher3.BubbleTextView
            r11 = 0
            if (r3 == 0) goto L31
            r4 = r1
            com.android.launcher3.BubbleTextView r4 = (com.android.launcher3.BubbleTextView) r4
            android.graphics.drawable.Drawable r4 = r4.getIcon()
            r12 = r4
            goto L32
        L31:
            r12 = r11
        L32:
            boolean r4 = r7 instanceof com.android.launcher3.popup.SystemShortcut
            if (r4 == 0) goto L57
            boolean r2 = r1 instanceof android.widget.ImageView
            if (r2 == 0) goto L43
            android.widget.ImageView r1 = (android.widget.ImageView) r1
            android.graphics.drawable.Drawable r12 = r1.getDrawable()
        L40:
            r2 = r11
            goto La9
        L43:
            boolean r2 = r1 instanceof com.android.launcher3.shortcuts.DeepShortcutView
            if (r2 == 0) goto L52
            com.android.launcher3.shortcuts.DeepShortcutView r1 = (com.android.launcher3.shortcuts.DeepShortcutView) r1
            android.view.View r1 = r1.getIconView()
            android.graphics.drawable.Drawable r12 = r1.getBackground()
            goto L40
        L52:
            android.graphics.drawable.Drawable r12 = r15.getBackground()
            goto L40
        L57:
            boolean r4 = r1 instanceof com.android.launcher3.folder.FolderIcon
            android.graphics.Rect r5 = new android.graphics.Rect
            r5.<init>()
            if (r4 == 0) goto L65
            com.android.launcher3.folder.FolderIcon r1 = (com.android.launcher3.folder.FolderIcon) r1
            r1.getPreviewBounds(r5)
        L65:
            if (r4 == 0) goto L6c
            int r1 = r5.width()
            goto L71
        L6c:
            float r1 = r17.width()
            int r1 = (int) r1
        L71:
            r6 = r1
            if (r4 == 0) goto L79
            int r1 = r5.height()
            goto L7e
        L79:
            float r1 = r17.height()
            int r1 = (int) r1
        L7e:
            r4 = r1
            if (r2 == 0) goto L99
            r5 = 0
            java.lang.Object[] r13 = com.android.launcher3.views.FloatingIconView.sTmpObjArray
            r1 = r14
            r2 = r16
            r3 = r6
            r6 = r13
            android.graphics.drawable.Drawable r1 = com.android.launcher3.Utilities.getFullDrawable(r1, r2, r3, r4, r5, r6)
            boolean r2 = r1 instanceof android.graphics.drawable.AdaptiveIconDrawable
            if (r2 == 0) goto L40
            r2 = r13[r10]
            android.graphics.drawable.Drawable r2 = com.android.launcher3.Utilities.getBadge(r14, r7, r2)
            r12 = r1
            goto La9
        L99:
            if (r3 == 0) goto L9c
            goto L40
        L9c:
            r5 = 0
            java.lang.Object[] r10 = com.android.launcher3.views.FloatingIconView.sTmpObjArray
            r1 = r14
            r2 = r16
            r3 = r6
            r6 = r10
            android.graphics.drawable.Drawable r12 = com.android.launcher3.Utilities.getFullDrawable(r1, r2, r3, r4, r5, r6)
            goto L40
        La9:
            if (r12 != 0) goto Lad
            r1 = r11
            goto Lb1
        Lad:
            android.graphics.drawable.Drawable$ConstantState r1 = r12.getConstantState()
        Lb1:
            if (r1 != 0) goto Lb7
            r3 = r17
            r1 = r11
            goto Lbd
        Lb7:
            android.graphics.drawable.Drawable r1 = r1.newDrawable()
            r3 = r17
        Lbd:
            int r3 = getOffsetForIconBounds(r14, r1, r3)
            monitor-enter(r18)
            r8.drawable = r1     // Catch: java.lang.Throwable -> Ldb
            r8.badge = r2     // Catch: java.lang.Throwable -> Ldb
            r8.iconOffset = r3     // Catch: java.lang.Throwable -> Ldb
            java.lang.Runnable r1 = r8.onIconLoaded     // Catch: java.lang.Throwable -> Ldb
            if (r1 == 0) goto Ld7
            java.util.concurrent.Executor r0 = r14.getMainExecutor()     // Catch: java.lang.Throwable -> Ldb
            java.lang.Runnable r1 = r8.onIconLoaded     // Catch: java.lang.Throwable -> Ldb
            r0.execute(r1)     // Catch: java.lang.Throwable -> Ldb
            r8.onIconLoaded = r11     // Catch: java.lang.Throwable -> Ldb
        Ld7:
            r8.isIconLoaded = r9     // Catch: java.lang.Throwable -> Ldb
            monitor-exit(r18)     // Catch: java.lang.Throwable -> Ldb
            return
        Ldb:
            r0 = move-exception
            monitor-exit(r18)     // Catch: java.lang.Throwable -> Ldb
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.views.FloatingIconView.getIconResult(com.android.launcher3.Launcher, android.view.View, com.android.launcher3.model.data.ItemInfo, android.graphics.RectF, com.android.launcher3.views.FloatingIconView$IconLoadResult):void");
    }

    private void setIcon(View originalView, Drawable drawable, Drawable badge, int iconOffset) {
        int marginStart;
        this.mBadge = badge;
        if (LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION.getValue()) {
            this.mIsAdaptiveIcon = drawable instanceof AdaptiveIconDrawable;
        } else {
            this.mIsAdaptiveIcon = false;
        }
        if (this.mIsAdaptiveIcon) {
            boolean z = drawable instanceof FolderAdaptiveIcon;
            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) drawable;
            Drawable background = adaptiveIconDrawable.getBackground();
            if (background == null) {
                background = new ColorDrawable(0);
            }
            this.mBackground = background;
            Drawable foreground = adaptiveIconDrawable.getForeground();
            if (foreground == null) {
                foreground = new ColorDrawable(0);
            }
            this.mForeground = foreground;
            InsettableFrameLayout.LayoutParams layoutParams = (InsettableFrameLayout.LayoutParams) getLayoutParams();
            int i = layoutParams.height;
            int i2 = layoutParams.width;
            int i3 = this.mBlurSizeOutline / 2;
            this.mFinalDrawableBounds.set(0, 0, i2, i);
            if (!z) {
                int i4 = iconOffset - i3;
                this.mFinalDrawableBounds.inset(i4, i4);
            }
            if (LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION.getValue()) {
                Utilities.scaleRectAboutCenter(this.mFinalDrawableBounds, 0.9599999f);
                Rect rect = new Rect(this.mFinalDrawableBounds);
                Utilities.scaleRectAboutCenter(rect, DragView.ADAPTIVE_ICON_SHRINK_RATIO_FOR_ANTIALIAS);
                adaptiveIconDrawable.setBounds(rect);
                this.mFinalDrawableBounds.inset((int) ((-r8.width()) * AdaptiveIconDrawable.getExtraInsetFraction()), (int) ((-this.mFinalDrawableBounds.height()) * AdaptiveIconDrawable.getExtraInsetFraction()));
            }
            this.mForeground.setBounds(this.mFinalDrawableBounds);
            this.mBackground.setBounds(this.mFinalDrawableBounds);
            this.mStartRevealRect.set(0, 0, i2, i);
            Drawable drawable2 = this.mBadge;
            if (drawable2 != null) {
                drawable2.setBounds(this.mStartRevealRect);
                if (!this.mIsOpening && !z) {
                    LauncherAnimUtils.DRAWABLE_ALPHA.set(this.mBadge, (Integer) 0);
                }
            }
            if (z) {
                Utilities.scaleRectAboutCenter(this.mStartRevealRect, IconShape.getNormalizationScale());
            } else {
                Utilities.scaleRectAboutCenter(this.mStartRevealRect, IconShape.getNormalizationScale());
            }
            float f = this.mLauncher.getDeviceProfile().aspectRatio;
            if (this.mIsVerticalBarLayout || this.mIsAllowRotationAndLandscape) {
                layoutParams.width = (int) Math.max(layoutParams.width, layoutParams.height * f);
            } else {
                layoutParams.height = (int) Math.max(layoutParams.height, layoutParams.width * f);
            }
            if (this.mIsRtl) {
                marginStart = (this.mLauncher.getDeviceProfile().widthPx - layoutParams.getMarginStart()) - layoutParams.width;
            } else {
                marginStart = layoutParams.leftMargin;
            }
            layout(marginStart, layoutParams.topMargin, layoutParams.width + marginStart, layoutParams.topMargin + layoutParams.height);
            float fMax = Math.max(layoutParams.height / i, layoutParams.width / i2);
            if (this.mIsOpening) {
                fMax = 1.0f;
                this.mOutline.set(0, 0, i2, i);
            } else {
                this.mOutline.set(0, 0, layoutParams.width, layoutParams.height);
            }
            setBackgroundDrawableBounds(fMax);
            this.mEndRevealRect.set(0, 0, layoutParams.width, layoutParams.height);
            setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.launcher3.views.FloatingIconView.4
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(FloatingIconView.this.mOutline, FloatingIconView.this.mTaskCornerRadius);
                }
            });
            setClipToOutline(true);
        } else if (getResources().getConfiguration().orientation == 1) {
            setBackground(drawable);
            setClipToOutline(false);
        }
        invalidate();
        invalidateOutline();
    }

    private void checkIconResult(final View originalView, boolean isOpening) {
        final CancellationSignal cancellationSignal = new CancellationSignal();
        IconLoadResult iconLoadResult = this.mIconLoadResult;
        if (iconLoadResult == null) {
            Log.w(TAG, "No icon load result found in checkIconResult");
            return;
        }
        synchronized (iconLoadResult) {
            if (this.mIconLoadResult.isIconLoaded) {
                setIcon(originalView, this.mIconLoadResult.drawable, this.mIconLoadResult.badge, this.mIconLoadResult.iconOffset);
                hideOriginalView(originalView);
            } else {
                this.mIconLoadResult.onIconLoaded = new Runnable() { // from class: com.android.launcher3.views.-$$Lambda$FloatingIconView$l0ycNvuqk-faB6PzjTxZ0X_D2Ao
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$checkIconResult$0$FloatingIconView(cancellationSignal, originalView);
                    }
                };
                this.mLoadIconSignal = cancellationSignal;
            }
        }
    }

    public /* synthetic */ void lambda$checkIconResult$0$FloatingIconView(CancellationSignal cancellationSignal, View view) {
        if (cancellationSignal.isCanceled()) {
            return;
        }
        setIcon(view, this.mIconLoadResult.drawable, this.mIconLoadResult.badge, this.mIconLoadResult.iconOffset);
        setVisibility(0);
        hideOriginalView(view);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    private void hideOriginalView(View originalView) {
        if (originalView instanceof IconLabelDotView) {
            IconLabelDotView iconLabelDotView = (IconLabelDotView) originalView;
            iconLabelDotView.setIconVisible(false);
            iconLabelDotView.setForceHideDot(true);
            return;
        }
        originalView.setVisibility(4);
    }

    private void setBackgroundDrawableBounds(float scale) {
        Rect rect = sTmpRect;
        rect.set(this.mFinalDrawableBounds);
        Utilities.scaleRectAboutCenter(rect, scale);
        if (this.mIsVerticalBarLayout || this.mIsAllowRotationAndLandscape) {
            rect.offsetTo((int) (this.mFinalDrawableBounds.left * scale), rect.top);
        } else {
            rect.offsetTo(rect.left, (int) (this.mFinalDrawableBounds.top * scale));
        }
        this.mBackground.setBounds(rect);
    }

    @Override // com.android.launcher3.views.ClipPathView
    public void setClipPath(Path clipPath) {
        this.mClipPath = clipPath;
        invalidate();
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        int iSave = canvas.save();
        canvas.rotate(this.mRotation, this.mFinalDrawableBounds.exactCenterX(), this.mFinalDrawableBounds.exactCenterY());
        Path path = this.mClipPath;
        if (path != null) {
            canvas.clipPath(path);
        }
        super.draw(canvas);
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        if (this.mForeground != null) {
            int iSave2 = canvas.save();
            canvas.translate(this.mFgTransX, this.mFgTransY);
            this.mForeground.draw(canvas);
            canvas.restoreToCount(iSave2);
        }
        Drawable drawable2 = this.mBadge;
        if (drawable2 != null) {
            drawable2.draw(canvas);
        }
        canvas.restoreToCount(iSave);
    }

    public void setFastFinishRunnable(Runnable runnable) {
        this.mFastFinishRunnable = runnable;
    }

    public void fastFinish() {
        Runnable runnable = this.mFastFinishRunnable;
        if (runnable != null) {
            runnable.run();
            this.mFastFinishRunnable = null;
        }
        CancellationSignal cancellationSignal = this.mLoadIconSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
        Runnable runnable2 = this.mEndRunnable;
        if (runnable2 != null) {
            runnable2.run();
            this.mEndRunnable = null;
        }
        AnimatorSet animatorSet = this.mFadeAnimatorSet;
        if (animatorSet != null) {
            animatorSet.end();
            this.mFadeAnimatorSet = null;
        }
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animator) {
        LGLog.d(TAG, "[FloatingIconAnim] onAnimationStart");
        IconLoadResult iconLoadResult = this.mIconLoadResult;
        if (iconLoadResult != null && iconLoadResult.isIconLoaded) {
            setVisibility(0);
        }
        if (this.mIsOpening) {
            return;
        }
        hideOriginalView(this.mOriginalIcon);
    }

    @Override // android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animator) {
        LGLog.d(TAG, "[FloatingIconAnim] onAnimationCancel");
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        if (!this.mOriginalIcon.isAttachedToWindow() || this.mPositionOut == null) {
            return;
        }
        Launcher launcher = this.mLauncher;
        View view = this.mOriginalIcon;
        boolean z = this.mIsOpening;
        RectF rectF = sTmpRectF;
        float locationBoundsForView = getLocationBoundsForView(launcher, view, z, rectF);
        if (locationBoundsForView == this.mRotation && rectF.equals(this.mPositionOut)) {
            return;
        }
        updatePosition(locationBoundsForView, rectF, (InsettableFrameLayout.LayoutParams) getLayoutParams());
        Runnable runnable = this.mOnTargetChangeRunnable;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setOnTargetChangeListener(Runnable onTargetChangeListener) {
        this.mOnTargetChangeRunnable = onTargetChangeListener;
    }

    public static IconLoadResult fetchIcon(final Launcher l, final View v, final ItemInfo info, final boolean isOpening) {
        final IconLoadResult iconLoadResult = new IconLoadResult();
        new Handler(LauncherModel.getWorkerLooper()).postAtFrontOfQueue(new Runnable() { // from class: com.android.launcher3.views.-$$Lambda$FloatingIconView$ifHV1fZ4pwRdNm_o75IOpy5Z87E
            @Override // java.lang.Runnable
            public final void run() {
                FloatingIconView.lambda$fetchIcon$1(l, v, isOpening, info, iconLoadResult);
            }
        });
        sIconLoadResult = iconLoadResult;
        return iconLoadResult;
    }

    static /* synthetic */ void lambda$fetchIcon$1(Launcher launcher, View view, boolean z, ItemInfo itemInfo, IconLoadResult iconLoadResult) {
        RectF rectF = new RectF();
        getLocationBoundsForView(launcher, view, z, rectF);
        getIconResult(launcher, view, itemInfo, rectF, iconLoadResult);
    }

    public static FloatingIconView getFloatingIconView(Launcher launcher, final View originalView, final boolean hideOriginal, RectF positionOut, Action action) {
        LGLog.d(TAG, "[FloatingIconAnim] getFloatingIconView");
        final DragLayer dragLayer = launcher.getDragLayer();
        ViewGroup viewGroup = (ViewGroup) dragLayer.getParent();
        final FloatingIconView floatingIconView = (FloatingIconView) launcher.getViewCache().getView(R.layout.floating_icon_view, launcher, viewGroup);
        floatingIconView.recycle();
        boolean z = action == Action.Open;
        float f = (LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION.getValue() && action == Action.Close) ? 0.3f : 0.75f;
        floatingIconView.mFgSpringX.getSpring().setDampingRatio(f);
        floatingIconView.mFgSpringY.getSpring().setDampingRatio(f);
        boolean z2 = (originalView.getTag() instanceof ItemInfo) && hideOriginal;
        floatingIconView.mIconLoadResult = originalView instanceof FolderIcon ? null : sIconLoadResult;
        if (z2 && ((ItemInfo) originalView.getTag()).getTargetComponent() != null && STK_PACKAGE_NAME.equals(((ItemInfo) originalView.getTag()).getTargetComponent().getPackageName())) {
            floatingIconView.mIconLoadResult = null;
        }
        if (z2 && floatingIconView.mIconLoadResult == null) {
            floatingIconView.mIconLoadResult = fetchIcon(launcher, originalView, (ItemInfo) originalView.getTag(), z);
        }
        sIconLoadResult = null;
        floatingIconView.mIsVerticalBarLayout = launcher.getDeviceProfile().isVerticalBarLayout();
        floatingIconView.mIsAllowRotationAndLandscape = launcher.getDeviceProfile().isAllowRotationAndLandscape();
        floatingIconView.mIsOpening = z;
        floatingIconView.mOriginalIcon = originalView;
        floatingIconView.mPositionOut = positionOut;
        floatingIconView.matchPositionOf(launcher, originalView, z, positionOut);
        floatingIconView.setVisibility(4);
        viewGroup.addView(floatingIconView);
        dragLayer.addView(floatingIconView.mListenerView);
        ListenerView listenerView = floatingIconView.mListenerView;
        Objects.requireNonNull(floatingIconView);
        listenerView.setListener(new Runnable() { // from class: com.android.launcher3.views.-$$Lambda$yqn3_9JVGwM-rmXKFnMc5V8fBN4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.fastFinish();
            }
        });
        final boolean z3 = z;
        floatingIconView.mEndRunnable = new Runnable() { // from class: com.android.launcher3.views.-$$Lambda$FloatingIconView$4bvdwKM5M369DqkpSsd5Asmhb14
            @Override // java.lang.Runnable
            public final void run() {
                FloatingIconView.lambda$getFloatingIconView$2(this.f$0, hideOriginal, z3, originalView, dragLayer);
            }
        };
        if (z2) {
            floatingIconView.checkIconResult(originalView, z);
        }
        return floatingIconView;
    }

    static /* synthetic */ void lambda$getFloatingIconView$2(FloatingIconView floatingIconView, boolean z, boolean z2, View view, DragLayer dragLayer) {
        LGLog.d(TAG, "[FloatingIconAnim]  mEndRunnable");
        floatingIconView.mEndRunnable = null;
        if (!z) {
            floatingIconView.finish(dragLayer);
            return;
        }
        if (z2) {
            if (view instanceof BubbleTextView) {
                BubbleTextView bubbleTextView = (BubbleTextView) view;
                bubbleTextView.setIconVisible(true);
                bubbleTextView.setForceHideDot(false);
            } else {
                view.setVisibility(0);
            }
            floatingIconView.finish(dragLayer);
            return;
        }
        AnimatorSet animatorSetCreateFadeAnimation = floatingIconView.createFadeAnimation(view, dragLayer);
        floatingIconView.mFadeAnimatorSet = animatorSetCreateFadeAnimation;
        animatorSetCreateFadeAnimation.start();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    private AnimatorSet createFadeAnimation(final View originalView, final DragLayer dragLayer) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200L);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.views.FloatingIconView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                LGLog.d(FloatingIconView.TAG, "[FloatingIconAnim] originalView - onAnimationStart");
                originalView.setVisibility(0);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                LGLog.d(FloatingIconView.TAG, "[FloatingIconAnim] originalView - onAnimationEnd");
                FloatingIconView.this.finish(dragLayer);
            }
        });
        Drawable drawable = this.mBadge;
        if (drawable != null) {
            ObjectAnimator objectAnimatorOfInt = ObjectAnimator.ofInt(drawable, LauncherAnimUtils.DRAWABLE_ALPHA, 255);
            objectAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.views.-$$Lambda$FloatingIconView$8cSWg6DAJTfuZOxqlJy0Dg-7lKg
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$createFadeAnimation$3$FloatingIconView(valueAnimator);
                }
            });
            animatorSet.play(objectAnimatorOfInt);
        }
        if (originalView instanceof IconLabelDotView) {
            final IconLabelDotView iconLabelDotView = (IconLabelDotView) originalView;
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.views.FloatingIconView.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    iconLabelDotView.setIconVisible(true);
                    iconLabelDotView.setForceHideDot(false);
                }
            });
        }
        if (originalView instanceof BubbleTextView) {
            final BubbleTextView bubbleTextView = (BubbleTextView) originalView;
            if (bubbleTextView.getIcon() != null) {
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.views.FloatingIconView.7
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                        bubbleTextView.setIconVisible(true);
                    }
                });
                animatorSet.play(ObjectAnimator.ofInt(bubbleTextView.getIcon(), LauncherAnimUtils.DRAWABLE_ALPHA, 0, 255));
            }
        } else if (!(originalView instanceof FolderIcon)) {
            animatorSet.play(ObjectAnimator.ofFloat(originalView, (Property<View, Float>) ALPHA, 0.0f, 1.0f));
        }
        return animatorSet;
    }

    public /* synthetic */ void lambda$createFadeAnimation$3$FloatingIconView(ValueAnimator valueAnimator) {
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finish(DragLayer dragLayer) {
        ((ViewGroup) dragLayer.getParent()).removeView(this);
        dragLayer.removeView(this.mListenerView);
        recycle();
        this.mLauncher.getViewCache().recycleView(R.layout.floating_icon_view, this);
    }

    private void recycle() {
        setTranslationX(0.0f);
        setTranslationY(0.0f);
        setScaleX(1.0f);
        setScaleY(1.0f);
        setAlpha(1.0f);
        setBackground(null);
        CancellationSignal cancellationSignal = this.mLoadIconSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
        this.mLoadIconSignal = null;
        this.mEndRunnable = null;
        this.mIsAdaptiveIcon = true;
        this.mForeground = null;
        this.mBackground = null;
        this.mClipPath = null;
        this.mFinalDrawableBounds.setEmpty();
        ValueAnimator valueAnimator = this.mRevealAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mRevealAnimator = null;
        AnimatorSet animatorSet = this.mFadeAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.mPositionOut = null;
        this.mFadeAnimatorSet = null;
        this.mListenerView.setListener(null);
        this.mOriginalIcon = null;
        this.mOnTargetChangeRunnable = null;
        this.mTaskCornerRadius = 0.0f;
        this.mOutline.setEmpty();
        this.mFgTransY = 0.0f;
        this.mFgSpringX.cancel();
        this.mFgTransX = 0.0f;
        this.mFgSpringY.cancel();
        this.mBadge = null;
        sTmpObjArray[0] = null;
        this.mIconLoadResult = null;
        this.mFastFinishRunnable = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static class IconLoadResult {
        Drawable badge;
        Drawable drawable;
        int iconOffset;
        boolean isIconLoaded;
        Runnable onIconLoaded;

        private IconLoadResult() {
        }
    }
}
