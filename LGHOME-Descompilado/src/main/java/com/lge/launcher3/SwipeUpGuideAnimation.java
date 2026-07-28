package com.lge.launcher3;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherState;
import com.android.launcher3.views.BaseDragLayer;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;

/* JADX INFO: loaded from: classes.dex */
public class SwipeUpGuideAnimation {
    private static boolean sSwipeUpGuideAnimation;
    LauncherExtension mLauncher;
    private View mSwipeUpGuideArrow1;
    private View mSwipeUpGuideArrow2;
    private View mSwpieUpGuideText;

    public SwipeUpGuideAnimation(LauncherExtension launcher) {
        this.mLauncher = launcher;
    }

    public void startSwipeUpGuideAnimation() {
        if (SharedPreferencesManager.getBoolean(this.mLauncher, 0, SharedPreferencesConst.SwipeUpKey.IS_ENABLED, true)) {
            pageIndicatorDownAnim();
        }
    }

    public void cancelSwipeUpAnim() {
        sSwipeUpGuideAnimation = false;
        cancelViewAnim(this.mSwipeUpGuideArrow1);
        cancelViewAnim(this.mSwipeUpGuideArrow2);
        cancelViewAnim(this.mSwpieUpGuideText);
        if (this.mLauncher.isInState(LauncherState.ALL_APPS) || this.mLauncher.isInState(LauncherState.OVERVIEW)) {
            this.mLauncher.getPageindicator().setAlpha(0.0f);
        } else {
            this.mLauncher.getPageindicator().setAlpha(1.0f);
        }
    }

    private void cancelViewAnim(View view) {
        if (view == null || view.getAnimation() == null) {
            return;
        }
        view.getAnimation().cancel();
    }

