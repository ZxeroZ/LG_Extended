package com.lge.launcher3.wallpaperblur;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.WindowManagerGlobal;
import android.widget.ImageView;
import com.android.launcher3.Launcher;
import com.android.launcher3.Workspace;
import com.lge.blurengine2.LGBlurView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.wallpaperblur.BlurInOutAnimator;
import com.lge.launcher3.wallpaperblur.FadeInOutAnimator;
import com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.AdaptiveColorEngine;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine;

/* JADX INFO: loaded from: classes.dex */
public class HomescreenBlurManager implements WallpaperBlurredImageController.OnWallpaperChangeListener {
    public static final boolean DEBUG = false;
    public static final String TAG = "HomescreenBlurManager";
    private static HomescreenBlurManager sInstance;
    private AnimatorController mAnimatorController;
    private Context mContext;
    private Launcher mLauncher = null;
    int mRadius = 0;
    private ImageView mBackgroundViewInTopDragLayer = null;
    private ImageView mBackgroundViewInBottomRootView = null;
    private ImageView mBackgroundViewInMiddleRootView = null;
    private LGBlurView mBackgroundBlurView2InBottomRootView = null;

    public enum BackgroundType {
        TOP_DRAGLAYER(true),
        BOTTOM_ROOTVIEW(false),
        MIDDLE_ROOTVIEW(true),
        TOP_DRAGLAYER_DIM(true);

        private boolean mIsSupportLiveWallpaperMode;

        BackgroundType(boolean supportLiveWallpaperMode) {
            this.mIsSupportLiveWallpaperMode = false;
            this.mIsSupportLiveWallpaperMode = supportLiveWallpaperMode;
        }

        public boolean isSupportLiveWallpaperMode() {
            return this.mIsSupportLiveWallpaperMode;
        }
    }

    public static HomescreenBlurManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new HomescreenBlurManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private HomescreenBlurManager(Context context) {
        this.mContext = null;
        this.mAnimatorController = null;
        LGLog.i(TAG, "Create a new HomescreenBlurManager instance.");
        this.mContext = context;
        this.mAnimatorController = new AnimatorController();
    }

    public void setLauncher(Launcher launcher) {
        if (launcher == null) {
            return;
        }
        this.mLauncher = launcher;
        WallpaperBlurredImageController.getInstance(this.mContext).setLauncher(this.mLauncher);
        WallpaperBlurredImageController.getInstance(this.mContext).addOnWallpaperChangeListener(this);
        ImageView imageView = (ImageView) this.mLauncher.findViewById(R.id.blur_background_in_top_of_draglayer);
        this.mBackgroundViewInTopDragLayer = imageView;
        imageView.setAlpha(0.0f);
        this.mBackgroundViewInTopDragLayer.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView imageView2 = (ImageView) this.mLauncher.findViewById(R.id.blur_background_in_bottom_of_rootview);
        this.mBackgroundViewInBottomRootView = imageView2;
        imageView2.setAlpha(0.0f);
        this.mBackgroundViewInBottomRootView.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView imageView3 = (ImageView) this.mLauncher.findViewById(R.id.blur_background_in_middle_of_rootview);
        this.mBackgroundViewInMiddleRootView = imageView3;
        imageView3.setAlpha(0.0f);
        this.mBackgroundViewInMiddleRootView.setScaleType(ImageView.ScaleType.FIT_XY);
        LGBlurView lGBlurView = null;
        this.mBackgroundBlurView2InBottomRootView = null;
        if (0 != 0) {
            lGBlurView.setHide(true);
        }
    }

    public void showBackground(BackgroundType backgroundType, int duration) {
        if (isDisabled()) {
            return;
        }
        if (isLiveWallpaperMode()) {
            setScreenShotWallpaper();
        }
        startFadeInOutAnimation(backgroundType, FadeInOutAnimator.FadeType.FADE_IN, duration);
    }

    public void hideBackground(BackgroundType backgroundType, int duration) {
        if (isDisabled()) {
            return;
        }
        if (!isLiveWallpaperMode() || backgroundType.isSupportLiveWallpaperMode()) {
            startFadeInOutAnimation(backgroundType, FadeInOutAnimator.FadeType.FADE_OUT, duration);
        }
    }

