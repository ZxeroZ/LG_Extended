package com.android.launcher3.icons.cache;

import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.android.launcher3.icons.cache.BaseIconCache;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/* JADX INFO: loaded from: classes.dex */
public class IconCacheUpdateHandler {
    private static final Object ICON_UPDATE_TOKEN = new Object();
    private static final boolean MODE_CLEAR_VALID_ITEMS = false;
    private static final boolean MODE_SET_INVALID_ITEMS = true;
    private static final String TAG = "IconCacheUpdateHandler";
    private final BaseIconCache mIconCache;
    private final ArrayMap<UserHandle, Set<String>> mPackagesToIgnore = new ArrayMap<>();
    private final SparseBooleanArray mItemsToDelete = new SparseBooleanArray();
    private boolean mFilterMode = true;
    private final HashMap<String, PackageInfo> mPkgInfoMap = new HashMap<>();

    public interface OnUpdateCallback {
        void onPackageIconsUpdated(HashSet<String> updatedPackages, UserHandle user);
    }

    IconCacheUpdateHandler(BaseIconCache cache) {
        this.mIconCache = cache;
        cache.mWorkerHandler.removeCallbacksAndMessages(ICON_UPDATE_TOKEN);
        createPackageInfoMap();
    }

    public void addPackagesToIgnore(UserHandle userHandle, String packageName) {
        Set<String> hashSet = this.mPackagesToIgnore.get(userHandle);
        if (hashSet == null) {
            hashSet = new HashSet<>();
            this.mPackagesToIgnore.put(userHandle, hashSet);
        }
        hashSet.add(packageName);
    }

    private void createPackageInfoMap() {
        for (PackageInfo packageInfo : this.mIconCache.mPackageManager.getInstalledPackages(8192)) {
            this.mPkgInfoMap.put(packageInfo.packageName, packageInfo);
        }
    }

    public <T> void updateIcons(List<T> apps, CachingLogic<T> cachingLogic, OnUpdateCallback onUpdateCallback) {
        HashMap map = new HashMap();
        int size = apps.size();
        for (int i = 0; i < size; i++) {
            T t = apps.get(i);
            UserHandle user = cachingLogic.getUser(t);
            HashMap map2 = (HashMap) map.get(user);
            if (map2 == null) {
                map2 = new HashMap();
                map.put(user, map2);
            }
            map2.put(cachingLogic.getComponent(t), t);
        }
        for (Map.Entry entry : map.entrySet()) {
            updateIconsPerUser((UserHandle) entry.getKey(), (HashMap) entry.getValue(), cachingLogic, onUpdateCallback);
        }
        this.mFilterMode = false;
    }

