package com.lge.launcher3.profile;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.SearchDropTargetBar;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.WindowBounds;
import com.android.quickstep.SysUINavigationMode;
import com.lge.launcher3.CustomUIManager;
import com.lge.launcher3.PagedView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class LGDeviceProfile extends DeviceProfile {
    private static final float MULTI_WINDOW_FOLDER_CELL_HEIGHT_SCALE = 0.763f;
    static final String TAG = "LGDeviceProfile";
    private float appWidgetScale;
    protected int desiredWorkspaceTopMarginPx;
    public int iconTextMaxLines;
    private int realCellHeight;
    private int realCellWidth;

    public LGDeviceProfile(Context context, InvariantDeviceProfile inv, Point minSize, Point maxSize, int width, int height, boolean isLandscape, boolean isMultiWindowMode, int displayId, boolean isMultiDisplay) {
        int dimensionPixelSize;
        super(context, inv, minSize, maxSize, width, height, isLandscape, isMultiWindowMode);
        this.appWidgetScale = 0.0f;
        this.realCellWidth = 0;
        this.realCellHeight = 0;
        Resources resources = context.getResources();
        if (isLandscape) {
            if (this.isPhone) {
                dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.device_profile_pageIndicator_height_land);
            } else {
                dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.device_profile_pageIndicator_height);
            }
            this.pageIndicatorHeightPx = dimensionPixelSize;
            this.desiredWorkspaceTopMarginPx = resources.getDimensionPixelSize(R.dimen.device_profile_workspace_top_margin_land);
            this.iconDrawablePaddingOriginalPx = resources.getDimensionPixelSize(R.dimen.device_profile_app_icon_drawable_padding_land);
            this.iconSizeLandPx = getLandscapeIconSizeByDynamicGrid(context, resources, this.numRows, this.numColumns, Utilities.getDisplaySize());
            this.hotseatIconSizePx = this.iconSizeLandPx;
            this.statusbarSizePx = WindowUtils.getStatusBarHeight(context);
            updateProfileByNaviMode(context, resources);
        } else {
            this.hotseatBarHeightPx = resources.getDimensionPixelSize(R.dimen.device_profile_hotseat_height);
            this.pageIndicatorHeightPx = resources.getDimensionPixelSize(R.dimen.device_profile_pageIndicator_height);
            this.desiredWorkspaceLeftRightMarginPx = resources.getDimensionPixelSize(R.dimen.device_profile_workspace_left_right_margin);
            this.desiredWorkspaceTopMarginPx = resources.getDimensionPixelSize(R.dimen.device_profile_workspace_top_margin);
            this.iconDrawablePaddingOriginalPx = resources.getDimensionPixelSize(R.dimen.device_profile_app_icon_drawable_padding);
            if (CustomUIManager.getInstance(context).getWorkspacePageSpacing() != 0) {
                this.desiredWorkspaceLeftRightMarginPx = CustomUIManager.getInstance(context).getWorkspacePageSpacing();
            }
        }
        this.iconTextMaxLines = resources.getInteger(R.integer.device_profile_iconTextMaxLines);
        this.mDisplayId = displayId;
        this.mIsMultiDisplay = isMultiDisplay;
        updateAvailableDimensions(context, resources.getDisplayMetrics(), resources);
        computeAllAppsButtonSize(context);
        LGLog.d(TAG, "[DEVICE_PROFILE] LGDeviceProfile : land = " + isLandscape + ", size(" + width + ", " + height + "), available (" + this.availableWidthPx + ", " + this.availableHeightPx + ")" + this);
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0046  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0068 A[PHI: r5
      0x0068: PHI (r5v4 float) = (r5v1 float), (r5v2 float), (r5v3 float) binds: [B:21:0x0066, B:32:0x0086, B:31:0x007a] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x006a  */
    @Override // com.android.launcher3.DeviceProfile
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void updateAvailableDimensions(android.content.Context r13, android.util.DisplayMetrics r14, android.content.res.Resources r15) {
        /*
            r12 = this;
            int r6 = r12.iconDrawablePaddingOriginalPx
            r2 = 1065353216(0x3f800000, float:1.0)
            r0 = r12
            r1 = r13
            r3 = r6
            r4 = r15
            r5 = r14
            r0.updateIconSize(r1, r2, r3, r4, r5)
            int r0 = r12.cellHeightPx
            com.android.launcher3.InvariantDeviceProfile r1 = r12.inv
            int r1 = r1.numRows
            int r0 = r0 * r1
            float r0 = (float) r0
            r1 = 0
            android.graphics.Rect r2 = r12.getWorkspacePadding(r1)
            int r3 = r12.availableHeightPx
            int r4 = r2.top
            int r3 = r3 - r4
            int r2 = r2.bottom
            int r3 = r3 - r2
            float r2 = (float) r3
            r3 = 0
            int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            r5 = 1065353216(0x3f800000, float:1.0)
            if (r4 == 0) goto L3c
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 == 0) goto L3c
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L3c
            float r2 = r2 / r0
            boolean r0 = r12.isLandscape
            if (r0 == 0) goto L3a
            boolean r0 = r12.isPhone
            if (r0 != 0) goto L3d
        L3a:
            r9 = r1
            goto L3e
        L3c:
            r2 = r5
        L3d:
            r9 = r6
        L3e:
            com.lge.launcher3.util.LGHomeFeature$Config r0 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_DEFAULT_560DPI
            boolean r0 = r0.getValue()
            if (r0 == 0) goto L64
            r0 = 2131361896(0x7f0a0068, float:1.8343557E38)
            int r0 = r15.getInteger(r0)
            r1 = 2131361897(0x7f0a0069, float:1.834356E38)
            int r1 = r15.getInteger(r1)
            r3 = 2131165566(0x7f07017e, float:1.7945353E38)
            float r3 = r15.getFloat(r3)
            int r4 = r14.densityDpi
            if (r4 != r0) goto L64
            r14.density = r3
            r14.densityDpi = r1
            r2 = r5
        L64:
            boolean r0 = r12.isTablet
            if (r0 == 0) goto L6a
        L68:
            r8 = r5
            goto La1
        L6a:
            boolean r0 = r12.isLandscape
            if (r0 == 0) goto La0
            boolean r0 = r12.allowRotation
            if (r0 == 0) goto La0
            boolean r0 = r12.cellLayoutHorizontal
            if (r0 == 0) goto La0
            int r0 = r12.iconSizeLandPx
            if (r0 <= 0) goto L86
            int r0 = r12.iconSizeLandPx
            float r0 = (float) r0
            int r1 = r12.iconSizePx
            float r1 = (float) r1
            float r0 = r0 / r1
            float r5 = java.lang.Math.min(r5, r0)
            goto L68
        L86:
            android.graphics.Point r0 = r12.getCellSize()
            int r0 = r0.y
            r1 = 2131165558(0x7f070176, float:1.7945337E38)
            int r1 = r15.getDimensionPixelSize(r1)
            int r1 = r1 * 2
            int r0 = r0 - r1
            float r0 = (float) r0
            int r1 = r12.iconSizePx
            float r1 = (float) r1
            float r0 = r0 / r1
            float r5 = java.lang.Math.min(r5, r0)
            goto L68
        La0:
            r8 = r2
        La1:
            r6 = r12
            r7 = r13
            r10 = r15
            r11 = r14
            r6.updateIconSize(r7, r8, r9, r10, r11)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.profile.LGDeviceProfile.updateAvailableDimensions(android.content.Context, android.util.DisplayMetrics, android.content.res.Resources):void");
    }

    @Override // com.android.launcher3.DeviceProfile
    public DeviceProfile getMultiWindowProfile(Context context, WindowBounds windowBounds) {
        Point point = new Point(Math.min(this.availableWidthPx, windowBounds.availableSize.x), Math.min(this.availableHeightPx, windowBounds.availableSize.y));
        LGDeviceProfile lGDeviceProfile = new LGDeviceProfile(context, this.inv, point, point, windowBounds.bounds.width(), windowBounds.bounds.height(), this.isLandscape, true, this.mDisplayId, this.mIsMultiDisplay);
        lGDeviceProfile.setWindowPosition(windowBounds);
        if (!this.isLandscape || !this.cellLayoutHorizontal) {
            lGDeviceProfile.cellHeightPx = lGDeviceProfile.iconSizePx + lGDeviceProfile.iconDrawablePaddingPx + Utilities.calculateTextHeight(lGDeviceProfile.iconTextSizePx);
        }
        lGDeviceProfile.appWidgetScale = Math.min(lGDeviceProfile.getCellSize().x / getCellSize().x, lGDeviceProfile.getCellSize().y / getCellSize().y);
        lGDeviceProfile.desiredWorkspaceTopMarginPx = context.getResources().getDimensionPixelSize(R.dimen.device_profile_workspace_top_margin_multiWindow);
        lGDeviceProfile.iconTextMaxLines = context.getResources().getInteger(R.integer.device_profile_iconTextMaxLines_multiWindow);
        return lGDeviceProfile;
    }

    @Override // com.android.launcher3.DeviceProfile
    protected void updateIconSize(Context context, float scale, int drawablePadding, Resources res, DisplayMetrics dm) {
        super.updateIconSize(context, scale, drawablePadding, res, dm);
        updateRealCellWidthHeight();
        if (this.isTablet) {
            this.searchBarSpaceWidthPx = ((int) (this.availableWidthPx * this.workspaceSpringLoadShrinkFactor)) - this.desiredWorkspaceLeftRightMarginPx;
            int i = this.realCellWidth;
            this.searchBarSpaceHeightPx = ((int) (i - (i * this.workspaceSpringLoadShrinkFactor))) / 2;
        } else if (this.isLandscape && this.allowRotation) {
            this.searchBarSpaceWidthPx = ((int) (((double) this.availableWidthPx) * (((double) this.workspaceSpringLoadShrinkFactor) - 0.05d))) + this.edgeMarginPx;
            this.searchBarSpaceHeightPx = res.getDimensionPixelSize(R.dimen.device_profile_search_dropbar_height);
        } else {
            this.searchBarSpaceWidthPx = ((int) (((double) this.availableWidthPx) * (((double) this.workspaceSpringLoadShrinkFactor) - 0.05d))) + this.edgeMarginPx;
            int i2 = this.realCellWidth;
            this.searchBarSpaceHeightPx = ((int) (i2 - (i2 * this.workspaceSpringLoadShrinkFactor))) / 2;
        }
        if (this.isLandscape && this.cellLayoutHorizontal) {
            this.folderCellWidthPx = Math.min((res.getDimensionPixelSize(R.dimen.folder_content_width) - (res.getDimensionPixelSize(R.dimen.folder_content_padding_side) * 2)) / this.inv.numFolderColumns, ((((this.availableWidthPx - (this.navibarSizePx * 2)) - res.getDimensionPixelSize(R.dimen.folder_plus_button_width)) - res.getDimensionPixelSize(R.dimen.folder_between_apps_plus_button)) - (res.getDimensionPixelSize(R.dimen.folder_content_padding_side) * 2)) / this.inv.numFolderColumns);
            this.folderCellHeightPx = Math.min(res.getDimensionPixelSize(R.dimen.folder_content_height) / this.inv.numFolderRows, ((((this.availableHeightPx - this.navibarSizePx) - this.statusbarSizePx) - res.getDimensionPixelSize(R.dimen.folder_title_height)) - res.getDimensionPixelSize(R.dimen.folder_footer_height)) / this.inv.numFolderRows);
            return;
        }
        Paint paint = new Paint();
        if (LGHomeFeature.Config.FEATURE_USE_DEFAULT_560DPI.getValue() || LGHomeFeature.Config.FEATURE_USE_DEFAULT_LOW_DPI.getValue()) {
            paint.setTextSize(this.iconTextSizePx);
        } else {
            paint.setTextSize(Utilities.pxFromDp(res.getFloat(R.dimen.device_profile_iconTextSize), dm));
        }
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        this.cellHeightPx = this.iconSizePx + (((int) Math.ceil(fontMetrics.bottom - fontMetrics.top)) * 2) + res.getDimensionPixelSize(R.dimen.device_profile_app_icon_drawable_padding);
        this.folderCellHeightPx = this.cellHeightPx + (res.getDimensionPixelOffset(R.dimen.dynamic_grid_edge_margin) * 2);
        this.folderCellWidthPx = (int) (this.folderCellHeightPx * 0.95f);
        int displaySize = Utilities.getDisplaySize();
        if (LGHomeFeature.isDisableEasyHome()) {
            try {
                TypedArray typedArrayObtainTypedArray = res.obtainTypedArray(R.array.folder_cell_width_scale_by_display_size);
                this.folderCellWidthPx = (int) (this.folderCellHeightPx * typedArrayObtainTypedArray.getFloat(displaySize, 0.8f));
                typedArrayObtainTypedArray.recycle();
                if (displaySize == 2) {
                    this.folderCellHeightPx = (int) (this.folderCellHeightPx * res.getFloat(R.dimen.config_folder_cell_height_ratio));
                }
            } catch (ArrayIndexOutOfBoundsException unused) {
                LGLog.e(TAG, "ArrayIndexOutOfBoundsException");
            }
            if (this.isMultiWindowMode) {
                this.folderCellHeightPx = (int) (this.folderCellHeightPx * MULTI_WINDOW_FOLDER_CELL_HEIGHT_SCALE);
            }
        }
    }

    @Override // com.android.launcher3.DeviceProfile
    public Rect getWorkspacePadding(boolean isLayoutRtl) {
        Rect workspacePadding = super.getWorkspacePadding(isLayoutRtl);
        if (this.isLandscape) {
            workspacePadding.left = this.desiredWorkspaceLeftRightMarginPx;
            workspacePadding.top = this.desiredWorkspaceTopMarginPx;
            workspacePadding.right = this.desiredWorkspaceLeftRightMarginPx;
            workspacePadding.bottom = this.pageIndicatorHeightPx;
            if (this.allowRotation) {
                if (!isVerticalBarLayout()) {
                    if (this.isPhone && this.currNaviBarMode != SysUINavigationMode.Mode.NO_BUTTON) {
                        if (isSeascape()) {
                            workspacePadding.left += this.navibarSizePx;
                        } else {
                            workspacePadding.right += this.navibarSizePx;
                        }
                    }
                    workspacePadding.bottom += this.hotseatBarHeightPx;
                } else if (isLayoutRtl) {
                    workspacePadding.left += this.hotseatBarHeightPx;
                } else {
                    workspacePadding.right += this.hotseatBarHeightPx;
                }
            } else {
                workspacePadding.bottom += this.hotseatBarHeightPx;
            }
        } else {
            workspacePadding.top = this.desiredWorkspaceTopMarginPx;
            workspacePadding.left = this.desiredWorkspaceLeftRightMarginPx;
            workspacePadding.right = this.desiredWorkspaceLeftRightMarginPx;
            workspacePadding.bottom = this.pageIndicatorHeightPx + this.hotseatBarHeightPx;
        }
        return workspacePadding;
    }

    @Override // com.android.launcher3.DeviceProfile
    public float getOverviewModeScale(boolean isLayoutRtl) {
        return this.overviewModeScaleFactor;
    }

    private void updateRealCellWidthHeight() {
        if (this.realCellWidth == 0 || this.realCellHeight == 0) {
            Rect workspacePadding = getWorkspacePadding(false);
            this.realCellWidth = (this.availableHeightPx - workspacePadding.top) - workspacePadding.bottom;
            this.realCellHeight = (this.availableWidthPx - workspacePadding.left) - workspacePadding.right;
        }
    }

    public void calculateAppWidgetScale(Context context) {
        updateRealCellWidthHeight();
        Resources resources = context.getResources();
        float fMin = Math.min(Math.min(this.realCellWidth / this.inv.numColumns, this.realCellHeight / this.inv.numRows) / Math.min(resources.getDimensionPixelSize(R.dimen.lg_workspace_cell_width_for_widget), resources.getDimensionPixelSize(R.dimen.lg_workspace_cell_height_for_widget)), Math.max(this.realCellWidth / this.inv.numColumns, this.realCellHeight / this.inv.numRows) / Math.max(r0, r6));
        if (fMin < 1.0f) {
            this.appWidgetScale = fMin;
        } else if (this.appWidgetScale < 1.0f) {
            this.appWidgetScale = 1.0f;
        }
    }

    public float getAppWidgetScale(Context context) {
        if (this.appWidgetScale == 0.0f) {
            calculateAppWidgetScale(context);
        }
        return this.appWidgetScale;
    }

    @Override // com.android.launcher3.DeviceProfile
    public void updateInsets(Rect insets) {
        super.updateInsets(insets);
    }

    @Override // com.android.launcher3.DeviceProfile
    public void layout(Launcher launcher) {
        updateProfileByNaviMode(launcher, launcher.getResources());
        Resources resources = launcher.getResources();
        boolean zIsVerticalBarLayout = isVerticalBarLayout();
        boolean zIsRtl = Utilities.isRtl(launcher.getResources());
        Rect workspacePadding = getWorkspacePadding(zIsRtl);
        SearchDropTargetBar searchBar = launcher.getSearchBar();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) searchBar.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = this.searchBarSpaceHeightPx;
        if (launcher.getDragLayer().getHeight() == 0) {
            if (this.isLandscape && this.allowRotation) {
                layoutParams.topMargin = 0;
            } else {
                layoutParams.topMargin = launcher.getResources().getDimensionPixelSize(R.dimen.device_profile_droptarget_top_margin);
            }
        } else {
            layoutParams.topMargin = this.mInsets.top != 0 ? this.mInsets.top : WindowUtils.getStatusBarHeight(launcher);
            if (this.isLandscape && this.allowRotation) {
                layoutParams.topMargin += 0;
            } else {
                layoutParams.topMargin += launcher.getResources().getDimensionPixelSize(R.dimen.device_profile_droptarget_top_margin);
            }
        }
        searchBar.setLayoutParams(layoutParams);
        PagedView pagedView = (PagedView) launcher.findViewById(R.id.workspace);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) pagedView.getLayoutParams();
        layoutParams2.gravity = 17;
        pagedView.setLayoutParams(layoutParams2);
        pagedView.setPadding(workspacePadding.left, workspacePadding.top, workspacePadding.right, workspacePadding.bottom);
        pagedView.setPageSpacing(getWorkspacePageSpacing(zIsRtl));
        View viewFindViewById = launcher.findViewById(R.id.hotseat);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) viewFindViewById.getLayoutParams();
        if (zIsVerticalBarLayout) {
            layoutParams3.gravity = 5;
            layoutParams3.width = this.hotseatBarHeightPx;
            layoutParams3.height = -1;
            viewFindViewById.findViewById(R.id.layout).setPadding(0, this.edgeMarginPx * 2, 0, this.edgeMarginPx * 2);
        } else {
            layoutParams3.gravity = 80;
            layoutParams3.width = -1;
            layoutParams3.height = this.hotseatBarHeightPx;
        }
        int i = layoutParams3.bottomMargin;
        viewFindViewById.setLayoutParams(layoutParams3);
        View viewFindViewById2 = launcher.findViewById(R.id.page_indicator);
        if (viewFindViewById2 != null) {
            if (viewFindViewById2 instanceof LinearLayout) {
                ((LinearLayout) viewFindViewById2).setGravity(17);
            }
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) viewFindViewById2.getLayoutParams();
            layoutParams4.gravity = 80;
            layoutParams4.width = -1;
            layoutParams4.height = this.pageIndicatorHeightPx;
            if (zIsVerticalBarLayout) {
                layoutParams4.bottomMargin = 0;
            } else {
                layoutParams4.bottomMargin = this.hotseatBarHeightPx + i;
            }
            viewFindViewById2.setLayoutParams(layoutParams4);
            if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                viewFindViewById2.setVisibility(0);
            }
        }
        launcher.mOrientationOfCurrentLayout = this.isLandscape ? 1 : 0;
        View viewFindViewById3 = launcher.findViewById(R.id.swipeup_arrow);
        View viewFindViewById4 = launcher.findViewById(R.id.swipeup_guide_arrow1);
        View viewFindViewById5 = launcher.findViewById(R.id.swipeup_guide_arrow2);
        if (viewFindViewById3 != null && viewFindViewById4 != null && viewFindViewById5 != null) {
            int dimensionPixelSize = (this.isPhone && this.isLandscape) ? this.pageIndicatorHeightPx : launcher.getResources().getDimensionPixelSize(R.dimen.swipe_up_view_height);
            LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) viewFindViewById4.getLayoutParams();
            layoutParams5.height = dimensionPixelSize;
            viewFindViewById4.setLayoutParams(layoutParams5);
            LinearLayout.LayoutParams layoutParams6 = (LinearLayout.LayoutParams) viewFindViewById5.getLayoutParams();
            layoutParams6.height = dimensionPixelSize;
            viewFindViewById5.setLayoutParams(layoutParams6);
            FrameLayout.LayoutParams layoutParams7 = (FrameLayout.LayoutParams) viewFindViewById3.getLayoutParams();
            layoutParams7.bottomMargin = i + this.hotseatBarHeightPx;
            viewFindViewById3.setLayoutParams(layoutParams7);
            if (this.isPhone && this.isLandscape && this.allowRotation && this.currNaviBarMode != SysUINavigationMode.Mode.NO_BUTTON) {
                if (isSeascape()) {
                    viewFindViewById.findViewById(R.id.layout).setPadding(workspacePadding.left - this.navibarSizePx, 0, workspacePadding.right, this.hotseatBottomPadding);
                    viewFindViewById2.setPadding(workspacePadding.left - this.navibarSizePx, 0, workspacePadding.right, 0);
                    viewFindViewById3.setPadding(0, 0, workspacePadding.right * 2, 0);
                } else {
                    viewFindViewById.findViewById(R.id.layout).setPadding(workspacePadding.left, 0, workspacePadding.right - this.navibarSizePx, this.hotseatBottomPadding);
                    viewFindViewById2.setPadding(workspacePadding.left, 0, workspacePadding.right - this.navibarSizePx, 0);
                    viewFindViewById3.setPadding(workspacePadding.left * 2, 0, 0, 0);
                }
            } else {
                viewFindViewById.findViewById(R.id.layout).setPadding(workspacePadding.left, 0, workspacePadding.right, 0);
                viewFindViewById2.setPadding(workspacePadding.left, 0, workspacePadding.right, 0);
                viewFindViewById3.setPadding(workspacePadding.left, 0, workspacePadding.right, 0);
            }
        }
        ViewGroup lGOverviewPanel = launcher.getLGOverviewPanel();
        if (lGOverviewPanel != null) {
            FrameLayout.LayoutParams layoutParams8 = (FrameLayout.LayoutParams) lGOverviewPanel.getLayoutParams();
            layoutParams8.gravity = 80;
            if (!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() && this.isPhone && this.isLandscape && this.allowRotation) {
                layoutParams8.width = resources.getDimensionPixelSize(R.dimen.overview_panel_width_land) + this.navibarSizePx;
                layoutParams8.height = resources.getDimensionPixelSize(R.dimen.overview_panel_height_land);
                if (this.currNaviBarMode != SysUINavigationMode.Mode.NO_BUTTON) {
                    if (isSeascape()) {
                        lGOverviewPanel.setPadding(0, 0, this.navibarSizePx, 0);
                    } else {
                        lGOverviewPanel.setPadding(this.navibarSizePx, 0, 0, 0);
                    }
                } else {
                    lGOverviewPanel.setPadding(0, 0, 0, 0);
                }
            } else {
                layoutParams8.width = -1;
                layoutParams8.height = getOverviewModeButtonBarRect().height();
                lGOverviewPanel.setPadding(0, LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() ? 0 : resources.getDimensionPixelSize(R.dimen.overview_panel_paddingTop), 0, 0);
            }
            lGOverviewPanel.setLayoutParams(layoutParams8);
        }
        ViewGroup dynamicGridPannelView = launcher.getDynamicGridPannelView();
        LinearLayout linearLayout = (LinearLayout) dynamicGridPannelView.findViewById(R.id.overview_dynaic_grid_layout);
        LinearLayout linearLayout2 = (LinearLayout) dynamicGridPannelView.findViewById(R.id.overview_dynamicgrid_btn_layout);
        Button button = (Button) linearLayout2.findViewById(R.id.overview_dynamicgrid_cancel_btn);
        Button button2 = (Button) linearLayout2.findViewById(R.id.overview_dynamicgrid_apply_btn);
        if (dynamicGridPannelView == null || linearLayout == null || linearLayout2 == null) {
            return;
        }
        if (this.isPhone && this.isLandscape && this.allowRotation) {
            LinearLayout linearLayout3 = (LinearLayout) dynamicGridPannelView;
            linearLayout3.setOrientation(0);
            linearLayout3.setWeightSum(10.0f);
            FrameLayout.LayoutParams layoutParams9 = (FrameLayout.LayoutParams) dynamicGridPannelView.getLayoutParams();
            layoutParams9.width = -1;
            layoutParams9.height = -2;
            dynamicGridPannelView.setLayoutParams(layoutParams9);
            linearLayout.setPadding(resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_padding_left_land), 0, resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_padding_right_land), 0);
            LinearLayout.LayoutParams layoutParams10 = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams10.weight = 6.0f;
            layoutParams10.width = 0;
            layoutParams10.height = resources.getDimensionPixelSize(R.dimen.overview_panel_height_land);
            linearLayout.setLayoutParams(layoutParams10);
            LinearLayout.LayoutParams layoutParams11 = (LinearLayout.LayoutParams) linearLayout2.getLayoutParams();
            layoutParams11.weight = 4.0f;
            layoutParams11.width = 0;
            layoutParams11.height = resources.getDimensionPixelSize(R.dimen.overview_panel_height_land);
            LinearLayout.LayoutParams layoutParams12 = (LinearLayout.LayoutParams) button.getLayoutParams();
            layoutParams12.width = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_button_width_land);
            layoutParams12.height = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_button_height_land);
            button.setLayoutParams(layoutParams12);
            LinearLayout.LayoutParams layoutParams13 = (LinearLayout.LayoutParams) button2.getLayoutParams();
            layoutParams13.width = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_button_width_land);
            layoutParams13.height = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_button_height_land);
            button2.setLayoutParams(layoutParams13);
            return;
        }
        ((LinearLayout) dynamicGridPannelView).setOrientation(1);
        LinearLayout.LayoutParams layoutParams14 = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams14.width = -1;
        layoutParams14.height = -2;
        linearLayout.setLayoutParams(layoutParams14);
        linearLayout.setPadding(resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_padding_left_port), 0, resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_padding_left_port), 0);
        LinearLayout.LayoutParams layoutParams15 = (LinearLayout.LayoutParams) linearLayout2.getLayoutParams();
        layoutParams15.width = -1;
        layoutParams15.height = -2;
        linearLayout2.setLayoutParams(layoutParams15);
        LinearLayout.LayoutParams layoutParams16 = (LinearLayout.LayoutParams) button.getLayoutParams();
        layoutParams16.width = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_button_width_port);
        layoutParams16.height = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_button_height_port);
        button.setLayoutParams(layoutParams16);
        LinearLayout.LayoutParams layoutParams17 = (LinearLayout.LayoutParams) button2.getLayoutParams();
        layoutParams17.width = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_button_width_port);
        layoutParams17.height = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_panel_button_height_port);
        button2.setLayoutParams(layoutParams17);
    }

    private int getLandscapeIconSizeByDynamicGrid(Context context, Resources res, int numRows, int numColumns, int displaySize) {
        int identifier = res.getIdentifier(String.format(Locale.ENGLISH, "device_profile_iconSize_%d_%dx%d_land", Integer.valueOf(displaySize), Integer.valueOf(numColumns), Integer.valueOf(numRows)), "dimen", context.getPackageName());
        return identifier == 0 ? this.iconSizePx : res.getDimensionPixelSize(identifier);
    }

    private int getLandscapeHotseatBarSizeByDynamicGrid(Context context, Resources res, int numRows, int numColumns, String navibar) {
        int identifier = res.getIdentifier(String.format(Locale.ENGLISH, "dev_prof_hotseat_height_%dx%d_%s_land", Integer.valueOf(numColumns), Integer.valueOf(numRows), navibar), "dimen", context.getPackageName());
        if (identifier == 0) {
            return 0;
        }
        return res.getDimensionPixelSize(identifier);
    }

    private void updateProfileByNaviMode(Context context, Resources res) {
        if (this.isLandscape && this.allowRotation) {
            SysUINavigationMode.Mode mode = SysUINavigationMode.getMode(context);
            if (this.currNaviBarMode == null || this.currNaviBarMode != mode) {
                if (mode != SysUINavigationMode.Mode.NO_BUTTON) {
                    this.hotseatBottomPadding = res.getDimensionPixelSize(R.dimen.device_profile_hotseat_padding_bottom);
                    int landscapeHotseatBarSizeByDynamicGrid = getLandscapeHotseatBarSizeByDynamicGrid(context, res, this.numRows, this.numColumns, "navi") + this.hotseatBottomPadding;
                    this.hotseatBarHeightPx = landscapeHotseatBarSizeByDynamicGrid;
                    this.hotseatBarSizePx = landscapeHotseatBarSizeByDynamicGrid;
                    this.desiredWorkspaceLeftRightMarginPx = res.getDimensionPixelSize(R.dimen.device_profile_workspace_left_right_margin_with_navibar_land);
                } else {
                    this.hotseatBottomPadding = 0;
                    int landscapeHotseatBarSizeByDynamicGrid2 = getLandscapeHotseatBarSizeByDynamicGrid(context, res, this.numRows, this.numColumns, "gesture");
                    this.hotseatBarHeightPx = landscapeHotseatBarSizeByDynamicGrid2;
                    this.hotseatBarSizePx = landscapeHotseatBarSizeByDynamicGrid2;
                    this.desiredWorkspaceLeftRightMarginPx = res.getDimensionPixelSize(R.dimen.device_profile_workspace_left_right_margin_land);
                }
                this.currNaviBarMode = mode;
                this.navibarSizePx = WindowUtils.getNavigationBarHeight(context);
            }
        }
    }
}
