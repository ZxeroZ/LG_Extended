package com.android.quickstep;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Region;
import android.net.Uri;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import com.android.launcher3.Utilities;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.SettingsCache;
import com.android.quickstep.GestureState;
import com.android.quickstep.OrientationTouchTransformer;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.util.NavBarPosition;
import com.android.quickstep.util.RecentsOrientedState;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.SystemGestureExclusionListenerCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import com.lge.display.DisplayManagerHelper;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes.dex */
public class RecentsAnimationDeviceState implements SysUINavigationMode.NavigationModeChangeListener, DisplayController.DisplayInfoChangeListener, SysUINavigationMode.OneHandedModeChangeListener {
    static final String SUPPORT_ONE_HANDED_MODE = "ro.support_one_handed_mode";
    private static final String TAG = "RecentsAnimationDeviceState";
    private boolean mAssistantAvailable;
    private boolean mAssistantAvailableForMulti;
    private float mAssistantVisibility;
    private float mAssistantVisibilityForMulti;
    private final Context mContext;
    private int mCurrentAppRotation;
    private int mCurrentAppRotationForMulti;
    private final DisplayController mDisplayController;
    private final int mDisplayId;
    private DisplayManagerHelper mDisplayManagerHelper;
    private int mDisplayRotation;
    private int mDisplayRotationForMulti;
    private SystemGestureExclusionListenerCompat mExclusionListener;
    private SystemGestureExclusionListenerCompat mExclusionListenerForMultiDisplay;
    private Region mExclusionRegion;
    private Region mExclusionRegionForMultiDisplay;
    private Runnable mExitOverviewRunnable;
    private TaskStackChangeListener mFrozenTaskListener;
    private final List<ComponentName> mGestureBlockedActivities;
    private boolean mInOverview;
    private boolean mIsOneHandedModeEnabled;
    private final boolean mIsOneHandedModeSupported;
    private boolean mIsSwipeToNotificationEnabled;
    private boolean mIsUserSetupComplete;
    private boolean mIsUserUnlocked;
    private int mMultiDisplayId;
    private NavBarPosition mNavBarPosition;
    private NavBarPosition mNavBarPositionForMulti;
    private Runnable mOnDestroyFrozenTaskRunnable;
    private OrientationEventListener mOrientationListener;
    private OrientationTouchTransformer mOrientationTouchTransformer;
    private OrientationTouchTransformer mOrientationTouchTransformerForMulti;
    private boolean mPipIsActive;
    private final TaskStackChangeListener mPipListener;
    private boolean mPrioritizeDeviceRotation;
    private int mSensorRotation;
    private final SysUINavigationMode mSysUiNavMode;
    private int mSystemUiStateFlags;
    private int mSystemUiStateFlagsForMulti;
    private boolean mTaskListFrozen;
    private final BroadcastReceiver mUserUnlockedReceiver;
    private final ArrayList<Runnable> mOnDestroyActions = new ArrayList<>();
    private SysUINavigationMode.Mode mMode = SysUINavigationMode.Mode.THREE_BUTTONS;
    private final Region mDeferredGestureRegion = new Region();
    private final ArrayList<Runnable> mUserUnlockedActions = new ArrayList<>();

