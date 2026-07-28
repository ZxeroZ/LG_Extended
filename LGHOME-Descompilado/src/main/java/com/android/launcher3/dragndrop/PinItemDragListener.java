package com.android.launcher3.dragndrop;

import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.compat.PinItemRequestCompat;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.PendingItemDragHelper;
import com.android.launcher3.widget.WidgetAddFlowHandler;
import com.lge.launcher3.R;
import com.lge.launcher3.util.PackageUtils;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class PinItemDragListener implements Parcelable, View.OnDragListener, DragSource, DragOptions.PreDragCondition {
    public static final Parcelable.Creator<PinItemDragListener> CREATOR = new Parcelable.Creator<PinItemDragListener>() { // from class: com.android.launcher3.dragndrop.PinItemDragListener.3
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinItemDragListener createFromParcel(Parcel source) {
            return new PinItemDragListener(source);
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinItemDragListener[] newArray(int size) {
            return new PinItemDragListener[size];
        }
    };
    public static final String EXTRA_PIN_ITEM_DRAG_LISTENER = "pin_item_drag_listener";
    private static final String MIME_TYPE_PREFIX = "com.android.launcher3.drag_and_drop/";
    private static final String TAG = "PinItemDragListener";
    private DragController mDragController;
    private long mDragStartTime;
    private final String mId;
    private Launcher mLauncher;
    private final int mPreviewBitmapWidth;
    private final Rect mPreviewRect;
    private final int mPreviewViewWidth;
    private final PinItemRequestCompat mRequest;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.android.launcher3.DragSource
    public float getIntrinsicIconScaleFactor() {
        return 1.0f;
    }

    @Override // com.android.launcher3.DragSource
    public void onFlingToDeleteCompleted() {
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsAppInfoDropTarget() {
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsDeleteDropTarget() {
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsFlingToDelete() {
        return false;
    }

    public PinItemDragListener(PinItemRequestCompat request, Rect previewRect, int previewBitmapWidth, int previewViewWidth) {
        this.mRequest = request;
        this.mPreviewRect = previewRect;
        this.mPreviewBitmapWidth = previewBitmapWidth;
        this.mPreviewViewWidth = previewViewWidth;
        this.mId = UUID.randomUUID().toString();
    }

    private PinItemDragListener(Parcel parcel) {
        this.mRequest = PinItemRequestCompat.CREATOR.createFromParcel(parcel);
        this.mPreviewRect = (Rect) Rect.CREATOR.createFromParcel(parcel);
        this.mPreviewBitmapWidth = parcel.readInt();
        this.mPreviewViewWidth = parcel.readInt();
        this.mId = parcel.readString();
    }

    public String getMimeType() {
        return MIME_TYPE_PREFIX + this.mId;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        this.mRequest.writeToParcel(parcel, i);
        this.mPreviewRect.writeToParcel(parcel, i);
        parcel.writeInt(this.mPreviewBitmapWidth);
        parcel.writeInt(this.mPreviewViewWidth);
        parcel.writeString(this.mId);
    }

    public void setLauncher(Launcher launcher) {
        this.mLauncher = launcher;
        this.mDragController = launcher.getDragController();
    }

    @Override // android.view.View.OnDragListener
    public boolean onDrag(View view, DragEvent event) {
        if (this.mLauncher == null || this.mDragController == null) {
            postCleanup();
            return false;
        }
        if (event.getAction() == 1) {
            if (onDragStart(event)) {
                return true;
            }
            postCleanup();
            return false;
        }
        return this.mDragController.onDragEvent(event);
    }

    private boolean onDragStart(DragEvent event) {
        Object pendingAddShortcutInfo;
        if (!this.mRequest.isValid()) {
            return false;
        }
        ClipDescription clipDescription = event.getClipDescription();
        if (clipDescription == null || !clipDescription.hasMimeType(getMimeType())) {
            Log.e(TAG, "Someone started a dragAndDrop before us.");
            return false;
        }
        if (this.mRequest.getRequestType() == 1) {
            pendingAddShortcutInfo = new PendingAddShortcutInfo(new PinShortcutRequestActivityInfo(this.mRequest, this.mLauncher));
        } else {
            Launcher launcher = this.mLauncher;
            LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfoFromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(launcher, this.mRequest.getAppWidgetProviderInfo(launcher));
            final PinWidgetFlowHandler pinWidgetFlowHandler = new PinWidgetFlowHandler(launcherAppWidgetProviderInfoFromProviderInfo, this.mRequest);
            pendingAddShortcutInfo = new PendingAddWidgetInfo(this.mLauncher, launcherAppWidgetProviderInfoFromProviderInfo, null) { // from class: com.android.launcher3.dragndrop.PinItemDragListener.1
                @Override // com.android.launcher3.widget.PendingAddWidgetInfo
                public WidgetAddFlowHandler getHandler() {
                    return pinWidgetFlowHandler;
                }
            };
        }
        View view = new View(this.mLauncher);
        view.setTag(pendingAddShortcutInfo);
        Point point = new Point((int) event.getX(), (int) event.getY());
        DragOptions dragOptions = new DragOptions();
        dragOptions.systemDndStartPoint = point;
        dragOptions.preDragCondition = this;
        PendingItemDragHelper pendingItemDragHelper = new PendingItemDragHelper(view);
        if (this.mRequest.getRequestType() == 2) {
            pendingItemDragHelper.setPreview(getPreview(this.mRequest));
        }
        pendingItemDragHelper.startDrag(new Rect(this.mPreviewRect), this.mPreviewBitmapWidth, this.mPreviewViewWidth, point, this, dragOptions);
        this.mDragStartTime = SystemClock.uptimeMillis();
        return true;
    }

    @Override // com.android.launcher3.dragndrop.DragOptions.PreDragCondition
    public boolean shouldStartDrag(double distanceDragged) {
        return !this.mLauncher.isWorkspaceLocked();
    }

    @Override // com.android.launcher3.dragndrop.DragOptions.PreDragCondition
    public void onPreDragStart(DropTarget.DragObject dragObject) {
        this.mLauncher.getDragLayer().setAlpha(1.0f);
        dragObject.dragView.setColor(this.mLauncher.getResources().getColor(R.color.delete_target_hover_tint));
    }

    @Override // com.android.launcher3.dragndrop.DragOptions.PreDragCondition
    public void onPreDragEnd(DropTarget.DragObject dragObject, boolean dragStarted) {
        if (dragStarted) {
            dragObject.dragView.setColor(0);
        }
    }

    @Override // com.android.launcher3.DragSource
    public void onDropCompleted(View target, DropTarget.DragObject d, boolean isFlingToDelete, boolean success) {
        if (isFlingToDelete || !success || (target != this.mLauncher.getWorkspace() && !(target instanceof DeleteDropTarget) && !(target instanceof Folder))) {
            this.mLauncher.exitSpringLoadedDragModeDelayed(true, 300, null);
        }
        if (!success) {
            d.deferDragViewCleanupPostAnimation = false;
        }
        postCleanup();
    }

    @Override // com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        targetParent.containerType = 10;
    }

    private void postCleanup() {
        if (this.mLauncher != null) {
            Intent intent = new Intent(this.mLauncher.getIntent());
            intent.removeExtra(EXTRA_PIN_ITEM_DRAG_LISTENER);
            this.mLauncher.setIntent(intent);
            this.mLauncher.setIsPinItemDragging(false);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.android.launcher3.dragndrop.PinItemDragListener.2
            @Override // java.lang.Runnable
            public void run() {
                PinItemDragListener.this.removeListener();
            }
        });
    }

    public void removeListener() {
        Launcher launcher = this.mLauncher;
        if (launcher != null) {
            launcher.getDragLayer().setOnDragListener(null);
        }
    }

    public static RemoteViews getPreview(PinItemRequestCompat request) {
        Bundle extras = request.getExtras();
        if (extras == null || !(extras.get("appWidgetPreview") instanceof RemoteViews)) {
            return null;
        }
        return (RemoteViews) extras.get("appWidgetPreview");
    }

    public static boolean handleDragRequest(Launcher launcher, Intent intent) {
        if (intent != null && PackageUtils.ANDROID_INTENT_ACTION_MAIN.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent.getParcelableExtra(EXTRA_PIN_ITEM_DRAG_LISTENER);
            if (parcelableExtra instanceof PinItemDragListener) {
                PinItemDragListener pinItemDragListener = (PinItemDragListener) parcelableExtra;
                pinItemDragListener.setLauncher(launcher);
                launcher.getDragLayer().setOnDragListener(pinItemDragListener);
                launcher.setIsPinItemDragging(true);
                return true;
            }
        }
        return false;
    }
}