    /* JADX DEBUG: Another duplicated slice has different insns count: {[]}, finally: {[THROW, INVOKE, MOVE_EXCEPTION, THROW, INVOKE, MOVE_EXCEPTION, THROW, IF] complete} */
    /* JADX DEBUG: Finally have unexpected throw blocks count: 3, expect 1 */
    private <T> void updateIconsPerUser(UserHandle user, HashMap<ComponentName, T> componentMap, CachingLogic<T> cachingLogic, OnUpdateCallback onUpdateCallback) {
        int i;
        int i2;
        boolean z;
        Set<String> setEmptySet = this.mPackagesToIgnore.get(user);
        if (setEmptySet == null) {
            setEmptySet = Collections.emptySet();
        }
        long serialNumberForUser = this.mIconCache.getSerialNumberForUser(user);
        Stack stack = new Stack();
        try {
            Cursor cursorQuery = this.mIconCache.mIconDb.query(new String[]{BaseIconCache.IconDB.COLUMN_ROWID, "componentName", BaseIconCache.IconDB.COLUMN_LAST_UPDATED, BaseIconCache.IconDB.COLUMN_VERSION, BaseIconCache.IconDB.COLUMN_SYSTEM_STATE}, "profileId = ? ", new String[]{Long.toString(serialNumberForUser)});
            try {
                int columnIndex = cursorQuery.getColumnIndex("componentName");
                int columnIndex2 = cursorQuery.getColumnIndex(BaseIconCache.IconDB.COLUMN_LAST_UPDATED);
                int columnIndex3 = cursorQuery.getColumnIndex(BaseIconCache.IconDB.COLUMN_VERSION);
                int columnIndex4 = cursorQuery.getColumnIndex(BaseIconCache.IconDB.COLUMN_ROWID);
                int columnIndex5 = cursorQuery.getColumnIndex(BaseIconCache.IconDB.COLUMN_SYSTEM_STATE);
                while (cursorQuery.moveToNext()) {
                    ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(cursorQuery.getString(columnIndex));
                    PackageInfo packageInfo = this.mPkgInfoMap.get(componentNameUnflattenFromString.getPackageName());
                    int i3 = cursorQuery.getInt(columnIndex4);
                    if (packageInfo == null) {
                        if (!setEmptySet.contains(componentNameUnflattenFromString.getPackageName())) {
                            if (this.mFilterMode) {
                                this.mIconCache.remove(componentNameUnflattenFromString, user);
                                this.mItemsToDelete.put(i3, true);
                            }
                        }
                    } else if ((packageInfo.applicationInfo.flags & 16777216) == 0) {
                        long j = cursorQuery.getLong(columnIndex2);
                        int i4 = cursorQuery.getInt(columnIndex3);
                        int i5 = columnIndex3;
                        int i6 = columnIndex2;
                        T tRemove = componentMap.remove(componentNameUnflattenFromString);
                        if (i4 == packageInfo.versionCode) {
                            i = columnIndex;
                            i2 = columnIndex4;
                            if (j == packageInfo.lastUpdateTime && TextUtils.equals(cursorQuery.getString(columnIndex5), this.mIconCache.getIconSystemState(packageInfo.packageName))) {
                                if (this.mFilterMode) {
                                    columnIndex = i;
                                    columnIndex4 = i2;
                                    columnIndex3 = i5;
                                    columnIndex2 = i6;
                                } else {
                                    z = false;
                                    this.mItemsToDelete.put(i3, false);
                                    columnIndex4 = i2;
                                    columnIndex2 = i6;
                                    columnIndex = i;
                                    columnIndex3 = i5;
                                }
                            }
                        } else {
                            i = columnIndex;
                            i2 = columnIndex4;
                        }
                        z = false;
                        if (tRemove == null) {
                            if (this.mFilterMode) {
                                this.mIconCache.remove(componentNameUnflattenFromString, user);
                                this.mItemsToDelete.put(i3, true);
                            }
                        } else {
                            stack.add(tRemove);
                        }
                        columnIndex4 = i2;
                        columnIndex2 = i6;
                        columnIndex = i;
                        columnIndex3 = i5;
                    }
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } finally {
                if (componentMap.isEmpty()) {
                }
                Stack stack2 = new Stack();
                stack2.addAll(componentMap.values());
                new SerializedIconUpdateTask(serialNumberForUser, user, stack2, stack, cachingLogic, onUpdateCallback).scheduleNext();
            }
        } catch (SQLiteException e) {
            Log.d(TAG, "Error reading icon cache", e);
        }
        if (componentMap.isEmpty() || !stack.isEmpty()) {
            Stack stack22 = new Stack();
            stack22.addAll(componentMap.values());
            new SerializedIconUpdateTask(serialNumberForUser, user, stack22, stack, cachingLogic, onUpdateCallback).scheduleNext();
        }
    }

    public void finish() {
        StringBuilder sbAppend = new StringBuilder().append(BaseIconCache.IconDB.COLUMN_ROWID).append(" IN (");
        int size = this.mItemsToDelete.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (this.mItemsToDelete.valueAt(i2)) {
                if (i > 0) {
                    sbAppend.append(", ");
                }
                sbAppend.append(this.mItemsToDelete.keyAt(i2));
                i++;
            }
        }
        sbAppend.append(')');
        if (i > 0) {
            this.mIconCache.mIconDb.delete(sbAppend.toString(), null);
        }
    }

    private class SerializedIconUpdateTask<T> implements Runnable {
        private final Stack<T> mAppsToAdd;
        private final Stack<T> mAppsToUpdate;
        private final CachingLogic<T> mCachingLogic;
        private final OnUpdateCallback mOnUpdateCallback;
        private final HashSet<String> mUpdatedPackages = new HashSet<>();
        private final UserHandle mUserHandle;
        private final long mUserSerial;

        SerializedIconUpdateTask(long userSerial, UserHandle userHandle, Stack<T> appsToAdd, Stack<T> appsToUpdate, CachingLogic<T> cachingLogic, OnUpdateCallback onUpdateCallback) {
            this.mUserHandle = userHandle;
            this.mUserSerial = userSerial;
            this.mAppsToAdd = appsToAdd;
            this.mAppsToUpdate = appsToUpdate;
            this.mCachingLogic = cachingLogic;
            this.mOnUpdateCallback = onUpdateCallback;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!this.mAppsToUpdate.isEmpty()) {
                T tPop = this.mAppsToUpdate.pop();
                String packageName = this.mCachingLogic.getComponent(tPop).getPackageName();
                IconCacheUpdateHandler.this.mIconCache.addIconToDBAndMemCache(tPop, this.mCachingLogic, (PackageInfo) IconCacheUpdateHandler.this.mPkgInfoMap.get(packageName), this.mUserSerial, true);
                this.mUpdatedPackages.add(packageName);
                if (this.mAppsToUpdate.isEmpty() && !this.mUpdatedPackages.isEmpty()) {
                    this.mOnUpdateCallback.onPackageIconsUpdated(this.mUpdatedPackages, this.mUserHandle);
                }
                scheduleNext();
                return;
            }
            if (this.mAppsToAdd.isEmpty()) {
                return;
            }
            T tPop2 = this.mAppsToAdd.pop();
            PackageInfo packageInfo = (PackageInfo) IconCacheUpdateHandler.this.mPkgInfoMap.get(this.mCachingLogic.getComponent(tPop2).getPackageName());
            if (packageInfo != null) {
                IconCacheUpdateHandler.this.mIconCache.addIconToDBAndMemCache(tPop2, this.mCachingLogic, packageInfo, this.mUserSerial, false);
            }
            if (this.mAppsToAdd.isEmpty()) {
                return;
            }
            scheduleNext();
        }

        public void scheduleNext() {
            IconCacheUpdateHandler.this.mIconCache.mWorkerHandler.postAtTime(this, IconCacheUpdateHandler.ICON_UPDATE_TOKEN, SystemClock.uptimeMillis() + 1);
        }
    }
}
