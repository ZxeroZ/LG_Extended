package com.android.launcher3.icons;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.CursorWindowAllocationException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import androidx.core.view.ViewCompat;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.LauncherIconsForShadow;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.cache.BaseIconCache;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.Provider;
import com.android.launcher3.util.SQLiteCacheHelper;
import com.android.quickstep.RecentsModel;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/* JADX INFO: loaded from: classes.dex */
public class IconCache {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_IGNORE_CACHE = false;
    public static final String EMPTY_CLASS_NAME = ".";
    static final Object ICON_UPDATE_TOKEN = new Object();
    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;
    private static final int LOW_RES_SCALE_FACTOR = 5;
    private static final String TAG = "Launcher.IconCache";
    private final int mActivityBgColor;
    private final Context mContext;
    private int mDeviceDpi;
    final IconDB mIconDb;
    private int mIconDpi;
    private IconProvider mIconProvider;
    private boolean mIsReloadIcon;
    private final LauncherAppsCompat mLauncherApps;
    private Bitmap mLowResBitmap;
    private final BitmapFactory.Options mLowResOptions;
    private final int mPackageBgColor;
    private final PackageManager mPackageManager;
    final UserManagerCompat mUserManager;
    private final HashMap<UserHandle, Bitmap> mDefaultIcons = new HashMap<>();
    final MainThreadExecutor mMainThreadExecutor = new MainThreadExecutor();
    private final HashMap<ComponentKey, CacheEntry> mCache = new HashMap<>(50);
    private int mPendingIconRequestCount = 0;
    public final HashMap<ComponentName, CustomAppIconEntry> mCustomAppIconList = new HashMap<>();
    private Canvas mLowResCanvas = new Canvas();
    private Paint mLowResPaint = new Paint(3);
    final Handler mWorkerHandler = new Handler(LauncherModel.getWorkerLooper());

    public static class CacheEntry {
        public Bitmap icon;
        public boolean isLowResIcon;
        public CharSequence title = "";
        public CharSequence contentDescription = "";
    }

    public interface ItemInfoUpdateReceiver {
        void reapplyItemInfo(ItemInfoWithIcon info);
    }

    public void getUnbadgedShortcutIcon(ItemInfoWithIcon info, ShortcutInfo si) {
    }

