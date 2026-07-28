package com.lge.launcher3.allapps;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.LongSparseArray;
import android.util.SparseArray;
import com.android.launcher3.DeferredHandler;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsSort;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import com.lge.launcher3.util.OrientationUtils;
import com.lge.launcher3.util.UserUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsItemFactory {
    private static final String CREATE_SWIVEL_ALLAPPS_ITEM = "create_swive_allapps_item";
    private static final boolean DEBUG = false;
    private static final ArrayList<ShortcutInfo> mFolderItems = new ArrayList<>();
    private static AllAppsItemFactory sInstance;
    static final Handler sWorker;
    static final HandlerThread sWorkerThread;
    private IAllAppsFolderListener mAllAppsFolderListener;
    private final ArrayList<AllAppsItemInfo> mAllAppsItems;
    private final Context mContext;
    private final AllAppsDBLoader mSwivelAllAppsDBLoader;
    private final String TAG = "AllAppsItemFactory";
    private SparseArray<CountInfo> mCountInfos = new SparseArray<>();
    private int mCurrentId = 0;
    final int LOAD_INIT = -1;
    public final int LOAD_FROM_MEMORY = 0;
    public final int LOAD_FROM_DB = 1;
    private int mLoadFromDbStatus = -1;
    DeferredHandler mHandler = new DeferredHandler();
    public boolean mUseSwivelDB = false;
    private final AllAppsDBLoader mAllAppsDBLoader = new AllAppsDBLoader();

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR  A[MD:():void (c)] call: java.util.ArrayList.<init>():void type: CONSTRUCTOR */
    public static /* synthetic */ ArrayList lambda$E2McIAGPWBO62AkYPcfatUmDGDA() {
        return new ArrayList();
    }

    static {
        HandlerThread handlerThread = new HandlerThread("allapps-loader");
        sWorkerThread = handlerThread;
        handlerThread.start();
        sWorker = new Handler(handlerThread.getLooper());
    }

    public class CountInfo {
        int[] countInfo;
        int id;
        int maxChildCount;

        public CountInfo(int id, int[] countInfo) {
            int[] iArr = {countInfo[0], countInfo[1]};
            this.countInfo = iArr;
            this.maxChildCount = 0;
            this.id = id;
            this.maxChildCount = iArr[0] * iArr[1];
        }

        public void updateCountInfo(int[] newInfo, String caller) {
            int i = this.id;
            int[] iArr = this.countInfo;
            LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]updateCountInfo : [" + i + "]. countInfo = [" + iArr[0] + ", " + iArr[1] + "], " + caller);
            int[] iArr2 = this.countInfo;
            iArr2[0] = newInfo[0];
            iArr2[1] = newInfo[1];
        }

        public void updateCountInfo(int x, int y, String caller) {
            LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]updateCountInfo : [" + this.id + "]. x,y = [" + x + ", " + y + "], " + caller);
            int[] iArr = this.countInfo;
            iArr[0] = x;
            iArr[1] = y;
        }
    }

    public CountInfo getCountInfo(int id) {
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            return this.mCountInfos.get(id);
        }
        return this.mCountInfos.get(0);
    }

    public CountInfo getCurrentCountInfo() {
        return getCountInfo(this.mCurrentId);
    }

    public static AllAppsItemFactory initialize(Context context, int countX, int countY) {
        AllAppsItemFactory allAppsItemFactory = new AllAppsItemFactory(context, countX, countY);
        sInstance = allAppsItemFactory;
        return allAppsItemFactory;
    }

    public static AllAppsItemFactory getInstance() {
        return sInstance;
    }

    public AllAppsItemFactory(Context context, int countX, int countY) {
        int integer;
        int integer2;
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            this.mSwivelAllAppsDBLoader = new AllAppsDBLoader(true);
            if (context.getResources().getConfiguration().orientation == 2) {
                integer = context.getResources().getInteger(R.integer.device_profile_allapps_swivel_home_numColumns_land);
                integer2 = context.getResources().getInteger(R.integer.device_profile_allapps_swivel_home_numRows_land);
            } else {
                integer = context.getResources().getInteger(R.integer.device_profile_allapps_swivel_home_numColumns_port);
                integer2 = context.getResources().getInteger(R.integer.device_profile_allapps_swivel_home_numRows_port);
            }
            this.mCountInfos.put(1, new CountInfo(1, new int[]{integer, integer2}));
        } else {
            this.mSwivelAllAppsDBLoader = null;
        }
        this.mAllAppsItems = AllAppsUtils.getAllAppsItemInfoList();
        this.mContext = context;
        this.mCountInfos.put(0, new CountInfo(0, new int[]{countX, countY}));
    }

    public int[] initLayout() {
        getDBLoader(false).setLauncher(this.mContext);
        getDBLoader(false).loadCellCountXY(getCountInfo(0).countInfo);
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            getDBLoader(true).setLauncher(this.mContext);
            getDBLoader(true).loadCellCountXY(getCountInfo(1).countInfo);
        }
        loadMenuItem(this.mAllAppsItems);
        return getCurrentCountInfo().countInfo;
    }

    public void setCellCountXY(int countX, int countY, ArrayList<AllAppsItemInfo> menuItems) {
        setCellCountXY(countX, countY, menuItems, false);
    }

    public void setCellCountXY(int countX, int countY, ArrayList<AllAppsItemInfo> menuItems, boolean isChagedLayout) {
        LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]setCellCountXY  countX = " + countX + " countY = " + countY + ", isChagedLayout = " + isChagedLayout + ", menuItem = " + (menuItems != null ? menuItems.size() : 0));
        if (countX == 0 || countY == 0) {
            return;
        }
        if (!isChagedLayout && getCurrentCountInfo().countInfo[0] == countX && getCurrentCountInfo().countInfo[1] == countY) {
            LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]setCellCountXY : skip mCountInfo = " + getCurrentCountInfo().countInfo[0] + ", " + getCurrentCountInfo().countInfo[1] + ", mMaxChildCount = " + getCurrentCountInfo().maxChildCount);
            return;
        }
        getCurrentCountInfo().updateCountInfo(countX, countY, "setCellCountXY");
        LGLog.d("AllAppsItemFactory", "[ALLAPPS_DB]setCellCountXY : update mCountInfo = " + getCurrentCountInfo().countInfo[0] + ", " + getCurrentCountInfo().countInfo[1] + ", mMaxChildCount = " + getCurrentCountInfo().maxChildCount);
        getCurrentDBLoader().updateCellCountXY(getCurrentCountInfo().countInfo);
        if (menuItems == null || menuItems.isEmpty()) {
            ArrayList<AllAppsItemInfo> arrayList = new ArrayList<>(this.mAllAppsItems);
            getCurrentDBLoader().loadMenuItemInfo(this.mAllAppsItems);
            makeMenuItemsBySeriallization();
            matchAppInfo(arrayList);
        } else {
            this.mAllAppsItems.clear();
            this.mAllAppsItems.addAll(menuItems);
            makeMenuItemsBySeriallization();
        }
        getCurrentDBLoader().bulkUpdateItemPositionByID(this.mAllAppsItems);
    }

    public ArrayList<AllAppsItemInfo> getAllAppsItemInfoList() {
        return this.mAllAppsItems;
    }

    private void loadMenuItem(ArrayList<AllAppsItemInfo> menuItem) {
        int[] layoutNumFromPreference = AllAppsUtils.getLayoutNumFromPreference(this.mContext, getCurrentCountInfo().countInfo);
        boolean appReload = AllAppsUtils.getAppReload();
        boolean zIsPortrait = OrientationUtils.isPortrait(this.mContext);
        LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]loadMenuItem reload = " + appReload + ", isport=" + zIsPortrait + ", cellcount = " + layoutNumFromPreference[0] + ", " + layoutNumFromPreference[1] + ", mCountInfo = " + getCurrentCountInfo().countInfo[0] + ", " + getCurrentCountInfo().countInfo[1]);
        if (menuItem == null) {
            return;
        }
        if (appReload) {
            if (layoutNumFromPreference[0] != getCurrentCountInfo().countInfo[0] || layoutNumFromPreference[1] != getCurrentCountInfo().countInfo[1]) {
                getCurrentCountInfo().updateCountInfo(layoutNumFromPreference, "loadMenuItem");
            }
            if (zIsPortrait != AllAppsUtils.getAppLastOrientation()) {
                int i = getCurrentCountInfo().countInfo[0];
                int i2 = getCurrentCountInfo().countInfo[1];
                for (AllAppsItemInfo allAppsItemInfo : menuItem) {
                    if (allAppsItemInfo != null) {
                        int i3 = (allAppsItemInfo.cellY * i2) + allAppsItemInfo.cellX;
                        allAppsItemInfo.cellX = i3 % i;
                        allAppsItemInfo.cellY = i3 / i;
                    }
                }
            }
            this.mLoadFromDbStatus = 0;
        } else {
            getCurrentDBLoader().loadMenuItemInfo(menuItem);
            if (layoutNumFromPreference[0] != getCurrentCountInfo().countInfo[0] || layoutNumFromPreference[1] != getCurrentCountInfo().countInfo[1]) {
                setCellCountXY(layoutNumFromPreference[0], layoutNumFromPreference[1], null);
            }
            this.mLoadFromDbStatus = 1;
        }
        AllAppsUtils.setAppLastOrientation(zIsPortrait);
    }

    public void resetDatabase(ArrayList<AppInfo> appList) {
        this.mAllAppsItems.clear();
        mFolderItems.clear();
        getDBLoader(false).factoryReset();
        getDBLoader(false).insertCellCountXY(getCountInfo(0).countInfo);
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            getDBLoader(true).factoryReset();
            setCreateSwiveDB(false);
            getDBLoader(true).insertCellCountXY(getCountInfo(1).countInfo);
        }
        AllAppsDefaultItems.clearDefaultItem();
        if (appList != null) {
            initAllAppsList(appList);
        }
    }

    private void initAllAppsList(ArrayList<? extends AppInfo> allAppsList) {
        long j;
        HashMap<ComponentName, AppInfo> map;
        int i = 0;
        LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]initAllAppsList : isSwivel = (" + this.mUseSwivelDB + ", " + LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() + "), mCountInfo = " + getCountInfo(0).countInfo[0] + ", " + getCountInfo(0).countInfo[1] + ")");
        HashMap<ComponentName, AppInfo> map2 = new HashMap<>();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (AppInfo appInfo : allAppsList) {
            if (ManagedProfileUtils.hasProfileOwnerAsUser(this.mContext, appInfo.user.getIdentifier())) {
                arrayList2.add(appInfo);
            } else if (LGHomeFeature.Config.FEATURE_USE_DUAL_APP.getValue()) {
                if (UserUtils.isSecondApplication(this.mContext, appInfo.user.getIdentifier())) {
                    arrayList.add(appInfo);
                } else {
                    map2.put(appInfo.componentName, appInfo);
                }
            } else {
                map2.put(appInfo.componentName, appInfo);
            }
        }
        HashMap<ComponentName, AppInfo> map3 = new HashMap<>();
        LongSparseArray<FolderInfo> folders = getDBLoader(false).getFolders();
        folders.clear();
        mFolderItems.clear();
        ArrayList<AllAppsItemInfo> defaultItems = AllAppsDefaultItems.getDefaultItems(this.mContext);
        if (defaultItems != null && !defaultItems.isEmpty()) {
            int i2 = 0;
            int i3 = 0;
            for (AllAppsItemInfo allAppsItemInfo : defaultItems) {
                AllAppsItemInfo allAppsItemInfoMakeMenuItemInfo = makeMenuItemInfo(allAppsItemInfo, map2, map3);
                if (allAppsItemInfoMakeMenuItemInfo != null) {
                    if (i2 == getCountInfo(0).maxChildCount) {
                        i3++;
                        i2 = 0;
                    }
                    long j2 = allAppsItemInfo.screenId;
                    HashMap<ComponentName, AppInfo> map4 = map3;
                    if (j2 > i3 && i2 > 0) {
                        i3++;
                        i2 = 0;
                    }
                    allAppsItemInfoMakeMenuItemInfo.screenId = i3;
                    allAppsItemInfoMakeMenuItemInfo.cellX = i2 % getCountInfo(0).countInfo[0];
                    allAppsItemInfoMakeMenuItemInfo.cellY = i2 / getCountInfo(0).countInfo[0];
                    allAppsItemInfoMakeMenuItemInfo.id = (getCountInfo(0).maxChildCount * i3) + i2 + 2;
                    if (allAppsItemInfoMakeMenuItemInfo.itemType == 2 && allAppsItemInfoMakeMenuItemInfo.mFolderInfo != null) {
                        allAppsItemInfoMakeMenuItemInfo.mFolderInfo.id = allAppsItemInfoMakeMenuItemInfo.id;
                        folders.put(allAppsItemInfoMakeMenuItemInfo.id, allAppsItemInfoMakeMenuItemInfo.mFolderInfo);
                        for (ShortcutInfo shortcutInfo : allAppsItemInfoMakeMenuItemInfo.mFolderInfo.getContents()) {
                            shortcutInfo.container = allAppsItemInfoMakeMenuItemInfo.mFolderInfo.id;
                            if (shortcutInfo.rank < 9) {
                                shortcutInfo.iconBitmap = null;
                            }
                        }
                    }
                    this.mAllAppsItems.add(allAppsItemInfoMakeMenuItemInfo);
                    i2++;
                    map3 = map4;
                }
            }
        }
        HashMap<ComponentName, AppInfo> map5 = map3;
        AllAppsItemInfo allAppsItemInfo2 = null;
        int size = this.mAllAppsItems.size();
        if (size > 0) {
            AllAppsItemInfo allAppsItemInfo3 = this.mAllAppsItems.get(size - 1);
            if (AllAppsDefaultItems.isDividePage()) {
                j = (allAppsItemInfo3.screenId + 1) * ((long) getCountInfo(0).maxChildCount);
            } else {
                j = allAppsItemInfo3.id - 1;
            }
        } else {
            j = 0;
        }
        for (AppInfo appInfo2 : allAppsList) {
            if (appInfo2.isPreloadedApp()) {
                map = map5;
                if (!map.containsKey(appInfo2.componentName) && !arrayList.contains(appInfo2) && !arrayList2.contains(appInfo2)) {
                    addToMenuItems(appInfo2, j, 0);
                    j++;
                }
            } else {
                map = map5;
            }
            map5 = map;
        }
        HashMap<ComponentName, AppInfo> map6 = map5;
        for (AppInfo appInfo3 : allAppsList) {
            if (!appInfo3.isPreloadedApp() && !map6.containsKey(appInfo3.componentName) && !arrayList.contains(appInfo3) && !arrayList2.contains(appInfo3)) {
                addToMenuItems(appInfo3, j, 0);
                j++;
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            addToMenuItems((AppInfo) it.next(), j, 0);
            j++;
        }
        long j3 = ((j / ((long) getCountInfo(0).maxChildCount)) + 1) * ((long) getCountInfo(0).maxChildCount);
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            addToMenuItems((AppInfo) it2.next(), j3, 0);
            j3++;
        }
        final ArrayList<AllAppsItemInfo> arrayList3 = new ArrayList<>();
        final ArrayList arrayList4 = new ArrayList(this.mAllAppsItems);
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            if (Utilities.getPrefs(this.mContext).getBoolean(CREATE_SWIVEL_ALLAPPS_ITEM, false)) {
                getDBLoader(true).loadMenuItemInfo(arrayList3);
            } else {
                int i4 = getCountInfo(1).countInfo[0];
                int i5 = getCountInfo(1).countInfo[1];
                int i6 = getCountInfo(1).maxChildCount;
                LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]make swivel item : numColumns = " + i4 + ", numRows = " + i5 + ", maxChildCount = " + i6 + ", mAllAppsItems.size = " + this.mAllAppsItems.size());
                for (AllAppsItemInfo allAppsItemInfo4 : this.mAllAppsItems) {
                    if (allAppsItemInfo4 != null) {
                        if (allAppsItemInfo4.itemType == 0 && allAppsItemInfo4.container <= 0) {
                            allAppsItemInfo2 = new AllAppsItemInfo(allAppsItemInfo4);
                        } else if (allAppsItemInfo4.itemType == 2) {
                            allAppsItemInfo2 = new AllAppsItemInfo(allAppsItemInfo4.mFolderInfo);
                            allAppsItemInfo2.id = allAppsItemInfo4.id;
                        }
                        allAppsItemInfo2.screenId = i / i6;
                        int i7 = (int) (((long) i) - (((long) i6) * allAppsItemInfo2.screenId));
                        allAppsItemInfo2.cellX = i7 % i4;
                        allAppsItemInfo2.cellY = i7 / i4;
                        arrayList3.add(allAppsItemInfo2);
                        i++;
                    }
                }
            }
            if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]initAllAppsList: change allAppsItems to swivelAllAppsItems");
                setUseSwivelDB(true, "initAllAppsList");
                this.mAllAppsItems.clear();
                this.mAllAppsItems.addAll(arrayList3);
            }
        }
        runOnWorkerThread(new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsItemFactory.1
            @Override // java.lang.Runnable
            public void run() {
                AllAppsItemFactory.this.getDBLoader(false).factoryReset();
                AllAppsItemFactory.this.getDBLoader(false).bulkInsertItems(arrayList4);
                AllAppsItemFactory.this.getDBLoader(false).bulkInsertFolderItems(AllAppsItemFactory.mFolderItems);
                if (!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() || Utilities.getPrefs(AllAppsItemFactory.this.mContext).getBoolean(AllAppsItemFactory.CREATE_SWIVEL_ALLAPPS_ITEM, false)) {
                    return;
                }
                AllAppsItemFactory.this.getDBLoader(true).factoryReset();
                AllAppsItemFactory.this.setCreateSwiveDB(false);
                AllAppsItemFactory.this.getDBLoader(true).bulkInsertItems(arrayList3);
                AllAppsItemFactory.this.getDBLoader(true).bulkInsertFolderItems(AllAppsItemFactory.mFolderItems);
                AllAppsItemFactory.this.setCreateSwiveDB(true);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCreateSwiveDB(boolean value) {
        this.mContext.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).edit().putBoolean(CREATE_SWIVEL_ALLAPPS_ITEM, value).commit();
    }

    private void addToMenuItems(AppInfo appInfo, long seq, int id) {
        AllAppsItemInfo allAppsItemInfo = new AllAppsItemInfo(appInfo);
        allAppsItemInfo.id = (int) (2 + seq);
        allAppsItemInfo.screenId = seq / ((long) getCountInfo(id).maxChildCount);
        allAppsItemInfo.cellX = (int) ((seq - (((long) getCountInfo(id).maxChildCount) * allAppsItemInfo.screenId)) % ((long) getCountInfo(id).countInfo[0]));
        allAppsItemInfo.cellY = (int) ((seq - (((long) getCountInfo(id).maxChildCount) * allAppsItemInfo.screenId)) / ((long) getCountInfo(id).countInfo[0]));
        this.mAllAppsItems.add(allAppsItemInfo);
    }

    private AllAppsItemInfo makeMenuItemInfo(AllAppsItemInfo itemInfo, HashMap<ComponentName, AppInfo> allAppHash, HashMap<ComponentName, AppInfo> defaultAppHash) {
        AppInfo appInfo;
        if (itemInfo.itemType != 0) {
            return makeFolderItem(itemInfo, allAppHash, defaultAppHash);
        }
        if (itemInfo.container > 0 || (appInfo = allAppHash.get(itemInfo.componentName)) == null) {
            return null;
        }
        AllAppsItemInfo allAppsItemInfo = new AllAppsItemInfo(appInfo);
        defaultAppHash.put(appInfo.componentName, appInfo);
        return allAppsItemInfo;
    }

    private AllAppsItemInfo makeFolderItem(AllAppsItemInfo itemInfo, HashMap<ComponentName, AppInfo> allAppHash, HashMap<ComponentName, AppInfo> defaultAppHash) {
        AllAppsFolderInfo allAppsFolderInfo = itemInfo.mFolderInfo;
        ArrayList<ShortcutInfo> arrayList = new ArrayList<>();
        for (ShortcutInfo shortcutInfo : allAppsFolderInfo.getContents()) {
            if (shortcutInfo.getIntent().getComponent() == null) {
                LGLog.e("AllAppsItemFactory", "check your default_pagemenu.xml");
                LGLog.e("AllAppsItemFactory", "Wrong componentName!");
            } else {
                AppInfo appInfo = allAppHash.get(ComponentName.unflattenFromString(shortcutInfo.getIntent().getComponent().flattenToShortString()));
                if (appInfo != null) {
                    ShortcutInfo shortcutInfo2 = new ShortcutInfo(appInfo);
                    shortcutInfo2.container = allAppsFolderInfo.id;
                    arrayList.add(shortcutInfo2);
                    defaultAppHash.put(appInfo.componentName, appInfo);
                    mFolderItems.add(shortcutInfo2);
                }
            }
        }
        int size = arrayList.size();
        if (size != 0) {
            if (size == 1) {
                ShortcutInfo shortcutInfo3 = arrayList.get(0);
                defaultAppHash.remove(shortcutInfo3.getIntent().getComponent());
                mFolderItems.remove(shortcutInfo3);
            } else {
                allAppsFolderInfo.setContents(arrayList);
                allAppsFolderInfo.user = Process.myUserHandle();
                return new AllAppsItemInfo(allAppsFolderInfo);
            }
        }
        return null;
    }

    private void makeMenuItemsBySeriallization() {
        boolean z = this.mUseSwivelDB;
        if (z) {
            long j = 0;
            LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]makeMenuItemsBySeriallization. " + z);
            int i = 0;
            for (AllAppsItemInfo allAppsItemInfo : this.mAllAppsItems) {
                if (allAppsItemInfo != null) {
                    if (j != allAppsItemInfo.screenId) {
                        i = 0;
                    }
                    j = allAppsItemInfo.screenId;
                    allAppsItemInfo.cellX = i % getCountInfo(1).countInfo[0];
                    allAppsItemInfo.cellY = i / getCountInfo(1).countInfo[0];
                    allAppsItemInfo.requiresDbUpdate = true;
                    i++;
                }
            }
            return;
        }
        if (AllAppsUtils.needToSeriallization()) {
            LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]makeMenuItemsBySeriallization. " + this.mUseSwivelDB);
            int i2 = 0;
            for (AllAppsItemInfo allAppsItemInfo2 : getManagedProfilesItems(this.mContext, this.mAllAppsItems, false)) {
                if (allAppsItemInfo2 != null) {
                    allAppsItemInfo2.screenId = i2 / getCountInfo(0).maxChildCount;
                    long j2 = i2;
                    allAppsItemInfo2.cellX = (int) ((j2 - (((long) getCountInfo(0).maxChildCount) * allAppsItemInfo2.screenId)) % ((long) getCountInfo(0).countInfo[0]));
                    allAppsItemInfo2.cellY = (int) ((j2 - (((long) getCountInfo(0).maxChildCount) * allAppsItemInfo2.screenId)) / ((long) getCountInfo(0).countInfo[0]));
                    allAppsItemInfo2.requiresDbUpdate = true;
                    i2++;
                }
            }
            int i3 = ((i2 / getCountInfo(0).maxChildCount) + 1) * getCountInfo(0).maxChildCount;
            for (AllAppsItemInfo allAppsItemInfo3 : getManagedProfilesItems(this.mContext, this.mAllAppsItems, true)) {
                if (allAppsItemInfo3 != null) {
                    allAppsItemInfo3.screenId = i3 / getCountInfo(0).maxChildCount;
                    long j3 = i3;
                    allAppsItemInfo3.cellX = (int) ((j3 - (((long) getCountInfo(0).maxChildCount) * allAppsItemInfo3.screenId)) % ((long) getCountInfo(0).countInfo[0]));
                    allAppsItemInfo3.cellY = (int) ((j3 - (((long) getCountInfo(0).maxChildCount) * allAppsItemInfo3.screenId)) / ((long) getCountInfo(0).countInfo[0]));
                    allAppsItemInfo3.requiresDbUpdate = true;
                    i3++;
                }
            }
            AllAppsUtils.setNeedToSeriallization(false, "makeMenuItemsBySeriallization");
            return;
        }
        LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]makeMenuItemsBySeriallization : skip. " + this.mUseSwivelDB);
    }

    private void matchAppInfo(ArrayList<AllAppsItemInfo> preItemInfos) {
        HashMap map = new HashMap();
        for (AllAppsItemInfo allAppsItemInfo : preItemInfos) {
            if (allAppsItemInfo != null && allAppsItemInfo.itemType == 0 && allAppsItemInfo.componentName != null) {
                map.put(CacheKey.createKey(allAppsItemInfo), allAppsItemInfo);
            }
        }
        for (AllAppsItemInfo allAppsItemInfo2 : this.mAllAppsItems) {
            if (allAppsItemInfo2 != null && allAppsItemInfo2.itemType != 2) {
                CacheKey cacheKeyCreateKey = CacheKey.createKey(allAppsItemInfo2);
                AppInfo appInfo = (AppInfo) map.get(cacheKeyCreateKey);
                if (appInfo != null) {
                    allAppsItemInfo2.copyFrom(appInfo);
                    map.remove(cacheKeyCreateKey);
                }
            }
        }
    }

    public void rearrangeBySortType() {
        rearrangeBySortType(AllAppsSort.SortType.NAME);
    }

    public void rearrangeBySortType(AllAppsSort.SortType sortType) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (AllAppsItemInfo allAppsItemInfo : this.mAllAppsItems) {
            if (allAppsItemInfo != null) {
                if (allAppsItemInfo.itemType == 2) {
                    arrayList.add(allAppsItemInfo);
                } else {
                    arrayList2.add(allAppsItemInfo);
                }
            }
        }
        this.mAllAppsItems.clear();
        int i = AnonymousClass4.$SwitchMap$com$lge$launcher3$allapps$AllAppsSort$SortType[sortType.ordinal()];
        if (i == 1) {
            Collections.sort(arrayList, AllAppsSort.FOLDER_NAME_COMPARATOR);
            Collections.sort(arrayList2, AllAppsSort.NAME_COMPARATOR);
            this.mAllAppsItems.addAll(arrayList);
            this.mAllAppsItems.addAll(arrayList2);
        } else if (i == 2) {
            Collections.sort(arrayList, AllAppsSort.POSITION_COMPARATOR);
            Collections.sort(arrayList2, AllAppsSort.INSTALL_TIME_COMPARATOR);
            this.mAllAppsItems.addAll(arrayList);
            this.mAllAppsItems.addAll(arrayList2);
        }
        long j = 0;
        for (AllAppsItemInfo allAppsItemInfo2 : this.mAllAppsItems) {
            if (allAppsItemInfo2 != null) {
                allAppsItemInfo2.requiresDbUpdate = true;
                allAppsItemInfo2.screenId = j / ((long) getCurrentCountInfo().maxChildCount);
                allAppsItemInfo2.cellX = (int) ((j - (((long) getCurrentCountInfo().maxChildCount) * allAppsItemInfo2.screenId)) % ((long) getCurrentCountInfo().countInfo[0]));
                allAppsItemInfo2.cellY = (int) ((j - (((long) getCurrentCountInfo().maxChildCount) * allAppsItemInfo2.screenId)) / ((long) getCurrentCountInfo().countInfo[0]));
                j++;
            }
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.allapps.AllAppsItemFactory$4, reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$allapps$AllAppsSort$SortType;

        static {
            int[] iArr = new int[AllAppsSort.SortType.values().length];
            $SwitchMap$com$lge$launcher3$allapps$AllAppsSort$SortType = iArr;
            try {
                iArr[AllAppsSort.SortType.NAME.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$allapps$AllAppsSort$SortType[AllAppsSort.SortType.DOWNLOAD_DATE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public boolean syncAllAppsList(ArrayList<AppInfo> allAppsList) {
        CacheKey cacheKey;
        AppInfo appInfo;
        Iterator<ShortcutInfo> it;
        if (this.mAllAppsItems.isEmpty() && LauncherAppState.getInstance(this.mContext).getHideAppsCount() == -1 && !this.mUseSwivelDB) {
            initAllAppsList(allAppsList);
            return true;
        }
        boolean z = this.mUseSwivelDB;
        HashMap<CacheKey, AppInfo> map = new HashMap<>();
        ArrayList arrayList = mFolderItems != null ? new ArrayList() : null;
        for (AppInfo appInfo2 : allAppsList) {
            map.put(new CacheKey(appInfo2.componentName, appInfo2.user), appInfo2);
        }
        int i = 0;
        while (i < this.mAllAppsItems.size()) {
            AllAppsItemInfo allAppsItemInfo = this.mAllAppsItems.get(i);
            if (allAppsItemInfo != null) {
                if (allAppsItemInfo.itemType == 2) {
                    AllAppsFolderInfo allAppsFolderInfo = allAppsItemInfo.mFolderInfo;
                    if (allAppsFolderInfo.user == null) {
                        if (arrayList != null) {
                            arrayList.add(allAppsItemInfo);
                        }
                        CharSequence charSequence = allAppsFolderInfo.title;
                        LGLog.d("AllAppsItemFactory", "UserHanlde is Null. removeFolders.add " + ((Object) charSequence) + " cellX, Y " + allAppsItemInfo.cellX + " , " + allAppsItemInfo.cellY);
                    } else {
                        ArrayList<ShortcutInfo> contents = allAppsFolderInfo.getContents();
                        allAppsFolderInfo.setContents(new ArrayList<>());
                        ArrayList arrayList2 = new ArrayList();
                        Iterator<ShortcutInfo> it2 = contents.iterator();
                        while (it2.hasNext()) {
                            ShortcutInfo next = it2.next();
                            AppInfo appInfo3 = map.get(CacheKey.createKey(next));
                            if (appInfo3 != null) {
                                arrayList2.add(appInfo3);
                                ShortcutInfo shortcutInfo = new ShortcutInfo(appInfo3);
                                it = it2;
                                shortcutInfo.container = next.container;
                                shortcutInfo.id = next.id;
                                shortcutInfo.rank = next.rank;
                                if (shortcutInfo.rank < 9) {
                                    shortcutInfo.iconBitmap = null;
                                    shortcutInfo.usingLowResIcon = false;
                                }
                                allAppsFolderInfo.getContents().add(shortcutInfo);
                            } else {
                                it = it2;
                                removeFolderItem(allAppsFolderInfo, next);
                            }
                            it2 = it;
                        }
                        if (allAppsFolderInfo.getContents().size() < 2) {
                            if (allAppsFolderInfo.getContents().size() == 1) {
                                AllAppsItemInfo allAppsItemInfo2 = new AllAppsItemInfo((AppInfo) arrayList2.get(0));
                                allAppsItemInfo2.cellX = allAppsItemInfo.cellX;
                                allAppsItemInfo2.cellY = allAppsItemInfo.cellY;
                                allAppsItemInfo2.screenId = allAppsItemInfo.screenId;
                                addNewItemInfo(allAppsItemInfo2);
                            }
                            if (arrayList != null) {
                                arrayList.add(allAppsItemInfo);
                            }
                            CharSequence charSequence2 = allAppsFolderInfo.title;
                            LGLog.d("AllAppsItemFactory", "removeFolders.add " + ((Object) charSequence2) + " cellX, Y " + allAppsItemInfo.cellX + " , " + allAppsItemInfo.cellY);
                        } else {
                            Iterator it3 = arrayList2.iterator();
                            while (it3.hasNext()) {
                                map.remove(CacheKey.createKey((AppInfo) it3.next()));
                            }
                        }
                    }
                } else {
                    if (allAppsItemInfo.user != null) {
                        cacheKey = new CacheKey(allAppsItemInfo.componentName, allAppsItemInfo.user);
                        appInfo = map.get(cacheKey);
                    } else {
                        cacheKey = null;
                        appInfo = null;
                    }
                    if (appInfo != null) {
                        allAppsItemInfo.copyFrom(appInfo);
                        map.remove(cacheKey);
                    } else {
                        getDBLoader(z).delete(allAppsItemInfo);
                        this.mAllAppsItems.remove(i);
                        i--;
                        LGLog.d("menuItemFactory", "remove mAllAppsItems =" + allAppsItemInfo.componentName);
                    }
                }
            }
            i++;
        }
        if (arrayList != null && arrayList.size() > 0) {
            Iterator it4 = arrayList.iterator();
            while (it4.hasNext()) {
                removeFolder((AllAppsItemInfo) it4.next());
            }
        }
        if (!isValidCellCount()) {
            LGLog.d("menuItemFactory", "syncAllAppsList : cellcount info mismatch!!");
            makeMenuItemsBySeriallization();
            getDBLoader(z).bulkUpdateItemPositionByID(this.mAllAppsItems);
        } else if (hasInvalidItems()) {
            LGLog.d("menuItemFactory", "syncAllAppsList : must reorder!!");
            removeVacantPage();
            removeVacantIcon();
            removeDuplicatedIcon();
            getDBLoader(z).bulkUpdateItemPositionByID(this.mAllAppsItems);
        }
        if (map.size() <= 0 || arrayList == null) {
            return true;
        }
        addNewApplicationToLastPage(map, this.mAllAppsItems);
        return true;
    }

    private void addNewApplicationToLastPage(HashMap<CacheKey, AppInfo> pmHashMap, ArrayList<AllAppsItemInfo> dbitems) {
        int[] iArr = {0, 0, 0};
        getNewItemPosForAdd(iArr, getManagedProfilesItems(this.mContext, dbitems, false));
        int[] iArr2 = {0, 0, 0};
        ArrayList<AllAppsItemInfo> managedProfilesItems = getManagedProfilesItems(this.mContext, dbitems, true);
        if (managedProfilesItems.size() == 0) {
            iArr2[0] = iArr[0];
            iArr2[1] = getCurrentCountInfo().countInfo[0] - 1;
            iArr2[2] = getCurrentCountInfo().countInfo[1] - 1;
        } else {
            getNewItemPosForAdd(iArr2, managedProfilesItems);
        }
        int i = dbitems.isEmpty() ? 0 : (iArr[2] * getCurrentCountInfo().countInfo[0]) + iArr[1] + 1;
        int i2 = dbitems.isEmpty() ? 0 : (iArr2[2] * getCurrentCountInfo().countInfo[0]) + iArr2[1] + 1;
        ArrayList arrayList = new ArrayList(pmHashMap.values());
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            AllAppsItemInfo allAppsItemInfo = new AllAppsItemInfo((AppInfo) arrayList.get(i3));
            if (isManagedProfileItem(this.mContext, allAppsItemInfo)) {
                if (i2 == getCurrentCountInfo().maxChildCount) {
                    iArr2[0] = iArr2[0] + 1;
                    i2 = 0;
                }
                allAppsItemInfo.screenId = iArr2[0];
                allAppsItemInfo.cellX = i2 % getCurrentCountInfo().countInfo[0];
                allAppsItemInfo.cellY = i2 / getCurrentCountInfo().countInfo[0];
                getCurrentDBLoader().insertItem(allAppsItemInfo);
                this.mAllAppsItems.add(allAppsItemInfo);
                i2++;
            } else {
                if (i == getCurrentCountInfo().maxChildCount) {
                    iArr[0] = iArr[0] + 1;
                    i = 0;
                }
                allAppsItemInfo.screenId = iArr[0];
                allAppsItemInfo.cellX = i % getCurrentCountInfo().countInfo[0];
                allAppsItemInfo.cellY = i / getCurrentCountInfo().countInfo[0];
                getCurrentDBLoader().insertItem(allAppsItemInfo);
                this.mAllAppsItems.add(allAppsItemInfo);
                i++;
            }
        }
    }

    private boolean isValidCellCount() {
        if (getCurrentCountInfo().countInfo[0] > this.mAllAppsItems.size()) {
            return true;
        }
        for (AllAppsItemInfo allAppsItemInfo : this.mAllAppsItems) {
            if (allAppsItemInfo != null && (allAppsItemInfo.cellX >= getCurrentCountInfo().countInfo[0] || allAppsItemInfo.cellY >= getCurrentCountInfo().countInfo[1])) {
                return false;
            }
        }
        return true;
    }

    private void removeVacantPage() {
        int maxPage = getMaxPage();
        int i = maxPage + 1;
        boolean[] zArr = new boolean[i];
        for (int i2 = 0; i2 < i; i2++) {
            zArr[i2] = true;
        }
        for (int i3 = 0; i3 < this.mAllAppsItems.size(); i3++) {
            AllAppsItemInfo allAppsItemInfo = this.mAllAppsItems.get(i3);
            if (allAppsItemInfo != null && allAppsItemInfo.screenId >= 0 && zArr[(int) allAppsItemInfo.screenId]) {
                zArr[(int) allAppsItemInfo.screenId] = false;
            }
        }
        int i4 = 0;
        for (int i5 = 0; i5 < maxPage; i5++) {
            if (zArr[i5]) {
                fillVacantPage(i5 + i4, this.mAllAppsItems);
                i4--;
            }
        }
    }

    private void removeVacantIcon() {
        boolean z;
        int maxPage = getMaxPage();
        ArrayList<AllAppsItemInfo> arrayList = new ArrayList<>();
        int i = 0;
        while (true) {
            boolean z2 = true;
            if (i < maxPage + 1) {
                if (!getOccupiedMap(i, arrayList, this.mAllAppsItems)) {
                    return;
                }
                int i2 = 0;
                while (i2 < arrayList.size()) {
                    if (arrayList.get(i2) == null) {
                        int i3 = i2 + 1;
                        while (i3 < arrayList.size()) {
                            AllAppsItemInfo allAppsItemInfo = arrayList.get(i3);
                            if (allAppsItemInfo != null) {
                                LGLog.d("AllAppsItemFactory", "chage cell Before =" + allAppsItemInfo.componentName + "cellX,Y = " + allAppsItemInfo.cellX + ", " + allAppsItemInfo.cellY);
                                z = true;
                                int i4 = ((allAppsItemInfo.cellY * getCurrentCountInfo().countInfo[0]) + allAppsItemInfo.cellX) - 1;
                                allAppsItemInfo.cellX = i4 % getCurrentCountInfo().countInfo[0];
                                allAppsItemInfo.cellY = i4 / getCurrentCountInfo().countInfo[0];
                                allAppsItemInfo.requiresDbUpdate = true;
                                LGLog.d("AllAppsItemFactory", "chage cell After=" + allAppsItemInfo.componentName + "cellX,Y = " + allAppsItemInfo.cellX + ", " + allAppsItemInfo.cellY);
                            } else {
                                z = z2;
                            }
                            i3++;
                            z2 = z;
                        }
                    }
                    i2++;
                    z2 = z2;
                }
                arrayList.clear();
                i++;
            } else {
                for (AllAppsItemInfo allAppsItemInfo2 : this.mAllAppsItems) {
                    if (allAppsItemInfo2 != null) {
                        LGLog.d("AllAppsItemFactory", "menuItems =" + allAppsItemInfo2.componentName + "cellX,Y = " + allAppsItemInfo2.cellX + ", " + allAppsItemInfo2.cellY);
                    }
                }
                return;
            }
        }
    }

    private boolean getOccupiedMap(int page, ArrayList<AllAppsItemInfo> occupied, ArrayList<AllAppsItemInfo> menuItems) {
        occupied.clear();
        for (int i = 0; i < getCurrentCountInfo().maxChildCount; i++) {
            occupied.add(i, null);
        }
        for (int i2 = 0; i2 < menuItems.size(); i2++) {
            AllAppsItemInfo allAppsItemInfo = menuItems.get(i2);
            if (allAppsItemInfo != null && allAppsItemInfo.screenId == page) {
                if ((allAppsItemInfo.cellY * getCurrentCountInfo().countInfo[0]) + allAppsItemInfo.cellX >= getCurrentCountInfo().countInfo[0] * getCurrentCountInfo().countInfo[1]) {
                    LGLog.w("AllAppsItemFactory", " Wrong cell X,Y  x=" + allAppsItemInfo.cellX + " y=" + allAppsItemInfo.cellY, new int[0]);
                    return false;
                }
                occupied.set((allAppsItemInfo.cellY * getCurrentCountInfo().countInfo[0]) + allAppsItemInfo.cellX, allAppsItemInfo);
            }
        }
        return true;
    }

    private void fillVacantPage(int page, ArrayList<AllAppsItemInfo> menuItems) {
        for (int i = 0; i < menuItems.size(); i++) {
            AllAppsItemInfo allAppsItemInfo = menuItems.get(i);
            if (allAppsItemInfo != null && allAppsItemInfo.screenId > page) {
                allAppsItemInfo.screenId--;
                allAppsItemInfo.requiresDbUpdate = true;
            }
        }
    }

    public void updatePositionChangedItems() {
        getCurrentDBLoader().bulkUpdateItemPositionByID(this.mAllAppsItems);
    }

    public void updateFolderColorAndTitle(FolderInfo folderInfo) {
        getCurrentDBLoader().updateFolderColorAndTitle(folderInfo);
    }

    public boolean removeItemInfo(AllAppsItemInfo itemInfo) {
        getCurrentDBLoader().delete(itemInfo);
        this.mAllAppsItems.remove(itemInfo);
        return true;
    }

    public void removeFolder(AllAppsItemInfo folderInfo) {
        this.mAllAppsItems.remove(folderInfo);
        getCurrentDBLoader().removeFolder(folderInfo.mFolderInfo);
        for (ShortcutInfo shortcutInfo : folderInfo.mFolderInfo.getContents()) {
            mFolderItems.remove(shortcutInfo);
            getCurrentDBLoader().deleteFolderItem(shortcutInfo);
        }
    }

    public void addNewItemInfo(AllAppsItemInfo itemInfo) {
        this.mAllAppsItems.add(itemInfo);
        getCurrentDBLoader().insert(itemInfo, false);
    }

    public AllAppsItemInfo addNewApplication(int pageID, int cellX, int cellY, AppInfo appInfo, boolean immediately) {
        AllAppsItemInfo allAppsItemInfo = new AllAppsItemInfo(appInfo);
        allAppsItemInfo.cellX = cellX;
        allAppsItemInfo.cellY = cellY;
        allAppsItemInfo.screenId = pageID;
        this.mAllAppsItems.add(allAppsItemInfo);
        if (immediately) {
            getCurrentDBLoader().insert(allAppsItemInfo, immediately);
        }
        return allAppsItemInfo;
    }

    public AllAppsItemInfo findMenuItemInfoByAppInfo(AppInfo appInfo) {
        ComponentName componentName = appInfo.componentName;
        for (AllAppsItemInfo allAppsItemInfo : this.mAllAppsItems) {
            if (allAppsItemInfo != null && allAppsItemInfo.itemType == 0 && componentName.equals(allAppsItemInfo.componentName) && allAppsItemInfo.user.equals(appInfo.user)) {
                return allAppsItemInfo;
            }
        }
        return null;
    }

    public int getMaxPage() {
        int i = 0;
        for (int i2 = 0; i2 < this.mAllAppsItems.size(); i2++) {
            AllAppsItemInfo allAppsItemInfo = this.mAllAppsItems.get(i2);
            if (allAppsItemInfo != null && allAppsItemInfo.screenId > i) {
                i = (int) allAppsItemInfo.screenId;
            }
        }
        return i;
    }

    private int[] getNewItemPosForAdd(int[] tempPos, ArrayList<AllAppsItemInfo> dbitems) {
        for (AllAppsItemInfo allAppsItemInfo : dbitems) {
            if (allAppsItemInfo != null) {
                if (allAppsItemInfo.screenId == tempPos[0]) {
                    if (allAppsItemInfo.cellY == tempPos[2]) {
                        if (allAppsItemInfo.cellX >= tempPos[1]) {
                            tempPos[1] = allAppsItemInfo.cellX;
                        }
                    } else if (allAppsItemInfo.cellY > tempPos[2]) {
                        tempPos[1] = allAppsItemInfo.cellX;
                        tempPos[2] = allAppsItemInfo.cellY;
                    }
                } else if (allAppsItemInfo.screenId > tempPos[0]) {
                    tempPos[0] = (int) allAppsItemInfo.screenId;
                    tempPos[1] = allAppsItemInfo.cellX;
                    tempPos[2] = allAppsItemInfo.cellY;
                }
            }
        }
        if (tempPos[1] == getCurrentCountInfo().countInfo[0]) {
            tempPos[2] = tempPos[2] + 1;
            tempPos[0] = 0;
        }
        if (tempPos[2] == getCurrentCountInfo().countInfo[1]) {
            tempPos[0] = tempPos[0] + 1;
            tempPos[1] = 0;
            tempPos[2] = 0;
        }
        return tempPos;
    }

    private boolean hasInvalidItems() {
        int maxPage = getMaxPage();
        ArrayList<AllAppsItemInfo> arrayList = new ArrayList<>();
        for (int i = 0; i < maxPage + 1; i++) {
            int occupiedMapfromDB = getOccupiedMapfromDB(i, arrayList, this.mAllAppsItems);
            if (occupiedMapfromDB < 0) {
                LGLog.w("AllAppsItemFactory", "hasInvalidItems : Duplicated cell!!", new int[0]);
                return true;
            }
            if (occupiedMapfromDB == 0) {
                LGLog.w("AllAppsItemFactory", "hasInvalidItems : Vacant page!!", new int[0]);
                return true;
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (arrayList.get(i2) == null && i2 <= occupiedMapfromDB - 1) {
                    LGLog.w("AllAppsItemFactory", "hasInvalidItems : Vacant cell!!", new int[0]);
                    return true;
                }
            }
            arrayList.clear();
        }
        return false;
    }

    private int getOccupiedMapfromDB(int page, ArrayList<AllAppsItemInfo> occupied, ArrayList<AllAppsItemInfo> menuItems) {
        int i;
        int i2 = getCurrentCountInfo().maxChildCount;
        occupied.clear();
        for (int i3 = 0; i3 < getCurrentCountInfo().maxChildCount; i3++) {
            occupied.add(i3, null);
        }
        for (int i4 = 0; i4 < menuItems.size(); i4++) {
            AllAppsItemInfo allAppsItemInfo = menuItems.get(i4);
            if (allAppsItemInfo != null && allAppsItemInfo.screenId == page && (i = (allAppsItemInfo.cellY * getCurrentCountInfo().countInfo[0]) + allAppsItemInfo.cellX) < occupied.size()) {
                if (occupied.get(i) == null) {
                    occupied.set((allAppsItemInfo.cellY * getCurrentCountInfo().countInfo[0]) + allAppsItemInfo.cellX, allAppsItemInfo);
                } else {
                    LGLog.w("menuItemFactory", "getOccupiedMapfromDB : Duplicate cell!!", new int[0]);
                    return -1;
                }
            }
        }
        for (int size = occupied.size() - 1; size >= 0 && occupied.get(size) == null; size--) {
            i2--;
        }
        return i2;
    }

    private void removeDuplicatedIcon() {
        int i;
        int i2;
        AllAppsItemFactory allAppsItemFactory = this;
        int maxPage = getMaxPage();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        while (i4 < maxPage + 1) {
            for (int i9 = 0; i9 < getCurrentCountInfo().maxChildCount; i9++) {
                arrayList.add(i9, null);
            }
            int i10 = 0;
            while (i10 < allAppsItemFactory.mAllAppsItems.size()) {
                AllAppsItemInfo allAppsItemInfo = allAppsItemFactory.mAllAppsItems.get(i10);
                if (allAppsItemInfo == null) {
                    i = maxPage;
                } else {
                    i = maxPage;
                    if (allAppsItemInfo.screenId == i4 && (i2 = (allAppsItemInfo.cellY * getCurrentCountInfo().countInfo[0]) + allAppsItemInfo.cellX) < arrayList.size()) {
                        if (arrayList.get(i2) == null) {
                            arrayList.set((allAppsItemInfo.cellY * getCurrentCountInfo().countInfo[0]) + allAppsItemInfo.cellX, allAppsItemInfo);
                            long j = i5;
                            if (j < allAppsItemInfo.screenId) {
                                int i11 = (int) allAppsItemInfo.screenId;
                                i6 = allAppsItemInfo.cellX;
                                i7 = allAppsItemInfo.cellY;
                                i5 = i11;
                            } else if (j == allAppsItemInfo.screenId) {
                                if (i7 < allAppsItemInfo.cellY) {
                                    i6 = allAppsItemInfo.cellX;
                                    i7 = allAppsItemInfo.cellY;
                                } else if (i7 == allAppsItemInfo.cellY) {
                                    i6 = allAppsItemInfo.cellX;
                                }
                            }
                        } else {
                            arrayList2.add(i8, allAppsItemInfo);
                            i8++;
                            LGLog.w("menuItemFactory", "removeDuplicateIcon : Duplicate cell!!", new int[0]);
                        }
                    }
                }
                i10++;
                allAppsItemFactory = this;
                maxPage = i;
            }
            arrayList.clear();
            i4++;
            allAppsItemFactory = this;
        }
        int i12 = 0;
        while (i12 < arrayList2.size()) {
            LGLog.w("AllAppsItemFactory", "removeDuplicateIcon : Duplicate item reordering!!", new int[i3]);
            AllAppsItemInfo allAppsItemInfo2 = (AllAppsItemInfo) arrayList2.get(i12);
            CharSequence charSequence = allAppsItemInfo2.title;
            LGLog.w("AllAppsItemFactory", "[Before]Duplicate item=" + ((Object) charSequence) + " page=" + allAppsItemInfo2.screenId + " cellX=" + allAppsItemInfo2.cellX + " cellY=" + allAppsItemInfo2.cellY, new int[i3]);
            if (i6 == getCurrentCountInfo().countInfo[i3] - 1 && i7 == getCurrentCountInfo().countInfo[1] - 1) {
                allAppsItemInfo2.screenId = i5 + 1;
                allAppsItemInfo2.cellX = i3;
                allAppsItemInfo2.cellY = i3;
            } else if (i6 == getCurrentCountInfo().countInfo[i3] - 1) {
                allAppsItemInfo2.screenId = i5;
                allAppsItemInfo2.cellX = i3;
                allAppsItemInfo2.cellY = i7 + 1;
            } else {
                allAppsItemInfo2.screenId = i5;
                allAppsItemInfo2.cellX = i6 + 1;
                allAppsItemInfo2.cellY = i7;
            }
            i5 = (int) allAppsItemInfo2.screenId;
            i6 = allAppsItemInfo2.cellX;
            i7 = allAppsItemInfo2.cellY;
            CharSequence charSequence2 = allAppsItemInfo2.title;
            LGLog.w("menuItemFactory", "[After]Duplicate item=" + ((Object) charSequence2) + " page=" + allAppsItemInfo2.screenId + " cellX=" + allAppsItemInfo2.cellX + " cellY=" + allAppsItemInfo2.cellY, new int[0]);
            i12++;
            i3 = 0;
        }
    }

    public void convertDatabaseLayout(ArrayList<AllAppsItemInfo> menuItemInfos) {
        if (menuItemInfos != null) {
            long j = 0;
            for (AllAppsItemInfo allAppsItemInfo : menuItemInfos) {
                allAppsItemInfo.screenId = j / ((long) getCurrentCountInfo().maxChildCount);
                allAppsItemInfo.cellX = (int) ((j - (((long) getCurrentCountInfo().maxChildCount) * allAppsItemInfo.screenId)) % ((long) getCurrentCountInfo().countInfo[0]));
                allAppsItemInfo.cellY = (int) ((j - (((long) getCurrentCountInfo().maxChildCount) * allAppsItemInfo.screenId)) / ((long) getCurrentCountInfo().countInfo[0]));
                j++;
            }
        }
    }

    public int getLoadFromDbStatus() {
        return this.mLoadFromDbStatus;
    }

    public void bulkInsertItems(ArrayList<AllAppsItemInfo> tempAddItems) {
        getCurrentDBLoader().bulkInsertItemsForNewApps(tempAddItems);
    }

    public void addNewAppInfosAndSort(ArrayList<AppInfo> remainApps) {
        ArrayList<AllAppsItemInfo> arrayList = new ArrayList<>();
        getCurrentDBLoader().loadMenuItemInfo(arrayList);
        HashMap<CacheKey, AppInfo> map = new HashMap<>();
        for (AppInfo appInfo : remainApps) {
            map.put(CacheKey.createKey(appInfo), appInfo);
        }
        addNewApplicationToLastPage(map, arrayList);
        rearrangeBySortType();
    }

    public AllAppsItemInfo updateMenuItemInfo(AppInfo appInfo) {
        AllAppsItemInfo allAppsItemInfoFindMenuItemInfoByAppInfo = findMenuItemInfoByAppInfo(appInfo);
        if (allAppsItemInfoFindMenuItemInfoByAppInfo == null) {
            return null;
        }
        boolean z = !allAppsItemInfoFindMenuItemInfoByAppInfo.title.equals(appInfo.title);
        allAppsItemInfoFindMenuItemInfoByAppInfo.copyFrom(appInfo);
        if (z) {
            getCurrentDBLoader().updateAppInfo(allAppsItemInfoFindMenuItemInfoByAppInfo);
        }
        return allAppsItemInfoFindMenuItemInfoByAppInfo;
    }

    public int[] getCellCount() {
        return getCurrentCountInfo().countInfo;
    }

    public void addFolderItem(FolderInfo info, ShortcutInfo item) {
        item.container = info.id;
        getCurrentDBLoader().addFolderItem(item);
        mFolderItems.add(item);
    }

    public AllAppsItemInfo addNewFolder(String title, int pageID, final int cellX, final int cellY) {
        AllAppsFolderInfo allAppsFolderInfo = new AllAppsFolderInfo();
        allAppsFolderInfo.setTitle(this.mContext.getText(R.string.folder_name));
        allAppsFolderInfo.folderColor = this.mContext.getResources().getInteger(R.integer.lg_default_folder_color_index);
        AllAppsItemInfo allAppsItemInfo = new AllAppsItemInfo(allAppsFolderInfo);
        allAppsItemInfo.cellX = cellX;
        allAppsItemInfo.cellY = cellY;
        allAppsItemInfo.screenId = pageID;
        getCurrentDBLoader().insertItem(allAppsItemInfo);
        this.mAllAppsItems.add(allAppsItemInfo);
        return allAppsItemInfo;
    }

    public void removeFolderItem(FolderInfo info, ShortcutInfo item) {
        mFolderItems.remove(item);
        getCurrentDBLoader().deleteFolderItem(item);
    }

    public void removeFolderItems(FolderInfo info, ArrayList<ShortcutInfo> items) {
        info.remove(items);
        for (ShortcutInfo shortcutInfo : items) {
            mFolderItems.remove(shortcutInfo);
            getCurrentDBLoader().deleteFolderItem(shortcutInfo);
        }
    }

    public void moveItemInDatabase(ShortcutInfo item, long container, int cellX, int cellY) {
        item.cellX = cellX;
        item.cellY = cellY;
        getCurrentDBLoader().moveFolderItemInDatabase(item);
    }

    public void moveItemsInDatabase(ArrayList<ItemInfo> items, long container, int screen) {
        for (ItemInfo itemInfo : items) {
            itemInfo.container = container;
            itemInfo.screenId = screen;
            getCurrentDBLoader().moveFolderItemInDatabase(itemInfo);
        }
    }

    public boolean hasFolder(AllAppsItemInfo folderItemInfo) {
        return (folderItemInfo == null || folderItemInfo.mFolderInfo == null || getCurrentDBLoader().getFolders().get(folderItemInfo.mFolderInfo.id) == null) ? false : true;
    }

    public void updateFolderOption(Context context, FolderInfo item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("options", Integer.valueOf(item.options));
        getCurrentDBLoader().updateItemInDatabase(contentValues, item);
    }

    public int getIndexOfAllAppsItemInfoByShortcutInfo(ShortcutInfo shortcutInfo) {
        int size = this.mAllAppsItems.size();
        if (shortcutInfo == null) {
            return -1;
        }
        for (int i = 0; i < size; i++) {
            if (this.mAllAppsItems.get(i) != null && this.mAllAppsItems.get(i).componentName != null && this.mAllAppsItems.get(i).componentName.equals(shortcutInfo.getTargetComponent())) {
                return i;
            }
        }
        return -1;
    }

    public AllAppsItemInfo getAllAppsItemInfoByShortcutInfo(ShortcutInfo shortcutInfo) {
        int size = this.mAllAppsItems.size();
        if (shortcutInfo == null) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            if (this.mAllAppsItems.get(i) != null && this.mAllAppsItems.get(i).componentName != null && this.mAllAppsItems.get(i).componentName.equals(shortcutInfo.getTargetComponent())) {
                return this.mAllAppsItems.get(i);
            }
        }
        return null;
    }

    public void setFolderListener(IAllAppsFolderListener listener) {
        this.mAllAppsFolderListener = listener;
    }

    public void removeAllAppsEmptyScreenModel() {
        IAllAppsFolderListener iAllAppsFolderListener = this.mAllAppsFolderListener;
        if (iAllAppsFolderListener != null) {
            iAllAppsFolderListener.removeVacantPage();
        }
    }

    public void moveFolderItemsToAllApps(final FolderInfo info, final ArrayList<ShortcutInfo> shortcutInfos) {
        if (this.mAllAppsFolderListener != null) {
            runOnMainThread(new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsItemFactory.2
                @Override // java.lang.Runnable
                public void run() {
                    AllAppsItemFactory.this.removeFolderItems(info, shortcutInfos);
                    AllAppsItemFactory.this.mAllAppsFolderListener.addItemsInAllApps(shortcutInfos);
                }
            });
        }
    }

    public void moveItemsToFolder(final ArrayList<ShortcutInfo> items, final FolderInfo target) {
        if (items == null || items.isEmpty() || target == null) {
            return;
        }
        runOnMainThread(new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsItemFactory.3
            @Override // java.lang.Runnable
            public void run() {
                AllAppsItemFactory.this.mAllAppsFolderListener.bindAppsMoved(items, target);
                target.add(items);
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

    private static void runOnWorkerThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
    }

    public FolderInfo getFolderInfoByName(String folderName) {
        if (folderName == null) {
            return null;
        }
        for (AllAppsItemInfo allAppsItemInfo : this.mAllAppsItems) {
            if (allAppsItemInfo != null && allAppsItemInfo.itemType == 2 && folderName.equals(allAppsItemInfo.getFolderInfo().title.toString())) {
                return allAppsItemInfo.getFolderInfo();
            }
        }
        return null;
    }

    public FolderInfo getFolderInfoById(long id) {
        return getCurrentDBLoader().getFolders().get(id);
    }

    public AllAppsDBLoader getDBLoader(boolean isSwivel) {
        LGLog.d("AllAppsItemFactory", "[ALLAPPS_DB]getDBLoader = " + isSwivel);
        if (!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            return this.mAllAppsDBLoader;
        }
        if (isSwivel) {
            return this.mSwivelAllAppsDBLoader;
        }
        return this.mAllAppsDBLoader;
    }

    public AllAppsDBLoader getCurrentDBLoader() {
        return getDBLoader(this.mUseSwivelDB);
    }

    public void setUseSwivelDB(boolean z, String str) {
        this.mUseSwivelDB = z;
        this.mCurrentId = z ? 1 : 0;
        LGLog.i("AllAppsItemFactory", "[ALLAPPS_DB]setUseSwivelDB = " + z + ", caller = " + str);
    }

    public static boolean isManagedProfileItem(Context context, AppInfo appInfo) {
        if (context == null || appInfo == null || appInfo.user == null) {
            return false;
        }
        return ManagedProfileUtils.hasProfileOwnerAsUser(context, appInfo.user.getIdentifier());
    }

    public static ArrayList<AllAppsItemInfo> getManagedProfilesItems(final Context context, ArrayList<AllAppsItemInfo> items, boolean isManagedProfile) {
        if (isManagedProfile) {
            return (ArrayList) items.stream().filter(new Predicate() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsItemFactory$_rlfOZbhfqMLqMHMbIaBUIW5yes
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return AllAppsItemFactory.isManagedProfileItem(context, (AllAppsItemInfo) obj);
                }
            }).collect(Collectors.toCollection(new Supplier() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsItemFactory$E2McIAGPWBO62AkYPcfatUmDGDA
                @Override // java.util.function.Supplier
                public final Object get() {
                    return AllAppsItemFactory.lambda$E2McIAGPWBO62AkYPcfatUmDGDA();
                }
            }));
        }
        return (ArrayList) items.stream().filter(new Predicate() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsItemFactory$LkKjs7Zx7cNlb_h1jJOlllX8z8g
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return AllAppsItemFactory.lambda$getManagedProfilesItems$1(context, (AllAppsItemInfo) obj);
            }
        }).collect(Collectors.toCollection(new Supplier() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsItemFactory$E2McIAGPWBO62AkYPcfatUmDGDA
            @Override // java.util.function.Supplier
            public final Object get() {
                return AllAppsItemFactory.lambda$E2McIAGPWBO62AkYPcfatUmDGDA();
            }
        }));
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: NOT 
      (wrap:boolean:0x0000: INVOKE (r0v0 android.content.Context), (r1v0 com.lge.launcher3.allapps.AllAppsItemInfo) STATIC call: com.lge.launcher3.allapps.AllAppsItemFactory.isManagedProfileItem(android.content.Context, com.android.launcher3.model.data.AppInfo):boolean A[DONT_GENERATE, MD:(android.content.Context, com.android.launcher3.model.data.AppInfo):boolean (m), REMOVE, WRAPPED] (LINE:1640))
     */
    static /* synthetic */ boolean lambda$getManagedProfilesItems$1(Context context, AllAppsItemInfo allAppsItemInfo) {
        return !isManagedProfileItem(context, allAppsItemInfo);
    }

    public int getManagedProfileStartPage() {
        return ((Integer) this.mAllAppsItems.stream().filter(new Predicate() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsItemFactory$ylkovObdBm23hQZWs3MkE_b9_HM
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f$0.lambda$getManagedProfileStartPage$2$AllAppsItemFactory((AllAppsItemInfo) obj);
            }
        }).findFirst().map(new Function() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsItemFactory$kM-SndKnauCHO4V0oon5zBsnhwI
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf((int) ((AllAppsItemInfo) obj).screenId);
            }
        }).orElse(-1)).intValue();
    }

    public /* synthetic */ boolean lambda$getManagedProfileStartPage$2$AllAppsItemFactory(AllAppsItemInfo allAppsItemInfo) {
        return isManagedProfileItem(this.mContext, allAppsItemInfo);
    }

    public void updateManagedProfileItemScreenId() {
        ArrayList<AllAppsItemInfo> arrayList = new ArrayList<>();
        for (int i = 0; i < this.mAllAppsItems.size(); i++) {
            AllAppsItemInfo allAppsItemInfo = this.mAllAppsItems.get(i);
            if (isManagedProfileItem(this.mContext, this.mAllAppsItems.get(i))) {
                allAppsItemInfo.screenId++;
                allAppsItemInfo.requiresDbUpdate = true;
                this.mAllAppsItems.set(i, allAppsItemInfo);
                arrayList.add(allAppsItemInfo);
            }
        }
        getCurrentDBLoader().bulkUpdateItemPositionByID(arrayList);
    }
}
