package com.android.launcher3;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.WindowBounds;
import com.android.quickstep.SysUINavigationMode;
import com.lge.launcher3.R;
import com.lge.launcher3.profile.LGDeviceProfile;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class DeviceProfile {
    private static final float MAX_HORIZONTAL_PADDING_PERCENT = 0.14f;
    private static final int PORTRAIT_TABLET_LEFT_RIGHT_PADDING_MULTIPLIER = 4;
    static final String TAG = "DeviceProfile";
    private static final float TALL_DEVICE_ASPECT_RATIO_THRESHOLD = 2.0f;
    public final float allAppsArrangeModeScaleFactor;
    public final int allAppsArrangeModeTranslationY;
    public int allAppsButtonVisualSize;
    public int allAppsCellHeightPx;
    public final int allAppsIconSizePx;
    public final int allAppsIconTextSizePx;
    public int allAppsNumCols;
    public int allAppsNumPredictiveCols;
    public final boolean allowRotation;
    public final float aspectRatio;
    public final int availableHeightPx;
    public final int availableWidthPx;
    public int cellHeightPx;
    public final int cellLayoutBottomPaddingPx;
    public boolean cellLayoutHorizontal;
    public final int cellLayoutPaddingLeftRightPx;
    public int cellWidthPx;
    private final int defaultPageSpacingPx;
    public final Rect defaultWidgetPadding;
    protected int desiredWorkspaceLeftRightMarginPx;
    private float dragViewScale;
    public int dropTargetBarSizePx;
    public int edgeMarginPx;
    public int folderBackgroundOffset;
    public int folderCellHeightPx;
    public int folderCellHeightPxForSwivel;
    public int folderCellWidthPx;
    public int folderCellWidthPxForSwivel;
    public int folderIconSizePx;
    public final int heightPx;
    public int hotseatBarBottomPaddingPx;
    protected int hotseatBarHeightPx;
    public final int hotseatBarSidePaddingEndPx;
    public final int hotseatBarSidePaddingStartPx;
    public int hotseatBarSizePx;
    public final int hotseatBarTopPaddingPx;
    public int hotseatCellHeightPx;
    public int hotseatCellWidthPx;
    public int hotseatIconSizePx;
    public int iconDrawablePaddingOriginalPx;
    public int iconDrawablePaddingPx;
    public int iconSizePx;
    public int iconTextSizePx;
    public final InvariantDeviceProfile inv;
    public final boolean isLandscape;
    public final boolean isLargeTablet;
    public final boolean isMultiWindowMode;
    public final boolean isPhone;
    public final boolean isTablet;
    public int mDisplayId;
    public float mIconScale;
    public boolean mIsMultiDisplay;
    private boolean mIsSeascape;
    private final int mWorkspacePageIndicatorOverlapWorkspace;
    public int numColumns;
    public int numDefaultColumns;
    public int numDefaultRows;
    public int numRows;
    private final int overviewModeBarItemWidthPx;
    private final int overviewModeBarSpacerWidthPx;
    public final int overviewModeDefaultScreenButtonHeightPx;
    private final float overviewModeIconZoneRatio;
    public int overviewModeMaxIconZoneHeightPx;
    private final int overviewModeMinIconZoneHeightPx;
    protected final float overviewModeScaleFactor;
    public final int overviewModeWodrkspaceTranslationYPx;
    public final int overviewModeWodrkspaceTranslationYPxInMultiWindow;
    public int pageIndicatorHeightPx;
    protected int searchBarSpaceHeightPx;
    protected int searchBarSpaceWidthPx;
    public final boolean transposeLayoutWithOrientation;
    public final int verticalDragHandleSizePx;
    public final int widthPx;
    public int windowX;
    public int windowY;
    public final int workspacePageIndicatorHeight;
    public float workspaceSpringLoadShrinkFactor;
    public final int workspaceSpringLoadedBottomSpace;
    public int navibarSizePx = 0;
    public int statusbarSizePx = 0;
    public SysUINavigationMode.Mode currNaviBarMode = null;
    public int iconSizeLandPx = 0;
    public int hotseatBottomPadding = 0;
    public final PointF appWidgetScale = new PointF(1.0f, 1.0f);
    protected final Rect mInsets = new Rect();
    public final Rect workspacePadding = new Rect();
    private final Rect mHotseatPadding = new Rect();

    public interface OnDeviceProfileChangeListener {
        void onDeviceProfileChanged(DeviceProfile dp);
    }

    public DeviceProfile(Context context, InvariantDeviceProfile inv, Point minSize, Point maxSize, int width, int height, boolean isLandscape, boolean isMultiWindowMode) {
        this.numDefaultRows = 0;
        this.numDefaultColumns = 0;
        this.inv = inv;
        this.isLandscape = isLandscape;
        this.isMultiWindowMode = isMultiWindowMode;
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        boolean z = resources.getBoolean(R.bool.is_tablet);
        this.isTablet = z;
        boolean z2 = resources.getBoolean(R.bool.is_large_tablet);
        this.isLargeTablet = z2;
        this.isPhone = (z || z2) ? false : true;
        this.transposeLayoutWithOrientation = resources.getBoolean(R.bool.hotseat_transpose_layout_with_orientation);
        this.allowRotation = isMultiWindowMode || LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue();
        this.cellLayoutHorizontal = resources.getBoolean(R.bool.cell_layout_horizontal);
        this.defaultWidgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(context, new ComponentName(context.getPackageName(), getClass().getName()), null);
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.dynamic_grid_edge_margin);
        this.edgeMarginPx = dimensionPixelSize;
        this.desiredWorkspaceLeftRightMarginPx = dimensionPixelSize * 2;
        this.pageIndicatorHeightPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_page_indicator_height);
        this.verticalDragHandleSizePx = resources.getDimensionPixelSize(R.dimen.vertical_drag_handle_size);
        this.defaultPageSpacingPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_workspace_page_spacing);
        this.overviewModeMinIconZoneHeightPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_min_icon_zone_height);
        this.overviewModeMaxIconZoneHeightPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_max_icon_zone_height);
        if (isLandscape) {
            this.overviewModeMaxIconZoneHeightPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_max_icon_zone_height_land);
        }
        this.overviewModeBarItemWidthPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_bar_item_width);
        this.overviewModeBarSpacerWidthPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_overview_bar_spacer_width);
        this.iconDrawablePaddingOriginalPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_icon_drawable_padding);
        this.workspaceSpringLoadedBottomSpace = resources.getDimensionPixelSize(R.dimen.dynamic_grid_min_spring_loaded_space);
        this.overviewModeDefaultScreenButtonHeightPx = resources.getDimensionPixelSize(R.dimen.overview_default_screen_button_height) + resources.getDimensionPixelSize(R.dimen.overview_default_screen_button_margin_top) + resources.getDimensionPixelSize(R.dimen.overview_default_screen_button_margin_bottom);
        this.overviewModeWodrkspaceTranslationYPxInMultiWindow = resources.getDimensionPixelSize(R.dimen.overview_workspace_translation_y_in_multiwindow);
        this.allAppsIconTextSizePx = Utilities.pxFromDp(inv.iconTextSize, displayMetrics);
        this.widthPx = width;
        this.heightPx = height;
        int i = (isVerticalBarLayout() || !z) ? 1 : 4;
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.dynamic_grid_cell_layout_padding);
        if (isLandscape) {
            this.overviewModeIconZoneRatio = resources.getInteger(R.integer.config_dynamic_grid_overview_icon_zone_percentage_land) / 100.0f;
            this.overviewModeScaleFactor = resources.getInteger(R.integer.config_dynamic_grid_overview_scale_percentage_land) / 100.0f;
            this.availableWidthPx = maxSize.x;
            this.availableHeightPx = minSize.y;
            this.workspaceSpringLoadShrinkFactor = resources.getInteger(R.integer.config_workspaceSpringLoadShrinkPercentage_land) / 100.0f;
            this.overviewModeWodrkspaceTranslationYPx = resources.getDimensionPixelSize(R.dimen.overview_workspace_translation_y_land);
            this.allAppsArrangeModeScaleFactor = resources.getInteger(R.integer.config_allAppsShrinkPercentage_arrangeMode_land) / 100.0f;
            this.allAppsArrangeModeTranslationY = resources.getDimensionPixelSize(R.dimen.all_apps_shrink_translationY_land);
            this.cellLayoutPaddingLeftRightPx = 0;
            this.cellLayoutBottomPaddingPx = dimensionPixelSize2;
        } else {
            this.overviewModeIconZoneRatio = resources.getInteger(R.integer.config_dynamic_grid_overview_icon_zone_percentage) / 100.0f;
            this.overviewModeScaleFactor = resources.getInteger(R.integer.config_dynamic_grid_overview_scale_percentage) / 100.0f;
            this.availableWidthPx = minSize.x;
            this.availableHeightPx = maxSize.y;
            this.workspaceSpringLoadShrinkFactor = resources.getInteger(R.integer.config_workspaceSpringLoadShrinkPercentage) / 100.0f;
            this.overviewModeWodrkspaceTranslationYPx = resources.getDimensionPixelSize(R.dimen.overview_workspace_translation_y_port);
            this.allAppsArrangeModeScaleFactor = resources.getInteger(R.integer.config_allAppsShrinkPercentage_arrangeMode) / 100.0f;
            this.allAppsArrangeModeTranslationY = resources.getDimensionPixelSize(R.dimen.all_apps_shrink_translationY);
            this.cellLayoutPaddingLeftRightPx = i * dimensionPixelSize2;
            this.cellLayoutBottomPaddingPx = 0;
        }
        float fMax = Math.max(width, height) / Math.min(width, height);
        this.aspectRatio = fMax;
        boolean z3 = Float.compare(fMax, 2.0f) >= 0;
        int dimensionPixelSize3 = resources.getDimensionPixelSize(R.dimen.workspace_page_indicator_height);
        this.workspacePageIndicatorHeight = dimensionPixelSize3;
        this.mWorkspacePageIndicatorOverlapWorkspace = resources.getDimensionPixelSize(R.dimen.workspace_page_indicator_overlap_workspace);
        this.hotseatBarTopPaddingPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_hotseat_top_padding);
        this.hotseatBarBottomPaddingPx = (z3 ? 0 : resources.getDimensionPixelSize(R.dimen.dynamic_grid_hotseat_bottom_non_tall_padding)) + resources.getDimensionPixelSize(R.dimen.dynamic_grid_hotseat_bottom_padding);
        this.hotseatBarSidePaddingEndPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_hotseat_side_padding);
        this.hotseatBarSidePaddingStartPx = isVerticalBarLayout() ? dimensionPixelSize3 : 0;
        this.numDefaultRows = resources.getInteger(R.integer.device_profile_default_numRows);
        this.numDefaultColumns = resources.getInteger(R.integer.device_profile_default_numColumns);
        this.numRows = LGInvariantDeviceProfile.getSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, this.numDefaultRows);
        this.numColumns = LGInvariantDeviceProfile.getSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, this.numDefaultColumns);
        this.allAppsIconSizePx = Utilities.pxFromDp(inv.iconSize, displayMetrics);
        this.allAppsCellHeightPx = getCellSize().y;
        checkValidGridSize(context);
        updateAvailableDimensions(context, displayMetrics, resources);
        computeAllAppsButtonSize(context);
    }

    public DeviceProfile copy(Context context) {
        Point point = new Point(this.availableWidthPx, this.availableHeightPx);
        return new LGDeviceProfile(context, this.inv, point, point, this.widthPx, this.heightPx, this.isLandscape, this.isMultiWindowMode, this.mDisplayId, this.mIsMultiDisplay);
    }

    private void checkValidGridSize(Context context) {
        for (String str : context.getResources().getStringArray(R.array.config_dynamic_grid_preset)) {
            String[] strArrSplit = new String(str).split("x");
            if (strArrSplit.length == 2) {
                try {
                    int i = Integer.parseInt(strArrSplit[0]);
                    if (this.numRows == Integer.parseInt(strArrSplit[1]) && this.numColumns == i) {
                        LGLog.d(TAG, "checkValidGridSize is true");
                        return;
                    }
                } catch (NumberFormatException unused) {
                    continue;
                }
            }
        }
        LGLog.i(TAG, "checkValidGridSize change to row = " + this.numDefaultRows + "/ colum = " + this.numDefaultColumns);
        this.numRows = this.numDefaultRows;
        this.numColumns = this.numDefaultColumns;
        LGInvariantDeviceProfile.setSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, this.numColumns);
        LGInvariantDeviceProfile.setSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, this.numRows);
    }

    public DeviceProfile getMultiWindowProfile(Context context, WindowBounds windowBounds) {
        Point point = new Point(Math.min(this.availableWidthPx, windowBounds.availableSize.x), Math.min(this.availableHeightPx, windowBounds.availableSize.y));
        DeviceProfile deviceProfile = new DeviceProfile(context, this.inv, point, point, point.x, point.y, this.isLandscape, true);
        deviceProfile.setWindowPosition(windowBounds);
        if (!this.isLandscape || !this.cellLayoutHorizontal) {
            deviceProfile.cellHeightPx = deviceProfile.iconSizePx + deviceProfile.iconDrawablePaddingPx + Utilities.calculateTextHeight(deviceProfile.iconTextSizePx);
        }
        deviceProfile.appWidgetScale.set(deviceProfile.getCellSize().x / getCellSize().x, deviceProfile.getCellSize().y / getCellSize().y);
        return deviceProfile;
    }

    public DeviceProfile getFullScreenProfile(Context context) {
        if (this.mIsMultiDisplay) {
            return this.inv.getMultiDisplayProfile(context);
        }
        return this.isLandscape ? this.inv.landscapeProfile : this.inv.portraitProfile;
    }

    protected void computeAllAppsButtonSize(Context context) {
        this.allAppsButtonVisualSize = (int) (this.hotseatIconSizePx * (1.0f - (context.getResources().getInteger(R.integer.config_allAppsButtonPaddingPercent) / 100.0f)));
    }

    /* JADX WARN: Removed duplicated region for block: B:8:0x0030  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void updateAvailableDimensions(android.content.Context r8, android.util.DisplayMetrics r9, android.content.res.Resources r10) {
        /*
            r7 = this;
            int r6 = r7.iconDrawablePaddingOriginalPx
            r2 = 1065353216(0x3f800000, float:1.0)
            r0 = r7
            r1 = r8
            r3 = r6
            r4 = r10
            r5 = r9
            r0.updateIconSize(r1, r2, r3, r4, r5)
            int r0 = r7.cellHeightPx
            com.android.launcher3.InvariantDeviceProfile r1 = r7.inv
            int r1 = r1.numRows
            int r0 = r0 * r1
            float r0 = (float) r0
            r1 = 0
            android.graphics.Rect r2 = r7.getWorkspacePadding(r1)
            int r3 = r7.availableHeightPx
            int r4 = r2.top
            int r3 = r3 - r4
            int r2 = r2.bottom
            int r3 = r3 - r2
            r2 = 0
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 == 0) goto L30
            if (r3 == 0) goto L30
            float r2 = (float) r3
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L30
            float r2 = r2 / r0
            r3 = r1
            goto L34
        L30:
            r0 = 1065353216(0x3f800000, float:1.0)
            r2 = r0
            r3 = r6
        L34:
            r0 = r7
            r1 = r8
            r4 = r10
            r5 = r9
            r0.updateIconSize(r1, r2, r3, r4, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.DeviceProfile.updateAvailableDimensions(android.content.Context, android.util.DisplayMetrics, android.content.res.Resources):void");
    }

    protected void updateIconSize(Context context, float scale, int drawablePadding, Resources res, DisplayMetrics dm) {
        if (LGHomeFeature.Config.FEATURE_USE_DEFAULT_LOW_DPI.getValue()) {
            if (Utilities.isLowDisplay(context, res)) {
                int i = this.numRows;
                if (i == 6) {
                    this.inv.iconSize = res.getFloat(R.dimen.device_profile_iconSize_4x6_low);
                } else if (this.numColumns == 5 && i == 5) {
                    this.inv.iconSize = res.getFloat(R.dimen.device_profile_iconSize_5x5_low);
                    if (LGHomeFeature.Config.FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME.getValue() && !LGHomeFeature.isDisableEasyHome()) {
                        this.inv.iconSize = res.getFloat(R.dimen.device_profile_iconSize_4x6_low);
                    }
                } else {
                    this.inv.iconSize = res.getFloat(R.dimen.device_profile_iconSize_low);
                }
                InvariantDeviceProfile invariantDeviceProfile = this.inv;
                invariantDeviceProfile.hotseatIconSize = invariantDeviceProfile.iconSize;
                this.inv.iconTextSize = res.getFloat(R.dimen.device_profile_iconTextSize_low);
            } else {
                int i2 = this.numRows;
                if (i2 == 6) {
                    this.inv.iconSize = res.getFloat(R.dimen.device_profile_iconSize_4x6);
                } else if (this.numColumns == 5 && i2 == 5) {
                    this.inv.iconSize = res.getFloat(R.dimen.device_profile_iconSize_5x5);
                    if (LGHomeFeature.Config.FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME.getValue() && !LGHomeFeature.isDisableEasyHome()) {
                        this.inv.iconSize = res.getFloat(R.dimen.device_profile_iconSize_4x6);
                    }
                } else {
                    this.inv.iconSize = res.getFloat(R.dimen.device_profile_iconSize);
                }
                InvariantDeviceProfile invariantDeviceProfile2 = this.inv;
                invariantDeviceProfile2.hotseatIconSize = invariantDeviceProfile2.iconSize;
                this.inv.iconTextSize = res.getFloat(R.dimen.device_profile_iconTextSize);
            }
            LGLog.d(TAG, "Grid:" + this.numColumns + "x" + this.numRows + ", iconSize = " + this.inv.iconSize + ", iconTextSize = " + this.inv.iconTextSize);
            float f = this.inv.hotseatIconSize;
            StringBuilder sb = new StringBuilder();
            sb.append("hotseatIconSize = ");
            sb.append(f);
            LGLog.d(TAG, sb.toString());
        }
        this.mIconScale = scale;
        this.iconSizePx = (int) (Utilities.pxFromDp(this.inv.iconSize, dm) * scale);
        if (this.isLandscape && this.cellLayoutHorizontal) {
            this.iconTextSizePx = Utilities.pxFromDp(res.getFloat(R.dimen.device_profile_iconTextSize_land), dm);
        } else {
            this.iconTextSizePx = (int) (Utilities.pxFromDp(this.inv.iconTextSize, dm) * scale);
        }
        this.iconDrawablePaddingPx = drawablePadding;
        this.hotseatIconSizePx = (int) (Utilities.pxFromDp(this.inv.hotseatIconSize, dm) * scale);
        this.searchBarSpaceWidthPx = Math.min(this.widthPx, res.getDimensionPixelSize(R.dimen.dynamic_grid_search_bar_max_width));
        this.searchBarSpaceHeightPx = getSearchBarTopOffset() + res.getDimensionPixelSize(R.dimen.dynamic_grid_search_bar_height);
        if (this.isLandscape && this.cellLayoutHorizontal) {
            int i3 = this.availableWidthPx / this.numColumns;
            int i4 = this.edgeMarginPx;
            this.cellWidthPx = i3 - (i4 * 2);
            this.cellHeightPx = (this.availableHeightPx / this.numRows) - (i4 * 2);
        } else {
            Paint paint = new Paint();
            paint.setTextSize(this.iconTextSizePx);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            int i5 = this.iconSizePx;
            this.cellWidthPx = i5;
            this.cellHeightPx = i5 + this.iconDrawablePaddingPx + ((int) Math.ceil(fontMetrics.bottom - fontMetrics.top));
        }
        float dimensionPixelSize = res.getDimensionPixelSize(R.dimen.dragViewScale);
        int i6 = this.iconSizePx;
        this.dragViewScale = (i6 + dimensionPixelSize) / i6;
        this.hotseatBarSizePx = this.hotseatBarHeightPx;
        this.hotseatCellWidthPx = i6;
        this.hotseatCellHeightPx = i6;
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            this.folderCellWidthPxForSwivel = res.getDimensionPixelSize(R.dimen.folder_background_width_swivel_landscape) / res.getInteger(R.integer.device_profile_numFolderColumns_swivel_home);
            this.folderCellHeightPxForSwivel = res.getDimensionPixelSize(R.dimen.folder_cell_height);
        }
        this.folderCellWidthPx = this.cellWidthPx;
        this.folderCellHeightPx = this.cellHeightPx;
        this.folderBackgroundOffset = 0;
        this.folderIconSizePx = this.iconSizePx;
    }

    public void updateAppsViewNumCols(Resources res, int recyclerViewWidth) {
        int dimensionPixelSize = res.getDimensionPixelSize(R.dimen.all_apps_grid_view_start_margin);
        int dimensionPixelSize2 = res.getDimensionPixelSize(R.dimen.all_apps_icon_width_gap);
        if (recyclerViewWidth <= 0) {
            recyclerViewWidth = this.availableWidthPx;
        }
        int i = (recyclerViewWidth - dimensionPixelSize) / (this.allAppsIconSizePx + dimensionPixelSize2);
        int iMax = Math.max(this.inv.minAllAppsPredictionColumns, i);
        this.allAppsNumCols = i;
        this.allAppsNumPredictiveCols = iMax;
    }

    private int getSearchBarTopOffset() {
        if (this.isTablet) {
            return this.edgeMarginPx * 4;
        }
        return this.edgeMarginPx * 2;
    }

    public Rect getSearchBarBounds(boolean isLayoutRtl) {
        Rect rect = new Rect();
        if (this.isTablet) {
            int currentWidth = ((getCurrentWidth() - (this.edgeMarginPx * 2)) - (this.inv.numColumns * this.cellWidthPx)) / ((this.inv.numColumns + 1) * 2);
            rect.set(this.edgeMarginPx + currentWidth, getSearchBarTopOffset(), this.availableWidthPx - (this.edgeMarginPx + currentWidth), this.searchBarSpaceHeightPx);
        } else {
            rect.set(this.desiredWorkspaceLeftRightMarginPx - this.defaultWidgetPadding.left, getSearchBarTopOffset(), this.availableWidthPx - (this.desiredWorkspaceLeftRightMarginPx - this.defaultWidgetPadding.right), this.searchBarSpaceHeightPx);
        }
        return rect;
    }

    public Point getCellSize() {
        Point point = new Point();
        Point totalWorkspacePadding = getTotalWorkspacePadding();
        point.x = calculateCellWidth(this.availableWidthPx - totalWorkspacePadding.x, this.inv.numColumns);
        point.y = calculateCellHeight(this.availableHeightPx - totalWorkspacePadding.y, this.inv.numRows);
        return point;
    }

    public Point getTotalWorkspacePadding() {
        Rect workspacePadding = getWorkspacePadding(false);
        return new Point(workspacePadding.left + workspacePadding.right, workspacePadding.top + workspacePadding.bottom);
    }

    public Rect getWorkspacePadding(boolean isLayoutRtl) {
        Rect searchBarBounds = getSearchBarBounds(isLayoutRtl);
        Rect rect = new Rect();
        if (this.isLandscape && this.transposeLayoutWithOrientation) {
            if (isLayoutRtl) {
                rect.set(this.hotseatBarHeightPx, this.edgeMarginPx, searchBarBounds.width(), this.edgeMarginPx);
            } else {
                int iWidth = searchBarBounds.width();
                int i = this.edgeMarginPx;
                rect.set(iWidth, i, this.hotseatBarHeightPx, i);
            }
        } else if (this.isTablet) {
            float f = ((this.dragViewScale - 1.0f) / 2.0f) + 1.0f;
            int currentWidth = getCurrentWidth();
            int currentHeight = getCurrentHeight();
            int i2 = searchBarBounds.bottom;
            int i3 = this.hotseatBarHeightPx + this.pageIndicatorHeightPx;
            int iMax = Math.max(0, currentWidth - ((int) ((this.inv.numColumns * this.cellWidthPx) + ((this.inv.numColumns * f) * this.cellWidthPx)))) / 2;
            int iMax2 = Math.max(0, ((currentHeight - i2) - i3) - ((this.inv.numRows * 2) * this.cellHeightPx)) / 2;
            rect.set(iMax, i2 + iMax2, iMax, i3 + iMax2);
        } else {
            rect.set(this.desiredWorkspaceLeftRightMarginPx - this.defaultWidgetPadding.left, searchBarBounds.bottom, this.desiredWorkspaceLeftRightMarginPx - this.defaultWidgetPadding.right, this.hotseatBarHeightPx + this.pageIndicatorHeightPx);
        }
        return rect;
    }

    protected int getWorkspacePageSpacing(boolean isLayoutRtl) {
        if (isVerticalBarLayout() || this.isLargeTablet) {
            return this.defaultPageSpacingPx;
        }
        if (this.isPhone && isSeascape()) {
            return Math.max(this.defaultPageSpacingPx, this.desiredWorkspaceLeftRightMarginPx);
        }
        return Math.max(this.defaultPageSpacingPx, getWorkspacePadding(isLayoutRtl).left * 2);
    }

    public Rect getOverviewModeButtonBarRect() {
        int iMin = Math.min(this.overviewModeMaxIconZoneHeightPx, Math.max(this.overviewModeMinIconZoneHeightPx, (int) (this.overviewModeIconZoneRatio * this.availableHeightPx)));
        int i = this.availableHeightPx;
        return new Rect(0, i - iMin, 0, i);
    }

    public float getOverviewModeScale(boolean isLayoutRtl) {
        Rect workspacePadding = getWorkspacePadding(isLayoutRtl);
        Rect overviewModeButtonBarRect = getOverviewModeButtonBarRect();
        return (this.overviewModeScaleFactor * (r1 - overviewModeButtonBarRect.height())) / (((this.availableHeightPx - workspacePadding.top) - workspacePadding.bottom) - this.overviewModeDefaultScreenButtonHeightPx);
    }

    Rect getHotseatRect() {
        if (isVerticalBarLayout()) {
            return new Rect(this.availableWidthPx - this.hotseatBarHeightPx, 0, Integer.MAX_VALUE, this.availableHeightPx);
        }
        return new Rect(0, this.availableHeightPx - this.hotseatBarHeightPx, this.availableWidthPx, Integer.MAX_VALUE);
    }

    public static int calculateCellWidth(int width, int countX) {
        if (width == 0 || countX == 0) {
            LGLog.w(TAG, "calculateCellHeight error. height = " + width + ", countY = " + countX, new int[0]);
            return 0;
        }
        return width / countX;
    }

    public static int calculateCellHeight(int height, int countY) {
        if (height == 0 || countY == 0) {
            LGLog.w(TAG, "calculateCellHeight error. height = " + height + ", countY = " + countY, new int[0]);
            return 0;
        }
        return height / countY;
    }

    public boolean isVerticalBarLayout() {
        return this.isLandscape && this.transposeLayoutWithOrientation;
    }

    public boolean isAllowRotationAndLandscape() {
        return this.isLandscape && this.allowRotation;
    }

    boolean shouldFadeAdjacentWorkspaceScreens() {
        return isVerticalBarLayout() || this.isLargeTablet || (this.isLandscape && this.allowRotation);
    }

    private int getVisibleChildCount(ViewGroup parent) {
        int i = 0;
        for (int i2 = 0; i2 < parent.getChildCount(); i2++) {
            if (parent.getChildAt(i2).getVisibility() != 8) {
                i++;
            }
        }
        return i;
    }

    public void layout(Launcher launcher) {
        boolean zIsVerticalBarLayout = isVerticalBarLayout();
        boolean zIsRtl = Utilities.isRtl(launcher.getResources());
        SearchDropTargetBar searchBar = launcher.getSearchBar();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) searchBar.getLayoutParams();
        layoutParams.gravity = 48;
        layoutParams.height = this.searchBarSpaceHeightPx;
        ((FrameLayout) searchBar.findViewById(R.id.drag_target_bar)).getLayoutParams().width = this.searchBarSpaceWidthPx;
        searchBar.setLayoutParams(layoutParams);
        com.lge.launcher3.PagedView pagedView = (com.lge.launcher3.PagedView) launcher.findViewById(R.id.workspace);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) pagedView.getLayoutParams();
        layoutParams2.gravity = 17;
        Rect workspacePadding = getWorkspacePadding(zIsRtl);
        pagedView.setLayoutParams(layoutParams2);
        pagedView.setPadding(workspacePadding.left, workspacePadding.top, workspacePadding.right, workspacePadding.bottom);
        pagedView.setPageSpacing(getWorkspacePageSpacing(zIsRtl));
        View viewFindViewById = launcher.findViewById(R.id.hotseat);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) viewFindViewById.getLayoutParams();
        if (zIsVerticalBarLayout) {
            layoutParams3.gravity = 5;
            layoutParams3.width = this.hotseatBarHeightPx;
            layoutParams3.height = -1;
            View viewFindViewById2 = viewFindViewById.findViewById(R.id.layout);
            int i = this.edgeMarginPx;
            viewFindViewById2.setPadding(0, i * 2, 0, i * 2);
        } else {
            layoutParams3.gravity = 80;
            layoutParams3.width = -1;
            layoutParams3.height = this.hotseatBarHeightPx;
            viewFindViewById.findViewById(R.id.layout).setPadding(workspacePadding.left, 0, workspacePadding.right, 0);
        }
        viewFindViewById.setLayoutParams(layoutParams3);
        View viewFindViewById3 = launcher.findViewById(R.id.page_indicator);
        if (viewFindViewById3 != null) {
            if (zIsVerticalBarLayout) {
                viewFindViewById3.setVisibility(8);
            } else {
                FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) viewFindViewById3.getLayoutParams();
                layoutParams4.gravity = 81;
                layoutParams4.width = -2;
                layoutParams4.height = launcher.getResources().getDimensionPixelSize(R.dimen.device_profile_pageIndicator_height);
                layoutParams4.bottomMargin = this.hotseatBarHeightPx;
                viewFindViewById3.setLayoutParams(layoutParams4);
            }
        }
        ViewGroup lGOverviewPanel = launcher.getLGOverviewPanel();
        if (lGOverviewPanel != null) {
            configViewEditmode(lGOverviewPanel);
        }
    }

    public void configViewEditmode(ViewGroup overviewMode) {
        Rect overviewModeButtonBarRect = getOverviewModeButtonBarRect();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) overviewMode.getLayoutParams();
        layoutParams.gravity = 81;
        int visibleChildCount = getVisibleChildCount(overviewMode);
        layoutParams.width = Math.min(this.availableWidthPx, (this.overviewModeBarItemWidthPx * visibleChildCount) + ((visibleChildCount - 1) * this.overviewModeBarSpacerWidthPx));
        layoutParams.height = overviewModeButtonBarRect.height();
        overviewMode.setLayoutParams(layoutParams);
    }

    private int getCurrentWidth() {
        if (this.isLandscape) {
            return Math.max(this.widthPx, this.heightPx);
        }
        return Math.min(this.widthPx, this.heightPx);
    }

    private int getCurrentHeight() {
        if (this.isLandscape) {
            return Math.min(this.widthPx, this.heightPx);
        }
        return Math.max(this.widthPx, this.heightPx);
    }

    public void resetGridSize(Context context) {
        this.numColumns = this.numDefaultColumns;
        this.numRows = this.numDefaultRows;
    }

    public Rect getHotseatLayoutPadding() {
        if (isVerticalBarLayout()) {
            if (isSeascape()) {
                this.mHotseatPadding.set(this.mInsets.left + this.hotseatBarSidePaddingStartPx, this.mInsets.top, this.hotseatBarSidePaddingEndPx, this.mInsets.bottom);
            } else {
                this.mHotseatPadding.set(this.hotseatBarSidePaddingEndPx, this.mInsets.top, this.mInsets.right + this.hotseatBarSidePaddingStartPx, this.mInsets.bottom);
            }
        } else {
            int iRound = Math.round(((this.widthPx / this.inv.numColumns) - (this.widthPx / this.inv.numHotseatIcons)) / 2.0f);
            this.mHotseatPadding.set(this.workspacePadding.left + iRound + this.cellLayoutPaddingLeftRightPx + this.mInsets.left, this.hotseatBarTopPaddingPx, iRound + this.workspacePadding.right + this.cellLayoutPaddingLeftRightPx + this.mInsets.right, this.hotseatBarBottomPaddingPx + this.mInsets.bottom + this.cellLayoutBottomPaddingPx);
        }
        return this.mHotseatPadding;
    }

    public void updateInsets(Rect insets) {
        this.mInsets.set(insets);
    }

    public Rect getInsets() {
        return this.mInsets;
    }

    public boolean updateIsSeascape(Context context) {
        if (isVerticalBarLayout() || this.allowRotation) {
            boolean z = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(context.getApplicationContext()).getInfo().rotation == 3;
            if (this.mIsSeascape != z) {
                this.mIsSeascape = z;
                return true;
            }
        }
        return false;
    }

    public boolean isSeascape() {
        return (isVerticalBarLayout() || this.allowRotation) && this.mIsSeascape;
    }

    public void getItemLocation(int cellX, int cellY, int spanX, int spanY, int container, int pageDiff, Rect outBounds) {
        outBounds.setEmpty();
        if (container == -101) {
            if (isVerticalBarLayout()) {
                int i = this.availableHeightPx / this.inv.numRows;
                if (this.mIsSeascape) {
                    outBounds.left = this.mHotseatPadding.left;
                } else {
                    outBounds.left = (this.availableWidthPx - this.hotseatBarSizePx) + this.mHotseatPadding.left;
                }
                outBounds.right = outBounds.left + this.iconSizePx;
                outBounds.top = this.mHotseatPadding.top + (((this.inv.numRows - cellX) - 1) * i);
                outBounds.bottom = outBounds.top + i;
                return;
            }
            int i2 = this.hotseatBarSizePx;
            outBounds.left = this.mInsets.left + this.workspacePadding.left;
            outBounds.right = outBounds.left + getCellSize().x;
            outBounds.top = (this.mInsets.top + this.availableHeightPx) - this.hotseatBarSizePx;
            outBounds.bottom = outBounds.top + i2;
            return;
        }
        outBounds.left = this.mInsets.left + this.workspacePadding.left;
        outBounds.right = outBounds.left + (getCellSize().x * spanX);
        outBounds.top = this.mInsets.top + this.workspacePadding.top + (cellY * getCellSize().y);
        outBounds.bottom = outBounds.top + (getCellSize().y * spanY);
    }

    public void setWindowPosition(WindowBounds windowBounds) {
        LGLog.d(TAG, "[DEVICE_PROFILE] setWindowPosition : " + windowBounds);
        this.windowX = windowBounds.bounds.left;
        this.windowY = windowBounds.bounds.top;
    }
}
