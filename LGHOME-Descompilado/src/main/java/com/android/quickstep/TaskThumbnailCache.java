package com.android.quickstep;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.cache.HandlerRunnable;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.Preconditions;
import com.android.quickstep.util.TaskKeyLruCache;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import java.util.ArrayList;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class TaskThumbnailCache {
    private final Handler mBackgroundHandler;
    private final TaskKeyLruCache<ThumbnailData> mCache;
    private final int mCacheSize;
    private final boolean mEnableTaskSnapshotPreloading;
    private final HighResLoadingState mHighResLoadingState;

    public static class HighResLoadingState {
        private ArrayList<HighResLoadingStateChangedCallback> mCallbacks;
        private boolean mFlingingFast;
        private boolean mForceHighResThumbnails;
        private boolean mHighResLoadingEnabled;
        private boolean mVisible;

        public interface HighResLoadingStateChangedCallback {
            void onHighResLoadingStateChanged(boolean enabled);
        }

        private HighResLoadingState(Context context) {
            this.mCallbacks = new ArrayList<>();
            this.mForceHighResThumbnails = !TaskThumbnailCache.supportsLowResThumbnails();
        }

        public void addCallback(HighResLoadingStateChangedCallback callback) {
            this.mCallbacks.add(callback);
        }

        public void removeCallback(HighResLoadingStateChangedCallback callback) {
            this.mCallbacks.remove(callback);
        }

        public void setVisible(boolean visible) {
            this.mVisible = visible;
            updateState();
        }

        public void setFlingingFast(boolean flingingFast) {
            this.mFlingingFast = flingingFast;
            updateState();
        }

        public boolean isEnabled() {
            return this.mHighResLoadingEnabled;
        }

        private void updateState() {
            boolean z = this.mHighResLoadingEnabled;
            boolean z2 = this.mForceHighResThumbnails || (this.mVisible && !this.mFlingingFast);
            this.mHighResLoadingEnabled = z2;
            if (z != z2) {
                for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
                    this.mCallbacks.get(size).onHighResLoadingStateChanged(this.mHighResLoadingEnabled);
                }
            }
        }
    }

    public TaskThumbnailCache(Context context, Looper backgroundLooper) {
        this.mBackgroundHandler = new Handler(backgroundLooper);
        this.mHighResLoadingState = new HighResLoadingState(context);
        Resources resources = context.getResources();
        int integer = resources.getInteger(R.integer.recentsThumbnailCacheSize);
        this.mCacheSize = integer;
        this.mEnableTaskSnapshotPreloading = resources.getBoolean(R.bool.config_enableTaskSnapshotPreloading);
        this.mCache = new TaskKeyLruCache<>(integer);
    }

    public void updateThumbnailInCache(final Task task) {
        Preconditions.assertUIThread();
        if (task.thumbnail == null) {
            updateThumbnailInBackground(task.key, true, new Consumer() { // from class: com.android.quickstep.-$$Lambda$TaskThumbnailCache$20QpHjcxR_b-6uqeV55Xi-Vo7wQ
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    task.thumbnail = (ThumbnailData) obj;
                }
            });
        }
    }

    public void updateTaskSnapShot(int taskId, ThumbnailData thumbnail) {
        Preconditions.assertUIThread();
        this.mCache.updateIfAlreadyInCache(taskId, thumbnail);
    }

    public ThumbnailLoadRequest updateThumbnailInBackground(final Task task, final Consumer<ThumbnailData> callback) {
        Preconditions.assertUIThread();
        boolean z = !this.mHighResLoadingState.isEnabled();
        if (task.thumbnail != null && (!task.thumbnail.reducedResolution || z)) {
            callback.accept(task.thumbnail);
            return null;
        }
        return updateThumbnailInBackground(task.key, !this.mHighResLoadingState.isEnabled(), new Consumer() { // from class: com.android.quickstep.-$$Lambda$TaskThumbnailCache$xqED9aYUB0LOUaMjNuSb_2Gt894
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TaskThumbnailCache.lambda$updateThumbnailInBackground$1(task, callback, (ThumbnailData) obj);
            }
        });
    }

    static /* synthetic */ void lambda$updateThumbnailInBackground$1(Task task, Consumer consumer, ThumbnailData thumbnailData) {
        task.thumbnail = thumbnailData;
        consumer.accept(thumbnailData);
    }

    private ThumbnailLoadRequest updateThumbnailInBackground(Task.TaskKey key, boolean lowResolution, Consumer<ThumbnailData> callback) {
        Preconditions.assertUIThread();
        ThumbnailData andInvalidateIfModified = this.mCache.getAndInvalidateIfModified(key);
        if (andInvalidateIfModified != null && (!andInvalidateIfModified.reducedResolution || lowResolution)) {
            callback.accept(andInvalidateIfModified);
            return null;
        }
        AnonymousClass1 anonymousClass1 = new AnonymousClass1(this.mBackgroundHandler, lowResolution, key, lowResolution, callback);
        Utilities.postAsyncCallback(this.mBackgroundHandler, anonymousClass1);
        return anonymousClass1;
    }

    /* JADX INFO: renamed from: com.android.quickstep.TaskThumbnailCache$1, reason: invalid class name */
    class AnonymousClass1 extends ThumbnailLoadRequest {
        final /* synthetic */ Consumer val$callback;
        final /* synthetic */ Task.TaskKey val$key;
        final /* synthetic */ boolean val$lowResolution;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(Handler handler, boolean lowResolution, final Task.TaskKey val$key, final boolean val$lowResolution, final Consumer val$callback) {
            super(handler, lowResolution);
            this.val$key = val$key;
            this.val$lowResolution = val$lowResolution;
            this.val$callback = val$callback;
        }

        @Override // java.lang.Runnable
        public void run() {
            final ThumbnailData taskThumbnail = ActivityManagerWrapper.getInstance().getTaskThumbnail(this.val$key.id, this.val$lowResolution);
            LooperExecutor looperExecutor = Executors.MAIN_EXECUTOR;
            final Task.TaskKey taskKey = this.val$key;
            final Consumer consumer = this.val$callback;
            looperExecutor.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$TaskThumbnailCache$1$mzP3glBNcG_6cRev_D6qFBdX7rc
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$run$0$TaskThumbnailCache$1(taskKey, taskThumbnail, consumer);
                }
            });
        }

        public /* synthetic */ void lambda$run$0$TaskThumbnailCache$1(Task.TaskKey taskKey, ThumbnailData thumbnailData, Consumer consumer) {
            if (isCanceled()) {
                return;
            }
            TaskThumbnailCache.this.mCache.put(taskKey, thumbnailData);
            consumer.accept(thumbnailData);
            onEnd();
        }
    }

    public void clear() {
        this.mCache.evictAll();
    }

    public void remove(Task.TaskKey key) {
        this.mCache.remove(key);
    }

    public int getCacheSize() {
        return this.mCacheSize;
    }

    public HighResLoadingState getHighResLoadingState() {
        return this.mHighResLoadingState;
    }

    public boolean isPreloadingEnabled() {
        return this.mEnableTaskSnapshotPreloading && this.mHighResLoadingState.mVisible;
    }

    public static abstract class ThumbnailLoadRequest extends HandlerRunnable {
        public final boolean mLowResolution;

        ThumbnailLoadRequest(Handler handler, boolean lowResolution) {
            super(handler, null);
            this.mLowResolution = lowResolution;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean supportsLowResThumbnails() {
        Resources system = Resources.getSystem();
        int identifier = system.getIdentifier("config_lowResTaskSnapshotScale", "dimen", LauncherConst.PACKAGE_NAME_NATIVE);
        return identifier == 0 || 0.0f < system.getFloat(identifier);
    }
}