    public RecentsAnimationDeviceState(Context context) {
        String[] stringArray;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.quickstep.RecentsAnimationDeviceState.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
                    RecentsAnimationDeviceState.this.mIsUserUnlocked = true;
                    RecentsAnimationDeviceState.this.notifyUserUnlocked();
                }
            }
        };
        this.mUserUnlockedReceiver = broadcastReceiver;
        this.mFrozenTaskListener = new TaskStackChangeListener() { // from class: com.android.quickstep.RecentsAnimationDeviceState.2
            @Override // com.android.systemui.shared.system.TaskStackChangeListener
            public void onRecentTaskListFrozenChanged(boolean frozen) {
                RecentsAnimationDeviceState.this.mTaskListFrozen = frozen;
                if (frozen || RecentsAnimationDeviceState.this.mInOverview) {
                    return;
                }
                RecentsAnimationDeviceState.this.enableMultipleRegions(false, 0);
                RecentsAnimationDeviceState recentsAnimationDeviceState = RecentsAnimationDeviceState.this;
                recentsAnimationDeviceState.enableMultipleRegions(false, recentsAnimationDeviceState.mMultiDisplayId);
            }

            @Override // com.android.systemui.shared.system.TaskStackChangeListener
            public void onActivityRotation(int displayId) {
                if (displayId != RecentsAnimationDeviceState.this.mDisplayId) {
                    return;
                }
                RecentsAnimationDeviceState.this.mPrioritizeDeviceRotation = true;
                if (RecentsAnimationDeviceState.this.mInOverview) {
                    RecentsAnimationDeviceState.this.mExitOverviewRunnable.run();
                }
            }
        };
        this.mExitOverviewRunnable = new Runnable() { // from class: com.android.quickstep.RecentsAnimationDeviceState.3
            @Override // java.lang.Runnable
            public void run() {
                RecentsAnimationDeviceState.this.mInOverview = false;
                RecentsAnimationDeviceState.this.enableMultipleRegions(false, 0);
                RecentsAnimationDeviceState recentsAnimationDeviceState = RecentsAnimationDeviceState.this;
                recentsAnimationDeviceState.enableMultipleRegions(false, recentsAnimationDeviceState.mMultiDisplayId);
            }
        };
        this.mSensorRotation = 0;
        this.mCurrentAppRotation = -1;
        this.mPrioritizeDeviceRotation = false;
        this.mCurrentAppRotationForMulti = -1;
        this.mContext = context;
        SysUINavigationMode sysUINavigationModeLambda$get$0$MainThreadInitializedObject = SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(context);
        this.mSysUiNavMode = sysUINavigationModeLambda$get$0$MainThreadInitializedObject;
        DisplayController displayControllerLambda$get$0$MainThreadInitializedObject = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(context);
        this.mDisplayController = displayControllerLambda$get$0$MainThreadInitializedObject;
        int i = displayControllerLambda$get$0$MainThreadInitializedObject.getInfo().id;
        this.mDisplayId = i;
        this.mIsOneHandedModeSupported = SystemProperties.getBoolean(SUPPORT_ONE_HANDED_MODE, false);
        DisplayManagerHelper displayManagerHelper = new DisplayManagerHelper(context);
        this.mDisplayManagerHelper = displayManagerHelper;
        this.mMultiDisplayId = displayManagerHelper.getMultiDisplayId();
        runOnDestroy(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$YiPXaANJFIBdNxKtyUoUSqZ7luU
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0$RecentsAnimationDeviceState();
            }
        });
        boolean zIsUserUnlocked = ((UserManager) context.getSystemService(UserManager.class)).isUserUnlocked(Process.myUserHandle());
        this.mIsUserUnlocked = zIsUserUnlocked;
        if (!zIsUserUnlocked) {
            context.registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        }
        runOnDestroy(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$_1IDg3wTFALsdQ4jcAJpFZEANqk
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$1$RecentsAnimationDeviceState();
            }
        });
        final SystemGestureExclusionListenerCompat systemGestureExclusionListenerCompat = new SystemGestureExclusionListenerCompat(i) { // from class: com.android.quickstep.RecentsAnimationDeviceState.4
            @Override // com.android.systemui.shared.system.SystemGestureExclusionListenerCompat
            public void onExclusionChanged(Region region) {
                RecentsAnimationDeviceState.this.mExclusionRegion = region;
            }
        };
        this.mExclusionListener = systemGestureExclusionListenerCompat;
        Objects.requireNonNull(systemGestureExclusionListenerCompat);
        runOnDestroy(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$bb3M7kNOHB8OakCxw6EsulTa6-o
            @Override // java.lang.Runnable
            public final void run() {
                systemGestureExclusionListenerCompat.unregister();
            }
        });
        if (DisplayManagerHelper.isMultiDisplayDevice()) {
            this.mExclusionListenerForMultiDisplay = new SystemGestureExclusionListenerCompat(this.mMultiDisplayId) { // from class: com.android.quickstep.RecentsAnimationDeviceState.5
                @Override // com.android.systemui.shared.system.SystemGestureExclusionListenerCompat
                public void onExclusionChanged(Region region) {
                    RecentsAnimationDeviceState.this.mExclusionRegionForMultiDisplay = region;
                }
            };
        }
        Resources resources = context.getResources();
        this.mOrientationTouchTransformer = new OrientationTouchTransformer(resources, this.mMode, new OrientationTouchTransformer.QuickStepContractInfo() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$KZvmFBCccA4ftzycRQl_Ha9RC_E
            @Override // com.android.quickstep.OrientationTouchTransformer.QuickStepContractInfo
            public final float getWindowCornerRadius() {
                return this.f$0.lambda$new$2$RecentsAnimationDeviceState();
            }
        }, 0);
        this.mOrientationTouchTransformerForMulti = new OrientationTouchTransformer(resources, this.mMode, new OrientationTouchTransformer.QuickStepContractInfo() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$QpxfATc79SZMIlVkBV-TzNh_EdI
            @Override // com.android.quickstep.OrientationTouchTransformer.QuickStepContractInfo
            public final float getWindowCornerRadius() {
                return this.f$0.lambda$new$3$RecentsAnimationDeviceState();
            }
        }, this.mMultiDisplayId);
        onNavigationModeChanged(sysUINavigationModeLambda$get$0$MainThreadInitializedObject.addModeChangeListener(this));
        runOnDestroy(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$lpcPSgVxMoatszxP8sLwY79TpwM
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$4$RecentsAnimationDeviceState();
            }
        });
        try {
            stringArray = context.getResources().getStringArray(R.array.gesture_blocking_activities);
        } catch (Resources.NotFoundException unused) {
            stringArray = new String[0];
        }
        this.mGestureBlockedActivities = new ArrayList(stringArray.length);
        for (String str : stringArray) {
            if (!TextUtils.isEmpty(str)) {
                this.mGestureBlockedActivities.add(ComponentName.unflattenFromString(str));
            }
        }
        final SettingsCache settingsCacheLambda$get$0$MainThreadInitializedObject = SettingsCache.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext);
        if (this.mIsOneHandedModeSupported) {
            final Uri uriFor = Settings.Secure.getUriFor(SettingsCache.ONE_HANDED_ENABLED);
            final SettingsCache.OnChangeListener onChangeListener = new SettingsCache.OnChangeListener() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$9s0TivM3OYrGd8-tF73G7j-hVI8
                @Override // com.android.launcher3.util.SettingsCache.OnChangeListener
                public final void onSettingsChanged(boolean z) {
                    this.f$0.lambda$new$5$RecentsAnimationDeviceState(z);
                }
            };
            settingsCacheLambda$get$0$MainThreadInitializedObject.register(uriFor, onChangeListener);
            this.mIsOneHandedModeEnabled = settingsCacheLambda$get$0$MainThreadInitializedObject.getValue(uriFor);
            runOnDestroy(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$9th3PIMr1dDdHFqYVviw3iVw1s0
                @Override // java.lang.Runnable
                public final void run() {
                    settingsCacheLambda$get$0$MainThreadInitializedObject.unregister(uriFor, onChangeListener);
                }
            });
        } else {
            this.mIsOneHandedModeEnabled = false;
        }
        final Uri uriFor2 = Settings.Secure.getUriFor(SettingsCache.ONE_HANDED_SWIPE_BOTTOM_TO_NOTIFICATION_ENABLED);
        final SettingsCache.OnChangeListener onChangeListener2 = new SettingsCache.OnChangeListener() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$nCXeHlamM8FVQBwbDT7hTfIZ10E
            @Override // com.android.launcher3.util.SettingsCache.OnChangeListener
            public final void onSettingsChanged(boolean z) {
                this.f$0.lambda$new$7$RecentsAnimationDeviceState(z);
            }
        };
        settingsCacheLambda$get$0$MainThreadInitializedObject.register(uriFor2, onChangeListener2);
        this.mIsSwipeToNotificationEnabled = settingsCacheLambda$get$0$MainThreadInitializedObject.getValue(uriFor2);
        runOnDestroy(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$GB6mr2cnjTxTIEr3Ooxa3RyAkGg
            @Override // java.lang.Runnable
            public final void run() {
                settingsCacheLambda$get$0$MainThreadInitializedObject.unregister(uriFor2, onChangeListener2);
            }
        });
        final Uri uriFor3 = Settings.Secure.getUriFor("user_setup_complete");
        boolean value = settingsCacheLambda$get$0$MainThreadInitializedObject.getValue(uriFor3, 0);
        this.mIsUserSetupComplete = value;
        if (!value) {
            final SettingsCache.OnChangeListener onChangeListener3 = new SettingsCache.OnChangeListener() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$aUGa2FOxaVrKMK4nfhGC5eKk2q4
                @Override // com.android.launcher3.util.SettingsCache.OnChangeListener
                public final void onSettingsChanged(boolean z) {
                    this.f$0.lambda$new$9$RecentsAnimationDeviceState(z);
                }
            };
            settingsCacheLambda$get$0$MainThreadInitializedObject.register(uriFor3, onChangeListener3);
            runOnDestroy(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$TDVDfbi6BR0t4yEgw2Qv0VElhtg
                @Override // java.lang.Runnable
                public final void run() {
                    settingsCacheLambda$get$0$MainThreadInitializedObject.unregister(uriFor3, onChangeListener3);
                }
            });
        }
        try {
            this.mPipIsActive = ActivityTaskManager.getService().getRootTaskInfo(2, 0) != null;
        } catch (RemoteException unused2) {
        }
        TaskStackChangeListener taskStackChangeListener = new TaskStackChangeListener() { // from class: com.android.quickstep.RecentsAnimationDeviceState.6
            @Override // com.android.systemui.shared.system.TaskStackChangeListener
            public void onActivityPinned(String packageName, int userId, int taskId, int stackId) {
                RecentsAnimationDeviceState.this.mPipIsActive = true;
            }

            @Override // com.android.systemui.shared.system.TaskStackChangeListener
            public void onActivityUnpinned() {
                RecentsAnimationDeviceState.this.mPipIsActive = false;
            }
        };
        this.mPipListener = taskStackChangeListener;
        TaskStackChangeListeners.getInstance().registerTaskStackListener(taskStackChangeListener);
        runOnDestroy(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$a-pooqx5Ump2wl2L1XWfd_Im4ok
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$11$RecentsAnimationDeviceState();
            }
        });
        this.mOrientationListener = new OrientationEventListener(context) { // from class: com.android.quickstep.RecentsAnimationDeviceState.7
            @Override // android.view.OrientationEventListener
            public void onOrientationChanged(int degrees) {
                int rotationForUserDegreesRotated = RecentsOrientedState.getRotationForUserDegreesRotated(degrees, RecentsAnimationDeviceState.this.mSensorRotation);
                LGLog.d(RecentsAnimationDeviceState.TAG, "onOrientationChanged : newRotation = " + rotationForUserDegreesRotated + ", mSensorRotation = " + RecentsAnimationDeviceState.this.mSensorRotation);
                if (rotationForUserDegreesRotated == RecentsAnimationDeviceState.this.mSensorRotation) {
                    return;
                }
                RecentsAnimationDeviceState.this.mSensorRotation = rotationForUserDegreesRotated;
                RecentsAnimationDeviceState.this.mPrioritizeDeviceRotation = true;
                if (rotationForUserDegreesRotated == RecentsAnimationDeviceState.this.mCurrentAppRotation) {
                    RecentsAnimationDeviceState.this.toggleSecondaryNavBarsForRotation(0);
                    RecentsAnimationDeviceState recentsAnimationDeviceState = RecentsAnimationDeviceState.this;
                    recentsAnimationDeviceState.toggleSecondaryNavBarsForRotation(recentsAnimationDeviceState.mMultiDisplayId);
                }
            }
        };
    }

    public /* synthetic */ void lambda$new$0$RecentsAnimationDeviceState() {
        this.mDisplayController.removeChangeListener(this);
    }

    public /* synthetic */ void lambda$new$1$RecentsAnimationDeviceState() {
        Utilities.unregisterReceiverSafely(this.mContext, this.mUserUnlockedReceiver);
    }

    public /* synthetic */ float lambda$new$2$RecentsAnimationDeviceState() {
        return QuickStepContract.getWindowCornerRadius(this.mContext);
    }

    public /* synthetic */ float lambda$new$3$RecentsAnimationDeviceState() {
        return QuickStepContract.getWindowCornerRadius(this.mContext);
    }

    public /* synthetic */ void lambda$new$4$RecentsAnimationDeviceState() {
        this.mSysUiNavMode.removeModeChangeListener(this);
    }

    public /* synthetic */ void lambda$new$5$RecentsAnimationDeviceState(boolean z) {
        this.mIsOneHandedModeEnabled = z;
    }

    public /* synthetic */ void lambda$new$7$RecentsAnimationDeviceState(boolean z) {
        this.mIsSwipeToNotificationEnabled = z;
    }

    public /* synthetic */ void lambda$new$9$RecentsAnimationDeviceState(boolean z) {
        this.mIsUserSetupComplete = z;
    }

    public /* synthetic */ void lambda$new$11$RecentsAnimationDeviceState() {
        TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mPipListener);
    }

    private void setupOrientationSwipeHandler() {
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mFrozenTaskListener);
        Runnable runnable = new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$nLlCXmqh7vHQoChC1Qm0l8uX2Q8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setupOrientationSwipeHandler$12$RecentsAnimationDeviceState();
            }
        };
        this.mOnDestroyFrozenTaskRunnable = runnable;
        runOnDestroy(runnable);
    }

    public /* synthetic */ void lambda$setupOrientationSwipeHandler$12$RecentsAnimationDeviceState() {
        ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mFrozenTaskListener);
    }

    private void destroyOrientationSwipeHandlerCallback() {
        ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mFrozenTaskListener);
        this.mOnDestroyActions.remove(this.mOnDestroyFrozenTaskRunnable);
    }

    private void runOnDestroy(Runnable action) {
        this.mOnDestroyActions.add(action);
    }

    public void destroy() {
        Iterator<Runnable> it = this.mOnDestroyActions.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
    }

    public void addNavigationModeChangedCallback(final SysUINavigationMode.NavigationModeChangeListener listener) {
        listener.onNavigationModeChanged(this.mSysUiNavMode.addModeChangeListener(listener));
        runOnDestroy(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$ktORXYSKUqx9PK_emmCk1XptVKY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$addNavigationModeChangedCallback$13$RecentsAnimationDeviceState(listener);
            }
        });
    }

    public /* synthetic */ void lambda$addNavigationModeChangedCallback$13$RecentsAnimationDeviceState(SysUINavigationMode.NavigationModeChangeListener navigationModeChangeListener) {
        this.mSysUiNavMode.removeModeChangeListener(navigationModeChangeListener);
    }

    public void addOneHandedModeChangedCallback(final SysUINavigationMode.OneHandedModeChangeListener listener) {
        listener.onOneHandedModeChanged(this.mSysUiNavMode.addOneHandedOverlayChangeListener(listener));
        runOnDestroy(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$LG0g2ow5hgJYjSx7ahRuU51kFyQ
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$addOneHandedModeChangedCallback$14$RecentsAnimationDeviceState(listener);
            }
        });
    }

    public /* synthetic */ void lambda$addOneHandedModeChangedCallback$14$RecentsAnimationDeviceState(SysUINavigationMode.OneHandedModeChangeListener oneHandedModeChangeListener) {
        this.mSysUiNavMode.removeOneHandedOverlayChangeListener(oneHandedModeChangeListener);
    }

    @Override // com.android.quickstep.SysUINavigationMode.NavigationModeChangeListener
    public void onNavigationModeChanged(SysUINavigationMode.Mode newMode) {
        this.mDisplayController.removeChangeListener(this);
        this.mDisplayController.addChangeListener(this);
        onDisplayInfoChanged(this.mContext, this.mDisplayController.getInfo(), 63);
        if (newMode == SysUINavigationMode.Mode.NO_BUTTON) {
            this.mExclusionListener.register();
        } else {
            this.mExclusionListener.unregister();
        }
        this.mNavBarPosition = new NavBarPosition(newMode, this.mDisplayController.getInfo());
        getOrientationTouchTransformer(0).setNavigationMode(newMode, this.mDisplayController.getInfo(), this.mContext.getResources());
        if (getOrientationTouchTransformer(this.mMultiDisplayId) != null) {
            getOrientationTouchTransformer(this.mMultiDisplayId).setNavigationMode(newMode, this.mDisplayController.getInfo(this.mMultiDisplayId), this.mContext.getResources());
        }
        if (!this.mMode.hasGestures && newMode.hasGestures) {
            setupOrientationSwipeHandler();
        } else if (this.mMode.hasGestures && !newMode.hasGestures) {
            destroyOrientationSwipeHandlerCallback();
        }
        this.mMode = newMode;
    }

    @Override // com.android.launcher3.util.DisplayController.DisplayInfoChangeListener
    public void onDisplayInfoChanged(Context ctx, DisplayController.Info info, int flags) {
        if (this.mMultiDisplayId != 0 && info.id == this.mMultiDisplayId) {
            this.mDisplayRotationForMulti = info.rotation;
            if (!this.mMode.hasGestures) {
                return;
            }
            this.mNavBarPositionForMulti = new NavBarPosition(this.mMode, info);
            updateGestureTouchRegions(info.id);
            getOrientationTouchTransformer(info.id).createOrAddTouchRegion(info);
            int i = this.mDisplayRotationForMulti;
            this.mCurrentAppRotationForMulti = i;
            if ((this.mPrioritizeDeviceRotation || i == this.mSensorRotation) && !this.mInOverview && this.mTaskListFrozen) {
                toggleSecondaryNavBarsForRotation(info.id);
            }
        }
        if (info.id != getDisplayId() || flags == 4) {
            return;
        }
        this.mDisplayRotation = info.rotation;
        if (this.mMode.hasGestures) {
            this.mNavBarPosition = new NavBarPosition(this.mMode, info);
            updateGestureTouchRegions();
            getOrientationTouchTransformer(0).createOrAddTouchRegion(info);
            int i2 = this.mDisplayRotation;
            this.mCurrentAppRotation = i2;
            if ((this.mPrioritizeDeviceRotation || i2 == this.mSensorRotation) && !this.mInOverview && this.mTaskListFrozen) {
                toggleSecondaryNavBarsForRotation(info.id);
            }
        }
    }

    @Override // com.android.quickstep.SysUINavigationMode.OneHandedModeChangeListener
    public void onOneHandedModeChanged(int newGesturalHeight) {
        this.mOrientationTouchTransformer.setGesturalHeight(newGesturalHeight, this.mDisplayController.getInfo(), this.mContext.getResources());
    }

    public SysUINavigationMode.Mode getNavMode() {
        return this.mMode;
    }

    public NavBarPosition getNavBarPosition() {
        return getNavBarPosition(0);
    }

    public boolean isFullyGesturalNavMode() {
        return this.mMode == SysUINavigationMode.Mode.NO_BUTTON;
    }

    public boolean isGesturalNavMode() {
        return this.mMode.hasGestures;
    }

    public boolean isTwoButtonNavMode() {
        return this.mMode == SysUINavigationMode.Mode.TWO_BUTTONS;
    }

    public boolean isButtonNavMode() {
        return this.mMode == SysUINavigationMode.Mode.THREE_BUTTONS;
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public void runOnUserUnlocked(Runnable action) {
        if (this.mIsUserUnlocked) {
            action.run();
        } else {
            this.mUserUnlockedActions.add(action);
        }
    }

    public boolean isUserUnlocked() {
        return this.mIsUserUnlocked;
    }

    public boolean isUserSetupComplete() {
        return this.mIsUserSetupComplete;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUserUnlocked() {
        Iterator<Runnable> it = this.mUserUnlockedActions.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mUserUnlockedActions.clear();
        Utilities.unregisterReceiverSafely(this.mContext, this.mUserUnlockedReceiver);
    }

    public boolean isGestureBlockedActivity(ActivityManager.RunningTaskInfo runningTaskInfo) {
        return runningTaskInfo != null && this.mGestureBlockedActivities.contains(runningTaskInfo.topActivity);
    }

    public List<String> getGestureBlockedActivityPackages() {
        return (List) this.mGestureBlockedActivities.stream().map(new Function() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$HIlrlc_k0A5lsH1JHDjcYuK2vqA
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ComponentName) obj).getPackageName();
            }
        }).collect(Collectors.toList());
    }

    public void setSystemUiFlags(int stateFlags) {
        setSystemUiFlags(stateFlags, 0);
    }

    public int getSystemUiStateFlags() {
        return getSystemUiStateFlags(0);
    }

    public boolean canStartSystemGesture() {
        return canStartSystemGesture(0);
    }

    public boolean isKeyguardShowingOccluded() {
        return isKeyguardShowingOccluded(0);
    }

    public boolean isScreenPinningActive() {
        return isScreenPinningActive(0);
    }

    public boolean isAssistantGestureIsConstrained() {
        return (this.mSystemUiStateFlags & 8192) != 0;
    }

    public boolean isNotificationPanelExpanded() {
        return (this.mSystemUiStateFlags & 4) != 0;
    }

    public boolean isBubblesExpanded() {
        return isBubblesExpanded(0);
    }

    public boolean isSystemUiDialogShowing() {
        return isSystemUiDialogShowing(0);
    }

    public boolean isLockToAppActive() {
        return ActivityManagerWrapper.getInstance().isLockToAppActive();
    }

    public boolean isAccessibilityMenuAvailable() {
        return isAccessibilityMenuAvailable(0);
    }

    public boolean isAccessibilityMenuShortcutAvailable() {
        return isAccessibilityMenuShortcutAvailable(0);
    }

    public boolean isHomeDisabled() {
        return isHomeDisabled(0);
    }

    public boolean isOverviewDisabled() {
        return isOverviewDisabled(0);
    }

    public boolean isOneHandedModeActive() {
        return (this.mSystemUiStateFlags & 65536) != 0;
    }

    public void updateGestureTouchRegions() {
        updateGestureTouchRegions(0);
    }

    public boolean isInSwipeUpTouchRegion(MotionEvent event) {
        return getOrientationTouchTransformer(event.getDisplayId()).touchInValidSwipeRegions(event.getX(), event.getY());
    }

    public boolean isInSwipeUpTouchRegion(MotionEvent event, int pointerIndex) {
        return getOrientationTouchTransformer(event.getDisplayId()).touchInValidSwipeRegions(event.getX(pointerIndex), event.getY(pointerIndex));
    }

    public void setDeferredGestureRegion(Region deferredGestureRegion) {
        this.mDeferredGestureRegion.set(deferredGestureRegion);
    }

    public boolean isInDeferredGestureRegion(MotionEvent event) {
        return this.mDeferredGestureRegion.contains((int) event.getX(), (int) event.getY());
    }

    public boolean isInExclusionRegion(MotionEvent event) {
        Region region;
        if (event.getDisplayId() == 0) {
            region = this.mExclusionRegion;
        } else {
            region = this.mExclusionRegionForMultiDisplay;
        }
        return this.mMode == SysUINavigationMode.Mode.NO_BUTTON && region != null && region.contains((int) event.getX(), (int) event.getY());
    }

    public void setAssistantAvailable(boolean assistantAvailable) {
        setAssistantAvailable(assistantAvailable, 0);
    }

    public void setAssistantVisibility(float visibility) {
        setAssistantVisibility(visibility, 0);
    }

    public float getAssistantVisibility() {
        return this.mAssistantVisibility;
    }

    public boolean canTriggerAssistantAction(MotionEvent ev, ActivityManager.RunningTaskInfo task) {
        return canTriggerAssistantAction(ev, task, ev.getDisplayId());
    }

    public boolean canTriggerOneHandedAction(MotionEvent ev) {
        if (!this.mIsOneHandedModeSupported || !this.mIsOneHandedModeEnabled) {
            return false;
        }
        DisplayController.Info info = this.mDisplayController.getInfo();
        return this.mOrientationTouchTransformer.touchInOneHandedModeRegion(ev) && info.rotation != 1 && info.rotation != 3 && info.densityDpi < 600;
    }

    public boolean isOneHandedModeEnabled() {
        return this.mIsOneHandedModeEnabled;
    }

    public boolean isSwipeToNotificationEnabled() {
        return this.mIsSwipeToNotificationEnabled;
    }

    public boolean isPipActive() {
        return this.mPipIsActive;
    }

    void setOrientationTransformIfNeeded(MotionEvent event) {
        setOrientationTransformIfNeeded(event, 0);
    }

    private void enableMultipleRegions(boolean enable) {
        enableMultipleRegions(enable, 0);
    }

    public void onStartGesture(int displayId) {
        if (this.mTaskListFrozen && displayId == 0) {
            notifySysuiOfCurrentRotation(getOrientationTouchTransformer(0).getCurrentActiveRotation());
        }
    }

    void onEndTargetCalculated(GestureState.GestureEndTarget endTarget, BaseActivityInterface activityInterface) {
        if (endTarget == GestureState.GestureEndTarget.RECENTS) {
            this.mInOverview = true;
            if (!this.mTaskListFrozen) {
                enableMultipleRegions(true, activityInterface.mDisplayId);
            }
            activityInterface.onExitOverview(this, this.mExitOverviewRunnable);
            return;
        }
        if (endTarget == GestureState.GestureEndTarget.HOME) {
            enableMultipleRegions(false, activityInterface.mDisplayId);
            return;
        }
        if (endTarget == GestureState.GestureEndTarget.NEW_TASK) {
            if (getOrientationTouchTransformer(activityInterface.mDisplayId).getQuickStepStartingRotation() == -1) {
                enableMultipleRegions(true, activityInterface.mDisplayId);
            } else if (activityInterface.mDisplayId == 0) {
                notifySysuiOfCurrentRotation(getOrientationTouchTransformer(activityInterface.mDisplayId).getCurrentActiveRotation());
            }
            this.mPrioritizeDeviceRotation = false;
            return;
        }
        if (endTarget == GestureState.GestureEndTarget.LAST_TASK && this.mTaskListFrozen) {
            notifySysuiOfCurrentRotation(getOrientationTouchTransformer(activityInterface.mDisplayId).getCurrentActiveRotation());
        }
    }

    private void notifySysuiOfCurrentRotation(final int rotation) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentsAnimationDeviceState$Px6ThcZNG-bESJcMDekTCMelKFU
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$notifySysuiOfCurrentRotation$15$RecentsAnimationDeviceState(rotation);
            }
        });
    }

    public /* synthetic */ void lambda$notifySysuiOfCurrentRotation$15$RecentsAnimationDeviceState(int i) {
        SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).notifyPrioritizedRotation(i);
    }

    private void toggleSecondaryNavBarsForRotation() {
        this.mOrientationTouchTransformer.setSingleActiveRegion(this.mDisplayController.getInfo());
        notifySysuiOfCurrentRotation(this.mOrientationTouchTransformer.getCurrentActiveRotation());
    }

    public int getCurrentActiveRotation() {
        return getCurrentActiveRotation(0);
    }

    public int getDisplayRotation() {
        return getDisplayRotation(0);
    }

    public void dump(PrintWriter pw) {
        pw.println("DeviceState:");
        pw.println("  canStartSystemGesture=" + canStartSystemGesture());
        pw.println("  systemUiFlags=" + this.mSystemUiStateFlags);
        pw.println("  systemUiFlagsDesc=" + QuickStepContract.getSystemUiStateString(this.mSystemUiStateFlags));
        pw.println("  assistantAvailable=" + this.mAssistantAvailable);
        pw.println("  assistantDisabled=" + QuickStepContract.isAssistantGestureDisabled(this.mSystemUiStateFlags));
        pw.println("  currentActiveRotation=" + getCurrentActiveRotation());
        pw.println("  displayRotation=" + getDisplayRotation());
        pw.println("  isUserUnlocked=" + this.mIsUserUnlocked);
        this.mOrientationTouchTransformer.dump(pw);
    }

    public OrientationTouchTransformer getOrientationTouchTransformer(int displayId) {
        if (displayId == 0) {
            return this.mOrientationTouchTransformer;
        }
        return this.mOrientationTouchTransformerForMulti;
    }

    public NavBarPosition getNavBarPosition(int displayId) {
        if (displayId == 0) {
            return this.mNavBarPosition;
        }
        if (this.mNavBarPositionForMulti == null) {
            this.mNavBarPositionForMulti = new NavBarPosition(this.mMode, this.mDisplayController.getInfo(displayId));
        }
        return this.mNavBarPositionForMulti;
    }

    public void setSystemUiFlags(int displayId, int stateFlags) {
        if (displayId == 0) {
            this.mSystemUiStateFlags = stateFlags;
        } else {
            this.mSystemUiStateFlagsForMulti = stateFlags;
        }
    }

    public int getSystemUiStateFlags(int displayId) {
        if (displayId == 0) {
            return this.mSystemUiStateFlags;
        }
        return this.mSystemUiStateFlagsForMulti;
    }

    public boolean canStartSystemGesture(int displayId) {
        int systemUiStateFlags = getSystemUiStateFlags(displayId);
        if (((systemUiStateFlags & 2) == 0 || (131072 & systemUiStateFlags) != 0 || this.mTaskListFrozen) && (systemUiStateFlags & 4) == 0 && (systemUiStateFlags & 64) == 0 && (systemUiStateFlags & 2048) == 0 && (524288 & systemUiStateFlags) == 0) {
            return (systemUiStateFlags & 256) == 0 || (systemUiStateFlags & 128) == 0;
        }
        return false;
    }

    public boolean isKeyguardShowingOccluded(int displayId) {
        return (getSystemUiStateFlags(displayId) & 512) != 0;
    }

    public boolean isScreenPinningActive(int displayId) {
        return (getSystemUiStateFlags(displayId) & 1) != 0;
    }

    public boolean isBubblesExpanded(int displayId) {
        return (getSystemUiStateFlags(displayId) & 16384) != 0;
    }

    public boolean isSystemUiDialogShowing(int displayId) {
        return (getSystemUiStateFlags(displayId) & 32768) != 0;
    }

    public boolean isAccessibilityMenuAvailable(int displayId) {
        return (getSystemUiStateFlags(displayId) & 16) != 0;
    }

    public boolean isAccessibilityMenuShortcutAvailable(int displayId) {
        return (getSystemUiStateFlags(displayId) & 32) != 0;
    }

    public boolean isHomeDisabled(int displayId) {
        return (getSystemUiStateFlags(displayId) & 256) != 0;
    }

    public boolean isOverviewDisabled(int displayId) {
        return (getSystemUiStateFlags(displayId) & 128) != 0;
    }

    public void updateGestureTouchRegions(int displayId) {
        if (this.mMode.hasGestures) {
            if (displayId == 0) {
                this.mOrientationTouchTransformer.createOrAddTouchRegion(this.mDisplayController.getInfo());
                return;
            }
            LGLog.d(TAG, "updateGestureTouchRegions : info(" + displayId + ") = " + this.mDisplayController.getInfo(displayId));
            this.mOrientationTouchTransformerForMulti.createOrAddTouchRegion(this.mDisplayController.getInfo(displayId));
        }
    }

    public void setAssistantAvailable(boolean assistantAvailable, int displayId) {
        if (displayId == 0) {
            this.mAssistantAvailable = assistantAvailable;
        } else {
            this.mAssistantAvailableForMulti = assistantAvailable;
        }
    }

    public boolean getAssistantAvailable(int displayId) {
        if (displayId == 0) {
            return this.mAssistantAvailable;
        }
        return this.mAssistantAvailableForMulti;
    }

    public void setAssistantVisibility(float visibility, int displayId) {
        if (displayId == 0) {
            this.mAssistantVisibility = visibility;
        } else {
            this.mAssistantVisibilityForMulti = visibility;
        }
    }

    public float getAssistantAvailAssistantVisibility(int displayId) {
        if (displayId == 0) {
            return this.mAssistantVisibility;
        }
        return this.mAssistantVisibilityForMulti;
    }

    public boolean canTriggerAssistantAction(MotionEvent ev, ActivityManager.RunningTaskInfo task, int displayId) {
        return (!getAssistantAvailable(displayId) || QuickStepContract.isAssistantGestureDisabled(getSystemUiStateFlags(displayId)) || !getOrientationTouchTransformer(displayId).touchInAssistantRegion(ev) || isLockToAppActive() || isGestureBlockedActivity(task)) ? false : true;
    }

    void setOrientationTransformIfNeeded(MotionEvent event, int displayId) {
        if (event.getX() < 0.0f || event.getY() < 0.0f) {
            event.setLocation(Math.max(0.0f, event.getX()), Math.max(0.0f, event.getY()));
        }
        getOrientationTouchTransformer(displayId).transform(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableMultipleRegions(boolean enable, int displayId) {
        getOrientationTouchTransformer(displayId).enableMultipleRegions(enable, this.mDisplayController.getInfo(displayId));
        if (displayId == 0) {
            notifySysuiOfCurrentRotation(getOrientationTouchTransformer(displayId).getQuickStepStartingRotation());
        }
        if (enable && !this.mInOverview && !TestProtocol.sDisableSensorRotation) {
            this.mSensorRotation = this.mCurrentAppRotation;
            this.mOrientationListener.enable();
        } else {
            this.mOrientationListener.disable();
        }
    }

    public int getCurrentActiveRotation(int displayId) {
        if (!this.mMode.hasGestures) {
            return getDisplayRotation(displayId);
        }
        return getOrientationTouchTransformer(displayId).getCurrentActiveRotation();
    }

    public int getDisplayRotation(int displayId) {
        if (displayId == 0) {
            return this.mDisplayRotation;
        }
        return this.mDisplayRotationForMulti;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleSecondaryNavBarsForRotation(int displayId) {
        getOrientationTouchTransformer(displayId).setSingleActiveRegion(this.mDisplayController.getInfo(displayId));
        if (displayId == 0) {
            notifySysuiOfCurrentRotation(getOrientationTouchTransformer(displayId).getCurrentActiveRotation());
        }
    }
}
