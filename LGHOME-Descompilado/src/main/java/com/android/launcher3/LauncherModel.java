package com.android.launcher3;

import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.os.TransactionTooLargeException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pair;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.allapps.AllAppsList;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.ExtendedModelTask;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.WidgetsModel;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.CursorIconInfo;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.ManagedProfileHeuristic;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.Provider;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.lge.launcher3.R;
import com.lge.launcher3.ScreenZoomChangeWatcher;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.config.QMemoPanelConst;
import com.lge.launcher3.hideapps.HideAppItem;
import com.lge.launcher3.homesettings.SettingsSearchUtils;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.silentota.SilentAppInfo;
import com.lge.launcher3.silentota.SilentOTA;
import com.lge.launcher3.silentota.SilentOTASwivel;
import com.lge.launcher3.util.AppNameComparator;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.UserUtils;
import com.lge.launcher3.widgettray.WidgetsModelExtension;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.LongCompanionObject;

/* JADX INFO: loaded from: classes.dex */
public class LauncherModel extends BroadcastReceiver implements LauncherAppsCompat.OnAppsChangedCallbackCompat {
    public static final Comparator<ItemInfo> COMPARATOR;
    private static final boolean DEBUG_RECEIVER = false;
    private static final int DUAL_APP_PROFILE = 97;
    private static final long INVALID_SCREEN_ID = -1;
    private static final int ITEMS_CHUNK = 6;
    public static final int LOADER_FLAG_CLEAR_WORKSPACE = 1;
    public static final int LOADER_FLAG_MIGRATE_SHORTCUTS = 2;
    public static final int LOADER_FLAG_NONE = 0;
    public static final int LOADER_FLAG_NOTIFY_FINISHED = 8;
    public static final int LOADER_FLAG_NOTIFY_LANDSCAPE = 16;
    public static final int LOADER_FLAG_NOTIFY_SWIVEL = 64;
    public static final int LOADER_FLAG_REBIND = 32;
    public static final int LOADER_FLAG_RECHECK_UPDATECENTER_OPTIONALS = 16;
    public static final int LOADER_FLAG_SET_DEFAULT_SCREEN = 4;
    private static final String MIGRATE_AUTHORITY = "com.android.launcher2.settings";
    private static final boolean REMOVE_UNRESTORED_ICONS = true;
    static final String TAG = "Launcher.Model";
    static final ArrayList<Runnable> mBindCompleteRunnables;
    static final ArrayList<Runnable> mDeferredBindRunnables;
    public static final BgDataModel sBgDataModel;
    public static HashMap<ComponentKey, LauncherAppWidgetProviderInfo> sBgWidgetProviders;
    private static HashMap<String, String> sHomePreferencesCache;
    public static final HashMap<UserHandle, HashSet<String>> sPendingPackages;
    static final Handler sWorker;
    static final HandlerThread sWorkerThread;
    boolean mAllAppsLoaded;
    final LauncherAppState mApp;
    private AppFilter mAppFilter;
    AllAppsList mBgAllAppsList;
    WidgetsModel mBgWidgetsModel;
    WeakReference<Callbacks> mCallbacks;
    private DeepShortcutManager mDeepShortcutManager;
    private boolean mDeepShortcutsLoaded;
    boolean mHasLoaderCompletedOnce;
    private boolean mHasShortcutHostPermission;
    IconCache mIconCache;
    boolean mIsLoaderTaskRunning;
    final LauncherAppsCompat mLauncherApps;
    private LiveIconManager mLiveIconManager;
    LoaderTask mLoaderTask;
    private boolean mModelLoaded;
    private final boolean mOldContentProviderExists;
    final UserManagerCompat mUserManager;
    private Handler mWorkerHandler;
    boolean mWorkspaceLoaded;
    public static boolean DEBUG_LOADERS = LGFeatureConfig.sDebugLauncherModel;
    public static boolean isInSprintID = false;
    private final MainThreadExecutor mUiExecutor = new MainThreadExecutor();
    final Object mLock = new Object();
    DeferredHandler mHandler = new DeferredHandler();
    private final MultiHashMap<ComponentKey, String> mBgDeepShortcutMap = new MultiHashMap<>();
    private boolean mRestored = false;
    private int mPreferredIndex = -1;
    private final Runnable mShortcutPermissionCheckRunnable = new Runnable() { // from class: com.android.launcher3.LauncherModel.34
        @Override // java.lang.Runnable
        public void run() {
            if (!LauncherModel.this.mDeepShortcutsLoaded || LauncherModel.this.mDeepShortcutManager.hasHostPermission() == LauncherModel.this.mHasShortcutHostPermission) {
                return;
            }
            LauncherModel.this.mApp.reloadWorkspace();
        }
    };

    public interface CallbackTask {
        void execute(Callbacks callbacks);
    }

    public interface Callbacks {
        void addAppsOnSwivelHome(ArrayList<? extends ItemInfo> apps);

        void addWidgetInNewPage(Intent intent);

        void bindAddScreens(ArrayList<Long> orderedScreenIds);

        void bindAllApplications(ArrayList<AppInfo> apps);

        void bindAllPackages(WidgetsModel model);

        void bindAppInfosRemoved(ArrayList<AppInfo> appInfos);

        void bindAppWidget(LauncherAppWidgetInfo info);

        void bindAppsAdded(ArrayList<Long> newScreens, ArrayList<ItemInfo> addNotAnimated, ArrayList<ItemInfo> addAnimated, ArrayList<AppInfo> addedApps, int op);

        void bindAppsMoved(ArrayList<ItemInfo> removed, FolderInfo target);

        void bindAppsUpdated(ArrayList<AppInfo> apps);

        void bindDeepShortcutMap(MultiHashMap<ComponentKey, String> deepShortcutMap);

        void bindFolders(LongArrayMap<FolderInfo> folders);

        void bindItems(ArrayList<ItemInfo> shortcuts, int start, int end, boolean forceAnimateIcons);

        void bindRestoreItemsChange(HashSet<ItemInfo> updates);

        void bindScreens(ArrayList<Long> orderedScreenIds);

        void bindSearchablesChanged();

        void bindShortcutsChanged(ArrayList<ShortcutInfo> updated, ArrayList<ShortcutInfo> removed, UserHandle user);

        void bindSilentAppsInFolderUpdated(FolderInfo folderInfo, ShortcutInfo itemInfo);

        void bindWidgetsRestored(ArrayList<LauncherAppWidgetInfo> widgets);

        void bindWorkspaceComponentsRemoved(HashSet<String> packageNames, HashSet<ComponentName> components, UserHandle user);

        void clearPendingBinds();

        void dumpLogsToLocalData();

        void executeOnNextDraw(ViewOnDrawExecutor executor);

        void exitCleanViewMode();

        void finishBindingHotSeats();

        void finishBindingItems();

        void finishFirstPageBind(ViewOnDrawExecutor executor);

        int getCurrentWorkspaceScreen();

        void invalidateHasCustomContentToLeft();

        boolean isAllAppsButtonRank(int rank);

        boolean isLoadedSwivelHome();

        void notifyAppFlashStatus(boolean isEnabled);

        void notifyGoogleNowStatus(boolean isEnabled);

        void notifyManagedProfileStatus(boolean isAdded);

        void onPageBoundSynchronously(int page);

        void rebindModel();

        void removeWorkspaceEmptyScreen();

        boolean resetSwivelHome();

        boolean setLoadOnResume();

        void startBinding(int loadFlags);

        void updateStringCache();

        void updateWorkProfileComponent();
    }

    public interface ItemInfoFilter {
        boolean filterItem(ItemInfo parent, ItemInfo info, ComponentName cn);
    }

    public interface ModelUpdateTask extends Runnable {
        void init(LauncherAppState app, LauncherModel model, BgDataModel dataModel, AllAppsList allAppsList, Executor uiExecutor);
    }

    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.launcher3.LauncherModel.LoaderTask.loadWorkspace():void] */
    /* JADX INFO: renamed from: -$$Nest$fgetmAppFilter, reason: not valid java name */
    static /* synthetic */ AppFilter m46$$Nest$fgetmAppFilter(LauncherModel launcherModel) {
        return launcherModel.mAppFilter;
    }

    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.launcher3.LauncherModel.LoaderTask.loadWorkspace():void] */
    /* JADX INFO: renamed from: -$$Nest$fgetmDeepShortcutManager, reason: not valid java name */
    static /* synthetic */ DeepShortcutManager m48$$Nest$fgetmDeepShortcutManager(LauncherModel launcherModel) {
        return launcherModel.mDeepShortcutManager;
    }

    /* JADX DEBUG: Incorrect args count in method signature: (Landroid/content/Context;Ljava/util/ArrayList<Ljava/lang/Long;>;)V */
    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.launcher3.LauncherModel.LoaderTask.loadWorkspace():void] */
    /* JADX INFO: renamed from: -$$Nest$madjustDefaultScreen, reason: not valid java name */
    static /* bridge */ /* synthetic */ void m56$$Nest$madjustDefaultScreen(LauncherModel launcherModel, Context context, ArrayList arrayList) {
        launcherModel.adjustDefaultScreen(context, arrayList);
    }

    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.launcher3.LauncherModel.LoaderTask.loadWorkspace():void] */
    /* JADX INFO: renamed from: -$$Nest$mrestoreInvalidFolder, reason: not valid java name */
    static /* bridge */ /* synthetic */ void m59$$Nest$mrestoreInvalidFolder(LauncherModel launcherModel, Context context, InvariantDeviceProfile invariantDeviceProfile) {
        launcherModel.restoreInvalidFolder(context, invariantDeviceProfile);
    }

    static int getCellLayoutChildId(long container, long screen, int localCellX, int localCellY, int spanX, int spanY) {
        return ((((int) container) & 255) << 24) | ((((int) screen) & 255) << 16) | ((localCellX & 255) << 8) | (localCellY & 255);
    }

    static {
        HandlerThread handlerThread = new HandlerThread("launcher-loader");
        sWorkerThread = handlerThread;
        handlerThread.start();
        sWorker = new Handler(handlerThread.getLooper());
        mDeferredBindRunnables = new ArrayList<>();
        mBindCompleteRunnables = new ArrayList<>();
        sPendingPackages = new HashMap<>();
        sBgDataModel = new BgDataModel();
        COMPARATOR = new Comparator<ItemInfo>() { // from class: com.android.launcher3.LauncherModel.28
            /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
            @Override // java.util.Comparator
            public final int compare(ItemInfo a, ItemInfo b) {
                CharSequence charSequence = a.title;
                CharSequence charSequence2 = b.title;
                if (charSequence == null && charSequence2 == null) {
                    return 0;
                }
                if (charSequence == null) {
                    return -1;
                }
                if (charSequence2 == null) {
                    return 1;
                }
                return AppNameComparator.compare(charSequence.toString(), charSequence2.toString());
            }
        };
        sHomePreferencesCache = new HashMap<>();
    }

    public boolean isModelLoaded() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mModelLoaded && this.mLoaderTask == null;
        }
        return z;
    }

    public WidgetsModel getBgWidgetsModel() {
        return this.mBgWidgetsModel;
    }

    LauncherModel(LauncherAppState app, IconCache iconCache, AppFilter appFilter, DeepShortcutManager deepShortcutManager) {
        boolean z = false;
        Context context = app.getContext();
        String string = context.getString(R.string.old_launcher_provider_uri);
        String authority = Uri.parse(string).getAuthority();
        ProviderInfo providerInfoResolveContentProvider = context.getPackageManager().resolveContentProvider(MIGRATE_AUTHORITY, 0);
        ProviderInfo providerInfoResolveContentProvider2 = context.getPackageManager().resolveContentProvider(authority, 0);
        Log.d(TAG, "Old launcher provider: " + string);
        if (providerInfoResolveContentProvider != null && providerInfoResolveContentProvider2 != null) {
            z = true;
        }
        this.mOldContentProviderExists = z;
        if (z) {
            Log.d(TAG, "Old launcher provider exists.");
        } else {
            Log.d(TAG, "Old launcher provider does not exist.");
        }
        this.mApp = app;
        this.mBgAllAppsList = new AllAppsList(iconCache, appFilter);
        this.mBgWidgetsModel = new WidgetsModelExtension(context, iconCache, appFilter);
        this.mIconCache = iconCache;
        this.mLauncherApps = LauncherAppsCompat.getInstance(context);
        this.mUserManager = UserManagerCompat.getInstance(context);
        this.mAppFilter = appFilter;
        this.mLiveIconManager = LiveIconManager.getInstance(context);
        this.mWorkerHandler = new Handler(getWorkerLooper());
        this.mDeepShortcutManager = deepShortcutManager;
        context.getContentResolver().registerContentObserver(LauncherSettings.Favorites.CONTENT_URI_EXTERNAL_ADD, true, new ContentObserver(new Handler()) { // from class: com.android.launcher3.LauncherModel.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                Log.d(LauncherModel.TAG, "CONTENT_URI_EXTERNAL_ADD changed");
                LauncherModel.isInSprintID = false;
            }
        });
    }

    void runOnMainThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            this.mHandler.post(r);
        } else {
            r.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void runOnWorkerThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
    }

    void runAfterBindCompletes(Runnable r) {
        if (isLoadingWorkspace() || !this.mHasLoaderCompletedOnce) {
            ArrayList<Runnable> arrayList = mBindCompleteRunnables;
            synchronized (arrayList) {
                arrayList.add(r);
            }
            return;
        }
        runOnWorkerThread(r);
    }

    boolean canMigrateFromOldLauncherDb(Launcher launcher) {
        return this.mOldContentProviderExists && !launcher.isLauncherPreinstalled();
    }

    public void setPackageState(final PackageInstallerCompat.PackageInstallInfo installInfo) {
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (LauncherModel.sBgDataModel) {
                    final HashSet hashSet = new HashSet();
                    if (installInfo.state == 0) {
                        return;
                    }
                    for (ItemInfo itemInfo : LauncherModel.sBgDataModel.itemsIdMap) {
                        if (itemInfo instanceof ShortcutInfo) {
                            ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                            ComponentName targetComponent = shortcutInfo.getTargetComponent();
                            if (shortcutInfo.isPromise() && targetComponent != null && installInfo.packageName != null && installInfo.packageName.equals(targetComponent.getPackageName())) {
                                shortcutInfo.setInstallProgress(installInfo.progress);
                                if (installInfo.state == 2) {
                                    shortcutInfo.status &= -5;
                                }
                                hashSet.add(shortcutInfo);
                            }
                        }
                    }
                    for (LauncherAppWidgetInfo launcherAppWidgetInfo : LauncherModel.sBgDataModel.appWidgets) {
                        if (launcherAppWidgetInfo.providerName.getPackageName().equals(installInfo.packageName)) {
                            launcherAppWidgetInfo.installProgress = installInfo.progress;
                            hashSet.add(launcherAppWidgetInfo);
                        }
                    }
                    if (!hashSet.isEmpty()) {
                        LauncherModel.this.mHandler.post(new Runnable() { // from class: com.android.launcher3.LauncherModel.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                Callbacks callback = LauncherModel.this.getCallback();
                                if (callback != null) {
                                    callback.bindRestoreItemsChange(hashSet);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void updateSessionDisplayInfo(final String packageName) {
        if (packageName == null) {
            LGLog.i(TAG, "updateSessionDisplayInfo(): packageName is null");
        } else {
            runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.3
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (LauncherModel.sBgDataModel) {
                        final ArrayList arrayList = new ArrayList();
                        final UserHandle userHandleMyUserHandle = Process.myUserHandle();
                        for (ItemInfo itemInfo : LauncherModel.sBgDataModel.itemsIdMap) {
                            if (itemInfo instanceof ShortcutInfo) {
                                ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                                ComponentName targetComponent = shortcutInfo.getTargetComponent();
                                if (shortcutInfo.isPromise() && targetComponent != null && targetComponent.getPackageName() != null && packageName.equals(targetComponent.getPackageName())) {
                                    if (shortcutInfo.hasStatusFlag(258)) {
                                        LauncherModel.this.mIconCache.getTitleAndIcon(shortcutInfo, shortcutInfo.promisedIntent, userHandleMyUserHandle, shortcutInfo.shouldUseLowResIcon());
                                    } else {
                                        shortcutInfo.updateIcon(LauncherModel.this.mIconCache);
                                    }
                                    arrayList.add(shortcutInfo);
                                }
                            }
                        }
                        if (!arrayList.isEmpty()) {
                            LauncherModel.this.mHandler.post(new Runnable() { // from class: com.android.launcher3.LauncherModel.3.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    Callbacks callback = LauncherModel.this.getCallback();
                                    if (callback != null) {
                                        callback.bindShortcutsChanged(arrayList, new ArrayList<>(), userHandleMyUserHandle);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public void addAppsToAllApps(final Context ctx, final ArrayList<AppInfo> allAppsApps, final int op) {
        final Callbacks callback = getCallback();
        if (allAppsApps == null) {
            throw new RuntimeException("allAppsApps must not be null");
        }
        if (allAppsApps.isEmpty()) {
            return;
        }
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.4
            @Override // java.lang.Runnable
            public void run() {
                LauncherModel.this.runOnMainThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.4.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Callbacks callback2 = LauncherModel.this.getCallback();
                        if (callback != callback2 || callback2 == null) {
                            return;
                        }
                        callback.bindAppsAdded(null, null, null, allAppsApps, op);
                    }
                });
            }
        });
    }

    private static boolean findNextAvailableIconSpaceInScreen(Context context, ArrayList<ItemInfo> occupiedPos, int[] xy, int spanX, int spanY) {
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        int i = idp.numColumns;
        int i2 = idp.numRows;
        boolean[][] zArr = (boolean[][]) Array.newInstance((Class<?>) boolean.class, i, i2);
        if (occupiedPos != null) {
            for (ItemInfo itemInfo : occupiedPos) {
                int i3 = itemInfo.cellX + itemInfo.spanX;
                int i4 = itemInfo.cellY + itemInfo.spanY;
                for (int i5 = itemInfo.cellX; i5 >= 0 && i5 < i3 && i5 < i; i5++) {
                    for (int i6 = itemInfo.cellY; i6 >= 0 && i6 < i4 && i6 < i2; i6++) {
                        zArr[i5][i6] = true;
                    }
                }
            }
        }
        return Utilities.findVacantCell(xy, spanX, spanY, i, i2, zArr);
    }

    Pair<Long, int[]> findSpaceForItem(Context context, ArrayList<Long> arrayList, ArrayList<Long> arrayList2, int i, int i2) {
        if (this.mRestored) {
            if (this.mPreferredIndex == -1) {
                this.mPreferredIndex = arrayList.size();
            }
            return findSpaceForItem(context, arrayList, arrayList2, i, i2, this.mPreferredIndex);
        }
        if (LGHomeFeature.Config.FEATURE_ADD_NEW_ITEMS_ON_LAST_PAGE.getValue()) {
            return findLastSpaceForItem(context, arrayList, arrayList2, i, i2);
        }
        LongSparseArray longSparseArray = new LongSparseArray();
        assertWorkspaceLoaded();
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            for (ItemInfo itemInfo : bgDataModel.itemsIdMap) {
                if (itemInfo.container == -100) {
                    ArrayList arrayList3 = (ArrayList) longSparseArray.get(itemInfo.screenId);
                    if (arrayList3 == null) {
                        arrayList3 = new ArrayList();
                        longSparseArray.put(itemInfo.screenId, arrayList3);
                    }
                    arrayList3.add(itemInfo);
                }
            }
        }
        long jLongValue = 0;
        int[] iArr = new int[2];
        boolean zFindNextAvailableIconSpaceInScreen = false;
        int size = arrayList.size();
        boolean z = true;
        int i3 = !arrayList.isEmpty() ? 1 : 0;
        if (i3 < size) {
            jLongValue = arrayList.get(i3).longValue();
            zFindNextAvailableIconSpaceInScreen = findNextAvailableIconSpaceInScreen(context, (ArrayList<ItemInfo>) longSparseArray.get(jLongValue), iArr, i, i2);
        }
        if (zFindNextAvailableIconSpaceInScreen) {
            z = zFindNextAvailableIconSpaceInScreen;
        } else {
            for (int i4 = 1; i4 < size; i4++) {
                jLongValue = arrayList.get(i4).longValue();
                if (findNextAvailableIconSpaceInScreen(context, (ArrayList<ItemInfo>) longSparseArray.get(jLongValue), iArr, i, i2)) {
                    break;
                }
            }
            z = zFindNextAvailableIconSpaceInScreen;
        }
        if (!z) {
            jLongValue = LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_SCREEN_ID).getLong("value");
            arrayList.add(Long.valueOf(jLongValue));
            arrayList2.add(Long.valueOf(jLongValue));
            if (!findNextAvailableIconSpaceInScreen(context, (ArrayList<ItemInfo>) longSparseArray.get(jLongValue), iArr, i, i2)) {
                throw new RuntimeException("Can't find space to add the item");
            }
        }
        return Pair.create(Long.valueOf(jLongValue), iArr);
    }

    public void addAndBindAddedWorkspaceItems(final Context context, final ArrayList<? extends ItemInfo> workspaceApps) {
        addAndBindAddedWorkspaceItems(context, workspaceApps, 0);
    }

    public void addItemsOnSwivelHome(final ArrayList<? extends ItemInfo> apps) {
        final Callbacks callback = getCallback();
        if (apps == null || apps.isEmpty()) {
            return;
        }
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.5
            @Override // java.lang.Runnable
            public void run() {
                callback.addAppsOnSwivelHome(apps);
            }
        });
    }

    public Pair<Long, int[]> findSpaceForHotSeatItem(final Context context) {
        return findSpaceForItem(context, loadWorkspaceScreensDb(context), new ArrayList<>(), 1, 1);
    }

    public void addAndBindAddedWorkspaceItems(final Context context, final ArrayList<? extends ItemInfo> workspaceApps, final int flags) {
        final Callbacks callback = getCallback();
        if (workspaceApps.isEmpty()) {
            return;
        }
        if (LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_IS_RESTORE_DB).getBoolean("value")) {
            LGLog.i(TAG, "addAndBindAddedWorkspaceItems(): skipped while restoring DB,  " + workspaceApps);
            return;
        }
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.6
            @Override // java.lang.Runnable
            public void run() {
                ArrayList<Long> arrayList;
                ArrayList<Long> arrayList2;
                long j;
                ItemInfo itemInfoMakeShortcut;
                boolean zIsOccufiedSpace;
                boolean z;
                final ArrayList arrayList3 = new ArrayList();
                final ArrayList<Long> arrayList4 = new ArrayList<>();
                ArrayList<Long> arrayListLoadWorkspaceScreensDb = LauncherModel.loadWorkspaceScreensDb(context);
                synchronized (LauncherModel.sBgDataModel) {
                    for (ItemInfo itemInfo : workspaceApps) {
                        if (!(itemInfo instanceof ShortcutInfo) || !LauncherModel.this.shortcutExists(context, itemInfo.getIntent(), itemInfo.user)) {
                            if (LGHomeFeature.isEnableDefaultHome() && (itemInfo instanceof AppInfo) && LauncherModel.this.shortcutExists(context, itemInfo.getIntent(), itemInfo.user)) {
                                LGLog.i(LauncherModel.TAG, "Shortcut exist: " + itemInfo);
                            } else {
                                Pair<Long, int[]> pairFindSpaceForItem = LauncherModel.this.findSpaceForItem(context, arrayListLoadWorkspaceScreensDb, arrayList4, 1, 1);
                                long jLongValue = ((Long) pairFindSpaceForItem.first).longValue();
                                int[] iArr = (int[]) pairFindSpaceForItem.second;
                                if ((itemInfo instanceof ShortcutInfo) || (itemInfo instanceof FolderInfo)) {
                                    arrayList2 = arrayListLoadWorkspaceScreensDb;
                                    j = jLongValue;
                                    itemInfoMakeShortcut = itemInfo;
                                    zIsOccufiedSpace = false;
                                    z = false;
                                } else if (itemInfo instanceof AppInfo) {
                                    itemInfoMakeShortcut = ((AppInfo) itemInfo).makeShortcut();
                                    boolean z2 = ((AppInfo) itemInfo).isSilentOTA;
                                    if (z2) {
                                        z = z2;
                                        arrayList2 = arrayListLoadWorkspaceScreensDb;
                                        zIsOccufiedSpace = LauncherModel.this.isOccufiedSpace(context, arrayListLoadWorkspaceScreensDb, 1, 1, (int) itemInfo.screenId, itemInfo.cellX, itemInfo.cellY);
                                        if (zIsOccufiedSpace) {
                                            jLongValue = itemInfo.screenId;
                                            iArr[0] = itemInfo.cellX;
                                            iArr[1] = itemInfo.cellY;
                                        }
                                        j = jLongValue;
                                    } else {
                                        z = z2;
                                        arrayList2 = arrayListLoadWorkspaceScreensDb;
                                        j = jLongValue;
                                        zIsOccufiedSpace = false;
                                    }
                                } else {
                                    throw new RuntimeException("Unexpected info type");
                                }
                                LauncherModel.addItemToDatabase(context, itemInfoMakeShortcut, -100L, j, iArr[0], iArr[1]);
                                if (z && zIsOccufiedSpace) {
                                    arrayList3.add(0, itemInfoMakeShortcut);
                                } else {
                                    arrayList3.add(itemInfoMakeShortcut);
                                }
                                arrayListLoadWorkspaceScreensDb = arrayList2;
                            }
                        }
                    }
                    arrayList = arrayListLoadWorkspaceScreensDb;
                }
                LauncherModel.updateWorkspaceScreenOrder(context, arrayList);
                if (arrayList3.isEmpty()) {
                    return;
                }
                LauncherModel.this.runOnMainThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.6.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Callbacks callback2 = LauncherModel.this.getCallback();
                        if (callback != callback2 || callback2 == null) {
                            return;
                        }
                        ArrayList<ItemInfo> arrayList5 = new ArrayList<>();
                        ArrayList<ItemInfo> arrayList6 = new ArrayList<>();
                        if (!arrayList3.isEmpty()) {
                            long j2 = ((ItemInfo) arrayList3.get(r0.size() - 1)).screenId;
                            for (ItemInfo itemInfo2 : arrayList3) {
                                if (itemInfo2.screenId == j2 && (flags & 4) != 4) {
                                    arrayList5.add(itemInfo2);
                                } else {
                                    arrayList6.add(itemInfo2);
                                }
                            }
                        }
                        callback.bindAppsAdded(arrayList4, arrayList6, arrayList5, null, 0);
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindItemInfosAndClearQueuedBindRunnables() {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            throw new RuntimeException("Expected unbindLauncherItemInfos() to be called from the main thread");
        }
        ArrayList<Runnable> arrayList = mDeferredBindRunnables;
        synchronized (arrayList) {
            arrayList.clear();
        }
        this.mHandler.cancelAll();
        unbindWorkspaceItemsOnMainThread();
    }

    void unbindWorkspaceItemsOnMainThread() {
        final ArrayList arrayList = new ArrayList();
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            arrayList.addAll(bgDataModel.workspaceItems);
            arrayList.addAll(bgDataModel.appWidgets);
        }
        runOnMainThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.7
            @Override // java.lang.Runnable
            public void run() {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    ((ItemInfo) it.next()).unbind();
                }
            }
        });
    }

    public static void addOrMoveItemInDatabase(Context context, ItemInfo item, long container, long screenId, int cellX, int cellY) {
        if (item.container == -1) {
            addItemToDatabase(context, item, container, screenId, cellX, cellY);
            addShortcutInFolderToDatabase(context, item);
        } else {
            moveItemInDatabase(context, item, container, screenId, cellX, cellY);
        }
    }

    static void checkItemInfoLocked(final long itemId, final ItemInfo item, StackTraceElement[] stackTrace) {
        ItemInfo itemInfo = sBgDataModel.itemsIdMap.get(itemId);
        if (itemInfo == null || item == itemInfo) {
            return;
        }
        if ((itemInfo instanceof ShortcutInfo) && (item instanceof ShortcutInfo)) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
            ShortcutInfo shortcutInfo2 = (ShortcutInfo) item;
            if (shortcutInfo.title != null && shortcutInfo2.title != null && shortcutInfo.title.toString().equals(shortcutInfo2.title.toString()) && shortcutInfo.intent.filterEquals(shortcutInfo2.intent) && shortcutInfo.id == shortcutInfo2.id && shortcutInfo.itemType == shortcutInfo2.itemType && shortcutInfo.container == shortcutInfo2.container && shortcutInfo.screenId == shortcutInfo2.screenId && shortcutInfo.cellX == shortcutInfo2.cellX && shortcutInfo.cellY == shortcutInfo2.cellY && shortcutInfo.spanX == shortcutInfo2.spanX && shortcutInfo.spanY == shortcutInfo2.spanY) {
                if (shortcutInfo.dropPos == null && shortcutInfo2.dropPos == null) {
                    return;
                }
                if (shortcutInfo.dropPos != null && shortcutInfo2.dropPos != null && shortcutInfo.dropPos[0] == shortcutInfo2.dropPos[0] && shortcutInfo.dropPos[1] == shortcutInfo2.dropPos[1]) {
                    return;
                }
            }
        }
        RuntimeException runtimeException = new RuntimeException("item: " + (item != null ? item.toString() : "null") + "modelItem: " + (itemInfo != null ? itemInfo.toString() : "null") + "Error: ItemInfo passed to checkItemInfo doesn't match original");
        if (stackTrace != null) {
            runtimeException.setStackTrace(stackTrace);
        }
        runtimeException.printStackTrace();
    }

    static void checkDuplicatedApplicationLocked(final ItemInfo item, StackTraceElement[] stackTrace) {
        Intent intent;
        if (item == null || !(item instanceof ShortcutInfo) || item.itemType != 0 || (intent = item.getIntent()) == null) {
            return;
        }
        ItemInfo itemInfo = null;
        Iterator<ItemInfo> it = sBgDataModel.itemsIdMap.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ItemInfo next = it.next();
            if (next instanceof ShortcutInfo) {
                if (next.itemType != 0) {
                    return;
                }
                Intent intent2 = next.getIntent();
                if (intent2 != null && intent.equals(intent2) && item != next && item.id != next.id && item.user.equals(next.user)) {
                    itemInfo = next;
                    break;
                }
            }
        }
        if (itemInfo == null || !LGHomeFeature.isEnableDefaultHome()) {
            return;
        }
        RuntimeException runtimeException = new RuntimeException("item: " + item + " modelItem: " + itemInfo + " Error: ItemInfo passed to checkItemInfo has been duplicated");
        if (stackTrace != null) {
            runtimeException.setStackTrace(stackTrace);
            throw runtimeException;
        }
        throw runtimeException;
    }

    public static void checkItemInfo(final ItemInfo item) {
        if (LGHomeFeature.Config.FEATURE_CHECK_ITEMINFO.getValue()) {
            final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            final long j = item.id;
            runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.8
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (LauncherModel.sBgDataModel) {
                        LauncherModel.checkItemInfoLocked(j, item, stackTrace);
                        LauncherModel.checkDuplicatedApplicationLocked(item, stackTrace);
                    }
                }
            });
        }
    }

    static void updateItemInDatabaseHelper(Context context, final ContentValues values, final ItemInfo item, final String callingFunction) {
        final long j = item.id;
        final Uri contentUri = LauncherSettings.Favorites.getContentUri(j);
        final ContentResolver contentResolver = context.getContentResolver();
        if (!MemoryUtils.hasAvailableFileSystemMemory(null, false)) {
            LGLog.i(TAG, "Memory is full. so LauncherModel.updateItemInDatabaseHelper() is skipped.");
            LGLog.i(TAG, "skipped uri =" + contentUri);
            return;
        }
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.9
            @Override // java.lang.Runnable
            public void run() {
                contentResolver.update(contentUri, values, null, null);
                LauncherModel.updateItemArrays(item, j, stackTrace);
            }
        });
    }

    static void updateItemsInDatabaseHelper(Context context, final ArrayList<ContentValues> valuesList, final ArrayList<ItemInfo> items, final String callingFunction) {
        final ContentResolver contentResolver = context.getContentResolver();
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.10
            @Override // java.lang.Runnable
            public void run() {
                ArrayList<ContentProviderOperation> arrayList = new ArrayList<>();
                int size = items.size();
                for (int i = 0; i < size; i++) {
                    ItemInfo itemInfo = (ItemInfo) items.get(i);
                    long j = itemInfo.id;
                    Uri contentUri = LauncherSettings.Favorites.getContentUri(j);
                    arrayList.add(ContentProviderOperation.newUpdate(contentUri).withValues((ContentValues) valuesList.get(i)).build());
                    LauncherModel.updateItemArrays(itemInfo, j, stackTrace);
                }
                try {
                    contentResolver.applyBatch(LauncherProvider.AUTHORITY, arrayList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static void updateItemArrays(ItemInfo item, long itemId, StackTraceElement[] stackTrace) {
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            checkItemInfoLocked(itemId, item, stackTrace);
            if (item.container != -100 && item.container != -101 && !bgDataModel.folders.containsKey(item.container)) {
                Log.e(TAG, "item: " + item + " container being set to: " + item.container + ", not in the list of folders");
            }
            ItemInfo itemInfo = bgDataModel.itemsIdMap.get(itemId);
            if (itemInfo != null && (itemInfo.container == -100 || itemInfo.container == -101)) {
                int i = itemInfo.itemType;
                if ((i == 0 || i == 1 || i == 2 || i == 6) && !bgDataModel.workspaceItems.contains(itemInfo)) {
                    bgDataModel.workspaceItems.add(itemInfo);
                }
            } else {
                bgDataModel.workspaceItems.remove(itemInfo);
            }
        }
    }

    public static void moveItemInDatabase(Context context, final ItemInfo item, final long container, final long screenId, final int cellX, final int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        if ((context instanceof Launcher) && screenId < 0 && container == -101) {
            item.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        } else {
            item.screenId = screenId;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(item.container));
        contentValues.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(item.cellX));
        contentValues.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(item.cellY));
        contentValues.put("rank", Integer.valueOf(item.rank));
        contentValues.put("screen", Long.valueOf(item.screenId));
        updateItemInDatabaseHelper(context, contentValues, item, "moveItemInDatabase");
    }

    public static void moveItemsInDatabase(Context context, final ArrayList<ItemInfo> items, final long container, final int screen) {
        ArrayList arrayList = new ArrayList();
        int size = items.size();
        for (int i = 0; i < size; i++) {
            ItemInfo itemInfo = items.get(i);
            itemInfo.container = container;
            if ((context instanceof Launcher) && screen < 0 && container == -101) {
                itemInfo.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(itemInfo.cellX, itemInfo.cellY);
            } else {
                itemInfo.screenId = screen;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(itemInfo.container));
            contentValues.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(itemInfo.cellX));
            contentValues.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(itemInfo.cellY));
            contentValues.put("rank", Integer.valueOf(itemInfo.rank));
            contentValues.put("screen", Long.valueOf(itemInfo.screenId));
            arrayList.add(contentValues);
        }
        updateItemsInDatabaseHelper(context, arrayList, items, "moveItemInDatabase");
    }

    public static void modifyItemInDatabase(Context context, final ItemInfo item, final long container, final long screenId, final int cellX, final int cellY, final int spanX, final int spanY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        item.spanX = spanX;
        item.spanY = spanY;
        if ((context instanceof Launcher) && screenId < 0 && container == -101) {
            Launcher launcher = (Launcher) context;
            item.screenId = launcher.getHotseat().getOrderInHotseat(cellX, cellY);
            item.cellX = launcher.getHotseat().getCellXFromOrder((int) item.screenId);
            item.cellY = launcher.getHotseat().getCellYFromOrder((int) item.screenId);
        } else {
            item.screenId = screenId;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(item.container));
        contentValues.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(item.cellX));
        contentValues.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(item.cellY));
        contentValues.put("rank", Integer.valueOf(item.rank));
        contentValues.put("spanX", Integer.valueOf(item.spanX));
        contentValues.put("spanY", Integer.valueOf(item.spanY));
        contentValues.put("screen", Long.valueOf(item.screenId));
        item.onResizeItemInDatabase(contentValues);
        updateItemInDatabaseHelper(context, contentValues, item, "modifyItemInDatabase");
    }

    public static void updateItemInDatabase(Context context, final ItemInfo item) {
        ContentValues contentValues = new ContentValues();
        item.onAddToDatabase(context, contentValues);
        updateItemInDatabaseHelper(context, contentValues, item, "updateItemInDatabase");
    }

    private void assertWorkspaceLoaded() {
        if (LauncherAppState.isDogfoodBuild()) {
            if (isLoadingWorkspace() || !this.mHasLoaderCompletedOnce) {
                throw new RuntimeException("Trying to add shortcut while loader is running");
            }
        }
    }

    boolean shortcutExists(Context context, Intent intent, UserHandle user) {
        String uri;
        String uri2;
        assertWorkspaceLoaded();
        if (intent.getComponent() != null) {
            String packageName = intent.getComponent().getPackageName();
            if (intent.getPackage() != null) {
                uri = intent.toUri(0);
                uri2 = new Intent(intent).setPackage(null).toUri(0);
            } else {
                uri = new Intent(intent).setPackage(packageName).toUri(0);
                uri2 = intent.toUri(0);
            }
        } else {
            uri = intent.toUri(0);
            uri2 = intent.toUri(0);
        }
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            for (ItemInfo itemInfo : bgDataModel.itemsIdMap) {
                if ((itemInfo instanceof ShortcutInfo) && itemInfo.itemType == 0) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                    Intent intent2 = shortcutInfo.promisedIntent == null ? shortcutInfo.intent : shortcutInfo.promisedIntent;
                    if (intent2 != null && shortcutInfo.user.equals(user)) {
                        String uri3 = intent2.toUri(0);
                        if (uri.equals(uri3) || uri2.equals(uri3)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    FolderInfo getFolderById(Context context, LongArrayMap<FolderInfo> folderList, long id) {
        int i = 0;
        Cursor cursorQuery = context.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, null, "_id=? and (itemType=? or itemType=?)", new String[]{String.valueOf(id), String.valueOf(2)}, null);
        try {
            FolderInfo folderInfoFindOrMakeFolder = null;
            if (!cursorQuery.moveToFirst()) {
                return null;
            }
            int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ITEM_TYPE);
            int columnIndexOrThrow2 = cursorQuery.getColumnIndexOrThrow("title");
            int columnIndexOrThrow3 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
            int columnIndexOrThrow4 = cursorQuery.getColumnIndexOrThrow("screen");
            int columnIndexOrThrow5 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
            int columnIndexOrThrow6 = cursorQuery.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
            int columnIndexOrThrow7 = cursorQuery.getColumnIndexOrThrow("options");
            if (cursorQuery.getInt(columnIndexOrThrow) == 2) {
                folderInfoFindOrMakeFolder = sBgDataModel.findOrMakeFolder(id);
            }
            folderInfoFindOrMakeFolder.title = cursorQuery.getString(columnIndexOrThrow2);
            folderInfoFindOrMakeFolder.id = id;
            folderInfoFindOrMakeFolder.container = cursorQuery.getInt(columnIndexOrThrow3);
            folderInfoFindOrMakeFolder.screenId = cursorQuery.getInt(columnIndexOrThrow4);
            folderInfoFindOrMakeFolder.cellX = cursorQuery.getInt(columnIndexOrThrow5);
            folderInfoFindOrMakeFolder.cellY = cursorQuery.getInt(columnIndexOrThrow6);
            folderInfoFindOrMakeFolder.options = cursorQuery.getInt(columnIndexOrThrow7);
            if (!DDTUtils.isAdditionalThemeApplied(context) && !DDTUtils.isAdditionalIconThemeApplied(context)) {
                i = cursorQuery.getInt(cursorQuery.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_TYPE));
            }
            folderInfoFindOrMakeFolder.folderColor = i;
            return folderInfoFindOrMakeFolder;
        } finally {
            cursorQuery.close();
        }
    }

    public static void addItemToDatabase(Context context, final ItemInfo item, final long container, final long screenId, final int cellX, final int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        if ((context instanceof Launcher) && screenId < 0 && container == -101) {
            item.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        } else {
            item.screenId = screenId;
        }
        final ContentValues contentValues = new ContentValues();
        final ContentResolver contentResolver = context.getContentResolver();
        item.onAddToDatabase(context, contentValues);
        item.id = LauncherSettings.Settings.call(contentResolver, LauncherSettings.Settings.METHOD_NEW_ITEM_ID).getLong("value");
        contentValues.put("_id", Long.valueOf(item.id));
        new Throwable().getStackTrace();
        final Context applicationContext = context.getApplicationContext();
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.11
            @Override // java.lang.Runnable
            public void run() {
                contentResolver.insert(LauncherSettings.Favorites.CONTENT_URI, contentValues);
                synchronized (LauncherModel.sBgDataModel) {
                    LauncherModel.sBgDataModel.addItem(applicationContext, item, true);
                }
            }
        });
    }

    private static ArrayList<ItemInfo> getItemsByPackageName(final String pn, final UserHandle user) {
        return filterItemInfos(sBgDataModel.itemsIdMap, new ItemInfoFilter() { // from class: com.android.launcher3.LauncherModel.12
            @Override // com.android.launcher3.LauncherModel.ItemInfoFilter
            public boolean filterItem(ItemInfo parent, ItemInfo info, ComponentName cn) {
                return cn.getPackageName().equals(pn) && info.user.equals(user);
            }
        });
    }

    static void deletePackageFromDatabase(Context context, final String pn, final UserHandle user) {
        deleteItemsFromDatabase(context, getItemsByPackageName(pn, user));
    }

    private static ArrayList<ItemInfo> getHideItemsByPackageName(final String pn, final UserHandle user) {
        return filterItemInfos(sBgDataModel.itemsIdMap, new ItemInfoFilter() { // from class: com.android.launcher3.LauncherModel.13
            @Override // com.android.launcher3.LauncherModel.ItemInfoFilter
            public boolean filterItem(ItemInfo parent, ItemInfo info, ComponentName cn) {
                return cn.flattenToString().equals(pn) && info.user.equals(user);
            }
        });
    }

    public static ArrayList<ItemInfo> deleteHideAppsFromDatabase(Context context, final ComponentName cn, final UserHandle user) {
        ArrayList<ItemInfo> hideItemsByPackageName = getHideItemsByPackageName(cn.flattenToString(), user);
        deleteItemsFromDatabase(context, hideItemsByPackageName);
        return hideItemsByPackageName;
    }

    public static ArrayList<ItemInfo> deleteHideAppsFromDatabase(Context context, List<HideAppItem> items) {
        ArrayList<ItemInfo> arrayList = new ArrayList<>();
        LongArrayMap longArrayMap = new LongArrayMap();
        for (HideAppItem hideAppItem : items) {
            arrayList.addAll(getHideItemsByPackageName(hideAppItem.getActivityInfo().getComponentName().flattenToString(), hideAppItem.getUserHandle()));
        }
        for (ItemInfo itemInfo : arrayList) {
            longArrayMap.put(itemInfo.id, itemInfo);
        }
        HashSet hashSet = new HashSet();
        Iterator<ItemInfo> it = arrayList.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ItemInfo next = it.next();
            if (next.container >= 0) {
                hashSet.add(Long.valueOf(next.container));
            }
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Iterator it2 = hashSet.iterator();
        while (it2.hasNext()) {
            FolderInfo folderInfo = sBgDataModel.folders.get(((Long) it2.next()).longValue());
            if (folderInfo != null) {
                Iterator<ShortcutInfo> it3 = folderInfo.getContents().iterator();
                int i = 0;
                while (it3.hasNext()) {
                    if (longArrayMap.get(it3.next().id) != 0) {
                        i++;
                    }
                }
                if (i == folderInfo.getContents().size()) {
                    LGLog.d(TAG, "empty folder: " + folderInfo);
                    arrayList2.add(folderInfo);
                } else {
                    LGLog.d(TAG, "update folder: " + folderInfo);
                    arrayList3.add(Long.valueOf(folderInfo.id));
                }
            }
        }
        arrayList.addAll(arrayList2);
        deleteItemsFromDatabase(context, arrayList);
        Iterator it4 = arrayList3.iterator();
        while (it4.hasNext()) {
            updateFolderItemsRankToBePacked(context, ((Long) it4.next()).longValue());
        }
        return arrayList;
    }

    public static void updateFolderItemsRankToBePacked(Context context, final long container) {
        final ContentResolver contentResolver = context.getContentResolver();
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.14
            /* JADX WARN: Removed duplicated region for block: B:23:0x007e  */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void run() throws java.lang.Throwable {
                /*
                    r10 = this;
                    java.lang.String r0 = "_id"
                    java.lang.String[] r3 = new java.lang.String[]{r0}
                    java.lang.String r4 = "container = ?"
                    r0 = 1
                    java.lang.String[] r5 = new java.lang.String[r0]
                    long r0 = r1
                    java.lang.String r0 = java.lang.Long.toString(r0)
                    r8 = 0
                    r5[r8] = r0
                    java.lang.String r6 = "rank"
                    r0 = 0
                    android.content.ContentResolver r1 = r3     // Catch: java.lang.Throwable -> L69 android.database.sqlite.SQLiteException -> L6e
                    android.net.Uri r2 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI     // Catch: java.lang.Throwable -> L69 android.database.sqlite.SQLiteException -> L6e
                    r7 = 0
                    android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> L69 android.database.sqlite.SQLiteException -> L6e
                    r2 = r8
                L21:
                    boolean r3 = r1.moveToNext()     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    if (r3 == 0) goto L64
                    android.content.ContentValues r3 = new android.content.ContentValues     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    r3.<init>()     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    java.lang.String r4 = "screen"
                    r3.putNull(r4)     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    java.lang.String r4 = "cellX"
                    r3.putNull(r4)     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    java.lang.String r4 = "cellY"
                    r3.putNull(r4)     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    java.lang.String r4 = "rank"
                    int r5 = r2 + 1
                    java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    r3.put(r4, r2)     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    int r2 = r1.getInt(r8)     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    r4.<init>()     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    java.lang.String r6 = "_id = "
                    r4.append(r6)     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    r4.append(r2)     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    java.lang.String r2 = r4.toString()     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    android.content.ContentResolver r4 = r3     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    android.net.Uri r6 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    r4.update(r6, r3, r2, r0)     // Catch: android.database.sqlite.SQLiteException -> L67 java.lang.Throwable -> L7b
                    r2 = r5
                    goto L21
                L64:
                    if (r1 == 0) goto L7a
                    goto L77
                L67:
                    r0 = move-exception
                    goto L72
                L69:
                    r1 = move-exception
                    r9 = r1
                    r1 = r0
                    r0 = r9
                    goto L7c
                L6e:
                    r1 = move-exception
                    r9 = r1
                    r1 = r0
                    r0 = r9
                L72:
                    r0.printStackTrace()     // Catch: java.lang.Throwable -> L7b
                    if (r1 == 0) goto L7a
                L77:
                    r1.close()
                L7a:
                    return
                L7b:
                    r0 = move-exception
                L7c:
                    if (r1 == 0) goto L81
                    r1.close()
                L81:
                    throw r0
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherModel.AnonymousClass14.run():void");
            }
        });
    }

    public static void deleteItemFromDatabase(Context context, final ItemInfo item) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(item);
        deleteItemsFromDatabase(context, arrayList);
    }

    static void deleteItemsFromDatabase(Context context, final ArrayList<? extends ItemInfo> items) {
        final ContentResolver contentResolver = context.getContentResolver();
        final Context applicationContext = context.getApplicationContext();
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.15
            @Override // java.lang.Runnable
            public void run() {
                for (ItemInfo itemInfo : items) {
                    contentResolver.delete(LauncherSettings.Favorites.getContentUri(itemInfo.id), null, null);
                    synchronized (LauncherModel.sBgDataModel) {
                        LauncherModel.sBgDataModel.removeItem(applicationContext, itemInfo);
                    }
                }
            }
        });
    }

    public static void updateWorkspaceScreenOrder(final Context context, final ArrayList<Long> screens) {
        final ArrayList arrayList = new ArrayList(screens);
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri uri = LauncherSettings.WorkspaceScreens.CONTENT_URI;
        ArraySet arraySet = new ArraySet(arrayList);
        if (arrayList.size() > arraySet.size()) {
            Log.e(TAG, "Sceen ID duplication issue : call by " + new Throwable().getStackTrace());
            arrayList.clear();
            arrayList.addAll(arraySet);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (((Long) it.next()).longValue() < 0) {
                it.remove();
            }
        }
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.16
            @Override // java.lang.Runnable
            public void run() {
                ArrayList<ContentProviderOperation> arrayList2 = new ArrayList<>();
                arrayList2.add(ContentProviderOperation.newDelete(uri).build());
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_id", Long.valueOf(((Long) arrayList.get(i)).longValue()));
                    contentValues.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
                    arrayList2.add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build());
                }
                try {
                    contentResolver.applyBatch(LauncherProvider.AUTHORITY, arrayList2);
                    synchronized (LauncherModel.sBgDataModel) {
                        LauncherModel.sBgDataModel.workspaceScreens.clear();
                        LauncherModel.sBgDataModel.workspaceScreens.addAll(arrayList);
                        LauncherModel.updateMaxScreenId(context, screens);
                        LGLog.i(LauncherModel.TAG, "ScreenCheck: updated screen order - " + LauncherModel.sBgDataModel.workspaceScreens);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void deleteFolderContentsFromDatabase(Context context, final FolderInfo info) {
        final ContentResolver contentResolver = context.getContentResolver();
        final Context applicationContext = context.getApplicationContext();
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.17
            @Override // java.lang.Runnable
            public void run() {
                contentResolver.delete(LauncherSettings.Favorites.CONTENT_URI, "container=" + info.id, null);
                LauncherModel.sBgDataModel.removeItem(applicationContext, info.contents);
                info.contents.clear();
                contentResolver.delete(LauncherSettings.Favorites.getContentUri(info.id), null, null);
                LauncherModel.sBgDataModel.removeItem(applicationContext, info);
            }
        });
    }

    public void initialize(Callbacks callbacks) {
        synchronized (this.mLock) {
            unbindItemInfosAndClearQueuedBindRunnables();
            this.mCallbacks = new WeakReference<>(callbacks);
        }
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackageChanged(String packageName, UserHandle user) throws Exception {
        enqueuePackageUpdated(new PackageUpdatedTask(2, new String[]{packageName}, user));
        if ("com.lge".compareTo(packageName) == 0) {
            LGLog.i(TAG, "Common resource changed");
            getWidgetProviders(this.mApp.getContext(), true);
        }
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackageRemoved(String packageName, UserHandle user) {
        checkQMemoplusPanelPackabe(packageName);
        enqueuePackageUpdated(new PackageUpdatedTask(3, new String[]{packageName}, user));
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackageAdded(String packageName, UserHandle user) {
        checkQMemoplusPanelPackabe(packageName);
        enqueuePackageUpdated(new PackageUpdatedTask(1, new String[]{packageName}, user));
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing) {
        enqueuePackageUpdated(new PackageUpdatedTask(2, packageNames, user));
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesUnavailable(String[] packageNames, UserHandle user, boolean replacing) {
        if (replacing) {
            return;
        }
        enqueuePackageUpdated(new PackageUpdatedTask(4, packageNames, user));
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesSuspended(String[] packageNames, UserHandle user) {
        enqueuePackageUpdated(new PackageUpdatedTask(5, packageNames, user));
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onPackagesUnsuspended(String[] packageNames, UserHandle user) {
        enqueuePackageUpdated(new PackageUpdatedTask(6, packageNames, user));
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) throws Exception {
        Callbacks callback;
        Callbacks callback2;
        LGLog.i(TAG, "onReceive intent=" + intent);
        String action = intent.getAction();
        if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
            Callbacks callback3 = getCallback();
            if (callback3 != null) {
                callback3.exitCleanViewMode();
            }
            this.mIconCache.onlyResetTitleinDB();
            Process.killProcess(Process.myPid());
            return;
        }
        if ("android.search.action.GLOBAL_SEARCH_ACTIVITY_CHANGED".equals(action) || "android.search.action.SEARCHABLES_CHANGED".equals(action)) {
            if (!context.getResources().getBoolean(R.bool.qsb_enabled) || (callback = getCallback()) == null) {
                return;
            }
            callback.bindSearchablesChanged();
            return;
        }
        if (LauncherAppsCompat.ACTION_MANAGED_PROFILE_ADDED.equals(action) || LauncherAppsCompat.ACTION_MANAGED_PROFILE_REMOVED.equals(action)) {
            LGLog.i(TAG, "UserProfiles : " + UserManagerCompat.getInstance(context).getUserProfiles());
            UserManagerCompat.getInstance(context).enableAndResetCache();
            forceReload();
            if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue()) {
                Utilities.checkDefineValuesForSwipeDownHome(context);
            }
            Callbacks callback4 = getCallback();
            if (callback4 != null) {
                if (LauncherAppsCompat.ACTION_MANAGED_PROFILE_ADDED.equals(action)) {
                    callback4.notifyManagedProfileStatus(true);
                    return;
                } else {
                    if (LauncherAppsCompat.ACTION_MANAGED_PROFILE_REMOVED.equals(action)) {
                        callback4.notifyManagedProfileStatus(false);
                        return;
                    }
                    return;
                }
            }
            return;
        }
        if (LauncherAppsCompat.ACTION_MANAGED_PROFILE_AVAILABLE.equals(action) || LauncherAppsCompat.ACTION_MANAGED_PROFILE_UNAVAILABLE.equals(action) || LauncherAppsCompat.ACTION_MANAGED_PROFILE_UNLOCKED.equals(action)) {
            UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
            if (userHandle != null) {
                if (LauncherAppsCompat.ACTION_MANAGED_PROFILE_AVAILABLE.equals(action) || LauncherAppsCompat.ACTION_MANAGED_PROFILE_UNAVAILABLE.equals(action)) {
                    enqueueItemUpdatedTask(new PackageUpdatedTask(7, new String[0], userHandle));
                    if (!Process.myUserHandle().equals(userHandle) && userHandle.getIdentifier() != 97 && (callback2 = getCallback()) != null) {
                        callback2.updateWorkProfileComponent();
                    }
                }
                if (LauncherAppsCompat.ACTION_MANAGED_PROFILE_UNAVAILABLE.equals(action) || LauncherAppsCompat.ACTION_MANAGED_PROFILE_UNLOCKED.equals(action)) {
                    LGLog.d(TAG, "execute UserLockStateChangedTask");
                    enqueueItemUpdatedTask(new UserLockStateChangedTask(userHandle));
                }
            }
            if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue()) {
                Utilities.checkDefineValuesForSwipeDownHome(context);
                return;
            }
            return;
        }
        if ("android.app.action.DEVICE_POLICY_RESOURCE_UPDATED".equals(action)) {
            Callbacks callback5 = getCallback();
            if (callback5 != null) {
                callback5.updateStringCache();
                return;
            }
            return;
        }
        if ("com.lge.launcher2.FORCERELOAD_HOME".equals(action)) {
            forceReload();
            return;
        }
        if (IntentConst.Action.ACTION_RELOAD_CUSTOMCONTENT.getValue(context).equals(action)) {
            LGLog.d(TAG, "IntentConst.Config.ACTION_RELOAD_CUSTOMCONTENT is received");
            Callbacks callback6 = getCallback();
            if (callback6 != null) {
                callback6.invalidateHasCustomContentToLeft();
                return;
            }
            return;
        }
        if ("com.lge.launcher2.ADD_WIDGET_IN_NEWPAGE".equals(action)) {
            LGLog.d(TAG, "com.lge.launcher2.ADD_WIDGET_IN_NEWPAGE is received");
            Callbacks callback7 = getCallback();
            if (callback7 != null) {
                callback7.addWidgetInNewPage(intent);
                return;
            }
            return;
        }
        if ("com.lge.launcher2.LLK_RESTORE".equals(action)) {
            LGLog.d(TAG, "com.lge.launcher2.LLK_RESTORE is received");
            LauncherAppState.getInstance(context).clearAndReloadWorkspace();
            return;
        }
        if ("com.lge.android.intent.action.RESOLUTION_SWITCH_MODE_CHANGED".equals(action)) {
            LGLog.d(TAG, "com.lge.android.intent.action.RESOLUTION_SWITCH_MODE_CHANGED is received");
            ScreenZoomChangeWatcher.getInstance().checkScreenResolutionChanged(this.mApp.getContext());
            return;
        }
        if ("com.lge.launcher3.intent.action.SPRINT_BRAND_MODE_CHANGED".equals(action)) {
            LGLog.i(TAG, "com.lge.launcher3.intent.action.SPRINT_BRAND_MODE_CHANGED is received");
            this.mIconCache.clearIconDB();
            forceReload();
            return;
        }
        if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(action)) {
            SettingsSearchUtils.updateIconFramesVisible(context, true, false);
            ScreenZoomChangeWatcher.getInstance().checkScreenResolutionChanged(this.mApp.getContext());
            return;
        }
        if ("android.intent.action.OVERLAY_CHANGED".equals(action)) {
            String string = intent.getData().toString().replace("package:", "").toString();
            LGLog.d(TAG, "ACTION_OVERLAY_CHAGNED : application Label : " + PackageUtils.getApplicationLabel(context, string));
            this.mIconCache.updateIconsForPkg(string, Process.myUserHandle());
            if (intent.getScheme() == null || AppNotifierManager.ExtraSpec.USAGE_PACKAGE.compareTo(intent.getScheme()) != 0) {
                return;
            }
            if (LauncherConst.PACKAGE_NAME_NATIVE.compareTo(intent.getData().getSchemeSpecificPart()) == 0 || "com.lge".compareTo(intent.getData().getSchemeSpecificPart()) == 0) {
                LGLog.i(TAG, "Overlay changed: " + intent.getData().toString());
                getWidgetProviders(context, true);
            }
        }
    }

    public void forceReload() {
        runOnMainThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.18
            @Override // java.lang.Runnable
            public void run() {
                LauncherModel.this.unbindItemInfosAndClearQueuedBindRunnables();
                LauncherModel.this.resetLoadedState(true, true);
                LauncherModel.this.startLoaderFromBackground();
            }
        });
    }

    public void resetLoadedState(boolean resetAllAppsLoaded, boolean resetWorkspaceLoaded) {
        synchronized (this.mLock) {
            stopLoaderLocked();
            if (resetAllAppsLoaded) {
                this.mAllAppsLoaded = false;
            }
            if (resetWorkspaceLoaded) {
                this.mWorkspaceLoaded = false;
            }
            this.mDeepShortcutsLoaded = false;
        }
    }

    public void startLoaderFromBackground() {
        Callbacks callback = getCallback();
        if ((callback == null || callback.setLoadOnResume()) ? false : true) {
            startLoader(com.lge.launcher3.PagedView.INVALID_RESTORE_PAGE);
        }
    }

    private void stopLoaderLocked() {
        LoaderTask loaderTask = this.mLoaderTask;
        if (loaderTask != null) {
            loaderTask.stopLocked();
        }
    }

    public boolean isCurrentCallbacks(Callbacks callbacks) {
        WeakReference<Callbacks> weakReference = this.mCallbacks;
        return weakReference != null && weakReference.get() == callbacks;
    }

    public boolean startLoader(int synchronousBindPage) {
        return startLoader(synchronousBindPage, 0);
    }

    public boolean startLoader(final int synchronousBindPage, int loadFlags) {
        LGLog.i(TAG, "startLoader(" + synchronousBindPage + ", " + loadFlags + ")");
        if ((loadFlags & 1) == 0 && LGFeatureConfig.isLauncherFacadeOperator() && isExistSprintInstaller(this.mApp.getContext())) {
            loadFlags |= 1;
        }
        InstallShortcutReceiver.enableInstallQueue();
        InstallShortcutReceiver.blockInstallQueueSwivel();
        synchronized (this.mLock) {
            ArrayList<Runnable> arrayList = mDeferredBindRunnables;
            synchronized (arrayList) {
                arrayList.clear();
            }
            WeakReference<Callbacks> weakReference = this.mCallbacks;
            if (weakReference != null && weakReference.get() != null) {
                final Callbacks callbacks = this.mCallbacks.get();
                MainThreadExecutor mainThreadExecutor = this.mUiExecutor;
                Objects.requireNonNull(callbacks);
                mainThreadExecutor.execute(new Runnable() { // from class: com.android.launcher3.-$$Lambda$VaXVAZ4E07z_RM8X0Qu_qWqY98E
                    @Override // java.lang.Runnable
                    public final void run() {
                        callbacks.clearPendingBinds();
                    }
                });
                stopLoaderLocked();
                this.mLoaderTask = new LoaderTask(this.mApp.getContext(), loadFlags);
                if (synchronousBindPage != -1001 && this.mAllAppsLoaded && this.mWorkspaceLoaded && this.mDeepShortcutsLoaded && !this.mIsLoaderTaskRunning) {
                    if ((loadFlags & 16) != 0) {
                        LGLog.d(TAG, "startLoader skip because landscape mode");
                        return true;
                    }
                    sWorkerThread.setPriority(5);
                    if ((loadFlags & 64) != 0) {
                        sWorker.post(new Runnable() { // from class: com.android.launcher3.-$$Lambda$LauncherModel$sPBBtXLaqanN1SPEMmMWeeYy49I
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$startLoader$0$LauncherModel();
                            }
                        });
                        return false;
                    }
                    sWorker.post(new Runnable() { // from class: com.android.launcher3.-$$Lambda$LauncherModel$PAuoRNZTDhAKAzt4cDgvRKusI8c
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$startLoader$1$LauncherModel(synchronousBindPage);
                        }
                    });
                    return true;
                }
                sWorkerThread.setPriority(5);
                sWorker.post(this.mLoaderTask);
            }
            return false;
        }
    }

    public /* synthetic */ void lambda$startLoader$0$LauncherModel() {
        this.mLoaderTask.runAllappsForSwivel();
    }

    public /* synthetic */ void lambda$startLoader$1$LauncherModel(int i) {
        this.mLoaderTask.runBindSynchronousPage(i);
    }

    void bindRemainingSynchronousPages() {
        Runnable[] runnableArr;
        ArrayList<Runnable> arrayList = mDeferredBindRunnables;
        if (!arrayList.isEmpty()) {
            synchronized (arrayList) {
                runnableArr = (Runnable[]) arrayList.toArray(new Runnable[arrayList.size()]);
                arrayList.clear();
            }
            for (Runnable runnable : runnableArr) {
                this.mHandler.post(runnable);
            }
        }
        ArrayList<Runnable> arrayList2 = mBindCompleteRunnables;
        if (arrayList2.isEmpty()) {
            return;
        }
        synchronized (arrayList2) {
            Iterator<Runnable> it = arrayList2.iterator();
            while (it.hasNext()) {
                runOnWorkerThread(it.next());
            }
            mBindCompleteRunnables.clear();
        }
    }

    public void stopLoader() {
        synchronized (this.mLock) {
            LoaderTask loaderTask = this.mLoaderTask;
            if (loaderTask != null) {
                loaderTask.stopLocked();
            }
        }
    }

    public static ArrayList<Long> loadWorkspaceScreensDb(Context context) {
        Cursor cursorQuery = context.getContentResolver().query(LauncherSettings.WorkspaceScreens.CONTENT_URI, null, null, null, LauncherSettings.WorkspaceScreens.SCREEN_RANK);
        ArrayList<Long> arrayList = new ArrayList<>();
        try {
            int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow("_id");
            while (cursorQuery.moveToNext()) {
                try {
                    arrayList.add(Long.valueOf(cursorQuery.getLong(columnIndexOrThrow)));
                } catch (Exception e) {
                    Launcher.addDumpLog(TAG, "Desktop items loading interrupted - invalid screens: " + e, true);
                }
            }
            LGLog.i(TAG, "ScreenCheck: loadWorkspaceScreensDb - " + arrayList);
            return arrayList;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    public boolean isAllAppsLoaded() {
        return this.mAllAppsLoaded;
    }

    public boolean isLoadingWorkspace() {
        synchronized (this.mLock) {
            LoaderTask loaderTask = this.mLoaderTask;
            if (loaderTask == null) {
                return false;
            }
            return loaderTask.isLoadingWorkspace();
        }
    }

    public class LoaderTask implements Runnable {
        private Context mContext;
        private int mFlags;
        boolean mIsLoadingAndBindingWorkspace;
        boolean mLoadAndBindStepFinished;
        private boolean mStopped;
        private final Executor mUiExecutor = new MainThreadExecutor();

        LoaderTask(Context context, int flags) {
            this.mContext = context;
            this.mFlags = flags;
        }

        boolean isLoadingWorkspace() {
            return this.mIsLoadingAndBindingWorkspace;
        }

        private void loadAndBindDeepShortcuts() {
            if (LauncherModel.DEBUG_LOADERS) {
                Log.d(LauncherModel.TAG, "loadAndBindDeepShortcuts mDeepShortcutsLoaded=" + LauncherModel.this.mDeepShortcutsLoaded);
            }
            if (!LauncherModel.this.mDeepShortcutsLoaded) {
                LauncherModel.this.mBgDeepShortcutMap.clear();
                LauncherModel launcherModel = LauncherModel.this;
                launcherModel.mHasShortcutHostPermission = launcherModel.mDeepShortcutManager.hasHostPermission();
                if (LauncherModel.this.mHasShortcutHostPermission) {
                    for (UserHandle userHandle : LauncherModel.this.mUserManager.getUserProfiles()) {
                        if (LauncherModel.this.mUserManager.isUserUnlocked(userHandle)) {
                            LauncherModel.this.updateDeepShortcutMap(null, userHandle, LauncherModel.this.mDeepShortcutManager.queryForAllShortcuts(userHandle));
                        }
                    }
                }
                synchronized (this) {
                    if (this.mStopped) {
                        return;
                    } else {
                        LauncherModel.this.mDeepShortcutsLoaded = true;
                    }
                }
            }
            bindDeepShortcuts();
        }

        private void loadAndBindWorkspace() {
            this.mIsLoadingAndBindingWorkspace = true;
            if (LauncherModel.DEBUG_LOADERS) {
                Log.d(LauncherModel.TAG, "loadAndBindWorkspace mWorkspaceLoaded=" + LauncherModel.this.mWorkspaceLoaded);
            }
            if (!LauncherModel.this.mWorkspaceLoaded) {
                loadWorkspace();
                synchronized (this) {
                    if (this.mStopped) {
                        return;
                    } else {
                        LauncherModel.this.mWorkspaceLoaded = true;
                    }
                }
            }
            bindWorkspace(-1);
        }

        private void waitForIdle() {
            synchronized (this) {
                long jUptimeMillis = LauncherModel.DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0L;
                LauncherModel.this.mHandler.postIdle(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.1
                    @Override // java.lang.Runnable
                    public void run() {
                        synchronized (LoaderTask.this) {
                            LoaderTask.this.mLoadAndBindStepFinished = true;
                            if (LauncherModel.DEBUG_LOADERS) {
                                Log.d(LauncherModel.TAG, "done with previous binding step");
                            }
                            LoaderTask.this.notify();
                        }
                    }
                });
                while (!this.mStopped && !this.mLoadAndBindStepFinished) {
                    try {
                        wait(1000L);
                    } catch (InterruptedException unused) {
                    }
                }
                if (LauncherModel.DEBUG_LOADERS) {
                    Log.d(LauncherModel.TAG, "waited " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms for previous step to finish binding");
                }
            }
        }

        void runBindSynchronousPage(int synchronousBindPage) {
            if (synchronousBindPage == -1001) {
                throw new RuntimeException("Should not call runBindSynchronousPage() without valid page index");
            }
            if (!LauncherModel.this.mAllAppsLoaded || !LauncherModel.this.mWorkspaceLoaded) {
                throw new RuntimeException("Expecting AllApps and Workspace to be loaded");
            }
            synchronized (LauncherModel.this.mLock) {
                if (LauncherModel.this.mIsLoaderTaskRunning) {
                    throw new RuntimeException("Error! Background loading is already running");
                }
            }
            this.mUiExecutor.execute(new Runnable() { // from class: com.android.launcher3.-$$Lambda$LauncherModel$LoaderTask$ukIDxG82ntSAUKxyQ51jJAyliOk
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$runBindSynchronousPage$0$LauncherModel$LoaderTask();
                }
            });
            LGLog.d(LauncherModel.TAG, "runBindSynchronousPage: " + this.mStopped);
            synchronized (this) {
                if (this.mStopped) {
                    return;
                }
                bindWorkspace(synchronousBindPage);
                synchronized (this) {
                    if (this.mStopped) {
                        return;
                    }
                    bindAllApps();
                    bindWidgets();
                    bindDeepShortcuts();
                }
            }
        }

        public /* synthetic */ void lambda$runBindSynchronousPage$0$LauncherModel$LoaderTask() {
            LauncherModel.this.mHandler.flush();
        }

        void runAllappsForSwivel() {
            if (!LauncherModel.this.mAllAppsLoaded || !LauncherModel.this.mWorkspaceLoaded) {
                throw new RuntimeException("Expecting AllApps and Workspace to be loaded");
            }
            synchronized (LauncherModel.this.mLock) {
                if (LauncherModel.this.mIsLoaderTaskRunning) {
                    throw new RuntimeException("Error! Background loading is already running");
                }
            }
            this.mUiExecutor.execute(new Runnable() { // from class: com.android.launcher3.-$$Lambda$LauncherModel$LoaderTask$TtiwIkSts8yAKf00oi_vyLJYndM
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$runAllappsForSwivel$1$LauncherModel$LoaderTask();
                }
            });
            LGLog.d(LauncherModel.TAG, "runAllappsForSwivel: " + this.mStopped);
            synchronized (this) {
                if (this.mStopped) {
                    return;
                }
                bindAllApps();
            }
        }

        public /* synthetic */ void lambda$runAllappsForSwivel$1$LauncherModel$LoaderTask() {
            LauncherModel.this.mHandler.flush();
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (LauncherModel.this.mLock) {
                if (this.mStopped) {
                    return;
                }
                LauncherModel.this.mModelLoaded = false;
                LauncherModel.this.mIsLoaderTaskRunning = true;
                if (LauncherModel.DEBUG_LOADERS) {
                    Log.d(LauncherModel.TAG, "step 1: loading workspace");
                }
                loadAndBindWorkspace();
                if (!this.mStopped) {
                    waitForIdle();
                    if (LauncherModel.DEBUG_LOADERS) {
                        Log.d(LauncherModel.TAG, "step 2: loading all apps");
                    }
                    loadAndBindAllApps();
                    if (LauncherModel.DEBUG_LOADERS) {
                        Log.d(LauncherModel.TAG, "step 3: loading deep shortcuts");
                    }
                    loadAndBindDeepShortcuts();
                }
                if (!this.mStopped && LGHomeFeature.isEnableDefaultHome()) {
                    verifyApplications();
                }
                this.mContext = null;
                synchronized (LauncherModel.this.mLock) {
                    if (LauncherModel.this.mLoaderTask == this) {
                        LauncherModel.this.mLoaderTask = null;
                    }
                    LauncherModel.this.mIsLoaderTaskRunning = false;
                    LauncherModel.this.mHasLoaderCompletedOnce = true;
                    LauncherModel.this.mModelLoaded = false;
                }
            }
        }

        public void stopLocked() {
            synchronized (this) {
                this.mStopped = true;
                notify();
            }
        }

        Callbacks tryGetCallbacks(Callbacks oldCallbacks) {
            synchronized (LauncherModel.this.mLock) {
                if (this.mStopped) {
                    return null;
                }
                if (LauncherModel.this.mCallbacks == null) {
                    return null;
                }
                Callbacks callbacks = LauncherModel.this.mCallbacks.get();
                if (callbacks != oldCallbacks) {
                    return null;
                }
                if (callbacks != null) {
                    return callbacks;
                }
                Log.w(LauncherModel.TAG, "no mCallbacks");
                return null;
            }
        }

        private void verifyApplications() {
            if (!MemoryUtils.hasAvailableFileSystemMemory(null, false)) {
                LGLog.i(LauncherModel.TAG, "Memory is full. so LoaderTask.verifyApplications() is canceled.");
                return;
            }
            Context context = LauncherModel.this.mApp.getContext();
            if (LauncherModel.DEBUG_LOADERS) {
                Log.d(LauncherModel.TAG, "step 3: verify applications");
            }
            ArrayList<? extends ItemInfo> arrayList = new ArrayList<>();
            ArrayList arrayList2 = new ArrayList();
            synchronized (LauncherModel.sBgDataModel) {
                for (AppInfo appInfo : LauncherModel.this.mBgAllAppsList.data) {
                    if (this.mStopped) {
                        return;
                    }
                    ArrayList<ItemInfo> applicationItemInfoForComponentName = LauncherModel.this.getApplicationItemInfoForComponentName(appInfo.componentName, appInfo.user);
                    if (applicationItemInfoForComponentName.isEmpty()) {
                        if (!LauncherModel.isInSprintID) {
                            arrayList.add(appInfo);
                        }
                        Log.i(LauncherModel.TAG, "Missing application on load: " + appInfo);
                    } else if (applicationItemInfoForComponentName.size() > 1) {
                        long j = LongCompanionObject.MAX_VALUE;
                        ItemInfo itemInfo = null;
                        for (ItemInfo itemInfo2 : applicationItemInfoForComponentName) {
                            if (itemInfo2.id < j) {
                                j = itemInfo2.id;
                                itemInfo = itemInfo2;
                            }
                        }
                        applicationItemInfoForComponentName.remove(itemInfo);
                        arrayList2.addAll(applicationItemInfoForComponentName);
                        Log.i(LauncherModel.TAG, "Duplicate applications on load: " + applicationItemInfoForComponentName);
                    }
                }
                if (LauncherModel.DEBUG_LOADERS) {
                    Log.d(LauncherModel.TAG, "step 3-1: checked add or remove applications");
                }
                if (!this.mStopped && !arrayList.isEmpty()) {
                    LauncherModel.this.mRestored = SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.BackupRestoreKey.RESTORED, false);
                    if (LauncherModel.this.mRestored) {
                        Collections.sort(arrayList, LauncherModel.COMPARATOR);
                    }
                    LauncherModel.this.addAndBindAddedWorkspaceItems(context, arrayList, this.mFlags);
                    LauncherModel.this.mRestored = false;
                    SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.BackupRestoreKey.RESTORED, false);
                    SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.BackupRestoreKey.ISRESTORING, false);
                }
                if (LauncherModel.DEBUG_LOADERS) {
                    Log.d(LauncherModel.TAG, "step 3-2: added and binded applications");
                }
                if (!this.mStopped && !arrayList2.isEmpty()) {
                    LauncherModel.deleteItemsFromDatabase(context, arrayList2);
                    LauncherModel.this.forceReload();
                }
                if (LauncherModel.DEBUG_LOADERS) {
                    Log.d(LauncherModel.TAG, "step 3-3: removed and forceReload");
                }
            }
        }

        private boolean checkItemPlacement(LongArrayMap<ItemInfo[][]> occupied, ItemInfo item) {
            InvariantDeviceProfile invariantDeviceProfile = LauncherAppState.getInstance(this.mContext).getInvariantDeviceProfile();
            int i = invariantDeviceProfile.numColumns;
            int i2 = invariantDeviceProfile.numRows;
            long j = item.screenId;
            if (item.container == -101) {
                ItemInfo[][] itemInfoArr = occupied.get(-101L);
                if (item.screenId >= invariantDeviceProfile.numHotseatIcons) {
                    Log.e(LauncherModel.TAG, "Error loading shortcut " + item + " into hotseat position " + item.screenId + ", position out of bounds: (0 to " + (invariantDeviceProfile.numHotseatIcons - 1) + ")");
                    return false;
                }
                if (itemInfoArr != null) {
                    if (itemInfoArr[(int) item.screenId][0] != null) {
                        Log.e(LauncherModel.TAG, "Error loading shortcut into hotseat " + item + " into position (" + item.screenId + ":" + item.cellX + "," + item.cellY + ") occupied by " + occupied.get(-101L)[(int) item.screenId][0]);
                        return false;
                    }
                    itemInfoArr[(int) item.screenId][0] = item;
                    return true;
                }
                ItemInfo[][] itemInfoArr2 = (ItemInfo[][]) Array.newInstance((Class<?>) ItemInfo.class, invariantDeviceProfile.numHotseatIcons, 1);
                itemInfoArr2[(int) item.screenId][0] = item;
                occupied.put(-101L, itemInfoArr2);
                return true;
            }
            if (item.container != -100) {
                return true;
            }
            if (!occupied.containsKey(item.screenId)) {
                occupied.put(item.screenId, (ItemInfo[][]) Array.newInstance((Class<?>) ItemInfo.class, i + 1, i2 + 1));
            }
            ItemInfo[][] itemInfoArr3 = occupied.get(item.screenId);
            if ((item.container == -100 && item.cellX < 0) || item.cellY < 0 || item.cellX + item.spanX > i || item.cellY + item.spanY > i2) {
                Log.e(LauncherModel.TAG, "Error loading shortcut " + item + " into cell (" + j + "-" + item.screenId + ":" + item.cellX + "," + item.cellY + ") out of screen bounds ( " + i + "x" + i2 + ")");
                return false;
            }
            for (int i3 = item.cellX; i3 < item.cellX + item.spanX; i3++) {
                for (int i4 = item.cellY; i4 < item.cellY + item.spanY; i4++) {
                    if (itemInfoArr3[i3][i4] != null) {
                        Log.e(LauncherModel.TAG, "Error loading shortcut " + item + " into cell (" + j + "-" + item.screenId + ":" + i3 + "," + i4 + ") occupied by " + itemInfoArr3[i3][i4]);
                        return false;
                    }
                }
            }
            for (int i5 = item.cellX; i5 < item.cellX + item.spanX; i5++) {
                for (int i6 = item.cellY; i6 < item.cellY + item.spanY; i6++) {
                    itemInfoArr3[i5][i6] = item;
                }
            }
            return true;
        }

        /*  JADX ERROR: Type inference failed with stack overflow
            jadx.core.utils.exceptions.JadxOverflowException
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:77)
            */
        private void loadWorkspace() {
            /*
                r84 = this;
                r1 = r84
                boolean r2 = com.android.launcher3.LauncherModel.DEBUG_LOADERS
                if (r2 == 0) goto Lb
                long r5 = android.os.SystemClock.uptimeMillis()
                goto Ld
            Lb:
                r5 = 0
            Ld:
                android.content.Context r2 = r1.mContext
                android.content.ContentResolver r15 = r2.getContentResolver()
                android.content.pm.PackageManager r14 = r2.getPackageManager()
                boolean r17 = r14.isSafeMode()
                com.android.launcher3.compat.LauncherAppsCompat r13 = com.android.launcher3.compat.LauncherAppsCompat.getInstance(r2)
                boolean r18 = com.android.launcher3.Utilities.isBootCompleted()
                com.lge.launcher3.sharedpreferences.SharedPreferencesConst$BackupRestoreKey r7 = com.lge.launcher3.sharedpreferences.SharedPreferencesConst.BackupRestoreKey.RESTORED
                r12 = 0
                boolean r19 = com.lge.launcher3.sharedpreferences.SharedPreferencesManager.getBoolean(r2, r12, r7, r12)
                java.util.ArrayList r11 = new java.util.ArrayList
                r11.<init>()
                java.util.ArrayList r10 = new java.util.ArrayList
                r10.<init>()
                com.android.launcher3.InvariantDeviceProfile r9 = com.android.launcher3.LauncherAppState.getIDP(r2)
                int r7 = r9.numColumns
                int r8 = r9.numRows
                int r3 = r1.mFlags
                r4 = 1
                r3 = r3 & r4
                if (r3 == 0) goto L5a
                java.lang.String r3 = "Launcher.Model"
                java.lang.String r7 = "loadWorkspace: resetting launcher database"
                com.android.launcher3.Launcher.addDumpLog(r3, r7, r4)
                java.lang.String r3 = "create_empty_db"
                com.android.launcher3.LauncherSettings.Settings.call(r15, r3)
                r3 = r9
                com.lge.launcher3.profile.LGInvariantDeviceProfile r3 = (com.lge.launcher3.profile.LGInvariantDeviceProfile) r3
                android.content.Context r7 = r1.mContext
                r3.resetGridSize(r7)
                int r7 = r9.numColumns
                int r8 = r9.numRows
            L5a:
                r3 = r7
                int r7 = r1.mFlags
                r16 = r11
                r11 = 2
                r7 = r7 & r11
                if (r7 == 0) goto L70
                java.lang.String r7 = "Launcher.Model"
                java.lang.String r11 = "loadWorkspace: migrating from launcher2"
                com.android.launcher3.Launcher.addDumpLog(r7, r11, r4)
                java.lang.String r7 = "migrate_launcher2_shortcuts"
                com.android.launcher3.LauncherSettings.Settings.call(r15, r7)
                goto L7c
            L70:
                java.lang.String r7 = "Launcher.Model"
                java.lang.String r11 = "loadWorkspace: loading default favorites"
                com.android.launcher3.Launcher.addDumpLog(r7, r11, r12)
                java.lang.String r7 = "load_default_favorites"
                com.android.launcher3.LauncherSettings.Settings.call(r15, r7)
            L7c:
                com.android.launcher3.model.BgDataModel r21 = com.android.launcher3.LauncherModel.sBgDataModel
                monitor-enter(r21)
                com.android.launcher3.model.BgDataModel r7 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                r7.clear()     // Catch: java.lang.Throwable -> L2017
                android.content.Context r7 = r1.mContext     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.compat.PackageInstallerCompat r7 = com.android.launcher3.compat.PackageInstallerCompat.getInstance(r7)     // Catch: java.lang.Throwable -> L2017
                java.util.HashMap r11 = r7.updateAndGetActiveSessionCache()     // Catch: java.lang.Throwable -> L2017
                android.content.Context r7 = r1.mContext     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.compat.PackageInstallerCompat r7 = com.android.launcher3.compat.PackageInstallerCompat.getInstance(r7)     // Catch: java.lang.Throwable -> L2017
                java.util.HashMap r7 = r7.getActiveSessionExceptAppBoxInstaller()     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList r4 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L2017
                r4.<init>()     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList r12 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L2017
                r12.<init>()     // Catch: java.lang.Throwable -> L2017
                r23 = r12
                java.util.HashMap r12 = new java.util.HashMap     // Catch: java.lang.Throwable -> L2017
                r12.<init>()     // Catch: java.lang.Throwable -> L2017
                r24 = r8
                android.net.Uri r8 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI     // Catch: java.lang.Throwable -> L2017
                boolean r25 = com.android.launcher3.LauncherModel.DEBUG_LOADERS     // Catch: java.lang.Throwable -> L2017
                if (r25 == 0) goto Lce
                r25 = r7
                java.lang.String r7 = "Launcher.Model"
                r26 = r9
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2017
                r9.<init>()     // Catch: java.lang.Throwable -> L2017
                r27 = r10
                java.lang.String r10 = "loading model from "
                r9.append(r10)     // Catch: java.lang.Throwable -> L2017
                r9.append(r8)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r9 = r9.toString()     // Catch: java.lang.Throwable -> L2017
                android.util.Log.d(r7, r9)     // Catch: java.lang.Throwable -> L2017
                goto Ld4
            Lce:
                r25 = r7
                r26 = r9
                r27 = r10
            Ld4:
                r9 = 0
                r10 = 0
                r28 = 0
                r29 = 0
                r30 = r25
                r7 = r15
                r31 = r24
                r32 = r26
                r33 = r27
                r34 = r11
                r24 = r15
                r15 = r16
                r11 = r28
                r20 = r3
                r3 = r12
                r35 = r23
                r12 = r29
                android.database.Cursor r12 = r7.query(r8, r9, r10, r11, r12)     // Catch: java.lang.Throwable -> L2017
                android.content.Context r11 = r2.getApplicationContext()     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.util.LongArrayMap r10 = new com.android.launcher3.util.LongArrayMap     // Catch: java.lang.Throwable -> L2017
                r10.<init>()     // Catch: java.lang.Throwable -> L2017
                java.lang.String r7 = "_id"
                int r9 = r12.getColumnIndexOrThrow(r7)     // Catch: java.lang.Throwable -> L200d
                java.lang.String r7 = "intent"
                int r8 = r12.getColumnIndexOrThrow(r7)     // Catch: java.lang.Throwable -> L200d
                java.lang.String r7 = "title"
                int r7 = r12.getColumnIndexOrThrow(r7)     // Catch: java.lang.Throwable -> L200d
                r25 = r5
                java.lang.String r5 = "container"
                int r5 = r12.getColumnIndexOrThrow(r5)     // Catch: java.lang.Throwable -> L200d
                java.lang.String r6 = "itemType"
                int r6 = r12.getColumnIndexOrThrow(r6)     // Catch: java.lang.Throwable -> L200d
                r16 = r15
                java.lang.String r15 = "appWidgetId"
                int r15 = r12.getColumnIndexOrThrow(r15)     // Catch: java.lang.Throwable -> L200d
                r23 = r14
                java.lang.String r14 = "appWidgetProvider"
                int r14 = r12.getColumnIndexOrThrow(r14)     // Catch: java.lang.Throwable -> L200d
                r27 = r13
                java.lang.String r13 = "screen"
                int r13 = r12.getColumnIndexOrThrow(r13)     // Catch: java.lang.Throwable -> L200d
                r28 = r7
                java.lang.String r7 = "cellX"
                int r7 = r12.getColumnIndexOrThrow(r7)     // Catch: java.lang.Throwable -> L200d
                r29 = r11
                java.lang.String r11 = "cellY"
                int r11 = r12.getColumnIndexOrThrow(r11)     // Catch: java.lang.Throwable -> L200d
                r36 = r10
                java.lang.String r10 = "spanX"
                int r10 = r12.getColumnIndexOrThrow(r10)     // Catch: java.lang.Throwable -> L200d
                r37 = r10
                java.lang.String r10 = "spanY"
                int r10 = r12.getColumnIndexOrThrow(r10)     // Catch: java.lang.Throwable -> L200d
                r38 = r10
                java.lang.String r10 = "rank"
                int r10 = r12.getColumnIndexOrThrow(r10)     // Catch: java.lang.Throwable -> L200d
                r39 = r10
                java.lang.String r10 = "restored"
                int r10 = r12.getColumnIndexOrThrow(r10)     // Catch: java.lang.Throwable -> L200d
                r40 = r11
                java.lang.String r11 = "profileId"
                int r11 = r12.getColumnIndexOrThrow(r11)     // Catch: java.lang.Throwable -> L200d
                r41 = r7
                java.lang.String r7 = "options"
                int r7 = r12.getColumnIndexOrThrow(r7)     // Catch: java.lang.Throwable -> L200d
                r42 = r7
                com.android.launcher3.util.CursorIconInfo r7 = new com.android.launcher3.util.CursorIconInfo     // Catch: java.lang.Throwable -> L200d
                r7.<init>(r12)     // Catch: java.lang.Throwable -> L200d
                r43 = r7
                android.util.LongSparseArray r7 = new android.util.LongSparseArray     // Catch: java.lang.Throwable -> L200d
                r7.<init>()     // Catch: java.lang.Throwable -> L200d
                r44 = r13
                android.util.LongSparseArray r13 = new android.util.LongSparseArray     // Catch: java.lang.Throwable -> L200d
                r13.<init>()     // Catch: java.lang.Throwable -> L200d
                r45 = r8
                android.util.LongSparseArray r8 = new android.util.LongSparseArray     // Catch: java.lang.Throwable -> L200d
                r8.<init>()     // Catch: java.lang.Throwable -> L200d
                r46 = r2
                com.android.launcher3.LauncherModel r2 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L200d
                com.android.launcher3.compat.UserManagerCompat r2 = r2.mUserManager     // Catch: java.lang.Throwable -> L200d
                java.util.List r2 = r2.getUserProfiles()     // Catch: java.lang.Throwable -> L200d
                java.util.Iterator r2 = r2.iterator()     // Catch: java.lang.Throwable -> L200d
            L1a1:
                boolean r47 = r2.hasNext()     // Catch: java.lang.Throwable -> L200d
                r48 = r4
                if (r47 == 0) goto L22d
                java.lang.Object r47 = r2.next()     // Catch: java.lang.Throwable -> L228
                r4 = r47
                android.os.UserHandle r4 = (android.os.UserHandle) r4     // Catch: java.lang.Throwable -> L228
                r47 = r2
                com.android.launcher3.LauncherModel r2 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L228
                com.android.launcher3.compat.UserManagerCompat r2 = r2.mUserManager     // Catch: java.lang.Throwable -> L228
                r50 = r14
                r49 = r15
                long r14 = r2.getSerialNumberForUser(r4)     // Catch: java.lang.Throwable -> L228
                r7.put(r14, r4)     // Catch: java.lang.Throwable -> L228
                com.android.launcher3.LauncherModel r2 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L228
                com.android.launcher3.compat.UserManagerCompat r2 = r2.mUserManager     // Catch: java.lang.Throwable -> L228
                boolean r2 = r2.isQuietModeEnabled(r4)     // Catch: java.lang.Throwable -> L228
                java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)     // Catch: java.lang.Throwable -> L228
                r13.put(r14, r2)     // Catch: java.lang.Throwable -> L228
                com.android.launcher3.LauncherModel r2 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L228
                com.android.launcher3.compat.UserManagerCompat r2 = r2.mUserManager     // Catch: java.lang.Throwable -> L228
                boolean r2 = r2.isUserUnlocked(r4)     // Catch: java.lang.Throwable -> L228
                if (r2 == 0) goto L211
                r51 = r2
                com.android.launcher3.LauncherModel r2 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L228
                com.android.launcher3.shortcuts.DeepShortcutManager r2 = com.android.launcher3.LauncherModel.m48$$Nest$fgetmDeepShortcutManager(r2)     // Catch: java.lang.Throwable -> L228
                r52 = r13
                r13 = 0
                java.util.List r2 = r2.queryForPinnedShortcuts(r13, r4)     // Catch: java.lang.Throwable -> L228
                com.android.launcher3.LauncherModel r4 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L228
                com.android.launcher3.shortcuts.DeepShortcutManager r4 = com.android.launcher3.LauncherModel.m48$$Nest$fgetmDeepShortcutManager(r4)     // Catch: java.lang.Throwable -> L228
                boolean r4 = r4.wasLastCallSuccess()     // Catch: java.lang.Throwable -> L228
                if (r4 == 0) goto L20e
                java.util.Iterator r2 = r2.iterator()     // Catch: java.lang.Throwable -> L228
            L1fa:
                boolean r4 = r2.hasNext()     // Catch: java.lang.Throwable -> L228
                if (r4 == 0) goto L215
                java.lang.Object r4 = r2.next()     // Catch: java.lang.Throwable -> L228
                com.android.launcher3.shortcuts.ShortcutInfoCompat r4 = (com.android.launcher3.shortcuts.ShortcutInfoCompat) r4     // Catch: java.lang.Throwable -> L228
                com.android.launcher3.shortcuts.ShortcutKey r13 = com.android.launcher3.shortcuts.ShortcutKey.fromInfo(r4)     // Catch: java.lang.Throwable -> L228
                r3.put(r13, r4)     // Catch: java.lang.Throwable -> L228
                goto L1fa
            L20e:
                r51 = 0
                goto L215
            L211:
                r51 = r2
                r52 = r13
            L215:
                java.lang.Boolean r2 = java.lang.Boolean.valueOf(r51)     // Catch: java.lang.Throwable -> L228
                r8.put(r14, r2)     // Catch: java.lang.Throwable -> L228
                r2 = r47
                r4 = r48
                r15 = r49
                r14 = r50
                r13 = r52
                goto L1a1
            L228:
                r0 = move-exception
            L229:
                r2 = r0
                r3 = r12
                goto L2011
            L22d:
                r52 = r13
                r50 = r14
                r49 = r15
            L233:
                boolean r2 = r1.mStopped     // Catch: java.lang.Throwable -> L200d
                r53 = -101(0xffffffffffffff9b, double:NaN)
                if (r2 != 0) goto L1cf0
                boolean r2 = r12.moveToNext()     // Catch: java.lang.Throwable -> L200d
                if (r2 == 0) goto L1cf0
                int r2 = r12.getInt(r6)     // Catch: java.lang.Exception -> L1c68 java.lang.Throwable -> L200d
                int r13 = r12.getInt(r10)     // Catch: java.lang.Exception -> L1c68 java.lang.Throwable -> L200d
                if (r13 == 0) goto L24b
                r13 = 1
                goto L24c
            L24b:
                r13 = 0
            L24c:
                int r15 = r12.getInt(r5)     // Catch: java.lang.Exception -> L1c68 java.lang.Throwable -> L200d
                if (r2 == 0) goto L2a1
                r4 = 1
                if (r2 == r4) goto L2a1
                r4 = 2
                if (r2 == r4) goto L8cf
                r4 = 5
                r14 = 4
                if (r2 == r14) goto L2c3
                if (r2 == r4) goto L2c3
                r14 = 6
                if (r2 == r14) goto L2a1
                r13 = r3
                r70 = r8
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
                r8 = r46
                r46 = r49
                r40 = r50
                r49 = r5
                r50 = r6
                r36 = r10
                r43 = r16
                r42 = r28
                r6 = r33
                r41 = r35
                r35 = r45
                r5 = r48
                r45 = r7
                r28 = r9
                r9 = r29
                r7 = r34
                r34 = r52
            L29d:
                r29 = r11
                goto L1cb0
            L2a1:
                r59 = r3
                r61 = r7
                r60 = r10
                r3 = r29
                r4 = r40
                r14 = r41
                r62 = r46
                r56 = r49
                r58 = r50
                r49 = r5
                r50 = r6
                r46 = r11
                r6 = r35
                r5 = r48
                r48 = r8
                r8 = r28
                goto Lb40
            L2c3:
                if (r2 != r4) goto L2c9
                r4 = r49
                r2 = 1
                goto L2cc
            L2c9:
                r4 = r49
                r2 = 0
            L2cc:
                int r13 = r12.getInt(r4)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L886
                r56 = r4
                r49 = r5
                long r4 = r12.getLong(r11)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L856
                r14 = r50
                r50 = r6
                java.lang.String r6 = r12.getString(r14)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L825
                r58 = r14
                r57 = r15
                long r14 = r12.getLong(r9)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L81d
                java.lang.Object r4 = r7.get(r4)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L81d
                android.os.UserHandle r4 = (android.os.UserHandle) r4     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L81d
                if (r4 != 0) goto L327
                java.lang.Long r2 = java.lang.Long.valueOf(r14)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L2ff
                r5 = r48
                r5.add(r2)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L2fd
                r48 = r5
                goto Lb8f
            L2fd:
                r0 = move-exception
                goto L302
            L2ff:
                r0 = move-exception
                r5 = r48
            L302:
                r4 = r0
                r13 = r3
                r70 = r8
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
                r8 = r46
                r46 = r56
                r40 = r58
                goto L8b7
            L327:
                r5 = r48
                r48 = r8
                android.content.ComponentName r8 = android.content.ComponentName.unflattenFromString(r6)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L7fa
                r59 = r3
                int r3 = r12.getInt(r10)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L7f8
                r53 = r3 & 1
                if (r53 != 0) goto L33c
                r53 = 1
                goto L33e
            L33c:
                r53 = 0
            L33e:
                r54 = r3 & 2
                r60 = r10
                if (r54 != 0) goto L347
                r54 = 1
                goto L349
            L347:
                r54 = 0
            L349:
                android.content.ComponentName r10 = android.content.ComponentName.unflattenFromString(r6)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L7cd
                r61 = r7
                r7 = r46
                com.android.launcher3.LauncherAppWidgetProviderInfo r10 = com.android.launcher3.LauncherModel.getProviderInfo(r7, r10, r4)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L790
                boolean r46 = com.android.launcher3.LauncherModel.isValidProvider(r10)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L790
                if (r17 != 0) goto L3cb
                if (r2 != 0) goto L3cb
                if (r54 == 0) goto L3cb
                if (r46 != 0) goto L3cb
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L3c4 java.lang.Exception -> L3c7
                r2.<init>()     // Catch: java.lang.Throwable -> L3c4 java.lang.Exception -> L3c7
                java.lang.String r3 = "Deleting widget that isn't installed anymore: id="
                r2.append(r3)     // Catch: java.lang.Throwable -> L3c4 java.lang.Exception -> L3c7
                r2.append(r14)     // Catch: java.lang.Throwable -> L3c4 java.lang.Exception -> L3c7
                java.lang.String r3 = " appWidgetId="
                r2.append(r3)     // Catch: java.lang.Throwable -> L3c4 java.lang.Exception -> L3c7
                r2.append(r13)     // Catch: java.lang.Throwable -> L3c4 java.lang.Exception -> L3c7
                java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L3c4 java.lang.Exception -> L3c7
                java.lang.String r3 = "Launcher.Model"
                android.util.Log.e(r3, r2)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L3ea
                java.lang.String r3 = "Launcher.Model"
                r4 = 0
                com.android.launcher3.Launcher.addDumpLog(r3, r2, r4)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L3ea
                java.lang.Long r2 = java.lang.Long.valueOf(r14)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L3ea
                r5.add(r2)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L3ea
                r8 = r7
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r43 = r16
                r42 = r28
                r41 = r35
                r35 = r45
                r45 = r61
                r28 = r9
                r9 = r29
                goto L29d
            L3c4:
                r0 = move-exception
                goto L229
            L3c7:
                r0 = move-exception
                r2 = r0
                r4 = r2
                goto L3ec
            L3cb:
                if (r46 == 0) goto L416
                com.android.launcher3.model.data.LauncherAppWidgetInfo r8 = new com.android.launcher3.model.data.LauncherAppWidgetInfo     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L3ea
                android.content.ComponentName r10 = r10.provider     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L3ea
                r8.<init>(r13, r10)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L3ea
                r10 = r3 & (-9)
                if (r54 != 0) goto L3de
                if (r53 == 0) goto L3dc
                r10 = 0
                goto L3de
            L3dc:
                r10 = r10 & (-3)
            L3de:
                r8.restoreStatus = r10     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L3ea
                r62 = r7
                r46 = r11
                r11 = r34
            L3e6:
                r7 = 32
                goto L4c0
            L3ea:
                r0 = move-exception
                r4 = r0
            L3ec:
                r8 = r7
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                goto L7bd
            L416:
                java.lang.String r10 = "Launcher.Model"
                r46 = r11
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L743 java.lang.Exception -> L751
                r11.<init>()     // Catch: java.lang.Throwable -> L743 java.lang.Exception -> L746
                r62 = r7
                java.lang.String r7 = "Widget restore pending id="
                r11.append(r7)     // Catch: java.lang.Exception -> L741 java.lang.Throwable -> L743
                r11.append(r14)     // Catch: java.lang.Exception -> L741 java.lang.Throwable -> L743
                java.lang.String r7 = " appWidgetId="
                r11.append(r7)     // Catch: java.lang.Exception -> L741 java.lang.Throwable -> L743
                r11.append(r13)     // Catch: java.lang.Exception -> L741 java.lang.Throwable -> L743
                java.lang.String r7 = " status ="
                r11.append(r7)     // Catch: java.lang.Exception -> L741 java.lang.Throwable -> L743
                r11.append(r3)     // Catch: java.lang.Exception -> L741 java.lang.Throwable -> L743
                java.lang.String r7 = r11.toString()     // Catch: java.lang.Exception -> L741 java.lang.Throwable -> L743
                android.util.Log.v(r10, r7)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L704
                com.android.launcher3.model.data.LauncherAppWidgetInfo r7 = new com.android.launcher3.model.data.LauncherAppWidgetInfo     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L704
                r7.<init>(r13, r8)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L704
                r7.restoreStatus = r3     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L704
                java.lang.String r10 = r8.getPackageName()     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L704
                r11 = r34
                java.lang.Object r10 = r11.get(r10)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
                java.lang.Integer r10 = (java.lang.Integer) r10     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
                r13 = r3 & 64
                if (r13 == 0) goto L45f
                int r13 = r1.mFlags     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
                r13 = r13 & 16
                if (r13 == 0) goto L45f
                r13 = 1
                goto L460
            L45f:
                r13 = 0
            L460:
                r34 = r3 & 8
                if (r34 == 0) goto L467
                if (r13 != 0) goto L467
                goto L4b3
            L467:
                if (r10 == 0) goto L471
                int r8 = r7.restoreStatus     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
                r13 = 8
                r8 = r8 | r13
                r7.restoreStatus = r8     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
                goto L4b3
            L471:
                if (r17 != 0) goto L4b3
                java.lang.String r2 = "Launcher.Model"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4ab java.lang.Exception -> L4ae
                r3.<init>()     // Catch: java.lang.Throwable -> L4ab java.lang.Exception -> L4ae
                java.lang.String r4 = "Unrestored widget removed: "
                r3.append(r4)     // Catch: java.lang.Throwable -> L4ab java.lang.Exception -> L4ae
                r3.append(r8)     // Catch: java.lang.Throwable -> L4ab java.lang.Exception -> L4ae
                java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L4ab java.lang.Exception -> L4ae
                r4 = 1
                com.android.launcher3.Launcher.addDumpLog(r2, r3, r4)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
                java.lang.Long r2 = java.lang.Long.valueOf(r14)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
                r5.add(r2)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
                r34 = r11
            L493:
                r11 = r46
                r8 = r48
                r6 = r50
                r50 = r58
                r3 = r59
                r10 = r60
                r7 = r61
                r46 = r62
                r48 = r5
                r5 = r49
                r49 = r56
                goto L233
            L4ab:
                r0 = move-exception
                goto L229
            L4ae:
                r0 = move-exception
                r2 = r0
                r4 = r2
                goto L6f9
            L4b3:
                if (r10 != 0) goto L4b7
                r8 = 0
                goto L4bb
            L4b7:
                int r8 = r10.intValue()     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
            L4bb:
                r7.installProgress = r8     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
                r8 = r7
                goto L3e6
            L4c0:
                boolean r7 = r8.hasRestoreFlag(r7)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6f7
                if (r7 == 0) goto L4da
                r10 = r45
                java.lang.String r7 = r12.getString(r10)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6c2
                boolean r13 = android.text.TextUtils.isEmpty(r7)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6c2
                if (r13 != 0) goto L4dc
                r13 = 0
                android.content.Intent r7 = android.content.Intent.parseUri(r7, r13)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6c2
                r8.bindOptions = r7     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6c2
                goto L4dc
            L4da:
                r10 = r45
            L4dc:
                r8.id = r14     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6c2
                r7 = r44
                int r13 = r12.getInt(r7)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L6a5
                r45 = r10
                r34 = r11
                long r10 = (long) r13
                r8.screenId = r10     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L686
                r10 = r41
                int r11 = r12.getInt(r10)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L651
                r8.cellX = r11     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L651
                r11 = r40
                int r13 = r12.getInt(r11)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L635
                r8.cellY = r13     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L635
                r40 = r11
                r13 = r37
                int r11 = r12.getInt(r13)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L62b
                r8.spanX = r11     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L62b
                r37 = r13
                r11 = r38
                int r13 = r12.getInt(r11)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L613
                r8.spanY = r13     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L613
                r8.user = r4     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L613
                r4 = -100
                r13 = r57
                if (r13 == r4) goto L52a
                r4 = -101(0xffffffffffffff9b, float:NaN)
                if (r13 == r4) goto L52a
                java.lang.String r2 = "Launcher.Model"
                java.lang.String r3 = "Widget found where container != CONTAINER_DESKTOP nor CONTAINER_HOTSEAT - ignoring!"
                android.util.Log.e(r2, r3)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L613
                r44 = r7
                r41 = r10
                r38 = r11
                goto L493
            L52a:
                r41 = r10
                r38 = r11
                long r10 = (long) r13
                r8.container = r10     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L686
                r4 = r36
                boolean r10 = r1.checkItemPlacement(r4, r8)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                if (r10 != 0) goto L57a
                java.lang.Long r2 = java.lang.Long.valueOf(r14)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                r5.add(r2)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                r2 = r4
                r78 = r7
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r70 = r48
                r34 = r52
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r43 = r16
                r42 = r28
                r41 = r35
                r35 = r45
                r45 = r61
                r28 = r9
                r9 = r29
                r29 = r46
                r46 = r56
                goto L1cb0
            L57a:
                if (r2 != 0) goto L5a4
                android.content.ComponentName r2 = r8.providerName     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                java.lang.String r2 = r2.flattenToString()     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                boolean r6 = r2.equals(r6)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                if (r6 == 0) goto L58c
                int r6 = r8.restoreStatus     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                if (r6 == r3) goto L5a4
            L58c:
                android.content.ContentValues r3 = new android.content.ContentValues     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                r3.<init>()     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                java.lang.String r6 = "appWidgetProvider"
                r3.put(r6, r2)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                java.lang.String r2 = "restored"
                int r6 = r8.restoreStatus     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                r3.put(r2, r6)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                r1.updateItem(r14, r3)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
            L5a4:
                com.android.launcher3.model.BgDataModel r2 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e9
                r3 = r29
                r6 = 0
                r2.addItem(r3, r8, r6)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L5e5
                r2 = r4
                r78 = r7
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r43 = r16
                r42 = r28
                r41 = r35
                r35 = r45
                r45 = r61
                r28 = r9
                r9 = r3
                r3 = r12
                goto L1cb0
            L5e5:
                r0 = move-exception
                r2 = r4
                goto Lb07
            L5e9:
                r0 = move-exception
                r2 = r4
                r78 = r7
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r70 = r48
                r34 = r52
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r4 = r0
                goto L72d
            L613:
                r0 = move-exception
                r4 = r0
                r78 = r7
                r68 = r10
                r73 = r11
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r41 = r35
                r2 = r36
                goto L668
            L62b:
                r0 = move-exception
                r4 = r0
                r78 = r7
                r68 = r10
                r3 = r12
                r37 = r13
                goto L658
            L635:
                r0 = move-exception
                r4 = r0
                r78 = r7
                r68 = r10
                r76 = r11
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r41 = r35
                r2 = r36
                r73 = r38
                r74 = r39
                goto L66c
            L651:
                r0 = move-exception
                r4 = r0
                r78 = r7
                r68 = r10
                r3 = r12
            L658:
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r41 = r35
                r2 = r36
                r73 = r38
            L668:
                r74 = r39
                r76 = r40
            L66c:
                r69 = r42
                r15 = r43
                r35 = r45
                r70 = r48
                r34 = r52
                r40 = r58
                r13 = r59
                r36 = r60
                r45 = r61
                r8 = r62
                r43 = r16
                r42 = r28
                goto L737
            L686:
                r0 = move-exception
                r4 = r0
                r78 = r7
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                goto L721
            L6a5:
                r0 = move-exception
                r4 = r0
                r78 = r7
                r7 = r11
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                goto L6de
            L6c2:
                r0 = move-exception
                r4 = r0
                r7 = r11
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
            L6de:
                r70 = r48
                r34 = r52
                r40 = r58
                r13 = r59
                r36 = r60
                r45 = r61
                r8 = r62
                r43 = r16
                r42 = r28
                r41 = r35
                r28 = r9
                r35 = r10
                goto L739
            L6f7:
                r0 = move-exception
                r4 = r0
            L6f9:
                r7 = r11
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                goto L711
            L704:
                r0 = move-exception
                r4 = r0
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
            L711:
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
            L721:
                r70 = r48
                r34 = r52
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
            L72d:
                r43 = r16
                r42 = r28
                r41 = r35
                r35 = r45
                r45 = r61
            L737:
                r28 = r9
            L739:
                r9 = r29
                r29 = r46
                r46 = r56
                goto L1ca8
            L741:
                r0 = move-exception
                goto L749
            L743:
                r0 = move-exception
                goto L229
            L746:
                r0 = move-exception
                r62 = r7
            L749:
                r3 = r29
                r4 = r36
                r7 = r44
                r2 = r0
                goto L755
            L751:
                r0 = move-exception
                r62 = r7
                goto L749
            L755:
                r78 = r7
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r43 = r16
                r42 = r28
                r41 = r35
                r35 = r45
                r45 = r61
                r28 = r9
                r9 = r3
                r3 = r12
                r82 = r4
                r4 = r2
                goto Lbd2
            L790:
                r0 = move-exception
                r62 = r7
                r4 = r0
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
            L7bd:
                r43 = r16
                r42 = r28
                r41 = r35
                r35 = r45
                r45 = r61
                r28 = r9
                r9 = r29
                goto L8cb
            L7cd:
                r0 = move-exception
                r62 = r46
                r4 = r0
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
                r70 = r48
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                goto L8b9
            L7f8:
                r0 = move-exception
                goto L7fd
            L7fa:
                r0 = move-exception
                r59 = r3
            L7fd:
                r62 = r46
                r4 = r0
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
                r70 = r48
                goto L87d
            L81d:
                r0 = move-exception
                r59 = r3
                r62 = r46
                r5 = r48
                goto L861
            L825:
                r0 = move-exception
                r59 = r3
                r62 = r46
                r5 = r48
                r4 = r0
                r70 = r8
                r3 = r12
                r79 = r24
                r6 = r33
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
                r46 = r56
                r13 = r59
                r8 = r62
                r36 = r10
                r40 = r14
                r43 = r16
                r14 = r23
                r23 = r27
                goto L8bb
            L856:
                r0 = move-exception
                r59 = r3
                r62 = r46
                r5 = r48
                r58 = r50
                r50 = r6
            L861:
                r4 = r0
                r70 = r8
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
            L87d:
                r46 = r56
                r40 = r58
                r13 = r59
                r8 = r62
                goto L8b7
            L886:
                r0 = move-exception
                r59 = r3
                r49 = r5
                r62 = r46
                r5 = r48
                r58 = r50
                r50 = r6
                r46 = r4
                r70 = r8
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
                r40 = r58
                r13 = r59
                r8 = r62
                r4 = r0
            L8b7:
                r36 = r10
            L8b9:
                r43 = r16
            L8bb:
                r42 = r28
                r41 = r35
                r35 = r45
                r45 = r7
                r28 = r9
                r9 = r29
                r7 = r34
                r34 = r52
            L8cb:
                r29 = r11
                goto L1ca8
            L8cf:
                r59 = r3
                r61 = r7
                r60 = r10
                r3 = r29
                r4 = r36
                r7 = r44
                r62 = r46
                r56 = r49
                r58 = r50
                r49 = r5
                r50 = r6
                r46 = r11
                r6 = r37
                r5 = r48
                r48 = r8
                long r10 = r12.getLong(r9)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lb03
                com.android.launcher3.model.BgDataModel r2 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lb03
                com.android.launcher3.model.data.FolderInfo r2 = r2.findOrMakeFolder(r10)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lb03
                r8 = r28
                java.lang.String r14 = r12.getString(r8)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lac6
                r2.title = r14     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lac6
                r2.id = r10     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lac6
                long r14 = (long) r15     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lac6
                r2.container = r14     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lac6
                int r14 = r12.getInt(r7)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lac6
                long r14 = (long) r14     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lac6
                r2.screenId = r14     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lac6
                r14 = r41
                int r15 = r12.getInt(r14)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La8c
                r2.cellX = r15     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La8c
                r37 = r6
                r15 = r40
                int r6 = r12.getInt(r15)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La71
                r2.cellY = r6     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La71
                r6 = 1
                r2.spanX = r6     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La71
                r2.spanY = r6     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La71
                r44 = r7
                r6 = r42
                int r7 = r12.getInt(r6)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La36
                r2.options = r7     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La36
                boolean r7 = com.lge.launcher3.util.DDTUtils.isAdditionalThemeApplied(r62)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La36
                if (r7 != 0) goto L944
                boolean r7 = com.lge.launcher3.util.DDTUtils.isAdditionalIconThemeApplied(r62)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La36
                if (r7 == 0) goto L939
                goto L944
            L939:
                java.lang.String r7 = "iconType"
                int r7 = r12.getColumnIndexOrThrow(r7)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La36
                int r7 = r12.getInt(r7)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La36
                goto L945
            L944:
                r7 = 0
            L945:
                r2.folderColor = r7     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La36
                boolean r7 = r1.checkItemPlacement(r4, r2)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La36
                if (r7 != 0) goto L99a
                if (r19 == 0) goto L97c
                java.lang.String r7 = "Launcher.Model"
                r36 = r4
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L973 java.lang.Exception -> L976
                r4.<init>()     // Catch: java.lang.Throwable -> L973 java.lang.Exception -> L976
                r42 = r6
                java.lang.String r6 = "add to restore : "
                r4.append(r6)     // Catch: java.lang.Exception -> L971 java.lang.Throwable -> L973
                r4.append(r2)     // Catch: java.lang.Exception -> L971 java.lang.Throwable -> L973
                java.lang.String r4 = r4.toString()     // Catch: java.lang.Exception -> L971 java.lang.Throwable -> L973
                com.lge.launcher3.util.LGLog.i(r7, r4)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L9aa
                com.android.launcher3.model.BgDataModel r4 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L9aa
                java.util.ArrayList<com.android.launcher3.model.data.FolderInfo> r4 = com.android.launcher3.model.BgDataModel.invalidFoldersToRestore     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L9aa
                r4.add(r2)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L9aa
                goto L99e
            L971:
                r0 = move-exception
                goto L979
            L973:
                r0 = move-exception
                goto L229
            L976:
                r0 = move-exception
                r42 = r6
            L979:
                r2 = r0
                r4 = r2
                goto L9ac
            L97c:
                r36 = r4
                r42 = r6
                java.lang.Long r2 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L9aa
                r5.add(r2)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L9aa
                r28 = r9
                r68 = r14
                r76 = r15
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r41 = r35
                goto L9da
            L99a:
                r36 = r4
                r42 = r6
            L99e:
                if (r13 == 0) goto L9c0
                java.lang.Long r4 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L9aa
                r6 = r35
                r6.add(r4)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La02
                goto L9c2
            L9aa:
                r0 = move-exception
                r4 = r0
            L9ac:
                r28 = r9
                r68 = r14
                r76 = r15
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r41 = r35
                goto La16
            L9c0:
                r6 = r35
            L9c2:
                com.android.launcher3.model.BgDataModel r4 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La02
                r7 = 0
                r4.addItem(r3, r2, r7)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> La02
                r41 = r6
                r28 = r9
                r68 = r14
                r76 = r15
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
            L9da:
                r2 = r36
                r73 = r38
                r74 = r39
                r69 = r42
                r15 = r43
                r78 = r44
                r35 = r45
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r45 = r61
                r9 = r3
                r42 = r8
                r3 = r12
                r43 = r16
                r8 = r62
                goto L1cb0
            La02:
                r0 = move-exception
                r4 = r0
                r41 = r6
                r28 = r9
                r68 = r14
                r76 = r15
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
            La16:
                r2 = r36
                r73 = r38
                r74 = r39
                r69 = r42
                r15 = r43
                r78 = r44
                r35 = r45
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r45 = r61
                goto Labf
            La36:
                r0 = move-exception
                r2 = r4
                r69 = r6
                r42 = r8
                r28 = r9
                r68 = r14
                r76 = r15
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r41 = r35
                r73 = r38
                r74 = r39
                r15 = r43
                r78 = r44
                r35 = r45
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r45 = r61
                r8 = r62
                r4 = r0
                r9 = r3
                r3 = r12
                r43 = r16
                goto L1ca8
            La71:
                r0 = move-exception
                r2 = r4
                r78 = r7
                r28 = r9
                r68 = r14
                r76 = r15
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r41 = r35
                r73 = r38
                r74 = r39
                goto Laa8
            La8c:
                r0 = move-exception
                r2 = r4
                r37 = r6
                r78 = r7
                r28 = r9
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r41 = r35
                r73 = r38
                r74 = r39
                r76 = r40
            Laa8:
                r69 = r42
                r15 = r43
                r35 = r45
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r45 = r61
                r4 = r0
            Labf:
                r9 = r3
                r42 = r8
                r3 = r12
                r43 = r16
                goto Laff
            Lac6:
                r0 = move-exception
                r2 = r4
                r37 = r6
                r78 = r7
                r28 = r9
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r4 = r0
                r9 = r3
                r42 = r8
                r3 = r12
                r43 = r16
                r41 = r35
                r35 = r45
                r45 = r61
            Laff:
                r8 = r62
                goto L1ca8
            Lb03:
                r0 = move-exception
                r2 = r4
                r37 = r6
            Lb07:
                r78 = r7
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r4 = r0
                r43 = r16
                r42 = r28
                r41 = r35
                r35 = r45
                r45 = r61
                r28 = r9
                r9 = r3
                r3 = r12
                goto L1ca8
            Lb40:
                long r10 = r12.getLong(r9)     // Catch: java.lang.Exception -> L1c2e java.lang.Throwable -> L200d
                r29 = r3
                r7 = r45
                java.lang.String r3 = r12.getString(r7)     // Catch: java.lang.Exception -> L1bf7 java.lang.Throwable -> L200d
                r40 = r4
                r45 = r7
                r4 = r46
                int r7 = r12.getInt(r4)     // Catch: java.lang.Exception -> L1bc2 java.lang.Throwable -> L200d
                r35 = r8
                r28 = r9
                long r8 = (long) r7
                r7 = r61
                java.lang.Object r41 = r7.get(r8)     // Catch: java.lang.Exception -> L1b88 java.lang.Throwable -> L200d
                r46 = r4
                r4 = r41
                android.os.UserHandle r4 = (android.os.UserHandle) r4     // Catch: java.lang.Exception -> L1b5b java.lang.Throwable -> L200d
                r41 = r2
                r61 = r7
                r2 = r60
                int r7 = r12.getInt(r2)     // Catch: java.lang.Exception -> L1b1f java.lang.Throwable -> L200d
                if (r4 != 0) goto Lbd6
                java.lang.Long r3 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lb99
                r5.add(r3)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lb99
                r10 = r2
                r41 = r14
                r9 = r28
                r28 = r35
                r11 = r46
                r8 = r48
                r3 = r59
                r7 = r61
                r46 = r62
                r48 = r5
                r35 = r6
            Lb8f:
                r5 = r49
                r6 = r50
                r49 = r56
            Lb95:
                r50 = r58
                goto L233
            Lb99:
                r0 = move-exception
                r4 = r0
                r41 = r6
                r3 = r12
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r8 = r62
                r43 = r16
                r42 = r35
                r35 = r45
                r45 = r61
                r82 = r36
                r36 = r2
            Lbd2:
                r2 = r82
                goto L1ca8
            Lbd6:
                r60 = r2
                r2 = 0
                android.content.Intent r57 = android.content.Intent.parseUri(r3, r2)     // Catch: java.lang.Exception -> L1a8a java.net.URISyntaxException -> L1ac4 java.lang.Throwable -> L200d
                android.content.ComponentName r2 = r57.getComponent()     // Catch: java.lang.Exception -> L1a8a java.net.URISyntaxException -> L1ac4 java.lang.Throwable -> L200d
                if (r2 == 0) goto L13ca
                java.lang.String r63 = r2.getPackageName()     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L1354 java.net.URISyntaxException -> L1393
                if (r63 == 0) goto L13ca
                r63 = r3
                android.os.UserHandle r3 = android.os.Process.myUserHandle()     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L1340 java.net.URISyntaxException -> L134b
                boolean r3 = r3.equals(r4)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> L1340 java.net.URISyntaxException -> L134b
                r22 = 1
                r3 = r3 ^ 1
                r64 = r8
                android.content.pm.PackageManager r8 = r62.getPackageManager()     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lc11 android.content.pm.PackageManager.NameNotFoundException -> Lc44 java.net.URISyntaxException -> L1ac6
                r9 = 0
                android.content.pm.ActivityInfo r8 = r8.getActivityInfo(r2, r9)     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lc11 android.content.pm.PackageManager.NameNotFoundException -> Lc44 java.net.URISyntaxException -> L1ac6
                if (r8 == 0) goto Lc0a
                boolean r8 = r8.exported     // Catch: java.lang.Throwable -> L228 java.lang.Exception -> Lc11 android.content.pm.PackageManager.NameNotFoundException -> Lc44 java.net.URISyntaxException -> L1ac6
                if (r8 == 0) goto Lc0a
                r8 = 1
                goto Lc0b
            Lc0a:
                r8 = 0
            Lc0b:
                r66 = r12
                r67 = r14
                r12 = r8
                goto Lc6a
            Lc11:
                r0 = move-exception
                r4 = r0
                r41 = r6
                r3 = r12
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                goto L1389
            Lc44:
                java.lang.String r8 = "Launcher.Model"
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L12b9 java.lang.Exception -> L12bd java.net.URISyntaxException -> L12c9
                r9.<init>()     // Catch: java.lang.Throwable -> L12b9 java.lang.Exception -> L12bd java.net.URISyntaxException -> L12c9
                r66 = r12
                java.lang.String r12 = "Cannot find "
                r9.append(r12)     // Catch: java.lang.Throwable -> L129c java.lang.Exception -> L12a1 java.net.URISyntaxException -> L12ad
                r9.append(r2)     // Catch: java.lang.Throwable -> L129c java.lang.Exception -> L12a1 java.net.URISyntaxException -> L12ad
                java.lang.String r12 = ". exportedActivity is false. restored = "
                r9.append(r12)     // Catch: java.lang.Throwable -> L129c java.lang.Exception -> L12a1 java.net.URISyntaxException -> L12ad
                r9.append(r13)     // Catch: java.lang.Throwable -> L129c java.lang.Exception -> L12a1 java.net.URISyntaxException -> L12ad
                java.lang.String r9 = r9.toString()     // Catch: java.lang.Throwable -> L129c java.lang.Exception -> L12a1 java.net.URISyntaxException -> L12ad
                r67 = r14
                r12 = 0
                int[] r14 = new int[r12]     // Catch: java.lang.Exception -> L123a java.net.URISyntaxException -> L126c java.lang.Throwable -> L1722
                com.lge.launcher3.util.LGLog.w(r8, r9, r14)     // Catch: java.lang.Exception -> L123a java.net.URISyntaxException -> L126c java.lang.Throwable -> L1722
                r12 = 0
            Lc6a:
                java.lang.String r8 = r2.getPackageName()     // Catch: java.lang.Exception -> L123a java.net.URISyntaxException -> L126c java.lang.Throwable -> L1722
                r14 = r27
                boolean r8 = r14.isPackageEnabledForProfile(r8, r4)     // Catch: java.lang.Exception -> L11c0 java.net.URISyntaxException -> L11fe java.lang.Throwable -> L1722
                if (r8 == 0) goto Lc82
                boolean r9 = r14.isActivityEnabledForProfile(r2, r4)     // Catch: java.lang.Exception -> L11c0 java.net.URISyntaxException -> L11fe java.lang.Throwable -> L1722
                if (r9 == 0) goto Lc82
                if (r3 != 0) goto Lc80
                if (r12 == 0) goto Lc82
            Lc80:
                r12 = 1
                goto Lc83
            Lc82:
                r12 = 0
            Lc83:
                if (r8 == 0) goto Lc8a
                java.lang.String r3 = r2.getPackageName()     // Catch: java.lang.Exception -> L11c0 java.net.URISyntaxException -> L11fe java.lang.Throwable -> L1722
                goto Lc8b
            Lc8a:
                r3 = 0
            Lc8b:
                if (r12 != 0) goto L110d
                if (r8 == 0) goto Lcef
                r9 = r62
                boolean r12 = com.lge.launcher3.util.PackageUtils.isIncludeValidPackage(r9, r3)     // Catch: java.lang.Exception -> Lc9b java.net.URISyntaxException -> Lcc6 java.lang.Throwable -> L1722
                if (r12 == 0) goto Lcf1
                r12 = r23
                goto L1111
            Lc9b:
                r0 = move-exception
                r4 = r0
                r41 = r6
                r8 = r9
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                goto L11ea
            Lcc6:
                r41 = r6
                r8 = r9
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                goto L1226
            Lcef:
                r9 = r62
            Lcf1:
                if (r8 == 0) goto Ld96
                r8 = r7 & 2
                if (r8 == 0) goto Ld23
                java.lang.String r8 = r2.getPackageName()     // Catch: java.lang.Exception -> Lc9b java.net.URISyntaxException -> Lcc6 java.lang.Throwable -> L1722
                r12 = r23
                android.content.Intent r8 = r12.getLaunchIntentForPackage(r8)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                if (r8 == 0) goto Ld1c
                android.content.ContentValues r13 = new android.content.ContentValues     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r13.<init>()     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r23 = r3
                java.lang.String r3 = "intent"
                r62 = r4
                r27 = r15
                r15 = 0
                java.lang.String r4 = r8.toUri(r15)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r13.put(r3, r4)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r1.updateItem(r10, r13)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                goto Ld2c
            Ld1c:
                r23 = r3
                r62 = r4
                r27 = r15
                goto Ld2c
            Ld23:
                r62 = r4
                r27 = r15
                r12 = r23
                r23 = r3
                r8 = 0
            Ld2c:
                if (r8 != 0) goto Ld78
                java.lang.String r3 = "Launcher.Model"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Ld70 java.lang.Exception -> Ld73 java.net.URISyntaxException -> L10de
                r4.<init>()     // Catch: java.lang.Throwable -> Ld70 java.lang.Exception -> Ld73 java.net.URISyntaxException -> L10de
                java.lang.String r7 = "Invalid component removed: "
                r4.append(r7)     // Catch: java.lang.Throwable -> Ld70 java.lang.Exception -> Ld73 java.net.URISyntaxException -> L10de
                r4.append(r2)     // Catch: java.lang.Throwable -> Ld70 java.lang.Exception -> Ld73 java.net.URISyntaxException -> L10de
                java.lang.String r2 = r4.toString()     // Catch: java.lang.Throwable -> Ld70 java.lang.Exception -> Ld73 java.net.URISyntaxException -> L10de
                r4 = 1
                com.android.launcher3.Launcher.addDumpLog(r3, r2, r4)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                java.lang.Long r2 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r5.add(r2)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
            Ld4c:
                r23 = r12
                r27 = r14
                r11 = r46
                r8 = r48
                r3 = r59
                r10 = r60
                r7 = r61
                r12 = r66
                r41 = r67
            Ld5e:
                r48 = r5
                r46 = r9
                r9 = r28
                r28 = r35
                r5 = r49
                r49 = r56
                r35 = r6
                r6 = r50
                goto Lb95
            Ld70:
                r0 = move-exception
                goto L1723
            Ld73:
                r0 = move-exception
            Ld74:
                r2 = r0
                r4 = r2
                goto L10af
            Ld78:
                java.lang.Long r2 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r6.add(r2)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r57 = r8
                r3 = r27
                r2 = r52
                r4 = r62
                r15 = r66
                r13 = 0
                r27 = 0
                r47 = 0
            Ld8e:
                r55 = 0
            Ld90:
                r62 = r9
                r8 = r64
                goto L11ad
            Ld96:
                r62 = r4
                r27 = r15
                r12 = r23
                r23 = r3
                if (r13 == 0) goto Lf27
                if (r19 == 0) goto Lde2
                java.lang.String r3 = r2.getPackageName()     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r4 = r30
                boolean r3 = r4.containsKey(r3)     // Catch: java.lang.Exception -> Ldaf java.net.URISyntaxException -> Lf23 java.lang.Throwable -> L1722
                r15 = r34
                goto Ldee
            Ldaf:
                r0 = move-exception
                r30 = r4
                r41 = r6
                r8 = r9
                r23 = r14
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r3 = r66
                r68 = r67
                r4 = r0
                goto L115a
            Lde2:
                r4 = r30
                java.lang.String r3 = r2.getPackageName()     // Catch: java.lang.Exception -> Lf1e java.net.URISyntaxException -> Lf23 java.lang.Throwable -> L1722
                r15 = r34
                boolean r3 = r15.containsKey(r3)     // Catch: java.lang.Exception -> Lefc java.net.URISyntaxException -> Lf0e java.lang.Throwable -> L1722
            Ldee:
                java.lang.String r8 = "Launcher.Model"
                r30 = r4
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lef0 java.lang.Exception -> Lef3 java.net.URISyntaxException -> Lef8
                r4.<init>()     // Catch: java.lang.Throwable -> Lef0 java.lang.Exception -> Lef3 java.net.URISyntaxException -> Lef8
                r34 = r15
                java.lang.String r15 = "package not yet restored: "
                r4.append(r15)     // Catch: java.lang.Exception -> Leed java.lang.Throwable -> Lef0 java.net.URISyntaxException -> L10de
                r4.append(r2)     // Catch: java.lang.Exception -> Leed java.lang.Throwable -> Lef0 java.net.URISyntaxException -> L10de
                java.lang.String r15 = ", isInstallingPkgs = "
                r4.append(r15)     // Catch: java.lang.Exception -> Leed java.lang.Throwable -> Lef0 java.net.URISyntaxException -> L10de
                r4.append(r3)     // Catch: java.lang.Exception -> Leed java.lang.Throwable -> Lef0 java.net.URISyntaxException -> L10de
                java.lang.String r15 = ", promiseType = "
                r4.append(r15)     // Catch: java.lang.Exception -> Leed java.lang.Throwable -> Lef0 java.net.URISyntaxException -> L10de
                r4.append(r7)     // Catch: java.lang.Exception -> Leed java.lang.Throwable -> Lef0 java.net.URISyntaxException -> L10de
                java.lang.String r4 = r4.toString()     // Catch: java.lang.Exception -> Leed java.lang.Throwable -> Lef0 java.net.URISyntaxException -> L10de
                r15 = 1
                com.android.launcher3.Launcher.addDumpLog(r8, r4, r15)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r4 = r7 & 256(0x100, float:3.59E-43)
                if (r4 == 0) goto Le25
                int r4 = r1.mFlags     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r4 = r4 & 16
                if (r4 == 0) goto Le25
                r4 = 1
                goto Le26
            Le25:
                r4 = 0
            Le26:
                r8 = r7 & 8
                if (r8 == 0) goto Le2e
                if (r4 != 0) goto Le2e
            Le2c:
                r2 = 0
                goto Le93
            Le2e:
                if (r3 == 0) goto Le68
                java.lang.String r3 = "Launcher.Model"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Le62 java.lang.Exception -> Le65 java.net.URISyntaxException -> L10de
                r4.<init>()     // Catch: java.lang.Throwable -> Le62 java.lang.Exception -> Le65 java.net.URISyntaxException -> L10de
                java.lang.String r8 = "package is in installingPkgs = "
                r4.append(r8)     // Catch: java.lang.Throwable -> Le62 java.lang.Exception -> Le65 java.net.URISyntaxException -> L10de
                r4.append(r2)     // Catch: java.lang.Throwable -> Le62 java.lang.Exception -> Le65 java.net.URISyntaxException -> L10de
                java.lang.String r2 = ", promiseType = "
                r4.append(r2)     // Catch: java.lang.Throwable -> Le62 java.lang.Exception -> Le65 java.net.URISyntaxException -> L10de
                r4.append(r7)     // Catch: java.lang.Throwable -> Le62 java.lang.Exception -> Le65 java.net.URISyntaxException -> L10de
                java.lang.String r2 = r4.toString()     // Catch: java.lang.Throwable -> Le62 java.lang.Exception -> Le65 java.net.URISyntaxException -> L10de
                com.lge.launcher3.util.LGLog.d(r3, r2)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r7 = r7 | 8
                android.content.ContentValues r2 = new android.content.ContentValues     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r2.<init>()     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                java.lang.String r3 = "restored"
                java.lang.Integer r4 = java.lang.Integer.valueOf(r7)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r2.put(r3, r4)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r1.updateItem(r10, r2)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                goto Le2c
            Le62:
                r0 = move-exception
                goto L1723
            Le65:
                r0 = move-exception
                goto Ld74
            Le68:
                r3 = r7 & 240(0xf0, float:3.36E-43)
                if (r3 == 0) goto Lec7
                int r3 = com.android.launcher3.CommonAppTypeParser.decodeItemTypeFromFlag(r7)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                com.android.launcher3.CommonAppTypeParser r4 = new com.android.launcher3.CommonAppTypeParser     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r4.<init>(r10, r3, r9)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                boolean r3 = r4.findDefaultApp()     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                if (r3 == 0) goto Lea1
                android.content.Intent r2 = r4.parsedIntent     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r2.getComponent()     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                android.content.ContentValues r3 = r4.parsedValues     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                java.lang.String r4 = "restored"
                r8 = 0
                java.lang.Integer r13 = java.lang.Integer.valueOf(r8)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r3.put(r4, r13)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r1.updateItem(r10, r3)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r57 = r2
                r2 = 1
                r13 = 0
            Le93:
                r47 = r2
                r3 = r27
                r2 = r52
                r4 = r62
                r15 = r66
                r27 = 0
                goto Ld8e
            Lea1:
                java.lang.String r3 = "Launcher.Model"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lec1 java.lang.Exception -> Lec4 java.net.URISyntaxException -> L10de
                r4.<init>()     // Catch: java.lang.Throwable -> Lec1 java.lang.Exception -> Lec4 java.net.URISyntaxException -> L10de
                java.lang.String r7 = "Unrestored package removed: "
                r4.append(r7)     // Catch: java.lang.Throwable -> Lec1 java.lang.Exception -> Lec4 java.net.URISyntaxException -> L10de
                r4.append(r2)     // Catch: java.lang.Throwable -> Lec1 java.lang.Exception -> Lec4 java.net.URISyntaxException -> L10de
                java.lang.String r2 = r4.toString()     // Catch: java.lang.Throwable -> Lec1 java.lang.Exception -> Lec4 java.net.URISyntaxException -> L10de
                r4 = 1
                com.android.launcher3.Launcher.addDumpLog(r3, r2, r4)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                java.lang.Long r2 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r5.add(r2)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                goto Ld4c
            Lec1:
                r0 = move-exception
                goto L1723
            Lec4:
                r0 = move-exception
                goto Ld74
            Lec7:
                java.lang.String r3 = "Launcher.Model"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lee7 java.lang.Exception -> Leea java.net.URISyntaxException -> L10de
                r4.<init>()     // Catch: java.lang.Throwable -> Lee7 java.lang.Exception -> Leea java.net.URISyntaxException -> L10de
                java.lang.String r7 = "Unrestored package removed: "
                r4.append(r7)     // Catch: java.lang.Throwable -> Lee7 java.lang.Exception -> Leea java.net.URISyntaxException -> L10de
                r4.append(r2)     // Catch: java.lang.Throwable -> Lee7 java.lang.Exception -> Leea java.net.URISyntaxException -> L10de
                java.lang.String r2 = r4.toString()     // Catch: java.lang.Throwable -> Lee7 java.lang.Exception -> Leea java.net.URISyntaxException -> L10de
                r4 = 1
                com.android.launcher3.Launcher.addDumpLog(r3, r2, r4)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                java.lang.Long r2 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r5.add(r2)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                goto Ld4c
            Lee7:
                r0 = move-exception
                goto L1723
            Leea:
                r0 = move-exception
                goto Ld74
            Leed:
                r0 = move-exception
                goto Ld74
            Lef0:
                r0 = move-exception
                goto L1723
            Lef3:
                r0 = move-exception
                r34 = r15
                goto Ld74
            Lef8:
                r34 = r15
                goto L10de
            Lefc:
                r0 = move-exception
                r30 = r4
                r4 = r0
                r41 = r6
                r8 = r9
                r23 = r14
                r7 = r15
                r79 = r24
                r9 = r29
                r6 = r33
                goto L10bc
            Lf0e:
                r30 = r4
                r41 = r6
                r8 = r9
                r23 = r14
                r7 = r15
                r79 = r24
                r9 = r29
                r6 = r33
                goto L10eb
            Lf1e:
                r0 = move-exception
                r30 = r4
                goto L10ae
            Lf23:
                r30 = r4
                goto L10de
            Lf27:
                java.lang.String r3 = r2.getPackageName()     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r4 = 8192(0x2000, float:1.148E-41)
                boolean r3 = r14.isAppEnabled(r12, r3, r4)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                if (r3 == 0) goto Lf4d
                int r3 = r62.getIdentifier()     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                boolean r3 = com.lge.launcher3.util.UserUtils.isSecondApplication(r9, r3)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                if (r3 != 0) goto Lf4d
                r3 = r27
                r2 = r52
                r4 = r62
                r15 = r66
                r27 = 1
                r47 = 0
                r55 = 2
                goto Ld90
            Lf4d:
                if (r18 != 0) goto Lfa0
                java.lang.String r3 = "Launcher.Model"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lf9a java.lang.Exception -> Lf9d java.net.URISyntaxException -> L10de
                r4.<init>()     // Catch: java.lang.Throwable -> Lf9a java.lang.Exception -> Lf9d java.net.URISyntaxException -> L10de
                java.lang.String r8 = "Invalid package: "
                r4.append(r8)     // Catch: java.lang.Throwable -> Lf9a java.lang.Exception -> Lf9d java.net.URISyntaxException -> L10de
                r4.append(r2)     // Catch: java.lang.Throwable -> Lf9a java.lang.Exception -> Lf9d java.net.URISyntaxException -> L10de
                java.lang.String r8 = " (check again later)"
                r4.append(r8)     // Catch: java.lang.Throwable -> Lf9a java.lang.Exception -> Lf9d java.net.URISyntaxException -> L10de
                java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> Lf9a java.lang.Exception -> Lf9d java.net.URISyntaxException -> L10de
                r8 = 1
                com.android.launcher3.Launcher.addDumpLog(r3, r4, r8)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                java.util.HashMap<android.os.UserHandle, java.util.HashSet<java.lang.String>> r3 = com.android.launcher3.LauncherModel.sPendingPackages     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r4 = r62
                java.lang.Object r3 = r3.get(r4)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                java.util.HashSet r3 = (java.util.HashSet) r3     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                if (r3 != 0) goto Lf81
                java.util.HashSet r3 = new java.util.HashSet     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r3.<init>()     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                java.util.HashMap<android.os.UserHandle, java.util.HashSet<java.lang.String>> r8 = com.android.launcher3.LauncherModel.sPendingPackages     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r8.put(r4, r3)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
            Lf81:
                java.lang.String r2 = r2.getPackageName()     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r3.add(r2)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r62 = r9
                r3 = r27
                r2 = r52
                r8 = r64
                r15 = r66
                r27 = 1
                r47 = 0
                r55 = 2
                goto L11ad
            Lf9a:
                r0 = move-exception
                goto L1723
            Lf9d:
                r0 = move-exception
                goto Ld74
            Lfa0:
                java.lang.String r3 = "Launcher.Model"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L1025 java.lang.Exception -> L102a java.net.URISyntaxException -> L1033
                r4.<init>()     // Catch: java.lang.Throwable -> L1025 java.lang.Exception -> L102a java.net.URISyntaxException -> L1033
                java.lang.String r7 = "Invalid package removed: "
                r4.append(r7)     // Catch: java.lang.Throwable -> L1025 java.lang.Exception -> L102a java.net.URISyntaxException -> L1033
                r4.append(r2)     // Catch: java.lang.Throwable -> L1025 java.lang.Exception -> L102a java.net.URISyntaxException -> L1033
                java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> L1025 java.lang.Exception -> L102a java.net.URISyntaxException -> L1033
                r7 = 1
                com.android.launcher3.Launcher.addDumpLog(r3, r4, r7)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                java.lang.Long r3 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r5.add(r3)     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r3 = r27
                r4 = -101(0xffffffffffffff9b, float:NaN)
                if (r3 != r4) goto L100a
                java.lang.String r3 = "Launcher.Model"
                java.lang.String r2 = r2.getPackageName()     // Catch: java.lang.Exception -> L10ad java.net.URISyntaxException -> L10de java.lang.Throwable -> L1722
                r15 = r66
                r8 = r67
                int r4 = r15.getInt(r8)     // Catch: java.lang.Exception -> L112c java.net.URISyntaxException -> L115d java.lang.Throwable -> L13e7
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lfff java.lang.Exception -> L1002 java.net.URISyntaxException -> L1006
                r7.<init>()     // Catch: java.lang.Throwable -> Lfff java.lang.Exception -> L1002 java.net.URISyntaxException -> L1006
                java.lang.String r10 = "[HOTSEAT_INFO] itemsToRemove : "
                r7.append(r10)     // Catch: java.lang.Throwable -> Lfff java.lang.Exception -> L1002 java.net.URISyntaxException -> L1006
                r7.append(r2)     // Catch: java.lang.Throwable -> Lfff java.lang.Exception -> L1002 java.net.URISyntaxException -> L1006
                java.lang.String r2 = " in Hotseat index : "
                r7.append(r2)     // Catch: java.lang.Throwable -> Lfff java.lang.Exception -> L1002 java.net.URISyntaxException -> L1006
                r7.append(r4)     // Catch: java.lang.Throwable -> Lfff java.lang.Exception -> L1002 java.net.URISyntaxException -> L1006
                java.lang.String r2 = r7.toString()     // Catch: java.lang.Throwable -> Lfff java.lang.Exception -> L1002 java.net.URISyntaxException -> L1006
                com.lge.launcher3.util.LGLog.i(r3, r2)     // Catch: java.lang.Exception -> L112c java.net.URISyntaxException -> L115d java.lang.Throwable -> L13e7
                int r2 = r15.getInt(r8)     // Catch: java.lang.Exception -> L112c java.net.URISyntaxException -> L115d java.lang.Throwable -> L13e7
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: java.lang.Exception -> L112c java.net.URISyntaxException -> L115d java.lang.Throwable -> L13e7
                r3 = r16
                r3.add(r2)     // Catch: java.lang.Exception -> Lffc java.net.URISyntaxException -> L1074 java.lang.Throwable -> L13e7
                goto L1010
            Lffc:
                r0 = move-exception
                r4 = r0
                goto L103b
            Lfff:
                r0 = move-exception
                goto L13e8
            L1002:
                r0 = move-exception
                r3 = r16
                goto L1031
            L1006:
                r3 = r16
                goto L1074
            L100a:
                r3 = r16
                r15 = r66
                r8 = r67
            L1010:
                r16 = r3
                r41 = r8
                r23 = r12
                r27 = r14
                r12 = r15
                r11 = r46
                r8 = r48
                r3 = r59
                r10 = r60
                r7 = r61
                goto Ld5e
            L1025:
                r0 = move-exception
                r15 = r66
                goto L13e8
            L102a:
                r0 = move-exception
                r3 = r16
                r15 = r66
                r8 = r67
            L1031:
                r2 = r0
                goto L103a
            L1033:
                r3 = r16
                r15 = r66
                r8 = r67
                goto L1074
            L103a:
                r4 = r2
            L103b:
                r41 = r6
                r68 = r8
                r8 = r9
                r23 = r14
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r14 = r12
                r42 = r35
                r35 = r45
                r45 = r61
                r82 = r43
                r43 = r3
                r3 = r15
                r15 = r82
                goto L1ca8
            L1074:
                r41 = r6
                r68 = r8
                r8 = r9
                r23 = r14
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r14 = r12
                r42 = r35
                r35 = r45
                r45 = r61
                r82 = r43
                r43 = r3
                r3 = r15
                r15 = r82
                goto L1afd
            L10ad:
                r0 = move-exception
            L10ae:
                r4 = r0
            L10af:
                r41 = r6
                r8 = r9
                r23 = r14
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
            L10bc:
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r3 = r66
                r68 = r67
                goto L115a
            L10de:
                r41 = r6
                r8 = r9
                r23 = r14
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
            L10eb:
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r3 = r66
                r68 = r67
                goto L1189
            L110d:
                r12 = r23
                r9 = r62
            L1111:
                r8 = r67
                r23 = r3
                r3 = r15
                r15 = r66
                if (r13 == 0) goto L118c
                java.lang.Long r2 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Exception -> L112c java.net.URISyntaxException -> L115d java.lang.Throwable -> L13e7
                r6.add(r2)     // Catch: java.lang.Exception -> L112c java.net.URISyntaxException -> L115d java.lang.Throwable -> L13e7
                r67 = r8
                r62 = r9
                r2 = r52
                r8 = r64
                r13 = 0
                goto L1194
            L112c:
                r0 = move-exception
                r4 = r0
                r41 = r6
                r68 = r8
                r8 = r9
                r23 = r14
                r3 = r15
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
            L115a:
                r14 = r12
                goto L1389
            L115d:
                r41 = r6
                r68 = r8
                r8 = r9
                r23 = r14
                r3 = r15
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
            L1189:
                r14 = r12
                goto L1af5
            L118c:
                r67 = r8
                r62 = r9
                r2 = r52
                r8 = r64
            L1194:
                java.lang.Object r27 = r2.get(r8)     // Catch: java.lang.Exception -> L11bc java.net.URISyntaxException -> L130a java.lang.Throwable -> L13e7
                java.lang.Boolean r27 = (java.lang.Boolean) r27     // Catch: java.lang.Exception -> L11bc java.net.URISyntaxException -> L130a java.lang.Throwable -> L13e7
                boolean r27 = r27.booleanValue()     // Catch: java.lang.Exception -> L11bc java.net.URISyntaxException -> L130a java.lang.Throwable -> L13e7
                if (r27 == 0) goto L11a7
                r27 = 0
                r47 = 0
                r55 = 8
                goto L11ad
            L11a7:
                r27 = 0
                r47 = 0
                r55 = 0
            L11ad:
                r52 = r27
                r27 = r13
                r13 = r57
                r82 = r23
                r23 = r2
                r2 = r7
                r7 = r82
                goto L146a
            L11bc:
                r0 = move-exception
                r4 = r0
                goto L12d4
            L11c0:
                r0 = move-exception
                r4 = r0
                r41 = r6
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
            L11ea:
                r3 = r66
                r68 = r67
                r43 = r16
                r42 = r35
                r35 = r45
                r45 = r61
                r82 = r23
                r23 = r14
                r14 = r82
                goto L1ca8
            L11fe:
                r41 = r6
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
            L1226:
                r3 = r66
                r68 = r67
                r43 = r16
                r42 = r35
                r35 = r45
                r45 = r61
                r82 = r23
                r23 = r14
                r14 = r82
                goto L1afd
            L123a:
                r0 = move-exception
                r4 = r0
                r41 = r6
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r3 = r66
                goto L1387
            L126c:
                r41 = r6
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r3 = r66
                goto L13c6
            L129c:
                r0 = move-exception
                r15 = r66
                goto L13e8
            L12a1:
                r0 = move-exception
                r67 = r14
                r12 = r23
                r14 = r27
                r2 = r52
                r15 = r66
                goto L12c7
            L12ad:
                r67 = r14
                r12 = r23
                r14 = r27
                r2 = r52
                r15 = r66
                goto L130a
            L12b9:
                r0 = move-exception
                r15 = r12
                goto L13e8
            L12bd:
                r0 = move-exception
                r15 = r12
                r67 = r14
                r12 = r23
                r14 = r27
                r2 = r52
            L12c7:
                r3 = r0
                goto L12d3
            L12c9:
                r15 = r12
                r67 = r14
                r12 = r23
                r14 = r27
                r2 = r52
                goto L130a
            L12d3:
                r4 = r3
            L12d4:
                r41 = r6
                r23 = r14
                r3 = r15
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r46 = r56
                r40 = r58
                r13 = r59
                r8 = r62
                r68 = r67
                r34 = r2
                r14 = r12
                r43 = r16
                r42 = r35
                r2 = r36
                r35 = r45
                r36 = r60
                goto L138f
            L130a:
                r41 = r6
                r23 = r14
                r3 = r15
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r46 = r56
                r40 = r58
                r13 = r59
                r8 = r62
                r68 = r67
                r34 = r2
                r14 = r12
                r43 = r16
                r42 = r35
                r2 = r36
                r35 = r45
                r36 = r60
                goto L1afb
            L1340:
                r0 = move-exception
                r15 = r12
                r67 = r14
                r4 = r0
                r41 = r6
                r3 = r15
                r14 = r23
                goto L135f
            L134b:
                r15 = r12
                r67 = r14
                r41 = r6
                r3 = r15
                r14 = r23
                goto L139e
            L1354:
                r0 = move-exception
                r15 = r12
                r67 = r14
                r12 = r23
                r4 = r0
                r41 = r6
                r14 = r12
                r3 = r15
            L135f:
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
            L1387:
                r68 = r67
            L1389:
                r43 = r16
                r42 = r35
                r35 = r45
            L138f:
                r45 = r61
                goto L1ca8
            L1393:
                r63 = r3
                r15 = r12
                r67 = r14
                r12 = r23
                r41 = r6
                r14 = r12
                r3 = r15
            L139e:
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
            L13c6:
                r68 = r67
                goto L1af5
            L13ca:
                r63 = r3
                r67 = r14
                r3 = r15
                r14 = r27
                r15 = r12
                r12 = r23
                r23 = r52
                if (r2 != 0) goto L145e
                java.lang.Long r2 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Throwable -> L13e7 java.lang.Exception -> L13ec java.net.URISyntaxException -> L1426
                r6.add(r2)     // Catch: java.lang.Throwable -> L13e7 java.lang.Exception -> L13ec java.net.URISyntaxException -> L1426
                r2 = r7
                r13 = r57
                r7 = 0
                r27 = 0
                goto L1464
            L13e7:
                r0 = move-exception
            L13e8:
                r2 = r0
                r3 = r15
                goto L2011
            L13ec:
                r0 = move-exception
                r4 = r0
                r41 = r6
                r3 = r15
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
            L13ff:
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r68 = r67
                r43 = r16
                r34 = r23
                r42 = r35
                r35 = r45
                r45 = r61
                r23 = r14
                r14 = r12
                goto L1ca8
            L1426:
                r41 = r6
                r3 = r15
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r68 = r67
                r43 = r16
                r34 = r23
                r42 = r35
                r35 = r45
                r45 = r61
                r23 = r14
                r14 = r12
                goto L1afd
            L145e:
                r2 = r7
                r27 = r13
                r13 = r57
                r7 = 0
            L1464:
                r47 = 0
                r52 = 0
                r55 = 0
            L146a:
                if (r3 < 0) goto L1492
                r57 = r3
                r64 = r8
                r3 = r39
                int r8 = r15.getInt(r3)     // Catch: java.lang.Throwable -> L13e7 java.lang.Exception -> L147d
                r9 = 9
                if (r8 < r9) goto L1498
                r39 = 1
                goto L149a
            L147d:
                r0 = move-exception
                r4 = r0
                r74 = r3
                r41 = r6
                r3 = r15
                r79 = r24
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                goto L13ff
            L1492:
                r57 = r3
                r64 = r8
                r3 = r39
            L1498:
                r39 = 0
            L149a:
                if (r47 == 0) goto L1630
                android.os.UserHandle r2 = android.os.Process.myUserHandle()     // Catch: java.lang.Throwable -> L13e7 java.lang.Exception -> L15f1
                boolean r2 = r4.equals(r2)     // Catch: java.lang.Throwable -> L13e7 java.lang.Exception -> L15f1
                if (r2 == 0) goto L155d
                com.android.launcher3.LauncherModel r7 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L13e7 java.lang.Exception -> L15f1
                r2 = 0
                r8 = r43
                int r9 = r8.iconIndex     // Catch: java.lang.Throwable -> L13e7 java.lang.Exception -> L1522
                r41 = 0
                r69 = r42
                r43 = r44
                r68 = r67
                r44 = r8
                r42 = r35
                r35 = r45
                r45 = r61
                r8 = r62
                r70 = r48
                r71 = r64
                r8 = r12
                r47 = r9
                r9 = r13
                r74 = r3
                r3 = r36
                r73 = r38
                r36 = r60
                r82 = r10
                r11 = r37
                r37 = r82
                r10 = r4
                r77 = r11
                r75 = r29
                r4 = r34
                r76 = r40
                r29 = r46
                r11 = r62
                r66 = r15
                r15 = r12
                r12 = r2
                r2 = r13
                r34 = r23
                r23 = r14
                r14 = r43
                r13 = r47
                r78 = r14
                r40 = r58
                r14 = r42
                r43 = r16
                r79 = r24
                r46 = r56
                r80 = r57
                r24 = r15
                r15 = r41
                r16 = r39
                com.android.launcher3.ShortcutInfo r7 = r7.getAppShortcutInfo(r8, r9, r10, r11, r12, r13, r14, r15, r16)     // Catch: java.lang.Exception -> L15da java.lang.Throwable -> L1722
                r81 = r4
                r41 = r6
                r4 = r7
            L150c:
                r14 = r24
                r9 = r42
                r6 = r44
                r13 = r59
                r8 = r62
                r7 = r69
                r10 = r70
                r11 = r71
                r44 = r3
                r3 = r66
                goto L1903
            L1522:
                r0 = move-exception
                r74 = r3
                r43 = r16
                r79 = r24
                r75 = r29
                r4 = r34
                r3 = r36
                r76 = r40
                r69 = r42
                r78 = r44
                r29 = r46
                r46 = r56
                r40 = r58
                r36 = r60
                r34 = r23
                r42 = r35
                r35 = r45
                r45 = r61
                r23 = r14
                r2 = r3
                r7 = r4
                r41 = r6
                r14 = r12
                r3 = r15
                r6 = r33
                r73 = r38
                r70 = r48
                r13 = r59
                r68 = r67
                r9 = r75
                r4 = r0
                r15 = r8
                goto Laff
            L155d:
                r74 = r3
                r66 = r15
                r79 = r24
                r75 = r29
                r4 = r34
                r3 = r36
                r77 = r37
                r73 = r38
                r76 = r40
                r69 = r42
                r78 = r44
                r29 = r46
                r70 = r48
                r46 = r56
                r40 = r58
                r36 = r60
                r68 = r67
                r37 = r10
                r24 = r12
                r34 = r23
                r42 = r35
                r44 = r43
                r35 = r45
                r45 = r61
                r23 = r14
                r43 = r16
                java.lang.Long r2 = java.lang.Long.valueOf(r37)     // Catch: java.lang.Exception -> L15da java.lang.Throwable -> L1722
                r5.add(r2)     // Catch: java.lang.Exception -> L15da java.lang.Throwable -> L1722
                r48 = r5
                r27 = r23
                r23 = r24
                r9 = r28
                r11 = r29
                r52 = r34
                r10 = r36
                r28 = r42
                r16 = r43
                r43 = r44
                r7 = r45
                r5 = r49
                r12 = r66
                r41 = r68
                r42 = r69
                r8 = r70
                r38 = r73
                r39 = r74
                r29 = r75
                r37 = r77
                r44 = r78
                r24 = r79
                r36 = r3
                r34 = r4
                r45 = r35
                r49 = r46
                r3 = r59
                r46 = r62
                r35 = r6
            L15d2:
                r6 = r50
                r50 = r40
                r40 = r76
                goto L233
            L15da:
                r0 = move-exception
                r2 = r3
                r7 = r4
                r41 = r6
                r14 = r24
                r6 = r33
                r15 = r44
                r13 = r59
                r8 = r62
                r3 = r66
                r9 = r75
                r37 = r77
                goto L1ca7
            L15f1:
                r0 = move-exception
                r74 = r3
                r79 = r24
                r75 = r29
                r4 = r34
                r3 = r36
                r76 = r40
                r69 = r42
                r78 = r44
                r29 = r46
                r46 = r56
                r40 = r58
                r36 = r60
                r34 = r23
                r42 = r35
                r44 = r43
                r35 = r45
                r45 = r61
                r23 = r14
                r43 = r16
                r2 = r3
                r7 = r4
                r41 = r6
                r14 = r12
                r3 = r15
                r6 = r33
                r73 = r38
                r15 = r44
                r70 = r48
                r13 = r59
                r8 = r62
                r68 = r67
            L162c:
                r9 = r75
                goto L1ca7
            L1630:
                r74 = r3
                r66 = r15
                r79 = r24
                r75 = r29
                r15 = r34
                r3 = r36
                r77 = r37
                r73 = r38
                r76 = r40
                r69 = r42
                r78 = r44
                r29 = r46
                r70 = r48
                r46 = r56
                r80 = r57
                r40 = r58
                r36 = r60
                r71 = r64
                r68 = r67
                r37 = r10
                r24 = r12
                r34 = r23
                r42 = r35
                r44 = r43
                r35 = r45
                r45 = r61
                r23 = r14
                r43 = r16
                r14 = r13
                if (r27 == 0) goto L1740
                android.os.UserHandle r7 = android.os.Process.myUserHandle()     // Catch: java.lang.Throwable -> L1722 java.lang.Exception -> L1728
                boolean r4 = r4.equals(r7)     // Catch: java.lang.Throwable -> L1722 java.lang.Exception -> L1728
                if (r4 == 0) goto L16dc
                java.lang.String r4 = "Launcher.Model"
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L16ce java.lang.Exception -> L16d3
                r7.<init>()     // Catch: java.lang.Throwable -> L16ce java.lang.Exception -> L16d3
                java.lang.String r8 = "constructing info for partially restored package. "
                r7.append(r8)     // Catch: java.lang.Throwable -> L16ce java.lang.Exception -> L16d3
                r7.append(r14)     // Catch: java.lang.Throwable -> L16ce java.lang.Exception -> L16d3
                java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> L16ce java.lang.Exception -> L16d3
                r8 = 1
                com.android.launcher3.Launcher.addDumpLog(r4, r7, r8)     // Catch: java.lang.Throwable -> L1722 java.lang.Exception -> L1728
                com.android.launcher3.LauncherModel r7 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L1722 java.lang.Exception -> L1728
                r8 = r66
                r9 = r42
                r10 = r14
                r11 = r2
                r12 = r41
                r13 = r44
                r4 = r14
                r14 = r62
                com.android.launcher3.ShortcutInfo r7 = r7.getRestoredItemInfo(r8, r9, r10, r11, r12, r13, r14)     // Catch: java.lang.Throwable -> L1722 java.lang.Exception -> L1728
                r2 = r2 & 256(0x100, float:3.59E-43)
                if (r2 != 0) goto L16c6
                com.android.launcher3.LauncherModel r2 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L1722 java.lang.Exception -> L1728
                r14 = r62
                r13 = r66
                android.content.Intent r2 = r2.getRestoredItemIntent(r13, r14, r4)     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
                r41 = r6
                r4 = r7
                r8 = r14
                r81 = r15
                r14 = r24
                r9 = r42
                r6 = r44
                r7 = r69
                r10 = r70
                r11 = r71
                r44 = r3
                r3 = r13
                r13 = r59
                goto L1903
            L16c6:
                r2 = r4
                r41 = r6
                r4 = r7
                r81 = r15
                goto L150c
            L16ce:
                r0 = move-exception
                r13 = r66
                goto L176f
            L16d3:
                r0 = move-exception
                r14 = r62
                r13 = r66
                r2 = r0
                r4 = r2
                goto L1775
            L16dc:
                r14 = r62
                r13 = r66
                java.lang.Long r2 = java.lang.Long.valueOf(r37)     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
                r5.add(r2)     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
            L16e7:
                r48 = r5
                r12 = r13
                r27 = r23
                r23 = r24
                r9 = r28
                r11 = r29
                r52 = r34
                r10 = r36
                r28 = r42
                r16 = r43
                r43 = r44
                r7 = r45
                r5 = r49
                r41 = r68
                r42 = r69
                r8 = r70
                r38 = r73
                r39 = r74
                r29 = r75
                r37 = r77
                r44 = r78
                r24 = r79
                r36 = r3
                r34 = r15
                r45 = r35
                r49 = r46
                r3 = r59
                r35 = r6
                r46 = r14
                goto L15d2
            L1722:
                r0 = move-exception
            L1723:
                r2 = r0
                r3 = r66
                goto L2011
            L1728:
                r0 = move-exception
                r4 = r0
                r2 = r3
                r41 = r6
                r7 = r15
                r14 = r24
                r6 = r33
                r15 = r44
                r13 = r59
                r8 = r62
                r3 = r66
            L173a:
                r9 = r75
                r37 = r77
                goto L1ca8
            L1740:
                r2 = r14
                r14 = r62
                r13 = r66
                if (r41 != 0) goto L17f8
                boolean r7 = com.lge.launcher3.util.LGHomeFeature.isEnableDefaultHome()     // Catch: java.lang.Throwable -> L17da java.lang.Exception -> L17de
                if (r7 == 0) goto L1784
                com.android.launcher3.LauncherModel r7 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
                com.android.launcher3.AppFilter r7 = com.android.launcher3.LauncherModel.m46$$Nest$fgetmAppFilter(r7)     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
                if (r7 == 0) goto L1784
                com.android.launcher3.LauncherModel r7 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
                com.android.launcher3.AppFilter r7 = com.android.launcher3.LauncherModel.m46$$Nest$fgetmAppFilter(r7)     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
                android.content.ComponentName r8 = r2.getComponent()     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
                boolean r7 = r7.shouldShowApp(r8, r4)     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
                if (r7 != 0) goto L1784
                java.lang.Long r2 = java.lang.Long.valueOf(r37)     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
                r5.add(r2)     // Catch: java.lang.Throwable -> L176e java.lang.Exception -> L1773
                goto L16e7
            L176e:
                r0 = move-exception
            L176f:
                r2 = r0
                r3 = r13
                goto L2011
            L1773:
                r0 = move-exception
                r4 = r0
            L1775:
                r2 = r3
                r41 = r6
                r3 = r13
                r8 = r14
                r7 = r15
                r14 = r24
                r6 = r33
                r15 = r44
                r13 = r59
                goto L173a
            L1784:
                com.android.launcher3.LauncherModel r7 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L17da java.lang.Exception -> L17de
                r12 = r44
                int r11 = r12.iconIndex     // Catch: java.lang.Exception -> L17c2 java.lang.Throwable -> L17da
                r8 = r24
                r9 = r2
                r10 = r4
                r4 = r11
                r11 = r14
                r41 = r6
                r6 = r12
                r12 = r13
                r44 = r3
                r3 = r13
                r13 = r4
                r4 = r14
                r14 = r42
                r81 = r15
                r15 = r52
                r16 = r39
                com.android.launcher3.ShortcutInfo r7 = r7.getAppShortcutInfo(r8, r9, r10, r11, r12, r13, r14, r15, r16)     // Catch: java.lang.Exception -> L17b5 java.lang.Throwable -> L1b18
                r8 = r4
                r4 = r7
                r14 = r24
                r9 = r42
                r13 = r59
                r7 = r69
                r10 = r70
                r11 = r71
                goto L1903
            L17b5:
                r0 = move-exception
                r8 = r4
                r15 = r6
                r14 = r24
                r6 = r33
                r2 = r44
                r13 = r59
                goto L1a82
            L17c2:
                r0 = move-exception
                r44 = r3
                r41 = r6
                r3 = r13
                r4 = r0
                r8 = r14
                r7 = r15
                r14 = r24
                r6 = r33
                r2 = r44
                r13 = r59
                r9 = r75
                r37 = r77
                r15 = r12
                goto L1ca8
            L17da:
                r0 = move-exception
                r3 = r13
                goto L1b19
            L17de:
                r0 = move-exception
                r41 = r6
                r6 = r44
                r44 = r3
                r3 = r13
                r4 = r0
                r8 = r14
                r7 = r15
                r14 = r24
                r2 = r44
                r13 = r59
                r9 = r75
                r37 = r77
                r15 = r6
                r6 = r33
                goto L1ca8
            L17f8:
                r8 = r14
                r81 = r15
                r9 = r41
                r10 = 6
                r41 = r6
                r6 = r44
                r44 = r3
                r3 = r13
                if (r9 != r10) goto L18b1
                com.android.launcher3.shortcuts.ShortcutKey r7 = com.android.launcher3.shortcuts.ShortcutKey.fromIntent(r2, r4)     // Catch: java.lang.Exception -> L189e java.lang.Throwable -> L1b18
                r10 = r70
                r11 = r71
                java.lang.Object r13 = r10.get(r11)     // Catch: java.lang.Exception -> L1896 java.lang.Throwable -> L1b18
                java.lang.Boolean r13 = (java.lang.Boolean) r13     // Catch: java.lang.Exception -> L1896 java.lang.Throwable -> L1b18
                boolean r13 = r13.booleanValue()     // Catch: java.lang.Exception -> L1896 java.lang.Throwable -> L1b18
                if (r13 == 0) goto L1874
                r13 = r59
                java.lang.Object r2 = r13.get(r7)     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                com.android.launcher3.shortcuts.ShortcutInfoCompat r2 = (com.android.launcher3.shortcuts.ShortcutInfoCompat) r2     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                if (r2 != 0) goto L186b
                java.lang.Long r2 = java.lang.Long.valueOf(r37)     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                r5.add(r2)     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                r12 = r3
                r48 = r5
                r3 = r13
                r27 = r23
                r23 = r24
                r9 = r28
                r11 = r29
                r52 = r34
                r28 = r42
                r16 = r43
                r7 = r45
                r5 = r49
                r42 = r69
                r38 = r73
                r39 = r74
                r29 = r75
                r37 = r77
                r24 = r79
                r34 = r81
                r43 = r6
                r45 = r35
                r35 = r41
                r49 = r46
                r6 = r50
                r41 = r68
                r46 = r8
                r8 = r10
                r10 = r36
                r50 = r40
                r36 = r44
                r40 = r76
                r44 = r78
                goto L233
            L186b:
                com.android.launcher3.ShortcutInfo r4 = new com.android.launcher3.ShortcutInfo     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                r4.<init>(r2, r8)     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                android.content.Intent r2 = r4.intent     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                r7 = r4
                goto L188b
            L1874:
                r13 = r59
                com.android.launcher3.ShortcutInfo r7 = new com.android.launcher3.ShortcutInfo     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                r7.<init>()     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                r7.user = r4     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                r7.itemType = r9     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                com.android.launcher3.LauncherModel r4 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                r4.loadInfoFromCursor(r7, r3, r6, r8)     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                int r4 = r7.runtimeStatusFlags     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
                r9 = 32
                r4 = r4 | r9
                r7.runtimeStatusFlags = r4     // Catch: java.lang.Exception -> L1894 java.lang.Throwable -> L1b18
            L188b:
                r4 = r7
                r14 = r24
                r9 = r42
                r7 = r69
                goto L1903
            L1894:
                r0 = move-exception
                goto L1899
            L1896:
                r0 = move-exception
                r13 = r59
            L1899:
                r4 = r0
                r15 = r6
                r70 = r10
                goto L18a3
            L189e:
                r0 = move-exception
                r13 = r59
                r4 = r0
                r15 = r6
            L18a3:
                r14 = r24
            L18a5:
                r6 = r33
                r2 = r44
                r9 = r75
                r37 = r77
            L18ad:
                r7 = r81
                goto L1ca8
            L18b1:
                r13 = r59
                r10 = r70
                r11 = r71
                com.android.launcher3.LauncherModel r4 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Exception -> L1a78 java.lang.Throwable -> L1b18
                r9 = r42
                com.android.launcher3.ShortcutInfo r4 = r4.getShortcutInfo(r3, r8, r9, r6)     // Catch: java.lang.Exception -> L1a73 java.lang.Throwable -> L1b18
                r14 = r24
                boolean r7 = com.android.launcher3.util.PackageManagerHelper.isAppSuspended(r14, r7)     // Catch: java.lang.Exception -> L1a6c java.lang.Throwable -> L1b18
                if (r7 == 0) goto L18c9
                r55 = r55 | 4
            L18c9:
                java.lang.String r7 = r2.getAction()     // Catch: java.lang.Exception -> L1a6c java.lang.Throwable -> L1b18
                if (r7 == 0) goto L18fb
                java.util.Set r7 = r2.getCategories()     // Catch: java.lang.Exception -> L18f3 java.lang.Throwable -> L1b18
                if (r7 == 0) goto L18fb
                java.lang.String r7 = r2.getAction()     // Catch: java.lang.Exception -> L18f3 java.lang.Throwable -> L1b18
                java.lang.String r15 = "android.intent.action.MAIN"
                boolean r7 = r7.equals(r15)     // Catch: java.lang.Exception -> L18f3 java.lang.Throwable -> L1b18
                if (r7 == 0) goto L18fb
                java.util.Set r7 = r2.getCategories()     // Catch: java.lang.Exception -> L18f3 java.lang.Throwable -> L1b18
                java.lang.String r15 = "android.intent.category.LAUNCHER"
                boolean r7 = r7.contains(r15)     // Catch: java.lang.Exception -> L18f3 java.lang.Throwable -> L1b18
                if (r7 == 0) goto L18fb
                r7 = 270532608(0x10200000, float:3.1554436E-29)
                r2.addFlags(r7)     // Catch: java.lang.Exception -> L18f3 java.lang.Throwable -> L1b18
                goto L18fb
            L18f3:
                r0 = move-exception
                r4 = r0
                r15 = r6
                r42 = r9
                r70 = r10
                goto L18a5
            L18fb:
                r7 = r69
                int r15 = r3.getInt(r7)     // Catch: java.lang.Exception -> L1a67 java.lang.Throwable -> L1b18
                r4.options = r15     // Catch: java.lang.Exception -> L1a67 java.lang.Throwable -> L1b18
            L1903:
                if (r4 == 0) goto L1a4e
                r15 = r6
                r69 = r7
                r6 = r37
                r4.id = r6     // Catch: java.lang.Exception -> L1a4c java.lang.Throwable -> L1b18
                r4.intent = r2     // Catch: java.lang.Exception -> L1a4c java.lang.Throwable -> L1b18
                r42 = r9
                r70 = r10
                r9 = r80
                long r9 = (long) r9
                r4.container = r9     // Catch: java.lang.Exception -> L1a4a java.lang.Throwable -> L1b18
                r9 = r78
                int r10 = r3.getInt(r9)     // Catch: java.lang.Exception -> L1a46 java.lang.Throwable -> L1b18
                r78 = r9
                long r9 = (long) r10
                r4.screenId = r9     // Catch: java.lang.Exception -> L1a4a java.lang.Throwable -> L1b18
                r9 = r68
                int r10 = r3.getInt(r9)     // Catch: java.lang.Exception -> L1a42 java.lang.Throwable -> L1b18
                r4.cellX = r10     // Catch: java.lang.Exception -> L1a42 java.lang.Throwable -> L1b18
                r68 = r9
                r10 = r76
                int r9 = r3.getInt(r10)     // Catch: java.lang.Exception -> L1a3e java.lang.Throwable -> L1b18
                r4.cellY = r9     // Catch: java.lang.Exception -> L1a3e java.lang.Throwable -> L1b18
                r76 = r10
                r9 = r74
                int r10 = r3.getInt(r9)     // Catch: java.lang.Exception -> L1a3a java.lang.Throwable -> L1b18
                r4.rank = r10     // Catch: java.lang.Exception -> L1a3a java.lang.Throwable -> L1b18
                boolean r10 = r4 instanceof com.android.launcher3.ShortcutInfo     // Catch: java.lang.Exception -> L1a3a java.lang.Throwable -> L1b18
                if (r10 == 0) goto L1986
                boolean r10 = r4.hasPhotoIcon()     // Catch: java.lang.Exception -> L1978 java.lang.Throwable -> L1b18
                if (r10 == 0) goto L1967
                boolean r10 = r4.hasLargeIcon()     // Catch: java.lang.Exception -> L1978 java.lang.Throwable -> L1b18
                if (r10 == 0) goto L1967
                r74 = r9
                r10 = r77
                int r9 = r3.getInt(r10)     // Catch: java.lang.Exception -> L1963 java.lang.Throwable -> L1b18
                r4.spanX = r9     // Catch: java.lang.Exception -> L1963 java.lang.Throwable -> L1b18
                r37 = r10
                r9 = r73
                int r10 = r3.getInt(r9)     // Catch: java.lang.Exception -> L1973 java.lang.Throwable -> L1b18
                r4.spanY = r10     // Catch: java.lang.Exception -> L1973 java.lang.Throwable -> L1b18
                goto L198c
            L1963:
                r0 = move-exception
                r37 = r10
                goto L197d
            L1967:
                r74 = r9
                r9 = r73
                r37 = r77
                r10 = 1
                r4.spanX = r10     // Catch: java.lang.Exception -> L1973 java.lang.Throwable -> L1b18
                r4.spanY = r10     // Catch: java.lang.Exception -> L1973 java.lang.Throwable -> L1b18
                goto L198c
            L1973:
                r0 = move-exception
                r4 = r0
                r73 = r9
                goto L197e
            L1978:
                r0 = move-exception
                r74 = r9
                r37 = r77
            L197d:
                r4 = r0
            L197e:
                r6 = r33
                r2 = r44
            L1982:
                r9 = r75
                goto L18ad
            L1986:
                r74 = r9
                r9 = r73
                r37 = r77
            L198c:
                android.content.Intent r10 = r4.intent     // Catch: java.lang.Exception -> L1a2f java.lang.Throwable -> L1b18
                r73 = r9
                java.lang.String r9 = "profile"
                r10.putExtra(r9, r11)     // Catch: java.lang.Exception -> L1a2d java.lang.Throwable -> L1b18
                android.content.Intent r9 = r4.promisedIntent     // Catch: java.lang.Exception -> L1a2d java.lang.Throwable -> L1b18
                if (r9 == 0) goto L19a3
                android.content.Intent r9 = r4.promisedIntent     // Catch: java.lang.Exception -> L19a1 java.lang.Throwable -> L1b18
                java.lang.String r10 = "profile"
                r9.putExtra(r10, r11)     // Catch: java.lang.Exception -> L19a1 java.lang.Throwable -> L1b18
                goto L19a3
            L19a1:
                r0 = move-exception
                goto L197d
            L19a3:
                int r9 = r4.runtimeStatusFlags     // Catch: java.lang.Exception -> L1a2d java.lang.Throwable -> L1b18
                r9 = r9 | r55
                r4.runtimeStatusFlags = r9     // Catch: java.lang.Exception -> L1a2d java.lang.Throwable -> L1b18
                if (r17 == 0) goto L19b7
                boolean r2 = com.android.launcher3.Utilities.isSystemApp(r8, r2)     // Catch: java.lang.Exception -> L19a1 java.lang.Throwable -> L1b18
                if (r2 != 0) goto L19b7
                int r2 = r4.runtimeStatusFlags     // Catch: java.lang.Exception -> L19a1 java.lang.Throwable -> L1b18
                r9 = 1
                r2 = r2 | r9
                r4.runtimeStatusFlags = r2     // Catch: java.lang.Exception -> L19a1 java.lang.Throwable -> L1b18
            L19b7:
                r2 = r44
                boolean r9 = r1.checkItemPlacement(r2, r4)     // Catch: java.lang.Exception -> L1a29 java.lang.Throwable -> L1b18
                if (r9 != 0) goto L19d3
                java.lang.Long r4 = java.lang.Long.valueOf(r6)     // Catch: java.lang.Exception -> L19ce java.lang.Throwable -> L1b18
                r5.add(r4)     // Catch: java.lang.Exception -> L19ce java.lang.Throwable -> L1b18
                r6 = r33
                r9 = r75
                r7 = r81
                goto L1cb0
            L19ce:
                r0 = move-exception
                r4 = r0
                r6 = r33
                goto L1982
            L19d3:
                if (r27 == 0) goto L1a04
                android.content.ComponentName r6 = r4.getTargetComponent()     // Catch: java.lang.Exception -> L19fa java.lang.Throwable -> L1b18
                if (r6 == 0) goto L1a04
                java.lang.String r6 = r6.getPackageName()     // Catch: java.lang.Exception -> L19fa java.lang.Throwable -> L1b18
                r7 = r81
                java.lang.Object r6 = r7.get(r6)     // Catch: java.lang.Exception -> L19f8 java.lang.Throwable -> L1b18
                java.lang.Integer r6 = (java.lang.Integer) r6     // Catch: java.lang.Exception -> L19f8 java.lang.Throwable -> L1b18
                if (r6 == 0) goto L19f1
                int r6 = r6.intValue()     // Catch: java.lang.Exception -> L19f8 java.lang.Throwable -> L1b18
                r4.setInstallProgress(r6)     // Catch: java.lang.Exception -> L19f8 java.lang.Throwable -> L1b18
                goto L1a06
            L19f1:
                int r6 = r4.status     // Catch: java.lang.Exception -> L19f8 java.lang.Throwable -> L1b18
                r6 = r6 & (-5)
                r4.status = r6     // Catch: java.lang.Exception -> L19f8 java.lang.Throwable -> L1b18
                goto L1a06
            L19f8:
                r0 = move-exception
                goto L19fd
            L19fa:
                r0 = move-exception
                r7 = r81
            L19fd:
                r4 = r0
                r6 = r33
                r9 = r75
                goto L1ca8
            L1a04:
                r7 = r81
            L1a06:
                com.android.launcher3.model.BgDataModel r6 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Exception -> L1a24 java.lang.Throwable -> L1b18
                r9 = r75
                r10 = 0
                r6.addItem(r9, r4, r10)     // Catch: java.lang.Exception -> L1a1f java.lang.Throwable -> L1b18
                long r10 = r4.container     // Catch: java.lang.Exception -> L1a1f java.lang.Throwable -> L1b18
                int r6 = (r10 > r53 ? 1 : (r10 == r53 ? 0 : -1))
                if (r6 != 0) goto L1a1b
                r6 = r33
                r6.add(r4)     // Catch: java.lang.Throwable -> L1b18 java.lang.Exception -> L1b1c
                goto L1cb0
            L1a1b:
                r6 = r33
                goto L1cb0
            L1a1f:
                r0 = move-exception
                r6 = r33
                goto L1ca7
            L1a24:
                r0 = move-exception
                r6 = r33
                goto L162c
            L1a29:
                r0 = move-exception
                r6 = r33
                goto L1a36
            L1a2d:
                r0 = move-exception
                goto L1a32
            L1a2f:
                r0 = move-exception
                r73 = r9
            L1a32:
                r6 = r33
                r2 = r44
            L1a36:
                r9 = r75
                goto L1a86
            L1a3a:
                r0 = move-exception
                r74 = r9
                goto L1a7e
            L1a3e:
                r0 = move-exception
                r76 = r10
                goto L1a7e
            L1a42:
                r0 = move-exception
                r68 = r9
                goto L1a7e
            L1a46:
                r0 = move-exception
                r78 = r9
                goto L1a7e
            L1a4a:
                r0 = move-exception
                goto L1a7e
            L1a4c:
                r0 = move-exception
                goto L1a6e
            L1a4e:
                r15 = r6
                r69 = r7
                r42 = r9
                r70 = r10
                r6 = r33
                r2 = r44
                r9 = r75
                r37 = r77
                r7 = r81
                java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch: java.lang.Throwable -> L1b18 java.lang.Exception -> L1b1c
                java.lang.String r10 = "Unexpected null ShortcutInfo"
                r4.<init>(r10)     // Catch: java.lang.Throwable -> L1b18 java.lang.Exception -> L1b1c
                throw r4     // Catch: java.lang.Throwable -> L1b18 java.lang.Exception -> L1b1c
            L1a67:
                r0 = move-exception
                r15 = r6
                r69 = r7
                goto L1a6e
            L1a6c:
                r0 = move-exception
                r15 = r6
            L1a6e:
                r42 = r9
                r70 = r10
                goto L1a7e
            L1a73:
                r0 = move-exception
                r15 = r6
                r42 = r9
                goto L1a7a
            L1a78:
                r0 = move-exception
                r15 = r6
            L1a7a:
                r70 = r10
                r14 = r24
            L1a7e:
                r6 = r33
                r2 = r44
            L1a82:
                r9 = r75
                r37 = r77
            L1a86:
                r7 = r81
                goto L1ca7
            L1a8a:
                r0 = move-exception
                r41 = r6
                r3 = r12
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r43 = r16
                r42 = r35
                r35 = r45
                r45 = r61
                goto L1ca7
            L1ac4:
                r63 = r3
            L1ac6:
                r41 = r6
                r3 = r12
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
            L1af5:
                r43 = r16
                r42 = r35
                r35 = r45
            L1afb:
                r45 = r61
            L1afd:
                java.lang.String r4 = "Launcher.Model"
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L1b18 java.lang.Exception -> L1b1c
                r10.<init>()     // Catch: java.lang.Throwable -> L1b18 java.lang.Exception -> L1b1c
                java.lang.String r11 = "Invalid uri: "
                r10.append(r11)     // Catch: java.lang.Throwable -> L1b18 java.lang.Exception -> L1b1c
                r11 = r63
                r10.append(r11)     // Catch: java.lang.Throwable -> L1b18 java.lang.Exception -> L1b1c
                java.lang.String r10 = r10.toString()     // Catch: java.lang.Throwable -> L1b18 java.lang.Exception -> L1b1c
                r11 = 1
                com.android.launcher3.Launcher.addDumpLog(r4, r10, r11)     // Catch: java.lang.Throwable -> L1b18 java.lang.Exception -> L1b1c
                goto L1cb0
            L1b18:
                r0 = move-exception
            L1b19:
                r2 = r0
                goto L2011
            L1b1c:
                r0 = move-exception
                goto L1ca7
            L1b1f:
                r0 = move-exception
                r41 = r6
                r3 = r12
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r8 = r62
                r43 = r16
                r42 = r35
                r35 = r45
                r45 = r61
                r82 = r36
                r36 = r2
                r2 = r82
                goto L1ca7
            L1b5b:
                r0 = move-exception
                r41 = r6
                r3 = r12
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                goto L1bb4
            L1b88:
                r0 = move-exception
                r41 = r6
                r3 = r12
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r70 = r48
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r8 = r62
                r29 = r4
            L1bb4:
                r43 = r16
                r42 = r35
                r35 = r45
                r45 = r7
                r7 = r34
                r34 = r52
                goto L1ca7
            L1bc2:
                r0 = move-exception
                r41 = r6
                r28 = r9
                r3 = r12
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r69 = r42
                r15 = r43
                r78 = r44
                r35 = r45
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r45 = r61
                r29 = r4
                goto L1c2b
            L1bf7:
                r0 = move-exception
                r76 = r4
                r41 = r6
                r35 = r7
                r28 = r9
                r3 = r12
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r9 = r29
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r69 = r42
                r15 = r43
                r78 = r44
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r45 = r61
            L1c2b:
                r42 = r8
                goto L1c63
            L1c2e:
                r0 = move-exception
                r76 = r4
                r41 = r6
                r28 = r9
                r68 = r14
                r14 = r23
                r79 = r24
                r23 = r27
                r6 = r33
                r7 = r34
                r2 = r36
                r73 = r38
                r74 = r39
                r69 = r42
                r15 = r43
                r78 = r44
                r35 = r45
                r29 = r46
                r70 = r48
                r34 = r52
                r46 = r56
                r40 = r58
                r13 = r59
                r36 = r60
                r45 = r61
                r9 = r3
                r42 = r8
                r3 = r12
            L1c63:
                r43 = r16
                r8 = r62
                goto L1ca7
            L1c68:
                r0 = move-exception
                r13 = r3
                r70 = r8
                r3 = r12
                r14 = r23
                r79 = r24
                r23 = r27
                r2 = r36
                r73 = r38
                r74 = r39
                r76 = r40
                r68 = r41
                r69 = r42
                r15 = r43
                r78 = r44
                r8 = r46
                r46 = r49
                r40 = r50
                r49 = r5
                r50 = r6
                r36 = r10
                r43 = r16
                r42 = r28
                r6 = r33
                r41 = r35
                r35 = r45
                r5 = r48
                r45 = r7
                r28 = r9
                r9 = r29
                r7 = r34
                r34 = r52
                r29 = r11
            L1ca7:
                r4 = r0
            L1ca8:
                java.lang.String r10 = "Launcher.Model"
                java.lang.String r11 = "Desktop items loading interrupted"
                r12 = 1
                com.android.launcher3.Launcher.addDumpLog(r10, r11, r4, r12)     // Catch: java.lang.Throwable -> L1b18
            L1cb0:
                r12 = r3
                r48 = r5
                r33 = r6
                r3 = r13
                r27 = r23
                r11 = r29
                r52 = r34
                r10 = r36
                r16 = r43
                r5 = r49
                r6 = r50
                r38 = r73
                r39 = r74
                r44 = r78
                r24 = r79
                r36 = r2
                r34 = r7
                r29 = r9
                r23 = r14
                r43 = r15
                r9 = r28
                r50 = r40
                r28 = r42
                r7 = r45
                r49 = r46
                r42 = r69
                r40 = r76
                r46 = r8
                r45 = r35
                r35 = r41
                r41 = r68
                r8 = r70
                goto L233
            L1cf0:
                r13 = r3
                r3 = r12
                r43 = r16
                r79 = r24
                r6 = r33
                r41 = r35
                r2 = r36
                r8 = r46
                r5 = r48
                if (r3 == 0) goto L1d05
                r3.close()     // Catch: java.lang.Throwable -> L2017
            L1d05:
                boolean r3 = r1.mStopped     // Catch: java.lang.Throwable -> L2017
                if (r3 == 0) goto L1d10
                com.android.launcher3.model.BgDataModel r2 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                r2.clear()     // Catch: java.lang.Throwable -> L2017
                monitor-exit(r21)     // Catch: java.lang.Throwable -> L2017
                return
            L1d10:
                int r3 = r5.size()     // Catch: java.lang.Throwable -> L2017
                if (r3 <= 0) goto L1de0
                int r3 = r43.size()     // Catch: java.lang.Throwable -> L2017
                if (r3 <= 0) goto L1d62
                int r3 = r43.size()     // Catch: java.lang.Throwable -> L2017
                r4 = 1
                int r3 = r3 - r4
            L1d22:
                if (r3 < 0) goto L1d62
                java.util.Iterator r4 = r6.iterator()     // Catch: java.lang.Throwable -> L2017
            L1d28:
                boolean r7 = r4.hasNext()     // Catch: java.lang.Throwable -> L2017
                if (r7 == 0) goto L1d5d
                java.lang.Object r7 = r4.next()     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.data.ItemInfo r7 = (com.android.launcher3.model.data.ItemInfo) r7     // Catch: java.lang.Throwable -> L2017
                long r9 = r7.container     // Catch: java.lang.Throwable -> L2017
                int r9 = (r9 > r53 ? 1 : (r9 == r53 ? 0 : -1))
                if (r9 != 0) goto L1d58
                int r9 = r7.cellX     // Catch: java.lang.Throwable -> L2017
                r10 = r43
                java.lang.Object r11 = r10.get(r3)     // Catch: java.lang.Throwable -> L2017
                java.lang.Integer r11 = (java.lang.Integer) r11     // Catch: java.lang.Throwable -> L2017
                int r11 = r11.intValue()     // Catch: java.lang.Throwable -> L2017
                if (r9 <= r11) goto L1d5a
                int r9 = r7.cellX     // Catch: java.lang.Throwable -> L2017
                r11 = 1
                int r9 = r9 - r11
                r7.cellX = r9     // Catch: java.lang.Throwable -> L2017
                long r11 = r7.screenId     // Catch: java.lang.Throwable -> L2017
                r14 = 1
                long r11 = r11 - r14
                r7.screenId = r11     // Catch: java.lang.Throwable -> L2017
                goto L1d5a
            L1d58:
                r10 = r43
            L1d5a:
                r43 = r10
                goto L1d28
            L1d5d:
                r10 = r43
                int r3 = r3 + (-1)
                goto L1d22
            L1d62:
                android.net.Uri r3 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI     // Catch: java.lang.Throwable -> L2017
                java.lang.String r4 = "_id"
                java.lang.String r4 = com.android.launcher3.Utilities.createDbSelectionQuery(r4, r5)     // Catch: java.lang.Throwable -> L2017
                r6 = r79
                r7 = 0
                r6.delete(r3, r4, r7)     // Catch: java.lang.Throwable -> L2017
                boolean r3 = com.android.launcher3.LauncherModel.DEBUG_LOADERS     // Catch: java.lang.Throwable -> L2017
                if (r3 == 0) goto L1d90
                java.lang.String r3 = "Launcher.Model"
                java.lang.String r4 = "_id"
                java.lang.String r4 = com.android.launcher3.Utilities.createDbSelectionQuery(r4, r5)     // Catch: java.lang.Throwable -> L2017
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2017
                r5.<init>()     // Catch: java.lang.Throwable -> L2017
                java.lang.String r7 = "Removed = "
                r5.append(r7)     // Catch: java.lang.Throwable -> L2017
                r5.append(r4)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r4 = r5.toString()     // Catch: java.lang.Throwable -> L2017
                android.util.Log.d(r3, r4)     // Catch: java.lang.Throwable -> L2017
            L1d90:
                java.lang.String r3 = "delete_empty_folders"
                android.os.Bundle r3 = com.android.launcher3.LauncherSettings.Settings.call(r6, r3)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r4 = "value"
                java.io.Serializable r3 = r3.getSerializable(r4)     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList r3 = (java.util.ArrayList) r3     // Catch: java.lang.Throwable -> L2017
                java.util.Iterator r3 = r3.iterator()     // Catch: java.lang.Throwable -> L2017
            L1da3:
                boolean r4 = r3.hasNext()     // Catch: java.lang.Throwable -> L2017
                if (r4 == 0) goto L1de2
                java.lang.Object r4 = r3.next()     // Catch: java.lang.Throwable -> L2017
                java.lang.Long r4 = (java.lang.Long) r4     // Catch: java.lang.Throwable -> L2017
                long r4 = r4.longValue()     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r7 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList<com.android.launcher3.model.data.ItemInfo> r7 = r7.workspaceItems     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r9 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.FolderInfo> r9 = r9.folders     // Catch: java.lang.Throwable -> L2017
                java.lang.Object r9 = r9.get(r4)     // Catch: java.lang.Throwable -> L2017
                r7.remove(r9)     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r7 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList<com.android.launcher3.model.data.FolderInfo> r7 = com.android.launcher3.model.BgDataModel.invalidFoldersToRestore     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r9 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.FolderInfo> r9 = r9.folders     // Catch: java.lang.Throwable -> L2017
                java.lang.Object r9 = r9.get(r4)     // Catch: java.lang.Throwable -> L2017
                r7.remove(r9)     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r7 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.FolderInfo> r7 = r7.folders     // Catch: java.lang.Throwable -> L2017
                r7.remove(r4)     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r7 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.ItemInfo> r7 = r7.itemsIdMap     // Catch: java.lang.Throwable -> L2017
                r7.remove(r4)     // Catch: java.lang.Throwable -> L2017
                goto L1da3
            L1de0:
                r6 = r79
            L1de2:
                java.util.Set r3 = r13.keySet()     // Catch: java.lang.Throwable -> L2017
                java.util.Iterator r3 = r3.iterator()     // Catch: java.lang.Throwable -> L2017
            L1dea:
                boolean r4 = r3.hasNext()     // Catch: java.lang.Throwable -> L2017
                if (r4 == 0) goto L1e05
                java.lang.Object r4 = r3.next()     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.shortcuts.ShortcutKey r4 = (com.android.launcher3.shortcuts.ShortcutKey) r4     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r5 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, android.util.MutableInt> r5 = r5.pinnedShortcutCounts     // Catch: java.lang.Throwable -> L2017
                java.lang.Object r4 = r5.get(r4)     // Catch: java.lang.Throwable -> L2017
                android.util.MutableInt r4 = (android.util.MutableInt) r4     // Catch: java.lang.Throwable -> L2017
                if (r4 == 0) goto L1dea
                int r4 = r4.value     // Catch: java.lang.Throwable -> L2017
                goto L1dea
            L1e05:
                r1.removeChildrenOfInvalidFolder(r6)     // Catch: java.lang.Throwable -> L2017
                int r3 = r41.size()     // Catch: java.lang.Throwable -> L2017
                if (r3 <= 0) goto L1e3c
                r3 = 0
                r4 = 0
                boolean r5 = com.lge.launcher3.memory.MemoryUtils.hasAvailableFileSystemMemory(r3, r4)     // Catch: java.lang.Throwable -> L2017
                if (r5 != 0) goto L1e1e
                java.lang.String r3 = "Launcher.Model"
                java.lang.String r4 = "Memory is full. skip to update restored items in loadworkspace"
                com.lge.launcher3.util.LGLog.i(r3, r4)     // Catch: java.lang.Throwable -> L2017
                goto L1e3c
            L1e1e:
                android.content.ContentValues r3 = new android.content.ContentValues     // Catch: java.lang.Throwable -> L2017
                r3.<init>()     // Catch: java.lang.Throwable -> L2017
                java.lang.String r4 = "restored"
                r5 = 0
                java.lang.Integer r7 = java.lang.Integer.valueOf(r5)     // Catch: java.lang.Throwable -> L2017
                r3.put(r4, r7)     // Catch: java.lang.Throwable -> L2017
                android.net.Uri r4 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI     // Catch: java.lang.Throwable -> L2017
                java.lang.String r7 = "_id"
                r9 = r41
                java.lang.String r7 = com.android.launcher3.Utilities.createDbSelectionQuery(r7, r9)     // Catch: java.lang.Throwable -> L2017
                r9 = 0
                r6.update(r4, r3, r7, r9)     // Catch: java.lang.Throwable -> L2017
                goto L1e3d
            L1e3c:
                r5 = 0
            L1e3d:
                if (r18 != 0) goto L1e5b
                java.util.HashMap<android.os.UserHandle, java.util.HashSet<java.lang.String>> r3 = com.android.launcher3.LauncherModel.sPendingPackages     // Catch: java.lang.Throwable -> L2017
                boolean r3 = r3.isEmpty()     // Catch: java.lang.Throwable -> L2017
                if (r3 != 0) goto L1e5b
                com.android.launcher3.LauncherModel$AppsAvailabilityCheck r3 = new com.android.launcher3.LauncherModel$AppsAvailabilityCheck     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.LauncherModel r4 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L2017
                r3.<init>()     // Catch: java.lang.Throwable -> L2017
                android.content.IntentFilter r4 = new android.content.IntentFilter     // Catch: java.lang.Throwable -> L2017
                java.lang.String r6 = "com.android.launcher3.SYSTEM_READY"
                r4.<init>(r6)     // Catch: java.lang.Throwable -> L2017
                android.os.Handler r6 = com.android.launcher3.LauncherModel.sWorker     // Catch: java.lang.Throwable -> L2017
                r7 = 0
                r8.registerReceiver(r3, r4, r7, r6)     // Catch: java.lang.Throwable -> L2017
            L1e5b:
                com.android.launcher3.model.BgDataModel r3 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList<java.lang.Long> r3 = r3.workspaceScreens     // Catch: java.lang.Throwable -> L2017
                android.content.Context r4 = r1.mContext     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList r4 = com.android.launcher3.LauncherModel.loadWorkspaceScreensDb(r4)     // Catch: java.lang.Throwable -> L2017
                r3.addAll(r4)     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.LauncherModel r3 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L2017
                r4 = r32
                com.android.launcher3.LauncherModel.m59$$Nest$mrestoreInvalidFolder(r3, r8, r4)     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList r3 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r4 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList<java.lang.Long> r4 = r4.workspaceScreens     // Catch: java.lang.Throwable -> L2017
                r3.<init>(r4)     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList r4 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r6 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList<java.lang.Long> r6 = r6.workspaceScreens     // Catch: java.lang.Throwable -> L2017
                r4.<init>(r6)     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList r6 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L2017
                r6.<init>()     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r7 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.ItemInfo> r7 = r7.itemsIdMap     // Catch: java.lang.Throwable -> L2017
                java.util.Iterator r7 = r7.iterator()     // Catch: java.lang.Throwable -> L2017
            L1e8e:
                boolean r9 = r7.hasNext()     // Catch: java.lang.Throwable -> L2017
                if (r9 == 0) goto L1ece
                java.lang.Object r9 = r7.next()     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.data.ItemInfo r9 = (com.android.launcher3.model.data.ItemInfo) r9     // Catch: java.lang.Throwable -> L2017
                long r10 = r9.screenId     // Catch: java.lang.Throwable -> L2017
                long r12 = r9.container     // Catch: java.lang.Throwable -> L2017
                r14 = -100
                int r12 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
                if (r12 != 0) goto L1eb6
                java.lang.Long r12 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Throwable -> L2017
                boolean r12 = r3.contains(r12)     // Catch: java.lang.Throwable -> L2017
                if (r12 == 0) goto L1eb6
                java.lang.Long r9 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Throwable -> L2017
                r3.remove(r9)     // Catch: java.lang.Throwable -> L2017
                goto L1e8e
            L1eb6:
                long r12 = r9.container     // Catch: java.lang.Throwable -> L2017
                int r9 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
                if (r9 != 0) goto L1e8e
                java.lang.Long r9 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Throwable -> L2017
                boolean r9 = r4.contains(r9)     // Catch: java.lang.Throwable -> L2017
                if (r9 != 0) goto L1e8e
                java.lang.Long r9 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Throwable -> L2017
                r6.add(r9)     // Catch: java.lang.Throwable -> L2017
                goto L1e8e
            L1ece:
                int r4 = r3.size()     // Catch: java.lang.Throwable -> L2017
                if (r4 == 0) goto L1efd
                com.android.launcher3.LauncherModel r4 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.LauncherModel.m56$$Nest$madjustDefaultScreen(r4, r8, r3)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r4 = "Launcher.Model"
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2017
                r7.<init>()     // Catch: java.lang.Throwable -> L2017
                java.lang.String r9 = "ScreenCheck: unusedScreens = "
                r7.append(r9)     // Catch: java.lang.Throwable -> L2017
                r7.append(r3)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> L2017
                com.lge.launcher3.util.LGLog.i(r4, r7)     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r4 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList<java.lang.Long> r4 = r4.workspaceScreens     // Catch: java.lang.Throwable -> L2017
                r4.removeAll(r3)     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r3 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList<java.lang.Long> r3 = r3.workspaceScreens     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.LauncherModel.updateWorkspaceScreenOrder(r8, r3)     // Catch: java.lang.Throwable -> L2017
            L1efd:
                int r3 = r6.size()     // Catch: java.lang.Throwable -> L2017
                if (r3 == 0) goto L1f2b
                java.lang.String r3 = "Launcher.Model"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2017
                r4.<init>()     // Catch: java.lang.Throwable -> L2017
                java.lang.String r7 = "ScreenCheck: lostScreens = "
                r4.append(r7)     // Catch: java.lang.Throwable -> L2017
                r4.append(r6)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> L2017
                com.lge.launcher3.util.LGLog.i(r3, r4)     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r3 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList<java.lang.Long> r3 = r3.workspaceScreens     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList r4 = com.android.launcher3.LauncherModel.removeDuplicateList(r6)     // Catch: java.lang.Throwable -> L2017
                r3.addAll(r4)     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.BgDataModel r3 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                java.util.ArrayList<java.lang.Long> r3 = r3.workspaceScreens     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.LauncherModel.updateWorkspaceScreenOrder(r8, r3)     // Catch: java.lang.Throwable -> L2017
            L1f2b:
                boolean r3 = com.android.launcher3.LauncherModel.DEBUG_LOADERS     // Catch: java.lang.Throwable -> L2017
                if (r3 == 0) goto L1ffc
                java.lang.String r3 = "Launcher.Model"
                long r6 = android.os.SystemClock.uptimeMillis()     // Catch: java.lang.Throwable -> L2017
                long r6 = r6 - r25
                com.android.launcher3.model.BgDataModel r4 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.ItemInfo> r4 = r4.itemsIdMap     // Catch: java.lang.Throwable -> L2017
                int r4 = r4.size()     // Catch: java.lang.Throwable -> L2017
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2017
                r9.<init>()     // Catch: java.lang.Throwable -> L2017
                java.lang.String r10 = "loaded workspace in "
                r9.append(r10)     // Catch: java.lang.Throwable -> L2017
                r9.append(r6)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r6 = "ms + sBgItemsIdMap.size(): "
                r9.append(r6)     // Catch: java.lang.Throwable -> L2017
                r9.append(r4)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r4 = r9.toString()     // Catch: java.lang.Throwable -> L2017
                android.util.Log.d(r3, r4)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r3 = "Launcher.Model"
                java.lang.String r4 = "workspace layout: "
                android.util.Log.d(r3, r4)     // Catch: java.lang.Throwable -> L2017
                int r3 = r2.size()     // Catch: java.lang.Throwable -> L2017
                r12 = r5
                r4 = r31
            L1f6a:
                if (r12 >= r4) goto L1ffc
                java.lang.String r6 = ""
                r7 = r5
            L1f6f:
                if (r7 >= r3) goto L1fd6
                long r9 = r2.keyAt(r7)     // Catch: java.lang.Throwable -> L2017
                r13 = 0
                int r9 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
                if (r9 <= 0) goto L1f8c
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2017
                r9.<init>()     // Catch: java.lang.Throwable -> L2017
                r9.append(r6)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r6 = " | "
                r9.append(r6)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r6 = r9.toString()     // Catch: java.lang.Throwable -> L2017
            L1f8c:
                java.lang.Object r9 = r2.valueAt(r7)     // Catch: java.lang.Throwable -> L2017
                com.android.launcher3.model.data.ItemInfo[][] r9 = (com.android.launcher3.model.data.ItemInfo[][]) r9     // Catch: java.lang.Throwable -> L2017
                r10 = r5
                r11 = r20
            L1f95:
                if (r10 >= r11) goto L1fd0
                int r15 = r9.length     // Catch: java.lang.Throwable -> L2017
                if (r10 >= r15) goto L1fba
                r15 = r9[r10]     // Catch: java.lang.Throwable -> L2017
                int r15 = r15.length     // Catch: java.lang.Throwable -> L2017
                if (r12 >= r15) goto L1fba
                r15 = r9[r10]     // Catch: java.lang.Throwable -> L2017
                r15 = r15[r12]     // Catch: java.lang.Throwable -> L2017
                if (r15 == 0) goto L1fa8
                java.lang.String r15 = "#"
                goto L1faa
            L1fa8:
                java.lang.String r15 = "."
            L1faa:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2017
                r5.<init>()     // Catch: java.lang.Throwable -> L2017
                r5.append(r6)     // Catch: java.lang.Throwable -> L2017
                r5.append(r15)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> L2017
                goto L1fcb
            L1fba:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2017
                r5.<init>()     // Catch: java.lang.Throwable -> L2017
                r5.append(r6)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r6 = "!"
                r5.append(r6)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> L2017
            L1fcb:
                r6 = r5
                int r10 = r10 + 1
                r5 = 0
                goto L1f95
            L1fd0:
                int r7 = r7 + 1
                r20 = r11
                r5 = 0
                goto L1f6f
            L1fd6:
                r11 = r20
                r13 = 0
                java.lang.String r5 = "Launcher.Model"
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2017
                r7.<init>()     // Catch: java.lang.Throwable -> L2017
                java.lang.String r9 = "[ "
                r7.append(r9)     // Catch: java.lang.Throwable -> L2017
                r7.append(r6)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r6 = " ]"
                r7.append(r6)     // Catch: java.lang.Throwable -> L2017
                java.lang.String r6 = r7.toString()     // Catch: java.lang.Throwable -> L2017
                android.util.Log.d(r5, r6)     // Catch: java.lang.Throwable -> L2017
                int r12 = r12 + 1
                r20 = r11
                r5 = 0
                goto L1f6a
            L1ffc:
                monitor-exit(r21)     // Catch: java.lang.Throwable -> L2017
                int r2 = r1.mFlags
                r3 = 8
                r2 = r2 & r3
                if (r2 == 0) goto L200c
                java.lang.String r2 = "appbox_reload"
                java.lang.String r3 = "reload_finished"
                r4 = 1
                com.android.launcher3.LauncherModel.setHomePreferences(r8, r2, r3, r4)
            L200c:
                return
            L200d:
                r0 = move-exception
                r3 = r12
                goto L1b19
            L2011:
                if (r3 == 0) goto L2016
                r3.close()     // Catch: java.lang.Throwable -> L2017
            L2016:
                throw r2     // Catch: java.lang.Throwable -> L2017
            L2017:
                r0 = move-exception
                r2 = r0
                monitor-exit(r21)     // Catch: java.lang.Throwable -> L2017
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherModel.LoaderTask.loadWorkspace():void");
        }

        private void removeChildrenOfInvalidFolder(final ContentResolver contentResolver) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < LauncherModel.sBgDataModel.folders.size(); i++) {
                FolderInfo folderInfo = LauncherModel.sBgDataModel.folders.get(LauncherModel.sBgDataModel.folders.keyAt(i));
                if (LauncherModel.DEBUG_LOADERS) {
                    Log.w(LauncherModel.TAG, "folder:" + folderInfo);
                }
                if (!LauncherModel.sBgDataModel.workspaceItems.contains(folderInfo) && !LauncherModel.sBgDataModel.itemsIdMap.containsKey(folderInfo.id)) {
                    if (LauncherModel.DEBUG_LOADERS) {
                        Log.w(LauncherModel.TAG, "Removed invalid folder:" + folderInfo);
                    }
                    for (ShortcutInfo shortcutInfo : folderInfo.contents) {
                        if (LauncherModel.DEBUG_LOADERS) {
                            Log.w(LauncherModel.TAG, "---> Removed child " + shortcutInfo);
                        }
                        arrayList.add(Long.valueOf(shortcutInfo.id));
                        LauncherModel.sBgDataModel.workspaceItems.remove(shortcutInfo);
                        LauncherModel.sBgDataModel.itemsIdMap.remove(shortcutInfo.id);
                    }
                    arrayList2.add(Long.valueOf(folderInfo.id));
                }
            }
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                LauncherModel.sBgDataModel.folders.remove(((Long) it.next()).longValue());
            }
            contentResolver.delete(LauncherSettings.Favorites.CONTENT_URI, Utilities.createDbSelectionQuery("_id", arrayList), null);
            if (LauncherModel.DEBUG_LOADERS) {
                Log.w(LauncherModel.TAG, "removeInvalidFolder() Removed children ids = " + Utilities.createDbSelectionQuery("_id", arrayList));
            }
        }

        private void updateItem(long itemId, ContentValues update) {
            this.mContext.getContentResolver().update(LauncherSettings.Favorites.CONTENT_URI, update, "_id= ?", new String[]{Long.toString(itemId)});
        }

        private void filterCurrentWorkspaceItems(long currentScreenId, ArrayList<ItemInfo> allWorkspaceItems, ArrayList<ItemInfo> currentScreenItems, ArrayList<ItemInfo> otherScreenItems) {
            Iterator<ItemInfo> it = allWorkspaceItems.iterator();
            while (it.hasNext()) {
                if (it.next() == null) {
                    it.remove();
                }
            }
            HashSet hashSet = new HashSet();
            Collections.sort(allWorkspaceItems, new Comparator<ItemInfo>() { // from class: com.android.launcher3.LauncherModel.LoaderTask.2
                /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
                @Override // java.util.Comparator
                public int compare(ItemInfo lhs, ItemInfo rhs) {
                    return (int) (lhs.container - rhs.container);
                }
            });
            for (ItemInfo itemInfo : allWorkspaceItems) {
                if (itemInfo.container == -100) {
                    if (itemInfo.screenId == currentScreenId) {
                        currentScreenItems.add(itemInfo);
                        hashSet.add(Long.valueOf(itemInfo.id));
                    } else {
                        otherScreenItems.add(itemInfo);
                    }
                } else if (itemInfo.container == -101) {
                    currentScreenItems.add(itemInfo);
                    hashSet.add(Long.valueOf(itemInfo.id));
                } else if (hashSet.contains(Long.valueOf(itemInfo.container))) {
                    currentScreenItems.add(itemInfo);
                    hashSet.add(Long.valueOf(itemInfo.id));
                } else {
                    otherScreenItems.add(itemInfo);
                }
            }
        }

        private void filterCurrentAppWidgets(long currentScreenId, ArrayList<LauncherAppWidgetInfo> appWidgets, ArrayList<LauncherAppWidgetInfo> currentScreenWidgets, ArrayList<LauncherAppWidgetInfo> otherScreenWidgets) {
            for (LauncherAppWidgetInfo launcherAppWidgetInfo : appWidgets) {
                if (launcherAppWidgetInfo != null) {
                    if (launcherAppWidgetInfo.container == -100 && launcherAppWidgetInfo.screenId == currentScreenId) {
                        currentScreenWidgets.add(launcherAppWidgetInfo);
                    } else {
                        otherScreenWidgets.add(launcherAppWidgetInfo);
                    }
                }
            }
        }

        private void filterCurrentFolders(long currentScreenId, LongArrayMap<ItemInfo> itemsIdMap, LongArrayMap<FolderInfo> folders, LongArrayMap<FolderInfo> currentScreenFolders, LongArrayMap<FolderInfo> otherScreenFolders) {
            int size = folders.size();
            for (int i = 0; i < size; i++) {
                long jKeyAt = folders.keyAt(i);
                FolderInfo folderInfoValueAt = folders.valueAt(i);
                ItemInfo itemInfo = itemsIdMap.get(jKeyAt);
                if (itemInfo != null && folderInfoValueAt != null) {
                    if (itemInfo.container == -100 && itemInfo.screenId == currentScreenId) {
                        currentScreenFolders.put(jKeyAt, folderInfoValueAt);
                    } else {
                        otherScreenFolders.put(jKeyAt, folderInfoValueAt);
                    }
                }
            }
        }

        private void sortWorkspaceItemsSpatially(ArrayList<ItemInfo> workspaceItems) {
            final InvariantDeviceProfile invariantDeviceProfile = LauncherAppState.getInstance(this.mContext).getInvariantDeviceProfile();
            Collections.sort(workspaceItems, new Comparator<ItemInfo>() { // from class: com.android.launcher3.LauncherModel.LoaderTask.3
                /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
                @Override // java.util.Comparator
                public int compare(ItemInfo lhs, ItemInfo rhs) {
                    int i = invariantDeviceProfile.numColumns;
                    int i2 = invariantDeviceProfile.numRows * i;
                    long j = i2 * 6;
                    long j2 = i2;
                    return (int) (((((lhs.container * j) + (lhs.screenId * j2)) + ((long) (lhs.cellY * i))) + ((long) lhs.cellX)) - ((((rhs.container * j) + (rhs.screenId * j2)) + ((long) (rhs.cellY * i))) + ((long) rhs.cellX)));
                }
            });
        }

        private void bindWorkspaceScreens(final Callbacks oldCallbacks, final ArrayList<Long> orderedScreens) {
            LauncherModel.this.runOnMainThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.4
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(oldCallbacks);
                    if (callbacksTryGetCallbacks != null) {
                        callbacksTryGetCallbacks.bindScreens(orderedScreens);
                    }
                }
            });
        }

        private void bindWorkspaceItems(final Callbacks oldCallbacks, final ArrayList<ItemInfo> workspaceItems, final ArrayList<LauncherAppWidgetInfo> appWidgets, final LongArrayMap<FolderInfo> folders, final Executor executor) {
            int size = workspaceItems.size();
            final int i = 0;
            while (i < size) {
                int i2 = i + 6;
                final int i3 = i2 <= size ? 6 : size - i;
                executor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.5
                    @Override // java.lang.Runnable
                    public void run() {
                        Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(oldCallbacks);
                        if (callbacksTryGetCallbacks != null) {
                            ArrayList<ItemInfo> arrayList = workspaceItems;
                            int i4 = i;
                            callbacksTryGetCallbacks.bindItems(arrayList, i4, i3 + i4, false);
                        }
                    }
                });
                i = i2;
            }
            if (!folders.isEmpty()) {
                executor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.6
                    @Override // java.lang.Runnable
                    public void run() {
                        Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(oldCallbacks);
                        if (callbacksTryGetCallbacks != null) {
                            callbacksTryGetCallbacks.bindFolders(folders);
                        }
                    }
                });
            }
            int size2 = appWidgets.size();
            for (int i4 = 0; i4 < size2; i4++) {
                final LauncherAppWidgetInfo launcherAppWidgetInfo = appWidgets.get(i4);
                executor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.7
                    @Override // java.lang.Runnable
                    public void run() {
                        Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(oldCallbacks);
                        if (callbacksTryGetCallbacks != null) {
                            callbacksTryGetCallbacks.bindAppWidget(launcherAppWidgetInfo);
                        }
                    }
                });
            }
        }

        private void bindWorkspace(int synchronizeBindPage) {
            LongArrayMap<FolderInfo> longArrayMapClone;
            LongArrayMap<ItemInfo> longArrayMapClone2;
            final long jUptimeMillis = SystemClock.uptimeMillis();
            final Callbacks callbacks = LauncherModel.this.mCallbacks.get();
            if (callbacks == null) {
                Log.w(LauncherModel.TAG, "LoaderTask running with no launcher");
                return;
            }
            ArrayList<ItemInfo> arrayList = new ArrayList<>();
            ArrayList<LauncherAppWidgetInfo> arrayList2 = new ArrayList<>();
            final ArrayList arrayList3 = new ArrayList();
            synchronized (LauncherModel.sBgDataModel) {
                arrayList.addAll(LauncherModel.sBgDataModel.workspaceItems);
                arrayList2.addAll(LauncherModel.sBgDataModel.appWidgets);
                arrayList3.addAll(LauncherModel.sBgDataModel.workspaceScreens);
                longArrayMapClone = LauncherModel.sBgDataModel.folders.clone();
                longArrayMapClone2 = LauncherModel.sBgDataModel.itemsIdMap.clone();
            }
            int currentWorkspaceScreen = synchronizeBindPage;
            if (!(currentWorkspaceScreen != -1001)) {
                currentWorkspaceScreen = callbacks.getCurrentWorkspaceScreen();
            }
            int i = currentWorkspaceScreen >= arrayList3.size() ? -1001 : currentWorkspaceScreen;
            final boolean z = i >= 0;
            long jLongValue = z ? ((Long) arrayList3.get(i)).longValue() : -1L;
            LauncherModel.this.unbindWorkspaceItemsOnMainThread();
            ArrayList<ItemInfo> arrayList4 = new ArrayList<>();
            ArrayList<ItemInfo> arrayList5 = new ArrayList<>();
            ArrayList<LauncherAppWidgetInfo> arrayList6 = new ArrayList<>();
            ArrayList<LauncherAppWidgetInfo> arrayList7 = new ArrayList<>();
            LongArrayMap<FolderInfo> longArrayMap = new LongArrayMap<>();
            LongArrayMap<FolderInfo> longArrayMap2 = new LongArrayMap<>();
            final int i2 = i;
            filterCurrentWorkspaceItems(jLongValue, arrayList, arrayList4, arrayList5);
            filterCurrentAppWidgets(jLongValue, arrayList2, arrayList6, arrayList7);
            filterCurrentFolders(jLongValue, longArrayMapClone2, longArrayMapClone, longArrayMap, longArrayMap2);
            sortWorkspaceItemsSpatially(arrayList4);
            sortWorkspaceItemsSpatially(arrayList5);
            this.mUiExecutor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.8
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(callbacks);
                    if (callbacksTryGetCallbacks != null) {
                        callbacksTryGetCallbacks.clearPendingBinds();
                        callbacksTryGetCallbacks.startBinding(LoaderTask.this.mFlags);
                    }
                }
            });
            this.mUiExecutor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.9
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(callbacks);
                    if (callbacksTryGetCallbacks != null) {
                        callbacksTryGetCallbacks.bindScreens(arrayList3);
                    }
                }
            });
            Executor executor = this.mUiExecutor;
            bindWorkspaceItems(callbacks, arrayList4, arrayList6, longArrayMap, executor);
            final Executor viewOnDrawExecutor = z ? new ViewOnDrawExecutor() : this.mUiExecutor;
            executor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.10
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(callbacks);
                    if (callbacksTryGetCallbacks != null) {
                        callbacksTryGetCallbacks.finishFirstPageBind(z ? (ViewOnDrawExecutor) viewOnDrawExecutor : null);
                        callbacksTryGetCallbacks.finishBindingHotSeats();
                    }
                }
            });
            bindWorkspaceItems(callbacks, arrayList5, arrayList7, longArrayMap2, viewOnDrawExecutor);
            viewOnDrawExecutor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.11
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(callbacks);
                    if (callbacksTryGetCallbacks != null) {
                        callbacksTryGetCallbacks.finishBindingItems();
                    }
                    if (LauncherModel.DEBUG_LOADERS) {
                        Log.d(LauncherModel.TAG, "bound workspace in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms");
                    }
                    LoaderTask.this.mIsLoadingAndBindingWorkspace = false;
                }
            });
            if (z) {
                this.mUiExecutor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.12
                    @Override // java.lang.Runnable
                    public void run() {
                        Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(callbacks);
                        if (callbacksTryGetCallbacks != null) {
                            int i3 = i2;
                            if (i3 != -1001) {
                                callbacksTryGetCallbacks.onPageBoundSynchronously(i3);
                            }
                            callbacksTryGetCallbacks.executeOnNextDraw((ViewOnDrawExecutor) viewOnDrawExecutor);
                        }
                    }
                });
            }
        }

        private void loadAndBindAllApps() {
            if (LauncherModel.DEBUG_LOADERS) {
                Log.d(LauncherModel.TAG, "loadAndBindAllApps mAllAppsLoaded=" + LauncherModel.this.mAllAppsLoaded);
            }
            if (!LauncherModel.this.mAllAppsLoaded) {
                loadAllApps();
                synchronized (this) {
                    if (this.mStopped) {
                        return;
                    }
                    updateIconCache();
                    synchronized (this) {
                        if (this.mStopped) {
                            return;
                        }
                        LauncherModel.this.mAllAppsLoaded = true;
                        return;
                    }
                }
            }
            synchronized (this) {
                if (this.mStopped) {
                    return;
                }
                bindAllApps();
                bindWidgets();
            }
        }

        private void updateIconCache() {
            HashSet hashSet = new HashSet();
            synchronized (LauncherModel.sBgDataModel) {
                for (ItemInfo itemInfo : LauncherModel.sBgDataModel.itemsIdMap) {
                    if (itemInfo instanceof ShortcutInfo) {
                        ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                        if (shortcutInfo.isPromise() && shortcutInfo.getTargetComponent() != null) {
                            hashSet.add(shortcutInfo.getTargetComponent().getPackageName());
                        }
                    } else if (itemInfo instanceof LauncherAppWidgetInfo) {
                        LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
                        if (launcherAppWidgetInfo.hasRestoreFlag(2)) {
                            hashSet.add(launcherAppWidgetInfo.providerName.getPackageName());
                        }
                    }
                }
            }
            LauncherModel.this.mIconCache.updateDbIcons(hashSet);
        }

        public void bindAllApps() {
            final ArrayList arrayList = (ArrayList) LauncherModel.this.mBgAllAppsList.data.clone();
            this.mUiExecutor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.13
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(LauncherModel.this.mCallbacks.get());
                    if (callbacksTryGetCallbacks != null) {
                        callbacksTryGetCallbacks.bindAllApplications(arrayList);
                    }
                }
            });
        }

        public void bindWidgets() {
            if (LauncherModel.this.mBgWidgetsModel.getPackageSize() == 0) {
                LauncherModel.this.updateWidgetsModel(this.mContext, true);
            }
            final WidgetsModel widgetsModelClone = LauncherModel.this.mBgWidgetsModel.mo209clone();
            this.mUiExecutor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.14
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(LauncherModel.this.mCallbacks.get());
                    if (callbacksTryGetCallbacks != null) {
                        callbacksTryGetCallbacks.bindAllPackages(widgetsModelClone);
                    }
                }
            });
        }

        public void bindDeepShortcuts() {
            final MultiHashMap multiHashMapClone;
            synchronized (LauncherModel.sBgDataModel) {
                multiHashMapClone = LauncherModel.this.mBgDeepShortcutMap.clone();
            }
            this.mUiExecutor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.15
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(LauncherModel.this.mCallbacks.get());
                    if (callbacksTryGetCallbacks != null) {
                        callbacksTryGetCallbacks.bindDeepShortcutMap(multiHashMapClone);
                    }
                }
            });
        }

        private void onlyBindAllApps() {
            final Callbacks callbacks = LauncherModel.this.mCallbacks.get();
            if (callbacks == null) {
                Log.w(LauncherModel.TAG, "LoaderTask running with no launcher (onlyBindAllApps)");
                return;
            }
            final ArrayList arrayList = (ArrayList) LauncherModel.this.mBgAllAppsList.data.clone();
            final WidgetsModel widgetsModelClone = LauncherModel.this.mBgWidgetsModel.mo209clone();
            Runnable runnable = new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.16
                @Override // java.lang.Runnable
                public void run() {
                    long jUptimeMillis = SystemClock.uptimeMillis();
                    Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(callbacks);
                    if (callbacksTryGetCallbacks != null) {
                        callbacksTryGetCallbacks.bindAllApplications(arrayList);
                        callbacksTryGetCallbacks.bindAllPackages(widgetsModelClone);
                    }
                    if (LauncherModel.DEBUG_LOADERS) {
                        Log.d(LauncherModel.TAG, "bound all " + arrayList.size() + " apps from cache in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms");
                    }
                }
            };
            if (LauncherModel.sWorkerThread.getThreadId() != Process.myTid()) {
                runnable.run();
            } else {
                LauncherModel.this.mHandler.post(runnable);
            }
        }

        private void loadAllApps() {
            long jUptimeMillis = LauncherModel.DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0L;
            final Callbacks callbacks = LauncherModel.this.mCallbacks.get();
            if (callbacks == null) {
                Log.w(LauncherModel.TAG, "LoaderTask running with no launcher (loadAllApps)");
                return;
            }
            List<UserHandle> userProfiles = LauncherModel.this.mUserManager.getUserProfiles();
            LauncherModel.this.mBgAllAppsList.clear();
            Iterator<UserHandle> it = userProfiles.iterator();
            while (it.hasNext()) {
                UserHandle next = it.next();
                long jUptimeMillis2 = LauncherModel.DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0L;
                List<LauncherActivityInfo> activityList = LauncherModel.this.mLauncherApps.getActivityList(null, next);
                if (LauncherModel.DEBUG_LOADERS) {
                    Log.d(LauncherModel.TAG, "getActivityList took " + (SystemClock.uptimeMillis() - jUptimeMillis2) + "ms for user " + next);
                    Log.d(LauncherModel.TAG, "getActivityList got " + activityList.size() + " apps for user " + next);
                }
                if (activityList != null && !activityList.isEmpty()) {
                    boolean zIsQuietModeEnabled = LauncherModel.this.mUserManager.isQuietModeEnabled(next);
                    int i = 0;
                    while (i < activityList.size()) {
                        LauncherModel.this.mBgAllAppsList.add(new AppInfo(this.mContext, activityList.get(i), next, LauncherModel.this.mIconCache, zIsQuietModeEnabled));
                        i++;
                        activityList = activityList;
                        it = it;
                    }
                    Iterator<UserHandle> it2 = it;
                    List<LauncherActivityInfo> list = activityList;
                    ManagedProfileHeuristic managedProfileHeuristic = ManagedProfileHeuristic.get(this.mContext, next);
                    if (managedProfileHeuristic != null) {
                        managedProfileHeuristic.processUserApps(list);
                    }
                    it = it2;
                }
            }
            final ArrayList<AppInfo> arrayList = LauncherModel.this.mBgAllAppsList.added;
            LauncherModel.this.mBgAllAppsList.added = new ArrayList<>();
            this.mUiExecutor.execute(new Runnable() { // from class: com.android.launcher3.LauncherModel.LoaderTask.17
                @Override // java.lang.Runnable
                public void run() {
                    long jUptimeMillis3 = SystemClock.uptimeMillis();
                    Callbacks callbacksTryGetCallbacks = LoaderTask.this.tryGetCallbacks(callbacks);
                    if (callbacksTryGetCallbacks != null) {
                        callbacksTryGetCallbacks.bindAllApplications(arrayList);
                        if (LauncherModel.DEBUG_LOADERS) {
                            Log.d(LauncherModel.TAG, "bound " + arrayList.size() + " apps in " + (SystemClock.uptimeMillis() - jUptimeMillis3) + "ms");
                            return;
                        }
                        return;
                    }
                    Log.i(LauncherModel.TAG, "not binding apps: no Launcher activity");
                }
            });
            ManagedProfileHeuristic.processAllUsers(userProfiles, this.mContext);
            LauncherModel launcherModel = LauncherModel.this;
            launcherModel.loadAndBindWidgetsAndShortcuts(launcherModel.mApp.getContext(), tryGetCallbacks(callbacks), true);
            if (LauncherModel.DEBUG_LOADERS) {
                Log.d(LauncherModel.TAG, "Icons processed in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms");
            }
        }

        public void dumpState() {
            synchronized (LauncherModel.sBgDataModel) {
                Log.d(LauncherModel.TAG, "mLoaderTask.mContext=" + this.mContext);
                Log.d(LauncherModel.TAG, "mLoaderTask.mStopped=" + this.mStopped);
                Log.d(LauncherModel.TAG, "mLoaderTask.mLoadAndBindStepFinished=" + this.mLoadAndBindStepFinished);
                Log.d(LauncherModel.TAG, "mItems size=" + LauncherModel.sBgDataModel.itemsIdMap.size());
            }
        }
    }

    public void onPackageIconsUpdated(HashSet<String> updatedPackages, final UserHandle user) {
        ShortcutInfo shortcutInfo;
        ComponentName targetComponent;
        final Callbacks callback = getCallback();
        final ArrayList<AppInfo> arrayList = new ArrayList<>();
        final ArrayList arrayList2 = new ArrayList();
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            for (ItemInfo itemInfo : bgDataModel.itemsIdMap) {
                if ((itemInfo instanceof ShortcutInfo) && user.equals(itemInfo.user) && itemInfo.itemType == 0 && (targetComponent = (shortcutInfo = (ShortcutInfo) itemInfo).getTargetComponent()) != null && updatedPackages.contains(targetComponent.getPackageName())) {
                    shortcutInfo.updateIcon(this.mIconCache);
                    arrayList2.add(shortcutInfo);
                }
            }
            this.mBgAllAppsList.updateIconsAndLabels(updatedPackages, user, arrayList);
        }
        if (!arrayList2.isEmpty()) {
            this.mHandler.post(new Runnable() { // from class: com.android.launcher3.LauncherModel.19
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callback2 = LauncherModel.this.getCallback();
                    if (callback2 == null || callback != callback2) {
                        return;
                    }
                    callback2.bindShortcutsChanged(arrayList2, new ArrayList<>(), user);
                }
            });
        }
        if (!arrayList.isEmpty()) {
            this.mHandler.post(new Runnable() { // from class: com.android.launcher3.LauncherModel.20
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callback2 = LauncherModel.this.getCallback();
                    if (callback2 == null || callback != callback2) {
                        return;
                    }
                    callback2.bindAppsUpdated(arrayList);
                }
            });
        }
        loadAndBindWidgetsAndShortcuts(this.mApp.getContext(), callback, false);
    }

    void enqueuePackageUpdated(PackageUpdatedTask task) {
        LGLog.i(TAG, String.format("enqueuePackageUpdated: mOp = %s, packageName = %s, User = &s", task.getPackageUpdateOpToString(task.mOp), Arrays.toString(task.mPackages), task.mUser));
        sWorker.post(task);
    }

    class AppsAvailabilityCheck extends BroadcastReceiver {
        AppsAvailabilityCheck() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (LauncherModel.sBgDataModel) {
                LauncherAppsCompat launcherAppsCompat = LauncherAppsCompat.getInstance(LauncherModel.this.mApp.getContext());
                PackageManager packageManager = context.getPackageManager();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (Map.Entry<UserHandle, HashSet<String>> entry : LauncherModel.sPendingPackages.entrySet()) {
                    UserHandle key = entry.getKey();
                    arrayList.clear();
                    arrayList2.clear();
                    for (String str : entry.getValue()) {
                        if (!launcherAppsCompat.isPackageEnabledForProfile(str, key)) {
                            if (launcherAppsCompat.isAppEnabled(packageManager, str, 8192)) {
                                Launcher.addDumpLog(LauncherModel.TAG, "Package found on sd-card: " + str, true);
                                arrayList2.add(str);
                            } else {
                                Launcher.addDumpLog(LauncherModel.TAG, "Package not found: " + str, true);
                                arrayList.add(str);
                            }
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        LauncherModel.this.enqueuePackageUpdated(LauncherModel.this.new PackageUpdatedTask(3, (String[]) arrayList.toArray(new String[arrayList.size()]), key));
                    }
                    if (!arrayList2.isEmpty()) {
                        LauncherModel.this.enqueuePackageUpdated(LauncherModel.this.new PackageUpdatedTask(4, (String[]) arrayList2.toArray(new String[arrayList2.size()]), key));
                    }
                }
                LauncherModel.sPendingPackages.clear();
            }
        }
    }

    public class PackageUpdatedTask implements Runnable {
        public static final int OP_ADD = 1;
        public static final int OP_NONE = 0;
        public static final int OP_REMOVE = 3;
        public static final int OP_SUSPEND = 5;
        public static final int OP_UNAVAILABLE = 4;
        public static final int OP_UNSUSPEND = 6;
        public static final int OP_UPDATE = 2;
        public static final int OP_USER_AVAILABILITY_CHANGE = 7;
        int mOp;
        String[] mPackages;
        UserHandle mUser;

        public PackageUpdatedTask(int op, String[] packages, UserHandle user) {
            this.mOp = op;
            this.mPackages = packages;
            this.mUser = user;
        }

        /* JADX DEBUG: Multi-variable search result rejected for r6v56, resolved type: boolean */
        /* JADX DEBUG: Multi-variable search result rejected for r6v57, resolved type: boolean */
        /* JADX DEBUG: Multi-variable search result rejected for r6v58, resolved type: boolean */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:199:0x0536 A[Catch: all -> 0x06ca, TryCatch #0 {, blocks: (B:145:0x0404, B:146:0x040c, B:148:0x0412, B:150:0x041e, B:152:0x0428, B:154:0x042e, B:156:0x0438, B:158:0x0446, B:160:0x044f, B:162:0x0455, B:164:0x0461, B:166:0x046d, B:168:0x0476, B:170:0x0497, B:172:0x04a1, B:176:0x04b2, B:177:0x04b5, B:181:0x04c8, B:182:0x04cc, B:185:0x04e9, B:187:0x04f7, B:189:0x04fb, B:191:0x0516, B:201:0x053b, B:199:0x0536, B:204:0x054a, B:206:0x054e, B:208:0x0553, B:210:0x055f, B:212:0x0566, B:214:0x0572, B:219:0x058d), top: B:275:0x0404 }] */
        /* JADX WARN: Removed duplicated region for block: B:201:0x053b A[Catch: all -> 0x06ca, TryCatch #0 {, blocks: (B:145:0x0404, B:146:0x040c, B:148:0x0412, B:150:0x041e, B:152:0x0428, B:154:0x042e, B:156:0x0438, B:158:0x0446, B:160:0x044f, B:162:0x0455, B:164:0x0461, B:166:0x046d, B:168:0x0476, B:170:0x0497, B:172:0x04a1, B:176:0x04b2, B:177:0x04b5, B:181:0x04c8, B:182:0x04cc, B:185:0x04e9, B:187:0x04f7, B:189:0x04fb, B:191:0x0516, B:201:0x053b, B:199:0x0536, B:204:0x054a, B:206:0x054e, B:208:0x0553, B:210:0x055f, B:212:0x0566, B:214:0x0572, B:219:0x058d), top: B:275:0x0404 }] */
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
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                r23 = this;
                r1 = r23
                java.lang.String r0 = "Launcher.Model"
                java.lang.String r2 = "PackageUpdatedTask.run() : Op{%s}, Package{%s}, %s"
                r3 = 3
                java.lang.Object[] r4 = new java.lang.Object[r3]
                int r5 = r1.mOp
                java.lang.String r5 = r1.getPackageUpdateOpToString(r5)
                r6 = 0
                r4[r6] = r5
                java.lang.String[] r5 = r1.mPackages
                java.lang.String r5 = java.util.Arrays.toString(r5)
                r7 = 1
                r4[r7] = r5
                android.os.UserHandle r5 = r1.mUser
                r8 = 2
                r4[r8] = r5
                java.lang.String r2 = java.lang.String.format(r2, r4)
                com.lge.launcher3.util.LGLog.i(r0, r2)
                r0 = 0
                boolean r2 = com.lge.launcher3.memory.MemoryUtils.hasAvailableFileSystemMemory(r0, r6)
                if (r2 != 0) goto L36
                java.lang.String r0 = "Launcher.Model"
                java.lang.String r2 = "Memory is full. so PackageUpdatedTask.run() is canceled."
                com.lge.launcher3.util.LGLog.i(r0, r2)
                return
            L36:
                com.android.launcher3.LauncherModel r2 = com.android.launcher3.LauncherModel.this
                boolean r2 = r2.mHasLoaderCompletedOnce
                if (r2 != 0) goto L3d
                return
            L3d:
                com.android.launcher3.LauncherModel r2 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherAppState r2 = r2.mApp
                android.content.Context r2 = r2.getContext()
                java.lang.String[] r4 = r1.mPackages
                int r5 = r4.length
                com.android.launcher3.util.FlagOp r9 = com.android.launcher3.util.FlagOp.NO_OP
                java.util.HashSet r10 = new java.util.HashSet
                java.util.List r11 = java.util.Arrays.asList(r4)
                r10.<init>(r11)
                com.android.launcher3.util.StringFilter r10 = com.android.launcher3.util.StringFilter.of(r10)
                int r11 = r1.mOp
                switch(r11) {
                    case 1: goto L27e;
                    case 2: goto L162;
                    case 3: goto Lb6;
                    case 4: goto L122;
                    case 5: goto L84;
                    case 6: goto L84;
                    case 7: goto L5e;
                    default: goto L5c;
                }
            L5c:
                goto L2c7
            L5e:
                com.android.launcher3.compat.UserManagerCompat r9 = com.android.launcher3.compat.UserManagerCompat.getInstance(r2)
                android.os.UserHandle r10 = r1.mUser
                boolean r9 = r9.isQuietModeEnabled(r10)
                r10 = 8
                if (r9 == 0) goto L71
                com.android.launcher3.util.FlagOp r9 = com.android.launcher3.util.FlagOp.addFlag(r10)
                goto L75
            L71:
                com.android.launcher3.util.FlagOp r9 = com.android.launcher3.util.FlagOp.removeFlag(r10)
            L75:
                com.android.launcher3.util.StringFilter r10 = com.android.launcher3.util.StringFilter.matchesAll()
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r11 = r11.mBgAllAppsList
                android.os.UserHandle r12 = r1.mUser
                r11.updatePackageFlags(r10, r12, r9)
                goto L2c7
            L84:
                r9 = 5
                r12 = 4
                if (r11 != r9) goto L8d
                com.android.launcher3.util.FlagOp r9 = com.android.launcher3.util.FlagOp.addFlag(r12)
                goto L91
            L8d:
                com.android.launcher3.util.FlagOp r9 = com.android.launcher3.util.FlagOp.removeFlag(r12)
            L91:
                boolean r11 = com.android.launcher3.LauncherModel.DEBUG_LOADERS
                if (r11 == 0) goto Lab
                java.lang.String r11 = "Launcher.Model"
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                java.lang.String r13 = "mAllAppsList.(un)suspend "
                r12.append(r13)
                r12.append(r5)
                java.lang.String r12 = r12.toString()
                android.util.Log.d(r11, r12)
            Lab:
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r11 = r11.mBgAllAppsList
                android.os.UserHandle r12 = r1.mUser
                r11.updatePackageFlags(r10, r12, r9)
                goto L2c7
            Lb6:
                android.os.UserHandle r9 = r1.mUser
                com.android.launcher3.util.ManagedProfileHeuristic r9 = com.android.launcher3.util.ManagedProfileHeuristic.get(r2, r9)
                if (r9 == 0) goto Lc3
                java.lang.String[] r11 = r1.mPackages
                r9.processPackageRemoved(r11)
            Lc3:
                r9 = r6
            Lc4:
                if (r9 >= r5) goto L122
                com.lge.launcher3.util.LGHomeFeature$Config r11 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_FI_SIM_MODE
                boolean r11 = r11.getValue()
                if (r11 == 0) goto Led
                r11 = r4[r9]
                android.content.ComponentName r12 = com.lge.launcher3.receiver.FiSimMode.mFiDialCN
                java.lang.String r12 = r12.getPackageName()
                boolean r11 = r11.equals(r12)
                if (r11 != 0) goto Lea
                r11 = r4[r9]
                android.content.ComponentName r12 = com.lge.launcher3.receiver.FiSimMode.mLGDialCN
                java.lang.String r12 = r12.getPackageName()
                boolean r11 = r11.equals(r12)
                if (r11 == 0) goto Led
            Lea:
                com.lge.launcher3.receiver.FiSimMode.changeFiSimMode(r2)
            Led:
                boolean r11 = com.android.launcher3.LauncherModel.DEBUG_LOADERS
                if (r11 == 0) goto L109
                java.lang.String r11 = "Launcher.Model"
                r12 = r4[r9]
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r14 = "mAllAppsList.removePackage "
                r13.append(r14)
                r13.append(r12)
                java.lang.String r12 = r13.toString()
                android.util.Log.d(r11, r12)
            L109:
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.icons.IconCache r11 = r11.mIconCache
                r12 = r4[r9]
                android.os.UserHandle r13 = r1.mUser
                r11.removeIconsForPkg(r12, r13)
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r11 = r11.mBgAllAppsList
                r12 = r4[r9]
                android.os.UserHandle r13 = r1.mUser
                r11.removePackage(r12, r13)
                int r9 = r9 + 1
                goto Lc4
            L122:
                r9 = r6
            L123:
                if (r9 >= r5) goto L153
                boolean r11 = com.android.launcher3.LauncherModel.DEBUG_LOADERS
                if (r11 == 0) goto L141
                java.lang.String r11 = "Launcher.Model"
                r12 = r4[r9]
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r14 = "mAllAppsList.removePackage "
                r13.append(r14)
                r13.append(r12)
                java.lang.String r12 = r13.toString()
                android.util.Log.d(r11, r12)
            L141:
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherAppState r11 = r11.mApp
                com.android.launcher3.WidgetPreviewLoader r11 = r11.getWidgetCache()
                r12 = r4[r9]
                android.os.UserHandle r13 = r1.mUser
                r11.removePackage(r12, r13)
                int r9 = r9 + 1
                goto L123
            L153:
                com.android.launcher3.util.FlagOp r9 = com.android.launcher3.util.FlagOp.addFlag(r8)
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r11 = r11.mBgAllAppsList
                android.os.UserHandle r12 = r1.mUser
                r11.updatePackageFlags(r10, r12, r9)
                goto L2c7
            L162:
                r9 = r6
            L163:
                if (r9 >= r5) goto L270
                boolean r11 = com.android.launcher3.LauncherModel.DEBUG_LOADERS
                if (r11 == 0) goto L181
                java.lang.String r11 = "Launcher.Model"
                r12 = r4[r9]
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r14 = "mAllAppsList.updatePackage "
                r13.append(r14)
                r13.append(r12)
                java.lang.String r12 = r13.toString()
                android.util.Log.d(r11, r12)
            L181:
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.icons.IconCache r11 = r11.mIconCache
                r12 = r4[r9]
                android.os.UserHandle r13 = r1.mUser
                r11.updateIconsForPkg(r12, r13)
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r11 = r11.mBgAllAppsList
                r12 = r4[r9]
                android.os.UserHandle r13 = r1.mUser
                r11.updatePackage(r2, r12, r13)
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherAppState r11 = r11.mApp
                com.android.launcher3.WidgetPreviewLoader r11 = r11.getWidgetCache()
                r12 = r4[r9]
                android.os.UserHandle r13 = r1.mUser
                r11.removePackage(r12, r13)
                com.lge.launcher3.util.LGHomeFeature$Config r11 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_FI_SIM_MODE
                boolean r11 = r11.getValue()
                if (r11 == 0) goto L1cd
                r11 = r4[r9]
                android.content.ComponentName r12 = com.lge.launcher3.receiver.FiSimMode.mFiDialCN
                java.lang.String r12 = r12.getPackageName()
                boolean r11 = r11.equals(r12)
                if (r11 != 0) goto L1ca
                r11 = r4[r9]
                android.content.ComponentName r12 = com.lge.launcher3.receiver.FiSimMode.mLGDialCN
                java.lang.String r12 = r12.getPackageName()
                boolean r11 = r11.equals(r12)
                if (r11 == 0) goto L1cd
            L1ca:
                com.lge.launcher3.receiver.FiSimMode.changeFiSimMode(r2)
            L1cd:
                com.lge.launcher3.util.LGHomeFeature$Config r11 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN
                boolean r11 = r11.getValue()
                if (r11 == 0) goto L205
                r11 = r4[r9]
                if (r11 == 0) goto L205
                r11 = r4[r9]
                java.lang.String r12 = com.lge.launcher3.operator.VZWSideScreenManager.sPackageName
                boolean r11 = r11.equals(r12)
                if (r11 == 0) goto L205
                android.content.pm.PackageManager r11 = r2.getPackageManager()
                com.lge.launcher3.operator.VZWSideScreenManager.setAppEnabled(r11)
                java.lang.String r11 = "homesettingsprefs_key_vzw_sidescreen"
                boolean r12 = com.lge.launcher3.operator.VZWSideScreenManager.isAppEnabled()
                com.lge.launcher3.homesettings.SettingsSearchUtils.updateVisible(r2, r11, r12)
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherModel$Callbacks r11 = r11.getCallback()
                com.android.launcher3.LauncherModel r12 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.DeferredHandler r12 = r12.mHandler
                com.android.launcher3.LauncherModel$PackageUpdatedTask$1 r13 = new com.android.launcher3.LauncherModel$PackageUpdatedTask$1
                r13.<init>()
                r12.post(r13)
            L205:
                com.lge.launcher3.util.LGHomeFeature$Config r11 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW
                boolean r11 = r11.getValue()
                if (r11 == 0) goto L248
                r11 = r4[r9]
                if (r11 == 0) goto L248
                r11 = r4[r9]
                java.lang.String r12 = com.lge.launcher3.operator.GoogleNowManager.sPackageName
                boolean r11 = r11.equals(r12)
                if (r11 == 0) goto L248
                android.content.pm.PackageManager r11 = r2.getPackageManager()
                com.lge.launcher3.operator.GoogleNowManager.setAppEnabled(r11)
                boolean r11 = com.lge.launcher3.operator.GoogleNowManager.isAppEnabled()
                com.lge.launcher3.util.LGHomeFeature$Config r12 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN
                boolean r12 = r12.getValue()
                if (r12 == 0) goto L231
                java.lang.String r12 = "homesettingsprefs_key_left_home_screen_google_feed"
                goto L233
            L231:
                java.lang.String r12 = "homesettingsprefs_key_google_now"
            L233:
                com.lge.launcher3.homesettings.SettingsSearchUtils.updateVisible(r2, r12, r11)
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherModel$Callbacks r11 = r11.getCallback()
                com.android.launcher3.LauncherModel r12 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.DeferredHandler r12 = r12.mHandler
                com.android.launcher3.LauncherModel$PackageUpdatedTask$2 r13 = new com.android.launcher3.LauncherModel$PackageUpdatedTask$2
                r13.<init>()
                r12.post(r13)
            L248:
                com.lge.launcher3.util.LGHomeFeature$Config r11 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME
                boolean r11 = r11.getValue()
                if (r11 == 0) goto L26c
                com.lge.launcher3.util.LGHomeFeature$Config r11 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH
                boolean r11 = r11.getValue()
                if (r11 == 0) goto L26c
                java.lang.String r11 = "com.lge.abba"
                r12 = r4[r9]
                boolean r11 = r11.equals(r12)
                if (r11 == 0) goto L26c
                java.lang.String r11 = "Launcher.Model"
                java.lang.String r12 = "Package update - call checkDefineValuesForSwipeDownHome."
                com.lge.launcher3.util.LGLog.i(r11, r12)
                com.android.launcher3.Utilities.checkDefineValuesForSwipeDownHome(r2)
            L26c:
                int r9 = r9 + 1
                goto L163
            L270:
                com.android.launcher3.util.FlagOp r9 = com.android.launcher3.util.FlagOp.removeFlag(r8)
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r11 = r11.mBgAllAppsList
                android.os.UserHandle r12 = r1.mUser
                r11.updatePackageFlags(r10, r12, r9)
                goto L2c7
            L27e:
                r9 = r6
            L27f:
                if (r9 >= r5) goto L2b6
                boolean r11 = com.android.launcher3.LauncherModel.DEBUG_LOADERS
                if (r11 == 0) goto L29d
                java.lang.String r11 = "Launcher.Model"
                r12 = r4[r9]
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r14 = "mAllAppsList.addPackage "
                r13.append(r14)
                r13.append(r12)
                java.lang.String r12 = r13.toString()
                android.util.Log.d(r11, r12)
            L29d:
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.icons.IconCache r11 = r11.mIconCache
                r12 = r4[r9]
                android.os.UserHandle r13 = r1.mUser
                r11.updateIconsForPkg(r12, r13)
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r11 = r11.mBgAllAppsList
                r12 = r4[r9]
                android.os.UserHandle r13 = r1.mUser
                r11.addPackage(r2, r12, r13)
                int r9 = r9 + 1
                goto L27f
            L2b6:
                android.os.UserHandle r9 = r1.mUser
                com.android.launcher3.util.ManagedProfileHeuristic r9 = com.android.launcher3.util.ManagedProfileHeuristic.get(r2, r9)
                if (r9 == 0) goto L2c3
                java.lang.String[] r11 = r1.mPackages
                r9.processPackageAdd(r11)
            L2c3:
                com.android.launcher3.util.FlagOp r9 = com.android.launcher3.util.FlagOp.removeFlag(r8)
            L2c7:
                java.util.ArrayList r11 = new java.util.ArrayList
                r11.<init>()
                com.android.launcher3.LauncherModel r12 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r12 = r12.mBgAllAppsList
                java.util.ArrayList<com.android.launcher3.model.data.AppInfo> r12 = r12.added
                int r12 = r12.size()
                if (r12 <= 0) goto L2ed
                java.util.ArrayList r12 = new java.util.ArrayList
                com.android.launcher3.LauncherModel r13 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r13 = r13.mBgAllAppsList
                java.util.ArrayList<com.android.launcher3.model.data.AppInfo> r13 = r13.added
                r12.<init>(r13)
                com.android.launcher3.LauncherModel r13 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r13 = r13.mBgAllAppsList
                java.util.ArrayList<com.android.launcher3.model.data.AppInfo> r13 = r13.added
                r13.clear()
                goto L2ee
            L2ed:
                r12 = r0
            L2ee:
                com.android.launcher3.LauncherModel r13 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r13 = r13.mBgAllAppsList
                java.util.ArrayList<com.android.launcher3.model.data.AppInfo> r13 = r13.modified
                int r13 = r13.size()
                if (r13 <= 0) goto L30f
                java.util.ArrayList r13 = new java.util.ArrayList
                com.android.launcher3.LauncherModel r14 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r14 = r14.mBgAllAppsList
                java.util.ArrayList<com.android.launcher3.model.data.AppInfo> r14 = r14.modified
                r13.<init>(r14)
                com.android.launcher3.LauncherModel r14 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r14 = r14.mBgAllAppsList
                java.util.ArrayList<com.android.launcher3.model.data.AppInfo> r14 = r14.modified
                r14.clear()
                goto L310
            L30f:
                r13 = r0
            L310:
                com.android.launcher3.LauncherModel r14 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r14 = r14.mBgAllAppsList
                java.util.ArrayList<com.android.launcher3.model.data.AppInfo> r14 = r14.removed
                int r14 = r14.size()
                if (r14 <= 0) goto L32e
                com.android.launcher3.LauncherModel r14 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r14 = r14.mBgAllAppsList
                java.util.ArrayList<com.android.launcher3.model.data.AppInfo> r14 = r14.removed
                r11.addAll(r14)
                com.android.launcher3.LauncherModel r14 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.allapps.AllAppsList r14 = r14.mBgAllAppsList
                java.util.ArrayList<com.android.launcher3.model.data.AppInfo> r14 = r14.removed
                r14.clear()
            L32e:
                java.util.HashMap r14 = new java.util.HashMap
                r14.<init>()
                if (r12 == 0) goto L3b9
                com.lge.launcher3.util.LGHomeFeature$Config r15 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME
                boolean r15 = r15.getValue()
                if (r15 == 0) goto L36a
                java.util.ArrayList r15 = r1.addSilentOTAToSwivel(r12)
                if (r15 == 0) goto L36a
                boolean r16 = r15.isEmpty()
                if (r16 != 0) goto L36a
                java.util.Iterator r16 = r15.iterator()
            L34d:
                boolean r17 = r16.hasNext()
                if (r17 == 0) goto L365
                java.lang.Object r17 = r16.next()
                com.android.launcher3.ShortcutInfo r17 = (com.android.launcher3.ShortcutInfo) r17
                android.content.ComponentName r17 = r17.getTargetComponent()
                java.lang.String r17 = r17.getPackageName()
                com.lge.launcher3.silentota.SilentOTASwivel.updateSilentSwivelPackage(r17)
                goto L34d
            L365:
                com.android.launcher3.LauncherModel r3 = com.android.launcher3.LauncherModel.this
                r3.addItemsOnSwivelHome(r15)
            L36a:
                boolean r3 = com.lge.launcher3.util.LGHomeFeature.isEnableDefaultHome()
                if (r3 == 0) goto L395
                java.util.ArrayList r3 = r1.addManagedProfileAppsToWorkFolder(r2, r12)
                java.util.ArrayList r3 = r1.addSilentOTAToFolder(r2, r3)
                com.android.launcher3.LauncherModel r15 = com.android.launcher3.LauncherModel.this
                r15.addAndBindAddedWorkspaceItems(r2, r3)
                com.android.launcher3.util.MainThreadInitializedObject<com.android.quickstep.SysUINavigationMode> r15 = com.android.quickstep.SysUINavigationMode.INSTANCE
                java.lang.Object r15 = r15.lambda$get$0$MainThreadInitializedObject(r2)
                com.android.quickstep.SysUINavigationMode r15 = (com.android.quickstep.SysUINavigationMode) r15
                com.android.quickstep.SysUINavigationMode$Mode r15 = r15.getMode()
                boolean r15 = r15.hasGestures
                if (r15 == 0) goto L3a3
                com.android.launcher3.LauncherModel r15 = com.android.launcher3.LauncherModel.this
                int r0 = r1.mOp
                r15.addAppsToAllApps(r2, r3, r0)
                goto L3a3
            L395:
                r1.addSilentOTAToFolder(r2, r12)
                java.util.ArrayList r0 = r1.addSilentOTAToFolderAllApps(r2, r12)
                com.android.launcher3.LauncherModel r3 = com.android.launcher3.LauncherModel.this
                int r15 = r1.mOp
                r3.addAppsToAllApps(r2, r0, r15)
            L3a3:
                java.util.Iterator r0 = r12.iterator()
            L3a7:
                boolean r3 = r0.hasNext()
                if (r3 == 0) goto L3b9
                java.lang.Object r3 = r0.next()
                com.android.launcher3.model.data.AppInfo r3 = (com.android.launcher3.model.data.AppInfo) r3
                android.content.ComponentName r12 = r3.componentName
                r14.put(r12, r3)
                goto L3a7
            L3b9:
                if (r13 == 0) goto L3e3
                com.android.launcher3.LauncherModel r0 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherModel$Callbacks r0 = r0.getCallback()
                java.util.Iterator r3 = r13.iterator()
            L3c5:
                boolean r12 = r3.hasNext()
                if (r12 == 0) goto L3d7
                java.lang.Object r12 = r3.next()
                com.android.launcher3.model.data.AppInfo r12 = (com.android.launcher3.model.data.AppInfo) r12
                android.content.ComponentName r15 = r12.componentName
                r14.put(r15, r12)
                goto L3c5
            L3d7:
                com.android.launcher3.LauncherModel r3 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.DeferredHandler r3 = r3.mHandler
                com.android.launcher3.LauncherModel$PackageUpdatedTask$3 r12 = new com.android.launcher3.LauncherModel$PackageUpdatedTask$3
                r12.<init>()
                r3.post(r12)
            L3e3:
                int r0 = r1.mOp
                if (r0 == r7) goto L3f2
                com.android.launcher3.util.FlagOp r0 = com.android.launcher3.util.FlagOp.NO_OP
                if (r9 == r0) goto L3ec
                goto L3f2
            L3ec:
                r8 = r6
                r22 = r11
                r11 = r7
                goto L5cd
            L3f2:
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                java.util.ArrayList r12 = new java.util.ArrayList
                r12.<init>()
                com.android.launcher3.model.BgDataModel r13 = com.android.launcher3.LauncherModel.sBgDataModel
                monitor-enter(r13)
                com.android.launcher3.model.BgDataModel r15 = com.android.launcher3.LauncherModel.sBgDataModel     // Catch: java.lang.Throwable -> L6ca
                com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.ItemInfo> r15 = r15.itemsIdMap     // Catch: java.lang.Throwable -> L6ca
                java.util.Iterator r15 = r15.iterator()     // Catch: java.lang.Throwable -> L6ca
            L40c:
                boolean r18 = r15.hasNext()     // Catch: java.lang.Throwable -> L6ca
                if (r18 == 0) goto L589
                java.lang.Object r18 = r15.next()     // Catch: java.lang.Throwable -> L6ca
                r7 = r18
                com.android.launcher3.model.data.ItemInfo r7 = (com.android.launcher3.model.data.ItemInfo) r7     // Catch: java.lang.Throwable -> L6ca
                boolean r8 = r7 instanceof com.android.launcher3.ShortcutInfo     // Catch: java.lang.Throwable -> L6ca
                if (r8 == 0) goto L545
                android.os.UserHandle r8 = r1.mUser     // Catch: java.lang.Throwable -> L6ca
                android.os.UserHandle r6 = r7.user     // Catch: java.lang.Throwable -> L6ca
                boolean r6 = r8.equals(r6)     // Catch: java.lang.Throwable -> L6ca
                if (r6 == 0) goto L53f
                com.android.launcher3.ShortcutInfo r7 = (com.android.launcher3.ShortcutInfo) r7     // Catch: java.lang.Throwable -> L6ca
                android.content.Intent$ShortcutIconResource r6 = r7.iconResource     // Catch: java.lang.Throwable -> L6ca
                if (r6 == 0) goto L44e
                android.content.Intent$ShortcutIconResource r6 = r7.iconResource     // Catch: java.lang.Throwable -> L6ca
                java.lang.String r6 = r6.packageName     // Catch: java.lang.Throwable -> L6ca
                boolean r6 = r10.matches(r6)     // Catch: java.lang.Throwable -> L6ca
                if (r6 == 0) goto L44e
                android.content.Intent$ShortcutIconResource r6 = r7.iconResource     // Catch: java.lang.Throwable -> L6ca
                java.lang.String r6 = r6.packageName     // Catch: java.lang.Throwable -> L6ca
                android.content.Intent$ShortcutIconResource r8 = r7.iconResource     // Catch: java.lang.Throwable -> L6ca
                java.lang.String r8 = r8.resourceName     // Catch: java.lang.Throwable -> L6ca
                android.graphics.Bitmap r6 = com.android.launcher3.Utilities.createIconBitmap(r6, r8, r2)     // Catch: java.lang.Throwable -> L6ca
                if (r6 == 0) goto L44e
                r7.setIcon(r6)     // Catch: java.lang.Throwable -> L6ca
                r6 = 0
                r7.usingFallbackIcon = r6     // Catch: java.lang.Throwable -> L6ca
                r6 = 1
                goto L44f
            L44e:
                r6 = 0
            L44f:
                android.content.ComponentName r8 = r7.getTargetComponent()     // Catch: java.lang.Throwable -> L6ca
                if (r8 == 0) goto L528
                r19 = r6
                java.lang.String r6 = r8.getPackageName()     // Catch: java.lang.Throwable -> L6ca
                boolean r6 = r10.matches(r6)     // Catch: java.lang.Throwable -> L6ca
                if (r6 == 0) goto L52a
                java.lang.Object r6 = r14.get(r8)     // Catch: java.lang.Throwable -> L6ca
                com.android.launcher3.model.data.AppInfo r6 = (com.android.launcher3.model.data.AppInfo) r6     // Catch: java.lang.Throwable -> L6ca
                boolean r20 = r7.isPromise()     // Catch: java.lang.Throwable -> L6ca
                if (r20 == 0) goto L4e0
                r20 = r6
                r6 = 2
                boolean r19 = r7.hasStatusFlag(r6)     // Catch: java.lang.Throwable -> L6ca
                if (r19 == 0) goto L4c0
                android.content.pm.PackageManager r6 = r2.getPackageManager()     // Catch: java.lang.Throwable -> L6ca
                r21 = r15
                android.content.Intent r15 = new android.content.Intent     // Catch: java.lang.Throwable -> L6ca
                r22 = r11
                java.lang.String r11 = "android.intent.action.MAIN"
                r15.<init>(r11)     // Catch: java.lang.Throwable -> L6ca
                android.content.Intent r11 = r15.setComponent(r8)     // Catch: java.lang.Throwable -> L6ca
                java.lang.String r15 = "android.intent.category.LAUNCHER"
                android.content.Intent r11 = r11.addCategory(r15)     // Catch: java.lang.Throwable -> L6ca
                r15 = 65536(0x10000, float:9.1835E-41)
                android.content.pm.ResolveInfo r11 = r6.resolveActivity(r11, r15)     // Catch: java.lang.Throwable -> L6ca
                if (r11 != 0) goto L4c4
                java.lang.String r8 = r8.getPackageName()     // Catch: java.lang.Throwable -> L6ca
                android.content.Intent r6 = r6.getLaunchIntentForPackage(r8)     // Catch: java.lang.Throwable -> L6ca
                if (r6 == 0) goto L4ad
                android.content.ComponentName r8 = r6.getComponent()     // Catch: java.lang.Throwable -> L6ca
                java.lang.Object r8 = r14.get(r8)     // Catch: java.lang.Throwable -> L6ca
                com.android.launcher3.model.data.AppInfo r8 = (com.android.launcher3.model.data.AppInfo) r8     // Catch: java.lang.Throwable -> L6ca
                r20 = r8
            L4ad:
                if (r6 == 0) goto L4b5
                if (r20 != 0) goto L4b2
                goto L4b5
            L4b2:
                r7.promisedIntent = r6     // Catch: java.lang.Throwable -> L6ca
                goto L4c4
            L4b5:
                r3.add(r7)     // Catch: java.lang.Throwable -> L6ca
                r15 = r21
                r11 = r22
                r6 = 0
                r7 = 1
                goto L586
            L4c0:
                r22 = r11
                r21 = r15
            L4c4:
                r6 = r20
                if (r6 == 0) goto L4cc
                int r8 = r6.flags     // Catch: java.lang.Throwable -> L6ca
                r7.flags = r8     // Catch: java.lang.Throwable -> L6ca
            L4cc:
                android.content.Intent r8 = r7.promisedIntent     // Catch: java.lang.Throwable -> L6ca
                r7.intent = r8     // Catch: java.lang.Throwable -> L6ca
                r8 = 0
                r7.promisedIntent = r8     // Catch: java.lang.Throwable -> L6ca
                r8 = 0
                r7.status = r8     // Catch: java.lang.Throwable -> L6ca
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L6ca
                com.android.launcher3.icons.IconCache r11 = r11.mIconCache     // Catch: java.lang.Throwable -> L6ca
                r7.updateIcon(r11)     // Catch: java.lang.Throwable -> L6ca
                r19 = 1
                goto L4e7
            L4e0:
                r20 = r6
                r22 = r11
                r21 = r15
                r8 = 0
            L4e7:
                if (r6 == 0) goto L514
                java.lang.String r11 = "android.intent.action.MAIN"
                android.content.Intent r15 = r7.intent     // Catch: java.lang.Throwable -> L6ca
                java.lang.String r15 = r15.getAction()     // Catch: java.lang.Throwable -> L6ca
                boolean r11 = r11.equals(r15)     // Catch: java.lang.Throwable -> L6ca
                if (r11 == 0) goto L514
                int r11 = r7.itemType     // Catch: java.lang.Throwable -> L6ca
                if (r11 != 0) goto L514
                com.android.launcher3.LauncherModel r11 = com.android.launcher3.LauncherModel.this     // Catch: java.lang.Throwable -> L6ca
                com.android.launcher3.icons.IconCache r11 = r11.mIconCache     // Catch: java.lang.Throwable -> L6ca
                r7.updateIcon(r11)     // Catch: java.lang.Throwable -> L6ca
                java.lang.CharSequence r11 = r6.title     // Catch: java.lang.Throwable -> L6ca
                java.lang.String r11 = com.android.launcher3.Utilities.trim(r11)     // Catch: java.lang.Throwable -> L6ca
                r7.title = r11     // Catch: java.lang.Throwable -> L6ca
                java.lang.CharSequence r11 = r6.contentDescription     // Catch: java.lang.Throwable -> L6ca
                r7.contentDescription = r11     // Catch: java.lang.Throwable -> L6ca
                int r6 = r6.flags     // Catch: java.lang.Throwable -> L6ca
                r7.flags = r6     // Catch: java.lang.Throwable -> L6ca
                r6 = 1
                goto L516
            L514:
                r6 = r19
            L516:
                int r11 = r7.runtimeStatusFlags     // Catch: java.lang.Throwable -> L6ca
                int r15 = r7.runtimeStatusFlags     // Catch: java.lang.Throwable -> L6ca
                int r15 = r9.apply(r15)     // Catch: java.lang.Throwable -> L6ca
                r7.runtimeStatusFlags = r15     // Catch: java.lang.Throwable -> L6ca
                int r15 = r7.runtimeStatusFlags     // Catch: java.lang.Throwable -> L6ca
                if (r15 == r11) goto L526
                r11 = 1
                goto L532
            L526:
                r11 = r8
                goto L532
            L528:
                r19 = r6
            L52a:
                r22 = r11
                r21 = r15
                r8 = 0
                r11 = r8
                r6 = r19
            L532:
                if (r6 != 0) goto L536
                if (r11 == 0) goto L539
            L536:
                r0.add(r7)     // Catch: java.lang.Throwable -> L6ca
            L539:
                if (r6 == 0) goto L57f
                com.android.launcher3.LauncherModel.updateItemInDatabase(r2, r7)     // Catch: java.lang.Throwable -> L6ca
                goto L57f
            L53f:
                r22 = r11
                r21 = r15
                r8 = 0
                goto L54a
            L545:
                r8 = r6
                r22 = r11
                r21 = r15
            L54a:
                boolean r6 = r7 instanceof com.android.launcher3.model.data.LauncherAppWidgetInfo     // Catch: java.lang.Throwable -> L6ca
                if (r6 == 0) goto L57f
                int r6 = r1.mOp     // Catch: java.lang.Throwable -> L6ca
                r11 = 1
                if (r6 != r11) goto L580
                com.android.launcher3.model.data.LauncherAppWidgetInfo r7 = (com.android.launcher3.model.data.LauncherAppWidgetInfo) r7     // Catch: java.lang.Throwable -> L6ca
                android.os.UserHandle r6 = r1.mUser     // Catch: java.lang.Throwable -> L6ca
                android.os.UserHandle r15 = r7.user     // Catch: java.lang.Throwable -> L6ca
                boolean r6 = r6.equals(r15)     // Catch: java.lang.Throwable -> L6ca
                if (r6 == 0) goto L580
                r6 = 2
                boolean r15 = r7.hasRestoreFlag(r6)     // Catch: java.lang.Throwable -> L6ca
                if (r15 == 0) goto L580
                android.content.ComponentName r6 = r7.providerName     // Catch: java.lang.Throwable -> L6ca
                java.lang.String r6 = r6.getPackageName()     // Catch: java.lang.Throwable -> L6ca
                boolean r6 = r10.matches(r6)     // Catch: java.lang.Throwable -> L6ca
                if (r6 == 0) goto L580
                int r6 = r7.restoreStatus     // Catch: java.lang.Throwable -> L6ca
                r6 = r6 & (-3)
                r7.restoreStatus = r6     // Catch: java.lang.Throwable -> L6ca
                r12.add(r7)     // Catch: java.lang.Throwable -> L6ca
                com.android.launcher3.LauncherModel.updateItemInDatabase(r2, r7)     // Catch: java.lang.Throwable -> L6ca
                goto L580
            L57f:
                r11 = 1
            L580:
                r6 = r8
                r7 = r11
                r15 = r21
                r11 = r22
            L586:
                r8 = 2
                goto L40c
            L589:
                r8 = r6
                r22 = r11
                r11 = r7
                monitor-exit(r13)     // Catch: java.lang.Throwable -> L6ca
                boolean r6 = r0.isEmpty()
                if (r6 == 0) goto L59a
                boolean r6 = r3.isEmpty()
                if (r6 != 0) goto L5b5
            L59a:
                com.android.launcher3.LauncherModel r6 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherModel$Callbacks r6 = r6.getCallback()
                com.android.launcher3.LauncherModel r7 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.DeferredHandler r7 = r7.mHandler
                com.android.launcher3.LauncherModel$PackageUpdatedTask$4 r9 = new com.android.launcher3.LauncherModel$PackageUpdatedTask$4
                r9.<init>()
                r7.post(r9)
                boolean r0 = r3.isEmpty()
                if (r0 != 0) goto L5b5
                com.android.launcher3.LauncherModel.deleteItemsFromDatabase(r2, r3)
            L5b5:
                boolean r0 = r12.isEmpty()
                if (r0 != 0) goto L5cd
                com.android.launcher3.LauncherModel r0 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherModel$Callbacks r0 = r0.getCallback()
                com.android.launcher3.LauncherModel r3 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.DeferredHandler r3 = r3.mHandler
                com.android.launcher3.LauncherModel$PackageUpdatedTask$5 r6 = new com.android.launcher3.LauncherModel$PackageUpdatedTask$5
                r6.<init>()
                r3.post(r6)
            L5cd:
                java.util.HashSet r0 = new java.util.HashSet
                r0.<init>()
                java.util.HashSet r3 = new java.util.HashSet
                r3.<init>()
                int r6 = r1.mOp
                r7 = 3
                if (r6 != r7) goto L5e0
                java.util.Collections.addAll(r0, r4)
                goto L62b
            L5e0:
                r7 = 2
                if (r6 != r7) goto L62b
                r6 = r8
            L5e4:
                if (r6 >= r5) goto L5f8
                r7 = r4[r6]
                android.os.UserHandle r9 = r1.mUser
                boolean r7 = com.android.launcher3.LauncherModel.isPackageDisabled(r2, r7, r9)
                if (r7 == 0) goto L5f5
                r7 = r4[r6]
                r0.add(r7)
            L5f5:
                int r6 = r6 + 1
                goto L5e4
            L5f8:
                int r4 = r0.size()
                if (r4 <= 0) goto L615
                android.os.UserHandle r4 = r1.mUser
                com.android.launcher3.util.ManagedProfileHeuristic r4 = com.android.launcher3.util.ManagedProfileHeuristic.get(r2, r4)
                if (r4 == 0) goto L615
                int r5 = r0.size()
                java.lang.String[] r5 = new java.lang.String[r5]
                java.lang.Object[] r5 = r0.toArray(r5)
                java.lang.String[] r5 = (java.lang.String[]) r5
                r4.processPackageRemoved(r5)
            L615:
                java.util.Iterator r4 = r22.iterator()
            L619:
                boolean r5 = r4.hasNext()
                if (r5 == 0) goto L62b
                java.lang.Object r5 = r4.next()
                com.android.launcher3.model.data.AppInfo r5 = (com.android.launcher3.model.data.AppInfo) r5
                android.content.ComponentName r5 = r5.componentName
                r3.add(r5)
                goto L619
            L62b:
                boolean r4 = r0.isEmpty()
                if (r4 == 0) goto L637
                boolean r4 = r3.isEmpty()
                if (r4 != 0) goto L68f
            L637:
                java.util.Iterator r4 = r0.iterator()
            L63b:
                boolean r5 = r4.hasNext()
                if (r5 == 0) goto L651
                java.lang.Object r5 = r4.next()
                java.lang.String r5 = (java.lang.String) r5
                r6 = 0
                com.lge.launcher3.LauncherExtension.onItemRemove(r5, r6)
                android.os.UserHandle r7 = r1.mUser
                com.android.launcher3.LauncherModel.deletePackageFromDatabase(r2, r5, r7)
                goto L63b
            L651:
                java.util.Iterator r4 = r3.iterator()
            L655:
                boolean r5 = r4.hasNext()
                if (r5 == 0) goto L678
                java.lang.Object r5 = r4.next()
                android.content.ComponentName r5 = (android.content.ComponentName) r5
                java.lang.String r6 = r5.getPackageName()
                java.lang.String r7 = r5.getClassName()
                com.lge.launcher3.LauncherExtension.onItemRemove(r6, r7)
                com.android.launcher3.LauncherModel r6 = com.android.launcher3.LauncherModel.this
                android.os.UserHandle r7 = r1.mUser
                java.util.ArrayList r5 = r6.getItemInfoForComponentName(r5, r7)
                com.android.launcher3.LauncherModel.deleteItemsFromDatabase(r2, r5)
                goto L655
            L678:
                android.os.UserHandle r4 = r1.mUser
                com.android.launcher3.InstallShortcutReceiver.removeFromInstallQueue(r2, r0, r4)
                com.android.launcher3.LauncherModel r4 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherModel$Callbacks r4 = r4.getCallback()
                com.android.launcher3.LauncherModel r5 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.DeferredHandler r5 = r5.mHandler
                com.android.launcher3.LauncherModel$PackageUpdatedTask$6 r6 = new com.android.launcher3.LauncherModel$PackageUpdatedTask$6
                r6.<init>()
                r5.post(r6)
            L68f:
                boolean r0 = r22.isEmpty()
                if (r0 != 0) goto L6a9
                com.android.launcher3.LauncherModel r0 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherModel$Callbacks r0 = r0.getCallback()
                com.android.launcher3.LauncherModel r3 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.DeferredHandler r3 = r3.mHandler
                com.android.launcher3.LauncherModel$PackageUpdatedTask$7 r4 = new com.android.launcher3.LauncherModel$PackageUpdatedTask$7
                r5 = r22
                r4.<init>()
                r3.post(r4)
            L6a9:
                com.android.launcher3.LauncherModel r0 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.LauncherModel$Callbacks r0 = r0.getCallback()
                com.android.launcher3.LauncherModel r3 = com.android.launcher3.LauncherModel.this
                int r4 = android.os.Build.VERSION.SDK_INT
                r5 = 17
                if (r4 >= r5) goto L6b9
                r6 = r11
                goto L6ba
            L6b9:
                r6 = r8
            L6ba:
                r3.loadAndBindWidgetsAndShortcuts(r2, r0, r6)
                com.android.launcher3.LauncherModel r0 = com.android.launcher3.LauncherModel.this
                com.android.launcher3.DeferredHandler r0 = r0.mHandler
                com.android.launcher3.LauncherModel$PackageUpdatedTask$8 r2 = new com.android.launcher3.LauncherModel$PackageUpdatedTask$8
                r2.<init>()
                r0.post(r2)
                return
            L6ca:
                r0 = move-exception
                monitor-exit(r13)     // Catch: java.lang.Throwable -> L6ca
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherModel.PackageUpdatedTask.run():void");
        }

        public ArrayList<AppInfo> addManagedProfileAppsToWorkFolder(Context context, ArrayList<AppInfo> added) {
            ManagedProfileHeuristic managedProfileHeuristic;
            if (added.isEmpty() || (managedProfileHeuristic = ManagedProfileHeuristic.get(context, this.mUser)) == null) {
                return added;
            }
            ArrayList<AppInfo> arrayList = new ArrayList<>(added);
            HashSet hashSet = new HashSet(Arrays.asList(this.mPackages));
            for (String str : managedProfileHeuristic.processPackageAdd(this.mPackages)) {
                if (hashSet.contains(str)) {
                    for (AppInfo appInfo : added) {
                        if (appInfo.componentName.getPackageName().equals(str) && appInfo.user.equals(this.mUser)) {
                            arrayList.remove(appInfo);
                        }
                    }
                }
            }
            return arrayList;
        }

        public ArrayList<AppInfo> addSilentOTAToFolder(Context context, ArrayList<AppInfo> added) {
            if (!LGHomeFeature.Config.FEATURE_USE_SILENT_OTA.getValue() || added.isEmpty()) {
                return added;
            }
            ArrayList<AppInfo> arrayList = new ArrayList<>(added);
            for (SilentAppInfo silentAppInfo : new SilentOTA(context, this.mUser).processPackageAdd(this.mPackages)) {
                for (AppInfo appInfo : added) {
                    if (appInfo.componentName.equals(silentAppInfo.getComponentName()) && appInfo.user.equals(this.mUser)) {
                        if (silentAppInfo.getFolderInfo() != null) {
                            arrayList.remove(appInfo);
                        } else {
                            appInfo.isSilentOTA = true;
                            appInfo.screenId = silentAppInfo.getScreen();
                            appInfo.cellX = silentAppInfo.getX();
                            appInfo.cellY = silentAppInfo.getY();
                        }
                    }
                }
            }
            return arrayList;
        }

        public ArrayList<AppInfo> addSilentOTAToFolderAllApps(Context context, ArrayList<AppInfo> added) {
            if (!LGHomeFeature.Config.FEATURE_USE_SILENT_OTA.getValue() || added.isEmpty()) {
                return added;
            }
            ArrayList<AppInfo> arrayList = new ArrayList<>(added);
            HashSet hashSet = new HashSet(Arrays.asList(this.mPackages));
            for (String str : new SilentOTA(context, this.mUser).processPackageAddAllApps(this.mPackages)) {
                if (hashSet.contains(str)) {
                    for (AppInfo appInfo : added) {
                        if (appInfo.componentName.getPackageName().equals(str) && appInfo.user.equals(this.mUser)) {
                            arrayList.remove(appInfo);
                        }
                    }
                }
            }
            return arrayList;
        }

        public ArrayList<ShortcutInfo> addSilentOTAToSwivel(ArrayList<AppInfo> added) {
            if (!LGHomeFeature.Config.FEATURE_USE_SILENT_OTA.getValue() || added.isEmpty()) {
                return null;
            }
            ArrayList<ShortcutInfo> arrayList = new ArrayList<>();
            for (String str : SilentOTASwivel.getSilentSwivelPackages()) {
                for (AppInfo appInfo : added) {
                    if (appInfo.componentName.getPackageName().equals(str) && appInfo.user.equals(this.mUser)) {
                        LGLog.i(LauncherModel.TAG, "silent ota for swivel : add candidate list : " + appInfo.componentName.getPackageName());
                        arrayList.add(new ShortcutInfo(appInfo));
                    }
                }
            }
            return arrayList;
        }

        public String getPackageUpdateOpToString(int op) {
            return op == 0 ? "OP_NONE" : op == 1 ? "OP_ADD" : op == 2 ? "OP_UPDATE" : op == 3 ? "OP_REMOVE" : op == 4 ? "OP_UNAVAILABLE" : Integer.toString(op);
        }
    }

    public static List<LauncherAppWidgetProviderInfo> getWidgetProviders(Context context, boolean refresh) throws Exception {
        ArrayList arrayList = new ArrayList();
        try {
            synchronized (sBgDataModel) {
                if (sBgWidgetProviders == null || refresh) {
                    HashMap<ComponentKey, LauncherAppWidgetProviderInfo> map = new HashMap<>();
                    AppWidgetManagerCompat appWidgetManagerCompat = AppWidgetManagerCompat.getInstance(context);
                    Iterator<AppWidgetProviderInfo> it = appWidgetManagerCompat.getAllProviders().iterator();
                    while (it.hasNext()) {
                        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfoFromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(context, it.next());
                        map.put(new ComponentKey(launcherAppWidgetProviderInfoFromProviderInfo.provider, appWidgetManagerCompat.getUser(launcherAppWidgetProviderInfoFromProviderInfo)), launcherAppWidgetProviderInfoFromProviderInfo);
                    }
                    Iterator<CustomAppWidget> it2 = Launcher.getCustomAppWidgets().values().iterator();
                    while (it2.hasNext()) {
                        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = new LauncherAppWidgetProviderInfo(context, it2.next());
                        map.put(new ComponentKey(launcherAppWidgetProviderInfo.provider, appWidgetManagerCompat.getUser(launcherAppWidgetProviderInfo)), launcherAppWidgetProviderInfo);
                    }
                    sBgWidgetProviders = map;
                }
                arrayList.addAll(sBgWidgetProviders.values());
            }
            return arrayList;
        } catch (Exception e) {
            if (e.getCause() instanceof TransactionTooLargeException) {
                synchronized (sBgDataModel) {
                    HashMap<ComponentKey, LauncherAppWidgetProviderInfo> map2 = sBgWidgetProviders;
                    if (map2 != null) {
                        arrayList.addAll(map2.values());
                    }
                    return arrayList;
                }
            }
            throw e;
        }
    }

    public static LauncherAppWidgetProviderInfo getProviderInfo(Context ctx, ComponentName name, UserHandle user) {
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo;
        synchronized (sBgDataModel) {
            if (sBgWidgetProviders == null) {
                getWidgetProviders(ctx, false);
            }
            launcherAppWidgetProviderInfo = sBgWidgetProviders.get(new ComponentKey(name, user));
        }
        return launcherAppWidgetProviderInfo;
    }

    public void loadAndBindWidgetsAndShortcuts(final Context context, final Callbacks callbacks, final boolean refresh) {
        LGLog.i(TAG, "loadAndBindWidgetsAndShortcuts - callbacks = " + callbacks + ", refresh = " + refresh);
        runAfterBindCompletes(new Runnable() { // from class: com.android.launcher3.LauncherModel.21
            @Override // java.lang.Runnable
            public void run() {
                LauncherModel.this.updateWidgetsModel(context, refresh);
                final WidgetsModel widgetsModelClone = LauncherModel.this.mBgWidgetsModel.mo209clone();
                LauncherModel.this.mHandler.post(new Runnable() { // from class: com.android.launcher3.LauncherModel.21.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Callbacks callback = LauncherModel.this.getCallback();
                        if (callbacks != callback || callback == null) {
                            return;
                        }
                        callbacks.bindAllPackages(widgetsModelClone);
                    }
                });
                LauncherAppState.getInstance(context).getWidgetCache().removeObsoletePreviews(widgetsModelClone.getRawList());
            }
        });
    }

    void updateWidgetsModel(Context context, boolean refresh) {
        LGLog.i(TAG, "updateWidgetsModel: refresh = " + refresh);
        PackageManager packageManager = context.getPackageManager();
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.addAll(getWidgetProviders(context, refresh));
        arrayList.addAll(packageManager.queryIntentActivities(new Intent("android.intent.action.CREATE_SHORTCUT"), 0));
        this.mBgWidgetsModel.setWidgetsAndShortcuts(context, arrayList);
    }

    static boolean isPackageDisabled(Context context, String packageName, UserHandle user) {
        return !LauncherAppsCompat.getInstance(context).isPackageEnabledForProfile(packageName, user);
    }

    public static boolean isValidPackageActivity(Context context, ComponentName cn, UserHandle user) {
        if (cn == null) {
            return false;
        }
        LauncherAppsCompat launcherAppsCompat = LauncherAppsCompat.getInstance(context);
        if (launcherAppsCompat.isPackageEnabledForProfile(cn.getPackageName(), user)) {
            return launcherAppsCompat.isActivityEnabledForProfile(cn, user);
        }
        return false;
    }

    public static boolean isValidPackage(Context context, String packageName, UserHandle user) {
        if (packageName == null) {
            return false;
        }
        return LauncherAppsCompat.getInstance(context).isPackageEnabledForProfile(packageName, user);
    }

    public ShortcutInfo getRestoredItemInfo(Cursor c, int titleIndex, Intent intent, int promiseType, int itemType, CursorIconInfo iconInfo, Context context) {
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.user = Process.myUserHandle();
        Bitmap bitmapLoadIcon = iconInfo.loadIcon(c, shortcutInfo, context);
        if (bitmapLoadIcon == null) {
            this.mIconCache.getTitleAndIcon(shortcutInfo, intent, shortcutInfo.user, false);
        } else {
            shortcutInfo.setIcon(bitmapLoadIcon);
        }
        if ((promiseType & 1) != 0) {
            String string = c != null ? c.getString(titleIndex) : null;
            if (!TextUtils.isEmpty(string)) {
                shortcutInfo.title = Utilities.trim(string);
            }
        } else if ((promiseType & 258) != 0) {
            if (TextUtils.isEmpty(shortcutInfo.title)) {
                shortcutInfo.title = c != null ? Utilities.trim(c.getString(titleIndex)) : "";
            }
        } else {
            throw new InvalidParameterException("Invalid restoreType " + promiseType);
        }
        shortcutInfo.contentDescription = this.mUserManager.getBadgedLabelForUser(shortcutInfo.title, shortcutInfo.user);
        shortcutInfo.itemType = itemType;
        shortcutInfo.promisedIntent = intent;
        shortcutInfo.status = promiseType;
        return shortcutInfo;
    }

    Intent getRestoredItemIntent(Cursor c, Context context, Intent intent) {
        return getMarketIntent(intent.getComponent().getPackageName());
    }

    static Intent getMarketIntent(String packageName) {
        return new Intent("android.intent.action.VIEW").setData(new Uri.Builder().scheme("market").authority("details").appendQueryParameter("id", packageName).build());
    }

    public ShortcutInfo getAppShortcutInfo(PackageManager manager, Intent intent, UserHandle user, Context context, Cursor c, int iconIndex, int titleIndex, boolean allowMissingTarget, boolean useLowResIcon) {
        if (user == null) {
            Log.d(TAG, "Null user found in getShortcutInfo");
            return null;
        }
        ComponentName component = intent.getComponent();
        if (component == null) {
            Log.d(TAG, "Missing component found in getShortcutInfo: " + component);
            return null;
        }
        Intent intent2 = new Intent(intent.getAction(), (Uri) null);
        intent2.addCategory("android.intent.category.LAUNCHER");
        intent2.setComponent(component);
        LauncherActivityInfo launcherActivityInfoResolveActivity = this.mLauncherApps.resolveActivity(intent2, user);
        if (launcherActivityInfoResolveActivity == null && UserUtils.isSecondApplication(context, user.getIdentifier())) {
            Log.d(TAG, "Dual ap is removed " + component);
            return null;
        }
        if (launcherActivityInfoResolveActivity == null && !allowMissingTarget) {
            Log.d(TAG, "Missing activity found in getShortcutInfo: " + component);
            return null;
        }
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        this.mIconCache.getTitleAndIcon(shortcutInfo, component, launcherActivityInfoResolveActivity, user, false, useLowResIcon);
        IconCache iconCache = this.mIconCache;
        if (iconCache.isDefaultIcon(shortcutInfo.getIcon(iconCache), user) && c != null) {
            Bitmap bitmapCreateIconBitmap = Utilities.createIconBitmap(c, iconIndex, context);
            if (bitmapCreateIconBitmap == null) {
                bitmapCreateIconBitmap = this.mIconCache.getDefaultIcon(user);
            }
            shortcutInfo.setIcon(bitmapCreateIconBitmap);
        }
        shortcutInfo.setIconId(c.getString(c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_ID)));
        if (shortcutInfo.getIconId() != null) {
            shortcutInfo.setUserCustomizedIcon(Utilities.createIconBitmap(c, c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.USER_CUSTOMIZED_ICON), context));
        }
        if (launcherActivityInfoResolveActivity != null && PackageManagerHelper.isAppSuspended(launcherActivityInfoResolveActivity.getApplicationInfo())) {
            shortcutInfo.runtimeStatusFlags |= 4;
        }
        if (TextUtils.isEmpty(shortcutInfo.title) && c != null) {
            shortcutInfo.title = Utilities.trim(c.getString(titleIndex));
        }
        if (shortcutInfo.title == null) {
            shortcutInfo.title = component.getClassName();
        }
        shortcutInfo.itemType = 0;
        shortcutInfo.user = user;
        shortcutInfo.contentDescription = this.mUserManager.getBadgedLabelForUser(shortcutInfo.title, shortcutInfo.user);
        if (launcherActivityInfoResolveActivity != null) {
            shortcutInfo.flags = AppInfo.initFlags(launcherActivityInfoResolveActivity);
        } else if (allowMissingTarget) {
            shortcutInfo.flags = AppInfo.getFlagsForUninstalledPackage(context.getPackageManager(), component);
            LGLog.i(TAG, "update flag of missing target Info(" + component + ") -> " + shortcutInfo.flags);
        }
        return shortcutInfo;
    }

    static ArrayList<ItemInfo> filterItemInfos(Iterable<ItemInfo> infos, ItemInfoFilter f) {
        LauncherAppWidgetInfo launcherAppWidgetInfo;
        ComponentName componentName;
        HashSet hashSet = new HashSet();
        for (ItemInfo itemInfo : infos) {
            if (itemInfo instanceof ShortcutInfo) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                ComponentName targetComponent = shortcutInfo.getTargetComponent();
                if (targetComponent != null && f.filterItem(null, shortcutInfo, targetComponent)) {
                    hashSet.add(shortcutInfo);
                }
            } else if (itemInfo instanceof FolderInfo) {
                FolderInfo folderInfo = (FolderInfo) itemInfo;
                try {
                    for (ShortcutInfo shortcutInfo2 : folderInfo.contents) {
                        ComponentName targetComponent2 = shortcutInfo2.getTargetComponent();
                        if (targetComponent2 != null && f.filterItem(folderInfo, shortcutInfo2, targetComponent2)) {
                            hashSet.add(shortcutInfo2);
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    Log.w(TAG, "Failed to filter folder items: " + e.getMessage());
                }
            } else if ((itemInfo instanceof LauncherAppWidgetInfo) && (componentName = (launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo).providerName) != null && f.filterItem(null, launcherAppWidgetInfo, componentName)) {
                hashSet.add(launcherAppWidgetInfo);
            }
        }
        return new ArrayList<>(hashSet);
    }

    ArrayList<ItemInfo> getItemInfoForComponentName(final ComponentName cname, final UserHandle user) {
        return filterItemInfos(sBgDataModel.itemsIdMap, new ItemInfoFilter() { // from class: com.android.launcher3.LauncherModel.22
            @Override // com.android.launcher3.LauncherModel.ItemInfoFilter
            public boolean filterItem(ItemInfo parent, ItemInfo info, ComponentName cn) {
                if (info.user == null) {
                    return cn.equals(cname);
                }
                return cn.equals(cname) && info.user.equals(user);
            }
        });
    }

    ArrayList<ItemInfo> getApplicationItemInfoForComponentName(final ComponentName cname, final UserHandle user) {
        return filterItemInfos(sBgDataModel.itemsIdMap, new ItemInfoFilter() { // from class: com.android.launcher3.LauncherModel.23
            @Override // com.android.launcher3.LauncherModel.ItemInfoFilter
            public boolean filterItem(ItemInfo parent, ItemInfo info, ComponentName cn) {
                return info.user == null ? cn.equals(cname) && info.itemType == 0 : cn.equals(cname) && info.itemType == 0 && info.user.equals(user);
            }
        });
    }

    ShortcutInfo getShortcutInfo(Cursor c, Context context, int titleIndex, CursorIconInfo iconInfo) {
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.user = Process.myUserHandle();
        shortcutInfo.itemType = 1;
        shortcutInfo.title = Utilities.trim(c.getString(titleIndex));
        Bitmap bitmapLoadIcon = iconInfo.loadIcon(c, shortcutInfo, context);
        if (bitmapLoadIcon == null) {
            bitmapLoadIcon = this.mIconCache.getDefaultIcon(shortcutInfo.user);
            shortcutInfo.usingFallbackIcon = true;
        }
        shortcutInfo.setIcon(bitmapLoadIcon);
        return shortcutInfo;
    }

    public void loadInfoFromCursor(ShortcutInfo info, Cursor c, CursorIconInfo iconInfo, Context context) {
        info.title = iconInfo.getTitle(c);
        Bitmap bitmapLoadIcon = iconInfo.loadIcon(c, info, context);
        if (bitmapLoadIcon == null) {
            bitmapLoadIcon = this.mIconCache.getDefaultIcon(info.user);
            info.usingFallbackIcon = true;
        }
        info.setIcon(bitmapLoadIcon);
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x005a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    com.android.launcher3.ShortcutInfo infoFromShortcutIntent(android.content.Context r10, android.content.Intent r11) {
        /*
            r9 = this;
            java.lang.String r0 = "android.intent.extra.shortcut.INTENT"
            android.os.Parcelable r0 = r11.getParcelableExtra(r0)
            android.content.Intent r0 = (android.content.Intent) r0
            java.lang.String r1 = "android.intent.extra.shortcut.NAME"
            java.lang.String r1 = r11.getStringExtra(r1)
            java.lang.String r2 = "android.intent.extra.shortcut.ICON"
            android.os.Parcelable r2 = r11.getParcelableExtra(r2)
            java.lang.String r3 = "no_icon_frames"
            r4 = 0
            boolean r3 = r11.getBooleanExtra(r3, r4)
            r5 = 0
            if (r0 != 0) goto L26
            java.lang.String r10 = "Launcher.Model"
            java.lang.String r11 = "Can't construct ShorcutInfo with null intent"
            android.util.Log.e(r10, r11)
            return r5
        L26:
            boolean r6 = r2 instanceof android.graphics.Bitmap
            r7 = 1
            if (r6 == 0) goto L36
            android.graphics.Bitmap r2 = (android.graphics.Bitmap) r2
            android.graphics.Bitmap r10 = com.android.launcher3.Utilities.createIconBitmap(r2, r10)
            r4 = r7
        L32:
            r8 = r5
            r5 = r10
            r10 = r8
            goto L4d
        L36:
            java.lang.String r2 = "android.intent.extra.shortcut.ICON_RESOURCE"
            android.os.Parcelable r11 = r11.getParcelableExtra(r2)
            boolean r2 = r11 instanceof android.content.Intent.ShortcutIconResource
            if (r2 == 0) goto L4c
            r5 = r11
            android.content.Intent$ShortcutIconResource r5 = (android.content.Intent.ShortcutIconResource) r5
            java.lang.String r11 = r5.packageName
            java.lang.String r2 = r5.resourceName
            android.graphics.Bitmap r10 = com.android.launcher3.Utilities.createIconBitmap(r11, r2, r10)
            goto L32
        L4c:
            r10 = r5
        L4d:
            com.android.launcher3.ShortcutInfo r11 = new com.android.launcher3.ShortcutInfo
            r11.<init>()
            android.os.UserHandle r2 = android.os.Process.myUserHandle()
            r11.user = r2
            if (r5 != 0) goto L64
            com.android.launcher3.icons.IconCache r2 = r9.mIconCache
            android.os.UserHandle r5 = r11.user
            android.graphics.Bitmap r5 = r2.getDefaultIcon(r5)
            r11.usingFallbackIcon = r7
        L64:
            r11.setIcon(r5)
            java.lang.String r1 = com.android.launcher3.Utilities.trim(r1)
            r11.title = r1
            com.android.launcher3.compat.UserManagerCompat r1 = r9.mUserManager
            java.lang.CharSequence r2 = r11.title
            android.os.UserHandle r5 = r11.user
            java.lang.CharSequence r1 = r1.getBadgedLabelForUser(r2, r5)
            r11.contentDescription = r1
            r11.intent = r0
            r11.customIcon = r4
            r11.iconResource = r10
            int r10 = r11.options
            r10 = r10 | r3
            r11.options = r10
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherModel.infoFromShortcutIntent(android.content.Context, android.content.Intent):com.android.launcher3.ShortcutInfo");
    }

    static boolean isValidProvider(AppWidgetProviderInfo provider) {
        return (provider == null || provider.provider == null || provider.provider.getPackageName() == null) ? false : true;
    }

    public void dumpState() {
        Log.d(TAG, "mCallbacks=" + this.mCallbacks);
        AppInfo.dumpApplicationInfoList(TAG, "mAllAppsList.data", this.mBgAllAppsList.data);
        AppInfo.dumpApplicationInfoList(TAG, "mAllAppsList.added", this.mBgAllAppsList.added);
        AppInfo.dumpApplicationInfoList(TAG, "mAllAppsList.removed", this.mBgAllAppsList.removed);
        AppInfo.dumpApplicationInfoList(TAG, "mAllAppsList.modified", this.mBgAllAppsList.modified);
        LoaderTask loaderTask = this.mLoaderTask;
        if (loaderTask != null) {
            loaderTask.dumpState();
        } else {
            Log.d(TAG, "mLoaderTask=null");
        }
    }

    public void dumpState(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        if (args.length > 0 && TextUtils.equals(args[0], "--all")) {
            writer.println(prefix + "All apps list: size=" + this.mBgAllAppsList.data.size());
            for (AppInfo appInfo : this.mBgAllAppsList.data) {
                CharSequence charSequence = appInfo.title;
                writer.println(prefix + "   title=\"" + ((Object) charSequence) + "\" iconBitmap=" + appInfo.iconBitmap + " componentName=" + appInfo.componentName.getPackageName());
            }
        }
        sBgDataModel.dump(prefix, fd, writer, args);
    }

    public Callbacks getCallback() {
        WeakReference<Callbacks> weakReference = this.mCallbacks;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public FolderInfo findFolderById(Long folderId) {
        FolderInfo folderInfo;
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            folderInfo = bgDataModel.folders.get(folderId.longValue());
        }
        return folderInfo;
    }

    public static Looper getWorkerLooper() {
        return sWorkerThread.getLooper();
    }

    public List<ItemInfo> getItems() {
        ArrayList arrayList = new ArrayList();
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            Iterator<ItemInfo> it = bgDataModel.itemsIdMap.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
        }
        return arrayList;
    }

    public void moveFolderItemsToWorkspace(final Context context, final ArrayList<ShortcutInfo> workspaceApps) {
        Callbacks callback = getCallback();
        if (workspaceApps.isEmpty()) {
            return;
        }
        runOnMainThread(new AnonymousClass24(workspaceApps, context, callback));
    }

    /* JADX INFO: renamed from: com.android.launcher3.LauncherModel$24, reason: invalid class name */
    class AnonymousClass24 implements Runnable {
        final /* synthetic */ Callbacks val$callbacks;
        final /* synthetic */ Context val$context;
        final /* synthetic */ ArrayList val$workspaceApps;

        AnonymousClass24(final ArrayList val$workspaceApps, final Context val$context, final Callbacks val$callbacks) {
            this.val$workspaceApps = val$workspaceApps;
            this.val$context = val$context;
            this.val$callbacks = val$callbacks;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (LauncherModel.sBgDataModel) {
                ItemInfo itemInfo = LauncherModel.sBgDataModel.itemsIdMap.get(((ShortcutInfo) this.val$workspaceApps.get(0)).container);
                if (itemInfo instanceof FolderInfo) {
                    ((FolderInfo) itemInfo).remove(this.val$workspaceApps);
                } else {
                    Log.w(LauncherModel.TAG, "Failed to move folder items: Invalid parent: " + itemInfo);
                }
            }
            LauncherModel.runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.24.1
                @Override // java.lang.Runnable
                public void run() {
                    final ArrayList arrayList = new ArrayList();
                    final ArrayList<Long> arrayList2 = new ArrayList<>();
                    ArrayList<Long> arrayListLoadWorkspaceScreensDb = LauncherModel.loadWorkspaceScreensDb(AnonymousClass24.this.val$context);
                    synchronized (LauncherModel.sBgDataModel) {
                        for (ShortcutInfo shortcutInfo : AnonymousClass24.this.val$workspaceApps) {
                            Pair<Long, int[]> pairFindSpaceForItem = LauncherModel.this.findSpaceForItem(AnonymousClass24.this.val$context, arrayListLoadWorkspaceScreensDb, arrayList2, 1, 1);
                            long jLongValue = ((Long) pairFindSpaceForItem.first).longValue();
                            int[] iArr = (int[]) pairFindSpaceForItem.second;
                            LauncherModel.moveItemInDatabase(AnonymousClass24.this.val$context, shortcutInfo, -100L, jLongValue, iArr[0], iArr[1]);
                            arrayList.add(shortcutInfo);
                        }
                    }
                    LauncherModel.updateWorkspaceScreenOrder(AnonymousClass24.this.val$context, arrayListLoadWorkspaceScreensDb);
                    if (arrayList.isEmpty()) {
                        return;
                    }
                    LauncherModel.this.runOnMainThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.24.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Callbacks callback = LauncherModel.this.getCallback();
                            if (AnonymousClass24.this.val$callbacks != callback || callback == null) {
                                return;
                            }
                            AnonymousClass24.this.val$callbacks.bindAppsAdded(arrayList2, arrayList, null, null, 0);
                        }
                    });
                }
            });
        }
    }

    public void moveItemsToFolder(final Context context, final ArrayList<ShortcutInfo> items, final FolderInfo target) {
        Callbacks callback = getCallback();
        if (items == null || items.isEmpty() || target == null) {
            return;
        }
        runOnMainThread(new AnonymousClass25(target, items, context, callback));
    }

    /* JADX INFO: renamed from: com.android.launcher3.LauncherModel$25, reason: invalid class name */
    class AnonymousClass25 implements Runnable {
        final /* synthetic */ Callbacks val$callbacks;
        final /* synthetic */ Context val$context;
        final /* synthetic */ ArrayList val$items;
        final /* synthetic */ FolderInfo val$target;

        AnonymousClass25(final FolderInfo val$target, final ArrayList val$items, final Context val$context, final Callbacks val$callbacks) {
            this.val$target = val$target;
            this.val$items = val$items;
            this.val$context = val$context;
            this.val$callbacks = val$callbacks;
        }

        @Override // java.lang.Runnable
        public void run() {
            final int size = this.val$target.contents.size();
            this.val$target.add(this.val$items);
            LauncherModel.runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.25.1
                @Override // java.lang.Runnable
                public void run() {
                    int i = size;
                    final ArrayList arrayList = new ArrayList();
                    for (ShortcutInfo shortcutInfo : AnonymousClass25.this.val$items) {
                        shortcutInfo.rank = i;
                        LauncherModel.moveItemInDatabase(AnonymousClass25.this.val$context, shortcutInfo, AnonymousClass25.this.val$target.id, AnonymousClass25.this.val$target.screenId, 0, 0);
                        arrayList.add(shortcutInfo);
                        i++;
                    }
                    if (arrayList.isEmpty()) {
                        return;
                    }
                    LauncherModel.this.runOnMainThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.25.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Callbacks callback = LauncherModel.this.getCallback();
                            if (AnonymousClass25.this.val$callbacks != callback || callback == null) {
                                return;
                            }
                            AnonymousClass25.this.val$callbacks.bindAppsMoved(arrayList, AnonymousClass25.this.val$target);
                        }
                    });
                }
            });
        }
    }

    public ArrayList<AppInfo> getAllAppsList() {
        return this.mBgAllAppsList.data;
    }

    public static int updateItemInSharingContentTable(Context context, String columnName, String value) {
        int iUpdate;
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, value);
        try {
            iUpdate = contentResolver.update(LauncherSettings.SharingContents.CONTENT_URI, contentValues, "_id=\"0\"", null);
        } catch (SQLiteException e) {
            LGLog.i(TAG, String.format("updateItemInSharingContentTable() : SQLiteException(%s)", e.toString()));
            iUpdate = -1;
        }
        if (iUpdate != -1) {
            LGLog.i(TAG, String.format("updateItemInSharingContentTable() : Notify %s is changed(%s)", columnName, value));
            contentResolver.notifyChange(Uri.withAppendedPath(LauncherSettings.SharingContents.CONTENT_URI, columnName), null);
        } else {
            LGLog.i(TAG, String.format("updateItemInSharingContentTable() : Fail to notify %s is changed : %s", columnName, value));
        }
        return iUpdate;
    }

    public static String getItemInSharingContentTable(Context context, String columnName) {
        Cursor cursorQuery;
        String string = null;
        try {
            cursorQuery = context.getContentResolver().query(LauncherSettings.SharingContents.CONTENT_URI, new String[]{"_id", columnName}, "_id = 0", null, null, null);
        } catch (SQLiteException e) {
            LGLog.i(TAG, String.format("getItemInSharingContentTable() : SQLiteException(%s)", e.toString()));
            cursorQuery = null;
        }
        if (cursorQuery == null) {
            LGLog.i(TAG, "getItemInSharingContentTable() : The cursor is null");
            return null;
        }
        int columnIndex = cursorQuery.getColumnIndex(columnName);
        while (cursorQuery.moveToNext()) {
            string = cursorQuery.getString(columnIndex);
        }
        cursorQuery.close();
        return string;
    }

    public static void updateItemsMap(final ItemInfo item) {
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            ItemInfo itemInfo = bgDataModel.itemsIdMap.get(item.id);
            if (((ShortcutInfo) item).getIconId() != null) {
                bgDataModel.workspaceItems.remove(itemInfo);
                bgDataModel.workspaceItems.add(item);
                bgDataModel.itemsIdMap.remove(item.id);
                bgDataModel.itemsIdMap.put(item.id, item);
            }
        }
    }

    public static void resizeItemInDatabase(Context context, final ItemInfo item, final int cellX, final int cellY, final int spanX, final int spanY) {
        item.spanX = spanX;
        item.spanY = spanY;
        item.cellX = cellX;
        item.cellY = cellY;
        updateItemsMap(item);
        modifyItemInDatabase(context, item, item.container, item.screenId, item.cellX, item.cellY, item.spanX, item.spanY);
    }

    private void checkQMemoplusPanelPackabe(String packageName) {
        Callbacks callback;
        if (QMemoPanelConst.QMEMOPANEL_PACKAGE_NAME.equals(packageName) && LGHomeFeature.Config.FEATURE_USE_QMEMOPLUS_PANEL.getValue() && (callback = getCallback()) != null) {
            callback.invalidateHasCustomContentToLeft();
        }
    }

    public static void updateMaxScreenId(Context context, final ArrayList<Long> screens) {
        int size = screens.size();
        long jLongValue = 0;
        for (int i = 0; i < size; i++) {
            if (screens.get(i).longValue() > jLongValue) {
                jLongValue = screens.get(i).longValue();
            }
        }
        LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_UPDATE_MAX_SCREEN_ID, Long.toString(jLongValue));
    }

    public static ItemInfo getLastItemInWorkspace() {
        ItemInfo itemInfo = null;
        for (ItemInfo itemInfo2 : sBgDataModel.itemsIdMap) {
            if (itemInfo2.container == -100 && (itemInfo == null || itemInfo.screenId < itemInfo2.screenId || (itemInfo.screenId == itemInfo2.screenId && (itemInfo.cellY < itemInfo2.cellY || (itemInfo.cellY == itemInfo2.cellY && itemInfo.cellX < itemInfo2.cellX))))) {
                itemInfo = itemInfo2;
            }
        }
        return itemInfo;
    }

    public static FolderInfo getLGFolderInWorkspace(String lgfolderName) {
        if (lgfolderName == null) {
            return null;
        }
        for (FolderInfo folderInfo : sBgDataModel.folders) {
            if (folderInfo.title.equals(lgfolderName)) {
                return folderInfo;
            }
        }
        return null;
    }

    public static FolderInfo getParentFolder(ShortcutInfo childItem) {
        int i = 0;
        while (true) {
            BgDataModel bgDataModel = sBgDataModel;
            if (i >= bgDataModel.folders.size()) {
                return null;
            }
            FolderInfo folderInfo = bgDataModel.folders.get(bgDataModel.folders.keyAt(i));
            if (folderInfo.id == childItem.container) {
                return folderInfo;
            }
            i++;
        }
    }

    public static ArrayList<Long> removeDuplicateList(ArrayList<Long> list) {
        return new ArrayList<>(new HashSet(list));
    }

    Pair<Long, int[]> findLastSpaceForItem(Context context, ArrayList<Long> workspaceScreens, ArrayList<Long> addedWorkspaceScreensFinal, int spanX, int spanY) {
        boolean zFindLastAvailableIconSpaceInScreen;
        LongSparseArray longSparseArray = new LongSparseArray();
        assertWorkspaceLoaded();
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            for (ItemInfo itemInfo : bgDataModel.itemsIdMap) {
                if (itemInfo.container == -100) {
                    ArrayList arrayList = (ArrayList) longSparseArray.get(itemInfo.screenId);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        longSparseArray.put(itemInfo.screenId, arrayList);
                    }
                    arrayList.add(itemInfo);
                }
            }
        }
        long jLongValue = 0;
        int[] iArr = new int[2];
        int size = workspaceScreens.size();
        int i = workspaceScreens.isEmpty() ? 0 : size - 1;
        if (i < size) {
            jLongValue = workspaceScreens.get(i).longValue();
            zFindLastAvailableIconSpaceInScreen = findLastAvailableIconSpaceInScreen(context, (ArrayList) longSparseArray.get(jLongValue), iArr, spanX, spanY);
        } else {
            zFindLastAvailableIconSpaceInScreen = false;
        }
        if (!zFindLastAvailableIconSpaceInScreen) {
            while (true) {
                jLongValue = LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_SCREEN_ID).getLong("value");
                workspaceScreens.add(Long.valueOf(jLongValue));
                addedWorkspaceScreensFinal.add(Long.valueOf(jLongValue));
                LGLog.i(TAG, "ScreenCheck: Can't find space last. generate new screenId(" + jLongValue + "). add workspaceScreens " + workspaceScreens + ", addedWorkspaceScreensFinal " + addedWorkspaceScreensFinal);
                if (findNextAvailableIconSpaceInScreen(context, (ArrayList<ItemInfo>) longSparseArray.get(jLongValue), iArr, spanX, spanY)) {
                    break;
                }
                LGLog.w(TAG, "Can't find space to add the item! screenId = " + jLongValue, new int[0]);
            }
        }
        return Pair.create(Long.valueOf(jLongValue), iArr);
    }

    private static boolean findLastAvailableIconSpaceInScreen(Context context, ArrayList<ItemInfo> occupiedPos, int[] xy, int spanX, int spanY) {
        InvariantDeviceProfile invariantDeviceProfile = LauncherAppState.getInstance(context).getInvariantDeviceProfile();
        int i = invariantDeviceProfile.numColumns;
        int i2 = invariantDeviceProfile.numRows;
        boolean[][] zArr = (boolean[][]) Array.newInstance((Class<?>) boolean.class, i, i2);
        if (occupiedPos != null) {
            for (ItemInfo itemInfo : occupiedPos) {
                int i3 = itemInfo.cellX + itemInfo.spanX;
                int i4 = itemInfo.cellY + itemInfo.spanY;
                for (int i5 = itemInfo.cellX; i5 >= 0 && i5 < i3 && i5 < i; i5++) {
                    for (int i6 = itemInfo.cellY; i6 >= 0 && i6 < i4 && i6 < i2; i6++) {
                        zArr[i5][i6] = true;
                    }
                }
            }
        }
        return findLastVacantCell(xy, spanX, spanY, i, i2, zArr);
    }

    public static boolean findLastVacantCell(int[] vacant, int spanX, int spanY, int xCount, int yCount, boolean[][] occupied) {
        boolean z = false;
        for (int i = yCount - spanY; i >= 0; i--) {
            int i2 = xCount - spanX;
            while (i2 >= 0) {
                boolean z2 = !occupied[i2][i];
                for (int i3 = i2; i3 < i2 + spanX; i3++) {
                    for (int i4 = i; i4 < i + spanY; i4++) {
                        z2 = z2 && !occupied[i3][i4];
                        if (!z2) {
                            break;
                        }
                    }
                }
                if (!z2) {
                    return z;
                }
                vacant[0] = i2;
                vacant[1] = i;
                i2--;
                z = true;
            }
        }
        return z;
    }

    public static boolean findNextAvailableIconSpaceInScreen(Context context, long screenId, int[] xy, int spanX, int spanY) {
        ArrayList arrayList = new ArrayList();
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            for (ItemInfo itemInfo : bgDataModel.itemsIdMap) {
                if (itemInfo.container == -100 && itemInfo.screenId == screenId) {
                    arrayList.add(itemInfo);
                }
            }
        }
        return findNextAvailableIconSpaceInScreen(context, (ArrayList<ItemInfo>) arrayList, xy, spanX, spanY);
    }

    private static boolean isExistSprintInstaller(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sprint.chameleon.mobile_id.enable", 0);
        boolean zContains = sharedPreferences.contains("sprint.chameleon.mobile_id.enable");
        boolean zIsExistingPackage = isExistingPackage(context, "com.sprint.w.installer");
        if (!zContains) {
            sharedPreferences.edit().putBoolean("sprint.chameleon.mobile_id.enable", zIsExistingPackage).commit();
            LGLog.i(TAG, "updateChameleonPreference: sprint.chameleon.mobile_id.enable changed as " + zIsExistingPackage);
            return zIsExistingPackage;
        }
        if (zIsExistingPackage && !sharedPreferences.getBoolean("sprint.chameleon.mobile_id.enable", true)) {
            sharedPreferences.edit().putBoolean("sprint.chameleon.mobile_id.enable", zIsExistingPackage).commit();
            LGLog.i(TAG, "updateChameleonPreference: sprint.chameleon.mobile_id.enable changed as " + zIsExistingPackage);
            return true;
        }
        LGLog.i(TAG, "updateChameleonPreference: sprint.chameleon.mobile_id.enable = " + zIsExistingPackage + ", not changed.");
        return false;
    }

    private static boolean isExistingPackage(Context context, String packageName) {
        boolean z = false;
        try {
            if (context.getPackageManager().getPackageInfo(packageName, 0) == null) {
                return false;
            }
            z = true;
            LGLog.i(TAG, "Found app " + packageName);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            LGLog.i(TAG, "Couldn't find app " + packageName);
            return z;
        }
    }

    public void removeWorkspaceEmptyScreenModel() {
        Callbacks callback = getCallback();
        if (callback != null) {
            callback.removeWorkspaceEmptyScreen();
        }
    }

    public void onLiveIconUpdated(String updatedPackage, final UserHandle user) {
        ShortcutInfo shortcutInfo;
        ComponentName targetComponent;
        final Callbacks callback = getCallback();
        final ArrayList<AppInfo> arrayList = new ArrayList<>();
        final ArrayList arrayList2 = new ArrayList();
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            for (ItemInfo itemInfo : bgDataModel.itemsIdMap) {
                if ((itemInfo instanceof ShortcutInfo) && user.equals(itemInfo.user) && itemInfo.itemType == 0 && (targetComponent = (shortcutInfo = (ShortcutInfo) itemInfo).getTargetComponent()) != null && updatedPackage.equals(targetComponent.getPackageName())) {
                    shortcutInfo.updateIcon(this.mIconCache);
                    arrayList2.add(shortcutInfo);
                }
            }
            this.mBgAllAppsList.updateIconAndLabel(updatedPackage, user, arrayList);
        }
        if (!arrayList2.isEmpty()) {
            this.mHandler.post(new Runnable() { // from class: com.android.launcher3.LauncherModel.26
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callback2 = LauncherModel.this.getCallback();
                    if (callback2 == null || callback != callback2) {
                        return;
                    }
                    callback2.bindShortcutsChanged(arrayList2, new ArrayList<>(), user);
                }
            });
        }
        if (arrayList.isEmpty()) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.android.launcher3.LauncherModel.27
            @Override // java.lang.Runnable
            public void run() {
                Callbacks callback2 = LauncherModel.this.getCallback();
                if (callback2 == null || callback != callback2) {
                    return;
                }
                callback2.bindAppsUpdated(arrayList);
            }
        });
    }

    public boolean isOccufiedSpace(Context context, ArrayList<Long> workspaceScreens, int spanX, int spanY, int preferredScreenIndex, int cellX, int cellY) {
        LongSparseArray longSparseArray = new LongSparseArray();
        assertWorkspaceLoaded();
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            for (ItemInfo itemInfo : bgDataModel.itemsIdMap) {
                if (itemInfo.container == -100) {
                    ArrayList arrayList = (ArrayList) longSparseArray.get(itemInfo.screenId);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        longSparseArray.put(itemInfo.screenId, arrayList);
                    }
                    arrayList.add(itemInfo);
                }
            }
        }
        if (preferredScreenIndex < workspaceScreens.size()) {
            long jLongValue = workspaceScreens.get(preferredScreenIndex).longValue();
            InvariantDeviceProfile invariantDeviceProfile = LauncherAppState.getInstance(context).getInvariantDeviceProfile();
            int i = invariantDeviceProfile.numColumns;
            int i2 = invariantDeviceProfile.numRows;
            boolean[][] zArr = (boolean[][]) Array.newInstance((Class<?>) boolean.class, i, i2);
            if (longSparseArray.get(jLongValue) != null) {
                for (ItemInfo itemInfo2 : (ArrayList) longSparseArray.get(jLongValue)) {
                    int i3 = itemInfo2.cellX + itemInfo2.spanX;
                    int i4 = itemInfo2.cellY + itemInfo2.spanY;
                    for (int i5 = itemInfo2.cellX; i5 >= 0 && i5 < i3 && i5 < i; i5++) {
                        for (int i6 = itemInfo2.cellY; i6 >= 0 && i6 < i4 && i6 < i2; i6++) {
                            zArr[i5][i6] = true;
                        }
                    }
                }
            }
            if (!zArr[cellX][cellY]) {
                return true;
            }
        }
        return false;
    }

    Pair<Long, int[]> findSpaceForItem(Context context, ArrayList<Long> workspaceScreens, ArrayList<Long> addedWorkspaceScreensFinal, int spanX, int spanY, int preferredScreenIndex) {
        LongSparseArray longSparseArray = new LongSparseArray();
        assertWorkspaceLoaded();
        BgDataModel bgDataModel = sBgDataModel;
        synchronized (bgDataModel) {
            for (ItemInfo itemInfo : bgDataModel.itemsIdMap) {
                if (itemInfo.container == -100) {
                    ArrayList arrayList = (ArrayList) longSparseArray.get(itemInfo.screenId);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        longSparseArray.put(itemInfo.screenId, arrayList);
                    }
                    arrayList.add(itemInfo);
                }
            }
        }
        long jLongValue = 0;
        int[] iArr = new int[2];
        boolean zFindNextAvailableIconSpaceInScreen = false;
        int size = workspaceScreens.size();
        if (preferredScreenIndex < size) {
            jLongValue = workspaceScreens.get(preferredScreenIndex).longValue();
            zFindNextAvailableIconSpaceInScreen = findNextAvailableIconSpaceInScreen(context, (ArrayList<ItemInfo>) longSparseArray.get(jLongValue), iArr, spanX, spanY);
        }
        if (!zFindNextAvailableIconSpaceInScreen) {
            while (true) {
                if (preferredScreenIndex >= size) {
                    break;
                }
                jLongValue = workspaceScreens.get(preferredScreenIndex).longValue();
                if (findNextAvailableIconSpaceInScreen(context, (ArrayList<ItemInfo>) longSparseArray.get(jLongValue), iArr, spanX, spanY)) {
                    zFindNextAvailableIconSpaceInScreen = true;
                    break;
                }
                preferredScreenIndex++;
            }
        }
        if (!zFindNextAvailableIconSpaceInScreen) {
            jLongValue = LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_SCREEN_ID).getLong("value");
            workspaceScreens.add(Long.valueOf(jLongValue));
            addedWorkspaceScreensFinal.add(Long.valueOf(jLongValue));
            LGLog.i(TAG, "ScreenCheck: Can't find space. generate new screenId(" + jLongValue + "). add workspaceScreens " + workspaceScreens + ", addedWorkspaceScreensFinal " + addedWorkspaceScreensFinal);
            if (!findNextAvailableIconSpaceInScreen(context, (ArrayList<ItemInfo>) longSparseArray.get(jLongValue), iArr, spanX, spanY)) {
                throw new RuntimeException("Can't find space to add the item");
            }
        }
        return Pair.create(Long.valueOf(jLongValue), iArr);
    }

    public static int getDefaultPageFromDatabase(Context context) {
        Cursor cursorQuery = context.getContentResolver().query(LauncherSettings.WorkspaceDetail.CONTENT_URI, new String[]{LauncherSettings.WorkspaceDetail.DEFAULT_SCREEN}, null, null, null, null);
        try {
            try {
                if (cursorQuery.moveToFirst()) {
                    return cursorQuery.getInt(cursorQuery.getColumnIndexOrThrow(LauncherSettings.WorkspaceDetail.DEFAULT_SCREEN));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LGLog.e(TAG, "Failed to read default page index from Database!");
            return 0;
        } finally {
            cursorQuery.close();
        }
    }

    public static void updateDefaultScreen(Context context, final int screenIndex) {
        final ContentResolver contentResolver = context.getContentResolver();
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.29
            @Override // java.lang.Runnable
            public void run() {
                Cursor cursorQuery = contentResolver.query(LauncherSettings.WorkspaceDetail.CONTENT_URI, new String[]{LauncherSettings.WorkspaceDetail.DEFAULT_SCREEN}, null, null, null, null);
                try {
                    if (cursorQuery.moveToFirst()) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(LauncherSettings.WorkspaceDetail.DEFAULT_SCREEN, Integer.valueOf(screenIndex));
                        contentResolver.update(LauncherSettings.WorkspaceDetail.CONTENT_URI, contentValues, null, null);
                    }
                } finally {
                    cursorQuery.close();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustDefaultScreen(Context context, ArrayList<Long> unusedScreens) {
        int defaultPageFromDatabase = getDefaultPageFromDatabase(context);
        Iterator<Long> it = unusedScreens.iterator();
        int i = 0;
        while (it.hasNext()) {
            int iIndexOf = sBgDataModel.workspaceScreens.indexOf(Long.valueOf(it.next().longValue()));
            if (iIndexOf >= 0 && iIndexOf <= defaultPageFromDatabase) {
                i++;
            }
        }
        if (i > 0) {
            updateDefaultScreen(context, Math.max(defaultPageFromDatabase - i, 0));
        }
    }

    public static void validateDefaultScreen(final Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.30
            @Override // java.lang.Runnable
            public void run() {
                Cursor cursorQuery = contentResolver.query(LauncherSettings.WorkspaceDetail.CONTENT_URI, new String[]{LauncherSettings.WorkspaceDetail.DEFAULT_SCREEN}, null, null, null, null);
                try {
                    if (cursorQuery.moveToFirst()) {
                        int i = cursorQuery.getInt(cursorQuery.getColumnIndexOrThrow(LauncherSettings.WorkspaceDetail.DEFAULT_SCREEN));
                        int iCountEmptyScreenTo = LauncherModel.countEmptyScreenTo(context, i, contentResolver);
                        if (iCountEmptyScreenTo > 0) {
                            int iMax = Math.max(i - iCountEmptyScreenTo, 0);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(LauncherSettings.WorkspaceDetail.DEFAULT_SCREEN, Integer.valueOf(iMax));
                            contentResolver.update(LauncherSettings.WorkspaceDetail.CONTENT_URI, contentValues, null, null);
                            LGLog.d(LauncherModel.TAG, "Adjust default screen: " + iMax);
                        }
                    }
                } catch (Exception unused) {
                    LGLog.e(LauncherModel.TAG, "Failed to update default screen!");
                } finally {
                    cursorQuery.close();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int countEmptyScreenTo(Context context, int toIndex, ContentResolver cr) {
        String[] strArr = {LauncherSettings.Favorites.CONTAINER, "screen"};
        ArrayList<Long> arrayListLoadWorkspaceScreensDb = loadWorkspaceScreensDb(context);
        Cursor cursorQuery = null;
        int i = 0;
        for (int i2 = 0; i2 <= toIndex; i2++) {
            cursorQuery = cr.query(LauncherSettings.Favorites.CONTENT_URI, strArr, "container=-100 AND screen=?", new String[]{arrayListLoadWorkspaceScreensDb.get(i2).toString()}, null, null);
            if (cursorQuery.getCount() == 0) {
                i++;
            }
        }
        if (cursorQuery != null) {
            cursorQuery.close();
        }
        LGLog.d(TAG, "Empty screens count: " + i);
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreInvalidFolder(Context context, InvariantDeviceProfile profile) {
        BgDataModel bgDataModel = sBgDataModel;
        if (BgDataModel.invalidFoldersToRestore.size() <= 0) {
            return;
        }
        int i = profile.numColumns * profile.numRows;
        int iCeil = (int) Math.ceil(((double) BgDataModel.invalidFoldersToRestore.size()) / ((double) i));
        long j = -1;
        Iterator<Long> it = bgDataModel.workspaceScreens.iterator();
        while (it.hasNext()) {
            long jLongValue = it.next().longValue();
            if (jLongValue > j) {
                j = jLongValue;
            }
        }
        for (int i2 = 1; i2 <= iCeil; i2++) {
            sBgDataModel.workspaceScreens.add(Long.valueOf(((long) i2) + j));
        }
        updateWorkspaceScreenOrder(context, sBgDataModel.workspaceScreens);
        Collections.sort(BgDataModel.invalidFoldersToRestore, COMPARATOR);
        for (int i3 = 0; i3 < BgDataModel.invalidFoldersToRestore.size(); i3++) {
            FolderInfo folderInfo = BgDataModel.invalidFoldersToRestore.get(i3);
            if (folderInfo != null) {
                folderInfo.screenId = 1 + j + ((long) (i3 / i));
                int i4 = i3 % i;
                folderInfo.cellX = i4 % profile.numColumns;
                folderInfo.cellY = i4 / profile.numColumns;
                LGLog.i(TAG, "invalid folder restored : " + folderInfo);
                updateItemInDatabase(context, folderInfo);
            }
        }
        BgDataModel.invalidFoldersToRestore.clear();
    }

    private static void addShortcutInFolderToDatabase(Context context, ItemInfo item) {
        if (item.itemType == 2 && (item instanceof FolderInfo)) {
            for (ShortcutInfo shortcutInfo : ((FolderInfo) item).contents) {
                addItemToDatabase(context, shortcutInfo, item.id, item.screenId, shortcutInfo.cellX, shortcutInfo.cellY);
            }
        }
    }

    /* JADX DEBUG: Another duplicated slice has different insns count: {[IF]}, finally: {[IF, INVOKE] complete} */
    public static String getHomePreferences(Context context, String key) {
        if (sHomePreferencesCache.containsKey(key)) {
            return sHomePreferencesCache.get(key);
        }
        Cursor cursorQuery = context.getContentResolver().query(LauncherSettings.HomePreferences.CONTENT_URI, null, "key = ?", new String[]{key}, null, null);
        try {
            try {
            } catch (Exception e) {
                e.printStackTrace();
                if (cursorQuery != null) {
                }
            }
            if (cursorQuery.moveToFirst()) {
                String string = cursorQuery.getString(cursorQuery.getColumnIndexOrThrow("value"));
                sHomePreferencesCache.put(key, string);
                return string;
            }
            if (cursorQuery != null) {
            }
            LGLog.e(TAG, "Failed to read : " + key);
            return null;
            cursorQuery.close();
            LGLog.e(TAG, "Failed to read : " + key);
            return null;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    public static void setHomePreferences(Context context, final String key, final String value) {
        setHomePreferences(context, key, value, false);
    }

    public static void setHomePreferences(Context context, final String key, final String value, final boolean notify) {
        final ContentResolver contentResolver = context.getContentResolver();
        Runnable runnable = new Runnable() { // from class: com.android.launcher3.LauncherModel.31
            @Override // java.lang.Runnable
            public void run() {
                Cursor cursorQuery = contentResolver.query(LauncherSettings.HomePreferences.CONTENT_URI, null, "key = ?", new String[]{key}, null, null);
                if (cursorQuery == null) {
                    LGLog.e(LauncherModel.TAG, "Failed to query : " + LauncherSettings.HomePreferences.CONTENT_URI.toString());
                    return;
                }
                try {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_id", (Integer) 0);
                    contentValues.put(LauncherSettings.HomePreferences.KEY, key);
                    contentValues.put("value", value);
                    if (cursorQuery.moveToFirst()) {
                        contentResolver.update(LauncherSettings.HomePreferences.CONTENT_URI, contentValues, "key = ?", new String[]{key});
                    } else {
                        contentResolver.insert(LauncherSettings.HomePreferences.CONTENT_URI, contentValues);
                    }
                    if (notify) {
                        contentResolver.notifyChange(Uri.withAppendedPath(LauncherSettings.HomePreferences.CONTENT_URI, key), null);
                    }
                } finally {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                }
            }
        };
        sHomePreferencesCache.put(key, value);
        runOnWorkerThread(runnable);
    }

    public void updateShortcutInfo(Context context, final ShortcutInfoCompat fullDetail, final ShortcutInfo info) {
        final Context applicationContext = context.getApplicationContext();
        enqueueItemUpdatedTask(new Runnable() { // from class: com.android.launcher3.LauncherModel.32
            @Override // java.lang.Runnable
            public void run() {
                info.updateFromDeepShortcutInfo(fullDetail, applicationContext);
                ArrayList arrayList = new ArrayList();
                arrayList.add(info);
                LauncherModel.this.bindUpdatedShortcuts(arrayList, fullDetail.getUserHandle());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindUpdatedShortcuts(ArrayList<ShortcutInfo> updatedShortcuts, UserHandle user) {
        bindUpdatedShortcuts(updatedShortcuts, new ArrayList<>(), user);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindUpdatedShortcuts(final ArrayList<ShortcutInfo> updatedShortcuts, final ArrayList<ShortcutInfo> removedShortcuts, final UserHandle user) {
        if (updatedShortcuts.isEmpty() && removedShortcuts.isEmpty()) {
            return;
        }
        final Callbacks callback = getCallback();
        this.mHandler.post(new Runnable() { // from class: com.android.launcher3.LauncherModel.33
            @Override // java.lang.Runnable
            public void run() {
                Callbacks callback2 = LauncherModel.this.getCallback();
                if (callback2 == null || callback != callback2) {
                    return;
                }
                callback2.bindShortcutsChanged(updatedShortcuts, removedShortcuts, user);
            }
        });
    }

    public void updateAndBindShortcutInfo(final ShortcutInfo si, final ShortcutInfoCompat info) {
        updateAndBindShortcutInfo(new Provider<ShortcutInfo>() { // from class: com.android.launcher3.LauncherModel.35
            /* JADX DEBUG: Method merged with bridge method: get()Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.launcher3.util.Provider
            public ShortcutInfo get() {
                si.updateFromDeepShortcutInfo(info, LauncherModel.this.mApp.getContext());
                si.iconBitmap = LauncherIcons.createShortcutIcon(info, LauncherModel.this.mApp.getContext());
                return si;
            }
        });
    }

    public void updateAndBindShortcutInfo(final Provider<ShortcutInfo> shortcutProvider) {
        enqueueModelUpdateTask(new ExtendedModelTask() { // from class: com.android.launcher3.LauncherModel.36
            @Override // com.android.launcher3.LauncherModel.BaseModelUpdateTask
            public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) shortcutProvider.get();
                ArrayList<ShortcutInfo> arrayList = new ArrayList<>();
                arrayList.add(shortcutInfo);
                bindUpdatedShortcuts(arrayList, shortcutInfo.user);
            }
        });
    }

    public void enqueueModelUpdateTask(BaseModelUpdateTask task) {
        task.init(this);
        runOnWorkerThread(task);
    }

    public static abstract class BaseModelUpdateTask implements Runnable {
        private LauncherModel mModel;
        private DeferredHandler mUiHandler;

        public abstract void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps);

        void init(LauncherModel model) {
            this.mModel = model;
            this.mUiHandler = model.mHandler;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mModel.mHasLoaderCompletedOnce) {
                execute(this.mModel.mApp, LauncherModel.sBgDataModel, this.mModel.mBgAllAppsList);
            }
        }

        public final void scheduleCallbackTask(final CallbackTask task) {
            final Callbacks callback = this.mModel.getCallback();
            this.mUiHandler.post(new Runnable() { // from class: com.android.launcher3.LauncherModel.BaseModelUpdateTask.1
                @Override // java.lang.Runnable
                public void run() {
                    Callbacks callback2 = BaseModelUpdateTask.this.mModel.getCallback();
                    Callbacks callbacks = callback;
                    if (callbacks != callback2 || callback2 == null) {
                        return;
                    }
                    task.execute(callbacks);
                }
            });
        }

        public ModelWriter getModelWriter() {
            return this.mModel.getWriter(false);
        }
    }

    public ModelWriter getWriter(boolean hasVerticalHotseat) {
        return new ModelWriter(this.mApp.getContext(), sBgDataModel, hasVerticalHotseat);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDeepShortcutMap(String packageName, UserHandle user, List<ShortcutInfoCompat> shortcuts) {
        if (packageName != null) {
            Iterator<ComponentKey> it = this.mBgDeepShortcutMap.keySet().iterator();
            while (it.hasNext()) {
                ComponentKey next = it.next();
                if (next.componentName.getPackageName().equals(packageName) && next.user.equals(user)) {
                    it.remove();
                }
            }
        }
        for (ShortcutInfoCompat shortcutInfoCompat : shortcuts) {
            if (shortcutInfoCompat.isEnabled() && (shortcutInfoCompat.isDeclaredInManifest() || shortcutInfoCompat.isDynamic())) {
                this.mBgDeepShortcutMap.addToList(new ComponentKey(shortcutInfoCompat.getActivity(), shortcutInfoCompat.getUserHandle()), shortcutInfoCompat.getId());
            }
        }
    }

    public void bindDeepShortcuts() {
        final MultiHashMap<ComponentKey, String> multiHashMapClone = this.mBgDeepShortcutMap.clone();
        scheduleCallbackTask(new CallbackTask() { // from class: com.android.launcher3.LauncherModel.37
            @Override // com.android.launcher3.LauncherModel.CallbackTask
            public void execute(Callbacks callbacks) {
                callbacks.bindDeepShortcutMap(multiHashMapClone);
            }
        });
    }

    public final void scheduleCallbackTask(final CallbackTask task) {
        final Callbacks callback = getCallback();
        this.mUiExecutor.execute(new Runnable() { // from class: com.android.launcher3.-$$Lambda$LauncherModel$emUKioMNi0ZFqXaTE-5EmRCcQc4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$scheduleCallbackTask$2$LauncherModel(callback, task);
            }
        });
    }

    public /* synthetic */ void lambda$scheduleCallbackTask$2$LauncherModel(Callbacks callbacks, CallbackTask callbackTask) {
        Callbacks callback = getCallback();
        if (callbacks != callback || callback == null) {
            return;
        }
        callbackTask.execute(callbacks);
    }

    public void refreshShortcutsIfRequired() {
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            Handler handler = sWorker;
            handler.removeCallbacks(this.mShortcutPermissionCheckRunnable);
            handler.post(this.mShortcutPermissionCheckRunnable);
        }
    }

    void enqueueItemUpdatedTask(Runnable task) {
        sWorker.post(task);
    }

    @Override // com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat
    public void onShortcutsChanged(String packageName, List<ShortcutInfoCompat> shortcuts, UserHandle user) {
        enqueueItemUpdatedTask(new ShortcutsChangedTask(packageName, shortcuts, user, true));
    }

    private class ShortcutsChangedTask implements Runnable {
        private final String mPackageName;
        private final List<ShortcutInfoCompat> mShortcuts;
        private final boolean mUpdateIdMap;
        private final UserHandle mUser;

        public ShortcutsChangedTask(String packageName, List<ShortcutInfoCompat> shortcuts, UserHandle user, boolean updateIdMap) {
            this.mPackageName = packageName;
            this.mShortcuts = shortcuts;
            this.mUser = user;
            this.mUpdateIdMap = updateIdMap;
        }

        @Override // java.lang.Runnable
        public void run() {
            LauncherModel.this.mDeepShortcutManager.onShortcutsChanged(this.mShortcuts);
            ArrayList arrayList = new ArrayList();
            MultiHashMap multiHashMap = new MultiHashMap();
            for (ItemInfo itemInfo : LauncherModel.sBgDataModel.itemsIdMap) {
                if (itemInfo.itemType == 6) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                    if (shortcutInfo.getPromisedIntent().getPackage().equals(this.mPackageName) && shortcutInfo.user.equals(this.mUser)) {
                        multiHashMap.addToList(shortcutInfo.getDeepShortcutId(), shortcutInfo);
                    }
                }
            }
            Context context = LauncherModel.this.mApp.getContext();
            ArrayList arrayList2 = new ArrayList();
            if (!multiHashMap.isEmpty()) {
                for (ShortcutInfoCompat shortcutInfoCompat : LauncherModel.this.mDeepShortcutManager.queryForFullDetails(this.mPackageName, new ArrayList(multiHashMap.keySet()), this.mUser)) {
                    List<ShortcutInfo> listRemove = multiHashMap.remove(shortcutInfoCompat.getId());
                    if (!shortcutInfoCompat.isPinned()) {
                        arrayList.addAll(listRemove);
                    } else {
                        for (ShortcutInfo shortcutInfo2 : listRemove) {
                            shortcutInfo2.updateFromDeepShortcutInfo(shortcutInfoCompat, context);
                            arrayList2.add(shortcutInfo2);
                        }
                    }
                }
            }
            Iterator it = multiHashMap.keySet().iterator();
            while (it.hasNext()) {
                arrayList.addAll(multiHashMap.get((String) it.next()));
            }
            LauncherModel.this.bindUpdatedShortcuts(arrayList2, arrayList, this.mUser);
            if (!arrayList.isEmpty()) {
                LauncherModel.deleteItemsFromDatabase(context, arrayList);
            }
            if (this.mUpdateIdMap) {
                LauncherModel.this.updateDeepShortcutMap(this.mPackageName, this.mUser, this.mShortcuts);
                LauncherModel.this.bindDeepShortcuts();
            }
        }
    }

    private class UserLockStateChangedTask implements Runnable {
        private final UserHandle mUser;

        public UserLockStateChangedTask(UserHandle user) {
            this.mUser = user;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean zIsUserUnlocked = LauncherModel.this.mUserManager.isUserUnlocked(this.mUser);
            Context context = LauncherModel.this.mApp.getContext();
            HashMap map = new HashMap();
            if (zIsUserUnlocked) {
                List<ShortcutInfoCompat> listQueryForPinnedShortcuts = LauncherModel.this.mDeepShortcutManager.queryForPinnedShortcuts(null, this.mUser);
                if (LauncherModel.this.mDeepShortcutManager.wasLastCallSuccess()) {
                    for (ShortcutInfoCompat shortcutInfoCompat : listQueryForPinnedShortcuts) {
                        map.put(ShortcutKey.fromInfo(shortcutInfoCompat), shortcutInfoCompat);
                    }
                } else {
                    zIsUserUnlocked = false;
                }
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (ItemInfo itemInfo : LauncherModel.sBgDataModel.itemsIdMap) {
                if (itemInfo.itemType == 6 && this.mUser.equals(itemInfo.user)) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                    if (zIsUserUnlocked) {
                        ShortcutInfoCompat shortcutInfoCompat2 = (ShortcutInfoCompat) map.get(ShortcutKey.fromShortcutInfo(shortcutInfo));
                        if (shortcutInfoCompat2 == null) {
                            arrayList2.add(shortcutInfo);
                        } else {
                            shortcutInfo.runtimeStatusFlags &= -33;
                            shortcutInfo.updateFromDeepShortcutInfo(shortcutInfoCompat2, context);
                        }
                    } else {
                        shortcutInfo.runtimeStatusFlags |= 32;
                    }
                    arrayList.add(shortcutInfo);
                }
            }
            LauncherModel.this.bindUpdatedShortcuts(arrayList, arrayList2, this.mUser);
            if (!arrayList2.isEmpty()) {
                LauncherModel.deleteItemsFromDatabase(context, arrayList2);
            }
            Iterator it = LauncherModel.this.mBgDeepShortcutMap.keySet().iterator();
            while (it.hasNext()) {
                if (((ComponentKey) it.next()).user.equals(this.mUser)) {
                    it.remove();
                }
            }
            if (zIsUserUnlocked) {
                LauncherModel launcherModel = LauncherModel.this;
                launcherModel.updateDeepShortcutMap(null, this.mUser, launcherModel.mDeepShortcutManager.queryForAllShortcuts(this.mUser));
            }
            LauncherModel.this.bindDeepShortcuts();
        }
    }

    public void forceReloadIcon() {
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.38
            @Override // java.lang.Runnable
            public void run() {
                HashSet hashSet = new HashSet();
                synchronized (LauncherModel.sBgDataModel) {
                    for (ItemInfo itemInfo : LauncherModel.sBgDataModel.itemsIdMap) {
                        if (itemInfo instanceof ShortcutInfo) {
                            ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                            if (shortcutInfo.isPromise() && shortcutInfo.getTargetComponent() != null) {
                                hashSet.add(shortcutInfo.getTargetComponent().getPackageName());
                            }
                        } else if (itemInfo instanceof LauncherAppWidgetInfo) {
                            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
                            if (launcherAppWidgetInfo.hasRestoreFlag(2)) {
                                hashSet.add(launcherAppWidgetInfo.providerName.getPackageName());
                            }
                        }
                    }
                }
                LauncherModel.this.mIconCache.setReloadIcon(true);
                LauncherModel.this.mIconCache.updateDbIcons(hashSet);
            }
        });
    }

    public static void setWorkerPriority(final int priority) {
        Process.setThreadPriority(sWorkerThread.getThreadId(), priority);
    }

    public static void removeNotNeededValuesForSwivel(ContentValues values) {
        values.remove(LauncherSettings.Favorites.CONTAINER);
        values.remove("screen");
        values.remove(LauncherSettings.Favorites.CELLX);
        values.remove(LauncherSettings.Favorites.CELLY);
        values.remove("spanX");
        values.remove("spanY");
    }

    public static void addItemToDatabaseSwivel(Context context, final ShortcutInfo info) {
        if (context == null || info == null) {
            LGLog.i(TAG, "addItemToDatabaseSwivel() parameter is null. context = " + context + ", appInfo = " + info);
            return;
        }
        final ContentValues contentValues = new ContentValues();
        final ContentResolver contentResolver = context.getContentResolver();
        if (info == null || contentResolver == null) {
            LGLog.w(TAG, "addItemToDatabaseSwivel() Item cannot be added to database. item = " + info + ", values = " + contentValues + ", ContentResolver = " + contentResolver + ", info = " + info, new int[0]);
            return;
        }
        info.onAddToDatabase(context, contentValues);
        info.id = LauncherSettings.Settings.callSwivel(contentResolver, LauncherSettings.Settings.METHOD_NEW_ITEM_ID).getLong("value");
        if (info.swivelPosition < 0) {
            info.swivelPosition = getLastPostionFromSwivelDB(context) + 1;
        }
        contentValues.put("_id", Long.valueOf(info.id));
        contentValues.put("swivelPosition", Integer.valueOf(info.swivelPosition));
        removeNotNeededValuesForSwivel(contentValues);
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.39
            /* JADX DEBUG: Another duplicated slice has different insns count: {[IF]}, finally: {[IF, INVOKE] complete} */
            @Override // java.lang.Runnable
            public void run() {
                int iUpdate = 0;
                Cursor cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, new String[]{"_id", "swivelPosition"}, "swivelPosition >= ?", new String[]{Integer.toString(info.swivelPosition)}, "swivelPosition ASC", null);
                contentResolver.insert(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, contentValues);
                try {
                    if (cursorQuery == null) {
                        LGLog.i(LauncherModel.TAG, "addItemToDatabaseSwivel() Cursor is null. Cursor = " + cursorQuery + ", info = " + info);
                        return;
                    }
                    try {
                        int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow("_id");
                        int columnIndexOrThrow2 = cursorQuery.getColumnIndexOrThrow("swivelPosition");
                        while (cursorQuery.moveToNext()) {
                            String str = "_id = " + cursorQuery.getLong(columnIndexOrThrow);
                            ContentValues contentValues2 = new ContentValues();
                            contentValues2.put("swivelPosition", Integer.valueOf(cursorQuery.getInt(columnIndexOrThrow2) + 1));
                            iUpdate += contentResolver.update(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, contentValues2, str, null);
                        }
                        LGLog.i(LauncherModel.TAG, "addItemToDatabaseSwivel() final, result = " + iUpdate);
                        if (cursorQuery == null) {
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (cursorQuery == null) {
                            return;
                        }
                    }
                    cursorQuery.close();
                } catch (Throwable th) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    throw th;
                }
            }
        });
    }

    public static void addBulkItemsToDatabaseSwivel(Context context, final ArrayList<ShortcutInfo> list) {
        if (context == null || list == null || list.isEmpty()) {
            LGLog.i(TAG, "addBulkItemsToDatabaseSwivel() parameter is null or empty. context = " + context + ", list = " + list);
            return;
        }
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            LGLog.w(TAG, "addBulkItemsToDatabaseSwivel() Items cannot be added to database. ContentResolver = " + contentResolver, new int[0]);
            return;
        }
        int size = list.size();
        final ContentValues[] contentValuesArr = new ContentValues[size];
        for (int i = 0; i < size; i++) {
            contentValuesArr[i] = new ContentValues();
            ShortcutInfo shortcutInfo = list.get(i);
            if (contentValuesArr[i] == null || shortcutInfo == null) {
                LGLog.i(TAG, "addBulkItemsToDatabaseSwivel() skip. bulkValues[" + i + "] = " + contentValuesArr[i] + ", item = " + shortcutInfo);
            } else {
                shortcutInfo.onAddToDatabase(context, contentValuesArr[i]);
                shortcutInfo.id = LauncherSettings.Settings.callSwivel(contentResolver, LauncherSettings.Settings.METHOD_NEW_ITEM_ID).getLong("value");
                contentValuesArr[i].put("_id", Long.valueOf(shortcutInfo.id));
                contentValuesArr[i].put("swivelPosition", Integer.valueOf(shortcutInfo.swivelPosition));
                removeNotNeededValuesForSwivel(contentValuesArr[i]);
            }
        }
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.40
            @Override // java.lang.Runnable
            public void run() {
                contentResolver.bulkInsert(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, contentValuesArr);
            }
        });
    }

    public static void updateItemToDatabaseSwivel(Context context, final ShortcutInfo info) {
        if (context == null || info == null) {
            LGLog.i(TAG, "updateItemToDatabaseSwivel() parameter is null. context = " + context + ", appInfo = " + info);
            return;
        }
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            LGLog.w(TAG, "updateItemToDatabaseSwivel() Item cannot be added to database. ", new int[0]);
        } else {
            runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.41
                /* JADX DEBUG: Another duplicated slice has different insns count: {[IF]}, finally: {[IF, INVOKE] complete} */
                @Override // java.lang.Runnable
                public void run() {
                    int iUpdate = 0;
                    Cursor cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, null, "swivelPosition = ?", new String[]{Long.toString(info.swivelPosition)}, null, null);
                    while (cursorQuery.moveToNext()) {
                        try {
                            try {
                                String str = "swivelPosition = " + info.swivelPosition;
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("title", info.title.toString());
                                contentValues.put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(info.status));
                                iUpdate += contentResolver.update(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, contentValues, str, null);
                            } catch (Exception e) {
                                LGLog.e(LauncherModel.TAG, "Exception occurred on updateItemToDatabaseSwivel : " + e);
                                if (cursorQuery == null) {
                                    return;
                                }
                            }
                        } catch (Throwable th) {
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            throw th;
                        }
                    }
                    LGLog.i(LauncherModel.TAG, "updateItemToDatabaseSwivel() final, result = " + iUpdate);
                    if (cursorQuery == null) {
                        return;
                    }
                    cursorQuery.close();
                }
            });
        }
    }

    public static void deleteItemsFromDatabaseSwivel(Context context, final ShortcutInfo info) {
        if (context == null || info == null) {
            LGLog.i(TAG, "deleteItemsFromDatabaseSwivel() parameter is null. context = " + context + ", info = " + info);
            return;
        }
        if (info.swivelPosition < 0) {
            LGLog.i(TAG, "deleteItemsFromDatabaseSwivel() position of app is invalid. info.swivelPosition = " + info.swivelPosition);
            return;
        }
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            LGLog.w(TAG, "deleteItemsFromDatabaseSwivel() Item cannot be deleted to database. ContentResolver = " + contentResolver + ", info = " + info, new int[0]);
            return;
        }
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.42
            /* JADX DEBUG: Another duplicated slice has different insns count: {[IF]}, finally: {[IF, INVOKE] complete} */
            @Override // java.lang.Runnable
            public void run() {
                Cursor cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, new String[]{"_id", "swivelPosition"}, "swivelPosition > ?", new String[]{Integer.toString(info.swivelPosition)}, "swivelPosition ASC", null);
                int iDelete = contentResolver.delete(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, "swivelPosition=" + info.swivelPosition, null);
                LGLog.i(LauncherModel.TAG, "deleteItemsFromDatabaseSwivel() after delete position = " + info.swivelPosition + ", result = " + iDelete);
                if (cursorQuery == null) {
                    LGLog.i(LauncherModel.TAG, "deleteItemsFromDatabaseSwivel() Cursor is null. Cursor = " + cursorQuery + ", info = " + info);
                    return;
                }
                while (cursorQuery.moveToNext()) {
                    try {
                        try {
                            String str = "_id = " + cursorQuery.getInt(0);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("swivelPosition", Integer.valueOf(cursorQuery.getInt(1) - 1));
                            iDelete += contentResolver.update(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, contentValues, str, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (cursorQuery == null) {
                                return;
                            }
                        }
                    } catch (Throwable th) {
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        throw th;
                    }
                }
                LGLog.i(LauncherModel.TAG, "deleteItemsFromDatabaseSwivel() final, result = " + iDelete);
                if (cursorQuery == null) {
                    return;
                }
                cursorQuery.close();
            }
        });
    }

    public static void updateSwivelPosition(Context context, final int prevPosition, final int newPosition) {
        if (prevPosition == newPosition || prevPosition < 0 || newPosition < 0) {
            LGLog.i(TAG, "updateSwivelPosition() position is invalid. prevPosition = " + prevPosition + ", newPosition = " + newPosition);
            return;
        }
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            LGLog.w(TAG, "updateSwivelPosition() Item cannot be updated to database. ContentResolver = " + contentResolver + ", prevPosition = " + prevPosition + ", newPosition = " + newPosition, new int[0]);
            return;
        }
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.43
            /* JADX DEBUG: Another duplicated slice has different insns count: {[IF]}, finally: {[IF, INVOKE] complete} */
            @Override // java.lang.Runnable
            public void run() {
                String[] strArr;
                String str;
                int i;
                String[] strArr2 = {"_id", "swivelPosition"};
                int i2 = prevPosition;
                int i3 = newPosition;
                if (i2 < i3) {
                    String[] strArr3 = {Integer.toString(i2), Integer.toString(newPosition)};
                    i = -1;
                    strArr = strArr3;
                    str = "swivelPosition > ? AND swivelPosition <= ? ";
                } else {
                    strArr = new String[]{Integer.toString(i3), Integer.toString(prevPosition)};
                    str = "swivelPosition >= ? AND swivelPosition < ? ";
                    i = 1;
                }
                Cursor cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, strArr2, str, strArr, "swivelPosition ASC", null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("swivelPosition", Integer.valueOf(newPosition));
                int iUpdate = contentResolver.update(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, contentValues, "swivelPosition=" + prevPosition, null) + 0;
                LGLog.i(LauncherModel.TAG, "updateSwivelPosition() updated from prevPosition(" + prevPosition + ") to newPosition(" + newPosition + "). result = " + iUpdate);
                if (cursorQuery == null) {
                    LGLog.i(LauncherModel.TAG, "updateSwivelPosition() Cursor is null. Cursor = " + cursorQuery + ", prevPosition = " + prevPosition + ", newPosition = " + newPosition);
                    return;
                }
                while (cursorQuery.moveToNext()) {
                    try {
                        try {
                            String str2 = "_id = " + cursorQuery.getInt(0);
                            ContentValues contentValues2 = new ContentValues();
                            contentValues2.put("swivelPosition", Integer.valueOf(cursorQuery.getInt(1) + i));
                            iUpdate += contentResolver.update(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, contentValues2, str2, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (cursorQuery == null) {
                                return;
                            }
                        }
                    } catch (Throwable th) {
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        throw th;
                    }
                }
                LGLog.i(LauncherModel.TAG, "updateSwivelPosition() final, result = " + iUpdate);
                if (cursorQuery == null) {
                    return;
                }
                cursorQuery.close();
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:66:0x018b  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0191  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.ArrayList<com.android.launcher3.ShortcutInfo> getAppListForSwivelHome(android.content.Context r21) {
        /*
            r0 = r21
            r1 = 0
            java.lang.String r2 = "Launcher.Model"
            if (r0 != 0) goto L1c
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "getAllAppListForSwivelHome() context is null. context = "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            com.lge.launcher3.util.LGLog.i(r2, r0)
            return r1
        L1c:
            android.content.ContentResolver r3 = r21.getContentResolver()
            r10 = 0
            if (r3 != 0) goto L3a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "getAppListForSwivelHome() Items cannot be gotten from database. ContentResolver = "
            r0.append(r4)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            int[] r3 = new int[r10]
            com.lge.launcher3.util.LGLog.w(r2, r0, r3)
            return r1
        L3a:
            android.net.Uri r4 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI_SWIVEL
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            java.lang.String r8 = "swivelPosition ASC"
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8, r9)
            if (r3 != 0) goto L5d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "getAllAppListForSwivelHome() Cursor is null. Cursor = "
            r0.append(r4)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            com.lge.launcher3.util.LGLog.i(r2, r0)
            return r1
        L5d:
            java.lang.String r4 = "_id"
            int r4 = r3.getColumnIndexOrThrow(r4)     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            java.lang.String r5 = "title"
            int r5 = r3.getColumnIndexOrThrow(r5)     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            java.lang.String r6 = "intent"
            int r6 = r3.getColumnIndexOrThrow(r6)     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            java.lang.String r7 = "container"
            int r7 = r3.getColumnIndexOrThrow(r7)     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            java.lang.String r8 = "swivelPosition"
            int r8 = r3.getColumnIndexOrThrow(r8)     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            java.lang.String r9 = "itemType"
            int r9 = r3.getColumnIndexOrThrow(r9)     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            java.lang.String r11 = "restored"
            int r11 = r3.getColumnIndexOrThrow(r11)     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            java.lang.String r12 = "profileId"
            int r12 = r3.getColumnIndexOrThrow(r12)     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            java.lang.String r13 = "rank"
            int r13 = r3.getColumnIndexOrThrow(r13)     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            com.android.launcher3.util.CursorIconInfo r14 = new com.android.launcher3.util.CursorIconInfo     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            r14.<init>(r3)     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            java.util.ArrayList r15 = new java.util.ArrayList     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            r15.<init>()     // Catch: java.lang.Exception -> L185 java.lang.Throwable -> L18f
            android.content.pm.PackageManager r1 = r21.getPackageManager()     // Catch: java.lang.Throwable -> L17e java.lang.Exception -> L181
        La1:
            boolean r16 = r3.moveToNext()     // Catch: java.lang.Throwable -> L17e java.lang.Exception -> L181
            if (r16 == 0) goto L15d
            int r10 = r3.getInt(r9)     // Catch: java.lang.Throwable -> L17e java.lang.Exception -> L181
            r17 = r9
            r9 = 6
            if (r10 == 0) goto Lb6
            if (r10 == r9) goto Lb6
            r9 = r17
            r10 = 0
            goto La1
        Lb6:
            com.android.launcher3.ShortcutInfo r9 = new com.android.launcher3.ShortcutInfo     // Catch: java.lang.Throwable -> L17e java.lang.Exception -> L181
            r9.<init>()     // Catch: java.lang.Throwable -> L17e java.lang.Exception -> L181
            r18 = r14
            r19 = r15
            long r14 = r3.getLong(r4)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            r9.id = r14     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            java.lang.String r14 = r3.getString(r6)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            r15 = 0
            android.content.Intent r14 = android.content.Intent.parseUri(r14, r15)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            r9.intent = r14     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            android.content.Intent r14 = r9.intent     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            java.lang.String r15 = "profile"
            r20 = r4
            int r4 = r3.getInt(r12)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            r14.putExtra(r15, r4)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            if (r10 != 0) goto Lf1
            android.content.ComponentName r4 = r9.getTargetComponent()     // Catch: java.lang.Throwable -> Le8 java.lang.Exception -> Lec
            java.lang.String r4 = com.lge.launcher3.util.PackageUtils.getApplicationLabelFromResolveInfo(r0, r4)     // Catch: java.lang.Throwable -> Le8 java.lang.Exception -> Lec
            goto Lf5
        Le8:
            r1 = r19
            goto L18f
        Lec:
            r0 = move-exception
            r1 = r19
            goto L186
        Lf1:
            java.lang.String r4 = r3.getString(r5)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
        Lf5:
            r9.title = r4     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            int r4 = r3.getInt(r7)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            long r14 = (long) r4     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            r9.container = r14     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            int r4 = r3.getInt(r8)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            r9.swivelPosition = r4     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            r9.itemType = r10     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            int r4 = r3.getInt(r13)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            r9.rank = r4     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            android.content.ComponentName r4 = r9.getTargetComponent()     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            int r4 = com.android.launcher3.model.data.AppInfo.getFlagsForUninstalledPackage(r1, r4)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            r9.flags = r4     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            int r4 = r3.getInt(r11)     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            if (r4 <= 0) goto L136
            android.content.ComponentName r4 = r9.getTargetComponent()     // Catch: java.lang.Throwable -> Le8 java.lang.Exception -> Lec
            java.lang.String r4 = r4.getPackageName()     // Catch: java.lang.Throwable -> Le8 java.lang.Exception -> Lec
            boolean r4 = com.lge.launcher3.util.PackageUtils.isPackageInstalled(r0, r4)     // Catch: java.lang.Throwable -> Le8 java.lang.Exception -> Lec
            if (r4 == 0) goto L12e
            r4 = 0
            r9.status = r4     // Catch: java.lang.Throwable -> Le8 java.lang.Exception -> Lec
            goto L137
        L12e:
            r4 = 0
            int r10 = r9.status     // Catch: java.lang.Throwable -> Le8 java.lang.Exception -> Lec
            r10 = r10 | 256(0x100, float:3.59E-43)
            r9.status = r10     // Catch: java.lang.Throwable -> Le8 java.lang.Exception -> Lec
            goto L137
        L136:
            r4 = 0
        L137:
            int r10 = r9.itemType     // Catch: java.lang.Throwable -> L156 java.lang.Exception -> L159
            r14 = 6
            if (r10 != r14) goto L146
            r10 = r18
            android.graphics.Bitmap r14 = r10.loadIcon(r3, r9, r0)     // Catch: java.lang.Throwable -> Le8 java.lang.Exception -> Lec
            r9.setIcon(r14)     // Catch: java.lang.Throwable -> Le8 java.lang.Exception -> Lec
            goto L148
        L146:
            r10 = r18
        L148:
            r14 = r19
            r14.add(r9)     // Catch: java.lang.Exception -> L17c java.lang.Throwable -> L17f
            r15 = r14
            r9 = r17
            r14 = r10
            r10 = r4
            r4 = r20
            goto La1
        L156:
            r14 = r19
            goto L17f
        L159:
            r0 = move-exception
            r14 = r19
            goto L183
        L15d:
            r14 = r15
            int r0 = r14.size()     // Catch: java.lang.Exception -> L17c java.lang.Throwable -> L17f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L17c java.lang.Throwable -> L17f
            r1.<init>()     // Catch: java.lang.Exception -> L17c java.lang.Throwable -> L17f
            java.lang.String r4 = "getAllAppListForSwivelHome() final, appList.size() = "
            r1.append(r4)     // Catch: java.lang.Exception -> L17c java.lang.Throwable -> L17f
            r1.append(r0)     // Catch: java.lang.Exception -> L17c java.lang.Throwable -> L17f
            java.lang.String r0 = r1.toString()     // Catch: java.lang.Exception -> L17c java.lang.Throwable -> L17f
            com.lge.launcher3.util.LGLog.i(r2, r0)     // Catch: java.lang.Exception -> L17c java.lang.Throwable -> L17f
            if (r3 == 0) goto L17b
            r3.close()
        L17b:
            return r14
        L17c:
            r0 = move-exception
            goto L183
        L17e:
            r14 = r15
        L17f:
            r1 = r14
            goto L18f
        L181:
            r0 = move-exception
            r14 = r15
        L183:
            r1 = r14
            goto L186
        L185:
            r0 = move-exception
        L186:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L18f
            if (r3 == 0) goto L18e
            r3.close()
        L18e:
            return r1
        L18f:
            if (r3 == 0) goto L194
            r3.close()
        L194:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherModel.getAppListForSwivelHome(android.content.Context):java.util.ArrayList");
    }

    public static int getLastPostionFromSwivelDB(Context context) {
        if (context == null) {
            LGLog.i(TAG, "getLastPostionFromSwivelDB() context is null. context = " + context);
            return -1;
        }
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            LGLog.w(TAG, "getLastPostionFromSwivelDB() Items cannot be gotten from database. ContentResolver = " + contentResolver, new int[0]);
            return -1;
        }
        Cursor cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, null, null, null, "swivelPosition DESC", null);
        try {
            if (cursorQuery == null) {
                LGLog.i(TAG, "getLastPostionFromSwivelDB() Cursor is null. Cursor = " + cursorQuery);
                return -1;
            }
            try {
                int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow("swivelPosition");
                cursorQuery.moveToNext();
                int i = cursorQuery.getInt(columnIndexOrThrow);
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                LGLog.i(TAG, "getLastPostionFromSwivelDB() lastPosition = " + i);
                return i;
            } catch (Exception e) {
                e.printStackTrace();
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                LGLog.i(TAG, "getLastPostionFromSwivelDB() lastPosition = -1");
                return -1;
            }
        } catch (Throwable unused) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            LGLog.i(TAG, "getLastPostionFromSwivelDB() lastPosition = -1");
            return -1;
        }
    }

    public boolean resetSwivelHome() {
        LGLog.i(TAG, "resetSwivelHome() Callback = " + getCallback());
        if (getCallback() == null) {
            return false;
        }
        return getCallback().resetSwivelHome();
    }

    public static void clearDatabaseSwivel(Context context) {
        LGLog.i(TAG, "clearDatabaseSwivel() context = " + context);
        if (context == null) {
            LGLog.i(TAG, "clearDatabaseSwivel() parameter is null. context = " + context);
            return;
        }
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            LGLog.w(TAG, "clearDatabaseSwivel() Item cannot be deleted to database. ContentResolver = " + contentResolver, new int[0]);
            return;
        }
        runOnWorkerThread(new Runnable() { // from class: com.android.launcher3.LauncherModel.44
            @Override // java.lang.Runnable
            public void run() {
                LGLog.i(LauncherModel.TAG, "clearDatabaseSwivel() after delete. result = " + contentResolver.delete(LauncherSettings.Favorites.CONTENT_URI_SWIVEL, null, null));
            }
        });
    }
}
