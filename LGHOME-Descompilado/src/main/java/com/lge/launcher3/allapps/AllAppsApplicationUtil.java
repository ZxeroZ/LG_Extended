package com.lge.launcher3.allapps;

import android.content.ComponentName;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.model.data.AppInfo;
import com.lge.launcher3.allapps.AllAppsConstant;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsApplicationUtil {
    private static final String LOG_TAG = "AllAppsApplicationUtil";
    private final BindAppsList mBindApps = new BindAppsList();
    private ArrayList<AppInfo> mApps = new ArrayList<>();

    private class BindAppsList {
        ArrayList<AppInfo> addApps;
        ArrayList<AppInfo> removeApps;
        ArrayList<AppInfo> updateApps;

        private BindAppsList() {
            this.addApps = new ArrayList<>();
            this.removeApps = new ArrayList<>();
            this.updateApps = new ArrayList<>();
        }
    }

    public ArrayList<AppInfo> getApps(boolean isNeedSort) {
        if (isNeedSort) {
            Collections.sort(this.mApps, AllAppsSort.NAME_COMPARATOR);
        }
        return this.mApps;
    }

    public int getAllAppsCount() {
        return this.mApps.size();
    }

    public boolean isAllAppsEmpty() {
        return this.mApps.isEmpty();
    }

    public ArrayList<AppInfo> setApps(ArrayList<AppInfo> list) {
        ArrayList<AppInfo> arrayList = new ArrayList<>(list);
        this.mApps = arrayList;
        try {
            Collections.sort(arrayList, AllAppsSort.NAME_COMPARATOR);
        } catch (IllegalArgumentException e) {
            String str = LOG_TAG;
            LGLog.w(str, e.getMessage(), new int[0]);
            LGLog.w(str, "================Sort Failed================", new int[0]);
            for (AppInfo appInfo : this.mApps) {
                if (appInfo != null && appInfo.title != null) {
                    LGLog.w(LOG_TAG, "= AppInfo title = " + ((Object) appInfo.title), new int[0]);
                } else {
                    LGLog.w(LOG_TAG, "= info = " + appInfo, new int[0]);
                }
            }
            String str2 = LOG_TAG;
            LGLog.w(str2, "===========================================", new int[0]);
            LGLog.w(str2, e.toString(), new int[0]);
            LGLog.w(str2, " " + e.getStackTrace(), new int[0]);
        }
        return this.mApps;
    }

    public void addApps(ArrayList<AppInfo> app_list) {
        if (app_list == null || app_list.size() <= 0) {
            return;
        }
        ArrayList arrayList = new ArrayList(app_list);
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            AppInfo appInfo = (AppInfo) arrayList.get(i);
            int iBinarySearch = Collections.binarySearch(this.mApps, appInfo, AllAppsSort.NAME_COMPARATOR);
            if (iBinarySearch < 0) {
                int i2 = -(iBinarySearch + 1);
                this.mApps.add(i2, appInfo);
                Log.d(LOG_TAG, "addAppsWithoutInvalidate index = " + i2 + " ItemInfo =" + (appInfo != null ? appInfo.componentName : null));
            } else {
                this.mApps.add(iBinarySearch, appInfo);
                Log.d(LOG_TAG, "addAppsWithoutInvalidate index = " + iBinarySearch + " ItemInfo =" + (appInfo != null ? appInfo.componentName : null));
            }
        }
    }

    public void removeAppsWithoutInvalidate(ArrayList<AppInfo> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            int iFindAppByComponent = findAppByComponent(this.mApps, list.get(i));
            if (iFindAppByComponent > -1) {
                this.mApps.remove(iFindAppByComponent);
            }
        }
    }

    public void initBindApps() {
        this.mBindApps.addApps.clear();
        this.mBindApps.updateApps.clear();
        this.mBindApps.removeApps.clear();
    }

    public int getRemainBindAppsSize() {
        return this.mBindApps.addApps.size() + this.mBindApps.updateApps.size() + this.mBindApps.removeApps.size();
    }

    /* JADX INFO: renamed from: com.lge.launcher3.allapps.AllAppsApplicationUtil$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$allapps$AllAppsConstant$AppState;

        static {
            int[] iArr = new int[AllAppsConstant.AppState.values().length];
            $SwitchMap$com$lge$launcher3$allapps$AllAppsConstant$AppState = iArr;
            try {
                iArr[AllAppsConstant.AppState.ADD.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$allapps$AllAppsConstant$AppState[AllAppsConstant.AppState.UPDATE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$allapps$AllAppsConstant$AppState[AllAppsConstant.AppState.REMOVE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public ArrayList<AppInfo> getRemainBindApps(AllAppsConstant.AppState state) {
        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$allapps$AllAppsConstant$AppState[state.ordinal()];
        if (i == 1) {
            return this.mBindApps.addApps;
        }
        if (i == 2) {
            return this.mBindApps.updateApps;
        }
        if (i != 3) {
            return null;
        }
        return this.mBindApps.removeApps;
    }

    public void clearBindApps(AllAppsConstant.AppState state) {
        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$allapps$AllAppsConstant$AppState[state.ordinal()];
        if (i == 1) {
            this.mBindApps.addApps.clear();
        } else if (i == 2) {
            this.mBindApps.updateApps.clear();
        } else {
            if (i != 3) {
                return;
            }
            this.mBindApps.removeApps.clear();
        }
    }

    public void appBindingCompress(ArrayList<AppInfo> addList, int op) {
        if (op == 2) {
            appBindingCompress(addList, AllAppsConstant.AppState.UPDATE);
        } else {
            appBindingCompress(addList, AllAppsConstant.AppState.ADD);
        }
    }

    public void appBindingCompress(ArrayList<AppInfo> list, AllAppsConstant.AppState opCode) {
        for (AppInfo appInfo : list) {
            AppInfo appInfo2 = null;
            Iterator<AppInfo> it = this.mBindApps.addApps.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                AppInfo next = it.next();
                if (appInfo.componentName.equals(next.componentName)) {
                    if (opCode == AllAppsConstant.AppState.REMOVE || opCode == AllAppsConstant.AppState.UPDATE) {
                        appInfo2 = next;
                    }
                }
            }
            if (appInfo2 != null) {
                if (opCode == AllAppsConstant.AppState.REMOVE) {
                    this.mBindApps.addApps.remove(appInfo2);
                }
            } else {
                if (opCode == AllAppsConstant.AppState.UPDATE || opCode == AllAppsConstant.AppState.REMOVE) {
                    Iterator<AppInfo> it2 = this.mBindApps.updateApps.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        AppInfo next2 = it2.next();
                        if (appInfo.componentName.equals(next2.componentName)) {
                            if (opCode == AllAppsConstant.AppState.REMOVE || opCode == AllAppsConstant.AppState.UPDATE) {
                                appInfo2 = next2;
                            }
                        }
                    }
                }
                if (appInfo2 != null) {
                    if (opCode == AllAppsConstant.AppState.REMOVE) {
                        this.mBindApps.updateApps.remove(appInfo2);
                        this.mBindApps.removeApps.add(appInfo2);
                    }
                } else {
                    Iterator<AppInfo> it3 = this.mBindApps.removeApps.iterator();
                    while (true) {
                        if (!it3.hasNext()) {
                            break;
                        }
                        AppInfo next3 = it3.next();
                        if (appInfo.componentName.equals(next3.componentName)) {
                            if (opCode == AllAppsConstant.AppState.ADD) {
                                this.mBindApps.updateApps.add(appInfo);
                                appInfo2 = next3;
                            }
                        }
                    }
                    if (appInfo2 != null) {
                        this.mBindApps.removeApps.remove(appInfo2);
                    } else {
                        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$allapps$AllAppsConstant$AppState[opCode.ordinal()];
                        if (i == 1) {
                            this.mBindApps.addApps.add(appInfo);
                        } else if (i == 2) {
                            this.mBindApps.updateApps.add(appInfo);
                        } else if (i == 3) {
                            this.mBindApps.removeApps.add(appInfo);
                        }
                    }
                }
            }
        }
    }

    public AppInfo findAppByComponent(ComponentName componentName, UserHandle user) {
        ArrayList<AppInfo> arrayList = this.mApps;
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        int size = this.mApps.size();
        for (int i = 0; i < size; i++) {
            AppInfo appInfo = this.mApps.get(i);
            if (user.equals(appInfo.user) && appInfo.intent.getComponent().equals(componentName)) {
                return appInfo;
            }
        }
        return null;
    }

    int findAppByComponent(ArrayList<AppInfo> list, AppInfo item) {
        if (item == null || item.intent == null) {
            return -1;
        }
        ComponentName component = item.intent.getComponent();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AppInfo appInfo = list.get(i);
            if (item.user.equals(appInfo.user) && appInfo.intent.getComponent().equals(component)) {
                return i;
            }
        }
        return -1;
    }

    public AppInfo getAppInfo(ComponentName component, UserHandle user) {
        return findAppByComponent(component, user);
    }

    public HashMap<String, AppInfo> makeAllAppsHashMap(ArrayList<AppInfo> list) {
        HashMap<String, AppInfo> map = new HashMap<>();
        for (AppInfo appInfo : list) {
            map.put(appInfo.componentName.flattenToShortString(), appInfo);
        }
        return map;
    }
}
