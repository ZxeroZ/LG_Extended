package com.lge.launcher3.screeneffect;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.lge.launcher3.PagedView;
import com.lge.launcher3.operator.GVNUtils;
import com.lge.launcher3.screeneffect.LauncherScrollerWatcher;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.screeneffect.WorkspaceStateTransitionWatcher;
import com.lge.launcher3.screeneffect.interpolator.ScreenEffectInterpolatorOvershoot;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.MathFunctionUtils;
import com.lge.launcher3.util.OrientationUtils;
import com.lge.launcher3.util.WindowUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectManager {
    public static final boolean DEBUG = false;
    public static final String TAG = "ScreenEffectManager";
    private static ScreenEffectManager sInstance;
    private Context mContext;
    private boolean mIsRtl;
    private ScreenEffectBase mSelectedScreenEffect = null;
    private ScreenEffectConst.ScreenEffectType mSelectedScreenEffectType = null;
    private OverscrollScreenEffectBase mOverscrollScreenEffect = null;
    private WorkspaceStateTransitionWatcher.StateTransitionListener mStateTransitionListener = null;
    private LayoutTransition.TransitionListener mLayoutTransitionListener = null;
    private LauncherScrollerWatcher.ScrollerListener mScrollerListener = null;
    private boolean mIsEnabled = true;
    private boolean mIsEnabledToSwitchInterpolator = false;
    private Interpolator mNativeDefaultScrollInterpolator = null;
    private Interpolator mOverscrollOvershootInterpolator = null;

    private float overScrollInfluenceCurve(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2) + 1.0f;
    }

    public static void showChildBounds(Canvas canvas, View child, int color, int stroke, boolean show) {
        if (!show) {
        }
    }

    public static ScreenEffectManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ScreenEffectManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private ScreenEffectManager(Context context) {
        this.mContext = null;
        this.mIsRtl = false;
        LGLog.i(TAG, "Create a new ScreenEffectManager instance.");
        this.mContext = context;
        updateSelectedScreenEffectType();
        this.mIsRtl = Utilities.isRtl(context.getResources());
        enableStateTransitionListener(true);
        enableLayoutTransitionListener(true);
        enableScrollerListener(true);
    }

    public void updateSelectedScreenEffectType() {
        ScreenEffectConst.ScreenEffectType selectedScreenEffectType = ScreenEffectUtils.getSelectedScreenEffectType(this.mContext);
        if (this.mSelectedScreenEffectType == selectedScreenEffectType) {
            return;
        }
        changeScreenEffectType(selectedScreenEffectType);
    }

    private void changeScreenEffectType(ScreenEffectConst.ScreenEffectType type) {
        this.mSelectedScreenEffectType = type;
        int i = AnonymousClass4.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType[type.ordinal()];
        if (i == 1) {
            this.mSelectedScreenEffect = new ScreenEffectBreeze(this.mContext);
        } else if (i == 2) {
            this.mSelectedScreenEffect = new ScreenEffectPanorama(this.mContext);
        } else if (i == 3) {
            if (GVNUtils.isGiovanna(this.mContext)) {
                this.mSelectedScreenEffect = new ScreenEffectCarouselGVN(this.mContext);
            } else {
                this.mSelectedScreenEffect = new ScreenEffectCarousel(this.mContext);
            }
        } else {
            this.mSelectedScreenEffect = new ScreenEffectSlide(this.mContext);
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_OVERSCROLL_SLIDE_SCREEN_EFFECT.getValue()) {
            this.mOverscrollScreenEffect = new OverscrollScreenEffectSlide(this.mContext);
        } else {
            this.mOverscrollScreenEffect = new OverscrollScreenEffectSpring(this.mContext);
        }
    }

    public ScreenEffectBase getSlideScreenEffect() {
        return new ScreenEffectSlide(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEnabled(boolean enabled) {
        this.mIsEnabled = enabled;
    }

    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean zDrawChild = false;
        if (!this.mIsEnabled) {
            ScreenEffectTargetManager.getInstance(this.mContext).getTargetInfo(child);
            return false;
        }
        ScreenEffectBase screenEffectForChild = getScreenEffectForChild(child, this.mSelectedScreenEffect);
        LoopNormalModeManager loopNormalModeManager = LoopNormalModeManager.getInstance(this.mContext);
        if (loopNormalModeManager.isEnabled((PagedView) child.getParent())) {
            loopNormalModeManager.translateCanvasForLoop(canvas, child);
        }
        int i = AnonymousClass4.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$DrawState[getDrawState(child, screenEffectForChild.isOverscrollHandledBySelf()).ordinal()];
        if (i == 1) {
            zDrawChild = screenEffectForChild.drawChild(canvas, child, drawingTime);
        } else if (i == 2) {
            zDrawChild = this.mOverscrollScreenEffect.drawChild(canvas, child, drawingTime);
        } else if (i == 3) {
            zDrawChild = true;
        }
        showChildBounds(canvas, child, -16776961, 5, zDrawChild);
        return zDrawChild;
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:23:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private com.lge.launcher3.screeneffect.ScreenEffectBase getScreenEffectForChild(android.view.View r5, com.lge.launcher3.screeneffect.ScreenEffectBase r6) {
        /*
            r4 = this;
            android.content.Context r0 = r4.mContext
            com.lge.launcher3.screeneffect.ScreenEffectTargetManager r0 = com.lge.launcher3.screeneffect.ScreenEffectTargetManager.getInstance(r0)
            com.lge.launcher3.screeneffect.ScreenEffectBase r1 = r4.getCustomScreenEffect(r5)
            if (r1 == 0) goto Ld
            return r1
        Ld:
            r1 = -1
            int[] r2 = com.lge.launcher3.screeneffect.ScreenEffectManager.AnonymousClass4.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw
            com.lge.launcher3.screeneffect.ScreenEffectTargetManager$TargetInfo r3 = r0.getTargetInfo(r5)
            com.lge.launcher3.screeneffect.ScreenEffectConst$WhichPageToDraw r3 = r3.whichPageToDraw
            int r3 = r3.ordinal()
            r2 = r2[r3]
            r3 = 1
            if (r2 == r3) goto L32
            r3 = 2
            if (r2 == r3) goto L23
            goto L40
        L23:
            boolean r1 = r4.mIsRtl
            if (r1 != 0) goto L2c
            int r5 = r0.indexOfPrevChild(r5)
            goto L30
        L2c:
            int r5 = r0.indexOfNextChild(r5)
        L30:
            r1 = r5
            goto L40
        L32:
            boolean r1 = r4.mIsRtl
            if (r1 != 0) goto L3b
            int r5 = r0.indexOfNextChild(r5)
            goto L30
        L3b:
            int r5 = r0.indexOfPrevChild(r5)
            goto L30
        L40:
            android.view.View r5 = r0.getChildAt(r1)
            com.lge.launcher3.screeneffect.ScreenEffectBase r5 = r4.getCustomScreenEffect(r5)
            if (r5 == 0) goto L4b
            r6 = r5
        L4b:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.screeneffect.ScreenEffectManager.getScreenEffectForChild(android.view.View, com.lge.launcher3.screeneffect.ScreenEffectBase):com.lge.launcher3.screeneffect.ScreenEffectBase");
    }

    /* JADX INFO: renamed from: com.lge.launcher3.screeneffect.ScreenEffectManager$4, reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$DrawState;
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType;
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw;

        static {
            int[] iArr = new int[ScreenEffectConst.WhichPageToDraw.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw = iArr;
            try {
                iArr[ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NONE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            int[] iArr2 = new int[ScreenEffectConst.DrawState.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$DrawState = iArr2;
            try {
                iArr2[ScreenEffectConst.DrawState.NORMAL_SCREEN_EFFECT.ordinal()] = 1;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$DrawState[ScreenEffectConst.DrawState.OVERSCROLL_SCREEN_EFFECT.ordinal()] = 2;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$DrawState[ScreenEffectConst.DrawState.SKIP.ordinal()] = 3;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$DrawState[ScreenEffectConst.DrawState.VIEW_SELF.ordinal()] = 4;
            } catch (NoSuchFieldError unused9) {
            }
            int[] iArr3 = new int[ScreenEffectConst.ScreenEffectType.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType = iArr3;
            try {
                iArr3[ScreenEffectConst.ScreenEffectType.BREEZE.ordinal()] = 1;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType[ScreenEffectConst.ScreenEffectType.PANORAMA.ordinal()] = 2;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType[ScreenEffectConst.ScreenEffectType.CAROUSEL.ordinal()] = 3;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScreenEffectType[ScreenEffectConst.ScreenEffectType.SLIDE.ordinal()] = 4;
            } catch (NoSuchFieldError unused13) {
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    private ScreenEffectBase getCustomScreenEffect(View child) {
        if (child instanceof IScreenEffectable) {
            return ((IScreenEffectable) child).getCustomScreenEffect();
        }
        return null;
    }

    public ScreenEffectConst.DrawState getDrawState(View child, boolean overscrollHandledBySelf) {
        ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
        ScreenEffectConst.WhichPageToDraw whichPageToDraw = screenEffectTargetManager.getTargetInfo(child).whichPageToDraw;
        ScreenEffectConst.DrawState drawState = ScreenEffectConst.DrawState.VIEW_SELF;
        int i = AnonymousClass4.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[whichPageToDraw.ordinal()];
        if (i == 1 || i == 2) {
            return ScreenEffectConst.DrawState.NORMAL_SCREEN_EFFECT;
        }
        if (i == 3 || i == 4) {
            ScreenEffectConst.DrawState drawState2 = ScreenEffectConst.DrawState.OVERSCROLL_SCREEN_EFFECT;
            boolean z = whichPageToDraw == ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT;
            boolean z2 = whichPageToDraw == ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT;
            boolean zIsHeadPage = screenEffectTargetManager.isHeadPage(child);
            boolean zIsTailPage = screenEffectTargetManager.isTailPage(child);
            return (overscrollHandledBySelf || LoopNormalModeManager.getInstance(this.mContext).isEnabled((PagedView) child.getParent())) ? ScreenEffectConst.DrawState.NORMAL_SCREEN_EFFECT : ((!z || (this.mIsRtl ? zIsTailPage : zIsHeadPage)) && (!z2 || (this.mIsRtl ? zIsHeadPage : zIsTailPage))) ? drawState2 : ScreenEffectConst.DrawState.SKIP;
        }
        return ScreenEffectConst.DrawState.VIEW_SELF;
    }

    private void enableStateTransitionListener(boolean enable) {
        if (this.mStateTransitionListener == null) {
            this.mStateTransitionListener = getStateTransitionListener();
        }
        WorkspaceStateTransitionWatcher workspaceStateTransitionWatcher = WorkspaceStateTransitionWatcher.getInstance(this.mContext);
        if (enable) {
            workspaceStateTransitionWatcher.addListener(this.mStateTransitionListener);
        } else {
            workspaceStateTransitionWatcher.removeListener(this.mStateTransitionListener);
        }
    }

    private WorkspaceStateTransitionWatcher.StateTransitionListener getStateTransitionListener() {
        return new WorkspaceStateTransitionWatcher.StateTransitionListener() { // from class: com.lge.launcher3.screeneffect.ScreenEffectManager.1
            @Override // com.lge.launcher3.screeneffect.WorkspaceStateTransitionWatcher.StateTransitionListener
            public void onStateTransitionStart(Workspace.State fromState, Workspace.State toState) {
                if (toState == Workspace.State.NORMAL) {
                    return;
                }
                ScreenEffectManager.this.setEnabled(false);
                ScreenEffectTargetManager.getInstance(ScreenEffectManager.this.mContext).setDefaultInterpolator(ScreenEffectManager.this.mNativeDefaultScrollInterpolator);
                ScreenEffectManager.this.enableLayoutTransitionListener(false);
                ScreenEffectManager.this.enableScrollerListener(false);
            }

            @Override // com.lge.launcher3.screeneffect.WorkspaceStateTransitionWatcher.StateTransitionListener
            public void onStateTransitionEnd(Workspace.State fromState, Workspace.State toState) {
                if (toState != Workspace.State.NORMAL) {
                    return;
                }
                ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(ScreenEffectManager.this.mContext);
                LayoutTransition layoutTransition = screenEffectTargetManager.getLayoutTransition();
                if (layoutTransition != null && !layoutTransition.isRunning()) {
                    ScreenEffectManager.this.setEnabled(true);
                }
                screenEffectTargetManager.setDefaultInterpolator(ScreenEffectManager.this.mSelectedScreenEffect.getInterpolator());
                ScreenEffectManager.this.enableLayoutTransitionListener(true);
                ScreenEffectManager.this.enableScrollerListener(true);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableLayoutTransitionListener(boolean enable) {
        LayoutTransition layoutTransition = ScreenEffectTargetManager.getInstance(this.mContext).getLayoutTransition();
        if (layoutTransition == null) {
            return;
        }
        if (this.mLayoutTransitionListener == null) {
            this.mLayoutTransitionListener = getLayoutTransitionListener();
        }
        if (enable) {
            layoutTransition.addTransitionListener(this.mLayoutTransitionListener);
        } else {
            layoutTransition.removeTransitionListener(this.mLayoutTransitionListener);
        }
    }

    private LayoutTransition.TransitionListener getLayoutTransitionListener() {
        return new LayoutTransition.TransitionListener() { // from class: com.lge.launcher3.screeneffect.ScreenEffectManager.2
            @Override // android.animation.LayoutTransition.TransitionListener
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                ScreenEffectManager.this.setEnabled(false);
                LoopNormalModeManager.getInstance(ScreenEffectManager.this.mContext).setEnabled(false);
            }

            @Override // android.animation.LayoutTransition.TransitionListener
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                if (transition.isRunning()) {
                    return;
                }
                ScreenEffectManager.this.setEnabled(true);
                LoopNormalModeManager.getInstance(ScreenEffectManager.this.mContext).setEnabled(true);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableScrollerListener(boolean enable) {
        if (this.mScrollerListener == null) {
            this.mScrollerListener = getScrollerListener();
        }
        LauncherScrollerWatcher launcherScrollerWatcher = LauncherScrollerWatcher.getInstance();
        if (enable) {
            launcherScrollerWatcher.addListener(this.mScrollerListener);
        } else {
            launcherScrollerWatcher.removeListener(this.mScrollerListener);
        }
    }

    private LauncherScrollerWatcher.ScrollerListener getScrollerListener() {
        return new LauncherScrollerWatcher.ScrollerListener() { // from class: com.lge.launcher3.screeneffect.ScreenEffectManager.3
            @Override // com.lge.launcher3.screeneffect.LauncherScrollerWatcher.ScrollerListener
            public void onScrollerStart(int startX, int startY) {
                Interpolator interpolator;
                if (ScreenEffectManager.this.mSelectedScreenEffect == null || ScreenEffectManager.this.mOverscrollScreenEffect == null) {
                    return;
                }
                ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(ScreenEffectManager.this.mContext);
                if (!ScreenEffectManager.this.mIsEnabledToSwitchInterpolator) {
                    screenEffectTargetManager.setDefaultInterpolator(ScreenEffectManager.this.mNativeDefaultScrollInterpolator);
                    return;
                }
                boolean z = screenEffectTargetManager.isOverscrollLeft() || screenEffectTargetManager.isOverscrollRight();
                boolean zIsOverscrollHandledBySelf = ScreenEffectManager.this.mSelectedScreenEffect.isOverscrollHandledBySelf();
                boolean zIsEnabled = LoopNormalModeManager.getInstance(ScreenEffectManager.this.mContext).isEnabled((PagedView) screenEffectTargetManager.getParent());
                if (!z || zIsOverscrollHandledBySelf || zIsEnabled) {
                    interpolator = ScreenEffectManager.this.mSelectedScreenEffect.getInterpolator();
                } else {
                    interpolator = ScreenEffectManager.this.mOverscrollScreenEffect.getInterpolator();
                }
                screenEffectTargetManager.setDefaultInterpolator(interpolator);
            }

            @Override // com.lge.launcher3.screeneffect.LauncherScrollerWatcher.ScrollerListener
            public void onScrollerFinish(int currX, int currY, LauncherScrollerWatcher.ScrollerFinishType finishType) {
                if (ScreenEffectManager.this.mSelectedScreenEffect == null) {
                    return;
                }
                ScreenEffectManager.this.mIsEnabledToSwitchInterpolator = false;
                ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(ScreenEffectManager.this.mContext);
                screenEffectTargetManager.setDefaultInterpolator(ScreenEffectManager.this.mSelectedScreenEffect.getInterpolator());
                if (finishType == LauncherScrollerWatcher.ScrollerFinishType.TIME_EXPIRATION && OrientationUtils.isPortrait(ScreenEffectManager.this.mContext) && !Utilities.ATLEAST_P) {
                    screenEffectTargetManager.updateAllPagesToOpaque();
                }
            }
        };
    }

    public void setNativeDefaultScrollInterpolator(Interpolator interpolator) {
        this.mNativeDefaultScrollInterpolator = interpolator;
    }

    public int adjustMinSnapVelocity(Interpolator interpolator, int defValue) {
        return !isOvershootInterpolator(interpolator) ? defValue : (int) (WindowUtils.getDensity(this.mContext) * 1000.0f);
    }

    public int adjustSnapDuration(Interpolator interpolator, int defValue) {
        return !isOvershootInterpolator(interpolator) ? defValue : Math.max(300, defValue);
    }

    public void enableToSwitchInterpolator() {
        this.mIsEnabledToSwitchInterpolator = true;
    }

    public void updateInterpolatorTension(Interpolator interpolator, int velocity, int duration, boolean isSkip) {
        if (isOvershootInterpolator(interpolator) && isSkip) {
            ((ScreenEffectInterpolatorOvershoot) interpolator).computeTension(0.0f);
        } else {
            updateInterpolatorTension(interpolator, velocity, duration);
        }
    }

    public void updateInterpolatorTension(Interpolator interpolator, int velocity, int duration) {
        float fNormalize;
        if (isOvershootInterpolator(interpolator)) {
            float density = WindowUtils.getDensity(this.mContext) * 1000.0f;
            int i = (int) (2.0f * density);
            int i2 = (int) (density * 3.0f);
            if (velocity < i) {
                fNormalize = 0.0f;
            } else {
                fNormalize = i2 <= velocity ? 1.0f : MathFunctionUtils.normalize(velocity, i, i2);
            }
            ((ScreenEffectInterpolatorOvershoot) interpolator).computeTension(fNormalize);
        }
    }

    private boolean isOvershootInterpolator(Interpolator interpolator) {
        return interpolator instanceof ScreenEffectInterpolatorOvershoot;
    }

    public int getOverscrollSnapAnimationDuration() {
        if (ScreenEffectTargetManager.getInstance(this.mContext).isOverscrollLeft() || ScreenEffectTargetManager.getInstance(this.mContext).isOverscrollRight()) {
            return 500;
        }
        return ScreenEffectConst.PAGE_OVERSCROLL_SNAP_ANIMATION_DURATION;
    }

    public int getDampedOverscrollAmount(float amount, int screenSize) {
        float f = screenSize;
        float f2 = amount / f;
        if (MathFunctionUtils.equals(f2, 0.0f)) {
            return (int) amount;
        }
        float fAbs = (f2 / Math.abs(f2)) * overScrollInfluenceCurve(Math.abs(f2));
        if (Math.abs(fAbs) >= 1.0f) {
            fAbs /= Math.abs(fAbs);
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_OVERSCROLL_SLIDE_SCREEN_EFFECT.getValue()) {
            return Math.round(fAbs * 0.21f * f);
        }
        return Math.round(fAbs * 0.07f * f);
    }

    private void removeAllListeners() {
        if (this.mStateTransitionListener != null) {
            enableStateTransitionListener(false);
            this.mStateTransitionListener = null;
        }
        if (this.mLayoutTransitionListener != null) {
            enableLayoutTransitionListener(false);
            this.mLayoutTransitionListener = null;
        }
        if (this.mScrollerListener != null) {
            enableScrollerListener(false);
            this.mScrollerListener = null;
        }
    }

    public void destroy() {
        LGLog.i(TAG, "Destroy ScreenEffectManager instance.");
        removeAllListeners();
        this.mNativeDefaultScrollInterpolator = null;
        this.mSelectedScreenEffect = null;
        this.mOverscrollScreenEffect = null;
        this.mContext = null;
        sInstance = null;
    }
}
