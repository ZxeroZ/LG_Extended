package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Pair;
import android.util.Property;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAnimationRunner;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.views.FloatingIconView;
import com.android.quickstep.RecentsModel;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.TaskViewUtils;
import com.android.quickstep.util.MultiValueUpdateListener;
import com.android.quickstep.util.RemoteAnimationProvider;
import com.android.quickstep.util.SurfaceTransactionApplier;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.ActivityCompat;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.RemoteAnimationAdapterCompat;
import com.android.systemui.shared.system.RemoteAnimationDefinitionCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.RemoteTransitionCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsHost;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.wallpaperblur.HomescreenBlurManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class QuickstepTransitionManager implements DeviceProfile.OnDeviceProfileChangeListener {
    public static final float ALL_APPS_PROGRESS_OFF_SCREEN = 1.3059858f;
    private static final long APP_LAUNCH_ALPHA_DOWN_DURATION = 40;
    private static final long APP_LAUNCH_ALPHA_DURATION = 50;
    private static final long APP_LAUNCH_ALPHA_START_DELAY = 25;
    private static final long APP_LAUNCH_CURVED_DURATION = 250;
    private static final long APP_LAUNCH_DOWN_CURVED_DURATION = 200;
    private static final long APP_LAUNCH_DOWN_DURATION = 360;
    private static final float APP_LAUNCH_DOWN_DUR_SCALE_FACTOR = 0.8f;
    private static final long APP_LAUNCH_DURATION = 450;
    private static final int CLOSING_TRANSITION_DURATION_MS = 250;
    protected static final int CONTENT_ALPHA_DURATION = 217;
    protected static final int CONTENT_TRANSLATION_DURATION = 350;
    private static final String CONTROL_REMOTE_APP_TRANSITION_PERMISSION = "android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS";
    private static final long CROP_DURATION = 375;
    private static final int LAUNCHER_RESUME_START_DELAY = 100;
    private static final long RADIUS_DURATION = 375;
    public static final int RECENTS_LAUNCH_DURATION = 336;
    public static final int SPLIT_DIVIDER_ANIM_DURATION = 100;
    public static final int SPLIT_LAUNCH_DURATION = 370;
    public static final int STATUS_BAR_TRANSITION_DURATION = 120;
    public static final int STATUS_BAR_TRANSITION_PRE_DELAY = 96;
    private static final String TAG = "QuickstepTransition";
    private final int THUMBNAIL_APP_LAUNCH_TOTAL_DURATION;
    private LauncherAnimationRunner.RemoteAnimationFactory mAppLaunchRunner;
    private final float mClosingWindowTransY;
    private final float mContentTransY;
    private DeviceProfile mDeviceProfile;
    private final DragLayer mDragLayer;
    private final MultiValueAlpha.AlphaProperty mDragLayerAlpha;
    final Handler mHandler;
    private final boolean mIsRtl;
    private LauncherAnimationRunner.RemoteAnimationFactory mKeyguardGoingAwayRunner;
    protected final BaseQuickstepLauncher mLauncher;
    private RemoteTransitionCompat mLauncherOpenTransition;
    private RemoteAnimationProvider mRemoteAnimationProvider;
    private LauncherAnimationRunner.RemoteAnimationFactory mWallpaperOpenRunner;
    private LauncherAnimationRunner.RemoteAnimationFactory mWallpaperOpenTransitionRunner;
    private final float mWorkspaceTransY;
    private final String ICON_ANIM_TAG = "[IconAnim] ";
    private final int NONE = -1;
    private final Interpolator mAppLaunchInterpolator = new PathInterpolator(0.11f, 0.7f, 0.21f, 0.96f);
    private final int THUMBNAIL_APP_CLOSE_TOTAL_DURATION = 350;
    private final int THUMBNAIL_APP_CLOSE_ALPHA_START_DELAY = 270;
    private final int CONTENTLAYER_CLOSE_DURATION = 500;
    private final int WALLPAPER_CLOSE_DURATION = 600;
    private final float WINDOW_CORNER_RADIUS_RATIO = 6.0f;
    private LauncherState mLauncherState = null;
    private boolean mLaunchedFromFolder = false;
    private boolean mIsRemoved = false;
    private ComponentName mSearchedAppComponentName = null;
    private int mLaunchedTaskId = -1;
    private View mIconView = null;
    private float BLUR_LEVEL = 60.0f;
    private final AnimatorListenerAdapter mForceInvisibleListener = new AnimatorListenerAdapter() { // from class: com.android.launcher3.QuickstepTransitionManager.1
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
            QuickstepTransitionManager.this.mLauncher.addForceInvisibleFlag(2);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            QuickstepTransitionManager.this.mLauncher.clearForceInvisibleFlag(2);
        }
    };

    public QuickstepTransitionManager(Context context) {
        BaseQuickstepLauncher baseQuickstepLauncher = (BaseQuickstepLauncher) Launcher.cast(Launcher.getLauncher(context));
        this.mLauncher = baseQuickstepLauncher;
        DragLayer dragLayer = baseQuickstepLauncher.getDragLayer();
        this.mDragLayer = dragLayer;
        this.mDragLayerAlpha = dragLayer.getAlphaProperty(2);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mIsRtl = Utilities.isRtl(baseQuickstepLauncher.getResources());
        this.mDeviceProfile = baseQuickstepLauncher.getDeviceProfile();
        Resources resources = baseQuickstepLauncher.getResources();
        this.mContentTransY = resources.getDimensionPixelSize(R.dimen.content_trans_y);
        this.mWorkspaceTransY = resources.getDimensionPixelSize(R.dimen.workspace_trans_y);
        this.mClosingWindowTransY = resources.getDimensionPixelSize(R.dimen.closing_window_trans_y);
        baseQuickstepLauncher.addOnDeviceProfileChangeListener(this);
        this.THUMBNAIL_APP_LAUNCH_TOTAL_DURATION = com.lge.launcher3.util.Utilities.LOW_CONDITION ? 160 : SPLIT_LAUNCH_DURATION;
        registerRemoteAnimations();
    }

    @Override // com.android.launcher3.DeviceProfile.OnDeviceProfileChangeListener
    public void onDeviceProfileChanged(DeviceProfile dp) {
        this.mDeviceProfile = dp;
    }

    public ActivityOptionsWrapper getActivityLaunchOptions(Launcher launcher, View v) {
        boolean zIsLaunchingFromRecents = isLaunchingFromRecents(v, null);
        RunnableList runnableList = new RunnableList();
        this.mAppLaunchRunner = new AppLaunchAnimationRunner(this.mHandler, v, runnableList);
        LauncherAnimationRunner launcherAnimationRunner = new LauncherAnimationRunner(this.mHandler, this.mAppLaunchRunner, true);
        long j = zIsLaunchingFromRecents ? 336L : APP_LAUNCH_DURATION;
        return new ActivityOptionsWrapper(ActivityOptionsCompat.makeRemoteAnimation(new RemoteAnimationAdapterCompat(launcherAnimationRunner, j, (j - 120) - 96, this.mLauncher.getIApplicationThread())), runnableList);
    }

    protected boolean isLaunchingFromRecents(View v, RemoteAnimationTargetCompat[] targets) {
        return ((LauncherState) this.mLauncher.getStateManager().getState()).overviewUi && TaskViewUtils.findTaskViewToLaunch((RecentsView) this.mLauncher.getOverviewPanel(), v, targets) != null;
    }

    protected void composeRecentsLaunchAnimator(AnimatorSet anim, View v, RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets, boolean launcherClosing) {
        Animator duration;
        AnimatorListenerAdapter animatorListenerAdapter;
        RecentsView recentsView = (RecentsView) this.mLauncher.getOverviewPanel();
        TaskView taskViewFindTaskViewToLaunch = TaskViewUtils.findTaskViewToLaunch((RecentsView) this.mLauncher.getOverviewPanel(), v, appTargets);
        PendingAnimation pendingAnimation = new PendingAnimation(336L);
        TaskViewUtils.createRecentsWindowAnimator(taskViewFindTaskViewToLaunch, !launcherClosing, appTargets, wallpaperTargets, this.mLauncher.getDepthController(), pendingAnimation);
        anim.play(pendingAnimation.buildAnim());
        AnimatorSet target = null;
        if (launcherClosing) {
            duration = recentsView.createAdjacentPageAnimForTaskLaunch(taskViewFindTaskViewToLaunch);
            duration.setInterpolator(Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            duration.setDuration(336L);
            animatorListenerAdapter = new AnimatorListenerAdapter() { // from class: com.android.launcher3.QuickstepTransitionManager.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    QuickstepTransitionManager.this.mLauncher.getStateManager().moveToRestState();
                    QuickstepTransitionManager.this.mLauncher.getStateManager().reapplyState();
                }
            };
        } else {
            AnimatorPlaybackController animatorPlaybackControllerCreateAnimationToNewWorkspace = this.mLauncher.getStateManager().createAnimationToNewWorkspace(LauncherState.NORMAL, 336L);
            animatorPlaybackControllerCreateAnimationToNewWorkspace.dispatchOnStart();
            target = animatorPlaybackControllerCreateAnimationToNewWorkspace.getTarget();
            duration = animatorPlaybackControllerCreateAnimationToNewWorkspace.getAnimationPlayer().setDuration(336L);
            animatorListenerAdapter = new AnimatorListenerAdapter() { // from class: com.android.launcher3.QuickstepTransitionManager.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    QuickstepTransitionManager.this.mLauncher.getStateManager().goToState(LauncherState.NORMAL, false);
                }
            };
        }
        anim.play(duration);
        this.mLauncher.getStateManager().setCurrentAnimation(anim, target);
        anim.addListener(animatorListenerAdapter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void composeIconLaunchAnimator(AnimatorSet anim, View v, RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets, boolean launcherClosing) {
        this.mLauncher.getStateManager().setCurrentAnimation(anim, new Animator[0]);
        Rect windowTargetBounds = getWindowTargetBounds(appTargets);
        boolean z = true;
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : appTargets) {
            if (remoteAnimationTargetCompat.mode == 0) {
                z &= remoteAnimationTargetCompat.isTranslucent;
            }
            if (!z) {
                break;
            }
        }
        anim.play(getOpeningWindowAnimators(v, appTargets, wallpaperTargets, windowTargetBounds, !z));
        if (!com.lge.launcher3.util.Utilities.LOW_CONDITION && launcherClosing) {
            final Pair<AnimatorSet, Runnable> launcherContentAnimator = getLauncherContentAnimator(true, this.mIconView != null ? null : new float[]{0.0f, -this.mContentTransY});
            anim.play((Animator) launcherContentAnimator.first);
            anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.QuickstepTransitionManager.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ((Runnable) launcherContentAnimator.second).run();
                }
            });
            return;
        }
        anim.addListener(new AnonymousClass5());
    }

    /* JADX INFO: renamed from: com.android.launcher3.QuickstepTransitionManager$5, reason: invalid class name */
    class AnonymousClass5 extends AnimatorListenerAdapter {
        AnonymousClass5() {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
            QuickstepTransitionManager.this.mLauncher.addOnResumeCallback(new Runnable() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$5$f7zM88JPHlGMgsGDONmkVNjTGUU
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onAnimationStart$0$QuickstepTransitionManager$5();
                }
            });
        }

        public /* synthetic */ void lambda$onAnimationStart$0$QuickstepTransitionManager$5() {
            ObjectAnimator.ofFloat(QuickstepTransitionManager.this.mLauncher.getDepthController(), DepthController.DEPTH, ((LauncherState) QuickstepTransitionManager.this.mLauncher.getStateManager().getState()).getDepth(QuickstepTransitionManager.this.mLauncher)).start();
        }
    }

    private Rect getWindowTargetBounds(RemoteAnimationTargetCompat[] appTargets) {
        Rect rect = new Rect(0, 0, this.mDeviceProfile.widthPx, this.mDeviceProfile.heightPx);
        if (this.mLauncher.isInMultiWindowMode()) {
            for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : appTargets) {
                if (remoteAnimationTargetCompat.mode == 0) {
                    rect.set(remoteAnimationTargetCompat.screenSpaceBounds);
                    if (remoteAnimationTargetCompat.localBounds != null) {
                        rect.set(remoteAnimationTargetCompat.localBounds);
                    } else {
                        rect.offsetTo(remoteAnimationTargetCompat.position.x, remoteAnimationTargetCompat.position.y);
                    }
                    return rect;
                }
            }
        }
        return rect;
    }

    public void setRemoteAnimationProvider(final RemoteAnimationProvider animationProvider, CancellationSignal cancellationSignal) {
        this.mRemoteAnimationProvider = animationProvider;
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$GLkIHhZQvGsUlvAFL2cqWC__-hQ
            @Override // android.os.CancellationSignal.OnCancelListener
            public final void onCancel() {
                this.f$0.lambda$setRemoteAnimationProvider$0$QuickstepTransitionManager(animationProvider);
            }
        });
    }

    public /* synthetic */ void lambda$setRemoteAnimationProvider$0$QuickstepTransitionManager(RemoteAnimationProvider remoteAnimationProvider) {
        if (remoteAnimationProvider == this.mRemoteAnimationProvider) {
            this.mRemoteAnimationProvider = null;
        }
    }

    public Pair<AnimatorSet, Runnable> getLauncherContentAnimator(boolean isAppOpening, float[] trans) {
        Runnable runnableComposeViewContentAnimator;
        long j;
        float[] fArr = trans;
        AnimatorSet animatorSet = new AnimatorSet();
        float[] fArr2 = isAppOpening ? new float[]{1.0f, 0.0f} : new float[]{0.0f, 1.0f};
        float[] fArr3 = {0.8f, 1.0f};
        if (isAppOpening) {
            // fill-array-data instruction
            fArr3[0] = 1.0f;
            fArr3[1] = 0.8f;
        }
        if (this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            final AllAppsHost allAppsHost = this.mLauncher.getAllAppsHost();
            if (fArr == null && com.lge.launcher3.util.Utilities.TIME_CONDITION) {
                j = isAppOpening ? this.THUMBNAIL_APP_LAUNCH_TOTAL_DURATION : 500L;
                final float alpha = allAppsHost.getAlpha();
                final float scaleX = allAppsHost.getScaleX();
                final float scaleY = allAppsHost.getScaleY();
                allAppsHost.setAlpha(fArr2[0]);
                allAppsHost.setScaleX(fArr3[0]);
                allAppsHost.setScaleY(fArr3[0]);
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(allAppsHost, (Property<AllAppsHost, Float>) View.ALPHA, fArr2);
                objectAnimatorOfFloat.setDuration(j);
                objectAnimatorOfFloat.setInterpolator(Interpolators.LINEAR);
                allAppsHost.setLayerType(2, null);
                objectAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.QuickstepTransitionManager.6
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        allAppsHost.setLayerType(0, null);
                    }
                });
                animatorSet.play(objectAnimatorOfFloat);
                ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(allAppsHost, (Property<AllAppsHost, Float>) View.SCALE_X, fArr3);
                objectAnimatorOfFloat2.setDuration(j);
                objectAnimatorOfFloat2.setInterpolator(this.mAppLaunchInterpolator);
                animatorSet.play(objectAnimatorOfFloat2);
                ObjectAnimator objectAnimatorOfFloat3 = ObjectAnimator.ofFloat(allAppsHost, (Property<AllAppsHost, Float>) View.SCALE_Y, fArr3);
                objectAnimatorOfFloat3.setDuration(j);
                objectAnimatorOfFloat3.setInterpolator(this.mAppLaunchInterpolator);
                animatorSet.play(objectAnimatorOfFloat3);
                runnableComposeViewContentAnimator = new Runnable() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$VhAbRwj8FZAKRX3LYqisjKL3asI
                    @Override // java.lang.Runnable
                    public final void run() {
                        QuickstepTransitionManager.lambda$getLauncherContentAnimator$1(allAppsHost, alpha, scaleX, scaleY);
                    }
                };
            } else {
                if (fArr == null) {
                    fArr = new float[2];
                    if (isAppOpening) {
                        fArr[0] = 0.0f;
                        fArr[1] = -this.mContentTransY;
                    } else {
                        fArr[0] = -this.mContentTransY;
                        fArr[1] = 0.0f;
                    }
                }
                final float alpha2 = allAppsHost.getAlpha();
                final float translationY = allAppsHost.getTranslationY();
                allAppsHost.setAlpha(fArr2[0]);
                allAppsHost.setTranslationY(fArr[0]);
                ObjectAnimator objectAnimatorOfFloat4 = ObjectAnimator.ofFloat(allAppsHost, (Property<AllAppsHost, Float>) View.ALPHA, fArr2);
                objectAnimatorOfFloat4.setDuration(217L);
                objectAnimatorOfFloat4.setInterpolator(Interpolators.LINEAR);
                allAppsHost.setLayerType(2, null);
                objectAnimatorOfFloat4.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.QuickstepTransitionManager.7
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        allAppsHost.setLayerType(0, null);
                    }
                });
                ObjectAnimator objectAnimatorOfFloat5 = ObjectAnimator.ofFloat(allAppsHost, (Property<AllAppsHost, Float>) View.TRANSLATION_Y, fArr);
                objectAnimatorOfFloat5.setInterpolator(Interpolators.AGGRESSIVE_EASE);
                objectAnimatorOfFloat5.setDuration(350L);
                animatorSet.play(objectAnimatorOfFloat4);
                animatorSet.play(objectAnimatorOfFloat5);
                runnableComposeViewContentAnimator = new Runnable() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$grGwAnKiIcGeWj5sLvm1hdxSRA0
                    @Override // java.lang.Runnable
                    public final void run() {
                        QuickstepTransitionManager.lambda$getLauncherContentAnimator$2(allAppsHost, alpha2, translationY);
                    }
                };
            }
        } else if (this.mLauncher.isInState(LauncherState.OVERVIEW)) {
            AllAppsTransitionController allAppsController = this.mLauncher.getAllAppsController();
            animatorSet.play(ObjectAnimator.ofFloat(allAppsController, AllAppsTransitionController.ALL_APPS_PROGRESS, allAppsController.getProgress(), 1.3059858f));
            runnableComposeViewContentAnimator = composeViewContentAnimator(animatorSet, fArr2, fArr);
        } else if (fArr == null && com.lge.launcher3.util.Utilities.TIME_CONDITION) {
            j = isAppOpening ? this.THUMBNAIL_APP_LAUNCH_TOTAL_DURATION : 500L;
            this.mDragLayerAlpha.setValue(fArr2[0]);
            ObjectAnimator objectAnimatorOfFloat6 = ObjectAnimator.ofFloat(this.mDragLayerAlpha, MultiValueAlpha.VALUE, fArr2);
            objectAnimatorOfFloat6.setDuration(j);
            objectAnimatorOfFloat6.setInterpolator(Interpolators.LINEAR);
            animatorSet.play(objectAnimatorOfFloat6);
            this.mDragLayer.setScaleX(fArr3[0]);
            this.mDragLayer.setScaleY(fArr3[0]);
            ObjectAnimator objectAnimatorOfFloat7 = ObjectAnimator.ofFloat(this.mDragLayer, (Property<DragLayer, Float>) View.SCALE_X, fArr3);
            objectAnimatorOfFloat7.setInterpolator(Interpolators.DEACCEL);
            objectAnimatorOfFloat7.setDuration(j);
            animatorSet.play(objectAnimatorOfFloat7);
            ObjectAnimator objectAnimatorOfFloat8 = ObjectAnimator.ofFloat(this.mDragLayer, (Property<DragLayer, Float>) View.SCALE_Y, fArr3);
            objectAnimatorOfFloat8.setInterpolator(Interpolators.DEACCEL);
            objectAnimatorOfFloat8.setDuration(j);
            animatorSet.play(objectAnimatorOfFloat8);
            this.mDragLayer.setLayerType(2, null);
            runnableComposeViewContentAnimator = new Runnable() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$75c3oTe5Bh6S8uUo81wQmVI2sRU
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getLauncherContentAnimator$3$QuickstepTransitionManager();
                }
            };
        } else {
            if (fArr == null) {
                fArr = new float[2];
                if (isAppOpening) {
                    fArr[0] = 0.0f;
                    fArr[1] = -this.mContentTransY;
                } else {
                    fArr[0] = -this.mContentTransY;
                    fArr[1] = 0.0f;
                }
            }
            this.mDragLayerAlpha.setValue(fArr2[0]);
            ObjectAnimator objectAnimatorOfFloat9 = ObjectAnimator.ofFloat(this.mDragLayerAlpha, MultiValueAlpha.VALUE, fArr2);
            objectAnimatorOfFloat9.setDuration(217L);
            objectAnimatorOfFloat9.setInterpolator(Interpolators.LINEAR);
            animatorSet.play(objectAnimatorOfFloat9);
            Workspace workspace = this.mLauncher.getWorkspace();
            if (workspace.getChildAt(workspace.getCurrentPage()) != null && this.mLauncher.getHotseat() != null) {
                final ShortcutAndWidgetContainer shortcutsAndWidgets = ((CellLayout) workspace.getChildAt(workspace.getCurrentPage())).getShortcutsAndWidgets();
                final Hotseat hotseat = this.mLauncher.getHotseat();
                shortcutsAndWidgets.setLayerType(2, null);
                hotseat.setLayerType(2, null);
                animatorSet.play(ObjectAnimator.ofFloat(shortcutsAndWidgets, (Property<ShortcutAndWidgetContainer, Float>) View.TRANSLATION_Y, fArr));
                animatorSet.play(ObjectAnimator.ofFloat(hotseat, (Property<Hotseat, Float>) View.TRANSLATION_Y, fArr));
                runnableComposeViewContentAnimator = new Runnable() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$-wL1_fpGH5WNrCB_NKHRL9YjlL8
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$getLauncherContentAnimator$4$QuickstepTransitionManager(shortcutsAndWidgets, hotseat);
                    }
                };
            } else {
                LGLog.d(TAG, "workspace.getCurrentPage(): " + workspace.getCurrentPage());
                runnableComposeViewContentAnimator = new Runnable() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$oyEFJjPEUOZjhpuIvs0VgNPcfkM
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$getLauncherContentAnimator$5$QuickstepTransitionManager();
                    }
                };
            }
        }
        return new Pair<>(animatorSet, runnableComposeViewContentAnimator);
    }

    static /* synthetic */ void lambda$getLauncherContentAnimator$1(View view, float f, float f2, float f3) {
        view.setAlpha(f);
        view.setScaleX(f2);
        view.setScaleY(f3);
        view.setLayerType(0, null);
    }

    static /* synthetic */ void lambda$getLauncherContentAnimator$2(View view, float f, float f2) {
        view.setAlpha(f);
        view.setTranslationY(f2);
        view.setLayerType(0, null);
    }

    public /* synthetic */ void lambda$getLauncherContentAnimator$3$QuickstepTransitionManager() {
        this.mDragLayerAlpha.setValue(1.0f);
        this.mDragLayer.setLayerType(0, null);
        this.mDragLayer.setTranslationY(0.0f);
        if (LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION.getValue()) {
            this.mDragLayer.setScaleX(1.0f);
            this.mDragLayer.setScaleY(1.0f);
        }
    }

    public /* synthetic */ void lambda$getLauncherContentAnimator$4$QuickstepTransitionManager(View view, View view2) {
        view.setTranslationY(0.0f);
        view2.setTranslationY(0.0f);
        view.setLayerType(0, null);
        view2.setLayerType(0, null);
        this.mDragLayerAlpha.setValue(1.0f);
    }

    public /* synthetic */ void lambda$getLauncherContentAnimator$5$QuickstepTransitionManager() {
        this.mDragLayerAlpha.setValue(1.0f);
    }

    protected Runnable composeViewContentAnimator(AnimatorSet anim, float[] alphas, float[] trans) {
        final RecentsView recentsView = (RecentsView) this.mLauncher.getOverviewPanel();
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(recentsView, RecentsView.CONTENT_ALPHA, alphas);
        objectAnimatorOfFloat.setDuration(217L);
        objectAnimatorOfFloat.setInterpolator(Interpolators.LINEAR);
        anim.play(objectAnimatorOfFloat);
        recentsView.setFreezeViewVisibility(true);
        ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(recentsView, (Property<RecentsView, Float>) View.TRANSLATION_Y, trans);
        objectAnimatorOfFloat2.setInterpolator(Interpolators.AGGRESSIVE_EASE);
        objectAnimatorOfFloat2.setDuration(350L);
        anim.play(objectAnimatorOfFloat2);
        return new Runnable() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$6uhQpfL9u8uqHAK5x6z8HbgeY-w
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$composeViewContentAnimator$6$QuickstepTransitionManager(recentsView);
            }
        };
    }

    public /* synthetic */ void lambda$composeViewContentAnimator$6$QuickstepTransitionManager(RecentsView recentsView) {
        recentsView.setFreezeViewVisibility(false);
        recentsView.setTranslationY(0.0f);
        this.mLauncher.getStateManager().reapplyState();
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x006b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private android.animation.Animator getOpeningWindowAnimators(final android.view.View r47, com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r48, com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r49, android.graphics.Rect r50, boolean r51) {
        /*
            r46 = this;
            r15 = r46
            r0 = r47
            r14 = r48
            android.graphics.RectF r13 = new android.graphics.RectF
            r13.<init>()
            com.android.launcher3.BaseQuickstepLauncher r1 = r15.mLauncher
            com.android.launcher3.views.FloatingIconView$Action r2 = com.android.launcher3.views.FloatingIconView.Action.Open
            r3 = r51
            com.android.launcher3.views.FloatingIconView r11 = com.android.launcher3.views.FloatingIconView.getFloatingIconView(r1, r0, r3, r13, r2)
            android.graphics.Rect r24 = new android.graphics.Rect
            r24.<init>()
            android.graphics.Matrix r29 = new android.graphics.Matrix
            r29.<init>()
            com.android.quickstep.RemoteAnimationTargets r1 = new com.android.quickstep.RemoteAnimationTargets
            r12 = 0
            com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r2 = new com.android.systemui.shared.system.RemoteAnimationTargetCompat[r12]
            r3 = r49
            r1.<init>(r14, r3, r2, r12)
            com.android.quickstep.util.SurfaceTransactionApplier r10 = new com.android.quickstep.util.SurfaceTransactionApplier
            r10.<init>(r11)
            r1.addReleaseCheck(r10)
            int r2 = r50.height()
            int r3 = r50.width()
            int r2 = java.lang.Math.min(r2, r3)
            float r2 = (float) r2
            float r3 = r13.width()
            float r3 = r2 / r3
            float r4 = r13.height()
            float r2 = r2 / r4
            float r16 = java.lang.Math.max(r3, r2)
            boolean r2 = r0 instanceof com.android.launcher3.BubbleTextView
            if (r2 == 0) goto L6b
            android.view.ViewParent r2 = r47.getParent()
            boolean r2 = r2 instanceof com.android.launcher3.shortcuts.DeepShortcutView
            if (r2 != 0) goto L6b
            r2 = r0
            com.android.launcher3.BubbleTextView r2 = (com.android.launcher3.BubbleTextView) r2
            android.graphics.drawable.Drawable r2 = r2.getIcon()
            boolean r3 = r2 instanceof com.android.launcher3.FastBitmapDrawable
            if (r3 == 0) goto L6b
            com.android.launcher3.FastBitmapDrawable r2 = (com.android.launcher3.FastBitmapDrawable) r2
            float r2 = r2.getAnimatedScale()
            goto L6d
        L6b:
            r2 = 1065353216(0x3f800000, float:1.0)
        L6d:
            r9 = r2
            r2 = 2
            int[] r7 = new int[r2]
            com.android.launcher3.dragndrop.DragLayer r3 = r15.mDragLayer
            r3.getLocationOnScreen(r7)
            int r3 = r50.centerX()
            r4 = r7[r12]
            int r3 = r3 - r4
            float r3 = (float) r3
            int r4 = r50.centerY()
            r8 = 1
            r5 = r7[r8]
            int r4 = r4 - r5
            float r4 = (float) r4
            float r5 = r13.centerX()
            float r3 = r3 - r5
            float r5 = r13.centerY()
            float r6 = r4 - r5
            float r5 = r13.top
            int r4 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r4 > 0) goto Lac
            float r4 = java.lang.Math.abs(r6)
            com.android.launcher3.BaseQuickstepLauncher r5 = r15.mLauncher
            com.android.launcher3.DeviceProfile r5 = r5.getDeviceProfile()
            int r5 = r5.cellHeightPx
            float r5 = (float) r5
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 >= 0) goto Laa
            goto Lac
        Laa:
            r4 = r12
            goto Lad
        Lac:
            r4 = r8
        Lad:
            com.lge.launcher3.util.LGHomeFeature$Config r5 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION
            boolean r5 = r5.getValue()
            r17 = 1136361472(0x43bb8000, float:375.0)
            r18 = 1112014848(0x42480000, float:50.0)
            r51 = r3
            if (r5 == 0) goto Ldc
            int r4 = r15.THUMBNAIL_APP_LAUNCH_TOTAL_DURATION
            long r2 = (long) r4
            r21 = r13
            long r12 = (long) r4
            r23 = r9
            long r8 = (long) r4
            float r5 = (float) r4
            float r4 = (float) r4
            r26 = r2
            android.view.animation.Interpolator r2 = r15.mAppLaunchInterpolator
            android.view.animation.Interpolator r3 = com.android.launcher3.anim.Interpolators.LINEAR
            android.view.animation.Interpolator r17 = com.android.launcher3.anim.Interpolators.LINEAR
            r43 = r5
            r5 = r3
            r44 = r26
            r27 = r4
            r26 = r43
            r3 = r12
            r12 = r44
            goto L106
        Ldc:
            r23 = r9
            r21 = r13
            if (r4 == 0) goto Le5
            r2 = 250(0xfa, double:1.235E-321)
            goto Le7
        Le5:
            r2 = 360(0x168, double:1.78E-321)
        Le7:
            if (r4 == 0) goto Lec
            r8 = 450(0x1c2, double:2.223E-321)
            goto Lee
        Lec:
            r8 = 200(0xc8, double:9.9E-322)
        Lee:
            if (r4 == 0) goto Lf1
            goto Lf3
        Lf1:
            r18 = 1109393408(0x42200000, float:40.0)
        Lf3:
            android.view.animation.Interpolator r4 = com.android.launcher3.anim.Interpolators.LINEAR
            android.view.animation.Interpolator r5 = com.android.launcher3.anim.Interpolators.AGGRESSIVE_EASE
            android.view.animation.Interpolator r12 = com.android.launcher3.anim.Interpolators.EXAGGERATED_EASE
            r26 = r17
            r27 = r26
            r17 = r12
            r12 = 450(0x1c2, double:2.223E-321)
            r43 = r2
            r2 = r4
            r3 = r43
        L106:
            r28 = 1103626240(0x41c80000, float:25.0)
            r30 = r7
            android.graphics.RectF r7 = new android.graphics.RectF
            r14 = r50
            r7.<init>(r14)
            android.graphics.RectF r31 = new android.graphics.RectF
            r31.<init>()
            android.graphics.RectF r32 = new android.graphics.RectF
            r32.<init>()
            android.graphics.Point r33 = new android.graphics.Point
            r33.<init>()
            android.animation.AnimatorSet r7 = new android.animation.AnimatorSet
            r7.<init>()
            r34 = r7
            r7 = 2
            float[] r7 = new float[r7]
            r7 = {x0226: FILL_ARRAY_DATA , data: [0, 1065353216} // fill-array
            android.animation.ValueAnimator r7 = android.animation.ValueAnimator.ofFloat(r7)
            r7.setDuration(r12)
            r7.setInterpolator(r2)
            r7.addListener(r11)
            com.android.launcher3.QuickstepTransitionManager$8 r2 = new com.android.launcher3.QuickstepTransitionManager$8
            r2.<init>()
            r7.addListener(r2)
            com.android.launcher3.DeviceProfile r0 = r15.mDeviceProfile
            boolean r0 = r0.isVerticalBarLayout()
            if (r0 == 0) goto L154
            int r0 = r50.height()
            float r0 = (float) r0
            int r1 = r50.width()
            goto L15d
        L154:
            int r0 = r50.width()
            float r0 = (float) r0
            int r1 = r50.height()
        L15d:
            float r1 = (float) r1
            r35 = r0
            r36 = r1
            com.android.launcher3.BaseQuickstepLauncher r0 = r15.mLauncher
            android.content.res.Resources r0 = r0.getResources()
            boolean r0 = com.android.systemui.shared.system.QuickStepContract.supportsRoundedCornersOnWindows(r0)
            r1 = 0
            if (r0 == 0) goto L181
            com.lge.launcher3.util.LGHomeFeature$Config r0 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION
            boolean r0 = r0.getValue()
            if (r0 == 0) goto L17a
            r0 = 1086324736(0x40c00000, float:6.0)
            goto L17c
        L17a:
            r0 = 1073741824(0x40000000, float:2.0)
        L17c:
            float r0 = r35 / r0
            r37 = r0
            goto L183
        L181:
            r37 = r1
        L183:
            com.android.launcher3.DeviceProfile r0 = r15.mDeviceProfile
            boolean r0 = r0.isMultiWindowMode
            if (r0 == 0) goto L18c
            r38 = r1
            goto L194
        L18c:
            com.android.launcher3.BaseQuickstepLauncher r0 = r15.mLauncher
            float r0 = com.android.systemui.shared.system.QuickStepContract.getWindowCornerRadius(r0)
            r38 = r0
        L194:
            com.android.launcher3.QuickstepTransitionManager$9 r2 = new com.android.launcher3.QuickstepTransitionManager$9
            r0 = r2
            r1 = r46
            r39 = r2
            r2 = r51
            r41 = r7
            r40 = r34
            r7 = r8
            r9 = r23
            r34 = r10
            r10 = r16
            r42 = r11
            r11 = r12
            r22 = r21
            r13 = r17
            r14 = r28
            r15 = r18
            r16 = r35
            r17 = r36
            r18 = r26
            r19 = r37
            r20 = r38
            r21 = r27
            r23 = r50
            r25 = r32
            r26 = r30
            r27 = r31
            r28 = r48
            r30 = r42
            r31 = r33
            r32 = r34
            r0.<init>(r2, r3, r5, r6, r7, r9, r10, r11, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            r1 = r39
            r0 = r41
            r0.addUpdateListener(r1)
            r1 = r46
            com.android.launcher3.BaseQuickstepLauncher r2 = r1.mLauncher
            com.android.launcher3.statemanager.StateManager r2 = r2.getStateManager()
            com.android.launcher3.statemanager.BaseState r2 = r2.getState()
            com.android.launcher3.LauncherState r3 = com.android.launcher3.LauncherState.OVERVIEW
            if (r2 == r3) goto L1eb
            r12 = 1
            goto L1ec
        L1eb:
            r12 = 0
        L1ec:
            com.android.launcher3.BaseQuickstepLauncher r2 = r1.mLauncher
            com.android.launcher3.statehandlers.DepthController r2 = r2.getDepthController()
            android.util.FloatProperty<com.android.launcher3.statehandlers.DepthController> r3 = com.android.launcher3.statehandlers.DepthController.DEPTH
            r4 = 1
            float[] r4 = new float[r4]
            com.android.launcher3.LauncherState r5 = com.android.launcher3.LauncherState.BACKGROUND_APP
            com.android.launcher3.BaseQuickstepLauncher r6 = r1.mLauncher
            float r5 = r5.getDepth(r6)
            r6 = 0
            r4[r6] = r5
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
            r4 = 450(0x1c2, double:2.223E-321)
            android.animation.ObjectAnimator r3 = r3.setDuration(r4)
            if (r12 == 0) goto L21f
            r4 = r48
            com.android.systemui.shared.system.RemoteAnimationTargetCompat r4 = com.android.quickstep.util.RemoteAnimationProvider.findLowestOpaqueLayerTarget(r4, r6)
            r2.setSurfaceToApp(r4)
            com.android.launcher3.QuickstepTransitionManager$10 r4 = new com.android.launcher3.QuickstepTransitionManager$10
            r4.<init>()
            r3.addListener(r4)
        L21f:
            r2 = r40
            r2.play(r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.QuickstepTransitionManager.getOpeningWindowAnimators(android.view.View, com.android.systemui.shared.system.RemoteAnimationTargetCompat[], com.android.systemui.shared.system.RemoteAnimationTargetCompat[], android.graphics.Rect, boolean):android.animation.Animator");
    }

    public void registerRemoteAnimations() {
        if (!FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get() && hasControlRemoteAppTransitionPermission()) {
            this.mWallpaperOpenRunner = createWallpaperOpenRunner(false);
            RemoteAnimationDefinitionCompat remoteAnimationDefinitionCompat = new RemoteAnimationDefinitionCompat();
            remoteAnimationDefinitionCompat.addRemoteAnimation(13, 1, new RemoteAnimationAdapterCompat(new LauncherAnimationRunner(this.mHandler, this.mWallpaperOpenRunner, false), APP_LAUNCH_CURVED_DURATION, 0L, this.mLauncher.getIApplicationThread()));
            if (FeatureFlags.KEYGUARD_ANIMATION.get()) {
                this.mKeyguardGoingAwayRunner = createWallpaperOpenRunner(true);
                remoteAnimationDefinitionCompat.addRemoteAnimation(21, new RemoteAnimationAdapterCompat(new LauncherAnimationRunner(this.mHandler, this.mKeyguardGoingAwayRunner, true), APP_LAUNCH_CURVED_DURATION, 0L, this.mLauncher.getIApplicationThread()));
            }
            new ActivityCompat(this.mLauncher).registerRemoteAnimations(remoteAnimationDefinitionCompat);
        }
    }

    public void registerRemoteTransitions() {
        if (!FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get() && hasControlRemoteAppTransitionPermission()) {
            this.mWallpaperOpenTransitionRunner = createWallpaperOpenRunner(false);
            RemoteTransitionCompat remoteTransitionCompatBuildRemoteTransition = RemoteAnimationAdapterCompat.buildRemoteTransition(new LauncherAnimationRunner(this.mHandler, this.mWallpaperOpenTransitionRunner, false), this.mLauncher.getIApplicationThread());
            this.mLauncherOpenTransition = remoteTransitionCompatBuildRemoteTransition;
            remoteTransitionCompatBuildRemoteTransition.addHomeOpenCheck(this.mLauncher.getComponentName());
            SystemUiProxy.INSTANCE.getNoCreate().registerRemoteTransition(this.mLauncherOpenTransition);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean launcherIsATargetWithMode(RemoteAnimationTargetCompat[] targets, int mode) {
        return TaskUtils.taskIsATargetWithMode(targets, this.mLauncher.getTaskId(), mode);
    }

    LauncherAnimationRunner.RemoteAnimationFactory createWallpaperOpenRunner(boolean fromUnlock) {
        return new WallpaperOpenLauncherAnimationRunner(this.mHandler, fromUnlock);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Animator getUnlockWindowAnimator(final RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets) {
        final SurfaceTransactionApplier surfaceTransactionApplier = new SurfaceTransactionApplier(this.mDragLayer);
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(APP_LAUNCH_CURVED_DURATION);
        final float windowCornerRadius = this.mDeviceProfile.isMultiWindowMode ? 0.0f : QuickStepContract.getWindowCornerRadius(this.mLauncher);
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.QuickstepTransitionManager.11
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = appTargets;
                SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[remoteAnimationTargetCompatArr.length];
                for (int length = remoteAnimationTargetCompatArr.length - 1; length >= 0; length--) {
                    RemoteAnimationTargetCompat remoteAnimationTargetCompat = appTargets[length];
                    surfaceParamsArr[length] = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash).withAlpha(1.0f).withWindowCrop(remoteAnimationTargetCompat.screenSpaceBounds).withCornerRadius(windowCornerRadius).build();
                }
                surfaceTransactionApplier.scheduleApply(surfaceParamsArr);
            }
        });
        return valueAnimatorOfFloat;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Animator getClosingWindowAnimators(RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets) {
        final RemoteAnimationTargets remoteAnimationTargets = new RemoteAnimationTargets(appTargets, wallpaperTargets, new RemoteAnimationTargetCompat[0], 1);
        SurfaceTransactionApplier surfaceTransactionApplier = new SurfaceTransactionApplier(this.mDragLayer);
        Matrix matrix = new Matrix();
        Point point = new Point();
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        int i = this.mLauncher.isWorkspaceLoading() ? 0 : 250;
        float windowCornerRadius = this.mDeviceProfile.isMultiWindowMode ? 0.0f : QuickStepContract.getWindowCornerRadius(this.mLauncher);
        valueAnimatorOfFloat.setDuration(i);
        valueAnimatorOfFloat.addUpdateListener(new MultiValueUpdateListener(i, appTargets, point, matrix, windowCornerRadius, surfaceTransactionApplier) { // from class: com.android.launcher3.QuickstepTransitionManager.12
            MultiValueUpdateListener.FloatProp mAlpha = new MultiValueUpdateListener.FloatProp(1.0f, 0.0f, 25.0f, 125.0f, Interpolators.LINEAR);
            MultiValueUpdateListener.FloatProp mDy;
            MultiValueUpdateListener.FloatProp mScale;
            final /* synthetic */ RemoteAnimationTargetCompat[] val$appTargets;
            final /* synthetic */ int val$duration;
            final /* synthetic */ Matrix val$matrix;
            final /* synthetic */ SurfaceTransactionApplier val$surfaceApplier;
            final /* synthetic */ Point val$tmpPos;
            final /* synthetic */ float val$windowCornerRadius;

            {
                this.val$duration = i;
                this.val$appTargets = appTargets;
                this.val$tmpPos = point;
                this.val$matrix = matrix;
                this.val$windowCornerRadius = windowCornerRadius;
                this.val$surfaceApplier = surfaceTransactionApplier;
                this.mDy = new MultiValueUpdateListener.FloatProp(0.0f, QuickstepTransitionManager.this.mClosingWindowTransY, 0.0f, i, Interpolators.DEACCEL_1_7);
                this.mScale = new MultiValueUpdateListener.FloatProp(1.0f, 1.0f, 0.0f, i, Interpolators.DEACCEL_1_7);
            }

            @Override // com.android.quickstep.util.MultiValueUpdateListener
            public void onUpdate(float percent) {
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = this.val$appTargets;
                SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[remoteAnimationTargetCompatArr.length];
                for (int length = remoteAnimationTargetCompatArr.length - 1; length >= 0; length--) {
                    RemoteAnimationTargetCompat remoteAnimationTargetCompat = this.val$appTargets[length];
                    SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash);
                    this.val$tmpPos.set(remoteAnimationTargetCompat.position.x, remoteAnimationTargetCompat.position.y);
                    if (remoteAnimationTargetCompat.localBounds != null) {
                        this.val$tmpPos.set(remoteAnimationTargetCompat.localBounds.left, remoteAnimationTargetCompat.localBounds.top);
                    }
                    if (remoteAnimationTargetCompat.mode == 1) {
                        this.val$matrix.setScale(this.mScale.value, this.mScale.value, remoteAnimationTargetCompat.screenSpaceBounds.centerX(), remoteAnimationTargetCompat.screenSpaceBounds.centerY());
                        this.val$matrix.postTranslate(0.0f, this.mDy.value);
                        this.val$matrix.postTranslate(this.val$tmpPos.x, this.val$tmpPos.y);
                        builder.withMatrix(this.val$matrix).withAlpha(this.mAlpha.value).withCornerRadius(this.val$windowCornerRadius);
                    } else {
                        this.val$matrix.setTranslate(this.val$tmpPos.x, this.val$tmpPos.y);
                        builder.withMatrix(this.val$matrix).withAlpha(1.0f);
                    }
                    Rect rect = new Rect(remoteAnimationTargetCompat.screenSpaceBounds);
                    rect.offsetTo(0, 0);
                    surfaceParamsArr[length] = builder.withWindowCrop(rect).build();
                }
                this.val$surfaceApplier.scheduleApply(surfaceParamsArr);
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.QuickstepTransitionManager.13
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                RemoteAnimationTargets remoteAnimationTargets2 = remoteAnimationTargets;
                if (remoteAnimationTargets2 != null) {
                    remoteAnimationTargets2.release();
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                RemoteAnimationTargets remoteAnimationTargets2 = remoteAnimationTargets;
                if (remoteAnimationTargets2 != null) {
                    remoteAnimationTargets2.release();
                }
            }
        });
        return valueAnimatorOfFloat;
    }

    public LauncherState checkLaunchedState() {
        return this.mLauncherState;
    }

    public void finishCurrentTransitionToHome() {
        this.mLauncherState = null;
        this.mLaunchedTaskId = -1;
        this.mLaunchedFromFolder = false;
        this.mIsRemoved = false;
        this.mSearchedAppComponentName = null;
        this.mIconView = null;
    }

    public boolean hasControlRemoteAppTransitionPermission() {
        return this.mLauncher.checkSelfPermission(CONTROL_REMOTE_APP_TRANSITION_PERMISSION) == 0;
    }

    protected class WallpaperOpenLauncherAnimationRunner implements LauncherAnimationRunner.RemoteAnimationFactory {
        private final boolean mFromUnlock;
        private final Handler mHandler;

        public WallpaperOpenLauncherAnimationRunner(Handler handler, boolean fromUnlock) {
            this.mHandler = handler;
            this.mFromUnlock = fromUnlock;
        }

        /* JADX DEBUG: Method merged with bridge method: lambda$onCreateAnimation$0$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner(I[Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;[Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;[Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;Lcom/android/launcher3/LauncherAnimationRunner$AnimationResult;)V */
        /* JADX WARN: Removed duplicated region for block: B:56:0x015c  */
        /* JADX WARN: Removed duplicated region for block: B:76:0x0210  */
        /* JADX WARN: Removed duplicated region for block: B:81:0x022b  */
        /* JADX WARN: Removed duplicated region for block: B:84:0x024d  */
        /* JADX WARN: Removed duplicated region for block: B:86:0x0250  */
        /* JADX WARN: Removed duplicated region for block: B:89:0x0267  */
        @Override // com.android.launcher3.LauncherAnimationRunner.RemoteAnimationFactory
        /* JADX INFO: renamed from: onCreateAnimation, reason: merged with bridge method [inline-methods] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void lambda$onCreateAnimation$0$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner(final int r10, final com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r11, final com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r12, final com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r13, final com.android.launcher3.LauncherAnimationRunner.AnimationResult r14) {
            /*
                r9 = this;
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r0 = r0.mLauncher
                boolean r0 = r0.isDestroyed()
                if (r0 == 0) goto L24
                android.animation.AnimatorSet r10 = new android.animation.AnimatorSet
                r10.<init>()
                com.android.launcher3.QuickstepTransitionManager r13 = com.android.launcher3.QuickstepTransitionManager.this
                android.animation.Animator r11 = com.android.launcher3.QuickstepTransitionManager.m91$$Nest$mgetClosingWindowAnimators(r13, r11, r12)
                r10.play(r11)
                com.android.launcher3.QuickstepTransitionManager r11 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r11 = r11.mLauncher
                android.content.Context r11 = r11.getApplicationContext()
                r14.setAnimation(r10, r11)
                return
            L24:
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r0 = r0.mLauncher
                boolean r0 = r0.hasBeenResumed()
                if (r0 != 0) goto L42
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r0 = r0.mLauncher
                com.android.launcher3.-$$Lambda$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner$sbnUibyxSB6kXgQnHIueoEnYVvs r8 = new com.android.launcher3.-$$Lambda$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner$sbnUibyxSB6kXgQnHIueoEnYVvs
                r1 = r8
                r2 = r9
                r3 = r10
                r4 = r11
                r5 = r12
                r6 = r13
                r7 = r14
                r1.<init>()
                r0.addOnResumeCallback(r8)
                return
            L42:
                com.android.launcher3.QuickstepTransitionManager r10 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r10 = r10.mLauncher
                r13 = 8
                boolean r10 = r10.hasSomeInvisibleFlag(r13)
                if (r10 == 0) goto L61
                com.android.launcher3.QuickstepTransitionManager r10 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r10 = r10.mLauncher
                r13 = 4
                r10.addForceInvisibleFlag(r13)
                com.android.launcher3.QuickstepTransitionManager r10 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r10 = r10.mLauncher
                com.android.launcher3.statemanager.StateManager r10 = r10.getStateManager()
                r10.moveToRestState()
            L61:
                com.android.launcher3.QuickstepTransitionManager r10 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.quickstep.util.RemoteAnimationProvider r10 = com.android.launcher3.QuickstepTransitionManager.m83$$Nest$fgetmRemoteAnimationProvider(r10)
                r13 = 0
                if (r10 == 0) goto L6f
                android.animation.AnimatorSet r10 = r10.createWindowAnimation(r11, r12)
                goto L70
            L6f:
                r10 = r13
            L70:
                if (r10 != 0) goto L27f
                android.animation.AnimatorSet r10 = new android.animation.AnimatorSet
                r10.<init>()
                boolean r0 = r9.mFromUnlock
                r1 = 1
                r2 = 0
                if (r0 == 0) goto L86
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                android.animation.Animator r12 = com.android.launcher3.QuickstepTransitionManager.m94$$Nest$mgetUnlockWindowAnimator(r0, r11, r12)
            L83:
                r0 = r2
                goto L1fd
            L86:
                int r0 = r11.length
                r3 = r2
            L88:
                java.lang.String r4 = "QuickstepTransition"
                if (r3 >= r0) goto L177
                r5 = r11[r3]
                int r6 = r5.mode
                if (r6 != r1) goto L173
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                int r0 = com.android.launcher3.QuickstepTransitionManager.m81$$Nest$fgetmLaunchedTaskId(r0)
                int r3 = r5.taskId
                if (r0 != r3) goto L165
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r0 = r0.mLauncher
                boolean r0 = r0.getIsPinItemDragging()
                if (r0 != 0) goto L165
                java.lang.String r0 = "[IconAnim] closing app is still same as launched app"
                com.lge.launcher3.util.LGLog.d(r4, r0)
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                boolean r0 = com.android.launcher3.QuickstepTransitionManager.m79$$Nest$fgetmIsRemoved(r0)
                if (r0 != 0) goto L177
                android.app.WindowConfiguration r0 = r5.windowConfiguration
                int r0 = r0.getWindowingMode()
                r3 = 6
                if (r0 == r3) goto L177
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r0 = r0.mLauncher
                com.android.launcher3.LauncherState r0 = r0.getState()
                com.android.launcher3.QuickstepTransitionManager r3 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r3 = r3.mLauncher
                com.android.launcher3.Workspace r3 = r3.getWorkspace()
                if (r3 == 0) goto Lde
                com.android.launcher3.QuickstepTransitionManager r3 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r3 = r3.mLauncher
                com.android.launcher3.Workspace r3 = r3.getWorkspace()
                com.android.launcher3.folder.Folder r3 = r3.getOpenFolder()
                if (r3 == 0) goto Lde
                r3 = r1
                goto Ldf
            Lde:
                r3 = r2
            Ldf:
                com.android.launcher3.QuickstepTransitionManager r6 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.LauncherState r6 = com.android.launcher3.QuickstepTransitionManager.m82$$Nest$fgetmLauncherState(r6)
                java.lang.String r7 = ", current: "
                if (r6 == r0) goto L10a
                com.android.launcher3.QuickstepTransitionManager r3 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.LauncherState r3 = com.android.launcher3.QuickstepTransitionManager.m82$$Nest$fgetmLauncherState(r3)
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r8 = "[IconAnim] launcher state has changed, launched from: "
                r6.append(r8)
                r6.append(r3)
                r6.append(r7)
                r6.append(r0)
                java.lang.String r0 = r6.toString()
                com.lge.launcher3.util.LGLog.d(r4, r0)
                goto L159
            L10a:
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                boolean r0 = com.android.launcher3.QuickstepTransitionManager.m80$$Nest$fgetmLaunchedFromFolder(r0)
                if (r0 == r3) goto L133
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                boolean r0 = com.android.launcher3.QuickstepTransitionManager.m80$$Nest$fgetmLaunchedFromFolder(r0)
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r8 = "[IconAnim] folder state has changed, opened when launch : "
                r6.append(r8)
                r6.append(r0)
                r6.append(r7)
                r6.append(r3)
                java.lang.String r0 = r6.toString()
                com.lge.launcher3.util.LGLog.d(r4, r0)
                goto L159
            L133:
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                android.view.View r0 = com.android.launcher3.QuickstepTransitionManager.m78$$Nest$fgetmIconView(r0)
                if (r0 != 0) goto L141
                java.lang.String r0 = "[IconAnim] launched app icon is null"
                com.lge.launcher3.util.LGLog.d(r4, r0)
                goto L159
            L141:
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                android.view.View r0 = com.android.launcher3.QuickstepTransitionManager.m78$$Nest$fgetmIconView(r0)
                android.view.ViewParent r0 = r0.getParent()
                if (r0 == 0) goto L154
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                android.view.View r0 = com.android.launcher3.QuickstepTransitionManager.m78$$Nest$fgetmIconView(r0)
                goto L15a
            L154:
                java.lang.String r0 = "[IconAnim] launched parent of app icon is null"
                com.lge.launcher3.util.LGLog.d(r4, r0)
            L159:
                r0 = r13
            L15a:
                if (r0 != 0) goto L178
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                int r3 = r5.taskId
                android.view.View r0 = com.android.launcher3.QuickstepTransitionManager.m93$$Nest$mgetFirstMatchForAppClose(r0, r3)
                goto L178
            L165:
                java.lang.String r0 = "[IconAnim] closing app is different with launched app"
                com.lge.launcher3.util.LGLog.d(r4, r0)
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                int r3 = r5.taskId
                android.view.View r0 = com.android.launcher3.QuickstepTransitionManager.m93$$Nest$mgetFirstMatchForAppClose(r0, r3)
                goto L178
            L173:
                int r3 = r3 + 1
                goto L88
            L177:
                r0 = r13
            L178:
                com.android.launcher3.QuickstepTransitionManager r3 = com.android.launcher3.QuickstepTransitionManager.this
                android.view.View r3 = com.android.launcher3.QuickstepTransitionManager.m78$$Nest$fgetmIconView(r3)
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "[IconAnim] final closing app icon: "
                r5.append(r6)
                r5.append(r0)
                java.lang.String r6 = ", "
                r5.append(r6)
                r5.append(r3)
                java.lang.String r3 = r5.toString()
                com.lge.launcher3.util.LGLog.d(r4, r3)
                com.android.launcher3.util.MainThreadInitializedObject<com.android.quickstep.SystemUiProxy> r3 = com.android.quickstep.SystemUiProxy.INSTANCE
                com.android.launcher3.QuickstepTransitionManager r5 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r5 = r5.mLauncher
                java.lang.Object r3 = r3.lambda$get$0$MainThreadInitializedObject(r5)
                com.android.quickstep.SystemUiProxy r3 = (com.android.quickstep.SystemUiProxy) r3
                boolean r3 = r3.isSplitScreenVisible()
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "[IconAnim] isSplitScreenVisible : "
                r5.append(r6)
                r5.append(r3)
                java.lang.String r5 = r5.toString()
                android.util.Log.i(r4, r5)
                com.lge.launcher3.util.LGHomeFeature$Config r4 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION
                boolean r4 = r4.getValue()
                if (r4 == 0) goto L1f5
                com.android.launcher3.QuickstepTransitionManager r4 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.DeviceProfile r4 = com.android.launcher3.QuickstepTransitionManager.m75$$Nest$fgetmDeviceProfile(r4)
                boolean r4 = r4.isMultiWindowMode
                if (r4 != 0) goto L1f5
                if (r0 == 0) goto L1f5
                if (r3 != 0) goto L1f5
                com.lge.launcher3.util.LGHomeFeature$Config r3 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
                boolean r3 = r3.getValue()
                if (r3 != 0) goto L1ed
                com.android.launcher3.QuickstepTransitionManager r3 = com.android.launcher3.QuickstepTransitionManager.this
                boolean r3 = com.android.launcher3.QuickstepTransitionManager.m95$$Nest$misClosingFromLandscape(r3, r11)
                if (r3 == 0) goto L1e5
                goto L1ed
            L1e5:
                com.android.launcher3.QuickstepTransitionManager r3 = com.android.launcher3.QuickstepTransitionManager.this
                android.animation.Animator r12 = com.android.launcher3.QuickstepTransitionManager.m92$$Nest$mgetClosingWindowAnimators(r3, r11, r12, r0)
                r0 = r1
                goto L1fd
            L1ed:
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                android.animation.Animator r12 = com.android.launcher3.QuickstepTransitionManager.m91$$Nest$mgetClosingWindowAnimators(r0, r11, r12)
                goto L83
            L1f5:
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                android.animation.Animator r12 = com.android.launcher3.QuickstepTransitionManager.m91$$Nest$mgetClosingWindowAnimators(r0, r11, r12)
                goto L83
            L1fd:
                r10.play(r12)
                com.android.launcher3.QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner$1 r12 = new com.android.launcher3.QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner$1
                r12.<init>()
                r10.addListener(r12)
                com.lge.launcher3.util.LGHomeFeature$Config r12 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LGBLUR_2_WITH_LAUNCH_ANIM
                boolean r12 = r12.getValue()
                if (r12 == 0) goto L219
                com.android.launcher3.QuickstepTransitionManager r12 = com.android.launcher3.QuickstepTransitionManager.this
                android.animation.Animator r12 = r12.getTransitionBlurAnimator(r2)
                r10.play(r12)
            L219:
                com.android.launcher3.QuickstepTransitionManager r12 = com.android.launcher3.QuickstepTransitionManager.this
                boolean r11 = com.android.launcher3.QuickstepTransitionManager.m96$$Nest$mlauncherIsATargetWithMode(r12, r11, r2)
                if (r11 != 0) goto L22b
                com.android.launcher3.QuickstepTransitionManager r11 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r11 = r11.mLauncher
                boolean r11 = r11.isForceInvisible()
                if (r11 == 0) goto L27f
            L22b:
                com.android.launcher3.QuickstepTransitionManager r11 = com.android.launcher3.QuickstepTransitionManager.this
                r12 = 9
                com.android.launcher3.QuickstepTransitionManager.m89$$Nest$maddCujInstrumentation(r11, r10, r12)
                com.android.launcher3.QuickstepTransitionManager r11 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r11 = r11.mLauncher
                com.android.launcher3.statemanager.StateManager r11 = r11.getStateManager()
                android.animation.Animator[] r12 = new android.animation.Animator[r2]
                r11.setCurrentAnimation(r10, r12)
                com.android.launcher3.QuickstepTransitionManager r11 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r11 = r11.mLauncher
                com.android.launcher3.LauncherState r12 = com.android.launcher3.LauncherState.ALL_APPS
                boolean r11 = r11.isInState(r12)
                if (r11 != 0) goto L24d
                if (r0 == 0) goto L27f
            L24d:
                if (r0 == 0) goto L250
                goto L25f
            L250:
                r11 = 2
                float[] r13 = new float[r11]
                com.android.launcher3.QuickstepTransitionManager r11 = com.android.launcher3.QuickstepTransitionManager.this
                float r11 = com.android.launcher3.QuickstepTransitionManager.m74$$Nest$fgetmContentTransY(r11)
                float r11 = -r11
                r13[r2] = r11
                r11 = 0
                r13[r1] = r11
            L25f:
                com.android.launcher3.QuickstepTransitionManager r11 = com.android.launcher3.QuickstepTransitionManager.this
                android.util.Pair r11 = r11.getLauncherContentAnimator(r2, r13)
                if (r0 != 0) goto L270
                java.lang.Object r12 = r11.first
                android.animation.AnimatorSet r12 = (android.animation.AnimatorSet) r12
                r0 = 100
                r12.setStartDelay(r0)
            L270:
                java.lang.Object r12 = r11.first
                android.animation.Animator r12 = (android.animation.Animator) r12
                r10.play(r12)
                com.android.launcher3.QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner$2 r12 = new com.android.launcher3.QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner$2
                r12.<init>()
                r10.addListener(r12)
            L27f:
                com.android.launcher3.QuickstepTransitionManager r11 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r11 = r11.mLauncher
                r12 = 15
                r11.clearForceInvisibleFlag(r12)
                com.android.launcher3.QuickstepTransitionManager r11 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r11 = r11.mLauncher
                r14.setAnimation(r10, r11)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.QuickstepTransitionManager.WallpaperOpenLauncherAnimationRunner.lambda$onCreateAnimation$0$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner(int, com.android.systemui.shared.system.RemoteAnimationTargetCompat[], com.android.systemui.shared.system.RemoteAnimationTargetCompat[], com.android.systemui.shared.system.RemoteAnimationTargetCompat[], com.android.launcher3.LauncherAnimationRunner$AnimationResult):void");
        }

        public /* synthetic */ void lambda$onCreateAnimation$1$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner(final int i, final RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, final RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, final RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, final LauncherAnimationRunner.AnimationResult animationResult) {
            Utilities.postAsyncCallback(this.mHandler, new Runnable() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner$wL7myzubpW-ilzdbzPntJYsDF48
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onCreateAnimation$0$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner(i, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, animationResult);
                }
            });
        }
    }

    private void resetContentView() {
        this.mDragLayerAlpha.setValue(1.0f);
        this.mDragLayer.setLayerType(0, null);
        this.mDragLayer.setTranslationY(0.0f);
        if (LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION.getValue()) {
            this.mDragLayer.setScaleX(1.0f);
            this.mDragLayer.setScaleY(1.0f);
        }
    }

    private class AppLaunchAnimationRunner implements LauncherAnimationRunner.RemoteAnimationFactory {
        private final Handler mHandler;
        private final RunnableList mOnEndCallback;
        private final View mV;

        AppLaunchAnimationRunner(Handler handler, View v, RunnableList onEndCallback) {
            this.mHandler = handler;
            this.mV = v;
            this.mOnEndCallback = onEndCallback;
        }

        /* JADX WARN: Code restructure failed: missing block: B:17:0x007f, code lost:
        
            r9 = true;
         */
        @Override // com.android.launcher3.LauncherAnimationRunner.RemoteAnimationFactory
        /* JADX INFO: renamed from: onCreateAnimation */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void lambda$onCreateAnimation$0$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner(int r9, com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r10, com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r11, com.android.systemui.shared.system.RemoteAnimationTargetCompat[] r12, com.android.launcher3.LauncherAnimationRunner.AnimationResult r13) {
            /*
                r8 = this;
                com.android.launcher3.QuickstepTransitionManager r9 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r12 = r9.mLauncher
                com.android.launcher3.LauncherState r12 = r12.getState()
                com.android.launcher3.QuickstepTransitionManager.m88$$Nest$fputmLauncherState(r9, r12)
                com.android.launcher3.QuickstepTransitionManager r9 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r12 = r9.mLauncher
                com.android.launcher3.Workspace r12 = r12.getWorkspace()
                r0 = 0
                r1 = 1
                if (r12 == 0) goto L27
                com.android.launcher3.QuickstepTransitionManager r12 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r12 = r12.mLauncher
                com.android.launcher3.Workspace r12 = r12.getWorkspace()
                com.android.launcher3.folder.Folder r12 = r12.getOpenFolder()
                if (r12 == 0) goto L27
                r12 = r1
                goto L28
            L27:
                r12 = r0
            L28:
                com.android.launcher3.QuickstepTransitionManager.m86$$Nest$fputmLaunchedFromFolder(r9, r12)
                int r9 = r10.length
                r12 = r0
            L2d:
                if (r12 >= r9) goto L7f
                r2 = r10[r12]
                int r3 = r2.mode
                if (r3 != 0) goto L7c
                com.android.launcher3.QuickstepTransitionManager r9 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.LauncherState r9 = com.android.launcher3.QuickstepTransitionManager.m82$$Nest$fgetmLauncherState(r9)
                com.android.launcher3.QuickstepTransitionManager r12 = com.android.launcher3.QuickstepTransitionManager.this
                boolean r12 = com.android.launcher3.QuickstepTransitionManager.m80$$Nest$fgetmLaunchedFromFolder(r12)
                int r3 = r2.taskId
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "[IconAnim] launcher state: "
                r4.append(r5)
                r4.append(r9)
                java.lang.String r9 = ", from folder: "
                r4.append(r9)
                r4.append(r12)
                java.lang.String r9 = ", open task id : "
                r4.append(r9)
                r4.append(r3)
                java.lang.String r9 = r4.toString()
                java.lang.String r12 = "QuickstepTransition"
                com.lge.launcher3.util.LGLog.d(r12, r9)
                com.android.launcher3.QuickstepTransitionManager r9 = com.android.launcher3.QuickstepTransitionManager.this
                int r9 = com.android.launcher3.QuickstepTransitionManager.m81$$Nest$fgetmLaunchedTaskId(r9)
                r12 = -1
                if (r9 != r12) goto L7a
                com.android.launcher3.QuickstepTransitionManager r9 = com.android.launcher3.QuickstepTransitionManager.this
                int r12 = r2.taskId
                com.android.launcher3.QuickstepTransitionManager.m87$$Nest$fputmLaunchedTaskId(r9, r12)
                goto L7f
            L7a:
                r9 = r0
                goto L80
            L7c:
                int r12 = r12 + 1
                goto L2d
            L7f:
                r9 = r1
            L80:
                com.lge.launcher3.util.LGHomeFeature$Config r12 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION
                boolean r12 = r12.getValue()
                if (r12 == 0) goto Ld8
                android.view.View r12 = r8.mV
                java.lang.Object r12 = r12.getTag()
                if (r12 == 0) goto Lb1
                boolean r2 = r12 instanceof com.lge.launcher3.allapps.AllAppsItemInfo
                if (r2 == 0) goto Lb1
                com.lge.launcher3.allapps.AllAppsItemInfo r12 = (com.lge.launcher3.allapps.AllAppsItemInfo) r12
                android.content.ComponentName r12 = r12.getTargetComponent()
                com.android.launcher3.QuickstepTransitionManager r2 = com.android.launcher3.QuickstepTransitionManager.this
                android.content.ComponentName r2 = com.android.launcher3.QuickstepTransitionManager.m84$$Nest$fgetmSearchedAppComponentName(r2)
                if (r2 == 0) goto Lb1
                if (r12 == 0) goto Lb1
                com.android.launcher3.QuickstepTransitionManager r2 = com.android.launcher3.QuickstepTransitionManager.this
                android.content.ComponentName r2 = com.android.launcher3.QuickstepTransitionManager.m84$$Nest$fgetmSearchedAppComponentName(r2)
                boolean r12 = r2.equals(r12)
                if (r12 == 0) goto Lb1
                r0 = r1
            Lb1:
                com.android.launcher3.QuickstepTransitionManager r12 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.DeviceProfile r12 = com.android.launcher3.QuickstepTransitionManager.m75$$Nest$fgetmDeviceProfile(r12)
                boolean r12 = r12.isMultiWindowMode
                if (r12 != 0) goto Ld2
                com.android.launcher3.QuickstepTransitionManager r12 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.LauncherState r12 = com.android.launcher3.QuickstepTransitionManager.m82$$Nest$fgetmLauncherState(r12)
                com.android.launcher3.LauncherState r2 = com.android.launcher3.LauncherState.OVERVIEW
                if (r12 == r2) goto Ld2
                if (r9 == 0) goto Ld2
                if (r0 == 0) goto Lca
                goto Ld2
            Lca:
                com.android.launcher3.QuickstepTransitionManager r9 = com.android.launcher3.QuickstepTransitionManager.this
                android.view.View r12 = r8.mV
                com.android.launcher3.QuickstepTransitionManager.m85$$Nest$fputmIconView(r9, r12)
                goto Ld8
            Ld2:
                com.android.launcher3.QuickstepTransitionManager r9 = com.android.launcher3.QuickstepTransitionManager.this
                r12 = 0
                com.android.launcher3.QuickstepTransitionManager.m85$$Nest$fputmIconView(r9, r12)
            Ld8:
                android.animation.AnimatorSet r9 = new android.animation.AnimatorSet
                r9.<init>()
                com.android.launcher3.QuickstepTransitionManager r12 = com.android.launcher3.QuickstepTransitionManager.this
                boolean r12 = com.android.launcher3.QuickstepTransitionManager.m96$$Nest$mlauncherIsATargetWithMode(r12, r10, r1)
                com.android.launcher3.QuickstepTransitionManager r0 = com.android.launcher3.QuickstepTransitionManager.this
                android.view.View r1 = r8.mV
                boolean r0 = r0.isLaunchingFromRecents(r1, r10)
                if (r0 == 0) goto Lf9
                com.android.launcher3.QuickstepTransitionManager r2 = com.android.launcher3.QuickstepTransitionManager.this
                android.view.View r4 = r8.mV
                r3 = r9
                r5 = r10
                r6 = r11
                r7 = r12
                r2.composeRecentsLaunchAnimator(r3, r4, r5, r6, r7)
                goto L104
            Lf9:
                com.android.launcher3.QuickstepTransitionManager r2 = com.android.launcher3.QuickstepTransitionManager.this
                android.view.View r4 = r8.mV
                r3 = r9
                r5 = r10
                r6 = r11
                r7 = r12
                com.android.launcher3.QuickstepTransitionManager.m90$$Nest$mcomposeIconLaunchAnimator(r2, r3, r4, r5, r6, r7)
            L104:
                if (r12 == 0) goto L10f
                com.android.launcher3.QuickstepTransitionManager r10 = com.android.launcher3.QuickstepTransitionManager.this
                android.animation.AnimatorListenerAdapter r10 = com.android.launcher3.QuickstepTransitionManager.m77$$Nest$fgetmForceInvisibleListener(r10)
                r9.addListener(r10)
            L10f:
                com.android.launcher3.QuickstepTransitionManager$AppLaunchAnimationRunner$1 r10 = new com.android.launcher3.QuickstepTransitionManager$AppLaunchAnimationRunner$1
                r10.<init>()
                r9.addListener(r10)
                com.android.launcher3.QuickstepTransitionManager r10 = com.android.launcher3.QuickstepTransitionManager.this
                com.android.launcher3.BaseQuickstepLauncher r10 = r10.mLauncher
                r13.setAnimation(r9, r10)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.QuickstepTransitionManager.AppLaunchAnimationRunner.lambda$onCreateAnimation$0$QuickstepTransitionManager$WallpaperOpenLauncherAnimationRunner(int, com.android.systemui.shared.system.RemoteAnimationTargetCompat[], com.android.systemui.shared.system.RemoteAnimationTargetCompat[], com.android.systemui.shared.system.RemoteAnimationTargetCompat[], com.android.launcher3.LauncherAnimationRunner$AnimationResult):void");
        }

        @Override // com.android.launcher3.LauncherAnimationRunner.RemoteAnimationFactory
        public void onAnimationCancelled() {
            this.mOnEndCallback.executeAllAndDestroy();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addCujInstrumentation(Animator anim, final int cuj) {
        anim.addListener(new AnimationSuccessListener() { // from class: com.android.launcher3.QuickstepTransitionManager.14

            /* JADX INFO: renamed from: com.android.launcher3.QuickstepTransitionManager$14$1, reason: invalid class name */
            class AnonymousClass1 implements ViewTreeObserver.OnDrawListener {
                boolean mHandled = false;

                AnonymousClass1() {
                }

                @Override // android.view.ViewTreeObserver.OnDrawListener
                public void onDraw() {
                    if (this.mHandled) {
                        return;
                    }
                    this.mHandled = true;
                    InteractionJankMonitorWrapper.begin(QuickstepTransitionManager.this.mDragLayer, cuj);
                    QuickstepTransitionManager.this.mDragLayer.post(new Runnable() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$14$1$cDlX5Ti0KoW1XPEZ-ElPiWgGEOo
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onDraw$0$QuickstepTransitionManager$14$1();
                        }
                    });
                }

                public /* synthetic */ void lambda$onDraw$0$QuickstepTransitionManager$14$1() {
                    QuickstepTransitionManager.this.mDragLayer.getViewTreeObserver().removeOnDrawListener(this);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                QuickstepTransitionManager.this.mDragLayer.getViewTreeObserver().addOnDrawListener(new AnonymousClass1());
                super.onAnimationStart(animation);
            }

            @Override // com.android.launcher3.anim.AnimationSuccessListener, android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                InteractionJankMonitorWrapper.cancel(cuj);
            }

            @Override // com.android.launcher3.anim.AnimationSuccessListener
            public void onAnimationSuccess(Animator animator) {
                InteractionJankMonitorWrapper.end(cuj);
            }
        });
    }

    public void unregisterRemoteAnimations() {
        if (!FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get() && hasControlRemoteAppTransitionPermission()) {
            new ActivityCompat(this.mLauncher).unregisterRemoteAnimations();
            this.mWallpaperOpenRunner = null;
            this.mAppLaunchRunner = null;
            this.mKeyguardGoingAwayRunner = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getFirstMatchForAppClose(final int taskId) {
        LGLog.i(TAG, "[IconAnim] getFirstMatchForAppClose()+");
        final View[] viewArr = new View[1];
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher.getApplicationContext()).getTasks(new Consumer() { // from class: com.android.launcher3.-$$Lambda$QuickstepTransitionManager$BAPlerG4-swcjvJ9iUEu3CCJHsc
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$getFirstMatchForAppClose$7$QuickstepTransitionManager(taskId, viewArr, (ArrayList) obj);
            }
        });
        return viewArr[0];
    }

    public /* synthetic */ void lambda$getFirstMatchForAppClose$7$QuickstepTransitionManager(int i, View[] viewArr, ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Task task = (Task) it.next();
            if (task.key != null && i == task.key.id) {
                if (task.key.windowingMode == 6) {
                    viewArr[0] = null;
                    return;
                }
                if (this.mLauncherState != null && this.mLauncher.getState() == LauncherState.NORMAL && this.mLauncher.getWorkspace() != null) {
                    View firstMatchForAppClose = this.mLauncher.getWorkspace().getFirstMatchForAppClose(task.key.getPackageName(), UserHandle.of(task.key.userId));
                    if (firstMatchForAppClose instanceof FolderIcon) {
                        LGLog.d(TAG, "[IconAnim] folder icon");
                        firstMatchForAppClose = null;
                    }
                    viewArr[0] = firstMatchForAppClose != null && firstMatchForAppClose.isAttachedToWindow() ? firstMatchForAppClose : null;
                    return;
                }
            }
        }
    }

    public void updateRemovedApp(final ArrayList<AppInfo> appInfos) {
        Object tag;
        ComponentName targetComponent;
        if (this.mIconView == null || appInfos == null || appInfos.size() <= 0 || (tag = this.mIconView.getTag()) == null || !(tag instanceof ItemInfo) || (targetComponent = ((ItemInfo) tag).getTargetComponent()) == null) {
            return;
        }
        Iterator<AppInfo> it = appInfos.iterator();
        while (it.hasNext()) {
            if (targetComponent.equals(it.next().componentName)) {
                LGLog.d(TAG, "[IconAnim] app is removed, componentName: " + targetComponent.toString());
                this.mIsRemoved = true;
                return;
            }
        }
    }

    public void updateSearchedApp(ComponentName appComponentName) {
        this.mSearchedAppComponentName = appComponentName;
        LGLog.d(TAG, "[IconAnim] app is searched at all apps, componentName: " + (appComponentName != null ? appComponentName.toString() : null));
    }

    public Animator getTransitionBlurAnimator(final boolean isOpening) {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(600L);
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.QuickstepTransitionManager.15
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                HomescreenBlurManager.getInstance(QuickstepTransitionManager.this.mLauncher.getApplicationContext()).setBlurView2Level(0);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                HomescreenBlurManager.getInstance(QuickstepTransitionManager.this.mLauncher.getApplicationContext()).setBlurView2Level(0);
            }
        });
        valueAnimatorOfFloat.addUpdateListener(new MultiValueUpdateListener() { // from class: com.android.launcher3.QuickstepTransitionManager.16
            @Override // com.android.quickstep.util.MultiValueUpdateListener
            public void onUpdate(float percent) {
                if (!isOpening) {
                    percent = 1.0f - percent;
                }
                HomescreenBlurManager.getInstance(QuickstepTransitionManager.this.mLauncher.getApplicationContext()).setBlurView2Level((int) (percent * QuickstepTransitionManager.this.BLUR_LEVEL));
            }
        });
        return valueAnimatorOfFloat;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Animator getClosingWindowAnimators(RemoteAnimationTargetCompat[] appTargets, RemoteAnimationTargetCompat[] wallpaperTargets, View iconView) {
        float fCenterX;
        float fWidth;
        float fHeight;
        float fCenterY;
        int i;
        RectF rectF;
        RectF rectF2 = new RectF();
        FloatingIconView floatingIconView = FloatingIconView.getFloatingIconView(this.mLauncher, iconView, true, rectF2, FloatingIconView.Action.Close);
        final RemoteAnimationTargets remoteAnimationTargets = new RemoteAnimationTargets(appTargets, wallpaperTargets, new RemoteAnimationTargetCompat[0], 1);
        SurfaceTransactionApplier surfaceTransactionApplier = new SurfaceTransactionApplier(floatingIconView);
        remoteAnimationTargets.addReleaseCheck(surfaceTransactionApplier);
        Rect windowTargetBounds = getWindowTargetBounds(appTargets);
        float fMin = Math.min(1.0f, Math.max(rectF2.width() / windowTargetBounds.width(), rectF2.height() / windowTargetBounds.height()));
        if (this.mDeviceProfile.isVerticalBarLayout() || this.mDeviceProfile.isAllowRotationAndLandscape()) {
            fCenterX = (rectF2.centerX() - windowTargetBounds.centerX()) + (((windowTargetBounds.width() * fMin) - rectF2.width()) / 2.0f);
            float fCenterY2 = rectF2.centerY() - windowTargetBounds.centerY();
            fWidth = windowTargetBounds.width();
            fHeight = windowTargetBounds.height();
            fCenterY = fCenterY2;
        } else {
            fCenterX = rectF2.centerX() - windowTargetBounds.centerX();
            fCenterY = (rectF2.centerY() - windowTargetBounds.centerY()) + (((windowTargetBounds.height() * fMin) - rectF2.height()) / 2.0f);
            float fHeight2 = windowTargetBounds.height();
            fHeight = windowTargetBounds.width();
            fWidth = fHeight2;
        }
        float windowCornerRadius = this.mDeviceProfile.isMultiWindowMode ? 0.0f : QuickStepContract.getWindowCornerRadius(this.mLauncher);
        float f = QuickStepContract.supportsRoundedCornersOnWindows(this.mLauncher.getResources()) ? fHeight / 6.0f : 0.0f;
        RectF rectF3 = new RectF(windowTargetBounds);
        RectF rectF4 = new RectF();
        Rect rect = new Rect();
        Matrix matrix = new Matrix();
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        if (this.mLauncher.isWorkspaceLoading()) {
            rectF = rectF3;
            i = 0;
        } else {
            i = 350;
            rectF = rectF3;
        }
        valueAnimatorOfFloat.setDuration(i);
        valueAnimatorOfFloat.setInterpolator(this.mAppLaunchInterpolator);
        valueAnimatorOfFloat.addListener(floatingIconView);
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.QuickstepTransitionManager.17
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                RemoteAnimationTargets remoteAnimationTargets2 = remoteAnimationTargets;
                if (remoteAnimationTargets2 != null) {
                    remoteAnimationTargets2.release();
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                RemoteAnimationTargets remoteAnimationTargets2 = remoteAnimationTargets;
                if (remoteAnimationTargets2 != null) {
                    remoteAnimationTargets2.release();
                }
            }
        });
        valueAnimatorOfFloat.addUpdateListener(new MultiValueUpdateListener(fCenterX, fCenterY, fMin, fWidth, fHeight, windowCornerRadius, f, windowTargetBounds, rect, appTargets, matrix, rectF4, rectF, floatingIconView, surfaceTransactionApplier) { // from class: com.android.launcher3.QuickstepTransitionManager.18
            MultiValueUpdateListener.FloatProp mCroppedSize;
            MultiValueUpdateListener.FloatProp mDx;
            MultiValueUpdateListener.FloatProp mDy;
            MultiValueUpdateListener.FloatProp mScale;
            MultiValueUpdateListener.FloatProp mWindowRadius;
            final /* synthetic */ RemoteAnimationTargetCompat[] val$appTargets;
            final /* synthetic */ Rect val$crop;
            final /* synthetic */ RectF val$currentBounds;
            final /* synthetic */ float val$dX;
            final /* synthetic */ float val$dY;
            final /* synthetic */ float val$endCrop;
            final /* synthetic */ float val$finalWindowRadius;
            final /* synthetic */ FloatingIconView val$floatingView;
            final /* synthetic */ Matrix val$matrix;
            final /* synthetic */ float val$scale;
            final /* synthetic */ float val$startCrop;
            final /* synthetic */ SurfaceTransactionApplier val$surfaceApplier;
            final /* synthetic */ RectF val$targetBounds;
            final /* synthetic */ float val$windowRadius;
            final /* synthetic */ Rect val$windowTargetBounds;
            MultiValueUpdateListener.FloatProp mAlpha = new MultiValueUpdateListener.FloatProp(1.0f, 0.0f, 270.0f, 50.0f, Interpolators.LINEAR);
            float iconStartDelay = 300.0f;
            MultiValueUpdateListener.FloatProp mIconAlpha = new MultiValueUpdateListener.FloatProp(0.0f, 1.0f, this.iconStartDelay, 50.0f, Interpolators.LINEAR);

            {
                this.val$dX = fCenterX;
                this.val$dY = fCenterY;
                this.val$scale = fMin;
                this.val$startCrop = fWidth;
                this.val$endCrop = fHeight;
                this.val$windowRadius = windowCornerRadius;
                this.val$finalWindowRadius = f;
                this.val$windowTargetBounds = windowTargetBounds;
                this.val$crop = rect;
                this.val$appTargets = appTargets;
                this.val$matrix = matrix;
                this.val$currentBounds = rectF4;
                this.val$targetBounds = rectF;
                this.val$floatingView = floatingIconView;
                this.val$surfaceApplier = surfaceTransactionApplier;
                this.mDx = new MultiValueUpdateListener.FloatProp(0.0f, fCenterX, 0.0f, 350.0f, Interpolators.LINEAR);
                this.mDy = new MultiValueUpdateListener.FloatProp(0.0f, fCenterY, 0.0f, 350.0f, Interpolators.LINEAR);
                this.mScale = new MultiValueUpdateListener.FloatProp(1.0f, fMin, 0.0f, 350.0f, Interpolators.LINEAR);
                this.mCroppedSize = new MultiValueUpdateListener.FloatProp(fWidth, fHeight, 0.0f, 350.0f, Interpolators.LINEAR);
                this.mWindowRadius = new MultiValueUpdateListener.FloatProp(windowCornerRadius, f, 0.0f, 350.0f, Interpolators.LINEAR);
            }

            @Override // com.android.quickstep.util.MultiValueUpdateListener
            public void onUpdate(float percent) {
                int iWidth;
                int iHeight;
                Rect rect2;
                float f2;
                float f3;
                int iMin;
                int iMax;
                if (QuickstepTransitionManager.this.mDeviceProfile.isVerticalBarLayout() || QuickstepTransitionManager.this.mDeviceProfile.isAllowRotationAndLandscape()) {
                    iWidth = (int) this.mCroppedSize.value;
                    iHeight = this.val$windowTargetBounds.height();
                } else {
                    iWidth = this.val$windowTargetBounds.width();
                    iHeight = (int) this.mCroppedSize.value;
                }
                this.val$crop.set(0, 0, iWidth, iHeight);
                float fWidth2 = (this.val$windowTargetBounds.width() - this.val$crop.width()) * this.val$scale;
                float fHeight3 = (this.val$windowTargetBounds.height() - this.val$crop.height()) * this.val$scale;
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = this.val$appTargets;
                SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[remoteAnimationTargetCompatArr.length];
                for (int length = remoteAnimationTargetCompatArr.length - 1; length >= 0; length--) {
                    RemoteAnimationTargetCompat remoteAnimationTargetCompat = this.val$appTargets[length];
                    SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash);
                    if (remoteAnimationTargetCompat.mode == 1) {
                        int iCenterX = remoteAnimationTargetCompat.sourceContainerBounds.centerX();
                        int iCenterY = remoteAnimationTargetCompat.sourceContainerBounds.centerY();
                        if (QuickstepTransitionManager.this.mDeviceProfile.isLandscape) {
                            iMin = Math.max(iCenterX, iCenterY);
                        } else {
                            iMin = Math.min(iCenterX, iCenterY);
                        }
                        if (QuickstepTransitionManager.this.mDeviceProfile.isLandscape) {
                            iMax = Math.min(iCenterX, iCenterY);
                        } else {
                            iMax = Math.max(iCenterX, iCenterY);
                        }
                        this.val$matrix.setScale(this.mScale.value, this.mScale.value, iMin, iMax);
                        this.val$matrix.postTranslate(this.mDx.value, this.mDy.value);
                        this.val$matrix.postTranslate(remoteAnimationTargetCompat.position.x, remoteAnimationTargetCompat.position.y);
                        this.val$matrix.mapRect(this.val$currentBounds, this.val$targetBounds);
                        if (QuickstepTransitionManager.this.mDeviceProfile.isVerticalBarLayout() || QuickstepTransitionManager.this.mDeviceProfile.isAllowRotationAndLandscape()) {
                            this.val$currentBounds.right -= fWidth2;
                        } else {
                            this.val$currentBounds.bottom -= fHeight3;
                        }
                        rect2 = this.val$crop;
                        f2 = this.mAlpha.value;
                        f3 = this.mWindowRadius.value;
                        FloatingIconView floatingIconView2 = this.val$floatingView;
                        if (floatingIconView2 != null) {
                            floatingIconView2.update(this.val$currentBounds, this.mIconAlpha.value, percent, 0.0f, f3 * this.mScale.value, FloatingIconView.Action.Close);
                        }
                    } else {
                        this.val$matrix.setTranslate(remoteAnimationTargetCompat.position.x, remoteAnimationTargetCompat.position.y);
                        rect2 = remoteAnimationTargetCompat.sourceContainerBounds;
                        f2 = 1.0f;
                        f3 = 0.0f;
                    }
                    surfaceParamsArr[length] = builder.withAlpha(f2).withMatrix(this.val$matrix).withWindowCrop(rect2).withLayer(RemoteAnimationProvider.getLayer(remoteAnimationTargetCompat, 1)).withCornerRadius(f3).build();
                }
                this.val$surfaceApplier.scheduleApply(surfaceParamsArr);
            }
        });
        return valueAnimatorOfFloat;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isClosingFromLandscape(RemoteAnimationTargetCompat[] appTargets) {
        int iWidth = appTargets[0].screenSpaceBounds.width();
        int iHeight = appTargets[0].screenSpaceBounds.height();
        if (appTargets[0].localBounds != null) {
            iWidth = appTargets[0].localBounds.width();
            iHeight = appTargets[0].localBounds.height();
        }
        return iHeight < iWidth;
    }
}