    public void showBackgroundWithNoAnim(BackgroundType backgroundType) {
        if (isDisabled()) {
            return;
        }
        if (isLiveWallpaperMode()) {
            setScreenShotWallpaper();
        }
        startFadeInOutAnimation(backgroundType, FadeInOutAnimator.FadeType.SHOW_NOANIM, 0);
    }

    public void showBackgroundWithScale(BackgroundType backgroundType, int duration) {
        if (isDisabled()) {
            return;
        }
        if (isLiveWallpaperMode()) {
            setScreenShotWallpaper();
        }
        startFadeInOutAnimation(backgroundType, FadeInOutAnimator.FadeType.FADEIN_SCALEUP, duration);
    }

    public void hideBackgroundWithScale(BackgroundType backgroundType, int duration) {
        if (isDisabled()) {
            return;
        }
        if (!isLiveWallpaperMode() || backgroundType.isSupportLiveWallpaperMode()) {
            startFadeInOutAnimation(backgroundType, FadeInOutAnimator.FadeType.FADEOUT_SCALEDOWN, duration);
        }
    }

    public void hideBackgroundWithNoAnim(BackgroundType backgroundType) {
        if (isDisabled()) {
            return;
        }
        if (!isLiveWallpaperMode() || backgroundType.isSupportLiveWallpaperMode()) {
            startFadeInOutAnimation(backgroundType, FadeInOutAnimator.FadeType.HIDE_NOANIM, 0);
        }
    }

    public void showBackgroundWithBlurAnim(BackgroundType backgroundType, int duration, boolean screenshot, boolean needUpdate) {
        if (isDisabled()) {
            return;
        }
        if (!isLiveWallpaperMode() || backgroundType.isSupportLiveWallpaperMode()) {
            if (!LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue() || StaticBlurEngine.getInstance().isPowerSaveEnabled(this.mContext)) {
                startFadeInOutAnimation(backgroundType, FadeInOutAnimator.FadeType.FADE_IN, duration);
            } else {
                startBlurInOutAnimation(backgroundType, BlurInOutAnimator.BlurType.BLUR_IN, duration, screenshot, needUpdate);
            }
        }
    }

    public void hideBackgroundWithBlurAnim(BackgroundType backgroundType, int duration, boolean screenshot, boolean needUpdate) {
        if (isDisabled()) {
            return;
        }
        if ((!isLiveWallpaperMode() || backgroundType.isSupportLiveWallpaperMode()) && LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue()) {
            if (!LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue() || StaticBlurEngine.getInstance().isPowerSaveEnabled(this.mContext)) {
                startFadeInOutAnimation(backgroundType, FadeInOutAnimator.FadeType.FADE_OUT, duration);
            } else {
                startBlurInOutAnimation(backgroundType, BlurInOutAnimator.BlurType.BLUR_OUT, duration, screenshot, needUpdate);
            }
        }
    }

    public void clearBackground() {
        if (isDisabled()) {
            return;
        }
        LGLog.i(TAG, "clearBackground()");
        this.mAnimatorController.clear();
    }

    private void startFadeInOutAnimation(BackgroundType backgoundType, FadeInOutAnimator.FadeType type, int duration) {
        ImageView imageView;
        int i = AnonymousClass2.$SwitchMap$com$lge$launcher3$wallpaperblur$HomescreenBlurManager$BackgroundType[backgoundType.ordinal()];
        if (i == 1 || i == 2) {
            imageView = this.mBackgroundViewInTopDragLayer;
        } else if (i == 3) {
            imageView = this.mBackgroundViewInBottomRootView;
        } else {
            imageView = i != 4 ? null : this.mBackgroundViewInMiddleRootView;
        }
        LGLog.i(TAG, String.format("startFadeInOutAnimation() : backgoundType(%s), type(%s), duration(%d), backgoundView(%s), isLiveWallpaperMode(%s)", backgoundType, type, Integer.valueOf(duration), imageView, Boolean.valueOf(isLiveWallpaperMode())));
        if (imageView == null) {
            return;
        }
        this.mAnimatorController.start(imageView, backgoundType, type, duration);
    }

