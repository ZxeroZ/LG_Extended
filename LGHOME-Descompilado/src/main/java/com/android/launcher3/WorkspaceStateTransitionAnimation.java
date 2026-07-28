package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.anim.SpringAnimationBuilder;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.graphics.WorkspaceAndHotseatScrim;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.DynamicResource;
import com.android.systemui.plugins.ResourceProvider;
import com.lge.launcher3.R;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.wallpaperblur.HomescreenBlurManager;
import com.lge.launcher3.wallpaperblur.WidgetBlurManager;
import com.lge.launcher3.wing.CarouselLayout;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class WorkspaceStateTransitionAnimation {
    static final int BACKGROUND_FADE_OUT_DURATION = 350;
    private static final boolean DEBUG_INDICATOR = false;
    public static final int SCROLL_TO_CURRENT_PAGE = -1;
    public static final String TAG = "WorkspaceStateTransitionAnimation";
    int mAllAppsTransitionTime;
    float mCurrentScale;
    final Launcher mLauncher;
    float[] mNewAlphas;
    float[] mNewBackgroundAlphas;
    float mNewScale;
    float[] mOldAlphas;
    float[] mOldBackgroundAlphas;
    int mOverlayTransitionTime;
    float mOverviewModeShrinkFactor;
    int mOverviewTransitionTime;
    private float mPanelAlpha;
    float mSpringLoadedShrinkFactor;
    AnimatorSet mStateAnimator;
    final Workspace mWorkspace;
    boolean mWorkspaceFadeInAdjacentScreens;
    float mWorkspaceScrimAlpha;
    int mLastChildCount = -1;
    final ZoomInInterpolator mZoomInInterpolator = new ZoomInInterpolator();

    public WorkspaceStateTransitionAnimation(Launcher launcher, Workspace workspace) {
        this.mLauncher = launcher;
        this.mWorkspace = workspace;
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        Resources resources = launcher.getResources();
        this.mAllAppsTransitionTime = resources.getInteger(R.integer.config_allAppsTransitionTime);
        this.mOverviewTransitionTime = resources.getInteger(R.integer.config_overviewTransitionTime);
        this.mOverlayTransitionTime = resources.getInteger(R.integer.config_overlayTransitionTime);
        this.mSpringLoadedShrinkFactor = deviceProfile.workspaceSpringLoadShrinkFactor;
        this.mWorkspaceScrimAlpha = resources.getInteger(R.integer.config_workspaceScrimAlpha) / 100.0f;
        this.mOverviewModeShrinkFactor = this.mSpringLoadedShrinkFactor;
        this.mWorkspaceFadeInAdjacentScreens = deviceProfile.shouldFadeAdjacentWorkspaceScreens();
    }

    public AnimatorSet getAnimationToState(Workspace.State fromState, Workspace.State toState, int toPage, boolean animated, boolean hasOverlaySearchBar, HashMap<View, Integer> layerViews) {
        boolean zIsEnabled = ((AccessibilityManager) this.mLauncher.getSystemService("accessibility")).isEnabled();
        TransitionStates transitionStates = new TransitionStates(fromState, toState);
        int animationDuration = getAnimationDuration(transitionStates);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            animateCarouselLayout(transitionStates, toPage, animated, animationDuration, layerViews, zIsEnabled);
            PageIndicator pageIndicator = this.mWorkspace.getPageIndicator();
            if (pageIndicator != null) {
                pageIndicator.setTranslationY(getPageIndicatorTranslationY(transitionStates));
            }
            animateDynamicPanel(transitionStates);
        } else {
            animateWorkspace(transitionStates, toPage, animated, animationDuration, layerViews, zIsEnabled);
            animateSearchBar(transitionStates, animated, animationDuration, hasOverlaySearchBar, layerViews, zIsEnabled);
        }
        animateBackgroundGradient(transitionStates, animated, 350);
        HomescreenBlurManager homescreenBlurManager = HomescreenBlurManager.getInstance(this.mLauncher);
        if (this.mStateAnimator != null) {
            this.mStateAnimator.addListener(homescreenBlurManager.getWorkspaceStateAnimationListener(fromState, toState, this.mOverlayTransitionTime));
        } else if (toState == Workspace.State.NORMAL && this.mLauncher.getWorkspace().getOpenFolder() == null) {
            homescreenBlurManager.clearBackground();
        }
        if (this.mStateAnimator != null) {
            this.mStateAnimator.addListener(WidgetBlurManager.getInstance(this.mLauncher).getWorkspaceStateAnimationListener(fromState, toState));
        }
        return this.mStateAnimator;
    }

    public void setState(LauncherState toState) {
        setWorkspaceProperty(toState, PropertySetter.NO_ANIM_PROPERTY_SETTER, new StateAnimationConfig());
    }

    public float getFinalScale() {
        return this.mNewScale;
    }

    private void reinitializeAnimationArrays() {
        int childCount = this.mWorkspace.getChildCount();
        if (this.mLastChildCount == childCount) {
            return;
        }
        this.mOldBackgroundAlphas = new float[childCount];
        this.mOldAlphas = new float[childCount];
        this.mNewBackgroundAlphas = new float[childCount];
        this.mNewAlphas = new float[childCount];
    }

    private int getAnimationDuration(TransitionStates states) {
        if (states.workspaceToAllApps || states.overviewToAllApps) {
            return this.mAllAppsTransitionTime;
        }
        if (states.workspaceToOverview || states.overviewToWorkspace) {
            return this.mOverviewTransitionTime;
        }
        return this.mOverlayTransitionTime;
    }

    private void animateWorkspace(final TransitionStates states, int toPage, final boolean animated, final int duration, final HashMap<View, Integer> layerViews, final boolean accessibilityEnabled) {
        int i;
        float f;
        reinitializeAnimationArrays();
        cancelAnimation();
        if (animated) {
            this.mStateAnimator = LauncherAnimUtils.createAnimatorSet();
        }
        float f2 = 1.0f;
        float f3 = (states.stateIsSpringLoaded || states.stateIsOverview) ? 1.0f : 0.0f;
        float f4 = (states.stateIsNormal || states.stateIsSpringLoaded) ? 1.0f : 0.0f;
        float f5 = states.stateIsOverview ? 1.0f : 0.0f;
        float overviewModeTranslationY = (states.stateIsOverview || states.stateIsOverviewHidden) ? this.mWorkspace.getOverviewModeTranslationY() : 0.0f;
        if (this.mLauncher.getDeviceProfile().isLandscape && this.mLauncher.getDeviceProfile().allowRotation && states.stateIsSpringLoaded) {
            overviewModeTranslationY = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.device_profile_springloaded_workspace_translation_y);
        }
        if (states.stateIsNormalHidden) {
            overviewModeTranslationY = -this.mLauncher.getResources().getInteger(R.integer.config_swipeup_transitiony);
        }
        int childCount = this.mWorkspace.getChildCount();
        int iNumCustomPages = this.mWorkspace.numCustomPages();
        this.mNewScale = 1.0f;
        if (states.oldStateIsOverview) {
            this.mWorkspace.disableFreeScroll();
        } else {
            boolean z = states.stateIsOverview;
        }
        if (!states.stateIsNormal) {
            if (states.stateIsSpringLoaded) {
                this.mNewScale = this.mLauncher.getDeviceProfile().workspaceSpringLoadShrinkFactor;
            } else if (states.stateIsOverview || states.stateIsOverviewHidden) {
                this.mNewScale = this.mLauncher.getDeviceProfile().overviewModeScaleFactor;
            }
        }
        int pageNearestToCenterOfScreen = toPage == -1 ? this.mWorkspace.getPageNearestToCenterOfScreen() : toPage;
        this.mWorkspace.snapToPage(pageNearestToCenterOfScreen, duration, this.mZoomInInterpolator);
        int i2 = 0;
        while (true) {
            if (i2 >= childCount) {
                break;
            }
            CellLayout cellLayout = (CellLayout) this.mWorkspace.getChildAt(i2);
            boolean z2 = i2 == pageNearestToCenterOfScreen;
            float alpha = cellLayout.getShortcutsAndWidgets().getAlpha();
            float f6 = (states.stateIsNormalHidden || states.stateIsOverviewHidden || (states.stateIsNormal && this.mWorkspaceFadeInAdjacentScreens && i2 != pageNearestToCenterOfScreen && i2 >= iNumCustomPages)) ? 0.0f : f2;
            if (!this.mWorkspace.isSwitchingState() && (states.workspaceToAllApps || states.allAppsToWorkspace)) {
                if (states.allAppsToWorkspace && z2) {
                    f = 0.0f;
                } else if (z2) {
                    f = alpha;
                } else {
                    f = 0.0f;
                    f6 = 0.0f;
                }
                cellLayout.setShortcutAndWidgetAlpha(f);
                alpha = f;
            }
            this.mOldAlphas[i2] = alpha;
            this.mNewAlphas[i2] = f6;
            if (animated) {
                this.mOldBackgroundAlphas[i2] = cellLayout.getBackgroundAlpha();
                this.mNewBackgroundAlphas[i2] = f3;
            } else {
                cellLayout.setBackgroundAlpha(f3);
                cellLayout.setShortcutAndWidgetAlpha(f6);
            }
            i2++;
            f2 = 1.0f;
        }
        final ViewGroup lGOverviewPanel = this.mLauncher.getLGOverviewPanel();
        View layout = this.mLauncher.getHotseat().getLayout();
        PageIndicator pageIndicator = this.mWorkspace.getPageIndicator();
        if (animated) {
            LauncherViewPropertyAnimator launcherViewPropertyAnimator = new LauncherViewPropertyAnimator(this.mWorkspace);
            long j = duration;
            launcherViewPropertyAnimator.scaleX(this.mNewScale).scaleY(this.mNewScale).translationY(overviewModeTranslationY).setDuration(j).setInterpolator(this.mZoomInInterpolator);
            boolean z3 = states.stateIsNormalHidden;
            if (states.stateIsNormal && this.mWorkspace.getCheckAppDrawerAnimationFinished().booleanValue()) {
                launcherViewPropertyAnimator.setStartDelay(50L);
            }
            this.mStateAnimator.play(launcherViewPropertyAnimator);
            int i3 = 0;
            while (i3 < childCount) {
                CellLayout cellLayout2 = (CellLayout) this.mWorkspace.getChildAt(i3);
                float alpha2 = cellLayout2.getShortcutsAndWidgets().getAlpha();
                if (com.lge.launcher3.util.Utilities.isLGUI7_1()) {
                    LauncherViewPropertyAnimator launcherViewPropertyAnimator2 = new LauncherViewPropertyAnimator(cellLayout2.getDefaultHomeLayout());
                    i = childCount;
                    launcherViewPropertyAnimator2.alpha(this.mNewScale == 1.0f ? 0.0f : 1.0f).setDuration(j).setInterpolator(this.mZoomInInterpolator);
                    this.mStateAnimator.play(launcherViewPropertyAnimator2);
                } else {
                    i = childCount;
                }
                if (this.mOldAlphas[i3] == 0.0f && this.mNewAlphas[i3] == 0.0f) {
                    cellLayout2.setBackgroundAlpha(this.mNewBackgroundAlphas[i3]);
                    cellLayout2.setShortcutAndWidgetAlpha(this.mNewAlphas[i3]);
                } else {
                    if (layerViews != null) {
                        layerViews.put(cellLayout2, 0);
                    }
                    float f7 = this.mOldAlphas[i3];
                    float[] fArr = this.mNewAlphas;
                    if (f7 != fArr[i3] || alpha2 != fArr[i3]) {
                        LauncherViewPropertyAnimator launcherViewPropertyAnimator3 = new LauncherViewPropertyAnimator(cellLayout2.getShortcutsAndWidgets());
                        launcherViewPropertyAnimator3.alpha(this.mNewAlphas[i3]).setDuration(j).setInterpolator(this.mZoomInInterpolator);
                        this.mStateAnimator.play(launcherViewPropertyAnimator3);
                    }
                    float[] fArr2 = this.mOldBackgroundAlphas;
                    if (fArr2[i3] != 0.0f || this.mNewBackgroundAlphas[i3] != 0.0f) {
                        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(cellLayout2, "backgroundAlpha", fArr2[i3], this.mNewBackgroundAlphas[i3]);
                        LauncherAnimUtils.ofFloat(cellLayout2, 0.0f, 1.0f);
                        objectAnimatorOfFloat.setInterpolator(this.mZoomInInterpolator);
                        objectAnimatorOfFloat.setDuration(j);
                        this.mStateAnimator.play(objectAnimatorOfFloat);
                    }
                }
                i3++;
                childCount = i;
            }
            if (pageIndicator != null) {
                pageIndicator.animate().translationY(getPageIndicatorTranslationY(states)).setListener(null).setDuration(j).withLayer().start();
            }
            LauncherViewPropertyAnimator launcherViewPropertyAnimatorAlpha = new LauncherViewPropertyAnimator(layout).alpha(f4);
            launcherViewPropertyAnimatorAlpha.addListener(new AlphaUpdateListener(layout, accessibilityEnabled));
            LauncherViewPropertyAnimator launcherViewPropertyAnimatorAlpha2 = new LauncherViewPropertyAnimator(lGOverviewPanel).alpha(f5);
            launcherViewPropertyAnimatorAlpha2.addListener(new AlphaUpdateListener(lGOverviewPanel, accessibilityEnabled));
            layout.setLayerType(2, null);
            lGOverviewPanel.setLayerType(2, null);
            if (layerViews != null) {
                layerViews.put(layout, 1);
                layerViews.put(lGOverviewPanel, 1);
            } else {
                launcherViewPropertyAnimatorAlpha.withLayer();
                launcherViewPropertyAnimatorAlpha2.withLayer();
            }
            if (states.workspaceToOverview) {
                launcherViewPropertyAnimatorAlpha.setInterpolator(new DecelerateInterpolator(2.0f));
                launcherViewPropertyAnimatorAlpha2.setInterpolator(null);
            } else if (states.overviewToWorkspace) {
                launcherViewPropertyAnimatorAlpha.setInterpolator(null);
                launcherViewPropertyAnimatorAlpha2.setInterpolator(new DecelerateInterpolator(2.0f));
                setVisiblePageIndicator(pageIndicator);
            } else if (states.oldStateIsOverviewHidden && states.stateIsSpringLoaded) {
                UninstallModeManager.getInstance(this.mLauncher).exitUninstallMode(this.mLauncher);
                if (pageIndicator != null) {
                    pageIndicator.setVisibility(0);
                }
            } else if (pageIndicator != null) {
                if (states.overviewToAllApps || states.stateIsOverviewHidden) {
                    pageIndicator.setVisibility(8);
                } else if (states.stateIsNormal) {
                    setVisiblePageIndicator(pageIndicator);
                } else if (states.stateIsNormalHidden || LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                    pageIndicator.setVisibility(4);
                } else {
                    pageIndicator.setVisibility(0);
                }
            }
            launcherViewPropertyAnimatorAlpha2.setDuration(j);
            launcherViewPropertyAnimatorAlpha.setDuration(j);
            this.mStateAnimator.play(launcherViewPropertyAnimatorAlpha2);
            this.mStateAnimator.play(launcherViewPropertyAnimatorAlpha);
            this.mStateAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.WorkspaceStateTransitionAnimation.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    WorkspaceStateTransitionAnimation.this.mStateAnimator = null;
                    if (accessibilityEnabled && lGOverviewPanel.getVisibility() == 0) {
                        lGOverviewPanel.getChildAt(0).performAccessibilityAction(64, null);
                    }
                }
            });
        } else {
            lGOverviewPanel.setAlpha(f5);
            AlphaUpdateListener.updateVisibility(lGOverviewPanel, accessibilityEnabled);
            if (this.mWorkspace.getCheckSwipeDownInAppDrawer().booleanValue()) {
                layout.setVisibility(0);
                this.mWorkspace.setCheckSwipeDownInAppDrawer(false);
            } else {
                layout.setAlpha(f4);
                AlphaUpdateListener.updateVisibility(layout, accessibilityEnabled);
            }
            if (pageIndicator != null) {
                pageIndicator.animate().translationY(getPageIndicatorTranslationY(states)).setListener(null).setDuration(duration).withLayer().start();
                if (states.overviewToWorkspace || states.stateIsNormal) {
                    setVisiblePageIndicator(pageIndicator);
                }
            }
            this.mWorkspace.setScaleX(this.mNewScale);
            this.mWorkspace.setScaleY(this.mNewScale);
            this.mWorkspace.setTranslationY(overviewModeTranslationY);
            if (accessibilityEnabled && lGOverviewPanel.getVisibility() == 0) {
                lGOverviewPanel.getChildAt(0).performAccessibilityAction(64, null);
            }
        }
        animateDynamicPanel(states);
    }

    private void animateCarouselLayout(final TransitionStates states, int toPage, final boolean animated, final int duration, final HashMap<View, Integer> layerViews, final boolean accessibilityEnabled) {
        cancelAnimation();
        if (animated) {
            this.mStateAnimator = LauncherAnimUtils.createAnimatorSet();
        }
        float f = states.stateIsOverview ? 1.0f : 0.0f;
        this.mNewScale = 1.0f;
        if (!states.stateIsNormal) {
            if (states.stateIsSpringLoaded) {
                this.mNewScale = this.mLauncher.getDeviceProfile().workspaceSpringLoadShrinkFactor;
            } else if (states.stateIsOverview || states.stateIsOverviewHidden) {
                this.mNewScale = this.mLauncher.getDeviceProfile().overviewModeScaleFactor;
            }
        }
        final ViewGroup lGOverviewPanel = this.mLauncher.getLGOverviewPanel();
        if (animated) {
            LauncherViewPropertyAnimator launcherViewPropertyAnimatorAlpha = new LauncherViewPropertyAnimator(lGOverviewPanel).alpha(f);
            launcherViewPropertyAnimatorAlpha.addListener(new AlphaUpdateListener(lGOverviewPanel, accessibilityEnabled));
            lGOverviewPanel.setLayerType(2, null);
            if (layerViews != null) {
                layerViews.put(lGOverviewPanel, 1);
            } else {
                launcherViewPropertyAnimatorAlpha.withLayer();
            }
            if (states.workspaceToOverview) {
                launcherViewPropertyAnimatorAlpha.setInterpolator(null);
            } else if (states.overviewToWorkspace) {
                launcherViewPropertyAnimatorAlpha.setInterpolator(new DecelerateInterpolator(2.0f));
            } else if (states.oldStateIsOverviewHidden && states.stateIsSpringLoaded) {
                UninstallModeManager.getInstance(this.mLauncher).exitUninstallMode(this.mLauncher);
            }
            launcherViewPropertyAnimatorAlpha.setDuration(duration);
            this.mStateAnimator.play(launcherViewPropertyAnimatorAlpha);
            this.mStateAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.WorkspaceStateTransitionAnimation.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    WorkspaceStateTransitionAnimation.this.mStateAnimator = null;
                    if (accessibilityEnabled && lGOverviewPanel.getVisibility() == 0) {
                        lGOverviewPanel.getChildAt(0).performAccessibilityAction(64, null);
                    }
                }
            });
        } else {
            lGOverviewPanel.setAlpha(f);
            AlphaUpdateListener.updateVisibility(lGOverviewPanel, accessibilityEnabled);
            if (this.mWorkspace.getCheckSwipeDownInAppDrawer().booleanValue()) {
                this.mWorkspace.setCheckSwipeDownInAppDrawer(false);
            }
            if (accessibilityEnabled && lGOverviewPanel.getVisibility() == 0) {
                lGOverviewPanel.getChildAt(0).performAccessibilityAction(64, null);
            }
        }
        CarouselLayout carouselLayout = this.mLauncher.getCarouselLayout();
        if (carouselLayout != null) {
            if (states.oldStateIsOverview && states.stateIsNormal) {
                carouselLayout.animate().y(carouselLayout.getTop()).setInterpolator(Interpolators.ACCEL_2);
            } else if (states.oldStateIsNormal && states.stateIsOverview) {
                carouselLayout.animate().translationY(-this.mLauncher.getResources().getDimensionPixelSize(R.dimen.swivel_carousel_view_edit_mode_translation_y)).setInterpolator(Interpolators.ACCEL_2);
            }
        }
    }

    private void animateSearchBar(TransitionStates states, boolean animated, int duration, boolean hasOverlaySearchBar, final HashMap<View, Integer> layerViews, final boolean accessibilityEnabled) {
        final View orCreateQsbBar = this.mLauncher.getOrCreateQsbBar();
        if (orCreateQsbBar != null) {
            final boolean z = states.stateIsNormal;
            final float f = z ? 1.0f : 0.0f;
            if (!animated) {
                orCreateQsbBar.setAlpha(f);
                AlphaUpdateListener.updateVisibility(orCreateQsbBar, accessibilityEnabled);
                return;
            }
            if (hasOverlaySearchBar) {
                this.mStateAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.WorkspaceStateTransitionAnimation.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                        if (z) {
                            orCreateQsbBar.setAlpha(f);
                            AlphaUpdateListener.updateVisibility(orCreateQsbBar, accessibilityEnabled);
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (z) {
                            return;
                        }
                        orCreateQsbBar.setAlpha(f);
                        AlphaUpdateListener.updateVisibility(orCreateQsbBar, accessibilityEnabled);
                    }
                });
                return;
            }
            LauncherViewPropertyAnimator launcherViewPropertyAnimatorAlpha = new LauncherViewPropertyAnimator(orCreateQsbBar).alpha(f);
            launcherViewPropertyAnimatorAlpha.addListener(new AlphaUpdateListener(orCreateQsbBar, accessibilityEnabled));
            orCreateQsbBar.setLayerType(2, null);
            if (layerViews != null) {
                layerViews.put(orCreateQsbBar, 1);
            } else {
                launcherViewPropertyAnimatorAlpha.withLayer();
            }
            launcherViewPropertyAnimatorAlpha.setDuration(duration);
            this.mStateAnimator.play(launcherViewPropertyAnimatorAlpha);
        }
    }

    private void animateBackgroundGradient(TransitionStates states, boolean animated, int duration) {
        final DragLayer dragLayer = this.mLauncher.getDragLayer();
        float backgroundAlpha = dragLayer.getBackgroundAlpha();
        float f = states.stateIsNormal ? 0.0f : this.mWorkspaceScrimAlpha;
        if (f != backgroundAlpha) {
            if (animated) {
                ValueAnimator valueAnimatorOfFloat = LauncherAnimUtils.ofFloat(this.mWorkspace, backgroundAlpha, f);
                valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.WorkspaceStateTransitionAnimation.4
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (WorkspaceStateTransitionAnimation.this.mLauncher.getWorkspace().getCurrentPage() == 0 && WorkspaceStateTransitionAnimation.this.mLauncher.getWorkspace().hasCustomContent() && WorkspaceStateTransitionAnimation.this.mLauncher.getStateManager().getState() == LauncherState.NORMAL) {
                            dragLayer.setBackgroundAlpha(0.4f);
                        } else {
                            dragLayer.setBackgroundAlpha(((Float) animation.getAnimatedValue()).floatValue());
                        }
                    }
                });
                valueAnimatorOfFloat.setInterpolator(new DecelerateInterpolator(1.5f));
                valueAnimatorOfFloat.setDuration(duration);
                this.mStateAnimator.play(valueAnimatorOfFloat);
                return;
            }
            dragLayer.setBackgroundAlpha(f);
        }
    }

    private void cancelAnimation() {
        AnimatorSet animatorSet = this.mStateAnimator;
        if (animatorSet != null) {
            animatorSet.setDuration(0L);
            this.mStateAnimator.cancel();
        }
        this.mStateAnimator = null;
    }

    public void animateDynamicPanel(TransitionStates states) {
        if (states.oldStateIsOverview) {
            if (states.stateIsNormal || states.stateIsOverviewHidden) {
                this.mLauncher.getDynamicGridPannelView().setVisibility(8);
            }
        }
    }

    public void setVisiblePageIndicator(final View pageIndicator) {
        if (pageIndicator != null) {
            if (this.mWorkspace.getCurrentPage() == 0 && this.mWorkspace.getScrollX() == 0 && this.mWorkspace.hasCustomContent()) {
                pageIndicator.setVisibility(8);
            } else {
                pageIndicator.setVisibility(0);
            }
        }
    }

    public int getPageIndicatorTranslationY(View pageIndicator, TransitionStates states) {
        int defaultHomeLayoutHeight = 0;
        if (!states.stateIsOverview) {
            return 0;
        }
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        Workspace workspace = this.mWorkspace;
        int bottom = ((CellLayout) workspace.getPageAt(workspace.numCustomPages())).getShortcutsAndWidgets().getBottom();
        if (com.lge.launcher3.util.Utilities.isLGUI7_1() && states.oldStateIsNormal && states.stateIsOverview) {
            Workspace workspace2 = this.mWorkspace;
            defaultHomeLayoutHeight = ((CellLayout) workspace2.getPageAt(workspace2.numCustomPages())).getDefaultHomeLayoutHeight();
        }
        int i = (bottom - deviceProfile.overviewModeWodrkspaceTranslationYPx) + defaultHomeLayoutHeight;
        return (i + ((((this.mLauncher.getResources().getDisplayMetrics().heightPixels - deviceProfile.overviewModeMaxIconZoneHeightPx) - i) - pageIndicator.getHeight()) / 2)) - pageIndicator.getTop();
    }

    public int getPageIndicatorTranslationY(TransitionStates states) {
        Workspace workspace = this.mWorkspace;
        View pageAt = workspace.getPageAt(workspace.numCustomPages());
        if (pageAt == null || !states.stateIsOverview) {
            return 0;
        }
        View pageindicator = this.mLauncher.getPageindicator();
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        int measuredHeight = (int) (pageAt.getMeasuredHeight() * this.mSpringLoadedShrinkFactor);
        int pivotY = (int) (pageAt.getPivotY() + ((this.mWorkspace.getPivotY() - (pageAt.getPivotY() + pageAt.getTop())) * (1.0f - this.mSpringLoadedShrinkFactor)) + (r3 - this.mWorkspace.getViewport().top) + (measuredHeight / 2) + this.mWorkspace.getOverviewModeTranslationY());
        int bottom = (this.mLauncher.getDragLayer().getBottom() - deviceProfile.getOverviewModeButtonBarRect().height()) - deviceProfile.getInsets().bottom;
        int top = (int) ((bottom - ((bottom - pivotY) / 2)) - (pageindicator.getTop() + pageindicator.getPivotY()));
        return (this.mLauncher.getDeviceProfile().isLandscape && this.mLauncher.getDeviceProfile().allowRotation) ? -this.mLauncher.getResources().getDimensionPixelSize(R.dimen.overview_pageindicator_translation_y_land) : top;
    }

    public void setStateWithAnimation(LauncherState toState, StateAnimationConfig config, PendingAnimation animation) {
        setWorkspaceProperty(toState, animation, config);
    }

    /* JADX WARN: Removed duplicated region for block: B:56:0x014d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void setWorkspaceProperty(com.android.launcher3.LauncherState r17, com.android.launcher3.anim.PropertySetter r18, com.android.launcher3.states.StateAnimationConfig r19) {
        /*
            r16 = this;
            r7 = r16
            r8 = r17
            r9 = r18
            r10 = r19
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.android.launcher3.LauncherState$ScaleAndTranslation r11 = r8.getWorkspaceScaleAndTranslation(r0)
            com.android.launcher3.Launcher r0 = r7.mLauncher
            r8.getHotseatScaleAndTranslation(r0)
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.android.launcher3.Workspace r0 = r0.getWorkspace()
            if (r0 == 0) goto L3a
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.android.launcher3.Workspace r0 = r0.getWorkspace()
            com.android.launcher3.Workspace$State r0 = r0.getState()
            com.android.launcher3.Workspace$State r1 = com.android.launcher3.Workspace.State.OVERVIEW
            if (r0 != r1) goto L3a
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.android.launcher3.Workspace r0 = r0.getWorkspace()
            com.android.launcher3.WorkspaceStateTransitionAnimation r0 = r0.getStateTransitionAnimation()
            float r0 = r0.getFinalScale()
            r7.mNewScale = r0
            goto L3e
        L3a:
            float r0 = r11.scale
            r7.mNewScale = r0
        L3e:
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.android.launcher3.LauncherState$PageAlphaProvider r12 = r8.getWorkspacePageAlphaProvider(r0)
            com.android.launcher3.Workspace r0 = r7.mWorkspace
            int r13 = r0.getChildCount()
            r15 = 0
        L4b:
            if (r15 >= r13) goto L66
            com.android.launcher3.Workspace r0 = r7.mWorkspace
            android.view.View r0 = r0.getChildAt(r15)
            r2 = r0
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            r0 = r16
            r1 = r17
            r3 = r15
            r4 = r12
            r5 = r18
            r6 = r19
            r0.applyChildState(r1, r2, r3, r4, r5, r6)
            int r15 = r15 + 1
            goto L4b
        L66:
            com.android.launcher3.Launcher r0 = r7.mLauncher
            int r0 = r8.getVisibleElements(r0)
            r1 = 3
            android.view.animation.Interpolator r2 = r12.interpolator
            android.view.animation.Interpolator r1 = r10.getInterpolator(r1, r2)
            boolean r2 = r19.playAtomicOverviewScaleComponent()
            com.android.launcher3.Launcher r3 = r7.mLauncher
            com.android.launcher3.Hotseat r3 = r3.getHotseat()
            r4 = 2
            r12 = 1
            if (r2 == 0) goto L14d
            android.view.animation.Interpolator r13 = com.android.launcher3.anim.Interpolators.ZOOM_OUT
            android.view.animation.Interpolator r13 = r10.getInterpolator(r12, r13)
            com.android.launcher3.Launcher r15 = r7.mLauncher
            com.android.launcher3.statemanager.StateManager r15 = r15.getStateManager()
            com.android.launcher3.statemanager.BaseState r15 = r15.getState()
            com.android.launcher3.LauncherState r15 = (com.android.launcher3.LauncherState) r15
            com.android.launcher3.Workspace r14 = r7.mWorkspace
            android.util.FloatProperty<android.view.View> r6 = com.android.launcher3.LauncherAnimUtils.SCALE_PROPERTY
            float r5 = r7.mNewScale
            r9.setFloat(r14, r6, r5, r13)
            boolean r5 = r9 instanceof com.android.launcher3.anim.PendingAnimation
            if (r5 == 0) goto Laa
            com.android.launcher3.LauncherState r5 = com.android.launcher3.LauncherState.HINT_STATE
            if (r15 != r5) goto Laa
            com.android.launcher3.LauncherState r5 = com.android.launcher3.LauncherState.NORMAL
            if (r8 != r5) goto Laa
            r5 = r12
            goto Lab
        Laa:
            r5 = 0
        Lab:
            if (r5 == 0) goto Lbe
            r5 = r9
            com.android.launcher3.anim.PendingAnimation r5 = (com.android.launcher3.anim.PendingAnimation) r5
            com.android.launcher3.Launcher r6 = r7.mLauncher
            com.android.launcher3.Workspace r8 = r7.mWorkspace
            float r13 = r7.mNewScale
            android.animation.ValueAnimator r6 = getSpringScaleAnimator(r6, r8, r13)
            r5.add(r6)
            goto Lc7
        Lbe:
            com.android.launcher3.Workspace r5 = r7.mWorkspace
            android.util.FloatProperty<android.view.View> r6 = com.android.launcher3.LauncherAnimUtils.SCALE_PROPERTY
            float r8 = r7.mNewScale
            r9.setFloat(r5, r6, r8, r13)
        Lc7:
            r5 = r0 & 1
            if (r5 == 0) goto Lce
            r5 = 1065353216(0x3f800000, float:1.0)
            goto Lcf
        Lce:
            r5 = 0
        Lcf:
            com.android.launcher3.Launcher r6 = r7.mLauncher
            com.android.launcher3.Hotseat r6 = r6.getHotseat()
            com.android.launcher3.CellLayout r6 = r6.getLayout()
            r9.setViewAlpha(r6, r5, r1)
            com.lge.launcher3.util.LGHomeFeature$Config r6 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r6 = r6.getValue()
            com.lge.launcher3.util.LGHomeFeature$Config r8 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_SIMPLE_TRANSITION_OF_LANDSCAPE
            boolean r8 = r8.getValue()
            if (r8 != 0) goto Lec
            if (r6 == 0) goto L114
        Lec:
            if (r6 != 0) goto L116
            com.android.launcher3.Launcher r6 = r7.mLauncher
            android.content.res.Resources r6 = r6.getResources()
            r8 = 2131361870(0x7f0a004e, float:1.8343505E38)
            int r6 = r6.getInteger(r8)
            if (r6 == r12) goto L105
            com.lge.launcher3.util.LGHomeFeature$Config r6 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME
            boolean r6 = r6.getValue()
            if (r6 == 0) goto L114
        L105:
            com.android.launcher3.Launcher r6 = r7.mLauncher
            android.content.res.Resources r6 = r6.getResources()
            android.content.res.Configuration r6 = r6.getConfiguration()
            int r6 = r6.orientation
            if (r6 != r4) goto L114
            goto L116
        L114:
            r6 = r12
            goto L117
        L116:
            r6 = 0
        L117:
            if (r6 == 0) goto L136
            com.android.launcher3.Launcher r6 = r7.mLauncher
            com.android.launcher3.Workspace r6 = r6.getWorkspace()
            if (r6 == 0) goto L136
            r6 = r0 & 512(0x200, float:7.17E-43)
            if (r6 == 0) goto L128
            r6 = 1065353216(0x3f800000, float:1.0)
            goto L129
        L128:
            r6 = 0
        L129:
            com.android.launcher3.Launcher r8 = r7.mLauncher
            com.android.launcher3.Workspace r8 = r8.getWorkspace()
            com.android.launcher3.PageIndicator r8 = r8.getPageIndicator()
            r9.setViewAlpha(r8, r6, r1)
        L136:
            com.android.launcher3.Launcher r1 = r7.mLauncher
            com.lge.launcher3.LauncherExtension r1 = (com.lge.launcher3.LauncherExtension) r1
            com.lge.launcher3.wallpapermotion.WallpaperMotionManager r1 = r1.mWallpaperMotionManager
            if (r1 == 0) goto L14d
            r6 = 1065353216(0x3f800000, float:1.0)
            int r5 = java.lang.Float.compare(r5, r6)
            if (r5 != 0) goto L148
            r14 = r12
            goto L149
        L148:
            r14 = 0
        L149:
            r1.setEnableParallax(r14)
            goto L14f
        L14d:
            r6 = 1065353216(0x3f800000, float:1.0)
        L14f:
            boolean r1 = r19.onlyPlayAtomicComponent()
            if (r1 == 0) goto L156
            return
        L156:
            if (r2 != 0) goto L15b
            android.view.animation.Interpolator r1 = com.android.launcher3.anim.Interpolators.LINEAR
            goto L161
        L15b:
            android.view.animation.Interpolator r1 = com.android.launcher3.anim.Interpolators.ZOOM_OUT
            android.view.animation.Interpolator r1 = r10.getInterpolator(r4, r1)
        L161:
            com.android.launcher3.Workspace r2 = r7.mWorkspace
            android.util.FloatProperty<android.view.View> r4 = com.android.launcher3.LauncherAnimUtils.VIEW_TRANSLATE_X
            float r5 = r11.translationX
            r9.setFloat(r2, r4, r5, r1)
            com.android.launcher3.Workspace r2 = r7.mWorkspace
            android.util.FloatProperty<android.view.View> r4 = com.android.launcher3.LauncherAnimUtils.VIEW_TRANSLATE_Y
            float r5 = r11.translationY
            r9.setFloat(r2, r4, r5, r1)
            r0 = r0 & r12
            if (r0 == 0) goto L178
            r5 = r6
            goto L179
        L178:
            r5 = 0
        L179:
            android.util.FloatProperty<android.view.View> r0 = com.android.launcher3.LauncherAnimUtils.VIEW_ALPHA
            android.view.animation.Interpolator r1 = com.android.launcher3.anim.Interpolators.DEACCEL_7
            r9.setFloat(r3, r0, r5, r1)
            com.lge.launcher3.util.LGHomeFeature$Config r0 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r0 = r0.getValue()
            if (r0 != 0) goto L1c2
            com.android.launcher3.Workspace r0 = r7.mWorkspace
            float r0 = r0.getOverlayTranslation()
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L1c2
            java.lang.String r0 = "WorkspaceStateTransitionAnimation"
            java.lang.String r2 = "setWorkspaceProperty(): reset OverlayTranslation"
            com.lge.launcher3.util.LGLog.d(r0, r2)
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.android.launcher3.SearchDropTargetBar r0 = r0.getSearchBar()
            r0.setTranslationX(r1)
            com.android.launcher3.Workspace r0 = r7.mWorkspace
            com.android.launcher3.PageIndicator r0 = r0.getPageIndicator()
            if (r0 == 0) goto L1b4
            com.android.launcher3.Workspace r0 = r7.mWorkspace
            com.android.launcher3.PageIndicator r0 = r0.getPageIndicator()
            r0.setTranslationX(r1)
        L1b4:
            com.android.launcher3.Workspace r0 = r7.mWorkspace
            r0.onOverlayScrollChanged(r1)
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.android.launcher3.Hotseat r0 = r0.getHotseat()
            r0.setTranslationX(r1)
        L1c2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.WorkspaceStateTransitionAnimation.setWorkspaceProperty(com.android.launcher3.LauncherState, com.android.launcher3.anim.PropertySetter, com.android.launcher3.states.StateAnimationConfig):void");
    }

    private void setPivotToScaleWithWorkspace(View sibling) {
        sibling.setPivotY(((this.mWorkspace.getPivotY() + this.mWorkspace.getTop()) - sibling.getTop()) - sibling.getTranslationY());
        sibling.setPivotX(((this.mWorkspace.getPivotX() + this.mWorkspace.getLeft()) - sibling.getLeft()) - sibling.getTranslationX());
    }

    public void setScrim(PropertySetter propertySetter, LauncherState state) {
        WorkspaceAndHotseatScrim scrim = this.mLauncher.getDragLayer().getScrim();
        propertySetter.setFloat(scrim, WorkspaceAndHotseatScrim.SCRIM_PROGRESS, state.getWorkspaceScrimAlpha(this.mLauncher), Interpolators.LINEAR);
        propertySetter.setFloat(scrim, WorkspaceAndHotseatScrim.SYSUI_PROGRESS, state.hasFlag(LauncherState.FLAG_HAS_SYS_UI_SCRIM) ? 1.0f : 0.0f, Interpolators.LINEAR);
    }

    public void applyChildState(LauncherState state, CellLayout cl, int childIndex) {
        applyChildState(state, cl, childIndex, state.getWorkspacePageAlphaProvider(this.mLauncher), PropertySetter.NO_ANIM_PROPERTY_SETTER, new StateAnimationConfig());
    }

    private void applyChildState(LauncherState state, CellLayout cl, int childIndex, LauncherState.PageAlphaProvider pageAlphaProvider, PropertySetter propertySetter, StateAnimationConfig config) {
        float pageAlpha = pageAlphaProvider.getPageAlpha(childIndex);
        Math.round((state.hasWorkspacePageBackground ? 255 : 0) * pageAlpha);
        if (config.playAtomicOverviewScaleComponent()) {
            propertySetter.setFloat(cl.getShortcutsAndWidgets(), LauncherAnimUtils.VIEW_ALPHA, pageAlpha, config.getInterpolator(3, pageAlphaProvider.interpolator));
        }
    }

    public static ValueAnimator getSpringScaleAnimator(Launcher launcher, View v, float scale) {
        ResourceProvider resourceProviderProvider = DynamicResource.provider(launcher);
        float f = resourceProviderProvider.getFloat(R.dimen.hint_scale_damping_ratio);
        float f2 = resourceProviderProvider.getFloat(R.dimen.hint_scale_stiffness);
        return new SpringAnimationBuilder(v.getContext()).setStiffness(f2).setDampingRatio(f).setMinimumVisibleChange(0.002f).setEndValue(scale).setStartValue(((Float) LauncherAnimUtils.SCALE_PROPERTY.get(v)).floatValue()).setStartVelocity(resourceProviderProvider.getDimension(R.dimen.hint_scale_velocity_dp_per_s)).build(v, LauncherAnimUtils.SCALE_PROPERTY);
    }
}
