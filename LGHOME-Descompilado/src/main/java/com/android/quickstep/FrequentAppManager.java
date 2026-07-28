package com.android.quickstep;

import android.content.Context;
import com.android.launcher3.util.UiThreadHelper;
import com.lge.contextenginelibrary.FrequentAppsLib;
import com.lge.contextenginelibrary.model.FrequentAppsInfo;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.lang.Thread;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes.dex */
public class FrequentAppManager {
    private static final String TAG = "FrequentAppManager";
    private static FrequentAppManager mInstance;
    private FrequentAppsInfo mBasicFrequentApps;
    private FrequentAppsLib mFrequentAppsLib;
    private boolean mUpdateFrequentApps = true;
    private Thread mFrequentAppThread = null;

    public static FrequentAppManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FrequentAppManager(context);
        }
        return mInstance;
    }

    private FrequentAppManager(Context context) {
        this.mFrequentAppsLib = null;
        this.mBasicFrequentApps = null;
        FrequentAppsLib frequentAppsLib = new FrequentAppsLib(context.getApplicationContext(), false);
        this.mFrequentAppsLib = frequentAppsLib;
        this.mBasicFrequentApps = frequentAppsLib.getBasicFrequentApps();
    }

    public boolean existFrequentApps(String pkgName) {
        FrequentAppsInfo frequentAppsInfo = this.mBasicFrequentApps;
        if (frequentAppsInfo != null) {
            Iterator<FrequentAppsInfo.AppInfo> it = frequentAppsInfo.appInfos.iterator();
            while (it.hasNext()) {
                if (it.next().packageName.equals(pkgName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean needUpdate(boolean forceUpdate) {
        LGLog.d(TAG, String.format("needUpdate : mUpdateFrequentApps(%s), forceUpdate(%s)", Boolean.valueOf(this.mUpdateFrequentApps), Boolean.valueOf(forceUpdate)));
        if (!this.mUpdateFrequentApps && !forceUpdate) {
            return false;
        }
        if (!forceUpdate) {
            this.mUpdateFrequentApps = false;
        }
        return true;
    }

    public List<FrequentAppsInfo.AppInfo> getFrequentApps() {
        FrequentAppsInfo frequentAppsInfo = this.mBasicFrequentApps;
        if (frequentAppsInfo != null) {
            return frequentAppsInfo.appInfos;
        }
        return null;
    }

    public List<String> getFrequentAppNames() {
        FrequentAppsInfo frequentAppsInfo = this.mBasicFrequentApps;
        if (frequentAppsInfo != null) {
            return getPackageList(frequentAppsInfo.appInfos, new Function() { // from class: com.android.quickstep.-$$Lambda$FrequentAppManager$7nR8gMA5U93M0yeYNXJz9z94nAQ
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((FrequentAppsInfo.AppInfo) obj).packageName;
                }
            });
        }
        return null;
    }

    public void checkUpdateFrequentApps(Context context) {
        Thread thread = this.mFrequentAppThread;
        if (thread == null || !thread.isAlive()) {
            this.mFrequentAppThread = new Thread(new Runnable() { // from class: com.android.quickstep.-$$Lambda$FrequentAppManager$vW2DpkBiaCdyyz5wxnx64bqnHWo
                @Override // java.lang.Runnable
                public final void run() throws Throwable {
                    this.f$0.lambda$checkUpdateFrequentApps$3$FrequentAppManager();
                }
            });
            UiThreadHelper.runAsyncCommand(context.getApplicationContext(), new UiThreadHelper.AsyncCommand() { // from class: com.android.quickstep.-$$Lambda$FrequentAppManager$LuAqu__EtwY8q9YtexTSSJETzRY
                @Override // com.android.launcher3.util.UiThreadHelper.AsyncCommand
                public final void execute(Context context2, int i, int i2) {
                    this.f$0.lambda$checkUpdateFrequentApps$4$FrequentAppManager(context2, i, i2);
                }
            }, 0, 0);
        }
    }

    public /* synthetic */ void lambda$checkUpdateFrequentApps$3$FrequentAppManager() throws Throwable {
        FrequentAppsLib frequentAppsLib = this.mFrequentAppsLib;
        if (frequentAppsLib != null) {
            FrequentAppsInfo basicFrequentApps = frequentAppsLib.getBasicFrequentApps();
            if (this.mBasicFrequentApps == null || basicFrequentApps == null || basicFrequentApps.appInfos.size() != this.mBasicFrequentApps.appInfos.size()) {
                this.mBasicFrequentApps = basicFrequentApps;
                this.mUpdateFrequentApps = true;
            } else if (isUpdated(getPackageList(this.mBasicFrequentApps.appInfos, new Function() { // from class: com.android.quickstep.-$$Lambda$FrequentAppManager$BMOO0bSYDWZdaqxIGLa8C0v5Tag
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((FrequentAppsInfo.AppInfo) obj).packageName;
                }
            }), getPackageList(basicFrequentApps.appInfos, new Function() { // from class: com.android.quickstep.-$$Lambda$FrequentAppManager$7YTIP15y_0iaqv8P00SAGErEo9w
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((FrequentAppsInfo.AppInfo) obj).packageName;
                }
            }))) {
                LGLog.d(TAG, "Recommend apps is updated.");
                this.mBasicFrequentApps = basicFrequentApps;
                this.mUpdateFrequentApps = true;
            }
        }
    }

    public /* synthetic */ void lambda$checkUpdateFrequentApps$4$FrequentAppManager(Context context, int i, int i2) {
        try {
            LGLog.d(TAG, "Frequent app thread state: " + this.mFrequentAppThread.getState());
            if (this.mFrequentAppThread.getState() == Thread.State.NEW) {
                this.mFrequentAppThread.start();
            }
        } catch (Exception e) {
            LGLog.w(TAG, e.toString(), new int[0]);
        }
    }

    public static boolean isUpdated(List<String> oldItems, List<String> newItems) {
        Collections.sort(oldItems);
        Collections.sort(newItems);
        return !oldItems.equals(newItems);
    }

    public static boolean isSupportRecommendApps() {
        return LGHomeFeature.Config.FEATURE_SUPPORT_SUGGESTION_APP.getValue() && LGHomeFeature.isAppSuggestionEnabled();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v0, resolved type: java.util.function.Function<C, T> */
    /* JADX WARN: Multi-variable type inference failed */
    public static <C, T> List<T> getPackageList(List<C> items, Function<C, T> func) {
        return (List) items.stream().map(func).collect(Collectors.toList());
    }
}