    private void startBlurInOutAnimation(BackgroundType backgoundType, BlurInOutAnimator.BlurType type, int duration, boolean screenshot, boolean needUpdate) {
        ImageView imageView;
        if (this.mAnimatorController.isBlurAnimatorRunning()) {
            return;
        }
        int i = AnonymousClass2.$SwitchMap$com$lge$launcher3$wallpaperblur$HomescreenBlurManager$BackgroundType[backgoundType.ordinal()];
        if (i == 1 || i == 2) {
            imageView = this.mBackgroundViewInTopDragLayer;
        } else if (i == 3) {
            imageView = this.mBackgroundViewInBottomRootView;
        } else {
            imageView = i != 4 ? null : this.mBackgroundViewInMiddleRootView;
        }
        ImageView imageView2 = imageView;
        LGLog.i(TAG, String.format("startBlurInOutAnimation() : backgoundType(%s), type(%s), duration(%d), isLiveWallpaperMode(%s), screenshot(%b), needUpdate(%b)", backgoundType, type, Integer.valueOf(duration), Boolean.valueOf(isLiveWallpaperMode()), Boolean.valueOf(screenshot), Boolean.valueOf(needUpdate)));
        if (imageView2 == null) {
            return;
        }
        this.mAnimatorController.start(imageView2, backgoundType, type, duration, screenshot, needUpdate);
    }

