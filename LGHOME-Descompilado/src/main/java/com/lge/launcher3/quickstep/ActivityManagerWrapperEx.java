package com.lge.launcher3.quickstep;

import android.app.ActivityManager;
import android.app.ActivityManagerEx;
import android.app.ActivityTaskManager;
import android.app.AppGlobals;
import android.app.Application;
import android.app.LGActivityTrigger;
import android.app.RecentTaskInfoEx;
import android.app.TaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.view.IRecentsAnimationController;
import android.view.IRecentsAnimationRunner;
import android.view.RemoteAnimationTarget;
import android.window.TaskSnapshot;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RecentsAnimationControllerCompat;
import com.android.systemui.shared.system.RecentsAnimationListener;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.lge.display.DisplayManagerHelper;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class ActivityManagerWrapperEx {
    private static boolean DEBUG = true;
    public static final String RECENT_TASK = "[RECENT_TASK] ";
    private static final String SERVICE_FLOATING_WINDOW = "com.lge.app.floating.FloatingWindowService";
    private static final String TAG = "ActivityManagerWrapperEx";
    private static ArrayList<String> mQSlideAppList;
    private static final ActivityManagerWrapperEx sInstance = new ActivityManagerWrapperEx();
    ActivityManagerEx mAm;
    private final Context mContext;
    private int mDisplayId;
    private DisplayManagerHelper mDisplayManagerHelper;
    private String[] mPackageNameForSkipRemoveTask;

    private ActivityManagerWrapperEx() {
        Application initialApplication = AppGlobals.getInitialApplication();
        this.mContext = initialApplication;
        mQSlideAppList = new ArrayList<>();
        this.mPackageNameForSkipRemoveTask = initialApplication.getResources().getStringArray(R.array.skip_remove_task_packages);
        this.mAm = (ActivityManagerEx) initialApplication.getSystemService("activity");
        DisplayManagerHelper displayManagerHelper = new DisplayManagerHelper(initialApplication);
        this.mDisplayManagerHelper = displayManagerHelper;
        this.mDisplayId = displayManagerHelper.getMultiDisplayId();
    }

    public static ActivityManagerWrapperEx getInstance() {
        return sInstance;
    }

    private boolean isQSlideApp(ActivityManagerEx am, String packageName) {
        mQSlideAppList.clear();
        List runningServices = am.getRunningServices(Integer.MAX_VALUE);
        if (runningServices != null) {
            for (int i = 0; i < runningServices.size(); i++) {
                if (SERVICE_FLOATING_WINDOW.equals(((ActivityManager.RunningServiceInfo) runningServices.get(i)).service.getClassName())) {
                    mQSlideAppList.add(((ActivityManager.RunningServiceInfo) runningServices.get(i)).service.getPackageName());
                }
            }
        }
        Iterator<String> it = mQSlideAppList.iterator();
        while (it.hasNext()) {
            if (it.next().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public List<RecentTaskInfoEx> getRecentTasksEx(int numTasks, int flags, int userId) {
        try {
            List<RecentTaskInfoEx> recentTasksForUserEx = this.mAm.getRecentTasksForUserEx(numTasks, flags | 2, userId);
            Iterator<RecentTaskInfoEx> it = recentTasksForUserEx.iterator();
            int size = recentTasksForUserEx.size();
            StringBuilder sb = new StringBuilder();
            while (it.hasNext()) {
                RecentTaskInfoEx next = it.next();
                if (isQSlideApp(this.mAm, next.baseIntent.getComponent().getPackageName())) {
                    it.remove();
                } else if ("com.skt.prod.incall.lib.ui.activities.incall.InCallActivity".equals(next.baseIntent.getComponent().getClassName())) {
                    it.remove();
                } else {
                    sb.append(", " + next.taskId);
                }
            }
            LGLog.i(TAG, "[RECENT_TASK] getRecentTasksEx : size(" + size + "->" + recentTasksForUserEx.size() + ") " + ((Object) sb) + ", numTask = " + numTasks);
            return recentTasksForUserEx;
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public void startRecentsActivityEx(Intent intent, long eventTime, final RecentsAnimationListener animationHandler, final Consumer<Boolean> resultCallback, Handler resultCallbackHandler, final int displayId) {
        IRecentsAnimationRunner iRecentsAnimationRunner = null;
        if (animationHandler != null) {
            try {
                iRecentsAnimationRunner = new IRecentsAnimationRunner.Stub() { // from class: com.lge.launcher3.quickstep.ActivityManagerWrapperEx.1
                    public void onTasksAppeared(RemoteAnimationTarget[] app) {
                    }

                    public void onAnimationStart(IRecentsAnimationController controller, RemoteAnimationTarget[] apps, RemoteAnimationTarget[] wallpapers, Rect homeContentInsets, Rect minimizedHomeBounds) {
                        RecentsAnimationControllerCompat recentsAnimationControllerCompat = new RecentsAnimationControllerCompat(controller);
                        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArrWrap = RemoteAnimationTargetCompat.wrap(apps);
                        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArrWrap2 = RemoteAnimationTargetCompat.wrap(wallpapers);
                        int i = displayId;
                        if (i == 0) {
                            animationHandler.onAnimationStart(recentsAnimationControllerCompat, remoteAnimationTargetCompatArrWrap, remoteAnimationTargetCompatArrWrap2, homeContentInsets, minimizedHomeBounds);
                        } else {
                            animationHandler.onAnimationStart(recentsAnimationControllerCompat, remoteAnimationTargetCompatArrWrap, remoteAnimationTargetCompatArrWrap2, homeContentInsets, minimizedHomeBounds, i);
                        }
                    }

                    public void onAnimationCanceled(int[] taskIds, TaskSnapshot[] taskSnapshots) {
                        animationHandler.onAnimationCanceled(ThumbnailData.wrap(taskIds, taskSnapshots));
                    }
                };
            } catch (Exception unused) {
                if (resultCallback != null) {
                    resultCallbackHandler.post(new Runnable() { // from class: com.lge.launcher3.quickstep.ActivityManagerWrapperEx.3
                        @Override // java.lang.Runnable
                        public void run() {
                            resultCallback.accept(false);
                        }
                    });
                    return;
                }
                return;
            }
        }
        IRecentsAnimationRunner iRecentsAnimationRunner2 = iRecentsAnimationRunner;
        if (displayId == 0) {
            ActivityTaskManager.getService().startRecentsActivity(intent, eventTime, iRecentsAnimationRunner2);
        } else {
            ActivityTaskManager.getService().startRecentsActivityAsDisplay(intent, eventTime, iRecentsAnimationRunner2, displayId);
        }
        if (resultCallback != null) {
            resultCallbackHandler.post(new Runnable() { // from class: com.lge.launcher3.quickstep.ActivityManagerWrapperEx.2
                @Override // java.lang.Runnable
                public void run() {
                    resultCallback.accept(true);
                }
            });
        }
    }

    public void startMultiDisplayHomeAsDisplayId(int displayId) {
        try {
            LGLog.i(TAG, "startMultiDisplayHomeAsDisplayId : " + displayId);
            this.mAm.startSecondHomeActivityAsDisplayId(displayId);
        } catch (Exception e) {
            LGLog.i(TAG, "startCoverDisplayHome: " + e.toString());
        }
    }

    public int getMultiDisplayId() {
        return this.mDisplayId;
    }

    public boolean isHomeTask(TaskInfo taskInfo, int displayId, String defaultHomeClassName) {
        ComponentName topActivityAsDisplay;
        boolean z = true;
        if (!DisplayManagerHelper.isMultiDisplayDevice() || taskInfo.displayId == -1 || taskInfo.displayId == displayId ? displayId != 0 ? displayId != this.mDisplayId || taskInfo.topActivity == null || !defaultHomeClassName.equals(taskInfo.topActivity.getClassName()) : taskInfo.configuration.windowConfiguration.getActivityType() != 2 : (topActivityAsDisplay = this.mAm.getTopActivityAsDisplay(displayId)) == null || !defaultHomeClassName.equals(topActivityAsDisplay.getClassName())) {
            z = false;
        }
        if (DEBUG) {
            LGLog.d(TAG, "isHomeTask : result =  " + z + ", displayId = " + displayId + ", defaultHomeClassName = " + defaultHomeClassName + ", taskInfo = " + taskInfo);
        }
        return z;
    }

    public ComponentName getTopActivityDisplay(int displayId) {
        return this.mAm.getTopActivityAsDisplay(displayId);
    }

    public Intent getSecondaryHomeIntent(int userId, int displayId) {
        try {
            LGLog.d(TAG, "getSecondaryHomeIntent : " + ActivityTaskManager.getService().getSecondaryHomeIntent(userId, displayId));
            return ActivityTaskManager.getService().getSecondaryHomeIntent(userId, displayId);
        } catch (Exception e) {
            LGLog.i(TAG, "getSecondaryHomeIntent: use default intent  because " + e.toString());
            return getDefaultCoverIntent();
        } catch (NoSuchMethodError unused) {
            LGLog.d(TAG, "getSecondaryHomeIntent: use default intent because NoSuchMethodError");
            return getDefaultCoverIntent();
        }
    }

    private Intent getDefaultCoverIntent() {
        return new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).setComponent(new ComponentName(this.mContext.getString(R.string.cover_home_pacakge), this.mContext.getString(R.string.cover_home_class))).addCategory("android.intent.category.DEFAULT").setFlags(268435456);
    }

    public boolean skipRemoveTask(Task task) {
        return isAnshinmodePackage(task) && hasFlagActivityExcludedFromRecents(task);
    }

    public boolean isAnshinmodePackage(Task task) {
        String[] strArr;
        if (task == null || task.topActivity == null || (strArr = this.mPackageNameForSkipRemoveTask) == null || strArr.length <= 0) {
            return false;
        }
        boolean z = false;
        for (String str : strArr) {
            if (str != null && str.equals(task.topActivity.getPackageName())) {
                z = true;
            }
        }
        return z;
    }

    public boolean hasFlagActivityExcludedFromRecents(Task task) {
        return (task == null || task.key == null || (task.key.baseIntent.getFlags() & 8388608) != 8388608) ? false : true;
    }

    public boolean hasFlagActivityExcludedFromRecents(TaskInfo taskInfo) {
        return taskInfo != null && (taskInfo.baseIntent.getFlags() & 8388608) == 8388608;
    }

    public boolean canBeLaunchedOnSubDisplay(int displayId, String packageName) {
        boolean zCanBeLaunchedOnSubDisplay = this.mAm.canBeLaunchedOnSubDisplay(displayId, packageName);
        LGLog.d(TAG, "canBeLaunchedOnSubDisplay : displayId = " + displayId + ", packageName = " + packageName);
        return zCanBeLaunchedOnSubDisplay;
    }

    public void registerLGActivityTrigger(LGActivityTrigger callback) {
        try {
            if (this.mAm != null) {
                LGLog.i(TAG, "registerLGActivityTrigger");
                this.mAm.registerLGActivityTrigger(callback);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unRegisterLGActivityTrigger(LGActivityTrigger callback) {
        try {
            if (this.mAm != null) {
                LGLog.i(TAG, "unRegisterLGActivityTrigger");
                this.mAm.unregisterLGActivityTrigger(callback);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