    public IconCache(Context context, InvariantDeviceProfile inv) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = UserManagerCompat.getInstance(context);
        this.mLauncherApps = LauncherAppsCompat.getInstance(context);
        this.mIconDpi = inv.fillResIconDpi;
        this.mIconDb = new IconDB(context, inv.iconBitmapSize);
        this.mIconProvider = (IconProvider) Utilities.getOverrideObject(IconProvider.class, context, com.lge.launcher3.R.string.icon_provider_class);
        this.mActivityBgColor = context.getResources().getColor(com.lge.launcher3.R.color.allapps_shortcut_bg_color);
        this.mPackageBgColor = context.getResources().getColor(com.lge.launcher3.R.color.quantum_panel_bg_color_dark);
        BitmapFactory.Options options = new BitmapFactory.Options();
        this.mLowResOptions = options;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        this.mDeviceDpi = displayMetrics.densityDpi;
        initCustomAppIconList();
    }

    private Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(), android.R.mipmap.sym_def_app_icon);
    }

    private Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable drawableForDensity;
        Drawable drawableForDensity2 = null;
        try {
            drawableForDensity = resources.getDrawableForDensity(iconId, this.mIconDpi);
        } catch (Resources.NotFoundException e) {
            Log.i(TAG, "Resources not found " + e);
            drawableForDensity = null;
        }
        if (drawableForDensity == null) {
            try {
                drawableForDensity2 = resources.getDrawableForDensity(iconId, this.mDeviceDpi);
            } catch (Resources.NotFoundException e2) {
                Log.i(TAG, "Resources not found " + e2);
            }
        } else {
            drawableForDensity2 = drawableForDensity;
        }
        return drawableForDensity2 != null ? drawableForDensity2 : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resourcesForApplication;
        try {
            resourcesForApplication = this.mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "package not found " + e);
            resourcesForApplication = null;
        }
        if (resourcesForApplication != null && iconId != 0) {
            return getFullResIcon(resourcesForApplication, iconId);
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ActivityInfo info) {
        Resources resourcesForApplication;
        int iconResource;
        Drawable fullResIcon = null;
        try {
            resourcesForApplication = this.mPackageManager.getResourcesForApplication(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "package not found " + e);
            resourcesForApplication = null;
        }
        if (resourcesForApplication != null && (iconResource = info.getIconResource()) != 0) {
            fullResIcon = getFullResIcon(resourcesForApplication, iconResource);
        }
        if (fullResIcon != null) {
            String str = info.applicationInfo.packageName;
            int iconResource2 = info.getIconResource();
            return ((!(iconResource2 != 0 && iconResource2 == info.applicationInfo.icon) || DDTUtils.needToConvertCushinIcon(this.mContext, str, iconResource2)) && LGHomeFeature.Config.FEATURE_USE_SHORTCUT_CUSHION.getValue()) ? DDTUtils.convertToCushionIcon(this.mContext, fullResIcon, str, iconResource2) : fullResIcon;
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(LauncherActivityInfo info) {
        CustomAppIconEntry customAppIconEntry;
        Drawable monochrome;
        Drawable applicationIcon = null;
        if (info == null) {
            return null;
        }
        Drawable icon = this.mIconProvider.getIcon(info, this.mIconDpi, getContext());
        try {
            applicationIcon = this.mContext.getPackageManager().getApplicationIcon(info.getComponentName().getPackageName());
        } catch (Exception unused) {
            LGLog.i(TAG, "getFullResIcon : drawable is null.");
        }
        if (HomeSettingsSharedPreferences.getThemedIconEnabled(this.mContext) && (applicationIcon instanceof AdaptiveIconDrawable) && (monochrome = ((AdaptiveIconDrawable) applicationIcon).getMonochrome()) != null) {
            icon = monochrome;
        }
        return (!this.mCustomAppIconList.containsKey(info.getComponentName()) || (customAppIconEntry = this.mCustomAppIconList.get(info.getComponentName())) == null || customAppIconEntry.mIconResId == 0) ? icon : getFullResCustomIcon(info.getComponentName().getPackageName(), customAppIconEntry.mIconResId);
    }

    public Drawable getSwivelIcon(LauncherActivityInfo info) {
        return this.mIconProvider.getSwivelIcon(info, this.mIconDpi, getContext());
    }

    protected Bitmap makeDefaultIcon(UserHandle user) {
        return com.android.launcher3.graphics.LauncherIcons.createBadgedIconBitmap(getFullResDefaultActivityIcon(), user, this.mContext, 26);
    }

    public synchronized void remove(ComponentName componentName, UserHandle user) {
        this.mCache.remove(new ComponentKey(componentName, user));
    }

    private void removeFromMemCacheLocked(String packageName, UserHandle user) {
        HashSet hashSet = new HashSet();
        for (ComponentKey componentKey : this.mCache.keySet()) {
            if (componentKey.componentName.getPackageName().equals(packageName) && componentKey.user.equals(user)) {
                hashSet.add(componentKey);
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            this.mCache.remove((ComponentKey) it.next());
        }
    }

    public synchronized void updateIconsForPkg(String packageName, UserHandle user) {
        removeIconsForPkg(packageName, user);
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(packageName, 8192);
            long serialNumberForUser = this.mUserManager.getSerialNumberForUser(user);
            Iterator<LauncherActivityInfo> it = this.mLauncherApps.getActivityList(packageName, user).iterator();
            while (it.hasNext()) {
                addIconToDBAndMemCache(it.next(), packageInfo, serialNumberForUser, false);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Package not found", e);
        }
    }

    public synchronized void removeIconsForPkg(String packageName, UserHandle user) {
        removeFromMemCacheLocked(packageName, user);
        long serialNumberForUser = this.mUserManager.getSerialNumberForUser(user);
        this.mIconDb.delete("componentName LIKE ? AND profileId = ?", new String[]{packageName + "/%", Long.toString(serialNumberForUser)});
    }

    public void updateDbIcons(Set<String> ignorePackagesForMainUser) {
        UserHandle next;
        List<LauncherActivityInfo> activityList;
        this.mWorkerHandler.removeCallbacksAndMessages(ICON_UPDATE_TOKEN);
        this.mIconProvider.updateSystemStateString();
        Iterator<UserHandle> it = this.mUserManager.getUserProfiles().iterator();
        while (it.hasNext() && (activityList = this.mLauncherApps.getActivityList(null, (next = it.next()))) != null && !activityList.isEmpty()) {
            updateDBIcons(next, activityList, Process.myUserHandle().equals(next) ? ignorePackagesForMainUser : Collections.emptySet());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0147 A[PHI: r12 r14
      0x0147: PHI (r12v3 android.database.Cursor) = (r12v4 android.database.Cursor), (r12v7 android.database.Cursor) binds: [B:55:0x0145, B:43:0x012d] A[DONT_GENERATE, DONT_INLINE]
      0x0147: PHI (r14v3 java.util.Stack) = (r14v4 java.util.Stack), (r14v9 java.util.Stack) binds: [B:55:0x0145, B:43:0x012d] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0150  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0160  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0186  */
    /* JADX WARN: Type inference failed for: r13v0 */
    /* JADX WARN: Type inference failed for: r13v1, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r13v2 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void updateDBIcons(android.os.UserHandle r23, java.util.List<android.content.pm.LauncherActivityInfo> r24, java.util.Set<java.lang.String> r25) throws java.lang.Throwable {
        /*
            r22 = this;
            r8 = r22
            r0 = r23
            java.lang.String r1 = "system_state"
            java.lang.String r2 = "version"
            java.lang.String r3 = "lastUpdated"
            java.lang.String r4 = "componentName"
            java.lang.String r5 = "rowid"
            com.android.launcher3.compat.UserManagerCompat r6 = r8.mUserManager
            long r6 = r6.getSerialNumberForUser(r0)
            android.content.Context r9 = r8.mContext
            android.content.pm.PackageManager r9 = r9.getPackageManager()
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            r11 = 8192(0x2000, float:1.148E-41)
            java.util.List r9 = r9.getInstalledPackages(r11)
            java.util.Iterator r9 = r9.iterator()
        L2a:
            boolean r11 = r9.hasNext()
            if (r11 == 0) goto L3c
            java.lang.Object r11 = r9.next()
            android.content.pm.PackageInfo r11 = (android.content.pm.PackageInfo) r11
            java.lang.String r12 = r11.packageName
            r10.put(r12, r11)
            goto L2a
        L3c:
            java.util.HashMap r9 = new java.util.HashMap
            r9.<init>()
            java.util.Iterator r11 = r24.iterator()
        L45:
            boolean r12 = r11.hasNext()
            if (r12 == 0) goto L59
            java.lang.Object r12 = r11.next()
            android.content.pm.LauncherActivityInfo r12 = (android.content.pm.LauncherActivityInfo) r12
            android.content.ComponentName r13 = r12.getComponentName()
            r9.put(r13, r12)
            goto L45
        L59:
            java.util.HashSet r11 = new java.util.HashSet
            r11.<init>()
            java.util.Stack r12 = new java.util.Stack
            r12.<init>()
            com.android.launcher3.icons.IconCache$IconDB r14 = r8.mIconDb     // Catch: java.lang.Throwable -> L138 android.database.sqlite.SQLiteException -> L13b
            java.lang.String[] r15 = new java.lang.String[]{r5, r4, r3, r2, r1}     // Catch: java.lang.Throwable -> L138 android.database.sqlite.SQLiteException -> L13b
            java.lang.String r13 = "profileId = ? "
            r16 = r12
            r12 = 1
            java.lang.String[] r12 = new java.lang.String[r12]     // Catch: android.database.sqlite.SQLiteException -> L134 java.lang.Throwable -> L138
            r17 = 0
            java.lang.String r18 = java.lang.Long.toString(r6)     // Catch: android.database.sqlite.SQLiteException -> L134 java.lang.Throwable -> L138
            r12[r17] = r18     // Catch: android.database.sqlite.SQLiteException -> L134 java.lang.Throwable -> L138
            android.database.Cursor r12 = r14.query(r15, r13, r12)     // Catch: android.database.sqlite.SQLiteException -> L134 java.lang.Throwable -> L138
            int r4 = r12.getColumnIndex(r4)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            int r3 = r12.getColumnIndex(r3)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            int r2 = r12.getColumnIndex(r2)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            int r13 = r12.getColumnIndex(r5)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            int r1 = r12.getColumnIndex(r1)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
        L90:
            boolean r14 = r12.moveToNext()     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            if (r14 == 0) goto L12b
            java.lang.String r14 = r12.getString(r4)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            android.content.ComponentName r14 = android.content.ComponentName.unflattenFromString(r14)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            java.lang.String r15 = r14.getPackageName()     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            java.lang.Object r15 = r10.get(r15)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            android.content.pm.PackageInfo r15 = (android.content.pm.PackageInfo) r15     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            if (r15 != 0) goto Lc9
            java.lang.String r15 = r14.getPackageName()     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            r17 = r4
            r4 = r25
            boolean r15 = r4.contains(r15)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            if (r15 != 0) goto Lc6
            r8.remove(r14, r0)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            int r14 = r12.getInt(r13)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            r11.add(r14)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
        Lc6:
            r4 = r17
            goto L90
        Lc9:
            r17 = r4
            android.content.pm.ApplicationInfo r4 = r15.applicationInfo     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            int r4 = r4.flags     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            r18 = 16777216(0x1000000, float:2.3509887E-38)
            r4 = r4 & r18
            if (r4 == 0) goto Ld6
            goto Lc6
        Ld6:
            long r18 = r12.getLong(r3)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            int r4 = r12.getInt(r2)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            java.lang.Object r20 = r9.remove(r14)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            r21 = r2
            r2 = r20
            android.content.pm.LauncherActivityInfo r2 = (android.content.pm.LauncherActivityInfo) r2     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            r20 = r3
            int r3 = r15.versionCode     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            if (r4 != r3) goto L107
            long r3 = r15.lastUpdateTime     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            int r3 = (r18 > r3 ? 1 : (r18 == r3 ? 0 : -1))
            if (r3 != 0) goto L107
            java.lang.String r3 = r12.getString(r1)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            com.android.launcher3.icons.IconProvider r4 = r8.mIconProvider     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            java.lang.String r15 = r15.packageName     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            java.lang.String r4 = r4.getIconSystemState(r15)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            boolean r3 = android.text.TextUtils.equals(r3, r4)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            if (r3 == 0) goto L107
            goto L121
        L107:
            if (r2 != 0) goto L11a
            r8.remove(r14, r0)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            int r2 = r12.getInt(r13)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            r11.add(r2)     // Catch: android.database.sqlite.SQLiteException -> L130 java.lang.Throwable -> L182
            r14 = r16
            goto L11f
        L11a:
            r14 = r16
            r14.add(r2)     // Catch: android.database.sqlite.SQLiteException -> L129 java.lang.Throwable -> L182
        L11f:
            r16 = r14
        L121:
            r4 = r17
            r3 = r20
            r2 = r21
            goto L90
        L129:
            r0 = move-exception
            goto L13e
        L12b:
            r14 = r16
            if (r12 == 0) goto L14a
            goto L147
        L130:
            r0 = move-exception
            r14 = r16
            goto L13e
        L134:
            r0 = move-exception
            r14 = r16
            goto L13d
        L138:
            r0 = move-exception
            r13 = 0
            goto L184
        L13b:
            r0 = move-exception
            r14 = r12
        L13d:
            r12 = 0
        L13e:
            java.lang.String r1 = "Launcher.IconCache"
            java.lang.String r2 = "Error reading icon cache"
            android.util.Log.d(r1, r2, r0)     // Catch: java.lang.Throwable -> L182
            if (r12 == 0) goto L14a
        L147:
            r12.close()
        L14a:
            boolean r0 = r11.isEmpty()
            if (r0 != 0) goto L15a
            com.android.launcher3.icons.IconCache$IconDB r0 = r8.mIconDb
            java.lang.String r1 = com.android.launcher3.Utilities.createDbSelectionQuery(r5, r11)
            r2 = 0
            r0.delete(r1, r2)
        L15a:
            boolean r0 = r9.isEmpty()
            if (r0 == 0) goto L166
            boolean r0 = r14.isEmpty()
            if (r0 != 0) goto L181
        L166:
            java.util.Stack r0 = new java.util.Stack
            r0.<init>()
            java.util.Collection r1 = r9.values()
            r0.addAll(r1)
            com.android.launcher3.icons.IconCache$SerializedIconUpdateTask r9 = new com.android.launcher3.icons.IconCache$SerializedIconUpdateTask
            r1 = r9
            r2 = r22
            r3 = r6
            r5 = r10
            r6 = r0
            r7 = r14
            r1.<init>(r3, r5, r6, r7)
            r9.scheduleNext()
        L181:
            return
        L182:
            r0 = move-exception
            r13 = r12
        L184:
            if (r13 == 0) goto L189
            r13.close()
        L189:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconCache.updateDBIcons(android.os.UserHandle, java.util.List, java.util.Set):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x00b2 A[Catch: all -> 0x00f9, TryCatch #1 {, blocks: (B:3:0x0001, B:5:0x0011, B:7:0x001b, B:9:0x001f, B:13:0x0026, B:14:0x002b, B:17:0x0041, B:19:0x004a, B:21:0x004e, B:23:0x0056, B:26:0x0076, B:24:0x006d, B:25:0x0072, B:16:0x003a, B:27:0x0092, B:29:0x00b2, B:30:0x00ce), top: B:38:0x0001, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    synchronized void addIconToDBAndMemCache(android.content.pm.LauncherActivityInfo r10, android.content.pm.PackageInfo r11, long r12, boolean r14) {
        /*
            r9 = this;
            monitor-enter(r9)
            com.android.launcher3.util.ComponentKey r0 = new com.android.launcher3.util.ComponentKey     // Catch: java.lang.Throwable -> Lf9
            android.content.ComponentName r1 = r10.getComponentName()     // Catch: java.lang.Throwable -> Lf9
            android.os.UserHandle r2 = r10.getUser()     // Catch: java.lang.Throwable -> Lf9
            r0.<init>(r1, r2)     // Catch: java.lang.Throwable -> Lf9
            r1 = 0
            if (r14 != 0) goto L23
            java.util.HashMap<com.android.launcher3.util.ComponentKey, com.android.launcher3.icons.IconCache$CacheEntry> r14 = r9.mCache     // Catch: java.lang.Throwable -> Lf9
            java.lang.Object r14 = r14.get(r0)     // Catch: java.lang.Throwable -> Lf9
            com.android.launcher3.icons.IconCache$CacheEntry r14 = (com.android.launcher3.icons.IconCache.CacheEntry) r14     // Catch: java.lang.Throwable -> Lf9
            if (r14 == 0) goto L23
            boolean r2 = r14.isLowResIcon     // Catch: java.lang.Throwable -> Lf9
            if (r2 != 0) goto L23
            android.graphics.Bitmap r2 = r14.icon     // Catch: java.lang.Throwable -> Lf9
            if (r2 != 0) goto L24
        L23:
            r14 = r1
        L24:
            if (r14 != 0) goto L92
            com.android.launcher3.icons.IconCache$CacheEntry r14 = new com.android.launcher3.icons.IconCache$CacheEntry     // Catch: java.lang.Throwable -> Lf9
            r14.<init>()     // Catch: java.lang.Throwable -> Lf9
            android.content.Context r2 = r9.mContext     // Catch: java.lang.Exception -> L3a java.lang.Throwable -> Lf9
            android.content.pm.PackageManager r2 = r2.getPackageManager()     // Catch: java.lang.Exception -> L3a java.lang.Throwable -> Lf9
            android.content.pm.ApplicationInfo r3 = r11.applicationInfo     // Catch: java.lang.Exception -> L3a java.lang.Throwable -> Lf9
            java.lang.String r3 = r3.packageName     // Catch: java.lang.Exception -> L3a java.lang.Throwable -> Lf9
            android.graphics.drawable.Drawable r1 = r2.getApplicationIcon(r3)     // Catch: java.lang.Exception -> L3a java.lang.Throwable -> Lf9
            goto L41
        L3a:
            java.lang.String r2 = "Launcher.IconCache"
            java.lang.String r3 = "addIconToDBAndMemCache : drawable is null."
            com.lge.launcher3.util.LGLog.i(r2, r3)     // Catch: java.lang.Throwable -> Lf9
        L41:
            android.content.Context r2 = r9.mContext     // Catch: java.lang.Throwable -> Lf9
            boolean r2 = com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences.getThemedIconEnabled(r2)     // Catch: java.lang.Throwable -> Lf9
            r3 = 0
            if (r2 == 0) goto L72
            boolean r2 = r1 instanceof android.graphics.drawable.AdaptiveIconDrawable     // Catch: java.lang.Throwable -> Lf9
            if (r2 == 0) goto L72
            android.graphics.drawable.AdaptiveIconDrawable r1 = (android.graphics.drawable.AdaptiveIconDrawable) r1     // Catch: java.lang.Throwable -> Lf9
            android.graphics.drawable.Drawable r1 = r1.getMonochrome()     // Catch: java.lang.Throwable -> Lf9
            if (r1 == 0) goto L6d
            com.android.launcher3.icons.IconCache$ClippedMonoDrawable r2 = new com.android.launcher3.icons.IconCache$ClippedMonoDrawable     // Catch: java.lang.Throwable -> Lf9
            r2.<init>(r1)     // Catch: java.lang.Throwable -> Lf9
            android.content.Context r1 = r9.mContext     // Catch: java.lang.Throwable -> Lf9
            android.content.res.Resources r1 = r1.getResources()     // Catch: java.lang.Throwable -> Lf9
            r3 = 2131099969(0x7f060141, float:1.7812306E38)
            int r1 = r1.getColor(r3)     // Catch: java.lang.Throwable -> Lf9
            r2.setTint(r1)     // Catch: java.lang.Throwable -> Lf9
            r3 = 1
            goto L76
        L6d:
            android.graphics.drawable.Drawable r2 = r9.getFullResIcon(r10)     // Catch: java.lang.Throwable -> Lf9
            goto L76
        L72:
            android.graphics.drawable.Drawable r2 = r9.getFullResIcon(r10)     // Catch: java.lang.Throwable -> Lf9
        L76:
            android.content.Context r1 = r9.mContext     // Catch: java.lang.Throwable -> Lf9
            android.content.pm.ApplicationInfo r4 = r11.applicationInfo     // Catch: java.lang.Throwable -> Lf9
            java.lang.String r4 = r4.packageName     // Catch: java.lang.Throwable -> Lf9
            android.graphics.drawable.Drawable r1 = getShadowIconIfNeeded(r1, r2, r4, r3)     // Catch: java.lang.Throwable -> Lf9
            android.os.UserHandle r2 = r10.getUser()     // Catch: java.lang.Throwable -> Lf9
            android.content.Context r3 = r9.mContext     // Catch: java.lang.Throwable -> Lf9
            android.content.pm.ApplicationInfo r4 = r10.getApplicationInfo()     // Catch: java.lang.Throwable -> Lf9
            int r4 = r4.targetSdkVersion     // Catch: java.lang.Throwable -> Lf9
            android.graphics.Bitmap r1 = com.android.launcher3.graphics.LauncherIcons.createBadgedIconBitmap(r1, r2, r3, r4)     // Catch: java.lang.Throwable -> Lf9
            r14.icon = r1     // Catch: java.lang.Throwable -> Lf9
        L92:
            java.lang.CharSequence r1 = r10.getLabel()     // Catch: java.lang.Throwable -> Lf9
            r14.title = r1     // Catch: java.lang.Throwable -> Lf9
            com.android.launcher3.compat.UserManagerCompat r1 = r9.mUserManager     // Catch: java.lang.Throwable -> Lf9
            java.lang.CharSequence r2 = r14.title     // Catch: java.lang.Throwable -> Lf9
            android.os.UserHandle r3 = r10.getUser()     // Catch: java.lang.Throwable -> Lf9
            java.lang.CharSequence r1 = r1.getBadgedLabelForUser(r2, r3)     // Catch: java.lang.Throwable -> Lf9
            r14.contentDescription = r1     // Catch: java.lang.Throwable -> Lf9
            java.util.HashMap<android.content.ComponentName, com.android.launcher3.icons.IconCache$CustomAppIconEntry> r1 = r9.mCustomAppIconList     // Catch: java.lang.Throwable -> Lf9
            android.content.ComponentName r2 = r10.getComponentName()     // Catch: java.lang.Throwable -> Lf9
            boolean r1 = r1.containsKey(r2)     // Catch: java.lang.Throwable -> Lf9
            if (r1 == 0) goto Lce
            java.lang.CharSequence r1 = r14.title     // Catch: java.lang.Throwable -> Lf9
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> Lf9
            android.content.ComponentName r2 = r10.getComponentName()     // Catch: java.lang.Throwable -> Lf9
            java.lang.String r1 = r9.loadCustomLabel(r1, r2)     // Catch: java.lang.Throwable -> Lf9
            r14.title = r1     // Catch: java.lang.Throwable -> Lf9
            android.graphics.Bitmap r1 = r14.icon     // Catch: java.lang.Throwable -> Lf9
            android.content.ComponentName r2 = r10.getComponentName()     // Catch: java.lang.Throwable -> Lf9
            android.graphics.Bitmap r1 = r9.getFullResCustomIcon(r1, r2)     // Catch: java.lang.Throwable -> Lf9
            r14.icon = r1     // Catch: java.lang.Throwable -> Lf9
        Lce:
            java.util.HashMap<com.android.launcher3.util.ComponentKey, com.android.launcher3.icons.IconCache$CacheEntry> r1 = r9.mCache     // Catch: java.lang.Throwable -> Lf9
            r1.put(r0, r14)     // Catch: java.lang.Throwable -> Lf9
            android.graphics.Bitmap r0 = r14.icon     // Catch: java.lang.Throwable -> Lf9
            int r1 = r9.mActivityBgColor     // Catch: java.lang.Throwable -> Lf9
            android.graphics.Bitmap r0 = r9.generateLowResIcon(r0, r1)     // Catch: java.lang.Throwable -> Lf9
            android.graphics.Bitmap r1 = r14.icon     // Catch: java.lang.Throwable -> Lf9
            java.lang.CharSequence r14 = r14.title     // Catch: java.lang.Throwable -> Lf9
            java.lang.String r14 = r14.toString()     // Catch: java.lang.Throwable -> Lf9
            android.content.pm.ApplicationInfo r2 = r10.getApplicationInfo()     // Catch: java.lang.Throwable -> Lf9
            java.lang.String r2 = r2.packageName     // Catch: java.lang.Throwable -> Lf9
            android.content.ContentValues r4 = r9.newContentValues(r1, r0, r14, r2)     // Catch: java.lang.Throwable -> Lf9
            android.content.ComponentName r5 = r10.getComponentName()     // Catch: java.lang.Throwable -> Lf9
            r3 = r9
            r6 = r11
            r7 = r12
            r3.addIconToDB(r4, r5, r6, r7)     // Catch: java.lang.Throwable -> Lf9
            monitor-exit(r9)
            return
        Lf9:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconCache.addIconToDBAndMemCache(android.content.pm.LauncherActivityInfo, android.content.pm.PackageInfo, long, boolean):void");
    }

    private void addIconToDB(ContentValues values, ComponentName key, PackageInfo info, long userSerial) {
        if (LiveIconManager.getInstance(this.mContext).hasLiveIcon(key)) {
            return;
        }
        values.put("componentName", key.flattenToString());
        values.put("profileId", Long.valueOf(userSerial));
        values.put(BaseIconCache.IconDB.COLUMN_LAST_UPDATED, Long.valueOf(info.lastUpdateTime));
        values.put(BaseIconCache.IconDB.COLUMN_VERSION, Integer.valueOf(info.versionCode));
        try {
            this.mIconDb.insertOrReplace(values);
        } catch (SQLiteException e) {
            if (e.getMessage().equals("unable to open database file")) {
                Log.e("SQLiteException", "MemoryFull:" + e.toString());
                return;
            }
            Log.e("SQLiteException", e.toString());
        }
    }

    public IconLoadRequest updateIconInBackground(final ItemInfoUpdateReceiver caller, final ItemInfoWithIcon info) {
        Preconditions.assertUIThread();
        if (this.mPendingIconRequestCount <= 0) {
            LauncherModel.setWorkerPriority(-2);
        }
        this.mPendingIconRequestCount++;
        AnonymousClass1 anonymousClass1 = new AnonymousClass1(this.mWorkerHandler, new Runnable() { // from class: com.android.launcher3.icons.-$$Lambda$IconCache$pZ_BB-NATpnSTVCuNmuPMkIR3Ug
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.onIconRequestEnd();
            }
        }, info, caller);
        Utilities.postAsyncCallback(this.mWorkerHandler, anonymousClass1);
        return anonymousClass1;
    }

    /* JADX INFO: renamed from: com.android.launcher3.icons.IconCache$1, reason: invalid class name */
    class AnonymousClass1 extends IconLoadRequest {
        final /* synthetic */ ItemInfoUpdateReceiver val$caller;
        final /* synthetic */ ItemInfoWithIcon val$info;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(Handler handler, Runnable endRunnable, final ItemInfoWithIcon val$info, final ItemInfoUpdateReceiver val$caller) {
            super(handler, endRunnable);
            this.val$info = val$info;
            this.val$caller = val$caller;
        }

        @Override // java.lang.Runnable
        public void run() {
            ItemInfoWithIcon itemInfoWithIcon = this.val$info;
            if ((itemInfoWithIcon instanceof AppInfo) || (itemInfoWithIcon instanceof com.android.launcher3.ShortcutInfo)) {
                IconCache.this.getTitleAndIcon(itemInfoWithIcon, false);
            } else if (itemInfoWithIcon instanceof PackageItemInfo) {
                IconCache.this.getTitleAndIconForApp((PackageItemInfo) itemInfoWithIcon, false);
            }
            MainThreadExecutor mainThreadExecutor = IconCache.this.mMainThreadExecutor;
            final ItemInfoUpdateReceiver itemInfoUpdateReceiver = this.val$caller;
            final ItemInfoWithIcon itemInfoWithIcon2 = this.val$info;
            mainThreadExecutor.execute(new Runnable() { // from class: com.android.launcher3.icons.-$$Lambda$IconCache$1$diocUAVl-fy8H5k9ycy8yKBV8Pk
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$run$0$IconCache$1(itemInfoUpdateReceiver, itemInfoWithIcon2);
                }
            });
        }

        public /* synthetic */ void lambda$run$0$IconCache$1(ItemInfoUpdateReceiver itemInfoUpdateReceiver, ItemInfoWithIcon itemInfoWithIcon) {
            itemInfoUpdateReceiver.reapplyItemInfo(itemInfoWithIcon);
            onEnd();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onIconRequestEnd() {
        int i = this.mPendingIconRequestCount - 1;
        this.mPendingIconRequestCount = i;
        if (i <= 0) {
            LauncherModel.setWorkerPriority(10);
        }
    }

    private Bitmap getNonNullIcon(CacheEntry entry, UserHandle user) {
        return entry.icon == null ? getDefaultIcon(user) : entry.icon;
    }

    public synchronized void updateTitleAndIcon(AppInfo application) {
        CacheEntry cacheEntryCacheLocked = cacheLocked(application.componentName, Provider.of(null), application.user, false, application.usingLowResIcon);
        if (cacheEntryCacheLocked.icon != null && !isDefaultIcon(cacheEntryCacheLocked.icon, application.user)) {
            applyCacheEntry(cacheEntryCacheLocked, application);
        }
    }

    public synchronized Bitmap getIcon(Intent intent, UserHandle user) {
        ComponentName component = intent.getComponent();
        if (component == null) {
            return getDefaultIcon(user);
        }
        return cacheLocked(component, Provider.of(this.mLauncherApps.resolveActivity(intent, user)), user, true, false).icon;
    }

    public synchronized void getTitleAndIcon(ItemInfoWithIcon info, LauncherActivityInfo activityInfo, boolean useLowResIcon) {
        getTitleAndIcon(info, Provider.of(activityInfo), false, useLowResIcon);
    }

    public synchronized void getTitleAndIcon(ItemInfoWithIcon info, boolean useLowResIcon) {
        if (info.getTargetComponent() == null) {
            info.iconBitmap = getDefaultIcon(info.user);
            info.title = "";
            info.contentDescription = "";
            info.usingLowResIcon = false;
        } else {
            getTitleAndIcon(info, (Provider<LauncherActivityInfo>) new ActivityInfoProvider(info.getIntent(), info.user), true, useLowResIcon);
        }
    }

    public synchronized void getTitleAndIcon(com.android.launcher3.ShortcutInfo shortcutInfo, Intent intent, UserHandle user, boolean useLowResIcon) {
        ComponentName component = intent.getComponent();
        if (component == null) {
            shortcutInfo.setIcon(getDefaultIcon(user));
            shortcutInfo.title = "";
            shortcutInfo.usingFallbackIcon = true;
            shortcutInfo.usingLowResIcon = false;
        } else {
            getTitleAndIcon(shortcutInfo, component, this.mLauncherApps.resolveActivity(intent, user), user, true, useLowResIcon);
        }
    }

    public synchronized void getTitleAndIcon(com.android.launcher3.ShortcutInfo shortcutInfo, ComponentName component, LauncherActivityInfo info, UserHandle user, boolean usePkgIcon, boolean useLowResIcon) {
        CacheEntry cacheEntryCacheLocked = cacheLocked(component, Provider.of(info), user, usePkgIcon, useLowResIcon);
        shortcutInfo.usingFallbackIcon = isDefaultIcon(cacheEntryCacheLocked.icon, user);
        applyCacheEntry(cacheEntryCacheLocked, shortcutInfo);
    }

    private synchronized void getTitleAndIcon(ItemInfoWithIcon infoInOut, Provider<LauncherActivityInfo> activityInfoProvider, boolean usePkgIcon, boolean useLowResIcon) {
        applyCacheEntry(cacheLocked(infoInOut.getTargetComponent(), activityInfoProvider, infoInOut.user, usePkgIcon, useLowResIcon), infoInOut);
    }

    public synchronized void getTitleAndIconForApp(String packageName, UserHandle user, boolean useLowResIcon, PackageItemInfo infoOut) {
        CacheEntry entryForPackageLocked = getEntryForPackageLocked(packageName, user, useLowResIcon);
        infoOut.iconBitmap = getNonNullIcon(entryForPackageLocked, user);
        infoOut.title = Utilities.trim(entryForPackageLocked.title);
        infoOut.usingLowResIcon = entryForPackageLocked.isLowResIcon;
        infoOut.contentDescription = entryForPackageLocked.contentDescription;
    }

    public synchronized void getTitleAndIconForApp(PackageItemInfo infoInOut, boolean useLowResIcon) {
        applyCacheEntry(getEntryForPackageLocked(infoInOut.packageName, infoInOut.user, useLowResIcon), infoInOut);
    }

    private void applyCacheEntry(CacheEntry entry, ItemInfoWithIcon info) {
        info.title = Utilities.trim(entry.title);
        info.contentDescription = entry.contentDescription;
        info.iconBitmap = entry.icon == null ? getDefaultIcon(info.user) : entry.icon;
        info.usingLowResIcon = entry.isLowResIcon;
    }

    public synchronized Bitmap getDefaultIcon(UserHandle user) {
        if (!this.mDefaultIcons.containsKey(user)) {
            this.mDefaultIcons.put(user, makeDefaultIcon(user));
        }
        return this.mDefaultIcons.get(user);
    }

    public boolean isDefaultIcon(Bitmap icon, UserHandle user) {
        return this.mDefaultIcons.get(user) == icon;
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x007e  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00a9  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected com.android.launcher3.icons.IconCache.CacheEntry cacheLocked(android.content.ComponentName r7, com.android.launcher3.util.Provider<android.content.pm.LauncherActivityInfo> r8, android.os.UserHandle r9, boolean r10, boolean r11) throws java.lang.Throwable {
        /*
            r6 = this;
            com.android.launcher3.util.ComponentKey r0 = new com.android.launcher3.util.ComponentKey
            r0.<init>(r7, r9)
            java.util.HashMap<com.android.launcher3.util.ComponentKey, com.android.launcher3.icons.IconCache$CacheEntry> r1 = r6.mCache
            java.lang.Object r1 = r1.get(r0)
            com.android.launcher3.icons.IconCache$CacheEntry r1 = (com.android.launcher3.icons.IconCache.CacheEntry) r1
            if (r1 == 0) goto L15
            boolean r2 = r1.isLowResIcon
            if (r2 == 0) goto L10d
            if (r11 != 0) goto L10d
        L15:
            com.android.launcher3.icons.IconCache$CacheEntry r1 = new com.android.launcher3.icons.IconCache$CacheEntry
            r1.<init>()
            java.util.HashMap<com.android.launcher3.util.ComponentKey, com.android.launcher3.icons.IconCache$CacheEntry> r2 = r6.mCache
            r2.put(r0, r1)
            boolean r11 = r6.getEntryFromDB(r0, r1, r11)
            r0 = 1
            r2 = 0
            r3 = 0
            if (r11 == 0) goto L2b
            r0 = r3
            goto Lcc
        L2b:
            java.lang.Object r11 = r8.get()
            android.content.pm.LauncherActivityInfo r11 = (android.content.pm.LauncherActivityInfo) r11
            android.content.Context r4 = r6.mContext     // Catch: java.lang.Exception -> L40
            android.content.pm.PackageManager r4 = r4.getPackageManager()     // Catch: java.lang.Exception -> L40
            java.lang.String r5 = r7.getPackageName()     // Catch: java.lang.Exception -> L40
            android.graphics.drawable.Drawable r2 = r4.getApplicationIcon(r5)     // Catch: java.lang.Exception -> L40
            goto L47
        L40:
            java.lang.String r4 = "Launcher.IconCache"
            java.lang.String r5 = "cacheLocked : drawable is null."
            com.lge.launcher3.util.LGLog.i(r4, r5)
        L47:
            android.content.Context r4 = r6.mContext
            boolean r4 = com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences.getThemedIconEnabled(r4)
            if (r4 == 0) goto L77
            boolean r4 = r2 instanceof android.graphics.drawable.AdaptiveIconDrawable
            if (r4 == 0) goto L77
            android.graphics.drawable.AdaptiveIconDrawable r2 = (android.graphics.drawable.AdaptiveIconDrawable) r2
            android.graphics.drawable.Drawable r2 = r2.getMonochrome()
            if (r2 == 0) goto L72
            com.android.launcher3.icons.IconCache$ClippedMonoDrawable r4 = new com.android.launcher3.icons.IconCache$ClippedMonoDrawable
            r4.<init>(r2)
            android.content.Context r2 = r6.mContext
            android.content.res.Resources r2 = r2.getResources()
            r5 = 2131099969(0x7f060141, float:1.7812306E38)
            int r2 = r2.getColor(r5)
            r4.setTint(r2)
            r2 = r0
            goto L7c
        L72:
            android.graphics.drawable.Drawable r4 = r6.getFullResIcon(r11)
            goto L7b
        L77:
            android.graphics.drawable.Drawable r4 = r6.getFullResIcon(r11)
        L7b:
            r2 = r3
        L7c:
            if (r11 == 0) goto La9
            android.content.Context r10 = r6.mContext
            java.lang.Object r3 = r8.get()
            android.content.pm.LauncherActivityInfo r3 = (android.content.pm.LauncherActivityInfo) r3
            android.content.pm.ApplicationInfo r3 = r3.getApplicationInfo()
            java.lang.String r3 = r3.packageName
            android.graphics.drawable.Drawable r10 = getShadowIconIfNeeded(r10, r4, r3, r2)
            android.os.UserHandle r2 = r11.getUser()
            android.content.Context r3 = r6.mContext
            java.lang.Object r4 = r8.get()
            android.content.pm.LauncherActivityInfo r4 = (android.content.pm.LauncherActivityInfo) r4
            android.content.pm.ApplicationInfo r4 = r4.getApplicationInfo()
            int r4 = r4.targetSdkVersion
            android.graphics.Bitmap r10 = com.android.launcher3.graphics.LauncherIcons.createBadgedIconBitmap(r10, r2, r3, r4)
            r1.icon = r10
            goto Lcb
        La9:
            if (r10 == 0) goto Lc1
            java.lang.String r10 = r7.getPackageName()
            com.android.launcher3.icons.IconCache$CacheEntry r10 = r6.getEntryForPackageLocked(r10, r9, r3)
            if (r10 == 0) goto Lc1
            android.graphics.Bitmap r2 = r10.icon
            r1.icon = r2
            java.lang.CharSequence r2 = r10.title
            r1.title = r2
            java.lang.CharSequence r10 = r10.contentDescription
            r1.contentDescription = r10
        Lc1:
            android.graphics.Bitmap r10 = r1.icon
            if (r10 != 0) goto Lcb
            android.graphics.Bitmap r10 = r6.getDefaultIcon(r9)
            r1.icon = r10
        Lcb:
            r2 = r11
        Lcc:
            java.lang.CharSequence r10 = r1.title
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 == 0) goto Lf1
            if (r2 != 0) goto Ldf
            if (r0 != 0) goto Ldf
            java.lang.Object r8 = r8.get()
            r2 = r8
            android.content.pm.LauncherActivityInfo r2 = (android.content.pm.LauncherActivityInfo) r2
        Ldf:
            if (r2 == 0) goto Lf1
            java.lang.CharSequence r8 = r2.getLabel()
            r1.title = r8
            com.android.launcher3.compat.UserManagerCompat r8 = r6.mUserManager
            java.lang.CharSequence r10 = r1.title
            java.lang.CharSequence r8 = r8.getBadgedLabelForUser(r10, r9)
            r1.contentDescription = r8
        Lf1:
            java.util.HashMap<android.content.ComponentName, com.android.launcher3.icons.IconCache$CustomAppIconEntry> r8 = r6.mCustomAppIconList
            boolean r8 = r8.containsKey(r7)
            if (r8 == 0) goto L10d
            java.lang.CharSequence r8 = r1.title
            java.lang.String r8 = r8.toString()
            java.lang.String r8 = r6.loadCustomLabel(r8, r7)
            r1.title = r8
            android.graphics.Bitmap r8 = r1.icon
            android.graphics.Bitmap r7 = r6.getFullResCustomIcon(r8, r7)
            r1.icon = r7
        L10d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconCache.cacheLocked(android.content.ComponentName, com.android.launcher3.util.Provider, android.os.UserHandle, boolean, boolean):com.android.launcher3.icons.IconCache$CacheEntry");
    }

    public synchronized void clear() {
        Preconditions.assertWorkerThread();
        this.mIconDb.clear();
    }

    public synchronized void cachePackageInstallInfo(String packageName, UserHandle user, Bitmap icon, CharSequence title) {
        removeFromMemCacheLocked(packageName, user);
        ComponentKey packageKey = getPackageKey(packageName, user);
        CacheEntry cacheEntry = this.mCache.get(packageKey);
        if (cacheEntry == null) {
            cacheEntry = new CacheEntry();
            this.mCache.put(packageKey, cacheEntry);
        }
        if (!TextUtils.isEmpty(title)) {
            cacheEntry.title = title;
        }
        if (icon != null) {
            cacheEntry.icon = com.android.launcher3.graphics.LauncherIcons.createIconBitmap(icon, this.mContext);
        }
    }

    private static ComponentKey getPackageKey(String packageName, UserHandle user) {
        return new ComponentKey(new ComponentName(packageName, packageName + "."), user);
    }

    private CacheEntry getEntryForPackageLocked(String packageName, UserHandle user, boolean useLowResIcon) {
        ComponentKey packageKey = getPackageKey(packageName, user);
        CacheEntry cacheEntry = this.mCache.get(packageKey);
        if (cacheEntry == null || (cacheEntry.isLowResIcon && !useLowResIcon)) {
            cacheEntry = new CacheEntry();
            boolean z = true;
            if (!getEntryFromDB(packageKey, cacheEntry, useLowResIcon)) {
                try {
                    PackageInfo packageInfo = this.mPackageManager.getPackageInfo(packageName, Process.myUserHandle().equals(user) ? 0 : 8192);
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    if (applicationInfo == null) {
                        throw new PackageManager.NameNotFoundException("ApplicationInfo is null");
                    }
                    Bitmap bitmapCreateBadgedIconBitmap = com.android.launcher3.graphics.LauncherIcons.createBadgedIconBitmap(applicationInfo.loadIcon(this.mPackageManager), user, this.mContext, applicationInfo.targetSdkVersion);
                    Bitmap bitmapGenerateLowResIcon = generateLowResIcon(bitmapCreateBadgedIconBitmap, this.mPackageBgColor);
                    cacheEntry.title = applicationInfo.loadLabel(this.mPackageManager);
                    cacheEntry.contentDescription = this.mUserManager.getBadgedLabelForUser(cacheEntry.title, user);
                    cacheEntry.icon = useLowResIcon ? bitmapGenerateLowResIcon : bitmapCreateBadgedIconBitmap;
                    cacheEntry.isLowResIcon = useLowResIcon;
                    addIconToDB(newContentValues(bitmapCreateBadgedIconBitmap, bitmapGenerateLowResIcon, cacheEntry.title.toString(), packageName), packageKey.componentName, packageInfo, this.mUserManager.getSerialNumberForUser(user));
                } catch (PackageManager.NameNotFoundException unused) {
                    z = false;
                }
            }
            if (z) {
                this.mCache.put(packageKey, cacheEntry);
            }
        }
        return cacheEntry;
    }

    public void preloadIcon(ComponentName componentName, Bitmap icon, int dpi, String label, long userSerial) {
        try {
            this.mContext.getPackageManager().getActivityIcon(componentName);
        } catch (PackageManager.NameNotFoundException unused) {
            ContentValues contentValuesNewContentValues = newContentValues(icon, label, 0);
            contentValuesNewContentValues.put("componentName", componentName.flattenToString());
            contentValuesNewContentValues.put("profileId", Long.valueOf(userSerial));
            try {
                this.mIconDb.insertOrReplace(contentValuesNewContentValues);
            } catch (SQLiteException e) {
                if (e.getMessage().equals("unable to open database file")) {
                    Log.e("SQLiteException", "MemoryFull:" + e.toString());
                    return;
                }
                Log.e("SQLiteException", e.toString());
            }
        }
    }

    private boolean getEntryFromDB(ComponentKey componentKey, CacheEntry cacheEntry, boolean z) throws Throwable {
        Cursor cursorQuery;
        Cursor cursor = null;
        cursor = null;
        cursor = null;
        cursor = null;
        cursor = null;
        try {
            try {
                IconDB iconDB = this.mIconDb;
                String[] strArr = new String[2];
                strArr[0] = z ? "icon_low_res" : "icon";
                strArr[1] = BaseIconCache.IconDB.COLUMN_LABEL;
                cursorQuery = iconDB.query(strArr, "componentName = ? AND profileId = ?", new String[]{componentKey.componentName.flattenToString(), Long.toString(this.mUserManager.getSerialNumberForUser(componentKey.user))});
                try {
                } catch (CursorWindowAllocationException e) {
                    e = e;
                    cursor = cursorQuery;
                    Log.d(TAG, "Error reading icon cache", e);
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (SQLiteException e2) {
                    e = e2;
                    cursor = cursorQuery;
                    Log.d(TAG, "Error reading icon cache", e);
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            } catch (CursorWindowAllocationException e3) {
                e = e3;
            } catch (SQLiteException e4) {
                e = e4;
            }
            if (!cursorQuery.moveToNext()) {
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                return false;
            }
            cacheEntry.icon = loadIconNoResize(cursorQuery, 0, z ? this.mLowResOptions : null);
            cacheEntry.isLowResIcon = z;
            cacheEntry.title = cursorQuery.getString(1);
            if (cacheEntry.title == null) {
                cacheEntry.title = "";
                cacheEntry.contentDescription = "";
            } else {
                cacheEntry.contentDescription = this.mUserManager.getBadgedLabelForUser(cacheEntry.title, componentKey.user);
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return true;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static abstract class IconLoadRequest implements Runnable {
        private final Runnable mEndRunnable;
        private boolean mEnded = false;
        private final Handler mHandler;

        IconLoadRequest(Handler handler, Runnable endRunnable) {
            this.mHandler = handler;
            this.mEndRunnable = endRunnable;
        }

        public void cancel() {
            this.mHandler.removeCallbacks(this);
            onEnd();
        }

        public void onEnd() {
            if (this.mEnded) {
                return;
            }
            this.mEnded = true;
            this.mEndRunnable.run();
        }
    }

    class SerializedIconUpdateTask implements Runnable {
        private final Stack<LauncherActivityInfo> mAppsToAdd;
        private final Stack<LauncherActivityInfo> mAppsToUpdate;
        private final HashMap<String, PackageInfo> mPkgInfoMap;
        private final HashSet<String> mUpdatedPackages = new HashSet<>();
        private final long mUserSerial;

        SerializedIconUpdateTask(long userSerial, HashMap<String, PackageInfo> pkgInfoMap, Stack<LauncherActivityInfo> appsToAdd, Stack<LauncherActivityInfo> appsToUpdate) {
            this.mUserSerial = userSerial;
            this.mPkgInfoMap = pkgInfoMap;
            this.mAppsToAdd = appsToAdd;
            this.mAppsToUpdate = appsToUpdate;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!this.mAppsToUpdate.isEmpty()) {
                LauncherActivityInfo launcherActivityInfoPop = this.mAppsToUpdate.pop();
                String packageName = launcherActivityInfoPop.getComponentName().getPackageName();
                IconCache.this.addIconToDBAndMemCache(launcherActivityInfoPop, this.mPkgInfoMap.get(packageName), this.mUserSerial, true);
                this.mUpdatedPackages.add(packageName);
                if (this.mAppsToUpdate.isEmpty() && !this.mUpdatedPackages.isEmpty()) {
                    LauncherAppState.getInstance(IconCache.this.mContext).getModel().onPackageIconsUpdated(this.mUpdatedPackages, IconCache.this.mUserManager.getUserForSerialNumber(this.mUserSerial));
                }
                scheduleNext();
                return;
            }
            if (this.mAppsToAdd.isEmpty()) {
                return;
            }
            LauncherActivityInfo launcherActivityInfoPop2 = this.mAppsToAdd.pop();
            PackageInfo packageInfo = this.mPkgInfoMap.get(launcherActivityInfoPop2.getComponentName().getPackageName());
            if (packageInfo != null) {
                IconCache.this.addIconToDBAndMemCache(launcherActivityInfoPop2, packageInfo, this.mUserSerial, false);
                if (IconCache.this.mIsReloadIcon) {
                    this.mUpdatedPackages.add(launcherActivityInfoPop2.getComponentName().getPackageName());
                    if (this.mAppsToAdd.isEmpty() && !this.mUpdatedPackages.isEmpty()) {
                        LauncherAppState.getInstance(IconCache.this.mContext).getModel().onPackageIconsUpdated(this.mUpdatedPackages, IconCache.this.mUserManager.getUserForSerialNumber(this.mUserSerial));
                        IconCache.this.mIsReloadIcon = false;
                    }
                }
            }
            if (this.mAppsToAdd.isEmpty()) {
                return;
            }
            scheduleNext();
        }

        public void scheduleNext() {
            IconCache.this.mWorkerHandler.postAtTime(this, IconCache.ICON_UPDATE_TOKEN, SystemClock.uptimeMillis() + 1);
        }
    }

    private static final class IconDB extends SQLiteCacheHelper {
        private static final String COLUMN_COMPONENT = "componentName";
        private static final String COLUMN_ICON = "icon";
        private static final String COLUMN_ICON_LOW_RES = "icon_low_res";
        private static final String COLUMN_LABEL = "label";
        private static final String COLUMN_LAST_UPDATED = "lastUpdated";
        private static final String COLUMN_ROWID = "rowid";
        private static final String COLUMN_SYSTEM_STATE = "system_state";
        private static final String COLUMN_USER = "profileId";
        private static final String COLUMN_VERSION = "version";
        private static final int DB_VERSION = 13;
        private static final int RELEASE_VERSION = (!FeatureFlags.LAUNCHER3_DISABLE_ICON_NORMALIZATION ? 1 : 0) + 13;
        private static final String TABLE_NAME = "icons";

        public IconDB(Context context, int iconPixelSize) {
            super(context, LauncherFiles.APP_ICONS_DB, (RELEASE_VERSION << 16) + iconPixelSize, "icons");
        }

        @Override // com.android.launcher3.util.SQLiteCacheHelper
        protected void onCreateTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS icons (componentName TEXT NOT NULL, profileId INTEGER NOT NULL, lastUpdated INTEGER NOT NULL DEFAULT 0, version INTEGER NOT NULL DEFAULT 0, icon BLOB, icon_low_res BLOB, label TEXT, system_state TEXT, PRIMARY KEY (componentName, profileId) );");
        }
    }

    private ContentValues newContentValues(Bitmap icon, Bitmap lowResIcon, String label, String packageName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("icon", Utilities.flattenBitmap(icon));
        contentValues.put("icon_low_res", Utilities.flattenBitmap(lowResIcon));
        contentValues.put(BaseIconCache.IconDB.COLUMN_LABEL, label);
        contentValues.put(BaseIconCache.IconDB.COLUMN_SYSTEM_STATE, this.mIconProvider.getIconSystemState(packageName));
        return contentValues;
    }

    private Bitmap generateLowResIcon(Bitmap icon, int lowResBackgroundColor) {
        if (lowResBackgroundColor == 0) {
            return Bitmap.createScaledBitmap(icon, icon.getWidth() / 5, icon.getHeight() / 5, true);
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(icon.getWidth() / 5, icon.getHeight() / 5, Bitmap.Config.RGB_565);
        synchronized (this) {
            this.mLowResCanvas.setBitmap(bitmapCreateBitmap);
            this.mLowResCanvas.drawColor(lowResBackgroundColor);
            this.mLowResCanvas.drawBitmap(icon, new Rect(0, 0, icon.getWidth(), icon.getHeight()), new Rect(0, 0, bitmapCreateBitmap.getWidth(), bitmapCreateBitmap.getHeight()), this.mLowResPaint);
            this.mLowResCanvas.setBitmap(null);
        }
        return bitmapCreateBitmap;
    }

    private ContentValues newContentValues(Bitmap icon, String label, int lowResBackgroundColor) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("icon", Utilities.flattenBitmap(icon));
        contentValues.put(BaseIconCache.IconDB.COLUMN_LABEL, label);
        contentValues.put(BaseIconCache.IconDB.COLUMN_SYSTEM_STATE, this.mIconProvider.getIconSystemState(null));
        if (lowResBackgroundColor == 0) {
            contentValues.put("icon_low_res", Utilities.flattenBitmap(Bitmap.createScaledBitmap(icon, icon.getWidth() / 5, icon.getHeight() / 5, true)));
        } else {
            synchronized (this) {
                if (this.mLowResBitmap == null) {
                    this.mLowResBitmap = Bitmap.createBitmap(icon.getWidth() / 5, icon.getHeight() / 5, Bitmap.Config.RGB_565);
                    this.mLowResCanvas = new Canvas(this.mLowResBitmap);
                    this.mLowResPaint = new Paint(3);
                }
                this.mLowResCanvas.drawColor(lowResBackgroundColor);
                this.mLowResCanvas.drawBitmap(icon, new Rect(0, 0, icon.getWidth(), icon.getHeight()), new Rect(0, 0, this.mLowResBitmap.getWidth(), this.mLowResBitmap.getHeight()), this.mLowResPaint);
                contentValues.put("icon_low_res", Utilities.flattenBitmap(this.mLowResBitmap));
            }
        }
        return contentValues;
    }

    private static Bitmap loadIconNoResize(Cursor c, int iconIndex, BitmapFactory.Options options) {
        byte[] blob = c.getBlob(iconIndex);
        try {
            return BitmapFactory.decodeByteArray(blob, 0, blob.length, options);
        } catch (Exception unused) {
            return null;
        }
    }

    private class ActivityInfoProvider extends Provider<LauncherActivityInfo> {
        private final Intent mIntent;
        private final UserHandle mUser;

        public ActivityInfoProvider(Intent intent, UserHandle user) {
            this.mIntent = intent;
            this.mUser = user;
        }

        /* JADX DEBUG: Method merged with bridge method: get()Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.launcher3.util.Provider
        public LauncherActivityInfo get() {
            return IconCache.this.mLauncherApps.resolveActivity(this.mIntent, this.mUser);
        }
    }

    public synchronized void clearIconDB() {
        LGLog.i(TAG, "clearIconDB()");
        this.mCache.clear();
        this.mIconDb.clear();
        RecentsModel.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mContext).getIconCache().clear();
    }

    public void updateInvariantDeviceProfile(InvariantDeviceProfile inv) {
        this.mIconDpi = inv.fillResIconDpi;
        clearIconDB();
    }

    public synchronized void removeIcon(ComponentName component, UserHandle user) {
        remove(component, user);
        this.mIconDb.delete("componentName = ? AND profileId = ?", new String[]{component.flattenToString(), Long.toString(this.mUserManager.getSerialNumberForUser(user))});
    }

    public synchronized void flush() {
        this.mCache.clear();
    }

    private static class CustomAppIconEntry {
        public int mIconResId;
        public int mTitleResId;
        public String mTitleValue;

        private CustomAppIconEntry() {
        }
    }

    public void initCustomAppIconList() {
        ComponentName componentNameUnflattenFromString;
        CustomAppIconEntry updateIconInfo;
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(LauncherConst.CUSTOMIZE_APPICONS_SHARED_PREF_NAME, 0);
        this.mCustomAppIconList.clear();
        for (String str : sharedPreferences.getAll().keySet()) {
            String string = sharedPreferences.getString(str, null);
            if (string != null && (updateIconInfo = getUpdateIconInfo((componentNameUnflattenFromString = ComponentName.unflattenFromString(str)), string)) != null) {
                this.mCustomAppIconList.put(componentNameUnflattenFromString, updateIconInfo);
            }
        }
    }

    private Resources getResources(String packageName) throws PackageManager.NameNotFoundException {
        return this.mPackageManager.getResourcesForApplication(packageName);
    }

    public String loadCustomLabel(String originaLabel, ComponentName componentName) {
        CustomAppIconEntry customAppIconEntry = this.mCustomAppIconList.get(componentName);
        if (customAppIconEntry == null) {
            return originaLabel;
        }
        if (customAppIconEntry.mTitleValue != null) {
            return customAppIconEntry.mTitleValue;
        }
        try {
            return getResources(componentName.getPackageName()).getString(customAppIconEntry.mTitleResId);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.w(TAG, "Exception loading custom label: " + e, new int[0]);
            return originaLabel;
        } catch (Resources.NotFoundException e2) {
            LGLog.w(TAG, "Exception loading custom label: " + e2, new int[0]);
            return originaLabel;
        }
    }

    public Bitmap getFullResCustomIcon(Bitmap originalIcon, ComponentName componentName) {
        Drawable fullResCustomIcon;
        LGLog.w(TAG, "getFullResCustomIcon : " + componentName, new int[0]);
        CustomAppIconEntry customAppIconEntry = this.mCustomAppIconList.get(componentName);
        return (customAppIconEntry == null || customAppIconEntry.mIconResId == 0 || (fullResCustomIcon = getFullResCustomIcon(componentName.getPackageName(), customAppIconEntry.mIconResId)) == null) ? originalIcon : Utilities.createIconBitmap(fullResCustomIcon, this.mContext);
    }

    public Drawable getFullResCustomIcon(String packageName, int iconId) {
        Resources resourcesForApplication;
        LGLog.d(TAG, "getFullResCustomIcon : packageName" + packageName + "/ iconId = " + iconId);
        Drawable fullResIcon = null;
        try {
            resourcesForApplication = this.mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "package not found " + e);
            resourcesForApplication = null;
        }
        if (resourcesForApplication != null && iconId != 0) {
            fullResIcon = getFullResIcon(resourcesForApplication, iconId);
        }
        if (fullResIcon != null) {
            return (!(iconId != 0) || DDTUtils.needToConvertCushinIcon(this.mContext, packageName, iconId)) ? DDTUtils.convertToCushionIcon(this.mContext, fullResIcon, packageName, iconId) : fullResIcon;
        }
        return getFullResDefaultActivityIcon();
    }

    public CustomAppIconEntry getUpdateIconInfo(ComponentName cn, String value) {
        try {
            JSONObject jSONObject = (JSONObject) new JSONTokener(value).nextValue();
            String string = jSONObject.getString(LauncherConst.KEY_ICON_RESOURCE_NAME);
            String string2 = jSONObject.getString(LauncherConst.KEY_TITLE_RESOURCE_NAME);
            CustomAppIconEntry customAppIconEntry = new CustomAppIconEntry();
            String packageName = cn.getPackageName();
            Resources resources = getResources(packageName);
            customAppIconEntry.mIconResId = resources.getIdentifier(string, LauncherConst.RESOURCE_IMAGE_TYPE, packageName);
            customAppIconEntry.mTitleResId = resources.getIdentifier(string2, "string", packageName);
            if (customAppIconEntry.mIconResId == 0 && customAppIconEntry.mTitleResId == 0) {
                return null;
            }
            if (!jSONObject.isNull(LauncherConst.KEY_TITLE_VALUE)) {
                customAppIconEntry.mTitleValue = jSONObject.getString(LauncherConst.KEY_TITLE_VALUE);
            }
            return customAppIconEntry;
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.w(TAG, "Exception reading change app icon list: " + e, new int[0]);
            return null;
        } catch (JSONException e2) {
            LGLog.w(TAG, "Exception reading change app icon list: " + e2, new int[0]);
            return null;
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public static Drawable getShadowIconIfNeeded(Context context, Drawable drawable, String packageName) {
        return getShadowIconIfNeeded(context, drawable, packageName, false);
    }

    public static Drawable getShadowIconIfNeeded(Context context, Drawable drawable, String packageName, boolean isUseMonochrome) {
        return (DDTUtils.isAdditionalThemeApplied(context) || (DDTUtils.isAdditionalIconThemeApplied(context) && !(drawable instanceof AdaptiveIconDrawable)) || isPackageResToBeSkipIconFrame(context, packageName)) ? drawable : new LauncherIconsForShadow(context).wrapIconDrawableWithShadow(drawable, false, isUseMonochrome);
    }

    private static boolean isPackageResToBeSkipIconFrame(Context context, String packageName) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 128);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d(TAG, "isPackageResToBeSkipIconFrame NameNotFoundException");
            applicationInfo = null;
        }
        if (applicationInfo == null || applicationInfo.metaData == null || !applicationInfo.metaData.getBoolean("com.lge.theme.skip_icon_frame", false)) {
            return false;
        }
        Log.d(TAG, packageName + " icon is requested to be not icon frame");
        return true;
    }

    public static Drawable getShadowIconIfNeeded(Context context, Drawable drawable, Boolean isTransparentBG) {
        return (DDTUtils.isAdditionalThemeApplied(context) || (DDTUtils.isAdditionalIconThemeApplied(context) && !(drawable instanceof AdaptiveIconDrawable))) ? drawable : new LauncherIconsForShadow(context).wrapIconDrawableWithShadow(drawable, isTransparentBG.booleanValue());
    }

    public boolean isReloadIcon() {
        return this.mIsReloadIcon;
    }

    public void setReloadIcon(boolean isReloadIcon) {
        this.mIsReloadIcon = isReloadIcon;
    }

    public synchronized void onlyResetTitleinDB() {
        LGLog.i(TAG, "onlyResetTitleinDB()");
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = this.mIconDb.query(new String[]{BaseIconCache.IconDB.COLUMN_ROWID}, null, null);
                if (cursorQuery != null) {
                    while (cursorQuery.moveToNext()) {
                        int i = cursorQuery.getInt(0);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(BaseIconCache.IconDB.COLUMN_LABEL, "");
                        this.mIconDb.update(contentValues, "rowid=?", new String[]{String.valueOf(i)});
                    }
                    cursorQuery.close();
                }
            } catch (Exception e) {
                Log.d(TAG, "Error reading icon cache for onlyResetTitleinDB", e);
                if (cursorQuery != null) {
                }
            }
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    private static class ClippedMonoDrawable extends InsetDrawable {
        private final AdaptiveIconDrawable mCrop;

        public ClippedMonoDrawable(Drawable base) {
            super(base, -AdaptiveIconDrawable.getExtraInsetFraction());
            this.mCrop = new AdaptiveIconDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK), null);
        }

        @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            this.mCrop.setBounds(getBounds());
            int iSave = canvas.save();
            canvas.clipPath(this.mCrop.getIconMask());
            super.draw(canvas);
            canvas.restoreToCount(iSave);
        }
    }
}
