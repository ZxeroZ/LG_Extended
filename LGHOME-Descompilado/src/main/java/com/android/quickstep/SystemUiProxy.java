package com.android.quickstep;

import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.window.IOnBackInvokedCallback;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.quickstep.SysUINavigationMode;
import com.android.systemui.shared.recents.ISystemUiProxy;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.RemoteTransitionCompat;
import com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController;
import com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController;
import com.android.systemui.shared.system.smartspace.SmartspaceState;
import com.android.wm.shell.back.IBackAnimation;
import com.android.wm.shell.onehanded.IOneHanded;
import com.android.wm.shell.pip.IPip;
import com.android.wm.shell.pip.IPipAnimationListener;
import com.android.wm.shell.recents.IRecentTasks;
import com.android.wm.shell.recents.IRecentTasksListener;
import com.android.wm.shell.splitscreen.ISplitScreen;
import com.android.wm.shell.splitscreen.ISplitScreenListener;
import com.android.wm.shell.startingsurface.IStartingWindow;
import com.android.wm.shell.startingsurface.IStartingWindowListener;
import com.android.wm.shell.transition.IShellTransitions;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SystemUiProxy implements ISystemUiProxy, SysUINavigationMode.NavigationModeChangeListener {
    public static final MainThreadInitializedObject<SystemUiProxy> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.quickstep.-$$Lambda$iAxkpMrj0WdrsxUoJw1biy3lxbw
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return new SystemUiProxy(context);
        }
    });
    private static final String TAG = "SystemUiProxy";
    private IBackAnimation mBackAnimation;
    private IOnBackInvokedCallback mBackToLauncherCallback;
    private float mLastNavButtonAlpha;
    private boolean mLastNavButtonAnimate;
    private int mLastShelfHeight;
    private boolean mLastShelfVisible;
    private int mLastSystemUiStateFlags;
    private IOneHanded mOneHanded;
    private ILauncherUnlockAnimationController mPendingLauncherUnlockAnimationController;
    private IPip mPip;
    private IPipAnimationListener mPipAnimationListener;
    private IRecentTasks mRecentTasks;
    private IRecentTasksListener mRecentTasksListener;
    private IShellTransitions mShellTransitions;
    private ISplitScreen mSplitScreen;
    private ISplitScreenListener mSplitScreenListener;
    private IStartingWindow mStartingWindow;
    private IStartingWindowListener mStartingWindowListener;
    private ISystemUiProxy mSystemUiProxy;
    private ISysuiUnlockAnimationController mSysuiUnlockAnimationController;
    private final IBinder.DeathRecipient mSystemUiProxyDeathRecipient = new IBinder.DeathRecipient() { // from class: com.android.quickstep.-$$Lambda$SystemUiProxy$Y5UAyaERPop6VyhJ0gqHph6olgY
        @Override // android.os.IBinder.DeathRecipient
        public final void binderDied() {
            this.f$0.lambda$new$1$SystemUiProxy();
        }
    };
    private final ArrayList<RemoteTransitionCompat> mRemoteTransitions = new ArrayList<>();
    private boolean mHasNavButtonAlphaBeenSet = false;
    private Runnable mPendingSetNavButtonAlpha = null;

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return null;
    }

    public /* synthetic */ void lambda$new$1$SystemUiProxy() {
        Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$SystemUiProxy$kEOCizQmmDw_KpYq_1li3YSZFuA
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0$SystemUiProxy();
            }
        });
    }

    public SystemUiProxy(Context context) {
        SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).addModeChangeListener(this);
    }

    @Override // com.android.quickstep.SysUINavigationMode.NavigationModeChangeListener
    public void onNavigationModeChanged(SysUINavigationMode.Mode newMode) {
        lambda$setNavBarButtonAlpha$2$SystemUiProxy(1.0f, false);
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void onBackPressed() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onBackPressed();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onBackPressed", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void onImeSwitcherPressed() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onImeSwitcherPressed();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onImeSwitcherPressed", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void setHomeRotationEnabled(boolean enabled) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.setHomeRotationEnabled(enabled);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onBackPressed", e);
            }
        }
    }

    public void setProxy(ISystemUiProxy proxy, IPip pip, ISplitScreen splitScreen, IOneHanded oneHanded, IShellTransitions shellTransitions, IStartingWindow startingWindow, IRecentTasks recentTasks, ISysuiUnlockAnimationController sysuiUnlockAnimationController, IBackAnimation backAnimation) {
        IOnBackInvokedCallback iOnBackInvokedCallback;
        unlinkToDeath();
        this.mSystemUiProxy = proxy;
        this.mPip = pip;
        this.mSplitScreen = splitScreen;
        this.mOneHanded = oneHanded;
        this.mShellTransitions = shellTransitions;
        this.mStartingWindow = startingWindow;
        this.mSysuiUnlockAnimationController = sysuiUnlockAnimationController;
        this.mRecentTasks = recentTasks;
        this.mBackAnimation = backAnimation;
        linkToDeath();
        IPipAnimationListener iPipAnimationListener = this.mPipAnimationListener;
        if (iPipAnimationListener != null && this.mPip != null) {
            setPinnedStackAnimationListener(iPipAnimationListener);
        }
        ISplitScreenListener iSplitScreenListener = this.mSplitScreenListener;
        if (iSplitScreenListener != null && this.mSplitScreen != null) {
            registerSplitScreenListener(iSplitScreenListener);
        }
        IStartingWindowListener iStartingWindowListener = this.mStartingWindowListener;
        if (iStartingWindowListener != null && this.mStartingWindow != null) {
            setStartingWindowListener(iStartingWindowListener);
        }
        ILauncherUnlockAnimationController iLauncherUnlockAnimationController = this.mPendingLauncherUnlockAnimationController;
        if (iLauncherUnlockAnimationController != null && this.mSysuiUnlockAnimationController != null) {
            setLauncherUnlockAnimationController(iLauncherUnlockAnimationController);
            this.mPendingLauncherUnlockAnimationController = null;
        }
        for (int size = this.mRemoteTransitions.size() - 1; size >= 0; size--) {
            registerRemoteTransition(this.mRemoteTransitions.get(size));
        }
        IRecentTasksListener iRecentTasksListener = this.mRecentTasksListener;
        if (iRecentTasksListener != null && this.mRecentTasks != null) {
            registerRecentTasksListener(iRecentTasksListener);
        }
        if (this.mBackAnimation != null && (iOnBackInvokedCallback = this.mBackToLauncherCallback) != null) {
            setBackToLauncherCallback(iOnBackInvokedCallback);
        }
        Runnable runnable = this.mPendingSetNavButtonAlpha;
        if (runnable != null) {
            runnable.run();
            this.mPendingSetNavButtonAlpha = null;
        }
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$new$0$SystemUiProxy()V */
    /* JADX INFO: renamed from: clearProxy, reason: merged with bridge method [inline-methods] */
    public void lambda$new$0$SystemUiProxy() {
        setProxy(null, null, null, null, null, null, null, null, null);
    }

    public void setLastSystemUiStateFlags(int stateFlags) {
        this.mLastSystemUiStateFlags = stateFlags;
    }

    public int getLastSystemUiStateFlags() {
        return this.mLastSystemUiStateFlags;
    }

    public boolean isActive() {
        return this.mSystemUiProxy != null;
    }

    private void linkToDeath() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.asBinder().linkToDeath(this.mSystemUiProxyDeathRecipient, 0);
            } catch (RemoteException unused) {
                Log.e(TAG, "Failed to link sysui proxy death recipient");
            }
        }
    }

    private void unlinkToDeath() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            iSystemUiProxy.asBinder().unlinkToDeath(this.mSystemUiProxyDeathRecipient, 0);
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void startScreenPinning(int taskId) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.startScreenPinning(taskId);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call startScreenPinning", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void onOverviewShown(boolean fromHome) {
        onOverviewShown(fromHome, TAG);
    }

    public void onOverviewShown(boolean fromHome, String tag) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onOverviewShown(fromHome);
            } catch (RemoteException e) {
                Log.w(tag, "Failed call onOverviewShown from: " + (fromHome ? "home" : "app"), e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public Rect getNonMinimizedSplitScreenSecondaryBounds() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy == null) {
            return null;
        }
        try {
            return iSystemUiProxy.getNonMinimizedSplitScreenSecondaryBounds();
        } catch (RemoteException e) {
            Log.w(TAG, "Failed call getNonMinimizedSplitScreenSecondaryBounds", e);
            return null;
        }
    }

    public float getLastNavButtonAlpha() {
        return this.mLastNavButtonAlpha;
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$setNavBarButtonAlpha$2$SystemUiProxy(FZ)V */
    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    /* JADX INFO: renamed from: setNavBarButtonAlpha, reason: merged with bridge method [inline-methods] */
    public void lambda$setNavBarButtonAlpha$2$SystemUiProxy(final float alpha, final boolean animate) {
        if ((Float.compare(alpha, this.mLastNavButtonAlpha) == 0 && animate == this.mLastNavButtonAnimate && this.mHasNavButtonAlphaBeenSet) ? false : true) {
            ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
            if (iSystemUiProxy == null) {
                this.mPendingSetNavButtonAlpha = new Runnable() { // from class: com.android.quickstep.-$$Lambda$SystemUiProxy$X52JpX3Vjl16TkSvgoQ9COlguXc
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$setNavBarButtonAlpha$2$SystemUiProxy(alpha, animate);
                    }
                };
                return;
            }
            this.mLastNavButtonAlpha = alpha;
            this.mLastNavButtonAnimate = animate;
            this.mHasNavButtonAlphaBeenSet = true;
            try {
                iSystemUiProxy.lambda$setNavBarButtonAlpha$2$SystemUiProxy(alpha, animate);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setNavBarButtonAlpha", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void onStatusBarMotionEvent(MotionEvent event) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onStatusBarMotionEvent(event);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onStatusBarMotionEvent", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void onAssistantProgress(float progress) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onAssistantProgress(progress);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onAssistantProgress with progress: " + progress, e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void onAssistantGestureCompletion(float velocity) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onAssistantGestureCompletion(velocity);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onAssistantGestureCompletion", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void startAssistant(Bundle args) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.startAssistant(args);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call startAssistant", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void notifyAccessibilityButtonClicked(int displayId) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifyAccessibilityButtonClicked(displayId);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifyAccessibilityButtonClicked", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void notifyAccessibilityButtonLongClicked() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifyAccessibilityButtonLongClicked();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifyAccessibilityButtonLongClicked", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void stopScreenPinning() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.stopScreenPinning();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call stopScreenPinning", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.handleImageAsScreenshot(bitmap, rect, insets, i);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call handleImageAsScreenshot", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void setSplitScreenMinimized(boolean minimized) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.setSplitScreenMinimized(minimized);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setSplitScreenMinimized", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void notifySwipeUpGestureStarted() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifySwipeUpGestureStarted();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifySwipeUpGestureStarted", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void notifySwipeToHomeFinished() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifySwipeToHomeFinished();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifySwipeToHomeFinished", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void expandNotificationPanel() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.expandNotificationPanel();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call expandNotificationPanel", e);
            }
        }
    }

    public void setShelfHeight(boolean visible, int shelfHeight) {
        boolean z = (visible == this.mLastShelfVisible && shelfHeight == this.mLastShelfHeight) ? false : true;
        IPip iPip = this.mPip;
        if (iPip == null || !z) {
            return;
        }
        this.mLastShelfVisible = visible;
        this.mLastShelfHeight = shelfHeight;
        try {
            iPip.setShelfHeight(visible, shelfHeight);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed call setShelfHeight visible: " + visible + " height: " + shelfHeight, e);
        }
    }

    public void setPinnedStackAnimationListener(IPipAnimationListener listener) {
        IPip iPip = this.mPip;
        if (iPip != null) {
            try {
                iPip.setPinnedStackAnimationListener(listener);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setPinnedStackAnimationListener", e);
            }
        }
        this.mPipAnimationListener = listener;
    }

    public Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int launcherRotation, int shelfHeight) {
        IPip iPip = this.mPip;
        if (iPip == null) {
            return null;
        }
        try {
            return iPip.startSwipePipToHome(componentName, activityInfo, pictureInPictureParams, launcherRotation, shelfHeight);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed call startSwipePipToHome", e);
            return null;
        }
    }

    public void stopSwipePipToHome(int taskId, ComponentName componentName, Rect destinationBounds, SurfaceControl overlay) {
        IPip iPip = this.mPip;
        if (iPip != null) {
            try {
                iPip.stopSwipePipToHome(taskId, componentName, destinationBounds, overlay);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call stopSwipePipToHome");
            }
        }
    }

    public void registerSplitScreenListener(ISplitScreenListener listener) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.registerSplitScreenListener(listener);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call registerSplitScreenListener");
            }
        }
        this.mSplitScreenListener = listener;
    }

    public void unregisterSplitScreenListener(ISplitScreenListener listener) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.unregisterSplitScreenListener(listener);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call unregisterSplitScreenListener");
            }
        }
        this.mSplitScreenListener = null;
    }

    public void startTask(int taskId, int stage, int position, Bundle options) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.startTask(taskId, stage, options);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startTask");
            }
        }
    }

    public void startTasks(int mainTaskId, Bundle mainOptions, int sideTaskId, Bundle sideOptions, int sidePosition, float splitRatio, RemoteTransitionCompat remoteTransition) {
        if (this.mSystemUiProxy != null) {
            try {
                this.mSplitScreen.startTasks(mainTaskId, mainOptions, sideTaskId, sideOptions, sidePosition, splitRatio, remoteTransition.getTransition());
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startTask");
            }
        }
    }

    public void startTasksWithLegacyTransition(int mainTaskId, Bundle mainOptions, int sideTaskId, Bundle sideOptions, int sidePosition, float splitRatio, RemoteAnimationAdapter adapter) {
        if (this.mSystemUiProxy != null) {
            try {
                this.mSplitScreen.startTasksWithLegacyTransition(mainTaskId, mainOptions, sideTaskId, sideOptions, sidePosition, splitRatio, adapter);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startTasksWithLegacyTransition");
            }
        }
    }

    public void startIntentAndTaskWithLegacyTransition(PendingIntent pendingIntent, Intent fillInIntent, int taskId, Bundle mainOptions, Bundle sideOptions, int sidePosition, float splitRatio, RemoteAnimationAdapter adapter) {
        if (this.mSystemUiProxy != null) {
            try {
                this.mSplitScreen.startIntentAndTaskWithLegacyTransition(pendingIntent, fillInIntent, taskId, mainOptions, sideOptions, sidePosition, splitRatio, adapter);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startTasksWithLegacyTransition");
            }
        }
    }

    public void startShortcut(String packageName, String shortcutId, int position, Bundle options, UserHandle user) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.startShortcut(packageName, shortcutId, position, options, user);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startShortcut");
            }
        }
    }

    public void startIntent(PendingIntent intent, Intent fillInIntent, int position, Bundle options) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.startIntent(intent, fillInIntent, position, options);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startIntent");
            }
        }
    }

    public void removeFromSideStage(int taskId) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.removeFromSideStage(taskId);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call removeFromSideStage");
            }
        }
    }

    public void exitSplitScreen(int toTopTaskId) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.exitSplitScreen(toTopTaskId);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call exitSplitScreen");
            }
        }
    }

    public RemoteAnimationTarget[] onGoingToRecentsLegacy(RemoteAnimationTarget[] apps, int displayId) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen == null) {
            return null;
        }
        try {
            return iSplitScreen.onGoingToRecentsLegacy(apps, displayId);
        } catch (RemoteException unused) {
            Log.w(TAG, "Failed call onGoingToRecentsLegacy");
            return null;
        }
    }

    public RemoteAnimationTarget[] onStartingSplitLegacy(RemoteAnimationTarget[] apps) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen == null) {
            return null;
        }
        try {
            return iSplitScreen.onStartingSplitLegacy(apps);
        } catch (RemoteException unused) {
            Log.w(TAG, "Failed call onStartingSplitLegacy");
            return null;
        }
    }

    public void onFinishGoingToRecentsLegacy() {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.onFinishGoingToRecentsLegacy();
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call onFinishGoingToRecentsLegacy");
            }
        }
    }

    public void onNotifyGestureStarted(int displayId) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.onNotifyGestureStarted(displayId);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call onFinishGoingToRecentsLegacy");
            }
        }
    }

    public boolean isSplitScreenVisible() {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen == null) {
            return false;
        }
        try {
            return iSplitScreen.isSplitScreenVisible();
        } catch (RemoteException unused) {
            Log.w(TAG, "Failed call isSplitScreenVisible");
            return false;
        }
    }

    public void startOneHandedMode() {
        IOneHanded iOneHanded = this.mOneHanded;
        if (iOneHanded != null) {
            try {
                iOneHanded.startOneHanded();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call startOneHandedMode", e);
            }
        }
    }

    public void stopOneHandedMode() {
        IOneHanded iOneHanded = this.mOneHanded;
        if (iOneHanded != null) {
            try {
                iOneHanded.stopOneHanded();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call stopOneHandedMode", e);
            }
        }
    }

    public void registerRemoteTransition(RemoteTransitionCompat remoteTransition) {
        IShellTransitions iShellTransitions = this.mShellTransitions;
        if (iShellTransitions != null) {
            try {
                iShellTransitions.registerRemote(remoteTransition.getFilter(), remoteTransition.getTransition());
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call registerRemoteTransition");
            }
        }
        if (this.mRemoteTransitions.contains(remoteTransition)) {
            return;
        }
        this.mRemoteTransitions.add(remoteTransition);
    }

    public void unregisterRemoteTransition(RemoteTransitionCompat remoteTransition) {
        IShellTransitions iShellTransitions = this.mShellTransitions;
        if (iShellTransitions != null) {
            try {
                iShellTransitions.unregisterRemote(remoteTransition.getTransition());
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call registerRemoteTransition");
            }
        }
        this.mRemoteTransitions.remove(remoteTransition);
    }

    public void setStartingWindowListener(IStartingWindowListener listener) {
        IStartingWindow iStartingWindow = this.mStartingWindow;
        if (iStartingWindow != null) {
            try {
                iStartingWindow.setStartingWindowListener(listener);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setStartingWindowListener", e);
            }
        }
        this.mStartingWindowListener = listener;
    }

    public void setLauncherUnlockAnimationController(ILauncherUnlockAnimationController controller) {
        ISysuiUnlockAnimationController iSysuiUnlockAnimationController = this.mSysuiUnlockAnimationController;
        if (iSysuiUnlockAnimationController != null) {
            try {
                iSysuiUnlockAnimationController.setLauncherUnlockController(controller);
                if (controller != null) {
                    controller.dispatchSmartspaceStateToSysui();
                    return;
                }
                return;
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setStartingWindowListener", e);
                return;
            }
        }
        this.mPendingLauncherUnlockAnimationController = controller;
    }

    public void notifySysuiSmartspaceStateUpdated(SmartspaceState state) {
        ISysuiUnlockAnimationController iSysuiUnlockAnimationController = this.mSysuiUnlockAnimationController;
        if (iSysuiUnlockAnimationController != null) {
            try {
                iSysuiUnlockAnimationController.onLauncherSmartspaceStateUpdated(state);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifySysuiSmartspaceStateUpdated", e);
                e.printStackTrace();
            }
        }
    }

    public void registerRecentTasksListener(IRecentTasksListener listener) {
        IRecentTasks iRecentTasks = this.mRecentTasks;
        if (iRecentTasks != null) {
            try {
                iRecentTasks.registerRecentTasksListener(listener);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call registerRecentTasksListener", e);
            }
        }
        this.mRecentTasksListener = listener;
    }

    public void unregisterRecentTasksListener(IRecentTasksListener listener) {
        IRecentTasks iRecentTasks = this.mRecentTasks;
        if (iRecentTasks != null) {
            try {
                iRecentTasks.unregisterRecentTasksListener(listener);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call unregisterRecentTasksListener");
            }
        }
        this.mRecentTasksListener = null;
    }

    public void setBackToLauncherCallback(IOnBackInvokedCallback callback) {
        this.mBackToLauncherCallback = callback;
        IBackAnimation iBackAnimation = this.mBackAnimation;
        if (iBackAnimation == null) {
            return;
        }
        try {
            iBackAnimation.setBackToLauncherCallback(callback);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed call setBackToLauncherCallback", e);
        }
    }

    public void clearBackToLauncherCallback(IOnBackInvokedCallback callback) {
        if (this.mBackToLauncherCallback != callback) {
            return;
        }
        this.mBackToLauncherCallback = null;
        IBackAnimation iBackAnimation = this.mBackAnimation;
        if (iBackAnimation == null) {
            return;
        }
        try {
            iBackAnimation.clearBackToLauncherCallback();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed call clearBackToLauncherCallback", e);
        }
    }

    public void onBackToLauncherAnimationFinished() {
        IBackAnimation iBackAnimation = this.mBackAnimation;
        if (iBackAnimation != null) {
            try {
                iBackAnimation.onBackToLauncherAnimationFinished();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onBackAnimationFinished", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void notifyPrioritizedRotation(int rotation) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifyPrioritizedRotation(rotation);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifyPrioritizedRotation with arg: " + rotation, e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void notifyTaskbarStatus(boolean visible, boolean stashed) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifyTaskbarStatus(visible, stashed);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifyTaskbarStatus with arg: " + visible + ", " + stashed, e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void notifyTaskbarAutohideSuspend(boolean suspend) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifyTaskbarAutohideSuspend(suspend);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifyTaskbarAutohideSuspend with arg: " + suspend, e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void toggleNotificationPanel() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.toggleNotificationPanel();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call toggleNotificationPanel", e);
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void handleImageBundleAsScreenshot(Bundle screenImageBundle, Rect locationInScreen, Insets visibleInsets, Task.TaskKey task) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.handleImageBundleAsScreenshot(screenImageBundle, locationInScreen, visibleInsets, task);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call handleImageBundleAsScreenshot");
            }
        }
    }

    @Override // com.android.systemui.shared.recents.ISystemUiProxy
    public void startFullscreenMode(int displayId, String packageName) throws RemoteException {
        Log.d(TAG, "startFullscreenMode");
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.startFullscreenMode(displayId, packageName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call startFullscreenMode", e);
            }
        }
    }
}