    private void pageIndicatorDownAnim() {
        sSwipeUpGuideAnimation = true;
        this.mLauncher.getPageindicator().setAlpha(1.0f);
        Animation animationLoadAnimation = AnimationUtils.loadAnimation(this.mLauncher.getApplicationContext(), R.anim.down_indicator);
        animationLoadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.lge.launcher3.SwipeUpGuideAnimation.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() { // from class: com.lge.launcher3.SwipeUpGuideAnimation.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (SwipeUpGuideAnimation.this.mLauncher != null && SwipeUpGuideAnimation.this.mLauncher.getWorkspace() != null && SwipeUpGuideAnimation.this.mLauncher.getWorkspace().getOverlayTranslation() == 0.0f) {
                            LGLog.i("SwipeUpGuideAnimation", "start swipeUpGuideArrowAnim");
                            SwipeUpGuideAnimation.this.swipeUpGuideArrowAnim();
                        } else {
                            LGLog.i("SwipeUpGuideAnimation", "skip swipeUpGuideArrowAnim");
                        }
                    }
                }, 200L);
                SwipeUpGuideAnimation.this.mLauncher.getPageindicator().setAlpha(0.0f);
            }
        });
        this.mLauncher.getPageindicator().startAnimation(animationLoadAnimation);
    }

    public void swipeUpGuideArrowAnim() {
        int dimensionPixelSize;
        this.mSwipeUpGuideArrow1 = this.mLauncher.findViewById(R.id.swipeup_guide_arrow1);
        this.mSwipeUpGuideArrow2 = this.mLauncher.findViewById(R.id.swipeup_guide_arrow2);
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        if (deviceProfile.allowRotation) {
            if (deviceProfile.isPhone && deviceProfile.isLandscape) {
                dimensionPixelSize = deviceProfile.pageIndicatorHeightPx;
            } else {
                dimensionPixelSize = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.swipe_up_view_height);
            }
            ViewGroup.LayoutParams layoutParams = this.mSwipeUpGuideArrow1.getLayoutParams();
            layoutParams.height = dimensionPixelSize;
            this.mSwipeUpGuideArrow1.setLayoutParams(layoutParams);
            ViewGroup.LayoutParams layoutParams2 = this.mSwipeUpGuideArrow2.getLayoutParams();
            layoutParams2.height = dimensionPixelSize;
            this.mSwipeUpGuideArrow2.setLayoutParams(layoutParams2);
            View viewFindViewById = this.mLauncher.findViewById(R.id.swipeup_arrow);
            BaseDragLayer.LayoutParams layoutParams3 = (BaseDragLayer.LayoutParams) viewFindViewById.getLayoutParams();
            if (deviceProfile.isPhone && deviceProfile.isLandscape) {
                layoutParams3.bottomMargin = deviceProfile.hotseatBarSizePx;
            } else {
                layoutParams3.bottomMargin = deviceProfile.hotseatBarSizePx + WindowUtils.getNavigationBarHeight(this.mLauncher.getApplicationContext());
            }
            viewFindViewById.setLayoutParams(layoutParams3);
        }
        Animation animationLoadAnimation = AnimationUtils.loadAnimation(this.mLauncher.getApplicationContext(), R.anim.swipe_up_arrow_anim);
        animationLoadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.lge.launcher3.SwipeUpGuideAnimation.2
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
                if (SwipeUpGuideAnimation.sSwipeUpGuideAnimation) {
                    SwipeUpGuideAnimation.this.mSwipeUpGuideArrow1.setVisibility(0);
                } else {
                    SwipeUpGuideAnimation.this.mSwipeUpGuideArrow1.setVisibility(8);
                }
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                SwipeUpGuideAnimation.this.mSwipeUpGuideArrow1.clearAnimation();
                SwipeUpGuideAnimation.this.mSwipeUpGuideArrow1.setVisibility(8);
            }
        });
        Animation animationLoadAnimation2 = AnimationUtils.loadAnimation(this.mLauncher.getApplicationContext(), R.anim.swipe_up_arrow_anim2);
        animationLoadAnimation2.setAnimationListener(new Animation.AnimationListener() { // from class: com.lge.launcher3.SwipeUpGuideAnimation.3
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
                if (SwipeUpGuideAnimation.sSwipeUpGuideAnimation) {
                    SwipeUpGuideAnimation.this.mSwipeUpGuideArrow2.setVisibility(0);
                } else {
                    SwipeUpGuideAnimation.this.mSwipeUpGuideArrow2.setVisibility(8);
                }
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                SwipeUpGuideAnimation.this.mSwipeUpGuideArrow2.clearAnimation();
                SwipeUpGuideAnimation.this.mSwipeUpGuideArrow2.setVisibility(8);
                if (SwipeUpGuideAnimation.sSwipeUpGuideAnimation) {
                    SwipeUpGuideAnimation.this.swipeUpGuideTextAnim();
                }
            }
        });
        animationLoadAnimation.setFillAfter(true);
        animationLoadAnimation2.setFillAfter(true);
        View view = this.mSwipeUpGuideArrow1;
        if (view == null || this.mSwipeUpGuideArrow2 == null) {
            return;
        }
        view.startAnimation(animationLoadAnimation);
        this.mSwipeUpGuideArrow2.startAnimation(animationLoadAnimation2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void swipeUpGuideTextAnim() {
        LauncherExtension launcherExtension = this.mLauncher;
        if (launcherExtension == null) {
            return;
        }
        View viewFindViewById = launcherExtension.findViewById(R.id.swipeup_guide_text);
        this.mSwpieUpGuideText = viewFindViewById;
        if (viewFindViewById == null) {
            return;
        }
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        if (deviceProfile.allowRotation) {
            BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) this.mSwpieUpGuideText.getLayoutParams();
            if (deviceProfile.isPhone && deviceProfile.isLandscape) {
                layoutParams.bottomMargin = deviceProfile.hotseatBarSizePx;
                layoutParams.height = deviceProfile.pageIndicatorHeightPx;
            } else if (deviceProfile.isPhone && !deviceProfile.isLandscape) {
                layoutParams.bottomMargin = deviceProfile.hotseatBarSizePx + WindowUtils.getNavigationBarHeight(this.mLauncher.getApplicationContext()) + this.mLauncher.getResources().getDimensionPixelSize(R.dimen.swipe_up_guide_text_bottom_margin);
                layoutParams.height = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.swipe_up_guide_text_height);
            } else {
                layoutParams.bottomMargin = deviceProfile.hotseatBarSizePx + WindowUtils.getNavigationBarHeight(this.mLauncher.getApplicationContext());
                layoutParams.height = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.swipe_up_view_height);
            }
            this.mSwpieUpGuideText.setLayoutParams(layoutParams);
        }
        Animation animationLoadAnimation = AnimationUtils.loadAnimation(this.mLauncher.getApplicationContext(), R.anim.swipe_up_apps_anim);
        animationLoadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.lge.launcher3.SwipeUpGuideAnimation.4
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
                if (SwipeUpGuideAnimation.sSwipeUpGuideAnimation) {
                    SwipeUpGuideAnimation.this.mSwpieUpGuideText.setVisibility(0);
                } else {
                    SwipeUpGuideAnimation.this.mSwpieUpGuideText.setVisibility(8);
                }
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                if (SwipeUpGuideAnimation.sSwipeUpGuideAnimation) {
                    SwipeUpGuideAnimation.this.mSwpieUpGuideText.clearAnimation();
                    SwipeUpGuideAnimation.this.mSwpieUpGuideText.setVisibility(8);
                    SwipeUpGuideAnimation.this.pageIndicatorUpAnim();
                } else {
                    SwipeUpGuideAnimation.this.mSwpieUpGuideText.clearAnimation();
                    SwipeUpGuideAnimation.this.mSwpieUpGuideText.setVisibility(8);
                    SwipeUpGuideAnimation.this.pageIndicatorUpAnim();
                }
            }
        });
        animationLoadAnimation.setFillAfter(true);
        View view = this.mSwpieUpGuideText;
        if (view != null) {
            view.startAnimation(animationLoadAnimation);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pageIndicatorUpAnim() {
        LauncherExtension launcherExtension = this.mLauncher;
        if (launcherExtension == null || launcherExtension.getPageindicator() == null) {
            return;
        }
        Animation animationLoadAnimation = AnimationUtils.loadAnimation(this.mLauncher.getApplicationContext(), R.anim.up_indicator);
        animationLoadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.lge.launcher3.SwipeUpGuideAnimation.5
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
                SwipeUpGuideAnimation.this.mLauncher.getPageindicator().setAlpha(1.0f);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                SwipeUpGuideAnimation.this.mLauncher.getPageindicator().clearAnimation();
                SwipeUpGuideAnimation.sSwipeUpGuideAnimation = false;
            }
        });
        animationLoadAnimation.setFillAfter(true);
        this.mLauncher.getPageindicator().startAnimation(animationLoadAnimation);
    }

    public static boolean isInSwipUpGuideAnination() {
        return sSwipeUpGuideAnimation;
    }
}
