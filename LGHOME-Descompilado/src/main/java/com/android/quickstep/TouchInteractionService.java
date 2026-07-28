package com.android.quickstep;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.RecentTaskInfoEx;
import android.app.RemoteAction;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Icon;
import android.hardware.display.ICoverDisplayEnabledCallback;
import android.hardware.display.IDisplayManagerEx;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Choreographer;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.tracing.nano.LauncherTraceProto;
import com.android.launcher3.tracing.nano.TouchInteractionServiceProto;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.TraceHelper;
import com.android.launcher3.util.WindowBounds;
import com.android.quickstep.BaseSwipeUpHandler;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.TouchInteractionService;
import com.android.quickstep.inputconsumers.AccessibilityInputConsumer;
import com.android.quickstep.inputconsumers.AssistantInputConsumer;
import com.android.quickstep.inputconsumers.DeviceLockedInputConsumer;
import com.android.quickstep.inputconsumers.OneHandedModeInputConsumer;
import com.android.quickstep.inputconsumers.OtherActivityInLandscapeInputConsumer;
import com.android.quickstep.inputconsumers.OtherActivityInputConsumer;
import com.android.quickstep.inputconsumers.OverviewInputConsumer;
import com.android.quickstep.inputconsumers.ProgressDelegateInputConsumer;
import com.android.quickstep.inputconsumers.ResetGestureInputConsumer;
import com.android.quickstep.inputconsumers.ScreenPinnedInputConsumer;
import com.android.quickstep.inputconsumers.SysUiOverlayInputConsumer;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.AssistantUtilities;
import com.android.quickstep.util.ProtoTracer;
import com.android.quickstep.util.ProxyScreenStatusProvider;
import com.android.quickstep.util.SplitScreenBounds;
import com.android.systemui.plugins.OverscrollPlugin;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.shared.recents.ISystemUiProxy;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputChannelCompat;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController;
import com.android.systemui.shared.tracing.ProtoTraceable;
import com.android.wm.shell.back.IBackAnimation;
import com.android.wm.shell.onehanded.IOneHanded;
import com.android.wm.shell.pip.IPip;
import com.android.wm.shell.recents.IRecentTasks;
import com.android.wm.shell.splitscreen.ISplitScreen;
import com.android.wm.shell.startingsurface.IStartingWindow;
import com.android.wm.shell.transition.IShellTransitions;
import com.lge.display.DisplayManagerHelper;
import com.lge.launcher3.R;
import com.lge.launcher3.StylusBlockSettingObserver;
import com.lge.launcher3.quickstep.ActivityManagerWrapperEx;
import com.lge.launcher3.quickstep.InputConsumerControllerEx;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import com.lge.systemservice.core.IPostureStateCallback;
import com.lge.systemservice.core.LGContext;
import com.lge.systemservice.core.PostureManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class TouchInteractionService extends Service implements ProtoTraceable<LauncherTraceProto> {
    private static final int COVER_DISPLAY_ID = 1;
    private static final boolean FILTER_OUT_STYLUS = SystemProperties.getBoolean("product.lge.systemui.debug.gesture_filter_out_stylus.enabled", false);
    private static final String HAS_ENABLED_QUICKSTEP_ONCE = "launcher.has_enabled_quickstep_once";
    private static final String KEY_BACK_NOTIFICATION_COUNT = "backNotificationCount";
    private static final int MAX_BACK_NOTIFICATION_COUNT = 3;
    private static final int MAX_MULTI_WINDOW_TASK = 2;
    private static final int MOST_RECENT_TASK_INDEX = 0;
    public static final String MTAG = "[MULIT_DISPLAY]";
    private static final String NOTIFY_ACTION_BACK = "com.android.quickstep.action.BACK_GESTURE";
    private static final int SWIVEL_DISPLAY_ID = 4;
    private static final int SYSTEM_ACTION_ID_ALL_APPS = 14;
    private static final String TAG = "TouchInteractionService";
    private static boolean sConnected;
    private static boolean sIsInitialized;
    private ActivityManagerWrapper mAM;
    private RecentsAnimationDeviceState mDeviceState;
    private DisplayManagerHelper mDisplayManagerHelper;
    private InputConsumerController mInputConsumer;
    private InputConsumerControllerEx mInputConsumerForMultiDisplay;
    private InputChannelCompat.InputEventReceiver mInputEventReceiver;
    private InputChannelCompat.InputEventReceiver mInputEventReceiverForMulti;
    private InputMonitorCompat mInputMonitorCompat;
    private InputMonitorCompat mInputMonitorCompatForMulti;
    private Choreographer mMainChoreographer;
    private OverscrollPlugin mOverscrollPlugin;
    private OverviewCommandHelper mOverviewCommandHelper;
    private OverviewComponentObserver mOverviewComponentObserver;
    private PostureManager mPostureManager;
    private InputConsumer mResetGestureInputConsumer;
    private StylusBlockSettingObserver mStylusBlockSettingObserver;
    private TaskAnimationManager mTaskAnimationManager;
    private int mBackGestureNotificationCounter = -1;
    private final IBinder mMyBinder = new AnonymousClass1();
    private final BaseSwipeUpHandler.Factory mLauncherSwipeHandlerFactory = new BaseSwipeUpHandler.Factory() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$maHoAWRwNMbzOvfjsiw6xrzRG08
        @Override // com.android.quickstep.BaseSwipeUpHandler.Factory
        public final BaseSwipeUpHandler newHandler(GestureState gestureState, long j, boolean z) {
            return this.f$0.createLauncherSwipeHandler(gestureState, j, z);
        }
    };
    private final BaseSwipeUpHandler.Factory mFallbackSwipeHandlerFactory = new BaseSwipeUpHandler.Factory() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$uAPnt3ZVFbycUvpLcE29PIYEI_0
        @Override // com.android.quickstep.BaseSwipeUpHandler.Factory
        public final BaseSwipeUpHandler newHandler(GestureState gestureState, long j, boolean z) {
            return this.f$0.createFallbackSwipeHandler(gestureState, j, z);
        }
    };
    private InputConsumer mUncheckedConsumer = InputConsumer.NO_OP;
    private InputConsumer mConsumer = InputConsumer.NO_OP;
    private GestureState mGestureState = GestureState.DEFAULT_STATE;
    private Function<GestureState, AnimatedFloat> mSwipeUpProxyProvider = new Function() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$sS1wCGZjpSGERlfGVKkK8cxW-vg
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return TouchInteractionService.lambda$new$0((GestureState) obj);
        }
    };
    private final ICoverDisplayEnabledCallback mCoverDisplayCallback = new AnonymousClass2();
    private final IPostureStateCallback mSubSwivelDisplayCallback = new AnonymousClass3();

    static /* synthetic */ AnimatedFloat lambda$new$0(GestureState gestureState) {
        return null;
    }

    /* JADX INFO: renamed from: com.android.quickstep.TouchInteractionService$1, reason: invalid class name */
    class AnonymousClass1 extends IOverviewProxy.Stub {
        private void executeForTaskbarManager(final Runnable r) {
        }

        static /* synthetic */ AnimatedFloat lambda$setSwipeUpProxy$7(GestureState gestureState) {
            return null;
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void disable(int displayId, int state1, int state2, boolean animate) {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onNavButtonsDarkIntensityChanged(float darkIntensity) {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onRotationProposal(int rotation, boolean isValid) {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onSystemBarAttributesChanged(int displayId, int behavior) {
        }

        public void setGestureBlockedTaskId(int taskId) {
        }

        AnonymousClass1() {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onInitialize(Bundle bundle) {
            final ISystemUiProxy iSystemUiProxyAsInterface = ISystemUiProxy.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SYSUI_PROXY));
            final IPip iPipAsInterface = IPip.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_PIP));
            final ISplitScreen iSplitScreenAsInterface = ISplitScreen.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_SPLIT_SCREEN));
            final IOneHanded iOneHandedAsInterface = IOneHanded.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_ONE_HANDED));
            final IShellTransitions iShellTransitionsAsInterface = IShellTransitions.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_SHELL_TRANSITIONS));
            final IStartingWindow iStartingWindowAsInterface = IStartingWindow.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_STARTING_WINDOW));
            final ISysuiUnlockAnimationController iSysuiUnlockAnimationControllerAsInterface = ISysuiUnlockAnimationController.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_UNLOCK_ANIMATION_CONTROLLER));
            final IRecentTasks iRecentTasksAsInterface = IRecentTasks.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_RECENT_TASKS));
            final IBackAnimation iBackAnimationAsInterface = IBackAnimation.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_BACK_ANIMATION));
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$1$N-Y5ilcGzW7NqftSRDAL6j5RmpU
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onInitialize$0$TouchInteractionService$1(iSystemUiProxyAsInterface, iPipAsInterface, iSplitScreenAsInterface, iOneHandedAsInterface, iShellTransitionsAsInterface, iStartingWindowAsInterface, iRecentTasksAsInterface, iSysuiUnlockAnimationControllerAsInterface, iBackAnimationAsInterface);
                }
            });
            TouchInteractionService.sIsInitialized = true;
        }

        public /* synthetic */ void lambda$onInitialize$0$TouchInteractionService$1(ISystemUiProxy iSystemUiProxy, IPip iPip, ISplitScreen iSplitScreen, IOneHanded iOneHanded, IShellTransitions iShellTransitions, IStartingWindow iStartingWindow, IRecentTasks iRecentTasks, ISysuiUnlockAnimationController iSysuiUnlockAnimationController, IBackAnimation iBackAnimation) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(TouchInteractionService.this).setProxy(iSystemUiProxy, iPip, iSplitScreen, iOneHanded, iShellTransitions, iStartingWindow, iRecentTasks, iSysuiUnlockAnimationController, iBackAnimation);
            TouchInteractionService.this.initInputMonitor();
            TouchInteractionService.this.initInputMonitorForMulti();
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewToggle() {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "onOverviewToggle");
            TouchInteractionService.this.mOverviewCommandHelper.onOverviewToggle();
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewShown(boolean triggeredFromAltTab) {
            TouchInteractionService.this.mOverviewCommandHelper.onOverviewShown(triggeredFromAltTab);
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewHidden(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) {
            if (!triggeredFromAltTab || triggeredFromHomeKey) {
                return;
            }
            TouchInteractionService.this.mOverviewCommandHelper.onOverviewHidden();
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onTip(int actionType, int viewType) {
            TouchInteractionService.this.mOverviewCommandHelper.onTip(actionType, viewType);
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onAssistantAvailable(final boolean available) {
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$1$5viw6pIsmP1_EXdI-s4CvDU9he0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onAssistantAvailable$1$TouchInteractionService$1(available);
                }
            });
        }

        public /* synthetic */ void lambda$onAssistantAvailable$1$TouchInteractionService$1(boolean z) {
            TouchInteractionService.this.mDeviceState.setAssistantAvailable(z);
            TouchInteractionService.this.onAssistantVisibilityChanged();
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onAssistantVisibilityChanged(final float visibility) {
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$1$YB4z5pjEWLXfA8qWNUO2S2AsrAQ
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onAssistantVisibilityChanged$2$TouchInteractionService$1(visibility);
                }
            });
        }

        public /* synthetic */ void lambda$onAssistantVisibilityChanged$2$TouchInteractionService$1(float f) {
            TouchInteractionService.this.mDeviceState.setAssistantVisibility(f);
            TouchInteractionService.this.onAssistantVisibilityChanged();
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onBackAction(boolean completed, int downX, int downY, boolean isButton, boolean gestureSwipeLeft) {
            if (TouchInteractionService.this.mOverviewComponentObserver == null) {
                return;
            }
            UserEventDispatcher.newInstance(TouchInteractionService.this.getBaseContext()).logActionBack(completed, downX, downY, isButton, gestureSwipeLeft, TouchInteractionService.this.mOverviewComponentObserver.getActivityInterface().getContainerType());
            if (completed && !isButton && TouchInteractionService.this.shouldNotifyBackGesture()) {
                LooperExecutor looperExecutor = Executors.UI_HELPER_EXECUTOR;
                final TouchInteractionService touchInteractionService = TouchInteractionService.this;
                looperExecutor.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$i69_pWvts76JMxNEJkDekVlVPL0
                    @Override // java.lang.Runnable
                    public final void run() {
                        touchInteractionService.tryNotifyBackGesture();
                    }
                });
            }
        }

        public void onSystemUiStateChanged(final int stateFlags) {
            LGLog.i(TouchInteractionService.TAG, String.format("onSystemUiStateChanged. stateFlags(%s)", Integer.valueOf(stateFlags)));
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$1$DJwHua-Bb9G3wVGH7HSk9yqYRXg
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onSystemUiStateChanged$3$TouchInteractionService$1(stateFlags);
                }
            });
        }

        public /* synthetic */ void lambda$onSystemUiStateChanged$3$TouchInteractionService$1(int i) {
            TouchInteractionService.this.mDeviceState.setSystemUiFlags(i);
            TouchInteractionService.this.onSystemUiFlagsChanged();
        }

        public /* synthetic */ void lambda$onActiveNavBarRegionChanges$4$TouchInteractionService$1(Region region) {
            TouchInteractionService.this.mDeviceState.setDeferredGestureRegion(region);
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onActiveNavBarRegionChanges(final Region region) {
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$1$-F1yK9hB4m-sXzqb7rOS0UEc6qY
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onActiveNavBarRegionChanges$4$TouchInteractionService$1(region);
                }
            });
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onSplitScreenSecondaryBoundsChanged(Rect bounds, Rect insets) {
            final WindowBounds windowBounds = new WindowBounds(bounds, insets, DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(TouchInteractionService.this.getApplicationContext()).getInfo(0).rotation);
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$1$q9S4byBIh2vdEQNp5huGPCtrOPo
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onSplitScreenSecondaryBoundsChanged$5$TouchInteractionService$1(windowBounds);
                }
            });
        }

        public /* synthetic */ void lambda$onSplitScreenSecondaryBoundsChanged$5$TouchInteractionService$1(WindowBounds windowBounds) {
            SplitScreenBounds.INSTANCE.setSecondaryWindowBounds(windowBounds, TouchInteractionService.this.getApplicationContext());
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onSystemUiStateChanged(final int displayId, final int stateFlags) throws RemoteException {
            LGLog.i(TouchInteractionService.TAG, String.format("onSystemUiStateChanged. displayId(%s), stateFlags(%s)", Integer.valueOf(displayId), Integer.valueOf(stateFlags)));
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$1$OIBMl92_FDR0gFULqzhUvZN33sk
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onSystemUiStateChanged$6$TouchInteractionService$1(displayId, stateFlags);
                }
            });
        }

        public /* synthetic */ void lambda$onSystemUiStateChanged$6$TouchInteractionService$1(int i, int i2) {
            TouchInteractionService.this.mDeviceState.setSystemUiFlags(i, i2);
            if (i == 0) {
                TouchInteractionService.this.onSystemUiFlagsChanged();
            }
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onDesktopAppDrawerToggle(int mode) throws RemoteException {
            LGLog.i(TouchInteractionService.TAG, "onDesktopAppDrawerToggle()");
            TouchInteractionService.this.mOverviewCommandHelper.onDesktopAppDrawerToggle(0);
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewShownDisplayId(int displayId, boolean triggeredFromAltTab) throws RemoteException {
            Log.d(TouchInteractionService.TAG, "onOverviewShownDisplayId");
            onOverviewShown(triggeredFromAltTab);
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewHiddenDisplayId(int displayId, boolean triggeredFromAltTab, boolean triggeredFromHomeKey) throws RemoteException {
            Log.d(TouchInteractionService.TAG, "onOverviewShownDisplayId");
            onOverviewHidden(triggeredFromAltTab, triggeredFromHomeKey);
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onScreenTurnedOn() {
            LooperExecutor looperExecutor = Executors.MAIN_EXECUTOR;
            final ProxyScreenStatusProvider proxyScreenStatusProvider = ProxyScreenStatusProvider.INSTANCE;
            Objects.requireNonNull(proxyScreenStatusProvider);
            looperExecutor.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$WRCXUDcuOt87__dPORTFAVjxr7Q
                @Override // java.lang.Runnable
                public final void run() {
                    proxyScreenStatusProvider.onScreenTurnedOn();
                }
            });
        }

        public OverviewCommandHelper getOverviewCommandHelper() {
            return TouchInteractionService.this.mOverviewCommandHelper;
        }

        public void setSwipeUpProxy(Function<GestureState, AnimatedFloat> proxy) {
            TouchInteractionService touchInteractionService = TouchInteractionService.this;
            if (proxy == null) {
                proxy = new Function() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$1$xPEP9jN5GVCvV_sbu8T-fnmJyzk
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return TouchInteractionService.AnonymousClass1.lambda$setSwipeUpProxy$7((GestureState) obj);
                    }
                };
            }
            touchInteractionService.mSwipeUpProxyProvider = proxy;
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewToggleDisplayId(int displayId) throws RemoteException {
            Log.d(TouchInteractionService.TAG, "onOverviewToggleDisplayId : " + displayId);
            TouchInteractionService.this.mOverviewCommandHelper.onOverviewToggle(displayId);
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onRecentLongPressed() throws RemoteException {
            Log.d(TouchInteractionService.TAG, "onRecentLongPressed");
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$1$F3tWQtLqptAJm5nDCzCWyzY0Luc
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onRecentLongPressed$8$TouchInteractionService$1();
                }
            });
        }

        public /* synthetic */ void lambda$onRecentLongPressed$8$TouchInteractionService$1() {
            TouchInteractionService.this.launchSplitSelectState();
        }
    }

    public static boolean isConnected() {
        return sConnected;
    }

    public static boolean isInitialized() {
        return sIsInitialized;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mMainChoreographer = Choreographer.getInstance();
        this.mAM = ActivityManagerWrapper.getInstance();
        StylusBlockSettingObserver stylusBlockSettingObserver = new StylusBlockSettingObserver(getApplicationContext(), new Handler());
        this.mStylusBlockSettingObserver = stylusBlockSettingObserver;
        stylusBlockSettingObserver.registerObserver(getApplicationContext());
        if (DisplayManagerHelper.isMultiDisplayDevice() && getMultiDisplayId() == 4) {
            this.mPostureManager = (PostureManager) new LGContext(getApplicationContext()).getLGSystemService("postureservice");
        }
        RecentsAnimationDeviceState recentsAnimationDeviceState = new RecentsAnimationDeviceState(this);
        this.mDeviceState = recentsAnimationDeviceState;
        recentsAnimationDeviceState.addNavigationModeChangedCallback(new SysUINavigationMode.NavigationModeChangeListener() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$JZBxllaQUjiddUlfZaPgpd-hrG8
            @Override // com.android.quickstep.SysUINavigationMode.NavigationModeChangeListener
            public final void onNavigationModeChanged(SysUINavigationMode.Mode mode) {
                this.f$0.onNavigationModeChanged(mode);
            }
        });
        this.mDeviceState.addOneHandedModeChangedCallback(new SysUINavigationMode.OneHandedModeChangeListener() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$-vT9sMCKYtCEEnRLdQqrwQvHysQ
            @Override // com.android.quickstep.SysUINavigationMode.OneHandedModeChangeListener
            public final void onOneHandedModeChanged(int i) {
                this.f$0.onOneHandedModeOverlayChanged(i);
            }
        });
        this.mDeviceState.runOnUserUnlocked(new Runnable() { // from class: com.android.quickstep.-$$Lambda$LdzDt0vhnC_sNqnYL0vC_yqepE0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.onUserUnlocked();
            }
        });
        ProtoTracer.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).add(this);
        sConnected = true;
    }

    private void disposeEventHandlers() {
        InputChannelCompat.InputEventReceiver inputEventReceiver = this.mInputEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        InputMonitorCompat inputMonitorCompat = this.mInputMonitorCompat;
        if (inputMonitorCompat != null) {
            inputMonitorCompat.dispose();
            this.mInputMonitorCompat = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initInputMonitor() {
        disposeEventHandlers();
        if (this.mDeviceState.isButtonNavMode() || !SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).isActive()) {
            return;
        }
        InputMonitorCompat inputMonitorCompat = new InputMonitorCompat("swipe-up", this.mDeviceState.getDisplayId());
        this.mInputMonitorCompat = inputMonitorCompat;
        this.mInputEventReceiver = inputMonitorCompat.getInputReceiver(Looper.getMainLooper(), this.mMainChoreographer, new $$Lambda$TouchInteractionService$QnySfMPM3HQvC_OREg1W70p37mY(this));
        this.mDeviceState.updateGestureTouchRegions();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNavigationModeChanged(SysUINavigationMode.Mode mode) {
        initInputMonitor();
        resetHomeBounceSeenOnQuickstepEnabledFirstTime();
        canUseMultiDisplay();
        if (mode.hasGestures) {
            if (DisplayManagerHelper.isMultiDisplayDevice()) {
                if (this.mDisplayManagerHelper.getMultiDisplayId() == 1) {
                    registerCoverDisplayEnabledCallback(this.mCoverDisplayCallback);
                } else if (this.mDisplayManagerHelper.getMultiDisplayId() == 4) {
                    registerSwivelDisplayEnabledCallback(this.mSubSwivelDisplayCallback);
                }
            }
        } else if (DisplayManagerHelper.isMultiDisplayDevice()) {
            if (this.mDisplayManagerHelper.getMultiDisplayId() == 1) {
                unregisterCoverDisplayEnabledCallback(this.mCoverDisplayCallback);
            } else if (this.mDisplayManagerHelper.getMultiDisplayId() == 4) {
                unregisterSwivelDisplayEnabledCallback(this.mSubSwivelDisplayCallback);
            }
        }
        initInputMonitorForMulti();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onOneHandedModeOverlayChanged(int newGesturalHeight) {
        initInputMonitor();
    }

    public void onUserUnlocked() {
        this.mTaskAnimationManager = new TaskAnimationManager(this);
        this.mOverviewComponentObserver = new OverviewComponentObserver(this, this.mDeviceState);
        this.mOverviewCommandHelper = new OverviewCommandHelper(this, this.mDeviceState, this.mOverviewComponentObserver);
        this.mResetGestureInputConsumer = new ResetGestureInputConsumer(this.mTaskAnimationManager);
        InputConsumerController recentsAnimationInputConsumer = InputConsumerController.getRecentsAnimationInputConsumer();
        this.mInputConsumer = recentsAnimationInputConsumer;
        recentsAnimationInputConsumer.registerInputConsumer();
        registerInputConsumerControllerEx(getMultiDisplayId());
        onSystemUiFlagsChanged();
        onAssistantVisibilityChanged();
        this.mBackGestureNotificationCounter = Math.max(0, Utilities.getDevicePrefs(this).getInt(KEY_BACK_NOTIFICATION_COUNT, 3));
        resetHomeBounceSeenOnQuickstepEnabledFirstTime();
        this.mOverviewComponentObserver.setOverviewChangeListener(new Consumer() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$nIalosRb7rTJlm_Cfw9B_R5rQ8E
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.onOverviewTargetChange(((Boolean) obj).booleanValue());
            }
        });
        onOverviewTargetChange(this.mOverviewComponentObserver.isHomeAndOverviewSame());
    }

    public OverviewCommandHelper getOverviewCommandHelper() {
        return this.mOverviewCommandHelper;
    }

    private void resetHomeBounceSeenOnQuickstepEnabledFirstTime() {
        if (!this.mDeviceState.isUserUnlocked() || this.mDeviceState.isButtonNavMode()) {
            return;
        }
        SharedPreferences prefs = Utilities.getPrefs(this);
        if (prefs.getBoolean(HAS_ENABLED_QUICKSTEP_ONCE, true)) {
            return;
        }
        prefs.edit().putBoolean(HAS_ENABLED_QUICKSTEP_ONCE, true).putBoolean("launcher.apps_view_shown", false).apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onOverviewTargetChange(boolean isHomeAndOverviewSame) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(AccessibilityManager.class);
        if (isHomeAndOverviewSame) {
            accessibilityManager.registerSystemAction(new RemoteAction(Icon.createWithResource(this, R.drawable.ic_apps), getString(R.string.all_apps_label), getString(R.string.all_apps_label), PendingIntent.getActivity(this, 14, new Intent(this.mOverviewComponentObserver.getHomeIntent()).setAction("android.intent.action.ALL_APPS"), 201326592)), 14);
        } else {
            accessibilityManager.unregisterSystemAction(14);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSystemUiFlagsChanged() {
        if (this.mDeviceState.isUserUnlocked()) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).setLastSystemUiStateFlags(this.mDeviceState.getSystemUiStateFlags());
            this.mOverviewComponentObserver.onSystemUiStateChanged();
            if ((this.mDeviceState.getSystemUiStateFlags() & 4096) != 0) {
                ProtoTracer.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).start();
            } else {
                ProtoTracer.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).stop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAssistantVisibilityChanged() {
        if (this.mDeviceState.isUserUnlocked()) {
            this.mOverviewComponentObserver.getActivityInterface().onAssistantVisibilityChanged(this.mDeviceState.getAssistantVisibility());
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        sIsInitialized = false;
        if (this.mDeviceState.isUserUnlocked()) {
            this.mInputConsumer.unregisterInputConsumer();
            this.mOverviewComponentObserver.onDestroy();
            unregisterInputConsumerControllerEx(getMultiDisplayId());
        }
        if (this.mDeviceState.getNavMode().hasGestures && DisplayManagerHelper.isMultiDisplayDevice()) {
            if (this.mDisplayManagerHelper.getMultiDisplayId() == 1) {
                unregisterCoverDisplayEnabledCallback(this.mCoverDisplayCallback);
            } else if (this.mDisplayManagerHelper.getMultiDisplayId() == 4) {
                unregisterSwivelDisplayEnabledCallback(this.mSubSwivelDisplayCallback);
            }
        }
        StylusBlockSettingObserver stylusBlockSettingObserver = this.mStylusBlockSettingObserver;
        if (stylusBlockSettingObserver != null) {
            stylusBlockSettingObserver.unregisterObserver(getApplicationContext());
            this.mStylusBlockSettingObserver = null;
        }
        disposeEventHandlers();
        disposeEventHandlersForMulti();
        this.mDeviceState.destroy();
        SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).lambda$new$0$SystemUiProxy();
        ProtoTracer.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).stop();
        ProtoTracer.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).remove(this);
        ((AccessibilityManager) getSystemService(AccessibilityManager.class)).unregisterSystemAction(14);
        sConnected = false;
        super.onDestroy();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Touch service connected");
        return this.mMyBinder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInputEvent(InputEvent ev) {
        InputConsumer inputConsumer;
        if (!(ev instanceof MotionEvent)) {
            Log.e(TAG, "Unknown event " + ev);
            return;
        }
        MotionEvent motionEvent = (MotionEvent) ev;
        TestLogging.recordMotionEvent(TestProtocol.SEQUENCE_TIS, "TouchInteractionService.onInputEvent", motionEvent);
        if (!needToSkipStylusEvent(1, motionEvent) && this.mDeviceState.isUserUnlocked()) {
            Object objBeginFlagsOverride = TraceHelper.INSTANCE.beginFlagsOverride(1);
            int action = motionEvent.getAction();
            int displayId = motionEvent.getDisplayId();
            boolean zIsMultiDisplayId = isMultiDisplayId(displayId);
            if (action == 0) {
                if (TestProtocol.sDebugTracing) {
                    Log.d(TestProtocol.NO_SWIPE_TO_HOME, "TouchInteractionService.onInputEvent:DOWN");
                }
                this.mDeviceState.setOrientationTransformIfNeeded(motionEvent, displayId);
                if (!this.mDeviceState.isOneHandedModeActive() && this.mDeviceState.isInSwipeUpTouchRegion(motionEvent)) {
                    if (TestProtocol.sDebugTracing) {
                        Log.d(TestProtocol.NO_SWIPE_TO_HOME, "TouchInteractionService.onInputEvent:isInSwipeUpTouchRegion");
                    }
                    GestureState gestureState = new GestureState(this.mGestureState);
                    GestureState gestureStateCreateGestureState = createGestureState(this.mGestureState, displayId);
                    gestureStateCreateGestureState.setSwipeUpStartTimeMs(SystemClock.uptimeMillis());
                    if (gestureState.getDisplayId() != gestureStateCreateGestureState.getDisplayId()) {
                        ActivityManagerWrapper.getInstance().cancelRecentsAnimation(true);
                    }
                    this.mConsumer.onConsumerAboutToBeSwitched();
                    this.mGestureState = gestureStateCreateGestureState;
                    this.mConsumer = newConsumer(gestureState, gestureStateCreateGestureState, motionEvent);
                    ActiveGestureLog.INSTANCE.addLog("setInputConsumer: " + this.mConsumer.getName());
                    this.mUncheckedConsumer = this.mConsumer;
                } else {
                    if (this.mGestureState.getDisplayId() != displayId) {
                        LGLog.d(TAG, "onInputEvent : skip. " + this.mGestureState.getDisplayId() + ", " + displayId);
                        return;
                    }
                    if (this.mDeviceState.isUserUnlocked() && this.mDeviceState.isFullyGesturalNavMode()) {
                        GestureState gestureStateCreateGestureState2 = createGestureState(this.mGestureState, displayId);
                        this.mGestureState = gestureStateCreateGestureState2;
                        if (this.mDeviceState.canTriggerAssistantAction(motionEvent, gestureStateCreateGestureState2.getRunningTask())) {
                            this.mUncheckedConsumer = new AssistantInputConsumer(this, this.mGestureState, InputConsumer.NO_OP, getInputMonitorCompat(zIsMultiDisplayId), this.mDeviceState, motionEvent);
                        } else if (this.mDeviceState.canTriggerOneHandedAction(motionEvent)) {
                            this.mUncheckedConsumer = new OneHandedModeInputConsumer(this, this.mDeviceState, InputConsumer.NO_OP, this.mInputMonitorCompat);
                        } else {
                            this.mUncheckedConsumer = InputConsumer.NO_OP;
                        }
                    } else if (this.mDeviceState.canTriggerOneHandedAction(motionEvent) && !this.mDeviceState.isOneHandedModeActive()) {
                        this.mUncheckedConsumer = new OneHandedModeInputConsumer(this, this.mDeviceState, InputConsumer.NO_OP, this.mInputMonitorCompat);
                    } else {
                        this.mUncheckedConsumer = InputConsumer.NO_OP;
                    }
                }
            } else if (this.mUncheckedConsumer != InputConsumer.NO_OP) {
                this.mDeviceState.setOrientationTransformIfNeeded(motionEvent, displayId);
            }
            if (this.mUncheckedConsumer != InputConsumer.NO_OP) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0 || actionMasked == 1) {
                    ActiveGestureLog.INSTANCE.addLog("onMotionEvent(" + ((int) motionEvent.getRawX()) + ", " + ((int) motionEvent.getRawY()) + ")", motionEvent.getActionMasked());
                } else {
                    ActiveGestureLog.INSTANCE.addLog("onMotionEvent", motionEvent.getActionMasked());
                }
            }
            boolean z = this.mGestureState.getActivityInterface() != null && this.mGestureState.getActivityInterface().shouldCancelCurrentGesture();
            boolean z2 = (!(action == 1 || action == 3 || z) || (inputConsumer = this.mConsumer) == null || inputConsumer.getActiveConsumerInHierarchy().isConsumerDetachedFromGesture()) ? false : true;
            if (this.mGestureState.getDisplayId() == displayId) {
                if (z) {
                    motionEvent.setAction(3);
                }
                this.mUncheckedConsumer.onMotionEvent(motionEvent);
            }
            if (z2) {
                reset();
            }
            TraceHelper.INSTANCE.endFlagsOverride(objBeginFlagsOverride);
        }
    }

    public GestureState createGestureState(GestureState previousGestureState, int displayId) {
        GestureState gestureState = new GestureState(this.mOverviewComponentObserver, ActiveGestureLog.INSTANCE.generateAndSetLogId(), displayId);
        if (this.mTaskAnimationManager.isRecentsAnimationRunning()) {
            gestureState.updateRunningTask(previousGestureState.getRunningTask());
            gestureState.updateLastStartedTaskId(previousGestureState.getLastStartedTaskId());
            gestureState.updatePreviouslyAppearedTaskIds(previousGestureState.getPreviouslyAppearedTaskIds());
        } else {
            gestureState.updateRunningTask((ActivityManager.RunningTaskInfo) TraceHelper.whitelistIpcs("getRunningTask.0", new Supplier() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$NkakvC9GI8i0WzvqMQYFL06Ol6U
                @Override // java.util.function.Supplier
                public final Object get() {
                    return this.f$0.lambda$createGestureState$1$TouchInteractionService();
                }
            }));
        }
        return gestureState;
    }

    public /* synthetic */ ActivityManager.RunningTaskInfo lambda$createGestureState$1$TouchInteractionService() {
        return this.mAM.getRunningTask(false);
    }

    private InputConsumer newConsumer(GestureState previousGestureState, GestureState newGestureState, MotionEvent event) {
        InputConsumer inputConsumerNewBaseConsumer;
        InputConsumer oneHandedModeInputConsumer;
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_SWIPE_TO_HOME, "newConsumer");
        }
        AnimatedFloat animatedFloatApply = this.mSwipeUpProxyProvider.apply(this.mGestureState);
        if (animatedFloatApply != null) {
            return new ProgressDelegateInputConsumer(this, this.mTaskAnimationManager, this.mGestureState, this.mInputMonitorCompat, animatedFloatApply);
        }
        int displayId = newGestureState.getDisplayId();
        boolean zIsMultiDisplayId = isMultiDisplayId(displayId);
        boolean zCanStartSystemGesture = this.mDeviceState.canStartSystemGesture(displayId);
        if (!this.mDeviceState.isUserUnlocked()) {
            if (zCanStartSystemGesture) {
                return createDeviceLockedInputConsumer(newGestureState);
            }
            return getDefaultInputConsumer();
        }
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_SWIPE_TO_HOME, "newConsumer:user is unlocked");
        }
        if (zCanStartSystemGesture || previousGestureState.isRecentsAnimationRunning()) {
            inputConsumerNewBaseConsumer = newBaseConsumer(previousGestureState, newGestureState, event);
        } else {
            inputConsumerNewBaseConsumer = getDefaultInputConsumer();
        }
        InputConsumer defaultInputConsumer = inputConsumerNewBaseConsumer;
        if (this.mDeviceState.isGesturalNavMode()) {
            handleOrientationSetup(defaultInputConsumer);
        }
        if (this.mDeviceState.isFullyGesturalNavMode()) {
            if (this.mDeviceState.canTriggerAssistantAction(event, newGestureState.getRunningTask())) {
                defaultInputConsumer = new AssistantInputConsumer(this, newGestureState, defaultInputConsumer, getInputMonitorCompat(zIsMultiDisplayId), this.mDeviceState, event);
            }
            if ((this.mDeviceState.isBubblesExpanded() && !this.mDeviceState.isNotificationPanelExpanded()) || this.mDeviceState.isSystemUiDialogShowing()) {
                defaultInputConsumer = new SysUiOverlayInputConsumer(getBaseContext(), this.mDeviceState, getInputMonitorCompat(zIsMultiDisplayId));
            }
            if (this.mDeviceState.isScreenPinningActive()) {
                defaultInputConsumer = new ScreenPinnedInputConsumer(this, newGestureState);
            }
            if (this.mDeviceState.canTriggerOneHandedAction(event)) {
                defaultInputConsumer = new OneHandedModeInputConsumer(this, this.mDeviceState, defaultInputConsumer, this.mInputMonitorCompat);
            }
            if (!this.mDeviceState.isAccessibilityMenuAvailable()) {
                return defaultInputConsumer;
            }
            oneHandedModeInputConsumer = new AccessibilityInputConsumer(this, this.mDeviceState, defaultInputConsumer, getInputMonitorCompat(zIsMultiDisplayId));
        } else {
            if (this.mDeviceState.isScreenPinningActive()) {
                defaultInputConsumer = getDefaultInputConsumer();
            }
            if (!this.mDeviceState.canTriggerOneHandedAction(event)) {
                return defaultInputConsumer;
            }
            oneHandedModeInputConsumer = new OneHandedModeInputConsumer(this, this.mDeviceState, defaultInputConsumer, this.mInputMonitorCompat);
        }
        return oneHandedModeInputConsumer;
    }

    private void handleOrientationSetup(InputConsumer baseInputConsumer) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "handleOrientationSetup.1");
        }
        baseInputConsumer.notifyOrientationSetup();
    }

    private InputConsumer newBaseConsumer(GestureState previousGestureState, GestureState gestureState, MotionEvent event) {
        int displayId = gestureState.getDisplayId();
        if (this.mDeviceState.isKeyguardShowingOccluded(displayId)) {
            return createDeviceLockedInputConsumer(gestureState);
        }
        boolean z = false;
        boolean z2 = gestureState.getActivityInterface().isStarted() && gestureState.getRunningTask() != null && "android.intent.action.CHOOSER".equals(gestureState.getRunningTask().baseIntent.getAction());
        if (AssistantUtilities.isExcludedAssistant(gestureState.getRunningTask())) {
            gestureState.updateRunningTask((ActivityManager.RunningTaskInfo) TraceHelper.whitelistIpcs("getRunningTask.assistant", new Supplier() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$W7r_sdA9BM4o-0iTIN4UcTziGJI
                @Override // java.util.function.Supplier
                public final Object get() {
                    return this.f$0.lambda$newBaseConsumer$2$TouchInteractionService();
                }
            }));
            ComponentName component = this.mOverviewComponentObserver.getHomeIntent(displayId).getComponent();
            ComponentName component2 = gestureState.getRunningTask().baseIntent.getComponent();
            z2 = component2 != null && component2.equals(component);
        }
        int rotation = WindowUtils.getRotation(this, displayId);
        if (LGHomeFeature.Config.FEATURE_USE_SIMPLE_TRANSITION_OF_LANDSCAPE.getValue() && (rotation == 1 || rotation == 3)) {
            z = true;
        }
        if (gestureState.getRunningTask() == null) {
            return getDefaultInputConsumer();
        }
        if (WindowUtils.isWideMode(getApplicationContext()) || z) {
            return new OtherActivityInLandscapeInputConsumer(this, gestureState, gestureState.getDisplayId(), this.mOverviewComponentObserver, this.mOverviewCommandHelper);
        }
        if (previousGestureState.isRunningAnimationToLauncher() || gestureState.getActivityInterface().isResumed() || z2) {
            return createOverviewInputConsumer(previousGestureState, gestureState, event, z2);
        }
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && gestureState.getActivityInterface().isInLiveTileMode()) {
            return createOverviewInputConsumer(previousGestureState, gestureState, event, z2);
        }
        if (this.mDeviceState.isGestureBlockedActivity(gestureState.getRunningTask())) {
            return getDefaultInputConsumer();
        }
        return createOtherActivityInputConsumer(gestureState, event);
    }

    public /* synthetic */ ActivityManager.RunningTaskInfo lambda$newBaseConsumer$2$TouchInteractionService() {
        return this.mAM.getRunningTask(true);
    }

    private InputConsumer createOtherActivityInputConsumer(GestureState gestureState, MotionEvent event) {
        BaseSwipeUpHandler.Factory factory;
        boolean zIsMultiDisplayId = isMultiDisplayId(gestureState.getDisplayId());
        if (zIsMultiDisplayId || !this.mOverviewComponentObserver.isHomeAndOverviewSame()) {
            factory = this.mFallbackSwipeHandlerFactory;
        } else {
            factory = this.mLauncherSwipeHandlerFactory;
        }
        return new OtherActivityInputConsumer(this, this.mDeviceState, this.mTaskAnimationManager, gestureState, !this.mOverviewComponentObserver.isHomeAndOverviewSame() || gestureState.getActivityInterface().deferStartingActivity(this.mDeviceState, event) || zIsMultiDisplayId || WindowUtils.isWideMode(getApplicationContext()), new Consumer() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$qEYihW-6vlnBKqYCx47rCxOt1AA
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.onConsumerInactive((OtherActivityInputConsumer) obj);
            }
        }, getInputMonitorCompat(zIsMultiDisplayId), this.mDeviceState.isInExclusionRegion(event), factory);
    }

    private InputConsumer createDeviceLockedInputConsumer(GestureState gestureState) {
        boolean zIsMultiDisplayId = isMultiDisplayId(gestureState.getDisplayId());
        if (this.mDeviceState.isFullyGesturalNavMode() && gestureState.getRunningTask() != null) {
            return new DeviceLockedInputConsumer(this, this.mDeviceState, this.mTaskAnimationManager, gestureState, getInputMonitorCompat(zIsMultiDisplayId));
        }
        return getDefaultInputConsumer();
    }

    public InputConsumer createOverviewInputConsumer(GestureState previousGestureState, GestureState gestureState, MotionEvent event, boolean forceOverviewInputConsumer) {
        StatefulActivity createdActivity = gestureState.getActivityInterface().getCreatedActivity();
        if (createdActivity == null) {
            return getDefaultInputConsumer();
        }
        return new OverviewInputConsumer(gestureState, createdActivity, getInputMonitorCompat(isMultiDisplayId(gestureState.getDisplayId())), false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConsumerInactive(InputConsumer caller) {
        InputConsumer inputConsumer = this.mConsumer;
        if (inputConsumer == null || inputConsumer.getActiveConsumerInHierarchy() != caller) {
            return;
        }
        reset();
    }

    private InputConsumer getDefaultInputConsumer() {
        InputConsumer inputConsumer = this.mResetGestureInputConsumer;
        return inputConsumer != null ? inputConsumer : InputConsumer.NO_OP;
    }

    private void reset() {
        InputConsumer defaultInputConsumer = getDefaultInputConsumer();
        this.mUncheckedConsumer = defaultInputConsumer;
        this.mConsumer = defaultInputConsumer;
        this.mGestureState = GestureState.DEFAULT_STATE;
    }

    private void preloadOverview(boolean fromInit) {
        if (this.mDeviceState.isUserUnlocked()) {
            if ((!this.mDeviceState.isButtonNavMode() || this.mOverviewComponentObserver.isHomeAndOverviewSame()) && this.mDeviceState.isUserSetupComplete()) {
                BaseActivityInterface activityInterface = this.mOverviewComponentObserver.getActivityInterface();
                Intent intent = new Intent(this.mOverviewComponentObserver.getOverviewIntentIgnoreSysUiState());
                if (activityInterface.getCreatedActivity() == null || !fromInit) {
                    this.mTaskAnimationManager.preloadRecentsAnimation(intent);
                }
            }
        }
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        StatefulActivity createdActivity;
        if (!this.mDeviceState.isUserUnlocked() || (createdActivity = this.mOverviewComponentObserver.getActivityInterface().getCreatedActivity()) == null || createdActivity.isStarted()) {
            return;
        }
        if (this.mOverviewComponentObserver.canHandleConfigChanges(createdActivity.getComponentName(), createdActivity.getResources().getConfiguration().diff(newConfig))) {
            this.mDeviceState.onOneHandedModeChanged(ResourceUtils.getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, getApplicationContext().getResources()));
        } else {
            preloadOverview(false);
        }
    }

    @Override // android.app.Service
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] rawArgs) {
        if (rawArgs.length > 0 && Utilities.IS_DEBUG_DEVICE) {
            ArgList argList = new ArgList(Arrays.asList(rawArgs));
            String strNextArg = argList.nextArg();
            strNextArg.hashCode();
            if (strNextArg.equals("cmd")) {
                if (argList.peekArg() == null) {
                    printAvailableCommands(pw);
                    return;
                } else {
                    onCommand(pw, argList);
                    return;
                }
            }
            return;
        }
        FeatureFlags.dump(pw);
        if (this.mDeviceState.isUserUnlocked()) {
            PluginManagerWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(getBaseContext()).dump(pw);
        }
        this.mDeviceState.dump(pw);
        OverviewComponentObserver overviewComponentObserver = this.mOverviewComponentObserver;
        if (overviewComponentObserver != null) {
            overviewComponentObserver.dump(pw);
        }
        GestureState gestureState = this.mGestureState;
        if (gestureState != null) {
            gestureState.dump(pw);
        }
        SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).dump(pw);
        pw.println("TouchState:");
        OverviewComponentObserver overviewComponentObserver2 = this.mOverviewComponentObserver;
        StatefulActivity createdActivity = overviewComponentObserver2 == null ? null : overviewComponentObserver2.getActivityInterface().getCreatedActivity();
        OverviewComponentObserver overviewComponentObserver3 = this.mOverviewComponentObserver;
        boolean z = overviewComponentObserver3 != null && overviewComponentObserver3.getActivityInterface().isResumed();
        pw.println("  createdOverviewActivity=" + createdActivity);
        pw.println("  resumed=" + z);
        pw.println("  mConsumer=" + this.mConsumer.getName());
        ActiveGestureLog.INSTANCE.dump("", pw);
        pw.println("ProtoTrace:");
        pw.println("  file=" + ProtoTracer.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).getTraceFile());
    }

    private void printAvailableCommands(PrintWriter pw) {
        pw.println("Available commands:");
        pw.println("  clear-touch-log: Clears the touch interaction log");
    }

    private void onCommand(PrintWriter pw, ArgList args) {
        String strNextArg = args.nextArg();
        strNextArg.hashCode();
        if (strNextArg.equals("clear-touch-log")) {
            ActiveGestureLog.INSTANCE.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public BaseSwipeUpHandler createLauncherSwipeHandler(GestureState gestureState, long touchTimeMs, boolean continuingLastGesture) {
        return new LauncherSwipeHandlerV2(this, this.mDeviceState, this.mTaskAnimationManager, gestureState, touchTimeMs, continuingLastGesture, this.mInputConsumer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public BaseSwipeUpHandler createFallbackSwipeHandler(GestureState gestureState, long touchTimeMs, boolean continuingLastGesture) {
        return new FallbackSwipeHandler(this, this.mDeviceState, this.mTaskAnimationManager, gestureState, touchTimeMs, continuingLastGesture, this.mInputConsumer);
    }

    protected boolean shouldNotifyBackGesture() {
        return this.mBackGestureNotificationCounter > 0 && !this.mDeviceState.getGestureBlockedActivityPackages().isEmpty();
    }

    protected void tryNotifyBackGesture() {
        if (shouldNotifyBackGesture()) {
            this.mBackGestureNotificationCounter--;
            Utilities.getDevicePrefs(this).edit().putInt(KEY_BACK_NOTIFICATION_COUNT, this.mBackGestureNotificationCounter).apply();
            this.mDeviceState.getGestureBlockedActivityPackages().forEach(new Consumer() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$1CGyrsTgInK_201P2Mvgf75N9Ro
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$tryNotifyBackGesture$3$TouchInteractionService((String) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$tryNotifyBackGesture$3$TouchInteractionService(String str) {
        sendBroadcast(new Intent(NOTIFY_ACTION_BACK).setPackage(str));
    }

    /* JADX DEBUG: Method merged with bridge method: writeToProto(Ljava/lang/Object;)V */
    @Override // com.android.systemui.shared.tracing.ProtoTraceable
    public void writeToProto(LauncherTraceProto proto) {
        if (proto.touchInteractionService == null) {
            proto.touchInteractionService = new TouchInteractionServiceProto();
        }
        proto.touchInteractionService.serviceConnected = true;
        proto.touchInteractionService.serviceConnected = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canUseMultiDisplay() {
        DisplayManagerHelper displayManagerHelper;
        if (this.mDisplayManagerHelper == null) {
            this.mDisplayManagerHelper = new DisplayManagerHelper(this);
        }
        if (!DisplayManagerHelper.isMultiDisplayDevice() || (displayManagerHelper = this.mDisplayManagerHelper) == null) {
            return false;
        }
        return displayManagerHelper.getCoverDisplayState() == 3 || this.mDisplayManagerHelper.getMultiDisplayId() == 4;
    }

    public int getMultiDisplayId() {
        if (!DisplayManagerHelper.isMultiDisplayDevice()) {
            return 1;
        }
        if (this.mDisplayManagerHelper == null) {
            this.mDisplayManagerHelper = new DisplayManagerHelper(this);
        }
        return this.mDisplayManagerHelper.getMultiDisplayId();
    }

    private void registerInputConsumerControllerEx(int displayId) {
        if (!canUseMultiDisplay()) {
            LGLog.d("[MULIT_DISPLAY]TouchInteractionService", "registerInputConsumerControllerEx: skip 1");
            return;
        }
        if (this.mInputConsumerForMultiDisplay == null) {
            this.mInputConsumerForMultiDisplay = InputConsumerControllerEx.getRecentsAnimationInputConsumer();
        }
        LGLog.d("[MULIT_DISPLAY]TouchInteractionService", "registerInputConsumerControllerEx: " + displayId + ", " + this.mInputConsumerForMultiDisplay);
        this.mInputConsumerForMultiDisplay.registerInputConsumer(displayId);
    }

    private void unregisterInputConsumerControllerEx(int displayId) {
        InputConsumerControllerEx inputConsumerControllerEx = this.mInputConsumerForMultiDisplay;
        if (inputConsumerControllerEx != null) {
            LGLog.d("[MULIT_DISPLAY]TouchInteractionService", "unregisterInputConsumerControllerEx: " + displayId + ", " + inputConsumerControllerEx);
            this.mInputConsumerForMultiDisplay.unregisterInputConsumer(displayId);
            this.mInputConsumerForMultiDisplay = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initInputMonitorForMulti() {
        disposeEventHandlersForMulti();
        if (!canUseMultiDisplay()) {
            LGLog.d("[MULIT_DISPLAY]TouchInteractionService", "initInputMonitorForMulti: skip 1");
            return;
        }
        if (this.mDeviceState.isButtonNavMode() || !SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).isActive()) {
            LGLog.d("[MULIT_DISPLAY]TouchInteractionService", "initInputMonitorForMulti: skip 2");
            return;
        }
        registerInputConsumerControllerEx(getMultiDisplayId());
        InputMonitorCompat inputMonitorCompat = new InputMonitorCompat("swipe-up", getMultiDisplayId());
        this.mInputMonitorCompatForMulti = inputMonitorCompat;
        this.mInputEventReceiverForMulti = inputMonitorCompat.getInputReceiver(Looper.getMainLooper(), this.mMainChoreographer, new $$Lambda$TouchInteractionService$QnySfMPM3HQvC_OREg1W70p37mY(this));
        this.mDeviceState.updateGestureTouchRegions(getMultiDisplayId());
        LGLog.d("[MULIT_DISPLAY]TouchInteractionService", "initInputMonitorForMulti: success. ");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disposeEventHandlersForMulti() {
        if (!DisplayManagerHelper.isMultiDisplayDevice()) {
            LGLog.d("[MULIT_DISPLAY]TouchInteractionService", "disposeEventHandlersForMulti: skip");
            return;
        }
        LGLog.d("[MULIT_DISPLAY]TouchInteractionService", "disposeEventHandlersForMulti:");
        InputChannelCompat.InputEventReceiver inputEventReceiver = this.mInputEventReceiverForMulti;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mInputEventReceiverForMulti = null;
        }
        InputMonitorCompat inputMonitorCompat = this.mInputMonitorCompatForMulti;
        if (inputMonitorCompat != null) {
            inputMonitorCompat.dispose();
            this.mInputMonitorCompatForMulti = null;
        }
    }

    private boolean isMultiDisplayId(int displayId) {
        return displayId == getMultiDisplayId();
    }

    public InputMonitorCompat getInputMonitorCompat(boolean isMultiDisplay) {
        if (isMultiDisplay) {
            return this.mInputMonitorCompatForMulti;
        }
        return this.mInputMonitorCompat;
    }

    public InputChannelCompat.InputEventReceiver getInputEventReceiver(boolean isMultiDisplay) {
        if (isMultiDisplay) {
            return this.mInputEventReceiverForMulti;
        }
        return this.mInputEventReceiver;
    }

    /* JADX INFO: renamed from: com.android.quickstep.TouchInteractionService$2, reason: invalid class name */
    class AnonymousClass2 extends ICoverDisplayEnabledCallback.Stub {
        AnonymousClass2() {
        }

        public void onCoverDisplayEnabledChangedCallback(final int state) {
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$2$s3WXiCWmzU00WPJyUty_DSc8wmM
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onCoverDisplayEnabledChangedCallback$0$TouchInteractionService$2(state);
                }
            });
        }

        public /* synthetic */ void lambda$onCoverDisplayEnabledChangedCallback$0$TouchInteractionService$2(int i) {
            LGLog.i(TouchInteractionService.TAG, "onCoverDisplayEnabledChangedCallback :  state = " + i + ", canUseMultiDisplay = " + TouchInteractionService.this.canUseMultiDisplay() + ", mInputMonitorCompatForMultiDisplay = " + TouchInteractionService.this.mInputMonitorCompatForMulti);
            if (i == 3) {
                if (TouchInteractionService.this.mInputMonitorCompatForMulti == null) {
                    TouchInteractionService.this.initInputMonitorForMulti();
                    return;
                }
                return;
            }
            TouchInteractionService.this.disposeEventHandlersForMulti();
        }
    }

    /* JADX INFO: renamed from: com.android.quickstep.TouchInteractionService$3, reason: invalid class name */
    class AnonymousClass3 extends IPostureStateCallback.Stub {
        AnonymousClass3() {
        }

        public void onSwivelStateChanged(final int state) throws RemoteException {
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TouchInteractionService$3$jY8d01Now5Gd5LqusYRx_rN3oA4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onSwivelStateChanged$0$TouchInteractionService$3(state);
                }
            });
        }

        public /* synthetic */ void lambda$onSwivelStateChanged$0$TouchInteractionService$3(int i) {
            LGLog.i(TouchInteractionService.TAG, "onSwivelStateChanged :  state = " + i + ", canUseSubSwivelDisplay = " + TouchInteractionService.this.canUseMultiDisplay() + ", mInputMonitorCompatForMultiDisplay = " + TouchInteractionService.this.mInputMonitorCompatForMulti);
            if (i == 101 || i == 100) {
                if (TouchInteractionService.this.mInputMonitorCompatForMulti == null) {
                    TouchInteractionService.this.initInputMonitorForMulti();
                    return;
                }
                return;
            }
            TouchInteractionService.this.disposeEventHandlersForMulti();
        }
    }

    public void registerCoverDisplayEnabledCallback(ICoverDisplayEnabledCallback callBack) {
        IDisplayManagerEx iDisplayManagerExAsInterface = IDisplayManagerEx.Stub.asInterface(ServiceManager.getService("display"));
        try {
            LGLog.i(TAG, "registerCoverDisplayEnabledCallback");
            iDisplayManagerExAsInterface.registerCoverDisplayEnabledCallback(callBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregisterCoverDisplayEnabledCallback(ICoverDisplayEnabledCallback callBack) {
        IDisplayManagerEx iDisplayManagerExAsInterface = IDisplayManagerEx.Stub.asInterface(ServiceManager.getService("display"));
        try {
            LGLog.i(TAG, "unregisterCoverDisplayEnabledCallback");
            iDisplayManagerExAsInterface.unregisterCoverDisplayEnabledCallback(callBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void registerSwivelDisplayEnabledCallback(IPostureStateCallback callBack) {
        if (this.mPostureManager != null) {
            LGLog.i(TAG, "registerSwivelDisplayEnabledCallback");
            this.mPostureManager.registerPostureStateCallback(callBack);
        }
    }

    public void unregisterSwivelDisplayEnabledCallback(IPostureStateCallback callBack) {
        if (this.mPostureManager != null) {
            LGLog.i(TAG, "unregisterSwivelDisplayEnabledCallback");
            this.mPostureManager.unregisterPostureStateCallback(callBack);
        }
    }

    private boolean needToSkipStylusEvent(int displayId, MotionEvent ev) {
        StylusBlockSettingObserver stylusBlockSettingObserver = this.mStylusBlockSettingObserver;
        if (stylusBlockSettingObserver == null || !stylusBlockSettingObserver.isPenGestureBlock() || ev.getToolType(0) != 2) {
            return false;
        }
        LGLog.d(TAG, "needToSkipStylusEvent : displayId = " + displayId + ", TOOL_TYPE_STYLUS event = " + ev);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchSplitSelectState() {
        BaseActivityInterface activityInterface;
        if (SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).isSplitScreenVisible() || (activityInterface = this.mOverviewComponentObserver.getActivityInterface()) == null || activityInterface.getOverviewStateOrSplitSelectedEnable()) {
            return;
        }
        ActivityManager.RunningTaskInfo runningTask = this.mAM.getRunningTask(false);
        if (ActivityManagerWrapperEx.getInstance().isHomeTask(runningTask, 0, this.mOverviewComponentObserver.getHomeIntent(0).getComponent().getClassName()) || ActivityManagerWrapperEx.getInstance().hasFlagActivityExcludedFromRecents(runningTask)) {
            return;
        }
        List<RecentTaskInfoEx> recentTasksEx = ActivityManagerWrapperEx.getInstance().getRecentTasksEx(Integer.MAX_VALUE, 0, Process.myUserHandle().getIdentifier());
        if (recentTasksEx.size() >= 2 && recentTasksEx.get(0).supportsSplitScreenMultiWindow) {
            this.mOverviewCommandHelper.onOverviewToggle(0);
            activityInterface.setNeedLaunchSplitSelectState(true);
        }
    }
}
