package com.android.systemui.shared.recents.model;

import android.app.ActivityManager;
import android.app.TaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.ViewDebug;
import java.io.PrintWriter;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class Task {
    public static final String TAG = "Task";

    @ViewDebug.ExportedProperty(category = "recents")
    public int colorBackground;

    @ViewDebug.ExportedProperty(category = "recents")
    public int colorPrimary;
    public Drawable icon;

    @ViewDebug.ExportedProperty(category = "recents")
    public boolean isDockable;

    @ViewDebug.ExportedProperty(category = "recents")
    public boolean isLocked;
    public boolean isPinned;

    @ViewDebug.ExportedProperty(deepExport = true, prefix = "key_")
    public TaskKey key;
    public ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData lastSnapshotData;
    public boolean supportSplitWindow;
    public ActivityManager.TaskDescription taskDescription;
    public ThumbnailData thumbnail;

    @ViewDebug.ExportedProperty(category = "recents")
    @Deprecated
    public String title;

    @ViewDebug.ExportedProperty(category = "recents")
    public String titleDescription;

    @ViewDebug.ExportedProperty(category = "recents")
    public ComponentName topActivity;

    public static class TaskKey implements Parcelable {
        public static final Parcelable.Creator<TaskKey> CREATOR = new Parcelable.Creator<TaskKey>() { // from class: com.android.systemui.shared.recents.model.Task.TaskKey.1
            /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TaskKey createFromParcel(Parcel parcel) {
                return TaskKey.readFromParcel(parcel);
            }

            /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TaskKey[] newArray(int i) {
                return new TaskKey[i];
            }
        };

        @ViewDebug.ExportedProperty(category = "recents")
        public final Intent baseIntent;

        @ViewDebug.ExportedProperty(category = "recents")
        public final int displayId;

        @ViewDebug.ExportedProperty(category = "recents")
        public final int id;

        @ViewDebug.ExportedProperty(category = "recents")
        public long lastActiveTime;
        private int mHashCode;
        public final ComponentName sourceComponent;

        @ViewDebug.ExportedProperty(category = "recents")
        public final int userId;

        @ViewDebug.ExportedProperty(category = "recents")
        public int windowingMode;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public TaskKey(TaskInfo taskInfo) {
            ComponentName componentName;
            if (taskInfo.origActivity != null) {
                componentName = taskInfo.origActivity;
            } else {
                componentName = taskInfo.realActivity;
            }
            this.id = taskInfo.taskId;
            this.windowingMode = taskInfo.configuration.windowConfiguration.getWindowingMode();
            this.baseIntent = taskInfo.baseIntent;
            this.sourceComponent = componentName;
            this.userId = taskInfo.userId;
            this.lastActiveTime = taskInfo.lastActiveTime;
            this.displayId = taskInfo.displayId;
            updateHashCode();
        }

        public TaskKey(int i, int i2, Intent intent, ComponentName componentName, int i3, long j) {
            this.id = i;
            this.windowingMode = i2;
            this.baseIntent = intent;
            this.sourceComponent = componentName;
            this.userId = i3;
            this.lastActiveTime = j;
            this.displayId = 0;
            updateHashCode();
        }

        public TaskKey(int i, int i2, Intent intent, ComponentName componentName, int i3, long j, int i4) {
            this.id = i;
            this.windowingMode = i2;
            this.baseIntent = intent;
            this.sourceComponent = componentName;
            this.userId = i3;
            this.lastActiveTime = j;
            this.displayId = i4;
            updateHashCode();
        }

        public void setWindowingMode(int i) {
            this.windowingMode = i;
            updateHashCode();
        }

        public ComponentName getComponent() {
            return this.baseIntent.getComponent();
        }

        public String getPackageName() {
            if (this.baseIntent.getComponent() != null) {
                return this.baseIntent.getComponent().getPackageName();
            }
            return this.baseIntent.getPackage();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof TaskKey)) {
                return false;
            }
            TaskKey taskKey = (TaskKey) obj;
            return this.id == taskKey.id && this.windowingMode == taskKey.windowingMode && this.userId == taskKey.userId;
        }

        public int hashCode() {
            return this.mHashCode;
        }

        public String toString() {
            return "id=" + this.id + " windowingMode=" + this.windowingMode + " user=" + this.userId + " lastActiveTime=" + this.lastActiveTime;
        }

        private void updateHashCode() {
            this.mHashCode = Objects.hash(Integer.valueOf(this.id), Integer.valueOf(this.windowingMode), Integer.valueOf(this.userId));
        }

        @Override // android.os.Parcelable
        public final void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.id);
            parcel.writeInt(this.windowingMode);
            parcel.writeTypedObject(this.baseIntent, i);
            parcel.writeInt(this.userId);
            parcel.writeLong(this.lastActiveTime);
            parcel.writeInt(this.displayId);
            parcel.writeTypedObject(this.sourceComponent, i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static TaskKey readFromParcel(Parcel parcel) {
            return new TaskKey(parcel.readInt(), parcel.readInt(), (Intent) parcel.readTypedObject(Intent.CREATOR), (ComponentName) parcel.readTypedObject(ComponentName.CREATOR), parcel.readInt(), parcel.readLong(), parcel.readInt());
        }
    }

    public Task() {
        this.lastSnapshotData = new ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData();
    }

    public static Task from(TaskKey taskKey, TaskInfo taskInfo, boolean z) {
        ActivityManager.TaskDescription taskDescription = taskInfo.taskDescription;
        return new Task(taskKey, taskDescription != null ? taskDescription.getPrimaryColor() : 0, taskDescription != null ? taskDescription.getBackgroundColor() : 0, taskInfo.supportsSplitScreenMultiWindow, z, taskDescription, taskInfo.topActivity);
    }

    public Task(TaskKey taskKey) {
        this.lastSnapshotData = new ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData();
        this.key = taskKey;
        this.taskDescription = new ActivityManager.TaskDescription();
    }

    public Task(Task task) {
        this(task.key, task.colorPrimary, task.colorBackground, task.isDockable, task.isLocked, task.taskDescription, task.topActivity);
        this.lastSnapshotData.set(task.lastSnapshotData);
    }

    @Deprecated
    public Task(TaskKey taskKey, int i, int i2, boolean z, boolean z2, ActivityManager.TaskDescription taskDescription, ComponentName componentName) {
        this.lastSnapshotData = new ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData();
        this.key = taskKey;
        this.colorPrimary = i;
        this.colorBackground = i2;
        this.taskDescription = taskDescription;
        this.isDockable = z;
        this.isLocked = z2;
        this.topActivity = componentName;
    }

    public Task(TaskKey taskKey, int i, int i2, boolean z, boolean z2, ActivityManager.TaskDescription taskDescription, ComponentName componentName, boolean z3) {
        this.lastSnapshotData = new ActivityManager.RecentTaskInfo.PersistedTaskSnapshotData();
        this.key = taskKey;
        this.colorPrimary = i;
        this.colorBackground = i2;
        this.taskDescription = taskDescription;
        this.isDockable = z;
        this.isLocked = z2;
        this.topActivity = componentName;
        this.isPinned = z3;
    }

    public ComponentName getTopComponent() {
        ComponentName componentName = this.topActivity;
        return componentName != null ? componentName : this.key.baseIntent.getComponent();
    }

    public void setLastSnapshotData(ActivityManager.RecentTaskInfo recentTaskInfo) {
        this.lastSnapshotData.set(recentTaskInfo.lastSnapshotData);
    }

    public float getVisibleThumbnailRatio(boolean z) {
        if (this.lastSnapshotData.taskSize == null || this.lastSnapshotData.contentInsets == null) {
            return 0.0f;
        }
        float f = this.lastSnapshotData.taskSize.x;
        float f2 = this.lastSnapshotData.taskSize.y;
        if (z) {
            f -= this.lastSnapshotData.contentInsets.left + this.lastSnapshotData.contentInsets.right;
            f2 -= this.lastSnapshotData.contentInsets.top + this.lastSnapshotData.contentInsets.bottom;
        }
        return f / f2;
    }

    public boolean equals(Object obj) {
        return this.key.equals(((Task) obj).key);
    }

    public String toString() {
        return "[" + this.key.toString() + "] " + this.title;
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print(this.key);
        if (!this.isDockable) {
            printWriter.print(" dockable=N");
        }
        if (this.isLocked) {
            printWriter.print(" locked=Y");
        }
        printWriter.print(" ");
        printWriter.print(this.title);
        printWriter.println();
    }
}
