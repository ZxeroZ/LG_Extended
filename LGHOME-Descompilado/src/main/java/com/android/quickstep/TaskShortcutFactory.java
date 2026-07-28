package com.android.quickstep;

import android.app.Activity;
import android.app.ActivityManagerEx;
import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.WellbeingModel;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.util.InstantAppResolver;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.quickstep.TaskShortcutFactory;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.view.AppTransitionAnimationSpecCompat;
import com.android.systemui.shared.recents.view.AppTransitionAnimationSpecsFuture;
import com.android.systemui.shared.recents.view.RecentsTransition;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.lge.content.pm.PackageManagerEx;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.initialguide.MultiWindowGuideManager;
import com.lge.launcher3.quickstep.FullScreenManager;
import com.lge.launcher3.quickstep.GameToolsDBManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import com.lge.mdm.LGMDMManagerInternal;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface TaskShortcutFactory {
    public static final TaskShortcutFactory APP_INFO = new TaskShortcutFactory() { // from class: com.android.quickstep.-$$Lambda$TaskShortcutFactory$QeqLBq4BOX2Z0A9rbWugIQrs0k4
        @Override // com.android.quickstep.TaskShortcutFactory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
            return TaskShortcutFactory.lambda$static$0(baseDraggingActivity, taskView);
        }
    };
    public static final TaskShortcutFactory SPLIT_SCREEN = new MultiWindowFactory(R.drawable.recentapp_ic_dualwindow_normal, R.string.recentapps_name_multi_window, StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_SPLIT_SCREEN_TAP, true) { // from class: com.android.quickstep.TaskShortcutFactory.1
        @Override // com.android.quickstep.TaskShortcutFactory.MultiWindowFactory
        protected boolean onActivityStarted(BaseDraggingActivity activity) {
            return true;
        }

        @Override // com.android.quickstep.TaskShortcutFactory.MultiWindowFactory
        protected boolean isAvailable(BaseDraggingActivity activity, int displayId) {
            return !activity.getDeviceProfile().isMultiWindowMode && (displayId == -1 || displayId == 0);
        }

        @Override // com.android.quickstep.TaskShortcutFactory.MultiWindowFactory
        protected ActivityOptions makeLaunchOptions(Activity activity) {
            int navBarPosition = WindowManagerWrapper.getInstance().getNavBarPosition(activity.getDisplayId());
            if (navBarPosition == -1) {
                return null;
            }
            return ActivityOptionsCompat.makeSplitScreenOptions(navBarPosition != 1);
        }
    };
    public static final TaskShortcutFactory FREE_FORM = new MultiWindowFactory(R.drawable.recentapp_ic_floating_normal, R.string.recent_task_option_popup_window, StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_FREE_FORM_TAP) { // from class: com.android.quickstep.TaskShortcutFactory.2
        @Override // com.android.quickstep.TaskShortcutFactory.MultiWindowFactory
        protected boolean isAvailable(BaseDraggingActivity activity, int displayId) {
            return ActivityManagerWrapper.getInstance().supportsFreeformMultiWindow(activity);
        }

        @Override // com.android.quickstep.TaskShortcutFactory.MultiWindowFactory
        protected ActivityOptions makeLaunchOptions(Activity activity) {
            ActivityOptions activityOptionsMakeFreeformOptions = ActivityOptionsCompat.makeFreeformOptions();
            Rect rect = new Rect();
            WindowUtils.getFreeformBounds(activity, rect);
            activityOptionsMakeFreeformOptions.setLaunchBounds(rect);
            return activityOptionsMakeFreeformOptions;
        }

        @Override // com.android.quickstep.TaskShortcutFactory.MultiWindowFactory
        protected boolean onActivityStarted(BaseDraggingActivity activity) {
            activity.returnToHomescreen();
            RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(activity).forceInvalidateLoadedTasks();
            return true;
        }
    };
    public static final TaskShortcutFactory PIN = new TaskShortcutFactory() { // from class: com.android.quickstep.-$$Lambda$TaskShortcutFactory$TOKxlc19s171UjdXO51GU4GJ6VE
        @Override // com.android.quickstep.TaskShortcutFactory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
            return TaskShortcutFactory.lambda$static$1(baseDraggingActivity, taskView);
        }
    };
    public static final TaskShortcutFactory INSTALL = new TaskShortcutFactory() { // from class: com.android.quickstep.-$$Lambda$TaskShortcutFactory$nxIbdkdACi7JqcoplfgyZ8Iiwas
        @Override // com.android.quickstep.TaskShortcutFactory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
            return TaskShortcutFactory.lambda$static$2(baseDraggingActivity, taskView);
        }
    };
    public static final TaskShortcutFactory WELLBEING = new TaskShortcutFactory() { // from class: com.android.quickstep.-$$Lambda$TaskShortcutFactory$U4mlWu8h0uFtu0JTC9nlCVcLoGM
        @Override // com.android.quickstep.TaskShortcutFactory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
            return WellbeingModel.SHORTCUT_FACTORY.getShortcut(baseDraggingActivity, taskView.getItemInfo());
        }
    };
    public static final TaskShortcutFactory SCREENSHOT = new TaskShortcutFactory() { // from class: com.android.quickstep.-$$Lambda$TaskShortcutFactory$uZ1P_O1S_2xrZisCW5knj40vfd8
        @Override // com.android.quickstep.TaskShortcutFactory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
            return TaskShortcutFactory.lambda$static$4(baseDraggingActivity, taskView);
        }
    };
    public static final TaskShortcutFactory MODAL = new TaskShortcutFactory() { // from class: com.android.quickstep.-$$Lambda$TaskShortcutFactory$o7iNR19hZNLiAdKGLWmwBH4Dj2Y
        @Override // com.android.quickstep.TaskShortcutFactory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
            return TaskShortcutFactory.lambda$static$5(baseDraggingActivity, taskView);
        }
    };
    public static final TaskShortcutFactory APP_PIN = new TaskShortcutFactory() { // from class: com.android.quickstep.-$$Lambda$TaskShortcutFactory$I_W--8-UNWv0_8ZHYGppYN6xHGU
        @Override // com.android.quickstep.TaskShortcutFactory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
            return TaskShortcutFactory.lambda$static$6(baseDraggingActivity, taskView);
        }
    };
    public static final TaskShortcutFactory GO_FULLSCREEN = new TaskShortcutFactory() { // from class: com.android.quickstep.-$$Lambda$TaskShortcutFactory$SHFuQBCAVwYpN717QJZ6qo-cB_g
        @Override // com.android.quickstep.TaskShortcutFactory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
            return TaskShortcutFactory.lambda$static$7(baseDraggingActivity, taskView);
        }
    };

    SystemShortcut getShortcut(BaseDraggingActivity activity, TaskView view);

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0006: CONSTRUCTOR 
      (r1v0 com.android.launcher3.BaseDraggingActivity)
      (wrap:com.android.launcher3.model.data.WorkspaceItemInfo:0x0002: INVOKE (r2v0 com.android.quickstep.views.TaskView) VIRTUAL call: com.android.quickstep.views.TaskView.getItemInfo():com.android.launcher3.model.data.WorkspaceItemInfo A[MD:():com.android.launcher3.model.data.WorkspaceItemInfo (m), WRAPPED])
     A[MD:(com.android.launcher3.BaseDraggingActivity, com.android.launcher3.model.data.ItemInfo):void (m)] (LINE:85) call: com.android.launcher3.popup.SystemShortcut.AppInfo.<init>(com.android.launcher3.BaseDraggingActivity, com.android.launcher3.model.data.ItemInfo):void type: CONSTRUCTOR */
    static /* synthetic */ SystemShortcut lambda$static$0(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
        return new SystemShortcut.AppInfo(baseDraggingActivity, taskView.getItemInfo());
    }

    public static abstract class MultiWindowFactory implements TaskShortcutFactory {
        private final int mIconRes;
        private final StatsLogManager.LauncherEvent mLauncherEvent;
        private final int mTextRes;
        private boolean mUseGuide;

        protected abstract boolean isAvailable(BaseDraggingActivity activity, int displayId);

        protected abstract ActivityOptions makeLaunchOptions(Activity activity);

        protected abstract boolean onActivityStarted(BaseDraggingActivity activity);

        MultiWindowFactory(int iconRes, int textRes, StatsLogManager.LauncherEvent launcherEvent) {
            this(iconRes, textRes, launcherEvent, false);
        }

        MultiWindowFactory(int iconRes, int textRes, StatsLogManager.LauncherEvent launcherEvent, boolean useGuide) {
            this.mIconRes = iconRes;
            this.mTextRes = textRes;
            this.mLauncherEvent = launcherEvent;
            this.mUseGuide = useGuide;
        }

        @Override // com.android.quickstep.TaskShortcutFactory
        public SystemShortcut getShortcut(BaseDraggingActivity activity, TaskView taskView) {
            Task task = taskView.getTask();
            int displayId = activity != null ? activity.getDisplayId() : 0;
            if (task.isDockable && displayId == 0 && !"com.lge.launcher3".equals(task.key.getPackageName())) {
                return new MultiWindowSystemShortcut(this.mIconRes, this.mTextRes, activity, taskView, this, this.mLauncherEvent, this.mUseGuide);
            }
            return null;
        }
    }

    public static class SplitSelectSystemShortcut extends SystemShortcut {
        private static final String TAG = "SplitSelectSystemShortcut";
        private final SplitConfigurationOptions.SplitPositionOption mSplitPositionOption;
        private final TaskView mTaskView;

        public SplitSelectSystemShortcut(BaseDraggingActivity target, TaskView taskView, SplitConfigurationOptions.SplitPositionOption option) {
            super(option.iconResId, option.textResId, target, taskView.getItemInfo(), taskView);
            this.mTaskView = taskView;
            this.mSplitPositionOption = option;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            try {
                if (!LGMDMManagerInternal.getInstance().checkAllowMultiWindow()) {
                    return;
                }
            } catch (Throwable th) {
                LGLog.i(TAG, "MDM MultiWindow Exception occurs=" + th);
            }
            this.mTaskView.initiateSplitSelect(this.mSplitPositionOption);
        }
    }

    public static class MultiWindowSystemShortcut extends SystemShortcut {
        private static final String TAG = "MultiWindowSystemShortcut";
        private final MultiWindowFactory mFactory;
        private Handler mHandler;
        private final StatsLogManager.LauncherEvent mLauncherEvent;
        private final RecentsView mRecentsView;
        private final TaskView mTaskView;
        private final TaskThumbnailView mThumbnailView;
        private final boolean mUseGuide;

        static /* synthetic */ void lambda$onClick$1() {
        }

        public MultiWindowSystemShortcut(int iconRes, int textRes, BaseDraggingActivity activity, TaskView taskView, MultiWindowFactory factory, StatsLogManager.LauncherEvent launcherEvent) {
            this(iconRes, textRes, activity, taskView, factory, launcherEvent, false);
        }

        public MultiWindowSystemShortcut(int iconRes, int textRes, BaseDraggingActivity activity, TaskView taskView, MultiWindowFactory factory, StatsLogManager.LauncherEvent launcherEvent, boolean useGuide) {
            super(iconRes, textRes, activity, taskView.getItemInfo());
            this.mLauncherEvent = launcherEvent;
            this.mHandler = new Handler(Looper.getMainLooper());
            this.mTaskView = taskView;
            this.mRecentsView = (RecentsView) activity.getOverviewPanel();
            this.mThumbnailView = taskView.getThumbnail();
            this.mFactory = factory;
            this.mUseGuide = useGuide;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Task.TaskKey taskKey = this.mTaskView.getTask().key;
            final int i = taskKey.id;
            dismissTaskMenuView(this.mTarget);
            final boolean z = this.mLauncherEvent == StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_FREE_FORM_TAP;
            if (this.mTarget.getDeviceProfile().isMultiWindowMode && !z) {
                this.mTaskView.launchTaskAnimated();
                return;
            }
            final View.OnLayoutChangeListener onLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: com.android.quickstep.TaskShortcutFactory.MultiWindowSystemShortcut.1
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View v, int l, int t, int r, int b, int oldL, int oldT, int oldR, int oldB) {
                    MultiWindowSystemShortcut.this.mTaskView.getRootView().removeOnLayoutChangeListener(this);
                    MultiWindowSystemShortcut.this.mRecentsView.dismissTask(MultiWindowSystemShortcut.this.mTaskView, false, false);
                }
            };
            DeviceProfile.OnDeviceProfileChangeListener onDeviceProfileChangeListener = new DeviceProfile.OnDeviceProfileChangeListener() { // from class: com.android.quickstep.TaskShortcutFactory.MultiWindowSystemShortcut.2
                @Override // com.android.launcher3.DeviceProfile.OnDeviceProfileChangeListener
                public void onDeviceProfileChanged(DeviceProfile dp) {
                    MultiWindowSystemShortcut.this.mTarget.removeOnDeviceProfileChangeListener(this);
                    if (dp.isMultiWindowMode && !z) {
                        MultiWindowSystemShortcut.this.mTaskView.setAlpha(0.0f);
                        MultiWindowSystemShortcut.this.mTaskView.getRootView().addOnLayoutChangeListener(onLayoutChangeListener);
                    } else {
                        MultiWindowSystemShortcut.this.mTaskView.setAlpha(1.0f);
                    }
                }
            };
            try {
                if (LGMDMManagerInternal.getInstance().checkStartActivity(this.mTarget, this.mTaskView.getTask().key.getComponent().getPackageName(), (String) null, true) != 0) {
                    return;
                }
                if (!LGMDMManagerInternal.getInstance().checkAllowMultiWindow()) {
                    return;
                }
            } catch (Throwable th) {
                LGLog.i(TAG, "MDM MultiWindow Exception occurs=" + th);
            }
            ActivityOptions activityOptionsMakeLaunchOptions = this.mFactory.makeLaunchOptions(this.mTarget);
            if (activityOptionsMakeLaunchOptions != null) {
                activityOptionsMakeLaunchOptions.setSplashScreenStyle(0);
            }
            if (activityOptionsMakeLaunchOptions != null) {
                activityOptionsMakeLaunchOptions.setLaunchDisplayId(0);
                if (ActivityManagerWrapper.getInstance().startActivityFromRecents(i, activityOptionsMakeLaunchOptions) && this.mFactory.onActivityStarted(this.mTarget)) {
                    this.mTarget.addOnDeviceProfileChangeListener(onDeviceProfileChangeListener);
                    new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskShortcutFactory$MultiWindowSystemShortcut$iDoRAhil_37I8EXKNlO2Fsk_Pk8
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onClick$0$TaskShortcutFactory$MultiWindowSystemShortcut(z, i);
                        }
                    };
                    int[] iArr = new int[2];
                    this.mThumbnailView.getLocationOnScreen(iArr);
                    final Rect rect = new Rect(iArr[0], iArr[1], iArr[0] + ((int) (this.mThumbnailView.getWidth() * this.mTaskView.getScaleX())), iArr[1] + ((int) (this.mThumbnailView.getHeight() * this.mTaskView.getScaleY())));
                    float dimAlpha = this.mThumbnailView.getDimAlpha();
                    this.mThumbnailView.setDimAlpha(0.0f);
                    TaskView taskView = this.mTaskView;
                    final Bitmap bitmapDrawViewIntoHardwareBitmap = RecentsTransition.drawViewIntoHardwareBitmap(rect.width(), rect.height(), this.mThumbnailView, taskView != null ? taskView.getScaleX() : 1.0f, ViewCompat.MEASURED_STATE_MASK);
                    this.mThumbnailView.setDimAlpha(dimAlpha);
                    WindowManagerWrapper.getInstance().overridePendingAppTransitionMultiThumbFuture(new AppTransitionAnimationSpecsFuture(this.mHandler) { // from class: com.android.quickstep.TaskShortcutFactory.MultiWindowSystemShortcut.3
                        @Override // com.android.systemui.shared.recents.view.AppTransitionAnimationSpecsFuture
                        public List<AppTransitionAnimationSpecCompat> composeSpecs() {
                            return Collections.singletonList(new AppTransitionAnimationSpecCompat(i, bitmapDrawViewIntoHardwareBitmap, rect));
                        }
                    }, new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskShortcutFactory$MultiWindowSystemShortcut$2yWQHi73K2VuFIzRC22WG5GJL2E
                        @Override // java.lang.Runnable
                        public final void run() {
                            TaskShortcutFactory.MultiWindowSystemShortcut.lambda$onClick$1();
                        }
                    }, this.mHandler, true, taskKey.displayId);
                    ((RecentsView) this.mTarget.getOverviewPanel()).setContentAlpha(0.0f);
                    this.mRecentsView.setIgnoreResetTask(i);
                    if (this.mUseGuide) {
                        MultiWindowGuideManager.getInstance(this.mTarget.getApplicationContext()).showGuide(this.mTarget.getWindow());
                    }
                    this.mTarget.getStatsLogManager().logger().withItemInfo(this.mTaskView.getItemInfo()).log(this.mLauncherEvent);
                }
            }
        }

        public /* synthetic */ void lambda$onClick$0$TaskShortcutFactory$MultiWindowSystemShortcut(boolean z, int i) {
            if (z) {
                return;
            }
            this.mRecentsView.setIgnoreResetTask(i);
            this.mTaskView.setAlpha(0.0f);
        }
    }

    static /* synthetic */ SystemShortcut lambda$static$1(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
        if (!SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(baseDraggingActivity).isActive() || !ActivityManagerWrapper.getInstance().isScreenPinningEnabled() || ActivityManagerWrapper.getInstance().isLockToAppActive() || "com.lge.launcher3".equals(taskView.getTask().key.getPackageName()) || taskView.getTask().key.windowingMode == 5) {
            return null;
        }
        return new PinSystemShortcut(baseDraggingActivity, taskView);
    }

    public static class PinSystemShortcut extends SystemShortcut {
        private static final String TAG = "PinSystemShortcut";
        private final TaskView mTaskView;

        public PinSystemShortcut(BaseDraggingActivity target, TaskView tv) {
            super(R.drawable.recentapp_ic_screen_pin_normal, R.string.app_pin_title, target, tv.getItemInfo());
            this.mTaskView = tv;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.mTaskView.launchTaskAnimated() != null) {
                SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mTarget).startScreenPinning(this.mTaskView.getTask().key.id);
            }
            dismissTaskMenuView(this.mTarget);
            this.mTarget.getStatsLogManager().logger().withItemInfo(this.mTaskView.getItemInfo()).log(StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_PIN_TAP);
        }
    }

    static /* synthetic */ SystemShortcut lambda$static$2(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
        if (InstantAppResolver.newInstance(baseDraggingActivity).isInstantApp(baseDraggingActivity, taskView.getTask().getTopComponent().getPackageName())) {
            return new SystemShortcut.Install(baseDraggingActivity, taskView.getItemInfo());
        }
        return null;
    }

    static /* synthetic */ SystemShortcut lambda$static$4(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
        if (FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get()) {
            return taskView.getThumbnail().getTaskOverlay().getScreenshotShortcut(baseDraggingActivity, taskView.getItemInfo());
        }
        return null;
    }

    static /* synthetic */ SystemShortcut lambda$static$5(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
        if (FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get() && FeatureFlags.ENABLE_OVERVIEW_SELECTIONS.get()) {
            return taskView.getThumbnail().getTaskOverlay().getModalStateSystemShortcut(taskView.getItemInfo());
        }
        return null;
    }

    static /* synthetic */ SystemShortcut lambda$static$6(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
        if (TaskUtils.hasAutoRemoveRecentFlag(taskView.getTask())) {
            LGLog.i("AppPinSystemShortcut", "hasAutoRemoveRecentFlag is exist, so remove APP_PIN menu");
            return null;
        }
        if ("com.lge.launcher3".equals(taskView.getTask().key.getPackageName()) || LGFeatureConfig.FEATURE_OPERATOR.equals("VZW")) {
            return null;
        }
        return new AppPinSystemShortcut(baseDraggingActivity, taskView);
    }

    public static class AppPinSystemShortcut extends SystemShortcut {
        private static final String TAG = "AppPinSystemShortcut";
        private TaskView mTaskView;

        public AppPinSystemShortcut(BaseDraggingActivity target, TaskView tv) {
            super(R.drawable.recentapp_ic_pin_app_normal, R.string.recentapps_task_lock_app, target, tv.getItemInfo());
            this.mTaskView = tv;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            boolean z = !this.mTaskView.getTask().isPinned;
            this.mTaskView.getTask().isPinned = z;
            ActivityManagerEx activityManagerEx = (ActivityManagerEx) view.getContext().getSystemService("activity");
            if (activityManagerEx != null) {
                if (z) {
                    activityManagerEx.updateFlag(3, this.mTaskView.getTask().key.id);
                    this.mTaskView.announceForAccessibility(view.getContext().getString(R.string.recentapps_task_pin_on));
                    Toast.makeText(this.mTaskView.getContext(), R.string.recentapps_task_lock_app_toast, 0).show();
                } else {
                    activityManagerEx.updateFlag(2, this.mTaskView.getTask().key.id);
                    this.mTaskView.announceForAccessibility(view.getContext().getString(R.string.recentapps_task_pin_off));
                }
            }
            if (z) {
                this.mTaskView.findViewById(R.id.pinned_btn).setVisibility(0);
            } else {
                this.mTaskView.findViewById(R.id.pinned_btn).setVisibility(4);
            }
            this.mTaskView.findViewById(R.id.dismiss_btn).setAlpha(z ? 0.5f : 1.0f);
            this.mTaskView.getRecentsView().updateClearAllEnabled();
            dismissTaskMenuView(this.mTarget);
        }
    }

    static /* synthetic */ SystemShortcut lambda$static$7(BaseDraggingActivity baseDraggingActivity, TaskView taskView) {
        if (!LGHomeFeature.Config.FEATURE_USE_GO_FULL_SCREEN.getValue()) {
            LGLog.i(GoFullScreenSystemShortcut.TAG, "GoFullScreen::getOnClickListener GoFullScreen is not supported. LGHomeFeature.Config.FEATURE_USE_GO_FULL_SCREEN.getValue() : " + LGHomeFeature.Config.FEATURE_USE_GO_FULL_SCREEN.getValue());
            return null;
        }
        if (baseDraggingActivity == null || taskView == null || taskView.getTask() == null || taskView.getTask().key == null || taskView.getTask().key.getComponent() == null) {
            LGLog.i(GoFullScreenSystemShortcut.TAG, "GoFullScreen::getOnClickListener activity is null. Or It cannot get package name.");
            return null;
        }
        String packageName = taskView.getTask().key.getComponent().getPackageName();
        boolean zIsSupportFullScreen = GoFullScreenSystemShortcut.isSupportFullScreen(baseDraggingActivity, packageName);
        LGLog.i(GoFullScreenSystemShortcut.TAG, "GoFullScreen::getOnClickListener  packageName : " + packageName + " supportFullScreen: " + zIsSupportFullScreen);
        if (zIsSupportFullScreen) {
            return new GoFullScreenSystemShortcut(baseDraggingActivity, taskView);
        }
        return null;
    }

    public static class GoFullScreenSystemShortcut extends SystemShortcut {
        public static final String TAG = "GoFullScreenSystemShortcut";
        private TaskView mTaskView;

        public GoFullScreenSystemShortcut(BaseDraggingActivity target, TaskView tv) {
            super(R.drawable.recentapp_ic_go_to_fullscreen_normal, R.string.gametools_scale_to_full_screen_7_0, target, tv.getItemInfo());
            this.mTaskView = tv;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Task task = this.mTaskView.getTask();
            try {
                try {
                    SystemUiProxy systemUiProxyLambda$get$0$MainThreadInitializedObject = SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mTarget);
                    String packageName = task.key.getComponent().getPackageName();
                    LGLog.i(TAG, "GoFullScreen::getOnClickListener  sysUiProxy = " + systemUiProxyLambda$get$0$MainThreadInitializedObject + ", DisplayId = " + this.mTarget.getDisplayId() + ", packageName = " + packageName);
                    if (systemUiProxyLambda$get$0$MainThreadInitializedObject != null) {
                        systemUiProxyLambda$get$0$MainThreadInitializedObject.startFullscreenMode(this.mTarget.getDisplayId(), packageName);
                    }
                } catch (RemoteException e) {
                    LGLog.e(TAG, "GoFullScreen::onClick Remote exception on sysUiProxy.", e);
                }
            } catch (Throwable unused) {
            }
            LGLog.d(TAG, "Clicked");
            dismissTaskMenuView(this.mTarget);
        }

        static boolean isSupportFullScreen(Context context, String topPkgName) {
            if (context == null || topPkgName == null || topPkgName.isEmpty()) {
                LGLog.i(TAG, "GoFullScreen::isSupportFullScreen  context = " + context + ", topPkgName = " + topPkgName);
                return false;
            }
            boolean zIsInDBList = GameToolsDBManager.getInstance(context).isInDBList(topPkgName);
            if (zIsInDBList) {
                LGLog.i(TAG, "GoFullScreen::isSupportFullScreen  context = " + context + ", topPkgName = " + topPkgName + ", isInGameDB = " + zIsInDBList);
                return false;
            }
            boolean zIsInFullScreenMode = FullScreenManager.getInstance(context).isInFullScreenMode(topPkgName);
            if (zIsInFullScreenMode) {
                LGLog.i(TAG, "GoFullScreen::isSupportFullScreen  context = " + context + ", topPkgName = " + topPkgName + ", isInFullScreen = " + zIsInFullScreenMode);
                return false;
            }
            PackageManagerEx packageManagerEx = PackageManagerEx.getDefault();
            return packageManagerEx != null && packageManagerEx.isLongDisplayCompatModeNeeded(context, topPkgName);
        }
    }
}
