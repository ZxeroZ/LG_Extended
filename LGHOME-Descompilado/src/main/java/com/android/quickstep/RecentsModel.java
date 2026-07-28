package com.android.quickstep;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.icons.IconProvider;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.KeyguardManagerCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class RecentsModel implements TaskStackChangeListener {
    public static final MainThreadInitializedObject<RecentsModel> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.quickstep.-$$Lambda$RecentsModel$P93tHa31NLuzhVvQ3PlsvxKGnOE
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return RecentsModel.lambda$P93tHa31NLuzhVvQ3PlsvxKGnOE(context);
        }
    });
    private final Context mContext;
    private final TaskIconCache mIconCache;
    private final RecentTasksList mTaskList;
    private final TaskThumbnailCache mThumbnailCache;
    private final List<TaskVisualsChangeListener> mThumbnailChangeListeners = new ArrayList();

    public interface TaskVisualsChangeListener {
        void onTaskIconChanged(String pkg, UserHandle user);

        Task onTaskThumbnailChanged(int taskId, ThumbnailData thumbnailData);
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR (r1v0 android.content.Context) A[MD:(android.content.Context):void (m)] call: com.android.quickstep.RecentsModel.<init>(android.content.Context):void type: CONSTRUCTOR */
    public static /* synthetic */ RecentsModel lambda$P93tHa31NLuzhVvQ3PlsvxKGnOE(Context context) {
        return new RecentsModel(context);
    }

    private RecentsModel(Context context) {
        this.mContext = context;
        Looper looperCreateAndStartNewLooper = Executors.createAndStartNewLooper("TaskThumbnailIconCache", 10);
        this.mTaskList = new RecentTasksList(Executors.MAIN_EXECUTOR, new KeyguardManagerCompat(context), ActivityManagerWrapper.getInstance());
        this.mIconCache = new TaskIconCache(context, looperCreateAndStartNewLooper);
        this.mThumbnailCache = new TaskThumbnailCache(context, looperCreateAndStartNewLooper);
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this);
        IconProvider.registerIconChangeListener(context, new BiConsumer() { // from class: com.android.quickstep.-$$Lambda$RecentsModel$Ob3vMnJD5wn0VyQeMuK1AWudaR4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                this.f$0.onPackageIconChanged((String) obj, (UserHandle) obj2);
            }
        }, Executors.MAIN_EXECUTOR.getHandler());
    }

    public TaskIconCache getIconCache() {
        return this.mIconCache;
    }

    public TaskThumbnailCache getThumbnailCache() {
        return this.mThumbnailCache;
    }

    public int getTasks(Consumer<ArrayList<Task>> callback) {
        return this.mTaskList.getTasks(false, callback);
    }

    public int getTasksAllReload(Consumer<ArrayList<Task>> callback) {
        return this.mTaskList.getTasksAllReolad(false, callback);
    }

    public void forceInvalidateLoadedTasks() {
        this.mTaskList.forceInvalidateLoadedTasks();
    }

    public static int getRunningTaskId() {
        ActivityManager.RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask();
        if (runningTask != null) {
            return runningTask.id;
        }
        return -1;
    }

    public boolean isTaskListValid(int changeId) {
        return this.mTaskList.isTaskListValid(changeId);
    }

    public void findTaskWithId(final int taskId, final Consumer<Task.TaskKey> callback) {
        this.mTaskList.getTasks(true, new Consumer() { // from class: com.android.quickstep.-$$Lambda$RecentsModel$I5qGzmPrTberW4zIgmPGq99aWeQ
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RecentsModel.lambda$findTaskWithId$0(taskId, callback, (ArrayList) obj);
            }
        });
    }

    static /* synthetic */ void lambda$findTaskWithId$0(int i, Consumer consumer, ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Task task = (Task) it.next();
            if (task.key.id == i) {
                consumer.accept(task.key);
                return;
            }
        }
        consumer.accept(null);
    }

    @Override // com.android.systemui.shared.system.TaskStackChangeListener
    public void onTaskStackChangedBackground() {
        if (this.mThumbnailCache.isPreloadingEnabled() && TaskUtils.checkCurrentOrManagedUserId(Process.myUserHandle().getIdentifier(), this.mContext)) {
            final int runningTaskId = getRunningTaskId();
            this.mTaskList.getTaskKeys(this.mThumbnailCache.getCacheSize(), new Consumer() { // from class: com.android.quickstep.-$$Lambda$RecentsModel$k1Mm-BV64Bc89eJHYugywZmW_Js
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$onTaskStackChangedBackground$1$RecentsModel(runningTaskId, (ArrayList) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onTaskStackChangedBackground$1$RecentsModel(int i, ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Task task = (Task) it.next();
            if (task.key.id != i) {
                this.mThumbnailCache.updateThumbnailInCache(task);
            }
        }
    }

    @Override // com.android.systemui.shared.system.TaskStackChangeListener
    public boolean onTaskSnapshotChanged(int taskId, ThumbnailData snapshot) {
        this.mThumbnailCache.updateTaskSnapShot(taskId, snapshot);
        for (int size = this.mThumbnailChangeListeners.size() - 1; size >= 0; size--) {
            Task taskOnTaskThumbnailChanged = this.mThumbnailChangeListeners.get(size).onTaskThumbnailChanged(taskId, snapshot);
            if (taskOnTaskThumbnailChanged != null) {
                taskOnTaskThumbnailChanged.thumbnail = snapshot;
            }
        }
        return true;
    }

    @Override // com.android.systemui.shared.system.TaskStackChangeListener
    public void onTaskRemoved(int taskId) {
        Task.TaskKey taskKey = new Task.TaskKey(taskId, 0, null, null, 0, 0L);
        this.mThumbnailCache.remove(taskKey);
        this.mIconCache.onTaskRemoved(taskKey);
    }

    public void onTrimMemory(int level) {
        if (level == 20) {
            this.mThumbnailCache.getHighResLoadingState().setVisible(false);
        }
        if (level == 15) {
            this.mThumbnailCache.clear();
            this.mIconCache.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPackageIconChanged(String pkg, UserHandle user) {
        this.mIconCache.invalidateCacheEntries(pkg, user);
        for (int size = this.mThumbnailChangeListeners.size() - 1; size >= 0; size--) {
            this.mThumbnailChangeListeners.get(size).onTaskIconChanged(pkg, user);
        }
    }

    public void addThumbnailChangeListener(TaskVisualsChangeListener listener) {
        this.mThumbnailChangeListeners.add(listener);
    }

    public void removeThumbnailChangeListener(TaskVisualsChangeListener listener) {
        this.mThumbnailChangeListeners.remove(listener);
    }

    public void callOnTaskStackChanged() {
        this.mTaskList.onTaskStackChanged();
    }
}
