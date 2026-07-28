package com.android.launcher3;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.launcher3.CellLayout;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class Hotseat extends FrameLayout {
    static final String TAG = "HotSeat";
    private boolean enableAppDrawerButton;
    private int mAllAppsButtonRank;
    private CellLayout mContent;
    private boolean mFirstAppDrawerIconSet;
    private boolean mHasVerticalHotseat;
    private int mHeightSize;
    private Launcher mLauncher;
    private boolean mNeedToResetTranslation;
    private int mWidthSize;

    public Hotseat(Context context) {
        this(context, null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFirstAppDrawerIconSet = true;
        this.mWidthSize = 0;
        this.mHeightSize = 0;
        this.mNeedToResetTranslation = false;
        Launcher launcher = (Launcher) context;
        this.mLauncher = launcher;
        this.mHasVerticalHotseat = launcher.getDeviceProfile().isVerticalBarLayout();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mNeedToResetTranslation) {
            CellLayout cellLayout = this.mContent;
            if (cellLayout != null) {
                ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
                for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
                    shortcutsAndWidgets.getChildAt(i).setTranslationX(0.0f);
                }
            }
            this.mNeedToResetTranslation = false;
        }
    }

    public CellLayout getLayout() {
        return this.mContent;
    }

    public boolean hasIcons() {
        return this.mContent.getShortcutsAndWidgets().getChildCount() > 1;
    }

    @Override // android.view.View
    public void setOnLongClickListener(View.OnLongClickListener l) {
        this.mContent.setOnLongClickListener(l);
    }

    int getOrderInHotseat(int x, int y) {
        return this.mHasVerticalHotseat ? (this.mContent.getCountY() - y) - 1 : x;
    }

    int getCellXFromOrder(int rank) {
        if (this.mHasVerticalHotseat) {
            return 0;
        }
        return rank;
    }

    int getCellYFromOrder(int rank) {
        if (this.mHasVerticalHotseat) {
            return this.mContent.getCountY() - (rank + 1);
        }
        return 0;
    }

    public boolean isAllAppsButtonRank(int rank) {
        LGLog.d(TAG, "isAllAppsButtonRank - mAllAppsButtonRank = " + this.mAllAppsButtonRank + ", rank = " + rank);
        return !LGHomeFeature.isEnableDefaultHome() && rank == this.mAllAppsButtonRank;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        this.mAllAppsButtonRank = 0;
        this.mContent = (CellLayout) findViewById(R.id.layout);
        if (deviceProfile.isVerticalBarLayout() && deviceProfile.allowRotation) {
            this.mContent.setGridSize(1, deviceProfile.inv.numHotseatIcons);
        } else {
            this.mContent.setGridSize(deviceProfile.inv.numHotseatIcons, 1);
        }
        this.mContent.setIsHotseat(true);
        resetLayout();
    }

    public void resetHotSeat(DeviceProfile grid) {
        this.mAllAppsButtonRank = 0;
        boolean zIsVerticalBarLayout = grid.isVerticalBarLayout();
        this.mHasVerticalHotseat = zIsVerticalBarLayout;
        this.mContent.setVertical(zIsVerticalBarLayout);
        if (grid.isVerticalBarLayout() && grid.allowRotation) {
            this.mContent.setGridSize(1, grid.inv.numHotseatIcons);
        } else {
            this.mContent.setGridSize(grid.inv.numHotseatIcons, 1);
        }
        this.mContent.setIsHotseat(true);
    }

    void resetLayout() {
        removeAllViewsInContent();
        this.mAllAppsButtonRank = 0;
        this.mFirstAppDrawerIconSet = true;
    }

    void addAllAppsButton() {
        BubbleTextView bubbleTextView;
        Drawable drawable;
        Context context = getContext();
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(context);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            bubbleTextView = (BubbleTextView) layoutInflaterFrom.inflate(R.layout.app_icon_port, (ViewGroup) this.mContent, false);
        } else {
            bubbleTextView = (BubbleTextView) layoutInflaterFrom.inflate(R.layout.app_icon, (ViewGroup) this.mContent, false);
        }
        BubbleTextView bubbleTextView2 = bubbleTextView;
        if (DDTUtils.isAdditionalThemeApplied(context) || DDTUtils.isAdditionalIconThemeApplied(context)) {
            drawable = context.getResources().getDrawable(R.mipmap.lg_iconframe_apps);
        } else {
            drawable = DDTUtils.convertToCushionIcon(context, context.getDrawable(R.mipmap.lg_iconframe_apps), "com.lge.launcher3", R.mipmap.lg_iconframe_apps);
        }
        Drawable shadowIconIfNeeded = IconCache.getShadowIconIfNeeded(context, drawable, (Boolean) true);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            Launcher.resizeIconDrawable(shadowIconIfNeeded, LauncherAppState.getIDP(context).portraitProfile.iconSizePx);
        } else {
            Launcher.resizeIconDrawable(shadowIconIfNeeded, this.mLauncher.getDeviceProfile().iconSizePx);
        }
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && this.mLauncher.getDeviceProfile().isLandscape && this.mLauncher.getDeviceProfile().allowRotation) {
            if (Utilities.isRtl(this.mLauncher.getResources())) {
                bubbleTextView2.setCompoundDrawables(null, null, shadowIconIfNeeded, null);
            } else {
                bubbleTextView2.setCompoundDrawables(shadowIconIfNeeded, null, null, null);
            }
            bubbleTextView2.setText(context.getString(R.string.all_apps_button));
            bubbleTextView2.setCompoundDrawablePadding(this.mLauncher.getDeviceProfile().iconDrawablePaddingPx);
        } else {
            bubbleTextView2.setCompoundDrawables(null, shadowIconIfNeeded, null, null);
        }
        if (LGHomeFeature.Config.FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME.getValue() && !LGHomeFeature.isDisableEasyHome()) {
            if (!this.mLauncher.getDeviceProfile().isLandscape) {
                bubbleTextView2.setBackground(null);
            } else {
                bubbleTextView2.setBackgroundResource(R.drawable.bg_easyhome_widget);
                bubbleTextView2.setTypeface(Typeface.DEFAULT, 1);
            }
        }
        bubbleTextView2.setContentDescription(context.getString(R.string.all_apps_button_label));
        bubbleTextView2.setOnKeyListener(new HotseatIconKeyEventListener());
        Launcher launcher = this.mLauncher;
        if (launcher != null) {
            launcher.setAllAppsButton(bubbleTextView2);
            bubbleTextView2.setOnTouchListener(this.mLauncher.getHapticFeedbackTouchListener());
            bubbleTextView2.setOnClickListener(this.mLauncher);
            bubbleTextView2.setOnLongClickListener(this.mLauncher);
            bubbleTextView2.setOnFocusChangeListener(this.mLauncher.mFocusHandler);
        }
        if (LGHomeFeature.isEnableDefaultHome()) {
            return;
        }
        if (HomeSettingsSharedPreferences.getEnableAppDrawerButton(getContext())) {
            getAllAppsButtonRank();
            int cellXFromOrder = getCellXFromOrder(this.mAllAppsButtonRank);
            int cellYFromOrder = getCellYFromOrder(this.mAllAppsButtonRank);
            if (this.mContent.getShortcutsAndWidgets().getChildCount() != 0 && this.mHasVerticalHotseat && this.mLauncher.getDeviceProfile().isLandscape && !isMaxHotSeat(this.mAllAppsButtonRank + 1).booleanValue()) {
                cellYFromOrder++;
            }
            CellLayout.LayoutParams layoutParams = new CellLayout.LayoutParams(cellXFromOrder, cellYFromOrder, 1, 1);
            if (LGHomeFeature.Config.FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME.getValue() && !LGHomeFeature.isDisableEasyHome() && this.mLauncher.getDeviceProfile().isLandscape) {
                layoutParams.topMargin = bubbleTextView2.getResources().getDimensionPixelSize(R.dimen.jp_ez_layout_marginTop);
                layoutParams.bottomMargin = bubbleTextView2.getResources().getDimensionPixelSize(R.dimen.jp_ez_layout_marginBottom);
                layoutParams.leftMargin = bubbleTextView2.getResources().getDimensionPixelSize(R.dimen.jp_ez_layout_marginLeft);
                layoutParams.rightMargin = bubbleTextView2.getResources().getDimensionPixelSize(R.dimen.jp_ez_layout_marginRight);
            }
            layoutParams.canReorder = false;
            LGLog.d(TAG, "Enable AppDrawer Button, AppDrawer Rank : " + this.mAllAppsButtonRank);
            this.mContent.addViewToCellLayout(bubbleTextView2, -1, bubbleTextView2.getId(), layoutParams, true);
            this.enableAppDrawerButton = true;
            return;
        }
        LGLog.d(TAG, "Disable AppDrawer Button");
        this.enableAppDrawerButton = false;
        this.mAllAppsButtonRank = 0;
        this.mFirstAppDrawerIconSet = false;
    }

    private void getAllAppsButtonRank() {
        if (this.mAllAppsButtonRank != 0 || this.mFirstAppDrawerIconSet) {
            return;
        }
        for (int i = 0; i < this.mContent.getShortcutsAndWidgets().getChildCount(); i++) {
            View childAt = this.mContent.getShortcutsAndWidgets().getChildAt(i);
            ItemInfo itemInfo = (ItemInfo) childAt.getTag();
            if (itemInfo.container == -101 && this.mLauncher.mHotseat.isAllAppsButtonRank(itemInfo.cellX)) {
                if (isMaxHotSeat(itemInfo.cellX).booleanValue()) {
                    getLayout().removeView(childAt);
                    Pair<Long, int[]> pairFindSpaceForHotSeatItem = this.mLauncher.getModel().findSpaceForHotSeatItem(getContext());
                    this.mLauncher.getWorkspace().addInScreenFromBind(childAt, -100L, ((Long) pairFindSpaceForHotSeatItem.first).longValue(), ((int[]) pairFindSpaceForHotSeatItem.second)[0], ((int[]) pairFindSpaceForHotSeatItem.second)[1], 1, 1);
                    LauncherModel.modifyItemInDatabase(this.mLauncher, itemInfo, -100L, ((Long) pairFindSpaceForHotSeatItem.first).longValue(), ((int[]) pairFindSpaceForHotSeatItem.second)[0], ((int[]) pairFindSpaceForHotSeatItem.second)[1], 1, 1);
                } else {
                    this.mLauncher.mHotseat.addRankForAllAppsButton();
                }
            }
        }
    }

    public Boolean isMaxHotSeat(int rank) {
        if (rank >= this.mLauncher.getDeviceProfile().inv.numHotseatIcons - 1) {
            return true;
        }
        return false;
    }

    public boolean getEnableAppDrawerButton() {
        return this.enableAppDrawerButton;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mLauncher.getWorkspace().workspaceInModalState() && !UninstallModeManager.getInstance(this.mLauncher).isInUninstallMode()) {
            if (ev.getAction() == 2) {
                return true;
            }
            LGLog.i(TAG, "onInterceptTouchEvent return true" + ev);
            return true;
        }
        if (ev.getAction() == 2) {
            return false;
        }
        LGLog.i(TAG, "onInterceptTouchEvent return false" + ev);
        return false;
    }

    public void removeAllViewsInContent() {
        this.mContent.removeAllViewsInLayout();
    }

    public void setupAllAppsButton() {
        addAllAppsButton();
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.cellX = this.mAllAppsButtonRank;
        itemInfo.cellY = this.mAllAppsButtonRank;
        itemInfo.container = -101L;
        itemInfo.screenId = this.mAllAppsButtonRank;
        View allAppsButton = this.mLauncher.getAllAppsButton();
        allAppsButton.setTag(itemInfo);
        ((CellLayout.LayoutParams) allAppsButton.getLayoutParams()).canReorder = true;
    }

    public void addRankForAllAppsButton() {
        this.mAllAppsButtonRank++;
    }

    @Override // android.view.View
    public void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) {
        super.setHapticFeedbackEnabled(hapticFeedbackEnabled);
        CellLayout cellLayout = this.mContent;
        if (cellLayout != null) {
            cellLayout.setHapticFeedbackEnabled(hapticFeedbackEnabled);
        }
    }
}
