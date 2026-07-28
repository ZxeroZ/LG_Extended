package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.BaseContainerView;
import com.android.launcher3.DragSource;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.WidgetsModel;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.PackageUserKey;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsContainerView extends BaseContainerView implements View.OnLongClickListener, View.OnClickListener, DragSource {
    private static final boolean DEBUG = false;
    private static final boolean LOGD = false;
    private static final int PRELOAD_SCREEN_HEIGHT_MULTIPLE = 1;
    private static final String TAG = "WidgetsContainerView";
    private WidgetsListAdapter mAdapter;
    protected View mContent;
    private DragController mDragController;
    private IconCache mIconCache;
    protected Launcher mLauncher;
    private Rect mPadding;
    private WidgetsRecyclerView mView;
    private Toast mWidgetInstructionToast;
    protected WidgetPreviewLoader mWidgetPreviewLoader;

    @Override // com.android.launcher3.DragSource
    public float getIntrinsicIconScaleFactor() {
        return 0.0f;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsDeleteDropTarget() {
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsFlingToDelete() {
        return false;
    }

    public WidgetsContainerView(Context context) {
        this(context, null);
    }

    public WidgetsContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetsContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPadding = new Rect();
        Launcher launcher = Launcher.getLauncher(context);
        this.mLauncher = launcher;
        this.mDragController = launcher.getDragController();
        this.mAdapter = new WidgetsListAdapter(this, this, context);
        this.mIconCache = LauncherAppState.getInstance(context).getIconCache();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        this.mContent = findViewById(R.id.content);
        WidgetsRecyclerView widgetsRecyclerView = (WidgetsRecyclerView) findViewById(R.id.widgets_list_view);
        this.mView = widgetsRecyclerView;
        widgetsRecyclerView.setAdapter(this.mAdapter);
        this.mView.setLayoutManager(new LinearLayoutManager(getContext()) { // from class: com.android.launcher3.widget.WidgetsContainerView.1
            @Override // androidx.recyclerview.widget.LinearLayoutManager
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return super.getExtraLayoutSpace(state) + (WidgetsContainerView.this.mLauncher.getDeviceProfile().availableHeightPx * 1);
            }
        });
        this.mPadding.set(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    public View getContentView() {
        return this.mView;
    }

    public View getRevealView() {
        return findViewById(R.id.widgets_reveal_view);
    }

    public void scrollToTop() {
        this.mView.scrollToPosition(0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (this.mLauncher.isWidgetsViewVisible() && !this.mLauncher.getWorkspace().isSwitchingState() && (v instanceof WidgetCell)) {
            handleClick();
        }
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        if (this.mLauncher.isWidgetsViewVisible()) {
            return handleLongClick(v);
        }
        return false;
    }

    private boolean beginDragging(View v) {
        if (v instanceof WidgetCell) {
            if (!beginDraggingWidget((WidgetCell) v)) {
                return false;
            }
        } else {
            Log.e(TAG, "Unexpected dragging view: " + v);
        }
        if (!this.mLauncher.getDragController().isDragging()) {
            return true;
        }
        this.mLauncher.enterSpringLoadedDragMode();
        return true;
    }

    private boolean beginDraggingWidget(WidgetCell v) {
        Bitmap bitmapCreateIconBitmap;
        float fWidth;
        int width;
        WidgetImageView widgetImageView = (WidgetImageView) v.findViewById(R.id.widget_preview);
        PendingAddItemInfo pendingAddItemInfo = (PendingAddItemInfo) v.getTag();
        if (widgetImageView.getBitmap() == null) {
            return false;
        }
        Rect bitmapBounds = widgetImageView.getBitmapBounds();
        boolean z = pendingAddItemInfo instanceof PendingAddWidgetInfo;
        if (z) {
            PendingAddWidgetInfo pendingAddWidgetInfo = (PendingAddWidgetInfo) pendingAddItemInfo;
            int[] iArrEstimateItemSize = this.mLauncher.getWorkspace().estimateItemSize(pendingAddWidgetInfo, true);
            Bitmap bitmap = widgetImageView.getBitmap();
            int iMin = Math.min((int) (bitmap.getWidth() * 1.25f), iArrEstimateItemSize[0]);
            int[] iArr = new int[1];
            bitmapCreateIconBitmap = getWidgetPreviewLoader().generateWidgetPreview(this.mLauncher, pendingAddWidgetInfo.info, iMin, null, iArr);
            if (iArr[0] < bitmap.getWidth()) {
                int width2 = (bitmap.getWidth() - iArr[0]) / 2;
                if (bitmap.getWidth() > widgetImageView.getWidth()) {
                    width2 = (width2 * widgetImageView.getWidth()) / bitmap.getWidth();
                }
                bitmapBounds.left += width2;
                bitmapBounds.right -= width2;
            }
            fWidth = bitmapBounds.width();
            width = bitmapCreateIconBitmap.getWidth();
        } else {
            bitmapCreateIconBitmap = Utilities.createIconBitmap(((PendingAddShortcutInfo) v.getTag()).activityInfo.getFullResIcon(this.mIconCache), this.mLauncher);
            pendingAddItemInfo.spanY = 1;
            pendingAddItemInfo.spanX = 1;
            fWidth = this.mLauncher.getDeviceProfile().iconSizePx;
            width = bitmapCreateIconBitmap.getWidth();
        }
        float f = fWidth / width;
        Bitmap bitmap2 = bitmapCreateIconBitmap;
        boolean z2 = (z && ((PendingAddWidgetInfo) pendingAddItemInfo).previewImage == 0) ? false : true;
        this.mLauncher.lockScreenOrientation();
        this.mLauncher.getWorkspace().onDragStartedWithItem(pendingAddItemInfo, bitmap2, z2);
        this.mDragController.startDrag(widgetImageView, bitmap2, this, pendingAddItemInfo, bitmapBounds, DragController.DRAG_ACTION_COPY, f);
        bitmap2.recycle();
        return true;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsAppInfoDropTarget() {
        return !LGHomeFeature.isEnableDefaultHome();
    }

    @Override // com.android.launcher3.DragSource
    public void onFlingToDeleteCompleted() {
        this.mLauncher.exitSpringLoadedDragModeDelayed(true, 300, null);
        this.mLauncher.unlockScreenOrientation(false);
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x004a  */
    @Override // com.android.launcher3.DragSource
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onDropCompleted(android.view.View r4, com.android.launcher3.DropTarget.DragObject r5, boolean r6, boolean r7) {
        /*
            r3 = this;
            r0 = 0
            r1 = 1
            if (r6 != 0) goto L16
            if (r7 == 0) goto L16
            com.android.launcher3.Launcher r6 = r3.mLauncher
            com.android.launcher3.Workspace r6 = r6.getWorkspace()
            if (r4 == r6) goto L1d
            boolean r6 = r4 instanceof com.android.launcher3.DeleteDropTarget
            if (r6 != 0) goto L1d
            boolean r6 = r4 instanceof com.android.launcher3.folder.Folder
            if (r6 != 0) goto L1d
        L16:
            com.android.launcher3.Launcher r6 = r3.mLauncher
            r2 = 300(0x12c, float:4.2E-43)
            r6.exitSpringLoadedDragModeDelayed(r1, r2, r0)
        L1d:
            com.android.launcher3.Launcher r6 = r3.mLauncher
            r2 = 0
            r6.unlockScreenOrientation(r2)
            if (r7 != 0) goto L54
            boolean r6 = r4 instanceof com.android.launcher3.Workspace
            if (r6 == 0) goto L4a
            com.android.launcher3.Launcher r6 = r3.mLauncher
            int r6 = r6.getCurrentWorkspaceScreen()
            com.android.launcher3.Workspace r4 = (com.android.launcher3.Workspace) r4
            android.view.View r4 = r4.getChildAt(r6)
            com.android.launcher3.CellLayout r4 = (com.android.launcher3.CellLayout) r4
            java.lang.Object r6 = r5.dragInfo
            com.android.launcher3.model.data.ItemInfo r6 = (com.android.launcher3.model.data.ItemInfo) r6
            if (r4 == 0) goto L4a
            r4.calculateSpans(r6)
            int r7 = r6.spanX
            int r6 = r6.spanY
            boolean r4 = r4.findCellForSpan(r0, r7, r6)
            r4 = r4 ^ r1
            goto L4b
        L4a:
            r4 = r2
        L4b:
            if (r4 == 0) goto L52
            com.android.launcher3.Launcher r4 = r3.mLauncher
            r4.showOutOfSpaceMessage(r2)
        L52:
            r5.deferDragViewCleanupPostAnimation = r2
        L54:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.widget.WidgetsContainerView.onDropCompleted(android.view.View, com.android.launcher3.DropTarget$DragObject, boolean, boolean):void");
    }

    @Override // com.android.launcher3.BaseContainerView
    protected void onUpdateBackgroundAndPaddings(Rect searchBarBounds, Rect padding) {
        this.mContent.setPadding(0, padding.top, 0, padding.bottom);
        InsetDrawable insetDrawable = new InsetDrawable(getResources().getDrawable(R.drawable.quantum_panel_shape_dark), padding.left, 0, padding.right, 0);
        Rect rect = new Rect();
        insetDrawable.getPadding(rect);
        this.mView.setBackground(insetDrawable);
        getRevealView().setBackground(insetDrawable.getConstantState().newDrawable());
        this.mView.updateBackgroundPadding(rect);
    }

    public void addWidgets(WidgetsModel model) {
        this.mView.setWidgets(model);
        this.mAdapter.setWidgetsModel(model);
        this.mAdapter.notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return this.mAdapter.getItemCount() == 0;
    }

    private WidgetPreviewLoader getWidgetPreviewLoader() {
        if (this.mWidgetPreviewLoader == null) {
            this.mWidgetPreviewLoader = LauncherAppState.getInstance(getContext()).getWidgetCache();
        }
        return this.mWidgetPreviewLoader;
    }

    public List<WidgetItem> getWidgetsForPackageUser(PackageUserKey packageUserKey) {
        return this.mAdapter.copyWidgetsForPackageUser(packageUserKey);
    }

    public void handleClick() {
        Toast toast = this.mWidgetInstructionToast;
        if (toast != null) {
            toast.cancel();
        }
        Toast toastMakeText = Toast.makeText(getContext(), Utilities.wrapForTts(getContext().getText(R.string.touch_widget_to_add), getContext().getString(R.string.touch_widget_to_add)), 0);
        this.mWidgetInstructionToast = toastMakeText;
        toastMakeText.show();
    }

    public boolean handleLongClick(View v) {
        if (this.mLauncher.getWorkspace().isSwitchingState() || !this.mLauncher.isDraggingEnabled()) {
            return false;
        }
        boolean zBeginDragging = beginDragging(v);
        if (zBeginDragging && (v.getTag() instanceof PendingAddWidgetInfo)) {
            new WidgetHostViewLoader(this.mLauncher, v).preloadWidget();
        }
        return zBeginDragging;
    }

    @Override // com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        targetParent.containerType = 5;
    }
}
