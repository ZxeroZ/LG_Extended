package com.android.launcher3.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.AppFilter;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.icons.IconCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsModel {
    private static final boolean DEBUG = false;
    public static final boolean GO_DISABLE_WIDGETS = false;
    private static final String TAG = "WidgetsModel";
    private final AppFilter mAppFilter;
    private final Comparator mAppNameComparator;
    private final AppWidgetManagerCompat mAppWidgetMgr;
    private final IconCache mIconCache;
    private AlphabeticIndexCompat mIndexer;
    private ArrayList<PackageItemInfo> mPackageItemInfos;
    private ArrayList<Object> mRawList;
    public HashMap<String, PackageItemInfo> mTmpPackageItemInfos;
    protected final WidgetsAndShortcutNameComparator mWidgetAndShortcutNameComparator;
    public HashMap<PackageItemInfo, ArrayList<Object>> mWidgetsList;

    public WidgetsModel(Context context, IconCache iconCache, AppFilter appFilter) {
        this.mPackageItemInfos = new ArrayList<>();
        this.mWidgetsList = new HashMap<>();
        this.mTmpPackageItemInfos = new HashMap<>();
        this.mAppWidgetMgr = AppWidgetManagerCompat.getInstance(context);
        this.mWidgetAndShortcutNameComparator = new WidgetsAndShortcutNameComparator(context);
        this.mAppNameComparator = new AppNameComparator(context).getAppInfoComparator();
        this.mIconCache = iconCache;
        this.mAppFilter = appFilter;
        this.mIndexer = new AlphabeticIndexCompat(context);
    }

    protected WidgetsModel(WidgetsModel model) {
        this.mPackageItemInfos = new ArrayList<>();
        this.mWidgetsList = new HashMap<>();
        this.mTmpPackageItemInfos = new HashMap<>();
        this.mAppWidgetMgr = model.mAppWidgetMgr;
        ArrayList<PackageItemInfo> arrayList = model.mPackageItemInfos;
        if (arrayList != null) {
            synchronized (arrayList) {
                this.mPackageItemInfos = (ArrayList) model.mPackageItemInfos.clone();
            }
        } else {
            this.mPackageItemInfos = new ArrayList<>();
        }
        HashMap<PackageItemInfo, ArrayList<Object>> map = model.mWidgetsList;
        if (map != null) {
            synchronized (map) {
                this.mWidgetsList = (HashMap) model.mWidgetsList.clone();
            }
        } else {
            this.mWidgetsList = new HashMap<>();
        }
        ArrayList<Object> arrayList2 = model.mRawList;
        if (arrayList2 != null) {
            synchronized (arrayList2) {
                this.mRawList = (ArrayList) model.mRawList.clone();
            }
        } else {
            this.mRawList = new ArrayList<>();
        }
        this.mWidgetAndShortcutNameComparator = model.mWidgetAndShortcutNameComparator;
        this.mAppNameComparator = model.mAppNameComparator;
        this.mIconCache = model.mIconCache;
        this.mAppFilter = model.mAppFilter;
    }

    public int getPackageSize() {
        ArrayList<PackageItemInfo> arrayList = this.mPackageItemInfos;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public PackageItemInfo getPackageItemInfo(int pos) {
        if (pos >= this.mPackageItemInfos.size() || pos < 0) {
            return null;
        }
        return this.mPackageItemInfos.get(pos);
    }

    public List<Object> getSortedWidgets(int pos) {
        return this.mWidgetsList.get(this.mPackageItemInfos.get(pos));
    }

    public ArrayList<Object> getRawList() {
        return this.mRawList;
    }

    public void setWidgetsAndShortcuts(Context context, ArrayList<Object> rawWidgetsShortcuts) {
        UserHandle userHandleMyUserHandle;
        Utilities.assertWorkerThread();
        ArrayList<Object> arrayList = this.mRawList;
        if (arrayList != null) {
            synchronized (arrayList) {
                this.mRawList = rawWidgetsShortcuts;
            }
        } else {
            this.mRawList = rawWidgetsShortcuts;
        }
        this.mTmpPackageItemInfos.clear();
        synchronized (this.mWidgetsList) {
            this.mWidgetsList.clear();
        }
        synchronized (this.mPackageItemInfos) {
            this.mPackageItemInfos.clear();
        }
        for (Object obj : rawWidgetsShortcuts) {
            String str = "";
            ComponentName componentName = null;
            if (obj instanceof LauncherAppWidgetProviderInfo) {
                LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) obj;
                if ((launcherAppWidgetProviderInfo.getWidgetFeatures() & 2) != 0) {
                    continue;
                } else {
                    componentName = launcherAppWidgetProviderInfo.provider;
                    String packageName = launcherAppWidgetProviderInfo.provider.getPackageName();
                    userHandleMyUserHandle = this.mAppWidgetMgr.getUser(launcherAppWidgetProviderInfo);
                    str = packageName;
                }
            } else if (obj instanceof ResolveInfo) {
                ResolveInfo resolveInfo = (ResolveInfo) obj;
                componentName = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                str = resolveInfo.activityInfo.packageName;
                userHandleMyUserHandle = Process.myUserHandle();
            } else {
                userHandleMyUserHandle = null;
            }
            if (componentName != null && componentName.getPackageName().equalsIgnoreCase("com.lge.secondlauncher")) {
                Log.w(TAG, "Widget made by Cover display home should be ignored");
            } else if (componentName == null || userHandleMyUserHandle == null) {
                Log.e(TAG, String.format("Widget cannot be set for %s.", obj.getClass().toString()));
            } else {
                AppFilter appFilter = this.mAppFilter;
                if (appFilter == null || appFilter.shouldShowApp(componentName, userHandleMyUserHandle)) {
                    ArrayList<Object> arrayList2 = this.mWidgetsList.get(this.mTmpPackageItemInfos.get(str));
                    if (arrayList2 != null) {
                        arrayList2.add(obj);
                    } else {
                        ArrayList<Object> arrayList3 = new ArrayList<>();
                        arrayList3.add(obj);
                        PackageItemInfo packageItemInfo = new PackageItemInfo(str);
                        this.mIconCache.getTitleAndIconForApp(str, userHandleMyUserHandle, true, packageItemInfo);
                        packageItemInfo.titleSectionName = this.mIndexer.computeSectionName(packageItemInfo.title);
                        synchronized (this.mWidgetsList) {
                            this.mWidgetsList.put(packageItemInfo, arrayList3);
                        }
                        this.mTmpPackageItemInfos.put(str, packageItemInfo);
                        synchronized (this.mPackageItemInfos) {
                            this.mPackageItemInfos.add(packageItemInfo);
                        }
                    }
                }
            }
        }
        setTitleByFirstItemTitle();
        Collections.sort(this.mPackageItemInfos, this.mAppNameComparator);
        this.mWidgetAndShortcutNameComparator.clearCache();
        Iterator<PackageItemInfo> it = this.mPackageItemInfos.iterator();
        while (it.hasNext()) {
            Collections.sort(this.mWidgetsList.get(it.next()), this.mWidgetAndShortcutNameComparator);
        }
    }

    private void setTitleByFirstItemTitle() {
        Object obj;
        for (PackageItemInfo packageItemInfo : this.mPackageItemInfos) {
            ArrayList<Object> arrayList = this.mWidgetsList.get(packageItemInfo);
            if (arrayList.size() == 1 && (obj = arrayList.get(0)) != null) {
                packageItemInfo.title = this.mWidgetAndShortcutNameComparator.loadLabel(obj);
            }
        }
    }

    /* JADX DEBUG: Method merged with bridge method: clone()Ljava/lang/Object; */
    @Override // 
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public WidgetsModel mo209clone() {
        return new WidgetsModel(this);
    }
}
