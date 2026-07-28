package com.lge.launcher3;

import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.ActiveFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.PinchDecision;
import android.view.TouchEventFilter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.android.launcher3.Alarm;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetHost;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.LauncherCallbacks;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.LauncherState;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.PageIndicator;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.WeightWatcher;
import com.android.launcher3.Workspace;
import com.android.launcher3.allapps.AllAppsSearchBarController;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.ShadowGenerator;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.WidgetsModel;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.uioverrides.QuickstepLauncher;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.WallpaperUtils;
import com.android.launcher3.widget.WidgetsContainerView;
import com.android.quickstep.RecentsModel;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.SystemUiProxy;
import com.evie.screen.api.EvieApi;
import com.evie.screen.api.EvieApiCallbacks;
import com.google.android.libraries.gsa.launcherclient.LauncherClient;
import com.google.android.libraries.gsa.launcherclient.LauncherClientCallbacks;
import com.lge.app.permission.DefaultUiProvider;
import com.lge.app.permission.RequestPermissionsHelper;
import com.lge.launcher3.DDTChangeWatcher;
import com.lge.launcher3.ScreenZoomChangeWatcher;
import com.lge.launcher3.adaptive.AdaptiveTextManager;
import com.lge.launcher3.adaptive.AdaptiveTextUtil;
import com.lge.launcher3.adaptive.LiveWallpaperColorObserver;
import com.lge.launcher3.adaptive.WallpaperColorInfoUtil;
import com.lge.launcher3.badge.BadgeUtils;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.badge.uninstall.UninstallBadgeUtils;
import com.lge.launcher3.concierge.ConciergeBoardMngr;
import com.lge.launcher3.concierge.ConciergeBoardNotificationReceiver;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.config.QMemoPanelConst;
import com.lge.launcher3.debug.DuplicatedApplicationChecker;
import com.lge.launcher3.debug.EventLogger;
import com.lge.launcher3.debug.LGHiddenMenuUtil;
import com.lge.launcher3.folder.FolderColorUtil;
import com.lge.launcher3.folder.FolderStateTransitionWatcher;
import com.lge.launcher3.homesettings.SBHomeDataBaseUtil;
import com.lge.launcher3.homesettings.SettingsSearchUtils;
import com.lge.launcher3.initialguide.InitialGuideManager;
import com.lge.launcher3.initialguide.MultiWindowGuideManager;
import com.lge.launcher3.initialguide.SwivelHomeGuideManager;
import com.lge.launcher3.liveicon.LiveIcon;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.liveicon.OnLiveIconUpdateListener;
import com.lge.launcher3.operator.GVNScreenSoundImage;
import com.lge.launcher3.operator.GVNUtils;
import com.lge.launcher3.operator.GoogleNowManager;
import com.lge.launcher3.operator.Operator;
import com.lge.launcher3.operator.VZWSideScreenManager;
import com.lge.launcher3.profile.LGDeviceProfile;
import com.lge.launcher3.receiver.PendingIntentObjectList;
import com.lge.launcher3.receiver.PendingIntentReceiver;
import com.lge.launcher3.receiver.TPhoneModeReceiver;
import com.lge.launcher3.screeneffect.LauncherScrollerWatcher;
import com.lge.launcher3.screeneffect.LoopNormalModeManager;
import com.lge.launcher3.screeneffect.ScreenEffectManager;
import com.lge.launcher3.screeneffect.WorkspaceStateTransitionWatcher;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.smartbulletin.constant.SBConstant;
import com.lge.launcher3.uioverrides.InAppsState;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.CPUBoostService;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.GiftBoxManager;
import com.lge.launcher3.util.IntentUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.SystemClockUtils;
import com.lge.launcher3.util.TPhoneModeUtils;
import com.lge.launcher3.util.TimeChecker;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.wallpaperblur.HomescreenBlurManager;
import com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController;
import com.lge.launcher3.wallpaperblur.WidgetBlurManager;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine;
import com.lge.launcher3.wallpapermotion.WallpaperMotionManager;
import com.lge.launcher3.wallpapermotion.WallpaperMotionUtils;
import com.lge.launcher3.wallpaperpicker.WallpaperChangeReceiver;
import com.lge.launcher3.widgettray.LGWidgetContainerView;
import com.lge.launcher3.widgettray.LGWidgetPreviewLoader;
import com.lge.launcher3.wing.CarouselLayout;
import com.lge.launcher3.wing.SwivelAppIconCache;
import com.lge.launcher3.wing.SwivelContentsView;
import com.lge.launcher3.wing.carousel.widget.CarouselView;
import com.lge.lgewidgetlib.LgeWidgetContext;
import com.lge.systemservice.core.IPostureStateCallback;
import com.lge.systemservice.core.LGContext;
import com.lge.systemservice.core.PostureManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LauncherExtension extends QuickstepLauncher implements DDTChangeWatcher.DDTChangeListener, ScreenZoomChangeWatcher.ScreenZoomChangeListener, GiftBoxManager.OnDataFreeAppUpdateListener, WallpaperColorInfoUtil.OnSwivelWallpaperChangeListener {
    public static final int ADD_APPS_AUTOMATICALLY = 2;
    private static final int DELETED_APP = 0;
    private static final boolean DEVELOPER_MODE = false;
    private static final int ORIGINAL_HOME = 0;
    private static final int PINCH_INTERVAL_TIME = 200;
    private static final int SWIVEL_HOME = 1;
    private static final String TAG = "LauncherExtension";
    private static Handler mDeleteItemHander;
    public static ArrayList<ItemInfo> mInstallQueueApps;
    public static Handler mInstallQueueSwivelHander;
    private AdaptiveTextManager mAdaptiveTextManager;
    private boolean mAlreadyOnHome;
    private AppDrawerButtonObserver mAppDrawerButtonObserver;
    public ArrayList<ShortcutInfo> mAppList;
    private BurnInProtectionHelper mBurnInProtectionHelper;
    private boolean mDateChangedReceiverRegistered;
    private EditModeOffManager mEditModeOffManager;
    private GiftBoxManager mGiftBoxManager;
    private GiftBoxObserver mGiftBoxObserver;
    private boolean mIsActivated;
    private boolean mIsMultiWindowModeInternal;
    private boolean mIsSwivelHome;
    private boolean mIsSwivelHomeShown;
    private boolean mIsSwivelItemInitialized;
    private LiveWallpaperColorObserver mLiveWallpaperColorObserver;
    private PinchDecision mPinchDecision;
    private long mPoint1DownTime;
    private PostureManager mPostureManager;
    private SelectedHomeObserver mSelectedHomeObserver;
    private long mStartSwivelHomeTime;
    private boolean mStarted;
    private boolean mStatusOfClientOptions;
    private SwipeUpGuideAnimation mSwipeUpGuideAnimation;
    private TouchEventFilter mTouchEventFilter;
    private boolean mWallpaperChangedReceiverRegistered;
    public Switch videoSwitch;
    public int mCurrentHome = -1;
    private int mPendingMoveScreenIndex = -1;
    boolean mAddAnimationflag = true;
    private WallpaperChangeReceiver mWallpaperChangeReceiver = new WallpaperChangeReceiver();
    private EvieApi mEvieApi = null;
    private int mRotate = -1;
    private boolean isTopActivity = true;
    private final Handler mSwivelHomeStateHandler = new Handler() { // from class: com.lge.launcher3.LauncherExtension.5
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 0) {
                LauncherExtension.this.showOriginalHomeView(true);
            } else {
                if (i != 1) {
                    return;
                }
                LauncherExtension.this.showSwivelHomeView();
            }
        }
    };
    BroadcastReceiver sDateChangedReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.LauncherExtension.6
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (LauncherExtension.this.mCarouselLayout != null) {
                LGLog.i(LauncherExtension.TAG, "Date changed, so update Swivel weather view");
                LauncherExtension.this.mCarouselLayout.updateWeatheInformation();
            }
        }
    };
    BroadcastReceiver sWallpaperChangedReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.LauncherExtension.7
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (LauncherExtension.this.mCarouselLayout != null) {
                LGLog.i(LauncherExtension.TAG, "Wallpaper changed, so update Swivel weather view's adaptive color");
                LauncherExtension.this.mCarouselLayout.getSwivelWeatherView().setAdaptiveColorForWeatherView();
            }
        }
    };
    private final IPostureStateCallback mPostureStateCallback = new IPostureStateCallback.Stub() { // from class: com.lge.launcher3.LauncherExtension.8
        public void onSwivelStateChanged(int state) throws RemoteException {
            LGLog.i(LauncherExtension.TAG, "onSwivelStateChanged " + state + " swivel state : " + LauncherExtension.this.mPostureManager.getSwivelState() + " / swivel home : " + LauncherExtension.this.mIsSwivelHome + " / swivel home is currently shown : " + LauncherExtension.this.mIsSwivelHomeShown);
            if (state == 101) {
                LGHomeFeature.updateEnableSwivelHomeState(LauncherExtension.this.getApplicationContext(), true);
                if (LauncherExtension.this.mIsSwivelHome && LauncherExtension.this.mIsSwivelHomeShown) {
                    return;
                }
                LauncherExtension.this.mSwivelHomeStateHandler.sendMessage(LauncherExtension.this.mSwivelHomeStateHandler.obtainMessage(1));
                LauncherExtension.this.mIsSwivelHome = true;
                return;
            }
            if (state == 201) {
                LGHomeFeature.updateEnableSwivelHomeState(LauncherExtension.this.getApplicationContext(), false);
                if (LauncherExtension.this.mIsSwivelHome || LauncherExtension.this.mIsSwivelHomeShown) {
                    LauncherExtension.this.mSwivelHomeStateHandler.sendMessage(LauncherExtension.this.mSwivelHomeStateHandler.obtainMessage(0));
                    LauncherExtension.this.mIsSwivelHome = false;
                }
                SwivelHomeGuideManager.getInstance(LauncherExtension.this.getApplicationContext()).endSwivel();
                return;
            }
            if (state == 100) {
                LauncherExtension.this.getRotationHelper().setCurrentStateRequest(1);
                LGHomeFeature.updateEnableSwivelHomeState(LauncherExtension.this.getApplicationContext(), true);
                LauncherExtension.this.mIsSwivelHome = true;
            } else if (state == 200) {
                LauncherExtension.this.getRotationHelper().setCurrentStateRequest(1);
                LGHomeFeature.updateEnableSwivelHomeState(LauncherExtension.this.getApplicationContext(), false);
                LauncherExtension.this.mIsSwivelHome = false;
            }
        }
    };
    private final SharedPreferences.OnSharedPreferenceChangeListener mIconChangeListObserver = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.lge.launcher3.LauncherExtension.10
        @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            IconCache iconCache = LauncherAppState.getInstance(LauncherExtension.this).getIconCache();
            iconCache.initCustomAppIconList();
            iconCache.flush();
            LauncherExtension.this.mModel.forceReload();
        }
    };
    private final SharedPreferences.OnSharedPreferenceChangeListener mVZWSideScreenObserver = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.lge.launcher3.LauncherExtension.11
        @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            LGLog.d(LauncherExtension.TAG, "mIVZWSideScreenObserver - prefs = " + prefs + ", key = " + key);
            if (!SharedPreferencesManager.toKeyString(SharedPreferencesConst.VZWSideScreen.IS_ENABLED).equals(key) || LauncherExtension.this.mLauncherCallbacks == null) {
                return;
            }
            ((LauncherCallbacksImpl) LauncherExtension.this.mLauncherCallbacks).updateEvieApi();
        }
    };
    private SpannableStringBuilder mDefaultKeySsb = null;
    private int mOrientationLockCount = 0;
    private final BroadcastReceiver mMDMPolicyReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.LauncherExtension.16
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            LGLog.i(LauncherExtension.TAG, "onReceive() intent:" + intent);
            if (intent == null || intent.getAction() == null) {
                return;
            }
            String action = intent.getAction();
            if (action.equals(IntentConst.Action.ACTION_MDM_CHANGE_UNINSTALLPOLICY.getValue(context)) || action.equals(IntentConst.Action.ACTION_MDM_ADMIN_ACTIVATE.getValue(context)) || action.equals(IntentConst.Action.ACTION_MDM_ADMIN_DEACTIVATE.getValue(context))) {
                LauncherExtension.this.applyMDMPolicy(action, intent.getStringArrayExtra(AppNotifierManager.ExtraSpec.USAGE_PACKAGE));
            }
        }
    };
    private final BroadcastReceiver mBubbleMessageExpandReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.LauncherExtension.17
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            LGLog.i(LauncherExtension.TAG, "onReceive() intent:" + intent);
            if (intent == null || intent.getAction() == null || !intent.getAction().equals(IntentConst.Action.ACTION_BUBBLE_MESSAGE_EXPAND.getValue(context))) {
                return;
            }
            boolean booleanExtra = intent.getBooleanExtra("com.lge.systemui.extra.STATE", false);
            LGLog.i(LauncherExtension.TAG, "Bubble_message_expand state is " + booleanExtra);
            if (booleanExtra) {
                LauncherExtension.this.getRootView().setDisallowBackGesture(!booleanExtra);
            } else {
                LauncherExtension.this.getRootView().setDisallowBackGesture(LauncherExtension.this.shouldBackButtonBeHidden());
            }
        }
    };
    private OnLiveIconUpdateListener mLauncherListener = new OnLiveIconUpdateListener() { // from class: com.lge.launcher3.LauncherExtension.19
        @Override // com.lge.launcher3.liveicon.OnLiveIconUpdateListener
        public void onLiveIconUpdate(final LiveIcon icon) {
            new Handler(LauncherModel.getWorkerLooper()).post(new Runnable() { // from class: com.lge.launcher3.LauncherExtension.19.1
                @Override // java.lang.Runnable
                public void run() {
                    LauncherAppState launcherAppState = LauncherAppState.getInstance(LauncherExtension.this);
                    Context context = launcherAppState.getContext();
                    IconCache iconCache = launcherAppState.getIconCache();
                    ComponentName componentName = icon.getComponentName();
                    String packageName = componentName.getPackageName();
                    for (UserHandle userHandle : UserManagerCompat.getInstance(context).getUserProfiles()) {
                        iconCache.removeIcon(componentName, userHandle);
                        launcherAppState.getModel().onLiveIconUpdated(packageName, userHandle);
                    }
                    if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || LauncherExtension.this.mCarouselLayout == null) {
                        return;
                    }
                    LauncherExtension.this.mCarouselLayout.updateLiveIcon(componentName);
                }
            });
        }
    };

    @Override // com.android.launcher3.uioverrides.QuickstepLauncher, com.android.launcher3.BaseQuickstepLauncher, com.android.launcher3.Launcher, com.android.launcher3.BaseDraggingActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        String str = TAG;
        LGLog.i(str, "Launcher.onCreate");
        SystemClockUtils.startElapsedTime(1);
        SystemClockUtils.startElapsedTime(2);
        EventLogger.initPackageVersion(getBaseContext());
        EventLogger.initStaticValues();
        LGHiddenMenuUtil.reLoadingFeature(this, LGHomeFeature.getInstance());
        if (LGFeatureConfig.sDebugPorfileStatup) {
            Debug.startMethodTracing(getFilesDir().getPath() + "/startupTrace", 104857600);
            Alarm alarm = new Alarm();
            alarm.setAlarm(5000L);
            alarm.setOnAlarmListener(new OnAlarmListener() { // from class: com.lge.launcher3.LauncherExtension.1
                @Override // com.android.launcher3.OnAlarmListener
                public void onAlarm(Alarm alarm2) {
                    Debug.stopMethodTracing();
                }
            });
        }
        CPUBoostService.boostUp(getBaseContext());
        this.mOrientationLockCount = 0;
        setLauncherCallbacks(new LauncherCallbacksImpl());
        ConciergeBoardMngr.init(this);
        Operator.setup(this);
        AppNotifierManager.destoryInstance();
        super.onCreate(savedInstanceState);
        ConciergeBoardNotificationReceiver.registerReceiver(this);
        ConciergeBoardNotificationReceiver.getInstance().addWorkSpaceMoveInterface(new ConciergeBoardNotificationReceiver.IWorkspaceMove() { // from class: com.lge.launcher3.LauncherExtension.2
            @Override // com.lge.launcher3.concierge.ConciergeBoardNotificationReceiver.IWorkspaceMove
            public void gotoConcirergeBoard() {
                int iIsExistConciergeBoardScreenInDatabase;
                if (LauncherExtension.this.getWorkspace() == null || !LauncherExtension.this.isInState(LauncherState.NORMAL) || (iIsExistConciergeBoardScreenInDatabase = ConciergeBoardNotificationReceiver.isExistConciergeBoardScreenInDatabase(LauncherExtension.this)) == -1) {
                    return;
                }
                LauncherExtension.this.mPendingMoveScreenIndex = iIsExistConciergeBoardScreenInDatabase;
            }
        });
        initTouchEventFilter();
        PackageUtils.setPrefHomeSetting(getApplicationContext(), getPackageName());
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        this.mDefaultKeySsb = spannableStringBuilder;
        Selection.setSelection(spannableStringBuilder, 0);
        registerIconChangeListObserver();
        LiveIconManager.getInstance(getBaseContext()).registerOnLiveIconUpdateListener(this.mLauncherListener);
        if (LGFeatureConfig.sDebugMemoryTracking) {
            this.mWeightWatcher = new WeightWatcher(this);
            this.mWeightWatcher.setAlpha(0.5f);
            getRootView().addView(this.mWeightWatcher, new FrameLayout.LayoutParams(-1, -2, 80));
            this.mWeightWatcher.setVisibility(0);
        }
        if (LGHomeFeature.Config.FEATURE_USE_SKT_PHONE_MODE.getValue()) {
            this.mTPMR = new TPhoneModeReceiver();
            this.mTPMR.registerReceiver(getApplicationContext());
        }
        SelectedHomeObserver selectedHomeObserver = new SelectedHomeObserver(this, new Handler());
        this.mSelectedHomeObserver = selectedHomeObserver;
        selectedHomeObserver.registerObserver(getApplicationContext());
        AppDrawerButtonObserver appDrawerButtonObserver = new AppDrawerButtonObserver(this, new Handler());
        this.mAppDrawerButtonObserver = appDrawerButtonObserver;
        appDrawerButtonObserver.registerObserver(getApplicationContext());
        if (EventLogger.VERBOSE) {
            long jEndElapsedTime = SystemClockUtils.endElapsedTime(1);
            LGLog.i(str, "Elapsed time for Launcher.onCreate: " + jEndElapsedTime + "ms");
            EventLogger.sElapsedForLauncherCreation = String.valueOf(jEndElapsedTime);
        }
        if (LGHomeFeature.Config.FEATURE_USE_WALLPAPER_MOTION.getValue()) {
            this.mWallpaperMotionManager = new WallpaperMotionManager(getApplicationContext(), getRootView());
        }
        if (LGHomeFeature.Config.FEATURE_EDITMODE_LONGPRESS_DELAY.getValue()) {
            this.mEditModeOffManager = new EditModeOffManager(getApplicationContext());
        }
        if (getApplicationContext().getResources().getBoolean(33947702)) {
            this.mBurnInProtectionHelper = new BurnInProtectionHelper(getApplicationContext(), -4, 4, -4, 4, 6, getDragLayer());
        }
        Utilities.writeInfoToSysFS(getApplicationContext(), this.mDeviceProfile.iconSizePx, this.mWorkspace.getTouchSlop());
        if (LGHomeFeature.Config.FEATURE_KT_GIFTBOX_DATA_FREE.getValue()) {
            GiftBoxManager giftBoxManager = new GiftBoxManager(getApplicationContext(), this);
            this.mGiftBoxManager = giftBoxManager;
            giftBoxManager.registerNetworkCallback(getApplicationContext());
            Utilities.setDataFreeApps(getApplicationContext());
            GiftBoxObserver giftBoxObserver = new GiftBoxObserver(this, new Handler());
            this.mGiftBoxObserver = giftBoxObserver;
            giftBoxObserver.registerObserver(this);
        }
        com.android.launcher3.Utilities.checkDefineValuesForSwipeDownHome(getApplicationContext());
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            LGLog.d(str, "initialize Swivel components ");
            this.mPostureManager = (PostureManager) new LGContext(this).getLGSystemService("postureservice");
            registerPostureStateCallback();
            registerDateChangedReceiver();
            registerWallpaperChangedReceiver();
            initializeSwivelHomeView();
            mDeleteItemHander = new Handler() { // from class: com.lge.launcher3.LauncherExtension.3
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    if (LauncherExtension.this.getCarouselLayout() != null) {
                        Bundle data = msg.getData();
                        LauncherExtension.this.getCarouselLayout().getAdapter().onItemRemove(data.getString(LauncherConst.EXTRA_PACKAGE_NAME), data.getString(LauncherConst.EXTRA_CLASS_NAME));
                    }
                }
            };
            mInstallQueueSwivelHander = new Handler() { // from class: com.lge.launcher3.LauncherExtension.4
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    if (msg.what == 2 && LauncherExtension.mInstallQueueApps != null) {
                        LauncherExtension.this.getCarouselLayout().addAppsAutomatically(LauncherExtension.mInstallQueueApps);
                        LauncherExtension.mInstallQueueApps = null;
                    }
                }
            };
            WallpaperColorInfoUtil.getInstance(getApplicationContext()).addOnChangeListener(this);
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR.getValue()) {
            LiveWallpaperColorObserver liveWallpaperColorObserver = new LiveWallpaperColorObserver(getApplicationContext(), new Handler());
            this.mLiveWallpaperColorObserver = liveWallpaperColorObserver;
            liveWallpaperColorObserver.registerObserver(getApplicationContext());
        }
    }

    public void initSwivelItems() {
        if (this.mCarouselLayout != null) {
            this.mAppList = LauncherModel.getAppListForSwivelHome(this);
            this.mCarouselLayout.setAppList(this.mAppList);
            validationSwivelItems();
            this.mCarouselLayout.setItemClickListener(new CarouselView.OnItemClickListener() { // from class: com.lge.launcher3.-$$Lambda$LauncherExtension$pLMKmfREom-tFUcCBR-p9a_xFg4
                @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.OnItemClickListener
                public final void onItemClick(RecyclerView.Adapter adapter, View view, int i, int i2) {
                    this.f$0.lambda$initSwivelItems$0$LauncherExtension(adapter, view, i, i2);
                }
            });
            this.mCarouselLayout.initIndex();
            SwivelAppIconCache.getInstance(this).fillCache(this.mAppList);
        }
    }

    public /* synthetic */ void lambda$initSwivelItems$0$LauncherExtension(RecyclerView.Adapter adapter, View view, int i, int i2) {
        onClick(view);
    }

    public void clearSwivelItems() {
        SwivelAppIconCache.getInstance(this).clearCache();
    }

    private void validationSwivelItems() {
        for (ShortcutInfo shortcutInfo : this.mAppList) {
            if (!PackageUtils.isPackageInstalled(this, shortcutInfo.getTargetComponent().getPackageName()) && !shortcutInfo.isPromise()) {
                LGLog.i(TAG, "Skip to add it since it is already deleted, so remove it from the database : " + ((Object) shortcutInfo.title));
                getCarouselLayout().getAdapter().onItemRemove(shortcutInfo);
            }
        }
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.LauncherModel.Callbacks
    public void bindAllApplications(ArrayList<AppInfo> apps) {
        super.bindAllApplications(apps);
    }

    private void registerPostureStateCallback() {
        this.mPostureManager.registerPostureStateCallback(this.mPostureStateCallback);
    }

    private void unRegisterPostureStateCallback() {
        this.mPostureManager.unregisterPostureStateCallback(this.mPostureStateCallback);
    }

    private void registerDateChangedReceiver() {
        if (this.mDateChangedReceiverRegistered) {
            return;
        }
        LGLog.i(TAG, "Register date changed receiver");
        registerReceiver(this.sDateChangedReceiver, new IntentFilter("android.intent.action.DATE_CHANGED"));
        this.mDateChangedReceiverRegistered = true;
    }

    private void unregisterDateChangedReceiver() {
        if (this.mDateChangedReceiverRegistered) {
            unregisterReceiver(this.sDateChangedReceiver);
            this.mDateChangedReceiverRegistered = false;
        }
    }

    private void registerWallpaperChangedReceiver() {
        if (this.mWallpaperChangedReceiverRegistered) {
            return;
        }
        LGLog.i(TAG, "Register wallpaper changed receiver");
        registerReceiver(this.sWallpaperChangedReceiver, new IntentFilter(Utilities.ACTION_WALLPAPER_CHANGED));
        this.mWallpaperChangedReceiverRegistered = true;
    }

    private void unregisterWallpaperChangedReceiver() {
        if (this.mWallpaperChangedReceiverRegistered) {
            unregisterReceiver(this.sWallpaperChangedReceiver);
            this.mWallpaperChangedReceiverRegistered = false;
        }
    }

    private void initializeSwivelHomeView() {
        this.videoSwitch = (Switch) findViewById(R.id.video_switch);
        this.mSwivelContentsView = (SwivelContentsView) findViewById(R.id.Swivel_contents);
        this.mSwivelContentsView.setOnLongClickListener(this);
        this.mSwivelContentsView.setOnClickListener(this);
        this.mSwivelContentsView.setVisibility(8);
        this.mCarouselLayout = (CarouselLayout) findViewById(R.id.carousel_layout);
    }

    public void showSwivelHomeView() {
        if (this.mIsSwivelHomeShown) {
            LGLog.i(TAG, "Swivel home is already shown");
            getRotationHelper().setCurrentStateRequest(1);
            return;
        }
        getRotationHelper().setCurrentStateRequest(1);
        this.mIsSwivelHomeShown = true;
        getStateManager().goToState(LauncherState.NORMAL, false);
        this.mStartSwivelHomeTime = System.currentTimeMillis();
        controlStatusBar();
        if (getWorkspace() != null) {
            this.mOverlayManager.hideOverlay(false);
            if (getWorkspace().getState() != Workspace.State.NORMAL) {
                showWorkspace(false);
            }
        }
        setWorkspaceAndHotseatVisibility(8, "showSwivelHomeView");
        if (this.mCarouselLayout == null || getDragController() == null) {
            LGLog.i(TAG, "mCarouselLayout : " + this.mCarouselLayout + ", getDragController() : " + getDragController());
            return;
        }
        this.mCarouselLayout.getSwivelWeatherView().setVisibility(0);
        this.mCarouselLayout.setVisibility(0);
        this.mCarouselLayout.registerDragController(getDragController());
        this.mCarouselLayout.initIndex();
        findViewById(R.id.carousel_bg).setVisibility(0);
        if (this.videoSwitch.isChecked() && this.mSwivelContentsView != null && !this.mSwivelContentsView.getVideoView().isPlaying()) {
            this.mSwivelContentsView.setVisibility(0);
            this.mSwivelContentsView.getVideoView().start();
            this.mSwivelContentsView.getVideoView().seekTo(this.mSwivelContentsView.getCurrentPosition());
        }
        setOverviewButtonsEnable(true);
        unblockAndFlushInstallQueueSwivel();
        this.mSwivelHomeStateHandler.post(new Runnable() { // from class: com.lge.launcher3.-$$Lambda$LauncherExtension$lATwTH1gnPcnQVNZOgLqd1JXRak
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showSwivelHomeView$1$LauncherExtension();
            }
        });
        if (this.isTopActivity) {
            Executors.MAIN_EXECUTOR.postDelayed(new Runnable() { // from class: com.lge.launcher3.-$$Lambda$LauncherExtension$Xp3S2l7wPg8jyv4npNykVoz5FCs
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showSwivelHomeView$2$LauncherExtension();
                }
            }, 400);
        }
        this.mWorkspace.updateWallpaperState();
        if (this.mWallpaperMotionManager != null) {
            this.mWallpaperMotionManager.resetRootViewTranslation();
            if (this.mWallpaperMotionManager.isRunning()) {
                this.mWallpaperMotionManager.end();
            }
        }
    }

    public /* synthetic */ void lambda$showSwivelHomeView$1$LauncherExtension() {
        this.mCarouselLayout.updateWeatheInformation();
        AdaptiveTextUtil.adaptiveStatusBar(getWorkspace(), AdaptiveTextUtil.getAdaptiveStatusBarColor(getApplicationContext()));
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$showSwivelHomeView$2$LauncherExtension()V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: showSwivelHomeGuide, reason: merged with bridge method [inline-methods] */
    public void lambda$showSwivelHomeView$2$LauncherExtension() {
        SwivelHomeGuideManager.getInstance(getApplicationContext()).showGuide(this);
    }

    public void showOriginalHomeView(boolean needRebind) {
        if (!this.mIsSwivelHomeShown) {
            LGLog.i(TAG, "Original home is already shown");
            getRotationHelper().setCurrentStateRequest(0);
            return;
        }
        if (getResources().getConfiguration().orientation != 1) {
            this.mShowOriginalHomeView = new Runnable() { // from class: com.lge.launcher3.-$$Lambda$LauncherExtension$n6gYtOuoU0-i7Xgu3xfNo0oGovs
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showOriginalHomeView$3$LauncherExtension();
                }
            };
            return;
        }
        LGLog.i(TAG, "showOriginalHomeView : " + needRebind);
        this.mIsSwivelHomeShown = false;
        getRotationHelper().setCurrentStateRequest(0);
        if (needRebind) {
            forceOnIdpChanged(this.mDeviceProfile.inv);
        }
        this.mShowOriginalHomeView = null;
        getStateManager().goToState(LauncherState.NORMAL, false);
        controlStatusBar();
        if (this.mSwivelContentsView != null && this.mSwivelContentsView.getVideoView().canPause()) {
            this.mSwivelContentsView.getVideoView().pause();
            this.mSwivelContentsView.setCurrentPosition(this.mSwivelContentsView.getVideoView().getCurrentPosition());
            this.mSwivelContentsView.setVisibility(8);
        }
        if (this.mCarouselLayout != null) {
            this.mCarouselLayout.unregisterDragController();
            this.mCarouselLayout.getSwivelWeatherView().setVisibility(8);
            this.mCarouselLayout.setVisibility(8);
            this.mCarouselLayout.resetLayoutPosition();
            this.videoSwitch.setVisibility(8);
            findViewById(R.id.carousel_bg).setVisibility(8);
            setWorkspaceAndHotseatVisibility(0, "showOriginalHomeView");
        }
        setOverviewButtonsEnable(false);
        this.mSwivelHomeStateHandler.post(new Runnable() { // from class: com.lge.launcher3.-$$Lambda$LauncherExtension$t34_d25cbj5vQnNwizM5p6a-Hzo
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showOriginalHomeView$4$LauncherExtension();
            }
        });
        this.mWorkspace.updateWallpaperState();
        if (this.mWallpaperMotionManager != null) {
            this.mWallpaperMotionManager.start();
            if (getState().useMotion) {
                return;
            }
            this.mWallpaperMotionManager.setEnableParallax(false);
        }
    }

    public /* synthetic */ void lambda$showOriginalHomeView$3$LauncherExtension() {
        LGLog.d(TAG, "mShowOriginalHomeView: showOriginalHomeView");
        showOriginalHomeView(false);
    }

    public /* synthetic */ void lambda$showOriginalHomeView$4$LauncherExtension() {
        AdaptiveTextUtil.adaptiveStatusBar(getWorkspace(), AdaptiveTextUtil.getAdaptiveStatusBarColor(getApplicationContext()));
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    public void controlStatusBar() {
        int i = getResources().getConfiguration().orientation;
        try {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && i == 2 && !this.mDeviceProfile.isMultiWindowMode) {
                insetsController.hide(WindowInsets.Type.statusBars());
            } else {
                insetsController.show(WindowInsets.Type.statusBars());
            }
            insetsController.setSystemBarsBehavior(2);
        } catch (Exception unused) {
            LGLog.i(TAG, "decor view is not attached yet.");
        }
    }

    private void setOverviewButtonsEnable(boolean isSwivelHome) {
        ((ViewGroup) getWidgetsButton().getParent()).setVisibility(isSwivelHome ? 8 : 0);
        ((ViewGroup) findViewById(R.id.dynamic_gird_button).getParent()).setVisibility(isSwivelHome ? 8 : 0);
        ((ViewGroup) findViewById(LGFeatureConfig.FEATURE_OPERATOR.equals("VZW") ? R.id.settings_button_vzw : R.id.settings_button).getParent()).setVisibility(isSwivelHome ? 8 : 0);
        View viewFindViewById = findViewById(R.id.swivel_home_settings_button);
        ((TextView) viewFindViewById).setText(String.format(getString(R.string.swivel_home_settings), getString(R.string.sp_swivel_homescreen_category_NORMAL)));
        ((ViewGroup) viewFindViewById.getParent()).setVisibility(isSwivelHome ? 0 : 8);
        viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.LauncherExtension.9
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                if (LauncherExtension.this.mWorkspace.isSwitchingState()) {
                    return;
                }
                LauncherExtension.this.onClickSwivelSettingsButton(arg0);
            }
        });
    }

    private void setSwivelHomeOrientation() {
        WindowManager windowManager = (WindowManager) getSystemService("window");
        if (windowManager == null) {
            LGLog.i(TAG, "Window Manager is null, force landscape mode");
            setRequestedOrientation(8);
            return;
        }
        int rotation = windowManager.getDefaultDisplay().getRotation();
        if (rotation == 0) {
            LGLog.i(TAG, "rotation from 0");
            setRequestedOrientation(8);
            return;
        }
        if (rotation == 2) {
            LGLog.i(TAG, "rotation from 180");
            setRequestedOrientation(0);
        } else if (rotation == 1) {
            LGLog.i(TAG, "rotation from 90");
            setRequestedOrientation(0);
        } else if (rotation == 3) {
            LGLog.i(TAG, "rotation from 270");
            setRequestedOrientation(8);
        } else {
            LGLog.i(TAG, "something wrong");
        }
    }

    @Override // com.android.launcher3.uioverrides.QuickstepLauncher, com.android.launcher3.Launcher, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        TimeChecker.getInstance().start("onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            String str = TAG;
            LGLog.i(str, "onConfigurationChanged : " + newConfig.orientation);
            if (newConfig.orientation == 1) {
                controlStatusBar();
                LGLog.i(str, "portrait mode / swivel home : " + this.mIsSwivelHome + " / " + this.mIsSwivelHomeShown);
                if (!this.mIsSwivelHome && this.mIsSwivelHomeShown) {
                    showOriginalHomeView(false);
                }
            } else if (newConfig.orientation == 2) {
                controlStatusBar();
                LGLog.i(str, "landscape mode / swivel home : " + this.mIsSwivelHome + " / " + this.mIsSwivelHomeShown);
                if (this.mIsSwivelHome && !this.mIsSwivelHomeShown) {
                    showSwivelHomeView();
                }
            }
            if (this.mCarouselLayout != null) {
                this.mCarouselLayout.setCarouselStartHeight(newConfig.orientation);
            }
        }
        MultiWindowGuideManager.getInstance(getApplicationContext()).onConfigurationChanged();
        TimeChecker.getInstance().endAndResult("onConfigurationChanged");
    }

    @Override // com.android.launcher3.Launcher, android.app.Activity
    public void onTopResumedActivityChanged(boolean isTopResumedActivity) {
        super.onTopResumedActivityChanged(isTopResumedActivity);
        this.isTopActivity = isTopResumedActivity;
        PendingIntentReceiver.disableAndFlushQueue(getApplicationContext(), true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reloadLauncherIfNeeded() {
        int i;
        if (SharedPreferencesManager.getBoolean(this, 0, SharedPreferencesConst.BackupRestoreKey.ISRESTORING, false)) {
            return;
        }
        int defaultHome = LGHomeFeature.getDefaultHome(this);
        Bundle bundleCall = LauncherSettings.Settings.call(getContentResolver(), LauncherSettings.Settings.METHOD_GET_DB_NAME);
        String string = LauncherFiles.LAUNCHER_UNKNOWN_DB;
        if (bundleCall != null) {
            string = bundleCall.getString("value", LauncherFiles.LAUNCHER_UNKNOWN_DB);
        }
        if (LauncherFiles.LAUNCHER_DB.equals(string)) {
            i = 0;
        } else if (LauncherFiles.LAUNCHER_ALLAPPS_DB.equals(string)) {
            i = 1;
        } else if (LauncherFiles.LAUNCHER_EASYHOME_DB.equals(string)) {
            i = 2;
        } else {
            LGLog.i(TAG, "reloadLauncherIfNeeded: currentDBName = " + string);
            i = -1;
        }
        String str = TAG;
        LGLog.i(str, "reloadLauncherIfNeeded: old = " + i + ", new = " + defaultHome + ", DB = " + string);
        if (i == -1 || i == defaultHome) {
            return;
        }
        LGHomeFeature.updateDefaultHome(this, defaultHome);
        SettingsSearchUtils.updateSettingSearchDB(this, LGHomeFeature.isEnableDefaultHome(), true, false);
        LGLog.i(str, "reloadLauncherIfNeeded: Restart Home by changing default home: old = " + i + ", new = " + defaultHome);
        Process.killProcess(Process.myPid());
    }

    private void registerIconChangeListObserver() {
        getSharedPreferences(LauncherConst.CUSTOMIZE_APPICONS_SHARED_PREF_NAME, 0).registerOnSharedPreferenceChangeListener(this.mIconChangeListObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterIconChangeListObserver() {
        getSharedPreferences(LauncherConst.CUSTOMIZE_APPICONS_SHARED_PREF_NAME, 0).unregisterOnSharedPreferenceChangeListener(this.mIconChangeListObserver);
    }

    private void initTouchEventFilter() {
        try {
            this.mPinchDecision = new PinchDecision(getApplicationContext());
            TouchEventFilter touchEventFilter = new TouchEventFilter();
            this.mTouchEventFilter = touchEventFilter;
            touchEventFilter.convertId(true);
            this.mTouchEventFilter.addTouchEventFilter(new ActiveFilter(getApplicationContext()));
        } catch (NoClassDefFoundError unused) {
            this.mPinchDecision = null;
            this.mTouchEventFilter = null;
        }
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.LauncherModel.Callbacks
    public void startBinding(int loadFlags) {
        ConciergeBoardMngr.onStartBiding();
        if ((loadFlags & 4) != 0 && this.mWorkspace != null) {
            LGLog.d(TAG, "startBinding(): setCurrentPage 0");
            this.mWorkspace.setCurrentPage(0);
        }
        super.startBinding(loadFlags);
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.LauncherModel.Callbacks
    public void bindScreens(ArrayList<Long> arrayList) {
        super.bindScreens(arrayList);
        this.mWorkspace.initDefaultScreenId();
        int defaultPageFromDatabase = LauncherModel.getDefaultPageFromDatabase(getApplicationContext());
        this.mWorkspace.setDefaultPage((this.mWorkspace.hasCustomContent() ? 1 : 0) + defaultPageFromDatabase);
        if (Utilities.isLGUI7_1()) {
            this.mWorkspace.setDefaultHomeSelected(defaultPageFromDatabase + (this.mWorkspace.hasCustomContent() ? 1 : 0));
        }
        if (this.mWorkspace.isInOverviewMode()) {
            this.mWorkspace.setDefaultPageBackground(true);
        }
        if (this.mWorkspace.getLauncher().getStateManager().getState() == LauncherState.WIDGETS) {
            this.mWorkspace.setVisibility(4);
            this.mWorkspace.getPageIndicator().setVisibility(4);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        LGLog.d("CreatePackageContext", String.format("Package Name(%s), Flags(%d)", packageName, Integer.valueOf(flags)));
        if (LgeWidgetContext.isLGEAppWidgetPackage(packageName)) {
            return new LgeWidgetContext(super.createPackageContext(packageName, 3));
        }
        return super.createPackageContext(packageName, flags);
    }

    public Context createPackageContextAsUser(String packageName, int flags, UserHandle user) throws PackageManager.NameNotFoundException {
        if (LgeWidgetContext.isLGEAppWidgetPackage(packageName)) {
            return new LgeWidgetContext(super.createPackageContextAsUser(packageName, 3, user));
        }
        return super.createPackageContextAsUser(packageName, flags, user);
    }

    @Override // com.android.launcher3.Launcher, android.app.Activity
    public void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch) {
        if (isInState(LauncherState.CLEAN_VIEW)) {
            exitCleanViewMode();
        }
        showWorkspace(true);
        if (initialQuery == null) {
            initialQuery = getTypedText();
        }
        String str = initialQuery;
        if (appSearchData == null) {
            appSearchData = new Bundle();
            appSearchData.putString("source", "launcher-search");
        }
        Bundle bundle = appSearchData;
        SearchManager searchManager = (SearchManager) getSystemService("search");
        if (searchManager != null) {
            searchManager.startSearch(str, selectInitialQuery, getComponentName(), bundle, globalSearch);
        }
    }

    private String getTypedText() {
        return this.mDefaultKeySsb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearTypedText() {
        this.mDefaultKeySsb.clear();
        this.mDefaultKeySsb.clearSpans();
        Selection.setSelection(this.mDefaultKeySsb, 0);
    }

    @Override // com.android.launcher3.Launcher
    public void lockScreenOrientation() {
        int i = this.mOrientationLockCount + 1;
        this.mOrientationLockCount = i;
        LGLog.i("lockScreenOrientation", "mOrientationLockCount = " + i);
        super.lockScreenOrientation();
    }

    @Override // com.android.launcher3.Launcher
    public void unlockScreenOrientation(boolean immediate) {
        int i = this.mOrientationLockCount - 1;
        this.mOrientationLockCount = i;
        LGLog.i("unlockScreenOrientation", "mOrientationLockCount = " + i);
        if (this.mOrientationLockCount <= 0) {
            this.mOrientationLockCount = 0;
            super.unlockScreenOrientation(immediate);
        }
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.LauncherModel.Callbacks
    public void bindAppWidget(LauncherAppWidgetInfo item) {
        super.bindAppWidget(item);
        if (EventLogger.VERBOSE && EventLogger.sElapsedForLauncherBinding == null) {
            EventLogger.sElapsedForLauncherBinding = String.valueOf(SystemClockUtils.endElapsedTime(2));
        }
    }

    public static void onItemRemove(String packageName, String className) {
        Handler handler = mDeleteItemHander;
        if (handler != null) {
            Message messageObtainMessage = handler.obtainMessage(0);
            Bundle bundle = new Bundle();
            bundle.putString(LauncherConst.EXTRA_PACKAGE_NAME, packageName);
            bundle.putString(LauncherConst.EXTRA_CLASS_NAME, className);
            messageObtainMessage.setData(bundle);
            mDeleteItemHander.sendMessage(messageObtainMessage);
        }
    }

    @Override // com.lge.launcher3.adaptive.WallpaperColorInfoUtil.OnSwivelWallpaperChangeListener
    public void onSwivelWallpaperChanged() {
        LGLog.d(TAG, "onSwivelWallpaperChanged");
        if (this.mWorkspace != null) {
            this.mWorkspace.post(new Runnable() { // from class: com.lge.launcher3.-$$Lambda$LauncherExtension$NOTEXwnsIdhOqxxfEtMkvAOMxa8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onSwivelWallpaperChanged$5$LauncherExtension();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onSwivelWallpaperChanged$5$LauncherExtension() {
        int adaptiveStatusBarColor = AdaptiveTextUtil.getAdaptiveStatusBarColor(getApplicationContext());
        LGLog.d(TAG, "onSwivelWallpaperChanged : adaptiveStatusBar. " + adaptiveStatusBarColor);
        AdaptiveTextUtil.adaptiveStatusBar(this.mWorkspace, adaptiveStatusBarColor);
    }

    public class LauncherCallbacksImpl implements LauncherCallbacks, DeviceProfile.OnDeviceProfileChangeListener {
        private LauncherClient mLauncherClient;
        private Launcher.LauncherOverlayCallbacks mLauncherOverlayCallbacks;
        private float mOverlayProgress;
        private boolean mSideScreenAttached;
        private final String TAG = LauncherCallbacksImpl.class.getSimpleName();
        LauncherClientCallbacksImpl mLauncherOverlay = new LauncherClientCallbacksImpl();
        private EvieApiCallbacks mEvieCallbacks = new EvieApiCallbacks() { // from class: com.lge.launcher3.LauncherExtension.LauncherCallbacksImpl.5
            @Override // com.evie.screen.api.EvieApiCallbacks
            public void onScrollUpdated(final float progress) {
                if (LauncherCallbacksImpl.this.mLauncherOverlayCallbacks != null) {
                    LauncherCallbacksImpl.this.mLauncherOverlayCallbacks.onScrollChanged(progress);
                }
            }

            @Override // com.evie.screen.api.EvieApiCallbacks
            public void onServiceStateChanged(final boolean sideScreenAttached) {
                LauncherCallbacksImpl.this.mSideScreenAttached = sideScreenAttached;
                VZWSideScreenManager.setServiceAttached(sideScreenAttached);
                if (LauncherCallbacksImpl.this.mSideScreenAttached || LauncherCallbacksImpl.this.mLauncherOverlayCallbacks == null) {
                    return;
                }
                LauncherCallbacksImpl.this.mLauncherOverlayCallbacks.onScrollChanged(0.0f);
            }
        };

        @Override // com.android.launcher3.LauncherCallbacks
        public AllAppsSearchBarController getAllAppsSearchBarController() {
            return null;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public Intent getFirstRunActivity() {
            return null;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public View getIntroScreen() {
            return null;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public View getQsbBar() {
            return null;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean hasDismissableIntroScreen() {
            return false;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean hasFirstRunActivity() {
            return false;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean isLauncherPreinstalled() {
            return false;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onClickAddWidgetButton(View v) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onClickAppShortcut(View v) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onClickFolderIcon(View v) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onClickPagedViewIcon(View v) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onClickWallpaperPicker(View v) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onDragStarted(View view) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onHomeIntent(boolean internalStateHandled) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onLauncherProviderChange() {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onPostCreate(Bundle savedInstanceState) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onSaveInstanceState(Bundle outState) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onTrimMemory(int level) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onWindowFocusChanged(boolean hasFocus) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onWorkspaceLockedChanged() {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean overrideWallpaperDimensions() {
            return true;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean providesSearch() {
            return true;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void setLauncherSearchCallback(Object callbacks) {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, Rect sourceBounds) {
            return false;
        }

        public LauncherCallbacksImpl() {
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void preOnCreate() {
            LauncherAppState.getInstance(LauncherExtension.this).setHideAppsCount(-1);
            LauncherExtension.this.reloadLauncherIfNeeded();
            LauncherExtension launcherExtension = LauncherExtension.this;
            launcherExtension.registerReceiver(launcherExtension.mWallpaperChangeReceiver, new IntentFilter(Utilities.ACTION_WALLPAPER_CHANGED));
            PendingIntentReceiver.registerReceiver(LauncherExtension.this);
            DDTChangeWatcher.getInstance().addListener(LauncherExtension.this);
            ScreenZoomChangeWatcher.getInstance().addListener(LauncherExtension.this);
            ScreenZoomChangeWatcher.getInstance().checkScreenZoomChangedOnCreate(LauncherExtension.this);
            DuplicatedApplicationChecker.init();
            LauncherExtension.this.mSwipeUpGuideAnimation = new SwipeUpGuideAnimation(LauncherExtension.this);
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onCreate(Bundle savedInstanceState) {
            if (LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue()) {
                VZWSideScreenManager.setAppEnabled(LauncherExtension.this.getPackageManager());
                if (!VZWSideScreenManager.isAppEnabled()) {
                    HomeSettingsSharedPreferences.setVZWSideScreenEnabled(LauncherExtension.this.getApplicationContext(), false);
                }
                LGLog.d(this.TAG, "register VZWSideScreenObserver");
                SharedPreferencesManager.registerOnSharedPreferenceChangeListener(LauncherExtension.this.getApplicationContext(), 0, LauncherExtension.this.mVZWSideScreenObserver);
            }
            if (LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue()) {
                GoogleNowManager.setAppEnabled(LauncherExtension.this.getPackageManager());
                if (!GoogleNowManager.isAppEnabled()) {
                    HomeSettingsSharedPreferences.setGoogleNowEnabled(LauncherExtension.this.getApplicationContext(), false);
                }
            }
            boolean zIsAvailable = GoogleNowManager.isAvailable(LauncherExtension.this.getApplicationContext());
            boolean z = LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue() && LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS_PREWARM.getValue();
            boolean value = LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_HOTWORD.getValue();
            boolean vZWSideScreenEnabled = HomeSettingsSharedPreferences.getVZWSideScreenEnabled(LauncherExtension.this.getApplicationContext());
            if (zIsAvailable || z || value) {
                LauncherExtension.this.mStatusOfClientOptions = zIsAvailable;
                this.mLauncherClient = new LauncherClient(LauncherExtension.this, this.mLauncherOverlay, new LauncherClient.ClientOptions(zIsAvailable, value, z));
                if (value) {
                    LauncherExtension.this.mHotword.setLauncherClient(this.mLauncherClient);
                }
            }
            if (!zIsAvailable && vZWSideScreenEnabled) {
                initEvieApi();
            }
            if (((zIsAvailable && GoogleNowManager.isAppEnabled()) || (vZWSideScreenEnabled && VZWSideScreenManager.isAppEnabled())) && HomeSettingsSharedPreferences.getContinuousLoopEnabled(LauncherExtension.this.getApplicationContext())) {
                HomeSettingsSharedPreferences.setContinuousLoopEnabled(LauncherExtension.this.getApplicationContext(), false);
            }
            LauncherExtension.this.registerMDMPolicyReceiver();
            LauncherExtension.this.registerBubbleMessageExpandReceiver();
            LauncherExtension.this.inflateLGDebugModeView();
            LauncherExtension.this.addOnDeviceProfileChangeListener(this);
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onAttachedToWindow() {
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.onAttachedToWindow();
            }
            if (LauncherExtension.this.mWallpaperMotionManager != null) {
                LauncherExtension.this.mWallpaperMotionManager.setWindowToken(LauncherExtension.this.getWindow().getDecorView().getWindowToken());
            }
            if (LauncherExtension.this.mEvieApi != null) {
                LauncherExtension.this.mEvieApi.onAttachedToWindow();
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onDetachedFromWindow() {
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.onDetachedFromWindow();
            }
            if (LauncherExtension.this.mEvieApi != null) {
                LauncherExtension.this.mEvieApi.updateScroll(0.0f);
                LauncherExtension.this.mEvieApi.onDetachedFromWindow();
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void preOnResume() {
            LauncherExtension.this.invalidateHasCustomContentToLeft();
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onResume() {
            LGLog.i(this.TAG, "Launcher.onResume // " + EventLogger.sPackageVersion + " this:" + this + " / swivel : " + LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue());
            LauncherExtension.this.mOrientationLockCount = 0;
            LauncherExtension.this.enableRecentlyUninstall();
            LauncherExtension.this.clearTypedText();
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.onResume();
            }
            if (LauncherExtension.this.mEvieApi != null) {
                LauncherExtension.this.mEvieApi.onResume();
            }
            if (LauncherExtension.this.mStarted) {
                LauncherExtension.this.mAlreadyOnHome = true;
            }
            PendingIntentReceiver.disableAndFlushQueue(LauncherExtension.this, false);
            ScreenEffectManager.getInstance(LauncherExtension.this.getBaseContext()).updateSelectedScreenEffectType();
            LoopNormalModeManager.getInstance(LauncherExtension.this.getBaseContext()).updateFeatureEnabled();
            LiveIconManager.getInstance(LauncherExtension.this.getBaseContext()).start();
            if (LauncherExtension.this.mWorkspace != null) {
                CellLayout cellLayout = (CellLayout) LauncherExtension.this.mWorkspace.getChildAt(LauncherExtension.this.mWorkspace.getCurrentPage());
                if (cellLayout != null && !UninstallModeManager.getInstance(LauncherExtension.this.getBaseContext()).isInUninstallMode()) {
                    cellLayout.setCrosshairsVisibility(0.0f);
                }
                LauncherExtension.this.mWorkspace.setInAppsEnabled(HomeSettingsSharedPreferences.getGoogleInAppsEnabled(LauncherExtension.this.getApplicationContext()) || HomeSettingsSharedPreferences.getABBASearchEnabled(LauncherExtension.this.getApplicationContext()));
                if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && LauncherExtension.this.mWorkspace.getPageIndicator() != null) {
                    LauncherExtension.this.mWorkspace.getPageIndicator().setVisibility(8);
                }
            }
            if (UninstallModeManager.getInstance(LauncherExtension.this.getBaseContext()).isInUninstallMode()) {
                UninstallModeManager.getInstance(LauncherExtension.this.getBaseContext()).runUninstallBadgeAnimation(true, 0);
            }
            if (!LGHomeFeature.isDisableAllApps() && LauncherExtension.this.getHotseat() != null && LauncherExtension.this.getAllAppsButton() != null && HomeSettingsSharedPreferences.getEnableAppDrawerButton(LauncherExtension.this.getApplicationContext()) != LauncherExtension.this.getHotseat().getEnableAppDrawerButton()) {
                LauncherExtension.this.getHotseat().getLayout().removeView(LauncherExtension.this.getAllAppsButton());
                LauncherExtension.this.getHotseat().setupAllAppsButton();
            }
            if (LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue()) {
                WallpaperBlurredImageController.getInstance(LauncherExtension.this.getBaseContext()).setWallpaperMotionManager(LauncherExtension.this.mWallpaperMotionManager);
                StaticBlurEngine.getInstance().initializeLGBlurEngine(LauncherExtension.this.getBaseContext());
            }
            HomescreenBlurManager.getInstance(LauncherExtension.this.getBaseContext()).setBlurView2onResume();
            if (LauncherExtension.this.isInState(LauncherState.INAPPS) && LauncherExtension.this.mStarted) {
                HomescreenBlurManager.getInstance(LauncherExtension.this.getBaseContext()).clearBackground();
                LauncherExtension.this.getStateManager().goToState(LauncherState.NORMAL, true, (Runnable) null);
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onStart() {
            Launcher.LauncherOverlayCallbacks launcherOverlayCallbacks;
            LauncherExtension.this.mStarted = true;
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.onStart();
            }
            if (LauncherExtension.this.mWallpaperMotionManager != null) {
                LauncherExtension.this.mWallpaperMotionManager.start();
                if (!LauncherExtension.this.getState().useMotion) {
                    LauncherExtension.this.mWallpaperMotionManager.setEnableParallax(false);
                }
            }
            LauncherExtension.this.startSwipeUpGuideAnimation();
            if (LauncherExtension.this.mEditModeOffManager != null && LauncherExtension.this.mWorkspace != null && LauncherExtension.this.mWorkspace.getState() == Workspace.State.OVERVIEW) {
                LauncherExtension.this.mEditModeOffManager.start();
            }
            if (LauncherExtension.this.mBurnInProtectionHelper != null && LauncherExtension.this.isInState(LauncherState.NORMAL) && LauncherExtension.this.getWorkspace().getState() == Workspace.State.NORMAL && !LauncherExtension.this.mModel.isLoadingWorkspace() && ((!hasCustomContentToLeft() || LauncherExtension.this.getWorkspace().getCurrentPage() != 0) && (launcherOverlayCallbacks = this.mLauncherOverlayCallbacks) != null && Float.compare(1.0f, launcherOverlayCallbacks.getProgress()) != 0)) {
                LauncherExtension.this.mBurnInProtectionHelper.startBurnInProtection();
            }
            HomescreenBlurManager.getInstance(LauncherExtension.this.getBaseContext()).setBlurView2onStart();
            WidgetBlurManager.getInstance(LauncherExtension.this.getBaseContext()).onStart(true);
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onStop() {
            LauncherExtension.this.mStarted = false;
            if (LauncherExtension.this.mPaused) {
                LauncherExtension.this.mAlreadyOnHome = false;
            }
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.onStop();
            }
            if (LauncherExtension.this.mWallpaperMotionManager != null) {
                LauncherExtension.this.mWallpaperMotionManager.end();
            }
            LauncherExtension.this.mSwipeUpGuideAnimation.cancelSwipeUpAnim();
            if (LauncherExtension.this.mEditModeOffManager != null) {
                LauncherExtension.this.mEditModeOffManager.end();
            }
            if (LauncherExtension.this.mBurnInProtectionHelper != null) {
                LauncherExtension.this.mBurnInProtectionHelper.cancelBurnInProtection();
            }
            if (LauncherExtension.this.mWidgetsView != null) {
                ((LGWidgetContainerView) LauncherExtension.this.mWidgetsView).clear();
                ((LGWidgetContainerView) LauncherExtension.this.mWidgetsView).setFlagForRefreshPreView(true);
                ((LGWidgetContainerView) LauncherExtension.this.mWidgetsView).cancelSearchWidgetsAsyncTask();
                if (LauncherExtension.this.isWidgetsViewVisible()) {
                    ((LGWidgetContainerView) LauncherExtension.this.mWidgetsView).closeGroupWidgetPopup();
                    LauncherExtension.this.getStateManager().goToState(LauncherState.NORMAL);
                }
            }
            MultiWindowGuideManager.getInstance(LauncherExtension.this.getApplicationContext()).hideGuide();
            LauncherExtension.this.cancelWorkspaceLongpress();
            HomescreenBlurManager.getInstance(LauncherExtension.this.getBaseContext()).setBlurView2onStop();
            WidgetBlurManager.getInstance(LauncherExtension.this.getBaseContext()).onStop(true);
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onPause() {
            LGLog.i(this.TAG, "Launcher.onPause // " + EventLogger.sPackageVersion + " this:" + this);
            if (ConciergeBoardMngr.isExtViewMode()) {
                ConciergeBoardMngr.cancelExtViewMode();
            }
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.onPause();
            }
            if (LauncherExtension.this.mEvieApi != null) {
                LauncherExtension.this.mEvieApi.onPause();
            }
            PendingIntentReceiver.enableQueue();
            LiveIconManager.getInstance(LauncherExtension.this.getBaseContext()).stop();
            if (UninstallModeManager.getInstance(LauncherExtension.this.getBaseContext()).isInUninstallMode()) {
                UninstallModeManager.getInstance(LauncherExtension.this.getBaseContext()).runUninstallBadgeAnimation(false, 0);
            }
            if (LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue()) {
                StaticBlurEngine.getInstance().releaseLGBlurEngine();
            }
            HomescreenBlurManager.getInstance(LauncherExtension.this.getBaseContext()).setBlurView2onPause();
            if (LauncherExtension.this.mSwivelContentsView == null || !LauncherExtension.this.mSwivelContentsView.getVideoView().canPause()) {
                return;
            }
            LauncherExtension.this.mSwivelContentsView.getVideoView().pause();
            LauncherExtension.this.mSwivelContentsView.setCurrentPosition(LauncherExtension.this.mSwivelContentsView.getVideoView().getCurrentPosition());
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onDestroy() {
            LGLog.i(this.TAG, "Launcher.onDestroy");
            ConciergeBoardNotificationReceiver.unregisterReceiver(LauncherExtension.this);
            LauncherExtension.this.unregisterIconChangeListObserver();
            LauncherExtension.this.unRegisterMDMPolicyReceiver();
            LauncherExtension.this.unregisterBubbleMessageExpandReceiver();
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.onDestroy();
            }
            if (LauncherExtension.this.mEvieApi != null) {
                LauncherExtension.this.mEvieApi.updateScroll(0.0f);
                LauncherExtension.this.mEvieApi.onDestroy();
                LGLog.d(this.TAG, "unregister VZWSideScreenObserver");
                SharedPreferencesManager.unregisterOnSharedPreferenceChangeListener(LauncherExtension.this.getApplicationContext(), 0, LauncherExtension.this.mVZWSideScreenObserver);
            }
            FolderColorUtil.destoryFolderIconMask();
            LauncherExtension launcherExtension = LauncherExtension.this;
            launcherExtension.unregisterReceiver(launcherExtension.mWallpaperChangeReceiver);
            PendingIntentReceiver.unregisterReceiver(LauncherExtension.this);
            DDTChangeWatcher.getInstance().removeAllListeners();
            ScreenZoomChangeWatcher.getInstance().removeAllListeners();
            UninstallModeManager.getInstance(LauncherExtension.this).destroy();
            AppNotifierManager.getInstance(LauncherExtension.this).destroyAppNotifier();
            FolderStateTransitionWatcher.getInstance().destroy();
            ScreenEffectManager.getInstance(LauncherExtension.this).destroy();
            HomescreenBlurManager.getInstance(LauncherExtension.this).destroy();
            WallpaperBlurredImageController.getInstance(LauncherExtension.this).destroy();
            WorkspaceStateTransitionWatcher.getInstance(LauncherExtension.this).destroy();
            WidgetBlurManager.getInstance(LauncherExtension.this).destroy();
            LauncherScrollerWatcher.getInstance().destroy();
            LiveIconManager liveIconManager = LiveIconManager.getInstance(LauncherExtension.this.getBaseContext());
            liveIconManager.stop();
            liveIconManager.unregisterOnLiveIconUpdateListener(LauncherExtension.this.mLauncherListener);
            if (LGHomeFeature.Config.FEATURE_USE_SKT_PHONE_MODE.getValue() && LauncherExtension.this.mTPMR != null) {
                LauncherExtension.this.mTPMR.unregisterReceiver(LauncherExtension.this.getApplicationContext());
                LauncherExtension.this.mTPMR = null;
            }
            if (LauncherExtension.this.mSelectedHomeObserver != null) {
                LauncherExtension.this.mSelectedHomeObserver.unregisterObserver(LauncherExtension.this.getApplicationContext());
                LauncherExtension.this.mSelectedHomeObserver = null;
            }
            if (LauncherExtension.this.mAppDrawerButtonObserver != null) {
                LauncherExtension.this.mAppDrawerButtonObserver.unregisterObserver(LauncherExtension.this.getApplicationContext());
                LauncherExtension.this.mAppDrawerButtonObserver = null;
            }
            LauncherExtension.this.mAdaptiveTextManager.destroy();
            if (LauncherExtension.this.mWidgetsView != null) {
                ((LGWidgetContainerView) LauncherExtension.this.mWidgetsView).clear();
                ((LGWidgetContainerView) LauncherExtension.this.mWidgetsView).cancelSearchWidgetsAsyncTask();
            }
            if (LauncherExtension.this.mWallpaperMotionManager != null) {
                LauncherExtension.this.mWallpaperMotionManager.destroy();
                LauncherExtension.this.mWallpaperMotionManager = null;
            }
            if (LauncherExtension.this.mEditModeOffManager != null) {
                LauncherExtension.this.mEditModeOffManager.destroy();
                LauncherExtension.this.mEditModeOffManager = null;
            }
            if (GVNUtils.isGVNScreenEffectOn(LauncherExtension.this.getBaseContext())) {
                GVNScreenSoundImage.getInstance(LauncherExtension.this.getBaseContext()).destroy();
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onNewIntent(Intent intent) {
            boolean z = false;
            if (intent.getBooleanExtra(SBConstant.EXTRA_TO_SMARTBULLETIN, false) && SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(LauncherExtension.this.getApplicationContext())) {
                LauncherExtension.this.mPendingMoveScreenIndex = 0;
            }
            if (LauncherExtension.this.mPendingMoveScreenIndex != -1) {
                LauncherExtension.this.mWorkspace.postDelayed(new Runnable() { // from class: com.lge.launcher3.LauncherExtension.LauncherCallbacksImpl.1
                    @Override // java.lang.Runnable
                    public void run() {
                        LauncherExtension.this.mWorkspace.setCurrentPage(LauncherExtension.this.mPendingMoveScreenIndex);
                        if (LauncherExtension.this.mWorkspace.getCurrentPage() == 0 && LauncherExtension.this.mWorkspace.hasCustomContent()) {
                            AdaptiveTextUtil.adaptiveNavigationBarLight(LauncherExtension.this.mWorkspace);
                        }
                        LauncherExtension.this.mPendingMoveScreenIndex = -1;
                    }
                }, 30L);
                return;
            }
            if (checkConditionToMoveDefaultScreen(intent)) {
                LauncherExtension.this.mWorkspace.postDelayed(new Runnable() { // from class: com.lge.launcher3.LauncherExtension.LauncherCallbacksImpl.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (LauncherExtension.this.mWorkspace != null) {
                            LauncherExtension.this.mWorkspace.moveToDefaultScreen(false);
                            if (LauncherCallbacksImpl.this.mLauncherClient != null) {
                                LauncherCallbacksImpl.this.mLauncherClient.hideOverlay(LauncherExtension.this.mAlreadyOnHome);
                            }
                        }
                    }
                }, 30L);
            }
            if (LauncherExtension.this.hasWindowFocus() && (intent.getFlags() & 4194304) != 4194304) {
                z = true;
            }
            if (!z && LauncherExtension.this.mWorkspace != null && LauncherExtension.this.mWorkspace.getOverlayTranslation() == 0.0f) {
                LauncherExtension.this.startSwipeUpGuideAnimation();
            }
            if (!"android.intent.action.SHOW_WORK_APPS".equals(intent.getAction()) || LGHomeFeature.isEnableDefaultHome()) {
                return;
            }
            LGLog.d(this.TAG, "need to show all apps work tab");
            if (LauncherExtension.this.getAllAppsHost() != null) {
                LauncherExtension.this.getAllAppsHost().setShowWorkTabIfNeeded();
                Executors.MAIN_EXECUTOR.postDelayed(new Runnable() { // from class: com.lge.launcher3.-$$Lambda$LauncherExtension$LauncherCallbacksImpl$l_J6DxjAvqG8iLM3AIdf805TGRc
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onNewIntent$0$LauncherExtension$LauncherCallbacksImpl();
                    }
                }, 1000);
            }
        }

        public /* synthetic */ void lambda$onNewIntent$0$LauncherExtension$LauncherCallbacksImpl() {
            LauncherExtension.this.getStateManager().goToState(LauncherState.ALL_APPS);
        }

        private boolean checkConditionToMoveDefaultScreen(Intent intent) {
            return LauncherExtension.this.mLauncherCallbacks != null && ((LauncherCallbacksImpl) LauncherExtension.this.mLauncherCallbacks).mOverlayProgress == 1.0f && PackageUtils.ANDROID_INTENT_ACTION_MAIN.equals(intent.getAction()) && LauncherExtension.this.mAlreadyOnHome;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 1112 && resultCode == 1113) {
                LauncherExtension.this.closeFolder(new boolean[0]);
            } else if (requestCode == 1115 && (LauncherExtension.this.getStateManager().getState() instanceof InAppsState)) {
                LauncherExtension.this.getStateManager().goToState(LauncherState.NORMAL);
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean onPrepareOptionsMenu(Menu menu) {
            if (!ConciergeBoardMngr.isExtViewMode()) {
                return false;
            }
            ConciergeBoardMngr.cancelExtViewMode();
            return false;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void dump(String prefix, FileDescriptor fd, PrintWriter w, String[] args) {
            w.println("\nLauncherEx:");
            w.println(prefix + "\tmAlreadyOnHome: " + LauncherExtension.this.mAlreadyOnHome);
            if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                w.println(prefix + "\tmIsSwivelHome: " + LauncherExtension.this.mIsSwivelHome);
                w.println(prefix + "\tmIsSwivelHomeShown: " + LauncherExtension.this.mIsSwivelHomeShown);
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean handleBackPressed() {
            if (ConciergeBoardMngr.isExtViewMode()) {
                ConciergeBoardMngr.cancelExtViewMode();
            }
            if (!LauncherExtension.this.isInState(LauncherState.NORMAL) || !LauncherExtension.this.isOnCustomContent()) {
                return false;
            }
            LauncherExtension.this.moveWorkspaceToDefaultScreenWithAnimation();
            return false;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void updateLauncherClient(boolean enableMinusOneScreen) {
            if (this.mLauncherClient == null || LauncherExtension.this.mStatusOfClientOptions == enableMinusOneScreen) {
                return;
            }
            this.mLauncherClient.setClientOptions(LauncherExtension.this.getClientOptions(enableMinusOneScreen));
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public LauncherClient getLauncherClient() {
            return this.mLauncherClient;
        }

        /* JADX WARN: Type inference failed for: r5v15, types: [com.lge.launcher3.LauncherExtension$LauncherCallbacksImpl$4] */
        @Override // com.android.launcher3.LauncherCallbacks
        public void finishBindingItems(boolean upgradePath) {
            InitialGuideManager.getInstance(LauncherExtension.this.getBaseContext()).setReadyToShow(true);
            if (LGHomeFeature.Config.FEATURE_USE_SKT_PHONE_MODE.getValue()) {
                TPhoneModeUtils.updateTPhoneMode(LauncherExtension.this.getApplicationContext());
            }
            LauncherExtension.this.getRootView().post(new Runnable() { // from class: com.lge.launcher3.LauncherExtension.LauncherCallbacksImpl.3
                @Override // java.lang.Runnable
                public void run() {
                    WallpaperMotionUtils.saveDrawingCache(LauncherExtension.this.getApplicationContext(), LauncherExtension.this.getRootView());
                }
            });
            if (WallpaperMotionUtils.isMotionEnabled(LauncherExtension.this.getApplicationContext())) {
                new AsyncTask<Void, Void, Void>() { // from class: com.lge.launcher3.LauncherExtension.LauncherCallbacksImpl.4
                    /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
                    @Override // android.os.AsyncTask
                    public Void doInBackground(Void... args) {
                        WallpaperMotionUtils.resetWallpaperIfNeed(LauncherExtension.this.getApplicationContext());
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onClickAllAppsButton(View v) {
            if (ConciergeBoardMngr.isExtViewMode()) {
                return;
            }
            ConciergeBoardMngr.cancelExtViewMode();
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void bindAllApplications(ArrayList<AppInfo> apps) {
            LGLog.i(this.TAG, "finish bindAllApplications by launcher extension");
            if (!LauncherExtension.this.mIsSwivelItemInitialized && LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                LGLog.i(this.TAG, "start swivel item initialization task");
                LauncherSettings.Settings.callSwivel(LauncherExtension.this.getContentResolver(), LauncherSettings.Settings.METHOD_LOAD_DEFAULT_SWIVEL_FAVORITES);
                LauncherExtension.this.mIsSwivelItemInitialized = true;
            }
            if (LauncherExtension.this.getAllAppsHost() != null && LauncherExtension.this.getAllAppsHost().getShowWorkTabIfNeeded()) {
                Executors.MAIN_EXECUTOR.postDelayed(new Runnable() { // from class: com.lge.launcher3.-$$Lambda$LauncherExtension$LauncherCallbacksImpl$ROUvlbqtHDnLSrtt4DuX6n6A-kY
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$bindAllApplications$1$LauncherExtension$LauncherCallbacksImpl();
                    }
                }, PathInterpolatorCompat.MAX_NUM_POINTS);
            }
            if (LauncherExtension.this.mNeedToWorkFolderPage) {
                Executors.MAIN_EXECUTOR.postDelayed(new Runnable() { // from class: com.lge.launcher3.-$$Lambda$LauncherExtension$LauncherCallbacksImpl$RJtsviAYMGDFM_4fY9G6xSUEF7Q
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$bindAllApplications$2$LauncherExtension$LauncherCallbacksImpl();
                    }
                }, 1000);
            }
        }

        public /* synthetic */ void lambda$bindAllApplications$1$LauncherExtension$LauncherCallbacksImpl() {
            LauncherExtension.this.getStateManager().goToState(LauncherState.ALL_APPS);
        }

        /* JADX DEBUG: Method merged with bridge method: lambda$bindAllApplications$2$LauncherExtension$LauncherCallbacksImpl()V */
        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:12:0x0065  */
        /* JADX WARN: Removed duplicated region for block: B:24:0x00a0  */
        /* JADX INFO: renamed from: goWorkFolderPage, reason: merged with bridge method [inline-methods] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void lambda$bindAllApplications$2$LauncherExtension$LauncherCallbacksImpl() {
            /*
                r9 = this;
                android.net.Uri r1 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI
                java.lang.String r6 = "screen"
                java.lang.String[] r2 = new java.lang.String[]{r6}
                long r3 = com.lge.launcher3.util.UserUtils.getWorkFolderId()
                long r7 = com.lge.launcher3.util.UserUtils.getUserSerialNum()
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r5 = "(itemType='2' AND container='-100' AND options='2' AND _id='"
                r0.append(r5)
                r0.append(r3)
                java.lang.String r3 = "' AND profileId='"
                r0.append(r3)
                r0.append(r7)
                java.lang.String r3 = "')"
                r0.append(r3)
                java.lang.String r3 = r0.toString()
                r7 = -1
                r8 = 0
                com.lge.launcher3.LauncherExtension r0 = com.lge.launcher3.LauncherExtension.this     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L6e
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L6e
                r4 = 0
                r5 = 0
                android.database.Cursor r8 = r0.query(r1, r2, r3, r4, r5)     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L6e
                if (r8 == 0) goto L65
                int r0 = r8.getColumnIndexOrThrow(r6)     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L6e
                boolean r1 = r8.moveToFirst()     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L6e
                if (r1 == 0) goto L65
                int r0 = r8.getInt(r0)     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L6e
                java.lang.String r1 = r9.TAG     // Catch: java.lang.Exception -> L63 java.lang.Throwable -> L6c
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L63 java.lang.Throwable -> L6c
                r2.<init>()     // Catch: java.lang.Exception -> L63 java.lang.Throwable -> L6c
                java.lang.String r3 = "goWorkFolderPage : screenId : "
                r2.append(r3)     // Catch: java.lang.Exception -> L63 java.lang.Throwable -> L6c
                r2.append(r0)     // Catch: java.lang.Exception -> L63 java.lang.Throwable -> L6c
                java.lang.String r2 = r2.toString()     // Catch: java.lang.Exception -> L63 java.lang.Throwable -> L6c
                com.lge.launcher3.util.LGLog.i(r1, r2)     // Catch: java.lang.Exception -> L63 java.lang.Throwable -> L6c
                goto L66
            L63:
                r1 = move-exception
                goto L70
            L65:
                r0 = r7
            L66:
                if (r8 == 0) goto L7d
            L68:
                r8.close()
                goto L7d
            L6c:
                r0 = move-exception
                goto Lb5
            L6e:
                r1 = move-exception
                r0 = r7
            L70:
                java.lang.String r2 = r9.TAG     // Catch: java.lang.Throwable -> L6c
                java.lang.String r3 = "failed to get screen page uri: "
                com.lge.launcher3.util.LGLog.e(r2, r3, r1)     // Catch: java.lang.Throwable -> L6c
                r1.printStackTrace()     // Catch: java.lang.Throwable -> L6c
                if (r8 == 0) goto L7d
                goto L68
            L7d:
                com.lge.launcher3.LauncherExtension r1 = com.lge.launcher3.LauncherExtension.this
                com.android.launcher3.Workspace r1 = r1.getWorkspace()
                long r2 = (long) r0
                int r0 = r1.getPageIndexForScreenId(r2)
                java.lang.String r1 = r9.TAG
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "go work folder page : screenPage : "
                r2.append(r3)
                r2.append(r0)
                java.lang.String r2 = r2.toString()
                com.lge.launcher3.util.LGLog.i(r1, r2)
                if (r0 <= r7) goto La9
                com.lge.launcher3.LauncherExtension r1 = com.lge.launcher3.LauncherExtension.this
                com.android.launcher3.Workspace r1 = r1.getWorkspace()
                r1.snapToPage(r0)
            La9:
                r0 = -1
                com.lge.launcher3.util.UserUtils.setWorkProfileInfo(r0, r0)
                com.lge.launcher3.LauncherExtension r0 = com.lge.launcher3.LauncherExtension.this
                r1 = 0
                com.lge.launcher3.LauncherExtension.access$2702(r0, r1)
                return
            Lb5:
                if (r8 == 0) goto Lba
                r8.close()
            Lba:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.LauncherExtension.LauncherCallbacksImpl.lambda$bindAllApplications$2$LauncherExtension$LauncherCallbacksImpl():void");
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onClickSettingsButton(View v) {
            LauncherExtension.this.startHomeSetting(v);
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onClickSwivelSettingsButton(View v) {
            LauncherExtension.this.startSwivelHomeSetting(v);
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onPageSwitch(View newPage, int newPageIndex) {
            if (newPage == null) {
                LGLog.w(this.TAG, "Switched new page is null.", new int[0]);
                return;
            }
            if (Utilities.isLGUI7_1()) {
                return;
            }
            if (LauncherExtension.this.mWorkspace.isInOverviewMode() && !LauncherExtension.this.mWorkspace.isReordering(false)) {
                if (((CellLayout) newPage).hasFullscreenItem() || LauncherExtension.this.isInState(LauncherState.DYNAMIC_GRID_OVERVIEW)) {
                    LauncherExtension.this.hideDefaultPageButton();
                } else {
                    LauncherExtension.this.showDefaultPageButton();
                }
            }
            LauncherExtension launcherExtension = LauncherExtension.this;
            launcherExtension.setDefaultPageButtonSelection(launcherExtension.mWorkspace.getDefaultPage() == newPageIndex);
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onInteractionBegin() {
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.requestHotwordDetection(false);
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void onInteractionEnd() {
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.requestHotwordDetection(true);
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean hasCustomContentToLeft() {
            if (GoogleNowManager.isAvailable(LauncherExtension.this.getApplicationContext()) && GoogleNowManager.isAppEnabled()) {
                return false;
            }
            if (VZWSideScreenManager.isAvailable() && VZWSideScreenManager.isAppEnabled()) {
                return false;
            }
            return SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(LauncherExtension.this.getApplicationContext()) || SBHomeDataBaseUtil.existQmemoPanelItemInDataBase(LauncherExtension.this.getApplicationContext());
        }

        /* JADX DEBUG: Multi-variable search result rejected for r0v18, resolved type: android.view.View */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        @Override // com.android.launcher3.LauncherCallbacks
        public void populateCustomContentContainer() {
            String str;
            View view;
            if (GoogleNowManager.isAvailable(LauncherExtension.this.getApplicationContext()) && GoogleNowManager.isAppEnabled()) {
                return;
            }
            if (VZWSideScreenManager.isAvailable() && VZWSideScreenManager.isAppEnabled()) {
                return;
            }
            String str2 = "populateCustomContentContainer is called";
            Context applicationContext = LauncherExtension.this.getApplicationContext();
            Launcher.CustomContentCallbacks customContentCallbacks = null;
            if (SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(applicationContext)) {
                View viewInflate = LauncherExtension.this.getLayoutInflater().inflate(R.layout.smartbulletin_container, (ViewGroup) null);
                if (Utilities.isLGUI8_0()) {
                    View viewFindViewById = viewInflate.findViewById(R.id.smartbulletin_navibar);
                    if (SysUINavigationMode.getMode(LauncherExtension.this.getApplicationContext()) == SysUINavigationMode.Mode.NO_BUTTON) {
                        viewFindViewById.getLayoutParams();
                    }
                    if (LauncherExtension.this.mDeviceProfile.isMultiWindowMode || SysUINavigationMode.getMode(LauncherExtension.this.getApplicationContext()) == SysUINavigationMode.Mode.NO_BUTTON) {
                        viewFindViewById.setVisibility(8);
                    } else {
                        viewFindViewById.setVisibility(0);
                    }
                }
                customContentCallbacks = (Launcher.CustomContentCallbacks) viewInflate;
                str = "This is for SmartBulletin";
                view = viewInflate;
            } else if (SBHomeDataBaseUtil.existQmemoPanelItemInDataBase(applicationContext)) {
                ComponentName componentName = new ComponentName(QMemoPanelConst.QMEMOPANEL_PACKAGE_NAME, QMemoPanelConst.QMEMOPANEL_CLASS_NAME);
                LauncherAppWidgetHost appWidgetHost = LauncherExtension.this.getAppWidgetHost();
                if (appWidgetHost != null) {
                    int i = SharedPreferencesManager.getInt(applicationContext, 0, SharedPreferencesConst.QMemoWidgetKey.WIDGETID, -1);
                    if (i != -1) {
                        appWidgetHost.deleteAppWidgetId(i);
                    }
                    int iAllocateAppWidgetId = appWidgetHost.allocateAppWidgetId();
                    if (iAllocateAppWidgetId == -1) {
                        LGLog.d(this.TAG, "appWidgetId is -1");
                        SharedPreferencesManager.putInt(applicationContext, 0, SharedPreferencesConst.QMemoWidgetKey.WIDGETID, -1);
                        return;
                    }
                    SharedPreferencesManager.putInt(applicationContext, 0, SharedPreferencesConst.QMemoWidgetKey.WIDGETID, iAllocateAppWidgetId);
                    AppWidgetManager.getInstance(applicationContext).bindAppWidgetId(iAllocateAppWidgetId, componentName);
                    LauncherAppWidgetProviderInfo providerInfo = LauncherModel.getProviderInfo(applicationContext, componentName, Process.myUserHandle());
                    if (providerInfo == null) {
                        SBHomeDataBaseUtil.turnOffQMemoPanel(applicationContext);
                        LGLog.d(this.TAG, "appWidgetInfo is null");
                        str2 = "QMemoPlus panel is disabled";
                    }
                    if (providerInfo != null) {
                        View viewCreateView = appWidgetHost.createView((Context) appWidgetHost.getLauncher(), iAllocateAppWidgetId, providerInfo);
                        if (viewCreateView != null) {
                            viewCreateView.setPaddingRelative(0, 0, 0, 0);
                            viewCreateView.setFocusable(true);
                        }
                        HomeSettingsSharedPreferences.putEnableQmemopluspanel(applicationContext, true);
                        str = "This is for QMemoPlus panel";
                        view = viewCreateView;
                    }
                } else {
                    LGLog.d(this.TAG, "getAppWidgetHost() is null");
                }
                str = str2;
                view = null;
            } else {
                LGLog.d(this.TAG, "FullScreenItemInfo does not exist");
                return;
            }
            if (view == null || LauncherExtension.this.getWorkspace() == null) {
                return;
            }
            LauncherExtension.this.addToCustomContentPage(view, customContentCallbacks, str);
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean shouldMoveToDefaultScreenOnHomeIntent() {
            if (!ConciergeBoardMngr.isExtViewMode()) {
                return true;
            }
            ConciergeBoardMngr.cancelExtViewMode();
            return false;
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public boolean hasSettings() {
            try {
                if (isSupportedLGPreferenceFragment()) {
                }
                return true;
            } catch (NoClassDefFoundError e) {
                e.printStackTrace();
                return false;
            }
        }

        private boolean isSupportedLGPreferenceFragment() throws NoClassDefFoundError {
            return true;
        }

        @Override // com.android.launcher3.DeviceProfile.OnDeviceProfileChangeListener
        public void onDeviceProfileChanged(DeviceProfile dp) {
            if (this.mLauncherClient != null) {
                LGLog.d(this.TAG, "onDeviceProfileChanged : reattachOverlay");
                this.mLauncherClient.reattachOverlay();
            }
        }

        private class LauncherOverlayImpl implements Launcher.LauncherOverlay {
            boolean mForwardMotion;

            private LauncherOverlayImpl() {
            }

            @Override // com.android.launcher3.Launcher.LauncherOverlay
            public void onScrollInteractionBegin() {
                this.mForwardMotion = true;
                if (LauncherCallbacksImpl.this.mLauncherClient == null || LauncherExtension.this.isCleanViewState()) {
                    return;
                }
                LauncherCallbacksImpl.this.mLauncherClient.startMove();
            }

            @Override // com.android.launcher3.Launcher.LauncherOverlay
            public void onScrollInteractionEnd(int velocityX) {
                if (LauncherCallbacksImpl.this.mLauncherClient != null) {
                    LauncherCallbacksImpl.this.mLauncherClient.endMove();
                }
                this.mForwardMotion = false;
            }

            @Override // com.android.launcher3.Launcher.LauncherOverlay
            public void onScrollChange(float progress, boolean rtl) {
                LauncherCallbacksImpl.this.mOverlayProgress = progress;
                if (!this.mForwardMotion || LauncherCallbacksImpl.this.mLauncherClient == null) {
                    return;
                }
                LauncherCallbacksImpl.this.mLauncherClient.updateMove(progress);
            }

            @Override // com.android.launcher3.Launcher.LauncherOverlay
            public void setOverlayCallbacks(Launcher.LauncherOverlayCallbacks callbacks) {
                LauncherCallbacksImpl.this.mLauncherOverlayCallbacks = callbacks;
            }
        }

        private class EvieOverlayImpl implements Launcher.LauncherOverlay {
            boolean mIScrollingEnabledForSideScreen;

            private EvieOverlayImpl() {
            }

            @Override // com.android.launcher3.Launcher.LauncherOverlay
            public void onScrollInteractionBegin() {
                boolean z = (LauncherExtension.this.mEvieApi == null || !LauncherCallbacksImpl.this.mSideScreenAttached || LauncherExtension.this.mWorkspace == null || LauncherExtension.this.mWorkspace.isInOverviewMode() || LauncherExtension.this.isCleanViewState()) ? false : true;
                this.mIScrollingEnabledForSideScreen = z;
                if (!z || LauncherExtension.this.mEvieApi == null) {
                    return;
                }
                LauncherExtension.this.mEvieApi.startScroll();
            }

            @Override // com.android.launcher3.Launcher.LauncherOverlay
            public void onScrollInteractionEnd(int velocityX) {
                if (LauncherCallbacksImpl.this.mSideScreenAttached && this.mIScrollingEnabledForSideScreen && LauncherExtension.this.mEvieApi != null) {
                    LauncherExtension.this.mEvieApi.endScroll(velocityX);
                }
                this.mIScrollingEnabledForSideScreen = false;
            }

            @Override // com.android.launcher3.Launcher.LauncherOverlay
            public void onScrollChange(float progress, boolean rtl) {
                LauncherCallbacksImpl.this.mOverlayProgress = progress;
                if (LauncherCallbacksImpl.this.mSideScreenAttached && this.mIScrollingEnabledForSideScreen) {
                    if (LauncherExtension.this.mEvieApi != null) {
                        LauncherExtension.this.mEvieApi.updateScroll(progress);
                    }
                    if (LauncherCallbacksImpl.this.mLauncherOverlayCallbacks != null) {
                        LauncherCallbacksImpl.this.mLauncherOverlayCallbacks.onScrollChanged(progress);
                    }
                }
            }

            @Override // com.android.launcher3.Launcher.LauncherOverlay
            public void setOverlayCallbacks(Launcher.LauncherOverlayCallbacks callbacks) {
                LauncherCallbacksImpl.this.mLauncherOverlayCallbacks = callbacks;
            }
        }

        class LauncherClientCallbacksImpl implements LauncherClientCallbacks {
            private boolean mWasAttached = false;

            LauncherClientCallbacksImpl() {
            }

            @Override // com.google.android.libraries.gsa.launcherclient.LauncherClientCallbacks
            public void onOverlayScrollChanged(float progress) {
                LauncherCallbacksImpl.this.mOverlayProgress = progress;
                if (LauncherExtension.this.getWorkspace() == null || LauncherExtension.this.getWorkspace().isInOverviewMode() || LauncherExtension.this.getWorkspace().getCheckInappsValue() || LauncherExtension.this.isAllAppsVisible() || LauncherCallbacksImpl.this.mLauncherOverlayCallbacks == null) {
                    return;
                }
                LauncherCallbacksImpl.this.mLauncherOverlayCallbacks.onScrollChanged(progress);
            }

            /* JADX WARN: Type inference fix 'apply assigned field type' failed
            java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
            	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
            	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
            	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
             */
            @Override // com.google.android.libraries.gsa.launcherclient.LauncherClientCallbacks
            public void onServiceStateChanged(boolean z, boolean z2) {
                if (this.mWasAttached != z) {
                    this.mWasAttached = z;
                    LauncherExtension.this.setLauncherOverlay(z ? new LauncherOverlayImpl() : null);
                }
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public List<ComponentKey> getPredictedApps() {
            return new ArrayList();
        }

        public void updateEvieApi() {
            if (HomeSettingsSharedPreferences.getVZWSideScreenEnabled(LauncherExtension.this.getApplicationContext()) || LauncherExtension.this.mEvieApi == null) {
                return;
            }
            LGLog.d(this.TAG, "updateEvieApi turnOff");
            LauncherExtension.this.setLauncherOverlay(null);
            LauncherExtension.this.mEvieApi.turnOff();
            LauncherExtension.this.mEvieApi = null;
        }

        private void initEvieApi() {
            if (LauncherExtension.this.mEvieApi != null) {
                LauncherExtension.this.mEvieApi.onDestroy();
                VZWSideScreenManager.setInitialized(false);
                VZWSideScreenManager.setServiceAttached(false);
                LauncherExtension.this.mEvieApi = null;
            }
            LauncherExtension launcherExtension = LauncherExtension.this;
            LauncherExtension launcherExtension2 = LauncherExtension.this;
            launcherExtension.mEvieApi = new EvieApi(launcherExtension2, this.mEvieCallbacks, com.android.launcher3.Utilities.isRtl(launcherExtension2.getResources()), IntentUtils.makeLGHomeSettingIntent());
            if (LauncherExtension.this.mEvieApi != null) {
                LauncherExtension.this.setLauncherOverlay(new EvieOverlayImpl());
                VZWSideScreenManager.setInitialized(true);
            } else {
                VZWSideScreenManager.setInitialized(false);
            }
        }

        private void hideEvie() {
            if (LauncherExtension.this.mEvieApi != null) {
                if (LauncherExtension.this.isPaused()) {
                    LauncherExtension.this.mEvieApi.onResume();
                    LauncherExtension.this.mEvieApi.updateScroll(0.0f);
                    if (this.mLauncherOverlayCallbacks.getProgress() > 0.0f) {
                        LauncherExtension.this.mEvieApi.endScroll(1.0f);
                    }
                }
                LauncherExtension.this.mEvieApi.hide();
                if (LauncherExtension.this.isPaused()) {
                    LauncherExtension.this.mEvieApi.onPause();
                }
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void hideLauncherOverlay(boolean animate) {
            hideEvie();
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.hideOverlay(animate);
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void hideLauncherOverlay(int duration) {
            hideEvie();
            LauncherClient launcherClient = this.mLauncherClient;
            if (launcherClient != null) {
                launcherClient.hideOverlay(duration);
            }
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void setLauncherOverlayLightNavigationBar(boolean enabled) {
            LauncherExtension.this.setEvieLightNavigationBar(enabled);
        }

        @Override // com.android.launcher3.LauncherCallbacks
        public void resetSwivelItemInitialized() {
            LauncherExtension.this.mIsSwivelItemInitialized = false;
        }
    }

    private boolean checkFilterAvailability(MotionEvent event) {
        return this.mTouchEventFilter != null && (this.mIsActivated || event.getPointerCount() != 1);
    }

    @Override // com.android.launcher3.Launcher, android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int iFindPointerIndex = ev.findPointerIndex(ev.getPointerId(0));
        if (iFindPointerIndex == -1) {
            LGLog.w("touch", iFindPointerIndex + " touch index outof execetion  ", new int[0]);
            return true;
        }
        if (WallpaperMotionUtils.isBuildDrawingCache()) {
            if (ev.getAction() == 0) {
                LGLog.i(TAG, "dispatchTouchEvent(): skip touch for building drawing cache");
            }
            return true;
        }
        if (this.mPinchDecision == null) {
            return super.dispatchTouchEvent(ev);
        }
        if (ev.getActionMasked() == 0) {
            this.mPoint1DownTime = ev.getEventTime();
        }
        if (this.mPinchDecision.isAvailable(ev) || ev.getEventTime() - this.mPoint1DownTime < 200 || !checkFilterAvailability(ev)) {
            return super.dispatchTouchEvent(ev);
        }
        if (1 == ev.getAction()) {
            this.mIsActivated = false;
        } else {
            this.mIsActivated = true;
        }
        int action = ev.getAction();
        boolean zDispatchTouchEvent = false;
        do {
            MotionEvent motionEventFiltering = this.mTouchEventFilter.filtering(ev);
            int iFindPointerIndex2 = ev.findPointerIndex(ev.getPointerId(0));
            if (iFindPointerIndex2 == -1) {
                LGLog.w("touch", iFindPointerIndex2 + " touch index outof execetion ", new int[0]);
            } else {
                if (motionEventFiltering != null) {
                    zDispatchTouchEvent = super.dispatchTouchEvent(motionEventFiltering);
                    if (ev.getSequenceNumber() != motionEventFiltering.getSequenceNumber()) {
                        motionEventFiltering.recycle();
                    }
                } else {
                    zDispatchTouchEvent = super.dispatchTouchEvent(ev);
                }
                ev.setAction(action);
            }
        } while (this.mTouchEventFilter.needToSendAdditionalEvent());
        return zDispatchTouchEvent;
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isWorkspaceLocked()) {
            return false;
        }
        super.onCreateOptionsMenu(menu);
        invalidateOptionsMenu();
        return true;
    }

    @Override // com.android.launcher3.Launcher, android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!isOptionMenuAvailable()) {
            return false;
        }
        closeFolder(new boolean[0]);
        return true;
    }

    private boolean isOptionMenuAvailable() {
        if (this.mWorkspace.getOpenFolder() == null && isInState(LauncherState.NORMAL)) {
            return (isInState(LauncherState.NORMAL) && this.mWorkspace != null && this.mWorkspace.workspaceInModalState()) ? false : true;
        }
        return false;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startHomeSetting(View v) {
        Context applicationContext = getApplicationContext();
        Intent intent = new Intent();
        intent.setAction(IntentConst.Action.ACTION_SHOW_SETTING.getValue(applicationContext));
        intent.setFlags(343932928);
        intent.putExtra("startedBy", "LGHome");
        if (Utilities.isLGUI8_0()) {
            intent.putExtra("com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION", true);
        }
        try {
            LGUserLog.send(getApplicationContext(), LGUserLog.FEATURENAME_SHOWHOMESCREENSETTINGS);
            if (v != null) {
                lambda$startActivitySafely$4$Launcher(v, intent, (ItemInfo) null);
            } else {
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSwivelHomeSetting(View v) {
        Context applicationContext = getApplicationContext();
        Intent intent = new Intent();
        intent.setAction(IntentConst.Action.ACTION_SHOW_SWIVEL_SETTING.getValue(applicationContext));
        intent.setFlags(343932928);
        intent.putExtra("startedBy", "LGHome");
        if (Utilities.isLGUI8_0()) {
            intent.putExtra("com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION", true);
        }
        try {
            LGUserLog.send(getApplicationContext(), LGUserLog.FEATURENAME_SHOWHOMESCREENSETTINGS);
            if (v != null) {
                lambda$startActivitySafely$4$Launcher(v, intent, (ItemInfo) null);
            } else {
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.android.launcher3.uioverrides.QuickstepLauncher, com.android.launcher3.BaseQuickstepLauncher, com.android.launcher3.Launcher
    protected void setupViews() {
        super.setupViews();
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            this.mCarouselLayout = (CarouselLayout) findViewById(R.id.carousel_layout);
        }
        this.mWidgetsView = null;
        this.mDynamicGridPanelView = (ViewGroup) findViewById(R.id.overview_dynamic_panel);
        setVisibilityThemeButton();
        setVisibilityWallpaperButton();
        setVisibilityUninstallButton();
        setVisibilityDefaultScreenButton();
        setVisibilityDynamicGridButton();
        WidgetBlurManager.getInstance(this).setLauncher(this);
        HomescreenBlurManager.getInstance(this).setLauncher(this);
        LoopNormalModeManager.getInstance(this).setLauncher(this);
        ConciergeBoardMngr.setupExtLayerForAttach(this.mDragLayer);
        LGLog.v(TAG, "laucherSetupViews " + this.mDragLayer);
        this.mAdaptiveTextManager = new AdaptiveTextManager(this.mWorkspace);
    }

    private void setVisibilityWallpaperButton() {
        if (LGHomeFeature.Config.FEATURE_USE_EDITMODE_WALLPAPER.getValue()) {
            ((ViewGroup) findViewById(R.id.wallpaper_button).getParent()).setVisibility(0);
        }
    }

    private void setVisibilityThemeButton() {
        if (LGHomeFeature.Config.FEATURE_USE_EDITMODE_THEME.getValue() && PackageUtils.isPackageExisted(DDTUtils.THEME_SQUARE_PACKAGE, getApplicationContext())) {
            TextView textView = (TextView) findViewById(R.id.theme_button);
            if ((Utilities.isLGUI8_0() && getApplicationContext().getResources().getBoolean(R.bool.is_tablet)) || "tablet".equals(com.android.launcher3.Utilities.getSystemProperty("ro.build.characteristics", "default"))) {
                textView.setText(R.string.pick_wallpaper);
            } else if (Utilities.isLGUI7_0()) {
                textView.setText(R.string.wallpaper_and_theme);
            }
            ((ViewGroup) textView.getParent()).setVisibility(0);
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.LauncherExtension.12
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (LauncherExtension.this.mWorkspace.isSwitchingState()) {
                        return;
                    }
                    LGLog.d(LauncherExtension.TAG, "onClickThemeButton");
                    LGUserLog.send(LauncherExtension.this.getApplicationContext(), LGUserLog.FEATURENAME_SHOWTHEMESQUARE);
                    try {
                        LauncherExtension.this.lambda$startActivitySafely$4$Launcher(v, DDTUtils.getThemeIntent(), (ItemInfo) null);
                    } catch (ActivityNotFoundException unused) {
                        Toast.makeText(LauncherExtension.this, R.string.activity_not_found, 0).show();
                    }
                }
            });
        }
    }

    private void setVisibilityDynamicGridButton() {
        if (LGHomeFeature.Config.FEATURE_USE_EDITMODE_DYNAMICGRID.getValue()) {
            View viewFindViewById = findViewById(R.id.dynamic_gird_button);
            ((ViewGroup) viewFindViewById.getParent()).setVisibility(0);
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.LauncherExtension.13
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (LauncherExtension.this.mWorkspace.isSwitchingState()) {
                        return;
                    }
                    LGUserLog.send(LauncherExtension.this.getApplicationContext(), LGUserLog.FEATURENAME_SHOWGRID);
                    LauncherExtension.this.getLGOverviewPanel().setVisibility(8);
                    LauncherExtension.this.getStateManager().setStateOnly(LauncherState.DYNAMIC_GRID_OVERVIEW);
                    LauncherExtension.this.hideDefaultPageButton();
                    UninstallModeManager.getInstance(LauncherExtension.this).exitUninstallMode(LauncherExtension.this);
                    LauncherExtension.this.mDynamicGridPanelView.setVisibility(0);
                }
            });
        }
    }

    private void setVisibilityUninstallButton() {
        boolean z = LGHomeFeature.Config.FEATURE_USE_RECENT_UNINSTALL_APP.getValue() && !ManagedProfileUtils.isAFW(getBaseContext());
        View viewFindViewById = findViewById(R.id.recent_uninstall_button);
        ViewGroup viewGroup = (ViewGroup) viewFindViewById.getParent();
        if (z) {
            viewGroup.setVisibility(0);
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.LauncherExtension.14
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (LauncherExtension.this.mWorkspace.isSwitchingState()) {
                        return;
                    }
                    LGLog.d(LauncherExtension.TAG, "onClickRecentUninstallButton");
                    Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_RECENTUNINSTALL.getValue(LauncherExtension.this.getBaseContext()));
                    intent.setFlags(335544320);
                    if (Utilities.isLGUI8_0()) {
                        intent.putExtra("com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION", true);
                    }
                    LauncherExtension.this.lambda$startActivitySafely$4$Launcher(v, intent, (ItemInfo) null);
                }
            });
        } else {
            viewGroup.setVisibility(8);
        }
    }

    private void setVisibilityDefaultScreenButton() {
        this.mDefaultPageButton = findViewById(R.id.default_screen);
        if (!Utilities.isLGUI7_1()) {
            this.mDefaultPageButton.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.LauncherExtension.15
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (LauncherExtension.this.mWorkspace.isSwitchingState() || LauncherExtension.this.mWorkspace.isScrolling()) {
                        return;
                    }
                    LauncherExtension.this.mWorkspace.setDefaultPage(LauncherExtension.this.mWorkspace.getCurrentPage());
                    LauncherExtension.this.setDefaultPageButtonSelection(true);
                    LGUserLog.send(LauncherExtension.this.getApplicationContext(), LGUserLog.FEATURENAME_DEFAULTSCREEN, LauncherExtension.this.mWorkspace.getCurrentPage());
                }
            });
        } else {
            this.mDefaultPageButton.setVisibility(8);
        }
    }

    @Override // com.android.launcher3.Launcher, android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        LGLog.d("RuntimePermission", "[RuntimePermission] requestCode = " + requestCode);
        if (requestCode != 1111) {
            return;
        }
        RequestPermissionsHelper.handlePermissionRequestResult(this, new String[]{"android.permission.CALL_PHONE"}, true, new DefaultUiProvider());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableRecentlyUninstall() {
        PackageUtils.enableRecentUninstall(getApplicationContext(), getPackageName());
        setVisibilityUninstallButton();
    }

    @Override // com.android.launcher3.Launcher
    protected boolean showWorkspace(int snapToPage, boolean animated, Runnable onCompleteRunnable) {
        boolean zShowWorkspace = super.showWorkspace(snapToPage, animated, onCompleteRunnable);
        if (this.mWorkspace != null) {
            this.mWorkspace.setDefaultPageBackground(false);
        } else {
            LGLog.i(TAG, "showWorkspace() : mWorkspace is null");
        }
        hideDefaultPageButton();
        UninstallModeManager.getInstance(this).exitUninstallMode(this);
        runBindOnResumeRunnable();
        EditModeOffManager editModeOffManager = this.mEditModeOffManager;
        if (editModeOffManager != null) {
            editModeOffManager.end();
        }
        if (this.mWallpaperMotionManager != null && this.mWorkspace != null && this.mWorkspace.getOpenFolder() == null) {
            this.mWallpaperMotionManager.setEnableParallax(true);
        }
        return zShowWorkspace;
    }

    private void runBindOnResumeRunnable() {
        if (this.mPaused || this.mBindOnResumeCallbacks.size() <= 0) {
            return;
        }
        for (int i = 0; i < this.mBindOnResumeCallbacks.size(); i++) {
            this.mBindOnResumeCallbacks.get(i).run();
        }
        this.mBindOnResumeCallbacks.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerMDMPolicyReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentConst.Action.ACTION_MDM_CHANGE_UNINSTALLPOLICY.getValue(getApplicationContext()));
        intentFilter.addAction(IntentConst.Action.ACTION_MDM_ADMIN_ACTIVATE.getValue(getApplicationContext()));
        intentFilter.addAction(IntentConst.Action.ACTION_MDM_ADMIN_DEACTIVATE.getValue(getApplicationContext()));
        registerReceiver(this.mMDMPolicyReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerBubbleMessageExpandReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentConst.Action.ACTION_BUBBLE_MESSAGE_EXPAND.getValue(getApplicationContext()));
        registerReceiver(this.mBubbleMessageExpandReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unRegisterMDMPolicyReceiver() {
        unregisterReceiver(this.mMDMPolicyReceiver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterBubbleMessageExpandReceiver() {
        unregisterReceiver(this.mBubbleMessageExpandReceiver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyMDMPolicy(final String action, final String[] packageList) {
        if (waitUntilResume(new Runnable() { // from class: com.lge.launcher3.LauncherExtension.18
            @Override // java.lang.Runnable
            public void run() {
                LauncherExtension.this.applyMDMPolicy(action, packageList);
            }
        })) {
            return;
        }
        if (action.equals(IntentConst.Action.ACTION_MDM_CHANGE_UNINSTALLPOLICY.getValue(getApplicationContext())) || action.equals(IntentConst.Action.ACTION_MDM_ADMIN_ACTIVATE.getValue(getApplicationContext())) || action.equals(IntentConst.Action.ACTION_MDM_ADMIN_DEACTIVATE.getValue(getApplicationContext()))) {
            LGLog.i(TAG, "applyMDMPolicy. action = " + action);
            if (packageList == null) {
                this.mWorkspace.updateUninstallPolicytoAll();
                if (LGHomeFeature.isEnableDefaultHome() || getAllAppsHost() == null || getAllAppsHost().getLGAllAppsPagedView() == null) {
                    return;
                }
                getAllAppsHost().getLGAllAppsPagedView().updateUninstallPolicytoAll();
                return;
            }
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(packageList));
            if (arrayList.size() > 0) {
                this.mWorkspace.updateUninstallPolicy(arrayList);
                if (LGHomeFeature.isEnableDefaultHome() || getAllAppsHost() == null || getAllAppsHost().getLGAllAppsPagedView() == null) {
                    return;
                }
                getAllAppsHost().getLGAllAppsPagedView().updateUninstallPolicy(arrayList);
            }
        }
    }

    @Override // com.lge.launcher3.DDTChangeWatcher.DDTChangeListener
    public void onDDTChanged(String oldThemePackageName, String newThemePackageName) {
        LGLog.i(TAG, "onDDTChanged(), restart process!");
        LauncherAppState launcherAppState = LauncherAppState.getInstance(this);
        launcherAppState.getIconCache().clearIconDB();
        ((LGWidgetPreviewLoader) launcherAppState.getWidgetCache()).clearCacheDB();
        SettingsSearchUtils.updateIconFramesVisible(getApplicationContext(), true, false);
        SettingsSearchUtils.updateIconFrameTypeVisible(getApplicationContext(), true, false);
        SettingsSearchUtils.updateThemedIconVisible(getApplicationContext(), true, false);
        Process.killProcess(Process.myPid());
    }

    @Override // com.lge.launcher3.ScreenZoomChangeWatcher.ScreenZoomChangeListener
    public void onScreenZoomChanged(float oldDensity, float newDensity) {
        LGLog.i(TAG, "onScreenZoomChanged(), restart process!");
        LauncherAppState launcherAppState = LauncherAppState.getInstance(this);
        launcherAppState.getIconCache().clearIconDB();
        ((LGWidgetPreviewLoader) LauncherAppState.getInstance(this).getWidgetCache()).clearCacheDB();
        if (launcherAppState.getModel() != null) {
            launcherAppState.getModel().loadAndBindWidgetsAndShortcuts(this, this, true);
        }
        launcherAppState.updateValues();
        UninstallBadgeUtils.initUninstallBadge();
        Process.killProcess(Process.myPid());
    }

    @Override // com.lge.launcher3.ScreenZoomChangeWatcher.ScreenZoomChangeListener
    public void onScreenResolutionChanged(float oldDensity, float newDensity) {
        LGLog.i(TAG, "onScreenResolutionChanged(), forceReload!");
        LauncherAppState launcherAppState = LauncherAppState.getInstance(this);
        ((LGWidgetPreviewLoader) launcherAppState.getWidgetCache()).clearCacheDB();
        if (launcherAppState.getModel() != null) {
            launcherAppState.getModel().loadAndBindWidgetsAndShortcuts(getApplicationContext(), this, true);
        }
        launcherAppState.updateValues();
        DeviceProfile deviceProfile = launcherAppState.getInvariantDeviceProfile().getDeviceProfile(this);
        if (deviceProfile instanceof LGDeviceProfile) {
            ((LGDeviceProfile) deviceProfile).calculateAppWidgetScale(getApplicationContext());
        }
        ShadowGenerator.updateShadowGenerator(this);
        UninstallBadgeUtils.initUninstallBadge();
        BadgeUtils.initShortcutBadge();
        Utilities.initDataFreeBadge();
        WallpaperUtils.resetDefaultWallpaperSize();
        this.mWorkspace.resetCellLayoutMetrics();
        if (LGHomeFeature.Config.FEATURE_IMPROVE_RESOLUTION_CHANGE.getValue()) {
            Iterator<LauncherAppWidgetInfo> it = LauncherModel.sBgDataModel.appWidgets.iterator();
            while (it.hasNext()) {
                it.next().resetHasNotifiedInitialWidgetSizeChanged();
            }
            this.mModel.forceReloadIcon();
            return;
        }
        this.mModel.forceReload();
    }

    @Override // com.android.launcher3.BaseQuickstepLauncher, com.android.launcher3.Launcher, com.android.launcher3.LauncherModel.Callbacks
    public void finishBindingItems() {
        closeInvalidfolder(false);
        super.finishBindingItems();
        Workspace workspace = getWorkspace();
        if (workspace == null) {
            return;
        }
        UninstallModeManager.getInstance(this).onBindingFinished(workspace);
        WallpaperBlurredImageController.getInstance(this).loadWallpaperBlurredImage(false);
        startSwipeUpGuideAnimation();
        if (LGHomeFeature.Config.FEATURE_GDEC_CHANGE_DEFAULT_PAGE.getValue()) {
            SharedPreferences sharedPreferences = getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0);
            if (sharedPreferences.getBoolean(PendingIntentObjectList.CotaReloadHandler.COTA_NEED_TO_MOVE_DEFAULT_PAGE, false)) {
                moveWorkspaceToDefaultScreen();
                sharedPreferences.edit().putBoolean(PendingIntentObjectList.CotaReloadHandler.COTA_NEED_TO_MOVE_DEFAULT_PAGE, false).commit();
            }
        }
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || workspace.getPageIndicator() == null) {
            return;
        }
        workspace.getPageIndicator().setVisibility(8);
        LGLog.i(TAG, "Finish to convert to swivel home, taken time : " + (System.currentTimeMillis() - this.mStartSwivelHomeTime));
    }

    private void closeInvalidfolder(boolean animated) {
        Folder openFolder = this.mWorkspace != null ? this.mWorkspace.getOpenFolder() : null;
        if (openFolder == null || sFolders == null) {
            return;
        }
        if (sFolders.get(openFolder.getInfo().id) == null) {
            closeFolder(openFolder, animated);
        } else {
            removeInvalidItem(openFolder);
        }
    }

    private void removeInvalidItem(Folder folder) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri uri = LauncherSettings.Favorites.CONTENT_URI;
        for (ShortcutInfo shortcutInfo : folder.getInfo().contents) {
            Cursor cursorQuery = contentResolver.query(uri, null, null, null, null);
            int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow("_id");
            boolean z = false;
            while (true) {
                if (cursorQuery.moveToNext()) {
                    if (shortcutInfo.id == cursorQuery.getLong(columnIndexOrThrow)) {
                        z = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!z) {
                folder.onRemove(shortcutInfo);
            }
            cursorQuery.close();
        }
    }

    @Override // com.android.launcher3.Launcher
    public void onScreenOff() {
        super.onScreenOff();
        LiveIconManager.getInstance(getBaseContext()).stop();
        if (this.mWorkspace != null) {
            this.mWorkspace.updateScrollToCurrentPageInNormalState();
        }
    }

    @Override // com.android.launcher3.Launcher
    protected FolderIcon addFolder(CellLayout layout, long container, long screenId, int cellX, int cellY) {
        FolderIcon folderIconAddFolder = super.addFolder(layout, container, screenId, cellX, cellY);
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService("accessibility");
        if (accessibilityManager.isEnabled()) {
            AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(32);
            accessibilityEventObtain.getText().add(getText(R.string.sp_talkback_folder_created));
            accessibilityEventObtain.setSource(layout, 0);
            accessibilityManager.sendAccessibilityEvent(accessibilityEventObtain);
        }
        return folderIconAddFolder;
    }

    @Override // com.android.launcher3.Launcher
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    @Override // com.android.launcher3.Launcher
    public void enterCleanViewMode() {
        super.enterCleanViewMode();
        this.mSwipeUpGuideAnimation.cancelSwipeUpAnim();
    }

    public void showOverviewMode() {
        super.showOverviewMode(true);
        LGUserLog.send(getApplicationContext(), LGUserLog.FEATURENAME_OVERVIEWMODE);
        EditModeOffManager editModeOffManager = this.mEditModeOffManager;
        if (editModeOffManager != null) {
            editModeOffManager.start();
        }
        if (this.mWallpaperMotionManager != null) {
            this.mWallpaperMotionManager.setEnableParallax(false);
        }
    }

    @Override // com.android.launcher3.Launcher
    protected void showOverviewMode(boolean animated) {
        if (this.mWidgetsView == null) {
            this.mWidgetsView = (WidgetsContainerView) ((ViewStub) findViewById(R.id.widgets_view_stub)).inflate();
            this.mWidgetsView.setVisibility(4);
        }
        if (this.mWidgetsView != null && this.mWidgetsModel != null) {
            this.mWidgetsView.addWidgets(this.mWidgetsModel);
            this.mWidgetsModel = null;
        } else if (this.mWidgetsView != null) {
            ((LGWidgetContainerView) this.mWidgetsView).notifyDataSetChanged();
        }
        super.showOverviewMode(animated);
        this.mWorkspace.setDefaultPageBackground(true);
        if (this.mDynamicGridPanelView != null) {
            this.mDynamicGridPanelView.setVisibility(8);
        }
        showDefaultPageButton();
        LGUserLog.send(getApplicationContext(), LGUserLog.FEATURENAME_OVERVIEWMODE);
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            this.mWorkspace.showAllCrossHair(true);
        }
        this.mSwipeUpGuideAnimation.cancelSwipeUpAnim();
        if (this.mEditModeOffManager != null) {
            this.mWorkspace.onOverlayScrollChanged(0.0f);
            this.mEditModeOffManager.start();
        }
        if (this.mWallpaperMotionManager != null) {
            this.mWallpaperMotionManager.setEnableParallax(false);
        }
    }

    @Override // com.android.launcher3.Launcher
    public WidgetsContainerView getWidgetsView() {
        if (this.mWidgetsView == null) {
            this.mWidgetsView = (WidgetsContainerView) ((ViewStub) findViewById(R.id.widgets_view_stub)).inflate();
            this.mWidgetsView.setVisibility(4);
        }
        if (this.mWidgetsView != null && this.mWidgetsModel != null) {
            this.mWidgetsView.addWidgets(this.mWidgetsModel);
            this.mWidgetsModel = null;
        }
        return this.mWidgetsView;
    }

    @Override // com.android.launcher3.Launcher, com.android.launcher3.LauncherModel.Callbacks
    public void bindAllPackages(WidgetsModel model) {
        super.bindAllPackages(model);
        if (this.mWidgetsView != null || model == null) {
            return;
        }
        this.mWidgetsModel = model;
    }

    @Override // com.android.launcher3.Launcher
    protected void startAppShortcutOrInfoActivity(View v) {
        CPUBoostService.boostUp(v.getContext());
        Workspace workspace = this.mWorkspace;
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            LGLog.i(TAG, "startAppShortcutOrInfoActivity() 1");
            if (UninstallModeManager.getInstance(this).checkAndShowUninstallPopup(this, v)) {
                return;
            }
            super.startAppShortcutOrInfoActivity(v);
            return;
        }
        LGLog.i(TAG, "startAppShortcutOrInfoActivity() 2");
        if (checkActionCallSelfPermission(v)) {
            if (workspace != null) {
                workspace.updateScrollToCurrentPageInNormalState();
            }
            super.startAppShortcutOrInfoActivity(v);
        }
    }

    @Override // com.android.launcher3.Launcher
    protected void showWidgetsView(boolean animated, boolean resetPageToZero) {
        if (this.mWidgetsView == null) {
            return;
        }
        hideDefaultPageButton();
        super.showWidgetsView(animated, resetPageToZero);
        PageIndicator pageIndicator = this.mWorkspace.getPageIndicator();
        if (pageIndicator != null) {
            pageIndicator.setVisibility(8);
        }
        LGWidgetContainerView lGWidgetContainerView = (LGWidgetContainerView) this.mWidgetsView;
        if (lGWidgetContainerView != null) {
            lGWidgetContainerView.resetMode();
        }
        LGUserLog.send(getApplicationContext(), LGUserLog.FEATURENAME_SHOWWIDGETLIST);
        EditModeOffManager editModeOffManager = this.mEditModeOffManager;
        if (editModeOffManager != null) {
            editModeOffManager.end();
        }
    }

    @Override // com.android.launcher3.Launcher
    public boolean startApplicationUninstallActivity(ComponentName componentName, int flags, UserHandle user) {
        if (ManagedProfileUtils.hasDeviceOwner(getBaseContext()) || ManagedProfileUtils.hasProfileOwner(getBaseContext())) {
            return super.startApplicationUninstallActivity(componentName, flags, user);
        }
        if ((flags & 1) == 0) {
            Toast.makeText(getBaseContext(), R.string.uninstall_system_app_text, 0).show();
            return false;
        }
        Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_DELETE_DIALOG.getValue(getBaseContext()), Uri.fromParts(AppNotifierManager.ExtraSpec.USAGE_PACKAGE, componentName.getPackageName(), componentName.getClassName()));
        intent.setFlags(545259520);
        if (user != null) {
            intent.putExtra("android.intent.extra.USER", user);
        }
        if (isInState(LauncherState.WIDGETS)) {
            intent.putExtra("startedBy", "Widgets");
        } else {
            intent.putExtra("startedBy", "Workspace");
        }
        startActivity(intent);
        return true;
    }

    @Override // com.android.launcher3.Launcher
    protected boolean canRunNewAppsAnimation() {
        boolean z;
        if (LauncherSettings.Settings.call(getContentResolver(), LauncherSettings.Settings.METHOD_WAS_NEW_DB_CREATED).getBoolean("value")) {
            z = !this.mAddAnimationflag;
            this.mAddAnimationflag = false;
        } else {
            z = true;
        }
        return z && super.canRunNewAppsAnimation();
    }

    @Override // com.android.launcher3.Launcher
    public void closeFolder(boolean... animate) {
        sendBroadcast(new Intent(IntentConst.Action.ACTION_FINISH_FOLDERPLUS.getValue(getApplicationContext())));
        super.closeFolder(animate);
    }

    @Override // com.android.launcher3.Launcher
    public void closeFolder(Folder folder, boolean... animate) {
        sendBroadcast(new Intent(IntentConst.Action.ACTION_FINISH_FOLDERPLUS.getValue(getApplicationContext())));
        super.closeFolder(folder, animate);
    }

    @Override // com.android.launcher3.Launcher
    protected void onClickWallpaperPicker(View v) {
        if (!Utilities.isWallapaperAllowed(getBaseContext())) {
            Toast.makeText(getBaseContext(), R.string.msg_disabled_by_admin, 0).show();
            return;
        }
        try {
            startActivitySafely(v, new Intent(IntentConst.Action.ACTION_SHOW_WALLPAPER_LIST_ACTIVITY.getValue(getBaseContext())), null, 10);
        } catch (ActivityNotFoundException e) {
            LGLog.e(TAG, "ActivityNotFoundException - ", e);
        }
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onClickWallpaperPicker(v);
        }
    }

    @Override // com.android.launcher3.Launcher, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (LGFeatureConfig.FEATURE_ENABLE_LGLOG && getWorkspace().isInOverviewMode()) {
            LGHiddenMenuUtil.hiddenMenuRunKeyCondition(this, keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void inflateLGDebugModeView() {
        final DragLayer dragLayer;
        if (!LGFeatureConfig.FEATURE_ENABLE_LGLOG || (dragLayer = getDragLayer()) == null) {
            return;
        }
        dragLayer.postDelayed(new Runnable() { // from class: com.lge.launcher3.LauncherExtension.20
            @Override // java.lang.Runnable
            public void run() {
                LayoutInflater inflater = LauncherExtension.this.getInflater();
                if (inflater == null) {
                    return;
                }
                inflater.inflate(R.layout.lg_debug_mode, (ViewGroup) dragLayer.getParent());
            }
        }, 5000L);
    }

    public void onStartReordering() {
        this.mWorkspace.setIsDragOccuring(true);
        hideDefaultPageButton();
    }

    public void onEndReordering() {
        this.mWorkspace.setIsDragOccuring(false);
        if (Utilities.isLGUI7_1() || !this.mWorkspace.isInOverviewMode()) {
            return;
        }
        setDefaultPageButtonSelection(this.mWorkspace.getCurrentPage() == this.mWorkspace.getDefaultPage());
        showDefaultPageButton();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDefaultPageButton() {
        if (Utilities.isLGUI7_1()) {
            return;
        }
        this.mDefaultPageButton.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideDefaultPageButton() {
        if (Utilities.isLGUI7_1()) {
            return;
        }
        this.mDefaultPageButton.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDefaultPageButtonSelection(boolean selected) {
        if (Utilities.isLGUI7_1() || this.mDefaultPageButton.isSelected() == selected) {
            return;
        }
        this.mDefaultPageButton.setSelected(selected);
    }

    public SwipeUpGuideAnimation getSwipeUpGuideAnimation() {
        return this.mSwipeUpGuideAnimation;
    }

    @Override // com.android.launcher3.Launcher
    public boolean startActivitySafely(View v, Intent intent, Object tag) {
        boolean zStartActivitySafely = super.startActivitySafely(v, intent, tag);
        if (zStartActivitySafely && this.mEvieApi != null && intent.getComponent() != null) {
            this.mEvieApi.onAppLaunched(intent.getComponent());
        }
        return zStartActivitySafely;
    }

    public void setEvieLightStatusBar(boolean enabled) {
        EvieApi evieApi = this.mEvieApi;
        if (evieApi != null) {
            evieApi.setLightStatusBar(enabled);
        }
    }

    public void setEvieLightNavigationBar(boolean enabled) {
        EvieApi evieApi = this.mEvieApi;
        if (evieApi != null) {
            evieApi.setLightNavigationBar(enabled);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSwipeUpGuideAnimation() {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            SwipeUpGuideAnimation swipeUpGuideAnimation = this.mSwipeUpGuideAnimation;
            if (swipeUpGuideAnimation != null) {
                swipeUpGuideAnimation.cancelSwipeUpAnim();
                return;
            }
            return;
        }
        if (!LGHomeFeature.isDisableAllApps() && LGHomeFeature.Config.FEATURE_SWIPEUP_APPDRAWER.getValue() && isInState(LauncherState.NORMAL) && getWorkspace().getState() == Workspace.State.NORMAL) {
            if ((hasCustomContentToLeft() && getWorkspace().getCurrentPage() == 0) || SwipeUpGuideAnimation.isInSwipUpGuideAnination() || SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).getMode().hasGestures) {
                return;
            }
            this.mSwipeUpGuideAnimation.startSwipeUpGuideAnimation();
        }
    }

    private class SelectedHomeObserver extends ContentObserver {
        Launcher mLauncher;

        public SelectedHomeObserver(Launcher launcher, Handler handler) {
            super(handler);
            this.mLauncher = launcher;
        }

        public void registerObserver(Context context) {
            context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(LauncherConst.LGHOME_DEFAULT_HOME), true, this);
            LGLog.d(LauncherExtension.TAG, "SelectedHomeObserver registerObserver");
        }

        public void unregisterObserver(Context context) {
            context.getContentResolver().unregisterContentObserver(this);
            LGLog.d(LauncherExtension.TAG, "SelectedHomeObserver unregisterObserver");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            LGLog.d(LauncherExtension.TAG, "SelectedHomeObserver onChange selfChange - " + selfChange);
            LauncherExtension.this.reloadLauncherIfNeeded();
        }
    }

    private class AppDrawerButtonObserver extends ContentObserver {
        Launcher mLauncher;

        public AppDrawerButtonObserver(Launcher launcher, Handler handler) {
            super(handler);
            this.mLauncher = launcher;
        }

        public void registerObserver(Context context) {
            context.getContentResolver().registerContentObserver(Settings.System.getUriFor(HomeSettingsSharedPreferences.ENABLE_APPDRAWER_BUTTON), true, this);
        }

        public void unregisterObserver(Context context) {
            context.getContentResolver().unregisterContentObserver(this);
            this.mLauncher = null;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (LGHomeFeature.isDisableAllApps()) {
                return;
            }
            LauncherExtension.this.getHotseat().getLayout().removeView(LauncherExtension.this.getAllAppsButton());
            this.mLauncher.getHotseat().setupAllAppsButton();
        }
    }

    @Override // com.lge.launcher3.util.GiftBoxManager.OnDataFreeAppUpdateListener
    public void onUpdatedDataFreeApps(final List<String> dataFreeApps) {
        if (this.mWorkspace != null) {
            this.mWorkspace.updateDataFreetoAll();
            if (LGHomeFeature.isEnableDefaultHome() || getAllAppsHost() == null || getAllAppsHost().getLGAllAppsPagedView() == null) {
                return;
            }
            getAllAppsHost().getLGAllAppsPagedView().updateDataFreetoAll();
        }
    }

    public void UpdatedDataFreeApps() {
        if (this.mWorkspace != null) {
            this.mWorkspace.updateDataFreetoAll();
            if (LGHomeFeature.isEnableDefaultHome() || getAllAppsHost() == null || getAllAppsHost().getLGAllAppsPagedView() == null) {
                return;
            }
            getAllAppsHost().getLGAllAppsPagedView().updateDataFreetoAll();
        }
    }

    @Override // com.android.launcher3.uioverrides.QuickstepLauncher, com.android.launcher3.BaseQuickstepLauncher, com.android.launcher3.Launcher, com.android.launcher3.BaseDraggingActivity, android.app.Activity
    public void onDestroy() {
        LiveWallpaperColorObserver liveWallpaperColorObserver;
        super.onDestroy();
        if (LGHomeFeature.Config.FEATURE_KT_GIFTBOX_DATA_FREE.getValue()) {
            this.mGiftBoxManager.unregisterNetworkCallback();
            this.mGiftBoxObserver.unregisterObserver(getApplicationContext());
        }
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            unRegisterPostureStateCallback();
            unregisterDateChangedReceiver();
            unregisterWallpaperChangedReceiver();
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR.getValue() && (liveWallpaperColorObserver = this.mLiveWallpaperColorObserver) != null) {
            liveWallpaperColorObserver.unregisterObserver(getApplicationContext());
            this.mLiveWallpaperColorObserver = null;
        }
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            WallpaperColorInfoUtil.getInstance(getApplicationContext()).removeOnChangeListener(this);
        }
        SwipeUpGuideAnimation swipeUpGuideAnimation = this.mSwipeUpGuideAnimation;
        if (swipeUpGuideAnimation != null) {
            swipeUpGuideAnimation.mLauncher = null;
            this.mSwipeUpGuideAnimation = null;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: java.util.ArrayList<? extends com.android.launcher3.model.data.ItemInfo> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.launcher3.Launcher, com.android.launcher3.LauncherModel.Callbacks
    public void addAppsOnSwivelHome(ArrayList<? extends ItemInfo> apps) {
        mInstallQueueApps = apps;
        Handler handler = mInstallQueueSwivelHander;
        if (handler != null) {
            mInstallQueueSwivelHander.sendMessage(handler.obtainMessage(2));
        }
    }

    private void onMultiWindowModeChangedInternal(boolean isMultiWindowMode) {
        Log.d(TAG, "onMultiWindowModeChanged : " + isMultiWindowMode);
        this.mIsMultiWindowModeInternal = isMultiWindowMode;
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).forceInvalidateLoadedTasks();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
        boolean zIsSplitScreenVisible = SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).isSplitScreenVisible();
        boolean z = this.mIsMultiWindowModeInternal;
        if (z != zIsSplitScreenVisible) {
            onMultiWindowModeChangedInternal(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public LauncherClient.ClientOptions getClientOptions(boolean enableGoogleFeed) {
        boolean z = LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue() && LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS_PREWARM.getValue();
        boolean value = LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_HOTWORD.getValue();
        this.mStatusOfClientOptions = enableGoogleFeed;
        return new LauncherClient.ClientOptions(enableGoogleFeed, value, z);
    }

    public class GiftBoxObserver extends ContentObserver {
        private static final String TAG = "GiftBoxObserver";
        private Context mContext;
        private Handler mHandler;
        private boolean mIsProviderAvailable;

        public GiftBoxObserver(Context context, Handler handler) {
            super(handler);
            this.mIsProviderAvailable = false;
            this.mHandler = handler;
            this.mContext = context;
        }

        public void registerObserver(Context context) {
            Uri uri = Uri.parse("content://com.lge.ktzradapter.provider/appInfo");
            if (context.getPackageManager().resolveContentProvider(uri.getAuthority(), 0) == null) {
                LGLog.i(TAG, "Not support " + uri);
                this.mIsProviderAvailable = false;
                return;
            }
            ContentResolver contentResolver = context.getContentResolver();
            this.mIsProviderAvailable = true;
            contentResolver.registerContentObserver(uri, true, this);
        }

        public void unregisterObserver(Context context) {
            ContentResolver contentResolver = context.getContentResolver();
            if (this.mIsProviderAvailable) {
                contentResolver.unregisterContentObserver(this);
                this.mIsProviderAvailable = false;
            }
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            LGLog.i(TAG, "onChange");
            super.onChange(selfChange);
            Utilities.setDataFreeApps(this.mContext);
            LauncherExtension.this.UpdatedDataFreeApps();
        }
    }
}
