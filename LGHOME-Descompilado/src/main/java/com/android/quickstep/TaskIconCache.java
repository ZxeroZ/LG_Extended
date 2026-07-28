package com.android.quickstep;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityManager;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.icons.IconProvider;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.icons.cache.HandlerRunnable;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.Preconditions;
import com.android.quickstep.util.TaskKeyLruCache;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.lge.content.pm.PackageManagerEx;
import com.lge.launcher3.R;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class TaskIconCache {
    private final AccessibilityManager mAccessibilityManager;
    private final Handler mBackgroundHandler;
    private final Context mContext;
    private final TaskKeyLruCache<TaskCacheEntry> mIconCache;
    private final SparseArray<BitmapInfo> mDefaultIcons = new SparseArray<>();
    private final IconProvider mIconProvider = new IconProvider();

    public TaskIconCache(Context context, Looper backgroundLooper) {
        this.mContext = context;
        this.mBackgroundHandler = new Handler(backgroundLooper);
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        this.mIconCache = new TaskKeyLruCache<>(context.getResources().getInteger(R.integer.recentsIconCacheSize));
    }

    public IconLoadRequest updateIconInBackground(Task task, Consumer<Task> callback) {
        Preconditions.assertUIThread();
        if (task.icon != null) {
            callback.accept(task);
            return null;
        }
        AnonymousClass1 anonymousClass1 = new AnonymousClass1(this.mBackgroundHandler, task, callback);
        Utilities.postAsyncCallback(this.mBackgroundHandler, anonymousClass1);
        return anonymousClass1;
    }

    /* JADX INFO: renamed from: com.android.quickstep.TaskIconCache$1, reason: invalid class name */
    class AnonymousClass1 extends IconLoadRequest {
        final /* synthetic */ Consumer val$callback;
        final /* synthetic */ Task val$task;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(Handler handler, final Task val$task, final Consumer val$callback) {
            super(handler);
            this.val$task = val$task;
            this.val$callback = val$callback;
        }

        @Override // java.lang.Runnable
        public void run() {
            final TaskCacheEntry cacheEntry = TaskIconCache.this.getCacheEntry(this.val$task);
            if (isCanceled()) {
                return;
            }
            LooperExecutor looperExecutor = Executors.MAIN_EXECUTOR;
            final Task task = this.val$task;
            final Consumer consumer = this.val$callback;
            looperExecutor.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskIconCache$1$O6-Ja7HQq7EzZ8hNWdhp1yPRdDU
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$run$0$TaskIconCache$1(task, cacheEntry, consumer);
                }
            });
        }

        public /* synthetic */ void lambda$run$0$TaskIconCache$1(Task task, TaskCacheEntry taskCacheEntry, Consumer consumer) {
            task.title = taskCacheEntry.title;
            task.icon = taskCacheEntry.icon;
            task.titleDescription = taskCacheEntry.contentDescription;
            consumer.accept(task);
            onEnd();
        }
    }

    public void clear() {
        this.mIconCache.evictAll();
    }

    void onTaskRemoved(Task.TaskKey taskKey) {
        this.mIconCache.remove(taskKey);
    }

    void invalidateCacheEntries(final String pkg, final UserHandle handle) {
        Utilities.postAsyncCallback(this.mBackgroundHandler, new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskIconCache$S69qaxf82uYofHQYP1eTIZDXdOc
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$invalidateCacheEntries$1$TaskIconCache(pkg, handle);
            }
        });
    }

    public /* synthetic */ void lambda$invalidateCacheEntries$1$TaskIconCache(final String str, final UserHandle userHandle) {
        this.mIconCache.removeAll(new Predicate() { // from class: com.android.quickstep.-$$Lambda$TaskIconCache$on9T4Ir0uVVB1PgixNjUh91GbOg
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return TaskIconCache.lambda$invalidateCacheEntries$0(str, userHandle, (Task.TaskKey) obj);
            }
        });
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: ?: TERNARY null = (((wrap:boolean:0x0004: INVOKE 
      (r1v0 java.lang.String)
      (wrap:java.lang.String:0x0000: INVOKE (r3v0 com.android.systemui.shared.recents.model.Task$TaskKey) VIRTUAL call: com.android.systemui.shared.recents.model.Task.TaskKey.getPackageName():java.lang.String A[MD:():java.lang.String (m), WRAPPED] (LINE:133))
     VIRTUAL call: java.lang.String.equals(java.lang.Object):boolean A[MD:(java.lang.Object):boolean (c), WRAPPED] (LINE:133)) == true && (wrap:int:0x000a: INVOKE (r2v0 android.os.UserHandle) VIRTUAL call: android.os.UserHandle.getIdentifier():int A[MD:():int (s), WRAPPED]) == (wrap:int:0x000e: IGET (r3v0 com.android.systemui.shared.recents.model.Task$TaskKey) A[WRAPPED] com.android.systemui.shared.recents.model.Task.TaskKey.userId int))) ? true : false */
    static /* synthetic */ boolean lambda$invalidateCacheEntries$0(String str, UserHandle userHandle, Task.TaskKey taskKey) {
        return str.equals(taskKey.getPackageName()) && userHandle.getIdentifier() == taskKey.userId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TaskCacheEntry getCacheEntry(Task task) {
        TaskCacheEntry andInvalidateIfModified = this.mIconCache.getAndInvalidateIfModified(task.key);
        if (andInvalidateIfModified != null) {
            return andInvalidateIfModified;
        }
        ActivityManager.TaskDescription taskDescription = task.taskDescription;
        Task.TaskKey taskKey = task.key;
        TaskCacheEntry taskCacheEntry = new TaskCacheEntry();
        ActivityInfo activityInfo = PackageManagerWrapper.getInstance().getActivityInfo(taskKey.getComponent(), taskKey.userId);
        if (activityInfo != null) {
            Drawable shadowIconIfNeeded = IconCache.getShadowIconIfNeeded(this.mContext, PackageManagerEx.getDefault().getIconDrawableAsIconFrameTheme(this.mContext, this.mContext.getPackageManager().getDrawable(activityInfo.packageName, activityInfo.getIconResource(), activityInfo.applicationInfo), activityInfo.packageName, activityInfo.getIconResource()), activityInfo.packageName);
            if (shadowIconIfNeeded == null) {
                taskCacheEntry.icon = getDefaultIcon(taskKey.userId);
            } else {
                taskCacheEntry.icon = FastBitmapDrawable.newIcon(this.mContext, getBitmapInfo(shadowIconIfNeeded, taskKey.userId, taskDescription.getPrimaryColor(), activityInfo.applicationInfo.isInstantApp()));
            }
            taskCacheEntry.title = getAndUpdateActivityTitle(activityInfo, task.key);
        } else {
            taskCacheEntry.icon = getDefaultIcon(taskKey.userId);
        }
        if (this.mAccessibilityManager.isEnabled()) {
            if (activityInfo == null) {
                activityInfo = PackageManagerWrapper.getInstance().getActivityInfo(taskKey.getComponent(), taskKey.userId);
            }
            if (activityInfo != null) {
                taskCacheEntry.contentDescription = getBadgedContentDescription(activityInfo, task.key.userId, task.taskDescription);
            }
        }
        this.mIconCache.put(task.key, taskCacheEntry);
        return taskCacheEntry;
    }

    private String getBadgedContentDescription(ActivityInfo info, int userId, ActivityManager.TaskDescription td) {
        PackageManager packageManager = this.mContext.getPackageManager();
        String strTrim = td == null ? null : Utilities.trim(td.getLabel());
        if (TextUtils.isEmpty(strTrim)) {
            strTrim = Utilities.trim(info.loadLabel(packageManager));
        }
        String strTrim2 = Utilities.trim(info.applicationInfo.loadLabel(packageManager));
        String string = userId != UserHandle.myUserId() ? packageManager.getUserBadgedLabel(strTrim2, UserHandle.of(userId)).toString() : strTrim2;
        if (strTrim2.equals(strTrim)) {
            return string;
        }
        return string + " " + strTrim;
    }

    private Drawable getDefaultIcon(int userId) {
        FastBitmapDrawable fastBitmapDrawable;
        synchronized (this.mDefaultIcons) {
            BitmapInfo bitmapInfo = this.mDefaultIcons.get(userId);
            if (bitmapInfo == null) {
                LauncherIcons launcherIconsObtain = LauncherIcons.obtain(this.mContext);
                try {
                    BitmapInfo bitmapInfoMakeDefaultIcon = launcherIconsObtain.makeDefaultIcon(UserHandle.of(userId));
                    if (launcherIconsObtain != null) {
                        launcherIconsObtain.close();
                    }
                    this.mDefaultIcons.put(userId, bitmapInfoMakeDefaultIcon);
                    bitmapInfo = bitmapInfoMakeDefaultIcon;
                } finally {
                }
            }
            fastBitmapDrawable = new FastBitmapDrawable(bitmapInfo);
        }
        return fastBitmapDrawable;
    }

    private BitmapInfo getBitmapInfo(Drawable drawable, int userId, int primaryColor, boolean isInstantApp) {
        return BitmapInfo.fromBitmap(com.android.launcher3.graphics.LauncherIcons.createBadgedIconBitmap(drawable, UserHandle.of(userId), this.mContext, 26));
    }

    public static abstract class IconLoadRequest extends HandlerRunnable {
        IconLoadRequest(Handler handler) {
            super(handler, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class TaskCacheEntry {
        public String contentDescription;
        public Drawable icon;
        public String title;

        private TaskCacheEntry() {
            this.title = "";
            this.contentDescription = "";
        }
    }

    private String getAndUpdateActivityTitle(ActivityInfo activityinfo, Task.TaskKey taskKey) {
        return activityinfo != null ? getBadgedActivityLabel(activityinfo, taskKey.userId) : "";
    }

    public String getBadgedActivityLabel(ActivityInfo info, int userId) {
        return getBadgedLabel(info.loadLabel(this.mContext.getPackageManager()).toString(), userId);
    }

    private String getBadgedLabel(String label, int userId) {
        return userId != UserHandle.myUserId() ? this.mContext.getPackageManager().getUserBadgedLabel(label, new UserHandle(userId)).toString() : label;
    }
}
