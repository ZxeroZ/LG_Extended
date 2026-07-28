package com.lge.launcher3.dynamicgrid;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Workspace;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.lge.launcher3.PagedView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.TalkBackUtils;
import com.lge.launcher3.util.dialog.LoadingProgressDialogAsyncTask;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class DynamicGridPannelView extends LinearLayout {
    private static final int INVALID_INDEX = -1;
    private static final int mMaxTableCount = 5;
    private Button mApplyButton;
    private Button mCancelButton;
    private String[] mDefaultString;
    private DynamicGrid mDynamicGrid;
    private DynamicGridManager mDynamicGridManager;
    private int[] mGridPreviewDisableBG;
    private int[] mGridPreviewEnableBG;
    private Launcher mLauncher;
    private int mOriginalIndex;
    private int mSelectedGridIndex;
    private View[] mTableLayoutIcon;
    private LinearLayout[] mTableList;
    private TextView[] mTextViews;

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public DynamicGridPannelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOriginalIndex = -1;
        this.mSelectedGridIndex = -1;
        this.mGridPreviewDisableBG = new int[]{R.drawable.ic_homescreen_grid4x4_normal, R.drawable.ic_homescreen_grid4x5_normal, R.drawable.ic_homescreen_grid4x6_normal, R.drawable.ic_homescreen_grid5x5_normal, R.drawable.ic_homescreen_grid5x6_normal};
        this.mGridPreviewEnableBG = new int[]{R.drawable.ic_homescreen_grid4x4_select, R.drawable.ic_homescreen_grid4x5_select, R.drawable.ic_homescreen_grid4x6_select, R.drawable.ic_homescreen_grid5x5_select, R.drawable.ic_homescreen_grid5x6_select};
        this.mLauncher = (Launcher) context;
        this.mDynamicGridManager = new DynamicGridManager(this.mLauncher);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        initDynamicGridView(this);
    }

    public void initDynamicGridView(View dynamicGridPannel) {
        ArrayList<int[]> grids = this.mDynamicGridManager.getGrids();
        setupView();
        if (this.mDefaultString == null) {
            this.mDefaultString = this.mDynamicGridManager.getPresetArray();
        }
        int size = grids.size();
        for (int i = 0; i < size; i++) {
            int[] iArr = grids.get(i);
            if (iArr[1] > 0) {
                for (int i2 = 0; i2 < iArr[1]; i2++) {
                    this.mTableList[i].setVisibility(0);
                }
            } else {
                this.mTableList[i].setVisibility(8);
            }
            this.mTextViews[i].setText(this.mDefaultString[i]);
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        Workspace workspace = this.mLauncher.getWorkspace();
        if (visibility == 0) {
            startDynamicGrid();
            if (workspace != null) {
                workspace.showAllCrossHair(true);
                workspace.disableLayoutTransitions();
                return;
            }
            return;
        }
        restoreOriginalGrid();
        if (workspace != null) {
            workspace.enableLayoutTransitions();
            workspace.showAllCrossHair(false);
        }
    }

    private void startDynamicGrid() {
        this.mDynamicGridManager.init();
        this.mSelectedGridIndex = this.mDynamicGridManager.getCurrentGridIndex();
        this.mDynamicGrid = this.mDynamicGridManager.getDynamicGrid();
        int currentGridIndex = this.mDynamicGridManager.getCurrentGridIndex();
        this.mOriginalIndex = currentGridIndex;
        this.mSelectedGridIndex = currentGridIndex;
        setTalkbackItem(currentGridIndex, false);
        setDynamicGridTableBorder(this.mOriginalIndex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDynamicGridTableBorder(int index) {
        int i = 0;
        while (true) {
            View[] viewArr = this.mTableLayoutIcon;
            if (i >= viewArr.length) {
                return;
            }
            if (index == i) {
                viewArr[i].setBackgroundResource(this.mGridPreviewEnableBG[i]);
            } else {
                viewArr[i].setBackgroundResource(this.mGridPreviewDisableBG[i]);
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTalkbackItem(int selectIndex, boolean isSendAccessibility) {
        TextView[] textViewArr;
        Resources resources = this.mLauncher.getResources();
        int i = 0;
        while (true) {
            textViewArr = this.mTextViews;
            if (i >= textViewArr.length) {
                break;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(resources.getString(R.string.dynamic_gird_label) + ",");
            sb.append(this.mDefaultString[i]);
            this.mTextViews[i].setContentDescription(sb.toString());
            i++;
        }
        if (!isSendAccessibility || textViewArr[selectIndex] == null) {
            return;
        }
        TalkBackUtils.setTalkBack(textViewArr[selectIndex], "");
    }

    private void setupView() {
        View[] viewArr = new View[5];
        this.mTableLayoutIcon = viewArr;
        final int i = 0;
        viewArr[0] = findViewById(R.id.overview_dynaic_grid_icon1);
        this.mTableLayoutIcon[1] = findViewById(R.id.overview_dynaic_grid_icon2);
        this.mTableLayoutIcon[2] = findViewById(R.id.overview_dynaic_grid_icon3);
        this.mTableLayoutIcon[3] = findViewById(R.id.overview_dynaic_grid_icon4);
        this.mTableLayoutIcon[4] = findViewById(R.id.overview_dynaic_grid_icon5);
        TextView[] textViewArr = new TextView[5];
        this.mTextViews = textViewArr;
        textViewArr[0] = (TextView) findViewById(R.id.overview_text1);
        this.mTextViews[1] = (TextView) findViewById(R.id.overview_text2);
        this.mTextViews[2] = (TextView) findViewById(R.id.overview_text3);
        this.mTextViews[3] = (TextView) findViewById(R.id.overview_text4);
        this.mTextViews[4] = (TextView) findViewById(R.id.overview_text5);
        LinearLayout[] linearLayoutArr = new LinearLayout[5];
        this.mTableList = linearLayoutArr;
        linearLayoutArr[0] = (LinearLayout) findViewById(R.id.overview_dynaic_grid_layout1);
        this.mTableList[1] = (LinearLayout) findViewById(R.id.overview_dynaic_grid_layout2);
        this.mTableList[2] = (LinearLayout) findViewById(R.id.overview_dynaic_grid_layout3);
        this.mTableList[3] = (LinearLayout) findViewById(R.id.overview_dynaic_grid_layout4);
        this.mTableList[4] = (LinearLayout) findViewById(R.id.overview_dynaic_grid_layout5);
        while (true) {
            LinearLayout[] linearLayoutArr2 = this.mTableList;
            if (i < linearLayoutArr2.length) {
                linearLayoutArr2[i].setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.dynamicgrid.DynamicGridPannelView.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        int i2 = DynamicGridPannelView.this.mSelectedGridIndex;
                        int i3 = i;
                        if (i2 != i3) {
                            DynamicGridPannelView.this.mSelectedGridIndex = i3;
                            DynamicGridPannelView dynamicGridPannelView = DynamicGridPannelView.this;
                            dynamicGridPannelView.setDynamicGridTableBorder(dynamicGridPannelView.mSelectedGridIndex);
                            DynamicGridPannelView dynamicGridPannelView2 = DynamicGridPannelView.this;
                            dynamicGridPannelView2.setTalkbackItem(dynamicGridPannelView2.mSelectedGridIndex, true);
                            DynamicGridPannelView dynamicGridPannelView3 = DynamicGridPannelView.this;
                            dynamicGridPannelView3.applyPreviewGrid(i2, dynamicGridPannelView3.mSelectedGridIndex, true);
                        }
                    }
                });
                this.mTableList[i].setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.lge.launcher3.dynamicgrid.DynamicGridPannelView.2
                    @Override // android.view.View.OnFocusChangeListener
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (DynamicGridPannelView.this.mSelectedGridIndex != i) {
                            if (hasFocus) {
                                DynamicGridPannelView.this.mTableLayoutIcon[i].setBackgroundResource(R.drawable.dynamic_grid_overview_border_focus);
                            } else {
                                DynamicGridPannelView.this.mTableLayoutIcon[i].setBackgroundColor(0);
                            }
                        }
                    }
                });
                i++;
            } else {
                Button button = (Button) findViewById(R.id.overview_dynamicgrid_apply_btn);
                this.mApplyButton = button;
                button.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.dynamicgrid.DynamicGridPannelView.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (DynamicGridPannelView.this.mSelectedGridIndex != DynamicGridPannelView.this.mOriginalIndex) {
                            DynamicGridPannelView.this.mDynamicGridManager.runDynamicGrid(DynamicGridPannelView.this.mSelectedGridIndex);
                            new LoadingProgressDialogAsyncTask(new ContextThemeWrapper(DynamicGridPannelView.this.mLauncher, 34210242)).show(1000);
                        } else {
                            DynamicGridPannelView.this.mLauncher.showWorkspace(true);
                        }
                    }
                });
                Button button2 = (Button) findViewById(R.id.overview_dynamicgrid_cancel_btn);
                this.mCancelButton = button2;
                button2.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.dynamicgrid.DynamicGridPannelView.4
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (DynamicGridPannelView.this.mLauncher != null) {
                            DynamicGridPannelView.this.mLauncher.onBackPressed();
                        }
                    }
                });
                return;
            }
        }
    }

    public void restoreOriginalGrid() {
        int i = this.mOriginalIndex;
        if (i == -1) {
            return;
        }
        applyPreviewGrid(this.mSelectedGridIndex, i, false);
        this.mOriginalIndex = -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyPreviewGrid(final int currentIndex, final int selectedIndex, boolean showCrossHair) {
        if (this.mDynamicGridManager == null) {
            return;
        }
        GridInfo gridInfoByIndex = getGridInfoByIndex(selectedIndex);
        Workspace workspace = (Workspace) this.mLauncher.findViewById(R.id.workspace);
        long representScreenId = getRepresentScreenId(currentIndex, selectedIndex, workspace);
        ArrayList<Long> pageOrders = gridInfoByIndex.getPageOrders();
        insertPreviewScreen(workspace, pageOrders);
        changePreviewWidgetSize(selectedIndex);
        changePreviewScreenGrid(selectedIndex);
        moveItemForSelectedGrid(selectedIndex);
        removeUnusedPreviewScreen(workspace, pageOrders);
        if (workspace != null) {
            workspace.showAllCrossHair(showCrossHair);
            workspace.snapToPageImmediately(workspace.getPageIndexForScreenId(representScreenId));
        }
    }

    private long getRepresentScreenId(int currentIndex, int selectedIndex, Workspace workspace) {
        long screenIdForPageIndex = workspace.getScreenIdForPageIndex(workspace.getCurrentPage());
        return getGridInfoByIndex(selectedIndex).getPageOrders().contains(Long.valueOf(screenIdForPageIndex)) ? screenIdForPageIndex : getGridInfoByIndex(currentIndex).getRepresentScreenId(screenIdForPageIndex);
    }

    private GridInfo getGridInfoByIndex(int index) {
        ArrayList<int[]> grids = this.mDynamicGridManager.getGrids();
        return this.mDynamicGrid.getGridInfo(grids.get(index)[0], grids.get(index)[1]);
    }

    private void changePreviewScreenGrid(int index) {
        ArrayList<int[]> grids = this.mDynamicGridManager.getGrids();
        Workspace workspace = (Workspace) this.mLauncher.findViewById(R.id.workspace);
        for (int i = 0; i < workspace.getChildCount(); i++) {
            ((CellLayout) workspace.getChildAt(i)).updateGridSize(grids.get(index)[0], grids.get(index)[1]);
        }
    }

    private void changePreviewWidgetSize(int selectedGridIndex) {
        ItemInfo itemInfo;
        GridInfo gridInfoByIndex = getGridInfoByIndex(selectedGridIndex);
        if (gridInfoByIndex == null) {
            return;
        }
        Workspace workspace = (Workspace) this.mLauncher.findViewById(R.id.workspace);
        for (int i = 0; i < workspace.getChildCount(); i++) {
            ShortcutAndWidgetContainer shortcutsAndWidgets = ((CellLayout) workspace.getChildAt(i)).getShortcutsAndWidgets();
            for (int i2 = 0; i2 < shortcutsAndWidgets.getChildCount(); i2++) {
                View childAt = shortcutsAndWidgets.getChildAt(i2);
                if ((childAt instanceof LauncherAppWidgetHostView) && (itemInfo = gridInfoByIndex.getItemInfo(((LauncherAppWidgetInfo) childAt.getTag()).id)) != null) {
                    if (itemInfo.spanX >= 1 || itemInfo.spanY >= 1) {
                        showItem(childAt);
                        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) childAt.getLayoutParams();
                        layoutParams.cellX = itemInfo.cellX;
                        layoutParams.cellY = itemInfo.cellY;
                        layoutParams.tmpCellX = itemInfo.cellX;
                        layoutParams.tmpCellY = itemInfo.cellY;
                        layoutParams.cellHSpan = itemInfo.spanX;
                        layoutParams.cellVSpan = itemInfo.spanY;
                    } else {
                        hidItem(childAt);
                    }
                }
            }
        }
    }

    private void hidItem(View child) {
        child.setAlpha(0.0f);
    }

    private void showItem(View child) {
        child.setAlpha(1.0f);
    }

    protected void moveItemForSelectedGrid(int selectedGridIndex) {
        GridInfo gridInfo;
        Workspace workspace;
        int i;
        CellLayout cellLayout;
        ItemInfo itemInfo;
        GridInfo gridInfoByIndex = getGridInfoByIndex(selectedGridIndex);
        if (gridInfoByIndex == null) {
            return;
        }
        Workspace workspace2 = (Workspace) this.mLauncher.findViewById(R.id.workspace);
        for (int i2 = 0; i2 < workspace2.getChildCount(); i2++) {
            CellLayout cellLayout2 = (CellLayout) workspace2.getChildAt(i2);
            ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout2.getShortcutsAndWidgets();
            int childCount = shortcutsAndWidgets.getChildCount() - 1;
            while (childCount >= 0) {
                View childAt = shortcutsAndWidgets.getChildAt(childCount);
                ItemInfo itemInfo2 = (ItemInfo) childAt.getTag();
                if (itemInfo2 == null || (itemInfo = gridInfoByIndex.getItemInfo(itemInfo2.id)) == null || (itemInfo2.screenId == itemInfo.screenId && itemInfo2.cellX == itemInfo.cellX && itemInfo2.cellY == itemInfo.cellY)) {
                    gridInfo = gridInfoByIndex;
                    workspace = workspace2;
                    i = childCount;
                    cellLayout = cellLayout2;
                } else {
                    cellLayout2.removeView(childAt);
                    gridInfo = gridInfoByIndex;
                    workspace = workspace2;
                    cellLayout = cellLayout2;
                    i = childCount;
                    workspace2.addInScreen(childAt, itemInfo.container, itemInfo.screenId, itemInfo.cellX, itemInfo.cellY, itemInfo.spanX, itemInfo.spanY);
                    itemInfo2.screenId = itemInfo.screenId;
                    itemInfo2.cellX = itemInfo.cellX;
                    itemInfo2.cellY = itemInfo.cellY;
                    itemInfo2.spanX = itemInfo.spanX;
                    itemInfo2.spanY = itemInfo.spanY;
                    CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) childAt.getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams.tmpCellX = itemInfo2.cellX;
                        layoutParams.tmpCellY = itemInfo2.cellY;
                    }
                }
                childCount = i - 1;
                gridInfoByIndex = gridInfo;
                cellLayout2 = cellLayout;
                workspace2 = workspace;
            }
        }
    }

    private void removeUnusedPreviewScreen(Workspace workspace, ArrayList<Long> tempScreenOrder) {
        ArrayList<Long> screenOrder = workspace.getScreenOrder();
        for (int size = screenOrder.size() - 1; size >= 0; size--) {
            if (screenOrder.get(size).longValue() != -201 && screenOrder.get(size).longValue() != -301 && screenOrder.get(size).longValue() != -401) {
                CellLayout cellLayout = (CellLayout) workspace.getChildAt(size);
                if (cellLayout.getShortcutsAndWidgets().getChildCount() == 0) {
                    workspace.removeWorkspaceScreen(screenOrder.get(size).longValue(), cellLayout);
                }
            }
        }
    }

    private void insertPreviewScreen(Workspace workspace, ArrayList<Long> arrayList) {
        ArrayList<Long> screenOrder = workspace.getScreenOrder();
        boolean zHasFullscreen = hasFullscreen(workspace);
        for (int i = 0; i < arrayList.size(); i++) {
            if (!screenOrder.contains(arrayList.get(i))) {
                workspace.insertNewWorkspaceScreen(arrayList.get(i).longValue(), i + (zHasFullscreen ? 1 : 0));
            }
        }
    }

    public boolean hasFullscreen(Workspace workspace) {
        View childAt;
        PagedView.LayoutParams layoutParams;
        if (workspace == null || (childAt = workspace.getChildAt(0)) == null || (layoutParams = (PagedView.LayoutParams) childAt.getLayoutParams()) == null) {
            return false;
        }
        return layoutParams.isFullScreenPage || layoutParams.isPreviewPage;
    }
}
