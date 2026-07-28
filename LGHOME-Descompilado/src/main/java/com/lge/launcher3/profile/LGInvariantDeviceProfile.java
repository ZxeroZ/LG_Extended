package com.lge.launcher3.profile;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.Utilities;
import com.android.launcher3.dot.DotInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class LGInvariantDeviceProfile extends InvariantDeviceProfile {
    static final String TAG = "LGInvariantDeviceProfile";
    public int numDefaultColumns;
    public int numDefaultRows;

    public LGInvariantDeviceProfile(InvariantDeviceProfile dp) {
        super(dp);
        this.numDefaultRows = 0;
        this.numDefaultColumns = 0;
        this.mRealSize.set(dp.mRealSize.x, dp.mRealSize.y);
        this.availableSizes.set(dp.availableSizes);
        this.availableSizesForMultiDisplay.set(dp.availableSizesForMultiDisplay);
    }

    public LGInvariantDeviceProfile(Context context) {
        super(context);
        this.numDefaultRows = 0;
        this.numDefaultColumns = 0;
        initGrid(context, null, false);
    }

    @Override // com.android.launcher3.InvariantDeviceProfile
    public String initGrid(Context context, String gridName) {
        return initGrid(context, gridName, true);
    }

    public String initGrid(Context context, String gridName, boolean callSuper) {
        Rect insets = this.portraitProfile != null ? this.portraitProfile.getInsets() : new Rect();
        Rect insets2 = this.landscapeProfile != null ? this.landscapeProfile.getInsets() : new Rect();
        LGLog.i(TAG, "initGrid : " + callSuper + ", oldInsets: " + insets + ", " + insets2);
        this.mDefaultTargetYRatio = context.getResources().getFloat(R.dimen.config_ratio_target_y);
        if (callSuper) {
            super.initGrid(context, gridName);
        }
        initResources(context);
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        defaultDisplay.getMetrics(new DisplayMetrics());
        Point point = new Point();
        Point point2 = new Point();
        defaultDisplay.getCurrentSizeRange(point, point2);
        Point point3 = new Point();
        defaultDisplay.getRealSize(point3);
        int iMin = Math.min(point3.x, point3.y);
        int iMax = Math.max(point3.x, point3.y);
        this.landscapeProfile = new LGDeviceProfile(context, this, point, point2, iMax, iMin, true, false, 0, false);
        this.portraitProfile = new LGDeviceProfile(context, this, point, point2, iMin, iMax, false, false, 0, false);
        this.landscapeProfile.updateInsets(insets2);
        this.portraitProfile.updateInsets(insets);
        this.hotseatAllAppsRank = LGHomeFeature.isEnableDefaultHome() ? DotInfo.MAX_COUNT : this.hotseatAllAppsRank;
        this.mRealSize.set(point3.x, point3.y);
        this.availableSizes.set(this.portraitProfile.availableWidthPx, this.portraitProfile.availableHeightPx, this.landscapeProfile.availableWidthPx, this.landscapeProfile.availableHeightPx);
        if (this.portraitProfileForMultiDisplay != null && this.landscapeProfileForMultiDisplay != null) {
            this.availableSizesForMultiDisplay.set(this.portraitProfileForMultiDisplay.availableWidthPx, this.portraitProfileForMultiDisplay.availableHeightPx, this.landscapeProfileForMultiDisplay.availableWidthPx, this.landscapeProfileForMultiDisplay.availableHeightPx);
        }
        this.mColorOfLetterBox = context.getResources().getColor(R.color.letterbox_color_for_thumbnail);
        return null;
    }

    public LGInvariantDeviceProfile(String name, float minWidthDps, float minHeightDps, int numRows, int numColumns, int numFolderRows, int numFolderColumns, int minAllAppsPredictionColumns, float iconSize, float iconTextSize, int numHotseatIcons, float hotseatIconSize, int defaultLayoutId) {
        super(name, minWidthDps, minHeightDps, numRows, numColumns, numFolderRows, numFolderColumns, minAllAppsPredictionColumns, iconSize, iconTextSize, numHotseatIcons, hotseatIconSize, defaultLayoutId);
        this.numDefaultRows = 0;
        this.numDefaultColumns = 0;
    }

    @Override // com.android.launcher3.InvariantDeviceProfile
    protected ArrayList<InvariantDeviceProfile> getPredefinedDeviceProfiles(Context context) {
        ArrayList<InvariantDeviceProfile> arrayList = new ArrayList<>();
        initResources(context);
        arrayList.add(new LGInvariantDeviceProfile(this.name, this.minWidthDps, this.minHeightDps, this.numRows, this.numColumns, this.numFolderRows, this.numFolderColumns, this.minAllAppsPredictionColumns, this.iconSize, this.iconTextSize, this.numHotseatIcons, this.hotseatIconSize, this.defaultLayoutId));
        return arrayList;
    }

    private void initResources(Context context) {
        Resources resources = context.getResources();
        this.name = resources.getString(R.string.device_profile_name);
        this.minWidthDps = resources.getFloat(R.dimen.device_profile_minWidthDps);
        this.minHeightDps = resources.getFloat(R.dimen.device_profile_minHeightDps);
        this.numDefaultRows = resources.getInteger(R.integer.device_profile_default_numRows);
        this.numDefaultColumns = resources.getInteger(R.integer.device_profile_default_numColumns);
        this.numRows = getSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, this.numDefaultRows);
        this.numColumns = getSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, this.numDefaultColumns);
        this.minAllAppsPredictionColumns = resources.getInteger(R.integer.device_profile_minAllAppsPredictionColumns);
        this.iconSize = getIconSizeByDynamicGrid(context, resources, this.numDefaultRows, this.numDefaultColumns, this.numRows, this.numColumns);
        this.iconTextSize = resources.getFloat(R.dimen.device_profile_iconTextSize);
        this.numHotseatIcons = (int) resources.getFloat(R.dimen.device_profile_numHotseatIcons);
        this.hotseatIconSize = getIconSizeByDynamicGrid(context, resources, this.numDefaultRows, this.numDefaultColumns, this.numRows, this.numColumns);
        boolean zIsEnableDefaultHome = LGHomeFeature.isEnableDefaultHome();
        boolean z = !LGHomeFeature.isDisableEasyHome();
        String str = "default_workspace_items" + (zIsEnableDefaultHome ? "" : z ? "_easyhome" : LGHomeFeature.isLoadDefaultWorkspaceFile() ? "" : "_allapps");
        LGLog.d(TAG, "Using workspace from " + str);
        this.numFolderRows = resources.getInteger(z ? R.integer.device_profile_numFolderRows_easyhome : R.integer.device_profile_numFolderRows);
        this.numFolderRowsForSwivel = resources.getInteger(R.integer.device_profile_numFolderRows_swivel_home);
        this.numFolderColumns = resources.getInteger(z ? R.integer.device_profile_numFolderColumns_easyhome : R.integer.device_profile_numFolderColumns);
        this.numFolderColumnsForSwivel = this.numFolderColumns;
        this.defaultLayoutId = resources.getIdentifier(str, "xml", context.getBasePackageName());
    }

    private float getIconSizeByDynamicGrid(Context context, Resources res, int numDefaultRows, int numDefaultColumns, int numRows, int numColumns) {
        float f;
        float f2 = res.getFloat(R.dimen.device_profile_iconSize);
        float f3 = res.getFloat(R.dimen.device_profile_icon_resize_scale_rate);
        int i = numDefaultRows + numDefaultColumns;
        int i2 = numRows + numColumns;
        if (LGHomeFeature.Config.FEATURE_USE_DEFAULT_LOW_DPI.getValue()) {
            boolean zIsLowDisplay = Utilities.isLowDisplay(context, res);
            if (zIsLowDisplay) {
                if (numRows == 6) {
                    f = res.getFloat(R.dimen.device_profile_iconSize_4x6_low);
                } else if (numColumns == 5 && numRows == 5) {
                    f2 = res.getFloat(R.dimen.device_profile_iconSize_5x5_low);
                    if (LGHomeFeature.Config.FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME.getValue() && !LGHomeFeature.isDisableEasyHome()) {
                        f = res.getFloat(R.dimen.device_profile_iconSize_4x6_low);
                    }
                    LGLog.d(TAG, "Grid:" + numColumns + "x" + numRows + ", isLowDisplay = " + zIsLowDisplay + ", defaultIconSize = " + f2);
                } else {
                    f = res.getFloat(R.dimen.device_profile_iconSize_low);
                }
                f2 = f;
                LGLog.d(TAG, "Grid:" + numColumns + "x" + numRows + ", isLowDisplay = " + zIsLowDisplay + ", defaultIconSize = " + f2);
            } else {
                if (numRows == 6) {
                    f = res.getFloat(R.dimen.device_profile_iconSize_4x6);
                } else if (numColumns == 5 && numRows == 5) {
                    float f4 = res.getFloat(R.dimen.device_profile_iconSize_5x5);
                    if (LGHomeFeature.Config.FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME.getValue() && !LGHomeFeature.isDisableEasyHome()) {
                        f = res.getFloat(R.dimen.device_profile_iconSize_4x6);
                    } else {
                        f2 = f4;
                        LGLog.d(TAG, "Grid:" + numColumns + "x" + numRows + ", isLowDisplay = " + zIsLowDisplay + ", defaultIconSize = " + f2);
                    }
                } else {
                    f = res.getFloat(R.dimen.device_profile_iconSize);
                }
                f2 = f;
                LGLog.d(TAG, "Grid:" + numColumns + "x" + numRows + ", isLowDisplay = " + zIsLowDisplay + ", defaultIconSize = " + f2);
            }
        } else if (numRows == 6) {
            f2 = res.getFloat(R.dimen.device_profile_iconSize_4x6);
        } else if (numColumns == 5 && numRows == 5) {
            f2 = res.getFloat(R.dimen.device_profile_iconSize_5x5);
            if (LGHomeFeature.Config.FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME.getValue() && !LGHomeFeature.isDisableEasyHome()) {
                f2 = res.getFloat(R.dimen.device_profile_iconSize_4x6);
            }
        }
        return (!LGHomeFeature.Config.FEATURE_ICON_SCALE_BY_GRID.getValue() || i >= i2 || i >= i2) ? f2 : Math.round(f2 * (1.0f - f3));
    }

    public static int getSharedPrefValue(Context context, SharedPreferencesConst.PreferenceKey preferenceKey, int defaultValue) {
        String keyString = SharedPreferencesManager.toKeyString(preferenceKey);
        String homePreferences = LauncherModel.getHomePreferences(context, keyString);
        if (homePreferences == null) {
            LauncherModel.setHomePreferences(context, keyString, Integer.toString(defaultValue));
            return defaultValue;
        }
        return Integer.parseInt(homePreferences);
    }

    public static void setSharedPrefValue(Context context, SharedPreferencesConst.PreferenceKey preferenceKey, int value) {
        LauncherModel.setHomePreferences(context, SharedPreferencesManager.toKeyString(preferenceKey), Integer.toString(value));
    }

    public void resetGridSize(Context context) {
        super.getDeviceProfile(context).resetGridSize(context);
        this.numColumns = this.numDefaultColumns;
        this.numRows = this.numDefaultRows;
        setSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, this.numColumns);
        setSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, this.numRows);
    }

    @Override // com.android.launcher3.InvariantDeviceProfile
    public int getAllAppsButtonRank() {
        return this.hotseatAllAppsRank;
    }
}
