package com.android.launcher3;

import android.app.WallpaperManager;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Looper;
import android.util.Log;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.config.ProviderConfig;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.util.Preconditions;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.widgettray.LGWidgetPreviewLoader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/* JADX INFO: loaded from: classes.dex */
public class LauncherAppState {
    public static final boolean BYPASS_NATIVE_CODES = false;
    private static LauncherAppState INSTANCE;
    private LauncherAccessibilityDelegate mAccessibilityDelegate;
    private final AppFilter mAppFilter;
    private final Context mContext;
    private final DeepShortcutManager mDeepShortcutManager;
    private int mHideAppsCount = -1;
    private final IconCache mIconCache;
    private InvariantDeviceProfile mInvariantDeviceProfile;
    private final LauncherModel mModel;
    private boolean mWallpaperChangedSinceLastCheck;
    private int mWallpaperId;
    private final WidgetPreviewLoader mWidgetCache;

    public static String getSharedPreferencesKey() {
        return LauncherFiles.SHARED_PREFERENCES_KEY;
    }

    public static LauncherAppState getInstance(final Context context) {
        if (INSTANCE == null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                INSTANCE = new LauncherAppState(context.getApplicationContext());
            } else {
                try {
                    return (LauncherAppState) new MainThreadExecutor().submit(new Callable<LauncherAppState>() { // from class: com.android.launcher3.LauncherAppState.1
                        /* JADX DEBUG: Method merged with bridge method: call()Ljava/lang/Object; */
                        /* JADX WARN: Can't rename method to resolve collision */
                        @Override // java.util.concurrent.Callable
                        public LauncherAppState call() throws Exception {
                            return LauncherAppState.getInstance(context);
                        }
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return INSTANCE;
    }

    public static LauncherAppState getInstanceNoCreate() {
        return INSTANCE;
    }

    public Context getContext() {
        return this.mContext;
    }

    private LauncherAppState(Context context) {
        this.mWallpaperId = -1;
        ContentProviderClient localProvider = getLocalProvider(context);
        if (localProvider == null) {
            throw new RuntimeException("Initializing LauncherAppState in the absence of LauncherProvider");
        }
        localProvider.close();
        Log.v("Launcher", "LauncherAppState initiated");
        Preconditions.assertUIThread();
        this.mContext = context;
        if (context.getResources().getBoolean(R.bool.debug_memory_enabled)) {
            MemoryTracker.startTrackingMe(context, "L");
        }
        this.mInvariantDeviceProfile = new LGInvariantDeviceProfile(context);
        IconCache iconCache = new IconCache(context, this.mInvariantDeviceProfile);
        this.mIconCache = iconCache;
        this.mWidgetCache = new LGWidgetPreviewLoader(context, iconCache);
        DeepShortcutManager deepShortcutManager = DeepShortcutManager.getInstance(context);
        this.mDeepShortcutManager = deepShortcutManager;
        AppFilter appFilter = (AppFilter) Utilities.getOverrideObject(AppFilter.class, context, R.string.app_filter_class);
        this.mAppFilter = appFilter;
        LauncherModel launcherModel = new LauncherModel(this, iconCache, appFilter, deepShortcutManager);
        this.mModel = launcherModel;
        LauncherAppsCompat.getInstance(context).addOnAppsChangedCallback(launcherModel);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        if (context.getResources().getBoolean(R.bool.qsb_enabled)) {
            intentFilter.addAction("android.search.action.GLOBAL_SEARCH_ACTIVITY_CHANGED");
            intentFilter.addAction("android.search.action.SEARCHABLES_CHANGED");
        }
        intentFilter.addAction(LauncherAppsCompat.ACTION_MANAGED_PROFILE_ADDED);
        intentFilter.addAction(LauncherAppsCompat.ACTION_MANAGED_PROFILE_REMOVED);
        intentFilter.addAction(LauncherAppsCompat.ACTION_MANAGED_PROFILE_AVAILABLE);
        intentFilter.addAction(LauncherAppsCompat.ACTION_MANAGED_PROFILE_UNAVAILABLE);
        intentFilter.addAction(LauncherAppsCompat.ACTION_MANAGED_PROFILE_UNLOCKED);
        intentFilter.addAction(IntentConst.Action.ACTION_RELOAD_CUSTOMCONTENT.getValue(context));
        intentFilter.addAction("com.lge.launcher2.FORCERELOAD_HOME");
        intentFilter.addAction("com.lge.launcher2.ADD_WIDGET_IN_NEWPAGE");
        intentFilter.addAction("com.lge.launcher2.LLK_RESTORE");
        intentFilter.addAction("com.lge.android.intent.action.RESOLUTION_SWITCH_MODE_CHANGED");
        intentFilter.addAction("com.lge.launcher3.intent.action.SPRINT_BRAND_MODE_CHANGED");
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("android.app.action.DEVICE_POLICY_RESOURCE_UPDATED");
        context.registerReceiver(launcherModel, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.OVERLAY_CHANGED");
        intentFilter2.addDataScheme(AppNotifierManager.ExtraSpec.USAGE_PACKAGE);
        context.registerReceiver(launcherModel, intentFilter2);
        UserManagerCompat.getInstance(context).enableAndResetCache();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        if (wallpaperManager != null) {
            this.mWallpaperId = wallpaperManager.getWallpaperId(1);
        }
    }

    public void onTerminate() {
        this.mContext.unregisterReceiver(this.mModel);
        LauncherAppsCompat.getInstance(this.mContext).removeOnAppsChangedCallback(this.mModel);
        PackageInstallerCompat.getInstance(this.mContext).onStop();
    }

    public void reloadWorkspace() {
        this.mModel.resetLoadedState(false, true);
        this.mModel.startLoaderFromBackground();
    }

    LauncherModel setLauncher(Launcher launcher) {
        ContentProviderClient localProvider = getLocalProvider(this.mContext);
        if (localProvider != null) {
            ((LauncherProvider) localProvider.getLocalContentProvider()).setLauncherProviderChangeListener(launcher);
            localProvider.close();
        }
        this.mModel.initialize(launcher);
        this.mAccessibilityDelegate = (launcher == null || !Utilities.isLmpOrAbove()) ? null : new LauncherAccessibilityDelegate(launcher);
        return this.mModel;
    }

    public LauncherAccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public IconCache getIconCache() {
        return this.mIconCache;
    }

    public LauncherModel getModel() {
        return this.mModel;
    }

    public WidgetPreviewLoader getWidgetCache() {
        return this.mWidgetCache;
    }

    public void onWallpaperChanged() {
        Context context = this.mContext;
        boolean z = true;
        int wallpaperId = (context == null || WallpaperManager.getInstance(context) == null) ? -1 : WallpaperManager.getInstance(this.mContext).getWallpaperId(1);
        Log.i("Launcher", "onWallpaperChanged(): prev = " + this.mWallpaperId + ", next = " + wallpaperId);
        if (this.mWallpaperId != wallpaperId) {
            this.mWallpaperChangedSinceLastCheck = true;
            this.mWallpaperId = wallpaperId;
        }
        Context context2 = this.mContext;
        if (context2 != null) {
            boolean themedIconEnabled = HomeSettingsSharedPreferences.getThemedIconEnabled(context2);
            if (!DDTUtils.isAdditionalThemeApplied(this.mContext) && !DDTUtils.isAdditionalIconThemeApplied(this.mContext)) {
                z = false;
            }
            if (!themedIconEnabled || z) {
                return;
            }
            getIconCache().clearIconDB();
            ((LGWidgetPreviewLoader) getWidgetCache()).clearCacheDB();
            FolderIcon.clearFolderCache();
            LiveIconManager.getInstance(getContext()).setForceUpdate();
            getModel().forceReload();
        }
    }

    public boolean hasWallpaperChangedSinceLastCheck() {
        boolean z = this.mWallpaperChangedSinceLastCheck;
        this.mWallpaperChangedSinceLastCheck = false;
        return z;
    }

    public InvariantDeviceProfile getInvariantDeviceProfile() {
        return this.mInvariantDeviceProfile;
    }

    public static InvariantDeviceProfile getIDP(Context context) {
        return getInstance(context).getInvariantDeviceProfile();
    }

    private static ContentProviderClient getLocalProvider(Context context) {
        try {
            return context.getContentResolver().acquireContentProviderClient(LauncherProvider.AUTHORITY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isDogfoodBuild() {
        return ProviderConfig.IS_DOGFOOD_BUILD;
    }

    public void updateValues() {
        LGInvariantDeviceProfile lGInvariantDeviceProfile = new LGInvariantDeviceProfile(this.mContext);
        this.mInvariantDeviceProfile = lGInvariantDeviceProfile;
        IconCache iconCache = this.mIconCache;
        if (iconCache != null) {
            iconCache.updateInvariantDeviceProfile(lGInvariantDeviceProfile);
        }
    }

    public void clearAndReloadWorkspace(int loadFlags) {
        this.mModel.resetLoadedState(true, true);
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() && !this.mModel.resetSwivelHome()) {
            LauncherModel.clearDatabaseSwivel(this.mContext);
            this.mContext.getSharedPreferences(getSharedPreferencesKey(), 0).edit().putBoolean("EMPTY_SWIVEL_DATABASE_CREATED", true).apply();
            LauncherSettings.Settings.callSwivel(this.mContext.getContentResolver(), LauncherSettings.Settings.METHOD_LOAD_DEFAULT_SWIVEL_FAVORITES);
        }
        this.mModel.startLoader(com.lge.launcher3.PagedView.INVALID_RESTORE_PAGE, loadFlags);
    }

    public void clearAndReloadWorkspace() {
        clearAndReloadWorkspace(1);
    }

    public void clearAndReloadWorkspaceNotify() {
        clearAndReloadWorkspace(9);
    }

    public DeepShortcutManager getShortcutManager() {
        return this.mDeepShortcutManager;
    }

    public int getHideAppsCount() {
        return this.mHideAppsCount;
    }

    public void setHideAppsCount(int hideAppsCount) {
        this.mHideAppsCount = hideAppsCount;
    }
}
