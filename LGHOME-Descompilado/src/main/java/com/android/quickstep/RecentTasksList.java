package com.android.quickstep;

import android.app.ActivityManager;
import android.app.RecentTaskInfoEx;
import android.os.Process;
import android.util.SparseBooleanArray;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LooperExecutor;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.KeyguardManagerCompat;
import com.android.systemui.shared.system.TaskDescriptionCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.lge.launcher3.quickstep.ActivityManagerWrapperEx;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class RecentTasksList implements TaskStackChangeListener {
    private static final TaskLoadResult INVALID_RESULT = new TaskLoadResult(-1, false, 0);
    private final ActivityManagerWrapper mActivityManagerWrapper;
    private int mChangeId;
    private final KeyguardManagerCompat mKeyguardManager;
    private final LooperExecutor mMainThreadExecutor;
    private TaskLoadResult mResultsBg;
    private TaskLoadResult mResultsUi;

    public RecentTasksList(LooperExecutor mainThreadExecutor, KeyguardManagerCompat keyguardManager, ActivityManagerWrapper activityManagerWrapper) {
        TaskLoadResult taskLoadResult = INVALID_RESULT;
        this.mResultsBg = taskLoadResult;
        this.mResultsUi = taskLoadResult;
        this.mMainThreadExecutor = mainThreadExecutor;
        this.mKeyguardManager = keyguardManager;
        this.mChangeId = 1;
        this.mActivityManagerWrapper = activityManagerWrapper;
        activityManagerWrapper.registerTaskStackListener(this);
    }

    public void getTaskKeys(final int numTasks, final Consumer<ArrayList<Task>> callback) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentTasksList$sQXPWpJM633ERwMEXks3IHa_N4k
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getTaskKeys$1$RecentTasksList(numTasks, callback);
            }
        });
    }

    public /* synthetic */ void lambda$getTaskKeys$1$RecentTasksList(int i, final Consumer consumer) {
        final TaskLoadResult taskLoadResultLoadTasksInBackground = loadTasksInBackground(i, -1, true);
        this.mMainThreadExecutor.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentTasksList$M66B8FRa6Fzv0WsLxJIHRtR8ydg
            @Override // java.lang.Runnable
            public final void run() {
                consumer.accept(taskLoadResultLoadTasksInBackground);
            }
        });
    }

    public synchronized int getTasks(final boolean loadKeysOnly, final Consumer<ArrayList<Task>> callback) {
        final int i = this.mChangeId;
        if (!this.mResultsUi.isValidForRequest(i, loadKeysOnly)) {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentTasksList$0rZOIGHjKo7aWwgK3nVVtia3YJc
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getTasks$4$RecentTasksList(i, loadKeysOnly, callback);
                }
            });
            return i;
        }
        if (callback != null) {
            final TaskLoadResult taskLoadResult = this.mResultsUi;
            this.mMainThreadExecutor.post(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentTasksList$fLJ5feW3MV-reLgv2Kxw34qiEBA
                @Override // java.lang.Runnable
                public final void run() {
                    callback.accept(taskLoadResult);
                }
            });
        }
        return i;
    }

    public /* synthetic */ void lambda$getTasks$4$RecentTasksList(int i, boolean z, final Consumer consumer) {
        if (!this.mResultsBg.isValidForRequest(i, z)) {
            this.mResultsBg = loadTasksInBackground(Integer.MAX_VALUE, i, z);
        }
        final TaskLoadResult taskLoadResult = this.mResultsBg;
        this.mMainThreadExecutor.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentTasksList$mhs4iorQpynvW6r7gY9rsnw6zQc
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getTasks$3$RecentTasksList(taskLoadResult, consumer);
            }
        });
    }

    public /* synthetic */ void lambda$getTasks$3$RecentTasksList(TaskLoadResult taskLoadResult, Consumer consumer) {
        this.mResultsUi = taskLoadResult;
        if (consumer != null) {
            consumer.accept(taskLoadResult);
        }
    }

    public synchronized int getTasksAllReolad(final boolean loadKeysOnly, final Consumer<ArrayList<Task>> callback) {
        final int i;
        i = this.mChangeId;
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentTasksList$FPccwnnx-i3HQhwx5X5V-bc6-vU
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getTasksAllReolad$6$RecentTasksList(i, loadKeysOnly, callback);
            }
        });
        return i;
    }

    public /* synthetic */ void lambda$getTasksAllReolad$6$RecentTasksList(int i, boolean z, final Consumer consumer) {
        final TaskLoadResult taskLoadResultLoadTasksInBackground = loadTasksInBackground(Integer.MAX_VALUE, i, z);
        this.mResultsBg = taskLoadResultLoadTasksInBackground;
        this.mMainThreadExecutor.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentTasksList$FE-plW0hhbpo6bvD0cRE0-E2umU
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getTasksAllReolad$5$RecentTasksList(taskLoadResultLoadTasksInBackground, consumer);
            }
        });
    }

    public /* synthetic */ void lambda$getTasksAllReolad$5$RecentTasksList(TaskLoadResult taskLoadResult, Consumer consumer) {
        this.mResultsUi = taskLoadResult;
        if (consumer != null) {
            consumer.accept(taskLoadResult);
        }
    }

    public synchronized void forceInvalidateLoadedTasks() {
        invalidateLoadedTasks();
    }

    public synchronized boolean isTaskListValid(int changeId) {
        return this.mChangeId == changeId;
    }

    @Override // com.android.systemui.shared.system.TaskStackChangeListener
    public void onTaskStackChanged() {
        invalidateLoadedTasks();
    }

    @Override // com.android.systemui.shared.system.TaskStackChangeListener
    public void onRecentTaskListUpdated() {
        invalidateLoadedTasks();
    }

    @Override // com.android.systemui.shared.system.TaskStackChangeListener
    public void onTaskRemoved(int taskId) {
        invalidateLoadedTasks();
    }

    @Override // com.android.systemui.shared.system.TaskStackChangeListener
    public void onActivityPinned(String packageName, int userId, int taskId, int stackId) {
        invalidateLoadedTasks();
    }

    @Override // com.android.systemui.shared.system.TaskStackChangeListener
    public synchronized void onActivityUnpinned() {
        invalidateLoadedTasks();
    }

    private synchronized void invalidateLoadedTasks() {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$RecentTasksList$UsYbvwPrydqSk9Pp_ZiOJpvgjTg
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$invalidateLoadedTasks$7$RecentTasksList();
            }
        });
        this.mResultsUi = INVALID_RESULT;
        this.mChangeId++;
    }

    public /* synthetic */ void lambda$invalidateLoadedTasks$7$RecentTasksList() {
        this.mResultsBg = INVALID_RESULT;
    }

    TaskLoadResult loadTasksInBackground(int numTasks, int requestId, boolean loadKeysOnly) {
        Task task;
        List<RecentTaskInfoEx> recentTasksEx = ActivityManagerWrapperEx.getInstance().getRecentTasksEx(numTasks, 0, Process.myUserHandle().getIdentifier());
        Collections.reverse(recentTasksEx);
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray() { // from class: com.android.quickstep.RecentTasksList.1
            @Override // android.util.SparseBooleanArray
            public boolean get(int key) {
                if (indexOfKey(key) < 0) {
                    put(key, RecentTasksList.this.mKeyguardManager.isDeviceLocked(key));
                }
                return super.get(key);
            }
        };
        TaskLoadResult taskLoadResult = new TaskLoadResult(requestId, loadKeysOnly, recentTasksEx.size());
        for (RecentTaskInfoEx recentTaskInfoEx : recentTasksEx) {
            Task.TaskKey taskKey = new Task.TaskKey(recentTaskInfoEx);
            if (!loadKeysOnly) {
                boolean z = sparseBooleanArray.get(taskKey.userId);
                ActivityManager.TaskDescription taskDescription = recentTaskInfoEx.taskDescription;
                TaskDescriptionCompat taskDescriptionCompat = new TaskDescriptionCompat(taskDescription);
                task = new Task(taskKey, taskDescriptionCompat.getPrimaryColor(), taskDescriptionCompat.getBackgroundColor(), recentTaskInfoEx.supportsSplitScreenMultiWindow, z, taskDescription, recentTaskInfoEx.topActivity, recentTaskInfoEx.isPinnedInRecent);
            } else {
                task = new Task(taskKey);
            }
            taskLoadResult.add(task);
        }
        return taskLoadResult;
    }

    private ArrayList<Task> copyOf(ArrayList<Task> tasks) {
        ArrayList<Task> arrayList = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            arrayList.add(new Task(task.key, task.colorPrimary, task.colorBackground, task.isDockable, task.isLocked, task.taskDescription, task.topActivity, task.isPinned));
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class TaskLoadResult extends ArrayList<Task> {
        final int mId;
        final boolean mKeysOnly;

        TaskLoadResult(int id, boolean keysOnly, int size) {
            super(size);
            this.mId = id;
            this.mKeysOnly = keysOnly;
        }

        boolean isValidForRequest(int requestId, boolean loadKeysOnly) {
            return this.mId == requestId && (!this.mKeysOnly || loadKeysOnly);
        }
    }
}