    public Animator.AnimatorListener getWorkspaceStateAnimationListener(final Workspace.State fromState, final Workspace.State toState, final int duration) {
        return new AnimatorListenerAdapter() { // from class: com.lge.launcher3.wallpaperblur.HomescreenBlurManager.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                if (HomescreenBlurManager.this.isDisabled()) {
                    return;
                }
                Workspace workspace = HomescreenBlurManager.this.mLauncher != null ? HomescreenBlurManager.this.mLauncher.getWorkspace() : null;
                if (workspace == null) {
                    return;
                }
                if (workspace.getOpenFolder() != null) {
                    return;
                }
                int i = AnonymousClass2.$SwitchMap$com$android$launcher3$Workspace$State[toState.ordinal()];
                if (i == 1 || i == 2 || i == 3) {
                    HomescreenBlurManager.this.showBackground(BackgroundType.BOTTOM_ROOTVIEW, duration);
                    return;
                }
                if (i == 4 || i == 5) {
                    if (HomescreenBlurManager.this.isLiveWallpaperMode()) {
                        HomescreenBlurManager.this.clearBackground();
                    } else {
                        HomescreenBlurManager.this.hideBackground(BackgroundType.BOTTOM_ROOTVIEW, duration);
                    }
                }
            }
        };
    }

    public void stopRealTimeBlur() {
        if (isDisabled()) {
            return;
        }
        this.mRadius = 0;
        WallpaperBlurredImageController.getInstance(this.mContext).stopRealTimeBlur();
    }

    public void updateBackgroundViewContents() {
        updateBackgroundViewContents(false);
    }

    public void updateBackgroundViewContents(boolean screenshot) {
        if (isDisabled()) {
            return;
        }
        this.mAnimatorController.updateBackgroundViewContents(screenshot);
    }

    public void updateBackgroundViewContents(float alpha) {
        if (isDisabled()) {
            return;
        }
        this.mAnimatorController.updateBackgroundViewContents(alpha);
    }

    private class AnimatorController {
        private BlurInOutAnimator mBlurInOutAnimator;
        private FadeInOutAnimator mFadeInOutAnimator;
        private FadeInOutAnimator.FadeType mFadeType = null;
        private BlurInOutAnimator.BlurType mBlurType = null;
        private ImageView mBackgroundView = null;
        private BackgroundType mBackgroundType = null;
        private Bitmap mBackgroundViewImage = null;

        public AnimatorController() {
            this.mFadeInOutAnimator = null;
            this.mBlurInOutAnimator = null;
            FadeInOutAnimator fadeInOutAnimator = new FadeInOutAnimator();
            this.mFadeInOutAnimator = fadeInOutAnimator;
            fadeInOutAnimator.addListener(new FadeInOutAnimator.FadeInOutAnimatorListener() { // from class: com.lge.launcher3.wallpaperblur.HomescreenBlurManager.AnimatorController.1
                @Override // com.lge.launcher3.wallpaperblur.FadeInOutAnimator.FadeInOutAnimatorListener
                public void onAnimationUpdate(ValueAnimator animation, FadeInOutAnimator.FadeType type) {
                }

                @Override // com.lge.launcher3.wallpaperblur.FadeInOutAnimator.FadeInOutAnimatorListener
                public void onAnimationStart(Animator animation, FadeInOutAnimator.FadeType type) {
                    LGLog.i(HomescreenBlurManager.TAG, String.format("%s onAnimationStart()", type));
                    if (type == FadeInOutAnimator.FadeType.FADE_IN || type == FadeInOutAnimator.FadeType.FADEIN_SCALEUP) {
                        AnimatorController.this.updateBackgroundViewContents();
                        if (AnimatorController.this.mBackgroundView != null) {
                            AnimatorController.this.mBackgroundView.setVisibility(0);
                        }
                    }
                    AnimatorController.this.mFadeType = type;
                }

                @Override // com.lge.launcher3.wallpaperblur.FadeInOutAnimator.FadeInOutAnimatorListener
                public void onAnimationEnd(Animator animation, FadeInOutAnimator.FadeType type) {
                    LGLog.i(HomescreenBlurManager.TAG, String.format("%s onAnimationEnd()", type));
                    if (type == FadeInOutAnimator.FadeType.FADE_OUT || type == FadeInOutAnimator.FadeType.FADEOUT_SCALEDOWN) {
                        AnimatorController.this.clearBackgroundView();
                    }
                    AnimatorController.this.mFadeType = null;
                }
            });
            if (LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue()) {
                BlurInOutAnimator blurInOutAnimator = new BlurInOutAnimator(HomescreenBlurManager.this.mContext);
                this.mBlurInOutAnimator = blurInOutAnimator;
                blurInOutAnimator.addListener(new BlurInOutAnimator.BlurInOutAnimatorListener() { // from class: com.lge.launcher3.wallpaperblur.HomescreenBlurManager.AnimatorController.2
                    @Override // com.lge.launcher3.wallpaperblur.BlurInOutAnimator.BlurInOutAnimatorListener
                    public void onAnimationUpdate(ValueAnimator animation, BlurInOutAnimator.BlurType type) {
                    }

                    @Override // com.lge.launcher3.wallpaperblur.BlurInOutAnimator.BlurInOutAnimatorListener
                    public void onAnimationStart(Animator animation, BlurInOutAnimator.BlurType type) {
                        LGLog.i(HomescreenBlurManager.TAG, String.format("%s onAnimationStart()", type));
                        if (type == BlurInOutAnimator.BlurType.BLUR_IN) {
                            AnimatorController.this.updateBackgroundViewContents(0.3f);
                            if (AnimatorController.this.mBackgroundView != null) {
                                AnimatorController.this.mBackgroundView.setVisibility(0);
                            }
                        }
                    }

                    @Override // com.lge.launcher3.wallpaperblur.BlurInOutAnimator.BlurInOutAnimatorListener
                    public void onAnimationEnd(Animator animation, BlurInOutAnimator.BlurType type) {
                        LGLog.i(HomescreenBlurManager.TAG, String.format("%s onAnimationEnd()", type));
                        if (type != BlurInOutAnimator.BlurType.BLUR_IN && type == BlurInOutAnimator.BlurType.BLUR_OUT) {
                            AnimatorController.this.clear();
                        }
                        AnimatorController.this.mBlurType = null;
                    }
                });
            }
        }

        public void start(ImageView backgroundView, BackgroundType backgoundType, FadeInOutAnimator.FadeType type, int duration) {
            if (this.mBackgroundView != backgroundView) {
                clear();
            }
            this.mBackgroundView = backgroundView;
            this.mBackgroundType = backgoundType;
            if (!shouldNeedAnimation(type)) {
                if (type == FadeInOutAnimator.FadeType.HIDE_NOANIM || type == FadeInOutAnimator.FadeType.FADE_OUT || type == FadeInOutAnimator.FadeType.FADEOUT_SCALEDOWN) {
                    clear();
                    return;
                }
                return;
            }
            if (type == FadeInOutAnimator.FadeType.FADEIN_SCALEUP) {
                this.mFadeInOutAnimator.setBGPivotX(HomescreenBlurManager.this.mLauncher.getBlurBGPivotX());
                this.mFadeInOutAnimator.setBGPivotY(HomescreenBlurManager.this.mLauncher.getBlurBGPivotY());
            }
            this.mFadeInOutAnimator.setTargetView(backgroundView);
            this.mFadeInOutAnimator.start(type, duration);
        }

        public void start(ImageView backgroundView, BackgroundType backgoundType, BlurInOutAnimator.BlurType type, int duration, boolean screenshot, boolean needUpdate) {
            if (this.mBlurInOutAnimator == null) {
                LGLog.i(HomescreenBlurManager.TAG, "start(): mBlurInOutAnimator is null");
                return;
            }
            if (this.mBackgroundView != backgroundView) {
                clear();
            }
            if (this.mBlurType != null) {
                return;
            }
            this.mBackgroundView = backgroundView;
            this.mBackgroundType = backgoundType;
            this.mBlurType = type;
            this.mBlurInOutAnimator.setTargetView(backgroundView);
            if (needUpdate) {
                if (screenshot) {
                    this.mBlurInOutAnimator.setScreenshotBuffer(2);
                } else {
                    this.mBlurInOutAnimator.setBitmap(WallpaperBlurredImageController.getInstance(HomescreenBlurManager.this.mContext).getWallpaperImageForCurrentWorkspace());
                }
            }
            this.mBlurInOutAnimator.start(type, duration);
        }

        public boolean isBlurAnimatorRunning() {
            BlurInOutAnimator blurInOutAnimator = this.mBlurInOutAnimator;
            return (blurInOutAnimator != null && blurInOutAnimator.isRunning()) || this.mBlurType != null;
        }

        private boolean shouldNeedAnimation(FadeInOutAnimator.FadeType type) {
            boolean z = this.mBackgroundView.getVisibility() == 0;
            int i = AnonymousClass2.$SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType[type.ordinal()];
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        if (this.mFadeType == FadeInOutAnimator.FadeType.FADEIN_SCALEUP || (this.mFadeType == null && z)) {
                            return false;
                        }
                    } else if (i != 4 || this.mFadeType == FadeInOutAnimator.FadeType.FADEOUT_SCALEDOWN || (this.mFadeType == null && !z)) {
                        return false;
                    }
                } else if (this.mFadeType == FadeInOutAnimator.FadeType.FADE_OUT || (this.mFadeType == null && !z)) {
                    return false;
                }
            } else if (this.mFadeType == FadeInOutAnimator.FadeType.FADE_IN || (this.mFadeType == null && z)) {
                return false;
            }
            return true;
        }

        public void updateBackgroundViewContents() {
            updateBackgroundViewContents(false);
        }

        public void updateBackgroundViewContents(boolean screenshot) {
            if (this.mBackgroundView == null || this.mBackgroundType == null) {
                return;
            }
            this.mBackgroundViewImage = WallpaperBlurredImageController.getInstance(HomescreenBlurManager.this.mContext).getBlurredImageForCurrentWorkspace(screenshot);
            LGLog.i(HomescreenBlurManager.TAG, String.format("updateBackgroundViewContents() : mBackgroundViewImage(%s)", this.mBackgroundViewImage));
            LGLog.i(HomescreenBlurManager.TAG, "mBackgroundView size : " + this.mBackgroundView);
            ImageView imageView = this.mBackgroundView;
            if (imageView != null) {
                imageView.setImageBitmap(this.mBackgroundViewImage);
            }
            if (LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_EDITMODE_UI.getValue()) {
                if (this.mBackgroundType == BackgroundType.TOP_DRAGLAYER_DIM) {
                    this.mBackgroundView.setColorFilter(HomescreenBlurManager.this.mContext.getResources().getColor(R.color.wallpaper_blur_dim_color));
                } else {
                    this.mBackgroundView.clearColorFilter();
                }
            }
        }

        public void updateBackgroundViewContents(float alpha) {
            if (this.mBackgroundView == null || this.mBackgroundType == null) {
                return;
            }
            if (!HomescreenBlurManager.this.isLiveWallpaperMode()) {
                int i = (int) (alpha * 60.0f);
                if (i > 60) {
                    i = 60;
                }
                if (i % 2 == 1) {
                    i++;
                }
                if (i == HomescreenBlurManager.this.mRadius) {
                    return;
                }
                HomescreenBlurManager.this.mRadius = i;
                this.mBackgroundView.setImageBitmap(WallpaperBlurredImageController.getInstance(HomescreenBlurManager.this.mContext).getBlurredImageForCurrentWorkspace(i));
            } else if (!this.mBackgroundType.isSupportLiveWallpaperMode()) {
                clear();
                return;
            } else {
                this.mBackgroundView.setImageBitmap(null);
                this.mBackgroundView.setBackgroundColor(HomescreenBlurManager.this.mContext.getResources().getColor(R.color.folder_background_dim_in_livewallpaper));
            }
            if (LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_EDITMODE_UI.getValue()) {
                if (this.mBackgroundType == BackgroundType.TOP_DRAGLAYER_DIM) {
                    this.mBackgroundView.setColorFilter(HomescreenBlurManager.this.mContext.getResources().getColor(R.color.wallpaper_blur_dim_color));
                } else {
                    this.mBackgroundView.clearColorFilter();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearBackgroundView() {
            HomescreenBlurManager.this.stopRealTimeBlur();
            if (this.mBackgroundView != null) {
                LGLog.i(HomescreenBlurManager.TAG, "clearBackgroundView - " + this.mBackgroundView);
                this.mBackgroundView.setAlpha(0.0f);
                this.mBackgroundView.setVisibility(8);
                this.mBackgroundView.setImageBitmap(null);
                this.mBackgroundView.setBackgroundColor(0);
                this.mBackgroundView = null;
            }
            Bitmap bitmap = this.mBackgroundViewImage;
            if (bitmap != null) {
                bitmap.recycle();
                this.mBackgroundViewImage = null;
            }
            this.mBlurType = null;
        }

        public void clear() {
            this.mFadeInOutAnimator.clear();
            BlurInOutAnimator blurInOutAnimator = this.mBlurInOutAnimator;
            if (blurInOutAnimator != null) {
                blurInOutAnimator.clear();
            }
            clearBackgroundView();
        }

        public void startDimAnimation() {
            ImageView imageView;
            if (LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_EDITMODE_UI.getValue() && (imageView = this.mBackgroundView) != null && imageView.getColorFilter() == null) {
                ValueAnimator valueAnimatorOfArgb = ValueAnimator.ofArgb(0, HomescreenBlurManager.this.mContext.getResources().getColor(R.color.wallpaper_blur_dim_color));
                valueAnimatorOfArgb.setDuration(HomescreenBlurManager.this.mContext.getResources().getInteger(R.integer.config_wallpaperBlurDimTime));
                valueAnimatorOfArgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.wallpaperblur.HomescreenBlurManager.AnimatorController.3
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (AnimatorController.this.mBackgroundView != null) {
                            AnimatorController.this.mBackgroundView.setColorFilter(((Integer) animation.getAnimatedValue()).intValue());
                        }
                    }
                });
                valueAnimatorOfArgb.start();
            }
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.wallpaperblur.HomescreenBlurManager$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$Workspace$State;
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType;
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$wallpaperblur$HomescreenBlurManager$BackgroundType;

        static {
            int[] iArr = new int[FadeInOutAnimator.FadeType.values().length];
            $SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType = iArr;
            try {
                iArr[FadeInOutAnimator.FadeType.FADE_IN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType[FadeInOutAnimator.FadeType.FADE_OUT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType[FadeInOutAnimator.FadeType.FADEIN_SCALEUP.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$FadeInOutAnimator$FadeType[FadeInOutAnimator.FadeType.FADEOUT_SCALEDOWN.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            int[] iArr2 = new int[Workspace.State.values().length];
            $SwitchMap$com$android$launcher3$Workspace$State = iArr2;
            try {
                iArr2[Workspace.State.OVERVIEW.ordinal()] = 1;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$launcher3$Workspace$State[Workspace.State.OVERVIEW_HIDDEN.ordinal()] = 2;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$launcher3$Workspace$State[Workspace.State.NORMAL_HIDDEN.ordinal()] = 3;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$launcher3$Workspace$State[Workspace.State.NORMAL.ordinal()] = 4;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$launcher3$Workspace$State[Workspace.State.SPRING_LOADED.ordinal()] = 5;
            } catch (NoSuchFieldError unused9) {
            }
            int[] iArr3 = new int[BackgroundType.values().length];
            $SwitchMap$com$lge$launcher3$wallpaperblur$HomescreenBlurManager$BackgroundType = iArr3;
            try {
                iArr3[BackgroundType.TOP_DRAGLAYER.ordinal()] = 1;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$HomescreenBlurManager$BackgroundType[BackgroundType.TOP_DRAGLAYER_DIM.ordinal()] = 2;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$HomescreenBlurManager$BackgroundType[BackgroundType.BOTTOM_ROOTVIEW.ordinal()] = 3;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$HomescreenBlurManager$BackgroundType[BackgroundType.MIDDLE_ROOTVIEW.ordinal()] = 4;
            } catch (NoSuchFieldError unused13) {
            }
        }
    }

    public boolean isDisabled() {
        return !LGHomeFeature.Config.FEATURE_USE_HOMESCREEN_BLUR.getValue();
    }

    public boolean isLiveWallpaperMode() {
        Context context = this.mContext;
        if (context == null) {
            return false;
        }
        return WallpaperBlurredImageController.getInstance(context).isLiveWallpaperMode();
    }

    @Override // com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController.OnWallpaperChangeListener
    public void onWallpaperChanged() {
        stopRealTimeBlur();
    }

    @Override // com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController.OnWallpaperChangeListener
    public void onWallpaperBlurredImageChanged(int adaptiveColor) {
        AnimatorController animatorController;
        LGLog.i(TAG, String.format("onWallpaperBlurredImageChanged(adpativeColor = %s(%d))", Integer.toHexString(adaptiveColor), Integer.valueOf(adaptiveColor)));
        if (isDisabled() || isLiveWallpaperMode() || (animatorController = this.mAnimatorController) == null) {
            return;
        }
        animatorController.updateBackgroundViewContents();
    }

    public void destroy() {
        LGLog.i(TAG, "Destroy HomescreenBlurManager instance.");
        WallpaperBlurredImageController.getInstance(this.mContext).removeOnWallpaperChangeListener(this);
        AnimatorController animatorController = this.mAnimatorController;
        if (animatorController != null) {
            animatorController.clear();
            this.mAnimatorController = null;
        }
        this.mBackgroundViewInBottomRootView = null;
        this.mBackgroundViewInMiddleRootView = null;
        this.mBackgroundViewInTopDragLayer = null;
        this.mLauncher = null;
        this.mContext = null;
        sInstance = null;
    }

    public View getBackgroundView() {
        AnimatorController animatorController;
        if (isDisabled() || (animatorController = this.mAnimatorController) == null) {
            return null;
        }
        return animatorController.mBackgroundView;
    }

    public void startDimAnimation() {
        AnimatorController animatorController;
        if (isDisabled() || (animatorController = this.mAnimatorController) == null) {
            return;
        }
        animatorController.startDimAnimation();
    }

    public void clearColorFilter() {
        AnimatorController animatorController = this.mAnimatorController;
        if (animatorController == null || animatorController.mBackgroundView == null) {
            return;
        }
        this.mAnimatorController.mBackgroundView.clearColorFilter();
    }

    public void setScreenShotWallpaper() {
        Bitmap bitmapScreenshotWallpaper;
        try {
            bitmapScreenshotWallpaper = WindowManagerGlobal.getWindowManagerService().screenshotWallpaper();
        } catch (Exception e) {
            LGLog.e(TAG, e.getMessage());
            bitmapScreenshotWallpaper = null;
        }
        if (bitmapScreenshotWallpaper != null) {
            AdaptiveColorEngine.getInstance().setBlurImage(bitmapScreenshotWallpaper);
        } else {
            LGLog.w(TAG, "setScreenShotWallpaper : wallpaper is null", new int[0]);
        }
    }

    public void setBlurView2Level(int level) {
        LGBlurView lGBlurView = this.mBackgroundBlurView2InBottomRootView;
        if (lGBlurView != null) {
            boolean hide = lGBlurView.getHide();
            if (level == 0) {
                if (hide) {
                    return;
                }
                this.mBackgroundBlurView2InBottomRootView.setHide(true);
            } else {
                if (hide) {
                    this.mBackgroundBlurView2InBottomRootView.setHide(false);
                }
                this.mBackgroundBlurView2InBottomRootView.setBlurLevel(level);
            }
        }
    }

    public void setBlurView2onStart() {
        LGBlurView lGBlurView = this.mBackgroundBlurView2InBottomRootView;
        if (lGBlurView != null) {
            lGBlurView.onResume();
        }
    }

    public void setBlurView2onStop() {
        LGBlurView lGBlurView = this.mBackgroundBlurView2InBottomRootView;
        if (lGBlurView != null) {
            lGBlurView.onPause();
        }
    }

    public void setBlurView2onResume() {
        LGBlurView lGBlurView = this.mBackgroundBlurView2InBottomRootView;
        if (lGBlurView != null) {
            lGBlurView.onResume();
        }
    }

    public void setBlurView2onPause() {
        LGBlurView lGBlurView = this.mBackgroundBlurView2InBottomRootView;
        if (lGBlurView != null) {
            lGBlurView.onPause();
        }
    }
}
