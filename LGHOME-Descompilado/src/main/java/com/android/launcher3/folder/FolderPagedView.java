package com.android.launcher3.folder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.FocusHelper;
import com.android.launcher3.FocusIndicatorView;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.PageIndicator;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.PagedView;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.badge.appnotifier.IAppNotifierView;
import com.lge.launcher3.folder.FolderColorUtil;
import com.lge.launcher3.pageindicator.PageIndicatorExtension;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.VibratorManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class FolderPagedView extends PagedView {
    private static final boolean ALLOW_FOLDER_SCROLL = true;
    private static final boolean DEBUG_DATA_MODEL = true;
    private static final int PAGE_INDICATOR_ANIMATION_DURATION = 400;
    private static final int PAGE_INDICATOR_ANIMATION_STAGGERED_DELAY = 150;
    private static final int PAGE_INDICATOR_ANIMATION_START_DELAY = 300;
    private static final float PAGE_INDICATOR_OVERSHOOT_TENSION = 4.9f;
    private static final int REORDER_ANIMATION_DURATION = 230;
    private static final float SCROLL_HINT_FRACTION = 0.07f;
    private static final int START_VIEW_REORDER_DELAY = 30;
    private static final String TAG = "FolderPagedView";
    private static final float VIEW_REORDER_DELAY_FACTOR = 0.9f;
    private static final int[] sTempPosArray = new int[2];
    private int mAllocatedContentSize;
    private Folder.DataModel mDataModel;
    protected FocusIndicatorView mFocusIndicatorView;
    protected Folder mFolder;
    private int mGridCountX;
    private int mGridCountY;
    protected final IconCache mIconCache;
    protected final LayoutInflater mInflater;
    public final boolean mIsRtl;
    private boolean mIsVisibleAllCrossHair;
    protected FocusHelper.PagedFolderKeyEventListener mKeyListener;
    protected final int mMaxCountX;
    protected final int mMaxCountY;
    private final int mMaxItemsPerPage;
    private PageIndicator mPageIndicator;
    final HashMap<View, Runnable> mPendingAnimations;

    public boolean isFull() {
        return false;
    }

    public FolderPagedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPendingAnimations = new HashMap<>();
        this.mIsVisibleAllCrossHair = false;
        LauncherAppState launcherAppState = LauncherAppState.getInstance(context);
        InvariantDeviceProfile invariantDeviceProfile = launcherAppState.getInvariantDeviceProfile();
        DeviceProfile deviceProfile = ((Launcher) getContext()).getDeviceProfile();
        int i = invariantDeviceProfile.numFolderColumns;
        this.mMaxCountX = i;
        if (deviceProfile.allowRotation && deviceProfile.isLandscape && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            this.mMaxCountY = invariantDeviceProfile.numFolderRowsForSwivel;
        } else {
            this.mMaxCountY = invariantDeviceProfile.numFolderRows;
        }
        this.mMaxItemsPerPage = i * this.mMaxCountY;
        this.mInflater = LayoutInflater.from(context);
        this.mIconCache = launcherAppState.getIconCache();
        this.mIsRtl = Utilities.isRtl(getResources());
        setImportantForAccessibility(1);
        setEdgeGlowColor(getResources().getColor(R.color.folder_edge_effect_color));
    }

    public void setFolder(Folder folder) {
        this.mFolder = folder;
        this.mFocusIndicatorView = (FocusIndicatorView) folder.findViewById(R.id.focus_indicator);
        this.mKeyListener = new FocusHelper.PagedFolderKeyEventListener(folder);
        this.mPageIndicator = (PageIndicator) folder.findViewById(R.id.folder_page_indicator);
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0063  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0015  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:32:0x0060 -> B:5:0x0010). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:6:0x0012 -> B:7:0x0013). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void setupContentDimensions(int r6) {
        /*
            r5 = this;
            r5.mAllocatedContentSize = r6
            int r0 = r5.mMaxItemsPerPage
            r1 = 0
            r2 = 1
            if (r6 < r0) goto L12
            int r0 = r5.mMaxCountX
            r5.mGridCountX = r0
            int r0 = r5.mMaxCountY
            r5.mGridCountY = r0
        L10:
            r0 = r2
            goto L13
        L12:
            r0 = r1
        L13:
            if (r0 != 0) goto L63
            int r0 = r5.mGridCountX
            int r3 = r5.mGridCountY
            int r4 = r0 * r3
            if (r4 >= r6) goto L3d
            if (r0 <= r3) goto L23
            int r4 = r5.mMaxCountY
            if (r3 != r4) goto L2c
        L23:
            int r4 = r5.mMaxCountX
            if (r0 >= r4) goto L2c
            int r4 = r0 + 1
            r5.mGridCountX = r4
            goto L34
        L2c:
            int r4 = r5.mMaxCountY
            if (r3 >= r4) goto L34
            int r4 = r3 + 1
            r5.mGridCountY = r4
        L34:
            int r4 = r5.mGridCountY
            if (r4 != 0) goto L5a
            int r4 = r4 + 1
            r5.mGridCountY = r4
            goto L5a
        L3d:
            int r4 = r3 + (-1)
            int r4 = r4 * r0
            if (r4 < r6) goto L4d
            if (r3 < r0) goto L4d
            int r4 = r3 + (-1)
            int r4 = java.lang.Math.max(r1, r4)
            r5.mGridCountY = r4
            goto L5a
        L4d:
            int r4 = r0 + (-1)
            int r4 = r4 * r3
            if (r4 < r6) goto L5a
            int r4 = r0 + (-1)
            int r4 = java.lang.Math.max(r1, r4)
            r5.mGridCountX = r4
        L5a:
            int r4 = r5.mGridCountX
            if (r4 != r0) goto L12
            int r0 = r5.mGridCountY
            if (r0 != r3) goto L12
            goto L10
        L63:
            int r6 = r5.getPageCount()
            int r6 = r6 - r2
        L68:
            if (r6 < 0) goto L78
            com.android.launcher3.CellLayout r0 = r5.getPageAt(r6)
            int r1 = r5.mGridCountX
            int r2 = r5.mGridCountY
            r0.setGridSize(r1, r2)
            int r6 = r6 + (-1)
            goto L68
        L78:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.FolderPagedView.setupContentDimensions(int):void");
    }

    public ArrayList<ShortcutInfo> bindItems(ArrayList<ShortcutInfo> items) {
        ArrayList<View> arrayList = new ArrayList<>();
        ArrayList<ShortcutInfo> arrayList2 = new ArrayList<>();
        Iterator<ShortcutInfo> it = items.iterator();
        while (it.hasNext()) {
            arrayList.add(createNewView(it.next()));
        }
        arrangeChildren(arrayList, arrayList.size(), false);
        return arrayList2;
    }

    public int allocateRankForNewItem(ShortcutInfo info) {
        int itemCount = getItemCount();
        ArrayList<View> arrayList = new ArrayList<>(this.mFolder.getItemsInReadingOrder());
        arrayList.add(itemCount, null);
        arrangeChildren(arrayList, arrayList.size(), false);
        setCurrentPage(itemCount / this.mMaxItemsPerPage);
        return itemCount;
    }

    public View createAndAddViewForRank(ShortcutInfo item, int rank) {
        View viewCreateNewView = createNewView(item);
        UninstallModeManager.getInstance(getContext()).setUninstallTypeForBadgeView(viewCreateNewView);
        addViewForRank(viewCreateNewView, item, rank);
        return viewCreateNewView;
    }

    private void allocateRankForNewItems(int count) {
        int itemCount = getItemCount();
        ArrayList<View> arrayList = new ArrayList<>(this.mFolder.getItemsInReadingOrder());
        int i = 0;
        while (i < count) {
            arrayList.add(itemCount, null);
            i++;
            itemCount++;
        }
        arrangeChildren(arrayList, arrayList.size(), false);
        setCurrentPage(itemCount / this.mMaxItemsPerPage);
    }

    public void createAndAddViews(List<ShortcutInfo> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        int itemCount = getItemCount();
        allocateRankForNewItems(items.size());
        Iterator<ShortcutInfo> it = items.iterator();
        while (it.hasNext()) {
            createAndAddViewForRank(it.next(), itemCount);
            itemCount++;
        }
    }

    public void addViewForRank(View view, ShortcutInfo item, int rank) {
        if (view == null || item == null) {
            return;
        }
        int i = this.mMaxItemsPerPage;
        int i2 = rank % i;
        item.rank = rank;
        item.cellX = i2 % this.mGridCountX;
        item.cellY = i2 / this.mGridCountX;
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) view.getLayoutParams();
        layoutParams.cellX = item.cellX;
        layoutParams.cellY = item.cellY;
        getPageAt(rank / i).addViewToCellLayout(view, -1, this.mFolder.mLauncher.getViewIdForItem(item), layoutParams, true);
    }

    public View createNewView(ShortcutInfo item) {
        BubbleTextView bubbleTextView;
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            bubbleTextView = (BubbleTextView) this.mInflater.inflate(R.layout.folder_application_swivel_home, (ViewGroup) null, false);
        } else {
            bubbleTextView = (BubbleTextView) this.mInflater.inflate(R.layout.folder_application, (ViewGroup) null, false);
        }
        bubbleTextView.applyFromShortcutInfo(item, this.mIconCache);
        bubbleTextView.setOnClickListener(this.mFolder);
        bubbleTextView.setOnLongClickListener(this.mFolder);
        bubbleTextView.setOnFocusChangeListener(this.mFocusIndicatorView);
        bubbleTextView.setOnKeyListener(this.mKeyListener);
        bubbleTextView.setLayoutParams(new CellLayout.LayoutParams(item.cellX, item.cellY, item.spanX, item.spanY));
        bubbleTextView.setTextColor(getTextColor());
        return bubbleTextView;
    }

    /* JADX DEBUG: Method merged with bridge method: getPageAt(I)Landroid/view/View; */
    @Override // com.lge.launcher3.PagedView
    public CellLayout getPageAt(int index) {
        return (CellLayout) getChildAt(index);
    }

    public void removeCellLayoutView(View view) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            getPageAt(childCount).removeView(view);
        }
    }

    public CellLayout getCurrentCellLayout() {
        return getPageAt(getNextPage());
    }

    private CellLayout createAndAddNewPage() {
        DeviceProfile deviceProfile = ((Launcher) getContext()).getDeviceProfile();
        CellLayout cellLayout = new CellLayout(getContext());
        if (deviceProfile.isLandscape && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            cellLayout.setCellDimensions(deviceProfile.folderCellWidthPxForSwivel, deviceProfile.folderCellHeightPxForSwivel);
        } else {
            cellLayout.setCellDimensions(deviceProfile.folderCellWidthPx, deviceProfile.folderCellHeightPx);
        }
        cellLayout.getShortcutsAndWidgets().setMotionEventSplittingEnabled(false);
        cellLayout.setImportantForAccessibility(2);
        cellLayout.setInvertIfRtl(true);
        cellLayout.setGridSize(this.mGridCountX, this.mGridCountY);
        cellLayout.createCrossHairsGrid(this.mGridCountX, this.mGridCountY);
        addView(cellLayout, -1, generateDefaultLayoutParams());
        return cellLayout;
    }

    @Override // com.lge.launcher3.PagedView
    public int getChildGap() {
        return getPaddingLeft() + getPaddingRight();
    }

    public void setFixedSize(int width, int height) {
        int paddingLeft = width - (getPaddingLeft() + getPaddingRight());
        int paddingTop = height - (getPaddingTop() + getPaddingBottom());
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            ((CellLayout) getChildAt(childCount)).setFixedSize(paddingLeft, paddingTop);
        }
    }

    public void removeItem(View v) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            getPageAt(childCount).removeView(v);
        }
    }

    public void arrangeChildren(ArrayList<View> list, int itemCount) {
        arrangeChildren(list, itemCount, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00b5  */
    /* JADX WARN: Type inference failed for: r11v0 */
    /* JADX WARN: Type inference failed for: r11v1, types: [int] */
    /* JADX WARN: Type inference failed for: r11v2 */
    /* JADX WARN: Type inference failed for: r2v5, types: [com.android.launcher3.PageIndicator] */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void arrangeChildren(java.util.ArrayList<android.view.View> r24, int r25, boolean r26) {
        /*
            r23 = this;
            r9 = r23
            int r0 = r23.itemsPerPage()
            r1 = r25
            int r10 = java.lang.Math.max(r1, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
        L12:
            int r2 = r23.getChildCount()
            if (r1 >= r2) goto L27
            android.view.View r2 = r9.getChildAt(r1)
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            r2.removeAllViews()
            r0.add(r2)
            int r1 = r1 + 1
            goto L12
        L27:
            r9.setupContentDimensions(r10)
            java.util.Iterator r12 = r0.iterator()
            r0 = 0
            r1 = 0
            r14 = 0
            r15 = 0
        L32:
            if (r14 >= r10) goto Ldb
            int r2 = r24.size()
            r8 = r24
            if (r2 <= r14) goto L45
            java.lang.Object r2 = r8.get(r14)
            android.view.View r2 = (android.view.View) r2
            r16 = r2
            goto L47
        L45:
            r16 = 0
        L47:
            if (r0 == 0) goto L53
            int r2 = r9.mMaxItemsPerPage
            if (r1 < r2) goto L4e
            goto L53
        L4e:
            r17 = r0
            r18 = r1
            goto L68
        L53:
            boolean r0 = r12.hasNext()
            if (r0 == 0) goto L60
            java.lang.Object r0 = r12.next()
            com.android.launcher3.CellLayout r0 = (com.android.launcher3.CellLayout) r0
            goto L64
        L60:
            com.android.launcher3.CellLayout r0 = r23.createAndAddNewPage()
        L64:
            r17 = r0
            r18 = 0
        L68:
            if (r16 == 0) goto Ld1
            android.view.ViewGroup$LayoutParams r0 = r16.getLayoutParams()
            r7 = r0
            com.android.launcher3.CellLayout$LayoutParams r7 = (com.android.launcher3.CellLayout.LayoutParams) r7
            int r0 = r9.mGridCountX
            int r1 = r18 % r0
            int r0 = r18 / r0
            java.lang.Object r2 = r16.getTag()
            r5 = r2
            com.android.launcher3.model.data.ItemInfo r5 = (com.android.launcher3.model.data.ItemInfo) r5
            int r2 = r5.cellX
            if (r2 != r1) goto L8a
            int r2 = r5.cellY
            if (r2 != r0) goto L8a
            int r2 = r5.rank
            if (r2 == r15) goto Lb5
        L8a:
            r5.cellX = r1
            r5.cellY = r0
            r5.rank = r15
            if (r26 == 0) goto Lb5
            android.content.Context r1 = r23.getContext()
            com.android.launcher3.folder.Folder r0 = r9.mFolder
            com.android.launcher3.model.data.FolderInfo r0 = r0.mInfo
            long r3 = r0.id
            r19 = 0
            int r6 = r5.cellX
            int r2 = r5.cellY
            r0 = r23
            r21 = r2
            r2 = r5
            r13 = r5
            r22 = r6
            r5 = r19
            r11 = r7
            r7 = r22
            r8 = r21
            r0.addOrMoveItemInDatabase(r1, r2, r3, r5, r7, r8)
            goto Lb7
        Lb5:
            r13 = r5
            r11 = r7
        Lb7:
            int r0 = r13.cellX
            r11.cellX = r0
            int r0 = r13.cellY
            r11.cellY = r0
            r5 = -1
            com.android.launcher3.folder.Folder r0 = r9.mFolder
            com.android.launcher3.Launcher r0 = r0.mLauncher
            int r6 = r0.getViewIdForItem(r13)
            r8 = 1
            r3 = r17
            r4 = r16
            r7 = r11
            r3.addViewToCellLayout(r4, r5, r6, r7, r8)
        Ld1:
            int r15 = r15 + 1
            int r1 = r18 + 1
            int r14 = r14 + 1
            r0 = r17
            goto L32
        Ldb:
            r0 = 1
            r1 = 0
        Ldd:
            boolean r2 = r12.hasNext()
            if (r2 == 0) goto Lee
            java.lang.Object r1 = r12.next()
            android.view.View r1 = (android.view.View) r1
            r9.removeView(r1)
            r1 = r0
            goto Ldd
        Lee:
            if (r1 == 0) goto Lf5
            r1 = 0
            r9.setCurrentPage(r1)
            goto Lf6
        Lf5:
            r1 = 0
        Lf6:
            int r2 = r23.getPageCount()
            if (r2 <= r0) goto Lfe
            r2 = r0
            goto Lff
        Lfe:
            r2 = r1
        Lff:
            r9.setEnableOverscroll(r2)
            com.android.launcher3.PageIndicator r2 = r9.mPageIndicator
            int r3 = r23.getPageCount()
            if (r3 <= r0) goto L10c
            r11 = r1
            goto L10e
        L10c:
            r11 = 8
        L10e:
            r2.setVisibility(r11)
            com.android.launcher3.folder.Folder r1 = r9.mFolder
            com.android.launcher3.folder.FolderEditText r1 = r1.mFolderName
            r1.setGravity(r0)
            r23.setFolderNameGravity()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.FolderPagedView.arrangeChildren(java.util.ArrayList, int, boolean):void");
    }

    public int getDesiredWidth() {
        if (getPageCount() <= 0) {
            return 0;
        }
        return getPaddingRight() + getPageAt(0).getDesiredWidth() + getPaddingLeft();
    }

    public int getDesiredHeight() {
        if (getPageCount() <= 0) {
            return 0;
        }
        return getPaddingBottom() + getPageAt(0).getDesiredHeight() + getPaddingTop();
    }

    public int getItemCount() {
        int childCount = getChildCount() - 1;
        if (childCount < 0) {
            return 0;
        }
        return getPageAt(childCount).getShortcutsAndWidgets().getChildCount() + (childCount * this.mMaxItemsPerPage);
    }

    public int findNearestArea(int pixelX, int pixelY) {
        int nextPage = getNextPage();
        CellLayout pageAt = getPageAt(nextPage);
        int[] iArr = sTempPosArray;
        pageAt.findNearestArea(pixelX, pixelY, 1, 1, iArr);
        if (this.mFolder.isLayoutRtl()) {
            iArr[0] = (pageAt.getCountX() - iArr[0]) - 1;
        }
        return Math.min(this.mAllocatedContentSize - 1, (nextPage * this.mMaxItemsPerPage) + (iArr[1] * this.mGridCountX) + iArr[0]);
    }

    @Override // com.lge.launcher3.PagedView
    protected PageIndicator.PageMarkerResources getPageIndicatorMarker(int pageIndex) {
        if (DDTUtils.isAdditionalThemeApplied(getContext())) {
            int folderTextColor = FolderColorUtil.getFolderTextColor(getContext(), 0);
            return new PageIndicator.PageMarkerResources(R.drawable.ic_homescreen_pageindicator_select, R.drawable.ic_homescreen_pageindicator_normal, folderTextColor, (-1711276033) & folderTextColor);
        }
        return new PageIndicator.PageMarkerResources(R.drawable.ic_homescreen_pageindicator_select, R.drawable.ic_homescreen_pageindicator_normal);
    }

    public View getLastItem() {
        if (getChildCount() < 1) {
            return null;
        }
        ShortcutAndWidgetContainer shortcutsAndWidgets = getCurrentCellLayout().getShortcutsAndWidgets();
        int childCount = shortcutsAndWidgets.getChildCount() - 1;
        int i = this.mGridCountX;
        if (i > 0) {
            return shortcutsAndWidgets.getChildAt(childCount % i, childCount / i);
        }
        return shortcutsAndWidgets.getChildAt(childCount);
    }

    public View iterateOverItems(Workspace.ItemOperator op) {
        for (int i = 0; i < getChildCount(); i++) {
            CellLayout pageAt = getPageAt(i);
            for (int i2 = 0; i2 < pageAt.getCountY(); i2++) {
                for (int i3 = 0; i3 < pageAt.getCountX(); i3++) {
                    View childAt = pageAt.getChildAt(i3, i2);
                    if (childAt != null && op.evaluate((ItemInfo) childAt.getTag(), childAt, this)) {
                        return childAt;
                    }
                }
            }
        }
        return null;
    }

    public String getAccessibilityDescription() {
        StringBuilder sb = new StringBuilder();
        if (this.mFolder.mFolderName.getText().length() > 0) {
            sb.append((CharSequence) this.mFolder.mFolderName.getText());
        } else {
            sb.append(getContext().getString(R.string.folder_hint_text));
        }
        sb.append(String.format(getContext().getString(R.string.showing_talkback), Integer.valueOf(getItemCount())));
        sb.append(String.format(getContext().getString(R.string.talkback_gird_locate_lines_rows), Integer.valueOf(this.mGridCountX), Integer.valueOf(this.mGridCountY)));
        return sb.toString();
    }

    public void setFocusOnFirstChild() {
        View childAt = getCurrentCellLayout().getChildAt(0, 0);
        if (childAt != null) {
            childAt.requestFocus();
        }
    }

    @Override // com.lge.launcher3.PagedView
    protected void notifyPageSwitchListener(int prevPage) {
        super.notifyPageSwitchListener(prevPage);
        Folder folder = this.mFolder;
        if (folder != null) {
            folder.updateTextViewFocus();
        }
    }

    public void showScrollHint(int direction) {
        int scrollForPage = (getScrollForPage(getNextPage()) + ((int) (((direction == 0) ^ this.mIsRtl ? -0.07f : 0.07f) * getWidth()))) - getScrollX();
        if (scrollForPage != 0) {
            this.mScroller.setInterpolator(new DecelerateInterpolator());
            this.mScroller.startScroll(getScrollX(), 0, scrollForPage, 0, 500);
            invalidate();
        }
    }

    public void clearScrollHint() {
        if (getScrollX() != getScrollForPage(getNextPage())) {
            snapToPage(getNextPage());
        }
    }

    public void completePendingPageChanges() {
        if (this.mPendingAnimations.isEmpty()) {
            return;
        }
        for (Map.Entry entry : new HashMap(this.mPendingAnimations).entrySet()) {
            ((View) entry.getKey()).animate().cancel();
            ((Runnable) entry.getValue()).run();
        }
    }

    public boolean rankOnCurrentPage(int rank) {
        return rank / this.mMaxItemsPerPage == getNextPage();
    }

    @Override // com.lge.launcher3.PagedView
    protected void onPageBeginTransition() {
        super.onPageBeginTransition();
        verifyVisibleHighResIcons(getCurrentPage() - 1);
        verifyVisibleHighResIcons(getCurrentPage() + 1);
    }

    public void verifyVisibleHighResIcons(int pageNo) {
        CellLayout pageAt = getPageAt(pageNo);
        if (pageAt != null) {
            ShortcutAndWidgetContainer shortcutsAndWidgets = pageAt.getShortcutsAndWidgets();
            for (int childCount = shortcutsAndWidgets.getChildCount() - 1; childCount >= 0; childCount--) {
                BubbleTextView bubbleTextView = (BubbleTextView) shortcutsAndWidgets.getChildAt(childCount);
                bubbleTextView.verifyHighRes();
                Drawable drawable = bubbleTextView.getCompoundDrawables()[1];
                if (drawable != null) {
                    drawable.setCallback(bubbleTextView);
                }
            }
        }
    }

    public int getAllocatedContentSize() {
        return this.mAllocatedContentSize;
    }

    public void realTimeReorder(int empty, int target) {
        int i;
        int i2;
        final int i3 = empty;
        completePendingPageChanges();
        int nextPage = getNextPage();
        int i4 = this.mMaxItemsPerPage;
        int i5 = target / i4;
        int i6 = target % i4;
        if (i5 != nextPage) {
            Log.e(TAG, "Cannot animate when the target cell is invisible");
        }
        int i7 = this.mMaxItemsPerPage;
        int i8 = i3 % i7;
        int i9 = i3 / i7;
        if (target == i3) {
            return;
        }
        int i10 = -1;
        boolean z = false;
        if (target > i3) {
            if (i9 < nextPage) {
                i10 = nextPage * i7;
                i8 = 0;
            } else {
                i3 = -1;
            }
            i2 = 1;
        } else {
            if (i9 > nextPage) {
                i = ((nextPage + 1) * i7) - 1;
                i8 = i7 - 1;
            } else {
                i3 = -1;
                i = -1;
            }
            i10 = i;
            i2 = -1;
        }
        while (i3 != i10) {
            int i11 = i3 + i2;
            int i12 = this.mMaxItemsPerPage;
            int i13 = i11 / i12;
            int i14 = i11 % i12;
            int i15 = this.mGridCountX;
            int i16 = i14 % i15;
            int i17 = i14 / i15;
            CellLayout pageAt = getPageAt(i13);
            final View childAt = pageAt.getChildAt(i16, i17);
            if (childAt != null) {
                if (nextPage != i13) {
                    pageAt.removeView(childAt);
                    addViewForRank(childAt, (ShortcutInfo) childAt.getTag(), i3);
                } else {
                    final float translationX = childAt.getTranslationX();
                    Runnable runnable = new Runnable() { // from class: com.android.launcher3.folder.FolderPagedView.1
                        @Override // java.lang.Runnable
                        public void run() {
                            FolderPagedView.this.mPendingAnimations.remove(childAt);
                            childAt.setTranslationX(translationX);
                            ((CellLayout) childAt.getParent().getParent()).removeView(childAt);
                            FolderPagedView folderPagedView = FolderPagedView.this;
                            View view = childAt;
                            folderPagedView.addViewForRank(view, (ShortcutInfo) view.getTag(), i3);
                        }
                    };
                    childAt.animate().translationXBy((i2 > 0) ^ this.mIsRtl ? -childAt.getWidth() : childAt.getWidth()).setDuration(230L).setStartDelay(0L).withEndAction(runnable);
                    this.mPendingAnimations.put(childAt, runnable);
                }
            }
            i3 = i11;
        }
        if ((i6 - i8) * i2 <= 0) {
            return;
        }
        CellLayout pageAt2 = getPageAt(nextPage);
        float f = 30.0f;
        int i18 = 0;
        while (i8 != i6) {
            int i19 = i8 + i2;
            int i20 = this.mGridCountX;
            View childAt2 = pageAt2.getChildAt(i19 % i20, i19 / i20);
            if (childAt2 != null) {
                ((ItemInfo) childAt2.getTag()).rank -= i2;
            }
            int i21 = this.mGridCountX;
            if (pageAt2.animateChildToPosition(childAt2, i8 % i21, i8 / i21, REORDER_ANIMATION_DURATION, i18, true, true)) {
                i18 = (int) (i18 + f);
                f *= 0.9f;
                z = true;
            }
            i8 = i19;
        }
        if (z) {
            VibratorManager.performHapticFeedback(this.mFolder.mLauncher, 65541);
        }
    }

    public void setMarkerScale(float scale) {
        int childCount = this.mPageIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mPageIndicator.getChildAt(i);
            childAt.animate().cancel();
            childAt.setScaleX(scale);
            childAt.setScaleY(scale);
        }
    }

    public void animateMarkers() {
        int childCount = this.mPageIndicator.getChildCount();
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(PAGE_INDICATOR_OVERSHOOT_TENSION);
        for (int i = 0; i < childCount; i++) {
            this.mPageIndicator.getChildAt(i).animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(overshootInterpolator).setDuration(400L).setStartDelay((i * 150) + 300);
        }
    }

    public int itemsPerPage() {
        return this.mMaxItemsPerPage;
    }

    @Override // com.lge.launcher3.PagedView
    protected void getEdgeVerticalPostion(int[] pos) {
        pos[0] = 0;
        pos[1] = getViewportHeight();
    }

    @Override // com.lge.launcher3.PagedView
    public void afterAttachedToWindow() {
        PageIndicatorExtension pageIndicatorExtension = (PageIndicatorExtension) getPageIndicator();
        if (pageIndicatorExtension != null) {
            setPageIndicator(pageIndicatorExtension);
            int dimension = (int) getResources().getDimension(R.dimen.device_profile_pageIndicator_folder_padding);
            setPageIndicator((PageIndicatorExtension) this.mPageIndicator);
            pageIndicatorExtension.setTypePadding(dimension);
        }
    }

    @Override // com.lge.launcher3.PagedView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        afterAttachedToWindow();
    }

    public int getTextColor() {
        if (DDTUtils.isAdditionalThemeApplied(getContext()) || DDTUtils.isAdditionalIconThemeApplied(getContext())) {
            return FolderColorUtil.getFolderTextColor(getContext(), 0);
        }
        return FolderColorUtil.getFolderTextColor(getContext(), 0);
    }

    public void setFolderNameGravity() {
        this.mFolder.mFolderName.setGravity((this.mIsRtl ? 5 : 3) | 16);
    }

    public void showAllCrossHair(boolean show) {
        if (!LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_EDITMODE_UI.getValue() || this.mIsVisibleAllCrossHair == show) {
            return;
        }
        this.mIsVisibleAllCrossHair = show;
        for (int i = 0; i < getChildCount(); i++) {
            ((CellLayout) getChildAt(i)).setCrosshairAnimation(show);
        }
    }

    public void setDataModel(Folder.DataModel dataModel) {
        this.mDataModel = dataModel;
    }

    private void addOrMoveItemInDatabase(Context context, ItemInfo item, long container, long screenId, int cellX, int cellY) {
        if (this.mDataModel != null) {
            LGLog.d(TAG, "call DataModel addOrMoveItemInDatabase");
            this.mDataModel.addOrMoveItemInDatabase(context, item, container, screenId, cellX, cellY);
        } else {
            LGLog.d(TAG, "call LauncherModel addOrMoveItemInDatabase");
            LauncherModel.addOrMoveItemInDatabase(context, item, container, screenId, cellX, cellY);
        }
    }

    @Override // com.lge.launcher3.PagedView
    protected void getVisiblePages(int[] range) {
        if (range != null) {
            if (range.equals(this.mDrawVisiblePagesRange)) {
                return;
            }
        } else {
            this.mDrawVisiblePagesRange = new int[2];
            range = this.mDrawVisiblePagesRange;
        }
        int childCount = getChildCount();
        int[] iArr = sTmpIntPoint;
        sTmpIntPoint[1] = 0;
        iArr[0] = 0;
        range[0] = -1;
        range[1] = -1;
        if (childCount > 0) {
            int viewportWidth = getViewportWidth();
            int i = 0;
            for (int i2 = 0; i2 < childCount; i2++) {
                CellLayout pageAt = getPageAt(i2);
                sTmpIntPoint[0] = 0;
                Utilities.getDescendantCoordRelativeToParent(pageAt, this, sTmpIntPoint, false);
                if (sTmpIntPoint[0] > viewportWidth) {
                    if (range[0] != -1) {
                        break;
                    }
                } else {
                    sTmpIntPoint[0] = pageAt.getMeasuredWidth();
                    Utilities.getDescendantCoordRelativeToParent(pageAt, this, sTmpIntPoint, false);
                    if (sTmpIntPoint[0] < 0) {
                        if (range[0] != -1) {
                            break;
                        }
                    } else {
                        if (range[0] < 0) {
                            range[0] = i2;
                        }
                        i = i2;
                    }
                }
            }
            range[1] = i;
            return;
        }
        range[0] = -1;
        range[1] = -1;
    }

    public void unregisterAppNotifier() {
        int i = 0;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            CellLayout cellLayout = (CellLayout) getChildAt(i2);
            if (cellLayout != null && cellLayout.getShortcutsAndWidgets() != null) {
                for (int i3 = 0; i3 < cellLayout.getShortcutsAndWidgets().getChildCount(); i3++) {
                    KeyEvent.Callback childAt = cellLayout.getShortcutsAndWidgets().getChildAt(i3);
                    if (childAt instanceof IAppNotifierView) {
                        AppNotifierManager.getInstance(getContext()).unregisterAppNotifier((IAppNotifierView) childAt);
                        i++;
                    }
                }
            }
        }
        LGLog.d(TAG, "unregisterAppNotifier : " + i);
    }
}
