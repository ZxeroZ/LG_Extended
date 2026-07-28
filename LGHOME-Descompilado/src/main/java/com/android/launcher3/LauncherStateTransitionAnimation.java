package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.launcher3.Workspace;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.anim.AnimatorSetBuilder;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.util.UiThreadCircularReveal;
import com.android.launcher3.widget.WidgetsContainerView;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsHost;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class LauncherStateTransitionAnimation {
    public static final int BUILD_AND_SET_LAYER = 1;
    public static final int BUILD_LAYER = 0;
    public static final int SINGLE_FRAME_DELAY = 16;
    public static final String TAG = "LauncherStateTransitionAnimation";
    Callbacks mCb;
    Launcher mLauncher;
    AnimatorSet mStateAnimation;

    interface Callbacks {
        void onStateTransitionHideSearchBar();
    }

    static abstract class PrivateTransitionCallbacks {
        AnimatorListenerAdapter getMaterialRevealViewAnimatorListener(View revealView, View buttonView) {
            return null;
        }

        float getMaterialRevealViewFinalAlpha(View revealView) {
            return 0.0f;
        }

        float getMaterialRevealViewStartFinalRadius() {
            return 0.0f;
        }

        void onTransitionComplete() {
        }

        PrivateTransitionCallbacks() {
        }
    }

    public LauncherStateTransitionAnimation(Launcher l, Callbacks cb) {
        this.mLauncher = l;
        this.mCb = cb;
    }

    public void startAnimationToAllApps(final boolean animated, final boolean startSearchAfterTransition) {
        final AllAppsHost allAppsHost = this.mLauncher.getAllAppsHost();
        startAnimationToOverlay(Workspace.State.NORMAL_HIDDEN, this.mLauncher.getHotseat().getLayout(), allAppsHost, allAppsHost.getContentView(), allAppsHost.getRevealView(), allAppsHost.getSearchBarView(), animated, true, new PrivateTransitionCallbacks() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.1
            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            public float getMaterialRevealViewFinalAlpha(View revealView) {
                return 1.0f;
            }

            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            public float getMaterialRevealViewStartFinalRadius() {
                return LauncherStateTransitionAnimation.this.mLauncher.getDeviceProfile().allAppsButtonVisualSize / 2;
            }

            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            public AnimatorListenerAdapter getMaterialRevealViewAnimatorListener(final View revealView, final View allAppsButtonView) {
                return new AnimatorListenerAdapter() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.1.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                    }
                };
            }

            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            void onTransitionComplete() {
                if (startSearchAfterTransition) {
                    allAppsHost.startAppsSearch();
                }
            }
        });
    }

    public void startAnimationToWidgets(final boolean animated) {
        WidgetsContainerView widgetsView = this.mLauncher.getWidgetsView();
        startAnimationToOverlay(Workspace.State.OVERVIEW_HIDDEN, this.mLauncher.getWidgetsButton(), widgetsView, widgetsView.getContentView(), widgetsView.getRevealView(), null, animated, true, new PrivateTransitionCallbacks() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.2
            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            public float getMaterialRevealViewFinalAlpha(View revealView) {
                return 0.3f;
            }
        });
    }

    public void startAnimationToWorkspace(final LauncherState fromState, final Workspace.State toWorkspaceState, final int toWorkspacePage, final boolean animated, final Runnable onCompleteRunnable) {
        if (toWorkspaceState != Workspace.State.NORMAL && toWorkspaceState != Workspace.State.SPRING_LOADED && toWorkspaceState != Workspace.State.OVERVIEW) {
            Log.e(TAG, "Unexpected call to startAnimationToWorkspace");
        }
        if (fromState == LauncherState.ALL_APPS || fromState == LauncherState.APPS_SPRING_LOADED) {
            startAnimationToWorkspaceFromAllApps(toWorkspaceState, toWorkspacePage, animated, onCompleteRunnable);
            return;
        }
        if (fromState == LauncherState.OVERVIEW) {
            LGLog.i(TAG, "startAnimationToWorkspace : fromState = " + fromState + ", toWorkspacePage = " + toWorkspacePage);
            startAnimationToWorkspaceFromOverView(toWorkspaceState, toWorkspacePage, animated, onCompleteRunnable);
            return;
        }
        startAnimationToWorkspaceFromWidgets(toWorkspaceState, toWorkspacePage, animated, onCompleteRunnable);
        if (fromState == LauncherState.WIDGETS) {
            this.mLauncher.unlockScreenOrientation(animated);
        }
    }

    private void startAnimationToOverlay(final Workspace.State toWorkspaceState, final View buttonView, final View toView, final View contentView, final View revealView, final View overlaySearchBarView, final boolean animated, final boolean hideSearchBar, final PrivateTransitionCallbacks pCb) {
        int i;
        float f;
        float materialRevealViewFinalAlpha;
        float f2;
        boolean z;
        boolean z2;
        HashMap<View, Integer> map;
        int i2;
        int i3;
        Resources resources = this.mLauncher.getResources();
        boolean zIsLmpOrAbove = Utilities.isLmpOrAbove();
        int integer = resources.getInteger(R.integer.config_overlayRevealTime);
        resources.getInteger(R.integer.config_overlayItemsAlphaStagger);
        final Workspace workspace = this.mLauncher.getWorkspace();
        HashMap<View, Integer> map2 = new HashMap<>();
        boolean z3 = buttonView != null;
        cancelAnimation();
        Animator animatorStartWorkspaceStateChangeAnimation = this.mLauncher.startWorkspaceStateChangeAnimation(toWorkspaceState, -1, animated, overlaySearchBarView != null, map2);
        if (animated && z3) {
            this.mStateAnimation = LauncherAnimUtils.createAnimatorSet();
            int measuredWidth = revealView.getMeasuredWidth();
            int measuredHeight = revealView.getMeasuredHeight();
            int i4 = measuredWidth / 2;
            int i5 = measuredHeight / 2;
            float fHypot = (float) Math.hypot(i4, i5);
            revealView.setVisibility(0);
            revealView.setAlpha(0.0f);
            revealView.setTranslationY(0.0f);
            revealView.setTranslationX(0.0f);
            if (zIsLmpOrAbove) {
                int[] centerDeltaInScreenSpace = Utilities.getCenterDeltaInScreenSpace(contentView, buttonView, null);
                materialRevealViewFinalAlpha = pCb.getMaterialRevealViewFinalAlpha(revealView);
                float f3 = centerDeltaInScreenSpace[1];
                f2 = centerDeltaInScreenSpace[0];
                f = f3;
                i = 2;
            } else {
                i = 2;
                f = (measuredHeight * 2) / 3;
                materialRevealViewFinalAlpha = 0.0f;
                f2 = 0.0f;
            }
            float[] fArr = new float[i];
            fArr[0] = materialRevealViewFinalAlpha;
            fArr[1] = 1.0f;
            PropertyValuesHolder propertyValuesHolderOfFloat = PropertyValuesHolder.ofFloat("alpha", fArr);
            float[] fArr2 = new float[i];
            fArr2[0] = f;
            fArr2[1] = 0.0f;
            PropertyValuesHolder propertyValuesHolderOfFloat2 = PropertyValuesHolder.ofFloat("translationY", fArr2);
            float[] fArr3 = new float[i];
            fArr3[0] = f2;
            fArr3[1] = 0.0f;
            ObjectAnimator objectAnimatorOfPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(revealView, propertyValuesHolderOfFloat, propertyValuesHolderOfFloat2, PropertyValuesHolder.ofFloat("translationX", fArr3));
            long j = integer;
            objectAnimatorOfPropertyValuesHolder.setDuration(j);
            objectAnimatorOfPropertyValuesHolder.setInterpolator(new LogDecelerateInterpolator(100, 0));
            map2.put(revealView, 1);
            this.mStateAnimation.play(objectAnimatorOfPropertyValuesHolder);
            if (overlaySearchBarView != null) {
                overlaySearchBarView.setAlpha(0.0f);
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(overlaySearchBarView, "alpha", 0.0f, 1.0f);
                z = zIsLmpOrAbove;
                objectAnimatorOfFloat.setDuration(100L);
                objectAnimatorOfFloat.setInterpolator(new AccelerateInterpolator(1.5f));
                map2.put(overlaySearchBarView, 1);
                this.mStateAnimation.play(objectAnimatorOfFloat);
            } else {
                z = zIsLmpOrAbove;
            }
            contentView.setVisibility(0);
            contentView.setAlpha(0.0f);
            contentView.setTranslationY(f);
            map2.put(contentView, 1);
            if (toWorkspaceState == Workspace.State.OVERVIEW_HIDDEN) {
                map = map2;
                PathInterpolator pathInterpolator = new PathInterpolator(0.0f, 0.2f, 0.0f, 1.0f);
                z2 = z;
                PathInterpolator pathInterpolator2 = new PathInterpolator(0.5f, 0.0f, 0.8f, 0.6f);
                ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(contentView, "translationX", f2, 0.0f);
                objectAnimatorOfFloat2.setDuration(336L);
                objectAnimatorOfFloat2.setInterpolator(pathInterpolator);
                this.mStateAnimation.play(objectAnimatorOfFloat2);
                ObjectAnimator objectAnimatorOfFloat3 = ObjectAnimator.ofFloat(contentView, "translationY", f, 0.0f);
                objectAnimatorOfFloat3.setDuration(336L);
                objectAnimatorOfFloat3.setInterpolator(pathInterpolator);
                this.mStateAnimation.play(objectAnimatorOfFloat3);
                ObjectAnimator objectAnimatorOfFloat4 = ObjectAnimator.ofFloat(contentView, "scaleX", 0.2f, 1.0f);
                objectAnimatorOfFloat4.setDuration(336L);
                objectAnimatorOfFloat4.setInterpolator(pathInterpolator);
                this.mStateAnimation.play(objectAnimatorOfFloat4);
                ObjectAnimator objectAnimatorOfFloat5 = ObjectAnimator.ofFloat(contentView, "scaleY", 0.14f, 1.0f);
                objectAnimatorOfFloat5.setDuration(336L);
                objectAnimatorOfFloat5.setInterpolator(pathInterpolator);
                this.mStateAnimation.play(objectAnimatorOfFloat5);
                ObjectAnimator objectAnimatorOfFloat6 = ObjectAnimator.ofFloat(contentView, "alpha", 0.0f, 1.0f);
                objectAnimatorOfFloat6.setDuration(280L);
                objectAnimatorOfFloat6.setInterpolator(pathInterpolator2);
                objectAnimatorOfFloat6.setStartDelay(56L);
                this.mStateAnimation.play(objectAnimatorOfFloat6);
                i2 = 0;
                i3 = 100;
            } else {
                z2 = z;
                map = map2;
                i2 = 0;
                ObjectAnimator objectAnimatorOfFloat7 = ObjectAnimator.ofFloat(contentView, "translationY", Utilities.pxFromDp(resources.getInteger(R.integer.config_swipeup_transitiony), resources.getDisplayMetrics()), 0.0f);
                objectAnimatorOfFloat7.setStartDelay(100L);
                objectAnimatorOfFloat7.setDuration(j);
                i3 = 100;
                objectAnimatorOfFloat7.setInterpolator(new LogDecelerateInterpolator(100, 0));
                this.mStateAnimation.play(objectAnimatorOfFloat7);
                ObjectAnimator objectAnimatorOfFloat8 = ObjectAnimator.ofFloat(contentView, "alpha", 0.0f, 1.0f);
                objectAnimatorOfFloat8.setStartDelay(100L);
                objectAnimatorOfFloat8.setDuration(j);
                objectAnimatorOfFloat8.setInterpolator(new LogDecelerateInterpolator(100, 0));
                this.mStateAnimation.play(objectAnimatorOfFloat8);
                ObjectAnimator objectAnimatorOfFloat9 = ObjectAnimator.ofFloat(contentView, "scaleX", 0.9f, 1.0f);
                objectAnimatorOfFloat8.setStartDelay(100L);
                objectAnimatorOfFloat9.setDuration(j);
                objectAnimatorOfFloat9.setInterpolator(new LogDecelerateInterpolator(100, 0));
                this.mStateAnimation.play(objectAnimatorOfFloat9);
                ObjectAnimator objectAnimatorOfFloat10 = ObjectAnimator.ofFloat(contentView, "scaleY", 0.9f, 1.0f);
                objectAnimatorOfFloat8.setStartDelay(100L);
                objectAnimatorOfFloat10.setDuration(j);
                objectAnimatorOfFloat10.setInterpolator(new LogDecelerateInterpolator(100, 0));
                this.mStateAnimation.play(objectAnimatorOfFloat10);
            }
            if (z2) {
                float materialRevealViewStartFinalRadius = pCb.getMaterialRevealViewStartFinalRadius();
                AnimatorListenerAdapter materialRevealViewAnimatorListener = pCb.getMaterialRevealViewAnimatorListener(revealView, buttonView);
                ValueAnimator valueAnimatorCreateCircularReveal = UiThreadCircularReveal.createCircularReveal(revealView, i4, i5, materialRevealViewStartFinalRadius, fHypot);
                valueAnimatorCreateCircularReveal.setDuration(j);
                valueAnimatorCreateCircularReveal.setInterpolator(new LogDecelerateInterpolator(i3, i2));
                if (materialRevealViewAnimatorListener != null) {
                    valueAnimatorCreateCircularReveal.addListener(materialRevealViewAnimatorListener);
                }
                this.mStateAnimation.play(valueAnimatorCreateCircularReveal);
            }
            final HashMap<View, Integer> map3 = map;
            this.mStateAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    LauncherStateTransitionAnimation.this.dispatchOnLauncherTransitionEnd(workspace, animated, false);
                    LauncherStateTransitionAnimation.this.dispatchOnLauncherTransitionEnd(toView, animated, false);
                    revealView.setVisibility(4);
                    for (View view : map3.keySet()) {
                        if (((Integer) map3.get(view)).intValue() == 1) {
                            view.setLayerType(0, null);
                        }
                    }
                    if (hideSearchBar) {
                        LauncherStateTransitionAnimation.this.mCb.onStateTransitionHideSearchBar();
                    }
                    LauncherStateTransitionAnimation.this.mStateAnimation = null;
                    pCb.onTransitionComplete();
                }
            });
            if (animatorStartWorkspaceStateChangeAnimation != null) {
                this.mStateAnimation.play(animatorStartWorkspaceStateChangeAnimation);
            }
            dispatchOnLauncherTransitionPrepare(workspace, animated, false);
            dispatchOnLauncherTransitionPrepare(toView, animated, false);
            final AnimatorSet animatorSet = this.mStateAnimation;
            final HashMap<View, Integer> map4 = map;
            Runnable runnable = new Runnable() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.4
                @Override // java.lang.Runnable
                public void run() {
                    if (LauncherStateTransitionAnimation.this.mStateAnimation != animatorSet) {
                        return;
                    }
                    LauncherStateTransitionAnimation.this.dispatchOnLauncherTransitionStart(workspace, animated, false);
                    LauncherStateTransitionAnimation.this.dispatchOnLauncherTransitionStart(toView, animated, false);
                    boolean zIsLmpOrAbove2 = Utilities.isLmpOrAbove();
                    for (View view : map4.keySet()) {
                        if (((Integer) map4.get(view)).intValue() == 1) {
                            view.setLayerType(2, null);
                        }
                        if (zIsLmpOrAbove2 && Utilities.isViewAttachedToWindow(view)) {
                            view.buildLayer();
                        }
                    }
                    toView.requestFocus();
                    LauncherStateTransitionAnimation.this.mStateAnimation.start();
                }
            };
            toView.setVisibility(0);
            toView.post(runnable);
            return;
        }
        toView.setTranslationX(0.0f);
        toView.setTranslationY(0.0f);
        toView.setScaleX(1.0f);
        toView.setScaleY(1.0f);
        toView.setVisibility(0);
        contentView.setVisibility(0);
        if (hideSearchBar) {
            this.mCb.onStateTransitionHideSearchBar();
        }
        dispatchOnLauncherTransitionPrepare(workspace, animated, false);
        dispatchOnLauncherTransitionStart(workspace, animated, false);
        dispatchOnLauncherTransitionEnd(workspace, animated, false);
        dispatchOnLauncherTransitionPrepare(toView, animated, false);
        dispatchOnLauncherTransitionStart(toView, animated, false);
        dispatchOnLauncherTransitionEnd(toView, animated, false);
        pCb.onTransitionComplete();
    }

    private void startAnimationToWorkspaceFromAllApps(final Workspace.State toWorkspaceState, final int toWorkspacePage, final boolean animated, final Runnable onCompleteRunnable) {
        AllAppsHost allAppsHost = this.mLauncher.getAllAppsHost();
        startAnimationToWorkspaceFromOverlay(toWorkspaceState, toWorkspacePage, this.mLauncher.getAllAppsButton(), allAppsHost, allAppsHost.getContentView(), allAppsHost.getRevealView(), allAppsHost.getSearchBarView(), animated, onCompleteRunnable, new PrivateTransitionCallbacks() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.5
            int[] mAllAppsToPanelDelta;

            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            float getMaterialRevealViewFinalAlpha(View revealView) {
                return 1.0f;
            }

            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            float getMaterialRevealViewStartFinalRadius() {
                return LauncherStateTransitionAnimation.this.mLauncher.getDeviceProfile().allAppsButtonVisualSize / 2;
            }

            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            public AnimatorListenerAdapter getMaterialRevealViewAnimatorListener(final View revealView, final View allAppsButtonView) {
                return new AnimatorListenerAdapter() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.5.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        revealView.setVisibility(4);
                    }
                };
            }
        });
    }

    private void startAnimationToWorkspaceFromOverView(final Workspace.State toWorkspaceState, final int toWorkspacePage, final boolean animated, final Runnable onCompleteRunnable) {
        new PrivateTransitionCallbacks() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.6
            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            float getMaterialRevealViewFinalAlpha(View revealView) {
                return 1.0f;
            }

            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            public AnimatorListenerAdapter getMaterialRevealViewAnimatorListener(final View revealView, final View widgetsButtonView) {
                return new AnimatorListenerAdapter() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.6.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        revealView.setVisibility(4);
                    }
                };
            }
        };
    }

    private void startAnimationToWorkspaceFromWidgets(final Workspace.State toWorkspaceState, final int toWorkspacePage, final boolean animated, final Runnable onCompleteRunnable) {
        WidgetsContainerView widgetsView = this.mLauncher.getWidgetsView();
        startAnimationToWorkspaceFromOverlay(toWorkspaceState, toWorkspacePage, this.mLauncher.getWidgetsButton(), widgetsView, widgetsView.getContentView(), widgetsView.getRevealView(), null, animated, onCompleteRunnable, new PrivateTransitionCallbacks() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.7
            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            float getMaterialRevealViewFinalAlpha(View revealView) {
                return 0.3f;
            }

            @Override // com.android.launcher3.LauncherStateTransitionAnimation.PrivateTransitionCallbacks
            public AnimatorListenerAdapter getMaterialRevealViewAnimatorListener(final View revealView, final View widgetsButtonView) {
                return new AnimatorListenerAdapter() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.7.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        revealView.setVisibility(4);
                    }
                };
            }
        });
    }

    private void startAnimationToWorkspaceFromOverlay(final Workspace.State toWorkspaceState, final int toWorkspacePage, final View buttonView, final View fromView, final View contentView, final View revealView, final View overlaySearchBarView, final boolean animated, final Runnable onCompleteRunnable, final PrivateTransitionCallbacks pCb) {
        HashMap<View, Integer> map;
        Workspace workspace;
        float f;
        float f2;
        int i;
        char c;
        TimeInterpolator decelerateInterpolator;
        float materialRevealViewFinalAlpha;
        float f3;
        int i2;
        TimeInterpolator timeInterpolator;
        int i3;
        long j;
        View view;
        boolean z;
        View view2 = fromView;
        boolean z2 = animated;
        Resources resources = this.mLauncher.getResources();
        boolean zIsLmpOrAbove = Utilities.isLmpOrAbove();
        int integer = resources.getInteger(R.integer.config_overlayRevealTime);
        int integer2 = resources.getInteger(R.integer.config_overlayItemsAlphaStagger);
        Workspace workspace2 = this.mLauncher.getWorkspace();
        HashMap<View, Integer> map2 = new HashMap<>();
        boolean z3 = LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() ? true : buttonView != null;
        cancelAnimation();
        Animator animatorStartWorkspaceStateChangeAnimation = this.mLauncher.startWorkspaceStateChangeAnimation(toWorkspaceState, toWorkspacePage, animated, overlaySearchBarView != null, map2);
        if (z2 && z3) {
            AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
            this.mStateAnimation = animatorSetCreateAnimatorSet;
            if (animatorStartWorkspaceStateChangeAnimation != null) {
                animatorSetCreateAnimatorSet.play(animatorStartWorkspaceStateChangeAnimation);
            }
            if (fromView.getVisibility() == 0) {
                int measuredWidth = revealView.getMeasuredWidth();
                int measuredHeight = revealView.getMeasuredHeight();
                int i4 = measuredWidth / 2;
                int i5 = measuredHeight / 2;
                float fHypot = (float) Math.hypot(i4, i5);
                revealView.setVisibility(0);
                revealView.setAlpha(1.0f);
                revealView.setTranslationY(0.0f);
                map2.put(revealView, 1);
                if (zIsLmpOrAbove) {
                    int[] centerDeltaInScreenSpace = Utilities.getCenterDeltaInScreenSpace(revealView, buttonView, null);
                    f = centerDeltaInScreenSpace[1];
                    f2 = centerDeltaInScreenSpace[0];
                } else {
                    f = (measuredHeight * 2) / 3;
                    f2 = 0.0f;
                }
                if (zIsLmpOrAbove) {
                    i = i5;
                    c = 0;
                    decelerateInterpolator = new LogDecelerateInterpolator(100, 0);
                } else {
                    i = i5;
                    c = 0;
                    decelerateInterpolator = new DecelerateInterpolator(1.0f);
                }
                float[] fArr = new float[2];
                fArr[c] = 0.0f;
                fArr[1] = f;
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(revealView, "translationY", fArr);
                long j2 = integer - 16;
                objectAnimatorOfFloat.setDuration(j2);
                float f4 = f;
                long j3 = integer2 + 16;
                objectAnimatorOfFloat.setStartDelay(j3);
                objectAnimatorOfFloat.setInterpolator(decelerateInterpolator);
                this.mStateAnimation.play(objectAnimatorOfFloat);
                ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(revealView, "translationX", 0.0f, f2);
                objectAnimatorOfFloat2.setDuration(j2);
                objectAnimatorOfFloat2.setStartDelay(j3);
                objectAnimatorOfFloat2.setInterpolator(decelerateInterpolator);
                this.mStateAnimation.play(objectAnimatorOfFloat2);
                if (zIsLmpOrAbove) {
                    materialRevealViewFinalAlpha = pCb.getMaterialRevealViewFinalAlpha(revealView);
                    f3 = 1.0f;
                } else {
                    f3 = 1.0f;
                    materialRevealViewFinalAlpha = 0.0f;
                }
                if (materialRevealViewFinalAlpha != f3) {
                    ObjectAnimator objectAnimatorOfFloat3 = ObjectAnimator.ofFloat(revealView, "alpha", f3, materialRevealViewFinalAlpha);
                    objectAnimatorOfFloat3.setDuration(zIsLmpOrAbove ? integer : 150L);
                    objectAnimatorOfFloat3.setStartDelay(zIsLmpOrAbove ? 0L : j3);
                    objectAnimatorOfFloat3.setInterpolator(decelerateInterpolator);
                    this.mStateAnimation.play(objectAnimatorOfFloat3);
                }
                map = map2;
                map.put(contentView, 1);
                if (toWorkspaceState == Workspace.State.OVERVIEW) {
                    DecelerateInterpolator decelerateInterpolator2 = new DecelerateInterpolator(2.5f);
                    i2 = integer2;
                    PathInterpolator pathInterpolator = new PathInterpolator(0.2f, 0.0f, 0.2f, 1.0f);
                    ObjectAnimator objectAnimatorOfFloat4 = ObjectAnimator.ofFloat(contentView, "translationX", 0.0f, f2 * 0.8f);
                    contentView.setTranslationX(0.0f);
                    objectAnimatorOfFloat4.setDuration(336L);
                    objectAnimatorOfFloat4.setInterpolator(pathInterpolator);
                    this.mStateAnimation.play(objectAnimatorOfFloat4);
                    ObjectAnimator objectAnimatorOfFloat5 = ObjectAnimator.ofFloat(contentView, "translationY", 0.0f, 0.8f * f4);
                    contentView.setTranslationY(0.0f);
                    objectAnimatorOfFloat5.setDuration(336L);
                    objectAnimatorOfFloat5.setInterpolator(pathInterpolator);
                    this.mStateAnimation.play(objectAnimatorOfFloat5);
                    ObjectAnimator objectAnimatorOfFloat6 = ObjectAnimator.ofFloat(contentView, "scaleX", 1.0f, 0.15f);
                    objectAnimatorOfFloat6.setDuration(336L);
                    objectAnimatorOfFloat6.setInterpolator(pathInterpolator);
                    this.mStateAnimation.play(objectAnimatorOfFloat6);
                    ObjectAnimator objectAnimatorOfFloat7 = ObjectAnimator.ofFloat(contentView, "scaleY", 1.0f, 0.15f);
                    objectAnimatorOfFloat7.setDuration(336L);
                    objectAnimatorOfFloat7.setInterpolator(pathInterpolator);
                    this.mStateAnimation.play(objectAnimatorOfFloat7);
                    contentView.setAlpha(1.0f);
                    ObjectAnimator objectAnimatorOfFloat8 = ObjectAnimator.ofFloat(contentView, "alpha", 1.0f, 0.0f);
                    objectAnimatorOfFloat8.setDuration(336L);
                    objectAnimatorOfFloat8.setInterpolator(decelerateInterpolator2);
                    this.mStateAnimation.play(objectAnimatorOfFloat8);
                    view2 = fromView;
                    timeInterpolator = decelerateInterpolator;
                    z = true;
                    i3 = 2;
                    j = 150;
                    view = overlaySearchBarView;
                } else {
                    i2 = integer2;
                    TimeInterpolator timeInterpolator2 = decelerateInterpolator;
                    AnimatorSetBuilder animatorSetBuilder = new AnimatorSetBuilder();
                    Interpolator interpolator = Interpolators.FAST_OUT_SLOW_IN;
                    ObjectAnimator objectAnimatorOfFloat9 = ObjectAnimator.ofFloat(this.mLauncher.getAllAppsController(), AllAppsTransitionController.ALL_APPS_PROGRESS, 0.0f, 1.0f);
                    objectAnimatorOfFloat9.setDuration(integer);
                    objectAnimatorOfFloat9.setInterpolator(animatorSetBuilder.getInterpolator(0, interpolator));
                    objectAnimatorOfFloat9.addListener(this.mLauncher.getAllAppsController().getProgressAnimatorListener());
                    animatorSetBuilder.play(objectAnimatorOfFloat9);
                    this.mStateAnimation.play(animatorSetBuilder.build());
                    view2 = fromView;
                    if (view2 instanceof AllAppsHost) {
                        i3 = 2;
                        ObjectAnimator objectAnimatorOfFloat10 = ObjectAnimator.ofFloat(view2, "alpha", 1.0f, 0.0f);
                        j = 150;
                        objectAnimatorOfFloat10.setDuration(150L);
                        timeInterpolator = timeInterpolator2;
                        objectAnimatorOfFloat10.setInterpolator(timeInterpolator);
                        this.mStateAnimation.play(objectAnimatorOfFloat10);
                    } else {
                        timeInterpolator = timeInterpolator2;
                        i3 = 2;
                        j = 150;
                    }
                    float[] fArr2 = new float[i3];
                    // fill-array-data instruction
                    fArr2[0] = 1.0f;
                    fArr2[1] = 0.0f;
                    ObjectAnimator objectAnimatorOfFloat11 = ObjectAnimator.ofFloat(contentView, "alpha", fArr2);
                    objectAnimatorOfFloat11.setDuration(j);
                    objectAnimatorOfFloat11.setInterpolator(timeInterpolator);
                    this.mStateAnimation.play(objectAnimatorOfFloat11);
                    view = overlaySearchBarView;
                    z = true;
                }
                if (view != null) {
                    view.setAlpha(1.0f);
                    float[] fArr3 = new float[i3];
                    // fill-array-data instruction
                    fArr3[0] = 1.0f;
                    fArr3[1] = 0.0f;
                    ObjectAnimator objectAnimatorOfFloat12 = ObjectAnimator.ofFloat(view, "alpha", fArr3);
                    objectAnimatorOfFloat12.setDuration(zIsLmpOrAbove ? 100L : j);
                    objectAnimatorOfFloat12.setInterpolator(timeInterpolator);
                    objectAnimatorOfFloat12.setStartDelay(zIsLmpOrAbove ? 0L : j3);
                    map.put(view, 1);
                    this.mStateAnimation.play(objectAnimatorOfFloat12);
                }
                if (zIsLmpOrAbove) {
                    float materialRevealViewStartFinalRadius = pCb.getMaterialRevealViewStartFinalRadius();
                    AnimatorListenerAdapter materialRevealViewAnimatorListener = pCb.getMaterialRevealViewAnimatorListener(revealView, buttonView);
                    ValueAnimator valueAnimatorCreateCircularReveal = UiThreadCircularReveal.createCircularReveal(revealView, i4, i, fHypot, materialRevealViewStartFinalRadius);
                    valueAnimatorCreateCircularReveal.setInterpolator(new LogDecelerateInterpolator(100, 0));
                    valueAnimatorCreateCircularReveal.setDuration(integer);
                    valueAnimatorCreateCircularReveal.setStartDelay(i2);
                    if (materialRevealViewAnimatorListener != null) {
                        valueAnimatorCreateCircularReveal.addListener(materialRevealViewAnimatorListener);
                    }
                    this.mStateAnimation.play(valueAnimatorCreateCircularReveal);
                }
                z2 = animated;
                dispatchOnLauncherTransitionPrepare(view2, z2, z);
                workspace = workspace2;
                dispatchOnLauncherTransitionPrepare(workspace, z2, z);
            } else {
                map = map2;
                workspace = workspace2;
            }
            final Workspace workspace3 = workspace;
            final Workspace workspace4 = workspace;
            final HashMap<View, Integer> map3 = map;
            this.mStateAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.8
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    View view3 = fromView;
                    if (view3 instanceof AllAppsHost) {
                        if (LauncherStateTransitionAnimation.this.mLauncher != null && LauncherStateTransitionAnimation.this.mLauncher.isInState(LauncherState.ALL_APPS)) {
                            return;
                        }
                        if (LauncherStateTransitionAnimation.this.mLauncher != null) {
                            fromView.setVisibility(4);
                        }
                    } else {
                        view3.setVisibility(8);
                    }
                    LauncherStateTransitionAnimation.this.dispatchOnLauncherTransitionEnd(fromView, animated, true);
                    LauncherStateTransitionAnimation.this.dispatchOnLauncherTransitionEnd(workspace3, animated, true);
                    Runnable runnable = onCompleteRunnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                    for (View view4 : map3.keySet()) {
                        if (((Integer) map3.get(view4)).intValue() == 1) {
                            view4.setLayerType(0, null);
                        }
                    }
                    View view5 = contentView;
                    if (view5 != null) {
                        view5.setTranslationX(0.0f);
                        contentView.setTranslationY(0.0f);
                        contentView.setAlpha(1.0f);
                    }
                    View view6 = overlaySearchBarView;
                    if (view6 != null) {
                        view6.setAlpha(1.0f);
                    }
                    LauncherStateTransitionAnimation.this.mStateAnimation = null;
                    pCb.onTransitionComplete();
                }
            });
            final AnimatorSet animatorSet = this.mStateAnimation;
            view2.post(new Runnable() { // from class: com.android.launcher3.LauncherStateTransitionAnimation.9
                @Override // java.lang.Runnable
                public void run() {
                    if (LauncherStateTransitionAnimation.this.mStateAnimation != animatorSet) {
                        return;
                    }
                    LauncherStateTransitionAnimation.this.dispatchOnLauncherTransitionStart(fromView, animated, true);
                    LauncherStateTransitionAnimation.this.dispatchOnLauncherTransitionStart(workspace4, animated, true);
                    boolean zIsLmpOrAbove2 = Utilities.isLmpOrAbove();
                    for (View view3 : map3.keySet()) {
                        if (((Integer) map3.get(view3)).intValue() == 1) {
                            view3.setLayerType(2, null);
                        }
                        if (zIsLmpOrAbove2 && Utilities.isViewAttachedToWindow(view3)) {
                            view3.buildLayer();
                        }
                    }
                    LauncherStateTransitionAnimation.this.mStateAnimation.start();
                }
            });
            return;
        }
        if (onCompleteRunnable != null) {
            onCompleteRunnable.run();
        }
        view2.setVisibility(8);
        dispatchOnLauncherTransitionPrepare(view2, z2, true);
        dispatchOnLauncherTransitionStart(view2, z2, true);
        dispatchOnLauncherTransitionEnd(view2, z2, true);
        dispatchOnLauncherTransitionPrepare(workspace2, z2, true);
        dispatchOnLauncherTransitionStart(workspace2, z2, true);
        dispatchOnLauncherTransitionEnd(workspace2, z2, true);
        pCb.onTransitionComplete();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    void dispatchOnLauncherTransitionPrepare(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionPrepare(this.mLauncher, animated, toWorkspace);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    void dispatchOnLauncherTransitionStart(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionStart(this.mLauncher, animated, toWorkspace);
        }
        dispatchOnLauncherTransitionStep(v, 0.0f);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    void dispatchOnLauncherTransitionStep(View v, float t) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionStep(this.mLauncher, t);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    void dispatchOnLauncherTransitionEnd(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionEnd(this.mLauncher, animated, toWorkspace);
        }
        dispatchOnLauncherTransitionStep(v, 1.0f);
    }

    private void cancelAnimation() {
        AnimatorSet animatorSet = this.mStateAnimation;
        if (animatorSet != null) {
            animatorSet.setDuration(0L);
            this.mStateAnimation.cancel();
            this.mStateAnimation = null;
        }
    }
}
