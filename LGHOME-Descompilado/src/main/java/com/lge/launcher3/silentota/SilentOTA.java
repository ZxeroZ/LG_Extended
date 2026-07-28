package com.lge.launcher3.silentota;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.model.data.FolderInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsDBAdapter;
import com.lge.launcher3.allapps.AllAppsDBProvider;
import com.lge.launcher3.allapps.AllAppsItemFactory;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.receiver.DefaultWorkspaceLoader;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class SilentOTA {
    private static final String TAG = "SilentOTA";
    private ArrayList<ShortcutInfo> mAllAppsFolderApps;
    private AllAppsFolderLayoutParser mAllAppsFolderLayout;
    private final Context mContext;
    private ArrayList<ShortcutInfo> mFolderApps;
    private FolderLayoutParser mFolderLayout;
    private LauncherModel mModel;
    private final HashSet<String> mOTAPackages;
    private final UserHandle mUser;

    public SilentOTA(Context context, UserHandle user) {
        HashSet<String> addedPackage;
        this.mContext = context;
        this.mUser = user;
        HashSet<String> hashSet = new HashSet<>(Arrays.asList(context.getResources().getStringArray(R.array.silent_ota_packages)));
        this.mOTAPackages = hashSet;
        if (LGHomeFeature.Config.FEATURE_USE_SILENT_OTA_EXTENSION.getValue() && (addedPackage = SilentOTA_Extension.getAddedPackage(context)) != null && addedPackage.size() > 0) {
            int size = hashSet.size();
            hashSet.addAll(addedPackage);
            LGLog.i(TAG, "[SilentOTA_Extension] SilentOTA: added size = " + hashSet.size() + ", original size = " + size);
        }
        this.mModel = LauncherAppState.getInstance(context).getModel();
    }

    public boolean isSilentOTAPackage(String packageName) {
        if (packageName != null) {
            for (String str : this.mOTAPackages) {
                if (packageName.equals(str)) {
                    if (!SilentOTA_Extension.getAddedPromisingPackage().contains(str)) {
                        return true;
                    }
                    LGLog.i(TAG, "[SilentOTA_Extension] SilentOTA: " + packageName + " is in promising apps list, so ignore it");
                    return false;
                }
            }
        }
        return false;
    }

    private void parseLayout() {
        if (this.mFolderLayout == null) {
            int iDFromCAList = DefaultWorkspaceLoader.getIDFromCAList(this.mContext);
            if (iDFromCAList == 0) {
                iDFromCAList = LauncherAppState.getInstance(this.mContext).getInvariantDeviceProfile().defaultLayoutId;
            }
            Context context = this.mContext;
            FolderLayoutParser folderLayoutParser = new FolderLayoutParser(context, context.getResources(), iDFromCAList);
            this.mFolderLayout = folderLayoutParser;
            try {
                folderLayoutParser.parseLayout();
            } catch (Exception e) {
                LGLog.e(TAG, e.toString());
            }
        }
    }

    private void parseLayoutAllApps() throws Throwable {
        if (LGHomeFeature.isEnableDefaultHome() || this.mAllAppsFolderLayout != null) {
            return;
        }
        AllAppsFolderLayoutParser allAppsFolderLayoutParser = new AllAppsFolderLayoutParser(this.mContext);
        this.mAllAppsFolderLayout = allAppsFolderLayoutParser;
        allAppsFolderLayoutParser.parseLayout();
    }

    private int getAllAppsDBItemCount() {
        Cursor cursorQuery = this.mContext.getContentResolver().query(AllAppsDBProvider.getContentPageMenuChildURi(false), null, null, null, null);
        if (cursorQuery == null) {
            return 0;
        }
        int count = cursorQuery.getCount();
        cursorQuery.close();
        return count;
    }

    private int getAllAppsDBId(Uri uri, String key, String value) {
        Cursor cursorQuery = this.mContext.getContentResolver().query(uri, null, key + " = ?", new String[]{value}, null);
        int i = -1;
        if (cursorQuery != null) {
            if (cursorQuery.getCount() > 0) {
                cursorQuery.moveToFirst();
                i = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
            }
            cursorQuery.close();
        }
        return i;
    }

    public void addAllAppsFolderItem(ShortcutInfo itemInfo) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i(TAG, "[insert] can not Access");
            return;
        }
        ContentValues contentValues = new ContentValues();
        AllAppsDBAdapter allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mContext.getContentResolver(), AllAppsDBProvider.getContentPageMenuFolderUri(false));
        if (allAppsDBAdapter != null) {
            contentValues.put("component_name", itemInfo.getIntent().getComponent().flattenToShortString());
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_PAGE_ID, Long.valueOf(itemInfo.screenId));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(itemInfo.cellX));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(itemInfo.cellY));
            contentValues.put("title", String.valueOf(itemInfo.title));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE, Integer.valueOf(itemInfo.itemType));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER, Long.valueOf(itemInfo.container));
            contentValues.put("profileId", Long.valueOf(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(itemInfo.user)));
            contentValues.put("rank", Integer.valueOf(itemInfo.rank));
            if (allAppsDBAdapter.insert(AllAppsDBProvider.getContentPageMenuFolderUri(false), contentValues) != null) {
                itemInfo.id = Integer.parseInt(r0.getLastPathSegment());
            }
        }
    }

    public HashSet<SilentAppInfo> processPackageAdd(String[] packages) {
        this.mFolderApps = new ArrayList<>();
        this.mAllAppsFolderApps = new ArrayList<>();
        HashSet<SilentAppInfo> hashSet = new HashSet<>();
        LauncherAppsCompat launcherAppsCompat = LauncherAppsCompat.getInstance(this.mContext);
        boolean z = false;
        for (String str : packages) {
            if (isSilentOTAPackage(str)) {
                LGLog.i(TAG, "SilentOTA package: " + str);
                parseLayout();
                for (LauncherActivityInfo launcherActivityInfo : launcherAppsCompat.getActivityList(str, this.mUser)) {
                    String folderName = this.mFolderLayout.getFolderName(launcherActivityInfo.getComponentName());
                    SilentAppInfo favoriteInfo = this.mFolderLayout.getFavoriteInfo(launcherActivityInfo.getComponentName());
                    ShortcutInfo shortcutInfoFromActivityInfo = ShortcutInfo.fromActivityInfo(launcherActivityInfo, this.mContext);
                    FolderInfo lGFolderInWorkspace = LauncherModel.getLGFolderInWorkspace(folderName);
                    if (lGFolderInWorkspace != null) {
                        shortcutInfoFromActivityInfo.container = lGFolderInWorkspace.id;
                        this.mFolderApps.add(shortcutInfoFromActivityInfo);
                        favoriteInfo.setFolderInfo(lGFolderInWorkspace);
                        hashSet.add(favoriteInfo);
                        z = true;
                    } else if (favoriteInfo != null && favoriteInfo.getFolderTitle() == null) {
                        hashSet.add(favoriteInfo);
                    }
                    LGLog.i(TAG, "Workspace, folderName = " + folderName + ": folderInfo  = " + lGFolderInWorkspace + " <- si(" + shortcutInfoFromActivityInfo + "), SilentAppInfo = " + favoriteInfo);
                }
            }
        }
        if (z) {
            finalizeWorkFolder();
        }
        return hashSet;
    }

    private void finalizeWorkFolder() {
        if (this.mFolderApps.isEmpty()) {
            return;
        }
        for (ShortcutInfo shortcutInfo : this.mFolderApps) {
            LauncherModel.addItemToDatabase(this.mContext, shortcutInfo, shortcutInfo.container, 0L, 0, 0);
        }
        new MainThreadExecutor().execute(new Runnable() { // from class: com.lge.launcher3.silentota.SilentOTA.1
            @Override // java.lang.Runnable
            public void run() {
                for (ShortcutInfo shortcutInfo2 : SilentOTA.this.mFolderApps) {
                    FolderInfo folderInfoFindFolderById = SilentOTA.this.mModel.findFolderById(Long.valueOf(shortcutInfo2.container));
                    if (folderInfoFindFolderById != null) {
                        LauncherModel.Callbacks callback = SilentOTA.this.mModel.getCallback();
                        if (callback != null) {
                            callback.bindSilentAppsInFolderUpdated(folderInfoFindFolderById, shortcutInfo2);
                        }
                    } else {
                        LGLog.i(SilentOTA.TAG, "folder not found : " + shortcutInfo2);
                    }
                }
            }
        });
    }

    public String[] processPackageAddAllApps(String[] packages) {
        this.mAllAppsFolderApps = new ArrayList<>();
        HashSet hashSet = new HashSet();
        LauncherAppsCompat launcherAppsCompat = LauncherAppsCompat.getInstance(this.mContext);
        int length = packages.length;
        boolean z = false;
        int i = 0;
        boolean z2 = false;
        while (i < length) {
            String str = packages[i];
            if (isSilentOTAPackage(str)) {
                LGLog.i(TAG, "SilentOTA package: " + str);
                if (!hashSet.contains(str)) {
                    parseLayoutAllApps();
                    for (LauncherActivityInfo launcherActivityInfo : launcherAppsCompat.getActivityList(str, this.mUser)) {
                        ShortcutInfo shortcutInfoFromActivityInfo = ShortcutInfo.fromActivityInfo(launcherActivityInfo, this.mContext);
                        String folderName = this.mAllAppsFolderLayout.getFolderName(launcherActivityInfo.getComponentName());
                        if (AllAppsItemFactory.getInstance() != null) {
                            FolderInfo folderInfoByName = AllAppsItemFactory.getInstance().getFolderInfoByName(folderName);
                            if (folderInfoByName != null) {
                                shortcutInfoFromActivityInfo.container = folderInfoByName.id;
                                this.mAllAppsFolderApps.add(shortcutInfoFromActivityInfo);
                                hashSet.add(str);
                                z2 = true;
                            }
                            LGLog.i(TAG, "AppDrawer, " + folderName + ":" + folderInfoByName + " <- " + shortcutInfoFromActivityInfo);
                        } else if (getAllAppsDBItemCount() > 0) {
                            int allAppsDBId = getAllAppsDBId(AllAppsDBProvider.getContentPageMenuChildURi(z), "title", folderName);
                            int allAppsDBId2 = getAllAppsDBId(AllAppsDBProvider.getContentPageMenuFolderUri(z), "component_name", launcherActivityInfo.getComponentName().flattenToShortString());
                            if (allAppsDBId > 0 && allAppsDBId2 == -1) {
                                shortcutInfoFromActivityInfo.container = allAppsDBId;
                                addAllAppsFolderItem(shortcutInfoFromActivityInfo);
                                LGLog.i(TAG, "AllAppsDB update, folderId = " + allAppsDBId + ", id = " + shortcutInfoFromActivityInfo.id);
                            } else {
                                LGLog.i(TAG, "Skip, folderId = " + allAppsDBId + ", id = " + allAppsDBId2);
                            }
                        } else {
                            LGLog.i(TAG, "Skip, AllAppsDB is empty");
                        }
                        z = false;
                    }
                }
            }
            i++;
            z = false;
        }
        if (z2) {
            finalizeWorkFolderAllApps();
        }
        return (String[]) hashSet.toArray(new String[hashSet.size()]);
    }

    private void finalizeWorkFolderAllApps() {
        if (this.mAllAppsFolderApps.isEmpty()) {
            return;
        }
        new MainThreadExecutor().execute(new Runnable() { // from class: com.lge.launcher3.silentota.SilentOTA.2
            @Override // java.lang.Runnable
            public void run() {
                for (ShortcutInfo shortcutInfo : SilentOTA.this.mAllAppsFolderApps) {
                    FolderInfo folderInfoById = AllAppsItemFactory.getInstance().getFolderInfoById(shortcutInfo.container);
                    if (folderInfoById != null) {
                        folderInfoById.add(shortcutInfo);
                    } else {
                        LGLog.i(SilentOTA.TAG, "folder not found : " + shortcutInfo);
                    }
                }
            }
        });
    }
}
