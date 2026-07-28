package com.android.quickstep;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.ResourceBasedOverride;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.quickstep.TaskShortcutFactory;
import com.android.quickstep.views.OverviewActionsView;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.plugins.OverscrollPlugin;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.lge.launcher3.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class TaskOverlayFactory implements ResourceBasedOverride {
    public static final MainThreadInitializedObject<TaskOverlayFactory> INSTANCE = MainThreadInitializedObject.forOverride(TaskOverlayFactory.class, R.string.task_overlay_factory_class);
    private static final TaskShortcutFactory[] MENU_OPTIONS = {TaskShortcutFactory.APP_INFO, TaskShortcutFactory.SPLIT_SCREEN, TaskShortcutFactory.FREE_FORM, TaskShortcutFactory.APP_PIN, TaskShortcutFactory.PIN, TaskShortcutFactory.INSTALL, TaskShortcutFactory.GO_FULLSCREEN, TaskShortcutFactory.WELLBEING};

    public interface OverlayUICallbacks {
        void onScreenshot();

        void onShare();
    }

    public OverscrollPlugin getLocalOverscrollPlugin() {
        return null;
    }

    public static List<SystemShortcut> getEnabledShortcuts(TaskView taskView, DeviceProfile dp) {
        ArrayList arrayList = new ArrayList();
        BaseDraggingActivity baseDraggingActivity = (BaseDraggingActivity) BaseActivity.fromContext(taskView.getContext());
        for (TaskShortcutFactory taskShortcutFactory : MENU_OPTIONS) {
            SystemShortcut shortcut = taskShortcutFactory.getShortcut(baseDraggingActivity, taskView);
            if (shortcut != null) {
                if (taskShortcutFactory == TaskShortcutFactory.SPLIT_SCREEN && FeatureFlags.ENABLE_SPLIT_SELECT.get()) {
                    addSplitOptions(arrayList, baseDraggingActivity, taskView, dp);
                } else {
                    arrayList.add(shortcut);
                }
            }
        }
        return arrayList;
    }

    private static void addSplitOptions(List<SystemShortcut> outShortcuts, BaseDraggingActivity activity, TaskView taskView, DeviceProfile deviceProfile) {
        RecentsView recentsView = taskView.getRecentsView();
        PagedOrientationHandler pagedOrientationHandler = recentsView.getPagedOrientationHandler();
        int[] taskIds = taskView.getTaskIds();
        boolean z = false;
        boolean z2 = (taskIds[0] == -1 || taskIds[1] == -1) ? false : true;
        boolean z3 = recentsView.getTaskViewCount() < 2;
        if (deviceProfile.isTablet && taskView.isFocusedTask()) {
            z = true;
        }
        boolean zIsTaskInExpectedScrollPosition = recentsView.isTaskInExpectedScrollPosition(recentsView.indexOfChild(taskView));
        boolean zIsInLockTaskMode = ((ActivityManager) taskView.getContext().getSystemService("activity")).isInLockTaskMode();
        if (z2 || z3 || zIsInLockTaskMode) {
            return;
        }
        if (z && zIsTaskInExpectedScrollPosition) {
            return;
        }
        Iterator<SplitConfigurationOptions.SplitPositionOption> it = pagedOrientationHandler.getSplitPositionOptions(deviceProfile).iterator();
        while (it.hasNext()) {
            outShortcuts.add(new TaskShortcutFactory.SplitSelectSystemShortcut(activity, taskView, it.next()));
        }
    }

    public TaskOverlay createOverlay(TaskThumbnailView thumbnailView) {
        return new TaskOverlay(thumbnailView);
    }

    public static class TaskOverlay<T extends OverviewActionsView> {
        private final Context mApplicationContext;
        private ImageActionsApi mImageApi;
        private boolean mIsAllowedByPolicy;
        protected final TaskThumbnailView mThumbnailView;

        public SystemShortcut getModalStateSystemShortcut(WorkspaceItemInfo itemInfo) {
            return null;
        }

        public void initOverlay(Task task, ThumbnailData thumbnail, Matrix matrix, boolean rotated) {
        }

        public void reset() {
        }

        public void resetModalVisuals() {
        }

        protected TaskOverlay(final TaskThumbnailView taskThumbnailView) {
            Context applicationContext = taskThumbnailView.getContext().getApplicationContext();
            this.mApplicationContext = applicationContext;
            this.mThumbnailView = taskThumbnailView;
            Objects.requireNonNull(taskThumbnailView);
            this.mImageApi = new ImageActionsApi(applicationContext, new Supplier() { // from class: com.android.quickstep.-$$Lambda$LK3vnVdPxo0L6jUtdOWJlbMKHso
                @Override // java.util.function.Supplier
                public final Object get() {
                    return taskThumbnailView.getThumbnail();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void saveScreenshot(Task task) {
            if (this.mThumbnailView.isRealSnapshot()) {
                this.mImageApi.saveScreenshot(this.mThumbnailView.getThumbnail(), getTaskSnapshotBounds(), getTaskSnapshotInsets(), task.key);
            } else {
                showBlockedByPolicyMessage();
            }
        }

        public SystemShortcut getScreenshotShortcut(BaseDraggingActivity activity, ItemInfo iteminfo) {
            return new ScreenshotSystemShortcut(activity, iteminfo);
        }

        public Rect getTaskSnapshotBounds() {
            int[] iArr = new int[2];
            this.mThumbnailView.getLocationOnScreen(iArr);
            return new Rect(iArr[0], iArr[1], this.mThumbnailView.getWidth() + iArr[0], this.mThumbnailView.getHeight() + iArr[1]);
        }

        public Insets getTaskSnapshotInsets() {
            return this.mThumbnailView.getScaledInsets();
        }

        private void showBlockedByPolicyMessage() {
            Toast.makeText(this.mThumbnailView.getContext(), R.string.blocked_by_policy, 1).show();
        }

        private class ScreenshotSystemShortcut extends SystemShortcut {
            private final BaseDraggingActivity mActivity;

            ScreenshotSystemShortcut(BaseDraggingActivity activity, ItemInfo itemInfo) {
                super(R.drawable.ic_screenshot, R.string.action_screenshot, activity, itemInfo);
                this.mActivity = activity;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                TaskOverlay taskOverlay = TaskOverlay.this;
                taskOverlay.saveScreenshot(taskOverlay.mThumbnailView.getTaskView().getTask());
                dismissTaskMenuView(this.mActivity);
            }
        }
    }
}
