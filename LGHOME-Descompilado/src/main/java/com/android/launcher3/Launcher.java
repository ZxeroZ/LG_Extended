package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Advanceable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DropTarget;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.LauncherStateTransitionAnimation;
import com.android.launcher3.Workspace;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PinItemRequestCompat;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.PinItemDragListener;
import com.android.launcher3.dragndrop.PinShortcutRequestActivityInfo;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.model.AppLaunchTracker;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.StringCache;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.WidgetsModel;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.touch.AllAppsSwipeController;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.ActivityResultInfo;
import com.android.launcher3.util.ActivityTracker;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.PendingRequestArgs;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.util.TraceHelper;
import com.android.launcher3.util.UiThreadHelper;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.views.FloatingIconView;
import com.android.launcher3.views.ScrimView;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.WidgetAddFlowHandler;
import com.android.launcher3.widget.WidgetHostViewLoader;
import com.android.launcher3.widget.WidgetsContainerView;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.views.RecentGuideView;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.RecommandAppLayout;
import com.android.quickstep.views.TaskMenuView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.plugins.OverlayPlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.shared.LauncherExterns;
import com.android.systemui.plugins.shared.LauncherOverlayManager;
import com.lge.launcher3.CustomUIManager;
import com.lge.launcher3.DDTChangeWatcher;
import com.lge.launcher3.LauncherExtension;
import com.lge.launcher3.OverViewPanel;
import com.lge.launcher3.PagedView;
import com.lge.launcher3.R;
import com.lge.launcher3.adaptive.AdaptiveTextUtil;
import com.lge.launcher3.allapps.AllAppsFolderInfo;
import com.lge.launcher3.allapps.AllAppsHost;
import com.lge.launcher3.allapps.AllAppsSortDialog;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.concierge.ConciergeBoardMngr;
import com.lge.launcher3.concierge.ConciergeBoardNotificationReceiver;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.dynamicgrid.AppWidgetSizeCalculator;
import com.lge.launcher3.hotword.HotwordServiceWrapper;
import com.lge.launcher3.initialguide.MultiWindowGuideManager;
import com.lge.launcher3.initialguide.SwivelHomeGuideManager;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.operator.VDFDataPopup;
import com.lge.launcher3.pageindicator.PageIndicatorExtension;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.receiver.PendingIntentObjectList;
import com.lge.launcher3.receiver.TPhoneModeReceiver;
import com.lge.launcher3.screeneffect.LoopNormalModeManager;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.CPUBoostService;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.OrientationUtils;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.TalkBackUtils;
import com.lge.launcher3.util.VibratorManager;
import com.lge.launcher3.util.WindowUtils;
import com.lge.launcher3.views.WorkGuideView;
import com.lge.launcher3.wallpaperblur.HomescreenBlurManager;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine;
import com.lge.launcher3.wallpapermotion.WallpaperMotionManager;
import com.lge.launcher3.widgettray.LGWidgetContainerView;
import com.lge.launcher3.wing.CarouselLayout;
import com.lge.launcher3.wing.SwivelContentsView;
import com.lge.systemservice.core.OneHandOperationManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes.dex */
public class Launcher extends StatefulActivity<LauncherState> implements View.OnClickListener, View.OnLongClickListener, LauncherModel.Callbacks, View.OnTouchListener, PagedView.PageSwitchListener, LauncherProviderChangeListener, LauncherStateTransitionAnimation.Callbacks, InvariantDeviceProfile.OnIDPChangeListener, LauncherExterns, PluginListener<OverlayPlugin> {
    static final String ACTION_FIRST_LOAD_COMPLETE = "com.android.launcher3.action.FIRST_LOAD_COMPLETE";
    private static final int ACTIVITY_START_DELAY = 1000;
    private static final int APPS_VIEW_ALPHA_CHANNEL_INDEX = 1;
    public static final int APPWIDGET_HOST_ID = 1024;
    private static final float BOUNCE_ANIMATION_TENSION = 1.3f;
    static final String CORRUPTION_EMAIL_SENT_KEY = "corruptionEmailSent";
    static final boolean DEBUG_DUMP_LOG = false;
    static final boolean DEBUG_RESUME_TIME = false;
    static final boolean DEBUG_STRICT_MODE = false;
    static final boolean DEBUG_WIDGETS = true;
    private static final boolean DISABLE_SYNCHRONOUS_BINDING_CURRENT_PAGE = false;
    static final String DUMP_STATE_PROPERTY = "launcher_dump_state";
    private static final boolean ENABLE_CUSTOM_WIDGET_TEST = false;
    static final boolean ENABLE_DEBUG_INTENTS = false;
    public static final int EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT = 300;
    static final String FIRST_LOAD_COMPLETE = "launcher.first_load_complete";
    static final String FIRST_RUN_ACTIVITY_DISPLAYED = "launcher.first_run_activity_displayed";
    public static final String INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION = "com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION";
    static final String INTRO_SCREEN_DISMISSED = "launcher.intro_screen_dismissed";
    static final boolean LOGD = false;
    private static final int ON_ACTIVITY_RESULT_ANIMATION_DELAY = 500;
    static final boolean PROFILE_STARTUP = false;
    private static final String QSB_WIDGET_ID = "qsb_widget_id";
    private static final String QSB_WIDGET_PROVIDER = "qsb_widget_provider";
    private static final int REQUEST_BIND_APPWIDGET = 11;
    public static final int REQUEST_BIND_PENDING_APPWIDGET = 14;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_CREATE_SHORTCUT = 1;
    protected static final int REQUEST_LAST = 100;
    private static final int REQUEST_PERMISSION_CALL_PHONE = 13;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    protected static final int REQUEST_PICK_WALLPAPER = 10;
    public static final int REQUEST_RECONFIGURE_APPWIDGET = 12;
    public static final int REQUEST_THEME_SELECT = 13;
    private static final String RUNTIME_STATE = "launcher.state";
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    private static final String RUNTIME_STATE_PENDING_ACTIVITY_RESULT = "launcher.activity_result";
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_X = "launcher.add_cell_x";
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_Y = "launcher.add_cell_y";
    private static final String RUNTIME_STATE_PENDING_ADD_COMPONENT = "launcher.add_component";
    private static final String RUNTIME_STATE_PENDING_ADD_CONTAINER = "launcher.add_container";
    private static final String RUNTIME_STATE_PENDING_ADD_SCREEN = "launcher.add_screen";
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_X = "launcher.add_span_x";
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_Y = "launcher.add_span_y";
    private static final String RUNTIME_STATE_PENDING_ADD_WIDGET_ID = "launcher.add_widget_id";
    private static final String RUNTIME_STATE_PENDING_ADD_WIDGET_INFO = "launcher.add_widget_info";
    private static final String RUNTIME_STATE_PENDING_REQUEST_ARGS = "launcher.request_args";
    private static final String RUNTIME_STATE_PENDING_REQUEST_CODE = "launcher.request_code";
    private static final String RUNTIME_STATE_VIEW_IDS = "launcher.view_ids";
    static final int SCREEN_COUNT = 5;
    private static final int SCRIM_VIEW_ALPHA_CHANNEL_INDEX = 0;
    public static final String SHOW_WEIGHT_WATCHER = "debug.show_mem";
    public static final boolean SHOW_WEIGHT_WATCHER_DEFAULT = false;
    private static final int SYSTEM_UI_FLAG_LIGHT_NAV_BAR = 16;
    public static final String TAG = "Launcher";
    public static final String USER_HAS_MIGRATED = "launcher.user_migrated_from_old_data";
    private static final int WORKSPACE_BACKGROUND_BLACK = 2;
    private static final int WORKSPACE_BACKGROUND_GRADIENT = 0;
    private static final int WORKSPACE_BACKGROUND_TRANSPARENT = 1;
    private static Method sClipRevealMethod;
    private static Method sCustomScaleUpMethod;
    private static PendingAddArguments sPendingAddItem;
    private View mAllAppsButton;
    AllAppsTransitionController mAllAppsController;
    private QuickstepTransitionManager mAppTransitionManager;
    private LauncherAppWidgetHost mAppWidgetHost;
    private AppWidgetManagerCompat mAppWidgetManager;
    private AllAppsHost mAppsCustomizeHost;
    AllAppsContainerView mAppsView;
    private long mAutoAdvanceSentTime;
    private Runnable mCancelTouchController;
    public CarouselLayout mCarouselLayout;
    protected View mDefaultPageButton;
    private boolean mDeferOverlayCallbacks;
    private boolean mDeferredResumePending;
    protected DragController mDragController;
    protected DragLayer mDragLayer;
    public ViewGroup mDynamicGridPanelView;
    public FocusIndicatorView mFocusHandler;
    public View mFolderAnimUseCellLayout;
    private Bitmap mFolderIconBitmap;
    private Canvas mFolderIconCanvas;
    ImageView mFolderIconImageView;
    private View.OnTouchListener mHapticFeedbackTouchListener;
    Hotseat mHotseat;
    private View mHotseatSearchBox;
    protected HotwordServiceWrapper mHotword;
    protected IconCache mIconCache;
    private LayoutInflater mInflater;
    private boolean mIsMirrorMode;
    private boolean mIsSafeModeEnabled;
    private ViewGroup mLGOverviewPanel;
    protected LauncherCallbacks mLauncherCallbacks;
    public LauncherModel mModel;
    private ModelWriter mModelWriter;
    private Configuration mOldConfig;
    private boolean mOnResumeNeedsLoad;
    private OnboardingPrefs mOnboardingPrefs;
    private OneHandOperationManager mOneHandOperationManager;
    protected LauncherOverlayManager mOverlayManager;
    private View mOverviewPanel;
    private View mOverviewPanelContainer;
    private View mPageIndicators;
    private ActivityResultInfo mPendingActivityResult;
    private LauncherAppWidgetProviderInfo mPendingAddWidgetInfo;
    private ViewOnDrawExecutor mPendingExecutor;
    private Intent mPendingIntent;
    private PendingRequestArgs mPendingRequestArgs;
    private PopupDataProvider mPopupDataProvider;
    private AppWidgetHostView mQsb;
    private boolean mRestoring;
    private RotationHelper mRotationHelper;
    private Bundle mSavedInstanceState;
    private Bundle mSavedState;
    private Bundle mSavedStateApps;
    ScrimView mScrimView;
    private SearchDropTargetBar mSearchDropTargetBar;
    private SharedPreferences mSharedPrefs;
    private StateManager<LauncherState> mStateManager;
    LauncherStateTransitionAnimation mStateTransitionAnimation;
    public SwivelContentsView mSwivelContentsView;
    public TPhoneModeReceiver mTPMR;
    ArrayList<AppInfo> mTmpAppsList;
    private boolean mWaitingForResult;
    private BubbleTextView mWaitingForResume;
    protected WallpaperMotionManager mWallpaperMotionManager;
    protected View mWeightWatcher;
    private View mWidgetsButton;
    protected WidgetsModel mWidgetsModel;
    protected WidgetsContainerView mWidgetsView;
    public Workspace mWorkspace;
    Drawable mWorkspaceBackgroundDrawable;
    public Runnable mWorkspaceVisibility;
    public static final ActivityTracker<Launcher> ACTIVITY_TRACKER = new ActivityTracker<>();
    private static boolean folderCloseByHomeKey = false;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static int NEW_APPS_PAGE_MOVE_DELAY = 500;
    private static int NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS = 5;
    static int NEW_APPS_ANIMATION_DELAY = 500;
    protected static LongArrayMap<FolderInfo> sFolders = new LongArrayMap<>();
    static final ArrayList<String> sDumpLogs = new ArrayList<>();
    static Date sDateStamp = new Date();
    static DateFormat sDateFormat = DateFormat.getDateTimeInstance(3, 3);
    static long sRunStart = System.currentTimeMillis();
    protected static HashMap<String, CustomAppWidget> sCustomAppWidgets = new HashMap<>();
    private HashMap<Integer, Integer> mItemIdToViewId = new HashMap<>();
    private final BroadcastReceiver mCloseSystemDialogsReceiver = new CloseSystemDialogsIntentReceiver();
    PendingAddItemInfo mPendingAddInfo = new PendingAddItemInfo();
    private int mPendingAddWidgetId = -1;
    protected int mPendingActivityRequestCode = -1;
    private int[] mTmpAddItemCellCoordinates = new int[2];
    private boolean mAutoAdvanceRunning = false;
    private LauncherState mOnResumeState = null;
    private SpannableStringBuilder mDefaultKeySsb = null;
    boolean mWorkspaceLoading = true;
    protected boolean mPaused = true;
    protected ArrayList<Runnable> mBindOnResumeCallbacks = new ArrayList<>();
    public Runnable mShowOriginalHomeView = null;
    boolean mUserPresent = true;
    private boolean mVisible = false;
    private boolean mAttached = false;
    public int mOrientationOfCurrentLayout = 0;
    private final int ADVANCE_MSG = 1;
    private final int WORKSPACE_LONGPRESS_MSG = 1000;
    private boolean mIsWorkspaceLongPressed = false;
    private final int mAdvanceInterval = 20000;
    private final int mAdvanceStagger = 250;
    private long mAutoAdvanceTimeLeft = -1;
    HashMap<View, AppWidgetProviderInfo> mWidgetsToAdvance = new HashMap<>();
    private boolean dispatchTouchEventDown = false;
    private final int mRestoreScreenOrientationDelay = 500;
    private final ArrayList<Integer> mSynchronouslyBoundPages = new ArrayList<>();
    private Rect mRectForFolderAnimation = new Rect();
    private float mBlurBGPivotX = 0.0f;
    private float mBlurBGPivotY = 0.0f;
    private MultiHashMap<ComponentKey, String> mDeepShortcutMap = new MultiHashMap<>();
    private float mCurrentAssistantVisibility = 0.0f;
    private final Runnable mDeferredOverlayCallbacks = new Runnable() { // from class: com.android.launcher3.-$$Lambda$Launcher$gOgw2lWxIvn_eg_T2ZPXAZTVjqQ
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.checkIfOverlayStillDeferred();
        }
    };
    private boolean mIsPinItemDragging = false;
    private StringCache mStringCache = null;
    Runnable mBuildLayersRunnable = new Runnable() { // from class: com.android.launcher3.Launcher.1
        @Override // java.lang.Runnable
        public void run() {
            if (Launcher.this.mWorkspace != null) {
                Launcher.this.mWorkspace.buildPageHardwareLayers();
            }
        }
    };
    private boolean mRotationEnabled = false;
    private Runnable mUpdateOrientationRunnable = new Runnable() { // from class: com.android.launcher3.Launcher.2
        @Override // java.lang.Runnable
        public void run() {
            Launcher.this.setOrientation();
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.launcher3.Launcher.13
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LGLog.i("Launcher", "[Receiver] onReceive: " + action);
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                Launcher.this.mUserPresent = false;
                AbstractFloatingView.closeOpenViews(Launcher.this, false, 8);
                Launcher.this.updateAutoAdvanceState();
                if (Launcher.this.isInState(LauncherState.CLEAN_VIEW)) {
                    Launcher.this.exitCleanViewMode();
                }
                if (Launcher.this.mAppsCustomizeHost != null) {
                    Launcher.this.mAppsCustomizeHost.reset(Launcher.this.mSavedStateApps);
                }
                if (Launcher.this.isInState(LauncherState.ALL_APPS)) {
                    Launcher.this.mWorkspace.setCheckSwipeUpAppDrawer(false);
                    Launcher.this.mWorkspace.backToWorkspaceFromSwipeUpAppDrawer(false);
                }
                if (Launcher.this.mPendingRequestArgs == null) {
                    Launcher.this.mStateManager.goToState(LauncherState.NORMAL);
                    if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                        Launcher.this.getRotationHelper().setCurrentStateRequest(1);
                    } else {
                        Launcher.this.getRotationHelper().setCurrentStateRequest(0);
                    }
                }
                if (Launcher.this.mWorkspace != null && Launcher.this.mWorkspace.getState() != Workspace.State.NORMAL) {
                    Launcher.this.showWorkspace(false);
                }
                Launcher.this.closeSystemDialogs();
                Launcher.this.onScreenOff();
                return;
            }
            if ("android.intent.action.USER_PRESENT".equals(action)) {
                Launcher.this.mUserPresent = true;
                Launcher.this.updateAutoAdvanceState();
            } else if (LauncherConst.ACTION_CONTROL_START_DUAL_RECENT.equals(action)) {
                if (Launcher.this.isInState(LauncherState.OVERVIEW) || (Launcher.this.mOverviewPanelContainer != null && Launcher.this.mOverviewPanelContainer.getVisibility() == 0)) {
                    Launcher.this.mStateManager.goToState(LauncherState.NORMAL);
                    AbstractFloatingView.closeOpenViews(Launcher.this, true, 2560);
                }
            }
        }
    };
    private final Runnable mHandleDeferredResume = new Runnable() { // from class: com.android.launcher3.-$$Lambda$k1pNtF732CtB0btpghiYI2sWB0U
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.handleDeferredResume();
        }
    };
    final Handler mHandler = new Handler(new Handler.Callback() { // from class: com.android.launcher3.Launcher.16
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            int i = 0;
            if (msg.what == 1) {
                for (View view : Launcher.this.mWidgetsToAdvance.keySet()) {
                    final View viewFindViewById = view.findViewById(Launcher.this.mWidgetsToAdvance.get(view).autoAdvanceViewId);
                    int i2 = i * 250;
                    if (viewFindViewById instanceof Advanceable) {
                        Launcher.this.mHandler.postDelayed(new Runnable() { // from class: com.android.launcher3.Launcher.16.1
                            @Override // java.lang.Runnable
                            public void run() {
                                ((Advanceable) viewFindViewById).advance();
                            }
                        }, i2);
                    }
                    i++;
                }
                Launcher.this.sendAdvanceMessage(20000L);
            } else if (msg.what == 1000 && LGHomeFeature.Config.FEATURE_EDITMODE_LONGPRESS_DELAY.getValue()) {
                if (Launcher.this.isInState(LauncherState.NORMAL)) {
                    LGLog.d("Launcher", "showOverviewMode by WORKSPACE_LONGPRESS_MSG");
                    if (!UninstallModeManager.getInstance(Launcher.this).isInUninstallMode()) {
                        VibratorManager.performHapticFeedback(Launcher.this, 0);
                    }
                    Launcher.this.mWorkspace.setActivePointerIdToInvalid();
                    Launcher.this.showOverviewMode(true);
                } else {
                    LGLog.i("Launcher", "skip showOverviewMode by WORKSPACE_LONGPRESS_MSG. state = " + Launcher.this.getState());
                }
            }
            return true;
        }
    });
    boolean mChangedProfileByMultiWindow = false;
    boolean mChangedProfile = false;
    private Runnable mBindAllApplicationsRunnable = new Runnable() { // from class: com.android.launcher3.Launcher.34
        @Override // java.lang.Runnable
        public void run() {
            Launcher launcher = Launcher.this;
            launcher.bindAllApplications(launcher.mTmpAppsList);
            Launcher.this.mTmpAppsList = null;
        }
    };
    private Runnable mBindPackagesUpdatedRunnable = new Runnable() { // from class: com.android.launcher3.Launcher.43
        @Override // java.lang.Runnable
        public void run() {
            Launcher launcher = Launcher.this;
            launcher.bindAllPackages(launcher.mWidgetsModel);
        }
    };
    public boolean isLongClickFromKeyEnter = false;
    public boolean mSuppressCloseFolder = false;
    private Bundle mMenuSavedState = null;
    protected boolean mNeedToWorkFolderPage = false;

    public interface CustomContentCallbacks {
        boolean isScrollingAllowed();

        void onHide();

        void onScrollProgressChanged(float progress);

        void onShow(boolean fromResume);
    }

    public interface LauncherOverlay {
        void onScrollChange(float progress, boolean rtl);

        void onScrollInteractionBegin();

        void onScrollInteractionEnd(int velocityX);

        void setOverlayCallbacks(LauncherOverlayCallbacks callbacks);
    }

    public interface LauncherOverlayCallbacks {
        float getProgress();

        void onScrollChanged(float progress);
    }

    public interface LauncherSearchCallbacks {
        void onSearchOverlayClosed();

        void onSearchOverlayOpened();
    }

    private void logStopAndResume(int command) {
    }

    private void onStartForResult(int requestCode) {
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void addAppsOnSwivelHome(ArrayList<? extends ItemInfo> apps) {
    }

    protected void disableVoiceButtonProxy(boolean disable) {
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void dumpLogsToLocalData() {
    }

    public void folderConvertedToItem(Folder folder, WorkspaceItemInfo itemInfo) {
    }

    public void folderCreatedFromItem(Folder folder, WorkspaceItemInfo itemInfo) {
    }

    @Override // com.android.launcher3.views.ActivityContext
    public void invalidateParent(ItemInfo info) {
    }

    void lockAllApps() {
    }

    public void onDragLayerHierarchyChanged() {
    }

    public void onScreenOff() {
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public void refreshAndBindWidgetsForPackageUser(PackageUserKey packageUser) {
    }

    public void setOnDeferredActivityLaunchCallback(Runnable callback) {
    }

    public void showOutOfSpaceMessage(boolean isHotseatLayout) {
    }

    public boolean supportsAdaptiveIconAnimation(View clickedView) {
        return false;
    }

    void unlockAllApps() {
    }

    public void useFadeOutAnimationForLauncherStart(CancellationSignal signal) {
    }

    static {
        sClipRevealMethod = null;
        sCustomScaleUpMethod = null;
        try {
            sClipRevealMethod = ActivityOptionsWrapper.class.getDeclaredMethod("makeClipRevealAnimation", View.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            sCustomScaleUpMethod = ActivityOptionsWrapper.class.getDeclaredMethod("makeCustomScaleUpAnimation", View.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE);
        } catch (Exception unused) {
        }
    }

    @Override // com.android.launcher3.InvariantDeviceProfile.OnIDPChangeListener
    public void onIdpChanged(int changeFlags, InvariantDeviceProfile idp) {
        boolean zIsInMultiWindowModeCompat;
        onIdpChanged(idp);
        if ((changeFlags & 4) != 0) {
            this.mDeviceProfile = idp.getDeviceProfile(this);
            zIsInMultiWindowModeCompat = isInMultiWindowModeCompat();
            if (zIsInMultiWindowModeCompat) {
                this.mDeviceProfile = this.mDeviceProfile.getMultiWindowProfile(this, getMultiWindowDisplaySize());
            }
            dispatchDeviceProfileChanged();
        } else {
            zIsInMultiWindowModeCompat = false;
        }
        if ((changeFlags & 2) != 0) {
            this.mIsMirrorMode = true;
        }
        if (this.mDragLayer != null) {
            boolean zNeedToChangeControllers = SysUINavigationMode.INSTANCE.lambda$get$0$MainThreadInitializedObject(this).needToChangeControllers();
            LGLog.i("Launcher", "onIdpChanged : recreateTouchControllers - " + zNeedToChangeControllers);
            if (zNeedToChangeControllers) {
                this.mDragLayer.recreateControllers();
            }
        }
        LGLog.i("Launcher", "[DEVICE_PROFILE] onIdpChanged : " + changeFlags + ", " + this.mIsMirrorMode + ", isMultiWindowMode = " + zIsInMultiWindowModeCompat);
    }

    public void forceOnIdpChanged(InvariantDeviceProfile idp) {
        onIdpChanged(idp);
    }

    private void onIdpChanged(InvariantDeviceProfile idp) {
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            getAllAppsHost().clearOptionMenu();
            AbstractFloatingView.closeOpenViews(this, true, AbstractFloatingView.TYPE_ALL);
        }
        this.mUserEventDispatcher = null;
        initDeviceProfile(idp);
        dispatchDeviceProfileChanged();
        reapplyUi();
        this.mDragLayer.recreateControllers();
        rebindModel();
    }

    static class PendingAddArguments {
        int appWidgetId;
        int cellX;
        int cellY;
        long container;
        Intent intent;
        int requestCode;
        long screenId;

        PendingAddArguments() {
        }
    }

    void setOrientation() {
        if (this.mRotationEnabled) {
            unlockScreenOrientation(true);
        } else {
            setRequestedOrientation(1);
        }
    }

    @Override // com.android.launcher3.BaseDraggingActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.preOnCreate();
        }
        super.onCreate(savedInstanceState);
        StringCache stringCache = new StringCache();
        this.mStringCache = stringCache;
        stringCache.loadStrings(getApplicationContext());
        CustomUIManager.getInstance(getApplicationContext());
        this.mOneHandOperationManager = new OneHandOperationManager(getApplicationContext());
        LauncherAppState launcherAppState = LauncherAppState.getInstance(this);
        this.mOldConfig = new Configuration(getResources().getConfiguration());
        this.mHotword = new HotwordServiceWrapper(this);
        RecentGuideView.init(getApplicationContext());
        WorkGuideView.init(getApplicationContext());
        this.mDeviceProfile = launcherAppState.getInvariantDeviceProfile().getDeviceProfile(this);
        LauncherAppState.getIDP(this).addOnChangeListener(this);
        if (isInMultiWindowModeCompat()) {
            this.mDeviceProfile = this.mDeviceProfile.getMultiWindowProfile(this, getMultiWindowDisplaySize());
        }
        onDeviceProfileInitiated();
        this.mSharedPrefs = Utilities.getPrefs(this);
        this.mIsSafeModeEnabled = getPackageManager().isSafeMode();
        this.mIsMirrorMode = false;
        LauncherModel launcher = launcherAppState.setLauncher(this);
        this.mModel = launcher;
        this.mModelWriter = launcher.getWriter(this.mDeviceProfile.isVerticalBarLayout());
        this.mIconCache = launcherAppState.getIconCache();
        this.mDragController = new DragController(this);
        this.mAllAppsController = new AllAppsTransitionController(this);
        this.mStateManager = new StateManager<>(this, LauncherState.NORMAL);
        this.mOnboardingPrefs = createOnboardingPrefs(this.mSharedPrefs);
        this.mInflater = getLayoutInflater();
        this.mStateTransitionAnimation = new LauncherStateTransitionAnimation(this, this);
        this.mAppWidgetManager = AppWidgetManagerCompat.getInstance(this);
        LauncherAppWidgetHost launcherAppWidgetHostCreateAppWidgetHost = createAppWidgetHost();
        this.mAppWidgetHost = launcherAppWidgetHostCreateAppWidgetHost;
        launcherAppWidgetHostCreateAppWidgetHost.startListening();
        this.mPaused = false;
        inflateRootView(R.layout.launcher);
        setupViews();
        this.mDeviceProfile.layout(this);
        getRootView().dispatchInsets();
        this.mPopupDataProvider = new PopupDataProvider(this);
        this.mRotationHelper = new RotationHelper(this);
        lockAllApps();
        if (ACTIVITY_TRACKER.handleCreate(this) && savedInstanceState != null) {
            savedInstanceState.remove(RUNTIME_STATE);
        }
        this.mSavedState = savedInstanceState;
        restoreState(savedInstanceState);
        this.mStateManager.reapplyState();
        DDTChangeWatcher.getInstance().checkDDTChangedOnCreate(this);
        if (!DDTChangeWatcher.getInstance().isDDTChanged() && !this.mRestoring) {
            this.mModel.startLoader(this.mWorkspace.getRestorePage());
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        this.mDefaultKeySsb = spannableStringBuilder;
        Selection.setSelection(spannableStringBuilder, 0);
        setContentView(getRootView());
        getRootView().dispatchInsets();
        registerReceiver(this.mCloseSystemDialogsReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        this.mRotationEnabled = LGHomeFeature.Config.FEATURE_SUPPORT_LANDSCAPE.getValue();
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            setOrientation();
        }
        LauncherCallbacks launcherCallbacks2 = this.mLauncherCallbacks;
        if (launcherCallbacks2 != null) {
            launcherCallbacks2.onCreate(savedInstanceState);
        }
        this.mOverlayManager = getDefaultOverlay();
        PluginManagerWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).addPluginListener(this, OverlayPlugin.class, false);
        this.mRotationHelper.initialize();
        sendCleanViewDeactivatedIntent(this);
        registerReceiverAtOnCreate();
        NotificationListener.setNotificationsChangedListener(this.mPopupDataProvider);
        this.mStateManager.addStateListener(new StateManager.StateListener<LauncherState>() { // from class: com.android.launcher3.Launcher.3
            /* JADX DEBUG: Method merged with bridge method: onStateTransitionStart(Ljava/lang/Object;)V */
            @Override // com.android.launcher3.statemanager.StateManager.StateListener
            public void onStateTransitionStart(LauncherState toState) {
            }

            /* JADX DEBUG: Method merged with bridge method: onStateTransitionComplete(Ljava/lang/Object;)V */
            @Override // com.android.launcher3.statemanager.StateManager.StateListener
            public void onStateTransitionComplete(LauncherState finalState) {
                float f = 1.0f - Launcher.this.mCurrentAssistantVisibility;
                if (Launcher.this.mAppsCustomizeHost != null) {
                    if (finalState == LauncherState.NORMAL || finalState == LauncherState.OVERVIEW || finalState == LauncherState.OVERVIEW_PEEK) {
                        Launcher.this.mAppsCustomizeHost.getAlphaProperty(1).setValue(f);
                    } else {
                        Launcher.this.mAppsCustomizeHost.getAlphaProperty(1).setValue(1.0f);
                    }
                    boolean zShouldBackButtonBeHidden = Launcher.this.shouldBackButtonBeHidden();
                    if (!Launcher.this.hasWindowFocus() || Launcher.this.getDragLayer() == null) {
                        return;
                    }
                    Launcher.this.getRootView().setDisallowBackGesture(zShouldBackButtonBeHidden);
                }
            }
        });
    }

    protected LauncherAppWidgetHost createAppWidgetHost() {
        return new LauncherAppWidgetHost(this, 1024);
    }

    protected LauncherOverlayManager getDefaultOverlay() {
        return new LauncherOverlayManager() { // from class: com.android.launcher3.Launcher.4
            @Override // com.android.systemui.plugins.shared.LauncherOverlayManager
            public void hideOverlay(boolean animate) {
                if (Launcher.this.mLauncherCallbacks != null) {
                    Launcher.this.mLauncherCallbacks.hideLauncherOverlay(animate);
                }
            }

            @Override // com.android.systemui.plugins.shared.LauncherOverlayManager
            public void hideOverlay(int duration) {
                if (Launcher.this.mLauncherCallbacks != null) {
                    Launcher.this.mLauncherCallbacks.hideLauncherOverlay(duration);
                }
            }
        };
    }

    protected OnboardingPrefs createOnboardingPrefs(SharedPreferences sharedPrefs) {
        return new OnboardingPrefs(this, sharedPrefs);
    }

    public OnboardingPrefs getOnboardingPrefs() {
        return this.mOnboardingPrefs;
    }

    public /* synthetic */ LauncherOverlayManager lambda$onPluginConnected$0$Launcher(OverlayPlugin overlayPlugin) {
        return overlayPlugin.createOverlayManager(this, this);
    }

    /* JADX DEBUG: Method merged with bridge method: onPluginConnected(Lcom/android/systemui/plugins/Plugin;Landroid/content/Context;)V */
    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginConnected(final OverlayPlugin overlayManager, Context context) {
        switchOverlay(new Supplier() { // from class: com.android.launcher3.-$$Lambda$Launcher$LK7w5-wyLbU3rlk4pCxnGT3LL5Y
            @Override // java.util.function.Supplier
            public final Object get() {
                return this.f$0.lambda$onPluginConnected$0$Launcher(overlayManager);
            }
        });
    }

    /* JADX DEBUG: Method merged with bridge method: onPluginDisconnected(Lcom/android/systemui/plugins/Plugin;)V */
    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginDisconnected(OverlayPlugin plugin) {
        switchOverlay(new Supplier() { // from class: com.android.launcher3.-$$Lambda$UvTOFBxhbMbxtAHofQUGh_1G0zo
            @Override // java.util.function.Supplier
            public final Object get() {
                return this.f$0.getDefaultOverlay();
            }
        });
    }

    private void switchOverlay(Supplier<LauncherOverlayManager> overlaySupplier) {
        LauncherOverlayManager launcherOverlayManager = this.mOverlayManager;
        if (launcherOverlayManager != null) {
            launcherOverlayManager.onActivityDestroyed(this);
        }
        this.mOverlayManager = overlaySupplier.get();
        if (getRootView().isAttachedToWindow()) {
            this.mOverlayManager.onAttachedToWindow();
        }
        this.mDeferOverlayCallbacks = true;
        checkIfOverlayStillDeferred();
    }

    private void scheduleDeferredCheck() {
        this.mHandler.removeCallbacks(this.mDeferredOverlayCallbacks);
        Utilities.postAsyncCallback(this.mHandler, this.mDeferredOverlayCallbacks);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIfOverlayStillDeferred() {
        if (this.mDeferOverlayCallbacks) {
            if (!isStarted() || (hasBeenResumed() && !((LauncherState) this.mStateManager.getState()).hasFlag(1))) {
                this.mDeferOverlayCallbacks = false;
                if (isStarted()) {
                    this.mOverlayManager.onActivityStarted(this);
                }
                if (hasBeenResumed()) {
                    this.mOverlayManager.onActivityResumed(this);
                } else {
                    this.mOverlayManager.onActivityPaused(this);
                }
                if (isStarted()) {
                    return;
                }
                this.mOverlayManager.onActivityStopped(this);
            }
        }
    }

    @Override // com.android.launcher3.LauncherProviderChangeListener
    public void onSettingsChanged(String settings, boolean value) {
        if ("pref_allowRotation".equals(settings)) {
            this.mRotationEnabled = value;
            if (waitUntilResume(this.mUpdateOrientationRunnable, true)) {
                return;
            }
            this.mUpdateOrientationRunnable.run();
        }
    }

    @Override // android.app.Activity
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onPostCreate(savedInstanceState);
        }
    }

    public void setLauncherOverlay(LauncherOverlay overlay) {
        if (overlay != null) {
            overlay.setOverlayCallbacks(new LauncherOverlayCallbacksImpl());
        }
        this.mWorkspace.setLauncherOverlay(overlay);
    }

    public boolean setLauncherCallbacks(LauncherCallbacks callbacks) {
        this.mLauncherCallbacks = callbacks;
        callbacks.setLauncherSearchCallback(new LauncherSearchCallbacks() { // from class: com.android.launcher3.Launcher.5
            private boolean mWorkspaceImportanceStored = false;
            private boolean mHotseatImportanceStored = false;
            private int mWorkspaceImportanceForAccessibility = 0;
            private int mHotseatImportanceForAccessibility = 0;

            @Override // com.android.launcher3.Launcher.LauncherSearchCallbacks
            public void onSearchOverlayOpened() {
                if (this.mWorkspaceImportanceStored || this.mHotseatImportanceStored) {
                    return;
                }
                if (Launcher.this.mWorkspace != null) {
                    this.mWorkspaceImportanceForAccessibility = Launcher.this.mWorkspace.getImportantForAccessibility();
                    Launcher.this.mWorkspace.setImportantForAccessibility(4);
                    this.mWorkspaceImportanceStored = true;
                }
                if (Launcher.this.mHotseat != null) {
                    this.mHotseatImportanceForAccessibility = Launcher.this.mHotseat.getImportantForAccessibility();
                    Launcher.this.mHotseat.setImportantForAccessibility(4);
                    this.mHotseatImportanceStored = true;
                }
            }

            @Override // com.android.launcher3.Launcher.LauncherSearchCallbacks
            public void onSearchOverlayClosed() {
                if (this.mWorkspaceImportanceStored && Launcher.this.mWorkspace != null) {
                    Launcher.this.mWorkspace.setImportantForAccessibility(this.mWorkspaceImportanceForAccessibility);
                }
                if (this.mHotseatImportanceStored && Launcher.this.mHotseat != null) {
                    Launcher.this.mHotseat.setImportantForAccessibility(this.mHotseatImportanceForAccessibility);
                }
                this.mWorkspaceImportanceStored = false;
                this.mHotseatImportanceStored = false;
            }
        });
        return true;
    }

    @Override // com.android.launcher3.LauncherProviderChangeListener
    public void onLauncherProviderChange() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onLauncherProviderChange();
        }
    }

    public void updateOverlayBounds(Rect newBounds) {
        this.mAppsView.setSearchBarBounds(newBounds);
        this.mWidgetsView.setSearchBarBounds(newBounds);
    }

    public boolean hasCustomContentToLeft() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            return launcherCallbacks.hasCustomContentToLeft();
        }
        return false;
    }

    protected void populateCustomContentContainer() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.populateCustomContentContainer();
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void invalidateHasCustomContentToLeft() {
        Workspace workspace = this.mWorkspace;
        if (workspace == null || workspace.getScreenOrder().isEmpty()) {
            return;
        }
        if (!this.mWorkspace.hasCustomContent() && hasCustomContentToLeft()) {
            this.mWorkspace.createCustomContentContainer();
            populateCustomContentContainer();
            if (this.mWorkspace.getState() == Workspace.State.OVERVIEW) {
                enablePageAsFullSize(false, this.mDeviceProfile.isVerticalBarLayout());
                return;
            }
            return;
        }
        if (!this.mWorkspace.hasCustomContent() || hasCustomContentToLeft()) {
            return;
        }
        this.mWorkspace.removeCustomContentPage();
    }

    public LayoutInflater getInflater() {
        return this.mInflater;
    }

    public boolean isDraggingEnabled() {
        return !this.mModel.isLoadingWorkspace();
    }

    public static int generateViewId() {
        AtomicInteger atomicInteger;
        int i;
        int i2;
        if (Build.VERSION.SDK_INT >= 17) {
            return View.generateViewId();
        }
        do {
            atomicInteger = sNextGeneratedId;
            i = atomicInteger.get();
            i2 = i + 1;
            if (i2 > 16777215) {
                i2 = 1;
            }
        } while (!atomicInteger.compareAndSet(i, i2));
        return i;
    }

    public int getViewIdForItem(ItemInfo info) {
        int i = (int) info.id;
        if (this.mItemIdToViewId.containsKey(Integer.valueOf(i))) {
            return this.mItemIdToViewId.get(Integer.valueOf(i)).intValue();
        }
        int iGenerateViewId = generateViewId();
        this.mItemIdToViewId.put(Integer.valueOf(i), Integer.valueOf(iGenerateViewId));
        return iGenerateViewId;
    }

    public PopupDataProvider getPopupDataProvider() {
        return this.mPopupDataProvider;
    }

    private long completeAdd(int requestCode, Intent intent, int appWidgetId, PendingRequestArgs info) {
        LauncherAppWidgetInfo launcherAppWidgetInfoCompleteRestoreAppWidget;
        LauncherAppWidgetProviderInfo launcherAppWidgetInfo;
        long jEnsurePendingDropLayoutExists = info.screenId;
        if (info.container == -100) {
            jEnsurePendingDropLayoutExists = ensurePendingDropLayoutExists(info.screenId);
        }
        if (requestCode == 1) {
            completeAddShortcut(intent, info.container, jEnsurePendingDropLayoutExists, info.cellX, info.cellY, info);
        } else if (requestCode == 5) {
            completeAddAppWidget(appWidgetId, info, null, null);
        } else if (requestCode == 12) {
            completeRestoreAppWidget(appWidgetId, 0);
        } else if (requestCode == 14 && (launcherAppWidgetInfoCompleteRestoreAppWidget = completeRestoreAppWidget(appWidgetId, 4)) != null && (launcherAppWidgetInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(appWidgetId)) != null) {
            new WidgetAddFlowHandler(launcherAppWidgetInfo).startConfigActivity(this, launcherAppWidgetInfoCompleteRestoreAppWidget, 12);
        }
        return jEnsurePendingDropLayoutExists;
    }

    private void handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (isWorkspaceLoading()) {
            this.mPendingActivityResult = new ActivityResultInfo(requestCode, resultCode, data);
            return;
        }
        this.mPendingActivityResult = null;
        final PendingRequestArgs pendingRequestArgs = this.mPendingRequestArgs;
        setWaitingForResult(null);
        if (pendingRequestArgs == null) {
            return;
        }
        int widgetId = pendingRequestArgs.getWidgetId();
        Runnable runnable = new Runnable() { // from class: com.android.launcher3.Launcher.6
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.exitSpringLoadedDragModeDelayed(resultCode != 0, 300, null);
            }
        };
        if (requestCode == 11) {
            int intExtra = data != null ? data.getIntExtra("appWidgetId", -1) : -1;
            if (resultCode == 0) {
                completeTwoStageWidgetDrop(0, intExtra, pendingRequestArgs);
                this.mWorkspace.removeExtraEmptyScreenDelayed(true, runnable, 500, false);
                return;
            } else {
                if (resultCode == -1) {
                    addAppWidgetImpl(intExtra, pendingRequestArgs, null, pendingRequestArgs.getWidgetHandler(), 500);
                    return;
                }
                return;
            }
        }
        if (requestCode == 10) {
            if (resultCode == -1 && this.mWorkspace.isInOverviewMode()) {
                Workspace workspace = this.mWorkspace;
                workspace.setCurrentPage(workspace.getPageNearestToCenterOfScreen());
                showWorkspace(false);
                return;
            }
            return;
        }
        boolean z = requestCode == 9 || requestCode == 5;
        isWorkspaceLocked();
        if (!z) {
            if (requestCode == 12 || requestCode == 14) {
                if (resultCode == -1) {
                    completeAdd(requestCode, data, widgetId, pendingRequestArgs);
                    return;
                }
                return;
            }
            if (requestCode == 1) {
                if (resultCode == -1 && pendingRequestArgs.container != -1) {
                    completeAdd(requestCode, data, -1, pendingRequestArgs);
                    this.mWorkspace.removeExtraEmptyScreenDelayed(true, runnable, 500, false);
                } else if (resultCode == 0) {
                    this.mWorkspace.removeExtraEmptyScreenDelayed(true, runnable, 500, false);
                    if (pendingRequestArgs.container == -101) {
                        getHotseat().getLayout().cleanupVacantCell(false);
                    }
                }
            }
            this.mDragLayer.clearAnimatedView();
            return;
        }
        View animatedView = this.mDragLayer.getAnimatedView();
        if (animatedView != null) {
            animatedView.setVisibility(8);
        }
        final int intExtra2 = data != null ? data.getIntExtra("appWidgetId", -1) : -1;
        if (intExtra2 < 0) {
            intExtra2 = widgetId;
        }
        if (intExtra2 < 0 || resultCode == 0) {
            Log.e("Launcher", "Error: appWidgetId (EXTRA_APPWIDGET_ID) was not returned from the widget configuration activity.");
            completeTwoStageWidgetDrop(0, intExtra2, pendingRequestArgs);
            this.mWorkspace.removeExtraEmptyScreenDelayed(true, new Runnable() { // from class: com.android.launcher3.Launcher.7
                @Override // java.lang.Runnable
                public void run() {
                    Launcher.this.exitSpringLoadedDragModeDelayed(false, 0, null);
                }
            }, 500, false);
        } else {
            if (pendingRequestArgs.container == -100) {
                pendingRequestArgs.screenId = ensurePendingDropLayoutExists(pendingRequestArgs.screenId);
            }
            final CellLayout screenWithId = this.mWorkspace.getScreenWithId(pendingRequestArgs.screenId);
            screenWithId.setDropPending(true);
            this.mWorkspace.removeExtraEmptyScreenDelayed(true, new Runnable() { // from class: com.android.launcher3.Launcher.8
                @Override // java.lang.Runnable
                public void run() {
                    Launcher.this.completeTwoStageWidgetDrop(resultCode, intExtra2, pendingRequestArgs);
                    screenWithId.setDropPending(false);
                }
            }, z ? 0 : 500, false);
        }
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        this.mPendingActivityRequestCode = -1;
        handleActivityResult(requestCode, resultCode, data);
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onActivityResult(requestCode, resultCode, data);
        }
        if (this.mDeviceProfile.isMultiWindowMode) {
            getRotationHelper().setCurrentStateRequest(0);
        }
        if (LGHomeFeature.Config.FEATURE_USE_DATA_CONNECTION_DIALOG_VDF.getValue()) {
            VDFDataPopup.runActivityResultDataPopup(requestCode, resultCode, this, this.mPendingIntent);
            this.mPendingIntent = null;
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PendingRequestArgs pendingRequestArgs = this.mPendingRequestArgs;
        if (requestCode == 13 && pendingRequestArgs != null && pendingRequestArgs.getRequestCode() == 13) {
            setWaitingForResult(null);
            CellLayout cellLayout = getCellLayout(pendingRequestArgs.container, pendingRequestArgs.screenId);
            View childAt = cellLayout != null ? cellLayout.getChildAt(pendingRequestArgs.cellX, pendingRequestArgs.cellY) : null;
            Intent pendingIntent = pendingRequestArgs.getPendingIntent();
            if (grantResults.length > 0 && grantResults[0] == 0) {
                lambda$startActivitySafely$4$Launcher(childAt, pendingIntent, (ItemInfo) null);
            } else {
                Toast.makeText(this, getString(R.string.msg_no_phone_permission, new Object[]{getString(R.string.derived_app_name)}), 0).show();
            }
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private PendingAddArguments preparePendingAddArgs(int requestCode, Intent data, int appWidgetId, ItemInfo info) {
        PendingAddArguments pendingAddArguments = new PendingAddArguments();
        pendingAddArguments.requestCode = requestCode;
        pendingAddArguments.intent = data;
        pendingAddArguments.container = info.container;
        pendingAddArguments.screenId = info.screenId;
        pendingAddArguments.cellX = info.cellX;
        pendingAddArguments.cellY = info.cellY;
        pendingAddArguments.appWidgetId = appWidgetId;
        return pendingAddArguments;
    }

    private long ensurePendingDropLayoutExists(long screenId) {
        if (this.mWorkspace.getScreenWithId(screenId) != null) {
            return screenId;
        }
        this.mWorkspace.addExtraEmptyScreen();
        return this.mWorkspace.commitExtraEmptyScreen();
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    void completeTwoStageWidgetDrop(final int resultCode, final int appWidgetId, final PendingRequestArgs requestArgs) {
        int i;
        int i2;
        Runnable runnable;
        AppWidgetHostView appWidgetHostView;
        CellLayout screenWithId = this.mWorkspace.getScreenWithId(requestArgs.screenId);
        if (resultCode == -1) {
            final AppWidgetHostView appWidgetHostViewCreateView = this.mAppWidgetHost.createView((Context) this, appWidgetId, requestArgs.getWidgetHandler().getProviderInfo(this));
            i2 = 3;
            appWidgetHostView = appWidgetHostViewCreateView;
            runnable = new Runnable() { // from class: com.android.launcher3.Launcher.9
                @Override // java.lang.Runnable
                public void run() {
                    Launcher.this.completeAddAppWidget(appWidgetId, requestArgs, appWidgetHostViewCreateView, null);
                    Launcher.this.exitSpringLoadedDragModeDelayed(resultCode != 0, 300, null);
                    if (appWidgetHostViewCreateView != null) {
                        Animation animationLoadAnimation = AnimationUtils.loadAnimation(Launcher.this.getApplicationContext(), R.anim.fade_in_widget);
                        animationLoadAnimation.setDuration(Launcher.this.getResources().getInteger(R.integer.config_completedWidgetFadeInAnimDuration));
                        appWidgetHostViewCreateView.startAnimation(animationLoadAnimation);
                    }
                }
            };
        } else {
            if (resultCode == 0) {
                this.mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                i = 4;
            } else {
                i = 0;
            }
            i2 = i;
            runnable = null;
            appWidgetHostView = null;
        }
        if (this.mDragLayer.getAnimatedView() != null) {
            this.mWorkspace.animateWidgetDrop(requestArgs, screenWithId, (DragView) this.mDragLayer.getAnimatedView(), runnable, i2, appWidgetHostView, true);
        } else if (runnable != null) {
            runnable.run();
        }
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity, com.android.launcher3.BaseActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        LGLog.i("Launcher", "Launcher.onStop");
        RecentsView recentsView = (RecentsView) getOverviewPanel();
        if (recentsView != null && ((TaskView) recentsView.getChildAt(0)) != null) {
            ((TaskView) recentsView.getChildAt(0)).checkFocusableFlag();
        }
        isInState(LauncherState.ALL_APPS);
        AllAppsHost allAppsHost = this.mAppsCustomizeHost;
        if (allAppsHost != null) {
            allAppsHost.onStop();
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onStop();
        }
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            SwivelHomeGuideManager.getInstance(getApplicationContext()).hideGuide();
        }
        logStopAndResume(5);
        NotificationListener.removeNotificationsChangedListener();
    }

    @Override // com.android.launcher3.BaseDraggingActivity, com.android.launcher3.BaseActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onStart();
        }
        this.mAppWidgetHost.setListenIfResumed(true);
        NotificationListener.refreshNotificationsChangedListener();
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    protected void onDeferredResumed() {
        super.onDeferredResumed();
        logStopAndResume(7);
        getUserEventDispatcher().startSession();
        AppLaunchTracker.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).onReturnedToHome();
        InstallShortcutReceiver.disableAndFlushInstallQueue(this);
        this.mModel.refreshShortcutsIfRequired();
        NotificationListener.setNotificationsChangedListener(this.mPopupDataProvider);
    }

    public void deferOverlayCallbacksUntilNextResumeOrStop() {
        this.mDeferOverlayCallbacks = true;
    }

    public LauncherOverlayManager getOverlayManager() {
        return this.mOverlayManager;
    }

    /* JADX DEBUG: Method merged with bridge method: onStateSetStart(Lcom/android/launcher3/statemanager/BaseState;)V */
    @Override // com.android.launcher3.statemanager.StatefulActivity
    public void onStateSetStart(LauncherState state) {
        super.onStateSetStart(state);
        if (state.hasFlag(LauncherState.FLAG_CLOSE_POPUPS)) {
            AbstractFloatingView.closeAllOpenViews(this, false);
        }
        if (state.hasFlag(LauncherState.FLAG_HIDE_FREEFORM_POPUPS)) {
            WindowUtils.addFlagForFreeform(getWindow(), true);
        } else {
            WindowUtils.addFlagForFreeform(getWindow(), false);
        }
    }

    /* JADX DEBUG: Method merged with bridge method: onStateSetEnd(Lcom/android/launcher3/statemanager/BaseState;)V */
    @Override // com.android.launcher3.statemanager.StatefulActivity
    public void onStateSetEnd(LauncherState state) {
        super.onStateSetEnd(state);
        if (getAppWidgetHost() != null) {
            getAppWidgetHost().setResumed(state == LauncherState.NORMAL);
        }
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity, com.android.launcher3.BaseDraggingActivity, com.android.launcher3.BaseActivity, android.app.Activity
    protected void onResume() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.preOnResume();
        }
        super.onResume();
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && this.mDeviceProfile.isMultiWindowMode) {
            if (getRotationHelper().getCurrentStateRequest() == 1 && this.mWorkspace.getOpenFolder() == null) {
                this.mStateManager.refreshState(LauncherState.OVERVIEW);
            } else {
                getRotationHelper().setCurrentStateRequest(0);
            }
        }
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && this.mOldConfig.windowConfiguration.getWindowingMode() != 4 && (isInState(LauncherState.NORMAL) || isInState(LauncherState.ALL_APPS))) {
            getRotationHelper().setCurrentStateRequest(0);
        }
        this.mHandler.removeCallbacks(this.mHandleDeferredResume);
        Utilities.postAsyncCallback(this.mHandler, this.mHandleDeferredResume);
        if (this.mOnResumeState != LauncherState.NORMAL) {
            if (this.mOnResumeState == LauncherState.ALL_APPS) {
                showAllAppsView(false, false, !(this.mWaitingForResume != null), false);
            } else if (this.mOnResumeState == LauncherState.WIDGETS) {
                showWidgetsView(false, false);
            }
        }
        this.mOnResumeState = null;
        this.mPaused = false;
        if (this.mRestoring || this.mOnResumeNeedsLoad) {
            if (!DDTChangeWatcher.getInstance().isDDTChanged()) {
                setWorkspaceLoading(true);
                this.mModel.startLoader(com.lge.launcher3.PagedView.INVALID_RESTORE_PAGE);
            }
            this.mRestoring = false;
            this.mOnResumeNeedsLoad = false;
        }
        DDTChangeWatcher.getInstance().clearDDTChanged();
        LGLog.i("Launcher", "mBindOnResumeCallbacks.size() : " + this.mBindOnResumeCallbacks.size());
        if (this.mBindOnResumeCallbacks.size() > 0) {
            for (int i = 0; i < this.mBindOnResumeCallbacks.size(); i++) {
                this.mBindOnResumeCallbacks.get(i).run();
            }
            this.mBindOnResumeCallbacks.clear();
        }
        BubbleTextView bubbleTextView = this.mWaitingForResume;
        if (bubbleTextView != null) {
            bubbleTextView.setStayPressed(false);
        }
        getWorkspace().reinflateWidgetsIfNecessary();
        reinflateQSBIfNecessary();
        if (this.mWorkspace.getCustomContentCallbacks() != null && this.mWorkspace.isOnOrMovingToCustomContent()) {
            this.mWorkspace.getCustomContentCallbacks().onShow(true);
        }
        updateInteraction(Workspace.State.NORMAL, this.mWorkspace.getState());
        this.mWorkspace.onResume();
        if (!isWorkspaceLoading()) {
            this.mModel.refreshShortcutsIfRequired();
        }
        AllAppsHost allAppsHost = this.mAppsCustomizeHost;
        if (allAppsHost != null) {
            allAppsHost.onResume((LauncherState) this.mStateManager.getState());
            if (this.mAppsCustomizeHost.getScaleX() < 1.0f || this.mAppsCustomizeHost.getScaleY() < 1.0f || this.mAppsCustomizeHost.getAlpha() < 1.0f) {
                allAppsReset();
            }
            if (!isInState(LauncherState.ALL_APPS) && this.mAppsCustomizeHost.getVisibility() == 0) {
                LGLog.w("Launcher", "change visibility of allapps because hotseat touch issue", new int[0]);
                setAllAppsAlphaAndVisibility(4);
            }
        }
        LauncherCallbacks launcherCallbacks2 = this.mLauncherCallbacks;
        if (launcherCallbacks2 != null) {
            launcherCallbacks2.onResume();
        }
        if (this.mHotword.mFolderOpened && getDeviceProfile().isTablet) {
            lockScreenOrientation();
        }
        if (this.mDeferOverlayCallbacks) {
            scheduleDeferredCheck();
        } else {
            this.mOverlayManager.onActivityResumed(this);
        }
    }

    @Override // android.app.Activity
    public void onTopResumedActivityChanged(boolean isTopResumedActivity) {
        super.onTopResumedActivityChanged(isTopResumedActivity);
        LGLog.i("Launcher", "onTopResumedActivityChanged state = " + isTopResumedActivity);
        if (isTopResumedActivity) {
            if (isWorkspaceLoading()) {
                return;
            }
            InstallShortcutReceiver.disableAndFlushInstallQueue(this);
            InstallShortcutReceiver.unblockAndFlushInstallQueueSwivel(this);
            this.mModel.refreshShortcutsIfRequired();
            return;
        }
        InstallShortcutReceiver.enableInstallQueue();
        InstallShortcutReceiver.blockInstallQueueSwivel();
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        this.mPaused = true;
        this.mDragController.cancelDrag();
        this.mDragController.resetLastGestureUpTime();
        getSearchBar().mDropTargetBar.setAlpha(0.0f);
        AbstractFloatingView.closeOpenViews(this, false, 2056);
        if (this.mWorkspace.getCustomContentCallbacks() != null) {
            this.mWorkspace.getCustomContentCallbacks().onHide();
        }
        if (!this.mDeferOverlayCallbacks) {
            this.mOverlayManager.onActivityPaused(this);
        }
        AllAppsHost allAppsHost = this.mAppsCustomizeHost;
        if (allAppsHost != null) {
            allAppsHost.onPause();
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onPause();
        }
        Workspace workspace = this.mWorkspace;
        Folder openFolder = workspace != null ? workspace.getOpenFolder() : null;
        if (openFolder == null || !openFolder.isEditingName()) {
            return;
        }
        openFolder.dismissEditingName();
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        boolean zShouldBackButtonBeHidden = shouldBackButtonBeHidden();
        if (hasFocus && getDragLayer() != null) {
            getRootView().setDisallowBackGesture(zShouldBackButtonBeHidden);
        }
        AllAppsHost allAppsHost = this.mAppsCustomizeHost;
        if (allAppsHost != null) {
            allAppsHost.setCustomFocus(hasFocus);
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onWindowFocusChanged(hasFocus);
        }
    }

    public boolean shouldBackButtonBeHidden() {
        boolean z = SysUINavigationMode.getMode(this).hasGestures && ((LauncherState) getStateManager().getState()).hasFlag(LauncherState.FLAG_HIDE_BACK_BUTTON) && this.mWorkspace.getState() == Workspace.State.NORMAL && hasWindowFocus() && (getActivityFlags() & 64) == 0;
        if (z) {
            return AbstractFloatingView.getTopOpenViewWithType(this, 3607) == null;
        }
        return z;
    }

    class LauncherOverlayCallbacksImpl implements LauncherOverlayCallbacks {
        public float progress = 0.0f;

        LauncherOverlayCallbacksImpl() {
        }

        @Override // com.android.launcher3.Launcher.LauncherOverlayCallbacks
        public void onScrollChanged(float progress) {
            this.progress = progress;
            if (Launcher.this.mWorkspace != null) {
                Launcher.this.mWorkspace.onOverlayScrollChanged(progress);
            }
        }

        @Override // com.android.launcher3.Launcher.LauncherOverlayCallbacks
        public float getProgress() {
            LGLog.d("Launcher", "getProgress = " + this.progress);
            return this.progress;
        }
    }

    protected boolean hasSettings() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            return launcherCallbacks.hasSettings();
        }
        return Utilities.isAtLeastO() || !LGHomeFeature.Config.FEATURE_SUPPORT_LANDSCAPE.getValue();
    }

    public void addToCustomContentPage(View customContent, CustomContentCallbacks callbacks, String description) {
        this.mWorkspace.addToCustomContentPage(customContent, callbacks, description);
    }

    public int getTopOffsetForCustomContent() {
        return this.mWorkspace.getPaddingTop();
    }

    @Override // android.app.Activity
    public Object onRetainNonConfigurationInstance() {
        if (this.mModel.isCurrentCallbacks(this)) {
            this.mModel.stopLoader();
        }
        return Boolean.TRUE;
    }

    private boolean acceptFilter() {
        return !((InputMethodManager) getApplicationContext().getSystemService("input_method")).isFullscreenMode();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        SpannableStringBuilder spannableStringBuilder;
        int unicodeChar = event.getUnicodeChar();
        boolean zOnKeyDown = super.onKeyDown(keyCode, event);
        boolean z = unicodeChar > 0 && !Character.isWhitespace(unicodeChar);
        if (!zOnKeyDown && acceptFilter() && z && TextKeyListener.getInstance().onKeyDown(this.mWorkspace, this.mDefaultKeySsb, keyCode, event) && (spannableStringBuilder = this.mDefaultKeySsb) != null && spannableStringBuilder.length() > 0) {
            return onSearchRequested();
        }
        if (keyCode == 82 && event.isLongPress()) {
            return true;
        }
        return zOnKeyDown;
    }

    private String getTypedText() {
        return this.mDefaultKeySsb.toString();
    }

    private void clearTypedText() {
        SpannableStringBuilder spannableStringBuilder = this.mDefaultKeySsb;
        if (spannableStringBuilder != null) {
            spannableStringBuilder.clear();
            this.mDefaultKeySsb.clearSpans();
            Selection.setSelection(this.mDefaultKeySsb, 0);
        }
    }

    private static LauncherState intToState(int stateOrdinal) {
        LauncherState launcherState = LauncherState.NORMAL;
        LauncherState[] launcherStateArrValues = LauncherState.values();
        for (int i = 0; i < launcherStateArrValues.length; i++) {
            if (launcherStateArrValues[i].ordinal == stateOrdinal) {
                return launcherStateArrValues[i];
            }
        }
        return launcherState;
    }

    private void restoreState(Bundle savedState) {
        if (savedState == null) {
            return;
        }
        intToState(savedState.getInt(RUNTIME_STATE, LauncherState.NORMAL.ordinal));
        int i = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN, com.lge.launcher3.PagedView.INVALID_RESTORE_PAGE);
        if (i != -1001) {
            this.mWorkspace.setRestorePage(i);
        }
        LGLog.i("Launcher", "Restore current screen = " + i);
        long j = savedState.getLong(RUNTIME_STATE_PENDING_ADD_CONTAINER, -1L);
        long j2 = savedState.getLong(RUNTIME_STATE_PENDING_ADD_SCREEN, -1L);
        if (j != -1 && j2 > -1) {
            this.mPendingAddInfo.container = j;
            this.mPendingAddInfo.screenId = j2;
            this.mPendingAddInfo.cellX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_X);
            this.mPendingAddInfo.cellY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_Y);
            this.mPendingAddInfo.spanX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_X);
            this.mPendingAddInfo.spanY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y);
            this.mPendingAddInfo.componentName = (ComponentName) savedState.getParcelable(RUNTIME_STATE_PENDING_ADD_COMPONENT);
            AppWidgetProviderInfo appWidgetProviderInfo = (AppWidgetProviderInfo) savedState.getParcelable(RUNTIME_STATE_PENDING_ADD_WIDGET_INFO);
            this.mPendingAddWidgetInfo = appWidgetProviderInfo == null ? null : LauncherAppWidgetProviderInfo.fromProviderInfo(this, appWidgetProviderInfo);
            this.mPendingAddWidgetId = savedState.getInt(RUNTIME_STATE_PENDING_ADD_WIDGET_ID);
            setWaitingForResult(null);
            this.mRestoring = true;
        }
        this.mPendingActivityRequestCode = savedState.getInt(RUNTIME_STATE_PENDING_REQUEST_CODE);
        this.mItemIdToViewId = (HashMap) savedState.getSerializable(RUNTIME_STATE_VIEW_IDS);
        if (this.mAppsCustomizeHost == null) {
            this.mMenuSavedState = savedState;
        }
    }

    protected void setupViews() {
        Drawable drawable;
        DragController dragController = this.mDragController;
        this.mFocusHandler = (FocusIndicatorView) findViewById(R.id.focus_indicator);
        DragLayer dragLayer = (DragLayer) findViewById(R.id.drag_layer);
        this.mDragLayer = dragLayer;
        Workspace workspace = (Workspace) dragLayer.findViewById(R.id.workspace);
        this.mWorkspace = workspace;
        workspace.setPageSwitchListener(this);
        View viewFindViewById = findViewById(R.id.overview_panel);
        this.mOverviewPanel = viewFindViewById;
        ((RecentsView) viewFindViewById).setRecommandAppLayout((RecommandAppLayout) findViewById(R.id.recommand_container));
        ((RecentsView) this.mOverviewPanel).setClearAllButton(findViewById(R.id.clear_all_button));
        this.mHotseatSearchBox = findViewById(R.id.search_container_hotseat);
        this.mPageIndicators = this.mDragLayer.findViewById(R.id.page_indicator);
        if (!LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR.getValue() && LGHomeFeature.Config.FEATURE_USE_WORKSPACE_BG.getValue()) {
            boolean z = LGHomeFeature.Config.FEATURE_USE_NATIVE_WORKSPACE_BG.getValue() || com.lge.launcher3.util.Utilities.LOW_CONDITION;
            LGLog.i("Launcher", "check workspace_native_bg : " + z + ", condition = " + com.lge.launcher3.util.Utilities.LOW_CONDITION_LEVEL);
            if (z) {
                drawable = getResources().getDrawable(R.drawable.workspace_native_bg);
            } else {
                drawable = getResources().getDrawable(R.drawable.workspace_bg);
            }
            this.mWorkspaceBackgroundDrawable = drawable;
        } else {
            LGLog.d("Launcher", "a WorkspaceDrawable is not used");
            this.mWorkspaceBackgroundDrawable = new ColorDrawable(0);
        }
        setWorkspaceBG(AdaptiveTextUtil.getAdaptiveTextColor(getBaseContext()) == getResources().getColor(R.color.workspace_adaptive_color2));
        setWorkspaceBackground(0);
        this.mDragLayer.setup(this, dragController);
        Hotseat hotseat = (Hotseat) findViewById(R.id.hotseat);
        this.mHotseat = hotseat;
        if (hotseat != null) {
            hotseat.setHapticFeedbackEnabled(false);
            this.mHotseat.setOnLongClickListener(this);
        }
        this.mLGOverviewPanel = (ViewGroup) findViewById(R.id.lg_overview_panel);
        View viewFindViewById2 = findViewById(R.id.widget_button);
        this.mWidgetsButton = viewFindViewById2;
        viewFindViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.Launcher.10
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                if (Launcher.this.mWorkspace.isSwitchingState()) {
                    return;
                }
                Launcher.this.onClickAddWidgetButton(arg0);
            }
        });
        findViewById(R.id.wallpaper_button).setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.Launcher.11
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                if (Launcher.this.mWorkspace.isSwitchingState()) {
                    return;
                }
                Launcher.this.onClickWallpaperPicker(arg0);
            }
        });
        View viewFindViewById3 = findViewById(LGFeatureConfig.FEATURE_OPERATOR.equals("VZW") ? R.id.settings_button_vzw : R.id.settings_button);
        ((ViewGroup) viewFindViewById3.getParent()).setVisibility(0);
        if (hasSettings()) {
            viewFindViewById3.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.Launcher.12
                @Override // android.view.View.OnClickListener
                public void onClick(View arg0) {
                    if (Launcher.this.mWorkspace.isSwitchingState()) {
                        return;
                    }
                    Launcher.this.onClickSettingsButton(arg0);
                }
            });
        } else {
            viewFindViewById3.setVisibility(8);
        }
        this.mLGOverviewPanel.setAlpha(0.0f);
        this.mWorkspace.setHapticFeedbackEnabled(false);
        this.mWorkspace.setOnLongClickListener(this);
        LGLog.i("Launcher", "setupViews() - mDragController = " + this.mDragController);
        this.mWorkspace.setup(dragController);
        dragController.addDragListener(this.mWorkspace);
        this.mSearchDropTargetBar = (SearchDropTargetBar) this.mDragLayer.findViewById(R.id.search_drop_target_bar);
        this.mWidgetsView = (WidgetsContainerView) findViewById(R.id.widgets_view);
        if (this.mAppsView != null) {
            LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
            if (launcherCallbacks != null && launcherCallbacks.getAllAppsSearchBarController() != null) {
                this.mAppsView.setSearchBarController(this.mLauncherCallbacks.getAllAppsSearchBarController());
            } else {
                AllAppsContainerView allAppsContainerView = this.mAppsView;
                allAppsContainerView.setSearchBarController(allAppsContainerView.newDefaultAppSearchController());
            }
        }
        dragController.setDragScoller(this.mWorkspace);
        dragController.setScrollView(this.mDragLayer);
        dragController.setMoveTarget(this.mWorkspace);
        dragController.addDropTarget(this.mWorkspace);
        SearchDropTargetBar searchDropTargetBar = this.mSearchDropTargetBar;
        if (searchDropTargetBar != null) {
            searchDropTargetBar.setup(this, dragController);
            this.mSearchDropTargetBar.setQsbSearchBar(getOrCreateQsbBar());
        }
        if (getResources().getBoolean(R.bool.debug_memory_enabled)) {
            Log.v("Launcher", "adding WeightWatcher");
            WeightWatcher weightWatcher = new WeightWatcher(this);
            this.mWeightWatcher = weightWatcher;
            weightWatcher.setAlpha(0.5f);
            getRootView().addView(this.mWeightWatcher, new FrameLayout.LayoutParams(-1, -2, 80));
            this.mWeightWatcher.setVisibility(shouldShowWeightWatcher() ? 0 : 8);
        }
        loadMenuStub();
        this.mAllAppsController.setupViews(this.mAppsCustomizeHost);
    }

    public void setAllAppsButton(View allAppsButton) {
        this.mAllAppsButton = allAppsButton;
    }

    public View getAllAppsButton() {
        return this.mAllAppsButton;
    }

    public View getWidgetsButton() {
        return this.mWidgetsButton;
    }

    public View createShortcut(ShortcutInfo info) {
        Workspace workspace = this.mWorkspace;
        return createShortcut((ViewGroup) workspace.getChildAt(workspace.getCurrentPage()), info);
    }

    public View createShortcut(ViewGroup parent, ShortcutInfo info) {
        return createShortcut(parent, info, 0);
    }

    public View createShortcut(ViewGroup parent, ShortcutInfo info, int resId) {
        if (info.hasPhotoIcon() && info.hasLargeIcon()) {
            resId = R.layout.app_photo_icon;
        } else if (resId == 0) {
            resId = R.layout.app_icon;
        }
        BubbleTextView bubbleTextView = (BubbleTextView) this.mInflater.inflate(resId, parent, false);
        bubbleTextView.applyFromShortcutInfo(info, this.mIconCache);
        bubbleTextView.setCompoundDrawablePadding(this.mDeviceProfile.iconDrawablePaddingPx);
        bubbleTextView.setOnClickListener(this);
        bubbleTextView.setOnFocusChangeListener(this.mFocusHandler);
        if (this.mDeviceProfile != null && this.mDeviceProfile.isLandscape && !this.mDeviceProfile.allowRotation) {
            LGLog.i("Launcher", "createShortcut - set app_icon for portrait. mDeviceProfile.isLandscape = " + this.mDeviceProfile.isLandscape + ", mDeviceProfile.allowRotation = " + this.mDeviceProfile.allowRotation);
            bubbleTextView.setPadding(0, bubbleTextView.getPaddingTop(), bubbleTextView.getPaddingRight(), bubbleTextView.getPaddingBottom());
            bubbleTextView.setPaddingRelative(0, bubbleTextView.getPaddingTop(), bubbleTextView.getPaddingEnd(), bubbleTextView.getPaddingBottom());
            bubbleTextView.setGravity(49);
            bubbleTextView.setSingleLine(false);
            bubbleTextView.setCompoundDrawablePadding(bubbleTextView.getResources().getDimensionPixelSize(R.dimen.device_profile_app_icon_drawable_padding));
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) bubbleTextView.getLayoutParams();
            if (marginLayoutParams != null) {
                marginLayoutParams.topMargin = bubbleTextView.getResources().getDimensionPixelSize(R.dimen.workspace_icon_margin_top);
            } else {
                LGLog.w("Launcher", "favorite layoutparams is null.", new int[0]);
            }
        }
        UninstallModeManager.getInstance(this).setUninstallTypeForBadgeView(bubbleTextView);
        return bubbleTextView;
    }

    public PendingRequestArgs getPendingRequestArgs(Intent intent) {
        PinItemRequestCompat pinItemRequest;
        if (!Utilities.isAtLeastO() || (pinItemRequest = PinItemRequestCompat.getPinItemRequest(intent)) == null || pinItemRequest.getRequestType() != 1) {
            return null;
        }
        PendingAddShortcutInfo pendingAddShortcutInfo = new PendingAddShortcutInfo(new PinShortcutRequestActivityInfo(pinItemRequest, getApplicationContext()));
        return PendingRequestArgs.forIntent(1, new Intent("android.intent.action.CREATE_SHORTCUT").setComponent(pendingAddShortcutInfo.componentName), pendingAddShortcutInfo);
    }

    protected void completeAddShortcut(Intent data, long container, long screenId, int cellX, int cellY, PendingRequestArgs args) {
        completeAddShortcut(data, container, screenId, cellX, cellY, args, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v10 */
    /* JADX WARN: Type inference failed for: r11v2, types: [boolean] */
    /* JADX WARN: Type inference failed for: r11v7 */
    /* JADX WARN: Type inference failed for: r11v8 */
    /* JADX WARN: Type inference failed for: r11v9 */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v13 */
    /* JADX WARN: Type inference failed for: r1v17 */
    /* JADX WARN: Type inference failed for: r1v18 */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r24v0, types: [android.content.Context, com.android.launcher3.Launcher] */
    /* JADX WARN: Type inference failed for: r9v10 */
    /* JADX WARN: Type inference failed for: r9v11 */
    /* JADX WARN: Type inference failed for: r9v3, types: [android.view.View, com.android.launcher3.CellLayout] */
    /* JADX WARN: Type inference failed for: r9v8 */
    /* JADX WARN: Type inference failed for: r9v9 */
    public void completeAddShortcut(Intent intent, long j, long j2, int i, int i2, PendingRequestArgs pendingRequestArgs, boolean z) {
        View view;
        char c;
        ShortcutInfo shortcutInfo;
        ?? r11;
        ?? r9;
        ?? FindCellForSpan;
        boolean z2;
        int[] iArr = this.mTmpAddItemCellCoordinates;
        CellLayout cellLayout = getCellLayout(j, j2);
        if (pendingRequestArgs.getRequestCode() != 1 || pendingRequestArgs.getPendingIntent().getComponent() == null) {
            return;
        }
        ShortcutInfo shortcutInfoCreateShortcutInfoFromPinItemRequest = (z && Utilities.isAtLeastO()) ? LauncherAppsCompat.createShortcutInfoFromPinItemRequest(this, PinItemRequestCompat.getPinItemRequest(intent), 0L) : null;
        if (shortcutInfoCreateShortcutInfoFromPinItemRequest == null) {
            shortcutInfoCreateShortcutInfoFromPinItemRequest = Process.myUserHandle().equals(pendingRequestArgs.user) ? InstallShortcutReceiver.fromShortcutIntent(this, intent) : null;
            if (shortcutInfoCreateShortcutInfoFromPinItemRequest == null) {
                if (Utilities.isAtLeastO()) {
                    shortcutInfoCreateShortcutInfoFromPinItemRequest = LauncherAppsCompat.createShortcutInfoFromPinItemRequest(this, PinItemRequestCompat.getPinItemRequest(intent), 0L);
                }
                if (shortcutInfoCreateShortcutInfoFromPinItemRequest == null) {
                    Log.e("Launcher", "Unable to parse a valid custom shortcut result");
                    return;
                }
            } else if (!new PackageManagerHelper(this).hasPermissionForActivity(shortcutInfoCreateShortcutInfoFromPinItemRequest.intent, pendingRequestArgs.getPendingIntent().getComponent().getPackageName())) {
                Log.e("Launcher", "Ignoring malicious intent " + shortcutInfoCreateShortcutInfoFromPinItemRequest.intent.toUri(0));
                return;
            }
        }
        ShortcutInfo shortcutInfo2 = shortcutInfoCreateShortcutInfoFromPinItemRequest;
        View viewCreateShortcut = createShortcut(shortcutInfo2);
        if (i >= 0 && i2 >= 0) {
            iArr[0] = i;
            iArr[1] = i2;
            if (cellLayout.isHotseat()) {
                cellLayout.cleanupVacantCell(false);
            }
            view = viewCreateShortcut;
            if (this.mWorkspace.createUserFolderIfNecessary(viewCreateShortcut, j, cellLayout, iArr, 0.0f, true, null, null)) {
                return;
            }
            DropTarget.DragObject dragObject = new DropTarget.DragObject();
            shortcutInfo = shortcutInfo2;
            dragObject.dragInfo = shortcutInfo;
            if (this.mWorkspace.addToExistingFolderIfNecessary(view, cellLayout, iArr, 0.0f, dragObject, true)) {
                return;
            }
            CellLayout cellLayout2 = cellLayout;
            if (cellLayout.isHotseat() || !cellLayout2.isOccupied(i, i2)) {
                c = 1;
                z2 = false;
            } else {
                LGLog.i("Launcher", "completeAddShortcut(): Occupied cellX=" + i + ", cellY=" + i2);
                int[] iArr2 = new int[2];
                cellLayout2.cellToCenterPoint(i, i2, iArr2);
                z2 = false;
                r11 = 0;
                c = 1;
                if (cellLayout2.findNearestVacantArea(iArr2[0], iArr2[1], 1, 1, iArr) == null) {
                    FindCellForSpan = 0;
                    r9 = cellLayout2;
                }
            }
            FindCellForSpan = c;
            r9 = cellLayout2;
            r11 = z2;
        } else {
            view = viewCreateShortcut;
            boolean z3 = false;
            c = 1;
            shortcutInfo = shortcutInfo2;
            CellLayout cellLayout3 = cellLayout;
            if (cellLayout3.isHotseat()) {
                FindCellForSpan = cellLayout3.canAddVacantCell();
                r9 = cellLayout3;
                r11 = z3;
            } else {
                FindCellForSpan = cellLayout3.findCellForSpan(iArr, 1, 1);
                r9 = cellLayout3;
                r11 = z3;
            }
        }
        if (FindCellForSpan == 0) {
            showOutOfSpaceMessage(isHotseatLayout(r9));
            return;
        }
        getModelWriter().addItemToDatabase(shortcutInfo, j, j2, iArr[r11], iArr[c]);
        this.mWorkspace.addInScreen(view, shortcutInfo);
        if (this.mRestoring || !r9.isHotseat()) {
            return;
        }
        r9.cleanupVacantCell(r11);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00df A[PHI: r9 r12 r13 r15
      0x00df: PHI (r9v9 com.android.launcher3.ShortcutInfo) = 
      (r9v4 com.android.launcher3.ShortcutInfo)
      (r9v13 com.android.launcher3.ShortcutInfo)
      (r9v18 com.android.launcher3.ShortcutInfo)
     binds: [B:37:0x00fb, B:33:0x00e8, B:28:0x00dd] A[DONT_GENERATE, DONT_INLINE]
      0x00df: PHI (r12v3 char) = (r12v0 char), (r12v5 char), (r12v6 char) binds: [B:37:0x00fb, B:33:0x00e8, B:28:0x00dd] A[DONT_GENERATE, DONT_INLINE]
      0x00df: PHI (r13v5 char) = (r13v1 char), (r13v8 char), (r13v9 char) binds: [B:37:0x00fb, B:33:0x00e8, B:28:0x00dd] A[DONT_GENERATE, DONT_INLINE]
      0x00df: PHI (r15v4 com.android.launcher3.CellLayout) = 
      (r15v0 com.android.launcher3.CellLayout)
      (r15v8 com.android.launcher3.CellLayout)
      (r15v9 com.android.launcher3.CellLayout)
     binds: [B:37:0x00fb, B:33:0x00e8, B:28:0x00dd] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00e1 A[PHI: r9 r12 r13 r15
      0x00e1: PHI (r9v5 com.android.launcher3.ShortcutInfo) = (r9v4 com.android.launcher3.ShortcutInfo), (r9v18 com.android.launcher3.ShortcutInfo) binds: [B:37:0x00fb, B:28:0x00dd] A[DONT_GENERATE, DONT_INLINE]
      0x00e1: PHI (r12v1 char) = (r12v0 char), (r12v6 char) binds: [B:37:0x00fb, B:28:0x00dd] A[DONT_GENERATE, DONT_INLINE]
      0x00e1: PHI (r13v2 char) = (r13v1 char), (r13v9 char) binds: [B:37:0x00fb, B:28:0x00dd] A[DONT_GENERATE, DONT_INLINE]
      0x00e1: PHI (r15v1 com.android.launcher3.CellLayout) = (r15v0 com.android.launcher3.CellLayout), (r15v9 com.android.launcher3.CellLayout) binds: [B:37:0x00fb, B:28:0x00dd] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Type inference failed for: r0v7, types: [boolean] */
    /* JADX WARN: Type inference failed for: r11v3 */
    /* JADX WARN: Type inference failed for: r11v4 */
    /* JADX WARN: Type inference failed for: r11v6 */
    /* JADX WARN: Type inference failed for: r11v7 */
    /* JADX WARN: Type inference failed for: r11v8 */
    /* JADX WARN: Type inference failed for: r3v2, types: [com.android.launcher3.CellLayout] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void completeAddShortcut(android.content.Intent r22, long r23, long r25, int r27, int r28) {
        /*
            r21 = this;
            r8 = r21
            r0 = r27
            r1 = r28
            int[] r7 = r8.mTmpAddItemCellCoordinates
            com.android.launcher3.PendingAddItemInfo r2 = r8.mPendingAddInfo
            int[] r2 = r2.dropPos
            r5 = r23
            r3 = r25
            com.android.launcher3.CellLayout r15 = r8.getCellLayout(r5, r3)
            com.android.launcher3.ShortcutInfo r14 = com.android.launcher3.InstallShortcutReceiver.fromShortcutIntent(r21, r22)
            if (r14 == 0) goto L151
            com.android.launcher3.PendingAddItemInfo r9 = r8.mPendingAddInfo
            android.content.ComponentName r9 = r9.componentName
            if (r9 != 0) goto L22
            goto L151
        L22:
            android.content.Intent r9 = r14.intent
            com.android.launcher3.PendingAddItemInfo r10 = r8.mPendingAddInfo
            android.content.ComponentName r10 = r10.componentName
            java.lang.String r10 = r10.getPackageName()
            boolean r9 = com.android.launcher3.util.PackageManagerHelper.hasPermissionForActivity(r8, r9, r10)
            java.lang.String r13 = "Launcher"
            r11 = 0
            if (r9 != 0) goto L50
            android.content.Intent r0 = r14.intent
            java.lang.String r0 = r0.toUri(r11)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Ignoring malicious intent "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            android.util.Log.e(r13, r0)
            return
        L50:
            android.view.View r19 = r8.createShortcut(r14)
            r12 = 1
            if (r0 < 0) goto Leb
            if (r1 < 0) goto Leb
            r7[r11] = r0
            r7[r12] = r1
            boolean r2 = r15.isHotseat()
            if (r2 == 0) goto L66
            r15.cleanupVacantCell(r11)
        L66:
            com.android.launcher3.Workspace r9 = r8.mWorkspace
            r2 = 0
            r16 = 1
            r17 = 0
            r18 = 0
            r10 = r19
            r4 = r11
            r3 = r12
            r11 = r23
            r3 = r13
            r13 = r15
            r4 = r14
            r14 = r7
            r20 = r15
            r15 = r2
            boolean r2 = r9.createUserFolderIfNecessary(r10, r11, r13, r14, r15, r16, r17, r18)
            if (r2 == 0) goto L83
            return
        L83:
            com.android.launcher3.DropTarget$DragObject r14 = new com.android.launcher3.DropTarget$DragObject
            r14.<init>()
            r14.dragInfo = r4
            com.android.launcher3.Workspace r9 = r8.mWorkspace
            r13 = 0
            r15 = 1
            r10 = r19
            r11 = r20
            r12 = r7
            boolean r2 = r9.addToExistingFolderIfNecessary(r10, r11, r12, r13, r14, r15)
            if (r2 == 0) goto L9a
            return
        L9a:
            boolean r2 = r20.isHotseat()
            if (r2 != 0) goto Le5
            r15 = r20
            boolean r2 = r15.isOccupied(r0, r1)
            if (r2 == 0) goto Le3
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r9 = "completeAddShortcut(): Occupied cellX="
            r2.append(r9)
            r2.append(r0)
            java.lang.String r9 = ", cellY="
            r2.append(r9)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            com.lge.launcher3.util.LGLog.i(r3, r2)
            r2 = 2
            int[] r2 = new int[r2]
            r15.cellToCenterPoint(r0, r1, r2)
            r9 = 0
            r1 = r2[r9]
            r3 = 1
            r2 = r2[r3]
            r10 = 1
            r11 = 1
            r0 = r15
            r12 = r3
            r3 = r10
            r13 = r9
            r9 = r4
            r4 = r11
            r5 = r7
            int[] r0 = r0.findNearestVacantArea(r1, r2, r3, r4, r5)
            if (r0 == 0) goto Le1
        Ldf:
            r11 = r12
            goto L10d
        Le1:
            r11 = r13
            goto L10d
        Le3:
            r9 = r4
            goto Le8
        Le5:
            r9 = r4
            r15 = r20
        Le8:
            r12 = 1
            r13 = 0
            goto Ldf
        Leb:
            r13 = r11
            r9 = r14
            if (r2 == 0) goto Lfe
            r1 = r2[r13]
            r2 = r2[r12]
            r3 = 1
            r4 = 1
            r0 = r15
            r5 = r7
            int[] r0 = r0.findNearestVacantArea(r1, r2, r3, r4, r5)
            if (r0 == 0) goto Le1
            goto Ldf
        Lfe:
            boolean r0 = r15.isHotseat()
            if (r0 == 0) goto L109
            boolean r11 = r15.canAddVacantCell()
            goto L10d
        L109:
            boolean r11 = r15.findCellForSpan(r7, r12, r12)
        L10d:
            if (r11 != 0) goto L117
            boolean r0 = r8.isHotseatLayout(r15)
            r8.showOutOfSpaceMessage(r0)
            return
        L117:
            r6 = r7[r13]
            r10 = r7[r12]
            r0 = r21
            r1 = r9
            r2 = r23
            r4 = r25
            r9 = r7
            r7 = r10
            com.android.launcher3.LauncherModel.addItemToDatabase(r0, r1, r2, r4, r6, r7)
            boolean r0 = r8.mRestoring
            if (r0 != 0) goto L151
            com.android.launcher3.Workspace r0 = r8.mWorkspace
            r1 = r9[r13]
            r16 = r9[r12]
            r17 = 1
            r18 = 1
            boolean r2 = r21.isWorkspaceLocked()
            r9 = r0
            r10 = r19
            r11 = r23
            r0 = r13
            r13 = r25
            r3 = r15
            r15 = r1
            r19 = r2
            r9.addInScreen(r10, r11, r13, r15, r16, r17, r18, r19)
            boolean r1 = r3.isHotseat()
            if (r1 == 0) goto L151
            r3.cleanupVacantCell(r0)
        L151:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Launcher.completeAddShortcut(android.content.Intent, long, long, int, int):void");
    }

    private int[] getSpanForWidget(ComponentName component, int minWidth, int minHeight) {
        return AppWidgetSizeCalculator.getSpanForWidget(this, component, minWidth, minHeight, null);
    }

    public int[] getSpanForWidget(AppWidgetProviderInfo info) {
        return getSpanForWidget(info.provider, info.minWidth, info.minHeight);
    }

    public int[] getMinSpanForWidget(AppWidgetProviderInfo info) {
        return getSpanForWidget(info.provider, info.minResizeWidth, info.minResizeHeight);
    }

    void completeAddAppWidget(int appWidgetId, ItemInfo itemInfo, AppWidgetHostView hostView, LauncherAppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo == null) {
            appWidgetInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(appWidgetId);
        }
        if (appWidgetInfo.isCustomWidget) {
            appWidgetId = -100;
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = new LauncherAppWidgetInfo(appWidgetId, appWidgetInfo.provider);
        launcherAppWidgetInfo.spanX = itemInfo.spanX;
        launcherAppWidgetInfo.spanY = itemInfo.spanY;
        launcherAppWidgetInfo.minSpanX = itemInfo.minSpanX;
        launcherAppWidgetInfo.minSpanY = itemInfo.minSpanY;
        launcherAppWidgetInfo.user = appWidgetInfo.getUser();
        getModelWriter().addItemToDatabase(launcherAppWidgetInfo, itemInfo.container, itemInfo.screenId, itemInfo.cellX, itemInfo.cellY);
        if (hostView == null) {
            hostView = this.mAppWidgetHost.createView((Context) this, appWidgetId, appWidgetInfo);
        }
        hostView.setVisibility(0);
        prepareAppWidget(hostView, launcherAppWidgetInfo);
        UninstallModeManager.getInstance(this).setUninstallTypeForBadgeView(hostView);
        this.mWorkspace.addInScreen(hostView, launcherAppWidgetInfo);
    }

    private void prepareAppWidget(AppWidgetHostView hostView, LauncherAppWidgetInfo item) {
        hostView.setTag(item);
        item.onBindAppWidget(this, hostView);
        hostView.setFocusable(true);
        hostView.setOnFocusChangeListener(this.mFocusHandler);
    }

    public void updateIconBadges(final Set<PackageUserKey> updatedBadges) {
        Runnable runnable = new Runnable() { // from class: com.android.launcher3.Launcher.14
            @Override // java.lang.Runnable
            public void run() {
                PopupContainerWithArrow open = PopupContainerWithArrow.getOpen(Launcher.this);
                if (open != null) {
                    open.updateNotificationHeader(updatedBadges);
                }
            }
        };
        if (waitUntilResume(runnable)) {
            return;
        }
        runnable.run();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        LGLog.i("Launcher", "[Receiver] onAttachedToWindow");
        setupTransparentSystemBarsForLmp();
        this.mVisible = true;
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onAttachedToWindow();
        }
    }

    private void registerReceiverAtOnCreate() {
        LGLog.i("Launcher", "[Receiver] registerReceiverAtOnCreate");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        intentFilter.addAction(LauncherConst.ACTION_CONTROL_START_DUAL_RECENT);
        registerReceiver(this.mReceiver, intentFilter);
    }

    private void unregisterReceiverAtOnDestroy() {
        LGLog.i("Launcher", "[Receiver] unregisterReceiverAtOnDestroy");
        unregisterReceiver(this.mReceiver);
    }

    private void setupTransparentSystemBarsForLmp() {
        if (Utilities.isLmpOrAbove()) {
            Window window = getWindow();
            window.setDecorFitsSystemWindows(false);
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsBehavior(2);
            }
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(0);
            window.setNavigationBarColor(0);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LGLog.i("Launcher", "[Receiver] onDetachedFromWindow");
        this.mVisible = false;
        updateAutoAdvanceState();
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onDetachedFromWindow();
        }
    }

    public void onWindowVisibilityChanged(int visibility) {
        this.mVisible = visibility == 0;
        updateAutoAdvanceState();
        if (this.mVisible) {
            if (!this.mWorkspaceLoading) {
                this.mWorkspace.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() { // from class: com.android.launcher3.Launcher.15
                    private boolean mStarted = false;

                    @Override // android.view.ViewTreeObserver.OnDrawListener
                    public void onDraw() {
                        if (Launcher.this.mWorkspace == null) {
                            LGLog.d("Launcher", "Workspace is null in onWindowVisibilityChanged");
                        } else {
                            if (this.mStarted) {
                                return;
                            }
                            this.mStarted = true;
                            Launcher.this.mWorkspace.postDelayed(Launcher.this.mBuildLayersRunnable, 500L);
                            Launcher.this.mWorkspace.post(new Runnable() { // from class: com.android.launcher3.Launcher.15.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    if (Launcher.this.mWorkspace == null || Launcher.this.mWorkspace.getViewTreeObserver() == null) {
                                        return;
                                    }
                                    Launcher.this.mWorkspace.getViewTreeObserver().removeOnDrawListener(this);
                                }
                            });
                        }
                    }
                });
            }
            clearTypedText();
        }
    }

    void sendAdvanceMessage(long delay) {
        this.mHandler.removeMessages(1);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), delay);
        this.mAutoAdvanceSentTime = System.currentTimeMillis();
    }

    void updateAutoAdvanceState() {
        boolean z = this.mVisible && this.mUserPresent && !this.mWidgetsToAdvance.isEmpty();
        if (z != this.mAutoAdvanceRunning) {
            this.mAutoAdvanceRunning = z;
            if (z) {
                long j = this.mAutoAdvanceTimeLeft;
                sendAdvanceMessage(j != -1 ? j : 20000L);
            } else {
                if (!this.mWidgetsToAdvance.isEmpty()) {
                    this.mAutoAdvanceTimeLeft = Math.max(0L, 20000 - (System.currentTimeMillis() - this.mAutoAdvanceSentTime));
                }
                this.mHandler.removeMessages(1);
                this.mHandler.removeMessages(0);
            }
        }
    }

    void addWidgetToAutoAdvanceIfNeeded(View hostView, AppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo == null || appWidgetInfo.autoAdvanceViewId == -1) {
            return;
        }
        KeyEvent.Callback callbackFindViewById = hostView.findViewById(appWidgetInfo.autoAdvanceViewId);
        if (callbackFindViewById instanceof Advanceable) {
            this.mWidgetsToAdvance.put(hostView, appWidgetInfo);
            ((Advanceable) callbackFindViewById).fyiWillBeAdvancedByHostKThx();
            updateAutoAdvanceState();
        }
    }

    void removeWidgetToAutoAdvance(View hostView) {
        if (this.mWidgetsToAdvance.containsKey(hostView)) {
            this.mWidgetsToAdvance.remove(hostView);
            updateAutoAdvanceState();
        }
    }

    public void removeAppWidget(LauncherAppWidgetInfo launcherInfo) {
        removeWidgetToAutoAdvance(launcherInfo.hostView);
        launcherInfo.hostView = null;
    }

    /* JADX DEBUG: Method merged with bridge method: getDragLayer()Lcom/android/launcher3/views/BaseDragLayer; */
    @Override // com.android.launcher3.views.ActivityContext
    public DragLayer getDragLayer() {
        return this.mDragLayer;
    }

    public AllAppsContainerView getAppsView() {
        return this.mAppsView;
    }

    public WidgetsContainerView getWidgetsView() {
        return this.mWidgetsView;
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    public Workspace getWorkspace() {
        return this.mWorkspace;
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    public CarouselLayout getCarouselLayout() {
        return this.mCarouselLayout;
    }

    public Hotseat getHotseat() {
        return this.mHotseat;
    }

    public View getPageindicator() {
        return this.mPageIndicators;
    }

    public ViewGroup getLGOverviewPanel() {
        return this.mLGOverviewPanel;
    }

    public View getHotseatSearchBox() {
        return this.mHotseatSearchBox;
    }

    @Override // com.android.launcher3.BaseDraggingActivity
    public <T extends View> T getOverviewPanel() {
        return (T) this.mOverviewPanel;
    }

    public <T extends View> T getOverviewPanelContainer() {
        return (T) this.mOverviewPanelContainer;
    }

    public SearchDropTargetBar getSearchBar() {
        return this.mSearchDropTargetBar;
    }

    public ScrimView getScrimView() {
        return this.mScrimView;
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return this.mAppWidgetHost;
    }

    public LauncherModel getModel() {
        return this.mModel;
    }

    public ModelWriter getModelWriter() {
        return this.mModelWriter;
    }

    @Override // com.android.systemui.plugins.shared.LauncherExterns
    public SharedPreferences getSharedPrefs() {
        return this.mSharedPrefs;
    }

    @Override // com.android.systemui.plugins.shared.LauncherExterns
    public SharedPreferences getDevicePrefs() {
        return Utilities.getDevicePrefs(this);
    }

    @Override // com.android.systemui.plugins.shared.LauncherExterns
    public void runOnOverlayHidden(Runnable runnable) {
        getWorkspace().runOnOverlayHidden(runnable);
    }

    @Override // com.android.launcher3.BaseActivity, com.android.launcher3.views.ActivityContext
    public DeviceProfile getDeviceProfile() {
        return this.mDeviceProfile;
    }

    public void closeSystemDialogs() {
        getWindow().closeAllPanels();
        if (this.mAppsCustomizeHost != null) {
            if (isInState(LauncherState.ALL_APPS)) {
                this.mAppsCustomizeHost.closeMenuDialog();
            } else {
                AllAppsSortDialog.closeDialog();
            }
        }
        MultiWindowGuideManager.getInstance(getApplicationContext()).hideGuide();
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            SwivelHomeGuideManager.getInstance(getApplicationContext()).hideGuide();
        }
    }

    public int getOrientation() {
        return this.mOldConfig.orientation;
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        WidgetsContainerView widgetsContainerView;
        AllAppsContainerView allAppsContainerView;
        super.onNewIntent(intent);
        boolean z = hasWindowFocus() && (intent.getFlags() & 4194304) != 4194304;
        boolean z2 = z && isInState(LauncherState.NORMAL) && AbstractFloatingView.getTopOpenView(this) == null;
        boolean zEquals = PackageUtils.ANDROID_INTENT_ACTION_MAIN.equals(intent.getAction());
        boolean zHandleNewIntent = ACTIVITY_TRACKER.handleNewIntent(this, intent);
        if (zEquals) {
            closeSystemDialogs();
            Workspace workspace = this.mWorkspace;
            if (workspace == null) {
                return;
            }
            Folder openFolder = workspace.getOpenFolder();
            this.mWorkspace.exitWidgetResizeMode();
            LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
            boolean zShouldMoveToDefaultScreenOnHomeIntent = launcherCallbacks != null ? launcherCallbacks.shouldMoveToDefaultScreenOnHomeIntent() : true;
            if (!zHandleNewIntent) {
                closeOpenViews(((isInState(LauncherState.ALL_APPS) || this.mWorkspace.isInOverviewMode()) && openFolder != null) ? false : isStarted());
                if (!isInState(LauncherState.NORMAL)) {
                    this.mStateManager.goToState(LauncherState.NORMAL, (this.mDeviceProfile.isMultiWindowMode && this.mDeviceProfile.isLandscape) ? false : true);
                }
                if (z2 && !this.mWorkspace.isTouchActive() && openFolder == null && zShouldMoveToDefaultScreenOnHomeIntent) {
                    this.mWorkspace.moveToDefaultScreen();
                    this.mWorkspace.showWorkGuide();
                } else if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && this.mOldConfig.windowConfiguration.getWindowingMode() == 4) {
                    getRotationHelper().setCurrentStateRequest(0);
                }
                if (isInState(LauncherState.ALL_APPS) || this.mWorkspace.isInOverviewMode()) {
                    closeFolder(false);
                } else {
                    closeFolder(true);
                }
                folderCloseByHomeKey = true;
                AbstractFloatingView.closeAllOpenViews(this, z);
                if (isInState(LauncherState.CLEAN_VIEW)) {
                    exitCleanViewMode();
                }
                if (this.mWorkspace.getState() != Workspace.State.NORMAL && !isInState(LauncherState.ALL_APPS)) {
                    showWorkspace(!isInState(LauncherState.ALL_APPS) || openFolder == null);
                } else {
                    this.mOnResumeState = LauncherState.NORMAL;
                }
                if (!z && (allAppsContainerView = this.mAppsView) != null) {
                    allAppsContainerView.scrollToTop();
                }
                if (!z && (widgetsContainerView = this.mWidgetsView) != null) {
                    widgetsContainerView.scrollToTop();
                }
                AllAppsHost allAppsHost = this.mAppsCustomizeHost;
                if (allAppsHost != null) {
                    allAppsHost.homeKeyPressed(z);
                }
                if (Build.VERSION.SDK_INT < 28 && !this.mWorkspace.getCheckInappsValue()) {
                    this.mWorkspace.exitInAppsWithoutAni();
                }
            }
            if (isInState(LauncherState.NORMAL) && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                int itemCount = this.mCarouselLayout.getCarouselView().getAdapter().getItemCount() / 2;
                if (this.mCarouselLayout.getCarouselView().getAdapter().getItemCount() % 2 == 0 && itemCount > 0) {
                    itemCount--;
                }
                this.mCarouselLayout.getCarouselView().smoothScrollToPosition(itemCount);
            }
            View viewPeekDecorView = getWindow().peekDecorView();
            if (viewPeekDecorView != null && viewPeekDecorView.getWindowToken() != null) {
                UiThreadHelper.hideKeyboardAsync(this, viewPeekDecorView.getWindowToken());
            }
            LauncherCallbacks launcherCallbacks2 = this.mLauncherCallbacks;
            if (launcherCallbacks2 != null) {
                launcherCallbacks2.onHomeIntent(zHandleNewIntent);
                this.mOverlayManager.hideOverlay(isStarted() && !isForceInvisible());
            }
        }
        PinItemDragListener.handleDragRequest(this, intent);
        LauncherCallbacks launcherCallbacks3 = this.mLauncherCallbacks;
        if (launcherCallbacks3 != null) {
            launcherCallbacks3.onNewIntent(intent);
        }
    }

    @Override // android.app.Activity
    public void onRestoreInstanceState(Bundle state) {
        try {
            super.onRestoreInstanceState(state);
            Iterator<Integer> it = this.mSynchronouslyBoundPages.iterator();
            while (it.hasNext()) {
                this.mWorkspace.restoreInstanceStateForChild(it.next().intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle;
        if (this.mWorkspace.getChildCount() > 0) {
            if (this.mWorkspace.getRestorePage() != -1001) {
                outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, this.mWorkspace.getRestorePage());
            } else {
                outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, this.mWorkspace.getCurrentPageOffsetFromCustomContent());
            }
        }
        super.onSaveInstanceState(outState);
        outState.putInt(RUNTIME_STATE, ((LauncherState) this.mStateManager.getState()).ordinal);
        if (this.mSuppressCloseFolder) {
            this.mSuppressCloseFolder = false;
        }
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this);
        if (topOpenView != null && !topOpenView.isOfType(1)) {
            topOpenView.close(false);
        }
        if (this.mPendingAddInfo.container != -1 && this.mPendingAddInfo.screenId > -1 && this.mWaitingForResult) {
            outState.putLong(RUNTIME_STATE_PENDING_ADD_CONTAINER, this.mPendingAddInfo.container);
            outState.putLong(RUNTIME_STATE_PENDING_ADD_SCREEN, this.mPendingAddInfo.screenId);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_X, this.mPendingAddInfo.cellX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_Y, this.mPendingAddInfo.cellY);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_X, this.mPendingAddInfo.spanX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y, this.mPendingAddInfo.spanY);
            outState.putParcelable(RUNTIME_STATE_PENDING_ADD_COMPONENT, this.mPendingAddInfo.componentName);
            outState.putParcelable(RUNTIME_STATE_PENDING_ADD_WIDGET_INFO, this.mPendingAddWidgetInfo);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_WIDGET_ID, this.mPendingAddWidgetId);
        }
        outState.putInt(RUNTIME_STATE_PENDING_REQUEST_CODE, this.mPendingActivityRequestCode);
        outState.putSerializable(RUNTIME_STATE_VIEW_IDS, this.mItemIdToViewId);
        this.mSavedStateApps = outState;
        if (!((PowerManager) getApplicationContext().getSystemService("power")).isScreenOn() && (bundle = this.mSavedStateApps) != null) {
            bundle.putInt(RUNTIME_STATE, LauncherState.NORMAL.ordinal);
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onSaveInstanceState(outState);
        }
        LGLog.d("Launcher", "onSaveInstanceState: Current screen = " + outState.getInt(RUNTIME_STATE_CURRENT_SCREEN));
    }

    @Override // com.android.launcher3.BaseDraggingActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        ACTIVITY_TRACKER.onActivityDestroyed(this);
        PluginManagerWrapper.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).removePluginListener(this);
        unregisterReceiverAtOnDestroy();
        if (this.mWorkspace.getOpenFolder() != null) {
            closeFolder(new boolean[0]);
        }
        if (isInState(LauncherState.CLEAN_VIEW)) {
            exitCleanViewMode();
        }
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(0);
        this.mWorkspace.removeCallbacks(this.mBuildLayersRunnable);
        Runnable runnable = this.mCancelTouchController;
        if (runnable != null) {
            runnable.run();
            this.mCancelTouchController = null;
        }
        if (this.mModel.isCurrentCallbacks(this)) {
            this.mModel.stopLoader();
            LauncherAppState.getInstance(this).setLauncher(null);
        }
        this.mRotationHelper.destroy();
        QuickstepTransitionManager quickstepTransitionManager = this.mAppTransitionManager;
        if (quickstepTransitionManager != null && (quickstepTransitionManager instanceof QuickstepTransitionManager)) {
            quickstepTransitionManager.unregisterRemoteAnimations();
        }
        try {
            ConciergeBoardMngr.onDestroyHost();
            this.mAppWidgetHost.stopListening();
        } catch (NullPointerException e) {
            Log.w("Launcher", "problem while stopping AppWidgetHost during Launcher destruction", e);
        }
        this.mAppWidgetHost = null;
        this.mWidgetsToAdvance.clear();
        TextKeyListener.getInstance().release();
        unregisterReceiver(this.mCloseSystemDialogsReceiver);
        AbstractFloatingView.closeOpenViews(this, false, 8);
        ((ViewGroup) this.mWorkspace.getParent()).removeAllViews();
        this.mWorkspace.removeAllWorkspaceScreens();
        this.mWorkspace = null;
        this.mDragController = null;
        LauncherAnimUtils.onDestroyActivity();
        clearPendingBinds();
        AllAppsHost allAppsHost = this.mAppsCustomizeHost;
        if (allAppsHost != null) {
            allAppsHost.onDestroy();
            this.mAppsCustomizeHost = null;
        }
        LauncherAppState.getIDP(this).removeOnChangeListener(this);
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onDestroy();
        }
        NotificationListener.removeNotificationsChangedListener();
        if (this.mPopupDataProvider != null) {
            PopupDataProvider.onDestroy();
            this.mPopupDataProvider = null;
        }
        CustomUIManager.getInstance(getApplicationContext()).destroy();
    }

    @Override // android.app.Activity
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            this.mRotationHelper.setCurrentTransitionRequest(0);
            this.mRotationHelper.setCurrentStateRequest(1);
        } else {
            this.mRotationHelper.setCurrentTransitionRequest(0);
        }
    }

    @Override // com.android.launcher3.BaseDraggingActivity, com.android.launcher3.util.DisplayController.DisplayInfoChangeListener
    public void onDisplayInfoChanged(Context context, DisplayController.Info info, int flags) {
        super.onDisplayInfoChanged(context, info, flags);
        if ((flags & 2) == 0 || !LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            return;
        }
        this.mCarouselLayout.getSwivelWeatherView().setAdaptiveColorForWeatherView();
        AdaptiveTextUtil.adaptiveStatusBar(getWorkspace(), AdaptiveTextUtil.getAdaptiveStatusBarColor(getApplicationContext()));
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        RecentsView recentsView;
        int iDiff = newConfig.diff(this.mOldConfig);
        LGLog.i("Launcher", "onConfigurationChanged() : diff = " + iDiff + ", newConfig = " + newConfig);
        if ((iDiff & 1152) != 0) {
            onIdpChanged(this.mDeviceProfile.inv);
            if (isInState(LauncherState.CLEAN_VIEW)) {
                exitCleanViewMode();
            }
        }
        int i = iDiff & 128;
        if (i != 0 && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            new Handler().post(new Runnable() { // from class: com.android.launcher3.-$$Lambda$Launcher$klA0uqMNAI7jRiNd_GDm2ZO9Zw8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onConfigurationChanged$1$Launcher();
                }
            });
        }
        if (i != 0 && !getDeviceProfile().allowRotation && !getDeviceProfile().isLandscape) {
            if (this.mWidgetsView != null && getDeviceProfile().isTablet) {
                ((LGWidgetContainerView) this.mWidgetsView).clear();
                ((LGWidgetContainerView) this.mWidgetsView).setFlagForRefreshPreView(true);
                ((LGWidgetContainerView) this.mWidgetsView).cancelSearchWidgetsAsyncTask();
            }
            HomescreenBlurManager.getInstance(this).updateBackgroundViewContents();
        }
        if (i != 0 && getDeviceProfile().allowRotation && getState() == LauncherState.OVERVIEW && (recentsView = (RecentsView) getOverviewPanel()) != null && recentsView.getNativeScroller() != null && !recentsView.getNativeScroller().isFinished()) {
            recentsView.forceFinishScroller(false);
        }
        this.mOldConfig.setTo(newConfig);
        QuickstepTransitionManager quickstepTransitionManager = this.mAppTransitionManager;
        if (quickstepTransitionManager != null) {
            boolean z = quickstepTransitionManager instanceof QuickstepTransitionManager;
        }
        super.onConfigurationChanged(newConfig);
    }

    public /* synthetic */ void lambda$onConfigurationChanged$1$Launcher() {
        this.mCarouselLayout.getSwivelWeatherView().initView();
        this.mCarouselLayout.updateWeatheInformation();
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity, com.android.launcher3.BaseDraggingActivity
    public void reapplyUi() {
        getRootView().dispatchInsets();
        getStateManager().reapplyState(true);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void rebindModel() {
        int i;
        Launcher launcher;
        if (this.mDeviceProfile.isMultiWindowMode && getWorkspace() != null && getWorkspace().getState() != Workspace.State.NORMAL) {
            showWorkspace(false);
        }
        Workspace workspace = this.mWorkspace;
        if (workspace == null) {
            return;
        }
        int nextPage = workspace.getNextPage();
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            ((LauncherExtension) this).showSwivelHomeView();
        }
        boolean z = true;
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() || !this.mDeviceProfile.allowRotation ? !(this.mChangedProfileByMultiWindow || this.mOrientationOfCurrentLayout != 0 || ((this.mWorkspaceLoading && this.mChangedProfile) || this.mIsMirrorMode)) : !(this.mChangedProfileByMultiWindow || this.mOrientationOfCurrentLayout != 0 || ((this.mWorkspaceLoading && this.mChangedProfile) || this.mIsMirrorMode || this.mStateManager.getState() == LauncherState.NORMAL || this.mStateManager.getState() == LauncherState.ALL_APPS || this.mStateManager.getState() == LauncherState.WIDGETS || this.mStateManager.getState() == LauncherState.DYNAMIC_GRID_OVERVIEW))) {
            z = false;
        }
        if (z) {
            i = 32;
            RecentsView recentsView = (RecentsView) getOverviewPanel();
            if (recentsView != null) {
                recentsView.updateRecommandLayoutItems();
            }
        } else {
            i = 0;
        }
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            int i2 = (this.mWorkspaceLoading || (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && z)) ? i : i | 64;
            LGLog.i("Launcher", "rebindModel(swivel): rebind = " + z + ", flag = " + i + "(" + i2 + "), currentPage = " + nextPage + ", isLandscape = " + this.mDeviceProfile.isLandscape + ", mChangedProfileByMultiWindow = " + this.mChangedProfileByMultiWindow + ", mOrientationOfCurrentLayout = " + this.mOrientationOfCurrentLayout + ", mWorkspaceLoading = " + this.mWorkspaceLoading + ", mChangedProfile = " + this.mChangedProfile + ", mIsMirrorMode = " + this.mIsMirrorMode);
            if (z || (i2 & 64) != 0) {
                launcher = this;
                if (launcher.mModel.startLoader(nextPage, i2)) {
                    launcher.mWorkspace.setCurrentPage(nextPage);
                    LGLog.i("Launcher", "rebindModel(swivel): setCurrentPage = (" + nextPage + ", " + launcher.mWorkspace.getCurrentPage() + ")");
                }
            } else {
                launcher = this;
            }
            if (launcher.mWorkspaceVisibility != null && !launcher.mDeviceProfile.isLandscape) {
                LGLog.d("Launcher", "rebindModel(swivel) : call mWorkspaceVisibility.");
                launcher.mWorkspaceVisibility.run();
                launcher.mWorkspaceVisibility = null;
            }
        } else {
            LGLog.i("Launcher", "rebindModel: rebind = " + z + ", flag = " + i + ", currentPage = " + nextPage + ", isLandscape = " + this.mDeviceProfile.isLandscape + ", mChangedProfileByMultiWindow = " + this.mChangedProfileByMultiWindow + ", mOrientationOfCurrentLayout = " + this.mOrientationOfCurrentLayout + ", mWorkspaceLoading = " + this.mWorkspaceLoading + ", mChangedProfile = " + this.mChangedProfile + ", mIsMirrorMode = " + this.mIsMirrorMode);
            launcher = this;
            if (z && launcher.mModel.startLoader(nextPage, i)) {
                launcher.mWorkspace.setCurrentPage(nextPage);
                LGLog.i("Launcher", "rebindModel: setCurrentPage = (" + nextPage + ", " + launcher.mWorkspace.getCurrentPage() + ")");
            }
        }
        launcher.mIsMirrorMode = false;
    }

    private /* synthetic */ void lambda$rebindModel$2() {
        CarouselLayout carouselLayout = this.mCarouselLayout;
        int visibility = carouselLayout != null ? carouselLayout.getVisibility() : -1;
        LGLog.d("Launcher", "run mWorkspaceVisibility. mCarouselLayout visible = " + visibility + ", " + this.mCarouselLayout);
        CarouselLayout carouselLayout2 = this.mCarouselLayout;
        if (carouselLayout2 == null || carouselLayout2.getVisibility() == 0) {
            return;
        }
        setWorkspaceAndHotseatVisibility(0, "Runnable mWorkspaceVisibility");
    }

    public DragController getDragController() {
        return this.mDragController;
    }

    @Override // android.app.Activity
    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) {
        if (requestCode != -1) {
            this.mPendingActivityRequestCode = requestCode;
        }
        try {
            super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } catch (IntentSender.SendIntentException unused) {
            throw new ActivityNotFoundException();
        }
    }

    @Override // android.app.Activity
    public void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch) {
        if (initialQuery == null) {
            initialQuery = getTypedText();
        }
        if (appSearchData == null) {
            appSearchData = new Bundle();
            appSearchData.putString("source", "launcher-search");
        }
        Rect rect = new Rect();
        SearchDropTargetBar searchDropTargetBar = this.mSearchDropTargetBar;
        if (searchDropTargetBar != null) {
            rect = searchDropTargetBar.getSearchBarBounds();
        }
        if (startSearch(initialQuery, selectInitialQuery, appSearchData, rect)) {
            clearTypedText();
        }
        showWorkspace(true);
    }

    public boolean startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, Rect sourceBounds) {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null && launcherCallbacks.providesSearch()) {
            return this.mLauncherCallbacks.startSearch(initialQuery, selectInitialQuery, appSearchData, sourceBounds);
        }
        startGlobalSearch(initialQuery, selectInitialQuery, appSearchData, sourceBounds);
        return false;
    }

    private void startGlobalSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, Rect sourceBounds) {
        Bundle bundle;
        ComponentName globalSearchActivity = ((SearchManager) getSystemService("search")).getGlobalSearchActivity();
        if (globalSearchActivity == null) {
            Log.w("Launcher", "No global search activity found.");
            return;
        }
        Intent intent = new Intent("android.search.action.GLOBAL_SEARCH");
        intent.addFlags(268435456);
        intent.setComponent(globalSearchActivity);
        if (appSearchData == null) {
            bundle = new Bundle();
        } else {
            bundle = new Bundle(appSearchData);
        }
        if (!bundle.containsKey("source")) {
            bundle.putString("source", getPackageName());
        }
        intent.putExtra("app_data", bundle);
        if (!TextUtils.isEmpty(initialQuery)) {
            intent.putExtra("query", initialQuery);
        }
        if (selectInitialQuery) {
            intent.putExtra("select_query", selectInitialQuery);
        }
        intent.setSourceBounds(sourceBounds);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Log.e("Launcher", "Global search activity not found: " + globalSearchActivity);
        }
    }

    public boolean isOnCustomContent() {
        return this.mWorkspace.isOnOrMovingToCustomContent();
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        isOnCustomContent();
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            return launcherCallbacks.onPrepareOptionsMenu(menu);
        }
        return false;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onSearchRequested() {
        startSearch((String) null, false, (Bundle) null, true);
        return true;
    }

    public boolean isWorkspaceLocked() {
        return this.mWorkspaceLoading || this.mWaitingForResult;
    }

    public boolean isWorkspaceLoading() {
        return this.mWorkspaceLoading;
    }

    private void setWorkspaceLoading(boolean value) {
        boolean zIsWorkspaceLocked = isWorkspaceLocked();
        this.mWorkspaceLoading = value;
        if (zIsWorkspaceLocked != isWorkspaceLocked()) {
            onWorkspaceLockedChanged();
        }
    }

    public void setWaitingForResult(PendingRequestArgs args) {
        boolean zIsWorkspaceLocked = isWorkspaceLocked();
        this.mPendingRequestArgs = args;
        if (zIsWorkspaceLocked != isWorkspaceLocked()) {
            onWorkspaceLockedChanged();
        }
    }

    protected void onWorkspaceLockedChanged() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onWorkspaceLockedChanged();
        }
    }

    private void resetAddInfo() {
        this.mPendingAddInfo.container = -1L;
        this.mPendingAddInfo.screenId = -1L;
        PendingAddItemInfo pendingAddItemInfo = this.mPendingAddInfo;
        pendingAddItemInfo.cellY = -1;
        pendingAddItemInfo.cellX = -1;
        PendingAddItemInfo pendingAddItemInfo2 = this.mPendingAddInfo;
        pendingAddItemInfo2.spanY = -1;
        pendingAddItemInfo2.spanX = -1;
        PendingAddItemInfo pendingAddItemInfo3 = this.mPendingAddInfo;
        pendingAddItemInfo3.minSpanY = 1;
        pendingAddItemInfo3.minSpanX = 1;
        this.mPendingAddInfo.dropPos = null;
        this.mPendingAddInfo.componentName = null;
        this.mPendingRequestArgs = null;
    }

    void addAppWidgetFromDropImpl(int appWidgetId, ItemInfo info, AppWidgetHostView boundWidget, WidgetAddFlowHandler addFlowHandler) {
        addAppWidgetImpl(appWidgetId, info, boundWidget, addFlowHandler, 0);
    }

    void addAppWidgetImpl(int appWidgetId, ItemInfo info, AppWidgetHostView boundWidget, WidgetAddFlowHandler addFlowHandler, int delay) {
        if (addFlowHandler.startConfigActivity(this, appWidgetId, info, 5)) {
            return;
        }
        Runnable runnable = new Runnable() { // from class: com.android.launcher3.Launcher.17
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.exitSpringLoadedDragModeDelayed(true, 300, null);
            }
        };
        completeAddAppWidget(appWidgetId, info, boundWidget, addFlowHandler.getProviderInfo(this));
        this.mWorkspace.removeExtraEmptyScreenDelayed(true, runnable, delay, false);
    }

    protected void moveToCustomContentScreen(boolean animate) {
        closeFolder(new boolean[0]);
        this.mWorkspace.moveToCustomContentScreen(animate);
    }

    public void addPendingItem(PendingAddItemInfo info, long container, long screenId, int[] cell, int spanX, int spanY) {
        info.container = container;
        info.screenId = screenId;
        if (cell != null) {
            info.cellX = cell[0];
            info.cellY = cell[1];
        }
        info.spanX = spanX;
        info.spanY = spanY;
        int i = info.itemType;
        if (i == 1) {
            processShortcutFromDrop((PendingAddShortcutInfo) info);
            return;
        }
        if (i == 4 || i == 5) {
            addAppWidgetFromDrop((PendingAddWidgetInfo) info);
        } else {
            if (i == 6) {
                return;
            }
            throw new IllegalStateException("Unknown item type: " + info.itemType);
        }
    }

    private void processShortcutFromDrop(PendingAddShortcutInfo info) {
        setWaitingForResult(PendingRequestArgs.forIntent(1, new Intent("android.intent.action.CREATE_SHORTCUT").setComponent(info.componentName), info));
        if (info.activityInfo.startConfigActivity(this, 1)) {
            return;
        }
        handleActivityResult(1, 0, null);
    }

    private void addAppWidgetFromDrop(PendingAddWidgetInfo info) {
        AppWidgetHostView appWidgetHostView = info.boundWidget;
        WidgetAddFlowHandler handler = info.getHandler();
        if (appWidgetHostView != null) {
            getDragLayer().removeView(appWidgetHostView);
            addAppWidgetFromDropImpl(appWidgetHostView.getAppWidgetId(), info, appWidgetHostView, handler);
            info.boundWidget = null;
        } else {
            int iAllocateAppWidgetId = getAppWidgetHost().allocateAppWidgetId();
            if (this.mAppWidgetManager.bindAppWidgetIdIfAllowed(iAllocateAppWidgetId, info.info, info.bindOptions)) {
                addAppWidgetFromDropImpl(iAllocateAppWidgetId, info, null, handler);
            } else {
                handler.startBindFlow(this, iAllocateAppWidgetId, info, 11);
            }
        }
    }

    protected FolderIcon addFolder(CellLayout layout, long container, final long screenId, int cellX, int cellY) {
        FolderInfo folderInfo = new FolderInfo();
        folderInfo.title = getText(R.string.folder_name);
        LauncherModel.addItemToDatabase(this, folderInfo, container, screenId, cellX, cellY);
        sFolders.put(folderInfo.id, folderInfo);
        FolderIcon folderIconFromXml = FolderIcon.fromXml(R.layout.folder_icon, this, layout, folderInfo, this.mIconCache);
        this.mWorkspace.addInScreen(folderIconFromXml, container, screenId, cellX, cellY, 1, 1, isWorkspaceLocked());
        this.mWorkspace.getParentCellLayoutForView(folderIconFromXml).getShortcutsAndWidgets().measureChild(folderIconFromXml);
        return folderIconFromXml;
    }

    public void removeFolder(FolderInfo folder) {
        sFolders.remove(folder.id);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            if (getWorkspace() != null) {
                getWorkspace().setBackgroundTransparentOfFocusHandler(false);
            }
            int keyCode = event.getKeyCode();
            if (keyCode != 3) {
                if (keyCode != 25) {
                    if (keyCode == 62 || keyCode == 66) {
                        this.isLongClickFromKeyEnter = true;
                    }
                } else if (Utilities.isPropertyEnabled(DUMP_STATE_PROPERTY)) {
                    dumpState();
                }
            }
            return true;
        }
        if (event.getAction() == 1) {
            int keyCode2 = event.getKeyCode();
            if (keyCode2 == 3) {
                return true;
            }
            if (keyCode2 == 62 || keyCode2 == 66) {
                this.isLongClickFromKeyEnter = false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        AllAppsHost allAppsHost;
        if (finishAutoCancelActionMode()) {
            return;
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks == null || !launcherCallbacks.handleBackPressed()) {
            if (this.mDragController.isDragging()) {
                this.mDragController.cancelDrag();
                return;
            }
            UserEventDispatcher userEventDispatcher = getUserEventDispatcher();
            AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this);
            if (topOpenView != null && topOpenView.onBackPressed()) {
                if (!isInState(LauncherState.OVERVIEW) || ((RecentsView) getOverviewPanel()).getTaskViewCount() == 0 || (topOpenView instanceof TaskMenuView)) {
                    return;
                }
                LauncherState launcherState = (LauncherState) this.mStateManager.getLastState();
                userEventDispatcher.logActionCommand(1, ((LauncherState) this.mStateManager.getState()).containerType, launcherState.containerType);
                if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && LGHomeFeature.Config.FEATURE_USE_SIMPLE_TRANSITION_OF_LANDSCAPE.getValue() && getResources().getInteger(R.integer.config_simple_transition_landscape) == 2 && getResources().getConfiguration().orientation == 2) {
                    getRotationHelper().setCurrentStateRequest(0);
                }
                this.mStateManager.goToState(launcherState);
                return;
            }
            if (isWidgetsViewVisible()) {
                if (((LGWidgetContainerView) this.mWidgetsView).closeGroupWidgetPopup()) {
                    return;
                }
                showOverviewMode(true);
                return;
            }
            if (isDynamicGridOverViewVisible()) {
                showOverviewMode(true);
                return;
            }
            if (this.mWorkspace.isInOverviewMode()) {
                showWorkspace(true);
                return;
            }
            if (UninstallModeManager.getInstance(this).isInUninstallMode() && !isInState(LauncherState.ALL_APPS)) {
                showWorkspace(true);
                return;
            }
            if (!isInState(LauncherState.NORMAL)) {
                if (isInState(LauncherState.CLEAN_VIEW)) {
                    exitCleanViewMode();
                }
                if (isAppsViewVisible() && this.mWorkspace.getOpenFolder() == null && (allAppsHost = this.mAppsCustomizeHost) != null && allAppsHost.onBackPressed()) {
                    return;
                }
                LauncherState launcherState2 = (LauncherState) this.mStateManager.getLastState();
                userEventDispatcher.logActionCommand(1, ((LauncherState) this.mStateManager.getState()).containerType, launcherState2.containerType);
                if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && LGHomeFeature.Config.FEATURE_USE_SIMPLE_TRANSITION_OF_LANDSCAPE.getValue() && getResources().getInteger(R.integer.config_simple_transition_landscape) == 2 && getResources().getConfiguration().orientation == 2) {
                    getRotationHelper().setCurrentStateRequest(0);
                }
                this.mStateManager.goToState(launcherState2);
                return;
            }
            if (isInState(LauncherState.CLEAN_VIEW)) {
                exitCleanViewMode();
            }
            this.mWorkspace.exitWidgetResizeMode();
            if (Build.VERSION.SDK_INT < 28) {
                this.mWorkspace.exitInAppsWithoutAni();
            }
        }
    }

    public void onAssistantVisibilityChanged(float visibility) {
        this.mCurrentAssistantVisibility = visibility;
        float f = 1.0f - visibility;
        LauncherState launcherState = (LauncherState) this.mStateManager.getState();
        if (launcherState == LauncherState.NORMAL) {
            this.mAppsCustomizeHost.getAlphaProperty(1).setValue(f);
        } else if (launcherState == LauncherState.OVERVIEW || launcherState == LauncherState.OVERVIEW_PEEK) {
            this.mAppsCustomizeHost.getAlphaProperty(1).setValue(f);
        }
    }

    private void initDeviceProfile(InvariantDeviceProfile idp) {
        boolean z = this.mDeviceProfile.isMultiWindowMode;
        DeviceProfile deviceProfile = this.mDeviceProfile;
        this.mDeviceProfile = idp.getDeviceProfile(this);
        LGLog.d("Launcher", "[DEVICE_PROFILE] initDeviceProfile : land = " + this.mDeviceProfile.isLandscape);
        if (isInMultiWindowModeCompat()) {
            this.mDeviceProfile = this.mDeviceProfile.getMultiWindowProfile(this, getMultiWindowDisplaySize());
        }
        onDeviceProfileInitiated();
        this.mChangedProfileByMultiWindow = this.mDeviceProfile.isMultiWindowMode || z != this.mDeviceProfile.isMultiWindowMode;
        if (deviceProfile != this.mDeviceProfile) {
            this.mChangedProfile = true;
        } else {
            this.mChangedProfile = false;
        }
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity, android.app.Activity
    public <T extends View> T findViewById(int i) {
        return (T) getRootView().findViewById(i);
    }

    @Override // com.android.launcher3.LauncherProviderChangeListener
    public void onAppWidgetHostReset() {
        LauncherAppWidgetHost launcherAppWidgetHost = this.mAppWidgetHost;
        if (launcherAppWidgetHost != null) {
            launcherAppWidgetHost.startListening();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        LGLog.d("Launcher", "Entered onClick");
        if (v.getWindowToken() == null) {
            LGLog.w("Launcher", "returned  because v.getWindowToken() == null - onClick", new int[0]);
            return;
        }
        if (!this.mWorkspace.isFinishedSwitchingState()) {
            LGLog.w("Launcher", "returned  because !mWorkspace.isFinishedSwitchingState() - onClick", new int[0]);
            return;
        }
        if (isCleanViewState()) {
            LGLog.w("Launcher", "returned  because isCleanViewState - onClick", new int[0]);
            return;
        }
        if (v instanceof Workspace) {
            if (this.mWorkspace.isInOverviewMode()) {
                showWorkspace(true);
                return;
            }
            return;
        }
        if (((v instanceof CellLayout) || (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && (v instanceof SwivelContentsView))) && (this.mWorkspace.isInOverviewMode() || UninstallModeManager.getInstance(this).isInUninstallMode())) {
            showWorkspace(this.mWorkspace.indexOfChild(v), true);
        }
        Object tag = v.getTag();
        if (tag instanceof ShortcutInfo) {
            onClickAppShortcut(v);
            return;
        }
        if (tag instanceof FolderInfo) {
            if (v instanceof FolderIcon) {
                onClickFolderIcon(v);
            }
        } else {
            if (v == this.mAllAppsButton) {
                onClickAllAppsButton(v);
                return;
            }
            if (tag instanceof AppInfo) {
                startAppShortcutOrInfoActivity(v);
            } else if (tag instanceof LauncherAppWidgetInfo) {
                if (v instanceof PendingAppWidgetHostView) {
                    onClickPendingWidget((PendingAppWidgetHostView) v);
                } else {
                    onClickLauncherAppWidget(v);
                }
            }
        }
    }

    public void onClickPendingWidget(final PendingAppWidgetHostView v) {
        if (this.mIsSafeModeEnabled) {
            Toast.makeText(this, R.string.safemode_widget_error, 0).show();
            return;
        }
        final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) v.getTag();
        if (v.isReadyForClickSetup()) {
            int i = launcherAppWidgetInfo.appWidgetId;
            AppWidgetProviderInfo appWidgetInfo = this.mAppWidgetManager.getAppWidgetInfo(i);
            if (appWidgetInfo != null) {
                this.mPendingAddWidgetInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this, appWidgetInfo);
                this.mPendingAddInfo.copyFrom(launcherAppWidgetInfo);
                this.mPendingAddWidgetId = i;
                AppWidgetManagerCompat.getInstance(this).startConfigActivity(appWidgetInfo, launcherAppWidgetInfo.appWidgetId, this, this.mAppWidgetHost, 12);
                return;
            }
            return;
        }
        if (launcherAppWidgetInfo.isUpdateCenterRestored()) {
            Toast.makeText(this, R.string.activity_not_found, 0).show();
        } else if (launcherAppWidgetInfo.installProgress < 0) {
            final String packageName = launcherAppWidgetInfo.providerName.getPackageName();
            showBrokenAppInstallDialog(packageName, new DialogInterface.OnClickListener() { // from class: com.android.launcher3.Launcher.18
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int id) {
                    Launcher.this.lambda$startActivitySafely$4$Launcher((View) v, LauncherModel.getMarketIntent(packageName), (ItemInfo) launcherAppWidgetInfo);
                }
            });
        } else {
            lambda$startActivitySafely$4$Launcher((View) v, LauncherModel.getMarketIntent(launcherAppWidgetInfo.providerName.getPackageName()), (ItemInfo) launcherAppWidgetInfo);
        }
    }

    public void onClickLauncherAppWidget(final View v) {
        if (this.mIsSafeModeEnabled) {
            Toast.makeText(this, R.string.safemode_widget_error, 0).show();
        } else {
            if (UninstallModeManager.getInstance(this).checkAndShowUninstallPopup(this, v)) {
            }
        }
    }

    protected void onClickAllAppsButton(View v) {
        if (isAppsViewVisible() || UninstallModeManager.getInstance(this).isInUninstallMode()) {
            return;
        }
        showAllAppsView(true, false, true, false);
    }

    private void showBrokenAppInstallDialog(final String packageName, DialogInterface.OnClickListener onSearchClickListener) {
        new AlertDialog.Builder(this).setTitle(R.string.abandoned_promises_title).setMessage(R.string.abandoned_promise_explanation).setPositiveButton(R.string.abandoned_search, onSearchClickListener).setNeutralButton(R.string.abandoned_clean_this, new DialogInterface.OnClickListener() { // from class: com.android.launcher3.Launcher.19
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int id) {
                Launcher.this.mWorkspace.removeAbandonedPromise(packageName, Process.myUserHandle());
            }
        }).create().show();
    }

    private void showBrokenAppInstallDialog(final ComponentName componentName, DialogInterface.OnClickListener onSearchClickListener) {
        new AlertDialog.Builder(this).setTitle(R.string.abandoned_promises_title).setMessage(R.string.abandoned_promise_explanation).setPositiveButton(R.string.abandoned_search, onSearchClickListener).setNeutralButton(R.string.abandoned_clean_this, new DialogInterface.OnClickListener() { // from class: com.android.launcher3.Launcher.20
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int id) {
                Process.myUserHandle();
                LauncherExtension.onItemRemove(componentName.getPackageName(), componentName.getClassName());
            }
        }).create().show();
    }

    protected void onClickAppShortcut(final View v) {
        Object tag = v.getTag();
        if (!(tag instanceof ShortcutInfo)) {
            throw new IllegalArgumentException("Input must be a Shortcut");
        }
        if (UninstallModeManager.getInstance(this).checkAndShowUninstallPopup(this, v)) {
            return;
        }
        final ShortcutInfo shortcutInfo = (ShortcutInfo) tag;
        if (shortcutInfo.runtimeStatusFlags != 0 && (shortcutInfo.runtimeStatusFlags & (-5) & (-9)) != 0) {
            if (!TextUtils.isEmpty(shortcutInfo.disabledMessage)) {
                Toast.makeText(this, shortcutInfo.disabledMessage, 0).show();
                return;
            }
            int i = R.string.activity_not_available;
            if ((shortcutInfo.runtimeStatusFlags & 1) != 0) {
                i = R.string.safemode_shortcut_error;
            } else if ((shortcutInfo.runtimeStatusFlags & 16) != 0 || (shortcutInfo.runtimeStatusFlags & 32) != 0) {
                i = R.string.shortcut_not_available;
            }
            Toast.makeText(this, i, 0).show();
            return;
        }
        Intent intent = shortcutInfo.intent;
        if (intent.getComponent() != null) {
            String className = intent.getComponent().getClassName();
            if (className.equals(MemoryDumpActivity.class.getName())) {
                MemoryDumpActivity.startDump(this);
                return;
            } else if (className.equals(ToggleWeightWatcher.class.getName())) {
                toggleShowWeightWatcher();
                return;
            }
        }
        if ((v instanceof BubbleTextView) && shortcutInfo.isPromise() && !shortcutInfo.hasStatusFlag(4)) {
            if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                showBrokenAppInstallDialog(shortcutInfo.getTargetComponent(), new DialogInterface.OnClickListener() { // from class: com.android.launcher3.Launcher.21
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int id) {
                        Launcher.this.lambda$startActivitySafely$4$Launcher(v, LauncherModel.getMarketIntent(shortcutInfo.getTargetComponent().getPackageName()), (ItemInfo) shortcutInfo);
                    }
                });
                return;
            } else {
                showBrokenAppInstallDialog(shortcutInfo.getTargetComponent().getPackageName(), new DialogInterface.OnClickListener() { // from class: com.android.launcher3.Launcher.22
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int id) {
                        Launcher.this.startAppShortcutOrInfoActivity(v);
                    }
                });
                return;
            }
        }
        startAppShortcutOrInfoActivity(v);
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onClickAppShortcut(v);
        }
    }

    @Override // android.app.Activity
    protected void onPostResume() {
        super.onPostResume();
    }

    protected void startAppShortcutOrInfoActivity(View v) {
        Intent intent;
        ShortcutInfo shortcutInfo;
        PopupContainerWithArrow open;
        CPUBoostService.boostUp(v.getContext());
        Object tag = v.getTag();
        View originalIcon = null;
        if (tag instanceof ShortcutInfo) {
            shortcutInfo = (ShortcutInfo) tag;
            intent = new Intent(shortcutInfo.intent);
            int[] iArr = new int[2];
            v.getLocationOnScreen(iArr);
            intent.setSourceBounds(new Rect(iArr[0], iArr[1], iArr[0] + v.getWidth(), iArr[1] + v.getHeight()));
        } else if (tag instanceof AppInfo) {
            intent = ((AppInfo) tag).intent;
            shortcutInfo = null;
        } else {
            throw new IllegalArgumentException("Input must be a Shortcut or AppInfo");
        }
        if (LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION.getValue()) {
            if (intent != null && intent.hasCategory(ShortcutInfoCompat.INTENT_CATEGORY) && (open = PopupContainerWithArrow.getOpen(this)) != null) {
                originalIcon = open.getOriginalIcon();
            }
            if (originalIcon == null) {
                originalIcon = v;
            }
            FloatingIconView.fetchIcon(this, originalIcon, (ItemInfo) originalIcon.getTag(), true);
        } else {
            FloatingIconView.fetchIcon(this, v, shortcutInfo, true);
        }
        boolean zStartActivitySafely = startActivitySafely(v, intent, tag);
        LGLog.i("Launcher", "startAppShortcutOrInfoActivity(): intent = " + intent + ", tag = " + tag);
        if (zStartActivitySafely && (v instanceof BubbleTextView)) {
            BubbleTextView bubbleTextView = (BubbleTextView) v;
            this.mWaitingForResume = bubbleTextView;
            bubbleTextView.setStayPressed(true);
        }
        if (this.mHotword.mFolderOpened && getDeviceProfile().isLandscape && getDeviceProfile().isTablet) {
            unlockScreenOrientation(true);
        }
    }

    protected void onClickFolderIcon(View v) {
        if (!(v instanceof FolderIcon)) {
            throw new IllegalArgumentException("Input must be a FolderIcon");
        }
        if (LGHomeFeature.isEnableDefaultHome() || !UninstallModeManager.getInstance(this).checkAndShowUninstallPopup(this, v)) {
            FolderIcon folderIcon = (FolderIcon) v;
            FolderInfo folderInfo = folderIcon.getFolderInfo();
            Folder folderForTag = this.mWorkspace.getFolderForTag(folderInfo);
            if (folderInfo.opened && folderForTag == null) {
                Log.d("Launcher", "Folder info marked as open, but associated folder is not open. Screen: " + folderInfo.screenId + " (" + folderInfo.cellX + ", " + folderInfo.cellY + ")");
                folderInfo.opened = false;
            }
            if (!folderInfo.opened && !folderIcon.getFolder().isDestroyed()) {
                closeFolder(false);
                openFolder(folderIcon);
            } else if (folderForTag != null) {
                int pageForView = this.mWorkspace.getPageForView(folderForTag);
                closeFolder(folderForTag, false);
                if (pageForView != this.mWorkspace.getCurrentPage()) {
                    closeFolder(false);
                    openFolder(folderIcon);
                }
            }
            LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
            if (launcherCallbacks != null) {
                launcherCallbacks.onClickFolderIcon(v);
            }
        }
    }

    protected void onClickAddWidgetButton(View view) {
        if (this.mIsSafeModeEnabled) {
            Toast.makeText(this, R.string.safemode_widget_error, 0).show();
            return;
        }
        showWidgetsView(true, true);
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onClickAddWidgetButton(view);
        }
    }

    protected void onClickWallpaperPicker(View v) {
        startActivityForResult(new Intent("android.intent.action.SET_WALLPAPER").setPackage(getPackageName()), 10);
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onClickWallpaperPicker(v);
        }
    }

    protected void onClickSettingsButton(View v) {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onClickSettingsButton(v);
        } else {
            lambda$startActivitySafely$4$Launcher(v, new Intent(this, (Class<?>) SettingsActivity.class), (ItemInfo) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onClickSwivelSettingsButton(View v) {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onClickSwivelSettingsButton(v);
        } else {
            lambda$startActivitySafely$4$Launcher(v, new Intent(this, (Class<?>) SwivelHomeSettingsActivity.class), (ItemInfo) null);
        }
    }

    public View.OnTouchListener getHapticFeedbackTouchListener() {
        if (this.mHapticFeedbackTouchListener == null) {
            this.mHapticFeedbackTouchListener = new View.OnTouchListener() { // from class: com.android.launcher3.Launcher.23
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    if ((event.getAction() & 255) != 1 || !VibratorManager.isVibratorFeedbackEnabled(v.getContext()) || v == Launcher.this.getAllAppsButton()) {
                        return false;
                    }
                    VibratorManager.performHapticFeedback(v.getContext(), 1);
                    return false;
                }
            };
        }
        return this.mHapticFeedbackTouchListener;
    }

    public void onDragStarted(View view) {
        if (isOnCustomContent()) {
            moveWorkspaceToDefaultScreen();
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onDragStarted(view);
        }
    }

    protected void onInteractionEnd() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onInteractionEnd();
        }
    }

    protected void onInteractionBegin() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onInteractionBegin();
        }
    }

    public void updateInteraction(Workspace.State fromState, Workspace.State toState) {
        boolean z = fromState != Workspace.State.NORMAL;
        if (toState != Workspace.State.NORMAL) {
            onInteractionBegin();
        } else if (z) {
            onInteractionEnd();
        }
    }

    void startApplicationDetailsActivity(ComponentName componentName, UserHandle user) {
        try {
            LauncherAppsCompat.getInstance(this).showAppDetailsForProfile(componentName, user, null, null);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(this, R.string.activity_not_found, 0).show();
            Log.e("Launcher", "Unable to launch settings");
        } catch (SecurityException unused2) {
            Toast.makeText(this, R.string.activity_not_found, 0).show();
            Log.e("Launcher", "Launcher does not have permission to launch settings");
        }
    }

    public boolean startApplicationUninstallActivity(ComponentName componentName, int flags, UserHandle user) {
        if ((flags & 1) == 0) {
            Toast.makeText(this, R.string.uninstall_system_app_text, 0).show();
            return false;
        }
        Intent intent = new Intent("android.intent.action.DELETE", Uri.fromParts(AppNotifierManager.ExtraSpec.USAGE_PACKAGE, componentName.getPackageName(), componentName.getClassName()));
        intent.setFlags(276824064);
        if (user != null) {
            intent.putExtra("android.intent.extra.USER", user);
        }
        startActivity(intent);
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0035  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x010b A[Catch: SecurityException -> 0x0032, TryCatch #1 {SecurityException -> 0x0032, blocks: (B:10:0x0028, B:16:0x0036, B:18:0x0044, B:21:0x0052, B:23:0x005a, B:53:0x0114, B:56:0x011f, B:59:0x012c, B:60:0x0130, B:62:0x014d, B:65:0x0152, B:66:0x0155, B:26:0x0064, B:27:0x006e, B:29:0x0074, B:49:0x010b, B:31:0x007b, B:33:0x007f, B:35:0x008c, B:37:0x0095, B:40:0x00af, B:43:0x00da, B:45:0x00f1, B:61:0x0134), top: B:71:0x0028, inners: #0, #2, #3, #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0130 A[Catch: SecurityException -> 0x0032, TRY_LEAVE, TryCatch #1 {SecurityException -> 0x0032, blocks: (B:10:0x0028, B:16:0x0036, B:18:0x0044, B:21:0x0052, B:23:0x005a, B:53:0x0114, B:56:0x011f, B:59:0x012c, B:60:0x0130, B:62:0x014d, B:65:0x0152, B:66:0x0155, B:26:0x0064, B:27:0x006e, B:29:0x0074, B:49:0x010b, B:31:0x007b, B:33:0x007f, B:35:0x008c, B:37:0x0095, B:40:0x00af, B:43:0x00da, B:45:0x00f1, B:61:0x0134), top: B:71:0x0028, inners: #0, #2, #3, #4 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean startActivity(android.view.View r17, android.content.Intent r18, java.lang.Object r19, android.os.Bundle r20, int r21) {
        /*
            r16 = this;
            r1 = r16
            r0 = r17
            r2 = r18
            r3 = r19
            java.lang.String r4 = "Could not call makeClipRevealAnimation: "
            java.lang.String r5 = "profile"
            com.lge.launcher3.util.LGHomeFeature$Config r6 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_DATA_CONNECTION_DIALOG_VDF
            boolean r6 = r6.getValue()
            r7 = 1
            if (r6 == 0) goto L1e
            boolean r6 = com.lge.launcher3.operator.VDFDataPopup.startDataConnectionDialogActivity(r1, r2, r0, r3)
            if (r6 == 0) goto L1e
            r1.mPendingIntent = r2
            return r7
        L1e:
            r6 = 268435456(0x10000000, float:2.524355E-29)
            r2.addFlags(r6)
            java.lang.String r6 = "Launcher"
            r8 = 0
            if (r0 == 0) goto L35
            java.lang.String r9 = "com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION"
            boolean r9 = r2.hasExtra(r9)     // Catch: java.lang.SecurityException -> L32
            if (r9 != 0) goto L35
            r9 = r7
            goto L36
        L32:
            r0 = move-exception
            goto L156
        L35:
            r9 = r8
        L36:
            com.android.launcher3.compat.LauncherAppsCompat r10 = com.android.launcher3.compat.LauncherAppsCompat.getInstance(r16)     // Catch: java.lang.SecurityException -> L32
            com.android.launcher3.compat.UserManagerCompat r11 = com.android.launcher3.compat.UserManagerCompat.getInstance(r16)     // Catch: java.lang.SecurityException -> L32
            boolean r12 = r2.hasExtra(r5)     // Catch: java.lang.SecurityException -> L32
            if (r12 == 0) goto L4f
            r14 = -1
            long r14 = r2.getLongExtra(r5, r14)     // Catch: java.lang.SecurityException -> L32
            android.os.UserHandle r5 = r11.getUserForSerialNumber(r14)     // Catch: java.lang.SecurityException -> L32
            goto L50
        L4f:
            r5 = 0
        L50:
            if (r9 == 0) goto L110
            com.lge.launcher3.util.LGHomeFeature$Config r9 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION     // Catch: java.lang.SecurityException -> L32
            boolean r9 = r9.getValue()     // Catch: java.lang.SecurityException -> L32
            if (r9 == 0) goto L6e
            boolean r0 = r1.needNoAnimation(r2)     // Catch: java.lang.SecurityException -> L32
            if (r0 != 0) goto L64
            r0 = r20
            goto L112
        L64:
            android.app.ActivityOptions r0 = r16.getNoAnimActivityOption()     // Catch: java.lang.SecurityException -> L32
            android.os.Bundle r0 = r0.toBundle()     // Catch: java.lang.SecurityException -> L32
            goto L112
        L6e:
            boolean r9 = com.lge.launcher3.util.Utilities.isLGUI7_0()     // Catch: java.lang.SecurityException -> L32
            if (r9 == 0) goto L7b
            com.android.launcher3.util.ActivityOptionsWrapper r0 = r16.makeActivityAnimation(r17)     // Catch: java.lang.SecurityException -> L32
        L78:
            r4 = 0
            goto L109
        L7b:
            java.lang.reflect.Method r9 = com.android.launcher3.Launcher.sClipRevealMethod     // Catch: java.lang.SecurityException -> L32
            if (r9 == 0) goto L107
            int r9 = r17.getMeasuredWidth()     // Catch: java.lang.SecurityException -> L32
            int r11 = r17.getMeasuredHeight()     // Catch: java.lang.SecurityException -> L32
            boolean r12 = r0 instanceof android.widget.TextView     // Catch: java.lang.SecurityException -> L32
            r14 = 2
            if (r12 == 0) goto Lac
            r12 = r0
            android.widget.TextView r12 = (android.widget.TextView) r12     // Catch: java.lang.SecurityException -> L32
            android.graphics.drawable.Drawable r12 = com.android.launcher3.Workspace.getTextViewIcon(r12)     // Catch: java.lang.SecurityException -> L32
            if (r12 == 0) goto Lac
            android.graphics.Rect r11 = r12.getBounds()     // Catch: java.lang.SecurityException -> L32
            int r12 = r11.width()     // Catch: java.lang.SecurityException -> L32
            int r9 = r9 - r12
            int r9 = r9 / r14
            int r12 = r17.getPaddingTop()     // Catch: java.lang.SecurityException -> L32
            int r15 = r11.width()     // Catch: java.lang.SecurityException -> L32
            int r11 = r11.height()     // Catch: java.lang.SecurityException -> L32
            goto Laf
        Lac:
            r12 = r8
            r15 = r9
            r9 = r12
        Laf:
            java.lang.reflect.Method r13 = com.android.launcher3.Launcher.sClipRevealMethod     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            r14 = 5
            java.lang.Object[] r14 = new java.lang.Object[r14]     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            r14[r8] = r0     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            r14[r7] = r0     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r12)     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            r9 = 2
            r14[r9] = r0     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            r0 = 3
            java.lang.Integer r9 = java.lang.Integer.valueOf(r15)     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            r14[r0] = r9     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            r0 = 4
            java.lang.Integer r9 = java.lang.Integer.valueOf(r11)     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            r14[r0] = r9     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            r9 = 0
            java.lang.Object r0 = r13.invoke(r9, r14)     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            com.android.launcher3.util.ActivityOptionsWrapper r0 = (com.android.launcher3.util.ActivityOptionsWrapper) r0     // Catch: java.lang.SecurityException -> L32 java.lang.reflect.InvocationTargetException -> Ld9 java.lang.IllegalAccessException -> Lf0
            goto L78
        Ld9:
            r0 = move-exception
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.SecurityException -> L32
            r9.<init>()     // Catch: java.lang.SecurityException -> L32
            r9.append(r4)     // Catch: java.lang.SecurityException -> L32
            r9.append(r0)     // Catch: java.lang.SecurityException -> L32
            java.lang.String r0 = r9.toString()     // Catch: java.lang.SecurityException -> L32
            android.util.Log.d(r6, r0)     // Catch: java.lang.SecurityException -> L32
            r4 = 0
            com.android.launcher3.Launcher.sClipRevealMethod = r4     // Catch: java.lang.SecurityException -> L32
            goto L107
        Lf0:
            r0 = move-exception
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.SecurityException -> L32
            r9.<init>()     // Catch: java.lang.SecurityException -> L32
            r9.append(r4)     // Catch: java.lang.SecurityException -> L32
            r9.append(r0)     // Catch: java.lang.SecurityException -> L32
            java.lang.String r0 = r9.toString()     // Catch: java.lang.SecurityException -> L32
            android.util.Log.d(r6, r0)     // Catch: java.lang.SecurityException -> L32
            r4 = 0
            com.android.launcher3.Launcher.sClipRevealMethod = r4     // Catch: java.lang.SecurityException -> L32
            goto L108
        L107:
            r4 = 0
        L108:
            r0 = r4
        L109:
            if (r0 == 0) goto L111
            android.os.Bundle r0 = r0.toBundle()     // Catch: java.lang.SecurityException -> L32
            goto L112
        L110:
            r4 = 0
        L111:
            r0 = r4
        L112:
            if (r5 == 0) goto L130
            android.os.UserHandle r4 = android.os.Process.myUserHandle()     // Catch: java.lang.SecurityException -> L32
            boolean r4 = r5.equals(r4)     // Catch: java.lang.SecurityException -> L32
            if (r4 == 0) goto L11f
            goto L130
        L11f:
            android.content.ComponentName r4 = r18.getComponent()     // Catch: java.lang.SecurityException -> L32 java.lang.Exception -> L12b
            android.graphics.Rect r9 = r18.getSourceBounds()     // Catch: java.lang.SecurityException -> L32 java.lang.Exception -> L12b
            r10.startActivityForProfile(r4, r5, r9, r0)     // Catch: java.lang.SecurityException -> L32 java.lang.Exception -> L12b
            goto L150
        L12b:
            r0 = move-exception
            r0.printStackTrace()     // Catch: java.lang.SecurityException -> L32
            goto L150
        L130:
            android.os.StrictMode$VmPolicy r4 = android.os.StrictMode.getVmPolicy()     // Catch: java.lang.SecurityException -> L32
            android.os.StrictMode$VmPolicy$Builder r5 = new android.os.StrictMode$VmPolicy$Builder     // Catch: java.lang.Throwable -> L151
            r5.<init>()     // Catch: java.lang.Throwable -> L151
            android.os.StrictMode$VmPolicy$Builder r5 = r5.detectAll()     // Catch: java.lang.Throwable -> L151
            android.os.StrictMode$VmPolicy$Builder r5 = r5.penaltyLog()     // Catch: java.lang.Throwable -> L151
            android.os.StrictMode$VmPolicy r5 = r5.build()     // Catch: java.lang.Throwable -> L151
            android.os.StrictMode.setVmPolicy(r5)     // Catch: java.lang.Throwable -> L151
            r5 = r21
            r1.startActivityForResult(r2, r5, r0)     // Catch: java.lang.Throwable -> L151
            android.os.StrictMode.setVmPolicy(r4)     // Catch: java.lang.SecurityException -> L32
        L150:
            return r7
        L151:
            r0 = move-exception
            android.os.StrictMode.setVmPolicy(r4)     // Catch: java.lang.SecurityException -> L32
            throw r0     // Catch: java.lang.SecurityException -> L32
        L156:
            r4 = 2131820618(0x7f11004a, float:1.9273956E38)
            android.widget.Toast r4 = android.widget.Toast.makeText(r1, r4, r8)
            r4.show()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Launcher does not have the permission to launch . Make sure to create a MAIN intent-filter for the corresponding activity or use the exported attribute for this activity. tag="
            r4.append(r5)
            r4.append(r3)
            java.lang.String r3 = " intent="
            r4.append(r3)
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            android.util.Log.e(r6, r2, r0)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Launcher.startActivity(android.view.View, android.content.Intent, java.lang.Object, android.os.Bundle, int):boolean");
    }

    @Override // com.android.launcher3.BaseDraggingActivity
    public Rect getViewBounds(View v) {
        int[] iArr = new int[2];
        v.getLocationOnScreen(iArr);
        return new Rect(iArr[0], iArr[1], iArr[0] + v.getWidth(), iArr[1] + v.getHeight());
    }

    public boolean startActivitySafely(View v, Intent intent, Object tag) {
        return startActivitySafely(v, intent, tag, -1);
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0045  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean startActivitySafely(android.view.View r12, android.content.Intent r13, java.lang.Object r14, int r15) {
        /*
            r11 = this;
            boolean r0 = r11.mIsSafeModeEnabled
            r1 = 0
            if (r0 == 0) goto L16
            boolean r0 = com.android.launcher3.Utilities.isSystemApp(r11, r13)
            if (r0 != 0) goto L16
            r12 = 2131821055(0x7f1101ff, float:1.9274842E38)
            android.widget.Toast r12 = android.widget.Toast.makeText(r11, r12, r1)
            r12.show()
            return r1
        L16:
            r0 = 1
            if (r12 == 0) goto L23
            java.lang.String r2 = "com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION"
            boolean r2 = r13.hasExtra(r2)
            if (r2 != 0) goto L23
            r2 = r0
            goto L24
        L23:
            r2 = r1
        L24:
            com.lge.launcher3.util.LGHomeFeature$Config r3 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION
            boolean r3 = r3.getValue()
            r4 = 0
            if (r3 == 0) goto L45
            if (r13 == 0) goto L42
            java.lang.String r3 = "com.android.launcher3.DEEP_SHORTCUT"
            boolean r3 = r13.hasCategory(r3)
            if (r3 == 0) goto L42
            com.android.launcher3.popup.PopupContainerWithArrow r3 = com.android.launcher3.popup.PopupContainerWithArrow.getOpen(r11)
            if (r3 == 0) goto L42
            android.view.View r3 = r3.getOriginalIcon()
            goto L43
        L42:
            r3 = r4
        L43:
            if (r3 != 0) goto L46
        L45:
            r3 = r12
        L46:
            if (r2 == 0) goto L52
            com.android.launcher3.util.ActivityOptionsWrapper r2 = r11.getActivityLaunchOptions(r3)
            android.os.Bundle r2 = r2.toBundle()
            r9 = r2
            goto L53
        L52:
            r9 = r4
        L53:
            java.lang.String r2 = "profile"
            boolean r5 = r13.hasExtra(r2)
            if (r5 == 0) goto L68
            r5 = -1
            long r5 = r13.getLongExtra(r2, r5)
            com.android.launcher3.compat.UserManagerCompat r2 = com.android.launcher3.compat.UserManagerCompat.getInstance(r11)
            r2.getUserForSerialNumber(r5)
        L68:
            r2 = 268435456(0x10000000, float:2.524355E-29)
            r13.addFlags(r2)
            if (r3 == 0) goto L76
            android.graphics.Rect r2 = r11.getViewBounds(r3)
            r13.setSourceBounds(r2)
        L76:
            boolean r2 = r14 instanceof com.android.launcher3.model.data.ItemInfo
            if (r2 == 0) goto L7e
            r2 = r14
            com.android.launcher3.model.data.ItemInfo r2 = (com.android.launcher3.model.data.ItemInfo) r2
            goto L7f
        L7e:
            r2 = r4
        L7f:
            boolean r3 = com.android.launcher3.Utilities.ATLEAST_MARSHMALLOW     // Catch: android.content.ActivityNotFoundException -> La1
            if (r3 == 0) goto L96
            if (r2 == 0) goto L96
            int r3 = r2.itemType     // Catch: android.content.ActivityNotFoundException -> La1
            r5 = 6
            if (r3 != r5) goto L96
            r3 = r2
            com.android.launcher3.ShortcutInfo r3 = (com.android.launcher3.ShortcutInfo) r3     // Catch: android.content.ActivityNotFoundException -> La1
            android.content.Intent r3 = r3.promisedIntent     // Catch: android.content.ActivityNotFoundException -> La1
            if (r3 != 0) goto L96
            r11.startShortcutIntentSafely(r13, r9, r2)     // Catch: android.content.ActivityNotFoundException -> La1
            r1 = r0
            goto Lec
        L96:
            r5 = r11
            r6 = r12
            r7 = r13
            r8 = r14
            r10 = r15
            boolean r13 = r5.startActivity(r6, r7, r8, r9, r10)     // Catch: android.content.ActivityNotFoundException -> La1
            r1 = r13
            goto Lec
        La1:
            r15 = move-exception
            java.lang.String r2 = r13.getPackage()
            if (r2 != 0) goto Lb6
            android.content.ComponentName r3 = r13.getComponent()
            if (r3 == 0) goto Lb6
            android.content.ComponentName r2 = r13.getComponent()
            java.lang.String r2 = r2.getPackageName()
        Lb6:
            com.lge.mdm.LGMDMManagerInternal r3 = com.lge.mdm.LGMDMManagerInternal.getInstance()
            android.content.Context r5 = r11.getApplicationContext()
            int r2 = r3.checkStartActivity(r5, r2, r4)
            if (r2 != 0) goto Lce
            r2 = 2131820618(0x7f11004a, float:1.9273956E38)
            android.widget.Toast r2 = android.widget.Toast.makeText(r11, r2, r1)
            r2.show()
        Lce:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unable to launch. tag="
            r2.append(r3)
            r2.append(r14)
            java.lang.String r14 = " intent="
            r2.append(r14)
            r2.append(r13)
            java.lang.String r13 = r2.toString()
            java.lang.String r14 = "Launcher"
            android.util.Log.e(r14, r13, r15)
        Lec:
            if (r1 == 0) goto Lff
            boolean r13 = r12 instanceof com.android.launcher3.BubbleTextView
            if (r13 == 0) goto Lff
            com.android.launcher3.BubbleTextView r12 = (com.android.launcher3.BubbleTextView) r12
            r12.setStayPressed(r0)
            com.android.launcher3.-$$Lambda$Launcher$G6gepsyg6qxvo-8gFp9PCC1JLG8 r13 = new com.android.launcher3.-$$Lambda$Launcher$G6gepsyg6qxvo-8gFp9PCC1JLG8
            r13.<init>()
            r11.addOnResumeCallback(r13)
        Lff:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Launcher.startActivitySafely(android.view.View, android.content.Intent, java.lang.Object, int):boolean");
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$startActivitySafely$4$Launcher(Landroid/view/View;Landroid/content/Intent;Lcom/android/launcher3/model/data/ItemInfo;)V */
    @Override // com.android.launcher3.BaseDraggingActivity
    /* JADX INFO: renamed from: startActivitySafely, reason: merged with bridge method [inline-methods] */
    public boolean lambda$startActivitySafely$4$Launcher(final View v, final Intent intent, final ItemInfo item) {
        if (!hasBeenResumed()) {
            addOnResumeCallback(new Runnable() { // from class: com.android.launcher3.-$$Lambda$Launcher$RCSMucgQc8g1aGGHBs-999YOMQE
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startActivitySafely$4$Launcher(v, intent, item);
                }
            });
            return true;
        }
        boolean zStartActivitySafely = super.lambda$startActivitySafely$4$Launcher(v, intent, item);
        if (zStartActivitySafely && (v instanceof BubbleTextView)) {
            final BubbleTextView bubbleTextView = (BubbleTextView) v;
            bubbleTextView.setStayPressed(true);
            addOnResumeCallback(new Runnable() { // from class: com.android.launcher3.-$$Lambda$Launcher$-v-vwrqnDIw-e5FdPQsZCQLkgfA
                @Override // java.lang.Runnable
                public final void run() {
                    bubbleTextView.setStayPressed(false);
                }
            });
        }
        return zStartActivitySafely;
    }

    public void updateSearchedApp(ComponentName appComponentName) {
        QuickstepTransitionManager quickstepTransitionManager = this.mAppTransitionManager;
        if (quickstepTransitionManager == null || !(quickstepTransitionManager instanceof QuickstepTransitionManager)) {
            return;
        }
        quickstepTransitionManager.updateSearchedApp(appComponentName);
    }

    private void copyFolderIconToImage(FolderIcon fi) {
        BaseDragLayer.LayoutParams layoutParams;
        int measuredWidth = fi.getMeasuredWidth();
        int measuredHeight = fi.getMeasuredHeight();
        fi.setTextVisible(false);
        if (this.mFolderIconImageView == null) {
            this.mFolderIconImageView = new ImageView(this);
        }
        Bitmap bitmap = this.mFolderIconBitmap;
        if (bitmap == null || bitmap.getWidth() != measuredWidth || this.mFolderIconBitmap.getHeight() != measuredHeight) {
            this.mFolderIconBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            this.mFolderIconCanvas = new Canvas(this.mFolderIconBitmap);
        }
        if (this.mFolderIconImageView.getLayoutParams() instanceof BaseDragLayer.LayoutParams) {
            layoutParams = (BaseDragLayer.LayoutParams) this.mFolderIconImageView.getLayoutParams();
        } else {
            layoutParams = new BaseDragLayer.LayoutParams(measuredWidth, measuredHeight);
        }
        float descendantRectRelativeToSelf = this.mDragLayer.getDescendantRectRelativeToSelf(fi, this.mRectForFolderAnimation);
        layoutParams.customPosition = true;
        layoutParams.x = this.mRectForFolderAnimation.left;
        layoutParams.y = this.mRectForFolderAnimation.top;
        layoutParams.width = (int) (measuredWidth * descendantRectRelativeToSelf);
        layoutParams.height = (int) (descendantRectRelativeToSelf * measuredHeight);
        this.mFolderIconCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        fi.draw(this.mFolderIconCanvas);
        this.mFolderIconImageView.setImageBitmap(this.mFolderIconBitmap);
        if (fi.getFolder() != null) {
            this.mFolderIconImageView.setPivotX(fi.getFolder().getPivotXForIconAnimation());
            this.mFolderIconImageView.setPivotY(fi.getFolder().getPivotYForIconAnimation());
        }
        if (this.mDragLayer.indexOfChild(this.mFolderIconImageView) != -1) {
            this.mDragLayer.removeView(this.mFolderIconImageView);
        }
        this.mDragLayer.addView(this.mFolderIconImageView, layoutParams);
        if (fi.getFolder() != null) {
            fi.getFolder().bringToFront();
        }
        if (fi.getParent().getParent().getParent() != this.mHotseat || this.mDeviceProfile.isLandscape) {
            fi.setTextVisible(true);
        }
    }

    private void growAndFadeOutFolderIcon(final FolderIcon fi) {
        if (fi == null) {
            LGLog.i("Launcher", "Folder: skip growAndFadeOutFolderIcon - fi is null");
            return;
        }
        PropertyValuesHolder propertyValuesHolderOfFloat = PropertyValuesHolder.ofFloat("alpha", 0.0f);
        PropertyValuesHolder propertyValuesHolderOfFloat2 = PropertyValuesHolder.ofFloat("scaleX", 1.5f);
        PropertyValuesHolder propertyValuesHolderOfFloat3 = PropertyValuesHolder.ofFloat("scaleY", 1.5f);
        int integer = getResources().getInteger(R.integer.config_folderEnlargeDuration);
        if (LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue()) {
            propertyValuesHolderOfFloat = PropertyValuesHolder.ofFloat("alpha", 0.0f);
            propertyValuesHolderOfFloat2 = PropertyValuesHolder.ofFloat("scaleX", 3.0f);
            propertyValuesHolderOfFloat3 = PropertyValuesHolder.ofFloat("scaleY", 3.5f);
        }
        if (fi.getFolderInfo().container == -101) {
            CellLayout cellLayout = (CellLayout) fi.getParent().getParent();
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) fi.getLayoutParams();
            cellLayout.setFolderLeaveBehindCell(layoutParams.cellX, layoutParams.cellY);
        }
        copyFolderIconToImage(fi);
        fi.setVisibility(4);
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(this.mFolderIconImageView, propertyValuesHolderOfFloat2, propertyValuesHolderOfFloat3);
        ObjectAnimator objectAnimatorOfPropertyValuesHolder2 = LauncherAnimUtils.ofPropertyValuesHolder(this.mFolderIconImageView, propertyValuesHolderOfFloat);
        if (Utilities.isLmpOrAbove()) {
            objectAnimatorOfPropertyValuesHolder.setInterpolator(new LogDecelerateInterpolator(100, 0));
            objectAnimatorOfPropertyValuesHolder2.setInterpolator(new LogDecelerateInterpolator(100, 0));
        }
        objectAnimatorOfPropertyValuesHolder.setDuration((int) (((double) integer) / 2.7d));
        objectAnimatorOfPropertyValuesHolder2.setDuration(integer / 5);
        AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        animatorSetCreateAnimatorSet.play(objectAnimatorOfPropertyValuesHolder);
        animatorSetCreateAnimatorSet.play(objectAnimatorOfPropertyValuesHolder2);
        animatorSetCreateAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.Launcher.24
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (Float.compare(0.0f, Launcher.this.mFolderIconImageView.getAlpha()) != 0) {
                    Launcher.this.mFolderIconImageView.setAlpha(0.0f);
                }
            }
        });
        animatorSetCreateAnimatorSet.start();
    }

    private void shrinkAndFadeInFolderIcon(final FolderIcon fi, boolean animate) {
        if (fi == null || fi.getParent() == null || fi.getParent().getParent() == null) {
            LGLog.i("Launcher", "Folder: skip shrinkAndFadeInFolderIcon - fi = " + fi);
            return;
        }
        PropertyValuesHolder propertyValuesHolderOfFloat = PropertyValuesHolder.ofFloat("alpha", 1.0f);
        PropertyValuesHolder propertyValuesHolderOfFloat2 = PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder propertyValuesHolderOfFloat3 = PropertyValuesHolder.ofFloat("scaleY", 1.0f);
        PropertyValuesHolder propertyValuesHolderOfFloat4 = PropertyValuesHolder.ofFloat("translationY", 0.0f);
        final CellLayout cellLayout = (CellLayout) fi.getParent().getParent();
        this.mDragLayer.removeView(this.mFolderIconImageView);
        closeFolderIconToImage(fi);
        AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(this.mFolderIconImageView, propertyValuesHolderOfFloat, propertyValuesHolderOfFloat2, propertyValuesHolderOfFloat3);
        ObjectAnimator objectAnimatorOfPropertyValuesHolder2 = LauncherAnimUtils.ofPropertyValuesHolder(this.mFolderIconImageView, propertyValuesHolderOfFloat4);
        int integer = getResources().getInteger(R.integer.config_folderShrinkDuration) / 6;
        int integer2 = getResources().getInteger(R.integer.config_folderShrinkDuration) - integer;
        objectAnimatorOfPropertyValuesHolder.setStartDelay(integer);
        objectAnimatorOfPropertyValuesHolder.setDuration(integer2);
        objectAnimatorOfPropertyValuesHolder2.setDuration(integer2 - integer);
        objectAnimatorOfPropertyValuesHolder.setInterpolator(new LogDecelerateInterpolator(100, 0));
        objectAnimatorOfPropertyValuesHolder2.setInterpolator(new LogDecelerateInterpolator(100, 0));
        animatorSetCreateAnimatorSet.playTogether(objectAnimatorOfPropertyValuesHolder, objectAnimatorOfPropertyValuesHolder2);
        animatorSetCreateAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.Launcher.25
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                CellLayout cellLayout2 = cellLayout;
                if (cellLayout2 != null) {
                    cellLayout2.clearFolderLeaveBehind();
                    Launcher.this.mDragLayer.removeView(Launcher.this.mFolderIconImageView);
                    fi.setVisibility(0);
                }
                if (Launcher.this.mDragController == null || Launcher.this.mDragController.isDragging()) {
                    return;
                }
                Launcher.this.getHotseat().getLayout().cleanupVacantCell(true);
            }
        });
        animatorSetCreateAnimatorSet.start();
        if (animate) {
            return;
        }
        animatorSetCreateAnimatorSet.end();
    }

    public void openFolder(final FolderIcon folderIcon) {
        int[] iArr = new int[2];
        if (folderIcon != null) {
            folderIcon.getLocationOnScreen(iArr);
            setBlurBGPivotX(iArr[0]);
            setBlurBGPivotY(isInMultiWindowMode() ? iArr[1] * 0.9f : iArr[1]);
        }
        Folder folder = folderIcon.getFolder();
        Workspace workspace = this.mWorkspace;
        Folder openFolder = workspace != null ? workspace.getOpenFolder() : null;
        if (openFolder != null && openFolder != folder) {
            closeFolder(false);
        }
        WallpaperMotionManager wallpaperMotionManager = this.mWallpaperMotionManager;
        if (wallpaperMotionManager != null) {
            wallpaperMotionManager.setEnableParallax(false);
        }
        FolderInfo folderInfo = folder.mInfo;
        folderInfo.opened = true;
        ((CellLayout.LayoutParams) folderIcon.getLayoutParams()).canReorder = false;
        if (folder.getParent() == null) {
            LGLog.i("Launcher", "Folder: openFolder - addView info.opened = " + folderInfo.opened + ", " + folderInfo);
            this.mDragLayer.addView(folder);
            this.mDragController.addDropTarget(folder);
        } else {
            Log.w("Launcher", "Opening folder (" + folder + ") which already has a parent (" + folder.getParent() + ").");
        }
        lockScreenOrientation();
        folder.animateOpen();
        growAndFadeOutFolderIcon(folderIcon);
        folder.sendAccessibilityEvent(32);
        getDragLayer().sendAccessibilityEvent(2048);
        folder.setFocusOnFirstChild();
        HomescreenBlurManager homescreenBlurManager = HomescreenBlurManager.getInstance(this);
        int expandDuration = folder.getExpandDuration();
        if (LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue()) {
            expandDuration /= 2;
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_EDITMODE_UI.getValue()) {
            if (isInState(LauncherState.ALL_APPS)) {
                homescreenBlurManager.showBackground(HomescreenBlurManager.BackgroundType.MIDDLE_ROOTVIEW, 0);
            } else if (LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue() && !StaticBlurEngine.getInstance().isPowerSaveEnabled(getApplicationContext())) {
                homescreenBlurManager.showBackgroundWithBlurAnim(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER_DIM, expandDuration * 2, false, true);
            } else {
                homescreenBlurManager.showBackgroundWithScale(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER_DIM, expandDuration * 2);
            }
        } else if (isInState(LauncherState.ALL_APPS)) {
            homescreenBlurManager.showBackground(HomescreenBlurManager.BackgroundType.MIDDLE_ROOTVIEW, 0);
        } else if (LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue() && !StaticBlurEngine.getInstance().isPowerSaveEnabled(getApplicationContext())) {
            homescreenBlurManager.showBackgroundWithBlurAnim(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER, expandDuration * 2, false, true);
        } else {
            homescreenBlurManager.showBackgroundWithScale(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER, expandDuration * 2);
        }
        if (isInState(LauncherState.ALL_APPS)) {
            allappFolderBackgroundAnim(true);
            getRootView().setDisallowBackGesture(true);
        } else if (this.mWorkspace.isInOverviewMode()) {
            editFolderBackgroundAnim(true, true);
        } else {
            Workspace workspace2 = this.mWorkspace;
            this.mFolderAnimUseCellLayout = workspace2.getPageAt(workspace2.getCurrentPage());
            folderBackgroundAnim(true);
        }
        this.mHotword.openFolder();
        UninstallModeManager.getInstance(getApplicationContext()).setFolderOpen(true);
        if (com.lge.launcher3.util.Utilities.isLGUI7_0()) {
            AdaptiveTextUtil.setAdaptiveSystemUi(getWindow().getDecorView(), this, false);
        } else {
            if (!isInState(LauncherState.ALL_APPS) || this.mAppsCustomizeHost.isInArrangeMode()) {
                return;
            }
            AdaptiveTextUtil.setAdaptiveSystemUi(getWindow().getDecorView(), this, true);
        }
    }

    public void closeFolder(boolean... animate) {
        Workspace workspace = this.mWorkspace;
        Folder openFolder = workspace != null ? workspace.getOpenFolder() : null;
        if (openFolder != null) {
            if (openFolder.isEditingName()) {
                openFolder.dismissEditingName();
            }
            if (openFolder.getInfo() instanceof AllAppsFolderInfo) {
                boolean z = isInState(LauncherState.ALL_APPS) && (animate.length <= 0 || animate[0]);
                LGLog.i("Launcher", "closeAllAppsFolder() : animate = " + z);
                closeFolder(openFolder, z);
            } else {
                closeFolder(openFolder, animate);
            }
            this.mHotword.closeFolder();
        } else if (folderCloseByHomeKey) {
            LGLog.d("Launcher", "Folder: closeFolder - folderCloseByHomeKey is true");
            folderCloseByHomeKey = false;
            return;
        } else {
            Workspace workspace2 = this.mWorkspace;
            Folder invalidOpenFolder = workspace2 != null ? workspace2.getInvalidOpenFolder() : null;
            if (invalidOpenFolder != null) {
                LGLog.w("Launcher", "Find invalidopenedfolder -- force close!", new int[0]);
                invalidOpenFolder.close();
            }
        }
        if (isInState(LauncherState.ALL_APPS)) {
            getRootView().setDisallowBackGesture(false);
        }
        UninstallModeManager.getInstance(getApplicationContext()).setFolderOpen(false);
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x003a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void closeFolder(com.android.launcher3.folder.Folder r9, boolean... r10) {
        /*
            r8 = this;
            android.content.Intent r0 = new android.content.Intent
            com.lge.launcher3.config.IntentConst$Action r1 = com.lge.launcher3.config.IntentConst.Action.ACTION_FINISH_FOLDERPLUS
            android.content.Context r2 = r8.getApplicationContext()
            java.lang.String r1 = r1.getValue(r2)
            r0.<init>(r1)
            r8.sendBroadcast(r0)
            com.android.launcher3.model.data.FolderInfo r0 = r9.getInfo()
            r1 = 0
            r0.opened = r1
            java.lang.String r0 = "Launcher"
            java.lang.String r2 = "Folder: closeFolder - start"
            com.lge.launcher3.util.LGLog.d(r0, r2)
            int r2 = r10.length
            r3 = 1
            if (r2 <= 0) goto L27
            boolean r2 = r10[r1]
            goto L28
        L27:
            r2 = r3
        L28:
            android.view.View r4 = r8.mFolderAnimUseCellLayout
            r5 = 1065353216(0x3f800000, float:1.0)
            if (r4 == 0) goto L3a
            com.android.launcher3.Workspace r6 = r8.mWorkspace
            int r7 = r6.getCurrentPage()
            android.view.View r6 = r6.getPageAt(r7)
            if (r4 == r6) goto L77
        L3a:
            r4 = r1
        L3b:
            com.android.launcher3.Workspace r6 = r8.mWorkspace
            int r6 = r6.getChildCount()
            if (r4 >= r6) goto L6b
            com.android.launcher3.Workspace r6 = r8.mWorkspace
            android.view.View r6 = r6.getPageAt(r4)
            r6.setAlpha(r5)
            com.android.launcher3.Workspace r6 = r8.mWorkspace
            android.view.View r6 = r6.getPageAt(r4)
            r6.setScaleY(r5)
            com.android.launcher3.Workspace r6 = r8.mWorkspace
            android.view.View r6 = r6.getPageAt(r4)
            r6.setScaleX(r5)
            com.android.launcher3.Workspace r6 = r8.mWorkspace
            android.view.View r6 = r6.getPageAt(r4)
            r7 = 0
            r6.setTranslationY(r7)
            int r4 = r4 + 1
            goto L3b
        L6b:
            com.android.launcher3.Workspace r4 = r8.mWorkspace
            int r6 = r4.getCurrentPage()
            android.view.View r4 = r4.getPageAt(r6)
            r8.mFolderAnimUseCellLayout = r4
        L77:
            android.view.ViewParent r4 = r9.getParent()
            android.view.ViewParent r4 = r4.getParent()
            android.view.ViewGroup r4 = (android.view.ViewGroup) r4
            if (r4 == 0) goto L9f
            com.android.launcher3.Workspace r0 = r8.mWorkspace
            com.android.launcher3.model.data.FolderInfo r4 = r9.mInfo
            android.view.View r0 = r0.getViewForTag(r4)
            com.android.launcher3.folder.FolderIcon r0 = (com.android.launcher3.folder.FolderIcon) r0
            if (r0 != 0) goto L91
            com.android.launcher3.folder.FolderIcon r0 = r9.mFolderIcon
        L91:
            r8.shrinkAndFadeInFolderIcon(r0, r2)
            if (r0 == 0) goto La4
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r0 = (com.android.launcher3.CellLayout.LayoutParams) r0
            r0.canReorder = r3
            goto La4
        L9f:
            java.lang.String r4 = "Folder: closeFolder - parent is null"
            com.lge.launcher3.util.LGLog.i(r0, r4)
        La4:
            r9.animateClosed(r10)
            r8.unlockScreenOrientation(r1)
            com.android.launcher3.dragndrop.DragLayer r10 = r8.getDragLayer()
            r0 = 32
            r10.sendAccessibilityEvent(r0)
            int r9 = r9.getExpandDuration()
            int r9 = r9 / 2
            com.lge.launcher3.wallpaperblur.HomescreenBlurManager r10 = com.lge.launcher3.wallpaperblur.HomescreenBlurManager.getInstance(r8)
            com.lge.launcher3.util.LGHomeFeature$Config r0 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_EDITMODE_UI
            boolean r0 = r0.getValue()
            if (r0 == 0) goto L157
            com.android.launcher3.LauncherState r0 = com.android.launcher3.LauncherState.ALL_APPS
            boolean r0 = r8.isInState(r0)
            if (r0 != 0) goto L134
            com.android.launcher3.LauncherState r0 = com.android.launcher3.LauncherState.OVERVIEW
            boolean r0 = r8.isInState(r0)
            if (r0 == 0) goto Lda
            com.android.launcher3.allapps.AllAppsTransitionController r0 = r8.mAllAppsController
            if (r0 == 0) goto Lda
            goto L134
        Lda:
            com.android.launcher3.Workspace r0 = r8.mWorkspace
            boolean r0 = r0.isInOverviewMode()
            if (r0 == 0) goto Le8
            com.lge.launcher3.wallpaperblur.HomescreenBlurManager$BackgroundType r9 = com.lge.launcher3.wallpaperblur.HomescreenBlurManager.BackgroundType.BOTTOM_ROOTVIEW
            r10.showBackground(r9, r1)
            goto L13f
        Le8:
            com.android.launcher3.Workspace r0 = r8.mWorkspace
            com.android.launcher3.Workspace$State r0 = r0.getState()
            com.android.launcher3.Workspace$State r4 = com.android.launcher3.Workspace.State.SPRING_LOADED
            if (r0 == r4) goto L10e
            com.android.launcher3.Workspace r0 = r8.mWorkspace
            boolean r0 = r0.isInOverviewMode()
            if (r0 != 0) goto L10e
            com.android.launcher3.LauncherState r0 = com.android.launcher3.LauncherState.ALL_APPS
            boolean r0 = r8.isInState(r0)
            if (r0 == 0) goto L10b
            com.lge.launcher3.allapps.AllAppsHost r0 = r8.mAppsCustomizeHost
            boolean r0 = r0.isInArrangeMode()
            if (r0 == 0) goto L10b
            goto L10e
        L10b:
            com.lge.launcher3.wallpaperblur.HomescreenBlurManager$BackgroundType r0 = com.lge.launcher3.wallpaperblur.HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER
            goto L110
        L10e:
            com.lge.launcher3.wallpaperblur.HomescreenBlurManager$BackgroundType r0 = com.lge.launcher3.wallpaperblur.HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER_DIM
        L110:
            com.lge.launcher3.util.LGHomeFeature$Config r4 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE
            boolean r4 = r4.getValue()
            if (r4 == 0) goto L12a
            com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine r4 = com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine.getInstance()
            android.content.Context r6 = r8.getApplicationContext()
            boolean r4 = r4.isPowerSaveEnabled(r6)
            if (r4 != 0) goto L12a
            r10.hideBackgroundWithBlurAnim(r0, r9, r1, r1)
            goto L13f
        L12a:
            if (r2 == 0) goto L130
            r10.hideBackgroundWithScale(r0, r9)
            goto L13f
        L130:
            r10.hideBackground(r0, r1)
            goto L13f
        L134:
            android.util.FloatProperty<com.android.launcher3.allapps.AllAppsTransitionController> r9 = com.android.launcher3.allapps.AllAppsTransitionController.BLUR_PROGRESS
            com.android.launcher3.allapps.AllAppsTransitionController r0 = r8.mAllAppsController
            java.lang.Float r4 = java.lang.Float.valueOf(r5)
            r9.set(r0, r4)
        L13f:
            com.lge.launcher3.util.LGHomeFeature$Config r9 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r9 = r9.getValue()
            if (r9 != 0) goto L178
            boolean r9 = r10.isLiveWallpaperMode()
            if (r9 != 0) goto L153
            int r9 = r8.getHomeVisibility()
            if (r9 == 0) goto L178
        L153:
            r8.setHomeVisibility(r1, r2)
            goto L178
        L157:
            com.lge.launcher3.util.LGHomeFeature$Config r0 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE
            boolean r0 = r0.getValue()
            if (r0 == 0) goto L173
            com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine r0 = com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine.getInstance()
            android.content.Context r4 = r8.getApplicationContext()
            boolean r0 = r0.isPowerSaveEnabled(r4)
            if (r0 != 0) goto L173
            com.lge.launcher3.wallpaperblur.HomescreenBlurManager$BackgroundType r0 = com.lge.launcher3.wallpaperblur.HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER
            r10.hideBackgroundWithBlurAnim(r0, r9, r1, r1)
            goto L178
        L173:
            com.lge.launcher3.wallpaperblur.HomescreenBlurManager$BackgroundType r0 = com.lge.launcher3.wallpaperblur.HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER
            r10.hideBackgroundWithScale(r0, r9)
        L178:
            com.android.launcher3.LauncherState r9 = com.android.launcher3.LauncherState.ALL_APPS
            boolean r9 = r8.isInState(r9)
            if (r9 != 0) goto L1ea
            com.lge.launcher3.allapps.AllAppsHost r9 = r8.getAllAppsHost()
            if (r9 == 0) goto L193
            com.lge.launcher3.allapps.AllAppsHost r9 = r8.getAllAppsHost()
            float r9 = r9.getScaleX()
            int r9 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r9 == 0) goto L193
            goto L1ea
        L193:
            com.android.launcher3.Workspace r9 = r8.mWorkspace
            boolean r9 = r9.isInOverviewMode()
            if (r9 == 0) goto L19f
            r8.editFolderBackgroundAnim(r1, r2)
            goto L1ed
        L19f:
            com.android.launcher3.Workspace r9 = r8.mWorkspace
            int r9 = r9.getCurrentPage()
            int r9 = r9 - r3
        L1a6:
            com.android.launcher3.Workspace r10 = r8.mWorkspace
            int r10 = r10.getCurrentPage()
            int r10 = r10 + 2
            if (r9 >= r10) goto L1e6
            if (r9 < 0) goto L1e3
            com.android.launcher3.Workspace r10 = r8.mWorkspace
            int r10 = r10.getPageCount()
            if (r9 >= r10) goto L1e3
            com.android.launcher3.Workspace r10 = r8.mWorkspace
            com.android.launcher3.CellLayout r10 = r10.getDropLayout(r9)
            float r10 = r10.getAlpha()
            int r10 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r10 >= 0) goto L1e3
            com.android.launcher3.Workspace r10 = r8.mWorkspace
            com.android.launcher3.CellLayout r10 = r10.getDropLayout(r9)
            r10.setAlpha(r5)
            com.android.launcher3.Workspace r10 = r8.mWorkspace
            com.android.launcher3.CellLayout r10 = r10.getDropLayout(r9)
            r10.setScaleX(r5)
            com.android.launcher3.Workspace r10 = r8.mWorkspace
            com.android.launcher3.CellLayout r10 = r10.getDropLayout(r9)
            r10.setScaleY(r5)
        L1e3:
            int r9 = r9 + 1
            goto L1a6
        L1e6:
            r8.folderBackgroundAnim(r1)
            goto L1ed
        L1ea:
            r8.allappFolderBackgroundAnim(r1)
        L1ed:
            com.lge.launcher3.hotword.HotwordServiceWrapper r9 = r8.mHotword
            r9.closeFolder()
            boolean r9 = com.lge.launcher3.util.Utilities.isLGUI7_0()
            if (r9 == 0) goto L216
            com.android.launcher3.LauncherState r9 = com.android.launcher3.LauncherState.NORMAL
            boolean r9 = r8.isInState(r9)
            if (r9 == 0) goto L231
            com.android.launcher3.Workspace r9 = r8.mWorkspace
            com.android.launcher3.Workspace$State r9 = r9.getState()
            com.android.launcher3.Workspace$State r10 = com.android.launcher3.Workspace.State.NORMAL
            if (r9 != r10) goto L231
            android.view.Window r9 = r8.getWindow()
            android.view.View r9 = r9.getDecorView()
            com.lge.launcher3.adaptive.AdaptiveTextUtil.setAdaptiveSystemUi(r9, r8, r3)
            goto L231
        L216:
            com.android.launcher3.LauncherState r9 = com.android.launcher3.LauncherState.ALL_APPS
            boolean r9 = r8.isInState(r9)
            if (r9 == 0) goto L231
            com.lge.launcher3.allapps.AllAppsHost r9 = r8.mAppsCustomizeHost
            boolean r9 = r9.isInArrangeMode()
            if (r9 != 0) goto L231
            android.view.Window r9 = r8.getWindow()
            android.view.View r9 = r9.getDecorView()
            com.lge.launcher3.adaptive.AdaptiveTextUtil.setAdaptiveSystemUi(r9, r8, r1)
        L231:
            com.lge.launcher3.wallpapermotion.WallpaperMotionManager r9 = r8.mWallpaperMotionManager
            if (r9 == 0) goto L254
            com.android.launcher3.Workspace r9 = r8.mWorkspace
            com.android.launcher3.Workspace$State r9 = r9.getState()
            com.android.launcher3.Workspace$State r10 = com.android.launcher3.Workspace.State.SPRING_LOADED
            if (r9 == r10) goto L254
            com.android.launcher3.Workspace r9 = r8.mWorkspace
            boolean r9 = r9.isInOverviewMode()
            if (r9 != 0) goto L254
            com.android.launcher3.LauncherState r9 = com.android.launcher3.LauncherState.ALL_APPS
            boolean r9 = r8.isInState(r9)
            if (r9 != 0) goto L254
            com.lge.launcher3.wallpapermotion.WallpaperMotionManager r9 = r8.mWallpaperMotionManager
            r9.setEnableParallax(r3)
        L254:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Launcher.closeFolder(com.android.launcher3.folder.Folder, boolean[]):void");
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        CellLayout.CellInfo cellInfo;
        LGLog.d("Launcher", "Launcher : onLongClick");
        ((OverViewPanel) this.mLGOverviewPanel).updateWatcher();
        boolean z = false;
        if (!this.dispatchTouchEventDown) {
            LGLog.d("Launcher", "dispatchTouchEvent ACTION_DOWN didn't call");
            return false;
        }
        if (!MemoryUtils.hasAvailableFileSystemMemory(this, true)) {
            LGLog.i("Launcher", "Memory is full. so onLongClick() is canceled.");
            return false;
        }
        if (!isDraggingEnabled()) {
            return false;
        }
        if (isWorkspaceLocked()) {
            LGLog.d("Launcher", "block onLongClick - mWorkspaceLoading = " + this.mWorkspaceLoading + ", mWaitingForResult = " + this.mWaitingForResult);
            return false;
        }
        if ((!UninstallModeManager.getInstance(this).isInUninstallMode() && !isInState(LauncherState.NORMAL)) || isInState(LauncherState.CLEAN_VIEW)) {
            return false;
        }
        if (this.mDeviceProfile.isMultiWindowMode) {
            Toast.makeText(this, getResources().getString(R.string.home_screen_lock_in_multiwindow), 0).show();
            return true;
        }
        if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(getApplicationContext())) {
            Toast.makeText(this, HomeSettingsSharedPreferences.getHomeLockDisableGuideText(getApplicationContext()), 0).show();
            return true;
        }
        if (this.isLongClickFromKeyEnter) {
            this.isLongClickFromKeyEnter = false;
            return true;
        }
        if ((v instanceof Workspace) || (v instanceof SwivelContentsView)) {
            if (this.mWorkspace.isInOverviewMode() || this.mWorkspace.isTouchActive()) {
                return false;
            }
            if (LGHomeFeature.Config.FEATURE_EDITMODE_LONGPRESS_DELAY.getValue()) {
                this.mIsWorkspaceLongPressed = true;
                this.mHandler.sendEmptyMessageDelayed(1000, getResources().getInteger(R.integer.config_editmode_longpress_delay));
            } else {
                if (!UninstallModeManager.getInstance(this).isInUninstallMode()) {
                    VibratorManager.performHapticFeedback(this, 0);
                }
                showOverviewMode(true);
            }
            return true;
        }
        View view = null;
        if (v.getTag() instanceof ItemInfo) {
            CellLayout.CellInfo cellInfo2 = new CellLayout.CellInfo(v, (ItemInfo) v.getTag());
            View view2 = cellInfo2.cell;
            resetAddInfo();
            view = view2;
            cellInfo = cellInfo2;
        } else {
            cellInfo = null;
        }
        boolean zIsHotseatLayout = isHotseatLayout(v);
        if (!this.mDragController.isDragging()) {
            if (view == null) {
                if (this.mWorkspace.isInOverviewMode()) {
                    if (VibratorManager.isVibratorFeedbackEnabled(v.getContext())) {
                        VibratorManager.performHapticFeedback(this, 0);
                    }
                    this.mWorkspace.startReordering(v);
                } else if (LGHomeFeature.Config.FEATURE_EDITMODE_LONGPRESS_DELAY.getValue()) {
                    this.mIsWorkspaceLongPressed = true;
                    this.mHandler.sendEmptyMessageDelayed(1000, getResources().getInteger(R.integer.config_editmode_longpress_delay));
                } else {
                    if (!UninstallModeManager.getInstance(this).isInUninstallMode()) {
                        VibratorManager.performHapticFeedback(this, 0);
                    }
                    this.mWorkspace.setActivePointerIdToInvalid();
                    showOverviewMode(true);
                }
            } else {
                if (zIsHotseatLayout && isAllAppsButtonRank(this.mHotseat.getOrderInHotseat(cellInfo.cellX, cellInfo.cellY))) {
                    z = true;
                }
                if (!(view instanceof Folder) && !z) {
                    this.mHotword.requestHotwordDetectionIfNeeded();
                    this.mWorkspace.startDragDeepShortcut(cellInfo, new DragOptions());
                }
            }
        }
        return true;
    }

    boolean isHotseatLayout(View layout) {
        Hotseat hotseat = this.mHotseat;
        return hotseat != null && layout != null && (layout instanceof CellLayout) && layout == hotseat.getLayout();
    }

    public CellLayout getCellLayout(long container, long screenId) {
        if (container == -101) {
            Hotseat hotseat = this.mHotseat;
            if (hotseat != null) {
                return hotseat.getLayout();
            }
            return null;
        }
        Workspace workspace = this.mWorkspace;
        if (workspace != null) {
            return workspace.getScreenWithId(screenId);
        }
        return null;
    }

    public boolean isAllAppsVisible() {
        return isAppsViewVisible();
    }

    public boolean isAppsViewVisible() {
        return isInState(LauncherState.ALL_APPS) || this.mOnResumeState == LauncherState.ALL_APPS;
    }

    public boolean isWidgetsViewVisible() {
        return isInState(LauncherState.WIDGETS) || this.mOnResumeState == LauncherState.WIDGETS;
    }

    private void setWorkspaceBackground(int background) {
        if (background == 1) {
            getWindow().setBackgroundDrawable(new ColorDrawable(0));
        } else if (background == 2) {
            getWindow().setBackgroundDrawable(null);
        } else {
            getWindow().setBackgroundDrawable(this.mWorkspaceBackgroundDrawable);
        }
    }

    protected void changeWallpaperVisiblity(boolean visible) {
        int i = visible ? 1048576 : 0;
        if (i != (getWindow().getAttributes().flags & 1048576)) {
            getWindow().setFlags(i, 1048576);
        }
        setWorkspaceBackground(visible ? 0 : 2);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= 20) {
            SQLiteDatabase.releaseMemory();
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onTrimMemory(level);
        }
    }

    @Override // com.android.launcher3.LauncherStateTransitionAnimation.Callbacks
    public void onStateTransitionHideSearchBar() {
        SearchDropTargetBar searchDropTargetBar = this.mSearchDropTargetBar;
        if (searchDropTargetBar != null) {
            searchDropTargetBar.hideSearchBar(false);
        }
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    public boolean showWorkspace(boolean animated) {
        return showWorkspace(-1, animated, null);
    }

    public boolean showWorkspace(boolean animated, Runnable onCompleteRunnable) {
        return showWorkspace(-1, animated, onCompleteRunnable);
    }

    protected boolean showWorkspace(int snapToPage, boolean animated) {
        return showWorkspace(snapToPage, animated, null);
    }

    protected boolean showWorkspace(int snapToPage, boolean animated, Runnable onCompleteRunnable) {
        if (this.mWorkspace == null) {
            return false;
        }
        boolean z = (isInState(LauncherState.NORMAL) && this.mWorkspace.getState() == Workspace.State.NORMAL) ? false : true;
        if (isInState(LauncherState.ALL_APPS)) {
            this.mWorkspace.setCheckSwipeUpAppDrawer(false);
            this.mWorkspace.backToWorkspaceFromSwipeUpAppDrawer(true);
        }
        if (z) {
            if (this.mWorkspace.hasCustomContent()) {
                enablePageAsFullSize(true, this.mDeviceProfile.isVerticalBarLayout());
            }
            boolean z2 = !isInState(LauncherState.NORMAL);
            if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                this.mWorkspace.setVisibility(0);
            }
            this.mStateTransitionAnimation.startAnimationToWorkspace((LauncherState) this.mStateManager.getState(), Workspace.State.NORMAL, snapToPage, animated, onCompleteRunnable);
            SearchDropTargetBar searchDropTargetBar = this.mSearchDropTargetBar;
            if (searchDropTargetBar != null) {
                searchDropTargetBar.showSearchBar(animated && z2);
            }
            View view = this.mAllAppsButton;
            if (view != null) {
                view.requestFocus();
            }
            this.mWorkspace.clearAccessibilityFocus();
            getWorkspace().showAllCrossHair(false);
        }
        this.mStateManager.setStateOnly(LauncherState.NORMAL);
        this.mHotword.requestHotwordDetectionIfNeeded();
        this.mUserPresent = true;
        updateAutoAdvanceState();
        if (!com.lge.launcher3.util.Utilities.isLGUI7_0() || this.mWorkspace.getOpenFolder() == null) {
            AdaptiveTextUtil.setAdaptiveSystemUi(getWindow().getDecorView(), this, true);
        }
        this.mWorkspace.resetMinusOneScreenPreview();
        return z;
    }

    protected void showOverviewMode(boolean animated) {
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            this.mWorkspace.setVisibility(0);
            this.mWorkspace.addMinusOneScreenPreview();
        }
        if (this.mWorkspace.hasCustomContent()) {
            enablePageAsFullSize(false, this.mDeviceProfile.isVerticalBarLayout());
        }
        this.mStateTransitionAnimation.startAnimationToWorkspace((LauncherState) this.mStateManager.getState(), Workspace.State.OVERVIEW, -1, animated, null);
        this.mStateManager.setStateOnly(LauncherState.NORMAL);
        this.mHotword.requestHotwordDetectionIfNeeded();
        UninstallModeManager.getInstance(this).enterUninstallMode(this);
        PageIndicatorExtension pageIndicatorExtension = (PageIndicatorExtension) this.mWorkspace.getPageIndicator();
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && pageIndicatorExtension != null) {
            pageIndicatorExtension.updateMarkerToMatchScreen();
        }
        AdaptiveTextUtil.setAdaptiveSystemUi(getWindow().getDecorView(), this, false);
        if (this.mLGOverviewPanel.getScaleX() < 1.0f || this.mLGOverviewPanel.getScaleY() < 1.0f) {
            this.mLGOverviewPanel.setAlpha(1.0f);
            this.mLGOverviewPanel.setScaleX(1.0f);
            this.mLGOverviewPanel.setScaleY(1.0f);
            this.mLGOverviewPanel.setTranslationY(1.0f);
        }
        this.mLGOverviewPanel.setPadding(getResources().getDimensionPixelSize(R.dimen.swivel_edit_mode_horizontal_padding), 0, getResources().getDimensionPixelSize(R.dimen.swivel_edit_mode_horizontal_padding), 0);
    }

    public void showAppsView(boolean animated, boolean resetListToTop, boolean updatePredictedApps, boolean focusSearchBar) {
        if (resetListToTop) {
            this.mAppsView.scrollToTop();
        }
        if (updatePredictedApps) {
            tryAndUpdatePredictedApps();
        }
        showAppsOrWidgets(LauncherState.ALL_APPS, animated, focusSearchBar);
    }

    protected void showWidgetsView(boolean animated, boolean resetPageToZero) {
        if (resetPageToZero) {
            this.mWidgetsView.scrollToTop();
        }
        showAppsOrWidgets(LauncherState.WIDGETS, animated, false);
        this.mWidgetsView.post(new Runnable() { // from class: com.android.launcher3.Launcher.26
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.mWidgetsView.requestFocus();
            }
        });
    }

    private boolean showAppsOrWidgets(LauncherState toState, boolean animated, boolean focusSearchBar) {
        if (!isInState(LauncherState.NORMAL) && !isInState(LauncherState.APPS_SPRING_LOADED) && !isInState(LauncherState.WIDGETS_SPRING_LOADED)) {
            return false;
        }
        if (toState != LauncherState.ALL_APPS && toState != LauncherState.WIDGETS) {
            return false;
        }
        if (toState == LauncherState.ALL_APPS) {
            this.mStateTransitionAnimation.startAnimationToAllApps(animated, focusSearchBar);
        } else {
            this.mStateTransitionAnimation.startAnimationToWidgets(animated);
        }
        this.mStateManager.setStateOnly(toState);
        this.mHotword.requestHotwordDetectionIfNeeded();
        this.mUserPresent = false;
        updateAutoAdvanceState();
        closeFolder(new boolean[0]);
        getWindow().getDecorView().sendAccessibilityEvent(32);
        ((LGWidgetContainerView) this.mWidgetsView).bringToFront();
        return true;
    }

    public Animator startWorkspaceStateChangeAnimation(Workspace.State toState, int toPage, boolean animated, boolean hasOverlaySearchBar, HashMap<View, Integer> layerViews) {
        Workspace.State state = this.mWorkspace.getState();
        Animator stateWithAnimation = this.mWorkspace.setStateWithAnimation(toState, toPage, animated, hasOverlaySearchBar, layerViews);
        updateInteraction(state, toState);
        return stateWithAnimation;
    }

    public void enterSpringLoadedDragMode() {
        Runnable runnable;
        boolean z;
        if (isInState(LauncherState.SPRING_LOADED) || isInState(LauncherState.APPS_SPRING_LOADED) || isInState(LauncherState.WIDGETS_SPRING_LOADED) || isInState(LauncherState.CLEAN_VIEW)) {
            return;
        }
        WallpaperMotionManager wallpaperMotionManager = this.mWallpaperMotionManager;
        if (wallpaperMotionManager != null) {
            wallpaperMotionManager.setEnableParallax(false);
        }
        enablePageAsFullSize(false, this.mDeviceProfile.isVerticalBarLayout());
        Workspace.State state = this.mWorkspace.isInOverviewMode() ? Workspace.State.OVERVIEW : Workspace.State.SPRING_LOADED;
        if (!HomescreenBlurManager.getInstance(this).isLiveWallpaperMode() || getWorkspace().getOpenFolder() == null) {
            runnable = null;
            z = true;
        } else {
            runnable = new Runnable() { // from class: com.android.launcher3.Launcher.27
                @Override // java.lang.Runnable
                public void run() {
                    Launcher.this.setHomeVisibility(4);
                }
            };
            z = false;
        }
        this.mStateTransitionAnimation.startAnimationToWorkspace((LauncherState) this.mStateManager.getState(), state, -1, z, runnable);
        this.mStateManager.setStateOnly(isAppsViewVisible() ? LauncherState.APPS_SPRING_LOADED : LauncherState.WIDGETS_SPRING_LOADED);
        this.mHotword.requestHotwordDetectionIfNeeded();
        drawGridWorkspace();
        if (!this.mWorkspace.isInOverviewMode()) {
            getWorkspace().showAllCrossHair(true);
        }
        AdaptiveTextUtil.setAdaptiveSystemUi(getWindow().getDecorView(), this, false);
    }

    private void drawGridWorkspace() {
        for (int i = 0; i < this.mWorkspace.getChildCount(); i++) {
            if (this.mWorkspace.getChildAt(i) instanceof CellLayout) {
                ((CellLayout) this.mWorkspace.getChildAt(i)).setDrawGrid(true);
            }
        }
    }

    public void exitSpringLoadedDragModeDelayed(final boolean successfulDrop, int delay, final Runnable onCompleteRunnable) {
        Workspace workspace;
        if ((isInState(LauncherState.APPS_SPRING_LOADED) || isInState(LauncherState.WIDGETS_SPRING_LOADED)) && (workspace = this.mWorkspace) != null) {
            if (workspace.isInOverviewMode()) {
                if (isInState(LauncherState.NORMAL)) {
                    return;
                }
                getStateManager().setStateOnly(LauncherState.NORMAL);
                return;
            }
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.launcher3.Launcher.28
                @Override // java.lang.Runnable
                public void run() {
                    if (successfulDrop) {
                        if (Launcher.this.mWidgetsView != null) {
                            Launcher.this.mWidgetsView.setVisibility(8);
                        }
                        if (Launcher.this.mWorkspace.showWorkspaceForNotFoundCellDrop(onCompleteRunnable)) {
                            return;
                        }
                        int nextPage = Launcher.this.mWorkspace.getNextPage();
                        if (HomescreenBlurManager.getInstance(Launcher.this.getApplicationContext()).isLiveWallpaperMode() && Launcher.this.getWorkspace().getOpenFolder() != null && onCompleteRunnable == null) {
                            Launcher.this.showWorkspace(nextPage, false, new Runnable() { // from class: com.android.launcher3.Launcher.28.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    Launcher.this.setHomeVisibility(4);
                                }
                            });
                            return;
                        } else {
                            Launcher.this.showWorkspace(nextPage, true, onCompleteRunnable);
                            return;
                        }
                    }
                    Launcher.this.exitSpringLoadedDragMode();
                }
            }, delay);
        }
    }

    void exitSpringLoadedDragMode() {
        if (isInState(LauncherState.APPS_SPRING_LOADED)) {
            showAllAppsView(true, false, false, false);
        } else if (isInState(LauncherState.WIDGETS_SPRING_LOADED)) {
            showWorkspace(true);
        }
        getWorkspace().showAllCrossHair(false);
    }

    public void tryAndUpdatePredictedApps() {
        List<ComponentKey> predictedApps;
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks == null || (predictedApps = launcherCallbacks.getPredictedApps()) == null) {
            return;
        }
        this.mAppsView.setPredictedApps(predictedApps);
    }

    public View getOrCreateQsbBar() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null && launcherCallbacks.providesSearch()) {
            return this.mLauncherCallbacks.getQsbBar();
        }
        if (this.mQsb == null) {
            AppWidgetProviderInfo searchWidgetProvider = Utilities.getSearchWidgetProvider(this);
            if (searchWidgetProvider == null) {
                return null;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("appWidgetCategory", 4);
            SharedPreferences sharedPreferences = getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0);
            int i = sharedPreferences.getInt(QSB_WIDGET_ID, -1);
            AppWidgetProviderInfo appWidgetInfo = this.mAppWidgetManager.getAppWidgetInfo(i);
            if (!searchWidgetProvider.provider.flattenToString().equals(sharedPreferences.getString(QSB_WIDGET_PROVIDER, null)) || appWidgetInfo == null || !appWidgetInfo.provider.equals(searchWidgetProvider.provider)) {
                if (i > -1) {
                    this.mAppWidgetHost.deleteAppWidgetId(i);
                }
                int iAllocateAppWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                if (AppWidgetManagerCompat.getInstance(this).bindAppWidgetIdIfAllowed(iAllocateAppWidgetId, searchWidgetProvider, bundle)) {
                    i = iAllocateAppWidgetId;
                } else {
                    this.mAppWidgetHost.deleteAppWidgetId(iAllocateAppWidgetId);
                    i = -1;
                }
                sharedPreferences.edit().putInt(QSB_WIDGET_ID, i).putString(QSB_WIDGET_PROVIDER, searchWidgetProvider.provider.flattenToString()).commit();
            }
            this.mAppWidgetHost.setQsbWidgetId(i);
            if (i != -1) {
                AppWidgetHostView appWidgetHostViewCreateView = this.mAppWidgetHost.createView(this, i, searchWidgetProvider);
                this.mQsb = appWidgetHostViewCreateView;
                appWidgetHostViewCreateView.updateAppWidgetOptions(bundle);
                this.mQsb.setPadding(0, 0, 0, 0);
                this.mSearchDropTargetBar.addView(this.mQsb);
                this.mSearchDropTargetBar.setQsbSearchBar(this.mQsb);
            }
        }
        return this.mQsb;
    }

    private void reinflateQSBIfNecessary() {
        AppWidgetHostView appWidgetHostView = this.mQsb;
        if ((appWidgetHostView instanceof LauncherAppWidgetHostView) && ((LauncherAppWidgetHostView) appWidgetHostView).isReinflateRequired()) {
            this.mSearchDropTargetBar.removeView(this.mQsb);
            this.mQsb = null;
            this.mSearchDropTargetBar.setQsbSearchBar(getOrCreateQsbBar());
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean zDispatchPopulateAccessibilityEvent = super.dispatchPopulateAccessibilityEvent(event);
        List<CharSequence> text = event.getText();
        text.clear();
        if (!isInState(LauncherState.ALL_APPS) && !isInState(LauncherState.WIDGETS)) {
            Workspace workspace = this.mWorkspace;
            if (workspace != null) {
                Folder openFolder = workspace.getOpenFolder();
                if (openFolder != null && openFolder.mContent != null) {
                    text.add(openFolder.mContent.getAccessibilityDescription());
                    text.add(openFolder.mContent.getCurrentPageDescription());
                } else {
                    text.add(this.mWorkspace.getCurrentPageDescription());
                }
            } else {
                text.add(getString(R.string.all_apps_home_button_label));
            }
        }
        return zDispatchPopulateAccessibilityEvent;
    }

    class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
        CloseSystemDialogsIntentReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Launcher.this.closeSystemDialogs();
        }
    }

    boolean waitUntilResume(Runnable run, boolean deletePreviousRunnables) {
        if (!this.mPaused && !isInState(LauncherState.DYNAMIC_GRID_OVERVIEW)) {
            return false;
        }
        if (deletePreviousRunnables) {
            while (this.mBindOnResumeCallbacks.remove(run)) {
            }
        }
        this.mBindOnResumeCallbacks.add(run);
        return true;
    }

    protected boolean waitUntilResume(Runnable run) {
        return waitUntilResume(run, false);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public boolean setLoadOnResume() {
        if (!this.mPaused) {
            return false;
        }
        this.mOnResumeNeedsLoad = true;
        return true;
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public int getCurrentWorkspaceScreen() {
        Workspace workspace = this.mWorkspace;
        if (workspace != null) {
            return workspace.getCurrentPage();
        }
        return 2;
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void clearPendingBinds() {
        ViewOnDrawExecutor viewOnDrawExecutor = this.mPendingExecutor;
        if (viewOnDrawExecutor != null) {
            viewOnDrawExecutor.markCompleted();
            this.mPendingExecutor = null;
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void startBinding(int loadFlags) {
        AbstractFloatingView.closeOpenViews(this, true, 3983);
        setWorkspaceLoading(true);
        this.mBindOnResumeCallbacks.clear();
        this.mWorkspace.clearDropTargets();
        this.mWorkspace.removeAllWorkspaceScreens();
        this.mWidgetsToAdvance.clear();
        Workspace workspace = this.mWorkspace;
        if (workspace != null && workspace.getState() == Workspace.State.OVERVIEW) {
            this.mWorkspace.addMinusOneScreenPreview();
        }
        boolean z = this.mOrientationOfCurrentLayout == 0;
        if (getOrientation() == 1 && !z) {
            loadFlags |= 32;
        }
        LGLog.i("Launcher", "startBinding - getOrientation() = " + getOrientation() + ", isPortraitLayout = " + z + ", isPortrait = " + OrientationUtils.isPortrait(this) + ", loadFlags = " + loadFlags);
        if ((loadFlags & 32) != 0) {
            if (getDragController() != null) {
                getDragController().cancelDrag();
            }
            AllAppsHost allAppsHost = this.mAppsCustomizeHost;
            if (allAppsHost != null) {
                allAppsHost.reset(null);
            }
            this.mDeviceProfile.layout(this);
            this.mWorkspace.initForRebind(this.mDeviceProfile);
            Hotseat hotseat = this.mHotseat;
            if (hotseat != null) {
                hotseat.resetLayout();
                this.mHotseat.resetHotSeat(this.mDeviceProfile);
                return;
            }
            return;
        }
        Hotseat hotseat2 = this.mHotseat;
        if (hotseat2 != null) {
            hotseat2.resetLayout();
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindScreens(ArrayList<Long> orderedScreenIds) {
        bindAddScreens(orderedScreenIds);
        if (orderedScreenIds.size() == 0) {
            this.mWorkspace.addExtraEmptyScreen();
        }
        this.mWorkspace.unlockWallpaperFromDefaultPageOnNextLayout();
        if (hasCustomContentToLeft()) {
            this.mWorkspace.createCustomContentContainer();
            populateCustomContentContainer();
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindAddScreens(ArrayList<Long> orderedScreenIds) {
        int size = orderedScreenIds.size();
        for (int i = 0; i < size; i++) {
            this.mWorkspace.insertNewWorkspaceScreenBeforeEmptyScreen(orderedScreenIds.get(i).longValue());
        }
        if (isInState(LauncherState.WIDGETS) || isInState(LauncherState.ALL_APPS)) {
            int childCount = this.mWorkspace.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                CellLayout cellLayout = (CellLayout) this.mWorkspace.getChildAt(i2);
                if (cellLayout != null) {
                    cellLayout.setShortcutAndWidgetAlpha(0.0f);
                }
            }
        }
    }

    private boolean shouldShowWeightWatcher() {
        return getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).getBoolean(SHOW_WEIGHT_WATCHER, false);
    }

    private void toggleShowWeightWatcher() {
        SharedPreferences sharedPreferences = getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0);
        boolean z = true ^ sharedPreferences.getBoolean(SHOW_WEIGHT_WATCHER, true);
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putBoolean(SHOW_WEIGHT_WATCHER, z);
        editorEdit.commit();
        View view = this.mWeightWatcher;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindAppsAdded(final ArrayList<Long> newScreens, final ArrayList<ItemInfo> addNotAnimated, final ArrayList<ItemInfo> addAnimated, final ArrayList<AppInfo> addedApps, final int op) {
        if (waitUntilResume(new Runnable() { // from class: com.android.launcher3.Launcher.29
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.bindAppsAdded(newScreens, addNotAnimated, addAnimated, addedApps, op);
            }
        })) {
            return;
        }
        if (newScreens != null) {
            bindAddScreens(newScreens);
        }
        if (addNotAnimated != null && !addNotAnimated.isEmpty()) {
            bindItems(addNotAnimated, 0, addNotAnimated.size(), false);
        }
        if (addAnimated != null && !addAnimated.isEmpty()) {
            bindItems(addAnimated, 0, addAnimated.size(), this.mStateManager.getState() != LauncherState.ALL_APPS);
        }
        this.mWorkspace.removeExtraEmptyScreen(false, false);
        AllAppsHost allAppsHost = this.mAppsCustomizeHost;
        if (allAppsHost != null) {
            allAppsHost.addApps(addedApps, op);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x00c9  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00ea  */
    @Override // com.android.launcher3.LauncherModel.Callbacks
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void bindItems(final java.util.ArrayList<com.android.launcher3.model.data.ItemInfo> r23, final int r24, final int r25, final boolean r26) {
        /*
            r22 = this;
            r1 = r22
            android.animation.AnimatorSet r2 = com.android.launcher3.LauncherAnimUtils.createAnimatorSet()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r4 = 0
            r5 = 1
            if (r26 == 0) goto L17
            boolean r0 = r22.canRunNewAppsAnimation()
            if (r0 == 0) goto L17
            r6 = r5
            goto L18
        L17:
            r6 = r4
        L18:
            com.android.launcher3.Workspace r15 = r1.mWorkspace
            r17 = -1
            r14 = r24
            r13 = r25
            r11 = r17
        L22:
            if (r14 >= r13) goto L18d
            r9 = r23
            java.lang.Object r0 = r9.get(r14)
            com.android.launcher3.model.data.ItemInfo r0 = (com.android.launcher3.model.data.ItemInfo) r0
            android.os.UserHandle r7 = r0.user
            boolean r7 = com.lge.launcher3.util.UserUtils.existUser(r1, r7)
            java.lang.String r8 = "Launcher"
            if (r7 != 0) goto L4d
            java.lang.Object[] r7 = new java.lang.Object[r5]
            r7[r4] = r0
            java.lang.String r0 = "bindItems() : Skip to bind an item (%s) because it's userprofile doesn't exist."
            java.lang.String r0 = java.lang.String.format(r0, r7)
            com.lge.launcher3.util.LGLog.i(r8, r0)
        L43:
            r21 = r2
            r26 = r6
            r5 = r11
            r4 = r14
            r19 = r15
            goto L17e
        L4d:
            long r4 = r0.container
            r19 = -101(0xffffffffffffff9b, double:NaN)
            int r4 = (r4 > r19 ? 1 : (r4 == r19 ? 0 : -1))
            if (r4 != 0) goto L5a
            com.android.launcher3.Hotseat r4 = r1.mHotseat
            if (r4 != 0) goto L5a
            goto L43
        L5a:
            int r4 = r0.itemType
            if (r4 == 0) goto L67
            r5 = 1
            if (r4 == r5) goto L67
            r7 = 2
            if (r4 == r7) goto L76
            r7 = 6
            if (r4 != r7) goto L6e
        L67:
            r26 = r6
            r5 = r11
            r4 = r14
            r19 = r15
            goto Lc2
        L6e:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r2 = "Invalid Item Type"
            r0.<init>(r2)
            throw r0
        L76:
            r1.removeFolderItemWhileLoading(r0)
            r4 = 2131492963(0x7f0c0063, float:1.8609393E38)
            int r7 = r15.getCurrentPage()
            android.view.View r7 = r15.getChildAt(r7)
            android.view.ViewGroup r7 = (android.view.ViewGroup) r7
            r8 = r0
            com.android.launcher3.model.data.FolderInfo r8 = (com.android.launcher3.model.data.FolderInfo) r8
            com.android.launcher3.icons.IconCache r10 = r1.mIconCache
            com.android.launcher3.folder.FolderIcon r8 = com.android.launcher3.folder.FolderIcon.fromXml(r4, r1, r7, r8, r10)
            android.content.Context r4 = r22.getApplicationContext()
            com.lge.launcher3.uninstallmode.UninstallModeManager r4 = com.lge.launcher3.uninstallmode.UninstallModeManager.getInstance(r4)
            com.android.launcher3.folder.Folder r7 = r8.mFolder
            com.android.launcher3.folder.FolderPagedView r7 = r7.mContent
            r4.setUninstallTypeForItemsInFolder(r7)
            r26 = r6
            long r5 = r0.container
            r19 = r11
            long r11 = r0.screenId
            int r4 = r0.cellX
            int r0 = r0.cellY
            r16 = 1
            r21 = 1
            r7 = r15
            r9 = r5
            r5 = r19
            r13 = r4
            r4 = r14
            r14 = r0
            r19 = r15
            r15 = r16
            r16 = r21
            r7.addInScreenFromBind(r8, r9, r11, r13, r14, r15, r16)
        Lbe:
            r21 = r2
            goto L17e
        Lc2:
            r7 = r0
            com.android.launcher3.ShortcutInfo r7 = (com.android.launcher3.ShortcutInfo) r7
            boolean r9 = r7.isRemoved
            if (r9 == 0) goto Lea
            int r0 = r7.hashCode()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Skip bindItems because it is removed item in BgDataModel - "
            r9.append(r10)
            r9.append(r7)
            java.lang.String r7 = ", "
            r9.append(r7)
            r9.append(r0)
            java.lang.String r0 = r9.toString()
            com.lge.launcher3.util.LGLog.i(r8, r0)
            goto Lbe
        Lea:
            android.view.View r15 = r1.createShortcut(r7)
            boolean r7 = r15 instanceof com.android.launcher3.BubbleTextView
            if (r7 == 0) goto Lf8
            r7 = r15
            com.android.launcher3.BubbleTextView r7 = (com.android.launcher3.BubbleTextView) r7
            r7.verifyHighRes()
        Lf8:
            long r9 = r0.container
            r11 = -100
            int r7 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r7 != 0) goto L14f
            com.android.launcher3.Workspace r7 = r1.mWorkspace
            long r9 = r0.screenId
            com.android.launcher3.CellLayout r7 = r7.getScreenWithId(r9)
            if (r7 == 0) goto L14f
            int r9 = r0.cellX     // Catch: java.lang.RuntimeException -> L149
            int r10 = r0.cellY     // Catch: java.lang.RuntimeException -> L149
            boolean r9 = r7.isOccupied(r9, r10)     // Catch: java.lang.RuntimeException -> L149
            if (r9 == 0) goto L14f
            int r9 = r0.cellX     // Catch: java.lang.RuntimeException -> L149
            int r10 = r0.cellY     // Catch: java.lang.RuntimeException -> L149
            android.view.View r7 = r7.getChildAt(r9, r10)     // Catch: java.lang.RuntimeException -> L149
            java.lang.Object r7 = r7.getTag()     // Catch: java.lang.RuntimeException -> L149
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.RuntimeException -> L149
            r9.<init>()     // Catch: java.lang.RuntimeException -> L149
            java.lang.String r10 = "Collision while binding workspace item: "
            r9.append(r10)     // Catch: java.lang.RuntimeException -> L149
            r9.append(r0)     // Catch: java.lang.RuntimeException -> L149
            java.lang.String r10 = ". Collides with "
            r9.append(r10)     // Catch: java.lang.RuntimeException -> L149
            r9.append(r7)     // Catch: java.lang.RuntimeException -> L149
            java.lang.String r7 = r9.toString()     // Catch: java.lang.RuntimeException -> L149
            boolean r9 = com.android.launcher3.LauncherAppState.isDogfoodBuild()     // Catch: java.lang.RuntimeException -> L149
            if (r9 != 0) goto L143
            android.util.Log.d(r8, r7)     // Catch: java.lang.RuntimeException -> L149
            goto L14f
        L143:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch: java.lang.RuntimeException -> L149
            r0.<init>(r7)     // Catch: java.lang.RuntimeException -> L149
            throw r0     // Catch: java.lang.RuntimeException -> L149
        L149:
            r0 = move-exception
            r0.printStackTrace()
            goto Lbe
        L14f:
            long r9 = r0.container
            long r11 = r0.screenId
            int r13 = r0.cellX
            int r14 = r0.cellY
            r16 = 1
            r20 = 1
            r7 = r19
            r8 = r15
            r21 = r2
            r2 = r15
            r15 = r16
            r16 = r20
            r7.addInScreenFromBind(r8, r9, r11, r13, r14, r15, r16)
            if (r26 == 0) goto L17e
            r5 = 0
            r2.setAlpha(r5)
            r2.setScaleX(r5)
            r2.setScaleY(r5)
            android.animation.ValueAnimator r2 = r1.createNewAppBounceAnimation(r2, r4)
            r3.add(r2)
            long r11 = r0.screenId
            goto L17f
        L17e:
            r11 = r5
        L17f:
            int r14 = r4 + 1
            r13 = r25
            r6 = r26
            r15 = r19
            r2 = r21
            r4 = 0
            r5 = 1
            goto L22
        L18d:
            r21 = r2
            r26 = r6
            r5 = r11
            r19 = r15
            if (r26 == 0) goto L1cb
            int r0 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
            if (r0 <= 0) goto L1cb
            com.android.launcher3.Workspace r0 = r1.mWorkspace
            int r2 = r0.getNextPage()
            long r7 = r0.getScreenIdForPageIndex(r2)
            com.android.launcher3.Workspace r0 = r1.mWorkspace
            int r0 = r0.getPageIndexForScreenId(r5)
            com.android.launcher3.Launcher$30 r2 = new com.android.launcher3.Launcher$30
            r4 = r21
            r2.<init>()
            int r3 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r3 == 0) goto L1c3
            com.android.launcher3.Workspace r3 = r1.mWorkspace
            com.android.launcher3.Launcher$31 r4 = new com.android.launcher3.Launcher$31
            r4.<init>()
            int r0 = com.android.launcher3.Launcher.NEW_APPS_PAGE_MOVE_DELAY
            long r5 = (long) r0
            r3.postDelayed(r4, r5)
            goto L1cb
        L1c3:
            com.android.launcher3.Workspace r0 = r1.mWorkspace
            int r3 = com.android.launcher3.Launcher.NEW_APPS_ANIMATION_DELAY
            long r3 = (long) r3
            r0.postDelayed(r2, r3)
        L1cb:
            r19.requestLayout()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Launcher.bindItems(java.util.ArrayList, int, int, boolean):void");
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindFolders(final LongArrayMap<FolderInfo> folders) {
        if (waitUntilResume(new Runnable() { // from class: com.android.launcher3.Launcher.32
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.bindFolders(folders);
            }
        })) {
            return;
        }
        sFolders = folders.clone();
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindAppWidget(final LauncherAppWidgetInfo item) {
        LauncherAppWidgetProviderInfo launcherAppWidgetInfo;
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo;
        if (ConciergeBoardMngr.isExtViewMode()) {
            ConciergeBoardMngr.cancelExtViewMode();
        }
        long jUptimeMillis = SystemClock.uptimeMillis();
        Log.d("Launcher", "bindAppWidget: " + item);
        Workspace workspace = this.mWorkspace;
        TraceHelper.INSTANCE.beginSection("BIND_WIDGET_id=" + item.appWidgetId);
        if (!this.mIsSafeModeEnabled && this.mAppWidgetManager.getAppWidgetInfo(item.appWidgetId) == null) {
            item.restoreStatus |= 1;
        }
        if (item.hasRestoreFlag(2)) {
            launcherAppWidgetProviderInfo = null;
        } else {
            if (item.hasRestoreFlag(1)) {
                launcherAppWidgetInfo = this.mAppWidgetManager.findProvider(item.providerName, item.user);
            } else {
                launcherAppWidgetInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(item.appWidgetId);
            }
            launcherAppWidgetProviderInfo = launcherAppWidgetInfo;
        }
        if (launcherAppWidgetProviderInfo != null && (item.minSpanX == -1 || item.minSpanY == -1)) {
            item.minSpanX = launcherAppWidgetProviderInfo.getMinSpanX(this);
            item.minSpanY = launcherAppWidgetProviderInfo.getMinSpanY(this);
        }
        if (!this.mIsSafeModeEnabled && (2 & item.restoreStatus) == 0 && item.restoreStatus != 0) {
            if (launcherAppWidgetProviderInfo == null) {
                Log.d("Launcher", "Removing restored widget: id=" + item.appWidgetId + " belongs to component " + item.providerName + ", as the povider is null");
                LauncherModel.deleteItemFromDatabase(this, item);
                return;
            }
            if (item.hasRestoreFlag(1)) {
                if (!item.hasRestoreFlag(16)) {
                    item.appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                    item.restoreStatus = 16 | item.restoreStatus;
                    PendingAddWidgetInfo pendingAddWidgetInfo = new PendingAddWidgetInfo(this, launcherAppWidgetProviderInfo, null);
                    pendingAddWidgetInfo.spanX = item.spanX;
                    pendingAddWidgetInfo.spanY = item.spanY;
                    pendingAddWidgetInfo.minSpanX = item.minSpanX;
                    pendingAddWidgetInfo.minSpanY = item.minSpanY;
                    Bundle defaultOptionsForWidget = WidgetHostViewLoader.getDefaultOptionsForWidget(this, pendingAddWidgetInfo);
                    boolean zHasRestoreFlag = item.hasRestoreFlag(32);
                    if (zHasRestoreFlag && item.bindOptions != null) {
                        Bundle extras = item.bindOptions.getExtras();
                        if (defaultOptionsForWidget != null) {
                            extras.putAll(defaultOptionsForWidget);
                        }
                        defaultOptionsForWidget = extras;
                    }
                    boolean zBindAppWidgetIdIfAllowed = this.mAppWidgetManager.bindAppWidgetIdIfAllowed(item.appWidgetId, launcherAppWidgetProviderInfo, defaultOptionsForWidget);
                    item.bindOptions = null;
                    item.restoreStatus &= -33;
                    if (zBindAppWidgetIdIfAllowed) {
                        item.restoreStatus = (launcherAppWidgetProviderInfo.configure == null || zHasRestoreFlag) ? 0 : 4;
                    }
                    getModelWriter().updateItemInDatabase(item);
                }
            } else if (item.hasRestoreFlag(4) && launcherAppWidgetProviderInfo.configure == null) {
                item.restoreStatus = 0;
                getModelWriter().updateItemInDatabase(item);
            }
        }
        if (launcherAppWidgetProviderInfo != null && !this.mIsSafeModeEnabled && item.restoreStatus == 0) {
            int i = item.appWidgetId;
            Log.d("Launcher", "bindAppWidget: id=" + item.appWidgetId + " belongs to component " + launcherAppWidgetProviderInfo.provider);
            if (LGHomeFeature.Config.FEATURE_USE_ROUND_SEARCH_WIDGET.getValue() && LauncherConst.GOOGLE_SEARCH_WIDGET_PACKAGENAME.equals(launcherAppWidgetProviderInfo.provider.getPackageName())) {
                try {
                    int i2 = getPackageManager().getReceiverInfo(launcherAppWidgetProviderInfo.provider, 128).metaData.getInt("com.google.android.gsa.searchwidget.alt_initial_layout_cqsb", -1);
                    if (i2 != -1) {
                        launcherAppWidgetProviderInfo.initialLayout = i2;
                    }
                } catch (Exception unused) {
                }
                PendingAddWidgetInfo pendingAddWidgetInfo2 = new PendingAddWidgetInfo(this, launcherAppWidgetProviderInfo, null);
                pendingAddWidgetInfo2.spanX = item.spanX;
                pendingAddWidgetInfo2.spanY = item.spanY;
                pendingAddWidgetInfo2.minSpanX = item.minSpanX;
                pendingAddWidgetInfo2.minSpanY = item.minSpanY;
                int iAllocateAppWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                this.mAppWidgetHost.deleteAppWidgetId(i);
                boolean zBindAppWidgetIdIfAllowed2 = this.mAppWidgetManager.bindAppWidgetIdIfAllowed(iAllocateAppWidgetId, launcherAppWidgetProviderInfo, WidgetHostViewLoader.getDefaultOptionsForWidget(this, pendingAddWidgetInfo2));
                item.appWidgetId = iAllocateAppWidgetId;
                LauncherModel.updateItemInDatabase(this, item);
                LGLog.d("Launcher", "bindAppWidget() " + launcherAppWidgetProviderInfo.provider.getPackageName() + " Allowed result = " + zBindAppWidgetIdIfAllowed2);
                i = iAllocateAppWidgetId;
            }
            item.hostView = this.mAppWidgetHost.createView((Context) this, i, launcherAppWidgetProviderInfo);
        } else {
            PendingAppWidgetHostView pendingAppWidgetHostView = new PendingAppWidgetHostView(this, item, this.mIsSafeModeEnabled);
            pendingAppWidgetHostView.updateIcon(this.mIconCache);
            item.hostView = pendingAppWidgetHostView;
            item.hostView.updateAppWidget(null);
            item.hostView.setOnClickListener(this);
        }
        item.hostView.setTag(item);
        item.onBindAppWidget(this, item.hostView, LGHomeFeature.Config.FEATURE_USE_EXTRA_WIDGET_INFO.getValue() && (launcherAppWidgetProviderInfo instanceof LauncherAppWidgetProviderInfo) && launcherAppWidgetProviderInfo.isLgeWidget);
        AppWidgetProviderInfo appWidgetProviderInfo = launcherAppWidgetProviderInfo;
        workspace.addInScreen(item.hostView, item.container, item.screenId, item.cellX, item.cellY, item.spanX, item.spanY, false);
        if (!item.isCustomWidget()) {
            addWidgetToAutoAdvanceIfNeeded(item.hostView, appWidgetProviderInfo);
        }
        workspace.requestLayout();
        Log.d("Launcher", "bound widget id=" + item.appWidgetId + " in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms");
    }

    private LauncherAppWidgetInfo completeRestoreAppWidget(int appWidgetId, int finalRestoreFlag) {
        LauncherAppWidgetHostView widgetForAppWidgetId = this.mWorkspace.getWidgetForAppWidgetId(appWidgetId);
        if (widgetForAppWidgetId == null || !(widgetForAppWidgetId instanceof PendingAppWidgetHostView)) {
            Log.e("Launcher", "Widget update called, when the widget no longer exists.");
            return null;
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) widgetForAppWidgetId.getTag();
        launcherAppWidgetInfo.restoreStatus = finalRestoreFlag;
        this.mWorkspace.reinflateWidgetsIfNecessary();
        getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
        return launcherAppWidgetInfo;
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void onPageBoundSynchronously(int page) {
        this.mSynchronouslyBoundPages.add(Integer.valueOf(page));
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void executeOnNextDraw(ViewOnDrawExecutor executor) {
        ViewOnDrawExecutor viewOnDrawExecutor = this.mPendingExecutor;
        if (viewOnDrawExecutor != null) {
            viewOnDrawExecutor.markCompleted();
        }
        this.mPendingExecutor = executor;
        isInState(LauncherState.ALL_APPS);
        executor.attachTo(this);
    }

    public void clearPendingExecutor(ViewOnDrawExecutor executor) {
        if (this.mPendingExecutor == executor) {
            this.mPendingExecutor = null;
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void finishFirstPageBind(final ViewOnDrawExecutor executor) {
        MultiValueAlpha.AlphaProperty alphaProperty = this.mDragLayer.getAlphaProperty(1);
        if (alphaProperty.getValue() >= 1.0f) {
            if (executor != null) {
                executor.onLoadAnimationCompleted();
            }
        } else {
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(alphaProperty, MultiValueAlpha.VALUE, 1.0f);
            if (executor != null) {
                objectAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.Launcher.33
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        executor.onLoadAnimationCompleted();
                    }
                });
            }
            objectAnimatorOfFloat.start();
        }
    }

    public void finishBindingItems() {
        if (this.mWorkspaceVisibility != null) {
            LGLog.d("Launcher", "finishBindingItems : call mWorkspaceVisibility");
            this.mWorkspaceVisibility.run();
            this.mWorkspaceVisibility = null;
        }
        if (this.mSavedState != null) {
            if (!this.mWorkspace.hasFocus()) {
                Workspace workspace = this.mWorkspace;
                workspace.getChildAt(workspace.getCurrentPage()).requestFocus();
            }
            this.mSavedState = null;
        }
        this.mWorkspace.restoreInstanceStateForRemainingPages();
        setWorkspaceLoading(false);
        sendLoadingCompleteBroadcastIfNecessary();
        ActivityResultInfo activityResultInfo = this.mPendingActivityResult;
        if (activityResultInfo != null) {
            handleActivityResult(activityResultInfo.requestCode, this.mPendingActivityResult.resultCode, this.mPendingActivityResult.data);
            this.mPendingActivityResult = null;
        }
        InstallShortcutReceiver.disableAndFlushInstallQueue(this);
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.finishBindingItems(false);
        }
        LGLog.d("Launcher", "finishBindingItems end");
    }

    private void sendLoadingCompleteBroadcastIfNecessary() {
        if (this.mSharedPrefs.getBoolean(FIRST_LOAD_COMPLETE, false)) {
            return;
        }
        sendBroadcast(new Intent(ACTION_FIRST_LOAD_COMPLETE), getResources().getString(R.string.receive_first_load_broadcast_permission));
        SharedPreferences.Editor editorEdit = this.mSharedPrefs.edit();
        editorEdit.putBoolean(FIRST_LOAD_COMPLETE, true);
        editorEdit.apply();
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public boolean isAllAppsButtonRank(int rank) {
        Hotseat hotseat = this.mHotseat;
        if (hotseat != null) {
            return hotseat.isAllAppsButtonRank(rank);
        }
        return false;
    }

    protected boolean canRunNewAppsAnimation() {
        return System.currentTimeMillis() - this.mDragController.getLastGestureUpTime() > ((long) (NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS * 1000));
    }

    private ValueAnimator createNewAppBounceAnimation(View v, int i) {
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(v, PropertyValuesHolder.ofFloat("alpha", 1.0f), PropertyValuesHolder.ofFloat("scaleX", 1.0f), PropertyValuesHolder.ofFloat("scaleY", 1.0f));
        objectAnimatorOfPropertyValuesHolder.setDuration(450L);
        objectAnimatorOfPropertyValuesHolder.setStartDelay(i * 85);
        objectAnimatorOfPropertyValuesHolder.setInterpolator(new OvershootInterpolator(BOUNCE_ANIMATION_TENSION));
        return objectAnimatorOfPropertyValuesHolder;
    }

    public boolean useVerticalBarLayout() {
        return this.mDeviceProfile.isVerticalBarLayout();
    }

    protected Rect getSearchBarBounds() {
        return this.mDeviceProfile.getSearchBarBounds(Utilities.isRtl(getResources()));
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindSearchablesChanged() {
        SearchDropTargetBar searchDropTargetBar = this.mSearchDropTargetBar;
        if (searchDropTargetBar == null) {
            return;
        }
        AppWidgetHostView appWidgetHostView = this.mQsb;
        if (appWidgetHostView != null) {
            searchDropTargetBar.removeView(appWidgetHostView);
            this.mQsb = null;
        }
        this.mSearchDropTargetBar.setQsbSearchBar(getOrCreateQsbBar());
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindAllApplications(final ArrayList<AppInfo> apps) {
        int size = apps != null ? apps.size() : 0;
        if (waitUntilResume(this.mBindAllApplicationsRunnable, true)) {
            LGLog.i("Launcher", "bindAllApplications will be called by onResume : mPaused = " + this.mPaused + ", size = " + size);
            this.mTmpAppsList = apps;
            return;
        }
        if (this.mAppsCustomizeHost != null) {
            LGLog.i("Launcher", "call bindAllApplications of AppsCustomizeHost : mPaused = " + this.mPaused + ", size = " + size);
            this.mAppsCustomizeHost.bindAllApplications(apps);
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.bindAllApplications(apps);
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindDeepShortcutMap(MultiHashMap<ComponentKey, String> deepShortcutMapCopy) {
        this.mPopupDataProvider.setDeepShortcutMap(deepShortcutMapCopy);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindAppsUpdated(final ArrayList<AppInfo> apps) {
        AllAppsHost allAppsHost;
        if (waitUntilResume(new Runnable() { // from class: com.android.launcher3.Launcher.35
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.bindAppsUpdated(apps);
            }
        }) || (allAppsHost = this.mAppsCustomizeHost) == null) {
            return;
        }
        allAppsHost.updateApps(apps);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindSilentAppsInFolderUpdated(final FolderInfo folder, final ShortcutInfo info) {
        if (waitUntilResume(new Runnable() { // from class: com.android.launcher3.Launcher.36
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.bindSilentAppsInFolderUpdated(folder, info);
            }
        })) {
            return;
        }
        folder.add(info);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindWidgetsRestored(final ArrayList<LauncherAppWidgetInfo> widgets) {
        if (waitUntilResume(new Runnable() { // from class: com.android.launcher3.Launcher.37
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.bindWidgetsRestored(widgets);
            }
        })) {
            return;
        }
        this.mWorkspace.widgetsRestored(widgets);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindShortcutsChanged(final ArrayList<ShortcutInfo> updated, final ArrayList<ShortcutInfo> removed, final UserHandle user) {
        if (waitUntilResume(new Runnable() { // from class: com.android.launcher3.Launcher.38
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.bindShortcutsChanged(updated, removed, user);
            }
        })) {
            return;
        }
        if (!updated.isEmpty()) {
            this.mWorkspace.updateShortcuts(updated);
            CarouselLayout carouselLayout = this.mCarouselLayout;
            if (carouselLayout != null) {
                carouselLayout.getAdapter().onItemUpdate(updated);
            }
        }
        if (removed.isEmpty()) {
            return;
        }
        HashSet<ComponentName> hashSet = new HashSet<>();
        HashSet hashSet2 = new HashSet();
        for (ShortcutInfo shortcutInfo : removed) {
            if (shortcutInfo.itemType == 6) {
                hashSet2.add(ShortcutKey.fromShortcutInfo(shortcutInfo));
            } else {
                hashSet.add(shortcutInfo.getTargetComponent());
            }
        }
        this.mWorkspace.removeItemsByComponentName(hashSet, user);
        this.mDragController.onAppsRemoved(new HashSet<>(), hashSet);
        if (hashSet2.isEmpty()) {
            return;
        }
        ItemInfoMatcher itemInfoMatcherOfShortcutKeys = ItemInfoMatcher.ofShortcutKeys(hashSet2);
        this.mWorkspace.removeItemsByMatcher(itemInfoMatcherOfShortcutKeys);
        this.mDragController.onAppsRemoved(itemInfoMatcherOfShortcutKeys);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindRestoreItemsChange(final HashSet<ItemInfo> updates) {
        if (waitUntilResume(new Runnable() { // from class: com.android.launcher3.Launcher.39
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.bindRestoreItemsChange(updates);
            }
        })) {
            return;
        }
        this.mWorkspace.updateRestoreItems(updates);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindWorkspaceComponentsRemoved(final HashSet<String> packageNames, final HashSet<ComponentName> components, final UserHandle user) {
        if (waitUntilResume(new Runnable() { // from class: com.android.launcher3.Launcher.40
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.bindWorkspaceComponentsRemoved(packageNames, components, user);
            }
        })) {
            return;
        }
        if (!packageNames.isEmpty()) {
            this.mWorkspace.removeItemsByPackageName(packageNames, user);
        }
        if (!components.isEmpty()) {
            this.mWorkspace.removeItemsByComponentName(components, user);
        }
        this.mDragController.onAppsRemoved(packageNames, components);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindAppInfosRemoved(final ArrayList<AppInfo> appInfos) {
        if (waitUntilResume(new Runnable() { // from class: com.android.launcher3.Launcher.41
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.bindAppInfosRemoved(appInfos);
            }
        })) {
            return;
        }
        AllAppsHost allAppsHost = this.mAppsCustomizeHost;
        if (allAppsHost != null) {
            allAppsHost.removeApps(appInfos);
        }
        QuickstepTransitionManager quickstepTransitionManager = this.mAppTransitionManager;
        if (quickstepTransitionManager == null || !(quickstepTransitionManager instanceof QuickstepTransitionManager) || quickstepTransitionManager.checkLaunchedState() == null) {
            return;
        }
        quickstepTransitionManager.updateRemovedApp(appInfos);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindAppsMoved(final ArrayList<ItemInfo> items, final FolderInfo target) {
        if (waitUntilResume(new Runnable() { // from class: com.android.launcher3.Launcher.42
            @Override // java.lang.Runnable
            public void run() {
                Launcher.this.bindAppsMoved(items, target);
            }
        })) {
            return;
        }
        this.mWorkspace.removeItemsByList(items, target);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void bindAllPackages(final WidgetsModel model) {
        if (waitUntilResume(this.mBindPackagesUpdatedRunnable, true)) {
            this.mWidgetsModel = model;
            return;
        }
        WidgetsContainerView widgetsContainerView = this.mWidgetsView;
        if (widgetsContainerView == null || model == null) {
            return;
        }
        widgetsContainerView.addWidgets(model);
        this.mWidgetsModel = null;
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    protected StateManager.StateHandler<LauncherState>[] createStateHandlers() {
        return new StateManager.StateHandler[]{getAllAppsController(), getWorkspace()};
    }

    public TouchController[] createTouchControllers() {
        return new TouchController[]{getDragController(), new AllAppsSwipeController(this)};
    }

    /* JADX WARN: Removed duplicated region for block: B:8:0x0017  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x0019  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int mapConfigurationOriActivityInfoOri(int r7) {
        /*
            r6 = this;
            android.view.WindowManager r0 = r6.getWindowManager()
            android.view.Display r0 = r0.getDefaultDisplay()
            int r1 = r0.getRotation()
            r2 = 1
            r3 = 2
            if (r1 == 0) goto L1c
            if (r1 == r2) goto L19
            if (r1 == r3) goto L1c
            r4 = 3
            if (r1 == r4) goto L19
        L17:
            r7 = r3
            goto L1c
        L19:
            if (r7 != r3) goto L17
            r7 = r2
        L1c:
            r1 = 4
            int[] r4 = new int[r1]
            r4 = {x0030: FILL_ARRAY_DATA , data: [1, 0, 9, 8} // fill-array
            r5 = 0
            if (r7 != r3) goto L26
            goto L27
        L26:
            r2 = r5
        L27:
            int r7 = r0.getRotation()
            int r7 = r7 + r2
            int r7 = r7 % r1
            r7 = r4[r7]
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Launcher.mapConfigurationOriActivityInfoOri(int):int");
    }

    public void lockScreenOrientation() {
        if (this.mRotationEnabled) {
            if (Build.VERSION.SDK_INT < 18) {
                setRequestedOrientation(mapConfigurationOriActivityInfoOri(getResources().getConfiguration().orientation));
            } else {
                setRequestedOrientation(14);
            }
        }
    }

    public void unlockScreenOrientation(boolean immediate) {
        if (this.mRotationEnabled) {
            if (immediate) {
                setRequestedOrientation(-1);
            } else {
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.launcher3.Launcher.44
                    @Override // java.lang.Runnable
                    public void run() {
                        Launcher.this.setRequestedOrientation(-1);
                    }
                }, 500L);
            }
        }
    }

    protected boolean isLauncherPreinstalled() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            return launcherCallbacks.isLauncherPreinstalled();
        }
        try {
            return (getPackageManager().getApplicationInfo(getComponentName().getPackageName(), 0).flags & 1) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean overrideWallpaperDimensions() {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            return launcherCallbacks.overrideWallpaperDimensions();
        }
        return true;
    }

    void showWorkspaceSearchAndHotseat() {
        Workspace workspace = this.mWorkspace;
        if (workspace != null) {
            workspace.setAlpha(1.0f);
        }
        Hotseat hotseat = this.mHotseat;
        if (hotseat != null) {
            hotseat.setAlpha(1.0f);
        }
        View view = this.mPageIndicators;
        if (view != null) {
            view.setAlpha(1.0f);
        }
        SearchDropTargetBar searchDropTargetBar = this.mSearchDropTargetBar;
        if (searchDropTargetBar != null) {
            searchDropTargetBar.showSearchBar(false);
        }
    }

    void hideWorkspaceSearchAndHotseat() {
        Workspace workspace = this.mWorkspace;
        if (workspace != null) {
            workspace.setAlpha(0.0f);
        }
        Hotseat hotseat = this.mHotseat;
        if (hotseat != null) {
            hotseat.setAlpha(0.0f);
        }
        View view = this.mPageIndicators;
        if (view != null) {
            view.setAlpha(0.0f);
        }
        SearchDropTargetBar searchDropTargetBar = this.mSearchDropTargetBar;
        if (searchDropTargetBar != null) {
            searchDropTargetBar.hideSearchBar(false);
        }
    }

    public ItemInfo createAppDragInfo(Intent appLaunchIntent) {
        UserHandle userHandle;
        if (!Utilities.isLmpOrAbove() || (userHandle = (UserHandle) appLaunchIntent.getParcelableExtra("android.intent.extra.USER")) == null) {
            userHandle = null;
        }
        return createAppDragInfo(appLaunchIntent, userHandle);
    }

    public ItemInfo createAppDragInfo(Intent intent, UserHandle user) {
        if (user == null) {
            user = Process.myUserHandle();
        }
        LauncherActivityInfo launcherActivityInfoResolveActivity = LauncherAppsCompat.getInstance(this).resolveActivity(intent, user);
        if (launcherActivityInfoResolveActivity == null) {
            return null;
        }
        return new AppInfo(this, launcherActivityInfoResolveActivity, user, this.mIconCache);
    }

    public ItemInfo createShortcutDragInfo(Intent shortcutIntent, CharSequence caption, Bitmap icon) {
        return new ShortcutInfo(shortcutIntent, caption, caption, icon, Process.myUserHandle());
    }

    public void startDrag(View dragView, ItemInfo dragInfo, DragSource source) {
        dragView.setTag(dragInfo);
        this.mWorkspace.onExternalDragStartedWithItem(dragView);
        this.mWorkspace.beginExternalDragShared(dragView, source);
    }

    protected void moveWorkspaceToDefaultScreen() {
        this.mWorkspace.moveToDefaultScreen(false);
    }

    @Override // com.lge.launcher3.PagedView.PageSwitchListener
    public void onPageSwitch(View newPage, int newPageIndex) {
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.onPageSwitch(newPage, newPageIndex);
        }
    }

    public static FastBitmapDrawable createIconDrawable(Bitmap icon, int iconSizePx) {
        FastBitmapDrawable fastBitmapDrawable = new FastBitmapDrawable(icon);
        fastBitmapDrawable.setFilterBitmap(true);
        resizeIconDrawable(fastBitmapDrawable, iconSizePx);
        return fastBitmapDrawable;
    }

    public static void resizeIconDrawable(Drawable icon, int iconSizePx) {
        icon.setBounds(0, 0, iconSizePx, iconSizePx);
    }

    public void dumpState() {
        Log.d("Launcher", "BEGIN launcher3 dump state for launcher " + this);
        Log.d("Launcher", "mSavedState=" + this.mSavedState);
        Log.d("Launcher", "mWorkspaceLoading=" + this.mWorkspaceLoading);
        Log.d("Launcher", "mRestoring=" + this.mRestoring);
        Log.d("Launcher", "mWaitingForResult=" + this.mWaitingForResult);
        Log.d("Launcher", "mSavedInstanceState=" + this.mSavedInstanceState);
        Log.d("Launcher", "sFolders.size=" + sFolders.size());
        this.mModel.dumpState();
        Log.d("Launcher", "END launcher3 dump state");
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        synchronized (sDumpLogs) {
            writer.println(" ");
            writer.println("Debug logs: ");
            int i = 0;
            while (true) {
                ArrayList<String> arrayList = sDumpLogs;
                if (i >= arrayList.size()) {
                    break;
                }
                writer.println("  " + arrayList.get(i));
                i++;
            }
        }
        writer.println(prefix + "Misc:");
        dumpMisc(prefix + "\t", writer);
        writer.println(prefix + "\tmWorkspaceLoading=" + this.mWorkspaceLoading);
        writer.println(prefix + "\tmPendingRequestArgs=" + this.mPendingRequestArgs + " mPendingActivityResult=" + this.mPendingActivityResult);
        RotationHelper rotationHelper = this.mRotationHelper;
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("\tmRotationHelper: ");
        sb.append(rotationHelper);
        writer.println(sb.toString());
        this.mDragLayer.dump(prefix, writer);
        this.mStateManager.dump(prefix, writer);
        this.mPopupDataProvider.dump(prefix, writer);
        try {
            FileLog.flushAll(writer);
        } catch (Exception unused) {
        }
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.dump(prefix, fd, writer, args);
        }
        this.mOverlayManager.dump(prefix, writer);
    }

    public static void addDumpLog(String tag, String log, boolean debugLog) {
        addDumpLog(tag, log, null, debugLog);
    }

    public static void addDumpLog(String tag, String log, Exception e, boolean debugLog) {
        if (debugLog) {
            if (e != null) {
                Log.d(tag, log, e);
            } else {
                Log.d(tag, log);
            }
        }
    }

    public static CustomAppWidget getCustomAppWidget(String name) {
        return sCustomAppWidgets.get(name);
    }

    public static HashMap<String, CustomAppWidget> getCustomAppWidgets() {
        return sCustomAppWidgets;
    }

    @Override // com.android.launcher3.LauncherProviderChangeListener
    public void onDeleteAppWidgetIds(int[] ids) {
        for (int i : ids) {
            this.mAppWidgetHost.deleteAppWidgetId(i);
        }
    }

    public void processShortcutFromFileManager(ComponentName componentName, long container, long screenId, int[] cell) {
        resetAddInfo();
        this.mPendingAddInfo.container = container;
        this.mPendingAddInfo.screenId = screenId;
        this.mPendingAddInfo.dropPos = null;
        this.mPendingAddInfo.componentName = componentName;
        if (cell != null) {
            this.mPendingAddInfo.cellX = cell[0];
            this.mPendingAddInfo.cellY = cell[1];
        }
    }

    public void enterCleanViewMode() {
        if (isInState(LauncherState.NORMAL)) {
            this.mStateManager.setStateOnly(LauncherState.CLEAN_VIEW);
            this.mHotword.requestHotwordDetectionIfNeeded();
            sendCleanViewActivatedIntent(this);
            this.mWorkspace.animateIntoCleanView(true);
            LGUserLog.send(this, LGUserLog.FEATURENAME_CLEANVIEW);
            this.mWorkspace.getCurrentPageDescription();
            cancelWorkspaceLongpress();
        }
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity, com.android.launcher3.LauncherModel.Callbacks
    public void exitCleanViewMode() {
        if (isInState(LauncherState.CLEAN_VIEW)) {
            sendCleanViewDeactivatedIntent(this);
            this.mWorkspace.animateOutCleanView();
            this.mWorkspace.getCleanViewExitDescription();
            this.mStateManager.setStateOnly(LauncherState.NORMAL);
            this.mHotword.requestHotwordDetectionIfNeeded();
        }
    }

    public static void sendCleanViewActivatedIntent(final Activity activity) {
        Intent intent = new Intent();
        intent.setAction("com.lge.livewallpaper.cleanview.ACTIVATED");
        activity.sendBroadcast(intent);
        LGLog.i("Launcher", "CleanView activated!");
    }

    public static void sendCleanViewDeactivatedIntent(final Activity activity) {
        Intent intent = new Intent();
        intent.setAction("com.lge.livewallpaper.cleanview.DEACTIVATED");
        activity.sendBroadcast(intent);
        LGLog.i("Launcher", "CleanView deactivated!");
    }

    public LauncherState getState() {
        return (LauncherState) this.mStateManager.getState();
    }

    public void setState(LauncherState curState) {
        ConciergeBoardMngr.enableConciergeExtView(isInState(LauncherState.NORMAL));
        LGLog.v("Launcher", "launcherSetState " + this.mStateManager.getState());
        this.mStateManager.setStateOnly(curState);
        this.mHotword.requestHotwordDetectionIfNeeded();
        LGLog.d("Launcher", "mState = " + this.mStateManager.getState());
    }

    public boolean isWorkspaceState() {
        return isInState(LauncherState.NORMAL) || this.mOnResumeState == LauncherState.NORMAL;
    }

    public boolean isCleanViewState() {
        return isInState(LauncherState.CLEAN_VIEW);
    }

    public void moveWorkspaceToDefaultScreenWithAnimation() {
        this.mWorkspace.moveToDefaultScreen(true);
    }

    public void enablePageAsFullSize(boolean fullScreenEnabled, boolean isVerticalLayout) {
        this.mWorkspace.setFullScreenPage(this.mWorkspace.getPageAt(0), fullScreenEnabled, isVerticalLayout);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void addWidgetInNewPage(Intent intent) {
        ComponentName componentName = new ComponentName(intent.getStringExtra(PendingIntentObjectList.CotaReloadHandler.COTA_EXTRA_PACKAGE), intent.getStringExtra("classname"));
        if (ConciergeBoardNotificationReceiver.isExistConciergeBoardScreenInDatabase(this) != -1) {
            LGLog.d("Launcher", "SmartNotice widget already exists");
            return;
        }
        PendingRequestArgs pendingRequestArgs = this.mPendingRequestArgs;
        if (pendingRequestArgs == null) {
            return;
        }
        this.mWorkspace.addExtraEmptyScreen();
        long jCommitExtraEmptyScreen = this.mWorkspace.commitExtraEmptyScreen();
        int iAllocateAppWidgetId = getAppWidgetHost().allocateAppWidgetId();
        LauncherAppWidgetProviderInfo providerInfo = LauncherModel.getProviderInfo(this, componentName, Process.myUserHandle());
        boolean zBindAppWidgetIdIfAllowed = this.mAppWidgetManager.bindAppWidgetIdIfAllowed(iAllocateAppWidgetId, providerInfo, null);
        LGLog.d("Launcher", "success = " + zBindAppWidgetIdIfAllowed + ", appWidgetId = " + iAllocateAppWidgetId + ", appWidgetInfo = " + providerInfo);
        if (zBindAppWidgetIdIfAllowed) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.container = -100L;
            itemInfo.screenId = jCommitExtraEmptyScreen;
            itemInfo.cellX = 0;
            itemInfo.cellY = 0;
            int sharedPrefValue = LGInvariantDeviceProfile.getSharedPrefValue(getApplicationContext(), SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, 0);
            itemInfo.spanX = providerInfo.getSpanX(this);
            if (sharedPrefValue > 0 && itemInfo.spanX > sharedPrefValue) {
                LGLog.d("Launcher", "info.spanX is changed " + itemInfo.spanX + " to " + sharedPrefValue);
                itemInfo.spanX = sharedPrefValue;
            }
            itemInfo.spanY = providerInfo.getSpanY(this);
            LGLog.d("Launcher", "info = " + itemInfo);
            resetAddInfo();
            this.mPendingAddInfo.copyFrom(itemInfo);
            completeAddAppWidget(iAllocateAppWidgetId, itemInfo, null, pendingRequestArgs.getWidgetHandler().getProviderInfo(this));
            getModel().forceReload();
            showWorkspace(false);
            new Handler().postDelayed(new Runnable() { // from class: com.android.launcher3.Launcher.46
                @Override // java.lang.Runnable
                public void run() {
                    int childCount = Launcher.this.mWorkspace.getChildCount() - 1;
                    LGLog.d("Launcher", "goToPage = " + childCount);
                    Launcher.this.mWorkspace.snapToPage(childCount);
                }
            }, 1000L);
        }
    }

    public boolean checkActionCallSelfPermission(View v) {
        Intent intent = ((ShortcutInfo) v.getTag()).intent;
        if (!Utilities.ATLEAST_MARSHMALLOW || !"android.intent.action.CALL".equals(intent.getAction()) || checkSelfPermission("android.permission.CALL_PHONE") == 0) {
            return true;
        }
        requestPermissions(new String[]{"android.permission.CALL_PHONE"}, LauncherConst.REQUEST_CALL_PHONE_PERMISSION);
        return false;
    }

    public boolean isSafeMode() {
        return this.mIsSafeModeEnabled;
    }

    public void setWorkspaceBG(boolean isDark) {
        Drawable drawable = this.mWorkspaceBackgroundDrawable;
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            layerDrawable.getDrawable(0).setAlpha(isDark ? 0 : 255);
            layerDrawable.getDrawable(1).setAlpha(isDark ? 255 : 0);
        } else if (isDark) {
            drawable.setTint(getColor(R.color.white_color));
        } else {
            drawable.setTint(getColor(R.color.black_color));
        }
    }

    public ViewGroup getDynamicGridPannelView() {
        return this.mDynamicGridPanelView;
    }

    public boolean isDynamicGridOverViewVisible() {
        ViewGroup viewGroup = this.mDynamicGridPanelView;
        return (viewGroup != null) & (viewGroup.getVisibility() == 0);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void finishBindingHotSeats() {
        this.mHotseat.setupAllAppsButton();
    }

    public void shrinkAndFadeOutWorkspaceItem(final View view) {
        if (view == null) {
            return;
        }
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat("alpha", 0.0f), PropertyValuesHolder.ofFloat("scaleX", 0.0f), PropertyValuesHolder.ofFloat("scaleY", 0.0f));
        objectAnimatorOfPropertyValuesHolder.setDuration(450L);
        objectAnimatorOfPropertyValuesHolder.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.Launcher.47
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                CellLayout parentCellLayoutForView = Launcher.this.mWorkspace.getParentCellLayoutForView(view);
                if (parentCellLayoutForView != null) {
                    parentCellLayoutForView.removeViewInLayout(view);
                } else {
                    LGLog.d("Launcher", "can't remove view(" + view + ") because parent cell is null");
                }
                Launcher.this.mWorkspace.stripEmptyScreens();
            }
        });
        objectAnimatorOfPropertyValuesHolder.start();
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void removeWorkspaceEmptyScreen() {
        getWorkspace().removeExtraEmptyScreenDelayed(false, this.mBuildLayersRunnable, FastBitmapDrawable.CLICK_FEEDBACK_DURATION, true);
    }

    public HotwordServiceWrapper getHotword() {
        return this.mHotword;
    }

    public AllAppsHost getAllAppsHost() {
        return this.mAppsCustomizeHost;
    }

    public void showAllAppsView(boolean animated, boolean resetListToTop, boolean updatePredictedApps, boolean focusSearchBar) {
        showAllApps(LauncherState.ALL_APPS, animated, focusSearchBar);
    }

    private boolean showAllApps(LauncherState toState, boolean animated, boolean focusSearchBar) {
        if (!isInState(LauncherState.NORMAL) && !isInState(LauncherState.APPS_SPRING_LOADED) && !isInState(LauncherState.WIDGETS_SPRING_LOADED)) {
            return false;
        }
        if (toState != LauncherState.ALL_APPS && toState != LauncherState.WIDGETS) {
            return false;
        }
        WallpaperMotionManager wallpaperMotionManager = this.mWallpaperMotionManager;
        if (wallpaperMotionManager != null) {
            wallpaperMotionManager.setEnableParallax(false);
        }
        ((LauncherExtension) this).getSwipeUpGuideAnimation().cancelSwipeUpAnim();
        if (toState == LauncherState.ALL_APPS) {
            this.mStateManager.goToState(toState);
        }
        this.mUserPresent = false;
        updateAutoAdvanceState();
        closeFolder(new boolean[0]);
        AbstractFloatingView.closeAllOpenViews(this);
        if (TalkBackUtils.isEnabled(getApplicationContext())) {
            TalkBackUtils.sendAccessibilityEvent((Context) this, this.mAppsCustomizeHost.getLGAllAppsPagedView().getCurrentPageDescription(), true);
        }
        AdaptiveTextUtil.setAdaptiveSystemUi(getWindow().getDecorView(), this, false);
        return true;
    }

    private void saveMenuState(Bundle outState) {
        Bundle bundle;
        AllAppsHost allAppsHost = this.mAppsCustomizeHost;
        if (allAppsHost != null || (bundle = this.mMenuSavedState) == null) {
            if (allAppsHost != null) {
                allAppsHost.saveInstanceState(outState, isInState(LauncherState.ALL_APPS));
                return;
            }
            return;
        }
        String string = bundle.getString("apps_customize_currentTab");
        if (string != null) {
            outState.putString("apps_customize_currentTab", string);
        }
        outState.putInt("apps_customize_currentIndex", this.mMenuSavedState.getInt("apps_customize_currentIndex"));
        outState.putBoolean("apps_customize_editmde", this.mMenuSavedState.getBoolean("apps_customize_editmde"));
        outState.putBoolean("apps_customize_searchstatus", this.mMenuSavedState.getBoolean("apps_customize_searchstatus"));
        boolean z = this.mMenuSavedState.getBoolean("apps_customize_multiselectorstatus");
        outState.putBoolean("apps_customize_multiselectorstatus", z);
        if (z) {
            outState.putIntArray("apps_customize_multiselectorindex", this.mMenuSavedState.getIntArray("apps_customize_multiselectorindex"));
        }
    }

    private void loadMenuStub() {
        ViewStub viewStub;
        if ((!LGHomeFeature.isEnableDefaultHome() || Utilities.ATLEAST_P) && this.mAppsCustomizeHost == null && (viewStub = (ViewStub) findViewById(R.id.all_apps_view_stub)) != null) {
            viewStub.inflate();
            Log.d("Launcher", "loadMenuStub allappsview");
            AllAppsHost allAppsHost = (AllAppsHost) findViewById(R.id.lg_apps_customize_pane);
            this.mAppsCustomizeHost = allAppsHost;
            if (allAppsHost != null) {
                allAppsHost.setup(this, this.mDragController);
                setAllAppsAlphaAndVisibility(4);
                Bundle bundle = this.mMenuSavedState;
                if (bundle != null) {
                    this.mAppsCustomizeHost.restoreState(bundle);
                    this.mMenuSavedState = null;
                }
            }
        }
    }

    public boolean isPaused() {
        return this.mPaused;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    private void dispatchOnLauncherTransitionPrepare(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionPrepare(this, animated, toWorkspace);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    private void dispatchOnLauncherTransitionStart(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionStart(this, animated, toWorkspace);
        }
        dispatchOnLauncherTransitionStep(v, 0.0f);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    private void dispatchOnLauncherTransitionStep(View v, float t) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionStep(this, t);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    private void dispatchOnLauncherTransitionEnd(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionEnd(this, animated, toWorkspace);
        }
        dispatchOnLauncherTransitionStep(v, 1.0f);
    }

    public List<String> getShortcutIdsForItem(ItemInfo info) {
        if (!DeepShortcutManager.supportsShortcuts(info)) {
            return Collections.EMPTY_LIST;
        }
        ComponentName targetComponent = info.getTargetComponent();
        if (targetComponent == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> list = (List) this.mDeepShortcutMap.get(new ComponentKey(targetComponent, info.user));
        return list == null ? Collections.EMPTY_LIST : list;
    }

    @Override // com.android.launcher3.BaseDraggingActivity
    public void returnToHomescreen() {
        super.returnToHomescreen();
        getStateManager().goToState(LauncherState.NORMAL);
    }

    private void closeOpenViews() {
        closeOpenViews(true);
    }

    protected void closeOpenViews(boolean animate) {
        AbstractFloatingView.closeAllOpenViews(this, animate);
    }

    public Stream<SystemShortcut.Factory> getSupportedShortcuts() {
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            return com.lge.launcher3.util.Utilities.isLGUI10_0() ? LGHomeFeature.isEnableDefaultHome() ? Stream.of((Object[]) new SystemShortcut.Factory[]{SystemShortcut.APP_INFO_SWIVEL, SystemShortcut.WIDGETS, SystemShortcut.DELETE}) : isAllAppsVisible() ? Stream.of((Object[]) new SystemShortcut.Factory[]{SystemShortcut.APP_INFO_SWIVEL, SystemShortcut.WIDGETS}) : Stream.of((Object[]) new SystemShortcut.Factory[]{SystemShortcut.APP_INFO_SWIVEL, SystemShortcut.WIDGETS, SystemShortcut.REMOVE, SystemShortcut.DELETE}) : Stream.of((Object[]) new SystemShortcut.Factory[]{SystemShortcut.APP_INFO, SystemShortcut.WIDGETS});
        }
        LGLog.i("Launcher", "getEnabledSystemShortcutsForItem() isAllAppsVisible = " + isAllAppsVisible());
        return isAllAppsVisible() ? Stream.of((Object[]) new SystemShortcut.Factory[]{SystemShortcut.APP_INFO_SWIVEL, SystemShortcut.DELETE}) : Stream.of((Object[]) new SystemShortcut.Factory[]{SystemShortcut.APP_INFO_SWIVEL, SystemShortcut.REMOVE, SystemShortcut.DELETE});
    }

    public Stream<SystemShortcut.Factory> getSupportedShortcutsForWidget(boolean showSettingIcon) {
        return showSettingIcon ? Stream.of((Object[]) new SystemShortcut.Factory[]{SystemShortcut.APP_INFO_SWIVEL, SystemShortcut.REMOVE, SystemShortcut.WIDGET_SETTING}) : Stream.of((Object[]) new SystemShortcut.Factory[]{SystemShortcut.APP_INFO_SWIVEL, SystemShortcut.REMOVE});
    }

    public float[] getNormalOverviewScaleAndOffset() {
        return new float[]{1.0f, 0.0f};
    }

    public static Launcher getLauncher(Context context) {
        if (context instanceof Launcher) {
            return (Launcher) context;
        }
        return (Launcher) ((ContextWrapper) context).getBaseContext();
    }

    private void startShortcutIntentSafely(Intent intent, Bundle optsBundle, ItemInfo info) {
        try {
            StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
            try {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
                if (info.itemType == 6) {
                    String deepShortcutId = ((ShortcutInfo) info).getDeepShortcutId();
                    LauncherAppState.getInstance(this).getShortcutManager().startShortcut(intent.getPackage(), deepShortcutId, intent.getSourceBounds(), optsBundle, info.user);
                } else {
                    startActivity(intent, optsBundle);
                }
                StrictMode.setVmPolicy(vmPolicy);
            } catch (Throwable th) {
                StrictMode.setVmPolicy(vmPolicy);
                throw th;
            }
        } catch (SecurityException e) {
            if (intent.getComponent() != null || !"android.intent.action.CALL".equals(intent.getAction()) || checkSelfPermission("android.permission.CALL_PHONE") == 0) {
                throw e;
            }
        }
    }

    public List<WidgetItem> getWidgetsForPackageUser(PackageUserKey packageUserKey) {
        setWidgetView();
        return this.mWidgetsView.getWidgetsForPackageUser(packageUserKey);
    }

    private void setWidgetView() {
        WidgetsModel widgetsModel;
        if (this.mWidgetsView == null) {
            WidgetsContainerView widgetsContainerView = (WidgetsContainerView) ((ViewStub) findViewById(R.id.widgets_view_stub)).inflate();
            this.mWidgetsView = widgetsContainerView;
            widgetsContainerView.setVisibility(4);
        }
        WidgetsContainerView widgetsContainerView2 = this.mWidgetsView;
        if (widgetsContainerView2 == null || (widgetsModel = this.mWidgetsModel) == null) {
            return;
        }
        widgetsContainerView2.addWidgets(widgetsModel);
        this.mWidgetsModel = null;
    }

    public void activateLightSystemBars(boolean isLight, boolean statusBar, boolean navBar) {
        int i;
        int systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        if (isLight) {
            i = statusBar ? systemUiVisibility | 8 : systemUiVisibility;
            if (navBar && Utilities.isAtLeastO()) {
                i |= 16;
            }
        } else {
            i = statusBar ? systemUiVisibility & (-9) : systemUiVisibility;
            if (navBar && Utilities.isAtLeastO()) {
                i &= -17;
            }
        }
        if (i != systemUiVisibility) {
            getWindow().getDecorView().setSystemUiVisibility(i);
        }
        this.mLauncherCallbacks.setLauncherOverlayLightNavigationBar(isLight);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void notifyGoogleNowStatus(boolean isEnabled) {
        Log.d("Launcher", "notifyGoogleNowStatus isEnabled = " + isEnabled);
        Workspace workspace = this.mWorkspace;
        if (workspace != null) {
            workspace.resetGoogleNowPageIndicator(isEnabled);
        }
        if (isEnabled && HomeSettingsSharedPreferences.getContinuousLoopEnabled(getApplicationContext())) {
            HomeSettingsSharedPreferences.setContinuousLoopEnabled(getApplicationContext(), false);
            LoopNormalModeManager.getInstance(getApplicationContext()).updateFeatureEnabled();
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void notifyManagedProfileStatus(boolean isAdded) {
        LGLog.d("Launcher", "notifyManagedProfileStatus: " + isAdded);
        if (isAdded) {
            AllAppsHost allAppsHost = this.mAppsCustomizeHost;
            if (allAppsHost != null) {
                allAppsHost.setOptionMenuVisibility(8);
                this.mAppsCustomizeHost.onTabChanged(0);
                if (!LGHomeFeature.isEnableDefaultHome()) {
                    LGLog.d("Launcher", "need to show all apps work tab");
                    this.mAppsCustomizeHost.setShowWorkTabIfNeeded();
                } else {
                    this.mNeedToWorkFolderPage = true;
                }
                this.mStateManager.goToState(LauncherState.NORMAL);
                return;
            }
            return;
        }
        AllAppsHost allAppsHost2 = this.mAppsCustomizeHost;
        if (allAppsHost2 != null) {
            allAppsHost2.setOptionMenuVisibility(0);
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void updateStringCache() {
        if (this.mStringCache == null) {
            this.mStringCache = new StringCache();
        }
        this.mStringCache.loadStrings(getApplicationContext());
    }

    public StringCache getStringCache() {
        return this.mStringCache;
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void updateWorkProfileComponent() {
        AllAppsHost allAppsHost = this.mAppsCustomizeHost;
        if (allAppsHost != null) {
            allAppsHost.updateWorkProfileComponent();
        }
    }

    public int getHomeVisibility() {
        if (isInState(LauncherState.ALL_APPS)) {
            return this.mAppsCustomizeHost.getVisibility();
        }
        return this.mWorkspace.getVisibility();
    }

    public void setWorkspaceAndHotseatVisibility(int visibility, String caller) {
        LGLog.d("Launcher", "setWorkspaceAndHotseatVisibility. new visibility = " + visibility + ", caller = " + caller);
        if (getWorkspace() != null) {
            getWorkspace().setVisibility(0);
            if (getWorkspace().getState() != Workspace.State.NORMAL) {
                showWorkspace(false);
            }
        }
        Workspace workspace = this.mWorkspace;
        if (workspace != null) {
            workspace.setVisibility(visibility);
            if (((LGHomeFeature.Config.FEATURE_USE_SIMPLE_TRANSITION_OF_LANDSCAPE.getValue() && getResources().getInteger(R.integer.config_simple_transition_landscape) == 1) || LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) && this.mWorkspace.getPageIndicator() != null) {
                this.mWorkspace.getPageIndicator().setVisibility(visibility);
            }
        }
        Hotseat hotseat = this.mHotseat;
        if (hotseat != null) {
            hotseat.setVisibility(visibility);
        }
    }

    public void setAllAppsAlphaAndVisibility(int visibility) {
        float f = visibility == 0 ? 1.0f : 0.0f;
        AllAppsHost allAppsHost = this.mAppsCustomizeHost;
        if (allAppsHost != null) {
            allAppsHost.setAlpha(f);
            this.mAppsCustomizeHost.setVisibility(visibility);
            if (this.mAppsCustomizeHost.getContentView() != null) {
                this.mAppsCustomizeHost.getContentView().setAlpha(f);
                this.mAppsCustomizeHost.getContentView().setVisibility(visibility);
            }
            if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
                if (this.mAppsCustomizeHost.getSearchView() != null) {
                    this.mAppsCustomizeHost.getSearchView().setAlpha(f);
                    this.mAppsCustomizeHost.getSearchView().setVisibility(visibility);
                }
                if (this.mAppsCustomizeHost.getAppContainerView() != null) {
                    this.mAppsCustomizeHost.getAppContainerView().setAlpha(f);
                    this.mAppsCustomizeHost.getAppContainerView().setVisibility(visibility);
                }
            }
        }
    }

    public void setHomeVisibility(int visibility) {
        setHomeVisibility(visibility, true);
    }

    public void setHomeVisibility(int visibility, boolean setAllAppsVisibility) {
        if (isInState(LauncherState.ALL_APPS)) {
            if (setAllAppsVisibility) {
                setAllAppsAlphaAndVisibility(visibility);
                return;
            }
            return;
        }
        this.mWorkspace.setVisibility(visibility);
        this.mPageIndicators.setVisibility(visibility);
        this.mHotseat.setVisibility(visibility);
        if (this.mWorkspace.getState() == Workspace.State.SPRING_LOADED) {
            this.mSearchDropTargetBar.setVisibility(visibility);
        } else if (this.mWorkspace.isInOverviewMode()) {
            if (!com.lge.launcher3.util.Utilities.isLGUI7_1()) {
                this.mDefaultPageButton.setVisibility(visibility);
            }
            this.mLGOverviewPanel.setVisibility(visibility);
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public void notifyAppFlashStatus(boolean isEnabled) {
        Workspace workspace = this.mWorkspace;
        if (workspace != null) {
            workspace.resetAppFlashPageIndicator(isEnabled);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 0) {
            this.dispatchTouchEventDown = true;
        } else if (action == 1 || action == 3) {
            this.dispatchTouchEventDown = false;
        }
        if (this.mIsWorkspaceLongPressed && (ev.getAction() == 1 || ev.getAction() == 3)) {
            cancelWorkspaceLongpress();
        }
        return super.dispatchTouchEvent(ev);
    }

    private ActivityOptionsWrapper makeActivityAnimation(View v) {
        int iWidth;
        int iWidth2;
        int paddingTop;
        Drawable textViewIcon;
        if (sCustomScaleUpMethod == null) {
            return null;
        }
        int measuredWidth = v.getMeasuredWidth();
        int measuredHeight = v.getMeasuredHeight();
        boolean z = false;
        if (!(v instanceof TextView) || (textViewIcon = Workspace.getTextViewIcon((TextView) v)) == null) {
            iWidth = measuredWidth;
            iWidth2 = 0;
            paddingTop = 0;
        } else {
            Rect bounds = textViewIcon.getBounds();
            iWidth2 = (measuredWidth - bounds.width()) / 2;
            paddingTop = v.getPaddingTop();
            iWidth = bounds.width();
            measuredHeight = bounds.height();
        }
        try {
            Method method = sCustomScaleUpMethod;
            Object[] objArr = new Object[6];
            objArr[0] = v;
            objArr[1] = Integer.valueOf(iWidth2);
            objArr[2] = Integer.valueOf(paddingTop);
            objArr[3] = Integer.valueOf(iWidth);
            objArr[4] = Integer.valueOf(measuredHeight);
            if (this.mWorkspace.getOpenFolder() == null && !isInState(LauncherState.ALL_APPS)) {
                z = true;
            }
            objArr[5] = Boolean.valueOf(z);
            return (ActivityOptionsWrapper) method.invoke(null, objArr);
        } catch (IllegalAccessException e) {
            Log.d("Launcher", "Could not call makeCustomScaleUpAnimation: " + e);
            sCustomScaleUpMethod = null;
            return null;
        } catch (InvocationTargetException e2) {
            Log.d("Launcher", "Could not call makeCustomScaleUpAnimation: " + e2);
            sCustomScaleUpMethod = null;
            return null;
        }
    }

    public void cancelWorkspaceLongpress() {
        if (LGHomeFeature.Config.FEATURE_EDITMODE_LONGPRESS_DELAY.getValue() && this.mIsWorkspaceLongPressed) {
            this.mHandler.removeMessages(1000);
            this.mIsWorkspaceLongPressed = false;
        }
    }

    private void removeFolderItemWhileLoading(ItemInfo item) {
        Iterator<ShortcutInfo> it = ((FolderInfo) item).contents.iterator();
        while (it.hasNext()) {
            ShortcutInfo next = it.next();
            if (next != null && next.isRemoved) {
                LGLog.i("Launcher", "remove item in folder because it was removed item in BgDataModel - " + next + ", " + next.hashCode());
                it.remove();
            }
        }
    }

    public void setBlurBGPivotX(float pivotX) {
        this.mBlurBGPivotX = pivotX;
    }

    public void setBlurBGPivotY(float pivotY) {
        this.mBlurBGPivotY = pivotY;
    }

    public float getBlurBGPivotX() {
        return this.mBlurBGPivotX;
    }

    public float getBlurBGPivotY() {
        return this.mBlurBGPivotY;
    }

    public RotationHelper getRotationHelper() {
        return this.mRotationHelper;
    }

    @Override // com.android.launcher3.statemanager.StatefulActivity
    public StateManager<LauncherState> getStateManager() {
        return this.mStateManager;
    }

    /* JADX DEBUG: Method merged with bridge method: isInState(Lcom/android/launcher3/statemanager/BaseState;)Z */
    @Override // com.android.launcher3.statemanager.StatefulActivity
    public boolean isInState(LauncherState state) {
        return this.mStateManager.getState() == state;
    }

    public AllAppsTransitionController getAllAppsController() {
        return this.mAllAppsController;
    }

    public static <T extends Launcher> T cast(ActivityContext activityContext) {
        return (T) activityContext;
    }

    public void folderBackgroundAnim(final boolean open) {
        float f;
        float f2;
        float measuredHeight;
        AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        float measuredHeight2 = ((this.mWorkspace.getMeasuredHeight() + this.mPageIndicators.getMeasuredHeight()) + this.mHotseat.getMeasuredHeight()) / 2;
        float measuredHeight3 = (measuredHeight2 - ((this.mHotseat.getMeasuredHeight() / 2) - this.mHotseat.getPivotY())) * (-0.075f);
        int integer = getResources().getInteger(R.integer.config_folderShrinkDuration);
        animatorSetCreateAnimatorSet.setInterpolator(new LogDecelerateInterpolator(70, 0));
        float f3 = 0.7f;
        float f4 = 1.0f;
        float f5 = 0.0f;
        if (open) {
            measuredHeight = (measuredHeight2 - ((this.mWorkspace.getMeasuredHeight() / 2) - this.mWorkspace.getPivotY())) * 0.05f;
            float measuredHeight4 = (measuredHeight2 - ((this.mPageIndicators.getMeasuredHeight() / 2) - this.mPageIndicators.getPivotY())) * (-0.075f);
            float measuredHeight5 = (measuredHeight2 - ((this.mHotseat.getMeasuredHeight() / 2) - this.mHotseat.getPivotY())) * (-0.075f);
            animatorSetCreateAnimatorSet.setInterpolator(new LogDecelerateInterpolator(100, 0));
            f2 = measuredHeight5;
            f = measuredHeight4;
            measuredHeight3 = 0.0f;
            f4 = 0.7f;
            f3 = 1.0f;
        } else {
            f = 0.0f;
            f2 = 0.0f;
            measuredHeight = 0.0f;
            f5 = 1.0f;
        }
        animatorSetCreateAnimatorSet.playTogether(LauncherAnimUtils.ofPropertyValuesHolder(this.mFolderAnimUseCellLayout, PropertyValuesHolder.ofFloat("alpha", f5), PropertyValuesHolder.ofFloat("scaleX", f3, f4), PropertyValuesHolder.ofFloat("scaleY", f3, f4), PropertyValuesHolder.ofFloat("translationY", measuredHeight)), LauncherAnimUtils.ofPropertyValuesHolder(this.mPageIndicators, PropertyValuesHolder.ofFloat("alpha", f5), PropertyValuesHolder.ofFloat("scaleX", f3, f4), PropertyValuesHolder.ofFloat("scaleY", f3, f4), PropertyValuesHolder.ofFloat("translationY", f)), LauncherAnimUtils.ofPropertyValuesHolder(this.mHotseat, PropertyValuesHolder.ofFloat("alpha", f5), PropertyValuesHolder.ofFloat("scaleX", f3, f4), PropertyValuesHolder.ofFloat("scaleX", f3, f4), PropertyValuesHolder.ofFloat("translationY", measuredHeight3, f2)));
        animatorSetCreateAnimatorSet.setDuration(integer);
        animatorSetCreateAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.Launcher.48
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                String str;
                super.onAnimationEnd(animation);
                LGLog.i("Launcher", "Folder: " + (open ? "Open" : "Close") + "Folder Background Animation End");
                if (Launcher.this.mWorkspace == null || Launcher.this.mFolderAnimUseCellLayout == null) {
                    str = "null";
                } else {
                    str = Launcher.this.mWorkspace.indexOfChild(Launcher.this.mFolderAnimUseCellLayout) + " page in workspace";
                }
                LGLog.i("Launcher", "Folder: use " + str);
                if (open) {
                    return;
                }
                if (Launcher.this.mFolderAnimUseCellLayout != null) {
                    Launcher.this.mFolderAnimUseCellLayout.setAlpha(1.0f);
                }
                Launcher.this.mPageIndicators.setAlpha(1.0f);
                Launcher.this.mHotseat.setAlpha(1.0f);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                String str;
                super.onAnimationCancel(animation);
                LGLog.i("Launcher", "Folder: " + (open ? "Open" : "Close") + "Folder Background Animation Cancel");
                if (Launcher.this.mWorkspace == null || Launcher.this.mFolderAnimUseCellLayout == null) {
                    str = "null";
                } else {
                    str = Launcher.this.mWorkspace.indexOfChild(Launcher.this.mFolderAnimUseCellLayout) + " page in workspace";
                }
                LGLog.i("Launcher", "Folder: use " + str);
                if (open) {
                    return;
                }
                if (Launcher.this.mFolderAnimUseCellLayout != null) {
                    Launcher.this.mFolderAnimUseCellLayout.setAlpha(1.0f);
                }
                Launcher.this.mPageIndicators.setAlpha(1.0f);
                Launcher.this.mHotseat.setAlpha(1.0f);
            }
        });
        animatorSetCreateAnimatorSet.start();
    }

    public void allappFolderBackgroundAnim(final boolean mode) {
        float f;
        AnimatorSet animatorSet = new AnimatorSet();
        int integer = getResources().getInteger(R.integer.config_folderShrinkDuration);
        float f2 = 0.7f;
        float f3 = 0.0f;
        float f4 = 1.0f;
        if (mode) {
            f = 0.0f;
            f3 = 1.0f;
            f4 = 0.7f;
            f2 = 1.0f;
        } else {
            f = 1.0f;
        }
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.mAppsCustomizeHost, "alpha", f3, f), ObjectAnimator.ofFloat(this.mAppsCustomizeHost, "scaleX", f2, f4), ObjectAnimator.ofFloat(this.mAppsCustomizeHost, "scaleY", f2, f4));
        animatorSet.setDuration(integer);
        animatorSet.setInterpolator(new LogDecelerateInterpolator(100, 0));
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.Launcher.49
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (Launcher.this.mAppsCustomizeHost != null) {
                    if (mode) {
                        Launcher.this.setAllAppsAlphaAndVisibility(4);
                    } else if (Launcher.this.isInState(LauncherState.ALL_APPS)) {
                        Launcher.this.setAllAppsAlphaAndVisibility(0);
                    }
                }
            }
        });
        animatorSet.start();
    }

    public void editFolderBackgroundAnim(final boolean mode, boolean animate) {
        float measuredHeight;
        float f;
        Launcher launcher;
        this.mPageIndicators.setVisibility(0);
        float measuredHeight2 = ((this.mWorkspace.getMeasuredHeight() + this.mPageIndicators.getMeasuredHeight()) + this.mLGOverviewPanel.getMeasuredHeight()) / 2;
        float measuredHeight3 = (measuredHeight2 - ((this.mPageIndicators.getMeasuredHeight() / 2) - this.mPageIndicators.getPivotY())) * (-0.045f);
        int integer = getResources().getInteger(R.integer.config_folderShrinkDuration);
        ArrayList arrayList = new ArrayList();
        for (int currentPage = this.mWorkspace.getCurrentPage() - 1; currentPage < this.mWorkspace.getCurrentPage() + 2; currentPage++) {
            if (currentPage >= 0 && currentPage < this.mWorkspace.getPageCount()) {
                arrayList.add(this.mWorkspace.getDropLayout(currentPage));
            }
        }
        float f2 = 0.0f;
        float f3 = 0.85f;
        if (mode) {
            measuredHeight = (measuredHeight2 - ((this.mLGOverviewPanel.getMeasuredHeight() / 2) - this.mLGOverviewPanel.getPivotY())) * (-0.06f);
            measuredHeight3 = (-0.1f) * (measuredHeight2 - ((this.mPageIndicators.getMeasuredHeight() / 2) - this.mPageIndicators.getPivotY()));
            f = 0.85f;
            f3 = 1.0f;
        } else {
            measuredHeight = 0.0f;
            f = 1.0f;
            f2 = 1.0f;
        }
        AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        for (int i = 0; i < arrayList.size(); i++) {
            animatorSetCreateAnimatorSet.playTogether(LauncherAnimUtils.ofPropertyValuesHolder((View) arrayList.get(i), PropertyValuesHolder.ofFloat("alpha", f2), PropertyValuesHolder.ofFloat("scaleX", f3, f), PropertyValuesHolder.ofFloat("scaleY", f3, f)));
        }
        animatorSetCreateAnimatorSet.playTogether(LauncherAnimUtils.ofPropertyValuesHolder(this.mLGOverviewPanel, PropertyValuesHolder.ofFloat("alpha", f2), PropertyValuesHolder.ofFloat("scaleX", f3, f), PropertyValuesHolder.ofFloat("scaleY", f3, f), PropertyValuesHolder.ofFloat("translationY", measuredHeight)));
        if (animate) {
            launcher = this;
            animatorSetCreateAnimatorSet.playTogether(LauncherAnimUtils.ofPropertyValuesHolder(launcher.mPageIndicators, PropertyValuesHolder.ofFloat("alpha", f2), PropertyValuesHolder.ofFloat("scaleX", f3, f), PropertyValuesHolder.ofFloat("scaleY", f3, f), PropertyValuesHolder.ofFloat("translationY", measuredHeight3)));
        } else {
            launcher = this;
            launcher.mPageIndicators.setAlpha(1.0f);
            launcher.mPageIndicators.setScaleX(1.0f);
            launcher.mPageIndicators.setScaleY(1.0f);
        }
        animatorSetCreateAnimatorSet.setDuration(integer);
        animatorSetCreateAnimatorSet.setInterpolator(new LogDecelerateInterpolator(100, 0));
        animatorSetCreateAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.Launcher.50
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                if (Launcher.this.mPageIndicators != null && mode) {
                    Launcher.this.mPageIndicators.setVisibility(4);
                } else {
                    Launcher.this.mPageIndicators.setVisibility(0);
                }
            }
        });
        animatorSetCreateAnimatorSet.start();
    }

    private void closeFolderIconToImage(FolderIcon fi) {
        BaseDragLayer.LayoutParams layoutParams;
        boolean z;
        if (this.mWorkspace.isInOverviewMode()) {
            Workspace workspace = this.mWorkspace;
            workspace.getDropLayout(workspace.getCurrentPage()).setScaleX(1.0f);
            Workspace workspace2 = this.mWorkspace;
            workspace2.getDropLayout(workspace2.getCurrentPage()).setScaleY(1.0f);
        } else if (isInState(LauncherState.ALL_APPS)) {
            this.mAppsCustomizeHost.setScaleX(1.0f);
            this.mAppsCustomizeHost.setScaleY(1.0f);
        } else {
            this.mFolderAnimUseCellLayout.setScaleY(1.0f);
            this.mFolderAnimUseCellLayout.setScaleX(1.0f);
            this.mFolderAnimUseCellLayout.setTranslationY(0.0f);
            this.mHotseat.setScaleX(1.0f);
            this.mHotseat.setScaleY(1.0f);
            this.mHotseat.setTranslationY(0.0f);
        }
        int measuredWidth = fi.getMeasuredWidth();
        int measuredHeight = fi.getMeasuredHeight();
        if (this.mFolderIconImageView == null) {
            this.mFolderIconImageView = new ImageView(this);
        }
        Bitmap bitmap = this.mFolderIconBitmap;
        if (bitmap == null || bitmap.getWidth() != measuredWidth || this.mFolderIconBitmap.getHeight() != measuredHeight) {
            this.mFolderIconBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            this.mFolderIconCanvas = new Canvas(this.mFolderIconBitmap);
        }
        if (this.mFolderIconImageView.getLayoutParams() instanceof BaseDragLayer.LayoutParams) {
            layoutParams = (BaseDragLayer.LayoutParams) this.mFolderIconImageView.getLayoutParams();
            z = true;
        } else {
            layoutParams = new BaseDragLayer.LayoutParams(measuredWidth, measuredHeight);
            z = false;
        }
        float descendantRectRelativeToSelf = this.mDragLayer.getDescendantRectRelativeToSelf(fi, this.mRectForFolderAnimation);
        layoutParams.customPosition = true;
        layoutParams.x = this.mRectForFolderAnimation.left;
        layoutParams.y = z ? layoutParams.y : this.mRectForFolderAnimation.top;
        layoutParams.width = (int) (measuredWidth * descendantRectRelativeToSelf);
        layoutParams.height = (int) (descendantRectRelativeToSelf * measuredHeight);
        this.mFolderIconCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        fi.draw(this.mFolderIconCanvas);
        this.mFolderIconImageView.setImageBitmap(this.mFolderIconBitmap);
        if (fi.getFolder() != null) {
            this.mFolderIconImageView.setPivotX(fi.getFolder().getPivotXForIconAnimation());
            this.mFolderIconImageView.setPivotY(fi.getFolder().getPivotYForIconAnimation());
        }
        if (this.mDragLayer.indexOfChild(this.mFolderIconImageView) != -1) {
            this.mDragLayer.removeView(this.mFolderIconImageView);
        }
        this.mDragLayer.addView(this.mFolderIconImageView, layoutParams);
        if (fi.getFolder() != null) {
            fi.getFolder().bringToFront();
        }
    }

    public void allAppsReset() {
        this.mAppsCustomizeHost.setAlpha(1.0f);
        this.mAppsCustomizeHost.setScaleX(1.0f);
        this.mAppsCustomizeHost.setScaleY(1.0f);
    }

    public FolderIcon findFolderIcon(final long folderIconId) {
        Workspace workspace = this.mWorkspace;
        if (workspace != null) {
            return (FolderIcon) workspace.getHomescreenIconByItemId(folderIconId);
        }
        return null;
    }

    public void setOneHandOperation(boolean value, String blockTag) {
        LGLog.d("Launcher", String.format("setOneHandOperation : %s, %s", Boolean.valueOf(value), blockTag));
        OneHandOperationManager oneHandOperationManager = this.mOneHandOperationManager;
        if (oneHandOperationManager != null) {
            if (value) {
                oneHandOperationManager.acquireBlock(blockTag);
            } else {
                oneHandOperationManager.releaseBlock(blockTag);
            }
        }
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public boolean isLoadedSwivelHome() {
        return getSharedPrefs().getBoolean("swivel_db_init", false);
    }

    public void blockInstallQueueSwivel() {
        InstallShortcutReceiver.blockInstallQueueSwivel();
    }

    public void unblockAndFlushInstallQueueSwivel() {
        InstallShortcutReceiver.unblockAndFlushInstallQueueSwivel(this);
    }

    @Override // com.android.launcher3.LauncherModel.Callbacks
    public boolean resetSwivelHome() {
        LGLog.i("Launcher", "resetSwivelHome()");
        CarouselLayout carouselLayout = this.mCarouselLayout;
        if (carouselLayout != null && carouselLayout.getAdapter() != null) {
            this.mCarouselLayout.getAdapter().onClearList();
        } else {
            LauncherModel.clearDatabaseSwivel(this);
        }
        getApplicationContext().getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).edit().putBoolean("EMPTY_SWIVEL_DATABASE_CREATED", true).apply();
        LauncherCallbacks launcherCallbacks = this.mLauncherCallbacks;
        if (launcherCallbacks != null) {
            launcherCallbacks.resetSwivelItemInitialized();
        } else {
            LauncherSettings.Settings.callSwivel(getContentResolver(), LauncherSettings.Settings.METHOD_LOAD_DEFAULT_SWIVEL_FAVORITES);
        }
        return true;
    }

    public void setIsPinItemDragging(boolean dragging) {
        LGLog.i("Launcher", "setIsPinItemDragging() dragging = " + dragging);
        this.mIsPinItemDragging = dragging;
    }

    public boolean getIsPinItemDragging() {
        return this.mIsPinItemDragging;
    }
}
