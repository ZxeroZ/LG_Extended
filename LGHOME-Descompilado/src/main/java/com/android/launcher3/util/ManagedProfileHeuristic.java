package com.android.launcher3.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.AppFilter;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.UserUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ManagedProfileHeuristic {
    private static final long AUTO_ADD_TO_FOLDER_DURATION = 28800000;
    private static final String INSTALLED_PACKAGES_PREFIX = "installed_packages_for_user_";
    private static final String TAG = "ManagedProfileHeuristic";
    private static final String USER_FOLDER_ID_PREFIX = "user_folder_";
    private final AppFilter mAppFilter;
    private final Context mContext;
    private ArrayList<ShortcutInfo> mHomescreenApps;
    private final LauncherModel mModel;
    private final String mPackageSetKey;
    private final SharedPreferences mPrefs;
    private final UserHandle mUser;
    private final long mUserCreationTime;
    private final long mUserSerial;
    private ArrayList<ShortcutInfo> mWorkFolderApps;

    public static ManagedProfileHeuristic get(Context context, UserHandle user) {
        if (!Utilities.isLmpOrAbove() || Process.myUserHandle().equals(user)) {
            return null;
        }
        return new ManagedProfileHeuristic(context, user);
    }

    private ManagedProfileHeuristic(Context context, UserHandle user) {
        this.mContext = context;
        this.mUser = user;
        this.mModel = LauncherAppState.getInstance(context).getModel();
        UserManagerCompat userManagerCompat = UserManagerCompat.getInstance(context);
        long serialNumberForUser = userManagerCompat.getSerialNumberForUser(user);
        this.mUserSerial = serialNumberForUser;
        this.mUserCreationTime = userManagerCompat.getUserCreationTime(user);
        this.mPackageSetKey = INSTALLED_PACKAGES_PREFIX + serialNumberForUser;
        this.mPrefs = context.getSharedPreferences(LauncherFiles.MANAGED_USER_PREFERENCES_KEY, 0);
        this.mAppFilter = (AppFilter) Utilities.getOverrideObject(AppFilter.class, context, R.string.app_filter_class);
    }

    public void processUserApps(List<LauncherActivityInfo> apps) {
        this.mHomescreenApps = new ArrayList<>();
        this.mWorkFolderApps = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        boolean userApps = getUserApps(hashSet);
        boolean z = false;
        for (LauncherActivityInfo launcherActivityInfo : apps) {
            String packageName = launcherActivityInfo.getComponentName().getPackageName();
            if (!hashSet.contains(packageName)) {
                hashSet.add(packageName);
                z = true;
            }
            try {
                markForAddition(launcherActivityInfo, this.mContext.getPackageManager().getPackageInfo(packageName, 8192).firstInstallTime);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Unknown package " + packageName, e);
            }
        }
        if (z) {
            this.mPrefs.edit().putStringSet(this.mPackageSetKey, hashSet).apply();
            finalizeAdditions(userApps);
        }
    }

    private void markForAddition(LauncherActivityInfo info, long installTime) {
        (installTime <= this.mUserCreationTime + AUTO_ADD_TO_FOLDER_DURATION ? this.mWorkFolderApps : this.mHomescreenApps).add(ShortcutInfo.fromActivityInfo(info, this.mContext));
    }

    private void finalizeWorkFolder() {
        if (LGHomeFeature.isDisableAllApps() && LGHomeFeature.isDisableEasyHome() && !this.mWorkFolderApps.isEmpty()) {
            Collections.sort(this.mWorkFolderApps, new Comparator<ShortcutInfo>() { // from class: com.android.launcher3.util.ManagedProfileHeuristic.1
                /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
                @Override // java.util.Comparator
                public int compare(ShortcutInfo lhs, ShortcutInfo rhs) {
                    return Long.compare(lhs.firstInstallTime, rhs.firstInstallTime);
                }
            });
            String str = USER_FOLDER_ID_PREFIX + this.mUserSerial;
            if (this.mPrefs.contains(str)) {
                long j = this.mPrefs.getLong(str, 0L);
                final FolderInfo folderInfoFindFolderById = this.mModel.findFolderById(Long.valueOf(j));
                if (folderInfoFindFolderById == null) {
                    Log.d(TAG, "workFolder == null");
                    createWorkFolder();
                    return;
                } else if (!folderInfoFindFolderById.hasOption(2)) {
                    Log.d(TAG, "Could not get a work folder. Add all the icons to homescreen");
                    this.mHomescreenApps.addAll(this.mWorkFolderApps);
                    return;
                } else {
                    saveWorkFolderShortcuts(j, folderInfoFindFolderById.contents.size());
                    final ArrayList<ShortcutInfo> arrayList = this.mWorkFolderApps;
                    new MainThreadExecutor().execute(new Runnable() { // from class: com.android.launcher3.util.ManagedProfileHeuristic.2
                        @Override // java.lang.Runnable
                        public void run() {
                            if (LGHomeFeature.Config.FEATURE_USE_DUAL_APP.getValue()) {
                                for (ShortcutInfo shortcutInfo : arrayList) {
                                    if (UserUtils.isSecondApplication(ManagedProfileHeuristic.this.mContext, shortcutInfo.user.getIdentifier())) {
                                        ManagedProfileHeuristic.this.mHomescreenApps.add(shortcutInfo);
                                    } else {
                                        folderInfoFindFolderById.add(shortcutInfo);
                                    }
                                }
                                return;
                            }
                            for (ShortcutInfo shortcutInfo2 : arrayList) {
                                if (ManagedProfileHeuristic.this.mAppFilter == null || ManagedProfileHeuristic.this.mAppFilter.shouldShowApp(shortcutInfo2.getTargetComponent(), shortcutInfo2.user)) {
                                    folderInfoFindFolderById.add(shortcutInfo2);
                                } else {
                                    Log.w(ManagedProfileHeuristic.TAG, "finalizeWorkFolder(): no showing app = " + shortcutInfo2.getTargetComponent());
                                }
                            }
                        }
                    });
                    return;
                }
            }
            createWorkFolder();
        }
    }

    private void createWorkFolder() {
        Log.d(TAG, "Create a new folder.");
        FolderInfo folderInfo = new FolderInfo();
        folderInfo.title = UserUtils.getWorkProfileFolderName();
        folderInfo.setOption(2, true, null);
        folderInfo.user = this.mUser;
        if (LGHomeFeature.Config.FEATURE_USE_DUAL_APP.getValue()) {
            boolean z = false;
            for (ShortcutInfo shortcutInfo : this.mWorkFolderApps) {
                if (!UserUtils.isSecondApplication(this.mContext, shortcutInfo.user.getIdentifier())) {
                    folderInfo.add(shortcutInfo);
                } else {
                    this.mHomescreenApps.add(shortcutInfo);
                    z = true;
                }
            }
            if (folderInfo.contents.size() != 0) {
                ArrayList<? extends ItemInfo> arrayList = new ArrayList<>(1);
                arrayList.add(folderInfo);
                this.mModel.addAndBindAddedWorkspaceItems(this.mContext, arrayList);
                this.mPrefs.edit().putLong(USER_FOLDER_ID_PREFIX + this.mUserSerial, folderInfo.id).apply();
                saveWorkFolderShortcuts(folderInfo.id, 0);
            }
            if (z) {
                this.mModel.addAndBindAddedWorkspaceItems(this.mContext, this.mHomescreenApps);
            }
        } else {
            Iterator<ShortcutInfo> it = this.mWorkFolderApps.iterator();
            while (it.hasNext()) {
                folderInfo.add(it.next());
            }
            ArrayList<? extends ItemInfo> arrayList2 = new ArrayList<>(1);
            arrayList2.add(folderInfo);
            this.mModel.addAndBindAddedWorkspaceItems(this.mContext, arrayList2);
            this.mPrefs.edit().putLong(USER_FOLDER_ID_PREFIX + this.mUserSerial, folderInfo.id).apply();
            saveWorkFolderShortcuts(folderInfo.id, 0);
        }
        UserUtils.setWorkProfileInfo(this.mUserSerial, folderInfo.id);
    }

    private void saveWorkFolderShortcuts(long workFolderId, int startingRank) {
        for (ShortcutInfo shortcutInfo : this.mWorkFolderApps) {
            shortcutInfo.rank = startingRank;
            LauncherModel.addItemToDatabase(this.mContext, shortcutInfo, workFolderId, 0L, 0, 0);
            startingRank++;
        }
    }

    private void finalizeAdditions(boolean addHomeScreenShortcuts) {
        finalizeWorkFolder();
        if (!addHomeScreenShortcuts || this.mHomescreenApps.isEmpty()) {
            return;
        }
        this.mModel.addAndBindAddedWorkspaceItems(this.mContext, this.mHomescreenApps);
    }

    public String[] processPackageAdd(String[] packages) {
        this.mHomescreenApps = new ArrayList<>();
        this.mWorkFolderApps = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        boolean userApps = getUserApps(hashSet);
        long jCurrentTimeMillis = System.currentTimeMillis();
        LauncherAppsCompat launcherAppsCompat = LauncherAppsCompat.getInstance(this.mContext);
        boolean z = false;
        for (String str : packages) {
            if (!hashSet.contains(str)) {
                hashSet.add(str);
                List<LauncherActivityInfo> activityList = launcherAppsCompat.getActivityList(str, this.mUser);
                if (LGHomeFeature.isEnableDefaultHome()) {
                    Iterator<LauncherActivityInfo> it = activityList.iterator();
                    while (it.hasNext()) {
                        markForAddition(it.next(), jCurrentTimeMillis);
                    }
                } else if (!activityList.isEmpty()) {
                    markForAddition(activityList.get(0), jCurrentTimeMillis);
                }
                z = true;
            }
        }
        if (z) {
            this.mPrefs.edit().putStringSet(this.mPackageSetKey, hashSet).apply();
            finalizeAdditions(userApps);
        }
        return (String[]) hashSet.toArray(new String[hashSet.size()]);
    }

    public void processPackageRemoved(String[] packages) {
        HashSet<String> hashSet = new HashSet<>();
        getUserApps(hashSet);
        boolean z = false;
        for (String str : packages) {
            if (hashSet.remove(str)) {
                z = true;
            }
        }
        if (z) {
            this.mPrefs.edit().putStringSet(this.mPackageSetKey, hashSet).apply();
        }
    }

    private boolean getUserApps(HashSet<String> outExistingApps) {
        Set<String> stringSet = this.mPrefs.getStringSet(this.mPackageSetKey, null);
        if (stringSet == null) {
            return false;
        }
        outExistingApps.addAll(stringSet);
        return true;
    }

    public static void processAllUsers(List<UserHandle> users, Context context) {
        if (Utilities.isLmpOrAbove()) {
            UserManagerCompat userManagerCompat = UserManagerCompat.getInstance(context);
            HashSet hashSet = new HashSet();
            Iterator<UserHandle> it = users.iterator();
            while (it.hasNext()) {
                addAllUserKeys(userManagerCompat.getSerialNumberForUser(it.next()), hashSet);
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences(LauncherFiles.MANAGED_USER_PREFERENCES_KEY, 0);
            SharedPreferences.Editor editorEdit = sharedPreferences.edit();
            for (String str : sharedPreferences.getAll().keySet()) {
                if (!hashSet.contains(str)) {
                    editorEdit.remove(str);
                }
            }
            editorEdit.apply();
        }
    }

    private static void addAllUserKeys(long userSerial, HashSet<String> keysOut) {
        keysOut.add(INSTALLED_PACKAGES_PREFIX + userSerial);
        keysOut.add(USER_FOLDER_ID_PREFIX + userSerial);
    }

    public static void markExistingUsersForNoFolderCreation(Context context) {
        UserManagerCompat userManagerCompat = UserManagerCompat.getInstance(context);
        UserHandle userHandleMyUserHandle = Process.myUserHandle();
        SharedPreferences sharedPreferences = null;
        for (UserHandle userHandle : userManagerCompat.getUserProfiles()) {
            if (!userHandleMyUserHandle.equals(userHandle)) {
                if (sharedPreferences == null) {
                    sharedPreferences = context.getSharedPreferences(LauncherFiles.MANAGED_USER_PREFERENCES_KEY, 0);
                }
                String str = USER_FOLDER_ID_PREFIX + userManagerCompat.getSerialNumberForUser(userHandle);
                if (!sharedPreferences.contains(str)) {
                    sharedPreferences.edit().putLong(str, -1L).apply();
                }
            }
        }
    }
}
